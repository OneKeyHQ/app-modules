# @onekeyfe/react-native-aes-crypto

Native cryptographic primitives for OneKey on iOS and Android: AES encryption,
authenticated GCM operations, PBKDF2, HMAC, hashing and random values.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-aes-crypto
```

Install the application's iOS pods and rebuild its native binary. The exported
functions return promises and depend on the native TurboModule.

```ts
import { sha256, randomKey } from '@onekeyfe/react-native-aes-crypto';

const digest = await sha256('example message');
const randomBytes = await randomKey(32);
```

## API and formats

Named exports include `encrypt`, `decrypt`, `aesGcmEncrypt`, `aesGcmDecrypt`,
`pbkdf2`, `hmac256`, `hmac512`, `sha1`, `sha256`, `sha512`, `randomUuid` and
`randomKey`. The default export is the native module.

The GCM helpers use lowercase hex inputs, a 12-byte nonce, a valid AES key
length and nonempty associated data. Encryption returns ciphertext followed
by a 16-byte authentication tag, encoded as hex. Decryption rejects when the
inputs or authentication tag are invalid. Those formats are specific to the
GCM helpers; do not assume that every other method uses the same encoding.

Use the exact signatures in [src/NativeAesCrypto.ts](src/NativeAesCrypto.ts) and
the GCM contract comments in [src/index.tsx](src/index.tsx). Applications own
key storage, nonce uniqueness, input encoding and handling promise failures.

## Upstream attribution

First, a sincere thank-you to tectiv3 and the
`react-native-aes-crypto` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[tectiv3/react-native-aes-crypto](https://github.com/tectiv3/react-native-aes-crypto).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-aes-crypto`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-aes-crypto` to keep
development and delivery stable.

## Upstream Project

- Repository: [tectiv3/react-native-aes-crypto](https://github.com/tectiv3/react-native-aes-crypto)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to tectiv3 and everyone who contributes to
`react-native-aes-crypto` 💙
