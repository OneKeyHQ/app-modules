//! Host-facing key API. No seed or mnemonic crosses this boundary; spending keys only enter, never leave.

use wasm_bindgen::prelude::*;
use zeroize::Zeroizing;

use crate::{account_keys, keys, review, sign, transparent_prevouts, transparent_send};

#[wasm_bindgen(js_name = transactionPolicy)]
pub fn transaction_policy(target_height: u32) -> Result<String, JsValue> {
    serde_json::to_string(&onekey_zcash_send_policy::transaction_policy(
        &zcash_protocol::consensus::MAIN_NETWORK,
        zcash_protocol::consensus::BlockHeight::from_u32(target_height),
    ))
    .map_err(|error| JsValue::from_str(&error.to_string()))
}

#[wasm_bindgen(js_name = inspectTransaction)]
pub fn inspect_transaction(request_json: &str) -> Result<String, JsValue> {
    Ok(crate::transaction_history::inspect_json(request_json)?)
}

#[wasm_bindgen(js_name = validateAddress)]
pub fn validate_address(network: &str, address: &str) -> Result<bool, JsValue> {
    Ok(keys::validate_address(network, address)?)
}

/// `receiverPolicy` (`"all"` / `"orchard"`) has no default: a transparent receiver links transparent and shielded activity.
#[wasm_bindgen(js_name = unifiedAddress)]
pub fn unified_address(
    network: &str,
    ufvk: &str,
    receiver_policy: &str,
) -> Result<String, JsValue> {
    Ok(keys::unified_address_with(network, ufvk, receiver_policy)?)
}

/// Mainnet UFVK from the 32-byte Orchard account spending key and the transparent account xpub.
#[wasm_bindgen(js_name = ufvkFromAccountKeys)]
pub fn ufvk_from_account_keys(
    network: &str,
    orchard_sk: Vec<u8>,
    account_xpub: &str,
    account_index: u32,
) -> Result<String, JsValue> {
    // Wipe the original bindgen allocation, including on validation errors; a copy would leave it in freed memory.
    let orchard_sk = Zeroizing::new(orchard_sk);
    Ok(account_keys::ufvk_from_account_keys(
        network,
        &orchard_sk,
        account_xpub,
        account_index,
    )?)
}

/// Signs only after checking the keys control the reviewed UFVK and the PCZT matches the reviewed
/// payment. The caller wipes its own copies; this side cannot wipe the JS heap.
#[wasm_bindgen(js_name = pcztSignWithAccountKeys)]
pub fn pczt_sign_with_account_keys(
    network: &str,
    orchard_sk: Vec<u8>,
    account_xprv: String,
    account_index: u32,
    pczt_bytes: Vec<u8>,
    review_json: &str,
) -> Result<Vec<u8>, JsValue> {
    let orchard_sk = Zeroizing::new(orchard_sk);
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(account_keys::sign_reviewed_pczt(
        network,
        &orchard_sk,
        &account_xprv,
        account_index,
        &pczt_bytes,
        review_json,
    )?)
}

/// PCZT copy for a hardware signer; keep the original for combining.
#[wasm_bindgen(js_name = pcztRedactForHardwareSigner)]
pub fn pczt_redact_for_hardware_signer(pczt_bytes: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(sign::redact_for_hardware_signer(&pczt_bytes)?)
}

#[wasm_bindgen(js_name = pcztVerifyReview)]
pub fn pczt_verify_review(pczt_bytes: Vec<u8>, review_json: &str) -> Result<(), JsValue> {
    Ok(review::verify_json(&pczt_bytes, review_json)?)
}

#[wasm_bindgen(js_name = transparentAddressFromUfvk)]
pub fn transparent_address_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_address(network, ufvk)?)
}

/// Transparent account pubkey (chain code || compressed key, hex). See `keys::transparent_account_pubkey`.
#[wasm_bindgen(js_name = transparentAccountPubKeyFromUfvk)]
pub fn transparent_account_pubkey_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_account_pubkey(network, ufvk)?)
}

#[wasm_bindgen(js_name = parseTransparentTransactions)]
pub fn parse_transparent_transactions(request_json: &str) -> Result<String, JsValue> {
    Ok(transparent_prevouts::parse_json(request_json)?)
}

