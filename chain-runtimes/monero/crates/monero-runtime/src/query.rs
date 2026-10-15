//! Balance, history and spendable-output queries.
//!
//! Paging, height filters and per-subaddress aggregation are indexed SQLite
//! queries, so long histories are never loaded into memory.

use monero_wallet::WalletOutput;
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::wallet::Db;

/// Consensus lock on **all** outputs: 10 confirmations.
///
/// Younger outputs cannot be used in a ring; nodes reject them.
/// `additional_timelock` applies on top of this.
pub const COINBASE_AND_STANDARD_LOCK: u64 = 10;

#[derive(Debug, Default, Clone)]
pub struct Balance {
    /// Sum of all unspent outputs.
    pub total_atomic: u128,
    /// Spendable now: past the 10-block lock and any extra timelock, and not reserved.
    pub unlocked_atomic: u128,
    /// Reserved by in-flight sends. Counted in total, not selectable.
    pub locked_by_reservation_atomic: u128,
    /// Outputs without a key image that appear in some ring. **May already be
    /// spent** until the host supplies key images; outputs never seen in a ring
    /// are certainly unspent and excluded.
    pub unverifiable_atomic: u128,
    /// Change from own unconfirmed sends. Counted in total but locked, as in wallet2.
    pub pending_change_atomic: u128,
    /// Unspent outputs lacking a key image. If > 0, compute key images before
    /// sending (selection only uses outputs with one).
    pub outputs_without_key_image: u64,
}

impl Balance {
    pub fn to_json(&self) -> Value {
        // Amounts cross the boundary as strings: piconero values exceed JS
        // Number.MAX_SAFE_INTEGER.
        json!({
            "totalAtomic": self.total_atomic.to_string(),
            "unlockedAtomic": self.unlocked_atomic.to_string(),
            "lockedByReservationAtomic": self.locked_by_reservation_atomic.to_string(),
            "unverifiableAtomic": self.unverifiable_atomic.to_string(),
            "pendingChangeAtomic": self.pending_change_atomic.to_string(),
            "outputsWithoutKeyImage": self.outputs_without_key_image,
        })
    }
}

/// Computes the balance. `chain_tip` drives the lock check; `None` (tip unknown)
/// treats everything as locked rather than offer funds that cannot be spent.
pub fn balance(db: &Db, identity_id: i64, chain_tip: Option<u64>) -> Result<Balance> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.amount_atomic, o.height, o.additional_timelock,
                    o.key_image IS NULL AS no_ki,
                    l.output_id IS NOT NULL AS reserved,
                    EXISTS (SELECT 1 FROM possible_spends p
                            WHERE p.identity_id = o.identity_id
                              AND p.out_txid = o.txid AND p.out_index = o.index_in_tx) AS maybe_spent
             FROM outputs o
             LEFT JOIN locked_outputs l ON l.output_id = o.id AND (l.locked_at > ?2 OR l.signed_at > ?3)
             WHERE o.identity_id = ?1 AND o.spent_at_height IS NULL",
        )
        .map_err(|e| db_err("balanceQuery", e))?;

    let rows = stmt
        .query_map(
            params![
                identity_id,
                crate::spend::reservation_cutoff(),
                crate::spend::signed_reservation_cutoff()
            ],
            |r| {
                Ok((
                    r.get::<_, i64>(0)? as u128,
                    r.get::<_, i64>(1)? as u64,
                    r.get::<_, i64>(2)? as u64,
                    r.get::<_, bool>(3)?,
                    r.get::<_, bool>(4)?,
                    r.get::<_, bool>(5)?,
                ))
            },
        )
        .map_err(|e| db_err("balanceQuery", e))?;

    let mut b = Balance::default();
    for row in rows {
        let (amount, height, timelock, no_key_image, reserved, maybe_spent) =
            row.map_err(|e| db_err("balanceQuery", e))?;
        b.total_atomic += amount;
        if no_key_image {
            b.outputs_without_key_image += 1;
        }
        // Only outputs seen in a ring and lacking a key image are unverifiable;
        // they may be spent, so they are not counted as unlocked.
        if no_key_image && maybe_spent {
            b.unverifiable_atomic += amount;
            continue;
        }
        if reserved {
            b.locked_by_reservation_atomic += amount;
            continue;
        }
        if let Some(tip) = chain_tip {
            if is_unlocked(height, timelock, tip) {
                b.unlocked_atomic += amount;
            }
        }
    }
    // Pending change is not in `outputs` until scanned, but belongs to the user.
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

/// Whether an output is spendable now.
///
/// Requires both the 10-block lock and any additional timelock to have passed.
/// `additional_timelock` is raw: below 500M it is a height, otherwise a Unix
/// timestamp (`CRYPTONOTE_MAX_BLOCK_NUMBER`).
pub fn is_unlocked(height: u64, additional_timelock: u64, chain_tip: u64) -> bool {
    const MAX_BLOCK_NUMBER: u64 = 500_000_000;

    if chain_tip < height + COINBASE_AND_STANDARD_LOCK {
        return false;
    }
    if additional_timelock == 0 {
        return true;
    }
    if additional_timelock < MAX_BLOCK_NUMBER {
        chain_tip >= additional_timelock
    } else {
        // Timestamp locks cannot be judged from height alone; treat as locked.
        false
    }
}

/// Spendable output **metadata**, without the deserialized crypto object.
///
/// Enough for selection and display; blobs are deserialized only when building
/// the transaction.
#[derive(Debug, Clone)]
pub struct SpendableRow {
    pub id: i64,
    pub amount_atomic: u64,
    pub global_index: u64,
    pub height: u64,
}

