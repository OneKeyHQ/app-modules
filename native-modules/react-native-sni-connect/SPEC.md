# OneKey SNI Connect Standard (OSCS)

- **Version:** 1.2
- **Status:** Active
- **Last updated:** 2026-06-30
- **Applies to:** every implementation of OneKey's SNI connect module: iOS
  (Swift + EMASCurl), Android (Kotlin + OkHttp), Node/Desktop (Electron main
  process + Node HTTPS), the React Native JS surface, app-monorepo shared
  adapters, and any future platform.

This document defines the mandatory behavior expected from the SNI connect
module. It is platform-agnostic: implementations MAY differ in transport and
native APIs, but they MUST be behaviorally equivalent for everything described
here. It is the reference; when an implementation and this document disagree,
the implementation is at fault.

Normative language:

- `MUST`, `MUST NOT`, `REQUIRED`, and `SHALL` define mandatory requirements.
- `SHOULD` defines a strong recommendation. Any exception MUST be documented
  with a platform reason and accepted by review.
- `MAY` defines optional behavior.

Acceptance is strict: an implementation that violates any mandatory requirement
is non-conformant and MUST NOT advertise SNI support in production.

---

## 1. Purpose & scope

The SNI connect module performs one HTTPS request to a caller-selected public IP
address while preserving the caller-selected hostname for:

- the request URL authority,
- the `Host` header,
- TLS SNI,
- certificate hostname verification.

This lets callers test or use a specific resolved endpoint without weakening TLS
hostname validation.

**In scope:** request input validation, URL construction, public-IP filtering,
pinned destination resolution, TLS validation, redirect boundaries, timeout
behavior, cancellation, response shape, client/session/socket caching, cleanup,
and platform equivalence.

**Out of scope:** choosing the best IP, resolving DNS in JS, retries, cookie
management, certificate pinning, application-level authentication, response
body streaming, and binary response transport.

General-purpose proxying is out of scope. SNI requests MUST bypass system,
session, PAC, and environment proxy configuration so the socket endpoint remains
the pinned `ip:443`.

---

## 2. Architecture

```
  caller
    |
    | request({ ip, hostname, method, path, headers, body, timeout })
    v
  +------------------------- SNI Connect -------------------------+
  |                                                               |
  |  validate inputs                                              |
  |    - ip is a public literal                                   |
  |    - hostname is a DNS hostname                               |
  |    - path is relative                                         |
  |    - method/header/body are bounded by this spec              |
  |                                                               |
  |  build HTTPS request                                          |
  |    url  = https://<hostname><path>                             |
  |    Host = <hostname>                                          |
  |                                                               |
  |  pinned destination                                           |
  |    URL authority/Host/SNI/cert name = <hostname>               |
  |    network endpoint = <ip>:443                                |
  |    any attempt to resolve/connect elsewhere fails closed       |
  |                                                               |
  |  transport                                                    |
  |    connect to <ip>:443                                        |
  |    send TLS SNI <hostname>                                    |
  |    validate certificate for <hostname>                         |
  |                                                               |
  +------------------------------+--------------------------------+
                                 |
                                 v
                    HTTPS response / platform error
```

The module is not a general-purpose HTTP client. It deliberately narrows the
request surface so caller-controlled IPs cannot become an SSRF, cleartext
downgrade, header injection, DNS fallback, redirect escape, or
certificate-bypass primitive.

---

## 3. Public API contract

This is the canonical contract across iOS, Android, Node/Desktop, and shared
JS adapters. Legacy integration layers MAY temporarily map equivalent field
names such as `body`/`statusCode`, but the boundary exposed to product code MUST
normalize to this shape and MUST NOT drop required information such as repeated
headers.

### 3.1 Request

```ts
type SniConnectRequestBase = {
  requestId?: string;
  ip: string;
  hostname: string;
  path: string;
  headers: Record<string, string>;
  timeout: number;
};

export type SniConnectRequest =
  | (SniConnectRequestBase & { method: 'GET' | 'HEAD'; body?: never })
  | (SniConnectRequestBase & { method: 'POST' | 'PUT' | 'PATCH'; body: string })
  | (SniConnectRequestBase & {
      method: 'DELETE' | 'OPTIONS';
      body?: string | null;
    });
```

