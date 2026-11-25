# electron-qr-scanner (Windows) SPEC

Status: **In development, not released.** Image decoding, camera enumeration
and switching, raw-text capture, and the preview frame path (D3D11 shared
texture NT handle → Electron `sharedTexture` → canvas) are implemented for
Windows x64 (with ARM64 build support). `app-monorepo` integration is pending.
The module is not listed in `desktop/release.json` and has no release workflow
yet; adding it requires the locked build, test and pack contract `desktop/SPEC.md`
asks of every module.

Design background: `app-monorepo` `docs/qr-scanner-sdk-design.md` section 4.

## 1. Scope

- Enumerate Windows video capture devices (`listCameras`) and switch cameras
  (`source.deviceId` / `capture.setDevice`).
- Decode one local image file to QR text through WIC + ZXing-C++ (4-pass
  pipeline matching iOS, Android, Web and macOS).
- Capture camera frames (or a local video file, for tests) through Media
  Foundation, decode QR codes with ZXing-C++, and emit optional BGRA D3D11
  shared-texture preview frames (`ntHandle`).
- Not in scope: BC-UR assembly and mode handling (the app feeds raw text into
  `@onekeyfe/react-native-qr-scanner/ur`), permission UI, preview rendering.

The npm package stays dependency-free, like the other desktop modules.

## 2. Structure

Rust (`napi-rs 3`, `windows = "=0.62.2"`) with `zxing-cpp = "=0.5.3"`
bundled via CMake and MSVC:

- `src/lib.rs`: N-API bindings (`decodeImage`, `listCameras`, `NativeCapture`)
  and `ThreadsafeFunction` event bridge.
- `src/image_decode.rs`: WIC image loading, EXIF orientation normalization,
  pixel budget downscaling, and the 4-pass QR decoding pipeline (`original`,
  `padded`, `grayscale`, `inverted`). `src/zxing_text.cpp` reads the plain
  ZXing UTF-8 result with its length for both image and capture decoding.
- `src/frame_gate.rs`: Generation tracking and 1-second repeat throttle for
  identical QR texts, passing distinct texts (such as animated BC-UR parts)
  immediately.
- `src/d3d11_preview.rs`: D3D11 device + 3-slot `DXGI_FORMAT_B8G8R8A8_UNORM`
  shared texture pool (`D3D11_RESOURCE_MISC_SHARED_NTHANDLE` and keyed mutex) backed by Windows NT handles (`IDXGIResource1::CreateSharedHandle`).
- `src/capture.rs`: Media Foundation device enumeration (`MFEnumDeviceSources`),
  async camera `IMFSourceReader` (`IMFSourceReaderCallback`), PTS-paced MP4
  file source, bounds-checked signed-stride RGB32 extraction, and bounded
  1-analyzing + 1-latest decode worker.

## 3. API

```ts
decodeImage(path: string, options?: { maxPixels?: number; engines?: "zxing"[] }): Promise<string | null>;
listCameras(): Promise<{ id: string; name: string; isDefault: boolean }[]>;
shouldFallbackToWeb(error: unknown): boolean;
createCapture(options?: {
  source?:
    | {
        kind: "camera";
        deviceId?: string;
        preferredResolution?: "720p" | "1080p";
        lockFocusOnDetect?: boolean;
      }
    | { kind: "file"; path: string; loop?: boolean };
  onReady?(generation: number, info?: CaptureReadyInfo, deviceId?: string): void;
  onText?(text: string, generation: number): void;
  onError?(error: Error & { code: QrScannerErrorCode }, generation: number): void;
  onEnded?(generation: number): void;
  onFrame?(frame: { ntHandle: Buffer; width: number; height: number; generation: number; release(): void }): void;
  onDiagnostic?(event: QrScannerDiagnosticEvent): void;
}): {
  start(): void;
  stop(): void;
  setDevice(deviceId?: string | null): void;
  focus(): boolean;
  lentFrames(): number;
  stats(): CaptureStats;
  dispose(): void;
};
```

- **Preview (`onFrame`, opt-in)**: each frame is a BGRA `ID3D11Texture2D` lent
  from a pool of **3 slots**. `ntHandle` is the 8-byte little-endian `HANDLE`
  buffer for `sharedTexture.importSharedTexture({ textureInfo: { pixelFormat:
  "bgra", codedSize, handle: { ntHandle } } })`. The consumer calls `release()`
  from Electron's `allReferencesReleased`. While all 3 slots are out, new
  preview frames are skipped while QR decoding continues. A keyed mutex timeout
  skips that frame, and abandonment discards the surface so the pool replaces
  it on the next lend. The wrapper releases queued frames from inactive
  generations without delivering them.
