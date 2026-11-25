"use strict";

// Manual real-camera check: preview through sharedTexture (ntHandle), camera
// listing and switching, live detection, animated UR assembly.
//   npx electron test/camera-live.cjs --out report.json --seconds 120
const { app, BrowserWindow, ipcMain, sharedTexture } = require("electron");
const { writeFileSync } = require("node:fs");
const { join } = require("node:path");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const arg = (name, fallback) => {
  const index = process.argv.indexOf(name);
  return index > 0 ? process.argv[index + 1] : fallback;
};
const outPath = arg("--out", join(require("node:os").tmpdir(), "qr-camera-live.json"));
const seconds = Number(arg("--seconds", "120"));
const initialDeviceId = arg("--device", undefined);
const preferredResolution = arg("--res", "720p");
const lockFocusOnDetect = process.argv.includes("--lock-focus");

app.whenReady().then(async () => {
  const win = new BrowserWindow({
    width: 800,
    height: 520,
    title: "electron-qr-scanner camera (Windows)",
    webPreferences: {
      preload: join(__dirname, "camera-live-preload.cjs"),
      sandbox: true,
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  await win.loadFile(join(__dirname, "camera-live.html"));

  const cameras = await api.listCameras();
  const activeDeviceId = initialDeviceId ?? cameras[0]?.id;
  win.webContents.send("camera-list", cameras, activeDeviceId);

  const started = Date.now();
  const report = {
    electron: process.versions.electron,
    cameraCount: cameras.length,
    preferredResolution,
    lockFocusOnDetect,
    readyMs: null,
    readyInfo: null,
    fallbackRecommended: false,
    diagnostics: [],
    errors: [],
    drawn: 0,
    maxLent: 0,
    plainTextDetections: 0,
    ur: null,
  };
  const decoder = new URDecoder();
  const urParts = new Set();
  let urStartedAt;
  let inFlight = false;
  let finished = false;
  let restartRequired = false;

  const status = () => {
    const infoLine = report.readyInfo
      ? `${report.readyInfo.width}x${report.readyInfo.height}@${Math.round(report.readyInfo.fps)}fps ${report.readyInfo.subtype} · AF:${report.readyInfo.autoFocusSupported ? "yes" : report.readyInfo.focusSupported ? "manual" : "fixed"}`
      : "";
    const lines = [
      report.readyMs === null
        ? "waiting for camera…"
        : `camera ready in ${report.readyMs} ms · frames drawn ${report.drawn}`,
      infoLine,
      urParts.size
        ? `UR parts ${urParts.size} · ${Math.round(decoder.estimatedPercentComplete() * 100)}%`
        : "",
      report.ur
        ? `UR done: ${report.ur.bytes} bytes in ${report.ur.ms} ms`
        : "",
      report.plainTextDetections ? `QR detections: ${report.plainTextDetections}` : "",
      ...report.errors.slice(-1).map((e) => `error ${e.code}`),
    ].filter(Boolean);
    if (!win.isDestroyed()) win.webContents.send("camera-status", lines.join("\n"));
  };
  ipcMain.on("camera-drawn", (_event, count) => {
    report.drawn = count;
  });

  const finish = () => {
    if (finished) return;
    finished = true;
    capture.dispose();
    report.lentAfterDispose = capture.lentFrames();
    report.totalMs = Date.now() - started;
    writeFileSync(outPath, JSON.stringify(report, null, 2));
    setTimeout(() => app.exit(0), 1500);
  };

  const recordCaptureError = (error) => {
    restartRequired = true;
    report.errors.push({ code: error.code ?? "decoder-unavailable" });
    if (api.shouldFallbackToWeb(error)) {
      report.fallbackRecommended = true;
    }
    status();
  };

  const stopAfterPreviewError = () => {
    capture.stop();
    recordCaptureError({ code: "preview-unavailable" });
  };

  const capture = api.createCapture({
    source: {
      kind: "camera",
      deviceId: activeDeviceId,
      preferredResolution,
      lockFocusOnDetect,
    },
    onDiagnostic: (evt) => {
      report.diagnostics.push(evt);
      console.log(`[qr-scanner] ${evt.event}`, JSON.stringify(evt));
    },
    onReady: (_generation, info) => {
      report.readyMs ??= Date.now() - started;
      report.readyInfo = info ?? null;
      status();
    },
    onError: recordCaptureError,
    onText: (text) => {
      if (/^ur:/i.test(text)) {
        urStartedAt ??= Date.now();
        urParts.add(text.toLowerCase());
        decoder.receivePart(text.toLowerCase());
        if (decoder.isComplete() && decoder.isSuccess() && !report.ur) {
          const ur = decoder.resultUR();
          report.ur = {
            bytes: ur.cbor.length,
            ms: Date.now() - urStartedAt,
            distinctParts: urParts.size,
          };
          status();
          setTimeout(finish, 2000);
        }
      } else {
        report.plainTextDetections += 1;
      }
      status();
    },
    onFrame: (frame) => {
      report.maxLent = Math.max(report.maxLent, capture.lentFrames());
      if (inFlight || win.isDestroyed()) {
        frame.release();
        return;
      }
      inFlight = true;
      let imported;
      try {
        imported = sharedTexture.importSharedTexture({
          textureInfo: {
            pixelFormat: "bgra",
            codedSize: { width: frame.width, height: frame.height },
            handle: { ntHandle: frame.ntHandle },
          },
          allReferencesReleased: () => frame.release(),
        });
      } catch {
        inFlight = false;
        frame.release();
        stopAfterPreviewError();
        return;
      }
      sharedTexture
        .sendSharedTexture({ frame: win.webContents.mainFrame, importedSharedTexture: imported })
        .catch(stopAfterPreviewError)
        .finally(() => {
          imported.release();
          inFlight = false;
        });
    },
  });

  const startCapture = () => {
    restartRequired = false;
    try {
      capture.start();
    } catch (error) {
      recordCaptureError(error);
    }
  };

  ipcMain.on("camera-select", (_event, deviceId) => {
    if (finished) return;
    try {
      capture.setDevice(deviceId);
      if (restartRequired) startCapture();
    } catch (error) {
      recordCaptureError(error);
    }
  });

  startCapture();
  setInterval(status, 500);
  setTimeout(finish, seconds * 1000);
  win.on("closed", finish);
});
