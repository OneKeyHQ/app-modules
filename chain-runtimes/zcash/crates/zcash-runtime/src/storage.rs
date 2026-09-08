//! 存储适配。
//!
//! SQLite 从不直接碰文件 —— 它通过 VFS（虚拟文件系统）接口读写字节，而那个接口
//! 只有五个动作：read / write / truncate / flush / size。把这五个动作接到哪里，
//! 决定了数据实际存在哪。
//!
//! 浏览器侧我们接到 IndexedDB（`sqlite-wasm-vfs` 的 `relaxed_idb` 实现）。选它
//! 而不是 OPFS 的 `sahpool`，是因为 sahpool 的同步访问句柄只在 Worker 里可用，
//! 而 relaxed-idb 在所有上下文都能跑 —— 这对我们要覆盖的载体（桌面 renderer、
//! 浏览器页面、插件 offscreen、移动 WebView）是必要条件。
//!
//! 代价是它不是「完全持久」：读写在内存里同步服务，异步批量刷进 IndexedDB，
//! 崩溃可能丢掉最近一批。对扫链缓存这是可接受的 —— 数据本来就可以从链上重建。

use crate::error::{ErrorCode, Result, RuntimeError};
use serde_json::json;

// 强制把 sqlite-wasm-rs 链进最终产物：它提供 sqlite3_os_init 与
// rust_sqlite_wasm_{malloc,free,localtime} 这几个符号。若没有任何代码引用它，
// rustc 不会把它的 rlib 纳入链接，链接期就会报 undefined symbol。
#[cfg(target_arch = "wasm32")]
use sqlite_wasm_rs as _;

#[cfg(target_arch = "wasm32")]
use std::sync::atomic::{AtomicBool, Ordering};

#[cfg(target_arch = "wasm32")]
static VFS_READY: AtomicBool = AtomicBool::new(false);

// `install()` 返回的管理句柄。**必须留着** —— `Preload::None` 下已存在的库不会
// 自己出现在 VFS 的文件表里，得先 `preload_db` 才能被 SQLite 打开。丢掉这个句柄
// 就没有别的入口，而症状是静默的：库看起来是空的，于是重新建表、从 birthday
// 全量重扫，扫链本身一切正常。
// `Rc` 是为了能在不持有 RefCell 借用的情况下跨 await 使用它 ——
// `RelaxedIdbUtil` 本身不是 Clone。
#[cfg(target_arch = "wasm32")]
thread_local! {
    static VFS_UTIL: std::cell::RefCell<
        Option<std::rc::Rc<sqlite_wasm_vfs::relaxed_idb::RelaxedIdbUtil>>,
    > = const { std::cell::RefCell::new(None) };
}

/// 把某个库从 IndexedDB 读进 VFS 的文件表。打开该库**之前**必须调一次。
///
/// 幂等：已经在表里的库会被跳过。
#[cfg(target_arch = "wasm32")]
pub async fn preload_database(db_name: &str) -> Result<()> {
    // 内存存储模式没有装持久化 VFS，也就没有 IndexedDB 可读 —— 这里必须放行，
    // 否则所有走内存存储的排查页都会在开库这一步报 NOT_INITIALIZED。
    if memory_mode() {
        return Ok(());
    }
    let util = VFS_UTIL.with(|u| u.borrow().clone());
    let Some(util) = util else {
        return Err(RuntimeError::with(
            ErrorCode::NotInitialized,
            json!({ "reason": "vfsNotInstalled" }),
        ));
    };
    util.preload_db(vec![db_name.to_owned()])
        .await
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "preloadDb" }),
            )
            .detail(format!("{e:?}"))
        })
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn preload_database(_db_name: &str) -> Result<()> {
    Ok(())
}

