//! 存储用量统计。**只读，固定 SQL，不改任何数据。**
//!
//! 存在的理由：`diagSelfCheck` 只能报出表 / 视图 / 索引的**数量**，答不了
//! 「哪一组数据占了多少字节」。没有这个数，任何裁剪都是盲改 —— 先量再改。
//!
//! 刻意**不**提供「执行任意 SQL」的接口：那等于把数据库交给宿主，
//! 一条写错的语句就能删掉仍待证明的 anchor。这里的每条查询都是写死的。
//!
//! 所有 SQL 里的表名与列名都来自 `wallet.rs::tests::dump_schema` 的实测输出，
//! 不是照文档抄的 —— 表名写错时 SQLite 会报错而不是安静返回 0，但列名写错
//! 在某些聚合下会返回 NULL，所以两者都必须对着真实 schema 核过。

use crate::error::{ErrorCode, Result, RuntimeError};
use rusqlite::Connection;
use serde_json::{json, Value};

fn db_err(op: &str, e: impl std::fmt::Debug) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": op }))
        .detail(format!("{e:?}"))
}

fn count(conn: &Connection, sql: &str, op: &str) -> Result<i64> {
    conn.query_row(sql, [], |r| r.get(0))
        .map_err(|e| db_err(op, e))
}

/// `SUM` 在空表上返回 NULL，直接 `get::<i64>` 会失败 —— 用 Option 接住并归零。
fn sum(conn: &Connection, sql: &str, op: &str) -> Result<i64> {
    conn.query_row(sql, [], |r| r.get::<_, Option<i64>>(0))
        .map(|v| v.unwrap_or(0))
        .map_err(|e| db_err(op, e))
}

fn opt_i64(conn: &Connection, sql: &str, op: &str) -> Result<Value> {
    conn.query_row(sql, [], |r| r.get::<_, Option<i64>>(0))
        .map(|v| v.map_or(Value::Null, |n| json!(n)))
        .map_err(|e| db_err(op, e))
}

/// 一棵树的用量。三个池的表名只差前缀，所以共用这一段。
fn tree_stats(conn: &Connection, prefix: &str) -> Result<Value> {
    Ok(json!({
        "shards": count(conn, &format!("SELECT COUNT(*) FROM {prefix}_tree_shards"), "shards")?,
        // shard_data 是聚合后的树分片，通常是数据库里最大的 BLOB 列。
        "shardBytes": sum(
            conn,
            &format!("SELECT SUM(LENGTH(shard_data)) FROM {prefix}_tree_shards"),
            "shardBytes",
        )?,
        "checkpoints": count(
            conn,
            &format!("SELECT COUNT(*) FROM {prefix}_tree_checkpoints"),
            "checkpoints",
        )?,
        // ZIP-318 的 durable anchor。它们**不受**普通 100 深度上限约束，
        // 而 runtime 目前没有任何回收路径，所以这个数只会单调增长 ——
        // 这正是需要先量出来的东西。
        "retained": count(
            conn,
            &format!("SELECT COUNT(*) FROM {prefix}_tree_retained_checkpoints"),
            "retained",
        )?,
        "marksRemoved": count(
            conn,
            &format!("SELECT COUNT(*) FROM {prefix}_tree_checkpoint_marks_removed"),
            "marksRemoved",
        )?,
    }))
}

