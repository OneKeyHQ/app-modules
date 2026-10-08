import assert from "node:assert/strict";
import test from "node:test";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { dirname, join } from "node:path";

import { syncAppMonorepo, updateMobileManifest } from "./sync-app-monorepo.mjs";

const workspaces = [
  { name: "@onekeyfe/react-native-image", version: "3.0.152" },
  { name: "@onekeyfe/react-native-native-list", version: "3.0.152" },
  { name: "@onekeyfe/react-native-get-random-values", version: "3.0.152" },
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

test("updates npm aliases without changing their dependency keys", () => {
  const input =
    '{"dependencies":{"react-native-get-random-values":"npm:@onekeyfe/react-native-get-random-values@3.0.151","unrelated":"npm:@onekeyfe/unrelated@1.0.0"}}';
  const result = updateMobileManifest(input, workspaces);
  assert.deepEqual(result.updated, ["react-native-get-random-values"]);
  assert.deepEqual(JSON.parse(result.text).dependencies, {
    "react-native-get-random-values":
      "npm:@onekeyfe/react-native-get-random-values@3.0.152",
    unrelated: "npm:@onekeyfe/unrelated@1.0.0",
  });
});

test("updates root dependency and resolution pins", () => {
  const input = JSON.stringify({
    dependencies: {
      "react-native-get-random-values":
        "npm:@onekeyfe/react-native-get-random-values@3.0.151",
    },
    resolutions: {
      "react-native-get-random-values":
        "npm:@onekeyfe/react-native-get-random-values@3.0.151",
    },
  });
  const result = updateMobileManifest(input, workspaces, [
    "dependencies",
    "resolutions",
  ]);
  const manifest = JSON.parse(result.text);
  assert.equal(
    manifest.dependencies["react-native-get-random-values"],
    "npm:@onekeyfe/react-native-get-random-values@3.0.152"
  );
  assert.equal(
    manifest.resolutions["react-native-get-random-values"],
    "npm:@onekeyfe/react-native-get-random-values@3.0.152"
  );
});

test("refuses to downgrade a newer app-monorepo dependency", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"3.0.153"}}';
  assert.throws(
    () => updateMobileManifest(input, workspaces),
    /Refusing to downgrade/
  );
});

test("refuses to downgrade an npm alias", () => {
  const input =
    '{"dependencies":{"react-native-get-random-values":"npm:@onekeyfe/react-native-get-random-values@3.0.153"}}';
  assert.throws(
    () => updateMobileManifest(input, workspaces),
    /Refusing to downgrade/
  );
});

test("updates a stable dependency to a newer alpha preview", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"3.0.152"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "3.0.153-alpha.248",
  }));
  const result = updateMobileManifest(input, previewWorkspaces);
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-image"],
    "3.0.153-alpha.248"
  );
});

test("refuses to replace a stable release with its alpha preview", () => {
  const input = '{"dependencies":{"@onekeyfe/react-native-image":"3.0.153"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "3.0.153-alpha.248",
  }));
  assert.throws(
    () => updateMobileManifest(input, previewWorkspaces),
    /Refusing to downgrade/
  );
});

test("updates an alpha dependency to a newer alpha preview", () => {
  const input =
    '{"dependencies":{"@onekeyfe/react-native-image":"3.0.153-alpha.247"}}';
  const previewWorkspaces = workspaces.map((workspace) => ({
    ...workspace,
    version: "3.0.153-alpha.248",
  }));
  const result = updateMobileManifest(input, previewWorkspaces);
  assert.equal(
    JSON.parse(result.text).dependencies["@onekeyfe/react-native-image"],
    "3.0.153-alpha.248"
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

test("migrates the temporary overlay workspace to its published exact version", () => {
  const result = updateMobileManifest(
    JSON.stringify({
      dependencies: {
        "@onekeyfe/react-native-native-overlay": "workspace:*",
        "@onekeyhq/components": "workspace:*",
      },
    }),
    [
      {
        name: "@onekeyfe/react-native-native-overlay",
        version: "3.0.162-alpha.268",
      },
    ]
  );
  assert.deepEqual(JSON.parse(result.text).dependencies, {
    "@onekeyfe/react-native-native-overlay": "3.0.162-alpha.268",
    "@onekeyhq/components": "workspace:*",
  });
});

test("does not silently migrate other published development workspaces", () => {
  assert.throws(
    () =>
      updateMobileManifest(
        JSON.stringify({
          dependencies: {
            "@onekeyfe/react-native-image": "workspace:*",
          },
        }),
        workspaces
      ),
    /Expected an exact stable or alpha version/
  );
});

test("syncs overlay consumers in mobile, components, kit and playground", async () => {
  const root = await mkdtemp(join(tmpdir(), "overlay-sync-"));
  const paths = [
    "apps/mobile/package.json",
    "packages/components/package.json",
    "packages/kit/package.json",
    "apps/playground/package.json",
    "package.json",
  ];
  const name = "@onekeyfe/react-native-native-overlay";
  try {
    for (const path of paths) {
      await mkdir(dirname(join(root, path)), { recursive: true });
      await writeFile(
        join(root, path),
        JSON.stringify({ dependencies: { [name]: "workspace:*" } })
      );
    }
    await syncAppMonorepo(root, [{ name, version: "3.0.162-alpha.268" }]);
    for (const path of paths) {
      const manifest = JSON.parse(await readFile(join(root, path), "utf8"));
      assert.equal(manifest.dependencies[name], "3.0.162-alpha.268");
    }
  } finally {
    await rm(root, { recursive: true, force: true });
  }
});
