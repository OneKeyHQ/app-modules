//! Host-facing JS/TS interface.
//!
//! ## Conventions
//!
//! - Parameters and return values are only string / number / boolean / JSON string.
//! - No Rust object handles are handed to JS for lifetime management.
//! - A thrown `Error` carries `code` / `params` / `detail`.
//!   Branch on `code` (stable error code); fill i18n placeholders from `params`;
//!   `detail` is for logs only. No user-facing text is produced here; that belongs to the host.
//! - Every call is a complete round trip: no background tasks, no timers.

// ── Stable contract vs diagnostics ──────────────────────────────────────
//
// `diag*` methods are debugging tools, not a stable contract: their signatures may
// change or they may disappear. The prefix makes dependence on them visible at the call site.
//
// All other methods form the wallet contract; changes require a version bump.

use wasm_bindgen::prelude::*;

use crate::perf;
use crate::{
    account,
    error::{ErrorCode, RuntimeError},
    guard, history, network, runtime, send, storage, sync, tx_state, wallet,
};

/// Inject a Fetch-compatible host transport; None retains browser Fetch.
#[wasm_bindgen(js_name = setRequestHandler)]
pub fn set_request_handler(handler: Option<js_sys::Function>) {
    network::set_request_handler(handler);
}

// ── Lifecycle ───────────────────────────────────────────────────────────

/// Installs the storage layer. Must be awaited once before any other call. Idempotent.
#[wasm_bindgen(js_name = init)]
pub async fn init() -> Result<(), JsValue> {
    install_panic_hook();
    storage::install_persistent_vfs().await?;
    Ok(())
}

/// Preserve a failed call's result, then retire its poisoned storage owner.
#[wasm_bindgen(js_name = storageRequiresWorkerRestart)]
pub fn storage_requires_worker_restart() -> bool {
    storage::requires_worker_restart()
}

/// Turns a panic into a console error with a stack trace instead of a Promise that never settles.
///
/// Must be installed at every entry point, including `diagInitMemoryStorage()`.
fn install_panic_hook() {
    console_error_panic_hook::set_once();
}

/// Enables tracing output to the browser console (requires the `debug-tracing` feature).
///
/// Upstream `zcash_client_backend` is instrumented with `tracing`; when sync stalls,
/// the last console entry shows the step it stopped at.
///
/// Very verbose and slows scanning; use only for debugging.
#[wasm_bindgen(js_name = diagEnableTracing)]
pub fn enable_debug_tracing() {
    #[cfg(feature = "debug-tracing")]
    {
        use tracing_subscriber::prelude::*;
        let _ = tracing_subscriber::registry()
            .with(tracing_wasm::WASMLayer::new(Default::default()))
            .try_init();
    }
}

/// Uses in-memory storage: no persistent VFS; data lives only for the page lifetime.
///
/// For ephemeral wallets, and for isolating storage from computation when profiling.
///
/// Call instead of `init()`, never both.
#[wasm_bindgen(js_name = diagInitMemoryStorage)]
pub fn init_memory_storage() {
    install_panic_hook();
    storage::use_memory_storage();
}

/// The capabilities of this version-locked wallet and keys package set.
///
/// Lets the host hide unsupported actions up front instead of failing at proving time,
/// after notes are locked and the user has entered a password.
///
#[wasm_bindgen(js_name = capabilities)]
pub fn capabilities() -> String {
    serde_json::json!({
        "prove": {
            "orchard": send::PROVE_ORCHARD,
            "sapling": send::PROVE_SAPLING,
            "ironwood": send::PROVE_IRONWOOD,
        },
        "scan": true,
        "balance": true,
        "history": true,
        // The wallet creates proposals and proofs; the keys package signs inputs.
        // The host must combine both packages' capabilities.
        "proveShielding": true,
        "notes": {
            // Why a capability is unavailable, so the host can show different hints.
            "sapling": "Sapling is out of scope: not scanned, shown, received or spent; \
    its ~50MB proving parameters are not shipped",
            "transparent": "Transparent inputs are signed by the keys package; this package \
    only proposes and proves. Shielding needs both packages' flags",
        },
    })
    .to_string()
}

/// Official dependency versions pinned by this runtime, as a JSON string.
#[wasm_bindgen(js_name = dependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// Diagnostic: structural summary of a single block. No scanning, no writes, no state change.
///
/// Confirms whether browser (gRPC-web proxy) and native (gRPC) clients receive the same data.
#[wasm_bindgen(js_name = diagBlockSummary)]
pub async fn block_summary(height: u32) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |db, mut client| async move {
            let r = network::block_summary(&mut client, height).await;
            (db, client, r)
        })
        .await?,
    )
}

