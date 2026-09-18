# @onekeyfe/react-native-image-crop-picker

Single photo picker and cropper for OneKey, built on [Nitro Modules](https://nitro.margelo.com/). It replaces `react-native-image-crop-picker` and keeps the part of its API that OneKey uses: `openPicker`, `openCropper`, `clean`, `cleanSingle`, and the `E_*` error codes.

Neither platform asks for photo library permission:

- **iOS** picks with `PHPickerViewController`, which runs out of process. `react-native-image-crop-picker` requested full photo library access first. Once a user denied it, iOS never showed the prompt again and every later `openPicker` call failed silently (OK-48227). The crop gestures come from a vendored TOCropViewController 3.2.0 `TOCropView`, with the OK-51551 rotation fix that upstream still lacks.
- **Android** picks with the system Photo Picker (`ActivityResultContracts.PickVisualMedia`, which falls back to `ACTION_OPEN_DOCUMENT` on devices without it). The crop gestures come from uCrop `2.2.11-native`'s `UCropView`. Activity results go through the activity's `ActivityResultRegistry`, so no `ActivityEventListener` is needed.

Both platforms show the same cropper screen, drawn by this package rather than by TOCropViewController or uCrop. See [Cropper screen](#cropper-screen).

## Installation

```sh
yarn add @onekeyfe/react-native-image-crop-picker react-native-nitro-modules
```

To keep existing `react-native-image-crop-picker` imports working, install it under that name with an npm alias instead: `react-native-image-crop-picker@npm:@onekeyfe/react-native-image-crop-picker`.

- **iOS**: do not also install the `TOCropViewController` pod. This package compiles its own copy, and the duplicate classes would clash at link time.
- **Android**: uCrop is only published to JitPack. Add `maven { url "https://www.jitpack.io" }` to the app's `allprojects.repositories`.

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
  // image.path is a file:// URI; image.data is base64 without a data: prefix.
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

`openCropper` accepts `file://` URIs, absolute paths, `http(s)://` URLs, `data:` URIs and, on Android, `content://` URIs.

## Cropper screen

The cropper is a full-screen page with the same layout and metrics on iOS and Android:

- A 56 pt header with `cropperToolbarTitle` centered and a rotate button on the right. The rotate button turns the image 90° counterclockwise; `cropperRotateButtonsHidden` hides it.
- The crop area. The crop box keeps 20 pt from every edge, and the image outside it shows the page background at 70% opacity. The rule-of-thirds grid shows while the image is moved, unless `showCropGuidelines` is `false`.
- A footer with two capsule buttons, `cropperCancelText` and `cropperChooseText`, 50 pt tall with 10 pt between them. The confirm button shows a spinner while the image is saved.

`cropperAppearance` sets its colors and fonts:

```ts
await ImageCropPicker.openPicker({
  width: 240,
  height: 240,
  cropping: true,
  cropperToolbarTitle: 'Crop image',
  cropperAppearance: {
    colorScheme: 'dark',
    backgroundColor: '#0f0f0f',
    confirmButtonColor: '#ffffffed',
    confirmButtonTextColor: '#000000df',
    titleFontFamily: 'Roobert-SemiBold',
    buttonFontFamily: 'Roobert-Medium',
  },
});
```

| Field | Used for |
| --- | --- |
| `colorScheme` | `'light'` or `'dark'`. Picks the fallback palette and the status bar style. Defaults to the system appearance. |
| `backgroundColor` | The page. The area outside the crop box is this color at 70% opacity. |
| `titleColor` | The title and the crop box border. |
| `iconColor` | The rotate button. |
| `cancelButtonColor`, `cancelButtonPressedColor`, `cancelButtonTextColor` | The cancel button. |
| `confirmButtonColor`, `confirmButtonPressedColor`, `confirmButtonTextColor` | The confirm button. |
| `titleFontFamily`, `buttonFontFamily` | Fonts bundled with the app. iOS looks them up with `UIFont(name:)`, Android with React Native's `ReactFontManager`. |
| `scale` | Multiplies every size, for apps that scale their UI. |

Colors are CSS hex strings: `#RGB`, `#RRGGBB` or `#RRGGBBAA`. Anything left out falls back to OneKey's light or dark palette.

## Behavior

- Results are always JPEG. `width` and `height` set the crop aspect ratio, and the cropped image is scaled to exactly that size, so a crop box a pixel off the ratio still yields the requested dimensions. With `freeStyleCropEnabled`, the crop keeps its own aspect ratio and is scaled to fit inside `width` × `height`.
- `compressImageMaxWidth`, `compressImageMaxHeight` and `compressImageQuality` apply after cropping. The default quality is 0.8 on iOS and 1 on Android, as in `react-native-image-crop-picker`.
- Photos are decoded at most 4096 px on the long side, which keeps very large photos from exhausting memory. `cropRect` is reported in the original image's coordinates on iOS.
- Results are written to `<tmp>/react-native-image-crop-picker/` on iOS and `<cache>/react-native-image-crop-picker/` on Android. `clean()` empties that directory.
- Only one picker or cropper can be open at a time. A second call rejects with `E_PICKER_IN_PROGRESS`.
- On iOS, cancelling the cropper that `openPicker` opened returns to the photo picker. On Android it rejects with `E_PICKER_CANCELLED`.
- On Android the cropper keeps the calling activity's requested orientation, so a portrait-only app gets a portrait-only cropper.

## Not supported

Multiple selection, video, the camera and `includeExif` are not implemented. `mediaType`, `forceJpg` and `sortOrder` are accepted for source compatibility and ignored.

## Error codes

| Code | Meaning |
| --- | --- |
| `E_PICKER_CANCELLED` | The user closed the picker or the cropper. |
| `E_PICKER_IN_PROGRESS` | Another picker or cropper is still open. |
| `E_ACTIVITY_DOES_NOT_EXIST` | Android: no current activity. |
| `E_FAILED_TO_SHOW_PICKER` | The picker or cropper could not be presented. |
| `E_NO_IMAGE_DATA_FOUND` | The selected item could not be read as an image. |
| `E_CROPPER_IMAGE_NOT_FOUND` | `openCropper` could not load `path`. |
| `E_CANNOT_SAVE_IMAGE` | The result could not be written. |
| `E_LOW_MEMORY_ERROR` | Android: out of memory while processing. |
| `E_ERROR_WHILE_CLEANING_FILES` | `clean` or `cleanSingle` failed. |
| `E_UNKNOWN` | Any other native error. |

## License

MIT. The vendored TOCropViewController in `ios/TOCropViewController` is MIT licensed by Tim Oliver; see its `LICENSE`.
