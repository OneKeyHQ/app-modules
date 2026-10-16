//! Account-scoped software keys supplied by the host.
//!
//! The host derives the ZIP-32 Orchard account spending key (`m/32'/133'/account'`)
//! and the BIP-44 transparent account key (`m/44'/133'/account'`) from its seed;
//! no seed or mnemonic crosses this boundary. Mainnet only.

use std::ops::Deref;

use orchard::keys::{FullViewingKey, SpendingKey};
use serde_json::json;
use transparent::keys::{AccountPrivKey, AccountPubKey};
use zcash_protocol::consensus::{Network, Parameters};
use zeroize::Zeroizing;
use zip32::AccountId;

use crate::{
    error::{ErrorCode, KeysError, Result},
    keys, review, sign, transparent_send,
};

/// Upstream key types that can be wiped in place.
///
/// # Safety
///
/// Implementors must be flat: no drop glue, pointers, or niches, so all-zero bytes
/// are a valid value. `SpendingKey` is `[u8; 32]`; `AccountPrivKey` is a bip32
/// `ExtendedPrivateKey` of `secp256k1::SecretKey([u8; 32])` and plain attributes.
pub(crate) unsafe trait FlatKey {}
unsafe impl FlatKey for SpendingKey {}
unsafe impl FlatKey for AccountPrivKey {}

/// Wipes an upstream key that lacks `ZeroizeOnDrop` when dropped. Copies the
/// compiler leaves in moved-from stack slots are outside its reach.
pub(crate) struct Wiped<T: FlatKey>(T);

impl<T: FlatKey> Wiped<T> {
    pub(crate) fn new(key: T) -> Self {
        const { assert!(!std::mem::needs_drop::<T>()) };
        Self(key)
    }
}

impl<T: FlatKey> Deref for Wiped<T> {
    type Target = T;

    fn deref(&self) -> &T {
        &self.0
    }
}

impl<T: FlatKey> Drop for Wiped<T> {
    fn drop(&mut self) {
        // SAFETY: `FlatKey` types are flat, and `new` rejects drop glue.
        unsafe { zeroize::zeroize_flat_type(&mut self.0) }
    }
}

/// Spending keys for one account, wiped on drop. Only the xprv carries account
/// metadata; `review::verify_signer` binds both keys to the reviewed UFVK.
pub struct AccountSigningKeys {
    params: Network,
    orchard: Wiped<SpendingKey>,
    transparent: Wiped<AccountPrivKey>,
}

impl AccountSigningKeys {
    pub fn parse(
        network: &str,
        orchard_sk: &[u8],
        account_xprv: &str,
        account_index: u32,
    ) -> Result<Self> {
        let params = parse_mainnet(network)?;
        account_id(account_index)?;
        let orchard = parse_orchard_sk(orchard_sk)?;
        let transparent = transparent_send::parse_account_xprv(account_xprv, account_index)?;
        Ok(Self {
            params,
            orchard,
            transparent,
        })
    }

    pub fn params(&self) -> Network {
        self.params
    }

    pub(crate) fn orchard(&self) -> &SpendingKey {
        &self.orchard
    }

    pub(crate) fn transparent(&self) -> &AccountPrivKey {
        &self.transparent
    }

    /// The UFVK these keys control, encoded exactly like `ufvkFromAccountKeys`.
    pub fn ufvk(&self) -> Result<String> {
        ufvk_from_parts(
            self.params,
            &FullViewingKey::from(&*self.orchard),
            &self.transparent.to_account_pubkey(),
        )
    }
}

/// The account-key APIs reject testnet.
fn parse_mainnet(network: &str) -> Result<Network> {
    match keys::parse_network(network)? {
        Network::MainNetwork => Ok(Network::MainNetwork),
        Network::TestNetwork => Err(KeysError::with(
            ErrorCode::InvalidNetwork,
            json!({ "value": network, "supported": ["main"] }),
        )),
    }
}

fn account_id(account_index: u32) -> Result<AccountId> {
    AccountId::try_from(account_index).map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            json!({ "value": account_index }),
        )
    })
}

