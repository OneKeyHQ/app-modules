//! Shared gate for destructive operations.
//!
//! Operations that drop scan results, accounts or the whole database require
//! the wallet to be idle: no in-flight proposals, no unmined spends, no
//! broadcasts with an unknown outcome.
//!
//! The check reads the database, not process memory: output locks persist in
//! SQLite and survive a Worker restart, so the check must as well.

use serde_json::json;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::history::uuid_blob;

/// Whether output locks count as busy.
#[derive(Clone, Copy, PartialEq, Eq)]
pub enum LockPolicy {
    /// Refuse on any unexpired lock: those notes may be being proven or signed.
    Blocking,
    /// The operation clears the locks itself, so they cannot block it.
    Ignored,
}

/// Tables with lock columns; upstream keeps one per pool.
const LOCKABLE_TABLES: [&str; 4] = [
    "sapling_received_notes",
    "orchard_received_notes",
    "ironwood_received_notes",
    "transparent_received_outputs",
];

fn db_err(operation: &str, error: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(error)
}

/// Chain tip, as upstream `chain_tip_height`: scan ranges are end-exclusive, hence -1.
fn chain_tip(conn: &rusqlite::Connection) -> Result<Option<u32>> {
    conn.query_row("SELECT MAX(block_range_end) FROM scan_queue", [], |row| {
        row.get::<_, Option<u32>>(0)
    })
    .map(|end| end.map(|h| h.saturating_sub(1)))
    .map_err(|e| db_err("walletIdleChainTip", e))
}

/// Whether the account (`None` = whole database) has unexpired output locks.
fn has_live_locks(conn: &rusqlite::Connection, account_uuid: Option<&str>) -> Result<bool> {
    // With an unknown tip, treat every lock as live: report busy rather than
    // free notes that may be mid-signing.
    let tip = chain_tip(conn)?;
    let account_filter = account_uuid
        .map(|uuid| uuid_blob(uuid).map(Some))
        .transpose()?
        .flatten();
    for table in LOCKABLE_TABLES {
        let sql = format!(
            "SELECT EXISTS(
               SELECT 1
               FROM {table} o
               JOIN accounts a ON a.id = o.account_id
               WHERE o.lock_owner IS NOT NULL
                 AND (?1 IS NULL OR a.uuid = ?1)
                 AND (?2 IS NULL OR o.lock_expiry_height IS NULL OR o.lock_expiry_height > ?2)
             )"
        );
        let found: bool = conn
            .query_row(&sql, rusqlite::params![account_filter, tip], |row| {
                row.get(0)
            })
            .map_err(|e| db_err("walletIdleLocks", e))?;
        if found {
            return Ok(true);
        }
    }
    Ok(false)
}

/// Spending transactions that are neither mined nor expired; a rewind or delete
/// would lose their records.
fn has_unsettled_outgoing(conn: &rusqlite::Connection, account_uuid: Option<&str>) -> Result<bool> {
    let account_filter = account_uuid
        .map(|uuid| uuid_blob(uuid).map(Some))
        .transpose()?
        .flatten();
    conn.query_row(
        "SELECT EXISTS(
           SELECT 1
           FROM v_transactions
           WHERE (?1 IS NULL OR account_uuid = ?1)
             AND mined_height IS NULL
             AND expired_unmined = 0
             AND COALESCE(total_spent, 0) > 0
         )",
        rusqlite::params![account_filter],
        |row| row.get(0),
    )
    .map_err(|e| db_err("walletIdleOutgoing", e))
}

/// Authorized broadcasts with no known outcome. This state cannot be rebuilt by rescanning.
fn has_unresolved_broadcast(
    conn: &rusqlite::Connection,
    account_uuid: Option<&str>,
) -> Result<bool> {
    let account_filter = account_uuid
        .map(|uuid| uuid_blob(uuid).map(Some))
        .transpose()?
        .flatten();
    conn.query_row(
        &format!(
            "SELECT EXISTS(
               SELECT 1
               FROM {table} state
               LEFT JOIN v_transactions tx
                 ON tx.txid = state.txid AND tx.account_uuid = state.account_uuid
               WHERE (?1 IS NULL OR state.account_uuid = ?1)
                 AND state.broadcast_state IN ('pending', 'accepted')
                 AND tx.mined_height IS NULL
                 AND COALESCE(tx.expired_unmined, 0) = 0
             )",
            table = crate::tx_state::TABLE,
        ),
        rusqlite::params![account_filter],
        |row| row.get(0),
    )
    .map_err(|e| db_err("walletIdleBroadcast", e))
}

