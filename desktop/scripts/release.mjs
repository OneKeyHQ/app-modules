import { execFileSync } from "node:child_process";
import { appendFileSync, copyFileSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import policy from "./verify-desktop-modules.cjs";

export const desktopRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
export const runtimeFiles = ["LICENSE", "README.md", "index.cjs", "index.d.ts"];

export function readBaseline(root = desktopRoot) {
  return policy.validateBaseline(JSON.parse(readFileSync(join(root, "release.json"))));
}

export function checkPe(bytes, arch) {
  const machine = { x64: 0x8664, arm64: 0xaa64 }[arch];
  if (!machine || bytes.length < 64 || bytes.readUInt16LE(0) !== 0x5a4d) {
    throw new Error(`Invalid ${arch} PE binary`);
  }
  const header = bytes.readUInt32LE(0x3c);
  if (
    header > bytes.length - 24 ||
    bytes.readUInt32LE(header) !== 0x4550 ||
    bytes.readUInt16LE(header + 4) !== machine
  ) {
    throw new Error(`Wrong PE architecture: ${arch}`);
  }
}

export function runtimeManifest(source, version) {
  if (Object.keys(source.dependencies ?? {}).length > 0) {
    throw new Error("Runtime dependencies need an explicit packaging contract");
  }
  return {
    name: source.name,
    version,
    description: source.description,
    main: source.main,
    types: source.types,
    exports: source.exports,
    engines: source.engines,
    license: source.license,
    repository: {
      type: "git",
      url: `git+https://github.com/${policy.repository}.git`,
      directory: `desktop/win/${source.name}`,
    },
  };
}

export function packRelease(root, sourceCommit) {
  const baseline = readBaseline(root);
  if (!/^[a-f0-9]{40}$/.test(sourceCommit)) throw new Error("Expected full source commit");
  const dist = join(root, "dist");
  const release = {
    schemaVersion: 1,
    repository: policy.repository,
    workflow: policy.workflow,
    sourceCommit,
    tag: `desktop-win-v${baseline.version}`,
    baseline,
    builders: {},
    modules: {},
  };
  for (const arch of ["x64", "arm64"]) {
    release.builders[arch] = JSON.parse(
      readFileSync(join(dist, `native-${arch}`, "build-info.json")),
    );
  }
  for (const name of baseline.modules.win) {
    const source = join(root, "win", name);
    const stage = join(dist, "staging", name);
    const destination = join(stage, "package");
    mkdirSync(destination, { recursive: true });
    const fileNames = [...runtimeFiles, "package.json"];
    for (const file of runtimeFiles) copyFileSync(join(source, file), join(destination, file));
    const manifest = JSON.parse(readFileSync(join(source, "package.json")));
    if (manifest.name !== name) throw new Error("Module package name mismatch");
    writeFileSync(
      join(destination, "package.json"),
      `${JSON.stringify(runtimeManifest(manifest, baseline.version), null, 2)}\n`,
    );
    for (const arch of ["x64", "arm64"]) {
      const binary = `${name}.win32-${arch}-msvc.node`;
      const bytes = readFileSync(join(dist, `native-${arch}`, binary));
      checkPe(bytes, arch);
      writeFileSync(join(destination, binary), bytes);
      fileNames.push(binary);
    }
    const files = Object.fromEntries(
      fileNames.sort().map((file) => [file, policy.sha256(readFileSync(join(destination, file)))]),
    );
    policy.verifyRuntimeFiles(destination, files);
    const asset = `${name}-${baseline.version}.tgz`;
    execFileSync("tar", ["-czf", join(dist, asset), "-C", stage, "package"]);
    release.modules[name] = {
      asset,
      sha256: policy.sha256(readFileSync(join(dist, asset))),
      files,
    };
  }
  policy.validateRelease(release);
  writeFileSync(join(dist, "desktop-win.json"), `${JSON.stringify(release, null, 2)}\n`);
  return release;
}

function main() {
  const baseline = readBaseline();
  if (process.argv[2] === "pack") {
    packRelease(desktopRoot, process.env.GITHUB_SHA);
  } else if (process.argv[2] === "verify-native") {
    for (const arch of ["x64", "arm64"]) {
      const directory = join(desktopRoot, "dist", `native-${arch}`);
      for (const name of baseline.modules.win) {
        policy.verifyAttestation(
          join(directory, `${name}.win32-${arch}-msvc.node`),
          process.env.GITHUB_SHA,
        );
      }
      policy.verifyAttestation(join(directory, "build-info.json"), process.env.GITHUB_SHA);
    }
  } else if (process.argv[2] === "verify-release") {
    const manifest = join(desktopRoot, "dist/desktop-win.json");
    const bundle = join(desktopRoot, "dist/desktop-win.sigstore.json");
    policy.verifyAttestation(manifest, process.env.GITHUB_SHA, bundle);
    const release = policy.validateRelease(JSON.parse(readFileSync(manifest)));
    if (
      release.sourceCommit !== process.env.GITHUB_SHA ||
      JSON.stringify(release.baseline) !== JSON.stringify(baseline)
    ) {
      throw new Error("Release differs from the expected source and baseline");
    }
    for (const entry of Object.values(release.modules)) {
      const file = join(desktopRoot, "dist", entry.asset);
      if (policy.sha256(readFileSync(file)) !== entry.sha256)
        throw new Error("Release asset digest mismatch");
      policy.verifyAttestation(file, process.env.GITHUB_SHA, bundle);
    }
  } else if (process.argv[2] === "validate") {
    const source = JSON.parse(
      readFileSync(join(desktopRoot, "win/electron-windows-hello/package.json")),
    );
    if (source.devDependencies.electron !== baseline.electronVersion) {
      throw new Error("Module Electron dependency differs from the desktop baseline");
    }
    if (process.env.GITHUB_OUTPUT) {
      appendFileSync(
        process.env.GITHUB_OUTPUT,
        `version=${baseline.version}\ntag=desktop-win-v${baseline.version}\nelectron=${baseline.electronVersion}\nrust=${baseline.rustToolchain}\n`,
      );
    }
    console.log(JSON.stringify(baseline));
  } else {
    throw new Error(
      "Usage: node desktop/scripts/release.mjs <validate|pack|verify-native|verify-release>",
    );
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) main();
