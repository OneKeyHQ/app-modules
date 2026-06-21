# OCDS Conformance — verification methods & code (Node / Android / iOS)

This directory records **how each platform's concurrent downloader is verified
against [the OCDS standard](../SPEC.md)**, and houses the runnable verification
code. Re-run the relevant suite whenever you touch downloader code.

The normative behaviors are SPEC §5 (coverage areas) and SPEC §6 (the 11
conformance scenarios `#1`–`#11`). Every suite below maps back to those.

---

## At a glance

| Platform | How it's verified | Where the code lives | How to run |
|---|---|---|---|
| **Node / Desktop** | Jest e2e against a real local Range HTTP/HTTPS server driving the **real** `DesktopApiBundleUpdate` downloader (positioned writes + `.partial` manifest) | app-monorepo `packages/kit-bg/src/desktopApis/DesktopApiBundleUpdate.e2e*.test.ts` + `__e2e__/desktopBundleUpdateE2eHarness.ts` | `cd app-monorepo && yarn jest packages/kit-bg/src/desktopApis/DesktopApiBundleUpdate.e2e` |
| **Android** | Pure-JVM unit + OkHttp **MockWebServer** fault injection against the real `ConcurrentRangeDownloader` (segment-file model) | this module: `android/src/test/java/com/margelo/nitro/reactnativerangedownloader/` (`ConcurrentRangeDownloaderOcdsTest.kt`, `FaultServer.kt`, `IsPermanentHttpStatusTest.kt`, `SmokeTest.kt`) | `cd android && ANDROID_HOME=~/Library/Android/sdk ./gradlew test` |
| **iOS (logic)** | SwiftPM unit tests over the dependency-free logic (`RangeDownloadLogic`), source-symlinked so tests exercise the real shipping code | this module: `tests/swiftpm/` | `swift test --package-path tests/swiftpm` |
| **iOS (end-to-end)** | Real Release `.app` on a booted **simulator**, driven by a local HTTP fault server + a multi-agent workflow; asserts on the app's `.segN`/`app-latest.log`/server log | this module: [`conformance/ios-simulator/`](./ios-simulator/) | see [`ios-simulator/README.md`](./ios-simulator/README.md) |

> The Node downloader is a TypeScript module that lives in the app-monorepo
> (Electron), so its runnable tests stay there; the table above is the
> authoritative pointer. Android + iOS native downloaders live in **this** repo,
> so their suites live here.

---

## Latest iOS end-to-end result (simulator)

Verified on a real `-configuration Release` build (iPhone 17 Pro sim) against the
local fault server. **9 of 11 SPEC §6 rows pass on-simulator**; 2 not reachable
without a temporary shim. (#6's earlier "deviation" was resolved at the spec
level — see below.)

> **Scope:** this is a *fake update task running through real download code*. The
> app's own native downloader, its silent-auto-download trigger, segmentation,
> resume, retry, fallback and SHA-256 verification are all **real**; the update
> server, manifest, version, and the 16 MB bundle payload are **synthetic** (the
> payload is filler bytes, not a signed zip). It therefore verifies the
> **download layer only** — the post-download unzip → signature-verify → install →
> relaunch chain is **not** exercised (the synthetic payload fails `verifyASC`
> after a successful download, which is expected and out of OCDS scope). See
> [`ios-simulator/README.md`](./ios-simulator/README.md) → "What is real vs.
> synthetic" for the full boundary and how to extend it to a real signed bundle.

| SPEC §6 | Behavior | Result |
|---|---|---|
| #1 | Transient error → backoff retry, no restart | ✅ PASS |
| #2 | Killed mid-run → resume from completed `.segN` (no restart-from-0) | ✅ PASS |
| #3 | `200` to a Range request → permanent → single-stream | ✅ PASS (probe-200, segment-200, 404 variants) |
| #4 | `416` / transient range error → re-evaluate, retry (not permanent) | ✅ PASS |
| #5 | short / over-long / mis-aligned / bad-total `206` → rejected, final checksum passes | ✅ PASS (4 variants) |
| #6 | Corrupted assembly → checksum mismatch → discard → terminal failure, no infinite loop | ✅ PASS — mismatch detected (`valid=false`), artifacts discarded, bounded (one concurrent attempt), terminal failure surfaced. (Conformant as of OCDS 1.2, which removed the over-specified single-stream retry-once — all 3 platforms terminate on mismatch.) |
| #7 | Network flap / lock / background → transient retry, progress never resets to 0 | ✅ PASS (literal screen-lock isn't performable on the simulator and is behaviorally redundant with the kill/flap paths) |
| #8 | User cancel mid-run | ❌ NOT COVERED — **no cancel trigger exists in the iOS app flow at all** (also a product gap); needs a temp shim calling `RangeDownloader.cancel` |
| #9 | Persistent failure → bounded terminal give-up, no infinite loop | ✅ PASS (5-attempt exponential backoff ladder then terminal failure) |
| #10 | Stalled socket → stall watchdog cancels + retries | ✅ PASS (per-segment watchdog at 30s) |
| #11 | Two concurrent `download()` for the same dest → single-flight | ❌ NOT COVERED — native single-flight guard blocks a 2nd call; needs a shim or a bg/main dual-dispatch harness |

**Resolved (#6):** the earlier "no single-stream retry-once" finding was an
over-specification in the standard, not an implementation bug — all three
platforms (iOS, Android, Node) terminate on a checksum mismatch rather than
re-fetching the same (would-re-corrupt) object. OCDS **1.2** removed the
retry-once requirement; a mismatch is now simply Permanent → discard → terminal.

---

## How to re-test after a code change

- **Touching `ConcurrentRangeDownloader.kt` / Android segment logic** → run the Android suite.
- **Touching `ReactNativeRangeDownloader.swift` / `RangeDownloadLogic.swift`** → run the SwiftPM logic suite; for behavioral changes (retry, fallback, resume, stall) also run the iOS simulator suite.
- **Touching `ReactNativeBundleUpdate.swift` download orchestration** → run the iOS simulator suite (it exercises the real caller→core path).
- **Touching `DesktopApiBundleUpdate.ts`** → run the Node e2e suite in app-monorepo.
- **Changing the OCDS standard itself** → update [`SPEC.md`](../SPEC.md) and add/adjust scenarios in every suite.
