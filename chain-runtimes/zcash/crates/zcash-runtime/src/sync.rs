//! Chain scanning with bounded progress.
//!
//! `zcash_client_backend::sync::run` loops until fully synced (`batch_size` only
//! sizes each batch), so callers cannot interrupt, observe progress, or yield the
//! main thread. This module drives the upstream public APIs in two parts:
//!
//! - [`prepare`]: one-off setup (subtree roots, chain tip). Slow; once per sync.
//! - [`step`]: scans at most `max_batches` batches, then returns progress.
//!
//! The host decides whether to call again.

use std::collections::{HashMap, HashSet};
use std::hash::Hash;

use futures_util::TryStreamExt;
use serde_json::json;

use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
use zcash_client_backend::data_api::chain::error::Error as ChainError;
use zcash_client_backend::data_api::chain::{BlockSource, ChainState, CommitmentTreeRoot};
use zcash_client_backend::data_api::scanning::{ScanPriority, ScanRange};
use zcash_client_backend::data_api::{
    TransactionDataRequest, TransactionStatus, WalletCommitmentTrees, WalletRead, WalletWrite,
};
use zcash_client_backend::decrypt_transaction;
use zcash_client_backend::proto::service;
use zcash_client_backend::scanning::{scan_block, Nullifiers, ScanningKeys};
use zcash_client_sqlite::AccountUuid;
use zcash_keys::keys::UnifiedFullViewingKey;
use zcash_primitives::merkle_tree::HashSer;
use zcash_primitives::transaction::{Transaction, TxId};
use zcash_protocol::consensus::{BlockHeight, BranchId, NetworkUpgrade, Parameters};

use crate::blockcache::MemoryBlockCache;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::LightClient;
use crate::wallet::Db;

/// Bound speculative scanning to the earlier Ironwood / Orchard tail, capped at 30 days.
/// `pick_range` also floors the cut at the upstream ChainTip start. Note-shard
/// completion and reorg safety can extend this window.
const FAST_WINDOW_MAX_BLOCKS: u32 = 34_560;
const FAST_WINDOW_REORG_MARGIN: u32 = 200;

/// Keep upstream Verify / FoundNote / OpenAdjacent handling. Only partition ChainTip:
/// its suffix runs in the fast lane, its prefix joins Historic in ascending order.
/// Keep the original priority on returned ranges for progress attribution.
fn pick_range(
    suggested: &[ScanRange],
    preferred: Option<ScanPriority>,
    window_start: BlockHeight,
) -> Option<ScanRange> {
    let Some(lane) = preferred else {
        return [
            ScanPriority::Verify,
            ScanPriority::ChainTip,
            ScanPriority::FoundNote,
            ScanPriority::OpenAdjacent,
            ScanPriority::Historic,
        ]
        .into_iter()
        .find_map(|lane| pick_range(suggested, Some(lane), window_start));
    };
    suggested
        .iter()
        .filter(|r| !r.is_empty())
        .filter_map(|r| {
            let bounds = r.block_range();
            match (lane, r.priority()) {
                (ScanPriority::ChainTip, ScanPriority::ChainTip) if bounds.end > window_start => {
                    Some(ScanRange::from_parts(
                        bounds.start.max(window_start)..bounds.end,
                        r.priority(),
                    ))
                }
                (ScanPriority::Historic, ScanPriority::ChainTip) if bounds.start < window_start => {
                    Some(ScanRange::from_parts(
                        bounds.start..bounds.end.min(window_start),
                        r.priority(),
                    ))
                }
                (p, q) if p == q && p != ScanPriority::ChainTip => Some(r.clone()),
                _ => None,
            }
        })
        .min_by_key(|r| {
            // Upstream orders equal-priority ranges by height as well. In particular,
            // Historic and demoted ChainTip ranges must share one ascending lane.
            r.block_range().start
        })
}

fn window_start(db: &mut Db) -> Result<BlockHeight> {
    let Some(tip) = db.chain_height().map_err(|e| db_err("chainHeight", e))? else {
        return Ok(BlockHeight::from_u32(u32::MAX));
    };
    let ironwood_activation = db
        .params()
        .activation_height(NetworkUpgrade::Nu6_3)
        .unwrap_or(BlockHeight::from_u32(0));
    let orchard_activation = db
        .params()
        .activation_height(NetworkUpgrade::Nu5)
        .unwrap_or(BlockHeight::from_u32(0));
    db.transactionally_with_extension(|_, ext| {
        let (ironwood, orchard): (Option<u32>, Option<u32>) = ext.query_row(
            "SELECT (SELECT MAX(subtree_end_height) FROM ironwood_tree_shards),
                    (SELECT MAX(subtree_end_height) FROM orchard_tree_shards)",
            [],
            |row| Ok((row.get(0)?, row.get(1)?)),
        )?;
        let ironwood = ironwood
            .map(BlockHeight::from_u32)
            .unwrap_or(ironwood_activation);
        let orchard = orchard
            .map(BlockHeight::from_u32)
            .unwrap_or(orchard_activation);
        let mut start = ironwood
            .min(orchard)
            .max(tip.saturating_sub(FAST_WINDOW_MAX_BLOCKS))
            .min(tip.saturating_sub(FAST_WINDOW_REORG_MARGIN));

        // A newly discovered note can span the cut. Complete its entire shard before
        // moving on, even when its below-window ranges still have ChainTip priority
        // and therefore cannot be promoted to FoundNote by upstream. Repeat because
        // extending one pool's window can expose an overlapping shard in the other.
        loop {
            let before = start;
            for pool in ["orchard", "ironwood"] {
                let note_start: Option<u32> = ext.query_row(
                    &format!(
                        "SELECT MIN(ss.subtree_start_height)
                         FROM {pool}_received_notes rn
                         JOIN v_{pool}_shards_scan_state ss
                           ON rn.commitment_tree_position >= ss.start_position
                          AND rn.commitment_tree_position < ss.end_position_exclusive
                         WHERE ss.max_priority > 10 AND ss.subtree_start_height < ?1
                           AND IFNULL(ss.subtree_end_height, ?2) >= ?1"
                    ),
                    rusqlite::params![u32::from(start), u32::from(tip)],
                    |row| row.get(0),
                )?;
                if let Some(note_start) = note_start {
                    start = start.min(BlockHeight::from_u32(note_start));
                }
            }
            if start == before {
                break;
            }
        }
        Ok(start)
    })
    .map_err(|e: rusqlite::Error| db_err("fastWindow", e))
}

