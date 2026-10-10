# @onekeyfe/react-native-auto-size-input

A native text input with font sizing and optional prefix/suffix content for
OneKey React Native applications on iOS and Android. The Nitro view exposes
text editing, keyboard configuration and imperative focus/blur methods.
The caller owns the input value and its surrounding layout.

## Installation

```sh
yarn add @onekeyfe/react-native-auto-size-input react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application after
adding the package. The component requires a native Nitro view registration.

## Usage

```tsx
import { useState } from 'react';
import { AutoSizeInputView } from '@onekeyfe/react-native-auto-size-input';

function AmountInput() {
  const [text, setText] = useState('');
  return (
    <AutoSizeInputView
      style={{ width: 240, height: 56 }}
      text={text}
      onChangeText={setText}
      suffix="USD"
      fontSize={32}
      minFontSize={16}
      keyboardType="decimal-pad"
    />
  );
}
```

## API

- Text: `text`, `prefix`, `suffix` and `placeholder`.
- Sizing: `fontSize`, `minFontSize`, `multiline` and `maxNumberOfLines`.
- Appearance: separate text/prefix/suffix/placeholder colors, font family,
  weight, alignment, border and input background.
- Layout: prefix/suffix margins, `contentAutoWidth` and `contentCentered`.
- Input: editable state, keyboard/return key appearance, autocorrection,
  capitalization and selection color.
- Events: `onChangeText(text)`, `onFocus()` and `onBlur()`.
- Native methods: `focus()` and `blur()` through the Nitro view reference.

Use the package's own prop types rather than assuming every React Native
TextInput prop is supported. Native input styling and keyboard behavior should
be checked on each target platform when integrating a new configuration.
The full interface is in [src/AutoSizeInput.nitro.ts](src/AutoSizeInput.nitro.ts).

## License

MIT.
