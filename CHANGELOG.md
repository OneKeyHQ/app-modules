# Changelog

All notable changes to this project will be documented in this file.

## [3.0.35] - 2026-05-13

### Chores
- Republish all packages as 3.0.35; no code changes.

## [3.0.34] - 2026-05-13

### Features
- **aes-crypto**: Add native AES-GCM `aesGcmEncrypt` / `aesGcmDecrypt` (hex I/O, 12-byte nonce, AAD-bound, returns `ciphertext || tag`). Android via `Cipher.getInstance("AES/GCM/NoPadding")`, iOS via CryptoKit `AES.GCM` in new `AesCryptoGcm.swift`.

### Bug Fixes
- **aes-crypto**: Enforce strict 12-byte GCM nonce on both platforms — Android's `GCMParameterSpec` previously accepted arbitrary lengths and produced iOS-incompatible ciphertexts.
- **aes-crypto**: Reject empty / NaN / Infinity / fractional / negative inputs at every entry point (`encrypt`/`decrypt`/`pbkdf2`/`hmac*`/`sha*`/`randomKey`/AES-GCM `key`/`nonce`/`aad`); empty AEAD plaintext is still allowed.
- **aes-crypto (Android)**: Preserve GCM auth tag for empty plaintext (would have mismatched iOS) and reject empty ciphertext (previously silently resolved to `""`, bypassing authentication).
- **aes-crypto (iOS)**: Unify all rejection codes to `"-1"` (CBC/CTR/PBKDF2/HMAC/SHA/randomKey/UUID); previously per-method strings.
- **aes-crypto (iOS)**: `AesCryptoGcmError` conforms to `LocalizedError` so JS gets a real message instead of Cocoa's default `(... error N.)`.
- **aes-crypto (Android)**: Remove dead branches in `encryptImpl` / `decryptImpl` / GCM impls now covered by entry-level guards.

### Documentation
- **aes-crypto**: JSDoc on `aesGcmEncrypt` / `aesGcmDecrypt` documents the contract; header / podspec comments explain the ObjC++ + Swift bridging quirks.

### Chores
- Bump all packages to 3.0.34. Legacy `fromHex` `strtol` leniency tracked as follow-up.

## [3.0.33] - 2026-05-13

### Bug Fixes
- **auto-size-input**: Dispatch `focus()` / `blur()` to the UI thread — Nitro hybrid view methods can run off-main, but `becomeFirstResponder` / `resignFirstResponder` (iOS, SIGTRAP) and `requestFocus` / `InputMethodManager` (Android, `CalledFromWrongThreadException`) must run on the main thread.

### Chores
- Bump `@onekeyfe/react-native-auto-size-input` to 3.0.33.

## [3.0.32] - 2026-05-12

### Features
- **background-thread**: New `restart(mode, reason)` TurboModule — `mode='ui'` reloads only the main runtime (language/currency/devSettings), `mode='all'` reloads both (OTA install, resetData); `reason` is forwarded to RN reload listeners for attribution.
- **background-thread (iOS)**: Two-stage post-reload health check (~3s + ~6s) self-heals integration omissions; self-respawn replays the host's last entry URL (cached as `lastEntryURL`) instead of falling back to the IPA `background.bundle` to avoid moduleId mismatch under OTA. Concurrent restarts gated by a monotonic `restartGeneration` counter.
- **background-thread (Android)**: `mode='ui'` soft-reloads via `ReactHost.reload(reason)`, `mode='all'` does a process restart; `triggerProcessRestart` now returns `Boolean` and propagates intent / `SecurityException` failures to the JS promise as `BG_RESTART_ERROR`.

### Bug Fixes
- **background-thread (iOS)**: Fix `EXC_BAD_ACCESS` on main reload — SharedRPC listener kept a `jsi::Function` tied to a torn-down runtime, and `RPCRuntimeExecutor` lambdas captured `RCTInstance` strongly. Per-listener `alive` flag flipped synchronously in `restart()`; lambdas now capture `RCTInstance` weakly.
- **background-thread**: `SharedRPC::invalidate(runtimeId)` now erases the dead listener entry instead of leaving it with `alive=false`, keeping `listeners_` clean across `mode='all'` restarts that never re-install.

### Chores
- Bump `@onekeyfe/react-native-background-thread` to 3.0.32.

## [3.0.31] - 2026-05-09

### Features
- **bundle-update (iOS)**: Persist `URLSession` resume data to a `<filePath>.resume` sidecar on download failure; next attempt resumes via `downloadTask(withResumeData:)` instead of restarting from byte 0. Targets the ~64% of failures that are `NSURL -1005` / `-1001`.
- **bundle-update (iOS)**: Snapshot resume data on `didEnterBackgroundNotification` via `cancel(byProducingResumeData:)` under a `beginBackgroundTask` window, so force-quit / OOM kills don't lose progress.
- **bundle-update (Android)**: Range-based resume via `<filePath>.partial` sidecar — retry sends `Range: bytes=<offset>-`; 206 resumes, 200 restarts, 416 wipes. Rename to final filename only after SHA256 verifies.
- **bundle-update**: Per-failure SHA256 subtype tagging via thread-local stamp (`SHA256_FILE_TRUNCATED`, `SHA256_OOM`, `SHA256_IO_<class>`, …) so the previously opaque Android `verifyPackage` analytics bucket can be split. iOS swaps raise-prone `readData(ofLength:)` for throwing `read(upToCount:)`.

