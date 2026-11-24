"use strict";

if (process.platform === "darwin") {
  module.exports = require("./mac/index.cjs");
} else if (process.platform === "win32") {
  const scanner = require("./win/index.cjs");
  module.exports = {
    ...scanner,
    createCapture(options) {
      const onReady = options?.onReady;
      if (!onReady) return scanner.createCapture(options);
      return scanner.createCapture({
        ...options,
        onReady: (generation, info, deviceId) => onReady(generation, deviceId, info),
      });
    },
  };
} else {
  throw Object.assign(new Error("Native QR scanner is unavailable on this platform"), {
    code: "decoder-unavailable",
  });
}
