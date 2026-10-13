//! 连 monerod 的传输层。
//!
//! 上游 `monero-daemon-rpc` 把网络抽象成一个 trait，只有一个方法：
//! `post(route, body, size_limit) -> Vec<u8>`。二进制 RPC 与 epee 解析全在
//! 上游 —— 我们只要把字节搬进搬出。
//!
//! ## `Send` 的问题
//!
//! 上游签名要求 `post` 返回 `impl Send + Future`，而浏览器 `fetch` 产出的
//! future **不是** `Send`（它持有 JS 值，JS 值绑在它所在的线程上）。
//!
//! wasm32-unknown-unknown 在我们的构建下没有 atomics、没有线程，整个模块跑在
//! 一个 Worker 里，`Send` 这个约束在那里**没有可被违反的语义**。所以用一层
//! 断言包装是成立的 —— 但仅限这个目标，见 [`SingleThreaded`] 上的安全性说明。

use monero_daemon_rpc::{HttpTransport, MoneroDaemon};
use monero_interface::InterfaceError;
use serde_json::json;

use crate::error::{net_err, ErrorCode, Result, RuntimeError};

/// 单次请求的默认超时。
///
/// 这是**停滞**超时，不是整批的截止时间：块流只要还在推进就不判死。
pub const DEFAULT_TIMEOUT_MS: u32 = 30_000;

/// 响应体上限，防一个恶意节点用超大响应把 wasm 堆撑爆。
/// 取 32 MB —— spam 墙区段一批块可达几十 MB，留够余量但不无界。
pub const MAX_RESPONSE_BYTES: usize = 32 * 1024 * 1024;

/// monerod 按路由区分 Content-Type。
///
/// 我一开始给所有路由都发 `application/octet-stream` 并在注释里断言「JSON
/// 路由也接受」—— **那是错的**，`/json_rpc` 会直接拒掉，表现为连接阶段就
/// 失败而看不出原因。二进制路由以 `.bin` 结尾，其余走 JSON。
fn content_type_for(route: &str) -> &'static str {
    if route.ends_with(".bin") {
        "application/octet-stream"
    } else {
        "application/json"
    }
}

/// 把一个 `!Send` 的 future 断言成 `Send`。
///
/// # 安全性
///
/// **只在 wasm32 上编译。** 该目标在本工程的构建配置下没有 atomics、没有
/// 线程：产物跑在单个 Worker 里，不存在第二个线程能观察到这个 future。
/// `Send` 在那里不约束任何真实行为。
///
/// 哪天这个 crate 要支持 wasm 线程（atomics + SharedArrayBuffer），**这一层
/// 必须先去掉**，否则就是真正的数据竞争。
#[cfg(target_arch = "wasm32")]
struct SingleThreaded<F>(F);

#[cfg(target_arch = "wasm32")]
// SAFETY: 见上方说明 —— 目标平台单线程，无第二个线程可观察。
unsafe impl<F> Send for SingleThreaded<F> {}

#[cfg(target_arch = "wasm32")]
impl<F: core::future::Future> core::future::Future for SingleThreaded<F> {
    type Output = F::Output;
    fn poll(
        self: core::pin::Pin<&mut Self>,
        cx: &mut core::task::Context<'_>,
    ) -> core::task::Poll<Self::Output> {
        // SAFETY: 只是把 pin 投影到唯一字段上，不移动它。
        unsafe { self.map_unchecked_mut(|s| &mut s.0).poll(cx) }
    }
}

/// 响应体上限检查，宿主通道与 wasm 内 fetch 共用。
///
/// 防一个恶意节点用超大响应把 wasm 堆撑爆。两条路都拿不到流式截断，只能收完
/// 再判 —— 但至少不会把它交给解析器再翻一倍。
#[cfg(target_arch = "wasm32")]
fn cap_response(
    bytes: Vec<u8>,
    limit: Option<usize>,
) -> std::result::Result<Vec<u8>, InterfaceError> {
    let cap = limit.unwrap_or(MAX_RESPONSE_BYTES).min(MAX_RESPONSE_BYTES);
    if bytes.len() > cap {
        return Err(InterfaceError::InvalidInterface("响应超出大小上限".into()));
    }
    Ok(bytes)
}

