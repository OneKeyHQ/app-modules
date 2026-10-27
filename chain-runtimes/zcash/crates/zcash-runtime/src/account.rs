//! Account import and reads (UFVK import, balances); no private keys or signing.

use incrementalmerkletree::{frontier::Frontier, Hashable};
use incrementalmerkletree::{Marking, Retention};
use shardtree::{error::ShardTreeError, store::ShardStore, ShardTree};
use zcash_client_backend::data_api::chain::ChainState;
use zcash_client_backend::data_api::scanning::ScanPriority;
use zcash_client_backend::data_api::wallet::ConfirmationsPolicy;
use zcash_client_backend::data_api::{
    Account as _, AccountBalance, AccountBirthday, AccountPurpose, AccountSource, Balance,
    WalletCommitmentTrees, WalletRead, WalletWrite, Zip32Derivation,
};
use zcash_client_sqlite::error::SqliteClientError;
use zcash_keys::keys::UnifiedFullViewingKey;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::{self, LightClient};
use crate::wallet::Db;
use serde_json::json;

/// Imports an account from a UFVK; scanning starts at `birthday_height`.
pub async fn import_ufvk(
    db: &mut Db,
    client: &mut LightClient,
    account_name: &str,
    ufvk_str: &str,
    birthday_height: u32,
    derivation: Option<(&str, u32)>,
) -> Result<String> {
    let params = *db.params();

    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;

    // A seed fingerprint plus account index imports the account as spending, so
    // zip32_derivation is set on PCZT spends and (by send::mark_wallet_outputs) on
    // change outputs; signers can then verify change returns to the wallet.
    let purpose = match derivation {
        Some((fingerprint_hex, account_index)) => AccountPurpose::Spending {
            derivation: Some(parse_zip32_derivation(fingerprint_hex, account_index)?),
        },
        None => AccountPurpose::ViewOnly,
    };

    // The account starts at the birthday, so the anchor is the tree state just before it.
    let anchor_height = birthday_height.saturating_sub(1);
    let treestate = network::tree_state(client, anchor_height).await?;
    check_birthday_tree_state(&treestate, anchor_height, &params)?;

    let birthday = AccountBirthday::from_treestate(treestate, None).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidBlockHeight,
            json!({ "birthdayHeight": birthday_height }),
        )
        .detail(format!("{e:?}"))
    })?;

    match import_with_birthday(db, account_name, &ufvk, &birthday, purpose.clone()) {
        Err(error) if error.params["reason"] == "checkpointsDiverged" => {
            let height = error.params["checkpointHeight"].as_u64().ok_or(error)? as u32;
            let treestate = network::tree_state(client, height).await?;
            check_birthday_tree_state(&treestate, height, &params)?;
            let checkpoint = AccountBirthday::from_treestate(treestate, None).map_err(|e| {
                RuntimeError::new(ErrorCode::InvalidBlockHeight).detail(format!("{e:?}"))
            })?;
            import_with_checkpoint(
                db,
                account_name,
                &ufvk,
                &birthday,
                purpose,
                checkpoint.prior_chain_state(),
            )
            .map_err(|mut error| {
                if error.params["reason"] == "checkpointsDiverged" {
                    error.params["reason"] = json!("checkpointRecoveryStalled");
                }
                error
            })
        }
        result => result,
    }
}

fn missing_checkpoint_height(error: &SqliteClientError) -> Option<u32> {
    let SqliteClientError::CorruptedData(message) = error else {
        return None;
    };
    [
        "the Sapling note commitment tree retains checkpoints both above and below height ",
        "the Orchard note commitment tree retains checkpoints both above and below height ",
        "the Ironwood note commitment tree retains checkpoints both above and below height ",
    ]
    .iter()
    .find_map(|prefix| message.strip_prefix(prefix))
    .and_then(|rest| rest.split_once(',').map(|(height, _)| height))
    .and_then(|height| height.parse().ok())
}

pub(crate) fn import_with_checkpoint(
    db: &mut Db,
    name: &str,
    ufvk: &UnifiedFullViewingKey,
    birthday: &AccountBirthday,
    purpose: AccountPurpose,
    checkpoint: &ChainState,
) -> Result<String> {
    db.transactionally(|wallet| {
        insert_missing_checkpoints(wallet, checkpoint)?;
        wallet
            .import_account_ufvk(name, ufvk, birthday, purpose, None)
            .map(|account| account.id().expose_uuid().to_string())
    })
    .map_err(import_error)
}

