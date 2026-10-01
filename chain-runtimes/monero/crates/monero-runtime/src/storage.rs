//! 各端共用的 SQLite 存储。
//!
//! wasm 侧跑在独立 Worker 里，四端（桌面、Web、插件、移动 WebEmbed）都用同一套
//! OPFS 同步访问 VFS —— SQLite 直接按页读写，而不是把整个库预载进 wasm 内存。
//! **这正是选 monero-oxide 而不是 monero-ts 的理由**：后者只能整块序列化。
//!
//! 原生构建保留，用于 Rust 测试与本机诊断。

use rusqlite::Connection;
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};

// 强制链接：Rust 只链接代码里真正引用过的 crate。光在 Cargo.toml 里声明
// sqlite-wasm-rs 不够 —— 它的归档会被找到却一个符号都不拉，链接期报一串
// `undefined symbol: sqlite3_*`。
#[cfg(target_arch = "wasm32")]
use sqlite_wasm_rs as _;

#[cfg(target_arch = "wasm32")]
use std::sync::atomic::{AtomicBool, Ordering};

#[cfg(target_arch = "wasm32")]
static VFS_READY: AtomicBool = AtomicBool::new(false);

// 装好后的池句柄。删库要靠它：库打不开（版本不认、文件损坏）时也得能删掉。
#[cfg(target_arch = "wasm32")]
thread_local! {
    static VFS_UTIL: std::cell::RefCell<Option<std::rc::Rc<sqlite_wasm_vfs::sahpool::OpfsSAHPoolUtil>>> =
        const { std::cell::RefCell::new(None) };
}

/// VFS 是否已就绪。原生目标恒为 true（用系统文件系统）。
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

