use std::collections::HashSet;

use rusqlite::{params, OptionalExtension};
use schemerz_rusqlite::RusqliteMigration;
use uuid::Uuid;
use zcash_client_sqlite::{
    wallet::init::{migrations, WalletMigrationError},
    ExtensionTransaction,
};

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::history::{display_order_to_internal, hex_display_order, uuid_blob};
use crate::wallet::Db;

const MIGRATION_ID: Uuid = Uuid::from_u128(0x5be0af90_63f4_43ea_ae65_8207c84e46dd);
const TABLE: &str = "ext_onekey_tx_state";

pub struct Migration;

impl schemerz::Migration<Uuid> for Migration {
    fn id(&self) -> Uuid {
        MIGRATION_ID
    }

    fn dependencies(&self) -> HashSet<Uuid> {
        migrations::V_0_22_0_RC6.iter().copied().collect()
    }

    fn description(&self) -> &'static str {
        "Adds OneKey-owned Zcash transaction lifecycle state."
    }
}

impl RusqliteMigration for Migration {
    type Error = WalletMigrationError;

    fn up(&self, transaction: &rusqlite::Transaction) -> std::result::Result<(), Self::Error> {
        transaction.execute_batch(
            "CREATE TABLE ext_onekey_tx_state (
               txid BLOB PRIMARY KEY NOT NULL,
               account_uuid BLOB NOT NULL,
               reservation_id TEXT UNIQUE,
               recipient TEXT,
               broadcast_state TEXT NOT NULL
                 CHECK (broadcast_state IN (
                   'observed', 'pending', 'accepted', 'rejected'
                 )),
               rejection_code INTEGER,
               rejection_reason TEXT
             );
             CREATE INDEX ext_onekey_tx_state_account_state
               ON ext_onekey_tx_state (account_uuid, broadcast_state);",
        )?;
        Ok(())
    }

    fn down(&self, _transaction: &rusqlite::Transaction) -> std::result::Result<(), Self::Error> {
        Err(WalletMigrationError::CannotRevert(MIGRATION_ID))
    }
}

fn db_err(operation: &'static str, error: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::DatabaseError,
        serde_json::json!({ "operation": operation }),
    )
    .detail(error)
}

pub fn existing_txid_for_reservation(
    ext: &ExtensionTransaction<'_>,
    reservation_id: &str,
) -> Result<Option<String>> {
    let txid = ext
        .query_row(
            &format!("SELECT txid FROM {TABLE} WHERE reservation_id = ?1"),
            [reservation_id],
            |row| row.get::<_, Vec<u8>>(0),
        )
        .optional()
        .map_err(|error| db_err("findBroadcastIntent", error))?;
    Ok(txid.map(|bytes| hex_display_order(&bytes)))
}

pub fn insert_finalized(
    ext: &ExtensionTransaction<'_>,
    txid: &str,
    account_uuid: &str,
    reservation_id: &str,
) -> Result<()> {
    let txid = display_order_to_internal(txid)?;
    let account_uuid = uuid_blob(account_uuid)?;
    ext.execute(
        &format!(
            "INSERT INTO {TABLE} (
               txid, account_uuid, reservation_id, recipient, broadcast_state
             )
             VALUES (
               ?1, ?2, ?3,
               (SELECT output.to_address
                FROM v_tx_outputs output
                WHERE output.txid = ?1
                  AND output.from_account_uuid = ?2
                  AND output.to_account_uuid IS NULL
                  AND COALESCE(output.is_change, 0) = 0
                LIMIT 1),
               'pending'
             )
             ON CONFLICT(txid) DO UPDATE SET
               account_uuid = excluded.account_uuid,
               reservation_id = excluded.reservation_id,
               recipient = COALESCE(excluded.recipient, {TABLE}.recipient),
               broadcast_state = 'pending',
               rejection_code = NULL,
               rejection_reason = NULL"
        ),
        params![txid, account_uuid, reservation_id],
    )
    .map_err(|error| db_err("saveBroadcastIntent", error))?;
    Ok(())
}

