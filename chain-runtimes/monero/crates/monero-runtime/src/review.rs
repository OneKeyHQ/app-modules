use monero_wallet::{
    address::MoneroAddress,
    ed25519::{CompressedPoint, Point, Scalar},
    io::{read_byte, read_bytes, read_u32, read_u64, read_vec},
    send::{Eventuality, SignableTransaction},
    transaction::Transaction,
    OutputWithDecoys,
};
use serde::Deserialize;
use zeroize::Zeroizing;

use crate::{
    error::{ErrorCode, Result, RuntimeError},
    keys,
};

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct ReviewRequest {
    pub network: String,
    pub from_address: String,
    pub to_address: String,
    pub amount_atomic: String,
    pub fee_atomic: String,
    pub change_atomic: String,
    pub is_max_send: bool,
}

fn mismatch(field: &str) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        serde_json::json!({"field": field, "reason": "transactionDoesNotMatchReview"}),
    )
}

fn amount(value: &str) -> Result<u64> {
    let parsed = value.parse::<u64>().map_err(|_| mismatch("amount"))?;
    if parsed.to_string() != value {
        return Err(mismatch("amount"));
    }
    Ok(parsed)
}

pub fn parse_request(json: &str) -> Result<ReviewRequest> {
    serde_json::from_str(json).map_err(|_| mismatch("reviewRequest"))
}

pub fn verify_sender(
    request: &ReviewRequest,
    pair: &monero_wallet::ViewPair,
    network: monero_wallet::address::Network,
) -> Result<()> {
    if keys::parse_network(&request.network)? != network
        || request.from_address != keys::primary_address(pair, network)
    {
        return Err(mismatch("fromAddress"));
    }
    Ok(())
}

/// Read the pinned monero-wallet 0.2 serialization after its own strict validator.
/// Its payment fields are private, so inspection uses its public wire readers.
pub fn verify(
    signable_hex: &str,
    request: &ReviewRequest,
    spend_key: Option<&[u8]>,
) -> Result<SignableTransaction> {
    if signable_hex.len() > 8 * 1024 * 1024 {
        return Err(mismatch("signableSize"));
    }
    let bytes = Zeroizing::new(hex::decode(signable_hex).map_err(|_| mismatch("signableHex"))?);
    let mut reader = bytes.as_slice();
    let signable = SignableTransaction::read(&mut reader).map_err(|_| mismatch("signableHex"))?;
    if !reader.is_empty() {
        return Err(mismatch("trailingBytes"));
    }
    let network = keys::parse_network(&request.network)?;
    let expected_recipient = keys::parse_address(network, &request.to_address, "toAddress")?;
    let expected_sender = keys::parse_address(network, &request.from_address, "fromAddress")?;
    let expected_amount = amount(&request.amount_atomic)?;
    let expected_fee = amount(&request.fee_atomic)?;
    let expected_change = amount(&request.change_atomic)?;
    let inspect = || -> std::io::Result<(u64, u64)> {
        let mut reader = bytes.as_slice();
        read_byte(&mut reader)?;
        let _outgoing_view_key = Zeroizing::new(read_bytes::<_, 32>(&mut reader)?);
        let inputs = read_vec(OutputWithDecoys::read, Some(1024), &mut reader)?;
        let total = inputs.iter().try_fold(0u64, |sum, input| {
            sum.checked_add(input.commitment().amount)
                .ok_or_else(|| std::io::Error::other("input amount overflow"))
        })?;
        let mut paid = None;
        let mut change_seen = false;
        read_vec(
            |reader| {
                match read_byte(reader)? {
                    0 => {
                        let address_bytes =
                            read_vec(read_byte, Some(MoneroAddress::SIZE_UPPER_BOUND.0), reader)?;
                        let address =
                            String::from_utf8(address_bytes).map_err(std::io::Error::other)?;
                        let address = MoneroAddress::from_str(network, &address)
                            .map_err(std::io::Error::other)?;
                        let value = read_u64(reader)?;
                        if paid.is_some()
                            || address != expected_recipient
                            || value != expected_amount
                            || value == 0
                        {
                            return Err(std::io::Error::other("unapproved recipient or amount"));
                        }
                        paid = Some(value);
                    }
                    2 => {
                        let spend = CompressedPoint::read(reader)?.to_bytes();
                        let view = Zeroizing::new(read_bytes::<_, 32>(reader)?);
                        let pair = keys::view_pair_from_bytes(&spend, view.as_ref())
                            .map_err(|_| std::io::Error::other("invalid change keys"))?;
                        if change_seen
                            || read_u32(reader)? != 0
                            || read_u32(reader)? != 0
                            || pair.legacy_address(network) != expected_sender
                        {
                            return Err(std::io::Error::other("unapproved change"));
                        }
                        if let Some(key) = spend_key {
                            let scalar = Zeroizing::new(Scalar::read(&mut &key[..])?);
                            let public = Point::from(
                                (*scalar).into()
                                    * curve25519_dalek::constants::ED25519_BASEPOINT_POINT,
                            );
                            let private_view =
                                Zeroizing::new(Scalar::hash(<[u8; 32]>::from(*scalar)));
                            if public.compress().to_bytes() != spend
                                || <[u8; 32]>::from(*private_view) != *view
                            {
                                return Err(std::io::Error::other("change is not owned by signer"));
                            }
                        }
                        change_seen = true;
                    }
                    _ => return Err(std::io::Error::other("unsupported change type")),
                }
                Ok(())
            },
            Some(16),
            &mut reader,
        )?;
        if !change_seen || paid.is_none() {
            return Err(std::io::Error::other("missing payment or change"));
        }
        let data = read_vec(
            |reader| read_vec(read_byte, Some(255), reader),
            Some(255),
            &mut reader,
        )?;
        if !data.is_empty() {
            return Err(std::io::Error::other("unapproved transaction extra"));
        }
        Ok((total, paid.expect("checked above")))
    };
    let (inputs, paid) = inspect().map_err(|_| mismatch("paymentOrChange"))?;
    let actual_fee = signable.necessary_fee();
    let change = inputs
        .checked_sub(paid)
        .and_then(|value| value.checked_sub(actual_fee))
        .ok_or_else(|| mismatch("amounts"))?;
    if actual_fee != expected_fee
        || change != expected_change
        || (request.is_max_send && change != 0)
    {
        return Err(mismatch("feeOrChange"));
    }
    Ok(signable)
}

