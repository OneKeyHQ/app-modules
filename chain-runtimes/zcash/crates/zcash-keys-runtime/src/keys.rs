//! 密钥派生。
//!
//! 种子和 USK 的生命周期严格控制在本模块内：入口接收字节、出口只给
//! UFVK 与地址，中间副本用完即 `zeroize`。

use zcash_keys::keys::{UnifiedFullViewingKey, UnifiedSpendingKey};
use zcash_protocol::consensus::Network;
#[cfg(test)]
use zeroize::Zeroizing;
use zip32::AccountId;

use crate::error::{ErrorCode, KeysError, Result};
use serde_json::json;

pub fn parse_network(s: &str) -> Result<Network> {
    match s {
        "main" => Ok(Network::MainNetwork),
        "test" => Ok(Network::TestNetwork),
        other => Err(KeysError::with(
            ErrorCode::InvalidNetwork,
            json!({ "value": other }),
        )),
    }
}

/// Decode only recipients supported by the transaction builders.
pub(crate) fn supported_recipient(
    params: &impl zcash_protocol::consensus::Parameters,
    encoded: &str,
) -> Option<zcash_keys::address::Address> {
    use zcash_keys::address::Address;
    // Host routing uses canonical lowercase Unified Address prefixes.
    if encoded.starts_with("U") {
        return None;
    }
    match Address::decode(params, encoded)? {
        address @ Address::Transparent(_) => Some(address),
        Address::Unified(address) if address.has_orchard() => Some(Address::Unified(address)),
        _ => None,
    }
}

/// Validate a recipient locally without storage, scanning, or network access.
pub fn validate_address(network: &str, encoded: &str) -> Result<bool> {
    let params = parse_network(network)?;
    Ok(supported_recipient(&params, encoded).is_some())
}

/// 测试专用：从助记词派生 UFVK。
///
/// 出厂产物不收助记词 —— 宿主只传种子字节。留着它是为了让金标向量测试
/// 能直接喂参考实现用的助记词。
#[cfg(test)]
pub(crate) fn ufvk_from_mnemonic(
    network: &str,
    mnemonic: &str,
    account_index: u32,
) -> Result<String> {
    let params = parse_network(network)?;
    let parsed = bip0039::Mnemonic::<bip0039::English>::from_phrase(mnemonic)
        .map_err(|_| KeysError::new(ErrorCode::InvalidMnemonic))?;
    let seed = Zeroizing::new(parsed.to_seed(""));
    ufvk_from_seed_bytes(params, seed.as_ref(), account_index)
}

/// 从原始种子字节派生 UFVK。
///
/// 调用方给的切片本 crate 不持有。注意中间的 `UnifiedSpendingKey` 上游没有
/// `ZeroizeOnDrop`，析构后仍留在线性内存里 —— 擦除责任在宿主那一侧。
pub fn ufvk_from_seed_bytes(params: Network, seed: &[u8], account_index: u32) -> Result<String> {
    if seed.len() < 32 {
        return Err(KeysError::with(
            ErrorCode::InvalidSeed,
            json!({ "byteLen": seed.len(), "min": 32 }),
        ));
    }

    let account = AccountId::try_from(account_index).map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            json!({ "value": account_index }),
        )
    })?;

    let ufvk = {
        let usk = UnifiedSpendingKey::from_seed(&params, seed, account)
            .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;
        usk.to_unified_full_viewing_key()
    };

    Ok(ufvk.encode(&params))
}

/// UA 里包含哪些 receiver。**这是隐私策略，不是协议事实** ——
/// 带不带 transparent receiver 决定了对方能不能用公开方式付给你，
/// 也决定了这个地址会不会把你的透明活动和屏蔽活动关联起来。所以由宿主指定。
///
/// Runtime 只负责校验组合在协议上是否成立。
fn receiver_policy(name: &str) -> Result<zcash_keys::keys::UnifiedAddressRequest> {
    use zcash_keys::keys::UnifiedAddressRequest as R;
    match name {
        // 空串不再当作 `"all"`。缺省最省事的那个取值恰好是隐私上最贵的一个：
        // 带 transparent receiver 的 UA 会把用户的透明活动和屏蔽活动绑在
        // 同一个字符串里，而调用方什么都没写，也就什么都没同意。
        "all" => Ok(R::ALLOW_ALL),
        "shielded" => Ok(R::SHIELDED),
        "orchard" => Ok(R::ORCHARD),
        other => Err(KeysError::with(
            ErrorCode::InvalidUfvk,
            json!({ "stage": "receiverPolicy", "value": other }),
        )),
    }
}

