import { fileURLToPath } from "node:url";

export function detectReleaseChannel({
  eventName,
  baseRef,
  ref,
  manualDistTag,
}) {
  if (eventName === "pull_request" && baseRef === "main") {
    return "latest";
  }
  if (eventName !== "workflow_dispatch") {
    throw new Error("Automatic releases require a merged PR targeting main");
  }
  if (!["next", "latest", "pr-145"].includes(manualDistTag)) {
    throw new Error(`Unsupported npm dist-tag: ${manualDistTag}`);
  }
  if (!ref?.startsWith("refs/heads/")) {
    throw new Error("Manual releases require a branch ref");
  }
  if (ref === "refs/heads/main" && manualDistTag !== "latest") {
    throw new Error(
      "main publishes stable releases; select latest or use a development branch for alpha previews"
    );
  }
  if (ref !== "refs/heads/main" && manualDistTag === "latest") {
    throw new Error(
      "Development branches publish alpha previews; merge into main for a stable release"
    );
  }
  return manualDistTag;
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  try {
    console.log(
      detectReleaseChannel({
        eventName: process.env.EVENT_NAME,
        baseRef: process.env.PR_BASE_REF,
        ref: process.env.RELEASE_REF,
        manualDistTag: process.env.MANUAL_DIST_TAG,
      })
    );
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
