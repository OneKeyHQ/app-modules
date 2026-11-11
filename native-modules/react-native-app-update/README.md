<div align="center">

# @onekeyfe/react-native-app-update

**Android APK download, signature verification and installation for OneKey apps.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-app-update?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-app-update)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-app-update?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Downloads an Android update APK with progress notifications, verifies its detached
ASC signature and package identity, and launches the system installer. The module
owns the APK, partial segments, ASC sidecar, verified-hash state, notifications and
download cancellation; the application owns release selection and user-facing
retry decisions. iOS exposes the same API as no-op stubs, so shared code runs on
both platforms but only Android performs real work.

## Features

- **Complete update pipeline** — `downloadAPK`, `downloadASC`, `verifyASC`,
  `verifyAPK`, `installAPK`, each a separate awaited step.
- **Progress and terminal events** — a numeric listener ID subscribes to
  download progress and completion events; remove it when the owner unmounts.
- **Signed-release checks** — the ASC signature is bound to the APK bytes, and the
  package's identity, downgrade state and signing certificate are rechecked.
- **Sanitized failures** — rejected promises and `update/error` events carry only
  `ONEKEY_UPDATE_ERROR:` plus `{ code, step, httpStatus? }`, never URLs or paths.
- **Bounded artifacts** — a reported APK size above 512 MiB is rejected with
  `SECURITY_REJECTED` before concurrent download workers start.
- **Lifecycle cleanup** — `clearCache()` waits for an active writer before
  deleting; `clearApkCache()` wipes downloaded `.apk`/`.partial`/ASC artifacts.

## Installation

```sh
yarn add @onekeyfe/react-native-app-update react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; the
JavaScript package alone does not register the Nitro hybrid object. The package
manifest merges Android's `REQUEST_INSTALL_PACKAGES` permission and a
`FileProvider`, so no manual manifest entry is needed. On iOS the pod also depends
on `ReactNativeNativeLogger`, so include `@onekeyfe/react-native-native-logger` in
the application's pods. No `Info.plist` key is required because iOS methods are
inert.

## Usage

```ts
import {
  ReactNativeAppUpdate,
  type DownloadEvent,
} from '@onekeyfe/react-native-app-update';

const listenerId = ReactNativeAppUpdate.addDownloadListener(
  (event: DownloadEvent) => {
    if (event.type === 'update/downloading') {
      console.log(event.progress); // 0–100
    }
  },
);

const downloadUrl = 'https://updates.example.com/app.apk';

try {
  await ReactNativeAppUpdate.downloadAPK({
    downloadUrl,
    notificationTitle: 'Downloading update',
    fileSize: 183670673,
  });
  await ReactNativeAppUpdate.downloadASC({ downloadUrl });
  await ReactNativeAppUpdate.verifyASC({ downloadUrl });
  await ReactNativeAppUpdate.verifyAPK({ downloadUrl });
  await ReactNativeAppUpdate.installAPK({ downloadUrl });
} finally {
  ReactNativeAppUpdate.removeDownloadListener(listenerId);
}
```

Each step is independent and rejects on its own; the application decides when a
failed step is retried and keeps the user informed.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `downloadAPK(params)` | `Promise<void>` | Android (iOS no-op) | Downloads an APK. `params` requires `downloadUrl`, `notificationTitle`, `fileSize`. |
| `downloadASC(params)` | `Promise<void>` | Android (iOS no-op) | Downloads the detached ASC signature next to the APK. |
| `verifyASC(params)` | `Promise<void>` | Android (iOS no-op) | Binds the signature to the downloaded APK bytes. |
| `verifyAPK(params)` | `Promise<void>` | Android (iOS no-op) | Checks package identity, downgrade state and signing certificate. |
| `installAPK(params)` | `Promise<void>` | Android (iOS no-op) | Rechecks the file hash, then launches the Android installer. |
| `clearCache()` | `Promise<void>` | Android (iOS no-op) | Clears the update cache; can cancel an in-flight download. |
| `clearApkCache()` | `Promise<void>` | Android (iOS no-op) | Removes cached APK artifacts; does not cancel a download. |
| `addDownloadListener(callback)` | `number` | Android (iOS no-op) | Subscribes to `DownloadEvent`; returns the listener ID. |
| `removeDownloadListener(id)` | `void` | Android (iOS no-op) | Removes the listener registered under `id`. |
| `testVerification()` / `testSkipVerification()` | `Promise<boolean>` | Android | Debug helpers for the verification gate. |
| `isSkipGpgVerificationAllowed()` | `boolean` | Android | Whether the build allows skipping GPG verification. |

`AppUpdateDownloadParams`, `AppUpdateFileParams` and `DownloadEvent` are exported
for adapters. See [`src/ReactNativeAppUpdate.nitro.ts`](src/ReactNativeAppUpdate.nitro.ts)
for the full interface and [`docs/SPEC.md`](docs/SPEC.md) for the failure contract.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Download APK / ASC | — | ✅ | — |
| Verify ASC / APK | — | ✅ | — |
| Install APK | — | ✅ | — |
| Download progress events | — | ✅ | — |
| Cache and artifact cleanup | — | ✅ | — |
| App Store / in-app update | — | — | — |

## Caveats

> [!WARNING]
> The verification steps are not optional in a production flow: a completed
> download is not proof of verification or installation. Run `verifyASC`,
> `verifyAPK` and `installAPK` as distinct steps and surface each failure.

> [!IMPORTANT]
> On iOS every method is a no-op stub, including listener registration. Do not use
> a `downloadAPK` call as an update mechanism on iOS; this package does not
> implement App Store updates. Only builds installed with
> `ONEKEY_ALLOW_SKIP_GPG_VERIFICATION` set allow the skip-verification test
> helpers — never ship a production build that enables them.

> [!NOTE]
> `clearCache()` may cancel an active download, while `clearApkCache()` only wipes
> artifacts and leaves an in-flight writer running. Coordinate both through the
> application's update state so a stale writer cannot race a new download.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/AppUpdateTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