fn insert_missing_checkpoints<W>(
    wallet: &mut W,
    checkpoint: &ChainState,
) -> std::result::Result<(), SqliteClientError>
where
    W: WalletRead<Error = SqliteClientError>
        + WalletCommitmentTrees<Error = zcash_client_sqlite::wallet::commitment_tree::Error>,
{
    let height = checkpoint.block_height();
    if wallet
        .get_block_hash(height)?
        .is_some_and(|hash| hash != checkpoint.block_hash())
    {
        return Err(SqliteClientError::BlockConflict(height));
    }
    // Do not mark blocks scanned or replace existing checkpoint marks.
    wallet.with_sapling_tree_mut(|tree| {
        insert_checkpoint(tree, checkpoint.final_sapling_tree().clone(), height)
    })?;
    wallet.with_orchard_tree_mut(|tree| {
        insert_checkpoint(tree, checkpoint.final_orchard_tree().clone(), height)
    })?;
    wallet.with_ironwood_tree_mut(|tree| {
        insert_checkpoint(tree, checkpoint.final_ironwood_tree().clone(), height)
    })?;
    Ok(())
}

/// Rescans one account from `from`. An earlier start rewinds and keeps what is
/// already known; a later one starts the account over at that height.
pub async fn rescan_account(
    db: &mut Db,
    client: &mut LightClient,
    uuid: &str,
    from: u32,
    has_pending_send: bool,
) -> Result<String> {
    let id = parse_account_id(db, uuid)?;
    let current = u32::from(db.get_account_birthday(id).map_err(import_error)?);
    let params = *db.params();
    if from > current {
        refuse_restart_with_pending_send(has_pending_send)?;
        let birthday = chain_state_at(client, from.saturating_sub(1), &params).await?;
        let uuid = match restart_with_birthday(db, id, &birthday, None) {
            Ok(uuid) => uuid,
            Err(error) => {
                let Some(height) = error.params["checkpointHeight"].as_u64() else {
                    return Err(error);
                };
                let checkpoint = chain_state_at(client, height as u32, &params).await?;
                restart_with_birthday(db, id, &birthday, Some(checkpoint.prior_chain_state()))?
            }
        };
        return Ok(json!({ "fromHeight": from, "accountUuid": uuid }).to_string());
    }
    if from < current {
        let birthday = chain_state_at(client, from.saturating_sub(1), &params).await?;
        if let Err(error) = rescan_with_birthday(db, id, &birthday, None) {
            let Some(height) = error.params["checkpointHeight"].as_u64() else {
                return Err(error);
            };
            let checkpoint = chain_state_at(client, height as u32, &params).await?;
            rescan_with_birthday(db, id, &birthday, Some(checkpoint.prior_chain_state()))?;
        }
    } else if db.chain_height().map_err(import_error)?.is_some() {
        crate::sync::queue_rescan_from(db, from)?;
    }
    Ok(json!({ "fromHeight": from, "accountUuid": uuid }).to_string())
}

/// Starting over drops the notes a pending send spends; it waits until the send settles.
fn refuse_restart_with_pending_send(has_pending_send: bool) -> Result<()> {
    if has_pending_send {
        return Err(RuntimeError::with(
            ErrorCode::WalletBusy,
            json!({ "operation": "rescanAccount", "reason": "pendingSend" }),
        ));
    }
    Ok(())
}

async fn chain_state_at(
    client: &mut LightClient,
    height: u32,
    params: &zcash_protocol::consensus::Network,
) -> Result<AccountBirthday> {
    let state = network::tree_state(client, height).await?;
    check_birthday_tree_state(&state, height, params)?;
    AccountBirthday::from_treestate(state, None)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidBlockHeight).detail(format!("{e:?}")))
}