/// A frontier cannot bridge missing completed shards in SQLite's shard store.
/// Check every pool, including Sapling whose ownership scanning is disabled.
fn roots_cover(db: &mut Db, state: &ChainState) -> Result<bool> {
    let sizes = [
        ("sapling", state.final_sapling_tree().tree_size()),
        ("orchard", state.final_orchard_tree().tree_size()),
        ("ironwood", state.final_ironwood_tree().tree_size()),
    ];
    db.transactionally_with_extension(|_, ext| {
        for (pool, size) in sizes {
            let required = size >> 16;
            let known: u64 = ext.query_row(
                &format!(
                    "SELECT COUNT(*) FROM {pool}_tree_shards
                     WHERE shard_index >= 0 AND shard_index < ?1
                       AND subtree_end_height IS NOT NULL AND root_hash IS NOT NULL"
                ),
                [required],
                |row| row.get(0),
            )?;
            if known != required {
                return Ok(false);
            }
        }
        Ok(true)
    })
    .map_err(|e: rusqlite::Error| db_err("fastWindowRoots", e))
}

/// When the server's roots lag, use upstream's original ChainTip start until
/// sequential scanning has filled the gap. Never retry the impossible jump forever.
fn guard_fast_range(
    db: &mut Db,
    suggested: &[ScanRange],
    range: ScanRange,
    state: &ChainState,
) -> Result<ScanRange> {
    if range.priority() == ScanPriority::ChainTip {
        if let Some(original) = suggested
            .iter()
            .filter(|r| !r.is_empty() && r.priority() == ScanPriority::ChainTip)
            .min_by_key(|r| r.block_range().start)
        {
            if range.block_range().start > original.block_range().start && !roots_cover(db, state)?
            {
                return Ok(original.clone());
            }
        }
    }
    Ok(range)
}

