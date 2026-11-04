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

### Locked-library fixture observations

- OkHttp 4.12.0 can resend replayable requests after a lost response on a reused
  connection, including POST/file/empty bodies. Retryable 503 responses can be
  followed even when the test explicitly disables connection recovery. Default
  retryable 408 responses can also be followed. The tests distinguish fresh
  routes, a listener closed after processing, and an explicit retry-disabled
  control using complete server counts and OkHttp events.
- EMASCurl 1.5.5 in the local TLS fixture resends warmed empty POST, GET, DELETE,
  HEAD and OPTIONS requests after response loss. Nonempty/streamed bodies and
  fresh-route cases can fail after one fully processed request. The fixture's
  408/503 responses are returned directly. Tests assert each exact observed
  result without imposing those counts on another platform or future version.

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
