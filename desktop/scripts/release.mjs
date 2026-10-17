import { execFileSync } from "node:child_process";
import { createHash } from "node:crypto";
import { appendFileSync, copyFileSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { verifyPublished } from "../../scripts/verify-published.mjs";
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
  if (source.name !== "@onekeyfe/electron-windows-hello") {
    throw new Error("Unsupported desktop npm package name");
  }
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
      directory: `desktop/win/${source.name.split("/")[1]}`,
    },
  };
}

export function prepareRelease(root, sourceCommit, sourceRef) {
  const baseline = readBaseline(root);
  if (!/^[a-f0-9]{40}$/.test(sourceCommit)) throw new Error("Expected full source commit");
  policy.validateSourceRef(sourceRef);
  const dist = join(root, "dist");
  const release = {
    schemaVersion: 2,
    repository: policy.repository,
    workflow: policy.workflow,
    sourceCommit,
    sourceRef,
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
    if (manifest.name !== `@onekeyfe/${name}`) throw new Error("Module package name mismatch");
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
    const asset = `onekeyfe-${name}-${baseline.version}.tgz`;
    release.modules[name] = {
      packageName: manifest.name,
      asset,
      files,
    };
  }
  policy.validateRelease(release);
  writeFileSync(join(dist, "desktop-win.json"), `${JSON.stringify(release, null, 2)}\n`);
  return release;
}

export function packRelease(root, sourceCommit, sourceRef) {
  const dist = join(root, "dist");
  const metadata = Object.fromEntries(
    policy.proofFiles.map((file) => [file, readFileSync(join(dist, file))]),
  );
  const release = policy.validateRelease(JSON.parse(readFileSync(join(dist, "desktop-win.json"))));
  if (
    release.sourceCommit !== sourceCommit ||
    release.sourceRef !== sourceRef ||
    JSON.stringify(release.baseline) !== JSON.stringify(readBaseline(root))
  ) {
    throw new Error("Release differs from the expected source and baseline");
  }
  for (const [name, entry] of Object.entries(release.modules)) {
    const stage = join(dist, "staging", name);
    const destination = join(stage, "package");
    policy.verifyRuntimeFiles(destination, entry.files);
    for (const [file, bytes] of Object.entries(metadata))
      writeFileSync(join(destination, file), bytes);
    policy.verifyRuntimeFiles(destination, entry.files, policy.proofFiles);
    execFileSync("tar", ["-czf", join(dist, entry.asset), "-C", stage, "package"]);
  }
  return release;
}

export async function checkUnpublished(baseline, request = fetch) {
  for (const name of baseline.modules.win) {
    const packageName = `@onekeyfe/${name}`;
    const response = await request(
      `https://registry.npmjs.org/${encodeURIComponent(packageName)}/${baseline.version}`,
      {
        signal: AbortSignal.timeout(30_000),
      },
    );
    if (response.status === 200)
      throw new Error(
        `Published npm version cannot be rebuilt: ${packageName}@${baseline.version}`,
      );
    if (response.status !== 404)
      throw new Error(`Cannot check npm version: HTTP ${response.status}`);
  }
}

export function verifyRegistryPackage(metadata, archive, name, version) {
  const integrity = `sha512-${createHash("sha512").update(archive).digest("base64")}`;
  if (
    metadata.name !== name ||
    metadata.version !== version ||
    metadata.repository?.url !== `git+https://github.com/${policy.repository}.git` ||
    metadata.dist?.integrity !== integrity ||
    !metadata.dist?.attestations?.url?.startsWith(
      "https://registry.npmjs.org/-/npm/v1/attestations/",
    )
  ) {
    throw new Error(
      "Published npm package does not match the archive, repository or provenance policy",
    );
  }
}