/// Deletes the account and imports it again at `birthday` in one transaction,
/// so nothing it found before the new birthday is kept.
pub(crate) fn restart_with_birthday(
    db: &mut Db,
    id: zcash_client_sqlite::AccountUuid,
    birthday: &AccountBirthday,
    checkpoint: Option<&ChainState>,
) -> Result<String> {
    let account = db
        .get_account(id)
        .map_err(import_error)?
        .ok_or_else(|| RuntimeError::new(ErrorCode::AccountNotFound))?;
    let name = account.name().unwrap_or_default().to_owned();
    let ufvk = account
        .ufvk()
        .cloned()
        .ok_or_else(|| RuntimeError::new(ErrorCode::InvalidUfvk))?;
    let AccountSource::Imported { purpose, .. } = account.source().clone() else {
        return Err(RuntimeError::new(ErrorCode::InvalidAccountId));
    };
    db.transactionally_with_extension(|wallet, ext| {
        if let Some(checkpoint) = checkpoint {
            insert_missing_checkpoints(wallet, checkpoint).map_err(import_error)?;
        }
        wallet.delete_account(id).map_err(import_error)?;
        let uuid = wallet
            .import_account_ufvk(&name, &ufvk, birthday, purpose, None)
            .map_err(import_error)?
            .id()
            .expose_uuid()
            .to_string();
        // The old birthday's unscanned ranges would find what the new one excludes.
        prune_scan_queue_below_wallet_birthday(wallet)?;
        crate::tx_state::prune_orphaned_rows(ext)?;
        Ok(uuid)
    })
    .map_err(|mut error: RuntimeError| {
        error.params["operation"] = json!("rescanAccount");
        error
    })
}

pub(crate) fn rescan_with_birthday(
    db: &mut Db,
    id: zcash_client_sqlite::AccountUuid,
    birthday: &AccountBirthday,
    checkpoint: Option<&ChainState>,
) -> Result<()> {
    use std::collections::HashSet;
    use zcash_client_backend::data_api::error::RewindError;
    db.transactionally(|wallet| {
        if let Some(checkpoint) = checkpoint {
            insert_missing_checkpoints(wallet, checkpoint).map_err(import_error)?;
        }
        wallet
            .rewind_to_chain_state(birthday.prior_chain_state().clone(), HashSet::from([id]))
            .map_err(|error| match error {
                RewindError::DataSource(error) => import_error(error),
                error => RuntimeError::new(ErrorCode::DatabaseError).detail(format!("{error:?}")),
            })
    })
    .map_err(|mut error: RuntimeError| {
        error.params["operation"] = json!("rescanAccount");
        error
    })
}

fn insert_checkpoint<S, const DEPTH: u8, const SHARD_HEIGHT: u8>(
    tree: &mut ShardTree<S, DEPTH, SHARD_HEIGHT>,
    frontier: Frontier<S::H, DEPTH>,
    height: zcash_protocol::consensus::BlockHeight,
) -> std::result::Result<(), SqliteClientError>
where
    S: ShardStore<
        CheckpointId = zcash_protocol::consensus::BlockHeight,
        Error = zcash_client_sqlite::wallet::commitment_tree::Error,
    >,
    S::H: Hashable + Clone + PartialEq,
{
    let existing = tree
        .store()
        .get_checkpoint(&height)
        .map_err(|error| SqliteClientError::from(ShardTreeError::Storage(error)))?;
    if let Some(existing) = existing {
        if existing.position() != frontier.value().map(|frontier| frontier.position()) {
            return Err(SqliteClientError::CorruptedData(
                "checkpoint position mismatch".into(),
            ));
        }
        return Ok(());
    }
    tree.insert_frontier(
        frontier,
        Retention::Checkpoint {
            id: height,
            marking: Marking::None,
        },
    )
    .map_err(SqliteClientError::from)
}

pub(crate) fn import_with_birthday(
    db: &mut Db,
    name: &str,
    ufvk: &UnifiedFullViewingKey,
    birthday: &AccountBirthday,
    purpose: AccountPurpose,
) -> Result<String> {
    db.import_account_ufvk(name, ufvk, birthday, purpose, None)
        .map(|account| account.id().expose_uuid().to_string())
        .map_err(import_error)
}

pub(crate) fn import_error(error: SqliteClientError) -> RuntimeError {
    let mut params = json!({ "operation": "importAccount" });
    if let Some(height) = missing_checkpoint_height(&error) {
        params["reason"] = json!("checkpointsDiverged");
        params["checkpointHeight"] = json!(height);
    }
    RuntimeError::with(ErrorCode::DatabaseError, params).detail(error)
}

