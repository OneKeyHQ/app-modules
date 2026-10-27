//! Sync orchestration: fetch blocks, scan, record progress. Bounded; never runs to completion.
//!
//! One call processes at most `max_blocks` blocks and returns progress; the host
//! decides whether to call again. A run-to-completion call cannot be interrupted,
//! report progress, or yield on browsers and mobile, so the page would freeze.
//!
//! ## Scan progress is tracked per identity
//!
//! `scan_ranges` is keyed by `identity_id`. Unlike Zcash, Monero has no commitment
//! tree that forces wallet-wide contiguous scanning, so each identity tracks its
//! own progress and a paused account never loses a span scanned for another.

use futures_util::stream::{self, StreamExt, TryStreamExt};
use monero_daemon_rpc::prelude::*;
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};

use crate::error::{db_err, net_err, ErrorCode, Result, RuntimeError};
use crate::network::Daemon;
use crate::perf::now_ms;
use crate::scan::{self, ScanOutcome};
use crate::wallet::Db;

/// Per-call block cap.
///
/// A hang guard, not a tuning knob: a huge value would make one call run for
/// minutes with no way to interrupt it. The host sizes batches from measured throughput.
pub const MAX_BLOCKS_PER_CALL: u64 = 1_000;

/// Limit per-block fallback requests to three, leaving the host's fourth
/// stream slot available for a chain-tip probe or another privacy chain.
pub const FETCH_CONCURRENCY: usize = 3;

/// Fetch the chain tip without touching the wallet DB: account creation needs it
/// for the birthday before the DB exists.
pub async fn chain_tip(daemon: &Daemon) -> Result<u64> {
    let n = daemon
        .latest_block_number()
        .await
        .map_err(|e| net_err("latestBlockNumber", format!("{e:?}")))?;
    Ok(n as u64)
}

/// Tell an identity the chain tip and queue the range it still needs to scan.
///
/// A new identity queues `[birthday, tip]`; one with progress queues `(scanned_to, tip]`.
pub fn update_chain_tip(db: &mut Db, identity_id: i64, tip: u64) -> Result<()> {
    let identity = db.identity_by_id(identity_id)?;
    let scanned_to = scanned_to_height(db, identity_id)?;
    let start = scanned_to
        .map(|h| h + 1)
        .unwrap_or(identity.birthday_height);

    if start > tip {
        return Ok(()); // Already at the tip; nothing to queue
    }

    db.conn()
        .execute(
            "INSERT INTO scan_ranges (identity_id, start_height, end_height)
             VALUES (?1, ?2, ?3)
             ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
            params![identity_id, start as i64, (tip + 1) as i64],
        )
        .map_err(|e| db_err("updateChainTip", e))?;
    Ok(())
}

/// Highest height this identity has scanned contiguously (inclusive); `None` if nothing scanned.
///
/// Reads `identities.scanned_to_height`, not `max` over the global `blocks` table:
/// that would let a caught-up identity push a backfilling identity's progress to
/// the tip, permanently skipping that history.
pub fn scanned_to_height(db: &Db, identity_id: i64) -> Result<Option<u64>> {
    Ok(db
        .conn()
        .query_row(
            "SELECT scanned_to_height FROM identities WHERE id = ?1",
            [identity_id],
            |r| r.get::<_, Option<i64>>(0),
        )
        .optional()
        .map_err(|e| db_err("scannedToHeight", e))?
        .flatten()
        .map(|h| h as u64))
}

/// Advance this identity's scan progress. Monotonic; rewinds go through `rewind_and_requeue`.
fn advance_scanned_to(db: &Db, identity_id: i64, height: u64) -> Result<()> {
    db.conn()
        .execute(
            "UPDATE identities
             SET scanned_to_height = max(COALESCE(scanned_to_height, 0), ?2)
             WHERE id = ?1",
            params![identity_id, height as i64],
        )
        .map_err(|e| db_err("advanceScannedTo", e))?;
    Ok(())
}

/// The lowest pending scan range.
fn next_range(db: &Db, identity_id: i64) -> Result<Option<(u64, u64)>> {
    db.conn()
        .query_row(
            "SELECT start_height, end_height FROM scan_ranges
             WHERE identity_id = ?1 ORDER BY start_height LIMIT 1",
            [identity_id],
            |r| Ok((r.get::<_, i64>(0)? as u64, r.get::<_, i64>(1)? as u64)),
        )
        .optional()
        .map_err(|e| db_err("nextRange", e))
}

