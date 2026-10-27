//! Error codes: a stable contract across the wasm boundary.
//!
//! The host branches on `code` and fills i18n placeholders from `params`.
//! `detail` is log-only, not user-facing or translated; it may carry raw
//! upstream text.
//!
//! **Never change existing values.** The host compares them as strings.

use serde_json::{json, Map, Value};
use std::fmt;

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    // ── Input validation ────────────────────────────────────────────────────────
    /// Network name is not mainnet/stagenet/testnet. Params: `{ value }`
    InvalidNetwork,
    /// Address does not parse or belongs to another network. Params: `{ field }`
    InvalidAddress,
    /// Key/seed has the wrong byte length. Params: `{ byteLen, expected }`
    InvalidKeyMaterial,
    /// Invalid subaddress index (major and minor cannot both be 0; that is the primary address).
    InvalidSubaddressIndex,
    /// Invalid height range (from > to, or to is past the chain tip). Params: `{ from, to }`
    InvalidBlockRange,
    /// Malformed JSON from the host. Params: `{ field, reason }`
    InvalidRequest,

    // ── Lifecycle ────────────────────────────────────────────────────────
    /// `openWallet` has not been called.
    WalletNotOpen,
    /// An async operation is in flight; wallet-switching calls are refused. Params: `{ operation }`
    WalletBusy,
    /// Storage not initialized (on wasm: VFS not installed).
    NotInitialized,
    /// The browser lacks the OPFS API the storage needs (an outdated WebView,
    /// or a context with no usable origin). Retrying cannot help.
    /// Params: `{ missing }`
    StorageUnsupported,
    /// Account not in the database. Params: `{ accountId }`
    AccountNotFound,

    // ── Scanning ────────────────────────────────────────────────────────────
    /// Node blocks do not chain onto local records: a reorg or a lying node.
    /// Params: `{ atHeight, suggestedRewindHeight }`
    ReorgDetected,
    /// **Node-reported RingCT output index differs from the locally derived one.**
    ///
    /// Upstream warns this field is unverifiable and clients must track and
    /// compare it themselves; a mismatch means the node serves a false view,
    /// so disconnect rather than keep scanning.
    /// Params: `{ height, claimed, expected }`
    RingCtIndexMismatch,
    /// Scanner rejected the block (upstream `ScanError`). Params: `{ height }`
    ScanFailed,

    // ── External ────────────────────────────────────────────────────────
    /// No node URL configured.
    NodeNotConfigured,
    /// Node is on a different network than the wallet (by genesis block), or
    /// its chain is unrecognized.
    /// Params: `{ nodeNetwork, walletNetwork }` or `{ nodeGenesis }`
    NodeNetworkMismatch,
    /// Network request failed or timed out. Params: `{ operation }`
    NetworkError,
    /// Node response does not decode (bad epee/JSON). Params: `{ operation }`
    MalformedNodeResponse,
    /// Node explicitly rejected the transaction (double spend, low fee, ...). The fresh record is undone and its inputs released.
    BroadcastRejected,

    // ── Storage ────────────────────────────────────────────────────────────
    /// SQLite operation failed. Params: `{ operation }`
    DatabaseError,
    /// Database migrated by a newer runtime; preserve it and update the app.
    DatabaseTooNew,
    /// Invalid database name (empty or contains a path separator). Params: `{ value }`
    InvalidDbName,

    /// Capability not implemented yet. Params: `{ what }`
    Unimplemented,
}

