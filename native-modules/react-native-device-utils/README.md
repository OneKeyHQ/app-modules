<div align="center">

# @onekeyfe/react-native-device-utils

**Host integration for OneKey apps: launch data, device tokens, foldables, iPadOS windows and a process-wide primitive store.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-device-utils?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-device-utils)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-device-utils?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

One Nitro HybridObject that groups the small native capabilities a host app
needs but React Native does not expose: launch options and startup time, push
device tokens, foldable/dual-screen state on both platforms, iPadOS window
controls and the app menu, Android navigation-bar styling and Play Services
inspection, boot-failure recovery, and a bounded in-memory primitive store. It
is a set of host integration helpers, not a portable device API: several calls
are deliberately no-ops on the platform that cannot honor them, and the store is
process-scoped and never persisted.

## Features

- **One object, many host capabilities** — launch options, tokens, appearance,
  foldables, services and recovery behind a single imported HybridObject.
- **Shared in-memory store** — 128 entries of string/boolean/number values shared
  by the main and background runtimes, with atomic set-if-absent claims and type
  preservation.
- **Explicit listener lifecycle** — every subscription returns a numeric ID that
  its owner removes; no hidden global listeners.
- **Platform-honest** — iOS-only and Android-only calls are documented as such
  and are safe no-ops on the other platform instead of throwing.
- **Typed boundary** — enums and result objects land in TypeScript through
  Nitro-generated bindings, so adapters get compile-time checks.

## Installation

```sh
yarn add @onekeyfe/react-native-device-utils react-native-nitro-modules
```

This package ships native code. Install the app's iOS pods and rebuild the
native binary; a JavaScript-only update does not register the module. Push
tokens and launch options additionally depend on the host application's native
configuration — installing this package does not register those services.

## Usage

```ts
import { ReactNativeDeviceUtils } from '@onekeyfe/react-native-device-utils';

// Call once during startup so native event listeners exist.
ReactNativeDeviceUtils.initEventListeners();

const { launchType, deepLink } =
  await ReactNativeDeviceUtils.getLaunchOptions();
const startupTime = await ReactNativeDeviceUtils.getStartupTime();

// Claim a startup stage exactly once across runtimes.
const claimed = ReactNativeDeviceUtils.setInMemoryValueIfAbsent(
  'analytics:startup:jsReadyTime',
  Date.now(),
);
```

Consumers own key namespaces and decide what to do with each result. Store
values are native copies, not retained JS objects.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `initEventListeners()` | `void` | iOS, Android | Registers native listeners; call once at startup. |
| `getLaunchOptions()` / `clearLaunchOptions()` | `Promise<LaunchOptions>` / `Promise<boolean>` | iOS, Android | Read or consume the app's launch type and optional deep link. |
| `getStartupTime()` | `Promise<number>` | iOS, Android | Native startup timestamp. |
| `getDeviceToken()` / `saveDeviceToken(token)` / `registerDeviceToken()` | `Promise<string>` / `Promise<void>` / `Promise<boolean>` | iOS, Android | Push-token plumbing; host setup still owns registration. |
| `getInMemoryValue(key)` / `setInMemoryValue(key, value)` / `removeInMemoryValue(key)` / `setInMemoryValueIfAbsent(key, value)` | `InMemoryValue \| undefined` / `void` / `boolean` / `boolean` | iOS, Android | Synchronous bounded primitive store. |
| `getAndClearColdStartLocalNotification()` | `Promise<string>` | iOS, Android | Returns local-notification JSON once; Android returns `""`. |
| `isDualScreenDevice()` / `isSpanning()` | `boolean` | iOS, Android | Foldable detection and spanning state. |
| `getWindowRects()` / `getHingeBounds()` | `Promise<DualScreenInfoRect[]>` / `Promise<DualScreenInfoRect>` | iOS, Android | Window and hinge geometry; iOS keeps empty/zero results. |
| `getWindowControlsInsets()` + change listeners | `WindowControlsInsets` / listener ID | iOS 26+ | Room taken by system window controls; zero elsewhere. |
| `setMainMenuConfig(config)` + command listeners | `void` / listener ID | iOS (iPadOS) | Rebuilds the iPadOS app menu; Android ignores it. |
| `setNavigationBarAppearance(r, g, b, a, useDarkIcons)` | `void` | Android | Applies the navigation-bar color and icon style; no-op on iOS. |
| `getCurrentWebViewPackageInfo()` / `isGooglePlayServicesAvailable()` | `Promise<WebViewPackageInfo>` / `Promise<GooglePlayServicesStatus>` | Android | WebView package and Play Services status. |
| `getAndroidChannel()` / `getInstallerPackageName()` | `AndroidChannel` / `InstallerPackageName` | Android | Distribution channel and installer source. |
| `markBootSuccess()` / `getConsecutiveBootFailCount()` / `incrementConsecutiveBootFailCount()` / `setConsecutiveBootFailCount(count)` / `getAndClearRecoveryAction()` | varies | iOS, Android | Boot-failure counter and one-shot recovery action. |
| `exitApp()` | `void` | iOS, Android | Terminates the app process. |

See [`src/ReactNativeDeviceUtils.nitro.ts`](src/ReactNativeDeviceUtils.nitro.ts)
for full signatures, result types and enum values.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Launch options, startup time, device tokens | ✅ | ✅ | — |
| In-memory primitive store | ✅ | ✅ | — |
| Dual-screen detection and spanning listeners | ✅ foldable iPhone | ✅ | — |
| Window controls insets | ✅ iOS 26+ | — | — |
| iPadOS main menu | ✅ iPadOS | — | — |
| Navigation-bar appearance | — | ✅ | — |
| WebView / Play Services / installer inspection | — | ✅ | — |
| Boot recovery | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> The in-memory store API needs a rebuilt native host containing these bindings;
> it is not an OTA-only change. The store survives JS reloads and runtime
> recreation but not process exit, and it is never written to disk.

> [!NOTE]
> The store holds at most 128 entries. Keys are limited to 256 UTF-8 bytes and
> string values to 4096 UTF-8 bytes; overlong arguments or a write to a new key
> at capacity throw synchronously and leave the map unchanged.

> [!WARNING]
> Do not treat a successful call as proof that the underlying platform feature
> exists. Android returns `""` for cold-start local notifications, iOS ignores the
> navigation-bar call, and window-controls insets are zero outside iOS 26+.

## References

- [Package spec](docs/SPEC.md)
- [Native interface](src/ReactNativeDeviceUtils.nitro.ts)
- [Example page](../../example/react-native/pages/DeviceUtilsTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
