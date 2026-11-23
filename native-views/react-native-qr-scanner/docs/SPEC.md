# react-native-qr-scanner SPEC

Status: **Proposed**. The `/ur` entry is the first part being implemented; every
other section describes intended behavior that has no implementation yet. Each
section labels its own status. Nothing here is runtime verified.

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
for bytewords and fountain decoding, so assembly matches production behavior.
That library needs a global `Buffer`, and its `assert` dependency reads
`process` when loaded; OneKey runtimes provide both (a plain browser page
needs polyfills). Replacing it with a dependency-free decoder is a possible
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

- Frame text is trimmed; UR parts are compared lower-cased.
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
  first hit; within a pass the platform engine runs before ZXing-C++:
  1. `original`
  2. `padded`: white border of 10% of the longer side (at least 16 px), for
     codes drawn edge to edge with no quiet zone.
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
  limits, errors and pass order. Orientation comes from
  `createImageBitmap(blob, { imageOrientation: "from-image" })`; the passes
  are pure functions on RGBA buffers (`src/web/imageOps.ts`); engines are a
  QR-capable native `BarcodeDetector` first, then zxing-wasm when configured.

### 3.3 Live view — iOS, Android and Web implemented

`QrScanner` (React component) props: `active`, `mode`, `allowPlainText`,
`limits`, `sessionKey`, `style`, and callbacks `onReady`, `onProgress`,
`onResult`, `onError`. `QrScannerView` is the raw native view with `active`
and `onReady(generation)`, `onFrameText(text, generation)`,
`onError(code, message, generation)`.

- Native code reports raw text only. `QrScanner` feeds it into a
  `createScanSession`; a new `mode`, `allowPlainText` or `sessionKey` starts a
  new session, and `active=false` keeps the current one.
- `onResult` fires once per session. `onProgress(1)` precedes a UR result.
- `onError` receives camera errors as `QrScannerError` and terminal UR errors
  (`too-large`, `invalid-payload`, `resource-limit`) as `ScanError`.
- `onReady` (iOS) fires when the capture session reports running, which can be
  slightly before the first frame is on screen.
- Camera error codes: `no-camera` (checked before any permission prompt),
  `permission-denied`, `camera-busy`, `decoder-unavailable`.
- iOS requests camera access when it is undetermined. Apps must declare
  `NSCameraUsageDescription`.
- Android: CameraX 1.6.0 `PreviewView` (`FILL_CENTER`) plus `ImageAnalysis`
  (`KEEP_ONLY_LATEST`, 1280x720 target) with bundled ML Kit, through
  `QrFrameTextGate.kt` (same rules as iOS). The view owns a `LifecycleOwner`
  that is RESUMED only while `active` and the host activity is resumed, so
  the camera pauses in the background. Missing hardware (`no-camera`) is
  checked before permission; permission is requested through React Native's
  `PermissionAwareActivity`. The library manifest declares `CAMERA` and
  `android.hardware.camera.any` (not required).

Web (`./web`):

