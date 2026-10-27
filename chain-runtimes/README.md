# Chain runtimes

Rust chain libraries compiled to wasm. Each chain owns its Cargo workspace,
lockfile, toolchain, patches and build outputs. These are not React Native packages
and are not part of the root Yarn workspaces or their version/publish commands.

- [Zcash](./zcash/README.md)
- [Monero](./monero/README.md)

Root shortcuts for Zcash (the direct commands in its README need no Node install):

```sh
yarn zcash:prepare
yarn zcash:test
yarn zcash:build
```

Generated wasm stays out of Git. Both runtimes are built, tested and published to
npm by the manually triggered **publish-runtime-packages** workflow.