async fn range_chain_state(
    db: &Db,
    client: &mut LightClient,
    start: BlockHeight,
) -> Result<ChainState> {
    let anchor = anchor_height(db.params(), start);
    crate::network::with_timeout(
        async {
            client
                .get_tree_state(service::BlockId {
                    height: u64::from(anchor),
                    hash: Vec::new(),
                })
                .await
                .map_err(|e| net_err("getTreeState", e))?
                .into_inner()
                .to_chain_state()
                .map_err(|e| net_err("toChainState", e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        "getTreeState",
    )
    .await
}

/// Host-selectable priority, as strings so values are checkable across the boundary.
pub fn parse_priority(name: &str) -> Result<Option<ScanPriority>> {
    Ok(match name {
        "" | "any" => None,
        "chainTip" => Some(ScanPriority::ChainTip),
        "historic" => Some(ScanPriority::Historic),
        "verify" => Some(ScanPriority::Verify),
        "openAdjacent" => Some(ScanPriority::OpenAdjacent),
        "foundNote" => Some(ScanPriority::FoundNote),
        other => {
            return Err(RuntimeError::with(
                ErrorCode::InvalidScanPriority,
                json!({ "value": other, "allowed": ["any", "chainTip", "historic", "verify", "openAdjacent", "foundNote"] }),
            ))
        }
    })
}

/// Suggested rewind depth on reorg.
///
/// Too deep forces a long rescan, too shallow may not pass the fork point.
/// Hosts may increase it on repeated failures.
const REORG_REWIND_BLOCKS: u32 = 10;

fn net_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

fn db_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(e)
}

/// Fetch all subtree roots for one pool.
async fn subtree_roots<H: HashSer>(
    client: &mut LightClient,
    protocol: service::ShieldedProtocol,
    operation: &'static str,
) -> Result<Vec<CommitmentTreeRoot<H>>> {
    let mut request = service::GetSubtreeRootsArg::default();
    request.set_shielded_protocol(protocol);

    let mut stream = crate::network::with_timeout(
        async {
            client
                .get_subtree_roots(request)
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err(operation, e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        operation,
    )
    .await?;

    let mut roots = Vec::new();
    loop {
        // Per-item inactivity timeout, not a deadline for the whole stream.
        let next = crate::network::with_timeout(
            async { stream.try_next().await.map_err(|e| net_err(operation, e)) },
            crate::network::DEFAULT_TIMEOUT_MS,
            operation,
        )
        .await?;
        let Some(root) = next else {
            break;
        };
        let hash = H::read(&root.root_hash[..]).map_err(|e| net_err(operation, e))?;
        roots.push(CommitmentTreeRoot::from_parts(
            BlockHeight::from_u32(root.completing_block_height as u32),
            hash,
        ));
    }
    Ok(roots)
}

/// One-off setup: load subtree roots for all three pools and update the chain tip.
///
/// Idempotent, but each call re-downloads every subtree root.
pub async fn prepare(db: &mut Db, client: &mut LightClient) -> Result<String> {
    let sapling: Vec<CommitmentTreeRoot<sapling::Node>> = subtree_roots(
        client,
        service::ShieldedProtocol::Sapling,
        "getSubtreeRoots.sapling",
    )
    .await?;
    let n_sapling = sapling.len();
    db.put_sapling_subtree_roots(0, &sapling)
        .map_err(|e| db_err("putSaplingSubtreeRoots", e))?;

    let orchard: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>> = subtree_roots(
        client,
        service::ShieldedProtocol::Orchard,
        "getSubtreeRoots.orchard",
    )
    .await?;
    let n_orchard = orchard.len();
    db.put_orchard_subtree_roots(0, &orchard)
        .map_err(|e| db_err("putOrchardSubtreeRoots", e))?;

    let ironwood: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>> = subtree_roots(
        client,
        service::ShieldedProtocol::Ironwood,
        "getSubtreeRoots.ironwood",
    )
    .await?;
    let n_ironwood = ironwood.len();
    db.put_ironwood_subtree_roots(0, &ironwood)
        .map_err(|e| db_err("putIronwoodSubtreeRoots", e))?;

    let tip = crate::network::chain_tip(client).await?;
    db.update_chain_tip(BlockHeight::from_u32(tip))
        .map_err(|e| db_err("updateChainTip", e))?;

    Ok(json!({
        "chainTip": tip,
        "saplingSubtrees": n_sapling,
        "orchardSubtrees": n_orchard,
        "ironwoodSubtrees": n_ironwood,
    })
    .to_string())
}

/// Update only the chain tip, leaving subtree roots untouched.
///
/// Subtree roots change rarely; use `prepare` once after opening the wallet and
/// this unary call for later syncs.
pub async fn tip(db: &mut Db, client: &mut LightClient) -> Result<String> {
    let tip = crate::network::chain_tip(client).await?;
    db.update_chain_tip(BlockHeight::from_u32(tip))
        .map_err(|e| db_err("updateChainTip", e))?;
    Ok(json!({ "chainTip": tip }).to_string())
}

/// Bounded progress: at most `max_batches` batches of `batch_size` blocks.
///
/// Returns progress JSON; `done` means no ranges remain. Never loops to full sync;
/// the host decides whether to call again.
pub async fn step(
    db: &mut Db,
    client: &mut LightClient,
    batch_size: u32,
    max_batches: u32,
    preferred: Option<ScanPriority>,
    active_ufvks: &[String],
) -> Result<String> {
    if batch_size == 0 || max_batches == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidBatchSize,
            json!({ "batchSize": batch_size, "maxBatches": max_batches }),
        ));
    }

    let params = *db.params();
    let wallet_ufvks = db
        .get_unified_full_viewing_keys()
        .map_err(|e| db_err("getUnifiedFullViewingKeys", e))?;
    let active_scanning_ufvks = select_active_scanning_ufvks(&params, wallet_ufvks, active_ufvks)?;
    let active_account_count = active_scanning_ufvks.len();
    let scanning_keys = ScanningKeys::from_account_ufvks(active_scanning_ufvks);
    let cache = MemoryBlockCache::new();

    let mut batches_done = 0u32;
    let mut blocks_scanned = 0u64;
    // Per-phase timings: in the browser slow download and slow scan look identical.
    let (mut ms_download, mut ms_treestate, mut ms_scan) = (0.0f64, 0.0f64, 0.0f64);
    let (mut scanned_from, mut scanned_to) = (None::<u32>, None::<u32>);
    let mut scanned_priority: Option<String> = None;

    while batches_done < max_batches {
        let suggested = db
            .suggest_scan_ranges()
            .map_err(|e| db_err("suggestScanRanges", e))?;

        // Special-priority and empty passes need no window/shard queries.
        let window = if preferred
            .is_none_or(|p| matches!(p, ScanPriority::ChainTip | ScanPriority::Historic))
            && suggested
                .iter()
                .any(|r| r.priority() == ScanPriority::ChainTip)
        {
            window_start(db)?
        } else {
            BlockHeight::from_u32(0)
        };
        let Some(range) = pick_range(&suggested, preferred, window) else {
            break; // no pending ranges at this priority
        };

        // Reuse the treestate needed by scanning to validate the jump before downloading.
        // Only a fallback needs a second treestate request; window planning is local SQL.
        let t_tree = now_ms();
        let mut chain_state = range_chain_state(db, client, range.block_range().start).await?;
        let original_start = range.block_range().start;
        let range = guard_fast_range(db, &suggested, range, &chain_state)?;
        if range.block_range().start != original_start {
            chain_state = range_chain_state(db, client, range.block_range().start).await?;
        }
        ms_treestate += now_ms() - t_tree;

        // Take only this batch; the rest is left for later calls.
        let start = range.block_range().start;
        if scanned_from.is_none() {
            scanned_from = Some(u32::from(start));
        }
        scanned_priority = Some(format!("{:?}", range.priority()));

        let t_start = now_ms();

        // Download: on failure retry the same batch once, then halve, down to 32 blocks.
        //
        // In the 2022 spam range (heights ~1.71M-1.95M) a 500-block batch can be
        // tens of MB and never finish on slow links. The successful size is
        // remembered across calls and grown back after successes.
        const MIN_DOWNLOAD_BATCH: u32 = 32;
        thread_local! {
            static DOWNLOAD_BATCH_HINT: std::cell::Cell<u32> =
                const { std::cell::Cell::new(0) };
            static DOWNLOAD_BATCH_STREAK: std::cell::Cell<u32> =
                const { std::cell::Cell::new(0) };
        }
        let hint = DOWNLOAD_BATCH_HINT.with(|h| h.get());
        let initial_len = if hint > 0 {
            std::cmp::min(batch_size, hint)
        } else {
            batch_size
        };
        let mut attempt_len: u32 = initial_len;
        let mut same_size_retry = 1u32;
        let (batch, batch_len) = loop {
            let attempt_end = std::cmp::min(start + attempt_len, range.block_range().end);
            let candidate = ScanRange::from_parts(start..attempt_end, range.priority());
            // download_batch times out on stalls; only real failures reach here.
            match download_batch(client, &cache, &candidate).await {
                Ok(got) => break (candidate, got),
                Err(e) => {
                    if e.code != ErrorCode::NetworkError {
                        return Err(e);
                    }
                    if same_size_retry > 0 {
                        same_size_retry -= 1;
                        continue;
                    }
                    if attempt_len <= MIN_DOWNLOAD_BATCH {
                        return Err(e);
                    }
                    attempt_len = std::cmp::max(MIN_DOWNLOAD_BATCH, attempt_len / 2);
                }
            }
        };
        // Grow one step only after three consecutive full batches at the remembered
        // size, so the hint does not oscillate back into timeouts.
        if attempt_len < initial_len {
            DOWNLOAD_BATCH_HINT.with(|h| h.set(attempt_len));
            DOWNLOAD_BATCH_STREAK.with(|s| s.set(0));
        } else if batch_len >= attempt_len as usize && initial_len < batch_size {
            let streak = DOWNLOAD_BATCH_STREAK.with(|s| {
                let v = s.get() + 1;
                s.set(v);
                v
            });
            if streak >= 3 {
                let grown = std::cmp::min(batch_size, attempt_len.saturating_mul(2));
                DOWNLOAD_BATCH_HINT.with(|h| h.set(grown));
                DOWNLOAD_BATCH_STREAK.with(|s| s.set(0));
            }
        }
        // A budget cut may store fewer blocks than requested; scan only what arrived.
        let _ = &batch;
        scanned_to = Some(u32::from(start) + batch_len as u32);
        let t_downloaded = now_ms();
        ms_download += t_downloaded - t_start;

        let t_scan = now_ms();

        // Reorgs surface as REORG_DETECTED with an exact atHeight.
        scan_blocks_inline(
            &params,
            &cache,
            db,
            &scanning_keys,
            start,
            &chain_state,
            batch_len,
        )?;

        ms_scan += now_ms() - t_scan;

        blocks_scanned += batch_len as u64;

        cache.delete(&batch).map_err(|e| db_err("cacheDelete", e))?;

        batches_done += 1;
    }

    // Compact blocks lack full outgoing amounts, fees and memos. Enrich only on
    // historic or lane-less steps to keep the per-turn network budget bounded.
    let enhancement = if preferred.is_none() || preferred == Some(ScanPriority::Historic) {
        enhance_transactions(db, client, active_ufvks).await
    } else {
        Ok(0)
    };

    // Re-query to report remaining work for the host progress bar.
    let remaining = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;
    let remaining_blocks: u64 = remaining.iter().map(|r| r.len() as u64).sum();

    Ok(json!({
        "done": remaining_blocks == 0,
        "transactionsEnhanced": enhancement.as_ref().copied().unwrap_or(0),
        "enhancementError": enhancement.err().map(|error| error.to_string()),
        "activeAccounts": active_account_count,
        "batchesDone": batches_done,
        "blocksScanned": blocks_scanned,
        "remainingBlocks": remaining_blocks,
        "remainingRanges": remaining.len(),
        // Range and priority actually scanned, so the host attributes progress to the right lane.
        "scannedFrom": scanned_from,
        "scannedTo": scanned_to,
        "scannedPriority": scanned_priority,
        "msDownload": ms_download as u32,
        "msTreeState": ms_treestate as u32,
        "msScan": ms_scan as u32,
    })
    .to_string())
}

// Decrypts with active identities only; upstream decrypt_and_store_transaction
// would try every stored UFVK, including paused accounts.

async fn enhance_transactions(
    db: &mut Db,
    client: &mut LightClient,
    active_ufvks: &[String],
) -> Result<u32> {
    let params = *db.params();
    let active: HashSet<&str> = active_ufvks.iter().map(String::as_str).collect();
    let keys = db
        .get_unified_full_viewing_keys()
        .map_err(|error| db_err("enhancementKeys", error))?
        .into_iter()
        .filter(|(_, key)| active.contains(key.encode(&params).as_str()))
        .collect::<HashMap<_, _>>();
    if keys.is_empty() {
        return Ok(0);
    }
    let requests = db
        .transaction_data_requests()
        .map_err(|error| db_err("transactionDataRequests", error))?;
    let mut txids = requests
        .into_iter()
        .filter_map(|request| match request {
            TransactionDataRequest::Enhancement(txid) => Some((0, txid)),
            TransactionDataRequest::GetStatus(txid) => Some((1, txid)),
            // Public address discovery remains owned by the transparent indexer.
            _ => None,
        })
        .collect::<Vec<_>>();
    // Never disclose a paused account's transaction ID to the sync endpoint.
    txids = select_enhancement_requests(db, &keys.keys().copied().collect::<Vec<_>>(), txids)?;
    txids.sort_by_key(|(priority, txid)| (*priority, txid.to_string()));
    txids.dedup_by_key(|(_, txid)| *txid);
    // Rotates even after a failed fetch so one missing transaction cannot starve the rest.
    thread_local! { static ENHANCEMENT_CURSOR: std::cell::Cell<usize> = const { std::cell::Cell::new(0) }; }
    if !txids.is_empty() {
        let offset = ENHANCEMENT_CURSOR.with(|cursor| {
            let value = cursor.get();
            cursor.set(value.wrapping_add(1));
            value % txids.len()
        });
        txids.rotate_left(offset);
    }
    let mut enhanced = 0;
    for (_, txid) in txids.into_iter().take(2) {
        let response = crate::network::with_timeout(
            async {
                match client
                    .get_transaction(service::TxFilter {
                        hash: txid.as_ref().to_vec(),
                        ..Default::default()
                    })
                    .await
                {
                    Ok(response) => Ok(Some(response.into_inner())),
                    Err(status) if status.code() == tonic::Code::NotFound => Ok(None),
                    Err(error) => Err(net_err("enhanceTransaction", error)),
                }
            },
            5_000,
            "enhanceTransaction",
        )
        .await?;
        let Some(raw) = response else {
            db.set_transaction_status(txid, TransactionStatus::TxidNotRecognized)
                .map_err(|error| db_err("enhancementStatus", error))?;
            continue;
        };
        let mined_height = match raw.height {
            0 | u64::MAX => None,
            height => Some(BlockHeight::from_u32(u32::try_from(height).map_err(
                |error| RuntimeError::new(ErrorCode::InvalidBlockHeight).detail(error),
            )?)),
        };
        let tip = db
            .chain_height()
            .map_err(|error| db_err("enhancementChainHeight", error))?;
        let branch = BranchId::for_height(
            &params,
            mined_height.or(tip).unwrap_or(BlockHeight::from_u32(0)),
        );
        let tx = parse_enhancement(&raw.data, branch, txid)?;
        let height = mined_height.or(db
            .get_tx_height(txid)
            .map_err(|error| db_err("enhancementTxHeight", error))?);
        db.store_decrypted_tx(decrypt_transaction(&params, height, tip, &tx, &keys))
            .map_err(|error| db_err("storeEnhancedTransaction", error))?;
        db.set_transaction_status(
            txid,
            mined_height
                .map(TransactionStatus::Mined)
                .unwrap_or(TransactionStatus::NotInMainChain),
        )
        .map_err(|error| db_err("enhancementStatus", error))?;
        enhanced += 1;
    }
    Ok(enhanced)
}

fn select_enhancement_requests(
    db: &mut Db,
    active_accounts: &[AccountUuid],
    requests: Vec<(u8, TxId)>,
) -> Result<Vec<(u8, TxId)>> {
    db.transactionally_with_extension(|_, ext| {
        let mut selected = Vec::new();
        for request in requests {
            for account in active_accounts {
                let visible: bool = ext.query_row(
                    "SELECT EXISTS(SELECT 1 FROM v_transactions WHERE txid = ?1 AND account_uuid = ?2)",
                    rusqlite::params![request.1.as_ref(), account.expose_uuid()],
                    |row| row.get(0),
                )?;
                if visible { selected.push(request); break; }
            }
        }
        Ok::<_, rusqlite::Error>(selected)
    }).map_err(|error| db_err("enhancementOwnership", error))
}

fn parse_enhancement(data: &[u8], branch: BranchId, expected: TxId) -> Result<Transaction> {
    let mut reader = std::io::Cursor::new(data);
    let tx = Transaction::read(&mut reader, branch)
        .map_err(|error| net_err("parseEnhancedTransaction", error))?;
    if reader.position() != data.len() as u64 || tx.txid() != expected {
        return Err(net_err(
            "parseEnhancedTransaction",
            "transaction identity or length mismatch",
        ));
    }
    Ok(tx)
}

/// Scans a batch of blocks inline and stores the results, without upstream
/// `scan_cached_blocks`.
///
/// `scan_cached_blocks` hands trial decryption to rayon (`Tasks for ()` does not
/// override `run_task`). On single-threaded wasm32 the pool has no workers, so
/// `BatchReceiver::into_results()` blocks forever at the first block with shielded
/// outputs. `scanning::scan_block` decrypts inline without forking upstream.
fn scan_blocks_inline<IvkTag>(
    params: &zcash_protocol::consensus::Network,
    cache: &MemoryBlockCache,
    db: &mut Db,
    scanning_keys: &ScanningKeys<<Db as WalletRead>::AccountId, IvkTag>,
    from_height: BlockHeight,
    from_state: &ChainState,
    limit: usize,
) -> Result<()>
where
    IvkTag: Copy + Hash + Eq + Send + 'static,
{
    let mut nullifiers = Nullifiers::unspent(db).map_err(|e| db_err("unspentNullifiers", e))?;

    let mut prior = if from_height > BlockHeight::from(0) {
        db.block_metadata(from_height - 1)
            .map_err(|e| db_err("blockMetadata", e))?
    } else {
        None
    };

    let mut scanned_blocks = Vec::new();
    cache
        .with_blocks::<_, <Db as WalletRead>::Error>(Some(from_height), Some(limit), |block| {
            let scanned = scan_block(params, block, scanning_keys, &nullifiers, prior.as_ref())
                .map_err(ChainError::Scan)?;
            nullifiers.update_with(&scanned);
            prior = Some(scanned.to_block_metadata());
            scanned_blocks.push(scanned);
            Ok(())
        })
        .map_err(|e| match &e {
            // Classify reorgs by the typed upstream predicate while the error is still typed.
            ChainError::Scan(scan_err) if scan_err.is_continuity_error() => {
                let at = u32::from(scan_err.at_height());
                RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": at,
                        "suggestedRewindHeight": at.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                )
                .detail(format!("{e:?}"))
            }
            _ => db_err("scanBlock", format!("{e:?}")),
        })?;

    db.put_blocks(from_state, scanned_blocks)
        .map_err(|e| db_err("putBlocks", e))?;
    Ok(())
}

