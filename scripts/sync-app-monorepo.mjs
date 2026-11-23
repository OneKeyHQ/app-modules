import { readFile, writeFile } from "node:fs/promises";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

import {
  loadReleaseWorkspaces,
  compareReleaseVersions,
  parseReleaseVersion,
} from "./validate-npm-dist-tag.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));

export const appMonorepoManifests = [
  ["apps/mobile/package.json", ["dependencies"]],
  ["packages/components/package.json", ["dependencies"]],
  ["packages/core/package.json", ["dependencies"]],
  ["packages/kit/package.json", ["dependencies"]],
  ["apps/playground/package.json", ["dependencies"]],
  ["package.json", ["dependencies", "resolutions"]],
];

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
  parseReleaseVersion(workspaces[0].version);
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
      const migratingOverlay =
        workspaceName === "@onekeyfe/react-native-native-overlay" &&
        currentVersion === "workspace:*";
      if (
        !migratingOverlay &&
        compareReleaseVersions(currentVersion, target) > 0
      ) {
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
  const [appMonorepoArg, releaseVersion] = process.argv.slice(2);
  if (!appMonorepoArg || !releaseVersion) {
    throw new Error(
      "Usage: node scripts/sync-app-monorepo.mjs <app-monorepo-path> <release-version>"
    );
  }
  const workspaces = (await loadReleaseWorkspaces(repoRoot)).map(
    (workspace) => ({ ...workspace, version: releaseVersion })
  );
  await syncAppMonorepo(appMonorepoArg, workspaces);
}

export async function syncAppMonorepo(appMonorepoArg, workspaces) {
  for (const [relativePath, sections] of appMonorepoManifests) {
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
