//! Sending: PCZT construction and broadcast.
//!
//! Signing is not in this crate:
//!
//! ```text
//!   runtime.pcztCreate()          build (needs wallet state: note selection, witness, anchor)
//!   runtime.pcztProve()           prove; pure computation, no private keys
//!   keys.pcztSignWithAccountKeys() sign (needs the spending keys; in onekey-zcash-keys)
//!   runtime.pcztSubmit()          hold inputs, broadcast once, store unless rejected (sync does not rebroadcast)
//! ```
//!
//! Build and broadcast need the wallet database, signing needs the spending keys; they never share a crate.

use rusqlite::OptionalExtension;
use zcash_client_backend::data_api::wallet::{
    create_pczt_from_proposal, extract_and_store_transaction_from_pczt,
    input_selection::GreedyInputSelector, propose_transfer,
};
use zcash_client_backend::data_api::{locking::LockError, OutputLockStore, WalletRead};
use zcash_client_backend::fees::{zip317::SingleOutputChangeStrategy, DustOutputPolicy};
use zcash_client_backend::wallet::{LockOwner, OutputRef, OvkPolicy};
use zcash_primitives::transaction::builder::BundlePadding;
use zcash_primitives::transaction::fees::zip317::FeeRule as Zip317FeeRule;
use zcash_primitives::transaction::TxId;
use zcash_protocol::{
    consensus::BlockHeight, memo::MemoBytes, value::Zatoshis, PoolType, ShieldedPool,
};

use crate::account;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::{tx_state, wallet::Db};
use serde_json::json;

/// `stage` is a stable identifier telling the host which build step failed.
fn build_err(stage: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::TransactionBuildError, json!({ "stage": stage })).detail(e)
}

/// Maps proposal failures to error codes the host can branch on.
///
/// Upstream reports insufficient funds from `Error::InsufficientFunds` (selection) and
/// `Error::Change(ChangeError::InsufficientFunds)` (change plus fee does not fit).
fn propose_err<DE, CT, SE, FE, CHE, NR>(
    stage: &str,
    e: zcash_client_backend::data_api::error::Error<DE, CT, SE, FE, CHE, NR>,
) -> RuntimeError
where
    DE: std::fmt::Debug,
    CT: std::fmt::Debug,
    SE: std::fmt::Debug,
    FE: std::fmt::Debug,
    CHE: std::fmt::Debug,
    NR: std::fmt::Debug,
{
    use zcash_client_backend::data_api::error::Error as DataApiError;
    use zcash_client_backend::fees::ChangeError;

    let insufficient = |available: &Zatoshis, required: &Zatoshis| {
        let (have, need) = (u64::from(*available), u64::from(*required));
        RuntimeError::with(
            ErrorCode::InsufficientFunds,
            json!({
                "availableZat": have,
                "requiredZat": need,
                "shortfallZat": need.saturating_sub(have),
                "stage": stage,
            }),
        )
    };

    match &e {
        DataApiError::InsufficientFunds {
            available,
            required,
        } => insufficient(available, required),
        DataApiError::Change(ChangeError::InsufficientFunds {
            available,
            required,
        }) => insufficient(available, required),
        DataApiError::ScanRequired => {
            RuntimeError::with(ErrorCode::NotSynced, json!({ "stage": stage }))
        }
        other => build_err(stage, format!("{other:?}")),
    }
}

/// Parses the host's change pool name. Shared by build and quote so they cannot diverge.
pub fn parse_change_pool(name: &str) -> Result<ShieldedPool> {
    match name {
        // NU6.3 makes Orchard spend-only; new change must enter Ironwood.
        "ironwood" => Ok(ShieldedPool::Ironwood),
        // Sapling is rejected: this runtime ships no Sapling Groth16 parameters,
        // and change there would land in a pool this wallet cannot spend.
        other => Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "fallbackChangePool", "value": other }),
        )),
    }
}

/// Parses the host's spend-source pool; `None` is rejected in `build_proposal`.
///
/// Orchard remains a valid spend source but cannot receive new change. Sapling
/// is unsupported because this runtime does not ship its prover.
pub fn parse_spend_source(name: Option<&str>) -> Result<Option<ShieldedPool>> {
    match name {
        None => Ok(None),
        Some("ironwood") => Ok(Some(ShieldedPool::Ironwood)),
        Some("orchard") => Ok(Some(ShieldedPool::Orchard)),
        Some(other) => Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "spendSource", "value": other }),
        )),
    }
}

/// Host-controlled send policy. Every field is explicit; no hidden defaults.
#[derive(Debug, Clone, Copy)]
pub struct SendPolicy {
    /// Confirmations required for trusted notes (>= 1).
    pub trusted: u32,
    /// Confirmations required for untrusted notes (>= trusted).
    pub untrusted: u32,
    /// Shielded pool for change when the transaction is fully transparent.
    pub fallback_change_pool: ShieldedPool,
    /// Pad the Orchard bundle to hide the real action count (larger transaction, higher fee).
    pub pad_orchard_bundle: bool,
    /// Allow spending zero-conf outputs of our own shielding (ZIP-315 recommends it).
    ///
    /// Must match the balance query's setting.
    pub allow_zero_conf_shielding: bool,
    /// Select notes only from this shielded pool. Required: `None` is rejected at build.
    ///
    /// Excludes Sapling, which upstream `SpendPolicy::default()` would select and then fail to prove.
    /// Pools are never crossed: an uncovered amount is `InsufficientFunds`.
    pub spend_source: Option<ShieldedPool>,
}

impl SendPolicy {
    fn change_pool(&self) -> ShieldedPool {
        self.fallback_change_pool
    }

    fn padding(&self) -> BundlePadding {
        if self.pad_orchard_bundle {
            BundlePadding::DEFAULT
        } else {
            BundlePadding::UNPADDED
        }
    }
}

/// Builds a send policy from the host's raw arguments.
///
/// Quote and build must both use this entry point: fields such as
/// `pad_orchard_bundle` change the ZIP-317 fee.
#[allow(clippy::too_many_arguments)]
pub fn parse_send_policy(
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: &str,
    pad_orchard_bundle: bool,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<&str>,
) -> Result<SendPolicy> {
    Ok(SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: parse_change_pool(fallback_change_pool)?,
        pad_orchard_bundle,
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: parse_spend_source(spend_source)?,
    })
}

/// A transfer proposal, shared by `create_pczt` and `quote` so selection, fee
/// and change strategy are identical and the quoted fee is the real one.
type TransferProposal =
    zcash_client_backend::proposal::Proposal<Zip317FeeRule, zcash_client_sqlite::ReceivedNoteId>;

/// Recipient can only receive into Sapling, which this runtime cannot prove;
/// refuse before building.
fn recipient_needs_sapling_output(
    params: &zcash_protocol::consensus::Network,
    recipient: &zcash_address::ZcashAddress,
) -> bool {
    use zcash_keys::address::Address;
    match Address::try_from_zcash_address(params, recipient.clone()) {
        Ok(Address::Sapling(_)) => true,
        Ok(Address::Unified(ua)) => ua.has_sapling() && !ua.has_orchard(),
        _ => false,
    }
}

/// ZIP-320 TEX addresses accept only transparent inputs; a shielded spend cannot pay them.
fn recipient_is_tex(
    params: &zcash_protocol::consensus::Network,
    recipient: &zcash_address::ZcashAddress,
) -> bool {
    matches!(
        zcash_keys::address::Address::try_from_zcash_address(params, recipient.clone()),
        Ok(zcash_keys::address::Address::Tex(_))
    )
}

// `text` is UTF-8; `hex` is the exact 512-byte ZIP 302 memo field.
pub fn parse_memo(text: Option<&str>, hex: Option<&str>) -> Result<Option<MemoBytes>> {
    let invalid = || RuntimeError::new(ErrorCode::InvalidMemo);
    match (text, hex) {
        (Some(_), Some(_)) => Err(invalid()),
        (_, Some(hex)) => {
            if hex.len() != 1024 {
                return Err(invalid());
            }
            let bytes = hex::decode(hex).map_err(|_| invalid())?;
            Ok(Some(MemoBytes::from_bytes(&bytes).map_err(|_| invalid())?))
        }
        (Some(text), None) => {
            let memo =
                zcash_protocol::memo::Memo::from_bytes(text.as_bytes()).map_err(|_| invalid())?;
            Ok(Some(MemoBytes::from(&memo)))
        }
        (None, None) => Ok(None),
    }
}

