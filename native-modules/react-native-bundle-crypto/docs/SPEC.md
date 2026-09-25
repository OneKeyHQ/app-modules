# Bundle Crypto Native Contract

Status: Implemented. The native checks listed below provide focused runtime evidence only where noted.

## Purpose and ownership

This package verifies signed update metadata, computes file hashes, checks a bundle directory against expected hashes, and provides local file operations to other OneKey native modules. The caller owns download, signature-file acquisition, metadata parsing, and the decision to install an update. The embedded OneKey public key is native-only and is not returned to JavaScript.

## Public API and data

- `verifyGpgCleartext` verifies a PGP cleartext-signed JSON message and returns its `sha256` field only after successful signature verification. `verifyDetachedAsc` verifies a PGP cleartext-signed SHA256SUMS body and returns its first 64-character hex token. Verification failures return `valid: false` with a reason and no hash.
- `sha256OfFile` streams a file and returns either a lowercase SHA-256 hex digest or a failure reason. The exact failure-reason taxonomy differs by platform.
- `secureEqualHex` compares two UTF-8 strings byte by byte after checking equal length.
- `hashDir` returns relative paths and SHA-256 hashes for files under a directory. `verifyDirAgainstHashes` checks that every non-exempt on-disk file has a matching expected hash and that every expected file exists. Files whose exact basename is `metadata.json` or `.DS_Store` are exempt from hashing.
- `validateExtractedPathSafety` rejects symbolic links and paths resolving outside an extracted directory. `atomicWriteFile`, `safeRename`, and `listVersionDirs` perform local file operations; the caller owns when to invoke them.

## Directory hash safety and failure

Each `DirHashEntry.relativePath` passed to `verifyDirAgainstHashes` must name a file below `dirPath`. Empty paths, absolute paths, and paths that resolve to the root or outside it are invalid. Resolution includes `..` segments and symbolic links. Invalid entries, missing expected files, unlisted files, and hash mismatches return `false`. An outside path is never accepted merely because the named file exists. Both Android and iOS enforce this boundary before checking directory contents.

## Lifecycle, platform behavior, and resource limits

The verification and file operations are synchronous inside each native core; the Nitro wrappers return promises for the asynchronous API methods. No long-lived request state is retained by the core. File hashing streams input rather than loading entire files into memory. Android uses BouncyCastle for PGP verification and Java `MessageDigest`; iOS uses Gopenpgp and CommonCrypto. Both platforms use the same embedded public key and directory-path boundary. The caller must separately limit downloaded data and decide how to handle failed verification.

## Conformance and acceptance

- Android implementation: `android/src/main/java/com/margelo/nitro/reactnativebundlecrypto/BundleCryptoCore.kt`. JVM tests: `android/src/test/java/com/margelo/nitro/reactnativebundlecrypto/BundleCryptoCoreTest.kt`.
- iOS implementation: `ios/BundleCryptoCore.swift`. XCTest: `ios/tests/BundleCryptoCoreTests.swift`.
- Focused tests exercise the real native cores for signed-message rejection, directory hash matching and failure, invalid expected paths, and extracted symlink rejection. The iOS tests also cover a valid signed metadata vector. The Android JVM test does not yet verify extraction of a valid signed JSON body because its host runtime lacks a real `org.json.JSONObject` implementation.
- The focused tests do not prove Nitro bridge behavior, download/install integration, actual archive extraction, or physical-device filesystem behavior. Those remain integration and device acceptance cases.
