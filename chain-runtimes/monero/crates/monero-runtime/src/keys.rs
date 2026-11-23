//! View keys and address derivation.
//!
//! Holds the public spend key and private view key (monero-oxide `ViewPair`).
//! The private spend key is never stored; signing takes the BIP-32 key per call.

use monero_wallet::{
    address::{MoneroAddress, Network, SubaddressIndex},
    ed25519::{CompressedPoint, Scalar},
    ViewPair,
};
use serde_json::json;
use zeroize::{Zeroize, Zeroizing};

use crate::error::{ErrorCode, Result, RuntimeError};

/// Parses a network name. No default: a wrong guess sends funds to an address nobody controls.
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

/// Builds a view pair from a compressed ed25519 spend point and a canonical view scalar.
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

    // Upstream rejects spend keys with torsion.
    ViewPair::new(spend, Zeroizing::new(view)).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPublicKey" }),
        )
        .detail(format!("{e:?}"))
    })
}

/// Primary (standard) address.
pub fn primary_address(pair: &ViewPair, network: Network) -> String {
    pair.legacy_address(network).to_string()
}

/// Subaddress. `(0, 0)` is the primary address and is rejected, or the scanner would never match the entry.
pub fn subaddress(pair: &ViewPair, network: Network, major: u32, minor: u32) -> Result<String> {
    let index = SubaddressIndex::new(major, minor).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::InvalidSubaddressIndex,
            json!({ "major": major, "minor": minor, "reason": "zeroZeroIsThePrimaryAddress" }),
        )
    })?;
    Ok(pair.subaddress(network, index).to_string())
}

/// Whether `encoded` is a valid address on `network`.
pub fn validate_address(network: Network, encoded: &str) -> bool {
    MoneroAddress::from_str(network, encoded).is_ok()
}

/// Parses a recipient address; the error names the expected network.
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

    /// A public address outside upstream test vectors; only exercises parsing.
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
        assert_eq!(parse_network("main").unwrap(), Network::Mainnet);
    }

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

    /// `ViewPair` lacks `Debug`, so `unwrap_err` is unavailable.
    fn expect_key_error(spend: &[u8], view: &[u8]) -> RuntimeError {
        match view_pair_from_bytes(spend, view) {
            Ok(_) => panic!("key material should have been rejected"),
            Err(e) => e,
        }
    }

    #[test]
    fn key_material_length_is_enforced() {
        let e = expect_key_error(&[0u8; 31], &[0u8; 32]);
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["byteLen"], 31);
    }

    #[test]
    fn non_curve_spend_key_is_rejected() {
        let mut not_a_point = [0u8; 32];
        not_a_point[0] = 2; // does not decompress
        let e = expect_key_error(&not_a_point, &[1u8; 32]);
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
    }

    #[test]
    fn subaddress_zero_zero_is_refused() {
        let pair = test_pair();
        let e = subaddress(&pair, Network::Mainnet, 0, 0).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidSubaddressIndex);
        assert!(subaddress(&pair, Network::Mainnet, 0, 1).is_ok());
        assert!(subaddress(&pair, Network::Mainnet, 1, 0).is_ok());
    }

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
        // The spend key and view scalar need not match; this tests derivation and encoding.
        let addr = MoneroAddress::from_str(Network::Mainnet, MAINNET_ADDR).unwrap();
        let spend = addr.spend().compress().to_bytes();
        let mut view = [0u8; 32];
        view[0] = 9; // small, hence canonical
        match view_pair_from_bytes(&spend, &view) {
            Ok(pair) => pair,
            Err(e) => panic!("failed to build test key pair: {e}"),
        }
    }
}

/// Derives Monero keys from a 32-byte BIP-32 private key.
///
/// ```text
/// spend_secret = sc_reduce32(bip32_private_key)
/// view_secret   = sc_reduce32(keccak256(spend_secret))
/// ```
///
/// Do not change it: OneKey V4 users would see an empty wallet for the same mnemonic.
/// Returns view material and the primary address, never the spend key.
pub fn derive_from_bip32_key(network: Network, bip32_private_key: &[u8]) -> Result<DerivedKeys> {
    let derived = derive_keys_in_frame(network, bip32_private_key);
    scrub_stack();
    derived
}