/// Pairs the spending derivation fields; half a pair is refused, not imported view-only.
pub fn derivation_pair(
    seed_fingerprint_hex: Option<&str>,
    account_index: Option<u32>,
) -> Result<Option<(&str, u32)>> {
    match (seed_fingerprint_hex, account_index) {
        (Some(fingerprint), Some(index)) => Ok(Some((fingerprint, index))),
        (None, None) => Ok(None),
        _ => Err(RuntimeError::with(
            ErrorCode::InvalidAccountId,
            json!({ "field": "derivation", "reason": "incomplete" }),
        )),
    }
}

fn parse_zip32_derivation(fingerprint_hex: &str, account_index: u32) -> Result<Zip32Derivation> {
    let bytes = hex::decode(fingerprint_hex).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidAccountId,
            json!({ "field": "seedFingerprint" }),
        )
        .detail(e)
    })?;
    let fingerprint: [u8; 32] = bytes.try_into().map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidAccountId,
            json!({ "field": "seedFingerprint", "expectedBytes": 32 }),
        )
    })?;
    let account_id = zip32::AccountId::try_from(account_index).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidAccountId,
            json!({ "field": "accountIndex", "value": account_index }),
        )
    })?;
    Ok(Zip32Derivation::new(
        zip32::fingerprint::SeedFingerprint::from_bytes(fingerprint),
        account_id,
    ))
}

pub fn balance_json(db: &Db, account_uuid: &str, policy: ConfirmationsPolicy) -> Result<String> {
    let account_id = parse_account_id(db, account_uuid)?;

    let summary = db.get_wallet_summary(policy).map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "walletSummary" }),
        )
        .detail(e)
    })?;

    // No summary before any block is scanned; reported as an error code, text left to the host.
    let Some(summary) = summary else {
        return Err(RuntimeError::new(ErrorCode::NotSynced));
    };

    let balances = summary.account_balances().get(&account_id).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::AccountNotFound,
            json!({ "accountId": account_uuid }),
        )
    })?;

    Ok(balance_value(
        u32::from(summary.chain_tip_height()),
        u32::from(summary.fully_scanned_height()),
        balances,
    )
    .to_string())
}

/// `total` is upstream's `Balance::total`, which leaves out notes worth at most
/// the ZIP-317 marginal fee; those are reported separately as `uneconomic`.
fn balance_value(
    chain_tip: u32,
    fully_scanned_height: u32,
    balances: &AccountBalance,
) -> serde_json::Value {
    // Per-pool values, no cross-pool merging: which pools count as spendable is
    // wallet policy; the host computes any totals.
    let pool = |b: &Balance| {
        serde_json::json!({
            "spendable": u64::from(b.spendable_value()),
            "pendingChange": u64::from(b.change_pending_confirmation()),
            "pendingSpendable": u64::from(b.value_pending_spendability()),
            // Held by an unexpired upstream output lock; this runtime's sends take none.
            "locked": u64::from(b.locked_value()),
            "total": u64::from(b.total()),
            // Unspent notes worth <= the marginal fee, confirmed or not. Upstream
            // note selection skips them, so they are never part of `spendable`.
            "uneconomic": u64::from(b.uneconomic_value()),
        })
    };

    let supported = [
        balances.orchard_balance(),
        balances.ironwood_balance(),
        balances.unshielded_regular_balance(),
        balances.unshielded_coinbase_balance(),
    ];
    let supported_total = supported
        .iter()
        .map(|balance| u64::from(balance.total()))
        .sum::<u64>();
    let supported_uneconomic = supported
        .iter()
        .map(|balance| u64::from(balance.uneconomic_value()))
        .sum::<u64>();

    serde_json::json!({
        "ready": true,
        "chainTip": chain_tip,
        "fullyScannedHeight": fully_scanned_height,

        "orchard": pool(balances.orchard_balance()),
        "ironwood": pool(balances.ironwood_balance()),

        // Coinbase is reported separately: it has its own maturity rules and can
        // only be shielded via `propose_shielding_coinbase`.
        "transparentRegular": pool(balances.unshielded_regular_balance()),
        "transparentCoinbase": pool(balances.unshielded_coinbase_balance()),

        // Sapling is intentionally excluded from both the pool list and the product total.
        "total": supported_total,
        "uneconomic": supported_uneconomic,
    })
}