pub fn assert_broadcastable(db: &mut Db, txid: &str) -> Result<()> {
    let txid = display_order_to_internal(txid)?;
    db.transactionally_with_extension(|_wallet, ext| {
        let state = ext
            .query_row(
                &format!(
                    "SELECT broadcast_state, rejection_code, rejection_reason
                     FROM {TABLE}
                     WHERE txid = ?1"
                ),
                [&txid],
                |row| {
                    Ok((
                        row.get::<_, String>(0)?,
                        row.get::<_, Option<i64>>(1)?,
                        row.get::<_, Option<String>>(2)?,
                    ))
                },
            )
            .optional()
            .map_err(|error| db_err("readBroadcastIntent", error))?;
        match state {
            Some((state, _, _)) if state == "pending" || state == "accepted" => Ok(()),
            Some((state, error_code, reason)) if state == "rejected" => {
                let mut error = RuntimeError::with(
                    ErrorCode::BroadcastRejected,
                    serde_json::json!({ "errorCode": error_code }),
                );
                error.detail = reason;
                Err(error)
            }
            Some((state, _, _)) => Err(RuntimeError::with(
                ErrorCode::BroadcastIntentNotReady,
                serde_json::json!({ "txid": hex_display_order(&txid), "state": state }),
            )),
            None => Err(RuntimeError::with(
                ErrorCode::BroadcastIntentNotFound,
                serde_json::json!({ "txid": hex_display_order(&txid) }),
            )),
        }
    })
}

pub fn mark_accepted(db: &mut Db, txid: &str) -> Result<()> {
    update_outcome(db, txid, "accepted", None)
}

pub fn mark_rejected(db: &mut Db, txid: &str, error: &RuntimeError) -> Result<()> {
    let error_code = error
        .params
        .get("errorCode")
        .and_then(serde_json::Value::as_i64);
    update_outcome(
        db,
        txid,
        "rejected",
        Some((error_code, error.detail.as_deref())),
    )
}

fn update_outcome(
    db: &mut Db,
    txid: &str,
    state: &str,
    rejection: Option<(Option<i64>, Option<&str>)>,
) -> Result<()> {
    let txid = display_order_to_internal(txid)?;
    let (rejection_code, rejection_reason) = rejection.unwrap_or((None, None));
    db.transactionally_with_extension(|_wallet, ext| {
        let changed = ext
            .execute(
                &format!(
                    "UPDATE {TABLE}
                     SET broadcast_state = ?2,
                         rejection_code = ?3,
                         rejection_reason = ?4
                     WHERE txid = ?1"
                ),
                params![txid, state, rejection_code, rejection_reason],
            )
            .map_err(|error| db_err("updateBroadcastIntent", error))?;
        if changed == 0 {
            return Err(RuntimeError::with(
                ErrorCode::BroadcastIntentNotFound,
                serde_json::json!({ "txid": hex_display_order(&txid) }),
            ));
        }
        Ok(())
    })
}

pub fn unresolved_txids(conn: &rusqlite::Connection, account_uuid: &str) -> Result<Vec<String>> {
    let account_uuid = uuid_blob(account_uuid)?;
    let mut stmt = conn
        .prepare(&format!(
            "SELECT state.txid
             FROM {TABLE} state
             LEFT JOIN v_transactions tx
               ON tx.txid = state.txid AND tx.account_uuid = state.account_uuid
             WHERE state.account_uuid = ?1
               AND state.broadcast_state IN ('pending', 'accepted')
               AND tx.mined_height IS NULL
               AND COALESCE(tx.expired_unmined, 0) = 0"
        ))
        .map_err(|error| db_err("listUnresolvedBroadcasts", error))?;
    let rows = stmt
        .query_map([account_uuid], |row| row.get::<_, Vec<u8>>(0))
        .map_err(|error| db_err("listUnresolvedBroadcasts", error))?;
    rows.map(|row| {
        row.map(|bytes| hex_display_order(&bytes))
            .map_err(|error| db_err("listUnresolvedBroadcasts", error))
    })
    .collect()
}

