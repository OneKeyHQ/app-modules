//! Error contract.
//!
//! The Rust side produces no user-facing text. The host receives a stable error
//! code plus structured params and renders copy and i18n from the code. Text baked
//! into wasm would need a rebuild and release (and an app-store review on mobile)
//! for every wording change.
//!
//! ## Host usage
//!
//! ```js
//! try {
//!   await rt.syncAdvance(1000);
//! } catch (e) {
//!   // e.code   stable error code; branch on it
//!   // e.params structured params; fill i18n placeholders
//!   // e.detail raw upstream error; logs only, never display
//!   switch (e.code) {
//!     case 'INSUFFICIENT_FUNDS':
//!       toast(t('zcash.insufficient', { need: e.params.requiredZat }));
//!       break;
//!   }
//! }
//! ```
//!
//! ## Evolution
//!
//! Error codes and param fields are append-only: never change the meaning of or
//! remove an existing one, since host branching depends on them.

use serde_json::{json, Map, Value};
use std::fmt;

/// Stable error code. Hosts branch on this, never on `detail` text.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    // ── Lifecycle ──
    /// `init()` not called; storage layer not installed.
    NotInitialized,
    /// The browser lacks the OPFS API the storage needs (an outdated WebView,
    /// or a context with no usable origin). Retrying cannot help.
    /// Params: `{ missing }`
    StorageUnsupported,
    /// `openWallet()` not called.
    WalletNotOpen,
    /// `setLightwalletdUrl()` not called.
    LightwalletdNotConfigured,

    // ── Input validation ──
    /// Network name is not "main" / "test". Params: `{ value }`
    InvalidNetwork,
    /// Database name is empty or contains a path separator. Params: `{ value }`
    InvalidDbName,
    /// Recipient address cannot be parsed. Params: `{ value }`
    InvalidAddress,
    /// UFVK cannot be parsed.
    InvalidUfvk,
    /// Invalid memo (too long, or sent to a transparent address). Params: `{ byteLen }`
    InvalidMemo,
    /// Account id is not a valid UUID. Params: `{ value }`
    InvalidAccountId,
    /// Amount outside the Zatoshis range. Params: `{ valueZat }`
    AmountOutOfRange,
    /// Invalid confirmations policy (requires >= 1 and trusted <= untrusted).
    /// Params: `{ trusted, untrusted }`
    InvalidConfirmationsPolicy,
    /// Batch size is 0 or out of range. Params: `{ value }`
    InvalidBatchSize,
    /// Block height out of range. Params: `{ value }`
    InvalidBlockHeight,

    // ── Wallet state ──
    /// Account not found. Params: `{ accountId }`
    AccountNotFound,
    /// No blocks synced yet; wallet summary unavailable.
    NotSynced,
    /// Insufficient funds. Params: `{ requiredZat, availableZat }` (when available)
    InsufficientFunds,

    // ── External dependencies ──
    /// lightwalletd network error. Params: `{ operation }`
    NetworkError,
    /// Wallet database error (including schema migration failure). Params: `{ operation }`
    DatabaseError,
    /// Chain reorg detected. Params: `{ atHeight, suggestedRewindHeight }`
    ///
    /// A normal event, not a failure: blocks near the tip were replaced. The host
    /// should `rewindTo` the suggested height and resume syncing.
    ReorgDetected,
    /// An async operation is in flight; the wallet cannot be switched or closed now.
    /// Params: `{ operation }`
    ///
    /// Concurrency guard: swapping databases mid-operation would silently write
    /// results to the wrong wallet. Hosts should serialize calls to one runtime or
    /// retry after the current operation finishes.
    WalletBusy,
    /// Invalid scan priority name. Params: `{ value }`
    InvalidScanPriority,
    /// No privacy identity was explicitly enabled for this scan.
    ///
    /// An empty active set must never fall back to every UFVK stored in the runtime database.
    NoActiveAccounts,
    /// Malformed txid (not 64 hex chars). Params: `{ value }` or `{ value, byteLen }`
    InvalidTxid,
    /// PCZT parse or serialization failed. Params: `{ stage }`
    PcztError,
    /// Transaction construction failed (note selection, fee, anchor, etc.). Params: `{ stage }`
    TransactionBuildError,
    /// The node definitively judged the transaction consensus-invalid or expired; it
    /// can never be mined. Params: `{ errorCode }`; `detail` holds the node's message.
    ///
    /// Unlike `NETWORK_ERROR`, this must not be retried as-is. The transaction is
    /// already stored; its spent notes return only at the expiry height.
    BroadcastRejected,
    /// The node returned an error that does not prove the transaction can never be
    /// mined: it may already be mined or in the mempool (double spend, duplicate
    /// nullifier, missing inputs), or the node may be temporarily refusing (busy,
    /// mempool full, lagging). The transaction stays pending and is rebroadcast
    /// during sync; scanning resolves it as mined or expired. Hosts must not treat
    /// this as a failed send. Params: `{ errorCode }`; `detail` holds the node's message.
    BroadcastOutcomeUnknown,
    /// The transaction may be in the in-memory wallet but persistence is unconfirmed.
    /// Do not unlock or rebuild the transaction.
    FinalizeOutcomeUnknown,

    /// Capability not implemented. Params: `{ what }`
    Unimplemented,
}

