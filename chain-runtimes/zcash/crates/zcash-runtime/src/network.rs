//! lightwalletd network layer: gRPC-web on wasm, tonic transport on native.
//!
//! Requires `tonic-web-wasm-client` >= 0.9.1; 0.8.0 drops gRPC trailers at stream
//! end, so streaming calls like `get_block_range` never complete.

use zcash_client_backend::proto::service::{
    compact_tx_streamer_client::CompactTxStreamerClient, BlockId, ChainSpec, TreeState,
};

use crate::error::{ErrorCode, Result, RuntimeError};
use serde_json::json;

#[cfg(target_arch = "wasm32")]
thread_local! {
    static REQUEST_HANDLER: std::cell::RefCell<Option<js_sys::Function>> = const { std::cell::RefCell::new(None) };
}

/// The handler belongs to this WASM instance, and is installed before networking.
#[cfg(target_arch = "wasm32")]
pub fn set_request_handler(handler: Option<js_sys::Function>) {
    REQUEST_HANDLER.with(|slot| *slot.borrow_mut() = handler);
}

// `LightClient` has the same signature on both transports, so callers need no cfg split.
#[cfg(target_arch = "wasm32")]
pub type Transport = tonic_web_wasm_client::Client;

#[cfg(not(target_arch = "wasm32"))]
pub type Transport = tonic::transport::Channel;

pub type LightClient = CompactTxStreamerClient<Transport>;

/// `operation` is a stable machine-readable id (not text) naming the failed step.
fn net_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

/// Wrap a network future in a timeout; in wasm the host cannot cancel a hung call
/// (the Promise never rejects), which would wedge the wallet silently.
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

/// Default network timeout; a single gRPC call exceeding it is treated as hung.
pub const DEFAULT_TIMEOUT_MS: u32 = 30_000;

/// Build a lightwalletd client. `base_url` looks like `https://host:port`.
/// No network round trip; the connection happens on the first call.
#[cfg(target_arch = "wasm32")]
pub fn connect(base_url: &str) -> Result<LightClient> {
    if base_url.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "emptyUrl" }),
        ));
    }
    // Require https: plaintext would expose which addresses this wallet queries.
    if !base_url.starts_with("https://") {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "httpsRequired" }),
        ));
    }
    let mut options = tonic_web_wasm_client::options::FetchOptions::new();
    options.fetch = REQUEST_HANDLER.with(|slot| slot.borrow().clone());
    Ok(CompactTxStreamerClient::new(
        tonic_web_wasm_client::Client::new_with_options(
            base_url.trim_end_matches('/').to_owned(),
            options,
        ),
    ))
}

/// Native connect. Unlike wasm this actually opens the connection, hence async.
#[cfg(not(target_arch = "wasm32"))]
pub async fn connect_native(base_url: &str) -> Result<LightClient> {
    if base_url.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "emptyUrl" }),
        ));
    }
    // https endpoints need explicit TLS; tonic 0.14 does not enable root certs by default.
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

