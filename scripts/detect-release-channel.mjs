import { execFileSync } from "node:child_process";
import { dirname } from "node:path";
import { fileURLToPath } from "node:url";

import { loadReleaseWorkspaces } from "./validate-npm-dist-tag.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));

export function detectReleaseChannel(before, after) {
  const beforeVersions = new Map(
    before.map(({ name, version }) => [name, version])
  );
  return after.some(
    ({ name, version }) => beforeVersions.get(name) !== version
  )
    ? "latest"
    : "next";
}

function readVersion(ref, manifestPath) {
  try {
    const manifest = execFileSync(
      "git",
      ["show", `${ref}:${manifestPath}`],
      { cwd: repoRoot, encoding: "utf8" }
    );
    return JSON.parse(manifest).version;
  } catch {
    return undefined;
  }
}

async function main() {
  const [baseRef, headRef] = process.argv.slice(2);
  if (!baseRef || !headRef) {
    throw new Error(
      "Usage: node scripts/detect-release-channel.mjs <base-ref> <head-ref>"
    );
  }
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const mergeBase = execFileSync("git", ["merge-base", baseRef, headRef], {
    cwd: repoRoot,
    encoding: "utf8",
  }).trim();
  const before = workspaces.map(({ name, manifestPath }) => ({
    name,
    version: readVersion(mergeBase, manifestPath),
  }));
  const after = workspaces.map(({ name, manifestPath }) => ({
    name,
    version: readVersion(headRef, manifestPath),
  }));
  const channel = detectReleaseChannel(before, after);
  const changed = after
    .filter(
      ({ name, version }) =>
        before.find((workspace) => workspace.name === name)?.version !== version
    )
    .map(({ name }) => name);
  console.error(
    changed.length > 0
      ? `Version changes found in ${changed.length} publishable workspace(s); using latest`
      : "No publishable workspace version changes found; using next preview"
  );
  console.log(channel);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