/// Open a page-backed database and apply the official SQLite migrations.
/// No whole-database preload is required by the shared OPFS VFS.
#[wasm_bindgen(js_name = openWallet)]
pub async fn open_wallet(network_name: String, db_name: String) -> Result<String, JsValue> {
    let _perf = perf::span("openWallet");
    let (network_name, db_name) = (network_name.as_str(), db_name.as_str());
    let db = wallet::open_and_migrate(network_name, db_name)?;
    // Rejected with WALLET_BUSY while an async operation runs; must propagate,
    // otherwise the caller assumes the database was swapped.
    runtime::set_db(db, db_name)?;
    Ok(serde_json::json!({ "dbName": db_name, "network": network_name }).to_string())
}

/// Temporary perf tracing switch: logs per-step timings to the console (`[PRIV-PERF] rt`).
#[wasm_bindgen(js_name = setPerfTrace)]
pub fn set_perf_trace(enabled: bool) {
    perf::set_enabled(enabled);
}

/// Closes the wallet, releasing the database and network client.
///
/// Throws `WALLET_BUSY` while an async operation holds the database.
#[wasm_bindgen(js_name = closeWallet)]
pub fn close_wallet() -> Result<(), JsValue> {
    Ok(runtime::close()?)
}

/// Configures the lightwalletd endpoint, e.g. `https://host:port`.
///
/// The host chooses the endpoint; this runtime hardcodes no server address.
/// The endpoint must support gRPC-web. CORS is required only without a host transport.
#[wasm_bindgen(js_name = setLightwalletdUrl)]
pub fn set_lightwalletd_url(base_url: &str) -> Result<(), JsValue> {
    runtime::set_client(network::connect(base_url)?);
    Ok(())
}

// ── Chain info ──────────────────────────────────────────────────────────

/// Current chain tip height. Requires only `setLightwalletdUrl`, not an open wallet.
///
/// The host uses it to pick a new account's birthday before the database exists;
/// otherwise the birthday falls back to Sapling activation and a full scan.
#[wasm_bindgen(js_name = chainTip)]
pub async fn chain_tip() -> Result<u32, JsValue> {
    Ok(runtime::with_client(|mut client| async move {
        let r = network::chain_tip(&mut client).await;
        (client, r)
    })
    .await?)
}

/// Fetches the current chain tip with an isolated client.
///
/// Host-side birthday and scheduler probes must not replace the wallet's
/// global client while a sync operation has borrowed it across an await.
#[wasm_bindgen(js_name = chainTipAt)]
pub async fn chain_tip_at(base_url: String) -> Result<u32, JsValue> {
    let mut client = network::connect(&base_url)?;
    Ok(network::chain_tip(&mut client).await?)
}

// ── Accounts ────────────────────────────────────────────────────────────

/// Imports a view-only account (UFVK) and returns its UUID.
///
/// `birthdayHeight` is the scan start. New accounts pass the current tip; restores must pass
/// a height early enough not to miss historical funds. This decision belongs to the host.
#[wasm_bindgen(js_name = importAccountUfvk)]
pub async fn import_account_ufvk(
    account_name: String,
    ufvk: String,
    birthday_height: u32,
    seed_fingerprint_hex: Option<String>,
    account_index: Option<u32>,
) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let derivation = seed_fingerprint_hex.as_deref().zip(account_index);
            let r = account::import_ufvk(
                &mut db,
                &mut client,
                &account_name,
                &ufvk,
                birthday_height,
                derivation,
            )
            .await;
            (db, client, r)
        })
        .await?,
    )
}

/// Removes one account from the shared database. Does not delete the database, which holds other wallets' accounts.
#[wasm_bindgen(js_name = removeAccount)]
pub async fn remove_account(account_uuid: String) -> Result<(), JsValue> {
    runtime::with_read_conn(|conn| {
        guard::assert_idle(
            conn,
            Some(&account_uuid),
            "removeAccount",
            guard::LockPolicy::Blocking,
        )
    })?;
    runtime::with_db(|db| account::remove_account(db, &account_uuid))?;
    storage::durability_barrier().await?;
    Ok(())
}

