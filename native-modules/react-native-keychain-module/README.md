# @onekeyfe/react-native-keychain-module

An iOS Keychain interface for storing and retrieving string values in OneKey
React Native applications. The native implementation uses the application's
bundle identifier as its service and supports optional iCloud synchronization.
The application owns key naming, data serialization and removal policy.

## Installation

```sh
yarn add @onekeyfe/react-native-keychain-module react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. iCloud
Keychain availability depends on the device account and application entitlement
configuration; this package does not configure either of those.

## Usage

```ts
import { KeychainModule } from '@onekeyfe/react-native-keychain-module';

await KeychainModule.setItem({
  key: 'example.preference',
  value: 'enabled',
  enableSync: false,
});
const item = await KeychainModule.getItem({ key: 'example.preference' });
await KeychainModule.removeItem({ key: 'example.preference' });
```

## API and platform behavior

| Method | Result |
| --- | --- |
| `setItem(params)` | Store a string; accepts optional sync, label and description. |
| `getItem({ key })` | Return `{ key, value }` or `null` when absent. |
| `hasItem({ key })` | Return whether an item exists. |
| `removeItem({ key })` | Remove the item. |
| `isICloudSyncEnabled()` | Probe whether synchronizable Keychain storage is available. |

On iOS, omitted `enableSync` defaults to `true`; pass it explicitly when storage
must remain local. Handle Keychain errors as promise rejections.
Android is a stub: writes/removals resolve without persistence, reads return
`null`, and existence/sync checks return `false`. Do not use this package as
Android secure storage or infer a successful backup from a resolved stub call.

The complete types are in [src/KeychainModule.nitro.ts](src/KeychainModule.nitro.ts).

## License

MIT.
