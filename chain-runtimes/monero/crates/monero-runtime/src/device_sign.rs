//! Hardware wallet signing (the Trezor Monero protocol, as OneKey firmware runs it).
//!
//! The device draws its own tx key, output masks and pseudo-out blinding, so neither
//! monero-oxide's `SignableTransaction` nor its `Eventuality` can describe what it signs.
//! A device plan carries the inputs, rings and destinations instead, and the transaction
//! the device returns is rebuilt and checked against it. The recipient output cannot be
//! re-derived without the device's tx key; the device shows it to the user.
//!
//! The plan is as private as `signableHex`: real ring members, masks, amounts and the
//! private view key. Never log or persist it.

use std::collections::{BTreeSet, HashSet};

use curve25519_dalek::{constants::ED25519_BASEPOINT_POINT, traits::IsIdentity as _, EdwardsPoint};
use monero_wallet::{
    address::{AddressType, MoneroAddress, Network},
    ed25519::{Commitment, CompressedPoint, Point, Scalar},
    extra::{Extra, MAX_EXTRA_SIZE_BY_RELAY_RULE},
    interface::FeeRate,
    io::VarInt,
    primitives::keccak256,
    ringct::{
        bulletproofs::Bulletproof, clsag::Clsag, EncryptedAmount, RctBase, RctProofs, RctPrunable,
        RctType,
    },
    transaction::{Input, NotPruned, Output, Timelock, Transaction, TransactionPrefix},
    OutputWithDecoys,
};
use rand_core::{CryptoRng, RngCore};
use rusqlite::OptionalExtension;
use serde::{de::Error as _, Deserialize, Deserializer, Serialize, Serializer};
use serde_json::{json, Value};
use zeroize::{Zeroize, Zeroizing};

use crate::device_key_images::{
    derives_output_key, real_tx_keys, receiving_spend_key, view_scalar,
};
use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::keys;
use crate::review::{self, ReviewRequest};
use crate::spend::{self, Prepared, FEE_QUANTIZATION_MASK, RING_LEN};
use crate::wallet::{Db, Identity};

const PLAN_VERSION: u32 = 1;
/// `MoneroTransactionData.version`.
const TSX_VERSION: u32 = 1;
/// Lowest host protocol version the firmware accepts (sealed CLSAGs).
const CLIENT_VERSION: u32 = 3;
/// The firmware defaults a missing hard fork to 12 and refuses it; view tags need 15+.
const HARD_FORK: u32 = 16;
/// `RangeProofPaddedBulletproof`, as wallet2 sends.
const RSIG_TYPE: u32 = 3;
/// Bulletproof+.
const BP_VERSION: u32 = 4;
/// Recipient and change: the device proves the Bulletproof+ itself only up to two outputs.
const OUTPUTS: usize = 2;
/// Same bound as `signableHex`.
const MAX_HEX_LEN: usize = 8 * 1024 * 1024;

const G: EdwardsPoint = ED25519_BASEPOINT_POINT;

/// What `prepareDeviceTransaction` returns as `devicePlan`; field names follow the
/// firmware protobuf messages (`MoneroTransactionData`, `...SourceEntry`, `...DestinationEntry`).
#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct DevicePlan {
    pub version: u32,
    /// `MoneroNetworkType`: 0 mainnet, 1 testnet, 2 stagenet.
    pub network_type: u32,
    pub tsx_data: TsxData,
    /// Original order; the device sorts by key image itself.
    pub inputs: Vec<SourceEntry>,
    /// Read by this runtime only; not sent to the device.
    pub expected: Expected,
}

#[derive(Serialize, Deserialize)]
pub struct TsxData {
    pub version: u32,
    pub client_version: u32,
    pub hard_fork: u32,
    pub unlock_time: u64,
    pub num_inputs: u32,
    pub mixin: u32,
    #[serde(with = "atomic")]
    pub fee: u64,
    pub account: u32,
    pub minor_indices: Vec<u32>,
    pub outputs: Vec<DestinationEntry>,
    pub change_dts: DestinationEntry,
    pub rsig_data: RsigData,
    /// Integrated recipients only; otherwise the device adds a dummy one.
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub payment_id: Option<String>,
    pub integrated_indices: Vec<u32>,
}

#[derive(Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct DestinationEntry {
    #[serde(with = "atomic")]
    pub amount: u64,
    pub addr: AccountPublicAddress,
    pub is_subaddress: bool,
    pub is_integrated: bool,
}

#[derive(Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct AccountPublicAddress {
    pub spend_public_key: String,
    pub view_public_key: String,
}

#[derive(PartialEq, Eq, Serialize, Deserialize)]
pub struct RsigData {
    pub rsig_type: u32,
    pub bp_version: u32,
    pub grouping: Vec<u64>,
}

#[derive(Serialize, Deserialize)]
pub struct SourceEntry {
    /// The ring, ascending by global index (the device derives `key_offsets` from it).
    pub outputs: Vec<OutputEntry>,
    pub real_output: u64,
    pub real_out_tx_key: String,
    /// Only this output's additional key; the device reads one element as that.
    pub real_out_additional_tx_keys: Vec<String>,
    pub real_output_in_tx_index: u64,
    #[serde(with = "atomic")]
    pub amount: u64,
    pub rct: bool,
    pub mask: String,
    pub subaddr_minor: u32,
}

#[derive(Serialize, Deserialize)]
pub struct OutputEntry {
    pub idx: u64,
    pub key: RctKeyPublic,
}

#[derive(Serialize, Deserialize)]
pub struct RctKeyPublic {
    pub dest: String,
    pub commitment: String,
}

#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Expected {
    /// From the stored outputs, per input in original order.
    pub key_images: Vec<String>,
    #[serde(with = "atomic")]
    pub fee: u64,
    #[serde(with = "atomic")]
    pub recipient_amount: u64,
    #[serde(with = "atomic")]
    pub change_amount: u64,
    /// The node's fee rate the fee was computed with.
    #[serde(with = "atomic")]
    pub fee_per_weight: u64,
    /// The account's private view key, to find the change in the signed transaction.
    pub view_key: SecretKeyHex,
}

/// A 32-byte secret as hex, wiped on drop (including the decoded string).
pub struct SecretKeyHex(Zeroizing<[u8; 32]>);

impl Serialize for SecretKeyHex {
    fn serialize<S: Serializer>(&self, serializer: S) -> std::result::Result<S::Ok, S::Error> {
        serializer.serialize_str(&Zeroizing::new(hex::encode(*self.0)))
    }
}

impl<'de> Deserialize<'de> for SecretKeyHex {
    fn deserialize<D: Deserializer<'de>>(deserializer: D) -> std::result::Result<Self, D::Error> {
        let mut text = String::deserialize(deserializer)?;
        let mut key = Zeroizing::new([0u8; 32]);
        let decoded = hex::decode_to_slice(&text, key.as_mut_slice());
        text.zeroize();
        decoded.map_err(|_| D::Error::custom("not a 32-byte hex key"))?;
        Ok(Self(key))
    }
}

/// Piconero amounts cross the boundary as canonical decimal strings.
mod atomic {
    use super::*;

    pub fn serialize<S: Serializer>(
        value: &u64,
        serializer: S,
    ) -> std::result::Result<S::Ok, S::Error> {
        serializer.serialize_str(&value.to_string())
    }

    pub fn deserialize<'de, D: Deserializer<'de>>(
        deserializer: D,
    ) -> std::result::Result<u64, D::Error> {
        let text = String::deserialize(deserializer)?;
        text.parse::<u64>()
            .ok()
            .filter(|value| value.to_string() == text)
            .ok_or_else(|| D::Error::custom("not a canonical decimal amount"))
    }
}

/// The device's result with its CLSAGs opened (contract §4).
#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct DeviceSigned {
    /// Plan input index of each sorted input.
    pub orig_indices: Vec<usize>,
    /// Sorted order, as are `pseudo_outs` and `clsags`.
    pub vinis: Vec<String>,
    pub pseudo_outs: Vec<String>,
    /// `varint(ring_len) || s[ring_len] || c1 || D`.
    pub clsags: Vec<String>,
    /// Output (`tsxData.outputs`) order, as are `out_pks` and `ecdh_infos`.
    pub tx_outs: Vec<String>,
    pub out_pks: Vec<String>,
    pub ecdh_infos: Vec<String>,
    pub rsig: String,
    pub extra: String,
    pub tx_prefix_hash: String,
    pub full_message_hash: String,
    pub rv: RingCtSig,
}

#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct RingCtSig {
    #[serde(deserialize_with = "atomic::deserialize")]
    pub txn_fee: u64,
    pub message: String,
    pub rv_type: u8,
}

fn invalid_plan(reason: &str) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({ "field": "devicePlan", "reason": reason }),
    )
}

/// The device's transaction fails a check against the plan.
fn rejected(reason: &str) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({ "field": "deviceTransaction", "reason": reason }),
    )
}

fn hex32(value: &str) -> Option<[u8; 32]> {
    let mut out = [0u8; 32];
    hex::decode_to_slice(value, &mut out).ok()?;
    Some(out)
}

fn hex_bytes(value: &str) -> Option<Vec<u8>> {
    if value.len() > MAX_HEX_LEN {
        return None;
    }
    hex::decode(value).ok()
}

fn point(value: &str) -> Option<EdwardsPoint> {
    CompressedPoint::from(hex32(value)?)
        .decompress()
        .map(Point::into)
}

fn hex_point(point: EdwardsPoint) -> String {
    hex::encode(point.compress().to_bytes())
}

fn public_address(spend: Point, view: Point) -> AccountPublicAddress {
    AccountPublicAddress {
        spend_public_key: hex::encode(spend.compress().to_bytes()),
        view_public_key: hex::encode(view.compress().to_bytes()),
    }
}

fn device_network_type(network: Network) -> u32 {
    match network {
        Network::Mainnet => 0,
        Network::Testnet => 1,
        Network::Stagenet => 2,
    }
}

fn network_from_type(network_type: u32) -> Option<Network> {
    match network_type {
        0 => Some(Network::Mainnet),
        1 => Some(Network::Testnet),
        2 => Some(Network::Stagenet),
        _ => None,
    }
}

/// `(is_subaddress, payment id)` of a recipient, or `None` for an address the device
/// cannot pay (featured addresses).
fn destination_kind(address: &MoneroAddress) -> Option<(bool, Option<[u8; 8]>)> {
    match address.kind() {
        AddressType::Legacy => Some((false, None)),
        AddressType::LegacyIntegrated(id) => Some((false, Some(*id))),
        AddressType::Subaddress => Some((true, None)),
        AddressType::Featured { .. } => None,
    }
}

