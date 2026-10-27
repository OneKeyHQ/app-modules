//! Stable error codes plus structured params; Rust never produces user-facing text.
//! Same shape as `onekey-zcash-runtime`; the code sets do not overlap.

use serde_json::{json, Map, Value};
use std::fmt;

/// Stable error codes. Append only; never change the meaning of an existing code.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    /// Network is not "main" / "test". Params: `{ value }`
    InvalidNetwork,
    /// Mnemonic cannot be parsed. Retired: no shipped API takes a mnemonic.
    InvalidMnemonic,
    /// Seed is too short. Retired: no shipped API takes a seed.
    InvalidSeed,
    /// Account index out of range. Params: `{ value }`
    InvalidAccountIndex,
    InvalidUfvk,
    DeriveFailed,
    PcztParseFailed,
    /// PCZT signing failed. Params: `{ orchardActions }` when available.
    PcztSignFailed,
    InvalidTransactionRequest,
    InvalidTargetHeight,
    InvalidAddress,
    InvalidOutpoint,
    UnknownInput,
    DuplicateInput,
    /// Unsupported by transparent send.
    CoinbaseInput,
    /// Unsupported by transparent send.
    UnconfirmedInput,
    /// Only regular P2PKH inputs are supported.
    UnsupportedInputScript,
    InvalidDerivationPath,
    KeyMismatch,
    InvalidAmount,
    InsufficientFunds,
    InvalidAccountXprv,
    InvalidAccountXpub,
    TransactionBuildFailed,
    TransactionSerializeFailed,
    /// Orchard spending key is not 32 bytes or fails ZIP-32 validity. Params: `{ byteLen, expected }` for a length error.
    InvalidSpendingKey,
}

impl ErrorCode {
    pub fn as_str(self) -> &'static str {
        use ErrorCode::*;
        match self {
            InvalidNetwork => "INVALID_NETWORK",
            InvalidMnemonic => "INVALID_MNEMONIC",
            InvalidSeed => "INVALID_SEED",
            InvalidAccountIndex => "INVALID_ACCOUNT_INDEX",
            InvalidUfvk => "INVALID_UFVK",
            DeriveFailed => "DERIVE_FAILED",
            PcztParseFailed => "PCZT_PARSE_FAILED",
            PcztSignFailed => "PCZT_SIGN_FAILED",
            InvalidTransactionRequest => "INVALID_TRANSACTION_REQUEST",
            InvalidTargetHeight => "INVALID_TARGET_HEIGHT",
            InvalidAddress => "INVALID_ADDRESS",
            InvalidOutpoint => "INVALID_OUTPOINT",
            UnknownInput => "UNKNOWN_INPUT",
            DuplicateInput => "DUPLICATE_INPUT",
            CoinbaseInput => "COINBASE_INPUT",
            UnconfirmedInput => "UNCONFIRMED_INPUT",
            UnsupportedInputScript => "UNSUPPORTED_INPUT_SCRIPT",
            InvalidDerivationPath => "INVALID_DERIVATION_PATH",
            KeyMismatch => "KEY_MISMATCH",
            InvalidAmount => "INVALID_AMOUNT",
            InsufficientFunds => "INSUFFICIENT_FUNDS",
            InvalidAccountXprv => "INVALID_ACCOUNT_XPRV",
            InvalidAccountXpub => "INVALID_ACCOUNT_XPUB",
            TransactionBuildFailed => "TRANSACTION_BUILD_FAILED",
            TransactionSerializeFailed => "TRANSACTION_SERIALIZE_FAILED",
            InvalidSpendingKey => "INVALID_SPENDING_KEY",
        }
    }
}

#[derive(Debug)]
pub struct KeysError {
    pub code: ErrorCode,
    /// Always a JSON object.
    pub params: Value,
    /// Raw upstream error, for logs only. Must never contain seeds, spending keys, or anything derived from them.
    pub detail: Option<String>,
}

impl KeysError {
    pub fn new(code: ErrorCode) -> Self {
        Self {
            code,
            params: Value::Object(Map::new()),
            detail: None,
        }
    }

    pub fn with(code: ErrorCode, params: Value) -> Self {
        Self {
            code,
            params,
            detail: None,
        }
    }

    pub fn detail(mut self, d: impl fmt::Display) -> Self {
        self.detail = Some(d.to_string());
        self
    }

    pub fn to_json(&self) -> Value {
        json!({ "code": self.code.as_str(), "params": self.params, "detail": self.detail })
    }
}

impl fmt::Display for KeysError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.code.as_str(), self.params)?;
        if let Some(d) = &self.detail {
            write!(f, " :: {d}")?;
        }
        Ok(())
    }
}

impl std::error::Error for KeysError {}

pub type Result<T> = std::result::Result<T, KeysError>;

#[cfg(target_arch = "wasm32")]
impl From<KeysError> for wasm_bindgen::JsValue {
    fn from(e: KeysError) -> Self {
        use wasm_bindgen::JsCast;

        let err = js_sys::Error::new(e.code.as_str());
        err.set_name(e.code.as_str());

        let obj: js_sys::Object = err.unchecked_into();
        let set = |k: &str, v: wasm_bindgen::JsValue| {
            let _ = js_sys::Reflect::set(&obj, &wasm_bindgen::JsValue::from_str(k), &v);
        };

        set("code", wasm_bindgen::JsValue::from_str(e.code.as_str()));
        set(
            "params",
            js_sys::JSON::parse(&e.params.to_string())
                .unwrap_or_else(|_| js_sys::Object::new().into()),
        );
        if let Some(d) = &e.detail {
            set("detail", wasm_bindgen::JsValue::from_str(d));
        }

        obj.into()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn codes_are_unique_and_screaming_snake() {
        use ErrorCode::*;
        let all = [
            InvalidNetwork,
            InvalidMnemonic,
            InvalidSeed,
            InvalidAccountIndex,
            InvalidUfvk,
            DeriveFailed,
            PcztParseFailed,
            PcztSignFailed,
            InvalidTransactionRequest,
            InvalidTargetHeight,
            InvalidAddress,
            InvalidOutpoint,
            UnknownInput,
            DuplicateInput,
            CoinbaseInput,
            UnconfirmedInput,
            UnsupportedInputScript,
            InvalidDerivationPath,
            KeyMismatch,
            InvalidAmount,
            InsufficientFunds,
            InvalidAccountXprv,
            InvalidAccountXpub,
            TransactionBuildFailed,
            TransactionSerializeFailed,
            InvalidSpendingKey,
        ];
        let mut seen = std::collections::HashSet::new();
        for c in all {
            let s = c.as_str();
            assert!(seen.insert(s), "duplicate error code: {s}");
            assert!(
                s.chars().all(|ch| ch.is_ascii_uppercase() || ch == '_'),
                "malformed error code: {s}"
            );
        }
    }

    #[test]
    fn json_shape_is_stable() {
        let e = KeysError::with(ErrorCode::InvalidSeed, json!({ "byteLen": 8, "min": 32 }));
        let j = e.to_json();
        assert_eq!(j["code"], "INVALID_SEED");
        assert_eq!(j["params"]["byteLen"], 8);
        assert!(j["detail"].is_null());
    }
}