/// Selects the host-authorized UFVKs and removes every non-Orchard component before scanning.
///
/// An Orchard-only UFVK still yields Orchard and Ironwood scanning keys, so the official
/// scanner maintains all trees while Sapling ownership discovery stays disabled.
fn select_active_scanning_ufvks<AccountId>(
    params: &zcash_protocol::consensus::Network,
    wallet_ufvks: HashMap<AccountId, UnifiedFullViewingKey>,
    active_ufvks: &[String],
) -> Result<Vec<(AccountId, UnifiedFullViewingKey)>>
where
    AccountId: Copy + Eq + Hash,
{
    if active_ufvks.is_empty() {
        return Err(RuntimeError::new(ErrorCode::NoActiveAccounts));
    }

    let requested = active_ufvks
        .iter()
        .map(|encoded| {
            UnifiedFullViewingKey::decode(params, encoded)
                .map(|ufvk| ufvk.encode(params))
                .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))
        })
        .collect::<Result<HashSet<_>>>()?;

    let mut selected = Vec::with_capacity(requested.len());
    let mut matched = HashSet::with_capacity(requested.len());
    for (account_id, ufvk) in wallet_ufvks {
        let canonical = ufvk.encode(params);
        if requested.contains(&canonical) && matched.insert(canonical) {
            selected.push((account_id, orchard_only_ufvk(params, &ufvk)?));
        }
    }

    if matched.len() != requested.len() {
        return Err(RuntimeError::with(
            ErrorCode::AccountNotFound,
            json!({
                "requestedActiveUfvks": requested.len(),
                "matchedActiveUfvks": matched.len(),
            }),
        ));
    }

    Ok(selected)
}

