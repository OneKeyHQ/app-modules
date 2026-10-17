//! Sending: PCZT construction and broadcast.
//!
//! Four stages; signing is not in this crate:
//!
//! ```text
//!   runtime.pcztCreate()          build (needs wallet state: note selection, witness, anchor)
//!   runtime.pcztProve()           prove; pure computation, no private keys
//!   keys.pcztSignWithAccountKeys() sign (needs the spending keys; in onekey-zcash-keys)
//!   runtime.pcztSubmit()          extract, store, broadcast (retries resend the stored bytes)
//! ```
//!
//! Proving precedes signing: the upstream Signer role needs enough information
//! to verify the proofs cover the right data, so proofs must already be in the PCZT.
//!
//! Key separation: build and broadcast need the wallet database, signing needs
//! the spending keys; the two never live in the same crate.
//!
//! ## Why this stays in Rust
//!
//! Note selection, ZIP-317 fees, anchor choice and witnesses are consensus-relevant:
//! a wrong anchor gets the transaction rejected, a wrong fee can lose funds. The
//! mechanism stays in the upstream implementation.
//!
//! Policy knobs (change pool, dust policy, OVK policy, confirmations, bundle
//! padding) all come from the host; this crate makes no product decisions and
//! hides no defaults.

use zcash_client_backend::data_api::locking::LockOwner;
use zcash_client_backend::data_api::wallet::{
    create_pczt_from_proposal, extract_and_store_transaction_from_pczt,
    input_selection::GreedyInputSelector, propose_transfer, unlock_proposal_inputs, LockRequest,
};
use zcash_client_backend::data_api::WalletRead;
use zcash_client_backend::fees::{zip317::SingleOutputChangeStrategy, DustOutputPolicy};
use zcash_client_backend::wallet::OvkPolicy;
use zcash_primitives::transaction::builder::BundlePadding;
use zcash_primitives::transaction::fees::zip317::FeeRule as Zip317FeeRule;
use zcash_protocol::{memo::MemoBytes, value::Zatoshis, ShieldedPool};

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
/// `INSUFFICIENT_FUNDS` (with the shortfall) and `NOT_SYNCED` must not collapse
/// into a generic `TRANSACTION_BUILD_ERROR`.
///
/// Upstream reports insufficient funds from two places; both are handled:
///
/// - `Error::InsufficientFunds`: selection cannot cover the amount (transfers)
/// - `Error::Change(ChangeError::InsufficientFunds)`: selection succeeded but
///   change plus fee does not fit (shielding)
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
                // Precomputed so the host does not subtract in the wrong direction.
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

/// Parses the host's spend-source pool. Build and quote both require it;
/// `None` is rejected in `build_proposal`.
///
/// Orchard remains a valid spend source, but cannot receive new change.
/// Sapling spending is unsupported because this runtime does not ship its prover.
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
    /// Pad the Orchard bundle to a fixed action count (hides the real count at
    /// the cost of a larger transaction and higher fee).
    pub pad_orchard_bundle: bool,
    /// Allow spending zero-conf outputs of our own shielding (ZIP-315 recommends it).
    ///
    /// Must match the balance query's setting, or the balance shows funds that
    /// selection refuses to spend.
    pub allow_zero_conf_shielding: bool,
    /// Select notes only from this shielded pool. Required: `None` is rejected at build.
    ///
    /// 1. Excludes Sapling. Upstream `SpendPolicy::default()` allows it and
    ///    selection prefers the oldest eligible note, so an account with pre-2022
    ///    history would pick Sapling notes and fail at proving (`PROVE_SAPLING = false`),
    ///    blocking every send.
    /// 2. Enables per-pool withdrawal, e.g. moving old Orchard funds to a
    ///    transparent address without spending Ironwood funds the user wants kept private.
    ///
    /// If the pool cannot cover the amount the result is `InsufficientFunds`;
    /// upstream never crosses pools.
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
/// Quote and build must both use this entry point. Fields such as
/// `pad_orchard_bundle` change the bundle size and therefore the ZIP-317 fee;
/// wiring them separately lets the quoted fee silently drift from the paid fee.
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

