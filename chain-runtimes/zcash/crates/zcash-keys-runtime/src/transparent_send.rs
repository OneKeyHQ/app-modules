//! Stateless transparent transaction quoting, construction, and signing.
//!
//! This module deliberately has no wallet database, scanner, or network client. The host supplies
//! a complete UTXO catalog plus an explicit set of outpoints to spend. Every selected coin is
//! revalidated against its exact BIP-44 path before a transaction is signed.

use std::collections::{HashMap, HashSet};

use orchard::bundle::Authorized as OrchardAuthorized;
use sapling::bundle::Authorized as SaplingAuthorized;
use serde::{Deserialize, Serialize};
use transparent::{
    address::TransparentAddress,
    builder::{TransparentBuilder, TransparentSigningSet},
    bundle::{OutPoint, TxOut},
    keys::{AccountPrivKey, NonHardenedChildIndex, TransparentKeyScope},
};
use zcash_address::ZcashAddress;
use zcash_keys::address::Address;
use zcash_primitives::transaction::{
    fees::{
        transparent::{InputSize, OutputView},
        zip317::{FeeRule as Zip317FeeRule, MARGINAL_FEE},
        FeeRule as _,
    },
    sighash::{signature_hash, SignableInput},
    txid::TxIdDigester,
    Authorization, Authorized, TransactionData,
};
use zcash_protocol::{
    consensus::{BlockHeight, BranchId, NetworkType, MAIN_NETWORK},
    value::Zatoshis,
    TxId,
};
use zcash_script::script::Evaluable;
use zeroize::Zeroizing;
use zip32::AccountId;

use crate::error::{ErrorCode, KeysError, Result};

const MAX_EXPIRY_DELTA: u32 = 100;
const XPRV_VERSION: [u8; 4] = [0x04, 0x88, 0xad, 0xe4];
const ACCOUNT_XPRV_DEPTH: u8 = 3;

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct TransparentTxRequest {
    pub network: String,
    pub account_index: u32,
    pub target_height: u32,
    pub expiry_height: u32,
    pub utxos: Vec<TransparentUtxo>,
    pub selected_outpoints: Vec<OutpointRef>,
    pub recipients: Vec<TransparentRecipient>,
    pub send_max: bool,
    pub change: Option<TransparentChange>,
}

#[derive(Clone, Debug, Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct TransparentUtxo {
    pub txid: String,
    pub vout: u32,
    pub value_zat: String,
    pub script_pub_key: String,
    pub is_coinbase: bool,
    pub confirmations: u32,
    pub derivation_path: String,
}

#[derive(Clone, Debug, Deserialize, Serialize, PartialEq, Eq)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct OutpointRef {
    pub txid: String,
    pub vout: u32,
}

#[derive(Clone, Debug, Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct TransparentRecipient {
    pub address: String,
    pub amount_zat: Option<String>,
}

#[derive(Clone, Debug, Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct TransparentChange {
    pub address: String,
    pub derivation_path: String,
}

#[derive(Debug, Deserialize, Serialize, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct TransparentQuote {
    pub fee_zat: String,
    pub input_total_zat: String,
    pub send_amount_zat: String,
    pub change_zat: String,
    pub expiry_height: u32,
    pub spent_outpoints: Vec<OutpointRef>,
}

#[derive(Debug, Deserialize, Serialize, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct TransparentBuildResult {
    pub raw_tx: String,
    pub txid: String,
    pub fee_zat: String,
    pub expiry_height: u32,
    pub spent_outpoints: Vec<OutpointRef>,
}

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
struct DerivationPath {
    account: u32,
    scope: u32,
    index: u32,
}

#[derive(Debug)]
struct PlannedInput {
    request: TransparentUtxo,
    txid: TxId,
    value: Zatoshis,
    script_pub_key: Vec<u8>,
    path: DerivationPath,
}

#[derive(Clone, Copy, Debug)]
enum RecipientAddress {
    Transparent(TransparentAddress),
    Ironwood(orchard::Address),
}

#[derive(Debug)]
struct PlannedOutput {
    address: RecipientAddress,
    value: Zatoshis,
}

#[derive(Debug)]
struct TransactionPlan {
    account_index: AccountId,
    expiry_height: BlockHeight,
    target_height: BlockHeight,
    inputs: Vec<PlannedInput>,
    recipients: Vec<PlannedOutput>,
    change: Option<(TransparentChange, PlannedOutput, DerivationPath)>,
    fee: Zatoshis,
    input_total: Zatoshis,
    send_amount: Zatoshis,
}

/// Authorization marker for a transaction whose only unauthorized component is transparent.
///
/// `zcash_primitives::transaction::Unauthorized` is gated behind its `circuits` feature. Using a
/// local marker keeps the stateless transparent signer free of Sapling and Orchard proving code.
struct TransparentUnauthorized;

