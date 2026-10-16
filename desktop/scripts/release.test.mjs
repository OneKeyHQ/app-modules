import assert from "node:assert/strict";
import { execFileSync } from "node:child_process";
import { createHash } from "node:crypto";
import { mkdirSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import test from "node:test";
import {
  checkPe,
  checkUnpublished,
  packRelease,
  prepareRelease,
  readBaseline,
  runtimeManifest,
  verifyRegistryPackage,
} from "./release.mjs";
import policy from "./verify-desktop-modules.cjs";

const sourceCommit = "a".repeat(40);
const sourceRef = "refs/heads/codex/desktop-win-npm-release";
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
  const release = prepareRelease(root, sourceCommit, sourceRef);
  return { root, release, file: join(root, "dist/desktop-win.json") };
}

test("desktop versions track the Electron major, not its patch", () => {
  for (const major of [36, 37, 44]) {
    const candidate = { ...baseline, version: `${major}.0.1`, electronVersion: `${major}.5.2` };
    assert.equal(policy.validateBaseline(candidate), candidate);
  }
  for (const changes of [
    { version: "37.0.1", electronVersion: "36.0.1" },
    { version: "44.0.2-alpha.0" },
    { version: "44.0.2-alpha.01" },
    { version: "44.0.2-beta.1" },
    { version: "044.0.1" },
    { rustToolchain: "stable" },
    { electronVersion: "^44.5.1" },
    { modules: { win: ["unknown"], mac: [], linux: [] } },
  ])
    assert.throws(() => policy.validateBaseline({ ...baseline, ...changes }));
});

test("only alpha previews can publish from branches and never use latest", () => {
  assert.equal(policy.validateBaseline(baseline), baseline);
  assert.equal(policy.releaseChannel("44.0.2-alpha.1", sourceRef), "next");
  assert.equal(policy.releaseChannel("44.0.2-alpha.1", "refs/heads/main"), "next");
  assert.equal(policy.releaseChannel("44.0.2", "refs/heads/main"), "latest");
  assert.throws(() => policy.releaseChannel("44.0.2", sourceRef), /require main/);
  for (const invalid of [
    undefined,
    "main",
    "refs/tags/v44.0.2",
    "refs/pull/156/merge",
    "refs/heads/a..b",
    "refs/heads/a//b",
    "refs/heads/a.lock",
    "refs/heads/a/.hidden",
    "refs/heads/a/",
  ]) {
    assert.throws(() => policy.validateSourceRef(invalid), /source branch ref/);
    assert.throws(
      () => policy.attestationArgs("artifact", sourceCommit, invalid),
      /source branch ref/,
    );
  }
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

test("npm version checks reject an existing version, registry errors and network failures", async () => {
  await checkUnpublished(baseline, async (url) => {
    assert.equal(url, `https://registry.npmjs.org/${name}/${baseline.version}`);
    return { status: 404 };
  });
  await assert.rejects(
    checkUnpublished(baseline, async () => ({ status: 200 })),
    /cannot be rebuilt/,
  );
  await assert.rejects(
    checkUnpublished(baseline, async () => ({ status: 503 })),
    /HTTP 503/,
  );
  await assert.rejects(
    checkUnpublished(baseline, async () => {
      throw new Error("offline");
    }),
    /offline/,
  );
});

test("a combined npm tarball carries exactly the runtime inventory and portable proof", (context) => {
  const fixture = packageFixture(context);
  const entry = fixture.release.modules[name];
  assert.throws(() => packRelease(fixture.root, sourceCommit, sourceRef), /ENOENT/);
  const proof = join(fixture.root, "dist/desktop-win.sigstore.json");
  writeFileSync(proof, "fixture proof; not a valid attestation");
  packRelease(fixture.root, sourceCommit, sourceRef);
  const archive = join(fixture.root, "dist", entry.asset);
  const extracted = temporary(context);
  execFileSync("tar", ["-xzf", archive, "-C", extracted]);
  policy.verifyRuntimeFiles(join(extracted, "package"), entry.files, policy.proofFiles);
  assert.deepEqual(
    readFileSync(join(extracted, "package/desktop-win.json")),
    readFileSync(fixture.file),
  );
  assert.deepEqual(
    readFileSync(join(extracted, "package/desktop-win.sigstore.json")),
    readFileSync(proof),
  );
  const manifest = JSON.parse(readFileSync(join(extracted, "package/package.json")));
  assert.equal(manifest.name, name);
  assert.equal(manifest.version, baseline.version);
  assert.equal(manifest.scripts, undefined);
});

test("packaging rejects a changed source, baseline or staged file after inventory preparation", (context) => {
  const fixture = packageFixture(context);
  writeFileSync(join(fixture.root, "dist/desktop-win.sigstore.json"), "fixture proof");
  assert.throws(() => packRelease(fixture.root, "b".repeat(40), sourceRef), /expected source/);
  assert.throws(
    () => packRelease(fixture.root, sourceCommit, "refs/heads/other"),
    /expected source/,
  );
  writeJson(join(fixture.root, "release.json"), { ...baseline, version: "44.0.3" });
  assert.throws(() => packRelease(fixture.root, sourceCommit, sourceRef), /baseline/);
  writeJson(join(fixture.root, "release.json"), baseline);
  writeFileSync(join(fixture.root, "dist/staging", name, "package/index.cjs"), "replaced");
  assert.throws(() => packRelease(fixture.root, sourceCommit, sourceRef), /digest mismatch/);
});

test("registry verification requires the exact npm archive, package identity and provenance metadata", () => {
  const archive = Buffer.from("approved npm archive");
  const metadata = {
    ...runtimeManifest({ name }, baseline.version),
    dist: {
      integrity: `sha512-${createHash("sha512").update(archive).digest("base64")}`,
      attestations: { url: "https://registry.npmjs.org/-/npm/v1/attestations/fixture" },
    },
  };
  verifyRegistryPackage(metadata, archive, name, baseline.version);
  assert.throws(
    () => verifyRegistryPackage(metadata, Buffer.from("replaced"), name, baseline.version),
    /does not match/,
  );
  for (const changes of [
    { version: "0.1.0" },
    { name: "unexpected" },
    { repository: { url: "git+https://github.com/attacker/repo.git" } },
    { dist: { integrity: metadata.dist.integrity } },
    { dist: { ...metadata.dist, attestations: { url: "https://attacker/attestations" } } },
  ])
    assert.throws(
      () => verifyRegistryPackage({ ...metadata, ...changes }, archive, name, baseline.version),
      /does not match/,
    );
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
    sourceRef,
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
  assert.throws(
    () => policy.readApprovedRelease(fixture.file, { ...pin, sourceRef: "refs/heads/other" }),
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

test("attestation verification constrains repository, workflow, exact commit, branch and hosted runners", () => {
  assert.deepEqual(policy.attestationArgs("artifact.tgz", sourceCommit, sourceRef), [
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
    sourceRef,
    "--deny-self-hosted-runners",
  ]);
  assert.throws(() => policy.attestationArgs("artifact", "main", sourceRef));
  assert.deepEqual(
    policy.attestationArgs("artifact.tgz", sourceCommit, sourceRef, "proof.json").slice(-2),
    ["--bundle", "proof.json"],
  );
});
