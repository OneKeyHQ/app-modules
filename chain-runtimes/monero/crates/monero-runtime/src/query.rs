//! Balance, history and spendable-output queries.
//!
//! Paging and aggregation run as indexed SQLite queries.

use monero_wallet::address::Network;
use monero_wallet::WalletOutput;
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::wallet::Db;

/// Consensus lock on all outputs: 10 confirmations. `additional_timelock` applies on top.
pub const COINBASE_AND_STANDARD_LOCK: u64 = 10;

/// `CRYPTONOTE_MAX_BLOCK_NUMBER`: unlock values at or above it are Unix timestamps.
const MAX_BLOCK_NUMBER: u64 = 500_000_000;
/// `BLOCKCHAIN_TIMESTAMP_CHECK_WINDOW`.
const TIMESTAMP_CHECK_WINDOW: usize = 60;
/// `CRYPTONOTE_LOCKED_TX_ALLOWED_DELTA_BLOCKS`: leeway on height-based unlock times.
const LOCKED_TX_ALLOWED_DELTA_BLOCKS: u64 = 1;
/// `DIFFICULTY_TARGET_V1` / `DIFFICULTY_TARGET_V2`, in seconds.
const TARGET_SECONDS_V1: u64 = 60;
const TARGET_SECONDS_V2: u64 = 120;

#[derive(Debug, Default, Clone)]
pub struct Balance {
    /// Sum of all unspent outputs.
    pub total_atomic: u128,
    /// Spendable now: past the 10-block lock and any extra timelock.
    pub unlocked_atomic: u128,
    /// Outputs without a key image that appear in some ring; may already be spent.
    pub unverifiable_atomic: u128,
    /// Change from own unconfirmed sends. Counted in total but locked, as in wallet2.
    pub pending_change_atomic: u128,
    /// Unspent outputs lacking a key image; selection only uses outputs with one.
    pub outputs_without_key_image: u64,
    /// Blocks until every output still inside the 10-block lock is spendable.
    pub confirming_blocks_left: Option<u64>,
}

impl Balance {
    pub fn to_json(&self) -> Value {
        // Strings: piconero values exceed JS Number.MAX_SAFE_INTEGER.
        json!({
            "totalAtomic": self.total_atomic.to_string(),
            "unlockedAtomic": self.unlocked_atomic.to_string(),
            "unverifiableAtomic": self.unverifiable_atomic.to_string(),
            "pendingChangeAtomic": self.pending_change_atomic.to_string(),
            "outputsWithoutKeyImage": self.outputs_without_key_image,
            "confirmingBlocksLeft": self.confirming_blocks_left,
        })
    }
}

/// Computes the balance. `chain_tip` drives the lock check; `None` (tip unknown)
/// treats everything as locked rather than offer funds that cannot be spent.
pub fn balance(db: &Db, identity_id: i64, chain_tip: Option<u64>) -> Result<Balance> {
    let now = adjusted_time(db, identity_id)?;
    let network = db.network();
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.amount_atomic, o.height, o.additional_timelock,
                    o.key_image IS NULL AS no_ki,
                    EXISTS (SELECT 1 FROM possible_spends p
                            WHERE p.identity_id = o.identity_id
                              AND p.out_txid = o.txid AND p.out_index = o.index_in_tx) AS maybe_spent
             FROM outputs o
             WHERE o.identity_id = ?1 AND o.scan_valid = 1 AND o.spent_at_height IS NULL",
        )
        .map_err(|e| db_err("balanceQuery", e))?;

    let rows = stmt
        .query_map([identity_id], |r| {
            Ok((
                r.get::<_, i64>(0)? as u128,
                r.get::<_, i64>(1)? as u64,
                r.get::<_, i64>(2)? as u64,
                r.get::<_, bool>(3)?,
                r.get::<_, bool>(4)?,
            ))
        })
        .map_err(|e| db_err("balanceQuery", e))?;

    let mut b = Balance::default();
    for row in rows {
        let (amount, height, timelock, no_key_image, maybe_spent) =
            row.map_err(|e| db_err("balanceQuery", e))?;
        b.total_atomic += amount;
        if no_key_image {
            b.outputs_without_key_image += 1;
        }
        // May be spent, so not counted as unlocked.
        if no_key_image && maybe_spent {
            b.unverifiable_atomic += amount;
            continue;
        }
        if let Some(tip) = chain_tip {
            if is_unlocked(height, timelock, tip, now, network) {
                b.unlocked_atomic += amount;
            } else {
                // As is_unlocked: the chain length is tip + 1.
                let left = (height + COINBASE_AND_STANDARD_LOCK).saturating_sub(tip + 1);
                if left > 0 {
                    b.confirming_blocks_left = b.confirming_blocks_left.max(Some(left));
                }
            }
        }
    }
    // Pending change is not in `outputs` until scanned.
    let change: Vec<Option<String>> = db
        .conn()
        .prepare(
            "SELECT change_atomic FROM transactions
             WHERE identity_id = ?1 AND direction = 'out' AND height IS NULL AND failed = 0",
        )
        .and_then(|mut stmt| {
            stmt.query_map(params![identity_id], |r| r.get(0))?
                .collect::<rusqlite::Result<Vec<_>>>()
        })
        .map_err(|e| db_err("pendingChangeQuery", e))?;
    for value in change.into_iter().flatten() {
        if let Ok(amount) = value.parse::<u128>() {
            b.pending_change_atomic += amount;
        }
    }
    b.total_atomic += b.pending_change_atomic;
    Ok(b)
}

