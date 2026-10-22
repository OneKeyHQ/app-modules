"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const test = require("node:test");
const vm = require("node:vm");
const source = readFileSync(require.resolve("../index.cjs"), "utf8");

function loadApi({ platform = "win32", arch = "x64", type, electron = "44.5.1", env = {}, binding, loadError } = {}) {
  const exports = {};
  let loads = 0;
  vm.runInNewContext(source, {
    exports,
    process: { platform, arch, type, versions: { electron }, env },
    require(filename) {
      loads += 1;
      assert.equal(filename, `./electron-windows-process-hardening.win32-${arch}-msvc.node`);
      if (loadError) throw loadError;
      return binding;
    },
  });
  return { api: exports, loads: () => loads };
}

test("import is inert and supported call returns independent native stages", () => {
  const expected = { dacl: { status: "failed", win32Error: 5, message: "SetSecurityInfo failed" }, extensionPoint: { status: "applied" } };
  const { api, loads } = loadApi({ binding: { hardenCurrentProcess: () => expected } });
  assert.equal(loads(), 0);
  assert.equal(api.hardenCurrentProcess(), expected);
  assert.equal(loads(), 1);
});

test("unsupported systems never load native code", () => {
  for (const options of [{ platform: "linux" }, { platform: "darwin" }, { arch: "ia32" }]) {
    const { api, loads } = loadApi(options);
    assert.equal(api.hardenCurrentProcess().dacl.status, "unsupported");
    assert.equal(api.hardenCurrentProcess().extensionPoint.status, "unsupported");
    assert.equal(loads(), 0);
  }
});

test("Electron renderer and utility processes are rejected before native load", () => {
  for (const type of ["renderer", "utility"]) {
    const { api, loads } = loadApi({ type });
    assert.throws(() => api.hardenCurrentProcess(), { code: "ERR_PROCESS_HARDENING_CONTEXT" });
    assert.equal(loads(), 0);
  }
});

test("binding load failure is reported for both stages", () => {
  const { api } = loadApi({ loadError: new Error("missing binding") });
  const result = api.hardenCurrentProcess();
  assert.equal(result.dacl.status, "failed");
  assert.equal(result.extensionPoint.status, "failed");
  assert.match(result.dacl.message, /missing binding/);
});

test("ARM64 selects its own binary", () => {
  const { api } = loadApi({ arch: "arm64", binding: { hardenCurrentProcess: () => ({ dacl: { status: "applied" }, extensionPoint: { status: "applied" } }) } });
  assert.equal(api.hardenCurrentProcess().dacl.status, "applied");
});

test("crash prototype gates reject unsupported or non-test contexts before loading native code", () => {
  const blocked = [
    { options: { type: "browser" }, status: "disabled" },
    { options: { type: "browser", env: { DESKTOP_E2E_MODE: "true" } }, status: "disabled" },
    { options: { type: "renderer", env: { DESKTOP_E2E_MODE: "true", ONEKEY_TEST_LOCAL_CRASH_HANDLER: "1" } }, status: "unsupported" },
    { options: { type: "browser", electron: "44.5.2", env: { DESKTOP_E2E_MODE: "true", ONEKEY_TEST_LOCAL_CRASH_HANDLER: "1" } }, status: "unsupported" },
    { options: { type: "browser", arch: "arm64", env: { DESKTOP_E2E_MODE: "true", ONEKEY_TEST_LOCAL_CRASH_HANDLER: "1" } }, status: "unsupported" },
  ];
  for (const { options, status } of blocked) {
    const { api, loads } = loadApi(options);
    assert.equal(api.startLocalCrashHandler().status, status);
    assert.throws(() => api.triggerLocalCrashForTest(), /unavailable/);
    assert.equal(loads(), 0);
  }
});
