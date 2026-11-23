# OneKey Monero Runtime

Wallet state layer over monero-oxide, published as `@onekeyfe/monero-runtime`.
monero-oxide provides cryptography, scanning and transaction construction; this
crate adds the storage it lacks: SQLite with incremental, transactional writes.

## Build

```bash
bash scripts/vendor-deps.sh                               # once, before any cargo command
cargo test --workspace                                    # native tests
cargo check --workspace --target wasm32-unknown-unknown
./scripts/build-wasm.sh                                   # release build to pkg/ (prepares vendor first)
./scripts/build-node-pkg.sh && node scripts/smoke.js      # wasm end-to-end smoke test
```

`smoke.js` runs offline; set `XMR_NODE=<monerod RPC URL>` to add node checks. The
toolchain matches the Zcash runtime: Rust 1.93.0 (pinned in `rust-toolchain.toml`),
wasm-pack 0.15.0, `wasm-bindgen-cli` at the version in `Cargo.lock`, and `wasm-opt`
(Binaryen). On macOS, install `llvm-ar` (`brew install llvm`); `/usr/bin/ar` cannot
read wasm objects.

## Release

Run the **publish-runtime-packages** workflow manually. It builds, tests and
publishes the package unless its version is already on npm: prereleases to `next`,
releases to `latest`. Branches other than `main` can publish alpha versions only.
`python3 scripts/package-release.py --version <v> --tag <next|latest>` packs into
`dist/` locally without publishing.

## API

Values cross the boundary as strings, numbers, booleans, bytes or JSON strings.
Errors carry a stable `code` and structured `params`.

```
init() / dependencyVersions()
storageRequiresWorkerRestart()
openWallet(dbName, network) / closeWallet() / deleteWallet(dbName)
setNodeUrl(url) / setRequestHandler(handler)
chainTip()

deriveAccount(network, bip32PrivateKeyHex)
deriveSubaddress(network, spendPub, viewPriv, major, minor)
validateAddress(network, address)
addAccount(uuid, spendPub, viewPriv, birthday, forceRescan?) / listAccounts() / removeAccount(uuid)
registerSubaddress(accountId, major, minor, label?)
importKeyImagesWithBip32Key(accountId, bip32PrivateKeyHex)
exportKeyImageSyncData(accountId) / importDeviceKeyImages(accountId, itemsJson)

updateChainTip(accountId, tip) / syncStep(accountId, maxBlocks) / syncPeek(accountId)
rewindTo(accountId, height, tip)
balance(accountId, chainTip?) / history(accountId, limit, offset)
transactionDetails(accountId, txid)

prepareTransaction(accountId, recipientsJson, maxSend, chainTip, priority)
verifyTransaction(signableHex, reviewJson)
verifySignedTransaction(signableHex, reviewJson, rawHex)
signTransactionWithBip32Key(signableHex, bip32PrivateKeyHex, reviewJson)
broadcastTransaction(accountId, rawHex, chainTip, outgoingAtomic?, changeAtomic?, signableHex, reviewJson)
diagDatabase()
```