/// Recipient can only receive into Sapling. Sapling proving parameters are not
/// shipped, so such a transaction would fail at proving after the user confirmed;
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

// Keep the legacy text argument compatible; new hosts pass the exact ZIP 302 field.
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
    lock: Option<zcash_client_backend::data_api::wallet::LockRequest>,
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

    // Spend only the selected shielded pool, never transparent UTXOs (as upstream's default).
    let spend_policy = {
        use zcash_client_backend::data_api::wallet::input_selection::SpendPolicy;
        // Not SpendPolicy::default(): it allows Sapling. See SendPolicy::spend_source.
        // Shielded pools are never combined, and the current pool is defined
        // only by the host (ZCASH_CURRENT_SHIELDED_POOL): no default here, missing is rejected.
        let pool = policy.spend_source.ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "spendSource", "reason": "required" }),
            )
        })?;
        let mut base = SpendPolicy::shielded_pools([pool]);
        // A retry under the same reservation id must reselect what that id
        // already holds. The default policy excludes every lock, the caller's
        // own included, so the retry would lock a second set and leave the
        // first frozen until expiry. Only this owner's locks are admitted.
        if let Some(lock) = &lock {
            use zcash_client_backend::data_api::locking::LockedInputPolicy;
            use zcash_client_backend::data_api::wallet::input_selection::NonEmptyBTreeSet;
            base = base.with_locked_input_policy(LockedInputPolicy::PreferLocked(
                NonEmptyBTreeSet::singleton(lock.owner()),
            ));
        }
        base
    };

    let selector = GreedyInputSelector::<Db>::new();
    let change = SingleOutputChangeStrategy::<_, Db>::new(
        Zip317FeeRule::standard(),
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
            lock,
            None,
        );

    proposal.map_err(|e| propose_err("proposeTransfer", e))
}

/// Returns the fee only; builds no PCZT and locks no notes.
///
/// Must use the same proposal path as `create_pczt` so the quote matches the fee paid.
pub fn quote(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
) -> Result<String> {
    let proposal = build_proposal(db, account_uuid, to_address, amount_zat, memo, policy, None)?;
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

// ── Note locking (the host contract's "reservation") ────────────────────
//
// Notes behave like UTXOs: spent whole, revealing a nullifier; a second
// transaction spending the same note is rejected by the node.
//
// Between selection and broadcast (select -> review -> password -> sign -> broadcast)
// a second send or shield would reselect the same notes. Locking marks them as
// held by one operation so other selections skip them; cancel releases them.
// Upstream locks also expire by block height, covering an app crash.
//
// The proposal stays in memory: `unlock_proposal_inputs` needs it to know which notes to unlock.
thread_local! {
    static RESERVATIONS: std::cell::RefCell<
        std::collections::HashMap<String, (String, TransferProposal, Vec<u8>)>,
    > = std::cell::RefCell::new(std::collections::HashMap::new());
}

fn hex32(bytes: &[u8; 32]) -> String {
    bytes.iter().map(|b| format!("{b:02x}")).collect()
}

fn parse_owner(reservation_id: &str) -> Result<LockOwner> {
    let bad = || {
        RuntimeError::with(
            ErrorCode::InvalidReservationId,
            json!({ "value": reservation_id }),
        )
    };
    if reservation_id.len() != 64 {
        return Err(bad());
    }
    let mut out = [0u8; 32];
    for (i, chunk) in reservation_id.as_bytes().chunks(2).enumerate() {
        let hex = std::str::from_utf8(chunk).map_err(|_| bad())?;
        out[i] = u8::from_str_radix(hex, 16).map_err(|_| bad())?;
    }
    Ok(LockOwner::new(out))
}

/// Releases the notes held by a reservation.
///
/// Idempotent: an unknown id (sent, already released, or never locked) returns
/// `false`, not an error.
pub fn release_reservation(db: &mut Db, reservation_id: &str) -> Result<bool> {
    let owner = parse_owner(reservation_id)?;

    // Finalization is committed in the wallet database before the host can
    // observe the txid. A durability/bridge failure can therefore leave the
    // host holding a reservation id for a transaction that already spends the
    // selected notes. In that state release must only discard the in-memory
    // proposal; unlocking its inputs would make spent notes selectable again.
    let finalized_txid = db.transactionally_with_extension(|_wallet, ext| {
        tx_state::existing_txid_for_reservation(ext, reservation_id, None)
    })?;
    if finalized_txid.is_some() {
        forget_reservation(reservation_id);
        return Ok(false);
    }

    if let Some((account_uuid, proposal, approved_pczt)) =
        RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id))
    {
        if let Err(e) = unlock_proposal_inputs(db, &proposal, owner) {
            RESERVATIONS.with(|r| {
                r.borrow_mut().insert(
                    reservation_id.to_owned(),
                    (account_uuid, proposal, approved_pczt),
                );
            });
            return Err(build_err("unlockProposalInputs", format!("{e:?}")));
        }
        // Anything else this owner still holds (a set locked before a retry
        // replaced the in-memory proposal) goes too.
        release_persisted_reservation(db, reservation_id, owner)?;
        return Ok(true);
    }
    release_persisted_reservation(db, reservation_id, owner)
}

