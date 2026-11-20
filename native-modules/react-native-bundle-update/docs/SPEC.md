# Bundle Update Native Contract

Status: Implemented in source. Device acceptance remains pending.

## Purpose and ownership

This module downloads, verifies, extracts, and activates OneKey JS bundles on
iOS and Android. The caller supplies release identity and signed metadata. The
native module owns the download directory, ZIP path, extracted bundle directory,
signature file, and their cleanup. Callers do not choose a local ZIP path.
The main and background JavaScript runtimes have separate heaps, while native
files and preferences are shared resources.

## Desktop Node entry point

`@onekeyfe/react-native-bundle-update/node` exports `BundleUpdateNode` in
ESM and CommonJS. Run one instance per managed user-data directory in the
Desktop main process. The Node updater and its callers share a single JS
runtime. Electron supplies the directory, native app version and build number,
trusted PGP public key, active/fallback bundle state, restart, and optional
download, progress and logger hooks. The package defaults to Node HTTPS for
downloads. Electron supplies its proxy-aware transport to the packaged
downloader. An invalid redirect is rejected through the update error contract.
The React Native package root continues to expose the Nitro module. The Node
ESM and CommonJS entry points include compatible TypeScript declarations.

The Node updater owns the managed `onekey-bundle-download` and `onekey-bundle`
directories, including partial download state and diff descriptors. Its
download, package verification, ASC verification, installation, listing,
pruning, and cleanup methods use the Desktop OTA wire contract. Install and
switch verify the extracted bundle before updating the host's active pointer
or requesting a restart. Current and up to three fallback bundles retain their
existing on-disk layout. Node failures provide the existing
`ONEKEY_UPDATE_ERROR` payload with code, step, and optional HTTP status.

Desktop retains `build/` archives and SHA-512 per-file metadata. The signed
JSON and ZIP digests use SHA-256. Both cleartext signatures must use the
configured trusted key and bind the file name, Electron app type, app version,
and bundle version. Diff verification limits control JSON to 16 MiB and bounds
extraction/reconstruction to 4,096 entries and 512 MiB. It reuses files from
the current version's local bundles and configured built-in/resource
locations. Unavailable reuse or an unusable archive may trigger full-bundle
fallback. Signature failures and archive security violations are terminal.
Only the trusted Desktop host can enable its existing development-only
verification setting.

Same-destination downloads join for the same checksum, including full
downloads initiated by verification fallback. If a managed full ZIP already
exists, the downloader verifies it and reuses or replaces it before returning
a full download result. Archive and verification operations
track cancellation until settled so cleanup waits before removing their files.
Full and diff ASC runs verify uninstalled bundles in per-run staging directories
and check cancellation before publishing the verified bundle.
The package verification ordering and release-record requirements below also
apply to Desktop. Persisted descriptors allow full fallback to resume across Node
instances. The Node package tests exercise the shipped entry points, signed
full and diff archives, local reuse, persisted fallback and cleanup. Electron
launch and installation runs are separate acceptance checks.

The Node downloader applies a 15-minute wall-clock deadline across probes,
redirects, retries, transfers, and the final hash. Expiry is `TIMEOUT` and a
caller abort is `CANCELLED`. Probe, single-stream, and segmented retries honor
valid `Retry-After` values up to 60 seconds. A successful HTTP 200 response to
an `If-Range` request restarts from byte zero with the new response headers;
the release checksum and expected length still apply. Desktop archive
verification hashes in asynchronous chunks, observes the verification run's
cancellation signal, and rejects archives over its 2 GiB limit. After store
state for installation is committed, deletion of an evicted fallback
directory is best effort. An explicit cache clear rejects with
`STORAGE_ERROR` if an extracted directory cannot be deleted, before clearing
the active bundle record.

The Desktop range transfer implementation is provided by
`@onekeyfe/react-native-range-downloader/node`. The bundle updater preserves its
download exports and translates transport failures to its existing OTA error
contract.

