//! Session state across calls.
//!
//! The wasm side holds no session state except the database connection and
//! node client, which are too costly to reopen per call (migration checks).
//! No caches, timers or background tasks: each host call runs to completion.

use std::cell::{Cell, RefCell};

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::Daemon;
use crate::wallet::Db;
use monero_wallet::address::Network;

thread_local! {
    static DB: RefCell<Option<Db>> = const { RefCell::new(None) };
    static DAEMON: RefCell<Option<Daemon>> = const { RefCell::new(None) };
    /// Network of the current node, from its genesis block at connect. Set and cleared with DAEMON.
    static DAEMON_NETWORK: Cell<Option<Network>> = const { Cell::new(None) };
    static DB_NAME: RefCell<Option<String>> = const { RefCell::new(None) };

    // ── Concurrency guards ──────────────────────────────────────────────
    //
    // Async operations take DB out of here across awaits and put it back
    // after. wasm is single-threaded, but awaits allow re-entrant host calls.
    //
    // Race without guards:
    //   1. A is scanning with DB(A) taken
    //   2. B calls openWallet(B); DB_NAME becomes B
    //   3. A finishes and puts DB(A) back, while DB_NAME says B
    // Every later operation then silently hits the wrong database.
    static BUSY: Cell<bool> = const { Cell::new(false) };
    static GENERATION: Cell<u64> = const { Cell::new(0) };

    // The node client has its **own** generation. Switching nodes leaves DB
    // and GENERATION untouched, so without it an in-flight tip fetch would
    // restore the old node and route all later requests there.
    static DAEMON_GENERATION: Cell<u64> = const { Cell::new(0) };
}

fn assert_idle(operation: &str) -> Result<()> {
    if BUSY.with(Cell::get) {
        return Err(RuntimeError::with(
            ErrorCode::WalletBusy,
            serde_json::json!({ "operation": operation }),
        ));
    }
    Ok(())
}

/// Sets BUSY while held; Drop **always** clears it, including early returns and panic unwinding.
struct BusyGuard;

impl BusyGuard {
    fn acquire(operation: &str) -> Result<Self> {
        assert_idle(operation)?;
        BUSY.with(|b| b.set(true));
        Ok(Self)
    }
}

impl Drop for BusyGuard {
    fn drop(&mut self) {
        BUSY.with(|b| b.set(false));
    }
}

fn bump_generation() {
    GENERATION.with(|g| g.set(g.get().wrapping_add(1)));
}

fn bump_daemon_generation() {
    DAEMON_GENERATION.with(|g| g.set(g.get().wrapping_add(1)));
}

fn daemon_generation() -> u64 {
    DAEMON_GENERATION.with(Cell::get)
}

/// `sampled` is taken before the client is borrowed. A mismatch means the node
/// was replaced or closed meanwhile, so drop the stale client instead.
fn put_daemon_back(daemon: Daemon, sampled: u64) {
    if daemon_generation() == sampled {
        DAEMON.with(|c| *c.borrow_mut() = Some(daemon));
    }
}

fn not_open() -> RuntimeError {
    RuntimeError::new(ErrorCode::WalletNotOpen)
}

fn no_daemon() -> RuntimeError {
    RuntimeError::new(ErrorCode::NodeNotConfigured)
}

/// Opens a wallet. Refused while an async operation is in flight, which would
/// otherwise silently write its results to the wrong database.
pub fn set_db(db: Db, db_name: &str) -> Result<()> {
    assert_idle("openWallet")?;
    DB.with(|c| *c.borrow_mut() = Some(db));
    DB_NAME.with(|c| *c.borrow_mut() = Some(db_name.to_owned()));
    bump_generation();
    Ok(())
}

pub fn set_daemon(daemon: Daemon, network: Network) {
    DAEMON.with(|c| *c.borrow_mut() = Some(daemon));
    DAEMON_NETWORK.with(|n| n.set(Some(network)));
    bump_daemon_generation();
}

