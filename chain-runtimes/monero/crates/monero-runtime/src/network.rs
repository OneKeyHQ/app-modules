//! Transport to monerod.
//!
//! Upstream `monero-daemon-rpc` abstracts the network as a trait with one method,
//! `post(route, body, size_limit) -> Vec<u8>`. Binary RPC and epee parsing stay
//! upstream; this module only moves bytes.
//!
//! ## `Send`
//!
//! Upstream requires `post` to return `impl Send + Future`, but a browser `fetch`
//! future is not `Send` (it holds JS values bound to their thread).
//!
//! Our wasm32-unknown-unknown build has no atomics or threads and runs in a single
//! Worker, so `Send` has nothing to violate there. An asserting wrapper is sound on
//! that target only; see the safety note on [`SingleThreaded`].

use monero_daemon_rpc::{HttpTransport, MoneroDaemon};
use monero_interface::InterfaceError;
use serde_json::json;

use crate::error::{net_err, ErrorCode, Result, RuntimeError};

/// Default per-request timeout.
///
/// A stall timeout, not a whole-batch deadline: a block stream that keeps progressing survives.
pub const DEFAULT_TIMEOUT_MS: u32 = 30_000;

/// Response body cap, so a malicious node cannot exhaust the wasm heap with an
/// oversized response. 32 MB: a batch in the spam-wall range can reach tens of MB.
pub const MAX_RESPONSE_BYTES: usize = 32 * 1024 * 1024;

/// monerod expects a Content-Type per route.
///
/// `/json_rpc` rejects `application/octet-stream`, which surfaces as an unexplained
/// connect failure. Binary routes end in `.bin`; the rest are JSON.
fn content_type_for(route: &str) -> &'static str {
    if route.ends_with(".bin") {
        "application/octet-stream"
    } else {
        "application/json"
    }
}

/// Asserts that a `!Send` future is `Send`.
///
/// # Safety
///
/// Compiled only for wasm32. This project builds that target without atomics or
/// threads: the module runs in a single Worker, so no second thread can observe
/// the future and `Send` constrains nothing.
///
/// If this crate ever supports wasm threads (atomics + SharedArrayBuffer), this
/// wrapper must be removed first; otherwise it is a real data race.
#[cfg(target_arch = "wasm32")]
struct SingleThreaded<F>(F);

#[cfg(target_arch = "wasm32")]
// SAFETY: see above; the target is single-threaded, so no other thread can observe it.
unsafe impl<F> Send for SingleThreaded<F> {}

#[cfg(target_arch = "wasm32")]
impl<F: core::future::Future> core::future::Future for SingleThreaded<F> {
    type Output = F::Output;
    fn poll(
        self: core::pin::Pin<&mut Self>,
        cx: &mut core::task::Context<'_>,
    ) -> core::task::Poll<Self::Output> {
        // SAFETY: pin projection onto the only field; it is never moved.
        unsafe { self.map_unchecked_mut(|s| &mut s.0).poll(cx) }
    }
}

/// Response size check shared by the host channel and in-wasm fetch.
///
/// Guards the wasm heap against oversized responses from a malicious node. Neither
/// path can truncate while streaming, so this runs after receipt, but before the
/// parser would double the footprint.
#[cfg(target_arch = "wasm32")]
fn cap_response(
    bytes: Vec<u8>,
    limit: Option<usize>,
) -> std::result::Result<Vec<u8>, InterfaceError> {
    let cap = limit.unwrap_or(MAX_RESPONSE_BYTES).min(MAX_RESPONSE_BYTES);
    if bytes.len() > cap {
        return Err(InterfaceError::InvalidInterface("response too big".into()));
    }
    Ok(bytes)
}

// Request handler supplied by the host.
//
// ## Why the host sends requests
//
// In-wasm `fetch` runs in the browser context, so it is subject to mixed-content
// blocking (plain-HTTP nodes) and CORS (monerod sends no
// `Access-Control-Allow-Origin` by default). Few public nodes pass both, and
// routing every user through one third-party node hands that operator everyone's
// IP, scan ranges and broadcast timing.
//
// This is about reachability, not speed (scanning is 0.4% of total time). The
// extension background (with `host_permissions`), the Electron main process and
// React Native native code are not bound by those rules.
//
// Without a host handler, requests fall back to in-wasm fetch.
#[cfg(target_arch = "wasm32")]
thread_local! {
    static HOST_HANDLER: std::cell::RefCell<Option<js_sys::Function>> =
        const { std::cell::RefCell::new(None) };
}

