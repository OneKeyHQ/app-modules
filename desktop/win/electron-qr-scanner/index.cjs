"use strict";

const ERROR_CODES = new Set([
  "permission-denied",
  "no-camera",
  "camera-busy",
  "decoder-unavailable",
  "preview-unavailable",
  "invalid-image",
  "resource-limit",
]);

const NON_FALLBACK_CODES = new Set([
  "permission-denied",
  "no-camera",
  "camera-busy",
  "invalid-image",
  "resource-limit",
]);

const EMPTY_STATS = Object.freeze({
  framesCaptured: 0,
  previewFramesEmitted: 0,
  previewFramesSkipped: 0,
  decodeAttempts: 0,
  decodeHits: 0,
  maxLent: 0,
});

let nativeBinding;

function errorWithCode(code, message) {
  return Object.assign(new Error(message), { code });
}

function binding() {
  if (process.platform !== "win32" || !["x64", "arm64"].includes(process.arch)) {
    throw errorWithCode(
      "decoder-unavailable",
      "electron-qr-scanner requires Windows x64 or arm64",
    );
  }
  if (!nativeBinding) {
    try {
      nativeBinding = require(`./electron-qr-scanner.win32-${process.arch}-msvc.node`);
    } catch (error) {
      const reason = error instanceof Error ? error.message : String(error);
      throw errorWithCode(
        "decoder-unavailable",
        `Failed to load native Windows QR scanner binary: ${reason}`,
      );
    }
  }
  return nativeBinding;
}

/** Native errors carry "<code>: <reason>" in the message. */
function toCodedError(error) {
  if (
    error &&
    typeof error === "object" &&
    typeof error.code === "string" &&
    ERROR_CODES.has(error.code)
  ) {
    return error;
  }
  const message = error instanceof Error ? error.message : String(error);
  const match = /^([a-z-]+):\s*(.*)$/s.exec(message);
  if (match && ERROR_CODES.has(match[1])) return errorWithCode(match[1], match[2]);
  return errorWithCode("decoder-unavailable", message);
}

/**
 * Decides whether the app should stop/dispose the native Windows capture and
 * fall back once to `@onekeyfe/react-native-qr-scanner/web`.
 */
function shouldFallbackToWeb(error) {
  if (!error) return false;
  const code =
    typeof error === "object" && typeof error.code === "string" ? error.code : undefined;
  if (code && NON_FALLBACK_CODES.has(code)) return false;
  return true;
}

async function decodeImage(path, options = {}) {
  if (typeof path !== "string" || path.length === 0) {
    throw errorWithCode("invalid-image", "path must be a non-empty string");
  }
  try {
    return await binding().decodeImage(path, options.maxPixels, options.engines);
  } catch (error) {
    throw toCodedError(error);
  }
}

async function listCameras() {
  try {
    return await binding().listCameras();
  } catch (error) {
    throw toCodedError(error);
  }
}

function normalizeReadyInfo(info) {
  if (!info) return undefined;
  return {
    width: info.width,
    height: info.height,
    fps: info.fps,
    subtype: info.subtype,
    cameraCount: info.cameraCount,
    cameraIndex: info.cameraIndex ?? null,
    isDefaultCamera: Boolean(info.isDefaultCamera),
    focusSupported: Boolean(info.focusSupported),
    autoFocusSupported: Boolean(info.autoFocusSupported),
    exposureSupported: Boolean(info.exposureSupported),
  };
}

/**
 * Starts reporting raw QR text from a Windows camera or a video file. Events
 * are delivered only for the capture generation selected by the wrapper.
 */
