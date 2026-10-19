//! Public key material: networks, recipients, UFVK-derived addresses.
//!
//! Account UFVKs are built from host-supplied account keys in `account_keys`.

use zcash_keys::keys::UnifiedFullViewingKey;
#[cfg(test)]
use zcash_keys::keys::UnifiedSpendingKey;
use zcash_protocol::consensus::Network;
#[cfg(test)]
use zeroize::Zeroizing;
#[cfg(test)]
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
        address @ (Address::Transparent(_) | Address::Tex(_)) => Some(address),
        Address::Unified(address) if address.has_orchard() => Some(Address::Unified(address)),
        _ => None,
    }
}

/// Validate a recipient locally without storage, scanning, or network access.
pub fn validate_address(network: &str, encoded: &str) -> Result<bool> {
    let params = parse_network(network)?;
    Ok(supported_recipient(&params, encoded).is_some())
}

/// Test-only: derive a UFVK from a mnemonic.
///
/// Shipped builds never take a mnemonic or seed.
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

/// Test-only oracle: the upstream seed-based UFVK, reduced to the transparent and
/// Orchard items. Parity tests compare `account_keys` against it.
#[cfg(test)]
pub(crate) fn ufvk_from_seed_bytes(
    params: Network,
    seed: &[u8],
    account_index: u32,
) -> Result<String> {
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

    without_sapling(params, &ufvk)
}

#[cfg(test)]
pub(crate) fn without_sapling(params: Network, ufvk: &UnifiedFullViewingKey) -> Result<String> {
    use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
    let (network, container) = Ufvk::decode(&ufvk.encode(&params))
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;
    let items = container
        .items()
        .into_iter()
        .filter(|item| matches!(item, Fvk::P2pkh(_) | Fvk::Orchard(_)))
        .collect::<Vec<_>>();
    let container = Ufvk::try_from_items(items)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;
    Ok(container.encode(&network))
}

/// Receivers to include in a UA. This is a privacy policy chosen by the host:
/// a transparent receiver links the user's transparent and shielded activity.
/// The runtime only checks that the combination is valid.
///
/// No policy includes a Sapling receiver: this runtime cannot spend Sapling, so
/// Sapling-only senders would pay into an unspendable pool. `"shielded"` is
/// therefore an alias of `"orchard"`, kept for existing callers.
fn receiver_policy(name: &str) -> Result<zcash_keys::keys::UnifiedAddressRequest> {
    use zcash_keys::keys::{ReceiverRequirement::*, UnifiedAddressRequest as R};
    match name {
        // No default: an empty policy must not silently select the least private option.
        "all" => Ok(R::unsafe_custom(Require, Omit, Allow)),
        "shielded" | "orchard" => Ok(R::ORCHARD),
        other => Err(KeysError::with(
            ErrorCode::InvalidUfvk,
            json!({ "stage": "receiverPolicy", "value": other }),
        )),
    }
}

/// Derive the Unified Address from a UFVK. No spending key involved.
///
/// `policy` selects the receivers: `"all"` / `"shielded"` / `"orchard"`.
pub fn unified_address_with(network: &str, ufvk_str: &str, policy: &str) -> Result<String> {
    let params = parse_network(network)?;
    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;

    let (addr, _) = ufvk
        .default_address(receiver_policy(policy)?)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;

    Ok(addr.encode(&params))
}

