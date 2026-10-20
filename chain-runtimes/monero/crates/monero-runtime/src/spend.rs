//! Spend path: key image import, transaction construction, signing, broadcast.
//!
//! ## The private spend key appears only here
//!
//! The host passes the account's BIP-32 key per call and the spend key is
//! reduced from it here; both are zeroized after use, never persisted, and
//! never returned across the wasm boundary.
//!
//! ## Why key images are imported
//!
//! A view-only wallet cannot tell whether its outputs were spent: that needs
//! the key image `(spend key + output offset) × H(output key)`, which requires
//! the private spend key.
//!
//! Scanning records outputs only; spend state is complete once the host calls
//! [`import_key_images`] on unlock. Until then the balance may be overstated.

use std::collections::HashMap;

use monero_wallet::{
    address::MoneroAddress,
    ed25519::{Point, Scalar},
    send::{Change, SignableTransaction},
    OutputWithDecoys, WalletOutput,
};
use rand_core::{CryptoRng, RngCore};
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};
use zeroize::Zeroizing;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::keys;
use crate::query;
use crate::wallet::Db;

/// Ring size. Current consensus is 16, including the real member.
///
/// Hardcoded rather than asked of the node: a smaller value weakens privacy
/// and may be rejected. It changes with hard forks via our releases.
pub const RING_LEN: u8 = 16;

/// Computes an output's key image from the private spend key.
///
/// Same formula as upstream `SignableTransaction::sign`:
/// `key_image = (spend + key_offset) × biased_hash(output_key)`.
///
/// Also checks that the key owns the output. A mismatch (wrong account seed)
/// errors instead of storing a meaningless key image that would never match a spend.
fn key_image_for(output: &WalletOutput, spend: &Zeroizing<Scalar>) -> Result<[u8; 32]> {
    use core::ops::Deref;

    let spend_dalek: Zeroizing<curve25519_dalek::Scalar> = Zeroizing::new((**spend).into());
    let offset: curve25519_dalek::Scalar = output.key_offset().into();
    let input_key = Zeroizing::new(*spend_dalek + offset);

    let derived =
        Point::from(input_key.deref() * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
    if derived.compress().to_bytes() != output.key().compress().to_bytes() {
        return Err(RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({
                "field": "spendPrivateKey",
                "reason": "doesNotMatchOutput",
            }),
        ));
    }

    let generator = Point::biased_hash(output.key().compress().to_bytes());
    let image =
        Point::from(input_key.deref() * curve25519_dalek::EdwardsPoint::from(generator.into()));
    Ok(image.compress().to_bytes())
}

/// Fills in key images for outputs that lack one, enabling spend detection.
///
/// Earlier spends are confirmed from possible spends recorded while scanning:
/// if the computed key image appears in one of the output's possible spends,
/// that is the real spend. Later spends are caught by normal scanning, so the
/// output's possible spends are then cleared.
pub fn import_key_images(
    db: &mut Db,
    identity_id: i64,
    spend_private: &[u8],
) -> Result<KeyImageImport> {
    let spend = parse_spend_key(spend_private)?;

    let pending: Vec<(i64, Vec<u8>, Vec<u8>, i64)> = {
        let mut stmt = db
            .conn()
            .prepare(
                "SELECT id, blob, txid, index_in_tx FROM outputs
                 WHERE identity_id = ?1 AND key_image IS NULL",
            )
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?;
        let rows = stmt
            .query_map([identity_id], |r| {
                Ok((r.get(0)?, r.get(1)?, r.get(2)?, r.get(3)?))
            })
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?
    };

    let computed = compute_key_images(pending, &spend);
    keys::scrub_stack();
    apply_key_images(db, identity_id, &computed?)
}

/// `(output id, blob, txid, index in tx)` of an output lacking a key image.
type PendingOutput = (i64, Vec<u8>, Vec<u8>, i64);
/// `(output id, key image, txid, index in tx)`.
type ComputedKeyImage = (i64, [u8; 32], Vec<u8>, i64);

// Separate frame so `scrub_stack` can overwrite what the key-image math left
// on the stack.
#[inline(never)]
fn compute_key_images(
    pending: Vec<PendingOutput>,
    spend: &Zeroizing<Scalar>,
) -> Result<Vec<ComputedKeyImage>> {
    let mut computed = Vec::with_capacity(pending.len());
    for (id, blob, txid, index) in pending {
        let output = WalletOutput::read(&mut blob.as_slice())
            .map_err(|e| db_err("deserializeOutput", format!("output {id}: {e}")))?;
        computed.push((id, key_image_for(&output, spend)?, txid, index));
    }
    Ok(computed)
}

/// Stores computed key images and confirms earlier spends from possible spends.
/// Separate from computation so the matching can be tested without real outputs.
fn apply_key_images(
    db: &mut Db,
    identity_id: i64,
    computed: &[(i64, [u8; 32], Vec<u8>, i64)],
) -> Result<KeyImageImport> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginImportKeyImages", e))?;
    let mut spends_found = 0;
    for (id, image, txid, index) in computed {
        tx.execute(
            "UPDATE outputs SET key_image = ?1 WHERE id = ?2",
            params![image.as_slice(), id],
        )
        .map_err(|e| db_err("updateKeyImage", e))?;

        let spent: Option<(Vec<u8>, i64, Option<i64>)> = tx
            .query_row(
                "SELECT spend_txid, height, timestamp FROM possible_spends
                 WHERE identity_id = ?1 AND out_txid = ?2 AND out_index = ?3 AND key_image = ?4
                 ORDER BY height LIMIT 1",
                params![identity_id, txid, index, image.as_slice()],
                |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)),
            )
            .optional()
            .map_err(|e| db_err("matchPossibleSpend", e))?;
        if let Some((spend_txid, height, timestamp)) = spent {
            tx.execute(
                "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
                 WHERE id = ?3 AND spent_at_height IS NULL",
                params![height, spend_txid, id],
            )
            .map_err(|e| db_err("markPossibleSpend", e))?;
            tx.execute(
                "INSERT INTO transactions (identity_id, txid, height, timestamp, direction)
                 VALUES (?1, ?2, ?3, ?4, 'out')
                 ON CONFLICT (identity_id, txid) DO UPDATE
                   SET direction = 'out', height = ?3, failed = 0,
                       timestamp = COALESCE(excluded.timestamp, transactions.timestamp)",
                params![identity_id, spend_txid, height, timestamp],
            )
            .map_err(|e| db_err("recordPossibleSpend", e))?;
            spends_found += 1;
        }
        tx.execute(
            "DELETE FROM possible_spends
             WHERE identity_id = ?1 AND out_txid = ?2 AND out_index = ?3",
            params![identity_id, txid, index],
        )
        .map_err(|e| db_err("clearPossibleSpends", e))?;
    }
    tx.commit()
        .map_err(|e| db_err("commitImportKeyImages", e))?;

    Ok(KeyImageImport {
        imported: computed.len(),
        spends_found,
    })
}

/// Result of `import_key_images`.
pub struct KeyImageImport {
    /// Outputs whose key image was computed.
    pub imported: usize,
    /// Of those, outputs confirmed as already spent via possible spends.
    pub spends_found: usize,
}

fn parse_spend_key(bytes: &[u8]) -> Result<Zeroizing<Scalar>> {
    let arr = Zeroizing::new(<[u8; 32]>::try_from(bytes).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPrivateKey", "byteLen": bytes.len(), "expected": 32 }),
        )
    })?);
    let scalar = Scalar::read(&mut arr.as_slice()).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPrivateKey", "reason": "notCanonicalScalar" }),
        )
    })?;
    Ok(Zeroizing::new(scalar))
}

/// A payment in a pending transaction.
#[derive(Debug, Clone)]
pub struct Payment {
    pub address: MoneroAddress,
    pub amount_atomic: u64,
}

/// Input selection result.
#[derive(Debug, Clone)]
pub struct Selection {
    pub output_ids: Vec<i64>,
    pub total_atomic: u64,
}

