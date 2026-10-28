import assert from "node:assert/strict";
import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import test from "node:test";

import {
  classifyFile,
  findPackageDirs,
  selectSyncEntries,
} from "./sync-from-node-modules.mjs";

const buf = (text) => (text === null ? null : Buffer.from(text));

test("considers published source entries but never build output or docs", () => {
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), "sync-entries-"));
  fs.writeFileSync(path.join(dir, "NativeOverlay.podspec"), "");
  fs.writeFileSync(path.join(dir, "Other.txt"), "");
  assert.deepEqual(
    selectSyncEntries(
      ["src", "lib", "docs", "ios", "*.podspec", "!ios/build", "README.md"],
      dir,
    ),
    ["src", "ios", "NativeOverlay.podspec"],
  );
});

test("copies only files edited in node_modules", () => {
  assert.equal(
    classifyFile({ edited: buf("a"), pristine: buf("a"), repo: buf("newer") }),
    "untouched",
    "an older install must not roll the repository back",
  );
  assert.equal(
    classifyFile({ edited: buf("b"), pristine: buf("a"), repo: buf("b") }),
    "in-sync",
  );
  assert.equal(
    classifyFile({ edited: buf("b"), pristine: buf("a"), repo: buf("a") }),
    "copy",
  );
  assert.equal(
    classifyFile({ edited: buf("new"), pristine: null, repo: null }),
    "copy",
  );
  assert.equal(
    classifyFile({ edited: buf("b"), pristine: buf("a"), repo: buf("c") }),
    "conflict",
  );
});

test("resolves package directories by package name", () => {
  const dirs = findPackageDirs();
  assert.match(
    dirs.get("@onekeyfe/react-native-native-overlay") ?? "",
    /native-views[/\\]react-native-native-overlay$/,
  );
});
