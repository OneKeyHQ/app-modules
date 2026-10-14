# @onekeyfe/react-native-capture-protection

Permission-minimal capture protection for OneKey iOS and Android.

```ts
import {
  CaptureProtection,
  CaptureEventType,
} from '@onekeyfe/react-native-capture-protection';

await CaptureProtection.prevent();
const subscription = CaptureProtection.addListener((event) => {
  if (event === CaptureEventType.CAPTURED) {
    /* Show a warning. */
  }
});
subscription.remove();
await CaptureProtection.allow();
```

Only the application's existing prevent/allow/listener API is supported.
No photo-library or filesystem permission is declared or requested.
Android screenshot notifications require Android 14; older Android versions still
block capture with FLAG_SECURE. iOS secure-layer behavior requires device testing.

See [SPEC](docs/SPEC.md) and [Acknowledgements](ACKNOWLEDGEMENTS.md).