/// 从 UFVK 取统一地址（UA）。不涉及任何私钥。
///
/// `policy` 决定包含哪些 receiver：`"all"` / `"shielded"` / `"orchard"`。
pub fn unified_address_with(network: &str, ufvk_str: &str, policy: &str) -> Result<String> {
    let params = parse_network(network)?;
    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;

    let (addr, _) = ufvk
        .default_address(receiver_policy(policy)?)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;

    Ok(addr.encode(&params))
}

/// 从 UFVK 取透明收款地址（t 地址）。不涉及任何私钥、不需要钱包库。
///
/// 与 UA 是同一份 UFVK 的两种呈现：UA 是包含多个 receiver 的统一地址，
/// 这里取出其中的透明 receiver 单独编码。交易所之类只认 t 地址的场合要用它。
pub fn transparent_address(network: &str, ufvk_str: &str) -> Result<String> {
    let params = parse_network(network)?;
    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;

    let (addr, _) = ufvk
        .default_address(zcash_keys::keys::UnifiedAddressRequest::ALLOW_ALL)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;

    let t = addr.transparent().ok_or_else(|| {
        KeysError::with(
            ErrorCode::DeriveFailed,
            json!({ "reason": "noTransparentReceiver" }),
        )
    })?;

    Ok(zcash_keys::encoding::encode_transparent_address_p(
        &params, t,
    ))
}

/// UFVK 里的透明账户公钥，BIP32 账户层（m/44'/133'/account'）：
/// 32 字节 chain code || 33 字节压缩公钥，hex。宿主据此拼 xpub，
/// 硬件账户无需再向设备额外要一次透明 xpub。
pub fn transparent_account_pubkey(network: &str, ufvk_str: &str) -> Result<String> {
    let params = parse_network(network)?;
    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;
    let account_pubkey = ufvk.transparent().ok_or_else(|| {
        KeysError::with(
            ErrorCode::DeriveFailed,
            json!({ "reason": "noTransparentFvk" }),
        )
    })?;
    Ok(hex::encode(account_pubkey.serialize()))
}

/// Standard ZIP-32 seed fingerprint, persisted as account identity.
pub fn seed_fingerprint(seed: &[u8]) -> Result<String> {
    if seed.len() < 32 {
        return Err(KeysError::with(
            ErrorCode::InvalidSeed,
            json!({ "byteLen": seed.len(), "min": 32 }),
        ));
    }
    let fp = zip32::fingerprint::SeedFingerprint::from_seed(seed)
        .ok_or_else(|| KeysError::with(ErrorCode::InvalidSeed, json!({ "byteLen": seed.len() })))?;
    Ok(fp.to_bytes().iter().map(|b| format!("{b:02x}")).collect())
}

#[cfg(test)]
mod tests {
    use super::*;

    /// 指纹是**标准值**且被宿主持久化当账户身份。这条测试锁死它：
    /// 实现哪天漂了，存量账户会全部失配，而那种故障很难从现象反推。
    #[test]
    fn seed_fingerprint_is_stable() {
        let seed = [0x42u8; 64];
        let fp = seed_fingerprint(&seed).unwrap();
        assert_eq!(fp.len(), 64, "32 字节 → 64 位 hex");
        assert_eq!(fp, seed_fingerprint(&seed).unwrap(), "同一种子必须同一指纹");
        // 换一个种子必须变
        assert_ne!(fp, seed_fingerprint(&[0x43u8; 64]).unwrap());
        // 长度不足要拒绝，而不是算出一个看似合理的值
        assert!(seed_fingerprint(&[0u8; 16]).is_err());
    }

