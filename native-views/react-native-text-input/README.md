# @onekeyfe/react-native-text-input

React Native TextInput with paste events for text and images on Android and iOS.

```tsx
import TextInput from '@onekeyfe/react-native-text-input';

<TextInput
  onPaste={({ nativeEvent }) => {
    console.log(nativeEvent.items);
  }}
/>
```

Image paste items contain a MIME type and a temporary local file URL. Text paste items use `text/plain`.
