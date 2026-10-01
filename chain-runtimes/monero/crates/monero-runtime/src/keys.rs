//! 观察密钥与地址派生。
//!
//! ## 这里有什么、没有什么
//!
//! **有**：花费公钥 + 私有观察密钥（即 monero-oxide 的 `ViewPair`），这是扫链
//! 需要的全部材料，等价于 Zcash 的 UFVK。
//!
//! **没有**：花费私钥。它永远不进这个 crate，也不落库 —— 签名走单独的路径，
//! 由宿主在需要时把种子传进签名函数，用完即弃。
//!
//! 想审计「私钥去了哪」，看这个文件就够：它一次都没出现。

use monero_wallet::{
    address::{MoneroAddress, Network, SubaddressIndex},
    ed25519::{CompressedPoint, Scalar},
    ViewPair,
};
use serde_json::json;
use zeroize::Zeroizing;

use crate::error::{ErrorCode, Result, RuntimeError};

/// 解析网络名。**没有缺省值** —— 主网和测试网的地址互不通用，猜错了钱会发到
/// 一个看起来合法、实际上无人持有的地址上。
pub fn parse_network(name: &str) -> Result<Network> {
    match name {
        "mainnet" | "main" => Ok(Network::Mainnet),
        "stagenet" | "stage" => Ok(Network::Stagenet),
        "testnet" | "test" => Ok(Network::Testnet),
        other => Err(RuntimeError::with(
            ErrorCode::InvalidNetwork,
            json!({ "value": other, "allowed": ["mainnet", "stagenet", "testnet"] }),
        )),
    }
}

pub fn network_name(network: Network) -> &'static str {
    match network {
        Network::Mainnet => "mainnet",
        Network::Stagenet => "stagenet",
        Network::Testnet => "testnet",
    }
}

fn expect_32(bytes: &[u8], field: &str) -> Result<[u8; 32]> {
    <[u8; 32]>::try_from(bytes).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": field, "byteLen": bytes.len(), "expected": 32 }),
        )
    })
}

/// 从原始字节造观察密钥对。
///
/// `spend_public` 是压缩的 ed25519 点，`view_private` 是标准序的标量。
/// 两者都来自宿主 —— 这个 crate 不做派生，只做使用。
pub fn view_pair_from_bytes(spend_public: &[u8], view_private: &[u8]) -> Result<ViewPair> {
    let spend = CompressedPoint::from(expect_32(spend_public, "spendPublicKey")?)
        .decompress()
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidKeyMaterial,
                json!({ "field": "spendPublicKey", "reason": "notACurvePoint" }),
            )
        })?;

    let view_bytes = expect_32(view_private, "viewPrivateKey")?;
    let view = Scalar::read(&mut view_bytes.as_slice()).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "viewPrivateKey", "reason": "notCanonicalScalar" }),
        )
    })?;

    // 上游拒绝带挠率的花费公钥（那种密钥能否花费存疑）。这是它的判断，不是
    // 我们的 —— 原样把拒绝理由传出去，不要吞掉。
    ViewPair::new(spend, Zeroizing::new(view)).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPublicKey" }),
        )
        .detail(format!("{e:?}"))
    })
}

/// 主地址（标准地址）。
pub fn primary_address(pair: &ViewPair, network: Network) -> String {
    pair.legacy_address(network).to_string()
}

/// 子地址。`(0, 0)` 是主地址，不是子地址 —— 上游对它返回 `None`，这里也拒绝，
/// 否则会注册出一个扫描器永远不认的条目。
pub fn subaddress(pair: &ViewPair, network: Network, major: u32, minor: u32) -> Result<String> {
    let index = SubaddressIndex::new(major, minor).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::InvalidSubaddressIndex,
            json!({ "major": major, "minor": minor, "reason": "zeroZeroIsThePrimaryAddress" }),
        )
    })?;
    Ok(pair.subaddress(network, index).to_string())
}

/// 校验一个收款地址：编码合法、且属于指定网络。
pub fn validate_address(network: Network, encoded: &str) -> bool {
    MoneroAddress::from_str(network, encoded).is_ok()
}

/// 解析收款地址，网络不符时明确报错而不是笼统的"地址无效"。
pub fn parse_address(network: Network, encoded: &str, field: &str) -> Result<MoneroAddress> {
    MoneroAddress::from_str(network, encoded).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "field": field, "network": network_name(network) }),
        )
        .detail(format!("{e:?}"))
    })
}

#[cfg(test)]
mod tests {
    use super::*;

    /// 上游测试向量之外的公开地址，只用来证明解析路径真的跑了。
    const MAINNET_ADDR: &str = "44AFFq5kSiGBoZ4NMDwYtN18obc8AemS33DBLWs3H7otXft3XjrpDtQGv7SqSsaBYBb98uNbr2VBBEt7f2wfn3RVGQBEP3A";