fn orchard_only_ufvk(
    params: &zcash_protocol::consensus::Network,
    ufvk: &UnifiedFullViewingKey,
) -> Result<UnifiedFullViewingKey> {
    let encoded = ufvk.encode(params);
    let (network, container) =
        Ufvk::decode(&encoded).map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;
    let orchard = container
        .items()
        .into_iter()
        .filter(|item| matches!(item, Fvk::Orchard(_)))
        .collect::<Vec<_>>();
    let container = Ufvk::try_from_items(orchard)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;
    UnifiedFullViewingKey::decode(params, &container.encode(&network))
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))
}

/// Sync completeness for a single account. No network requests.
///
/// All accounts in a shared database share one scan queue, so whole-database
/// progress is misleading per account. Only pending ranges above this account's
/// birthday count; ranges below it belong to other accounts' history.
pub fn account_status(db: &Db, account_uuid: &str) -> Result<String> {
    let account_id = crate::account::parse_account_id(db, account_uuid)?;
    let birthday = u32::from(
        db.get_account_birthday(account_id)
            .map_err(|e| db_err("getAccountBirthday", e))?,
    );

    let ranges = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;

    let mut remaining: u64 = 0;
    let mut lowest_pending: Option<u32> = None;
    for r in &ranges {
        let end = u32::from(r.block_range().end);
        if end <= birthday {
            continue;
        }
        let start = u32::from(r.block_range().start).max(birthday);
        remaining += u64::from(end.saturating_sub(start));
        lowest_pending = Some(lowest_pending.map_or(start, |cur: u32| cur.min(start)));
    }

    let (chain_tip, fully_scanned) = scan_heights(db)?;

    // Scanned height = lowest pending height for this account, else the chain tip.
    let scanned_to = lowest_pending.or(chain_tip);

    Ok(json!({
        "birthdayHeight": birthday,
        "chainTip": chain_tip,
        "fullyScannedHeight": fully_scanned,
        "scannedToHeight": scanned_to,
        "remainingBlocks": remaining,
        "isComplete": remaining == 0,
    })
    .to_string())
}

