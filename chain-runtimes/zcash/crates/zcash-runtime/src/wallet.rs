//! Opening and initializing the wallet database.
//!
//! Connects storage, builds the `WalletDb` and runs schema migrations. All
//! wallet protocol logic comes from `zcash_client_sqlite` / `zcash_client_backend`.

use std::collections::{BTreeSet, HashSet};

use rand::SeedableRng;
use rand_chacha::ChaCha20Rng;
use uuid::Uuid;
use zcash_client_sqlite::{wallet::init::WalletMigrator, WalletDb};
use zcash_protocol::consensus::Network;

use crate::clock::HostClock;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::storage;
use serde_json::json;

/// The concrete `WalletDb` used by this runtime.
pub type Db = WalletDb<rusqlite::Connection, Network, HostClock, ChaCha20Rng>;

/// Parses the network name passed across the wasm boundary.
pub fn parse_network(s: &str) -> Result<Network> {
    match s {
        "main" => Ok(Network::MainNetwork),
        "test" => Ok(Network::TestNetwork),
        other => Err(RuntimeError::with(
            ErrorCode::InvalidNetwork,
            json!({ "value": other }),
        )),
    }
}

/// Opens (creating if needed) the wallet database and migrates it to the
/// current schema. Migration is idempotent and runs on every open.
pub fn open_and_migrate(network: &str, db_name: &str) -> Result<Db> {
    let params = parse_network(network)?;
    let conn = storage::open_connection(db_name)?;
    reject_newer_schema(&conn, params)?;

    // Used only for non-key purposes inside the library (migration ids, nonces).
    // No key material passes through it or through this crate.
    let rng = ChaCha20Rng::from_seed(random_seed()?);

    let mut db = WalletDb::from_connection(conn, params, HostClock, rng);

    migrator().init_or_migrate(&mut db).map_err(|e| {
        RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "migrate" }))
            .detail(format!("{e:?}"))
    })?;

    Ok(db)
}

fn migrator() -> WalletMigrator {
    WalletMigrator::new().with_external_migrations(crate::tx_state::migrations())
}

/// Refuses a database migrated by a newer runtime. Upstream migration skips
/// unknown applied migrations, so an older runtime would run on a schema it
/// does not understand. No migration runs against it.
fn reject_newer_schema(conn: &rusqlite::Connection, params: Network) -> Result<()> {
    let applied = applied_migrations(conn)?;
    if applied.is_empty() {
        return Ok(());
    }
    let known = known_migrations(params)?;
    let unknown: BTreeSet<String> = applied.difference(&known).map(Uuid::to_string).collect();
    if unknown.is_empty() {
        return Ok(());
    }
    Err(RuntimeError::with(
        ErrorCode::DatabaseTooNew,
        json!({ "unknownMigrations": unknown.len() }),
    )
    .detail(unknown.into_iter().collect::<Vec<_>>().join(",")))
}

/// Migration ids recorded in a database; empty before its first migration.
fn applied_migrations(conn: &rusqlite::Connection) -> Result<HashSet<Uuid>> {
    let tracked: bool = conn
        .query_row(
            "SELECT EXISTS (SELECT 1 FROM sqlite_master \
             WHERE type = 'table' AND name = 'schemer_migrations')",
            [],
            |r| r.get(0),
        )
        .map_err(schema_read_err)?;
    if !tracked {
        return Ok(HashSet::new());
    }
    let mut stmt = conn
        .prepare("SELECT id FROM schemer_migrations")
        .map_err(schema_read_err)?;
    let ids = stmt
        .query_map([], |r| r.get::<_, Vec<u8>>(0))
        .map_err(schema_read_err)?;
    let mut applied = HashSet::new();
    for id in ids {
        let id = id.map_err(schema_read_err)?;
        applied.insert(Uuid::from_slice(&id).map_err(schema_read_err)?);
    }
    Ok(applied)
}

fn schema_read_err(e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::DatabaseError,
        json!({ "operation": "readSchemaVersion" }),
    )
    .detail(e)
}

