//! Paused accounts stay in the shared database, but their history no longer sets where
//! scanning starts or which notes count as spendable. The patched `zcash_client_sqlite`
//! derives that floor from the accounts not listed in [`TABLE`].

use std::collections::BTreeSet;

use serde_json::json;
use zcash_client_backend::data_api::{scanning::ScanRange, WalletRead};
use zcash_client_sqlite::{AccountUuid, ExtensionTransaction};
use zcash_protocol::consensus::BlockHeight;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::wallet::Db;

/// Created by the patched upstream migrator, before its migrations run.
pub(crate) const TABLE: &str = "ext_onekey_paused_accounts";

fn db_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(e)
}

fn hex(uuid: &AccountUuid) -> String {
    uuid.expose_uuid()
        .as_bytes()
        .iter()
        .map(|b| format!("{b:02X}"))
        .collect()
}

/// Pauses every account outside `active`. Writes only when the set changes.
pub fn pause_inactive_accounts(db: &mut Db, active: &[AccountUuid]) -> Result<()> {
    let paused: Vec<AccountUuid> = db
        .get_account_ids()
        .map_err(|e| db_err("getAccountIds", e))?
        .into_iter()
        .filter(|id| !active.contains(id))
        .collect();
    let wanted: BTreeSet<String> = paused.iter().map(hex).collect();
    db.transactionally_with_extension(|_, ext| {
        let current: BTreeSet<String> = ext
            .query_row(
                &format!("SELECT IFNULL(group_concat(hex(account_uuid)), '') FROM {TABLE}"),
                [],
                |row| row.get::<_, String>(0),
            )
            .map_err(|e| db_err("readPausedAccounts", e))?
            .split(',')
            .filter(|id| !id.is_empty())
            .map(str::to_owned)
            .collect();
        if current == wanted {
            return Ok(());
        }
        ext.execute(&format!("DELETE FROM {TABLE}"), [])
            .map_err(|e| db_err("clearPausedAccounts", e))?;
        for id in &paused {
            ext.execute(
                &format!("INSERT INTO {TABLE} (account_uuid) VALUES (?1)"),
                [id.expose_uuid().as_bytes().to_vec()],
            )
            .map_err(|e| db_err("pauseAccount", e))?;
        }
        Ok(())
    })
}

/// The earliest birthday of every account, paused ones included. Pruning the scan queue
/// below anything later would drop a paused account's remaining history.
pub fn earliest_birthday(ext: &ExtensionTransaction<'_>) -> Result<Option<BlockHeight>> {
    ext.query_row("SELECT MIN(birthday_height) FROM accounts", [], |row| {
        row.get::<_, Option<u32>>(0)
    })
    .map(|height| height.map(BlockHeight::from_u32))
    .map_err(|e| db_err("earliestBirthday", e))
}

/// The ranges at or above the scan floor; one crossing it starts at the floor. A paused
/// account's older ranges stay queued until it scans again.
pub fn above_floor(db: &Db, ranges: Vec<ScanRange>) -> Result<Vec<ScanRange>> {
    let Some(floor) = db
        .get_wallet_birthday()
        .map_err(|e| db_err("walletBirthday", e))?
    else {
        return Ok(ranges);
    };
    Ok(ranges
        .into_iter()
        .filter_map(|range| {
            let end = range.block_range().end;
            if end <= floor {
                return None;
            }
            let start = range.block_range().start.max(floor);
            Some(ScanRange::from_parts(start..end, range.priority()))
        })
        .collect())
}