/// Wallet and node must be on the same network. A testnet wallet on a mainnet
/// node would scan and send real XMR.
fn check_node_network(node: Option<Network>, wallet: Network) -> Result<()> {
    if node == Some(wallet) {
        return Ok(());
    }
    Err(RuntimeError::with(
        ErrorCode::NodeNetworkMismatch,
        serde_json::json!({
            "nodeNetwork": node.map(crate::keys::network_name),
            "walletNetwork": crate::keys::network_name(wallet),
        }),
    ))
}

/// Network of the current node (by genesis block at connect); `None` without a node.
pub fn daemon_network() -> Option<Network> {
    DAEMON_NETWORK.with(Cell::get)
}

/// Disconnects the node. A borrowed client is discarded on return via the generation check.
pub fn clear_daemon() {
    DAEMON.with(|c| *c.borrow_mut() = None);
    DAEMON_NETWORK.with(|n| n.set(None));
    bump_daemon_generation();
}

pub fn open_db_name() -> Option<String> {
    DB_NAME.with(|c| c.borrow().clone())
}

/// Closes the wallet, dropping the database and client. Used for account deletion or network switches.
pub fn close() -> Result<()> {
    assert_idle("closeWallet")?;
    DB.with(|c| *c.borrow_mut() = None);
    DAEMON.with(|c| *c.borrow_mut() = None);
    DAEMON_NETWORK.with(|n| n.set(None));
    DB_NAME.with(|c| *c.borrow_mut() = None);
    bump_generation();
    bump_daemon_generation();
    Ok(())
}

/// Borrows the database for a synchronous operation.
pub fn with_db<T>(f: impl FnOnce(&mut Db) -> Result<T>) -> Result<T> {
    let _guard = BusyGuard::acquire("walletOperation")?;
    DB.with(|c| {
        let mut slot = c.borrow_mut();
        let db = slot.as_mut().ok_or_else(not_open)?;
        f(db)
    })
}

/// Read-only borrow.
pub fn with_db_read<T>(f: impl FnOnce(&Db) -> Result<T>) -> Result<T> {
    DB.with(|c| {
        let slot = c.borrow();
        let db = slot.as_ref().ok_or_else(not_open)?;
        f(db)
    })
}

/// Borrows the database and node client for an async operation.
///
/// Take, use, put back: no `RefCell` borrow is held across awaits.
pub async fn with_db_and_daemon<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(Db, Daemon) -> Fut,
    Fut: std::future::Future<Output = (Db, Daemon, Result<T>)>,
{
    let guard = BusyGuard::acquire("walletOperation")?;
    let generation = GENERATION.with(Cell::get);
    let daemon_gen = daemon_generation();

    let db = DB.with(|c| c.borrow_mut().take()).ok_or_else(not_open)?;
    let daemon = match DAEMON.with(|c| c.borrow_mut().take()) {
        Some(d) => d,
        None => {
            DB.with(|cell| *cell.borrow_mut() = Some(db));
            return Err(no_daemon());
        }
    };
    if let Err(e) = check_node_network(DAEMON_NETWORK.with(Cell::get), db.network()) {
        DB.with(|cell| *cell.borrow_mut() = Some(db));
        DAEMON.with(|cell| *cell.borrow_mut() = Some(daemon));
        return Err(e);
    }

    let (db, daemon, out) = f(db, daemon).await;

    // If the wallet was replaced or closed meanwhile, **drop** this one;
    // putting it back would overwrite the current wallet.
    if GENERATION.with(Cell::get) != generation {
        drop(db);
    } else {
        DB.with(|c| *c.borrow_mut() = Some(db));
    }
    put_daemon_back(daemon, daemon_gen);
    drop(guard);
    out
}

