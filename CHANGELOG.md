# Changelog

All notable changes to this project will be documented in this file.

## [3.0.22] - 2026-04-24

### Documentation
- **background-thread / split-bundle-loader**: Correct stale `common.jsbundle` references in comments to match the actual resource name (`common.bundle`) used in code. No runtime change.

### Chores
- Bump all packages to 3.0.22.

## [3.0.21] - 2026-04-23

### Features
- **background-thread (iOS)**: Prefer OTA-installed `common.bundle` / `background.bundle` over the IPA built-in copies. Reflectively queries `BundleUpdateStore` (mirroring the SplitBundleLoader cross-framework reflection pattern, since the Swift umbrella header pulls in C++/Nitro headers that break the Clang dependency scanner) and falls back to the built-in resources only when no OTA is active. Without this, an OTA three-bundle update would push a new `common.bundle` to disk but the background runtime would keep loading the stale IPA copy and crash on moduleId mismatch with the OTA-loaded background bundle. Migrated from `app-monorepo` patch.
- **bundle-update**: Three-bundle metadata support — parse and reserve `requiresCommonBundle` and `bundleFormat` keys; when set, `validateBundleDescriptor` requires a `common.bundle` alongside `main.jsbundle.hbc` so OTA installs of the union/split-thread layout fail closed instead of silently shipping a broken main bundle that references moduleIds living in common. The Android metadata parser also now accepts boolean/numeric scalars (skipping nested objects) so manifests can carry future descriptors without breaking sha verification.
- **bundle-update**: Add `validatedCurrentBundleInfo` cache. Every bundleURL / common / main / background path getter on startup previously re-ran the full signature + sha256 pipeline; now memoized by `currentBundleVersion` and invalidated on every mutation (`clearBundle`, `resetToBuiltInBundle`, `clearAllJSBundleData`, `setCurrentUpdateBundleData`, `installBundle`) and on the no-version path.
- **bundle-update**: Fast-path entry sha256 at startup. Replace the full-tree `validateAllFilesInDir` sweep on the hot path with `validateEntryBundlesSha256`, which only hashes main + common + background (gated by metadata flags). Full-tree validation already runs at install time; per-segment integrity is now enforced at `loadSegment` time by SplitBundleLoader.
- **bundle-update (iOS)**: Expose `BundleUpdateStore.currentBundleCommonJSBundle()` so the background runtime can look up the OTA common bundle path.
- **split-bundle-loader**: Verify segment SHA-256 at `resolveSegmentPath` / `loadSegment` time. Pairs with the bundle-update fast-path startup change — since BundleUpdate no longer re-hashes the whole bundle directory on every launch, segment integrity is now enforced lazily here instead of trusting install-time validation. Hashes are streamed in 64KB chunks so a large segment doesn't pull the whole file into memory to verify it. For the three-bundle / split-thread layout, empty per-segment expected hash is a hard fail (via `BundleUpdateStore.currentBundleRequiresPerSegmentHash()`); older formats keep the back-compat skip. Migrated from `app-monorepo` patch.

