import { QrScannerError } from '../errors';
import { createScanSession } from '../ur';
import { getEngines } from './engines';
import { fitToPixelBudget } from './imageOps';

import type { QrScannerErrorCode } from '../errors';
import type { ScanError, ScanMode, ScanResult, UrLimits } from '../ur';
import type { QrImageEngine, RgbaImage } from './imageOps';

export type WebScanSource =
  /** getUserMedia; the scanner owns and stops the stream. */
  | { kind: 'camera'; deviceId?: string }
  /** A video element the caller already plays (a file, a stream it owns). */
  | { kind: 'element' };

export interface WebQrScannerOptions {
  video: HTMLVideoElement;
  source?: WebScanSource;
  mode: ScanMode;
  allowPlainText?: boolean;
  limits?: Partial<UrLimits>;
  /** Longest side of the analysed frame, in pixels. Default 1280. */
  maxAnalysisSize?: number;
  /** A camera that produces no frame within this many ms fails with `camera-busy`. Default 8000. */
  firstFrameTimeoutMs?: number;
  onReady?: () => void;
  onProgress?: (progress: number) => void;
  onResult?: (result: ScanResult) => void;
  onError?: (error: QrScannerError | ScanError) => void;
}

export interface WebQrScanner {
  start(): Promise<void>;
  /** Stops analysis (and the camera, for a camera source); keeps UR parts. */
  stop(): void;
  /** Starts a new scan session: drops collected UR parts. */
  resetSession(): void;
  dispose(): void;
}

/** With a native detector, zxing also runs on every Nth missed frame. */
const ZXING_EVERY_N_FRAMES = 4;

type VideoWithFrameCallback = HTMLVideoElement & {
  requestVideoFrameCallback?: (callback: () => void) => number;
  cancelVideoFrameCallback?: (handle: number) => void;
};

function cameraErrorCode(error: unknown): QrScannerErrorCode {
  const name = (error as { name?: string })?.name;
  if (name === 'NotAllowedError' || name === 'SecurityError') return 'permission-denied';
  if (name === 'NotFoundError' || name === 'OverconstrainedError') return 'no-camera';
  if (name === 'NotReadableError' || name === 'AbortError') return 'camera-busy';
  return 'decoder-unavailable';
}

