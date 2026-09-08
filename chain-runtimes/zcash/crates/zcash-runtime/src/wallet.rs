//! 钱包数据库的打开与初始化。
//!
//! 这里只做三件事：接上存储、组装 `WalletDb`、跑 schema 迁移。
//! 钱包的协议逻辑全部来自 `zcash_client_sqlite` / `zcash_client_backend`，
//! 本 crate 不实现任何 Zcash 语义。

use rand::SeedableRng;
use rand_chacha::ChaCha20Rng;
use zcash_client_sqlite::{wallet::init::WalletMigrator, WalletDb};
use zcash_protocol::consensus::Network;

use crate::clock::HostClock;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::storage;
use serde_json::json;

/// 本 runtime 使用的具体 `WalletDb` 组合。
pub type Db = WalletDb<rusqlite::Connection, Network, HostClock, ChaCha20Rng>;

/// 网络选择。跨 wasm 边界时用字符串，不暴露 Rust 枚举形状。
pub fn parse_network(s: &str) -> Result<Network> {
    match s {
        "main" | "mainnet" => Ok(Network::MainNetwork),
        "test" | "testnet" => Ok(Network::TestNetwork),
        other => Err(RuntimeError::with(
            ErrorCode::InvalidNetwork,
            json!({ "value": other }),
        )),
    }
}

/// 打开（必要时创建）钱包数据库，并把 schema 迁移到当前库版本。
///
/// `init_wallet_db` 是幂等的：每次打开都跑一遍是安全且推荐的做法。
/// 它会跑完 `zcash_client_sqlite` 的全部 migration —— 这也是我们验证
/// SQL 方言与存储层是否真的可用的最强证据。
pub fn open_and_migrate(network: &str, db_name: &str) -> Result<Db> {
    let params = parse_network(network)?;
    let conn = storage::open_connection(db_name)?;

    // RNG 只用于库内部的非密钥用途（如 migration id、临时值）。
    // 密钥材料不经过它，也不经过本 crate 的任何路径。
    let rng = ChaCha20Rng::from_seed(random_seed()?);

    let mut db = WalletDb::from_connection(conn, params, HostClock, rng);

    WalletMigrator::new()
        .with_external_migrations(vec![Box::new(crate::tx_state::Migration)])
        .init_or_migrate(&mut db)
        .map_err(|e| {
            RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "migrate" }))
                .detail(format!("{e:?}"))
        })?;

    Ok(db)
}

/// 32 字节随机种子。wasm 上走宿主的 crypto.getRandomValues。
fn random_seed() -> Result<[u8; 32]> {
    let mut seed = [0u8; 32];
    getrandom::fill(&mut seed).map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "getrandom" }),
        )
        .detail(e)
    })?;
    Ok(seed)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn network_parsing() {
        assert!(matches!(parse_network("main"), Ok(Network::MainNetwork)));
        assert!(matches!(parse_network("test"), Ok(Network::TestNetwork)));
        assert!(parse_network("regtest").is_err());
    }

    #[test]
    fn seed_is_not_all_zero() {
        let s = random_seed().expect("应能取到随机数");
        assert_ne!(s, [0u8; 32]);
    }

    /// 打印迁移后的真实 schema。`storageStats` 的 SQL 必须照着这份写 ——
    /// 表名靠记忆或靠外部文档写，出错时只会安静地返回 0，而不是报错。
    /// 需要 `ZR_DUMP_SCHEMA=1`，未设置时直接跳过。
    #[test]
    fn dump_schema() {
        if std::env::var("ZR_DUMP_SCHEMA")
            .unwrap_or_default()
            .is_empty()
        {
            return;
        }
        let path = std::env::temp_dir().join("zr-schema-dump.sqlite");
        let _ = std::fs::remove_file(&path);
        let db = open_and_migrate("test", path.to_str().unwrap()).expect("迁移");
        drop(db);

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("开连接");
        let mut stmt = conn
            .prepare("SELECT type, name FROM sqlite_master WHERE name NOT LIKE 'sqlite_%' ORDER BY type, name")
            .expect("查 sqlite_master");
        let rows: Vec<(String, String)> = stmt
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?)))
            .expect("遍历")
            .map(|r| r.expect("行"))
            .collect();
        // 统计要按 BLOB 列求和，所以列名也得是实测的。
        for t in [
            "blocks",
            "transactions",
            "sent_notes",
            "orchard_tree_shards",
            "orchard_tree_checkpoints",
            "orchard_tree_retained_checkpoints",
            "orchard_received_notes",
            "orchard_ironwood_migrations",
            "orchard_ironwood_migration_transactions",
            "nullifier_map",
            "tx_locator_map",
            "tx_retrieval_queue",
            "scan_queue",
        ] {
            let mut s = conn
                .prepare(&format!("PRAGMA table_info({t})"))
                .expect("列");
            let cols: Vec<String> = s
                .query_map([], |r| r.get::<_, String>(1))
                .expect("遍历列")
                .map(|r| r.expect("列名"))
                .collect();
            println!("-- {t}: {}", cols.join(", "));
        }

        for kind in ["table", "view", "index", "trigger"] {
            let names: Vec<&str> = rows
                .iter()
                .filter(|(t, _)| t == kind)
                .map(|(_, n)| n.as_str())
                .collect();
            println!("== {kind} ({}) ==", names.len());
            for n in &names {
                println!("  {n}");
            }
        }
        let _ = std::fs::remove_file(&path);
    }
}