impl ErrorCode {
    /// Stable string form. **Never change existing values.**
    pub fn as_str(self) -> &'static str {
        use ErrorCode::*;
        match self {
            InvalidNetwork => "INVALID_NETWORK",
            InvalidAddress => "INVALID_ADDRESS",
            InvalidKeyMaterial => "INVALID_KEY_MATERIAL",
            InvalidSubaddressIndex => "INVALID_SUBADDRESS_INDEX",
            InvalidBlockRange => "INVALID_BLOCK_RANGE",
            InvalidRequest => "INVALID_REQUEST",
            WalletNotOpen => "WALLET_NOT_OPEN",
            WalletBusy => "WALLET_BUSY",
            NotInitialized => "NOT_INITIALIZED",
            StorageUnsupported => "STORAGE_UNSUPPORTED",
            AccountNotFound => "ACCOUNT_NOT_FOUND",
            ReorgDetected => "REORG_DETECTED",
            RingCtIndexMismatch => "RINGCT_INDEX_MISMATCH",
            ScanFailed => "SCAN_FAILED",
            NodeNotConfigured => "NODE_NOT_CONFIGURED",
            NodeNetworkMismatch => "NODE_NETWORK_MISMATCH",
            NetworkError => "NETWORK_ERROR",
            MalformedNodeResponse => "MALFORMED_NODE_RESPONSE",
            BroadcastRejected => "BROADCAST_REJECTED",
            DatabaseError => "DATABASE_ERROR",
            DatabaseTooNew => "DATABASE_TOO_NEW",
            InvalidDbName => "INVALID_DB_NAME",
            Unimplemented => "UNIMPLEMENTED",
        }
    }
}

#[derive(Debug)]
pub struct RuntimeError {
    pub code: ErrorCode,
    /// Structured params for host i18n placeholders. **Always a JSON object.**
    pub params: Value,
    /// Raw upstream error. **Log-only**; not user-facing, not translated.
    pub detail: Option<String>,
}

impl RuntimeError {
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
        json!({
            "code": self.code.as_str(),
            "params": self.params,
            "detail": self.detail,
        })
    }
}

impl fmt::Display for RuntimeError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.to_json())
    }
}

impl std::error::Error for RuntimeError {}

impl From<rusqlite::Error> for RuntimeError {
    fn from(e: rusqlite::Error) -> Self {
        RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "sqlite" })).detail(e)
    }
}

pub type Result<T> = std::result::Result<T, RuntimeError>;

/// Tags a database error with the operation that failed.
pub fn db_err(operation: &str, e: impl fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(e)
}

pub fn net_err(operation: &str, e: impl fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

#[cfg(target_arch = "wasm32")]
impl From<RuntimeError> for wasm_bindgen::JsValue {
    fn from(e: RuntimeError) -> Self {
        // A JSON string rather than an Error object: the host always
        // `JSON.parse`s it to get code/params, with a single code path.
        wasm_bindgen::JsValue::from_str(&e.to_json().to_string())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashSet;

    /// Codes must be unique SCREAMING_SNAKE: the host compares strings, so a
    /// duplicate would merge two errors on the host side.
    #[test]
    fn codes_are_unique_and_screaming_snake() {
        use ErrorCode::*;
        let all = [
            InvalidNetwork,
            InvalidAddress,
            InvalidKeyMaterial,
            InvalidSubaddressIndex,
            InvalidBlockRange,
            InvalidRequest,
            WalletNotOpen,
            WalletBusy,
            NotInitialized,
            StorageUnsupported,
            AccountNotFound,
            ReorgDetected,
            RingCtIndexMismatch,
            ScanFailed,
            NodeNotConfigured,
            NetworkError,
            MalformedNodeResponse,
            BroadcastRejected,
            DatabaseError,
            InvalidDbName,
            Unimplemented,
        ];
        let mut seen = HashSet::new();
        for code in all {
            let s = code.as_str();
            assert!(seen.insert(s), "duplicate code: {s}");
            assert!(
                !s.is_empty()
                    && s.chars()
                        .all(|c| c.is_ascii_uppercase() || c.is_ascii_digit() || c == '_'),
                "malformed code: {s}"
            );
        }
    }

    /// `params` is always an object, so the host can read fields without type checks.
    #[test]
    fn params_default_to_empty_object() {
        let e = RuntimeError::new(ErrorCode::WalletNotOpen);
        assert_eq!(e.to_json()["params"], json!({}));
        assert!(e.to_json()["detail"].is_null());
    }

    #[test]
    fn json_shape_is_stable() {
        let e = RuntimeError::with(ErrorCode::InvalidBlockRange, json!({ "from": 5, "to": 1 }))
            .detail("from > to");
        let v = e.to_json();
        assert_eq!(v["code"], "INVALID_BLOCK_RANGE");
        assert_eq!(v["params"]["from"], 5);
        assert_eq!(v["detail"], "from > to");
    }
}
