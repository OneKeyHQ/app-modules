<div align="center">

# @onekeyfe/react-native-cloud-fs

**Cloud file access for OneKey apps: iCloud on iOS, Google Drive on Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-cloud-fs?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-cloud-fs)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-cloud-fs?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Wraps the platform cloud provider behind one TurboModule: iCloud Drive access on
iOS and Google Drive access on Android. Both sides expose availability,
listing, existence checks, copy/write and delete; iOS adds iCloud document
reading and ubiquity sync, Android adds Google sign-in, sign-out, Drive document
reading and signed-in user data.

## Features

- **iCloud Drive operations** — availability, directory listing, document read,
  create, copy, delete and a metadata sync that downloads non-local items.
- **Google Drive operations** — sign-in/sign-out, listing, copy, document read,
  delete and signed-in user data via the Drive app-data or root folder.
- **One import for both providers** — the platform decides whether calls hit
  iCloud or Google Drive; callers branch on `Platform.OS`.
- **Explicit scope** — `scope` selects the user-visible Documents/root area
  (`visible`) versus the private app-data container.
- **Typed interface** — `CloudFsSpec` is exported for adapters, and the named
  `CloudFs` and default export are the same object.

## Installation

```sh
yarn add @onekeyfe/react-native-cloud-fs
```

Install the application's iOS pods and rebuild the native binary. Package
installation alone grants no cloud access: iOS needs iCloud capability with a
ubiquity container entitlement, and Android needs a Google API/OAuth client with
the Drive app-data scope plus Google Sign-In configured. No runtime permission is
declared by the package itself.

## Usage

```ts
import { Platform } from 'react-native';
import { CloudFs } from '@onekeyfe/react-native-cloud-fs';

if (Platform.OS === 'ios') {
  if (await CloudFs.isAvailable()) {
    await CloudFs.copyToCloud({
      scope: 'visible',
      sourcePath: { path: '/local/backup.zip' },
      targetPath: 'backup.zip',
      mimetype: 'application/zip',
    });
  }
} else {
  await CloudFs.loginIfNeeded();
  const { files } = await CloudFs.listFiles({ scope: 'visible' });
}
```

The application owns upload selection, naming and failure handling.

## API

| Method | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `isAvailable()` | `() => Promise<boolean>` | iOS | Whether an iCloud container is reachable; Android resolves `false`. |
| `syncCloud()` | `() => Promise<boolean>` | iOS | Starts an iCloud metadata query and downloads non-local items. |
| `listFiles(options)` | `({ scope, targetPath? }) => Promise<{ files }>` | iOS, Android | Lists files; iOS entries carry `path`/`uri`/`size`, Android entries carry `id`. |
| `fileExists(options)` | `({ fileId?, targetPath?, scope? }) => Promise<boolean>` | iOS, Android | Checks an iCloud path on iOS or a Drive file ID on Android. |
| `copyToCloud(options)` | `({ scope, sourcePath, targetPath, mimetype? }) => Promise<string>` | iOS, Android | Copies a local/URI source into the cloud; resolves a path or Drive file ID. |
| `createFile(options)` | `({ targetPath, content, scope? }) => Promise<string>` | iOS | Creates an iCloud file from UTF-8 content. |
| `deleteFromCloud(item)` | `({ id, path? }) => Promise<boolean>` | iOS, Android | Deletes by iCloud path on iOS or Drive ID on Android. |
| `getIcloudDocument(filename)` | `(filename) => Promise<string>` | iOS | Reads an iCloud document as a string. |
| `loginIfNeeded()` | `() => Promise<boolean>` | Android | Starts or reuses a Google sign-in session; iOS resolves `false`. |
| `logout()` | `() => Promise<boolean>` | Android | Signs out of Google; iOS resolves `false`. |
| `getGoogleDriveDocument(fileId)` | `(fileId) => Promise<string>` | Android | Reads a Drive file as a string; iOS rejects `NOT_AVAILABLE`. |
| `getCurrentlySignedInUserData()` | `() => Promise<{ email, name, avatarUrl } \| null>` | Android | Returns the signed-in Google account, or `null`; iOS resolves `null`. |

See [`src/NativeCloudFs.ts`](src/NativeCloudFs.ts) for the complete option and result shapes; Google methods reject with `NOT_LOGGED_IN` until `loginIfNeeded()` succeeds.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| iCloud availability, sync, list/read/create/copy/delete | ✅ | — | — |
| Google Drive sign-in and sign-out | — | ✅ | — |
| Google Drive list/read/copy/delete | — | ✅ | — |
| Signed-in Google user data | — | ✅ | — |

## Caveats

> [!IMPORTANT]
> The same method names target different providers: iOS uses iCloud paths, Android
> uses Google Drive file IDs in the root or app-data folder. Branch on
> `Platform.OS`, never reuse one platform's identifier on the other, and note that
> iOS `listFiles` entries expose `path`/`uri` while Android exposes `id`.

> [!WARNING]
> Installing the package grants no cloud access. iOS requires the app's iCloud
> capability and ubiquity container entitlement; Android requires a Google
> OAuth client, Google Sign-In configuration and the Drive app-data scope. Calls
> before sign-in reject with `NOT_LOGGED_IN`, and a failed authorization surfaces
> as a rejected promise.

> [!NOTE]
> iOS-only methods on Android reject `NOT_AVAILABLE` (`isAvailable()` resolves
> `false`), and Android-only methods on iOS resolve `false` or `null`. A successful
> availability check does not prove a later transfer completed.

## References

- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>npomfret/react-native-cloud-fs</code></summary>

First, a sincere thank-you to the `react-native-cloud-fs` maintainers for their excellent work 🙏

This package is built on, and inspired by, [npomfret/react-native-cloud-fs](https://github.com/npomfret/react-native-cloud-fs).

Our original plan was to keep our customizations as patches on top of upstream `react-native-cloud-fs`. As our product requirements evolved, the scope of those changes outgrew what we could reasonably maintain as an upstream patch set. We regret that we were unable to keep this work in patch form. As the gap grew, we ultimately forked `react-native-cloud-fs` to keep development and delivery stable.

### Upstream Project

- Repository: [npomfret/react-native-cloud-fs](https://github.com/npomfret/react-native-cloud-fs)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer to the upstream repository.

Thank you again to everyone who contributes to `react-native-cloud-fs` 💙

The upstream MIT notice is preserved verbatim in [LICENSE](LICENSE). Its copyright line retains upstream year/name placeholders; no replacement author or year is inferred here.

</details>

## License

MIT. See [LICENSE](LICENSE).
