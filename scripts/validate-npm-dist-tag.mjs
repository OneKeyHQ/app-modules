import { readdir, readFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const allowedDistTags = new Set(["latest", "next", "pr-145"]);

export function parseReleaseVersion(version, { allowLegacy = false } = {}) {
  const match =
    typeof version === "string" &&
    /^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-([1-9]\d*)|-alpha\.([1-9]\d*))?$/.exec(
      version
    );
  if (
    !match ||
    (!allowLegacy && !match[4] && !match[5]) ||
    match
      .slice(1)
      .some((part) => part !== undefined && !Number.isSafeInteger(Number(part)))
  ) {
    throw new Error(
      `Expected <app-version>-N or <app-version>-alpha.N (N >= 1), got ${version}`
    );
  }
  return {
    appVersion: match.slice(1, 4).join("."),
    channel: match[5] ? "next" : "latest",
    number: Number(match[4] ?? match[5] ?? 0),
    legacy: !match[4] && !match[5],
  };
}

// Compare App baselines first, then the counter within a release channel.
// Numeric releases and alpha previews have independent counters; allow exact
// dependency pins to switch between them instead of using SemVer's alpha > N.
// Legacy plain versions are accepted only when inspecting existing consumers
// or registry tags during migration, never as new publish targets.
export function compareReleaseVersions(left, right) {
  const a = parseReleaseVersion(left, { allowLegacy: true });
  const b = parseReleaseVersion(right, { allowLegacy: true });
  const aCore = a.appVersion.split(".").map(Number);
  const bCore = b.appVersion.split(".").map(Number);
  for (let index = 0; index < 3; index += 1) {
    if (aCore[index] !== bCore[index]) {
      return aCore[index] - bCore[index];
    }
  }
  if (a.channel !== b.channel) {
    if (a.legacy || b.legacy) {
      return a.channel === "latest" ? 1 : -1;
    }
    return 0;
  }
  return a.number - b.number;
}

export function validateNpmDistTag(distTag, releaseWorkspaces) {
  if (!allowedDistTags.has(distTag)) {
    throw new Error(`Unsupported npm dist-tag: ${distTag}`);
  }

  if (
    releaseWorkspaces.length === 0 ||
    new Set(releaseWorkspaces.map(({ version }) => version)).size !== 1
  ) {
    throw new Error(
      "Publishable workspace versions must match and must not be empty"
    );
  }
  const { version } = releaseWorkspaces[0];
  const channel = distTag === "pr-145" ? "next" : distTag;
  if (parseReleaseVersion(version).channel !== channel) {
    throw new Error(
      `Version ${version} cannot use the ${distTag} dist-tag: numeric revisions require latest; alpha previews require next`
    );
  }
}

export async function loadReleaseWorkspaces(repoRoot) {
  const rootPackage = JSON.parse(
    await readFile(join(repoRoot, "package.json"), "utf8")
  );
  const releaseWorkspaces = [];

  for (const workspacePattern of rootPackage.workspaces ?? []) {
    if (!workspacePattern.endsWith("/*")) {
      throw new Error(`Unsupported workspace pattern: ${workspacePattern}`);
    }
    const workspaceRoot = join(repoRoot, workspacePattern.slice(0, -2));
    const entries = await readdir(workspaceRoot, { withFileTypes: true });
    for (const entry of entries) {
      if (!entry.isDirectory()) {
        continue;
      }
      const packagePath = join(workspaceRoot, entry.name, "package.json");
      let workspacePackage;
      try {
        workspacePackage = JSON.parse(await readFile(packagePath, "utf8"));
      } catch (error) {
        if (error?.code === "ENOENT") {
          continue;
        }
        throw error;
      }
      if (
        workspacePackage.private !== true &&
        typeof workspacePackage.scripts?.release === "string"
      ) {
        const manifestPath = join(
          workspacePattern.slice(0, -2),
          entry.name,
          "package.json"
        );
        releaseWorkspaces.push({
          name: workspacePackage.name,
          version: workspacePackage.version,
          manifestPath,
        });
      }
    }
  }

  return releaseWorkspaces;
}

async function main() {
  const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
  const releaseWorkspaces = await loadReleaseWorkspaces(repoRoot);
  const distTag =
    process.argv[2] ??
    parseReleaseVersion(releaseWorkspaces[0]?.version).channel;
  validateNpmDistTag(distTag, releaseWorkspaces);
  console.log(
    `Validated npm dist-tag ${distTag} for ${releaseWorkspaces.length} release workspaces`
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