/// Wait until every relaxed-IDB commit queued before this call has completed.
///
/// SQLite's xSync schedules the real IndexedDB write but cannot await it from
/// the synchronous VFS callback. The vendored VFS barrier runs on that same
/// FIFO queue and reports any earlier sync failure. This is required before a
/// locally finalized transaction may be broadcast.
#[cfg(target_arch = "wasm32")]
pub async fn durability_barrier() -> Result<()> {
    if memory_mode() {
        return Ok(());
    }
    let util = VFS_UTIL.with(|u| u.borrow().clone()).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::NotInitialized,
            json!({ "reason": "vfsNotInstalled" }),
        )
    })?;
    util.barrier()
        .map_err(|error| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "durabilityBarrier" }),
            )
            .detail(format!("{error:?}"))
        })?
        .await
        .map_err(|error| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "durabilityBarrier" }),
            )
            .detail(format!("{error:?}"))
        })
}

#[cfg(not(target_arch = "wasm32"))]
pub async fn durability_barrier() -> Result<()> {
    Ok(())
}

/// 安装持久化 VFS 并设为默认。
///
/// 幂等：重复调用只有第一次生效。必须在任何 `open_wallet` 之前完成。
///
/// 不调用它就走 sqlite-wasm-rs 内置的**内存 VFS** —— 数据不落盘，页面刷新即失。
/// 那条路的用途有二：临时/一次性钱包；以及排查问题时把「存储层」从
/// 「纯计算」里摘出来（同样的扫描在内存 VFS 上跑一遍，就知道慢的是哪一半）。
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
    use sqlite_wasm_rs as ffi;
    use sqlite_wasm_vfs::relaxed_idb::{install, RelaxedIdbCfg};

    if VFS_READY.load(Ordering::SeqCst) {
        return Ok(());
    }
    // 默认是 `Preload::All` —— 打开 VFS 时把该 origin 下**所有**库整个读进内存。
    // 改成 `None`，配合开库时的 `preload_database(name)`，只加载当前网络那一个。
    //
    // **这不是按页懒加载。** `preload_db_impl` 会遍历该文件的全部 IndexedDB block
    // 组成内存文件，SQLite 之后同步访问的是内存副本。所以库有多大，wasm 堆就吃多少、
    // 开库就等多久 —— 省下的只是「同一 origin 下其他库」的那份，不是这一个库本身。
    // 真要按页读，得换一个 page-backed 的 VFS。
    let cfg = RelaxedIdbCfg {
        preload: sqlite_wasm_vfs::relaxed_idb::Preload::None,
        ..RelaxedIdbCfg::default()
    };
    let util = install::<ffi::WasmOsCallback>(&cfg, true)
        .await
        .map_err(|e| RuntimeError::new(ErrorCode::NotInitialized).detail(format!("{e:?}")))?;
    VFS_UTIL.with(|u| *u.borrow_mut() = Some(std::rc::Rc::new(util)));
    VFS_READY.store(true, Ordering::SeqCst);
    Ok(())
}

/// 原生目标（桌面 / 移动 / 测试）不需要自定义 VFS —— 那里有真的文件系统。
#[cfg(not(target_arch = "wasm32"))]
pub async fn install_persistent_vfs() -> Result<()> {
    Ok(())
}

#[cfg(target_arch = "wasm32")]
pub fn is_ready() -> bool {
    VFS_READY.load(Ordering::SeqCst)
}

#[cfg(not(target_arch = "wasm32"))]
pub fn is_ready() -> bool {
    true
}

/// 打开一个 SQLite 连接。路径在 wasm 上是 VFS 内的逻辑名，不是磁盘路径。
/// 标记「本次会话有意使用内存存储」，让 `open_connection` 不再要求先装 VFS。
#[cfg(target_arch = "wasm32")]
static MEMORY_MODE: AtomicBool = AtomicBool::new(false);

/// 选择内存存储：不安装持久化 VFS，数据只活在本页面生命周期内。
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
    // wasm 上 db_name 是 VFS 里的逻辑名，不该含路径分隔符；
    // 原生上它是真实文件路径，分隔符完全合法。
    let bad_name = db_name.is_empty()
        || (cfg!(target_arch = "wasm32") && (db_name.contains('/') || db_name.contains('\\')));
    if bad_name {
        return Err(RuntimeError::with(
            ErrorCode::InvalidDbName,
            json!({ "value": db_name }),
        ));
    }
    let conn = rusqlite::Connection::open(db_name)?;
    // carray 虚拟表：zcash_client_sqlite 的部分查询用它传数组参数。
    rusqlite::vtab::array::load_module(&conn)?;
    Ok(conn)
}

