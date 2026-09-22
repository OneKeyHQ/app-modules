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

export function updateMobileManifest(
  manifestText,
  workspaces,
  sections = ["dependencies"]
) {
  const manifest = JSON.parse(manifestText);
  const versions = new Set(workspaces.map(({ version }) => version));
  if (workspaces.length === 0 || versions.size !== 1) {
    throw new Error("Publishable workspace versions must match");
  }
  const byName = new Map(
    workspaces.map(({ name, version }) => [name, version])
  );
  const updated = [];
  for (const section of sections) {
    for (const [name, current] of Object.entries(manifest[section] ?? {})) {
      const alias = /^npm:((?:@[^/]+\/)?[^@]+)@(.+)$/.exec(current);
      const workspaceName = alias ? alias[1] : name;
      const currentVersion = alias ? alias[2] : current;
      const target = byName.get(workspaceName);
      if (!target || currentVersion === target) {
        continue;
      }
      if (compareVersions(currentVersion, target) > 0) {
        throw new Error(
          `Refusing to downgrade ${name} from ${currentVersion} to ${target}`
        );
      }
      manifest[section][name] = alias
        ? `npm:${workspaceName}@${target}`
        : target;
      updated.push(name);
    }
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
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const manifests = [
    ["apps/mobile/package.json", ["dependencies"]],
    ["packages/components/package.json", ["dependencies"]],
    ["package.json", ["dependencies", "resolutions"]],
  ];
  for (const [relativePath, sections] of manifests) {
    const manifestPath = join(resolve(appMonorepoArg), relativePath);
    const original = await readFile(manifestPath, "utf8");
    const { text, updated } = updateMobileManifest(
      original,
      workspaces,
      sections
    );
    if (updated.length > 0) {
      await writeFile(manifestPath, text);
    }
    console.log(`Updated ${updated.length} dependencies in ${relativePath}`);
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
