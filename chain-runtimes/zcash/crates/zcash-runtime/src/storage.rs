//! Shared SQLite storage: an OPFS sync-access VFS in a Worker for every browser
//! carrier. Native builds are for Rust tests and diagnostics.

use crate::error::{ErrorCode, Result, RuntimeError};
use serde_json::json;

#[cfg(target_arch = "wasm32")]
use sqlite_wasm_rs as _;

#[cfg(target_arch = "wasm32")]
use std::sync::atomic::{AtomicBool, Ordering};

#[cfg(target_arch = "wasm32")]
static VFS_READY: AtomicBool = AtomicBool::new(false);

#[cfg(target_arch = "wasm32")]
thread_local! {
    static VFS_UTIL: std::cell::RefCell<
        Option<std::rc::Rc<sqlite_wasm_vfs::sahpool::OpfsSAHPoolUtil>>,
    > = const { std::cell::RefCell::new(None) };
}

#[cfg(target_arch = "wasm32")]
fn persistent_vfs() -> Result<std::rc::Rc<sqlite_wasm_vfs::sahpool::OpfsSAHPoolUtil>> {
    VFS_UTIL.with(|slot| slot.borrow().clone()).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::NotInitialized,
            json!({ "reason": "vfsNotInstalled" }),
        )
    })
}

/// The OPFS API the pool needs, checked before install: an outdated WebView would
/// otherwise throw a bare TypeError the host cannot tell apart from other failures.
#[cfg(target_arch = "wasm32")]
fn missing_opfs_api() -> Option<&'static str> {
    use wasm_bindgen::JsValue;
    let get = |target: &JsValue, key: &str| {
        js_sys::Reflect::get(target, &JsValue::from_str(key))
            .ok()
            .filter(|value| !value.is_undefined() && !value.is_null())
    };
    let global: JsValue = js_sys::global().into();
    let has_get_directory = get(&global, "navigator")
        .and_then(|navigator| get(&navigator, "storage"))
        .and_then(|storage| get(&storage, "getDirectory"))
        .is_some();
    if !has_get_directory {
        return Some("getDirectory");
    }
    let has_sync_access_handle = get(&global, "FileSystemFileHandle")
        .and_then(|handle| get(&handle, "prototype"))
        .and_then(|prototype| get(&prototype, "createSyncAccessHandle"))
        .is_some();
    if !has_sync_access_handle {
        return Some("createSyncAccessHandle");
    }
    None
}

/// An opaque origin (a file:// page) has the API but refuses to use it.
#[cfg(target_arch = "wasm32")]
fn is_security_error(value: &wasm_bindgen::JsValue) -> bool {
    js_sys::Reflect::get(value, &wasm_bindgen::JsValue::from_str("name"))
        .ok()
        .and_then(|name| name.as_string())
        .as_deref()
        == Some("SecurityError")
}

/// Install the same page-backed VFS in every browser carrier.
/// The pool owns exclusive file handles until its Worker is terminated.
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
    use sqlite_wasm_rs as ffi;
    use sqlite_wasm_vfs::sahpool::{install, OpfsSAHError, OpfsSAHPoolCfg};

    if is_ready() {
        return Ok(());
    }
    if let Some(missing) = missing_opfs_api() {
        return Err(RuntimeError::with(
            ErrorCode::StorageUnsupported,
            json!({ "missing": missing }),
        ));
    }
    let cfg = OpfsSAHPoolCfg {
        vfs_name: "onekey-zcash-opfs".to_owned(),
        directory: ".onekey-zcash-opfs".to_owned(),
        clear_on_init: false,
        // Two network databases plus their rollback journals and spare handles.
        initial_capacity: 6,
    };
    // Worker termination is asynchronous: retry only acquiring ownership, before
    // opening SQLite. A live owner still fails after the bounded handoff window.
    let mut retries = 20;
    let util = loop {
        match install::<ffi::WasmOsCallback>(&cfg, true).await {
            Ok(util) => break util,
            Err(error) => {
                let owner_releasing = match &error {
                    OpfsSAHError::CreateSyncAccessHandle(value) => {
                        js_sys::Reflect::get(value, &wasm_bindgen::JsValue::from_str("name"))
                            .ok()
                            .and_then(|name| name.as_string())
                            .as_deref()
                            == Some("NoModificationAllowedError")
                    }
                    _ => false,
                };
                if matches!(&error, OpfsSAHError::GetDirHandle(value) if is_security_error(value)) {
                    return Err(RuntimeError::with(
                        ErrorCode::StorageUnsupported,
                        json!({ "missing": "origin" }),
                    )
                    .detail(format!("{error:?}")));
                }
                if !owner_releasing || retries == 0 {
                    return Err(
                        RuntimeError::new(ErrorCode::NotInitialized).detail(format!("{error:?}"))
                    );
                }
                retries -= 1;
                gloo_timers::future::TimeoutFuture::new(250).await;
            }
        }
    };
    VFS_UTIL.with(|slot| *slot.borrow_mut() = Some(std::rc::Rc::new(util)));
    VFS_READY.store(true, Ordering::SeqCst);
    Ok(())
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn install_persistent_vfs() -> Result<()> {
    Ok(())
}

