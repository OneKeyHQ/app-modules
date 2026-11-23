# electron-qr-scanner (macOS) SPEC

Status: **In development, not released.** Image decoding, raw-text capture
and the preview frame path (IOSurface → Electron `sharedTexture` → canvas)
are implemented and tested on macOS arm64 (Node and Electron 44.5.1).
app-monorepo integration is pending. The module is not listed in `desktop/release.json`
and has no release workflow; adding it requires the locked build, test and
pack contract `desktop/SPEC.md` asks of every module.

Design background: app-monorepo `docs/qr-scanner-sdk-design.md` section 4.

## 1. Scope

- Decode one local image file to QR text.
- Capture camera frames (or a video file, for tests), detect QR codes with
  Vision and report raw text.
- Not in scope: BC-UR assembly and mode handling (the app feeds raw text into
  `@onekeyfe/react-native-qr-scanner/ur`), permission UI, preview rendering.

The npm package stays dependency-free, like the other desktop modules.

## 2. Structure and shared code

Rust (napi-rs 3, `src/lib.rs`) provides the N-API surface. Apple-framework
code is Swift compiled by `build.rs` into a static library behind a C ABI
(`swift/Bridge.swift`). Two Swift files come from the iOS package and are
compiled unchanged, so both platforms decode and filter identically:

- `native-views/react-native-qr-scanner/ios/QrImageDecodingPipeline.swift`
- `native-views/react-native-qr-scanner/ios/QrFrameTextGate.swift`

Minimum macOS 12. The Swift runtime comes from the OS (`/usr/lib/swift`).

ZXing-C++ (the image fallback) comes from the Rust `zxing-cpp` crate
`=0.5.3` with the `bundled` feature, which builds the C++ library with CMake
(CMake is required to build the module). Rust exports `oqs_zxing_decode` /
`oqs_zxing_free`; Swift reaches them through `swift/ZXingBridge.h`
(`-import-objc-header`) and wraps them as the `zxing` engine
(`swift/ZXingEngine.swift`), which renders each image into the packed RGBA
buffer the reader expects. The crate also compiles ZXing's writers (its build
script enables them); the module grew from 0.54 MB to 1.30 MB (arm64,
stripped).

## 3. API

```ts
decodeImage(path: string, options?: { maxPixels?: number }): Promise<string | null>;
createCapture(options?: {
  source?: { kind: "camera" } | { kind: "file"; path: string; loop?: boolean };
  onReady?(generation: number): void;
  onText?(text: string, generation: number): void;
  onError?(error: Error & { code: QrScannerErrorCode }, generation: number): void;
  onEnded?(generation: number): void;
  onFrame?(frame: { ioSurface: Buffer; width: number; height: number; generation: number; release(): void }): void;
}): { start(): void; stop(): void; lentFrames(): number; dispose(): void };
```

Preview (`onFrame`, opt-in): each frame is a BGRA, IOSurface-backed
`CVPixelBuffer` lent from a pool of **3 slots**. `ioSurface` is the 8-byte
IOSurfaceRef buffer for `sharedTexture.importSharedTexture({ textureInfo: {
pixelFormat: "bgra", codedSize, handle: { ioSurface } } })`. The consumer
calls `release()` when Electron reports `allReferencesReleased`. While all
slots are out, new preview frames are skipped (detection still runs). Frames
from an older generation or arriving after `dispose` are released by the JS
wrapper without being delivered. Preview is emitted before detection on the
analysis queue, so preview rate is bounded by detection time per frame.

- `decodeImage`: passes `original`, `padded`, `grayscale`, `inverted`, Vision
  then ZXing-C++ within each pass (same pipeline and order as iOS).
  `options.engines` (`["vision"]`, `["zxing"]`) limits the engines for
  diagnostics and tests; unknown or empty lists reject with
  `decoder-unavailable`. `maxPixels` default 12 MP, capped at 50 MP.
  Errors: `invalid-image`, `resource-limit`.
- `createCapture`: every `start`/`stop` begins a new generation; events from
  an older generation are dropped in JS. The same text repeats at most once
  per second; new text (each part of an animated code) passes at once.
  `dispose()` is idempotent and no callback runs after it returns.
- Camera: the default video device at 720p, BGRA, late frames discarded so
  at most one frame is in analysis. Missing hardware is reported as
  `no-camera` before any permission request. Error codes: `no-camera`,
  `permission-denied`, `camera-busy`, `decoder-unavailable`.

## 4. Threads and lifetime

A serial session queue starts and stops sources; a serial analysis queue runs
Vision. Events cross into JS through a napi thread-safe function. Swift guards
every callback with a lock that `destroy` clears synchronously, and Rust frees
the callback context only after `destroy` returns.

## 5. Conformance

| Contract | Check |
| --- | --- |
| ZXing-C++ fallback | `test/native.test.cjs`: with `engines: ["zxing"]` the clean, BTCPay, light-on-dark and EXIF-rotated fixtures decode to the exact payload and the no-QR image to `null`; Vision alone still decodes; unknown and empty engine lists reject. |
| Image decoding | `test/native.test.cjs`: BTCPay lightning fixtures decode to the exact payload; no-QR image resolves `null`; corrupt, missing, empty path and `maxPixels: 0` reject with `invalid-image`. |
| Animated UR through live frames | `test/native.test.cjs`: `test/fixtures/animated-ur.mp4` (2,003-byte `crypto-psbt`, 17 fragments, 51 frames at 8 fps, from `scripts/make-ur-fixture.mjs`) assembles to the expected type, size and FNV-1a in about 2.2 s. |
| Generations and dispose | `test/native.test.cjs`: stop/start yields a newer generation, dispose is idempotent and later `start` throws. |
| Electron runtime | `test/electron.cjs` on Electron 44.5.1 (main process): image decode, error code, animated UR. |
| Real camera (manual) | `test/camera-live.cjs` (`npm run test:camera`, or `open -n -W -a Electron.app --args test/camera-live.cjs --out report.json` so macOS asks Electron for camera access): MacBook Pro camera, Electron 44.5.1, animated UR shown on an iPhone held 20-30 cm away. Camera ready 1.45 s; 285 preview frames drawn through `sharedTexture` (~23 fps); `crypto-psbt` 2,003 bytes assembled in 4.23 s from the first part with 25 distinct parts, FNV-1a matching; at most 1 slot lent, 0 after dispose; no errors. Static codes not exercised in that run. |
| Preview path | `test/preview.cjs` on Electron 44.5.1 with a sandboxed, context-isolated preload (`test/preview-preload.cjs`): frames from the animated video go through `importSharedTexture` → `sendSharedTexture` → `setSharedTextureReceiver` → canvas. 32-33 of 32-33 frames drawn in 4 s at 8 fps, none skipped, at most 1 slot lent, 0 after stop; the captured page shows the QR (dark and light pixels); the animated UR completes at the same time. |

Build note: Cargo's `strip = "symbols"` produced a binary whose LC_SYMTAB
string table was not 8-byte aligned, which dyld on macOS 27 refuses to load
("mis-aligned LINKEDIT string pool"). The profile keeps symbols and
`scripts/build.mjs` strips with Xcode `strip -x`, then fails the build if the
string table offset is misaligned.

The tests load `@ngraveio/bc-ur` (to assemble the animated UR) from the
repository root `node_modules`; the package itself has no dependencies.

Pending:

- app-monorepo integration: main-process camera lease and IPC, preload
  receiver, DeviceStage/scan-page canvas, permission flow; real-camera
  preview latency and frame rate.
- x64 build and test; release packaging and attestation contract.
