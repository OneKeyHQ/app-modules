use monero_wallet::{
    address::MoneroAddress,
    ed25519::{CompressedPoint, Point, Scalar},
    io::{read_byte, read_bytes, read_u32, read_u64, read_vec},
    send::SignableTransaction,
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
                            let scalar = Zeroizing::new(Scalar::read(&mut key.as_ref())?);
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

    // Public, deterministic test keys and invented outputs; no node or funds.
    fn fixture(extra: Vec<Vec<u8>>) -> (Vec<u8>, ReviewRequest, [u8; 32]) {
        let mut public_bytes = [0x66u8; 32];
        public_bytes[0] = 0x58;
        let public = CompressedPoint::from(public_bytes).decompress().unwrap();
        let commitment = Commitment::new(scalar(1), INPUT_AMOUNT);
        let mut input_bytes = Vec::new();
        input_bytes.extend(public_bytes);
        scalar(0).write(&mut input_bytes).unwrap();
        commitment.write(&mut input_bytes).unwrap();
        let members = (0..16)
            .map(|index| {
                if index == 0 {
                    [public, commitment.commit()]
                } else {
                    [
                        Point::biased_hash([index; 32]),
                        Point::biased_hash([index + 100; 32]),
                    ]
                }
            })
            .collect();
        Decoys::new(vec![1; 16], 0, members)
            .unwrap()
            .write(&mut input_bytes)
            .unwrap();
        let input = OutputWithDecoys::read(&mut input_bytes.as_slice()).unwrap();
        let spend_bytes = <[u8; 32]>::from(scalar(1));
        let pair = ViewPair::new(public, Zeroizing::new(Scalar::hash(spend_bytes))).unwrap();
        let sender = pair.legacy_address(Network::Mainnet).to_string();
        let tx = SignableTransaction::new(
            RctType::ClsagBulletproofPlus,
            Zeroizing::new([7; 32]),
            vec![input],
            vec![(
                MoneroAddress::from_str(Network::Mainnet, RECIPIENT).unwrap(),
                PAYMENT_AMOUNT,
            )],
            Change::new(pair, None),
            extra,
            FeeRate::new(20_000, 1).unwrap(),
        )
        .unwrap();
        let fee = tx.necessary_fee();
        let review = ReviewRequest {
            network: "mainnet".into(),
            from_address: sender,
            to_address: RECIPIENT.into(),
            amount_atomic: PAYMENT_AMOUNT.to_string(),
            fee_atomic: fee.to_string(),
            change_atomic: (INPUT_AMOUNT - PAYMENT_AMOUNT - fee).to_string(),
            is_max_send: false,
        };
        (tx.serialize(), review, spend_bytes)
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
