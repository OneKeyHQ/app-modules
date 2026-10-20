import { dirname } from "node:path";
import { fileURLToPath } from "node:url";

import { setWorkspaceVersions } from "./prepare-preview.mjs";
import {
  loadReleaseWorkspaces,
  parseReleaseVersion,
} from "./validate-npm-dist-tag.mjs";

export function nextReleaseVersion(version) {
  const { appVersion, channel, number } = parseReleaseVersion(version);
  if (channel !== "latest") {
    throw new Error(
      "Choose an explicit numeric release with version:set after an alpha preview"
    );
  }
  const next = `${appVersion}-${number + 1}`;
  parseReleaseVersion(next);
  return next;
}

async function main() {
  const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
  const [requestedVersion] = process.argv.slice(2);
  if (!requestedVersion) {
    throw new Error(
      "Usage: yarn version:set <app-version>-N | <app-version>-alpha.N"
    );
  }
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  const version =
    requestedVersion === "--next"
      ? nextReleaseVersion(workspaces[0]?.version)
      : requestedVersion;
  const count = await setWorkspaceVersions(repoRoot, version);
  console.log(`Updated ${count} publishable workspaces to ${version}`);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