/// Whether an output is spendable now, as wallet2's `is_transfer_unlocked`.
///
/// `chain_tip` is the top block number. `additional_timelock` is raw: below 500M a
/// height, otherwise a Unix timestamp compared against `now`; with `None` timestamp locks stay locked.
pub fn is_unlocked(
    height: u64,
    additional_timelock: u64,
    chain_tip: u64,
    now: Option<u64>,
    network: Network,
) -> bool {
    // wallet2 compares against get_blockchain_current_height(): the chain length, i.e. tip + 1.
    let current_height = chain_tip.saturating_add(1);
    if height + COINBASE_AND_STANDARD_LOCK > current_height {
        return false;
    }
    if additional_timelock == 0 {
        return true;
    }
    if additional_timelock < MAX_BLOCK_NUMBER {
        current_height - 1 + LOCKED_TX_ALLOWED_DELTA_BLOCKS >= additional_timelock
    } else {
        // wallet2::is_tx_spendtime_unlocked: one block of leeway past the timestamp.
        now.is_some_and(|now| {
            now.saturating_add(timestamp_lock_leeway(network, height)) >= additional_timelock
        })
    }
}

/// `CRYPTONOTE_LOCKED_TX_ALLOWED_DELTA_SECONDS_V1/_V2`, picked by the output's
/// height against the v2 fork heights wallet2 hard-codes.
fn timestamp_lock_leeway(network: Network, height: u64) -> u64 {
    let v2_height = match network {
        Network::Testnet => 624_634,
        Network::Stagenet => 32_000,
        _ => 1_009_827,
    };
    if height < v2_height {
        TARGET_SECONDS_V1
    } else {
        TARGET_SECONDS_V2
    }
}

/// "Now" for timestamp locks, recomputed from the latest 60 stored block timestamps.
/// With fewer stored it is `None`: the local clock is never trusted for an unlock.
pub fn adjusted_time(db: &Db, identity_id: i64) -> Result<Option<u64>> {
    let mut timestamps: Vec<u64> = db
        .conn()
        .prepare(
            "SELECT timestamp FROM blocks WHERE identity_id = ?1 ORDER BY height DESC LIMIT ?2",
        )
        .and_then(|mut stmt| {
            stmt.query_map(params![identity_id, TIMESTAMP_CHECK_WINDOW as i64], |r| {
                r.get::<_, i64>(0)
            })?
            .map(|t| t.map(|t| t.max(0) as u64))
            .collect::<rusqlite::Result<Vec<_>>>()
        })
        .map_err(|e| db_err("adjustedTime", e))?;
    if timestamps.len() < TIMESTAMP_CHECK_WINDOW {
        return Ok(None);
    }
    Ok(Some(daemon_adjusted_time(&mut timestamps)))
}

