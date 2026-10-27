//! Wallet database schema and migrations.
//!
//! Monero has no pools and (before FCMP++) no commitment tree, so no witnesses
//! or checkpoints. Writes are incremental and transactional.

use rusqlite::Connection;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};

/// Current schema version. Once released, any schema change must add a migration
/// and bump this; before that, V1 is edited in place.
pub const SCHEMA_VERSION: i64 = 1;

/// Reorg safety window: `blocks` keeps hashes only for this many blocks below the tip (about a day).
pub const REORG_WINDOW_BLOCKS: u64 = 720;

/// Creates the tables. Idempotent: safe to call on every open.
pub fn migrate(conn: &Connection) -> Result<()> {
    let version: i64 = conn
        .query_row("PRAGMA user_version", [], |r| r.get(0))
        .map_err(|e| db_err("readUserVersion", e))?;

    if version > SCHEMA_VERSION {
        return Err(RuntimeError::with(
            ErrorCode::DatabaseTooNew,
            serde_json::json!({ "version": version, "supportedVersion": SCHEMA_VERSION }),
        ));
    }

    // One transaction for the whole migration: a partial migration with an
    // unchanged version would re-run ALTERs on the next open and never succeed.
    let tx = conn
        .unchecked_transaction()
        .map_err(|e| db_err("beginMigrate", e))?;
    if version < 1 {
        tx.execute_batch(V1).map_err(|e| db_err("migrateV1", e))?;
    }

    tx.execute(&format!("PRAGMA user_version = {SCHEMA_VERSION}"), [])
        .map_err(|e| db_err("setUserVersion", e))?;
    tx.commit().map_err(|e| db_err("commitMigrate", e))
}

/// Version 1.
///
/// Amounts are `*_atomic` in piconero. Total supply (1.8e19) exceeds i64, but a
/// single amount does not; an overflow fails on write rather than truncating
/// (see the CHECK on `outputs.amount_atomic`).
const V1: &str = r#"
PRAGMA foreign_keys = ON;

-- Wallet metadata. Single row, id is always 0.
CREATE TABLE IF NOT EXISTS wallet_meta (
    id              INTEGER PRIMARY KEY CHECK (id = 0),
    network         TEXT    NOT NULL,
    created_at      INTEGER NOT NULL
);

-- One identity = one view key pair = one host account.
--
-- Stores view material only (spend public key + private view key, like a
-- Zcash UFVK). The private spend key never reaches the database or this crate.
CREATE TABLE IF NOT EXISTS identities (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    -- Stable host-provided identifier across sessions
    uuid                TEXT    NOT NULL UNIQUE,
    -- Primary (standard, non-sub) address; also the human-readable identity
    primary_address     TEXT    NOT NULL UNIQUE,
    spend_public_key    BLOB    NOT NULL CHECK (length(spend_public_key) = 32),
    view_private_key    BLOB    NOT NULL CHECK (length(view_private_key) = 32),
    -- Height to start scanning from; provided by the host on import.
    birthday_height     INTEGER NOT NULL CHECK (birthday_height >= 0),
    -- Highest height (inclusive) contiguously scanned for this identity. NULL = none.
    --
    -- Must be per identity: deriving it from another identity's progress would
    -- let a caught-up identity skip a backfilling identity's history for good.
    scanned_to_height   INTEGER,
    created_at          INTEGER NOT NULL
);

-- Subaddress registry. All entries are loaded into the monero-oxide Scanner,
-- which only recognises registered subaddresses.
--
-- (0, 0) is the primary address and is excluded (upstream
-- SubaddressIndex::new returns None for it).
CREATE TABLE IF NOT EXISTS subaddresses (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    major           INTEGER NOT NULL CHECK (major >= 0),
    minor           INTEGER NOT NULL CHECK (minor >= 0),
    address         TEXT    NOT NULL,
    label           TEXT,
    PRIMARY KEY (identity_id, major, minor),
    CHECK (major != 0 OR minor != 0)
);
CREATE INDEX IF NOT EXISTS idx_subaddresses_address ON subaddresses(address);

