//! PCZT 签名。
//!
//! **这是整个方案里唯一接触花费密钥的地方。**
//!
//! 构造 PCZT 需要钱包数据库（选币、witness、anchor），签名需要种子。
//! 两者分处两个 crate，所以：
//!
//! - `onekey-zcash-runtime` 里没有任何路径能拿到种子；
//! - 移动端只打这个 keys 包时，签名依然可用；
//! - 想审计「私钥去了哪」，只需要看这一个文件。
//!
//! 种子在函数内派生、用完即 `zeroize`，绝不跨 wasm 边界返回。

use pczt::roles::signer::Signer;
use zcash_keys::keys::UnifiedSpendingKey;
use zcash_protocol::consensus::Network;
use zeroize::Zeroizing;
use zip32::AccountId;

use crate::error::{ErrorCode, KeysError, Result};
use crate::keys::parse_network;

/// 用助记词派生出的花费密钥给 PCZT 签名。
///
/// 只签 Orchard 花费 —— 本方案不支持 Sapling 花费（Sapling 的 Groth16 证明参数
/// 约 50 MB，不适合随包分发；新收到的资金也不会落在 Sapling 池）。
///
/// 返回签名后的 PCZT 字节，交回 runtime 的 `pcztSend` 终结并广播。
pub fn sign_pczt_with_mnemonic(
    network: &str,
    mnemonic: &str,
    account_index: u32,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    let params = parse_network(network)?;

    let parsed = bip0039::Mnemonic::<bip0039::English>::from_phrase(mnemonic)
        .map_err(|_| KeysError::new(ErrorCode::InvalidMnemonic))?;
    let seed = Zeroizing::new(parsed.to_seed(""));

    sign_pczt_with_seed(params, seed.as_ref(), account_index, pczt_bytes)
}

/// 同上，但直接接收种子字节。调用方负责擦除自己那份。
/// 本 crate 当前实际能签的 bundle 类型。定义在实现旁边，翻转时必然改到实现。
pub const SIGN_ORCHARD: bool = true;
pub const SIGN_SAPLING: bool = false;
pub const SIGN_TRANSPARENT: bool = true;
pub const SIGN_IRONWOOD: bool = true;

/// 试签透明输入时扫多少个地址序号。
///
/// PCZT 里每个透明输入都带着自己的 BIP-32 派生路径，但 `pczt` 没有把那个字段
/// 公开出来，所以只能按序号逐个试。上游 `sign` 会从私钥反推 pubkey 并核对
/// `script_pubkey`，不匹配就报错 —— 所以试签不会静默产出错误签名。
///
/// 取值要覆盖钱包的 gap limit（默认 20），留一倍余量。
const TRANSPARENT_SCAN_GAP: u32 = 40;

/// 给全部透明输入签名，返回 (签上了几个, 最后一次失败原因)。
///
/// 透明输入是屏蔽（transparent → shielded）这条路的必经之处：不签它，打进
/// t 地址的钱就永远出不来。
fn sign_transparent_inputs(
    signer: &mut Signer,
    usk: &UnifiedSpendingKey,
    input_count: usize,
) -> (usize, Option<String>) {
    use transparent::keys::{NonHardenedChildIndex, TransparentKeyScope};

    let account_key = usk.transparent();
    let mut signed = 0usize;
    let mut last_err = None;

    for index in 0..input_count {
        let mut done = false;
        // 找零走 internal scope，收款走 external，两个都要试。
        for scope in [TransparentKeyScope::EXTERNAL, TransparentKeyScope::INTERNAL] {
            for i in 0..TRANSPARENT_SCAN_GAP {
                let Some(child) = NonHardenedChildIndex::from_index(i) else {
                    continue;
                };
                let Ok(sk) = account_key.derive_secret_key(scope, child) else {
                    continue;
                };
                match signer.sign_transparent(index, &sk) {
                    Ok(()) => {
                        signed += 1;
                        done = true;
                        break;
                    }
                    Err(e) => last_err = Some(format!("transparent[{index}]: {e:?}")),
                }
            }
            if done {
                break;
            }
        }
    }

    (signed, last_err)
}

