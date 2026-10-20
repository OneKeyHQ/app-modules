import assert from "node:assert/strict";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";

import { setWorkspaceVersions } from "./prepare-preview.mjs";
import { nextReleaseVersion } from "./set-release-version.mjs";
import {
  loadReleaseWorkspaces,
  validateNpmDistTag,
} from "./validate-npm-dist-tag.mjs";

test("increments only the official revision and requires an explicit release after alpha", () => {
  assert.equal(nextReleaseVersion("6.7.0-9"), "6.7.0-10");
  assert.throws(
    () => nextReleaseVersion("6.7.0-alpha.9"),
    /explicit numeric release/
  );
});

test("migrates synchronized manifests and internal pins; rejects bad input before writing", async (t) => {
  const root = await mkdtemp(join(tmpdir(), "app-modules-version-"));
  t.after(() => rm(root, { recursive: true, force: true }));
  await writeFile(
    join(root, "package.json"),
    JSON.stringify({ workspaces: ["packages/*"] })
  );
  const manifests = [
    {
      name: "module-a",
      version: "3.0.178",
      scripts: { release: "npm publish" },
      peerDependencies: { "module-b": "3.0.178", external: "^1.0.0" },
    },
    {
      name: "module-b",
      version: "3.0.178",
      scripts: { release: "npm publish" },
      dependencies: { "module-a": "workspace:*" },
    },
    { name: "example", private: true, version: "0.0.1" },
  ];
  for (const manifest of manifests) {
    await mkdir(join(root, "packages", manifest.name), { recursive: true });
    await writeFile(
      join(root, "packages", manifest.name, "package.json"),
      JSON.stringify(manifest)
    );
  }
  const path = join(root, "packages/module-a/package.json");
  const before = await readFile(path, "utf8");
  await assert.rejects(
    setWorkspaceVersions(root, "6.7.0.1"),
    /Expected <app-version>/
  );
  assert.equal(await readFile(path, "utf8"), before);
  for (const version of ["6.7.0-1", "6.7.0-alpha.12", "6.7.0-2", "6.7.1-1"]) {
    assert.equal(await setWorkspaceVersions(root, version), 2);
    validateNpmDistTag(
      version.includes("alpha") ? "next" : "latest",
      await loadReleaseWorkspaces(root)
    );
    const a = JSON.parse(await readFile(path, "utf8"));
    assert.equal(a.peerDependencies["module-b"], version);
    assert.equal(a.peerDependencies.external, "^1.0.0");
    const b = JSON.parse(
      await readFile(join(root, "packages/module-b/package.json"), "utf8")
    );
    assert.equal(b.dependencies["module-a"], "workspace:*");
    assert.deepEqual(
      JSON.parse(
        await readFile(join(root, "packages/example/package.json"), "utf8")
      ),
      manifests[2]
    );
  }
  const unchanged = await readFile(path, "utf8");
  await assert.rejects(
    setWorkspaceVersions(root, "6.7.0-99"),
    /Refusing to downgrade/
  );
  assert.equal(await readFile(path, "utf8"), unchanged);
  await writeFile(
    join(root, "packages/module-b/package.json"),
    JSON.stringify(manifests[1])
  );
  await assert.rejects(
    setWorkspaceVersions(root, "6.7.1-2"),
    /versions must match/
  );
  assert.equal(await readFile(path, "utf8"), unchanged);
});
