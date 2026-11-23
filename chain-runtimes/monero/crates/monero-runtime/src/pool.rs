//! Liveness of our own broadcast, unconfirmed transactions.
//!
//! Like wallet2 `process_unconfirmed_transfer`: a send still unknown to the node
//! [`TX_PROPAGATION_TIMEOUT_SECS`] after broadcast is marked failed and its inputs
//! are released, otherwise those funds stay locked.
//!
//! Confirmed sends are handled by scanning via key images, which sets the height
//! and clears the failed flag.

use monero_wallet::transaction::{Input, Transaction};
use rusqlite::params;
use serde_json::Value;

use crate::error::{db_err, ErrorCode, Result};
use crate::network::Daemon;
use crate::wallet::{now_secs, Db};

/// Same as wallet2. Shorter than Dandelion++ propagation would fail fresh sends.
pub const TX_PROPAGATION_TIMEOUT_SECS: i64 = 500;

/// Minimum interval between node checks of the same pending send; sync steps run every few seconds.
const POOL_RECHECK_SECS: i64 = 60;

const RESPONSE_SIZE_LIMIT: usize = 4 * 1024 * 1024;

/// Own unconfirmed sends, including previously failed ones, due for the normal pool refresh.
fn due_pending(db: &Db, identity_id: i64, now: i64) -> Result<Vec<[u8; 32]>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT txid FROM transactions
             WHERE identity_id = ?1 AND height IS NULL AND direction = 'out'
               AND sent_at IS NOT NULL AND sent_at + ?2 <= ?3
               AND (pool_checked_at IS NULL OR pool_checked_at + ?4 <= ?3)",
        )
        .map_err(|e| db_err("duePending", e))?;
    let rows = stmt
        .query_map(
            params![
                identity_id,
                TX_PROPAGATION_TIMEOUT_SECS,
                now,
                POOL_RECHECK_SECS
            ],
            |r| r.get::<_, Vec<u8>>(0),
        )
        .map_err(|e| db_err("duePending", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("duePending", e))?;
    Ok(rows
        .into_iter()
        .filter_map(|v| <[u8; 32]>::try_from(v.as_slice()).ok())
        .collect())
}

struct PooledTransaction {
    txid: [u8; 32],
    key_images: Vec<[u8; 32]>,
}

struct PoolStatus {
    missed: Vec<[u8; 32]>,
    pooled: Vec<PooledTransaction>,
}

// Validate transaction identities before applying the node's pool observations.
fn read_pool_status(response: &str) -> Option<PoolStatus> {
    let value: Value = serde_json::from_str(response).ok()?;
    if value.get("status")?.as_str()? != "OK" {
        return None;
    }
    let hash = |value: &Value| {
        let bytes = hex::decode(value.as_str()?).ok()?;
        <[u8; 32]>::try_from(bytes.as_slice()).ok()
    };
    let mut missed = Vec::new();
    if let Some(items) = value.get("missed_tx") {
        for item in items.as_array()? {
            missed.push(hash(item)?);
        }
    }
    let mut pooled = Vec::new();
    if let Some(items) = value.get("txs") {
        for item in items.as_array()? {
            // Mined transactions are reconciled by the block scanner.
            if item.get("in_pool").and_then(Value::as_bool) != Some(true) {
                continue;
            }
            let txid = hash(item.get("tx_hash")?)?;
            let raw = hex::decode(item.get("as_hex")?.as_str()?).ok()?;
            let mut remaining = raw.as_slice();
            let tx = Transaction::read(&mut remaining).ok()?;
            if !remaining.is_empty() || tx.hash() != txid || missed.contains(&txid) {
                return None;
            }
            let key_images = tx
                .prefix()
                .inputs
                .iter()
                .filter_map(|input| match input {
                    Input::ToKey { key_image, .. } => Some(key_image.to_bytes()),
                    _ => None,
                })
                .collect::<Vec<_>>();
            if key_images.is_empty() {
                return None;
            }
            pooled.push(PooledTransaction { txid, key_images });
        }
    }
    Some(PoolStatus { missed, pooled })
}

