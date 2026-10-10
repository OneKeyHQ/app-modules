# @onekeyfe/react-native-text-input

React Native TextInput with paste events for text and images on Android and iOS.

## Installation

```sh
yarn add @onekeyfe/react-native-text-input
```

Install the application's iOS pods and rebuild the native application to register
the paste integration. The default export accepts React Native `TextInputProps`
and the additional `onPaste` callback.

## Usage

```tsx
import TextInput from '@onekeyfe/react-native-text-input';

<TextInput
  onPaste={({ nativeEvent }) => {
    console.log(nativeEvent.items);
  }}
/>
```

## Paste events and platform differences

`onPaste` receives `nativeEvent.items`, an optional array whose entries have
`type` and `data` fields. Text items use `text/plain` and carry their text in
`data`. The event does not supply image bytes directly.

- iOS image items contain a MIME type and a temporary local file URL. Consume
  that file while available; the application owns any durable copy it needs.
- Android inspects the first clipboard item. For URI content it reports the
  content resolver's MIME type and original URI, without copying an image into
  a temporary file. Text paste still follows the underlying native input path.
- With no `onPaste` callback, the native paste subscription/watcher is disabled.

Do not assume an Android content URI is an iOS-style filesystem path. The native
input retains its ordinary paste fallback when there is no reportable event.
This package defines paste integration for iOS and Android, not a Web clipboard
implementation.

The public payload types are in [src/type.ts](src/type.ts). The
[native behavior specification](https://github.com/OneKeyHQ/app-modules/blob/main/native-views/react-native-text-input/docs/SPEC.md)
records event ownership, temporary-file handling and acceptance limits.

## Consuming paste payloads

Check `nativeEvent.items` before iterating: the public type allows it to be
absent. Inspect each item's MIME `type` before interpreting its string `data`.
A text string, a temporary iOS file URL and an Android content URI need distinct
consumer handling; the callback does not copy them into a common byte format.

On Android both Paste and Paste as plain text continue through the underlying
input paste path after the optional event. On iOS the availability cache is only
a menu hint; the actual paste action checks clipboard contents. The
[specification](https://github.com/OneKeyHQ/app-modules/blob/main/native-views/react-native-text-input/docs/SPEC.md) separates these platform behaviors and documents
pending device acceptance.

## License

MIT.
