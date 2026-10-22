//! Host-facing wasm interface.
//!
//! ## Conventions
//!
//! - Every return value is a JSON string for the host to `JSON.parse`. Errors are
//!   JSON too (`{ code, params, detail }`), so the host needs no separate Error path.
//! - Amounts are always strings: piconero values exceed JS `Number.MAX_SAFE_INTEGER`,
//!   and numbers would silently lose precision.
//! - Key material goes in, never out: the BIP-32 key and the view key may be
//!   passed in but are never returned. The spend key is reduced from the BIP-32
//!   key inside each export that needs it, so it never crosses this boundary.
//! - Secret parameters are owned `String`s wrapped in `Zeroizing`, wiping the
//!   bindgen allocation itself; a `&str` would leave the input in freed memory.
//!
//! ## No scheduling here
//!
//! When to scan, how much, and how to back off on failure belong to the host. Each
//! export is one complete round trip and never loops.

use wasm_bindgen::prelude::*;

use crate::error::{ErrorCode, Result, RuntimeError};
use zeroize::Zeroizing;

use crate::{keys, query, runtime, spend, storage, sync, wallet};

fn json(value: serde_json::Value) -> std::result::Result<String, JsValue> {
    Ok(value.to_string())
}

/// Install persistent storage. Must be called once before opening any DB.
#[wasm_bindgen(js_name = init)]
pub async fn init() -> std::result::Result<(), JsValue> {
    storage::install_persistent_vfs().await?;
    Ok(())
}

/// Preserve a failed call's result, then retire its poisoned storage owner.
#[wasm_bindgen(js_name = storageRequiresWorkerRestart)]
pub fn storage_requires_worker_restart() -> bool {
    storage::requires_worker_restart()
}

/// Upstream versions pinned by this runtime, for host self-checks and logs.
#[wasm_bindgen(js_name = dependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// Open (or create) a wallet DB.
#[wasm_bindgen(js_name = openWallet)]
pub fn open_wallet(db_name: &str, network: &str) -> std::result::Result<String, JsValue> {
    let net = keys::parse_network(network)?;
    let db = wallet::Db::open(db_name, net)?;
    runtime::set_db(db, db_name)?;
    json(serde_json::json!({ "dbName": db_name, "network": network }))
}

#[wasm_bindgen(js_name = closeWallet)]
pub fn close_wallet() -> std::result::Result<(), JsValue> {
    runtime::close()?;
    Ok(())
}

/// Set the node URL; an empty string disables it. No built-in default: one would
/// decide for the user who sees their IP and queries.
///
/// The old node is dropped before connecting the new one: a replaced or disabled
/// node must receive no further requests, even if the new one fails to connect.
/// If calls overlap, the last one wins.
#[wasm_bindgen(js_name = setNodeUrl)]
pub async fn set_node_url(url: &str) -> std::result::Result<(), JsValue> {
    runtime::select_node(url).await?;
    Ok(())
}

/// Fetch the chain tip. Does not require an open wallet DB: account creation needs
/// it for the birthday before the DB exists.
///
/// Returns `{ height, network }`, where `network` is the node's chain (by genesis
/// block). There is no wallet DB to compare against yet, so the host must check it
/// matches the wallet being created before setting the birthday.
#[wasm_bindgen(js_name = chainTip)]
pub async fn chain_tip() -> std::result::Result<String, JsValue> {
    let tip: u64 = runtime::with_daemon(|daemon| async move {
        let r = sync::chain_tip(&daemon).await;
        (daemon, r)
    })
    .await?;
    json(serde_json::json!({
        "height": tip,
        "network": runtime::daemon_network().map(crate::keys::network_name),
    }))
}