impl Authorization for TransparentUnauthorized {
    type TransparentAuth = transparent::builder::Unauthorized;
    type SaplingAuth = SaplingAuthorized;
    type OrchardAuth = OrchardAuthorized;
}

pub fn quote_json(request_json: &str) -> Result<String> {
    let request = parse_request(request_json)?;
    let plan = plan_transaction(request)?;
    serde_json::to_string(&plan.quote())
        .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))
}

pub fn build_with_seed_json(request_json: &str, seed: &[u8]) -> Result<String> {
    if seed.len() < 32 {
        return Err(KeysError::with(
            ErrorCode::InvalidSeed,
            serde_json::json!({ "byteLen": seed.len(), "min": 32 }),
        ));
    }

    let request = parse_request(request_json)?;
    let plan = plan_transaction(request)?;
    let account_key = AccountPrivKey::from_seed(&MAIN_NETWORK, seed, plan.account_index)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(e))?;
    serialize_build_result(build_transaction(plan, &account_key)?)
}

/// Builds with an account-level BIP-44 xprv at `m/44'/133'/account'`.
///
/// An account xprv cannot prove its ancestor path. The encoded key metadata is therefore checked
/// for depth 3 and the expected hardened account index, while the host remains responsible for
/// obtaining it from the exact `m/44'/133'/account'` path.
pub fn build_with_account_xprv_json(request_json: &str, account_xprv: &str) -> Result<String> {
    let request = parse_request(request_json)?;
    let plan = plan_transaction(request)?;
    let account_key = parse_account_xprv(account_xprv, u32::from(plan.account_index))?;
    serialize_build_result(build_transaction(plan, &account_key)?)
}

fn parse_request(request_json: &str) -> Result<TransparentTxRequest> {
    serde_json::from_str(request_json)
        .map_err(|e| KeysError::new(ErrorCode::InvalidTransactionRequest).detail(e))
}

fn serialize_build_result(result: TransparentBuildResult) -> Result<String> {
    serde_json::to_string(&result)
        .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))
}

impl TransactionPlan {
    fn quote(&self) -> TransparentQuote {
        TransparentQuote {
            fee_zat: self.fee.into_u64().to_string(),
            input_total_zat: self.input_total.into_u64().to_string(),
            send_amount_zat: self.send_amount.into_u64().to_string(),
            change_zat: self
                .change
                .as_ref()
                .map_or(0, |(_, output, _)| output.value.into_u64())
                .to_string(),
            expiry_height: self.expiry_height.into(),
            spent_outpoints: self
                .inputs
                .iter()
                .map(|input| OutpointRef {
                    txid: input.txid.to_string(),
                    vout: input.request.vout,
                })
                .collect(),
        }
    }
}

