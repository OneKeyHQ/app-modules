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

/// Install the same page-backed VFS in every browser carrier.
/// The pool owns exclusive file handles until its Worker is terminated.
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
    use sqlite_wasm_rs as ffi;
    use sqlite_wasm_vfs::sahpool::{install, OpfsSAHError, OpfsSAHPoolCfg};

    if is_ready() {
        return Ok(());
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
    #[cfg(target_arch = "wasm32")]
    if !memory_mode() {
        // SAH-pool uses rollback journals, not cross-context WAL coordination.
        // Never weaken transaction durability to improve scan throughput.
        conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")?;
    }
    rusqlite::vtab::array::load_module(&conn)?;
    Ok(conn)
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