### Bug Fixes
- **bundle-update**: Drop inner exception text from thrown messages to prevent path leakage — iOS `verifyBundleASC` embedded `SSZipArchive` paths containing install UUID; Android `getMetadata` embedded `org.json` text with local paths. Only an `IO_<class>` tag escapes now; full descriptions still go to OneKeyLog.
- **bundle-update**: Verify-stage SHA failures propagate the subtype (`Bundle SHA256 verification failed: <reason>`) so analytics splits download-stage and verify-stage failures into matching buckets.
- **bundle-update (iOS)**: Protect `activeDownloadFilePath` under `stateQueue` so foreground/background races can't observe torn state with `isDownloading`.
- **bundle-update (iOS)**: Invalidate `URLSession` in `deinit` so the session and `DownloadDelegate` don't outlive the module on hot-reload.
- **bundle-update (Android)**: Recover a crashed-before-rename `.partial` via promote+verify when size matches `expectedSize`, instead of unconditionally deleting it.
- **bundle-update (Android)**: Recover from HTTP 416 when `Content-Range: bytes */<total>` indicates the partial is already byte-complete.
- **bundle-update (Android)**: Sanitize `update/error` payloads through `sanitizeErrorMessageForEvent` so `/data/user/...` paths never leak to JS / analytics.
- **perf-stats (Android)**: Remove the WindowManager overlay on Activity destroy to prevent View/token leak across configuration changes.

### Chores
- Bump all packages to 3.0.31.

## [3.0.28] - 2026-05-08

### Features
- **perf-stats**: Add UI FPS (`Choreographer` / `CADisplayLink`) and JS FPS (rAF, pushed via `setJsFpsHint` with 2s staleness) to `PerfSample`. Anomaly logging extends to sustained `ui_fps <= 45` and `js_fps <= 30`.
- **perf-stats**: `start` / `stop` now auto-manage the JS FPS tracker; manual `startJsFpsTracker` / `stopJsFpsTracker` remain as escape hatches.

### Bug Fixes
- **perf-stats (iOS)**: Keep overlay above modal-presented controllers — switch from a `UILabel` on the key `UIWindow` to a dedicated `windowLevel = .alert + 1` `UIWindow` subclass that forwards taps outside the label via `hitTest`.

### Chores
- Bump all packages to 3.0.28.

## [3.0.25] - 2026-05-07

### Features
- **perf-stats**: New `@onekeyfe/react-native-perf-stats` Nitro module — periodic CPU% and RSS sampler with a debug overlay (Android Kotlin/JNI, iOS Swift).
- **perf-stats**: Log sustained CPU/RSS anomalies (CPU ≥ 150% or RSS ≥ 800 MB for 5 consecutive samples) to native-logger with per-category 30s cooldown.

### Bug Fixes
- **perf-stats**: Close `start` / `stop` race that stranded the sampler — move `running=true` and handler lifecycle into one synchronized block, plus a generation token so a tick whose scheduler was stopped drops itself instead of rescheduling on a quitting handler.
- **perf-stats (Android)**: Use literal `1005` for `ABOVE_SUB_PANEL` — `TYPE_APPLICATION_ABOVE_SUB_PANEL` is `@hide` in the public SDK and fails to compile by name.

### Documentation
- **perf-stats**: Rewrite README with the real API and scoped package name.

### Chores
- Bump all packages to 3.0.25.

## [3.0.24] - 2026-04-28

### Bug Fixes
- **bundle-update (Android)**: Annotate `BundleUpdateStoreAndroid.getCurrentBundle*JSBundle` with `@JvmStatic` so external reflection callers (e.g. `SplitBundleLoaderModule`) don't NPE on null-receiver invocation — without it, fresh OTA installs silently loaded the APK common bundle and crashed on moduleId mismatch.

### Chores
- Bump all packages to 3.0.24.

## [3.0.23] - 2026-04-24

### Bug Fixes
- **bundle-update (Android)**: Rewrite the `validateWebEmbedSha256` KDoc in prose — `web-embed/**` inside `/** ... */` was lexed as a nested comment opener (Kotlin block comments nest), swallowing the rest of the file as comment body and breaking Android release builds.

### Chores
- Bump all packages to 3.0.23.

## [3.0.22] - 2026-04-24

### Documentation
- **background-thread / split-bundle-loader**: Correct stale `common.jsbundle` references in comments to match the actual resource name (`common.bundle`). No runtime change.

### Chores
- Bump all packages to 3.0.22.

## [3.0.21] - 2026-04-23