fn plan_transaction(request: TransparentTxRequest) -> Result<TransactionPlan> {
    if request.network != "main" && request.network != "mainnet" {
        return Err(KeysError::with(
            ErrorCode::InvalidNetwork,
            serde_json::json!({ "value": request.network }),
        ));
    }

    let account_index = AccountId::try_from(request.account_index).map_err(|_| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            serde_json::json!({ "value": request.account_index }),
        )
    })?;
    let target_height = BlockHeight::from_u32(request.target_height);
    if BranchId::for_height(&MAIN_NETWORK, target_height) != BranchId::Nu6_3 {
        return Err(KeysError::with(
            ErrorCode::InvalidTargetHeight,
            serde_json::json!({
                "targetHeight": request.target_height,
                "requiredBranch": "Nu6.3",
            }),
        ));
    }
    let expiry_delta = request
        .expiry_height
        .checked_sub(request.target_height)
        .ok_or_else(|| {
            KeysError::with(
                ErrorCode::InvalidTargetHeight,
                serde_json::json!({
                    "targetHeight": request.target_height,
                    "expiryHeight": request.expiry_height,
                    "reason": "expiryMustBeAfterTarget",
                }),
            )
        })?;
    if expiry_delta == 0 || expiry_delta > MAX_EXPIRY_DELTA {
        return Err(KeysError::with(
            ErrorCode::InvalidTargetHeight,
            serde_json::json!({
                "targetHeight": request.target_height,
                "expiryHeight": request.expiry_height,
                "maximumDelta": MAX_EXPIRY_DELTA,
            }),
        ));
    }
    let expiry_height = BlockHeight::from_u32(request.expiry_height);

    let inputs = select_inputs(
        request.utxos,
        request.selected_outpoints,
        request.account_index,
    )?;
    let input_total_raw = checked_sum(
        inputs.iter().map(|input| input.value.into_u64()),
        "inputTotal",
    )?;
    let input_total = parse_zatoshis_u64(input_total_raw, "inputTotal")?;

    if request.recipients.is_empty() {
        return Err(KeysError::with(
            ErrorCode::InvalidTransactionRequest,
            serde_json::json!({ "field": "recipients", "reason": "empty" }),
        ));
    }
    if request.send_max && request.recipients.len() != 1 {
        return Err(KeysError::with(
            ErrorCode::InvalidTransactionRequest,
            serde_json::json!({ "field": "recipients", "reason": "sendMaxRequiresOneRecipient" }),
        ));
    }

    let parsed_recipients = request
        .recipients
        .into_iter()
        .map(|recipient| {
            let address = parse_recipient_address(&recipient.address)?;
            if request.send_max {
                if recipient.amount_zat.is_some() {
                    return Err(KeysError::with(
                        ErrorCode::InvalidTransactionRequest,
                        serde_json::json!({ "field": "amountZat", "reason": "mustBeOmittedForSendMax" }),
                    ));
                }
                Ok((address, None))
            } else {
                let amount = recipient.amount_zat.as_deref().ok_or_else(|| {
                    KeysError::with(
                        ErrorCode::InvalidTransactionRequest,
                        serde_json::json!({ "field": "amountZat", "reason": "required" }),
                    )
                })?;
                let value = parse_positive_zatoshis(amount, "recipient.amountZat")?;
                Ok((address, Some(value)))
            }
        })
        .collect::<Result<Vec<_>>>()?;

    if request.send_max {
        let address = parsed_recipients[0].0;
        let fee = calculate_fee(target_height, inputs.len(), &[address])?;
        let send_amount_raw = input_total_raw
            .checked_sub(fee.into_u64())
            .ok_or_else(|| insufficient_funds(input_total_raw, fee.into_u64()))?;
        let send_amount = parse_positive_zatoshis(send_amount_raw.to_string().as_str(), "sendMax")?;
        return Ok(TransactionPlan {
            account_index,
            expiry_height,
            target_height,
            inputs,
            recipients: vec![PlannedOutput {
                address,
                value: send_amount,
            }],
            change: None,
            fee,
            input_total,
            send_amount,
        });
    }

    let recipients = parsed_recipients
        .into_iter()
        .map(|(address, value)| PlannedOutput {
            address,
            value: value.expect("non-send-max recipients were checked above"),
        })
        .collect::<Vec<_>>();
    let send_total_raw = checked_sum(
        recipients.iter().map(|output| output.value.into_u64()),
        "recipientTotal",
    )?;
    let send_amount = parse_zatoshis_u64(send_total_raw, "recipientTotal")?;
    let recipient_addresses = recipients
        .iter()
        .map(|output| output.address)
        .collect::<Vec<_>>();
    let fee_without_change = calculate_fee(target_height, inputs.len(), &recipient_addresses)?;
    let needed_without_change = send_total_raw
        .checked_add(fee_without_change.into_u64())
        .ok_or_else(|| amount_error("recipientTotal"))?;

    if input_total_raw < needed_without_change {
        return Err(insufficient_funds(input_total_raw, needed_without_change));
    }

    let (fee, change) = if input_total_raw == needed_without_change {
        (fee_without_change, None)
    } else {
        let change_request = request.change.ok_or_else(|| {
            KeysError::with(
                ErrorCode::InvalidTransactionRequest,
                serde_json::json!({ "field": "change", "reason": "required" }),
            )
        })?;
        let change_address = parse_transparent_address(&change_request.address, "change.address")?;
        if !matches!(change_address, TransparentAddress::PublicKeyHash(_)) {
            return Err(KeysError::with(
                ErrorCode::InvalidAddress,
                serde_json::json!({ "field": "change.address", "reason": "p2pkhRequired" }),
            ));
        }
        let change_path =
            parse_derivation_path(&change_request.derivation_path, request.account_index)?;
        if change_path.scope != 1 {
            return Err(KeysError::with(
                ErrorCode::InvalidDerivationPath,
                serde_json::json!({ "field": "change.derivationPath", "reason": "internalScopeRequired" }),
            ));
        }
        let mut addresses_with_change = recipient_addresses;
        addresses_with_change.push(RecipientAddress::Transparent(change_address));
        let fee = calculate_fee(target_height, inputs.len(), &addresses_with_change)?;
        let needed = send_total_raw
            .checked_add(fee.into_u64())
            .ok_or_else(|| amount_error("recipientTotal"))?;
        let change_raw = input_total_raw
            .checked_sub(needed)
            .ok_or_else(|| insufficient_funds(input_total_raw, needed))?;
        if change_raw < MARGINAL_FEE.into_u64() {
            return Err(KeysError::with(
                ErrorCode::DustChange,
                serde_json::json!({
                    "changeZat": change_raw.to_string(),
                    "minimumZat": MARGINAL_FEE.into_u64().to_string(),
                }),
            ));
        }
        let change_value = parse_zatoshis_u64(change_raw, "change")?;
        (
            fee,
            Some((
                change_request,
                PlannedOutput {
                    address: RecipientAddress::Transparent(change_address),
                    value: change_value,
                },
                change_path,
            )),
        )
    };

    Ok(TransactionPlan {
        account_index,
        expiry_height,
        target_height,
        inputs,
        recipients,
        change,
        fee,
        input_total,
        send_amount,
    })
}

