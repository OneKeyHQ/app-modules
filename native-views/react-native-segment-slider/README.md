<div align="center">

# @onekeyfe/react-native-segment-slider

**One segmented slider for iOS, Android and Web — track, fill, marks, thumb and value bubble — with no JavaScript round trip per drag frame.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-segment-slider?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-segment-slider)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-segment-slider?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4D6BFE)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

`SegmentSlider` is a controlled slider with the same props on every platform.
On iOS and Android a Nitro view draws the slider and handles the drag on the UI
thread; on Web a DOM implementation updates styles imperatively during a drag
instead of re-rendering React per frame. The application resolves theme colors
and owns the value's meaning.

## Features

- **Native drag** — on iOS and Android the pan gesture and drawing stay on the
  UI thread; there is no JavaScript frame callback while dragging.
- **Controlled** — `value` drives the slider and `onChange` reports interaction
  values. A value the caller changes mid-drag is applied when the drag ends.
- **Segment marks** — `segments` draws the marks; `snapTapToSegment` snaps a
  quick tap to the nearest mark while drags stay free.
- **Per-step haptics** — `hapticStep` ticks on every step of a drag on iOS and
  Android, with a stronger tick on each mark.
- **Theme-agnostic colors** — nine color props take resolved color strings.
- **Optional value bubble** and **signed ranges** with `centerOrigin`.

## Installation

```sh
yarn add @onekeyfe/react-native-segment-slider react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. Install the app's iOS pods and rebuild the native application
after adding the package; Android links through autolinking. Web needs no extra
setup.

## Usage

```tsx
import { useState } from 'react';
import { SegmentSlider } from '@onekeyfe/react-native-segment-slider';

function Amount() {
  const [value, setValue] = useState(50);
  return (
    <SegmentSlider
      aria-label="Amount"
      value={value}
      onChange={setValue}
      segments={4}
      snapTapToSegment
      hapticStep={1}
      fillColor="#000000df"
      trackColor="#0000001f"
    />
  );
}
```

`onChange` receives an integer inside the range. Setting `value` from outside
(for example a reset button) moves the thumb without calling `onChange`. Color
props you leave out use a neutral light palette (`DEFAULT_SEGMENT_SLIDER_COLORS`).

## API

| Prop | Type | Description | Platforms |
| --- | --- | --- | :-: |
| `value` | `number` | Current value. | All |
| `onChange` | `(value: number) => void` | Integer value from an interaction, within `[ceil(min), floor(max)]`. | All |
| `segments` | `number` | Number of intervals to draw marks for; below 1 renders no marks. | All |
| `min`, `max` | `number` | Range bounds (default 0 / 100). | All |
| `sliderHeight` | `number` | Track thickness (default 4). | All |
| `disabled` | `boolean` | Dims the slider, blocks interaction and cancels an active drag. | All |
| `showBubble` | `boolean` | Shows the rounded value with `%` above the thumb during interaction (default true). | All |
| `centerOrigin` | `boolean` | Fills from the zero origin instead of the left edge. | All |
| `snapTapToSegment` | `boolean` | Snaps a quick tap to the nearest mark; drags stay continuous. | All |
| `hapticStep` | `number` | Light tick each time a drag enters a new step bucket; 0 keeps mark-only ticks. | iOS, Android |
| `alignSegmentMarksToIntegerValues` | `boolean` | Places marks on the integer values the slider emits. | Web |
| `aria-label`, `aria-labelledby` | `string` | Accessible name for the slider. | All |
| `fillColor`, `trackColor`, `thumbColor`, `thumbBorderColor`, `markActiveColor`, `markInactiveColor`, `markBorderColor`, `bubbleColor`, `bubbleTextColor` | `string` | Resolved colors (hex or `rgba()`). | All |
| `onSlideStart`, `onSlideComplete` | `() => void` | Delimit one drag, tap or Web key press. | All |

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Render and drag the slider | ✅ | ✅ | ✅ |
| Segment marks and tap snapping | ✅ | ✅ | ✅ |
| Value bubble | ✅ | ✅ | ✅ |
| Per-step haptics | ✅ | ✅ | — |
| Keyboard (arrows, Home, End) | — | — | ✅ |

## Caveats

> [!NOTE]
> The bubble draws above the 24-point touch area. Give the slider vertical
> headroom and do not clip an ancestor to the touch area, or the bubble will be
> cut off.

> [!NOTE]
> Web leaves 8 px at each end of the track and keeps a 16-px thumb while
> pressed; iOS and Android leave 12 points/dp for the enlarged pressed thumb.

## References

- [Package spec](docs/SPEC.md)
- [Style gallery](docs/screenshots/style-gallery.png)
- [Example page](../../example/react-native/pages/SegmentSliderTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
