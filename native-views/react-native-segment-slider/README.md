# @onekeyfe/react-native-segment-slider

SegmentSlider view for React Native

## Installation

```sh
yarn add @onekeyfe/react-native-segment-slider
```

## Usage

```tsx
import { SegmentSliderView } from '@onekeyfe/react-native-segment-slider';

// ...

<SegmentSliderView
  style={{ width: 240, height: 24 }}
  value={50}
  min={0}
  max={100}
  segments={4}
  sliderHeight={4}
  disabled={false}
  showBubble={true}
  centerOrigin={false}
  snapTapToSegment={true}
  epoch={0}
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