-- Scanned block hashes and RingCT output cursors, per identity.
--
-- 1. Reorg detection: a new block whose prev_hash does not match is a reorg.
-- 2. RingCT index check: upstream `ScannableBlock` warns that
--    `output_index_for_first_ringct_output` cannot be verified and the client
--    must track it itself; a mismatch means the node is serving a false view.
--    `ringct_next` is the global index of the next RingCT output after this block.
--
-- Not shared across identities: one identity's rollback or pruning would
-- remove anchors another identity still needs, hiding a reorg from it.
--
-- Only the last REORG_WINDOW_BLOCKS blocks below the tip are kept.
CREATE TABLE IF NOT EXISTS blocks (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    height          INTEGER NOT NULL,
    hash            BLOB    NOT NULL CHECK (length(hash) = 32),
    -- Global index of the next RingCT output after this block.
    --
    -- NULL when scanning starts mid-chain at a block with no RingCT outputs, so
    -- the cursor is unknown. Storing 0 instead would raise a false
    -- RINGCT_INDEX_MISMATCH against an honest node on the next block.
    ringct_next     INTEGER CHECK (ringct_next IS NULL OR ringct_next >= 0),
    timestamp       INTEGER NOT NULL,
    PRIMARY KEY (identity_id, height)
);

-- Scan progress as ranges rather than one cursor: importing an old wallet
-- yields two disjoint pending ranges (history backfill and tip catch-up).
--
-- With no commitment tree, Monero does not require contiguous scanning
-- (Zcash does, to maintain witnesses).
CREATE TABLE IF NOT EXISTS scan_ranges (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    start_height    INTEGER NOT NULL,
    end_height      INTEGER NOT NULL,
    PRIMARY KEY (identity_id, start_height),
    CHECK (end_height > start_height)
);

-- Received outputs.
--
-- `blob` is monero-oxide `WalletOutput::serialize()` output, read back with
-- `WalletOutput::read()` when spending. The other columns are derived for
-- querying and display only; anything cryptographic must deserialize the blob.
CREATE TABLE IF NOT EXISTS outputs (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    identity_id         INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    -- monero-oxide serialization, read back when spending
    blob                BLOB    NOT NULL,
    -- ── Columns below are derived from blob, for queries only ──
    txid                BLOB    NOT NULL CHECK (length(txid) = 32),
    -- Global output index on chain, used for ring construction
    global_index        INTEGER NOT NULL,
    index_in_tx         INTEGER NOT NULL,
    amount_atomic       INTEGER NOT NULL CHECK (amount_atomic >= 0),
    -- Receiving subaddress; NULL for the primary address
    subaddress_major    INTEGER,
    subaddress_minor    INTEGER,
    height              INTEGER NOT NULL,
    -- Additional timelock (Monero unlock_time), 0 = none
    additional_timelock INTEGER NOT NULL DEFAULT 0,
    -- For spend detection. Requires the spend key, so NULL in view-only wallets.
    key_image           BLOB    CHECK (key_image IS NULL OR length(key_image) = 32),
    -- Height spent at; NULL if unspent
    spent_at_height     INTEGER,
    spent_in_txid       BLOB,
    -- Transaction public keys R, concatenated 32 bytes each (usually one).
    --
    -- monero-oxide folds R into `key_offset` and drops it; software signing
    -- does not need it, but hardware signing does (firmware
    -- `step_02_set_input.py` derives the one-time key itself). Recovering it
    -- later needs a full rescan or a txid query to the node (leaks privacy),
    -- so it is kept at scan time.
    tx_pub_keys             BLOB,
    -- Additional keys for subaddress receives, one per output; NULL if none.
    tx_additional_pub_keys  BLOB,
    -- One-time output public key, for burning-bug checks (only one output per key is spendable)
    output_key          BLOB    CHECK (output_key IS NULL OR length(output_key) = 32),
    -- 0 while a rescan or reorg re-verifies the row; it does not count until seen again
    scan_valid          INTEGER NOT NULL DEFAULT 1 CHECK (scan_valid IN (0, 1)),
    UNIQUE (identity_id, txid, index_in_tx)
);
CREATE INDEX IF NOT EXISTS idx_outputs_unspent
    ON outputs(identity_id, spent_at_height) WHERE spent_at_height IS NULL;
CREATE INDEX IF NOT EXISTS idx_outputs_height ON outputs(identity_id, height);
CREATE INDEX IF NOT EXISTS idx_outputs_key_image ON outputs(key_image) WHERE key_image IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_outputs_global ON outputs(identity_id, global_index);
CREATE INDEX IF NOT EXISTS idx_outputs_output_key ON outputs(identity_id, output_key);