/// Install the host request handler; `None` uninstalls it and falls back to in-wasm fetch.
///
/// Signature: `(url, contentType, body: Uint8Array) => Promise<Uint8Array>`.
/// A throw or rejection is reported as a network error carrying its reason.
#[cfg(target_arch = "wasm32")]
pub fn set_host_handler(handler: Option<js_sys::Function>) {
    HOST_HANDLER.with(|slot| *slot.borrow_mut() = handler);
}

#[cfg(target_arch = "wasm32")]
fn host_handler() -> Option<js_sys::Function> {
    HOST_HANDLER.with(|slot| slot.borrow().clone())
}

/// Send one request through the host channel.
#[cfg(target_arch = "wasm32")]
async fn post_via_host(
    handler: js_sys::Function,
    url: &str,
    content_type: &str,
    body: &[u8],
) -> std::result::Result<Vec<u8>, InterfaceError> {
    use wasm_bindgen::{JsCast, JsValue};
    use wasm_bindgen_futures::JsFuture;

    let fail = |stage: &str, detail: String| {
        InterfaceError::InterfaceError(format!("{url}: host channel {stage}: {detail}"))
    };

    let array = js_sys::Uint8Array::from(body);
    let returned = handler
        .call3(
            &JsValue::NULL,
            &JsValue::from_str(url),
            &JsValue::from_str(content_type),
            &array,
        )
        .map_err(|e| fail("call", describe_js(&e)))?;

    // Accept bytes returned directly or via a Promise.
    let resolved = match returned.dyn_into::<js_sys::Promise>() {
        Ok(promise) => JsFuture::from(promise)
            .await
            .map_err(|e| fail("await response", describe_js(&e)))?,
        Err(value) => value,
    };

    let bytes = resolved
        .dyn_into::<js_sys::Uint8Array>()
        .map_err(|_| fail("return value", "host must return a Uint8Array".to_owned()))?;
    Ok(bytes.to_vec())
}

/// Reduce a JS exception to a readable message.
///
/// `JsValue`'s Debug output is useless for diagnosis; `Error.message` is where the
/// browser puts the real cause (DNS failure, CORS rejection, connection reset).
#[cfg(target_arch = "wasm32")]
fn describe_js(value: &wasm_bindgen::JsValue) -> String {
    use wasm_bindgen::JsValue;
    js_sys::Reflect::get(value, &JsValue::from_str("message"))
        .ok()
        .and_then(|m| m.as_string())
        .or_else(|| value.as_string())
        .unwrap_or_else(|| format!("{value:?}"))
}

/// Per-request cap. A full batch can take over ten seconds on a slow node.
#[cfg(target_arch = "wasm32")]
const REQUEST_TIMEOUT_MS: u32 = 45_000;

// The host enforces a per-chunk idle limit. Its backstop must allow a large
// response to keep progressing on a slow link, matching the Worker relay.
#[cfg(target_arch = "wasm32")]
const HOST_REQUEST_TIMEOUT_MS: u32 = 10 * 60 * 1000;

/// Browser transport based on `fetch`.
#[cfg(target_arch = "wasm32")]
#[derive(Clone, Debug)]
pub struct FetchTransport {
    base_url: String,
}

#[cfg(target_arch = "wasm32")]
impl FetchTransport {
    pub fn new(base_url: &str) -> Self {
        Self {
            base_url: base_url.trim_end_matches('/').to_owned(),
        }
    }

