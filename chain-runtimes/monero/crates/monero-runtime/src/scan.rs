//! Scanning: feeds blocks to the monero-oxide `Scanner` and persists results to SQLite.
//!
//! ## Bounded steps
//!
//! Never loops to full sync. The caller passes a batch and gets one result; the
//! host decides whether to continue, since a run-to-completion call cannot be
//! interrupted, report progress, or yield the main thread on web and mobile.
//!
//! ## Three checks
//!
//! 1. **Block continuity**: `header.previous` must equal the recorded previous
//!    hash. A mismatch is a reorg (or a lying node), reported as
//!    `REORG_DETECTED` with the exact height.
//!
//! 2. **RingCT output index**: upstream `ScannableBlock` documents that
//!    `output_index_for_first_ringct_output` cannot be verified and the client
//!    must track and compare it. The cursor advances by the block's v2 output
//!    count and the next block is checked against it. A wrong index picks the
//!    wrong ring members and the transaction is rejected.
//!
//! 3. **Increasing heights within a batch**: out-of-order blocks are rejected,
//!    not sorted, because an out-of-order peer is untrusted.
//!
//! ## Spend detection limits
//!
//! A view-only wallet cannot tell whether its outputs were spent: that needs
//! key images, which need the private spend key. This is a protocol property.
//!
//! The host fills `outputs.key_image` via [`crate::spend::import_key_images`]
//! on unlock; scanning matches known key images against transaction inputs.
//! Outputs without a key image cannot be detected as spent, so the balance may
//! be overstated; see [`ScanOutcome::outputs_without_key_image`].

use std::collections::{HashMap, HashSet};

use monero_wallet::{
    address::SubaddressIndex,
    interface::ScannableBlock,
    transaction::{Input, Transaction},
    Scanner, WalletOutput,
};
use rusqlite::{params, OptionalExtension};
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::schema::REORG_WINDOW_BLOCKS;
use crate::wallet::Db;

/// Suggested rewind depth on a detected reorg.
///
/// Monero reorgs rarely exceed a few blocks. Too deep forces a long rescan; too
/// shallow may not pass the fork point. The host should increase it on repeated failures.
pub const REORG_REWIND_BLOCKS: u64 = 10;

#[derive(Debug, Default, Clone)]
pub struct ScanOutcome {
    pub blocks_scanned: u64,
    pub outputs_found: usize,
    pub spends_detected: usize,
    /// Possible spends recorded in this batch (our outputs without a key image seen in a ring).
    pub possible_spends: usize,
    pub from_height: Option<u64>,
    pub to_height: Option<u64>,
    /// Outputs without a key image. Greater than 0 means the balance may be
    /// overstated: spends of these outputs cannot be detected.
    pub outputs_without_key_image: i64,
    /// Block download time in ms. Reported apart from `ms_scan` to tell a slow
    /// network from slow decryption.
    pub ms_download: u32,
    /// Trial decryption and persistence time in ms.
    pub ms_scan: u32,
    /// How the batch was fetched: `blocksBin` (one bulk request) or `perBlock` (concurrent per block).
    pub fetched_via: &'static str,
    /// Why the bulk path was abandoned; set only on the step where that happened.
    pub fetch_note: Option<String>,
    /// Whether this step's first batch used the request prefetched by the previous step.
    pub prefetch_hit: bool,
    /// Batch size after this step (blocks in the next request).
    pub batch_blocks: u64,
}

impl ScanOutcome {
    pub fn to_json(&self) -> serde_json::Value {
        json!({
            "blocksScanned": self.blocks_scanned,
            "outputsFound": self.outputs_found,
            "spendsDetected": self.spends_detected,
            "possibleSpends": self.possible_spends,
            "fromHeight": self.from_height,
            "toHeight": self.to_height,
            "outputsWithoutKeyImage": self.outputs_without_key_image,
            "msDownload": self.ms_download,
            "msScan": self.ms_scan,
            "fetchedVia": self.fetched_via,
            "fetchNote": self.fetch_note,
            "prefetchHit": self.prefetch_hit,
            "batchBlocks": self.batch_blocks,
        })
    }
}

/// Number of RingCT outputs in the block.
///
/// Only v2 transactions produce RingCT outputs; the miner transaction counts
/// and comes first. Used to derive global output indices independently of the node.
fn ringct_output_count(block: &ScannableBlock) -> u64 {
    fn count(tx: &Transaction<impl monero_wallet::transaction::PotentiallyPruned>) -> u64 {
        match tx {
            Transaction::V1 { .. } => 0,
            Transaction::V2 { prefix, .. } => prefix.outputs.len() as u64,
        }
    }
    count(block.block.miner_transaction()) + block.transactions.iter().map(count).sum::<u64>()
}

/// Key images of all inputs in the block, with the txid of their transaction.
///
/// Txids come from `block.block.transactions`, which matches
/// `block.transactions` one to one and in order (upstream
/// `expand_to_scannable_block` fetches bodies from that list).
///
/// Not `Transaction::hash()`: these are `Pruned` transactions, and a V2 hash
/// needs the prunable hash that was pruned.
///
/// A length mismatch means an inconsistent block and is rejected; misalignment
/// would attribute spends to the wrong transaction.
fn key_images_in(block: &ScannableBlock) -> Result<Vec<([u8; 32], [u8; 32])>> {
    if block.block.transactions.len() != block.transactions.len() {
        return Err(RuntimeError::with(
            ErrorCode::MalformedNodeResponse,
            json!({
                "operation": "expandBlock",
                "height": block.block.number(),
                "txidCount": block.block.transactions.len(),
                "bodyCount": block.transactions.len(),
            }),
        ));
    }

    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        for input in &tx.prefix().inputs {
            if let Input::ToKey { key_image, .. } = input {
                out.push((key_image.to_bytes(), *txid));
            }
        }
    }
    Ok(out)
}

