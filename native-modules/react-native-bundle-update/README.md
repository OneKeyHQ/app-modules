<div align="center">

# @onekeyfe/react-native-bundle-update

**Native OTA JavaScript bundle download, verification, installation and selection for OneKey apps.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-bundle-update?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-bundle-update)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-bundle-update?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Downloads OTA JavaScript bundles over HTTPS, verifies their signature and
extracted contents, installs them, and exposes the paths the UI, background and
embedded-Web runtimes should load. The native module owns the download directory,
ZIP, extracted bundle, signature sidecar and cleanup — callers never choose a path.

## Features

- **Verified downloads** — `downloadBundle` streams to a partial ZIP, checks the
  expected size and SHA-256, then promotes it to the managed ZIP name.
- **Separate verification steps** — `verifyBundle`, `verifyBundleASC`,
  `downloadBundleASC` and `verifyExtractedBundle` keep signature, hash and
  extracted-file checks distinct from installation.
- **Atomic install and switch** — `installBundle` re-verifies before extraction,
  and `setCurrentUpdateBundleData` rechecks every extracted file before changing
  the active bundle.
- **Runtime-specific paths** — sync and async getters for the UI, background and
  embedded-Web bundle locations, so a switch updates each runtime without assuming
  they share one bundle.
- **Bounded, traversal-safe archives** — ZIP and uncompressed payloads are capped
  at 512 MiB, extraction at 4,096 entries, and paths escaping the managed root are
  rejected.

## Installation

```sh
yarn add @onekeyfe/react-native-bundle-update react-native-nitro-modules
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-bundle-crypto @onekeyfe/react-native-native-logger @onekeyfe/react-native-range-downloader
```

Install the application's iOS pods and rebuild the native application; this is a
Nitro module, so a JavaScript-only install cannot change the bundle the host app
loads. The iOS pod uses Foundation and Gopenpgp, Android uses Java files and
BouncyCastle, and the iOS pod also depends on `ReactNativeNativeLogger`,
`ReactNativeBundleCrypto` and `ReactNativeRangeDownloader` — include those
`@onekeyfe/*` packages in the application's pods. No permissions or `Info.plist`
keys are required.

## Usage

```ts
import {
  ReactNativeBundleUpdate,
  type BundleDownloadEvent,
} from '@onekeyfe/react-native-bundle-update';

const listenerId = ReactNativeBundleUpdate.addDownloadListener(
  (event: BundleDownloadEvent) => {
    if (event.type === 'update/downloading') {
      console.log(event.progress); // 0–100
    }
  },
);

try {
  await ReactNativeBundleUpdate.downloadBundle({
    downloadUrl: 'https://updates.example.com/bundle.zip',
    latestVersion: '1.2.3',
    bundleVersion: '1.2.3-1',
    fileSize: 25165824,
    sha256: '<expected-hex>',
  });
  await ReactNativeBundleUpdate.verifyBundleASC({
    sha256: '<expected-hex>',
    latestVersion: '1.2.3',
    bundleVersion: '1.2.3-1',
    signature: '<asc-content>',
  });
  await ReactNativeBundleUpdate.installBundle({
    latestVersion: '1.2.3',
    bundleVersion: '1.2.3-1',
    signature: '<asc-content>',
  });
} finally {
  ReactNativeBundleUpdate.removeDownloadListener(listenerId);
}
```

After a successful switch, restart both JavaScript runtimes so the UI and
background heaps load the new bundle instead of running mismatched module tables.

## API

| Method | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `downloadBundle` | `(BundleDownloadParams) => Promise<BundleDownloadResult>` | iOS, Android | Downloads and verifies one OTA ZIP; the result carries identity and hash, never a local path. |
| `downloadBundleASC` | `(BundleDownloadASCParams) => Promise<void>` | iOS, Android | Downloads and validates the signature sidecar into its managed destination. |
| `verifyBundle` | `(BundleVerifyParams) => Promise<void>` | iOS, Android | Checks the managed ZIP against the expected SHA-256. |
| `verifyBundleASC` | `(BundleVerifyASCParams) => Promise<void>` | iOS, Android | Verifies signed metadata and ZIP hash before extraction. |
| `verifyExtractedBundle` | `(appVersion, bundleVersion) => Promise<void>` | iOS, Android | Re-checks every extracted file against signed metadata. |
| `installBundle` | `(BundleInstallParams) => Promise<void>` | iOS, Android | Extracts and installs the verified bundle without activating it. |
| `setCurrentUpdateBundleData` | `(BundleSwitchParams) => Promise<void>` | iOS, Android | Re-verifies and switches the active bundle. |
| `addDownloadListener` / `removeDownloadListener` | `(callback) => number` / `(id) => void` | iOS, Android | Subscribe to and release download progress events. |

| Area | Methods | Platforms |
| --- | --- | :-: |
| Runtime paths | `getJsBundlePath`, `getBackgroundJsBundlePath`, `getWebEmbedPath` and their `Async` variants | iOS, Android |
| Identity and inspection | `getNativeAppVersion`, `getNativeBuildNumber`, `getBuiltinBundleVersion`, `listLocalBundles`, `listAscFiles`, `isBundleExists`, `getFallbackUpdateBundleData`, `getSha256FromFilePath` | iOS, Android |
| Cleanup and controls | `clearDownload`, `clearBundle`, `clearAllJSBundleData`, `resetToBuiltInBundle`, `pruneStaleAppVersionBundles`, `testVerification`, `testSkipVerification`, `isSkipGpgVerificationAllowed` | iOS, Android |

Parameter, result and listener types (`BundleDownloadParams`, `BundleDownloadResult`, `BundleVerifyParams`, `BundleVerifyASCParams`, `BundleDownloadASCParams`, `BundleInstallParams`, `BundleSwitchParams`, `LocalBundleInfo`, `AscFileInfo`) are exported (see [`src/ReactNativeBundleUpdate.nitro.ts`](src/ReactNativeBundleUpdate.nitro.ts)).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| OTA bundle download with progress | ✅ | ✅ | — |
| Signature and extracted-file verification | ✅ Gopenpgp | ✅ BouncyCastle | — |
| Install and switch bundle | ✅ | ✅ | — |
| Runtime bundle paths and local cleanup | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> Verification and installation are distinct steps. An existing directory is not
> evidence that its contents are trusted: run `verifyBundleASC` (or
> `verifyExtractedBundle`) before `installBundle`, and keep
> `testSkipVerification` out of production paths.

> [!WARNING]
> Rejected promises and `update/error` events carry only `ONEKEY_UPDATE_ERROR:`
> plus `{ code, step, httpStatus? }` — no message, URL or path. `INTEGRITY_FAILED`
> means signature or hash failure and must not be retried; cleanup cancellation
> rejects as `CANCELLED` without an error event.

> [!NOTE]
> The main and background runtimes have separate heaps but share native files and
> preferences. Restart both after a switch, and use each runtime's own path getter.
> `listLocalBundles()` returns only bundles for the running native app version.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/BundleUpdateTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