/// Borrows only the node client; **no wallet needs to be open**.
///
/// Account creation needs the chain tip for the birthday before the database exists.
pub async fn with_daemon<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(Daemon) -> Fut,
    Fut: std::future::Future<Output = (Daemon, Result<T>)>,
{
    let daemon_gen = daemon_generation();
    let daemon = DAEMON
        .with(|c| c.borrow_mut().take())
        .ok_or_else(no_daemon)?;
    let (daemon, result) = f(daemon).await;
    put_daemon_back(daemon, daemon_gen);
    result
}

#[cfg(test)]
mod tests {
    use super::*;
    use monero_wallet::address::Network;

    // Each test runs on its own thread, so thread_locals are isolated.

    fn open_memory_db() {
        let db = Db::open(":memory:", Network::Mainnet).expect("in-memory db");
        set_db(db, ":memory:").expect("open db");
    }

    #[test]
    fn operations_fail_cleanly_before_a_wallet_is_open() {
        let e = with_db_read(|_| Ok(())).unwrap_err();
        assert_eq!(e.code, ErrorCode::WalletNotOpen);
    }

    #[test]
    fn opening_then_closing_clears_the_name() {
        open_memory_db();
        assert_eq!(open_db_name().as_deref(), Some(":memory:"));
        close().unwrap();
        assert!(open_db_name().is_none());
        assert_eq!(
            with_db_read(|_| Ok(())).unwrap_err().code,
            ErrorCode::WalletNotOpen
        );
    }

    /// The wallet cannot be switched while borrowed, or the operation would
    /// silently write to the wrong database.
    #[test]
    fn switching_the_wallet_is_refused_while_an_operation_holds_it() {
        open_memory_db();
        let result = with_db(|_| {
            // Host opens a wallet while an operation is in progress
            let db2 = Db::open(":memory:", Network::Mainnet).expect("second db");
            Ok(set_db(db2, "other").map(|_| ()).unwrap_err().code)
        })
        .unwrap();
        assert_eq!(result, ErrorCode::WalletBusy);
    }

    /// The guard clears BUSY on the error path too; otherwise one failure
    /// would wedge the runtime in BUSY forever.
    #[test]
    fn the_busy_flag_is_cleared_even_when_the_operation_fails() {
        open_memory_db();
        let _ = with_db(|_| -> Result<()> { Err(RuntimeError::new(ErrorCode::InvalidRequest)) });
        assert!(!BUSY.with(Cell::get), "BUSY must be cleared on failure");
        // Usable again afterwards
        with_db(|_| Ok(())).expect("operations should work after a failure");
    }

    /// Without a node, node operations report NODE_NOT_CONFIGURED rather than a
    /// generic network error; the host handles them differently.
    #[test]
    fn daemon_operations_report_a_missing_node_distinctly() {
        open_memory_db();
        let e = futures_lite::future::block_on(with_db_and_daemon(|db, daemon| async move {
            (db, daemon, Ok(()))
        }))
        .unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
        // DB must be put back when no client is available, or the wallet silently closes
        assert!(open_db_name().is_some(), "failure must not drop the db");
        with_db_read(|_| Ok(())).expect("db should still be open");
    }

    #[test]
    fn a_node_on_another_network_is_refused() {
        check_node_network(Some(Network::Mainnet), Network::Mainnet).unwrap();
        for (node, wallet) in [
            (Some(Network::Mainnet), Network::Stagenet),
            (Some(Network::Testnet), Network::Mainnet),
            (None, Network::Mainnet),
        ] {
            let e = check_node_network(node, wallet).unwrap_err();
            assert_eq!(e.code, ErrorCode::NodeNetworkMismatch);
        }
    }

    /// Closing invalidates an in-flight client borrow, so it cannot restore
    /// the old node when it finishes.
    #[test]
    fn closing_invalidates_an_in_flight_daemon_borrow() {
        let sampled = daemon_generation();
        assert_eq!(
            daemon_generation(),
            sampled,
            "generation is stable when nothing happens"
        );
        close().expect("close should succeed on empty state");
        assert_ne!(
            daemon_generation(),
            sampled,
            "in-flight borrow must be invalid after close"
        );
    }
}