/// Selects inputs largest first until `needed` is covered.
///
/// Fees are by weight, so fewer inputs are cheaper, at the cost of breaking up
/// large outputs sooner; this matches wallet2's default.
pub fn select_inputs(
    db: &Db,
    identity_id: i64,
    chain_tip: u64,
    needed_atomic: u64,
) -> Result<Selection> {
    let rows = query::spendable_rows(db, identity_id, chain_tip)?;

    let mut picked = Vec::new();
    let mut total: u64 = 0;
    for row in rows {
        picked.push(row.id);
        total = total.checked_add(row.amount_atomic).ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "amount", "reason": "totalOverflow" }),
            )
        })?;
        if total >= needed_atomic {
            break;
        }
    }

    if total < needed_atomic {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({
                "field": "amount",
                "reason": "insufficientUnlockedFunds",
                "availableAtomic": total.to_string(),
                "neededAtomic": needed_atomic.to_string(),
            }),
        ));
    }

    Ok(Selection {
        output_ids: picked,
        total_atomic: total,
    })
}

/// A constructed transaction awaiting signature.
pub struct Prepared {
    pub signable: SignableTransaction,
    pub output_ids: Vec<i64>,
    pub inputs_atomic: u64,
    /// Total received by recipients. For max-send it is derived, not the host's input.
    pub amount_atomic: u64,
    pub fee_atomic: u64,
}

/// monerod joins rejection reasons by flag; a double spend reads "double spend".
fn is_double_spend(error: &RuntimeError) -> bool {
    error
        .detail
        .as_deref()
        .is_some_and(|reason| reason.to_lowercase().contains("double spend"))
}

/// Shares one RingCT output distribution across all inputs of a construction.
///
/// Upstream fetches the full-chain distribution (about 18MB) per decoy
/// selection, N times for N inputs, which easily times out when relayed through
/// the host on mobile. Same range is reused, so the node is asked once.
struct CachedDecoys<'a, P> {
    inner: &'a P,
    distributions:
        std::sync::Mutex<HashMap<(std::ops::Bound<usize>, std::ops::Bound<usize>), Vec<u64>>>,
}

impl<'a, P> CachedDecoys<'a, P> {
    fn new(inner: &'a P) -> Self {
        Self {
            inner,
            distributions: std::sync::Mutex::new(HashMap::new()),
        }
    }
}

impl<P: monero_wallet::interface::ProvidesDecoys> monero_wallet::interface::ProvidesBlockchainMeta
    for CachedDecoys<'_, P>
{
    fn latest_block_number(
        &self,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<usize, monero_wallet::interface::InterfaceError>,
    > {
        self.inner.latest_block_number()
    }
}

impl<P: monero_wallet::interface::ProvidesDecoys>
    monero_wallet::interface::ProvidesUnvalidatedDecoys for CachedDecoys<'_, P>
{
    fn ringct_output_distribution(
        &self,
        range: impl Send + std::ops::RangeBounds<usize>,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<Vec<u64>, monero_wallet::interface::InterfaceError>,
    > {
        let key = (range.start_bound().cloned(), range.end_bound().cloned());
        async move {
            let cached = self
                .distributions
                .lock()
                .ok()
                .and_then(|map| map.get(&key).cloned());
            if let Some(distribution) = cached {
                return Ok(distribution);
            }
            let distribution =
                monero_wallet::interface::ProvidesDecoys::ringct_output_distribution(
                    self.inner, key,
                )
                .await?;
            if let Ok(mut map) = self.distributions.lock() {
                map.insert(key, distribution.clone());
            }
            Ok(distribution)
        }
    }

    fn unlocked_ringct_outputs(
        &self,
        indexes: &[u64],
        evaluate_unlocked: monero_wallet::interface::EvaluateUnlocked,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<
            Vec<Option<[Point; 2]>>,
            monero_wallet::interface::TransactionsError,
        >,
    > {
        monero_wallet::interface::ProvidesDecoys::unlocked_ringct_outputs(
            self.inner,
            indexes,
            evaluate_unlocked,
        )
    }
}

/// Picks a ring of decoys for one output.
///
/// Decoys must be selected at the current height; a stale height skews the
/// ring distribution into a statistical fingerprint.
async fn ring_for<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    output_id: i64,
    chain_tip: u64,
) -> Result<OutputWithDecoys> {
    let output = query::load_output(db, output_id)?;
    OutputWithDecoys::new(rng, decoy_source, RING_LEN, chain_tip as usize, output)
        .await
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::NetworkError,
                json!({ "operation": "selectDecoys", "outputId": output_id }),
            )
            .detail(format!("{e:?}"))
        })
}

/// Rings for a set of outputs, reusing ones already fetched.
///
/// Each ring costs a dozen or more network round trips.
async fn rings_for<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    output_ids: &[i64],
    chain_tip: u64,
    cache: &mut HashMap<i64, OutputWithDecoys>,
) -> Result<Vec<OutputWithDecoys>> {
    let mut inputs = Vec::with_capacity(output_ids.len());
    for id in output_ids {
        if !cache.contains_key(id) {
            let ring = ring_for(db, decoy_source, rng, *id, chain_tip).await?;
            cache.insert(*id, ring);
        }
        inputs.push(cache[id].clone());
    }
    Ok(inputs)
}

/// Assembles a `SignableTransaction`. Change always goes to the account's primary address.
fn assemble<R: RngCore + CryptoRng>(
    identity: &crate::wallet::Identity,
    rng: &mut R,
    rct_type: monero_wallet::ringct::RctType,
    inputs: Vec<OutputWithDecoys>,
    recipients: Vec<(MoneroAddress, u64)>,
    fee_rate: monero_wallet::interface::FeeRate,
) -> std::result::Result<SignableTransaction, monero_wallet::send::SendError> {
    let change = Change::new(identity.view_pair.clone(), None);

    // `outgoing_view_key` seeds this transaction's deterministic RNG. Never reuse
    // it across transactions (upstream: reuse repeats ephemeral secrets).
    let mut ovk = Zeroizing::new([0u8; 32]);
    rng.fill_bytes(ovk.as_mut());

    SignableTransaction::new(
        rct_type,
        ovk,
        inputs,
        recipients,
        change,
        Vec::new(),
        fee_rate,
    )
}

fn build_error(e: monero_wallet::send::SendError) -> RuntimeError {
    RuntimeError::with(ErrorCode::InvalidRequest, json!({ "field": "transaction" }))
        .detail(format!("{e:?}"))
}

/// Selects inputs and constructs a transaction; no private spend key needed.
///
/// `Prepared` carries the actual fee, which the host shows on the confirm
/// screen, so the displayed fee is the one charged.
///
/// ## Why iterate
///
/// The fee depends on weight, weight on input count, and input count on the
/// fee. The first round covers the amount only; upstream reports the real fee
/// in `NotEnoughFunds`, and selection repeats with the new target. Converges in two rounds in practice.
#[allow(clippy::too_many_arguments)]
pub async fn prepare<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    identity_id: i64,
    payments: &[Payment],
    fee_rate: monero_wallet::interface::FeeRate,
    chain_tip: u64,
    rct_type: monero_wallet::ringct::RctType,
) -> Result<Prepared> {
    use monero_wallet::send::SendError;

    let identity = db.identity_by_id(identity_id)?;
    let out_total = sum_payments(payments)?;
    let recipients: Vec<(MoneroAddress, u64)> = payments
        .iter()
        .map(|p| (p.address, p.amount_atomic))
        .collect();

    let mut cache: HashMap<i64, OutputWithDecoys> = HashMap::new();
    let mut needed = out_total;

    // Three rounds: without fee, with the real fee, and once more in case the
    // added inputs raised the fee. Not converging means insufficient funds.
    for _ in 0..3 {
        let selection = select_inputs(db, identity_id, chain_tip, needed)?;
        let inputs = rings_for(
            db,
            decoy_source,
            rng,
            &selection.output_ids,
            chain_tip,
            &mut cache,
        )
        .await?;

        match assemble(
            &identity,
            rng,
            rct_type,
            inputs,
            recipients.clone(),
            fee_rate,
        ) {
            Ok(signable) => {
                let fee_atomic = signable.necessary_fee();
                return Ok(Prepared {
                    signable,
                    output_ids: selection.output_ids,
                    inputs_atomic: selection.total_atomic,
                    amount_atomic: out_total,
                    fee_atomic,
                });
            }
            Err(SendError::NotEnoughFunds {
                outputs,
                necessary_fee: Some(fee),
                ..
            }) => {
                // Retarget using the fee computed by upstream.
                let next = outputs.checked_add(fee).ok_or_else(overflow)?;
                if next <= needed {
                    // Target did not grow; another round would fail the same way.
                    return Err(insufficient(selection.total_atomic, next));
                }
                needed = next;
            }
            Err(e) => return Err(build_error(e)),
        }
    }

    Err(insufficient(0, needed))
}

