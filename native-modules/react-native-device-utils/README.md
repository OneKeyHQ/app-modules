# @onekeyfe/react-native-device-utils

Native application and device integration utilities for OneKey on iOS and
Android. The Nitro interface groups launch information, display and system-bar
appearance, device tokens, boot recovery and platform-service inspection.
These are host integration helpers rather than a portable browser device API.

## Installation

```sh
yarn add @onekeyfe/react-native-device-utils react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. Features
such as push tokens and launch options also require the application's native
host configuration; installing this package does not register those services.

## Usage

```ts
import { ReactNativeDeviceUtils } from '@onekeyfe/react-native-device-utils';

const launch = await ReactNativeDeviceUtils.getLaunchOptions();
const startupTime = await ReactNativeDeviceUtils.getStartupTime();
const installer = ReactNativeDeviceUtils.getInstallerPackageName();
```

## API groups

- Display: dual-screen status, window/hinge rectangles, background color and
  interface style.
- Android navigation bar: `setNavigationBarAppearance(r, g, b, a, useDarkIcons)`.
  Color channels use 0–255; this method is a no-op on iOS.
- Launch and notifications: read/clear launch options, register/store device
  tokens and consume cold-start local notification data.
- Android services: WebView package information, Google Play Services status,
  distribution channel and installer identification.
- Boot recovery: mark boot success, inspect/update failure counts and consume
  a pending recovery action.

Spanning listeners return IDs that must be removed when their owner finishes.
Cold-start local notification consumption is meaningful on iOS; Android returns
an empty string. Platform-specific results should not be interpreted as a
cross-platform guarantee that a service or hardware feature is available.

See [src/ReactNativeDeviceUtils.nitro.ts](src/ReactNativeDeviceUtils.nitro.ts)
for the complete signatures, enum values and platform notes.

## Process memory

`ReactNativeDeviceUtils` exposes synchronous Android/iOS methods:

```ts
getProcessMemory(key: string): string | undefined;
setProcessMemory(key: string, value: string): void;
removeProcessMemory(key: string): boolean;
setProcessMemoryIfAbsent(key: string, value: string): boolean;
```

Values are strings, including empty strings. `setProcessMemoryIfAbsent` inserts
only when the key is missing and returns whether it inserted. All operations use
the same native lock. A module recreation, JS reload, or main/background runtime
recreation retains the same native Map. Process exit discards it. No data is
persisted, and there is no implicit reset on React lifecycle events.

Use namespaced keys. For startup reporting, only main UI may claim
`analytics:startup:jsReadyTime` or `analytics:startup:uiVisibleTime`. Claim and
enqueue synchronously; remove the key if local enqueue throws. Keep the key after
enqueue succeeds; network delivery remains the existing analytics path's job.

This API requires a rebuilt native host containing the updated module. It is not
an OTA-only change. Desktop/web/extension are not implemented by this package.

Run `node tests/process-memory.cjs` from this package to compile the production
Swift/Kotlin stores, exercise concurrent claims and independent keys, and run
each executable in two fresh processes. Requires Swift, Kotlin, and Java.

## License

MIT.