    #[test]
    fn network_names_round_trip() {
        for (name, net) in [
            ("mainnet", Network::Mainnet),
            ("stagenet", Network::Stagenet),
            ("testnet", Network::Testnet),
        ] {
            assert_eq!(parse_network(name).unwrap(), net);
            assert_eq!(network_name(net), name);
        }
        // 别名也收
        assert_eq!(parse_network("main").unwrap(), Network::Mainnet);
    }

    /// 没有缺省网络 —— 猜错网络的代价是钱发到无人持有的地址上。
    #[test]
    fn unknown_network_is_rejected_not_defaulted() {
        let e = parse_network("").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidNetwork);
        assert!(parse_network("regtest").is_err());
    }

    #[test]
    fn mainnet_address_is_not_valid_on_other_networks() {
        assert!(validate_address(Network::Mainnet, MAINNET_ADDR));
        assert!(!validate_address(Network::Testnet, MAINNET_ADDR));
        assert!(!validate_address(Network::Stagenet, MAINNET_ADDR));
    }

    /// `ViewPair` 刻意不实现 `Debug`（免得密钥被打进日志），所以测试里不能用
    /// `unwrap_err` —— 那要求 Ok 类型可 Debug。显式 match 取错误。
    fn expect_key_error(spend: &[u8], view: &[u8]) -> RuntimeError {
        match view_pair_from_bytes(spend, view) {
            Ok(_) => panic!("本该被拒绝的密钥材料却通过了"),
            Err(e) => e,
        }
    }

    #[test]
    fn key_material_length_is_enforced() {
        let e = expect_key_error(&[0u8; 31], &[0u8; 32]);
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["byteLen"], 31);
    }

    /// 全零不是合法曲线点，必须在解压那一步就被挡住，而不是带着一个无效点
    /// 一路跑到扫描里去。
    #[test]
    fn non_curve_spend_key_is_rejected() {
        let mut not_a_point = [0u8; 32];
        not_a_point[0] = 2; // 刻意构造一个解压不出来的值
        let e = expect_key_error(&not_a_point, &[1u8; 32]);
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
    }

    /// (0,0) 是主地址。允许它进子地址注册表会产出一个扫描器永远不认的条目，
    /// 表现为「这个地址收到的钱永远扫不到」。
    #[test]
    fn subaddress_zero_zero_is_refused() {
        let pair = test_pair();
        let e = subaddress(&pair, Network::Mainnet, 0, 0).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidSubaddressIndex);
        assert!(subaddress(&pair, Network::Mainnet, 0, 1).is_ok());
        assert!(subaddress(&pair, Network::Mainnet, 1, 0).is_ok());
    }

    /// 子地址必须与主地址不同，且不同序号之间互不相同 —— 派生错了会让不同
    /// 账户共用一个收款地址，是最坏的那类 bug。
    #[test]
    fn subaddresses_are_distinct_from_each_other_and_the_primary() {
        let pair = test_pair();
        let primary = primary_address(&pair, Network::Mainnet);
        let a = subaddress(&pair, Network::Mainnet, 0, 1).unwrap();
        let b = subaddress(&pair, Network::Mainnet, 0, 2).unwrap();
        let c = subaddress(&pair, Network::Mainnet, 1, 1).unwrap();
        for x in [&a, &b, &c] {
            assert_ne!(*x, primary);
        }
        assert_ne!(a, b);
        assert_ne!(a, c);
        assert_ne!(b, c);
    }

    fn test_pair() -> ViewPair {
        // 从一个已知合法地址取出它的公钥对，再配一个任意标量当观察私钥。
        // 这里不关心它们是否互相对应 —— 测的是派生与编码路径。
        let addr = MoneroAddress::from_str(Network::Mainnet, MAINNET_ADDR).unwrap();
        let spend = addr.spend().compress().to_bytes();
        let mut view = [0u8; 32];
        view[0] = 9; // 小标量，必然是标准序
        match view_pair_from_bytes(&spend, &view) {
            Ok(pair) => pair,
            Err(e) => panic!("测试密钥对构造失败: {e}"),
        }
    }
}

// ── 从 BIP-39 / BIP-32 派生 Monero 密钥 ─────────────────────────────────

/// 从 BIP-32 派生出来的 32 字节私钥造出 Monero 的密钥三元组。
///
/// ## 为什么是这个约定
///
/// Monero 原生用自己的 25 词助记词，而 OneKey 全链统一 BIP-39。业界（以及
/// **OneKey V4 自己**）的做法是：
///
/// ```text
/// spend_secret = sc_reduce32(bip32_private_key)
/// view_secret   = sc_reduce32(keccak256(spend_secret))
/// ```
///
/// **这个约定不能改。** V4 用的就是它（`sdkXmr/helper.ts` 的
/// `getKeyPairFromRawPrivatekey`：先 `scReduce32` 再 `hashToScalar`），换一个
/// 会让老用户导入同一套助记词后看到一个空钱包 —— 钱还在链上，只是这个钱包
/// 再也找不到它。
///
/// 返回观察材料与主地址。**花费私钥不返回** —— 它就是调用方传进来的那个
/// BIP-32 私钥，宿主本来就持有，没有理由让它再跨一次边界。
pub fn derive_from_bip32_key(network: Network, bip32_private_key: &[u8]) -> Result<DerivedKeys> {
    use monero_wallet::ed25519::Point;

    let raw = expect_32(bip32_private_key, "bip32PrivateKey")?;

    // sc_reduce32：把任意 32 字节约简到标量域内。
    let spend = Scalar::from(curve25519_dalek::Scalar::from_bytes_mod_order(raw));
    // hash_to_scalar：keccak256 再约简。上游 `Scalar::hash` 正是这个。
    let view = Scalar::hash(<[u8; 32]>::from(spend));

    let spend_public =
        Point::from(spend.into() * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);

    let pair = ViewPair::new(spend_public, Zeroizing::new(view)).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "bip32PrivateKey" }),
        )
        .detail(format!("{e:?}"))
    })?;

    Ok(DerivedKeys {
        spend_public_key: spend_public.compress().to_bytes(),
        view_private_key: <[u8; 32]>::from(view),
        primary_address: primary_address(&pair, network),
    })
}