/// Sends all currently spendable outputs to one address.
///
/// Omitting the change output would, per upstream, add the remainder to the
/// fee. Instead the real fee is obtained first and the amount derived.
///
/// Amounts do not affect weight (RingCT amounts are fixed-size), so a probe
/// with 1 piconero gives the fee, then `total - fee` is built with the same weight.
#[allow(clippy::too_many_arguments)]
pub async fn prepare_max<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    identity_id: i64,
    recipient: MoneroAddress,
    fee_rate: monero_wallet::interface::FeeRate,
    chain_tip: u64,
    rct_type: monero_wallet::ringct::RctType,
) -> Result<Prepared> {
    use monero_wallet::send::SendError;

    let identity = db.identity_by_id(identity_id)?;
    let rows = query::spendable_rows(db, identity_id, chain_tip)?;
    if rows.is_empty() {
        return Err(insufficient(0, 1));
    }

    let mut output_ids = Vec::with_capacity(rows.len());
    let mut inputs_atomic: u64 = 0;
    for row in &rows {
        output_ids.push(row.id);
        inputs_atomic = inputs_atomic
            .checked_add(row.amount_atomic)
            .ok_or_else(overflow)?;
    }

    let mut cache: HashMap<i64, OutputWithDecoys> = HashMap::new();
    let inputs = rings_for(db, decoy_source, rng, &output_ids, chain_tip, &mut cache).await?;

    // Probe for the fee: `necessary_fee()` on success, or from `NotEnoughFunds`.
    let fee_atomic = match assemble(
        &identity,
        rng,
        rct_type,
        inputs.clone(),
        vec![(recipient, 1)],
        fee_rate,
    ) {
        Ok(probe) => probe.necessary_fee(),
        Err(SendError::NotEnoughFunds {
            necessary_fee: Some(fee),
            ..
        }) => fee,
        Err(e) => return Err(build_error(e)),
    };

    let amount_atomic = inputs_atomic
        .checked_sub(fee_atomic)
        .filter(|a| *a > 0)
        .ok_or_else(|| insufficient(inputs_atomic, fee_atomic.saturating_add(1)))?;

    let signable = assemble(
        &identity,
        rng,
        rct_type,
        inputs,
        vec![(recipient, amount_atomic)],
        fee_rate,
    )
    .map_err(build_error)?;

    Ok(Prepared {
        signable,
        output_ids,
        inputs_atomic,
        amount_atomic,
        fee_atomic,
    })
}

fn sum_payments(payments: &[Payment]) -> Result<u64> {
    let mut total: u64 = 0;
    for payment in payments {
        total = total
            .checked_add(payment.amount_atomic)
            .ok_or_else(overflow)?;
    }
    Ok(total)
}

fn overflow() -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({ "field": "amount", "reason": "totalOverflow" }),
    )
}

fn insufficient(available: u64, needed: u64) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({
            "field": "amount",
            "reason": "insufficientUnlockedFunds",
            "availableAtomic": available.to_string(),
            "neededAtomic": needed.to_string(),
        }),
    )
}

/// Signs a constructed transaction. No network or database access.
///
/// `spend_private` is zeroized after use. Returns broadcastable bytes and the txid.
pub fn sign_prepared<R: RngCore + CryptoRng>(
    signable: SignableTransaction,
    rng: &mut R,
    spend_private: &[u8],
) -> Result<(Vec<u8>, [u8; 32])> {
    let spend = parse_spend_key(spend_private)?;
    let signed = signable.sign(rng, &spend).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidRequest, json!({ "field": "signature" }))
            .detail(format!("{e:?}"))
    })?;
    let txid = signed.hash();
    Ok((signed.serialize(), txid))
}

/// Broadcasts. Irreversible.
pub async fn broadcast(daemon: &crate::network::Daemon, raw: &[u8]) -> Result<()> {
    use monero_daemon_rpc::prelude::*;

    let tx = monero_wallet::transaction::Transaction::read(&mut &*raw)
        .map_err(|e| db_err("parseSignedTransaction", e))?;
    daemon.publish_transaction(&tx).await.map_err(|e| match e {
        PublishTransactionError::TransactionRejected(reason) => {
            RuntimeError::new(ErrorCode::BroadcastRejected).detail(reason)
        }
        PublishTransactionError::InterfaceError(e) => RuntimeError::with(
            ErrorCode::NetworkError,
            json!({ "operation": "publishTransaction" }),
        )
        .detail(format!("{e:?}")),
    })
}

/// Validates a recipient address and parses the amount.
pub fn parse_payment(
    network: monero_wallet::address::Network,
    address: &str,
    amount_atomic: &str,
) -> Result<Payment> {
    let address = keys::parse_address(network, address, "recipient.address")?;
    let amount_atomic: u64 = amount_atomic.parse().map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "recipient.amountAtomic", "reason": "notAnUnsignedInteger" }),
        )
    })?;
    if amount_atomic == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "recipient.amountAtomic", "reason": "mustBePositive" }),
        ));
    }
    Ok(Payment {
        address,
        amount_atomic,
    })
}

pub fn selection_to_json(selection: &Selection) -> Value {
    json!({
        "outputIds": selection.output_ids,
        "totalAtomic": selection.total_atomic.to_string(),
    })
}

#[cfg(test)]
mod tests {
    use super::test_support::{plus_group_order, reference_spend_scalar};
    use super::*;
    use crate::wallet::testing::db_with_identity;
    use monero_wallet::address::Network;

