//! Zcash key derivation, signing, and export of public viewing material. No storage, scanning, or network.
//!
//! No seed or mnemonic crosses the wasm boundary: software signing takes only the Orchard spending
//! key and transparent account xprv. Secrets are zeroized and never returned to JS. Mainnet only.

pub mod account_keys;
pub mod error;
pub mod keys;
pub mod review;
pub mod sign;
pub mod transparent_prevouts;
pub mod transparent_send;

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

pub use error::{ErrorCode, KeysError, Result};

pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_keys": "0.16.1",
        "zcash_primitives": "0.30.1",
        "zcash_transparent": "0.10.0",
        "crate": env!("CARGO_PKG_VERSION"),
    })
}
