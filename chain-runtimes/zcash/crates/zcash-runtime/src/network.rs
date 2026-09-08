//! lightwalletd 网络层。
//!
//! 浏览器里跑不了原生 gRPC（没有 HTTP/2 裸连接），所以走 gRPC-web：
//! `tonic-web-wasm-client` 把 tonic 生成的客户端接到 fetch 上。
//!
//! 版本约束：`tonic-web-wasm-client` 必须 >= 0.9.1。0.8.0 在流结束时不转发
//! gRPC trailer，会让 `get_block_range` 这类流式调用永远等不到结束标志。

use zcash_client_backend::proto::service::{
    compact_tx_streamer_client::CompactTxStreamerClient, BlockId, ChainSpec, TreeState,
};

use crate::error::{ErrorCode, Result, RuntimeError};
use serde_json::json;

// 浏览器走 gRPC-web（没有原生 HTTP/2 裸连接），原生目标走 tonic 自带 transport。
// 上层的 `LightClient` 签名在两边一致，`chain_tip` / `tree_state` 等函数不用分叉。
#[cfg(target_arch = "wasm32")]
pub type Transport = tonic_web_wasm_client::Client;

#[cfg(not(target_arch = "wasm32"))]
pub type Transport = tonic::transport::Channel;

pub type LightClient = CompactTxStreamerClient<Transport>;

/// `operation` 是稳定的机器可读标识（不是文案），宿主可据此区分是哪一步失败。
fn net_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

/// 把一个网络 future 套上超时。
///
/// **为什么必须有**：wasm 里挂死的调用宿主无法取消 —— 没有线程可以中断，
/// Promise 也不会自己 reject。一个不返回的 gRPC 调用会让整个钱包永久卡住，
/// 而且没有任何错误信息。加超时把「挂死」变成「可处理的错误」。
///
/// 实测踩过：`get_block_range` 在某些情况下不返回也不报错。
#[cfg(target_arch = "wasm32")]
pub async fn with_timeout<T>(
    fut: impl std::future::Future<Output = Result<T>>,
    timeout_ms: u32,
    operation: &str,
) -> Result<T> {
    use futures_util::future::{select, Either};

    let timer = gloo_timers::future::TimeoutFuture::new(timeout_ms);
    futures_util::pin_mut!(fut);

    match select(fut, timer).await {
        Either::Left((r, _)) => r,
        Either::Right((_, _)) => Err(timeout_err(operation, timeout_ms)),
    }
}

/// 原生目标用 tokio 的定时器。
#[cfg(not(target_arch = "wasm32"))]
pub async fn with_timeout<T>(
    fut: impl std::future::Future<Output = Result<T>>,
    timeout_ms: u32,
    operation: &str,
) -> Result<T> {
    match tokio::time::timeout(std::time::Duration::from_millis(timeout_ms.into()), fut).await {
        Ok(r) => r,
        Err(_) => Err(timeout_err(operation, timeout_ms)),
    }
}

fn timeout_err(operation: &str, timeout_ms: u32) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::NetworkError,
        json!({ "operation": operation, "timeoutMs": timeout_ms }),
    )
}

/// 默认网络超时。单次 gRPC 调用超过这个时长基本可以判定为挂死。
pub const DEFAULT_TIMEOUT_MS: u32 = 30_000;

/// 建立一个 lightwalletd 客户端。`base_url` 形如 `https://host:port`。
///
/// 不做网络往返 —— 真正的连接发生在第一次调用时。
#[cfg(target_arch = "wasm32")]
pub fn connect(base_url: &str) -> Result<LightClient> {
    if base_url.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "emptyUrl" }),
        ));
    }
    Ok(CompactTxStreamerClient::new(
        tonic_web_wasm_client::Client::new(base_url.to_owned()),
    ))
}

