# @onekeyfe/react-native-bundle-update

Native JavaScript bundle update storage and operations for OneKey on iOS and
Android. The Nitro module downloads bundles, verifies their artifacts, installs
and selects versions, and exposes paths for UI, background and embedded Web
content. The application owns release metadata and runtime restart decisions.

## Installation

```sh
yarn add @onekeyfe/react-native-bundle-update react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application. This
module depends on native update integration; a JavaScript-only install cannot
change the bundle used by the application's host.

## Usage

```ts
import { ReactNativeBundleUpdate } from '@onekeyfe/react-native-bundle-update';

const appVersion = await ReactNativeBundleUpdate.getNativeAppVersion();
const builtinVersion = await ReactNativeBundleUpdate.getBuiltinBundleVersion();
const bundles = await ReactNativeBundleUpdate.listLocalBundles();
const bundlePath = await ReactNativeBundleUpdate.getJsBundlePathAsync();
```

## Update operations

| Area | Methods |
| --- | --- |
| Download | `downloadBundle`, `downloadBundleASC` |
| Verification | `verifyBundle`, `verifyBundleASC`, `verifyExtractedBundle` |
| Install and select | `installBundle`, `setCurrentUpdateBundleData` |
| Inspect | `listLocalBundles`, `listAscFiles`, `isBundleExists` |
| Reset and cleanup | `clearDownload`, `clearBundle`, `resetToBuiltInBundle`, `pruneStaleAppVersionBundles` |

Download parameters include release versions, expected size and SHA-256.
Signature-bearing operations have separate parameter types. Keep verification
and installation as distinct steps; discovering a directory is not evidence
that its contents are trusted.

Register progress with `addDownloadListener` and release the returned listener ID
with `removeDownloadListener`. Cleanup removes local update artifacts and must
be coordinated with active downloads and the application's running versions.
The debug deletion helpers should not be part of a normal update flow.

See [src/ReactNativeBundleUpdate.nitro.ts](src/ReactNativeBundleUpdate.nitro.ts)
for all methods, result objects and path APIs. This README documents the existing
interface; it does not establish end-to-end update acceptance for a consumer.

## Version and path result types

`listLocalBundles()` returns `LocalBundleInfo[]`, containing `appVersion` and
`bundleVersion`. `getFallbackUpdateBundleData()` returns records with those
versions plus `signature`; fallback metadata and an installed directory are
separate from a completed verification operation.

The interface offers synchronous and asynchronous variants of the UI, background
and embedded-Web path getters. Select the appropriate runtime path rather than
assuming both runtimes use one JavaScript bundle. Application adapters can import
`BundleDownloadParams`, `BundleDownloadResult`, `BundleVerifyParams`,
`BundleInstallParams` and `BundleSwitchParams` as types from this package.

## License

MIT.
