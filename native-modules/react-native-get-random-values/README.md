# @onekeyfe/react-native-get-random-values

OneKey's React Native `crypto.getRandomValues` integration and native random
byte interface. Importing the package installs the global function only when
it is not already defined.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-get-random-values react-native-nitro-modules
```

The current wrapper also loads `expo-crypto` and `fast-base64-decode`; configure
the consumer's existing dependencies accordingly. Install iOS pods and rebuild
the native application before using the native path.

```ts
import '@onekeyfe/react-native-get-random-values';

const bytes = new Uint8Array(16);
crypto.getRandomValues(bytes);
```

## API and limits

The global function fills the provided integer typed array in place and returns
it. Floating-point arrays are rejected. One call may request at most 65,536
bytes; requests above that size throw a quota error.

The wrapper prefers Expo Crypto when available, otherwise it uses the native
`getRandomBase64(byteLength)` method. A development debugger without synchronous
native-call support has an explicitly insecure `Math.random` fallback and emits
a warning. That fallback must not be mistaken for the native random-byte path.

The named `ReactNativeGetRandomValues` export exposes the Nitro object on native
platforms. See [src/index.tsx](src/index.tsx) for path selection and validation,
and [src/ReactNativeGetRandomValues.nitro.ts](src/ReactNativeGetRandomValues.nitro.ts)
for the native method signature.

## Upstream attribution

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

## Upstream Project

- Repository: [LinusU/react-native-get-random-values](https://github.com/LinusU/react-native-get-random-values)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Linus Unnebäck and everyone who contributes to
`react-native-get-random-values` 💙
