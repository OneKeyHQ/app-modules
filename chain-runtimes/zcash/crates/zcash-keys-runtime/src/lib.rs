//! # OneKey Zcash Keys
//!
//! 只做两件事：从种子派生密钥、导出可公开的观察材料（UFVK / 地址）。
//!
//! **不含存储、不含扫链、不含网络。** 这是刻意的：移动端只需要这一半，
//! 不该为了派生一个地址就把 SQLite 和整个扫链引擎打进包里。
//!
//! ## 密钥纪律
//!
//! - 种子与花费密钥（USK）**只在本 crate 内部存在**，不跨 wasm 边界。
//! - JS 侧永远只拿到 UFVK 和地址；出厂边界不收助记词，只收种子字节。
//! - 种子（含调用方传进来的那份）用 `Zeroizing` 包裹，用完即擦。
//! - **但派生出的密钥擦不掉**：`UnifiedSpendingKey`、`AccountPrivKey`、
//!   `secp256k1::SecretKey` 上游都没有 `ZeroizeOnDrop`，析构后仍留在 wasm
//!   线性内存里。别把这一条读成「全链路擦干净」—— 它不是。

pub mod error;
pub mod keys;
pub mod review;
pub mod sign;
pub mod transparent_prevouts;
pub mod transparent_send;

#[cfg(target_arch = "wasm32")]
pub mod bindgen;

pub use error::{ErrorCode, KeysError, Result};

/// 本 crate 锁定的官方依赖版本。
pub fn dependency_versions() -> serde_json::Value {
    serde_json::json!({
        "zcash_keys": "0.16.1",
        "zcash_primitives": "0.30.1",
        "zcash_transparent": "0.10.0",
        "crate": env!("CARGO_PKG_VERSION"),
    })
}