### Features
- **background-thread (iOS)**: Prefer OTA-installed `common.bundle` / `background.bundle` over IPA built-ins via reflective `BundleUpdateStore` lookup, so a three-bundle OTA doesn't moduleId-mismatch a stale IPA copy. Migrated from `app-monorepo` patch.
- **bundle-update**: Three-bundle metadata support — parse `requiresCommonBundle` and `bundleFormat`; `validateBundleDescriptor` now requires `common.bundle` when set so split-thread OTAs fail closed. Android metadata parser also accepts boolean / numeric scalars.
- **bundle-update**: Add `validatedCurrentBundleInfo` cache keyed by `currentBundleVersion`, invalidated on every mutation, so startup path getters no longer re-run the full signature + sha256 pipeline.
- **bundle-update**: Fast-path entry sha256 at startup — only hash main + common + background (gated by metadata); full-tree validation still runs at install time, per-segment integrity is now enforced at `loadSegment` time.
- **bundle-update (iOS)**: Expose `BundleUpdateStore.currentBundleCommonJSBundle()` for background-runtime lookup.
- **split-bundle-loader**: Verify segment SHA-256 at `resolveSegmentPath` / `loadSegment` time, streamed in 64KB chunks. Empty per-segment hash is a hard fail under the three-bundle layout; older formats keep the back-compat skip. Migrated from `app-monorepo` patch.

### Bug Fixes
- **bundle-update**: Lazy web-embed sha256 verification + cache — walk `web-embed/**` at `getWebEmbedPath` time, reject files-not-in-metadata and metadata-without-files, cache by `bundleVersion`. Both platforms.
- **bundle-update (Android)**: Fail closed when `validateWebEmbedRecursive` gets `listFiles() == null`, instead of silently allowing a tampered/unlistable dir.
- **bundle-update (Android)**: `metadataRequiresCommonBundle` now uses OR semantics so `bundleFormat=three-bundle` is honored even when `requiresCommonBundle=false`, matching iOS.
- **bundle-update**: Close `installBundle` same-version reinstall race — also invalidate the validated-bundle cache at the end of the async body, so a concurrent reader can't re-cache pre-install state.
- **bundle-update (Android)**: Use constant-time `secureCompare` in `validateEntryBundlesSha256`, matching every other sha256 comparison in the file.
- **bundle-update (iOS)**: Lowercase the `bundleFormat` comparand at all three sites for parity with Android's case-insensitive semantic.
- **background-thread (iOS)**: Refuse IPA fallback when OTA main is active — if OTA common / background lookup fails (typical: bundle-update older than the consumer), don't fall back to IPA built-ins that would moduleId-mismatch the OTA main.
- **background-thread (iOS)**: Abort `hostDidStart` when OTA common loaded but OTA background missing, instead of installing SharedStore / SharedRPC against a runtime with no entry bundle. Tracked via new `_otaActiveAtBundleResolve` ivar.

### Refactors
- **bundle-update**: Extract `"three-bundle"` into a constant on both platforms (`bundleFormatThreeBundle` / `BUNDLE_FORMAT_THREE_BUNDLE`) so iOS and Android stay in lockstep.

### Chores
- **split-bundle-loader / background-thread**: Peer-depend on `@onekeyfe/react-native-bundle-update` `>=3.0.21` — both now reflectively call new `BundleUpdateStore` APIs.
- Bump all packages to 3.0.21.

## [3.0.20] - 2026-04-21

### Features
- **device-utils**: Add synchronous `getAndroidChannel()` and `getInstallerPackageName()` Nitro HostObject methods, both typed as closed string unions for compile-time safety (`AndroidChannel`: `direct` / `google` / `huawei` / `unknown`; `InstallerPackageName`: `appStore` / `testFlight` / `other` / `playStore` / `huaweiAppGallery` / `unknown`).
- **device-utils (Android)**: `getAndroidChannel()` reflects `BuildConfig.ANDROID_CHANNEL` with a candidate-package walk so `applicationIdSuffix` / sub-packaged Application classes resolve correctly; ships `consumer-rules.pro` to keep the field through R8.
- **device-utils (Android)**: `getInstallerPackageName()` uses `getInstallSourceInfo()` on API 30+ (deprecated `getInstallerPackageName()` below), mapping `com.android.vending` → `playStore`, `com.huawei.appmarket` → `huaweiAppGallery`, other → `other`, null → `unknown`.
- **device-utils (iOS)**: `getInstallerPackageName()` distinguishes AppStore / TestFlight / Other via `appStoreReceiptURL.lastPathComponent` + `embedded.mobileprovision`; simulator returns `unknown`. `getAndroidChannel()` returns `unknown` for API parity.

### Chores
- Bump all packages to 3.0.20.

## [3.0.19] - 2026-04-21

