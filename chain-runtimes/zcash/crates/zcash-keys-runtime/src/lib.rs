//! # OneKey Zcash Keys
//!
//! Key derivation, signing, and export of public viewing material (UFVK /
//! addresses). No storage, chain scanning, or network, so mobile builds avoid
//! SQLite and the scan engine.
//!
//! ## Key handling
//!
//! - No seed or mnemonic crosses the wasm boundary. Software signing takes only
//!   account-scoped keys: the 32-byte Orchard spending key (`m/32'/133'/account'`)
//!   and the transparent account xprv (`m/44'/133'/account'`). Mainnet only.
//! - Spending keys never cross the boundary outward; JS receives UFVKs, addresses,
//!   and signed transactions.
//! - Secret inputs, including the bindgen copies, are wrapped in `Zeroizing` and wiped after use.
//! - The xprv is parsed without heap copies, and the parsed account keys are wiped on
//!   drop (`account_keys::Wiped`). Known limit: upstream `SpendingKey`, `AccountPrivKey`,
//!   and `secp256k1::SecretKey` lack `ZeroizeOnDrop`, so keys derived from them inside
//!   upstream code (child secret keys, spend authorizing keys) and moved-from stack
//!   copies are not wiped here; the host wipes the wasm stack after each call.

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

/// Pinned upstream dependency versions.
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_keys": "0.16.1",
        "zcash_primitives": "0.30.1",
        "zcash_transparent": "0.10.0",
        "crate": env!("CARGO_PKG_VERSION"),
    })
}