/// Removes one account from the shared database. Deleting the database would
/// remove other wallets' accounts; the host drops it after the last account.
pub fn remove_account(db: &mut Db, account_uuid: &str) -> Result<()> {
    let account_id = parse_account_id(db, account_uuid)?;
    db.transactionally_with_extension(|wallet, ext| {
        wallet.delete_account(account_id).map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "deleteAccount" }),
            )
            .detail(e)
        })?;
        prune_scan_queue_below_wallet_birthday(wallet)?;
        crate::tx_state::prune_orphaned_rows(ext)
    })
}

/// delete_account leaves the scan queue alone, so ranges queued only for a
/// removed account's earlier birthday would still be scanned for nobody.
fn prune_scan_queue_below_wallet_birthday<W>(wallet: &mut W) -> Result<()>
where
    W: WalletWrite,
    <W as WalletRead>::Error: std::fmt::Display,
{
    let birthday = wallet.get_wallet_birthday().map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "getWalletBirthday" }),
        )
        .detail(e)
    })?;
    if let Some(birthday) = birthday {
        wallet
            .prune_scan_queue_below(birthday, Some(ScanPriority::OpenAdjacent))
            .map_err(|e| {
                RuntimeError::with(
                    ErrorCode::DatabaseError,
                    json!({ "operation": "pruneScanQueue" }),
                )
                .detail(e)
            })?;
    }
    Ok(())
}

/// Finds the account with this UFVK, or returns `"null"`. The UFVK is the host's
/// stable key; runtime UUIDs change after a purge.
pub fn account_by_ufvk(db: &Db, ufvk_str: &str) -> Result<String> {
    let params = *db.params();
    let target = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?
        .encode(&params);
    find_account(db, |acct| {
        acct.ufvk().map(|k| k.encode(&params)).as_deref() == Some(target.as_str())
    })
}

/// Finds the account the host named at import (its viewing key fingerprint), or
/// returns `"null"`. Names survive a purge, and need no viewing key to match.
/// The host deletes by this, so a name held twice is refused, never guessed.
pub fn account_by_name(db: &Db, name: &str) -> Result<String> {
    match matching_accounts(db, |acct| acct.name() == Some(name))?.as_slice() {
        [] => Ok("null".to_string()),
        [uuid] => Ok(json!({ "uuid": uuid }).to_string()),
        _ => Err(RuntimeError::with(
            ErrorCode::InvalidAccountId,
            json!({ "reason": "duplicateName" }),
        )),
    }
}

/// `{ uuid }` of the first account `matches` accepts, or `"null"`.
fn find_account(
    db: &Db,
    matches: impl Fn(&<Db as WalletRead>::Account) -> bool,
) -> Result<String> {
    Ok(matching_accounts(db, matches)?
        .first()
        .map_or_else(|| "null".to_string(), |uuid| json!({ "uuid": uuid }).to_string()))
}

/// UUIDs of every account `matches` accepts.
fn matching_accounts(
    db: &Db,
    matches: impl Fn(&<Db as WalletRead>::Account) -> bool,
) -> Result<Vec<String>> {
    let mut found = Vec::new();
    for id in db.get_account_ids().map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "getAccountIds" }),
        )
        .detail(e)
    })? {
        let Some(acct) = db.get_account(id).map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "getAccount" }),
            )
            .detail(e)
        })?
        else {
            continue;
        };
        if matches(&acct) {
            found.push(acct.id().expose_uuid().to_string());
        }
    }
    Ok(found)
}

pub fn list_accounts_json(db: &Db) -> Result<String> {
    let ids = db.get_account_ids().map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "listAccounts" }),
        )
        .detail(e)
    })?;
    let out: Vec<String> = ids.iter().map(|id| id.expose_uuid().to_string()).collect();
    Ok(serde_json::to_string(&out).unwrap_or_else(|_| "[]".into()))
}

pub fn parse_account_id(db: &Db, account_uuid: &str) -> Result<<Db as WalletRead>::AccountId> {
    let uuid = uuid_from_str(account_uuid)?;
    let id = zcash_client_sqlite::AccountUuid::from_uuid(uuid);
    db.get_account(id)
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "getAccount" }),
            )
            .detail(e)
        })?
        .map(|a| a.id())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::AccountNotFound,
                json!({ "accountId": account_uuid }),
            )
        })
}

