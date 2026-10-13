# OneKey Zcash Runtime

Thin wasm bindings over the official `librustzcash`, with `zcash_client_sqlite` as
the wallet store. Does not depend on WebZjs.

Two packages built from one source, used by every App platform (Desktop, Web,
Extension, iOS, Android):

- `onekey-zcash-keys`: derivation, review checks and signing. No SQLite, network,
  scanner or prover.
- `onekey-zcash-runtime`: wallet, scanning, SQLite and PCZT proving.

Both run inside a DedicatedWorker (an inline Blob Worker from WebEmbed on mobile, a
URL Worker elsewhere). Native Rust builds are for tests and diagnostics only.
`zcash-storage-benchmark` is a developer tool for synthetic storage tests.

## Build

Run from this directory. Requires Rust 1.93.0 (pinned in `rust-toolchain.toml`),
Python 3.11+, LLVM/Clang with wasm32 support, wasm-pack 0.15.0, `wasm-bindgen-cli`
at the version in `Cargo.lock`, and `wasm-opt` (Binaryen). Builds use
`--mode no-install` and never update `Cargo.lock`.

```bash
rustup target add wasm32-unknown-unknown
brew install llvm                    # Apple clang cannot target wasm32

bash scripts/build-wasm.sh           # builds pkg/ and pkg-keys/ (prepares vendor first)

bash scripts/vendor-deps.sh          # once before native tests
cargo test --workspace --locked

bash scripts/vendor-deps.sh --offline   # verify vendor against recorded upstream + patches
```

`pkg/` and `pkg-keys/` are not committed; they are built together for the App's
local `portal:` dependencies or CI artifacts. Publishing is in [RELEASING.md](./RELEASING.md).

## Design rules

1. **No forked Zcash storage.** `zcash_client_sqlite` comes from crates.io with no
   Rust source changes; the only change is one Cargo.toml feature (see Vendor patches).
2. **Seeds enter only the keys wasm, only for an authorized derive or sign call.** Rust
   wipes its secret buffers; JS strings and cross-bridge copies cannot be reliably
   wiped. The scanner holds the UFVK, never the seed.
3. **Few verbs, plain data.** Only strings, numbers, booleans, bytes and JSON strings
   cross the boundary; no Rust object handles are given to JS.

| Rust (wasm) | Host TS |
|---|---|
| Trial decryption | When and how much to scan |
| Witness and commitment trees | Accounts and birthdays |
| Transaction construction and note selection | Caching and persistence policy |
| Key derivation and signing | Progress, error handling, retries |
| Platform glue (clock, RNG, VFS) | History display model |

Code belongs in Rust only if it touches key security, large cross-boundary data or
consensus correctness.

## Vendor patches

`zcash_client_sqlite` enables `rusqlite`'s `bundled` feature, which compiles SQLite's
C source with cc. `wasm32-unknown-unknown` has no libc sysroot and stops at
`fatal error: 'stdio.h' file not found`. Cargo features are additive, so a downstream
crate cannot disable it; only the upstream Cargo.toml can.

`scripts/vendor-deps.sh` downloads the official releases and applies the patches
recorded in `patches/manifest.json`:

- `zcash_client_sqlite`: the one-line Cargo.toml change.
- `sqlite-wasm-vfs`: OPFS durability fixes (connection lock, durable journal deletion,
  handle cleanup, stop I/O after a metadata failure).
- `tonic-web-wasm-client`: host fetch injection.

`scripts/prepare-vendor.py --offline` rebuilds and compares every file from the
verified archives and patches, and rejects unrecorded vendor changes.

The wasm SQLite comes precompiled from `sqlite-wasm-rs`; `crates/zcash-runtime/build.rs`
links its `libwsqlite3.a` under the name the linker expects. No manual RUSTFLAGS.

## Storage

All platforms use the `sqlite-wasm-vfs` OPFS SAH pool in `.onekey-zcash-opfs`, with
six file handles reserved by default. Databases are `zcash-main.db` / `zcash-test.db`.
SQLite reads and writes pages through sync access handles; the database is never
preloaded into wasm memory. One Worker owns each origin's pool; a second Worker must
not silently switch to another database.