### Bug Fixes
- **split-bundle-loader (iOS)**: Apply `stringByStandardizingPath` to `otaRoot` / `builtinRoot` so the `hasPrefix` safety check survives the `/var → /private/var` canonicalization that was silently rejecting every candidate. Add diagnostic logs.
- **split-bundle-loader (Android)**: Wipe `onekey-builtin-segments/` whenever `PackageInfo.lastUpdateTime` changes (double-checked locking, called from every segment entry point) so APK overwrite installs don't reuse the previous build's HBC segments against drifted Metro module IDs.
- **split-bundle-loader (Android, NewArch)**: Switch segment registration from `hasCatalystInstance` + reflection fallback to `ReactContext.registerSegment(id, path, callback)` so bridgeless works.
- **background-thread (Android)**: Add a reflection-based, allowlist-gated Activity bridge — replays the current Activity onto the bg ReactContext and forwards lifecycle / Activity-result events only to listeners whose FQCN matches a registered prefix; non-allowlisted modules keep the prior baseline so no resource collisions with the UI host.
- **background-thread (Android, NewArch)**: `registerSegmentInBackground` now uses `ReactContext.registerSegment` so bridge and bridgeless share one code path.
- **background-thread**: Fix SIGSEGV on reload / teardown — make the timer worker joinable, stop+join in `nativeDestroy` before clearing shared state, intentionally leak remaining `jsi::Function` / `std::function` captures so destructors don't run on a dead runtime.
- **background-thread (iOS)**: Skip the `entryURL` override when it's the placeholder `"background.bundle"`, so release builds taking the split-bundle path actually fall through to common + entry.
- **cloud-fs (Android)**: Collapse the inverted `saveFile` ternary to `mimeType ?: guessMimeType(uriOrPath)` so caller-supplied MIME types win and guessing is the fallback.

### Features
- **background-thread**: New allowlist API on `BackgroundThreadManager` — `addBgActivityBridgeListenerClassPrefix(prefix)`, `setBgActivityBridgeListenerClassAllowlist(prefixes)`, `getBgActivityBridgeListenerClassAllowlist()`. Empty by default; host apps must register FQCN prefixes early in `Application.onCreate`.
- **split-bundle-loader**: Diagnostic logging (`[resolveSeg]` / `[extractBuiltin]` / `[install-stamp]` on Android, `[resolveAbs]` on iOS) for production "segment not found" triage.

### Chores
- Inline former `app-monorepo` patches (`@onekeyfe+react-native-background-thread+3.0.18.patch`, `@onekeyfe+react-native-split-bundle-loader+3.0.18.patch`) into source; no `patch-package` needed.
- Bump all packages to 3.0.19.

## [3.0.18] - 2026-04-10

### Features
- **background-thread**: Drain the Hermes microtask queue after every `nativeExecuteWork` so `Promise.then` / `async-await` continuations actually run on the background runtime (RN 0.74+ requires explicit `drainMicrotasks()` — without it, all awaits hang in bg).
- **background-thread**: Add cross-runtime timer primitives in `cpp-adapter.cpp` (timer worker thread, pending work queue, JSI-safe scheduling) underpinning split-bundle + bg-host `setTimeout`/`Promise` behaviour.

### Chores
- Bump all packages to 3.0.18.

## [3.0.17] - 2026-04-10

### Bug Fixes
- **tcp-socket / zip-archive / network-info / ping / async-storage / cloud-fs / dns-lookup**: Align Android TurboModule class names with their TS spec filenames so codegen resolves the native modules correctly.

### Chores
- Bump all packages to 3.0.17.

## [3.0.16] - 2026-04-10

### Bug Fixes
- **async-storage (Android)**: Correct codegen class name in `RNCAsyncStorageModule.kt`.

### Chores
- Bump all packages to 3.0.16.

## [3.0.15] - 2026-04-09

### Features
- **cloud-fs**: Align types and native implementations with the upstream source repo — replace `Object` params with proper TS types across the Spec, port `DriveServiceHelper` and the full `RNCloudFsModule` from Java to a Kotlin TurboModule, add Android Google Drive methods (`loginIfNeeded`, `logout`, `getGoogleDriveDocument`, `getCurrentlySignedInUserData`), add iOS stubs for Android-only methods, fix iOS `syncCloud` to return a boolean, re-add `createFile` to the Spec, and wire the Google Drive dependencies into Android `build.gradle`.

### Bug Fixes
- **async-storage (web)**: Resolve web type errors by adding `DOM` lib to tsconfig and declaring types for `merge-options`.

### Chores
- Patch bump workspaces and fix async-storage module files.
- Bump all packages to 3.0.15.

## [3.0.13] - 2026-04-08

### Features
- **async-storage**: Add web implementation (`NativeAsyncStorage`) backed by `localStorage`.

### Chores
- Bump all packages to 3.0.13.

## [3.0.11] - 2026-04-03

### Features
- **async-storage**: Add `AsyncStorageStatic` compatibility layer for legacy call sites.

### Bug Fixes
- **ping / pbkdf2 / network-info**: Fix compilation errors.
- **aes-crypto**: Sync patch changes — use Hex encoding for all I/O.
- **dns-lookup (iOS)**: Add `CFDataRef` cast in `DnsLookup.mm` to fix compilation.
- Align Android implementations with upstream originals.
- iOS compilation fixes verified with local build.

