import assert from "node:assert/strict";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";

import {
  makePreviewVersion,
  makeStableVersion,
  preparePreview,
  setWorkspaceVersions,
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

test("stable revisions increment the source and every published latest counter without using alpha counters", () => {
  assert.equal(makeStableVersion("6.7.0-26", ["6.7.0-26"]), "6.7.0-27");
  assert.equal(
    makeStableVersion("6.7.0-26", ["6.7.0-27", "6.7.0-28"]),
    "6.7.0-29"
  );
  assert.equal(makeStableVersion("6.7.0-alpha.900", ["6.7.0-26"]), "6.7.0-27");
  assert.equal(makeStableVersion("6.7.0-alpha.900"), "6.7.0-1");
  assert.equal(
    makeStableVersion("6.7.1-alpha.1", ["6.7.0-26", "3.0.178"]),
    "6.7.1-1"
  );
  assert.throws(
    () => makeStableVersion("6.7.0-26", ["6.7.1-1"]),
    /older App baseline/
  );
  assert.throws(
    () => makeStableVersion("6.7.0-9007199254740991"),
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

test("prepares one synchronized stable version above a partially published release", async (t) => {
  const root = await previewRepo(t, "6.7.0-alpha.900");
  t.mock.method(
    globalThis,
    "fetch",
    async (url) =>
      new Response(
        JSON.stringify({
          "dist-tags": {
            latest: url.includes("module-a") ? "6.7.0-26" : "6.7.0-27",
            next: "6.7.0-alpha.999",
          },
        })
      )
  );
  assert.equal(await preparePreview(root, "500", "latest"), "6.7.0-28");
  for (const name of ["module-a", "module-b"]) {
    const manifest = JSON.parse(
      await readFile(join(root, "packages", name, "package.json"), "utf8")
    );
    assert.equal(manifest.version, "6.7.0-28");
    assert.equal(manifest.peerDependencies["module-a"], manifest.version);
  }
});

test("prepares stable versions when registry packages are new", async (t) => {
  const root = await previewRepo(t, "6.7.0-alpha.1");
  t.mock.method(
    globalThis,
    "fetch",
    async () => new Response(null, { status: 404 })
  );
  assert.equal(await preparePreview(root, "500", "latest"), "6.7.0-1");
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
  for (const distTag of ["next", "latest"]) {
    await assert.rejects(
      preparePreview(root, "100", distTag),
      /Registry returned HTTP 503/
    );
  }
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

test("preserves internal wildcards through preview and numeric releases", async (t) => {
  const root = await previewRepo(t, "6.7.0-22");
  const sections = ["dependencies", "peerDependencies", "optionalDependencies", "devDependencies"];
  for (const name of ["module-a", "module-b"]) {
    const path = join(root, "packages", name, "package.json");
    const manifest = JSON.parse(await readFile(path, "utf8"));
    for (const section of sections) {
      manifest[section] = { "module-a": "*", external: "1.2.3" };
    }
    await writeFile(path, JSON.stringify(manifest));
  }
  const example = {
    name: "example",
    private: true,
    dependencies: { "module-a": "workspace:*", external: "1.2.3" },
  };
  await mkdir(join(root, "packages", "example"));
  const examplePath = join(root, "packages", "example", "package.json");
  await writeFile(examplePath, JSON.stringify(example));
  t.mock.method(globalThis, "fetch", async () =>
    new Response(JSON.stringify({ "dist-tags": { next: "6.7.0-alpha.444" } }))
  );
  const preview = await preparePreview(root, "445");
  assert.equal(preview, "6.7.0-alpha.445");
  for (const version of [preview, "6.7.0-24", "6.7.1-1"]) {
    if (version !== preview) await setWorkspaceVersions(root, version);
    for (const name of ["module-a", "module-b"]) {
      const manifest = JSON.parse(
        await readFile(join(root, "packages", name, "package.json"), "utf8")
      );
      assert.equal(manifest.version, version);
      for (const section of sections) {
        assert.deepEqual(manifest[section], { "module-a": "*", external: "1.2.3" });
      }
    }
    assert.deepEqual(JSON.parse(await readFile(examplePath, "utf8")), example);
  }
});


test("invalid recovery inputs, source conflicts and newer tags fail before manifest changes", async (t) => {
  const sourceCommit = "a".repeat(40);
  for (const [recoveryVersion, commit, publishedSource, latest, error] of [
    [
      "6.7.0-alpha.27",
      sourceCommit,
      sourceCommit,
      "6.7.0-26",
      /numeric revisions require latest/,
    ],
    ["6.7.1-1", sourceCommit, sourceCommit, "6.7.0-26", /source App baseline/],
    ["6.7.0-27", "", sourceCommit, "6.7.0-26", /full source commit/],
    [
      "6.7.0-27",
      sourceCommit,
      "b".repeat(40),
      "6.7.0-27",
      /Recovery source does not match/,
    ],
    ["6.7.0-27", sourceCommit, sourceCommit, "6.7.0-28", /backwards/],
  ]) {
    const root = await previewRepo(t, "6.7.0-26");
    t.mock.method(
      globalThis,
      "fetch",
      async () =>
        new Response(
          JSON.stringify({
            versions: { "6.7.0-27": { gitHead: publishedSource } },
            "dist-tags": { latest },
          })
        )
    );
    await assert.rejects(
      preparePreview(root, "500", "latest", {
        recoveryVersion,
        sourceCommit: commit,
      }),
      error
    );
    for (const name of ["module-a", "module-b"]) {
      const manifest = JSON.parse(
        await readFile(join(root, "packages", name, "package.json"), "utf8")
      );
      assert.equal(manifest.version, "6.7.0-26");
      assert.equal(manifest.peerDependencies["module-a"], "6.7.0-26");
    }
  }
});
