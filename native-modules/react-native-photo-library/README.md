# @onekeyfe/react-native-photo-library

Minimal add-only image saving. [Contract and acceptance](docs/SPEC.md).

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
Neither platform needs whole-library read authorization. Permanent denial is
returned as `{status: 'denied', canAskAgain: false}`. `PhotoLibraryError.code`
identifies permission, missing input, activity/concurrency and save failures.