/// Submit a signed transaction to lightwalletd.
///
/// Node rejections arrive as `SendResponse.error_code != 0`, not a gRPC status.
/// Definite rejections return `BROADCAST_REJECTED`, other node errors
/// `BROADCAST_OUTCOME_UNKNOWN`, transport failures `NETWORK_ERROR`.
pub async fn broadcast(client: &mut LightClient, tx_bytes: Vec<u8>) -> Result<()> {
    use zcash_client_backend::proto::service::RawTransaction;

    let resp = with_timeout(
        async {
            client
                .send_transaction(RawTransaction {
                    data: tx_bytes,
                    // Height is unknown at broadcast; lightwalletd ignores it.
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
        // A node that already holds the tx is the outcome we want.
        if broadcast_already_known(&resp.error_message) {
            return Ok(());
        }
        let code = if broadcast_definitely_invalid(&resp.error_message) {
            ErrorCode::BroadcastRejected
        } else {
            ErrorCode::BroadcastOutcomeUnknown
        };
        return Err(RuntimeError::with(
            code,
            json!({
                "errorCode": resp.error_code,
                "errorMessage": resp.error_message,
            }),
        )
        .detail(resp.error_message));
    }
    Ok(())
}

/// Replies a node also gives for a tx it already mined or holds (inputs spent,
/// possibly by this very tx); treating them as rejected would show a failed send
/// for a payment that went through.
fn broadcast_outcome_ambiguous(message: &str) -> bool {
    let message = message.to_lowercase();
    [
        // Zebra: zebra-consensus / zebrad mempool errors.
        "double-spend",
        "duplicate nullifier",
        "could not find transparent input utxo",
        "same effects",
        // zcashd.
        "bad-txns-inputs-spent",
        "bad-txns-inputs-missingorspent",
        "txn-mempool-conflict",
        "missing inputs",
        // REJECT_DUPLICATE for a fully shielded tx whose nullifiers are already
        // spent, including by this tx once mined.
        "bad-txns-shielded-requirements-not-met",
    ]
    .iter()
    .any(|needle| message.contains(needle))
}

/// Only replies proving these bytes can never be mined count as a rejection.
///
/// A rejection lets the user resend with other notes, and both could be mined if
/// the original propagated. So this is an allowlist (consensus-invalid or expired);
/// transient or policy replies stay an unknown outcome.
fn broadcast_definitely_invalid(message: &str) -> bool {
    if broadcast_outcome_ambiguous(message) {
        return false;
    }
    let message = message.to_lowercase();
    // zcashd consensus reject reasons; `tx-expiring-soon` is policy and excluded.
    ["bad-txns-", "bad-tx-", "tx-overwinter-expired"]
        .iter()
        .any(|needle| message.contains(needle))
}

fn broadcast_already_known(message: &str) -> bool {
    let message = message.to_lowercase();
    [
        "already in mempool",
        "already in block chain",
        "already in blockchain",
        "already have transaction",
        "txn-already-known",
        // Zebra (behind Zaino): zebrad mempool::error.
        "already exists in mempool",
        "already queued for download",
    ]
    .iter()
    .any(|needle| message.contains(needle))
}

/// Fetch the treestate at `height`; `AccountBirthday` needs the tree state preceding the birthday.
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
    use super::{
        broadcast_already_known, broadcast_definitely_invalid, broadcast_outcome_ambiguous,
        with_timeout,
    };
    use crate::error::{ErrorCode, RuntimeError};

    // A node also gives these for our own mined tx; they must stay unknown so scanning decides.
    #[test]
    fn spent_input_rejections_are_an_unknown_outcome_not_a_rejection() {
        for message in [
            "orchard double-spend: duplicate nullifier: Nullifier(..)",
            "ironwood double-spend: duplicate nullifier: Nullifier(..)",
            "transparent double-spend: OutPoint(..) is spent twice",
            "could not find transparent input UTXO in the best chain or mempool",
            "any transaction with the same effects will be rejected from the mempool until the next chain tip block: ..",
            "bad-txns-inputs-spent",
            "txn-mempool-conflict",
            "18: bad-txns-shielded-requirements-not-met",
        ] {
            assert!(broadcast_outcome_ambiguous(message), "{message}");
            assert!(!broadcast_already_known(message), "{message}");
        }
        for message in [
            "transaction is non-standard",
            "tx-overwinter-expired",
            "bad-txns-fee-too-low",
        ] {
            assert!(!broadcast_outcome_ambiguous(message), "{message}");
        }
    }

    #[test]
    fn only_consensus_invalid_or_expired_replies_count_as_a_rejection() {
        for message in [
            "tx-overwinter-expired",
            "bad-txns-fee-too-low",
            "bad-txns-orchard-verification-failed",
            "16: bad-txns-vout-negative",
        ] {
            assert!(broadcast_definitely_invalid(message), "{message}");
        }
        for message in [
            "bad-txns-inputs-spent",
            "bad-txns-inputs-missingorspent",
            "bad-txns-shielded-requirements-not-met",
            "tx-expiring-soon",
            "transaction is non-standard",
            "transaction dropped because the queue is full",
            "mempool is disabled since synchronization is behind the chain tip",
            "",
            "something nobody has seen before",
        ] {
            assert!(!broadcast_definitely_invalid(message), "{message}");
        }
    }

    #[test]
    fn duplicate_broadcast_is_an_idempotent_success() {
        for message in [
            "txn-already-known",
            "Transaction already in mempool",
            "transaction already in block chain",
            "transaction already exists in mempool",
            "transaction dropped because it is already queued for download",
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