// The proposal map is process-local, but the lock owner survives in SQLite.
// Read the owner's exact output references and use the upstream lock API;
// never clear another proposal's locks or write wallet-owned tables directly.
fn release_persisted_reservation(
    db: &mut Db,
    reservation_id: &str,
    owner: LockOwner,
) -> Result<bool> {
    use rusqlite::OptionalExtension;
    use zcash_client_backend::{data_api::OutputLockStore, wallet::OutputRef};
    use zcash_primitives::transaction::TxId;
    use zcash_protocol::PoolType;

    db.transactionally_with_extension(|wallet, ext| {
        if tx_state::existing_txid_for_reservation(ext, reservation_id, None)?.is_some() {
            return Ok(false);
        }
        let mut released = false;
        for (table, index, pool) in [
            (
                "sapling_received_notes",
                "output_index",
                PoolType::Shielded(ShieldedPool::Sapling),
            ),
            (
                "orchard_received_notes",
                "action_index",
                PoolType::Shielded(ShieldedPool::Orchard),
            ),
            (
                "ironwood_received_notes",
                "action_index",
                PoolType::Shielded(ShieldedPool::Ironwood),
            ),
            (
                "transparent_received_outputs",
                "output_index",
                PoolType::Transparent,
            ),
        ] {
            loop {
                let output = ext
                    .query_row(
                        &format!(
                            "SELECT tx.txid, output.{index}
                              FROM {table} output
                              JOIN transactions tx ON tx.id_tx = output.transaction_id
                              WHERE output.lock_owner = ?1 LIMIT 1"
                        ),
                        [owner.as_bytes()],
                        |row| Ok((row.get::<_, [u8; 32]>(0)?, row.get::<_, u32>(1)?)),
                    )
                    .optional()
                    .map_err(|e| build_err("readReservationLocks", e))?;
                let Some((txid, output_index)) = output else {
                    break;
                };
                let output = OutputRef::new(TxId::from_bytes(txid), pool, output_index);
                if !wallet
                    .unlock_output(&output, owner)
                    .map_err(|e| build_err("releaseReservationLock", e))?
                {
                    return Err(build_err(
                        "releaseReservationLock",
                        "persisted lock was not released",
                    ));
                }
                released = true;
            }
        }
        Ok(released)
    })
}

/// Drops the reservation record once the transaction is stored.
///
/// The stored spend now holds the notes; unlocking would make spent notes
/// selectable again.
pub fn forget_reservation(reservation_id: &str) {
    RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id));
}

