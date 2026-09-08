//! 跨调用的运行时状态。
//!
//! 按边界原则，wasm 侧**不持有会话状态** —— 唯一的例外是那个数据库连接和
//! lightwalletd 客户端，因为每次调用都重开代价太高（重开要重跑一遍迁移检查）。
//!
//! 除此之外没有任何缓存、没有定时器、没有后台任务。宿主 TS 每调一次，
//! 这里就完整做完一次并返回。

use std::cell::{Cell, RefCell};

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::LightClient;
use crate::wallet::Db;

thread_local! {
    static DB: RefCell<Option<Db>> = const { RefCell::new(None) };
    static CLIENT: RefCell<Option<LightClient>> = const { RefCell::new(None) };
    // 历史查询走 v_transactions 视图，而 WalletDb 不对外暴露内部连接，
    // 所以记住库名，需要时另开一条只读连接（同 VFS、同库名 = 同一份数据）。
    static DB_NAME: RefCell<Option<String>> = const { RefCell::new(None) };

    // ── 并发防线 ────────────────────────────────────────────────────────
    //
    // 异步操作会把 DB 从这里**取走**，跨 await 持有，结束后放回。这中间宿主
    // 完全可以再发起别的调用 —— wasm 是单线程，但 await 让重入变得可能。
    //
    // 没有防线时的真实竞态：
    //   1. A 在 syncStep，DB(A) 已被取走
    //   2. B 调 openWallet(B)，DB_NAME 变成 B
    //   3. A 结束，把 DB(A) 放回 —— 此刻 DB_NAME 说 B，实际却是 A
    // 之后所有操作都在错误的库上进行，而且看不出来：历史串库、余额串库、
    // 甚至可能用 A 的 note 去发 B 的交易。
    //
    // 两道防线：BUSY 期间拒绝一切会切换钱包的操作；generation 保证异步返回时
    // 不会用一个过期的钱包覆盖新的。两者独立，任一失效另一个仍能兜住。
    static BUSY: Cell<bool> = const { Cell::new(false) };
    static GENERATION: Cell<u64> = const { Cell::new(0) };
}

/// 正在进行一次异步操作时，任何切换钱包的调用都必须被拒绝。
fn assert_idle(operation: &str) -> Result<()> {
    if BUSY.with(Cell::get) {
        return Err(RuntimeError::with(
            ErrorCode::WalletBusy,
            serde_json::json!({ "operation": operation }),
        ));
    }
    Ok(())
}

/// 持有期间标记 BUSY，析构时**一定**清除 —— 包括提前返回和 panic 展开。
///
/// ## 它覆盖哪个窗口，不覆盖哪个
///
/// wasm-bindgen 的 `async fn` 返回的 Promise 是排进微任务队列的，函数体要等
/// 当前同步块结束才开始跑。所以存在两个不同的窗口：
///
/// - **调用之后、函数体开始之前**：此时还没取走任何东西，`openWallet` 会成功，
///   随后那次操作作用在新库上。这是调用方的时序错误，由宿主侧的租约
///   （`withWalletLease`）负责序列化，runtime 这里拦不住 —— 拦得住的唯一办法是
///   把导出改成同步返回 Promise，代价是所有返回值退化成 `any`，不值得。
/// - **函数体开始之后、await 期间**：DB 已被取走，此时切库/关库才是真正危险的，
///   这个窗口由 BUSY 拦住。
///
/// 不变量则由 generation 独立保证：无论哪个窗口，异步返回时都不会用一个
/// 已经作废的钱包覆盖当前的。
struct BusyGuard {
    generation: u64,
}

impl BusyGuard {
    fn acquire(operation: &str) -> Result<Self> {
        assert_idle(operation)?;
        BUSY.with(|b| b.set(true));
        Ok(Self {
            // 采样点就是「取走 DB 的那一刻」—— 早于此的切库不算陈旧，
            // 那只是这次操作作用在了新库上；晚于此的才是。
            generation: GENERATION.with(Cell::get),
        })
    }

