# OneKey Zcash Runtime

Thin wasm bindings over the official `librustzcash`, with `zcash_client_sqlite` as
the wallet store. Two packages built from one source, used on every App platform:

- `onekey-zcash-keys`: derivation, review checks and signing. No SQLite, network,
  scanner or prover.
- `onekey-zcash-runtime`: wallet, scanning, SQLite and PCZT proving.

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

`pkg/` and `pkg-keys/` are build outputs and are not committed. Publishing is in
[RELEASING.md](./RELEASING.md).

## Design rules

1. **No forked Zcash storage.** `zcash_client_sqlite` is used unmodified except for
   one Cargo.toml feature (see Pitfalls).
2. **No seed or mnemonic enters any wasm.** The host passes only account-scoped keys
   (Orchard `m/32'/133'/account'`, transparent `m/44'/133'/account'`), only for a
   derive or sign call. The scanner holds the UFVK, never a spending key.
3. **Plain data across the boundary.** Strings, numbers, booleans, bytes and JSON
   strings only; no Rust object handles reach JS.
4. **Rust owns key security, large data and consensus correctness;** the host owns
   scheduling, accounts, persistence policy, retries and display.
5. **A rescan follows the new birthday.** An earlier start lowers the stored
   birthday and keeps what is known; a later start deletes the account and imports
   it again at that height, so nothing found before it remains. A later start
   waits (`WALLET_BUSY`, reason `pendingSend`) while the account has an unmined send.

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
removeAccount(uuid)                     pending transactions do not block removal
rescanAccount(uuid, fromHeight)          earlier: rewind and keep; later: start over there
                                         (new UUID; look the account up by UFVK)
accountByUfvk(ufvk) / listAccounts() / accountSyncStatus(uuid)
accountBalance(uuid, trusted, untrusted, allowZeroConfShielding)
                                         per pool: spendable, pendingChange,
                                         pendingSpendable, locked, total,
                                         uneconomic; total leaves out notes
                                         worth <= the ZIP-317 marginal fee,
                                         which are uneconomic instead

syncPrepare() / syncTip() / syncPeek()   prepare (subtree roots + tip), read-only progress
                                         syncPeek/accountSyncStatus include nullable
                                         chainTip and fullyScannedHeight without
                                         computing wallet balances
syncStep(batchSize, maxBatches, priority, activeUfvksJson)
                                         bounded step, only for UFVKs the host enabled
queueRescanFrom(fromHeight)              non-destructive requeue of [fromHeight, tip]
rewindTo(height)                         destructive

transactionHistory(uuid, limit, offset)  limit ≤ 500
transactionDetails(uuid, txid) / transactionOutputs(uuid, txid)
deleteWallet(dbName)                     deletes the local cache (rebuildable)
storageStats()
diagDatabaseExists(dbName)               persisted bytes exist; opens nothing
diagDatabaseHealth()                     read-only consistency checks

pcztQuote(...)                           quote only; builds nothing
pcztCreate(...) → { pcztHex, feeZat }    build; locks no notes
pcztProve(pczt)
pcztCombine(original, signed)            merge an external signer's copy (signatures only)
pcztSubmit(uuid, pczt)                   → { txid, broadcast }
pendingBroadcastTxids(uuid)              stored, unmined, unexpired, not rejected
broadcastTransaction(txid)               resend a stored transaction; explicit user request only

prewarmPczt(network, height)             build proving parameters; no wallet
pcztProveAtHeight(height, pczt)          prove without a wallet
pcztExtractStateless(pczt)               verify and extract without a wallet
capabilities() / dependencyVersions()
```

### Consecutive old-birthday imports

`DATABASE_ERROR` with `params.operation = "importAccount"` and
`params.reason = "checkpointsDiverged"` is upstream rejecting mismatched pool
checkpoints. The import rolls back and the database stays usable: keep the account
pending and retry the import after further scanning; it succeeds once the pending
scan finishes.

### `onekey-zcash-keys`

```
ufvkFromAccountKeys(network, orchardSk, accountXpub, accountIndex)
                                         mainnet only; orchardSk is the 32-byte Orchard key
unifiedAddress(network, ufvk, receiverPolicy)
                                         "all" = Orchard + transparent;
                                         "orchard" = Orchard only; never Sapling
transparentAddressFromUfvk(network, ufvk) / transparentAccountPubKeyFromUfvk(network, ufvk)
validateAddress(network, address)
pcztVerifyReview(pczt, reviewJson)       check only, no signing
pcztSignWithAccountKeys(network, orchardSk, accountXprv, accountIndex, pczt, reviewJson)
                                         mainnet only; requires the keys to rebuild the
                                         reviewed UFVK, then checks every spend and output
