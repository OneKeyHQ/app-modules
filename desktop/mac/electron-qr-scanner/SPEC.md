# electron-qr-scanner (macOS) SPEC

Status: **In development, not released.** Image decoding and raw-text capture
are implemented and tested on macOS arm64 (Node and Electron 44.5.1). The
embedded camera preview (IOSurface shared texture) and app-monorepo
integration are pending. The module is not listed in `desktop/release.json`
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

## 3. API

```ts
decodeImage(path: string, options?: { maxPixels?: number }): Promise<string | null>;
createCapture(options?: {
  source?: { kind: "camera" } | { kind: "file"; path: string; loop?: boolean };
  onReady?(generation: number): void;
  onText?(text: string, generation: number): void;
  onError?(error: Error & { code: QrScannerErrorCode }, generation: number): void;
  onEnded?(generation: number): void;
}): { start(): void; stop(): void; dispose(): void };
```

- `decodeImage`: passes `original`, `padded`, `grayscale`, `inverted` with
  Vision (same pipeline as iOS). `maxPixels` default 12 MP, capped at 50 MP.
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
| Image decoding | `test/native.test.cjs`: BTCPay lightning fixtures decode to the exact payload; no-QR image resolves `null`; corrupt, missing, empty path and `maxPixels: 0` reject with `invalid-image`. |
| Animated UR through live frames | `test/native.test.cjs`: `test/fixtures/animated-ur.mp4` (2,003-byte `crypto-psbt`, 17 fragments, 51 frames at 8 fps, from `scripts/make-ur-fixture.mjs`) assembles to the expected type, size and FNV-1a in about 2.2 s. |
| Generations and dispose | `test/native.test.cjs`: stop/start yields a newer generation, dispose is idempotent and later `start` throws. |
| Electron runtime | `test/electron.cjs` on Electron 44.5.1 (main process): image decode, error code, animated UR. |

The tests load `@ngraveio/bc-ur` (to assemble the animated UR) from the
repository root `node_modules`; the package itself has no dependencies.

Pending:

- Real camera run (`QR_SCANNER_CAMERA=1 npm test`, or `electron test/electron.cjs --camera`) needs a person to grant camera access.
- ZXing-C++ fallback (the design's macOS fallback); Vision only today.
- Preview: IOSurface frames to Electron `sharedTexture` and the DeviceStage canvas.
- x64 build and test; release packaging and attestation contract.