-- Transaction index. Receives come from scanning; spends from key image matches.
CREATE TABLE IF NOT EXISTS transactions (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    txid            BLOB    NOT NULL CHECK (length(txid) = 32),
    height          INTEGER,           -- NULL = in mempool
    timestamp       INTEGER,
    -- 'in' receive / 'out' spend. A tx with both (change) is recorded as 'out'.
    direction       TEXT    NOT NULL CHECK (direction IN ('in', 'out')),
    -- In-flight state of our own broadcasts (as wallet2
    -- `process_unconfirmed_transfer`): one that never reaches the pool is
    -- marked failed and its inputs released, otherwise the funds stay locked.
    sent_at         INTEGER,
    pool_checked_at INTEGER,
    failed          INTEGER NOT NULL DEFAULT 0,
    -- Total paid out (destinations + fee), piconero decimal string. Only known
    -- from the broadcast while pending (change appears once scanned).
    outgoing_atomic TEXT,
    -- Pending change, piconero decimal string. wallet2 counts it in the total
    -- (locked) balance; once scanned it becomes a normal output and is not counted here.
    change_atomic   TEXT,
    -- Network fee, piconero, read from the scanned spending transaction.
    fee_atomic      INTEGER,
    -- Payment ID decrypted with the view key when an output of the tx is scanned (8 bytes, or
    -- 32 for a pre-v12 unencrypted one). NULL when absent or all zeros (wallet2's dummy).
    payment_id      BLOB    CHECK (payment_id IS NULL OR length(payment_id) IN (8, 32)),
    -- 0 while a rescan or reorg re-verifies the row; shown without a height until seen again
    scan_valid      INTEGER NOT NULL DEFAULT 1 CHECK (scan_valid IN (0, 1)),
    PRIMARY KEY (identity_id, txid)
);
CREATE INDEX IF NOT EXISTS idx_transactions_height ON transactions(identity_id, height);

-- Possible spends (as in monero-lws).
--
-- A view-only wallet cannot recognise its own spends, but a spent output must
-- appear in that input's ring. Scanning records inputs whose ring contains one
-- of our outputs, with their key image; once the host computes the real key
-- image (password or hardware), matching reveals the true spend. An output
-- never seen in any ring is unspent. Matching uses global indices only, no keys.
CREATE TABLE IF NOT EXISTS possible_spends (
    identity_id  INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    -- Referenced output: (txid, index_in_tx), same as the outputs unique key
    out_txid     BLOB    NOT NULL CHECK (length(out_txid) = 32),
    out_index    INTEGER NOT NULL,
    -- Key image of the referencing input, and its transaction
    key_image    BLOB    NOT NULL CHECK (length(key_image) = 32),
    spend_txid   BLOB    NOT NULL CHECK (length(spend_txid) = 32),
    height       INTEGER NOT NULL,
    timestamp    INTEGER,
    -- The spending transaction's fee, moved to `transactions` once confirmed
    fee_atomic   INTEGER,
    PRIMARY KEY (identity_id, out_txid, out_index, key_image)
);
CREATE INDEX IF NOT EXISTS idx_possible_spends_height ON possible_spends(identity_id, height);
"#;

#[cfg(test)]
mod tests {
    use super::*;

    fn db() -> Connection {
        let conn = Connection::open_in_memory().expect("in-memory db");
        migrate(&conn).expect("migrate");
        conn
    }

    /// Transaction public keys must round-trip byte for byte: corruption would
    /// yield an unspendable signed transaction without an error.
    #[test]
    fn transaction_public_keys_round_trip() {
        let (db, identity_id) = crate::wallet::testing::db_with_identity();
        let conn = db.conn();

        let r = vec![0xABu8; 32];
        let additional = vec![0xCDu8; 64];
        conn.execute(
            "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                  amount_atomic, height, tx_pub_keys, tx_additional_pub_keys)
             VALUES (?1, x'00', ?2, 1, 0, 1, 1, ?3, ?4)",
            rusqlite::params![identity_id, &vec![1u8; 32], &r, &additional],
        )
        .unwrap();

        let (got_r, got_additional): (Vec<u8>, Option<Vec<u8>>) = conn
            .query_row(
                "SELECT tx_pub_keys, tx_additional_pub_keys FROM outputs",
                [],
                |row| Ok((row.get(0)?, row.get(1)?)),
            )
            .unwrap();
        assert_eq!(got_r, r);
        assert_eq!(got_additional, Some(additional));
    }

    /// A transaction without additional keys (sent to a primary address)
    /// stores NULL, not an empty blob: "zero keys" and "field absent" differ.
    #[test]
    fn an_output_without_additional_keys_stores_null() {
        let (db, identity_id) = crate::wallet::testing::db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, tx_pub_keys)
                 VALUES (?1, x'00', ?2, 1, 0, 1, 1, ?3)",
                rusqlite::params![identity_id, &vec![2u8; 32], &vec![0xABu8; 32]],
            )
            .unwrap();

        let additional: Option<Vec<u8>> = db
            .conn()
            .query_row("SELECT tx_additional_pub_keys FROM outputs", [], |r| {
                r.get(0)
            })
            .unwrap();
        assert_eq!(additional, None);
    }

    /// Only an 8-byte (encrypted) or 32-byte (legacy) payment ID fits.
    #[test]
    fn a_payment_id_must_be_8_or_32_bytes() {
        let (db, identity_id) = crate::wallet::testing::db_with_identity();
        let insert = |txid: u8, payment_id: Vec<u8>| {
            db.conn().execute(
                "INSERT INTO transactions (identity_id, txid, direction, payment_id)
                 VALUES (?1, ?2, 'in', ?3)",
                rusqlite::params![identity_id, vec![txid; 32], payment_id],
            )
        };
        insert(1, vec![1; 8]).expect("encrypted payment ID");
        insert(2, vec![1; 32]).expect("legacy payment ID");
        assert!(insert(3, vec![1; 9]).is_err());
    }

    #[test]
    fn migrate_is_idempotent() {
        let conn = db();
        migrate(&conn).expect("repeated migration must be harmless");
        let v: i64 = conn
            .query_row("PRAGMA user_version", [], |r| r.get(0))
            .unwrap();
        assert_eq!(v, SCHEMA_VERSION);
    }

    #[test]
    fn refuses_to_open_a_newer_database() {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch("PRAGMA user_version = 999").unwrap();
        // Old code could misread data written by a newer version
        assert!(migrate(&conn).is_err());
    }

    #[test]
    fn every_table_exists() {
        let conn = db();
        for t in [
            "wallet_meta",
            "identities",
            "subaddresses",
            "blocks",
            "scan_ranges",
            "outputs",
            "transactions",
            "possible_spends",
        ] {
            let n: i64 = conn
                .query_row(
                    "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?1",
                    [t],
                    |r| r.get(0),
                )
                .unwrap();
            assert_eq!(n, 1, "missing table {t}");
        }
    }

    /// (0,0) is the primary address and must be rejected here too (upstream
    /// `SubaddressIndex::new(0,0)` returns None), or the scanner would never
    /// recognise the entry.
    #[test]
    fn subaddress_zero_zero_is_rejected() {
        let conn = db();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        let bad = conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 0, 'a')",
            [],
        );
        assert!(bad.is_err(), "(0,0) must be rejected by the CHECK");
        conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 1, 'a')",
            [],
        )
        .expect("(0,1) is a valid subaddress");
    }

    /// The same output cannot be stored twice; rescanning a range must be
    /// idempotent or balances double.
    #[test]
    fn the_same_output_cannot_be_inserted_twice() {
        let conn = db();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        let insert = "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx, amount_atomic, height)
                      VALUES (1, x'00', zeroblob(32), 7, 0, 100, 10)";
        conn.execute(insert, []).expect("first insert");
        assert!(
            conn.execute(insert, []).is_err(),
            "duplicate (identity, txid, index_in_tx) must hit the unique constraint"
        );
    }

    /// Deleting an identity cascades to all its data; orphan rows left behind
    /// by "delete wallet" would be a privacy leak.
    #[test]
    fn deleting_an_identity_cascades() {
        let conn = db();
        conn.execute("PRAGMA foreign_keys = ON", []).unwrap();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx, amount_atomic, height)
             VALUES (1, x'00', zeroblob(32), 7, 0, 100, 10)",
            [],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 1, 'a')",
            [],
        )
        .unwrap();

        conn.execute("DELETE FROM identities WHERE id = 1", [])
            .unwrap();
        for t in ["outputs", "subaddresses"] {
            let n: i64 = conn
                .query_row(&format!("SELECT count(*) FROM {t}"), [], |r| r.get(0))
                .unwrap();
            assert_eq!(n, 0, "{t} should be cleared by cascade");
        }
    }
}