fn select_inputs(
    utxos: Vec<TransparentUtxo>,
    selected: Vec<OutpointRef>,
    account_index: u32,
) -> Result<Vec<PlannedInput>> {
    if selected.is_empty() {
        return Err(KeysError::with(
            ErrorCode::InvalidTransactionRequest,
            serde_json::json!({ "field": "selectedOutpoints", "reason": "empty" }),
        ));
    }

    let mut catalog = HashMap::new();
    for utxo in utxos {
        let txid = parse_txid(&utxo.txid)?;
        let key = (txid.to_string(), utxo.vout);
        if catalog.insert(key.clone(), (utxo, txid)).is_some() {
            return Err(KeysError::with(
                ErrorCode::DuplicateInput,
                serde_json::json!({ "txid": key.0, "vout": key.1 }),
            ));
        }
    }

    let mut selected_seen = HashSet::new();
    selected
        .into_iter()
        .map(|outpoint| {
            let txid = parse_txid(&outpoint.txid)?;
            let key = (txid.to_string(), outpoint.vout);
            if !selected_seen.insert(key.clone()) {
                return Err(KeysError::with(
                    ErrorCode::DuplicateInput,
                    serde_json::json!({ "txid": key.0, "vout": key.1 }),
                ));
            }
            let (utxo, txid) = catalog.remove(&key).ok_or_else(|| {
                KeysError::with(
                    ErrorCode::UnknownInput,
                    serde_json::json!({ "txid": key.0, "vout": key.1 }),
                )
            })?;
            validate_utxo(utxo, txid, account_index)
        })
        .collect()
}

fn validate_utxo(utxo: TransparentUtxo, txid: TxId, account_index: u32) -> Result<PlannedInput> {
    if txid.is_null() {
        return Err(KeysError::with(
            ErrorCode::InvalidOutpoint,
            serde_json::json!({ "txid": utxo.txid, "vout": utxo.vout, "reason": "nullTxid" }),
        ));
    }
    if utxo.is_coinbase {
        return Err(KeysError::with(
            ErrorCode::CoinbaseInput,
            serde_json::json!({ "txid": txid.to_string(), "vout": utxo.vout }),
        ));
    }
    if utxo.confirmations == 0 {
        return Err(KeysError::with(
            ErrorCode::UnconfirmedInput,
            serde_json::json!({ "txid": txid.to_string(), "vout": utxo.vout }),
        ));
    }
    let value = parse_positive_zatoshis(&utxo.value_zat, "utxo.valueZat")?;
    let script_pub_key = hex::decode(&utxo.script_pub_key).map_err(|_| {
        KeysError::with(
            ErrorCode::UnsupportedInputScript,
            serde_json::json!({ "txid": txid.to_string(), "vout": utxo.vout }),
        )
    })?;
    if !is_p2pkh_script(&script_pub_key) {
        return Err(KeysError::with(
            ErrorCode::UnsupportedInputScript,
            serde_json::json!({ "txid": txid.to_string(), "vout": utxo.vout, "required": "p2pkh" }),
        ));
    }
    let path = parse_derivation_path(&utxo.derivation_path, account_index)?;

    Ok(PlannedInput {
        request: utxo,
        txid,
        value,
        script_pub_key,
        path,
    })
}

