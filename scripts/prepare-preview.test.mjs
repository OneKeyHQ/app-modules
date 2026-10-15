import assert from "node:assert/strict";
import test from "node:test";

import {
  makePreviewVersion,
  updatePreviewManifest,
} from "./prepare-preview.mjs";

test("preserves the App baseline and appends the alpha counter", () => {
  assert.equal(makePreviewVersion("6.7.0-2", "27"), "6.7.0-alpha.27");
  assert.equal(makePreviewVersion("6.7.0-alpha.26", "27"), "6.7.0-alpha.27");
  for (const counter of ["0", "01", "-1", "1.2", undefined]) {
    assert.throws(
      () => makePreviewVersion("6.7.0-2", counter),
      /Expected <app-version>/
    );
  }
});

test("rejects a legacy plain preview base", () => {
  assert.throws(
    () => makePreviewVersion("6.7.0", "2"),
    /Expected <app-version>/
  );
});

test("updates package and exact internal dependency versions", () => {
  const manifest = {
    name: "@onekeyfe/module-a",
    version: "6.7.0-2",
    dependencies: {
      "@onekeyfe/module-b": "6.7.0-2",
      external: "^1.0.0",
    },
  };
  assert.deepEqual(
    updatePreviewManifest(manifest, {
      currentVersion: "6.7.0-2",
      previewVersion: "6.7.0-alpha.27",
      workspaceNames: new Set(["@onekeyfe/module-a", "@onekeyfe/module-b"]),
    }),
    {
      ...manifest,
      version: "6.7.0-alpha.27",
      dependencies: {
        "@onekeyfe/module-b": "6.7.0-alpha.27",
        external: "^1.0.0",
      },
    }
  );
});
