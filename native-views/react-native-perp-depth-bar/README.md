<div align="center">

# @onekeyfe/react-native-perp-depth-bar

**Native order-book depth bars and the bid/ask ratio track for OneKey's perpetual screens.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-perp-depth-bar?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-perp-depth-bar)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-perp-depth-bar?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

`PerpDepthBarsView` draws one side of an order book as a single native view: one
row per depth level with a horizontal fill, price and size text, placeholder rows
and native row taps. `PerpSideRatioView` draws the separate bid/ask ratio track.
The application owns the market data, formatting and row count; the views own
drawing, layout, touch handling and animation. There is no Web implementation.

## Features

- **One view per column** — native code owns all row layers and the display
  link instead of one React view per order-book row.
- **High-frequency imperative path** — `setDepth()` takes packed Float32
  percentages, `setText()` takes price/size arrays and `setRatio()` takes the
  split, bypassing the Fabric prop-commit path.
- **Native text rows** — price and size strings are drawn by the view, so no
  sibling `Text` rows need to stay pixel-aligned.
- **Stable placeholders** — `placeholderText` and `placeholderRows` reserve the
  column's height before data lands and swap out without a blank frame.
- **Native row press** — `onRowPress(rowIndex)` reports a zero-based row without
  a JS overlay or hit-testing.
- **Dataset snap** — `epoch` lets a coin or tick-size change jump to the new
  depth instead of animating across it; `reducedMotion` disables animation.

## Installation

```sh
yarn add @onekeyfe/react-native-perp-depth-bar react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. Install the app's iOS pods and rebuild the native application
after adding the package; Android links through autolinking.

## Usage

```tsx
import {
  PerpDepthBarsView,
  PerpSideRatioView,
} from '@onekeyfe/react-native-perp-depth-bar';

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
  onRowPress={(rowIndex) => console.log(rowIndex)}
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

Keep price and size arrays aligned with the depth rows and give the view enough
height for `rowHeight + rowMarginTop` per row.

## API

| Export | Prop / method | Type | Description | Platforms |
| --- | --- | --- | --- | :-: |
| `PerpDepthBarsView` | `percents` | `number[]` | Depth percentage per row, `0..100`. | iOS, Android |
| | `rowHeight`, `rowMarginTop`, `barInset` | `number` | Row geometry in points. | iOS, Android |
| | `color`, `origin` | `string` | Fill color and `left`/`right` growth anchor. | iOS, Android |
| | `reducedMotion`, `epoch` | `boolean`, `number` | Skip animation; snap to a new dataset. | iOS, Android |
| | `prices`, `sizes` | `string[]` | Per-row text, parallel to `percents`. | iOS, Android |
| | `priceColor`, `sizeColor`, `priceFontSize`, `sizeFontSize`, `textInset` | `string` / `number` | Row text style. | iOS, Android |
| | `placeholderText`, `placeholderRows` | `string`, `number` | Rows drawn before real data. | iOS, Android |
| | `onRowPress` | `(rowIndex: number) => void` | Zero-based row tap. | iOS, Android |
| | `setDepth(buffer)` | `ArrayBuffer` | Packed little-endian Float32 percentages, e.g. `new Float32Array(percents).buffer`. | iOS, Android |
| | `setText(prices, sizes)` | `string[]`, `string[]` | Imperative row text update. | iOS, Android |
| `PerpSideRatioView` | `bidPercentage`, `askPercentage` | `number` | Bid and ask shares, `0..100`. | iOS, Android |
| | `longColor`, `shortColor` | `string` | Segment colors. | iOS, Android |
| | `segmentHeight`, `cornerRadius`, `gap` | `number` | Track geometry in points. | iOS, Android |
| | `reducedMotion` | `boolean` | Skip the width animation. | iOS, Android |
| | `setRatio(bidPercentage, askPercentage)` | `number`, `number` | Imperative ratio update. | iOS, Android |

Pass a Nitro `hybridRef` callback to either view to receive the live view object,
then call `setDepth`/`setText` on the depth view and `setRatio` on the ratio view.
See [`src/PerpDepthBars.nitro.ts`](src/PerpDepthBars.nitro.ts),
[`src/PerpSideRatio.nitro.ts`](src/PerpSideRatio.nitro.ts) and
[`docs/SPEC.md`](docs/SPEC.md) for the full contract.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| `PerpDepthBarsView` bars and text | ✅ | ✅ | — |
| `PerpSideRatioView` | ✅ | ✅ | — |
| Imperative `setDepth`/`setText`/`setRatio` | ✅ | ✅ | — |
| Native row press | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> The first `setDepth`, `setText` or `setRatio` call switches that data channel
> into imperative mode for the lifetime of the view. After that, the matching
> props are ignored, including empty writes, so pick one data path per channel.

> [!NOTE]
> `setDepth` reads complete little-endian Float32 values and ignores trailing
> bytes; callers own the row alignment of `prices` and `sizes`. The iOS
> imperative methods publish on the main thread and no performance claim is
> made for production row counts.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/PerpDepthBarTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