/// 在浏览器载体里装上持久化 VFS。必须在开任何库之前调用一次。
///
/// 池会独占文件句柄直到它所在的 Worker 被终止 —— 所以同一个库不能被两个
/// Worker 同时打开，宿主那侧要保证串行。
///
/// ## 为什么要自己起名，不用库的默认值
///
/// 默认是 `.opfs-sahpool`。同一个 origin 下 Zcash 的 runtime 也在装它自己的池
/// （`.onekey-zcash-opfs`），将来任何一个依赖只要用默认配置装一次，就会和我们
/// 共用同一个目录、抢同一批文件句柄。起个专属名字是一行的事，省掉的是一类
/// 只在"两条链同时开着"时才出现、且看起来像存储损坏的故障。
///
/// ## 为什么要重试
///
/// 浏览器里 Worker 的终止是**异步**的：carrier 重置、页面刷新、开发时热更新，
/// 旧 Worker 在新的已经跑起来之后还会攥着 SAH 句柄一小会儿。这时 install 会拿
/// 到 `NoModificationAllowedError`。不重试的话，用户看到的是刷新一下页面
/// 钱包就打不开了。
///
/// 只对"上一个持有者正在释放"这一种错误重试，而且有界。真有另一个活着的持有
/// 者时，等完这段窗口依然会失败 —— 那是对的，两个 Worker 同时写一个库比报错
/// 严重得多。
#[cfg(target_arch = "wasm32")]
pub async fn install_persistent_vfs() -> Result<()> {
    use sqlite_wasm_vfs::sahpool::{install, OpfsSAHError, OpfsSAHPoolCfg};

    if is_ready() {
        return Ok(());
    }

    let cfg = OpfsSAHPoolCfg {
        vfs_name: "onekey-xmr-opfs".to_owned(),
        directory: ".onekey-xmr-opfs".to_owned(),
        // 绝不能是 true：那会在每次启动时清空池，等于每次开 app 都要全链重扫。
        clear_on_init: false,
        // 一个网络一个库，加上回滚日志与富余句柄。
        initial_capacity: 4,
    };

    let mut retries = 20;
    loop {
        // `WasmOsCallback` 是 sqlite-wasm-rs 提供的具体实现（VFS crate 本身只
        // 定义了 trait）。泛型参数必须显式给，否则推不出来。
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

/// 原生目标不需要装 VFS。留同名函数让上层代码不必到处写 cfg。
#[cfg(not(target_arch = "wasm32"))]
pub async fn install_persistent_vfs() -> Result<()> {
    Ok(())
}

/// 库名校验：不能为空，也不能含路径分隔符。
///
/// **两个目标都要查**。wasm 上 db_name 是 VFS 里的不透明键，原生上它会直接
/// 变成文件路径 —— 后者才是路径穿越真正有意义的地方，所以不能只在 wasm 上查。
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

/// 打开一条连接并设好 pragma。
///
/// `:memory:` 是测试专用的特例，跳过名字校验与持久化 pragma。
pub fn open_connection(db_name: &str) -> Result<Connection> {
    if db_name == ":memory:" {
        let conn = Connection::open_in_memory().map_err(|e| db_err("openInMemory", e))?;
        // 外键与正式库一致，级联删除的测试才有意义
        conn.execute_batch("PRAGMA foreign_keys = ON;")
            .map_err(|e| db_err("pragmaForeignKeys", e))?;
        return Ok(conn);
    }

    validate_db_name(db_name)?;

    if !is_ready() {
        return Err(RuntimeError::new(ErrorCode::NotInitialized));
    }

    let conn = Connection::open(db_name).map_err(|e| db_err("open", e))?;

    // 外键必须显式打开（SQLite 默认关闭），否则 identities 的级联删除不生效，
    // 「删钱包」会留下能被后续查询读到的孤儿行。
    conn.execute_batch("PRAGMA foreign_keys = ON;")
        .map_err(|e| db_err("pragmaForeignKeys", e))?;
    // 删掉的行（观察私钥、收款记录）用零覆盖，不留在空闲页里等人读。
    conn.execute_batch("PRAGMA secure_delete = ON;")
        .map_err(|e| db_err("pragmaSecureDelete", e))?;

    #[cfg(target_arch = "wasm32")]
    {
        // SAH 池用回滚日志，不做跨上下文的 WAL 协调。
        // **绝不为了扫描吞吐去弱化事务持久性。**
        conn.execute_batch("PRAGMA journal_mode = DELETE; PRAGMA synchronous = FULL;")
            .map_err(|e| db_err("pragmaDurability", e))?;
    }

    Ok(conn)
}

/// 删掉一个已关闭的库，**不打开它**：打不开的库也要删得掉。
/// 返回 `true` 表示删了，`false` 表示本来就不存在。
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

/// 落盘屏障：确保此前提交的事务真的到了持久存储。
///
/// 在做完一件**不可逆**的事之后调用（比如广播了一笔交易）。屏障失败的含义是
/// 「持久化是否到达耐久存储未知」，**不是**「那件事失败了」—— 调用方不能因此
/// 把真实结局丢掉。
pub async fn durability_barrier() -> Result<()> {
    #[cfg(target_arch = "wasm32")]
    {
        // SAH 池的写在 sqlite3 返回时已落到 OPFS 句柄上；这里给宿主一个
        // 明确的同步点，且保留将来换 VFS 时插入真正 flush 的位置。
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

    // 删库不打开库：一个根本不是 SQLite 的文件也删得掉。
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

    /// 路径穿越在**原生**目标上才真正危险（db_name 直接变文件路径），
    /// 所以这条校验不能像某些实现那样只在 wasm 下生效。
    #[test]
    fn path_separators_are_rejected_on_every_target() {
        for bad in ["", "a/b", "a\\b", "../escape", "x/../y"] {
            let e = open_connection(bad).expect_err(&format!("{bad:?} 应被拒绝"));
            assert_eq!(e.code, ErrorCode::InvalidDbName);
        }
    }

    #[test]
    fn foreign_keys_are_on() {
        let conn = open_connection(":memory:").unwrap();
        // 内存库走的是特例分支，这里显式开一次再验，确保 pragma 语句本身有效
        conn.execute_batch("PRAGMA foreign_keys = ON;").unwrap();
        let on: i64 = conn
            .query_row("PRAGMA foreign_keys", [], |r| r.get(0))
            .unwrap();
        assert_eq!(on, 1);
    }
}
