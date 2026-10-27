import assert from "node:assert/strict";
import test from "node:test";

import { detectReleaseChannel } from "./detect-release-channel.mjs";

const release = [
  { name: "@onekeyfe/module-a", version: "6.7.0-2" },
  { name: "@onekeyfe/module-b", version: "6.7.0-2" },
];

test("uses next when the change does not update package versions", () => {
  assert.equal(detectReleaseChannel(release, release), "next");
});

test("uses latest when a package version changes", () => {
  assert.equal(
    detectReleaseChannel(release, [
      { name: "@onekeyfe/module-a", version: "6.7.0-3" },
      { ...release[1], version: "6.7.0-3" },
    ]),
    "latest"
  );
});

test("uses latest when an existing package version changes alongside an added package", () => {
  assert.equal(
    detectReleaseChannel(release, [
      ...release.map((workspace) => ({ ...workspace, version: "6.7.0-3" })),
      { name: "@onekeyfe/module-c", version: "6.7.0-3" },
    ]),
    "latest"
  );
});

test("uses next when only a publishable package is added at the current version", () => {
  assert.equal(
    detectReleaseChannel(release, [
      ...release,
      { name: "@onekeyfe/module-c", version: "6.7.0-2" },
    ]),
    "next"
  );
});

test("uses next when a newly added package has no version at the base ref", () => {
  assert.equal(
    detectReleaseChannel(
      [...release, { name: "@onekeyfe/module-c", version: undefined }],
      [...release, { name: "@onekeyfe/module-c", version: "6.7.0-2" }]
    ),
    "next"
  );
});

test("uses next for explicit alpha version changes and rejects mixed versions", () => {
  assert.equal(
    detectReleaseChannel(
      release,
      release.map((workspace) => ({ ...workspace, version: "6.7.0-alpha.12" }))
    ),
    "next"
  );
  assert.throws(
    () =>
      detectReleaseChannel(release, [
        release[0],
        { ...release[1], version: "6.7.0-3" },
      ]),
    /versions must match/
  );
});
