//! Host-facing JS/TS interface.
//!
//! - Parameters and return values are only string / number / boolean / JSON string.
//! - A thrown `Error` carries `code` / `params` / `detail`; see `error`.
//! - Every call is a complete round trip: no background tasks, no timers.

// `diag*` methods are not a stable contract; all others are, and changes require a version bump.

use wasm_bindgen::prelude::*;

use crate::{
    account,
    error::{ErrorCode, RuntimeError},
    history, network, runtime, send, storage, sync, tx_state, wallet,
};

/// Inject a Fetch-compatible host transport; None retains browser Fetch.
#[wasm_bindgen(js_name = setRequestHandler)]
pub fn set_request_handler(handler: Option<js_sys::Function>) {
    network::set_request_handler(handler);
}

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
/// Must be installed at every entry point.
fn install_panic_hook() {
    console_error_panic_hook::set_once();
}

/// The capabilities of this version-locked wallet and keys package set.
///
/// Lets the host hide unsupported actions up front instead of failing at proving time.
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
        // Proposals and proofs only; the keys package signs the inputs.
        "proveShielding": true,
        "notes": {
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

/// Opens a page-backed database and applies the official SQLite migrations.
#[wasm_bindgen(js_name = openWallet)]
pub async fn open_wallet(network_name: String, db_name: String) -> Result<String, JsValue> {
    let (network_name, db_name) = (network_name.as_str(), db_name.as_str());
    let db = wallet::open_and_migrate(network_name, db_name)?;
    // WALLET_BUSY must propagate, or the caller assumes the database was swapped.
    runtime::set_db(db, db_name)?;
    Ok(serde_json::json!({ "dbName": db_name, "network": network_name }).to_string())
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
/// The endpoint must support gRPC-web. CORS is required only without a host transport.
#[wasm_bindgen(js_name = setLightwalletdUrl)]
pub fn set_lightwalletd_url(base_url: &str) -> Result<(), JsValue> {
    runtime::set_client(network::connect(base_url)?);
    Ok(())
}

/// Current chain tip height. Requires only `setLightwalletdUrl`, not an open wallet.
///
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
/// Probes must not replace the wallet's global client while a sync has borrowed it.
#[wasm_bindgen(js_name = chainTipAt)]
pub async fn chain_tip_at(base_url: String) -> Result<u32, JsValue> {
    let mut client = network::connect(&base_url)?;
    Ok(network::chain_tip(&mut client).await?)
}

/// Imports a view-only account (UFVK) and returns its UUID.
///
/// `birthdayHeight` is the scan start: the current tip for new accounts, an early enough
/// height for restores.
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
            let r = async {
                let derivation =
                    account::derivation_pair(seed_fingerprint_hex.as_deref(), account_index)?;
                account::import_ufvk(
                    &mut db,
                    &mut client,
                    &account_name,
                    &ufvk,
                    birthday_height,
                    derivation,
                )
                .await
            }
            .await;
            (db, client, r)
        })
        .await?,
    )
}

/// Removes one account from the shared database; other accounts and the database remain.
#[wasm_bindgen(js_name = removeAccount)]
pub async fn remove_account(account_uuid: String) -> Result<(), JsValue> {
    runtime::with_db(|db| account::remove_account(db, &account_uuid))?;
    storage::durability_barrier().await?;
    runtime::reclaim_storage(storage::RECLAIM_PAGES_AFTER_DELETE);
    Ok(())
}

/// Finds the account for a UFVK, returning `{ uuid }` or `"null"` if absent.
///
/// The only correct lookup in the shared per-network database: UUIDs change after a purge.
#[wasm_bindgen(js_name = accountByUfvk)]
pub fn account_by_ufvk(ufvk: String) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| account::account_by_ufvk(db, &ufvk))?)
}

/// Per-account sync status, derived from the account birthday and the database's remaining scan ranges.
///
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
/// `trusted` / `untrusted` are confirmation thresholds (both >= 1, trusted <= untrusted).
/// Throws `NOT_SYNCED` before any block has been synced.
#[wasm_bindgen(js_name = accountBalance)]
pub fn account_balance(
    account_uuid: String,
    trusted: u32,
    untrusted: u32,
    allow_zero_conf_shielding: bool,
) -> Result<String, JsValue> {
    let policy = account::confirmations_policy(trusted, untrusted, allow_zero_conf_shielding)?;
    Ok(runtime::with_db(|db| {
        account::balance_json(db, &account_uuid, policy)
    })?)
}

/// One-time sync preparation: downloads subtree roots for all three pools and updates the chain tip.
///
/// The slowest step of a first sync; exposed separately so the host can show a "preparing" state.
///
/// Returns `{ chainTip, saplingSubtrees, orchardSubtrees, ironwoodSubtrees }`.
#[wasm_bindgen(js_name = syncPrepare)]
pub async fn sync_prepare() -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(|mut db, mut client| async move {
            let r = sync::prepare(&mut db, &mut client).await;
            (db, client, r)
        })
        .await?,
    )
}

