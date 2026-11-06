import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";

import { setWorkspaceVersions } from "./prepare-preview.mjs";
import {
  nextReleaseVersion,
  setReleaseVersion,
} from "./set-release-version.mjs";
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

async function releaseFixture(t) {
  const root = await mkdtemp(join(tmpdir(), "app-modules-release-"));
  t.after(() => rm(root, { recursive: true, force: true }));
  await mkdir(join(root, "packages/module-a"), { recursive: true });
  await mkdir(join(root, "packages/module-b"), { recursive: true });
  await mkdir(join(root, "example/react-native/ios"), { recursive: true });
  await writeFile(
    join(root, "package.json"),
    JSON.stringify({ workspaces: ["packages/*"] })
  );
  const manifestPath = join(root, "packages/module-a/package.json");
  await writeFile(
    manifestPath,
    JSON.stringify({
      name: "module-a",
      version: "6.7.0-26",
      scripts: { release: "npm publish" },
    })
  );
  await writeFile(join(root, "example/react-native/.ruby-version"), "3.4.4\n");
  await writeFile(join(root, "packages/module-a/nitro.json"), "{}");
  await writeFile(
    join(root, "packages/module-b/package.json"),
    JSON.stringify({
      name: "module-b",
      version: "6.7.0-26",
      scripts: { release: "npm publish" },
    })
  );
  return { root, manifestPath };
}

test("release preparation updates versions before syncing Yarn, bindings and CocoaPods", async (t) => {
  const { root, manifestPath } = await releaseFixture(t);
  const calls = [];
  const result = await setReleaseVersion(root, "--next", {
    platform: "darwin",
    run(command, args, options) {
      const version = JSON.parse(readFileSync(manifestPath, "utf8")).version;
      assert.equal(version, calls.length < 4 ? "6.7.0-26" : "6.7.0-27");
      calls.push({ command, args, options });
    },
  });
  assert.deepEqual(result, { count: 2, version: "6.7.0-27" });
  assert.deepEqual(
    calls.map(({ command, args }) => [command, ...args]).slice(1),
    [
      ["xcodebuild", "-version"],
      ["bundle", "check"],
      ["bundle", "exec", "pod", "--version"],
      ["yarn", "install"],
      ["yarn", "--cwd", join(root, "packages/module-a"), "nitrogen"],
      ["bundle", "exec", "pod", "install"],
    ]
  );
  assert.equal(calls[0].args.at(-1), "3.4.4");
  assert.equal(calls[4].options.cwd, root);
  assert.equal(calls[4].options.env.YARN_ENABLE_IMMUTABLE_INSTALLS, "false");
  assert.equal(calls[5].options.cwd, root);
  assert.equal(calls[6].options.cwd, join(root, "example/react-native/ios"));
  assert.equal(
    calls[6].options.env.BUNDLE_GEMFILE,
    join(root, "example/react-native/Gemfile")
  );
});

test("unsupported platforms and missing native tools fail before changing versions", async (t) => {
  const { root, manifestPath } = await releaseFixture(t);
  const before = await readFile(manifestPath, "utf8");
  await assert.rejects(
    setReleaseVersion(root, "6.7.0-27", {
      platform: "linux",
      run() {
        assert.fail("must not run commands on unsupported platforms");
      },
    }),
    /requires macOS/
  );
  for (const failureIndex of [0, 1, 2, 3]) {
    let calls = 0;
    await assert.rejects(
      setReleaseVersion(root, "6.7.0-27", {
        platform: "darwin",
        run() {
          if (calls++ === failureIndex)
            throw new Error("missing native prerequisite");
        },
      }),
      /missing native prerequisite/
    );
    assert.equal(await readFile(manifestPath, "utf8"), before);
    assert.equal(calls, failureIndex + 1);
  }
});

test("sync failures stop preparation and allow retrying the same version", async (t) => {
  const { root, manifestPath } = await releaseFixture(t);
  for (const failureIndex of [4, 5, 6]) {
    let calls = 0;
    await assert.rejects(
      setReleaseVersion(root, "6.7.0-27", {
        platform: "darwin",
        run() {
          if (calls++ === failureIndex) throw new Error("sync failed");
        },
      }),
      /Version preparation incomplete: sync failed\n.*yarn version:set 6\.7\.0-27/
    );
    assert.equal(calls, failureIndex + 1);
    assert.equal(
      JSON.parse(await readFile(manifestPath, "utf8")).version,
      "6.7.0-27"
    );
  }
  const result = await setReleaseVersion(root, "6.7.0-27", {
    platform: "darwin",
    run() {},
  });
  assert.deepEqual(result, { count: 2, version: "6.7.0-27" });
});