/// Drops in-memory proposals of a deleted account; its notes are gone with it.
pub fn forget_account_reservations(account_uuid: &str) {
    RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_uuid, _, _)| owner_uuid.as_str() != account_uuid)
    });
}

/// A reservation maps to one transaction. Rebuilding under a stored id would lock
/// a second set under the same owner while submit only replays the old one, so
/// the new locks could be neither used nor released.
fn refuse_submitted_reservation(db: &mut Db, reservation_id: &str) -> Result<()> {
    let submitted = db.transactionally_with_extension(|_wallet, ext| {
        tx_state::existing_txid_for_reservation(ext, reservation_id, None)
    })?;
    match submitted {
        Some(txid) => Err(RuntimeError::with(
            ErrorCode::InvalidReservationId,
            json!({ "reason": "alreadySubmitted", "txid": txid }),
        )),
        None => Ok(()),
    }
}

/// Builds an unsigned transfer PCZT and locks the selected notes.
///
/// Returns `{ pcztHex, reservationId }`. The caller must keep `reservationId`
/// until submit (`pcztSubmit`) or cancel (`releaseReservation`). Locks also
/// expire by block height, but only as a fallback.
///
/// `lock_for_blocks` is host-chosen and must outlast the transaction's expiry
/// height. The lock covers build until storage; after that the stored
/// transaction holds the notes.
///
/// `reservation_id` may be supplied (64 hex chars) so the caller can generate
/// and persist it before building, and after a crash still release or relock
/// its notes. Otherwise one is generated randomly.
// The public binding keeps proposal policy explicit at the call boundary.
#[allow(clippy::too_many_arguments)]
pub fn create_pczt(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
    lock_for_blocks: u32,
    reservation_id: Option<&str>,
) -> Result<(Vec<u8>, String, u64)> {
    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;

    let owner = match reservation_id {
        Some(id) => parse_owner(id)?,
        None => LockOwner::random(&mut rand::rngs::OsRng),
    };
    let reservation_id = hex32(owner.as_bytes());
    refuse_submitted_reservation(db, &reservation_id)?;
    let proposal = build_proposal(
        db,
        account_uuid,
        to_address,
        amount_zat,
        memo,
        policy,
        Some(LockRequest::new(owner, lock_for_blocks)),
    )?;
    let fee_zat = proposal
        .steps()
        .iter()
        .map(|step| u64::from(step.balance().fee_required()))
        .sum();

    // InputsErrT / ChangeErrT appear only in the Err branch and cannot be inferred.
    // The notes are already locked: any failure below must unlock before returning,
    // since the caller has no reservationId yet and could not release them.
    let built = (|| -> Result<Vec<u8>> {
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
            None,
            policy.padding(),
        )
        .map_err(|e| build_err("createPczt", format!("{e:?}")))?;
        let pczt = mark_wallet_outputs(db, account_id, pczt)?;

        pczt.serialize().map_err(|e| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
                .detail(format!("{e:?}"))
        })
    })();

    let bytes = match built {
        Ok(b) => b,
        Err(e) => {
            // Best effort: keep the original error; lock expiry covers an unlock failure.
            let _ = unlock_proposal_inputs(db, &proposal, owner);
            return Err(e);
        }
    };

    // Keep the proposal: unlocking needs it to know which notes to release.
    RESERVATIONS.with(|r| {
        r.borrow_mut().insert(
            reservation_id.clone(),
            (account_uuid.to_owned(), proposal, bytes.clone()),
        );
    });
    Ok((bytes, reservation_id, fee_zat))
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
/// scope) and whose note decrypts under that scope's IVK. Outputs that already
/// name a `user_address` (real recipients) are left alone, as are outputs we do
/// not own (padding). Undecryptable owned outputs are the zero-valued outputs
/// the post-NU6.3 Orchard builder pairs with external-scope spends, with a
/// randomized ciphertext: no one can decrypt them, so like padding they get no
/// path claiming them as wallet outputs.
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

