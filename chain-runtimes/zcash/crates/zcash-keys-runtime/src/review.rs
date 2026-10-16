use pczt::roles::verifier::{OrchardError, Verifier};
use serde::Deserialize;
use zcash_keys::{address::Address, keys::UnifiedFullViewingKey};
use zcash_note_encryption::Domain;
use zcash_protocol::memo::MemoBytes;
use zcash_script::script::Evaluable;
use zip32::Scope;

use crate::{
    account_keys::AccountSigningKeys,
    error::{ErrorCode, KeysError, Result},
    keys,
};

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct ReviewRequest {
    pub network: String,
    pub ufvk: String,
    pub to_address: String,
    pub amount_zat: String,
    pub fee_zat: String,
    pub spend_source: String,
    pub is_max_send: bool,
    pub memo_hex: Option<String>,
}

fn mismatch(field: &str) -> KeysError {
    KeysError::with(
        ErrorCode::InvalidTransactionRequest,
        serde_json::json!({"field": field, "reason": "transactionDoesNotMatchReview"}),
    )
}

fn amount(value: &str) -> Result<u64> {
    let parsed = value.parse::<u64>().map_err(|_| mismatch("amount"))?;
    if parsed.to_string() != value || parsed > 2_100_000_000_000_000 {
        return Err(mismatch("amount"));
    }
    Ok(parsed)
}

pub fn parse_request(json: &str) -> Result<ReviewRequest> {
    serde_json::from_str(json).map_err(|_| mismatch("reviewRequest"))
}

