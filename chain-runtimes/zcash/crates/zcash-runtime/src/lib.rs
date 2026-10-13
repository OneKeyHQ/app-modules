//! # OneKey Zcash Runtime
//!
//! A thin binding layer over the official `librustzcash`.
//!
//! ## Scope
//!
//! Only three things live on this side:
//!
//! 1. **Cryptography and protocol**: trial decryption, witness and commitment
//!    tree maintenance, transaction building and note selection. Costly to move
//!    across the boundary, and mistakes lose funds.
//! 2. **Platform glue**: clock, randomness, storage VFS, which std lacks on wasm32.
//! 3. **Shape conversion**: Rust types to JSON for the host.
//!
//! Scheduling, account and birthday management, caching, progress and retries
//! belong to the host.
//!
//! ## Interface
//!
//! Few verbs, simple data. Each call is a full round trip; apart from the
//! database, wasm holds no state across calls.

// ── All platforms: wallet core ─────────────────────────────────────────
// Builds on wasm32, aarch64-apple-ios and Android, so mobile can run the full
// wallet, not just keys.
pub mod account;
pub mod blockcache;
mod circuit_keys;
pub mod clock;
pub mod error;
pub mod guard;
pub mod history;
pub mod send;
pub mod stats;
pub mod storage;
pub mod tx_state;
pub mod wallet;

// Network and sync: gRPC-web in browsers, tonic transport natively.
pub mod network;
pub mod perf;
pub mod sync;

// Cross-call state uses thread_local, valid only in the single-threaded wasm host.
#[cfg(target_arch = "wasm32")]
pub mod runtime;

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

pub use error::{ErrorCode, Result, RuntimeError};

/// Upstream crate versions this runtime is built on, for host checks and logs.
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_client_backend": "0.24.0",
        "zcash_client_sqlite": "0.22.0",
        "zcash_keys": "0.16.1",
        "runtime": env!("CARGO_PKG_VERSION"),
    })
}
