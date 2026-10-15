//! Host-facing key API.
//!
//! Seeds and spending keys never cross this boundary outward.

use wasm_bindgen::prelude::*;
use zeroize::Zeroizing;

use crate::{keys, review, sign, transparent_prevouts, transparent_send};

/// Check recipient encoding, network, and supported receivers locally.
#[wasm_bindgen(js_name = validateAddress)]
pub fn validate_address(network: &str, address: &str) -> Result<bool, JsValue> {
    Ok(keys::validate_address(network, address)?)
}

/// Derive the Unified Address from a UFVK. Public material only.
///
/// `receiverPolicy` selects the receivers: `"all"` / `"shielded"` / `"orchard"`.
/// It is a host privacy decision with no default: a transparent receiver links
/// the user's transparent and shielded activity.
#[wasm_bindgen(js_name = unifiedAddress)]
pub fn unified_address(
    network: &str,
    ufvk: &str,
    receiver_policy: &str,
) -> Result<String, JsValue> {
    Ok(keys::unified_address_with(network, ufvk, receiver_policy)?)
}

/// Derive a UFVK from seed bytes. This boundary never accepts a mnemonic.
#[wasm_bindgen(js_name = ufvkFromSeed)]
pub fn ufvk_from_seed(network: &str, seed: Vec<u8>, account_index: u32) -> Result<String, JsValue> {
    // Own and wipe the original bindgen allocation, including on validation errors.
    // Wiping another copy would leave the JS-to-WASM input in freed linear memory.
    let seed = Zeroizing::new(seed);
    let params = keys::parse_network(network)?;
    Ok(keys::ufvk_from_seed_bytes(params, &seed, account_index)?)
}

/// Sign a PCZT with seed bytes after verifying it against the reviewed payment.
///
/// Spending keys are derived in Rust and never returned. The caller wipes its
/// own seed copy; this side cannot wipe the JS heap.
#[wasm_bindgen(js_name = pcztSignWithSeed)]
pub fn pczt_sign_with_seed(
    network: &str,
    seed: Vec<u8>,
    account_index: u32,
    pczt_bytes: Vec<u8>,
    review_json: &str,
) -> Result<Vec<u8>, JsValue> {
    let seed = Zeroizing::new(seed);
    let params = keys::parse_network(network)?;
    review::verify_signer(params, &seed, account_index, &pczt_bytes, review_json)?;
    Ok(sign::sign_pczt_with_seed(
        params,
        &seed,
        account_index,
        &pczt_bytes,
    )?)
}

/// The copy of a PCZT to send to a hardware signer; keep the original for combining.
#[wasm_bindgen(js_name = pcztRedactForHardwareSigner)]
pub fn pczt_redact_for_hardware_signer(pczt_bytes: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(sign::redact_for_hardware_signer(&pczt_bytes)?)
}

/// Verify a private payment before showing its confirmation or contacting a signer.
#[wasm_bindgen(js_name = pcztVerifyReview)]
pub fn pczt_verify_review(pczt_bytes: Vec<u8>, review_json: &str) -> Result<(), JsValue> {
    Ok(review::verify_json(&pczt_bytes, review_json)?)
}

/// Derive the transparent receiving address (t-address) from a UFVK, offline.
#[wasm_bindgen(js_name = transparentAddressFromUfvk)]
pub fn transparent_address_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_address(network, ufvk)?)
}

/// Transparent account pubkey (chain code || compressed key, hex). See `keys::transparent_account_pubkey`.
#[wasm_bindgen(js_name = transparentAccountPubKeyFromUfvk)]
pub fn transparent_account_pubkey_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_account_pubkey(network, ufvk)?)
}

/// Standard ZIP-32 seed fingerprint, persisted by the host as account identity.
#[wasm_bindgen(js_name = seedFingerprint)]
pub fn seed_fingerprint(seed: Vec<u8>) -> Result<String, JsValue> {
    let seed = Zeroizing::new(seed);
    Ok(keys::seed_fingerprint(&seed)?)
}

/// Parses and verifies previous transactions without loading wallet state.
#[wasm_bindgen(js_name = parseTransparentTransactions)]
pub fn parse_transparent_transactions(request_json: &str) -> Result<String, JsValue> {
    Ok(transparent_prevouts::parse_json(request_json)?)
}

/// Quotes a mainnet V6 transparent transaction without loading wallet state.
#[wasm_bindgen(js_name = transparentTxQuote)]
pub fn transparent_tx_quote(request_json: &str) -> Result<String, JsValue> {
    Ok(transparent_send::quote_json(request_json)?)
}