/// The host already deleted this account: removes it regardless of in-flight transactions,
/// skipping `removeAccount`'s idle check.
///
/// Funds are on chain and the seed is with the user, so a re-import and rescan recovers them.
#[wasm_bindgen(js_name = removeDeletedAccount)]
pub async fn remove_deleted_account(account_uuid: String) -> Result<(), JsValue> {
    runtime::with_db(|db| account::remove_account(db, &account_uuid))?;
    send::forget_account_reservations(&account_uuid);
    storage::durability_barrier().await?;
    Ok(())
}

/// Finds the account for a UFVK, returning `{ uuid }` or `"null"` if absent.
///
/// The only correct lookup in the shared per-network database: the first account is not
/// necessarily the one wanted, and UUIDs change after a purge.
#[wasm_bindgen(js_name = accountByUfvk)]
pub fn account_by_ufvk(ufvk: String) -> Result<String, JsValue> {
    let _perf = perf::span("accountByUfvk");
    Ok(runtime::with_db(|db| account::account_by_ufvk(db, &ufvk))?)
}

/// Per-account sync status, derived from the account birthday and the database's remaining scan ranges.
///
/// Global progress misleads in a shared database: adding an account makes all accounts look unsynced.
/// The official schema has no per-account cursor and is not changed for this; this is read-only.
///
/// Returns block heights, not a percentage: the upstream progress ratio is over notes in a moving
/// window and never reaches 100%.
#[wasm_bindgen(js_name = accountSyncStatus)]
pub fn account_sync_status(account_uuid: String) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| {
        sync::account_status(db, &account_uuid)
    })?)
}

/// Lists local account UUIDs as a JSON array.
#[wasm_bindgen(js_name = listAccounts)]
pub fn list_accounts() -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| account::list_accounts_json(db))?)
}

/// Account balance as JSON.
///
/// `trusted` / `untrusted` are confirmation thresholds (both >= 1, trusted <= untrusted):
/// own change can be looser, external notes stricter. Values are a product decision passed by the host.
/// Throws `NOT_SYNCED` before any block has been synced.
#[wasm_bindgen(js_name = accountBalance)]
pub fn account_balance(
    account_uuid: String,
    trusted: u32,
    untrusted: u32,
    allow_zero_conf_shielding: bool,
) -> Result<String, JsValue> {
    let _perf = perf::span("accountBalance");
    let policy = account::confirmations_policy(trusted, untrusted, allow_zero_conf_shielding)?;
    Ok(runtime::with_db(|db| {
        account::balance_json(db, &account_uuid, policy)
    })?)
}

// ── Scanning ────────────────────────────────────────────────────────────

/// One-time sync preparation: downloads subtree roots for all three pools and updates the chain tip.
///
/// The slowest step of a first sync, independent of batchSize; exposed separately so the host
/// can show a distinct "preparing" state.
///
/// Returns `{ chainTip, saplingSubtrees, orchardSubtrees, ironwoodSubtrees }`.
/// Call once at the start of a sync round.
#[wasm_bindgen(js_name = syncPrepare)]
pub async fn sync_prepare() -> Result<String, JsValue> {
    let _perf = perf::span("syncPrepare");
    Ok(
        runtime::with_db_and_client(|mut db, mut client| async move {
            let r = sync::prepare(&mut db, &mut client).await;
            (db, client, r)
        })
        .await?,
    )
}

/// Updates only the chain tip (one unary call, ~270ms on mainnet). Returns `{ chainTip }`.
///
/// Use every sync round; subtree roots go through `syncPrepare` once per open, since
/// re-downloading the ~1900 immutable roots costs ~7s per round on mainnet.
#[wasm_bindgen(js_name = syncTip)]
pub async fn sync_tip() -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(|mut db, mut client| async move {
            let r = sync::tip(&mut db, &mut client).await;
            (db, client, r)
        })
        .await?,
    )
}

/// Read-only view of pending scan ranges; no network requests. Returns
/// `{ ranges: [{ start, end, len, priority }], totalBlocks }`.
///
/// For host progress bars and for checking which range the wallet wants to scan.
#[wasm_bindgen(js_name = syncPeek)]
pub fn sync_peek() -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| sync::peek(db))?)
}

/// Diagnostic: downloads without scanning and returns per-phase timings. Does not change wallet state.
///
/// Separates network stalls from scan stalls, which look the same in the browser.
#[wasm_bindgen(js_name = diagProbeDownload)]
pub async fn sync_probe_download(batch_size: u32) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |db, mut client| async move {
            let r = sync::probe_download(&db, &mut client, batch_size).await;
            (db, client, r)
        })
        .await?,
    )
}

