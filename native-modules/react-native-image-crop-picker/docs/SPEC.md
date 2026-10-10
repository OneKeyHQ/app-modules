# System image picker and cropper

Status: implemented additions; existing crop behavior is preserved. Native unit
tests verify PNG copy identity and image validation. System picker runtime
acceptance remains pending.

## Purpose and scope

Pick one user-selected image, optionally crop it, using private temporary files. Supported native platforms are iOS and Android. Album enumeration,
deletion, videos, camera capture, and product UI are outside this package.

## Ownership and boundaries

Consumers invoke UI operations from the foreground/main JS runtime. Native
resources belong to the OS or native module; main/bg JS heaps remain separate.
Importing the package creates no native object until its first use. Do not infer
read-library authorization from a picker result or add-only authorization.
The picker grants access only to the selected resource. Copied results belong to
the module's temporary directory; consumers own clean/cleanSingle timing and
must not clean while another operation is using the file. Photo saving and its authorization belong to react-native-photo-library.

## API and defaults

- Existing openPicker/openCropper/clean/cleanSingle semantics remain intact.
- `preserveOriginal?: boolean` defaults to false. With openPicker and cropping
  disabled, true copies the selected representation byte-for-byte, retaining
  format, dimensions and orientation metadata. Compression options are ignored.
  Cropping (including openCropper) takes precedence and still returns JPEG.
- Original selection inputs are limited to 64 MiB. Exceeding that limit
  rejects `E_NO_IMAGE_DATA_FOUND`. Metadata must identify a decodable image with
  positive dimensions. Processed-image limits are unchanged (4096px long side).

## Lifecycle and concurrency

Picker/cropper presentations run on the UI thread.
Existing picker concurrency and E_PICKER_IN_PROGRESS behavior stay unchanged.
Android resolves the UI ReactHost activity because Nitro's global context may
belong to bg. No save-permission request or library save is exposed by this package.

## Data and resources

Original selection uses a streaming copy and metadata inspection without a full
pixel decode. Copies use UUID names, retain the actual image MIME and use format
extensions where known (a provider without a MIME type uses `.img`). They support
clean/cleanSingle. Failure removes incomplete copies. Optional base64 necessarily
allocates an encoded copy, bounded by the 64 MiB input limit. There is no new DB,
JS cache, persistent resource index or migration. No save authorization state is persisted by this package.

## Platform contract

| Operation | iOS | Android |
| --- | --- | --- |
| Pick | PHPicker, no read prompt | PickVisualMedia, ACTION_OPEN_DOCUMENT fallback, no read prompt |
| Original | Copy item-provider file before callback returns | Stream the granted content URI to private cache |
| Failure cleanup | Failed private copies are removed | Failed private copies are removed |

The picker requires no photo read or write authorization and declares neither.
The App may omit NSPhotoLibraryUsageDescription. The separate save module owns
add-only authorization, with declarations configured by the App.

## Failure and safety

No automatic retry. Pick cancellation remains E_PICKER_CANCELLED. Original metadata (including location) is retained locally; consumers decide
whether to upload it. Native logs must not contain source paths or photo bytes.
Android picker/cropper failures use fixed error messages rather than provider,
filesystem or crop-activity exception text; existing E_* codes are preserved.
Consumers must not clean a file before scanning/saving finishes.

## Performance

File copies and metadata reads run off the UI thread. No new full-image
decode for originals, and no library-wide query. Streaming Android
copies use bounded buffers. No unmeasured latency/memory claim is made.

## Conformance and acceptance

Contract locations: Nitro spec and JS wrapper; ImageCropPickerSession and
ImageCropPickerImageProcessor; platform picker sessions and processors.
Focused tests cover original image validation and JS error/result propagation. Codegen,
TypeScript/lint and native compilation verify bridge/build compatibility.
Local iOS 26.5 Simulator XCTest ran three image tests with zero failures: PNG byte/base64
identity and dimensions, non-image/oversized rejection and
missing-source error. JS bridge tests and web chooser tests also pass. These
checks do not exercise a Photos authorization dialog or a saved Photos asset.
Required runtime acceptance: iOS picker with Photos denied; PNG/JPEG/HEIC original
byte identity and QR scan; crop still JPEG; original metadata validation. Android API 36 and 28:
selected URI copy, back/cancel, repeated picker launches, activity teardown,
private copy cleanup and merged permission declarations.
Web/desktop/extension picking and both app share entry points are consumer tests.
Source/unit checks must not be reported as device or App Store validation.