/// Register an identity (view key pair).
///
/// `spendPublicKeyHex` and `viewPrivateKeyHex` are 32-byte hex. No spend secret
/// is taken here; see `signTransactionWithBip32Key`.
#[wasm_bindgen(js_name = addAccount)]
pub fn add_account(
    uuid: &str,
    spend_public_key_hex: &str,
    view_private_key_hex: String,
    birthday_height: u64,
) -> std::result::Result<String, JsValue> {
    let view_private_key_hex = Zeroizing::new(view_private_key_hex);
    let spend = decode_hex(spend_public_key_hex, "spendPublicKeyHex")?;
    let view = decode_secret_hex(&view_private_key_hex, "viewPrivateKeyHex")?;
    let result = runtime::with_db(|db| {
        let identity = db.add_identity(uuid, &spend, &view, birthday_height)?;
        Ok(serde_json::json!({
            "accountId": identity.id,
            "uuid": identity.uuid,
            "primaryAddress": identity.primary_address,
            "birthdayHeight": identity.birthday_height,
        }))
    })?;
    json(result)
}

#[wasm_bindgen(js_name = listAccounts)]
pub fn list_accounts() -> std::result::Result<String, JsValue> {
    let rows = runtime::with_db_read(|db| db.list_identities())?;
    let items: Vec<_> = rows
        .into_iter()
        .map(|(id, uuid, address)| {
            serde_json::json!({ "accountId": id, "uuid": uuid, "primaryAddress": address })
        })
        .collect();
    json(serde_json::json!({ "items": items }))
}

/// Delete an identity and all its data. Irreversible.
#[wasm_bindgen(js_name = removeAccount)]
pub fn remove_account(uuid: &str) -> std::result::Result<String, JsValue> {
    let removed = runtime::with_db(|db| {
        let removed = db.remove_identity(uuid)?;
        db.reclaim_storage(storage::RECLAIM_PAGES_AFTER_DELETE);
        Ok(removed)
    })?;
    json(serde_json::json!({ "removed": removed }))
}

/// Delete the whole wallet DB file (app reset, storage rebuild). Irreversible.
///
/// Does not open the DB, so unsupported-version or corrupt DBs can still be deleted.
/// Closes the DB first if it is the one currently open.
#[wasm_bindgen(js_name = deleteWallet)]
pub async fn delete_wallet(db_name: String) -> std::result::Result<String, JsValue> {
    if runtime::open_db_name().as_deref() == Some(db_name.as_str()) {
        runtime::close()?;
    }
    let deleted = storage::delete_database(&db_name).await?;
    json(serde_json::json!({ "deleted": deleted }))
}

/// Register a subaddress and return its address. Funds to unregistered subaddresses go undetected.
#[wasm_bindgen(js_name = registerSubaddress)]
pub fn register_subaddress(
    account_id: i64,
    major: u32,
    minor: u32,
    label: Option<String>,
) -> std::result::Result<String, JsValue> {
    let address =
        runtime::with_db(|db| db.register_subaddress(account_id, major, minor, label.as_deref()))?;
    json(serde_json::json!({ "address": address, "major": major, "minor": minor }))
}

/// Tell an identity the chain tip and queue its pending scan ranges.
#[wasm_bindgen(js_name = updateChainTip)]
pub fn update_chain_tip(account_id: i64, tip: u64) -> std::result::Result<String, JsValue> {
    runtime::with_db(|db| sync::update_chain_tip(db, account_id, tip))?;
    let progress = runtime::with_db_read(|db| sync::peek(db, account_id))?;
    json(progress)
}

/// One bounded step: scan at most `maxBlocks` blocks and return progress.
///
/// Never loops to completion; the host decides whether to call again.
#[wasm_bindgen(js_name = syncStep)]
pub async fn sync_step(account_id: i64, max_blocks: u64) -> std::result::Result<String, JsValue> {
    let progress = runtime::with_db_and_daemon(|mut db, daemon| async move {
        let r = sync::step(&mut db, &daemon, account_id, max_blocks).await;
        if r.is_ok() {
            db.reclaim_storage(storage::RECLAIM_PAGES_PER_STEP);
        }
        (db, daemon, r)
    })
    .await?;
    json(progress)
}

/// Read-only scan progress; no network requests. Shows which ranges sync intends to scan.
#[wasm_bindgen(js_name = syncPeek)]
pub fn sync_peek(account_id: i64) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| sync::peek(db, account_id))?;
    json(v)
}

