<div align="center">

# @onekeyfe/react-native-cloud-kit-module

**CloudKit account and private-record access for OneKey apps on iOS.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-cloud-kit-module?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-cloud-kit-module)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-cloud-kit-module?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Reads the iCloud account status and stores private records in the application's
default CloudKit container. The module wraps the private database only — there is
no public or shared database access. Records carry string `data` and `meta`
payloads that the application serializes itself. Android compiles against the same
interface but returns placeholder responses and stores nothing.

## Features

- **Account awareness** — `isAvailable()` and `getAccountInfo()` expose the
  CloudKit account status and the optional container user ID.
- **Private-record CRUD** — save, fetch, delete and existence-check records by
  type and ID inside the app's private database.
- **Typed record queries** — `queryRecords()` returns records for one type with
  stable result typing.
- **String payload boundary** — `data` and `meta` are strings; the package never
  interprets or serializes application data.
- **Input validation** — record types and IDs are validated before any CloudKit
  call, so malformed identifiers reject instead of creating odd records.
- **Timestamps in milliseconds** — results expose `createdAt`/`modifiedAt` as
  numbers.

## Installation

```sh
yarn add @onekeyfe/react-native-cloud-kit-module react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; this is a
Nitro module, so a JavaScript-only install does not register it. The iOS pod also
links `ReactNativeNativeLogger`, so the application's pods must include
`@onekeyfe/react-native-native-logger`.

The application must enable iCloud/CloudKit capabilities and provision the
container used by `CKContainer.default()`; package installation does not grant
CloudKit access. No `Info.plist` usage-description key is required.

## Usage

```ts
import { CloudKit } from '@onekeyfe/react-native-cloud-kit-module';

if (await CloudKit.isAvailable()) {
  const account = await CloudKit.getAccountInfo();

  await CloudKit.saveRecord({
    recordType: 'Backup',
    recordID: 'example-record',
    data: JSON.stringify({ version: 1 }),
    meta: JSON.stringify({ createdAt: Date.now() }),
  });

  const record = await CloudKit.fetchRecord({
    recordType: 'Backup',
    recordID: 'example-record',
  });
}
```

The caller owns the record schema, serialization format and which application
data is allowed to leave the device for iCloud.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `isAvailable()` | `Promise<boolean>` | iOS | `true` when the CloudKit account status is `available`. |
| `getAccountInfo()` | `Promise<AccountInfoResult>` | iOS | Account `status`, `statusName` and optional `containerUserId`. |
| `saveRecord(params)` | `Promise<SaveRecordResult>` | iOS | Saves `{ recordType, recordID, data, meta }` and returns `recordID` + `createdAt`. |
| `fetchRecord(params)` | `Promise<RecordResult \| null>` | iOS | Reads a record or resolves `null`. |
| `deleteRecord(params)` | `Promise<void>` | iOS | Deletes a record in the private database. |
| `recordExists(params)` | `Promise<boolean>` | iOS | Checks for a record by type and ID. |
| `queryRecords(params)` | `Promise<QueryRecordsResult>` | iOS | Returns `{ records }` for one record type. |

`SaveRecordParams`, `RecordResult`, `AccountInfoResult`, `QueryRecordsResult` and
the other parameter types are exported for adapters. See
[`src/CloudKitModule.nitro.ts`](src/CloudKitModule.nitro.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Check CloudKit availability/account | ✅ | — | — |
| Save/fetch/delete private records | ✅ | — | — |
| Record existence check | ✅ | — | — |
| Query records by type | ✅ | — | — |

## Caveats

> [!IMPORTANT]
> This is an iOS-only module. On Android every method returns a placeholder —
> `false`, `null`, an empty record or a zeroed result — without persisting
> anything. Never treat a placeholder write as a successful iCloud backup.

> [!WARNING]
> The application must configure its iCloud/CloudKit entitlement and the container
> used by `CKContainer.default()`. Availability, a successful write and later
> retrieval are separate checks; handle CloudKit rejections in the application.

> [!NOTE]
> `queryRecords()` returns at most 200 records and fetches only the `meta` field,
> so `data` is empty for query results — fetch each record for its payload.
> Record types must match `^[a-zA-Z][a-zA-Z0-9_]{0,254}$` and record IDs must be
> 1–255 characters.

## References

- [Example page](../../example/react-native/pages/CloudKitTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