impl ErrorCode {
    /// Stable string form. Never change existing values.
    pub fn as_str(self) -> &'static str {
        use ErrorCode::*;
        match self {
            NotInitialized => "NOT_INITIALIZED",
            StorageUnsupported => "STORAGE_UNSUPPORTED",
            WalletNotOpen => "WALLET_NOT_OPEN",
            LightwalletdNotConfigured => "LIGHTWALLETD_NOT_CONFIGURED",
            InvalidNetwork => "INVALID_NETWORK",
            InvalidDbName => "INVALID_DB_NAME",
            InvalidAddress => "INVALID_ADDRESS",
            InvalidUfvk => "INVALID_UFVK",
            InvalidMemo => "INVALID_MEMO",
            InvalidAccountId => "INVALID_ACCOUNT_ID",
            AmountOutOfRange => "AMOUNT_OUT_OF_RANGE",
            InvalidConfirmationsPolicy => "INVALID_CONFIRMATIONS_POLICY",
            InvalidBatchSize => "INVALID_BATCH_SIZE",
            InvalidBlockHeight => "INVALID_BLOCK_HEIGHT",
            AccountNotFound => "ACCOUNT_NOT_FOUND",
            NotSynced => "NOT_SYNCED",
            InsufficientFunds => "INSUFFICIENT_FUNDS",
            NetworkError => "NETWORK_ERROR",
            DatabaseError => "DATABASE_ERROR",
            ReorgDetected => "REORG_DETECTED",
            WalletBusy => "WALLET_BUSY",
            InvalidScanPriority => "INVALID_SCAN_PRIORITY",
            NoActiveAccounts => "NO_ACTIVE_ACCOUNTS",
            InvalidTxid => "INVALID_TXID",
            PcztError => "PCZT_ERROR",
            TransactionBuildError => "TRANSACTION_BUILD_ERROR",
            BroadcastRejected => "BROADCAST_REJECTED",
            BroadcastOutcomeUnknown => "BROADCAST_OUTCOME_UNKNOWN",
            FinalizeOutcomeUnknown => "FINALIZE_OUTCOME_UNKNOWN",
            Unimplemented => "UNIMPLEMENTED",
        }
    }
}

#[derive(Debug)]
pub struct RuntimeError {
    pub code: ErrorCode,
    /// Structured params for host i18n placeholders. Always a JSON object.
    pub params: Value,
    /// Raw upstream error. Logs only; not user-facing, not localized.
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

    /// With structured params. `params` should be a JSON object.
    pub fn with(code: ErrorCode, params: Value) -> Self {
        Self {
            code,
            params,
            detail: None,
        }
    }

    /// Attach the raw upstream error, for logs only.
    pub fn detail(mut self, d: impl fmt::Display) -> Self {
        self.detail = Some(d.to_string());
        self
    }

    /// Serialize to JSON the host can consume directly.
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
        // For logs, not users.
        write!(f, "{} {}", self.code.as_str(), self.params)?;
        if let Some(d) = &self.detail {
            write!(f, " :: {d}")?;
        }
        Ok(())
    }
}

