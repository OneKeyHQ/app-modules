"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { test } = require("node:test");
const { runInNewContext } = require("node:vm");

function createMockCapture(options) {
  let callback;
  let generation = 0;
  let running = false;
  const released = [];

  class NativeCapture {
    constructor(_options, onEvent) {
      callback = onEvent;
    }

    start() {
      running = true;
      return ++generation;
    }

    stop() {
      running = false;
    }

    setPaused() { return 0; }

    setDevice() {
      return running ? this.start() : undefined;
    }

    releaseFrame(token) {
      released.push(token);
    }

    stats() {
      return {};
    }

    lentFrames() {
      return 0;
    }
  }

  const module = { exports: {} };
  runInNewContext(readFileSync(join(__dirname, "../index.cjs"), "utf8"), {
    module,
    process: { platform: "win32", arch: "x64" },
    require: () => ({ NativeCapture }),
  });
  const capture = module.exports.createCapture(options);
  return { capture, emit: (event) => callback(event), released };
}

test("drops queued events across stop, restart and camera switch", () => {
  const received = [];
  const { capture, emit, released } = createMockCapture({
    onText: (text, generation) => received.push([text, generation]),
    onReady: (generation) => received.push(["ready", generation]),
    onError: (_error, generation) => received.push(["error", generation]),
    onEnded: (generation) => received.push(["ended", generation]),
    onFrame: (frame) => {
      received.push(["frame", frame.generation]);
      frame.release();
      frame.release();
    },
  });
  const text = (generation) => emit({ kind: "text", text: "qr", generation, analysisRevision: 0 });
  const frame = (generation, token) => emit({ kind: "frame", generation, token, analysisRevision: 0 });

  capture.setDevice("initial");
  text(0);
  capture.start();
  capture.stop();
  for (const kind of ["text", "ready", "error", "ended"]) {
    emit({ kind, generation: 1 });
  }
  frame(1, 11);

  capture.start();
  text(1);
  text(2);
  capture.setDevice("replacement");
  text(2);
  frame(2, 22);
  frame(3, 33);
  text(3);

  assert.deepEqual(received, [["qr", 2], ["frame", 3], ["qr", 3]]);
  assert.deepEqual(released, [11, 22, 33]);
});
