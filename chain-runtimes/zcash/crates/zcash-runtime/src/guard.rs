//! 破坏性操作前的统一闸门。
//!
//! 这些操作会丢掉扫描结果、账户或整个库。它们共用一个判断：钱包现在是不是
//! 「闲着」—— 没有在途提案、没有未落块的花费、没有未确认结局的广播。
//!
//! ## 为什么问库而不是问进程
//!
//! 原来「有没有在途提案」问的是 `send` 里的进程内 map。Worker 一重启它就是
//! 空的，闸门于是静默放行，而锁本身还在 SQLite 里 —— 正是「用户刷新了页面，
//! 然后点了删除」这条路。锁是持久化的，判断也必须是。

use serde_json::json;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::history::uuid_blob;

/// 输出锁算不算「忙」。
#[derive(Clone, Copy, PartialEq, Eq)]
pub enum LockPolicy {
    /// 未过期的锁就拒绝：可能有人正在证明或签名这批 note。
    Blocking,
    /// 锁正是这个操作要清的东西，拿它当拒绝理由等于让修复按钮永远点不动。
    Ignored,
}

/// 带锁的四张表。上游按池分表，锁列在每张表上各有一份。
const LOCKABLE_TABLES: [&str; 4] = [
    "sapling_received_notes",
    "orchard_received_notes",
    "ironwood_received_notes",
    "transparent_received_outputs",
];

fn db_err(operation: &str, error: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(error)
}

/// 链尖。与上游 `chain_tip_height` 同源：扫描区间是右开的，所以减一。
fn chain_tip(conn: &rusqlite::Connection) -> Result<Option<u32>> {
    conn.query_row("SELECT MAX(block_range_end) FROM scan_queue", [], |row| {
        row.get::<_, Option<u32>>(0)
    })
    .map(|end| end.map(|h| h.saturating_sub(1)))
    .map_err(|e| db_err("walletIdleChainTip", e))
}

/// 这个账户（`None` = 整个库）名下还有没有未过期的输出锁。
fn has_live_locks(conn: &rusqlite::Connection, account_uuid: Option<&str>) -> Result<bool> {
    // 链尖未知时不做过期判断：宁可报忙，也不要因为读不到高度就把一批
    // 正在签名的 note 判成空闲。
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

/// 已花出去但还没落块、也还没过期的交易。回退或删除会把它的记录一起丢掉。
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

/// 已授权广播、结局还没确定的交易。这份状态不是可重建的扫描缓存。
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

/// 钱包不空闲就拒绝。`account_uuid` 为 `None` 时判整个库 —— 回退和删库影响
/// 的是共享库里的每一个账户，只看当前这个会漏掉别人的在途交易。
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

    /// 在真实迁移出来的库上跑一遍。目的不是看结果，是证明**每条 SQL 的表名和
    /// 列名都真实存在** —— 写错的话 SQLite 在这里就报错，而不是等到线上把
    /// 「读不出来」当成「钱包空闲」然后删库。
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

    /// 这是整个改动的理由：锁在库里，进程内那张表重启就空了。
    #[test]
    fn a_persisted_lock_blocks_the_account_that_holds_it() {
        let conn = Connection::open_in_memory().unwrap();
        fixture(&conn);
        let owner = uuid::Uuid::from_u128(1).to_string();
        let other = uuid::Uuid::from_u128(2).to_string();

        // 还没有锁：谁都空闲。
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

        // 别人的账户不受影响；整库判则看得见。
        assert!(assert_idle(&conn, Some(&other), "removeAccount", LockPolicy::Blocking).is_ok());
        assert!(assert_idle(&conn, None, "rewindTo", LockPolicy::Blocking).is_err());

        // clearLockedOutputs 清的就是它，不能拿它当拒绝理由。
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
        // 链尖 1000（scan_queue 右开），过期高度 900 已经过去了。
        conn.execute(
            "INSERT INTO orchard_received_notes VALUES (1, ?1, 900)",
            rusqlite::params![[7_u8; 32]],
        )
        .unwrap();
        assert!(assert_idle(&conn, Some(&owner), "removeAccount", LockPolicy::Blocking).is_ok());
    }

    /// 广播还没有结局的时候，连「修复」按钮也不能动锁 —— 解锁等于把正在被
    /// 一笔待确认交易花掉的 note 放回可选池。
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
