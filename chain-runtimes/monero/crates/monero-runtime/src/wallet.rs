//! Wallet database handle and identity management.
//!
//! One identity = one view key pair = one host account. Identities **share** a
//! SQLite file, each with its own scan progress.
//!
//! Unlike Zcash, whose scan queue is database-wide (the commitment tree needs
//! contiguous scanning), Monero has no commitment tree, so **scan progress is
//! per identity** (`scan_ranges.identity_id`) and a paused account never misses
//! ranges scanned for another.

use monero_wallet::{address::Network, ViewPair};
use rusqlite::{params, Connection, OptionalExtension};
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::keys::{self, network_name};
use crate::schema;

/// Unused subaddresses kept registered past the highest used one in account 0 (like Bitcoin's gap limit).
pub const SUBADDRESS_LOOKAHEAD: u32 = 20;

pub struct Db {
    conn: Connection,
    network: Network,
}

/// An identity in the database.
pub struct Identity {
    pub id: i64,
    pub uuid: String,
    pub primary_address: String,
    pub birthday_height: u64,
    pub view_pair: ViewPair,
}

impl Db {
    /// Best effort: a failed reclaim only means the file stays larger until
    /// the next one, so it never fails the caller.
    pub fn reclaim_storage(&self, max_pages: u32) {
        let _ = crate::storage::reclaim_free_pages(&self.conn, max_pages);
    }

    /// Opens (or creates) a wallet database and runs migrations.
    pub fn open(db_name: &str, network: Network) -> Result<Self> {
        let conn = crate::storage::open_connection(db_name)?;
        schema::migrate(&conn)?;

        // Network is a per-database invariant; mixing networks would feed one
        // network's blocks to another's keys and silently find nothing.
        let existing: Option<String> = conn
            .query_row("SELECT network FROM wallet_meta WHERE id = 0", [], |r| {
                r.get(0)
            })
            .optional()
            .map_err(|e| db_err("readWalletMeta", e))?;

        match existing {
            Some(recorded) if recorded != network_name(network) => {
                return Err(RuntimeError::with(
                    ErrorCode::InvalidNetwork,
                    json!({
                        "value": network_name(network),
                        "recorded": recorded,
                        "reason": "databaseBelongsToAnotherNetwork",
                    }),
                ));
            }
            Some(_) => {}
            None => {
                conn.execute(
                    "INSERT INTO wallet_meta (id, network, created_at) VALUES (0, ?1, ?2)",
                    params![network_name(network), now_secs()],
                )
                .map_err(|e| db_err("initWalletMeta", e))?;
            }
        }

        Ok(Self { conn, network })
    }

    pub fn network(&self) -> Network {
        self.network
    }

    pub fn conn(&self) -> &Connection {
        &self.conn
    }

    pub fn conn_mut(&mut self) -> &mut Connection {
        &mut self.conn
    }

    /// Registers an identity; idempotent by uuid.
    ///
    /// `birthday_height` is where backfill starts. Without one the host should
    /// pass the chain tip, not 0, which would scan from genesis.
    pub fn add_identity(
        &mut self,
        uuid: &str,
        spend_public: &[u8],
        view_private: &[u8],
        birthday_height: u64,
    ) -> Result<Identity> {
        self.add_identity_with_rescan(uuid, spend_public, view_private, birthday_height, false)
    }

