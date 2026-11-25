"use strict";

const ERROR_CODES = new Set([
  "permission-denied",
  "no-camera",
  "camera-busy",
  "decoder-unavailable",
  "invalid-image",
  "resource-limit",
]);

let nativeBinding;

function errorWithCode(code, message) {
  return Object.assign(new Error(message), { code });
}

function binding() {
  if (process.platform !== "darwin" || !["arm64", "x64"].includes(process.arch)) {
    throw errorWithCode(
      "decoder-unavailable",
      "electron-qr-scanner requires macOS arm64 or x64",
    );
  }
  nativeBinding ??= require(`./electron-qr-scanner.darwin-${process.arch}.node`);
  return nativeBinding;
}

/** Native errors carry "<code>: <reason>" in the message. */
function toCodedError(error) {
  const message = error instanceof Error ? error.message : String(error);
  const match = /^([a-z-]+):\s*(.*)$/s.exec(message);
  if (match && ERROR_CODES.has(match[1])) return errorWithCode(match[1], match[2]);
  return errorWithCode("decoder-unavailable", message);
}

async function decodeImage(path, options = {}) {
  if (typeof path !== "string" || path.length === 0) {
    throw errorWithCode("invalid-image", "path must be a non-empty string");
  }
  try {
    // `engines` is for diagnostics and tests; default is Vision then ZXing-C++.
    return await binding().decodeImage(path, options.maxPixels, options.engines);
  } catch (error) {
    throw toCodedError(error);
  }
}

/**
 * Starts reporting raw QR text from the camera or a video file. Events from
 * an older capture generation are dropped; nothing is delivered after
 * dispose().
 */
function createCapture(options) {
  const { source = { kind: "camera" }, onReady, onText, onError, onEnded, onFrame } =
    options ?? {};
  if (source.kind !== "camera" && source.kind !== "file") {
    throw errorWithCode("decoder-unavailable", `Unknown source: ${source.kind}`);
  }
  let activeGeneration;
  let disposed = false;
  const native = new (binding().NativeCapture)(
    source.kind,
    source.kind === "file" ? source.path : source.deviceId,
    source.kind === "file" ? Boolean(source.loop) : undefined,
    typeof onFrame === "function",
    (event) => {
      if (event.kind === "frame") {
        // Every lent frame must go back to the pool, delivered or not.
        let released = false;
        const release = () => {
          if (released || disposed) return;
          released = true;
          native.releaseFrame(event.token);
        };
        if (disposed || event.generation !== activeGeneration) {
          release();
          return;
        }
        try {
          onFrame({
            ioSurface: event.ioSurface,
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
      if (disposed || event.generation !== activeGeneration) return;
      switch (event.kind) {
        case "ready":
          onReady?.(event.generation, event.deviceId);
          break;
        case "text":
          onText?.(event.text, event.generation);
          break;
        case "error":
          activeGeneration = undefined;
          onError?.(
            errorWithCode(
              ERROR_CODES.has(event.code) ? event.code : "decoder-unavailable",
              event.message,
            ),
            event.generation,
          );
          break;
        default:
          activeGeneration = undefined;
          onEnded?.(event.generation);
      }
    },
  );
  return {
    start() {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      if (activeGeneration === undefined) activeGeneration = native.start();
    },
    stop() {
      if (disposed) return;
      activeGeneration = undefined;
      native.stop();
    },
    focus(x = 0.5, y = 0.5) {
      if (disposed || activeGeneration === undefined || typeof native.focus !== "function") {
        return false;
      }
      return Boolean(native.focus(x, y));
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

/**
 * Cameras on this Mac: the built-in camera ("front") and USB or Continuity
 * cameras ("external"). Pass an id as `source.deviceId`.
 */
function listCameras() {
  return JSON.parse(binding().listCameras());
}

module.exports = { decodeImage, createCapture, listCameras };
