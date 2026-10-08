# Add-only photo saving

Status: Implemented extraction of the picker save implementation. JS tests, types, codegen and Android native build pass. iOS test code compiles
and links; local execution is blocked by launchd_sim startup. Native runtime
acceptance and final application artifacts remain pending.

## Purpose and boundaries

Save one caller-owned local image to the system photo library on iOS/Android.
Only getSavePermission, requestSavePermission and saveToLibrary are public
operations. No selection, cropping, album listing, asset readback, deletion,
remote download, video or general filesystem API is provided. Generic app file
operations remain in RNFS. OS-required stream writes are part of the save
transaction; no intermediate image copy is created. Picker item-provider copies
remain in the picker because they must finish before the native callback returns;
there is no second consumer requiring a shared temporary-copy helper.

Callers use the foreground/main JS runtime. main and bg initialize independently
and have separate JS objects/heaps. Native save queues and Android pending
permission ownership are process-shared; no bg readiness is assumed. Import is
lazy and performs no I/O or permission request.

## Public API and defaults

- getSavePermission(): Promise<PhotoSavePermission>, no prompt.
- requestSavePermission(): Promise<PhotoSavePermission>, called only after an
  explicit user save action. Permission is never requested by import or save.
- saveToLibrary(path: string): Promise<void>, requires existing save permission.
  Accepts an absolute local path or file:// URI, and resolves after OS completion.
- PhotoSavePermission: {status: 'granted' | 'denied' | 'undetermined',
  canAskAgain: boolean}. Denied is never translated to successful save.
- PhotoLibraryError.code is E_NO_LIBRARY_PERMISSION, E_NO_IMAGE_DATA_FOUND,
  E_CANNOT_SAVE_IMAGE, E_PERMISSION_IN_PROGRESS, E_ACTIVITY_DOES_NOT_EXIST or
  E_UNKNOWN. No raw native stack or file path is exposed in library errors.
- Missing, non-image or over-64-MiB input rejects E_NO_IMAGE_DATA_FOUND. Metadata
  must identify an image with positive dimensions; no full pixel decode occurs.
  Callers must keep the source unchanged and present until the promise settles.

## Platform contract

| Behavior | iOS | Android |
| --- | --- | --- |
| Authorization | PHPhotoLibrary .addOnly | API 29+: granted without prompt; API 26-28: WRITE_EXTERNAL_STORAGE |
| Permanent denial | denied/restricted, canAskAgain false | Previously requested plus OS rationale false |
| Save | PHAssetCreationRequest.addResource, no readback | API 29+: MediaStore pending insert/write/publish; API 26-28: Pictures/OneKey + scan |
| Declaration | App keeps NSPhotoLibraryAddUsageDescription | App declares WRITE_EXTERNAL_STORAGE with maxSdkVersion 28 |

The module declares no Android storage/media-read permission. App configuration
owns the legacy write declaration and iOS usage strings. No NSPhotoLibraryUsageDescription
is needed. Android permission launch resolves the UI ReactHost activity rather
than relying on the global Nitro context (which may belong to bg).

## Lifecycle, concurrency and ownership

Permissions launch on the UI thread. Android owns at most one pending request
process-wide, rejects overlapping requests with E_PERMISSION_IN_PROGRESS, and
unregisters the launcher/observer on completion or activity destruction. Teardown
rejects E_ACTIVITY_DOES_NOT_EXIST. Each promise settles at most once.
Saves run serially on one background queue per platform; no retry or app cache.
Android persists only whether legacy write authorization has been requested, in
onekey-photo-library/writePermissionRequested (preserved from the picker).
UUID destination names avoid overwriting another save. Source files are never
removed. No JS cache, DB schema, migration or asset index is introduced.

## Failure and resources

Modern Android deletes failed pending rows best-effort and publishes only after
stream completion. Legacy Android removes failed output, waits at most 30 seconds
for media scanning, and rejects null/late scan results. A late OS callback can
leave a stale OS index entry after timeout; the failed file is removed.
iOS failures reject without fetching the newly created asset. No automatic retry.
Streaming uses bounded buffers; metadata and save I/O stay off the UI thread.
Input cap is 64 MiB. No unmeasured memory/latency claim is made. Paths and image
bytes are never logged by this package. Queued sources remain caller-owned.

## Conformance and acceptance

Contract locations: Nitro spec, lazy JS wrapper, platform ReactNativePhotoLibrary
and ImagePhotoLibrary implementations. Focused JS checks cover native permission
results, save-error mapping, and absence of implicit permission requests. Native
checks cover input validation and iOS permission mapping; bridge generation,
TypeScript and native builds cover registration and compilation.
Required runtime cases: iOS first add-only grant/deny/permanent deny and saved
asset, with full-library access denied; Android API 28 first grant/deny/permanent
deny and scan completion; API 29+ save without prompt and no media-read grants;
concurrent permission request, activity teardown, missing/oversized source,
cleanup after failed write. Final App APK/AAB and iOS Info.plist must be inspected
independently. Automated/source checks do not prove those UI cases.

## Template deviations

The repository create:module template is used. Placeholder add API is replaced
with the three save APIs. Local package Babel removes an undeclared module-resolver
plugin, ESLint uses the repository's working FlatCompat setup, and RN devDependency
matches the root patched RN version. A Photos test spec/frameworks and Android
activity-result dependency are required for the extracted behavior.