/// Checks whether a persisted database exists without opening or creating it.
#[cfg(target_arch = "wasm32")]
pub async fn database_exists(db_name: &str) -> Result<bool> {
    if memory_mode() {
        return Ok(false);
    }
    let util = VFS_UTIL.with(|u| u.borrow().clone()).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::NotInitialized,
            json!({ "reason": "vfsNotInstalled" }),
        )
    })?;
    util.preload_db(vec![db_name.to_owned()])
        .await
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "inspectDb" }),
            )
            .detail(format!("{e:?}"))
        })?;
    Ok(util.exists(db_name))
}

/// Checks whether a native database file exists without opening or creating it.
#[cfg(not(target_arch = "wasm32"))]
pub async fn database_exists(db_name: &str) -> Result<bool> {
    Ok(std::path::Path::new(db_name).exists())
}

/// 删除一个钱包库，连同它在 IndexedDB 里的全部字节。
///
/// ## 为什么这是安全的
///
/// 钱包库是**纯派生缓存**：扫过的区块、解密出来的 note、witness、交易历史，
/// 全都能从链上重新扫出来。真正不可再生的东西 —— UFVK 与 birthday ——
/// 由宿主存在自己的账户元数据里，不在这个库里。所以擦掉它不丢任何东西，
/// 只是下次要重扫。
///
/// **反过来说这是一条硬约束**：将来若把 birthday 之类的权威数据挪进这个库，
/// 本函数就从「清缓存」变成「毁数据」。要挪之前先改这里。
///
/// ## 两种用途
///
/// - 出问题时的**手动重置**：历史错乱、余额对不上，擦掉重建
/// - **删账户**时的清理：库里存着已解密的屏蔽交易历史，正是 Zcash 要隐藏的东西，
///   账户没了它必须跟着没
///
/// 返回是否确实删掉了（库本来就不存在时返回 false，不算错误）。
#[cfg(target_arch = "wasm32")]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    use sqlite_wasm_rs as ffi;
    use sqlite_wasm_vfs::relaxed_idb::{install, RelaxedIdbCfg};

    if memory_mode() {
        // 内存存储下没有持久化副本，数据随页面消失，没什么可删。
        return Ok(false);
    }
    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }

    // install 是幂等的：已注册时只返回管理句柄，不会重复注册。
    let util = install::<ffi::WasmOsCallback>(&RelaxedIdbCfg::default(), true)
        .await
        .map_err(|e| RuntimeError::new(ErrorCode::NotInitialized).detail(format!("{e:?}")))?;

    if !util.exists(db_name) {
        return Ok(false);
    }
    // 上游明确要求删除前库必须已关闭，否则句柄与存储会不一致。
    util.delete_db(db_name)
        .map_err(|e| db_delete_err(db_name, e))?
        .await
        .map_err(|e| db_delete_err(db_name, e))?;
    Ok(true)
}

/// 原生目标：直接删文件。
#[cfg(not(target_arch = "wasm32"))]
pub async fn delete_database(db_name: &str) -> Result<bool> {
    match std::fs::remove_file(db_name) {
        Ok(()) => Ok(true),
        Err(e) if e.kind() == std::io::ErrorKind::NotFound => Ok(false),
        Err(e) => Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "deleteDatabase", "dbName": db_name }),
        )
        .detail(e)),
    }
}

#[cfg(target_arch = "wasm32")]
fn db_delete_err(db_name: &str, e: impl std::fmt::Debug) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::DatabaseError,
        json!({ "operation": "deleteDatabase", "dbName": db_name }),
    )
    .detail(format!("{e:?}"))
}
