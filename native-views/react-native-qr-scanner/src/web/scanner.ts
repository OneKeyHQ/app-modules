import { QrScannerError } from '../errors';
import { createScanSession } from '../ur';
import { getEngines } from './engines';
import { fitToPixelBudget } from './imageOps';

import type { QrScannerErrorCode } from '../errors';
import type { QrDetectedSymbol, QrFrameSymbolsEvent } from '../symbols';
import type { ScanError, ScanMode, ScanResult, UrLimits } from '../ur';
import type { QrImageEngine, RgbaImage } from './imageOps';

export type WebScanSource =
  /** getUserMedia; the scanner owns and stops the stream. */
  | { kind: 'camera'; deviceId?: string }
  /** A video element the caller already plays (a file, a stream it owns). */
  | { kind: 'element' };

export type WebQrFrameSymbolsEvent = Omit<QrFrameSymbolsEvent, 'generation'> & {
  /**
   * Exact RGBA frame pixels that produced `symbols`. Consumers should render
   * this frame when freezing for multi-QR selection so markers stay locked to
   * the decoded frame even if the video advanced during async decoding.
   */
  frame?: RgbaImage;
};

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
  /**
   * When true, detect up to 8 QR symbols per frame and deliver them via
   * `onFrameSymbols` without splitting through the 1 s repeat throttle.
   * Default false.
   */
  multiSymbol?: boolean;
  /** Whole-frame symbols in normalized `frame` coordinates (top-left origin). */
  onFrameSymbols?: (event: WebQrFrameSymbolsEvent) => void;
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
  /**
   * Dynamically toggles multi-symbol detection without restarting the camera
   * or incrementing the capture generation. Takes effect on the next frame.
   */
  setMultiSymbol(enabled: boolean): void;
  /**
   * Freezes or unfreezes preview playback and frame analysis while keeping the
   * underlying MediaStream alive (zero black flash on resume).
   */
  setFrozen(frozen: boolean): void;
  /**
   * Triggers tap-to-focus on the active camera track when the hardware
   * reports autofocus capability (`single-shot` or `continuous`). `point`
   * uses normalized [0, 1] preview coordinates. Resolves true when focus was
   * triggered, or false when the camera is fixed-focus / inactive.
   */
  focus(point?: { x: number; y: number }): Promise<boolean>;
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

/** Hold single-shot tap focus before returning to continuous autofocus. */
const FOCUS_HOLD_MS = 5000;

type FocusTrackCapabilities = MediaTrackCapabilities & {
  focusMode?: string[];
};

type FocusTrackSettings = MediaTrackSettings & {
  focusMode?: string;
  pointsOfInterest?: { x: number; y: number }[];
};

type FocusConstraintSet = MediaTrackConstraintSet & {
  focusMode?: string;
  pointsOfInterest?: { x: number; y: number }[];
};

const NON_IMAGE_CAPTURE_CONSTRAINT_KEYS = new Set([
  'width',
  'height',
  'aspectRatio',
  'frameRate',
  'facingMode',
  'resizeMode',
  'deviceId',
  'groupId',
]);

function stripFocusAndBasicVideoKeys(source: Record<string, unknown>): MediaTrackConstraintSet {
  const result: Record<string, unknown> = {};
  for (const [key, value] of Object.entries(source)) {
    if (
      key === 'advanced' ||
      key === 'focusMode' ||
      key === 'pointsOfInterest' ||
      NON_IMAGE_CAPTURE_CONSTRAINT_KEYS.has(key) ||
      value === undefined
    ) {
      continue;
    }
    result[key] = value;
  }
  return result as MediaTrackConstraintSet;
}

function buildFocusConstraints(
  track: MediaStreamTrack,
  focusConstraint: FocusConstraintSet
): MediaTrackConstraints {
  const current = (track.getConstraints?.() ?? {}) as Record<string, unknown>;
  const topLevel = stripFocusAndBasicVideoKeys(current);
  const preservedAdvanced: MediaTrackConstraintSet[] = [];
  if (Array.isArray(current.advanced)) {
    for (const entry of current.advanced) {
      if (!entry || typeof entry !== 'object') continue;
      const cleaned = stripFocusAndBasicVideoKeys(entry as Record<string, unknown>);
      if (Object.keys(cleaned).length > 0) {
        preservedAdvanced.push(cleaned);
      }
    }
  }
  return {
    ...topLevel,
    advanced: [...preservedAdvanced, focusConstraint],
  };
}