// Polling heights must not recompute every account's balances and subtree progress.
fn scan_heights(db: &Db) -> Result<(Option<u32>, Option<u32>)> {
    let chain_tip = db.chain_height().map_err(|e| db_err("chainHeight", e))?;
    let birthday = db
        .get_wallet_birthday()
        .map_err(|e| db_err("walletBirthday", e))?;
    let (Some(chain_tip), Some(birthday)) = (chain_tip, birthday) else {
        return Ok((None, None));
    };
    let fully_scanned = db
        .block_fully_scanned()
        .map_err(|e| db_err("blockFullyScanned", e))?
        .map(|block| u32::from(block.block_height()))
        .unwrap_or(u32::from(birthday).saturating_sub(1));
    Ok((Some(u32::from(chain_tip)), Some(fully_scanned)))
}

/// Read-only: the ranges the wallet currently suggests scanning. No network requests.
pub fn peek(db: &Db) -> Result<String> {
    let ranges = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;

    let items: Vec<_> = ranges
        .iter()
        .map(|r| {
            json!({
                "start": u32::from(r.block_range().start),
                "end": u32::from(r.block_range().end),
                "len": r.len(),
                "priority": format!("{:?}", r.priority()),
            })
        })
        .collect();

    let total: u64 = ranges.iter().map(|r| r.len() as u64).sum();
    let (chain_tip, fully_scanned) = scan_heights(db)?;
    Ok(json!({
        "ranges": items,
        "totalBlocks": total,
        "chainTip": chain_tip,
        "fullyScannedHeight": fully_scanned,
    })
    .to_string())
}

#[cfg(target_arch = "wasm32")]
fn now_ms() -> f64 {
    js_sys::Date::now()
}

#[cfg(not(target_arch = "wasm32"))]
fn now_ms() -> f64 {
    use std::time::{SystemTime, UNIX_EPOCH};
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_secs_f64() * 1000.0)
        .unwrap_or(0.0)
}

/// Anchor at `start - 1`, but not below Sapling activation.
///
/// lightwalletd has no treestate below it, and its error reply hangs the wasm
/// gRPC call forever instead of failing, so clamp before requesting.
fn anchor_height(params: &zcash_protocol::consensus::Network, start: BlockHeight) -> u32 {
    let sapling = params
        .activation_height(NetworkUpgrade::Sapling)
        .map(u32::from)
        .unwrap_or(1);
    u32::from(start).saturating_sub(1).max(sapling)
}

/// Soft download budget. Past it, scan the contiguous prefix received so far
/// instead of waiting for the rest; only a stall is fatal.
const DOWNLOAD_SOFT_BUDGET_MS: f64 = 30_000.0;

/// Stream a block range into the cache; returns the number of blocks stored
/// (a contiguous prefix).
///
/// Each stream item has its own DEFAULT_TIMEOUT_MS stall timeout. A stall before
/// any block arrives is an error (caller retries or halves); a later stall, like
/// the budget, truncates.

