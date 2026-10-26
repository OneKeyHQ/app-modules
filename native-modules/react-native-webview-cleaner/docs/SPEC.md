# React Native WebView Cleaner contract

Status: Implemented migration of the published `@onekeyfe/react-native-webview-cleaner` legacy bridge. Source and local package checks passed; consumer Release runtime checks are pending.

## Purpose, scope, and non-goals

The package exposes the existing `clearAll()` call to React Native on iOS and Android. It requests removal of app-wide WebView website data through the platform APIs described below. It does not clear every on-disk WebView cache, distinguish wallet accounts, or add a completion result to the JavaScript API. Web is unsupported.

## Definitions and ownership boundaries

JavaScript owns only a wrapper around `NativeModules.RNWebViewCleaner`. Native code invokes system-owned WebKit or Android WebView stores. The system owns the stored website data and completion timing. On iOS and Android, the app's `main` and `bg` JavaScript heaps are independent, while their native website data stores are app-wide resources; calls from either runtime can affect the same underlying data. No caller-specific partition or cross-runtime queue is provided.

## Public API and defaults

The package default export has `clearAll(): void`. The wrapper invokes `NativeModules.RNWebViewCleaner.clearAll()` and does not return its native result. It takes no arguments, applies no filters, and has no caller-visible completion callback. The native module name remains `RNWebViewCleaner`; the package name remains `@onekeyfe/react-native-webview-cleaner`, with the existing React Native consumer alias `react-native-webview-cleaner`.

The package retains its CommonJS, ESM, TypeScript declarations, and React Native source entry points. This migration must preserve the old import and autolinking paths. No data is serialized across the bridge by this call.

## Lifecycle and concurrency

Each call starts the platform deletion work. The wrapper does not await completion, cancel a call, serialize calls, or guard concurrent calls from separate JavaScript runtimes. Callers must not assume that a later WebView use waits for deletion to finish. Repeated calls may overlap. The package retains no JavaScript cache or request state.

## Data, cache, and identity

The operation targets the default platform website data stores, without a wallet, account, tab, URL, or request key. It does not own persistent data formats or perform migrations. Clearing may affect other WebViews in the same app because the native stores are app-wide.

## Platform contract

| Platform | Existing native action | Completion visible to JavaScript |
| --- | --- | --- |
| iOS | On the main queue, `WKWebsiteDataStore.defaultDataStore` removes all website data types modified since the Unix epoch. The native promise resolves with `nil` in the completion handler. | No. The JavaScript wrapper discards the native promise. |
| Android | `CookieManager.removeAllCookies(null)`, `CookieManager.flush()`, then `WebStorage.deleteAllData()`. | No. The native method returns `void`; cookie removal may still be pending. |
| Web | Unsupported. | Not applicable. |

The Android operation does not claim to clear every WebView disk cache. Platform APIs and ordering remain as in the published package; the migration must not normalize these differences by changing the public API.

## Failure, fallback, and safety

The wrapper has no retry, fallback, or explicit error mapping. An unavailable `NativeModules.RNWebViewCleaner` can cause a synchronous JavaScript failure. Native asynchronous failures are not reported through `clearAll()`. The package does not bypass platform permissions or security checks. The system APIs determine data volume and duration; the package sets no numeric limit.

## Performance and resource budget

The wrapper performs one bridge call. Native deletion can involve system I/O; neither latency nor bytes removed are guaranteed. No measured performance claim is made. The iOS native code dispatches its WebKit call to the main queue as in the published implementation.

## Migration exception and conformance

Repository policy requires starting new packages with `yarn create:module`; that template generates a Nitro implementation. This package is an explicit legacy-bridge migration exception: the generated Nitro interface, Swift implementation, Kotlin module, C++ adapter, CMake file, `nitro.json`, and Nitro-only peer/configuration must be replaced or removed. A Nitro rewrite would change the native module name, generated ABI, and JavaScript call path without being required to fix the Android compile SDK issue.

The template-created `src/index.tsx`, `src/ReactNativeWebviewCleaner.nitro.ts`, `ios/ReactNativeWebviewCleaner.swift`, generated podspec, `android/CMakeLists.txt`, `android/gradle.properties`, C++ adapter, two Kotlin files, and `nitro.json` were removed. The package uses its original `src/index.ts`, Objective-C bridge, Android Java bridge, podspec, Babel/TypeScript build settings, and copyright license. The repository's ESLint, Git hook, and Turbo task conventions remain; the Turbo task has no Nitro output.

The migrated `src/index.ts`, `ios/RNWebViewCleaner/*`, Android Java module/package, and podspec must retain the published behavior. The only Android native build change is to use `rootProject.ext.compileSdkVersion` when defined, falling back to the old API 30 value otherwise. The existing minimum SDK, target SDK, ABI filter, and build tools declaration remain unchanged.

The standalone package's Yarn 3 `packageManager`, ignored dependency resolution, and nonexistent `example` workspace/script are omitted so this workspace uses the repository's Yarn 4 installation. Its release script follows the repository's package workflow. The newer Bob version emits the same CommonJS implementation and TypeScript declarations, adds `"use strict"` to an already strict ESM module, and updates JavaScript source maps; these changes do not add an API or native behavior. The legacy podspec still contains its historical `s.source` repository placeholder. The current consumer uses CocoaPods `:path` autolinking from the installed npm package and does not fetch that source; standalone CocoaPods source-based installation is outside this migration's verified scope.

The generated template `.gitignore`, migrated Android Gradle file, and migrated podspec had trailing blank whitespace removed to satisfy repository diff checks. Those edits do not change native code, dependency coordinates, or build settings.

The standalone Xcode project contained ignored `project.xcworkspace/contents.xcworkspacedata`, `project.xcworkspace/xcshareddata/IDEWorkspaceChecks.plist`, and `xcuserdata/cancue.xcuserdatad/xcschemes/xcschememanagement.plist` files. They are omitted from this workspace and its published tarball because none is tracked by the template policy or used by the podspec's Objective-C source selection. The original standalone repository retains them; this omission has no runtime effect.

Source and package comparison with published 1.0.0 must show the native implementation and JavaScript source byte-identical; the Android Gradle file differs by the compile SDK line and terminal whitespace, and the podspec differs only by terminal whitespace. Generated entry points and declarations must preserve `clearAll(): void`; the shipped package must include this spec linked from its README. The candidate Android AAR metadata task must pass with the consumer's compile SDK and React Native version. Full consumer Release APK/R8 and iOS archive/runtime tests are separate evidence and remain pending until the published version is installed and locked.
