import assert from "node:assert/strict";
import { createHash } from "node:crypto";
import { mkdtemp, mkdir, readFile, writeFile, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";
import { installKotlin, kotlinCompilerPin } from "./install-kotlin.mjs";

const fixture = Buffer.from("synthetic compiler archive");
const pin = { ...kotlinCompilerPin, sha256: createHash("sha256").update(fixture).digest("hex") };

async function scenario(t, { cached, failures = 0, download = fixture, version = pin.version } = {}) {
  const root = await mkdtemp(join(tmpdir(), "kotlin-installer-test-"));
  t.after(() => rm(root, { recursive: true, force: true }));
  const cacheDirectory = join(root, "cache");
  const installDirectory = join(root, "install");
  await mkdir(cacheDirectory);
  const archive = join(cacheDirectory, `kotlin-compiler-${pin.version}.zip`);
  if (cached) await writeFile(archive, cached);
  const calls = [];
  const sleeps = [];
  const run = async (command, args) => {
    calls.push({ command, args });
    if (command === "curl") {
      assert.equal(args[0], "--disable");
      assert.equal(args[args.indexOf("--proto") + 1], "=https");
      assert.equal(args[args.indexOf("--proto-redir") + 1], "=https");
      if (calls.filter((call) => call.command === "curl").length <= failures) throw new Error("PRIVATE gateway detail");
      await writeFile(args[args.indexOf("--output") + 1], download);
    } else if (command === "unzip") {
      assert.deepEqual(await readFile(archive), fixture, "Only a verified archive reaches extraction");
    } else {
      assert.equal(command, join(installDirectory, "kotlinc/bin/kotlinc"));
      assert.deepEqual(args, ["-version"]);
      return { stdout: "", stderr: `info: kotlinc-jvm ${version} (JRE synthetic)` };
    }
    return { stdout: "", stderr: "" };
  };
  const execute = () => installKotlin({ cacheDirectory, installDirectory, pin }, { run, sleep: async (ms) => sleeps.push(ms) });
  return { execute, calls, sleeps, archive };
}

test("verified cache hit still extracts and checks compiler version", async (t) => {
  const s = await scenario(t, { cached: fixture });
  assert.equal((await s.execute()).cacheHit, true);
  assert.deepEqual(s.calls.map(({ command }) => command === "unzip" ? command : command.endsWith("kotlinc") ? "version" : command), ["unzip", "version"]);
});

test("corrupt cache is replaced by a verified official download", async (t) => {
  const s = await scenario(t, { cached: Buffer.from("corrupt cache") });
  assert.equal((await s.execute()).cacheHit, false);
  assert.equal(s.calls.filter(({ command }) => command === "curl").length, 1);
  assert.deepEqual(await readFile(s.archive), fixture);
});

test("transient download failures retry finitely before verification", async (t) => {
  const s = await scenario(t, { failures: 2 });
  assert.equal((await s.execute()).cacheHit, false);
  assert.equal(s.calls.filter(({ command }) => command === "curl").length, 3);
  assert.deepEqual(s.sleeps, [1000, 2000]);
});

test("exhausted retries fail without extracting or retaining an archive", async (t) => {
  const s = await scenario(t, { failures: 99 });
  await assert.rejects(s.execute, /^Error: kotlin_download_failed$/);
  assert.equal(s.calls.length, 3);
  assert.deepEqual(s.sleeps, [1000, 2000]);
  await assert.rejects(readFile(s.archive), { code: "ENOENT" });
});

test("a downloaded hash mismatch never reaches extraction", async (t) => {
  const s = await scenario(t, { download: Buffer.from("wrong compiler") });
  await assert.rejects(s.execute, /^Error: kotlin_checksum_mismatch$/);
  assert.equal(s.calls.length, 1);
  await assert.rejects(readFile(s.archive), { code: "ENOENT" });
});

test("compiler version mismatch fails even on a valid cache hit", async (t) => {
  const s = await scenario(t, { cached: fixture, version: "2.3.20" });
  await assert.rejects(s.execute, /^Error: kotlin_version_mismatch$/);
  assert.equal(s.calls.filter(({ command }) => command === "curl").length, 0);
});