/// Quotes only: no transaction built, no notes locked. Returns `{ feeZat, steps }`.
///
/// Shares the proposal path with `pcztCreate`, so the quoted fee is the fee paid.
/// Insufficient balance throws `INSUFFICIENT_FUNDS` (with shortfallZat).
#[wasm_bindgen(js_name = pcztQuote)]
pub fn pczt_quote(
    account_uuid: String,
    to_address: String,
    amount_zat: u64,
    memo: Option<String>,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    pad_orchard_bundle: bool,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<String>,
    memo_hex: Option<String>,
) -> Result<String, JsValue> {
    // Same constructor and parameter order as `pcztCreate`: any differing selection
    // field would make the quoted fee differ from the fee paid.
    let policy = send::parse_send_policy(
        trusted,
        untrusted,
        &fallback_change_pool,
        pad_orchard_bundle,
        allow_zero_conf_shielding,
        spend_source.as_deref(),
    )?;
    Ok(runtime::with_db(|db| {
        send::quote(
            db,
            &account_uuid,
            &to_address,
            amount_zat,
            send::parse_memo(memo.as_deref(), memo_hex.as_deref())?.as_ref(),
            policy,
        )
    })?)
}

/// Single transaction details as JSON. Returns `"null"` rather than an error when the wallet
/// lacks the transaction (external txid, rolled-back transaction); that is a normal state.
#[wasm_bindgen(js_name = transactionDetails)]
pub fn transaction_details(account_uuid: String, txid: String) -> Result<String, JsValue> {
    let _perf = perf::span("transactionDetails");
    Ok(runtime::with_read_conn(|conn| {
        history::details(conn, &account_uuid, &txid)
    })?)
}

/// Per-output breakdown of one transaction: `{ txid, spent[], received[], external[] }`.
///
/// `transactionDetails` gives aggregates (value delta, counts) for lists; this gives each
/// output (value, pool, change flag, memo, address) for the details page.
#[wasm_bindgen(js_name = transactionOutputs)]
pub fn transaction_outputs(account_uuid: String, txid: String) -> Result<String, JsValue> {
    Ok(runtime::with_read_conn(|conn| {
        history::details_outputs(conn, &account_uuid, &txid)
    })?)
}

/// Bounded scan step: at most `maxBatches` batches of `batchSize` blocks.
///
/// `priority` selects the lane: `"chainTip"` follows new blocks, `"historic"` backfills,
/// absent or `"any"` follows upstream order. Candidate ranges come only from upstream
/// suggestions; the host cannot pick arbitrary heights. Scheduling belongs to the host,
/// range safety to the protocol implementation.
///
/// `activeUfvksJson` is a required JSON string array. Only those identities are used for
/// Orchard/Ironwood trial decryption. An empty array fails with `NO_ACTIVE_ACCOUNTS`; it never
/// falls back to every account stored in the runtime database.
///
/// The result includes `scannedFrom/scannedTo/scannedPriority` so the host can attribute progress to a lane.
///
/// Never loops to completion on its own. Returns
/// `{ done, batchesDone, blocksScanned, remainingBlocks, remainingRanges }`;
/// the host decides whether to call again.
#[wasm_bindgen(js_name = syncStep)]
pub async fn sync_step(
    batch_size: u32,
    max_batches: u32,
    priority: Option<String>,
    active_ufvks_json: String,
) -> Result<String, JsValue> {
    let _perf = perf::span("syncStep");
    let preferred = sync::parse_priority(priority.as_deref().unwrap_or(""))?;
    let active_ufvks: Vec<String> = serde_json::from_str(&active_ufvks_json).map_err(|e| {
        RuntimeError::new(ErrorCode::InvalidUfvk).detail(format!("activeUfvksJson: {e}"))
    })?;
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let r = sync::step(
                &mut db,
                &mut client,
                batch_size,
                max_batches,
                preferred,
                &active_ufvks,
            )
            .await;
            (db, client, r)
        })
        .await?,
    )
}

// ── Transaction history ─────────────────────────────────────────────────

/// Paged transaction history as a JSON array. Unmined first, then by height descending.
///
/// Returns neutral facts (value delta, change flag, note counts, timestamps), no
/// incoming/outgoing classification and no text; the display model belongs to the host.
///
/// `limit` is capped at 500; above that throws `INVALID_BATCH_SIZE`.
#[wasm_bindgen(js_name = transactionHistory)]
pub fn transaction_history(
    account_uuid: String,
    limit: u32,
    offset: u32,
) -> Result<String, JsValue> {
    let _perf = perf::span("transactionHistory");
    Ok(runtime::with_read_conn(|conn| {
        history::list(conn, &account_uuid, limit, offset)
    })?)
}

