//! # OneKey Monero Runtime
//!
//! monero-oxide 提供密码学、扫描器与交易构造；**它不提供任何存储**。
//! 这个 crate 就是缺的那一层：把扫描产出的输出落进 SQLite，并支持查询。
//!
//! ## 为什么不是 monero-ts
//!
//! 那条路把整个钱包放在 wasm 内存里，持久化只能 `getData()` 整块序列化再写出去
//! —— 历史越长每次保存越贵，且写一半崩了丢的是全部。这里走增量写 + 事务，
//! 和 Zcash runtime 同一个模型。
//!
//! ## 设计边界
//!
//! 留在 Rust 这一侧的只有三类：密码学与协议、平台胶水（存储 VFS、时钟）、
//! 形状转换（Rust 富类型 → JSON）。**调度不在这里** —— 什么时候扫、扫哪一段，
//! 由宿主 TS 决定。

pub mod blocks_bin;
pub mod error;
pub mod keys;
pub mod network;
pub mod perf;
pub mod pool;
pub mod query;
pub mod runtime;
pub mod scan;
pub mod schema;
pub mod spend;
pub mod storage;
pub mod sync;
pub mod wallet;

pub use error::{ErrorCode, Result, RuntimeError};

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

/// 本 runtime 锁定的上游版本，供宿主自检与日志使用。
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "monero-wallet": "0.2.0",
        "monero-address": "0.1.0",
        "runtime": env!("CARGO_PKG_VERSION"),
    })
}