- **Camera enumeration, switching, resolution & focus**: `listCameras()` returns
  Media Foundation video capture devices (`id` = symbolic link, `name` = friendly
  name, `isDefault: true` for index 0). `{ kind: "camera" }` opens the default
  camera when `deviceId` is omitted; `capture.setDevice(id)` switches cameras
  cleanly (releasing the previous camera handle before opening the next with a
  newer generation while running). After a terminal camera error,
  `setDevice()` selects the device for the next explicit `start()`. Mode negotiation honors `preferredResolution` (`"720p"`
  default or `"1080p"`), favors ≥ 25 fps, and prefers uncompressed `NV12`/`YUY2`
  over `MJPG` at equal frame rate. On open, `IAMCameraControl` probes hardware
  focus/exposure capabilities (reported in `onReady(generation, info)`), resets
  supported cameras to Auto focus, and optionally locks focus during active QR
  detection (`lockFocusOnDetect`, auto-unlocking after 1.5 s without hits and
  restoring Auto on teardown). `capture.focus()` dispatches a whole-camera
  autofocus request (`CameraControl_Flags_Auto`) to the active COM MTA camera
  session thread when `IAMCameraControl` reports autofocus support, returning
  `true` when applied (or `false` when stopped, on file sources, or on
  fixed-focus cameras) and holding off `lockFocusOnDetect` re-locking for 1.5 s
  while the autofocus sweep settles.
- Readiness supplies the bound camera ID to the application callback. File sources have no ID. Diagnostics still omit the camera ID.
- **Web fallback contract**: `shouldFallbackToWeb(error)` returns `true` for
  `preview-unavailable`, `decoder-unavailable` (including native `.node` load
  failures), and unexpected GPU/runtime errors, and `false` for
  `permission-denied`, `no-camera`, `camera-busy`, `invalid-image`, and
  `resource-limit`. When D3D11 shared-texture creation fails, native capture
  emits `preview-unavailable` and stops immediately. Both `stop()` and
  `dispose()` wait for the Windows camera handle to close. `dispose()` also
  clears the preview pool before the app falls back to
  `@onekeyfe/react-native-qr-scanner/web` with a new capture generation in the
  same BC-UR session.
- **Privacy-safe local diagnostics (`onDiagnostic` / `stats()`)**: emits
  low-frequency lifecycle events (`start`, `ready`, `switch-device`, `error`,
  `stop`) containing only counts, timings, negotiated format/capabilities
  (`cameraCount`, `cameraIndex`, `isDefaultCamera`, `width`, `height`, `fps`,
  `subtype`, `focusSupported`, `autoFocusSupported`, `exposureSupported`),
  `code`, and `fallbackToWeb`. Raw `deviceId` symbolic links, `camera.name`,
  local file paths, raw pixels, and QR/BC-UR payload text are never emitted.
  Designed to plug directly into OneKey Desktop's local `electron-log/main`
  (`onDiagnostic: (e) => logger[e.level]("[qr-scanner] " + e.event, e)`).
- **`decodeImage`**: passes `original`, `padded`, `grayscale`, `inverted` with
  ZXing-C++. `maxPixels` defaults to 12 MP (ceiling 50 MP) and bounds the
  normalized image and padded retry. Padding that exceeds the budget is skipped
  and the grayscale and inverted passes still run. Sources above 200 MP or
  scanner-owned image buffer allocation failures reject with `resource-limit`.
  Image and capture text preserve GS1 group separators, embedded NULs, and
  ZXing character-set decoding. `options.engines` accepts `["zxing"]`; unknown
  or empty lists reject with `decoder-unavailable`.
- **Error codes**: `permission-denied`, `no-camera`, `camera-busy`,
  `decoder-unavailable`, `preview-unavailable`, `invalid-image`,
  `resource-limit`.
- **Multi-symbol detection and freeze (Implemented)** — contract shared with
  `native-views/react-native-qr-scanner/docs/SPEC.md` section 3.4:
  - `createCapture` adds `multiSymbol?: boolean` (default `false`) and
    `onSymbols?(symbols: QrDetectedSymbol[], frameWidth: number, frameHeight: number, generation: number): void`,
    plus handle methods `setMultiSymbol(enabled: boolean): void` and
    `setPaused(paused: boolean): void` (pauses decode worker evaluation without
    closing the Media Foundation camera handle or D3D11 pool; Electron preload
    freezes `<canvas>` while still releasing every `importedSharedTexture`).
  - When `multiSymbol` is `true`, `src/zxing_text.cpp` switches from
    `ZXing::ReadBarcode` (`maxNumberOfSymbols(1)`) to `ZXing::ReadBarcodes`
    (`maxNumberOfSymbols(8)`) and emits top-left normalized `[0, 1]` `frame`
    coordinates computed from each `ZXing::Barcode::position()` 4-corner
    centroid divided by `width`/`height`, bypassing `frame_gate.rs` splitting.
    Switching `setMultiSymbol(false)` (e.g. on UR fast-path latch) takes effect
    on the next frame with zero camera restart.
  - Add `decodeImageSymbols(path: string, options?)` returning EXIF-oriented
    image dimensions and all detected symbols with normalized top-left
    coordinates.

## 4. Threads and lifetime

- Capture runs on a dedicated COM MTA / Media Foundation session thread
  (async `IMFSourceReaderCallback` for cameras, interruptible PTS-paced loop
  for video files).
