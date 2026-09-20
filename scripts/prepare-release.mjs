import { execFileSync } from "node:child_process";
import { readFile, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

import { loadReleaseWorkspaces } from "./validate-npm-dist-tag.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
const changelogPath = join(repoRoot, "CHANGELOG.md");

export function renderReleaseChangelog(
  changelog,
  { version, date, commitTitles, workspaceCount }
) {
  const marker = "## [Unreleased]\n";
  const markerStart = changelog.indexOf(marker);
  if (markerStart < 0 || changelog.indexOf(marker, markerStart + 1) >= 0) {
    throw new Error("CHANGELOG.md must contain exactly one Unreleased section");
  }
  if (changelog.includes(`## [${version}]`)) {
    throw new Error(`CHANGELOG.md already contains ${version}`);
  }
  if (commitTitles.length === 0) {
    throw new Error("No merged commits since the previous release");
  }

  const bodyStart = markerStart + marker.length;
  const remaining = changelog.slice(bodyStart);
  const nextHeading = remaining.search(/^## \[/m);
  if (nextHeading < 0) {
    throw new Error("CHANGELOG.md has no previous release section");
  }
  const unreleased = remaining.slice(0, nextHeading).trim();
  const previousReleases = remaining.slice(nextHeading);
  const mergedChanges = commitTitles.map((title) => `- ${title}`).join("\n");
  const releaseBody = [
    unreleased,
    `### Merged changes\n${mergedChanges}`,
    `### Version\n- Bump all ${workspaceCount} publishable packages to ${version}.`,
  ]
    .filter(Boolean)
    .join("\n\n");

  return (
    changelog.slice(0, bodyStart) +
    `\n## [${version}] - ${date}\n\n${releaseBody}\n\n` +
    previousReleases
  );
}

function git(...args) {
  return execFileSync("git", args, { cwd: repoRoot, encoding: "utf8" }).trim();
}

async function main() {
  const previousRelease = git(
    "log",
    "-1",
    "--format=%H",
    "--extended-regexp",
    "--grep=^release: app-modules [0-9]+\\.[0-9]+\\.[0-9]+"
  );
  if (!previousRelease) {
    throw new Error("Cannot find the previous app-modules release commit");
  }
  const commitTitles = git(
    "log",
    "--reverse",
    "--format=%s",
    `${previousRelease}..HEAD`
  )
    .split("\n")
    .filter(Boolean);
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const versions = new Set(workspaces.map(({ version }) => version));
  if (versions.size !== 1 || workspaces.length === 0) {
    throw new Error("Publishable workspace versions must match before release");
  }
  const [version] = versions;
  const changelog = await readFile(changelogPath, "utf8");
  const updated = renderReleaseChangelog(changelog, {
    version,
    date: new Date().toISOString().slice(0, 10),
    commitTitles,
    workspaceCount: workspaces.length,
  });
  await writeFile(changelogPath, updated);
  console.log(
    `Prepared CHANGELOG.md for ${version} (${commitTitles.length} commits)`
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
