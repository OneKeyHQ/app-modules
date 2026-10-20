# @onekeyfe/react-native-chart-webview

ChartWebview view for React Native

The native iOS and Android host can load a remote chart URL or a local asset
bundle. Its JavaScript wrapper supplies the default chart bridge script and
forwards load, error and message events to the application.

## Installation

```sh
yarn add @onekeyfe/react-native-chart-webview react-native-nitro-modules
```

## Usage

```tsx
import { ChartWebviewView } from '@onekeyfe/react-native-chart-webview';

// ...

<ChartWebviewView
  style={{ flex: 1 }}
  uri="https://tradingview.onekey.so/?theme=dark&symbol=BTC"
  reuseKey="market"
  pooled={true}
  active={true}
/>;

<ChartWebviewView
  style={{ flex: 1 }}
  localBundle="tradingview-assets"
  entry="index.html"
  paramsJson={JSON.stringify({ theme: 'dark', symbol: 'BTC' })}
/>;
```

## Source, pooling and messaging

Install the application's iOS pods and rebuild the native application after
adding this Nitro view. Give the component a visible layout size through style.

Use `uri` for remote content, or `localBundle`, `entry` and `paramsJson` for
application-provided offline assets. `paramsJson` is a serialized JSON object;
the application owns its business meaning. Android can set `assetHost` for
the offline origin; iOS serves offline content through its custom scheme.

`pooled` opts into reuse, `reuseKey` identifies the shared WebView group and
`active` coordinates ownership when hosts share that group. An unchanged source
can preserve the WebView; pooling does not interpret symbols or migrate page
state for the caller.

`onMessage` receives a string from the page. Native methods expose `postMessage`,
`reload` and `clearSnapshot` through the Nitro reference. See
[src/ChartWebview.nitro.ts](src/ChartWebview.nitro.ts) for the full prop/method
surface and [src/index.tsx](src/index.tsx) for callback wrapping.

## Contributing

Follow the repository's
[native development workflow](https://github.com/OneKeyHQ/app-modules/blob/main/docs/NATIVE_MODULE_DEVELOPMENT.md).

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
