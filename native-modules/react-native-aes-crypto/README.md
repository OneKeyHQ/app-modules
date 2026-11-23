<div align="center">

# @onekeyfe/react-native-aes-crypto

**Native AES, PBKDF2, HMAC, hashing and random-value primitives for OneKey apps on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-aes-crypto?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-aes-crypto)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-aes-crypto?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Exposes the crypto primitives OneKey needs without a JavaScript crypto
implementation: AES-CBC/CTR encryption, AES-GCM authenticated encryption,
PBKDF2, HMAC-SHA256/512, SHA-1/256/512 and random values. Every call crosses a
React Native TurboModule and returns a promise; the module has no Web
implementation. Applications still own key storage, nonce uniqueness and the
decision of when a failure is fatal.

## Features

- **Audited-style AEAD surface** — AES-GCM with a stricter-than-RFC contract:
  12-byte nonce, non-empty AAD, and ciphertext with a trailing 16-byte tag.
- **Cross-platform byte compatibility** — iOS CryptoKit/CommonCrypto and Android
  JCE/BouncyCastle are pinned to the same formats, so ciphertexts match.
- **Symmetric helpers** — AES-128/192/256 in CBC (PKCS#7) and CTR modes with hex
  input and hex output.
- **Key derivation and MACs** — PBKDF2 with SHA-1/256/512 PRFs, HMAC-SHA256 and
  HMAC-SHA512.
- **Random values** — `randomUuid()` and a cryptographically secure
  `randomKey(length)`.
- **Fail-loud validation** — empty, malformed or non-integer arguments reject
  with a stable error code instead of producing zero-length input.

## Installation

```sh
yarn add @onekeyfe/react-native-aes-crypto
```

Install the application's iOS pods and rebuild the native binary; this package is
a TurboModule, so a JavaScript-only install does not register it. The iOS pod
links `Security` and `CryptoKit`; Android resolves its dependencies through
autolinking. No permissions or `Info.plist` keys are required.

## Usage

```ts
import {
  sha256,
  randomKey,
  aesGcmEncrypt,
  aesGcmDecrypt,
} from '@onekeyfe/react-native-aes-crypto';

// Every string argument is hex-encoded bytes; every result is hex.
const digest = await sha256('68656c6c6f');

const key = await randomKey(32); // 32 random bytes, 64 hex characters
const nonce = await randomKey(12); // GCM requires exactly 12 nonce bytes
const sealed = await aesGcmEncrypt('deadbeef', key, nonce, '0102');
const opened = await aesGcmDecrypt(sealed, key, nonce, '0102');

console.log(digest, opened); // opened === 'deadbeef'
```

The application owns key storage, nonce uniqueness and long-term encoding of the
hex strings; the module does not persist anything.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `encrypt` | `(data, key, iv, algorithm) => Promise<string>` | iOS, Android | AES encrypt; `algorithm` is `aes-128-cbc`, `aes-192-cbc`, `aes-256-cbc`, or the matching `-ctr` name. |
| `decrypt` | `(ciphertext, key, iv, algorithm) => Promise<string>` | iOS, Android | Inverse of `encrypt`; returns hex. |
| `aesGcmEncrypt` | `(data, key, nonce, aad) => Promise<string>` | iOS, Android | Returns hex `ciphertext || 16-byte tag`; empty plaintext is allowed. |
| `aesGcmDecrypt` | `(ciphertextWithTag, key, nonce, aad) => Promise<string>` | iOS, Android | Verifies the tag; parameters must match the encryption call byte for byte. |
| `pbkdf2` | `(password, salt, cost, length, algorithm) => Promise<string>` | iOS, Android | PBKDF2; `length` is in bits, `algorithm` is `sha1`, `sha256` or `sha512`. |
| `hmac256` | `(data, key) => Promise<string>` | iOS, Android | HMAC-SHA256 over hex-decoded bytes. |
| `hmac512` | `(data, key) => Promise<string>` | iOS, Android | HMAC-SHA512 over hex-decoded bytes. |
| `sha1` | `(text) => Promise<string>` | iOS, Android | SHA-1 digest of hex-decoded bytes. |
| `sha256` | `(text) => Promise<string>` | iOS, Android | SHA-256 digest of hex-decoded bytes. |
| `sha512` | `(text) => Promise<string>` | iOS, Android | SHA-512 digest of hex-decoded bytes. |
| `randomUuid` | `() => Promise<string>` | iOS, Android | Random UUID string. |
| `randomKey` | `(length) => Promise<string>` | iOS, Android | `length` secure random bytes, returned as hex. |

All failures reject with code `"-1"`. The default export is the raw TurboModule;
named exports wrap the same methods. See
[`src/NativeAesCrypto.ts`](src/NativeAesCrypto.ts) for the bridge contract and
[`src/index.tsx`](src/index.tsx) for the documented GCM rules.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| AES-CBC / AES-CTR encrypt and decrypt | ✅ | ✅ | — |
| AES-GCM authenticated encryption | ✅ | ✅ | — |
| PBKDF2 | ✅ | ✅ | — |
| HMAC-SHA256 / HMAC-SHA512 | ✅ | ✅ | — |
| SHA-1 / SHA-256 / SHA-512 | ✅ | ✅ | — |
| Random UUID and random bytes | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> String arguments are not UTF-8 text: they are hex-encoded byte arrays. Pass
> `'68656c6c6f'`, not `'hello'`, to `sha256`, HMAC and AES functions; malformed
> hex rejects instead of hashing the literal string.

> [!WARNING]
> GCM is deliberately stricter than RFC 5116: the nonce is exactly 12 bytes, AAD
> must be non-empty, the key must decode to 16, 24 or 32 bytes, and the
> ciphertext passed to decryption must include the 16-byte tag. Any mismatch
> rejects as a tag-verification failure.

> [!NOTE]
> PBKDF2 `length` is in bits and `cost`/`length` must be positive integers. Never
> reuse a GCM nonce with the same key.

## References

- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>tectiv3/react-native-aes</code></summary>

First, a sincere thank-you to tectiv3 and the `react-native-aes-crypto` maintainers for their excellent work 🙏

This package is built on, and inspired by, [tectiv3/react-native-aes](https://github.com/tectiv3/react-native-aes).

Our original plan was to keep our customizations as patches on top of upstream `react-native-aes-crypto`. As our product requirements evolved, the scope of those changes outgrew what we could reasonably maintain as an upstream patch set. We regret that we were unable to keep this work in patch form. As the gap grew, we ultimately forked `react-native-aes-crypto` to keep development and delivery stable.

### Upstream Project

- Repository: [tectiv3/react-native-aes](https://github.com/tectiv3/react-native-aes)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer to the upstream repository.

Thank you again to tectiv3 and everyone who contributes to `react-native-aes-crypto` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
