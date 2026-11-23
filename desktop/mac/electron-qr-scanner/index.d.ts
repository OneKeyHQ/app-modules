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
}

/** Raw text of the first QR symbol in a local image file, or null. */
export function decodeImage(path: string, options?: DecodeImageOptions): Promise<string | null>;

export type CaptureSource =
  | { kind: "camera" }
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
}

export interface QrCapture {
  start(): void;
  stop(): void;
  /** Idempotent; no callback runs after it returns. */
  dispose(): void;
}

export function createCapture(options?: CaptureOptions): QrCapture;