/// monerod's `Blockchain::get_adjusted_time` over the last 60 block timestamps.
fn daemon_adjusted_time(timestamps: &mut [u64]) -> u64 {
    // epee median sorts in place, so the "top" timestamp upstream reads is the maximum.
    timestamps.sort_unstable();
    let n = timestamps.len() / 2;
    let median = if timestamps.len() % 2 == 1 {
        timestamps[n]
    } else {
        let (a, b) = (timestamps[n - 1], timestamps[n]);
        a / 2 + b / 2 + (a % 2 + b % 2) / 2
    };
    let median_ts =
        median.saturating_add((TIMESTAMP_CHECK_WINDOW as u64 + 1) * TARGET_SECONDS_V2 / 2);
    let current_ts = timestamps[timestamps.len() - 1].saturating_add(TARGET_SECONDS_V2);
    current_ts.min(median_ts)
}

/// Spendable output metadata; blobs are deserialized only when building the transaction.
#[derive(Debug, Clone)]
pub struct SpendableRow {
    pub id: i64,
    pub amount_atomic: u64,
    pub global_index: u64,
    pub height: u64,
}

/// Currently spendable outputs, largest first (fewer inputs, lower fee).
///
/// Only outputs with a key image: without one it may already be spent.
pub fn spendable_rows(db: &Db, identity_id: i64, chain_tip: u64) -> Result<Vec<SpendableRow>> {
    let now = adjusted_time(db, identity_id)?;
    let network = db.network();
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.id, o.amount_atomic, o.global_index, o.height, o.additional_timelock
             FROM outputs o
             WHERE o.identity_id = ?1
               AND o.scan_valid = 1
               AND o.spent_at_height IS NULL
               AND o.key_image IS NOT NULL
             ORDER BY o.amount_atomic DESC",
        )
        .map_err(|e| db_err("spendableRows", e))?;

    let rows = stmt
        .query_map([identity_id], |r| {
            Ok((
                r.get::<_, i64>(0)?,
                r.get::<_, i64>(1)? as u64,
                r.get::<_, i64>(2)? as u64,
                r.get::<_, i64>(3)? as u64,
                r.get::<_, i64>(4)? as u64,
            ))
        })
        .map_err(|e| db_err("spendableRows", e))?;

    let mut out = Vec::new();
    for row in rows {
        let (id, amount_atomic, global_index, height, timelock) =
            row.map_err(|e| db_err("spendableRows", e))?;
        if !is_unlocked(height, timelock, chain_tip, now, network) {
            continue;
        }
        out.push(SpendableRow {
            id,
            amount_atomic,
            global_index,
            height,
        });
    }
    Ok(out)
}

/// Deserializes an output blob into the monero-oxide object.
///
/// All cryptographic use goes through the blob; the other `outputs` columns are for filtering and display.
/// An unreadable blob errors out rather than being skipped, which would hide funds.
pub fn load_output(db: &Db, output_id: i64) -> Result<WalletOutput> {
    let blob: Vec<u8> = db
        .conn()
        .query_row("SELECT blob FROM outputs WHERE id = ?1", [output_id], |r| {
            r.get(0)
        })
        .optional()
        .map_err(|e| db_err("loadOutput", e))?
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "outputId", "value": output_id }),
            )
        })?;

    WalletOutput::read(&mut blob.as_slice()).map_err(|e| {
        db_err(
            "deserializeOutput",
            format!("cannot read blob of output {output_id}: {e}"),
        )
    })
}