#[wasm_bindgen(js_name = transparentTxQuote)]
pub fn transparent_tx_quote(request_json: &str) -> Result<String, JsValue> {
    Ok(transparent_send::quote_json(request_json)?)
}

#[wasm_bindgen(js_name = transparentTxBuildWithAccountXprv)]
pub fn transparent_tx_build_with_account_xprv(
    request_json: &str,
    account_xprv: String,
) -> Result<String, JsValue> {
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(transparent_send::build_with_account_xprv_json(
        request_json,
        &account_xprv,
    )?)
}

/// What the keys side can sign; a bundle is usable only if the runtime's `capabilities()` also supports it.
#[wasm_bindgen(js_name = keysCapabilities)]
pub fn keys_capabilities() -> String {
    serde_json::json!({
        "sign": {
            "orchard": sign::SIGN_ORCHARD,
            "sapling": sign::SIGN_SAPLING,
            "transparent": sign::SIGN_TRANSPARENT,
            "ironwood": sign::SIGN_IRONWOOD,
        },
        "accountKeys": {
            "network": "main",
            "ufvk": true,
            "pcztSign": true,
        },
        "transparentTx": {
            "network": "main",
            "version": 6,
            "quote": true,
            "buildWithAccountXprv": true,
            "createWithAccountXpub": true,
            "combineHardwareSigned": true,
        },
        "transparentShield": {
            "createWithAccountXprv": transparent_send::shielding::SHIELD_CREATE_WITH_ACCOUNT_XPRV,
            "signWithAccountXprv": transparent_send::shielding::SHIELD_SIGN_WITH_ACCOUNT_XPRV,
        },
    })
    .to_string()
}

/// `ufvk` (this account's) seals Ironwood outputs to its Orchard external OVK so the sender can
/// recover them; without it they are unrecoverable by the sender. The same key goes to the finalizing call.
#[wasm_bindgen(js_name = transparentTxCreateWithAccountXpub)]
pub fn transparent_tx_create_with_account_xpub(
    request_json: &str,
    account_xpub: &str,
    seed_fingerprint_hex: &str,
    ufvk: Option<String>,
) -> Result<Vec<u8>, JsValue> {
    Ok(transparent_send::shielding::create_with_account_xpub(
        request_json,
        account_xpub,
        seed_fingerprint_hex,
        ufvk.as_deref(),
    )?)
}

/// With `ufvk`, every Ironwood output must be recoverable with its OVK.
#[wasm_bindgen(js_name = transparentTxCombineHardwareSigned)]
pub fn transparent_tx_combine_hardware_signed(
    request_json: &str,
    account_xpub: &str,
    original: Vec<u8>,
    signed: Vec<u8>,
    ufvk: Option<String>,
) -> Result<Vec<u8>, JsValue> {
    Ok(transparent_send::shielding::combine_hardware_signed(
        request_json,
        account_xpub,
        &original,
        &signed,
        ufvk.as_deref(),
    )?)
}

#[wasm_bindgen(js_name = keysDependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// `ufvk` as in `transparentTxCreateWithAccountXpub`; pass the same key to the signing call.
#[wasm_bindgen(js_name = transparentShieldCreateWithAccountXprv)]
pub fn transparent_shield_create_with_account_xprv(
    request_json: &str,
    account_xprv: String,
    ufvk: Option<String>,
) -> Result<Vec<u8>, JsValue> {
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(transparent_send::shielding::create_with_account_xprv(
        request_json,
        &account_xprv,
        ufvk.as_deref(),
    )?)
}

#[wasm_bindgen(js_name = transparentShieldSignWithAccountXprv)]
pub fn transparent_shield_sign_with_account_xprv(
    request_json: &str,
    account_xprv: String,
    original: Vec<u8>,
    pczt: Vec<u8>,
    ufvk: Option<String>,
) -> Result<Vec<u8>, JsValue> {
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(transparent_send::shielding::sign_with_account_xprv(
        request_json,
        &account_xprv,
        &original,
        &pczt,
        ufvk.as_deref(),
    )?)
}
