import assert from "node:assert/strict";
import test from "node:test";

import { validateNpmDistTag } from "./validate-npm-dist-tag.mjs";

const prereleaseWorkspaces = [
  { name: "@onekeyfe/module-a", version: "3.0.81-alpha.1" },
  { name: "@onekeyfe/module-b", version: "3.0.81-alpha.2" },
];

test("allows prerelease workspaces on the next dist-tag", () => {
  assert.doesNotThrow(() => validateNpmDistTag("next", prereleaseWorkspaces));
});

test("rejects prerelease workspaces on the latest dist-tag", () => {
  assert.throws(
    () => validateNpmDistTag("latest", prereleaseWorkspaces),
    /Refusing to publish prerelease workspaces with the latest dist-tag/
  );
});

test("allows stable workspaces on the latest dist-tag", () => {
  assert.doesNotThrow(() =>
    validateNpmDistTag("latest", [
      { name: "@onekeyfe/module-a", version: "3.0.81" },
    ])
  );
});

test("rejects unsupported dist-tags", () => {
  assert.throws(
    () => validateNpmDistTag("beta", prereleaseWorkspaces),
    /Unsupported npm dist-tag/
  );
});

test("allows an isolated PR preview without moving standard tags", () => {
  assert.doesNotThrow(() => validateNpmDistTag("pr-145", prereleaseWorkspaces));
});

test("rejects stable versions on the isolated PR preview tag", () => {
  assert.throws(() => validateNpmDistTag("pr-145", [
    { name: "@onekeyfe/module-a", version: "3.0.178" },
  ]), /requires prerelease versions/);
});
