# @onekeyfe/react-native-photo-library

Minimal add-only image saving. [Contract and acceptance](docs/SPEC.md).

## Installation and application configuration

```sh
yarn add @onekeyfe/react-native-photo-library react-native-nitro-modules@0.37.0
```

Install iOS pods and rebuild the native application. This package has no Web
implementation. It registers its native object lazily on the first API call;
importing the JavaScript wrapper performs no permission request or file I/O.

On iOS, supply an application-specific `NSPhotoLibraryAddUsageDescription` in
Info.plist. This module uses `.addOnly` authorization, so its save operation does
not require `NSPhotoLibraryUsageDescription` or whole-library read authorization.
For Android 8–9 only, the application declares:

```xml
<uses-permission
  android:name="android.permission.WRITE_EXTERNAL_STORAGE"
  android:maxSdkVersion="28" />
```

Android 10+ saves with MediaStore and does not require this legacy permission.
The application owns these declarations and the user-facing explanation.

## Save after an explicit user action

```ts
import PhotoLibrary from '@onekeyfe/react-native-photo-library';

// Invoke only after the user explicitly chooses Save.
const permission = await PhotoLibrary.requestSavePermission();
if (permission.status === 'granted') {
  await PhotoLibrary.saveToLibrary('file:///local/image.png');
}
```

`getSavePermission()` checks without prompting. `saveToLibrary()` never requests
permission, reads back assets, or removes its source. Inputs must be local images
of at most 64 MiB. Keep the source until completion. General file IO remains in
RNFS; use the separate OneKey picker for user selection and cropping.

iOS uses Photos `.addOnly`; keep `NSPhotoLibraryAddUsageDescription` in the App.
Android 10+ uses MediaStore with no storage permission. Android 8-9 requires App
`WRITE_EXTERNAL_STORAGE` capped at `maxSdkVersion=28`; request it only on Save.
iOS and Android 10+ do not request whole-library read authorization. On Android
8-9, granting the legacy write permission also implicitly grants storage read
access, and the system prompt describes access to photos, media and files.
Permanent denial is returned as `{status: 'denied', canAskAgain: false}`.
`PhotoLibraryError.code` identifies permission, missing input,
activity/concurrency and save failures.

## API and failure handling

The default export and named exports expose the same three methods:

| Method | Result and ownership |
| --- | --- |
| `getSavePermission()` | `Promise<PhotoSavePermission>`; checks without a prompt. |
| `requestSavePermission()` | `Promise<PhotoSavePermission>`; request from a foreground user action. |
| `saveToLibrary(path)` | `Promise<void>`; resolves after the OS save completes. |

`PhotoSavePermission` contains `status` (`granted`, `denied`, or `undetermined`)
and `canAskAgain`. Handle denial in the application's existing settings flow;
do not retry a permanently denied request in a loop. Overlapping Android
permission requests reject rather than sharing an activity result.

| `PhotoLibraryError.code` | Meaning |
| --- | --- |
| `E_NO_LIBRARY_PERMISSION` | Saving lacks the required authorization. |
| `E_NO_IMAGE_DATA_FOUND` | Missing, invalid, non-image, or oversized input. |
| `E_CANNOT_SAVE_IMAGE` | The native save or legacy media scan failed. |
| `E_PERMISSION_IN_PROGRESS` | Another Android permission request is pending. |
| `E_ACTIVITY_DOES_NOT_EXIST` | No usable activity, or its lifecycle ended. |
| `E_UNKNOWN` | A failure without a recognized native error code. |

Catch operation failures using the exported `PhotoLibraryError` class and its
`code`. The wrapper removes unrecognized native error details; do not infer a
successful save from a permission result alone.

## Source-file and runtime boundaries

Pass an absolute local path or `file://` URI, not a remote URL or an Android
content URI. Keep the image unchanged until the promise settles. Saving runs
serially on a native background queue and leaves the source in caller ownership.
The module does not queue retries, create an application image cache, or provide
asset selection/deletion APIs. UI/background JS objects initialize independently;
the application's foreground runtime owns permission actions.

The [specification](docs/SPEC.md) records source and automated evidence separately
from App/device acceptance. Confirm the actual permission prompt, saved asset,
and failure cleanup on the target OS versions before shipping a new integration.

## License

MIT; see [LICENSE](LICENSE).
