import { execFileSync } from "node:child_process";
import { existsSync } from "node:fs";
import { readFile } from "node:fs/promises";
import { dirname, join } from "node:path";
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

export async function setReleaseVersion(
  repoRoot,
  requestedVersion,
  { platform = process.platform, run = execFileSync } = {}
) {
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
  parseReleaseVersion(version);
  if (platform !== "darwin") {
    throw new Error("Preparing release versions requires macOS and CocoaPods");
  }
  const nativeRoot = join(repoRoot, "example/react-native");
  const rubyVersion = (
    await readFile(join(nativeRoot, ".ruby-version"), "utf8")
  ).trim();
  const options = {
    cwd: nativeRoot,
    stdio: "inherit",
    env: { ...process.env, BUNDLE_GEMFILE: join(nativeRoot, "Gemfile") },
  };
  // Check native prerequisites before changing any package versions.
  run(
    "ruby",
    [
      "-e",
      'abort "Use Ruby #{ARGV[0]} from example/react-native/.ruby-version" unless RUBY_VERSION == ARGV.fetch(0)',
      rubyVersion,
    ],
    options
  );
  run("xcodebuild", ["-version"], options);
  run("bundle", ["check"], options);
  run("bundle", ["exec", "pod", "--version"], options);
  const count = await setWorkspaceVersions(repoRoot, version);
  try {
    run("yarn", ["install"], {
      ...options,
      cwd: repoRoot,
      env: { ...options.env, YARN_ENABLE_IMMUTABLE_INSTALLS: "false" },
    });
    for (const { manifestPath } of workspaces) {
      const workspaceRoot = join(repoRoot, dirname(manifestPath));
      if (existsSync(join(workspaceRoot, "nitro.json"))) {
        run("yarn", ["--cwd", workspaceRoot, "nitrogen"], {
          ...options,
          cwd: repoRoot,
        });
      }
    }
    run("bundle", ["exec", "pod", "install"], {
      ...options,
      cwd: join(nativeRoot, "ios"),
    });
  } catch (error) {
    throw new Error(
      `Version preparation incomplete: ${error.message}\nFix the failure and rerun yarn version:set ${version} to synchronize the lockfiles.`,
      { cause: error }
    );
  }
  return { count, version };
}

async function main() {
  const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
  const { count, version } = await setReleaseVersion(repoRoot, process.argv[2]);
  console.log(
    `Updated ${count} publishable workspaces and synchronized Yarn/CocoaPods lockfiles to ${version}`
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
