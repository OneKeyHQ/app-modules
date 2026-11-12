# SNI Connect native upload contract

Status: **Implemented**. The shared request, validation, transport, and resource
contract remains [OSCS](../SPEC.md). This document specifies the React Native
upload boundary and its focused review corrections.

## Public API and compatibility

- `isUploadFileSupported(): boolean` reports whether the loaded native module
  exposes a callable `uploadFile` method. It performs no I/O.
- `uploadFile(config)` always returns a Promise. When the native method is
  absent, it rejects with an Error whose `code` is `SNI_UNSUPPORTED`.
- Supported native methods receive the caller's config unchanged.

## File ownership, lifecycle, and bounds

- The caller owns the file and should keep it unchanged until settlement.
- Initial validation still requires a readable regular file in app-owned storage,
  with a size of 1 byte through 20 MiB, before admission or network I/O.
- iOS reopens the validated URL after admission, reads at most 20 MiB + 1 byte,
  and rejects an empty or oversized read before attaching the body to a request.
  The handle is closed on success and failure; file data is not memory mapped.
- Android opens and closes the validated file inside `RequestBody.writeTo`.
  It reads in bounded chunks and never writes more than the declared body length
  or 20 MiB to the sink. A changed length (growth or truncation) aborts the upload
  with an I/O error (`SNI_REQUEST_FAILED`); an already transmitted prefix can
  remain at the server. Disk input and socket output remain streaming.
- Admission, cancellation, total deadlines, response handling, and destination
  pinning continue to follow OSCS without changes.

## Transport recovery boundary

Status: **Default platform-library behavior, with historical real TLS characterization**.
The SDK adds no request retry loop. Android retains the locked OkHttp 4.12.0
connection-recovery and HTTP follow-up defaults; iOS retains the unmodified
EMASCurl 1.5.5/libcurl behavior. Existing redirect, destination, TLS, proxy,
resource and deadline constraints remain unchanged.

- One API invocation is a logical operation and may send more than one HTTP
  request. A final connection error cannot prove that the server did not
  process an earlier attempt. A 408/503 response may be followed internally and
  the caller may receive a later response instead of that original status.
- Additional business retries and domain fallbacks remain the caller's
  responsibility. The SDK does not infer business idempotency or whether a
  write completed. A caller must account for an unknown outcome.
- Internal behavior differs by library, request body and connection history.
  Fixture-specific counts below characterize locked versions; they are not
  cross-platform public guarantees or a requirement to retry every request.
- The historical Node fixture loaded the actual external consumer source;
  this package has no Node transport implementation. That HTTPS backend does
  not add a recovery loop in the characterized cases.
- Historical iOS package acceptance compiled the unpacked npm artifact's five
  original Swift sources against pristine EMASCurl 1.5.5. No dependency patch,
  consumer patch hook or replay-verification build phase is required.
- The removed fixtures replaced only test ports, DNS destinations and trusted
  CAs, retaining certificate/hostname verification and production pooling/body
  handling. Those JVM and simulator observations do not establish Android hostname
  verification, production destination admission, React Native bridge behavior
  or physical-device acceptance.

### Default behavior by layer

These are this project's **current configured defaults**, not every default of
an unconfigured library. Redirect restrictions below are explicit SDK choices.

| Layer | Default recovery and forwarding | Redirect behavior |
| --- | --- | --- |
| Android / OkHttp 4.12.0 | `retryOnConnectionFailure` is unset, retaining `true`; connection recovery and 408/503 follow-ups remain subject to the library gates below. Normal connection pooling is retained; no SDK retry loop. | `followRedirects(false)` and `followSslRedirects(false)`. |
| iOS / URLSession + EMASCurl 1.5.5 | Unmodified EMASCurl/libcurl may recover when a reused connection loses its response; body replayability matters. No SDK retry loop. Bundled `curlver.h` identifies `8.17.0-DEV`, not a separately pinned public libcurl release or a runtime `curl_version()` measurement. | `enableBuiltInRedirection = false`; the native request session delegate also rejects redirection. |
| External App Node/Desktop backend | Electron main-process `https.request` with a keep-alive `https.Agent`, not fetch/Undici. This implementation has no connection-recovery or HTTP-status retry loop; 408/503 are returned directly. It is not an SDK Node branch. | No 3xx-following loop. |
| React Native JS facade | `request` and supported `uploadFile` each forward to native once. TurboModuleRegistry takes priority over NativeModules; a missing module throws the linking error. Unsupported upload rejects with `SNI_UNSUPPORTED`. No JS retry loop; one forwarding call does not imply one HTTP send. | Determined by the native transport above. |
| App native adapter | Routes to the native SDK when the App SNI gate permits it; Mac Catalyst is excluded by the App capability check. | Native SNI restrictions above; ordinary domain routing is a separate layer. |
| App desktop adapter | Forwards through `desktopApiProxy` to the external Node backend when available and enabled. | Node backend behavior above. |
| App Web / Extension adapter | `isSniSupported()` returns `false`; `sniRequest()` returns `null`. There is no direct-IP SNI transport; ordinary domain adapters are used. | Browser/domain-client recovery and redirect defaults are outside this native SNI specification. |