/// Fee of each transaction in the block, from its RingCT data. Pre-RingCT
/// (V1) transactions are skipped: no OneKey account predates RingCT.
///
/// Length mismatches are reported by `key_images_in`; skip here.
fn tx_fees_in(block: &ScannableBlock) -> Vec<([u8; 32], u64)> {
    block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
        .filter_map(|(txid, tx)| match tx {
            Transaction::V2 {
                proofs: Some(proofs),
                ..
            } => Some((*txid, proofs.base.fee)),
            _ => None,
        })
        .collect()
}

/// Rings referenced by each input: `(key image, txid, amount pool, absolute global member indices)`.
///
/// `key_offsets` are deltas: the first is absolute, each next is relative to the previous.
fn ring_references_in(block: &ScannableBlock) -> Vec<RingReference> {
    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        for input in &tx.prefix().inputs {
            if let Input::ToKey {
                amount,
                key_offsets,
                key_image,
            } = input
            {
                let mut absolute = Vec::with_capacity(key_offsets.len());
                let mut index = 0u64;
                for offset in key_offsets {
                    index = index.saturating_add(*offset);
                    absolute.push(index);
                }
                out.push(RingReference {
                    key_image: key_image.to_bytes(),
                    spend_txid: *txid,
                    amount: *amount,
                    members: absolute,
                });
            }
        }
    }
    out
}

/// A possible spend: one of our outputs appears in an input's ring.
struct PossibleSpend {
    out_txid: [u8; 32],
    out_index: u64,
    key_image: [u8; 32],
    spend_txid: [u8; 32],
    height: u64,
    timestamp: u64,
}

struct RingReference {
    key_image: [u8; 32],
    spend_txid: [u8; 32],
    /// `None`/`Some(0)`: RingCT pool; otherwise a pre-2017 per-amount pool.
    amount: Option<u64>,
    members: Vec<u64>,
}

/// Unspent outputs without a key image, by global index: `(txid, index_in_tx, amount)`.
/// Only these need possible spends; outputs with a key image use normal spend detection.
type RingCandidates = HashMap<u64, Vec<([u8; 32], u64, u64)>>;

fn load_ring_candidates(db: &Db, identity_id: i64) -> Result<RingCandidates> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT txid, index_in_tx, global_index, amount_atomic FROM outputs
             WHERE identity_id = ?1 AND scan_valid = 1 AND key_image IS NULL AND spent_at_height IS NULL",
        )
        .map_err(|e| db_err("loadRingCandidates", e))?;
    let rows = stmt
        .query_map([identity_id], |r| {
            Ok((
                r.get::<_, Vec<u8>>(0)?,
                r.get::<_, i64>(1)? as u64,
                r.get::<_, i64>(2)? as u64,
                r.get::<_, i64>(3)? as u64,
            ))
        })
        .map_err(|e| db_err("loadRingCandidates", e))?;
    let mut map: RingCandidates = HashMap::new();
    for row in rows {
        let (txid, index, global, amount) = row.map_err(|e| db_err("loadRingCandidates", e))?;
        let Ok(txid) = <[u8; 32]>::try_from(txid.as_slice()) else {
            continue;
        };
        map.entry(global).or_default().push((txid, index, amount));
    }
    Ok(map)
}

/// Our outputs in an input's ring. Pre-RingCT inputs also require a matching
/// amount; RingCT inputs match by index only, since a false positive is
/// harmless and a miss hides a spend.
fn ring_matches<'a>(
    candidates: &'a RingCandidates,
    reference: &'a RingReference,
) -> impl Iterator<Item = &'a ([u8; 32], u64, u64)> + 'a {
    let pool = reference.amount.filter(|a| *a != 0);
    reference
        .members
        .iter()
        .filter_map(|member| candidates.get(member))
        .flatten()
        .filter(move |(_, _, amount)| pool.is_none_or(|p| p == *amount))
}

/// Transaction public keys `(R, additional)` by txid.
///
/// ## Why kept separately
///
/// monero-oxide folds the R + output index + subaddress derivation into a
/// `key_offset` scalar on `WalletOutput` and drops R. Software signing only
/// needs `spend + key_offset`.
///
/// Firmware does not accept `key_offset`; it derives the one-time key and key
/// image from `real_out_tx_key` itself, since accepting a host-computed offset
/// would let a malicious host make the device sign with any key.
///
/// R is only available at scan time; recovering it later needs a full rescan
/// or asking the node for the transaction by txid, which leaks interest in it.
fn tx_public_keys_in(block: &ScannableBlock) -> Vec<([u8; 32], Vec<u8>, Option<Vec<u8>>)> {
    // Length mismatches are reported by `key_images_in`; skip here.
    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        if let Some((keys, additional)) = tx_public_keys_from_extra(&tx.prefix().extra) {
            out.push((*txid, keys, additional));
        }
    }
    out
}

/// Extracts `(R, additional)` from a transaction `extra`, each as concatenated 32-byte keys.
///
/// A pure function so it can be tested against real chain data without
/// building a full `ScannableBlock`.
fn tx_public_keys_from_extra(extra: &[u8]) -> Option<(Vec<u8>, Option<Vec<u8>>)> {
    fn flatten(points: &[monero_wallet::ed25519::Point]) -> Vec<u8> {
        let mut out = Vec::with_capacity(points.len() * 32);
        for p in points {
            out.extend_from_slice(&p.compress().to_bytes());
        }
        out
    }

    let extra = monero_wallet::extra::Extra::read(&mut &*extra).ok()?;
    let (keys, additional) = extra.keys()?;
    Some((flatten(&keys), additional.as_deref().map(flatten)))
}