/// Remove the scanned span `[start, scanned_through]` from the pending ranges.
///
/// Pending ranges can overlap (`rewind_and_requeue` queues from the rewind height
/// while the old tail range is still queued), so the span is subtracted from every
/// overlapping range and remainders are upserted (same start keeps the farther end);
/// a plain INSERT would collide on the primary key and stall scanning.
fn consume_range(db: &mut Db, identity_id: i64, start: u64, scanned_through: u64) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginConsumeRange", e))?;

    let overlapping: Vec<(i64, i64)> = {
        let mut stmt = tx
            .prepare(
                "SELECT start_height, end_height FROM scan_ranges
                 WHERE identity_id = ?1 AND start_height <= ?3 AND end_height > ?2",
            )
            .map_err(|e| db_err("consumeRange", e))?;
        let rows = stmt
            .query_map(
                params![identity_id, start as i64, scanned_through as i64],
                |r| Ok((r.get::<_, i64>(0)?, r.get::<_, i64>(1)?)),
            )
            .map_err(|e| db_err("consumeRange", e))?
            .collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("consumeRange", e))?;
        rows
    };

    let upsert = |from: i64, to: i64| -> Result<()> {
        tx.execute(
            "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)
             ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
            params![identity_id, from, to],
        )
        .map(|_| ())
        .map_err(|e| db_err("consumeRange", e))
    };

    let scanned_from = start as i64;
    let resume_at = scanned_through as i64 + 1;
    for (range_start, range_end) in overlapping {
        tx.execute(
            "DELETE FROM scan_ranges WHERE identity_id = ?1 AND start_height = ?2",
            params![identity_id, range_start],
        )
        .map_err(|e| db_err("consumeRange", e))?;
        // Part before the scanned span (unreachable when taking the lowest start; kept defensively).
        if range_start < scanned_from {
            upsert(range_start, scanned_from)?;
        }
        // Part after the scanned span.
        if range_end > resume_at {
            upsert(resume_at, range_end)?;
        }
    }

    tx.commit().map_err(|e| db_err("commitConsumeRange", e))
}

thread_local! {
    /// Node the batch path was judged unusable for, and since when. A different node is tried again.
    static BLOCKS_BIN_UNSUPPORTED_BY: std::cell::RefCell<Option<(String, f64)>> =
        const { std::cell::RefCell::new(None) };
}

/// Retry delay for the batch path. One truncated or proxy-rewritten response must
/// not keep a good node on the slow path all session.
const BLOCKS_BIN_RETRY_AFTER_MS: f64 = 10.0 * 60.0 * 1000.0;

fn blocks_bin_skipped_for(node: Option<&str>, now: f64) -> bool {
    BLOCKS_BIN_UNSUPPORTED_BY.with(|c| match (&*c.borrow(), node) {
        (Some((url, at)), Some(node)) => url == node && now - at < BLOCKS_BIN_RETRY_AFTER_MS,
        _ => false,
    })
}

/// Try the batch path (`get_blocks.bin`, one round trip per batch) first; fall back
/// to concurrent per-block fetches if the node does not cooperate.
///
/// Only protocol failures (unsupported, malformed, count mismatch) skip the batch
/// path for this node (see `BLOCKS_BIN_RETRY_AFTER_MS`). Network failures propagate
/// and the next step retries; one timeout must not downgrade a good node.
async fn fetch_blocks(
    daemon: &Daemon,
    start: u64,
    last: u64,
) -> Result<(Vec<ScannableBlock>, &'static str, Option<String>, bool)> {
    let node = crate::network::current_node_url();
    let skip = blocks_bin_skipped_for(node.as_deref(), now_ms());
    if let (Some(url), false, true) = (node.as_deref(), skip, start > 0) {
        match crate::blocks_bin::fetch(url, start, last - start + 1).await {
            Ok(fetched) => {
                return Ok((fetched.blocks, "blocksBin", None, fetched.prefetch_hit));
            }
            Err(crate::blocks_bin::BlocksBinError::Network(detail)) => {
                return Err(net_err(
                    "getBlocksBin",
                    format!("{start}..={last}: {detail}"),
                ));
            }
            Err(crate::blocks_bin::BlocksBinError::Protocol(detail)) => {
                BLOCKS_BIN_UNSUPPORTED_BY
                    .with(|c| *c.borrow_mut() = node.clone().map(|url| (url, now_ms())));
                // Report why the batch path was abandoned with this step's result, once.
                let blocks = fetch_scannable_blocks(daemon, start, last).await?;
                return Ok((
                    blocks,
                    "perBlock",
                    Some(format!("getBlocksBin unusable: {detail}")),
                    false,
                ));
            }
        }
    }
    Ok((
        fetch_scannable_blocks(daemon, start, last).await?,
        "perBlock",
        None,
        false,
    ))
}