fn mark_checked(db: &Db, identity_id: i64, txids: &[[u8; 32]], now: i64) -> Result<()> {
    for txid in txids {
        db.conn()
            .execute(
                "UPDATE transactions SET pool_checked_at = ?3 WHERE identity_id = ?1 AND txid = ?2",
                params![identity_id, txid.as_slice(), now],
            )
            .map_err(|e| db_err("markPoolChecked", e))?;
    }
    Ok(())
}

/// Mark a send failed: keep the transaction (shown as failed) and release its inputs.
///
/// `spent_in_txid` is kept to compute the failed transaction's amount; a later
/// spend of the output overwrites it.
pub(crate) fn mark_failed(db: &Db, identity_id: i64, txid: &[u8; 32]) -> Result<bool> {
    let tx = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginMarkFailed", e))?;
    let changed = tx
        .execute(
            "UPDATE transactions SET failed = 1
         WHERE identity_id = ?1 AND txid = ?2 AND height IS NULL AND failed = 0",
            params![identity_id, txid.as_slice()],
        )
        .map_err(|e| db_err("markSendFailed", e))?;
    if changed == 0 {
        return Ok(false);
    }
    tx.execute(
        "UPDATE outputs SET spent_at_height = NULL
         WHERE identity_id = ?1 AND spent_in_txid = ?2",
        params![identity_id, txid.as_slice()],
    )
    .map_err(|e| db_err("releaseFailedInputs", e))?;
    tx.commit().map_err(|e| db_err("commitMarkFailed", e))?;
    Ok(true)
}

// Restore the existing record and its inputs atomically. A later local spend
// retains ownership until normal scanning resolves the conflict.
fn restore_pending(db: &Db, identity_id: i64, pooled: &PooledTransaction) -> Result<bool> {
    let tx = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginRestorePending", e))?;
    let exists: bool = tx
        .query_row(
            "SELECT EXISTS(SELECT 1 FROM transactions
         WHERE identity_id = ?1 AND txid = ?2 AND height IS NULL AND sent_at IS NOT NULL)",
            params![identity_id, pooled.txid.as_slice()],
            |row| row.get(0),
        )
        .map_err(|e| db_err("findPendingSend", e))?;
    if !exists {
        return Ok(false);
    }
    if let Err(error) =
        crate::spend::check_inputs_owned(&tx, identity_id, &pooled.txid, &pooled.key_images)
    {
        if error.code == ErrorCode::InvalidRequest {
            return Ok(false);
        }
        return Err(error);
    }
    let mut changed = tx
        .execute(
            "UPDATE transactions SET failed = 0
         WHERE identity_id = ?1 AND txid = ?2 AND failed <> 0",
            params![identity_id, pooled.txid.as_slice()],
        )
        .map_err(|e| db_err("restorePendingSend", e))?;
    for key_image in &pooled.key_images {
        changed += tx
            .execute(
                "UPDATE outputs SET spent_at_height = 0, spent_in_txid = ?2
             WHERE identity_id = ?1 AND key_image = ?3 AND spent_at_height IS NULL",
                params![identity_id, pooled.txid.as_slice(), key_image.as_slice()],
            )
            .map_err(|e| db_err("restorePendingInput", e))?;
    }
    tx.commit().map_err(|e| db_err("commitRestorePending", e))?;
    Ok(changed > 0)
}

fn apply_pool_response(
    db: &Db,
    identity_id: i64,
    due: &[[u8; 32]],
    response: &str,
    now: i64,
) -> Result<(usize, usize)> {
    let Some(status) = read_pool_status(response) else {
        return Ok((0, 0));
    };
    let mut failed = 0;
    let mut restored = 0;
    for txid in status.missed.iter().filter(|txid| due.contains(txid)) {
        failed += usize::from(mark_failed(db, identity_id, txid)?);
    }
    for pooled in status.pooled.iter().filter(|tx| due.contains(&tx.txid)) {
        restored += usize::from(restore_pending(db, identity_id, pooled)?);
    }
    mark_checked(db, identity_id, due, now)?;
    Ok((failed, restored))
}