// 宿主提供的请求处理器。
//
// ## 为什么要把网络交给宿主
//
// 在 wasm 里 `fetch`，请求就是从浏览器上下文发出去的，于是受两条浏览器规则
// 管：混合内容（明文 HTTP 节点直接被拦）和 CORS（monerod 默认不发
// `Access-Control-Allow-Origin`，要运维自己加反代）。
//
// 结果是全网几百个活着的公共节点里我们只能用个位数 —— 2026-09-22 实测 12 家，
// 只有 1 家同时满足。让一条隐私链的所有用户共用一个第三方节点，等于把用户
// 来这里要的东西还回去：那个运营方看得见每个人的 IP、同步区间和广播时机。
//
// **这和性能无关。** 纯扫描 153 块/秒、只占总耗时 0.4%。受限的是可达性不是
// 速度，所以解法不是换原生，是把请求交出去。
//
// 这两条规则只管浏览器上下文。插件的 background（配 `host_permissions`）、
// 桌面的 Electron 主进程、移动端的 RN 原生都不受管。
//
// 宿主不装时退回 wasm 内 fetch，行为与以前完全一致 —— 增量改动。
#[cfg(target_arch = "wasm32")]
thread_local! {
    static HOST_HANDLER: std::cell::RefCell<Option<js_sys::Function>> =
        const { std::cell::RefCell::new(None) };
}

/// 装上宿主的请求处理器。传 `None` 卸载，退回 wasm 内 fetch。
///
/// 处理器签名：`(url, contentType, body: Uint8Array) => Promise<Uint8Array>`。
/// 抛出或 reject 都当作网络错误上报，原因原样带出。
#[cfg(target_arch = "wasm32")]
pub fn set_host_handler(handler: Option<js_sys::Function>) {
    HOST_HANDLER.with(|slot| *slot.borrow_mut() = handler);
}

#[cfg(target_arch = "wasm32")]
fn host_handler() -> Option<js_sys::Function> {
    HOST_HANDLER.with(|slot| slot.borrow().clone())
}

/// 走宿主通道发一次请求。
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
        InterfaceError::InterfaceError(format!("{url}: 宿主通道 {stage}: {detail}"))
    };

    let array = js_sys::Uint8Array::from(body);
    let returned = handler
        .call3(
            &JsValue::NULL,
            &JsValue::from_str(url),
            &JsValue::from_str(content_type),
            &array,
        )
        .map_err(|e| fail("调用", describe_js(&e)))?;

    // 同步返回字节或返回 Promise 都接受 —— 宿主那边怎么写都行。
    let resolved = match returned.dyn_into::<js_sys::Promise>() {
        Ok(promise) => JsFuture::from(promise)
            .await
            .map_err(|e| fail("等待响应", describe_js(&e)))?,
        Err(value) => value,
    };

    let bytes = resolved
        .dyn_into::<js_sys::Uint8Array>()
        .map_err(|_| fail("返回值", "宿主必须回 Uint8Array".to_owned()))?;
    Ok(bytes.to_vec())
}

/// 把一个 JS 侧的异常压成一句人能读的话。
///
/// `JsValue` 的 Debug 输出对排障几乎没用，而 `Error.message` 恰恰是浏览器把
/// 真实原因（DNS 失败、CORS 被拒、连接被重置）写进去的地方。
#[cfg(target_arch = "wasm32")]
fn describe_js(value: &wasm_bindgen::JsValue) -> String {
    use wasm_bindgen::JsValue;
    js_sys::Reflect::get(value, &JsValue::from_str("message"))
        .ok()
        .and_then(|m| m.as_string())
        .or_else(|| value.as_string())
        .unwrap_or_else(|| format!("{value:?}"))
}

/// 单次请求的上限。一整批块在慢节点上要十几秒，留足余量。
#[cfg(target_arch = "wasm32")]
const REQUEST_TIMEOUT_MS: u32 = 45_000;

// The host enforces a per-chunk idle limit. Its backstop must allow a large
// response to keep progressing on a slow link, matching the Worker relay.
#[cfg(target_arch = "wasm32")]
const HOST_REQUEST_TIMEOUT_MS: u32 = 10 * 60 * 1000;

