//! Opening and initializing the wallet database: storage, `WalletDb` and schema migrations.

use std::collections::{BTreeSet, HashSet};

use rand::SeedableRng;
use rand_chacha::ChaCha20Rng;
use schemerz_rusqlite::RusqliteAdapter;
use uuid::Uuid;
use zcash_client_sqlite::wallet::init::WalletMigrationError;
use zcash_client_sqlite::{wallet::init::WalletMigrator, WalletDb};
use zcash_protocol::consensus::Network;

use crate::clock::HostClock;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::storage;
use serde_json::json;

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
    let mut conn = storage::open_connection(db_name)?;
    reject_newer_schema(&conn, params)?;
    initialize_migration_table(&mut conn)?;

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

fn initialize_migration_table(conn: &mut rusqlite::Connection) -> Result<()> {
    // Upstream expects this initialization to succeed; storage failures must remain errors.
    RusqliteAdapter::<'_, WalletMigrationError>::new(conn, Some("schemer_migrations".to_string()))
        .init()
        .map_err(|error| {
            RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "migrate" }))
                .detail(error)
        })
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
    fn migration_table_storage_failures_are_errors_without_a_panic() {
        for pragma in ["PRAGMA max_page_count = 1", "PRAGMA query_only = ON"] {
            let mut conn = rusqlite::Connection::open_in_memory().unwrap();
            conn.execute_batch(pragma).unwrap();
            let error = initialize_migration_table(&mut conn).unwrap_err();
            assert_eq!(error.code, ErrorCode::DatabaseError);
            assert_eq!(error.params["operation"], "migrate");
            let tables: i64 = conn
                .query_row(
                    "SELECT count(*) FROM sqlite_master WHERE name = 'schemer_migrations'",
                    [],
                    |row| row.get(0),
                )
                .unwrap();
            assert_eq!(tables, 0);
        }
    }

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
}
