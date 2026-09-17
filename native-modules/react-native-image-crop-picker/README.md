# @onekeyfe/react-native-image-crop-picker

Single photo picker and cropper for OneKey, built on [Nitro Modules](https://nitro.margelo.com/). It replaces `react-native-image-crop-picker` and keeps the part of its API that OneKey uses: `openPicker`, `openCropper`, `clean`, `cleanSingle`, and the `E_*` error codes.

Neither platform asks for photo library permission:

- **iOS** picks with `PHPickerViewController`, which runs out of process. `react-native-image-crop-picker` requested full photo library access first. Once a user denied it, iOS never showed the prompt again and every later `openPicker` call failed silently (OK-48227). Cropping uses a vendored TOCropViewController 3.2.0 with the OK-51551 rotation fix, which upstream still lacks.
- **Android** picks with the system Photo Picker (`ActivityResultContracts.PickVisualMedia`, which falls back to `ACTION_OPEN_DOCUMENT` on devices without it) and crops with uCrop `2.2.11-native`. Activity results go through the activity's `ActivityResultRegistry`, so no `ActivityEventListener` is needed.

## Installation

```sh
yarn add react-native-image-crop-picker@npm:@onekeyfe/react-native-image-crop-picker react-native-nitro-modules
```

The npm alias keeps existing `react-native-image-crop-picker` imports working.

- **iOS**: do not also install the `TOCropViewController` pod. This package compiles its own copy, and the duplicate classes would clash at link time.
- **Android**: uCrop is only published to JitPack. Add `maven { url "https://www.jitpack.io" }` to the app's `allprojects.repositories`.

## Usage

```ts
import ImageCropPicker, {
  ImageCropPickerError,
} from 'react-native-image-crop-picker';

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

## Behavior

- Results are always JPEG. `width` and `height` set the crop aspect ratio, and the cropped image is scaled to exactly that size, so a crop box a pixel off the ratio still yields the requested dimensions. With `freeStyleCropEnabled`, the crop keeps its own aspect ratio and is scaled to fit inside `width` × `height`.
- `compressImageMaxWidth`, `compressImageMaxHeight` and `compressImageQuality` apply after cropping. The default quality is 0.8 on iOS and 1 on Android, as in `react-native-image-crop-picker`.
- Photos are decoded at most 4096 px on the long side, which keeps very large photos from exhausting memory. `cropRect` is reported in the original image's coordinates on iOS.
- Results are written to `<tmp>/react-native-image-crop-picker/` on iOS and `<cache>/react-native-image-crop-picker/` on Android. `clean()` empties that directory.
- Only one picker or cropper can be open at a time. A second call rejects with `E_PICKER_IN_PROGRESS`.
- On iOS, cancelling the cropper that `openPicker` opened returns to the photo picker. On Android it rejects with `E_PICKER_CANCELLED`.

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