### Chores
- `gitignore` the `lib/` build output and exclude `.map` files from npm publish.
- Bump all packages through 3.0.11.

## [3.0.4] - 2026-04-03

### Bug Fixes
- **split-bundle-loader**: Fix stale reflection class name for `BundleUpdateStore` in Android `getOtaBundlePath()` — updated from `expo.modules.onekeybundleupdate.BundleUpdateStore` to `com.margelo.nitro.reactnativebundleupdate.BundleUpdateStoreAndroid`
- **native-logger**: Fix dedup logic suppressing error logs — comparison now includes level, tag, and message instead of message-only
- **background-thread**: Fix JNI GlobalRef leak on each `nativeInstallSharedBridge` call — wrap in `shared_ptr` with custom deleter
- **background-thread**: Fix `SharedRPC::reset()` crash from destroying `jsi::Function` on wrong thread — use intentional leak pattern
- **background-thread**: Fix `nativeDestroy` not resetting `SharedStore`, leaving stale data across restarts
- Correct codegen class names to match TS spec file names

### Chores
- Align all package versions to 3.x line (cloud-fs cannot use 1.x since npm already has 2.6.5)
- Bump all packages to 3.0.4

## [1.1.59] - 2026-04-03

### Bug Fixes
- **tcp-socket**: Correct header import to match codegenConfig name

### Chores
- Bump all packages to 1.1.59

## [1.1.58] - 2026-04-03

### Bug Fixes
- **cloud-fs**: Set version to 3.0.0 (npm already has 2.6.5, cannot publish lower)

### Chores
- Bump all packages to 1.1.58

## [1.1.57] - 2026-04-03

### Bug Fixes
- Add missing release scripts for cloud-fs, ping, zip-archive

### Chores
- Bump all packages to 1.1.57

## [1.1.56] - 2026-04-03

### Features
- **aes-crypto / async-storage / cloud-fs / dns-lookup / network-info / ping / tcp-socket / zip-archive**: Add Android TurboModule implementations for legacy bridge module replacements
- **tcp-socket**: Fix type definitions

### Chores
- Bump all packages to 1.1.56

## [1.1.55] - 2026-04-03

### Features
- **aes-crypto / async-storage / cloud-fs / dns-lookup / network-info / ping / tcp-socket / zip-archive**: Add TurboModule replacements for legacy React Native bridge modules (iOS + JS)

### Chores
- Bump all packages to 1.1.55

## [1.1.54] - 2026-04-02

### Chores
- Bump all packages to 1.1.54

## [1.1.53] - 2026-04-02

### Features
- **split-bundle-loader**: Add split-bundle timing instrumentation and update PGP public key
- **split-bundle-loader**: Add comprehensive timing logs for three-bundle split verification

### Chores
- Bump all packages to 1.1.53

## [1.1.52] - 2026-04-02

### Features
- **background-thread**: Add split-bundle common+entry loading strategy for background runtime

### Chores
- Bump all packages to 1.1.52

## [1.1.51] - 2026-04-01

### Features
- **split-bundle-loader**: Add `resolveSegmentPath` API and path traversal protection

### Bug Fixes
- **split-bundle-loader**: Resolve Android `registerSegmentInBackground` race condition
- **split-bundle-loader**: Enhance bridgeless support and robustness improvements

### Chores
- Bump all packages to 1.1.51

## [1.1.49] - 2026-04-01

### Features
- **split-bundle-loader**: Add `react-native-split-bundle-loader` TurboModule with `getRuntimeBundleContext` and `loadSegment` APIs
- **split-bundle-loader**: Expose `loadSegmentInBackground` from TurboModule API
- **bundle-update**: Add `registerSegmentInBackground` for late HBC segment loading

### Chores
- Bump all packages to 1.1.49

## [1.1.48] - 2026-03-31

### Features
- **bundle-update**: Support background bundle pair bootstrap — add `getBackgroundJsBundlePath`, metadata validation for `requiresBackgroundBundle` and `backgroundProtocolVersion`, and bundle pair compatibility checks

### Chores
- Bump all packages to 1.1.48

## [1.1.47] - 2026-03-31

### Features
- **background-thread**: Add SharedBridge JSI HostObject for cross-runtime data transfer between main and background JS runtimes
- **background-thread**: Implement Android background runtime with second ReactHost and SharedBridge
- **background-thread**: Replace SharedBridge with SharedStore + SharedRPC architecture
- **background-thread**: Add onWrite cross-runtime notification, remove legacy messaging
- **native-logger**: Add dedup for identical consecutive log messages

### Bug Fixes
- **background-thread**: Stabilize background thread runtime initialization
- **background-thread**: Initialize Android shared bridge at app startup
- **shared-rpc**: Rename `RuntimeExecutor` to `RPCRuntimeExecutor` to avoid React Native conflict
- **shared-rpc**: Prevent crash on JS reload by deduplicating listeners with runtimeId
- **shared-rpc**: Leak stale `jsi::Function` callback on reload to prevent crash

