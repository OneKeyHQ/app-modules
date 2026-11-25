export type QrScannerErrorCode =
  | "permission-denied"
  | "no-camera"
  | "camera-busy"
  | "decoder-unavailable"
  | "preview-unavailable"
  | "invalid-image"
  | "resource-limit";

export interface QrScannerError extends Error {
  code: QrScannerErrorCode;
}

export interface DecodeImageOptions {
  /** Images above this many pixels are downscaled first. Default 12 MP, capped at 50 MP. */
  maxPixels?: number;
  /** Diagnostics and tests only: limit the engines. Default: ZXing-C++. */
  engines?: "zxing"[];
}

export interface QrSymbolBounds {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface QrDetectedSymbol {
  text: string;
  centerX: number;
  centerY: number;
  bounds?: QrSymbolBounds;
}

export interface QrImageSymbolsResult {
  width: number;
  height: number;
  symbols: QrDetectedSymbol[];
}

/** Raw text of the first QR symbol in a local image file, or null. */
export function decodeImage(path: string, options?: DecodeImageOptions): Promise<string | null>;

/** All QR symbols in a local image file with top-left normalized coordinates. */
export function decodeImageSymbols(
  path: string,
  options?: DecodeImageOptions,
): Promise<QrImageSymbolsResult>;

export interface CameraDevice {
  /** Media Foundation symbolic link used to target this camera in `CaptureSource` or `setDevice`. */
  id: string;
  /** Human-readable camera name reported by Windows. */
  name: string;
  /** True for the default camera opened when `deviceId` is omitted. */
  isDefault: boolean;
}

/** Enumerates available Windows video capture devices. Returns an empty array when none are attached. */
export function listCameras(): Promise<CameraDevice[]>;

export type PreferredResolution = "720p" | "1080p";

export interface CaptureReadyInfo {
  /** Actual negotiated frame width in pixels. */
  width: number;
  /** Actual negotiated frame height in pixels. */
  height: number;
  /** Actual negotiated frame rate (0 when unknown, e.g. variable-rate files). */
  fps: number;
  /** Negotiated native video subtype (e.g. "NV12", "YUY2", "MJPG", "RGB32"). */
  subtype: string;
  /** Total Windows cameras enumerated when opening this session (0 for file sources). */
  cameraCount: number;
  /** Zero-based index of the opened camera (null for file sources). Never exposes raw deviceId/name. */
  cameraIndex: number | null;
  /** True when the default camera (index 0) was opened. */
  isDefaultCamera: boolean;
  /** True when the camera exposes hardware focus control (false on fixed-focus webcams). */
  focusSupported: boolean;
  /** True when the camera supports hardware auto-focus. */
  autoFocusSupported: boolean;
  /** True when the camera exposes hardware exposure control. */
  exposureSupported: boolean;
}

export interface CaptureStats {
  framesCaptured: number;
  previewFramesEmitted: number;
  previewFramesSkipped: number;
  decodeAttempts: number;
  decodeHits: number;
  maxLent: number;
}

/**
 * Privacy-safe structured lifecycle events safe to forward directly to
 * OneKey's local logger (`electron-log/main`). Never contains raw `deviceId`,
 * `camera.name`, local file paths, pixel data, or QR/BC-UR payload text.
 */
export type QrScannerDiagnosticEvent =
  | {
      event: "start";
      level: "info";
      backend: "windows-native";
      sourceKind: "camera" | "file";
      preferredResolution?: PreferredResolution;
      lockFocusOnDetect?: boolean;
      hasCustomDeviceId?: boolean;
      withPreview: boolean;
    }
  | {
      event: "ready";
      level: "info";
      backend: "windows-native";
      generation: number;
      readyMs: number;
      cameraCount: number;
      cameraIndex: number | null;
      isDefaultCamera: boolean;
      width: number;
      height: number;
      fps: number;
      subtype: string;
      focusSupported: boolean;
      autoFocusSupported: boolean;
      exposureSupported: boolean;
    }
  | {
      event: "switch-device";
      level: "info";
      backend: "windows-native";
      hasCustomDeviceId: boolean;
    }
  | {
      event: "error";
      level: "warn";
      backend: "windows-native";
      generation: number;
      code: QrScannerErrorCode;
      fallbackToWeb: boolean;
    }
  | {
      event: "stop";
      level: "info";
      backend: "windows-native";
      reason: "stop" | "dispose";
      generation: number;
      durationMs: number;
      framesCaptured: number;
      previewFramesEmitted: number;
      previewFramesSkipped: number;
      decodeAttempts: number;
      decodeHits: number;
      maxLent: number;
      lentAfterStop: number;
    };

export type CaptureSource =
  | {
      kind: "camera";
      deviceId?: string;
      /** Preferred camera resolution tier. Default: "720p". */
      preferredResolution?: PreferredResolution;
      /**
       * Temporarily lock focus at the current lens position while QR codes are
       * decoding (preventing AF hunting on animated BC-UR) and automatically
       * return to Auto after 1.5 s without detections. Default: false.
       */
      lockFocusOnDetect?: boolean;
    }
  /** Frames from a local video file, paced by presentation time. For tests. */
  | { kind: "file"; path: string; loop?: boolean };

export interface CaptureOptions {
  source?: CaptureSource;
  /** When true, report whole-frame symbols via `onSymbols` (top-left normalized `frame` space). Default false. */
  multiSymbol?: boolean;
  onReady?: (generation: number, info?: CaptureReadyInfo, deviceId?: string) => void;
  /** Each distinct QR text; the same text repeats at most once per second. */
  onText?: (text: string, generation: number) => void;
  /** Whole-frame symbols when `multiSymbol` is enabled. */
  onSymbols?: (
    symbols: QrDetectedSymbol[],
    frameWidth: number,
    frameHeight: number,
    generation: number,
  ) => void;
  onError?: (error: QrScannerError, generation: number) => void;
  /** A non-looping file reached its end and the last queued decode finished. */
  onEnded?: (generation: number) => void;
  /**
   * Preview frames (BGRA D3D11 shared texture NT handle). Setting this enables
   * preview. Call `release()` once the frame is no longer used, e.g. from
   * Electron's `allReferencesReleased`; at most 3 frames are lent at a time and
   * newer frames are skipped while all are out.
   */
  onFrame?: (frame: QrPreviewFrame) => void;
  /**
   * Privacy-safe lifecycle diagnostics (`start`, `ready`, `switch-device`,
   * `error`, `stop`) suitable for writing to OneKey's local log files.
   */
  onDiagnostic?: (event: QrScannerDiagnosticEvent) => void;
}

export interface QrPreviewFrame {
  /** Windows NT HANDLE buffer for `sharedTexture.importSharedTexture({ handle: { ntHandle } })`. */
  ntHandle: Buffer;
  width: number;
  height: number;
  generation: number;
  /** Idempotent. */
  release(): void;
}

export interface QrCapture {
  start(): void;
  stop(): void;
  /** Dynamically toggles whole-frame multi-symbol reporting on the next frame. */
  setMultiSymbol(enabled: boolean): void;
  /** Pauses or resumes ZXing decode worker evaluation without releasing the camera. */
  setPaused(paused: boolean): void;
  /**
   * Selects a camera by `CameraDevice.id` (or resets to the default camera when
   * omitted / null). If the capture is running, stops the current camera and
   * restarts on the requested camera with a newer generation. After a terminal
   * camera error this only updates the selection for the next start().
   */
  setDevice(deviceId?: string | null): void;
  /** Preview frames currently held by the consumer (at most 3). */
  lentFrames(): number;
  /** Triggers hardware autofocus when the active camera supports it. */
  focus?(x?: number, y?: number): boolean;
  /** Current session counters (privacy-safe). */
  stats(): CaptureStats;
  /** Idempotent; no callback runs after it returns. */
  dispose(): void;
}

export function createCapture(options?: CaptureOptions): QrCapture;

/**
 * Returns true when an error from the native Windows backend or Electron
 * `sharedTexture` path should trigger fallback to the Web QR scanner backend
 * (`@onekeyfe/react-native-qr-scanner/web`) after stopping/disposing the native
 * capture so the camera handle is released.
 *
 * Returns false for `permission-denied`, `no-camera`, `camera-busy`,
 * `invalid-image`, and `resource-limit`.
 */
export function shouldFallbackToWeb(error: unknown): boolean;
