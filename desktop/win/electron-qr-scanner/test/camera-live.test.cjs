"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { test } = require("node:test");
const { runInNewContext } = require("node:vm");

test("manual camera picker restarts an inactive capture after a terminal error", async () => {
  const calls = [];
  const listeners = new Map();
  let options;
  const modules = {
    electron: {
      app: { whenReady: () => Promise.resolve() },
      BrowserWindow: class {
        webContents = { send() {} };
        async loadFile() {}
        isDestroyed() { return false; }
        on() {}
      },
      ipcMain: { on: (event, callback) => listeners.set(event, callback) },
      sharedTexture: { importSharedTexture() { throw new Error("Preview unavailable"); } },
    },
    "node:fs": { writeFileSync() {} },
    "node:path": require("node:path"),
    "node:os": require("node:os"),
    "@ngraveio/bc-ur": { URDecoder: class {} },
    "../index.cjs": {
      listCameras: async () => [],
      shouldFallbackToWeb: (error) => error.code === "preview-unavailable",
      createCapture(captureOptions) {
        options = captureOptions;
        return {
          start: () => calls.push("start"),
          stop: () => calls.push("stop"),
          setDevice: (deviceId) => calls.push(deviceId),
          lentFrames: () => 0,
        };
      },
    },
  };
  await runInNewContext(readFileSync(join(__dirname, "camera-live.cjs"), "utf8"), {
    __dirname,
    process: { argv: [], versions: {} },
    require: (name) => modules[name],
    setInterval() {},
    setTimeout() {},
  });

  const select = (id) => listeners.get("camera-select")(null, id);
  options.onError({ code: "no-camera" });
  select("working-camera");
  select("another-camera");
  options.onFrame({ release: () => calls.push("release") });
  select("camera-after-preview-error");

  assert.deepEqual(calls, [
    "start", "working-camera", "start", "another-camera",
    "release", "stop", "camera-after-preview-error", "start",
  ]);
});