/// Scans and persists a batch of blocks in one transaction: all or nothing.
///
/// Blocks must be contiguous in ascending height. The host fetches them by range.
pub fn scan_blocks(
    db: &mut Db,
    identity_id: i64,
    blocks: &[ScannableBlock],
) -> Result<ScanOutcome> {
    if blocks.is_empty() {
        let mut outcome = ScanOutcome::default();
        outcome.outputs_without_key_image = count_outputs_without_key_image(db, identity_id)?;
        return Ok(outcome);
    }

    let identity = db.identity_by_id(identity_id)?;
    let subaddresses = db.subaddress_indices(identity_id)?;

    let mut scanner = Scanner::new(identity.view_pair.clone());
    for (major, minor) in &subaddresses {
        // The schema CHECK excludes (0,0), so None means the constraint was
        // bypassed; skip rather than panic.
        if let Some(index) = SubaddressIndex::new(*major, *minor) {
            scanner.register_subaddress(index);
        }
    }

    // Known key images, for spend detection.
    let known_key_images = load_key_images(db, identity_id)?;
    // Outputs without a key image, matched against ring members as possible spends.
    let mut ring_candidates = load_ring_candidates(db, identity_id)?;
    let mut possible_spends: Vec<PossibleSpend> = Vec::new();

    let mut outcome = ScanOutcome::default();
    let mut found: Vec<(u64, WalletOutput)> = Vec::new();
    // txid -> (R, additional), only for transactions that paid us.
    let mut tx_pub_keys: HashMap<[u8; 32], (Vec<u8>, Option<Vec<u8>>)> = HashMap::new();
    let mut spends: Vec<(u64, [u8; 32], [u8; 32])> = Vec::new(); // (height, key_image, txid)

    // Fees of transactions that spend ours: history needs them to tell the payment from the fee.
    let mut spend_fees: HashMap<[u8; 32], u64> = HashMap::new();
    let mut block_rows: Vec<(u64, [u8; 32], Option<u64>, u64)> = Vec::new(); // (height, hash, ringct_next, ts)

    // Block before the batch start, for continuity and RingCT cursor checks.
    let first_height = blocks[0].block.number() as u64;
    let mut prev = previous_block_state(db, identity_id, first_height)?;

    for block in blocks {
        let height = block.block.number() as u64;

        // ── 1. Heights must be strictly increasing and contiguous ─────────
        if let Some((prev_height, _, _)) = prev {
            if height != prev_height + 1 {
                return Err(RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": height,
                        "expectedHeight": prev_height + 1,
                        "suggestedRewindHeight": height.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                ));
            }
        }

        // ── 2. Previous hash check: reorg detection ──────────────────────
        if let Some((_, prev_hash, _)) = prev {
            if block.block.header.previous != prev_hash {
                return Err(RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": height,
                        "suggestedRewindHeight": height.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                ));
            }
        }

        // ── 3. RingCT index check (upstream requires client-side tracking) ─
        if let (Some((_, _, Some(expected_next))), Some(claimed)) =
            (prev, block.output_index_for_first_ringct_output)
        {
            if claimed != expected_next {
                return Err(RuntimeError::with(
                    ErrorCode::RingCtIndexMismatch,
                    json!({
                        "height": height,
                        "claimed": claimed,
                        "expected": expected_next,
                    }),
                ));
            }
        }

        let outputs = scanner
            .scan(block.clone())
            .map_err(|e| {
                RuntimeError::with(ErrorCode::ScanFailed, json!({ "height": height }))
                    .detail(format!("{e:?}"))
            })?
            // Timelocks are evaluated by the query layer against the current
            // height; dropping locked outputs here would lose them for good.
            .ignore_additional_timelock();

        if !outputs.is_empty() {
            // Parse extra only for blocks that paid us; most blocks do not.
            for (txid, keys, additional) in tx_public_keys_in(block) {
                tx_pub_keys.insert(txid, (keys, additional));
            }
        }
        let spends_before = spends.len() + possible_spends.len();
        // Match inputs before adding this block's outputs as candidates: new
        // outputs are locked for at least 10 blocks and cannot be spent in the same block.
        for reference in ring_references_in(block) {
            if known_key_images.contains_key(&reference.key_image) {
                continue;
            }
            for (out_txid, out_index, _) in ring_matches(&ring_candidates, &reference) {
                possible_spends.push(PossibleSpend {
                    out_txid: *out_txid,
                    out_index: *out_index,
                    key_image: reference.key_image,
                    spend_txid: reference.spend_txid,
                    height,
                    timestamp: block.block.header.timestamp,
                });
            }
        }
        for output in outputs {
            ring_candidates
                .entry(output.index_on_blockchain())
                .or_default()
                .push((
                    output.transaction(),
                    output.index_in_transaction(),
                    output.commitment().amount,
                ));
            found.push((height, output));
        }

        for (key_image, txid) in key_images_in(block)? {
            if known_key_images.contains_key(&key_image) {
                // The spending transaction's txid, not the block hash.
                spends.push((height, key_image, txid));
            }
        }
        if spends.len() + possible_spends.len() > spends_before {
            spend_fees.extend(tx_fees_in(block));
        }

        // Cursor base: the previous block's cursor, else the node's claimed
        // start. With neither, stay unknown; falling back to 0 would raise a
        // false RINGCT_INDEX_MISMATCH on the next block.
        let next_ringct = prev
            .and_then(|(_, _, n)| n)
            .or(block.output_index_for_first_ringct_output)
            .map(|base| base + ringct_output_count(block));

        block_rows.push((
            height,
            block.block.hash(),
            next_ringct,
            block.block.header.timestamp,
        ));
        prev = Some((height, block.block.hash(), next_ringct));
        outcome.blocks_scanned += 1;
    }

    outcome.from_height = Some(first_height);
    outcome.to_height = Some(first_height + outcome.blocks_scanned - 1);
    outcome.outputs_found = found.len();
    outcome.spends_detected = spends.len();
    outcome.possible_spends = possible_spends.len();

    persist(
        db,
        identity_id,
        &found,
        &tx_pub_keys,
        &spends,
        &known_key_images,
        &block_rows,
        &possible_spends,
        &spend_fees,
    )?;
    outcome.outputs_without_key_image = count_outputs_without_key_image(db, identity_id)?;
    Ok(outcome)
}