Desktop installation reads the active bundle and fallback list after
verification, then commits them synchronously. Rollback history follows the
order in which verified installs commit. Before recording full fallback or
deleting the diff archive for full fallback, Desktop ASC re-reads the managed
descriptor in the coordinated download step. A changed release identity,
archive digest, or fallback flag rejects the stale ASC run with `INCOMPATIBLE`.
A terminal ASC failure retains its original error and cleans up the diff only
if the descriptor still matches those fields and no diff download is active.

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
diff ZIP in the dedicated `.diff` download subdirectory at
`onekey-bundle-download/.diff/<latestVersion>-<bundleVersion>.diff.zip`,
its sidecar descriptor at
`onekey-bundle-download/.diff/<latestVersion>-<bundleVersion>.diff.zip.meta.json`,
isolated per-run diff staging directories under
`onekey-bundle/.diff-staging/<latestVersion>-<bundleVersion>-<runId>`, and the
extracted bundle at `onekey-bundle/<latestVersion>-<bundleVersion>` under iOS
Documents or Android `context.filesDir`. Version strings must pass each
platform's safe-version validation before path construction (rejecting empty
strings and leading/trailing dots while retaining backward compatibility with
all valid alphanumeric/`.`/`-`/`_` version names, including legacy installed
versions ending in `-diff` or `.diff-staging`, because diff download and staging
artifacts live inside dot-prefixed `.diff/` and `.diff-staging/` directories).
Signed metadata binds the supplied app and bundle versions as separate fields;
verification never recovers them by splitting the combined directory name.
Startup derives the bundle version only by removing the exact running native
app-version prefix and separator from the stored directory name. An absent or
empty suffix fails closed. Native bundle listing and stale-version pruning use
the complete running app-version prefix to preserve bundle versions containing
hyphens. Native listing returns bundles for the running app version. Desktop
also lists older cached versions until they are removed by best-effort pruning.
Desktop listing, pruning, and diff source discovery use explicit app-version
and bundle-version pairs from the active and fallback records and from a
matching, regular sidecar in the managed diff directory. The sidecar filename
must match its safe version fields. Conflicting records leave the cache
identity unresolved, and pruning retains the corresponding cached artifacts
and fallback records. An unrecorded legacy name first matches the complete
running app-version prefix. For other legacy names with a numeric
`major.minor.patch` app-version prefix, the first hyphen separates the release
from the entire bundle-version suffix. Remaining legacy names retain the previous
listing behavior and require an explicit record before stale pruning can delete
them. Full downloads strip the `.zip` extension and diff downloads strip the
`.diff.zip` extension within their respective directories.
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
     identity is not marked for fallback (in memory or via a persisted
     `fallbackRequired: true` descriptor for the same full `sha256`),
     `downloadBundle` downloads (or reuses after SHA-256 verification)
     `onekey-bundle-download/.diff/<latestVersion>-<bundleVersion>.diff.zip`
     and writes
     `onekey-bundle-download/.diff/<latestVersion>-<bundleVersion>.diff.zip.meta.json`
     binding the target versions, full `downloadUrl`, `fileSize`, normalized
     `sha256`, and normalized `diffSha256` (preserving any previously verified
     `signature` already recorded in `.diff.zip.meta.json` for the same hashes).
     A record write failure returns `STORAGE_ERROR` and retains the verified
     diff ZIP for a retry.
   - Native Range probes, resumed runs, and single-stream downloads enforce the
     same 512 MiB archive limit before accepting or promoting an artifact. A
     stopped segment pool does not cancel the enclosing Android update or its
     next transfer.
     Android single-stream `206` responses must begin at the requested offset.
     The downloader appends only to a partial file of exactly that positive
     length. An offset of zero starts a new stream and truncates any old partial.
     On iOS, HTTP 416 from a single-stream download invalidates that archive's
     URLSession resume snapshot, including a pending background snapshot write.
     It returns retryable `HTTP_ERROR` with status 416 so the next caller retry
     can start a fresh single-stream request if needed. Diff eligibility and
     surviving Range segments are retained.
   - Full fallback parameters are recorded only after single-flight download
     registration succeeds so a rejected duplicate call cannot overwrite an
     in-flight download's fallback parameters.
   - If the diff download encounters a retryable transport interruption
     (`NETWORK_ERROR` or `TIMEOUT`, including iOS background pause resume-data
     snapshots, and retryable HTTP responses on iOS and Desktop), the module
     preserves usable partial/resume artifacts and diff eligibility and rejects with
     that retryable error so the caller can resume. On iOS, a transient Range
     HTTP result keeps using the bounded segmented retry budget. After that
     budget is exhausted, the updater returns `HTTP_ERROR` with the original
     retryable status (408, 416, 429, or 5xx except 501 and 505).
   - If the diff download fails with a non-retryable archive or HTTP error
     (such as HTTP 404 or SHA-256 mismatch), the module marks the target
     identity for session fallback, deletes partial diff artifacts, and
     immediately downloads the full `<latestVersion>-<bundleVersion>.zip`.
