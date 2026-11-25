import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { test } from "node:test";
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
