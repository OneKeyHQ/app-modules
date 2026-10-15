//! Cross-call runtime state.
//!
//! Only the database connection and the lightwalletd client are kept, since
//! reopening per call would rerun migration checks. No caches, timers or
//! background tasks.

use std::cell::{Cell, RefCell};

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::LightClient;
use crate::wallet::Db;

thread_local! {
    static DB: RefCell<Option<Db>> = const { RefCell::new(None) };
    static CLIENT: RefCell<Option<LightClient>> = const { RefCell::new(None) };
    // WalletDb does not expose its connection, so history queries on
    // v_transactions open a separate read connection by name (same VFS, same data).
    static DB_NAME: RefCell<Option<String>> = const { RefCell::new(None) };

    // ── Reentrancy guard ────────────────────────────────────────────────
    //
    // Async operations take DB out, hold it across awaits and put it back; the
    // host can call in meanwhile. Unguarded race:
    //   1. A is in syncStep with DB(A) taken
    //   2. B calls openWallet(B); DB_NAME becomes B
    //   3. A finishes and puts DB(A) back while DB_NAME says B
    // Every later call then silently hits the wrong wallet, possibly spending
    // A's notes in B's transactions.
    //
    // BUSY is the real guard: it refuses wallet switches meanwhile. GENERATION is
    // a fallback that is currently unreachable (it is sampled after BUSY is set
    // and set_db/close are blocked by BUSY); it covers future paths that await
    // without holding BUSY. It is not a second independent guard today.
    static BUSY: Cell<bool> = const { Cell::new(false) };
    static GENERATION: Cell<u64> = const { Cell::new(0) };

    // The client has its own generation: `setLightwalletdUrl` does not touch DB,
    // so without it an in-flight `getChainTip` would restore the old endpoint.
    static CLIENT_GENERATION: Cell<u64> = const { Cell::new(0) };
}

/// Sampled before taking the client and compared before putting it back; a
/// mismatch means the endpoint changed or closed, so the stale client is dropped.
fn client_generation() -> u64 {
    CLIENT_GENERATION.with(Cell::get)
}

fn put_client_back(client: LightClient, sampled: u64) {
    if client_generation() == sampled {
        CLIENT.with(|c| *c.borrow_mut() = Some(client));
    }
}

/// Refuses wallet-switching calls while an async operation is running.
fn assert_idle(operation: &str) -> Result<()> {
    if BUSY.with(Cell::get) {
        return Err(RuntimeError::with(
            ErrorCode::WalletBusy,
            serde_json::json!({ "operation": operation }),
        ));
    }
    Ok(())
}

/// Sets BUSY while held and always clears it on drop, including early returns
/// and panic unwinds.
///
/// A wasm-bindgen `async fn` body starts only after the current synchronous
/// block, so there are two windows:
///
/// - **After the call, before the body runs**: nothing is taken yet, so
///   `openWallet` succeeds and the operation runs on the new wallet. The host's
///   `withWalletLease` serializes this; the runtime cannot without making exports
///   return Promises synchronously (typing every result as `any`).
/// - **During awaits in the body**: DB is taken; BUSY blocks switching or closing.
///
/// Generation ensures that in either window a stale wallet never overwrites the
/// current one.
struct BusyGuard {
    generation: u64,
}

impl BusyGuard {
    fn acquire(operation: &str) -> Result<Self> {
        assert_idle(operation)?;
        BUSY.with(|b| b.set(true));
        Ok(Self {
            // Sampled when DB is taken: a switch before this is not stale (the
            // operation just runs on the new wallet); one after is.
            generation: GENERATION.with(Cell::get),
        })
    }

    /// Whether the wallet was switched or closed since the operation started.
    fn stale(&self) -> bool {
        self.generation != GENERATION.with(Cell::get)
    }
}

impl Drop for BusyGuard {
    fn drop(&mut self) {
        BUSY.with(|b| b.set(false));
    }
}

fn bump_client_generation() {
    CLIENT_GENERATION.with(|g| g.set(g.get().wrapping_add(1)));
}

fn bump_generation() {
    GENERATION.with(|g| g.set(g.get().wrapping_add(1)));
}

fn not_open() -> RuntimeError {
    RuntimeError::new(ErrorCode::WalletNotOpen)
}

/// Distinguishes a DB borrowed by an async operation (retry later) from one
/// not open (host must reopen), so the host does not reopen a wallet mid-sync.
fn db_unavailable(operation: &str) -> RuntimeError {
    if BUSY.with(Cell::get) {
        RuntimeError::with(
            ErrorCode::WalletBusy,
            serde_json::json!({ "operation": operation }),
        )
    } else {
        not_open()
    }
}

