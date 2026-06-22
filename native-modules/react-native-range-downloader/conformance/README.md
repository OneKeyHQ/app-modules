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
| **Android** | Pure-JVM unit + OkHttp **MockWebServer** fault injection against the real `ConcurrentRangeDownloader` (segment-file model) | this module: `android/src/test/java/com/margelo/nitro/reactnativerangedownloader/` (`ConcurrentRangeDownloaderOcdsTest.kt`, `FaultServer.kt`, `IsPermanentHttpStatusTest.kt`, `SmokeTest.kt`, `Ocds*.kt`) | the module has **no own `gradlew`** — build via the example host: `cd example/react-native/android && ANDROID_HOME=~/Library/Android/sdk ./gradlew :onekeyfe_react-native-range-downloader:testDebugUnitTest` |
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

## Audit gap closure (coverage delta)

An adversarial audit of the Android + Node suites found gaps (dead fault modes
never armed, behaviors only asserted by decision/log not outcome, and missing
malformed-response cases). New tests were added to close the testable ones, and
each was **mutation-verified** — a targeted regression was injected into the
production code to confirm the test actually goes red (a green test that survives
its own regression is not load-bearing).

**Android** (`android/src/test/.../Ocds*.kt`, 9 cases) — full module suite green:
| Test | SPEC | Mutation-proven |
|---|---|---|
| `OcdsTransient5xxTest` — 503 retry-in-place | #4 | ✅ 503→permanent |
| `OcdsTransient5xxTest` — budget exhaustion throws | #4/#9 | ✅ retry budget +2 |
| `OcdsTransient5xxTest` — 501 permanent bypass | #4 | ✅ remove `501,505→true` |
| `Ocds416ResumeTest` — 416 transient resume | #4/#6 | ✅ remove `416→false` |
| `Ocds416ResumeTest` — never discards seeded segs | #6 | ⚠️ green, no clean single-line regression (overlaps the resume test; only breaks under an insertion) |
| `OcdsMultipartRejectTest` | #5 | ✅ bypass multipart detection |
| `OcdsBadTotalRejectTest` | #5 | ✅ drop Content-Range total check |
| `OcdsReadOnlyFsTest` (a6a/a6b) | §5.2 | ⚠️ green, no clean single-line regression (read-only-fs guard needs an insertion-type mutation) |

**Node** (`packages/kit-bg/src/desktopApis/DesktopApiBundleUpdate.e2e.*.test.ts`):
| Test | SPEC | Mutation-proven |
|---|---|---|
| `e2e.transient416` — 416 concurrent | #4 | ✅ 416→permanent in `classifyHttpStatus` |
| `e2e.transient416` — 416 single-stream | #4 | ✅ break the single-stream 416 finalize |
| `e2e.handoff` — concurrent→single-stream | #3 | ✅ `isConcurrentFallback`→false |

**Android adapter layer** (`ReactNativeRangeDownloader.kt`) — **now CLOSED** via a
behavior-preserving extraction. The adapter's dependency-free pieces were moved
verbatim into `RangeDownloadLogic.kt` (the adapter delegates; diff +11/−21, pure
move) and unit-tested with **39 new pure-JVM tests** (`RangeDownloadLogicTest`,
`RunRegistrySingleFlightTest`, `MonotonicProgressGateTest`, `SegmentArtifactSweepTest`):
| Behavior | Extracted symbol | Mutation-proven |
|---|---|---|
| single-flight registry (dedup + identity-checked finish) | `RangeDownloadLogic.RunRegistry` | ✅ identity-drop mutant killed |
| progress clamp/guard feeding the (unchanged) CAS gate | `RangeDownloadLogic.progressPercent` | ✅ clamp-removal mutant killed |
| `.segN`/`.partial` artifact sweep | `RangeDownloadLogic.sweepPartialArtifacts` | ✅ glob-narrow mutant killed |
Behavior preservation is proven by the pre-existing OCDS/HTTP/Smoke suite (which
drives the real download paths) staying green. The CAS gate primitive itself and
the JNI `Promise.async`/`NitroModules.applicationContext` boundary remain in the
adapter and are genuinely not JVM-unit-testable (no JSI stand-in).

