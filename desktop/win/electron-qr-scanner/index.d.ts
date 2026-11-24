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

/** Raw text of the first QR symbol in a local image file, or null. */
export function decodeImage(path: string, options?: DecodeImageOptions): Promise<string | null>;

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
  /** True when the camera exposes hardware focus control (false on fixed-focus webcams). */
  focusSupported: boolean;
  /** True when the camera supports hardware auto-focus. */
  autoFocusSupported: boolean;
  /** True when the camera exposes hardware exposure control. */
  exposureSupported: boolean;
}

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
  onReady?: (generation: number, info?: CaptureReadyInfo) => void;
  /** Each distinct QR text; the same text repeats at most once per second. */
  onText?: (text: string, generation: number) => void;
  onError?: (error: QrScannerError, generation: number) => void;
  /** A file source reached its end (not looping). */
  onEnded?: (generation: number) => void;
  /**
   * Preview frames (BGRA D3D11 shared texture NT handle). Setting this enables
   * preview. Call `release()` once the frame is no longer used, e.g. from
   * Electron's `allReferencesReleased`; at most 3 frames are lent at a time and
   * newer frames are skipped while all are out.
   */
  onFrame?: (frame: QrPreviewFrame) => void;
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
  /**
   * Selects a camera by `CameraDevice.id` (or resets to the default camera when
   * omitted / null). If the capture is running, stops the current camera and
   * restarts on the requested camera with a newer generation.
   */
  setDevice(deviceId?: string | null): void;
  /** Preview frames currently held by the consumer (at most 3). */
  lentFrames(): number;
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