/// (height, hash, RingCT cursor after it) of the block before `height`.
fn previous_block_state(
    db: &Db,
    identity_id: i64,
    height: u64,
) -> Result<Option<(u64, [u8; 32], Option<u64>)>> {
    if height == 0 {
        return Ok(None);
    }
    db.conn()
        .query_row(
            "SELECT height, hash, ringct_next FROM blocks WHERE identity_id = ?1 AND height = ?2",
            params![identity_id, (height - 1) as i64],
            |r| {
                let h: i64 = r.get(0)?;
                let hash: Vec<u8> = r.get(1)?;
                let next: Option<i64> = r.get(2)?;
                Ok((h as u64, hash, next.map(|n| n as u64)))
            },
        )
        .optional()
        .map_err(|e| db_err("previousBlockState", e))?
        .map(|(h, hash, next)| {
            let hash = <[u8; 32]>::try_from(hash.as_slice())
                .map_err(|_| db_err("previousBlockState", "block hash is not 32 bytes"))?;
            Ok((h, hash, next))
        })
        .transpose()
}

/// Unspent outputs plus those spent by our own unconfirmed broadcasts, which
/// are confirmed by a key image match once mined.
fn load_key_images(db: &Db, identity_id: i64) -> Result<HashMap<[u8; 32], i64>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT id, key_image FROM outputs o
             WHERE o.identity_id = ?1 AND o.key_image IS NOT NULL
               AND (o.spent_at_height IS NULL OR o.spent_in_txid IN (
                 SELECT txid FROM transactions WHERE identity_id = ?1 AND (height IS NULL OR scan_valid = 0)))",
        )
        .map_err(|e| db_err("loadKeyImages", e))?;
    let rows = stmt
        .query_map([identity_id], |r| {
            let id: i64 = r.get(0)?;
            let ki: Vec<u8> = r.get(1)?;
            Ok((id, ki))
        })
        .map_err(|e| db_err("loadKeyImages", e))?;

    let mut map = HashMap::new();
    for row in rows {
        let (id, ki) = row.map_err(|e| db_err("loadKeyImages", e))?;
        if let Ok(ki) = <[u8; 32]>::try_from(ki.as_slice()) {
            map.insert(ki, id);
        }
    }
    Ok(map)
}

fn count_outputs_without_key_image(db: &Db, identity_id: i64) -> Result<i64> {
    db.conn()
        .query_row(
            "SELECT count(*) FROM outputs WHERE identity_id = ?1 AND scan_valid = 1 AND key_image IS NULL",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("countOutputsWithoutKeyImage", e))
}

#[allow(clippy::type_complexity, clippy::too_many_arguments)]
fn persist(
    db: &mut Db,
    identity_id: i64,
    found: &[(u64, WalletOutput)],
    tx_pub_keys: &HashMap<[u8; 32], (Vec<u8>, Option<Vec<u8>>)>,
    spends: &[(u64, [u8; 32], [u8; 32])],
    known_key_images: &HashMap<[u8; 32], i64>,
    block_rows: &[(u64, [u8; 32], Option<u64>, u64)],
    possible_spends: &[PossibleSpend],
    spend_fees: &HashMap<[u8; 32], u64>,
) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginScanCommit", e))?;

    let mut seen_txids: HashSet<[u8; 32]> = HashSet::new();
    // Store the time on the transaction row: `blocks` is pruned near the tip,
    // so backfilled transactions cannot join for it later.
    let block_time: HashMap<u64, i64> = block_rows
        .iter()
        .map(|(h, _, _, ts)| (*h, *ts as i64))
        .collect();

    for (height, output) in found {
        let txid = output.transaction();
        let subaddress = output.subaddress();
        let output_key = output.key().compress().to_bytes();
        if is_burning_duplicate(
            &tx,
            identity_id,
            &output_key,
            &txid,
            output.index_in_transaction(),
        )? {
            continue;
        }
        // Revalidate the same output without losing its key image or send lock.
        tx.execute(
            "INSERT INTO outputs
               (identity_id, blob, txid, global_index, index_in_tx, amount_atomic,
                subaddress_major, subaddress_minor, height, additional_timelock,
                tx_pub_keys, tx_additional_pub_keys, output_key)
             VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)
             ON CONFLICT (identity_id, txid, index_in_tx) DO UPDATE SET
               blob = excluded.blob, global_index = excluded.global_index,
               amount_atomic = excluded.amount_atomic,
               subaddress_major = excluded.subaddress_major, subaddress_minor = excluded.subaddress_minor,
               height = excluded.height, additional_timelock = excluded.additional_timelock,
               tx_pub_keys = excluded.tx_pub_keys, tx_additional_pub_keys = excluded.tx_additional_pub_keys,
               output_key = excluded.output_key, scan_valid = 1",
            params![
                identity_id,
                output.serialize(),
                txid.as_slice(),
                output.index_on_blockchain() as i64,
                output.index_in_transaction() as i64,
                output.commitment().amount as i64,
                subaddress.map(|s| s.account() as i64),
                subaddress.map(|s| s.address() as i64),
                *height as i64,
                timelock_to_i64(output.additional_timelock()),
                tx_pub_keys.get(&txid).map(|(k, _)| k.as_slice()),
                tx_pub_keys.get(&txid).and_then(|(_, a)| a.as_deref()),
                output_key.as_slice(),
            ],
        )
        .map_err(|e| db_err("insertOutput", e))?;

        if seen_txids.insert(txid) {
            tx.execute(
                "INSERT INTO transactions (identity_id, txid, height, timestamp, direction)
                 VALUES (?1, ?2, ?3, ?4, 'in')
                 ON CONFLICT (identity_id, txid) DO UPDATE
                   SET height = excluded.height, failed = 0, scan_valid = 1,
                       timestamp = COALESCE(excluded.timestamp, transactions.timestamp)",
                params![
                    identity_id,
                    txid.as_slice(),
                    *height as i64,
                    block_time.get(height)
                ],
            )
            .map_err(|e| db_err("insertIncomingTransaction", e))?;
        }
    }

    for (height, key_image, txid) in spends {
        let Some(output_id) = known_key_images.get(key_image) else {
            continue;
        };
        tx.execute(
            "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
             WHERE id = ?3 AND (spent_at_height IS NULL OR spent_in_txid IN (
               SELECT txid FROM transactions WHERE identity_id = ?4 AND (height IS NULL OR scan_valid = 0)))",
            params![*height as i64, txid.as_slice(), output_id, identity_id],
        )
        .map_err(|e| db_err("markOutputSpent", e))?;
        tx.execute(
            "INSERT INTO transactions (identity_id, txid, height, timestamp, direction, fee_atomic)
             VALUES (?1, ?2, ?3, ?4, 'out', ?5)
             ON CONFLICT (identity_id, txid) DO UPDATE
               SET direction = 'out', height = ?3, failed = 0, scan_valid = 1,
                   timestamp = COALESCE(excluded.timestamp, transactions.timestamp),
                   fee_atomic = COALESCE(excluded.fee_atomic, transactions.fee_atomic)",
            params![
                identity_id,
                txid.as_slice(),
                *height as i64,
                block_time.get(height),
                spend_fees.get(txid).map(|fee| *fee as i64)
            ],
        )
        .map_err(|e| db_err("insertOutgoingTransaction", e))?;
    }

    // Idempotent: rescans hit the same ring references.
    for ps in possible_spends {
        tx.execute(
            "INSERT OR IGNORE INTO possible_spends
               (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp, fee_atomic)
             VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)",
            params![
                identity_id,
                ps.out_txid.as_slice(),
                ps.out_index as i64,
                ps.key_image.as_slice(),
                ps.spend_txid.as_slice(),
                ps.height as i64,
                ps.timestamp as i64,
                spend_fees.get(&ps.spend_txid).map(|fee| *fee as i64)
            ],
        )
        .map_err(|e| db_err("insertPossibleSpend", e))?;
    }

    for (height, hash, ringct_next, timestamp) in block_rows {
        tx.execute(
            "INSERT OR REPLACE INTO blocks (identity_id, height, hash, ringct_next, timestamp)
             VALUES (?1, ?2, ?3, ?4, ?5)",
            params![
                identity_id,
                *height as i64,
                hash.as_slice(),
                ringct_next.map(|n| n as i64),
                *timestamp as i64
            ],
        )
        .map_err(|e| db_err("insertBlock", e))?;
    }

    // Keep only REORG_WINDOW_BLOCKS below the tip so the table does not grow with the chain.
    if let Some((tip, _, _, _)) = block_rows.last() {
        let floor = tip.saturating_sub(REORG_WINDOW_BLOCKS);
        tx.execute(
            "DELETE FROM blocks WHERE identity_id = ?1 AND height < ?2",
            params![identity_id, floor as i64],
        )
        .map_err(|e| db_err("pruneBlocks", e))?;
    }

    tx.commit().map_err(|e| db_err("commitScan", e))
}

