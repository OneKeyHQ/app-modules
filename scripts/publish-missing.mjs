import { spawnSync } from "node:child_process";
import { dirname } from "node:path";
import { fileURLToPath } from "node:url";

import {
  loadReleaseWorkspaces,
  validateNpmDistTag,
} from "./validate-npm-dist-tag.mjs";
import { packumentUrl } from "./verify-published.mjs";

const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
const nativeListName = "@onekeyfe/react-native-native-list";

function compareStableVersions(left, right) {
  if (!/^\d+\.\d+\.\d+$/.test(left) || !/^\d+\.\d+\.\d+$/.test(right)) {
    return null;
  }
  const a = left.split(".").map(Number);
  const b = right.split(".").map(Number);
  for (let index = 0; index < 3; index += 1) {
    if (a[index] !== b[index]) {
      return a[index] - b[index];
    }
  }
  return 0;
}

export function selectUnpublished(workspaces, packuments, distTag) {
  const missing = [];
  for (const workspace of workspaces) {
    const document = packuments.get(workspace.name);
    const tagged = document?.["dist-tags"]?.[distTag];
    if (tagged && compareStableVersions(tagged, workspace.version) > 0) {
      throw new Error(
        `Refusing to move ${workspace.name} ${distTag} backwards from ${tagged} to ${workspace.version}`
      );
    }
    if (document?.versions?.[workspace.version]) {
      if (tagged !== workspace.version) {
        throw new Error(
          `${workspace.name}@${
            workspace.version
          } already exists but ${distTag} points at ${tagged ?? "nothing"}`
        );
      }
    } else {
      missing.push(workspace);
    }
  }
  return missing;
}

async function readPackument(name) {
  const response = await fetch(packumentUrl(name), {
    headers: { accept: "application/json" },
  });
  if (response.status === 404) {
    return null;
  }
  if (!response.ok) {
    throw new Error(`Registry returned HTTP ${response.status} for ${name}`);
  }
  return response.json();
}

function runYarn(args) {
  const result = spawnSync("yarn", args, { cwd: repoRoot, stdio: "inherit" });
  if (result.error) {
    throw result.error;
  }
  if (result.status !== 0) {
    throw new Error(`yarn ${args.join(" ")} failed with exit ${result.status}`);
  }
}

async function main() {
  const [distTag] = process.argv.slice(2);
  const workspaces = await loadReleaseWorkspaces(repoRoot);
  validateNpmDistTag(distTag, workspaces);
  if (
    workspaces.length === 0 ||
    new Set(workspaces.map(({ version }) => version)).size !== 1
  ) {
    throw new Error("Publishable workspace versions must match before release");
  }
  const packuments = new Map();
  for (const { name } of workspaces) {
    packuments.set(name, await readPackument(name));
  }
  const missing = selectUnpublished(workspaces, packuments, distTag);
  console.log(
    `${missing.length} of ${workspaces.length} packages still need publishing`
  );
  const first = missing.filter(({ name }) => name !== nativeListName);
  if (first.length > 0) {
    runYarn([
      "workspaces",
      "foreach",
      "--all",
      "--topological",
      "--parallel",
      "--jobs",
      "4",
      "--interlaced",
      ...first.flatMap(({ name }) => ["--include", name]),
      "run",
      "release",
      "--tag",
      distTag,
    ]);
  }
  if (missing.some(({ name }) => name === nativeListName)) {
    runYarn(["workspace", nativeListName, "release", "--tag", distTag]);
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
