"use strict";

const assert = require("node:assert/strict");
const test = require("node:test");
const api = require("../index.cjs");
const nativeWindows =
  process.platform === "win32" && ["x64", "arm64"].includes(process.arch);

test(
  "native availability returns a documented status",
  { skip: !nativeWindows },
  async () => {
    assert.ok(
      Object.values(api.Availability).includes(await api.checkAvailability()),
    );
  },
);

test(
  "native verification rejects unavailable HWNDs without prompting",
  { skip: !nativeWindows },
  async () => {
    for (const windowHandle of [Buffer.alloc(8), Buffer.alloc(8, 255)]) {
      await assert.rejects(
        api.requestVerification({
          windowHandle,
          message: "Native validation test",
        }),
        { code: "InvalidArg" },
      );
    }
  },
);