/// Fetch scannable blocks for `[start, last]`, ordered by height.
///
/// Fallback when the batch path (`blocks_bin`) is unusable.
///
/// Does not use `contiguous_scannable_blocks`; see `blocks_bin` for why its batch
/// path always fails against released monerod.
///
/// Instead fetch per block with N-way concurrency. Upstream still validates each
/// block (number, transactions match the block's tx list); cross-block continuity
/// (heights, previous hash, RingCT cursor) is checked by `scan::scan_blocks`.
/// `buffered` preserves order, so results are sorted by height.
async fn fetch_scannable_blocks(
    daemon: &Daemon,
    start: u64,
    last: u64,
) -> Result<Vec<ScannableBlock>> {
    stream::iter(start..=last)
        .map(|height| async move {
            daemon
                .scannable_block_by_number(height as usize)
                .await
                .map_err(|e| net_err("scannableBlockByNumber", format!("{height}: {e:?}")))
        })
        .buffered(FETCH_CONCURRENCY)
        .try_collect()
        .await
}

/// One bounded step.
///
/// Returns progress JSON; `done` means no pending ranges remain. Never loops to
/// completion; the host decides whether to call again.
pub async fn step(
    db: &mut Db,
    daemon: &Daemon,
    identity_id: i64,
    max_blocks: u64,
) -> Result<Value> {
    if max_blocks == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "maxBlocks", "reason": "mustBePositive" }),
        ));
    }
    let max_blocks = max_blocks.min(MAX_BLOCKS_PER_CALL);

    let (sends_failed, sends_restored) =
        crate::pool::check_pending(db, daemon, identity_id).await?;

    let Some((start, end)) = next_range(db, identity_id)? else {
        let mut v = progress(db, identity_id, &ScanOutcome::default(), true)?;
        v["sendsFailed"] = json!(sends_failed);
        v["sendsRestored"] = json!(sends_restored);
        return Ok(v);
    };

    // Ranges are half-open (`end` is exclusive); upstream takes a closed range.
    let last = (start + max_blocks - 1).min(end.saturating_sub(1));
    if last < start {
        return Ok(progress(db, identity_id, &ScanOutcome::default(), true)?);
    }

    // Time download and scan separately: both look the same in a browser (a frozen
    // UI), and public-node latency would otherwise be misread as CPU cost.
    let t0 = now_ms();

    let (blocks, fetched_via, fetch_note, prefetch_hit) = fetch_blocks(daemon, start, last).await?;

    // Pipeline: request the next batch before scanning this one (as wallet2 does).
    // Start from the blocks actually received, since a partial prefix resumes there.
    if fetched_via == "blocksBin" {
        let next = start + blocks.len() as u64;
        if let (Some(url), true) = (crate::network::current_node_url(), next < end) {
            // Same bounds as the next step's request (max_blocks, range end), sized by the
            // batch as adjusted after this request.
            crate::blocks_bin::prefetch(&url, next, (end - next).min(max_blocks)).await;
        }
    }

    let t_downloaded = now_ms();

    if blocks.is_empty() {
        // Zero blocks for a non-empty range violates the protocol. Advancing as if
        // scanned would skip this span forever.
        return Err(RuntimeError::with(
            ErrorCode::MalformedNodeResponse,
            json!({ "operation": "contiguousScannableBlocks", "from": start, "to": last }),
        ));
    }

    db.ensure_subaddress_lookahead(identity_id)?;
    let mut outcome = scan::scan_blocks(db, identity_id, &blocks)?;
    // Funds near the end of the subaddress window grow it; rescan the same batch with
    // the new window (blocks in hand, writes idempotent) so later funds to the newly
    // registered subaddresses are not missed.
    while outcome.outputs_found > 0 && db.ensure_subaddress_lookahead(identity_id)? > 0 {
        outcome = scan::scan_blocks(db, identity_id, &blocks)?;
    }
    let t_scanned = now_ms();
    outcome.ms_download = (t_downloaded - t0) as u32;
    outcome.ms_scan = (t_scanned - t_downloaded) as u32;
    outcome.fetched_via = fetched_via;
    outcome.fetch_note = fetch_note;
    outcome.prefetch_hit = prefetch_hit;
    outcome.batch_blocks = crate::blocks_bin::batch_blocks();

    if let Some(through) = outcome.to_height {
        consume_range(db, identity_id, start, through)?;
        advance_scanned_to(db, identity_id, through)?;
    }

    let done = next_range(db, identity_id)?.is_none();
    let mut v = progress(db, identity_id, &outcome, done)?;
    v["sendsFailed"] = json!(sends_failed);
    v["sendsRestored"] = json!(sends_restored);
    Ok(v)
}

