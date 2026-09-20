import assert from "node:assert/strict";
import test from "node:test";

import { updateMobileManifest } from "./sync-app-monorepo.mjs";

const workspaces = [
  { name: "@onekeyfe/react-native-image", version: "3.0.152" },
  { name: "@onekeyfe/react-native-native-list", version: "3.0.152" },
];

test("updates only packages published by app-modules", () => {
  const input = `${JSON.stringify(
    {
      dependencies: {
        "@onekeyfe/react-native-image": "3.0.151",
        "@onekeyfe/react-native-native-list": "3.0.151",
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
    "3.0.152"
  );
});

test("does nothing when app-monorepo already uses the release", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"3.0.152"}}';
  assert.deepEqual(updateMobileManifest(input, workspaces), {
    text: input,
    updated: [],
  });
});

test("refuses to downgrade a newer app-monorepo dependency", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"3.0.153"}}';
  assert.throws(
    () => updateMobileManifest(input, workspaces),
    /Refusing to downgrade/
  );
});

test("requires a synchronized app-modules release", () => {
  assert.throws(
    () =>
      updateMobileManifest("{}", [
        { ...workspaces[0], version: "3.0.151" },
        workspaces[1],
      ]),
    /versions must match/
  );
});
