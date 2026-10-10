# @onekeyfe/react-native-cloud-fs

Cloud file access for OneKey React Native applications. The native interface
supports cloud availability checks, listing, copying, creating and deleting
files, with iCloud-specific and Google Drive-specific methods.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-cloud-fs
```

Install the application's iOS pods and rebuild its native binary. The consumer
must configure its cloud provider, native entitlements and authentication flow.
Package installation alone does not grant cloud access.

```ts
import { CloudFs } from '@onekeyfe/react-native-cloud-fs';

const available = await CloudFs.isAvailable();
```

## API and platforms

Shared methods include `syncCloud`, `listFiles`, `deleteFromCloud`, `fileExists`,
`copyToCloud` and `createFile`. Their parameters use explicit provider scope,
paths or file IDs; the application owns those identifiers and file contents.

`getIcloudDocument` is iOS-specific. Android exposes `loginIfNeeded`, `logout`,
`getGoogleDriveDocument` and `getCurrentlySignedInUserData` for its Google Drive
integration. Route calls to the appropriate provider and handle promise failures
such as missing account access or unavailable files.

The package exports both the named `CloudFs` and the default native module.
See [src/NativeCloudFs.ts](src/NativeCloudFs.ts) for complete option/result shapes.
A successful availability check does not establish that a later transfer has
completed or that every requested scope is permitted.

## Upstream attribution

First, a sincere thank-you to the
`react-native-cloud-fs` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[nicola/react-native-cloud-fs](https://github.com/nicola/react-native-cloud-fs).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-cloud-fs`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-cloud-fs` to keep
development and delivery stable.

## Upstream Project

- Repository: [nicola/react-native-cloud-fs](https://github.com/nicola/react-native-cloud-fs)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-cloud-fs` 💙
