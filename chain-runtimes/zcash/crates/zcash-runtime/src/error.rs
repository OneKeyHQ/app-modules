//! 错误契约。
//!
//! ## 原则
//!
//! **Rust 侧不产出面向用户的文案。** 抛给宿主的是「稳定错误码 + 结构化参数」，
//! 文案与 i18n 全部由 monorepo 那边根据错误码渲染。
//!
//! 这不只是分层洁癖：文案编进 wasm 意味着改一句提示语就要重编、重发包、
//! （移动端还要）过应用商店。错误码和参数是契约，文案是产品。
//!
//! ## 宿主怎么用
//!
//! ```js
//! try {
//!   await rt.syncAdvance(1000);
//! } catch (e) {
//!   // e.code   稳定错误码，用它做分支
//!   // e.params 结构化参数，用它填 i18n 占位符
//!   // e.detail 上游库的原始报错，只用于日志，不要展示
//!   switch (e.code) {
//!     case 'INSUFFICIENT_FUNDS':
//!       toast(t('zcash.insufficient', { need: e.params.requiredZat }));
//!       break;
//!   }
//! }
//! ```
//!
//! ## 演进规则
//!
//! 错误码只允许**追加**，不允许改动已有取值的含义或删除 —— 宿主的分支依赖它。
//! 参数字段同理：可以加，不可以改语义。

use serde_json::{json, Map, Value};
use std::fmt;

/// 稳定错误码。宿主按此做分支，不要去匹配 `detail` 的文本。
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    // ── 生命周期 ──
    /// 尚未 `init()`，存储层未安装。
    NotInitialized,
    /// 尚未 `openWallet()`。
    WalletNotOpen,
    /// 尚未 `setLightwalletdUrl()`。
    LightwalletdNotConfigured,

    // ── 入参校验 ──
    /// 网络名不是 "main" / "test"。参数：`{ value }`
    InvalidNetwork,
    /// 数据库名为空或含路径分隔符。参数：`{ value }`
    InvalidDbName,
    /// 收款地址无法解析。参数：`{ value }`
    InvalidAddress,
    /// UFVK 无法解析。
    InvalidUfvk,
    /// memo 非法（如超长，或给了透明地址）。参数：`{ byteLen }`
    InvalidMemo,
    /// 账户 id 不是合法 UUID。参数：`{ value }`
    InvalidAccountId,
    /// 金额超出 Zatoshis 范围。参数：`{ valueZat }`
    AmountOutOfRange,
    /// 确认数策略不合法（须 >= 1 且 trusted <= untrusted）。
    /// 参数：`{ trusted, untrusted }`
    InvalidConfirmationsPolicy,
    /// 批大小为 0 或越界。参数：`{ value }`
    InvalidBatchSize,
    /// 区块高度越界。参数：`{ value }`
    InvalidBlockHeight,

    // ── 业务状态 ──
    /// 找不到该账户。参数：`{ accountId }`
    AccountNotFound,
    /// 尚未同步任何区块，读不到钱包摘要。
    NotSynced,
    /// 余额不足。参数：`{ requiredZat, availableZat }`（能取到时）
    InsufficientFunds,
    /// 屏蔽余额不足，但透明余额本可补上 —— 钱在，只是在透明池里。
    ///
    /// 与 `InsufficientFunds` 分开，是因为对用户来说这两件事完全不同：
    /// 一个是「钱不够」，一个是「钱够但要先屏蔽」。合成一个码的话，宿主只能
    /// 显示「余额不足」，而用户明明看着账上有钱 —— 那种提示只会让人去猜。
    ///
    /// 参数：`{ requiredZat, shieldedAvailableZat, shortfallZat,
    ///          transparentZat, transparentCoinbaseZat }`
    FundsNeedShielding,
    /// 差额只有 coinbase 输出能补上，而本钱包花不了 coinbase。
    ///
    /// 转账用 `NonCoinbase`、屏蔽用 `NonCoinbaseOnly`，两条路都绕开它 ——
    /// 矿工场景不在目标内。所以这笔钱在本钱包里**没有任何出口**，
    /// 告诉用户「先屏蔽」等于把人往死路上指：屏蔽同样碰不到它。
    ///
    /// 参数同 `FundsNeedShielding`。
    CoinbaseFundsUnspendable,

    // ── 外部依赖 ──
    /// lightwalletd 网络错误。参数：`{ operation }`
    NetworkError,
    /// 钱包数据库错误（含 schema 迁移失败）。参数：`{ operation }`
    DatabaseError,
    /// 检测到链重组。参数：`{ atHeight, suggestedRewindHeight }`
    ///
    /// **不是故障，是正常事件** —— 链尖几个块被替换了。宿主应当调 `rewindTo`
    /// 到建议高度然后继续同步。压成 `DATABASE_ERROR` 会让宿主无从判断该重试
    /// 还是该回退、更不知道退到哪里。
    ReorgDetected,
    /// 正在进行一次异步操作，此刻不能切换/关闭钱包。参数：`{ operation }`
    ///
    /// 这不是内部错误而是**并发保护**：中途换库会让进行中的操作把结果写到
    /// 错误的库上，且没有任何迹象。宿主应当序列化对同一 runtime 的调用，
    /// 或等当前操作结束后重试。
    WalletBusy,
    /// 扫描优先级名称不合法。参数：`{ value }`
    InvalidScanPriority,
    /// No privacy identity was explicitly enabled for this scan.
    ///
    /// An empty active set must never fall back to every UFVK stored in the runtime database.
    NoActiveAccounts,
    /// reservationId 不是 64 位 hex。参数：`{ value }`
    InvalidReservationId,
    /// txid 格式不合法（不是 64 位 hex）。参数：`{ value }` 或 `{ value, byteLen }`
    InvalidTxid,
    /// PCZT 解析或序列化失败。参数：`{ stage }`
    PcztError,
    /// 交易构造失败（选币、费用、anchor 等）。参数：`{ stage }`
    TransactionBuildError,
    /// 交易已提交但被节点拒绝。参数：`{ errorCode }`，`detail` 里是节点原文。
    ///
    /// 与 `NETWORK_ERROR` 分开：网络错误可以原样重试，被拒绝**不能** ——
    /// 重发同一笔只会再被拒一次。宿主该做的是查明原因（费用不足、双花、
    /// 共识分支不对），而不是退避重试。
    BroadcastRejected,
    /// 本地交易没有持久化的广播授权。参数：`{ txid }`。
    BroadcastIntentNotFound,
    /// 广播授权存在，但当前状态不允许广播。参数：`{ txid, state }`。
    BroadcastIntentNotReady,
    /// 交易可能已写入内存钱包，但持久化结果无法确认。不得解锁或重建交易。
    FinalizeOutcomeUnknown,

    /// 该能力尚未实现。参数：`{ what }`
    Unimplemented,
}