- `requestId` is optional. It is required only if the caller wants cancellation.
  A caller MUST NOT reuse a request id while a previous request with that id is
  still active. When present, it MUST be a non-empty UTF-8 string no longer than
  128 bytes and MUST NOT contain control characters.
- `ip` is a literal IPv4 or IPv6 address. It MUST NOT be a hostname.
- `hostname` is the real DNS hostname used for URL authority, `Host`, SNI, and
  certificate validation. It MUST NOT be replaced by `ip`.
- `method` is normalized to uppercase and is limited to the supported method
  set in Section 4.3.
- `path` is a relative path/query. It MAY contain a query string but MUST NOT carry
  a scheme, authority, host, or port.
- `headers` are caller headers except for module-owned headers described in
  Section 4.4.
- `body` is an optional UTF-8 string request body.
  `GET` and `HEAD` MUST NOT carry a body. `POST`, `PUT`, and `PATCH` MUST carry
  an explicit body; callers that need an empty payload MUST pass an empty string.
- `timeout` is a total request deadline in milliseconds. It includes pinned
  destination setup, connect, TLS, request upload, response headers, and
  response body read.
  The value MUST be a positive finite number. Implementations MUST define a
  maximum accepted timeout and reject larger values before network I/O. The
  baseline maximum is 120 seconds unless a product flow deliberately raises it
  on every supported platform and adds conformance tests.

### 3.2 Response

```ts
export type SniConnectResponse = {
  data: string;
  status: number;
  statusText: string;
  headers: Record<string, string>;
  multiValueHeaders?: Record<string, string[]>;
};
```

- `data` is a string. This module is a text-response API; binary response
  transport is out of scope.
- `status` comes from the HTTP response. `statusText` is the reason phrase when
  the transport exposes one; otherwise it is an empty string.
- `headers` is a backward-compatible single-value map. Header names are
  normalized to lowercase where the platform exposes enough information to do so.
  If a response contains repeated headers, `headers[name]` contains the last
  observed value.
- `multiValueHeaders`, when the transport exposes enough information, MUST preserve
  repeated header values such as `set-cookie` without collapsing them. New
  implementations MUST expose it; platforms that cannot MUST document the
  degradation at the adapter boundary.
- HTTP `4xx` and `5xx` are resolved as normal responses. The caller inspects
  `status`.

### 3.3 Methods

- `request(config): Promise<SniConnectResponse>`
- `cancelRequest(requestId): Promise<{ success: boolean }>`
- `cancelAllRequests(): Promise<{ success: boolean }>`
- `clearDNSCache(): Promise<{ success: boolean }>`
- `isProxyActiveForUrl(url): Promise<boolean>`

Adapters MAY additionally expose `isSupported()`. It is a capability probe only
and does not change the behavior required when SNI is supported.

`isProxyActiveForUrl(url)` is a preflight probe for shared adapters before they
enter SNI mode. It MUST inspect platform per-URL/system proxy state for the
given HTTP(S) URL and resolve `true` when the URL would use a proxy outside the
SNI module. It MUST NOT change low-level SNI request behavior: actual SNI
transport still bypasses proxy configuration directly.

---

## 4. Request validation & normalization

### 4.1 IP literal and public destination

`ip` MUST parse as a literal IPv4 or IPv6 address without DNS resolution.
It MUST NOT include brackets, a port, a zone identifier, whitespace, or any
hostname-like value.

The module MUST reject:

- loopback and unspecified addresses,
- private and unique-local ranges,
- link-local ranges, including cloud metadata addresses,
- carrier-grade NAT ranges,
- multicast ranges,
- documentation, benchmark, and reserved ranges,
- IPv4-compatible and IPv4-mapped IPv6 addresses whose embedded IPv4 target is
  forbidden,
- IPv6 translation or transition ranges that can hide or route to forbidden IPv4
  destinations, including NAT64 local-use and 6to4,
- NAT64 well-known-prefix addresses whose embedded IPv4 target is forbidden.

The accepted target is therefore a public/global-unicast literal address.

### 4.2 Hostname

`hostname` MUST be a DNS hostname:

- 1 to 253 characters total,
- labels are 1 to 63 characters,
- labels contain only ASCII letters, digits, and hyphen,
- labels MUST NOT start or end with hyphen.

