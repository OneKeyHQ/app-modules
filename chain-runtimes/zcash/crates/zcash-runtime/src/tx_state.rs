//! OneKey-owned record of node rejections.
//!
//! Everything else about a sent transaction lives in `zcash_client_sqlite`:
//! the signed transaction is stored before broadcast, and stays in
//! `v_transactions` as unmined (holding its notes) until it is mined or
//! expires. The one fact the wallet schema cannot hold is that the trusted node
//! definitively rejected it; that must stop any resend and show the send as
//! failed. An unmined send that was not rejected stays pending until mined or
//! expired; the host does not resubmit it.

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

const LIFECYCLE_MIGRATION_ID: Uuid = Uuid::from_u128(0x5be0af90_63f4_43ea_ae65_8207c84e46dd);
const REJECTIONS_MIGRATION_ID: Uuid = Uuid::from_u128(0x0d3b6f2e_9a41_4c8e_b7d5_3e1f6a2c9b70);
pub(crate) const TABLE: &str = "ext_onekey_tx_state";

/// External migrations, in dependency order.
pub fn migrations() -> Vec<Box<dyn RusqliteMigration<Error = WalletMigrationError>>> {
    vec![Box::new(LifecycleMigration), Box::new(RejectionsMigration)]
}

/// The original lifecycle table. Kept so databases that already applied it
/// keep a consistent migration graph; `RejectionsMigration` replaces it.
struct LifecycleMigration;

impl schemerz::Migration<Uuid> for LifecycleMigration {
    fn id(&self) -> Uuid {
        LIFECYCLE_MIGRATION_ID
    }

    fn dependencies(&self) -> HashSet<Uuid> {
        migrations::V_0_22_0_RC6.iter().copied().collect()
    }

    fn description(&self) -> &'static str {
        "Adds OneKey-owned Zcash transaction lifecycle state."
    }
}

impl RusqliteMigration for LifecycleMigration {
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
        Err(WalletMigrationError::CannotRevert(LIFECYCLE_MIGRATION_ID))
    }
}

/// Keeps only node rejections. Pending/accepted rows are dropped: those
/// transactions are already unmined rows in `v_transactions`.
struct RejectionsMigration;

impl schemerz::Migration<Uuid> for RejectionsMigration {
    fn id(&self) -> Uuid {
        REJECTIONS_MIGRATION_ID
    }

    fn dependencies(&self) -> HashSet<Uuid> {
        HashSet::from([LIFECYCLE_MIGRATION_ID])
    }

    fn description(&self) -> &'static str {
        "Reduces OneKey Zcash transaction state to node rejections."
    }
}

impl RusqliteMigration for RejectionsMigration {
    type Error = WalletMigrationError;

    fn up(&self, transaction: &rusqlite::Transaction) -> std::result::Result<(), Self::Error> {
        transaction.execute_batch(
            "CREATE TABLE ext_onekey_tx_state_new (
               txid BLOB PRIMARY KEY NOT NULL,
               account_uuid BLOB NOT NULL,
               rejection_code INTEGER,
               rejection_reason TEXT
             );
             INSERT INTO ext_onekey_tx_state_new (
               txid, account_uuid, rejection_code, rejection_reason
             )
               SELECT txid, account_uuid, rejection_code, rejection_reason
               FROM ext_onekey_tx_state
               WHERE broadcast_state = 'rejected';
             DROP TABLE ext_onekey_tx_state;
             ALTER TABLE ext_onekey_tx_state_new RENAME TO ext_onekey_tx_state;",
        )?;
        Ok(())
    }

    fn down(&self, _transaction: &rusqlite::Transaction) -> std::result::Result<(), Self::Error> {
        Err(WalletMigrationError::CannotRevert(REJECTIONS_MIGRATION_ID))
    }
}

fn db_err(operation: &'static str, error: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::DatabaseError,
        serde_json::json!({ "operation": operation }),
    )
    .detail(error)
}

/// Account that sent this stored transaction, for a rejection row.
pub fn account_uuid_for_txid(db: &mut Db, txid: &str) -> Result<String> {
    let txid = display_order_to_internal(txid)?;
    let bytes = db.transactionally_with_extension(|_wallet, ext| {
        ext.query_row(
            "SELECT account_uuid
             FROM v_transactions
             WHERE txid = ?1 AND COALESCE(total_spent, 0) > 0
             LIMIT 1",
            [&txid],
            |row| row.get::<_, Vec<u8>>(0),
        )
        .map_err(|error| db_err("readAccountForTxid", error))
    })?;
    uuid::Uuid::from_slice(&bytes)
        .map(|uuid| uuid.to_string())
        .map_err(|error| db_err("parseAccountForTxid", error))
}