fn uuid_from_str(s: &str) -> Result<uuid::Uuid> {
    uuid::Uuid::parse_str(s).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidAccountId, json!({ "value": s })).detail(e)
    })
}

/// Builds a confirmations policy. Both thresholds must be >= 1 and trusted <= untrusted.
pub fn confirmations_policy(
    trusted: u32,
    untrusted: u32,
    allow_zero_conf_shielding: bool,
) -> Result<ConfirmationsPolicy> {
    let bad = || {
        RuntimeError::with(
            ErrorCode::InvalidConfirmationsPolicy,
            json!({ "trusted": trusted, "untrusted": untrusted }),
        )
    };
    let t = std::num::NonZeroU32::new(trusted).ok_or_else(bad)?;
    let u = std::num::NonZeroU32::new(untrusted).ok_or_else(bad)?;
    ConfirmationsPolicy::new(t, u, allow_zero_conf_shielding).map_err(|e| bad().detail(e))
}

/// Upstream sets the birthday to the server's reported tree state height + 1
/// without checking it. A height too high skips earlier receipts forever and a
/// wrong network puts the account on another chain, so the height must equal
/// the request and the network (if given) must match the wallet.
fn check_birthday_tree_state(
    treestate: &zcash_client_backend::proto::service::TreeState,
    anchor_height: u32,
    params: &zcash_protocol::consensus::Network,
) -> Result<()> {
    use zcash_protocol::consensus::Network;
    let expected_network = match params {
        Network::MainNetwork => "main",
        Network::TestNetwork => "test",
    };
    let network_ok = treestate.network.is_empty() || treestate.network == expected_network;
    if treestate.height != u64::from(anchor_height) || !network_ok {
        return Err(RuntimeError::with(
            ErrorCode::InvalidBlockHeight,
            json!({
                "stage": "birthdayTreeState",
                "requestedHeight": anchor_height,
                "returnedHeight": treestate.height,
                "returnedNetwork": treestate.network,
            }),
        ));
    }
    Ok(())
}

#[cfg(test)]
mod import_error_tests {
    use super::*;

    #[test]
    fn diverged_checkpoints_have_a_structured_reason_and_keep_the_detail() {
        for pool in ["Sapling", "Orchard", "Ironwood"] {
            let error = SqliteClientError::CorruptedData(format!(
                "the {pool} note commitment tree retains checkpoints both above and below \
                 height 4135105, but none at that height to truncate to"
            ));
            let detail = error.to_string();
            assert_eq!(
                import_error(error).to_json(),
                json!({
                    "code": "DATABASE_ERROR",
                    "params": { "operation": "importAccount", "reason": "checkpointsDiverged", "checkpointHeight": 4135105 },
                    "detail": detail,
                })
            );
        }
    }

    #[test]
    fn a_later_birthday_waits_for_a_pending_send() {
        assert!(refuse_restart_with_pending_send(false).is_ok());
        let error = refuse_restart_with_pending_send(true).unwrap_err();
        assert_eq!(error.to_json()["code"], "WALLET_BUSY");
        assert_eq!(error.params["reason"], "pendingSend");
    }

    #[test]
    fn other_import_errors_keep_their_params_and_detail() {
        let prefix = "the Orchard note commitment tree retains checkpoints both above and below height 4135105";
        for error in [
            SqliteClientError::CorruptedData("invalid stored note".into()),
            SqliteClientError::CorruptedData(format!("other failure: {prefix}")),
            SqliteClientError::DbError(rusqlite::Error::InvalidParameterName(prefix.into())),
            SqliteClientError::RequestedRewindInvalid {
                safe_rewind_height: None,
                requested_height: 4_135_029_u32.into(),
            },
        ] {
            let detail = error.to_string();
            assert_eq!(
                import_error(error).to_json(),
                json!({
                    "code": "DATABASE_ERROR",
                    "params": { "operation": "importAccount" },
                    "detail": detail,
                })
            );
        }
    }
}

#[cfg(test)]
mod birthday_tests {
    use super::check_birthday_tree_state;
    use zcash_client_backend::proto::service::TreeState;
    use zcash_protocol::consensus::Network;

    fn state(network: &str, height: u64) -> TreeState {
        TreeState {
            network: network.to_owned(),
            height,
            ..Default::default()
        }
    }