**Still open (and why):**
- **Node #9 give-up budget** — lives in the kit caller (`runDownloadWithRetry`/`ServiceAppUpdate`), tested in `useAppUpdate.test.ts`, not a downloader concern.
- **True cross-restart resume (#2)** — a real SIGKILL-mid-write cannot be reproduced in jest (an in-process interrupt either persists an empty manifest below the 4 MiB flush threshold, or hangs on a stalled socket). The realistic case is covered by the `seedResumeState` resume test (manifest persisted) + the OCDS-T1 intra-call resume; a faithful kill-resume needs a process-level harness.

## Android APK updater (react-native-app-update)

The APK download path (`native-modules/react-native-app-update`) shares the OCDS
core (`ConcurrentRangeDownloader`) with the bundle path, but its ~1346-line caller
(`ReactNativeAppUpdate.kt`) was **never audited and had ZERO tests**. An
adversarial audit found 4 issues; 3 were fixed and the dependency-free pure logic
was extracted + unit-tested.

**Bugs found & fixed:**
| # | Severity | SPEC | What | Fix | Commit |
|---|---|---|---|---|---|
| 1 | HIGH | §5.8 | Concurrent download invoked with **no `cancelHandle`** → `clearCache`/`clearApkCache` deleted `.segN` while 8 worker threads were still streaming into them (the cancel-then-delete resurrection race the standard forbids) | Wired a `CancelHandle` (mirror the bundle caller) + `cancel()` (`shutdownNow`+`awaitTermination`) before any `.segN` delete | `213acf0f3` |
| 2a | MEDIUM | — | Once cancel was wired, an intentional `clearCache`-cancel surfaced as a spurious update/error | Suppressed via `cancelHandle.aborted` (mirror bundle's `intentionallyCancelled`) | `24d5973cb` |
| 2b | MEDIUM | — | When `SHA256SUMS.asc` is offline, verification returns Indeterminate/Deferred (`ApkVerificationDeferredException`) — was mis-reported as update/error on every retry | Suppressed (type-based); byte-preservation untouched; Promise still rejects so the awaited JS caller still retries | `011c24a58` |

**Coverage added** (`b9caf71f9`) — extracted the dependency-free pure logic to
`AppUpdateLogic.kt` (adapter delegates, pure move); added junit + **17 JVM unit
tests**; all 10 planted mutants killed (mutation-proven):
| Extracted logic | Mutation-proven |
|---|---|
| segment-name template + `CONCURRENT_SEGMENT_COUNT == 8` | ✅ |
| `416` Content-Range total parse + `is416Complete` | ✅ |
| `206` Content-Range parse + `is206StartAligned` | ✅ |

Also wired the adapter's inline `206` parse through `parse206ContentRange` (was an
untested inline duplicate — coverage theater).

**Still device/Robolectric-ONLY (honest boundary):**
- the JNI `downloadAPK` streaming loop (`206`-resume vs `200`-full, append-to-`.partial`),
- the actual `.segN` deletion + the §5.8 cancel-race **execution**,
- promote/verify/install File-I/O

— all need a live `Context`.

**Backlog (not fixed):**
- **BUG #3** — single-flight is one process-global `AtomicBoolean`, not per-dest.
  Stricter (so it doesn't corrupt) but diverges from the bundle's per-dest registry
  and has no per-dest cancel.
- **BUG #4** — concurrent `COMPLETED` promotes to final **before** whole-file
  integrity verify; relies on JS calling `verifyAPK`. The `installAPK` TOCTOU guard
  mitigates.

---

## How to re-test after a code change

- **Touching `ConcurrentRangeDownloader.kt` / Android segment logic** → run the Android suite.
- **Touching `ReactNativeRangeDownloader.swift` / `RangeDownloadLogic.swift`** → run the SwiftPM logic suite; for behavioral changes (retry, fallback, resume, stall) also run the iOS simulator suite.
- **Touching `ReactNativeBundleUpdate.swift` download orchestration** → run the iOS simulator suite (it exercises the real caller→core path).
- **Touching `DesktopApiBundleUpdate.ts`** → run the Node e2e suite in app-monorepo.
- **Changing the OCDS standard itself** → update [`SPEC.md`](../SPEC.md) and add/adjust scenarios in every suite.