impl std::error::Error for RuntimeError {}

impl From<rusqlite::Error> for RuntimeError {
    fn from(e: rusqlite::Error) -> Self {
        RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "sqlite" })).detail(e)
    }
}

pub type Result<T> = std::result::Result<T, RuntimeError>;

/// JS shape: an `Error` carrying `code` / `params` / `detail`.
///
/// `message` is just the error code; it is not user-facing text. Hosts map `code`
/// through their own i18n table.
#[cfg(target_arch = "wasm32")]
impl From<RuntimeError> for wasm_bindgen::JsValue {
    fn from(e: RuntimeError) -> Self {
        use wasm_bindgen::JsCast;

        let err = js_sys::Error::new(e.code.as_str());
        err.set_name(e.code.as_str());

        let obj: js_sys::Object = err.unchecked_into();
        let set = |k: &str, v: wasm_bindgen::JsValue| {
            let _ = js_sys::Reflect::set(&obj, &wasm_bindgen::JsValue::from_str(k), &v);
        };

        let params = js_sys::JSON::parse(&e.params.to_string())
            .unwrap_or_else(|_| js_sys::Object::new().into());

        set("code", wasm_bindgen::JsValue::from_str(e.code.as_str()));
        set("params", params.clone());
        if let Some(d) = &e.detail {
            set("detail", wasm_bindgen::JsValue::from_str(d));
        }

        // Duplicate the same fields under `payload`.
        //
        // When the host forwards errors across processes or WebViews, its serializer
        // only passes a fixed field allowlist that includes `payload` but not `params`
        // or `detail`. The flat fields are this crate's contract for direct calls;
        // `payload` carries them across that boundary.
        let payload = js_sys::Object::new();
        let pset = |k: &str, v: wasm_bindgen::JsValue| {
            let _ = js_sys::Reflect::set(&payload, &wasm_bindgen::JsValue::from_str(k), &v);
        };
        pset("code", wasm_bindgen::JsValue::from_str(e.code.as_str()));
        pset("params", params);
        if let Some(d) = &e.detail {
            pset("detail", wasm_bindgen::JsValue::from_str(d));
        }
        set("payload", payload.into());

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
            NotInitialized,
            WalletNotOpen,
            LightwalletdNotConfigured,
            InvalidNetwork,
            InvalidDbName,
            InvalidAddress,
            InvalidUfvk,
            InvalidMemo,
            InvalidAccountId,
            AmountOutOfRange,
            InvalidConfirmationsPolicy,
            InvalidBatchSize,
            InvalidBlockHeight,
            NoActiveAccounts,
            AccountNotFound,
            NotSynced,
            InsufficientFunds,
            NetworkError,
            DatabaseError,
            InvalidTxid,
            PcztError,
            TransactionBuildError,
            BroadcastRejected,
            BroadcastOutcomeUnknown,
            Unimplemented,
            ReorgDetected,
            WalletBusy,
            InvalidScanPriority,
            FinalizeOutcomeUnknown,
        ];
        let mut seen = std::collections::HashSet::new();
        for c in all {
            let s = c.as_str();
            assert!(seen.insert(s), "duplicate error code: {s}");
            assert!(
                s.chars().all(|ch| ch.is_ascii_uppercase() || ch == '_'),
                "error code must be SCREAMING_SNAKE_CASE: {s}"
            );
        }
    }

    #[test]
    fn json_shape_is_stable() {
        let e = RuntimeError::with(
            ErrorCode::AmountOutOfRange,
            json!({ "valueZat": 2_100_000_000_000_001_u64 }),
        )
        .detail("upstream says no");
        let j = e.to_json();
        assert_eq!(j["code"], "AMOUNT_OUT_OF_RANGE");
        assert_eq!(j["params"]["valueZat"], 2_100_000_000_000_001_u64);
        assert_eq!(j["detail"], "upstream says no");
    }

    #[test]
    fn params_default_to_empty_object() {
        let e = RuntimeError::new(ErrorCode::WalletNotOpen);
        assert!(e.to_json()["params"].is_object());
        assert_eq!(e.to_json()["detail"], serde_json::Value::Null);
    }
}