IP literals, empty strings, control characters, and URL-like values are invalid
hostnames for this module.

### 4.3 Method and path

Supported methods are:

- `GET`
- `POST`
- `PUT`
- `PATCH`
- `DELETE`
- `HEAD`
- `OPTIONS`

`path` is normalized by trimming surrounding whitespace and prepending `/` when
missing. It MUST be rejected if it:

- contains control characters,
- starts with `//`,
- contains `://`,
- starts with a URI scheme such as `http:`, `https:`, `file:`, or `javascript:`.

The final URL is always `https://<hostname><normalizedPath>` on the implicit
HTTPS port `443`.

### 4.4 Headers

Header names and values MUST reject CR, LF, and all other control characters.
Header names MUST follow the HTTP token grammar:

```
! # $ % & ' * + - . ^ _ ` | ~ 0-9 A-Z a-z
```

Header values MAY be empty but MUST NOT contain control characters.
Implementations MUST reject requests whose caller headers exceed these baseline
limits:

- header count: 64,
- total caller header bytes: 32 KiB,
- single header name: 128 bytes,
- single header value: 8 KiB.

The module owns these headers:

- `Host`
- `Content-Length`
- platform/internal transport headers, including EMASCurl config headers
- transport security and routing headers that can alter connection semantics,
  including `Connection`, `Keep-Alive`, `Proxy-*`, `TE`, `Trailer`,
  `Transfer-Encoding`, `Upgrade`, `Expect`, HTTP/2 pseudo-headers, and
  implementation private headers

Caller-provided values for module-owned headers MUST NOT override the module's
own values. The module MUST set `Host` to `hostname` after filtering caller
headers. The module MUST compute or omit `Content-Length` according to the final
body it sends. Unsafe routing/security headers MUST be rejected before network
I/O, except for documented compatibility drops of `Host`, `Content-Length`, and
platform-internal headers. Caller-provided values MUST NEVER override
module-owned values.

### 4.5 Body and size limits

`body` is a UTF-8 string. Binary request bodies and streaming uploads are out of
scope. Implementations MUST define finite request-body and response-body limits
so a caller-selected endpoint cannot force unbounded memory growth. These
baseline limits are REQUIRED unless a product flow deliberately raises them on
every supported platform and adds conformance tests:

- request body: 1 MiB,
- response body: 10 MiB,
- path/query: 8 KiB.

Requests exceeding the request body or path/query limits MUST be rejected before
network I/O. Responses exceeding the response body limit MUST abort transport
work and reject with a response-processing error.

---

## 5. Destination pinning, TLS, and redirects

### 5.1 Pinned destination

For each request, the network destination is pinned to exactly one pair:

```
hostname -> ip
```

The request authority, `Host` header, TLS SNI, and certificate hostname
verification all use `hostname`. The socket endpoint uses `ip:443`.

Implementations MAY satisfy this in either way:

- URL-stack transports MAY build `https://<hostname><path>` and install a pinned
  resolver that returns `ip` only for `hostname`.
- Direct-socket transports MAY connect to the IP literal directly, provided
  `Host`, TLS SNI, and certificate verification all remain bound to `hostname`.

If any transport layer attempts to resolve or connect to a hostname other than
the configured `hostname`, the implementation MUST fail closed instead of
falling back to system DNS. Direct-socket implementations MUST NOT use the
system resolver for the caller-provided `ip`.

SNI requests MUST bypass all proxy configuration, including system proxy,
environment proxy variables, PAC files, Electron session proxy settings, and
platform URL-session proxy settings. If a platform cannot guarantee proxy
bypass, it MUST return unsupported before starting network I/O.
Shared adapters SHOULD call `isProxyActiveForUrl(url)` before starting SNI and
avoid SNI mode when it returns `true`; this preflight does not relax the native
transport's mandatory direct/no-proxy requirement.

Pinned destination state MUST be isolated by `(hostname, ip)`. Concurrent
requests for the same hostname but different IPs MUST NOT overwrite each other.
Connection pool keys MUST include at least normalized `hostname`, `ip`, and
port. Connections MUST NOT be reused across different `(hostname, ip, port)`
pairs, even when TLS certificates would allow HTTP/2 connection coalescing.

### 5.2 TLS

The module MUST NOT disable TLS certificate validation.