/// Reorg recovery: rewind to `height` and requeue everything above it. Destructive.
#[wasm_bindgen(js_name = rewindTo)]
pub fn rewind_to(account_id: i64, height: u64, tip: u64) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db(|db| {
        let v = sync::rewind_and_requeue(db, account_id, height, tip)?;
        db.reclaim_storage(storage::RECLAIM_PAGES_AFTER_DELETE);
        Ok(v)
    })?;
    json(v)
}

/// Balance. `chainTip` = `null` means the tip is unknown; spendable is then reported
/// as 0 rather than invite a spend that cannot be built.
#[wasm_bindgen(js_name = balance)]
pub fn balance(account_id: i64, chain_tip: Option<u64>) -> std::result::Result<String, JsValue> {
    let b = runtime::with_db_read(|db| query::balance(db, account_id, chain_tip))?;
    json(b.to_json())
}

#[wasm_bindgen(js_name = history)]
pub fn history(account_id: i64, limit: u32, offset: u32) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| query::history(db, account_id, limit, offset))?;
    json(v)
}

#[wasm_bindgen(js_name = transactionDetails)]
pub fn transaction_details(
    account_id: i64,
    txid_hex: &str,
) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| query::transaction_details(db, account_id, txid_hex))?;
    json(v)
}

/// Import key images so spend detection works.
///
/// Call as early as possible after unlock. Until then the balance is too high: a
/// view-only wallet cannot detect spends, so spent outputs still look unspent. This
/// only affects blocks scanned afterwards; earlier spends need a rewind and rescan.
///
/// Takes the account's BIP-32 key (as `deriveAccount`); the spend key is reduced
/// here and wiped after use.
#[wasm_bindgen(js_name = importKeyImagesWithBip32Key)]
pub fn import_key_images_with_bip32_key(
    account_id: i64,
    bip32_private_key_hex: String,
) -> std::result::Result<String, JsValue> {
    let bip32_private_key_hex = Zeroizing::new(bip32_private_key_hex);
    let raw = decode_secret_hex(&bip32_private_key_hex, "bip32PrivateKeyHex")?;
    let result =
        runtime::with_db(|db| spend::import_key_images_with_bip32_key(db, account_id, &raw))?;
    json(serde_json::json!({
        "imported": result.imported,
        "spendsFound": result.spends_found,
    }))
}

/// Derive Monero view material and the primary address from a BIP-32-derived private key.
///
/// Pure derivation: no DB, no node, so an address can exist before any scanning.
///
/// Matches OneKey V4 (`spend = sc_reduce32(key)`, `view = keccak(spend)`). Changing
/// it would show existing users an empty wallet after importing the same mnemonic.
///
/// Does not return the spend private key.
#[wasm_bindgen(js_name = deriveAccount)]
pub fn derive_account(
    network: &str,
    bip32_private_key_hex: String,
) -> std::result::Result<String, JsValue> {
    let bip32_private_key_hex = Zeroizing::new(bip32_private_key_hex);
    let net = keys::parse_network(network)?;
    let raw = decode_secret_hex(&bip32_private_key_hex, "bip32PrivateKeyHex")?;
    let derived = keys::derive_from_bip32_key(net, &raw)?;
    json(serde_json::json!({
        "primaryAddress": derived.primary_address,
        "spendPublicKeyHex": hex::encode(derived.spend_public_key),
        "viewPrivateKeyHex": hex::encode(derived.view_private_key),
    }))
}

/// Derive a subaddress. Pure derivation; no DB.
#[wasm_bindgen(js_name = deriveSubaddress)]
pub fn derive_subaddress(
    network: &str,
    spend_public_key_hex: &str,
    view_private_key_hex: String,
    major: u32,
    minor: u32,
) -> std::result::Result<String, JsValue> {
    let view_private_key_hex = Zeroizing::new(view_private_key_hex);
    let net = keys::parse_network(network)?;
    let spend = decode_hex(spend_public_key_hex, "spendPublicKeyHex")?;
    let view = decode_secret_hex(&view_private_key_hex, "viewPrivateKeyHex")?;
    let pair = keys::view_pair_from_bytes(&spend, &view)?;
    let address = keys::subaddress(&pair, net, major, minor)?;
    json(serde_json::json!({ "address": address, "major": major, "minor": minor }))
}

