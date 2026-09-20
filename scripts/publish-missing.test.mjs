import assert from "node:assert/strict";
import test from "node:test";

import { selectUnpublished } from "./publish-missing.mjs";

const workspaces = [
  { name: "@onekeyfe/module-a", version: "3.0.152" },
  { name: "@onekeyfe/module-b", version: "3.0.152" },
];

test("skips already published packages on a retry", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "3.0.152": {} }, "dist-tags": { latest: "3.0.152" } },
    ],
    ["@onekeyfe/module-b", { versions: { "3.0.151": {} } }],
  ]);
  assert.deepEqual(selectUnpublished(workspaces, packuments, "latest"), [
    workspaces[1],
  ]);
});

test("refuses an existing version with the wrong dist-tag", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "3.0.152": {} }, "dist-tags": { latest: "3.0.151" } },
    ],
  ]);
  assert.throws(
    () => selectUnpublished(workspaces, packuments, "latest"),
    /already exists but latest points at 3\.0\.151/
  );
});

test("never moves latest backwards when a newer release won the race", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { "dist-tags": { latest: "3.0.153" }, versions: {} },
    ],
  ]);
  assert.throws(
    () => selectUnpublished(workspaces, packuments, "latest"),
    /Refusing to move .* latest backwards/
  );
});