TLS validation is performed against `hostname`, not `ip`:

- SNI is `hostname`,
- hostname verification checks the certificate CN/SAN against `hostname`,
- the trust chain is validated by the platform trust manager.

An expired, untrusted, mismatched, or otherwise invalid certificate MUST reject
the request.

### 5.3 Redirects

Redirect handling MUST NOT widen the trust boundary. Best practice is to not
follow redirects automatically and to return the `3xx` response to JS.

If a platform follows redirects, every hop MUST keep the same scheme
(`https`) and the same hostname, and each hop MUST continue to use the same
pinned destination semantics. Relative redirects are allowed only when they
resolve under the same `https://<hostname>` authority.

Redirects to another hostname, another scheme, cleartext HTTP, an absolute URL
that changes authority, or any URL whose host would require system DNS MUST be
rejected.

### 5.4 Protocol upgrades and alternate services

Protocol features that can move traffic away from the pinned endpoint MUST be
disabled unless the implementation can prove and test that the effective socket
endpoint remains the same `ip:443` for the entire request.

Required behavior:

- HTTP/2 connection coalescing across hostnames MUST be disabled or prevented by
  pool isolation.
- Alt-Svc, HTTP/3, QUIC, and connection migration MUST be disabled.
- Cleartext upgrade mechanisms MUST be rejected.
- Platform caches that remember alternate services or protocol routes MUST NOT
  affect SNI requests.

---

## 6. Timeout, cancellation, and concurrency

### 6.1 Timeout

`timeout` is a total request deadline. It MUST be applied per request rather
than by mutating process-global transport state.

An implementation MAY use internal connect/read/write timeouts, but those
timeouts MUST NOT cause a request with a larger caller deadline to fail earlier
unless the failure is surfaced as a timeout and documented as a platform limit.
Socket idle timeouts, agent timeouts, and transport defaults MUST either be
disabled for the request or set no shorter than the effective caller deadline.
Timeout state MUST NOT be stored in process-global mutable configuration.

### 6.2 Cancellation

Cancellation is request-id based.

- A request with no `requestId` cannot be cancelled individually.
- `cancelRequest(requestId)` cancels the active platform task/call/request for
  that id and resolves `{ success: true }` when one was found.
- It resolves `{ success: false }` when no active request exists for that id.
- `cancelAllRequests()` cancels every request that is active at the time the
  cancellation snapshot is taken.

Every runtime that supports SNI requests, including Node/Desktop, MUST expose
the cancellation API. Unsupported platforms MAY return `null` from a higher
level adapter before a request starts, but MUST NOT start non-cancellable SNI
work while advertising support.

Completion cleanup MUST be pair-aware: a finishing request MUST NOT unregister a
newer request that reused the same id after it.

The request MUST remain cancellable until the platform response body has been
fully processed or the request has failed.

### 6.3 Concurrent requests

Concurrent requests are allowed. They MUST be isolated by request state,
especially:

- pinned destination or DNS resolver state,
- transport/session/client cache keys,
- cancellation registration,
- timeout state.

The implementation MUST avoid per-request unbounded thread pools, unbounded
socket creation, or unbounded client/session growth. Caches and connection pools
MUST be bounded and clearable.

Baseline concurrency limits:

- active SNI requests per runtime: 64,
- active SNI requests per `(hostname, ip)` pair: 16,
- queued SNI requests, if supported: 64.

Implementations MUST either reject work that exceeds these limits with a stable
error or queue it within the bounded queue. They MUST NOT create unbounded native
threads, Node sockets, sessions, clients, promises, or task records.

---

## 7. Cache and cleanup

The module MAY cache platform clients, sessions, resolver classes, agents, and
underlying connections to avoid connection setup overhead.

Required properties:

- cache keys MUST include at least `(hostname, ip)`,
- cache size MUST be bounded,
- cache eviction MUST NOT invalidate unrelated active requests,
- `clearDNSCache()` MUST remove cached pinned destination, DNS resolver, client,
  session, and agent state, and MUST evict idle pinned connections,
- memory-pressure cleanup MAY be used when supported by the platform.

`clearDNSCache()` is a cleanup operation. It is not a substitute for request
cancellation. It MUST NOT cancel active requests unless the platform cannot
evict idle state independently; that limitation MUST be documented and covered
by tests.

