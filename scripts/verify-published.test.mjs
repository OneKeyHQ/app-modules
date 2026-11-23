import assert from "node:assert/strict";
import test from "node:test";

import {
  checkPackument,
  mapConcurrent,
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

test("checks a missing package at most 41 times", async () => {
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
    assert.equal(checks, 41);
    assert.equal(waits, 40);
    assert.deepEqual(missing.map(({ name }) => name), [workspaces[0].name]);
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("accepts a package that becomes visible after ten minutes", async (t) => {
  t.mock.timers.enable({ apis: ["setTimeout"] });
  const originalFetch = globalThis.fetch;
  let checks = 0;
  globalThis.fetch = async () => {
    checks += 1;
    return {
      ok: true,
      json: async () =>
        checks < 41
          ? { versions: {}, "dist-tags": {} }
          : {
              versions: { "3.0.1": {} },
              "dist-tags": { latest: "3.0.1" },
            },
    };
  };
  try {
    const verification = verifyPublished([workspaces[0]], "latest", {
      log: () => {},
    });
    for (let interval = 0; interval < 40; interval += 1) {
      await new Promise(setImmediate);
      assert.equal(checks, interval + 1);
      t.mock.timers.tick(14_999);
      await new Promise(setImmediate);
      assert.equal(checks, interval + 1);
      t.mock.timers.tick(1);
    }
    assert.deepEqual(await verification, []);
    assert.equal(checks, 41);
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("maps items with bounded concurrency while preserving input order", async () => {
  let active = 0;
  let maxActive = 0;
  const items = [1, 2, 3, 4, 5];
  const results = await mapConcurrent(
    items,
    async (value) => {
      active += 1;
      maxActive = Math.max(maxActive, active);
      await new Promise((resolve) => setTimeout(resolve, 5));
      active -= 1;
      return value * 10;
    },
    2
  );
  assert.deepEqual(results, [10, 20, 30, 40, 50]);
  assert.ok(maxActive <= 2);
});
