<div align="center">

# @onekeyfe/react-native-async-storage

**Asynchronous string key/value storage for OneKey on iOS, Android and Web.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-async-storage?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-async-storage)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-async-storage?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A fork of the community AsyncStorage API with OneKey's cross-runtime write
routing built in. The default export mirrors the familiar single- and multi-key
storage methods; iOS keeps a manifest plus per-key files, Android uses a SQLite
database, and Web falls back to `localStorage`. Values are strings only, so
structured data must be serialized by the caller, and the application owns key
namespaces and migration.

## Features

- **Familiar AsyncStorage surface** — `getItem`, `setItem`, `removeItem`,
  `mergeItem`, `clear`, `getAllKeys` and the `multi*` variants, each usable with a
  promise or a legacy callback.
- **One API across three platforms** — native iOS/Android implementations plus a
  `localStorage` Web implementation resolved from the same import.
- **Cross-runtime write routing** — install a forwarding getter and forwarder so
  a second JavaScript runtime can send writes to the owning runtime instead of
  writing to a stale manifest.
- **Fresher iOS reads** — the wrapper refreshes the per-runtime manifest before
  reads and local writes, so main can observe background-origin writes.
- **No native permissions** — storage lives in the application sandbox on iOS
  and in the app database on Android.

## Installation

```sh
yarn add @onekeyfe/react-native-async-storage
```

Install the application's iOS pods and rebuild the native application; the
native side is a TurboModule and is not registered by a JavaScript-only install.
Web needs no extra setup. No permissions or `Info.plist` keys are required.

## Usage

```ts
import AsyncStorage from '@onekeyfe/react-native-async-storage';

await AsyncStorage.setItem('example.preference', 'enabled');
const value = await AsyncStorage.getItem('example.preference'); // 'enabled'

await AsyncStorage.multiSet([['account.a', '1'], ['account.b', '2']]);
const pairs = await AsyncStorage.multiGet(['account.a', 'account.b']);

await AsyncStorage.removeItem('example.preference');

// Optional cross-runtime routing, installed by the owning runtime.
import {
  setAsyncStorageShouldForwardWriteGetter,
  setAsyncStorageWriteForwarder,
} from '@onekeyfe/react-native-async-storage';
```

Forwarding is opt-in: without a getter and forwarder, each runtime writes
locally to the shared native store.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `getItem(key, callback?)` | `Promise<string \| null>` | iOS, Android, Web | Reads one key; missing keys resolve `null`. |
| `setItem(key, value, callback?)` | `Promise<void>` | iOS, Android, Web | Writes one string value. |
| `removeItem(key, callback?)` | `Promise<void>` | iOS, Android, Web | Removes one key. |
| `mergeItem(key, value, callback?)` | `Promise<void>` | iOS, Android, Web | Deep-merges a JSON object string into the stored value. |
| `clear(callback?)` | `Promise<void>` | iOS, Android, Web | Removes every key in the backend. |
| `getAllKeys(callback?)` | `Promise<readonly string[]>` | iOS, Android, Web | Lists all stored keys. |
| `multiGet(keys, callback?)` | `Promise<readonly KeyValuePair[]>` | iOS, Android, Web | Reads several keys in one call. |
| `multiSet(pairs, callback?)` | `Promise<void>` | iOS, Android, Web | Writes several key/value pairs. |
| `multiRemove(keys, callback?)` | `Promise<void>` | iOS, Android, Web | Removes several keys. |
| `multiMerge(pairs, callback?)` | `Promise<void>` | iOS, Android, Web | Merges several JSON object values. |
| `flushGetRequests()` | `void` | iOS, Android, Web | Legacy batching no-op; kept for API compatibility. |
| `setAsyncStorageShouldForwardWriteGetter(getter)` / `setAsyncStorageWriteForwarder(forwarder)` | `void` | iOS, Android, Web | Chooses whether this runtime forwards writes and forwards `clear`/`multi*` writes to the owner runtime. |

See [`src/types.ts`](src/types.ts) for the method and callback types and
[`src/runtimeConfig.ts`](src/runtimeConfig.ts) for the forwarding contract.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Single-key read/write/remove | ✅ | ✅ | ✅ |
| Multi-key read/write/remove | ✅ | ✅ | ✅ |
| JSON merge (`mergeItem`, `multiMerge`) | ✅ objects only | ✅ objects only | ⚠️ arrays concatenated |
| Clear and key listing | ✅ | ✅ | ✅ |
| Cross-runtime write forwarding | ✅ | ✅ | ✅ |
| Per-runtime manifest refresh | ✅ | — | — |

## Caveats

> [!IMPORTANT]
> Values are strings. `mergeItem` and `multiMerge` expect serialized JSON objects;
> on native, non-object values and arrays are overwritten, while the Web
> implementation concatenates arrays and ignores `undefined`. Do not rely on one
> merge behavior across platforms.

> [!WARNING]
> iOS keeps one manifest cache per JavaScript runtime while the files are shared.
> Register a forwarding getter and forwarder from the owning runtime before
> background runtimes write, otherwise those writes are not serialized.

> [!NOTE]
> `clear()` removes all keys in the backend, not only the caller's namespace, and
> the Android SQLite database is configured with a 6 MB maximum size.

## References

- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>react-native-async-storage/async-storage</code></summary>

First, a sincere thank-you to Krzysztof Borowy and the `@react-native-async-storage/async-storage` maintainers for their excellent work 🙏

This package is built on, and inspired by, [react-native-async-storage/async-storage](https://github.com/react-native-async-storage/async-storage).

Our original plan was to keep our customizations as patches on top of upstream `@react-native-async-storage/async-storage`. As our product requirements evolved, the scope of those changes outgrew what we could reasonably maintain as an upstream patch set. We regret that we were unable to keep this work in patch form. As the gap grew, we ultimately forked `async-storage` to keep development and delivery stable.

### Upstream Project

- Repository: [react-native-async-storage/async-storage](https://github.com/react-native-async-storage/async-storage)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer to the upstream repository.

Thank you again to Krzysztof Borowy and everyone who contributes to `@react-native-async-storage/async-storage` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
