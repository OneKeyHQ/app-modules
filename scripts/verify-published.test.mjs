import assert from "node:assert/strict";
import test from "node:test";

import {
  checkPackument,
  packumentUrl,
  selectWorkspaces,
  verifyPublished,
} from "./verify-published.mjs";

const workspaces = [
  { name: "@onekeyfe/module-a", version: "3.0.1" },
  { name: "@onekeyfe/module-b", version: "3.0.1" },
];

test("verifies every workspace when no single workspace is given", () => {
  assert.deepEqual(selectWorkspaces(workspaces, ""), workspaces);
  assert.deepEqual(selectWorkspaces(workspaces, undefined), workspaces);
});

test("narrows to one workspace for a single-package retry", () => {
  assert.deepEqual(selectWorkspaces(workspaces, "@onekeyfe/module-b"), [
    workspaces[1],
  ]);
});

test("rejects an unknown workspace name rather than verifying nothing", () => {
  assert.throws(
    () => selectWorkspaces(workspaces, "@onekeyfe/typo"),
    /Unknown workspace @onekeyfe\/typo/
  );
});

test("bypasses the CDN so a fresh publish is not read as missing", () => {
  assert.equal(
    packumentUrl("@onekeyfe/module-a"),
    "https://registry.npmjs.org/%40onekeyfe%2Fmodule-a?write=true"
  );
});

test("accepts a version that is listed and carries the dist-tag", () => {
  const packument = {
    versions: { "3.0.1": {} },
    "dist-tags": { latest: "3.0.1" },
  };
  assert.deepEqual(checkPackument(packument, "3.0.1", "latest"), { ok: true });
});

test("rejects the staged-but-never-committed case", () => {
  // What @onekeyfe/react-native-bundle-crypto@3.0.137 looked like: npm printed
  // a successful publish, the packument never listed the version.
  const packument = {
    versions: { "3.0.0": {} },
    "dist-tags": { latest: "3.0.0" },
  };
  const result = checkPackument(packument, "3.0.1", "latest");
  assert.equal(result.ok, false);
  assert.match(result.reason, /3\.0\.1 is absent from the registry/);
});

test("rejects a version that is present but not tagged", () => {
  const packument = {
    versions: { "3.0.0": {}, "3.0.1": {} },
    "dist-tags": { latest: "3.0.0" },
  };
  const result = checkPackument(packument, "3.0.1", "latest");
  assert.equal(result.ok, false);
  assert.match(result.reason, /dist-tag latest points at 3\.0\.0/);
});

test("reports every package that never became available", async () => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => ({
    ok: true,
    json: async () => ({ versions: {}, "dist-tags": {} }),
  });
  try {
    const missing = await verifyPublished(workspaces, "latest", {
      maxAttempts: 1,
      log: () => {},
    });
    assert.deepEqual(
      missing.map(({ name }) => name),
      ["@onekeyfe/module-a", "@onekeyfe/module-b"]
    );
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("returns nothing missing once every package is visible", async () => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => ({
    ok: true,
    json: async () => ({
      versions: { "3.0.1": {} },
      "dist-tags": { latest: "3.0.1" },
    }),
  });
  try {
    const missing = await verifyPublished(workspaces, "latest", {
      maxAttempts: 1,
      log: () => {},
    });
    assert.deepEqual(missing, []);
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("checks a missing package at most ten times", async () => {
  const originalFetch = globalThis.fetch;
  let checks = 0;
  let waits = 0;
  globalThis.fetch = async () => {
    checks += 1;
    return {
      ok: true,
      json: async () => ({ versions: {}, "dist-tags": {} }),
    };
  };
  try {
    const missing = await verifyPublished([workspaces[0]], "latest", {
      pollIntervalMs: 0,
      log: (message) => {
        if (message.includes("waiting")) waits += 1;
      },
    });
    assert.equal(checks, 10);
    assert.equal(waits, 9);
    assert.deepEqual(missing.map(({ name }) => name), [workspaces[0].name]);
  } finally {
    globalThis.fetch = originalFetch;
  }
});