async fn download_batch(
    client: &mut LightClient,
    cache: &MemoryBlockCache,
    range: &ScanRange,
) -> Result<usize> {
    let start = service::BlockId {
        height: range.block_range().start.into(),
        hash: Vec::new(),
    };
    let end = service::BlockId {
        height: (range.block_range().end - 1).into(),
        hash: Vec::new(),
    };

    let mut stream = crate::network::with_timeout(
        async {
            client
                .get_block_range(service::BlockRange {
                    start: Some(start),
                    end: Some(end),
                    pool_types: vec![],
                })
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err("getBlockRange", e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        "getBlockRange",
    )
    .await?;

    let started = now_ms();
    let mut blocks = Vec::new();
    loop {
        if !blocks.is_empty() && now_ms() - started > DOWNLOAD_SOFT_BUDGET_MS {
            break;
        }
        let next = crate::network::with_timeout(
            async {
                stream
                    .try_next()
                    .await
                    .map_err(|e| net_err("getBlockRange", e))
            },
            crate::network::DEFAULT_TIMEOUT_MS,
            "getBlockRange",
        )
        .await;
        match next {
            Ok(Some(block)) => blocks.push(block),
            Ok(None) => break, // stream ended normally (full batch)
            Err(e) => {
                if blocks.is_empty() {
                    return Err(e); // stalled with nothing received
                }
                break;
            }
        }
    }

    let got = blocks.len();
    if got == 0 {
        return Err(net_err("getBlockRange", "empty stream for non-empty range"));
    }
    check_block_sequence(
        u32::from(range.block_range().start),
        u32::from(range.block_range().end),
        blocks.iter().map(|b| b.height),
    )?;
    cache.insert(blocks).map_err(|e| db_err("cacheInsert", e))?;
    Ok(got)
}

/// Blocks must start at the range start, be contiguous, and stay below the
/// exclusive end. A truncated prefix is valid (download budget); out-of-order,
/// duplicate, skipped, or out-of-range blocks must not enter the cache.
fn check_block_sequence(
    start: u32,
    end_exclusive: u32,
    heights: impl Iterator<Item = u64>,
) -> Result<()> {
    for (offset, height) in heights.enumerate() {
        let expected = u64::from(start) + offset as u64;
        if height != expected || height >= u64::from(end_exclusive) {
            return Err(net_err(
                "getBlockRange",
                format!(
                    "block {height} where {expected} was expected (range {start}..{end_exclusive})"
                ),
            ));
        }
    }
    Ok(())
}

/// Rewind the wallet to `height` so later syncs rescan above it.
///
/// Destructive: scan results above the height (notes, witnesses, commitment tree
/// checkpoints) are discarded. Broadcast transactions are unaffected on chain,
/// but local unconfirmed records may be lost.
///
/// Returns the actual height, which may be lower than requested because of
/// checkpoint positions.
pub fn rewind_to(db: &mut Db, height: u32) -> Result<u32> {
    let target = BlockHeight::from_u32(height);
    let actual = db
        .truncate_to_height(target)
        .map_err(|e| db_err("truncateToHeight", e))?;
    Ok(u32::from(actual))
}

/// Re-queues an inclusive block range without truncating cached wallet state.
///
/// Used when a paused UFVK becomes active again: the shared scan queue may have advanced
/// past its pause point. The host passes the first height to trial-decrypt again, with a
/// reorg margin applied.
pub fn queue_rescan_from(db: &mut Db, from_height: u32) -> Result<String> {
    let Some(tip) = db
        .chain_height()
        .map_err(|e| db_err("chainHeight", e))?
        .map(u32::from)
    else {
        return Err(RuntimeError::new(ErrorCode::NotSynced));
    };

    if from_height > tip {
        return Ok(json!({
            "queued": false,
            "fromHeight": from_height,
            "toHeight": tip,
        })
        .to_string());
    }

    let range = BlockHeight::from_u32(from_height)..BlockHeight::from_u32(tip.saturating_add(1));
    let ranges = (range, Vec::new()).into();
    db.queue_rescans(ranges, ScanPriority::Historic)
        .map_err(|e| db_err("queueRescanFrom", e))?;

    Ok(json!({
        "queued": true,
        "fromHeight": from_height,
        "toHeight": tip,
    })
    .to_string())
}

#[cfg(test)]
#[path = "sync_tests.rs"]
mod scan_order_tests;

#[cfg(test)]
mod tests {
    use super::*;
    use zcash_keys::keys::UnifiedSpendingKey;
    use zcash_protocol::consensus::Network;

    #[test]
    fn enhancement_only_requests_transactions_owned_by_enabled_accounts() {
        use rand::SeedableRng;
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch("CREATE TABLE v_transactions (txid BLOB, account_uuid BLOB)")
            .unwrap();
        let active = AccountUuid::from_uuid(uuid::Uuid::from_bytes([1; 16]));
        let paused = AccountUuid::from_uuid(uuid::Uuid::from_bytes([2; 16]));
        let active_tx = TxId::from_bytes([3; 32]);
        let paused_tx = TxId::from_bytes([4; 32]);
        for (account, txid) in [(active, active_tx), (paused, paused_tx)] {
            conn.execute(
                "INSERT INTO v_transactions VALUES (?1, ?2)",
                rusqlite::params![txid.as_ref(), account.expose_uuid()],
            )
            .unwrap();
        }
        let mut db = zcash_client_sqlite::WalletDb::from_connection(
            conn,
            Network::TestNetwork,
            crate::clock::HostClock,
            rand_chacha::ChaCha20Rng::from_seed([7; 32]),
        );
        assert_eq!(
            select_enhancement_requests(&mut db, &[active], vec![(0, active_tx), (0, paused_tx)])
                .unwrap(),
            vec![(0, active_tx)]
        );
        assert!(
            select_enhancement_requests(&mut db, &[], vec![(0, active_tx)])
                .unwrap()
                .is_empty()
        );
    }

    #[test]
    fn enhancement_validates_identity_and_complete_serialization() {
        let fixture: serde_json::Value = serde_json::from_str(include_str!(
            "../../zcash-keys-runtime/tests/fixtures/previous-transactions.json"
        ))
        .unwrap();
        let entry = &fixture["transactions"][0];
        let raw = hex::decode(entry["rawTx"].as_str().unwrap()).unwrap();
        let expected = Transaction::read(&raw[..], BranchId::Nu6_2).unwrap().txid();
        assert!(parse_enhancement(&raw, BranchId::Nu6_2, expected).is_ok());
        assert!(parse_enhancement(&raw, BranchId::Nu6_2, TxId::from_bytes([0; 32])).is_err());
        assert!(parse_enhancement(&raw[..4], BranchId::Nu6_2, expected).is_err());
        let mut trailing = raw;
        trailing.push(0);
        assert!(parse_enhancement(&trailing, BranchId::Nu6_2, expected).is_err());
    }

    fn test_ufvk(network: &Network, seed_byte: u8) -> UnifiedFullViewingKey {
        UnifiedSpendingKey::from_seed(
            network,
            &[seed_byte; 32],
            0u32.try_into().expect("valid ZIP-32 account index"),
        )
        .expect("test seed derives a USK")
        .to_unified_full_viewing_key()
    }

    #[test]
    fn only_a_contiguous_prefix_of_the_range_is_accepted() {
        assert!(check_block_sequence(10, 13, [10u64, 11, 12].into_iter()).is_ok());
        assert!(check_block_sequence(10, 13, [10u64, 11].into_iter()).is_ok());
        assert!(check_block_sequence(10, 13, [11u64].into_iter()).is_err());
        assert!(check_block_sequence(10, 13, [10u64, 10].into_iter()).is_err());
        assert!(check_block_sequence(10, 13, [10u64, 12].into_iter()).is_err());
        assert!(check_block_sequence(10, 12, [10u64, 11, 12].into_iter()).is_err());
    }

    fn selected_ufvks_for(
        network: &Network,
        wallet_ufvks: HashMap<u32, UnifiedFullViewingKey>,
        active_ufvks: &[String],
    ) -> Result<Vec<(u32, UnifiedFullViewingKey)>> {
        select_active_scanning_ufvks(network, wallet_ufvks, active_ufvks)
    }

    #[test]
    fn anchor_never_precedes_sapling_activation() {
        for net in [Network::MainNetwork, Network::TestNetwork] {
            let sapling = u32::from(net.activation_height(NetworkUpgrade::Sapling).unwrap());
            // Far below Sapling: must clamp to activation or gRPC hangs
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(1)), sapling);
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(sapling)), sapling);
            // Normally start - 1
            let h = sapling + 1000;
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(h)), h - 1);
        }
    }

    #[test]
    fn only_explicitly_active_ufvk_gets_scanning_keys() {
        let network = Network::TestNetwork;
        let active = test_ufvk(&network, 1);
        let inactive = test_ufvk(&network, 2);
        let keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active.clone()), (20, inactive)]),
                &[active.encode(&network)],
            )
            .expect("active UFVK is present in the wallet database"),
        );

        assert!(keys.sapling().is_empty());
        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
        assert!(keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 10));
        assert!(keys
            .ironwood()
            .keys()
            .all(|(account_id, _)| *account_id == 10));
    }

    #[test]
    fn duplicate_active_ufvk_is_deduplicated() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 3);
        let encoded = ufvk.encode(&network);
        let keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, ufvk)]),
                &[encoded.clone(), encoded],
            )
            .expect("duplicate host aliases select one runtime identity"),
        );

        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
    }

    #[test]
    fn empty_active_set_fails_closed() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 4);
        let error = selected_ufvks_for(&network, HashMap::from([(10, ufvk)]), &[])
            .expect_err("an empty set must not fall back to every database account");

        assert_eq!(error.code, ErrorCode::NoActiveAccounts);
    }

    #[test]
    fn sapling_component_never_reaches_scanning_keys() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 5);
        assert!(ufvk.sapling().is_some());
        assert!(ufvk.orchard().is_some());

        let encoded = ufvk.encode(&network);
        let selected =
            select_active_scanning_ufvks(&network, HashMap::from([(10, ufvk)]), &[encoded])
                .expect("active UFVK is present in the wallet database");
        assert!(selected[0].1.sapling().is_none());
        assert!(selected[0].1.orchard().is_some());

        let keys = ScanningKeys::from_account_ufvks(selected);
        assert!(keys.sapling().is_empty());
        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
    }

    #[test]
    fn paused_identity_can_requeue_range_advanced_by_another_identity() {
        let network = Network::TestNetwork;
        let paused = test_ufvk(&network, 6);
        let active = test_ufvk(&network, 7);
        let active_keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active.clone()), (20, paused.clone())]),
                &[active.encode(&network)],
            )
            .expect("the active identity can advance the shared queue alone"),
        );
        assert!(active_keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 10));

        let mut db = crate::wallet::open_and_migrate("test", ":memory:")
            .expect("in-memory wallet database opens");
        let activation = u32::from(
            network
                .activation_height(NetworkUpgrade::Sapling)
                .expect("testnet has Sapling activation"),
        );
        let resume_from = activation + 50;
        let tip = activation + 100;
        db.update_chain_tip(BlockHeight::from_u32(tip))
            .expect("chain tip update succeeds");

        let result: serde_json::Value = serde_json::from_str(
            &queue_rescan_from(&mut db, resume_from).expect("resume range is queued"),
        )
        .expect("queue result is JSON");
        assert_eq!(result["queued"], true);
        assert_eq!(result["fromHeight"], resume_from);
        assert_eq!(result["toHeight"], tip);

        let ranges = db.suggest_scan_ranges().expect("scan queue is readable");
        assert!(ranges.iter().any(|range| {
            range.priority() == ScanPriority::Historic
                && u32::from(range.block_range().start) <= resume_from
                && u32::from(range.block_range().end) >= tip + 1
        }));

        let resumed_keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active), (20, paused.clone())]),
                &[paused.encode(&network)],
            )
            .expect("the resumed identity is explicitly selected"),
        );
        assert!(resumed_keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 20));
    }
}
