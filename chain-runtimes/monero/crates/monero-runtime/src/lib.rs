//! # OneKey Monero Runtime
//!
//! monero-oxide provides cryptography, scanning, and transaction construction but
//! no storage. This crate adds it: scanned outputs are persisted to SQLite and queryable.
//!
//! ## Why not monero-ts
//!
//! monero-ts keeps the whole wallet in wasm memory and persists by serializing it
//! all via `getData()`, so saves grow with history and a crash mid-write loses
//! everything. This crate uses incremental transactional writes, like the Zcash runtime.
//!
//! ## Boundary
//!
//! Rust owns cryptography and protocol, platform glue (storage VFS, clock), and
//! conversion to JSON. Scheduling (when and what range to scan) belongs to the host.

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
