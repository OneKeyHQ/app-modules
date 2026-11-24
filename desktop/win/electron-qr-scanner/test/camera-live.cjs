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

function fnv1a(bytes) {
  let hash = 0x811c9dc5;
  for (const byte of bytes) hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  return hash.toString(16).padStart(8, "0");
}

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
    cameras,
    preferredResolution,
    lockFocusOnDetect,
    readyMs: null,
    readyInfo: null,
    fallbackRecommended: false,
    diagnostics: [],
    errors: [],
    drawn: 0,
    maxLent: 0,
    texts: [],
    ur: null,
  };
  const decoder = new URDecoder();
  const urParts = new Set();
  let urStartedAt;
  let inFlight = false;
  let finished = false;

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
        ? `UR done: ${report.ur.type} ${report.ur.bytes} bytes fnv ${report.ur.fnv} in ${report.ur.ms} ms`
        : "",
      ...report.texts
        .slice(-2)
        .map((t) => `text: ${t.text.slice(0, 70)}${t.text.length > 70 ? "…" : ""}`),
      ...report.errors.slice(-1).map((e) => `error ${e.code}: ${e.message}`),
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
    onError: (error) => {
      report.errors.push({ code: error.code, message: error.message });
      if (api.shouldFallbackToWeb(error)) {
        report.fallbackRecommended = true;
      }
      status();
    },
    onText: (text) => {
      if (/^ur:/i.test(text)) {
        urStartedAt ??= Date.now();
        urParts.add(text.toLowerCase());
        decoder.receivePart(text.toLowerCase());
        if (decoder.isComplete() && decoder.isSuccess() && !report.ur) {
          const ur = decoder.resultUR();
          report.ur = {
            type: ur.type,
            bytes: ur.cbor.length,
            fnv: fnv1a(ur.cbor),
            ms: Date.now() - urStartedAt,
            distinctParts: urParts.size,
          };
          status();
          setTimeout(finish, 2000);
        }
      } else if (!report.texts.some((t) => t.text === text)) {
        report.texts.push({ text, atMs: Date.now() - started });
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
      } catch (error) {
        inFlight = false;
        frame.release();
        report.errors.push({ code: "preview-unavailable", message: error.message });
        report.fallbackRecommended = true;
        capture.stop();
        status();
        return;
      }
      sharedTexture
        .sendSharedTexture({ frame: win.webContents.mainFrame, importedSharedTexture: imported })
        .catch((error) => {
          report.errors.push({ code: "preview-unavailable", message: error.message });
          report.fallbackRecommended = true;
          capture.stop();
          status();
        })
        .finally(() => {
          imported.release();
          inFlight = false;
        });
    },
  });

  ipcMain.on("camera-select", (_event, deviceId) => {
    try {
      capture.setDevice(deviceId);
    } catch (error) {
      report.errors.push({ code: error.code ?? "decoder-unavailable", message: error.message });
      status();
    }
  });

  capture.start();
  setInterval(status, 500);
  setTimeout(finish, seconds * 1000);
  win.on("closed", finish);
});
