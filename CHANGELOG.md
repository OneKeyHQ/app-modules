# Changelog

All notable changes to this project will be documented in this file.

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
