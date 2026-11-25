"use strict";

const isWindows = process.platform === "win32";
let scanner;
if (process.platform === "darwin") {
  scanner = require("./mac/index.cjs");
} else if (isWindows) {
  scanner = require("./win/index.cjs");
} else {
  throw Object.assign(new Error("Native QR scanner is unavailable on this platform"), {
    code: "decoder-unavailable",
  });
}

exports.decodeImage = scanner.decodeImage;
exports.listCameras = scanner.listCameras;
exports.shouldFallbackToWeb = scanner.shouldFallbackToWeb;
exports.createCapture = function createCapture(options) {
  if (!isWindows) return scanner.createCapture(options);
  const onReady = options?.onReady;
  if (!onReady) return scanner.createCapture(options);
  return scanner.createCapture({
    source: options.source,
    onText: options.onText,
    onError: options.onError,
    onEnded: options.onEnded,
    onFrame: options.onFrame,
    onDiagnostic: options.onDiagnostic,
    onReady: (generation, info, deviceId) => onReady(generation, deviceId, info),
  });
};