/// Derive the transparent receiving address (t-address) from a UFVK. No
/// spending key or wallet database involved.
///
/// This is the transparent receiver of the `"all"` UA, encoded on its own for
/// counterparties that only accept t-addresses.
pub fn transparent_address(network: &str, ufvk_str: &str) -> Result<String> {
    let params = parse_network(network)?;
    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;

    // Same address as `"all"`, so this matches the UA's transparent receiver.
    let (addr, _) = ufvk
        .default_address(receiver_policy("all")?)
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

/// Transparent account public key from the UFVK at the BIP32 account level
/// (m/44'/133'/account'), hex of 32-byte chain code || 33-byte compressed key.
/// The host builds the xpub from it without asking a hardware device again.
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

#[cfg(test)]
mod tests {
    use super::*;

    /// Same items as the firmware's UFVK, so a device and a software account on
    /// one seed agree.
    #[test]
    fn ufvk_holds_only_transparent_and_orchard() {
        use crate::account_keys::{test_keys, ufvk_from_account_keys};
        use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
        let seed = [0x42u8; 64];
        let encoded = ufvk_from_account_keys(
            "main",
            &test_keys::orchard_sk(&seed, 0),
            &test_keys::account_xpub(&seed, 0),
            0,
        )
        .unwrap();
        let (_, container) = Ufvk::decode(&encoded).unwrap();
        let mut kinds = container
            .items()
            .into_iter()
            .map(|item| match item {
                Fvk::P2pkh(_) => "p2pkh",
                Fvk::Orchard(_) => "orchard",
                Fvk::Sapling(_) => "sapling",
                _ => "other",
            })
            .collect::<Vec<_>>();
        kinds.sort_unstable();
        assert_eq!(kinds, vec!["orchard", "p2pkh"]);
        assert!(UnifiedFullViewingKey::decode(&Network::MainNetwork, &encoded).is_ok());
    }

    #[test]
    fn transparent_address_from_ufvk_is_t_prefixed() {
        let ufvk = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).unwrap();
        let t = transparent_address("test", &ufvk).unwrap();
        assert!(t.starts_with("tm"), "expected a tm t-address, got {t}");
        let ua = unified_address_with("test", &ufvk, "all").unwrap();
        assert!(ua.starts_with("utest"), "UA must start with utest");
        assert_ne!(t, ua, "t-address and UA must differ");
    }

    #[test]
    fn no_address_policy_includes_a_sapling_receiver() {
        use zcash_address::unified::{Address, Container, Encoding, Receiver};

        for network in ["main", "test"] {
            let ufvk = ufvk_from_mnemonic(network, TEST_MNEMONIC, 0).unwrap();
            for policy in ["all", "shielded", "orchard"] {
                let ua = unified_address_with(network, &ufvk, policy).unwrap();
                let (_, address) = Address::decode(&ua).unwrap();
                let receivers = address.items();
                assert!(
                    !receivers.iter().any(|r| matches!(r, Receiver::Sapling(_))),
                    "{network}/{policy} must not contain a Sapling receiver"
                );
                assert!(receivers.iter().any(|r| matches!(r, Receiver::Orchard(_))));
                assert_eq!(
                    receivers.iter().any(|r| matches!(r, Receiver::P2pkh(_))),
                    policy == "all",
                    "{network}/{policy}"
                );
            }
            // The transparent receiver in "all" must be the standalone t-address (/0/0).
            let params = parse_network(network).unwrap();
            let all = unified_address_with(network, &ufvk, "all").unwrap();
            let Some(zcash_keys::address::Address::Unified(all)) =
                zcash_keys::address::Address::decode(&params, &all)
            else {
                panic!("{network}: expected a UA");
            };
            assert_eq!(
                zcash_keys::encoding::encode_transparent_address_p(
                    &params,
                    all.transparent().unwrap()
                ),
                transparent_address(network, &ufvk).unwrap()
            );
        }
    }

    // Public test vector; never use for real funds.
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
            "compressed key prefix must be 02/03"
        );
    }

    /// An omitted or empty receiver policy must not default to `"all"`.
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
    fn derives_ufvk_and_address() {
        let ufvk = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).expect("derive UFVK");
        assert!(
            ufvk.starts_with("uview"),
            "testnet UFVK must start with uview, got {ufvk}"
        );

        let addr = unified_address_with("test", &ufvk, "all").expect("derive address");
        assert!(
            addr.starts_with("utest"),
            "testnet UA must start with utest, got {addr}"
        );
    }

    #[test]
    fn different_accounts_differ() {
        let a = ufvk_from_mnemonic("test", TEST_MNEMONIC, 0).unwrap();
        let b = ufvk_from_mnemonic("test", TEST_MNEMONIC, 1).unwrap();
        assert_ne!(a, b, "different accounts must derive different UFVKs");
    }
}

#[cfg(test)]
mod ua_inspect {
    fn receivers(ua: &str) -> Vec<String> {
        use zcash_address::unified::{Container, Encoding};
        let (_, parsed) = zcash_address::unified::Address::decode(ua).expect("decode UA");
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

    /// Print receiving addresses for a mnemonic, for manual testing (e.g. faucet
    /// funds). Skipped unless `ZR_MNEMONIC` is set.
    ///
    /// Also prints each policy's receivers, since an unspendable receiver in a
    /// receiving address would strand incoming funds.
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
            super::ufvk_from_mnemonic(&network, mnemonic.trim(), account).expect("derive UFVK");

        println!("network      {network}");
        println!(
            "t-addr       {}",
            super::transparent_address(&network, &ufvk).expect("t-address")
        );
        for policy in ["all", "shielded", "orchard"] {
            let ua = super::unified_address_with(&network, &ufvk, policy).expect("UA");
            println!("UA[{policy}]  {ua}");
            println!("             receivers = {:?}", receivers(&ua));
        }
    }

    /// Decode a UFVK (uview...) and list its key items. Differing item sets mean
    /// an encoding difference; same items with different bytes mean a derivation
    /// difference.
    #[test]
    fn inspect_external_ufvk() {
        let s = std::env::var("ZR_UFVK").unwrap_or_default();
        if s.is_empty() {
            return;
        }
        use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
        let hex = |b: &[u8]| b.iter().map(|x| format!("{x:02x}")).collect::<String>();
        let (net, parsed) = Ufvk::decode(&s).expect("decode UFVK");
        println!("network: {net:?}");
        for item in parsed.items() {
            match &item {
                Fvk::Orchard(d) => println!("  orchard          {}", hex(d)),
                Fvk::Sapling(d) => println!("  sapling          {}", hex(d)),
                Fvk::P2pkh(d) => println!("  transparent-p2pkh {}", hex(d)),
                other => println!("  other key item: {other:?}"),
            }
        }
    }

    /// Decode a UA and list its receivers, e.g. to inspect other wallets' addresses.
    #[test]
    fn inspect_external_ua() {
        let ua = std::env::var("ZR_UA").unwrap_or_default();
        if ua.is_empty() {
            return;
        }
        use zcash_address::unified::{Container, Encoding};
        let hex = |b: &[u8]| b.iter().map(|x| format!("{x:02x}")).collect::<String>();
        let (net, parsed) = zcash_address::unified::Address::decode(&ua).expect("decode UA");
        println!("network: {net:?}");
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
                other => println!("  other receiver: {other:?}"),
            }
        }
    }
}
