"use strict";

// Loads the native module in Electron's main process (the process that will
// own the camera in the app) and runs image and animated-video decoding.
// `--camera` also opens the real camera, which prompts for access.
const { app } = require("electron");
const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const fixtures = join(__dirname, "../../../../native-views/react-native-qr-scanner/ios/tests/fixtures");
const animated = JSON.parse(readFileSync(join(__dirname, "fixtures/animated-ur.json"), "utf8"));

function assembleFromVideo() {
  return new Promise((resolve, reject) => {
    const decoder = new URDecoder();
    const capture = api.createCapture({
      source: { kind: "file", path: join(__dirname, "fixtures/animated-ur.mp4"), loop: true },
      onText: (text) => {
        decoder.receivePart(text.toLowerCase());
        if (decoder.isComplete()) {
          capture.dispose();
          resolve(decoder.resultUR());
        }
      },
      onError: (error) => {
        capture.dispose();
        reject(error);
      },
    });
    capture.start();
    setTimeout(() => {
      capture.dispose();
      reject(new Error("animated UR timed out"));
    }, 30_000);
  });
}

function openCamera() {
  return new Promise((resolve) => {
    const capture = api.createCapture({
      onReady: () => {
        setTimeout(() => {
          capture.dispose();
          resolve("ready");
        }, 1_000);
      },
      onError: (error) => {
        capture.dispose();
        resolve(error.code);
      },
    });
    capture.start();
  });
}

app
  .whenReady()
  .then(async () => {
    assert.equal(process.versions.electron, require("../package.json").devDependencies.electron);
    const payload = readFileSync(join(fixtures, "lightning-payload.txt"), "utf8");
    assert.equal(await api.decodeImage(join(fixtures, "lightning-btcpay-dark.png")), payload);
    await assert.rejects(api.decodeImage(join(fixtures, "corrupt.png")), { code: "invalid-image" });
    const ur = await assembleFromVideo();
    assert.equal(ur.type, animated.type);
    assert.equal(ur.cbor.length, animated.bytes);
    const report = { electron: process.versions.electron, image: "ok", animatedUr: "ok" };
    if (process.argv.includes("--camera")) report.camera = await openCamera();
    console.log(`electron-qr-scanner: ${JSON.stringify(report)}`);
    app.exit(0);
  })
  .catch((error) => {
    console.error(error);
    app.exit(1);
  });