function createCapture(options) {
  const {
    source = { kind: "camera" },
    onReady,
    onText,
    onError,
    onEnded,
    onFrame,
    onDiagnostic,
  } = options ?? {};
  if (source.kind !== "camera" && source.kind !== "file") {
    throw errorWithCode("decoder-unavailable", `Unknown source: ${source.kind}`);
  }
  if (
    source.kind === "camera" &&
    source.preferredResolution !== undefined &&
    source.preferredResolution !== "720p" &&
    source.preferredResolution !== "1080p"
  ) {
    throw errorWithCode(
      "decoder-unavailable",
      `Unsupported preferredResolution: ${source.preferredResolution}`,
    );
  }

  const withPreview = typeof onFrame === "function";
  const preferredResolution =
    source.kind === "camera" ? (source.preferredResolution ?? "720p") : undefined;
  const lockFocusOnDetect =
    source.kind === "camera" ? Boolean(source.lockFocusOnDetect) : undefined;
  let hasCustomDeviceId =
    source.kind === "camera" && typeof source.deviceId === "string" && source.deviceId.length > 0;

  let activeGeneration = null;
  let disposed = false;
  let startedAt = null;
  let native;

  const emitDiagnostic = (payload) => {
    if (typeof onDiagnostic !== "function") return;
    try {
      onDiagnostic(payload);
    } catch {
      // Diagnostic logging must never break scanning or leak preview frames.
    }
  };

  const accepts = (event) => !disposed && event.generation === activeGeneration;

  try {
    native = new (binding().NativeCapture)(
      {
        sourceKind: source.kind,
        filePath: source.kind === "file" ? source.path : undefined,
        loopFile: source.kind === "file" ? Boolean(source.loop) : undefined,
        deviceId: hasCustomDeviceId ? source.deviceId : undefined,
        preferredResolution: source.kind === "camera" ? source.preferredResolution : undefined,
        lockFocusOnDetect,
        withPreview,
      },
      (event) => {
        if (event.kind === "frame") {
          // Every lent frame must go back to the pool, delivered or not.
          let released = false;
          const release = () => {
            if (released || disposed) return;
            released = true;
            native.releaseFrame(event.token);
          };
          if (!accepts(event)) {
            release();
            return;
          }
          try {
            onFrame({
              ntHandle: event.ntHandle,
              width: event.width,
              height: event.height,
              generation: event.generation,
              release,
            });
          } catch (error) {
            release();
            throw error;
          }
          return;
        }
        if (!accepts(event)) return;
        switch (event.kind) {
          case "ready": {
            const info = normalizeReadyInfo(event.info);
            emitDiagnostic({
              event: "ready",
              level: "info",
              backend: "windows-native",
              generation: event.generation,
              readyMs: startedAt === null ? 0 : Math.max(0, Date.now() - startedAt),
              cameraCount: info?.cameraCount ?? 0,
              cameraIndex: info?.cameraIndex ?? null,
              isDefaultCamera: info?.isDefaultCamera ?? false,
              width: info?.width ?? 0,
              height: info?.height ?? 0,
              fps: Math.round(info?.fps ?? 0),
              subtype: info?.subtype ?? "unknown",
              focusSupported: info?.focusSupported ?? false,
              autoFocusSupported: info?.autoFocusSupported ?? false,
              exposureSupported: info?.exposureSupported ?? false,
            });
            if (accepts(event)) onReady?.(event.generation, info, event.info?.deviceId);
            break;
          }
          case "text":
            onText?.(event.text, event.generation);
            break;
          case "error": {
            const code = ERROR_CODES.has(event.code) ? event.code : "decoder-unavailable";
            const coded = errorWithCode(code, event.message);
            emitDiagnostic({
              event: "error",
              level: "warn",
              backend: "windows-native",
              generation: event.generation,
              code,
              fallbackToWeb: shouldFallbackToWeb(coded),
            });
            if (accepts(event)) onError?.(coded, event.generation);
            break;
          }
          default:
            onEnded?.(event.generation);
        }
      },
    );
  } catch (error) {
    const coded = toCodedError(error);
    emitDiagnostic({
      event: "error",
      level: "warn",
      backend: "windows-native",
      generation: 0,
      code: coded.code,
      fallbackToWeb: shouldFallbackToWeb(coded),
    });
    throw coded;
  }

  const readStats = () => (disposed ? { ...EMPTY_STATS } : native.stats());

  const emitStopDiagnostic = (reason, generation, snapshot, lentAfterStop) => {
    if (generation === null) return;
    const durationMs = startedAt === null ? 0 : Math.max(0, Date.now() - startedAt);
    startedAt = null;
    emitDiagnostic({
      event: "stop",
      level: "info",
      backend: "windows-native",
      reason,
      generation,
      durationMs,
      framesCaptured: snapshot.framesCaptured,
      previewFramesEmitted: snapshot.previewFramesEmitted,
      previewFramesSkipped: snapshot.previewFramesSkipped,
      decodeAttempts: snapshot.decodeAttempts,
      decodeHits: snapshot.decodeHits,
      maxLent: snapshot.maxLent,
      lentAfterStop,
    });
  };

  return {
    start() {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      const previousGeneration = activeGeneration;
      activeGeneration = null;
      try {
        activeGeneration = native.start();
        startedAt = Date.now();
        emitDiagnostic({
          event: "start",
          level: "info",
          backend: "windows-native",
          sourceKind: source.kind,
          ...(source.kind === "camera"
            ? {
                preferredResolution,
                lockFocusOnDetect,
                hasCustomDeviceId,
              }
            : {}),
          withPreview,
        });
      } catch (error) {
        const coded = toCodedError(error);
        emitDiagnostic({
          event: "error",
          level: "warn",
          backend: "windows-native",
          generation: previousGeneration ?? 0,
          code: coded.code,
          fallbackToWeb: shouldFallbackToWeb(coded),
        });
        throw coded;
      }
    },
    stop() {
      if (disposed) return;
      const generation = activeGeneration;
      activeGeneration = null;
      native.stop();
      emitStopDiagnostic("stop", generation, native.stats(), native.lentFrames());
    },
    setDevice(deviceId) {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      if (source.kind !== "camera") {
        throw errorWithCode("decoder-unavailable", "setDevice is only supported for camera sources");
      }
      const normalized = typeof deviceId === "string" && deviceId.length > 0 ? deviceId : undefined;
      activeGeneration = null;
      try {
        activeGeneration = native.setDevice(normalized) ?? null;
      } catch (error) {
        throw toCodedError(error);
      }
      hasCustomDeviceId = normalized !== undefined;
      if (activeGeneration !== null) startedAt = Date.now();
      emitDiagnostic({
        event: "switch-device",
        level: "info",
        backend: "windows-native",
        hasCustomDeviceId,
      });
    },
    /** Preview frames currently held by the consumer (at most 3). */
    lentFrames() {
      return disposed ? 0 : native.lentFrames();
    },
    /** Privacy-safe session counters. */
    stats() {
      return readStats();
    },
    dispose() {
      if (disposed) return;
      const snapshot = native.stats();
      const generation = activeGeneration;
      activeGeneration = null;
      disposed = true;
      native.dispose();
      emitStopDiagnostic("dispose", generation, snapshot, 0);
    },
  };
}

module.exports = { decodeImage, listCameras, createCapture, shouldFallbackToWeb };
