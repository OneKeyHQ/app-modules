"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const test = require("node:test");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const nativeMac = process.platform === "darwin" && ["arm64", "x64"].includes(process.arch);
// Image fixtures shared with the iOS XCTests.
const imageFixtures = join(
  __dirname,
  "../../../../native-views/react-native-qr-scanner/ios/tests/fixtures",
);
const fixture = (name) => join(imageFixtures, name);
const animated = JSON.parse(readFileSync(join(__dirname, "fixtures/animated-ur.json"), "utf8"));

function fnv1a(bytes) {
  let hash = 0x811c9dc5;
  for (const byte of bytes) hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  return hash.toString(16).padStart(8, "0");
}

test("decodes the BTCPay lightning invoice image", { skip: !nativeMac }, async () => {
  const payload = readFileSync(fixture("lightning-payload.txt"), "utf8");
  for (const name of ["lightning-clean.png", "lightning-btcpay-dark.png", "lightning-exif-rotated.jpg"]) {
    assert.equal(await api.decodeImage(fixture(name)), payload, name);
  }
});

test("ZXing-C++ fallback decodes on its own", { skip: !nativeMac }, async () => {
  const payload = readFileSync(fixture("lightning-payload.txt"), "utf8");
  for (const name of [
    "lightning-clean.png",
    "lightning-btcpay-dark.png",
    "lightning-inverted.png",
    "lightning-exif-rotated.jpg",
  ]) {
    assert.equal(await api.decodeImage(fixture(name), { engines: ["zxing"] }), payload, name);
  }
  assert.equal(await api.decodeImage(fixture("no-qr.png"), { engines: ["zxing"] }), null);
});

test("Vision alone and engine validation", { skip: !nativeMac }, async () => {
  const payload = readFileSync(fixture("lightning-payload.txt"), "utf8");
  assert.equal(
    await api.decodeImage(fixture("lightning-btcpay-dark.png"), { engines: ["vision"] }),
    payload,
  );
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { engines: ["mlkit"] }), {
    code: "decoder-unavailable",
  });
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { engines: [] }), {
    code: "decoder-unavailable",
  });
});

test("returns null for an image without a code", { skip: !nativeMac }, async () => {
  assert.equal(await api.decodeImage(fixture("no-qr.png")), null);
});

test("rejects unreadable input with invalid-image", { skip: !nativeMac }, async () => {
  for (const path of [fixture("corrupt.png"), "/nonexistent/qr.png"]) {
    await assert.rejects(api.decodeImage(path), { code: "invalid-image" });
  }
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { maxPixels: 0 }), {
    code: "invalid-image",
  });
  await assert.rejects(api.decodeImage(""), { code: "invalid-image" });
});

test("assembles an animated UR from video frames", { skip: !nativeMac }, async () => {
  const decoder = new URDecoder();
  const seen = new Set();
  let readyGeneration;
  const started = Date.now();
  const result = await new Promise((resolve, reject) => {
    const capture = api.createCapture({
      source: { kind: "file", path: join(__dirname, "fixtures/animated-ur.mp4"), loop: true },
      onReady: (generation) => {
        readyGeneration = generation;
      },
      onText: (text, generation) => {
        assert.equal(generation, readyGeneration);
        seen.add(text);
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
      reject(new Error(`timed out after ${seen.size} distinct parts`));
    }, 30_000).unref();
  });
  assert.equal(result.type, animated.type);
  assert.equal(result.cbor.length, animated.bytes);
  assert.equal(fnv1a(result.cbor), animated.fnv1a);
  // One loop of the video is frames / fps seconds; the fountain code needs
  // about the fragment count of distinct parts.
  const elapsed = (Date.now() - started) / 1000;
  assert.ok(elapsed < (animated.frames / animated.fps) * 2, `took ${elapsed}s`);
  assert.ok(seen.size >= animated.fragments, `only ${seen.size} distinct parts`);
});

test("stop and start begin a new generation; dispose silences events", { skip: !nativeMac }, async () => {
  const generations = [];
  const capture = api.createCapture({
    source: { kind: "file", path: join(__dirname, "fixtures/animated-ur.mp4"), loop: true },
    onReady: (generation) => generations.push(generation),
  });
  capture.start();
  await new Promise((resolve) => setTimeout(resolve, 300));
  capture.stop();
  capture.start();
  await new Promise((resolve) => setTimeout(resolve, 300));
  capture.dispose();
  capture.dispose();
  assert.equal(generations.length, 2);
  assert.ok(generations[1] > generations[0]);
  assert.throws(() => capture.start(), { code: "decoder-unavailable" });
});

test("a missing video file reports an error", { skip: !nativeMac }, async () => {
  const error = await new Promise((resolve) => {
    const capture = api.createCapture({
      source: { kind: "file", path: "/nonexistent/animated.mp4" },
      onError: (failure) => {
        capture.dispose();
        resolve(failure);
      },
    });
    capture.start();
  });
  assert.equal(error.code, "invalid-image");
});

// Opens the real camera; macOS asks the host app for camera access.
test(
  "camera capture starts or reports a documented error",
  { skip: !nativeMac || process.env.QR_SCANNER_CAMERA !== "1" },
  async () => {
    const outcome = await new Promise((resolve) => {
      const capture = api.createCapture({
        onReady: () => {
          capture.dispose();
          resolve("ready");
        },
        onError: (error) => {
          capture.dispose();
          resolve(error.code);
        },
      });
      capture.start();
    });
    assert.ok(["ready", "permission-denied", "no-camera", "camera-busy"].includes(outcome), outcome);
  },
);
