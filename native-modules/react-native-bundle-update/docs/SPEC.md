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
`fileSize`, `sha256`, and optional incremental diff parameters
(`diffDownloadUrl`, `diffFileSize`, `diffSha256`, `diffSignature`). Its result
returns the full URL, versions, full SHA-256, and (when a diff archive was
downloaded or reused) the normalized diff parameters, with no local path.
`downloadBundleASC` takes the release identity, signature, and optional diff
parameters. `verifyBundle` takes the versions, `sha256`, and optional
`signature`, `diffSha256`, and `diffSignature`. `verifyBundleASC` takes the
versions, `sha256`, `signature`, optional full fallback `downloadUrl` /
`fileSize`, and optional diff parameters. `installBundle` takes the versions and
signature. These methods use `latestVersion` and `bundleVersion` to locate the
full ZIP at `onekey-bundle-download/<latestVersion>-<bundleVersion>.zip`, the
diff ZIP at `onekey-bundle-download/<latestVersion>-<bundleVersion>.diff.zip`,
its sidecar descriptor at
`onekey-bundle-download/<latestVersion>-<bundleVersion>.diff.zip.meta.json`,
isolated per-run diff staging directories under
`onekey-bundle/.diff-staging/<latestVersion>-<bundleVersion>-<runId>`, and the
extracted bundle at `onekey-bundle/<latestVersion>-<bundleVersion>` under iOS
Documents or Android `context.filesDir`. Version strings must pass each
platform's safe-version validation before path construction (rejecting empty
strings, leading/trailing dots, and reserved `.diff`, `-diff`, `.diff-staging`,
and `-diff-staging` suffixes so valid bundle identities never collide with diff
or staging artifacts).
Signed metadata binds the supplied app and bundle versions as separate fields;
verification never recovers them by splitting the combined directory name.
Startup derives the bundle version only by removing the exact running native
app-version prefix and separator from the stored directory name. An absent or
empty suffix fails closed. Local bundle listing and stale-version pruning must
not mistake a hyphen in the current app or bundle version for that separator.
Listing returns only bundles for the running native app version; older-version
artifacts remain subject to the existing best-effort prune rules.
`downloadBundleASC` validates the signature and its managed destination; the
installed signature sidecar (`asc/<latestVersion>-<bundleVersion>-signature.asc`)
is committed only when installation succeeds.

## Incremental diff update contract

Status: Implemented in source. Focused Swift XCTest (`BundleDiffReconstructionTests`)
and Kotlin JVM (`BundleDiffReconstructionTest`) suites verify manifest parsing,
partition validation, built-in path mapping, cumulative payload limits, and
restart/fallback descriptor resolution. Full Nitro bridge and real-device
lifecycle acceptance remain pending.

1. **Download selection and fallback**:
   - When `diffDownloadUrl` (HTTPS), `diffSha256` (64-char hex, normalized to
     lowercase), and `diffSignature` (non-empty) are provided and the target
     identity is not marked for session fallback, `downloadBundle` downloads (or
     reuses after SHA-256 verification) `<latestVersion>-<bundleVersion>.diff.zip`
     and writes `<latestVersion>-<bundleVersion>.diff.zip.meta.json` binding the
     target versions, full `downloadUrl`, `fileSize`, normalized `sha256`, and
     normalized `diffSha256`.
   - Full fallback parameters are recorded only after single-flight download
     registration succeeds so a rejected duplicate call cannot overwrite an
     in-flight download's fallback parameters.
   - If the diff download fails for any non-cancellation reason, the module
     marks the target identity for session fallback, deletes partial diff
     artifacts, and immediately downloads the full `<latestVersion>-<bundleVersion>.zip`.
2. **Dual GPG verification and local reconstruction (`verifyBundleASC`)**:
   - `verifyBundleASC` coordinates per-target single-flight execution and
     registers cancellation tracking before starting diff verification so
     `clearDownload` / `clearBundle` cancels and waits for both reconstruction
     and any fallback download before removing files.
   - When verifying a diff archive, `verifyBundleASC` first verifies two PGP
     cleartext signatures against the embedded OneKey public key:
     `diffSignature` bound to `fileName == "diff-manifest.json"` and `signature`
     bound to `fileName == "metadata.json"`, both bound to `appType`,
     `appVersion`, and `bundleVersion`.
   - Next, it verifies the streaming SHA-256 of `.diff.zip` against `diffSha256`
     (including when `isInstalled` is true, so a corrupted managed diff archive
     never passes ASC verification).
   - It extracts `.diff.zip` into a unique per-run directory
     `onekey-bundle/.diff-staging/<latestVersion>-<bundleVersion>-<runId>`.
   - Both `diff-manifest.json` and staged `metadata.json` are capped at 16 MiB
     and their streaming SHA-256 hashes are verified against the GPG-signed
     digests (and `manifest.targetMetadataSha256`) before reading their bytes
     into memory or parsing JSON.
   - `changedFiles` and `reusedFiles` must form a bijective partition of the
     file entries in `metadata.json`. Cumulative entry count (`<= 4,096`) and
     cumulative uncompressed byte size (`<= 512 MiB`) are enforced across all
     `changedFiles` plus `reusedFiles`.
   - Each `reusedFiles` entry is copied and SHA-256 verified from candidate
     same-`appVersion` local OTA directories (active first, then newest) or from
     the built-in app package (`Bundle.main` on iOS mapping `main.jsbundle.hbc`
     -> `main.jsbundle`, `assets/**`, and `web-embed/**`; `context.assets` and
     APK `res/drawable*` / `res/raw*` entries on Android mapping
     `main.jsbundle.hbc` -> `index.android.bundle`, `assets/**`, and
     `web-embed/**`).
   - After deleting `diff-manifest.json` from staging, the module validates path
     safety, every file's SHA-256 against `metadata.json`, and bundle-pair /
     storage-schema compatibility before promoting the staging directory to
     `onekey-bundle/<latestVersion>-<bundleVersion>` and recording the verified
     `signature` in `.diff.zip.meta.json`.
   - If diff extraction (including an empty or corrupt archive, whereas zip-bomb
     size/entry violations and symlink/traversal escapes remain hard
     `SECURITY_REJECTED` failures) or local file reuse fails with
     `FallbackRequired`, `verifyBundleASC` deletes the diff archive and downloads
     + verifies the full `.zip` using explicit `params.downloadUrl`, in-memory
     fallback parameters, or the persisted `.diff.zip.meta.json` descriptor
     (surviving process restarts).
