<div align="center">

# @onekeyfe/react-native-keychain-module

**iOS Keychain string storage for OneKey, device-local or iCloud-synchronized.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-keychain-module?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-keychain-module)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-keychain-module?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Stores, reads, checks and removes string values in the iOS Keychain through a
small Nitro HybridObject. Items are generic passwords in the application's
bundle-identifier service, stored with `kSecAttrAccessibleWhenUnlocked`, and can
be made synchronizable across the user's devices. This is an iOS package: the
Android binding in the package is a stub that persists nothing. Key naming,
value serialization and removal policy belong to the application.

## Features

- **Upsert semantics** — `setItem` adds a new item or updates the existing one
  in place, with no delete-then-add race window.
- **Per-item sync choice** — `enableSync` selects iCloud-synchronizable storage;
  pass `false` to keep a value on this device only.
- **Local and synced lookups** — `getItem`, `hasItem` and `removeItem` match
  synchronizable and non-synchronizable items alike.
- **Availability probe** — `isICloudSyncEnabled()` reports whether the app can
  actually write a synchronizable item right now.
- **Typed parameters** — exported `SetItemParams`, `GetItemParams` and result
  types keep consumer adapters type-safe.

## Installation

```sh
yarn add @onekeyfe/react-native-keychain-module react-native-nitro-modules
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

This is an iOS-native package. Install the application's iOS pods and rebuild
the native binary; a JavaScript-only update does not register the module.

iCloud Keychain synchronization needs the app's iCloud Keychain entitlement and
a signed-in iCloud account. This package configures neither; when either is
missing, synchronizable writes fail and `isICloudSyncEnabled()` returns `false`.

## Usage

```ts
import { KeychainModule } from '@onekeyfe/react-native-keychain-module';

await KeychainModule.setItem({
  key: 'example.preference',
  value: 'enabled',
  enableSync: true,
  label: 'Example preference',
});

const item = await KeychainModule.getItem({ key: 'example.preference' });
if (item) {
  // item.value is the stored string.
}

await KeychainModule.removeItem({ key: 'example.preference' });
```

`enableSync` defaults to `true` on iOS, so omit it only when the value may leave
the device. Serialize structured values yourself and handle parse failures above
this storage boundary.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `setItem(params)` | `Promise<void>` | iOS | Stores or updates a string; `enableSync` defaults to `true`, `label`/`description` are optional. |
| `getItem({ key })` | `Promise<{ key, value } \| null>` | iOS | Returns the item, or `null` when absent. |
| `hasItem({ key })` | `Promise<boolean>` | iOS | Whether an item exists. |
| `removeItem({ key })` | `Promise<void>` | iOS | Removes the item; a missing item is not an error. |
| `isICloudSyncEnabled()` | `Promise<boolean>` | iOS | Probes whether synchronizable Keychain storage is available. |
| `SetItemParams`, `GetItemParams`, `RemoveItemParams`, `HasItemParams`, `GetItemResult` | types | iOS | Parameter and result types for consumer adapters. |

Failures reject as promises. Native failures carry the `OSStatus` code in the
`NSOSStatusErrorDomain`; encoding and wrapper failures use the
`keychain_*_error` domains. See
[`src/KeychainModule.nitro.ts`](src/KeychainModule.nitro.ts) for the complete
interface.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Persist a string in the Keychain | ✅ | — | — |
| Read, check and remove items | ✅ | — | — |
| iCloud-synchronizable items | ✅ with entitlement | — | — |
| `isICloudSyncEnabled()` probe | ✅ | — | — |

On Android the module compiles as a stub: writes and removals resolve without
persisting, reads return `null`, and existence/sync checks return `false`. Do not
use it as Android secure storage.

## Caveats

> [!IMPORTANT]
> `enableSync` defaults to `true`. Omit it and an item is eligible to sync to the
> user's other devices through iCloud Keychain. Pass `enableSync: false`
> explicitly for device-local secrets.

> [!WARNING]
> The Android binding is a stub with no persistence: a resolved `setItem` or
> `removeItem` proves nothing. Keep Android secure-storage code out of this
> package, and never infer a successful backup from a stub call.

> [!NOTE]
> `getItem` returns an object, not the string, and an empty-string value is a
> present value. `hasItem` is a separate promise and cannot make a later read
> atomic.

## References

- [Native interface](src/KeychainModule.nitro.ts)
- [Example page](../../example/react-native/pages/KeychainTestPage.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-keychain-module)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