/// Parse a raw 32-byte Orchard spending key, rejecting keys upstream deems invalid.
pub(crate) fn parse_orchard_sk(bytes: &[u8]) -> Result<Wiped<SpendingKey>> {
    let bytes: Zeroizing<[u8; 32]> = Zeroizing::new(bytes.try_into().map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidSpendingKey,
            json!({ "byteLen": bytes.len(), "expected": 32 }),
        )
    })?);
    Option::from(SpendingKey::from_bytes(*bytes))
        .map(Wiped::new)
        .ok_or_else(|| KeysError::new(ErrorCode::InvalidSpendingKey))
}

/// Transparent + Orchard UFVK, the same items the OneKey firmware returns.
///
/// Sapling is left out: this runtime can neither scan for nor spend it, and the
/// firmware rejects PCZTs that carry it. Ironwood keys derive from the Orchard item.
pub(crate) fn ufvk_from_parts(
    params: Network,
    fvk: &FullViewingKey,
    account: &AccountPubKey,
) -> Result<String> {
    use zcash_address::unified::{Encoding, Fvk, Ufvk};
    let p2pkh: [u8; 65] = account
        .serialize()
        .try_into()
        .map_err(|_| KeysError::new(ErrorCode::DeriveFailed))?;
    let container = Ufvk::try_from_items(vec![Fvk::P2pkh(p2pkh), Fvk::Orchard(fvk.to_bytes())])
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(format!("{e:?}")))?;
    Ok(container.encode(&params.network_type()))
}

/// UFVK from the Orchard account spending key and the transparent account xpub.
///
/// Account setup needs no transparent secret: the xpub is public account material.
pub fn ufvk_from_account_keys(
    network: &str,
    orchard_sk: &[u8],
    account_xpub: &str,
    account_index: u32,
) -> Result<String> {
    let params = parse_mainnet(network)?;
    account_id(account_index)?;
    let orchard = parse_orchard_sk(orchard_sk)?;
    let transparent = transparent_send::parse_account_xpub(account_xpub, account_index)?;
    ufvk_from_parts(params, &FullViewingKey::from(&*orchard), &transparent)
}

/// Verify a PCZT against the reviewed payment and the signer's own UFVK, then sign it.
pub fn sign_reviewed_pczt(
    network: &str,
    orchard_sk: &[u8],
    account_xprv: &str,
    account_index: u32,
    pczt_bytes: &[u8],
    review_json: &str,
) -> Result<Vec<u8>> {
    let keys = AccountSigningKeys::parse(network, orchard_sk, account_xprv, account_index)?;
    review::verify_signer(&keys, pczt_bytes, review_json)?;
    sign::sign_pczt_with_account_keys(&keys, pczt_bytes)
}

/// Test-only key material derived from a seed, mirroring the host derivation.
#[cfg(test)]
pub(crate) mod test_keys {
    use super::*;
    use zcash_keys::keys::UnifiedSpendingKey;
    use zcash_protocol::consensus::MAIN_NETWORK;

    const XPRV_VERSION: [u8; 4] = [0x04, 0x88, 0xad, 0xe4];
    const XPUB_VERSION: [u8; 4] = [0x04, 0x88, 0xb2, 0x1e];

    fn blake2b_512(personal: &[u8; 16], parts: &[&[u8]]) -> [u8; 64] {
        let mut state = blake2b_simd::Params::new()
            .hash_length(64)
            .personal(personal)
            .to_state();
        for part in parts {
            state.update(part);
        }
        state.finalize().as_bytes().try_into().unwrap()
    }

    /// ZIP-32 Orchard hardened derivation, written out the way the host computes it.
    pub(crate) fn orchard_sk_at(seed: &[u8], path: &[u32]) -> [u8; 32] {
        let i = blake2b_512(b"ZcashIP32Orchard", &[seed]);
        let (mut sk, mut chain): ([u8; 32], [u8; 32]) =
            (i[..32].try_into().unwrap(), i[32..].try_into().unwrap());
        for &index in path {
            let child = (index | (1 << 31)).to_le_bytes();
            let i = blake2b_512(b"Zcash_ExpandSeed", &[&chain, &[0x81], &sk, &child]);
            sk = i[..32].try_into().unwrap();
            chain = i[32..].try_into().unwrap();
        }
        sk
    }

