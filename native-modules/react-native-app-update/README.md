# @onekeyfe/react-native-app-update

Android APK download, signature verification and installation support for OneKey.
The Nitro interface also exposes download progress and downloaded-artifact
cleanup. The application owns update selection, user interaction and deciding
when an installation should proceed.

## Installation

```sh
yarn add @onekeyfe/react-native-app-update react-native-nitro-modules
```

Rebuild the native application after installation. On iOS, install the application
pods as part of its usual native dependency workflow.

## Usage

```ts
import { ReactNativeAppUpdate } from '@onekeyfe/react-native-app-update';

const listenerId = ReactNativeAppUpdate.addDownloadListener((event) => {
  console.log(event.type, event.progress);
});

try {
  await ReactNativeAppUpdate.downloadAPK({
    downloadUrl: 'https://updates.example.com/app.apk',
    notificationTitle: 'Application update',
    fileSize: 123456,
  });
} finally {
  ReactNativeAppUpdate.removeDownloadListener(listenerId);
}
```

## API and platform limits

- `downloadAPK` downloads an Android update; `downloadASC`, `verifyASC` and
  `verifyAPK` provide the signature and verification operations.
- `installAPK` requests Android installation. A completed download is not proof
  of verification or installation; callers must perform those separate steps.
- `clearCache` and `clearApkCache` clean cached update artifacts.
- Download listeners return a numeric ID; remove each listener when its owner
  finishes or unmounts.

APK operations on iOS are no-op stubs. This package does not implement App Store
updates. `clearApkCache` does not cancel an in-flight Android download, so callers
must coordinate cleanup with update state.

For complete parameter and event types, see
[src/ReactNativeAppUpdate.nitro.ts](src/ReactNativeAppUpdate.nitro.ts). Verification
test helpers are also exposed there; applications should keep the production
download and verification flow enabled.

## License

MIT.
