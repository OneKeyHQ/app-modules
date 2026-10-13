# Zcash source import

- Source revision: `c11be01b2f395b0c64321df2e2cab026008da6b5` (`feat: add zcash wallet and keys wasm runtime`).
- Destination: `OneKeyHQ/app-modules`, `chain-runtimes/zcash`.
- Imported files: the source repository's tracked Cargo workspace, Rust source,
  tests, documentation, patches, and build scripts.

The runtime is OneKey's adapter over the versions of librustzcash pinned in
`Cargo.lock`, rather than a fork of the entire librustzcash repository. Generated
packages, Cargo build caches, browser experiments, logs, and local wallet data are
not imported.

## Vendor changes

Every vendored crate is reconstructed from the upstream crate archive checksum and
an exact patch listed in `patches/manifest.json`:

1. `zcash_client_sqlite 0.22.0`: disable rusqlite's `bundled` feature for WASM.
   Its Rust source is unchanged.
2. `sqlite-wasm-vfs 0.2.0`: `sqlite-wasm-vfs-0.2.0-opfs-durability.patch` adds
   per-connection locks, durable journal deletion, handle cleanup and fail-closed
   metadata I/O for the OPFS SAH-pool. All App platforms use this same patch.
3. `tonic-web-wasm-client 0.9.1`: `tonic-web-wasm-client-0.9.1-host-fetch.patch`
   lets the host supply the fetch handler used for lightwalletd requests.

`scripts/vendor-deps.sh` verifies the archives, applies the exact patches, and
rejects an existing vendor tree whose contents differ. No vendor source or
prebuilt WASM is committed.

## Application handoff

During local development, the App's `packages/core/package.json` points its
portal dependencies to this directory's `pkg` and `pkg-keys`. Build both outputs
before installing the App dependencies. Registry publishing and a versioned App
dependency are a separate release step; see [RELEASING.md](./RELEASING.md).
