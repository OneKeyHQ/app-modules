import { execFileSync } from "node:child_process";
import {
  copyFileSync,
  existsSync,
  mkdtempSync,
  readFileSync,
  rmSync,
  writeFileSync,
} from "node:fs";
import { tmpdir } from "node:os";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { readBaseline } from "./release.mjs";
import policy from "./verify-desktop-modules.cjs";

const scriptRoot = dirname(fileURLToPath(import.meta.url));
const encode = (value) => `${JSON.stringify(value, null, 2)}\n`;

export function compareVersions(left, right) {
  const previous = left.split(".").map(Number);
  const next = right.split(".").map(Number);
  for (let index = 0; index < 3; index += 1) {
    if (previous[index] !== next[index]) return previous[index] - next[index];
  }
  return 0;
}

export function synchronize(appRoot, releaseFile) {
  const bytes = readFileSync(releaseFile);
  const release = policy.validateRelease(JSON.parse(bytes));
  if (JSON.stringify(release.baseline) !== JSON.stringify(readBaseline())) {
    throw new Error("Published baseline differs from the reviewed release configuration");
  }
  const desktopRoot = join(appRoot, "apps/desktop");
  const packageFile = join(desktopRoot, "package.json");
  const runtimeFile = join(desktopRoot, "app/package.json");
  const pinFile = join(desktopRoot, "desktop-modules.json");
  const workspace = JSON.parse(readFileSync(packageFile));
  const runtime = JSON.parse(readFileSync(runtimeFile));
  if (workspace.devDependencies?.electron !== release.baseline.electronVersion) {
    throw new Error("app-monorepo Electron does not match the tested release baseline");
  }
  const pin = {
    schemaVersion: 1,
    version: release.baseline.version,
    sourceCommit: release.sourceCommit,
    manifestSha256: policy.sha256(bytes),
  };
  if (existsSync(pinFile)) {
    const previous = policy.validatePin(JSON.parse(readFileSync(pinFile)));
    if (
      compareVersions(previous.version, pin.version) > 0 ||
      (previous.version === pin.version &&
        (previous.sourceCommit !== pin.sourceCommit ||
          previous.manifestSha256 !== pin.manifestSha256))
    ) {
      throw new Error("Refusing a desktop downgrade or replacement of an approved version");
    }
  }
  for (const [name, entry] of Object.entries(release.modules)) {
    if (!workspace.dependencies?.[name] || !runtime.dependencies?.[name]) {
      throw new Error(`Merge the Windows Hello integration before syncing ${name}`);
    }
    const url = `https://github.com/${policy.repository}/releases/download/${release.tag}/${entry.asset}`;
    workspace.dependencies[name] = url;
    runtime.dependencies[name] = url;
  }
  const verification = "node scripts/verify-desktop-modules.cjs";
  if (!workspace.scripts?.["install-app-deps"])
    throw new Error("Missing desktop install-app-deps hook");
  if (!workspace.scripts["install-app-deps"].endsWith(verification)) {
    workspace.scripts["install-app-deps"] += ` && ${verification}`;
  }
  copyFileSync(
    join(scriptRoot, "verify-desktop-modules.cjs"),
    join(desktopRoot, "scripts/verify-desktop-modules.cjs"),
  );
  writeFileSync(pinFile, encode(pin));
  writeFileSync(packageFile, encode(workspace));
  writeFileSync(runtimeFile, encode(runtime));
  return pin;
}

export function refreshRuntimeLock(appRoot) {
  const temporary = mkdtempSync(join(tmpdir(), "onekey-desktop-lock-"));
  try {
    const runtime = join(appRoot, "apps/desktop/app");
    copyFileSync(join(runtime, "package.json"), join(temporary, "package.json"));
    copyFileSync(join(runtime, "yarn.lock"), join(temporary, "yarn.lock"));
    writeFileSync(
      join(temporary, ".yarnrc.yml"),
      "nodeLinker: pnp\nenableScripts: false\nenableGlobalCache: true\n",
    );
    execFileSync(
      process.execPath,
      [join(appRoot, ".yarn/releases/yarn-4.12.0.cjs"), "install", "--mode=skip-build"],
      {
        cwd: temporary,
        stdio: "inherit",
        env: { ...process.env, YARN_ENABLE_IMMUTABLE_INSTALLS: "false" },
      },
    );
    copyFileSync(join(temporary, "yarn.lock"), join(runtime, "yarn.lock"));
  } finally {
    rmSync(temporary, { recursive: true, force: true });
  }
}

function main() {
  const [appPath, manifest, command] = process.argv.slice(2);
  if (!appPath || !manifest) {
    throw new Error(
      "Usage: node desktop/scripts/sync-app-monorepo.mjs <app-path> <verified-manifest> [runtime-lock]",
    );
  }
  const appRoot = resolve(appPath);
  if (command === "runtime-lock") {
    refreshRuntimeLock(appRoot);
  } else if (command === undefined) {
    synchronize(appRoot, resolve(manifest));
  } else {
    throw new Error("Unknown synchronization command");
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) main();