fn build_proposal(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
) -> Result<TransferProposal> {
    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;

    // Zero amount: upstream rejects it only for transparent recipients; a shielded
    // recipient would yield a fee-only transaction.
    let amount = Zatoshis::from_u64(amount_zat)
        .ok()
        .filter(|a| !a.is_zero())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::AmountOutOfRange,
                json!({ "valueZat": amount_zat }),
            )
        })?;

    let recipient = zcash_address::ZcashAddress::try_from_encoded(to_address).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidAddress, json!({ "value": to_address })).detail(e)
    })?;

    if recipient_is_tex(&params, &recipient) {
        return Err(RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "reason": "texRequiresTransparentSource" }),
        ));
    }

    if recipient_needs_sapling_output(&params, &recipient) {
        return Err(RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "reason": "saplingOnlyRecipient" }),
        ));
    }

    // Payment::new enforces constraints such as no memo for transparent recipients.
    let payment = zip321::Payment::new(recipient, Some(amount), memo.cloned(), None, None, vec![])
        .map_err(|e| {
            RuntimeError::with(ErrorCode::InvalidMemo, json!({ "reason": "payment" }))
                .detail(format!("{e:?}"))
        })?;

    let request = zip321::TransactionRequest::new(vec![payment])
        .map_err(|e| build_err("transactionRequest", format!("{e:?}")))?;

    let confirmations = account::confirmations_policy(
        policy.trusted,
        policy.untrusted,
        policy.allow_zero_conf_shielding,
    )?;
    let (target, _) = db
        .get_target_and_anchor_heights(confirmations.trusted())
        .map_err(|error| build_err("targetHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    let target = BlockHeight::from(target);
    if onekey_zcash_send_policy::expiry_height(&params, target).is_none() {
        return Err(build_err("expiryHeight", "network upgrade is too close"));
    }

    // Spend only the selected shielded pool, never transparent UTXOs.
    let spend_policy = {
        use zcash_client_backend::data_api::wallet::input_selection::SpendPolicy;
        // Not SpendPolicy::default(): it allows Sapling. See SendPolicy::spend_source.
        let pool = policy.spend_source.ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "spendSource", "reason": "required" }),
            )
        })?;
        SpendPolicy::shielded_pools([pool])
    };

    let selector = GreedyInputSelector::<Db>::new();
    let change = SingleOutputChangeStrategy::<_, Db>::new(
        onekey_zcash_send_policy::fee_rule(&params, target),
        None,
        policy.change_pool(),
        DustOutputPolicy::default(),
    );

    // The commitment-tree error type cannot be inferred here.
    let proposal =
        propose_transfer::<Db, _, _, _, zcash_client_sqlite::wallet::commitment_tree::Error>(
            db,
            &params,
            account_id,
            &selector,
            &change,
            request,
            confirmations,
            &spend_policy,
            None,
            None,
        );

    proposal.map_err(|e| propose_err("proposeTransfer", e))
}

/// Returns the fee only; builds no PCZT and locks no notes.
pub fn quote(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
) -> Result<String> {
    let proposal = build_proposal(db, account_uuid, to_address, amount_zat, memo, policy)?;
    let fee: u64 = proposal
        .steps()
        .iter()
        .map(|s| u64::from(s.balance().fee_required()))
        .sum();

    Ok(json!({
        "feeZat": fee,
        "steps": proposal.steps().len(),
    })
    .to_string())
}

/// Builds an unsigned transfer PCZT for review. Returns `(pcztBytes, feeZat)`.
///
/// Locks nothing, so an abandoned review leaves the notes spendable. Notes are
/// held from `submit_signed_pczt` until the transaction is mined or expires, or
/// the node rejects it.
pub fn create_pczt(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
) -> Result<(Vec<u8>, u64)> {
    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;
    let proposal = build_proposal(db, account_uuid, to_address, amount_zat, memo, policy)?;
    let fee_zat = proposal
        .steps()
        .iter()
        .map(|step| u64::from(step.balance().fee_required()))
        .sum();
    let expiry = onekey_zcash_send_policy::expiry_height(
        &params,
        BlockHeight::from(proposal.min_target_height()),
    )
    .ok_or_else(|| build_err("expiryHeight", "network upgrade is too close"))?;

    // InputsErrT / ChangeErrT appear only in the Err branch and cannot be inferred.
    let pczt = create_pczt_from_proposal::<
        Db,
        _,
        std::convert::Infallible,
        _,
        std::convert::Infallible,
        _,
    >(
        db,
        &params,
        account_id,
        OvkPolicy::Sender,
        &proposal,
        Some(expiry),
        policy.padding(),
    )
    .map_err(|e| build_err("createPczt", format!("{e:?}")))?;
    let pczt = mark_wallet_outputs(db, account_id, pczt)?;
    let bytes = pczt.serialize().map_err(|e| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
            .detail(format!("{e:?}"))
    })?;
    Ok((bytes, fee_zat))
}

/// Adds the account's ZIP 32 path to every Orchard/Ironwood output that pays
/// this account (change), so a hardware signer can verify it as its own instead
/// of rejecting it for having no `user_address`. Upstream sets only the spend
/// paths. View-only accounts (no derivation) are left unchanged.
fn mark_wallet_outputs(
    db: &Db,
    account_id: <Db as WalletRead>::AccountId,
    pczt: pczt::Pczt,
) -> Result<pczt::Pczt> {
    use zcash_client_backend::data_api::Account as _;
    use zcash_protocol::consensus::{NetworkConstants as _, Parameters as _};

    let account = db
        .get_account(account_id)
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "getAccount" }),
            )
            .detail(e)
        })?
        .ok_or_else(|| RuntimeError::new(ErrorCode::AccountNotFound))?;
    let (Some(derivation), Some(fvk)) = (
        account.source().key_derivation(),
        account.ufvk().and_then(|ufvk| ufvk.orchard()),
    ) else {
        return Ok(pczt);
    };
    mark_owned_outputs(
        pczt,
        fvk,
        derivation,
        db.params().network_type().coin_type(),
    )
    .map_err(|e| build_err("markWalletOutputs", format!("{e:?}")))
}

/// Sets `zip32_derivation` on each output whose recipient `fvk` owns (either
/// scope) and whose note decrypts under that scope's IVK. Real recipients
/// (`user_address` set) and padding are left alone. Undecryptable owned outputs
/// are the zero-valued outputs the post-NU6.3 Orchard builder pairs with
/// external-scope spends; like padding they get no path.
fn mark_owned_outputs(
    pczt: pczt::Pczt,
    fvk: &orchard::keys::FullViewingKey,
    derivation: &zcash_client_backend::data_api::Zip32Derivation,
    coin_type: u32,
) -> std::result::Result<pczt::Pczt, pczt::roles::updater::OrchardError> {
    use orchard::keys::PreparedIncomingViewingKey;
    use orchard::note_encryption::{IronwoodDomain, OrchardDomain};
    use orchard::pczt::{Action, Bundle, Updater, UpdaterError, Zip32Derivation};
    use zcash_note_encryption::try_note_decryption;

    type Decrypts = fn(&Action, &PreparedIncomingViewingKey) -> bool;

    let seed_fingerprint = derivation.seed_fingerprint().to_bytes();
    let path = [
        zip32::ChildIndex::hardened(32).index(),
        zip32::ChildIndex::hardened(coin_type).index(),
        zip32::ChildIndex::hardened(u32::from(derivation.account_index())).index(),
    ];
    let owned_actions = |bundle: &Bundle, decrypts: Decrypts| -> Vec<usize> {
        let owned = |action: &Action| {
            let output = action.output();
            if output.user_address().is_some() {
                return false;
            }
            let Some(scope) = output
                .recipient()
                .as_ref()
                .and_then(|recipient| fvk.scope_for_address(recipient))
            else {
                return false;
            };
            decrypts(action, &PreparedIncomingViewingKey::new(&fvk.to_ivk(scope)))
        };
        let actions = bundle.actions().iter().enumerate();
        actions.filter(|(_, a)| owned(a)).map(|(i, _)| i).collect()
    };
    let mark = |mut updater: Updater<'_>, decrypts: Decrypts| {
        for index in owned_actions(updater.bundle(), decrypts) {
            updater.update_action_with(index, |mut action| {
                let derivation = Zip32Derivation::parse(seed_fingerprint, path.to_vec())
                    .expect("hardened ZIP 32 indices");
                action.set_output_zip32_derivation(derivation);
                Ok::<_, UpdaterError>(())
            })?;
        }
        Ok(())
    };
    let orchard_decrypts: Decrypts = |action, ivk| {
        try_note_decryption(&OrchardDomain::for_pczt_action(action), ivk, action).is_some()
    };
    let ironwood_decrypts: Decrypts = |action, ivk| {
        try_note_decryption(&IronwoodDomain::for_pczt_action(action), ivk, action).is_some()
    };
    Ok(pczt::roles::updater::Updater::new(pczt)
        .update_orchard_with(|updater| mark(updater, orchard_decrypts))?
        .update_ironwood_with(|updater| mark(updater, ironwood_decrypts))?
        .finish())
}

/// The proving circuit version is set by the consensus branch, not a product option.
///
/// `height` is the start of the transaction's branch: PostNu6_3 at or after NU6.3
/// activation, otherwise FixedPostNu6_2. `InsecurePreNu6_2` is never used for
/// proving (marked so upstream).
fn circuit_version_for(
    params: &zcash_protocol::consensus::Network,
    height: zcash_protocol::consensus::BlockHeight,
) -> orchard::circuit::OrchardCircuitVersion {
    use zcash_protocol::consensus::{NetworkUpgrade, Parameters};
    match params.activation_height(NetworkUpgrade::Nu6_3) {
        Some(activation) if height >= activation => {
            orchard::circuit::OrchardCircuitVersion::PostNu6_3
        }
        _ => orchard::circuit::OrchardCircuitVersion::FixedPostNu6_2,
    }
}

/// The transaction's own branch decides, not the tip: around an upgrade they disagree and a
/// proof under the wrong circuit is rejected. The tip is only the fallback for an unknown branch id.
fn circuit_version_for_branch(
    params: &zcash_protocol::consensus::Network,
    consensus_branch_id: u32,
    chain_tip: zcash_protocol::consensus::BlockHeight,
) -> orchard::circuit::OrchardCircuitVersion {
    let branch_start = zcash_protocol::consensus::BranchId::try_from(consensus_branch_id)
        .ok()
        .and_then(|branch| branch.height_bounds(params))
        .map(|(start, _)| start);
    circuit_version_for(params, branch_start.unwrap_or(chain_tip))
}

