# @onekeyfe/react-native-perp-depth-bar

PerpDepthBar view for React Native

Native iOS and Android views for an order-book depth column and a bid/ask ratio
track. Each view owns native drawing and animation; the application provides
formatted prices, sizes and percentages.

## Installation

```sh
yarn add @onekeyfe/react-native-perp-depth-bar react-native-nitro-modules
```

## Usage

```tsx
import { PerpDepthBarsView, PerpSideRatioView } from '@onekeyfe/react-native-perp-depth-bar';

// ...

<PerpDepthBarsView
  style={{ width: 220, height: 120 }}
  percents={[90, 60, 35]}
  rowHeight={28}
  rowMarginTop={4}
  barInset={2}
  color="rgba(255, 88, 88, 0.18)"
  origin="left"
  reducedMotion={false}
  epoch={0}
  prices={['4100.1', '4099.8', '4099.5']}
  sizes={['1.2', '0.8', '2.4']}
  priceColor="#ff6b6b"
  sizeColor="#8b949e"
  priceFontSize={13}
  sizeFontSize={13}
  textInset={8}
  placeholderText="--"
  placeholderRows={3}
/>;

<PerpSideRatioView
  style={{ width: 220, height: 4 }}
  bidPercentage={56}
  askPercentage={44}
  longColor="#16a34a"
  shortColor="#dc2626"
  segmentHeight={4}
  cornerRadius={2}
  gap={2}
  reducedMotion={false}
/>;
```

## Updates and interaction

Install the application's iOS pods and rebuild the native application after
adding these Nitro views. Keep price/size arrays aligned with the depth rows,
and provide enough height for `rowHeight` and `rowMarginTop`.

For frequent updates, use `setDepth(ArrayBuffer)` with packed Float32 percentages
and `setText(prices, sizes)` on the depth view's Nitro reference. The first
imperative call switches that data channel into imperative mode; subsequent
updates to its initial props are ignored. Likewise, the ratio view exposes
`setRatio(bidPercentage, askPercentage)` instead of high-frequency ratio props.

`onRowPress` returns the zero-based depth row index. `epoch` lets callers snap
to a new depth dataset rather than animate across a coin or tick-size change.
`reducedMotion` disables animation. Empty depth/text data can use the configured
placeholder text and row count.

The interfaces are defined in [src/PerpDepthBars.nitro.ts](src/PerpDepthBars.nitro.ts)
and [src/PerpSideRatio.nitro.ts](src/PerpSideRatio.nitro.ts).

## Contributing

Follow the repository's
[native development workflow](https://github.com/OneKeyHQ/app-modules/blob/main/docs/NATIVE_MODULE_DEVELOPMENT.md).

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
