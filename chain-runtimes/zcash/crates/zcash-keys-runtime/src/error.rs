//! 错误契约。与 `onekey-zcash-runtime` 完全同构：
//! **稳定错误码 + 结构化参数，Rust 侧不产出面向用户的文案。**
//!
//! 两个包的错误码取值空间不重叠，宿主可以用同一套 catch 逻辑处理。

use serde_json::{json, Map, Value};
use std::fmt;

/// 稳定错误码。只允许追加，不允许改动已有取值的含义。
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    /// 网络名不是 "main" / "test"。参数：`{ value }`
    InvalidNetwork,
    /// 助记词无法解析。
    InvalidMnemonic,
    /// 种子过短。参数：`{ byteLen, min }`
    InvalidSeed,
    /// 账户序号越界。参数：`{ value }`
    InvalidAccountIndex,
    /// UFVK 无法解析。
    InvalidUfvk,
    /// 密钥派生失败。
    DeriveFailed,
    /// PCZT 解析失败。
    PcztParseFailed,
    /// PCZT 签名失败。参数：`{ orchardActions }`（能取到时）
    PcztSignFailed,
    /// The stateless transparent transaction request is malformed.
    InvalidTransactionRequest,
    /// The requested target height is not valid for the supported V6 mainnet branch.
    InvalidTargetHeight,
    /// A destination or change address is invalid or belongs to another network.
    InvalidAddress,
    /// An outpoint is malformed or reserved.
    InvalidOutpoint,
    /// A selected outpoint is not present in the supplied UTXO catalog.
    UnknownInput,
    /// An outpoint appears more than once.
    DuplicateInput,
    /// Coinbase inputs are intentionally unsupported by transparent send.
    CoinbaseInput,
    /// Unconfirmed transparent inputs are intentionally unsupported.
    UnconfirmedInput,
    /// Only regular P2PKH inputs are supported.
    UnsupportedInputScript,
    /// The supplied BIP-44 path is outside the exact Zcash account path.
    InvalidDerivationPath,
    /// A selected coin or change address does not match its derived key.
    KeyMismatch,
    /// A zatoshi amount is malformed or outside the consensus range.
    InvalidAmount,
    /// Selected inputs cannot cover outputs and the ZIP-317 fee.
    InsufficientFunds,
    /// The transaction would create uneconomic transparent change.
    DustChange,
    /// An account-level BIP-44 xprv is malformed or has unexpected metadata.
    InvalidAccountXprv,
    /// Transparent transaction construction or signing failed.
    TransactionBuildFailed,
    /// The signed transaction could not be serialized.
    TransactionSerializeFailed,
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
            DustChange => "DUST_CHANGE",
            InvalidAccountXprv => "INVALID_ACCOUNT_XPRV",
            TransactionBuildFailed => "TRANSACTION_BUILD_FAILED",
            TransactionSerializeFailed => "TRANSACTION_SERIALIZE_FAILED",
        }
    }
}

#[derive(Debug)]
pub struct KeysError {
    pub code: ErrorCode,
    /// 结构化参数，供宿主填 i18n 占位符。永远是 JSON 对象。
    pub params: Value,
    /// 上游原始报错，**仅用于日志**。
    ///
    /// 注意：本包处理密钥材料，任何 detail 都不得包含种子、USK 或其派生物。
    /// 上游库的错误只描述结构问题，不回显输入，所以直接透传是安全的。
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

/// 抛给 JS：`Error` 上挂 `code` / `params` / `detail`。
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
            DustChange,
            InvalidAccountXprv,
            TransactionBuildFailed,
            TransactionSerializeFailed,
        ];
        let mut seen = std::collections::HashSet::new();
        for c in all {
            let s = c.as_str();
            assert!(seen.insert(s), "错误码重复: {s}");
            assert!(
                s.chars().all(|ch| ch.is_ascii_uppercase() || ch == '_'),
                "格式不对: {s}"
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
