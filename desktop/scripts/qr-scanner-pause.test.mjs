import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { createRequire } from "node:module";
import { test } from "node:test";
import { fileURLToPath } from "node:url";
import { runInNewContext } from "node:vm";

for (const [directory, platform] of [["mac", "darwin"], ["win", "win32"]]) {
  test(`${platform} discards queued analysis and returns stale texture slots after resume`, () => {
    let deliver;
    let revision = 0;
    let paused = false;
    const released = [];
    class NativeCapture {
      constructor(...args) { deliver = args.at(-1); }
      start() { return 1; }
      stop() {}
      stats() { return {}; }
      lentFrames() { return 0; }
      setMultiSymbol() {}
      setPaused(value) {
        if (paused !== value) revision += 1;
        paused = value;
        return revision;
      }
      releaseFrame(token) { released.push(token); }
    }
    const source = new URL(`../${directory}/electron-qr-scanner/index.cjs`, import.meta.url);
    const context = {
      module: { exports: {} },
      process: { platform, arch: "arm64" },
      require: () => ({ NativeCapture }),
    };
    runInNewContext(readFileSync(source, "utf8"), context);
    const results = [];
    const capture = context.module.exports.createCapture({
      multiSymbol: true,
      onText: (text) => results.push(text),
      onSymbols: (symbols) => results.push(symbols[0].text),
      onFrame: (frame) => { results.push("preview"); frame.release(); },
    });
    capture.start();
    capture.setPaused(true);
    capture.setPaused(false);
    capture.setPaused(false);

    const deliverRevision = (analysisRevision, token) => {
      const qr = [{ text: "symbol" }];
      const payload = JSON.stringify({ symbols: qr, frameWidth: 80, frameHeight: 60 });
      const common = { generation: 1, analysisRevision, width: 80, height: 60 };
      deliver({ ...common, kind: "text", text: "text" });
      deliver({ ...common, kind: "symbols", symbols: qr, text: payload });
      deliver({ ...common, kind: "frame", token });
    };
    deliverRevision(0, 8);
    deliverRevision(1, 9);
    assert.deepEqual(results, []);
    assert.deepEqual(released, [8, 9]);
    deliverRevision(2, 10);
    assert.deepEqual(results, ["text", "symbol", "preview"]);
    assert.deepEqual(released, [8, 9, 10]);
    capture.stop();
  });
}

const nativePlatform = { darwin: "mac", win32: "win" }[process.platform];

test("pausing a native desktop capture preserves the repeat interval", {
  skip: !nativePlatform || !["arm64", "x64"].includes(process.arch),
  timeout: 8_000,
}, async ({ signal }) => {
  const require = createRequire(import.meta.url);
  const { createCapture } = require(`../${nativePlatform}/electron-qr-scanner/index.cjs`);
  const path = fileURLToPath(new URL("../mac/electron-qr-scanner/test/fixtures/static-qr.mp4", import.meta.url));
  let capture;
  let first;
  try {
    await new Promise((resolve, reject) => {
      signal.addEventListener("abort", () => reject(signal.reason), { once: true });
      capture = createCapture({
        source: { kind: "file", path, loop: true },
        onText(text, generation) {
          const now = performance.now();
          if (!first) {
            first = { text, generation, now };
            capture.setPaused(true);
            capture.setPaused(false);
            return;
          }
          try {
            assert.equal(text, first.text);
            assert.equal(generation, first.generation);
            // Native timestamps precede JS delivery, so allow dispatch latency.
            const elapsed = now - first.now;
            assert.ok(elapsed >= 750, `QR repeated ${Math.round(elapsed)} ms after resume`);
            resolve();
          } catch (error) {
            reject(error);
          }
        },
        onError: reject,
      });
      capture.start();
    });
  } finally {
    capture?.dispose();
  }
});
