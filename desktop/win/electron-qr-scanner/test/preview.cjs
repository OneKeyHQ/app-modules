"use strict";

// Preview path end to end: native D3D11 NT-handle frames -> sharedTexture ->
// sandboxed preload -> canvas, while the same frames feed QR detection.
const { app, BrowserWindow, ipcMain, sharedTexture } = require("electron");
const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const videoFixtures = join(__dirname, "../../../mac/electron-qr-scanner/test/fixtures");
const animated = JSON.parse(readFileSync(join(videoFixtures, "animated-ur.json"), "utf8"));

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
    const diagnostics = [];
    const decoder = new URDecoder();
    const capture = api.createCapture({
      source: { kind: "file", path: join(videoFixtures, "animated-ur.mp4"), loop: true },
      onDiagnostic: (evt) => diagnostics.push(evt),
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
            handle: { ntHandle: frame.ntHandle },
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
    capture.stop();
    await new Promise((resolve) => setTimeout(resolve, 500));
    const lentAfterStop = capture.lentFrames();
    capture.dispose();

    phase = "checking repeated capture starts";
    // Repeated open/close leak check (10 cycles, each actually sending preview frames).
    let maxLentAcrossCycles = 0;
    let cycleFramesSent = 0;
    for (let cycle = 0; cycle < 10; cycle += 1) {
      let cycleInFlight = false;
      let cycleSent = 0;
      let resolveCycle;
      const cycleGotFrames = new Promise((resolve) => {
        resolveCycle = resolve;
      });
      const cycleCapture = api.createCapture({
        source: { kind: "file", path: join(videoFixtures, "animated-ur.mp4"), loop: true },
        onFrame: (frame) => {
          maxLentAcrossCycles = Math.max(maxLentAcrossCycles, cycleCapture.lentFrames());
          if (cycleInFlight || win.isDestroyed()) {
            frame.release();
            return;
          }
          cycleInFlight = true;
          const imported = sharedTexture.importSharedTexture({
            textureInfo: {
              pixelFormat: "bgra",
              codedSize: { width: frame.width, height: frame.height },
              handle: { ntHandle: frame.ntHandle },
            },
            allReferencesReleased: () => frame.release(),
          });
          sharedTexture
            .sendSharedTexture({ frame: win.webContents.mainFrame, importedSharedTexture: imported })
            .then(() => {
              cycleSent += 1;
              cycleFramesSent += 1;
              if (cycleSent >= 2) resolveCycle();
            })
            .catch(() => {})
            .finally(() => {
              imported.release();
              cycleInFlight = false;
            });
        },
      });
      cycleCapture.start();
      await Promise.race([cycleGotFrames, new Promise((resolve) => setTimeout(resolve, 2_000))]);
      cycleCapture.stop();
      await new Promise((resolve) => setTimeout(resolve, 150));
      assert.equal(cycleCapture.lentFrames(), 0, `slot leak on cycle ${cycle}`);
      cycleCapture.dispose();
    }

    phase = "checking the preview fallback";
    // Simulated preview failure -> clean teardown for Web fallback.
    let fallbackTriggered = false;
    let eventsAfterFallback = 0;
    const fallbackDone = new Promise((resolve, reject) => {
      let failedOnce = false;
      const fallbackCapture = api.createCapture({
        source: { kind: "file", path: join(videoFixtures, "animated-ur.mp4"), loop: true },
        onError: reject,
        onFrame: (frame) => {
          if (failedOnce) {
            eventsAfterFallback += 1;
            frame.release();
            return;
          }
          failedOnce = true;
          try {
            throw Object.assign(new Error("simulated GPU importSharedTexture failure"), {
              code: "preview-unavailable",
            });
          } catch (err) {
            frame.release();
            if (api.shouldFallbackToWeb(err)) {
              fallbackTriggered = true;
              fallbackCapture.dispose();
              assert.equal(fallbackCapture.lentFrames(), 0);
              setTimeout(resolve, 200);
            }
          }
        },
      });
      fallbackCapture.start();
    });
    await fallbackDone;

    const report = {
      sent,
      drawn,
      skipped,
      maxLent,
      lentAfterStop,
      maxLentAcrossCycles,
      cycleFramesSent,
      fallbackTriggered,
      eventsAfterFallback,
      dark,
      light,
      urDone,
    };
    console.log(`preview: ${JSON.stringify(report)}`);
    assert.ok(drawn >= 10, `only ${drawn} frames drawn`);
    assert.ok(maxLent <= 3, `lent ${maxLent} frames`);
    assert.ok(cycleFramesSent >= 20, `only ${cycleFramesSent} cycle frames sent`);
    assert.ok(maxLentAcrossCycles >= 1 && maxLentAcrossCycles <= 3, `lent ${maxLentAcrossCycles} frames across cycles`);
    assert.equal(lentAfterStop, 0);
    assert.equal(fallbackTriggered, true);
    assert.equal(eventsAfterFallback, 0);
    const stopDiag = diagnostics.find((d) => d.event === "stop");
    assert.ok(stopDiag && stopDiag.previewFramesEmitted >= 10, "missing stop preview diagnostic");
    assert.ok(stopDiag.maxLent <= 3, `diagnostic maxLent=${stopDiag.maxLent}`);
    assert.equal(stopDiag.lentAfterStop, 0);
    assert.ok(dark > 50 && light > 50, "canvas does not show a QR code");
    assert.ok(urDone, `animated UR did not complete (${animated.fragments} fragments)`);
    clearTimeout(deadline);
    app.exit(0);
  } catch (error) {
    console.error(error);
    app.exit(1);
  }
});