/// 浏览器载体里的传输实现，基于 `fetch`。
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

    /// 有上限地等一次请求。卡住的请求会让这一步一直占着钱包库，之后的每次调用
    /// 都只能拿到 WALLET_BUSY；超时后放手，请求本身在后台自生自灭。
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
                "{url}: {} 秒没有响应",
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

        // 宿主装了处理器就交给它 —— 那条路不受浏览器的混合内容与 CORS 限制，
        // 因此能用全网任意节点。没装则退回下面的 wasm 内 fetch。
        if let Some(handler) = host_handler() {
            let bytes = post_via_host(handler, &url, content_type, &body).await?;
            return cap_response(bytes, limit);
        }

        // 每一步都带上自己的原因。九个失败点共用一句「请求失败」，就是把
        // 「节点挂了」「节点没开 CORS」「地址写错了」「响应体读不动」压成
        // 同一条信息 —— 而这几种情况用户要做的事完全不同。
        let fail = |stage: &str, detail: String| {
            InterfaceError::InterfaceError(format!("{url}: {stage}: {detail}"))
        };

        let opts = web_sys::RequestInit::new();
        opts.set_method("POST");
        let array = js_sys::Uint8Array::from(body.as_slice());
        opts.set_body(&array);

        let request = web_sys::Request::new_with_str_and_init(&url, &opts)
            .map_err(|e| fail("构造请求", describe_js(&e)))?;
        let _ = request.headers().set("Content-Type", content_type);

        let global = js_sys::global();
        let fetch = js_sys::Reflect::get(&global, &JsValue::from_str("fetch"))
            .ok()
            .and_then(|f| f.dyn_into::<js_sys::Function>().ok())
            .ok_or_else(|| {
                fail(
                    "取 fetch",
                    "当前环境没有全局 fetch（载体没装好，不是节点的问题）".to_owned(),
                )
            })?;
        let promise = fetch
            .call1(&global, &request)
            .map_err(|e| fail("发起请求", describe_js(&e)))?
            .dyn_into::<js_sys::Promise>()
            .map_err(|e| fail("发起请求", describe_js(&e)))?;

        let response = JsFuture::from(promise)
            .await
            // 走到这里基本只有两种：网络不通，或者节点没回 CORS 头。浏览器
            // 出于安全不会告诉我们是哪种，所以把两种可能都写出来。
            .map_err(|e| {
                fail(
                    "等待响应",
                    format!("{}（节点不可达，或者没有返回 CORS 头）", describe_js(&e)),
                )
            })?
            .dyn_into::<web_sys::Response>()
            .map_err(|e| fail("解析响应", describe_js(&e)))?;

        if !response.ok() {
            return Err(fail(
                "节点拒绝",
                format!("HTTP {} {}", response.status(), response.status_text()),
            ));
        }

        let buffer = JsFuture::from(
            response
                .array_buffer()
                .map_err(|e| fail("读取响应体", describe_js(&e)))?,
        )
        .await
        .map_err(|e| fail("读取响应体", describe_js(&e)))?;

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

/// 原生传输，用于本机测试与诊断 —— 浏览器里出的问题可以在这边上真调试器复现。
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
                // reqwest 默认读 macOS 系统代理，curl 与 Node 不读。本机真链测试里
                // 同一节点 curl 秒回、这里却次次卡满超时时，用它排除代理的影响。
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
            // 把真实原因带出来。一律吞成「请求失败」会让排查完全没有抓手 ——
            // 连不上、404、超时、TLS 失败在现象上一模一样。
            let response = client
                .post(&url)
                .header("Content-Type", content_type)
                .body(body)
                .send()
                .await
                .map_err(|e| {
                    // reqwest 的顶层文案只有 "error sending request"，真正原因
                    // （超时、TLS、连接被重置、DNS）在 source 链里。
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
                .map_err(|e| InterfaceError::InterfaceError(format!("读取 {url} 的响应体: {e}")))?
                .to_vec();
            let cap = response_size_limit
                .unwrap_or(MAX_RESPONSE_BYTES)
                .min(MAX_RESPONSE_BYTES);
            if bytes.len() > cap {
                return Err(InterfaceError::InvalidInterface("响应超出大小上限".into()));
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

/// 连上一个 monerod 节点。
///
/// 节点地址**由宿主给**，这里没有内置默认值 —— 内置一个默认节点等于替用户
/// 决定把自己的 IP 和查询交给谁，那是隐私决策，不该藏在 runtime 里。
pub async fn connect(base_url: &str) -> Result<Daemon> {
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
    CURRENT_NODE_URL.with(|c| *c.borrow_mut() = Some(base_url.to_owned()));
    Ok(daemon)
}

thread_local! {
    /// 当前连着的节点地址。批量取块（blocks_bin）要自己发请求，而
    /// `MoneroDaemon` 不把传输层交出来。
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

    /// 不内置默认节点：内置一个等于替用户决定把 IP 和查询交给谁。
    #[test]
    fn an_empty_node_url_is_refused_not_defaulted() {
        let e = futures_lite::future::block_on(connect("")).unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
        let e = futures_lite::future::block_on(connect("   ")).unwrap_err();
        assert_eq!(e.code, ErrorCode::NodeNotConfigured);
    }

    /// 协议头必须显式。给个 `1.2.3.4:18081` 会被当成相对路径拼出乱七八糟的
    /// URL，错误现场离真正的原因很远。
    #[test]
    fn the_node_url_must_carry_a_scheme() {
        for bad in ["1.2.3.4:18081", "node.example.com", "ftp://x"] {
            let e = futures_lite::future::block_on(connect(bad)).unwrap_err();
            assert_eq!(e.code, ErrorCode::InvalidRequest, "{bad}");
        }
    }

    /// 路由决定 Content-Type。发错会让 `/json_rpc` 在连接阶段就被拒，
    /// 而错误现场离真正的原因很远 —— 这条踩过。
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
        // 宿主传一个比硬上限还大的值时，以硬上限为准
        let requested = MAX_RESPONSE_BYTES * 4;
        assert_eq!(requested.min(MAX_RESPONSE_BYTES), MAX_RESPONSE_BYTES);
    }
}