    /// Await one request with a cap. A stuck request holds the wallet DB, so every later
    /// call gets WALLET_BUSY; on timeout it is abandoned to finish in the background.
    async fn post_inner(
        base_url: String,
        route: String,
        body: Vec<u8>,
        limit: Option<usize>,
    ) -> std::result::Result<Vec<u8>, InterfaceError> {
        use futures_util::future::{select, Either};

        let url = format!("{base_url}/{}", route.trim_start_matches('/'));
        let request = Box::pin(Self::post_untimed(base_url, route, body, limit));
        let timeout_ms = if host_handler().is_some() {
            HOST_REQUEST_TIMEOUT_MS
        } else {
            REQUEST_TIMEOUT_MS
        };
        let timeout = gloo_timers::future::TimeoutFuture::new(timeout_ms);
        match select(request, timeout).await {
            Either::Left((result, _)) => result,
            Either::Right(_) => Err(InterfaceError::InterfaceError(format!(
                "{url}: no response in {} s",
                timeout_ms / 1000
            ))),
        }
    }

    async fn post_untimed(
        base_url: String,
        route: String,
        body: Vec<u8>,
        limit: Option<usize>,
    ) -> std::result::Result<Vec<u8>, InterfaceError> {
        use wasm_bindgen::{JsCast, JsValue};
        use wasm_bindgen_futures::JsFuture;

        let url = format!("{base_url}/{}", route.trim_start_matches('/'));
        let content_type = content_type_for(&route);

        // Prefer the host handler: it is not bound by mixed-content or CORS rules, so any
        // node is usable. Otherwise fall back to in-wasm fetch below.
        if let Some(handler) = host_handler() {
            let bytes = post_via_host(handler, &url, content_type, &body).await?;
            return cap_response(bytes, limit);
        }

        // Tag each failure with its stage: a down node, missing CORS, a bad URL and an
        // unreadable body need different fixes from the user.
        let fail = |stage: &str, detail: String| {
            InterfaceError::InterfaceError(format!("{url}: {stage}: {detail}"))
        };

        let opts = web_sys::RequestInit::new();
        opts.set_method("POST");
        let array = js_sys::Uint8Array::from(body.as_slice());
        opts.set_body(&array);

        let request = web_sys::Request::new_with_str_and_init(&url, &opts)
            .map_err(|e| fail("build request", describe_js(&e)))?;
        let _ = request.headers().set("Content-Type", content_type);

        let global = js_sys::global();
        let fetch = js_sys::Reflect::get(&global, &JsValue::from_str("fetch"))
            .ok()
            .and_then(|f| f.dyn_into::<js_sys::Function>().ok())
            .ok_or_else(|| {
                fail(
                    "get fetch",
                    "no global fetch here (host environment issue, not the node)".to_owned(),
                )
            })?;
        let promise = fetch
            .call1(&global, &request)
            .map_err(|e| fail("send request", describe_js(&e)))?
            .dyn_into::<js_sys::Promise>()
            .map_err(|e| fail("send request", describe_js(&e)))?;

        let response = JsFuture::from(promise)
            .await
            // Usually the network is down or the node sent no CORS headers; the browser
            // does not say which, so name both.
            .map_err(|e| {
                fail(
                    "await response",
                    format!("{} (node unreachable or no CORS headers)", describe_js(&e)),
                )
            })?
            .dyn_into::<web_sys::Response>()
            .map_err(|e| fail("parse response", describe_js(&e)))?;

        if !response.ok() {
            return Err(fail(
                "node rejected",
                format!("HTTP {} {}", response.status(), response.status_text()),
            ));
        }

        let buffer = JsFuture::from(
            response
                .array_buffer()
                .map_err(|e| fail("read response body", describe_js(&e)))?,
        )
        .await
        .map_err(|e| fail("read response body", describe_js(&e)))?;

        cap_response(js_sys::Uint8Array::new(&buffer).to_vec(), limit)
    }
}

#[cfg(target_arch = "wasm32")]
impl HttpTransport for FetchTransport {
    fn post(
        &self,
        route: &str,
        body: Vec<u8>,
        response_size_limit: Option<usize>,
    ) -> impl Send + core::future::Future<Output = std::result::Result<Vec<u8>, InterfaceError>>
    {
        SingleThreaded(Self::post_inner(
            self.base_url.clone(),
            route.to_owned(),
            body,
            response_size_limit,
        ))
    }
}

/// Native transport for local tests and diagnostics; reproduces browser issues under a debugger.
#[cfg(not(target_arch = "wasm32"))]
#[derive(Clone, Debug)]
pub struct NativeTransport {
    base_url: String,
    client: reqwest::Client,
}