/// 分组统计当前钱包库的用量，JSON 字符串。
pub fn storage_stats(conn: &Connection) -> Result<String> {
    // 总量走 PRAGMA：`dbstat` 虚拟表未必编进当前 SQLite，PRAGMA 一定在。
    let page_size = count(conn, "PRAGMA page_size", "pageSize")?;
    let page_count = count(conn, "PRAGMA page_count", "pageCount")?;
    let free_pages = count(conn, "PRAGMA freelist_count", "freelistCount")?;

    Ok(json!({
        "sqlite": {
            "pageSize": page_size,
            "pageCount": page_count,
            "freePages": free_pages,
            // 页数 × 页大小。这是**逻辑**大小，不等于 IndexedDB 的物理占用 ——
            // 那边有写放大、旧版本页和异步刷盘，两个数不该互相外推。
            "logicalBytes": page_size * page_count,
        },
        "scan": {
            "minHeight": opt_i64(conn, "SELECT MIN(height) FROM blocks", "minHeight")?,
            "maxHeight": opt_i64(conn, "SELECT MAX(height) FROM blocks", "maxHeight")?,
            // 严格随「已扫块数」线性增长，是最明确的线性项。
            // 注意：它不是 compact block 本身，每行只有 height/hash/time 和三池的
            // 树大小与 action 计数。
            "blockRows": count(conn, "SELECT COUNT(*) FROM blocks", "blockRows")?,
            "rangeRows": count(conn, "SELECT COUNT(*) FROM scan_queue", "rangeRows")?,
        },
        "trees": {
            "sapling": tree_stats(conn, "sapling")?,
            "orchard": tree_stats(conn, "orchard")?,
            "ironwood": tree_stats(conn, "ironwood")?,
        },
        "wallet": {
            "accounts": count(conn, "SELECT COUNT(*) FROM accounts", "accounts")?,
            "addresses": count(conn, "SELECT COUNT(*) FROM addresses", "addresses")?,
            "transactions": count(conn, "SELECT COUNT(*) FROM transactions", "transactions")?,
            // 完整交易字节，只在本钱包构造或做过 enhancement 后才有。
            "rawTxBytes": sum(conn, "SELECT SUM(LENGTH(raw)) FROM transactions", "rawTxBytes")?,
            "notes": count(
                conn,
                "SELECT (SELECT COUNT(*) FROM sapling_received_notes)
                      + (SELECT COUNT(*) FROM orchard_received_notes)
                      + (SELECT COUNT(*) FROM ironwood_received_notes)",
                "notes",
            )?,
            "memoBytes": sum(
                conn,
                "SELECT (SELECT COALESCE(SUM(LENGTH(memo)), 0) FROM sapling_received_notes)
                      + (SELECT COALESCE(SUM(LENGTH(memo)), 0) FROM orchard_received_notes)
                      + (SELECT COALESCE(SUM(LENGTH(memo)), 0) FROM ironwood_received_notes)
                      + (SELECT COALESCE(SUM(LENGTH(memo)), 0) FROM sent_notes)",
                "memoBytes",
            )?,
            "transparentOutputs": count(
                conn,
                "SELECT COUNT(*) FROM transparent_received_outputs",
                "transparentOutputs",
            )?,
        },
        // 有界或有删除路径的几组。官方把 nullifier_map 裁到最近 100 块，
        // 所以这里的数**不该**随链头无限增长 —— 长了说明裁剪没生效。
        "transient": {
            "nullifiers": count(conn, "SELECT COUNT(*) FROM nullifier_map", "nullifiers")?,
            "locators": count(conn, "SELECT COUNT(*) FROM tx_locator_map", "locators")?,
            "retrievalQueue": count(
                conn,
                "SELECT COUNT(*) FROM tx_retrieval_queue",
                "retrievalQueue",
            )?,
            "spendSearchQueue": count(
                conn,
                "SELECT COUNT(*) FROM transparent_spend_search_queue",
                "spendSearchQueue",
            )?,
        },
        "poolMigration": {
            "runs": count(
                conn,
                "SELECT COUNT(*) FROM orchard_ironwood_migrations",
                "migrationRuns",
            )?,
            "transactions": count(
                conn,
                "SELECT COUNT(*) FROM orchard_ironwood_migration_transactions",
                "migrationTxs",
            )?,
            // 在途 migration 的 PCZT 可能不小，且按次数单调增长。
            "pcztBytes": sum(
                conn,
                "SELECT SUM(LENGTH(pczt)) FROM orchard_ironwood_migration_transactions",
                "pcztBytes",
            )?,
        },
    })
    .to_string())
}