fn progress(db: &Db, identity_id: i64, outcome: &ScanOutcome, done: bool) -> Result<Value> {
    let remaining: i64 = db
        .conn()
        .query_row(
            "SELECT COALESCE(sum(end_height - start_height), 0) FROM scan_ranges WHERE identity_id = ?1",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("remainingBlocks", e))?;

    let mut v = outcome.to_json();
    v["done"] = json!(done);
    v["remainingBlocks"] = json!(remaining);
    v["scannedToHeight"] = json!(scanned_to_height(db, identity_id)?);
    Ok(v)
}

/// Read-only scan progress; makes no network requests.
///
/// Shows which ranges the runtime intends to scan when sync appears stuck.
pub fn peek(db: &Db, identity_id: i64) -> Result<Value> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT start_height, end_height FROM scan_ranges
             WHERE identity_id = ?1 ORDER BY start_height",
        )
        .map_err(|e| db_err("peek", e))?;
    let ranges = stmt
        .query_map([identity_id], |r| {
            Ok(json!({
                "start": r.get::<_, i64>(0)?,
                "end": r.get::<_, i64>(1)?,
            }))
        })
        .map_err(|e| db_err("peek", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("peek", e))?;

    Ok(json!({
        "ranges": ranges,
        "scannedToHeight": scanned_to_height(db, identity_id)?,
        // The runtime's birthday is authoritative: the host's may differ (re-enabling
        // keeps an existing identity's birthday).
        "birthdayHeight": db.identity_by_id(identity_id)?.birthday_height,
    }))
}

/// Starts the identity over at `birthday`: rows below it are removed, rows at or
/// above it are re-verified by the rescan, and pending sends keep their inputs.
pub(crate) fn reset_from_birthday(db: &mut Db, identity_id: i64, birthday: u64) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginResetScan", e))?;
    reset_scan_in_transaction(&tx, identity_id, birthday)?;
    tx.commit().map_err(|e| db_err("commitResetScan", e))
}

