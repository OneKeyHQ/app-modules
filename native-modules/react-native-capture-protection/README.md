# @onekeyfe/react-native-capture-protection

Permission-minimal capture protection for OneKey iOS and Android.

## Installation

```sh
yarn add @onekeyfe/react-native-capture-protection react-native-nitro-modules@0.37.0
```

Install iOS pods and rebuild the native application. Web and desktop callers
use their existing platform integration; this package has no implementation for
those runtimes. Import is lazy, while a missing native registration throws when
protection or a listener is requested.

## Usage and cleanup

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

## API and ownership

| Method | Contract |
| --- | --- |
| `prevent()` | `Promise<void>`; request screenshot, recording and app-switcher protection together. |
| `allow()` | `Promise<void>`; release only the calling native object's protection. |
| `addListener(callback)` | Return a subscription with an idempotent `remove()` method. |

Repeated `prevent()` calls by the same object are idempotent. Protection is owned
by a native object rather than by each screen or each call: applications sharing
one JS object across sensitive screens must coordinate their prevent/allow
lifecycle. A separate runtime/object cannot release another object's protection.

Remove subscriptions when their owner unmounts. Removing the final JS subscriber
releases its native callback; it does not call `allow()`. There are at most 128
JS subscribers per runtime and 32 native owner/subscriber identities. Excess
registration throws. No providers, React hooks, custom-overlay API or upstream
permission-request API is exported.

## Events and platform limits

`CaptureEventType` exports `NONE`, `RECORDING`, `END_RECORDING`, `CAPTURED`,
`APP_SWITCHING`, `UNKNOWN`, `ALLOW`, `PREVENT_SCREEN_CAPTURE`,
`PREVENT_SCREEN_RECORDING` and `PREVENT_SCREEN_APP_SWITCHING`. They represent
native event values; the module never reads screenshot contents.

Android declares `DETECT_SCREEN_CAPTURE`, a normal permission used for Android
14+ screenshot callbacks. Its recording events use a virtual-display heuristic,
which can also reflect casting or external displays. It is not proof that a
recording was made. iOS uses notifications, an upstream secure-text-field layer
technique and opaque overlays; this is not a documented system-wide prevention
API. Missing activity/window or an unavailable secure layer rejects protection.

Check nested navigation, recording, app-switcher snapshots, activity recreation
and cleanup on each target device. The [contract](docs/SPEC.md) lists required
runtime acceptance; source and unit checks do not establish physical capture
prevention.

## License and attribution

MIT; [LICENSE](LICENSE) covers this package and [LICENSE.upstream](LICENSE.upstream)
preserves the adapted upstream notice. [ACKNOWLEDGEMENTS.md](ACKNOWLEDGEMENTS.md)
identifies the upstream subset and source provenance.