/// `8·R/8`, as wallet2 sends tx keys (`pubkey_clear_torsion`); the device's `8·a·R` is unchanged.
fn clear_torsion(key: &[u8]) -> Option<String> {
    let key = <[u8; 32]>::try_from(key).ok()?;
    let key: EdwardsPoint = CompressedPoint::from(key).decompress()?.into();
    Some(hex_point(key.mul_by_cofactor() * Scalar::INV_EIGHT.into()))
}

/// First absolute index, then each one's distance from the previous.
fn relative_offsets(positions: &[u64]) -> Option<Vec<u64>> {
    let mut offsets = Vec::with_capacity(positions.len());
    for (i, position) in positions.iter().enumerate() {
        offsets.push(if i == 0 {
            *position
        } else {
            position
                .checked_sub(positions[i - 1])
                .filter(|offset| *offset > 0)?
        });
    }
    Some(offsets)
}

/// `keccak(varint(2) || prefix)`; oxide keeps the prefix hash private.
pub(crate) fn prefix_hash(prefix: &TransactionPrefix) -> [u8; 32] {
    // Without proofs a V2 transaction serializes as its version, prefix and one 0 byte.
    let mut bytes = Transaction::<NotPruned>::V2 {
        prefix: prefix.clone(),
        proofs: None,
    }
    .serialize();
    bytes.pop();
    keccak256(bytes)
}

pub fn is_device_plan(value: &str) -> bool {
    value.trim_start().starts_with('{')
}

// ── Plan ──

/// Selects inputs and builds the plan a hardware wallet signs. Same selection, rings and
/// fee as `prepareTransaction`; every input must carry a key image (device key image sync).
#[allow(clippy::too_many_arguments)]
pub async fn prepare_for_host(
    db: &Db,
    daemon: &crate::network::Daemon,
    identity_id: i64,
    recipients: &[(&str, &str)],
    max_send: bool,
    chain_tip: u64,
    priority: monero_wallet::interface::FeePriority,
) -> Result<Value> {
    let (prepared, recipient, rate) = spend::prepare_selection(
        db,
        daemon,
        identity_id,
        recipients,
        max_send,
        chain_tip,
        priority,
    )
    .await?;
    let plan = build_plan(
        db,
        identity_id,
        &prepared,
        &recipient,
        rate.per_weight(),
        &mut rand_core::OsRng,
    )?;
    let device_plan = Zeroizing::new(
        serde_json::to_string(&plan).map_err(|e| invalid_plan("serialize").detail(e))?,
    );

    Ok(json!({
        "devicePlan": device_plan.as_str(),
        "feeAtomic": prepared.fee_atomic.to_string(),
        "amountAtomic": prepared.amount_atomic.to_string(),
        "inputsAtomic": prepared.inputs_atomic.to_string(),
        "changeAtomic": prepared.change_atomic().to_string(),
        "inputCount": prepared.output_ids.len(),
    }))
}

/// Builds the device plan from a prepared selection. Change goes to the primary address.
pub fn build_plan<R: RngCore + CryptoRng>(
    db: &Db,
    identity_id: i64,
    prepared: &Prepared,
    recipient: &MoneroAddress,
    fee_per_weight: u64,
    rng: &mut R,
) -> Result<DevicePlan> {
    let identity = db.identity_by_id(identity_id)?;
    let view = view_scalar(db, identity_id)?;
    let (is_subaddress, payment_id) = destination_kind(recipient).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "field": "recipient.address", "reason": "unsupportedByDevice" }),
        )
    })?;
    if prepared.output_ids.len() != prepared.inputs.len() {
        return Err(invalid_plan("inputCount"));
    }

    let change = DestinationEntry {
        amount: prepared.change_atomic(),
        addr: public_address(identity.view_pair.spend(), identity.view_pair.view()),
        is_subaddress: false,
        is_integrated: false,
    };
    let mut outputs = vec![
        DestinationEntry {
            amount: prepared.amount_atomic,
            addr: public_address(recipient.spend(), recipient.view()),
            is_subaddress,
            is_integrated: payment_id.is_some(),
        },
        change.clone(),
    ];
    // The device keeps the order it is given; oxide shuffles its payments too.
    let recipient_index = rng.next_u32() & 1;
    if recipient_index == 1 {
        outputs.swap(0, 1);
    }

    let mut inputs = Vec::with_capacity(prepared.inputs.len());
    let mut key_images = Vec::with_capacity(prepared.inputs.len());
    let mut minors = BTreeSet::new();
    for (output_id, input) in prepared.output_ids.iter().zip(&prepared.inputs) {
        let (entry, key_image) = source_entry(db, &identity, &view, *output_id, input)?;
        minors.insert(entry.subaddr_minor);
        key_images.push(hex::encode(key_image));
        inputs.push(entry);
    }

    let plan = DevicePlan {
        version: PLAN_VERSION,
        network_type: device_network_type(db.network()),
        tsx_data: TsxData {
            version: TSX_VERSION,
            client_version: CLIENT_VERSION,
            hard_fork: HARD_FORK,
            unlock_time: 0,
            num_inputs: u32::try_from(inputs.len()).map_err(|_| invalid_plan("inputCount"))?,
            mixin: u32::from(RING_LEN) - 1,
            fee: prepared.fee_atomic,
            account: 0,
            minor_indices: minors.into_iter().collect(),
            outputs,
            change_dts: change,
            rsig_data: RsigData {
                rsig_type: RSIG_TYPE,
                bp_version: BP_VERSION,
                grouping: vec![OUTPUTS as u64],
            },
            payment_id: payment_id.map(hex::encode),
            integrated_indices: payment_id.map(|_| recipient_index).into_iter().collect(),
        },
        inputs,
        expected: Expected {
            key_images,
            fee: prepared.fee_atomic,
            recipient_amount: prepared.amount_atomic,
            change_amount: prepared.change_atomic(),
            fee_per_weight,
            view_key: SecretKeyHex(Zeroizing::new(view.to_bytes())),
        },
    };
    check_plan(&plan)?;
    Ok(plan)
}

/// `(key image, index in tx, global index, output key, R keys, additional keys, major, minor)`.
type InputRow = (
    Option<Vec<u8>>,
    i64,
    i64,
    Option<Vec<u8>>,
    Option<Vec<u8>>,
    Option<Vec<u8>>,
    Option<i64>,
    Option<i64>,
);

/// One input as the device needs it, and its stored key image.
fn source_entry(
    db: &Db,
    identity: &Identity,
    view: &curve25519_dalek::Scalar,
    output_id: i64,
    input: &OutputWithDecoys,
) -> Result<(SourceEntry, [u8; 32])> {
    let row: Option<InputRow> = db
        .conn()
        .query_row(
            "SELECT key_image, index_in_tx, global_index, output_key, tx_pub_keys,
                    tx_additional_pub_keys, subaddress_major, subaddress_minor
             FROM outputs WHERE id = ?1 AND identity_id = ?2",
            [output_id, identity.id],
            |r| {
                Ok((
                    r.get(0)?,
                    r.get(1)?,
                    r.get(2)?,
                    r.get(3)?,
                    r.get(4)?,
                    r.get(5)?,
                    r.get(6)?,
                    r.get(7)?,
                ))
            },
        )
        .optional()
        .map_err(|e| db_err("loadDeviceInput", e))?;
    let corrupt = |what: &str| db_err("loadDeviceInput", format!("output {output_id}: {what}"));
    let (key_image, index, global_index, output_key, tx_keys, additional, major, minor) =
        row.ok_or_else(|| corrupt("not found"))?;

    // The hardware flow syncs key images before preparing; selection only takes outputs with one.
    let key_image = key_image
        .and_then(|k| <[u8; 32]>::try_from(k).ok())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "inputs", "reason": "keyImageRequired", "outputId": output_id }),
            )
        })?;
    // The plan signs for account (major) 0 only.
    if major.unwrap_or(0) != 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "inputs", "reason": "subaddressAccountUnsupported", "outputId": output_id }),
        ));
    }
    let minor = u32::try_from(minor.unwrap_or(0)).map_err(|_| corrupt("bad subaddress"))?;
    let index = u32::try_from(index).map_err(|_| corrupt("output index out of range"))?;
    let output_key = output_key
        .and_then(|k| <[u8; 32]>::try_from(k).ok())
        .ok_or_else(|| corrupt("no output key"))?;
    if output_key != input.key().compress().to_bytes() {
        return Err(corrupt("output key differs from its blob"));
    }
    let tx_keys = tx_keys
        .filter(|k| !k.is_empty() && k.len() % 32 == 0)
        .ok_or_else(|| corrupt("no transaction public key"))?;

    let spend = receiving_spend_key(db, identity, 0, minor);
    let (tx_key, additional) = real_tx_keys(
        view,
        spend,
        &tx_keys,
        additional.as_deref(),
        index,
        &output_key,
    );
    // The device derives the one-time key from these; fail here rather than on the device.
    let derives = |r: &[u8]| derives_output_key(view, r, index, spend, &output_key);
    if !derives(tx_key) && !additional.is_some_and(derives) {
        return Err(corrupt("no transaction key derives the output"));
    }

    let decoys = input.decoys();
    let positions = decoys.positions();
    let ring = decoys.ring();
    let real = usize::from(decoys.signer_index());
    if positions.len() != usize::from(RING_LEN)
        || relative_offsets(&positions).is_none()
        || u64::try_from(global_index).ok() != Some(positions[real])
        || ring[real][0].compress().to_bytes() != output_key
    {
        return Err(corrupt("ring does not hold the output"));
    }

    let commitment = input.commitment();
    let entry = SourceEntry {
        outputs: positions
            .iter()
            .zip(ring)
            .map(|(idx, member)| OutputEntry {
                idx: *idx,
                key: RctKeyPublic {
                    dest: hex_point(member[0].into()),
                    commitment: hex_point(member[1].into()),
                },
            })
            .collect(),
        real_output: real as u64,
        real_out_tx_key: clear_torsion(tx_key)
            .ok_or_else(|| corrupt("invalid transaction public key"))?,
        real_out_additional_tx_keys: additional
            .map(|key| clear_torsion(key).ok_or_else(|| corrupt("invalid additional key")))
            .transpose()?
            .into_iter()
            .collect(),
        real_output_in_tx_index: u64::from(index),
        amount: commitment.amount,
        rct: true,
        mask: hex::encode(<[u8; 32]>::from(commitment.mask)),
        subaddr_minor: minor,
    };
    Ok((entry, key_image))
}