Connections use `journal_mode=DELETE` and `synchronous=FULL`. Flush errors on commit
and journal deletion reach the host. After a metadata I/O failure the VFS stops
serving and `storageRequiresWorkerRestart()` returns true: keep the failed call's
result and restart the Worker. No SharedArrayBuffer, COOP/COEP or UI-thread sync file
access is needed. OPFS is still subject to origin, quota and eviction rules, so the
App's embedded page origin must be stable.

## Known constraints

- The lightwalletd endpoint **must support gRPC-web** (and CORS without a host
  transport). A native-gRPC-only endpoint hangs on the first browser request even if
  `grpcurl` works. The host supplies the endpoint; none is hardcoded.
- **Scanning does not use upstream `scan_cached_blocks`** but `scan_blocks_inline`.
  Upstream hands trial decryption to `rayon::spawn_fifo`; single-threaded wasm has no
  worker threads, so the task never runs and the main thread blocks forever. This
  loses batched trial decryption. Use `syncPeek()` for progress.
- `syncPrepare()` downloads every pool's subtree roots, many more on mainnet; give it
  its own "preparing" state.
- Sapling is out of scope: not a receiver, scan identity or send source.

## API

Failures throw an `Error` with a stable `code`, structured `params`, and a log-only
`detail`. Rust produces no user-facing text. `capabilities()` / `keysCapabilities()`
report what the build supports.

### `onekey-zcash-runtime`

```
init()                                   install storage; idempotent; call first
storageRequiresWorkerRestart()           true after a VFS failure
setRequestHandler(handler)               host fetch for lightwalletd
openWallet(network, dbName) / closeWallet()
setLightwalletdUrl(baseUrl)
chainTip() / chainTipAt(baseUrl)

importAccountUfvk(name, ufvk, birthday, seedFingerprintHex?, accountIndex?)
                                         → UUID; the server's tree state must be
                                         exactly the block before birthday
removeAccount(uuid) / removeDeletedAccount(uuid)
accountByUfvk(ufvk) / listAccounts() / accountSyncStatus(uuid)
accountBalance(uuid, trusted, untrusted, allowZeroConfShielding)

syncPrepare() / syncTip() / syncPeek()   prepare (subtree roots + tip), read-only progress
syncStep(batchSize, maxBatches, priority, activeUfvksJson)
                                         bounded step, only for UFVKs the host enabled
queueRescanFrom(fromHeight)              non-destructive requeue of [fromHeight, tip]
rewindTo(height)                         destructive

transactionHistory(uuid, limit, offset)  limit ≤ 500
transactionDetails(uuid, txid) / transactionOutputs(uuid, txid)
deleteWallet(dbName)                     deletes the local cache (rebuildable)
storageStats()

pcztQuote(...)                           quote only; builds nothing, locks nothing
pcztCreate(...) → { pcztHex, reservationId }   build and lock the selected notes
releaseReservation(id)                   user cancel; idempotent
clearLockedOutputs(uuid)                 repair: clear the account's locks; refused
                                         while broadcasts are pending
pcztProve(pczt)
pcztCombine(original, signed)            merge an external signer's copy (signatures only)
pcztSubmit(uuid, pczt, reservationId)    → { txid, broadcast }
pendingBroadcastTxids(uuid) / broadcastRetryTxids(uuid)
broadcastTransaction(txid)               rebroadcast a stored transaction

prewarmPczt(network, height)             build proving parameters; no wallet
pcztProveAtHeight(height, pczt)          prove without a wallet
pcztExtractStateless(pczt)               verify and extract without a wallet
dependencyVersions() / setPerfTrace(enabled) / diag*
```

### `onekey-zcash-keys`

