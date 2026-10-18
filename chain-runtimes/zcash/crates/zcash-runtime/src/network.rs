//! lightwalletd network layer.
//!
//! Browsers have no raw HTTP/2, so wasm uses gRPC-web: `tonic-web-wasm-client`
//! runs the tonic-generated client over fetch.
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

// wasm uses gRPC-web; native uses tonic's own transport. `LightClient` has the
// same signature on both, so `chain_tip` / `tree_state` etc. need no cfg split.
#[cfg(target_arch = "wasm32")]
pub type Transport = tonic_web_wasm_client::Client;

#[cfg(not(target_arch = "wasm32"))]
pub type Transport = tonic::transport::Channel;

pub type LightClient = CompactTxStreamerClient<Transport>;

/// `operation` is a stable machine-readable id (not text) naming the failed step.
fn net_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

/// Wrap a network future in a timeout.
///
/// In wasm the host cannot cancel a hung call (no thread to interrupt, the
/// Promise never rejects), so a gRPC call that never returns would wedge the
/// wallet silently. `get_block_range` can hang this way.
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

/// Native targets use the tokio timer.
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
///
/// No network round trip; the connection happens on the first call.
#[cfg(target_arch = "wasm32")]
pub fn connect(base_url: &str) -> Result<LightClient> {
    if base_url.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::LightwalletdNotConfigured,
            json!({ "reason": "emptyUrl" }),
        ));
    }
    // Plaintext would expose which addresses this wallet queries to anyone on the
    // path. Require https so a custom node or mistyped fallback URL cannot
    // silently downgrade.
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

/// Current chain tip height.
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
/// `SendTransaction` does not signal node errors via gRPC status: the call
/// succeeds but `SendResponse.error_code != 0` means the node did not accept the
/// transaction.
///
/// Definite rejections return `BROADCAST_REJECTED` (not retryable), other node
/// errors `BROADCAST_OUTCOME_UNKNOWN`, transport failures `NETWORK_ERROR`.
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
        // `SendTransaction` is used as an idempotent retry primitive after an
        // unknown network outcome. zcashd reports a duplicate through the
        // same business-error channel as a real rejection, even though the
        // desired postcondition (the node already has the tx) is satisfied.
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

/// A rejection that a node also gives for a transaction it already mined or
/// holds: its inputs are spent, possibly by this very transaction. Marking it
/// rejected would show a failed send for a payment that went through.
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
/// A rejection makes the host show a failure and the user resend with other
/// notes (the stored original holds its notes until expiry). If the original
/// actually propagated, both would be mined. So this is an allowlist:
/// consensus-invalid (proof, signature, value, branch) or expired. Transient or
/// policy replies (busy, mempool full, lagging, non-standard) are an unknown
/// outcome, rebroadcast during sync until mined or expired.
fn broadcast_definitely_invalid(message: &str) -> bool {
    if broadcast_outcome_ambiguous(message) {
        return false;
    }
    let message = message.to_lowercase();
    // zcashd: consensus reject reasons from main.cpp. `tx-expiring-soon` is policy
    // (other nodes may still mine it) and is excluded.
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

/// Diagnostic: fetch one block and return a structural summary. No scanning, no writes.
///
/// wasm (gRPC-web proxy) and native (gRPC) may hit different servers returning
/// different compact blocks; compare summaries before comparing behavior. Per-pool
/// counts show whether a difference comes from the server rather than computation.
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
        // protobuf encoded length + checksum: any differing bit shows up
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

/// FNV-1a, only for comparing bytes across endpoints; not for security.
fn fnv1a(bytes: &[u8]) -> String {
    let mut h: u64 = 0xcbf2_9ce4_8422_2325;
    for b in bytes {
        h ^= u64::from(*b);
        h = h.wrapping_mul(0x1000_0000_01b3);
    }
    format!("{h:016x}")
}

/// Fetch the treestate at `height`.
///
/// Needed for account import: `AccountBirthday` requires the commitment tree state
/// preceding the birthday height to compute witnesses for notes received later.
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

    // A node gives these for our own tx once it is mined, as well as for a real
    // conflict; they must stay "unknown" so scanning decides (mined or expired).
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

    // Rejection is an allowlist: any uncertain reply is an unknown outcome and
    // the tx stays pending for rebroadcast.
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
            // Zebra's wording (zebrad/src/components/mempool/error.rs).
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
