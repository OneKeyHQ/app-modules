# Bundle Update Native Contract

Status: Implemented in source. Device acceptance remains pending.

## Purpose and ownership

This module downloads, verifies, extracts, and activates OneKey JS bundles on
iOS and Android. The caller supplies release identity and signed metadata. The
native module owns the download directory, ZIP path, extracted bundle directory,
signature file, and their cleanup. Callers do not choose a local ZIP path.
The main and background JavaScript runtimes have separate heaps, while native
files and preferences are shared resources.

## Public API and identity

`downloadBundle` takes an HTTPS `downloadUrl`, `latestVersion`, `bundleVersion`,
`fileSize`, and `sha256`. Its result returns the URL, versions, and SHA-256, with
no local path. `downloadBundleASC` takes the release identity and signature.
`verifyBundle` takes the versions and SHA-256; `verifyBundleASC` also takes the
signature. `installBundle` takes the versions and signature. These methods use
`latestVersion` and `bundleVersion` to locate the ZIP at
`onekey-bundle-download/<latestVersion>-<bundleVersion>.zip` and the extracted
bundle at `onekey-bundle/<latestVersion>-<bundleVersion>` under iOS Documents
or Android `context.filesDir`. Version strings must pass each platform's
safe-version validation before path construction.
Signed metadata binds the supplied app and bundle versions as separate fields;
verification never recovers them by splitting the combined directory name.
Startup derives the bundle version only by removing the exact running native
app-version prefix and separator from the stored directory name. An absent or
empty suffix fails closed. Local bundle listing and stale-version pruning must
not mistake a hyphen in the current app or bundle version for that separator.
Listing returns only bundles for the running native app version; older-version
artifacts remain subject to the existing best-effort prune rules.
`downloadBundleASC` validates the signature and its managed destination; the
signature sidecar is committed only when installation succeeds.

## Lifecycle and data

Download writes and verifies a partial ZIP before promoting it to the managed
ZIP name. A matching existing ZIP may be reused only after SHA-256 verification.
ASC verification checks signed metadata and ZIP hash before extraction, then
checks extracted files and bundle compatibility. Installation and every bundle
switch, including a pre-launch pending switch, recheck every extracted file
against signed metadata immediately before changing the active bundle. Routine
startup retains its entry-bundle-only hot-path check. Existing cancellation,
resume, cleanup, and fallback ownership remain with the native module.
`clearDownload` and `clearBundle` first cancel each active writer, wait for its
entire download operation (including resume-data and final ZIP writes) to stop,
then remove managed files. A failed or timed-out wait leaves those files intact
and rejects cleanup; the stopped writer later releases its own download slot.
Another download may not enter while cleanup owns the deletion window. On
Android, an aborted writer retains its destination registration until it exits,
so a timed-out cleanup cannot admit a second writer for that same ZIP; the
global cleanup gate is released after any cleanup outcome.
For an identity in the current or fallback installed bundle list, ASC
verification checks the existing signature and every extracted file, then
reuses the directory without extraction or deletion. Failed verification of
an installed identity preserves its directory. A failed verification of an
uninstalled identity cleans up its extraction directory. The installed-identity
path still requires the caller's managed ZIP and verifies its hash before reuse.

## Platform and safety contract

Both platforms derive the same ZIP name from validated versions inside their
own managed download root. iOS uses Foundation and Gopenpgp; Android uses Java
files and BouncyCastle. Both reject paths resolving outside the managed root,
including symlink redirects, and retain signature binding to app and bundle
versions. ZIP download and uncompressed payload limits remain 512 MiB, and the
extraction entry limit remains 4,096. iOS enumerates extracted entries after
extraction; Android counts entries during extraction. The extracted-path
constraints remain in force. Failure rejects the Promise; neither platform
falls back to a caller-provided path.

## Update failure contract

Status: Implemented in source; Android arm64 AAR/Nitro C++ and iOS wrapper host builds plus native wire/normalizer checks passed. Device lifecycle and install acceptance remain pending.

The five update steps are `download`, `downloadASC`, `verifyASC`,
`verifyPackage`, and `install`; `clearDownload`, `clearBundle`,
`resetToBuiltInBundle`, and `clearAllJSBundleData` use `cleanup`.
`verifyExtractedBundle` uses `verifyPackage`, and bundle activation through
`setCurrentUpdateBundleData` uses `install`.
A rejected update Promise and an `update/error`
event carry only `ONEKEY_UPDATE_ERROR:` followed by compact JSON with `code`,
`step`, and optional `httpStatus`. The fixed codes are `NETWORK_ERROR`,
`TIMEOUT`, `HTTP_ERROR`, `PACKAGE_MISSING`, `STORAGE_ERROR`,
`INTEGRITY_FAILED`, `INCOMPATIBLE`, `INSTALL_FAILED`, `CANCELLED`, `UNKNOWN`,
`INVALID_ARGUMENT`, and `SECURITY_REJECTED`. `httpStatus` is present only for
`HTTP_ERROR` with an integer status from 100 through 599. The wire payload never
contains a message, cause, URL, path, stack, or arbitrary additional field.

Validation and boundary rejections use typed native errors at the point of
failure. Platform errors are classified by error domain or exception type; an
unclassified error becomes `UNKNOWN`. Error prose is never parsed to select a
code. Raw diagnostics may be written to local native logs but never to the
Promise rejection or update event. An intentional `clearDownload` or
`clearBundle` cancellation is `CANCELLED` for its Promise and does not emit a
failure event. iOS background resume-data snapshot cancellation remains a
recoverable network interruption. A real I/O failure must not be treated as
cancellation. Signature and hash failures are `INTEGRITY_FAILED` and are not
retried by the caller. Missing managed ZIPs and extracted bundles are
`PACKAGE_MISSING`; an existing file with an unreadable hash is a storage
failure, while a calculated hash mismatch is an integrity failure.

## Performance and acceptance

Downloads and hashing run off the UI thread. Native code remains responsible for
bounded transfer and extraction work. The source implementations are
`ios/ReactNativeBundleUpdate.swift` and
`android/src/main/java/com/margelo/nitro/reactnativebundleupdate/ReactNativeBundleUpdate.kt`;
the public bridge is `src/ReactNativeBundleUpdate.nitro.ts`. Focused acceptance
must cover matching identities, wrong version, missing ZIP, and symlink escape
on iOS and Android, plus installed-directory preservation and uninstalled failure
cleanup. Generated Nitro bindings, native compilation, bridge calls,
and real-device install behavior are distinct checks; their results must be
reported separately.