/// Bind the reviewed outputs to the actual signed transaction without changing
/// signing or scanning. Eventuality validates output keys, commitments and ciphertexts.
pub fn verify_signed(
    signable_hex: &str,
    request: &ReviewRequest,
    raw_hex: &str,
) -> Result<Transaction> {
    let signable = verify(signable_hex, request, None)?;
    if raw_hex.len() > 8 * 1024 * 1024 {
        return Err(mismatch("rawHex"));
    }
    let raw = hex::decode(raw_hex).map_err(|_| mismatch("rawHex"))?;
    let mut reader = raw.as_slice();
    let transaction: Transaction =
        Transaction::read(&mut reader).map_err(|_| mismatch("rawHex"))?;
    if !reader.is_empty() {
        return Err(mismatch("trailingBytes"));
    }
    let Transaction::V2 {
        proofs: Some(ref proofs),
        ..
    } = transaction
    else {
        return Err(mismatch("signedTransaction"));
    };
    if proofs.base.fee != amount(&request.fee_atomic)?
        || !Eventuality::from(signable).matches(&transaction.clone().pruned_with_prunable().0)
    {
        return Err(mismatch("signedTransaction"));
    }
    Ok(transaction)
}

#[cfg(test)]
mod tests {
    use super::*;
    use monero_wallet::{
        address::Network,
        ed25519::Commitment,
        interface::FeeRate,
        ringct::{clsag::Decoys, RctType},
        send::Change,
        ViewPair,
    };

    const RECIPIENT: &str = "4AdUndXHHZ6cfufTMvppY6JwXNouMBzSkbLYfpAV5Usx3skxNgYeYTRj5UzqtReoS44qo9mtmXCqY45DJ852K5Jv2684Rge";
    const OTHER: &str = "44AFFq5kSiGBoZ4NMDwYtN18obc8AemS33DBLWs3H7otXft3XjrpDtQGv7SqSsaBYBb98uNbr2VBBEt7f2wfn3RVGQBEP3A";
    const INPUT_AMOUNT: u64 = 2_000_000_000_000;
    const PAYMENT_AMOUNT: u64 = 1_000_000_000_000;

