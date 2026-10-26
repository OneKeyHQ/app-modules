import { readFile, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

import {
  compareReleaseVersions,
  loadReleaseWorkspaces,
  parseReleaseVersion,
  validateNpmDistTag,
} from "./validate-npm-dist-tag.mjs";
import { mapConcurrent, packumentUrl } from "./verify-published.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
const dependencySections = [
  "dependencies",
  "devDependencies",
  "optionalDependencies",
  "peerDependencies",
];

export function makePreviewVersion(
  stableVersion,
  previewNumber,
  publishedVersions = []
) {
  const current = parseReleaseVersion(stableVersion);
  const { appVersion } = current;
  parseReleaseVersion(`${appVersion}-alpha.${previewNumber}`);
  let counter = Number(previewNumber);
  for (const version of [stableVersion, ...publishedVersions]) {
    const parsed = parseReleaseVersion(version, { allowLegacy: true });
    if (parsed.appVersion === appVersion && parsed.channel === "next") {
      counter = Math.max(counter, parsed.number + 1);
    }
  }
  const previewVersion = `${appVersion}-alpha.${counter}`;
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

export async function preparePreview(
  repoRoot,
  previewNumber,
  distTag = "next"
) {
  if (distTag !== "next" && distTag !== "pr-145") {
    throw new Error(`Unsupported preview dist-tag: ${distTag}`);
  }
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  validateNpmDistTag(
    parseReleaseVersion(workspaces[0]?.version).channel,
    workspaces
  );
  makePreviewVersion(workspaces[0].version, previewNumber);
  const tagLists = await mapConcurrent(workspaces, async ({ name }) => {
    const response = await fetch(packumentUrl(name), {
      headers: { accept: "application/json" },
    });
    if (response.status === 404) {
      return [];
    }
    if (!response.ok) {
      throw new Error(`Registry returned HTTP ${response.status} for ${name}`);
    }
    const document = await response.json();
    const versions = [];
    for (const tag of new Set(["next", distTag])) {
      const version = document?.["dist-tags"]?.[tag];
      if (version) {
        versions.push(version);
      }
    }
    return versions;
  });
  const publishedVersions = tagLists.flat();
  const previewVersion = makePreviewVersion(
    workspaces[0].version,
    previewNumber,
    publishedVersions
  );
  const count = await setWorkspaceVersions(repoRoot, previewVersion);
  console.error(`Prepared ${count} preview workspaces at ${previewVersion}`);
  return previewVersion;
}

async function main() {
  const [previewNumber, distTag = "next"] = process.argv.slice(2);
  const previewVersion = await preparePreview(repoRoot, previewNumber, distTag);
  console.log(previewVersion);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
