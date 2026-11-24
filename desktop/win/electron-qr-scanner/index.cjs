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
  if (error && typeof error === "object" && typeof error.code === "string" && ERROR_CODES.has(error.code)) {
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

/**
 * Starts reporting raw QR text from a Windows camera or a video file. Events
 * from an older capture generation are dropped; nothing is delivered after
 * dispose().
 */
function createCapture(options) {
  const { source = { kind: "camera" }, onReady, onText, onError, onEnded, onFrame } =
    options ?? {};
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
  let latestGeneration = 0;
  let disposed = false;
  let native;
  try {
    native = new (binding().NativeCapture)(
      {
        sourceKind: source.kind,
        filePath: source.kind === "file" ? source.path : undefined,
        loopFile: source.kind === "file" ? Boolean(source.loop) : undefined,
        deviceId:
          source.kind === "camera" &&
          typeof source.deviceId === "string" &&
          source.deviceId.length > 0
            ? source.deviceId
            : undefined,
        preferredResolution: source.kind === "camera" ? source.preferredResolution : undefined,
        lockFocusOnDetect: source.kind === "camera" ? Boolean(source.lockFocusOnDetect) : undefined,
        withPreview: typeof onFrame === "function",
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
          if (disposed || event.generation < latestGeneration) {
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
        if (disposed || event.generation < latestGeneration) return;
        latestGeneration = event.generation;
        switch (event.kind) {
          case "ready":
            onReady?.(event.generation, event.info);
            break;
          case "text":
            onText?.(event.text, event.generation);
            break;
          case "error":
            onError?.(
              errorWithCode(
                ERROR_CODES.has(event.code) ? event.code : "decoder-unavailable",
                event.message,
              ),
              event.generation,
            );
            break;
          default:
            onEnded?.(event.generation);
        }
      },
    );
  } catch (error) {
    throw toCodedError(error);
  }
  return {
    start() {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      try {
        native.start();
      } catch (error) {
        throw toCodedError(error);
      }
    },
    stop() {
      if (!disposed) native.stop();
    },
    setDevice(deviceId) {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      if (source.kind !== "camera") {
        throw errorWithCode("decoder-unavailable", "setDevice is only supported for camera sources");
      }
      const normalized = typeof deviceId === "string" && deviceId.length > 0 ? deviceId : undefined;
      try {
        native.setDevice(normalized);
      } catch (error) {
        throw toCodedError(error);
      }
    },
    /** Preview frames currently held by the consumer (at most 3). */
    lentFrames() {
      return disposed ? 0 : native.lentFrames();
    },
    dispose() {
      if (disposed) return;
      disposed = true;
      native.dispose();
    },
  };
}

module.exports = { decodeImage, listCameras, createCapture, shouldFallbackToWeb };
