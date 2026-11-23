# `@onekeyfe/onekey-release-sign`

Multi-signature OpenPGP/YubiKey CLI and zero-dependency Node.js library for signing, merging, and verifying OneKey OTA (`metadata.json.info`) and full-release (`SHA256SUMS.asc`) artifacts.

## Why single-file RFC 4880 multi-signature?

Two or more release signers on separate machines sign the exact same canonical payload with their own YubiKey OpenPGP keys (`gpg --clearsign --digest-algo SHA256`). `onekey-release-sign merge` validates and packs their OpenPGP Signature Packets (`Tag 2`, type `0x01`, `SHA-256/384/512`) into **one standard RFC 4880 `.asc` file**:

- Zero API/filename changes (`jsBundle.signature` stays one string; `<installer>.SHA256SUMS.asc` stays one file).
- 100% compatible with standard `gpg --verify`, Desktop `openpgp.js`, Android `BouncyCastle`, and iOS `Gopenpgp`.
- Verification deduplicates strictly by **Primary Key Fingerprint**, preventing duplicate-signature or primary+subkey bypasses.

## Requirements

- Node.js `>= 20`
- `gpg` (GnuPG 2.2+ with `pinentry-mac` on macOS or Gpg4win on Windows)
- Optional: `ykman` (YubiKey Manager CLI) for touch-policy diagnostics

## Quick Start

### 1. Check local GPG & YubiKey setup

```sh
npx @onekeyfe/onekey-release-sign doctor
```

### 2. Sign independently (Signer A & Signer B on separate machines)

```sh
# Signer A (verifies metadata.json SHA-256 before prompting YubiKey PIN + touch)
onekey-release-sign sign \
  --in electron.metadata.json.info \
  --artifact metadata.json \
  --key <SIGNER_A_FPR> \
  --out sig-a.asc

# Signer B (on another machine, exact same payload)
onekey-release-sign sign \
  --in electron.metadata.json.info \
  --artifact metadata.json \
  --key <SIGNER_B_FPR> \
  --out sig-b.asc
```

### 3. Merge into one standard RFC 4880 `.asc` (Release Gate / Dashboard Backend)

```sh
onekey-release-sign merge \
  --in sig-a.asc \
  --in sig-b.asc \
  --keyring trusted-yubikeys.asc \
  --threshold 2 \
  --artifact metadata.json \
  --out electron.metadata.json.info.asc
```

### 4. Verify multi-signature `.asc`

```sh
onekey-release-sign verify \
  --in electron.metadata.json.info.asc \
  --keyring trusted-yubikeys.asc \
  --threshold 2 \
  --artifact metadata.json \
  --json
```

### 5. Full-release installer checksum helper

```sh
onekey-release-sign checksum \
  --artifact OneKey-Wallet-6.8.0-mac-arm64.zip \
  --out OneKey-Wallet-6.8.0-mac-arm64.zip.SHA256SUMS
```

## Programmatic Usage (e.g. Dashboard Backend)

```js
import {
  mergeCleartextSignatures,
  verifyMultisigCleartext,
} from '@onekeyfe/onekey-release-sign';

const merged = mergeCleartextSignatures([sigA, sigB]);

const result = verifyMultisigCleartext({
  signedText: merged.armored,
  keyringTexts: [trustedPublicKeysArmored],
  threshold: 2,
});
```

## Development

```sh
cd cli/onekey-release-sign
npm test
npm run check
```

See [docs/SPEC.md](docs/SPEC.md) for the authoritative security contract.
