"use strict";

const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const test = require("node:test");
const vm = require("node:vm");
const source = readFileSync(require.resolve("../index.cjs"), "utf8");

function loadApi(native = {}, platform = "win32", arch = "x64") {
  const exports = {};
  vm.runInNewContext(source, {
    exports,
    Buffer,
    process: { platform, arch },
    require(filename) {
      assert.equal(
        filename,
        `./electron-windows-hello.win32-${arch}-msvc.node`,
      );
      return native;
    },
  });
  return exports;
}

test("maps every Windows verification outcome without treating failures as success", async () => {
  const api = loadApi({ requestVerification: async () => result });
  let result = 0;
  for (const expected of Object.values(api.VerificationResult)) {
    assert.equal(
      await api.requestVerification({
        windowHandle: Buffer.alloc(8),
        message: "Unlock wallet",
      }),
      expected,
    );
    result += 1;
  }
  await assert.rejects(
    api.requestVerification({
      windowHandle: Buffer.alloc(8),
      message: "Unlock wallet",
    }),
    { code: "ERR_WINDOWS_HELLO_RESULT" },
  );
});

test("a second request cannot share the first request authorization", async () => {
  let complete;
  let calls = 0;
  const api = loadApi({
    requestVerification: () => {
      calls += 1;
      return new Promise((resolve) => {
        complete = resolve;
      });
    },
  });
  const first = api.requestVerification({
    windowHandle: Buffer.alloc(8),
    message: "Unlock wallet",
  });
  assert.equal(
    await api.requestVerification({
      windowHandle: Buffer.alloc(8),
      message: "Reveal recovery phrase",
    }),
    api.VerificationResult.DeviceBusy,
  );
  assert.equal(calls, 1);
  complete(0);
  assert.equal(await first, api.VerificationResult.Verified);
});

test("a native rejection releases the request guard and preserves the error", async () => {
  const nativeError = new Error("native failure");
  let calls = 0;
  const api = loadApi({
    requestVerification: async () => {
      calls += 1;
      if (calls === 1) throw nativeError;
      return 6;
    },
  });
  await assert.rejects(
    api.requestVerification({
      windowHandle: Buffer.alloc(8),
      message: "Unlock wallet",
    }),
    (error) => error === nativeError,
  );
  assert.equal(
    await api.requestVerification({
      windowHandle: Buffer.alloc(8),
      message: "Unlock wallet",
    }),
    api.VerificationResult.Canceled,
  );
});

test("copies handle bytes before passing them to native code", async () => {
  const handle = Buffer.alloc(8, 1);
  const api = loadApi({
    requestVerification: async (received, message) => {
      handle.fill(0);
      assert.equal(received[0], 1);
      assert.equal(message, "Unlock wallet");
      return 0;
    },
  });
  await api.requestVerification({
    windowHandle: handle,
    message: "Unlock wallet",
  });
});

test("rejects malformed handles, messages, and wildcard foreground grants", async () => {
  const api = loadApi();
  for (const windowHandle of [undefined, [], Buffer.alloc(4)]) {
    await assert.rejects(
      api.requestVerification({ windowHandle, message: "Unlock wallet" }),
      { code: "ERR_WINDOWS_HELLO_HANDLE" },
    );
  }
  for (const message of [undefined, "", "   ", 7]) {
    await assert.rejects(
      api.requestVerification({ windowHandle: Buffer.alloc(8), message }),
      { code: "ERR_WINDOWS_HELLO_MESSAGE" },
    );
  }
  for (const processId of [0, -1, 0xffffffff, 1.5, "123"]) {
    assert.throws(() => api.allowSetForegroundWindow(processId), {
      code: "ERR_WINDOWS_HELLO_PROCESS",
    });
  }
});

test("does not load Windows binaries on unsupported platforms", async () => {
  for (const [platform, arch] of [
    ["linux", "x64"],
    ["darwin", "arm64"],
    ["win32", "ia32"],
  ]) {
    const api = loadApi({}, platform, arch);
    assert.equal(
      await api.checkAvailability(),
      api.Availability.DeviceNotPresent,
    );
    assert.equal(api.allowSetForegroundWindow(123), false);
    await assert.rejects(
      api.requestVerification({
        windowHandle: Buffer.alloc(8),
        message: "Unlock wallet",
      }),
      { code: "ERR_WINDOWS_HELLO_PLATFORM" },
    );
  }
});

test("uses an ARM64 binary in an ARM64 Node or Electron process", async () => {
  const api = loadApi({ checkAvailability: async () => 0 }, "win32", "arm64");
  assert.equal(await api.checkAvailability(), api.Availability.Available);
});

test("supports named imports from ESM", async () => {
  const api = await import("../index.cjs");
  assert.equal(typeof api.requestVerification, "function");
  assert.equal(api.VerificationResult.Verified, "verified");
});