    fn add_output(db: &Db, identity_id: i64, amount: i64, height: i64, with_ki: bool) -> i64 {
        let ki: Option<Vec<u8>> = with_ki.then(|| {
            let mut k = vec![0u8; 32];
            k[0] = (amount % 251) as u8;
            k[1] = (height % 251) as u8;
            k
        });
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image)
                 VALUES (?1, x'00', ?2, ?3, 0, ?4, ?5, ?6)",
                params![
                    identity_id,
                    &vec![height as u8; 32],
                    height,
                    amount,
                    height,
                    ki
                ],
            )
            .unwrap();
        db.conn().last_insert_rowid()
    }

    fn add_possible_spend(db: &Db, id: i64, out_height: i64, key_image: u8, spend: u8, at: i64) {
        db.conn()
            .execute(
                "INSERT INTO possible_spends
                   (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                 VALUES (?1, ?2, 0, ?3, ?4, ?5, 1700000000)",
                params![
                    id,
                    vec![out_height as u8; 32],
                    vec![key_image; 32],
                    vec![spend; 32],
                    at
                ],
            )
            .unwrap();
    }

    /// A computed key image found in the output's possible spends confirms that
    /// spend (marked spent, outgoing record added). Use as someone else's decoy does not count.
    #[test]
    fn a_key_image_found_among_possible_spends_confirms_an_earlier_spend() {
        let (mut db, id) = db_with_identity();
        let spent = add_output(&db, id, 700, 10, false);
        let decoyed = add_output(&db, id, 300, 11, false);
        add_possible_spend(&db, id, 10, 0xAA, 0x51, 30); // real: key image matches
        add_possible_spend(&db, id, 10, 0xBB, 0x52, 31); // used as a decoy
        add_possible_spend(&db, id, 11, 0xCC, 0x53, 32); // used as a decoy

        let result = apply_key_images(
            &mut db,
            id,
            &[
                (spent, [0xAA; 32], vec![10; 32], 0),
                (decoyed, [0xDD; 32], vec![11; 32], 0),
            ],
        )
        .unwrap();
        assert_eq!((result.imported, result.spends_found), (2, 1));

        let (height, txid): (Option<i64>, Option<Vec<u8>>) = db
            .conn()
            .query_row(
                "SELECT spent_at_height, spent_in_txid FROM outputs WHERE id = ?1",
                [spent],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((height, txid), (Some(30), Some(vec![0x51; 32])));
        let decoyed_spent: Option<i64> = db
            .conn()
            .query_row(
                "SELECT spent_at_height FROM outputs WHERE id = ?1",
                [decoyed],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(decoyed_spent, None, "only used as a decoy, not spent");

        let (direction, out_height): (String, i64) = db
            .conn()
            .query_row(
                "SELECT direction, height FROM transactions WHERE txid = ?1",
                [vec![0x51u8; 32]],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((direction.as_str(), out_height), ("out", 30));
        let left: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM possible_spends", [], |r| r.get(0))
            .unwrap();
        assert_eq!(left, 0, "key-imaged outputs need no possible spends");
    }

    /// A serialized `WalletOutput` owned by `spend` with the given offset.
    fn owned_output_blob(spend: &[u8; 32], offset: u8, txid: [u8; 32]) -> (Vec<u8>, [u8; 32]) {
        use monero_wallet::ed25519::Commitment;
        let mut offset_bytes = [0u8; 32];
        offset_bytes[0] = offset;
        let offset = Scalar::read(&mut offset_bytes.as_slice()).unwrap();
        let secret: curve25519_dalek::Scalar =
            curve25519_dalek::Scalar::from_bytes_mod_order(*spend) + offset.into();
        let key = Point::from(secret * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
        let generator: curve25519_dalek::EdwardsPoint =
            Point::biased_hash(key.compress().to_bytes()).into();
        let image = Point::from(secret * generator);

        let mut blob = Vec::new();
        blob.extend_from_slice(&txid);
        blob.extend_from_slice(&0u64.to_le_bytes()); // index in transaction
        blob.extend_from_slice(&7u64.to_le_bytes()); // index on chain
        key.compress().write(&mut blob).unwrap();
        offset.write(&mut blob).unwrap();
        Commitment::new(offset, 1_000).write(&mut blob).unwrap();
        blob.extend_from_slice(&[0, 0, 0, 0]); // no timelock, subaddress, payment id, data
        WalletOutput::read(&mut blob.as_slice()).expect("fixture must deserialize");
        (blob, image.compress().to_bytes())
    }

    /// Outputs owned by `spend`; the first has a possible spend with its real key image.
    fn db_with_owned_outputs(spend: &[u8; 32]) -> (Db, i64) {
        let (db, id) = db_with_identity();
        for n in 1..=3u8 {
            let txid = [n; 32];
            let (blob, image) = owned_output_blob(spend, n, txid);
            db.conn()
                .execute(
                    "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                          amount_atomic, height)
                     VALUES (?1, ?2, ?3, ?4, 0, 1000, ?4)",
                    params![id, blob, txid.to_vec(), i64::from(n) * 10],
                )
                .unwrap();
            if n == 1 {
                db.conn()
                    .execute(
                        "INSERT INTO possible_spends
                           (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                         VALUES (?1, ?2, 0, ?3, ?4, 40, 1700000000)",
                        params![id, txid.to_vec(), image.to_vec(), vec![0x51u8; 32]],
                    )
                    .unwrap();
            }
        }
        (db, id)
    }

    type OutputRow = (i64, Option<Vec<u8>>, Option<i64>, Option<Vec<u8>>);

    fn output_rows(db: &Db) -> Vec<OutputRow> {
        let mut stmt = db
            .conn()
            .prepare(
                "SELECT id, key_image, spent_at_height, spent_in_txid FROM outputs ORDER BY id",
            )
            .unwrap();
        stmt.query_map([], |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?, r.get(3)?)))
            .unwrap()
            .collect::<rusqlite::Result<Vec<_>>>()
            .unwrap()
    }

    /// Importing from the BIP-32 key stores exactly what the old spend-key path
    /// stored, for canonical, reducible and non-canonical BIP-32 keys alike.
    #[test]
    fn key_image_import_from_the_bip32_key_matches_the_spend_key_path() {
        let v4: [u8; 32] =
            hex::decode("8b28f5749ec99464d5a9d6527125aa8778ea017dd9e5cffaa567d2f62f2c8cbc")
                .unwrap()
                .try_into()
                .unwrap();
        let mut rng = test_support::SeededRng::new(7);
        let mut keys = vec![v4, [0xff; 32], [1; 32], plus_group_order(&[5; 32])];
        for _ in 0..8 {
            let mut key = [0u8; 32];
            rng.fill_bytes(&mut key);
            keys.push(key);
        }
        for bip32 in keys {
            let spend = reference_spend_scalar(&bip32);

            let (mut old_db, old_id) = db_with_owned_outputs(&spend);
            let old = import_key_images(&mut old_db, old_id, &spend).unwrap();
            let (mut new_db, new_id) = db_with_owned_outputs(&spend);
            let new = import_key_images_with_bip32_key(&mut new_db, new_id, &bip32).unwrap();

            assert_eq!((old.imported, old.spends_found), (3, 1));
            assert_eq!(
                (new.imported, new.spends_found),
                (old.imported, old.spends_found)
            );
            let rows = output_rows(&new_db);
            assert_eq!(rows, output_rows(&old_db));
            assert!(rows.iter().all(|row| row.1.is_some()));
            assert_eq!(rows[0].2, Some(40), "the real possible spend is confirmed");
        }
    }

    /// A different account's key is still refused, not stored as a useless key image.
    #[test]
    fn key_image_import_from_another_bip32_key_is_refused() {
        let (mut db, id) = db_with_owned_outputs(&reference_spend_scalar(&[3; 32]));
        let e = match import_key_images_with_bip32_key(&mut db, id, &[4; 32]) {
            Ok(_) => panic!("another key must not import"),
            Err(e) => e,
        };
        assert_eq!(e.params["reason"], "doesNotMatchOutput");
        assert!(output_rows(&db).iter().all(|row| row.1.is_none()));
        let e = match import_key_images_with_bip32_key(&mut db, id, &[4; 31]) {
            Ok(_) => panic!("short key must be refused"),
            Err(e) => e,
        };
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["field"], "bip32PrivateKey");
    }

    // Invalid transaction bytes must fail before signing, even with a review attached.
    #[test]
    fn signing_rejects_a_blob_that_is_not_a_signable_transaction() {
        let review = r#"{"network":"mainnet","fromAddress":"unused","toAddress":"unused","amountAtomic":"1","feeAtomic":"1","changeAtomic":"0","isMaxSend":false}"#;
        let e = sign_for_host("zz", &[0u8; 32], review).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "transactionDoesNotMatchReview");

        let e = sign_for_host("00010203", &[0u8; 32], review).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "transactionDoesNotMatchReview");
    }

    /// Amount overflow must error, not wrap; wrapping could build a transaction
    /// paying a total of 0.
    #[test]
    fn payment_totals_do_not_wrap_on_overflow() {
        let address = keys::parse_address(
            monero_wallet::address::Network::Mainnet,
            "4AdUndXHHZ6cfufTMvppY6JwXNouMBzSkbLYfpAV5Usx3skxNgYeYTRj5UzqtReoS44qo9mtmXCqY45DJ852K5Jv2684Rge",
            "recipient.address",
        )
        .unwrap();
        let payments = vec![
            Payment {
                address,
                amount_atomic: u64::MAX,
            },
            Payment {
                address,
                amount_atomic: 1,
            },
        ];
        let e = sum_payments(&payments).unwrap_err();
        assert_eq!(e.params["reason"], "totalOverflow");
    }

    #[test]
    fn spend_key_length_is_enforced() {
        let e = parse_spend_key(&[0u8; 31]).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["byteLen"], 31);
    }

    /// Largest first: fees are by weight, so fewer inputs are cheaper.
    #[test]
    fn selection_prefers_large_outputs_and_stops_once_covered() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 100, 10, true);
        add_output(&db, id, 900, 11, true);
        add_output(&db, id, 500, 12, true);

        let s = select_inputs(&db, id, 100, 1_000).unwrap();
        assert_eq!(s.total_atomic, 1_400, "900 + 500 suffice; skip the 100");
        assert_eq!(s.output_ids.len(), 2);
    }

    #[test]
    fn selection_fails_loudly_when_funds_are_short() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 100, 10, true);
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
        assert_eq!(e.params["availableAtomic"], "100");
    }

    /// Outputs without a key image are never spent: their unspent status is unknown.
    #[test]
    fn outputs_without_key_images_are_never_selected() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 5_000, 10, false);
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
    }

    /// Locked outputs are not selected; the node would reject them.
    #[test]
    fn locked_outputs_are_not_selected() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 5_000, 95, true);
        // Tip 100, output at 95: within the 10-block lock
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
        // Unlocked at 105
        assert!(select_inputs(&db, id, 105, 1_000).is_ok());
    }

    /// As wallet2: pending change counts in the total (locked), not unlocked, until the send fails.
    #[test]
    fn pending_change_counts_in_total_until_the_send_fails() {
        let (db, id) = db_with_identity();
        let spent = add_output(&db, id, 500, 10, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row(
                "SELECT key_image FROM outputs WHERE id = ?1",
                [spent],
                |r| r.get(0),
            )
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let txid = [0x55u8; 32];
        record_broadcast(
            &db,
            id,
            &txid,
            &[key_image],
            900,
            SentAmounts {
                outgoing_atomic: Some(200),
                change_atomic: Some(300),
            },
        )
        .unwrap();

        let b = crate::query::balance(&db, id, Some(10_000)).unwrap();
        assert_eq!(b.total_atomic, 300);
        assert_eq!(b.pending_change_atomic, 300);
        assert_eq!(b.unlocked_atomic, 0);

        record_broadcast(
            &db,
            id,
            &txid,
            &[key_image],
            901,
            SentAmounts {
                outgoing_atomic: Some(499),
                change_atomic: Some(1),
            },
        )
        .unwrap();
        let recorded: (String, String) = db.conn().query_row(
            "SELECT outgoing_atomic, change_atomic FROM transactions WHERE identity_id = ?1 AND txid = ?2",
            params![id, txid.as_slice()], |row| Ok((row.get(0)?, row.get(1)?)),
        ).unwrap();
        assert_eq!(recorded, ("200".to_owned(), "300".to_owned()));
        assert_eq!(
            crate::query::balance(&db, id, Some(10_000))
                .unwrap()
                .total_atomic,
            300
        );

        crate::pool::mark_failed(&db, id, &txid).unwrap();
        let b = crate::query::balance(&db, id, Some(10_000)).unwrap();
        assert_eq!(b.pending_change_atomic, 0);
        assert_eq!(b.total_atomic, 500);
    }

    #[test]
    fn a_known_incoming_transaction_cannot_bypass_input_ownership_on_rebroadcast() {
        let (db, id) = db_with_identity();
        let txid = [0x57u8; 32];
        db.conn().execute(
            "INSERT INTO transactions (identity_id, txid, height, direction) VALUES (?1, ?2, 90, 'in')",
            params![id, txid.as_slice()],
        ).unwrap();
        assert!(
            record_broadcast(&db, id, &txid, &[[0x99; 32]], 100, SentAmounts::default()).is_err()
        );
        let direction: String = db
            .conn()
            .query_row(
                "SELECT direction FROM transactions WHERE identity_id = ?1 AND txid = ?2",
                params![id, txid.as_slice()],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(direction, "in");
    }

    /// Broadcast marks inputs spent by the transaction's key images.
    #[test]
    fn a_broadcast_marks_its_inputs_by_key_image() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        let untouched = add_output(&db, id, 400, 11, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row("SELECT key_image FROM outputs WHERE id = ?1", [a], |r| {
                r.get(0)
            })
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let txid = [0x77u8; 32];

        record_broadcast(
            &db,
            id,
            &txid,
            &[key_image],
            900,
            SentAmounts {
                outgoing_atomic: Some(123),
                change_atomic: None,
            },
        )
        .unwrap();

        let spent: Vec<(i64, Option<Vec<u8>>)> = db
            .conn()
            .prepare("SELECT id, spent_in_txid FROM outputs ORDER BY id")
            .unwrap()
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(spent, vec![(a, Some(txid.to_vec())), (untouched, None)]);
        let outgoing: Option<String> = db
            .conn()
            .query_row(
                "SELECT outgoing_atomic FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(outgoing.as_deref(), Some("123"));
    }

    /// Recorded before broadcast: a rejection undoes only a fresh record; a retry is not fresh.
    #[test]
    fn a_rejected_first_broadcast_is_undone_but_a_retry_is_not_fresh() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row("SELECT key_image FROM outputs WHERE id = ?1", [a], |r| {
                r.get(0)
            })
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let txid = [0x66u8; 32];
        let record = |db: &Db| {
            record_broadcast(db, id, &txid, &[key_image], 900, SentAmounts::default()).unwrap()
        };

        assert!(record(&db), "first record is fresh");
        assert!(
            crate::query::spendable_rows(&db, id, 10_000)
                .unwrap()
                .is_empty(),
            "inputs are unselectable before broadcast"
        );
        assert!(!record(&db), "recording the same tx again is not fresh");

        undo_recorded_broadcast(&db, id, &txid).unwrap();
        assert_eq!(
            crate::query::spendable_rows(&db, id, 10_000).unwrap().len(),
            1
        );
        let rows: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM transactions", [], |r| r.get(0))
            .unwrap();
        assert_eq!(rows, 0, "rejected transaction is not kept in history");
    }

    /// A send passed with the wrong account records nothing and leaves the
    /// source account's inputs as they were.
    #[test]
    fn a_broadcast_under_another_account_is_refused_before_sending() {
        let (mut db, source) = db_with_identity();
        let (spend, mut view) = crate::wallet::testing::key_bytes();
        view[0] = 10;
        let other = db.add_identity("acct-2", &spend, &view, 100).unwrap().id;
        let a = add_output(&db, source, 500, 10, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row("SELECT key_image FROM outputs WHERE id = ?1", [a], |r| {
                r.get(0)
            })
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let record = |db: &Db, identity, txid: &[u8; 32]| {
            record_broadcast(
                db,
                identity,
                txid,
                &[key_image],
                900,
                SentAmounts::default(),
            )
        };

        let err = record(&db, other, &[0x66; 32]).unwrap_err();
        assert_eq!(err.params["reason"], "inputNotOwnedByAccount");
        let rows: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM transactions", [], |r| r.get(0))
            .unwrap();
        assert_eq!(rows, 0);
        assert_eq!(
            crate::query::spendable_rows(&db, source, 10_000)
                .unwrap()
                .len(),
            1,
            "the source inputs stay spendable"
        );

        assert!(record(&db, source, &[0x66; 32]).unwrap());
        let err = record(&db, source, &[0x77; 32]).unwrap_err();
        assert_eq!(
            err.params["reason"], "inputAlreadySpent",
            "a second transaction over the same inputs is not recorded"
        );
        assert!(
            !record(&db, source, &[0x66; 32]).unwrap(),
            "a retry is still accepted"
        );
    }

    /// A send failed by the pending check releases its inputs; a new
    /// transaction over the same inputs must be recordable again.
    #[test]
    fn an_input_released_by_a_failed_send_can_be_spent_again() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row("SELECT key_image FROM outputs WHERE id = ?1", [a], |r| {
                r.get(0)
            })
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let failed = [0x66u8; 32];
        let retry = [0x77u8; 32];
        let record = |txid: &[u8; 32]| {
            record_broadcast(&db, id, txid, &[key_image], 900, SentAmounts::default())
        };

        assert!(record(&failed).unwrap());
        crate::pool::mark_failed(&db, id, &failed).unwrap();
        assert_eq!(
            crate::query::spendable_rows(&db, id, 10_000).unwrap().len(),
            1,
            "the failed send released its input"
        );

        assert!(record(&retry).expect("a released input is not already spent"));
        let (spent_at, spent_in): (Option<i64>, Option<Vec<u8>>) = db
            .conn()
            .query_row(
                "SELECT spent_at_height, spent_in_txid FROM outputs WHERE id = ?1",
                [a],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((spent_at, spent_in), (Some(900), Some(retry.to_vec())));
        assert!(crate::query::spendable_rows(&db, id, 10_000)
            .unwrap()
            .is_empty());
        let failed_kept: i64 = db
            .conn()
            .query_row(
                "SELECT failed FROM transactions WHERE txid = ?1",
                [failed.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(failed_kept, 1, "the failed send stays in history");
    }

    #[test]
    fn payments_reject_zero_and_non_numeric_amounts() {
        let addr = crate::wallet::testing::ADDR;
        assert!(parse_payment(Network::Mainnet, addr, "0").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "-1").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "abc").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "1000").is_ok());
    }

    /// Amounts cross the boundary as strings: piconero values exceed JS MAX_SAFE_INTEGER.
    #[test]
    fn large_amounts_survive_the_string_boundary() {
        let addr = crate::wallet::testing::ADDR;
        let big = (u64::MAX / 2).to_string();
        let p = parse_payment(Network::Mainnet, addr, &big).unwrap();
        assert_eq!(p.amount_atomic.to_string(), big);
    }

    #[test]
    fn a_mainnet_address_is_refused_on_testnet() {
        let addr = crate::wallet::testing::ADDR;
        let e = parse_payment(Network::Testnet, addr, "1000").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidAddress);
    }
}