// Separate frame so `scrub_stack` can overwrite its by-value key copies.
#[inline(never)]
fn derive_keys_in_frame(network: Network, bip32_private_key: &[u8]) -> Result<DerivedKeys> {
    use monero_wallet::ed25519::Point;

    let raw = secret_32(bip32_private_key, "bip32PrivateKey")?;

    let spend = Zeroizing::new(Scalar::from(
        curve25519_dalek::Scalar::from_bytes_mod_order(*raw),
    ));
    let view = Zeroizing::new(Scalar::hash(<[u8; 32]>::from(*spend)));

    let spend_public =
        Point::from((*spend).into() * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);

    let pair = ViewPair::new(spend_public, Zeroizing::new(*view)).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "bip32PrivateKey" }),
        )
        .detail(format!("{e:?}"))
    })?;

    Ok(DerivedKeys {
        spend_public_key: spend_public.compress().to_bytes(),
        view_private_key: <[u8; 32]>::from(*view),
        primary_address: primary_address(&pair, network),
    })
}

pub struct DerivedKeys {
    pub spend_public_key: [u8; 32],
    pub view_private_key: [u8; 32],
    pub primary_address: String,
}

/// Spend key scalar for signing; same reduction as [`derive_from_bip32_key`].
///
/// Stays inside the runtime, so the spend key never crosses the wasm boundary.
pub fn spend_scalar_from_bip32_key(bip32_private_key: &[u8]) -> Result<Zeroizing<[u8; 32]>> {
    let spend = reduce_spend_key_in_frame(bip32_private_key);
    scrub_stack();
    spend
}

#[inline(never)]
fn reduce_spend_key_in_frame(bip32_private_key: &[u8]) -> Result<Zeroizing<[u8; 32]>> {
    let raw = secret_32(bip32_private_key, "bip32PrivateKey")?;
    let spend = Zeroizing::new(Scalar::from(
        curve25519_dalek::Scalar::from_bytes_mod_order(*raw),
    ));
    let mut out = Zeroizing::new([0u8; 32]);
    spend
        .write(&mut out.as_mut_slice())
        .expect("a scalar is 32 bytes");
    Ok(out)
}

/// [`expect_32`] into a buffer that is wiped on drop.
fn secret_32(bytes: &[u8], field: &str) -> Result<Zeroizing<[u8; 32]>> {
    if bytes.len() != 32 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": field, "byteLen": bytes.len(), "expected": 32 }),
        ));
    }
    let mut out = Zeroizing::new([0u8; 32]);
    out.copy_from_slice(bytes);
    Ok(out)
}

/// Stack bytes overwritten by [`scrub_stack`].
const STACK_SCRUB_BYTES: usize = 64 * 1024;

/// Overwrites the stack below the caller's frame, wiping secret temporaries that
/// `Zeroizing` cannot reach. Best effort: deeper frames are not covered.
#[inline(never)]
pub(crate) fn scrub_stack() {
    let mut area = [0u8; STACK_SCRUB_BYTES];
    area.zeroize();
    core::hint::black_box(&area);
}

#[cfg(test)]
mod derive_tests {
    use super::*;

    #[test]
    fn derivation_is_deterministic_and_network_scoped() {
        let key = [3u8; 32];
        let a = derive_from_bip32_key(Network::Mainnet, &key).expect("derive");
        let b = derive_from_bip32_key(Network::Mainnet, &key).expect("derive");
        assert_eq!(a.primary_address, b.primary_address);
        assert_eq!(a.spend_public_key, b.spend_public_key);
        assert!(a.primary_address.starts_with('4'));

        let t = derive_from_bip32_key(Network::Testnet, &key).expect("derive");
        assert_ne!(t.primary_address, a.primary_address);
    }

    #[test]
    fn different_keys_give_different_addresses() {
        let a = derive_from_bip32_key(Network::Mainnet, &[1u8; 32]).unwrap();
        let b = derive_from_bip32_key(Network::Mainnet, &[2u8; 32]).unwrap();
        assert_ne!(a.primary_address, b.primary_address);
        assert_ne!(a.view_private_key, b.view_private_key);
    }

    /// V4 compatibility: a real V4 vector (`m/44'/128'/0'` key and primary address).
    #[test]
    fn the_v4_derivation_vector_still_produces_the_v4_address() {
        let key = hex::decode("8b28f5749ec99464d5a9d6527125aa8778ea017dd9e5cffaa567d2f62f2c8cbc")
            .unwrap();
        let derived =
            derive_from_bip32_key(Network::Mainnet, &key).expect("derivation should succeed");
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
        let expected = <[u8; 32]>::from(Scalar::hash(*spend));
        assert_eq!(derived.view_private_key, expected);
    }

