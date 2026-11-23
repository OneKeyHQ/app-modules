# `@onekeyfe/onekey-release-sign` Specification

## Purpose

Provide a zero-dependency Node.js CLI and programmatic library for OneKey's
M-of-N (default 2-of-N) YubiKey/OpenPGP release-signing workflow covering:

1. **OTA hot-update metadata** (`*.metadata.json.info` JSON referencing `metadata.json` SHA-256).
2. **Full-release installers/APKs** (`*.SHA256SUMS` / `*.SHA256SUMS.asc`).

Multiple independent signers on separate machines sign the exact same canonical
payload with their own YubiKey OpenPGP keys. Their individual signatures are
merged into a **single RFC 4880 Cleartext Signed Message** (`.asc`) whose
signature armor block packs all `Signature Packet` (tag 2) binary packets in
sequence.

## Security Contract & Invariants

1. **Single-message framing**: Inputs containing more than one
   `-----BEGIN PGP SIGNED MESSAGE-----` or `-----BEGIN PGP SIGNATURE-----`
   block MUST be rejected.
2. **Strict signature packet validation**:
   - Every packet inside the signature armor block MUST be OpenPGP Packet Tag 2 (`Signature`).
   - Signature type MUST be `0x01` (`CANONICAL_TEXT_DOCUMENT`).
   - Hash algorithm MUST be one of `SHA-256` (`8`), `SHA-384` (`9`), or `SHA-512` (`10`).
   - ASCII Armor CRC-24 checksum MUST be valid.
3. **Canonical body identity during merge**:
   - All inputs merged into one `.asc` MUST match the exact same RFC 4880 §7.1
     canonicalized body (dash-unescaped, per-line trailing space/tab stripped,
     CRLF joined). Any mismatch aborts the merge.
   - Exact duplicate signature packets are rejected.
4. **Isolated verification keyring**:
   - Verification MUST run `gpg` in a freshly created temporary `GNUPGHOME`
     (`0700`) importing only explicitly provided `--keyring` public keys so the
     caller's ambient `~/.gnupg` cannot inject trust.
5. **Primary-key fingerprint deduplication**:
   - Valid signatures are counted strictly by distinct **Primary Key
     Fingerprint** (normalized 40-hex uppercase from GnuPG `VALIDSIG` field 10).
   - Signing twice with the same key, or signing with both a primary key and
     its subkey, counts as **1** signer and MUST NOT satisfy `threshold >= 2`.
6. **Allowlist & artifact binding**:
   - When `--allow-fingerprint` is specified, only primary fingerprints in the
     allowlist count toward the threshold.
   - When `--artifact` is specified, the artifact's streaming SHA-256 digest
     MUST match the signed `sha256` field (for `metadata.json.info`) or the
     matching filename line (for `SHA256SUMS`).
7. **Safe subprocess execution**:
   - All `gpg` / `ykman` invocations use `execFileSync` with argument arrays
     (never shell interpolation).

## CLI Subcommands

- `doctor` — Check `gpg`, optional `ykman`, and plugged-in YubiKey OpenPGP card.
- `checksum` — Generate canonical `<sha256>  <basename>` payload for an artifact.
- `inspect` — Parse and display a `metadata.json.info`, `SHA256SUMS`, or `.asc` file (with optional `--artifact` hash verification).
- `sign` — Verify payload/artifact locally, invoke `gpg --clearsign --digest-algo SHA256` (YubiKey PIN + touch), validate RFC 4880 output, and write `.asc`.
- `merge` — Merge 2+ signed `.asc` files into one standard RFC 4880 multi-signature `.asc` (with optional isolated `--keyring` + `--threshold` + `--artifact` verification before writing).
- `verify` — Verify a multi-signature `.asc` against an explicit `--keyring`, `--threshold` (default `2`), optional `--allow-fingerprint`, and optional `--artifact` (supports `--json`).