```
ufvkFromSeed(network, seedBytes, accountIndex)
unifiedAddress(network, ufvk, receiverPolicy)
                                         "all" = Orchard + transparent;
                                         "shielded" / "orchard" = Orchard only; never Sapling
transparentAddressFromUfvk(network, ufvk) / transparentAccountPubKeyFromUfvk(network, ufvk)
validateAddress(network, address) / seedFingerprint(seed)
pcztVerifyReview(pczt, reviewJson)       check only, no signing
pcztSignWithSeed(network, seed, accountIndex, pczt, reviewJson)
                                         only place spend keys are used; checks every
                                         spend and output against the review first
transparentTx* / transparentShield*      stateless transparent sends, see
                                         crates/zcash-keys-runtime/TRANSPARENT_TX_API.md
parseTransparentTransactions(requestJson)
keysCapabilities() / keysDependencyVersions()
```

### Sending

```
runtime.pcztCreate()      build (needs wallet DB: selection, witness, anchor), lock notes
runtime.pcztProve()       zero-knowledge proofs (slow)
keys.pcztSignWithSeed()   check against the review, then sign (needs the seed)
runtime.pcztSubmit()      combine check → extract → store → broadcast
```

Prove before signing: the upstream Signer must be able to verify what the proofs cover.

The signed bytes and the reservation→txid link are stored atomically before
broadcast; every retry and crash recovery sends the same bytes. Outcomes:

- `accepted`: the node took it. `already known`, `already in mempool` and
  `already in blockchain` count as success.
- `rejected`: only a reply that the transaction is consensus-invalid or expired. It
  stays stored; its notes return only after its expiry height.
- `unknown`: anything else (timeout, transient error, unclear reply). It stays pending
  and sync rebroadcasts the same bytes. The host must not show it as failed or
  prompt a resend, and must check `pendingBroadcastTxids` before building another
  payment.

`FINALIZE_OUTCOME_UNKNOWN` from `pcztSubmit` or `broadcastTransaction` means the
step went through but persistence is unconfirmed. Treat it like `unknown`: do not
unlock notes or rebuild the transaction.

A `reservationId` with a stored transaction cannot build again: `pcztCreate` refuses
it and `pcztSubmit` only rebroadcasts the stored one. Network calls time out after
30 seconds.

### Stateless transparent sends to Unified Addresses

`transparentTxQuote` accepts mainnet t1/t3/tex recipients and Unified Addresses with
an Orchard receiver (paid as an Ironwood output after NU6.3). It uses only the
host-supplied confirmed, non-coinbase transparent UTXOs, explicit outpoints, target
height and transparent change address. No wallet, database, scan or network call.
Sapling/Sprout destinations and coinbase inputs are unsupported.

Software keys (seed or account xprv):

1. `keys.transparentShieldCreateWithSeed` / `WithAccountXprv` builds a PCZT from the
   request used for the quote.
2. `runtime.pcztProveAtHeight` proves it.
3. `keys.transparentShieldSignWithSeed` / `WithAccountXprv` checks the request, the
   original PCZT and the proved PCZT, then signs.
4. `runtime.pcztExtractStateless` verifies and extracts the final transaction.

Hardware: `keys.transparentTxCreateWithAccountXpub` → device signs →
`keys.transparentTxCombineHardwareSigned` (verifies every signature against the
request) → `runtime.pcztProveAtHeight` → `runtime.pcztExtractStateless`.

`parseTransparentTransactions` verifies raw previous transactions against their
txids and returns output values, scripts and coinbase status; the host checks each
UTXO's index and value against it.

The host owns outpoint reservations, UTXO revalidation at signing time, pending
broadcast records and submission. The recipient scans to find the shielded notes;
the sender does not need to scan.

## Layout

```
crates/zcash-runtime/            wallet, scanning, PCZT build/prove/submit
  src/bindgen.rs                 JS interface
  src/send.rs                    proposals, reservations, PCZT lifecycle
  src/tx_state.rs                reservation→txid link and broadcast outcomes
  src/guard.rs                   persistent gate for destructive operations
  src/sync.rs                    bounded scanning
  src/storage.rs                 VFS install, connections, durability barrier
  build.rs                       links the wasm SQLite static library
crates/zcash-keys-runtime/       derivation, review checks, signing, stateless transparent sends
crates/zcash-storage-benchmark/  synthetic storage tests
patches/                         pinned upstream checksums and patches
scripts/vendor-deps.sh           verify official releases and apply recorded patches
scripts/build-wasm.sh            build
```