fn build_transaction(
    plan: TransactionPlan,
    account_key: &AccountPrivKey,
) -> Result<TransparentBuildResult> {
    let quote = plan.quote();
    let mut builder = TransparentBuilder::empty();
    let mut signing_set = TransparentSigningSet::new();

    for input in &plan.inputs {
        let secret_key = derive_secret_key(account_key, input.path)?;
        let pubkey = signing_set.add_key(secret_key);
        let derived_address = TransparentAddress::from_pubkey(&pubkey);
        let expected_script = derived_address.script().to_bytes();
        if input.script_pub_key != expected_script {
            return Err(KeysError::with(
                ErrorCode::KeyMismatch,
                serde_json::json!({ "txid": input.txid.to_string(), "vout": input.request.vout }),
            ));
        }
        builder
            .add_p2pkh_input(
                pubkey,
                OutPoint::new(*input.txid.as_ref(), input.request.vout),
                TxOut::new(input.value, derived_address.script().into()),
            )
            .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))?;
    }

    for output in &plan.recipients {
        builder
            .add_output(&require_transparent(output.address)?, output.value)
            .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))?;
    }
    if let Some((change_request, output, path)) = &plan.change {
        let secret_key = derive_secret_key(account_key, *path)?;
        let pubkey = signing_set.add_key(secret_key);
        let derived_address = TransparentAddress::from_pubkey(&pubkey);
        if derived_address != require_transparent(output.address)? {
            return Err(KeysError::with(
                ErrorCode::KeyMismatch,
                serde_json::json!({ "field": "change.address" }),
            ));
        }
        let encoded = derived_address
            .to_zcash_address(NetworkType::Main)
            .to_string();
        if encoded != change_request.address {
            return Err(KeysError::with(
                ErrorCode::KeyMismatch,
                serde_json::json!({ "field": "change.address" }),
            ));
        }
        builder
            .add_output(&require_transparent(output.address)?, output.value)
            .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))?;
    }

    let transparent_bundle = builder.build().ok_or_else(|| {
        KeysError::with(
            ErrorCode::TransactionBuildFailed,
            serde_json::json!({ "reason": "emptyBundle" }),
        )
    })?;
    let unsigned_tx = TransactionData::<TransparentUnauthorized>::from_parts_v6(
        BranchId::Nu6_3,
        0,
        plan.expiry_height,
        Some(transparent_bundle.clone()),
        None,
        None,
        None,
    );
    let txid_parts = unsigned_tx.digest(TxIdDigester);
    let authorized_bundle = transparent_bundle
        .apply_signatures(
            |input| {
                *signature_hash(
                    &unsigned_tx,
                    &SignableInput::Transparent(input),
                    &txid_parts,
                )
                .as_ref()
            },
            &signing_set,
        )
        .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))?;
    let transaction = TransactionData::<Authorized>::from_parts_v6(
        BranchId::Nu6_3,
        0,
        plan.expiry_height,
        Some(authorized_bundle),
        None,
        None,
        None,
    )
    .freeze()
    .map_err(|e| KeysError::new(ErrorCode::TransactionSerializeFailed).detail(e))?;
    let mut raw_tx = Vec::new();
    transaction
        .write(&mut raw_tx)
        .map_err(|e| KeysError::new(ErrorCode::TransactionSerializeFailed).detail(e))?;

    Ok(TransparentBuildResult {
        raw_tx: hex::encode(raw_tx),
        txid: transaction.txid().to_string(),
        fee_zat: quote.fee_zat,
        expiry_height: quote.expiry_height,
        spent_outpoints: quote.spent_outpoints,
    })
}

fn calculate_fee(
    target_height: BlockHeight,
    input_count: usize,
    output_addresses: &[RecipientAddress],
) -> Result<Zatoshis> {
    let output_sizes = output_addresses.iter().filter_map(|address| match address {
        RecipientAddress::Transparent(address) => {
            Some(TxOut::new(Zatoshis::ZERO, address.script().into()).serialized_size())
        }
        RecipientAddress::Ironwood(_) => None,
    });
    let ironwood_outputs = output_addresses
        .iter()
        .filter(|a| matches!(a, RecipientAddress::Ironwood(_)))
        .count();
    let ironwood_actions = if ironwood_outputs == 0 {
        0
    } else {
        ironwood_outputs.max(2)
    };
    Zip317FeeRule::standard()
        .fee_required(
            &MAIN_NETWORK,
            target_height,
            std::iter::repeat_n(InputSize::STANDARD_P2PKH, input_count),
            output_sizes,
            0,
            0,
            0,
            ironwood_actions,
        )
        .map_err(|e| KeysError::new(ErrorCode::TransactionBuildFailed).detail(e))
}

fn derive_secret_key(
    account_key: &AccountPrivKey,
    path: DerivationPath,
) -> Result<secp256k1::SecretKey> {
    let scope = match path.scope {
        0 => TransparentKeyScope::EXTERNAL,
        1 => TransparentKeyScope::INTERNAL,
        _ => {
            return Err(KeysError::with(
                ErrorCode::InvalidDerivationPath,
                serde_json::json!({ "reason": "unsupportedScope", "scope": path.scope }),
            ));
        }
    };
    let index = NonHardenedChildIndex::from_index(path.index).ok_or_else(|| {
        KeysError::with(
            ErrorCode::InvalidDerivationPath,
            serde_json::json!({ "reason": "hardenedAddressIndex", "index": path.index }),
        )
    })?;
    account_key
        .derive_secret_key(scope, index)
        .map_err(|e| KeysError::new(ErrorCode::DeriveFailed).detail(e))
}