// ── Plan checks ──

/// A ring as the transaction commits to it.
struct Ring {
    offsets: Vec<u64>,
    members: Vec<[CompressedPoint; 2]>,
}

/// The decoded, consistency-checked plan.
struct Checked {
    network: Network,
    key_images: Vec<[u8; 32]>,
    rings: Vec<Ring>,
    change_index: usize,
    recipient_index: usize,
    /// The recipient is the account's own primary address, so its output is scanned too.
    self_send: bool,
    change_spend: EdwardsPoint,
    view: Zeroizing<curve25519_dalek::Scalar>,
    fee: u64,
    recipient_amount: u64,
    change_amount: u64,
    fee_rate: FeeRate,
}

fn parse_plan(json: &str) -> Result<DevicePlan> {
    if json.len() > MAX_HEX_LEN {
        return Err(invalid_plan("tooLarge"));
    }
    serde_json::from_str(json).map_err(|e| invalid_plan("malformed").detail(e))
}

/// Structure and amounts the runtime itself produced; anything else is refused.
fn check_plan(plan: &DevicePlan) -> Result<Checked> {
    let tsx = &plan.tsx_data;
    let expected = &plan.expected;
    let network =
        network_from_type(plan.network_type).ok_or_else(|| invalid_plan("networkType"))?;
    if plan.version != PLAN_VERSION
        || tsx.version != TSX_VERSION
        || tsx.client_version < CLIENT_VERSION
        || tsx.hard_fork != HARD_FORK
        || tsx.unlock_time != 0
        || tsx.account != 0
        || tsx.mixin != u32::from(RING_LEN) - 1
        || tsx.rsig_data
            != (RsigData {
                rsig_type: RSIG_TYPE,
                bp_version: BP_VERSION,
                grouping: vec![OUTPUTS as u64],
            })
        || tsx.outputs.len() != OUTPUTS
    {
        return Err(invalid_plan("unsupportedTransaction"));
    }
    if plan.inputs.is_empty()
        || usize::try_from(tsx.num_inputs).ok() != Some(plan.inputs.len())
        || expected.key_images.len() != plan.inputs.len()
    {
        return Err(invalid_plan("inputCount"));
    }

    // The change is the entry equal to `change_dts`; if both are (a self-send of the
    // same amount), either one is.
    let change = &tsx.change_dts;
    let change_index = tsx
        .outputs
        .iter()
        .position(|output| output == change)
        .ok_or_else(|| invalid_plan("change"))?;
    let recipient_index = 1 - change_index;
    let recipient = &tsx.outputs[recipient_index];
    if change.is_subaddress
        || change.is_integrated
        || change.amount != expected.change_amount
        || recipient.amount != expected.recipient_amount
        || expected.recipient_amount == 0
        || tsx.fee != expected.fee
    {
        return Err(invalid_plan("amounts"));
    }
    let integrated: &[u32] = match &tsx.payment_id {
        Some(id) if id.len() == 16 && hex::decode(id).is_ok() => &[recipient_index as u32],
        Some(_) => return Err(invalid_plan("paymentId")),
        None => &[],
    };
    if tsx.integrated_indices != integrated
        || recipient.is_integrated != tsx.payment_id.is_some()
        || (recipient.is_integrated && recipient.is_subaddress)
    {
        return Err(invalid_plan("paymentId"));
    }

    let change_spend =
        point(&change.addr.spend_public_key).ok_or_else(|| invalid_plan("change"))?;
    let view = Zeroizing::new(
        Scalar::read(&mut expected.view_key.0.as_slice())
            .map_err(|_| invalid_plan("viewKey"))?
            .into(),
    );
    if point(&change.addr.view_public_key) != Some(*view * G) {
        return Err(invalid_plan("viewKey"));
    }
    let fee_rate = FeeRate::new(expected.fee_per_weight, FEE_QUANTIZATION_MASK)
        .ok_or_else(|| invalid_plan("feePerWeight"))?;

    let mut total: u64 = 0;
    let mut rings = Vec::with_capacity(plan.inputs.len());
    let mut minors = BTreeSet::new();
    for input in &plan.inputs {
        let positions: Vec<u64> = input.outputs.iter().map(|o| o.idx).collect();
        let offsets = relative_offsets(&positions).ok_or_else(|| invalid_plan("ring"))?;
        let members = input
            .outputs
            .iter()
            .map(|o| {
                Some([
                    CompressedPoint::from(hex32(&o.key.dest)?),
                    CompressedPoint::from(hex32(&o.key.commitment)?),
                ])
            })
            .collect::<Option<Vec<_>>>()
            .ok_or_else(|| invalid_plan("ring"))?;
        let real = usize::try_from(input.real_output).map_err(|_| invalid_plan("ring"))?;
        if members.len() != usize::from(RING_LEN) || real >= members.len() || !input.rct {
            return Err(invalid_plan("ring"));
        }
        let mask = hex32(&input.mask)
            .and_then(|mask| Scalar::read(&mut mask.as_slice()).ok())
            .ok_or_else(|| invalid_plan("mask"))?;
        // The device refuses an input whose mask and amount do not open its commitment.
        if Commitment::new(mask, input.amount)
            .commit()
            .compress()
            .to_bytes()
            != members[real][1].to_bytes()
        {
            return Err(invalid_plan("mask"));
        }
        total = total
            .checked_add(input.amount)
            .ok_or_else(|| invalid_plan("amounts"))?;
        minors.insert(input.subaddr_minor);
        rings.push(Ring { offsets, members });
    }
    if tsx.minor_indices != minors.into_iter().collect::<Vec<_>>() {
        return Err(invalid_plan("minorIndices"));
    }
    let spent = expected
        .fee
        .checked_add(expected.recipient_amount)
        .and_then(|sum| sum.checked_add(expected.change_amount));
    if spent != Some(total) {
        return Err(invalid_plan("amounts"));
    }

    let key_images = expected
        .key_images
        .iter()
        .map(|k| hex32(k))
        .collect::<Option<Vec<_>>>()
        .ok_or_else(|| invalid_plan("keyImages"))?;
    if key_images.iter().collect::<HashSet<_>>().len() != key_images.len() {
        return Err(invalid_plan("keyImages"));
    }

    Ok(Checked {
        network,
        key_images,
        rings,
        change_index,
        recipient_index,
        // The device derives every output to the change address with the view key.
        self_send: recipient.addr == change.addr,
        change_spend,
        view,
        fee: expected.fee,
        recipient_amount: expected.recipient_amount,
        change_amount: expected.change_amount,
        fee_rate,
    })
}

/// The review the user confirmed must describe this plan.
fn check_review(plan: &DevicePlan, checked: &Checked, request: &ReviewRequest) -> Result<()> {
    let mismatch = review::mismatch;
    let network = keys::parse_network(&request.network)?;
    if network != checked.network {
        return Err(mismatch("network"));
    }
    let tsx = &plan.tsx_data;
    let from = keys::parse_address(network, &request.from_address, "fromAddress")?;
    if *from.kind() != AddressType::Legacy
        || public_address(from.spend(), from.view()) != tsx.change_dts.addr
    {
        return Err(mismatch("fromAddress"));
    }
    let to = keys::parse_address(network, &request.to_address, "toAddress")?;
    let recipient = &tsx.outputs[checked.recipient_index];
    match destination_kind(&to) {
        Some((is_subaddress, payment_id))
            if public_address(to.spend(), to.view()) == recipient.addr
                && recipient.is_subaddress == is_subaddress
                && tsx.payment_id == payment_id.map(hex::encode) => {}
        _ => return Err(mismatch("toAddress")),
    }
    let expected = &plan.expected;
    let change = review::amount(&request.change_atomic)?;
    if review::amount(&request.amount_atomic)? != expected.recipient_amount {
        return Err(mismatch("amount"));
    }
    if review::amount(&request.fee_atomic)? != expected.fee
        || change != expected.change_amount
        || (request.is_max_send && change != 0)
    {
        return Err(mismatch("feeOrChange"));
    }
    Ok(())
}

/// Checks a plan against the review before it goes to the device.
pub fn verify_unsigned(plan_json: &str, request: &ReviewRequest) -> Result<()> {
    let plan = parse_plan(plan_json)?;
    let checked = check_plan(&plan)?;
    check_review(&plan, &checked, request)
}

// ── Assembly and verification ──

/// Rebuilds the device's transaction, verifies it against the plan and returns
/// `{ txid, rawHex }`. No DB or network access.
pub fn assemble(plan_json: &str, signed_json: &str) -> Result<Value> {
    let plan = parse_plan(plan_json)?;
    let checked = check_plan(&plan)?;
    let signed: DeviceSigned = serde_json::from_str(signed_json).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "signedJson", "reason": "malformed" }),
        )
        .detail(e)
    })?;
    let transaction = rebuild(&plan, &checked, &signed)?;
    verify_transaction(&checked, &transaction)?;

    let raw = transaction.serialize();
    // Broadcast verification reads it back; make sure it can.
    let mut reader = raw.as_slice();
    if Transaction::read(&mut reader).ok().as_ref() != Some(&transaction) || !reader.is_empty() {
        return Err(rejected("serialization"));
    }
    Ok(json!({
        "txid": hex::encode(transaction.hash()),
        "rawHex": hex::encode(raw),
    }))
}

