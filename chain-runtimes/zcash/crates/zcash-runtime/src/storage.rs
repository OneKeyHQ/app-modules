//! Shared SQLite storage for every App carrier.
//!
//! WASM runs in a dedicated Worker and uses the same OPFS synchronous-access
//! VFS on desktop, web, extension, and mobile WebEmbed. SQLite accesses pages
//! directly instead of preloading a complete IndexedDB mirror into WASM memory.
//! Native builds remain available for Rust tests and diagnostics.

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

/// The OPFS API the pool needs, checked before installing it: on an outdated
/// WebView the install would otherwise throw a bare TypeError from inside the
/// bindings, which the host cannot tell apart from any other failure.
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
    // Worker termination is asynchronous in browsers. Retry only acquiring
    // ownership, before opening SQLite or dispatching any wallet operation.
    // A live owner still fails after the bounded handoff window.
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

/// SQLite COMMIT synchronously calls this VFS's xSync under synchronous=FULL.
/// There is no asynchronous write queue to drain. Retain the explicit boundary
/// used by transaction finalization and broadcasting; commit errors propagate
/// from the SQLite operation before this point.
pub async fn durability_barrier() -> Result<()> {
    if requires_worker_restart() {
        return Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "reason": "storageRequiresWorkerRestart" }),
        ));
    }
    if !memory_mode() && !is_ready() {
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

#[cfg(target_arch = "wasm32")]
static MEMORY_MODE: AtomicBool = AtomicBool::new(false);

/// Diagnostic-only mode, selected before installing a persistent VFS.
#[cfg(target_arch = "wasm32")]
pub fn use_memory_storage() {
    MEMORY_MODE.store(true, Ordering::SeqCst);
}

#[cfg(not(target_arch = "wasm32"))]
pub fn use_memory_storage() {}

#[cfg(target_arch = "wasm32")]
fn memory_mode() -> bool {
    MEMORY_MODE.load(Ordering::SeqCst)
}

#[cfg(not(target_arch = "wasm32"))]
fn memory_mode() -> bool {
    true
}

pub fn open_connection(db_name: &str) -> Result<rusqlite::Connection> {
    if !memory_mode() && !is_ready() {
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
    #[cfg(target_arch = "wasm32")]
    if !memory_mode() {
        // SAH-pool uses rollback journals, not cross-context WAL coordination.
        // Never weaken transaction durability to improve scan throughput.
        conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")?;
    }
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
/// many it reclaimed. Rescans and removals otherwise leave a file that only
/// grows. Databases created before auto_vacuum was set report 0.
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
    if memory_mode() {
        return Ok(false);
    }
    persistent_vfs()?
        .exists(db_name)
        .map_err(|error| db_storage_err("inspectDatabase", db_name, error))
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn database_exists(db_name: &str) -> Result<bool> {
    Ok(std::path::Path::new(db_name).exists())
}

/// Delete a closed database. Callers must first resolve active transactions;
/// pending broadcast state must not be discarded as rebuildable scan cache.
#[cfg(target_arch = "wasm32")]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    if memory_mode() {
        return Ok(false);
    }
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
    use super::*;

    fn page_counts(conn: &rusqlite::Connection) -> (i64, i64) {
        let pages = conn
            .query_row("PRAGMA page_count", [], |r| r.get(0))
            .unwrap();
        let free = conn
            .query_row("PRAGMA freelist_count", [], |r| r.get(0))
            .unwrap();
        (pages, free)
    }

    /// A rescan or removal frees pages that only a reclaim gives back.
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

    /// Too few free pages to be worth a write transaction.
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
