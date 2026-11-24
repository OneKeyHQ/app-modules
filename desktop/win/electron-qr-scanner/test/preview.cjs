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

app.whenReady().then(async () => {
  try {
    const win = new BrowserWindow({
      width: 480,
      height: 480,
      show: process.argv.includes("--show"),
      webPreferences: {
        preload: join(__dirname, "preview-preload.cjs"),
        sandbox: true,
        contextIsolation: true,
        nodeIntegration: false,
      },
    });
    await win.loadFile(join(__dirname, "preview.html"));

    let drawn = 0;
    ipcMain.on("preview-drawn", (_event, count) => {
      drawn = count;
    });

    let inFlight = false;
    let maxLent = 0;
    let sent = 0;
    let skipped = 0;
    const decoder = new URDecoder();
    let urDone = false;
    let capture;
    const done = new Promise((resolve, reject) => {
      capture = api.createCapture({
        source: { kind: "file", path: join(videoFixtures, "animated-ur.mp4"), loop: true },
        onText: (text) => {
          if (urDone) return;
          decoder.receivePart(text.toLowerCase());
          urDone = decoder.isComplete();
        },
        onError: reject,
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
            .catch(reject)
            .finally(() => {
              imported.release();
              inFlight = false;
            });
        },
      });
      setTimeout(resolve, 4_000);
    });
    capture.start();
    await done;

    const image = await win.webContents.capturePage();
    const saveIndex = process.argv.indexOf("--save");
    if (saveIndex > 0) require("node:fs").writeFileSync(process.argv[saveIndex + 1], image.toPNG());
    const bitmap = image.toBitmap();
    let dark = 0;
    let light = 0;
    for (let i = 0; i < bitmap.length; i += 4 * 97) {
      const luma = (bitmap[i] + bitmap[i + 1] + bitmap[i + 2]) / 3;
      if (luma < 60) dark += 1;
      else if (luma > 200) light += 1;
    }
    capture.stop();
    await new Promise((resolve) => setTimeout(resolve, 500));
    const lentAfterStop = capture.lentFrames();
    capture.dispose();

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

    const report = {
      sent,
      drawn,
      skipped,
      maxLent,
      lentAfterStop,
      maxLentAcrossCycles,
      cycleFramesSent,
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
    assert.ok(dark > 50 && light > 50, "canvas does not show a QR code");
    assert.ok(urDone, `animated UR did not complete (${animated.fragments} fragments)`);
    app.exit(0);
  } catch (error) {
    console.error(error);
    app.exit(1);
  }
});
