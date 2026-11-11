<div align="center">

# @onekeyfe/react-native-pbkdf2

**Native PBKDF2 key derivation for React Native on iOS and Android.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-pbkdf2?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-pbkdf2)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-pbkdf2?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

A one-method TurboModule that derives a PBKDF2 key with platform crypto:
`CCKeyDerivationPBKDF` on iOS and SpongyCastle's `PKCS5S2ParametersGenerator`
on Android. Password and salt are Base64-encoded byte sequences, the caller
chooses the iteration count, output length and hash name, and the derived key
comes back Base64-encoded. Derivation runs natively rather than in JavaScript.
The package stores no keys and recommends no parameters — iteration count, salt
and hash belong to the application's existing key format.

## Features

- **Native derivation** — CommonCrypto on iOS and SpongyCastle on Android, off
  the JS thread.
- **Explicit byte inputs** — Base64 password and salt remove any ambiguity about
  text encoding at the bridge.
- **Selectable PRF** — `sha-256` and `sha-512` are supported, with SHA-1 kept as
  the compatibility fallback.
- **Caller-chosen output length** — `keyLength` sets the derived key size in
  bytes.
- **Pure computation** — no persistence, no key management and no global state;
  the caller owns the result.

## Installation

```sh
yarn add @onekeyfe/react-native-pbkdf2
```

The package ships native code. Install the application's iOS pods and rebuild the
native binary; a JavaScript-only update does not register the TurboModule.

## Usage

```ts
import Pbkdf2 from '@onekeyfe/react-native-pbkdf2';

// Base64 password/salt for illustration only — use the app's key format.
const keyBase64 = await Pbkdf2.derive(
  'cGFzc3dvcmQ=', // "password"
  'c2FsdA==', // "salt"
  1000,
  32,
  'sha-256',
);
```

Argument order is password, salt, rounds, output length in bytes, hash name. The
call above is not a parameter recommendation. Handle rejection and keep the
returned Base64 aligned with the format the application expects.

## API

| Export | Signature | Platforms | Description |
| --- | --- | --- | --- |
| `derive(password, salt, rounds, keyLength, hash)` | `Promise<string>` | iOS, Android | Derives a key; password/salt are Base64 bytes, result is Base64. |
| `default export` | `Spec` | iOS, Android | The TurboModule instance; `derive` is its only method. |
| `Pbkdf2Spec` | type | iOS, Android | The native module interface type. |

`hash` accepts `'sha-256'` and `'sha-512'`. Any other value selects SHA-1 for
compatibility with the original API rather than failing, so pass an explicit
supported name. Native failures reject with `derive_fail` on iOS and
`PBKDF2_ERROR` on Android. See
[`src/NativePbkdf2.ts`](src/NativePbkdf2.ts) for the exact interface.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| PBKDF2 derivation | ✅ CommonCrypto | ✅ SpongyCastle | — |
| SHA-256 / SHA-512 PRF | ✅ | ✅ | — |
| SHA-1 fallback | ✅ | ✅ | — |
| Caller-selected output length | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> An unrecognized `hash` silently falls back to SHA-1. Always pass `'sha-256'` or
> `'sha-512'` explicitly when the key format requires them; a typo otherwise
> produces a valid-looking key from the wrong PRF.

> [!WARNING]
> Invalid Base64 is not rejected consistently. iOS decodes an invalid string to
> empty bytes and derives from that, while Android rejects with `PBKDF2_ERROR`.
> Validate and encode password and salt in JavaScript before calling `derive`.

> [!NOTE]
> The package neither stores nor manages derived keys and does not choose
> iteration counts or salts. Align every parameter with the application's
> existing key format; resolving the promise persists nothing.

## References

- [Native interface](src/NativePbkdf2.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-pbkdf2)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

<details>
<summary>Forked from <code>PublicaIO/react-native-pbkdf2</code> (MIT)</summary>

First, a sincere thank-you to the
`react-native-pbkdf2` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[PublicaIO/react-native-pbkdf2](https://github.com/PublicaIO/react-native-pbkdf2).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-pbkdf2`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-pbkdf2` to keep
development and delivery stable.

### Upstream Project

- Repository: [PublicaIO/react-native-pbkdf2](https://github.com/PublicaIO/react-native-pbkdf2)
- License: MIT

### Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-pbkdf2` 💙

</details>

## License

MIT. See [LICENSE](LICENSE).