// ── Send ────────────────────────────────────────────────────────────────

/// Rewinds the wallet to a height so later syncs rescan that range.
///
/// Destructive: scan results above the height are discarded. Returns the actual height
/// rewound to (upstream may go further back to a commitment tree checkpoint).
#[wasm_bindgen(js_name = rewindTo)]
pub fn rewind_to(height: u32) -> Result<u32, JsValue> {
    // Deliberately no idle gate: this is how the wallet follows a reorg (sole caller is
    // syncWallet's reorg branch). Refusing because a transaction is unmined would pin the
    // account to an abandoned chain.
    //
    // User-triggered rescans use `queueRescanFrom`, which only requeues ranges.
    Ok(runtime::with_db(|db| sync::rewind_to(db, height))?)
}

/// Re-queue `[fromHeight, chainTip]` as historic work without deleting cached notes or history.
///
/// This is the resume companion to `syncStep(activeUfvksJson)`: the host persists each privacy
/// identity's pause cursor, applies its reorg margin, queues the missing range, and only then adds
/// the identity back to the active UFVK set.
#[wasm_bindgen(js_name = queueRescanFrom)]
pub fn queue_rescan_from(from_height: u32) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| {
        sync::queue_rescan_from(db, from_height)
    })?)
}

// ── Send ────────────────────────────────────────────────────────────────
//
// Flow: pcztCreate → pcztProve → (keys package) pcztSign → pcztSubmit
//
// Proving and signing may happen in either order (they touch different PCZT parts),
// but both must finish before pcztSubmit or the transaction is invalid.

/// Builds an unproven, unsigned PCZT for a transfer and returns its bytes.
///
/// `fallbackChangePool`: the product currently always passes `"ironwood"`, so change goes to the newest shielded pool.
/// `padOrchardBundle`: pads the Orchard bundle to hide the real action count (larger, higher fee).
#[wasm_bindgen(js_name = pcztCreate)]
pub fn pczt_create(
    account_uuid: String,
    to_address: String,
    amount_zat: u64,
    memo: Option<String>,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    pad_orchard_bundle: bool,
    lock_for_blocks: u32,
    reservation_id: Option<String>,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<String>,
    memo_hex: Option<String>,
) -> Result<String, JsValue> {
    let policy = send::parse_send_policy(
        trusted,
        untrusted,
        &fallback_change_pool,
        pad_orchard_bundle,
        allow_zero_conf_shielding,
        spend_source.as_deref(),
    )?;
    Ok(runtime::with_db(|db| {
        let (bytes, reservation_id, fee_zat) = send::create_pczt(
            db,
            &account_uuid,
            &to_address,
            amount_zat,
            send::parse_memo(memo.as_deref(), memo_hex.as_deref())?.as_ref(),
            policy,
            lock_for_blocks,
            reservation_id.as_deref(),
        )?;
        Ok(serde_json::json!({
            "pcztHex": bytes.iter().map(|b| format!("{b:02x}")).collect::<String>(),
            "reservationId": reservation_id,
            "feeZat": fee_zat,
        })
        .to_string())
    })?)
}

/// Creates Orchard / Ironwood zero-knowledge proofs. Must run before pcztSubmit.
#[wasm_bindgen(js_name = pcztProve)]
pub fn pczt_prove(pczt_bytes: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(runtime::with_db(|db| send::prove_pczt(db, &pczt_bytes))?)
}

/// Merges a redacted PCZT returned by an external (hardware) signer back into the full local PCZT.
///
/// Hardware returns only public fields such as signatures; private fields (rseed, witness, etc.)
/// come back as None. The Combiner takes whichever side has a value, so local + signed copies
/// yield a provable, extractable PCZT. Needs no wallet database.
#[wasm_bindgen(js_name = pcztCombine)]
pub fn pczt_combine(original: Vec<u8>, signed: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(send::combine_pczt(&original, &signed)?)
}