2. **Dual GPG verification and local reconstruction (`verifyBundleASC`)**:
   - `verifyBundleASC` coordinates per-target single-flight execution and
     registers per-target cancellation tracking before starting diff
     verification so `clearDownload` / `clearBundle` cancels and waits for both
     reconstruction and any fallback download before removing files. The
     native admission and cleanup barrier covers both Nitro instances sharing
     the app process and storage. A cancelled verification cannot publish a
     successful result. On iOS,
     local diff verification uses its per-target verification slot without
     acquiring the global transfer slot, so local verification of version B can
     complete while version A is downloading; the global transfer slot is
     requested only if version B enters full-bundle network fallback.
   - A diff can be reconstructed only for the existing download record with
     matching target versions and both archive digests. If a valid record
     cannot be established, the updater must obtain and verify the full ZIP
     before reporting success or reject verification. An explicit signature
     authenticates metadata for a version and does not establish a missing
     full ZIP digest.
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
     Before verification and during stale-version pruning, the module makes a
     best-effort sweep of abandoned staging entries, including those for the
     running app version. It preserves staging directories owned by live runs
     across native module instances in the app process.
   - Both `diff-manifest.json` and staged `metadata.json` are capped at 16 MiB
     and their streaming SHA-256 hashes are verified against the GPG-signed
     digests (and `manifest.targetMetadataSha256`) before reading their bytes
     into memory or parsing JSON.
   - `changedFiles` and `reusedFiles` must form a bijective partition of the
     file entries in `metadata.json`. Cumulative entry count (`<= 4,096`) and
     cumulative uncompressed byte size (`<= 512 MiB`) are enforced across all
     `changedFiles` plus `reusedFiles`.
   - Each `reusedFiles` entry is copied and SHA-256 verified from candidate
     same-`appVersion` local OTA directories (active first, then newest; a
     candidate whose SHA-256 does not match the expected hash is skipped before
     enforcing the remaining byte budget so an unrelated larger file in an
     earlier directory cannot block a matching smaller file in a later candidate)
     or from the built-in app package (`Bundle.main` on iOS mapping
     `main.jsbundle.hbc` -> `main.jsbundle`, `assets/**`, and `web-embed/**`;
     `context.assets` and base + installed split APK (`sourceDir` +
     `splitSourceDirs`) `res/drawable*` / `res/raw*` entries on Android mapping
     `main.jsbundle.hbc` -> `index.android.bundle`, `assets/**`, and
     `web-embed/**`).
   - After deleting `diff-manifest.json` from staging, the module validates path
     safety, every file's SHA-256 against `metadata.json`, and bundle-pair /
     storage-schema compatibility. It records the verified `signature` in
     `.diff.zip.meta.json` before promoting the verified staging directory to
     `onekey-bundle/<latestVersion>-<bundleVersion>`.
   - If diff extraction (including an empty or corrupt archive, whereas zip-bomb
     size/entry violations and symlink/traversal escapes remain hard
     `SECURITY_REJECTED` failures) or local file reuse fails with
     `FallbackRequired`, `verifyBundleASC` persists `fallbackRequired: true` in
     `.diff.zip.meta.json`, deletes `.diff.zip`, and downloads + verifies the
     full `.zip` using explicit `params.downloadUrl`, in-memory fallback
     parameters, or the persisted `.diff.zip.meta.json` descriptor (surviving
     process restarts and resuming the full download if a retryable network
     interruption occurs before the final `.zip` is promoted).
     A failed fallback descriptor write returns `STORAGE_ERROR` before
     marking session fallback or removing the existing diff artifacts.
