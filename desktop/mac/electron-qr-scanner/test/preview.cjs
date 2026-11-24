"use strict";

// Preview path end to end: native IOSurface frames -> sharedTexture ->
// sandboxed preload -> canvas, while the same frames feed QR detection.
const { app, BrowserWindow, ipcMain, sharedTexture } = require("electron");
const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const animated = JSON.parse(readFileSync(join(__dirname, "fixtures/animated-ur.json"), "utf8"));

let phase = "starting Electron";
const deadline = setTimeout(() => {
  console.error(`Timed out while ${phase}`);
  app.exit(1);
}, 90_000);

app.whenReady().then(async () => {
  try {
    const win = new BrowserWindow({
      width: 480,
      height: 480,
      show: process.argv.includes("--show"),
      webPreferences: {
        preload: join(__dirname, "preview-preload.cjs"),
        sandbox: true,
        backgroundThrottling: false,
        contextIsolation: true,
        nodeIntegration: false,
      },
    });
    phase = "loading the preview document";
    await win.loadFile(join(__dirname, "preview.html"));
    phase = "sending native preview frames";
    console.log(`Preview renderer ready (GPU compositing: ${app.getGPUFeatureStatus().gpu_compositing})`);

    let drawn = 0;
    let dark = 0;
    let light = 0;
    let urDone = false;
    const previewReady = Promise.withResolvers();
    const finishIfReady = () => {
      if (drawn >= 10 && dark > 50 && light > 50 && urDone) previewReady.resolve();
    };
    ipcMain.on("preview-drawn", (_event, report) => {
      ({ drawn, dark, light } = report);
      finishIfReady();
    });

    let inFlight = false;
    let maxLent = 0;
    let sent = 0;
    let skipped = 0;
    const decoder = new URDecoder();
    const capture = api.createCapture({
      source: { kind: "file", path: join(__dirname, "fixtures/animated-ur.mp4"), loop: true },
      onText: (text) => {
        if (urDone) return;
        decoder.receivePart(text.toLowerCase());
        urDone = decoder.isComplete();
        finishIfReady();
      },
      onError: previewReady.reject,
      onFrame: (frame) => {
        maxLent = Math.max(maxLent, capture.lentFrames());
        if (inFlight || win.isDestroyed()) {
          skipped += 1;
          frame.release();
          return;
        }
        inFlight = true;
        const imported = sharedTexture.importSharedTexture({
          textureInfo: {
            pixelFormat: "bgra",
            codedSize: { width: frame.width, height: frame.height },
            handle: { ioSurface: frame.ioSurface },
          },
          allReferencesReleased: () => frame.release(),
        });
        sharedTexture
          .sendSharedTexture({ frame: win.webContents.mainFrame, importedSharedTexture: imported })
          .then(() => {
            sent += 1;
          })
          .catch(previewReady.reject)
          .finally(() => {
            imported.release();
            inFlight = false;
          });
      },
    });
    const timeout = setTimeout(() => previewReady.reject(new Error(
      `Preview timed out after drawing ${drawn} frames (dark: ${dark}, light: ${light}, UR complete: ${urDone})`,
    )), 30_000);
    capture.start();
    try {
      await previewReady.promise;
    } finally {
      clearTimeout(timeout);
    }

    phase = "stopping capture";
    const saveIndex = process.argv.indexOf("--save");
    if (saveIndex > 0) {
      const image = await win.webContents.capturePage();
      require("node:fs").writeFileSync(process.argv[saveIndex + 1], image.toPNG());
    }
    capture.stop();
    await new Promise((resolve) => setTimeout(resolve, 500));
    const lentAfterStop = capture.lentFrames();
    capture.dispose();

    const report = { sent, drawn, skipped, maxLent, lentAfterStop, dark, light, urDone };
    console.log(`preview: ${JSON.stringify(report)}`);
    assert.ok(drawn >= 10, `only ${drawn} frames drawn`);
    assert.ok(maxLent <= 3, `lent ${maxLent} frames`);
    assert.equal(lentAfterStop, 0);
    assert.ok(dark > 50 && light > 50, "canvas does not show a QR code");
    assert.ok(urDone, `animated UR did not complete (${animated.fragments} fragments)`);
    clearTimeout(deadline);
    app.exit(0);
  } catch (error) {
    console.error(error);
    app.exit(1);
  }
});