    #[test]
    fn the_birthday_tree_state_must_be_the_requested_block_on_our_network() {
        let main = Network::MainNetwork;
        check_birthday_tree_state(&state("main", 3_000_000), 3_000_000, &main).unwrap();
        check_birthday_tree_state(&state("", 3_000_000), 3_000_000, &main).unwrap();
        for bad in [
            state("main", 3_000_001),
            state("main", 2_999_999),
            state("test", 3_000_000),
        ] {
            assert!(check_birthday_tree_state(&bad, 3_000_000, &main).is_err());
        }
        check_birthday_tree_state(&state("test", 10), 10, &Network::TestNetwork).unwrap();
    }
}

#[cfg(test)]
mod balance_tests {
    use super::balance_value;
    use zcash_client_backend::data_api::{AccountBalance, Balance};
    use zcash_protocol::value::{BalanceError, Zatoshis};

    fn zat(value: u64) -> Zatoshis {
        Zatoshis::const_from_u64(value)
    }

    #[test]
    fn dust_change_is_reported_beside_total_not_inside_it() {
        let mut balances = AccountBalance::ZERO;
        balances
            .with_ironwood_balance_mut::<_, BalanceError>(|b: &mut Balance| {
                b.add_spendable_value(zat(100_000))?;
                b.add_uneconomic_value(zat(5_000))
            })
            .unwrap();
        balances
            .with_orchard_balance_mut::<_, BalanceError>(|b: &mut Balance| {
                b.add_pending_change_value(zat(20_000))?;
                b.add_uneconomic_value(zat(1_000))
            })
            .unwrap();
        // Sapling is out of the product, dust included.
        balances
            .with_sapling_balance_mut::<_, BalanceError>(|b: &mut Balance| {
                b.add_uneconomic_value(zat(3_000))
            })
            .unwrap();

        let v = balance_value(10, 9, &balances);

        assert_eq!(v["ironwood"]["spendable"], 100_000);
        assert_eq!(v["ironwood"]["total"], 100_000);
        assert_eq!(v["ironwood"]["uneconomic"], 5_000);
        assert_eq!(v["orchard"]["pendingChange"], 20_000);
        assert_eq!(v["orchard"]["total"], 20_000);
        assert_eq!(v["orchard"]["uneconomic"], 1_000);
        assert_eq!(v["transparentRegular"]["uneconomic"], 0);
        assert_eq!(v["total"], 120_000);
        assert_eq!(v["uneconomic"], 6_000);
    }

    #[test]
    fn no_dust_reports_zero() {
        let v = balance_value(1, 1, &AccountBalance::ZERO);
        for pool in [
            "orchard",
            "ironwood",
            "transparentRegular",
            "transparentCoinbase",
        ] {
            assert_eq!(v[pool]["uneconomic"], 0);
        }
        assert_eq!(v["uneconomic"], 0);
    }

    #[test]
    fn small_change_preserves_value_after_payment_and_fee() {
        let payment = 10_000;
        let fee = 10_000;
        for change in [1, 4_999, 5_000] {
            let mut before = AccountBalance::ZERO;
            before
                .with_ironwood_balance_mut::<_, BalanceError>(|balance| {
                    balance.add_spendable_value(zat(payment + fee + change))
                })
                .unwrap();
            let mut after = AccountBalance::ZERO;
            after
                .with_ironwood_balance_mut::<_, BalanceError>(|balance| {
                    balance.add_uneconomic_value(zat(change))
                })
                .unwrap();

            let before = balance_value(10, 10, &before);
            let after = balance_value(11, 11, &after);
            assert_eq!(
                before["total"].as_u64().unwrap(),
                payment
                    + fee
                    + after["total"].as_u64().unwrap()
                    + after["uneconomic"].as_u64().unwrap()
            );
            assert_eq!(after["ironwood"]["spendable"], 0);
            assert_eq!(after["ironwood"]["uneconomic"], change);
        }
    }
}

#[cfg(test)]
mod derivation_tests {
    use super::derivation_pair;
    use crate::ErrorCode;

