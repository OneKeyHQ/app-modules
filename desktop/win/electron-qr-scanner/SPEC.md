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

Pure Rust (`napi-rs 3`, `windows = "=0.62.2"`, `zxing-cpp = "=0.5.3"` bundled
with CMake + MSVC):

- `src/lib.rs`: N-API bindings (`decodeImage`, `listCameras`, `NativeCapture`)
  and `ThreadsafeFunction` event bridge.
- `src/image_decode.rs`: WIC image loading, EXIF orientation normalization,
  pixel budget downscaling, and the 4-pass QR decoding pipeline (`original`,
  `padded`, `grayscale`, `inverted`).
- `src/frame_gate.rs`: Generation tracking and 1-second repeat throttle for
  identical QR texts, passing distinct texts (such as animated BC-UR parts)
  immediately.
- `src/d3d11_preview.rs`: D3D11 device + 3-slot `DXGI_FORMAT_B8G8R8A8_UNORM`
  shared texture pool (`D3D11_RESOURCE_MISC_SHARED_NTHANDLE` + keyed mutex when
  supported) backed by Windows NT handles (`IDXGIResource1::CreateSharedHandle`).
- `src/capture.rs`: Media Foundation device enumeration (`MFEnumDeviceSources`),
  async camera `IMFSourceReader` (`IMFSourceReaderCallback`), PTS-paced MP4
  file source, stride-safe RGB32 extraction, and bounded 1-analyzing + 1-latest
  decode worker.

## 3. API

```ts
decodeImage(path: string, options?: { maxPixels?: number; engines?: "zxing"[] }): Promise<string | null>;
listCameras(): Promise<{ id: string; name: string; isDefault: boolean }[]>;
createCapture(options?: {
  source?: { kind: "camera"; deviceId?: string } | { kind: "file"; path: string; loop?: boolean };
  onReady?(generation: number): void;
  onText?(text: string, generation: number): void;
  onError?(error: Error & { code: QrScannerErrorCode }, generation: number): void;
  onEnded?(generation: number): void;
  onFrame?(frame: { ntHandle: Buffer; width: number; height: number; generation: number; release(): void }): void;
}): {
  start(): void;
  stop(): void;
  setDevice(deviceId?: string | null): void;
  lentFrames(): number;
  dispose(): void;
};
```

- **Preview (`onFrame`, opt-in)**: each frame is a BGRA `ID3D11Texture2D` lent
  from a pool of **3 slots**. `ntHandle` is the 8-byte little-endian `HANDLE`
  buffer for `sharedTexture.importSharedTexture({ textureInfo: { pixelFormat:
  "bgra", codedSize, handle: { ntHandle } } })`. The consumer calls `release()`
  from Electron's `allReferencesReleased`. While all 3 slots are out, new
  preview frames are skipped while QR decoding continues. Frames from an older
  generation or arriving after `dispose()` are released by `index.cjs` without
  being delivered.
- **Camera enumeration & switching**: `listCameras()` returns Media Foundation
  video capture devices (`id` = symbolic link, `name` = friendly name,
  `isDefault: true` for index 0). `{ kind: "camera" }` opens the default camera
  when `deviceId` is omitted; `capture.setDevice(id)` switches cameras cleanly
  (releasing the previous camera handle before opening the next with a newer
  generation). Unknown or unplugged `deviceId` reports `no-camera`.
- **`decodeImage`**: passes `original`, `padded`, `grayscale`, `inverted` with
  ZXing-C++. `maxPixels` defaults to 12 MP (ceiling 50 MP); sources above 200 MP
  reject with `resource-limit`. `options.engines` accepts `["zxing"]`; unknown
  or empty lists reject with `decoder-unavailable`.
- **Error codes**: `permission-denied`, `no-camera`, `camera-busy`,
  `decoder-unavailable`, `invalid-image`, `resource-limit`.

## 4. Threads and lifetime

- Capture runs on a dedicated COM MTA / Media Foundation session thread
  (async `IMFSourceReaderCallback` for cameras, interruptible PTS-paced loop
  for video files).
