# @onekeyfe/react-native-zip-archive

Native ZIP archive operations for OneKey on iOS and Android. The Nitro module
extracts archives, creates archives from directories or explicit file lists,
and inspects password protection and uncompressed size.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-zip-archive react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application.

```ts
import { unzip, getUncompressedSize } from '@onekeyfe/react-native-zip-archive';

const bytes = await getUncompressedSize('/absolute/path/archive.zip');
const directory = await unzip('/absolute/path/archive.zip', '/absolute/path/output');
```

## API and caller responsibilities

Named functions include `isPasswordProtected`, `unzip`, `unzipWithPassword`,
`zipFolder`, `zipFiles` and `getUncompressedSize`. `zip` aliases `zipFolder`;
`ZipArchive` exposes the underlying Nitro object. Extraction and creation
methods resolve with the destination path.

`getUncompressedSize` returns bytes, or -1 when inspection fails. File paths are
local paths rather than remote URLs. The application owns destination selection,
disk-space budgeting, trust decisions and cleanup after failed operations.
Archive extraction alone does not verify an update's authenticity or integrity.

Handle promise failures and inspect the destination through the consumer's
existing validation flow before using extracted content. The full signatures
are in [src/ReactNativeZipArchive.nitro.ts](src/ReactNativeZipArchive.nitro.ts),
and [src/index.tsx](src/index.tsx) documents the compatibility exports.

## Choosing an archive operation

`zipFolder(from, to)` archives a directory, while `zipFiles(files, to)` takes an
explicit array of local file paths. Both resolve with the destination string.
The `zip(source, target)` compatibility name calls `zipFolder`, not `zipFiles`.
`unzipWithPassword(from, to, password)` is separate from ordinary `unzip`.

`isPasswordProtected(file)` returns a boolean through a promise; it is not an
authenticity check. A failed size inspection returns `-1`, which must not be
used as a valid byte budget. Type-only consumers can import `ZipArchiveSpec`
without initializing the native hybrid object.

## Upstream attribution

First, a sincere thank-you to Mockingbot and the
`react-native-zip-archive` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[mockingbot/react-native-zip-archive](https://github.com/mockingbot/react-native-zip-archive).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-zip-archive`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-zip-archive` to keep
development and delivery stable.

## Upstream Project

- Repository: [mockingbot/react-native-zip-archive](https://github.com/mockingbot/react-native-zip-archive)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Mockingbot and everyone who contributes to
`react-native-zip-archive` 💙
