import assert from "node:assert/strict";
import test from "node:test";

import { detectReleaseChannel } from "./detect-release-channel.mjs";

test("main PR merges always select stable without inspecting version changes or PR heads", () => {
  assert.equal(
    detectReleaseChannel({ eventName: "pull_request", baseRef: "main" }),
    "latest"
  );
});

test("development branches manually select alpha channels", () => {
  for (const manualDistTag of ["next", "pr-145"]) {
    assert.equal(
      detectReleaseChannel({
        eventName: "workflow_dispatch",
        ref: "refs/heads/codex/feature",
        manualDistTag,
      }),
      manualDistTag
    );
  }
});

test("main permits manual stable publication for recovery", () => {
  assert.equal(
    detectReleaseChannel({
      eventName: "workflow_dispatch",
      ref: "refs/heads/main",
      manualDistTag: "latest",
    }),
    "latest"
  );
});

test("rejects stable publication from development branches and alpha publication from main", () => {
  for (const [ref, manualDistTag] of [
    ["refs/heads/codex/feature", "latest"],
    ["refs/heads/main", "next"],
    ["refs/heads/main", "pr-145"],
  ]) {
    assert.throws(
      () =>
        detectReleaseChannel({
          eventName: "workflow_dispatch",
          ref,
          manualDistTag,
        }),
      /Development branches publish alpha|main publishes stable/
    );
  }
});

test("rejects unsupported events, non-main targets, tags and invalid channels", () => {
  for (const options of [
    { eventName: "push", baseRef: "main" },
    { eventName: "pull_request", baseRef: "dev" },
    {
      eventName: "workflow_dispatch",
      ref: "refs/tags/v1",
      manualDistTag: "next",
    },
    {
      eventName: "workflow_dispatch",
      ref: "refs/heads/codex/feature",
      manualDistTag: "beta",
    },
  ]) {
    assert.throws(() => detectReleaseChannel(options));
  }
});
