import { readFile, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

import {
  compareReleaseVersions,
  loadReleaseWorkspaces,
  parseReleaseVersion,
} from "./validate-npm-dist-tag.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
const dependencySections = [
  "dependencies",
  "devDependencies",
  "optionalDependencies",
  "peerDependencies",
];

export function makePreviewVersion(stableVersion, previewNumber) {
  const { appVersion } = parseReleaseVersion(stableVersion);
  const previewVersion = `${appVersion}-alpha.${previewNumber}`;
  parseReleaseVersion(previewVersion);
  return previewVersion;
}

export function updatePreviewManifest(
  manifest,
  { currentVersion, previewVersion, workspaceNames }
) {
  const updated = { ...manifest, version: previewVersion };
  for (const section of dependencySections) {
    if (!manifest[section]) {
      continue;
    }
    updated[section] = { ...manifest[section] };
    for (const [name, version] of Object.entries(updated[section])) {
      if (workspaceNames.has(name) && version === currentVersion) {
        updated[section][name] = previewVersion;
      }
    }
  }
  return updated;
}

export async function setWorkspaceVersions(repoRoot, previewVersion) {
  parseReleaseVersion(previewVersion);
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const versions = new Set(workspaces.map(({ version }) => version));
  if (workspaces.length === 0 || versions.size !== 1) {
    throw new Error(
      "Publishable workspace versions must match before updating"
    );
  }
  const [currentVersion] = versions;
  if (compareReleaseVersions(currentVersion, previewVersion) > 0) {
    throw new Error(
      `Refusing to downgrade workspaces from ${currentVersion} to ${previewVersion}`
    );
  }
  const workspaceNames = new Set(workspaces.map(({ name }) => name));

  for (const { manifestPath } of workspaces) {
    const path = join(repoRoot, manifestPath);
    const manifest = JSON.parse(await readFile(path, "utf8"));
    const updated = updatePreviewManifest(manifest, {
      currentVersion,
      previewVersion,
      workspaceNames,
    });
    await writeFile(path, `${JSON.stringify(updated, null, 2)}\n`);
  }
  return workspaces.length;
}

async function main() {
  const [previewNumber] = process.argv.slice(2);
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const previewVersion = makePreviewVersion(
    workspaces[0]?.version,
    previewNumber
  );
  const count = await setWorkspaceVersions(repoRoot, previewVersion);
  console.error(`Prepared ${count} preview workspaces at ${previewVersion}`);
  console.log(previewVersion);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
