# @onekeyfe/react-native-text

A React Native `Text`-compatible component with an Android text-clipping guard.

## Installation

```sh
yarn add @onekeyfe/react-native-text
```

Rebuild the native Android application after adding the package so its native
text host is registered. This component is opt-in: importing it does not replace
React Native's global Text component.

## Usage

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

## API and platform behavior

The package exports `Text` as both a named and default component, together with
`TextProps`. Use ordinary text props for line limits, styles and event handlers;
there is no separate imperative layout or measurement API in this wrapper.

The Android guard only adds width when the layout constraints allow it. A fixed
width can still truncate text according to `numberOfLines` and ellipsizing;
this component does not expand a fixed container or reduce its font size.
Check clipped strings in the actual application's fonts, density and layout.

The implementation uses React Native text internals on Android. Compatibility
with a different React Native release needs validation with the consumer's
native build and text interactions. iOS and the generic implementation continue
to export React Native Text directly.

See [src/types.ts](src/types.ts) for the public props and the platform-specific
files under `src` for the wrapper implementation.

## Choosing the platform component

Both `import { Text }` and the package's default import select the same public
component. `TextProps` extends the supported text prop surface; the package
does not add a font-size-fitting or container-resizing prop.

The Android implementation distinguishes root and nested text to preserve
React Native text composition. On other platforms it forwards to React Native
Text. Keep the component opt-in at the call sites needing the clipping guard,
and validate native builds when changing the React Native version because
Android registration depends on its renderer internals.

## License

MIT.
