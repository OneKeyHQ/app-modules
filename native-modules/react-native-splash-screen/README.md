# @onekeyfe/react-native-splash-screen

Native splash-screen visibility controls for OneKey on iOS and Android.
The Nitro interface lets application startup retain the splash screen and hide
it once the application's own readiness conditions are met.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-splash-screen react-native-nitro-modules
```

Configure the application's native splash resources and host integration,
install iOS pods and rebuild the native application. Installing this package
does not create the launch screen artwork or application startup sequence.

```ts
import { ReactNativeSplashScreen } from '@onekeyfe/react-native-splash-screen';

await ReactNativeSplashScreen.preventAutoHideAsync();
// Complete the application's existing startup/readiness work.
await ReactNativeSplashScreen.hideAsync();
```

## API and ownership

Both `preventAutoHideAsync()` and `hideAsync()` return `Promise<boolean>`.
The application owns when each method is invoked and handles their outcomes;
the module does not decide whether data loading, navigation or rendering has
completed.

Call startup retention early enough for the consumer's native splash lifecycle.
Validate the actual cold-start transition on each platform, including failure
and retry paths in the application. A resolved visibility call does not prove
that the first application frame is ready or that launch performance improved.

The exported interface is in
[src/ReactNativeSplashScreen.nitro.ts](src/ReactNativeSplashScreen.nitro.ts).

## Upstream attribution

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

## Upstream Project

- Repository: [crazycodeboy/react-native-splash-screen](https://github.com/crazycodeboy/react-native-splash-screen)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Crazycodeboy and everyone who contributes to
`react-native-splash-screen` 💙
