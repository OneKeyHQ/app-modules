# Chain runtimes

Chain execution libraries live here alongside the React Native modules and views.
Each chain owns its Cargo workspace, lockfile, toolchain, patches, and build outputs.
These directories are not React Native packages and are not part of the root Yarn
workspaces or their shared version/publish commands.

## Zcash

See [zcash](./zcash/README.md) and its [import record](./zcash/IMPORT.md).

From the repository root:

```sh
yarn zcash:prepare
yarn zcash:test
yarn zcash:build
```

These convenience commands dispatch to the Rust project. The equivalent direct
commands in the Zcash README do not require installing the root Node dependencies.
Generated WASM stays out of Git. The Zcash CI workflow builds
both runtime and keys packages together and stores them as one artifact; it does
not publish npm packages.