/// COMMIT already syncs through the VFS under synchronous=FULL, so there is no
/// write queue to drain; this only checks the storage is still usable.
pub async fn durability_barrier() -> Result<()> {
    if requires_worker_restart() {
        return Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "reason": "storageRequiresWorkerRestart" }),
        ));
    }
    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }
    Ok(())
}

#[cfg(target_arch = "wasm32")]
pub fn requires_worker_restart() -> bool {
    VFS_UTIL.with(|slot| {
        slot.borrow()
            .as_ref()
            .is_some_and(|util| util.is_poisoned())
    })
}

#[cfg(not(target_arch = "wasm32"))]
pub fn requires_worker_restart() -> bool {
    false
}

#[cfg(target_arch = "wasm32")]
pub fn is_ready() -> bool {
    VFS_READY.load(Ordering::SeqCst)
}

#[cfg(not(target_arch = "wasm32"))]
pub fn is_ready() -> bool {
    true
}

pub fn open_connection(db_name: &str) -> Result<rusqlite::Connection> {
    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }
    let bad_name = db_name.is_empty()
        || (cfg!(target_arch = "wasm32") && (db_name.contains('/') || db_name.contains('\\')));
    if bad_name {
        return Err(RuntimeError::with(
            ErrorCode::InvalidDbName,
            json!({ "value": db_name }),
        ));
    }
    let conn = rusqlite::Connection::open(db_name)?;
    // Only takes effect before the first table exists, so every database
    // created from here on can hand freed pages back; see reclaim_free_pages.
    conn.execute_batch("PRAGMA auto_vacuum = INCREMENTAL;")?;
    // Zero deleted rows (viewing keys, notes, memos) instead of leaving them
    // in free pages; per connection, so every open needs it.
    conn.execute_batch("PRAGMA secure_delete = ON;")?;
    // SAH-pool uses rollback journals, not cross-context WAL coordination.
    // Never weaken transaction durability to improve scan throughput.
    #[cfg(target_arch = "wasm32")]
    conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")?;
    rusqlite::vtab::array::load_module(&conn)?;
    Ok(conn)
}

/// Free pages below which a reclaim is not worth its write transaction.
const RECLAIM_MIN_FREE_PAGES: i64 = 256;
/// Per sync step: small enough never to hold the step up noticeably.
pub const RECLAIM_PAGES_PER_STEP: u32 = 512;
/// After removing or rewinding an account; later steps take the rest.
pub const RECLAIM_PAGES_AFTER_DELETE: u32 = 4096;

/// Hands up to `max_pages` free pages back to the file system and returns how
/// many it reclaimed. Databases created before auto_vacuum was set report 0.
pub fn reclaim_free_pages(conn: &rusqlite::Connection, max_pages: u32) -> Result<u32> {
    let mode: i64 = conn.query_row("PRAGMA auto_vacuum", [], |r| r.get(0))?;
    // 2 = INCREMENTAL.
    if mode != 2 {
        return Ok(0);
    }
    let before: i64 = conn.query_row("PRAGMA freelist_count", [], |r| r.get(0))?;
    if before < RECLAIM_MIN_FREE_PAGES {
        return Ok(0);
    }
    // Each step frees one page; a single execute would stop after the first.
    let mut stmt = conn.prepare(&format!("PRAGMA incremental_vacuum({max_pages})"))?;
    let mut rows = stmt.query([])?;
    while rows.next()?.is_some() {}
    let after: i64 = conn.query_row("PRAGMA freelist_count", [], |r| r.get(0))?;
    Ok(u32::try_from(before - after).unwrap_or(0))
}