    fn scalar(value: u8) -> Scalar {
        let mut bytes = [0u8; 32];
        bytes[0] = value;
        Scalar::read(&mut bytes.as_slice()).unwrap()
    }

    fn fixture(extra: Vec<Vec<u8>>) -> (Vec<u8>, ReviewRequest, [u8; 32]) {
        fixture_with(extra, |pair| Change::new(pair, None))
    }

    fn review_json(review: &ReviewRequest) -> String {
        serde_json::json!({
            "network": review.network, "fromAddress": review.from_address,
            "toAddress": review.to_address, "amountAtomic": review.amount_atomic,
            "feeAtomic": review.fee_atomic, "changeAtomic": review.change_atomic,
            "isMaxSend": review.is_max_send,
        })
        .to_string()
    }

    fn signed_fixture() -> (String, ReviewRequest, String) {
        let (bytes, review, spend) = fixture(vec![]);
        let encoded = hex::encode(bytes);
        let signed = crate::spend::sign_for_host(&encoded, &spend, &review_json(&review)).unwrap();
        (
            encoded,
            review,
            signed["rawHex"].as_str().unwrap().to_owned(),
        )
    }

    #[test]
    fn signed_outputs_and_fee_must_match_the_prepared_review() {
        let (encoded, review, raw) = signed_fixture();
        let transaction = verify_signed(&encoded, &review, &raw).unwrap();
        let mut wrong_outputs = transaction.clone();
        wrong_outputs.prefix_mut().outputs.swap(0, 1);
        assert!(verify_signed(&encoded, &review, &hex::encode(wrong_outputs.serialize())).is_err());
        let mut wrong_extra = transaction.clone();
        wrong_extra.prefix_mut().extra.push(0);
        assert!(verify_signed(&encoded, &review, &hex::encode(wrong_extra.serialize())).is_err());
        let mut wrong_fee = transaction;
        if let Transaction::V2 {
            proofs: Some(ref mut proofs),
            ..
        } = wrong_fee
        {
            proofs.base.fee += 1;
        }
        assert!(verify_signed(&encoded, &review, &hex::encode(wrong_fee.serialize())).is_err());
        assert!(verify_signed(&encoded, &review, &format!("{raw}00")).is_err());
        assert!(verify_signed(&encoded, &review, "zz").is_err());
    }

    #[test]
    fn signed_multiple_inputs_match_the_prepared_review() {
        let (bytes, review, spend) = fixture_with_inputs(vec![], |pair| Change::new(pair, None), 3);
        let encoded = hex::encode(bytes);
        let signed = crate::spend::sign_for_host(&encoded, &spend, &review_json(&review)).unwrap();
        assert!(verify_signed(&encoded, &review, signed["rawHex"].as_str().unwrap()).is_ok());
    }

    #[test]
    fn broadcast_review_must_belong_to_the_local_account_and_network() {
        let (_, review, _) = fixture(vec![]);
        assert!(verify_sender(&review, &other_account(1), Network::Mainnet).is_ok());
        assert!(verify_sender(&review, &other_account(2), Network::Mainnet).is_err());
        assert!(verify_sender(&review, &other_account(1), Network::Testnet).is_err());
    }

    #[test]
    fn signed_self_payments_and_max_sends_match_the_prepared_review() {
        for (self_payment, max_send) in [(true, false), (false, true), (true, true)] {
            let (bytes, review, spend) = fixture_with_options(
                vec![],
                |pair| Change::new(pair, None),
                3,
                self_payment,
                max_send,
            );
            let encoded = hex::encode(bytes);
            let signed =
                crate::spend::sign_for_host(&encoded, &spend, &review_json(&review)).unwrap();
            let amounts = crate::spend::reviewed_broadcast_amounts(
                &encoded,
                &review,
                signed["rawHex"].as_str().unwrap(),
                None,
                None,
            )
            .unwrap();
            assert_eq!(
                amounts.change_atomic,
                Some(review.change_atomic.parse::<u64>().unwrap())
            );
            if max_send {
                assert_eq!(amounts.change_atomic, Some(0));
                assert_eq!(amounts.outgoing_atomic, Some(INPUT_AMOUNT * 3));
            }
        }
    }