pub struct DerivedKeys {
    pub spend_public_key: [u8; 32],
    pub view_private_key: [u8; 32],
    pub primary_address: String,
}

/// 花费私钥的标量形式，签名时用。与 [`derive_from_bip32_key`] 同一个约简。
pub fn spend_scalar_from_bip32_key(bip32_private_key: &[u8]) -> Result<[u8; 32]> {
    let raw = expect_32(bip32_private_key, "bip32PrivateKey")?;
    let spend = Scalar::from(curve25519_dalek::Scalar::from_bytes_mod_order(raw));
    Ok(<[u8; 32]>::from(spend))
}

#[cfg(test)]
mod derive_tests {
    use super::*;

    /// 派生必须是确定性的，且同一把 BIP-32 私钥在不同网络上给出不同前缀的
    /// 地址（主网 4 / 测试网 9 / stagenet 5）。
    #[test]
    fn derivation_is_deterministic_and_network_scoped() {
        let key = [3u8; 32];
        let a = derive_from_bip32_key(Network::Mainnet, &key).expect("派生");
        let b = derive_from_bip32_key(Network::Mainnet, &key).expect("派生");
        assert_eq!(a.primary_address, b.primary_address);
        assert_eq!(a.spend_public_key, b.spend_public_key);
        assert!(a.primary_address.starts_with('4'));

        let t = derive_from_bip32_key(Network::Testnet, &key).expect("派生");
        assert_ne!(t.primary_address, a.primary_address);
    }

    /// 不同的 BIP-32 私钥必须给出不同的地址 —— 撞了就是灾难。
    #[test]
    fn different_keys_give_different_addresses() {
        let a = derive_from_bip32_key(Network::Mainnet, &[1u8; 32]).unwrap();
        let b = derive_from_bip32_key(Network::Mainnet, &[2u8; 32]).unwrap();
        assert_ne!(a.primary_address, b.primary_address);
        assert_ne!(a.view_private_key, b.view_private_key);
    }

    /// 观察密钥必须由花费密钥经 keccak 得出，而不是别的什么 —— 这条锁住
    /// 与 V4 的兼容性（`hashToScalar(privateSpendKey)`）。
    /// **V4 兼容性契约。** 这是从 OneKey V4 取的真实向量：
    /// 助记词经 `m/44'/128'/0'` 派生出的 BIP-32 私钥，以及 V4 当时显示给用户的
    /// 那个主地址（见 app 仓 `CoreChainSoftware.test.ts` 的 fixture）。
    ///
    /// 派生约定是 `spend = sc_reduce32(bip32_key)`、`view = keccak256(spend)`。
    /// 这条一旦改动，V4 用户导入同一助记词会看到**空钱包** —— 钱还在链上，
    /// 只是这个钱包再也找不到它。所以用真实向量钉死，不是自己造一个。
    #[test]
    fn the_v4_derivation_vector_still_produces_the_v4_address() {
        let key = hex::decode("8b28f5749ec99464d5a9d6527125aa8778ea017dd9e5cffaa567d2f62f2c8cbc")
            .unwrap();
        let derived = derive_from_bip32_key(Network::Mainnet, &key).expect("派生应当成功");
        assert_eq!(
            derived.primary_address,
            "45vw7EY1nYs66nCedDxS9hYptSP2PnnQwezJ4WmGsNFFKAKXMDWSEspFUKBD6UCivUB5LGW2vXoX8D3DjzhrFuoZSoDcYPN"
        );
    }

    #[test]
    fn the_view_key_is_the_hash_of_the_spend_key() {
        let key = [7u8; 32];
        let derived = derive_from_bip32_key(Network::Mainnet, &key).unwrap();
        let spend = spend_scalar_from_bip32_key(&key).unwrap();
        let expected = <[u8; 32]>::from(Scalar::hash(spend));
        assert_eq!(derived.view_private_key, expected);
    }

    #[test]
    fn key_length_is_enforced() {
        let e = derive_from_bip32_key(Network::Mainnet, &[0u8; 31])
            .err()
            .expect("长度不对必须拒绝");
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
    }
}
