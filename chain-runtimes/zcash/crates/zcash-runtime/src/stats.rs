//! Storage usage stats: read-only, fixed SQL, bytes per data group.
//!
//! There is deliberately no arbitrary-SQL entry point: one bad statement could
//! delete anchors still needed for proving. Table and column names must match the
//! migrated `zcash_client_sqlite` schema; a wrong column can yield NULL under some
//! aggregates instead of an error.

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

/// `SUM` over an empty table is NULL; map it to 0.
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

/// Usage for one pool's tree; pool tables differ only by prefix.
fn tree_stats(conn: &Connection, prefix: &str) -> Result<Value> {
    Ok(json!({
        "shards": count(conn, &format!("SELECT COUNT(*) FROM {prefix}_tree_shards"), "shards")?,
        // shard_data is usually the largest BLOB column in the database.
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
        // ZIP-318 durable anchors. Not bound by the 100-checkpoint limit and
        // never pruned by the runtime, so this only grows.
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

/// Storage usage of the wallet database by group, as a JSON string.
pub fn storage_stats(conn: &Connection) -> Result<String> {
    // Totals use PRAGMA; the `dbstat` virtual table may not be compiled in.
    let page_size = count(conn, "PRAGMA page_size", "pageSize")?;
    let page_count = count(conn, "PRAGMA page_count", "pageCount")?;
    let free_pages = count(conn, "PRAGMA freelist_count", "freelistCount")?;

    Ok(json!({
        "sqlite": {
            "pageSize": page_size,
            "pageCount": page_count,
            "freePages": free_pages,
            // Logical size only; physical storage usage can differ.
            "logicalBytes": page_size * page_count,
        },
        "scan": {
            "minHeight": opt_i64(conn, "SELECT MIN(height) FROM blocks", "minHeight")?,
            "maxHeight": opt_i64(conn, "SELECT MAX(height) FROM blocks", "maxHeight")?,
            // Grows linearly with scanned blocks; rows are small block metadata, not compact blocks.
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
            // Full transaction bytes, present only for own or enhanced transactions.
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
        // Bounded or pruned groups. Upstream prunes nullifier_map to the last
        // 100 blocks, so steady growth here means pruning is not working.
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
            // In-flight migration PCZTs can be large and grow with each run.
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

    /// Runs on an empty database to prove every table and column in the SQL exists.
    #[test]
    fn every_query_matches_the_real_schema() {
        let path = std::env::temp_dir().join("zr-stats-schema-check.sqlite");
        let _ = std::fs::remove_file(&path);
        let db = crate::wallet::open_and_migrate("test", path.to_str().unwrap()).expect("migrate");
        drop(db);

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("open");
        let out = storage_stats(&conn).expect("storage stats");
        let v: Value = serde_json::from_str(&out).expect("valid JSON");

        for group in [
            "sqlite",
            "scan",
            "trees",
            "wallet",
            "transient",
            "poolMigration",
        ] {
            assert!(v.get(group).is_some(), "missing group {group}");
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
                assert!(t.get(k).is_some(), "{pool} missing {k}");
            }
        }
        // Unscanned heights are null, not 0, which would read as genesis.
        assert!(v["scan"]["minHeight"].is_null());
        assert!(v["scan"]["maxHeight"].is_null());
        // A real page size, or logicalBytes is meaningless.
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
