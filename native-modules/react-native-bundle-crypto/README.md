# react-native-bundle-crypto

Native update signature verification, file hashing and safe local file operations
for OneKey on iOS and Android. The caller owns download and installation policy.

[Native behavior specification](https://github.com/OneKeyHQ/app-modules/blob/main/native-modules/react-native-bundle-crypto/docs/SPEC.md)

## Installation

```sh
npm install @onekeyfe/react-native-bundle-crypto react-native-nitro-modules
```

> `react-native-nitro-modules` is required as this library relies on [Nitro Modules](https://nitro.margelo.com/).

## Usage

```ts
import { ReactNativeBundleCrypto } from '@onekeyfe/react-native-bundle-crypto';

// ...

const hash = await ReactNativeBundleCrypto.sha256OfFile('/absolute/path/bundle.zip');
if (hash.sha256) {
  console.log('sha256:', hash.sha256);
}

const isSame = ReactNativeBundleCrypto.secureEqualHex(
  '0123456789abcdef',
  '0123456789abcdef'
);
console.log(isSame);
```

## Verification and file operations

`verifyGpgCleartext` and `verifyDetachedAsc` return a validity flag, an optional
verified hash and a failure reason. Check validity before using the hash.
`sha256OfFile` returns a digest or a failure reason; it does not establish that
the file came from a trusted publisher on its own.

`hashDir` and `verifyDirAgainstHashes` support directory integrity checks.
Expected paths must stay inside the directory, and the verifier checks both
hashes and completeness. `validateExtractedPathSafety` checks extracted paths;
`atomicWriteFile`, `safeRename` and `listVersionDirs` expose local file helpers.
The application owns destination selection and sequencing these operations.

The complete types are in
[src/ReactNativeBundleCrypto.nitro.ts](src/ReactNativeBundleCrypto.nitro.ts).
Install the application's iOS pods and rebuild the native binary after adding
this Nitro module. Native verification uses the embedded OneKey public key,
not a caller-provided arbitrary verification key.

## Apple Gopenpgp framework

The Mac Catalyst slice in the vendored `Gopenpgp.xcframework` uses Gopenpgp
`v3.4.1`. It is a static `arm64-apple-ios15.5-macabi` build produced with Go
`1.26.2` and `golang.org/x/mobile`
`v0.0.0-20260821190718-4776eadac327`:

```sh
gomobile bind \
  -tags=mobile,ios \
  -target=ios,iossimulator,maccatalyst/arm64 \
  -iosversion=15.5 \
  -ldflags='-s -w' \
  -o Gopenpgp.xcframework \
  github.com/ProtonMail/gopenpgp/v3/crypto \
  github.com/ProtonMail/gopenpgp/v3/armor \
  github.com/ProtonMail/gopenpgp/v3/constants \
  github.com/ProtonMail/gopenpgp/v3/mime \
  github.com/ProtonMail/gopenpgp/v3/mobile \
  github.com/ProtonMail/gopenpgp/v3/profile
```

## Contributing

Follow the repository's
[native development workflow](https://github.com/OneKeyHQ/app-modules/blob/main/docs/NATIVE_MODULE_DEVELOPMENT.md).

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
