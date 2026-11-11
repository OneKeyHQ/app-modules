<div align="center">

# @onekeyfe/react-native-segment-slider

**A fully native segmented slider — track, fill, marks, thumb and value bubble — with no JavaScript round trip per drag frame.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-segment-slider?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-segment-slider)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-segment-slider?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Native code owns the value, drawing the track, fill bar, segment marks, draggable
thumb and optional percentage bubble while the pan gesture is handled on the UI
thread. The slider is uncontrolled: `defaultValue` seeds it once, drags are
reported through `onChange`, and programmatic updates go through `setValue` on
the view reference. The application resolves theme colors and owns the value's
meaning.

## Features

- **Native drag** — the pan gesture, layer/paint updates, thumb and halo stay on
  the native UI thread; there is no JavaScript frame callback while dragging.
- **Uncontrolled by design** — `defaultValue` applies once and cannot fight a
  drag; `setValue` moves the thumb without emitting `onChange`.
- **Segment marks** — `segments` draws the visual marks, and
  `snapTapToSegment` snaps a tap while drags stay continuous.
- **Optional value bubble** — `showBubble` displays the rounded percentage above
  the thumb during interaction.
- **Signed ranges** — `min`/`max` can be negative, and `centerOrigin` fills
  outward from the zero point instead of the left edge.
- **Theme-agnostic colors** — colors arrive as resolved hex/rgba strings, so the
  native view never depends on the application's theme system.

## Installation

```sh
yarn add @onekeyfe/react-native-segment-slider react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. Install the app's iOS pods and rebuild the native application
after adding the package; Android links through autolinking. There is no Web
implementation in this package.

## Usage

```tsx
import { SegmentSliderView } from '@onekeyfe/react-native-segment-slider';

const COLORS = {
  fillColor: '#000000df',
  trackColor: '#0000001f',
  thumbColor: '#ffffff',
  thumbBorderColor: '#00000031',
  markActiveColor: '#000000df',
  markInactiveColor: '#ffffff',
  markBorderColor: '#0000001f',
  bubbleColor: '#000000df',
  bubbleTextColor: '#ffffff',
};

<SegmentSliderView
  style={{ width: 240, height: 24 }}
  defaultValue={50}
  min={0}
  max={100}
  segments={4}
  sliderHeight={4}
  disabled={false}
  showBubble
  centerOrigin={false}
  snapTapToSegment
  onChange={(value) => console.log(value)}
  {...COLORS}
/>;
```

`onChange` receives an already rounded integer. For a programmatic reset, pass a
callback through the Nitro `hybridRef` prop and call `setValue(v)` on the
`HybridView<SegmentSliderProps, SegmentSliderMethods>` reference; the wrapper
itself wraps `onChange`/`onSlideStart`/`onSlideComplete`.

## API

All slider props are required except the callbacks. See
[`src/SegmentSlider.nitro.ts`](src/SegmentSlider.nitro.ts) for the exact types.

| Prop / method | Type | Description | Platforms |
| --- | --- | --- | :-: |
| `defaultValue` | `number` | Initial value, applied once on mount. | iOS, Android |
| `min`, `max` | `number` | Range bounds; both platforms inset 12 points/dp at each end for the thumb and halo. | iOS, Android |
| `segments` | `number` | Number of intervals to draw marks for; below 1 renders no marks. | iOS, Android |
| `sliderHeight` | `number` | Track thickness in points. | iOS, Android |
| `disabled` | `boolean` | Dims the view and blocks interaction. | iOS, Android |
| `showBubble` | `boolean` | Shows the rounded value with `%` above the thumb during interaction. | iOS, Android |
| `centerOrigin` | `boolean` | Fills from the zero origin instead of the left edge. | iOS, Android |
| `snapTapToSegment` | `boolean` | Snaps a tap to the nearest mark; drags stay continuous. | iOS, Android |
| `fillColor`, `trackColor`, `thumbColor`, `thumbBorderColor`, `markActiveColor`, `markInactiveColor`, `markBorderColor`, `bubbleColor`, `bubbleTextColor` | `string` | Resolved colors (hex or `rgba()`); invalid colors fall back to the platform parser. | iOS, Android |
| `onChange` | `(value: number) => void` | Integer-rounded value during interaction. | iOS, Android |
| `onSlideStart`, `onSlideComplete` | `() => void` | Delimit one drag or tap interaction. | iOS, Android |
| `setValue(value)` | `void` | Clamps to the range, hard-snaps the thumb, cancels a drag and does not fire `onChange`. | iOS, Android |

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Render and drag the slider | ✅ | ✅ | — |
| Segment marks and tap snapping | ✅ | ✅ | — |
| Value bubble | ✅ | ✅ | — |
| `setValue` imperative update | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> The slider is uncontrolled. A later `defaultValue` prop change does not reset
> it; use `setValue` on the hybrid ref, which deliberately does not fire
> `onChange` because the caller already knows the value.

> [!NOTE]
> The bubble draws above the 24-point touch view. Give the view vertical
> headroom and do not clip an ancestor to the touch view, or the bubble will be
> cut off. The package does not implement a Web slider.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/SegmentSliderTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