/// Transaction history, paged, newest height first.
pub fn history(db: &Db, identity_id: i64, limit: u32, offset: u32) -> Result<Value> {
    let limit = limit.clamp(1, 500);

    let mut stmt = db
        .conn()
        .prepare(
            "SELECT t.txid, CASE WHEN t.scan_valid = 1 THEN t.height END, t.direction,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND txid = t.txid AND scan_valid = 1), 0) AS received,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND spent_in_txid = t.txid), 0) AS spent,
                    COALESCE(t.timestamp, b.timestamp, t.sent_at), t.failed, t.outgoing_atomic,
                    t.fee_atomic,
                    EXISTS (SELECT 1 FROM outputs
                            WHERE identity_id = t.identity_id AND txid = t.txid AND scan_valid = 1
                              AND subaddress_major IS NULL) AS received_on_primary
             FROM transactions t
             LEFT JOIN blocks b ON b.identity_id = t.identity_id AND b.height = t.height
             WHERE t.identity_id = ?1
             ORDER BY t.height DESC NULLS FIRST, t.txid
             LIMIT ?2 OFFSET ?3",
        )
        .map_err(|e| db_err("history", e))?;

    let rows = stmt
        .query_map(params![identity_id, limit, offset], |r| {
            let txid: Vec<u8> = r.get(0)?;
            Ok(json!({
                "txid": hex::encode(txid),
                "height": r.get::<_, Option<i64>>(1)?,
                "direction": r.get::<_, String>(2)?,
                "receivedAtomic": r.get::<_, i64>(3)?.to_string(),
                "spentAtomic": r.get::<_, i64>(4)?.to_string(),
                "timestamp": r.get::<_, Option<i64>>(5)?,
                "failed": r.get::<_, i64>(6)? != 0,
                "outgoingAtomic": r.get::<_, Option<String>>(7)?,
                "feeAtomic": r.get::<_, Option<i64>>(8)?.map(|fee| fee.to_string()),
                // Paid to the account's primary address (subaddresses are not shown as ours).
                "receivedOnPrimary": r.get::<_, bool>(9)?,
            }))
        })
        .map_err(|e| db_err("history", e))?;

    let items = rows
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("history", e))?;

    let total: i64 = db
        .conn()
        .query_row(
            "SELECT count(*) FROM transactions WHERE identity_id = ?1",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("historyCount", e))?;

    Ok(json!({ "items": items, "total": total, "limit": limit, "offset": offset }))
}

