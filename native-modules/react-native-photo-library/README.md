<div align="center">

# @onekeyfe/react-native-photo-library

**Add-only photo saving for OneKey apps, without whole-library read access.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-photo-library?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-photo-library)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-photo-library?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Saves a local image into the system photo library through a minimal, add-only
surface. iOS requests Photos `.addOnly` authorization, Android 10+ saves through
MediaStore, and the legacy write permission is capped at API 28 for Android 8–9.
The package does not read back assets, request whole-library access, or provide
picking, cropping or deletion.

## Features

- **Add-only by design** — no `NSPhotoLibraryUsageDescription` and no asset
  read-back; the module cannot browse the library.
- **Explicit permission flow** — check without prompting, request from a user
  action, then save; each step is a separate call.
- **Bounded input** — local images only, up to 64 MiB, kept in caller ownership
  until the save settles.
- **Typed failures** — `PhotoLibraryError` with stable codes for permission,
  input, activity and save failures.
- **Lazy native object** — importing the wrapper performs no permission request
  and no file I/O.

## Installation

```sh
yarn add @onekeyfe/react-native-photo-library
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

Install the app's iOS pods and rebuild the native application. The package has no
Web implementation.

### iOS

Add an application-specific `NSPhotoLibraryAddUsageDescription` to `Info.plist`.
Saving uses `.addOnly` authorization, so `NSPhotoLibraryUsageDescription` is not
required.

### Android

Android 10+ saves with MediaStore and needs no storage permission. For Android 8–9
only, the application declares:

```xml
<uses-permission
  android:name="android.permission.WRITE_EXTERNAL_STORAGE"
  android:maxSdkVersion="28" />
```

The application owns these declarations and the user-facing explanation.

## Usage

```ts
import PhotoLibrary from '@onekeyfe/react-native-photo-library';

// Invoke only after the user explicitly chooses Save.
const permission = await PhotoLibrary.requestSavePermission();
if (permission.status === 'granted') {
  await PhotoLibrary.saveToLibrary('file:///local/image.png');
}
```

Pass an absolute local path or `file://` URI and keep the source file in place
until the promise settles. Saving runs serially on a native background queue.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `getSavePermission()` | `Promise<PhotoSavePermission>` | iOS, Android | Checks authorization without prompting. |
| `requestSavePermission()` | `Promise<PhotoSavePermission>` | iOS, Android | Requests authorization; call from a foreground user action. |
| `saveToLibrary(path)` | `Promise<void>` | iOS, Android | Saves the local image; never requests permission or deletes the source. |

`PhotoSavePermission` contains `status` (`granted`, `denied`, `undetermined`) and
`canAskAgain`. Failures reject with `PhotoLibraryError`:

| `PhotoLibraryError.code` | Meaning |
| --- | --- |
| `E_NO_LIBRARY_PERMISSION` | Saving lacks the required authorization. |
| `E_NO_IMAGE_DATA_FOUND` | Missing, invalid, non-image, or oversized input. |
| `E_CANNOT_SAVE_IMAGE` | The native save or legacy media scan failed. |
| `E_PERMISSION_IN_PROGRESS` | Another Android permission request is pending. |
| `E_ACTIVITY_DOES_NOT_EXIST` | No usable activity, or its lifecycle ended. |
| `E_UNKNOWN` | A failure without a recognized native error code. |

The default export and the named exports expose the same methods. See
[`docs/SPEC.md`](docs/SPEC.md) for the full contract, ownership boundaries and
acceptance evidence.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Save a local image | ✅ `.addOnly` | ✅ MediaStore | — |
| Check/request permission | ✅ | ✅ | — |
| Read library assets | — | — | — |
| Legacy write permission | — | ✅ API ≤ 28 only | — |

## Caveats

> [!IMPORTANT]
> `saveToLibrary()` never requests permission. Run `requestSavePermission()` from
> an explicit user action first, and do not infer a successful save from a
> permission result alone.

> [!NOTE]
> On Android 8–9 the legacy write permission also implies storage read access,
> and the system prompt describes access to photos, media and files. On Android
> 10+ and iOS this package never asks for whole-library read authorization.

## References

- [Package spec](docs/SPEC.md)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-photo-library)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