### Chores
- Bump all packages to 1.1.47

## [1.1.46] - 2026-03-19

### Bug Fixes
- **bundle-update**: Use `scheduledEnvBuildNumber` from task instead of stored `getNativeBuildNumber()` for buildNumber change detection in pre-launch pending task processing

### Chores
- Bump all native modules and views to 1.1.46

## [1.1.45] - 2026-03-19

### Bug Fixes
- **bundle-update**: Use `scheduledEnvBuildNumber` from task instead of stored `getNativeBuildNumber()` for buildNumber change detection in pre-launch pending task processing, so the check works even without a prior successful bundle install
- **react-native-tab-view**: Add `delayedFreeze` prop to control freeze/unfreeze delay on tab switch; defaults to immediate freeze for better iPad sidebar switching

### Chores
- Bump all native modules and views to 1.1.45

## [1.1.44] - 2026-03-19

### Bug Fixes
- **react-native-tab-view (iOS)**: Force iPad tab bar to bottom by overriding `horizontalSizeClass` to `.compact` on iPad (iOS 18+), preventing `UITabBarController` from placing tabs at the top

### Chores
- **react-native-tab-view (iOS)**: Remove verbose debug logging (KVO observers, prop change logs, layout logs, delegate proxy logs)
- Bump all native modules and views to 1.1.44

## [1.1.43] - 2026-03-18

### Features
- **bundle-update**: Add native-layer pre-launch pending task processing for iOS/Android — process pending bundle-switch tasks before JS runtime starts

### Bug Fixes
- **bundle-update**: Harden pre-launch pending task with entry file existence check and synchronous writes (commit instead of apply on Android)

### Chores
- Bump all native modules and views to 1.1.43

## [1.1.42] - 2026-03-18

### Features
- **bundle-update**: Add `clearDownload()` method that only clears the download cache directory
- **bundle-update**: Fix `clearBundle()` to also clear the installed bundle directory, aligning behavior across desktop/iOS/Android

### Chores
- Bump all native modules and views to 1.1.42

## [1.1.41] - 2026-03-17

### Features
- **bundle-update**: Add buildNumber change detection in `getJsBundlePath()` — clears hot-update bundle data and falls back to builtin JS bundle when native buildNumber changes
- **bundle-update**: Add `getNativeBuildNumber()` and `getBuiltinBundleVersion()` APIs on both iOS and Android

### Bug Fixes
- **bundle-update (iOS)**: Guard against empty stored buildNumber to match Android behavior in build number change detection
- **bundle-update (Android)**: Use `get().toString()` instead of `getString()` in `getBuiltinBundleVersion()` to handle numeric meta-data values that AAPT2 stores as Integer

### Chores
- Bump all native modules and views to 1.1.41

## [1.1.39] - 2026-03-17

### Features
- **device-utils**: Add boot recovery APIs and test buttons to DeviceUtilsTestPage

### Documentation
- Add acknowledgment READMEs for forked upstream projects: react-native-tab-view, TOCropViewController, react-native-get-random-values

### Chores
- Bump all native modules and views to 1.1.39

## [1.1.38] - 2026-03-14

### Features
- **scroll-guard**: Add new `@onekeyfe/react-native-scroll-guard` native view module that prevents parent scrollable containers (PagerView/ViewPager2) from intercepting child scroll gestures
- **scroll-guard**: Support `direction` prop with horizontal, vertical, and both modes

### Bug Fixes
- **scroll-guard (Android)**: Use `ViewGroupManager` instead of nitrogen-generated `SimpleViewManager` to properly support child views (fixes `IViewGroupManager` ClassCastException)
- **scroll-guard (iOS)**: Improve gesture blocking reliability

### Chores
- Add `nitrogen/` to `.gitignore` and remove tracked nitrogen files
- Bump all native modules and views to 1.1.38

## [1.1.37] - 2026-03-12

### Features
- **bundle-update**: Add `resetToBuiltInBundle()` API to clear the current bundle version preference, reverting to built-in JS bundle on next restart

### Chores
- Bump all native modules and views to 1.1.37

## [1.1.36] - 2026-03-10

### Features
- **auto-size-input**: Add `contentCentered` prop to center prefix/input/suffix as one visual group in single-line mode

### Bug Fixes
- **auto-size-input (Android)**: Improve baseline centering and centered-layout width calculations for single-line input
- **auto-size-input (iOS)**: Align auto-width sizing behavior with Android so content width and suffix positioning stay consistent

### Chores
- Bump all native modules and views to 1.1.36

## [1.1.35] - 2026-03-10

### Features
- **pager-view**: Add local `@onekeyfe/react-native-pager-view` package in `native-views` with iOS and Android support
- **example**: Add PagerView test page and route integration, including nested pager demos

### Bug Fixes
- **pager-view**: Fix layout metrics/child-view guards and scope refresh-layout callback to the host instance
- **tab-view**: Guard invalid route keys in tab press/long-press callbacks and safely resync selected tab on Android

