# Zcash source import

- Source: the local `onekey-zcash-runtime` repository.
- Source revision: `c11be01b2f395b0c64321df2e2cab026008da6b5` (`feat: add zcash wallet and keys wasm runtime`).
- Destination: `OneKeyHQ/app-modules`, `chain-runtimes/zcash`.
- Imported files: the source repository's tracked Cargo workspace, Rust source,
  tests, documentation, patches, and build scripts.

The runtime is OneKey's adapter over the versions of librustzcash pinned in
`Cargo.lock`, rather than a fork of the entire librustzcash repository. The
original local repository is retained. Generated packages, Cargo build caches,
browser experiments, logs, and local wallet data are not imported.

## Vendor changes

The source checkout depended on two local vendor trees. Only one of their
patches was originally tracked. Both are now reconstructible from the upstream
crate archive checksums in `patches/manifest.json`:

1. `zcash_client_sqlite 0.22.0`: disable rusqlite's `bundled` feature for WASM.
   Its Rust source is unchanged.
2. The import initially preserved the existing `relaxed_idb` durability barrier.
   The subsequent unified Worker change replaces it with
   `sqlite-wasm-vfs-0.2.0-opfs-durability.patch`: per-connection locks, durable
   journal deletion, handle cleanup and fail-closed metadata I/O. All App
   platforms use this same patch. The old IndexedDB patch is no longer applied.

`scripts/vendor-deps.sh` verifies the archives, applies the exact patches, and
rejects an existing vendor tree whose contents differ. No vendor source or
prebuilt WASM is committed.

## Application handoff

During local development, `app-monorepo/packages/core/package.json` points its
two existing portal dependencies to this directory's `pkg` and `pkg-keys`.
Build both outputs before installing the app dependencies. Package names and
runtime interfaces are unchanged by this import. Registry publishing and a
versioned app dependency are a separate release step.

## Source-import validation (before the OPFS change, 2026-09-08)

- All first-party Rust source and `Cargo.lock` match the source checkout byte
  for byte. Both reconstructed vendor trees match the source checkout's Rust
  source and Cargo manifests, including its previously untracked VFS patch.
- Rust workspace: 52 tests passed; 9 opt-in network tests remained ignored.
- Vendor reconstruction guards: 6 tests passed (including checksum, drift,
  offline, archive traversal, and archive link rejection).
- Release builds succeeded for runtime, keys, and storage benchmark.
- The app's installed runtime resolves to this directory and loads in Node;
  its capability and dependency-version probes succeeded.
- The app's installed keys package passed address scheme v3 goldens and all
  5 synthetic-seed cleanup checks against the actual WASM.
- `app-monorepo` passed `yarn agent:check --profile commit`, including TypeScript
  and the background API contract checks.

The immutable app installation changed only the two portal locations. Its
repository-specific `afterInstall` hook still ran despite disabled package build
scripts and reported a `react-native-collapsible-tab-view` patch failure. That
package and patch are unchanged by this migration; the issue is not a Zcash
build failure. The GitHub workflow has been added but has not been run remotely.
