# @onekeyfe/react-native-pbkdf2

Native PBKDF2 key derivation for OneKey on iOS and Android. The TurboModule
accepts explicitly encoded password and salt bytes and returns a derived key
through a promise.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-pbkdf2
```

Install the application's iOS pods and rebuild the native application.

```ts
import Pbkdf2 from '@onekeyfe/react-native-pbkdf2';

// Encoded test strings only; application parameters belong to its key format.
const key = await Pbkdf2.derive('cGFzc3dvcmQ=', 'c2FsdA==', 1000, 32, 'sha-256');
```

## API and formats

`derive(password, salt, rounds, keyLength, hash)` returns `Promise<string>`.
Password and salt are Base64-encoded byte sequences. `keyLength` is the output
length in bytes; the derived result is Base64. `sha-256` and `sha-512` select
the corresponding PRF, while other hash values use the legacy SHA-1 path.

Keep derivation parameters aligned with the application's existing key format;
the illustrative call above is not a recommendation for production password
parameters. Handle promise rejection and decode the returned Base64 when raw
key bytes are needed. The package does not store keys or choose parameters.

See [src/NativePbkdf2.ts](src/NativePbkdf2.ts) for the native interface and the
platform sources for encoding and derivation behavior.

## Matching an existing derived-key format

The method argument order is password, salt, rounds, output-byte length and
hash name. The encoded password and salt are byte sequences, so do not pass
an unencoded user password in place of Base64 or reinterpret a textual salt
without the application's format rules.

The default export exposes `derive`; this package does not export the AES
module's differently shaped `pbkdf2` helper. `Pbkdf2Spec` can be imported as a
type when adapting this method. A derived key must still be decoded, stored or
used by the caller; resolving the promise does not persist it.

## Upstream attribution

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

## Upstream Project

- Repository: [PublicaIO/react-native-pbkdf2](https://github.com/PublicaIO/react-native-pbkdf2)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to everyone who contributes to
`react-native-pbkdf2` 💙