/// Merges the signed copy returned by an external signer (hardware) into the
/// local original PCZT. It may add signatures only, never change the transaction.
///
/// `TransactionExtractor` only checks self-consistency, not what the user
/// approved, and the bare `Combiner` blocks substitution only as a side effect of
/// upstream behaviour. So sighashes must be byte-identical before and after, and
/// `scriptSig` must stay empty (it is outside the sighash). Errors carry `field`.
pub fn combine_pczt(original: &[u8], signed: &[u8]) -> Result<Vec<u8>> {
    use pczt::roles::signer::Signer;
    use pczt::roles::verifier::{TransparentError, Verifier};

    let parse = |bytes: &[u8], which: &str| {
        pczt::Pczt::parse(bytes).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "parse", "which": which }),
            )
            .detail(format!("{error:?}"))
        })
    };
    let signer = |pczt: pczt::Pczt, which: &str| {
        Signer::new(pczt).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "signer", "which": which }),
            )
            .detail(format!("{error:?}"))
        })
    };
    let mismatch = |field: &str| {
        RuntimeError::with(
            ErrorCode::PcztError,
            json!({
                "stage": "combine",
                "reason": "signedPcztChangedTheTransaction",
                "field": field,
            }),
        )
    };

    let original = parse(original, "original")?;
    let signed = parse(signed, "signed")?;
    let transparent_inputs = original.transparent().inputs().len();
    let approved = signer(original.clone(), "original")?;

    let combined = pczt::roles::combiner::Combiner::new(vec![original, signed])
        .combine()
        .map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "combine" }))
                .detail(format!("{error:?}"))
        })?;
    let result = signer(combined.clone(), "combined")?;

    if result.shielded_sighash() != approved.shielded_sighash() {
        return Err(mismatch("shieldedSighash"));
    }
    if combined.transparent().inputs().len() != transparent_inputs {
        return Err(mismatch("transparentInputCount"));
    }
    for index in 0..transparent_inputs {
        let approved_sighash = approved.transparent_sighash(index).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "transparentSighash", "which": "original" }),
            )
            .detail(format!("{error:?}"))
        })?;
        let combined_sighash = result.transparent_sighash(index).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "transparentSighash", "which": "combined" }),
            )
            .detail(format!("{error:?}"))
        })?;
        if approved_sighash != combined_sighash {
            return Err(mismatch("transparentSighash"));
        }
    }
    // scriptSig is built by SpendFinalizer at extraction, never by a signer.
    // Check a clone: Verifier reparses and reserializes the bundle.
    if transparent_inputs > 0 {
        Verifier::new(combined.clone())
            .with_transparent(|bundle| {
                if bundle
                    .inputs()
                    .iter()
                    .any(|input| input.script_sig().is_some())
                {
                    Err(TransparentError::Custom(()))
                } else {
                    Ok(())
                }
            })
            .map_err(|error| match error {
                TransparentError::Custom(()) => mismatch("transparentScriptSig"),
                other => RuntimeError::with(
                    ErrorCode::PcztError,
                    json!({ "stage": "verifyTransparent" }),
                )
                .detail(format!("{other:?}")),
            })?;
    }

    combined.serialize().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
            .detail(format!("{error:?}"))
    })
}

pub fn prove_pczt(db: &Db, pczt_bytes: &[u8]) -> Result<Vec<u8>> {
    let params = *db.params();
    let chain_tip = db
        .chain_height()
        .map_err(|error| build_err("chainHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    prove_pczt_at_height(&params, chain_tip, pczt_bytes)
}

/// Builds only public parameters, without a wallet, proposal, or spending key.
pub fn prewarm_pczt(
    params: &zcash_protocol::consensus::Network,
    target_height: zcash_protocol::consensus::BlockHeight,
) -> Result<()> {
    let version = circuit_version_for(params, target_height);
    crate::circuit_keys::proving_key(version)?;
    crate::circuit_keys::verifying_key(version);
    Ok(())
}

/// Retains upstream proof/signature validation while reusing public parameters.
pub fn extract_pczt(pczt: pczt::Pczt) -> Result<zcash_primitives::transaction::Transaction> {
    use orchard::circuit::OrchardCircuitVersion;
    use zcash_protocol::consensus::{BranchId, OrchardProtocolRevision};

    let version = BranchId::try_from(*pczt.global().consensus_branch_id())
        .ok()
        .and_then(|branch| branch.orchard_protocol_revision())
        .map(|revision| match revision {
            OrchardProtocolRevision::InsecureV1 => OrchardCircuitVersion::InsecurePreNu6_2,
            OrchardProtocolRevision::V2 => OrchardCircuitVersion::FixedPostNu6_2,
            OrchardProtocolRevision::V3 => OrchardCircuitVersion::PostNu6_3,
        });
    let has_shielded_bundle =
        !pczt.orchard().actions().is_empty() || !pczt.ironwood().actions().is_empty();
    let extractor = pczt::roles::tx_extractor::TransactionExtractor::new(pczt);
    let extractor = match version.filter(|_| has_shielded_bundle) {
        Some(version) => extractor.with_orchard(crate::circuit_keys::verifying_key(version)),
        // Let upstream report unsupported branches and malformed bundles.
        None => extractor,
    };
    extractor.extract().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "extract" }))
            .detail(format!("{error:?}"))
    })
}

/// Pure proving: no wallet, database, scan, or storage initialization.
pub fn prove_pczt_at_height(
    params: &zcash_protocol::consensus::Network,
    chain_tip: zcash_protocol::consensus::BlockHeight,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    let pczt = pczt::Pczt::parse(pczt_bytes).map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{error:?}"))
    })?;
    let version =
        circuit_version_for_branch(params, *pczt.global().consensus_branch_id(), chain_tip);
    let prover = pczt::roles::prover::Prover::new(pczt);

    let prover = if prover.requires_ironwood_proof() {
        let proving_key = crate::circuit_keys::proving_key(version)?;
        prover.create_ironwood_proof(proving_key).map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "ironwoodProof" }))
                .detail(format!("{error:?}"))
        })?
    } else {
        prover
    };

    if prover.requires_sapling_proofs() {
        return Err(RuntimeError::with(
            ErrorCode::Unimplemented,
            json!({ "what": "saplingProof" }),
        ));
    }

    let prover = if prover.requires_orchard_proof() {
        let proving_key = crate::circuit_keys::proving_key(version)?;
        prover.create_orchard_proof(proving_key).map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "orchardProof" }))
                .detail(format!("{error:?}"))
        })?
    } else {
        prover
    };

    prover.finish().serialize().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serializeProven" }))
            .detail(format!("{error:?}"))
    })
}

/// Spend tables with their spent-output columns, and the spent outputs' tables,
/// index columns and pools.
const SPEND_TABLES: [(&str, &str, &str, &str, PoolType); 4] = [
    (
        "sapling_received_note_spends",
        "sapling_received_note_id",
        "sapling_received_notes",
        "output_index",
        PoolType::SAPLING,
    ),
    (
        "orchard_received_note_spends",
        "orchard_received_note_id",
        "orchard_received_notes",
        "action_index",
        PoolType::ORCHARD,
    ),
    (
        "ironwood_received_note_spends",
        "ironwood_received_note_id",
        "ironwood_received_notes",
        "action_index",
        PoolType::IRONWOOD,
    ),
    (
        "transparent_received_output_spends",
        "transparent_received_output_id",
        "transparent_received_outputs",
        "output_index",
        PoolType::TRANSPARENT,
    ),
];

fn refuse_store(reason: &str) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::TransactionBuildError,
        json!({ "stage": "storeSignedPczt", "reason": reason }),
    )
}

/// Refuses a just-stored transaction that does not spend from `account_uuid`,
/// or that spends an output another stored transaction still holds (mined, or
/// unmined and unexpired). Reviews lock nothing, so two can select the same notes.
fn assert_sole_spend(
    ext: &zcash_client_sqlite::ExtensionTransaction<'_>,
    txid: &str,
    account_uuid: &str,
) -> Result<()> {
    let internal = crate::history::display_order_to_internal(txid)?;
    let spends_from_account: bool = ext
        .query_row(
            "SELECT EXISTS(
               SELECT 1 FROM v_transactions
               WHERE txid = ?1 AND account_uuid = ?2 AND COALESCE(total_spent, 0) > 0
             )",
            rusqlite::params![internal, crate::history::uuid_blob(account_uuid)?],
            |row| row.get(0),
        )
        .map_err(|e| build_err("readStoredSpend", e))?;
    if !spends_from_account {
        return Err(refuse_store("accountMismatch"));
    }
    for (table, output, ..) in SPEND_TABLES {
        let conflict: bool = ext
            .query_row(
                &format!(
                    "SELECT EXISTS(
                       SELECT 1
                       FROM transactions own
                       JOIN {table} own_spend ON own_spend.transaction_id = own.id_tx
                       JOIN {table} other_spend
                         ON other_spend.{output} = own_spend.{output}
                        AND other_spend.transaction_id <> own.id_tx
                       JOIN transactions other ON other.id_tx = other_spend.transaction_id
                       WHERE own.txid = ?1
                         AND (other.mined_height IS NOT NULL
                              OR COALESCE(other.expiry_height, 0) = 0
                              OR other.expiry_height
                                 > COALESCE((SELECT MAX(height) FROM blocks), 0))
                     )"
                ),
                [&internal],
                |row| row.get(0),
            )
            .map_err(|e| build_err("readConflictingSpends", e))?;
        if conflict {
            return Err(refuse_store("inputsAlreadySpent"));
        }
    }
    Ok(())
}

/// Extracts the signed PCZT and stores it in the wallet; from then on the
/// transaction holds its spent notes until it is mined or expires.
fn extract_and_store(
    wallet: &mut zcash_client_sqlite::WalletDb<
        zcash_client_sqlite::SqlTransaction<'_>,
        &zcash_protocol::consensus::Network,
        &crate::clock::HostClock,
        &mut rand_chacha::ChaCha20Rng,
    >,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
) -> Result<TxId> {
    extract_and_store_transaction_from_pczt::<_, zcash_client_sqlite::ReceivedNoteId>(
        wallet,
        pczt,
        None,
        Some(orchard_vk),
    )
    .map_err(|e| build_err("extractAndStore", format!("{e:?}")))
}