/// The transaction from the device's parts, with the device's prefix and message hashes checked.
fn rebuild(plan: &DevicePlan, checked: &Checked, signed: &DeviceSigned) -> Result<Transaction> {
    let inputs_len = checked.key_images.len();
    let outputs_len = plan.tsx_data.outputs.len();
    if [
        signed.orig_indices.len(),
        signed.vinis.len(),
        signed.pseudo_outs.len(),
        signed.clsags.len(),
    ] != [inputs_len; 4]
        || [
            signed.tx_outs.len(),
            signed.out_pks.len(),
            signed.ecdh_infos.len(),
        ] != [outputs_len; 3]
    {
        return Err(rejected("count"));
    }
    let mut seen = vec![false; inputs_len];
    for &orig in &signed.orig_indices {
        if orig >= inputs_len || std::mem::replace(&mut seen[orig], true) {
            return Err(rejected("origIndices"));
        }
    }

    let mut inputs = Vec::with_capacity(inputs_len);
    for (vini, &orig) in signed.vinis.iter().zip(&signed.orig_indices) {
        let bytes = hex_bytes(vini).ok_or_else(|| rejected("vini"))?;
        let mut reader = bytes.as_slice();
        let input = Input::read(&mut reader).map_err(|_| rejected("vini"))?;
        if !reader.is_empty() {
            return Err(rejected("vini"));
        }
        match &input {
            Input::ToKey { key_image, .. } if key_image.to_bytes() == checked.key_images[orig] => {}
            _ => return Err(rejected("keyImageMismatch")),
        }
        inputs.push(input);
    }

    let mut outputs = Vec::with_capacity(outputs_len);
    let mut commitments = Vec::with_capacity(outputs_len);
    let mut encrypted_amounts = Vec::with_capacity(outputs_len);
    for ((tx_out, out_pk), ecdh) in signed
        .tx_outs
        .iter()
        .zip(&signed.out_pks)
        .zip(&signed.ecdh_infos)
    {
        let bytes = hex_bytes(tx_out).ok_or_else(|| rejected("txOut"))?;
        let mut reader = bytes.as_slice();
        let output = Output::read(true, &mut reader).map_err(|_| rejected("txOut"))?;
        if !reader.is_empty() {
            return Err(rejected("txOut"));
        }
        let out_pk: [u8; 64] = hex_bytes(out_pk)
            .and_then(|b| b.try_into().ok())
            .ok_or_else(|| rejected("outPk"))?;
        if out_pk[..32] != output.key.to_bytes() {
            return Err(rejected("outPk"));
        }
        let commitment: [u8; 32] = out_pk[32..].try_into().expect("32 of 64 bytes");
        let amount: [u8; 8] = hex_bytes(ecdh)
            .and_then(|b| b.try_into().ok())
            .ok_or_else(|| rejected("ecdhInfo"))?;
        outputs.push(output);
        commitments.push(CompressedPoint::from(commitment));
        encrypted_amounts.push(EncryptedAmount::Compact { amount });
    }

    let extra = hex_bytes(&signed.extra)
        .filter(|extra| extra.len() <= MAX_EXTRA_SIZE_BY_RELAY_RULE)
        .ok_or_else(|| rejected("extra"))?;
    let prefix = TransactionPrefix {
        additional_timelock: Timelock::None,
        inputs,
        outputs,
        extra,
    };
    let prefix_hash = prefix_hash(&prefix);
    if hex32(&signed.tx_prefix_hash) != Some(prefix_hash)
        || hex32(&signed.rv.message) != Some(prefix_hash)
    {
        return Err(rejected("prefixHashMismatch"));
    }
    if signed.rv.rv_type != u8::from(RctType::ClsagBulletproofPlus) {
        return Err(rejected("rctType"));
    }
    if signed.rv.txn_fee != plan.expected.fee {
        return Err(rejected("feeMismatch"));
    }

    let rsig = hex_bytes(&signed.rsig).ok_or_else(|| rejected("rangeProof"))?;
    let mut reader = rsig.as_slice();
    let bulletproof = Bulletproof::read_plus(&mut reader).map_err(|_| rejected("rangeProof"))?;
    if !reader.is_empty() {
        return Err(rejected("rangeProof"));
    }

    let mut clsags = Vec::with_capacity(inputs_len);
    for (clsag, &orig) in signed.clsags.iter().zip(&signed.orig_indices) {
        let bytes = Zeroizing::new(hex_bytes(clsag).ok_or_else(|| rejected("signature"))?);
        let mut reader = bytes.as_slice();
        let ring_len: usize = VarInt::read(&mut reader).map_err(|_| rejected("signature"))?;
        if ring_len != checked.rings[orig].members.len() {
            return Err(rejected("signature"));
        }
        let clsag = Clsag::read(ring_len, &mut reader).map_err(|_| rejected("signature"))?;
        if !reader.is_empty() {
            return Err(rejected("signature"));
        }
        clsags.push(clsag);
    }
    let pseudo_outs = signed
        .pseudo_outs
        .iter()
        .map(|p| hex32(p).map(CompressedPoint::from))
        .collect::<Option<Vec<_>>>()
        .ok_or_else(|| rejected("pseudoOut"))?;

    let transaction = Transaction::V2 {
        prefix,
        proofs: Some(RctProofs {
            base: RctBase {
                fee: signed.rv.txn_fee,
                pseudo_outs: vec![],
                encrypted_amounts,
                commitments,
            },
            prunable: RctPrunable::Clsag {
                bulletproof,
                clsags,
                pseudo_outs,
            },
        }),
    };
    if transaction.signature_hash() != hex32(&signed.full_message_hash) {
        return Err(rejected("fullMessageHashMismatch"));
    }
    Ok(transaction)
}

/// Everything the host can check of a device-built transaction against its plan.
fn verify_transaction(checked: &Checked, transaction: &Transaction) -> Result<()> {
    let Transaction::V2 {
        prefix,
        proofs: Some(proofs),
    } = transaction
    else {
        return Err(rejected("notRingCt"));
    };
    let RctPrunable::Clsag {
        bulletproof,
        clsags,
        pseudo_outs,
    } = &proofs.prunable
    else {
        return Err(rejected("rctType"));
    };
    let inputs_len = checked.key_images.len();
    if proofs.rct_type() != RctType::ClsagBulletproofPlus
        || prefix.additional_timelock != Timelock::None
        || prefix.inputs.len() != inputs_len
        || clsags.len() != inputs_len
        || pseudo_outs.len() != inputs_len
        || prefix.outputs.len() != OUTPUTS
        || proofs.base.commitments.len() != OUTPUTS
        || proofs.base.encrypted_amounts.len() != OUTPUTS
        || !proofs.base.pseudo_outs.is_empty()
        || prefix
            .outputs
            .iter()
            .any(|output| output.amount.is_some() || output.view_tag.is_none())
    {
        return Err(rejected("shape"));
    }
    if proofs.base.fee != checked.fee {
        return Err(rejected("feeMismatch"));
    }

    // Each input spends one planned input, by its stored key image, over its planned ring.
    let message = transaction
        .signature_hash()
        .ok_or_else(|| rejected("notRingCt"))?;
    let mut spent = vec![false; inputs_len];
    let mut previous: Option<[u8; 32]> = None;
    for ((input, clsag), pseudo_out) in prefix.inputs.iter().zip(clsags).zip(pseudo_outs) {
        let Input::ToKey {
            amount: None,
            key_offsets,
            key_image,
        } = input
        else {
            return Err(rejected("input"));
        };
        let image = key_image.to_bytes();
        // Strictly descending, as consensus and the device require.
        if previous.is_some_and(|previous| previous <= image) {
            return Err(rejected("keyImageOrder"));
        }
        previous = Some(image);
        let index = checked
            .key_images
            .iter()
            .position(|expected| *expected == image)
            .filter(|index| !std::mem::replace(&mut spent[*index], true))
            .ok_or_else(|| rejected("keyImageMismatch"))?;
        let ring = &checked.rings[index];
        if *key_offsets != ring.offsets {
            return Err(rejected("ringMismatch"));
        }
        clsag
            .verify(ring.members.clone(), key_image, pseudo_out, &message)
            .map_err(|e| rejected("invalidSignature").detail(format!("{e:?}")))?;
    }

    if !bulletproof.verify(&mut rand_core::OsRng, &proofs.base.commitments) {
        return Err(rejected("invalidRangeProof"));
    }

    // Inputs balance outputs plus fee.
    let sum = |points: &[CompressedPoint]| -> Option<EdwardsPoint> {
        points
            .iter()
            .map(|p| p.decompress().map(Point::into))
            .sum::<Option<EdwardsPoint>>()
    };
    let fee = Commitment::new(Scalar::ZERO, proofs.base.fee)
        .commit()
        .into();
    match (sum(pseudo_outs), sum(&proofs.base.commitments)) {
        (Some(inputs), Some(outputs)) if (inputs - outputs - fee).is_identity() => {}
        _ => return Err(rejected("unbalanced")),
    }

    let weight = u64::try_from(transaction.weight()).map_err(|_| rejected("feeTooLow"))?;
    if u128::from(proofs.base.fee) < checked.fee_rate.calculate_fee_from_weight(weight) {
        return Err(rejected("feeTooLow"));
    }

    verify_change(checked, prefix, &proofs.base)
}

/// The change, scanned with the view key, pays the planned amount to the primary address;
/// so does the recipient output of a self-send.
fn verify_change(checked: &Checked, prefix: &TransactionPrefix, base: &RctBase) -> Result<()> {
    let extra = Extra::read(&mut prefix.extra.as_slice()).map_err(|_| rejected("extra"))?;
    let (keys, additional) = extra.keys().ok_or_else(|| rejected("extra"))?;
    let owned = |index: usize| -> Result<Option<u64>> {
        let output = &prefix.outputs[index];
        let candidates = keys
            .iter()
            .chain(additional.as_ref().and_then(|keys| keys.get(index)));
        for key in candidates {
            let derivation = Zeroizing::new(
                (*checked.view * Point::into(*key))
                    .mul_by_cofactor()
                    .compress()
                    .to_bytes(),
            );
            let mut hashed = Zeroizing::new(derivation.to_vec());
            VarInt::write(&(index as u64), &mut *hashed).expect("writing to a Vec cannot fail");
            let view_tag = keccak256([b"view_tag".as_slice(), &hashed].concat())[0];
            let shared = Zeroizing::new(Scalar::hash(&*hashed));
            if output.view_tag != Some(view_tag)
                || (Scalar::into(*shared) * G + checked.change_spend)
                    .compress()
                    .to_bytes()
                    != output.key.to_bytes()
            {
                continue;
            }
            let shared_bytes = Zeroizing::new(<[u8; 32]>::from(*shared));
            let EncryptedAmount::Compact { amount } = &base.encrypted_amounts[index] else {
                return Err(rejected("shape"));
            };
            let pad = keccak256([b"amount".as_slice(), shared_bytes.as_slice()].concat());
            let amount = u64::from_le_bytes(*amount)
                ^ u64::from_le_bytes(pad[..8].try_into().expect("8 of 32 bytes"));
            let mask =
                Scalar::hash([b"commitment_mask".as_slice(), shared_bytes.as_slice()].concat());
            if Commitment::new(mask, amount).commit().compress() != base.commitments[index] {
                return Err(rejected("changeCommitmentMismatch"));
            }
            return Ok(Some(amount));
        }
        Ok(None)
    };

    if owned(checked.change_index)? != Some(checked.change_amount) {
        return Err(rejected("changeMismatch"));
    }
    if checked.self_send && owned(checked.recipient_index)? != Some(checked.recipient_amount) {
        return Err(rejected("changeMismatch"));
    }
    Ok(())
}