/// Inspect actual effects and note ciphertexts; UI metadata is not authoritative.
pub fn verify(pczt_bytes: &[u8], request: &ReviewRequest) -> Result<()> {
    if pczt_bytes.len() > 8 * 1024 * 1024 {
        return Err(mismatch("pcztSize"));
    }
    let params = keys::parse_network(&request.network)?;
    let ufvk =
        UnifiedFullViewingKey::decode(&params, &request.ufvk).map_err(|_| mismatch("ufvk"))?;
    let fvk = ufvk.orchard().ok_or_else(|| mismatch("ufvk"))?;
    let recipient = keys::supported_recipient(&params, &request.to_address)
        .ok_or_else(|| mismatch("recipient"))?;
    let recipient_orchard = match &recipient {
        Address::Unified(address) => address.orchard().copied(),
        _ => None,
    };
    let recipient_transparent = match recipient {
        Address::Transparent(address) => Some(address),
        // TEX recipients require transparent inputs, which this private path forbids.
        _ => None,
    };
    let expected_amount = amount(&request.amount_zat)?;
    let expected_fee = amount(&request.fee_zat)?;
    let expected_memo = super::transparent_send::parse_memo_hex(request.memo_hex.as_deref())?;
    if request.memo_hex.is_some() && recipient_orchard.is_none() {
        return Err(mismatch("memoRecipient"));
    }
    if expected_amount == 0 || !matches!(request.spend_source.as_str(), "orchard" | "ironwood") {
        return Err(mismatch("spendSourceOrAmount"));
    }
    let pczt = pczt::Pczt::parse(pczt_bytes).map_err(|_| mismatch("pczt"))?;
    if pczt.clone().serialize().map_err(|_| mismatch("pczt"))? != pczt_bytes {
        return Err(mismatch("nonCanonicalPczt"));
    }
    if !pczt.transparent().inputs().is_empty()
        || !pczt.sapling().spends().is_empty()
        || !pczt.sapling().outputs().is_empty()
    {
        return Err(mismatch("unsupportedInputs"));
    }
    let mut paid = 0u64;
    let mut fee = 0i128;
    for output in pczt.transparent().outputs() {
        let address = recipient_transparent.ok_or_else(|| mismatch("transparentOutput"))?;
        if *output.script_pubkey() != address.script().to_bytes() {
            return Err(mismatch("transparentRecipient"));
        }
        paid = paid
            .checked_add(*output.value())
            .ok_or_else(|| mismatch("amount"))?;
        fee -= i128::from(*output.value());
    }
    let mut real_spends = 0usize;
    let mut check_bundle = |bundle: &orchard::pczt::Bundle,
                            pool: &str|
     -> std::result::Result<(), OrchardError<KeysError>> {
        let custom = |field| OrchardError::Custom(mismatch(field));
        let (magnitude, sign) = bundle.value_sum().magnitude_sign();
        let value_sum = match sign {
            orchard::value::Sign::Positive => i128::from(magnitude),
            orchard::value::Sign::Negative => -i128::from(magnitude),
        };
        let mut actual_value_sum = 0i128;
        bundle.verify_cross_address_restriction()?;
        for action in bundle.actions() {
            let spend = action.spend();
            let output = action.output();
            let spend_value = spend.value().ok_or_else(|| custom("spendValue"))?;
            if spend_value.inner() != 0 {
                if pool != request.spend_source || spend.dummy_sk().is_some() {
                    return Err(custom("spendSource"));
                }
                real_spends += 1;
                spend.verify_nullifier(Some(fvk))?;
                spend.verify_rk(Some(fvk))?;
            }
            action.verify_cv_net()?;
            output.verify_note_commitment(spend)?;
            let value = output.value().ok_or_else(|| custom("outputValue"))?;
            actual_value_sum += i128::from(spend_value.inner()) - i128::from(value.inner());
            if value.inner() == 0 {
                continue;
            }
            let address = output
                .recipient()
                .ok_or_else(|| custom("outputRecipient"))?;
            let is_recipient = Some(address) == recipient_orchard;
            if is_recipient {
                if pool != "ironwood" {
                    return Err(custom("recipientOutputPool"));
                }
                paid = paid
                    .checked_add(value.inner())
                    .ok_or_else(|| custom("amount"))?;
            } else if request.is_max_send
                || fvk.scope_for_address(&address) != Some(Scope::Internal)
                || (pool == "orchard" && request.spend_source != "orchard")
            {
                return Err(custom("changeOwnership"));
            }
            let note = orchard::Note::from_parts(
                address,
                value,
                orchard::note::Rho::from_bytes(&spend.nullifier().to_bytes())
                    .into_option()
                    .ok_or_else(|| custom("rho"))?,
                output.rseed().ok_or_else(|| custom("rseed"))?,
                *output.note_version(),
            )
            .into_option()
            .ok_or_else(|| custom("note"))?;
            // Bind the approved memo to recipient ciphertext; change retains an empty memo.
            let memo = if is_recipient {
                *expected_memo.as_array()
            } else {
                *MemoBytes::empty().as_array()
            };
            let (ciphertext, epk) = if pool == "ironwood" {
                let encryptor =
                    orchard::note_encryption::IronwoodNoteEncryption::new(None, note, memo);
                (
                    encryptor.encrypt_note_plaintext(),
                    orchard::note_encryption::IronwoodDomain::epk_bytes(encryptor.epk()).0,
                )
            } else {
                let encryptor =
                    orchard::note_encryption::OrchardNoteEncryption::new(None, note, memo);
                (
                    encryptor.encrypt_note_plaintext(),
                    orchard::note_encryption::OrchardDomain::epk_bytes(encryptor.epk()).0,
                )
            };
            if ciphertext != output.encrypted_note().enc_ciphertext
                || epk != output.encrypted_note().epk_bytes
            {
                return Err(custom("outputCiphertext"));
            }
        }
        if actual_value_sum != value_sum {
            return Err(custom("bundleValueSum"));
        }
        fee += actual_value_sum;
        Ok(())
    };
    Verifier::new(pczt)
        .with_orchard(|bundle| check_bundle(bundle, "orchard"))
        .and_then(|verifier| verifier.with_ironwood(|bundle| check_bundle(bundle, "ironwood")))
        .map_err(|_| mismatch("shieldedEffects"))?;
    if real_spends == 0 || paid != expected_amount || fee != i128::from(expected_fee) {
        return Err(mismatch("amountOrFee"));
    }
    Ok(())
}

pub fn verify_json(pczt_bytes: &[u8], json: &str) -> Result<()> {
    verify(pczt_bytes, &parse_request(json)?)
}