/// The wallet outputs a stored transaction spends.
fn spent_outputs(
    ext: &zcash_client_sqlite::ExtensionTransaction<'_>,
    txid: &TxId,
) -> Result<Vec<OutputRef>> {
    let mut outputs = Vec::new();
    for (spends, spent, table, index, pool) in SPEND_TABLES {
        let mut after = -1_i64;
        while let Some((id, source, output_index)) = ext
            .query_row(
                &format!(
                    "SELECT output.id, source.txid, output.{index}
                     FROM {spends} spend
                     JOIN {table} output ON output.id = spend.{spent}
                     JOIN transactions source ON source.id_tx = output.transaction_id
                     JOIN transactions own ON own.id_tx = spend.transaction_id
                     WHERE own.txid = ?1 AND output.id > ?2
                     ORDER BY output.id LIMIT 1"
                ),
                rusqlite::params![txid.as_ref(), after],
                |row| Ok((row.get(0)?, row.get::<_, [u8; 32]>(1)?, row.get(2)?)),
            )
            .optional()
            .map_err(|e| build_err("readSpentOutputs", e))?
        {
            outputs.push(OutputRef::new(TxId::from_bytes(source), pool, output_index));
            after = id;
        }
    }
    Ok(outputs)
}

/// A signed transaction that passed the store checks but is not stored.
struct Staged {
    txid: TxId,
    raw: Vec<u8>,
    inputs: Vec<OutputRef>,
    /// The transaction's expiry height, or the maximum if it never expires.
    lock_expiry: BlockHeight,
}

/// Stores the signed PCZT with its checks in a transaction that is then rolled
/// back, to learn what storing it would hold. Leaves the wallet unchanged.
fn stage_signed_pczt(
    db: &mut Db,
    account_uuid: &str,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
) -> Result<Staged> {
    // Only an error from the closure rolls the transaction back.
    enum Rollback {
        Staged(Staged),
        Failed(RuntimeError),
    }
    impl From<rusqlite::Error> for Rollback {
        fn from(error: rusqlite::Error) -> Self {
            Rollback::Failed(error.into())
        }
    }
    let rehearsal = db.transactionally_with_extension(
        |wallet, ext| -> std::result::Result<std::convert::Infallible, Rollback> {
            let staged = extract_and_store(wallet, pczt, orchard_vk).and_then(|txid| {
                assert_sole_spend(ext, &txid.to_string(), account_uuid)?;
                let (raw, expiry) = ext
                    .query_row(
                        "SELECT raw, expiry_height FROM transactions WHERE txid = ?1",
                        [txid.as_ref()],
                        |row| Ok((row.get(0)?, row.get::<_, Option<u32>>(1)?)),
                    )
                    .map_err(|e| build_err("readStaged", e))?;
                Ok(Staged {
                    txid,
                    raw,
                    inputs: spent_outputs(ext, &txid)?,
                    lock_expiry: BlockHeight::from_u32(
                        expiry.filter(|height| *height != 0).unwrap_or(u32::MAX),
                    ),
                })
            });
            Err(match staged {
                Ok(staged) => Rollback::Staged(staged),
                Err(error) => Rollback::Failed(error),
            })
        },
    );
    match rehearsal {
        Ok(never) => match never {},
        Err(Rollback::Staged(staged)) => Ok(staged),
        Err(Rollback::Failed(error)) => Err(error),
    }
}

/// Locks the inputs until the transaction's expiry height, as long as a stored
/// spend would hold them, and records the submission, before any broadcast.
fn hold_inputs(db: &mut Db, account_uuid: &str, staged: &Staged, pczt_bytes: &[u8]) -> Result<()> {
    db.transactionally_with_extension(|wallet, ext| {
        wallet
            .lock_outputs(
                &staged.inputs,
                LockOwner::new(*staged.txid.as_ref()),
                staged.lock_expiry,
            )
            .map_err(|error| match error {
                LockError::LockFailure(_) => refuse_store("inputsAlreadySpent"),
                error => build_err("lockInputs", error),
            })?;
        tx_state::put_submission(ext, &staged.txid.to_string(), account_uuid, pczt_bytes)
    })
}

/// Stores a held submission; its spends take over from the input locks.
fn store_submission(
    db: &mut Db,
    txid: &str,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
) -> Result<()> {
    db.transactionally_with_extension(|wallet, ext| {
        extract_and_store(wallet, pczt, orchard_vk)?;
        tx_state::delete_submission(ext, txid)
    })
}

/// Releases a rejected submission's inputs. A copy an earlier submit stored still
/// spends them until expiry, so its rejection is recorded instead.
fn release_rejected(
    db: &mut Db,
    account_uuid: &str,
    staged: &Staged,
    error: &RuntimeError,
) -> Result<()> {
    let txid = staged.txid.to_string();
    let owner = LockOwner::new(*staged.txid.as_ref());
    db.transactionally_with_extension(|wallet, ext| {
        for input in &staged.inputs {
            wallet
                .unlock_output(input, owner)
                .map_err(|e| build_err("releaseInputs", e))?;
        }
        tx_state::delete_submission(ext, &txid)?;
        if tx_state::is_stored(ext, &txid)? {
            tx_state::insert_rejection(ext, &txid, account_uuid, error)?;
        }
        Ok(())
    })
}

/// Outcome of a submit.
pub enum SubmitOutcome {
    Accepted {
        txid: String,
    },
    /// The trusted node rejected it. It is not stored and its notes are spendable
    /// again, unless an earlier submit stored it: then they return at its expiry.
    Rejected {
        txid: String,
        error: RuntimeError,
    },
    /// Unknown whether the node received it: stored, holds its notes, and stays pending
    /// until mined or expired; the host does not resubmit it.
    Unknown {
        txid: String,
        error: RuntimeError,
    },
}

// Only a definitive rejection releases the notes; a spent-input reply can refer to
// this transaction itself.
fn first_submit_rejection(error: RuntimeError) -> std::result::Result<RuntimeError, RuntimeError> {
    if error.code == ErrorCode::BroadcastRejected {
        Ok(error)
    } else {
        Err(error)
    }
}

/// Stores the broadcast transaction, or releases its inputs if the node
/// definitively rejected it. Storing after a broadcast cannot fail as a plain error.
fn finish_submission(
    db: &mut Db,
    account_uuid: &str,
    staged: Staged,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
    result: Result<()>,
) -> Result<SubmitOutcome> {
    let txid = staged.txid.to_string();
    let result = match result.map_err(first_submit_rejection) {
        Err(Ok(error)) => {
            return Ok(match release_rejected(db, account_uuid, &staged, &error) {
                Ok(()) => SubmitOutcome::Rejected { txid, error },
                // Still recorded: it is kept as a pending send.
                Err(error) => SubmitOutcome::Unknown { txid, error },
            });
        }
        Err(Err(error)) => Err(error),
        Ok(()) => Ok(()),
    };
    if let Err(error) = store_submission(db, &txid, pczt, orchard_vk) {
        let broadcast = if result.is_ok() {
            "accepted"
        } else {
            "unknown"
        };
        return Err(RuntimeError::with(
            ErrorCode::FinalizeOutcomeUnknown,
            json!({ "txid": txid, "broadcast": broadcast }),
        )
        .detail(error));
    }
    Ok(match result {
        Ok(()) => SubmitOutcome::Accepted { txid },
        Err(error) => SubmitOutcome::Unknown { txid, error },
    })
}

fn parse_pczt(bytes: &[u8]) -> Result<pczt::Pczt> {
    pczt::Pczt::parse(bytes).map_err(|e| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{e:?}"))
    })
}

/// The Orchard verifying key for the transaction's own branch.
fn verifying_key_for(
    db: &Db,
    pczt: &pczt::Pczt,
) -> Result<&'static orchard::circuit::VerifyingKey> {
    let chain_tip = db
        .chain_height()
        .map_err(|error| build_err("chainHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    Ok(crate::circuit_keys::verifying_key(
        circuit_version_for_branch(db.params(), *pczt.global().consensus_branch_id(), chain_tip),
    ))
}

/// Stores submissions whose outcome was never stored: the process stopped after
/// holding their inputs, or storing failed after the broadcast. The node may have
/// them, so each is kept as a pending send, like an unknown outcome.
pub fn settle_interrupted_submissions(db: &mut Db) -> Result<()> {
    while let Some((txid, pczt_bytes)) =
        db.transactionally_with_extension(|_wallet, ext| tx_state::first_submission(ext))?
    {
        let pczt = parse_pczt(&pczt_bytes)?;
        let orchard_vk = verifying_key_for(db, &pczt)?;
        store_submission(db, &txid, pczt, orchard_vk)?;
    }
    Ok(())
}

/// Holds the inputs and records the submission before broadcasting, then stores
/// the transaction unless the node definitively rejected it.
///
/// Storing is what makes the upstream wallet hold the notes until expiry, so a
/// rejection must come first to release them at once. The record made before the
/// broadcast keeps a lost outcome from looking like an unsent payment.
pub async fn submit_signed_pczt(
    db: &mut Db,
    client: &mut crate::network::LightClient,
    account_uuid: &str,
    pczt_bytes: &[u8],
) -> Result<SubmitOutcome> {
    submit_with(db, account_uuid, pczt_bytes, |raw| {
        crate::network::broadcast(client, raw)
    })
    .await
}

pub(crate) async fn submit_with<F, Fut>(
    db: &mut Db,
    account_uuid: &str,
    pczt_bytes: &[u8],
    broadcast: F,
) -> Result<SubmitOutcome>
where
    F: FnOnce(Vec<u8>) -> Fut,
    Fut: std::future::Future<Output = Result<()>>,
{
    let pczt = parse_pczt(pczt_bytes)?;
    settle_interrupted_submissions(db)?;
    let orchard_vk = verifying_key_for(db, &pczt)?;
    let mut staged = stage_signed_pczt(db, account_uuid, pczt.clone(), orchard_vk)?;
    // A stored copy the node already rejected is not sent again.
    if let Err(error) = tx_state::assert_not_rejected(db, &staged.txid.to_string()) {
        if error.code == ErrorCode::BroadcastRejected {
            let txid = staged.txid.to_string();
            return Ok(SubmitOutcome::Rejected { txid, error });
        }
        return Err(error);
    }
    hold_inputs(db, account_uuid, &staged, pczt_bytes)?;
    // Each extraction draws a new binding signature: the stored bytes can differ
    // from the broadcast ones, never the txid.
    let result = broadcast(std::mem::take(&mut staged.raw)).await;
    finish_submission(db, account_uuid, staged, pczt, orchard_vk, result)
}

