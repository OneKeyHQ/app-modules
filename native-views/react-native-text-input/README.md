<div align="center">

# @onekeyfe/react-native-text-input

**React Native's `TextInput` plus native paste events for text and image clipboard items.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-text-input?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-text-input)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-text-input?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)

</div>

Wraps React Native text inputs and emits a native `onPaste` event for clipboard
content that React Native does not surface, including images. The platform input
still performs the actual paste; the callback reports what the clipboard held and
the application decides how to use it. Payloads are platform-shaped: iOS loads an
image into a temporary file, Android reports the clipboard item's URI and MIME
type without copying it. There is no Web implementation.

## Features

- **Native paste events** — an optional `onPaste` callback reports clipboard
  items next to the ordinary `TextInput` props.
- **Text and image coverage** — text uses `text/plain`; image clipboard items
  arrive with a platform-specific locator instead of raw bytes.
- **No Android copy** — for URI content the package reports the content
  resolver's MIME type and the original URI, without creating a temporary file.
- **iOS temporary file URL** — image data is written to a temporary file and the
  application owns any durable copy it needs.
- **Full `TextInput` surface** — the default export accepts React Native
  `TextInputProps`, including controlled text and ref methods.
- **Opt-in native listener** — with no `onPaste`, iOS has no paste subscription
  and Android has no paste watcher.

## Installation

```sh
yarn add @onekeyfe/react-native-text-input
```

Install the app's iOS pods and rebuild the native application after adding the
package so the iOS paste observer and the Android input component are
registered; Android links through autolinking. The package declares no
additional permissions or `Info.plist` entries.

## Usage

```tsx
import { useCallback, useState } from 'react';
import TextInput, {
  type IPasteEventParams,
} from '@onekeyfe/react-native-text-input';

function PasteAwareField() {
  const [text, setText] = useState('');
  const onPaste = useCallback((event: IPasteEventParams) => {
    const item = event.nativeEvent.items?.[0];
    if (item?.type === 'text/plain') {
      console.log(item.data);
    }
  }, []);

  return (
    <TextInput
      style={{ minHeight: 48 }}
      value={text}
      onChangeText={setText}
      onPaste={onPaste}
      placeholder="Paste text or an image"
    />
  );
}
```

Check `nativeEvent.items` before iterating and inspect each item's `type` before
interpreting its `data`.

## API

| Export | Type | Description | Platforms |
| --- | --- | --- | :-: |
| `TextInput` (default) | component | React Native `TextInput` props and ref, plus `onPaste`. | iOS, Android |
| `ITextInputProps` | interface | Extends React Native `TextInputProps` with `onPaste`. | iOS, Android |
| `onPaste` | `(event: IPasteEventParams) => void` | Paste event; absent items are allowed. | iOS, Android |
| `IPasteEventParams` | type | `{ nativeEvent: { items?: IPasteEventPayload } }`. | iOS, Android |
| `IPasteEventPayloadItem` | type | `{ type?: EPasteEventPayloadItemType; data?: string }`. | iOS, Android |
| `EPasteEventPayloadItemType` | enum | MIME types; currently `TextPlain = 'text/plain'`. | iOS, Android |

See [`src/type.ts`](src/type.ts) for the payload types and
[`docs/SPEC.md`](docs/SPEC.md) for event ownership, temporary-file handling and
the platform behavior.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Emit `onPaste` | ✅ | ✅ | — |
| Text items (`text/plain`) | ✅ | ✅ | — |
| Image items | ✅ MIME + temporary file URL | ✅ MIME + content URI | — |
| Plain `TextInput` behavior without `onPaste` | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> `onPaste` never supplies image bytes. iOS gives a temporary file URL that the
> application should consume while it is available; Android gives a content URI
> plus MIME type and copies nothing. The application owns any durable copy.

> [!NOTE]
> Android inspects only the first clipboard item. Without an `onPaste` callback
> the native paste subscription/watcher is disabled, and both platforms still
> run their ordinary native paste path after an optional event.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/TextInputTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
