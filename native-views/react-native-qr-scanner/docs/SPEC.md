# react-native-qr-scanner SPEC

Status: **In development**. Platform implementations and validation evidence
are listed in sections 6 and 9.

Design background, desktop texture path and rollout order live in app-monorepo
`docs/qr-scanner-sdk-design.md` ([OK-34497](https://onekeyhq.atlassian.net/browse/OK-34497)).

### Template exception

Initialized with `yarn create:view qr-scanner` on 2026-11-23 from app-modules
main `57fe2caeb`. The generated Nitro view, podspec, Android build files and
configs are kept. Planned additions beyond the template, each to be justified
when it lands:

- Extra package entries: `./web` (DOM backend, no React Native imports) and
  `./ur` (pure TypeScript, no DOM, React Native or native imports).
- A native ZXing-C++ dependency on iOS and Android for the fallback decoder.
- `babel.config.js` uses the builder-bob preset like the other packages. The
  template's `module-resolver` plugin is not installed in this repository, so
  jest could not run with the generated config.
- The Android `QrScannerPackage` registers `HybridQrScannerManager` and loads
  the native library, like the other Nitro views. The template returned no
  view managers, so the view could not mount on Android.
- Android `CMakeLists.txt` uses Nitrogen's `qrscanner+autolinking.cmake` and
  `cpp-adapter.cpp` calls `margelo::nitro::qrscanner::initialize`, like the
  other Nitro views. The template compiled a hardcoded
  `../node_modules/react-native-nitro-modules/...` source that does not exist
  once nitro-modules is hoisted, and only initialized the Nitro core.
- A second Nitro HybridObject, `QrImageDecoder`, sits beside the view in
  `nitro.json`.
- The `react-native` devDependency uses the repository's patched build
  (`patch:react-native@npm%3A0.88.0#…`) like the other packages. The
  template's plain `0.88.0` made Yarn install a private copy of
  `react-native` and `react-native-nitro-modules` inside this package, so the
  view registered with one React Native and rendered with another ("View
  config getter callback for component `QrScanner` must be a function").

### Dependencies

`./ur` uses `@ngraveio/bc-ur` `1.1.13` (pinned; the version app-monorepo ships)
for Bytewords, fountain index selection and CRC32. The SDK solves fountain
equations in bounded storage and preserves the vendor encoder’s wire format.
That library needs a global `Buffer`, and its `assert` dependency reads
`process` when loaded; OneKey runtimes provide both (a plain browser page
needs polyfills). The React Native example installs Buffer before loading its
app. Replacing it with a dependency-free decoder is a possible
follow-up, not a goal here.

`./web` uses `zxing-wasm` `3.1.3` (the version app-monorepo ships) as its
fallback reader. The SDK never downloads the WASM binary: callers pass
`configureWebQrScanner({ loadZXingWasm })` (the app keeps its own bundled,
CSP- and `file://`-aware loader). Without a loader, zxing is unavailable and
only a QR-capable native `BarcodeDetector` is used.

iOS depends on the CocoaPods `zxing-cpp` pod `2.2.0` (Apache-2.0, default
`Wrapper` subspec) for the image fallback. Its `Core` headers are private to
the pod, so the package calls the Objective-C `ZXIBarcodeReader` from
`ios/QrZXingReader.mm`; that file's header exposes CoreGraphics types only.
The pod compiles every ZXing format plus the writer; the binary-size cost is
not measured yet (see section 9).

**Integration requirement.** `zxing-cpp` defines no module, and CocoaPods
refuses to integrate a Swift pod whose dependency has none when building
static libraries. The app Podfile must contain:

```ruby
pod 'zxing-cpp', '2.2.0', :modular_headers => true
```

This matches how app-monorepo already integrates `SDWebImage` and `EMASCurl`.

The zxing-cpp target also needs header maps off. zxing includes its own
headers by bare name (`#include "Point.h"`), and the Pods project's shared
header map resolves that name to React Native's
`react/renderer/graphics/Point.h`, so `QRVersion.cpp` fails to compile. In the
app Podfile's `post_install`:

```ruby
build_config.build_settings['USE_HEADERMAP'] = 'NO' if target.name == 'zxing-cpp'
```

Both requirements come from consuming the trunk pod. Vendoring the QR reader
sources into this pod would let the package set its own build settings, drop
both Podfile lines and the unused formats; it is a candidate follow-up.

## 1. Purpose, scope and non-goals

The package owns camera QR capture, QR decoding from live frames and from a
single local image, and assembly of BC-UR multi-part codes into one result.

Callers: the OneKey scan page and DeviceStage (app-monorepo `packages/kit`).
Platforms: iOS, Android, Web/extension/Linux Electron (`./web`). Windows and
macOS Electron use separate N-API packages under `desktop/` that share `./ur`.

Non-goals:

- Business parsing (addresses, payment URIs, WalletConnect, deeplinks), chain
  registries, UR registry types and transaction validation. These stay in the
  app and `qr-wallet-sdk`.
- Copy, translations, permission-settings navigation, blur overlays.
- Animated GIF/video import, non-QR symbologies, non-UR multi-part protocols.

## 2. Definitions and ownership

| Term | Meaning |
| --- | --- |
| Frame text | The string one decoder returns for one QR symbol. Untrusted. |
| UR part | Frame text matching `ur:<type>/<bytewords>` (single part) or `ur:<type>/<seq>-<len>/<bytewords>` (fountain part). Case-insensitive. |
| Message binding | `(type, seqLength, messageLength, checksum)` of the first accepted fountain part. Every later part must match it. |
| Scan session | One caller-visible attempt to get one result. Owns one assembler. |
| Capture generation | One camera start. Restarting the camera starts a new generation inside the same scan session. |

Ownership:

- The scan session (created on the caller's JS runtime, `main` on split-runtime
  targets) owns assembly state. Nothing is held in module-level singletons.
- The caller owns image picking and temporary-file cleanup.
- Native backends own the camera, frame buffers and decoder instances, and
  release them on `active=false` or `dispose()`.

## 3. Public API and defaults

### 3.1 `./ur` — Implemented (unit tested)

```ts
type ScanResult =
  | { kind: 'text'; text: string }
  | { kind: 'ur'; type: string; cbor: Uint8Array };

type ScanMode = 'static' | 'auto' | 'ur';

interface UrLimits {
  maxMessageBytes: number;      // default 1 MiB
  maxSeqLength: number;         // default 32_768
  maxCborDepth: number;         // default 64
  maxDistinctParts: number;     // default 65_536
  maxRetainedBytes: number;     // default 8 MiB
}

createUrAssembler(options?: { limits?: Partial<UrLimits> }): UrAssembler;
createScanSession(options: {
  mode: ScanMode;
  allowPlainText?: boolean;     // default false; only read in 'ur' mode
  limits?: Partial<UrLimits>;
}): ScanSession;
```

`UrAssembler.receive(frameText)` and `ScanSession.receive(frameText)` return one
of:

| Status | Meaning |
| --- | --- |
| `ignored` with `reason` `not-ur`, `duplicate`, `malformed`, `foreign` or `finished` | The frame added nothing. The session keeps waiting. |
| `progress` with `progress` (0..1), `receivedParts`, `expectedParts` | The frame was a new contribution. |
| `complete` with `result: ScanResult` | Terminal. Emitted exactly once per session. |
| `rejected` with `error: { code, message }` | Terminal. `code` is `too-large`, `invalid-payload` or `resource-limit`. |

Rules:

- Frame text is trimmed; UR parts are compared lower-cased. A multipart
  sequence number is a positive unsigned 32-bit integer and must match its
  CBOR header.
- `static` mode returns any non-empty frame text as `text`, UR or not.
- `auto` mode returns non-UR text as `text` and assembles UR parts.
- `ur` mode ignores non-UR text (`not-ur`) unless `allowPlainText` is true.
- A single-part UR completes immediately when no fountain message is bound.
  After binding, a single-part UR is `foreign`.
- In `auto` and `ur` modes, non-UR text is `foreign` while a fountain message
  is bound, so another code in view cannot end a half-collected message.
- Progress reaches exactly 1 only on `complete`.
- After a terminal status, every `receive` returns `ignored`/`finished` until
  `reset()`. `reset()` drops all parts and the binding. `stats()` reports counts
  of accepted, duplicate, malformed and foreign frames for metrics; it never
  exposes frame contents.

### 3.2 Image decoding — iOS, Android and Web implemented

```ts
decodeQrImage(uri: string, options?: { maxPixels?: number }): Promise<string | null>;
DEFAULT_MAX_IMAGE_PIXELS = 12_000_000;
```

- `uri` is a `file://` URL or an absolute path. Anything else rejects with
  `invalid-image`.
- Resolves the raw text of the first QR found, or `null`.
- Rejects with `QrScannerError` whose `code` is `invalid-image` (unreadable,
  unsupported, `maxPixels` < 1), `resource-limit` (source above 200 MP) or
  `decoder-unavailable`.
- The image is oriented by EXIF and downscaled to at most `maxPixels`
  (capped at 50 MP natively). Passes run in this order, stopping at the
  first hit; within a pass the platform engine runs before ZXing-C++. An
  engine failure allows the next engine to try. If every engine attempt
  fails, decoding rejects with `decoder-unavailable`:
  1. `original`
  2. `padded`: white border of 10% of the longer side (at least 16 px), for
     codes drawn edge to edge with no quiet zone. The padded pass scales the
     image and border together to fit the pixel budget.
  3. `grayscale`: saturation 0, contrast 1.3.
  4. `inverted`: the grayscale image inverted, for light-on-dark codes.
- Native code resolves `{ text?, engine?, pass? }`; the JS entry returns the
  text only. Engine and pass exist for diagnostics and tests.
- Android: same budget, passes and errors. Bitmaps are decoded with a
  power-of-two `inSampleSize`, scaled to the budget and oriented by
  ExifInterface; passes use Canvas and `ColorMatrix`. Engines: bundled ML Kit
  (`barcode-scanning` 17.3.0, no Play Services), then ZXing-C++
  (`io.github.zxing-cpp:android` 2.2.0). The ZXing AAR ships arm64-v8a and
  armeabi-v7a only; on other ABIs its library does not load and ML Kit is
  used alone. `content://` URIs are not accepted (`invalid-image`).
- Web (`./web`): `decodeQrImage(blob: Blob, options?)` with the same budget,
  limits, errors and pass order. The Web entry preflights raster headers
  for JPEG, PNG, WebP, GIF and BMP before creating any bitmap. Invalid or
  unsupported headers reject with `invalid-image`; sources above 200 MP
  reject with `resource-limit`. Header traversal reads only required ranges,
  with up to 512 metadata segments or chunks. PNG metadata traversal stops
  at the first IDAT. EXIF orientation informs resize dimensions. When sizing
  a rotated PNG or WebP, a cached small bitmap probe accounts for whether
  the browser applies that format’s orientation. The bitmap request uses a budgeted output
  size; internal decode allocations remain browser-dependent. The bitmap
  uses `imageOrientation: "from-image"`; the passes
  are pure functions on RGBA buffers (`src/web/imageOps.ts`); engines are a
  QR-capable native `BarcodeDetector` first, then zxing-wasm when configured.

### 3.3 Live view — iOS, Android and Web implemented

`QrScanner` (React component) props: `active`, `mode`, `allowPlainText`,
`limits`, `sessionKey`, `style`, and callbacks `onReady`, `onProgress`,
`onResult`, `onError`. `QrScannerView` is the raw native view with `active`
and `onReady(generation, cameraId)`, `onFrameText(text, generation)`,
`onError(code, message, generation)`.

- Native code reports raw text only. `QrScanner` feeds it into a
  `createScanSession`; a new `mode`, `allowPlainText` or `sessionKey` starts a
  new session, and `active=false` keeps the current one.
- `onResult` fires once per session. `onProgress(1)` precedes a UR result.
- `onError` receives camera errors as `QrScannerError` and terminal UR errors
  (`too-large`, `invalid-payload`, `resource-limit`) as `ScanError`.
- Native readiness reports the ID of the camera bound to that generation,
  including when the requested ID was unavailable and a default opened. The ID
  uses the same format as `listCameras()`. `QrScanner.onReady(cameraId)`
  forwards that ID after its generation check. A queued readiness callback
  remains valid across scan session resets while `active` and `cameraId`
  stay the same. On iOS readiness fires when the session reports running,
  which can precede the first frame on screen. The native ID is not part of the
  Web readiness callback. Hardware verification of the reported IDs for
  startup, switching and disconnect fallback is pending.
- The iOS built-in camera preview follows its window scene's interface
  orientation on attachment, after a
  camera input change or a rollback that restores the previous input, and
  whenever the scene changes orientation. Unknown scene orientations leave
  built-in previews as they are.
  This includes a 180° rotation and fixed-size previews whose bounds stay
  the same. iOS 16 and later observe effective scene geometry. On iOS 15,
  the attached, running preview checks the current scene orientation every
  100 ms, including after UIKit commits a rotation without laying out the
  scanner. The check stops when capture stops or the view leaves its window.
  On iOS 27 and later with the iOS 27 SDK, AVFoundation derives the rotation
  angle from the connected built-in camera and the scene orientation. Older
  SDKs or systems use AVFoundation's typed video orientation for built-in
  cameras. An external camera uses its native orientation (0°).
  Rotating the preview keeps the current capture generation, decoder,
  and scan session.
- Camera error codes: `no-camera` (checked before any permission prompt),
  `permission-denied`, `camera-busy`, `decoder-unavailable`.
- iOS requests camera access when it is undetermined. Apps must declare
  `NSCameraUsageDescription`.
- Android: CameraX 1.6.0 `PreviewView` (`FILL_CENTER`) plus `ImageAnalysis`
  (`KEEP_ONLY_LATEST`, 1280x720 target) with bundled ML Kit, through
  `QrFrameTextGate.kt` (same rules as iOS). The view owns a `LifecycleOwner`
  that is RESUMED only while `active` and the host activity is resumed, so
  the camera pauses in the background. Missing hardware (`no-camera`) is
  checked before permission. Overlapping scanner views share a request per
  activity. Activity Result Registry hosts use a dedicated result callback
  that is independent of React Native's permission listener and modal focus.
  An empty Android permission result means the prompt was cancelled, for
  example when a different permission prompt was already in flight. The
  scanner keeps one shared retry pending while views are waiting and retries
  when the host is resumed and its activity or scanner window has focus.
  After five minutes of repeated or blocked
  retries it reports `camera-busy` without recording a denial.
  Legacy `PermissionAwareActivity` hosts retain a lost-callback deadline
  after 60 s with window focus on Activity hosts and RESUMED state where
  available, with a five-minute backstop. At expiry the
  scanner rechecks the grant and releases the shared request. Deactivation
  releases the view's registration, and host destruction clears the request.
  Live decoder failures report `decoder-unavailable` at most once per
  second per binding; a failed frame is closed and analysis can continue.
  The library manifest declares `CAMERA` and
  optional camera, autofocus and camera.any features. Android readiness
  follows the first streaming preview state and capture errors are reported
  for the active binding. Stopping unbinds only this view’s use cases.
- Camera choice (Implemented): `listCameras()` returns `QrCameraInfo`
  (`id`, `position: back | front | external`, `label`): one back camera
  (on iOS the virtual device), one front camera, then external cameras
  (iPadOS 17+ `.external`; Android `LENS_FACING_EXTERNAL`), deduplicated.
  `subscribeCameraChanges` reports connects and disconnects (iOS
  `wasConnected`/`wasDisconnected`; Android CameraX presence events after
  its catalog updates). Each subscription has independent cleanup. The `cameraId` prop picks the camera; unset or
  disconnected uses the back camera. Changing it while active swaps the
  input (iOS, inside one configuration, without stopping the session) or
  rebinds CameraX (Android) in a new capture generation with a new
  `onReady`; the JS scan session, and so collected UR parts, stays. iOS
  falls back to the default when the camera in use is unplugged. The front
  preview is mirrored by the platform; detection reads unmirrored frames.
- Camera and gestures (Implemented; matches the camera-kit 17 behavior the
  app had before):
  - iOS picks the back virtual device first (`builtInTripleCamera`, then
    `builtInDualWideCamera`, `builtInDualCamera`, `builtInWideAngleCamera`)
    and starts at the wide lens's zoom factor (1x). Virtual devices move to
    the ultra-wide lens by themselves when a code is closer than the wide lens
    can focus. Rules live in `ios/QrCameraControl.swift`.
  - Tap to focus: focus and exposure move to the tapped point and a 73 pt /
    73 dp yellow focus box shrinks from 2x to 1x over 200 ms, holds for 1 s,
    and fades out over 200 ms. iOS uses full-range continuous autofocus and
    returns to continuous focus over the whole frame when the subject area
    changes; Android checks `FocusMeteringAction` (AF + AE) support before
    showing the indicator or starting focus and metering with a 5 s
    auto-cancel, and hides the indicator on unbind.
  - Pinch to zoom from the current zoom, clamped to the device range and at
    most 8x the 1x factor (`QrCameraControl.clampZoom`,
    `QrCameraZoom.clampRatio`). A pinch applies only to the running capture
    where it began. Android uses two-finger scaling and independent focus taps.
  - The view consumes touches on its area, as camera-kit did.

Web (`./web`):

```ts
configureWebQrScanner({ loadZXingWasm?, disableNativeDetector? }): void;
createWebQrScanner({
  video: HTMLVideoElement;
  source?: { kind: "camera"; deviceId? } | { kind: "element" };
  mode; allowPlainText?; limits?; maxAnalysisSize?; // default 1280
  firstFrameTimeoutMs?; // default 8000
  onReady?; onFrameText?; onProgress?; onResult?; onError?;
}): {
  start(): Promise<void>;
  switchCamera(deviceId?: string): Promise<void>;
  focus(point?: { x: number; y: number }): Promise<boolean>;
  stop(): void;
  resetSession(): void;
  dispose(): void;
};
```

- `camera` calls `getUserMedia({ audio: false, video: { facingMode:
  "environment", 1280x720 ideal } })` and owns the stream; `element` analyses
  a video the caller already plays (a file, a stream it owns).
- One frame in analysis at a time, driven by `requestVideoFrameCallback`
  (15 fps timer fallback); frames arriving meanwhile are skipped. Frames are
  downscaled to `maxAnalysisSize` on the longer side.
- The native detector runs on every frame; zxing runs on every 4th frame the
  native detector missed, or on every frame when there is no native detector.
- Errors: `NotAllowedError`/`SecurityError` → `permission-denied`,
  `NotFoundError`/`OverconstrainedError` → `no-camera`,
  `NotReadableError`/`AbortError` → `camera-busy`, others and engine failures
  → `decoder-unavailable`. Without an available native detector or a WASM
  loader, startup reports `decoder-unavailable` before camera access. Results
  from a stopped generation are dropped.
- zxing runs on the page thread by default. With
  `configureWebQrScanner({ createZXingWorker })` it runs in a Worker built
  from the `./web/zxing-worker` entry: one worker, requests answered in order,
  pixels copied before transfer (the same image feeds several passes), and a
  crashed worker is replaced on the next request (`decoder-unavailable` for
  the failed one). Worker initialization (including the loader) and each
  decode have a 10 s deadline. Expiry terminates the worker and rejects
  outstanding requests. The callers create the Worker because worker URLs are
  bundler-specific.
- `onFrameText(text)` reports raw decoded text before assembly. Repeats use
  a monotonic 1 s interval, and a different text passes immediately. If
  `onFrameText` is supplied by itself, the caller owns assembly. Supply
  `onResult` or `onProgress` alongside it to also run the built-in session.
  Exceptions from the raw handler do not interrupt assembly. Session reset
  drops any in-flight decode from the previous session.
- `listWebCameras()` returns device IDs and labels (or “Camera N” before
  permission) and infers position from the label. `subscribeWebCameraChanges()`
  listens to `devicechange`. `switchCamera(deviceId?)` stages a replacement
  until it produces a frame. It retains the running stream on failure when
  the cameras can coexist. On a busy error, the current request can release
  the stream it was replacing and retry once, including when that stream
  already ended. Recovery retains the last committed camera when releasing
  an ended stream for this retry. While stopped, selection is saved for
  `start()`. A newer selection supersedes pending acquisition, including a
  return to the current camera. If a stale request ends the working track,
  the scanner releases the stale stream. If the newest switch settles
  without replacing the ended track, the scanner attempts to restore the
  last committed camera. The target survives an interrupted restoration so
  it can be used if a newer switch fails. If opening the camera during
  restoration reports `camera-busy` while superseded acquisitions could
  still be holding the hardware, each stale acquisition that settles and
  releases its stream can trigger a retry. A restoration first-frame
  timeout releases the candidate and reports `camera-busy` through
  `onError` immediately. A successful handoff, terminal restoration error,
  stop, or disposal clears pending recovery. Each camera-change subscription
  has independent cleanup. On startup, a missing remembered ID falls back
  to the default. Cancellation prevents later permission requests and
  disposes streams that arrive late.
- A camera whose track delivers no frame within `firstFrameTimeoutMs`
  (default 8000) fails `start()` with `camera-busy` and is released, instead
  of waiting forever on `video.play()`.
- `focus(point?)` triggers tap-to-focus on the active scanner-owned camera
  track (`source.kind === "camera"`) when `getCapabilities().focusMode`
  includes `"single-shot"` or `"continuous"`. `point` uses normalized `[0, 1]`
  preview coordinates (clamped when finite) passed via `pointsOfInterest`.
  Existing non-focus track constraints from `getConstraints()` are preserved.
  If `pointsOfInterest` is rejected or `getSettings().focusMode` reports that
  the mode did not take effect, the scanner falls back to a mode-only
  constraint and resolves `false` when the mode still does not apply or the
  capture generation changes while in flight. When the track supports both
  `"single-shot"` and `"continuous"`, the latest successful tap schedules a
  5 s restore to `"continuous"`.

## 4. Lifecycle and concurrency

- `./ur` is synchronous and single-owner. Each call to `receive` runs to
  completion; there is no shared state between sessions. — Implemented
- Live view (iOS, implemented): props apply in `afterUpdate` on a serial
  session queue. `ios/QrFrameTextGate.swift` owns which detections reach JS:
  every start and stop increments the capture generation, nothing is
  reported while stopped, and the same text is re-emitted at most once per
  second per generation while new text (each part of an animated code) passes
  immediately. JS drops any event older than the newest generation it has
  seen. Generations only grow for the lifetime of a view. `onDropView` stops
  capture and removes observers. Explicit `dispose()` performs the same
  terminal cleanup. Failed iOS configuration restores the prior input,
  output and preset. Native frames carry the generation that produced them.
  React callbacks queued before a capture selection change, deactivation,
  or unmount are discarded. Frames and errors also expire when the React
  scan session is replaced.
- Image decoding (iOS): one decode at a time on a serial queue; each decode
  holds up to a few full-size bitmaps for its passes.

## 5. Data, cache and identity

No persistence. Dedupe identity of a fountain part is its sequence number under
the bound message; of any other frame, its normalized text. Session state is
freed on terminal status plus `reset()`, or when the session is dropped.

## 6. Platform contract

| Platform | Live engine | Image engine | Fallback | Status |
| --- | --- | --- | --- | --- |
| iOS | AVFoundation metadata output, 1080p, back virtual device at 1x, continuous autofocus, tap to focus, pinch zoom | Vision | ZXing-C++ (image only) | Implemented in source |
| Android | CameraX + bundled ML Kit (no Play Services) | Same ML Kit detector | ZXing-C++ (image only; arm ABIs) | Implemented, emulator verified (image) |
| Web / extension / Linux Electron | `BarcodeDetector` when it supports `qr_code` | Same | `zxing-wasm` reader (page thread or Worker) | Implemented; Chrome, WebKit and extension-CSP verified |
| macOS Electron | `desktop/mac/electron-qr-scanner` (AVFoundation + Vision) | Same | ZXing-C++ (image) | Implemented, not integrated |
| Windows Electron | `desktop/win/electron-qr-scanner` | Same | ZXing-C++ | Not started |

All platforms feed frame text into the same `./ur` session.

Known divergence (pending): iOS live scanning has no ZXing fallback yet. The
design calls for a bounded-rate ZXing pass when the platform engine finds
nothing for a while; its rate needs device measurements first.

## 7. Failure, fallback and safety

Hard limits (`./ur`, Implemented): message ≤ 1 MiB, declared parts ≤ 32,768,
CBOR nesting ≤ 64, distinct accepted parts ≤ 65,536, retained fragment bytes ≤
8 MiB. The first five are configurable per session. One fountain frame's own
CBOR is capped at 16 KiB (fixed; a QR symbol holds under 3 KB). CBOR of the completed message must use definite lengths that fit the
bytes present, with no trailing bytes. A part whose text header, CBOR header
and sizes disagree is `malformed` and dropped. Once bound, a foreign frame is ignored even if it exceeds the declared-size
limits. Exceeding a declared-size limit on an eligible frame ends the session
with `too-large`. The decoder also bounds mixed-frame work and retained
fragment and index storage. Exceeding those budgets ends the session with
`resource-limit`. A failed message checksum or CBOR walk ends it with
`invalid-payload`.

Frame text, pixels and CBOR never go to logs or metrics.

## 8. Performance and resource budget

`./ur` bounds synchronous fountain work per admitted frame. The fixed
2 million operation budget covers vendor index selection, sparse-to-packed
equation reduction, and final back-substitution. Each independent equation
occupies one pivot slot; dependent frames do not add rows. The per-session
retention budget counts the encoded bytes in live fragments. Packed indexes,
pivot storage and word-alignment overhead share a separate internal 8 MiB limit. Distinct sequence tracking remains
bounded separately. Camera analysis and native image decoding run off the
UI thread.

Acceptance covers the full original byte sequence of a 100-part message
when only mixed frames are seen, and a 201-part, 60 KB message when the
camera sees one original frame followed by mixed frames.

## 9. Conformance and acceptance

| Contract | Source | Check |
| --- | --- | --- |
| UR assembly, limits, terminal states | `src/ur/assembler.ts`, `src/ur/limits.ts` | `src/ur/__tests__/assembler.test.ts`: single part, out-of-order and upper-case parts, duplicates, fountain redundancy, foreign message, bad CRC, forged header, too-large, resource-limit, unbounded CBOR, reset |
| Scan modes | `src/ur/session.ts` | `src/ur/__tests__/session.test.ts`: static/auto/ur, `allowPlainText`, foreign text while bound, terminal after rejection |

| Native error codes to `QrScannerError` | `src/errors.ts` | `src/__tests__/errors.test.ts` |
| iOS image passes and Vision engine | `ios/QrImageDecodingPipeline.swift` | Compiled and run on macOS (Vision only, no ZXing) against generated BTCPay-style lightning invoice QR images (`lightning:` + upper-case BOLT11, version 13, no margin, center icon, dark page): clean, 3x, and edge-to-edge images hit on `original`; a 2x image with an oversized center icon, which plain Vision and CIDetector both miss, hits on `padded`. A missing file rejects with `invalid-image`. Source evidence only, not a device run. |

| iOS image pipeline at runtime | `ios/QrImageDecodingPipeline.swift`, `ios/HybridQrImageDecoder.swift`, `ios/QrZXingReader.mm` | `QrScanner-Unit-Tests` (`ios/tests/`, fixtures in `ios/tests/fixtures/`), 9 tests passing on the iOS 27.2 simulator (iPad Air 13-inch M4; 20 in the whole `QrScanner-Unit-Tests` target with the gate and camera-control suites). Vision + ZXing decode the real expired BTCPay invoice payload (461 chars) from clean, BTCPay-dark (no QR margin, page padding, center icon), inverted and EXIF-rotated fixtures; ZXing alone decodes clean and inverted codes, proving it is linked; no-QR image resolves `nil`; corrupt and missing files throw `invalid-image`; fake engines prove pass order, stop-at-first-hit, platform-before-fallback and the `maxPixels` downscale. Also in the CI `services` group (`scripts/ios-native-plan.mjs`). |
| Camera selection and zoom | `ios/QrCameraControl.swift`, `android/.../QrCameraZoom.kt` | `ios/tests/QrCameraControlTests.swift` (5 XCTests: device preference order, 1x factor for triple, dual-wide, wide-first and physical devices, clamp to device range and the 8x cap) and `QrCameraZoomTest.kt` (3 Robolectric tests). Android emulator (Pixel 7, API 35), example app: tapping the live preview logs CameraX `startFocusAndMetering: updating 3A regions & triggering AF` and draws the focus box at the tap point. Pinch is not exercised at runtime (no multi-touch injection); the iOS gestures need a device (simulators have no camera). |
| iOS preview orientation | `ios/QrScanner.swift` | `ios/tests/QrCameraControlTests.swift` covers the four interface orientations and rejects an unknown orientation. On iPadOS 18.6 and 27.2 simulators a fixed-size preview view read the updated scene orientation on entry to landscape left, a 180° flip to landscape right, and a return to portrait. Live camera pixels require device testing. |
| Camera list and switching | `src/cameras.ts`, `ios/HybridQrCameraList.swift`, `android/.../HybridQrCameraList.kt`, `QrCameraCatalog.kt`, `src/web/cameras.ts`, `src/web/scanner.ts` | jest: one native listener shared and cleared after the last unsubscribe; web label positions, unlabeled entries, `devicechange`; web switching opens the new camera before stopping the old, retries after release on `NotReadableError`, records the choice while stopped. XCTest: list dedupe. Robolectric `QrCameraCatalogTest` (3). Android emulator (API 35, emulated back and front cameras), example app: `listCameras` returns `[{id:"0",position:"back"},{id:"1",position:"front"}]`; switching while active closes camera 0 and opens camera 1 (`Opened CameraId-1 in 11 ms`), and back again (12.6 ms), one active client each time, `onReady` per switch. iOS switching needs a device. |
| Live frame gate (animated UR) | `ios/QrFrameTextGate.swift` | `ios/tests/QrFrameTextGateTests.swift`, 6 XCTests on the iOS 27.2 simulator: nothing while stopped, repeat throttling with new text passing at once, new generation on stop/restart, a 12-part animated UR at 10 fps / 30 fps camera loses no part, concurrent start/stop stays consistent. |
| Live view JS session wiring | `src/QrScanner.tsx` | `src/__tests__/QrScanner.test.tsx` (jest, mocked native host): a looping 40-frame animated `crypto-psbt` stream completes once with progress < 1 until done; pause keeps parts and drops stale generations; `sessionKey` resets assembly and drops queued frames while preserving current capture readiness; other codes ignored mid-assembly; static text once; UR limit and camera error mapping. |
| JS → Nitro → native on device runtime | example app `pages/QrScannerTestPage.tsx` | iOS 27.2 simulator (iPad Air 13-inch M4), example app Debug build: the native `QrScanner` view mounts; "Pick from album and decode" (image-crop-picker → `decodeQrImage`) returns the full BTCPay `lightning:LNBC…` invoice in 137 ms; "Start camera" reports `no-camera` with no permission prompt. |
| Web pixel passes and engine selection | `src/web/imageOps.ts`, `src/web/engines.ts` | `src/web/__tests__/` (jest, 9 tests): budget fit, padding, grayscale/invert, pass order and stop-at-first-hit, no zxing without a loader, loader used once, native preferred only with `qr_code`, `disableNativeDetector`. |
| Android gate and pipeline | `android/src/main/java/.../QrFrameTextGate.kt`, `QrImageDecodingPipeline.kt` | `android/src/test/` Robolectric (SDK 34, native graphics), 11 tests (14 with `QrCameraZoomTest`): gate rules incl. 10 fps animated UR, pass order, platform-before-fallback, padding/grayscale/invert, downscale from a PNG file, `invalid-image` for corrupt and missing files, accepted URI forms. In CI `android-unit`. |
| Android on device runtime | example app, Android 15 emulator (Pixel 7, arm64) | `decodeQrImage` through JS → Nitro → Kotlin: BTCPay lightning fixture → full invoice (12-41 ms), EXIF-rotated JPEG → invoice (13 ms), no-QR → `null`, corrupt and missing → `invalid-image`. Live view: camera permission prompt via `PermissionAwareActivity`, CameraX binds camera 1, preview surface attached, `onReady` fires; stop releases the camera. Preview content was black (headless emulated camera), so live detection is not verified. |
| Web backend in a browser | `src/web/` | Chrome 156 (built-in browser), esbuild bundle of `./web` with Buffer/process shims, served locally. Native `BarcodeDetector`: BTCPay lightning fixtures → exact payload (14-159 ms), no-QR → `null`, corrupt → `invalid-image`; live scanner on a `<video>` of the animated `crypto-psbt` UR (2,003 bytes) assembles with matching FNV-1a in 2.8 s. Forced zxing-wasm: same image results (16-61 ms), animated UR in 3.6 s; WASM fetched only through `loadZXingWasm`, no CDN request. `getUserMedia` camera not exercised. |
| Web worker, engines and camera start | `src/web/engines.ts`, `src/web/zxingWorkerProtocol.ts`, `src/web/scanner.ts` | jest: worker handler (no decode before init, init failure then retry), worker client (one worker, copy transferred, crashed worker replaced), camera start (no-frame timeout → `camera-busy` and release, `NotAllowedError`/`NotFoundError`/`NotReadableError` mapping, stream arriving after `stop()` is stopped), `onFrameText` repeat throttle. Regression tests cover cancellation, staged switches, stale frames on reset, and Worker initialization retries. |
| Web real camera | local test harness in Chrome 157, MacBook Pro camera, codes shown on an iPhone (reproduction below) | Camera ready in 498 ms at 1280x720 @ 30 fps; native BarcodeDetector; animated `crypto-psbt` (2,003 bytes) assembled in 2.86 s with matching FNV-1a; 211 static results (lightning invoice and `bitcoin:` URI) all matching their expected payloads (compared on the first 70 characters); track `ended` after stop. |
| Web in other engines and the extension CSP | harness via Playwright 1.58 | Chrome 156 Worker mode: images exact (5-26 ms), animated UR 2.4 s, one worker. WebKit 26.0 (Safari's engine; no BarcodeDetector, so zxing automatically): page thread and Worker, images exact, animated UR ~2.4 s. MV3 extension with OneKey's production CSP (`script-src 'self' 'wasm-unsafe-eval'; object-src 'self'`): page thread and Worker, images exact, animated UR 2.4-2.6 s, no CSP violations. Real camera: Chrome 157 sees "MacBook Pro Camera" and getUserMedia returns a live 640x480 track, but no frames reached the `<video>` when launched from this automation (OS capture permission of the launching process); the SDK now reports `camera-busy` and releases the camera in that case. The built-in browser pane blocks capture, which verified `permission-denied`. |
| iOS native build | `ios/`, `QrScanner.podspec` | `xcodebuild -scheme QrScanner` (iphonesimulator, Debug, Xcode 27.2) succeeds with zxing-cpp 2.2.0 and the two Podfile settings above, no warnings in package sources. Build evidence only. |

To reproduce the Web camera check, bundle a local entry with esbuild for a
browser. Install or inject `buffer` and `process` shims before importing
`src/web/index.ts`. Serve the bundle and a page containing
`<video autoplay playsinline muted></video>` over localhost. From a button
click, create a scanner with the video, `mode: 'auto'` and callbacks for ready,
result and errors, then call `start()` in a regular Chrome window with camera
permission. Restart the result session between static and animated scans.

- Present the QR from `ios/tests/fixtures/lightning-clean.png` on a separate
  screen and compare its exact text to `lightning-payload.txt` in the same
  directory. A second static check can use a synthetic `bitcoin:` URI.
- Play `desktop/mac/electron-qr-scanner/test/fixtures/animated-ur.mp4` on the
  other screen. Compare the assembled type, length and FNV-1a to the adjacent
  `animated-ur.json`. Record time from the first UR fragment.
- Before calling `stop()`, keep a reference to the active video track.
  Verify its `readyState` becomes `ended` after stopping and that the scanner
  delivers no further frames.

Known image-decoding gaps (iOS 27.2 simulator; recorded as non-strict
expected failures in `testStressFixturesAreKnownGaps`):

- A code drawn edge to edge on a dark page, with no light quiet zone at all
  (`lightning-no-margin.png`). The padded pass does not help because the dark
  page still touches the code. macOS Vision decodes it; iOS simulator Vision
  and ZXing do not. A crop-to-code or adaptive-threshold pass is a candidate.
- A center icon about 37% of the code width (`lightning-large-icon.png`),
  likely beyond error correction. BTCPay's real icon size (the
  `lightning-btcpay-dark.png` fixture) decodes.
- The `inverted` pass has not been needed by any fixture: Vision and ZXing
  both read light-on-dark codes on the `original` pass. It stays as a cheap
  last resort.
- Simulator Vision may differ from device Vision; device results are not
  recorded yet.

Still required:

- iOS: device run of live scanning with a real camera (simulators have
  none), including an animated UR played on a screen; completion time and
  parts per second still unmeasured. Binary-size delta of the `zxing-cpp`
  pod.
- iPad: verify front and rear live previews in portrait and both landscape
  orientations on an iPad with a landscape-mounted front camera. Check a
  fixed-size scanner during a 180° flip on iOS 15 where available. Verify
  switching in both directions between the built-in camera and an upright
  stationary USB camera, including a failed switch that restores the input.
- iOS and Android devices: tap to focus, pinch zoom, and (iPhone 13 Pro and
  later) the automatic switch to the ultra-wide lens for close codes.
- Android: live preview content and live detection on a device (the
  headless emulator's emulated camera produced black frames), real-device
  permission flows, binary-size delta (ML Kit model, ZXing .so ~1.6 MB on
  arm64).
- Web: real Safari.app (WebKit verified instead), Firefox, Linux Electron
  from `file://`; zxing-only live scanning with a real camera.
- The original OK-67798 album image once available.
