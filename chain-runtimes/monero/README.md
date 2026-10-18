# OneKey Monero Runtime

Wallet state layer over monero-oxide, published as `onekey-monero-runtime`.
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
(Binaryen).

**macOS needs `llvm-ar`** (`brew install llvm`). `/usr/bin/ar` cannot read wasm
objects and silently writes an empty archive; linking then fails with
`undefined symbol: sqlite3_*`, which `cargo check` never shows. `build-wasm.sh` and
`.cargo/config.toml` pick up Homebrew's toolchain.

## Release

Run the **publish-runtime-packages** workflow manually. It builds, tests and
publishes the package unless its version is already on npm: prereleases to `next`,
releases to `latest`. Branches other than `main` can publish alpha versions only.
`python3 scripts/package-release.py --version <v> --tag <next|latest>` packs into
`dist/` locally without publishing.

## Host contract

1. **Import key images on unlock.** A view-only wallet cannot detect spends, so the
   balance stays too high until `importKeyImages` is called. It covers only blocks
   scanned afterwards; earlier spends need a rewind and rescan. `balance` reports the
   unverified part as `unverifiableAtomic`.
2. **The node must be on the wallet's network.** It is identified by its genesis
   block; a mismatch fails with `NODE_NETWORK_MISMATCH`. `chainTip` works without an
   open wallet and returns `network`; check it before picking a birthday.
3. **An `unknown` broadcast is not a failure.** The send is recorded and its inputs
   marked spent before broadcasting; pool checks and scanning settle it. Do not
   translate an unclear broadcast result into a failure or prompt a resend.
4. **The host schedules sync.** `syncStep` scans at most `maxBlocks` and returns.
   Its existing pool refresh includes previously failed own sends. A matching
   transaction seen in the pool restores the record and released inputs in one
   SQLite transaction, unless a later local spend already owns an input.
   `sendsRestored` reports those changes so the host can refresh balances/history.
5. **Register subaddresses before use.** Funds to unregistered subaddresses go
   undetected.
6. **Local management does not require a live tip.** Deletion needs only the local
   database. For `rewindTo`, pass the last locally observed tip; normal sync fetches
   a fresh one later. Rewind, cursor updates and range requeue commit together.

## API

Values cross the boundary as strings, numbers, booleans, bytes or JSON strings.
Errors carry a stable `code` and structured `params`; user-facing text belongs to
the host.

```
init() / dependencyVersions()
storageRequiresWorkerRestart()                  true after a VFS failure
openWallet(dbName, network) / closeWallet() / deleteWallet(dbName)
setNodeUrl(url) / setRequestHandler(handler)    no default node
chainTip()                                      { height, network }

deriveAccount(network, bip32PrivateKeyHex) / spendScalar(bip32PrivateKeyHex)
deriveSubaddress(network, spendPub, viewPriv, major, minor)
validateAddress(network, address)
addAccount(uuid, spendPub, viewPriv, birthday) / listAccounts() / removeAccount(uuid)
registerSubaddress(accountId, major, minor, label?)
importKeyImages(accountId, spendPrivateKeyHex)

updateChainTip(accountId, tip) / syncStep(accountId, maxBlocks) / syncPeek(accountId)
rewindTo(accountId, height, tip)                destructive
balance(accountId, chainTip?) / history(accountId, limit, offset)
transactionDetails(accountId, txid)

prepareTransaction(accountId, recipientsJson, maxSend, chainTip, priority)
verifyTransaction(signableHex, reviewJson)
signTransaction(signableHex, spendPrivateKeyHex, reviewJson)
broadcastTransaction(accountId, rawHex, chainTip, outgoingAtomic?, changeAtomic?)
diagDatabase()
```

### Sending

```
prepareTransaction      select inputs, build; no spend key
verifyTransaction       check recipient, amount, fee and change against the review
signTransaction         verify again, then sign; no network, no DB
broadcastTransaction    record, then broadcast → { txid, broadcast: accepted | unknown }
```

- `prepareTransaction` takes exactly one recipient.
- A first `broadcastTransaction` whose inputs are not this account's, or are already
  spent elsewhere, is refused with `INVALID_REQUEST` before anything is recorded.
- `BROADCAST_REJECTED` is thrown only when the node explicitly rejects a first
  broadcast for a reason other than double spend; the record is undone and the
  inputs released.
- Inputs are not reserved between prepare and broadcast. Two sends built from
  the same inputs both sign, but only the first broadcast is recorded; the second
  is refused with `inputAlreadySpent`. Inputs of a send the node never received
  are released after the propagation timeout and an explicit missing response,
  and can be spent again. Failed sends remain eligible for normal pool refresh;
  response transaction hashes are checked against their serialized bodies before
  updating local input ownership.
- The scanner derives RingCT output indexes itself and fails with
  `RINGCT_INDEX_MISMATCH` when the node's claim differs.

## Relation to the Zcash runtime

No code dependency, but one storage layer: `vendor-deps.sh` rebuilds
`sqlite-wasm-vfs` with the Zcash patch via its `prepare-vendor.py`, so a VFS fix
lands in both. `crates/monero-runtime/build.rs` mirrors the Zcash wasm SQLite link
glue; keep them in sync.
