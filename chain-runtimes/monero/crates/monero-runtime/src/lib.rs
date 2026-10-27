//! OneKey Monero runtime: monero-oxide scanning and signing with SQLite persistence.
//!
//! Rust owns cryptography, protocol and storage; the host owns scan scheduling.

pub mod blocks_bin;
pub mod error;
pub mod keys;
pub mod network;
pub mod perf;
pub mod pool;
pub mod query;
pub mod review;
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

/// Pinned upstream versions, for host self-checks and logs.
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "monero-wallet": "0.2.0",
        "monero-address": "0.1.0",
        "runtime": env!("CARGO_PKG_VERSION"),
    })
}
