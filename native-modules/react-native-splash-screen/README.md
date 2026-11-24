<div align="center">

# @onekeyfe/react-native-splash-screen

**Keep the launch splash on screen until the app's startup work is ready.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-splash-screen?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-splash-screen)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-splash-screen?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A Nitro module exposing two startup calls: retain the splash screen and hide it
when the application decides it is ready. On Android 12+ the system AndroidX
SplashScreen API owns the splash and both calls are no-ops that resolve `true`;
on Android 11 and below the host Activity displays a full-screen overlay that the
module keeps visible or removes. On iOS the module is intentionally inert — the
app's launch storyboard (`expo-splash-screen` in OneKey) owns the splash — so
both calls resolve `true` without changing visibility. No Web implementation.

## Features

- **Explicit retention** — `preventAutoHideAsync()` disables the automatic hide
  when the React root mounts; without it, the overlay is removed as soon as the
  root gets its first child.
- **Explicit release** — `hideAsync()` removes the legacy Android overlay and
  resolves whether the call actually had an effect.
- **Honest result values** — missing activity, missing controller and destroyed
  activities resolve `false` instead of pretending success.
- **Artwork stays app-owned** — the package draws the app's existing splash
  drawable and background color; it ships no splash assets.

## Installation

```sh
yarn add @onekeyfe/react-native-splash-screen react-native-nitro-modules @onekeyfe/react-native-native-logger
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. The native code logs through `OneKeyLog` from
`@onekeyfe/react-native-native-logger`, so that package must be installed too.

**Android (API < 31).** The host Activity must display the overlay itself. Call `SplashScreenBridge.show`
from `MainActivity.onCreate()` before React Native mounts:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
    com.margelo.nitro.reactnativesplashscreen.SplashScreenBridge.show(this)
  }
}
```

The overlay reads these app resources when present: `splashscreen_image` (or
`splashscreen` in `native` mode), `splashscreen_background` (white when absent)
and the optional string `expo_splash_screen_resize_mode` — `contain` (default),
`cover` or `native`.

**iOS.** Installing the pod is enough; the module performs no visibility work
because the app's launch storyboard is the splash screen.

## Usage

```ts
import { ReactNativeSplashScreen } from '@onekeyfe/react-native-splash-screen';

await ReactNativeSplashScreen.preventAutoHideAsync();
// Await the application's own startup/readiness work.
await ReactNativeSplashScreen.hideAsync();
```

The application owns when each method is invoked; the module is not coupled to
data loading, navigation or the first JavaScript render.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `preventAutoHideAsync` | `() => Promise<boolean>` | iOS, Android | Disables auto-hide; `false` when Android has no current Activity. |
| `hideAsync` | `() => Promise<boolean>` | iOS, Android | Removes the legacy Android overlay; `true` only when it had an effect. |

`preventAutoHideAsync()` and `hideAsync()` are independent asynchronous calls;
await them when ordering matters. On iOS both resolve `true` immediately. See
[`src/ReactNativeSplashScreen.nitro.ts`](src/ReactNativeSplashScreen.nitro.ts)
for the interface and
[`SplashScreenBridge.kt`](android/src/main/java/com/margelo/nitro/reactnativesplashscreen/SplashScreenBridge.kt)
for the Android overlay.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Retain splash until explicit hide | — app-owned | ✅ API < 31 | — |
| Hide splash on demand | — no-op | ✅ API < 31 | — |
| Auto-hide when React root mounts | — app-owned | ✅ API < 31 | — |

## Caveats

> [!IMPORTANT]
> On iOS both methods are intentional no-ops that resolve `true`. A resolved call
> does not prove the splash screen was visible, retained or hidden, and it does
> not prove the first application frame is ready.

> [!NOTE]
> On Android 12+ the AndroidX SplashScreen API handles the launch screen and the
> module returns `true` without touching it.

> [!WARNING]
> `hideAsync()` resolves `false` when the overlay was never shown or the Activity
> is already destroyed; confirm `SplashScreenBridge.show()` ran first.

## References

- [Example page](../../example/react-native/pages/SplashScreenTestPage.tsx)
- [Native interface](src/ReactNativeSplashScreen.nitro.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-splash-screen)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Fork history and upstream project</summary>

First, a sincere thank-you to Crazycodeboy and the
`react-native-splash-screen` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[crazycodeboy/react-native-splash-screen](https://github.com/crazycodeboy/react-native-splash-screen).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-splash-screen`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-splash-screen` to keep
development and delivery stable.

### Upstream Project

- Repository: [crazycodeboy/react-native-splash-screen](https://github.com/crazycodeboy/react-native-splash-screen)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Crazycodeboy and everyone who contributes to
`react-native-splash-screen` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
