# @onekeyfe/react-native-async-storage

Asynchronous string key/value storage for OneKey on iOS, Android and Web.
The wrapper exposes familiar AsyncStorage operations together with application
hooks for routing writes between JavaScript runtimes.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-async-storage
```

Install the application's iOS pods and rebuild the native application.

```ts
import AsyncStorage from '@onekeyfe/react-native-async-storage';

await AsyncStorage.setItem('example.preference', 'enabled');
const value = await AsyncStorage.getItem('example.preference');
await AsyncStorage.removeItem('example.preference');
```

## API and runtime integration

The default export exposes `getItem`, `setItem`, `removeItem`, `mergeItem`,
`clear`, `getAllKeys`, `multiGet`, `multiSet`, `multiRemove` and `multiMerge`.
Store strings; serialize structured values explicitly. Missing items return
`null`, and operations can reject. `clear` removes all keys in the storage
backend, not just the caller's prefix.

`setAsyncStorageShouldForwardWriteGetter` and `setAsyncStorageWriteForwarder`
configure application-owned write routing. When forwarding is enabled, the
forwarder must be configured. iOS refreshes its per-runtime manifest around
reads and writes; this does not make uninjected writes cross-runtime serialized.
The Web implementation uses localStorage.

See [src/types.ts](src/types.ts) for method and callback types and
[src/runtimeConfig.ts](src/runtimeConfig.ts) for the forwarding contract.
Storage ownership, key namespaces and migration remain the caller's responsibility.

## Upstream attribution

First, a sincere thank-you to Krzysztof Borowy and the
`@react-native-async-storage/async-storage` maintainers for their excellent
work 🙏

This package is built on, and inspired by,
[react-native-async-storage/async-storage](https://github.com/react-native-async-storage/async-storage).

Our original plan was to keep our customizations as patches on top of upstream
`@react-native-async-storage/async-storage`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `async-storage` to keep
development and delivery stable.

## Upstream Project

- Repository: [react-native-async-storage/async-storage](https://github.com/react-native-async-storage/async-storage)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Krzysztof Borowy and everyone who contributes to
`@react-native-async-storage/async-storage` 💙