    #[test]
    fn broadcast_amounts_are_derived_from_verified_outputs_not_optional_host_values() {
        let (encoded, review, raw) = signed_fixture();
        let expected = PAYMENT_AMOUNT + review.fee_atomic.parse::<u64>().unwrap();
        let amounts =
            crate::spend::reviewed_broadcast_amounts(&encoded, &review, &raw, None, None).unwrap();
        assert_eq!(amounts.outgoing_atomic, Some(expected));
        assert_eq!(
            amounts.change_atomic,
            Some(review.change_atomic.parse::<u64>().unwrap())
        );
        let outgoing = expected.to_string();
        assert!(crate::spend::reviewed_broadcast_amounts(
            &encoded,
            &review,
            &raw,
            Some(&outgoing),
            Some(&review.change_atomic)
        )
        .is_ok());
        for bad in [
            "",
            "-1",
            "+1",
            "01",
            "1.0",
            "1e3",
            "18446744073709551616",
            "1",
        ] {
            assert!(crate::spend::reviewed_broadcast_amounts(
                &encoded,
                &review,
                &raw,
                None,
                Some(bad)
            )
            .is_err());
            assert!(crate::spend::reviewed_broadcast_amounts(
                &encoded,
                &review,
                &raw,
                Some(bad),
                None
            )
            .is_err());
        }
    }

