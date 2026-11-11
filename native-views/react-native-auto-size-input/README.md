<div align="center">

# @onekeyfe/react-native-auto-size-input

**A native text input that fits text into its box by shrinking the font, with built-in prefix and suffix slots.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-auto-size-input?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-auto-size-input)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-auto-size-input?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A Nitro view that renders an editable text field together with optional prefix
and suffix labels and fits the whole line into the view's measured width. Native
code owns measurement, font fitting and layout; the application owns the text
value, the surrounding screen and when the input is mounted. There is no Web
implementation.

## Features

- **Font fitting without JavaScript** — native code measures prefix, editable
  text and suffix together and picks the largest font between `fontSize` and
  `minFontSize` that fits.
- **Prefix and suffix slots** — separate text, colors and margins for each slot,
  so a currency symbol or unit does not need a sibling `Text`.
- **Width modes** — `contentAutoWidth` lets the input grow with its content and
  pushes the suffix right; `contentCentered` centers the whole group.
- **Multiline with a line cap** — `multiline` plus `maxNumberOfLines` keeps long
  input inside a bounded box.
- **Controlled text with event counts** — `text` and `mostRecentEventCount`
  follow the React Native protocol so a stale controlled echo cannot overwrite
  newer typed text.
- **Imperative focus** — `focus()` and `blur()` are called on the view reference.

## Installation

```sh
yarn add @onekeyfe/react-native-auto-size-input react-native-nitro-modules
```

`react-native-nitro-modules` is a required peer dependency at the version pinned
by the package. The view is registered natively, so install the app's iOS pods
and rebuild the native application after adding it; Android links through
autolinking.

## Usage

```tsx
import { useCallback, useState } from 'react';
import { callback } from 'react-native-nitro-modules';
import { AutoSizeInputView } from '@onekeyfe/react-native-auto-size-input';

function AmountInput() {
  const [text, setText] = useState('');
  const onChangeText = useCallback((next: string) => setText(next), []);

  return (
    <AutoSizeInputView
      style={{ width: 240, height: 56 }}
      text={text}
      prefix="$"
      suffix="USD"
      fontSize={32}
      minFontSize={16}
      keyboardType="decimalPad"
      onChangeText={callback(onChangeText)}
    />
  );
}
```

Give the view explicit width and height so the native fit has bounds to work
with, and keep `text` synchronized with application state.

## API

Function props cross the Nitro boundary wrapped with `callback(...)` from
`react-native-nitro-modules`; the example above shows the pattern.

| Prop / method | Type | Default | Description | Platforms |
| --- | --- | --- | --- | :-: |
| `text` | `string` | — | Controlled text value. | iOS, Android |
| `mostRecentEventCount` | `number` | — | Event count used to reject stale controlled updates. | iOS, Android |
| `prefix` / `suffix` | `string` | — | Labels drawn around the editable text. | iOS, Android |
| `placeholder` | `string` | — | Hint shown while the text is empty. | iOS, Android |
| `fontSize` | `number` | `48` | Maximum font size. | iOS, Android |
| `minFontSize` | `number` | `16` | Minimum font size used by fitting. | iOS, Android |
| `multiline` | `boolean` | `false` | Allow line breaks. | iOS, Android |
| `maxNumberOfLines` | `number` | `1` | Line cap in multiline mode. | iOS, Android |
| `textColor`, `prefixColor`, `suffixColor`, `placeholderColor` | `string` | native defaults | Per-part text colors. | iOS, Android |
| `textAlign`, `fontFamily`, `fontWeight` | `string` | native defaults | Text layout and font. | iOS, Android |
| `editable` | `boolean` | `true` | Whether the input accepts edits. | iOS, Android |
| `keyboardType`, `returnKeyType` | `string` | native defaults | Keyboard configuration. | iOS, Android |
| `keyboardAppearance` | `string` | native default | Keyboard appearance. | iOS |
| `autoCorrect`, `autoCapitalize` | `boolean` / `string` | native defaults | Typing helpers. | iOS, Android |
| `selectionColor` | `string` | native default | Caret and selection color. | iOS, Android |
| `prefixMarginRight`, `suffixMarginLeft` | `number` | `0` | Spacing around the editable text. | iOS, Android |
| `showBorder`, `inputBackgroundColor` | `boolean` / `string` | `false` / transparent | Input box appearance. | iOS, Android |
| `contentAutoWidth`, `contentCentered` | `boolean` | `false` | Width modes for single-line input. | iOS, Android |
| `onChangeText` | `(text: string) => void` | — | Text changes; receives the string directly. | iOS, Android |
| `onFocus` / `onBlur` | `() => void` | — | Focus changes, without an event payload. | iOS, Android |
| `focus()` / `blur()` | `void` | — | Imperative focus control. | iOS, Android |

See [`src/AutoSizeInput.nitro.ts`](src/AutoSizeInput.nitro.ts) for the exact
interface and [`src/index.tsx`](src/index.tsx) for the exported component.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Render and edit text | ✅ | ✅ | — |
| Native font fitting | ✅ | ✅ | — |
| Prefix/suffix, width modes | ✅ | ✅ | — |
| `focus()` / `blur()` | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> `onChangeText` receives a string, not a native event, and `onFocus`/`onBlur`
> have no payload. Wrap function props with `callback(...)` before passing them
> to the view.

> [!NOTE]
> Fitting runs against the view's measured size. A view without explicit layout
> space keeps the maximum font size, and a fixed container is never expanded or
> shrunk to fit the text.

## References

- [Public interface](src/AutoSizeInput.nitro.ts)
- [Example page](../../example/react-native/pages/AutoSizeInputTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