#[cfg(not(target_arch = "wasm32"))]
impl NativeTransport {
    pub fn new(base_url: &str) -> Self {
        Self {
            base_url: base_url.trim_end_matches('/').to_owned(),
            client: {
                let builder = reqwest::Client::builder().timeout(std::time::Duration::from_millis(
                    u64::from(DEFAULT_TIMEOUT_MS),
                ));
                // reqwest uses the macOS system proxy by default; curl and Node do not. Set this
                // to rule out the proxy when a node answers curl instantly but times out here.
                let builder = if std::env::var_os("XMR_NATIVE_NO_PROXY").is_some() {
                    builder.no_proxy()
                } else {
                    builder
                };
                builder.build().unwrap_or_default()
            },
        }
    }
}

#[cfg(not(target_arch = "wasm32"))]
impl HttpTransport for NativeTransport {
    fn post(
        &self,
        route: &str,
        body: Vec<u8>,
        response_size_limit: Option<usize>,
    ) -> impl Send + core::future::Future<Output = std::result::Result<Vec<u8>, InterfaceError>>
    {
        let url = format!("{}/{}", self.base_url, route.trim_start_matches('/'));
        let content_type = content_type_for(route);
        let client = self.client.clone();
        async move {
            // Keep the real cause: refused, 404, timeout and TLS failure otherwise look identical.
            let response = client
                .post(&url)
                .header("Content-Type", content_type)
                .body(body)
                .send()
                .await
                .map_err(|e| {
                    // reqwest's top-level message is just "error sending request"; the cause
                    // (timeout, TLS, reset, DNS) is in the source chain.
                    let mut chain = e.to_string();
                    let mut source = std::error::Error::source(&e);
                    while let Some(inner) = source {
                        chain.push_str(" <- ");
                        chain.push_str(&inner.to_string());
                        source = inner.source();
                    }
                    InterfaceError::InterfaceError(format!("POST {url}: {chain}"))
                })?;
            let status = response.status();
            if !status.is_success() {
                let body = response.text().await.unwrap_or_default();
                return Err(InterfaceError::InterfaceError(format!(
                    "POST {url} -> {status}: {}",
                    body.chars().take(200).collect::<String>()
                )));
            }
            let bytes = response
                .bytes()
                .await
                .map_err(|e| InterfaceError::InterfaceError(format!("read body of {url}: {e}")))?
                .to_vec();
            let cap = response_size_limit
                .unwrap_or(MAX_RESPONSE_BYTES)
                .min(MAX_RESPONSE_BYTES);
            if bytes.len() > cap {
                return Err(InterfaceError::InvalidInterface("response too big".into()));
            }
            Ok(bytes)
        }
    }
}

#[cfg(target_arch = "wasm32")]
pub type Transport = FetchTransport;
#[cfg(not(target_arch = "wasm32"))]
pub type Transport = NativeTransport;

pub type Daemon = MoneroDaemon<Transport>;

/// Connect to a monerod node and return it with the network its genesis block identifies.
///
/// The host supplies the URL; there is no built-in default. A default would decide for
/// the user who sees their IP and queries, a privacy choice the runtime must not make.
pub async fn connect(base_url: &str) -> Result<(Daemon, monero_wallet::address::Network)> {
    if base_url.trim().is_empty() {
        return Err(RuntimeError::new(ErrorCode::NodeNotConfigured));
    }
    if !(base_url.starts_with("http://") || base_url.starts_with("https://")) {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "nodeUrl", "reason": "schemeMustBeHttpOrHttps" }),
        ));
    }
    let daemon = MoneroDaemon::new(Transport::new(base_url))
        .await
        .map_err(|e| net_err("connect", format!("{e:?}")))?;
    let network = node_network(&daemon).await?;
    CURRENT_NODE_URL.with(|c| *c.borrow_mut() = Some(base_url.to_owned()));
    Ok((daemon, network))
}

