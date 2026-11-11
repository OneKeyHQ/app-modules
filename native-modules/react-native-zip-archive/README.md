<div align="center">

# @onekeyfe/react-native-zip-archive

**Native ZIP extraction and creation for OneKey apps on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-zip-archive?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-zip-archive)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-zip-archive?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Extracts archives and creates new ones without leaving the app process. iOS
wraps `SSZipArchive`; Android uses `java.util.zip`. Named functions keep the
legacy `react-native-zip-archive` call shapes, and `ZipArchive` exposes the
underlying Nitro object. All paths are local file paths — the package does not
download archives or verify their authenticity. There is no Web implementation.

## Features

- **Extract and create** — `unzip`, `unzipWithPassword`, `zipFolder` and
  `zipFiles` all resolve with the destination path.
- **Password support on iOS** — protected archives can be detected and extracted;
  the pod pulls an `SSZipArchive` version that includes the zip-slip fixes.
- **Path-traversal defense** — Android canonicalizes every entry against the
  destination directory and rejects entries that escape it.
- **Size inspection** — `getUncompressedSize()` reports the total uncompressed
  bytes for disk budgeting before extraction.
- **Legacy-compatible exports** — `zip` aliases `zipFolder`, and the
  `ZipArchiveSpec` type remains importable for type-only consumers.
- **No retained native state** — each call operates on caller-supplied paths.

## Installation

```sh
yarn add @onekeyfe/react-native-zip-archive react-native-nitro-modules
```

Install the app's iOS pods and rebuild the native binary; Android links through
autolinking. `react-native-nitro-modules` is a required peer dependency. On iOS,
CocoaPods resolves `SSZipArchive` through the podspec.

## Usage

```ts
import {
  getUncompressedSize,
  unzip,
  isPasswordProtected,
} from '@onekeyfe/react-native-zip-archive';

const bytes = await getUncompressedSize('/absolute/path/archive.zip');
if (bytes >= 0 && !(await isPasswordProtected('/absolute/path/archive.zip'))) {
  const destination = await unzip(
    '/absolute/path/archive.zip',
    '/absolute/path/output',
  );
}
```

The application owns destination selection, disk-space budgeting, trust
decisions and cleanup after a failed extraction. Use a local file that already
exists; these APIs do not fetch anything.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `unzip` | `(from: string, to: string) => Promise<string>` | iOS, Android | Extracts an archive into `to`; resolves `to`. |
| `unzipWithPassword` | `(from: string, to: string, password: string) => Promise<string>` | iOS only | Extracts a password-protected archive; Android rejects. |
| `isPasswordProtected` | `(file: string) => Promise<boolean>` | iOS, Android | iOS inspects the archive; Android always resolves `false`. |
| `zipFolder` | `(from: string, to: string) => Promise<string>` | iOS, Android | Zips a directory; archive layout differs by platform (see caveats). |
| `zipFiles` | `(files: string[], to: string) => Promise<string>` | iOS, Android | Zips an explicit list of local files. |
| `zip` | `(source: string, target: string) => Promise<string>` | iOS, Android | Legacy alias for `zipFolder`. |
| `getUncompressedSize` | `(path: string) => Promise<number>` | iOS, Android | Total uncompressed bytes; iOS resolves `-1` on failure. |
| `ZipArchive` | Nitro hybrid object | iOS, Android | Raw instance exposing the same six methods. |

`ZipArchiveSpec` and the `ReactNativeZipArchive` interface type are re-exported
from [`src/index.tsx`](src/index.tsx); native signatures live in
[`src/ReactNativeZipArchive.nitro.ts`](src/ReactNativeZipArchive.nitro.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Extract an archive | ✅ | ✅ | — |
| Extract a password-protected archive | ✅ | — | — |
| Detect password protection | ✅ | ⚠️ always `false` | — |
| Create an archive from a directory | ✅ contents only | ✅ includes the folder name | — |
| Create an archive from a file list | ✅ | ✅ | — |
| Report uncompressed size | ✅ `-1` on failure | ✅ rejects on failure | — |
| 512 MiB uncompressed-size pre-check | ✅ | — | — |

## Caveats

> [!IMPORTANT]
> Password-protected archives are iOS-only. On Android `isPasswordProtected()`
> always resolves `false` and `unzipWithPassword()` rejects; callers must not use
> it as a real capability probe.

> [!WARNING]
> `zipFolder()` output differs by platform: iOS zips the directory's contents at
> the archive root, while Android stores them under a top-level folder named
> after the source directory. Compare or verify archive structure before relying
> on a fixed layout.

> [!NOTE]
> iOS rejects an archive whose uncompressed payload exceeds 512 MiB before
> extraction; Android has no equivalent pre-check and streams entries as it goes.
> `getUncompressedSize()` returns `-1` for an unreadable archive on iOS but
> rejects on Android.

## References

- [Example page](../../example/react-native/pages/ZipArchiveTestPage.tsx)
- [Native interface](src/ReactNativeZipArchive.nitro.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-zip-archive)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Fork history and upstream project</summary>

First, a sincere thank-you to Mockingbot and the `react-native-zip-archive`
maintainers for their excellent work 🙏

This package is built on, and inspired by,
[mockingbot/react-native-zip-archive](https://github.com/mockingbot/react-native-zip-archive).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-zip-archive`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-zip-archive` to keep
development and delivery stable.

### Upstream Project

- Repository: [mockingbot/react-native-zip-archive](https://github.com/mockingbot/react-native-zip-archive)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Mockingbot and everyone who contributes to
`react-native-zip-archive` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
