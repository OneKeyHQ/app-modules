# @onekeyfe/react-native-cloud-kit-module

CloudKit account and private-record access for OneKey React Native applications.
The iOS implementation uses the application's default CloudKit container and
its private database. The caller owns record schema, serialization and deciding
which application data may be stored in iCloud.

## Installation

```sh
yarn add @onekeyfe/react-native-cloud-kit-module react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. Configure
the application's iCloud/CloudKit entitlements and container before using the
module; package installation does not provision them.

## Usage

```ts
import { CloudKit } from '@onekeyfe/react-native-cloud-kit-module';

if (await CloudKit.isAvailable()) {
  const account = await CloudKit.getAccountInfo();
  const record = await CloudKit.fetchRecord({
    recordType: 'Backup',
    recordID: 'example-record',
  });
}
```

## API

| Method | Purpose |
| --- | --- |
| `isAvailable()` | Check iCloud account availability. |
| `getAccountInfo()` | Return account status and optional container user ID. |
| `saveRecord({ recordType, recordID, data, meta })` | Store string data and metadata. |
| `fetchRecord({ recordType, recordID })` | Read a record or return `null`. |
| `recordExists({ recordType, recordID })` | Check for a record. |
| `deleteRecord({ recordType, recordID })` | Remove a record. |
| `queryRecords({ recordType })` | Return records matching a type. |

Records include IDs, type, string data/metadata and creation/modification times.
Handle promise failures in the application, including unavailable accounts and
CloudKit errors. Android exposes placeholder methods and reports unavailable;
it does not provide CloudKit storage. Do not treat a placeholder write result
as a persisted backup.

See [src/CloudKitModule.nitro.ts](src/CloudKitModule.nitro.ts) for the exported
parameter and result types.

## License

MIT.