/// Releases all locked notes of the account; returns the number unlocked.
///
/// For crash recovery: `releaseReservation` relies on the in-memory proposal table, which is
/// lost when the app / offscreen restarts.
///
/// Also releases in-progress proposals, so call only from a user-triggered repair action,
/// never from the normal flow.
#[wasm_bindgen(js_name = clearLockedOutputs)]
pub fn clear_locked_outputs(account_uuid: String) -> Result<u32, JsValue> {
    // Locks are not "busy" (they are what this clears), but sent transactions are: unlocking
    // notes spent by a pending broadcast would return them to selection and allow a double spend.
    runtime::with_read_conn(|conn| {
        guard::assert_idle(
            conn,
            Some(&account_uuid),
            "clearLockedOutputs",
            guard::LockPolicy::Ignored,
        )
    })?;
    Ok(runtime::with_db(|db| {
        send::clear_locked_outputs(db, &account_uuid)
    })?)
}

/// Releases the notes held by a reservation (called when the user cancels a send).
///
/// Idempotent: returns `false` instead of an error when the id was sent, already released,
/// or never existed; repeated calls on the cancel path are normal.
#[wasm_bindgen(js_name = releaseReservation)]
pub fn release_reservation(reservation_id: String) -> Result<bool, JsValue> {
    Ok(runtime::with_db(|db| {
        send::release_reservation(db, &reservation_id)
    })?)
}

/// Durability barrier after an irreversible step.
///
/// A barrier failure means "unknown whether persistence reached durable storage", not that
/// the step failed. Both callers are past the point of no return (`pcztSubmit` committed the
/// transaction, `broadcastTransaction` sent it), so the real outcome must not be replaced by a
/// generic storage error that would make the host retry or show an on-chain tx as failed.
///
/// Callers supply `params` (outcome shapes differ); the error code and detail are set here.
async fn barrier_after_irreversible(params: serde_json::Value) -> Result<(), crate::RuntimeError> {
    storage::durability_barrier().await.map_err(|error| {
        crate::RuntimeError::with(crate::ErrorCode::FinalizeOutcomeUnknown, params).detail(error)
    })
}

/// Submits a proven and signed PCZT: extracts the transaction, persists it, then broadcasts.
///
/// Returns `{ txid, broadcast: "accepted" | "rejected" | "unknown", error? }`.
/// - `rejected`: the node definitively judged it consensus-invalid or expired; it can never be
///   mined. The transaction stays stored and its notes return only at expiry height.
/// - `unknown`: delivery to the node is uncertain. The transaction stays pending and is
///   rebroadcast on sync; the host must not treat it as a failed send.
#[wasm_bindgen(js_name = pcztSubmit)]
pub async fn pczt_submit(
    account_uuid: String,
    pczt_bytes: Vec<u8>,
    reservation_id: String,
) -> Result<String, JsValue> {
    let outcome = runtime::with_db_and_client(move |mut db, mut client| async move {
        let r = send::submit_signed_pczt(
            &mut db,
            &mut client,
            &account_uuid,
            &pczt_bytes,
            &reservation_id,
        )
        .await;
        (db, client, r)
    })
    .await?;
    let json = match outcome {
        send::SubmitOutcome::Accepted { txid } => {
            serde_json::json!({ "txid": txid, "broadcast": "accepted" })
        }
        send::SubmitOutcome::Rejected { txid, error } => serde_json::json!({
            "txid": txid,
            "broadcast": "rejected",
            "error": error.to_json(),
        }),
        send::SubmitOutcome::Unknown { txid, error } => serde_json::json!({
            "txid": txid,
            "broadcast": "unknown",
            "error": error.to_json(),
        }),
    };
    barrier_after_irreversible(json.clone()).await?;
    Ok(json.to_string())
}

/// Runtime-owned unresolved broadcast intents for this account. The host uses
/// this only to fail closed before constructing another payment; it does not
/// persist or reinterpret the returned transaction IDs.
#[wasm_bindgen(js_name = pendingBroadcastTxids)]
pub fn pending_broadcast_txids(account_uuid: String) -> Result<String, JsValue> {
    let txids = runtime::with_read_conn(|conn| tx_state::unresolved_txids(conn, &account_uuid))?;
    Ok(serde_json::to_string(&txids).map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::DatabaseError,
            serde_json::json!({ "operation": "serializePendingBroadcasts" }),
        )
        .detail(error)
    })?)
}

#[wasm_bindgen(js_name = broadcastRetryTxids)]
pub fn rebroadcastable_txids(account_uuid: String) -> Result<String, JsValue> {
    let txids =
        runtime::with_read_conn(|conn| tx_state::rebroadcastable_txids(conn, &account_uuid))?;
    Ok(serde_json::to_string(&txids).map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::DatabaseError,
            serde_json::json!({ "operation": "serializeRebroadcastableTransactions" }),
        )
        .detail(error)
    })?)
}