/// Errors with `WalletBusy` unless the wallet is idle. Pass `None` for operations
/// that affect every account in the shared database (rewind, delete).
pub fn assert_idle(
    conn: &rusqlite::Connection,
    account_uuid: Option<&str>,
    operation: &str,
    locks: LockPolicy,
) -> Result<()> {
    let active_reservation = match locks {
        LockPolicy::Blocking => has_live_locks(conn, account_uuid)?,
        LockPolicy::Ignored => false,
    };
    let unsettled_outgoing = has_unsettled_outgoing(conn, account_uuid)?;
    let unresolved_broadcast = has_unresolved_broadcast(conn, account_uuid)?;
    if !active_reservation && !unsettled_outgoing && !unresolved_broadcast {
        return Ok(());
    }
    Err(RuntimeError::with(
        ErrorCode::WalletBusy,
        json!({
            "operation": operation,
            "activeReservation": active_reservation,
            "unsettledOutgoing": unsettled_outgoing,
            "unresolvedBroadcast": unresolved_broadcast,
        }),
    ))
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod tests {
    use super::*;
    use rusqlite::Connection;

    /// Runs against a migrated database to prove every table and column in the
    /// SQL exists, so a typo fails here rather than reading as "idle".
    #[test]
    fn every_query_matches_the_real_schema() {
        let path =
            std::env::temp_dir().join(format!("zr-guard-schema-{}.sqlite", uuid::Uuid::new_v4()));
        let _ = std::fs::remove_file(&path);
        drop(crate::wallet::open_and_migrate("test", path.to_str().unwrap()).expect("migrate"));

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("open");
        let nil = uuid::Uuid::nil().to_string();
        for (account, locks) in [
            (None, LockPolicy::Blocking),
            (None, LockPolicy::Ignored),
            (Some(nil.as_str()), LockPolicy::Blocking),
            (Some(nil.as_str()), LockPolicy::Ignored),
        ] {
            assert!(assert_idle(&conn, account, "schemaCheck", locks).is_ok());
        }
        let _ = std::fs::remove_file(&path);
    }

    fn fixture(conn: &Connection) {
        conn.execute_batch(
            "CREATE TABLE accounts (id INTEGER PRIMARY KEY, uuid BLOB);
             CREATE TABLE scan_queue (block_range_end INTEGER);
             CREATE VIEW v_transactions AS
               SELECT NULL AS account_uuid, NULL AS txid, NULL AS mined_height,
                      0 AS expired_unmined, 0 AS total_spent WHERE 0;
             CREATE TABLE ext_onekey_tx_state (
               txid BLOB, account_uuid BLOB, broadcast_state TEXT
             );",
        )
        .unwrap();
        for table in LOCKABLE_TABLES {
            conn.execute_batch(&format!(
                "CREATE TABLE {table} (
                   account_id INTEGER, lock_owner BLOB, lock_expiry_height INTEGER
                 );"
            ))
            .unwrap();
        }
        conn.execute("INSERT INTO scan_queue VALUES (1001)", [])
            .unwrap();
        for (id, uuid) in [(1, uuid::Uuid::from_u128(1)), (2, uuid::Uuid::from_u128(2))] {
            conn.execute(
                "INSERT INTO accounts VALUES (?1, ?2)",
                rusqlite::params![id, uuid.as_bytes().to_vec()],
            )
            .unwrap();
        }
    }

    /// Locks persist in the database, so they block after a restart.
    #[test]
    fn a_persisted_lock_blocks_the_account_that_holds_it() {
        let conn = Connection::open_in_memory().unwrap();
        fixture(&conn);
        let owner = uuid::Uuid::from_u128(1).to_string();
        let other = uuid::Uuid::from_u128(2).to_string();

        // No locks yet: idle.
        assert!(assert_idle(&conn, Some(&owner), "removeAccount", LockPolicy::Blocking).is_ok());

        conn.execute(
            "INSERT INTO orchard_received_notes VALUES (1, ?1, 1500)",
            rusqlite::params![[7_u8; 32]],
        )
        .unwrap();

        let err = assert_idle(&conn, Some(&owner), "removeAccount", LockPolicy::Blocking)
            .expect_err("a live lock must refuse a destructive operation");
        assert_eq!(err.code, ErrorCode::WalletBusy);
        assert_eq!(err.params["activeReservation"], true);
        assert_eq!(err.params["operation"], "removeAccount");

        // Other accounts are unaffected; the whole-database check sees it.
        assert!(assert_idle(&conn, Some(&other), "removeAccount", LockPolicy::Blocking).is_ok());
        assert!(assert_idle(&conn, None, "rewindTo", LockPolicy::Blocking).is_err());

        // clearLockedOutputs clears this lock, so it must not be refused.
        assert!(assert_idle(
            &conn,
            Some(&owner),
            "clearLockedOutputs",
            LockPolicy::Ignored
        )
        .is_ok());
    }

    #[test]
    fn an_expired_lock_no_longer_blocks() {
        let conn = Connection::open_in_memory().unwrap();
        fixture(&conn);
        let owner = uuid::Uuid::from_u128(1).to_string();
        // Tip is 1000 (scan_queue is end-exclusive); expiry 900 has passed.
        conn.execute(
            "INSERT INTO orchard_received_notes VALUES (1, ?1, 900)",
            rusqlite::params![[7_u8; 32]],
        )
        .unwrap();
        assert!(assert_idle(&conn, Some(&owner), "removeAccount", LockPolicy::Blocking).is_ok());
    }

    /// With an unresolved broadcast even the repair path must not unlock: that
    /// would return notes spent by a pending transaction to the selectable pool.
    #[test]
    fn an_unresolved_broadcast_blocks_even_the_repair_path() {
        let conn = Connection::open_in_memory().unwrap();
        fixture(&conn);
        let owner = uuid::Uuid::from_u128(1).to_string();
        conn.execute(
            "INSERT INTO ext_onekey_tx_state VALUES (?1, ?2, 'pending')",
            rusqlite::params![[9_u8; 32], uuid::Uuid::from_u128(1).as_bytes().to_vec()],
        )
        .unwrap();
        let err = assert_idle(
            &conn,
            Some(&owner),
            "clearLockedOutputs",
            LockPolicy::Ignored,
        )
        .expect_err("an unresolved broadcast must refuse");
        assert_eq!(err.code, ErrorCode::WalletBusy);
        assert_eq!(err.params["unresolvedBroadcast"], true);
        assert_eq!(err.params["activeReservation"], false);
    }
}
