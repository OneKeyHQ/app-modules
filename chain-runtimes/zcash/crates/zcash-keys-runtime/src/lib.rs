//! # OneKey Zcash Keys
//!
//! Key derivation, signing, and export of public viewing material (UFVK /
//! addresses). No storage, chain scanning, or network, so mobile builds avoid
//! SQLite and the scan engine.
//!
//! ## Key handling
//!
//! - Seeds and spending keys (USK) stay inside this crate and never cross the wasm boundary.
//! - JS receives only UFVKs and addresses; the boundary takes seed bytes, never a mnemonic.
//! - Seeds, including the caller's copy, are wrapped in `Zeroizing` and wiped after use.
//! - Derived keys are not wiped: upstream `UnifiedSpendingKey`, `AccountPrivKey`, and
//!   `secp256k1::SecretKey` lack `ZeroizeOnDrop` and remain in wasm linear memory.

pub mod error;
pub mod keys;
pub mod review;
pub mod sign;
pub mod transparent_prevouts;
pub mod transparent_send;

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

pub use error::{ErrorCode, KeysError, Result};

/// Pinned upstream dependency versions.
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_keys": "0.16.1",
        "zcash_primitives": "0.30.1",
        "zcash_transparent": "0.10.0",
        "crate": env!("CARGO_PKG_VERSION"),
    })
}