/// Reads a stored transaction's raw bytes from the wallet for broadcast.
pub fn raw_transaction(db: &Db, txid: &str) -> Result<Vec<u8>> {
    let bytes = hex::decode(txid)
        .ok()
        .filter(|b| b.len() == 32)
        .ok_or_else(|| {
            RuntimeError::with(ErrorCode::TransactionBuildError, json!({ "stage": "txid" }))
        })?;
    // The txid string is in display order (big-endian); the internal form is little-endian.
    let mut internal = [0u8; 32];
    for (i, b) in bytes.iter().rev().enumerate() {
        internal[i] = *b;
    }

    let tx = db
        .get_transaction(TxId::from_bytes(internal))
        .map_err(|e| build_err("getTransaction", e))?
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "getTransaction", "txid": txid }),
            )
        })?;

    let mut raw = Vec::new();
    tx.write(&mut raw)
        .map_err(|e| build_err("serializeTransaction", e))?;
    Ok(raw)
}

/// Proof types supported by this wallet runtime.
pub const PROVE_ORCHARD: bool = true;
pub const PROVE_SAPLING: bool = false;
pub const PROVE_IRONWOOD: bool = true;

#[cfg(all(test, not(target_arch = "wasm32")))]
mod submit_state_tests {
    use super::*;
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use rusqlite::Connection;
    use zcash_client_sqlite::WalletDb;
    use zcash_protocol::consensus::Network;

    fn wallet(conn: Connection) -> Db {
        WalletDb::from_connection(
            conn,
            Network::TestNetwork,
            crate::clock::HostClock,
            ChaCha20Rng::from_seed([0; 32]),
        )
    }

    /// Transaction 1 is the one just stored; it spends orchard note 7.
    fn spend_fixture(other: Option<(Option<u32>, Option<u32>)>) -> Db {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE transactions (
               id_tx INTEGER PRIMARY KEY, txid BLOB, mined_height INTEGER, expiry_height INTEGER
             );
             CREATE TABLE blocks (height INTEGER);
             INSERT INTO blocks VALUES (1000);
             CREATE TABLE v_transactions (txid BLOB, account_uuid BLOB, total_spent INTEGER);",
        )
        .unwrap();
        for (table, output, ..) in SPEND_TABLES {
            conn.execute_batch(&format!(
                "CREATE TABLE {table} ({output} INTEGER, transaction_id INTEGER);"
            ))
            .unwrap();
        }
        conn.execute(
            "INSERT INTO transactions VALUES (1, ?1, NULL, 1040)",
            [[1_u8; 32]],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO v_transactions VALUES (?1, ?2, 10)",
            rusqlite::params![[1_u8; 32], uuid::Uuid::from_u128(1).as_bytes().as_slice()],
        )
        .unwrap();
        conn.execute("INSERT INTO orchard_received_note_spends VALUES (7, 1)", [])
            .unwrap();
        if let Some((mined_height, expiry_height)) = other {
            conn.execute(
                "INSERT INTO transactions VALUES (2, ?1, ?2, ?3)",
                rusqlite::params![[2_u8; 32], mined_height, expiry_height],
            )
            .unwrap();
            conn.execute("INSERT INTO orchard_received_note_spends VALUES (7, 2)", [])
                .unwrap();
        }
        wallet(conn)
    }

    fn check(db: &mut Db, account: u128) -> Result<()> {
        let txid = crate::history::hex_display_order(&[1_u8; 32]);
        let account = uuid::Uuid::from_u128(account).to_string();
        db.transactionally_with_extension(|_, ext| assert_sole_spend(ext, &txid, &account))
    }

    fn lock_fixture(fail_delete: bool) -> (Db, Staged, String) {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB);
             CREATE TABLE scan_queue (block_range_end INTEGER);
             INSERT INTO scan_queue VALUES (1001);
             CREATE TABLE orchard_received_notes (
               transaction_id INTEGER, action_index INTEGER,
               lock_owner BLOB, lock_expiry_height INTEGER
             );
             INSERT INTO orchard_received_notes VALUES (1, 0, NULL, NULL);
             CREATE TABLE ext_onekey_submission (
               txid BLOB PRIMARY KEY, account_uuid BLOB, pczt BLOB
             );",
        )
        .unwrap();
        conn.execute("INSERT INTO transactions VALUES (1, ?1)", [[9_u8; 32]])
            .unwrap();
        if fail_delete {
            conn.execute_batch(
                "CREATE TRIGGER fail_release BEFORE DELETE ON ext_onekey_submission
                 BEGIN SELECT RAISE(ABORT, 'simulated storage failure'); END;",
            )
            .unwrap();
        }
        let staged = Staged {
            txid: TxId::from_bytes([1; 32]),
            raw: vec![],
            inputs: vec![OutputRef::new(
                TxId::from_bytes([9; 32]),
                PoolType::ORCHARD,
                0,
            )],
            lock_expiry: BlockHeight::from_u32(1040),
        };
        (wallet(conn), staged, uuid::Uuid::from_u128(1).to_string())
    }

    fn held_state(db: &mut Db) -> (Option<Vec<u8>>, bool) {
        db.transactionally_with_extension(|_, ext| {
            let owner =
                ext.query_row("SELECT lock_owner FROM orchard_received_notes", [], |row| {
                    row.get(0)
                })?;
            Ok::<_, RuntimeError>((owner, tx_state::first_submission(ext)?.is_some()))
        })
        .unwrap()
    }

    #[test]
    fn definite_rejection_releases_the_input_and_durable_submission_together() {
        let (mut db, staged, account) = lock_fixture(false);
        hold_inputs(&mut db, &account, &staged, &[4, 5]).unwrap();
        assert_eq!(held_state(&mut db), (Some(vec![1; 32]), true));
        release_rejected(
            &mut db,
            &account,
            &staged,
            &RuntimeError::new(ErrorCode::BroadcastRejected),
        )
        .unwrap();
        assert_eq!(held_state(&mut db), (None, false));
    }

    #[test]
    fn failed_release_rolls_back_the_unlock_and_keeps_recovery_state() {
        let (mut db, staged, account) = lock_fixture(true);
        hold_inputs(&mut db, &account, &staged, &[4, 5]).unwrap();
        assert!(release_rejected(
            &mut db,
            &account,
            &staged,
            &RuntimeError::new(ErrorCode::BroadcastRejected)
        )
        .is_err());
        assert_eq!(held_state(&mut db), (Some(vec![1; 32]), true));
    }

    #[test]
    fn a_second_submission_cannot_take_the_first_submissions_inputs() {
        let (mut db, mut staged, account) = lock_fixture(false);
        hold_inputs(&mut db, &account, &staged, &[4, 5]).unwrap();
        staged.txid = TxId::from_bytes([2; 32]);
        assert!(hold_inputs(&mut db, &account, &staged, &[6, 7]).is_err());
        assert_eq!(held_state(&mut db), (Some(vec![1; 32]), true));
    }

    #[test]
    fn a_spend_already_held_by_another_stored_transaction_is_refused() {
        check(&mut spend_fixture(None), 1).unwrap();
        // Expired at 990, below the 1000 tip: its notes are free again.
        check(&mut spend_fixture(Some((None, Some(990)))), 1).unwrap();
        for held in [(None, Some(1040)), (None, None), (Some(900), Some(990))] {
            let error = check(&mut spend_fixture(Some(held)), 1).unwrap_err();
            assert_eq!(error.code, ErrorCode::TransactionBuildError);
            assert_eq!(error.params["reason"], "inputsAlreadySpent");
        }
    }

    #[test]
    fn a_pczt_spending_from_another_account_is_refused() {
        let error = check(&mut spend_fixture(None), 2).unwrap_err();
        assert_eq!(error.params["reason"], "accountMismatch");
    }
}
#[cfg(all(test, not(target_arch = "wasm32")))]
mod policy_tests {
    use super::*;

    /// Sapling is invalid in both places: it cannot be proven, and change would
    /// land in a pool this wallet cannot spend.
    #[test]
    fn sapling_is_not_a_spendable_source_nor_a_change_destination() {
        assert!(parse_change_pool("ironwood").is_ok());
        assert!(parse_change_pool("orchard").is_err());
        assert!(parse_change_pool("sapling").is_err());
        assert!(parse_spend_source(Some("sapling")).is_err());
    }

    /// Around an upgrade the local tip can be on the old branch while the
    /// transaction targets the new one, or vice versa.
    #[test]
    fn the_proving_circuit_follows_the_transactions_branch_not_the_tip() {
        use orchard::circuit::OrchardCircuitVersion;
        use zcash_protocol::consensus::{BranchId, NetworkUpgrade, Parameters};
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let Some(activation) = params.activation_height(NetworkUpgrade::Nu6_3) else {
            return;
        };
        let before = activation - 1;
        assert_eq!(
            circuit_version_for_branch(&params, u32::from(BranchId::Nu6_3), before),
            OrchardCircuitVersion::PostNu6_3
        );
        assert_eq!(
            circuit_version_for_branch(&params, u32::from(BranchId::Nu6_1), activation),
            OrchardCircuitVersion::FixedPostNu6_2
        );
        assert_eq!(
            circuit_version_for_branch(&params, 0xdead_beef, activation),
            OrchardCircuitVersion::PostNu6_3,
            "unknown branch falls back to the tip"
        );
    }

