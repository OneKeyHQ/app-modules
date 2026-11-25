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
  /** Diagnostics and tests only: limit the engines. Default: Vision, then ZXing-C++. */
  engines?: ("vision" | "zxing")[];
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

export type CaptureSource =
  /** `deviceId` from listCameras(); unset or disconnected uses the default camera. */
  | { kind: "camera"; deviceId?: string }
  /** Frames from a local video file, paced by presentation time. For tests. */
  | { kind: "file"; path: string; loop?: boolean };

export interface CaptureOptions {
  source?: CaptureSource;
  /** When true, report whole-frame symbols via `onSymbols` (top-left normalized `frame` space). Default false. */
  multiSymbol?: boolean;
  onReady?: (generation: number, deviceId?: string) => void;
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
  /** A file source reached its end (not looping). */
  onEnded?: (generation: number) => void;
  /**
   * Preview frames (BGRA IOSurface). Setting this enables preview. Call
   * `release()` once the frame is no longer used, e.g. from Electron's
   * `allReferencesReleased`; at most 3 frames are lent at a time and newer
   * frames are skipped while all are out.
   */
  onFrame?: (frame: QrPreviewFrame) => void;
}

export interface QrPreviewFrame {
  /** IOSurfaceRef for `sharedTexture.importSharedTexture({ handle: { ioSurface } })`. */
  ioSurface: Buffer;
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
  /** Pauses or resumes Vision analysis without stopping capture or releasing the camera. */
  setPaused(paused: boolean): void;
  /** Preview frames currently held by the consumer (at most 3). */
  lentFrames(): number;
  /** Idempotent; no callback runs after it returns. */
  dispose(): void;
}

export function createCapture(options?: CaptureOptions): QrCapture;

export interface QrCameraInfo {
  id: string;
  /** The built-in camera faces the user; USB and Continuity cameras are external. */
  position: "front" | "external";
  label: string;
}

/** Cameras the capture can use. Switching means a new capture with `deviceId`. */
export function listCameras(): QrCameraInfo[];