3. **Package verification (`verifyBundle`) ordering and trust anchor**:
   - If the full `.zip` exists on disk, `verifyBundle` checks its streaming
     SHA-256 against `params.sha256`.
   - Without a full ZIP, `verifyBundle` requires the managed diff ZIP. If a
     target directory exists, the complete tree and its real `metadata.json`
     must verify. The metadata digest must be authenticated and associated
     with `params.sha256` by either
     a live verified-ASC record or a matching persisted download descriptor
     and a re-verified version-bound PGP `signature` (from `params.signature`,
     `.diff.zip.meta.json`, or `asc/`). If no authenticated digest can be
     established or `params.sha256` does not match, `verifyBundle` fails closed
     with `INTEGRITY_FAILED`.
   - If the target directory is absent after `downloadBundle`, `verifyBundle`
     verifies the downloaded
     `.diff.zip` streaming SHA-256 against the descriptor bound to
     `params.sha256`. An explicit `params.diffSha256` must also match that
     descriptor.

## Lifecycle and data

Download writes and verifies a partial ZIP before promoting it to the managed
ZIP name. A matching existing ZIP may be reused only after SHA-256 verification.
ASC verification checks signed metadata and ZIP hash before extraction, then
checks extracted files and bundle compatibility. Whole-directory verification
exempts the root `metadata.json` control file. A nested file with that name must
appear in the manifest and pass its hash check. Every manifest path must
correspond to a regular file whose hash was verified. Installation and every
bundle switch, including a pre-launch pending switch, recheck every extracted file
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
versions. Android supports API 24 and later from full and diff download
admission through extraction, verification, and bundle promotion. Managed-path
checks reject symbolic links in and below the managed root, including dangling
links. Android syncs each new diff descriptor before atomically renaming it
from a uniquely named temporary file in the same directory. The native process
protects the temporary file and its versioned destination from stale-version
pruning for the lifetime of the write. Pruning can reclaim abandoned temporary
files, including the legacy versioned names, without following symbolic links.
A failed rename leaves the prior record in place.
ZIP download and uncompressed payload limits remain 512 MiB, and the
extraction entry limit remains 4,096. The updater and its Range Downloader
peer are released together so the native byte-limit contract is present.
iOS enumerates extracted entries after
extraction; Android counts entries during extraction. The extracted-path
constraints remain in force. Failure rejects the Promise; neither platform
falls back to a caller-provided path.

## Native bundle protocol

For split-runtime OTA bundles, `metadata.json` declares
`requiresBackgroundBundle` and `backgroundProtocolVersion`. The installer
requires the metadata version to match the host's supported protocol before
activating the main and background bundle pair, including in developer mode.
Startup rechecks the protocol before returning the active downloaded bundle.

Protocol 2 requires the two-argument Nitro SegmentSlider `setValue` ABI.
Protocol 1 hosts reject protocol 2 OTA bundles. Protocol 2 hosts reject
protocol 1 OTA bundles, whose older JS can call the one-argument method.
The app-monorepo native OTA packager must emit the same protocol as the host.

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