/// Refresh own unconfirmed sends, returning (newly failed, restored).
/// Unreachable nodes and malformed responses leave local transaction data unchanged.
pub async fn check_pending(db: &Db, daemon: &Daemon, identity_id: i64) -> Result<(usize, usize)> {
    let now = now_secs();
    let due = due_pending(db, identity_id, now)?;
    if due.is_empty() {
        return Ok((0, 0));
    }
    let hashes: Vec<String> = due.iter().map(hex::encode).collect();
    // Full transactions let the upstream parser verify txids and recover the
    // exact key images without adding another persistent input index.
    let request =
        serde_json::json!({ "txs_hashes": hashes, "prune": false, "split": false }).to_string();
    let Ok(response) = daemon
        .rpc_call("get_transactions", Some(request), RESPONSE_SIZE_LIMIT)
        .await
    else {
        return Ok((0, 0));
    };
    apply_pool_response(db, identity_id, &due, &response, now)
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    fn add_pending_send(db: &Db, id: i64, txid: [u8; 32], sent_at: i64) -> i64 {
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3, 500, ?4)",
                params![
                    id,
                    [txid[0] ^ 0xFF; 32].as_slice(),
                    [txid[0]; 32].as_slice(),
                    txid.as_slice()
                ],
            )
            .unwrap();
        let output = db.conn().last_insert_rowid();
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction, sent_at)
                 VALUES (?1, ?2, NULL, 'out', ?3)",
                params![id, txid.as_slice(), sent_at],
            )
            .unwrap();
        output
    }

    #[test]
    fn only_sends_past_the_propagation_timeout_are_asked_about() {
        let (db, id) = db_with_identity();
        let now = 10_000;
        add_pending_send(&db, id, [2u8; 32], now - TX_PROPAGATION_TIMEOUT_SECS - 1);
        add_pending_send(&db, id, [3u8; 32], now - 10);

        assert_eq!(due_pending(&db, id, now).unwrap(), vec![[2u8; 32]]);

        mark_checked(&db, id, &[[2u8; 32]], now).unwrap();
        assert!(due_pending(&db, id, now + 1).unwrap().is_empty(), "recent");
        assert_eq!(
            due_pending(&db, id, now + POOL_RECHECK_SECS).unwrap(),
            vec![[2u8; 32]]
        );
    }

    #[test]
    fn a_send_the_node_does_not_know_releases_its_inputs() {
        let (db, id) = db_with_identity();
        let txid = [2u8; 32];
        let output = add_pending_send(&db, id, txid, 0);

        mark_failed(&db, id, &txid).unwrap();

        let (failed, height): (i64, Option<i64>) = db
            .conn()
            .query_row(
                "SELECT failed, height FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((failed, height), (1, None));
        let spent_at: Option<i64> = db
            .conn()
            .query_row(
                "SELECT spent_at_height FROM outputs WHERE id = ?1",
                [output],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(spent_at, None, "output released");
        assert_eq!(
            due_pending(&db, id, i64::MAX / 2).unwrap(),
            vec![txid],
            "failed sends remain eligible for the normal pool refresh"
        );
    }

    #[test]
    fn only_a_clear_answer_from_the_node_counts() {
        let tx = hex::encode([2u8; 32]);
        assert_eq!(
            read_pool_status(&format!(r#"{{"status":"OK","missed_tx":["{tx}"]}}"#))
                .unwrap()
                .missed,
            vec![[2u8; 32]]
        );
        for response in [
            r#"{"status":"BUSY","missed_tx":[]}"#,
            "not json",
            r#"{"status":"OK","missed_tx":["zz"]}"#,
            r#"{"status":"OK","txs":[{"in_pool":true}]}"#,
        ] {
            assert!(read_pool_status(response).is_none());
        }
    }

    fn pooled_send() -> (Db, i64, [u8; 32], String) {
        use curve25519_dalek::constants::ED25519_BASEPOINT_POINT;
        use monero_wallet::transaction::{Timelock, TransactionPrefix};

        let tx = Transaction::V2 {
            prefix: TransactionPrefix {
                additional_timelock: Timelock::None,
                inputs: vec![Input::ToKey {
                    amount: None,
                    key_offsets: vec![1],
                    key_image: ED25519_BASEPOINT_POINT.compress().to_bytes().into(),
                }],
                outputs: vec![],
                extra: vec![],
            },
            proofs: None,
        };
        let txid = tx.hash();
        let (db, id) = db_with_identity();
        let output = add_pending_send(&db, id, txid, 0);
        db.conn()
            .execute(
                "UPDATE outputs SET key_image = ?1 WHERE id = ?2",
                params![ED25519_BASEPOINT_POINT.compress().as_bytes(), output],
            )
            .unwrap();
        db.conn()
            .execute(
                "UPDATE transactions SET change_atomic = '35' WHERE txid = ?1",
                [txid.as_slice()],
            )
            .unwrap();
        let response = serde_json::json!({ "status": "OK", "txs": [{
            "tx_hash": hex::encode(txid), "in_pool": true, "as_hex": hex::encode(tx.serialize()),
        }] })
        .to_string();
        (db, id, txid, response)
    }

    fn failed(db: &Db, txid: &[u8; 32]) -> bool {
        db.conn()
            .query_row(
                "SELECT failed FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |row| row.get(0),
            )
            .unwrap()
    }

    #[test]
    fn a_failed_send_seen_again_restores_inputs_and_pending_change_together() {
        let (db, id, txid, response) = pooled_send();
        mark_failed(&db, id, &txid).unwrap();
        assert_eq!(
            crate::query::balance(&db, id, Some(1000))
                .unwrap()
                .total_atomic,
            700
        );
        assert_eq!(
            apply_pool_response(&db, id, &[txid], &response, 10_000).unwrap(),
            (0, 1)
        );
        assert!(!failed(&db, &txid));
        let balance = crate::query::balance(&db, id, Some(1000)).unwrap();
        assert_eq!((balance.total_atomic, balance.unlocked_atomic), (35, 0));
        assert_eq!(balance.pending_change_atomic, 35);
        assert_eq!(
            apply_pool_response(&db, id, &[txid], &response, 10_060).unwrap(),
            (0, 0)
        );
    }

    #[test]
    fn pool_refresh_does_not_overwrite_a_later_input_owner() {
        let (db, id, txid, response) = pooled_send();
        mark_failed(&db, id, &txid).unwrap();
        db.conn()
            .execute(
                "UPDATE outputs SET spent_at_height = 900, spent_in_txid = ?1",
                [[9u8; 32].as_slice()],
            )
            .unwrap();
        assert_eq!(
            apply_pool_response(&db, id, &[txid], &response, 10_000).unwrap(),
            (0, 0)
        );
        assert!(failed(&db, &txid));
        let owner: Vec<u8> = db
            .conn()
            .query_row("SELECT spent_in_txid FROM outputs", [], |row| row.get(0))
            .unwrap();
        assert_eq!(owner, [9u8; 32]);
    }

    #[test]
    fn malformed_or_unrequested_pool_transactions_do_not_restore_a_send() {
        let (db, id, txid, response) = pooled_send();
        mark_failed(&db, id, &txid).unwrap();
        let mut value: Value = serde_json::from_str(&response).unwrap();
        value["txs"][0]["tx_hash"] = serde_json::json!(hex::encode([9u8; 32]));
        assert_eq!(
            apply_pool_response(&db, id, &[txid], &value.to_string(), 10_000).unwrap(),
            (0, 0)
        );
        assert!(failed(&db, &txid));
        assert_eq!(
            apply_pool_response(&db, id, &[[8u8; 32]], &response, 10_000).unwrap(),
            (0, 0)
        );
        assert!(failed(&db, &txid));
    }

    #[test]
    fn a_failed_input_write_rolls_back_the_transaction_status() {
        let (db, id, txid, response) = pooled_send();
        mark_failed(&db, id, &txid).unwrap();
        db.conn()
            .execute_batch(
                "CREATE TRIGGER refuse_input BEFORE UPDATE OF spent_at_height ON outputs
            BEGIN SELECT RAISE(ABORT, 'synthetic write failure'); END;",
            )
            .unwrap();
        assert!(apply_pool_response(&db, id, &[txid], &response, 10_000).is_err());
        assert!(failed(&db, &txid));
        assert_eq!(
            crate::query::balance(&db, id, Some(1000))
                .unwrap()
                .total_atomic,
            700
        );
    }
}
