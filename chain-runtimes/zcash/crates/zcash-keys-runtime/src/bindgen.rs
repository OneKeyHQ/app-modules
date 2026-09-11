//! 面向宿主的密钥接口。
//!
//! 出口只有 UFVK 与地址 —— 种子和花费密钥永远不跨这条边界。

use wasm_bindgen::prelude::*;
use zeroize::Zeroizing;

use crate::{keys, sign, transparent_send};

/// 从助记词派生该账户的 UFVK。助记词与种子在 Rust 侧用完即擦。
#[wasm_bindgen(js_name = ufvkFromMnemonic)]
pub fn ufvk_from_mnemonic(
    network: &str,
    mnemonic: String,
    account_index: u32,
) -> Result<String, JsValue> {
    let mnemonic = Zeroizing::new(mnemonic);
    Ok(keys::ufvk_from_mnemonic(network, &mnemonic, account_index)?)
}

/// 从 UFVK 取统一地址。纯公开材料，不涉及私钥。
///
/// `receiverPolicy` 决定 UA 里包含哪些 receiver：`"all"`（默认）/ `"shielded"` /
/// `"orchard"`。**这是隐私策略，由宿主决定** —— 带 transparent receiver 的地址
/// 别人可以用公开方式付给你，代价是把你的透明与屏蔽活动关联起来。
#[wasm_bindgen(js_name = unifiedAddress)]
pub fn unified_address(
    network: &str,
    ufvk: &str,
    receiver_policy: Option<String>,
) -> Result<String, JsValue> {
    Ok(keys::unified_address_with(
        network,
        ufvk,
        receiver_policy.as_deref().unwrap_or("all"),
    )?)
}

/// 给 PCZT 签名。
///
/// 这是整个方案里唯一接触花费密钥的接口。助记词在 Rust 侧派生、用完即擦，
/// 种子与花费密钥不会跨这条边界返回。
///
/// 输入是 `onekey-zcash-runtime` 的 `pcztCreate` 产物，
/// 输出交回它的 `pcztSend` 终结并广播。
#[wasm_bindgen(js_name = pcztSign)]
pub fn pczt_sign(
    network: &str,
    mnemonic: String,
    account_index: u32,
    pczt_bytes: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    let mnemonic = Zeroizing::new(mnemonic);
    Ok(sign::sign_pczt_with_mnemonic(
        network,
        &mnemonic,
        account_index,
        &pczt_bytes,
    )?)
}

/// 从**种子字节**派生 UFVK。
///
/// OneKey 的架构里，助记词只在解密那一刻出现一次，之后全程传种子。
/// 所以这才是接进 app 的实际入口；`ufvkFromMnemonic` 主要用于测试与独立验证。
#[wasm_bindgen(js_name = ufvkFromSeed)]
pub fn ufvk_from_seed(network: &str, seed: Vec<u8>, account_index: u32) -> Result<String, JsValue> {
    // Own and wipe the original bindgen allocation, including on validation errors.
    // Wiping another copy would leave the JS-to-WASM input in freed linear memory.
    let seed = Zeroizing::new(seed);
    let params = keys::parse_network(network)?;
    Ok(keys::ufvk_from_seed_bytes(params, &seed, account_index)?)
}

/// 用**种子字节**给 PCZT 签名。整个方案里唯一接触花费密钥的接口之一。
///
/// 花费密钥在 Rust 侧从种子现场派生、用完即弃，不跨边界返回。
/// 调用方负责擦除自己那份种子 —— 这一侧擦不了 JS 堆上的副本。
#[wasm_bindgen(js_name = pcztSignWithSeed)]
pub fn pczt_sign_with_seed(
    network: &str,
    seed: Vec<u8>,
    account_index: u32,
    pczt_bytes: Vec<u8>,
) -> Result<Vec<u8>, JsValue> {
    let seed = Zeroizing::new(seed);
    let params = keys::parse_network(network)?;
    Ok(sign::sign_pczt_with_seed(
        params,
        &seed,
        account_index,
        &pczt_bytes,
    )?)
}

/// 从 UFVK 取透明收款地址（t 地址）。离线可算，不需要钱包库。
#[wasm_bindgen(js_name = transparentAddressFromUfvk)]
pub fn transparent_address_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_address(network, ufvk)?)
}

/// UFVK 的透明账户公钥（chain code || 压缩公钥，hex）。见 keys::transparent_account_pubkey。
#[wasm_bindgen(js_name = transparentAccountPubKeyFromUfvk)]
pub fn transparent_account_pubkey_from_ufvk(network: &str, ufvk: &str) -> Result<String, JsValue> {
    Ok(keys::transparent_account_pubkey(network, ufvk)?)
}

/// 种子指纹（ZIP-32 标准值）。宿主拿它当账户身份持久化，
/// 所以必须与标准一致 —— 不能另发明一套哈希。
#[wasm_bindgen(js_name = seedFingerprint)]
pub fn seed_fingerprint(seed: Vec<u8>) -> Result<String, JsValue> {
    let seed = Zeroizing::new(seed);
    Ok(keys::seed_fingerprint(&seed)?)
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

/// keys 侧**实际**能签什么。与 runtime 的 `capabilities()` 合并后才是完整判断：
/// 一笔交易要能发出去，证明和签名两边都得支持同一种 bundle。
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

/// 本 crate 锁定的官方依赖版本。
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