/// The transaction's own branch decides, not our tip: around an upgrade the two
/// disagree, and a proof under the wrong circuit is rejected. The tip is only
/// the fallback for a branch id this build does not know.
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
/// `TransactionExtractor` cannot catch this later: it recomputes the sighash of
/// the merged transaction, so it only checks self-consistency, not what the user approved.
///
/// The bare `Combiner` currently blocks substitution only as a side effect of
/// upstream behaviour (our PCZTs have `tx_modifiable == 0`, and all effect fields
/// are set so conflicts are detected), which an upgrade could change.
///
/// Invariant enforced here: sighashes must be byte-identical before and after,
/// and `scriptSig` must stay empty (it is outside the sighash, `None` in the
/// original, and adopted as-is by `merge_optional`). Fails before proving;
/// errors carry `field`.
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
    // scriptSig is outside the sighash and is the one field the other side can
    // inject (always None in our original; merge_optional adopts it). We never set
    // it; SpendFinalizer builds it from partial_signatures at extraction.
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

/// Stores the signed PCZT in the wallet; from now on this transaction holds its spent notes.
fn store_signed_pczt(
    db: &mut Db,
    account_uuid: &str,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
    reservation_id: &str,
) -> Result<String> {
    db.transactionally_with_extension(|wallet, ext| {
        let txid =
            extract_and_store_transaction_from_pczt::<_, zcash_client_sqlite::ReceivedNoteId>(
                wallet,
                pczt,
                None,
                Some(orchard_vk),
            )
            .map_err(|e| build_err("extractAndStore", format!("{e:?}")))?
            .to_string();
        tx_state::insert_finalized(ext, &txid, account_uuid, reservation_id)?;
        Ok(txid)
    })
}

/// Outcome of a submit.
pub enum SubmitOutcome {
    Accepted {
        txid: String,
    },
    /// The trusted node rejected it; keep the durable record until chain expiry.
    Rejected {
        txid: String,
        error: RuntimeError,
    },
    /// Unknown whether the node received it: stored, holds its notes, rebroadcast on sync.
    Unknown {
        txid: String,
        error: RuntimeError,
    },
}

// A spent-input/duplicate-nullifier reply can refer to this transaction itself.
fn first_submit_rejection(error: RuntimeError) -> std::result::Result<RuntimeError, RuntimeError> {
    if error.code == ErrorCode::BroadcastRejected {
        Ok(error)
    } else {
        Err(error)
    }
}

fn record_submit_outcome(db: &mut Db, txid: String, result: Result<()>) -> SubmitOutcome {
    match result {
        Ok(()) => match tx_state::mark_accepted(db, &txid) {
            Ok(()) => SubmitOutcome::Accepted { txid },
            Err(error) => SubmitOutcome::Unknown { txid, error },
        },
        Err(error) => match first_submit_rejection(error) {
            Ok(error) => match tx_state::mark_rejected(db, &txid, &error) {
                Ok(()) => SubmitOutcome::Rejected { txid, error },
                Err(error) => SubmitOutcome::Unknown { txid, error },
            },
            Err(error) => SubmitOutcome::Unknown { txid, error },
        },
    }
}

