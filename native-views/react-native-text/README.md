# @onekeyfe/react-native-text

A React Native `Text`-compatible component with an Android text-clipping guard.

```tsx
import { Text } from '@onekeyfe/react-native-text';

<Text numberOfLines={1} style={{ fontSize: 16 }}>
  Auto
</Text>;
```

On Android, root text uses the React Native Paragraph/`ReactTextView` stack and
adds one physical pixel to intrinsic width when constraints permit. Nested text,
selection, ellipsizing, text-layout callbacks, press handling, accessibility,
and refs retain React Native `Text` semantics. Other platforms export React
Native `Text` directly.
