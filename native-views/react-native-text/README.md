<div align="center">

# @onekeyfe/react-native-text

**An opt-in native Android `Text` that refuses to clip the last glyph column; iOS and Web keep React Native's `Text`.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-text?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-text)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-text?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)

</div>

On Android this package renders root text through a Fabric paragraph component
whose shadow node adds one physical pixel to the measured intrinsic width when
the layout constraints allow it, so glyphs at the right edge are not clipped.
It is opt-in: importing it never replaces React Native's global `Text`. The
component keeps React Native `Text` props, refs, nested-text composition,
pressability and accessibility semantics, and on iOS and Web it exports React
Native's `Text` unchanged.

## Features

- **Android clipping guard** — one extra physical pixel of intrinsic width,
  clamped to the available maximum width, instead of custom text layout.
- **Drop-in surface** — the same props and ref target as React Native `Text`;
  the type tests assert parity in both directions.
- **Preserved text composition** — nested text stays in React Native's virtual
  text stack while root text uses the native host.
- **Pressability and accessibility parity** — `onPress`/`onLongPress`, the
  implicit link role, ARIA props and style normalization follow React Native.
- **Layout events and refs** — `onTextLayout`, `selectable`, refs and text
  styling keep their React Native behavior.
- **Opt-in by import** — only call sites that import this component get the
  guard; no global component registration is changed.

## Installation

```sh
yarn add @onekeyfe/react-native-text
```

The package has native Android code only. Rebuild the Android application after
adding it so the `OneKeyText` view manager is registered through autolinking.
iOS and Web need no native step because they use React Native's `Text`.

## Usage

```tsx
import { Text } from '@onekeyfe/react-native-text';

<Text numberOfLines={1} style={{ fontSize: 16 }}>
  自动
</Text>;
```

Use it at call sites where the Android clipping guard matters; everywhere else
React Native's own `Text` behaves the same.

## API

| Export | Type | Description | Platforms |
| --- | --- | --- | :-: |
| `Text` | component | React Native-compatible text component; native host on Android, React Native `Text` elsewhere. | iOS, Android, Web |
| default export | component | The same component as the named `Text`. | iOS, Android, Web |
| `TextProps` | type | Alias of React Native's `TextProps`; no extra props are added. | iOS, Android, Web |

There is no separate measurement or layout API. See
[`src/types.ts`](src/types.ts) for the public props, [`src/index.ts`](src/index.ts)
for the exports and the platform files under `src/` for the implementation.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Render text | ✅ React Native `Text` | ✅ native host | ✅ React Native `Text` |
| Extra physical-pixel width guard | — | ✅ constraints permitting | — |
| Nested text, pressability, refs | ✅ | ✅ | ✅ |
| Replaces React Native's global `Text` | — | — | — |

## Caveats

> [!IMPORTANT]
> The package is opt-in and never replaces React Native's global `Text`. On iOS
> and Web `Text` is React Native's `Text`, so the clipping guard only exists on
> Android.

> [!NOTE]
> The guard adds width only when the layout constraints allow it. A fixed-width
> container can still truncate text according to `numberOfLines` and
> `ellipsizeMode`; the component does not resize the container or shrink the
> font to fit.

> [!WARNING]
> The Android implementation builds on React Native text renderer internals
> (a custom paragraph shadow node and view config). Re-validate the native build
> and text interactions after a React Native upgrade.

## References

- [Example page](../../example/react-native/pages/TextTestPage.tsx)
- [Android native host](android/src/main/jni/react/renderer/components/RNOneKeyTextSpec/ComponentDescriptors.h)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