Static resolver or agent registries are cache state. They MUST be bounded,
reused, or clearable; they MUST NOT grow without limit as JS supplies new
`(hostname, ip)` pairs.

---

## 8. Response handling

The response body is returned as a string after transport decompression, if the
transport performs decompression. The module MUST NOT promise binary fidelity
for arbitrary bytes.

Compression handling:

- Implementations MAY enable `gzip`, `deflate`, or `br` decompression.
- If decompression is enabled, implementations MUST count both compressed bytes
  and decompressed bytes while streaming or reading.
- The decompressed byte limit is the response body limit in Section 4.5.
- The compressed byte limit MUST NOT exceed the response body limit.
- The decompressed/compressed ratio MUST NOT exceed 20:1 unless a product flow
  deliberately raises it on every supported platform and adds conformance tests.
- If an implementation cannot enforce these limits, it MUST disable automatic
  decompression for SNI requests.

Header behavior:

- single-value `headers` is retained for backward compatibility,
- repeated headers MUST be exposed through `multiValueHeaders` where the
  transport exposes repeated values,
- header names MUST be lowercase where the transport exposes header names,
- `headers[name]` uses the last observed value for that lowercased header name,
- response headers MUST NOT be parsed by ad hoc splitting of raw header text.

The module MUST NOT use automatic platform cookie storage. It MUST NOT read from
the platform cookie jar before a request and MUST NOT write `Set-Cookie` values
from a response into the platform cookie jar. Caller-provided `Cookie` headers
are treated as ordinary explicit caller headers after validation.

The response body MUST be read with a finite size limit. If the limit is
exceeded, the promise rejects with a response-processing error instead of
continuing to allocate memory.

---

## 9. Error model

The module rejects the promise for:

- invalid input,
- pinned destination lookup or connection failure,
- TLS/certificate failure,
- network failure,
- timeout,
- cancellation,
- platform response processing failure.

The module resolves the promise for HTTP responses, including `3xx`, `4xx`, and
`5xx`, unless the response violates this standard.

Errors surfaced to JS MUST be stable enough for callers to distinguish:

- invalid config,
- cancellation,
- timeout,
- pinned destination or DNS failure,
- TLS/certificate failure,
- network failure,
- response processing failure.

Platform logs MAY contain diagnostic detail, but JS-facing error messages MUST
not leak sensitive headers, request bodies, internal file paths, or transport
configuration ids.

The low-level `request(config)` API MUST reject on the failures above. A
higher-level product adapter MAY catch those failures and fall back to a normal
domain request, but that fallback is outside this module and MUST report or log
the SNI failure with enough structured detail to debug platform differences.

Fallback policy:

- Security policy failures MUST fail closed and MUST NOT fall back to a normal
  domain request. This includes invalid input, forbidden IPs, unsafe headers,
  proxy-bypass failure, redirect-boundary failure, TLS/certificate failure,
  pinned destination escape, protocol-upgrade escape, and response-size limit
  violations.
- Capability or routing absence MAY fall back before SNI work starts. Examples:
  unsupported platform, disabled IP table, or no selected IP for the hostname.
- Transient network failures MAY fall back only if the product flow explicitly
  allows it and logs the SNI failure. Examples: connect timeout, connection
  refused, network unreachable, or remote connection reset.
- Cancellation MUST NOT fall back.
- Fallback MUST preserve the original product request semantics and MUST NOT
  rewrite unsafe SNI inputs into a new request.

---

## 10. Trust boundary

This module enforces transport security for a caller-selected endpoint. It does
not decide whether the caller trusts the chosen IP.

The low-level SNI request API is a privileged transport primitive. Product-level
shared adapters MUST restrict SNI usage to an explicit hostname/root-domain
allowlist owned by the IP table configuration. Arbitrary app code MUST NOT be
able to use SNI connect as a general-purpose public-IP HTTPS client.

Caller responsibilities:

- choose `ip` and `hostname` from a trusted source,
- verify that using a specific IP is appropriate for the product flow,
- supply authentication headers or tokens,
- perform application-level retry/backoff if desired,
- interpret HTTP status and response body.

Module responsibilities:

- reject unsafe IP literals and malformed request data,
- reject or refuse to advertise support when proxy bypass or pinned destination
  guarantees cannot be enforced,
