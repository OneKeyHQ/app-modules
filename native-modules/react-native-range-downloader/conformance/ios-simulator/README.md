# iOS end-to-end OCDS verification on the Simulator

Drives the **real** Release `.app` on a booted simulator through every OCDS §6
scenario against a local HTTP fault server, and adversarially verifies the
result. The app's *own* native concurrent downloader runs — this is not a
re-implementation.

## What is real vs. synthetic (READ THIS — scope & honesty)

This is a **fake update task running through real download code.** Be clear on
the boundary before trusting a result:

**Real (the thing under test):**
- The app itself and its native concurrent downloader (`RangeDownloader.swift`, `ReactNativeBundleUpdate.swift`).
- The trigger path: on launch the app does its normal update check → because the manifest declares `updateStrategy: silent`, `AppUpdateForeground` **auto-calls `downloadPackage()`** — the same code path a real silent update uses. No UI is poked; **every launch auto-starts one download.**
- 8-way concurrent Range fetch, `.segN` segment files, concatenation, SHA-256 verification, and all retry / fallback / stall / resume logic.

**Synthetic (faked by this harness):**
- The update server, the manifest, and the version number (`localhost:8788` impersonates the update backend; version `202699999` is invented).
- The downloaded "bundle" is **16 MB of deterministic filler bytes, NOT a real signed OneKey bundle.** The manifest's `sha256` is the sha of those filler bytes, so the integrity check passes legitimately.

**What this does NOT verify (out of OCDS scope):**
- Post-download **unzip → signature/ASC verification → install → relaunch**. Because the payload is not a valid signed zip, `verifyBundleASC` (SSZipArchive) fails *after* a successful download. That is expected and does **not** affect any download-layer conclusion — but it means the full "download a real bundle and boot into it" chain is unproven here.
- To also cover install/verify/relaunch, point the server at a **real signed test bundle** (a genuine bundle + its true `sha256`/`signature` from a test channel) instead of the filler payload.

## Files

| File | Role |
|---|---|
| `server.js` | Local HTTP fault server (`:8788`). Serves a 16 MB bundle over Range with per-scenario fault injection + a control plane (`/ocds/scenario`, `/ocds/log`, `/ocds/reset`, `/ocds/health`). |
| `drive.sh` | Bash helpers: `simctl` lifecycle, app-container `.segN` / `app-latest.log` inspection, scenario control, clean reinstall. |
| `capture.sh` | Deterministic per-scenario driver → writes an evidence bundle to `evidence/<scenario>.json`. |
| `ocds-verify.workflow.js` | Multi-agent workflow: Preflight (prove the seam) → serial Capture (all scenarios) → parallel adversarial Verify → Synthesis (SPEC §6 table). Run with the app's Workflow tool. |

## Why a special build is needed (the "local-verify" patches)

iOS hard-blocks non-HTTPS downloads in native code and there is no production
seam to repoint the bundle URL. So a localhost fault server requires **temporary,
never-shipped** patches. iOS ATS already exempts `localhost` cleartext, so no
TLS/cert is needed — only these guard relaxations + a JS override:

1. **`react-native-range-downloader/ios/ReactNativeRangeDownloader.swift`** — the URL guard (`guard urlString.hasPrefix("https://")`): add `|| urlString.hasPrefix("http://localhost")`.
2. **`react-native-bundle-update/ios/ReactNativeBundleUpdate.swift`** — the entry URL guard (`guard downloadUrl.hasPrefix("https://")`): add `|| downloadUrl.hasPrefix("http://localhost")`.
3. **`react-native-bundle-update/ios/ReactNativeBundleUpdate.swift`** — the single-stream post-download redirect guard (`responseUrl.scheme != "https"`): add `&& !(responseUrl.host == "localhost")`.
4. **app-monorepo `packages/kit-bg/src/services/ServiceAppUpdate.ts` → `fetchConfig`** — short-circuit: fetch `http://localhost:8788/ocds/manifest.json`; if up, return it as the update info (the manifest sets `updateStrategy: silent` so the app **auto-downloads on launch with zero UI**). Also relax the two JS `startsWith('https://')` guards to allow `http://localhost`.

All four are marked `OCDS-LOCAL-VERIFY(temp)` in the source. They are local-only,
must never be committed, and are wiped from `node_modules` on the next
`yarn install`. (Patches 1–3 are compiled into the binary, so they require a
native rebuild; patch 4 ships in the JS bundle.)

## Run it

```bash
# 0. Apply the 4 local-verify patches above (to node_modules + the monorepo JS).
# 1. Build & deploy the Release app to a booted simulator (from app-monorepo):
./development/scripts/ios-release-build-deploy.sh xcode     # native (after patches 1-3)
./development/scripts/ios-release-build-deploy.sh build     # HBC bundles (after patch 4)
./development/scripts/ios-release-build-deploy.sh deploy    # install + launch

# 2. Start the fault server:
node conformance/ios-simulator/server.js          # logs to stdout; listens on :8788

# 3a. Single scenario (deterministic, no agents):
bash conformance/ios-simulator/capture.sh clean normal
bash conformance/ios-simulator/capture.sh delay-tail kill-resume   # #2 resume
#    -> writes evidence/<scenario>.json

# 3b. Full sweep + adversarial verification (multi-agent):
#    invoke the Workflow tool with scriptPath=.../conformance/ios-simulator/ocds-verify.workflow.js
```

## Scenarios (server.js) → SPEC §6

`clean` (#1/#2 base) · `delay-tail`+`kill-resume` (#2) · `range-ignored-probe` /
`range-ignored-segment` / `permanent-4xx` (#3) · `transient-5xx` / `range-416`
(#1/#4) · `short-body` / `overlong-206` / `misaligned-206` / `bad-total-206` (#5)
· `corrupt-bytes` (#6) · `flap` (#7) · `stall` (#10) · `give-up` (#9).

Not covered without a shim: **#8 cancel** (no cancel trigger exists in the iOS
app), **#11 two-concurrent-download** (native single-flight guard).

## Reading evidence

`evidence/<scenario>.json` carries: `serverRangeStarts` / `serverStatuses` (what
the server saw), `appLogTail` (filtered `app-latest.log`), `shaMatch` /
`finalSha` (assembled-file integrity), `segPeak`, and for kill-resume
`seededSegs` (head segments on disk at the force-quit — must NOT be re-fetched on
resume). A scenario passes only when server log + app log + sha agree.

## Known traps (learned the hard way)

- **Don't fault the probe.** The downloader probes with `bytes=0-0` (start `0`, shared with segment 0's start). Per-segment fault scenarios must exempt `start===0 && end===0`, or the probe fails and the run wrongly takes a single-stream fallback instead of exercising per-segment retry.
- **Don't treat an intermediate `retry N/5` as terminal.** `give-up`'s "retry 5/5" only *schedules* the final retry (24s backoff); wait ~28s past it for the 5th attempt to fire and the terminal failed-result to log.
- **localhost downloads finish <1s**, so on-disk `.segN` are not reliably caught by polling (`segPeak` is noisy). Judge segmentation by the distinct Range windows in the server log, not `segPeak`.
- **Resume needs the app's OWN segments**, not orphan `curl`'d files — pre-seeded orphan `.segN` are not honored. Use `delay-tail` + force-quit + relaunch (the app writes real segments, which survive `simctl terminate` and are reused).