/// Inspect the pool's file table without opening or creating a database.
#[cfg(target_arch = "wasm32")]
pub async fn database_exists(db_name: &str) -> Result<bool> {
    persistent_vfs()?
        .exists(db_name)
        .map_err(|error| db_storage_err("inspectDatabase", db_name, error))
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn database_exists(db_name: &str) -> Result<bool> {
    Ok(std::path::Path::new(db_name).exists())
}

#[cfg(target_arch = "wasm32")]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    persistent_vfs()?
        .delete_db(db_name)
        .map_err(|error| db_storage_err("deleteDatabase", db_name, error))
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    match std::fs::remove_file(db_name) {
        Ok(()) => Ok(true),
        Err(error) if error.kind() == std::io::ErrorKind::NotFound => Ok(false),
        Err(error) => Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "deleteDatabase", "dbName": db_name }),
        )
        .detail(error)),
    }
}

#[cfg(target_arch = "wasm32")]
fn db_storage_err(operation: &str, db_name: &str, error: impl std::fmt::Debug) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::DatabaseError,
        json!({ "operation": operation, "dbName": db_name }),
    )
    .detail(format!("{error:?}"))
}

#[cfg(test)]
mod tests {
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use zcash_client_sqlite::{wallet::init::WalletMigrator, WalletDb};
    use zcash_protocol::consensus::Network;

    use super::*;
    use crate::clock::HostClock;

    fn page_counts(conn: &rusqlite::Connection) -> (i64, i64) {
        let pages = conn
            .query_row("PRAGMA page_count", [], |r| r.get(0))
            .unwrap();
        let free = conn
            .query_row("PRAGMA freelist_count", [], |r| r.get(0))
            .unwrap();
        (pages, free)
    }

    #[test]
    fn reclaims_freed_pages_in_bounded_batches() {
        let path = std::env::temp_dir().join("zr-reclaim-free-pages.sqlite");
        let _ = std::fs::remove_file(&path);
        let conn = open_connection(path.to_str().unwrap()).expect("open");
        conn.execute_batch(
            "CREATE TABLE blob (id INTEGER PRIMARY KEY, data BLOB NOT NULL);
             WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
             INSERT INTO blob (data) SELECT zeroblob(4000) FROM n;
             DELETE FROM blob;",
        )
        .unwrap();
        let (pages_before, free_before) = page_counts(&conn);
        assert!(
            free_before > 1000,
            "expected many free pages, got {free_before}"
        );

        assert_eq!(reclaim_free_pages(&conn, 300).unwrap(), 300);
        assert_eq!(page_counts(&conn).0, pages_before - 300);

        reclaim_free_pages(&conn, u32::MAX).unwrap();
        let (_, free_after) = page_counts(&conn);
        assert_eq!(free_after, 0);
        let _ = std::fs::remove_file(&path);
    }

    #[test]
    fn leaves_a_small_freelist_alone() {
        let path = std::env::temp_dir().join("zr-reclaim-small-freelist.sqlite");
        let _ = std::fs::remove_file(&path);
        let conn = open_connection(path.to_str().unwrap()).expect("open");
        conn.execute_batch(
            "CREATE TABLE blob (id INTEGER PRIMARY KEY, data BLOB NOT NULL);
             INSERT INTO blob (data) VALUES (zeroblob(40000));
             DELETE FROM blob;",
        )
        .unwrap();
        assert!(page_counts(&conn).1 > 0);
        assert_eq!(reclaim_free_pages(&conn, u32::MAX).unwrap(), 0);
        let _ = std::fs::remove_file(&path);
    }

    fn secure_delete(conn: &rusqlite::Connection) -> i64 {
        conn.query_row("PRAGMA secure_delete", [], |r| r.get(0))
            .unwrap()
    }