- preserve hostname-based TLS validation,
- prevent redirect and DNS fallback from escaping the pinned endpoint,
- avoid cross-request state leakage,
- provide cancellation and cleanup primitives.

The public request API does not accept a caller-controlled port. The network
endpoint is `ip:443`. Test-only or internal tooling that needs a custom port
MUST keep the same validation, TLS, redirect, and cancellation guarantees and
MUST NOT be reachable from normal product request flows.

---

## 11. Conformance scenarios

An implementation is conformant when it passes all of the following:

| # | Scenario | Expected result |
| --- | --- | --- |
| 1 | Two concurrent requests use the same hostname but different IPs | Each request connects to its own pinned IP; neither resolver overwrites the other |
| 2 | The pinned IP is private, loopback, link-local, metadata, multicast, reserved, or hidden inside an IPv6 transition form | Request is rejected before network I/O |
| 3 | `path` is an absolute URL, protocol-relative URL, cleartext URL, or scheme-like value | Request is rejected before network I/O |
| 4 | Caller supplies `Host`, `Content-Length`, or an internal transport config header | Module-owned value wins; caller cannot override SNI/Host/body framing/config routing |
| 5 | Caller supplies `Connection`, `Proxy-*`, `Transfer-Encoding`, `Expect`, HTTP/2 pseudo-headers, or another routing/security header | Request is rejected or the unsafe header is dropped before network I/O according to documented compatibility policy |
| 6 | Server redirects to another hostname | Redirect is not followed, or request fails closed without system DNS |
| 7 | Server redirects to HTTP | Redirect is not followed, or request fails closed before cleartext I/O |
| 8 | Server returns a same-host relative redirect | Redirect is returned as `3xx`, or followed only with the same pinned destination semantics |
| 9 | TLS certificate is valid for the IP but not the hostname | Request fails certificate validation |
| 10 | TLS certificate is valid for the hostname while the socket connects to the pinned IP | Request succeeds when the server completes HTTPS normally |
| 11 | `cancelRequest()` is called while headers/body are still being processed | Platform work is cancelled and the promise rejects as cancelled |
| 12 | `cancelRequest()` is called for an unknown id | Promise resolves `{ success: false }` |
| 13 | A request completes while a newer request reuses the same request id | Completion cleanup does not unregister or cancel the newer request |
| 14 | Response contains repeated headers such as `Set-Cookie` | Single-value headers remain backward compatible and multi-value headers preserve all values where the transport exposes them |
| 15 | Response body exceeds the documented limit | Request rejects with a response-processing error without unbounded allocation |
| 16 | `clearDNSCache()` is called after requests complete | Cached pinned destination/resolver/client/session/agent state and idle connections are dropped without corrupting future requests |
| 17 | Node/Desktop direct-socket implementation receives a hostname in `ip` or an IP with a port | Request is rejected before `https.request()` or equivalent network I/O |
| 18 | System, environment, PAC, or Electron/session proxy is configured | SNI request bypasses the proxy, or the platform reports unsupported before network I/O |
| 18a | `isProxyActiveForUrl(url)` is called for a URL that would use a system/per-URL proxy | The preflight resolves `true`, and adapters avoid SNI before native request I/O starts |
| 19 | A server advertises Alt-Svc, HTTP/3, QUIC, or cross-host HTTP/2 coalescing is possible | The request remains on the pinned `ip:443` endpoint or the feature is disabled |
| 20 | SNI fails because of invalid input, forbidden IP, unsafe header, proxy-bypass failure, redirect escape, TLS/cert failure, protocol escape, or response-size violation | Higher-level adapters do not fall back to a normal domain request |
| 21 | SNI fails because the platform is unsupported or no selected IP exists | Higher-level adapters MAY fall back before SNI network I/O starts |
| 22 | The response sets cookies and a later SNI request omits an explicit `Cookie` header | The later request does not automatically send cookies from platform storage |
| 23 | Caller explicitly supplies a valid `Cookie` header | The header is sent only for that request and is not stored globally |
| 24 | Compressed response expands past the response limit or ratio limit | Transport is aborted and the promise rejects without unbounded allocation |
| 25 | Caller exceeds requestId, header count, header byte, request body, path/query, active request, or queue limits | Request is rejected or bounded-queued according to this standard; no unbounded resources are created |
| 26 | Product-level shared adapter is asked to use SNI for a hostname outside the IP table allowlist | Adapter refuses SNI and does not expose the low-level primitive as a general-purpose client |
| 27 | A request without `requestId` is active when `cancelAllRequests()` is called | The platform task/call/request is cancelled even though it cannot be cancelled individually |