/// Builds and signs a mainnet V6 transparent transaction from a software-wallet seed.
#[wasm_bindgen(js_name = transparentTxBuildWithSeed)]
pub fn transparent_tx_build_with_seed(
    request_json: &str,
    seed: Vec<u8>,
) -> Result<String, JsValue> {
    let seed = Zeroizing::new(seed);
    Ok(transparent_send::build_with_seed_json(request_json, &seed)?)
}

/// Builds and signs using an account xprv at `m/44'/133'/account'`.
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

/// What the keys side can sign. Combine with the runtime's `capabilities()`:
/// a bundle is usable only if both proving and signing support it.
#[wasm_bindgen(js_name = keysCapabilities)]
pub fn keys_capabilities() -> String {
    serde_json::json!({
        "sign": {
            "orchard": sign::SIGN_ORCHARD,
            "sapling": sign::SIGN_SAPLING,
            "transparent": sign::SIGN_TRANSPARENT,
            "ironwood": sign::SIGN_IRONWOOD,
        },
        "transparentTx": {
            "network": "main",
            "version": 6,
            "quote": true,
            "buildWithSeed": true,
            "buildWithAccountXprv": true,
            "createWithAccountXpub": true,
            "combineHardwareSigned": true,
        },
        "transparentShield": {
            "createWithSeed": transparent_send::shielding::SHIELD_CREATE_WITH_SEED,
            "createWithAccountXprv": transparent_send::shielding::SHIELD_CREATE_WITH_ACCOUNT_XPRV,
            "signWithSeed": transparent_send::shielding::SHIELD_SIGN_WITH_SEED,
            "signWithAccountXprv": transparent_send::shielding::SHIELD_SIGN_WITH_ACCOUNT_XPRV,
        },
    })
    .to_string()
}

/// Creates a device-signable PCZT from public account material without wallet storage.
#[wasm_bindgen(js_name = transparentTxCreateWithAccountXpub)]
pub fn transparent_tx_create_with_account_xpub(
    request_json: &str,
    account_xpub: &str,
    seed_fingerprint_hex: &str,
) -> Result<Vec<u8>, JsValue> {
    Ok(transparent_send::shielding::create_with_account_xpub(
        request_json,
        account_xpub,
        seed_fingerprint_hex,
    )?)
}

/// Validates and finalizes device signatures against the approved transaction.
#[wasm_bindgen(js_name = transparentTxCombineHardwareSigned)]
pub fn transparent_tx_combine_hardware_signed(
    request_json: &str,
    account_xpub: &str,
    original: Vec<u8>,
    signed: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    Ok(transparent_send::shielding::combine_hardware_signed(
        request_json,
        account_xpub,
        &original,
        &signed,
    )?)
}

/// Pinned upstream dependency versions.
#[wasm_bindgen(js_name = keysDependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// Creates a shielding PCZT using only host-supplied transparent inputs.
#[wasm_bindgen(js_name = transparentShieldCreateWithSeed)]
pub fn transparent_shield_create_with_seed(
    request_json: &str,
    seed: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    let seed = Zeroizing::new(seed);
    Ok(transparent_send::shielding::create(request_json, &seed)?)
}

/// Signs the locally created, proved shielding PCZT.
#[wasm_bindgen(js_name = transparentShieldSignWithSeed)]
pub fn transparent_shield_sign_with_seed(
    request_json: &str,
    seed: Vec<u8>,
    original: Vec<u8>,
    pczt: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    let seed = Zeroizing::new(seed);
    Ok(transparent_send::shielding::sign(
        request_json,
        &seed,
        &original,
        &pczt,
    )?)
}

/// Creates a shielding PCZT with an account-level transparent key.
#[wasm_bindgen(js_name = transparentShieldCreateWithAccountXprv)]
pub fn transparent_shield_create_with_account_xprv(
    request_json: &str,
    account_xprv: String,
) -> Result<Vec<u8>, JsValue> {
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(transparent_send::shielding::create_with_account_xprv(
        request_json,
        &account_xprv,
    )?)
}

/// Signs a proved shielding PCZT with an account-level transparent key.
#[wasm_bindgen(js_name = transparentShieldSignWithAccountXprv)]
pub fn transparent_shield_sign_with_account_xprv(
    request_json: &str,
    account_xprv: String,
    original: Vec<u8>,
    pczt: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    let account_xprv = Zeroizing::new(account_xprv);
    Ok(transparent_send::shielding::sign_with_account_xprv(
        request_json,
        &account_xprv,
        &original,
        &pczt,
    )?)
}