/// Every migration this runtime knows, read back from a fresh in-memory
/// migration once per thread (upstream does not expose the full list).
fn known_migrations(params: Network) -> Result<HashSet<Uuid>> {
    thread_local! {
        static KNOWN: std::cell::RefCell<Option<HashSet<Uuid>>> =
            const { std::cell::RefCell::new(None) };
    }
    if let Some(known) = KNOWN.with(|k| k.borrow().clone()) {
        return Ok(known);
    }
    let reference_err = |detail: String| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "knownMigrations" }),
        )
        .detail(detail)
    };
    let mut conn =
        rusqlite::Connection::open_in_memory().map_err(|e| reference_err(e.to_string()))?;
    {
        let rng = ChaCha20Rng::from_seed(random_seed()?);
        let mut db = WalletDb::from_connection(&mut conn, params, HostClock, rng);
        migrator()
            .init_or_migrate(&mut db)
            .map_err(|e| reference_err(format!("{e:?}")))?;
    }
    let known = applied_migrations(&conn)?;
    KNOWN.with(|k| *k.borrow_mut() = Some(known.clone()));
    Ok(known)
}

/// 32-byte random seed; on wasm this uses the host's crypto.getRandomValues.
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
        assert!(parse_network("mainnet").is_err());
        assert!(parse_network("testnet").is_err());
    }

    #[test]
    fn seed_is_not_all_zero() {
        let s = random_seed().expect("random bytes available");
        assert_ne!(s, [0u8; 32]);
    }

    #[test]
    fn refuses_a_database_migrated_by_a_newer_runtime() {
        let path = std::env::temp_dir().join("zr-newer-schema.sqlite");
        let _ = std::fs::remove_file(&path);
        let name = path.to_str().unwrap();
        drop(open_and_migrate("test", name).expect("create"));
        drop(open_and_migrate("test", name).expect("reopen a current database"));

        let schema = |conn: &rusqlite::Connection| {
            let mut stmt = conn
                .prepare("SELECT sql FROM sqlite_master ORDER BY name")
                .unwrap();
            let sql: Vec<Option<String>> = stmt
                .query_map([], |r| r.get(0))
                .unwrap()
                .map(|r| r.unwrap())
                .collect();
            (applied_migrations(conn).unwrap(), sql)
        };
        let newer = Uuid::from_u128(0x7e57_0000_0000_4000_8000_0000_0000_0001);
        let conn = rusqlite::Connection::open(&path).unwrap();
        conn.execute(
            "INSERT INTO schemer_migrations (id) VALUES (?1)",
            [&newer.as_bytes()[..]],
        )
        .unwrap();
        let before = schema(&conn);
        drop(conn);

        let error = open_and_migrate("test", name).err().expect("refused");
        assert_eq!(error.code, ErrorCode::DatabaseTooNew);
        assert_eq!(error.params["unknownMigrations"], 1);
        assert_eq!(error.detail.as_deref(), Some(newer.to_string().as_str()));
        // No migration ran against the newer schema.
        let conn = rusqlite::Connection::open(&path).unwrap();
        assert_eq!(schema(&conn), before);
        drop(conn);
        let _ = std::fs::remove_file(&path);
    }

    #[test]
    fn known_migrations_match_a_fresh_database() {
        let path = std::env::temp_dir().join("zr-known-migrations.sqlite");
        let _ = std::fs::remove_file(&path);
        drop(open_and_migrate("main", path.to_str().unwrap()).expect("create"));
        let conn = rusqlite::Connection::open(&path).unwrap();
        let applied = applied_migrations(&conn).unwrap();
        assert!(!applied.is_empty());
        assert_eq!(known_migrations(Network::MainNetwork).unwrap(), applied);
        let _ = std::fs::remove_file(&path);
    }

    /// Prints the migrated schema. `storageStats` SQL must follow it: a wrong
    /// table name silently returns 0 instead of failing. Runs only with
    /// `ZR_DUMP_SCHEMA=1`.
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
        let db = open_and_migrate("test", path.to_str().unwrap()).expect("migrate");
        drop(db);

        let conn = crate::storage::open_connection(path.to_str().unwrap()).expect("open");
        let mut stmt = conn
            .prepare("SELECT type, name FROM sqlite_master WHERE name NOT LIKE 'sqlite_%' ORDER BY type, name")
            .expect("query sqlite_master");
        let rows: Vec<(String, String)> = stmt
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?)))
            .expect("iterate rows")
            .map(|r| r.expect("row"))
            .collect();
        // Stats sum BLOB columns, so column names must come from the real schema.
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
                .expect("table_info");
            let cols: Vec<String> = s
                .query_map([], |r| r.get::<_, String>(1))
                .expect("iterate columns")
                .map(|r| r.expect("column name"))
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
