//! Liveness of our own broadcast, unconfirmed transactions.
//!
//! Like wallet2 `process_unconfirmed_transfer`: a send still unknown to the node
//! [`TX_PROPAGATION_TIMEOUT_SECS`] after broadcast is marked failed and its inputs
//! are released, otherwise those funds stay locked.
//!
//! Confirmed sends are handled by scanning via key images, which sets the height
//! and clears the failed flag.

use rusqlite::params;
use serde_json::Value;

use crate::error::{db_err, Result};
use crate::network::Daemon;
use crate::wallet::{now_secs, Db};

/// Same as wallet2. Shorter than Dandelion++ propagation would fail fresh sends.
pub const TX_PROPAGATION_TIMEOUT_SECS: i64 = 500;

/// Minimum interval between node checks of the same pending send; sync steps run every few seconds.
const POOL_RECHECK_SECS: i64 = 60;

const RESPONSE_SIZE_LIMIT: usize = 4 * 1024 * 1024;

/// Pending sends to check: past the propagation timeout, not failed, not checked recently.
fn due_pending(db: &Db, identity_id: i64, now: i64) -> Result<Vec<[u8; 32]>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT txid FROM transactions
             WHERE identity_id = ?1 AND height IS NULL AND failed = 0
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

/// Transactions the node explicitly reports missing. A malformed response counts
/// none, so a send is never failed on an unclear answer.
fn missed_in(response: &str) -> Option<Vec<[u8; 32]>> {
    let v: Value = serde_json::from_str(response).ok()?;
    if v.get("status").and_then(Value::as_str) != Some("OK") {
        return None;
    }
    // epee may omit empty arrays.
    let Some(missed) = v.get("missed_tx") else {
        return Some(Vec::new());
    };
    missed
        .as_array()?
        .iter()
        .map(|h| {
            let bytes = hex::decode(h.as_str()?).ok()?;
            <[u8; 32]>::try_from(bytes.as_slice()).ok()
        })
        .collect()
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
pub(crate) fn mark_failed(db: &Db, identity_id: i64, txid: &[u8; 32]) -> Result<()> {
    let tx = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginMarkFailed", e))?;
    tx.execute(
        "UPDATE transactions SET failed = 1
         WHERE identity_id = ?1 AND txid = ?2 AND height IS NULL",
        params![identity_id, txid.as_slice()],
    )
    .map_err(|e| db_err("markSendFailed", e))?;
    tx.execute(
        "UPDATE outputs SET spent_at_height = NULL
         WHERE identity_id = ?1 AND spent_in_txid = ?2",
        params![identity_id, txid.as_slice()],
    )
    .map_err(|e| db_err("releaseFailedInputs", e))?;
    tx.commit().map_err(|e| db_err("commitMarkFailed", e))
}

/// Query the node once and mark missing sends failed; returns how many.
///
/// Changes nothing if the node is unreachable or the response is malformed: only
/// an explicit "missing" fails a send.
pub async fn check_pending(db: &Db, daemon: &Daemon, identity_id: i64) -> Result<usize> {
    let now = now_secs();
    let due = due_pending(db, identity_id, now)?;
    if due.is_empty() {
        return Ok(0);
    }
    let hashes: Vec<String> = due.iter().map(hex::encode).collect();
    let request = serde_json::json!({ "txs_hashes": hashes, "prune": true }).to_string();
    let Ok(response) = daemon
        .rpc_call("get_transactions", Some(request), RESPONSE_SIZE_LIMIT)
        .await
    else {
        return Ok(0);
    };
    let Some(missed) = missed_in(&response) else {
        return Ok(0);
    };
    mark_checked(db, identity_id, &due, now)?;
    let mut failed = 0;
    for txid in missed.iter().filter(|t| due.contains(t)) {
        mark_failed(db, identity_id, txid)?;
        failed += 1;
    }
    Ok(failed)
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
        assert!(
            due_pending(&db, id, i64::MAX / 2).unwrap().is_empty(),
            "failed sends are not rechecked"
        );
    }

    #[test]
    fn only_a_clear_answer_from_the_node_counts() {
        let tx = hex::encode([2u8; 32]);
        assert_eq!(
            missed_in(&format!(r#"{{"status":"OK","missed_tx":["{tx}"]}}"#)),
            Some(vec![[2u8; 32]])
        );
        assert_eq!(
            missed_in(r#"{"status":"OK","txs":[{"in_pool":true}]}"#),
            Some(vec![])
        );
        assert_eq!(missed_in(r#"{"status":"BUSY","missed_tx":[]}"#), None);
        assert_eq!(missed_in("not json"), None);
        assert_eq!(missed_in(r#"{"status":"OK","missed_tx":["zz"]}"#), None);
    }
}