/// Records the node's definitive rejection of a stored transaction.
pub fn record_rejection(
    db: &mut Db,
    txid: &str,
    account_uuid: &str,
    error: &RuntimeError,
) -> Result<()> {
    let txid = display_order_to_internal(txid)?;
    let account_uuid = uuid_blob(account_uuid)?;
    let rejection_code = error
        .params
        .get("errorCode")
        .and_then(serde_json::Value::as_i64);
    db.transactionally_with_extension(|_wallet, ext| {
        ext.execute(
            &format!(
                "INSERT INTO {TABLE} (txid, account_uuid, rejection_code, rejection_reason)
                 VALUES (?1, ?2, ?3, ?4)
                 ON CONFLICT(txid) DO UPDATE SET
                   account_uuid = excluded.account_uuid,
                   rejection_code = excluded.rejection_code,
                   rejection_reason = excluded.rejection_reason"
            ),
            params![txid, account_uuid, rejection_code, error.detail.as_deref()],
        )
        .map_err(|error| db_err("recordRejection", error))?;
        Ok(())
    })
}

/// Fails with the recorded `BroadcastRejected` if the node already rejected this transaction.
pub fn assert_not_rejected(db: &mut Db, txid: &str) -> Result<()> {
    let txid = display_order_to_internal(txid)?;
    let rejection = db.transactionally_with_extension(|_wallet, ext| {
        ext.query_row(
            &format!("SELECT rejection_code, rejection_reason FROM {TABLE} WHERE txid = ?1"),
            [&txid],
            |row| {
                Ok((
                    row.get::<_, Option<i64>>(0)?,
                    row.get::<_, Option<String>>(1)?,
                ))
            },
        )
        .optional()
        .map_err(|error| db_err("readRejection", error))
    })?;
    match rejection {
        None => Ok(()),
        Some((error_code, reason)) => {
            let mut error = RuntimeError::with(
                ErrorCode::BroadcastRejected,
                serde_json::json!({ "errorCode": error_code }),
            );
            error.detail = reason;
            Err(error)
        }
    }
}

/// This account's stored outgoing transactions that are neither mined, expired
/// nor rejected: still pending. The host does not resubmit them.
pub fn pending_txids(conn: &rusqlite::Connection, account_uuid: &str) -> Result<Vec<String>> {
    let account_uuid = uuid_blob(account_uuid)?;
    let mut stmt = conn
        .prepare(&format!(
            "SELECT tx.txid
             FROM v_transactions tx
             JOIN transactions chain_tx ON chain_tx.txid = tx.txid
             WHERE tx.account_uuid = ?1
               AND tx.mined_height IS NULL
               AND tx.expired_unmined = 0
               AND COALESCE(tx.total_spent, 0) > 0
               AND chain_tx.raw IS NOT NULL
               AND NOT EXISTS (SELECT 1 FROM {TABLE} rejected WHERE rejected.txid = tx.txid)"
        ))
        .map_err(|error| db_err("listPendingBroadcasts", error))?;
    let rows = stmt
        .query_map([account_uuid], |row| row.get::<_, Vec<u8>>(0))
        .map_err(|error| db_err("listPendingBroadcasts", error))?;
    rows.map(|row| {
        row.map(|bytes| hex_display_order(&bytes))
            .map_err(|error| db_err("listPendingBroadcasts", error))
    })
    .collect()
}