pub fn sign_pczt_with_seed(
    params: Network,
    seed: &[u8],
    account_index: u32,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    if seed.len() < 32 {
        return Err(KeysError::with(
            ErrorCode::InvalidSeed,
            serde_json::json!({ "byteLen": seed.len(), "min": 32 }),
        ));
    }

    let account = AccountId::try_from(account_index).map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            serde_json::json!({ "value": account_index }),
        )
    })?;

    let usk = UnifiedSpendingKey::from_seed(&params, seed, account)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;

    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;

    let mut signer = Signer::new(pczt)
        .map_err(|e| KeysError::new(ErrorCode::PcztSignFailed).detail(format!("{e:?}")))?;

    // Orchard 花费授权密钥由花费密钥派生。
    let ask = orchard::keys::SpendAuthorizingKey::from(usk.orchard());

    // 逐个 action 尝试签名：不属于本账户的 action 会被上游拒绝，
    // 这里按「签得上就签」处理，两个池都签不上才算错误。
    //
    // 两个池都要签：NU6.3 之后新的花费走 Ironwood，而从 Orchard 迁出（那个池
    // 现在只出不进）走 Orchard。一笔迁移交易可能两边都有 action。
    let (orchard_count, ironwood_count, transparent_count) = bundle_counts(pczt_bytes)?;
    let mut signed_any = false;
    let mut last_err: Option<String> = None;

    // 透明输入必须**全部**签上。少签一个，交易就是无效的 —— 屏蔽这条路上
    // 所有输入都是透明的，漏一个等于整笔作废，所以这里是硬要求而非尽力而为。
    let (transparent_signed, transparent_err) =
        sign_transparent_inputs(&mut signer, &usk, transparent_count);
    if transparent_signed < transparent_count {
        return Err(KeysError::with(
            ErrorCode::PcztSignFailed,
            serde_json::json!({
                "transparentInputs": transparent_count,
                "transparentSigned": transparent_signed,
            }),
        )
        .detail(transparent_err.unwrap_or_else(|| {
            "a transparent input did not match any key in the scan range".into()
        })));
    }
    signed_any |= transparent_signed > 0;

    for index in 0..orchard_count {
        match signer.sign_orchard(index, &ask) {
            Ok(()) => signed_any = true,
            Err(e) => last_err = Some(format!("orchard[{index}]: {e:?}")),
        }
    }
    for index in 0..ironwood_count {
        match signer.sign_ironwood(index, &ask) {
            Ok(()) => signed_any = true,
            Err(e) => last_err = Some(format!("ironwood[{index}]: {e:?}")),
        }
    }

    if !signed_any {
        return Err(KeysError::with(
            ErrorCode::PcztSignFailed,
            serde_json::json!({
                "orchardActions": orchard_count,
                "ironwoodActions": ironwood_count,
                "transparentInputs": transparent_count,
            }),
        )
        .detail(last_err.unwrap_or_else(|| "nothing in this PCZT was signable".into())));
    }

    let signed = signer.finish();
    signed
        .serialize()
        .map_err(|e| KeysError::new(ErrorCode::PcztSignFailed).detail(format!("{e:?}")))
}

/// 数出这份 PCZT 里各 bundle 有多少东西要签：(orchard, ironwood, transparent)。
///
/// Ironwood 的 note 与 Orchard 是同一个 Rust 类型，只靠 `ValuePool` 标签区分，
/// 但 PCZT 里是两个独立的 bundle，所以必须分别数、分别签。
fn bundle_counts(pczt_bytes: &[u8]) -> Result<(usize, usize, usize)> {
    let pczt = pczt::Pczt::parse(pczt_bytes)
        .map_err(|e| KeysError::new(ErrorCode::PcztParseFailed).detail(format!("{e:?}")))?;
    Ok((
        pczt.orchard().actions().len(),
        pczt.ironwood().actions().len(),
        pczt.transparent().inputs().len(),
    ))
}

#[cfg(test)]
mod tests {
    use super::*;

    const M: &str =
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

    #[test]
    fn rejects_short_seed() {
        let e = sign_pczt_with_seed(Network::TestNetwork, &[0u8; 8], 0, &[]).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidSeed);
        assert_eq!(e.params["byteLen"], 8);
    }

    #[test]
    fn rejects_garbage_pczt() {
        let e = sign_pczt_with_mnemonic("test", M, 0, b"not a pczt").unwrap_err();
        assert_eq!(e.code, ErrorCode::PcztParseFailed);
    }

    #[test]
    fn rejects_bad_mnemonic() {
        let e = sign_pczt_with_mnemonic("test", "clearly not a mnemonic", 0, &[]).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidMnemonic);
    }
}