// ── End-to-end send ─────────────────────────────────────────────────────

/// Fee rate cap per priority (piconero / weight).
///
/// Upstream `fee_rate` requires a cap because the rate comes from the node,
/// which may lie; an absurd rate could spend the balance on fees.
///
/// monerod's four tiers are fixed multiples of the base fee, currently 20,000 /
/// 80,000 / 320,000 / 4,000,000 on mainnet. The base fee derives from block
/// reward and median block size: busier blocks lower it, and tail emission
/// fixes the reward. Caps leave 5x headroom over current values. Hitting a cap
/// fails the send at that tier; it never overpays.
pub fn max_fee_per_weight(priority: monero_wallet::interface::FeePriority) -> u64 {
    use monero_wallet::interface::FeePriority;
    match priority {
        FeePriority::Unimportant => 100_000,
        FeePriority::Normal => 400_000,
        FeePriority::Elevated => 1_600_000,
        FeePriority::Priority | FeePriority::Custom { .. } => 20_000_000,
    }
}

/// Fee quantization mask (piconero).
///
/// A monerod constant (`PER_KB_FEE_QUANTIZATION_DECIMALS = 8`, i.e.
/// 10^(12-8)), not a price. Upstream rounds the fee up to a multiple of the
/// node-supplied mask with no cap, so a large mask would multiply the fee;
/// only this value is accepted.
pub const FEE_QUANTIZATION_MASK: u64 = 10_000;