pcztRedactForHardwareSigner(pczt)        copy to send to a device; keep the original
transparentTx* / transparentShield*      stateless transparent sends, see
parseTransparentTransactions(requestJson)  crates/zcash-keys-runtime/TRANSPARENT_TX_API.md
keysCapabilities() / keysDependencyVersions()
```

### Sending

The transaction-output history API distinguishes recorded payment outputs from
change using the exact transaction, pool and output index in `sent_notes`. Its
`isChange` excludes explicit self-payments, which compact scanning also flags as
change for wallet accounting. This read projection does not modify scan state.

```
runtime.pcztCreate()             build (selection, witness, anchor)
runtime.pcztProve()              zero-knowledge proofs (slow)
keys.pcztSignWithAccountKeys()   check against the review, then sign
  or hardware: keys.pcztRedactForHardwareSigner() → device → runtime.pcztCombine()
runtime.pcztSubmit()             extract → store → broadcast
```

Prove before signing: the upstream Signer must be able to verify what the proofs
cover. Building locks no notes: the signed transaction is stored before broadcast
and from then on holds its notes until mined or expired. Storing is refused when
another stored, unexpired transaction already spends one of the same notes (two
reviews opened at once). Sync does not rebroadcast it. Broadcast outcomes:

- `accepted`: the node took it (`already known` / `in mempool` / `in blockchain`
  count as success).
- `rejected`: only an explicit consensus-invalid or expired reply, recorded in
  `ext_onekey_tx_state` (the only rows it holds). The transaction stays stored;
  its notes return after its expiry height.
- `unknown`: anything else. It stays pending until mined or expired; the host does
  not resubmit it.

Pending transactions do not block another payment or user-requested local-data
deletion, reset or rescan.

Submission always extracts and stores the current signed transaction, including
on retry. A shared nullifier identifies an input conflict; it never authorizes
returning or broadcasting a different stored transaction in its place.

`FINALIZE_OUTCOME_UNKNOWN` means persistence is unconfirmed: treat it like `unknown`
and do not rebuild. Network calls time out after 30 seconds.

Transparent-only sends (t-address or Unified Address recipients, no wallet or scan)
use the stateless API in
[TRANSPARENT_TX_API.md](./crates/zcash-keys-runtime/TRANSPARENT_TX_API.md).

## Pitfalls

- **The lightwalletd endpoint must support gRPC-web** (and CORS without a host
  transport). A native-gRPC-only endpoint hangs on the first browser request even
  if `grpcurl` works. No endpoint is hardcoded.
- **Scanning does not use upstream `scan_cached_blocks`.** It hands trial decryption
  to `rayon::spawn_fifo`, which never runs in single-threaded wasm and blocks the
  thread forever. `scan_blocks_inline` replaces it; use `syncPeek()` for progress.
- **`rusqlite`'s `bundled` feature breaks wasm32** (`'stdio.h' file not found`) and
  Cargo features cannot be disabled downstream, so `scripts/vendor-deps.sh` patches
  `zcash_client_sqlite`'s Cargo.toml. It also applies OPFS durability fixes to
  `sqlite-wasm-vfs` and host fetch injection to `tonic-web-wasm-client`. All patches
  are recorded in `patches/manifest.json`; `--offline` rejects unrecorded vendor
  changes. The precompiled wasm SQLite is linked by `crates/zcash-runtime/build.rs`.
- **Storage is OPFS** (SAH pool in `.onekey-zcash-opfs`, `zcash-main.db` /
  `zcash-test.db`), with `journal_mode=DELETE`, `synchronous=FULL` and
  `secure_delete=ON`. Deleting a database or journal truncates its pool file to
  the 4 KiB header once the header clear is flushed; opening the pool truncates
  any free file that still holds data. One Worker
  owns each origin's pool. OPFS is per-origin and subject to quota and eviction, so
  the embedding page's origin must be stable. When `storageRequiresWorkerRestart()`
  is true after an I/O failure, keep the failed call's result and restart the
  Worker. No SharedArrayBuffer or COOP/COEP is needed.
- `openWallet()` fails with `DATABASE_TOO_NEW` when a newer runtime migrated the
  database. It is not corruption: keep the database and update the runtime.
- `syncPrepare()` downloads every pool's subtree roots (many on mainnet); give it its
  own "preparing" state.
- Sapling is out of scope: not a receiver, scan identity or send source.

## Layout

```
crates/zcash-runtime/            wallet, scanning, PCZT build/prove/submit
crates/zcash-keys-runtime/       derivation, review checks, signing, stateless transparent sends
patches/                         pinned upstream checksums and patches
scripts/                         vendor-deps.sh, build-wasm.sh, package-release.py
```
