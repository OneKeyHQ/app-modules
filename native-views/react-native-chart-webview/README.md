# @onekeyfe/react-native-chart-webview

ChartWebview view for React Native

## Installation

```sh
yarn add @onekeyfe/react-native-chart-webview
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
/>

<ChartWebviewView
  style={{ flex: 1 }}
  localBundle="tradingview-assets"
  entry="index.html"
  paramsJson={JSON.stringify({ theme: 'dark', symbol: 'BTC' })}
/>
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
