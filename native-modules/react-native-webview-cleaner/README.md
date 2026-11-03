# @onekeyfe/react-native-webview-cleaner

React Native WebView website data cleaner for iOS and Android. The package exposes
one method, `clearAll()`, which requests deletion through the platform's website
data APIs. It uses the existing React Native bridge module `RNWebViewCleaner`.

The [behavioral contract](docs/SPEC.md) is the authoritative reference for platform
behavior, completion limits, and migration details.

## Installation

Install the package in your React Native application using one package manager:

```sh
yarn add @onekeyfe/react-native-webview-cleaner
```

Or with npm:

```sh
npm install @onekeyfe/react-native-webview-cleaner
```

For iOS, install the application's CocoaPods dependencies from its `ios` directory
and rebuild the native app:

```sh
cd ios
pod install
```

Android also requires a native rebuild after installation. React Native autolinking
uses the installed npm package; this package does not require Nitro Modules.
Standalone CocoaPods installation from the podspec's source repository is outside
the verified installation scope; the podspec retains a historical source placeholder.

The package declares `react` and `react-native` as peer dependencies and Node.js
18 or newer as its package engine. The wildcard peer ranges do not establish
compatibility with every React Native version or architecture. See the verification
status below for the current limits of acceptance evidence.

## Usage

```ts
import WebViewCleaner from '@onekeyfe/react-native-webview-cleaner';

// Request deletion of app-wide WebView website data.
WebViewCleaner.clearAll();
// Execution continues without waiting for native deletion to finish.
```

Existing OneKey consumers that install the scoped package under the
`react-native-webview-cleaner` dependency alias can retain their existing import:

```ts
import WebViewCleaner from 'react-native-webview-cleaner';

WebViewCleaner.clearAll();
```

Use the import that matches the dependency name in your application's manifest.

## API

The default export exposes one method:

```ts
clearAll(): void;
```

`clearAll()` takes no arguments and applies no account, tab, origin, URL, or data-type
filter. It makes one call to `NativeModules.RNWebViewCleaner.clearAll()` and returns
`undefined`. There is no caller-visible completion callback, result, or event.

**`await WebViewCleaner.clearAll()` does not wait for deletion to complete.** The
JavaScript wrapper discards the native return value, including the native promise
on iOS. A subsequent WebView operation can run while deletion is still pending.

## Platform behavior

| Platform | Requested operation | Completion behavior |
| --- | --- | --- |
| iOS | Dispatch to the main queue and remove all WebKit website data types from `WKWebsiteDataStore.defaultDataStore` modified since the Unix epoch. | WebKit invokes a native completion handler and the native promise resolves with `nil`; the JavaScript wrapper discards that promise. |
| Android | Call `CookieManager.removeAllCookies(null)`, then `CookieManager.flush()`, then `WebStorage.deleteAllData()`. | The native method returns `void`. Cookie removal may still be pending. |
| Web | Unsupported. | No Web implementation or fallback is provided. |

The Android operation does not guarantee removal of every WebView disk cache.
The two platforms retain their existing platform-specific deletion behavior.

## Shared data and concurrent calls

The native website data stores are app-wide resources. Clearing them can remove
cookies and website storage used by other WebViews in the same app and can affect
their signed-in website sessions. There is no per-wallet, per-account, or per-tab
partition in this API.

The app's `main` and `bg` JavaScript runtimes have independent heaps but can operate
on the same native website data stores. Calls from either runtime can affect the
same data. The package provides no cross-runtime queue, cancellation, or
serialization; repeated calls can overlap.

Consumers own any application-level coordination around clearing. Do not treat
the return from `clearAll()` as a completion barrier for logout, account switching,
or the next WebView load. Workflows requiring confirmed completion need an API
that explicitly reports it.

## Failures and resource limits

- An unavailable `NativeModules.RNWebViewCleaner` can cause a synchronous
  JavaScript failure. Check native installation, autolinking, and the rebuilt app
  when the module is unavailable.
- Native asynchronous failures are not reported through the public `clearAll()`
  method. A JavaScript `try`/`catch` cannot confirm the outcome of deletion.
- The package provides no retries, fallback, progress reporting, or timeout.
- Platform APIs control deletion duration and data volume. There is no guaranteed
  latency, byte count, or package-defined numeric limit.
- The wrapper retains no JavaScript cache or request state. Native deletion can
  involve system I/O, and platform permissions and security checks still apply.

## Compatibility and verification status

This package preserves the published legacy bridge API, native module name,
consumer alias, and autolinking paths. It ships CommonJS, ESM, TypeScript
declarations, and a React Native source entry point.

The current contract records source and local package checks as passed. Consumer
Release runtime checks remain pending, including full Android Release APK/R8 and
iOS archive/runtime acceptance with the published version installed and locked.
These checks are distinct from source conformance; consult the
[SPEC](docs/SPEC.md) for acceptance requirements and known migration limitations.

## License and support

Licensed under the [MIT License](LICENSE). Report package issues in the
[app-modules issue tracker](https://github.com/OneKeyHQ/app-modules/issues), including
the package version, platform, React Native version, and reproduction steps.