/// Details of a single transaction.
pub fn transaction_details(db: &Db, identity_id: i64, txid_hex: &str) -> Result<Value> {
    let txid = hex::decode(txid_hex).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "txid", "reason": "notHex" }),
        )
    })?;
    if txid.len() != 32 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "txid", "byteLen": txid.len(), "expected": 32 }),
        ));
    }

    let row = db
        .conn()
        .query_row(
            "SELECT CASE WHEN t.scan_valid = 1 THEN t.height END, t.direction, COALESCE(t.timestamp, b.timestamp, t.sent_at)
             FROM transactions t LEFT JOIN blocks b ON b.identity_id = t.identity_id AND b.height = t.height
             WHERE t.identity_id = ?1 AND t.txid = ?2",
            params![identity_id, &txid],
            |r| {
                Ok((
                    r.get::<_, Option<i64>>(0)?,
                    r.get::<_, String>(1)?,
                    r.get::<_, Option<i64>>(2)?,
                ))
            },
        )
        .optional()
        .map_err(|e| db_err("transactionDetails", e))?;

    let Some((height, direction, timestamp)) = row else {
        return Ok(Value::Null);
    };

    let mut stmt = db
        .conn()
        .prepare(
            "SELECT amount_atomic, subaddress_major, subaddress_minor, index_in_tx
             FROM outputs WHERE identity_id = ?1 AND txid = ?2 AND scan_valid = 1 ORDER BY index_in_tx",
        )
        .map_err(|e| db_err("transactionOutputs", e))?;
    let received = stmt
        .query_map(params![identity_id, &txid], |r| {
            Ok(json!({
                "amountAtomic": r.get::<_, i64>(0)?.to_string(),
                "subaddressMajor": r.get::<_, Option<i64>>(1)?,
                "subaddressMinor": r.get::<_, Option<i64>>(2)?,
                "indexInTransaction": r.get::<_, i64>(3)?,
            }))
        })
        .map_err(|e| db_err("transactionOutputs", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("transactionOutputs", e))?;

    Ok(json!({
        "txid": txid_hex,
        "height": height,
        "direction": direction,
        "timestamp": timestamp,
        "received": received,
    }))
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    fn add_output(
        db: &Db,
        identity_id: i64,
        amount: i64,
        height: i64,
        with_key_image: bool,
    ) -> i64 {
        let ki: Option<Vec<u8>> = with_key_image.then(|| {
            let mut k = vec![0u8; 32];
            k[0] = (amount % 251) as u8;
            k[1] = (height % 251) as u8;
            k
        });
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image)
                 VALUES (?1, x'00', ?2, ?3, 0, ?4, ?5, ?6)",
                params![
                    identity_id,
                    &vec![height as u8; 32],
                    height,
                    amount,
                    height,
                    ki
                ],
            )
            .unwrap();
        db.conn().last_insert_rowid()
    }

    const MAIN: Network = Network::Mainnet;

    #[test]
    fn the_ten_block_lock_is_enforced() {
        assert!(
            !is_unlocked(100, 0, 100, None, MAIN),
            "locked at the same height"
        );
        assert!(
            !is_unlocked(100, 0, 108, None, MAIN),
            "locked one block before wallet2's unlock height"
        );
        assert!(
            is_unlocked(100, 0, 109, None, MAIN),
            "unlocked once the chain length reaches height + 10"
        );
        assert!(is_unlocked(100, 0, 110, None, MAIN));
    }

    #[test]
    fn additional_block_timelock_is_honoured() {
        assert!(!is_unlocked(100, 200, 150, None, MAIN));
        assert!(
            !is_unlocked(100, 200, 198, None, MAIN),
            "locked one block before wallet2's unlock height"
        );
        assert!(
            is_unlocked(100, 200, 199, None, MAIN),
            "unlocked at wallet2's unlock height"
        );
        assert!(is_unlocked(100, 200, 200, None, MAIN));
        assert!(!is_unlocked(100, 200, 198, Some(u64::MAX), MAIN));
        assert!(!is_unlocked(100, 50, 108, None, MAIN));
        assert!(is_unlocked(100, 50, 109, None, MAIN));
    }

    /// `now + leeway >= unlock_time`, with a 120 s leeway after the v2 fork and 60 s before it.
    #[test]
    fn timestamp_timelocks_unlock_at_the_leeway_boundary() {
        let t = 1_700_000_000;
        let h = 3_000_000;
        assert!(
            !is_unlocked(h, t, h + 10, Some(t - 121), MAIN),
            "before expiry"
        );
        assert!(
            is_unlocked(h, t, h + 10, Some(t - 120), MAIN),
            "at the tolerance boundary"
        );
        assert!(is_unlocked(h, t, h + 10, Some(t + 1), MAIN), "after expiry");
        assert!(
            !is_unlocked(500, t, 510, Some(t - 61), MAIN),
            "v1 era leeway is 60 s"
        );
        assert!(is_unlocked(500, t, 510, Some(t - 60), MAIN));
        assert!(!is_unlocked(
            31_999,
            t,
            32_009,
            Some(t - 61),
            Network::Stagenet
        ));
        assert!(is_unlocked(
            32_000,
            t,
            32_010,
            Some(t - 120),
            Network::Stagenet
        ));
        assert!(is_unlocked(
            624_634,
            t,
            624_644,
            Some(t - 120),
            Network::Testnet
        ));
        assert!(!is_unlocked(h, t, h + 8, Some(t + 1), MAIN));
        assert!(is_unlocked(h, t, h + 9, Some(t + 1), MAIN));
        assert!(!is_unlocked(h, t, h + 9, Some(t - 121), MAIN));
        assert!(is_unlocked(h, t, h + 9, Some(t - 120), MAIN));
        assert!(!is_unlocked(h, 500_000_000, h + 10, None, MAIN));
    }

    /// Matches monerod's `get_adjusted_time`: min(max ts + 120, median + 3660).
    #[test]
    fn adjusted_time_follows_monerod() {
        let mut regular: Vec<u64> = (0..60).map(|i| 1_000_000 + i * 120).collect();
        assert_eq!(
            daemon_adjusted_time(&mut regular),
            1_000_000 + 59 * 120 + 120
        );
        let mut outlier: Vec<u64> = (0..60).map(|i| 1_000_000 + i * 120).collect();
        outlier[59] = 9_000_000;
        let median = (1_000_000 + 29 * 120) / 2 + (1_000_000 + 30 * 120) / 2;
        assert_eq!(daemon_adjusted_time(&mut outlier), median + 3_660);
    }

    fn add_block(db: &Db, identity_id: i64, height: u64, timestamp: u64) {
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                 VALUES (?1, ?2, ?3, NULL, ?4)",
                params![
                    identity_id,
                    height as i64,
                    vec![(height % 251) as u8; 32],
                    timestamp as i64
                ],
            )
            .unwrap();
    }

    /// Stores `count` blocks from height 1_000 at 120 s spacing, ending at `last_timestamp`.
    fn add_blocks(db: &Db, identity_id: i64, count: u64, last_timestamp: u64) {
        for i in 0..count {
            add_block(
                db,
                identity_id,
                1_000 + i,
                last_timestamp - (count - 1 - i) * 120,
            );
        }
    }

    #[test]
    fn adjusted_time_needs_sixty_stored_blocks() {
        let (db, id) = db_with_identity();
        add_blocks(&db, id, 59, 1_000_000);
        assert_eq!(adjusted_time(&db, id).unwrap(), None);
        add_block(&db, id, 1_059, 1_000_120);
        assert_eq!(adjusted_time(&db, id).unwrap(), Some(1_000_240));
    }

    #[test]
    fn timestamp_locked_outputs_stay_locked_without_chain_time() {
        let (db, id) = db_with_identity();
        let timestamp_locked = add_output(&db, id, 800, 1_000, true);
        let height_locked = add_output(&db, id, 50, 1_001, true);
        add_blocks(&db, id, 59, 2_000_000_000);
        db.conn()
            .execute(
                "UPDATE outputs SET additional_timelock = ?1 WHERE id = ?2",
                params![500_000_000i64, timestamp_locked],
            )
            .unwrap();
        db.conn()
            .execute(
                "UPDATE outputs SET additional_timelock = ?1 WHERE id = ?2",
                params![1_050i64, height_locked],
            )
            .unwrap();
        assert_eq!(balance(&db, id, Some(1_100)).unwrap().unlocked_atomic, 50);
        let rows = spendable_rows(&db, id, 1_100).unwrap();
        assert_eq!(rows.len(), 1);
        assert_eq!(rows[0].id, height_locked);

        add_block(&db, id, 1_059, 2_000_000_120);
        assert_eq!(balance(&db, id, Some(1_100)).unwrap().unlocked_atomic, 850);
        assert_eq!(spendable_rows(&db, id, 1_100).unwrap().len(), 2);
    }

    #[test]
    fn reports_how_many_blocks_the_youngest_output_still_waits() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 100, 1_000, true);
        add_output(&db, id, 200, 1_095, true);
        add_output(&db, id, 300, 1_098, true);
        // Chain length 1_101: the 1_095 output needs 4 more blocks, the 1_098 one 7.
        let b = balance(&db, id, Some(1_100)).unwrap();
        assert_eq!(b.unlocked_atomic, 100);
        assert_eq!(b.confirming_blocks_left, Some(7));
        assert_eq!(b.to_json()["confirmingBlocksLeft"], 7);

        let settled = balance(&db, id, Some(1_200)).unwrap();
        assert_eq!(settled.confirming_blocks_left, None);
        assert!(settled.to_json()["confirmingBlocksLeft"].is_null());
    }

    #[test]
    fn timestamp_locked_outputs_unlock_in_balance_and_selection() {
        let (db, id) = db_with_identity();
        let o = add_output(&db, id, 800, 1_000, true);
        add_blocks(&db, id, 60, 2_000_000_000);
        // Adjusted time = 2_000_000_120; leeway 60 s (height 1_000 predates v2)
        db.conn()
            .execute(
                "UPDATE outputs SET additional_timelock = ?1 WHERE id = ?2",
                params![2_000_000_181i64, o],
            )
            .unwrap();
        assert_eq!(balance(&db, id, Some(1_100)).unwrap().unlocked_atomic, 0);
        assert!(spendable_rows(&db, id, 1_100).unwrap().is_empty());

        db.conn()
            .execute(
                "UPDATE outputs SET additional_timelock = ?1 WHERE id = ?2",
                params![2_000_000_180i64, o],
            )
            .unwrap();
        assert_eq!(balance(&db, id, Some(1_100)).unwrap().unlocked_atomic, 800);
        assert_eq!(spendable_rows(&db, id, 1_100).unwrap().len(), 1);
    }

    #[test]
    fn unknown_chain_tip_means_nothing_is_spendable() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 1_000, 10, true);
        let b = balance(&db, id, None).unwrap();
        assert_eq!(b.total_atomic, 1_000);
        assert_eq!(b.unlocked_atomic, 0);
    }

    #[test]
    fn only_outputs_seen_in_a_ring_are_pending_verification() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 700, 10, true);
        add_output(&db, id, 300, 11, false);
        add_output(&db, id, 200, 12, false);
        db.conn()
            .execute(
                "INSERT INTO possible_spends
                   (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                 VALUES (?1, ?2, 0, ?3, ?4, 20, 0)",
                params![id, vec![12u8; 32], vec![9u8; 32], vec![8u8; 32]],
            )
            .unwrap();
        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 1_200);
        assert_eq!(b.unverifiable_atomic, 200);
        assert_eq!(
            b.unlocked_atomic, 1_000,
            "possibly spent outputs are not unlocked"
        );
    }

    #[test]
    fn spent_outputs_leave_the_balance() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        db.conn()
            .execute("UPDATE outputs SET spent_at_height = 20 WHERE id = ?1", [a])
            .unwrap();
        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 0);
    }

    #[test]
    fn a_pending_send_reports_its_broadcast_time_amount_and_failure() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO transactions
                   (identity_id, txid, height, direction, sent_at, failed, outgoing_atomic)
                 VALUES (?1, ?2, NULL, 'out', 1700000000, 1, '1234')",
                params![id, vec![5u8; 32]],
            )
            .unwrap();
        let item = &history(&db, id, 10, 0).unwrap()["items"][0];
        assert_eq!(item["timestamp"], 1_700_000_000);
        assert_eq!(item["failed"], true);
        assert_eq!(item["outgoingAtomic"], "1234");
    }

    #[test]
    fn a_receive_reports_its_fee_and_whether_it_reached_the_primary_address() {
        let (db, id) = db_with_identity();
        for (txid, subaddress) in [([6u8; 32], None), ([7u8; 32], Some(1i64))] {
            db.conn()
                .execute(
                    "INSERT INTO transactions (identity_id, txid, height, direction, fee_atomic)
                     VALUES (?1, ?2, 100, 'in', 30000000)",
                    params![id, txid.as_slice()],
                )
                .unwrap();
            db.conn()
                .execute(
                    "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                          amount_atomic, height, subaddress_major, subaddress_minor)
                     VALUES (?1, x'00', ?2, ?3, 0, 700, 100, ?4, ?4)",
                    params![id, txid.as_slice(), txid[0] as i64, subaddress],
                )
                .unwrap();
        }
        let items = &history(&db, id, 10, 0).unwrap()["items"];
        let by_txid = |txid: [u8; 32]| {
            items
                .as_array()
                .unwrap()
                .iter()
                .find(|item| item["txid"] == hex::encode(txid))
                .unwrap()
                .clone()
        };
        let primary = by_txid([6u8; 32]);
        assert_eq!(primary["feeAtomic"], "30000000");
        assert_eq!(primary["receivedOnPrimary"], true);
        assert_eq!(by_txid([7u8; 32])["receivedOnPrimary"], false);
    }

    #[test]
    fn history_limit_is_clamped() {
        let (db, id) = db_with_identity();
        let v = history(&db, id, 99_999, 0).unwrap();
        assert_eq!(v["limit"], 500);
        let v = history(&db, id, 0, 0).unwrap();
        assert_eq!(v["limit"], 1);
    }

    #[test]
    fn transaction_details_rejects_malformed_txids() {
        let (db, id) = db_with_identity();
        let e = transaction_details(&db, id, "zzzz").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        let e = transaction_details(&db, id, "00ff").unwrap_err();
        assert_eq!(e.params["byteLen"], 2);
    }

    #[test]
    fn unknown_transaction_returns_null_not_an_error() {
        let (db, id) = db_with_identity();
        let v = transaction_details(&db, id, &"ab".repeat(32)).unwrap();
        assert!(v.is_null());
    }
}
