<div align="center">

# @onekeyfe/react-native-webview-cleaner

**Clear the app's WebView website data with one fire-and-forget call.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-webview-cleaner?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-webview-cleaner)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-webview-cleaner?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![NativeModule](https://img.shields.io/badge/NativeModule-4D6BFE)

</div>

Exposes a single `clearAll()` method that requests deletion of app-wide WebView
website data through the platform stores. iOS removes every `WKWebsiteDataStore`
data type modified since the Unix epoch from the default data store; Android
removes all cookies, flushes them, and deletes all `WebStorage` data. The call is
fire-and-forget — it returns `undefined`, takes no filters, and reports no
completion. There is no Web implementation.

## Features

- **One call** — `clearAll()` clears the app's default WebView stores without
  arguments, keys or per-origin bookkeeping.
- **Covers both stores** — WebKit website data types on iOS; cookies plus
  `WebStorage` on Android.
- **No retained state** — the wrapper performs one bridge call and keeps no
  JavaScript cache, queue or request state.
- **Legacy-compatible entry point** — the default export and the
  `RNWebViewCleaner` native module name are unchanged, so existing consumers and
  dependency aliases keep working.
- **No new native runtime** — the package is a classic React Native bridge
  module; it does not add a Nitro dependency.

## Installation

```sh
yarn add @onekeyfe/react-native-webview-cleaner
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking and also requires a native rebuild. The package has no Nitro or
other native peer dependency.

If an application installs the package under the legacy
`react-native-webview-cleaner` dependency alias, the alias import below still
resolves to the same module.

## Usage

```ts
import WebViewCleaner from '@onekeyfe/react-native-webview-cleaner';

// Requests deletion of app-wide WebView website data.
WebViewCleaner.clearAll();
// This returns immediately; native deletion is not awaited.
```

The application owns any coordination around clearing — for example deciding
when to run it relative to logout or the next WebView load.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| default `clearAll` | `() => void` | iOS, Android | Requests deletion of the app's default WebView website data. |

`clearAll()` has no caller-visible completion callback, result or event. The
wrapper calls `NativeModules.RNWebViewCleaner.clearAll()` and discards the native
result, including the iOS promise. See [`src/index.ts`](src/index.ts) for the
wrapper and [`docs/SPEC.md`](docs/SPEC.md) for the full behavioral contract.

If the installed dependency uses the legacy alias, import it the same way:

```ts
import WebViewCleaner from 'react-native-webview-cleaner';

WebViewCleaner.clearAll();
```

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Remove website data | ✅ all WebKit types since epoch | ✅ cookies + `WebStorage` | — |
| Filter by account, origin or data type | — | — | — |
| Completion callback or result | — | — | — |
| Await deletion completion | — | — | — |

## Caveats

> [!IMPORTANT]
> `clearAll()` returns `undefined`; `await` does not wait for deletion to finish.
> Do not treat the call as a completion barrier for logout, account switching or
> the next WebView load.

> [!WARNING]
> The native stores are app-wide. Clearing removes cookies and website storage
> used by every WebView in the same app, with no per-wallet, per-account or
> per-tab partition, and repeated calls can overlap.

> [!NOTE]
> Android's operation does not guarantee removal of every WebView disk cache, and
> cookie removal may still be pending when the method returns. Native failures are
> not reported through the public API; an unavailable native module can throw
> synchronously.

## References

- [Package spec](docs/SPEC.md)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-webview-cleaner)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
