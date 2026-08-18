import { readdir, readFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const allowedDistTags = new Set(["latest", "next"]);

export function validateNpmDistTag(distTag, releaseWorkspaces) {
  if (!allowedDistTags.has(distTag)) {
    throw new Error(`Unsupported npm dist-tag: ${distTag}`);
  }

  if (distTag !== "latest") {
    return;
  }

  const prereleaseWorkspaces = releaseWorkspaces.filter(({ version }) =>
    version.includes("-")
  );
  if (prereleaseWorkspaces.length === 0) {
    return;
  }

  const versions = prereleaseWorkspaces
    .map(({ name, version }) => `${name}@${version}`)
    .sort()
    .join(", ");
  throw new Error(
    `Refusing to publish prerelease workspaces with the latest dist-tag: ${versions}`
  );
}

async function loadReleaseWorkspaces(repoRoot) {
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
        releaseWorkspaces.push({
          name: workspacePackage.name,
          version: workspacePackage.version,
        });
      }
    }
  }

  return releaseWorkspaces;
}

async function main() {
  const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
  const releaseWorkspaces = await loadReleaseWorkspaces(repoRoot);
  validateNpmDistTag(process.argv[2], releaseWorkspaces);
  console.log(
    `Validated npm dist-tag ${process.argv[2]} for ${releaseWorkspaces.length} release workspaces`
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
