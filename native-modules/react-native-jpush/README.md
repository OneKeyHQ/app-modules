<div align="center">

# @onekeyfe/react-native-jpush

**A clean-room JPush and JCore implementation for React Native, readable end to end and API-compatible with `jpush-react-native`.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-jpush?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-jpush)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-jpush?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A source-implemented JCore (5.2.0) and JPush (6.0.0 iOS / 5.9.0 Android) client:
UDP SIS resolution, the asymmetric TCP framing, AES-256-CBC / SM4-CBC encryption,
registration and login, tag/alias/properties, local notifications and the
Android `:pushcore` IPC are all compiled from readable Objective-C/Swift and
Java/Kotlin instead of shipped as `.a`/`.jar` blobs. The JavaScript surface
mirrors `jpush-react-native` and `jcore-react-native` — default `JPush`, named
`JCore` — dispatched through a Nitro HybridObject with a legacy bridge fallback.
Supported on iOS (13.4+) and Android (minSdk 23); web and desktop are out of
scope.

## Features

- **Auditable native source** — no decompiled or obfuscated vendor binaries in
  the build, and dynamic code loading is intentionally excluded.
- **Drop-in JS API** — the `JPush` and `JCore` classes keep the methods and
  callbacks that `jpush-react-native` / `jcore-react-native` consumers use.
- **Shared native state** — main and background JS runtimes share one
  process-owned SDK; policy changes invalidate stale native content instead of
  replaying it.
- **Bounded queues** — at most 128 pre-login requests and 65,536 decoded GZIP
  bytes, with fixed error codes instead of unbounded buffering.
- **Privacy-safe logging** — diagnostics go through
  `@onekeyfe/react-native-native-logger` and redact notification content,
  alias, tags, properties, mobile number and tokens.
- **Optional Google transport** — an Android-only source library under
  `android/fcm` replaces the opaque Jiguang FCM plugin.

## Installation

```sh
yarn add @onekeyfe/react-native-jpush react-native-nitro-modules
```

`@onekeyfe/react-native-native-logger` is a peer dependency. Install the
application's iOS pods and rebuild the native binary; the module cannot run from
a JavaScript-only update.

### iOS

Enable the Push Notifications capability and APNs entitlements in the app
target. The host AppDelegate forwards the APNs device token to the native SDK
(for example with `JPUSHService registerDeviceToken:`); the module installs its
`UNUserNotificationCenter` delegate when the app has not set one.

### Android

The package manifest merges its permissions and components, including
`POST_NOTIFICATIONS`, `INTERNET`, `ACCESS_NETWORK_STATE` and the vendor badge
permissions. On Android 13+ call `JPush.requestPermission()` from a user action
to obtain the runtime notification permission. Minimum SDK is 23.

### Google / FCM distribution (optional)

Google Play builds can link the source library in `android/fcm`, which replaces
`cn.jiguang.sdk.plugin:fcm` and delegates transport to the official Firebase
Messaging SDK. It is an Android Gradle project, not a second npm package; the
host keeps its `google-services.json` and Google Services plugin. Integration
steps, receive gates and acceptance status are in [`docs/SPEC.md`](docs/SPEC.md).

## Usage

```ts
import JPush from '@onekeyfe/react-native-jpush';

JPush.setLoggerEnable(__DEV__);
JPush.init({
  appKey: '<your-jpush-app-key>',
  channel: 'developer-default',
  production: false,
});

JPush.getRegistrationID(({ registerID }) => {
  // Send registerID to your backend for targeted pushes.
});

JPush.addConnectEventListener(() => {
  // Fires on connection state changes.
});
```

Register listeners after `init()` and remove them with `JPush.removeListener()`
when their owner unmounts. Permission is a separate step: the user must grant
notification authorization before Android or iOS can deliver.

## API

| Export | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `JPush.init({ appKey, channel, production })` | `void` | iOS, Android | Configures and starts the push client. |
| `JPush.getRegistrationID(cb)` | `{ registerID }` | iOS, Android | Current registration ID. |
| `JPush.addTags` / `updateTags` / `deleteTag(s)` / `queryTag(s)` | callbacks / events | iOS, Android | Tag operations, keyed by `sequence`. |
| `JPush.setAlias` / `deleteAlias` / `queryAlias` | callbacks / events | iOS, Android | Alias operations. |
| `JPush.setProperties` / `deleteProperties` / `cleanProperties` | callbacks / events | iOS, Android | User-property operations. |
| `JPush.setBadge({ badge, appBadge })` | `void` | iOS, Android | App-icon badge count. |
| `JPush.addLocalNotification` / `removeLocalNotification` / `clearAllNotifications` / `clearNotificationById` | `void` | iOS, Android | Local notification scheduling and cleanup. |
| `JPush.stopPush` / `resumePush` / `isPushStopped(cb)` | `void` / `boolean` | iOS, Android | Client-side push enable/disable. |
| `JPush.requestPermission()` | `void` | iOS, Android | Requests notification authorization. |
| `JPush.add*Listener(cb)` / `removeListener(cb)` | `void` | iOS, Android | Connect, notification, local, custom-message, tag/alias, command and button events. |
| `JCore.setAuth(auth)` and JCore compliance/diagnostics methods | `void` | iOS, Android | Collection authorization, country code, wakeup and local logs. |

The underlying `ReactNativeJpush` HybridObject in
[`src/ReactNativeJpush.nitro.ts`](src/ReactNativeJpush.nitro.ts) exposes the same
operations plus lower-level badge, geofence and channel setters. See
[`docs/SPEC.md`](docs/SPEC.md) for the wire protocol, defaults and error codes.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Registration, login and reconnection | ✅ | ✅ | — |
| Tags, alias, properties, mobile number | ✅ | ✅ | — |
| Notifications, local notifications and badge | ✅ | ✅ | — |
| Notification authorization request | ✅ | ✅ | — |
| APNs device-token binding | ✅ | — | — |
| Optional Google FCM source transport | — | ✅ | — |

## Caveats

> [!IMPORTANT]
> The native SDK requires a rebuilt host binary. Web, desktop and extension
> targets are unsupported — guard every call with a platform check, because the
> wrapper returns without effect when no native module is registered.

> [!WARNING]
> Push stop and collection-authorization changes gate delivery immediately.
> Content blocked while stopped or unauthorized is discarded and is never
> replayed after resume or regrant, and notifications already displayed are not
> retracted.

> [!NOTE]
> Local tests, loopback protocol suites and synthetic payloads do not prove APNs,
> FCM or Jiguang cloud delivery. Configure real AppKey/APNs credentials and
> validate delivery on a device before shipping.

## References

- [Package spec](docs/SPEC.md)
- [JS wrapper](src/index.tsx)
- [Example page](../../example/react-native/pages/JPushTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