/// 原生目标的连接。与 wasm 版不同，这里会真的建链接，所以是 async。
#[cfg(not(target_arch = "wasm32"))]
pub async fn connect_native(base_url: &str) -> Result<LightClient> {
    if base_url.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "emptyUrl" }),
        ));
    }
    // https 端点必须显式配 TLS —— tonic 0.14 不会自动启用根证书。
    let mut endpoint = tonic::transport::Channel::from_shared(base_url.to_owned())
        .map_err(|e| net_err("parseEndpoint", e))?;
    if base_url.starts_with("https://") {
        endpoint = endpoint
            .tls_config(tonic::transport::ClientTlsConfig::new().with_webpki_roots())
            .map_err(|e| net_err("tlsConfig", e))?;
    }
    let channel = endpoint
        .connect()
        .await
        .map_err(|e| net_err("connect", e))?;
    Ok(CompactTxStreamerClient::new(channel))
}

/// 当前链尖高度。
pub async fn chain_tip(client: &mut LightClient) -> Result<u32> {
    let block = with_timeout(
        async {
            client
                .get_latest_block(ChainSpec {})
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err("getLatestBlock", e))
        },
        DEFAULT_TIMEOUT_MS,
        "getLatestBlock",
    )
    .await?;
    u32::try_from(block.height).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidBlockHeight,
            json!({ "value": block.height }),
        )
    })
}

/// 把已签名的交易提交给 lightwalletd。
///
/// lightwalletd 的 `SendTransaction` **不用 gRPC 错误表示业务失败** —— 调用成功返回，
/// 但 `SendResponse.error_code != 0` 表示节点拒绝了这笔交易。只看 `Result::Ok`
/// 会把「被拒绝」当成「已广播」，这是这条链路上最容易漏的一步。
///
/// 拒绝抛 `BROADCAST_REJECTED`（不可重试），网络本身出错才抛 `NETWORK_ERROR`。
pub async fn broadcast(client: &mut LightClient, tx_bytes: Vec<u8>) -> Result<()> {
    use zcash_client_backend::proto::service::RawTransaction;

    let resp = with_timeout(
        async {
            client
                .send_transaction(RawTransaction {
                    data: tx_bytes,
                    // 广播时高度未知；lightwalletd 忽略该字段。
                    height: 0,
                })
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err("sendTransaction", e))
        },
        DEFAULT_TIMEOUT_MS,
        "sendTransaction",
    )
    .await?;

    if resp.error_code != 0 {
        // `SendTransaction` is used as an idempotent retry primitive after an
        // unknown network outcome. zcashd reports a duplicate through the
        // same business-error channel as a real rejection, even though the
        // desired postcondition (the node already has the tx) is satisfied.
        if broadcast_already_known(&resp.error_message) {
            return Ok(());
        }
        return Err(RuntimeError::with(
            ErrorCode::BroadcastRejected,
            json!({
                "errorCode": resp.error_code,
                "errorMessage": resp.error_message,
            }),
        )
        .detail(resp.error_message));
    }
    Ok(())
}

fn broadcast_already_known(message: &str) -> bool {
    let message = message.to_lowercase();
    [
        "already in mempool",
        "already in block chain",
        "already in blockchain",
        "already have transaction",
        "txn-already-known",
    ]
    .iter()
    .any(|needle| message.contains(needle))
}