/// Runs bounded, read-only checks suitable for an existing large wallet database.
pub fn database_health(conn: &Connection) -> Result<String> {
    let mut quick_check_statement = conn
        .prepare("PRAGMA quick_check(20)")
        .map_err(|e| db_err("quickCheckPrepare", e))?;
    let quick_check_messages = quick_check_statement
        .query_map([], |row| row.get::<_, String>(0))
        .map_err(|e| db_err("quickCheckQuery", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("quickCheckRead", e))?;
    let quick_check_ok =
        quick_check_messages.len() == 1 && quick_check_messages.first().is_some_and(|v| v == "ok");

    let mut foreign_key_statement = conn
        .prepare("PRAGMA foreign_key_check")
        .map_err(|e| db_err("foreignKeyCheckPrepare", e))?;
    let mut foreign_key_rows = foreign_key_statement
        .query([])
        .map_err(|e| db_err("foreignKeyCheckQuery", e))?;
    let mut foreign_key_violations = 0_u64;
    while foreign_key_rows
        .next()
        .map_err(|e| db_err("foreignKeyCheckRead", e))?
        .is_some()
    {
        foreign_key_violations += 1;
    }

    Ok(json!({
        "quickCheck": {
            "ok": quick_check_ok,
            "messages": quick_check_messages,
        },
        "foreignKeyViolations": foreign_key_violations,
        "schema": {
            "tables": count(conn, "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table'", "tables")?,
            "views": count(conn, "SELECT COUNT(*) FROM sqlite_master WHERE type = 'view'", "views")?,
            "indexes": count(conn, "SELECT COUNT(*) FROM sqlite_master WHERE type = 'index'", "indexes")?,
        },
        "versions": crate::dependency_versions(),
    })
    .to_string())
}

#[cfg(test)]
mod tests {
    use super::*;

    /// 空库上跑一遍。目的不是看数字，是证明**每条 SQL 的表名和列名都真实存在** ——
    /// 写错的话 SQLite 会在这里报错，而不是等到线上安静地返回 0。
    #[test]
    fn every_query_matches_the_real_schema() {
        let path = std::env::temp_dir().join("zr-stats-schema-check.sqlite");
        let _ = std::fs::remove_file(&path);
        let db = crate::wallet::open_and_migrate("test", path.to_str().unwrap()).expect("迁移");
        drop(db);

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("开连接");
        let out = storage_stats(&conn).expect("统计不应失败");
        let v: Value = serde_json::from_str(&out).expect("应是合法 JSON");

        // 空库也必须报出结构，而不是缺字段。
        for group in [
            "sqlite",
            "scan",
            "trees",
            "wallet",
            "transient",
            "poolMigration",
        ] {
            assert!(v.get(group).is_some(), "缺少分组 {group}");
        }
        for pool in ["sapling", "orchard", "ironwood"] {
            let t = &v["trees"][pool];
            for k in [
                "shards",
                "shardBytes",
                "checkpoints",
                "retained",
                "marksRemoved",
            ] {
                assert!(t.get(k).is_some(), "{pool} 缺少 {k}");
            }
        }
        // 空库没有扫过块，高度必须是 null 而不是 0 —— 0 会被误读成「扫到创世块」。
        assert!(v["scan"]["minHeight"].is_null());
        assert!(v["scan"]["maxHeight"].is_null());
        // 页大小必须是真数，否则 logicalBytes 没有意义。
        assert!(v["sqlite"]["pageSize"].as_i64().unwrap_or(0) > 0);
        assert!(v["sqlite"]["logicalBytes"].as_i64().unwrap_or(0) > 0);

        let _ = std::fs::remove_file(&path);
    }

    #[test]
    fn healthy_database_passes_bounded_checks() {
        let path = std::env::temp_dir().join("zr-database-health.sqlite");
        let _ = std::fs::remove_file(&path);
        let db = crate::wallet::open_and_migrate("test", path.to_str().unwrap()).expect("migrate");
        drop(db);

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("open");
        let out = database_health(&conn).expect("health check");
        let value: Value = serde_json::from_str(&out).expect("valid JSON");
        assert_eq!(value["quickCheck"]["ok"], true);
        assert_eq!(value["foreignKeyViolations"], 0);
        assert!(value["schema"]["tables"].as_i64().unwrap_or(0) > 0);

        drop(conn);
        let _ = std::fs::remove_file(&path);
    }
}