/// Upstream `FeeRate` does not expose the mask; read it from the serialization
/// (per_weight then mask, 8 bytes LE each). The layout is pinned by a test.
fn fee_mask(rate: &monero_wallet::interface::FeeRate) -> Option<u64> {
    let bytes = rate.serialize();
    Some(u64::from_le_bytes(bytes.get(8..16)?.try_into().ok()?))
}

fn check_fee_rate(rate: &monero_wallet::interface::FeeRate) -> Result<()> {
    let mask = fee_mask(rate);
    if mask != Some(FEE_QUANTIZATION_MASK) {
        return Err(RuntimeError::with(
            ErrorCode::NetworkError,
            json!({
                "operation": "feeRate",
                "reason": "unexpectedQuantizationMask",
                "mask": mask.map(|m| m.to_string()),
            }),
        ));
    }
    Ok(())
}

/// Fee priority, as a string so it can be validated across the boundary.
pub fn parse_priority(name: &str) -> Result<monero_wallet::interface::FeePriority> {
    use monero_wallet::interface::FeePriority;
    Ok(match name {
        "unimportant" | "low" => FeePriority::Unimportant,
        "" | "normal" => FeePriority::Normal,
        "elevated" | "high" => FeePriority::Elevated,
        "priority" | "highest" => FeePriority::Priority,
        other => {
            return Err(RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({
                    "field": "priority",
                    "value": other,
                    "allowed": ["unimportant", "normal", "elevated", "priority"],
                }),
            ))
        }
    })
}

/// Constructs a transaction to sign. No private spend key needed; does not
/// broadcast.
///
/// ## `signableHex` contains the private view key
///
/// Upstream writes the full view pair into the change (`ChangeEnum::Standard`)
/// so the sender can see its own change. The keyless variant
/// `Change::fingerprintable` is documented upstream as "You MUST assume this
/// will harm your privacy", so it is not used.
///
/// The output is therefore equivalent to a view key: whoever holds it can see
/// every payment this account received. Any export-to-sign design must treat it so.
///
/// `recipients` are `(address, piconero amount)`. Exactly one is accepted: the
/// review contract covers a single recipient. With `max_send`, the amount is
/// spendable total minus fee; the given amount is ignored.
///
/// The returned `feeAtomic` is the fee actually paid and does not change at
/// signing; `amountAtomic` is what the recipient actually receives under max-send.
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
    use monero_daemon_rpc::prelude::*;

    if recipients.len() != 1 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "recipientsJson", "reason": "exactlyOneRecipient" }),
        ));
    }
    let network = db.network();
    let rate = daemon
        .fee_rate(priority, max_fee_per_weight(priority))
        .await
        .map_err(|e| {
            RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": "feeRate" }))
                .detail(format!("{e:?}"))
        })?;
    check_fee_rate(&rate)?;

    // Current consensus: CLSAG + Bulletproofs+. Set by our releases per hard
    // fork, not by the node; a wrong type is rejected network-wide.
    let rct_type = monero_wallet::ringct::RctType::ClsagBulletproofPlus;
    let mut rng = rand_core::OsRng;
    let decoys = CachedDecoys::new(daemon);

    let prepared = if max_send {
        let address = keys::parse_address(network, recipients[0].0, "recipient.address")?;
        prepare_max(
            db,
            &decoys,
            &mut rng,
            identity_id,
            address,
            rate,
            chain_tip,
            rct_type,
        )
        .await?
    } else {
        let payments = recipients
            .iter()
            .map(|(addr, amount)| parse_payment(network, addr, amount))
            .collect::<Result<Vec<_>>>()?;
        prepare(
            db,
            &decoys,
            &mut rng,
            identity_id,
            &payments,
            rate,
            chain_tip,
            rct_type,
        )
        .await?
    };

    let change_atomic = prepared
        .inputs_atomic
        .saturating_sub(prepared.amount_atomic)
        .saturating_sub(prepared.fee_atomic);

    Ok(json!({
        "signableHex": hex::encode(prepared.signable.serialize()),
        "feeAtomic": prepared.fee_atomic.to_string(),
        "amountAtomic": prepared.amount_atomic.to_string(),
        "inputsAtomic": prepared.inputs_atomic.to_string(),
        "changeAtomic": change_atomic.to_string(),
        "inputCount": prepared.output_ids.len(),
    }))
}

/// Signs a constructed transaction. No network or database access.
pub fn sign_for_host(signable_hex: &str, spend_private: &[u8], review_json: &str) -> Result<Value> {
    sign_for_host_with_rng(
        signable_hex,
        spend_private,
        review_json,
        &mut rand_core::OsRng,
    )
}

/// [`sign_for_host`] from the BIP-32 key the account was derived from. The spend
/// key is reduced here, with the same reduction as address derivation, so it
/// never crosses the wasm boundary.
pub fn sign_for_host_with_bip32_key(
    signable_hex: &str,
    bip32_private_key: &[u8],
    review_json: &str,
) -> Result<Value> {
    sign_with_bip32_key_and_rng(
        signable_hex,
        bip32_private_key,
        review_json,
        &mut rand_core::OsRng,
    )
}

/// [`import_key_images`] from the BIP-32 key; see [`sign_for_host_with_bip32_key`].
pub fn import_key_images_with_bip32_key(
    db: &mut Db,
    identity_id: i64,
    bip32_private_key: &[u8],
) -> Result<KeyImageImport> {
    let spend = keys::spend_scalar_from_bip32_key(bip32_private_key)?;
    import_key_images(db, identity_id, spend.as_slice())
}

