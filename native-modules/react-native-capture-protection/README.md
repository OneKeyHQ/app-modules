<div align="center">

# @onekeyfe/react-native-capture-protection

**Permission-minimal screenshot, recording and app-switcher protection for OneKey on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-capture-protection?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-capture-protection)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-capture-protection?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Protects wallet-sensitive screens without photo-library or filesystem access.
One call turns on screenshot, recording and app-switcher protection together, a
subscription reports capture-related native events, and `allow()` releases the
protection owned by that native object. This is deliberately a subset of the
upstream library: only `prevent()`, `allow()` and `addListener()` are exported,
and Web/desktop runtimes keep their own platform integration.

## Features

- **One-call protection** — `prevent()` enables screenshot, recording and
  app-switcher protection together; repeated calls are idempotent.
- **Typed capture events** — `CaptureEventType` reports captures, recordings,
  app switching and protection-state changes without reading image contents.
- **Permission-minimal** — no photo-library or storage permission; Android only
  adds the normal `DETECT_SCREEN_CAPTURE` permission for Android 14+ callbacks.
- **Fail-closed** — when the native registration, foreground window or secure
  layer is unavailable, protection rejects instead of silently pretending the
  screen is covered.
- **Scoped ownership** — protection belongs to a native object, so one owner's
  `allow()` cannot release another owner's protection.
- **Lazy native object** — importing the wrapper performs no native work; the
  hybrid object is created on first use.

## Installation

```sh
yarn add @onekeyfe/react-native-capture-protection react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; this is a
Nitro module, so a JavaScript-only install does not register it. The iOS pod also
links `ReactNativeNativeLogger`, so the application's pods must include
`@onekeyfe/react-native-native-logger`. Android's `DETECT_SCREEN_CAPTURE`
permission is declared by the package manifest for Android 14+; no `Info.plist`
key is required.

## Usage

```ts
import {
  CaptureProtection,
  CaptureEventType,
} from '@onekeyfe/react-native-capture-protection';

await CaptureProtection.prevent();

const subscription = CaptureProtection.addListener((event) => {
  if (event === CaptureEventType.CAPTURED) {
    // Show the application's own warning UI.
  }
});

// Remove on unmount; this does not call allow().
subscription.remove();
await CaptureProtection.allow();
```

The application owns the warning UI, debouncing repeated events and when a
sensitive screen enters or leaves the protected state.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `prevent()` | `() => Promise<void>` | iOS, Android | Enables screenshot, recording and app-switcher protection together. |
| `allow()` | `() => Promise<void>` | iOS, Android | Releases only the calling native object's protection. |
| `addListener(callback)` | `(event: CaptureEventType) => void` returns `{ remove(): void }` | iOS, Android | Subscribes to native events; `remove()` is idempotent. |
| `CaptureEventType` | `NONE`, `RECORDING`, `END_RECORDING`, `CAPTURED`, `APP_SWITCHING`, `UNKNOWN`, `ALLOW`, `PREVENT_SCREEN_CAPTURE`, `PREVENT_SCREEN_RECORDING`, `PREVENT_SCREEN_APP_SWITCHING` | iOS, Android | Event values forwarded from the native layer. |

At most 128 JavaScript subscribers per runtime and 32 native owner/subscriber
identities are admitted; excess registration throws. The native interface is in
[`src/ReactNativeCaptureProtection.nitro.ts`](src/ReactNativeCaptureProtection.nitro.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Screenshot protection | ✅ secure layer | ✅ `FLAG_SECURE` | — |
| Recording protection | ✅ | ✅ `FLAG_SECURE` | — |
| App-switcher protection | ✅ | ✅ | — |
| Screenshot event | ✅ notification | ✅ Android 14+ | — |
| Recording event | ✅ notification | ⚠️ virtual-display heuristic | — |
| Permission required | — | `DETECT_SCREEN_CAPTURE` (normal, 14+) | — |

## Caveats

> [!WARNING]
> On iOS the protection uses an upstream-derived secure-text-field layer plus
> opaque windows; this is not a documented system-wide prevention API and needs
> device testing per OS version. Missing foreground activity/window or an
> unavailable secure layer rejects `prevent()`.

> [!IMPORTANT]
> Protection is owned by the native object, not by a screen or a call. Repeated
> `prevent()` calls from one object are idempotent, and a separate runtime/object
> cannot release another object's protection. Coordinate prevent/allow when
> several sensitive screens share one JavaScript object.

> [!NOTE]
> Android below 14 has no screenshot notification while blocking still works, and
> Android recording events are a virtual-display heuristic that may also reflect
> casting or external displays — they are not proof that a recording was made.

## References

- [Package spec](docs/SPEC.md)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Adapted from <code>react-native-capture-protection</code> 2.3.0</summary>

This module is adapted from
[react-native-capture-protection](https://github.com/wn-na/react-native-capture-protection)
version **2.3.0**, authored by **lethe / wn-na**. Thank you for the upstream
screenshot, recording and app-switcher protection implementation.

The upstream code is distributed under the MIT license. Its copyright and full
license are retained in [`LICENSE.upstream`](LICENSE.upstream); the main
[`LICENSE`](LICENSE) also retains the upstream copyright for CocoaPods
acknowledgements. [`ACKNOWLEDGEMENTS.md`](ACKNOWLEDGEMENTS.md) identifies the
upstream subset and source provenance.

</details>

## License

MIT. See [LICENSE](LICENSE) for this package and
[LICENSE.upstream](LICENSE.upstream) for the adapted upstream notice.