/// Updates only the chain tip (one unary call). Returns `{ chainTip }`.
///
/// Use every sync round; call `syncPrepare` once per open, since re-downloading the
/// ~1900 subtree roots costs several seconds on mainnet.
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
/// `{ ranges: [{ start, end, len, priority }], totalBlocks, chainTip, fullyScannedHeight }`.
#[wasm_bindgen(js_name = syncPeek)]
pub fn sync_peek() -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| sync::peek(db))?)
}

/// Quotes only: no transaction built. Returns `{ feeZat, steps }`.
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
    Ok(runtime::with_read_conn(|conn| {
        history::details(conn, &account_uuid, &txid)
    })?)
}

/// Per-output breakdown of one transaction: `{ txid, spent[], received[], external[] }`.
///
/// `transactionDetails` gives aggregates; this gives each output (value, pool, change flag,
/// memo, address).
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
/// suggestions; the host cannot pick arbitrary heights.
///
/// `activeUfvksJson` is a required JSON string array. Only those identities are used for
/// Orchard/Ironwood trial decryption. An empty array fails with `NO_ACTIVE_ACCOUNTS`; it never
/// falls back to every account stored in the runtime database.
///
/// Never loops to completion. Returns
/// `{ done, batchesDone, blocksScanned, remainingBlocks, remainingRanges, scannedFrom,
/// scannedTo, scannedPriority }`; the host decides whether to call again.
#[wasm_bindgen(js_name = syncStep)]
pub async fn sync_step(
    batch_size: u32,
    max_batches: u32,
    priority: Option<String>,
    active_ufvks_json: String,
) -> Result<String, JsValue> {
    let preferred = sync::parse_priority(priority.as_deref().unwrap_or(""))?;
    let active_ufvks: Vec<String> = serde_json::from_str(&active_ufvks_json).map_err(|e| {
        RuntimeError::new(ErrorCode::InvalidUfvk).detail(format!("activeUfvksJson: {e}"))
    })?;
    let progress = runtime::with_db_and_client(move |mut db, mut client| async move {
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
    .await?;
    runtime::reclaim_storage(storage::RECLAIM_PAGES_PER_STEP);
    Ok(progress)
}

/// Paged transaction history as a JSON array. Unmined first, then by height descending.
///
/// Returns neutral facts (value delta, change flag, note counts, timestamps), no
/// incoming/outgoing classification.
///
/// `limit` is capped at 500; above that throws `INVALID_BATCH_SIZE`.
#[wasm_bindgen(js_name = transactionHistory)]
pub fn transaction_history(
    account_uuid: String,
    limit: u32,
    offset: u32,
) -> Result<String, JsValue> {
    Ok(runtime::with_read_conn(|conn| {
        history::list(conn, &account_uuid, limit, offset)
    })?)
}

/// Rewinds the wallet to a height so later syncs rescan that range.
///
/// Destructive: scan results above the height are discarded. Returns the actual height
/// rewound to (upstream may go further back to a commitment tree checkpoint).
#[wasm_bindgen(js_name = rewindTo)]
pub fn rewind_to(height: u32) -> Result<u32, JsValue> {
    // No idle gate: refusing while a transaction is unmined would pin the account to an abandoned chain.
    let rewound = runtime::with_db(|db| sync::rewind_to(db, height))?;
    runtime::reclaim_storage(storage::RECLAIM_PAGES_AFTER_DELETE);
    Ok(rewound)
}

/// Re-queue `[fromHeight, chainTip]` as historic work without deleting cached notes or history.
///
/// Resume companion to `syncStep(activeUfvksJson)`: the host queues the range missed while an
/// identity was paused (with a reorg margin) before adding it back to the active UFVK set.
#[wasm_bindgen(js_name = queueRescanFrom)]
pub fn queue_rescan_from(from_height: u32) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| {
        sync::queue_rescan_from(db, from_height)
    })?)
}

/// Rescans one account from a new birthday; see `account::rescan_account`.
#[wasm_bindgen(js_name = rescanAccount)]
pub async fn rescan_account(account_uuid: String, from_height: u32) -> Result<String, JsValue> {
    let has_pending_send =
        !runtime::with_read_conn(|conn| tx_state::pending_txids(conn, &account_uuid))?.is_empty();
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let result = account::rescan_account(
                &mut db,
                &mut client,
                &account_uuid,
                from_height,
                has_pending_send,
            )
            .await;
            (db, client, result)
        })
        .await?,
    )
}

