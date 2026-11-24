"use strict";

const assert = require("node:assert/strict");
const { execFileSync } = require("node:child_process");
const { copyFileSync, mkdirSync, mkdtempSync, readFileSync, rmSync, writeFileSync } = require("node:fs");
const { tmpdir } = require("node:os");
const { join } = require("node:path");
const { test } = require("node:test");
const { runInNewContext } = require("node:vm");

const staged = join(__dirname, "../../dist/qr-scanner/staging/package");
const entry = readFileSync(join(staged, "index.cjs"), "utf8");

test("packaged readiness normalizes arguments and preserves prototype options", () => {
  const source = { kind: "file", path: "clip.mp4" };
  let results;
  class Options {
    #source = source;

    get source() {
      return this.#source;
    }

    onReady(...args) {
      results = args;
    }

    onText() {}
    onError() {}
    onEnded() {}
    onFrame() {}
    onDiagnostic() {}
  }

  const options = new Options();
  for (const platform of ["darwin", "win32"]) {
    const module = { exports: {} };
    const metadata = { width: 640, height: 480 };
    const capture = {};
    const scanner = {
      createCapture(forwarded) {
        assert.equal(forwarded.source, source);
        for (const name of ["onText", "onError", "onEnded", "onFrame", "onDiagnostic"]) {
          assert.equal(forwarded[name], options[name]);
        }
        if (platform === "win32") forwarded.onReady(3, metadata, "camera");
        else forwarded.onReady(3, "camera");
        return capture;
      },
    };
    runInNewContext(entry, {
      module,
      exports: module.exports,
      process: { platform },
      require(name) {
        assert.equal(name, platform === "darwin" ? "./mac/index.cjs" : "./win/index.cjs");
        return scanner;
      },
    });

    assert.equal(module.exports.createCapture(options), capture);
    assert.deepEqual(results, platform === "win32" ? [3, "camera", metadata] : [3, "camera"]);
  }
});

test("packaged root supports named ESM imports on both platforms", (t) => {
  const dir = mkdtempSync(join(tmpdir(), "qr-scanner-esm-"));
  t.after(() => rmSync(dir, { force: true, recursive: true }));
  const pkg = join(dir, "node_modules", "@onekeyfe", "electron-qr-scanner");
  for (const [subpath, platform] of [["mac", "darwin"], ["win", "win32"]]) {
    mkdirSync(join(pkg, subpath), { recursive: true });
    writeFileSync(join(pkg, subpath, "index.cjs"), `
      exports.decodeImage = () => ${JSON.stringify(platform)};
      exports.listCameras = () => [];
      exports.createCapture = () => ({});
    `);
  }
  for (const file of ["package.json", "index.cjs"]) {
    copyFileSync(join(staged, file), join(pkg, file));
  }
  writeFileSync(join(dir, "client.mjs"), `
    import assert from "node:assert/strict";
    import { createCapture, decodeImage, listCameras, shouldFallbackToWeb } from "@onekeyfe/electron-qr-scanner";
    assert.equal(decodeImage(), process.platform);
    assert.deepEqual(listCameras(), []);
    assert.deepEqual(createCapture(), {});
    assert.equal(shouldFallbackToWeb, undefined);
  `);

  for (const platform of ["darwin", "win32"]) {
    execFileSync(process.execPath, [
      "--input-type=module", "--eval",
      'Object.defineProperty(process, "platform", { value: process.argv[1] }); await import("./client.mjs");',
      platform,
    ], { cwd: dir });
  }
});
