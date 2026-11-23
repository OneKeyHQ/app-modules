<div align="center">

# @onekeyfe/react-native-bundle-crypto

**Native update signature verification, file hashing and safe local file operations for OneKey apps.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-bundle-crypto?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-bundle-crypto)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-bundle-crypto?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Nitro Modules](https://img.shields.io/badge/Nitro_Modules-4D6BFE)

</div>

Verifies signed update metadata, hashes bundle files, checks a bundle directory
against expected hashes, and performs the local file operations sibling modules
need. Verification uses an embedded OneKey public key that never crosses into
JavaScript; the caller owns download, signature-file acquisition, metadata parsing
and the decision to install an update. iOS uses Gopenpgp and CommonCrypto, Android
uses BouncyCastle and `MessageDigest`, with the same embedded key and directory
boundary on both.

## Features

- **Update signature checks** — `verifyGpgCleartext` validates signed metadata and
  returns its hash only after the signature and expected fields match;
  `verifyDetachedAsc` binds a detached ASC to the expected file name.
- **Streaming file hashing** — `sha256OfFile` streams input instead of loading the
  whole file into memory and reports a failure reason when it cannot hash.
- **Directory integrity** — `hashDir` and `verifyDirAgainstHashes` check every
  non-exempt file and reject missing, extra or mismatched entries.
- **Path-traversal guard** — `validateExtractedPathSafety` rejects symbolic links
  and paths that resolve outside the extracted directory.
- **Local file verbs** — atomic write, safe rename and version-directory
  enumeration for bundle maintenance code.
- **Constant-time comparison** — `secureEqualHex` compares hex digests without
  leaking length-independent timing.

## Installation

```sh
yarn add @onekeyfe/react-native-bundle-crypto react-native-nitro-modules
```

Install the application's iOS pods and rebuild the native application; this is a
Nitro module and is not registered by a JavaScript-only install. The iOS pod
vendors the `Gopenpgp.xcframework` (including a Mac Catalyst slice) and repairs
its npm-stripped framework links during pod installation. Android resolves
BouncyCastle through autolinking. On iOS the pod also depends on
`ReactNativeNativeLogger`, so include `@onekeyfe/react-native-native-logger` in the
application's pods. No permissions or `Info.plist` keys are required.

## Usage

```ts
import { ReactNativeBundleCrypto } from '@onekeyfe/react-native-bundle-crypto';

const hash = await ReactNativeBundleCrypto.sha256OfFile(
  '/absolute/path/bundle.zip'
);
if (hash.sha256) {
  console.log('sha256:', hash.sha256);
}

const isSame = ReactNativeBundleCrypto.secureEqualHex(
  '0123456789abcdef',
  '0123456789abcdef'
);

const verified = await ReactNativeBundleCrypto.verifyGpgCleartext(signedMessage);
if (verified.valid && verified.sha256) {
  // Use the hash only after verification succeeded.
}
```

The application owns where files are written and the order in which verification,
hashing and installation happen.

## API

| Method | Result | Platforms | Description |
| --- | --- | :-: | --- |
| `verifyGpgCleartext(signedMessage)` | `Promise<VerifyGpgCleartextResult>` | iOS, Android | Verifies PGP cleartext-signed metadata for `metadata.json`; returns `valid`, `sha256?`, `reason?`. |
| `verifyDetachedAsc(ascContent)` | `Promise<VerifyDetachedAscResult>` | iOS, Android | Verifies a detached `SHA256SUMS.asc` against the expected file name. |
| `sha256OfFile(filePath)` | `Promise<Sha256Result>` | iOS, Android | Streams a file and returns a lowercase SHA-256 hex digest or `failureReason`. |
| `secureEqualHex(a, b)` | `boolean` | iOS, Android | Constant-time comparison of two hex strings after a length check. |
| `hashDir(dirPath)` | `Promise<DirHashEntry[]>` | iOS, Android | Returns relative paths and SHA-256 hashes for files under a directory. |
| `verifyDirAgainstHashes(dirPath, entries)` | `Promise<boolean>` | iOS, Android | Checks hashes and completeness; entries must resolve below `dirPath`. |
| `validateExtractedPathSafety(destination)` | `Promise<boolean>` | iOS, Android | Rejects symlinks and paths outside the extracted directory. |
| `atomicWriteFile(filePath, contentUtf8)` | `Promise<void>` | iOS, Android | Writes through a temporary file and rename. |
| `safeRename(fromPath, toPath)` | `Promise<void>` | iOS, Android | Same-volume move that removes an existing target first. |
| `listVersionDirs(parentDir)` | `Promise<string[]>` | iOS, Android | Lists version sub-directory names under a parent directory. |

The full result and entry types are in
[`src/ReactNativeBundleCrypto.nitro.ts`](src/ReactNativeBundleCrypto.nitro.ts).
See [`docs/SPEC.md`](docs/SPEC.md) for the failure taxonomy, directory rules and
acceptance evidence.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| GPG cleartext and detached ASC verification | ✅ Gopenpgp | ✅ BouncyCastle | — |
| File and directory hashing | ✅ | ✅ | — |
| Path-traversal and symlink guard | ✅ | ✅ | — |
| Atomic write, safe rename, version dirs | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> The verification key is the embedded OneKey public key; there is no parameter
> for a caller-provided key. Check `valid` before using the returned `sha256` — a
> failed verification returns a reason and no hash.

> [!WARNING]
> `sha256OfFile` proves only that a file hashes to a digest. It does not establish
> publisher trust, and its failure-reason taxonomy differs between iOS and
> Android. The caller must limit download sizes and decide how failed
> verification is handled.

> [!NOTE]
> `hashDir` and `verifyDirAgainstHashes` exempt files whose exact basename is
> `metadata.json` or `.DS_Store`. Expected paths that are empty, absolute, or
> resolve outside `dirPath` invalidate the verification.

## References

- [Package spec](docs/SPEC.md)
- [Example page](../../example/react-native/pages/BundleCryptoTestPage.tsx)
- [Changelog](../../CHANGELOG.md)

## License

MIT for OneKey-owned code. [`LICENSE`](LICENSE) also contains identified Gopenpgp
MIT and Go mobile BSD notices and states that it is not a complete audit of the
vendored framework's linked dependency closure.
