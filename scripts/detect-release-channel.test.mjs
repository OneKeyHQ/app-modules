import assert from "node:assert/strict";
import test from "node:test";

import { detectReleaseChannel } from "./detect-release-channel.mjs";

const release = [
  { name: "@onekeyfe/module-a", version: "3.0.152" },
  { name: "@onekeyfe/module-b", version: "3.0.152" },
];

test("uses next when the change does not update package versions", () => {
  assert.equal(detectReleaseChannel(release, release), "next");
});

test("uses latest when a package version changes", () => {
  assert.equal(
    detectReleaseChannel(release, [
      { name: "@onekeyfe/module-a", version: "3.0.153" },
      release[1],
    ]),
    "latest"
  );
});

test("uses latest when a publishable package is added", () => {
  assert.equal(
    detectReleaseChannel(release, [
      ...release,
      { name: "@onekeyfe/module-c", version: "3.0.153" },
    ]),
    "latest"
  );
});
