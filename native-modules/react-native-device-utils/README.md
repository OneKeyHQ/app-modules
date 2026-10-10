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

## Consumed launch data and listener cleanup

`getAndClearColdStartLocalNotification()` returns serialized local-notification
user info on iOS and consumes that value. An empty string means there is no
pending payload. Parse it only after checking for an empty result and avoid
logging notification contents or stored device tokens.

`addSpanningChangedListener` returns a numeric ID; retain it for
`removeSpanningChangedListener`. `getLaunchOptions()` returns a `LaunchOptions`
object with `launchType` and optional `deepLink`, rather than a raw URL string.
Use the exported result types for WebView, Google Play Services and hinge/window
geometry when building a platform adapter.

## License

MIT.
