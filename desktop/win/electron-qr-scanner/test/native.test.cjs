"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { test } = require("node:test");
const { URDecoder } = require("@ngraveio/bc-ur");
const api = require("../index.cjs");

const nativeWin = process.platform === "win32" && ["x64", "arm64"].includes(process.arch);
const fixturesDir = join(__dirname, "../../../../native-views/react-native-qr-scanner/ios/tests/fixtures");
const videoFixturesDir = join(__dirname, "../../../mac/electron-qr-scanner/test/fixtures");
const fixture = (name) => join(fixturesDir, name);
const animated = JSON.parse(readFileSync(join(videoFixturesDir, "animated-ur.json"), "utf8"));

function fnv1a(bytes) {
  let hash = 0x811c9dc5;
  for (const byte of bytes) hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  return hash.toString(16).padStart(8, "0");
}

test("decodes BTCPay lightning fixtures through the 4-pass WIC + ZXing pipeline", { skip: !nativeWin }, async () => {
  const payload = readFileSync(fixture("lightning-payload.txt"), "utf8");
  for (const name of [
    "lightning-clean.png",
    "lightning-btcpay-dark.png",
    "lightning-inverted.png",
    "lightning-exif-rotated.jpg",
  ]) {
    assert.equal(await api.decodeImage(fixture(name)), payload, name);
  }
});

test("preserves embedded NUL in image QR text", { skip: !nativeWin }, async () => {
  const path = join(videoFixturesDir, "embedded-nul.png");
  assert.equal(await api.decodeImage(path), "before\0after");
});

test("engines option accepts zxing and rejects unknown or empty lists", { skip: !nativeWin }, async () => {
  const payload = readFileSync(fixture("lightning-payload.txt"), "utf8");
  assert.equal(
    await api.decodeImage(fixture("lightning-btcpay-dark.png"), { engines: ["zxing"] }),
    payload,
  );
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { engines: ["vision"] }), {
    code: "decoder-unavailable",
  });
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { engines: [] }), {
    code: "decoder-unavailable",
  });
});

test("returns null for an image without a code", { skip: !nativeWin }, async () => {
  assert.equal(await api.decodeImage(fixture("no-qr.png")), null);
});

test("rejects unreadable input with invalid-image", { skip: !nativeWin }, async () => {
  for (const path of [fixture("corrupt.png"), "C:\\nonexistent\\qr-scanner-missing.png"]) {
    await assert.rejects(api.decodeImage(path), { code: "invalid-image" });
  }
  await assert.rejects(api.decodeImage(fixture("lightning-clean.png"), { maxPixels: 0 }), {
    code: "invalid-image",
  });
  await assert.rejects(api.decodeImage(""), { code: "invalid-image" });
});

test("listCameras returns a well-formed device list", { skip: !nativeWin }, async () => {
  const cameras = await api.listCameras();
  assert.ok(Array.isArray(cameras));
  for (const [index, device] of cameras.entries()) {
    assert.equal(typeof device.id, "string");
    assert.ok(device.id.length > 0);
    assert.equal(typeof device.name, "string");
    assert.equal(device.isDefault, index === 0);
  }
});