    /// `m/32'/133'/account'`.
    pub(crate) fn orchard_sk(seed: &[u8], account: u32) -> [u8; 32] {
        orchard_sk_at(seed, &[32, 133, account])
    }

    pub(crate) fn account_priv_key(seed: &[u8], account: u32) -> AccountPrivKey {
        AccountPrivKey::from_seed(&MAIN_NETWORK, seed, AccountId::try_from(account).unwrap())
            .unwrap()
    }

    pub(crate) fn encode_xprv(key: &AccountPrivKey) -> String {
        let mut payload = XPRV_VERSION.to_vec();
        payload.extend(key.to_bytes());
        bs58::encode(payload).with_check().into_string()
    }

    pub(crate) fn account_xprv(seed: &[u8], account: u32) -> String {
        encode_xprv(&account_priv_key(seed, account))
    }

    pub(crate) fn account_xpub(seed: &[u8], account: u32) -> String {
        let key = account_priv_key(seed, account);
        // depth || parent fingerprint || child number, then chain code || pubkey.
        let header = key.to_bytes()[..9].to_vec();
        let mut payload = XPUB_VERSION.to_vec();
        payload.extend(header);
        payload.extend(key.to_account_pubkey().serialize());
        bs58::encode(payload).with_check().into_string()
    }

    /// Oracle: the upstream seed-based USK this crate no longer accepts.
    pub(crate) fn usk(seed: &[u8], account: u32) -> UnifiedSpendingKey {
        UnifiedSpendingKey::from_seed(&MAIN_NETWORK, seed, AccountId::try_from(account).unwrap())
            .unwrap()
    }

    pub(crate) fn signing_keys(seed: &[u8], account: u32) -> AccountSigningKeys {
        AccountSigningKeys::parse(
            "main",
            &orchard_sk(seed, account),
            &account_xprv(seed, account),
            account,
        )
        .unwrap()
    }
}

#[cfg(test)]
mod tests {
    use super::test_keys::*;
    use super::*;
    use orchard::keys::{SpendAuthorizingKey, SpendValidatingKey};

    const ABANDON_MNEMONIC: &str =
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

    fn abandon_seed() -> Vec<u8> {
        bip0039::Mnemonic::<bip0039::English>::from_phrase(ABANDON_MNEMONIC)
            .unwrap()
            .to_seed("")
            .to_vec()
    }

    /// Deterministic seeds of both lengths the transparent and Orchard derivations accept.
    fn deterministic_seeds(count: u32) -> Vec<Vec<u8>> {
        (0..count)
            .map(|i| {
                // BIP-32 (transparent) accepts 16, 32 or 64 bytes; BIP-39 seeds are 64.
                let len = [64usize, 32][i as usize % 2];
                let mut seed = Vec::new();
                let mut block = 0u32;
                while seed.len() < len {
                    let hash = blake2b_simd::Params::new()
                        .hash_length(64)
                        .personal(b"OneKeyZcashSeeds")
                        .to_state()
                        .update(&i.to_le_bytes())
                        .update(&block.to_le_bytes())
                        .finalize();
                    seed.extend_from_slice(hash.as_bytes());
                    block += 1;
                }
                seed.truncate(len);
                seed
            })
            .collect()
    }

    fn assert_parity(seed: &[u8], account: u32) {
        let oracle = usk(seed, account);
        let oracle_ufvk = oracle.to_unified_full_viewing_key();
        let sk = orchard_sk(seed, account);
        assert_eq!(
            &sk,
            oracle.orchard().to_bytes(),
            "orchard sk, account {account}"
        );

        let keys = signing_keys(seed, account);
        let new_ufvk = keys.ufvk().unwrap();
        assert_eq!(
            new_ufvk,
            keys::without_sapling(Network::MainNetwork, &oracle_ufvk).unwrap(),
            "UFVK, account {account}"
        );
        assert_eq!(
            ufvk_from_account_keys("main", &sk, &account_xpub(seed, account), account).unwrap(),
            new_ufvk
        );

        let new_fvk = FullViewingKey::from(keys.orchard());
        let oracle_fvk = oracle_ufvk.orchard().unwrap().clone();
        assert_eq!(new_fvk.to_bytes(), oracle_fvk.to_bytes(), "FVK bytes");
        // ak, the SpendValidatingKey encoding, is the first 32 bytes of the FVK encoding.
        assert_eq!(new_fvk.to_bytes()[..32], oracle_fvk.to_bytes()[..32]);
        assert_eq!(
            SpendValidatingKey::from(&SpendAuthorizingKey::from(keys.orchard())),
            SpendValidatingKey::from(&SpendAuthorizingKey::from(oracle.orchard())),
            "SpendValidatingKey, account {account}"
        );
        assert_eq!(
            SpendValidatingKey::from(new_fvk),
            SpendValidatingKey::from(oracle_fvk)
        );
        assert_eq!(
            keys.transparent().to_account_pubkey().serialize(),
            oracle.transparent().to_account_pubkey().serialize()
        );
    }