    #[test]
    fn a_sapling_only_recipient_is_refused_before_building() {
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let ufvk = |seed: u8| {
            zcash_keys::keys::UnifiedSpendingKey::from_seed(
                &params,
                &[seed; 32],
                0u32.try_into().unwrap(),
            )
            .unwrap()
            .to_unified_full_viewing_key()
        };
        let key = ufvk(9);
        let sapling = key.sapling().unwrap().default_address().1;
        let encoded = zcash_keys::encoding::encode_payment_address_p(&params, &sapling);
        let parse = |s: &str| zcash_address::ZcashAddress::try_from_encoded(s).unwrap();
        assert!(recipient_needs_sapling_output(&params, &parse(&encoded)));
        let (ua, _) = key
            .default_address(zcash_keys::keys::UnifiedAddressRequest::AllAvailableKeys)
            .unwrap();
        assert!(
            !recipient_needs_sapling_output(&params, &parse(&ua.encode(&params))),
            "a UA with an Orchard receiver is fine"
        );
    }

    #[test]
    fn a_tex_recipient_is_recognised() {
        use zcash_address::ToAddress;
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let tex = zcash_address::ZcashAddress::from_tex(
            zcash_protocol::consensus::NetworkType::Main,
            [9; 20],
        );
        let t1 = zcash_address::ZcashAddress::from_transparent_p2pkh(
            zcash_protocol::consensus::NetworkType::Main,
            [9; 20],
        );
        // Pinned: the app decodes the same pair in JS.
        assert_eq!(tex.encode(), "tex1pyysjzgfpyysjzgfpyysjzgfpyysjzgfl7c006");
        assert_eq!(t1.encode(), "t1JhNuqypTPZu5vdcjsvGHzbTCrTd9Xwtru");
        assert!(recipient_is_tex(&params, &tex));
        assert!(!recipient_is_tex(&params, &t1));
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod combine_tests {
    use super::*;
    use pczt::roles::{
        creator::Creator, io_finalizer::IoFinalizer, signer::Signer,
        spend_finalizer::SpendFinalizer,
    };
    use transparent::address::TransparentAddress;
    use transparent::bundle::{OutPoint, TxOut};
    use zcash_primitives::transaction::builder::{BuildConfig, Builder};
    use zcash_protocol::consensus::{BlockHeight, Network};

    fn secret_key() -> secp256k1::SecretKey {
        secp256k1::SecretKey::from_slice(&[0x11; 32]).unwrap()
    }

    /// A transparent-only PCZT, io-finalized. `extra` appends a second
    /// input/output pair; the first pair stays byte-identical so the two PCZTs
    /// still merge.
    fn transparent_pczt(send_zat: u64, recipient: u8, extra: bool) -> Vec<u8> {
        let secp = secp256k1::Secp256k1::new();
        let pubkey = secp256k1::PublicKey::from_secret_key(&secp, &secret_key());
        let from = TransparentAddress::from_pubkey(&pubkey);
        let mut builder = Builder::new(
            Network::MainNetwork,
            BlockHeight::from_u32(3_000_000),
            BuildConfig::Standard {
                sapling_anchor: None,
                orchard_anchor: None,
                ironwood_anchor: None,
                orchard_padding: BundlePadding::DEFAULT,
                ironwood_padding: BundlePadding::DEFAULT,
            },
        );
        let input = |index: u8, value: u64, builder: &mut Builder<Network, ()>| {
            builder
                .add_transparent_p2pkh_input(
                    pubkey,
                    OutPoint::new([index; 32], 0),
                    TxOut::new(Zatoshis::const_from_u64(value), from.script().into()),
                )
                .unwrap();
        };
        input(7, 100_000, &mut builder);
        if extra {
            input(9, 50_000, &mut builder);
        }
        builder
            .add_transparent_output(
                &TransparentAddress::PublicKeyHash([recipient; 20]),
                Zatoshis::const_from_u64(send_zat),
            )
            .unwrap();
        if extra {
            builder
                .add_transparent_output(
                    &TransparentAddress::PublicKeyHash([0xcc; 20]),
                    Zatoshis::const_from_u64(50_000),
                )
                .unwrap();
        }
        let parts = builder
            .build_for_pczt(rand::rngs::OsRng, &Zip317FeeRule::standard())
            .unwrap();
        let pczt = Creator::build_from_parts(parts.pczt_parts).unwrap();
        IoFinalizer::new(pczt)
            .finalize_io()
            .unwrap()
            .serialize()
            .unwrap()
    }

    fn signed_copy(original: &[u8]) -> Vec<u8> {
        let mut signer = Signer::new(pczt::Pczt::parse(original).unwrap()).unwrap();
        for index in 0..pczt::Pczt::parse(original)
            .unwrap()
            .transparent()
            .inputs()
            .len()
        {
            signer.sign_transparent(index, &secret_key()).unwrap();
        }
        signer.finish().serialize().unwrap()
    }

    fn reason(error: &RuntimeError) -> String {
        format!("{error:?}")
    }

    #[test]
    fn combining_a_signature_copy_keeps_the_approved_transaction() {
        let original = transparent_pczt(90_000, 0xaa, false);
        let combined = combine_pczt(&original, &signed_copy(&original)).unwrap();
        let approved = Signer::new(pczt::Pczt::parse(&original).unwrap()).unwrap();
        let result = Signer::new(pczt::Pczt::parse(&combined).unwrap()).unwrap();
        assert_eq!(result.shielded_sighash(), approved.shielded_sighash());
    }

    #[test]
    fn transaction_identity_is_stable_on_retry_and_changes_with_the_recipient() {
        let signed = signed_copy(&transparent_pczt(90_000, 0xaa, false));
        let extract = |bytes: &[u8]| {
            let finalized = SpendFinalizer::new(pczt::Pczt::parse(bytes).unwrap())
                .finalize_spends()
                .unwrap();
            extract_pczt(finalized).unwrap()
        };
        let first = extract(&signed);
        assert_eq!(first.txid(), extract(&signed).txid());
        let other = extract(&signed_copy(&transparent_pczt(90_000, 0xbb, false)));
        assert_ne!(first.txid(), other.txid());
    }

    // The bare Combiner already refuses this; pinned against upstream changes.
    #[test]
    fn combine_rejects_a_conflicting_recipient() {
        let approved = transparent_pczt(90_000, 0xaa, false);
        let other = transparent_pczt(90_000, 0xbb, false);
        let error = combine_pczt(&approved, &signed_copy(&other)).unwrap_err();
        assert!(
            reason(&error).contains("DataMismatch"),
            "{}",
            reason(&error)
        );
    }

    // Upstream behaviour, pinned: the Combiner refuses to grow a non-modifiable
    // PCZT. The sighash check in `combine_pczt` covers a future relaxation.
    #[test]
    fn our_pczts_are_not_modifiable_so_the_combiner_refuses_to_grow_them() {
        let approved = transparent_pczt(90_000, 0xaa, false);
        let enlarged = transparent_pczt(90_000, 0xaa, true);
        let global = pczt::Pczt::parse(&approved).unwrap();
        let global = global.global();
        assert!(!global.inputs_modifiable());
        assert!(!global.outputs_modifiable());
        assert!(!global.shielded_modifiable());
        let error = combine_pczt(&approved, &signed_copy(&enlarged)).unwrap_err();
        assert!(
            reason(&error).contains("DataMismatch"),
            "{}",
            reason(&error)
        );
    }

    #[test]
    fn combine_rejects_a_copy_that_already_carries_a_script_sig() {
        // script_sig is outside the sighash, so the bare Combiner would adopt it.
        let original = transparent_pczt(90_000, 0xaa, false);
        let finalized = SpendFinalizer::new(pczt::Pczt::parse(&signed_copy(&original)).unwrap())
            .finalize_spends()
            .unwrap()
            .serialize()
            .unwrap();
        let error = combine_pczt(&original, &finalized).unwrap_err();
        assert!(
            reason(&error).contains("transparentScriptSig"),
            "{}",
            reason(&error)
        );
    }

    // Hardware signers get a copy without the Sapling `bsk`; the original keeps it.
    #[test]
    fn combine_restores_sapling_bsk_from_the_original() {
        let original = transparent_pczt(90_000, 0xaa, false);
        let device_copy =
            pczt::roles::redactor::Redactor::new(pczt::Pczt::parse(&original).unwrap())
                .redact_sapling_with(|mut sapling| sapling.clear_bsk())
                .finish()
                .serialize()
                .unwrap();
        assert!(device_copy.len() < original.len());
        assert_eq!(
            combine_pczt(&original, &signed_copy(&device_copy)).unwrap(),
            combine_pczt(&original, &signed_copy(&original)).unwrap(),
        );
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod submit_tests {
    use super::*;

    #[test]
    fn first_submit_keeps_spent_inputs_unknown() {
        let spent = RuntimeError::with(
            ErrorCode::BroadcastOutcomeUnknown,
            json!({ "errorCode": -26 }),
        )
        .detail("bad-txns-inputs-spent".to_string());
        let rejection = first_submit_rejection(spent).unwrap_err();
        assert_eq!(rejection.code, ErrorCode::BroadcastOutcomeUnknown);
        assert_eq!(rejection.params["errorCode"], -26);
        assert_eq!(rejection.detail.as_deref(), Some("bad-txns-inputs-spent"));
    }

    #[test]
    fn first_submit_keeps_network_errors_unknown() {
        let error = RuntimeError::new(ErrorCode::NetworkError);
        assert_eq!(
            first_submit_rejection(error).unwrap_err().code,
            ErrorCode::NetworkError
        );
    }
}

#[cfg(test)]
mod memo_tests {
    use super::*;
    #[test]
    fn text_and_hex_fields_preserve_bytes_and_reject_ambiguous_input() {
        let text = parse_memo(Some(" 中文\n🙂 "), None).unwrap().unwrap();
        let hex = hex::encode(text.as_array());
        assert_eq!(parse_memo(None, Some(&hex)).unwrap().unwrap(), text);
        let binary = format!("ff000102{}", "00".repeat(508));
        assert_eq!(
            hex::encode(parse_memo(None, Some(&binary)).unwrap().unwrap().as_array()),
            binary
        );
        assert!(parse_memo(Some("text"), Some(&hex)).is_err());
        assert!(parse_memo(None, Some("ff00")).is_err());
        assert!(parse_memo(None, Some(&"zz".repeat(512))).is_err());
        assert!(parse_memo(Some(&"🙂".repeat(129)), None).is_err());
        assert!(parse_memo(None, None).unwrap().is_none());
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod owned_output_tests {
    use super::*;
    use orchard::keys::{FullViewingKey, Scope, SpendAuthorizingKey, SpendingKey};
    use orchard::note::{NoteVersion, RandomSeed, Rho};
    use orchard::note_encryption::{IronwoodDomain, OrchardDomain};
    use orchard::pczt::{Action, Bundle};
    use orchard::tree::{MerkleHashOrchard, MerklePath};
    use orchard::value::NoteValue;
    use orchard::{Address, Anchor, Note};
    use pczt::roles::{creator::Creator, io_finalizer::IoFinalizer, signer::Signer};
    use zcash_client_backend::data_api::Zip32Derivation;
    use zcash_note_encryption::{try_output_recovery_with_pkd_esk, Domain};
    use zcash_primitives::transaction::builder::{BuildConfig, Builder};
    use zcash_protocol::consensus::{BlockHeight, Network};

    const SEED: [u8; 32] = [7; 32];
    const ACCOUNT: u32 = 5;
    const SEND: u64 = 60_000;
    const INPUT: u64 = 200_000;

    fn spending_key(seed: &[u8], account: u32) -> SpendingKey {
        SpendingKey::from_zip32_seed(seed, 133, zip32::AccountId::try_from(account).unwrap())
            .unwrap()
    }

    fn ours() -> FullViewingKey {
        FullViewingKey::from(&spending_key(&SEED, ACCOUNT))
    }

    fn fingerprint() -> [u8; 32] {
        zip32::fingerprint::SeedFingerprint::from_seed(&SEED)
            .unwrap()
            .to_bytes()
    }

    fn derivation() -> Zip32Derivation {
        Zip32Derivation::new(
            zip32::fingerprint::SeedFingerprint::from_bytes(fingerprint()),
            zip32::AccountId::try_from(ACCOUNT).unwrap(),
        )
    }

    fn expected_path() -> Vec<u32> {
        [32, 133, ACCOUNT].map(|i| i | (1 << 31)).to_vec()
    }

    fn note(recipient: Address, value: u64, version: NoteVersion) -> Note {
        (1u8..)
            .find_map(|b| {
                let rho = Rho::from_bytes(&[b; 32]).into_option()?;
                let rseed = RandomSeed::from_bytes([b.wrapping_add(1); 32], &rho).into_option()?;
                Note::from_parts(recipient, NoteValue::from_raw(value), rho, rseed, version)
                    .into_option()
            })
            .unwrap()
    }

    fn witness(note: &Note) -> (MerklePath, Anchor) {
        let leaf = MerkleHashOrchard::from_bytes(&[0; 32]).unwrap();
        let path = MerklePath::from_parts(0, [leaf; 32]);
        let anchor = path.root(note.commitment().into());
        (path, anchor)
    }

    struct Built {
        pczt: pczt::Pczt,
        orchard_change: Option<usize>,
        ironwood_recipient: usize,
        ironwood_change: Option<usize>,
    }

    /// A post-NU6.3 shielded spend of one Orchard note: the payment goes to a
    /// foreign Ironwood address, change goes to our internal address, in
    /// Ironwood or (when `orchard_change`) in Orchard. The external recipient
    /// gets `user_address` as `create_pczt_from_proposal` sets it.
    fn spend(spent_scope: Scope, orchard_change: bool) -> Built {
        spend_to(spent_scope, orchard_change, 9)
    }

    fn spend_to(spent_scope: Scope, orchard_change: bool, recipient_seed: u8) -> Built {
        let fvk = ours();
        let foreign = FullViewingKey::from(&spending_key(&[recipient_seed; 32], 0));
        let recipient = foreign.address_at(0u32, Scope::External);
        let change_address = fvk.address_at(0u32, Scope::Internal);
        let spent = note(fvk.address_at(0u32, spent_scope), INPUT, NoteVersion::V2);
        let (path, anchor) = witness(&spent);

        let builder = |change: u64| {
            let mut b = Builder::new(
                Network::MainNetwork,
                BlockHeight::from_u32(3_500_000),
                BuildConfig::Standard {
                    sapling_anchor: None,
                    orchard_anchor: Some(anchor),
                    ironwood_anchor: Some(Anchor::empty_tree()),
                    orchard_padding: BundlePadding::DEFAULT,
                    ironwood_padding: BundlePadding::DEFAULT,
                },
            );
            b.add_orchard_spend::<std::convert::Infallible>(fvk.clone(), spent, path.clone())
                .unwrap();
            b.add_ironwood_output::<std::convert::Infallible>(
                None,
                recipient,
                Zatoshis::const_from_u64(SEND),
                MemoBytes::empty(),
            )
            .unwrap();
            let change = Zatoshis::const_from_u64(change);
            if orchard_change {
                b.add_orchard_change_output::<std::convert::Infallible>(
                    fvk.clone(),
                    Some(fvk.to_ovk(Scope::Internal)),
                    change_address,
                    change,
                    MemoBytes::empty(),
                )
                .unwrap();
            } else {
                b.add_ironwood_output::<std::convert::Infallible>(
                    Some(fvk.to_ovk(Scope::Internal)),
                    change_address,
                    change,
                    MemoBytes::empty(),
                )
                .unwrap();
            }
            b
        };
        let fee_rule = Zip317FeeRule::standard();
        let fee = u64::from(builder(1).get_fee(&fee_rule).unwrap());
        let result = builder(INPUT - SEND - fee)
            .build_for_pczt(rand::rngs::OsRng, &fee_rule)
            .unwrap();
        let ironwood_recipient = result.ironwood_meta.output_action_index(0).unwrap();
        let (orchard_change, ironwood_change) = if orchard_change {
            (result.orchard_meta.output_action_index(0), None)
        } else {
            (None, result.ironwood_meta.output_action_index(1))
        };
        let pczt = IoFinalizer::new(Creator::build_from_parts(result.pczt_parts).unwrap())
            .finalize_io()
            .unwrap();
        let pczt = pczt::roles::updater::Updater::new(pczt)
            .update_ironwood_with(|mut updater| {
                updater.update_action_with(ironwood_recipient, |mut action| {
                    action.set_output_user_address("recipient-ua".into());
                    Ok(())
                })
            })
            .unwrap()
            .finish();
        Built {
            pczt,
            orchard_change,
            ironwood_recipient,
            ironwood_change,
        }
    }

    #[tokio::test]
    async fn another_payment_with_a_shared_input_cannot_reuse_the_stored_transaction() {
        use rand::SeedableRng;
        use rand_chacha::ChaCha20Rng;

        let first = spend_to(Scope::External, false, 9).pczt;
        let other = spend_to(Scope::External, false, 10).pczt;
        let common = first
            .orchard()
            .actions()
            .iter()
            .find_map(|action| {
                let nf = action.spend().nullifier();
                other
                    .orchard()
                    .actions()
                    .iter()
                    .any(|a| a.spend().nullifier() == nf)
                    .then_some(*nf)
            })
            .expect("synthetic payments spend the same note");
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch("CREATE TABLE scan_queue (block_range_end INTEGER);
            INSERT INTO scan_queue VALUES (3500001);
            CREATE TABLE blocks (height INTEGER);
            INSERT INTO blocks VALUES (3500000);
            CREATE TABLE transactions (id_tx INTEGER, txid BLOB, raw BLOB, mined_height INTEGER, expiry_height INTEGER);
            CREATE TABLE orchard_received_notes (id INTEGER, nf BLOB);
            CREATE TABLE orchard_received_note_spends (orchard_received_note_id INTEGER, transaction_id INTEGER);
            CREATE TABLE ironwood_received_notes (id INTEGER, nf BLOB);
            CREATE TABLE ironwood_received_note_spends (ironwood_received_note_id INTEGER, transaction_id INTEGER);
            CREATE TABLE ext_onekey_tx_state (txid BLOB, account_uuid BLOB, rejection_code INTEGER, rejection_reason TEXT);
            CREATE TABLE ext_onekey_submission (txid BLOB, account_uuid BLOB, pczt BLOB);")
            .unwrap();
        conn.execute(
            "INSERT INTO transactions VALUES (1, ?1, x'00', NULL, 3500040)",
            [[1u8; 32]],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO orchard_received_notes VALUES (7, ?1)",
            [common],
        )
        .unwrap();
        conn.execute("INSERT INTO orchard_received_note_spends VALUES (7, 1)", [])
            .unwrap();
        let mut db = zcash_client_sqlite::WalletDb::from_connection(
            conn,
            Network::MainNetwork,
            crate::clock::HostClock,
            ChaCha20Rng::from_seed([0; 32]),
        );
        let mut client = crate::network::LightClient::new(
            tonic::transport::Endpoint::from_static("http://127.0.0.1:1").connect_lazy(),
        );

        // The second payment has no proof or authorization. A stored shared
        // input must never let it skip extraction and return the first txid.
        match submit_signed_pczt(
            &mut db,
            &mut client,
            &uuid::Uuid::nil().to_string(),
            &other.serialize().unwrap(),
        )
        .await
        {
            Err(error) => assert_eq!(error.params["stage"], "extractAndStore"),
            Ok(_) => panic!("a different payment must not reuse a stored transaction"),
        }
    }

    /// Runs `f` on the parsed Orchard (or Ironwood) bundle of `pczt`.
    fn inspect<T>(pczt: &pczt::Pczt, ironwood: bool, f: impl FnOnce(&Bundle) -> T) -> T {
        let mut out = None;
        let verifier = pczt::roles::verifier::Verifier::new(pczt.clone());
        let run = |bundle: &Bundle| {
            out = Some(f(bundle));
            Ok::<_, pczt::roles::verifier::OrchardError<()>>(())
        };
        if ironwood {
            verifier.with_ironwood(run).unwrap();
        } else {
            verifier.with_orchard(run).unwrap();
        }
        out.unwrap()
    }

    fn mark(pczt: pczt::Pczt) -> pczt::Pczt {
        mark_owned_outputs(pczt, &ours(), &derivation(), 133).unwrap()
    }

    /// What a hardware signer needs to accept each Orchard-protocol output:
    /// - a padding output the sender cannot recover must be zero-valued, in a
    ///   bundle without cross-address, with no ock, ZIP-32 derivation or
    ///   `user_address`;
    /// - a wallet-owned output carries a ZIP-32 derivation matching the
    ///   account's seed fingerprint and path, to a recipient its FVK owns;
    /// - a payment carries a `user_address`;
    /// - otherwise only a zero-valued output with no ock and an empty memo.
    fn firmware_rejection(bundle: &Bundle, ironwood: bool) -> Option<String> {
        let fvk = ours();
        for (index, action) in bundle.actions().iter().enumerate() {
            let output = action.output();
            let value = output.value().unwrap().inner();
            let memo = recover_memo(action, ironwood);
            let Some(memo) = memo else {
                if bundle.flags().cross_address_enabled()
                    || value != 0
                    || output.ock().is_some()
                    || output.zip32_derivation().is_some()
                    || output.user_address().is_some()
                {
                    return Some(format!("{index}: invalid fabricated output"));
                }
                continue;
            };
            if let Some(derivation) = output.zip32_derivation() {
                let path: Vec<u32> = derivation
                    .derivation_path()
                    .iter()
                    .map(|i| i.index())
                    .collect();
                if derivation.seed_fingerprint() != &fingerprint() || path != expected_path() {
                    return Some(format!("{index}: path/fingerprint mismatch"));
                }
                if fvk
                    .scope_for_address(&output.recipient().unwrap())
                    .is_none()
                {
                    return Some(format!("{index}: recipient not owned"));
                }
                continue;
            }
            if output.user_address().is_some() {
                continue;
            }
            let empty_memo = memo.iter().all(|b| *b == 0)
                || (memo[0] == 0xF6 && memo[1..].iter().all(|b| *b == 0));
            if !(value == 0 && output.ock().is_none() && empty_memo) {
                return Some(format!("{index}: Missing Orchard output address"));
            }
        }
        None
    }

    /// Sender-side recovery as the firmware does it (no ock in our PCZTs, so
    /// via pk_d and esk derived from the note).
    fn recover_memo(action: &Action, ironwood: bool) -> Option<[u8; 512]> {
        let output = action.output();
        let rho = Rho::from_bytes(&action.spend().nullifier().to_bytes()).unwrap();
        let note = Note::from_parts(
            output.recipient().unwrap(),
            output.value().unwrap(),
            rho,
            output.rseed().unwrap(),
            *output.note_version(),
        )
        .unwrap();
        if ironwood {
            let domain = IronwoodDomain::for_pczt_action(action);
            let esk = IronwoodDomain::derive_esk(&note)?;
            try_output_recovery_with_pkd_esk(&domain, IronwoodDomain::get_pk_d(&note), esk, action)
                .map(|(_, _, memo)| memo)
        } else {
            let domain = OrchardDomain::for_pczt_action(action);
            let esk = OrchardDomain::derive_esk(&note)?;
            try_output_recovery_with_pkd_esk(&domain, OrchardDomain::get_pk_d(&note), esk, action)
                .map(|(_, _, memo)| memo)
        }
    }

    fn has_our_path(action: &Action) -> bool {
        action
            .output()
            .zip32_derivation()
            .as_ref()
            .is_some_and(|d| {
                d.seed_fingerprint() == &fingerprint()
                    && d.derivation_path()
                        .iter()
                        .map(|i| i.index())
                        .collect::<Vec<_>>()
                        == expected_path()
            })
    }

    fn assert_only_marked(bundle: &Bundle, marked: &[usize]) {
        for (index, action) in bundle.actions().iter().enumerate() {
            assert_eq!(
                action.output().zip32_derivation().is_some(),
                marked.contains(&index),
                "action {index}"
            );
            if marked.contains(&index) {
                assert!(has_our_path(action), "action {index}");
                assert!(action.output().user_address().is_none(), "action {index}");
            }
        }
    }

    fn owned_by_us(action: &Action) -> Option<Scope> {
        ours().scope_for_address(&action.output().recipient().unwrap())
    }

    /// Has a zero-valued output to an address that is not ours.
    fn is_padded(bundle: &Bundle) -> bool {
        bundle.actions().iter().any(|a| {
            owned_by_us(a).is_none()
                && a.output().value().unwrap().inner() == 0
                && a.output().user_address().is_none()
        })
    }

    #[test]
    fn unmarked_change_is_what_the_firmware_rejected() {
        let built = spend(Scope::External, false);
        assert_eq!(
            inspect(&built.pczt, true, |b| firmware_rejection(b, true)),
            Some(format!(
                "{}: Missing Orchard output address",
                built.ironwood_change.unwrap()
            ))
        );
    }

    #[test]
    fn ironwood_change_gets_the_account_path_and_the_recipient_keeps_its_address() {
        let built = spend(Scope::External, false);
        let change = built.ironwood_change.unwrap();
        let pczt = mark(built.pczt);

        inspect(&pczt, true, |bundle| {
            assert_only_marked(bundle, &[change]);
            let recipient = bundle.actions()[built.ironwood_recipient].output();
            assert_eq!(recipient.user_address().as_deref(), Some("recipient-ua"));
            assert_eq!(firmware_rejection(bundle, true), None);
        });
        // The Orchard bundle holds the real spend's fabricated zero output (to
        // our external address, randomized ciphertext) and padding: untouched.
        inspect(&pczt, false, |bundle| {
            assert_only_marked(bundle, &[]);
            assert!(bundle
                .actions()
                .iter()
                .any(|a| owned_by_us(a) == Some(Scope::External)));
            assert!(is_padded(bundle));
            assert_eq!(firmware_rejection(bundle, false), None);
        });
    }

    #[test]
    fn orchard_change_gets_the_account_path() {
        let built = spend(Scope::Internal, true);
        let change = built.orchard_change.unwrap();
        let pczt = mark(built.pczt);

        inspect(&pczt, false, |bundle| {
            assert!(has_our_path(&bundle.actions()[change]));
            // Internal-scope spends get a decryptable fabricated zero output to
            // our own address; it is ours and carries the path too.
            let owned: Vec<usize> = (0..bundle.actions().len())
                .filter(|&i| owned_by_us(&bundle.actions()[i]).is_some())
                .collect();
            assert_eq!(owned.len(), 2);
            assert_only_marked(bundle, &owned);
            assert_eq!(firmware_rejection(bundle, false), None);
        });
        inspect(&pczt, true, |bundle| {
            assert!(is_padded(bundle));
            assert_only_marked(bundle, &[]);
            assert_eq!(firmware_rejection(bundle, true), None);
        });
    }

    #[test]
    fn marked_pczt_round_trips_and_still_signs_in_software() {
        for (scope, orchard_change) in [(Scope::External, false), (Scope::Internal, true)] {
            let bytes = mark(spend(scope, orchard_change).pczt).serialize().unwrap();
            let parsed = pczt::Pczt::parse(&bytes).unwrap();
            assert_eq!(parsed.clone().serialize().unwrap(), bytes);

            let ask = SpendAuthorizingKey::from(&spending_key(&SEED, ACCOUNT));
            let actions = parsed.orchard().actions().len();
            let mut signer = Signer::new(parsed).unwrap();
            let signed = (0..actions)
                .filter(|&i| signer.sign_orchard(i, &ask).is_ok())
                .count();
            assert!(signed >= 1);
        }
    }

    #[test]
    fn marking_the_randomized_fabricated_output_would_be_rejected() {
        let pczt = spend(Scope::External, false).pczt;
        let fabricated = inspect(&pczt, false, |bundle| {
            (0..bundle.actions().len())
                .find(|&i| owned_by_us(&bundle.actions()[i]) == Some(Scope::External))
                .unwrap()
        });
        let pczt = pczt::roles::updater::Updater::new(pczt)
            .update_orchard_with(|mut updater| {
                updater.update_action_with(fabricated, |mut action| {
                    let path = expected_path();
                    let derivation =
                        orchard::pczt::Zip32Derivation::parse(fingerprint(), path).unwrap();
                    action.set_output_zip32_derivation(derivation);
                    Ok(())
                })
            })
            .unwrap()
            .finish();
        assert_eq!(
            inspect(&pczt, false, |b| firmware_rejection(b, false)),
            Some(format!("{fabricated}: invalid fabricated output"))
        );
    }

    #[test]
    fn foreign_keys_mark_nothing() {
        let built = spend(Scope::External, false);
        let foreign = FullViewingKey::from(&spending_key(&[3; 32], 0));
        let pczt = mark_owned_outputs(built.pczt, &foreign, &derivation(), 133).unwrap();
        inspect(&pczt, false, |b| assert_only_marked(b, &[]));
        inspect(&pczt, true, |b| assert_only_marked(b, &[]));
    }
}
