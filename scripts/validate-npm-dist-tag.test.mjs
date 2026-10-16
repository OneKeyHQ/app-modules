import assert from "node:assert/strict";
import test from "node:test";

import {
  compareReleaseVersions,
  parseReleaseVersion,
  validateNpmDistTag,
} from "./validate-npm-dist-tag.mjs";

const workspaces = (version) => [
  { name: "@onekeyfe/module-a", version },
  { name: "@onekeyfe/module-b", version },
];

test("numeric releases require latest and alpha previews require next", () => {
  assert.doesNotThrow(() =>
    validateNpmDistTag("latest", workspaces("6.7.0-1"))
  );
  assert.doesNotThrow(() =>
    validateNpmDistTag("next", workspaces("6.7.0-alpha.1"))
  );
  assert.throws(
    () => validateNpmDistTag("latest", workspaces("6.7.0-alpha.1")),
    /cannot use/
  );
  assert.throws(
    () => validateNpmDistTag("next", workspaces("6.7.0-1")),
    /cannot use/
  );
  assert.throws(
    () => validateNpmDistTag("beta", workspaces("6.7.0-1")),
    /Unsupported npm dist-tag/
  );
});

test("rejects invalid versions instead of treating every hyphen as a preview", () => {
  for (const version of [
    "6.7.0",
    "6.7.0.1",
    "6.7.0-0",
    "6.7.0-01",
    "06.7.0-1",
    "6.7.0-alpha.0",
    "6.7.0-alpha.01",
    "6.7.0-beta.1",
    "6.7.0-1+build",
    "6.7.0-alpha.-1",
    "6.7.0-9007199254740992",
    undefined,
  ]) {
    assert.throws(() => parseReleaseVersion(version), /Expected <app-version>/);
  }
});

test("rejects empty or mismatched releases on either channel", () => {
  for (const tag of ["next", "latest"]) {
    assert.throws(() => validateNpmDistTag(tag, []), /versions must match/);
    assert.throws(
      () =>
        validateNpmDistTag(tag, [
          workspaces("6.7.0-1")[0],
          workspaces("6.7.0-2")[1],
        ]),
      /versions must match/
    );
  }
});

test("compares App baselines and same-channel counters numerically", () => {
  assert.ok(compareReleaseVersions("6.7.0-10", "6.7.0-2") > 0);
  assert.ok(compareReleaseVersions("6.7.0-alpha.10", "6.7.0-alpha.2") > 0);
  assert.ok(compareReleaseVersions("6.7.1-1", "6.7.0-99") > 0);
  assert.ok(compareReleaseVersions("6.7.1-alpha.1", "6.7.0-99") > 0);
  assert.ok(compareReleaseVersions("3.0.178", "6.7.0-1") < 0);
});

test("allows explicit channel changes without comparing independent counters", () => {
  assert.equal(compareReleaseVersions("6.7.0-alpha.99", "6.7.0-1"), 0);
  assert.equal(compareReleaseVersions("6.7.0-9", "6.7.0-alpha.1"), 0);
});

test("preserves the existing isolated PR tag for synchronized alpha previews", () => {
  assert.doesNotThrow(() =>
    validateNpmDistTag("pr-145", workspaces("6.7.0-alpha.1"))
  );
  assert.throws(
    () => validateNpmDistTag("pr-145", workspaces("6.7.0-1")),
    /cannot use/
  );
  assert.throws(
    () => validateNpmDistTag("pr-145", workspaces("6.7.0")),
    /Expected <app-version>/
  );
});
