import assert from "node:assert/strict";
import { execFileSync } from "node:child_process";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { delimiter, join } from "node:path";
import test from "node:test";
import { fileURLToPath } from "node:url";

import { selectRetags, selectUnpublished } from "./publish-missing.mjs";
import {
  loadReleaseWorkspaces,
  parseReleaseVersion,
} from "./validate-npm-dist-tag.mjs";

const workspaces = [
  { name: "@onekeyfe/module-a", version: "6.7.0-2" },
  { name: "@onekeyfe/module-b", version: "6.7.0-2" },
];

test("skips already published packages on a retry", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "6.7.0-2": {} }, "dist-tags": { latest: "6.7.0-2" } },
    ],
    ["@onekeyfe/module-b", { versions: { "6.7.0-1": {} } }],
  ]);
  assert.deepEqual(selectUnpublished(workspaces, packuments, "latest"), [
    workspaces[1],
  ]);
});

test("retags an existing version instead of trying to republish it", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { versions: { "6.7.0-2": {} }, "dist-tags": { latest: "6.7.0-1" } },
    ],
  ]);
  assert.deepEqual(selectUnpublished(workspaces, packuments, "latest"), [
    workspaces[1],
  ]);
  assert.deepEqual(selectRetags(workspaces, packuments, "latest"), [
    workspaces[0],
  ]);
});

test("never moves latest backwards when a newer release won the race", () => {
  const packuments = new Map([
    [
      "@onekeyfe/module-a",
      { "dist-tags": { latest: "6.7.0-3" }, versions: {} },
    ],
  ]);
  assert.throws(
    () => selectUnpublished(workspaces, packuments, "latest"),
    /Refusing to move .* latest backwards/
  );
});

test("protects numeric counters, alpha counters and App baselines from rollback", () => {
  for (const [tag, published, target] of [
    ["latest", "6.7.0-10", "6.7.0-2"],
    ["latest", "6.7.1-1", "6.7.0-99"],
    ["next", "6.7.0-alpha.10", "6.7.0-alpha.2"],
  ]) {
    const packages = [{ name: "module-a", version: target }];
    const documents = new Map([
      ["module-a", { "dist-tags": { [tag]: published } }],
    ]);
    assert.throws(
      () => selectUnpublished(packages, documents, tag),
      /backwards/
    );
  }
});

for (const missing of [
  ["react-native-image", "react-native-pager-view", "react-native-native-list"],
  ["react-native-pager-view", "react-native-native-list"],
  ["react-native-pager-view"],
  ["react-native-native-list"],
  [],
]) {
  test(`orders image declarations before missing peer consumers: ${missing.join(", ") || "none"}`, async (t) => {
    const root = await mkdtemp(join(tmpdir(), "app-modules-publish-order-"));
    t.after(() => rm(root, { recursive: true, force: true }));
    const bin = join(root, "bin");
    await mkdir(bin);
    const log = join(root, "calls.jsonl");
    await writeFile(log, "");
    await writeFile(
      join(bin, "yarn"),
      `#!${process.execPath}\nrequire("node:fs").appendFileSync(process.env.PUBLISH_TEST_LOG, JSON.stringify(process.argv.slice(2)) + "\\n");\n`,
      { mode: 0o755 }
    );
    const repo = fileURLToPath(new URL("../", import.meta.url));
    const [workspace] = await loadReleaseWorkspaces(repo);
    const version = workspace.version;
    const distTag = parseReleaseVersion(version).channel;
    const names = missing.map((name) => `@onekeyfe/${name}`);
    const preload = join(root, "registry.mjs");
    await writeFile(
      preload,
      `const missing = new Set(${JSON.stringify(names)});\n` +
        `globalThis.fetch = async (url) => {\n` +
        `const name = decodeURIComponent(new URL(url).pathname.slice(1));\n` +
        `return new Response(JSON.stringify({ versions: missing.has(name) ? {} : { [${JSON.stringify(version)}]: {} }, "dist-tags": missing.has(name) ? {} : { [${JSON.stringify(distTag)}]: ${JSON.stringify(version)} } }));\n};\n`
    );
    execFileSync(
      process.execPath,
      ["--import", preload, fileURLToPath(new URL("./publish-missing.mjs", import.meta.url)), distTag],
      { env: { ...process.env, PATH: `${bin}${delimiter}${process.env.PATH}`, PUBLISH_TEST_LOG: log } }
    );
    const calls = (await readFile(log, "utf8")).trim().split("\n").filter(Boolean).map((line) => JSON.parse(line));
    if (missing.includes("react-native-image")) {
      const first = calls.shift();
      assert.equal(first[0], "workspaces");
      assert.ok(first.includes("@onekeyfe/react-native-image"));
      assert.ok(!first.includes("@onekeyfe/react-native-pager-view"));
      assert.ok(!first.includes("@onekeyfe/react-native-native-list"));
    }
    const consumers = ["react-native-pager-view", "react-native-native-list"].filter((name) => missing.includes(name));
    const expected = consumers.length
      ? [["workspace", "@onekeyfe/react-native-image", "prepare"], ...consumers.map((name) => ["workspace", `@onekeyfe/${name}`, "release", "--tag", distTag])]
      : [];
    assert.deepEqual(calls, expected);
  });
}
