<div align="center">

# @onekeyfe/react-native-image-crop-picker

**Pick one photo and crop it with one API on iOS, Android and the web — without photo-library permission.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-image-crop-picker?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-image-crop-picker)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-image-crop-picker?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

A single-photo picker and cropper for OneKey, replacing the parts of
`react-native-image-crop-picker` that OneKey uses: `openPicker`, `openCropper`,
`clean`, `cleanSingle` and the `E_*` error codes. iOS picks with
`PHPickerViewController`, Android with the system Photo Picker, and the browser
with a file input, so no platform asks for photo-library permission. The cropper
is a screen drawn by this package, identical on iOS and Android, with a DOM
equivalent on the web. Photo saving and its authorization belong to
`@onekeyfe/react-native-photo-library`; this package exposes no save API.

## Features

- **No photo-library prompt** — picking grants access only to the selected
  item, so a denied library permission can no longer break the picker.
- **One shared cropper** — the same header, crop box, dimmed background,
  rotate control and footer on iOS and Android; the same layout on the web with
  zoom and keyboard input for mouse users.
- **Optional cropping or original bytes** — `cropping: true` returns a JPEG at
  the requested size; `preserveOriginal: true` copies the selected
  representation untouched when cropping is off.
- **Explicit cleanup** — results live in the module's temporary directory until
  the caller runs `clean()` or `cleanSingle()`.
- **Typed failures** — every rejection is an `ImageCropPickerError` with a
  stable `E_*` code.

## Installation

```sh
yarn add @onekeyfe/react-native-image-crop-picker react-native-nitro-modules
```

Install the native peer packages this package links as well:

```sh
yarn add @onekeyfe/react-native-native-logger
```

To keep existing `react-native-image-crop-picker` imports working, install it
under an npm alias: `react-native-image-crop-picker@npm:@onekeyfe/react-native-image-crop-picker`.
Install the application's iOS pods and rebuild the native binary; the web entry
needs no native code.

- **iOS**: do not also install the `TOCropViewController` pod. This package
  compiles its own copy and duplicate classes clash at link time.
- **Android**: uCrop is published only to JitPack, so add
  `maven { url "https://www.jitpack.io" }` to the app's `allprojects.repositories`.

## Usage

```ts
import ImageCropPicker, {
  ImageCropPickerError,
} from '@onekeyfe/react-native-image-crop-picker';

try {
  const image = await ImageCropPicker.openPicker({
    width: 240,
    height: 240,
    cropping: true,
    includeBase64: true,
    compressImageQuality: 0.8,
  });
  // image.path is a file:// URI (blob: URL on the web).
} catch (error) {
  if (error instanceof ImageCropPickerError && error.code === 'E_PICKER_CANCELLED') {
    return;
  }
  throw error;
}

const cropped = await ImageCropPicker.openCropper({
  path: 'file:///path/to/image.jpg',
  width: 480,
  height: 800,
});
```

`openCropper` accepts `file://` URIs, absolute paths, `http(s)://` URLs,
`data:` URIs and, on Android, `content://` URIs. Read or save a result before
cleaning it; only one picker or cropper can be open per app.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `openPicker(options?)` | `Promise<Image>` | iOS, Android, Web | Picks one photo, optionally connected to the cropper. |
| `openCropper({ path, ...options })` | `Promise<Image>` | iOS, Android, Web | Crops an existing image and returns a JPEG. |
| `clean()` | `Promise<void>` | iOS, Android, Web | Deletes every file this module wrote. |
| `cleanSingle(path)` | `Promise<void>` | iOS, Android, Web | Deletes one file returned by this module. |
| `ImageCropPickerError` | `Error` subclass with `code` | iOS, Android, Web | Rejection type for all operations. |
| `Image` | `{ path, size, width, height, mime, data?, cropRect?, filename? }` | iOS, Android, Web | Pick/crop result; `data` is base64 without a `data:` prefix. |

Cropping uses `width`/`height` as both the target size and the aspect ratio;
`freeStyleCropEnabled` lets the box keep its own ratio and fit inside that size.
`compressImageMaxWidth`, `compressImageMaxHeight` and `compressImageQuality`
apply after cropping. `cropperAppearance`, `cropperToolbarTitle`,
`cropperChooseText`, `cropperCancelText`, `cropperRotateButtonsHidden` and
`showCropGuidelines` style the cropper. See
[`src/ReactNativeImageCropPicker.nitro.ts`](src/ReactNativeImageCropPicker.nitro.ts)
for every option and [`src/shared.ts`](src/shared.ts) for the full error-code list.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Pick one photo without library permission | ✅ PHPicker | ✅ Photo Picker | ✅ file input |
| Interactive cropper | ✅ | ✅ | ✅ DOM |
| `preserveOriginal` (byte-for-byte copy) | ✅ | ✅ | ✅ |
| Native Nitro module | ✅ | ✅ | — |
| `freeStyleCropEnabled` | ✅ | ✅ | — |

## Caveats

> [!NOTE]
> Cancelling the cropper that `openPicker` opened returns to the photo picker on
> iOS but rejects with `E_PICKER_CANCELLED` on Android and the web. A second
> picker or cropper while one is open rejects with `E_PICKER_IN_PROGRESS`.

> [!WARNING]
> On the web, `freeStyleCropEnabled` is not implemented, `path` is a `blob:` URL
> that `clean()` revokes, and `http(s)` sources for `openCropper` must allow CORS
> or the crop rejects with `E_CANNOT_SAVE_IMAGE`.

> [!IMPORTANT]
> A picker result proves access to that one item only — never photo-library
> authorization. Results are temporary files owned by the caller; do not clean a
> file while a scan or save still reads it.

## References

- [Package spec](docs/SPEC.md)
- [Native interface](src/ReactNativeImageCropPicker.nitro.ts)
- [Example page](../../example/react-native/pages/ImageCropPickerTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE). The vendored TOCropViewController in
`ios/TOCropViewController` is MIT licensed by Tim Oliver; see its `LICENSE`.
