# @onekeyfe/react-native-segment-slider

SegmentSlider view for React Native

## Installation

```sh
yarn add @onekeyfe/react-native-segment-slider
```

## Usage

The slider is **uncontrolled**: pass the initial value via `defaultValue` (applied
once on mount), read live changes through `onChange`, and set the value
imperatively via the `setValue` method on the hybrid ref. This keeps reset/programmatic
updates off the Fabric prop-commit path and avoids any controlled-value-vs-drag conflict.

```tsx
import { useMemo, useRef } from 'react';
import {
  type SegmentSliderMethods,
  type SegmentSliderProps,
  SegmentSliderView,
} from '@onekeyfe/react-native-segment-slider';
import { type HybridView, callback } from 'react-native-nitro-modules';

type ISegmentSliderRef = HybridView<SegmentSliderProps, SegmentSliderMethods>;

// ...

const sliderRef = useRef<ISegmentSliderRef | null>(null);
const hybridRef = useMemo(
  () => callback((node: ISegmentSliderRef) => { sliderRef.current = node; }),
  [],
);

// Programmatically move the thumb (does NOT fire onChange):
// sliderRef.current?.setValue(50);

<SegmentSliderView
  style={{ width: 240, height: 24 }}
  hybridRef={hybridRef}
  defaultValue={50}
  min={0}
  max={100}
  segments={4}
  sliderHeight={4}
  disabled={false}
  showBubble={true}
  centerOrigin={false}
  snapTapToSegment={true}
  fillColor="#000000df"
  trackColor="#0000001f"
  thumbColor="#ffffff"
  thumbBorderColor="#00000031"
  markActiveColor="#000000df"
  markInactiveColor="#ffffff"
  markBorderColor="#0000001f"
  bubbleColor="#000000df"
  bubbleTextColor="#ffffff"
  onChange={(nextValue) => console.log(nextValue)}
/>
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
