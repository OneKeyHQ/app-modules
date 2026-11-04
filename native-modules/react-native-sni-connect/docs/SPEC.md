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

## HTTP replay boundary

Status: **Android and iOS implemented and verified with real local TLS regressions**.
Android uses the production JVM transport; iOS uses the unpacked npm artifact's
Swift sources and dependency patched by its shipped helper in a native simulator
process. Physical-device and React Native bridge acceptance remain unverified. The consumer Node backend is tested
by loading its actual source through an explicit `--consumer-root`; this package
does not provide a Node transport implementation.

- One native or consumer Node request invocation MUST NOT automatically send a
  second HTTP request, regardless of method, body size, connection recovery, or
  a 408/503 response. Explicit retries belong to the caller.
- A lost response MUST be returned as a transport error. HTTP 408 and 503 MUST
  retain their original status, headers and body, including `Retry-After: 0`.
- Connection preparation is distinct from HTTP transmission. Normal connection
  pooling SHOULD remain available; establishing a connection is not itself a
  second HTTP request.

### Android implementation

- Connection recovery and 408 follow-ups are disabled on the production client.
- OkHttp 4.12.0 still follows a 503 response when its final effective
  `Retry-After` value is zero. The client uses private per-call state and public
  interceptors to prevent that internal follow-up, then restores the exact
  original header name/value in both the application and network responses.
- The wire response, status, body, duplicate header order and unrelated headers
  MUST remain unchanged. Transparent gzip decoding MUST retain its normal
  behavior; restoration MUST NOT replace the entire application header set.
- Request bodies retain their original streaming behavior. There is no
  method-specific one-shot wrapper or process-global response state.
- Android device and React Native bridge acceptance remain unverified. TLS/JVM
  regressions do not establish Android hostname verification, production IP/port
  admission, Promise serialization, or the consuming app's fallback policy.

### iOS dependency integration

- The package ships a controlled patch for EMASCurl 1.5.5. Its pre-request
  callback permits at most one HTTP send per protocol/easy-handle lifecycle,
  preserving normal connection reuse. It MUST NOT reset the permit on recovery.
  CocoaPods shares this dependency: the policy applies to every EMASCurlProtocol
  instance in the consumer process, including any other integration using it.
- A rejected second send MUST surface as a network failure with the original
  error retained, rather than a user cancellation. Genuine cancellation remains
  unchanged. Failure to install either required curl option MUST fail before
  enqueueing the request.
- Every consuming CocoaPods project MUST call `apply_emascurl_no_replay.rb` from
  `post_install`, passing the resolved EMASCurl version and installed pod root.
  The helper accepts only the pinned version and exact original/patched hashes;
  unknown source drift MUST fail without modifying a target. Reapplication is a
  no-op. The SDK build phase only verifies the patched source and MUST fail if
  the consumer omitted installation; it MUST NOT patch during a build.
- Package acceptance MUST unpack the npm artifact, run its shipped helper on a
  clean dependency copy, and compile that copy with the package's actual Swift
  sources. A separately injected test-only replay guard is not acceptance.
- Local fixtures may replace only the port, DNS destination and trusted CA,
  retaining certificate and hostname verification. Simulator acceptance does
  not establish React Native bridge behavior or physical-device acceptance.

For a consumer whose `ios/Podfile` sits beside `../node_modules`, integrate the
installed SDK helper in the existing `post_install` block (adjust only the
package location for other workspace layouts):

```ruby
require_relative '../node_modules/@onekeyfe/react-native-sni-connect/ios/apply_emascurl_no_replay'

post_install do |installer|
  target = installer.pod_targets.find { |pod| pod.pod_name == 'EMASCurl' }
  raise 'EMASCurl dependency is missing' unless target
  OneKeyEmascurlNoReplay.apply!(
    pod_root: installer.sandbox.root.join('EMASCurl').to_s,
    version: target.root_spec.version.to_s
  )
  # Keep the project's existing React Native post-install work here.
end
```

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
  responses, streamed/empty POST bodies, duplicate headers, gzip decoding and
  concurrent calls. The JVM fixture changes only local routing, certificate
  trust/hostname verification, test ports and deadlines; it does not prove the
  Android bridge or production destination admission.
- Native device/network upload acceptance remains separate from these unit tests.

### Automated replay conformance

- `native-tests` runs the Android TLS suite in the existing `android-unit` job.
- The existing `device-utils-store` job installs only EMASCurl 1.5.5 in a
  temporary CocoaPods project, then runs `tests/ios-replay/package_test.py
  --output-dir <temporary-directory>`. It packs the SDK once, executes all six
  packaged-helper checks and the real verify-only build phase, and compiles
  that same package and verified dependency for all 15 native TLS cases.
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
  slower CI hosts without changing SDK defaults or retrying a target request.
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
  skipped and todo tests fail acceptance. iOS requires exactly one complete
  request and one target-header event per case, including warmed-pool reuse.
  These CI hosts do not establish React Native bridge or device acceptance.