/// Currently spendable outputs, largest first (fewer inputs, lower fee).
///
/// Only outputs **with a key image**: without one we cannot tell whether it is
/// already spent, and the node would reject a double spend.
pub fn spendable_rows(db: &Db, identity_id: i64, chain_tip: u64) -> Result<Vec<SpendableRow>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.id, o.amount_atomic, o.global_index, o.height, o.additional_timelock
             FROM outputs o
             LEFT JOIN locked_outputs l ON l.output_id = o.id AND (l.locked_at > ?2 OR l.signed_at > ?3)
             WHERE o.identity_id = ?1
               AND o.spent_at_height IS NULL
               AND o.key_image IS NOT NULL
               AND l.output_id IS NULL
             ORDER BY o.amount_atomic DESC",
        )
        .map_err(|e| db_err("spendableRows", e))?;

    let rows = stmt
        .query_map(
            params![
                identity_id,
                crate::spend::reservation_cutoff(),
                crate::spend::signed_reservation_cutoff()
            ],
            |r| {
                Ok((
                    r.get::<_, i64>(0)?,
                    r.get::<_, i64>(1)? as u64,
                    r.get::<_, i64>(2)? as u64,
                    r.get::<_, i64>(3)? as u64,
                    r.get::<_, i64>(4)? as u64,
                ))
            },
        )
        .map_err(|e| db_err("spendableRows", e))?;

    let mut out = Vec::new();
    for row in rows {
        let (id, amount_atomic, global_index, height, timelock) =
            row.map_err(|e| db_err("spendableRows", e))?;
        if !is_unlocked(height, timelock, chain_tip) {
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
/// **All cryptographic use goes through here.** The extracted columns on
/// `outputs` are for filtering and display only, not a second source of truth.
///
/// An unreadable blob means a corrupt database; error out rather than skip,
/// which would make funds silently vanish.
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
    // Cap the page size so a huge limit cannot load the whole history.
    let limit = limit.clamp(1, 500);

    let mut stmt = db
        .conn()
        .prepare(
            "SELECT t.txid, t.height, t.direction,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND txid = t.txid), 0) AS received,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND spent_in_txid = t.txid), 0) AS spent,
                    COALESCE(t.timestamp, b.timestamp, t.sent_at), t.failed, t.outgoing_atomic
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
            "SELECT t.height, t.direction, COALESCE(t.timestamp, b.timestamp, t.sent_at)
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
             FROM outputs WHERE identity_id = ?1 AND txid = ?2 ORDER BY index_in_tx",
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
            // Distinct key image per output so uniqueness issues do not mask bugs
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

    /// The 10-block lock is consensus: younger outputs cannot be used in a ring.
    #[test]
    fn the_ten_block_lock_is_enforced() {
        assert!(!is_unlocked(100, 0, 100), "locked at the same height");
        assert!(!is_unlocked(100, 0, 109), "locked one block short");
        assert!(is_unlocked(100, 0, 110), "unlocked after 10 blocks");
    }

    #[test]
    fn additional_block_timelock_is_honoured() {
        // Extra lock to height 200: still locked after the 10-block lock
        assert!(!is_unlocked(100, 200, 150));
        assert!(is_unlocked(100, 200, 200));
    }

    /// Timestamp timelocks cannot be judged from height; treat as locked.
    #[test]
    fn timestamp_timelocks_are_treated_as_locked() {
        assert!(!is_unlocked(100, 1_700_000_000, 999_999));
    }

    /// With an unknown chain tip the unlocked balance must be 0.
    #[test]
    fn unknown_chain_tip_means_nothing_is_spendable() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 1_000, 10, true);
        let b = balance(&db, id, None).unwrap();
        assert_eq!(b.total_atomic, 1_000);
        assert_eq!(b.unlocked_atomic, 0);
    }

    /// Outputs without a key image seen in a ring **may be spent** and are
    /// reported separately; never-ringed ones are certainly unspent (spending
    /// requires a ring).
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

    /// Reserved outputs count toward total but are not selectable, so two
    /// sends cannot spend the same output.
    #[test]
    fn reserved_outputs_are_excluded_from_unlocked_and_from_selection() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        add_output(&db, id, 400, 11, true);
        db.conn()
            .execute(
                "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, 'r', ?2)",
                params![a, crate::wallet::now_secs()],
            )
            .unwrap();

        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 900);
        assert_eq!(b.locked_by_reservation_atomic, 500);
        assert_eq!(b.unlocked_atomic, 400);

        let spendable = spendable_rows(&db, id, 100).unwrap();
        assert_eq!(spendable.len(), 1, "reserved outputs must not be selected");
        assert_eq!(spendable[0].amount_atomic, 400);
    }

    /// Abandoning the confirm screen does not notify the runtime; expired reservations must release funds.
    #[test]
    fn an_expired_reservation_no_longer_holds_its_outputs() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        db.conn()
            .execute(
                "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, 'r', ?2)",
                params![a, crate::spend::reservation_cutoff() - 1],
            )
            .unwrap();

        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.locked_by_reservation_atomic, 0);
        assert_eq!(b.unlocked_atomic, 500);
        assert_eq!(spendable_rows(&db, id, 100).unwrap().len(), 1);
    }

    /// Spent outputs drop out of every balance figure.
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

    /// An unconfirmed own send reports its broadcast time (not "now"), the
    /// recorded amount, and failure.
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

    /// History page size has a hard cap.
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