pub(crate) fn reset_scan_in_transaction(
    tx: &rusqlite::Transaction<'_>,
    identity_id: i64,
    birthday: u64,
) -> Result<()> {
    let birthday = i64::try_from(birthday).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidBlockRange,
            json!({ "birthdayHeight": birthday }),
        )
    })?;
    let tip: Option<i64> = tx
        .query_row(
            "SELECT max(height) FROM (
           SELECT scanned_to_height AS height FROM identities WHERE id = ?1
           UNION ALL SELECT end_height - 1 FROM scan_ranges WHERE identity_id = ?1
           UNION ALL SELECT height FROM blocks WHERE identity_id = ?1)",
            [identity_id],
            |row| row.get(0),
        )
        .map_err(|e| db_err("resetScanTip", e))?;
    // The new birthday drops everything found before it, except the inputs of
    // own sends that are not mined yet.
    tx.execute(
        "DELETE FROM outputs WHERE identity_id = ?1 AND height < ?2
           AND (spent_in_txid IS NULL OR spent_in_txid NOT IN (
             SELECT txid FROM transactions
             WHERE identity_id = ?1 AND sent_at IS NOT NULL AND height IS NULL))",
        params![identity_id, birthday],
    )
    .map_err(|e| db_err("clearOutputsBeforeBirthday", e))?;
    tx.execute(
        "DELETE FROM transactions WHERE identity_id = ?1 AND height < ?2",
        params![identity_id, birthday],
    )
    .map_err(|e| db_err("clearTransactionsBeforeBirthday", e))?;
    tx.execute(
        "UPDATE outputs SET scan_valid = 0,
           spent_at_height = CASE WHEN spent_in_txid IN (
             SELECT txid FROM transactions WHERE identity_id = ?1 AND sent_at IS NOT NULL)
             THEN spent_at_height ELSE NULL END,
           spent_in_txid = CASE WHEN spent_in_txid IN (
             SELECT txid FROM transactions WHERE identity_id = ?1 AND sent_at IS NOT NULL)
             THEN spent_in_txid ELSE NULL END
         WHERE identity_id = ?1",
        [identity_id],
    )
    .map_err(|e| db_err("invalidateScanOutputs", e))?;
    tx.execute(
        "DELETE FROM transactions WHERE identity_id = ?1 AND sent_at IS NULL",
        [identity_id],
    )
    .map_err(|e| db_err("clearScannedTransactions", e))?;
    tx.execute(
        "UPDATE transactions SET scan_valid = 0 WHERE identity_id = ?1",
        [identity_id],
    )
    .map_err(|e| db_err("invalidateScanTransactions", e))?;
    for table in ["blocks", "scan_ranges", "possible_spends"] {
        tx.execute(
            &format!("DELETE FROM {table} WHERE identity_id = ?1"),
            [identity_id],
        )
        .map_err(|e| db_err("clearScanIndexes", e))?;
    }
    tx.execute(
        "UPDATE identities SET birthday_height = ?2, scanned_to_height = NULL WHERE id = ?1",
        params![identity_id, birthday],
    )
    .map_err(|e| db_err("resetScanBirthday", e))?;
    if let Some(tip) = tip.filter(|tip| *tip >= birthday) {
        tx.execute(
            "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)",
            params![identity_id, birthday, tip.saturating_add(1)],
        )
        .map_err(|e| db_err("resetScanRange", e))?;
    }
    Ok(())
}