/// Rebroadcasts a stored transaction with an unknown outcome (called during sync). Irreversible.
///
/// Throws `BROADCAST_REJECTED` when the node definitively rejects it (do not retry);
/// `BROADCAST_OUTCOME_UNKNOWN` when the node errored without proving it can never be mined;
/// `NETWORK_ERROR` on transport failure. In the latter two cases the transaction stays pending
/// and is rebroadcast on the next sync.
#[wasm_bindgen(js_name = broadcastTransaction)]
pub async fn broadcast_transaction(txid: String) -> Result<(), JsValue> {
    let result = runtime::with_db_and_client(move |mut db, mut client| async move {
        let r = match tx_state::assert_broadcastable(&mut db, &txid)
            .and_then(|_| send::raw_transaction(&db, &txid))
        {
            Ok(raw) => network::broadcast(&mut client, raw).await,
            Err(e) => Err(e),
        };
        let r = match r {
            Ok(()) => tx_state::mark_accepted(&mut db, &txid),
            Err(e) if e.code == crate::ErrorCode::BroadcastRejected => {
                // Even if persisting fails, return the node's rejection reason: it is what the
                // user can act on. The persistence failure is attached as detail.
                match tx_state::mark_rejected(&mut db, &txid, &e) {
                    Ok(()) => Err(e),
                    Err(persist_error) => {
                        Err(e.detail(format!("mark_rejected failed: {persist_error}")))
                    }
                }
            }
            Err(e) => Err(e),
        };
        (db, client, r)
    })
    .await;
    let changed_lifecycle = result.is_ok()
        || result
            .as_ref()
            .is_err_and(|error| error.code == crate::ErrorCode::BroadcastRejected);
    if changed_lifecycle {
        // The broadcast happened and the lifecycle is committed; the outcome must reach the host unchanged.
        let outcome = match &result {
            Ok(()) => serde_json::json!({ "broadcast": "accepted" }),
            Err(rejection) => serde_json::json!({
                "broadcast": "rejected",
                "rejection": rejection.to_json(),
            }),
        };
        barrier_after_irreversible(
            serde_json::json!({ "operation": "broadcastTransaction", "outcome": outcome }),
        )
        .await?;
    }
    Ok(result?)
}

// ── Self-check ──────────────────────────────────────────────────────────

/// Deletes a wallet database (including its bytes in IndexedDB). Irreversible.
///
/// The database is a derived cache: UFVKs and birthdays are kept by the host, so deleting
/// only forces a rescan. Used after the last account is removed and on app reset.
///
/// Does not check in-flight transactions: both uses must succeed, and transactions are already
/// on chain or will expire. Operations that must wait for settlement are gated by the host.
///
/// Closes the database first if it is the one currently open (required before deletion).
/// Returns `true` if deleted, `false` if it did not exist (not an error).
#[wasm_bindgen(js_name = deleteWallet)]
pub async fn delete_wallet(db_name: String) -> Result<bool, JsValue> {
    if !storage::database_exists(&db_name).await? {
        return Ok(false);
    }
    if runtime::open_db_name().as_deref() == Some(db_name.as_str()) {
        runtime::close()?;
    }
    Ok(storage::delete_database(&db_name).await?)
}

/// Usage of the open wallet database, grouped by data meaning. Read-only.
///
/// A stable contract, not `diag*`: the host uses it for capacity guards (e.g. stopping
/// birthday backfill above a soft threshold).
///
/// `logicalBytes` is page count × page size, not IndexedDB physical usage (which includes
/// write amplification, stale pages and async flushes). Do not extrapolate one from the other.
///
/// Arbitrary SQL is deliberately not offered: one bad statement could delete an anchor still needed for proving.
#[wasm_bindgen(js_name = storageStats)]
pub fn storage_stats() -> Result<String, JsValue> {
    let db_name = runtime::open_db_name()
        .ok_or_else(|| JsValue::from(crate::RuntimeError::new(crate::ErrorCode::WalletNotOpen)))?;
    let conn = storage::open_connection(&db_name)?;
    Ok(crate::stats::storage_stats(&conn)?)
}

/// Diagnostic: checks for persisted bytes without opening or creating a database.
#[wasm_bindgen(js_name = diagDatabaseExists)]
pub async fn database_exists(db_name: &str) -> Result<bool, JsValue> {
    Ok(storage::database_exists(db_name).await?)
}

