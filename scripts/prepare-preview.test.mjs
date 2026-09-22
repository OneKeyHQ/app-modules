import assert from "node:assert/strict";
import test from "node:test";

import {
  makePreviewVersion,
  updatePreviewManifest,
} from "./prepare-preview.mjs";

test("increments the patch and appends the numeric alpha identifier", () => {
  assert.equal(makePreviewVersion("3.0.152", "27"), "3.0.153-alpha.27");
});

test("requires a stable preview base", () => {
  assert.throws(
    () => makePreviewVersion("3.0.153-alpha.1", "2"),
    /must be a stable version/
  );
});

test("updates package and exact internal dependency versions", () => {
  const manifest = {
    name: "@onekeyfe/module-a",
    version: "3.0.152",
    dependencies: {
      "@onekeyfe/module-b": "3.0.152",
      external: "^1.0.0",
    },
  };
  assert.deepEqual(
    updatePreviewManifest(manifest, {
      currentVersion: "3.0.152",
      previewVersion: "3.0.153-alpha.27",
      workspaceNames: new Set([
        "@onekeyfe/module-a",
        "@onekeyfe/module-b",
      ]),
    }),
    {
      ...manifest,
      version: "3.0.153-alpha.27",
      dependencies: {
        "@onekeyfe/module-b": "3.0.153-alpha.27",
        external: "^1.0.0",
      },
    }
  );
});
