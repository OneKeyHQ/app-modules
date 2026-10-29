# OneKey Zcash Runtime

Thin wasm bindings over the official `librustzcash`, with `zcash_client_sqlite` as
the wallet store. Two packages built from one source, used on every App platform:

- `@onekeyfe/zcash-keys`: derivation, review checks and signing. No SQLite, network,
  scanner or prover.
- `@onekeyfe/zcash-runtime`: wallet, scanning, SQLite and PCZT proving.

Both run inside a DedicatedWorker. Native Rust builds are for tests and diagnostics
only.

## Build

Run from this directory. Requires Rust 1.93.0 (pinned in `rust-toolchain.toml`),
Python 3.11+, LLVM/Clang with wasm32 support, wasm-pack 0.15.0, `wasm-bindgen-cli`
at the version in `Cargo.lock`, and `wasm-opt` (Binaryen). Builds never update
`Cargo.lock`.

```bash
rustup target add wasm32-unknown-unknown
brew install llvm                       # Apple clang cannot target wasm32

bash scripts/build-wasm.sh              # builds pkg/ and pkg-keys/ (prepares vendor first)

bash scripts/vendor-deps.sh             # once before native tests
cargo test --workspace --locked

bash scripts/vendor-deps.sh --offline   # verify vendor against recorded upstream + patches
```

`pkg/` and `pkg-keys/` are build outputs and are not committed.

## Release

Zcash versions are independent of the native modules' release train. Both packages
share the version in `Cargo.toml`. Run the **publish-runtime-packages** workflow
manually. It builds, tests and publishes each package whose version is not on npm
yet: prereleases to `next`, releases to `latest`. Branches other than `main` can
publish alpha versions only. `python3 scripts/package-release.py pack --version <v>
--tag <next|latest> --revision <commit>` packs locally without publishing. The App
pins the exact version.

## API

Failures throw an `Error` with a stable `code`, structured `params`, and a log-only
`detail`. `capabilities()` / `keysCapabilities()` report what the build supports.

### `@onekeyfe/zcash-runtime`

```
init() / storageRequiresWorkerRestart()
setRequestHandler(handler) / setLightwalletdUrl(baseUrl)
openWallet(network, dbName) / closeWallet() / deleteWallet(dbName)
chainTip() / chainTipAt(baseUrl)

importAccountUfvk(name, ufvk, birthday, seedFingerprintHex?, accountIndex?)
removeAccount(uuid) / rescanAccount(uuid, fromHeight)
accountByUfvk(ufvk) / listAccounts() / accountSyncStatus(uuid)
accountBalance(uuid, trusted, untrusted, allowZeroConfShielding)

syncPrepare() / syncTip() / syncPeek()
syncStep(batchSize, maxBatches, priority, activeUfvksJson)
queueRescanFrom(fromHeight) / rewindTo(height)

transactionHistory(uuid, limit, offset)
transactionDetails(uuid, txid) / transactionOutputs(uuid, txid)
storageStats() / diagDatabaseExists(dbName) / diagDatabaseHealth()

pcztQuote(...) / pcztCreate(...) / pcztProve(pczt)
pcztCombine(original, signed) / pcztSubmit(uuid, pczt)
pendingBroadcastTxids(uuid) / broadcastTransaction(txid)

prewarmPczt(network, height) / pcztProveAtHeight(height, pczt)
pcztExtractStateless(pczt)
capabilities() / dependencyVersions()
```

### `@onekeyfe/zcash-keys`

```
ufvkFromAccountKeys(network, orchardSk, accountXpub, accountIndex)
unifiedAddress(network, ufvk, receiverPolicy)
transparentAddressFromUfvk(network, ufvk) / transparentAccountPubKeyFromUfvk(network, ufvk)
validateAddress(network, address)
pcztVerifyReview(pczt, reviewJson)
pcztSignWithAccountKeys(network, orchardSk, accountXprv, accountIndex, pczt, reviewJson)
pcztRedactForHardwareSigner(pczt)
transparentTx* / transparentShield* / parseTransparentTransactions(requestJson)
transactionPolicy(targetHeight) / inspectTransaction(requestJson)
keysCapabilities() / keysDependencyVersions()
```

## Layout

```
crates/zcash-runtime/            wallet, scanning, PCZT build/prove/submit
crates/zcash-send-policy/        height-gated ZIP 2009 fees and bounded expiry
crates/zcash-keys-runtime/       derivation, review checks, signing, stateless transparent sends
patches/                         pinned upstream checksums and patches
scripts/                         vendor-deps.sh, build-wasm.sh, package-release.py
```

The pinned SQLite patch also uses the shared height-dependent marginal fee for
spendable-note selection and uneconomic balance classification. `vendor-deps.sh`
verifies this patch against the recorded upstream archive before a build.