/// Check that a recipient address is valid and belongs to this network.
#[wasm_bindgen(js_name = validateAddress)]
pub fn validate_address(network: &str, address: &str) -> std::result::Result<bool, JsValue> {
    let net = keys::parse_network(network)?;
    Ok(keys::validate_address(net, address))
}

/// Hand network requests to the host instead of `fetch`ing in wasm.
///
/// `handler` is `(url, contentType, body: Uint8Array) => Promise<Uint8Array>`;
/// `undefined` uninstalls it.
///
/// Lets nodes that in-wasm fetch cannot reach (mixed content, no CORS headers) be
/// used through the extension background, desktop main process or mobile native code.
#[wasm_bindgen(js_name = setRequestHandler)]
pub fn set_request_handler(handler: Option<js_sys::Function>) {
    crate::network::set_host_handler(handler);
}

/// Diagnostics: DB health, per-table row counts, logical size. No network requests.
#[wasm_bindgen(js_name = diagDatabase)]
pub fn diag_database() -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| {
        let mut counts = serde_json::Map::new();
        for table in [
            "identities",
            "subaddresses",
            "outputs",
            "transactions",
            "blocks",
            "scan_ranges",
            "possible_spends",
        ] {
            let n: i64 = db
                .conn()
                .query_row(&format!("SELECT count(*) FROM {table}"), [], |r| r.get(0))
                .map_err(|e| crate::error::db_err("diagCount", e))?;
            counts.insert(table.to_owned(), serde_json::json!(n));
        }
        // Logical size (pages x page size); the backing store's physical footprint
        // can differ (free pages, storage-layer overhead).
        let pragma = |name: &str| -> Result<i64> {
            db.conn()
                .query_row(&format!("PRAGMA {name}"), [], |r| r.get(0))
                .map_err(|e| crate::error::db_err("diagPragma", e))
        };
        let page_size = pragma("page_size")?;
        let page_count = pragma("page_count")?;
        Ok(serde_json::json!({
            "dbName": runtime::open_db_name(),
            "counts": counts,
            "pageSize": page_size,
            "pageCount": page_count,
            "logicalBytes": page_size * page_count,
        }))
    })?;
    json(v)
}

fn decode_hex(value: &str, field: &str) -> Result<Vec<u8>> {
    hex::decode(value).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            serde_json::json!({ "field": field, "reason": "notHex" }),
        )
    })
}

/// [`decode_hex`] into a buffer that is wiped on drop, including the bytes
/// already decoded when the input turns out to be malformed.
fn decode_secret_hex(value: &str, field: &str) -> Result<Zeroizing<Vec<u8>>> {
    let mut out = Zeroizing::new(vec![0u8; value.len() / 2]);
    hex::decode_to_slice(value, out.as_mut_slice()).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            serde_json::json!({ "field": field, "reason": "notHex" }),
        )
    })?;
    Ok(out)
}

// ── Spend path ─────────────────────────────────────────────────────────────

/// Select inputs and build. Needs no spend private key; does not broadcast.
///
/// `recipientsJson` is `[{ "address": "...", "amountAtomic": "1000000000000" }]`.
///
/// With `maxSend`, exactly one recipient is accepted and its amount is computed as
/// spendable total minus fee; the passed amount is ignored. Merely omitting change
/// would fold the remainder into the fee.
///
/// Returns `{ signableHex, feeAtomic, amountAtomic, changeAtomic, inputsAtomic,
/// inputCount }`. `feeAtomic` and `amountAtomic` are what the
/// transaction will use: the confirmation page shows them and signing keeps them.
///
/// Inputs are not reserved: a second prepare before broadcast may select the same
/// ones, and broadcast then refuses the later transaction as `inputAlreadySpent`.
#[wasm_bindgen(js_name = prepareTransaction)]
pub async fn prepare_transaction(
    account_id: i64,
    recipients_json: &str,
    max_send: bool,
    chain_tip: u64,
    priority: &str,
) -> std::result::Result<String, JsValue> {
    let recipients: Vec<RecipientJson> = serde_json::from_str(recipients_json).map_err(|e| {
        JsValue::from(
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                serde_json::json!({ "field": "recipientsJson" }),
            )
            .detail(e),
        )
    })?;
    if recipients.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            serde_json::json!({ "field": "recipientsJson", "reason": "empty" }),
        )
        .into());
    }

    let fee_priority = spend::parse_priority(priority)?;

    let v = runtime::with_db_and_daemon(move |db, daemon| async move {
        let r = spend::prepare_for_host(
            &db,
            &daemon,
            account_id,
            &recipients
                .iter()
                .map(|r| (r.address.as_str(), r.amount_atomic.as_str()))
                .collect::<Vec<_>>(),
            max_send,
            chain_tip,
            fee_priority,
        )
        .await;
        (db, daemon, r)
    })
    .await?;

    json(v)
}