/// Drops rejections whose transaction left the wallet with a removed account.
pub fn prune_orphaned_rows(ext: &ExtensionTransaction<'_>) -> Result<()> {
    ext.execute(
        &format!("DELETE FROM {TABLE} WHERE txid NOT IN (SELECT txid FROM transactions)"),
        [],
    )
    .map_err(|error| db_err("pruneTransactionState", error))?;
    Ok(())
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::clock::HostClock;
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use zcash_client_sqlite::WalletDb;
    use zcash_protocol::consensus::Network;

    fn wallet(conn: rusqlite::Connection) -> Db {
        WalletDb::from_connection(
            conn,
            Network::TestNetwork,
            HostClock,
            ChaCha20Rng::from_seed([7; 32]),
        )
    }

    #[test]
    fn the_rebuild_keeps_only_rejections() {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        let tx = conn.unchecked_transaction().unwrap();
        LifecycleMigration.up(&tx).unwrap();
        for (byte, state) in [(1_u8, "pending"), (2, "accepted"), (3, "rejected")] {
            tx.execute(
                "INSERT INTO ext_onekey_tx_state (
                   txid, account_uuid, broadcast_state, rejection_code, rejection_reason
                 ) VALUES (?1, ?2, ?3, -26, 'bad-txns')",
                params![vec![byte; 32], Uuid::nil().as_bytes().to_vec(), state],
            )
            .unwrap();
        }
        RejectionsMigration.up(&tx).unwrap();
        tx.commit().unwrap();

        let rows: Vec<(Vec<u8>, Vec<u8>, i64, String)> = conn
            .prepare(
                "SELECT txid, account_uuid, rejection_code, rejection_reason
                 FROM ext_onekey_tx_state",
            )
            .unwrap()
            .query_map([], |row| {
                Ok((row.get(0)?, row.get(1)?, row.get(2)?, row.get(3)?))
            })
            .unwrap()
            .map(|row| row.unwrap())
            .collect();
        assert_eq!(
            rows,
            vec![(
                vec![3_u8; 32],
                Uuid::nil().as_bytes().to_vec(),
                -26,
                "bad-txns".to_owned()
            )]
        );
    }

    #[test]
    fn a_recorded_rejection_is_returned_with_the_nodes_reason() {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE ext_onekey_tx_state (
               txid BLOB PRIMARY KEY NOT NULL,
               account_uuid BLOB NOT NULL,
               rejection_code INTEGER,
               rejection_reason TEXT
             );",
        )
        .unwrap();
        let mut db = wallet(conn);
        let txid = "01".repeat(32);
        let account = Uuid::nil().to_string();
        assert_not_rejected(&mut db, &txid).unwrap();

        let rejected = RuntimeError::with(
            ErrorCode::BroadcastRejected,
            serde_json::json!({ "errorCode": 18 }),
        )
        .detail("bad-txns-expired");
        record_rejection(&mut db, &txid, &account, &rejected).unwrap();
        record_rejection(&mut db, &txid, &account, &rejected).unwrap();

        let error = assert_not_rejected(&mut db, &txid).unwrap_err();
        assert_eq!(error.code, ErrorCode::BroadcastRejected);
        assert_eq!(error.params["errorCode"], 18);
        assert_eq!(error.detail.as_deref(), Some("bad-txns-expired"));
    }

    #[test]
    fn only_unmined_unexpired_unrejected_outgoing_transactions_are_pending() {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE ext_onekey_tx_state (txid BLOB PRIMARY KEY NOT NULL);
             CREATE TABLE transactions (txid BLOB NOT NULL, raw BLOB);
             CREATE TABLE v_transactions (
               txid BLOB NOT NULL,
               account_uuid BLOB NOT NULL,
               mined_height INTEGER,
               expired_unmined INTEGER NOT NULL,
               total_spent INTEGER
             );",
        )
        .unwrap();
        let account = Uuid::nil().as_bytes().to_vec();
        // (txid byte, mined_height, expired_unmined, total_spent, has raw, rejected)
        let rows = [
            (1_u8, None, 0, 10, true, false),
            (2, Some(100), 0, 10, true, false),
            (3, None, 1, 10, true, false),
            (4, None, 0, 0, true, false),
            (5, None, 0, 10, false, false),
            (6, None, 0, 10, true, true),
        ];
        for (byte, mined_height, expired_unmined, total_spent, has_raw, rejected) in rows {
            let txid = vec![byte; 32];
            conn.execute(
                "INSERT INTO transactions (txid, raw) VALUES (?1, ?2)",
                params![txid, has_raw.then(|| vec![0_u8])],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO v_transactions VALUES (?1, ?2, ?3, ?4, ?5)",
                params![txid, account, mined_height, expired_unmined, total_spent],
            )
            .unwrap();
            if rejected {
                conn.execute("INSERT INTO ext_onekey_tx_state VALUES (?1)", [txid])
                    .unwrap();
            }
        }

        assert_eq!(
            pending_txids(&conn, &Uuid::nil().to_string()).unwrap(),
            vec![hex_display_order(&[1_u8; 32])]
        );
    }
}