function didApplyFocusConstraint(
  track: MediaStreamTrack,
  expectedMode: string,
  expectedPoint?: { x: number; y: number }
): boolean {
  if (typeof track.getSettings !== 'function') return true;
  const settings = track.getSettings() as FocusTrackSettings | undefined;
  if (!settings) return true;
  if ('focusMode' in settings && settings.focusMode !== expectedMode) {
    return false;
  }
  if (expectedPoint && 'pointsOfInterest' in settings) {
    const points = settings.pointsOfInterest;
    if (!Array.isArray(points) || points.length === 0) return false;
    const first = points[0];
    if (
      !first ||
      Math.abs(first.x - expectedPoint.x) > 1e-3 ||
      Math.abs(first.y - expectedPoint.y) > 1e-3
    ) {
      return false;
    }
  }
  return true;
}

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
  let multiSymbol = options.multiSymbol === true;
  let frozen = false;
  let acquisition: { deviceId?: string; cancelPreview?: () => void } | undefined;
  let mediaAcquisitions = 0;
  let staleMediaSettlements = 0;
  const usesBuiltInSession =
    (!handlers.onFrameText && !handlers.onFrameSymbols) ||
    !!handlers.onResult ||
    !!handlers.onProgress;
  let stream: MediaStream | undefined;
  type RecoveryTarget = { stream: MediaStream; deviceId?: string; blockedAt?: number };
  let interruptedCamera: RecoveryTarget | undefined;
  let frameHandle: number | undefined;
  let timer: ReturnType<typeof setTimeout> | undefined;
  let focusResetTimer: ReturnType<typeof setTimeout> | undefined;
  let nextFocusRequestId = 0;
  let latestAppliedFocusRequestId = 0;
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
    if (frozen) return;
    const image = readFrame();
    if (!image) return;
    const frameSession = session;
    const isCurrent = () =>
      runGeneration === generation && frameSession === session && !frozen;
    const wantMulti = multiSymbol;
    frameIndex += 1;
    let failed: { error: unknown } | undefined;
    for (const [index, engine] of engines.entries()) {
      if (index > 0 && !failed && frameIndex % ZXING_EVERY_N_FRAMES !== 0) continue;
      if (handlers.onFrameSymbols) {
        let symbols: QrDetectedSymbol[];
        try {
          symbols = engine.decodeSymbols
            ? await engine.decodeSymbols(image, wantMulti)
            : await engine.decode(image).then((t) =>
                t ? [{ text: t, centerX: 0.5, centerY: 0.5 }] : []
              );
        } catch (error) {
          failed = { error };
          if (!isCurrent()) return;
          continue;
        }
        if (!isCurrent()) return;
        if (symbols.length === 0) continue;
        try {
          handlers.onFrameSymbols({
            symbols,
            coordinateSpace: 'frame',
            frameWidth: image.width,
            frameHeight: image.height,
            frame: image,
          });
        } catch {
          // A consumer observer cannot interrupt the built-in session.
        }
        if (!isCurrent()) return;
        const firstText = symbols[0]?.text;
        if (firstText) {
          reportFrameText(firstText);
          if (!isCurrent() || !usesBuiltInSession) return;
          const outcome = frameSession.receive(firstText);
          if (outcome.status === 'progress') handlers.onProgress?.(outcome.progress);
          else if (outcome.status === 'complete') {
            if (outcome.result.kind === 'ur') handlers.onProgress?.(1);
            if (isCurrent()) handlers.onResult?.(outcome.result);
          } else if (outcome.status === 'rejected') handlers.onError?.(outcome.error);
        }
        return;
      }

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
    if (runGeneration !== generation || disposed || frozen) return;
    if (frameHandle !== undefined || timer !== undefined) return;
    const tick = () => {
      frameHandle = undefined;
      timer = undefined;
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

  function clearFocusResetTimer() {
    if (focusResetTimer !== undefined) clearTimeout(focusResetTimer);
    focusResetTimer = undefined;
  }

  function releaseCamera() {
    clearFocusResetTimer();
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
    recovery?: RecoveryTarget
  ) {
    const settlementsAtRecoveryStart = recovery && mediaAcquisitions > 0
      ? staleMediaSettlements
      : undefined;
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
      if (recovery && stream === recovery.stream) {
        generation += 1;
        cancelSchedule();
        releaseCamera();
      }
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
          if (!recovery && !hasLiveCamera()) {
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
      clearFocusResetTimer();
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
        if (!frozen) {
          void video.play().catch((error: unknown) => {
            if (stream === candidateStream) {
              fail(cameraErrorCode(error), error instanceof Error ? error.message : String(error));
            }
          });
        }
      }
      if (frozen) {
        video.pause();
      }
      startAnalysis(nextEngines);
    } catch (error) {
      if (!isCurrent()) {
        reconcileAfterRelease = requestedMedia;
        return;
      }
      acquisition = undefined;
      const failedToOpen = requestedMedia && !candidate;
      releaseCandidate();
      const code = error instanceof QrScannerError ? error.code : cameraErrorCode(error);
      if (
        recovery && failedToOpen && code === 'camera-busy' && settlementsAtRecoveryStart !== undefined
      ) {
        interruptedCamera = { ...recovery, blockedAt: settlementsAtRecoveryStart };
        return;
      }
      if (recovery && interruptedCamera === recovery) interruptedCamera = undefined;
      fail(code, error instanceof Error ? error.message : String(error));
    } finally {
      releaseCandidate();
      if (reconcileAfterRelease) {
        staleMediaSettlements += 1;
        if (active && stream && !hasLiveCamera() && interruptedCamera?.stream !== stream) {
          interruptedCamera = { stream, deviceId: streamDeviceId };
        }
      }
      if (requestedMedia) mediaAcquisitions -= 1;
      const interrupted = interruptedCamera;
      // An unanswered permission prompt may never settle. Only park a
      // restore after a busy result, and let each stale settlement wake it.
      if (active && !acquisition && interrupted && interrupted.blockedAt !== staleMediaSettlements) {
        if ((!stream || stream === interrupted.stream) && !hasLiveCamera()) {
          // A newer switch can supersede this restore and still need its target.
          await acquireCamera(interrupted.deviceId, true, interrupted);
        } else {
          interruptedCamera = undefined;
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
      const activeTrackId = stream
        ?.getVideoTracks?.()[0]
        ?.getSettings?.().deviceId;
      if (
        (nextDeviceId === streamDeviceId ||
          (nextDeviceId !== undefined && nextDeviceId === activeTrackId)) &&
        hasLiveCamera()
      ) {
        streamDeviceId = nextDeviceId;
        invalidateAcquisition();
        return;
      }
      if (acquisition && nextDeviceId === acquisition.deviceId) return;
      await acquireCamera(nextDeviceId, !hasLiveCamera());
    },
    setMultiSymbol(enabled: boolean) {
      multiSymbol = enabled;
    },
    setFrozen(nextFrozen: boolean) {
      if (disposed || frozen === nextFrozen) return;
      frozen = nextFrozen;
      if (frozen) {
        cancelSchedule();
        video.pause();
        return;
      }
      if (active) {
        lastTime = -1;
        void video.play().catch(() => {});
        // Always schedule current generation: `schedule` is idempotent via
        // frameHandle/timer, and an in-flight decode from a previous camera
        // generation will not schedule the new generation when it settles.
        schedule(generation);
      }
    },
    async focus(point?: { x: number; y: number }): Promise<boolean> {
      if (disposed || !active || source.kind !== 'camera' || !stream) return false;
      const targetStream = stream;
      const targetGeneration = generation;
      const track = targetStream
        .getVideoTracks()
        .find((candidate) => candidate.readyState === 'live');
      if (!track || typeof track.applyConstraints !== 'function') return false;
      const capabilities = track.getCapabilities?.() as FocusTrackCapabilities | undefined;
      const focusModes = capabilities?.focusMode;
      if (!Array.isArray(focusModes)) return false;
      const supportsSingleShot = focusModes.includes('single-shot');
      const supportsContinuous = focusModes.includes('continuous');
      if (!supportsSingleShot && !supportsContinuous) return false;

      const focusMode = supportsSingleShot ? 'single-shot' : 'continuous';
      const normalizedPoint =
        point && Number.isFinite(point.x) && Number.isFinite(point.y)
          ? {
              x: Math.min(1, Math.max(0, point.x)),
              y: Math.min(1, Math.max(0, point.y)),
            }
          : undefined;
      const requestId = ++nextFocusRequestId;
      const isCurrentTarget = () =>
        active &&
        !disposed &&
        stream === targetStream &&
        generation === targetGeneration &&
        track.readyState === 'live';
      const applyMode = (withPoint: boolean) => {
        const constraint: FocusConstraintSet =
          withPoint && normalizedPoint
            ? { focusMode, pointsOfInterest: [normalizedPoint] }
            : { focusMode };
        return track.applyConstraints(buildFocusConstraints(track, constraint));
      };
      try {
        if (normalizedPoint) {
          let appliedWithPoint = false;
          try {
            await applyMode(true);
            appliedWithPoint =
              isCurrentTarget() && didApplyFocusConstraint(track, focusMode, normalizedPoint);
          } catch {
            appliedWithPoint = false;
          }
          if (!appliedWithPoint) {
            if (!isCurrentTarget()) return false;
            await applyMode(false);
          }
        } else {
          await applyMode(false);
        }
      } catch {
        return false;
      }
      if (!isCurrentTarget() || !didApplyFocusConstraint(track, focusMode)) {
        return false;
      }
      if (requestId >= latestAppliedFocusRequestId) {
        latestAppliedFocusRequestId = requestId;
        clearFocusResetTimer();
        if (supportsSingleShot && supportsContinuous) {
          focusResetTimer = setTimeout(() => {
            focusResetTimer = undefined;
            if (!isCurrentTarget() || latestAppliedFocusRequestId !== requestId) {
              return;
            }
            const continuousConstraint: FocusConstraintSet = { focusMode: 'continuous' };
            void track
              .applyConstraints(buildFocusConstraints(track, continuousConstraint))
              .catch(() => {});
          }, FOCUS_HOLD_MS);
        }
      }
      return true;
    },
    stop() {
      active = false;
      frozen = false;
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