fn parse_account_xprv(encoded: &str, expected_account: u32) -> Result<AccountPrivKey> {
    let decoded = Zeroizing::new(
        bs58::decode(encoded)
            .with_check(None)
            .into_vec()
            .map_err(|_| KeysError::new(ErrorCode::InvalidAccountXprv))?,
    );
    if decoded.len() != 78 || decoded[..4] != XPRV_VERSION || decoded[4] != ACCOUNT_XPRV_DEPTH {
        return Err(KeysError::new(ErrorCode::InvalidAccountXprv));
    }
    let child = u32::from_be_bytes(decoded[9..13].try_into().expect("fixed slice length"));
    let expected_child = expected_account.checked_add(1 << 31).ok_or_else(|| {
        KeysError::with(
            ErrorCode::InvalidAccountIndex,
            serde_json::json!({ "value": expected_account }),
        )
    })?;
    if child != expected_child {
        return Err(KeysError::with(
            ErrorCode::InvalidAccountXprv,
            serde_json::json!({ "reason": "accountIndexMismatch" }),
        ));
    }
    AccountPrivKey::from_bytes(&decoded[4..])
        .ok_or_else(|| KeysError::new(ErrorCode::InvalidAccountXprv))
}

fn parse_derivation_path(path: &str, expected_account: u32) -> Result<DerivationPath> {
    let parts = path.split('/').collect::<Vec<_>>();
    let hardened = |part: &str| part.strip_suffix('\'')?.parse::<u32>().ok();
    let plain = |part: &str| {
        if part.ends_with('\'') {
            None
        } else {
            part.parse::<u32>().ok()
        }
    };
    let parsed = match parts.as_slice() {
        ["m", purpose, coin_type, account, scope, index]
            if hardened(purpose) == Some(44) && hardened(coin_type) == Some(133) =>
        {
            hardened(account).zip(plain(scope)).zip(plain(index)).map(
                |((account, scope), index)| DerivationPath {
                    account,
                    scope,
                    index,
                },
            )
        }
        _ => None,
    }
    .filter(|parsed| {
        parsed.account == expected_account && parsed.scope <= 1 && parsed.index < (1 << 31)
    })
    .ok_or_else(|| {
        KeysError::with(
            ErrorCode::InvalidDerivationPath,
            serde_json::json!({
                "required": format!("m/44'/133'/{expected_account}'/<0|1>/<index>"),
            }),
        )
    })?;
    Ok(parsed)
}

fn parse_transparent_address(encoded: &str, field: &str) -> Result<TransparentAddress> {
    ZcashAddress::try_from_encoded(encoded)
        .map_err(|_| {
            KeysError::with(
                ErrorCode::InvalidAddress,
                serde_json::json!({ "field": field }),
            )
        })?
        .convert_if_network(NetworkType::Main)
        .map_err(|_| {
            KeysError::with(
                ErrorCode::InvalidAddress,
                serde_json::json!({ "field": field, "network": "main" }),
            )
        })
}

fn parse_txid(encoded: &str) -> Result<TxId> {
    TxId::from_hex(encoded).ok_or_else(|| {
        KeysError::with(
            ErrorCode::InvalidOutpoint,
            serde_json::json!({ "reason": "invalidTxid" }),
        )
    })
}

fn parse_positive_zatoshis(encoded: &str, field: &str) -> Result<Zatoshis> {
    let value = encoded.parse::<u64>().map_err(|_| amount_error(field))?;
    if value == 0 {
        return Err(amount_error(field));
    }
    parse_zatoshis_u64(value, field)
}

fn parse_zatoshis_u64(value: u64, field: &str) -> Result<Zatoshis> {
    Zatoshis::from_u64(value).map_err(|_| amount_error(field))
}

fn checked_sum(mut values: impl Iterator<Item = u64>, field: &str) -> Result<u64> {
    values
        .try_fold(0u64, |sum, value| sum.checked_add(value))
        .ok_or_else(|| amount_error(field))
}

fn amount_error(field: &str) -> KeysError {
    KeysError::with(
        ErrorCode::InvalidAmount,
        serde_json::json!({ "field": field }),
    )
}

fn insufficient_funds(available: u64, required: u64) -> KeysError {
    KeysError::with(
        ErrorCode::InsufficientFunds,
        serde_json::json!({
            "availableZat": available.to_string(),
            "requiredZat": required.to_string(),
            "shortfallZat": required.saturating_sub(available).to_string(),
        }),
    )
}

fn is_p2pkh_script(script: &[u8]) -> bool {
    script.len() == 25
        && script[0] == 0x76
        && script[1] == 0xa9
        && script[2] == 0x14
        && script[23] == 0x88
        && script[24] == 0xac
}

#[cfg(test)]
mod tests {
    use super::*;

    const TEST_MNEMONIC: &str =
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about";

    fn seed() -> Vec<u8> {
        bip0039::Mnemonic::<bip0039::English>::from_phrase(TEST_MNEMONIC)
            .unwrap()
            .to_seed("")
            .to_vec()
    }

    fn account_key(seed: &[u8], account: u32) -> AccountPrivKey {
        AccountPrivKey::from_seed(&MAIN_NETWORK, seed, AccountId::try_from(account).unwrap())
            .unwrap()
    }