    #[test]
    fn account_keys_match_the_seed_oracle_for_fixed_seeds() {
        for seed in [vec![7u8; 32], vec![0x42u8; 64], abandon_seed()] {
            for account in [0, 1, (1 << 31) - 1] {
                assert_parity(&seed, account);
                assert_eq!(
                    signing_keys(&seed, account).ufvk().unwrap(),
                    keys::ufvk_from_seed_bytes(Network::MainNetwork, &seed, account).unwrap()
                );
            }
        }
    }

    #[test]
    fn account_keys_match_the_seed_oracle_for_deterministic_seeds() {
        let seeds = deterministic_seeds(100);
        let threads = std::thread::available_parallelism().map_or(4, |n| n.get());
        std::thread::scope(|scope| {
            for chunk in seeds.chunks(seeds.len().div_ceil(threads)) {
                scope.spawn(move || {
                    for seed in chunk {
                        for account in [0, 1, (1 << 31) - 1] {
                            assert_parity(seed, account);
                        }
                    }
                });
            }
        });
    }

    /// ZIP-32 Orchard test vectors (orchard `test_vectors::zip32`, seed 0x00..0x1f):
    /// pin the hand-written derivation the host mirrors.
    #[test]
    fn orchard_derivation_matches_zip32_vectors() {
        let seed = (0u8..32).collect::<Vec<_>>();
        assert_eq!(
            hex::encode(orchard_sk_at(&seed, &[])),
            "7eee3c1017870990a3dd6891b82f80be8976c1e7dc20d60817a5e88e8b2cd4b8"
        );
        assert_eq!(
            hex::encode(orchard_sk_at(&seed, &[1, 2, 3])),
            "96439ea348a4b2ce4ec7beb4543c70274c8f76495d60c5fa5f018b68f3c32367"
        );
    }

