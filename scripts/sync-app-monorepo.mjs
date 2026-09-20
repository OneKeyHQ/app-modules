import { readFile, writeFile } from "node:fs/promises";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

import { loadReleaseWorkspaces } from "./validate-npm-dist-tag.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));

function compareVersions(left, right) {
  const parse = (version) => {
    if (!/^\d+\.\d+\.\d+$/.test(version)) {
      throw new Error(`Expected an exact stable version, got ${version}`);
    }
    return version.split(".").map(Number);
  };
  const a = parse(left);
  const b = parse(right);
  for (let index = 0; index < 3; index += 1) {
    if (a[index] !== b[index]) {
      return a[index] - b[index];
    }
  }
  return 0;
}

export function updateMobileManifest(manifestText, workspaces) {
  const manifest = JSON.parse(manifestText);
  const versions = new Set(workspaces.map(({ version }) => version));
  if (workspaces.length === 0 || versions.size !== 1) {
    throw new Error("Publishable workspace versions must match");
  }
  const byName = new Map(
    workspaces.map(({ name, version }) => [name, version])
  );
  const updated = [];
  for (const [name, current] of Object.entries(manifest.dependencies ?? {})) {
    const target = byName.get(name);
    if (!target || current === target) {
      continue;
    }
    if (compareVersions(current, target) > 0) {
      throw new Error(
        `Refusing to downgrade ${name} from ${current} to ${target}`
      );
    }
    manifest.dependencies[name] = target;
    updated.push(name);
  }
  return {
    text:
      updated.length > 0
        ? `${JSON.stringify(manifest, null, 2)}\n`
        : manifestText,
    updated,
  };
}

async function main() {
  const [appMonorepoArg] = process.argv.slice(2);
  if (!appMonorepoArg) {
    throw new Error(
      "Usage: node scripts/sync-app-monorepo.mjs <app-monorepo-path>"
    );
  }
  const manifestPath = join(
    resolve(appMonorepoArg),
    "apps/mobile/package.json"
  );
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const original = await readFile(manifestPath, "utf8");
  const { text, updated } = updateMobileManifest(original, workspaces);
  if (updated.length > 0) {
    await writeFile(manifestPath, text);
  }
  console.log(`Updated ${updated.length} app-monorepo mobile dependencies`);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
