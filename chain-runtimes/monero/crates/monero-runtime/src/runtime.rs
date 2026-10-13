//! 跨调用的会话状态。
//!
//! 按边界原则，wasm 侧**不持有会话状态** —— 唯一的例外是数据库连接和节点
//! 客户端，因为每次调用都重开代价太高（重开要重跑一遍迁移检查）。
//!
//! 除此之外没有缓存、没有定时器、没有后台任务。宿主每调一次，这里完整做完
//! 一次并返回。

use std::cell::{Cell, RefCell};

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::Daemon;
use crate::wallet::Db;
use monero_wallet::address::Network;

thread_local! {
    static DB: RefCell<Option<Db>> = const { RefCell::new(None) };
    static DAEMON: RefCell<Option<Daemon>> = const { RefCell::new(None) };
    /// 当前节点所在的网络，连接时按创世块认定。与 DAEMON 同进同出。
    static DAEMON_NETWORK: Cell<Option<Network>> = const { Cell::new(None) };
    static DB_NAME: RefCell<Option<String>> = const { RefCell::new(None) };

    // ── 并发防线 ────────────────────────────────────────────────────────
    //
    // 异步操作会把 DB 从这里**取走**，跨 await 持有，结束后放回。这中间宿主
    // 完全可以再发起别的调用 —— wasm 是单线程，但 await 让重入变得可能。
    //
    // 没有防线时的真实竞态：
    //   1. A 在扫链，DB(A) 已被取走
    //   2. B 调 openWallet(B)，DB_NAME 变成 B
    //   3. A 结束，把 DB(A) 放回 —— 此刻 DB_NAME 说 B，实际却是 A
    // 之后所有操作都在错误的库上进行，而且看不出来。
    static BUSY: Cell<bool> = const { Cell::new(false) };
    static GENERATION: Cell<u64> = const { Cell::new(0) };

    // 节点客户端有**自己的**世代号。DB 那道防线盖不住它：换节点时不碰 DB，
    // GENERATION 不动，于是一次在途的取链尖结束后会把旧节点原样盖回去，
    // 之后所有请求都发给上一个节点。
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

/// 持有期间标记 BUSY，析构时**一定**清除 —— 包括提前返回与 panic 展开。
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

/// 取走客户端前采样，放回前比对。不相等说明期间节点被换掉或关掉了，
/// 手上这个已经作废 —— 丢弃它，而不是覆盖当前的。
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

/// 打开钱包。正在进行异步操作时会被拒绝 —— 中途换库会让那次操作把结果写到
/// 错误的地方，而且没有任何迹象。
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

/// 钱包库与节点必须在同一个网络上。不一致时什么都不做：测试网钱包连着主网
/// 节点扫到的是真 XMR，发出去的也是。
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

/// 当前节点所在的网络（连接时按创世块认定）。没有节点时为 `None`。
pub fn daemon_network() -> Option<Network> {
    DAEMON_NETWORK.with(Cell::get)
}

/// 断开当前节点。借出中的客户端放回时按世代号作废，不会复活。
pub fn clear_daemon() {
    DAEMON.with(|c| *c.borrow_mut() = None);
    DAEMON_NETWORK.with(|n| n.set(None));
    bump_daemon_generation();
}

pub fn open_db_name() -> Option<String> {
    DB_NAME.with(|c| c.borrow().clone())
}

/// 关闭钱包，释放数据库与客户端。用于账户删除或切换网络。
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

/// 借出数据库做一次同步操作。
pub fn with_db<T>(f: impl FnOnce(&mut Db) -> Result<T>) -> Result<T> {
    let _guard = BusyGuard::acquire("walletOperation")?;
    DB.with(|c| {
        let mut slot = c.borrow_mut();
        let db = slot.as_mut().ok_or_else(not_open)?;
        f(db)
    })
}

/// 只读借出。
pub fn with_db_read<T>(f: impl FnOnce(&Db) -> Result<T>) -> Result<T> {
    DB.with(|c| {
        let slot = c.borrow();
        let db = slot.as_ref().ok_or_else(not_open)?;
        f(db)
    })
}

/// 借出数据库与节点客户端做一次异步操作。
///
/// 用「取出 → 使用 → 放回」而不是长期持有 `RefCell` 借用：异步过程中不持有
/// 任何借用，这是在 wasm 单线程上跨 await 安全共享状态的关键。
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

    // 期间钱包被换掉/关掉时，**丢弃**手上这个而不是放回去。
    // 放回去等于让一个已经作废的钱包覆盖当前的，那正是串库的源头。
    if GENERATION.with(Cell::get) != generation {
        drop(db);
    } else {
        DB.with(|c| *c.borrow_mut() = Some(db));
    }
    put_daemon_back(daemon, daemon_gen);
    drop(guard);
    out
}

/// 只借出节点客户端，**不要求钱包库已打开**。
///
/// 建账户时链尖是先决条件：宿主要用它定 birthday，而那一刻库还没建。
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

    // 测试各跑在自己的线程上，thread_local 天然隔离，互不干扰。

    fn open_memory_db() {
        let db = Db::open(":memory:", Network::Mainnet).expect("内存库");
        set_db(db, ":memory:").expect("开库");
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

    /// 借出期间不许换库。换了会让那次操作把结果写到错误的地方，而且没有
    /// 任何迹象 —— 这是最难查的一类 bug。
    #[test]
    fn switching_the_wallet_is_refused_while_an_operation_holds_it() {
        open_memory_db();
        let result = with_db(|_| {
            // 模拟「操作进行中，宿主又来开库」
            let db2 = Db::open(":memory:", Network::Mainnet).expect("第二个库");
            Ok(set_db(db2, "other").map(|_| ()).unwrap_err().code)
        })
        .unwrap();
        assert_eq!(result, ErrorCode::WalletBusy);
    }

    /// 守卫析构时一定清 BUSY —— 包括提前返回那条路。否则一次失败会把
    /// runtime 永久卡在 BUSY，表现为「之后什么都做不了」。
    #[test]
    fn the_busy_flag_is_cleared_even_when_the_operation_fails() {
        open_memory_db();
        let _ = with_db(|_| -> Result<()> { Err(RuntimeError::new(ErrorCode::InvalidRequest)) });
        assert!(!BUSY.with(Cell::get), "失败路径也必须清 BUSY");
        // 清干净了才能接着用
        with_db(|_| Ok(())).expect("失败之后应当还能继续操作");
    }

    /// 没配节点时，要求节点的操作必须报 NODE_NOT_CONFIGURED，
    /// 而不是笼统的网络错误 —— 两者的处置完全不同。
    #[test]
    fn daemon_operations_report_a_missing_node_distinctly() {
        open_memory_db();
        let e = futures_lite::future::block_on(with_db_and_daemon(|db, daemon| async move {
            (db, daemon, Ok(()))
        }))
        .unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
        // 借不到客户端时 DB 必须被放回去，否则钱包就凭空关了
        assert!(open_db_name().is_some(), "失败不该顺手把库弄丢");
        with_db_read(|_| Ok(())).expect("库应当还在");
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

    /// 关库必须让在途的客户端借用作废。否则它结束时会把旧节点盖回去，
    /// 之后所有请求都发给上一个节点。
    #[test]
    fn closing_invalidates_an_in_flight_daemon_borrow() {
        let sampled = daemon_generation();
        assert_eq!(daemon_generation(), sampled, "无事发生时采样应稳定");
        close().expect("空状态下关闭应当成功");
        assert_ne!(
            daemon_generation(),
            sampled,
            "close 之后在途借用必须被判作废"
        );
    }
}
