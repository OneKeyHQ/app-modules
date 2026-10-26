# SNI Connect native upload contract

Status: **Implemented**. The shared request, validation, transport, and resource
contract remains [OSCS](../SPEC.md). This document specifies the React Native
upload boundary and its focused review corrections.

## Public API and compatibility

- `isUploadFileSupported(): boolean` reports whether the loaded native module
  exposes a callable `uploadFile` method. It performs no I/O.
- `uploadFile(config)` always returns a Promise. When the native method is
  absent, it rejects with an Error whose `code` is `SNI_UNSUPPORTED`.
- Supported native methods receive the caller's config unchanged.

## File ownership, lifecycle, and bounds

- The caller owns the file and should keep it unchanged until settlement.
- Initial validation still requires a readable regular file in app-owned storage,
  with a size of 1 byte through 20 MiB, before admission or network I/O.
- iOS reopens the validated URL after admission, reads at most 20 MiB + 1 byte,
  and rejects an empty or oversized read before attaching the body to a request.
  The handle is closed on success and failure; file data is not memory mapped.
- Android opens and closes the validated file inside `RequestBody.writeTo`.
  It reads in bounded chunks and never writes more than the declared body length
  or 20 MiB to the sink. A changed length (growth or truncation) aborts the upload
  with an I/O error (`SNI_REQUEST_FAILED`); an already transmitted prefix can
  remain at the server. Disk input and socket output remain streaming.
- Admission, cancellation, total deadlines, response handling, and destination
  pinning continue to follow OSCS without changes.

## Conformance and remaining evidence

- JS API tests cover capability detection, missing-native Promise rejection,
  supported forwarding, and synchronous native errors becoming rejections.
- Swift validation tests exercise bounded reads after validated files grow or
  become empty, and exact-limit binary contents.
- Android tests exercise actual RequestBody writes for exact-limit data, growth
  before and during writing, and truncation, including output byte bounds.
- Native device/network upload acceptance remains separate from these unit tests.
