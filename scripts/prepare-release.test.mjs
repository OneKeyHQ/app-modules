import assert from "node:assert/strict";
import test from "node:test";

import { renderReleaseChangelog } from "./prepare-release.mjs";

const changelog =
  "# Changelog\n\n## [Unreleased]\n\n### Bug Fixes\n- Detailed fix.\n\n## [3.0.151] - 2026-09-20\n\nOlder notes.\n";

test("preserves detailed unreleased notes and lists every merged change", () => {
  const result = renderReleaseChangelog(changelog, {
    version: "3.0.152",
    date: "2026-09-21",
    commitTitles: ["fix: first (#122)", "feat: second (#123)"],
    workspaceCount: 41,
  });
  assert.match(result, /## \[Unreleased\]\n\n## \[3\.0\.152\] - 2026-09-21/);
  assert.match(result, /### Bug Fixes\n- Detailed fix\./);
  assert.match(
    result,
    /### Merged changes\n- fix: first \(#122\)\n- feat: second \(#123\)/
  );
  assert.match(result, /Bump all 41 publishable packages to 3\.0\.152/);
  assert.match(result, /## \[3\.0\.151\] - 2026-09-20\n\nOlder notes\./);
});

test("creates a release without handwritten unreleased notes", () => {
  const result = renderReleaseChangelog(
    "# Changelog\n\n## [Unreleased]\n\n## [3.0.151] - 2026-09-20\n",
    {
      version: "3.0.152",
      date: "2026-09-21",
      commitTitles: ["fix: issue (#122)"],
      workspaceCount: 41,
    }
  );
  assert.match(result, /## \[3\.0\.152\] - 2026-09-21\n\n### Merged changes/);
});

test("refuses duplicate versions and empty release ranges", () => {
  const options = {
    version: "3.0.151",
    date: "2026-09-21",
    commitTitles: ["fix: issue (#122)"],
    workspaceCount: 41,
  };
  assert.throws(
    () => renderReleaseChangelog(changelog, options),
    /already contains/
  );
  assert.throws(
    () =>
      renderReleaseChangelog(changelog, {
        ...options,
        version: "3.0.152",
        commitTitles: [],
      }),
    /No merged commits/
  );
});
