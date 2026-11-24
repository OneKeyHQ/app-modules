<div align="center">

# @onekeyfe/react-native-chart-webview

**A thin native WebView host for OneKey charts: offline bundles, a document-start message bridge and optional WebView pooling.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-chart-webview?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-chart-webview)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-chart-webview?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Loads either a remote chart URL or an app-bundled offline chart folder into a
native WebView, installs the shared bridge script before any page JavaScript
runs, and forwards page messages to the application. Hosts sharing a `reuseKey`
can pool one backing WebView and reparent it across screens without reloading.
It is deliberately a transport host, not a general-purpose browser: chart
protocol, symbol routing and data fetching belong to the application.

## Features

- **Two source modes** — a remote `uri`, or `localBundle` + `entry` +
  `paramsJson` served from a secure virtual origin instead of `file://`.
- **Document-start bridge** — the shared `CHART_BRIDGE_JS` wires `$private`,
  `ReactNativeWebView` and scoped `message` events into a single native hook,
  and blocks the load when no bridge is available.
- **Reload-free switching** — an unchanged effective URL is not reloaded, so
  a stable source plus application messages switches symbols without a flash.
- **Pooled hosts** — `pooled` with a non-empty `reuseKey` shares one WebView;
  `active` arbitrates ownership and a snapshot masks the reparent frame.
- **Raw message pipe** — `onMessage` delivers page strings and
  `postMessage()` sends application strings back into the page.
- **Imperative control** — `reload()` and `clearSnapshot()` are available on the
  view reference.

## Installation

```sh
yarn add @onekeyfe/react-native-chart-webview react-native-nitro-modules
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. Install the app's iOS pods and rebuild the native application
after adding the package; Android links through autolinking. There is no Web
implementation.

## Usage

```tsx
import { ChartWebviewView } from '@onekeyfe/react-native-chart-webview';

<ChartWebviewView
  style={{ flex: 1 }}
  localBundle="tradingview-assets"
  entry="index.html"
  paramsJson={JSON.stringify({ theme: 'dark', locale: 'zh-CN' })}
  onLoadEnd={() => console.log('chart loaded')}
  onMessage={(message) => console.log(message)}
/>;
```

Give the view a visible size through `style`, and memoize the source object and
callback props: the native host re-applies props it receives, and a re-render
loop (for example one triggered by `onLoadEnd`) can otherwise re-initialise the
chart.

## API

| Prop / method | Type | Default | Description | Platforms |
| --- | --- | --- | --- | :-: |
| `uri` | `string` | — | Remote URL; when non-empty it wins over the offline props. | iOS, Android |
| `localBundle` | `string` | — | App-bundled folder served as the offline chart. | iOS, Android |
| `entry` | `string` | `index.html` | Entry document inside the bundle. | iOS, Android |
| `paramsJson` | `string` | — | JSON object string encoded as the entry query string. | iOS, Android |
| `assetHost` | `string` | `appassets.androidplatform.net` | Bare hostname for the Android offline origin; ignored on iOS. | Android |
| `bridgeScript` | `string` | `CHART_BRIDGE_JS` | Document-start script; the JS wrapper supplies the shared bridge. | iOS, Android |
| `reuseKey` | `string` | — | Pool identity; hosts with the same key share one WebView. | iOS, Android |
| `pooled` | `boolean` | `false` | Opt into the shared backing WebView. | iOS, Android |
| `active` | `boolean` | treated as active | Names the visible owner among pooled hosts. | iOS, Android |
| `webviewDebuggingEnabled` | `boolean` | dev-build default | Request WebView inspection. | iOS, Android |
| `onMessage` | `(message: string) => void` | — | Raw page-to-app message strings. | iOS, Android |
| `onLoadEnd` | `() => void` | — | Top-level page finished loading. | iOS, Android |
| `onError` | `(message: string) => void` | — | Invalid URL, load failure or missing bridge. | iOS, Android |
| `postMessage(message)` | `void` | — | Sends a JSON string into the page via `window.postMessage`. | iOS, Android |
| `reload()` | `void` | — | Reloads the current backing WebView. | iOS, Android |
| `clearSnapshot()` | `void` | — | Drops the cached reparent frame and any visible overlay. | iOS, Android |

Reach the methods through the `hybridRef` prop: the wrapper accepts a plain
`(ref) => void` callback and returns the live Nitro view object. `CHART_BRIDGE_JS`
is exported for tests or controlled experiments. See
[`SPEC.md`](SPEC.md) for the full transport contract and
[`src/ChartWebview.nitro.ts`](src/ChartWebview.nitro.ts) for the interface.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Remote URL mode | ✅ | ✅ | — |
| Offline bundle mode | ✅ `onekey-chart://` | ✅ HTTPS asset origin | — |
| Document-start bridge | ✅ | ✅ | — |
| Pooled WebView reuse and snapshots | ✅ | ✅ | — |
| `assetHost` offline origin override | — | ✅ | — |
| `webviewDebuggingEnabled` | ✅ iOS 16.4+ | ✅ process-wide | — |

## Caveats

> [!WARNING]
> Do not treat this as a general-purpose WebView. With the default privileged
> bridge, load only trusted OneKey chart pages or the app-bundled chart bundle.

> [!IMPORTANT]
> `assetHost` is Android-only, and `webviewDebuggingEnabled` on Android enables
> inspection process-wide: it cannot be turned off again until the process
> restarts.

> [!NOTE]
> For offline bundles, `paramsJson` is environment bootstrap only: theme, locale,
> platform and similar values. Business state such as the symbol belongs to
> application messages, because a bundled page ignores business query params.

## References

- [Chart WebView standard](SPEC.md)
- [Example page](../../example/react-native/pages/ChartWebViewTestPage.tsx)
- [Pooling example](../../example/react-native/pages/ChartSingletonTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
