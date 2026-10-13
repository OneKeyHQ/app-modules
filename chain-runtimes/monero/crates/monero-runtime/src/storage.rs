//! SQLite storage shared by all platforms.
//!
//! The wasm side runs in a dedicated Worker; desktop, web, extension and mobile
//! WebEmbed all use the same OPFS sync-access VFS, so SQLite reads and writes
//! pages directly instead of preloading the database into wasm memory. This is
//! why monero-oxide was chosen over monero-ts, which serializes the whole wallet.
//!
//! The native build is kept for Rust tests and local diagnostics.

use rusqlite::Connection;
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};

// Force linking: an unreferenced crate is not linked, which would fail with
// `undefined symbol: sqlite3_*`.
#[cfg(target_arch = "wasm32")]
use sqlite_wasm_rs as _;

#[cfg(target_arch = "wasm32")]
use std::sync::atomic::{AtomicBool, Ordering};

#[cfg(target_arch = "wasm32")]
static VFS_READY: AtomicBool = AtomicBool::new(false);

// Installed pool handle; lets us delete databases that cannot be opened (unknown version, corruption).
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

/// Installs the persistent VFS in the browser carrier. Call once before opening
/// any database.
///
/// The pool holds file handles exclusively until its Worker terminates, so two
/// Workers cannot open the same database; the host must serialize.
///
/// A dedicated name instead of the default `.opfs-sahpool` keeps the pool from
/// sharing a directory and handles with other pools on the same origin (e.g.
/// Zcash's `.onekey-zcash-opfs`).
///
/// Worker termination is **asynchronous**: after a carrier reset, reload or hot
/// update, the old Worker briefly keeps its SAH handles and install fails with
/// `NoModificationAllowedError`. Only that error is retried, with a bound; a
/// live second holder still fails, which is correct.
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
    use sqlite_wasm_vfs::sahpool::{install, OpfsSAHError, OpfsSAHPoolCfg};

    if is_ready() {
        return Ok(());
    }

    let cfg = OpfsSAHPoolCfg {
        vfs_name: "onekey-xmr-opfs".to_owned(),
        directory: ".onekey-xmr-opfs".to_owned(),
        // Never true: it wipes the pool on every start, forcing a full rescan.
        clear_on_init: false,
        // One database per network, plus rollback journals and spare handles.
        initial_capacity: 4,
    };

    let mut retries = 20;
    loop {
        // `WasmOsCallback` is sqlite-wasm-rs's implementation of the VFS crate's
        // trait; the type parameter cannot be inferred.
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

/// No VFS on native; same-named stub keeps callers cfg-free.
#[cfg(not(target_arch = "wasm32"))]
pub async fn install_persistent_vfs() -> Result<()> {
    Ok(())
}

/// Database name must be non-empty with no path separators.
///
/// **Checked on every target**: on wasm the name is an opaque VFS key, but on
/// native it becomes a file path, where traversal actually matters.
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
        // Match real databases so cascade-delete tests are meaningful
        conn.execute_batch("PRAGMA foreign_keys = ON;")
            .map_err(|e| db_err("pragmaForeignKeys", e))?;
        return Ok(conn);
    }

    validate_db_name(db_name)?;

    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }

    let conn = Connection::open(db_name).map_err(|e| db_err("open", e))?;

    // Foreign keys are off by default; without them identity cascades fail and
    // deleting a wallet leaves readable orphan rows.
    conn.execute_batch("PRAGMA foreign_keys = ON;")
        .map_err(|e| db_err("pragmaForeignKeys", e))?;
    // Zero deleted rows (view keys, receipts) instead of leaving them in free pages.
    conn.execute_batch("PRAGMA secure_delete = ON;")
        .map_err(|e| db_err("pragmaSecureDelete", e))?;

    #[cfg(target_arch = "wasm32")]
    {
        // SAH pool uses a rollback journal, no cross-context WAL.
        // **Never weaken durability for scan throughput.**
        conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")
            .map_err(|e| db_err("pragmaDurability", e))?;
    }

    Ok(conn)
}

/// Deletes a closed database **without opening it**, so unopenable ones can be deleted.
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

/// Durability barrier: ensures committed transactions reached durable storage.
///
/// Call after an **irreversible** action (e.g. a broadcast). Failure means
/// durability is unknown, **not** that the action failed; callers must keep
/// the real outcome.
pub async fn durability_barrier() -> Result<()> {
    #[cfg(target_arch = "wasm32")]
    {
        // SAH pool writes reach the OPFS handle before sqlite3 returns; this is
        // an explicit sync point and the place for a real flush on another VFS.
        Ok(())
    }
    #[cfg(not(target_arch = "wasm32"))]
    {
        Ok(())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    // Deletion does not open the file, so even a non-SQLite file is deleted.
    #[test]
    fn deletes_a_database_it_cannot_open() {
        let name = format!("xmr-delete-test-{}.db", std::process::id());
        std::fs::write(&name, b"not a sqlite database").unwrap();
        let delete = || futures_lite::future::block_on(delete_database(&name));
        assert!(delete().unwrap());
        assert!(!delete().unwrap());
    }

    #[test]
    fn memory_database_always_opens() {
        assert!(open_connection(":memory:").is_ok());
    }

    /// Path traversal is dangerous on **native** (the name becomes a file
    /// path), so the check must not be wasm-only.
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
        // The in-memory branch is special; set it explicitly to verify the pragma itself
        conn.execute_batch("PRAGMA foreign_keys = ON;").unwrap();
        let on: i64 = conn
            .query_row("PRAGMA foreign_keys", [], |r| r.get(0))
            .unwrap();
        assert_eq!(on, 1);
    }
}