test("assembles an animated UR from video frames and emits privacy-safe diagnostics", { skip: !nativeWin }, async () => {
  const decoder = new URDecoder();
  const seen = new Set();
  const diagnostics = [];
  const videoPath = join(videoFixturesDir, "animated-ur.mp4");
  let readyGeneration;
  let readyInfo;
  const started = Date.now();
  const result = await new Promise((resolve, reject) => {
    const capture = api.createCapture({
      source: { kind: "file", path: videoPath, loop: true },
      onDiagnostic: (evt) => diagnostics.push(evt),
      onReady: (generation, info) => {
        readyGeneration = generation;
        readyInfo = info;
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
  assert.ok(readyInfo && readyInfo.width > 0 && readyInfo.height > 0);
  assert.equal(typeof readyInfo.fps, "number");
  assert.equal(typeof readyInfo.subtype, "string");
  assert.equal(readyInfo.cameraCount, 0);
  assert.equal(readyInfo.cameraIndex, null);
  assert.equal(readyInfo.isDefaultCamera, false);
  assert.equal(typeof readyInfo.focusSupported, "boolean");
  assert.equal(typeof readyInfo.autoFocusSupported, "boolean");
  assert.equal(typeof readyInfo.exposureSupported, "boolean");
  assert.equal(result.type, animated.type);
  assert.equal(result.cbor.length, animated.bytes);
  assert.equal(fnv1a(result.cbor), animated.fnv1a);
  const elapsed = (Date.now() - started) / 1000;
  assert.ok(elapsed < (animated.frames / animated.fps) * 2, `took ${elapsed}s`);
  assert.ok(seen.size >= animated.fragments, `only ${seen.size} distinct parts`);

  // Diagnostics lifecycle and strict privacy invariants.
  assert.deepEqual(
    diagnostics.map((d) => d.event),
    ["start", "ready", "stop"],
  );
  const stopEvent = diagnostics[2];
  assert.ok(stopEvent.framesCaptured >= animated.fragments);
  assert.ok(stopEvent.decodeAttempts >= animated.fragments);
  assert.ok(stopEvent.decodeHits >= animated.fragments);
  assert.equal(stopEvent.lentAfterStop, 0);
  const serialized = JSON.stringify(diagnostics);
  assert.ok(!serialized.includes(videoPath), "diagnostic leaked local file path");
  assert.ok(!serialized.includes("animated-ur.mp4"), "diagnostic leaked file name");
  assert.ok(!serialized.includes("ur:"), "diagnostic leaked QR/UR payload");
});

test("diagnostics scrub custom deviceId and never leak raw identifiers on error", { skip: !nativeWin }, async () => {
  const diagnostics = [];
  const secretDeviceId = "\\\\?\\usb#vid_dead&pid_beef#serial_secret_12345";
  await new Promise((resolve) => {
    const capture = api.createCapture({
      source: { kind: "camera", deviceId: secretDeviceId },
      onDiagnostic: (evt) => diagnostics.push(evt),
      onError: () => {
        capture.dispose();
        resolve();
      },
    });
    capture.start();
  });
  assert.ok(diagnostics.some((d) => d.event === "start" && d.hasCustomDeviceId === true));
  assert.ok(diagnostics.some((d) => d.event === "error" && d.code === "no-camera" && d.fallbackToWeb === false));
  const serialized = JSON.stringify(diagnostics);
  assert.ok(!serialized.includes("dead"), "diagnostic leaked deviceId");
  assert.ok(!serialized.includes("serial_secret_12345"), "diagnostic leaked hardware serial");
});

test("shouldFallbackToWeb classifies fallback-eligible vs non-fallback errors", () => {
  const withCode = (code) => Object.assign(new Error(code), { code });
  assert.equal(api.shouldFallbackToWeb(withCode("preview-unavailable")), true);
  assert.equal(api.shouldFallbackToWeb(withCode("decoder-unavailable")), true);
  assert.equal(api.shouldFallbackToWeb(new Error("unexpected GPU error")), true);
  assert.equal(api.shouldFallbackToWeb(withCode("permission-denied")), false);
  assert.equal(api.shouldFallbackToWeb(withCode("no-camera")), false);
  assert.equal(api.shouldFallbackToWeb(withCode("camera-busy")), false);
  assert.equal(api.shouldFallbackToWeb(withCode("invalid-image")), false);
  assert.equal(api.shouldFallbackToWeb(withCode("resource-limit")), false);
  assert.equal(api.shouldFallbackToWeb(null), false);
});

test("rejects unsupported preferredResolution", { skip: !nativeWin }, () => {
  assert.throws(
    () =>
      api.createCapture({
        source: { kind: "camera", preferredResolution: "4k" },
      }),
    { code: "decoder-unavailable" },
  );
});

test("stop and start begin a new generation; dispose silences events", { skip: !nativeWin }, async () => {
  const generations = [];
  let waitReady;
  const nextReady = () =>
    new Promise((resolve, reject) => {
      waitReady = resolve;
      setTimeout(() => reject(new Error("timed out waiting for ready")), 5_000).unref();
    });
  const capture = api.createCapture({
    source: { kind: "file", path: join(videoFixturesDir, "animated-ur.mp4"), loop: true },
    onReady: (generation) => {
      generations.push(generation);
      waitReady?.();
      waitReady = undefined;
    },
  });
  let readyPromise = nextReady();
  capture.start();
  await readyPromise;
  capture.stop();
  readyPromise = nextReady();
  capture.start();
  await readyPromise;
  capture.dispose();
  capture.dispose();
  await new Promise((resolve) => setTimeout(resolve, 100));
  assert.equal(generations.length, 2);
  assert.ok(generations[1] > generations[0]);
  assert.throws(() => capture.start(), { code: "decoder-unavailable" });
  assert.throws(() => capture.setDevice("any"), { code: "decoder-unavailable" });
});

test("setDevice rejects on file sources and unknown camera deviceId reports no-camera", { skip: !nativeWin }, async () => {
  const fileCapture = api.createCapture({
    source: { kind: "file", path: join(videoFixturesDir, "animated-ur.mp4") },
  });
  assert.throws(() => fileCapture.setDevice("some-id"), { code: "decoder-unavailable" });
  fileCapture.dispose();

  const error = await new Promise((resolve) => {
    const capture = api.createCapture({
      source: { kind: "camera", deviceId: "\\\\?\\nonexistent-camera-id-test" },
      onError: (failure) => {
        capture.dispose();
        resolve(failure);
      },
    });
    capture.start();
  });
  assert.equal(error.code, "no-camera");
});

test("a failed camera requires an explicit start after setDevice", { skip: !nativeWin, timeout: 10_000 }, async () => {
  const generations = [];
  let nextError;
  const capture = api.createCapture({
    source: { kind: "camera", deviceId: "missing-camera-before-switch" },
    onError(error, generation) {
      generations.push(generation);
      nextError?.(error);
      nextError = undefined;
    },
  });
  const start = () => new Promise((resolve) => {
    nextError = resolve;
    capture.start();
  });

  try {
    assert.equal((await start()).code, "no-camera");
    capture.setDevice("missing-camera-after-switch");
    await new Promise((resolve) => setTimeout(resolve, 200));
    assert.equal(generations.length, 1);
    assert.equal((await start()).code, "no-camera");
    assert.ok(generations[1] > generations[0]);
  } finally {
    capture.dispose();
  }
});

test("delivers a final-frame QR before non-looping video EOF", { skip: !nativeWin, timeout: 10_000 }, async () => {
  const events = [];
  let capture;
  try {
    await new Promise((resolve, reject) => {
      capture = api.createCapture({
        source: { kind: "file", path: join(__dirname, "last-frame-qr.mp4") },
        onText: (text) => events.push(text),
        onEnded: () => {
          events.push("ended");
          capture.dispose();
          resolve();
        },
        onError: reject,
      });
      capture.start();
    });
    assert.deepEqual(events, ["before\0after", "ended"]);
  } finally {
    capture?.dispose();
  }
});

test("a missing video file reports an error", { skip: !nativeWin }, async () => {
  const error = await new Promise((resolve) => {
    const capture = api.createCapture({
      source: { kind: "file", path: "C:\\nonexistent\\animated.mp4" },
      onError: (failure) => {
        capture.dispose();
        resolve(failure);
      },
    });
    capture.start();
  });
  assert.equal(error.code, "invalid-image");
});

// Opens the real camera; runs when QR_SCANNER_CAMERA=1 is set.
test(
  "camera capture starts or reports a documented error, and supports switching",
  { skip: !nativeWin || process.env.QR_SCANNER_CAMERA !== "1" },
  async () => {
    const cameras = await api.listCameras();
    const outcome = await new Promise((resolve) => {
      const capture = api.createCapture({
        source: { kind: "camera", deviceId: cameras[0]?.id },
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
