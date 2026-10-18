//! Account import and reads: UFVK import and balances. No private keys,
//! address derivation or signing on this path.

use zcash_client_backend::data_api::wallet::ConfirmationsPolicy;
use zcash_client_backend::data_api::{
    Account as _, AccountBirthday, AccountPurpose, WalletRead, WalletWrite, Zip32Derivation,
};
use zcash_keys::keys::UnifiedFullViewingKey;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::{self, LightClient};
use crate::wallet::Db;
use serde_json::json;

/// Imports an account from a UFVK.
///
/// `birthday_height` is the earliest height the account can have received a
/// note; scanning starts there. Fetches the tree state of the block before it.
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

    // With a seed fingerprint and account index the account is imported as
    // spending: upstream then puts zip32_derivation on PCZT spends, and
    // send::mark_wallet_outputs puts it on change outputs (upstream does not),
    // so signers can find the key and verify change returns to the wallet.
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

    let account = db
        .import_account_ufvk(account_name, &ufvk, &birthday, purpose, None)
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "importAccount" }),
            )
            .detail(e)
        })?;

    Ok(account.id().expose_uuid().to_string())
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

/// Account balance as JSON.
pub fn balance_json(db: &Db, account_uuid: &str, policy: ConfirmationsPolicy) -> Result<String> {
    let account_id = parse_account_id(db, account_uuid)?;

    let summary = db.get_wallet_summary(policy).map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "walletSummary" }),
        )
        .detail(e)
    })?;

    // No summary before any block is scanned; reported as an error code, with
    // user-facing text left to the host.
    let Some(summary) = summary else {
        return Err(RuntimeError::new(ErrorCode::NotSynced));
    };

    let balances = summary.account_balances().get(&account_id).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::AccountNotFound,
            json!({ "accountId": account_uuid }),
        )
    })?;

    // Each pool reports all five values with no cross-pool merging: which pools
    // count as spendable is wallet policy, not chain fact, and per-pool detail is
    // shown in the UI. The host computes any totals it needs.
    let pool = |b: &zcash_client_backend::data_api::Balance| {
        serde_json::json!({
            "spendable": u64::from(b.spendable_value()),
            "pendingChange": u64::from(b.change_pending_confirmation()),
            "pendingSpendable": u64::from(b.value_pending_spendability()),
            // Held by an unexpired upstream output lock; this runtime's sends take none.
            "locked": u64::from(b.locked_value()),
            "total": u64::from(b.total()),
        })
    };

    let supported_total = [
        balances.orchard_balance(),
        balances.ironwood_balance(),
        balances.unshielded_regular_balance(),
        balances.unshielded_coinbase_balance(),
    ]
    .into_iter()
    .map(|balance| u64::from(balance.total()))
    .sum::<u64>();

    Ok(serde_json::json!({
        "ready": true,
        "chainTip": u32::from(summary.chain_tip_height()),
        "fullyScannedHeight": u32::from(summary.fully_scanned_height()),

        "orchard": pool(balances.orchard_balance()),
        "ironwood": pool(balances.ironwood_balance()),

        // Coinbase is reported separately: it has its own maturity rules and can
        // only be shielded via `propose_shielding_coinbase`.
        "transparentRegular": pool(balances.unshielded_regular_balance()),
        "transparentCoinbase": pool(balances.unshielded_coinbase_balance()),

        // Sapling is intentionally excluded from both the pool list and the product total.
        "total": supported_total,
    })
    .to_string())
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
        crate::tx_state::prune_orphaned_rows(ext)
    })
}

/// Finds the account with this UFVK, or returns `"null"`.
///
/// This is how to locate an account in the shared database: it holds every
/// wallet's accounts, and runtime UUIDs change after a purge. The UFVK is the
/// host's stable unique key.
pub fn account_by_ufvk(db: &Db, ufvk_str: &str) -> Result<String> {
    let params = *db.params();
    let target = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?
        .encode(&params);

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
        if acct.ufvk().map(|k| k.encode(&params)).as_deref() == Some(target.as_str()) {
            return Ok(json!({ "uuid": acct.id().expose_uuid().to_string() }).to_string());
        }
    }
    Ok("null".to_string())
}

/// Local account UUIDs as a JSON array.
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