async function main() {
  const baseline = readBaseline();
  const sourceRef =
    process.env.GITHUB_EVENT_NAME === "pull_request"
      ? `refs/heads/${process.env.GITHUB_HEAD_REF}`
      : process.env.GITHUB_REF;
  if (process.argv[2] === "prepare") {
    prepareRelease(desktopRoot, process.env.GITHUB_SHA, sourceRef);
  } else if (process.argv[2] === "pack") {
    packRelease(desktopRoot, process.env.GITHUB_SHA, sourceRef);
  } else if (process.argv[2] === "validate-publish") {
    if (
      process.env.GITHUB_REPOSITORY !== policy.repository ||
      process.env.GITHUB_EVENT_NAME !== "workflow_dispatch"
    ) {
      throw new Error("Desktop publication requires a manual run in the source repository");
    }
    const channel = policy.releaseChannel(baseline.version, sourceRef);
    if (process.env.GITHUB_OUTPUT)
      appendFileSync(process.env.GITHUB_OUTPUT, `dist_tag=${channel}\n`);
    console.log(`Validated ${baseline.version} from ${sourceRef} for npm ${channel}`);
  } else if (process.argv[2] === "check-version") {
    await checkUnpublished(baseline);
  } else if (process.argv[2] === "verify-npm") {
    const packages = baseline.modules.win.map((name) => ({
      name: `@onekeyfe/${name}`,
      version: baseline.version,
    }));
    const missing = await verifyPublished(
      packages,
      policy.releaseChannel(baseline.version, sourceRef),
    );
    if (missing.length > 0)
      throw new Error(`npm publication not available: ${JSON.stringify(missing)}`);
    for (const { name, version } of packages) {
      const response = await fetch(
        `https://registry.npmjs.org/${encodeURIComponent(name)}/${version}?write=true`,
        {
          signal: AbortSignal.timeout(30_000),
        },
      );
      if (!response.ok)
        throw new Error(`Cannot verify published npm metadata: HTTP ${response.status}`);
      verifyRegistryPackage(
        await response.json(),
        readFileSync(
          join(desktopRoot, "dist", `${name.replace("@", "").replace("/", "-")}-${version}.tgz`),
        ),
        name,
        version,
      );
    }
  } else if (process.argv[2] === "verify-native") {
    for (const arch of ["x64", "arm64"]) {
      const directory = join(desktopRoot, "dist", `native-${arch}`);
      for (const name of baseline.modules.win) {
        policy.verifyAttestation(
          join(directory, `${name}.win32-${arch}-msvc.node`),
          process.env.GITHUB_SHA,
          sourceRef,
        );
      }
      policy.verifyAttestation(
        join(directory, "build-info.json"),
        process.env.GITHUB_SHA,
        sourceRef,
      );
    }
  } else if (process.argv[2] === "verify-release") {
    for (const name of baseline.modules.win) {
      const asset = `onekeyfe-${name}-${baseline.version}.tgz`;
      const file = join(desktopRoot, "dist", asset);
      policy.verifyAttestation(file, process.env.GITHUB_SHA, sourceRef);
      const destination = join(desktopRoot, "dist", "verified", name);
      mkdirSync(destination, { recursive: true });
      execFileSync("tar", ["-xzf", file, "-C", destination]);
      const directory = join(destination, "package");
      const manifest = join(directory, "desktop-win.json");
      const release = policy.validateRelease(JSON.parse(readFileSync(manifest)));
      if (
        release.sourceCommit !== process.env.GITHUB_SHA ||
        release.sourceRef !== sourceRef ||
        JSON.stringify(release.baseline) !== JSON.stringify(baseline)
      ) {
        throw new Error("Release differs from the expected source and baseline");
      }
      policy.verifyRuntimeFiles(directory, release.modules[name].files, policy.proofFiles);
      policy.verifyAttestation(
        manifest,
        process.env.GITHUB_SHA,
        sourceRef,
        join(directory, "desktop-win.sigstore.json"),
      );
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
        `version=${baseline.version}\nelectron=${baseline.electronVersion}\nrust=${baseline.rustToolchain}\n`,
      );
    }
    console.log(JSON.stringify(baseline));
  } else {
    throw new Error(
      "Usage: node desktop/scripts/release.mjs <validate|validate-publish|check-version|prepare|pack|verify-native|verify-release|verify-npm>",
    );
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) await main();
