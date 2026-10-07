# Image picker and add-only photo saving

Status: implemented additions; existing crop behavior is preserved. Native unit
tests verify PNG copy identity and permission mapping. System picker/save runtime
acceptance remains pending.

## Purpose and scope

Pick one user-selected image, optionally crop it, and save one local image to
Photos. Supported native platforms are iOS and Android. Album enumeration,
deletion, videos, camera capture, and product UI are outside this package.

## Ownership and boundaries

Consumers invoke UI operations from the foreground/main JS runtime. Native
resources belong to the OS or native module; main/bg JS heaps remain separate.
Importing the package creates no native object until its first use. Do not infer
read-library authorization from a picker result or add-only authorization.
The picker grants access only to the selected resource. Copied results belong to
the module's temporary directory; consumers own clean/cleanSingle timing and
must not clean while another operation is using the file. Saving never deletes
the source and never fetches the newly created photo.

## API and defaults

- Existing openPicker/openCropper/clean/cleanSingle semantics remain intact.
- `preserveOriginal?: boolean` defaults to false. With openPicker and cropping
  disabled, true copies the selected representation byte-for-byte, retaining
  format, dimensions and orientation metadata. Compression options are ignored.
  Cropping (including openCropper) takes precedence and still returns JPEG.
- `getSavePermission()` and `requestSavePermission()` return
  `{ status: 'granted' | 'denied' | 'undetermined', canAskAgain: boolean }`.
  They concern saving only; they never request library read authorization.
- `saveToLibrary(path)` accepts an absolute local path or file:// URI, creates
  one photo and resolves void only after the OS reports completion. It does not
  implicitly request permission. Missing permission rejects
  `E_NO_LIBRARY_PERMISSION`; invalid/missing sources reject
  `E_NO_IMAGE_DATA_FOUND`; write failures reject `E_CANNOT_SAVE_IMAGE`.
- Original selection and save inputs are limited to 64 MiB. Exceeding that limit
  rejects `E_NO_IMAGE_DATA_FOUND`. Metadata must identify a decodable image with
  positive dimensions. Processed-image limits are unchanged (4096px long side).

## Lifecycle and concurrency

Picker/cropper presentations and permission launches run on the UI thread.
Existing picker concurrency and E_PICKER_IN_PROGRESS behavior stay unchanged.
Android permission requests share one native pending request and reject another
with E_PICKER_IN_PROGRESS. Activity destruction rejects the pending request with
E_ACTIVITY_DOES_NOT_EXIST and unregisters the result launcher. No callback may
settle a promise twice. Saves use background I/O, one save at a time per platform;
the source must remain available until the promise settles.

## Data and resources

Original selection uses a streaming copy and metadata inspection without a full
pixel decode. Copies use UUID names, retain the actual image MIME and use format
extensions where known (a provider without a MIME type uses `.img`). They support
clean/cleanSingle. Failure removes incomplete copies. Optional base64 necessarily
allocates an encoded copy, bounded by the 64 MiB input limit. There is no new DB,
JS cache, persistent resource index or migration. Android persists only whether
legacy write authorization has been requested, to distinguish undetermined from
permanent denial after restart.

## Platform contract

| Operation | iOS | Android |
| --- | --- | --- |
| Pick | PHPicker, no read prompt | PickVisualMedia, ACTION_OPEN_DOCUMENT fallback, no read prompt |
| Original | Copy item-provider file before callback returns | Stream the granted content URI to private cache |
| Save permission | Photos addOnly only; denied/restricted cannot ask again | API 29+: granted without prompt; API 26-28: WRITE_EXTERNAL_STORAGE |
| Save | PHAssetCreationRequest in performChanges; no readback | API 29+: MediaStore pending insert/write/publish; API 26-28: Pictures/OneKey + media scan |
| Failure cleanup | Failed private copies are removed | Failed private copies are removed; pending rows/public legacy files are deleted on a best-effort basis |

The app retains NSPhotoLibraryAddUsageDescription. Other dependencies may still
require NSPhotoLibraryUsageDescription; deleting that key is not this module's
responsibility. Android's WRITE_EXTERNAL_STORAGE declaration is capped at 28.
The module neither declares nor requests any photo/video read permission.

## Failure and safety

No automatic retry. Pick cancellation remains E_PICKER_CANCELLED. Permission
denial is represented by the permission result, not a successful save. Saving
requires a local image; remote URLs/data URIs/content URIs are not save inputs.
Original metadata (including location) is retained locally; consumers decide
whether to upload it. Native logs must not contain source paths or photo bytes.
Consumers must not clean a file before scanning/saving finishes. Modern Android
must not publish a partially written media item; legacy save resolves only after
media scanning succeeds (30-second timeout). A late legacy media-scan callback
after timeout may leave a stale OS index entry; the failed file is removed.

## Performance

File copy, metadata reads and saving run off the UI thread. No new full-image
decode for originals or saves, and no library-wide query. Streaming Android
copies use bounded buffers. No unmeasured latency/memory claim is made.

## Conformance and acceptance

Contract locations: Nitro spec and JS wrapper; ImageCropPickerSession and
ImageCropPickerImageProcessor; platform ImagePhotoLibrary implementations.
Focused tests cover permission mapping and JS error/result propagation. Codegen,
TypeScript/lint and native compilation verify bridge/build compatibility.
Local iOS 26.5 Simulator XCTest ran four tests with zero failures: PNG byte/base64
identity and dimensions, permission mapping, non-image/oversized rejection and
missing-source error. JS bridge tests and web chooser tests also pass. These
checks do not exercise a Photos authorization dialog or a saved Photos asset.
Required runtime acceptance: iOS picker with Photos denied; PNG/JPEG/HEIC original
byte identity and QR scan; crop still JPEG; add-only first grant/deny/permanent
deny; missing/oversized save source; actual saved photo. Android API 36 and 28:
selected URI copy, back/cancel, repeated request, legacy grant/deny/permanent deny,
activity teardown, published saved photo and merged permission declarations.
Web/desktop/extension picking and both app share entry points are consumer tests.
Source/unit checks must not be reported as device or App Store validation.
