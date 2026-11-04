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

Status: **Default platform-library behavior, characterized with real local TLS tests**.
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
- The consumer Node backend is tested by loading its actual source through an
  explicit `--consumer-root`; this package has no Node transport implementation.
  Its current HTTPS backend does not add a recovery loop in these cases.
- iOS package acceptance compiles the unpacked npm artifact's five original
  Swift sources against pristine EMASCurl 1.5.5. No dependency patch, consumer
  patch hook or replay-verification build phase is required.
- Local fixtures replace only test ports, DNS destinations and trusted CAs,
  retaining certificate/hostname verification and production pooling/body
  handling. JVM and simulator acceptance do not establish Android hostname
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
  original Protocol source SHA is checked by the native fixture.
- JS facade: [forwarding API](../src/index.tsx) and
  [native-module selection](../src/NativeSniConnect.ts).
- Cross-repository source baseline is App commit
  `326854b4ba1997e06e713f12b6604f0a0f6fe165`:
  [Node backend](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/kit-bg/src/desktopApis/DesktopApiSniRequest.ts),
  [native adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.native.ts),
  [desktop adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.desktop.ts),
  and [Web/Extension adapter](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/packages/shared/src/request/helpers/sniRequest.ts).
  Its [desktop package](https://github.com/OneKeyHQ/app-monorepo/blob/326854b4ba1997e06e713f12b6604f0a0f6fe165/apps/desktop/package.json)
  configures Electron 44.5.1. The Node CI host (observed as Node 24.22.1) tests
  the backend source; it does not establish Electron's embedded Node version.

### Locked-library fixture observations

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
counts. Evidence: [Android TLS suite](../android/src/test/java/com/sniconnect/SniPinnedTransportReplayTest.kt),
[iOS native fixture](../tests/ios-replay/run.py) and
[Node consumer fixture](../tests/node-replay/transport.test.cjs).

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

## Conformance and remaining evidence

- JS API tests cover capability detection, missing-native Promise rejection,
  supported forwarding, and synchronous native errors becoming rejections.
- Swift validation tests exercise bounded reads after validated files grow or
  become empty, and exact-limit binary contents.
- Android tests exercise actual RequestBody writes for exact-limit data, growth
  before and during writing, and truncation, including output byte bounds.
- `SniPinnedTransportReplayTest` uses real local TLS, the production client
  factory, a warmed connection pool, server-side complete-body counters, and
  OkHttp events. It covers lost responses, a closed listener, fresh-route and
  disabled-connection-retry controls, all supported HTTP methods, 503/408
  responses and streamed/empty POST bodies. The JVM fixture changes only local routing, certificate
  trust/hostname verification, test ports and deadlines; it does not prove the
  Android bridge or production destination admission.
- Native device/network upload acceptance remains separate from these unit tests.

### Automated replay conformance

- `native-tests` runs the Android TLS suite in the existing `android-unit` job.
- The existing `device-utils-store` job installs only EMASCurl 1.5.5 in a
  temporary CocoaPods project, then runs `tests/ios-replay/package_test.py
  --output-dir <temporary-directory>`. It packs the SDK once, verifies original source
  identity and the absence of the removed replay patch/hook, and compiles
  that package with pristine EMASCurl for all 15 native TLS cases.
  Runtime/device and host architecture are discovered from available simulator
  inventory; a missing runtime fails rather than skipping. GitHub-hosted CI
  starts an available preinstalled iPhone immediately after selecting Xcode,
  overlapping its boot with existing host suites. It records the initial state
  and whether this suite started the boot; cleanup shuts down only a boot owned
  by this suite. Local runs still use only their dedicated external device set.
  Readiness remains bounded by 180 seconds. Downloads may be cached; device
  data and builds MUST NOT be cached.
- One separate native-process preflight performs only a real GET `/warm` before
  the formal 15 cases; it must succeed and is not included in their count or
  connection pools. Fixture-only HTTP/connect/socket/process deadlines allow
  slower CI hosts without changing SDK defaults or adding test-owned retries.
  A timed-out native process preserves partial logs and server counters, stops
  subsequent cases and fails the exact-count gate before owned-device cleanup.
- The existing `native-overlay-package` job runs all 14 Node consumer cases
  against App commit `326854b4ba1997e06e713f12b6604f0a0f6fe165`. Only three real
  TypeScript source files are loaded from that read-only checkout. Existing
  modules Jest/TypeScript tools and an integrity-locked `ipaddr.js` 2.4.0
  archive avoid a full App dependency installation. Electron host services are
  mocked; HTTPS, sockets, destination validation and shared helpers remain real.
- Each replay runner requires its exact nonzero passing case count and records
  machine-readable results, source provenance and elapsed time. Node pending,
  skipped and todo tests fail acceptance. Each native case requires the exact
  locked-library complete-request/header counts and warmed-pool reuse evidence.
  These CI hosts do not establish React Native bridge or device acceptance.
