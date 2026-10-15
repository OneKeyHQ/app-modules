import assert from "node:assert/strict";
import { execFileSync } from "node:child_process";
import { existsSync, mkdirSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";
import { checkPe, packRelease, readBaseline, runtimeManifest } from "./release.mjs";
import { synchronize } from "./sync-app-monorepo.mjs";
import policy from "./verify-desktop-modules.cjs";

const sourceCommit = "a".repeat(40);
const baseline = readBaseline();
const name = "electron-windows-hello";

function temporary(context) {
  const directory = mkdtempSync(join(tmpdir(), "desktop-release-test-"));
  context.after(() => rmSync(directory, { recursive: true, force: true }));
  return directory;
}

function writeJson(file, value) {
  writeFileSync(file, JSON.stringify(value));
}

function pe(arch) {
  const bytes = Buffer.alloc(128);
  bytes.writeUInt16LE(0x5a4d, 0);
  bytes.writeUInt32LE(64, 0x3c);
  bytes.writeUInt32LE(0x4550, 64);
  bytes.writeUInt16LE(arch === "x64" ? 0x8664 : 0xaa64, 68);
  return bytes;
}

function packageFixture(context) {
  const root = temporary(context);
  writeJson(join(root, "release.json"), baseline);
  const moduleRoot = join(root, "win", name);
  mkdirSync(moduleRoot, { recursive: true });
  for (const file of ["LICENSE", "README.md", "index.cjs", "index.d.ts"]) {
    writeFileSync(join(moduleRoot, file), `fixture ${file}`);
  }
  writeJson(join(moduleRoot, "package.json"), {
    name,
    version: "0.1.0",
    main: "index.cjs",
    types: "index.d.ts",
    scripts: { postinstall: "must not ship" },
    devDependencies: { electron: baseline.electronVersion },
  });
  for (const arch of ["x64", "arm64"]) {
    const directory = join(root, "dist", `native-${arch}`);
    mkdirSync(directory, { recursive: true });
    writeFileSync(join(directory, `${name}.win32-${arch}-msvc.node`), pe(arch));
    writeJson(join(directory, "build-info.json"), { arch });
  }
  const release = packRelease(root, sourceCommit);
  return { root, release, file: join(root, "dist/desktop-win.json") };
}

function appFixture(context) {
  const root = temporary(context);
  const desktop = join(root, "apps/desktop");
  mkdirSync(join(desktop, "app"), { recursive: true });
  mkdirSync(join(desktop, "scripts"));
  writeJson(join(desktop, "package.json"), {
    name: "desktop",
    devDependencies: { electron: baseline.electronVersion },
    dependencies: { [name]: "0.1.0", unrelated: "1.0.0" },
    scripts: { "install-app-deps": "electron-builder install-app-deps" },
  });
  writeJson(join(desktop, "app/package.json"), {
    name: "desktop",
    dependencies: { [name]: "0.1.0", unrelated: "1.0.0" },
  });
  return { root, desktop };
}

test("desktop versions track the Electron major, not its patch", () => {
  for (const major of [36, 37, 44]) {
    const candidate = { ...baseline, version: `${major}.0.1`, electronVersion: `${major}.5.2` };
    assert.equal(policy.validateBaseline(candidate), candidate);
  }
  for (const changes of [
    { version: "37.0.1", electronVersion: "36.0.1" },
    { version: "44.0.1-alpha.1" },
    { version: "044.0.1" },
    { rustToolchain: "stable" },
    { electronVersion: "^44.5.1" },
    { modules: { win: ["unknown"], mac: [], linux: [] } },
  ])
    assert.throws(() => policy.validateBaseline({ ...baseline, ...changes }));
});

test("PE validation rejects wrong, short and out-of-bounds native artifacts", () => {
  for (const arch of ["x64", "arm64"]) checkPe(pe(arch), arch);
  assert.throws(() => checkPe(pe("x64"), "arm64"));
  assert.throws(() => checkPe(Buffer.alloc(4), "x64"));
  const corrupt = pe("x64");
  corrupt.writeUInt32LE(0xffffffff, 0x3c);
  assert.throws(() => checkPe(corrupt, "x64"));
});

test("runtime packaging strips scripts and dev dependencies and rejects runtime dependencies", () => {
  const packed = runtimeManifest(
    { name, scripts: { install: "bad" }, devDependencies: { electron: "1" } },
    baseline.version,
  );
  assert.equal(packed.version, baseline.version);
  assert.equal(packed.scripts, undefined);
  assert.equal(packed.devDependencies, undefined);
  assert.throws(() =>
    runtimeManifest({ name, dependencies: { unexpected: "1" } }, baseline.version),
  );
});

test("a combined tarball has exactly the signed runtime inventory", (context) => {
  const fixture = packageFixture(context);
  const entry = fixture.release.modules[name];
  const archive = join(fixture.root, "dist", entry.asset);
  assert.equal(policy.sha256(readFileSync(archive)), entry.sha256);
  const extracted = temporary(context);
  execFileSync("tar", ["-xzf", archive, "-C", extracted]);
  policy.verifyRuntimeFiles(join(extracted, "package"), entry.files);
  const manifest = JSON.parse(readFileSync(join(extracted, "package/package.json")));
  assert.equal(manifest.name, name);
  assert.equal(manifest.version, baseline.version);
  assert.equal(manifest.scripts, undefined);
});

test("runtime verification rejects tampering and extra or missing files without executing code", (context) => {
  const directory = temporary(context);
  writeFileSync(join(directory, "index.cjs"), "throw new Error('must not execute')");
  const inventory = { "index.cjs": policy.sha256(readFileSync(join(directory, "index.cjs"))) };
  policy.verifyRuntimeFiles(directory, inventory);
  writeFileSync(join(directory, "extra.node"), "unexpected");
  assert.throws(() => policy.verifyRuntimeFiles(directory, inventory), /Unexpected/);
  rmSync(join(directory, "extra.node"));
  writeFileSync(join(directory, "index.cjs"), "replaced");
  assert.throws(() => policy.verifyRuntimeFiles(directory, inventory), /digest mismatch/);
  rmSync(join(directory, "index.cjs"));
  assert.throws(() => policy.verifyRuntimeFiles(directory, inventory), /missing/);
});

test("manifest approval pins bytes, source and version; inventory cannot traverse directories", (context) => {
  const fixture = packageFixture(context);
  const pin = {
    schemaVersion: 1,
    version: baseline.version,
    sourceCommit,
    manifestSha256: policy.sha256(readFileSync(fixture.file)),
  };
  assert.deepEqual(policy.readApprovedRelease(fixture.file, pin), fixture.release);
  assert.throws(
    () => policy.readApprovedRelease(fixture.file, { ...pin, manifestSha256: "b".repeat(64) }),
    /digest mismatch/,
  );
  assert.throws(
    () => policy.readApprovedRelease(fixture.file, { ...pin, sourceCommit: "b".repeat(40) }),
    /source\/version/,
  );
  const malicious = structuredClone(fixture.release);
  malicious.modules[name].files["../evil.node"] = "a".repeat(64);
  assert.throws(() => policy.validateRelease(malicious), /inventory/);
  assert.throws(
    () => policy.validateRelease({ ...fixture.release, repository: "attacker/repo" }),
    /identity/,
  );
});

test("attestation verification constrains repository, workflow, commit, main and hosted runners", () => {
  assert.deepEqual(policy.attestationArgs("artifact.tgz", sourceCommit), [
    "attestation",
    "verify",
    "artifact.tgz",
    "--repo",
    "OneKeyHQ/app-modules",
    "--signer-workflow",
    "OneKeyHQ/app-modules/.github/workflows/desktop-win-release.yml",
    "--source-digest",
    sourceCommit,
    "--signer-digest",
    sourceCommit,
    "--source-ref",
    "refs/heads/main",
    "--deny-self-hosted-runners",
  ]);
  assert.throws(() => policy.attestationArgs("artifact", "main"));
  assert.deepEqual(policy.attestationArgs("artifact.tgz", sourceCommit, "proof.json").slice(-2), [
    "--bundle",
    "proof.json",
  ]);
});

test("sync updates only desktop dependencies, records approval and is idempotent", (context) => {
  const fixture = packageFixture(context);
  const app = appFixture(context);
  const pin = synchronize(app.root, fixture.file);
  assert.equal(pin.manifestSha256, policy.sha256(readFileSync(fixture.file)));
  const first = JSON.parse(readFileSync(join(app.desktop, "package.json")));
  assert.equal(first.devDependencies.electron, baseline.electronVersion);
  assert.equal(first.dependencies.unrelated, "1.0.0");
  assert.equal(
    first.dependencies[name],
    `https://github.com/OneKeyHQ/app-modules/releases/download/desktop-win-v${baseline.version}/${name}-${baseline.version}.tgz`,
  );
  assert.ok(first.scripts["install-app-deps"].endsWith("node scripts/verify-desktop-modules.cjs"));
  assert.deepEqual(synchronize(app.root, fixture.file), pin);
  assert.deepEqual(JSON.parse(readFileSync(join(app.desktop, "package.json"))), first);
  const runtime = JSON.parse(readFileSync(join(app.desktop, "app/package.json")));
  assert.equal(runtime.dependencies[name], first.dependencies[name]);
});

test("sync refuses stale Electron, missing integration, downgrade and same-version replacement", (context) => {
  const fixture = packageFixture(context);
  const app = appFixture(context);
  const manifestFile = join(app.desktop, "package.json");
  const original = JSON.parse(readFileSync(manifestFile));
  writeJson(manifestFile, { ...original, devDependencies: { electron: "36.0.1" } });
  assert.throws(() => synchronize(app.root, fixture.file), /Electron/);
  assert.equal(existsSync(join(app.desktop, "desktop-modules.json")), false);
  writeJson(manifestFile, { ...original, dependencies: {} });
  assert.throws(() => synchronize(app.root, fixture.file), /Merge/);
  writeJson(manifestFile, original);
  const pin = synchronize(app.root, fixture.file);
  writeJson(join(app.desktop, "desktop-modules.json"), { ...pin, version: "99.0.1" });
  assert.throws(() => synchronize(app.root, fixture.file), /downgrade/);
  writeJson(join(app.desktop, "desktop-modules.json"), { ...pin, sourceCommit: "b".repeat(40) });
  assert.throws(() => synchronize(app.root, fixture.file), /replacement/);
});