/// [`crate::review::verify_signed`] for a hardware device plan.
pub fn verify_signed(
    plan_json: &str,
    request: &ReviewRequest,
    raw_hex: &str,
) -> Result<Transaction> {
    let plan = parse_plan(plan_json)?;
    let checked = check_plan(&plan)?;
    check_review(&plan, &checked, request)?;
    if raw_hex.len() > MAX_HEX_LEN {
        return Err(review::mismatch("rawHex"));
    }
    let raw = hex::decode(raw_hex).map_err(|_| review::mismatch("rawHex"))?;
    let mut reader = raw.as_slice();
    let transaction = Transaction::read(&mut reader).map_err(|_| review::mismatch("rawHex"))?;
    if !reader.is_empty() {
        return Err(review::mismatch("trailingBytes"));
    }
    verify_transaction(&checked, &transaction)?;
    Ok(transaction)
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::spend::test_support::SeededRng;
    use curve25519_dalek::Scalar as Dalek;
    use monero_wallet::{
        address::SubaddressIndex,
        ringct::clsag::{ClsagContext, Decoys},
        send::{Change, SignableTransaction},
        ViewPair, WalletOutput,
    };
    use rusqlite::params;

    /// A hardware account: the runtime holds the view key, the (software) device the spend key.
    struct Wallet {
        spend: Dalek,
        view: Dalek,
        pair: ViewPair,
        db: Db,
        id: i64,
    }

    fn random_scalar(rng: &mut SeededRng) -> Dalek {
        let mut wide = [0u8; 64];
        rng.fill_bytes(&mut wide);
        Dalek::from_bytes_mod_order_wide(&wide)
    }

    fn random_point(rng: &mut SeededRng) -> EdwardsPoint {
        random_scalar(rng) * G
    }

    fn wallet(rng: &mut SeededRng) -> Wallet {
        let spend = random_scalar(rng);
        // OneKey derives the view key as Hs(spend).
        let view: Dalek = Scalar::hash(spend.to_bytes()).into();
        let mut db = Db::open(":memory:", Network::Mainnet).unwrap();
        let id = db
            .add_identity(
                "hw-1",
                &(spend * G).compress().to_bytes(),
                &view.to_bytes(),
                100,
            )
            .unwrap()
            .id;
        let pair =
            ViewPair::new(Point::from(spend * G), Zeroizing::new(Scalar::from(view))).unwrap();
        Wallet {
            spend,
            view,
            pair,
            db,
            id,
        }
    }

    fn subaddress_secret(view: &Dalek, minor: u32) -> Dalek {
        if minor == 0 {
            return Dalek::ZERO;
        }
        Scalar::hash(
            [
                b"SubAddr\0".as_slice(),
                &view.to_bytes(),
                &0u32.to_le_bytes(),
                &minor.to_le_bytes(),
            ]
            .concat(),
        )
        .into()
    }

    /// `(8·shared || varint(index), Hs(8·shared || varint(index)))`.
    fn derive(shared: EdwardsPoint, index: u64) -> (Vec<u8>, Dalek) {
        let mut bytes = shared.mul_by_cofactor().compress().to_bytes().to_vec();
        VarInt::write(&index, &mut bytes).unwrap();
        let scalar = Scalar::hash(&bytes).into();
        (bytes, scalar)
    }

    /// How an output reached the wallet.
    #[derive(Clone, Copy)]
    enum Via {
        Primary,
        Subaddress(u32),
        /// To a subaddress through the transaction's additional key.
        Additional(u32),
        /// To the primary address, the transaction listing several R.
        ManyKeys,
    }

    struct Owned {
        id: i64,
        input: OutputWithDecoys,
        key_image: [u8; 32],
        amount: u64,
        global: u64,
        output_key: [u8; 32],
        mask: [u8; 32],
        minor: u32,
        tx_key: EdwardsPoint,
        additional: Option<EdwardsPoint>,
    }

    /// Stores an output paid to the wallet (as scanning does) with a ring of 16 around it.
    fn receive(w: &Wallet, rng: &mut SeededRng, n: u64, amount: u64, via: Via) -> Owned {
        let minor = match via {
            Via::Subaddress(minor) | Via::Additional(minor) => minor,
            Via::Primary | Via::ManyKeys => 0,
        };
        let m = subaddress_secret(&w.view, minor);
        let spend_key = w.spend * G + m * G;
        let index_in_tx = 1u64;
        let r = random_scalar(rng);
        // Paying a subaddress D the sender's key is r·D; the primary address, r·G.
        let used = if minor == 0 { r * G } else { r * spend_key };
        let (tx_keys, additional, tx_key, real_additional) = match via {
            Via::Primary | Via::Subaddress(_) => (vec![used], None, used, None),
            Via::ManyKeys => (
                vec![random_point(rng), used, random_point(rng)],
                None,
                used,
                None,
            ),
            Via::Additional(_) => {
                let main = random_point(rng);
                (
                    vec![main],
                    Some(vec![random_point(rng), used]),
                    main,
                    Some(used),
                )
            }
        };
        let (_, shared) = derive(w.view * used, index_in_tx);
        let key = shared * G + spend_key;
        let key_offset = shared + m;
        let mask = random_scalar(rng);
        let commitment = Commitment::new(Scalar::from(mask), amount);

        let global = 10_000 * n + 5_000;
        let real = u64::from(rng.next_u32() % 16);
        let positions: Vec<u64> = (0..16).map(|k| global - real * 13 + k * 13).collect();
        let ring = (0..16)
            .map(|k| {
                if k == real {
                    [Point::from(key), commitment.commit()]
                } else {
                    [
                        Point::from(random_point(rng)),
                        Point::from(random_point(rng)),
                    ]
                }
            })
            .collect();
        let decoys = Decoys::new(relative_offsets(&positions).unwrap(), real as u8, ring).unwrap();
        let mut bytes = key.compress().to_bytes().to_vec();
        Scalar::from(key_offset).write(&mut bytes).unwrap();
        commitment.write(&mut bytes).unwrap();
        decoys.write(&mut bytes).unwrap();
        let input = OutputWithDecoys::read(&mut bytes.as_slice()).unwrap();

        let txid = keccak256(n.to_le_bytes());
        let mut blob = txid.to_vec();
        blob.extend(index_in_tx.to_le_bytes());
        blob.extend(global.to_le_bytes());
        blob.extend(key.compress().to_bytes());
        Scalar::from(key_offset).write(&mut blob).unwrap();
        commitment.write(&mut blob).unwrap();
        blob.push(0); // no timelock
        if minor == 0 {
            blob.push(0);
        } else {
            blob.push(1);
            blob.extend(0u32.to_le_bytes());
            blob.extend(minor.to_le_bytes());
        }
        blob.extend([0, 0]); // no payment ID, no data
        WalletOutput::read(&mut blob.as_slice()).expect("fixture must deserialize");

        let generator: EdwardsPoint = Point::biased_hash(key.compress().to_bytes()).into();
        let key_image = ((w.spend + key_offset) * generator).compress().to_bytes();
        let concat = |points: &[EdwardsPoint]| -> Vec<u8> {
            points
                .iter()
                .flat_map(|p| p.compress().to_bytes())
                .collect()
        };
        w.db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                   amount_atomic, height, key_image, tx_pub_keys, tx_additional_pub_keys,
                   output_key, subaddress_major, subaddress_minor)
                 VALUES (?1, ?2, ?3, ?4, ?5, ?6, 10, ?7, ?8, ?9, ?10, ?11, ?12)",
                params![
                    w.id,
                    blob,
                    txid.to_vec(),
                    global as i64,
                    index_in_tx as i64,
                    amount as i64,
                    key_image.to_vec(),
                    concat(&tx_keys),
                    additional.as_deref().map(concat),
                    key.compress().to_bytes().to_vec(),
                    (minor != 0).then_some(0i64),
                    (minor != 0).then_some(i64::from(minor)),
                ],
            )
            .unwrap();
        Owned {
            id: w.db.conn().last_insert_rowid(),
            input,
            key_image,
            amount,
            global,
            output_key: key.compress().to_bytes(),
            mask: mask.to_bytes(),
            minor,
            tx_key,
            additional: real_additional,
        }
    }

    fn fee_rate() -> FeeRate {
        FeeRate::new(20_000, FEE_QUANTIZATION_MASK).unwrap()
    }

    #[derive(Clone, Copy, PartialEq, Eq)]
    enum To {
        Standard,
        Subaddress,
        Integrated,
        OwnPrimary,
    }

    struct Scenario {
        w: Wallet,
        owned: Vec<Owned>,
        prepared: Prepared,
        recipient: MoneroAddress,
        max_send: bool,
    }

    const XMR: u64 = 1_000_000_000_000;

    /// A prepared send as `prepareTransaction` builds it (oxide's selection, fee and rings).
    fn scenario(seed: u64, inputs: &[Via], to: To, max_send: bool) -> Scenario {
        let mut rng = SeededRng::new(seed);
        let w = wallet(&mut rng);
        let owned: Vec<Owned> = inputs
            .iter()
            .enumerate()
            .map(|(n, via)| receive(&w, &mut rng, n as u64 + 1, (n as u64 + 2) * XMR, *via))
            .collect();
        let other = ViewPair::new(
            Point::from(random_point(&mut rng)),
            Zeroizing::new(Scalar::from(random_scalar(&mut rng))),
        )
        .unwrap();
        let recipient = match to {
            To::Standard => other.legacy_address(Network::Mainnet),
            To::Subaddress => {
                other.subaddress(Network::Mainnet, SubaddressIndex::new(0, 3).unwrap())
            }
            To::Integrated => other.legacy_integrated_address(Network::Mainnet, [9; 8]),
            To::OwnPrimary => w.pair.legacy_address(Network::Mainnet),
        };

        let inputs: Vec<OutputWithDecoys> = owned.iter().map(|o| o.input.clone()).collect();
        let total: u64 = owned.iter().map(|o| o.amount).sum();
        let build = |paid| {
            SignableTransaction::new(
                RctType::ClsagBulletproofPlus,
                Zeroizing::new([7; 32]),
                inputs.clone(),
                vec![(recipient, paid)],
                Change::new(w.pair.clone(), None),
                vec![],
                fee_rate(),
            )
            .unwrap()
        };
        let paid = if max_send {
            total - build(1).necessary_fee()
        } else {
            XMR + 123_456
        };
        let signable = build(paid);
        let prepared = Prepared {
            fee_atomic: signable.necessary_fee(),
            signable,
            output_ids: owned.iter().map(|o| o.id).collect(),
            inputs,
            inputs_atomic: total,
            amount_atomic: paid,
        };
        Scenario {
            w,
            owned,
            prepared,
            recipient,
            max_send,
        }
    }

    fn plan_json(s: &Scenario, seed: u64) -> String {
        let plan = build_plan(
            &s.w.db,
            s.w.id,
            &s.prepared,
            &s.recipient,
            fee_rate().per_weight(),
            &mut SeededRng::new(seed),
        )
        .unwrap();
        serde_json::to_string(&plan).unwrap()
    }

    fn review(s: &Scenario) -> ReviewRequest {
        ReviewRequest {
            network: "mainnet".into(),
            from_address: s.w.pair.legacy_address(Network::Mainnet).to_string(),
            to_address: s.recipient.to_string(),
            amount_atomic: s.prepared.amount_atomic.to_string(),
            fee_atomic: s.prepared.fee_atomic.to_string(),
            change_atomic: s.prepared.change_atomic().to_string(),
            is_max_send: s.max_send,
        }
    }

    /// Ways a faulty or malicious device departs from the protocol while staying self-consistent.
    #[derive(Clone, Copy, PartialEq, Eq)]
    enum Tamper {
        None,
        AscendingKeyImages,
        /// Moves one piconero from the change to the recipient.
        ShiftChange,
        /// Pays the change to a key that is not the account's.
        ChangeElsewhere,
        /// Proves the range for other commitments than the outputs'.
        BadRangeProof,
    }

    /// The device side of the protocol for the wallet's keys, following the firmware
    /// (`signing/step_01..10`): returns the SDK result with the CLSAGs already opened.
    fn device_sign(w: &Wallet, plan: &DevicePlan, rng: &mut SeededRng, tamper: Tamper) -> Value {
        let tsx = &plan.tsx_data;
        let pt = |value: &str| point(value).unwrap();

        // Step 2: one-time key, key image and vin per input, in original order.
        let mut inputs = Vec::new();
        for src in &plan.inputs {
            let out_key = pt(&src.outputs[src.real_output as usize].key.dest);
            let m = subaddress_secret(&w.view, src.subaddr_minor);
            let keys = std::iter::once(pt(&src.real_out_tx_key))
                .chain(src.real_out_additional_tx_keys.first().map(|k| pt(k)));
            let x = keys
                .map(|r| derive(w.view * r, src.real_output_in_tx_index).1 + w.spend + m)
                .find(|x| x * G == out_key)
                .expect("the device owns every input");
            let generator: EdwardsPoint = Point::biased_hash(out_key.compress().to_bytes()).into();
            let key_image = CompressedPoint::from((x * generator).compress().to_bytes());
            let positions: Vec<u64> = src.outputs.iter().map(|o| o.idx).collect();
            let offsets = relative_offsets(&positions).unwrap();
            let vini = Input::ToKey {
                amount: None,
                key_offsets: offsets.clone(),
                key_image,
            }
            .serialize();
            let ring = src
                .outputs
                .iter()
                .map(|o| {
                    [
                        Point::from(pt(&o.key.dest)),
                        Point::from(pt(&o.key.commitment)),
                    ]
                })
                .collect();
            let mask = Scalar::read(&mut hex32(&src.mask).unwrap().as_slice()).unwrap();
            let context = ClsagContext::new(
                Decoys::new(offsets, src.real_output as u8, ring).unwrap(),
                Commitment::new(mask, src.amount),
            )
            .expect("mask and amount open the commitment");
            inputs.push((vini, key_image.to_bytes(), x, context));
        }
        // The host sorts by key image, descending.
        let mut order: Vec<usize> = (0..inputs.len()).collect();
        order.sort_by(|i, j| inputs[*j].1.cmp(&inputs[*i].1));
        if tamper == Tamper::AscendingKeyImages {
            order.reverse();
        }

        // Steps 1 and 6: tx key, outputs and the range proof.
        let r = random_scalar(rng);
        let change = &tsx.change_dts;
        let others: Vec<&DestinationEntry> = tsx
            .outputs
            .iter()
            .filter(|o| o.addr != change.addr)
            .collect();
        let tx_pub = match others.as_slice() {
            [only] if only.is_subaddress => r * pt(&only.addr.spend_public_key),
            _ => r * G,
        };
        let change_index = tsx.outputs.iter().position(|o| o == change).unwrap();
        let mut amounts: Vec<u64> = tsx.outputs.iter().map(|o| o.amount).collect();
        if tamper == Tamper::ShiftChange {
            amounts[change_index] -= 1;
            amounts[1 - change_index] += 1;
        }
        let mut tx_outs = vec![];
        let mut out_pks = vec![];
        let mut ecdhs = vec![];
        let mut openings = vec![];
        for (i, dst) in tsx.outputs.iter().enumerate() {
            let to_change = dst.addr == change.addr;
            let shared = if to_change {
                w.view * tx_pub
            } else {
                r * pt(&dst.addr.view_public_key)
            };
            let (hashed, scalar) = derive(shared, i as u64);
            let spend = if to_change && tamper == Tamper::ChangeElsewhere {
                random_point(rng)
            } else {
                pt(&dst.addr.spend_public_key)
            };
            let key = scalar * G + spend;
            let view_tag = keccak256([b"view_tag".as_slice(), &hashed].concat())[0];
            let mask = Scalar::hash([b"commitment_mask".as_slice(), &scalar.to_bytes()].concat());
            let pad = keccak256([b"amount".as_slice(), &scalar.to_bytes()].concat());
            let opening = Commitment::new(mask, amounts[i]);
            tx_outs.push(
                Output {
                    amount: None,
                    key: CompressedPoint::from(key.compress().to_bytes()),
                    view_tag: Some(view_tag),
                }
                .serialize(),
            );
            out_pks.push(
                [
                    key.compress().to_bytes(),
                    opening.commit().compress().to_bytes(),
                ]
                .concat(),
            );
            ecdhs.push(
                (amounts[i] ^ u64::from_le_bytes(pad[..8].try_into().unwrap())).to_le_bytes(),
            );
            openings.push(opening);
        }
        let sum_masks: Dalek = openings.iter().map(|o| o.mask.into()).sum();
        let mut proven = openings.clone();
        if tamper == Tamper::BadRangeProof {
            proven[0].amount += 1;
        }
        let bulletproof = Bulletproof::prove_plus(rng, proven).unwrap();

        // Step 7: extra with the tx key and the (dummy or real) encrypted payment ID.
        let payment_id: [u8; 8] = tsx
            .payment_id
            .as_ref()
            .map(|id| hex::decode(id).unwrap().try_into().unwrap())
            .unwrap_or([0; 8]);
        let id_key = others.iter().find(|o| o.amount > 0).map_or_else(
            || pt(&change.addr.view_public_key),
            |o| pt(&o.addr.view_public_key),
        );
        let mut id_derivation = (r * id_key)
            .mul_by_cofactor()
            .compress()
            .to_bytes()
            .to_vec();
        id_derivation.push(0x8d);
        let pad = keccak256(id_derivation);
        let mut extra = vec![1];
        extra.extend(tx_pub.compress().to_bytes());
        extra.extend([2, 9, 1]);
        extra.extend(payment_id.iter().zip(pad).map(|(id, pad)| id ^ pad));

        let prefix = TransactionPrefix {
            additional_timelock: Timelock::None,
            inputs: order
                .iter()
                .map(|i| Input::read(&mut inputs[*i].0.as_slice()).unwrap())
                .collect(),
            outputs: tx_outs
                .iter()
                .map(|o| Output::read(true, &mut o.as_slice()).unwrap())
                .collect(),
            extra: extra.clone(),
        };
        let unsigned = Transaction::V2 {
            prefix: prefix.clone(),
            proofs: Some(RctProofs {
                base: RctBase {
                    fee: tsx.fee,
                    pseudo_outs: vec![],
                    encrypted_amounts: ecdhs
                        .iter()
                        .map(|amount| EncryptedAmount::Compact { amount: *amount })
                        .collect(),
                    commitments: openings.iter().map(|o| o.commit().compress()).collect(),
                },
                prunable: RctPrunable::Clsag {
                    bulletproof: bulletproof.clone(),
                    clsags: vec![],
                    pseudo_outs: vec![],
                },
            }),
        };
        let full_message = unsigned.signature_hash().unwrap();

        // Step 9: CLSAGs in the sorted order; pseudo-out masks sum to the outputs'.
        let signed = Clsag::sign(
            rng,
            order
                .iter()
                .map(|i| {
                    (
                        Zeroizing::new(Scalar::from(inputs[*i].2)),
                        inputs[*i].3.clone(),
                    )
                })
                .collect(),
            Scalar::from(sum_masks),
            full_message,
        )
        .unwrap();
        let clsags: Vec<String> = signed
            .iter()
            .map(|(clsag, _)| {
                let mut bytes = vec![];
                VarInt::write(&clsag.s.len(), &mut bytes).unwrap();
                clsag.write(&mut bytes).unwrap();
                hex::encode(bytes)
            })
            .collect();
        let prefix_hash = hex::encode(prefix_hash(&prefix));
        json!({
            "origIndices": order,
            "vinis": order.iter().map(|i| hex::encode(&inputs[*i].0)).collect::<Vec<_>>(),
            "pseudoOuts": signed
                .iter()
                .map(|(_, pseudo_out)| hex::encode(pseudo_out.compress().to_bytes()))
                .collect::<Vec<_>>(),
            "clsags": clsags,
            "txOuts": tx_outs.iter().map(hex::encode).collect::<Vec<_>>(),
            "outPks": out_pks.iter().map(hex::encode).collect::<Vec<_>>(),
            "ecdhInfos": ecdhs.iter().map(hex::encode).collect::<Vec<_>>(),
            "rsig": hex::encode(bulletproof.serialize()),
            "extra": hex::encode(&extra),
            "txPrefixHash": prefix_hash,
            "fullMessageHash": hex::encode(full_message),
            "rv": { "txnFee": tsx.fee.to_string(), "message": prefix_hash, "rvType": 6 },
        })
    }

    fn sign(s: &Scenario, plan: &str, seed: u64, tamper: Tamper) -> Value {
        device_sign(
            &s.w,
            &parse_plan(plan).unwrap(),
            &mut SeededRng::new(seed),
            tamper,
        )
    }

    fn reason(e: RuntimeError) -> String {
        e.params["reason"].as_str().unwrap_or_default().to_owned()
    }

    fn assemble_err(plan: &str, signed: &Value) -> String {
        match assemble(plan, &signed.to_string()) {
            Ok(_) => panic!("assembly must be refused"),
            Err(e) => reason(e),
        }
    }

    /// Every layout the app sends, signed by the software device, assembles; the result
    /// passes broadcast verification and pays exactly the fee oxide computed for its weight.
    #[test]
    fn device_signed_transactions_assemble_and_verify_end_to_end() {
        let cases: [(&[Via], To, bool); 6] = [
            (&[Via::Primary, Via::ManyKeys], To::Standard, false),
            (
                &[Via::Subaddress(2), Via::Additional(5)],
                To::Subaddress,
                false,
            ),
            (&[Via::Primary], To::Integrated, false),
            (&[Via::Primary, Via::Subaddress(1)], To::OwnPrimary, false),
            (
                &[Via::Primary, Via::ManyKeys, Via::Additional(4)],
                To::Standard,
                true,
            ),
            (&[Via::Subaddress(7)], To::OwnPrimary, true),
        ];
        for (seed, (vias, to, max_send)) in cases.into_iter().enumerate() {
            let seed = seed as u64;
            let s = scenario(seed, vias, to, max_send);
            for plan_seed in [0, 1] {
                let plan = plan_json(&s, plan_seed);
                let signed = sign(&s, &plan, seed + 100, Tamper::None);
                let result = assemble(&plan, &signed.to_string()).unwrap();
                let raw = result["rawHex"].as_str().unwrap();
                let tx = Transaction::read(&mut hex::decode(raw).unwrap().as_slice()).unwrap();
                assert_eq!(result["txid"], hex::encode(tx.hash()));
                assert_eq!(
                    fee_rate().calculate_fee_from_weight(tx.weight() as u64),
                    u128::from(s.prepared.fee_atomic),
                    "the device's weight is what oxide priced"
                );

                // Sync will find the change (and a self-send's payment) at the planned amounts.
                let parsed = parse_plan(&plan).unwrap();
                let checked = check_plan(&parsed).unwrap();
                let mut wanted = vec![(checked.change_index as u64, s.prepared.change_atomic())];
                if to == To::OwnPrimary {
                    wanted.push((checked.recipient_index as u64, s.prepared.amount_atomic));
                    wanted.sort();
                }
                assert_eq!(scanned(&s.w, &tx), wanted);

                let request = review(&s);
                review::verify_unsigned(&plan, &request).unwrap();
                let verified = review::verify_signed(&plan, &request, raw).unwrap();
                assert_eq!(verified.hash(), tx.hash());
                let amounts =
                    spend::reviewed_broadcast_amounts(&plan, &request, raw, None, None).unwrap();
                assert_eq!(amounts.change_atomic, Some(s.prepared.change_atomic()));
                assert_eq!(
                    amounts.outgoing_atomic,
                    Some(s.prepared.amount_atomic + s.prepared.fee_atomic)
                );
                if max_send {
                    assert_eq!(s.prepared.change_atomic(), 0);
                }
            }
        }
    }

    /// `(index, amount)` of the outputs oxide's scanner, which sync uses, finds for the wallet.
    fn scanned(w: &Wallet, tx: &Transaction) -> Vec<(u64, u64)> {
        use monero_wallet::{
            block::{Block, BlockHeader},
            interface::ScannableBlock,
            Scanner,
        };
        let miner = Transaction::V2 {
            prefix: TransactionPrefix {
                additional_timelock: Timelock::Block(160),
                inputs: vec![Input::Gen(100)],
                outputs: vec![],
                extra: vec![],
            },
            proofs: None,
        };
        let header = BlockHeader {
            hardfork_version: 16,
            hardfork_signal: 16,
            timestamp: 0,
            previous: [0; 32],
            nonce: 0,
        };
        let block = ScannableBlock {
            block: Block::new(header, miner, vec![tx.hash()]).unwrap(),
            transactions: vec![tx.clone().into()],
            output_index_for_first_ringct_output: Some(0),
        };
        Scanner::new(w.pair.clone())
            .scan(block)
            .unwrap()
            .ignore_additional_timelock()
            .iter()
            .map(|output| (output.index_in_transaction(), output.commitment().amount))
            .collect()
    }

    fn keys_of(value: &Value) -> BTreeSet<&str> {
        value
            .as_object()
            .unwrap()
            .keys()
            .map(String::as_str)
            .collect()
    }

    /// The plan has exactly the protobuf fields the device takes, with the values it needs.
    #[test]
    fn the_plan_carries_what_the_device_needs() {
        let vias = [
            Via::Primary,
            Via::Subaddress(2),
            Via::Additional(5),
            Via::ManyKeys,
        ];
        let s = scenario(7, &vias, To::Standard, false);
        let plan: Value = serde_json::from_str(&plan_json(&s, 3)).unwrap();

        assert_eq!(
            keys_of(&plan),
            BTreeSet::from(["version", "networkType", "tsxData", "inputs", "expected"])
        );
        assert_eq!(
            (plan["version"].clone(), plan["networkType"].clone()),
            (json!(1), json!(0))
        );
        let tsx = &plan["tsxData"];
        assert_eq!(
            keys_of(tsx),
            BTreeSet::from([
                "version",
                "client_version",
                "hard_fork",
                "unlock_time",
                "num_inputs",
                "mixin",
                "fee",
                "account",
                "minor_indices",
                "outputs",
                "change_dts",
                "rsig_data",
                "integrated_indices",
            ]),
            "no payment_id without an integrated recipient, and never null"
        );
        for (field, value) in [
            ("version", json!(1)),
            ("client_version", json!(3)),
            ("hard_fork", json!(16)),
            ("unlock_time", json!(0)),
            ("num_inputs", json!(4)),
            ("mixin", json!(15)),
            ("fee", json!(s.prepared.fee_atomic.to_string())),
            ("account", json!(0)),
            ("minor_indices", json!([0, 2, 5])),
            (
                "rsig_data",
                json!({ "rsig_type": 3, "bp_version": 4, "grouping": [2] }),
            ),
            ("integrated_indices", json!([])),
        ] {
            assert_eq!(tsx[field], value, "{field}");
        }

        let change = &tsx["change_dts"];
        let primary = |v: &Value| {
            v["addr"]
                == json!({
                    "spend_public_key": hex::encode(s.w.pair.spend().compress().to_bytes()),
                    "view_public_key": hex::encode(s.w.pair.view().compress().to_bytes()),
                })
        };
        assert!(primary(change));
        assert_eq!(change["amount"], s.prepared.change_atomic().to_string());
        assert_eq!(
            (
                change["is_subaddress"].clone(),
                change["is_integrated"].clone()
            ),
            (json!(false), json!(false))
        );
        let outputs = tsx["outputs"].as_array().unwrap();
        assert_eq!(outputs.len(), 2);
        let change_index = outputs.iter().position(|o| o == change).unwrap();
        let recipient = &outputs[1 - change_index];
        assert_eq!(
            keys_of(recipient),
            BTreeSet::from(["amount", "addr", "is_subaddress", "is_integrated"])
        );
        assert_eq!(recipient["amount"], s.prepared.amount_atomic.to_string());
        assert_eq!(
            recipient["addr"]["spend_public_key"],
            hex::encode(s.recipient.spend().compress().to_bytes())
        );
        assert!(!primary(recipient));

        let inputs = plan["inputs"].as_array().unwrap();
        assert_eq!(inputs.len(), 4);
        for (input, owned) in inputs.iter().zip(&s.owned) {
            assert_eq!(
                keys_of(input),
                BTreeSet::from([
                    "outputs",
                    "real_output",
                    "real_out_tx_key",
                    "real_out_additional_tx_keys",
                    "real_output_in_tx_index",
                    "amount",
                    "rct",
                    "mask",
                    "subaddr_minor",
                ])
            );
            let ring = input["outputs"].as_array().unwrap();
            assert_eq!(ring.len(), 16);
            let idx: Vec<u64> = ring.iter().map(|o| o["idx"].as_u64().unwrap()).collect();
            assert!(
                idx.windows(2).all(|w| w[0] < w[1]),
                "ascending by global index"
            );
            let real = &ring[input["real_output"].as_u64().unwrap() as usize];
            assert_eq!(real["idx"], owned.global);
            assert_eq!(real["key"]["dest"], hex::encode(owned.output_key));
            assert_eq!(input["real_out_tx_key"], hex_point(owned.tx_key));
            assert_eq!(
                input["real_out_additional_tx_keys"],
                json!(owned
                    .additional
                    .map(hex_point)
                    .into_iter()
                    .collect::<Vec<_>>())
            );
            assert_eq!(input["real_output_in_tx_index"], 1);
            assert_eq!(input["amount"], owned.amount.to_string());
            assert_eq!(input["rct"], true);
            assert_eq!(input["mask"], hex::encode(owned.mask));
            assert_eq!(input["subaddr_minor"], owned.minor);
        }

        let expected = &plan["expected"];
        assert_eq!(
            expected["keyImages"],
            json!(s
                .owned
                .iter()
                .map(|o| hex::encode(o.key_image))
                .collect::<Vec<_>>())
        );
        assert_eq!(expected["fee"], s.prepared.fee_atomic.to_string());
        assert_eq!(
            expected["recipientAmount"],
            s.prepared.amount_atomic.to_string()
        );
        assert_eq!(
            expected["changeAmount"],
            s.prepared.change_atomic().to_string()
        );
        assert_eq!(expected["feePerWeight"], "20000");
        assert_eq!(expected["viewKey"], hex::encode(s.w.view.to_bytes()));
    }

    #[test]
    fn an_integrated_recipient_carries_its_payment_id() {
        let s = scenario(8, &[Via::Primary], To::Integrated, false);
        let plan: Value = serde_json::from_str(&plan_json(&s, 0)).unwrap();
        let tsx = &plan["tsxData"];
        assert_eq!(tsx["payment_id"], hex::encode([9u8; 8]));
        let index = tsx["integrated_indices"][0].as_u64().unwrap() as usize;
        assert_eq!(tsx["integrated_indices"].as_array().unwrap().len(), 1);
        assert_eq!(tsx["outputs"][index]["is_integrated"], true);
        assert_ne!(tsx["outputs"][index], tsx["change_dts"]);

        let s = scenario(8, &[Via::Primary], To::Subaddress, false);
        let plan: Value = serde_json::from_str(&plan_json(&s, 0)).unwrap();
        let outputs = plan["tsxData"]["outputs"].as_array().unwrap();
        assert!(outputs.iter().any(|o| o["is_subaddress"] == true));
    }

    /// The device keeps the order it is given, so the plan shuffles recipient and change.
    #[test]
    fn recipient_and_change_are_shuffled() {
        let s = scenario(9, &[Via::Primary], To::Standard, false);
        let positions: BTreeSet<usize> = (0..16)
            .map(|seed| {
                let plan = parse_plan(&plan_json(&s, seed)).unwrap();
                check_plan(&plan).unwrap().change_index
            })
            .collect();
        assert_eq!(positions, BTreeSet::from([0, 1]));
    }

    #[test]
    fn an_input_without_a_key_image_is_refused() {
        let s = scenario(10, &[Via::Primary, Via::Primary], To::Standard, false);
        s.w.db
            .conn()
            .execute(
                "UPDATE outputs SET key_image = NULL WHERE id = ?1",
                [s.owned[1].id],
            )
            .unwrap();
        let e = build_plan(
            &s.w.db,
            s.w.id,
            &s.prepared,
            &s.recipient,
            20_000,
            &mut SeededRng::new(0),
        )
        .err()
        .expect("must be refused");
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "keyImageRequired");
        assert_eq!(e.params["outputId"], s.owned[1].id);
    }

    #[test]
    fn a_featured_recipient_is_refused() {
        let s = scenario(11, &[Via::Primary], To::Standard, false);
        let featured = MoneroAddress::new(
            Network::Mainnet,
            AddressType::Featured {
                subaddress: false,
                payment_id: None,
                guaranteed: true,
            },
            s.recipient.spend(),
            s.recipient.view(),
        );
        let e = build_plan(
            &s.w.db,
            s.w.id,
            &s.prepared,
            &featured,
            20_000,
            &mut SeededRng::new(0),
        )
        .err()
        .expect("must be refused");
        assert_eq!(e.code, ErrorCode::InvalidAddress);
        assert_eq!(e.params["reason"], "unsupportedByDevice");
    }

    /// A device that departs from the protocol, or a result altered on the way back,
    /// never yields a transaction.
    #[test]
    fn assembly_refuses_a_deviating_device() {
        let s = scenario(
            12,
            &[Via::Primary, Via::ManyKeys, Via::Subaddress(3)],
            To::Standard,
            false,
        );
        let plan = plan_json(&s, 0);
        let good = sign(&s, &plan, 1, Tamper::None);
        assemble(&plan, &good.to_string()).unwrap();

        for (tamper, expected) in [
            (Tamper::AscendingKeyImages, "keyImageOrder"),
            (Tamper::ShiftChange, "changeMismatch"),
            (Tamper::ChangeElsewhere, "changeMismatch"),
            (Tamper::BadRangeProof, "invalidRangeProof"),
        ] {
            assert_eq!(assemble_err(&plan, &sign(&s, &plan, 1, tamper)), expected);
        }

        let edit = |f: &dyn Fn(&mut Value)| {
            let mut signed = good.clone();
            f(&mut signed);
            assemble_err(&plan, &signed)
        };
        let flip = |value: &mut Value, at: usize| {
            let mut bytes = hex::decode(value.as_str().unwrap()).unwrap();
            bytes[at] ^= 1;
            *value = json!(hex::encode(bytes));
        };
        assert_eq!(
            edit(&|v| v["pseudoOuts"][0] = v["pseudoOuts"][1].clone()),
            "invalidSignature"
        );
        assert_eq!(edit(&|v| flip(&mut v["clsags"][1], 1)), "invalidSignature");
        assert_eq!(
            edit(&|v| flip(&mut v["txPrefixHash"], 0)),
            "prefixHashMismatch"
        );
        assert_eq!(
            edit(&|v| flip(&mut v["rv"]["message"], 0)),
            "prefixHashMismatch"
        );
        assert_eq!(
            edit(&|v| v["extra"] = json!(format!("{}00", v["extra"].as_str().unwrap()))),
            "prefixHashMismatch"
        );
        assert_eq!(
            edit(&|v| flip(&mut v["fullMessageHash"], 0)),
            "fullMessageHashMismatch"
        );
        assert_eq!(
            edit(&|v| flip(&mut v["ecdhInfos"][0], 0)),
            "fullMessageHashMismatch"
        );
        assert_eq!(
            edit(&|v| {
                let vinis = v["vinis"].as_array_mut().unwrap();
                vinis.swap(0, 1);
            }),
            "keyImageMismatch"
        );
        assert_eq!(
            edit(&|v| v["origIndices"] = json!([0, 0, 1])),
            "origIndices"
        );
        assert_eq!(
            edit(&|v| v["rv"]["txnFee"] = json!((s.prepared.fee_atomic + 1).to_string())),
            "feeMismatch"
        );
        assert_eq!(edit(&|v| v["rv"]["rvType"] = json!(5)), "rctType");
        assert_eq!(
            edit(&|v| {
                v["ecdhInfos"].as_array_mut().unwrap().pop();
            }),
            "count"
        );
        assert_eq!(edit(&|v| flip(&mut v["outPks"][0], 0)), "outPk");
        assert_eq!(
            edit(&|v| v["rsig"] = json!(format!("{}00", v["rsig"].as_str().unwrap()))),
            "rangeProof"
        );
        assert_eq!(
            edit(&|v| v["clsags"][0] =
                json!(format!("0f{}", &v["clsags"][0].as_str().unwrap()[2..]))),
            "signature"
        );
        let e = assemble(&plan, &json!({ "origIndices": [] }).to_string()).unwrap_err();
        assert_eq!(e.params["field"], "signedJson");
    }

    /// The plan that comes back is checked too: a different stored key image, a higher
    /// fee rate or an altered amount is refused.
    #[test]
    fn assembly_holds_the_device_to_the_plan() {
        let s = scenario(
            13,
            &[Via::Primary, Via::Additional(2)],
            To::Subaddress,
            false,
        );
        let plan = plan_json(&s, 1);
        let signed = sign(&s, &plan, 2, Tamper::None);
        let alter = |f: &dyn Fn(&mut Value)| {
            let mut altered: Value = serde_json::from_str(&plan).unwrap();
            f(&mut altered);
            altered.to_string()
        };

        let other_image = alter(&|p| {
            p["expected"]["keyImages"][0] = json!(hex::encode(
                random_point(&mut SeededRng::new(5)).compress().to_bytes()
            ));
        });
        assert_eq!(assemble_err(&other_image, &signed), "keyImageMismatch");

        let dearer = alter(&|p| {
            p["expected"]["feePerWeight"] = json!("40000");
        });
        assert_eq!(assemble_err(&dearer, &signed), "feeTooLow");

        let e = assemble(
            &alter(&|p| p["expected"]["changeAmount"] = json!("1")),
            &signed.to_string(),
        )
        .unwrap_err();
        assert_eq!(
            (e.params["field"].clone(), reason(e)),
            (json!("devicePlan"), "amounts".to_owned())
        );

        let e = assemble(
            &alter(&|p| p["tsxData"]["hard_fork"] = json!(15)),
            &signed.to_string(),
        )
        .unwrap_err();
        assert_eq!(reason(e), "unsupportedTransaction");

        let other_view = alter(&|p| p["expected"]["viewKey"] = json!(hex::encode([3u8; 32])));
        assert_eq!(
            reason(assemble(&other_view, &signed.to_string()).unwrap_err()),
            "viewKey"
        );
    }

    /// At broadcast the review the user confirmed is bound to the plan, and the raw
    /// transaction to both.
    #[test]
    fn broadcast_verification_binds_review_plan_and_transaction() {
        let s = scenario(14, &[Via::Primary, Via::ManyKeys], To::Standard, false);
        let plan = plan_json(&s, 0);
        let signed = sign(&s, &plan, 3, Tamper::None);
        let raw = assemble(&plan, &signed.to_string()).unwrap()["rawHex"]
            .as_str()
            .unwrap()
            .to_owned();
        let request = review(&s);
        review::verify_signed(&plan, &request, &raw).unwrap();

        let other = scenario(15, &[Via::Primary], To::Standard, false);
        for field in ["amount", "fee", "change", "max", "from", "to", "network"] {
            let mut changed = review(&s);
            match field {
                "amount" => changed.amount_atomic = (s.prepared.amount_atomic - 1).to_string(),
                "fee" => changed.fee_atomic = (s.prepared.fee_atomic + 1).to_string(),
                "change" => changed.change_atomic = "0".into(),
                "max" => changed.is_max_send = true,
                "from" => {
                    changed.from_address = other.w.pair.legacy_address(Network::Mainnet).to_string()
                }
                "to" => changed.to_address = other.recipient.to_string(),
                "network" => changed.network = "testnet".into(),
                _ => unreachable!(),
            }
            assert!(
                review::verify_signed(&plan, &changed, &raw).is_err(),
                "{field}"
            );
            assert!(review::verify_unsigned(&plan, &changed).is_err(), "{field}");
        }

        let mut tx: Transaction =
            Transaction::read(&mut hex::decode(&raw).unwrap().as_slice()).unwrap();
        assert!(review::verify_signed(&plan, &request, &format!("{raw}00")).is_err());
        if let Transaction::V2 {
            proofs: Some(ref mut proofs),
            ..
        } = tx
        {
            proofs.base.fee -= 1;
        }
        let e = review::verify_signed(&plan, &request, &hex::encode(tx.serialize())).unwrap_err();
        assert_eq!(reason(e), "feeMismatch");

        // Another account's transaction under this plan spends other key images.
        let other_plan = plan_json(&other, 0);
        let other_raw = assemble(
            &other_plan,
            &sign(&other, &other_plan, 4, Tamper::None).to_string(),
        )
        .unwrap()["rawHex"]
            .as_str()
            .unwrap()
            .to_owned();
        assert!(review::verify_signed(&plan, &request, &other_raw).is_err());
        // A device plan never reaches the software signer.
        let json = serde_json::to_string(&json!({
            "network": request.network, "fromAddress": request.from_address,
            "toAddress": request.to_address, "amountAtomic": request.amount_atomic,
            "feeAtomic": request.fee_atomic, "changeAtomic": request.change_atomic,
            "isMaxSend": request.is_max_send,
        }))
        .unwrap();
        assert!(spend::sign_for_host(&plan, &[1; 32], &json).is_err());
    }

    #[test]
    fn device_plans_are_told_from_signable_hex_by_content() {
        assert!(is_device_plan("{\"version\":1}"));
        assert!(is_device_plan(" \n{"));
        assert!(!is_device_plan("0102ab"));
        assert!(!is_device_plan(""));
    }
}