    #[test]
    fn rejects_testnet_and_malformed_keys() {
        let seed = [7u8; 32];
        let sk = orchard_sk(&seed, 0);
        let xpub = account_xpub(&seed, 0);
        let xprv = account_xprv(&seed, 0);

        let e = ufvk_from_account_keys("test", &sk, &xpub, 0).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidNetwork);
        let e = AccountSigningKeys::parse("test", &sk, &xprv, 0)
            .err()
            .unwrap();
        assert_eq!(e.code, ErrorCode::InvalidNetwork);
        let e = sign_reviewed_pczt("test", &sk, &xprv, 0, &[], "{}").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidNetwork);

        for len in [0usize, 31, 33, 64] {
            let e = ufvk_from_account_keys("main", &vec![1u8; len], &xpub, 0).unwrap_err();
            assert_eq!(e.code, ErrorCode::InvalidSpendingKey);
            assert_eq!(e.params["byteLen"], len);
        }

        // The xpub/xprv metadata must name the requested account.
        let e = ufvk_from_account_keys("main", &sk, &xpub, 1).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidAccountXpub);
        let e = AccountSigningKeys::parse("main", &sk, &xprv, 1)
            .err()
            .unwrap();
        assert_eq!(e.code, ErrorCode::InvalidAccountXprv);
        let e = ufvk_from_account_keys("main", &sk, &xprv, 0).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidAccountXpub);
        let e = AccountSigningKeys::parse("main", &sk, &xpub, 0)
            .err()
            .unwrap();
        assert_eq!(e.code, ErrorCode::InvalidAccountXprv);

        let e = ufvk_from_account_keys("main", &sk, &xpub, 1 << 31).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidAccountIndex);
    }

    #[test]
    fn xprv_parse_matches_upstream_decoding() {
        for account in [0, 1, (1 << 31) - 1] {
            let seed = [7u8; 32];
            let parsed =
                transparent_send::parse_account_xprv(&account_xprv(&seed, account), account)
                    .unwrap();
            assert_eq!(
                parsed.to_bytes(),
                account_priv_key(&seed, account).to_bytes()
            );
        }
    }

    #[test]
    fn parsed_keys_are_wiped_on_drop() {
        let mut keys = std::mem::ManuallyDrop::new(signing_keys(&[7u8; 32], 0));
        // SAFETY: dropped once; the fields are flat, so their bytes stay readable.
        unsafe { std::mem::ManuallyDrop::drop(&mut keys) };
        assert_eq!(keys.orchard().to_bytes(), &[0u8; 32]);
        assert!(keys.transparent().to_bytes().iter().all(|&b| b == 0));
    }

    #[test]
    fn errors_never_carry_key_material() {
        let seed = [7u8; 32];
        let sk = orchard_sk(&seed, 0);
        let xprv = account_xprv(&seed, 0);
        let mut bad_checksum = xprv.clone();
        bad_checksum.pop();
        bad_checksum.push(if xprv.ends_with('1') { '2' } else { '1' });
        let secrets = [
            xprv.clone(),
            hex::encode(sk),
            hex::encode(account_priv_key(&seed, 0).to_bytes()),
        ];
        let errors = [
            sign_reviewed_pczt("main", &sk, &xprv, 0, &[], "{}").unwrap_err(),
            sign_reviewed_pczt("main", &sk, &xprv, 0, &[], "not json").unwrap_err(),
            sign_reviewed_pczt("main", &sk, &xprv, 1, &[], "{}").unwrap_err(),
            sign_reviewed_pczt("main", &sk, &bad_checksum, 0, &[], "{}").unwrap_err(),
            sign_reviewed_pczt("main", &sk[..31], &xprv, 0, &[], "{}").unwrap_err(),
            transparent_send::build_with_account_xprv_json("{}", &xprv).unwrap_err(),
        ];
        for e in errors {
            let rendered = format!("{e} {}", e.to_json());
            for secret in &secrets {
                assert!(!rendered.contains(secret.as_str()), "{rendered}");
            }
        }
    }

    /// Hardware parity vectors from the independent firmware implementation:
    /// fingerprint, Orchard FVK (96B), transparent pubkey (65B), and both receivers.
    #[test]
    fn hardware_parity_vectors() {
        use zcash_address::unified::{Container, Encoding, Fvk, Receiver, Ufvk};

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

        let seed = abandon_seed();
        let fingerprint = zip32::fingerprint::SeedFingerprint::from_seed(&seed).unwrap();
        assert_eq!(hex::encode(fingerprint.to_bytes()), FINGERPRINT);

        let fvk_items = |s: &str| {
            let (_, u) = Ufvk::decode(s).expect("decode UFVK");
            let mut orchard = None;
            let mut p2pkh = None;
            for item in u.items() {
                match item {
                    Fvk::Orchard(d) => orchard = Some(d.to_vec()),
                    Fvk::P2pkh(d) => p2pkh = Some(d.to_vec()),
                    _ => {}
                }
            }
            (orchard.expect("orchard item"), p2pkh.expect("p2pkh item"))
        };
        let ua_receivers = |s: &str| {
            let (_, a) = zcash_address::unified::Address::decode(s).expect("decode UA");
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
                orchard.expect("orchard receiver"),
                p2pkh.expect("p2pkh receiver"),
            )
        };

        for (account, hw_ufvk, hw_ua) in HW {
            let ours = ufvk_from_account_keys(
                "main",
                &orchard_sk(&seed, account),
                &account_xpub(&seed, account),
                account,
            )
            .unwrap();
            assert_eq!(fvk_items(&ours), fvk_items(hw_ufvk), "account {account}");
            let our_ua = keys::unified_address_with("main", &ours, "all").unwrap();
            assert_eq!(
                ua_receivers(&our_ua),
                ua_receivers(hw_ua),
                "account {account}"
            );
        }
    }
}
