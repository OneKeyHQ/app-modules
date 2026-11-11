<div align="center">

# @onekeyfe/react-native-get-random-values

**A native-backed `crypto.getRandomValues` for React Native, installed only when the global is missing.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-get-random-values?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-get-random-values)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-get-random-values?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Importing this package fills `global.crypto.getRandomValues` when the runtime
does not already provide it, backed by platform crypto rather than JavaScript
`Math.random`. It prefers `expo-crypto` when Expo modules are present, falls back
to a Nitro `getRandomBase64(byteLength)` method, and leaves an existing global
untouched. The package also exports that native method directly for callers that
only want base64-encoded random bytes.

## Features

- **Drop-in global** — one side-effect import installs `crypto.getRandomValues`;
  an already-defined implementation is never replaced.
- **Platform crypto** — expo-crypto first, then `SecRandomCopyBytes` on iOS and
  `SecureRandom` on Android through Nitro.
- **Typed-array contract** — fills the caller's integer array in place and
  returns it; floating-point arrays and other values throw a type error.
- **Quota guard** — the global rejects requests above 65,536 bytes and the
  native method accepts only integer lengths in `1...65536`.
- **Debugger-aware fallback** — a debugger without synchronous native calls gets
  an explicit insecure `Math.random` fallback plus a console warning.

## Installation

```sh
yarn add @onekeyfe/react-native-get-random-values react-native-nitro-modules
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

`expo-crypto` and `fast-base64-decode` are installed as runtime dependencies of
this package. The wrapper expects Expo modules: a bare React Native app must
[install and configure Expo modules](https://docs.expo.dev/bare/installing-expo-modules/)
first. Install the application's iOS pods and rebuild the native binary before
using the native path.

## Usage

```ts
// Side-effect import: installs crypto.getRandomValues only if missing.
import '@onekeyfe/react-native-get-random-values';

const bytes = new Uint8Array(16);
crypto.getRandomValues(bytes);
```

The function fills the array in place and returns the same array. Keep using
`crypto.getRandomValues` rather than caching a reference at module scope, so the
check stays meaningful in every runtime.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `crypto.getRandomValues(array)` | `<T extends IntegerArray>(array: T) => T` | iOS, Android | Global installed on import when absent; fills the array with random bytes. |
| `ReactNativeGetRandomValues.getRandomBase64(byteLength)` | `(byteLength: number) => string` | iOS, Android | Native base64-encoded random bytes; length must be an integer in `1...65536`. |
| `ReactNativeGetRandomValues` | Nitro HybridObject | iOS, Android | Direct access to the native random-byte method. |

The global accepts `Int8Array`, `Uint8Array`, `Uint8ClampedArray`, `Int16Array`,
`Uint16Array`, `Int32Array` and `Uint32Array`. A request over 65,536 bytes throws
a quota error; any other value throws a type-mismatch error. On a debugger where
synchronous native calls are unavailable, the wrapper warns once and fills the
array with `Math.random` output.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Install `crypto.getRandomValues` when missing | ✅ | ✅ | — |
| Native random bytes through Nitro | ✅ `SecRandomCopyBytes` | ✅ `SecureRandom` | — |
| 65,536-byte quota enforcement | ✅ | ✅ | — |

## Caveats

> [!WARNING]
> The `Math.random` fallback is not cryptographically secure. It exists only for
> a debugger without synchronous native-call support, logs a warning, and must
> never be treated as the native random-byte path in production.

> [!IMPORTANT]
> The wrapper requires Expo modules because it prefers `expo-crypto`. In a bare
> React Native app, configure Expo modules and rebuild before relying on the
> native path; a JavaScript-only install is not enough.

## References

- [Public interface](src/ReactNativeGetRandomValues.nitro.ts)
- [Global installation logic](src/index.tsx)
- [Example page](../../example/react-native/pages/GetRandomValuesTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>LinusU/react-native-get-random-values</code> (MIT)</summary>

First, a sincere thank-you to Linus Unnebäck and the
`react-native-get-random-values` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[LinusU/react-native-get-random-values](https://github.com/LinusU/react-native-get-random-values).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-get-random-values`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-get-random-values` to keep
development and delivery stable.

### Upstream Project

- Repository: [LinusU/react-native-get-random-values](https://github.com/LinusU/react-native-get-random-values)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Linus Unnebäck and everyone who contributes to
`react-native-get-random-values` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
