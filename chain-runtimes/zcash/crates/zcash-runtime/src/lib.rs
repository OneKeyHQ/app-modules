//! # OneKey Zcash Runtime
//!
//! A thin binding layer over `librustzcash`: cryptography and protocol (trial
//! decryption, trees, transaction building, note selection), platform glue
//! (clock, randomness, storage VFS) and Rust-to-JSON shape conversion.
//! Scheduling, accounts, caching, progress and retries belong to the host. Apart
//! from the database, wasm holds no state across calls.

pub mod account;
pub mod blockcache;
mod circuit_keys;
pub mod clock;
pub mod error;
pub mod history;
pub mod send;
pub mod stats;
pub mod storage;
pub mod tx_state;
pub mod wallet;

pub mod network;
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
