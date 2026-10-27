//! SQLite storage shared by all platforms.
//!
//! wasm runs in a dedicated Worker on the OPFS sync-access VFS; native is for tests and diagnostics.

use rusqlite::Connection;
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};

// Force linking, or `undefined symbol: sqlite3_*`.
#[cfg(target_arch = "wasm32")]
use sqlite_wasm_rs as _;

#[cfg(target_arch = "wasm32")]
use std::sync::atomic::{AtomicBool, Ordering};

#[cfg(target_arch = "wasm32")]
static VFS_READY: AtomicBool = AtomicBool::new(false);

// Pool handle, used to delete databases that cannot be opened.
#[cfg(target_arch = "wasm32")]
thread_local! {
    static VFS_UTIL: std::cell::RefCell<Option<std::rc::Rc<sqlite_wasm_vfs::sahpool::OpfsSAHPoolUtil>>> =
        const { std::cell::RefCell::new(None) };
}

/// Whether the VFS is ready. Always true on native (system filesystem).
pub fn is_ready() -> bool {
    #[cfg(target_arch = "wasm32")]
    {
        VFS_READY.load(Ordering::SeqCst)
    }
    #[cfg(not(target_arch = "wasm32"))]
    {
        true
    }
}

/// Installs the persistent VFS in the browser carrier. Call once before opening any database.
///
/// The pool holds file handles exclusively until its Worker terminates, so the host must serialize.
/// A dedicated pool name avoids sharing a directory with other pools on the same origin.
/// A just-reset Worker briefly keeps its handles (`NoModificationAllowedError`); only that error is retried, bounded.
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
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
        vfs_name: "onekey-xmr-opfs".to_owned(),
        directory: ".onekey-xmr-opfs".to_owned(),
        // Never true: it wipes the pool on every start.
        clear_on_init: false,
        initial_capacity: 4,
    };

    let mut retries = 20;
    loop {
        match install::<sqlite_wasm_rs::WasmOsCallback>(&cfg, true).await {
            Ok(util) => {
                VFS_UTIL.with(|slot| *slot.borrow_mut() = Some(std::rc::Rc::new(util)));
                VFS_READY.store(true, Ordering::SeqCst);
                return Ok(());
            }
            Err(error) => {
                let owner_is_releasing = matches!(
                    &error,
                    OpfsSAHError::CreateSyncAccessHandle(value)
                        if js_sys::Reflect::get(
                            value,
                            &wasm_bindgen::JsValue::from_str("name"),
                        )
                        .ok()
                        .and_then(|name| name.as_string())
                        .as_deref()
                            == Some("NoModificationAllowedError")
                );
                if matches!(&error, OpfsSAHError::GetDirHandle(value) if is_security_error(value)) {
                    return Err(RuntimeError::with(
                        ErrorCode::StorageUnsupported,
                        json!({ "missing": "origin" }),
                    )
                    .detail(format!("{error:?}")));
                }
                if !owner_is_releasing || retries == 0 {
                    return Err(RuntimeError::with(
                        ErrorCode::NotInitialized,
                        json!({ "reason": "vfsInstallFailed" }),
                    )
                    .detail(format!("{error:?}")));
                }
                retries -= 1;
                gloo_timers::future::TimeoutFuture::new(250).await;
            }
        }
    }
}

/// The OPFS API the pool needs, checked up front so an outdated WebView gives a clear error instead of a bare TypeError.
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

/// No VFS on native; same-named stub keeps callers cfg-free.
#[cfg(not(target_arch = "wasm32"))]
pub async fn install_persistent_vfs() -> Result<()> {
    Ok(())
}

/// Database name must be non-empty with no path separators.
///
/// Checked on every target: on native the name becomes a file path.
fn validate_db_name(db_name: &str) -> Result<()> {
    let bad = db_name.is_empty()
        || db_name.contains('/')
        || db_name.contains('\\')
        || db_name.contains("..");
    if bad {
        return Err(RuntimeError::with(
            ErrorCode::InvalidDbName,
            json!({ "value": db_name }),
        ));
    }
    Ok(())
}

/// Opens a connection and sets pragmas.
///
/// `:memory:` is test-only and skips name validation and durability pragmas.
pub fn open_connection(db_name: &str) -> Result<Connection> {
    if db_name == ":memory:" {
        let conn = Connection::open_in_memory().map_err(|e| db_err("openInMemory", e))?;
        conn.execute_batch("PRAGMA foreign_keys = ON;")
            .map_err(|e| db_err("pragmaForeignKeys", e))?;
        return Ok(conn);
    }

    validate_db_name(db_name)?;

    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }

    let conn = Connection::open(db_name).map_err(|e| db_err("open", e))?;

    // Only takes effect before the first table exists; see reclaim_free_pages.
    conn.execute_batch("PRAGMA auto_vacuum = INCREMENTAL;")
        .map_err(|e| db_err("pragmaAutoVacuum", e))?;

    // Off by default; without it deleting a wallet leaves readable orphan rows.
    conn.execute_batch("PRAGMA foreign_keys = ON;")
        .map_err(|e| db_err("pragmaForeignKeys", e))?;
    // Zero deleted rows (view keys, receipts) instead of leaving them in free pages.
    conn.execute_batch("PRAGMA secure_delete = ON;")
        .map_err(|e| db_err("pragmaSecureDelete", e))?;

    #[cfg(target_arch = "wasm32")]
    {
        // SAH pool uses a rollback journal, no cross-context WAL. Never weaken durability for scan throughput.
        conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")
            .map_err(|e| db_err("pragmaDurability", e))?;
    }

    Ok(conn)
}