- QR decoding runs on a dedicated worker thread fed by a 1-slot replaceable
  mailbox so camera capture and preview never stall on dense frames.
- `stop()` invalidates the generation, halts callbacks, flushes/drops the
  reader, calls `IMFMediaSource::Shutdown()`, and joins the session thread so
  camera hardware is released before `stop()` returns.
- `dispose()` is idempotent, clears the event emitter synchronously, stops any
  active capture, joins the decode worker, and closes all D3D11 NT handles.

## 5. Conformance

Verified on Windows x64 (Rust 1.100.0, MSVC 2022, Node 24.13.0, Electron
44.5.1). Release binary size: ~2.91 MB stripped with LTO (includes bundled
`zxing-cpp =0.5.3`).

| Contract | Check |
| --- | --- |
| Frame gate (Rust) | `cargo test --locked`: 3/3 passing — distinct animated UR parts pass immediately at 30 fps, identical text throttled to once per second, generation change resets throttle. |
| Code quality | `cargo fmt --check`, `cargo clippy --locked -- -D warnings`, `npm run test:types` (`tsc --strict`), `node scripts/check-package.mjs` (PE x64 header check). |
| Image decoding | `test/native.test.cjs`: BTCPay lightning fixtures (`clean`, `btcpay-dark`, `inverted`, `exif-rotated`) decode to the exact 461-char payload through the 4-pass WIC + ZXing-C++ pipeline; `no-qr.png` resolves `null`; `corrupt.png`, missing path, empty path and `maxPixels: 0` reject with `invalid-image`; `engines` accepts `["zxing"]` and rejects unknown or empty lists with `decoder-unavailable`. |
| Camera enumeration & switching | `test/native.test.cjs`: `listCameras()` returns `{ id, name, isDefault }[]`; `setDevice` rejects on file sources with `decoder-unavailable`; unknown `deviceId` reports `no-camera`; `QR_SCANNER_CAMERA=1` cleanly reports `no-camera` when no webcam is attached. |
| Animated UR through live frames | `test/native.test.cjs`: `animated-ur.mp4` (2,003-byte `crypto-psbt`, 17 fragments, 51 frames at 8 fps) assembles via Media Foundation + ZXing-C++ to the exact type, size and FNV-1a (`07a5e3d7`) in ~2.14 s. |
| Generations and dispose | `test/native.test.cjs`: `stop()` / `start()` delivers a newer generation, `dispose()` is idempotent and silences callbacks, and subsequent `start()` / `setDevice()` throw `decoder-unavailable`. |
| Electron runtime | `test/electron.cjs` on Electron 44.5.1 (main process): image decode, error code, `listCameras`, and animated UR assembly all pass. |
| Preview path & leak check | `test/preview.cjs` on Electron 44.5.1 with a sandboxed, context-isolated preload (`test/preview-preload.cjs`): D3D11 shared BGRA NT handles → `importSharedTexture` → `sendSharedTexture` → `setSharedTextureReceiver` → `<canvas>`. 32/32 primary frames + 20 frames across 10 open/close cycles drawn (52 total, 0 skipped), `maxLent <= 3`, `lentFrames() === 0` after every stop, captured page confirms QR dark/light pixels (`dark: 1778`, `light: 2591`), and animated UR completes concurrently. |
| Desktop baseline tooling | `node desktop/scripts/release.mjs validate` and `node --test desktop/scripts/*.test.mjs` (11/11 passing). |

Pending:

- Real Windows webcam verification (builtin + USB cameras, switching between
  multiple physical devices, OneKey Pro direct-scan animated UR benchmark via
  `npm run test:camera`).
- Windows ARM64 native build and test.
- Release packaging, attestation contract, and `desktop/release.json` listing.
- `app-monorepo` integration: main-process camera lease and IPC, preload
  `sharedTexture` receiver, DeviceStage / scan-page `<canvas>`, camera picker
  UI, and Web fallback on GPU/native error.
