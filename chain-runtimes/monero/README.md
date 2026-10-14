# OneKey Monero Runtime

Wallet state layer over monero-oxide. monero-oxide provides cryptography, scanning
and transaction construction; this crate adds the storage it lacks: SQLite with
incremental, transactional writes.

## Build

```bash
cargo test --workspace                                    # native tests
cargo check --workspace --target wasm32-unknown-unknown
./scripts/build-wasm.sh                                   # release build to pkg/
./scripts/build-node-pkg.sh && node scripts/smoke.js      # wasm end-to-end smoke test
```

`smoke.js` runs offline; set `XMR_NODE=<monerod RPC URL>` to add node checks.
The toolchain matches the Zcash runtime, so one install builds both: Rust 1.93.0
(pinned in `rust-toolchain.toml`), wasm-pack 0.15.0, `wasm-bindgen-cli` at the
version in `Cargo.lock`, and `wasm-opt` (Binaryen 133 in CI).

**macOS needs `llvm-ar`** (`brew install llvm`). `/usr/bin/ar` cannot read wasm
objects and silently writes an empty archive; linking then fails with
`undefined symbol: sqlite3_*`. `cargo check` does not link, so it never shows this.
`build-wasm.sh` finds Homebrew's clang and `llvm-ar` on Apple Silicon and Intel;
`.cargo/config.toml` sets the Apple Silicon archiver for direct `cargo` builds.

## Release

Run the **publish-runtime-packages** workflow manually to build, test and publish
`onekey-monero-runtime` (skipped if its version is already on npm): prereleases to npm `next`, release
versions to `latest`. Locally,
`python3 scripts/package-release.py --version <v> --tag <next|latest>` packs the
archive into `dist/` without publishing.

## Host contract

1. **Import key images on unlock.** A view-only wallet cannot detect spends, so the
   balance stays too high until `importKeyImages` is called. It covers only blocks
   scanned afterwards; earlier spends need a rewind and rescan. `balance` reports the
   unverified part as `unverifiableAtomic`.
2. **The node must be on the wallet's network.** The node's chain is identified by its
   genesis block; a mismatch fails with `NODE_NETWORK_MISMATCH`. `chainTip` works
   without an open wallet and returns `network`; compare it before picking a birthday.
3. **An `unknown` broadcast is not a failure.** The send is recorded and its inputs
   marked spent before broadcasting; pool checks and scanning settle it. Never report
   it as failed or prompt a resend.
4. **The host schedules sync.** `syncStep` scans at most `maxBlocks` and returns; it
   never loops to completion. Timing, batch size and backoff belong to the host.
5. **Register subaddresses before use.** Funds to unregistered subaddresses go
   undetected.

The scanner derives RingCT output indexes itself and fails with
`RINGCT_INDEX_MISMATCH` when the node's claim differs.

## API

Values cross the boundary as strings, numbers, booleans, bytes or JSON strings.
Errors carry a stable `code` and structured `params`; user-facing text belongs to the host.

```
init() / dependencyVersions()
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

prepareTransaction(accountId, recipientsJson, maxSend, reservationId, chainTip, priority)
verifyTransaction(signableHex, reviewJson)
markReservationSigned(reservationId, expectedInputs)
signTransaction(signableHex, spendPrivateKeyHex, reviewJson)
broadcastTransaction(accountId, rawHex, reservationId, chainTip, outgoingAtomic?, changeAtomic?)
releaseReservation(reservationId)
diagDatabase()
```

### Sending

```
prepareTransaction      select and reserve inputs, build; no spend key
verifyTransaction       check recipient, amount, fee and change against the review
markReservationSigned   claim the reserved inputs for this review
signTransaction         verify again, then sign; no network, no DB
broadcastTransaction    record, then broadcast → { txid, broadcast: accepted | unknown }
```

`prepareTransaction` takes exactly one recipient. A first `broadcastTransaction`
whose inputs are not this account's, or are already spent by another transaction,
is refused with `INVALID_REQUEST` before anything is recorded or sent.

`BROADCAST_REJECTED` is thrown only when the node explicitly rejects a first
broadcast for a reason other than double spend; the record is then undone and the
inputs released. Unsigned reservations expire after 1 hour, signed ones after 24
hours. Call `releaseReservation` when the user cancels.

## Layout

```
error.rs     error codes (stable across the wasm boundary)
storage.rs   SQLite connection, OPFS VFS, durability barrier
schema.rs    tables and migrations
keys.rs      view keys, addresses, subaddress derivation
wallet.rs    DB handle, identities, subaddress registry
scan.rs      scan loop, output checks, persistence
sync.rs      bounded sync (fetch → scan → record progress)
query.rs     balance, history, spendable outputs
spend.rs     key images, selection, reservation, build, sign, broadcast
review.rs    pre-signing check of recipient, amount, fee and change
pool.rs      liveness check of our unconfirmed sends
network.rs   monerod transport and node network check
runtime.rs   session state and concurrency guards
bindgen.rs   wasm boundary
```

## Relation to the Zcash runtime

Separate workspaces with no code dependency. `crates/monero-runtime/build.rs` (wasm
SQLite link glue) mirrors the Zcash one; keep them in sync. Unlike Zcash, `runtime.rs`
is not wasm-only, so its concurrency guards are covered by native tests.