    fn address_and_script(seed: &[u8], account: u32, scope: u32, index: u32) -> (String, String) {
        let key = account_key(seed, account);
        let secret = derive_secret_key(
            &key,
            DerivationPath {
                account,
                scope,
                index,
            },
        )
        .unwrap();
        let mut signing_set = TransparentSigningSet::new();
        let public = signing_set.add_key(secret);
        let address = TransparentAddress::from_pubkey(&public);
        (
            address.to_zcash_address(NetworkType::Main).to_string(),
            hex::encode(address.script().to_bytes()),
        )
    }

    fn request_json(values: &[u64], send_max: bool) -> String {
        let seed = seed();
        let (recipient, _) = address_and_script(&seed, 0, 0, 9);
        let (change, _) = address_and_script(&seed, 0, 1, 0);
        let utxos = values
            .iter()
            .enumerate()
            .map(|(i, value)| {
                let (_, script) = address_and_script(&seed, 0, 0, i as u32);
                serde_json::json!({
                    "txid": format!("{:064x}", i + 1),
                    "vout": i as u32,
                    "valueZat": value.to_string(),
                    "scriptPubKey": script,
                    "isCoinbase": false,
                    "confirmations": 1,
                    "derivationPath": format!("m/44'/133'/0'/0/{i}"),
                })
            })
            .collect::<Vec<_>>();
        let selected = utxos
            .iter()
            .map(|utxo| serde_json::json!({ "txid": utxo["txid"], "vout": utxo["vout"] }))
            .collect::<Vec<_>>();
        serde_json::json!({
            "network": "main",
            "accountIndex": 0,
            "targetHeight": 3_500_000,
            "expiryHeight": 3_500_020,
            "utxos": utxos,
            "selectedOutpoints": selected,
            "recipients": [{
                "address": recipient,
                "amountZat": if send_max { serde_json::Value::Null } else { serde_json::Value::String("50000".into()) },
            }],
            "sendMax": send_max,
            "change": if send_max { serde_json::Value::Null } else { serde_json::json!({
                "address": change,
                "derivationPath": "m/44'/133'/0'/1/0",
            }) },
        })
        .to_string()
    }

