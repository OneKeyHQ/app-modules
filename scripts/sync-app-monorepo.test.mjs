import assert from "node:assert/strict";
import test from "node:test";

import { updateMobileManifest } from "./sync-app-monorepo.mjs";

const workspaces = [
  { name: "@onekeyfe/react-native-image", version: "6.7.0-2" },
  { name: "@onekeyfe/react-native-native-list", version: "6.7.0-2" },
  { name: "@onekeyfe/react-native-get-random-values", version: "6.7.0-2" },
];

test("updates only packages published by app-modules", () => {
  const input = `${JSON.stringify(
    {
      dependencies: {
        "@onekeyfe/react-native-image": "6.7.0-1",
        "@onekeyfe/react-native-native-list": "6.7.0-1",
        "@onekeyfe/react-native-ble-utils": "0.1.6",
      },
    },
    null,
    2
  )}\n`;
  const result = updateMobileManifest(input, workspaces);
  assert.deepEqual(result.updated, [
    "@onekeyfe/react-native-image",
    "@onekeyfe/react-native-native-list",
  ]);
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-ble-utils"],
    "0.1.6"
  );
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-image"],
    "6.7.0-2"
  );
});

test("does nothing when app-monorepo already uses the release", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"6.7.0-2"}}';
  assert.deepEqual(updateMobileManifest(input, workspaces), {
    text: input,
    updated: [],
  });
});

test("updates npm aliases without changing their dependency keys", () => {
  const input =
    '{"dependencies":{"react-native-get-random-values":"npm:@onekeyfe/react-native-get-random-values@6.7.0-1","unrelated":"npm:@onekeyfe/unrelated@1.0.0"}}';
  const result = updateMobileManifest(input, workspaces);
  assert.deepEqual(result.updated, ["react-native-get-random-values"]);
  assert.deepEqual(JSON.parse(result.text).dependencies, {
    "react-native-get-random-values":
      "npm:@onekeyfe/react-native-get-random-values@6.7.0-2",
    unrelated: "npm:@onekeyfe/unrelated@1.0.0",
  });
});

test("updates root dependency and resolution pins", () => {
  const input = JSON.stringify({
    dependencies: {
      "react-native-get-random-values":
        "npm:@onekeyfe/react-native-get-random-values@6.7.0-1",
    },
    resolutions: {
      "react-native-get-random-values":
        "npm:@onekeyfe/react-native-get-random-values@6.7.0-1",
    },
  });
  const result = updateMobileManifest(input, workspaces, [
    "dependencies",
    "resolutions",
  ]);
  const manifest = JSON.parse(result.text);
  assert.equal(
    manifest.dependencies["react-native-get-random-values"],
    "npm:@onekeyfe/react-native-get-random-values@6.7.0-2"
  );
  assert.equal(
    manifest.resolutions["react-native-get-random-values"],
    "npm:@onekeyfe/react-native-get-random-values@6.7.0-2"
  );
});

test("refuses to downgrade a newer app-monorepo dependency", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"6.7.0-3"}}';
  assert.throws(
    () => updateMobileManifest(input, workspaces),
    /Refusing to downgrade/
  );
});

test("refuses to downgrade an npm alias", () => {
  const input =
    '{"dependencies":{"react-native-get-random-values":"npm:@onekeyfe/react-native-get-random-values@6.7.0-3"}}';
  assert.throws(
    () => updateMobileManifest(input, workspaces),
    /Refusing to downgrade/
  );
});

test("updates a stable dependency to a newer alpha preview", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"6.7.0-2"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "6.7.0-alpha.248",
  }));
  const result = updateMobileManifest(input, previewWorkspaces);
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-image"],
    "6.7.0-alpha.248"
  );
});

test("allows switching a numeric release to an alpha preview on the same App baseline", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"6.7.0-3"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "6.7.0-alpha.248",
  }));
  assert.equal(
    JSON.parse(updateMobileManifest(input, previewWorkspaces).text)
      .dependencies["@onekeyfe/react-native-image"],
    "6.7.0-alpha.248"
  );
});

test("updates an alpha dependency to a newer alpha preview", () => {
  const input =
    '{"dependencies":{"@onekeyfe/react-native-image":"6.7.0-alpha.247"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "6.7.0-alpha.248",
  }));
  const result = updateMobileManifest(input, previewWorkspaces);
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-image"],
    "6.7.0-alpha.248"
  );
});

test("requires a synchronized app-modules release", () => {
  assert.throws(
    () =>
      updateMobileManifest("{}", [
        { ...workspaces[0], version: "6.7.0-1" },
        workspaces[1],
      ]),
    /versions must match/
  );
});

test("migrates legacy pins and promotes alpha pins to official numeric releases", () => {
  for (const version of ["3.0.178", "3.0.179-alpha.248", "6.7.0-alpha.999"]) {
    const input = JSON.stringify({
      dependencies: { "@onekeyfe/react-native-image": version },
    });
    assert.equal(
      JSON.parse(updateMobileManifest(input, workspaces).text).dependencies[
        "@onekeyfe/react-native-image"
      ],
      "6.7.0-2"
    );
  }
});

test("rejects older App baselines and stale alpha counters", () => {
  const alphaWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "6.7.0-alpha.2",
  }));
  for (const version of ["6.7.1-1", "6.7.0-alpha.10"]) {
    const input = JSON.stringify({
      dependencies: { "@onekeyfe/react-native-image": version },
    });
    assert.throws(
      () => updateMobileManifest(input, alphaWorkspaces),
      /Refusing to downgrade/
    );
  }
});
