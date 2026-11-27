import { execFileSync } from "node:child_process";
import { createHash } from "node:crypto";
import { copyFileSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

import { verifyPublished } from "../../scripts/verify-published.mjs";
import { checkPe, verifyRegistryPackage } from "./release.mjs";
import policy from "./verify-desktop-modules.cjs";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const packageName = "@onekeyfe/electron-qr-scanner";
const version = "44.0.2";
const repository = "OneKeyHQ/app-modules";

function readLockedElectronVersion(platform) {
  const lockfile = JSON.parse(
    readFileSync(join(root, platform, "electron-qr-scanner", "package-lock.json"), "utf8"),
  );
  const pinned = lockfile.packages?.["node_modules/electron"]?.version;
  if (typeof pinned !== "string" || !/^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$/.test(pinned)) {
    throw new Error(`Invalid locked Electron version for ${platform}/electron-qr-scanner`);
  }
  return pinned;
}

const macElectron = readLockedElectronVersion("mac");
const winElectron = readLockedElectronVersion("win");
if (macElectron !== winElectron) {
  throw new Error(`macOS (${macElectron}) and Windows (${winElectron}) Electron lockfile pins disagree`);
}
if (macElectron.split(".")[0] !== version.split(".")[0]) {
  throw new Error(`Locked Electron (${macElectron}) major does not match package version (${version})`);
}
const electron = macElectron;
const workflow = `${repository}/.github/workflows/desktop-qr-scanner-preview.yml`;
const inventoryFile = "desktop-qr-scanner.json";
const bundle = "desktop-qr-scanner.sigstore.json";
const platforms = {
  mac: {
    binary: (arch) => `electron-qr-scanner.darwin-${arch}.node`,
    arch: { x64: 0x01000007, arm64: 0x0100000c },
  },
  win: {
    binary: (arch) => `electron-qr-scanner.win32-${arch}-msvc.node`,
  },
};
const architectures = ["x64", "arm64"];
const dist = join(root, "dist", "qr-scanner");
const archive = join(dist, `onekeyfe-electron-qr-scanner-${version}.tgz`);
const sourceCommit = process.env.GITHUB_SHA;
const sourceRef = process.env.GITHUB_REF;
const manifest = {
  name: packageName,
  version,
  description: "Native QR scanning for Electron on macOS and Windows",
  main: "./index.cjs",
  types: "./index.d.ts",
  exports: {
    ".": { types: "./index.d.ts", default: "./index.cjs" },
    "./mac": { types: "./mac/index.d.ts", default: "./mac/index.cjs" },
    "./win": { types: "./win/index.d.ts", default: "./win/index.cjs" },
    "./package.json": "./package.json",
  },
  engines: { node: ">=20.17.0" },
  license: "MIT",
  repository: { type: "git", url: `git+https://github.com/${repository}.git` },
};
const previewSource = join(root, "qr-scanner-preview");
const previewFiles = ["index.cjs", "index.d.ts", "LICENSE-ZXING-CXX"];
const nestedFiles = ["index.cjs", "index.d.ts", "README.md"];

function checkSource() {
  if (
    process.env.GITHUB_REPOSITORY !== repository ||
    process.env.GITHUB_EVENT_NAME !== "workflow_dispatch" ||
    !/^[a-f0-9]{40}$/.test(sourceCommit ?? "")
  ) {
    throw new Error("The QR scanner release requires a manual app-modules workflow run");
  }
  return policy.releaseChannel(version, sourceRef);
}

function attest(file, proof) {
  execFileSync("gh", [
    "attestation", "verify", file, "--repo", repository,
    "--signer-workflow", workflow, "--source-digest", sourceCommit,
    "--source-ref", sourceRef, "--signer-digest", sourceCommit,
    "--deny-self-hosted-runners", ...(proof ? ["--bundle", proof] : []),
  ], { stdio: "inherit" });
}

function verifyBinary(bytes, platform, arch) {
  if (platform === "win") {
    checkPe(bytes, arch);
  } else if (
    bytes.length < 32 ||
    bytes.readUInt32LE(0) !== 0xfeedfacf ||
    bytes.readUInt32LE(4) !== platforms.mac.arch[arch]
  ) {
    throw new Error(`Invalid macOS ${arch} Mach-O binary`);
  }
}

const digest = (bytes) => createHash("sha256").update(bytes).digest("hex");
const runtimePaths = [
  ...previewFiles,
  "package.json",
  "LICENSE",
  ...Object.entries(platforms).flatMap(([platform, info]) => [
    ...nestedFiles.map((name) => `${platform}/${name}`),
    ...architectures.map((arch) => `${platform}/${info.binary(arch)}`),
  ]),
].sort();

function verifyFiles(directory, proofs) {
  const inventory = JSON.parse(readFileSync(join(directory, inventoryFile)));
  if (
    inventory.schemaVersion !== 1 || inventory.name !== packageName ||
    inventory.version !== version || inventory.electron !== electron ||
    inventory.sourceCommit !== sourceCommit || inventory.sourceRef !== sourceRef ||
    JSON.stringify(Object.keys(inventory.files).sort()) !== JSON.stringify(runtimePaths)
  ) {
    throw new Error("QR scanner inventory does not match the approved release");
  }
  for (const parent of ["", ...Object.keys(platforms)]) {
    const files = runtimePaths.filter((name) => dirname(name) === (parent || "."));
    const known = Object.fromEntries(files.map((name) => [name.split("/").at(-1), inventory.files[name]]));
    if (parent) {
      policy.verifyRuntimeFiles(join(directory, parent), known);
      continue;
    }
    for (const [name, hash] of Object.entries(known)) {
      if (!/^[a-f0-9]{64}$/.test(hash) || digest(readFileSync(join(directory, name))) !== hash) {
        throw new Error(`Runtime file digest mismatch: ${name}`);
      }
    }
    const listing = execFileSync("tar", ["-tf", archive], { encoding: "utf8" })
      .trim().split("\n").filter((name) => !name.endsWith("/")).sort();
    const expected = [...runtimePaths, inventoryFile, ...(proofs ? [bundle] : [])]
      .map((name) => `package/${name}`).sort();
    if (JSON.stringify(listing) !== JSON.stringify(expected)) {
      throw new Error("Unexpected files in QR scanner archive");
    }
  }
}

const staging = join(dist, "staging");
const directory = join(staging, "package");
if (process.argv[2] === "archive-path") {
  console.log(archive);
} else if (process.argv[2] === "dist-tag") {
  console.log(checkSource());
} else if (process.argv[2] === "prepare") {
  checkSource();
  mkdirSync(directory, { recursive: true });
  for (const name of previewFiles) {
    copyFileSync(join(previewSource, name), join(directory, name));
  }
  writeFileSync(join(directory, "package.json"), `${JSON.stringify(manifest, null, 2)}\n`);
  copyFileSync(join(root, "win/electron-qr-scanner/LICENSE"), join(directory, "LICENSE"));

  for (const [platform, info] of Object.entries(platforms)) {
    const destination = join(directory, platform);
    mkdirSync(destination, { recursive: true });
    for (const name of nestedFiles) {
      copyFileSync(join(root, platform, "electron-qr-scanner", name), join(destination, name));
    }
    for (const arch of architectures) {
      const name = info.binary(arch);
      const artifact = join(dist, `qr-native-${platform}-${arch}`, name);
      attest(artifact);
      const bytes = readFileSync(artifact);
      verifyBinary(bytes, platform, arch);
      writeFileSync(join(destination, name), bytes);
    }
  }
  const files = Object.fromEntries(runtimePaths.map((name) => [name, digest(readFileSync(join(directory, name)))]));
  writeFileSync(join(directory, inventoryFile), `${JSON.stringify({
    schemaVersion: 1, name: packageName, version, electron, sourceCommit, sourceRef, files,
  }, null, 2)}\n`);
} else if (process.argv[2] === "pack") {
  checkSource();
  copyFileSync(join(dist, bundle), join(directory, bundle));
  execFileSync("tar", ["-czf", archive, "-C", staging, "package"], {
    env: { ...process.env, COPYFILE_DISABLE: "1" },
  });
  verifyFiles(directory, true);
} else if (process.argv[2] === "verify") {
  checkSource();
  attest(archive);
  const verified = join(dist, "verified");
  mkdirSync(verified, { recursive: true });
  execFileSync("tar", ["-xzf", archive, "-C", verified]);
  const unpacked = join(verified, "package");
  verifyFiles(unpacked, true);
  attest(join(unpacked, inventoryFile), join(unpacked, bundle));
} else if (process.argv[2] === "check-version") {
  checkSource();
  const response = await fetch(`https://registry.npmjs.org/${encodeURIComponent(packageName)}/${version}`, {
    signal: AbortSignal.timeout(30_000),
  });
  if (response.status !== 404) {
    throw new Error(`QR scanner version is already published or registry lookup failed (HTTP ${response.status})`);
  }
} else if (process.argv[2] === "verify-npm") {
  const channel = checkSource();
  const missing = await verifyPublished([{ name: packageName, version }], channel);
  if (missing.length) throw new Error("The QR scanner release is not available on npm");
  const response = await fetch(`https://registry.npmjs.org/${encodeURIComponent(packageName)}/${version}?write=true`, {
    signal: AbortSignal.timeout(30_000),
  });
  if (!response.ok) throw new Error(`Cannot verify the published registry metadata (HTTP ${response.status})`);
  verifyRegistryPackage(await response.json(), readFileSync(archive), packageName, version);
} else {
  throw new Error("Expected archive-path, dist-tag, prepare, pack, verify, check-version or verify-npm");
}
