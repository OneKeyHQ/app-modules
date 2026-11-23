# CLI tools

Standalone Node.js command-line packages for OneKey release engineering and
security tooling. These are not React Native packages and are not part of the
root Yarn workspaces or their synchronized RN version/publish commands.

- [onekey-release-sign](./onekey-release-sign/README.md) — Multi-signature
  OpenPGP/YubiKey release signing, RFC 4880 `.asc` merging, and verification
  for OneKey OTA (`metadata.json.info`) and full-release (`SHA256SUMS.asc`)
  artifacts.