// Flow: pcztCreate → pcztProve → (keys package) pcztSign → pcztSubmit
//
// Proving and signing may happen in either order (they touch different PCZT parts),
// but both must finish before pcztSubmit or the transaction is invalid.

/// Builds an unproven, unsigned PCZT for a transfer and returns its bytes.
///
/// `fallbackChangePool`: must be `"ironwood"`; Orchard is spend-only after NU6.3.
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
        let (bytes, fee_zat) = send::create_pczt(
            db,
            &account_uuid,
            &to_address,
            amount_zat,
            send::parse_memo(memo.as_deref(), memo_hex.as_deref())?.as_ref(),
            policy,
        )?;
        Ok(serde_json::json!({
            "pcztHex": bytes.iter().map(|b| format!("{b:02x}")).collect::<String>(),
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
/// Hardware returns only public fields such as signatures; the Combiner takes whichever side
/// has a value. Needs no wallet database.
#[wasm_bindgen(js_name = pcztCombine)]
pub fn pczt_combine(original: Vec<u8>, signed: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(send::combine_pczt(&original, &signed)?)
}

/// Durability barrier after an irreversible step.
///
/// A failure means persistence is unconfirmed, not that the step failed; callers are past the
/// point of no return, so a generic storage error would make the host retry or show a sent tx as failed.
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
/// - `unknown`: delivery to the node is uncertain. The transaction stays pending until mined
///   or expired; the host does not resubmit it and must not treat it as failed.
#[wasm_bindgen(js_name = pcztSubmit)]
pub async fn pczt_submit(account_uuid: String, pczt_bytes: Vec<u8>) -> Result<String, JsValue> {
    let outcome = runtime::with_db_and_client(move |mut db, mut client| async move {
        let r = send::submit_signed_pczt(&mut db, &mut client, &account_uuid, &pczt_bytes).await;
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

/// Stored outgoing transactions of this account that are unmined, unexpired, and not
/// rejected by the node. Each stays pending until mined or expired; the host does not
/// resubmit them on sync.
#[wasm_bindgen(js_name = pendingBroadcastTxids)]
pub fn pending_broadcast_txids(account_uuid: String) -> Result<String, JsValue> {
    let txids = runtime::with_read_conn(|conn| tx_state::pending_txids(conn, &account_uuid))?;
    Ok(serde_json::to_string(&txids).map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::DatabaseError,
            serde_json::json!({ "operation": "serializePendingBroadcasts" }),
        )
        .detail(error)
    })?)
}

/// Resends a stored pending transaction. Irreversible. For an explicit user request only:
/// sync does not call it, and an unmined send otherwise stays pending until mined or expired.
///
/// Throws `BROADCAST_REJECTED` when the node definitively rejects it (do not retry);
/// `BROADCAST_OUTCOME_UNKNOWN` when the node errored without proving it can never be mined;
/// `NETWORK_ERROR` on transport failure. In the latter two cases the transaction stays pending
/// until mined or expired.
#[wasm_bindgen(js_name = broadcastTransaction)]
pub async fn broadcast_transaction(txid: String) -> Result<(), JsValue> {
    let result = runtime::with_db_and_client(move |mut db, mut client| async move {
        let r = match tx_state::assert_not_rejected(&mut db, &txid)
            .and_then(|_| send::raw_transaction(&db, &txid))
        {
            Ok(raw) => network::broadcast(&mut client, raw).await,
            Err(e) => Err(e),
        };
        let r = match r {
            Err(e) if e.code == crate::ErrorCode::BroadcastRejected => {
                // Return the node's rejection even if persisting it fails.
                let recorded =
                    tx_state::account_uuid_for_txid(&mut db, &txid).and_then(|account_uuid| {
                        tx_state::record_rejection(&mut db, &txid, &account_uuid, &e)
                    });
                match recorded {
                    Ok(()) => Err(e),
                    Err(persist_error) => {
                        Err(e.detail(format!("record_rejection failed: {persist_error}")))
                    }
                }
            }
            r => r,
        };
        (db, client, r)
    })
    .await;
    if let Err(rejection) = &result {
        if rejection.code == crate::ErrorCode::BroadcastRejected {
            // The rejection is recorded; it must reach the host unchanged.
            barrier_after_irreversible(serde_json::json!({
                "operation": "broadcastTransaction",
                "outcome": { "broadcast": "rejected", "rejection": rejection.to_json() },
            }))
            .await?;
        }
    }
    Ok(result?)
}

/// Deletes a wallet database file. Irreversible.
///
/// The database is a derived cache; deleting only forces a rescan.
///
/// Closes the database first if it is the one currently open.
/// Returns `true` if deleted, `false` if it did not exist.
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
/// A stable contract, not `diag*`: the host uses it for capacity guards.
///
/// `logicalBytes` is page count × page size, not physical storage usage.
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