    #[test]
    fn transparent_address_from_ufvk_is_t_prefixed() {
        let ufvk = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).unwrap();
        let t = transparent_address("test", &ufvk).unwrap();
        assert!(t.starts_with("tm"), "testnet t 地址前缀应为 tm，实际 {t}");
        let ua = unified_address_with("test", &ufvk, "all").unwrap();
        assert!(ua.starts_with("utest"), "UA 前缀应为 utest");
        assert_ne!(t, ua, "t 地址与 UA 是两种呈现，不该相同");
    }

    // 公开测试向量，切勿用于真实资金。
    const TEST_MNEMONIC: &str =
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

    #[test]
    fn recipient_validation_checks_network_checksum_and_supported_receivers() {
        use zcash_address::unified::{Address, Encoding, Receiver};
        use zcash_protocol::consensus::NetworkType;

        let ufvk = ufvk_from_mnemonic("main", TEST_MNEMONIC, 0).unwrap();
        let ua = unified_address_with("main", &ufvk, "orchard").unwrap();
        assert!(validate_address("main", &ua).unwrap());
        assert!(!validate_address("test", &ua).unwrap());
        assert!(!validate_address("main", &ua.to_uppercase()).unwrap());
        let mut corrupt = ua.clone();
        corrupt.pop();
        corrupt.push(if ua.ends_with('q') { 'p' } else { 'q' });
        assert!(!validate_address("main", &corrupt).unwrap());
        for receiver in [Receiver::P2pkh([1; 20]), Receiver::P2sh([2; 20])] {
            let no_orchard = Address::try_from_items(vec![
                receiver,
                Receiver::Unknown {
                    typecode: 65535,
                    data: vec![1; 43],
                },
            ])
            .unwrap()
            .encode(&NetworkType::Main);
            assert!(!validate_address("main", &no_orchard).unwrap());
        }
        let transparent = transparent_address("main", &ufvk).unwrap();
        assert!(validate_address("main", &transparent).unwrap());
        assert!(!validate_address("test", &transparent).unwrap());
        assert!(!validate_address("main", "u1not-an-address").unwrap());
        assert!(validate_address("regtest", &ua).is_err());
    }

    #[test]
    fn transparent_account_pubkey_is_chain_code_and_compressed_key() {
        let ufvk = ufvk_from_mnemonic("main", TEST_MNEMONIC, 0).unwrap();
        let hex_key = transparent_account_pubkey("main", &ufvk).unwrap();
        assert_eq!(hex_key.len(), 65 * 2);
        let tag = &hex_key[64..66];
        assert!(
            tag == "02" || tag == "03",
            "压缩公钥前缀应为 02/03，实际 {tag}"
        );
    }

    /// 省略 / 留空 receiver 策略曾经等于选中 `"all"` —— 那是隐私上最贵的取值，
    /// 而调用方什么都没写，也就什么都没同意。
    #[test]
    fn receiver_policy_has_no_default() {
        assert!(receiver_policy("all").is_ok());
        assert!(receiver_policy("shielded").is_ok());
        assert!(receiver_policy("orchard").is_ok());
        assert!(receiver_policy("").is_err());
        assert!(receiver_policy("transparent").is_err());
    }

    #[test]
    fn network_parsing() {
        assert!(parse_network("main").is_ok());
        assert!(parse_network("test").is_ok());
        assert!(parse_network("regtest").is_err());
        assert!(parse_network("mainnet").is_err());
        assert!(parse_network("testnet").is_err());
    }

    #[test]
    fn short_seed_is_rejected() {
        let err = ufvk_from_seed_bytes(Network::TestNetwork, &[0u8; 16], 0);
        assert!(err.is_err(), "过短的种子必须拒绝");
    }

    #[test]
    fn derives_ufvk_and_address() {
        let ufvk = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).expect("应能派生");
        assert!(
            ufvk.starts_with("uview"),
            "testnet UFVK 前缀应为 uview，实际 {ufvk}"
        );

        let addr = unified_address_with("test", &ufvk, "all").expect("应能取地址");
        assert!(
            addr.starts_with("utest"),
            "testnet UA 前缀应为 utest，实际 {addr}"
        );
    }

    #[test]
    fn different_accounts_differ() {
        let a = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).unwrap();
        let b = ufvk_from_mnemonic("test", TEST_MNEMONIC, 1).unwrap();
        assert_ne!(a, b, "不同账户序号必须派生出不同的 UFVK");
    }

    /// 与硬件固件的互通向量（2026-08-25 对齐），路径 m/32'/133'/account'。
    ///
    /// 硬件侧向量是固件独立实现产出的：UFVK 不含 sapling 项、UA 为
    /// orchard+p2pkh 组合，所以**不比整串，比同名项的字节** —— 指纹、
    /// orchard FVK（96B）、transparent 公钥（65B）、两个 receiver。任何一侧
    /// 的路径、币种或 diversifier 策略漂移都会在这里炸。
    #[test]
    fn hardware_parity_vectors() {
        use zcash_address::unified::{Container, Encoding, Fvk, Receiver, Ufvk};

        const MNEMONIC: &str = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";
        const FINGERPRINT: &str =
            "21ed3d7882c7e37fe012b54a6408048048cb09782d4b2938617da793ccd27815";
        const HW: [(u32, &str, &str); 2] = [
            (
                0,
                "uview1s8x5c6zq8pllw2v8ywmcp0fm46d5dk0er8gwwraz2l2s6g0cvq0jlyar820w9z6sh5l285d2ctzcukr2j2fn6cl7nyapa235fxkcmf7xkg4e3pc0phu7e95mukwx3j8t96p7yzuuxhdl9u28letaxay3gyscd9s4tr59dxg4uulsdxjxmy64ls4sy36n8p03j7f0d9w4ycjullvp5lqgslc4agjlgxzk53wnwkcm4andl5xqgs2afa7ewkkfl9fcyw7s5kknzlmn55m6sz93yvqpx78shy0uausk3avw",
                "u1wzxtkul0j9he76y7m87agwd4z3vrkauchag9kwrzng07mkm2ntmc687mg45m4rjr979zhyjkn80ml4vkaty3ej20sc7hk65n0yr42chn7hw6ysdgwpu5l38ttxzhyldaa2unx7u4j8c",
            ),
            (
                1,
                "uview1anf6l9jhehdpktvfw4xur3emcrx7yeh6tczezfks6jmgujakhny45uqu6tc0un9ewqlk7uh5eq5fxfacwhgu3pw94yr5khdqdfl660ypxl2xryyqm6hamwwukn5dygk2hkl8lutx3ck5qy9zched9rny0ckz7rezx65um0rj0l0lcxsry8nzjakx9ftz6lecjjnym76pz5eury9yvh7rnk4pn8l76nc092h5l0x38vnxxgkwngdk0lguqjjy3sa2vzr5vay65p04zh0nqev5wvy5ypv7u060usd8yhqs",
                "u1w969tctgrxzjplcpjape6al3wdqfzap4v82zhc6spfz24erd5jaea8w0rg649vknphqp30kgsahnupznu6faasvxwr700xxld9ttwermpgghrk7lxvxydmzku6vw3gwnkgac53c2v3h",
            ),
        ];

        let parsed = bip0039::Mnemonic::<bip0039::English>::from_phrase(MNEMONIC).expect("助记词");
        let seed = parsed.to_seed("");
        assert_eq!(
            seed_fingerprint(seed.as_ref()).unwrap(),
            FINGERPRINT,
            "种子指纹与硬件不一致"
        );

        // UFVK 里的 orchard / transparent 项字节。
        let fvk_items = |s: &str| {
            let (_, u) = Ufvk::decode(s).expect("解析 UFVK");
            let mut orchard = None;
            let mut p2pkh = None;
            for item in u.items() {
                match item {
                    Fvk::Orchard(d) => orchard = Some(d.to_vec()),
                    Fvk::P2pkh(d) => p2pkh = Some(d.to_vec()),
                    _ => {}
                }
            }
            (orchard.expect("缺 orchard 项"), p2pkh.expect("缺 p2pkh 项"))
        };
        // UA 里的 orchard / p2pkh receiver 字节。
        let ua_receivers = |s: &str| {
            let (_, a) = zcash_address::unified::Address::decode(s).expect("解析 UA");
            let mut orchard = None;
            let mut p2pkh = None;
            for item in a.items() {
                match item {
                    Receiver::Orchard(d) => orchard = Some(d.to_vec()),
                    Receiver::P2pkh(d) => p2pkh = Some(d.to_vec()),
                    _ => {}
                }
            }
            (
                orchard.expect("缺 orchard receiver"),
                p2pkh.expect("缺 p2pkh receiver"),
            )
        };

        for (account, hw_ufvk, hw_ua) in HW {
            let ours = ufvk_from_mnemonic("main", MNEMONIC, account).expect("派生 UFVK");
            assert_eq!(
                fvk_items(&ours),
                fvk_items(hw_ufvk),
                "account {account}: FVK 项字节与硬件不一致"
            );
            // 我们带 p2pkh receiver 的组装是 "all"；只取它的 receiver 字节比对。
            let our_ua = unified_address_with("main", &ours, "all").expect("UA");
            assert_eq!(
                ua_receivers(&our_ua),
                ua_receivers(hw_ua),
                "account {account}: 地址 receiver 字节与硬件不一致"
            );
        }
    }
}