/// Diagnostic: runs bounded, read-only consistency checks on the open database.
#[wasm_bindgen(js_name = diagDatabaseHealth)]
pub fn database_health() -> Result<String, JsValue> {
    Ok(runtime::with_read_conn(crate::stats::database_health)?)
}

/// Diagnostic: counts inputs per bundle in a PCZT.
///
/// Input counts are direct evidence of which pool funds left from; fee differences are only indirect.
#[wasm_bindgen(js_name = diagPcztInputCounts)]
pub fn pczt_input_counts(pczt_bytes: Vec<u8>) -> Result<String, JsValue> {
    let pczt = pczt::Pczt::parse(&pczt_bytes).map_err(|e| {
        JsValue::from(
            crate::RuntimeError::new(crate::ErrorCode::PcztError).detail(format!("{e:?}")),
        )
    })?;
    // Action counts alone mislead: Orchard/Ironwood actions pair a spend with an output, and
    // bundles are padded with dummies to at least 2. Two actions may be a real shielded spend
    // or just a change output plus a dummy.
    //
    // `valueSum` = the pool's spends minus outputs (sign given separately). Negative means net
    // inflow (change only); positive means notes from this pool were actually spent.
    let sum = |b: &pczt::orchard::Bundle| {
        let (magnitude, negative) = *b.value_sum();
        serde_json::json!({ "magnitude": magnitude, "negative": negative })
    };
    // The transparent input value is the reliable mixing indicator:
    // shielded spend = payment + fee − transparent input. `valueSum` gives only net flow, and a
    // net inflow does not imply zero shielded spend.
    let transparent_value: u64 = pczt.transparent().inputs().iter().map(|i| *i.value()).sum();

    Ok(serde_json::json!({
        "transparent": pczt.transparent().inputs().len(),
        "transparentValue": transparent_value,
        "sapling": pczt.sapling().spends().len(),
        "orchard": pczt.orchard().actions().len(),
        "ironwood": pczt.ironwood().actions().len(),
        "orchardValueSum": sum(pczt.orchard()),
        "ironwoodValueSum": sum(pczt.ironwood()),
    })
    .to_string())
}

/// Self-check: opens the database, runs migrations, and reports table / view / index counts.
#[wasm_bindgen(js_name = diagSelfCheck)]
pub fn self_check(network_name: &str, db_name: &str) -> Result<String, JsValue> {
    drop(wallet::open_and_migrate(network_name, db_name)?);
    let conn = storage::open_connection(db_name)?;

    let count_of = |kind: &str| -> Result<i64, JsValue> {
        conn.query_row(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = ?1",
            [kind],
            |r| r.get(0),
        )
        .map_err(|e| JsValue::from(crate::RuntimeError::from(e)))
    };

    Ok(serde_json::json!({
        "tables": count_of("table")?,
        "views": count_of("view")?,
        "indexes": count_of("index")?,
        "versions": crate::dependency_versions(),
    })
    .to_string())
}

/// Builds only public circuit parameters without opening wallet storage.
#[wasm_bindgen(js_name = prewarmPczt)]
pub fn prewarm_pczt(network_name: &str, target_height: u32) -> Result<(), JsValue> {
    install_panic_hook();
    Ok(send::prewarm_pczt(
        &wallet::parse_network(network_name)?,
        target_height.into(),
    )?)
}

/// Proves a locally constructed PCZT without opening a wallet or initializing storage.
#[wasm_bindgen(js_name = pcztProveAtHeight)]
pub fn pczt_prove_at_height(target_height: u32, pczt_bytes: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    install_panic_hook();
    Ok(send::prove_pczt_at_height(
        &zcash_protocol::consensus::Network::MainNetwork,
        target_height.into(),
        &pczt_bytes,
    )?)
}

/// Validates and extracts a signed PCZT without persisting wallet state.
#[wasm_bindgen(js_name = pcztExtractStateless)]
pub fn pczt_extract_stateless(pczt_bytes: Vec<u8>) -> Result<String, JsValue> {
    install_panic_hook();
    let failure = |error: String| RuntimeError::new(ErrorCode::PcztError).detail(error);
    let pczt = pczt::Pczt::parse(&pczt_bytes).map_err(|e| failure(format!("{e:?}")))?;
    let tx = send::extract_pczt(pczt)?;
    let mut bytes = Vec::new();
    tx.write(&mut bytes)
        .map_err(|e| failure(format!("{e:?}")))?;
    Ok(
        serde_json::json!({ "rawTx": hex::encode(bytes), "txid": tx.txid().to_string() })
            .to_string(),
    )
}