    /// Pinned `sc_reduce32` outputs (little-endian mod l), checked against a big-integer reference.
    #[test]
    fn spend_scalar_reduction_is_pinned() {
        for (input, expected) in [
            (
                "8b28f5749ec99464d5a9d6527125aa8778ea017dd9e5cffaa567d2f62f2c8cbc",
                "5c0d65767c87ca9b9fec3252e06815a277ea017dd9e5cffaa567d2f62f2c8c0c",
            ),
            (
                "0000000000000000000000000000000000000000000000000000000000000000",
                "0000000000000000000000000000000000000000000000000000000000000000",
            ),
            (
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                "1c95988d7431ecd670cf7d73f45befc6feffffffffffffffffffffffffffff0f",
            ),
            // l - 1
            (
                "ecd3f55c1a631258d69cf7a2def9de1400000000000000000000000000000010",
                "ecd3f55c1a631258d69cf7a2def9de1400000000000000000000000000000010",
            ),
            // l
            (
                "edd3f55c1a631258d69cf7a2def9de1400000000000000000000000000000010",
                "0000000000000000000000000000000000000000000000000000000000000000",
            ),
            // l + 1
            (
                "eed3f55c1a631258d69cf7a2def9de1400000000000000000000000000000010",
                "0100000000000000000000000000000000000000000000000000000000000000",
            ),
        ] {
            let scalar = spend_scalar_from_bip32_key(&hex::decode(input).unwrap()).unwrap();
            assert_eq!(hex::encode(&scalar[..]), expected, "input {input}");
        }
    }

    #[test]
    fn spend_scalar_matches_the_reference_and_the_derived_address_for_random_keys() {
        use rand_core::RngCore;
        let mut rng = crate::spend::test_support::SeededRng::new(0x5eed);
        for _ in 0..1000 {
            let mut key = [0u8; 32];
            rng.fill_bytes(&mut key);
            let scalar = spend_scalar_from_bip32_key(&key).unwrap();
            assert_eq!(
                *scalar,
                crate::spend::test_support::reference_spend_scalar(&key)
            );
            let dalek: curve25519_dalek::Scalar =
                Scalar::read(&mut scalar.as_slice()).unwrap().into();
            let public = dalek * curve25519_dalek::constants::ED25519_BASEPOINT_POINT;
            assert_eq!(
                public.compress().to_bytes(),
                derive_from_bip32_key(Network::Mainnet, &key)
                    .unwrap()
                    .spend_public_key
            );
        }
    }

    /// Reference derivation without stack wiping.
    fn reference_derive(raw: [u8; 32]) -> ([u8; 32], [u8; 32], String) {
        use monero_wallet::ed25519::Point;
        let spend = Scalar::from(curve25519_dalek::Scalar::from_bytes_mod_order(raw));
        let view = Scalar::hash(<[u8; 32]>::from(spend));
        let spend_public =
            Point::from(spend.into() * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
        let pair = ViewPair::new(spend_public, Zeroizing::new(view)).unwrap();
        (
            spend_public.compress().to_bytes(),
            <[u8; 32]>::from(view),
            primary_address(&pair, Network::Mainnet),
        )
    }

    #[test]
    fn derivation_matches_the_reference_for_random_and_edge_keys() {
        use rand_core::RngCore;
        let mut rng = crate::spend::test_support::SeededRng::new(0xd3e1);
        let mut inputs = vec![[0xffu8; 32], [1u8; 32]];
        inputs.push(
            hex::decode("8b28f5749ec99464d5a9d6527125aa8778ea017dd9e5cffaa567d2f62f2c8cbc")
                .unwrap()
                .try_into()
                .unwrap(),
        );
        for _ in 0..300 {
            let mut key = [0u8; 32];
            rng.fill_bytes(&mut key);
            inputs.push(key);
        }
        for key in inputs {
            let derived = derive_from_bip32_key(Network::Mainnet, &key).unwrap();
            let (spend_public, view, address) = reference_derive(key);
            assert_eq!(derived.spend_public_key, spend_public);
            assert_eq!(derived.view_private_key, view);
            assert_eq!(derived.primary_address, address);
        }
    }

    #[test]
    fn key_length_is_enforced() {
        let e = derive_from_bip32_key(Network::Mainnet, &[0u8; 31])
            .err()
            .expect("wrong length must be rejected");
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
    }
}