OkHttp 4.12.0 recovery requires retry enabled, a recoverable exception and an
available recovery route; once sending started, one-shot bodies and
`FileNotFoundException` prevent recovery. Protocol errors, sent-request
interruptions/timeouts and certificate-verification failures are not recovered.
408 follow-up requires retry enabled, a replayable body, the immediately
preceding response (if any) not to be 408, and parsed `Retry-After <= 0` (missing
means zero). 503 requires the immediately preceding response (if any) not to be 503 and parsed
`Retry-After == 0` (missing means no immediate follow-up), without checking
the retry flag; the common follow-up exit still rejects one-shot bodies.
Non-numeric values do not request an immediate follow-up. These conditions are
from the [locked interceptor source](https://raw.githubusercontent.com/square/okhttp/parent-4.12.0/okhttp/src/main/kotlin/okhttp3/internal/http/RetryAndFollowUpInterceptor.kt),
not a global attempt-count cap.

Implementation references:

- Android: [client factory](../android/src/main/java/com/sniconnect/SniPinnedTransport.kt)
  and [OkHttp dependency](../android/build.gradle).
- iOS: [transport configuration](../ios/SniConnectPinnedTransport.swift) and
  [request/session delegate](../ios/SniConnectClient.swift). The bundled version
  identifier comes from the EMASCurl artifact's
  `precompiled/libcurl-HTTP2.xcframework/<slice>/Headers/curl/curlver.h`; the
  original Protocol source SHA was checked by the historical native fixture.
- JS facade: [forwarding API](../src/index.tsx) and
  [native-module selection](../src/NativeSniConnect.ts).
- Cross-repository source baseline is App commit
  `326854b4ba1997e06e713f12b6604f0a0f6fe165`:
  [Node backend](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/kit-bg/src/desktopApis/DesktopApiSniRequest.ts),
  [native adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.native.ts),
  [desktop adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.desktop.ts),
  and [Web/Extension adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.ts).
  Its [desktop package](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/apps/desktop/package.json)
  configures Electron 44.5.1. The historical Node CI host (observed as Node 24.22.1) tested
  the backend source; it does not establish Electron's embedded Node version.

### Historical locked-library fixture observations

The experimental recovery suites and their dedicated CI steps have been removed
from the current tree. These results come from fixed commit
[`4bbcbe4c3f94a1a4d582668aebca52f85e4f1a06`](https://github.com/OneKeyHQ/app-modules/commit/4bbcbe4c3f94a1a4d582668aebca52f85e4f1a06)
and the successful [historical CI run](https://github.com/OneKeyHQ/app-modules/actions/runs/41852054126).
They are historical observations, not current automated coverage or a promise
that future dependencies produce the same counts.

Numbers below are **server-complete requests in the fixed TLS fixture**; native
header/body events provide additional evidence. The Node suite does not
independently count header events. `UNKNOWN/65` means `SNI_UNKNOWN_ERROR` with
libcurl error 65; `NETWORK` means `SNI_NETWORK_UNREACHABLE`. HEAD's successful
response body is empty. Other method/body combinations are not inferred.

| Scenario | Android / OkHttp 4.12.0 | iOS / EMASCurl 1.5.5 | External Node backend |
| --- | --- | --- | --- |
| Warm connection, POST `demo`, response lost | 2 / final 200 | 1 / UNKNOWN/65 | 1 / `ECONNRESET` |
| Warm connection, empty POST, response lost | 2 / final 200 | 2 / final 200 | 1 / `ECONNRESET` |
| Warm connection, bodyless GET/HEAD/OPTIONS/DELETE, response lost | 2 / final 200 | 2 / final 200 | 1 / `ECONNRESET` |
| Warm connection, PUT `demo`, response lost | 2 / final 200 | 1 / UNKNOWN/65 | 1 / `ECONNRESET` |
| Warm connection, PATCH `demo`, response lost | 2 / final 200 | 1 / NETWORK | 1 / `ECONNRESET` |
| Warm connection, file POST, response lost | 2 / final 200 | 1 / UNKNOWN/65 | Not covered; the external Node suite does not exercise `uploadFile`. |
| Fresh single-route POST, response lost | 1 / I/O error | 1 / NETWORK | 1 / `ECONNRESET` |
| Processed POST, then listener and response connection close | 1 / subsequent connection attempt fails with `ConnectException` | 1 / UNKNOWN/65 | 1 / `ECONNRESET` |
| GET or POST gets 408/503 with `Retry-After: 0` | 2 / final 200 | 1 / original 408 or 503 | 1 / original 408 or 503 |
| Android test-only `retryOnConnectionFailure(false)` control | Warm POST loss: 1 / error; 503 GET/POST: still 2 / final 200 | Not applicable | Not applicable |

The last row is a test override, not the production Android default. These are
characterization results for locked versions and fixtures, not public replay
counts. Historical source: [Android TLS suite](https://github.com/OneKeyHQ/app-modules/blob/4bbcbe4c3f94a1a4d582668aebca52f85e4f1a06/native-modules/react-native-sni-connect/android/src/test/java/com/sniconnect/SniPinnedTransportReplayTest.kt),
[iOS native fixture](https://github.com/OneKeyHQ/app-modules/blob/4bbcbe4c3f94a1a4d582668aebca52f85e4f1a06/native-modules/react-native-sni-connect/tests/ios-replay/run.py) and
[Node consumer fixture](https://github.com/OneKeyHQ/app-modules/blob/4bbcbe4c3f94a1a4d582668aebca52f85e4f1a06/native-modules/react-native-sni-connect/tests/node-replay/transport.test.cjs).

### App shared routing defaults

This separate policy describes the pinned App baseline, not an SDK retry
setting. [IP_TABLE_ENABLED is `false`](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/constants/ipTableDefaults.ts),
so ordinary App native/desktop routing currently uses the domain adapter. The
SDK itself still supports direct calls. If that App gate is enabled and SNI is
supported, the host eligible and an IP selected, the
[shared adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/ipTableAdapter.ts)
behaves as follows:

- Before a SNI call, an unsupported platform, no selected IP, active proxy or
  failed proxy preflight selects the first domain request; this is not a retry
  after a SNI send. An unavailable proxy probe returning `null` alone can retain
  the legacy SNI route.
- After SNI starts, a normal thrown transport error permits one domain-adapter
  call only for GET/HEAD/OPTIONS. For fail-closed errors, only `SNI_TLS_FAILED`
  has that read-method exception; certificate/security-policy failures and
  cancellation do not receive a blanket read-method fallback.
- A `null` SNI response also permits read-method domain fallback. That branch
  awaits the domain call inside the outer `try`; if this first domain call
  fails with an allowed error, the outer `catch` can call the domain adapter a
  second time. This is current shared-layer behavior, not native-library
  recovery or a global one-fallback guarantee; it is documented here, not fixed.
- POST/PUT/PATCH/DELETE cannot use their final SNI error code to authorize an
  additional domain fallback. A resolved HTTP 408/503 response is returned
  without entering this transport-error fallback path.

An outer fallback count cannot bound total HTTP sends: the native library or
ordinary domain client can itself perform internal recovery. No additional
retry configuration is introduced by this specification.

## Android failure classification and upload replay safety

Status: **Implemented on Android**. This section maps the Section 9 error
categories of [OSCS](../SPEC.md) to the codes Android rejects with; iOS keeps
its own mapping.

- `SNI_CONNECTION_REFUSED` reports a `java.net.ConnectException`, but only when
  no attempt of the logical request ever started transmitting it. The transport
  records request-header transmission from OkHttp's `requestHeadersStart`
  event, which fires after connect/TLS, so the same evidence covers bodyless
  requests such as `DELETE` that never write a request body. The pinned IP
  refused the TCP connection before any request bytes could have been sent, so
  this code is evidence that the request was not transmitted.
- A connect failure is downgraded to `SNI_REQUEST_FAILED` when any attempt of
  the same logical request already started transmitting request headers. OkHttp
  can recover and retry inside one logical operation, so a final `ConnectException`
  does not prove that a previous attempt was unsent, even for a bodyless
  DELETE. `SNI_REQUEST_FAILED` also covers unclassified request-phase I/O
  failures; timeout, TLS, certificate, and DNS failures keep their own codes.
  Callers MUST NOT treat `SNI_REQUEST_FAILED` as pre-write authorization to
  replay an upload or another write operation.
- `SNI_DNS_FAILED` reports a `java.net.UnknownHostException` that is not a
  pinned-destination escape.
- The pinned resolver never consults system DNS, so a lookup for a host other
  than the caller-selected hostname is a pinned-destination escape. It stays
  `SNI_SECURITY_POLICY_FAILED` and remains fail-closed with no fallback.
- Upload file read failures keep their existing codes: a changed file length is
  reported as `SNI_REQUEST_FAILED` because an already transmitted prefix may
  have been processed by the server.

Only a provably pre-write `SNI_CONNECTION_REFUSED` can authorize a
same-operation domain upload, and only Android provides that evidence from the
native module. The App-side pre-write upload allowlist that consumes this
distinction is a separate change: the routing defaults above are unchanged by
this section, so a POST/PUT/PATCH/DELETE still cannot fall back on any other
final SNI code and read methods keep their existing fallback rules.

## Diagnostic log fields

Status: **Implemented**. Structured native logs record `requestId`, destination
`ip`, and unexpected DNS `requestedHost` in plaintext, preserving identifiers
and endpoints for troubleshooting. These fields are not hashed. Log values
still escape whitespace to keep each event on one line. Sensitive headers,
request/response bodies, and local file paths are not added to these logs.
Long hexadecimal runs in the displayed `requestId` are separated with `-` after
32 hex characters when another 32 hex characters follow. This preserves the
complete ID while avoiding the final logger's generic 64-hex private-key filter.
For example, a 64-hex ID is displayed as two 32-hex groups. UUIDs are unchanged;
the ID sent in headers and used for cancellation is never reformatted.
IDs affected by whitespace cleanup are encoded from their original value before
that cleanup. Only reformatted IDs add `requestIdFormat=uri-hex32` to the event.
Marked IDs are UTF-8 percent-encoded, leaving only ASCII letters, digits, `_` and
`.` unescaped, before hex grouping. This includes hex runs formed beside escape
sequences. To restore a marked ID, remove grouping hyphens, then percent-decode
once. This preserves original spaces, separators and percent signs. An already
separated 32-hex/32-hex ID is unchanged and has no marker. `requestId` is
non-sensitive diagnostic metadata and must not carry secrets; unrelated values
retain the generic logger's sensitive-data filtering.

## Conformance and remaining evidence

- JS API tests cover capability detection, missing-native Promise rejection,
  supported forwarding, and synchronous native errors becoming rejections.
- Swift validation tests exercise bounded reads after validated files grow or
  become empty, and exact-limit binary contents.
- Android tests exercise actual RequestBody writes for exact-limit data, growth
  before and during writing, and truncation, including output byte bounds.
- Android tests classify a pre-write `ConnectException` as
  `SNI_CONNECTION_REFUSED`, keep the pinned-destination escape fail-closed,
  verify the request-header transmission tracking, and keep a processed
  bodyless `DELETE` followed by a refused recovery connection ambiguous as
  `SNI_REQUEST_FAILED`.
- The removed recovery suites used real TLS, production transport sources,
  warmed pools, complete-body counters and native events. Their observations
  are retained in the historical matrix above; the current tree and CI no
  longer provide those experimental 18/15/14-case suites.
- Native device/network upload acceptance remains separate from these unit tests.