    fn shielding_request(send_max: bool) -> String {
        let sk = orchard::keys::SpendingKey::from_bytes([7; 32]).unwrap();
        let fvk = orchard::keys::FullViewingKey::from(&sk);
        let recipient = zcash_keys::address::UnifiedAddress::from_receivers(
            Some(fvk.address_at(0u32, zip32::Scope::External)),
            None,
            None,
        )
        .unwrap()
        .encode(&MAIN_NETWORK);
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], send_max)).unwrap();
        request["recipients"][0]["address"] = recipient.into();
        request.to_string()
    }

    #[test]
    fn quotes_and_creates_shielding_without_a_wallet_or_scan() {
        for send_max in [false, true] {
            let request = shielding_request(send_max);
            let quote: TransparentQuote =
                serde_json::from_str(&quote_json(&request).unwrap()).unwrap();
            assert_eq!(quote.fee_zat, "15000");
            assert_eq!(
                quote.send_amount_zat,
                if send_max { "85000" } else { "50000" }
            );
            let created = shielding::create(&request, &seed()).unwrap();
            let pczt = pczt::Pczt::parse(&created).unwrap();
            assert_eq!(pczt.transparent().inputs().len(), 1);
            assert_eq!(pczt.transparent().outputs().len(), usize::from(!send_max));
            assert_eq!(pczt.ironwood().actions().len(), 2);
            assert!(pczt.orchard().actions().is_empty());
            assert!(pczt.sapling().spends().is_empty());
        }
    }

    #[test]
    fn quotes_zip317_for_multiple_inputs() {
        let request = request_json(&[30_000, 30_000, 30_000], false);
        let quote: TransparentQuote = serde_json::from_str(&quote_json(&request).unwrap()).unwrap();
        assert_eq!(quote.fee_zat, "15000");
        assert_eq!(quote.input_total_zat, "90000");
        assert_eq!(quote.send_amount_zat, "50000");
        assert_eq!(quote.change_zat, "25000");
        assert_eq!(quote.expiry_height, 3_500_020);
        assert_eq!(quote.spent_outpoints.len(), 3);

        let built: TransparentBuildResult =
            serde_json::from_str(&build_with_seed_json(&request, &seed()).unwrap()).unwrap();
        assert_eq!(built.fee_zat, "15000");
        assert_eq!(built.spent_outpoints, quote.spent_outpoints);
    }

    #[test]
    fn quotes_send_max_without_change() {
        let quote: TransparentQuote =
            serde_json::from_str(&quote_json(&request_json(&[100_000], true)).unwrap()).unwrap();
        assert_eq!(quote.fee_zat, "10000");
        assert_eq!(quote.send_amount_zat, "90000");
        assert_eq!(quote.change_zat, "0");
    }

    #[test]
    fn rejects_coinbase_unconfirmed_and_unknown_inputs() {
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        request["utxos"][0]["isCoinbase"] = true.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::CoinbaseInput);

        request["utxos"][0]["isCoinbase"] = false.into();
        request["utxos"][0]["confirmations"] = 0.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::UnconfirmedInput);

        request["utxos"][0]["confirmations"] = 1.into();
        request["selectedOutpoints"][0]["vout"] = 9.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::UnknownInput);
    }

    #[test]
    fn rejects_script_and_key_mismatch() {
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        request["utxos"][0]["scriptPubKey"] =
            "a914000000000000000000000000000000000000000087".into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::UnsupportedInputScript);

        let other_seed = vec![0x42; 64];
        request = serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        let error = build_with_seed_json(&request.to_string(), &other_seed).unwrap_err();
        assert_eq!(error.code, ErrorCode::KeyMismatch);
    }

    #[test]
    fn enforces_exact_bip44_path() {
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        request["utxos"][0]["derivationPath"] = "m/44'/0'/0'/0/0".into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::InvalidDerivationPath);

        request["utxos"][0]["derivationPath"] = "m/44'/133'/0'/0/0".into();
        request["accountIndex"] = 1.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::InvalidDerivationPath);
    }

    #[test]
    fn requires_host_selected_bounded_expiry() {
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        request["expiryHeight"] = 3_500_000.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::InvalidTargetHeight);

        request["expiryHeight"] = 3_500_101.into();
        let error = quote_json(&request.to_string()).unwrap_err();
        assert_eq!(error.code, ErrorCode::InvalidTargetHeight);
    }

    #[test]
    fn supports_p2sh_destination_and_account_xprv() {
        let seed = seed();
        let mut request: serde_json::Value =
            serde_json::from_str(&request_json(&[100_000], true)).unwrap();
        request["recipients"][0]["address"] = TransparentAddress::ScriptHash([7; 20])
            .to_zcash_address(NetworkType::Main)
            .to_string()
            .into();

        let account_key = account_key(&seed, 0);
        let mut encoded = XPRV_VERSION.to_vec();
        encoded.extend(account_key.to_bytes());
        let account_xprv = bs58::encode(encoded).with_check().into_string();

        let from_seed = build_with_seed_json(&request.to_string(), &seed).unwrap();
        let from_xprv = build_with_account_xprv_json(&request.to_string(), &account_xprv).unwrap();
        assert_eq!(from_xprv, from_seed);

        request["accountIndex"] = 1.into();
        let error = build_with_account_xprv_json(&request.to_string(), &account_xprv).unwrap_err();
        assert_eq!(error.code, ErrorCode::InvalidDerivationPath);
    }

    #[test]
    fn builds_deterministic_v6_transaction() {
        let raw = build_with_seed_json(&request_json(&[100_000], true), &seed()).unwrap();
        let result: TransparentBuildResult = serde_json::from_str(&raw).unwrap();
        assert_eq!(result.fee_zat, "10000");
        assert_eq!(result.expiry_height, 3_500_020);
        assert_eq!(result.spent_outpoints.len(), 1);
        assert_eq!(
            result.raw_tx,
            "0600008098b684d85b16a53700000000f4673500010100000000000000000000000000000000000000000000000000000000000000000000006a47304402206be3be8ac1c9bc252843c9ad8a1f1d41f3e7c0716217078f44ecd819f7103bc902202bdd61b62d35d0be08ca15b558fd01d4c076102584a03371d3e064d0b856bea1012103db98d8f87716269ed31879aef19bdadbc869a9ea67729e36332d023b916cbcc9ffffffff01905f0100000000001976a9148d484bcaf1d7993da6db4cca2c1517e1cf020ab788ac00000000"
        );
        assert_eq!(
            result.txid,
            "81004865cdc4217f34aafd1d4b307b3cc5d60fcee7afdb4a829983d394ae39cd"
        );
    }
}

fn require_transparent(address: RecipientAddress) -> Result<TransparentAddress> {
    match address {
        RecipientAddress::Transparent(address) => Ok(address),
        RecipientAddress::Ironwood(_) => Err(KeysError::new(ErrorCode::InvalidAddress)),
    }
}

fn parse_recipient_address(encoded: &str) -> Result<RecipientAddress> {
    match Address::decode(&MAIN_NETWORK, encoded) {
        Some(Address::Transparent(address)) => Ok(RecipientAddress::Transparent(address)),
        // Unified Orchard receivers receive Ironwood notes after NU6.3. Never silently
        // fall back to a public receiver when a Unified Address lacks this receiver.
        Some(Address::Unified(address)) => address
            .orchard()
            .copied()
            .map(RecipientAddress::Ironwood)
            .ok_or_else(|| KeysError::new(ErrorCode::InvalidAddress)),
        _ => Err(KeysError::new(ErrorCode::InvalidAddress)),
    }
}

pub mod shielding;