#[cfg(test)]
mod ua_inspect {
    fn receivers(ua: &str) -> Vec<String> {
        use zcash_address::unified::{Container, Encoding};
        let (_, parsed) = zcash_address::unified::Address::decode(ua).expect("解析 UA");
        parsed
            .items()
            .iter()
            .map(|item| match item {
                zcash_address::unified::Receiver::Orchard(_) => "orchard".to_owned(),
                zcash_address::unified::Receiver::Sapling(_) => "sapling".to_owned(),
                zcash_address::unified::Receiver::P2pkh(_) => "transparent-p2pkh".to_owned(),
                zcash_address::unified::Receiver::P2sh(_) => "transparent-p2sh".to_owned(),
                other => format!("{other:?}"),
            })
            .collect()
    }

    /// 打印某助记词的收款地址，供本机手工测试（要水龙头的币）用。
    /// 需要 `ZR_MNEMONIC`，未设置时直接跳过，所以常规 `cargo test` 不受影响。
    ///
    /// 同时打印每种 policy 的 receiver 组成 —— 收款地址里混进不可花的 receiver
    /// 会让打进来的钱永远取不出来，这条是唯一能提前看见它的地方。
    #[test]
    fn print_receiving_addresses() {
        let mnemonic = std::env::var("ZR_MNEMONIC").unwrap_or_default();
        if mnemonic.trim().is_empty() {
            return;
        }
        let network = std::env::var("ZR_NETWORK").unwrap_or_else(|_| "test".to_owned());
        let account: u32 = std::env::var("ZR_ACCOUNT")
            .ok()
            .and_then(|v| v.parse().ok())
            .unwrap_or(0);
        let ufvk =
            super::ufvk_from_mnemonic(&network, mnemonic.trim(), account).expect("派生 UFVK");

        // 与硬件对向量用的完整三元组：指纹 → UFVK → 地址，粗到细，
        // 哪一级先岔开，问题就在那一级对应的派生步骤里。
        let parsed =
            bip0039::Mnemonic::<bip0039::English>::from_phrase(mnemonic.trim()).expect("助记词");
        let seed = parsed.to_seed("");
        println!("network      {network}  account {account}");
        println!(
            "fingerprint  {}",
            super::seed_fingerprint(seed.as_ref()).expect("指纹")
        );
        println!("ufvk         {ufvk}");
        println!(
            "t-addr       {}",
            super::transparent_address(&network, &ufvk).expect("t 地址")
        );
        for policy in ["all", "shielded", "orchard"] {
            let ua = super::unified_address_with(&network, &ufvk, policy).expect("UA");
            println!("UA[{policy}]  {ua}");
            println!("             receivers = {:?}", receivers(&ua));
        }
    }

