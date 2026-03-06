# Changelog

All notable changes to this project will be documented in this file.

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
