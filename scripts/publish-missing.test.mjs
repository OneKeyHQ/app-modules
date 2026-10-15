import assert from "node:assert/strict";
import test from "node:test";

import { selectRetags, selectUnpublished } from "./publish-missing.mjs";

const workspaces = [
  { name: "@onekeyfe/module-a", version: "6.7.0-2" },
  { name: "@onekeyfe/module-b", version: "6.7.0-2" },
];

test("skips already published packages on a retry", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "6.7.0-2": {} }, "dist-tags": { latest: "6.7.0-2" } },
    ],
    ["@onekeyfe/module-b", { versions: { "6.7.0-1": {} } }],
  ]);
  assert.deepEqual(selectUnpublished(workspaces, packuments, "latest"), [
    workspaces[1],
  ]);
});

test("retags an existing version instead of trying to republish it", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "6.7.0-2": {} }, "dist-tags": { latest: "6.7.0-1" } },
    ],
  ]);
  assert.deepEqual(selectUnpublished(workspaces, packuments, "latest"), [
    workspaces[1],
  ]);
  assert.deepEqual(selectRetags(workspaces, packuments, "latest"), [
    workspaces[0],
  ]);
});

test("never moves latest backwards when a newer release won the race", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { "dist-tags": { latest: "6.7.0-3" }, versions: {} },
    ],
  ]);
  assert.throws(
    () => selectUnpublished(workspaces, packuments, "latest"),
    /Refusing to move .* latest backwards/
  );
});

test("protects numeric counters, alpha counters and App baselines from rollback", () => {
  for (const [tag, published, target] of [
    ["latest", "6.7.0-10", "6.7.0-2"],
    ["latest", "6.7.1-1", "6.7.0-99"],
    ["next", "6.7.0-alpha.10", "6.7.0-alpha.2"],
  ]) {
    const packages = [{ name: "module-a", version: target }];
    const documents = new Map([
      ["module-a", { "dist-tags": { [tag]: published } }],
    ]);
    assert.throws(
      () => selectUnpublished(packages, documents, tag),
      /backwards/
    );
  }
});
