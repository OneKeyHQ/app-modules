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
  /**
   * Raw text of every decoded frame, before the scan session. The same text
   * is reported again only after 1 s (as the native `onFrameText`); a new
   * text, such as the next UR part, is reported at once.
   */
  onFrameText?: (text: string) => void;
  onProgress?: (progress: number) => void;
  onResult?: (result: ScanResult) => void;
  onError?: (error: QrScannerError | ScanError) => void;
}

export interface WebQrScanner {
  start(): Promise<void>;
  /**
   * Moves a camera source to another device (`listWebCameras()` id; unset
   * means the default back-facing camera). Keeps the scan session, so UR
   * parts collected so far still count. While stopped it only records the
   * choice for the next `start()`.
   */
  switchCamera(deviceId?: string): Promise<void>;
  /** Stops analysis (and the camera, for a camera source); keeps UR parts. */
  stop(): void;
  /** Starts a new scan session: drops collected UR parts. */
  resetSession(): void;
  dispose(): void;
}

/** With a native detector, zxing also runs on every Nth missed frame. */
const ZXING_EVERY_N_FRAMES = 4;

/** Same rule as the native frame gates (`QrFrameTextGate`). */
const REPEAT_INTERVAL_MS = 1000;

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
  let active = false;
  let acquisition: { deviceId?: string; cancelPreview?: () => void } | undefined;
  let mediaAcquisitions = 0;
  const usesBuiltInSession = !handlers.onFrameText || !!handlers.onResult || !!handlers.onProgress;
  let stream: MediaStream | undefined;
  let interruptedCamera: { stream: MediaStream; deviceId?: string } | undefined;
  let frameHandle: number | undefined;
  let timer: ReturnType<typeof setTimeout> | undefined;
  let analysing = false;
  let lastTime = -1;
  let frameIndex = 0;
  let canvas: HTMLCanvasElement | OffscreenCanvas | undefined;
  let engines: QrImageEngine[] = [];
  let deviceId = source.kind === 'camera' ? source.deviceId : undefined;
  /** The camera of the current stream; undefined for the default camera. */
  let streamDeviceId: string | undefined;
  let lastFrameText: string | undefined;
  let lastFrameTextAt = 0;

  function reportFrameText(text: string) {
    if (!handlers.onFrameText) return;
    const now = performance.now();
    if (text === lastFrameText && now - lastFrameTextAt < REPEAT_INTERVAL_MS) return;
    lastFrameText = text;
    lastFrameTextAt = now;
    try {
      handlers.onFrameText(text);
    } catch {
      // A consumer observer cannot interrupt the built-in session.
    }
  }

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
    const frameSession = session;
    const isCurrent = () => runGeneration === generation && frameSession === session;
    frameIndex += 1;
    let failed: { error: unknown } | undefined;
    for (const [index, engine] of engines.entries()) {
      if (index > 0 && !failed && frameIndex % ZXING_EVERY_N_FRAMES !== 0) continue;
      let text: string | null;
      try {
        text = await engine.decode(image);
      } catch (error) {
        failed = { error };
        if (!isCurrent()) return;
        continue;
      }
      if (!isCurrent()) return;
      if (!text) continue;
      reportFrameText(text);
      if (!isCurrent() || !usesBuiltInSession) return;
      const outcome = frameSession.receive(text);
      if (outcome.status === 'progress') handlers.onProgress?.(outcome.progress);
      else if (outcome.status === 'complete') {
        if (outcome.result.kind === 'ur') handlers.onProgress?.(1);
        if (isCurrent()) handlers.onResult?.(outcome.result);
      } else if (outcome.status === 'rejected') handlers.onError?.(outcome.error);
      return;
    }
    if (failed && isCurrent()) {
      const { error } = failed;
      fail('decoder-unavailable', error instanceof Error ? error.message : String(error));
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

  function invalidateAcquisition() {
    const previous = acquisition;
    acquisition = undefined;
    previous?.cancelPreview?.();
  }

  function startAnalysis(nextEngines: QrImageEngine[]) {
    cancelSchedule();
    generation += 1;
    const current = generation;
    engines = nextEngines;
    lastTime = -1;
    lastFrameText = undefined;
    handlers.onReady?.();
    schedule(current);
  }

  const hasLiveCamera = () =>
    stream?.getVideoTracks().some((track) => track.readyState === 'live') ?? false;

  async function acquireCamera(
    requestedId: string | undefined,
    allowDefault: boolean,
    recovering = false
  ) {
    invalidateAcquisition();
    const attempt: NonNullable<typeof acquisition> = { deviceId: requestedId };
    acquisition = attempt;
    const isCurrent = () => acquisition === attempt;
    let candidate: MediaStream | undefined;
    let preview: HTMLVideoElement | undefined;
    let requestedMedia = false;
    let reconcileAfterRelease = false;
    function releaseCandidate() {
      if (!candidate) return;
      if (preview?.srcObject === candidate) preview.srcObject = null;
      candidate.getTracks().forEach((track) => track.stop());
      candidate = undefined;
    }

    try {
      const nextEngines = await getEngines();
      if (!isCurrent()) return;
      if (!navigator.mediaDevices?.getUserMedia) {
        throw new QrScannerError('no-camera', 'getUserMedia is not available');
      }
      const open = (id?: string) => {
        if (!requestedMedia) {
          requestedMedia = true;
          mediaAcquisitions += 1;
        }
        return navigator.mediaDevices.getUserMedia({
          audio: false,
          video: {
            deviceId: id ? { exact: id } : undefined,
            facingMode: id ? undefined : { ideal: 'environment' },
            width: { ideal: 1280 },
            height: { ideal: 720 },
          },
        });
      };
      const previous = stream;
      try {
        candidate = await open(requestedId);
      } catch (error) {
        if (!isCurrent()) {
          reconcileAfterRelease = true;
          return;
        }
        const code = cameraErrorCode(error);
        if (previous && stream === previous && code === 'camera-busy') {
          if (!recovering && !hasLiveCamera()) {
            interruptedCamera = { stream: previous, deviceId: streamDeviceId };
          }
          generation += 1;
          cancelSchedule();
          releaseCamera();
          candidate = await open(requestedId);
        } else if (allowDefault && requestedId && code === 'no-camera') {
          attempt.deviceId = undefined;
          candidate = await open();
        } else {
          throw error;
        }
      }
      if (!isCurrent()) {
        reconcileAfterRelease = true;
        return;
      }

      // A separate video validates the candidate while the working preview
      // keeps rendering and analysing its own stream.
      preview = stream ? document.createElement('video') : video;
      preview.muted = true;
      preview.playsInline = true;
      preview.srcObject = candidate;
      const candidateStream = candidate;
      let timeout: ReturnType<typeof setTimeout> | undefined;
      try {
        const started = await Promise.race([
          preview.play().then(() => true),
          new Promise<false>((resolve) => {
            timeout = setTimeout(() => resolve(false), firstFrameTimeoutMs);
            attempt.cancelPreview = () => {
              releaseCandidate();
              resolve(false);
            };
          }),
        ]);
        if (!isCurrent()) {
          reconcileAfterRelease = true;
          return;
        }
        if (!started) throw new QrScannerError('camera-busy', 'The camera produced no frames');
      } finally {
        clearTimeout(timeout);
        attempt.cancelPreview = undefined;
      }

      // Handoff has no await so a stale request cannot release the new stream.
      const superseded = stream;
      video.srcObject = candidate;
      stream = candidate;
      streamDeviceId = attempt.deviceId;
      candidate = undefined;
      acquisition = undefined;
      interruptedCamera = undefined;
      superseded?.getTracks().forEach((track) => track.stop());
      if (preview !== video) {
        preview.srcObject = null;
        void video.play().catch((error: unknown) => {
          if (stream === candidateStream) {
            fail(cameraErrorCode(error), error instanceof Error ? error.message : String(error));
          }
        });
      }
      startAnalysis(nextEngines);
    } catch (error) {
      if (!isCurrent()) {
        reconcileAfterRelease = requestedMedia;
        return;
      }
      acquisition = undefined;
      releaseCandidate();
      fail(error instanceof QrScannerError ? error.code : cameraErrorCode(error),
        error instanceof Error ? error.message : String(error));
    } finally {
      releaseCandidate();
      // Wait until the current switch settles and stale candidates release
      // the camera before consuming the recovery target.
      if (reconcileAfterRelease && active && stream && !hasLiveCamera()) {
        interruptedCamera = { stream, deviceId: streamDeviceId };
      }
      if (requestedMedia) mediaAcquisitions -= 1;
      if (active && !acquisition && mediaAcquisitions === 0 && interruptedCamera) {
        const interrupted = interruptedCamera;
        interruptedCamera = undefined;
        if ((!stream || stream === interrupted.stream) && !hasLiveCamera()) {
          await acquireCamera(interrupted.deviceId, true, true);
        }
      }
    }
  }

  const scanner: WebQrScanner = {
    async start() {
      if (disposed) throw new QrScannerError('decoder-unavailable', 'The scanner was disposed');
      active = true;
      if (source.kind === 'camera') {
        await acquireCamera(deviceId, true);
        return;
      }
      const current = ++generation;
      cancelSchedule();
      try {
        const nextEngines = await getEngines();
        if (current === generation) startAnalysis(nextEngines);
      } catch (error) {
        if (current === generation) {
          fail('decoder-unavailable', error instanceof Error ? error.message : String(error));
        }
      }
    },
    async switchCamera(nextDeviceId?: string) {
      deviceId = nextDeviceId;
      if (disposed || source.kind !== 'camera' || !active) return;
      if (nextDeviceId === streamDeviceId && hasLiveCamera()) {
        invalidateAcquisition();
        return;
      }
      if (acquisition && nextDeviceId === acquisition.deviceId) return;
      await acquireCamera(nextDeviceId, !hasLiveCamera());
    },
    stop() {
      active = false;
      interruptedCamera = undefined;
      generation += 1;
      invalidateAcquisition();
      cancelSchedule();
      releaseCamera();
    },
    resetSession() {
      session = createScanSession({ mode, allowPlainText, limits });
      lastFrameText = undefined;
    },
    dispose() {
      if (disposed) return;
      disposed = true;
      scanner.stop();
    },
  };
  return scanner;
}