Conformance is evaluated per behavior. This document records no implementation's
state; platform-specific gaps are tracked in code review or issue notes until
closed.

---

## 12. Acceptance gate and enforcement

This document is both the implementation standard and the acceptance checklist.
For a platform implementation or shared adapter to be accepted:

- Every `MUST` and `MUST NOT` requirement in this document MUST be satisfied.
- Every scenario in Section 11 MUST have automated or documented manual
  verification for each supported runtime: iOS, Android, Node/Desktop, and
  shared JS adapters where applicable.
- A runtime MUST NOT return supported from `isSupported()` or equivalent
  capability checks unless it satisfies all mandatory requirements that apply to
  that runtime.
- A shared adapter MUST NOT route production traffic through SNI unless the
  underlying runtime is conformant and the target hostname passes the configured
  allowlist.
- Any `SHOULD` exception MUST be documented with the platform limitation, risk,
  fallback behavior, and reviewer approval.
- Compatibility mappings such as `statusCode`/`body` MUST be tested to preserve
  the canonical response contract.
- Security-policy failures MUST be covered by negative tests that prove
  fail-closed behavior and no normal-domain fallback.

Acceptance evidence MUST include formal unit tests for validators and adapters
on every supported implementation: XCTest for iOS validation logic, Gradle/JUnit
for Android validation logic, and Jest for Node/Desktop and shared JS adapters.
Those unit tests MUST cover the negative security cases in Section 11 that can
be proven without network I/O. Acceptance evidence MUST also include integration
tests against controlled HTTPS servers for transport-only behavior, plus platform
build verification. Missing evidence for a mandatory requirement is a release
blocker unless the platform does not advertise SNI support.

---

## Appendix A. Platform constraints (non-normative)

- **iOS** uses `URLSession` configured through EMASCurl/libcurl. DNS pinning is
  implemented with EMASCurl DNS resolver classes. Resolver state MUST be
  isolated per `(hostname, ip)` because resolver classes are shared by the
  transport. EMASCurl 1.5.5 exposes responses to this module through
  `HTTPURLResponse`, whose header fields are already a dictionary view; iOS can
  only preserve repeated response header values when the transport exposes raw
  repeated header entries.
- **Android** uses OkHttp. DNS pinning is implemented with a custom `Dns`
  instance per cached `(hostname, ip)` client. OkHttp redirect defaults MUST be
  overridden or constrained so redirects do not bypass the pinned endpoint.
- **Node/Desktop** uses the Electron main process and Node HTTPS stack. It MAY
  connect directly to the IP literal instead of installing a DNS hook, but it
  MUST set TLS `servername` to `hostname`, verify the certificate against
  `hostname`, force `Host` to `hostname`, isolate agent pool keys by
  `(hostname, ip, port)`, bypass system/Electron proxies, disable endpoint-moving
  protocol features, and expose cancellation plus cache cleanup by destroying
  idle pinned sockets.
- **Shared JS adapters** normalize product-level request/response shapes. They
  MAY translate legacy field names, but they MUST NOT weaken validation, hide
  support status, bypass hostname allowlists, drop repeated headers when
  available, or silently convert a low-level SNI failure into success.
- **React Native codegen** limits the portable JS/native type surface. The
  public API stays string-body based until a binary-safe response type is added
  deliberately on every platform.

## Appendix B. Change log

- **1.2** (2026-06-29) - Added normative language, acceptance gate,
  proxy-bypass requirements, fallback fail-closed policy, protocol-upgrade and
  connection-coalescing constraints, concrete resource limits, compression bomb
  protection, cookie-store isolation, product allowlist requirements, and
  expanded conformance scenarios.
- **1.1** (2026-06-29) - Added Node/Desktop and shared-adapter scope, replaced
  DNS-only wording with pinned destination semantics, tightened header,
  timeout, cancellation, cache, response-size, and conformance requirements.
- **1.0** (2026-06-29) - Initial standard.