### Chores
- Bump all native modules and views to 1.1.35

## [1.1.34] - 2026-03-09

### Bug Fixes
- **auto-size-input**: Use `setRawInputType` on Android so the IME shows the correct keyboard layout without restricting accepted characters; JS-side sanitization handles character filtering
- **auto-size-input**: Skip `autoCorrect` and `autoCapitalize` mutations on number/phone input classes to avoid stripping decimal/signed flags and installing a restrictive KeyListener

## [1.1.33] - 2026-03-09

### Bug Fixes
- **tab-view**: Remove `interfaceOnly` option from `codegenNativeComponent` to fix Fabric component registration

## [1.1.32] - 2026-03-09

### Features
- **tab-view**: Add new `@onekeyfe/react-native-tab-view` Fabric Native Component with native iOS tab bar (UITabBarController) and Android bottom navigation (BottomNavigationView)
- **tab-view**: Add `ignoreBottomInsets` prop for controlling safe-area inset behavior
- **tab-view**: Add TabView settings page for Android with shared store
- **tab-view**: Add OneKeyLog debug logging for tab bar visibility debugging
- **tab-view**: Migrate to Fabric Native Component (New Architecture), remove Paper (old arch) code on iOS
- **example**: Add TabView test page and migrate example app routing to `@react-navigation/native`
- **example**: Add floating A-Z alphabet sidebar, emoji icons, and compact iOS Settings-style module list to home screen

### Bug Fixes
- **tab-view**: Prevent React child views from covering tab bar
- **tab-view**: Prevent Fabric from stealing tab childViews, fix scroll and layout issues
- **tab-view**: Forward events from Swift container to Fabric EventEmitter on iOS
- **tab-view**: Resolve Kotlin compilation errors in RCTTabViewManager
- **tab-view**: Wrap BottomNavigationView context with MaterialComponents theme
- **tab-view**: Remove bridging header for framework target compatibility
- **tab-view**: Use ObjC runtime bridge to call OneKeyLog, avoid Nitro C++ header import
- **tab-view**: Resolve iOS Fabric ComponentView build errors
- **tab-view**: Restore `#ifdef RCT_NEW_ARCH_ENABLED` in .h files for Swift module compatibility

### Refactors
- **tab-view**: Migrate from Nitro HybridView to Fabric ViewManager, then to Fabric Native Component

### Chores
- Bump all native modules and views to 1.1.32

## [1.1.30] - 2026-03-06

### Features
- **bundle-update / app-update**: Add synchronous `isSkipGpgVerificationAllowed` API to expose whether build-time skip-GPG capability is enabled

## [1.1.29] - 2026-03-06

### Bug Fixes
- **auto-size-input**: Re-layout immediately in `contentAutoWidth` mode as text changes, and shrink font when max width is reached so content stays visible

## [1.1.28] - 2026-03-05

### Features
- **auto-size-input**: Add new `@onekeyfe/react-native-auto-size-input` native view module with font auto-scaling, prefix/suffix support, multiline support, and example page
- **auto-size-input**: Add `showBorder`, `inputBackgroundColor`, and `contentAutoWidth` props; make composed prefix/input/suffix area tappable to focus input
- **bundle-update / app-update**: Add `testVerification` and `testSkipVerification` testing APIs
- **native-logger**: Add level-based token bucket rate limiting for log writes

### Bug Fixes
- **auto-size-input**: Fix iOS build issue (delegate/`NSObjectProtocol` conformance), fix layout-loop behavior, and align callback typing on test page
- **native-logger**: Harden log rolling behavior and move rate limiting to low-level logger implementation

## [1.1.27] - 2026-03-04

### Features
- **bundle-update / app-update**: Gate skip-GPG logic with build-time env var `ONEKEY_ALLOW_SKIP_GPG_VERIFICATION` — code paths are compiled out (iOS) or short-circuited by immutable constant (Android) when flag is unset

### Bug Fixes
- **bundle-update / app-update**: Treat empty string env var as disabled for `ALLOW_SKIP_GPG_VERIFICATION`

### Chores
- Bump all native modules and views to 1.1.27

## [1.1.26] - 2026-03-04

### Features
- **bundle-update**: Add synchronous `getJsBundlePath`, rename async version to `getJsBundlePathAsync`

## [1.1.24] - 2026-03-04

### Bug Fixes
- Address Copilot review — stack traces, import Security, FileProvider authority

### Chores
- Bump native modules and views version to 1.1.24

## [1.1.23] - 2026-03-04

### Bug Fixes
- **bundle-update / app-update**: Address PR review security and robustness issues
- **bundle-update / app-update**: Use `onekey-app-dev-setting` MMKV instance and require dual check for GPG skip

### Chores
- Bump native modules and views version to 1.1.23

## [1.1.22] - 2026-03-03

