# App Update Native Contract

Status: Android failure contract implemented in source; arm64 AAR/Nitro C++ host build and native wire/normalizer checks passed. Device download, cleanup, and installer acceptance remain pending.

## Purpose, ownership, and public API

Android downloads, verifies, and starts installation of an APK through
`downloadAPK`, `downloadASC`, `verifyASC`, `verifyAPK`, and `installAPK`. The
module owns the APK, partial segments, ASC sidecar, verified hash state,
notifications, and download cancellation. iOS update methods are existing
no-op stubs. Callers own release selection and user-facing retry decisions.

## Lifecycle and safety

Download progress events and terminal events follow the existing order.
Downloaded bytes are preserved when detached ASC verification is temporarily
unavailable; a dedicated deferred-verification exception marks this condition
and never authorizes installation. Clearing the cache can cancel an in-flight
download. `verifyASC` binds the detached signature to APK bytes; `verifyAPK`
checks package identity, downgrade, and signing certificate; `installAPK`
rechecks the file hash before launching the Android installer. Existing debug
verification controls retain their current gates.
`clearCache` blocks a new APK download while it owns cleanup and waits for the
active writer to finish before deleting files. If that wait times out or is
interrupted, cleanup rejects without deleting files; the old writer retains its
download slot until it exits, after which a new download may start.

## Failure contract

The update steps are `download`, `downloadASC`, `verifyASC`, `verifyPackage`,
and `install`; `clearCache` and `clearApkCache` use `cleanup`. A rejected update
Promise and an `update/error` event carry only
`ONEKEY_UPDATE_ERROR:` followed by compact JSON with `code`, `step`, and
optional `httpStatus`. The fixed codes are `NETWORK_ERROR`, `TIMEOUT`,
`HTTP_ERROR`, `PACKAGE_MISSING`, `STORAGE_ERROR`, `INTEGRITY_FAILED`,
`INCOMPATIBLE`, `INSTALL_FAILED`, `CANCELLED`, `UNKNOWN`, `INVALID_ARGUMENT`,
and `SECURITY_REJECTED`. `httpStatus` is present only for `HTTP_ERROR` with an
integer status from 100 through 599. No message, cause, URL, path, stack, or
arbitrary field crosses this boundary.

Validation and boundary rejections use typed native errors at their origin.
Platform exceptions are classified by exception type, never English prose;
unclassified exceptions become `UNKNOWN`. Raw diagnostics may be written to
local native logs. Intentional cancellation and transient network/HTTP deferred
verification reject the Promise without a failure event. A nontransient
verification failure emits one safe `update/error` payload. Signature and hash
failures are `INTEGRITY_FAILED`.

## Conformance and acceptance

The Android source is `android/src/main/java/com/margelo/nitro/reactnativeappupdate/ReactNativeAppUpdate.kt`.
Focused native and bridge checks must cover HTTP status, network timeout,
missing APK, integrity and identity rejection, deferred verification,
intentional cancellation, unknown exceptions, and absence of raw paths from
Promise and event payloads. Source checks and host compilation are separate
from real-device install acceptance.
