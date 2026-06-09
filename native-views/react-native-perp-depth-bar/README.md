# @onekeyfe/react-native-perp-depth-bar

PerpDepthBar view for React Native

## Installation

```sh
yarn add @onekeyfe/react-native-perp-depth-bar
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
/>

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
/>
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