    /// 这次操作开始之后，钱包有没有被换掉/关掉。
    fn stale(&self) -> bool {
        self.generation != GENERATION.with(Cell::get)
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

fn not_open() -> RuntimeError {
    RuntimeError::new(ErrorCode::WalletNotOpen)
}

fn no_client() -> RuntimeError {
    RuntimeError::new(ErrorCode::LightwalletdNotConfigured)
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

/// 另开一条连接做只读查询（历史等）。
pub fn with_read_conn<T>(f: impl FnOnce(&rusqlite::Connection) -> Result<T>) -> Result<T> {
    let name = DB_NAME.with(|c| c.borrow().clone()).ok_or_else(not_open)?;
    let conn = crate::storage::open_connection(&name)?;
    f(&conn)
}

pub fn set_client(client: LightClient) {
    CLIENT.with(|c| *c.borrow_mut() = Some(client));
}

pub fn has_db() -> bool {
    DB.with(|c| c.borrow().is_some())
}

/// 借出数据库做一次同步操作。
///
/// 用「取出 → 使用 → 放回」而不是长期持有 `RefCell` 借用，
/// 这样即便 `f` 里再次进入本模块也不会 panic。
pub fn with_db<T>(f: impl FnOnce(&mut Db) -> Result<T>) -> Result<T> {
    let mut db = DB.with(|c| c.borrow_mut().take()).ok_or_else(not_open)?;
    let out = f(&mut db);
    DB.with(|c| *c.borrow_mut() = Some(db));
    out
}

/// 借出数据库与网络客户端做一次异步操作（扫链、导入账户）。
///
/// 同样是取出再放回 —— 异步过程中不持有任何 `RefCell` 借用，
/// 这是在 wasm 单线程上跨 await 安全共享状态的关键。
pub async fn with_db_and_client<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(Db, LightClient) -> Fut,
    Fut: std::future::Future<Output = (Db, LightClient, Result<T>)>,
{
    // 先拿锁再取 DB，两者之间没有 await，所以 generation 的采样点与取走 DB
    // 是同一时刻 —— 这正是「陈旧」判定成立的前提。
    let guard = BusyGuard::acquire("walletOperation")?;
    let db = DB.with(|c| c.borrow_mut().take()).ok_or_else(not_open)?;
    let client = match CLIENT.with(|c| c.borrow_mut().take()) {
        Some(c) => c,
        None => {
            DB.with(|cell| *cell.borrow_mut() = Some(db));
            return Err(no_client());
        }
    };

    let (db, client, out) = f(db, client).await;

    // 期间钱包被换掉/关掉时，**丢弃**手上这个而不是放回去。
    // 放回去等于让一个已经作废的钱包覆盖当前的，那正是串库的源头。
    if guard.stale() {
        drop(db);
    } else {
        DB.with(|c| *c.borrow_mut() = Some(db));
    }
    CLIENT.with(|c| *c.borrow_mut() = Some(client));
    out
}

/// 只借出网络客户端，**不要求钱包库已打开**。
///
/// 建新账户时链尖是先决条件：宿主要用它定 birthday，而那一刻库还没建。
/// 如果取链尖也要求先开库，这条路必然失败、失败又被吞成 null，
/// 结果 birthday 退回 Sapling 激活高度，新助记词也要从头扫。
pub async fn with_client<T, F, Fut>(f: F) -> Result<T>
where
    F: FnOnce(LightClient) -> Fut,
    Fut: std::future::Future<Output = (LightClient, Result<T>)>,
{
    let client = CLIENT
        .with(|c| c.borrow_mut().take())
        .ok_or_else(|| RuntimeError::new(ErrorCode::LightwalletdNotConfigured))?;
    let (client, result) = f(client).await;
    CLIENT.with(|c| *c.borrow_mut() = Some(client));
    result
}

/// 当前打开的库名。删除库前要用它判断是不是正在用的那个。
pub fn open_db_name() -> Option<String> {
    DB_NAME.with(|c| c.borrow().clone())
}

/// 关闭钱包，释放数据库与客户端。用于账户删除或切换网络。
///
/// 异步操作进行中会被拒绝：那次操作手上还握着 DB，此时清空只会让它结束后
/// 把一个本该消失的钱包放回来。
pub fn close() -> Result<()> {
    assert_idle("closeWallet")?;
    DB.with(|c| *c.borrow_mut() = None);
    CLIENT.with(|c| *c.borrow_mut() = None);
    DB_NAME.with(|c| *c.borrow_mut() = None);
    bump_generation();
    Ok(())
}