fn no_client() -> RuntimeError {
    RuntimeError::new(ErrorCode::LightwalletdNotConfigured)
}

/// Opens a wallet. Refused during an async operation, which would otherwise
/// silently write its results to the wrong wallet.
pub fn set_db(db: Db, db_name: &str) -> Result<()> {
    assert_idle("openWallet")?;
    DB.with(|c| *c.borrow_mut() = Some(db));
    DB_NAME.with(|c| *c.borrow_mut() = Some(db_name.to_owned()));
    bump_generation();
    Ok(())
}

/// Opens a separate connection for read-only queries such as history.
pub fn with_read_conn<T>(f: impl FnOnce(&rusqlite::Connection) -> Result<T>) -> Result<T> {
    let name = DB_NAME.with(|c| c.borrow().clone()).ok_or_else(not_open)?;
    let conn = crate::storage::open_connection(&name)?;
    f(&conn)
}

/// Best effort on its own connection: a failed reclaim only means the file
/// stays larger until the next one, so it never fails the caller.
pub fn reclaim_storage(max_pages: u32) {
    if let Some(name) = DB_NAME.with(|c| c.borrow().clone()) {
        let _ = crate::storage::open_connection(&name)
            .and_then(|conn| crate::storage::reclaim_free_pages(&conn, max_pages));
    }
}

pub fn set_client(client: LightClient) {
    CLIENT.with(|c| *c.borrow_mut() = Some(client));
    bump_client_generation();
}

/// Lends the database for a synchronous operation.
///
/// Take, use, put back rather than holding a `RefCell` borrow, so reentry from
/// `f` does not panic.
pub fn with_db<T>(f: impl FnOnce(&mut Db) -> Result<T>) -> Result<T> {
    let mut db = DB
        .with(|c| c.borrow_mut().take())
        .ok_or_else(|| db_unavailable("walletRead"))?;
    let out = f(&mut db);
    DB.with(|c| *c.borrow_mut() = Some(db));
    out
}

/// Lends the database and client for an async operation (sync, account import).
///
/// No `RefCell` borrow is held across awaits.
pub async fn with_db_and_client<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(Db, LightClient) -> Fut,
    Fut: std::future::Future<Output = (Db, LightClient, Result<T>)>,
{
    // No await between acquiring the guard and taking DB, so the generation is
    // sampled at the moment DB is taken; staleness relies on this.
    let guard = BusyGuard::acquire("walletOperation")?;
    let client_gen = client_generation();
    let db = DB.with(|c| c.borrow_mut().take()).ok_or_else(not_open)?;
    let client = match CLIENT.with(|c| c.borrow_mut().take()) {
        Some(c) => c,
        None => {
            DB.with(|cell| *cell.borrow_mut() = Some(db));
            return Err(no_client());
        }
    };

    let (db, client, out) = f(db, client).await;

    // If the wallet was switched or closed meanwhile, drop this one rather than
    // overwrite the current wallet with a stale one.
    if guard.stale() {
        drop(db);
    } else {
        DB.with(|c| *c.borrow_mut() = Some(db));
    }
    put_client_back(client, client_gen);
    out
}

/// Lends only the client; no wallet needs to be open.
///
/// New accounts need the chain tip for their birthday before the database
/// exists. Requiring an open wallet here would fall back to the Sapling
/// activation birthday and force a full scan.
pub async fn with_client<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(LightClient) -> Fut,
    Fut: std::future::Future<Output = (LightClient, Result<T>)>,
{
    let client_gen = client_generation();
    let client = CLIENT
        .with(|c| c.borrow_mut().take())
        .ok_or_else(|| RuntimeError::new(ErrorCode::LightwalletdNotConfigured))?;
    let (client, result) = f(client).await;
    put_client_back(client, client_gen);
    result
}

/// Name of the open database, checked before deleting a database.
pub fn open_db_name() -> Option<String> {
    DB_NAME.with(|c| c.borrow().clone())
}

/// Closes the wallet, releasing the database and client (account removal,
/// network switch).
///
/// Refused during an async operation, which holds DB and would put it back.
pub fn close() -> Result<()> {
    assert_idle("closeWallet")?;
    DB.with(|c| *c.borrow_mut() = None);
    CLIENT.with(|c| *c.borrow_mut() = None);
    DB_NAME.with(|c| *c.borrow_mut() = None);
    bump_generation();
    bump_client_generation();
    Ok(())
}