/// Persist the exact authorized transaction before any external broadcast.
/// Retries and crash recovery always rebroadcast the stored bytes.
pub async fn submit_signed_pczt(
    db: &mut Db,
    client: &mut crate::network::LightClient,
    account_uuid: &str,
    pczt_bytes: &[u8],
    reservation_id: &str,
) -> Result<SubmitOutcome> {
    // Already stored by an earlier submit (host retried): rebroadcast it, never extract again.
    let stored = db.transactionally_with_extension(|_wallet, ext| {
        tx_state::existing_txid_for_reservation(ext, reservation_id, Some(account_uuid))
    })?;
    if let Some(txid) = stored {
        if let Err(error) = tx_state::assert_broadcastable(db, &txid) {
            return Ok(if error.code == ErrorCode::BroadcastRejected {
                SubmitOutcome::Rejected { txid, error }
            } else {
                SubmitOutcome::Unknown { txid, error }
            });
        }
        let raw = match raw_transaction(db, &txid) {
            Ok(raw) => raw,
            Err(error) => return Ok(SubmitOutcome::Unknown { txid, error }),
        };
        let result = crate::network::broadcast(client, raw).await;
        return Ok(record_submit_outcome(db, txid, result));
    }

    let approved_pczt = RESERVATIONS
        .with(|reservations| {
            reservations
                .borrow()
                .get(reservation_id)
                .filter(|(owner, _, _)| owner == account_uuid)
                .map(|(_, _, pczt)| pczt.clone())
        })
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidReservationId,
                json!({ "reason": "missingOrWrongAccount" }),
            )
        })?;
    let bound_pczt = combine_pczt(&approved_pczt, pczt_bytes)?;
    let pczt = pczt::Pczt::parse(&bound_pczt).map_err(|e| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{e:?}"))
    })?;
    let params = *db.params();
    let chain_tip = db
        .chain_height()
        .map_err(|error| build_err("chainHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    let version =
        circuit_version_for_branch(&params, *pczt.global().consensus_branch_id(), chain_tip);
    let orchard_vk = crate::circuit_keys::verifying_key(version);

    // This transaction atomically stores raw bytes and the reservation/txid association.
    // If it fails, nothing has been sent and the caller can safely abandon the proposal.
    let stored_txid = store_signed_pczt(db, account_uuid, pczt, orchard_vk, reservation_id)?;
    forget_reservation(reservation_id);
    // Extraction randomizes binding signatures. Broadcast only the stored bytes,
    // so the first attempt and every recovery attempt use identical authorizations.
    let raw = match raw_transaction(db, &stored_txid) {
        Ok(raw) => raw,
        Err(error) => {
            return Ok(SubmitOutcome::Unknown {
                txid: stored_txid,
                error,
            })
        }
    };
    let result = crate::network::broadcast(client, raw).await;
    Ok(record_submit_outcome(db, stored_txid, result))
}

/// Reads a stored transaction's raw bytes from the wallet for rebroadcast.
pub fn raw_transaction(db: &Db, txid: &str) -> Result<Vec<u8>> {
    use zcash_primitives::transaction::TxId;

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

/// Clears all advisory locks for an account during explicit state repair.
/// Normal cancellation uses the durable owner-scoped `release_reservation`,
/// including after restart. This broader operation also clears other proposals
/// and must not be used as an automatic cancellation fallback.
pub fn clear_locked_outputs(db: &mut Db, account_uuid: &str) -> Result<u32> {
    use zcash_client_backend::data_api::OutputLockStore;

    let account_id = account::parse_account_id(db, account_uuid)?;
    let n = db
        .clear_locked_outputs(account_id)
        .map_err(|e| build_err("clearLockedOutputs", e))?;

    RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_account_uuid, _, _)| owner_account_uuid != account_uuid);
    });
    Ok(n as u32)
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod reservation_recovery_tests {
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

    fn outcome_wallet(fail_update: bool) -> (Db, String) {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch("CREATE TABLE ext_onekey_tx_state (txid BLOB PRIMARY KEY, account_uuid BLOB, reservation_id TEXT, broadcast_state TEXT, rejection_code INTEGER, rejection_reason TEXT);").unwrap();
        conn.execute("INSERT INTO ext_onekey_tx_state (txid,account_uuid,reservation_id,broadcast_state) VALUES (?1,?2,'review','pending')", rusqlite::params![[1u8;32].as_slice(), uuid::Uuid::from_u128(1).as_bytes().as_slice()]).unwrap();
        if fail_update {
            conn.execute_batch("CREATE TRIGGER fail_state BEFORE UPDATE ON ext_onekey_tx_state BEGIN SELECT RAISE(ABORT,'synthetic write failure'); END;").unwrap();
        }
        (wallet(conn), "01".repeat(32))
    }

    #[test]
    fn both_first_and_retried_broadcasts_record_definitive_node_outcomes() {
        let (mut db, txid) = outcome_wallet(false);
        assert!(matches!(
            record_submit_outcome(&mut db, txid.clone(), Ok(())),
            SubmitOutcome::Accepted { .. }
        ));
        let rejected = RuntimeError::new(ErrorCode::BroadcastRejected).detail("bad-txns-expired");
        assert!(matches!(
            record_submit_outcome(&mut db, txid.clone(), Err(rejected)),
            SubmitOutcome::Rejected { .. }
        ));
        assert_eq!(
            tx_state::assert_broadcastable(&mut db, &txid)
                .unwrap_err()
                .code,
            ErrorCode::BroadcastRejected
        );
    }

    #[test]
    fn accepted_broadcast_with_failed_state_write_keeps_its_known_txid() {
        let (mut db, txid) = outcome_wallet(true);
        match record_submit_outcome(&mut db, txid.clone(), Ok(())) {
            SubmitOutcome::Unknown { txid: retained, .. } => assert_eq!(retained, txid),
            _ => panic!("an accepted transaction must not become a signing failure"),
        }
        tx_state::assert_broadcastable(&mut db, &txid).unwrap();
    }

    #[test]
    fn duplicate_spend_or_network_failure_keeps_the_persisted_send_pending() {
        for code in [ErrorCode::BroadcastOutcomeUnknown, ErrorCode::NetworkError] {
            let (mut db, txid) = outcome_wallet(false);
            assert!(matches!(
                record_submit_outcome(&mut db, txid.clone(), Err(RuntimeError::new(code))),
                SubmitOutcome::Unknown { .. }
            ));
            tx_state::assert_broadcastable(&mut db, &txid).unwrap();
        }
    }

    #[test]
    fn a_reservation_that_already_has_a_stored_transaction_cannot_build_again() {
        let (mut db, txid) = outcome_wallet(false);
        let e = refuse_submitted_reservation(&mut db, "review").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidReservationId);
        assert_eq!(e.params["txid"], txid);
        refuse_submitted_reservation(&mut db, &"ab".repeat(32)).unwrap();
    }

    #[test]
    fn retry_cannot_use_another_accounts_reservation() {
        let (mut db, txid) = outcome_wallet(false);
        let owner = uuid::Uuid::from_u128(1).to_string();
        let other = uuid::Uuid::from_u128(2).to_string();
        db.transactionally_with_extension(|_, ext| {
            assert_eq!(
                tx_state::existing_txid_for_reservation(ext, "review", Some(&owner))?,
                Some(txid)
            );
            assert_eq!(
                tx_state::existing_txid_for_reservation(ext, "review", Some(&other))
                    .unwrap_err()
                    .code,
                ErrorCode::InvalidReservationId
            );
            Ok::<(), RuntimeError>(())
        })
        .unwrap();
    }

    #[test]
    fn restart_cancellation_releases_only_its_owner_and_preserves_finalized_locks() {
        let path =
            std::env::temp_dir().join(format!("zcash-reservation-{}.sqlite", uuid::Uuid::new_v4()));
        let owner = [1_u8; 32];
        let other_owner = [2_u8; 32];
        let conn = Connection::open(&path).unwrap();
        conn.execute_batch(
            "CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB);
                            CREATE TABLE ext_onekey_tx_state (txid BLOB, reservation_id TEXT, account_uuid BLOB);",
        )
        .unwrap();
        conn.execute("INSERT INTO transactions VALUES (1, ?1)", [[3_u8; 32]])
            .unwrap();
        for (table, index) in [
            ("sapling_received_notes", "output_index"),
            ("orchard_received_notes", "action_index"),
            ("ironwood_received_notes", "action_index"),
            ("transparent_received_outputs", "output_index"),
        ] {
            conn.execute_batch(&format!(
                "CREATE TABLE {table} (
                transaction_id INTEGER, {index} INTEGER, lock_owner BLOB, lock_expiry_height INTEGER
            );"
            ))
            .unwrap();
            conn.execute(
                &format!("INSERT INTO {table} VALUES (1, 0, ?1, 1000), (1, 1, ?2, 1000)"),
                rusqlite::params![owner, other_owner],
            )
            .unwrap();
        }
        drop(wallet(conn));

        let mut db = wallet(Connection::open(&path).unwrap());
        assert!(release_reservation(&mut db, &hex32(&owner)).unwrap());
        assert!(!release_reservation(&mut db, &hex32(&owner)).unwrap());
        let conn = Connection::open(&path).unwrap();
        for table in [
            "sapling_received_notes",
            "orchard_received_notes",
            "ironwood_received_notes",
            "transparent_received_outputs",
        ] {
            let locks: u32 = conn.query_row(&format!("SELECT COUNT(*) FROM {table} WHERE lock_owner = ?1 AND lock_expiry_height = 1000"), [other_owner], |row| row.get(0)).unwrap();
            assert_eq!(locks, 1);
            let released: u32 = conn.query_row(&format!("SELECT COUNT(*) FROM {table} WHERE lock_owner IS NULL AND lock_expiry_height IS NULL"), [], |row| row.get(0)).unwrap();
            assert_eq!(released, 1);
        }
        conn.execute(
            "INSERT INTO ext_onekey_tx_state (txid, reservation_id, account_uuid) VALUES (?1, ?2, ?3)",
            rusqlite::params![[4_u8; 32], hex32(&other_owner), uuid::Uuid::from_u128(1).as_bytes()],
        )
        .unwrap();
        assert!(!release_reservation(&mut db, &hex32(&other_owner)).unwrap());
        let locks: u32 = conn
            .query_row(
                "SELECT COUNT(*) FROM orchard_received_notes WHERE lock_owner = ?1",
                [other_owner],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(locks, 1);
        drop(conn);
        drop(db);
        std::fs::remove_file(path).unwrap();
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
        // Pinned: the app decodes this same pair in JS (sdkZcash/index.test.ts).
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
    ///
    /// Transparent-only is deliberate. `IoFinalizer` clears the three
    /// `tx_modifiable` flags ONLY when the transaction has shielded spends or
    /// outputs (io_finalizer/mod.rs:70), so a transparent-only PCZT stays
    /// modifiable and the bare Combiner will happily append the other side's
    /// extra inputs, outputs and Orchard actions.
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

    // Conflicting values on a field both sides carry: the bare Combiner already
    // refuses this one. Pinned so a future upstream bump cannot quietly relax it.
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

    // What actually stops a counterparty from appending an input, output or
    // Orchard action: `tx_modifiable` is already 0 on anything our pipeline
    // builds, and the Combiner refuses to grow a non-modifiable bundle. That is
    // upstream's behaviour, not ours, so pin it -- the sighash comparison in
    // `combine_pczt` is what still covers us if a future bump relaxes it.
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
        // script_sig is absent from the sighash and absent from our original, so
        // the bare Combiner would adopt whatever the other side put there.
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

    // Hardware signers get a copy without the Sapling `bsk` (see the keys
    // runtime's `redact_for_hardware_signer`); the original keeps it.
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
        let fvk = ours();
        let foreign = FullViewingKey::from(&spending_key(&[9; 32], 0));
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

    /// Mirrors the firmware's per-output acceptance (firmware-pro2
    /// app/zcash/sign_pczt.py `_validate_orchard_protocol_outputs` and
    /// signer_parse.rs): an output is accepted if it is an unrecoverable
    /// fabricated zero output with nothing attached, carries a path whose
    /// fingerprint/schema match and whose recipient the account FVK owns, carries
    /// a `user_address`, or is a zero-valued output with no ock and no memo.
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

            // Same loop as zcash-keys-runtime sign_pczt_with_account_keys.
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
