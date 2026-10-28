//! Authenticated transaction facts and sender-side recovery; never queries a node.

use std::collections::HashMap;

use serde::{Deserialize, Serialize};
use zcash_keys::keys::UnifiedFullViewingKey;
use zcash_primitives::transaction::Transaction;
use zcash_protocol::{
    consensus::{BranchId, MAIN_NETWORK},
    value::BalanceError,
    TxId,
};

use crate::{ErrorCode, KeysError, Result};

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
struct RawTransaction {
    txid: String,
    raw_tx: String,
}

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
struct Request {
    transaction: RawTransaction,
    #[serde(default)]
    previous_transactions: Vec<RawTransaction>,
    ufvk: Option<String>,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct Outpoint {
    txid: String,
    vout: u32,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct RecoveredOutput {
    recipient: String,
    amount_zat: String,
    memo_hex: String,
}

fn parse(raw: &RawTransaction) -> Result<Transaction> {
    let invalid = || KeysError::new(ErrorCode::InvalidTransactionRequest);
    if raw.raw_tx.len() > 8 * 1024 * 1024 {
        return Err(invalid());
    }
    let bytes = hex::decode(&raw.raw_tx).map_err(|_| invalid())?;
    let mut reader = &bytes[..];
    let tx = Transaction::read(&mut reader, BranchId::Sprout).map_err(|e| invalid().detail(e))?;
    if !reader.is_empty() || !tx.txid().to_string().eq_ignore_ascii_case(&raw.txid) {
        return Err(invalid().detail("transaction does not match its txid"));
    }
    Ok(tx)
}

pub fn inspect_json(request_json: &str) -> Result<String> {
    if request_json.len() > 16 * 1024 * 1024 {
        return Err(KeysError::new(ErrorCode::InvalidTransactionRequest));
    }
    let request: Request = serde_json::from_str(request_json)
        .map_err(|_| KeysError::new(ErrorCode::InvalidTransactionRequest))?;
    if request.previous_transactions.len() > 256 {
        return Err(KeysError::new(ErrorCode::InvalidTransactionRequest));
    }
    let tx = parse(&request.transaction)?;
    let previous = request
        .previous_transactions
        .iter()
        .map(|raw| parse(raw).map(|tx| (tx.txid().to_string(), tx)))
        .collect::<Result<HashMap<_, _>>>()?;
    let is_coinbase = tx
        .transparent_bundle()
        .is_some_and(|bundle| bundle.is_coinbase());
    let inputs = tx
        .transparent_bundle()
        .filter(|_| !is_coinbase)
        .map(|bundle| {
            bundle
                .vin
                .iter()
                .map(|input| Outpoint {
                    txid: TxId::from_bytes(*input.prevout().hash()).to_string(),
                    vout: input.prevout().n(),
                })
                .collect::<Vec<_>>()
        })
        .unwrap_or_default();
    let fee = if is_coinbase {
        None
    } else {
        tx.fee_paid::<BalanceError, _>(|outpoint| {
            Ok(previous
                .get(&TxId::from_bytes(*outpoint.hash()).to_string())
                .and_then(|tx| tx.transparent_bundle())
                .and_then(|bundle| bundle.vout.get(outpoint.n() as usize))
                .map(|output| output.value()))
        })
        .map_err(|e| KeysError::new(ErrorCode::InvalidAmount).detail(e))?
    };
    let mut recovered = Vec::new();
    if let Some(ufvk) = request.ufvk {
        let ufvk = UnifiedFullViewingKey::decode(&MAIN_NETWORK, &ufvk)
            .map_err(|e| KeysError::new(ErrorCode::InvalidUfvk).detail(e))?;
        if let (Some(fvk), Some(bundle)) = (ufvk.orchard(), tx.ironwood_bundle()) {
            let ovk = fvk.to_ovk(zip32::Scope::External);
            for action in bundle.actions().iter() {
                if let Some((note, recipient, memo)) =
                    zcash_note_encryption::try_output_recovery_with_ovk(
                        &orchard::note_encryption::IronwoodDomain::for_action(action),
                        &ovk,
                        action,
                        action.cv_net(),
                        &action.encrypted_note().out_ciphertext,
                    )
                {
                    if note.value().inner() == 0 {
                        continue;
                    }
                    let recipient = zcash_keys::address::UnifiedAddress::from_receivers(
                        Some(recipient),
                        None,
                        None,
                    )
                    .ok_or_else(|| KeysError::new(ErrorCode::InvalidAddress))?
                    .encode(&MAIN_NETWORK);
                    recovered.push(RecoveredOutput {
                        recipient,
                        amount_zat: note.value().inner().to_string(),
                        memo_hex: hex::encode(memo),
                    });
                }
            }
        }
    }
    serde_json::to_string(&serde_json::json!({
        "txid": tx.txid().to_string(),
        "inputs": inputs,
        "feeZat": fee.map(|value| u64::from(value).to_string()),
        "recoveredOutputs": recovered,
    }))
    .map_err(|e| KeysError::new(ErrorCode::InvalidTransactionRequest).detail(e))
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::{json, Value};

    fn fixture() -> Value {
        serde_json::from_str(include_str!("../tests/fixtures/previous-transactions.json")).unwrap()
    }

    fn raw_transaction(input: Option<transparent::bundle::OutPoint>, value: u64) -> Value {
        use transparent::address::{Script, TransparentAddress};
        use transparent::bundle::{Bundle, OutPoint, TxIn, TxOut};
        use zcash_primitives::transaction::{Authorized, TransactionData};
        use zcash_protocol::{consensus::BlockHeight, value::Zatoshis};
        let vin = input
            .map(|prevout: OutPoint| TxIn::from_parts(prevout, Script::default(), u32::MAX))
            .into_iter()
            .collect();
        let bundle = Bundle {
            vin,
            vout: vec![TxOut::new(
                Zatoshis::from_u64(value).unwrap(),
                TransparentAddress::PublicKeyHash([1; 20]).script().into(),
            )],
            authorization: transparent::bundle::Authorized,
        };
        let tx = TransactionData::<Authorized>::from_parts_v6(
            BranchId::Nu6_3,
            0,
            BlockHeight::from_u32(3_500_040),
            Some(bundle),
            None,
            None,
            None,
        )
        .freeze()
        .unwrap();
        let mut bytes = Vec::new();
        tx.write(&mut bytes).unwrap();
        json!({ "txid": tx.txid().to_string(), "rawTx": hex::encode(bytes) })
    }

    #[test]
    fn fee_uses_the_authenticated_previous_output() {
        let previous = raw_transaction(None, 100_000);
        let previous_tx = parse(&serde_json::from_value(previous.clone()).unwrap()).unwrap();
        let transaction = raw_transaction(
            Some(transparent::bundle::OutPoint::new(
                *previous_tx.txid().as_ref(),
                0,
            )),
            90_000,
        );
        let request = json!({ "transaction": transaction, "previousTransactions": [previous] });
        let result: Value =
            serde_json::from_str(&inspect_json(&request.to_string()).unwrap()).unwrap();
        assert_eq!(result["feeZat"], "10000");
        let mut corrupt = request;
        corrupt["previousTransactions"][0]["txid"] = "ff".repeat(32).into();
        assert!(inspect_json(&corrupt.to_string()).is_err());
    }

    #[test]
    fn an_unknown_previous_output_does_not_invent_a_fee() {
        let request = json!({"transaction": fixture()["transactions"][0]});
        let result: Value =
            serde_json::from_str(&inspect_json(&request.to_string()).unwrap()).unwrap();
        assert!(result["feeZat"].is_null());
        assert!(!result["inputs"].as_array().unwrap().is_empty());
        assert!(result["recoveredOutputs"].as_array().unwrap().is_empty());
    }

    #[test]
    fn a_backend_transaction_must_match_its_requested_hash() {
        let mut request = json!({"transaction": fixture()["transactions"][0]});
        request["transaction"]["txid"] = "00".repeat(32).into();
        assert!(inspect_json(&request.to_string()).is_err());
    }
}
