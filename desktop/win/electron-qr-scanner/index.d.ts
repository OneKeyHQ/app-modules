export type QrScannerErrorCode =
  | "permission-denied"
  | "no-camera"
  | "camera-busy"
  | "decoder-unavailable"
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

export type CaptureSource =
  | { kind: "camera"; deviceId?: string }
  /** Frames from a local video file, paced by presentation time. For tests. */
  | { kind: "file"; path: string; loop?: boolean };

export interface CaptureOptions {
  source?: CaptureSource;
  onReady?: (generation: number) => void;
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