3. **Package verification (`verifyBundle`) ordering and trust anchor**:
   - If the full `.zip` exists on disk, `verifyBundle` checks its streaming
     SHA-256 against `params.sha256`.
   - If the reconstructed directory `onekey-bundle/<latestVersion>-<bundleVersion>`
     exists on disk alongside `.diff.zip`, `verifyBundle` requires an
     authenticated `metadata.json` digest bound to `params.sha256` — either from
     the current module instance's verified-ASC cache or by re-verifying the
     version-bound PGP `signature` (from `params.signature`, `.diff.zip.meta.json`,
     or `asc/`). If no authenticated digest can be established or `params.sha256`
     does not match, `verifyBundle` fails closed with `INTEGRITY_FAILED`.
   - If `verifyBundle` is called after `downloadBundle` before `verifyBundleASC`
     has reconstructed the target directory, it verifies the downloaded
     `.diff.zip` streaming SHA-256 against `params.diffSha256` or the
     `.diff.zip.meta.json` descriptor bound to `params.sha256`.

## Lifecycle and data

Download writes and verifies a partial ZIP before promoting it to the managed
ZIP name. A matching existing ZIP may be reused only after SHA-256 verification.
ASC verification checks signed metadata and ZIP hash before extraction, then
checks extracted files and bundle compatibility. Installation and every bundle
switch, including a pre-launch pending switch, recheck every extracted file
against signed metadata immediately before changing the active bundle. Routine
startup retains its entry-bundle-only hot-path check. Existing cancellation,
resume, cleanup, and fallback ownership remain with the native module.
Extracted-file verification derives each metadata key from the directory it
actually walks and MUST NOT depend on the string form a caller used to name that
directory: the same tree may be reachable through a symlinked and a resolved
path, and Android `context.filesDir` can itself be symlinked, so mixing the two
forms produced absolute-path keys and rejected every file on affected devices.
The walked directory MUST resolve to the managed bundle directory for the
supplied identity, and a file that cannot be expressed relative to it MUST fail
closed with a local diagnostic rather than be looked up by an absolute path.
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

## OTA storage-schema guard

Status: implemented on iOS and Android. The policy unit suites
(`BundleStorageSchemaPolicyTests`, `BundleStorageSchemaPolicyTest`) run the
shipping decision logic with an in-memory ledger reader; they do not execute a
native runtime reload or a signed-OTA install.

An OTA bundle may declare the reserved optional metadata key
`storageSchemaVersion` in `metadata.json`. The key is reserved: it is never
treated as a file entry. It prevents an old JavaScript bundle from returning to
retained AsyncStorage data once the host app has started migrating AppStorage or
Jotai to MMKV.

The guard reads the host app's native migration ledger: the
`onekey_native_storage_migration` shared preferences on Android, and the
`UserDefaults` keys prefixed `onekey_native_storage_migration_` on iOS, for the
`app-storage-v1` and `jotai-storage-v1` entries. The decision is:

| `storageSchemaVersion` | Migration ledger | Result |
| --- | --- | --- |
| Missing or empty (legacy bundle) | Both entries empty | Accept |
| Missing or empty | Any entry non-empty, including migrating or reset | Reject |
| `mmkv-v1` | Either state | Accept |
| Any other non-empty value | Either state | Reject |

The guard runs when the current bundle is validated at startup and when an
extracted or installed bundle is validated. Cached validated-bundle metadata is
re-evaluated after a JS runtime reload, even when entry-bundle hashes are cached,
because the host migration state can change while the native bundle cache
survives. A rejected bundle is reported as an `INCOMPATIBLE` update failure on
the step that raises the typed error; at startup the validated-bundle cache is
invalidated so no incompatible OTA bundle path is returned.

The guard only reads the host ledger. It never opens MMKV or AsyncStorage,
deletes or migrates storage data, or emits an `update/error` event of its own. An
old native binary that predates the guard cannot enforce it, so MMKV-migration
OTA bundles must still target a host binary that contains this guard.

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
