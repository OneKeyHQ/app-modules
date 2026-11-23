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
    return await binding().decodeImage(path, options.maxPixels);
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
  const { source = { kind: "camera" }, onReady, onText, onError, onEnded } = options ?? {};
  if (source.kind !== "camera" && source.kind !== "file") {
    throw errorWithCode("decoder-unavailable", `Unknown source: ${source.kind}`);
  }
  let latestGeneration = 0;
  let disposed = false;
  const native = new (binding().NativeCapture)(
    source.kind,
    source.kind === "file" ? source.path : undefined,
    source.kind === "file" ? Boolean(source.loop) : undefined,
    (event) => {
      if (disposed || event.generation < latestGeneration) return;
      latestGeneration = event.generation;
      switch (event.kind) {
        case "ready":
          onReady?.(event.generation);
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
  return {
    start() {
      if (disposed) throw errorWithCode("decoder-unavailable", "The capture was disposed");
      native.start();
    },
    stop() {
      if (!disposed) native.stop();
    },
    dispose() {
      if (disposed) return;
      disposed = true;
      native.dispose();
    },
  };
}

module.exports = { decodeImage, createCapture };
