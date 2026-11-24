# `@onekeyfe/onekey-release-sign` Specification

## Purpose

Provide a zero-dependency Node.js CLI and programmatic library for OneKey's
M-of-N (default 2-of-N) YubiKey/OpenPGP release-signing workflow covering:

1. **OTA hot-update metadata** (`*.metadata.json.info` JSON referencing `metadata.json` SHA-256).
2. **Full-release installers/APKs** (`*.SHA256SUMS` / `*.SHA256SUMS.asc`).

Multiple independent signers on separate machines sign the exact same canonical
payload with their own YubiKey OpenPGP keys. Their individual clear-signed
messages are merged into a **single RFC 4880 Cleartext Signed Message** (`.asc`)
whose signature armor block packs all `Signature Packet` (tag 2) binary packets
in sequence.

> **Rollout compatibility boundary**: Shipped Android (`BundleCryptoCore.kt`
> `signatureList[0]`) and Desktop (`result[0]`) verifiers inspect only the first
> signature packet against the legacy embedded release key. Independently keyed
> multi-signature `.asc` output is **release-gate / server-side verified** until
> client binaries ship multi-key threshold verification.

## Security Contract & Invariants

1. **Single-message framing & mandatory CRC-24**:
   - Inputs containing more than one `-----BEGIN PGP SIGNED MESSAGE-----` or
     `-----BEGIN PGP SIGNATURE-----` block MUST be rejected.
   - ASCII Armor blocks MUST include a valid RFC 4880 CRC-24 checksum line;
     omitted or corrupted checksums MUST be rejected.
2. **Strict signature packet validation**:
   - Every packet inside the signature armor block MUST be OpenPGP Packet Tag 2 (`Signature`).
   - Signature type MUST be `0x01` (`CANONICAL_TEXT_DOCUMENT`).
   - Hash algorithm MUST be one of `SHA-256` (`8`), `SHA-384` (`9`), or `SHA-512` (`10`).
3. **Clear-signed-only merge & canonical body identity**:
   - `mergeCleartextSignatures` accepts only clear-signed `PGP SIGNED MESSAGE`
     inputs (detached signatures without an embedded body are rejected) so every
     input undergoes canonical body equality comparison.
   - When `expectedBody` (`--payload`) is provided, it is canonicalized using
     GnuPG clear-sign framing rules (stripping one framing-equivalent terminal
     line ending and per-line trailing spaces/tabs, joining with CRLF).
   - Exact duplicate signature packets are rejected.
4. **TOCTOU-safe signing & no CLI passphrases**:
   - `sign` reads the payload file once into an immutable byte buffer, validates
     it against every supplied `--artifact`, and pipes that exact snapshot to
     `gpg --clearsign` via `stdin`.
   - Protected keys obtain credentials strictly through GnuPG `pinentry` (never
     via CLI argv or subprocess arguments). Subprocess error messages omit full
     argument lists.
5. **Isolated verification keyring**:
   - Verification MUST run `gpg` in a freshly created temporary `GNUPGHOME`
     (`0700`) importing only explicitly provided `--keyring` public keys so the
     caller's ambient `~/.gnupg` cannot inject trust.
6. **Primary-key fingerprint deduplication**:
   - Valid signatures are counted strictly by distinct **Primary Key
     Fingerprint** (normalized 40-hex uppercase from GnuPG `VALIDSIG` field 10).
   - Signing twice with the same key, or signing with both a primary key and
     its subkey, counts as **1** signer and MUST NOT satisfy `threshold >= 2`.
7. **Strict CLI option validation & complete artifact binding**:
   - Every CLI subcommand validates option names and positional counts against
     an explicit per-command allowlist and exits non-zero on unknown/misspelled
     flags.
   - Every repeated `--artifact` path is verified against the manifest (and
     `metadata.json.info` rejects `> 1` `--artifact`).
   - Release artifact basenames enforce one shared rule across `checksum` and
     `inspectReleasePayload` (single-segment basename with no whitespace, path
     separators, traversal, or control characters, matching shipped mobile and
     desktop `SHA256SUMS` parsers).
8. **Safe subprocess execution**:
   - All `gpg` / `ykman` invocations use `execFileSync` with argument arrays
     (never shell interpolation).

## CLI Subcommands

- `doctor` — Check `gpg`, optional `ykman`, and plugged-in YubiKey OpenPGP card.
- `checksum` — Generate canonical `<sha256>  <basename>` payload for one or more artifacts.
- `inspect` — Parse and display a `metadata.json.info`, `SHA256SUMS`, or `.asc` file (verifying all supplied `--artifact` files).
- `sign` — Validate one immutable payload snapshot & all `--artifact` files locally, pipe that snapshot to `gpg --clearsign --digest-algo SHA256` via stdin, validate RFC 4880 output, and write `.asc`.
- `merge` — Merge 2+ clear-signed `.asc` files into one standard RFC 4880 multi-signature `.asc` (with optional `--payload`, isolated `--keyring`, `--threshold`, `--allow-fingerprint`, and `--artifact` checks).
- `verify` — Verify a multi-signature `.asc` against an explicit `--keyring`, `--threshold` (default `2`), optional `--allow-fingerprint`, and all supplied `--artifact` files (supports `--json`).