/// Sign a prepared transaction. No network, no DB, no broadcast.
///
/// Takes the account's BIP-32 key (as `deriveAccount`); the spend key is reduced
/// here, and both are wiped after use, never stored or returned.
/// Returns `{ txid, rawHex, sizeBytes }`; the txid is fixed at signing, so the host
/// can record the pending transaction before broadcasting.
#[wasm_bindgen(js_name = signTransactionWithBip32Key)]
pub fn sign_transaction_with_bip32_key(
    signable_hex: &str,
    bip32_private_key_hex: String,
    review_json: &str,
) -> std::result::Result<String, JsValue> {
    let bip32_private_key_hex = Zeroizing::new(bip32_private_key_hex);
    let raw = decode_secret_hex(&bip32_private_key_hex, "bip32PrivateKeyHex")?;
    json(spend::sign_for_host_with_bip32_key(
        signable_hex,
        &raw,
        review_json,
    )?)
}

/// Validate actual payment effects before the host presents its confirmation.
#[wasm_bindgen(js_name = verifyTransaction)]
pub fn verify_transaction(
    signable_hex: &str,
    review_json: &str,
) -> std::result::Result<(), JsValue> {
    let request = crate::review::parse_request(review_json)?;
    crate::review::verify(signable_hex, &request, None)?;
    Ok(())
}

#[wasm_bindgen(js_name = verifySignedTransaction)]
pub fn verify_signed_transaction(
    signable_hex: &str,
    review_json: &str,
    raw_hex: &str,
) -> std::result::Result<String, JsValue> {
    let request = crate::review::parse_request(review_json)?;
    let transaction = crate::review::verify_signed(signable_hex, &request, raw_hex)?;
    Ok(hex::encode(transaction.hash()))
}

/// Broadcast a signed transaction. Irreversible.
///
/// Accepted and uncertain (network error) outcomes are both recorded: spent outputs
/// are marked, the transaction goes in flight, and `broadcast: accepted | unknown` is
/// returned. Only an explicit node rejection errors and releases the inputs.
#[wasm_bindgen(js_name = broadcastTransaction)]
pub async fn broadcast_transaction(
    account_id: i64,
    raw_hex: &str,
    chain_tip: u64,
    outgoing_atomic: Option<String>,
    change_atomic: Option<String>,
    signable_hex: Option<String>,
    review_json: Option<String>,
) -> std::result::Result<String, JsValue> {
    let raw = raw_hex.to_owned();
    let required = |value: Option<String>, field: &str| {
        value.ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                serde_json::json!({"field": field, "reason": "reviewRequired"}),
            )
        })
    };
    let request = crate::review::parse_request(&required(review_json, "reviewJson")?)?;
    let amounts = spend::reviewed_broadcast_amounts(
        &required(signable_hex, "signableHex")?,
        &request,
        &raw,
        outgoing_atomic.as_deref(),
        change_atomic.as_deref(),
    )?;
    let v = runtime::with_db_and_daemon(move |db, daemon| async move {
        let r = async {
            let identity = db.identity_by_id(account_id)?;
            crate::review::verify_sender(&request, &identity.view_pair, db.network())?;
            spend::broadcast_signed(&db, &daemon, account_id, &raw, chain_tip, amounts).await
        }
        .await;
        (db, daemon, r)
    })
    .await?;
    json(v)
}

#[derive(serde::Deserialize)]
struct RecipientJson {
    address: String,
    #[serde(rename = "amountAtomic")]
    amount_atomic: String,
}
