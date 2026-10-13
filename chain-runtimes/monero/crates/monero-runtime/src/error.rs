//! 错误码：跨 wasm 边界的稳定契约。
//!
//! 宿主按 `code` 分支处理、按 `params` 填 i18n 占位符。`detail` 只进日志，
//! 不面向用户、不翻译 —— 它可能带上游库的原始文案。
//!
//! **不要改动已有取值。** 宿主会按字符串比对。

use serde_json::{json, Map, Value};
use std::fmt;

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ErrorCode {
    // ── 输入校验 ────────────────────────────────────────────────────────
    /// 网络名不是 mainnet/stagenet/testnet。参数：`{ value }`
    InvalidNetwork,
    /// 地址无法解析，或不属于该网络。参数：`{ field }`
    InvalidAddress,
    /// 私钥/种子字节数不对。参数：`{ byteLen, expected }`
    InvalidKeyMaterial,
    /// 子地址序号非法（主序号与次序号不可同时为 0，那是主地址）。
    InvalidSubaddressIndex,
    /// 高度区间不合法（起点大于终点，或终点超过链尖）。参数：`{ from, to }`
    InvalidBlockRange,
    /// 宿主传入的 JSON 结构不对。参数：`{ field, reason }`
    InvalidRequest,

    // ── 生命周期 ────────────────────────────────────────────────────────
    /// 还没 `openWallet`。
    WalletNotOpen,
    /// 正在进行一次异步操作，此刻不接受切换钱包的调用。参数：`{ operation }`
    WalletBusy,
    /// 存储层还没初始化（wasm 上是 VFS 未安装）。
    NotInitialized,
    /// 该账户在库里不存在。参数：`{ accountId }`
    AccountNotFound,

    // ── 扫描 ────────────────────────────────────────────────────────────
    /// 节点返回的块与本地记录不连续 —— 重组，或节点在骗人。
    /// 参数：`{ atHeight, suggestedRewindHeight }`
    ReorgDetected,
    /// **节点给的 RingCT 输出索引与本地推算不符。**
    ///
    /// 上游明确警告这个字段不可验证，要求客户端自己维护索引并比对；不一致
    /// 意味着节点在提供虚假视图，应当断开而不是继续扫。
    /// 参数：`{ height, claimed, expected }`
    RingCtIndexMismatch,
    /// 扫描器拒绝了这个块（上游 `ScanError`）。参数：`{ height }`
    ScanFailed,

    // ── 外部世界 ────────────────────────────────────────────────────────
    /// 节点地址没配。
    NodeNotConfigured,
    /// 节点不在钱包所属的网络上（按创世块判断），或认不出它在哪条链上。
    /// 参数：`{ nodeNetwork, walletNetwork }` 或 `{ nodeGenesis }`
    NodeNetworkMismatch,
    /// 网络请求失败或超时。参数：`{ operation }`
    NetworkError,
    /// 节点返回的数据解不开（epee/JSON 格式不对）。参数：`{ operation }`
    MalformedNodeResponse,
    /// 节点明确拒收了这笔交易（双花、手续费不足……）。输入的预留已释放。
    BroadcastRejected,

    // ── 存储 ────────────────────────────────────────────────────────────
    /// SQLite 操作失败。参数：`{ operation }`
    DatabaseError,
    /// 库名非法（空，或含路径分隔符）。参数：`{ value }`
    InvalidDbName,

    /// 该能力尚未实现。参数：`{ what }`
    Unimplemented,
}

impl ErrorCode {
    /// 稳定字符串形式。**不要改动已有取值。**
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
            InvalidDbName => "INVALID_DB_NAME",
            Unimplemented => "UNIMPLEMENTED",
        }
    }
}

#[derive(Debug)]
pub struct RuntimeError {
    pub code: ErrorCode,
    /// 结构化参数，供宿主填 i18n 占位符。**永远是一个 JSON 对象。**
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

/// 给数据库操作贴上操作名，排查时能知道是哪一步挂的。
pub fn db_err(operation: &str, e: impl fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(e)
}

pub fn net_err(operation: &str, e: impl fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

#[cfg(target_arch = "wasm32")]
impl From<RuntimeError> for wasm_bindgen::JsValue {
    fn from(e: RuntimeError) -> Self {
        // 用 JSON 字符串而不是 Error 对象：宿主那头统一 `JSON.parse` 就能拿到
        // 结构化的 code/params，不必区分 Error 与非 Error 两条路径。
        wasm_bindgen::JsValue::from_str(&e.to_json().to_string())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashSet;

    /// 码值必须唯一且是 SCREAMING_SNAKE —— 宿主按字符串比对，复制粘贴出一个
    /// 重复值会让两类错误在宿主眼里变成同一个，而那种 bug 很难从现象反推。
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
            assert!(seen.insert(s), "码值重复: {s}");
            assert!(
                !s.is_empty()
                    && s.chars()
                        .all(|c| c.is_ascii_uppercase() || c.is_ascii_digit() || c == '_'),
                "码值格式不对: {s}"
            );
        }
    }

    /// `params` 永远是对象 —— 宿主直接取字段，不必先判类型。
    #[test]
    fn params_default_to_empty_object() {
        let e = RuntimeError::new(ErrorCode::WalletNotOpen);
        assert_eq!(e.to_json()["params"], json!({}));
        assert!(e.to_json()["detail"].is_null());
    }

    #[test]
    fn json_shape_is_stable() {
        let e = RuntimeError::with(ErrorCode::InvalidBlockRange, json!({ "from": 5, "to": 1 }))
            .detail("起点大于终点");
        let v = e.to_json();
        assert_eq!(v["code"], "INVALID_BLOCK_RANGE");
        assert_eq!(v["params"]["from"], 5);
        assert_eq!(v["detail"], "起点大于终点");
    }
}