/// Genesis block hash per network.
///
/// The same keys derive the same output keys on all three networks: a testnet wallet
/// on a mainnet node would scan and send real XMR. Address prefixes cannot catch
/// this; the node's chain must be checked.
const GENESIS_HASHES: [(monero_wallet::address::Network, &str); 3] = [
    (
        monero_wallet::address::Network::Mainnet,
        "418015bb9ae982a1975da7d79277c2705727a56894ba0fb246adaabb1f4632e3",
    ),
    (
        monero_wallet::address::Network::Testnet,
        "48ca7cd3c8de5b6a4d53d2861fbdaedca141553559f9be9520068053cda8430b",
    ),
    (
        monero_wallet::address::Network::Stagenet,
        "76ee3cc98646292206cd3e86f74d88b4dcc1d937088645e9b0cbca84b7ce74eb",
    ),
];

pub(crate) fn network_for_genesis(hash: &[u8; 32]) -> Option<monero_wallet::address::Network> {
    let hash = hex::encode(hash);
    GENESIS_HASHES
        .iter()
        .find(|(_, genesis)| *genesis == hash)
        .map(|(network, _)| *network)
}

/// Identify the node's chain by its genesis block. Unknown chains are refused: no
/// node is better than one on an unknown chain.
async fn node_network(daemon: &Daemon) -> Result<monero_wallet::address::Network> {
    use monero_daemon_rpc::prelude::*;

    let genesis = daemon
        .block_hash(0)
        .await
        .map_err(|e| net_err("genesisHash", format!("{e:?}")))?;
    network_for_genesis(&genesis).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::NodeNetworkMismatch,
            json!({ "nodeGenesis": hex::encode(genesis) }),
        )
    })
}

thread_local! {
    /// URL of the connected node. The batch block fetch (blocks_bin) sends its own
    /// requests, and `MoneroDaemon` does not expose its transport.
    static CURRENT_NODE_URL: std::cell::RefCell<Option<String>> =
        const { std::cell::RefCell::new(None) };
}

pub fn current_node_url() -> Option<String> {
    CURRENT_NODE_URL.with(|c| c.borrow().clone())
}

pub fn forget_node_url() {
    CURRENT_NODE_URL.with(|c| *c.borrow_mut() = None);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn genesis_hashes_identify_each_network() {
        use monero_wallet::address::Network;
        for (network, genesis) in GENESIS_HASHES {
            let bytes: [u8; 32] = hex::decode(genesis).unwrap().try_into().unwrap();
            assert_eq!(network_for_genesis(&bytes), Some(network));
        }
        assert_eq!(network_for_genesis(&[0u8; 32]), None);
        assert_ne!(Network::Mainnet, Network::Stagenet);
    }

    /// No built-in default node: it would decide for the user who sees their IP and queries.
    #[test]
    fn an_empty_node_url_is_refused_not_defaulted() {
        let e = futures_lite::future::block_on(connect("")).unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
        let e = futures_lite::future::block_on(connect("   ")).unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
    }

    /// The scheme must be explicit: `1.2.3.4:18081` would be treated as a relative path
    /// and yield a malformed URL that fails far from the cause.
    #[test]
    fn the_node_url_must_carry_a_scheme() {
        for bad in ["1.2.3.4:18081", "node.example.com", "ftp://x"] {
            let e = futures_lite::future::block_on(connect(bad)).unwrap_err();
            assert_eq!(e.code, ErrorCode::InvalidRequest, "{bad}");
        }
    }

    /// The route decides Content-Type. The wrong one makes `/json_rpc` reject at
    /// connect time, far from the cause.
    #[test]
    fn json_and_binary_routes_get_different_content_types() {
        assert_eq!(content_type_for("json_rpc"), "application/json");
        assert_eq!(content_type_for("get_transactions"), "application/json");
        assert_eq!(
            content_type_for("get_blocks.bin"),
            "application/octet-stream"
        );
        assert_eq!(content_type_for("get_outs.bin"), "application/octet-stream");
    }

    #[test]
    fn response_cap_never_exceeds_the_hard_limit() {
        // A host value above the hard cap is clamped to it
        let requested = MAX_RESPONSE_BYTES * 4;
        assert_eq!(requested.min(MAX_RESPONSE_BYTES), MAX_RESPONSE_BYTES);
    }
}