impl ErrorCode {
    /// 稳定字符串形式。**不要改动已有取值。**
    pub fn as_str(self) -> &'static str {
        use ErrorCode::*;
        match self {
            NotInitialized => "NOT_INITIALIZED",
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
            FundsNeedShielding => "FUNDS_NEED_SHIELDING",
            CoinbaseFundsUnspendable => "COINBASE_FUNDS_UNSPENDABLE",
            NetworkError => "NETWORK_ERROR",
            DatabaseError => "DATABASE_ERROR",
            ReorgDetected => "REORG_DETECTED",
            WalletBusy => "WALLET_BUSY",
            InvalidScanPriority => "INVALID_SCAN_PRIORITY",
            NoActiveAccounts => "NO_ACTIVE_ACCOUNTS",
            InvalidReservationId => "INVALID_RESERVATION_ID",
            InvalidTxid => "INVALID_TXID",
            PcztError => "PCZT_ERROR",
            TransactionBuildError => "TRANSACTION_BUILD_ERROR",
            BroadcastRejected => "BROADCAST_REJECTED",
            BroadcastIntentNotFound => "BROADCAST_INTENT_NOT_FOUND",
            BroadcastIntentNotReady => "BROADCAST_INTENT_NOT_READY",
            FinalizeOutcomeUnknown => "FINALIZE_OUTCOME_UNKNOWN",
            Unimplemented => "UNIMPLEMENTED",
        }
    }
}

#[derive(Debug)]
pub struct RuntimeError {
    pub code: ErrorCode,
    /// 结构化参数，供宿主填 i18n 占位符。永远是一个 JSON 对象。
    pub params: Value,
    /// 上游库的原始报错。**仅用于日志**，不面向用户，不做 i18n。
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

    /// 带结构化参数。`params` 应当是一个 JSON 对象。
    pub fn with(code: ErrorCode, params: Value) -> Self {
        Self {
            code,
            params,
            detail: None,
        }
    }

    /// 附上游原始报错，仅供日志。
    pub fn detail(mut self, d: impl fmt::Display) -> Self {
        self.detail = Some(d.to_string());
        self
    }

    /// 序列化成宿主能直接消费的 JSON。
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
        // 面向日志，不面向用户。
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

/// 抛给 JS 的形状：`Error` 对象上挂 `code` / `params` / `detail`。
///
/// `message` 故意只放错误码 —— 它不是给用户看的文案，
/// 宿主拿 `code` 去查自己的 i18n 表。
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

        // 同一份内容再放进 `payload`。
        //
        // 宿主把错误跨进程/跨 WebView 传递时，序列化器只转发一份**固定的**字段
        // 名单，`params` 与 `detail` 不在其中 —— 平铺的那几个字段只有直接调用
        // （不跨载体）时才到得了对面，跨载体就只剩 `code`。`payload` 在名单里，
        // 所以结构化参数必须搭这班车。
        //
        // 两份都留：平铺的是本 crate 的契约，宿主直接调时读它；`payload` 是给
        // 跨载体那条路用的。内容相同，不存在哪份更新的问题。
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
            InvalidReservationId,
            InvalidTxid,
            PcztError,
            TransactionBuildError,
            BroadcastRejected,
            Unimplemented,
        ];
        let mut seen = std::collections::HashSet::new();
        for c in all {
            let s = c.as_str();
            assert!(seen.insert(s), "错误码重复: {s}");
            assert!(
                s.chars().all(|ch| ch.is_ascii_uppercase() || ch == '_'),
                "错误码必须是 SCREAMING_SNAKE_CASE: {s}"
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