/// 诊断：取单个区块并给出结构摘要，不做任何扫描、不写库。
///
/// 存在的理由：浏览器走 gRPC-web 代理、原生走真 gRPC，**两边可能根本不是同一个
/// 服务端**，返回的 compact block 未必一致。排查「同一高度在 wasm 上出问题、
/// 在原生上正常」时，必须先确认两边拿到的数据是不是同一份，否则一切对比都不成立。
///
/// 摘要里带上各池的计数：某个池只在一边出现，就说明是服务端差异而非计算差异。
pub async fn block_summary(client: &mut LightClient, height: u32) -> Result<String> {
    use futures_util::TryStreamExt;
    use zcash_client_backend::proto::service::BlockRange;

    let id = |h: u32| BlockId {
        height: u64::from(h),
        hash: Vec::new(),
    };
    let blocks = with_timeout(
        async {
            client
                .get_block_range(BlockRange {
                    start: Some(id(height)),
                    end: Some(id(height)),
                    pool_types: vec![],
                })
                .await
                .map_err(|e| net_err("getBlockRange", e))?
                .into_inner()
                .try_collect::<Vec<_>>()
                .await
                .map_err(|e| net_err("getBlockRange", e))
        },
        DEFAULT_TIMEOUT_MS,
        "getBlockRange",
    )
    .await?;

    let Some(block) = blocks.into_iter().next() else {
        return Ok(json!({ "height": height, "found": false }).to_string());
    };

    let (mut spends, mut outputs, mut actions, mut ironwood) = (0usize, 0usize, 0usize, 0usize);
    for tx in &block.vtx {
        spends += tx.spends.len();
        outputs += tx.outputs.len();
        actions += tx.actions.len();
        ironwood += tx.ironwood_actions.len();
    }

    Ok(json!({
        "height": height,
        "found": true,
        // protobuf 编码长度 + 校验和：两边只要有一个 bit 不同就能看出来
        "encodedLen": prost::Message::encoded_len(&block),
        "checksum": fnv1a(&prost::Message::encode_to_vec(&block)),
        "vtx": block.vtx.len(),
        "saplingSpends": spends,
        "saplingOutputs": outputs,
        "orchardActions": actions,
        "ironwoodActions": ironwood,
    })
    .to_string())
}

/// FNV-1a：只用来比对两端拿到的字节是否一致，不做任何安全用途。
fn fnv1a(bytes: &[u8]) -> String {
    let mut h: u64 = 0xcbf2_9ce4_8422_2325;
    for b in bytes {
        h ^= u64::from(*b);
        h = h.wrapping_mul(0x1000_0000_01b3);
    }
    format!("{h:016x}")
}

/// 取指定高度的 treestate。
///
/// 导入账户时需要它：`AccountBirthday` 要知道 birthday 高度之前的承诺树状态，
/// 否则新账户无法为之后收到的 note 计算 witness。
pub async fn tree_state(client: &mut LightClient, height: u32) -> Result<TreeState> {
    let ts = with_timeout(
        async {
            client
                .get_tree_state(BlockId {
                    height: u64::from(height),
                    hash: Vec::new(),
                })
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err("getTreeState", format!("height={height}: {e}")))
        },
        DEFAULT_TIMEOUT_MS,
        "getTreeState",
    )
    .await?;
    Ok(ts)
}

#[cfg(test)]
mod tests {
    use super::{broadcast_already_known, with_timeout};
    use crate::error::{ErrorCode, RuntimeError};

    #[test]
    fn duplicate_broadcast_is_an_idempotent_success() {
        for message in [
            "txn-already-known",
            "Transaction already in mempool",
            "transaction already in block chain",
        ] {
            assert!(broadcast_already_known(message), "{message}");
        }
        assert!(!broadcast_already_known("bad-txns-inputs-missingorspent"));
    }

    #[cfg(not(target_arch = "wasm32"))]
    #[tokio::test]
    async fn stalled_stream_handshake_times_out() {
        let result = with_timeout(
            async {
                futures_util::future::pending::<()>().await;
                Ok::<(), RuntimeError>(())
            },
            10,
            "getBlockRange",
        )
        .await;

        let error = result.expect_err("a stalled stream handshake must time out");
        assert_eq!(error.code, ErrorCode::NetworkError);
        assert_eq!(error.params["operation"], "getBlockRange");
        assert_eq!(error.params["timeoutMs"], 10);
    }

    #[cfg(not(target_arch = "wasm32"))]
    #[tokio::test]
    async fn stream_item_timeout_is_reapplied_for_each_item() {
        let mut received = Vec::new();
        for item in 0..4 {
            let next = with_timeout(
                async {
                    tokio::time::sleep(std::time::Duration::from_millis(20)).await;
                    Ok::<_, RuntimeError>(item)
                },
                50,
                "getBlockRange",
            )
            .await
            .expect("each stream item arrives before its own timeout");
            received.push(next);
        }

        assert_eq!(received, vec![0, 1, 2, 3]);
    }
}
