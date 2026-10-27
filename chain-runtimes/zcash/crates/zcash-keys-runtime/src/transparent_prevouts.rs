use serde::{Deserialize, Serialize};
use zcash_primitives::transaction::Transaction;
use zcash_protocol::consensus::BranchId;

use crate::{ErrorCode, KeysError, Result};

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
struct Request {
    transactions: Vec<PreviousTransaction>,
}

#[derive(Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
struct PreviousTransaction {
    txid: String,
    raw_tx: String,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct ParsedTransaction {
    txid: String,
    is_coinbase: bool,
    outputs: Vec<Output>,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct Output {
    value_zat: String,
    script_pub_key: String,
}

pub fn parse_json(request_json: &str) -> Result<String> {
    let invalid = || KeysError::new(ErrorCode::InvalidTransactionRequest);
    let request: Request = serde_json::from_str(request_json).map_err(|_| invalid())?;
    let parsed = request
        .transactions
        .into_iter()
        .map(|previous| {
            let bytes = hex::decode(&previous.raw_tx).map_err(|_| invalid())?;
            let mut reader = &bytes[..];
            // Branch is unused for txids and transparent outputs; never sign this object.
            let tx = Transaction::read(&mut reader, BranchId::Sprout)
                .map_err(|e| invalid().detail(e))?;
            let txid = tx.txid().to_string();
            if !reader.is_empty() || !txid.eq_ignore_ascii_case(&previous.txid) {
                return Err(invalid().detail("previous transaction does not match its txid"));
            }
            let bundle = tx.transparent_bundle().ok_or_else(invalid)?;
            Ok(ParsedTransaction {
                txid,
                is_coinbase: bundle.is_coinbase(),
                outputs: bundle
                    .vout
                    .iter()
                    .map(|output| Output {
                        value_zat: u64::from(output.value()).to_string(),
                        script_pub_key: hex::encode(&output.script_pubkey().0 .0),
                    })
                    .collect(),
            })
        })
        .collect::<Result<Vec<_>>>()?;
    serde_json::to_string(&parsed).map_err(|_| invalid())
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::{json, Value};

    fn request() -> Value {
        serde_json::from_str(include_str!("../tests/fixtures/previous-transactions.json")).unwrap()
    }

    #[test]
    fn parses_real_v4_and_v6_previous_outputs() {
        let parsed: Value =
            serde_json::from_str(&parse_json(&request().to_string()).unwrap()).unwrap();
        assert_eq!(parsed[0]["outputs"][0]["valueZat"], "100000000");
        assert_eq!(parsed[1]["outputs"][0]["valueZat"], "1000");
        assert_eq!(
            parsed[1]["outputs"][0]["scriptPubKey"],
            "76a9149564d9fed247986b15a2f57d0b3b032eeb28476c88ac"
        );
        assert_eq!(parsed[0]["isCoinbase"], false);
        assert_eq!(parsed[1]["isCoinbase"], false);
    }

    #[test]
    fn rejects_mismatched_malformed_and_trailing_data() {
        for mode in ["txid", "truncated", "trailing", "hex"] {
            let mut request = request();
            let tx = &mut request["transactions"][0];
            match mode {
                "txid" => tx["txid"] = "00".repeat(32).into(),
                "truncated" => tx["rawTx"] = "04000080".into(),
                "trailing" => tx["rawTx"] = format!("{}00", tx["rawTx"].as_str().unwrap()).into(),
                "hex" => tx["rawTx"] = "zz".into(),
                _ => unreachable!(),
            }
            assert!(parse_json(&request.to_string()).is_err(), "{mode}");
        }
    }

    #[test]
    fn recognizes_coinbase_instead_of_assuming_regular_inputs() {
        let mut request = request();
        let mut bytes = hex::decode(request["transactions"][0]["rawTx"].as_str().unwrap()).unwrap();
        bytes[9..41].fill(0);
        bytes[41..45].fill(0xff);
        let tx = Transaction::read(&bytes[..], BranchId::Nu6_2).unwrap();
        request["transactions"] = json!([{
            "txid": tx.txid().to_string(), "rawTx": hex::encode(bytes),
        }]);
        let parsed: Value =
            serde_json::from_str(&parse_json(&request.to_string()).unwrap()).unwrap();
        assert_eq!(parsed[0]["isCoinbase"], true);
    }
}