pub(crate) fn sign_with_bip32_key_and_rng<R: RngCore + CryptoRng>(
    signable_hex: &str,
    bip32_private_key: &[u8],
    review_json: &str,
    rng: &mut R,
) -> Result<Value> {
    let spend = keys::spend_scalar_from_bip32_key(bip32_private_key)?;
    sign_for_host_with_rng(signable_hex, spend.as_slice(), review_json, rng)
}

pub(crate) fn sign_for_host_with_rng<R: RngCore + CryptoRng>(
    signable_hex: &str,
    spend_private: &[u8],
    review_json: &str,
    rng: &mut R,
) -> Result<Value> {
    let request = crate::review::parse_request(review_json)?;
    let signed = verify_and_sign(signable_hex, &request, spend_private, rng);
    keys::scrub_stack();
    let (raw, txid) = signed?;
    Ok(json!({
        "txid": hex::encode(txid),
        "rawHex": hex::encode(&raw),
        "sizeBytes": raw.len(),
    }))
}

// Separate frame so `scrub_stack` can overwrite what signing left on the stack.
#[inline(never)]
fn verify_and_sign<R: RngCore + CryptoRng>(
    signable_hex: &str,
    request: &crate::review::ReviewRequest,
    spend_private: &[u8],
    rng: &mut R,
) -> Result<(Vec<u8>, [u8; 32])> {
    let signable = crate::review::verify(signable_hex, request, Some(spend_private))?;
    sign_prepared(signable, rng, spend_private)
}

/// Amounts of our own send, known only at broadcast.
#[derive(Debug, Default, Clone, Copy)]
pub struct SentAmounts {
    /// Total paid out (destinations + fee).
    pub outgoing_atomic: Option<u64>,
    /// Change, counted in the total (locked) balance until mined, as wallet2.
    pub change_atomic: Option<u64>,
}

pub fn reviewed_broadcast_amounts(
    signable_hex: &str,
    request: &crate::review::ReviewRequest,
    raw_hex: &str,
    outgoing_atomic: Option<&str>,
    change_atomic: Option<&str>,
) -> Result<SentAmounts> {
    crate::review::verify_signed(signable_hex, request, raw_hex)?;
    let parse = |value: &str, field: &str| -> Result<u64> {
        let parsed = value.parse::<u64>().map_err(|_| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({"field": field, "reason": "invalidAmount"}),
            )
        })?;
        if parsed.to_string() != value {
            return Err(RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({"field": field, "reason": "invalidAmount"}),
            ));
        }
        Ok(parsed)
    };
    let outgoing = parse(&request.amount_atomic, "amountAtomic")?
        .checked_add(parse(&request.fee_atomic, "feeAtomic")?)
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({"field": "outgoingAtomic", "reason": "amountOverflow"}),
            )
        })?;
    let change = parse(&request.change_atomic, "changeAtomic")?;
    for (supplied, expected, field) in [
        (outgoing_atomic, outgoing, "outgoingAtomic"),
        (change_atomic, change, "changeAtomic"),
    ] {
        if let Some(value) = supplied {
            if parse(value, field)? != expected {
                return Err(RuntimeError::with(
                    ErrorCode::InvalidRequest,
                    json!({"field": field, "reason": "transactionDoesNotMatchReview"}),
                ));
            }
        }
    }
    Ok(SentAmounts {
        outgoing_atomic: Some(outgoing),
        change_atomic: Some(change),
    })
}

/// Pre-broadcast bookkeeping in one transaction: mark inputs spent and record a
/// pending send.
///
/// Returns whether the record is fresh. An existing record means the same
/// transaction was sent before (a retry), which the node may have accepted.
///
/// A fresh record must spend only this account's outputs that no other
/// transaction spent; otherwise nothing is recorded and nothing is sent.
fn record_broadcast(
    db: &Db,
    identity_id: i64,
    txid: &[u8; 32],
    key_images: &[[u8; 32]],
    chain_tip: u64,
    amounts: SentAmounts,
) -> Result<bool> {
    let tx_db = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginRecordSend", e))?;
    let existed: bool = tx_db
        .query_row(
            "SELECT EXISTS (SELECT 1 FROM transactions WHERE identity_id = ?1 AND txid = ?2)",
            params![identity_id, txid.as_slice()],
            |r| r.get(0),
        )
        .map_err(|e| db_err("findRecordedSend", e))?;
    check_inputs_owned(&tx_db, identity_id, txid, key_images)?;
    for key_image in key_images {
        tx_db
            .execute(
                "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
                 WHERE identity_id = ?3 AND key_image = ?4 AND spent_at_height IS NULL",
                params![
                    chain_tip as i64,
                    txid.as_slice(),
                    identity_id,
                    key_image.as_slice()
                ],
            )
            .map_err(|e| db_err("markSpentAfterBroadcast", e))?;
    }
    tx_db
        .execute(
            "INSERT INTO transactions
               (identity_id, txid, height, direction, sent_at, outgoing_atomic, change_atomic)
             VALUES (?1, ?2, NULL, 'out', ?3, ?4, ?5)
             ON CONFLICT (identity_id, txid) DO UPDATE
               SET direction = 'out', sent_at = ?3, failed = 0,
                   outgoing_atomic = COALESCE(outgoing_atomic, ?4),
                   change_atomic = COALESCE(change_atomic, ?5)",
            params![
                identity_id,
                txid.as_slice(),
                crate::wallet::now_secs(),
                amounts.outgoing_atomic.map(|a| a.to_string()),
                amounts.change_atomic.map(|a| a.to_string())
            ],
        )
        .map_err(|e| db_err("recordOutgoing", e))?;
    tx_db.commit().map_err(|e| db_err("commitRecordSend", e))?;
    Ok(!existed)
}

/// Every input must be an output of `identity_id` not spent by another transaction.
///
/// A failed send releases its inputs (`spent_at_height` cleared) but keeps
/// `spent_in_txid` for its history; such an input is spendable again.
pub(crate) fn check_inputs_owned(
    db: &rusqlite::Connection,
    identity_id: i64,
    txid: &[u8; 32],
    key_images: &[[u8; 32]],
) -> Result<()> {
    if key_images.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "rawHex", "reason": "noInputs" }),
        ));
    }
    for key_image in key_images {
        let spent_in: Option<Option<Vec<u8>>> = db
            .query_row(
                "SELECT CASE WHEN spent_at_height IS NULL THEN NULL ELSE spent_in_txid END
                 FROM outputs WHERE identity_id = ?1 AND key_image = ?2",
                params![identity_id, key_image.as_slice()],
                |r| r.get(0),
            )
            .optional()
            .map_err(|e| db_err("checkInputOwner", e))?;
        let reason = match spent_in {
            None => "inputNotOwnedByAccount",
            Some(Some(other)) if other.as_slice() != txid.as_slice() => "inputAlreadySpent",
            Some(_) => continue,
        };
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "rawHex", "reason": reason }),
        ));
    }
    Ok(())
}

/// Undoes a fresh pending record after an explicit rejection; the inputs become spendable again.
fn undo_recorded_broadcast(db: &Db, identity_id: i64, txid: &[u8; 32]) -> Result<()> {
    let tx_db = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginUndoSend", e))?;
    tx_db
        .execute(
            "UPDATE outputs SET spent_at_height = NULL, spent_in_txid = NULL
             WHERE identity_id = ?1 AND spent_in_txid = ?2",
            params![identity_id, txid.as_slice()],
        )
        .map_err(|e| db_err("releaseRejectedInputs", e))?;
    tx_db
        .execute(
            "DELETE FROM transactions WHERE identity_id = ?1 AND txid = ?2 AND height IS NULL",
            params![identity_id, txid.as_slice()],
        )
        .map_err(|e| db_err("forgetRejectedSend", e))?;
    tx_db.commit().map_err(|e| db_err("commitUndoSend", e))
}