/// The reviewed UFVK must be the one the signing keys control, rebuilt from those keys.
pub fn verify_signer(keys: &AccountSigningKeys, pczt_bytes: &[u8], json: &str) -> Result<()> {
    let request = parse_request(json)?;
    if keys::parse_network(&request.network)? != keys.params() || keys.ufvk()? != request.ufvk {
        return Err(mismatch("signerAccount"));
    }
    verify(pczt_bytes, &request)
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::account_keys::{self, test_keys};
    use pczt::roles::{
        creator::Creator,
        signer::{extract_orchard_spend_auth_signatures, Signer},
    };
    use zcash_primitives::transaction::{
        builder::{BuildConfig, Builder, BundlePadding},
        fees::fixed::FeeRule,
    };
    use zcash_protocol::consensus::Network;
    use zcash_protocol::value::Zatoshis;

    fn fixture(source: &str, max: bool, foreign_change: bool) -> (Vec<u8>, ReviewRequest) {
        fixture_with_memo(source, max, foreign_change, None)
    }

    fn fixture_with_memo(
        source: &str,
        max: bool,
        foreign_change: bool,
        memo_hex: Option<String>,
    ) -> (Vec<u8>, ReviewRequest) {
        let ufvk_string = test_keys::signing_keys(&[7; 32], 0).ufvk().unwrap();
        let ufvk = UnifiedFullViewingKey::decode(&Network::MainNetwork, &ufvk_string).unwrap();
        let fvk = ufvk.orchard().unwrap().clone();
        let other = test_keys::signing_keys(&[8; 32], 0).ufvk().unwrap();
        let other = UnifiedFullViewingKey::decode(&Network::MainNetwork, &other).unwrap();
        let receiver = other.orchard().unwrap().address_at(0u32, Scope::External);
        let to_address =
            keys::unified_address_with("main", &other.encode(&Network::MainNetwork), "orchard")
                .unwrap();
        let rho = orchard::note::Rho::from_bytes(&[1; 32]).unwrap();
        let rseed = (0u8..=255)
            .find_map(|byte| orchard::note::RandomSeed::from_bytes([byte; 32], &rho).into_option())
            .unwrap();
        let note = orchard::Note::from_parts(
            fvk.address_at(0u32, Scope::External),
            orchard::value::NoteValue::from_raw(100_000),
            rho,
            rseed,
            if source == "ironwood" {
                orchard::note::NoteVersion::V3
            } else {
                orchard::note::NoteVersion::V2
            },
        )
        .unwrap();
        let zero = orchard::tree::MerkleHashOrchard::from_bytes(&[0; 32]).unwrap();
        let path = orchard::tree::MerklePath::from_parts(0, [zero; 32]);
        let anchor = path.root(note.commitment().into());
        let mut builder = Builder::new(
            Network::MainNetwork,
            3_500_000u32.into(),
            BuildConfig::Standard {
                sapling_anchor: None,
                orchard_anchor: (source == "orchard").then_some(anchor),
                ironwood_anchor: Some(if source == "ironwood" {
                    anchor
                } else {
                    orchard::Anchor::empty_tree()
                }),
                orchard_padding: BundlePadding::DEFAULT,
                ironwood_padding: BundlePadding::DEFAULT,
            },
        );
        if source == "orchard" {
            builder
                .add_orchard_spend::<std::convert::Infallible>(fvk.clone(), note, path)
                .unwrap();
        } else {
            builder
                .add_ironwood_spend::<std::convert::Infallible>(fvk.clone(), note, path)
                .unwrap();
        }
        let amount = if max { 80_000 } else { 50_000 };
        builder
            .add_ironwood_output::<std::convert::Infallible>(
                None,
                receiver,
                Zatoshis::const_from_u64(amount),
                super::super::transparent_send::parse_memo_hex(memo_hex.as_deref()).unwrap(),
            )
            .unwrap();
        if !max {
            let change_fvk = if foreign_change {
                other.orchard().unwrap().clone()
            } else {
                fvk
            };
            let address = change_fvk.address_at(0u32, Scope::Internal);
            if source == "orchard" {
                builder
                    .add_orchard_change_output::<std::convert::Infallible>(
                        change_fvk,
                        None,
                        address,
                        Zatoshis::const_from_u64(30_000),
                        MemoBytes::empty(),
                    )
                    .unwrap();
            } else {
                builder
                    .add_ironwood_output::<std::convert::Infallible>(
                        None,
                        address,
                        Zatoshis::const_from_u64(30_000),
                        MemoBytes::empty(),
                    )
                    .unwrap();
            }
        }
        let built = builder
            .build_for_pczt(
                rand::rngs::OsRng,
                &FeeRule::non_standard(Zatoshis::const_from_u64(20_000)),
            )
            .unwrap();
        let pczt = Creator::build_from_parts(built.pczt_parts)
            .unwrap()
            .serialize()
            .unwrap();
        (
            pczt,
            ReviewRequest {
                network: "main".into(),
                ufvk: ufvk_string,
                to_address,
                amount_zat: amount.to_string(),
                fee_zat: "20000".into(),
                spend_source: source.into(),
                is_max_send: max,
                memo_hex,
            },
        )
    }

    #[test]
    fn text_and_binary_memo_must_match_recipient_ciphertext() {
        for source in ["orchard", "ironwood"] {
            for max in [false, true] {
                for prefix in ["e4b8ade6968720f09f9982", "ff000102"] {
                    let memo = format!("{prefix}{}", "00".repeat(512 - prefix.len() / 2));
                    let (bytes, mut request) = fixture_with_memo(source, max, false, Some(memo));
                    verify(&bytes, &request).unwrap();
                    request.memo_hex = None;
                    assert!(verify(&bytes, &request).is_err());
                    request.memo_hex = Some(format!("ff99{}", "00".repeat(510)));
                    assert!(verify(&bytes, &request).is_err());
                }
            }
        }
    }

    #[test]
    fn both_pools_and_max_verify_with_owned_change() {
        for source in ["orchard", "ironwood"] {
            for max in [false, true] {
                let (bytes, request) = fixture(source, max, false);
                verify(&bytes, &request).unwrap();
            }
        }
    }

    #[test]
    fn display_intent_cannot_replace_actual_effects() {
        for field in [
            "recipient",
            "amount",
            "fee",
            "source",
            "max",
            "ufvk",
            "network",
        ] {
            let (bytes, mut request) = fixture("orchard", false, false);
            match field {
                "recipient" => {
                    request.to_address =
                        keys::unified_address_with("main", &request.ufvk, "orchard").unwrap()
                }
                "amount" => request.amount_zat = "49999".into(),
                "fee" => request.fee_zat = "19999".into(),
                "source" => request.spend_source = "ironwood".into(),
                "max" => request.is_max_send = true,
                "ufvk" => request.ufvk = test_keys::signing_keys(&[9; 32], 0).ufvk().unwrap(),
                "network" => request.network = "test".into(),
                _ => unreachable!(),
            }
            assert!(verify(&bytes, &request).is_err(), "{field}");
        }
    }

    #[test]
    fn change_cannot_be_redirected_to_another_wallet() {
        for source in ["orchard", "ironwood"] {
            let (bytes, request) = fixture(source, false, true);
            assert!(verify(&bytes, &request).is_err());
        }
    }

    #[test]
    fn suffix_and_noncanonical_amounts_are_rejected() {
        let (mut bytes, request) = fixture("ironwood", true, false);
        bytes.push(0);
        assert!(verify(&bytes, &request).is_err());
        for invalid in ["080000", "-1", "8e4", "2100000000000001"] {
            let (bytes, mut request) = fixture("ironwood", true, false);
            request.amount_zat = invalid.into();
            assert!(verify(&bytes, &request).is_err());
        }
    }

    fn request_json(request: &ReviewRequest) -> String {
        serde_json::json!({"network":request.network,"ufvk":request.ufvk,"toAddress":request.to_address,"amountZat":request.amount_zat,"feeZat":request.fee_zat,"spendSource":request.spend_source,"isMaxSend":request.is_max_send,"memoHex":request.memo_hex}).to_string()
    }

    fn sign_with(seed: &[u8], account: u32, bytes: &[u8], json: &str) -> Result<Vec<u8>> {
        account_keys::sign_reviewed_pczt(
            "main",
            &test_keys::orchard_sk(seed, account),
            &test_keys::account_xprv(seed, account),
            account,
            bytes,
            json,
        )
    }

    fn signer_account_mismatch(result: Result<Vec<u8>>) -> bool {
        matches!(result, Err(e) if e.params["field"] == "signerAccount")
    }

    #[test]
    fn signer_must_own_the_reviewed_ufvk() {
        let (bytes, request) = fixture("orchard", false, false);
        let json = request_json(&request);
        let seed = [7u8; 32];
        verify_signer(&test_keys::signing_keys(&seed, 0), &bytes, &json).unwrap();
        assert!(verify_signer(&test_keys::signing_keys(&[8; 32], 0), &bytes, &json).is_err());
        assert!(verify_signer(&test_keys::signing_keys(&seed, 1), &bytes, &json).is_err());

        assert!(signer_account_mismatch(sign_with(
            &[8; 32], 0, &bytes, &json
        )));
        // Wrong-account keys, each self-consistent with their own account index.
        for account in [1, (1 << 31) - 1] {
            assert!(signer_account_mismatch(sign_with(
                &seed, account, &bytes, &json
            )));
        }
        // Mixed accounts: the Orchard key of account 0 with the transparent key of account 1.
        for (orchard_account, transparent_account) in [(0, 1), (1, 0)] {
            let mixed = account_keys::AccountSigningKeys::parse(
                "main",
                &test_keys::orchard_sk(&seed, orchard_account),
                &test_keys::account_xprv(&seed, transparent_account),
                transparent_account,
            )
            .unwrap();
            assert!(verify_signer(&mixed, &bytes, &json).is_err());
        }
        // Another wallet's Orchard key paired with this wallet's transparent key.
        let foreign = account_keys::AccountSigningKeys::parse(
            "main",
            &test_keys::orchard_sk(&[8; 32], 0),
            &test_keys::account_xprv(&seed, 0),
            0,
        )
        .unwrap();
        assert!(verify_signer(&foreign, &bytes, &json).is_err());
    }

    #[test]
    fn signer_rejects_testnet_keys_and_reviews() {
        let (bytes, request) = fixture("orchard", false, false);
        let seed = [7u8; 32];
        let e = account_keys::sign_reviewed_pczt(
            "test",
            &test_keys::orchard_sk(&seed, 0),
            &test_keys::account_xprv(&seed, 0),
            0,
            &bytes,
            &request_json(&request),
        )
        .unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidNetwork);

        let mut testnet = request;
        testnet.network = "test".into();
        assert!(signer_account_mismatch(sign_with(
            &seed,
            0,
            &bytes,
            &request_json(&testnet)
        )));
    }

    /// Re-applies each spend authorization signature to the unsigned PCZT, where
    /// upstream verifies it against the action's randomized key `rk`. Returns the
    /// signed (pool, action) positions.
    fn verified_signatures(unsigned: &[u8], signed: &pczt::Pczt) -> Vec<(bool, usize)> {
        let signatures = extract_orchard_spend_auth_signatures(signed);
        let mut signer = Signer::new(pczt::Pczt::parse(unsigned).unwrap()).unwrap();
        for signature in &signatures {
            signer
                .apply_orchard_spend_auth_signature(signature)
                .expect("signature verifies under rk");
        }
        signatures
            .iter()
            .map(|s| {
                (
                    s.value_pool() == orchard::ValuePool::Ironwood,
                    s.action_index(),
                )
            })
            .collect()
    }

    /// Account keys and the upstream seed-derived USK authorize the same spends.
    #[test]
    fn account_keys_and_seed_oracle_sign_the_same_reviewed_pczt() {
        let seed = [7u8; 32];
        for source in ["orchard", "ironwood"] {
            for max in [false, true] {
                let (bytes, request) = fixture(source, max, false);
                let json = request_json(&request);

                let signed =
                    pczt::Pczt::parse(&sign_with(&seed, 0, &bytes, &json).unwrap()).unwrap();
                let by_account_keys = verified_signatures(&bytes, &signed);
                assert!(!by_account_keys.is_empty());
                assert!(by_account_keys
                    .iter()
                    .all(|(ironwood, _)| *ironwood == (source == "ironwood")));

                let oracle = test_keys::usk(&seed, 0);
                let ask = orchard::keys::SpendAuthorizingKey::from(oracle.orchard());
                let unsigned = pczt::Pczt::parse(&bytes).unwrap();
                let (orchard_actions, ironwood_actions) = (
                    unsigned.orchard().actions().len(),
                    unsigned.ironwood().actions().len(),
                );
                let mut signer = Signer::new(unsigned).unwrap();
                for index in 0..orchard_actions {
                    let _ = signer.sign_orchard(index, &ask);
                }
                for index in 0..ironwood_actions {
                    let _ = signer.sign_ironwood(index, &ask);
                }
                assert_eq!(
                    verified_signatures(&bytes, &signer.finish()),
                    by_account_keys,
                    "{source} max={max}"
                );
            }
        }
    }

    #[test]
    fn altered_recipient_ciphertext_is_rejected_even_with_unchanged_metadata() {
        let (mut bytes, request) = fixture("ironwood", true, false);
        let mut ciphertext = None;
        Verifier::new(pczt::Pczt::parse(&bytes).unwrap())
            .with_ironwood::<std::convert::Infallible, _>(|bundle| {
                ciphertext = bundle
                    .actions()
                    .iter()
                    .find(|action| action.output().value().unwrap().inner() > 0)
                    .map(|action| action.output().encrypted_note().enc_ciphertext);
                Ok(())
            })
            .unwrap();
        let ciphertext = ciphertext.unwrap();
        let position = bytes
            .windows(ciphertext.len())
            .position(|part| part == ciphertext)
            .unwrap();
        bytes[position] ^= 1;
        assert!(
            pczt::Pczt::parse(&bytes).is_ok(),
            "attack retains a structurally valid PCZT"
        );
        assert!(verify(&bytes, &request).is_err());
    }
}