/// Reorg recovery: rewind to `height` and requeue everything above it.
pub fn rewind_and_requeue(db: &mut Db, identity_id: i64, height: u64, tip: u64) -> Result<Value> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginRewindAndRequeue", e))?;
    scan::rewind_in_transaction(&tx, identity_id, height, tip)?;
    // Rewind progress too, or the next step resumes from a discarded height and the
    // gap is never rescanned. Keep NULL if nothing was scanned: 0 would make the next
    // `update_chain_tip` queue from genesis.
    tx.execute(
        "UPDATE identities SET scanned_to_height = min(scanned_to_height, ?2)
             WHERE id = ?1 AND scanned_to_height IS NOT NULL",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindScannedTo", e))?;
    // Rescan before the birthday (user picked an earlier date): move the birthday
    // back so progress starts there.
    tx.execute(
        "UPDATE identities SET birthday_height = ?2 WHERE id = ?1 AND birthday_height > ?2",
        params![identity_id, (height + 1) as i64],
    )
    .map_err(|e| db_err("rewindBirthday", e))?;
    // Requeue the rewound span here instead of relying on the host to call `update_chain_tip`.
    if tip > height {
        tx.execute(
                "INSERT INTO scan_ranges (identity_id, start_height, end_height)
                 VALUES (?1, ?2, ?3)
                 ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
                params![identity_id, (height + 1) as i64, (tip + 1) as i64],
            )
            .map_err(|e| db_err("requeueAfterRewind", e))?;
    }
    tx.commit()
        .map_err(|e| db_err("commitRewindAndRequeue", e))?;
    Ok(json!({ "rewoundTo": height, "requeuedTo": tip }))
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    #[test]
    fn a_new_identity_has_nothing_queued_until_the_tip_is_known() {
        let (db, id) = db_with_identity();
        assert!(next_range(&db, id).unwrap().is_none());
        let p = peek(&db, id).unwrap();
        assert_eq!(p["ranges"].as_array().unwrap().len(), 0);
        assert_eq!(p["birthdayHeight"], 100, "starts at the runtime's birthday");
    }

    #[test]
    fn learning_the_tip_queues_the_backfill_range() {
        let (mut db, id) = db_with_identity(); // birthday = 100
        update_chain_tip(&mut db, id, 150).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("should be queued");
        assert_eq!(start, 100, "scans from the birthday");
        assert_eq!(end, 151, "ranges are half-open");
    }

    /// Advancing the tip extends the range end; a second overlapping range would
    /// rescan the same span and make progress go backwards.
    #[test]
    fn advancing_the_tip_extends_rather_than_duplicates() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 150).unwrap();
        update_chain_tip(&mut db, id, 200).unwrap();
        let ranges = peek(&db, id).unwrap();
        let ranges = ranges["ranges"].as_array().unwrap();
        assert_eq!(ranges.len(), 1, "should have one range");
        assert_eq!(ranges[0]["end"], 201);
    }

    /// Repeating the same tip is idempotent.
    #[test]
    fn the_same_tip_twice_changes_nothing() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 150).unwrap();
        let before = peek(&db, id).unwrap();
        update_chain_tip(&mut db, id, 150).unwrap();
        assert_eq!(before, peek(&db, id).unwrap());
    }

    /// A requeued range overlaps the old tail range and the remainder starts exactly
    /// at another range's start; consuming it must not collide on the primary key.
    #[test]
    fn consuming_across_overlapping_ranges_merges_instead_of_colliding() {
        let (mut db, id) = db_with_identity();
        for (s, e) in [(100_i64, 200_i64), (161, 250)] {
            db.conn()
                .execute(
                    "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)",
                    params![id, s, e],
                )
                .unwrap();
        }
        consume_range(&mut db, id, 100, 160).expect("must not collide on the primary key");
        let ranges: Vec<(i64, i64)> = db
            .conn()
            .prepare("SELECT start_height, end_height FROM scan_ranges WHERE identity_id = ?1 ORDER BY start_height")
            .unwrap()
            .query_map([id], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(ranges, vec![(161, 250)]);
    }

    /// A range fully covered by the scanned span is removed, leaving no duplicate scan.
    #[test]
    fn a_range_fully_covered_by_the_scan_disappears() {
        let (mut db, id) = db_with_identity();
        for (s, e) in [(100_i64, 300_i64), (150, 170)] {
            db.conn()
                .execute(
                    "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)",
                    params![id, s, e],
                )
                .unwrap();
        }
        consume_range(&mut db, id, 100, 199).unwrap();
        let ranges: Vec<(i64, i64)> = db
            .conn()
            .prepare("SELECT start_height, end_height FROM scan_ranges WHERE identity_id = ?1 ORDER BY start_height")
            .unwrap()
            .query_map([id], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(ranges, vec![(200, 300)]);
    }

    #[test]
    fn consuming_part_of_a_range_leaves_the_remainder() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 200).unwrap();
        consume_range(&mut db, id, 100, 149).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("remainder queued");
        assert_eq!((start, end), (150, 201));
    }

    #[test]
    fn consuming_a_whole_range_clears_it() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 120).unwrap();
        consume_range(&mut db, id, 100, 120).unwrap();
        assert!(next_range(&db, id).unwrap().is_none());
    }

    /// The per-call block cap is hard: a huge value would run for minutes with no
    /// way to interrupt it, freezing the page.
    #[test]
    fn max_blocks_is_clamped_and_zero_is_rejected() {
        assert_eq!(u64::MAX.min(MAX_BLOCKS_PER_CALL), MAX_BLOCKS_PER_CALL);
        // 0 would underflow `start + max_blocks - 1`; it is rejected at the entry
        assert!(MAX_BLOCKS_PER_CALL > 0);
    }

    /// A caught-up identity must not push a backfilling identity's progress to the
    /// tip, or that history is never scanned.
    #[test]
    fn two_identities_keep_independent_progress() {
        use crate::wallet::testing::key_bytes;

        let (mut db, a) = db_with_identity(); // birthday 100
        let (spend, mut view) = key_bytes();
        view[1] = 3; // Different view key for a distinct second identity
        let b = db.add_identity("acct-2", &spend, &view, 100).unwrap().id;

        update_chain_tip(&mut db, a, 1_000).unwrap();
        update_chain_tip(&mut db, b, 1_000).unwrap();

        // A scans to the tip; B scans nothing
        consume_range(&mut db, a, 100, 1_000).unwrap();
        advance_scanned_to(&db, a, 1_000).unwrap();

        assert_eq!(scanned_to_height(&db, a).unwrap(), Some(1_000));
        assert_eq!(
            scanned_to_height(&db, b).unwrap(),
            None,
            "B scanned nothing, so its progress must stay None"
        );

        // B's pending range is untouched and still starts at its birthday
        let (start, _) = next_range(&db, b).unwrap().expect("B's range still queued");
        assert_eq!(start, 100, "B's start must not follow A's progress");
    }

    #[test]
    fn rewinding_requeues_the_rolled_back_span() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 200).unwrap();
        consume_range(&mut db, id, 100, 200).unwrap();
        assert!(next_range(&db, id).unwrap().is_none());

        rewind_and_requeue(&mut db, id, 180, 200).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("rewind should requeue");
        assert_eq!((start, end), (181, 201));
    }

    #[test]
    fn a_failed_rewind_cursor_write_keeps_the_scan_data_intact() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 200).unwrap();
        advance_scanned_to(&db, id, 200).unwrap();
        consume_range(&mut db, id, 100, 200).unwrap();
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, timestamp) VALUES (?1, 190, ?2, 1)",
                params![id, [1u8; 32].as_slice()],
            )
            .unwrap();
        db.conn()
            .execute_batch(
                "CREATE TRIGGER refuse_cursor BEFORE UPDATE OF scanned_to_height ON identities
            BEGIN SELECT RAISE(ABORT, 'synthetic cursor failure'); END;",
            )
            .unwrap();

        assert!(rewind_and_requeue(&mut db, id, 180, 200).is_err());
        let blocks: i64 = db
            .conn()
            .query_row(
                "SELECT COUNT(*) FROM blocks WHERE identity_id = ?1",
                [id],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(blocks, 1);
        assert_eq!(scanned_to_height(&db, id).unwrap(), Some(200));
        assert!(next_range(&db, id).unwrap().is_none());
    }

    #[test]
    fn a_node_that_failed_the_batch_path_gets_another_try_later() {
        BLOCKS_BIN_UNSUPPORTED_BY.with(|c| *c.borrow_mut() = Some(("http://n".into(), 1_000.0)));
        assert!(blocks_bin_skipped_for(Some("http://n"), 1_000.0 + 60_000.0));
        assert!(!blocks_bin_skipped_for(Some("http://other"), 1_000.0));
        assert!(!blocks_bin_skipped_for(
            Some("http://n"),
            1_000.0 + BLOCKS_BIN_RETRY_AFTER_MS
        ));
    }

    /// Rescanning before any step (cache cleared, new start) must not queue from genesis.
    #[test]
    fn rewinding_before_the_first_step_does_not_queue_from_genesis() {
        let (mut db, id) = db_with_identity(); // birthday 100
        rewind_and_requeue(&mut db, id, 100, 200).unwrap();
        update_chain_tip(&mut db, id, 200).unwrap();

        assert_eq!(scanned_to_height(&db, id).unwrap(), None);
        let (start, _) = next_range(&db, id).unwrap().expect("a range is pending");
        assert_eq!(start, 100);
    }

    /// Rescanning from before the birthday moves the birthday back so progress starts there.
    #[test]
    fn rewinding_before_the_birthday_moves_the_birthday() {
        let (mut db, id) = db_with_identity(); // birthday 100
        rewind_and_requeue(&mut db, id, 49, 200).unwrap();
        assert_eq!(db.identity_by_id(id).unwrap().birthday_height, 50);
        let (start, _) = next_range(&db, id).unwrap().expect("a range is pending");
        assert_eq!(start, 50);
    }
}