```ts
configureWebQrScanner({ loadZXingWasm?, disableNativeDetector? }): void;
createWebQrScanner({
  video: HTMLVideoElement;
  source?: { kind: "camera"; deviceId? } | { kind: "element" };
  mode; allowPlainText?; limits?; maxAnalysisSize?; // default 1280
  onReady?; onProgress?; onResult?; onError?;
}): { start(): Promise<void>; stop(): void; resetSession(): void; dispose(): void };
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
  → `decoder-unavailable`. Results from a stopped generation are dropped.
- Detection runs on the page's thread today; moving zxing into a Worker is
  pending (section 9).

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
  capture and removes observers.
- Image decoding (iOS): one decode at a time on a serial queue; each decode
  holds up to a few full-size bitmaps for its passes.

## 5. Data, cache and identity

No persistence. Dedupe identity of a fountain part is its sequence number under
the bound message; of any other frame, its normalized text. Session state is
freed on terminal status plus `reset()`, or when the session is dropped.

## 6. Platform contract

| Platform | Live engine | Image engine | Fallback | Status |
| --- | --- | --- | --- | --- |
| iOS | AVFoundation metadata output, 1080p, continuous near autofocus | Vision | ZXing-C++ (image only) | Implemented in source |
| Android | CameraX + bundled ML Kit (no Play Services) | Same ML Kit detector | ZXing-C++ (image only; arm ABIs) | Implemented, emulator verified (image) |
| Web / extension / Linux Electron | `BarcodeDetector` when it supports `qr_code` | Same | `zxing-wasm` reader (page thread; Worker pending) | Implemented, browser verified |
| macOS Electron | `desktop/mac/electron-qr-scanner` (AVFoundation + Vision) | Same | ZXing-C++ pending | Implemented, not integrated |
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
and sizes disagree is `malformed` and dropped. Exceeding a declared-size limit
ends the session with `too-large`; exceeding a retention limit ends it with
`resource-limit`; a completed message failing the CBOR walk ends it with
`invalid-payload`.

Frame text, pixels and CBOR never go to logs or metrics.

## 8. Performance and resource budget

`./ur` per-frame cost is bounded by the part-count and retained-byte limits
above. No camera or decoding work runs on the UI thread (Proposed).

## 9. Conformance and acceptance

| Contract | Source | Check |
| --- | --- | --- |
| UR assembly, limits, terminal states | `src/ur/assembler.ts`, `src/ur/limits.ts` | `src/ur/__tests__/assembler.test.ts`: single part, out-of-order and upper-case parts, duplicates, fountain redundancy, foreign message, bad CRC, forged header, too-large, resource-limit, unbounded CBOR, reset |
| Scan modes | `src/ur/session.ts` | `src/ur/__tests__/session.test.ts`: static/auto/ur, `allowPlainText`, foreign text while bound, terminal after rejection |

| Native error codes to `QrScannerError` | `src/errors.ts` | `src/__tests__/errors.test.ts` |
| iOS image passes and Vision engine | `ios/QrImageDecodingPipeline.swift` | Compiled and run on macOS (Vision only, no ZXing) against generated BTCPay-style lightning invoice QR images (`lightning:` + upper-case BOLT11, version 13, no margin, center icon, dark page): clean, 3x, and edge-to-edge images hit on `original`; a 2x image with an oversized center icon, which plain Vision and CIDetector both miss, hits on `padded`. A missing file rejects with `invalid-image`. Source evidence only, not a device run. |

| iOS image pipeline at runtime | `ios/QrImageDecodingPipeline.swift`, `ios/HybridQrImageDecoder.swift`, `ios/QrZXingReader.mm` | `QrScanner-Unit-Tests` (`ios/tests/`, fixtures in `ios/tests/fixtures/`), 9 tests passing on the iOS 27.2 simulator (iPad Air 13-inch M4). Vision + ZXing decode the real expired BTCPay invoice payload (461 chars) from clean, BTCPay-dark (no QR margin, page padding, center icon), inverted and EXIF-rotated fixtures; ZXing alone decodes clean and inverted codes, proving it is linked; no-QR image resolves `nil`; corrupt and missing files throw `invalid-image`; fake engines prove pass order, stop-at-first-hit, platform-before-fallback and the `maxPixels` downscale. Also in the CI `services` group (`scripts/ios-native-plan.mjs`). |
| Live frame gate (animated UR) | `ios/QrFrameTextGate.swift` | `ios/tests/QrFrameTextGateTests.swift`, 6 XCTests on the iOS 27.2 simulator: nothing while stopped, repeat throttling with new text passing at once, new generation on stop/restart, a 12-part animated UR at 10 fps / 30 fps camera loses no part, concurrent start/stop stays consistent. |
| Live view JS session wiring | `src/QrScanner.tsx` | `src/__tests__/QrScanner.test.tsx` (jest, mocked native host): a looping 40-frame animated `crypto-psbt` stream completes once with progress < 1 until done; pause keeps parts and drops stale generations; `sessionKey` resets; other codes ignored mid-assembly; static text once; UR limit and camera error mapping. |
| JS → Nitro → native on device runtime | example app `pages/QrScannerTestPage.tsx` | iOS 27.2 simulator (iPad Air 13-inch M4), example app Debug build: the native `QrScanner` view mounts; "Pick from album and decode" (image-crop-picker → `decodeQrImage`) returns the full BTCPay `lightning:LNBC…` invoice in 137 ms; "Start camera" reports `no-camera` with no permission prompt. |
| Web pixel passes and engine selection | `src/web/imageOps.ts`, `src/web/engines.ts` | `src/web/__tests__/` (jest, 9 tests): budget fit, padding, grayscale/invert, pass order and stop-at-first-hit, no zxing without a loader, loader used once, native preferred only with `qr_code`, `disableNativeDetector`. |
| Android gate and pipeline | `android/src/main/java/.../QrFrameTextGate.kt`, `QrImageDecodingPipeline.kt` | `android/src/test/` Robolectric (SDK 34, native graphics), 11 tests: gate rules incl. 10 fps animated UR, pass order, platform-before-fallback, padding/grayscale/invert, downscale from a PNG file, `invalid-image` for corrupt and missing files, accepted URI forms. In CI `android-unit`. |
| Android on device runtime | example app, Android 15 emulator (Pixel 7, arm64) | `decodeQrImage` through JS → Nitro → Kotlin: BTCPay lightning fixture → full invoice (12-41 ms), EXIF-rotated JPEG → invoice (13 ms), no-QR → `null`, corrupt and missing → `invalid-image`. Live view: camera permission prompt via `PermissionAwareActivity`, CameraX binds camera 1, preview surface attached, `onReady` fires; stop releases the camera. Preview content was black (headless emulated camera), so live detection is not verified. |
| Web backend in a browser | `src/web/` | Chrome 156 (built-in browser), esbuild bundle of `./web` with Buffer/process shims, served locally. Native `BarcodeDetector`: BTCPay lightning fixtures → exact payload (14-159 ms), no-QR → `null`, corrupt → `invalid-image`; live scanner on a `<video>` of the animated `crypto-psbt` UR (2,003 bytes) assembles with matching FNV-1a in 2.8 s. Forced zxing-wasm: same image results (16-61 ms), animated UR in 3.6 s; WASM fetched only through `loadZXingWasm`, no CDN request. `getUserMedia` camera not exercised. |
| iOS native build | `ios/`, `QrScanner.podspec` | `xcodebuild -scheme QrScanner` (iphonesimulator, Debug, Xcode 27.2) succeeds with zxing-cpp 2.2.0 and the two Podfile settings above, no warnings in package sources. Build evidence only. |

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
- Android: live preview content and live detection on a device (the
  headless emulator's emulated camera produced black frames), real-device
  permission flows, binary-size delta (ML Kit model, ZXing .so ~1.6 MB on
  arm64).
- Web: zxing in a Worker; `getUserMedia` camera run (permission, device
  switch, track release); Firefox and Safari runs; extension CSP and
  Linux Electron `file://` runs.
- The original OK-67798 album image once available.
