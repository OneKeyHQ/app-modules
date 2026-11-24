"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { join } = require("node:path");
const { test } = require("node:test");
const { runInNewContext } = require("node:vm");

const staged = join(__dirname, "../../dist/qr-scanner/staging/package");
const entry = readFileSync(join(staged, "index.cjs"), "utf8");

test("packaged onReady puts the camera ID second on both platforms", () => {
  for (const platform of ["darwin", "win32"]) {
    const module = { exports: {} };
    const metadata = { width: 640, height: 480 };
    const capture = {};
    const scanner = {
      createCapture({ onReady }) {
        if (platform === "win32") onReady(3, metadata, "camera");
        else onReady(3, "camera");
        return capture;
      },
    };
    runInNewContext(entry, {
      module,
      process: { platform },
      require(name) {
        assert.equal(name, platform === "darwin" ? "./mac/index.cjs" : "./win/index.cjs");
        return scanner;
      },
    });

    let results;
    const result = module.exports.createCapture({ onReady: (...args) => { results = args; } });
    assert.equal(result, capture);
    assert.deepEqual(results, platform === "win32" ? [3, "camera", metadata] : [3, "camera"]);
  }
});
