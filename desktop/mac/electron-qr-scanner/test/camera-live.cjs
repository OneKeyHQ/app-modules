"use strict";

// Manual real-camera check: preview through sharedTexture, live detection,
// animated UR assembly. Hold a code (e.g. the test materials page on a phone)
// in front of the camera. Writes a JSON report to --out and quits when a UR
// completes or after --seconds (default 120).
//   open -n -W -a <Electron.app> --args test/camera-live.cjs --out /tmp/r.json
const { app, BrowserWindow, ipcMain, sharedTexture } = require("electron");
const { readFileSync, writeFileSync } = require("node:fs");
const { join } = require("node:path");
const animated = require("./fixtures/animated-ur.json");
const staticFixture = readFileSync(join(
  __dirname, "../../../../native-views/react-native-qr-scanner/ios/tests/fixtures/lightning-payload.txt",
), "utf8");
const api = require("../index.cjs");

const arg = (name, fallback) => {
  const index = process.argv.indexOf(name);
  return index > 0 ? process.argv[index + 1] : fallback;
};
const outPath = arg("--out", join(require("node:os").tmpdir(), "qr-camera-live.json"));
const seconds = Number(arg("--seconds", "120"));

function fnv1a(bytes) {
  let hash = 0x811c9dc5;
  for (const byte of bytes) hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  return hash.toString(16).padStart(8, "0");
}

app.whenReady().then(async () => {
  const { createUrAssembler } = await import("@onekeyfe/react-native-qr-scanner/ur");
  const win = new BrowserWindow({
    width: 800,
    height: 520,
    title: "electron-qr-scanner camera",
    webPreferences: {
      preload: join(__dirname, "camera-live-preload.cjs"),
      sandbox: true,
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  await win.loadFile(join(__dirname, "camera-live.html"));
  const started = Date.now();
  const report = {
    electron: process.versions.electron,
    readyMs: null,
    errors: [],
    drawn: 0,
    maxLent: 0,
    staticDetections: 0,
    staticFixtureMatches: 0,
    malformedUrFrames: 0,
    ur: null,
  };
  const decoder = createUrAssembler();
  let progress = 0;
  let acceptedParts = 0;
  let urStartedAt;
  let transfer;
  let finished = false;

  const status = () => {
    const lines = [
      report.readyMs === null ? "waiting for camera…" : `camera ready in ${report.readyMs} ms · frames drawn ${report.drawn}`,
      acceptedParts ? `UR parts ${acceptedParts} · ${Math.round(progress * 100)}%` : "",
      report.ur ? `UR complete in ${report.ur.ms} ms · fixture ${report.ur.fixtureMatch ? "matched" : "did not match"}` : "",
      `static detections ${report.staticDetections} · fixture matches ${report.staticFixtureMatches}`,
      ...report.errors.slice(-1).map((e) => `error ${e.code}: ${e.message}`),
    ].filter(Boolean);
    if (!win.isDestroyed()) win.webContents.send("camera-status", lines.join("\n"));
  };
  const onDrawn = (_event, count) => { report.drawn = count; };
  ipcMain.on("camera-drawn", onDrawn);
  // Keep the app alive through the final slot measurement even if the window closes.
  app.on("window-all-closed", finish);

  let statusTimer;
  let deadline;
  let completeTimer;

  async function finish() {
    if (finished) return;
    finished = true;
    clearInterval(statusTimer);
    clearTimeout(deadline);
    clearTimeout(completeTimer);
    capture.stop();
    const drainDeadline = Date.now() + 1500;
    while ((transfer || capture.lentFrames() > 0) && Date.now() < drainDeadline) {
      await new Promise((resolve) => setTimeout(resolve, 25));
    }
    report.lentBeforeDispose = capture.lentFrames();
    capture.dispose();
    ipcMain.removeListener("camera-drawn", onDrawn);
    report.totalMs = Date.now() - started;
    try {
      writeFileSync(outPath, JSON.stringify(report, null, 2));
      app.exit(0);
    } catch (error) {
      console.error(`Could not write camera report to ${outPath}:`, error.message);
      app.exit(1);
    }
  }

  const capture = api.createCapture({
    onReady: () => {
      report.readyMs = Date.now() - started;
      status();
    },
    onError: (error) => {
      report.errors.push({ code: error.code, message: error.message });
      status();
    },
    onText: (text) => {
      if (/^ur:/i.test(text)) {
        const outcome = decoder.receive(text);
        if (outcome.status === "progress") {
          urStartedAt ??= Date.now();
          progress = outcome.progress;
          acceptedParts = outcome.receivedParts;
        } else if (outcome.status === "complete" && outcome.result.kind === "ur") {
          const { type, cbor } = outcome.result;
          report.ur = {
            fixtureMatch: type === animated.type &&
              cbor.length === animated.bytes && fnv1a(cbor) === animated.fnv1a,
            ms: Date.now() - (urStartedAt ?? Date.now()),
            distinctParts: decoder.stats().accepted,
          };
          progress = 1;
          completeTimer = setTimeout(finish, 2000);
        } else if (outcome.status === "rejected") {
          report.errors.push(outcome.error);
          decoder.reset();
          urStartedAt = undefined;
          acceptedParts = 0;
          progress = 0;
        } else if (outcome.status === "ignored" && outcome.reason === "malformed") {
          report.malformedUrFrames += 1;
        }
      } else {
        report.staticDetections += 1;
        if (text === staticFixture) report.staticFixtureMatches += 1;
      }
      status();
    },
    onFrame: (frame) => {
      report.maxLent = Math.max(report.maxLent, capture.lentFrames());
      if (finished || transfer || win.isDestroyed()) {
        frame.release();
        return;
      }
      transfer = (async () => {
        let imported;
        try {
          imported = sharedTexture.importSharedTexture({
            textureInfo: {
              pixelFormat: "bgra",
              codedSize: { width: frame.width, height: frame.height },
              handle: { ioSurface: frame.ioSurface },
            },
            allReferencesReleased: () => frame.release(),
          });
          await sharedTexture.sendSharedTexture({
            frame: win.webContents.mainFrame, importedSharedTexture: imported,
          });
        } catch (error) {
          report.errors.push({ code: "preview-transfer", message: error.message });
          status();
        } finally {
          if (imported) imported.release();
          else frame.release();
        }
      })().finally(() => { transfer = undefined; });
    },
  });
  capture.start();
  statusTimer = setInterval(status, 500);
  deadline = setTimeout(finish, seconds * 1000);
  win.on("closed", finish);
}).catch((error) => {
  console.error("Could not start camera check:", error.message);
  app.exit(1);
});