    /// A view pair for another account: spend key `value`, view key derived as OneKey does.
    fn other_account(value: u8) -> ViewPair {
        let bytes = <[u8; 32]>::from(scalar(value));
        let spend: curve25519_dalek::Scalar = scalar(value).into();
        let public = Point::from(spend * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
        ViewPair::new(public, Zeroizing::new(Scalar::hash(bytes))).unwrap()
    }

    // Public, deterministic test keys and invented outputs; no node or funds.
    // `change` picks the change destination from the sender's view pair.
    fn fixture_with(
        extra: Vec<Vec<u8>>,
        change: impl FnOnce(ViewPair) -> Change,
    ) -> (Vec<u8>, ReviewRequest, [u8; 32]) {
        fixture_with_inputs(extra, change, 1)
    }

    fn fixture_with_inputs(
        extra: Vec<Vec<u8>>,
        change: impl FnOnce(ViewPair) -> Change,
        input_count: u8,
    ) -> (Vec<u8>, ReviewRequest, [u8; 32]) {
        fixture_with_options(extra, change, input_count, false, false)
    }

    fn fixture_with_options(
        extra: Vec<Vec<u8>>,
        change: impl FnOnce(ViewPair) -> Change,
        input_count: u8,
        self_payment: bool,
        max_send: bool,
    ) -> (Vec<u8>, ReviewRequest, [u8; 32]) {
        let mut public_bytes = [0x66u8; 32];
        public_bytes[0] = 0x58;
        let public = CompressedPoint::from(public_bytes).decompress().unwrap();
        let inputs: Vec<OutputWithDecoys> = (0..input_count)
            .map(|input_index| {
                let private: curve25519_dalek::Scalar = scalar(input_index + 1).into();
                let input_public =
                    Point::from(private * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
                let commitment = Commitment::new(scalar(input_index + 1), INPUT_AMOUNT);
                let mut input_bytes = Vec::new();
                input_public.compress().write(&mut input_bytes).unwrap();
                scalar(input_index).write(&mut input_bytes).unwrap();
                commitment.write(&mut input_bytes).unwrap();
                let members = (0..16)
                    .map(|index| {
                        if index == 0 {
                            [input_public, commitment.commit()]
                        } else {
                            [
                                Point::biased_hash([index + input_index * 16; 32]),
                                Point::biased_hash([index + 100 + input_index * 16; 32]),
                            ]
                        }
                    })
                    .collect();
                Decoys::new(vec![1; 16], 0, members)
                    .unwrap()
                    .write(&mut input_bytes)
                    .unwrap();
                OutputWithDecoys::read(&mut input_bytes.as_slice()).unwrap()
            })
            .collect();
        let spend_bytes = <[u8; 32]>::from(scalar(1));
        let pair = ViewPair::new(public, Zeroizing::new(Scalar::hash(spend_bytes))).unwrap();
        let sender = pair.legacy_address(Network::Mainnet).to_string();
        let recipient = if self_payment { &sender } else { RECIPIENT };
        let change = change(pair);
        let build = |payment_amount| {
            SignableTransaction::new(
                RctType::ClsagBulletproofPlus,
                Zeroizing::new([7; 32]),
                inputs.clone(),
                vec![(
                    MoneroAddress::from_str(Network::Mainnet, recipient).unwrap(),
                    payment_amount,
                )],
                change.clone(),
                extra.clone(),
                FeeRate::new(20_000, 1).unwrap(),
            )
            .unwrap()
        };
        let total = INPUT_AMOUNT * u64::from(input_count);
        let mut tx = build(PAYMENT_AMOUNT);
        let paid = if max_send {
            let paid = total - tx.necessary_fee();
            tx = build(paid);
            paid
        } else {
            PAYMENT_AMOUNT
        };
        let fee = tx.necessary_fee();
        let review = ReviewRequest {
            network: "mainnet".into(),
            from_address: sender.clone(),
            to_address: recipient.into(),
            amount_atomic: paid.to_string(),
            fee_atomic: fee.to_string(),
            change_atomic: (total - paid - fee).to_string(),
            is_max_send: max_send,
        };
        (tx.serialize(), review, spend_bytes)
    }

    /// Signing from the BIP-32 key produces the same transaction, byte for
    /// byte under a fixed RNG, as signing from its reduced spend key (pinned txids).
    #[test]
    fn signing_from_the_bip32_key_matches_the_spend_key_path() {
        use crate::spend::test_support::{plus_group_order, SeededRng};
        let change = |pair| Change::new(pair, None);
        for (txid, (bytes, review, spend)) in [
            (
                "2d468af49a6d7fe0f591e6813a3b751e379dcd35f341ebf1258202c00a755c57",
                fixture(vec![]),
            ),
            (
                "47cf01a87459ab9ff041d29ff843bae47f7b4808b6743cf429bd4f0607238a8b",
                fixture_with_inputs(vec![], change, 3),
            ),
            (
                "c8ff2c3a62fd2552e49748c6fe8d749a50720da8632c7e0cc72fddd3f460d9e1",
                fixture_with_options(vec![], change, 3, true, false),
            ),
            (
                "e88d2717232572d5419be5ee7482b3986bc1fbf2b03a3f24f5c657b0c2bf6ea2",
                fixture_with_options(vec![], change, 3, false, true),
            ),
            (
                "c08e858375049f3c51d0290fc2fa7763a38d3ffd4882c678e5f918641dbf86cb",
                fixture_with_options(vec![], change, 3, true, true),
            ),
        ] {
            let encoded = hex::encode(bytes);
            let json = review_json(&review);
            let old = crate::spend::sign_for_host_with_rng(
                &encoded,
                &spend,
                &json,
                &mut SeededRng::new(42),
            )
            .unwrap();
            assert_eq!(old["txid"], txid);
            // A canonical key reduces to itself; `key + l` reduces to the key.
            for bip32 in [spend, plus_group_order(&spend)] {
                let new = crate::spend::sign_with_bip32_key_and_rng(
                    &encoded,
                    &bip32,
                    &json,
                    &mut SeededRng::new(42),
                )
                .unwrap();
                assert_eq!(new, old);
                verify_signed(&encoded, &review, new["rawHex"].as_str().unwrap()).unwrap();
            }
            let signed =
                crate::spend::sign_for_host_with_bip32_key(&encoded, &spend, &json).unwrap();
            verify_signed(&encoded, &review, signed["rawHex"].as_str().unwrap()).unwrap();
        }
    }

    /// Another account's BIP-32 key cannot sign: the change check rejects it.
    #[test]
    fn signing_from_another_bip32_key_is_rejected() {
        let (bytes, review, _) = fixture(vec![]);
        let e = crate::spend::sign_for_host_with_bip32_key(
            &hex::encode(bytes),
            &[2u8; 32],
            &review_json(&review),
        )
        .unwrap_err();
        assert_eq!(e.code, crate::ErrorCode::InvalidRequest);
        let e = crate::spend::sign_for_host_with_bip32_key("00", &[2u8; 31], &review_json(&review))
            .unwrap_err();
        assert_eq!(e.code, crate::ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["field"], "bip32PrivateKey");
    }

    #[test]
    fn approved_transaction_is_verified_and_signed() {
        let (bytes, review, spend) = fixture(vec![]);
        let encoded = hex::encode(bytes);
        verify(&encoded, &review, None).unwrap();
        let json = serde_json::json!({
            "network": review.network, "fromAddress": review.from_address,
            "toAddress": review.to_address, "amountAtomic": review.amount_atomic,
            "feeAtomic": review.fee_atomic, "changeAtomic": review.change_atomic,
            "isMaxSend": review.is_max_send,
        })
        .to_string();
        let signed = crate::spend::sign_for_host(&encoded, &spend, &json).unwrap();
        assert_eq!(signed["txid"].as_str().unwrap().len(), 64);
        assert!(!signed["rawHex"].as_str().unwrap().is_empty());
    }

    #[test]
    fn replacing_actual_recipient_is_rejected_with_unchanged_review() {
        let (mut bytes, review, spend) = fixture(vec![]);
        let position = bytes
            .windows(RECIPIENT.len())
            .position(|part| part == RECIPIENT.as_bytes())
            .unwrap();
        bytes[position..position + RECIPIENT.len()].copy_from_slice(OTHER.as_bytes());
        assert!(verify(&hex::encode(bytes), &review, Some(&spend)).is_err());
    }

    #[test]
    fn amount_fee_change_max_sender_and_network_must_match() {
        for field in [
            "amount",
            "fee",
            "change",
            "max",
            "sender",
            "network",
            "recipient",
        ] {
            let (bytes, mut review, spend) = fixture(vec![]);
            match field {
                "amount" => review.amount_atomic = "999999999999".into(),
                "fee" => {
                    review.fee_atomic = (review.fee_atomic.parse::<u64>().unwrap() + 1).to_string()
                }
                "change" => review.change_atomic = "0".into(),
                "max" => review.is_max_send = true,
                "sender" => review.from_address = OTHER.into(),
                "network" => review.network = "testnet".into(),
                "recipient" => review.to_address = OTHER.into(),
                _ => unreachable!(),
            }
            assert!(
                verify(&hex::encode(bytes), &review, Some(&spend)).is_err(),
                "{field}"
            );
        }
    }

    #[test]
    fn another_spend_key_cannot_authorize_the_change() {
        let (bytes, review, _) = fixture(vec![]);
        assert!(verify(
            &hex::encode(bytes),
            &review,
            Some(&<[u8; 32]>::from(scalar(2)))
        )
        .is_err());
    }

    /// Change must return to the signer's primary address: not one of its
    /// subaddresses, not another account of the same wallet.
    #[test]
    fn change_to_any_other_own_address_is_rejected() {
        let to_subaddress = |pair| {
            Change::new(
                pair,
                Some(monero_wallet::address::SubaddressIndex::new(0, 1).unwrap()),
            )
        };
        let to_other_account = |_| Change::new(other_account(3), None);
        type ChangeFor = Box<dyn FnOnce(ViewPair) -> Change>;
        let cases: [(&str, ChangeFor); 2] = [
            ("subaddress", Box::new(to_subaddress)),
            ("other account", Box::new(to_other_account)),
        ];
        for (label, change) in cases {
            let (bytes, review, spend) = fixture_with(vec![], change);
            let encoded = hex::encode(bytes);
            assert!(verify(&encoded, &review, None).is_err(), "{label}");
            assert!(verify(&encoded, &review, Some(&spend)).is_err(), "{label}");
        }
    }

    #[test]
    fn suffix_extra_and_noncanonical_amounts_are_rejected() {
        let (mut bytes, review, spend) = fixture(vec![]);
        bytes.push(0);
        assert!(verify(&hex::encode(bytes), &review, Some(&spend)).is_err());
        let (bytes, review, spend) = fixture(vec![vec![1, 2, 3]]);
        assert!(verify(&hex::encode(bytes), &review, Some(&spend)).is_err());
        for bad in ["01", "-1", "1e12", "18446744073709551616"] {
            let (bytes, mut review, spend) = fixture(vec![]);
            review.amount_atomic = bad.into();
            assert!(verify(&hex::encode(bytes), &review, Some(&spend)).is_err());
        }
    }
}