export function createWebQrScanner(options: WebQrScannerOptions): WebQrScanner {
  const { video, source = { kind: 'camera' }, mode, allowPlainText, limits } = options;
  const maxAnalysisSize = options.maxAnalysisSize ?? 1280;
  const firstFrameTimeoutMs = options.firstFrameTimeoutMs ?? 8000;
  const element = video as VideoWithFrameCallback;
  const handlers = options;

  let session = createScanSession({ mode, allowPlainText, limits });
  let generation = 0;
  let disposed = false;
  let stream: MediaStream | undefined;
  let frameHandle: number | undefined;
  let timer: ReturnType<typeof setTimeout> | undefined;
  let analysing = false;
  let lastTime = -1;
  let frameIndex = 0;
  let canvas: HTMLCanvasElement | OffscreenCanvas | undefined;
  let engines: QrImageEngine[] = [];

  const fail = (code: QrScannerErrorCode, message: string) =>
    handlers.onError?.(new QrScannerError(code, message));

  function readFrame(): RgbaImage | null {
    const { videoWidth, videoHeight } = video;
    if (!videoWidth || !videoHeight) return null;
    const scale = Math.min(1, maxAnalysisSize / Math.max(videoWidth, videoHeight));
    const { width, height } = fitToPixelBudget(
      Math.round(videoWidth * scale),
      Math.round(videoHeight * scale),
      maxAnalysisSize * maxAnalysisSize
    );
    canvas ??=
      typeof OffscreenCanvas === 'function'
        ? new OffscreenCanvas(width, height)
        : document.createElement('canvas');
    if (canvas.width !== width) canvas.width = width;
    if (canvas.height !== height) canvas.height = height;
    const context = canvas.getContext('2d', { willReadFrequently: true }) as
      | CanvasRenderingContext2D
      | OffscreenCanvasRenderingContext2D
      | null;
    if (!context) return null;
    context.drawImage(video, 0, 0, width, height);
    return { data: context.getImageData(0, 0, width, height).data, width, height };
  }

  async function analyse(runGeneration: number) {
    const image = readFrame();
    if (!image) return;
    frameIndex += 1;
    for (const [index, engine] of engines.entries()) {
      const isFallback = index > 0;
      if (isFallback && frameIndex % ZXING_EVERY_N_FRAMES !== 0) continue;
      const text = await engine.decode(image);
      if (runGeneration !== generation || disposed) return;
      if (!text) continue;
      const outcome = session.receive(text);
      if (outcome.status === 'progress') handlers.onProgress?.(outcome.progress);
      else if (outcome.status === 'complete') {
        if (outcome.result.kind === 'ur') handlers.onProgress?.(1);
        handlers.onResult?.(outcome.result);
      } else if (outcome.status === 'rejected') handlers.onError?.(outcome.error);
      return;
    }
  }

  function schedule(runGeneration: number) {
    if (runGeneration !== generation || disposed) return;
    const tick = () => {
      if (runGeneration !== generation || disposed) return;
      // One frame in analysis at a time; skip frames that arrive meanwhile.
      if (analysing || video.currentTime === lastTime) {
        schedule(runGeneration);
        return;
      }
      lastTime = video.currentTime;
      analysing = true;
      analyse(runGeneration)
        .catch((error: unknown) => {
          if (runGeneration === generation) {
            fail(
              error instanceof QrScannerError ? error.code : 'decoder-unavailable',
              error instanceof Error ? error.message : String(error)
            );
          }
        })
        .finally(() => {
          analysing = false;
          schedule(runGeneration);
        });
    };
    if (element.requestVideoFrameCallback) {
      frameHandle = element.requestVideoFrameCallback(tick);
    } else {
      timer = setTimeout(tick, 1000 / 15);
    }
  }

  function cancelSchedule() {
    if (frameHandle !== undefined) element.cancelVideoFrameCallback?.(frameHandle);
    if (timer !== undefined) clearTimeout(timer);
    frameHandle = undefined;
    timer = undefined;
  }

  function releaseCamera() {
    stream?.getTracks().forEach((track) => track.stop());
    if (stream && video.srcObject === stream) video.srcObject = null;
    stream = undefined;
  }

  return {
    async start() {
      if (disposed) throw new QrScannerError('decoder-unavailable', 'The scanner was disposed');
      cancelSchedule();
      generation += 1;
      const runGeneration = generation;
      try {
        engines = await getEngines();
      } catch (error) {
        fail('decoder-unavailable', error instanceof Error ? error.message : String(error));
        return;
      }
      if (source.kind === 'camera') {
        if (!navigator.mediaDevices?.getUserMedia) {
          fail('no-camera', 'getUserMedia is not available');
          return;
        }
        try {
          const next = await navigator.mediaDevices.getUserMedia({
            audio: false,
            video: {
              deviceId: source.deviceId ? { exact: source.deviceId } : undefined,
              facingMode: source.deviceId ? undefined : { ideal: 'environment' },
              width: { ideal: 1280 },
              height: { ideal: 720 },
            },
          });
          if (runGeneration !== generation || disposed) {
            next.getTracks().forEach((track) => track.stop());
            return;
          }
          releaseCamera();
          stream = next;
          video.srcObject = next;
          video.muted = true;
          video.playsInline = true;
          // A live track can still deliver no frames (another app holds the
          // device, OS-level capture permission); play() then never settles.
          const started = await Promise.race([
            video.play().then(() => true),
            new Promise<false>((resolve) =>
              setTimeout(() => resolve(false), firstFrameTimeoutMs)
            ),
          ]);
          if (!started) {
            if (runGeneration === generation) {
              releaseCamera();
              fail('camera-busy', 'The camera produced no frames');
            }
            return;
          }
        } catch (error) {
          if (runGeneration === generation) {
            releaseCamera();
            fail(cameraErrorCode(error), error instanceof Error ? error.message : String(error));
          }
          return;
        }
      }
      if (runGeneration !== generation || disposed) return;
      lastTime = -1;
      handlers.onReady?.();
      schedule(runGeneration);
    },
    stop() {
      generation += 1;
      cancelSchedule();
      releaseCamera();
    },
    resetSession() {
      session = createScanSession({ mode, allowPlainText, limits });
    },
    dispose() {
      if (disposed) return;
      disposed = true;
      generation += 1;
      cancelSchedule();
      releaseCamera();
    },
  };
}
