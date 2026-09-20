import { dirname } from "node:path";
import { fileURLToPath } from "node:url";

import { loadReleaseWorkspaces } from "./validate-npm-dist-tag.mjs";

const REGISTRY = "https://registry.npmjs.org";

// npm reports a publish as successful the moment it accepts the tarball, but
// acceptance is not availability: a version can sit staged and never become
// downloadable, and the job still goes green. That happened to
// @onekeyfe/react-native-bundle-crypto@3.0.137 — npm printed
// `+ @onekeyfe/react-native-bundle-crypto@3.0.137`, every workflow step passed,
// and the version never appeared. It could not even be republished afterwards
// ("409 Cannot publish over previously staged version"), so the number was
// burned and the whole set had to move to 3.0.138. This check exists so that a
// release like that fails loudly in the run that produced it.
const DEFAULT_MAX_ATTEMPTS = 10;
const POLL_INTERVAL_MS = 45 * 1000;

/**
 * The workspaces a run was supposed to publish. `onlyWorkspace` mirrors the
 * workflow input of the same name, so a single-package retry verifies only
 * that package instead of failing on the 39 it did not touch.
 */
export function selectWorkspaces(releaseWorkspaces, onlyWorkspace) {
  if (!onlyWorkspace) {
    return releaseWorkspaces;
  }
  const match = releaseWorkspaces.find(({ name }) => name === onlyWorkspace);
  if (!match) {
    const known = releaseWorkspaces
      .map(({ name }) => name)
      .sort()
      .join(", ");
    throw new Error(
      `Unknown workspace ${onlyWorkspace}. Publishable workspaces: ${known}`
    );
  }
  return [match];
}

/**
 * `?write=true` bypasses npm's read-through CDN. Plain reads (and `npm view`)
 * can serve a package document that is hours stale, which makes a healthy
 * publish look missing and would fail this check for the wrong reason.
 */
export function packumentUrl(name) {
  return `${REGISTRY}/${encodeURIComponent(name)}?write=true`;
}

/**
 * A version counts as published only when the registry lists it AND the
 * dist-tag we published under points at it. The tag matters: a version present
 * but untagged means consumers resolving by tag still get the old one.
 */
export function checkPackument(packument, version, distTag) {
  const versions = packument?.versions ?? {};
  if (!Object.prototype.hasOwnProperty.call(versions, version)) {
    return { ok: false, reason: `${version} is absent from the registry` };
  }
  const tagged = packument?.["dist-tags"]?.[distTag];
  if (tagged !== version) {
    return {
      ok: false,
      reason: `dist-tag ${distTag} points at ${
        tagged ?? "nothing"
      }, expected ${version}`,
    };
  }
  return { ok: true };
}

async function fetchPackument(name) {
  const response = await fetch(packumentUrl(name), {
    headers: { accept: "application/json" },
  });
  if (!response.ok) {
    return null;
  }
  return response.json();
}

const sleep = (ms) =>
  new Promise((resolve) => {
    setTimeout(resolve, ms);
  });

export async function verifyPublished(
  workspaces,
  distTag,
  {
    maxAttempts = DEFAULT_MAX_ATTEMPTS,
    pollIntervalMs = POLL_INTERVAL_MS,
    log = console.log,
  } = {}
) {
  let pending = workspaces;
  const lastReasons = new Map();

  for (
    let attempt = 1;
    attempt <= maxAttempts && pending.length > 0;
    attempt += 1
  ) {
    const stillPending = [];
    for (const workspace of pending) {
      let result;
      try {
        const packument = await fetchPackument(workspace.name);
        result = packument
          ? checkPackument(packument, workspace.version, distTag)
          : { ok: false, reason: "registry returned no package document" };
      } catch (error) {
        // A transient network blip must not fail the release; keep polling.
        result = { ok: false, reason: `registry request failed: ${error.message}` };
      }
      if (result.ok) {
        log(`  ok       ${workspace.name}@${workspace.version}`);
      } else {
        lastReasons.set(workspace.name, result.reason);
        stillPending.push(workspace);
      }
    }
    pending = stillPending;
    if (pending.length === 0 || attempt === maxAttempts) {
      break;
    }
    log(
      `  waiting  ${pending.length} package(s) not visible yet; re-checking in ${
        pollIntervalMs / 1000
      }s`
    );
    await sleep(pollIntervalMs);
  }

  return pending.map((workspace) => ({
    name: workspace.name,
    version: workspace.version,
    reason: lastReasons.get(workspace.name) ?? "unknown",
  }));
}

async function main() {
  const [distTag, onlyWorkspaceArg] = process.argv.slice(2);
  if (!distTag) {
    throw new Error("Usage: node scripts/verify-published.mjs <dist-tag> [workspace]");
  }
  const onlyWorkspace = onlyWorkspaceArg?.trim() || "";

  const repoRoot = dirname(dirname(fileURLToPath(import.meta.url)));
  const releaseWorkspaces = await loadReleaseWorkspaces(repoRoot);
  const workspaces = selectWorkspaces(releaseWorkspaces, onlyWorkspace);

  console.log(
    `Verifying ${workspaces.length} package(s) on ${REGISTRY} under dist-tag ${distTag}`
  );
  const missing = await verifyPublished(workspaces, distTag);

  if (missing.length > 0) {
    console.error(
      `\n${missing.length} package(s) did not become available:\n` +
        missing
          .map(({ name, version, reason }) => `  - ${name}@${version}: ${reason}`)
          .join("\n") +
        "\n\nThe versions were not visible after 10 checks. They may still be " +
        "propagating; retry verification before considering a new release. " +
        "If npm rejects republishing with 409, the staged version may be burned."
    );
    process.exitCode = 1;
    return;
  }
  console.log(`\nAll ${workspaces.length} package(s) verified.`);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