### Bug Fixes
- **bundle-update**: Lazy web-embed sha256 verification + cache. Startup fast path only verifies JS entry bundles, so a tampered web-embed asset (HTML/JS/CSS) would slip through and execute inside the WebView (WebKit/Chromium SRI isn't enforced because the build pipeline doesn't yet inject integrity attributes). Walk `web-embed/**` at `getWebEmbedPath` time, gate against metadata sha256 entries (rejecting files-on-disk-not-in-metadata and metadata-entries-missing-on-disk), and cache the verified `bundleVersion` so the cost is paid once per (re)install. Cache is invalidated alongside `cachedValidatedBundleInfo` on every bundle mutation. Both platforms.
- **bundle-update (Android)**: Fail closed when `validateWebEmbedRecursive` gets `listFiles() == null` (I/O error or unreadable dir). Previously treating that as "nothing to verify" silently allowed a tampered web-embed asset whose containing directory was unlistable.
- **bundle-update (Android)**: Align `metadataRequiresCommonBundle` with iOS OR semantics. Previously an explicit `requiresCommonBundle=false` would suppress the `bundleFormat=three-bundle` signal, letting Android accept three-bundle manifests that iOS rejects (missing `common.bundle` allowed through, `common.bundle` sha256 not enforced on startup hot path). Switch the helper itself to OR semantics so `validateBundleDescriptor`, `validateEntryBundlesSha256`, and `currentBundleRequiresPerSegmentHash` all agree with iOS.
- **bundle-update**: Close `installBundle` same-version reinstall race. Invalidating the validated-bundle cache *before* the async install body allowed a concurrent `validatedCurrentBundleInfo()` reader to re-cache pre-install state. Add a second invalidate at the end of the async body on both platforms; different-version installs were already safe via the version-keyed cache.
- **bundle-update (Android)**: Use constant-time `secureCompare` in `validateEntryBundlesSha256` instead of `String.equals(..., ignoreCase = true)` to match the timing-attack guarantee of every other sha256 comparison in the file. `secureCompare(expected.lowercase(), actual.lowercase())` preserves case-insensitive semantics against uppercase-hex manifests.
- **bundle-update (iOS)**: Lowercase `bundleFormat` comparand at all three sites (`currentBundleRequiresPerSegmentHash`, `validateBundlePairCompatibility`, `validateEntryBundlesSha256`) to match Android's case-insensitive semantic. A manifest with `bundleFormat: "Three-Bundle"` previously activated three-bundle gating on Android but not on iOS, leaving iOS silently in two-bundle mode.
- **background-thread (iOS)**: Refuse IPA fallback when OTA main is active. If the OTA main bundle is loaded but the OTA common / background lookup fails (typical cause: package skew where bundle-update is older than split-bundle-loader/background-thread and doesn't expose the new `currentBundleCommonJSBundle` selector), falling back to the IPA built-in `common.bundle` / `background.bundle` would moduleId-mismatch the OTA main and crash on first `require()`. Detect "OTA main is active" via reflective lookup and return nil from `bundleURL` / `resolveBackgroundEntryBundlePath`.
- **background-thread (iOS)**: Abort `hostDidStart` when OTA common loaded but OTA background missing. Previously nil entry bundle was treated identically to the legitimate "no background bundle" case (warn and continue), so `SharedStore` / `SharedRPC` / `__setupBackgroundRPCHandler` would install against a runtime with no entry bundle — RPC silently broken instead of loudly fatal. Track OTA-active state on a new `_otaActiveAtBundleResolve` ivar (set in both OTA branches of `bundleURL` to capture the invariant "this delegate is locked to OTA territory") and return early when bg bundle path is nil AND the flag is set. Clear `_rctInstance = nil` before the abort so `registerSegmentWithId` can't call into the half-initialized runtime.

### Refactors
- **bundle-update**: Extract `"three-bundle"` string literal into a `bundleFormatThreeBundle` (Swift) / `BUNDLE_FORMAT_THREE_BUNDLE` (Kotlin) constant alongside the existing metadata-key constants. Keeps the iOS and Android sides in lockstep and prevents the class of bug where one platform gets updated to a new format token (or a typo) while the other silently keeps comparing against the old literal.

### Chores
- **split-bundle-loader / background-thread**: Peer-depend on `@onekeyfe/react-native-bundle-update` `>=3.0.21`. Both packages reflectively call `BundleUpdateStore` APIs only available in this release (`currentBundleRequiresPerSegmentHash` for segment fail-closed gating; `currentBundleCommonJSBundle` for OTA common path lookup). Without the peer dep, an app that bumps either package without bumping bundle-update silently falls through to back-compat code paths (reflection returns nil, fail-open per-segment, IPA built-in common bundle). Now surfaced as an install-time peer-dependency warning.
- Bump all packages to 3.0.21.

## [3.0.20] - 2026-04-21

### Features
- **device-utils**: Add synchronous `getAndroidChannel()` and `getInstallerPackageName()` Nitro HostObject methods. Both return closed string-union types (compiled to native enums) rather than open-ended strings so JS callers get a fixed set at the type level — `AndroidChannel`: `direct` / `google` / `huawei` / `unknown`; `InstallerPackageName`: `appStore` / `testFlight` / `other` / `playStore` / `huaweiAppGallery` / `unknown`.
- **device-utils (Android)**: `getAndroidChannel()` reflects the host app's `BuildConfig.ANDROID_CHANNEL` with a candidate-package walk (original `packageName`, its parent packages, and the Application class's package hierarchy) so the lookup stays correct under `applicationIdSuffix` or a sub-packaged Application class. All fallback paths return `direct` to match the gradle default; only a successfully-read but unrecognized string maps to `unknown`. Ships `consumer-rules.pro` with a `-keep class **.BuildConfig { ANDROID_CHANNEL }` rule so R8/minify in the consumer app cannot strip or rename the reflected class/field.
- **device-utils (Android)**: `getInstallerPackageName()` uses `PackageManager.getInstallSourceInfo()` on API 30+ (and the deprecated `getInstallerPackageName()` below), mapping `com.android.vending` → `playStore`, `com.huawei.appmarket` → `huaweiAppGallery`, any other non-null installer → `other`, and null/empty → `unknown`.
- **device-utils (iOS)**: `getInstallerPackageName()` distinguishes AppStore / TestFlight / Other using `Bundle.main.appStoreReceiptURL.lastPathComponent == "sandboxReceipt"` + the presence of `embedded.mobileprovision`, aligned with `react-native-device-info`'s contract; simulator returns `unknown`. `getAndroidChannel()` returns `unknown` on iOS for API parity.