    #[test]
    fn half_a_derivation_pair_is_refused() {
        assert_eq!(derivation_pair(None, None).unwrap(), None);
        assert_eq!(
            derivation_pair(Some("ab"), Some(0)).unwrap(),
            Some(("ab", 0))
        );
        for (fingerprint, index) in [(Some("ab"), None), (None, Some(0))] {
            let err = derivation_pair(fingerprint, index).unwrap_err();
            assert_eq!(err.code, ErrorCode::InvalidAccountId);
            assert_eq!(err.params["reason"], "incomplete");
        }
    }
}

#[cfg(test)]
mod remove_account_tests {
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use zcash_client_sqlite::wallet::init::WalletMigrator;
    use zcash_client_sqlite::WalletDb;
    use zcash_keys::keys::UnifiedSpendingKey;
    use zcash_primitives::block::BlockHash;
    use zcash_protocol::consensus::{BlockHeight, Network};

    use super::*;
    use crate::clock::HostClock;

    const NET: Network = Network::MainNetwork;
    const TIP: u32 = 3_525_600;

    fn wallet() -> Db {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        rusqlite::vtab::array::load_module(&conn).unwrap();
        let mut db = WalletDb::from_connection(conn, NET, HostClock, ChaCha20Rng::from_seed([7; 32]));
        WalletMigrator::new()
            .with_external_migrations(crate::tx_state::migrations())
            .init_or_migrate(&mut db)
            .unwrap();
        db
    }

    fn import(db: &mut Db, name: &str, index: u32, birthday: u32) -> String {
        let ufvk = UnifiedSpendingKey::from_seed(&NET, &[9; 32], index.try_into().unwrap())
            .unwrap()
            .to_unified_full_viewing_key();
        let birthday = AccountBirthday::from_parts(
            ChainState::empty(BlockHeight::from_u32(birthday), BlockHash([0; 32])),
            None,
        );
        db.import_account_ufvk(name, &ufvk, &birthday, AccountPurpose::ViewOnly, None)
            .unwrap()
            .id()
            .expose_uuid()
            .to_string()
    }

    fn lowest_queued(db: &Db) -> u32 {
        db.suggest_scan_ranges()
            .unwrap()
            .iter()
            .map(|range| u32::from(range.block_range().start))
            .min()
            .unwrap()
    }

    #[test]
    fn removing_the_earliest_account_stops_scanning_below_the_rest() {
        let mut db = wallet();
        let early = import(&mut db, "fp-early", 0, 3_500_000);
        import(&mut db, "fp-late", 1, 3_520_000);
        db.update_chain_tip(BlockHeight::from_u32(TIP)).unwrap();
        assert!(lowest_queued(&db) < 3_520_000);

        remove_account(&mut db, &early).unwrap();

        assert!(lowest_queued(&db) >= 3_520_000);
    }

    #[test]
    fn finds_an_account_by_the_name_it_was_imported_under() {
        let mut db = wallet();
        let uuid = import(&mut db, "fp-a", 0, 3_520_000);
        import(&mut db, "fp-b", 1, 3_520_000);

        assert_eq!(
            account_by_name(&db, "fp-a").unwrap(),
            json!({ "uuid": uuid }).to_string()
        );
        assert_eq!(account_by_name(&db, "fp-missing").unwrap(), "null");
    }

    // Deleting goes by this name, so a name held twice must not pick one.
    #[test]
    fn refuses_a_name_two_accounts_share() {
        let mut db = wallet();
        import(&mut db, "fp-same", 0, 3_520_000);
        import(&mut db, "fp-same", 1, 3_520_000);

        let err = account_by_name(&db, "fp-same").unwrap_err();
        assert_eq!(err.code, ErrorCode::InvalidAccountId);
        assert_eq!(err.params["reason"], "duplicateName");
    }

    #[test]
    fn moving_the_birthday_later_stops_scanning_below_it() {
        let mut db = wallet();
        let uuid = import(&mut db, "fp-a", 0, 3_500_000);
        db.update_chain_tip(BlockHeight::from_u32(TIP)).unwrap();
        assert!(lowest_queued(&db) < 3_520_000);

        let id = parse_account_id(&db, &uuid).unwrap();
        let later = AccountBirthday::from_parts(
            ChainState::empty(BlockHeight::from_u32(3_520_000), BlockHash([0; 32])),
            None,
        );
        restart_with_birthday(&mut db, id, &later, None).unwrap();

        assert!(lowest_queued(&db) >= 3_520_000);
    }
}
