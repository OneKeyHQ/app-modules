//! # OneKey Zcash Runtime
//!
//! 官方 `librustzcash` 之上的最薄绑定层。
//!
//! ## 设计边界
//!
//! 留在这一侧（wasm / Rust）的只有三类东西：
//!
//! 1. **密码学与协议** —— 扫链的 trial decryption、witness 与承诺树维护、
//!    交易构造与选币。这些跨边界成本高、且错了会丢钱，必须留在原地。
//! 2. **平台胶水** —— 时钟、随机数、存储 VFS。std 在 wasm32 上不提供的那些。
//! 3. **形状转换** —— 把 Rust 的富类型转成 JSON 交给宿主。
//!
//! 明确**不**放在这一侧的：调度（什么时候扫、扫哪段）、账户与 birthday 管理、
//! 缓存策略、进度展示、错误重试。那些属于宿主 TS，改起来快、看得见、能打断点。
//!
//! ## 接口形状
//!
//! 动词少、数据简单。每次调用是一次完整往返，wasm 不持有跨调用的会话状态
//! （数据库除外）。这样宿主侧出问题永远能自己修，wasm 侧只在协议变化时才动。

// ── 全平台：钱包核心 ────────────────────────────────────────────────────
// 这几个模块在 wasm32、aarch64-apple-ios、Android 上都编译。
// 也就是说移动端**可以**跑完整的钱包逻辑，不必退化成 keys-only。
pub mod account;
pub mod blockcache;
pub mod clock;
pub mod error;
pub mod history;
pub mod send;
pub mod stats;
pub mod storage;
pub mod tx_state;
pub mod wallet;

// 网络与扫链现在是全平台的：浏览器走 gRPC-web，原生走 tonic transport。
// 这也让我们能在本机原生环境复现浏览器里的问题 —— 那边可以上真调试器。
pub mod network;
pub mod perf;
pub mod sync;

// 跨调用状态用 thread_local，只在单线程的 wasm 宿主里成立。
#[cfg(target_arch = "wasm32")]
pub mod runtime;

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

pub use error::{ErrorCode, Result, RuntimeError};

/// 本 runtime 依赖的官方 crate 版本，供宿主自检与日志使用。
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_client_backend": "0.24.0",
        "zcash_client_sqlite": "0.22.0",
        "zcash_keys": "0.16.1",
        "runtime": env!("CARGO_PKG_VERSION"),
    })
}