- QR decoding runs on a dedicated worker thread fed by a 1-slot replaceable
  mailbox so camera capture and preview never stall on dense frames.
  File EOF is emitted by the decode worker after it has processed the final
  pending frame. Malformed frame bounds skip the sample. A failed media type
  refresh ends the capture with an error.
- `start()` and camera switches synchronously select the generation accepted
  by the JS wrapper. `stop()` clears it so queued events cannot reach capture
  callbacks. Camera teardown halts sample requests, flushes the reader, waits
  up to 2 seconds for active callbacks, and attempts to restore Auto focus
  before releasing the reader and shutting down the media source. A failed
  focus restore logs only the HRESULT. `stop()` joins the session thread
  before returning.
- `dispose()` is idempotent, clears the event emitter synchronously, stops any
  active capture, joins the decode worker, and closes all D3D11 NT handles.

## 5. Conformance

Verified on Windows x64 (Rust 1.100.0, MSVC 2022, Node 24.13.0, Electron
44.5.1). Release binary size: ~2.91 MB stripped with LTO (includes bundled
`zxing-cpp =0.5.3`).

| Contract | Check |
| --- | --- |
| Rust unit tests | `cargo test --locked`: 4/4 passing — frame gate (distinct UR parts at 30 fps, 1 s duplicate throttle, generation reset) and native mode scoring (uncompressed NV12 > MJPG, 720p@30 > 1080p@10, 1080p@30 preferred when requested). |
| Code quality | `cargo fmt --check`, `cargo clippy --locked -- -D warnings`, `npm run test:types` (`tsc --strict`), `node scripts/check-package.mjs` (PE x64 header check). |
| Image decoding | `test/native.test.cjs`: BTCPay lightning fixtures (`clean`, `btcpay-dark`, `inverted`, `exif-rotated`) decode to the exact 461-char payload through the 4-pass WIC + ZXing-C++ pipeline; `no-qr.png` resolves `null`; `corrupt.png`, missing path, empty path and `maxPixels: 0` reject with `invalid-image`; `engines` accepts `["zxing"]` and rejects unknown or empty lists with `decoder-unavailable`. |
| Camera enumeration, switching & fallback | `test/native.test.cjs`: `listCameras()` returns `{ id, name, isDefault }[]`; `setDevice` rejects on file sources with `decoder-unavailable`; unknown `deviceId` reports `no-camera`; invalid `preferredResolution` rejects; `shouldFallbackToWeb` classifies fallback-eligible (`preview-unavailable`, `decoder-unavailable`, uncoded GPU errors) vs non-fallback codes; `QR_SCANNER_CAMERA=1` cleanly reports `no-camera` when no webcam is attached. |
| Animated UR, ready info & diagnostics | `test/native.test.cjs`: `animated-ur.mp4` (2,003-byte `crypto-psbt`, 17 fragments, 51 frames at 8 fps) emits `onReady(generation, info)`, assembles via Media Foundation + ZXing-C++ to the exact type, size and FNV-1a (`07a5e3d7`) in ~2.11 s, and emits `start` → `ready` → `stop` diagnostics verified free of file paths, QR payloads, and custom `deviceId` strings. |
| Generations and dispose | `test/native.test.cjs`: `stop()` / `start()` delivers a newer generation, `dispose()` is idempotent and silences callbacks, and subsequent `start()` / `setDevice()` throw `decoder-unavailable`. |
| Electron runtime | `test/electron.cjs` on Electron 44.5.1 (main process): image decode, error code, `listCameras`, and animated UR assembly all pass. |
| Preview path, leak check & fallback teardown | `test/preview.cjs` on Electron 44.5.1 with a sandboxed, context-isolated preload (`test/preview-preload.cjs`): D3D11 shared BGRA NT handles → `importSharedTexture` → `sendSharedTexture` → `setSharedTextureReceiver` → `<canvas>`. 32/32 primary frames + 20 frames across 10 open/close cycles drawn (52 total, 0 skipped), `maxLent <= 3`, `lentFrames() === 0` after every stop, simulated `preview-unavailable` triggers clean `dispose()` with 0 leaked slots and 0 post-fallback events, captured page confirms QR dark/light pixels (`dark: 1823`, `light: 2544`), and animated UR completes concurrently. |
| Desktop baseline tooling | `node desktop/scripts/release.mjs validate` and `node --test desktop/scripts/*.test.mjs` (11/11 passing). |

Pending:

- The manual camera picker starts a new capture when selecting a device after
  a terminal error. Manual camera reports contain sanitized metadata, coded errors, counts,
  timings and UR conformance results. Camera identifiers and QR text are
  kept out of the report.
- Real Windows webcam verification (builtin + USB cameras, switching between
  multiple physical devices, OneKey Pro direct-scan animated UR benchmark via
  `npm run test:camera`).
- Windows ARM64 native build and test.
- Release packaging, attestation contract, and `desktop/release.json` listing.
- `app-monorepo` integration: main-process camera lease and IPC, preload
  `sharedTexture` receiver, DeviceStage / scan-page `<canvas>`, camera picker
  UI, and Web fallback on GPU/native error.