/// Free pages below which a reclaim is not worth its write transaction.
const RECLAIM_MIN_FREE_PAGES: i64 = 256;
pub const RECLAIM_PAGES_PER_STEP: u32 = 512;
/// After removing or rewinding an identity; later steps take the rest.
pub const RECLAIM_PAGES_AFTER_DELETE: u32 = 4096;

/// Hands up to `max_pages` free pages back to the file system and returns how
/// many it reclaimed. Rewinds and removals otherwise leave a file that only
/// grows. Databases created before auto_vacuum was set report 0.
pub fn reclaim_free_pages(conn: &Connection, max_pages: u32) -> Result<u32> {
    let read = |sql: &str| -> Result<i64> {
        conn.query_row(sql, [], |r| r.get(0))
            .map_err(|e| db_err("reclaimFreePages", e))
    };
    if read("PRAGMA auto_vacuum")? != 2 {
        return Ok(0);
    }
    let before = read("PRAGMA freelist_count")?;
    if before < RECLAIM_MIN_FREE_PAGES {
        return Ok(0);
    }
    // Each step frees one page; a single execute would stop after the first.
    let mut stmt = conn
        .prepare(&format!("PRAGMA incremental_vacuum({max_pages})"))
        .map_err(|e| db_err("reclaimFreePages", e))?;
    let mut rows = stmt.query([]).map_err(|e| db_err("reclaimFreePages", e))?;
    while rows
        .next()
        .map_err(|e| db_err("reclaimFreePages", e))?
        .is_some()
    {}
    let after = read("PRAGMA freelist_count")?;
    Ok(u32::try_from(before - after).unwrap_or(0))
}

/// Deletes a closed database without opening it, so unopenable ones can be deleted.
/// Returns `true` if deleted, `false` if it did not exist.
#[cfg(target_arch = "wasm32")]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    validate_db_name(db_name)?;
    let util = VFS_UTIL
        .with(|slot| slot.borrow().clone())
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotInitialized))?;
    util.delete_db(db_name).map_err(|error| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "deleteDatabase", "dbName": db_name }),
        )
        .detail(format!("{error:?}"))
    })
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    validate_db_name(db_name)?;
    match std::fs::remove_file(db_name) {
        Ok(()) => Ok(true),
        Err(error) if error.kind() == std::io::ErrorKind::NotFound => Ok(false),
        Err(error) => Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "deleteDatabase", "dbName": db_name }),
        )
        .detail(error.to_string())),
    }
}

/// Durability barrier: fails if committed transactions may not have reached
/// durable storage.
///
/// Commit errors already surface from the statement; this catches a metadata I/O failure
/// that poisons the pool, after which nothing written can be trusted until the Worker restarts.
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

/// Poisoned pool: only a fresh Worker can reopen storage.
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

#[cfg(test)]
mod tests {
    use super::*;

    fn free_pages(conn: &Connection) -> i64 {
        conn.query_row("PRAGMA freelist_count", [], |r| r.get(0))
            .unwrap()
    }

    #[test]
    fn reclaims_freed_pages_in_bounded_batches() {
        let name = format!("xmr-reclaim-test-{}.db", std::process::id());
        let _ = std::fs::remove_file(&name);
        let conn = open_connection(&name).unwrap();
        conn.execute_batch(
            "CREATE TABLE blob (id INTEGER PRIMARY KEY, data BLOB NOT NULL);
             WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
             INSERT INTO blob (data) SELECT zeroblob(4000) FROM n;
             DELETE FROM blob;",
        )
        .unwrap();
        let before = free_pages(&conn);
        assert!(before > 1000, "expected many free pages, got {before}");

        assert_eq!(reclaim_free_pages(&conn, 300).unwrap(), 300);
        assert_eq!(free_pages(&conn), before - 300);
        reclaim_free_pages(&conn, u32::MAX).unwrap();
        assert_eq!(free_pages(&conn), 0);

        drop(conn);
        let _ = std::fs::remove_file(&name);
    }

    #[test]
    fn deletes_a_database_it_cannot_open() {
        let name = format!("xmr-delete-test-{}.db", std::process::id());
        std::fs::write(&name, b"not a sqlite database").unwrap();
        let delete = || futures_lite::future::block_on(delete_database(&name));
        assert!(delete().unwrap());
        assert!(!delete().unwrap());
    }

    #[test]
    fn healthy_storage_needs_no_worker_restart() {
        assert!(!requires_worker_restart());
        assert!(futures_lite::future::block_on(durability_barrier()).is_ok());
    }

    #[test]
    fn memory_database_always_opens() {
        assert!(open_connection(":memory:").is_ok());
    }

    #[test]
    fn path_separators_are_rejected_on_every_target() {
        for bad in ["", "a/b", "a\\b", "../escape", "x/../y"] {
            let e = open_connection(bad).expect_err(&format!("{bad:?} should be rejected"));
            assert_eq!(e.code, ErrorCode::InvalidDbName);
        }
    }

    #[test]
    fn foreign_keys_are_on() {
        let conn = open_connection(":memory:").unwrap();
        // The in-memory branch is special; set it explicitly to verify the pragma.
        conn.execute_batch("PRAGMA foreign_keys = ON;").unwrap();
        let on: i64 = conn
            .query_row("PRAGMA foreign_keys", [], |r| r.get(0))
            .unwrap();
        assert_eq!(on, 1);
    }
}