### Features
- **bundle-update**: Migrate signature storage from SharedPreferences/UserDefaults to file-based storage
- **bundle-update**: Add `filePath` field to `AscFileInfo` in `listAscFiles` API
- **app-update**: Add Download ASC and Verify ASC steps to AppUpdate pipeline
- **app-update**: Improve download with sandbox path fix, error logging, and APK cache verification
- **bundle-update / app-update**: Add comprehensive logging to native modules
- **bundle-update**: Add download progress logging on both iOS and Android

### Bug Fixes
- **bundle-update**: Expose `BundleUpdateStore` to ObjC runtime
- **bundle-update**: Fix iOS build errors (MMKV and SSZipArchive API)
- **bundle-update / app-update**: Fix Android build errors across native modules
- **app-update**: Fix download progress events and prevent duplicate downloads in native layer
- **app-update**: Fix remaining `params.filePath` reference in `installAPK`
- **app-update**: Skip `packageName` and certificate checks in debug builds, still log results
- **app-update**: Add FileProvider config for APK install intent
- **app-update**: Add BouncyCastle provider and fix conflict with Android built-in BC
- **bundle-update**: Add diagnostic logging for bundle download file creation
- **bundle-update**: Add detailed diagnostic logging to `getJsBundlePath` on both platforms
- **lite-card**: Add BuildConfig import to LogUtil
- **native-logger**: Pass archived path to `super.didArchiveLogFile` to prevent crash
- Use host app `FLAG_DEBUGGABLE` instead of library `BuildConfig.DEBUG`

### Refactors
- **app-update**: Remove `filePath` from AppUpdate API, derive from `downloadUrl`
- **app-update**: Unify AppUpdate event types with `update/` prefix

## [1.1.21] - 2026-03-03

### Features
- **device-utils**: Merge `react-native-webview-checker` into `react-native-device-utils`
- **device-utils**: Add WebView & Play Services availability checks
- **device-utils**: Add `saveDeviceToken` and make `exitApp` a no-op on iOS
- **splash-screen**: Implement Android legacy splash screen with `SplashScreenBridge`
- **get-random-values**: Add logging for invalid `byteLength`
- **bundle-update / app-update**: Replace debug-mode GPG skip with MMKV DevSettings toggle

## [1.1.20] - 2026-02-28

### Features
- **native-logger**: Add `react-native-native-logger` module with file-based logging, log rotation, and CocoaLumberjack/Timber backends
- **native-logger**: Add startup log in native layer for iOS and Android
- **native-logger**: Add timestamp format for log writes and copy button for log dir
- Integrate `OneKeyLog` into all native modules

### Bug Fixes
- **native-logger**: Auto-init OneKeyLog via ContentProvider before `Application.onCreate`
- **native-logger**: Use correct autolinked Gradle project name
- **native-logger**: Security hardening — remove key names from keychain logs
- **lite-card**: Migrate logging to NativeLogger, remove sensitive data from logs
- **background-thread**: Migrate logging to NativeLogger, replace `@import` with dynamic dispatch
- **bundle-update**: Add missing `deepLink` parameter to `LaunchOptions` initializer

### Chores
- Bump version to 1.1.20

## [1.1.19] - 2026-02-24

### Bug Fixes
- **device-utils**: Improve `setUserInterfaceStyle` reliability and code quality
- **device-utils (Android)**: Prevent `ConcurrentModificationException` in foldable device listener
- **device-utils (Android)**: Add null check in `setTopActivity` to prevent `NullPointerException`
- **device-utils (Android)**: Add synchronized blocks to prevent race conditions

## [1.1.18] - 2026-02-03

### Features
- **device-utils**: Add `setUserInterfaceStyle` API with local persistence for dark/light mode control
- **device-utils**: Add comprehensive foldable device detection with manufacturer-specific caching
- **device-utils**: Expand foldable device list from Google Play supported devices
- **cloud-kit**: Correct `fetchRecord` return type on both iOS and Android

### Chores
- Upgrade `react-native-nitro-modules` to 0.33.2

## [1.1.17 and earlier] - 2025-12-11 to 2026-01-29

### Features
- **lite-card**: Initial OneKey Lite NFC card module with full card management (read/write mnemonic, change PIN, reset, get card info)
- **lite-card**: Refactor to Turbo Module architecture
- **check-biometric-auth-changed**: Support biometric authentication change detection on iOS and Android
- **cloud-kit**: Add CloudKit/iCloud module for record CRUD operations
- **keychain-module**: Add secure keychain storage module (iOS Keychain / Android Keystore)
- **background-thread**: Add background JavaScript thread execution module
- **get-random-values**: Add cryptographic random values module (Nitro Module)
- **device-utils**: Add device utilities module — system info, clipboard, haptics, locale, and more
- **skeleton**: Add native skeleton loading view component with shimmer animation

### Bug Fixes
- **skeleton**: Fix `EXC_BAD_ACCESS` crash on iOS
- **skeleton**: Improve memory safety and cleanup in Skeleton component
- **skeleton**: Fix memory leak
- **cloud-kit**: Fix native type errors in CloudKitModule

### Chores
- Upgrade project structure to monorepo with workspaces
- Create CI publish workflow