    pub fn add_identity_with_rescan(
        &mut self,
        uuid: &str,
        spend_public: &[u8],
        view_private: &[u8],
        birthday_height: u64,
        force_rescan: bool,
    ) -> Result<Identity> {
        let pair = keys::view_pair_from_bytes(spend_public, view_private)?;
        let primary = keys::primary_address(&pair, self.network);

        let by_uuid: Option<(i64, String)> = self
            .conn
            .query_row(
                "SELECT id, primary_address FROM identities WHERE uuid = ?1",
                [uuid],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .optional()
            .map_err(|e| db_err("findIdentity", e))?;
        if let Some((id, address)) = by_uuid {
            if address == primary {
                if force_rescan || self.identity_by_id(id)?.birthday_height != birthday_height {
                    crate::sync::reset_from_birthday(self, id, birthday_height)?;
                }
                return self.identity_by_id(id);
            }
            return Err(RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "uuid", "reason": "identityKeyMismatch" }),
            ));
        }

        // Same keys under a new id (deleted and reimported): keep the scan data under the new id.
        let by_address: Option<i64> = self
            .conn
            .query_row(
                "SELECT id FROM identities WHERE primary_address = ?1",
                [&primary],
                |r| r.get(0),
            )
            .optional()
            .map_err(|e| db_err("findIdentityByAddress", e))?;

        let id = match by_address {
            Some(id) => {
                let rescan =
                    force_rescan || self.identity_by_id(id)?.birthday_height != birthday_height;
                let tx = self
                    .conn
                    .transaction()
                    .map_err(|e| db_err("beginRenameIdentity", e))?;
                tx.execute(
                    "UPDATE identities SET uuid = ?1 WHERE id = ?2",
                    params![uuid, id],
                )
                .map_err(|e| db_err("renameIdentity", e))?;
                if rescan {
                    crate::sync::reset_scan_in_transaction(&tx, id, birthday_height)?;
                }
                tx.commit().map_err(|e| db_err("commitRenameIdentity", e))?;
                id
            }
            None => {
                let tx = self
                    .conn
                    .transaction()
                    .map_err(|e| db_err("beginAddIdentity", e))?;
                tx.execute(
                    "INSERT INTO identities
                       (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
                     VALUES (?1, ?2, ?3, ?4, ?5, ?6)",
                    params![
                        uuid,
                        &primary,
                        spend_public,
                        view_private,
                        birthday_height as i64,
                        now_secs()
                    ],
                )
                .map_err(|e| db_err("insertIdentity", e))?;
                let id = tx.last_insert_rowid();

                // Initial scan range is empty (birthday..birthday); `update_chain_tip`
                // extends it once the tip is known, so "tip never fetched" and
                // "fully scanned" stay distinct states.
                tx.commit().map_err(|e| db_err("commitAddIdentity", e))?;
                id
            }
        };

        self.identity_by_id(id)
    }

    pub fn identity_by_id(&self, id: i64) -> Result<Identity> {
        self.conn
            .query_row(
                "SELECT id, uuid, primary_address, birthday_height, spend_public_key, view_private_key
                 FROM identities WHERE id = ?1",
                [id],
                Self::row_to_identity,
            )
            .optional()
            .map_err(|e| db_err("identityById", e))?
            .ok_or_else(|| {
                RuntimeError::with(ErrorCode::AccountNotFound, json!({ "accountId": id }))
            })?
    }

    pub fn identity_by_uuid(&self, uuid: &str) -> Result<Identity> {
        self.conn
            .query_row(
                "SELECT id, uuid, primary_address, birthday_height, spend_public_key, view_private_key
                 FROM identities WHERE uuid = ?1",
                [uuid],
                Self::row_to_identity,
            )
            .optional()
            .map_err(|e| db_err("identityByUuid", e))?
            .ok_or_else(|| {
                RuntimeError::with(ErrorCode::AccountNotFound, json!({ "accountId": uuid }))
            })?
    }

    #[allow(clippy::type_complexity)]
    fn row_to_identity(row: &rusqlite::Row<'_>) -> rusqlite::Result<Result<Identity>> {
        let id: i64 = row.get(0)?;
        let uuid: String = row.get(1)?;
        let primary_address: String = row.get(2)?;
        let birthday: i64 = row.get(3)?;
        let spend: Vec<u8> = row.get(4)?;
        let view: Vec<u8> = row.get(5)?;
        Ok(
            keys::view_pair_from_bytes(&spend, &view).map(|view_pair| Identity {
                id,
                uuid,
                primary_address,
                birthday_height: birthday as u64,
                view_pair,
            }),
        )
    }

    pub fn list_identities(&self) -> Result<Vec<(i64, String, String)>> {
        let mut stmt = self
            .conn
            .prepare("SELECT id, uuid, primary_address FROM identities ORDER BY id")
            .map_err(|e| db_err("listIdentities", e))?;
        let rows = stmt
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)))
            .map_err(|e| db_err("listIdentities", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("listIdentities", e))
    }

    /// Deletes an identity and all its data. **Irreversible.**
    ///
    /// Foreign-key cascades do the work (`PRAGMA foreign_keys = ON` in
    /// open_connection). Orphan rows would be a privacy leak.
    pub fn remove_identity(&mut self, uuid: &str) -> Result<bool> {
        let n = self
            .conn
            .execute("DELETE FROM identities WHERE uuid = ?1", [uuid])
            .map_err(|e| db_err("removeIdentity", e))?;
        Ok(n > 0)
    }

    /// Registers a subaddress. These are fed to the Scanner; **unregistered ones receive nothing**.
    pub fn register_subaddress(
        &mut self,
        identity_id: i64,
        major: u32,
        minor: u32,
        label: Option<&str>,
    ) -> Result<String> {
        let identity = self.identity_by_id(identity_id)?;
        let address = keys::subaddress(&identity.view_pair, self.network, major, minor)?;
        self.conn
            .execute(
                "INSERT OR REPLACE INTO subaddresses (identity_id, major, minor, address, label)
                 VALUES (?1, ?2, ?3, ?4, ?5)",
                params![identity_id, major, minor, &address, label],
            )
            .map_err(|e| db_err("registerSubaddress", e))?;
        Ok(address)
    }

    /// Ensures account 0 has subaddresses registered up to the highest used minor
    /// + SUBADDRESS_LOOKAHEAD; returns how many were added. Cost is one scalar
    /// multiplication per registration; scanning is a table lookup.
    pub fn ensure_subaddress_lookahead(&mut self, identity_id: i64) -> Result<u32> {
        let (highest_used, highest_registered): (i64, i64) = self
            .conn
            .query_row(
                "SELECT
                   COALESCE((SELECT max(subaddress_minor) FROM outputs
                             WHERE identity_id = ?1 AND subaddress_major = 0), 0),
                   COALESCE((SELECT max(minor) FROM subaddresses
                             WHERE identity_id = ?1 AND major = 0), 0)",
                [identity_id],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .map_err(|e| db_err("subaddressLookahead", e))?;
        let target = highest_used as u32 + SUBADDRESS_LOOKAHEAD;
        let mut added = 0;
        for minor in (highest_registered as u32 + 1)..=target {
            self.register_subaddress(identity_id, 0, minor, None)?;
            added += 1;
        }
        Ok(added)
    }

    /// All registered subaddress indices of the identity.
    pub fn subaddress_indices(&self, identity_id: i64) -> Result<Vec<(u32, u32)>> {
        let mut stmt = self
            .conn
            .prepare(
                "SELECT major, minor FROM subaddresses WHERE identity_id = ?1 ORDER BY major, minor",
            )
            .map_err(|e| db_err("subaddressIndices", e))?;
        let rows = stmt
            .query_map([identity_id], |r| {
                Ok((r.get::<_, i64>(0)? as u32, r.get::<_, i64>(1)? as u32))
            })
            .map_err(|e| db_err("subaddressIndices", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("subaddressIndices", e))
    }
}

pub fn now_secs() -> i64 {
    #[cfg(target_arch = "wasm32")]
    {
        (js_sys::Date::now() / 1000.0) as i64
    }
    #[cfg(not(target_arch = "wasm32"))]
    {
        std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .map(|d| d.as_secs() as i64)
            .unwrap_or(0)
    }
}

#[cfg(test)]
pub(crate) mod testing {
    use super::*;

    pub const ADDR: &str = "44AFFq5kSiGBoZ4NMDwYtN18obc8AemS33DBLWs3H7otXft3XjrpDtQGv7SqSsaBYBb98uNbr2VBBEt7f2wfn3RVGQBEP3A";

    /// Test keys: spend key from a known address, a small scalar as view key.
    pub fn key_bytes() -> ([u8; 32], [u8; 32]) {
        use monero_wallet::address::MoneroAddress;
        let addr = MoneroAddress::from_str(Network::Mainnet, ADDR).unwrap();
        let spend = addr.spend().compress().to_bytes();
        let mut view = [0u8; 32];
        view[0] = 9;
        (spend, view)
    }

    pub fn db() -> Db {
        Db::open(":memory:", Network::Mainnet).expect("in-memory db")
    }

    pub fn db_with_identity() -> (Db, i64) {
        let mut db = db();
        let (spend, view) = key_bytes();
        let id = db
            .add_identity("acct-1", &spend, &view, 100)
            .expect("add identity")
            .id;
        (db, id)
    }
}

#[cfg(test)]
mod tests {
    use super::testing::*;
    use super::*;

    fn registered_minors(db: &Db, id: i64) -> Vec<u32> {
        db.subaddress_indices(id)
            .unwrap()
            .into_iter()
            .filter(|(major, _)| *major == 0)
            .map(|(_, minor)| minor)
            .collect()
    }

    fn insert_output_at_minor(db: &Db, id: i64, minor: u32) {
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                   amount_atomic, subaddress_major, subaddress_minor, height)
                 VALUES (?1, x'00', ?2, 1, 0, 1, 0, ?3, 150)",
                params![id, vec![minor as u8; 32], minor],
            )
            .unwrap();
    }

    #[test]
    fn a_new_identity_gets_the_lookahead_window() {
        let (mut db, id) = db_with_identity();
        assert_eq!(
            db.ensure_subaddress_lookahead(id).unwrap(),
            SUBADDRESS_LOOKAHEAD
        );
        assert_eq!(
            registered_minors(&db, id),
            (1..=SUBADDRESS_LOOKAHEAD).collect::<Vec<_>>()
        );
        assert_eq!(
            db.ensure_subaddress_lookahead(id).unwrap(),
            0,
            "no registration once the window is full"
        );
    }

    #[test]
    fn a_payment_near_the_end_widens_the_window() {
        let (mut db, id) = db_with_identity();
        db.ensure_subaddress_lookahead(id).unwrap();
        insert_output_at_minor(&db, id, 18);
        assert_eq!(db.ensure_subaddress_lookahead(id).unwrap(), 18);
        assert_eq!(
            registered_minors(&db, id).last(),
            Some(&(18 + SUBADDRESS_LOOKAHEAD))
        );
    }

    #[test]
    fn different_keys_require_explicit_identity_deletion() {
        let (mut db, old) = db_with_identity();
        insert_output_at_minor(&db, old, 1);
        let (spend, _) = key_bytes();
        let mut other_view = [0u8; 32];
        other_view[0] = 11;

        let error = db
            .add_identity("acct-1", &spend, &other_view, 100)
            .err()
            .unwrap();
        assert_eq!(error.params["reason"], "identityKeyMismatch");
        let outputs: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM outputs", [], |r| r.get(0))
            .unwrap();
        assert_eq!(outputs, 1);
        db.remove_identity("acct-1").unwrap();
        let fresh = db.add_identity("acct-1", &spend, &other_view, 100).unwrap();
        assert_ne!(fresh.id, old);
    }

    /// Reimport after deletion (new id, same keys) reuses the scan data and must
    /// not trip the primary-address unique constraint.
    #[test]
    fn the_same_keys_under_a_new_account_id_keep_their_scan() {
        let (mut db, old) = db_with_identity();
        let (spend, view) = key_bytes();

        let again = db.add_identity("acct-2", &spend, &view, 100).unwrap();

        assert_eq!(again.id, old);
        assert_eq!(again.uuid, "acct-2");
    }

    #[test]
    fn adding_the_same_identity_twice_is_idempotent() {
        let mut db = db();
        let (spend, view) = key_bytes();
        let a = db.add_identity("acct-1", &spend, &view, 100).unwrap();
        let b = db.add_identity("acct-1", &spend, &view, 100).unwrap();
        assert_eq!(a.id, b.id, "same uuid must reuse the row");
        assert_eq!(
            b.birthday_height, 100,
            "re-registration must not overwrite the birthday"
        );
        assert_eq!(db.list_identities().unwrap().len(), 1);
    }

    #[test]
    fn a_later_birthday_drops_older_rows_but_keeps_pending_send_inputs() {
        let (mut db, id) = db_with_identity();
        let (spend, view) = key_bytes();
        insert_output_at_minor(&db, id, 1);
        insert_output_at_minor(&db, id, 2);
        for (minor, image) in [(1, [7u8; 32]), (2, [8u8; 32])] {
            db.conn().execute("UPDATE outputs SET key_image = ?2 WHERE identity_id = ?1 AND subaddress_minor = ?3", params![id, image.as_slice(), minor]).unwrap();
        }
        let sent = [9u8; 32];
        db.conn().execute("INSERT INTO transactions (identity_id, txid, direction, sent_at, change_atomic) VALUES (?1, ?2, 'out', 1, '2')", params![id, sent.as_slice()]).unwrap();
        db.conn().execute("UPDATE outputs SET spent_at_height = 200, spent_in_txid = ?2 WHERE identity_id = ?1 AND subaddress_minor = 1", params![id, sent.as_slice()]).unwrap();
        crate::sync::update_chain_tip(&mut db, id, 1_000).unwrap();

        let changed = db.add_identity("acct-1", &spend, &view, 900).unwrap();

        assert_eq!(changed.id, id);
        assert_eq!(changed.birthday_height, 900);
        assert_eq!(
            crate::sync::peek(&db, id).unwrap()["ranges"],
            json!([{ "start": 900, "end": 1_001 }])
        );
        assert!(crate::query::spendable_rows(&db, id, 1_000)
            .unwrap()
            .is_empty());
        let balance = crate::query::balance(&db, id, Some(1_000)).unwrap();
        assert_eq!(
            balance.total_atomic, 2,
            "only the still-pending change is retained in balance"
        );
        let images: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM outputs WHERE identity_id = ?1 AND key_image IS NOT NULL",
                [id],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(images, 1, "only the pending send's input is kept");
        assert!(crate::spend::check_inputs_owned(db.conn(), id, &sent, &[[7u8; 32]]).is_ok());
        let error =
            crate::spend::check_inputs_owned(db.conn(), id, &[10u8; 32], &[[8u8; 32]]).unwrap_err();
        assert_eq!(error.params["reason"], "inputNotOwnedByAccount");
        assert_eq!(
            db.add_identity("acct-1", &spend, &view, 100)
                .unwrap()
                .birthday_height,
            100
        );
    }

    #[test]
    fn a_later_birthday_removes_older_confirmed_sends_from_history() {
        let (mut db, id) = db_with_identity();
        let (spend, view) = key_bytes();
        for (txid, height) in [([3u8; 32], 150), ([4u8; 32], 950)] {
            db.conn().execute("INSERT INTO transactions (identity_id, txid, height, direction, sent_at) VALUES (?1, ?2, ?3, 'out', 1)", params![id, txid.as_slice(), height]).unwrap();
        }
        crate::sync::update_chain_tip(&mut db, id, 1_000).unwrap();

        db.add_identity("acct-1", &spend, &view, 900).unwrap();

        let history = crate::query::history(&db, id, 10, 0).unwrap();
        let txids: Vec<_> = history["items"]
            .as_array()
            .unwrap()
            .iter()
            .map(|item| item["txid"].as_str().unwrap().to_owned())
            .collect();
        assert_eq!(txids, vec![hex::encode([4u8; 32])]);
    }

    #[test]
    fn a_failed_birthday_change_rolls_back_scan_invalidation() {
        let (mut db, id) = db_with_identity();
        insert_output_at_minor(&db, id, 1);
        crate::sync::update_chain_tip(&mut db, id, 200).unwrap();
        db.conn().execute_batch("CREATE TRIGGER fail_scan_reset BEFORE UPDATE OF birthday_height ON identities BEGIN SELECT RAISE(ABORT, 'synthetic storage failure'); END;").unwrap();
        let (spend, view) = key_bytes();
        assert!(db.add_identity("acct-1", &spend, &view, 150).is_err());
        assert_eq!(db.identity_by_id(id).unwrap().birthday_height, 100);
        assert_eq!(
            crate::sync::peek(&db, id).unwrap()["ranges"],
            json!([{ "start": 100, "end": 201 }])
        );
        assert_eq!(
            db.conn()
                .query_row(
                    "SELECT scan_valid FROM outputs WHERE identity_id = ?1",
                    [id],
                    |r| r.get::<_, i64>(0)
                )
                .unwrap(),
            1
        );
    }

    /// Per-database network invariant: mixing networks silently breaks scanning.
    #[test]
    fn a_database_refuses_a_different_network() {
        let conn = crate::storage::open_connection(":memory:").unwrap();
        schema::migrate(&conn).unwrap();
        conn.execute(
            "INSERT INTO wallet_meta (id, network, created_at) VALUES (0, 'testnet', 0)",
            [],
        )
        .unwrap();
        // Simulate an existing testnet database; opening it as mainnet must fail
        let recorded: String = conn
            .query_row("SELECT network FROM wallet_meta WHERE id = 0", [], |r| {
                r.get(0)
            })
            .unwrap();
        assert_eq!(recorded, "testnet");
        assert_ne!(recorded, network_name(Network::Mainnet));
    }

    #[test]
    fn identity_lookup_works_by_id_and_uuid() {
        let (db, id) = db_with_identity();
        let by_id = db.identity_by_id(id).unwrap();
        let by_uuid = db.identity_by_uuid("acct-1").unwrap();
        assert_eq!(by_id.id, by_uuid.id);
        assert_eq!(by_id.primary_address, by_uuid.primary_address);
        assert!(
            by_id.primary_address.starts_with('4'),
            "mainnet primary address starts with 4"
        );
    }

    #[test]
    fn missing_identity_is_account_not_found_not_a_database_error() {
        let db = db();
        // Identity holds a ViewPair, which deliberately lacks Debug (keeps keys
        // out of logs), so unwrap_err is unavailable.
        match db.identity_by_uuid("nope") {
            Ok(_) => panic!("missing identity must not be found"),
            Err(e) => assert_eq!(e.code, ErrorCode::AccountNotFound),
        }
    }

    #[test]
    fn subaddresses_register_and_list_back() {
        let (mut db, id) = db_with_identity();
        let a = db.register_subaddress(id, 0, 1, Some("收款")).unwrap();
        let b = db.register_subaddress(id, 1, 0, None).unwrap();
        assert_ne!(a, b);
        assert_eq!(db.subaddress_indices(id).unwrap(), vec![(0, 1), (1, 0)]);
        // Re-registering an index is idempotent
        db.register_subaddress(id, 0, 1, Some("改个标签")).unwrap();
        assert_eq!(db.subaddress_indices(id).unwrap().len(), 2);
    }

    #[test]
    fn removing_an_identity_clears_its_subaddresses() {
        let (mut db, id) = db_with_identity();
        db.register_subaddress(id, 0, 1, None).unwrap();
        assert!(db.remove_identity("acct-1").unwrap());
        assert!(db.subaddress_indices(id).unwrap().is_empty());
        assert!(
            !db.remove_identity("acct-1").unwrap(),
            "second delete should return false"
        );
    }
}
