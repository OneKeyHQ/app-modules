import assert from "node:assert/strict";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";

import {
  makePreviewVersion,
  preparePreview,
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

test("automatic counters exceed committed and published alphas on the same baseline", () => {
  assert.equal(makePreviewVersion("6.7.0-alpha.248", "100"), "6.7.0-alpha.249");
  assert.equal(
    makePreviewVersion("6.7.0-1", "100", [
      "6.7.0-alpha.500",
      "6.6.0-alpha.9999",
      "3.0.178",
    ]),
    "6.7.0-alpha.501"
  );
  assert.throws(
    () => makePreviewVersion("6.7.0-alpha.9007199254740991", "100"),
    /Expected <app-version>/
  );
});

async function previewRepo(t, version) {
  const root = await mkdtemp(join(tmpdir(), "app-modules-preview-"));
  t.after(() => rm(root, { recursive: true, force: true }));
  await writeFile(
    join(root, "package.json"),
    JSON.stringify({ workspaces: ["packages/*"] })
  );
  for (const name of ["module-a", "module-b"]) {
    await mkdir(join(root, "packages", name), { recursive: true });
    await writeFile(
      join(root, "packages", name, "package.json"),
      JSON.stringify({
        name,
        version,
        scripts: { release: "npm publish" },
        peerDependencies: { "module-a": version },
      })
    );
  }
  return root;
}

test("prepares one synchronized preview above every published next and isolated tag", async (t) => {
  const root = await previewRepo(t, "6.7.0-alpha.248");
  const requested = [];
  t.mock.method(globalThis, "fetch", async (url) => {
    requested.push(url);
    const tags = url.includes("module-a")
      ? { next: "6.7.0-alpha.600" }
      : { next: "6.7.0-alpha.550", "pr-145": "6.7.0-alpha.700" };
    return new Response(JSON.stringify({ "dist-tags": tags }));
  });
  assert.equal(await preparePreview(root, "100", "pr-145"), "6.7.0-alpha.701");
  assert.deepEqual(requested.sort(), [
    "https://registry.npmjs.org/module-a?write=true",
    "https://registry.npmjs.org/module-b?write=true",
  ]);
  for (const name of ["module-a", "module-b"]) {
    const manifest = JSON.parse(
      await readFile(join(root, "packages", name, "package.json"), "utf8")
    );
    assert.equal(manifest.version, "6.7.0-alpha.701");
    assert.equal(manifest.peerDependencies["module-a"], manifest.version);
  }
});

test("new packages without registry documents can still get a preview", async (t) => {
  const root = await previewRepo(t, "6.7.0-1");
  t.mock.method(
    globalThis,
    "fetch",
    async () => new Response(null, { status: 404 })
  );
  assert.equal(await preparePreview(root, "100"), "6.7.0-alpha.100");
});

test("registry failure aborts before changing any manifest", async (t) => {
  const root = await previewRepo(t, "6.7.0-alpha.248");
  t.mock.method(globalThis, "fetch", async (url) =>
    url.includes("module-a")
      ? new Response(
          JSON.stringify({ "dist-tags": { next: "6.7.0-alpha.600" } })
        )
      : new Response(null, { status: 503 })
  );
  await assert.rejects(
    preparePreview(root, "100"),
    /Registry returned HTTP 503/
  );
  for (const name of ["module-a", "module-b"]) {
    const manifest = JSON.parse(
      await readFile(join(root, "packages", name, "package.json"), "utf8")
    );
    assert.equal(manifest.version, "6.7.0-alpha.248");
    assert.equal(manifest.peerDependencies["module-a"], manifest.version);
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