/// Records, then broadcasts a signed transaction. Irreversible.
///
/// Recording first marks inputs spent and creates a pending record, so an
/// interruption (worker killed, response lost) leaves the send visible and its
/// inputs unselectable. Otherwise the next send could use other inputs and both
/// would confirm, paying twice.
pub async fn broadcast_signed(
    db: &Db,
    daemon: &crate::network::Daemon,
    identity_id: i64,
    raw_hex: &str,
    chain_tip: u64,
    amounts: SentAmounts,
) -> Result<Value> {
    let raw = hex::decode(raw_hex).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "rawHex", "reason": "notHex" }),
        )
    })?;

    let tx = monero_wallet::transaction::Transaction::read(&mut raw.as_slice())
        .map_err(|e| db_err("parseSignedTransaction", e))?;
    let txid = tx.hash();

    // Mark by the transaction's own key images.
    let key_images: Vec<[u8; 32]> = tx
        .prefix()
        .inputs
        .iter()
        .filter_map(|input| match input {
            monero_wallet::transaction::Input::ToKey { key_image, .. } => {
                Some(key_image.to_bytes())
            }
            _ => None,
        })
        .collect();
    // If recording fails, do not send; nothing has happened yet.
    let fresh = record_broadcast(db, identity_id, &txid, &key_images, chain_tip, amounts)?;
    // Same for a record that may not be durable: a restart could make its
    // inputs selectable again while the transaction is in flight.
    crate::storage::durability_barrier().await?;

    // An explicit rejection means it will not confirm: undo the fresh record
    // and release the inputs. Other errors are uncertain: keep the record so the
    // inputs are not reselected; the pending check fails it after the
    // propagation window if the node never got it.
    // "double spend" may be this transaction already mined (lost response,
    // retried signature), so it is uncertain too, as is a rejection of a
    // non-fresh record, which the node may have accepted earlier.
    let unknown = match broadcast(daemon, &raw).await {
        Ok(()) => None,
        Err(e) if e.code == ErrorCode::BroadcastRejected && !is_double_spend(&e) && fresh => {
            undo_recorded_broadcast(db, identity_id, &txid)?;
            return Err(e);
        }
        Err(e) => Some(e),
    };

    Ok(json!({
        "txid": hex::encode(txid),
        "sizeBytes": raw.len(),
        "broadcast": if unknown.is_some() { "unknown" } else { "accepted" },
        "error": unknown.map(|e| e.to_json()),
    }))
}

#[cfg(test)]
mod cached_decoys_tests {
    use super::*;
    use monero_wallet::interface::{
        EvaluateUnlocked, InterfaceError, ProvidesBlockchainMeta, ProvidesDecoys,
        ProvidesUnvalidatedDecoys, TransactionsError,
    };
    use std::sync::atomic::{AtomicUsize, Ordering};

    struct CountingSource(AtomicUsize);

    impl ProvidesBlockchainMeta for CountingSource {
        async fn latest_block_number(&self) -> std::result::Result<usize, InterfaceError> {
            Ok(100)
        }
    }

    impl ProvidesUnvalidatedDecoys for CountingSource {
        async fn ringct_output_distribution(
            &self,
            _range: impl Send + std::ops::RangeBounds<usize>,
        ) -> std::result::Result<Vec<u64>, InterfaceError> {
            self.0.fetch_add(1, Ordering::SeqCst);
            Ok(vec![1, 2, 3])
        }

        async fn unlocked_ringct_outputs(
            &self,
            _indexes: &[u64],
            _evaluate_unlocked: EvaluateUnlocked,
        ) -> std::result::Result<Vec<Option<[Point; 2]>>, TransactionsError> {
            Ok(Vec::new())
        }
    }

    // Every input's decoy selection asks for the same range; the node is asked once.
    #[test]
    fn one_distribution_download_per_prepare() {
        let source = CountingSource(AtomicUsize::new(0));
        let cached = CachedDecoys::new(&source);
        for _ in 0..3 {
            let d = futures_lite::future::block_on(ProvidesDecoys::ringct_output_distribution(
                &cached,
                ..=100usize,
            ))
            .unwrap();
            assert_eq!(d, vec![1, 2, 3]);
        }
        assert_eq!(source.0.load(Ordering::SeqCst), 1);
    }
}

#[cfg(test)]
mod double_spend_tests {
    use super::*;

    #[test]
    fn only_the_protocol_quantization_mask_is_accepted() {
        use monero_wallet::interface::FeeRate;
        let rate = FeeRate::new(80_000, FEE_QUANTIZATION_MASK).unwrap();
        assert_eq!(
            fee_mask(&rate),
            Some(FEE_QUANTIZATION_MASK),
            "serialization layout changed"
        );
        check_fee_rate(&rate).unwrap();
        for mask in [1, 9_999, 10_001, 10_000_000_000] {
            let rate = FeeRate::new(80_000, mask).unwrap();
            assert_eq!(fee_mask(&rate), Some(mask));
            let e = check_fee_rate(&rate).unwrap_err();
            assert_eq!(e.params["reason"], "unexpectedQuantizationMask");
        }
    }

    /// Current mainnet rates (2026-10) fit every tier with at least 5x headroom.

    #[test]
    fn current_mainnet_fee_rates_fit_every_priority() {
        use monero_wallet::interface::FeePriority;
        for (priority, current) in [
            (FeePriority::Unimportant, 20_000),
            (FeePriority::Normal, 80_000),
            (FeePriority::Elevated, 320_000),
            (FeePriority::Priority, 4_000_000),
        ] {
            assert!(current * 5 <= max_fee_per_weight(priority));
        }
    }

    #[test]
    fn a_double_spend_rejection_is_not_treated_as_final() {
        let spent = RuntimeError::new(ErrorCode::BroadcastRejected).detail("double spend");
        let other = RuntimeError::new(ErrorCode::BroadcastRejected).detail("fee too low");
        assert!(is_double_spend(&spent));
        assert!(!is_double_spend(&other));
    }
}

/// Shared test fixtures for comparing the BIP-32-key paths with the old ones.
#[cfg(test)]
pub(crate) mod test_support {
    use monero_wallet::ed25519::Scalar;
    use rand_core::{CryptoRng, RngCore};

    /// The scalar reduction as shipped before signing took the BIP-32 key,
    /// frozen here as the reference for the old host-side path.
    pub fn reference_spend_scalar(bip32: &[u8; 32]) -> [u8; 32] {
        <[u8; 32]>::from(Scalar::from(
            curve25519_dalek::Scalar::from_bytes_mod_order(*bip32),
        ))
    }

    /// `bytes + l` (little-endian), a non-canonical encoding of the same scalar.
    pub fn plus_group_order(bytes: &[u8; 32]) -> [u8; 32] {
        let l = hex::decode("edd3f55c1a631258d69cf7a2def9de1400000000000000000000000000000010")
            .unwrap();
        let mut out = [0u8; 32];
        let mut carry = 0u16;
        for i in 0..32 {
            let sum = u16::from(bytes[i]) + u16::from(l[i]) + carry;
            out[i] = sum as u8;
            carry = sum >> 8;
        }
        assert_eq!(carry, 0, "test key too large to add l");
        out
    }

    /// Deterministic RNG so tests can compare signatures byte for byte.
    /// SplitMix64. Not secure; tests only.
    pub struct SeededRng(u64);

    impl SeededRng {
        pub fn new(seed: u64) -> Self {
            Self(seed)
        }
    }

    impl RngCore for SeededRng {
        fn next_u32(&mut self) -> u32 {
            self.next_u64() as u32
        }

        fn next_u64(&mut self) -> u64 {
            self.0 = self.0.wrapping_add(0x9e37_79b9_7f4a_7c15);
            let mut z = self.0;
            z = (z ^ (z >> 30)).wrapping_mul(0xbf58_476d_1ce4_e5b9);
            z = (z ^ (z >> 27)).wrapping_mul(0x94d0_49bb_1331_11eb);
            z ^ (z >> 31)
        }

        fn fill_bytes(&mut self, dest: &mut [u8]) {
            for chunk in dest.chunks_mut(8) {
                let bytes = self.next_u64().to_le_bytes();
                chunk.copy_from_slice(&bytes[..chunk.len()]);
            }
        }

        fn try_fill_bytes(&mut self, dest: &mut [u8]) -> Result<(), rand_core::Error> {
            self.fill_bytes(dest);
            Ok(())
        }
    }

    impl CryptoRng for SeededRng {}
}