    /// 解析一个 UFVK（uview…），列出它包含哪些密钥项。
    /// 与硬件对派生时用：整串不等先看项集合 —— 项集合不同是组装差异，
    /// 项集合相同而字节不同才是真正的派生差异。
    #[test]
    fn inspect_external_ufvk() {
        let s = std::env::var("ZR_UFVK").unwrap_or_default();
        if s.is_empty() {
            return;
        }
        use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
        let hex = |b: &[u8]| b.iter().map(|x| format!("{x:02x}")).collect::<String>();
        let (net, parsed) = Ufvk::decode(&s).expect("解析 UFVK");
        println!("网络: {net:?}");
        for item in parsed.items() {
            match &item {
                Fvk::Orchard(d) => println!("  orchard          {}", hex(d)),
                Fvk::Sapling(d) => println!("  sapling          {}", hex(d)),
                Fvk::P2pkh(d) => println!("  transparent-p2pkh {}", hex(d)),
                other => println!("  其他密钥项: {other:?}"),
            }
        }
    }

    /// 解析一个 UA，列出它包含哪些 receiver。
    /// 用来看别的钱包实际给用户的收款地址里放了什么。
    #[test]
    fn inspect_external_ua() {
        let ua = std::env::var("ZR_UA").unwrap_or_default();
        if ua.is_empty() {
            return;
        }
        use zcash_address::unified::{Container, Encoding};
        let hex = |b: &[u8]| b.iter().map(|x| format!("{x:02x}")).collect::<String>();
        let (net, parsed) = zcash_address::unified::Address::decode(&ua).expect("解析 UA");
        println!("网络: {net:?}");
        for item in parsed.items() {
            match &item {
                zcash_address::unified::Receiver::Orchard(d) => {
                    println!("  orchard          {}", hex(d));
                }
                zcash_address::unified::Receiver::Sapling(d) => {
                    println!("  sapling          {}", hex(d));
                }
                zcash_address::unified::Receiver::P2pkh(d) => {
                    println!("  transparent-p2pkh {}", hex(d));
                }
                zcash_address::unified::Receiver::P2sh(d) => {
                    println!("  transparent-p2sh {}", hex(d));
                }
                other => println!("  其他 receiver: {other:?}"),
            }
        }
    }
}