pub fn rebroadcastable_txids(
    conn: &rusqlite::Connection,
    account_uuid: &str,
) -> Result<Vec<String>> {
    let account_uuid = uuid_blob(account_uuid)?;
    let mut stmt = conn
        .prepare(&format!(
            "SELECT state.txid
             FROM {TABLE} state
             JOIN v_transactions tx
               ON tx.txid = state.txid AND tx.account_uuid = state.account_uuid
             WHERE state.account_uuid = ?1
               AND state.broadcast_state IN ('pending', 'accepted')
               AND tx.mined_height IS NULL
               AND COALESCE(tx.expired_unmined, 0) = 0"
        ))
        .map_err(|error| db_err("listRebroadcastableTransactions", error))?;
    let rows = stmt
        .query_map([account_uuid], |row| row.get::<_, Vec<u8>>(0))
        .map_err(|error| db_err("listRebroadcastableTransactions", error))?;
    rows.map(|row| {
        row.map(|bytes| hex_display_order(&bytes))
            .map_err(|error| db_err("listRebroadcastableTransactions", error))
    })
    .collect()
}

pub fn remove_account_rows(ext: &ExtensionTransaction<'_>, account_uuid: &str) -> Result<()> {
    let account_uuid = uuid_blob(account_uuid)?;
    ext.execute(
        &format!("DELETE FROM {TABLE} WHERE account_uuid = ?1"),
        [account_uuid],
    )
    .map_err(|error| db_err("deleteAccountTransactionState", error))?;
    Ok(())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn only_runtime_owned_active_intents_are_unresolved_and_rebroadcastable() {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE ext_onekey_tx_state (
               txid BLOB PRIMARY KEY NOT NULL,
               account_uuid BLOB NOT NULL,
               reservation_id TEXT UNIQUE,
               recipient TEXT,
               broadcast_state TEXT NOT NULL,
               rejection_code INTEGER,
               rejection_reason TEXT
             );
             CREATE TABLE v_transactions (
               txid BLOB NOT NULL,
               account_uuid BLOB NOT NULL,
               mined_height INTEGER,
               expired_unmined INTEGER NOT NULL
             );",
        )
        .unwrap();
        let account = Uuid::nil().as_bytes().to_vec();
        let rows = [
            (1_u8, "pending", Some((None, 0))),
            (2, "accepted", Some((None, 0))),
            (3, "observed", Some((None, 0))),
            (4, "rejected", Some((None, 0))),
            (5, "accepted", Some((Some(100), 0))),
            (6, "accepted", Some((None, 1))),
        ];
        for (byte, state, chain_state) in rows {
            let txid = vec![byte; 32];
            conn.execute(
                "INSERT INTO ext_onekey_tx_state (
                   txid, account_uuid, broadcast_state
                 ) VALUES (?1, ?2, ?3)",
                params![txid, account, state],
            )
            .unwrap();
            if let Some((mined_height, expired_unmined)) = chain_state {
                conn.execute(
                    "INSERT INTO v_transactions (
                       txid, account_uuid, mined_height, expired_unmined
                     ) VALUES (?1, ?2, ?3, ?4)",
                    params![txid, account, mined_height, expired_unmined],
                )
                .unwrap();
            }
        }

        let mut unresolved = unresolved_txids(&conn, &Uuid::nil().to_string()).unwrap();
        unresolved.sort();
        assert_eq!(
            unresolved,
            [1_u8, 2]
                .map(|byte| hex_display_order(&[byte; 32]))
                .to_vec()
        );

        let mut rebroadcastable = rebroadcastable_txids(&conn, &Uuid::nil().to_string()).unwrap();
        rebroadcastable.sort();
        assert_eq!(
            rebroadcastable,
            [1_u8, 2]
                .map(|byte| hex_display_order(&[byte; 32]))
                .to_vec()
        );
    }
}