### Chores
- Bump all packages to 3.0.20.

## [3.0.19] - 2026-04-21

### Bug Fixes
- **split-bundle-loader (iOS)**: Fix all segment loads silently returning "not found" after iOS path canonicalization. `[NSBundle resourcePath]` returns paths rooted at `/var/...` but iOS transparently resolves them to `/private/var/...`; the `hasPrefix` safety check therefore rejected every candidate. Apply `stringByStandardizingPath` to both `otaRoot` and `builtinRoot` so the comparison is apples-to-apples, and add diagnostic logs for each resolution attempt.
- **split-bundle-loader (Android)**: Fix post-upgrade crashes where an APK overwrite install (`adb install -r`, Play Store update, sideload) reused the previous build's extracted HBC segments even though Metro module IDs had drifted. Now wipe `onekey-builtin-segments/` whenever `PackageInfo.lastUpdateTime` changes, gated by double-checked locking so the wipe runs at most once per process. Called from every segment entry point (`getRuntimeBundleContext`, `resolveSegmentPath`, `loadSegment`, `extractBuiltinSegmentIfNeeded`).
- **split-bundle-loader (Android, New Architecture)**: Fix segment registration failing with "Neither CatalystInstance nor ReactHost available" on bridgeless. The old code required `hasCatalystInstance()` with a fragile reflection fallback; now uses `ReactContext.registerSegment(id, path, callback)` which RN routes correctly in both bridge and bridgeless modes.
- **background-thread (Android)**: Fix TurboModules on the background ReactHost never observing Activity state — `getCurrentActivity()` returned null, and `ActivityEventListener.onActivityResult` / `onNewIntent` / `LifecycleEventListener.onHostResume|Pause|Destroy` were never fired. Modules that depend on Activity context (file pickers, intent launchers, keyboard observers, etc.) silently no-op'd on the bg host. Add a reflection-based, allowlist-gated Activity bridge that replays the most recent Activity onto the bg ReactContext and forwards lifecycle / Activity-result events **only** to listeners whose class FQCN matches an entry registered via `BackgroundThreadManager.addBgActivityBridgeListenerClassPrefix(...)`. Non-allowlisted modules keep the pre-existing "never-resumed on bg" baseline, so no double resource registration or `requestCode` collisions with the UI host.
- **background-thread (Android, New Architecture)**: Fix `registerSegmentInBackground` throwing "Background CatalystInstance not available" in bridgeless mode. Now dispatches via `ReactContext.registerSegment(id, path, callback)` so bridge and bridgeless both work through a single code path.
- **background-thread**: Fix SIGSEGV on reload / teardown. The timer worker was started detached, so `nativeDestroy` could tear down `gTimers` / `gBgTimerExecutor` while the worker was mid-dispatch, and any pending `jsi::Function` callbacks in `gTimers` / `gPendingWork` were destroyed against the already-torn-down runtime (same failure mode as the original `SharedRPC::reset` crash). Now make the worker joinable, read the executor under the mutex, and in `nativeDestroy` stop + join the worker before clearing shared state; intentionally leak remaining `jsi::Function` / `std::function<void(jsi::Runtime&)>` captures so their destructors don't run on a dead runtime.
- **background-thread (iOS)**: Fix bg host failing to start in release builds that use split bundles. The delegate's default `entryURL` is the placeholder string `"background.bundle"`; passing that through `setJsBundleSource` prevented the delegate from taking its split-bundle fallback path (common.jsbundle + entry.jsbundle). Skip the override when `entryURL` is the placeholder.
- **cloud-fs (Android)**: Fix caller-provided `mimeType` being silently discarded by `saveFile`. The previous ternary was inverted — when the caller supplied an explicit MIME type, `actualMimeType` was set to `null` and `guessMimeType` was **only** consulted when the caller passed null. Collapse to `mimeType ?: guessMimeType(uriOrPath)` so caller input wins and guessing is the fallback.

### Features
- **background-thread**: New public API on `BackgroundThreadManager` for the Activity-bridge allowlist: `addBgActivityBridgeListenerClassPrefix(prefix)`, `setBgActivityBridgeListenerClassAllowlist(prefixes)`, `getBgActivityBridgeListenerClassAllowlist()`. The module ships an empty default — host apps must register FQCN prefixes early in `Application.onCreate` before the first Activity lifecycle callback fires.
- **split-bundle-loader**: Diagnostic logging on Android (`[resolveSeg]`, `[extractBuiltin]`, `[install-stamp]`) and iOS (`[resolveAbs]`) for triaging "segment not found" reports in production.

### Chores
- Inline former `app-monorepo/patches/@onekeyfe+react-native-background-thread+3.0.18.patch` and `@onekeyfe+react-native-split-bundle-loader+3.0.18.patch` directly into source; consumers no longer need `patch-package`.
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