/// Burning bug: the same one-time key in another output. Only one is spendable,
/// so only the first seen is counted, never overstating the balance. A rescan
/// of the same output is not a duplicate.
fn is_burning_duplicate(
    conn: &rusqlite::Connection,
    identity_id: i64,
    output_key: &[u8; 32],
    txid: &[u8; 32],
    index_in_tx: u64,
) -> Result<bool> {
    conn.query_row(
        "SELECT EXISTS (SELECT 1 FROM outputs
           WHERE identity_id = ?1 AND scan_valid = 1 AND output_key = ?2
             AND NOT (txid = ?3 AND index_in_tx = ?4))",
        params![
            identity_id,
            output_key.as_slice(),
            txid.as_slice(),
            index_in_tx as i64
        ],
        |r| r.get(0),
    )
    .map_err(|e| db_err("burningBugCheck", e))
}

fn timelock_to_i64(timelock: monero_wallet::transaction::Timelock) -> i64 {
    use monero_wallet::transaction::Timelock;
    match timelock {
        Timelock::None => 0,
        Timelock::Block(b) => b as i64,
        Timelock::Time(t) => t as i64,
    }
}

/// Invalidate the rewound span, retaining key images and local send evidence.
pub(crate) fn rewind_in_transaction(
    tx: &rusqlite::Transaction<'_>,
    identity_id: i64,
    height: u64,
    tip: u64,
) -> Result<()> {
    // Must run before clearing spend marks: that step keeps marks for pending transactions.
    let reorgable_above = height.max(tip.saturating_sub(REORG_WINDOW_BLOCKS));
    tx.execute(
        "UPDATE transactions SET scan_valid = 0 WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("invalidateRewoundTransactions", e))?;
    tx.execute(
        "UPDATE transactions SET height = NULL, failed = 0, pool_checked_at = NULL
         WHERE identity_id = ?1 AND height > ?2 AND sent_at IS NOT NULL",
        params![identity_id, reorgable_above as i64],
    )
    .map_err(|e| db_err("rewindOwnSends", e))?;

    tx.execute(
        "UPDATE outputs SET scan_valid = 0 WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindOutputs", e))?;

    // Retain our own send locks until the transaction is reconciled on chain or in the pool.
    tx.execute(
        "UPDATE outputs SET spent_at_height = NULL, spent_in_txid = NULL
         WHERE identity_id = ?1 AND spent_at_height > ?2
           AND spent_in_txid NOT IN (
             SELECT txid FROM transactions WHERE identity_id = ?1 AND (height IS NULL OR sent_at IS NOT NULL))",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindSpends", e))?;

    tx.execute(
        "DELETE FROM transactions WHERE identity_id = ?1 AND height > ?2 AND sent_at IS NULL",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindTransactions", e))?;

    tx.execute(
        "DELETE FROM possible_spends WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindPossibleSpends", e))?;

    tx.execute(
        "DELETE FROM blocks WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindBlocks", e))?;

    Ok(())
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    #[test]
    fn a_rescan_and_reorg_revalidate_outputs_without_losing_local_send_evidence() {
        use monero_wallet::ed25519::{Commitment, Point, Scalar};
        let (mut db, id) = db_with_identity();
        let txid = [0x61; 32];
        let own_send = [0x62; 32];
        let image = [0x63; 32];
        let mut blob = Vec::new();
        blob.extend_from_slice(&txid);
        blob.extend_from_slice(&0u64.to_le_bytes());
        blob.extend_from_slice(&7u64.to_le_bytes());
        let scalar = Scalar::read(&mut [1u8; 32].as_slice()).unwrap();
        Point::from(curve25519_dalek::constants::ED25519_BASEPOINT_POINT)
            .compress()
            .write(&mut blob)
            .unwrap();
        scalar.write(&mut blob).unwrap();
        Commitment::new(scalar, 1_000).write(&mut blob).unwrap();
        blob.extend_from_slice(&[0, 0, 0, 0]);
        let output = WalletOutput::read(&mut blob.as_slice()).unwrap();
        persist(
            &mut db,
            id,
            &[(990, output)],
            &HashMap::new(),
            &[],
            &HashMap::new(),
            &[],
            &[],
            &HashMap::new(),
        )
        .unwrap();
        db.conn().execute("INSERT INTO transactions (identity_id, txid, height, direction, sent_at) VALUES (?1, ?2, 995, 'out', 1)", params![id, own_send.as_slice()]).unwrap();
        db.conn()
            .execute(
                "UPDATE outputs SET key_image = ?1, spent_at_height = 995, spent_in_txid = ?2",
                params![image.as_slice(), own_send.as_slice()],
            )
            .unwrap();
        db.conn()
            .execute(
                "UPDATE identities SET scanned_to_height = 1000 WHERE id = ?1",
                [id],
            )
            .unwrap();
        crate::sync::reset_from_birthday(&mut db, id, 100).unwrap();
        crate::sync::rewind_and_requeue(&mut db, id, 550, 600).unwrap();
        let (valid, stored_image, lock): (i64, Vec<u8>, Vec<u8>) = db
            .conn()
            .query_row(
                "SELECT scan_valid, key_image, spent_in_txid FROM outputs",
                [],
                |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)),
            )
            .unwrap();
        assert_eq!(
            (valid, stored_image, lock),
            (0, image.to_vec(), own_send.to_vec())
        );
        assert_eq!(load_key_images(&db, id).unwrap().get(&image), Some(&1));
        // The same output on the canonical chain replaces stale chain metadata.
        blob[40..48].copy_from_slice(&17u64.to_le_bytes());
        let output = WalletOutput::read(&mut blob.as_slice()).unwrap();
        persist(
            &mut db,
            id,
            &[(991, output)],
            &HashMap::new(),
            &[],
            &HashMap::new(),
            &[],
            &[],
            &HashMap::new(),
        )
        .unwrap();
        let (valid, index, height, stored_image, lock): (i64, i64, i64, Vec<u8>, Vec<u8>) = db
            .conn()
            .query_row(
                "SELECT scan_valid, global_index, height, key_image, spent_in_txid FROM outputs",
                [],
                |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?, r.get(3)?, r.get(4)?)),
            )
            .unwrap();
        assert_eq!(
            (valid, index, height, stored_image, lock),
            (1, 17, 991, image.to_vec(), own_send.to_vec())
        );
        let own_sends: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM transactions WHERE txid = ?1 AND sent_at = 1",
                [own_send.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(own_sends, 1);
    }

    /// On real mainnet blocks, ring offsets resolve to strictly increasing
    /// absolute indices; ring size is 16 and all inputs are RingCT.
    #[test]
    fn ring_references_resolve_to_increasing_absolute_positions_on_real_blocks() {
        let blocks = crate::blocks_bin::parse_response(
            include_bytes!("../tests/fixtures/get_blocks_bin_3768621.bin"),
            3_768_621,
        )
        .expect("parse real response");
        let references: Vec<RingReference> = blocks.iter().flat_map(ring_references_in).collect();
        assert!(!references.is_empty());
        let mut key_images = HashSet::new();
        for reference in &references {
            assert_eq!(reference.members.len(), 16);
            assert!(reference.members.windows(2).all(|w| w[0] < w[1]));
            assert_eq!(reference.amount.unwrap_or(0), 0, "RingCT input");
            assert!(key_images.insert(reference.key_image), "unique key images");
        }

        // An output of ours that is a ring member is a possible spend.
        let target = &references[0];
        let mut candidates = RingCandidates::new();
        candidates.insert(target.members[5], vec![([7; 32], 1, 12_345)]);
        assert_eq!(ring_matches(&candidates, target).count(), 1);
    }

    /// Every RingCT transaction in a real block states a small, non-zero fee.
    #[test]
    fn real_transactions_state_their_fee() {
        let blocks = crate::blocks_bin::parse_response(
            include_bytes!("../tests/fixtures/get_blocks_bin_3768621.bin"),
            3_768_621,
        )
        .expect("parse real response");
        let fees: Vec<([u8; 32], u64)> = blocks.iter().flat_map(tx_fees_in).collect();
        let tx_count: usize = blocks.iter().map(|b| b.transactions.len()).sum();
        assert!(tx_count > 0);
        assert_eq!(fees.len(), tx_count);
        for (_, fee) in fees {
            assert!(fee > 0 && fee < 1_000_000_000_000, "fee {fee}");
        }
    }

    /// Pre-RingCT input indices are only meaningful within the same amount pool.
    #[test]
    fn a_pre_ringct_input_matches_only_outputs_of_its_amount() {
        let reference = RingReference {
            key_image: [1; 32],
            spend_txid: [2; 32],
            amount: Some(5_000),
            members: vec![40, 41],
        };
        let mut candidates = RingCandidates::new();
        candidates.insert(41, vec![([7; 32], 0, 5_000), ([8; 32], 0, 6_000)]);
        let matched: Vec<_> = ring_matches(&candidates, &reference).collect();
        assert_eq!(matched.len(), 1);
        assert_eq!(matched[0].0, [7; 32]);
    }
    use rusqlite::params;

    /// Real mainnet vector.
    ///
    /// Transaction
    /// `097edcb40a4345c0e83c41e8631b9e71173389270694e7a7499d3c2fa17951bb`
    /// at mainnet height 3767756. Layout: `01` (TxPublicKey) + 32-byte R +
    /// `02 09 01` (nonce / encrypted payment id) + 8 bytes.
    #[test]
    fn the_transaction_public_key_is_read_from_a_real_mainnet_extra() {
        let extra = hex::decode(
            "01d3c8b566d9fd0bff3f93448591ec257cc8bf813e1aaa808e97254a5c65ea166\
             00209012c9f54c03a7c72d0",
        )
        .unwrap();

        let (keys, additional) = tx_public_keys_from_extra(&extra).expect("parse tx public key");
        assert_eq!(
            hex::encode(&keys),
            "d3c8b566d9fd0bff3f93448591ec257cc8bf813e1aaa808e97254a5c65ea1660"
        );
        assert_eq!(keys.len(), 32, "a single R is exactly 32 bytes");
        // Sent to a primary address, so no additional keys: None, not an empty Vec.
        assert_eq!(additional, None);
    }

    /// An empty extra has no keys; it must not panic or abort the batch.
    #[test]
    fn an_extra_without_keys_yields_nothing_rather_than_failing() {
        assert!(tx_public_keys_from_extra(&[]).is_none());
    }

    /// An unknown RingCT cursor is stored as NULL, not 0.
    ///
    /// Starting mid-chain at a block without RingCT outputs leaves
    /// `output_index_for_first_ringct_output` as None. Storing 0 would raise a
    /// false `RINGCT_INDEX_MISMATCH` against an honest node on the next block;
    /// NULL skips the check until a real anchor is known.
    #[test]
    fn an_unknown_ringct_cursor_is_stored_as_null_not_zero() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                 VALUES (?1, ?2, ?3, NULL, 0)",
                params![id, 3_000_000i64, &vec![7u8; 32]],
            )
            .expect("cursor should allow NULL");

        let prev = previous_block_state(&db, id, 3_000_001).expect("read previous block");
        let (height, _, cursor) = prev.expect("previous block should exist");
        assert_eq!(height, 3_000_000);
        assert_eq!(cursor, None, "unknown must be None, not Some(0)");
    }

    /// One identity's rewind must not touch another's anchors, or the other
    /// would miss the reorg and keep orphaned funds in its balance.
    #[test]
    fn one_identity_rewinding_leaves_the_others_anchors_alone() {
        let (mut db, a) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO identities (uuid, primary_address, spend_public_key,
                   view_private_key, birthday_height, created_at)
                 VALUES ('b', 'b', ?1, ?1, 0, 0)",
                [vec![9u8; 32]],
            )
            .unwrap();
        let b = db.conn().last_insert_rowid();
        for id in [a, b] {
            db.conn()
                .execute(
                    "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                     VALUES (?1, 100, ?2, NULL, 0)",
                    params![id, vec![7u8; 32]],
                )
                .unwrap();
        }

        crate::sync::rewind_and_requeue(&mut db, a, 50, 60).unwrap();

        assert!(previous_block_state(&db, a, 101).unwrap().is_none());
        assert!(previous_block_state(&db, b, 101).unwrap().is_some());
    }

    #[test]
    fn a_reused_output_key_from_another_transaction_is_a_burning_duplicate() {
        let (db, id) = db_with_identity();
        let key = [4u8; 32];
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, output_key)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3)",
                params![id, [1u8; 32].as_slice(), key.as_slice()],
            )
            .unwrap();

        assert!(is_burning_duplicate(db.conn(), id, &key, &[2u8; 32], 0).unwrap());
        assert!(
            !is_burning_duplicate(db.conn(), id, &key, &[1u8; 32], 0).unwrap(),
            "rescanning the same output is not a duplicate"
        );
        assert!(!is_burning_duplicate(db.conn(), id, &[5u8; 32], &[2u8; 32], 0).unwrap());
    }

    /// A known cursor round-trips.
    #[test]
    fn a_known_ringct_cursor_round_trips() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                 VALUES (?1, ?2, ?3, ?4, 0)",
                params![id, 3_000_000i64, &vec![7u8; 32], 82_000_000i64],
            )
            .unwrap();
        let (_, _, cursor) = previous_block_state(&db, id, 3_000_001).unwrap().unwrap();
        assert_eq!(cursor, Some(82_000_000));
    }

    /// A backfilled transaction keeps its time after its block is pruned (otherwise the UI shows 1970).
    #[test]
    fn a_transaction_keeps_its_time_after_its_block_is_pruned() {
        let (mut db, id) = db_with_identity();
        let tx_height = 100u64;
        let tip = tx_height + REORG_WINDOW_BLOCKS + 5;
        let key_image = [1u8; 32];
        let txid = [2u8; 32];
        let known: HashMap<[u8; 32], i64> = [(key_image, 1)].into_iter().collect();
        persist(
            &mut db,
            id,
            &[],
            &HashMap::new(),
            &[(tx_height, key_image, txid)],
            &known,
            &[
                (tx_height, [7u8; 32], None, 1_700_000_000),
                (tip, [8u8; 32], None, 1_700_100_000),
            ],
            &[],
            &HashMap::new(),
        )
        .unwrap();

        let pruned: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM blocks WHERE height = ?1",
                [tx_height as i64],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(pruned, 0, "the block itself should be pruned");

        let history = crate::query::history(&db, id, 10, 0).unwrap();
        assert_eq!(history["items"][0]["timestamp"], 1_700_000_000);
    }

    /// Our broadcast is mined: outputs already marked spent at broadcast must
    /// still match by key image, confirming the transaction at its mined height.
    #[test]
    fn our_broadcast_spend_is_confirmed_when_its_block_is_scanned() {
        let (mut db, id) = db_with_identity();
        let key_image = [1u8; 32];
        let txid = [2u8; 32];
        let (broadcast_tip, mined_at) = (500i64, 503u64);
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3, ?4, ?5)",
                params![id, [9u8; 32].as_slice(), key_image.as_slice(), broadcast_tip, txid.as_slice()],
            )
            .unwrap();
        let output_id = db.conn().last_insert_rowid();
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction)
                 VALUES (?1, ?2, NULL, 'out')",
                params![id, txid.as_slice()],
            )
            .unwrap();

        let known = load_key_images(&db, id).unwrap();
        assert_eq!(
            known.get(&key_image),
            Some(&output_id),
            "pending spends must be matched too"
        );

        persist(
            &mut db,
            id,
            &[],
            &HashMap::new(),
            &[(mined_at, key_image, txid)],
            &known,
            &[(mined_at, [7u8; 32], None, 1_700_000_000)],
            &[],
            &HashMap::new(),
        )
        .unwrap();

        let tx_height: Option<i64> = db
            .conn()
            .query_row(
                "SELECT height FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(tx_height, Some(mined_at as i64));
        let spent_at: i64 = db
            .conn()
            .query_row(
                "SELECT spent_at_height FROM outputs WHERE id = ?1",
                [output_id],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(spent_at, mined_at as i64);
        assert!(
            load_key_images(&db, id).unwrap().is_empty(),
            "confirmed spends are no longer matched"
        );
    }

    /// A spend found by scanning keeps its fee, so history can tell the
    /// payment apart from the fee; a possible spend keeps it until confirmed.
    #[test]
    fn a_scanned_spend_records_its_transaction_fee() {
        let (mut db, id) = db_with_identity();
        let key_image = [1u8; 32];
        let (txid, possible_txid) = ([2u8; 32], [3u8; 32]);
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3)",
                params![id, [9u8; 32].as_slice(), key_image.as_slice()],
            )
            .unwrap();
        let known = load_key_images(&db, id).unwrap();
        let possible = PossibleSpend {
            out_txid: [8; 32],
            out_index: 0,
            key_image: [4; 32],
            spend_txid: possible_txid,
            height: 503,
            timestamp: 1_700_000_000,
        };
        let fees = HashMap::from([(txid, 30_000_000u64), (possible_txid, 40_000_000)]);

        persist(
            &mut db,
            id,
            &[],
            &HashMap::new(),
            &[(503, key_image, txid)],
            &known,
            &[(503, [7u8; 32], None, 1_700_000_000)],
            &[possible],
            &fees,
        )
        .unwrap();

        let history = crate::query::history(&db, id, 10, 0).unwrap();
        assert_eq!(history["items"][0]["feeAtomic"], "30000000");
        let possible_fee: Option<i64> = db
            .conn()
            .query_row(
                "SELECT fee_atomic FROM possible_spends WHERE spend_txid = ?1",
                [possible_txid.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(possible_fee, Some(40_000_000));
    }

    #[test]
    fn a_rewind_keeps_the_mark_on_outputs_our_pending_send_spent() {
        let (mut db, id) = db_with_identity();
        let txid = [2u8; 32];
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3, 500, ?4)",
                params![id, [9u8; 32].as_slice(), [1u8; 32].as_slice(), txid.as_slice()],
            )
            .unwrap();
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction)
                 VALUES (?1, ?2, NULL, 'out')",
                params![id, txid.as_slice()],
            )
            .unwrap();

        crate::sync::rewind_and_requeue(&mut db, id, 490, 505).unwrap();

        let spent_in: Option<Vec<u8>> = db
            .conn()
            .query_row("SELECT spent_in_txid FROM outputs", [], |r| r.get(0))
            .unwrap();
        assert_eq!(spent_in, Some(txid.to_vec()));
    }

    /// A deep rewind retains old sends as unverified instead of treating them as pool transactions.
    #[test]
    fn a_deep_rewind_does_not_turn_old_sends_pending() {
        let (mut db, id) = db_with_identity();
        let ours = [3u8; 32];
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction, sent_at)
                 VALUES (?1, ?2, 500, 'out', 1000)",
                params![id, ours.as_slice()],
            )
            .unwrap();

        crate::sync::rewind_and_requeue(&mut db, id, 490, 500 + REORG_WINDOW_BLOCKS + 10).unwrap();

        let pending: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM transactions WHERE height IS NULL",
                [],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(pending, 0);
    }

    /// Our confirmed send reorged out reverts to pending; inputs stay spent.
    #[test]
    fn a_rewind_turns_our_confirmed_send_back_into_a_pending_one() {
        let (mut db, id) = db_with_identity();
        let ours = [3u8; 32];
        let scanned = [4u8; 32];
        for (n, txid) in [(1u8, ours), (2u8, scanned)] {
            db.conn()
                .execute(
                    "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                          amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                     VALUES (?1, x'00', ?2, ?3, 0, 700, 10, ?4, 500, ?5)",
                    params![id, [9u8 + n; 32].as_slice(), n, [n; 32].as_slice(), txid.as_slice()],
                )
                .unwrap();
        }
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction, sent_at)
                 VALUES (?1, ?2, 500, 'out', 1000), (?1, ?3, 500, 'out', NULL)",
                params![id, ours.as_slice(), scanned.as_slice()],
            )
            .unwrap();

        crate::sync::rewind_and_requeue(&mut db, id, 490, 505).unwrap();

        let rows: Vec<(Vec<u8>, Option<i64>)> = db
            .conn()
            .prepare("SELECT txid, height FROM transactions")
            .unwrap()
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(
            rows,
            vec![(ours.to_vec(), None)],
            "our send reverts to pending, scanned ones are deleted"
        );
        let spent: Vec<Option<Vec<u8>>> = db
            .conn()
            .prepare("SELECT spent_in_txid FROM outputs ORDER BY global_index")
            .unwrap()
            .query_map([], |r| r.get(0))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(spent, vec![Some(ours.to_vec()), None]);
    }

    /// An empty batch is a no-op: no error, no state change.
    #[test]
    fn an_empty_batch_is_a_harmless_no_op() {
        let (mut db, id) = db_with_identity();
        let outcome = scan_blocks(&mut db, id, &[]).expect("empty batch");

        assert_eq!(outcome.blocks_scanned, 0);
        assert_eq!(outcome.outputs_found, 0);
        assert!(outcome.from_height.is_none());
    }
}