    /// The pragma is per connection: the wallet's own, the separate read and
    /// reclaim connections, and every reopen after a migration.
    #[test]
    fn every_connection_turns_on_secure_delete() {
        let path = std::env::temp_dir().join("zr-secure-delete-connections.sqlite");
        let _ = std::fs::remove_file(&path);
        let name = path.to_str().unwrap();

        // Same steps as wallet::open_and_migrate; migrations must not turn the pragma off.
        for _ in 0..2 {
            let mut conn = open_connection(name).expect("open");
            {
                let rng = ChaCha20Rng::from_seed([7; 32]);
                let mut db =
                    WalletDb::from_connection(&mut conn, Network::TestNetwork, HostClock, rng);
                WalletMigrator::new()
                    .with_external_migrations(crate::tx_state::migrations())
                    .init_or_migrate(&mut db)
                    .unwrap();
            }
            assert_eq!(secure_delete(&conn), 1);
        }

        let db = crate::wallet::open_and_migrate("test", name).expect("reopen");
        let read_conn = open_connection(name).expect("open");
        assert_eq!(secure_delete(&read_conn), 1);
        drop(read_conn);
        drop(db);
        let _ = std::fs::remove_file(&path);
    }

    /// Deleting a row must not leave its bytes readable in the file.
    #[test]
    fn deleted_rows_leave_no_plaintext_in_the_file() {
        const MARKER: &str = "zr-secure-delete-marker-viewing-key";
        // Larger than a page: the overflow pages it frees are zeroed only by
        // a full secure_delete, not by the FAST mode some hosts default to.
        let secret = MARKER.repeat(400);
        let write_and_delete = |path: &std::path::Path, secure_delete: bool| {
            let _ = std::fs::remove_file(path);
            let conn = open_connection(path.to_str().unwrap()).expect("open");
            if !secure_delete {
                conn.execute_batch("PRAGMA secure_delete = OFF;").unwrap();
            }
            conn.execute_batch(
                "CREATE TABLE secret (id INTEGER PRIMARY KEY, value TEXT NOT NULL);
                 INSERT INTO secret (value) VALUES ('kept');",
            )
            .unwrap();
            conn.execute("INSERT INTO secret (value) VALUES (?1)", [&secret])
                .unwrap();
            conn.execute("DELETE FROM secret WHERE value = ?1", [&secret])
                .unwrap();
        };
        let contains_marker = |path: &std::path::Path| {
            let bytes = std::fs::read(path).unwrap();
            let _ = std::fs::remove_file(path);
            bytes
                .windows(MARKER.len())
                .any(|window| window == MARKER.as_bytes())
        };

        // Control: the same steps with the pragma off keep the bytes, so the
        // check below can tell the two apart.
        let plain = std::env::temp_dir().join("zr-secure-delete-plain.sqlite");
        write_and_delete(&plain, false);
        assert!(contains_marker(&plain));

        let zeroed = std::env::temp_dir().join("zr-secure-delete-zeroed.sqlite");
        write_and_delete(&zeroed, true);
        assert!(!contains_marker(&zeroed));
    }

    /// auto_vacuum is fixed when the first table is created; an older file
    /// keeps its mode and is never touched.
    #[test]
    fn leaves_databases_without_incremental_vacuum_alone() {
        let path = std::env::temp_dir().join("zr-reclaim-legacy.sqlite");
        let _ = std::fs::remove_file(&path);
        {
            let legacy = rusqlite::Connection::open(&path).unwrap();
            legacy
                .execute_batch(
                    "CREATE TABLE blob (id INTEGER PRIMARY KEY, data BLOB NOT NULL);
                     WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
                     INSERT INTO blob (data) SELECT zeroblob(4000) FROM n;
                     DELETE FROM blob;",
                )
                .unwrap();
        }
        let conn = open_connection(path.to_str().unwrap()).expect("open");
        let free = page_counts(&conn).1;
        assert_eq!(reclaim_free_pages(&conn, u32::MAX).unwrap(), 0);
        assert_eq!(page_counts(&conn).1, free);
        let _ = std::fs::remove_file(&path);
    }
}
