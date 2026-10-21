//! Transaction history.
//!
//! Upstream `WalletRead::get_tx_history` is test-only (its return type lives in
//! `testing`), so this reads the `v_transactions` view maintained by
//! `zcash_client_sqlite`, as the official Android / iOS SDKs do.
//!
//! Column names are not checked at compile time: run the `history_smoke` test
//! after upgrading `zcash_client_sqlite`. Failures are `DATABASE_ERROR` with
//! `params.operation = "history"`.
//!
//! Returns neutral facts (deltas, change, note counts, timestamps); classifying
//! and labelling transactions is up to the host.

use serde_json::json;

use crate::error::{ErrorCode, Result, RuntimeError};

/// Account UUIDs are 16-byte BLOBs in the upstream schema, not text.
///
/// Binding the hyphenated string matches no rows without any error, which is
/// indistinguishable from an empty wallet.
pub(crate) fn uuid_blob(account_uuid: &str) -> Result<Vec<u8>> {
    uuid::Uuid::parse_str(account_uuid)
        .map(|u| u.as_bytes().to_vec())
        .map_err(|_| {
            RuntimeError::with(
                ErrorCode::InvalidAccountId,
                json!({ "value": account_uuid }),
            )
        })
}

/// Maximum rows per page.
pub const MAX_PAGE_SIZE: u32 = 500;

fn db_err(e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "history" })).detail(e)
}

// List and detail share one projection: `row_to_json` has one shape, so a column
// added to only one query would fail at runtime in the other.
const HISTORY_ROW_SELECT: &str = "SELECT
         tx_summary.txid,
         tx_summary.mined_height,
         tx_summary.block_time,
         tx_summary.expiry_height,
         tx_summary.expired_unmined,
         tx_summary.fee_paid,
         tx_summary.account_balance_delta,
         tx_summary.has_change,
         tx_summary.sent_note_count,
         tx_summary.received_note_count,
         tx_summary.memo_count,
         tx_summary.is_shielding,
         tx_summary.total_spent,
         tx_summary.total_received,
         COALESCE((
           SELECT GROUP_CONCAT(
             pool_delta.pool || ':' || pool_delta.balance_delta_zat,
             ','
           )
           FROM (
             SELECT activity.pool,
                    SUM(activity.balance_delta_zat) AS balance_delta_zat
             FROM (
               SELECT owned_output.pool,
                      owned_output.value AS balance_delta_zat
               FROM v_received_outputs owned_output
               JOIN accounts owned_account
                 ON owned_account.id = owned_output.account_id
               WHERE owned_output.transaction_id = chain_tx.id_tx
                 AND owned_account.uuid = tx_summary.account_uuid
                 AND owned_output.pool <> 2
               UNION ALL
               SELECT owned_spend.pool,
                      -spent_output.value AS balance_delta_zat
               FROM v_received_output_spends owned_spend
               JOIN v_received_outputs spent_output
                 ON spent_output.pool = owned_spend.pool
                AND spent_output.id_within_pool_table = owned_spend.received_output_id
               JOIN accounts spent_account
                 ON spent_account.id = owned_spend.account_id
               WHERE owned_spend.transaction_id = chain_tx.id_tx
                 AND spent_account.uuid = tx_summary.account_uuid
                 AND owned_spend.pool <> 2
             ) activity
             GROUP BY activity.pool
             ORDER BY activity.pool
           ) pool_delta
         ), '') AS per_pool_balance_deltas,
         rejected.txid IS NOT NULL AS rejected,
         (SELECT output.to_address
          FROM v_tx_outputs output
          WHERE output.txid = tx_summary.txid
            AND output.from_account_uuid = tx_summary.account_uuid
            AND (output.to_account_uuid IS NULL
                 OR output.to_account_uuid <> tx_summary.account_uuid)
            AND COALESCE(output.is_change, 0) = 0
          LIMIT 1) AS recipient,
         (SELECT COUNT(*)
            FROM v_tx_outputs output
           WHERE output.txid = tx_summary.txid
             AND output.from_account_uuid = tx_summary.account_uuid
             AND (output.to_account_uuid IS NULL
                  OR output.to_account_uuid <> tx_summary.account_uuid)
             AND COALESCE(output.is_change, 0) = 0
         ) AS external_output_count
     FROM v_transactions tx_summary
     JOIN transactions chain_tx ON chain_tx.txid = tx_summary.txid
     LEFT JOIN ext_onekey_tx_state rejected
       ON rejected.txid = tx_summary.txid";

const NON_SAPLING_ACTIVITY: &str = "(
  EXISTS (
    SELECT 1
    FROM v_received_outputs visible_output
    JOIN accounts visible_account ON visible_account.id = visible_output.account_id
    WHERE visible_output.transaction_id = chain_tx.id_tx
      AND visible_account.uuid = tx_summary.account_uuid
      AND visible_output.pool <> 2
  )
  OR EXISTS (
    SELECT 1
    FROM v_received_output_spends visible_spend
    JOIN accounts visible_account ON visible_account.id = visible_spend.account_id
    WHERE visible_spend.transaction_id = chain_tx.id_tx
      AND visible_account.uuid = tx_summary.account_uuid
      AND visible_spend.pool <> 2
  )
)";

/// A page of an account's transaction history as a JSON array.
///
/// Unmined transactions come first, then by height descending.
pub fn list(
    conn: &rusqlite::Connection,
    account_uuid: &str,
    limit: u32,
    offset: u32,
) -> Result<String> {
    if limit == 0 || limit > MAX_PAGE_SIZE {
        return Err(RuntimeError::with(
            ErrorCode::InvalidBatchSize,
            json!({ "value": limit, "max": MAX_PAGE_SIZE }),
        ));
    }

    let sql = format!(
        "{HISTORY_ROW_SELECT}
         WHERE tx_summary.account_uuid = ?1
           AND {NON_SAPLING_ACTIVITY}
         ORDER BY (tx_summary.mined_height IS NULL) DESC,
                  tx_summary.mined_height DESC,
                  tx_summary.tx_index DESC
         LIMIT ?2 OFFSET ?3"
    );
    let mut stmt = conn.prepare(&sql).map_err(db_err)?;

    let rows = stmt
        .query_map(
            rusqlite::params![uuid_blob(account_uuid)?, limit, offset],
            row_to_json,
        )
        .map_err(db_err)?;

    let mut out = Vec::new();
    for r in rows {
        out.push(r.map_err(db_err)?);
    }

    serde_json::to_string(&out).map_err(db_err)
}

/// A `v_transactions` row as JSON; shared by list and details so fields match.
fn row_to_json(row: &rusqlite::Row<'_>) -> rusqlite::Result<serde_json::Value> {
    let txid: Vec<u8> = row.get(0)?;
    let per_pool_entries = row
        .get::<_, String>(14)?
        .split(',')
        .filter(|value| !value.is_empty())
        .map(|value| {
            let (pool, delta) = value.split_once(':').ok_or_else(|| {
                rusqlite::Error::FromSqlConversionFailure(
                    14,
                    rusqlite::types::Type::Text,
                    "invalid per-pool balance delta".into(),
                )
            })?;
            let pool = pool.parse::<i64>().map_err(|error| {
                rusqlite::Error::FromSqlConversionFailure(
                    14,
                    rusqlite::types::Type::Text,
                    Box::new(error),
                )
            })?;
            delta.parse::<i64>().map_err(|error| {
                rusqlite::Error::FromSqlConversionFailure(
                    14,
                    rusqlite::types::Type::Text,
                    Box::new(error),
                )
            })?;
            Ok((pool, delta.to_owned()))
        })
        .collect::<rusqlite::Result<Vec<_>>>()?;
    let pool_ids = per_pool_entries
        .iter()
        .map(|(pool, _)| *pool)
        .collect::<Vec<_>>();
    let per_pool_balance_delta_zat = per_pool_entries
        .into_iter()
        .map(|(pool, delta)| (pool.to_string(), serde_json::Value::String(delta)))
        .collect::<serde_json::Map<_, _>>();
    Ok(json!({
        // Display (big-endian) order, as block explorers show it.
        "txid": hex_display_order(&txid),
        "minedHeight": row.get::<_, Option<u32>>(1)?,
        "blockTime": row.get::<_, Option<i64>>(2)?,
        "expiryHeight": row.get::<_, Option<u32>>(3)?,
        "expiredUnmined": row.get::<_, Option<bool>>(4)?.unwrap_or(false),
        "feeZat": row.get::<_, Option<i64>>(5)?,
        "balanceDeltaZat": row.get::<_, Option<i64>>(6)?,
        "hasChange": row.get::<_, Option<bool>>(7)?.unwrap_or(false),
        "sentNoteCount": row.get::<_, Option<i64>>(8)?.unwrap_or(0),
        "receivedNoteCount": row.get::<_, Option<i64>>(9)?.unwrap_or(0),
        "memoCount": row.get::<_, Option<i64>>(10)?.unwrap_or(0),
        "isShielding": row.get::<_, Option<bool>>(11)?.unwrap_or(false),
        // Account-side amounts. Hosts show sends as spent - received - fee and
        // self-transfers/shielding as received; balance_delta would show a
        // shielding transaction as just its fee.
        "totalSpentZat": row.get::<_, Option<i64>>(12)?,
        "totalReceivedZat": row.get::<_, Option<i64>>(13)?,
        // Exact upstream pool identifiers; the host aggregates as it needs.
        "poolIds": pool_ids,
        // Signed account-owned value change for each exact pool. This cannot be
        // recovered from the aggregate row after an Orchard-to-Ironwood move.
        "perPoolBalanceDeltaZat": per_pool_balance_delta_zat,
        // The trusted node definitively rejected it; it can never be mined.
        "rejected": row.get::<_, bool>(15)?,
        "recipient": row.get::<_, Option<String>>(16)?,
        // Outputs sent to addresses outside this account. Zero with spends means
        // an internal transfer, whether or not the fee is known; inferring from
        // spent - received misreads self-transfers as sends when the fee is unknown.
        "externalOutputCount": row.get::<_, i64>(17)?,
    }))
}

/// One transaction's details, or JSON `null` if it is not in the wallet
/// (foreign txid, rolled back); that is not an error.
pub fn details(conn: &rusqlite::Connection, account_uuid: &str, txid: &str) -> Result<String> {
    // Stored in internal (little-endian) order; the input is display order.
    let internal = display_order_to_internal(txid)?;

    let sql = format!(
        "{HISTORY_ROW_SELECT}
         WHERE tx_summary.account_uuid = ?1
           AND tx_summary.txid = ?2
           AND {NON_SAPLING_ACTIVITY}
         LIMIT 1"
    );
    let mut stmt = conn.prepare(&sql).map_err(db_err)?;

    let mut rows = stmt
        .query(rusqlite::params![uuid_blob(account_uuid)?, internal])
        .map_err(db_err)?;

    match rows.next().map_err(db_err)? {
        None => Ok("null".to_string()),
        Some(row) => {
            let v = row_to_json(row).map_err(db_err)?;
            serde_json::to_string(&v).map_err(db_err)
        }
    }
}

/// Per-output breakdown of one transaction: outputs this account spent,
/// received, and sent externally.
///
/// Reads `v_tx_outputs`, the per-output view the official SDKs also use:
///
/// - `received`: `to_account_uuid` is this account (`is_change` marks change)
/// - `external`: sent by this account to an unknown or different account,
///   including another account registered in this wallet database.
///   Recipient addresses are only known for our own sends.
/// - `spent`: outputs received earlier and spent here, via `v_received_output_spends`
///
/// Pools use upstream codes (0=transparent, 2=sapling, 3=orchard, 4=ironwood),
/// returned with their names.
pub fn details_outputs(
    conn: &rusqlite::Connection,
    account_uuid: &str,
    txid: &str,
) -> Result<String> {
    let internal = display_order_to_internal(txid)?;
    let me = uuid_blob(account_uuid)?;

    let mut received = Vec::new();
    let mut external = Vec::new();
    {
        let mut stmt = conn
            .prepare(
                "SELECT output.output_pool, output.value, output.is_change,
                        output.memo, output.to_address,
                        output.to_account_uuid, output.from_account_uuid,
                        payment.to_address
                 FROM v_tx_outputs output
                 LEFT JOIN sent_notes payment
                   ON payment.transaction_id = output.transaction_id
                  AND payment.output_pool = output.output_pool
                  AND payment.output_index = output.output_index
                 WHERE output.txid = ?1",
            )
            .map_err(db_err)?;
        let mut rows = stmt.query(rusqlite::params![internal]).map_err(db_err)?;
        while let Some(row) = rows.next().map_err(db_err)? {
            let pool: i64 = row.get(0).map_err(db_err)?;
            let value: i64 = row.get(1).map_err(db_err)?;
            let is_change: bool = row
                .get::<_, Option<bool>>(2)
                .map_err(db_err)?
                .unwrap_or(false);
            let memo: Option<Vec<u8>> = row.get(3).map_err(db_err)?;
            let address: Option<String> = row.get(4).map_err(db_err)?;
            // Account UUIDs are BLOBs; reading them as String fails.
            let to_acct: Option<Vec<u8>> = row.get(5).map_err(db_err)?;
            let from_acct: Option<Vec<u8>> = row.get(6).map_err(db_err)?;
            let payment_address: Option<String> = row.get(7).map_err(db_err)?;
            // Compact scanning also marks explicit self-payments as change.
            // A recorded shielded recipient identifies a payment; actual change
            // has no sent-note recipient address. Keep the scan flags intact.
            let is_change = is_change && (pool == 0 || payment_address.is_none());
            let address = payment_address.or(address);

            if pool == 2 {
                continue;
            }

            if to_acct.as_deref() == Some(me.as_slice()) {
                let (memo_text, memo_binary) =
                    memo.as_deref().map(memo_details).unwrap_or((None, None));
                received.push(json!({
                    "valueZat": value,
                    "pool": pool,
                    "poolName": pool_name(pool),
                    "isChange": is_change,
                    "memo": memo_text,
                    "memoBinary": memo_binary,
                    "address": address,
                }));
            } else if from_acct.as_deref() == Some(me.as_slice()) {
                let (memo_text, memo_binary) =
                    memo.as_deref().map(memo_details).unwrap_or((None, None));
                external.push(json!({
                    "memo": memo_text,
                    "memoBinary": memo_binary,
                    "valueZat": value,
                    "pool": pool,
                    "poolName": pool_name(pool),
                    "address": address,
                }));
            }
        }
    }

    let mut spent = Vec::new();
    {
        // This account's received outputs spent by this transaction. Join on
        // (pool, id_within_pool_table) = (pool, received_output_id);
        // `v_received_output_spends` has no output_index.
        let mut stmt = conn
            .prepare(
                "SELECT ro.pool, ro.value, addr.address
                 FROM v_received_output_spends s
                 JOIN transactions t ON t.id_tx = s.transaction_id
                 JOIN v_received_outputs ro
                   ON ro.pool = s.pool
                  AND ro.id_within_pool_table = s.received_output_id
                 JOIN accounts acct ON acct.id = ro.account_id
                 LEFT JOIN addresses addr ON addr.id = ro.address_id
                 WHERE t.txid = ?1 AND acct.uuid = ?2",
            )
            .map_err(db_err)?;
        let mut rows = stmt
            .query(rusqlite::params![internal, uuid_blob(account_uuid)?])
            .map_err(db_err)?;
        while let Some(row) = rows.next().map_err(db_err)? {
            let pool: i64 = row.get(0).map_err(db_err)?;
            if pool == 2 {
                continue;
            }
            spent.push(json!({
                "valueZat": row.get::<_, i64>(1).map_err(db_err)?,
                "pool": pool,
                "poolName": pool_name(pool),
                "address": row.get::<_, Option<String>>(2).map_err(db_err)?,
            }));
        }
    }

    serde_json::to_string(&json!({
        "txid": txid,
        "spent": spent,
        "received": received,
        "external": external,
    }))
    .map_err(db_err)
}

/// Name for an upstream pool code.
fn pool_name(pool: i64) -> &'static str {
    match pool {
        0 => "transparent",
        2 => "sapling",
        3 => "orchard",
        4 => "ironwood",
        _ => "unknown",
    }
}

#[derive(Debug, PartialEq)]
enum DecodedMemo {
    None,
    Text(String),
    /// `hex`: the field without its zero padding. `hash`: BLAKE2b-256 of the
    /// whole 512-byte field, hex.
    Binary {
        hex: String,
        hash: String,
    },
}

/// Classified per ZIP 302: text is shown as is; arbitrary-data and
/// future-format memos cannot be, so they are identified by a hash.
fn decode_memo(bytes: &[u8]) -> DecodedMemo {
    use zcash_protocol::memo::{Memo, MemoBytes};
    let Ok(memo_bytes) = MemoBytes::from_bytes(bytes) else {
        return DecodedMemo::None;
    };
    match Memo::try_from(&memo_bytes) {
        Ok(Memo::Empty) => DecodedMemo::None,
        // All zeroes decodes as empty text; it means no memo too.
        Ok(Memo::Text(text)) if text.is_empty() => DecodedMemo::None,
        Ok(Memo::Text(text)) => DecodedMemo::Text(String::from(text)),
        // Invalid UTF-8 in the text range is still data the sender put there.
        Ok(Memo::Future(_)) | Ok(Memo::Arbitrary(_)) | Err(_) => {
            let hash = blake2b_simd::Params::new()
                .hash_length(32)
                .hash(memo_bytes.as_array());
            DecodedMemo::Binary {
                hex: hex::encode(memo_bytes.as_slice()),
                hash: hex::encode(hash.as_bytes()),
            }
        }
    }
}

/// Preserve the exact padded field for both text and binary memo displays.
fn memo_details(bytes: &[u8]) -> (Option<String>, Option<serde_json::Value>) {
    let text = match decode_memo(bytes) {
        DecodedMemo::None => return (None, None),
        DecodedMemo::Text(text) => Some(text),
        DecodedMemo::Binary { .. } => None,
    };
    let Ok(field) = zcash_protocol::memo::MemoBytes::from_bytes(bytes) else {
        return (None, None);
    };
    let hash = blake2b_simd::Params::new()
        .hash_length(32)
        .hash(field.as_array());
    (
        text,
        Some(json!({
            "hex": hex::encode(field.as_array()),
            "hash": hex::encode(hash.as_bytes()),
        })),
    )
}

/// Display order (big-endian hex) to internal order (little-endian bytes).
pub(crate) fn display_order_to_internal(txid: &str) -> Result<Vec<u8>> {
    let bytes = hex_decode(txid)
        .ok_or_else(|| RuntimeError::with(ErrorCode::InvalidTxid, json!({ "value": txid })))?;
    if bytes.len() != 32 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidTxid,
            json!({ "value": txid, "byteLen": bytes.len() }),
        ));
    }
    Ok(bytes.into_iter().rev().collect())
}

// Byte slicing a `&str` panics on a non-ASCII boundary, and in wasm a panic
// traps the whole runtime; `hex::decode` rejects such input instead.
fn hex_decode(s: &str) -> Option<Vec<u8>> {
    hex::decode(s).ok()
}

/// Stored (little-endian) txid to display-order hex.
pub(crate) fn hex_display_order(txid_le: &[u8]) -> String {
    txid_le.iter().rev().map(|b| format!("{b:02x}")).collect()
}

#[cfg(test)]
mod tests {
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use zcash_client_sqlite::{wallet::init::WalletMigrator, WalletDb};
    use zcash_protocol::consensus::Network;

    use super::*;
    use crate::clock::HostClock;

    // Expected hashes from Python: hashlib.blake2b(field, digest_size=32),
    // where field is the memo padded with zeroes to 512 bytes.
    #[test]
    fn memos_are_classified_per_zip_302() {
        assert_eq!(decode_memo(b"hello"), DecodedMemo::Text("hello".into()));
        assert_eq!(decode_memo(&[0xF6]), DecodedMemo::None);
        assert_eq!(decode_memo(&[0u8; 512]), DecodedMemo::None);
        assert_eq!(
            decode_memo(&[0xFF, 1, 2, 3]),
            DecodedMemo::Binary {
                hex: "ff010203".into(),
                hash: "65f2f3b6161331db680c67819cb0419fbebc46d460926cb5b2251300be0bab19".into(),
            }
        );
        assert_eq!(
            decode_memo(&[0xF5, 9]),
            DecodedMemo::Binary {
                hex: "f509".into(),
                hash: "0e72c1749d69b52a0360b93c9d8598c99c03ff333a1b011504f35a9983560f73".into(),
            }
        );
        assert_eq!(
            decode_memo(&[0xC3, 0x28]),
            DecodedMemo::Binary {
                hex: "c328".into(),
                hash: "e839917e76e97f37720fabe711e787adf8819788647c7407363819b8e4bf80ae".into(),
            }
        );
    }

    #[test]
    fn memo_details_preserve_full_text_and_binary_fields() {
        let (text, raw) = memo_details("  中文\n🙂  ".as_bytes());
        assert_eq!(text.as_deref(), Some("  中文\n🙂  "));
        let raw = raw.unwrap();
        assert_eq!(raw["hex"].as_str().unwrap().len(), 1024);
        assert!(raw["hex"]
            .as_str()
            .unwrap()
            .starts_with("2020e4b8ade696870af09f99822020"));
        assert_eq!(
            raw["hash"],
            "8ddea12a10d4fba7353a20aff34eb4a0a4e9533eabc637a5fca9e6e90ab7eeef"
        );
        let (text, raw) = memo_details(&[0xff, 1, 2, 3]);
        assert_eq!(text, None);
        let raw = raw.unwrap();
        assert_eq!(raw["hex"], format!("ff010203{}", "00".repeat(508)));
        assert_eq!(
            raw["hash"],
            "65f2f3b6161331db680c67819cb0419fbebc46d460926cb5b2251300be0bab19"
        );
        assert_eq!(memo_details(&[0xf6]), (None, None));
        assert_eq!(memo_details(&[0; 512]), (None, None));
    }

    #[test]
    fn a_non_ascii_txid_is_rejected_not_a_panic() {
        // Four bytes, but byte 2 falls inside "é": slicing there panics.
        assert!(display_order_to_internal("aéa").is_err());
    }

    #[test]
    fn txid_is_reversed_for_display() {
        assert_eq!(hex_display_order(&[0x01, 0x02, 0x03]), "030201");
        assert_eq!(hex_display_order(&[]), "");
    }

    #[test]
    fn rejects_bad_page_size() {
        // Validation fails before any query runs.
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        for bad in [0, MAX_PAGE_SIZE + 1] {
            let e = list(&conn, "whatever", bad, 0).unwrap_err();
            assert_eq!(e.code, ErrorCode::InvalidBatchSize);
            assert_eq!(e.params["value"], bad);
        }
    }

    #[test]
    fn history_queries_compile_against_the_migrated_wallet_schema() {
        let mut conn = rusqlite::Connection::open_in_memory().unwrap();
        rusqlite::vtab::array::load_module(&conn).unwrap();
        {
            let rng = ChaCha20Rng::from_seed([7; 32]);
            let mut db = WalletDb::from_connection(&mut conn, Network::TestNetwork, HostClock, rng);
            WalletMigrator::new()
                .with_external_migrations(crate::tx_state::migrations())
                .init_or_migrate(&mut db)
                .unwrap();
        }

        let account_uuid = uuid::Uuid::nil().to_string();
        assert_eq!(list(&conn, &account_uuid, 20, 0).unwrap(), "[]");
        assert_eq!(
            details(&conn, &account_uuid, &"00".repeat(32)).unwrap(),
            "null"
        );
        let outputs: serde_json::Value =
            serde_json::from_str(&details_outputs(&conn, &account_uuid, &"00".repeat(32)).unwrap())
                .unwrap();
        assert_eq!(outputs["received"], json!([]));
    }

    fn history_fixture() -> rusqlite::Connection {
        let conn = rusqlite::Connection::open_in_memory().unwrap();
        conn.execute_batch(
            "CREATE TABLE accounts (id INTEGER PRIMARY KEY, uuid BLOB NOT NULL);
             CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB NOT NULL);
             CREATE TABLE v_received_outputs (
               id_within_pool_table INTEGER NOT NULL,
               transaction_id INTEGER NOT NULL,
               account_id INTEGER NOT NULL,
               pool INTEGER NOT NULL,
               value INTEGER NOT NULL
             );
             CREATE TABLE v_received_output_spends (
               transaction_id INTEGER NOT NULL,
               account_id INTEGER NOT NULL,
               pool INTEGER NOT NULL,
               received_output_id INTEGER NOT NULL
             );
             CREATE TABLE v_tx_outputs (
               transaction_id INTEGER,
               output_index INTEGER DEFAULT 0,
               txid BLOB NOT NULL,
               from_account_uuid BLOB,
               to_account_uuid BLOB,
               to_address TEXT,
               is_change INTEGER
             );
             CREATE TABLE sent_notes (
               transaction_id INTEGER,
               output_pool INTEGER,
               output_index INTEGER,
               to_address TEXT
             );
             CREATE TABLE ext_onekey_tx_state (
               txid BLOB PRIMARY KEY NOT NULL,
               rejection_code INTEGER,
               rejection_reason TEXT
             );
             CREATE TABLE v_transactions (
               account_uuid BLOB NOT NULL,
               mined_height INTEGER,
               txid BLOB NOT NULL,
               tx_index INTEGER,
               block_time INTEGER,
               expiry_height INTEGER,
               expired_unmined INTEGER,
               fee_paid INTEGER,
               account_balance_delta INTEGER,
               has_change INTEGER,
               sent_note_count INTEGER,
               received_note_count INTEGER,
               memo_count INTEGER,
               is_shielding INTEGER,
               total_spent INTEGER,
               total_received INTEGER
             );",
        )
        .unwrap();

        conn
    }

    #[test]
    fn preserves_supported_pool_ids_and_hides_sapling() {
        let conn = history_fixture();
        let account_uuid = uuid::Uuid::parse_str("11111111-1111-1111-1111-111111111111").unwrap();
        conn.execute(
            "INSERT INTO accounts (id, uuid) VALUES (1, ?1)",
            [account_uuid.as_bytes().as_slice()],
        )
        .unwrap();

        for id in 1_i64..=6 {
            let txid = vec![id as u8; 32];
            conn.execute(
                "INSERT INTO transactions (id_tx, txid) VALUES (?1, ?2)",
                rusqlite::params![id, txid],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO v_transactions (
                   account_uuid, mined_height, txid, tx_index, block_time,
                   expiry_height, expired_unmined, fee_paid,
                   account_balance_delta, has_change, sent_note_count,
                   received_note_count, memo_count, is_shielding, total_spent,
                   total_received
                 ) VALUES (
                   ?1, ?2, ?3, ?4, 1000, 200, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0
                 )",
                rusqlite::params![
                    account_uuid.as_bytes().as_slice(),
                    id,
                    vec![id as u8; 32],
                    id
                ],
            )
            .unwrap();
        }

        conn.execute(
            "INSERT INTO v_received_outputs (
               id_within_pool_table, transaction_id, account_id, pool, value
             ) VALUES
               (1, 1, 1, 0, 10),
               (1, 1, 1, 2, 20),
               (2, 3, 1, 4, 99),
               (3, 4, 1, 99, 40),
               (2, 5, 1, 2, 50),
               (4, 99, 1, 0, 100),
               (5, 98, 1, 4, 70),
               (6, 97, 1, 3, 100),
               (7, 96, 1, 3, 50),
               (8, 6, 1, 3, 50)",
            [],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO v_received_output_spends (
               transaction_id, account_id, pool, received_output_id
             ) VALUES
               (2, 1, 4, 5),
               (3, 1, 3, 6),
               (6, 1, 3, 7)",
            [],
        )
        .unwrap();

        let rows: Vec<serde_json::Value> =
            serde_json::from_str(&list(&conn, &account_uuid.to_string(), 20, 0).unwrap()).unwrap();
        let pool_ids: Vec<Vec<i64>> = rows
            .iter()
            .map(|row| {
                row["poolIds"]
                    .as_array()
                    .unwrap()
                    .iter()
                    .map(|pool| pool.as_i64().unwrap())
                    .collect()
            })
            .collect();
        assert_eq!(
            pool_ids,
            vec![vec![3], vec![99], vec![3, 4], vec![4], vec![0]]
        );

        let pool_deltas: Vec<serde_json::Value> = rows
            .iter()
            .map(|row| row["perPoolBalanceDeltaZat"].clone())
            .collect();
        assert_eq!(
            pool_deltas,
            vec![
                // Touching a pool on both sides must preserve the pool key even
                // when the net value change is zero.
                json!({ "3": "0" }),
                json!({ "99": "40" }),
                json!({ "3": "-100", "4": "99" }),
                json!({ "4": "-70" }),
                json!({ "0": "10" }),
            ]
        );

        let mixed: serde_json::Value = serde_json::from_str(
            &details(&conn, &account_uuid.to_string(), &"03".repeat(32)).unwrap(),
        )
        .unwrap();
        assert_eq!(mixed["poolIds"], json!([3, 4]));
        assert_eq!(
            mixed["perPoolBalanceDeltaZat"],
            json!({ "3": "-100", "4": "99" })
        );
    }
    #[test]
    fn decrypted_memos_are_visible_only_to_the_output_sender_or_receiver() {
        let conn = history_fixture();
        conn.execute_batch(
            "ALTER TABLE v_tx_outputs ADD COLUMN output_pool INTEGER DEFAULT 4;
             ALTER TABLE v_tx_outputs ADD COLUMN value INTEGER DEFAULT 10000;
             ALTER TABLE v_tx_outputs ADD COLUMN memo BLOB;
             ALTER TABLE v_received_outputs ADD COLUMN address_id INTEGER;
             CREATE TABLE addresses (id INTEGER PRIMARY KEY, address TEXT);",
        )
        .unwrap();
        let sender = uuid::Uuid::from_u128(1);
        let receiver = uuid::Uuid::from_u128(2);
        let stranger = uuid::Uuid::from_u128(3);
        let txid = [7u8; 32];
        let text = "  中文\n🙂  ";
        conn.execute(
            "INSERT INTO v_tx_outputs
             (txid, from_account_uuid, to_account_uuid, to_address, is_change, memo)
             VALUES (?1, ?2, ?3, 'synthetic-recipient', 0, ?4)",
            rusqlite::params![
                txid.as_slice(),
                sender.as_bytes().as_slice(),
                receiver.as_bytes().as_slice(),
                text.as_bytes(),
            ],
        )
        .unwrap();
        for (account, field) in [(sender, "external"), (receiver, "received")] {
            let output: serde_json::Value = serde_json::from_str(
                &details_outputs(&conn, &account.to_string(), &hex_display_order(&txid)).unwrap(),
            )
            .unwrap();
            assert_eq!(output[field][0]["memo"], text);
            assert_eq!(
                output[field][0]["memoBinary"]["hex"]
                    .as_str()
                    .unwrap()
                    .len(),
                1024
            );
            assert_eq!(
                output[field][0]["memoBinary"]["hash"],
                "8ddea12a10d4fba7353a20aff34eb4a0a4e9533eabc637a5fca9e6e90ab7eeef"
            );
        }
        let output: serde_json::Value = serde_json::from_str(
            &details_outputs(&conn, &stranger.to_string(), &hex_display_order(&txid)).unwrap(),
        )
        .unwrap();
        assert_eq!(output["external"], json!([]));
        assert_eq!(output["received"], json!([]));
    }

    #[test]
    fn explicit_self_payment_is_not_displayed_as_change_after_scanning() {
        let conn = history_fixture();
        conn.execute_batch(
            "ALTER TABLE v_tx_outputs ADD COLUMN output_pool INTEGER DEFAULT 4;
             ALTER TABLE v_tx_outputs ADD COLUMN value INTEGER;
             ALTER TABLE v_tx_outputs ADD COLUMN memo BLOB;
             ALTER TABLE v_received_outputs ADD COLUMN address_id INTEGER;
             CREATE TABLE addresses (id INTEGER PRIMARY KEY, address TEXT);",
        )
        .unwrap();
        let account = uuid::Uuid::from_u128(1);
        let txid = [7u8; 32];
        let text = "  self payment\n\u{1f642}  ";
        for (index, value, recipient, memo) in [
            (0, 10_000, Some("synthetic-self-recipient"), Some(text)),
            (1, 2_536_200, None, None),
        ] {
            conn.execute(
                "INSERT INTO v_tx_outputs
                 (transaction_id, output_index, txid, from_account_uuid,
                  to_account_uuid, to_address, is_change, value, memo)
                 VALUES (1, ?1, ?2, ?3, ?3, ?4, 1, ?5, ?6)",
                rusqlite::params![
                    index,
                    txid.as_slice(),
                    account.as_bytes().as_slice(),
                    recipient,
                    value,
                    memo.map(str::as_bytes),
                ],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO sent_notes
                 (transaction_id, output_pool, output_index, to_address)
                 VALUES (1, 4, ?1, ?2)",
                rusqlite::params![index, recipient],
            )
            .unwrap();
        }
        conn.execute_batch(
            "INSERT INTO sent_notes
             (transaction_id, output_pool, output_index, to_address) VALUES
               (1, 3, 1, 'different-pool-payment'),
               (2, 4, 1, 'different-transaction-payment');",
        )
        .unwrap();

        let output: serde_json::Value = serde_json::from_str(
            &details_outputs(&conn, &account.to_string(), &hex_display_order(&txid)).unwrap(),
        )
        .unwrap();
        let received = output["received"].as_array().unwrap();
        let payment = received
            .iter()
            .find(|row| row["valueZat"] == 10_000)
            .unwrap();
        let change = received
            .iter()
            .find(|row| row["valueZat"] == 2_536_200)
            .unwrap();
        assert_eq!(payment["isChange"], false);
        assert_eq!(payment["memo"], text);
        assert_eq!(payment["address"], "synthetic-self-recipient");
        assert_eq!(change["isChange"], true);
        assert_eq!(output["external"], json!([]));
        // This is a read projection, not a rewrite of upstream wallet state.
        let scanner_change_count: i64 = conn
            .query_row(
                "SELECT COUNT(*) FROM v_tx_outputs WHERE is_change = 1",
                [],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(scanner_change_count, 2);
    }

    #[test]
    fn outgoing_outputs_are_external_relative_to_the_current_account() {
        let conn = history_fixture();
        let sender = uuid::Uuid::from_u128(1);
        let receiver = uuid::Uuid::from_u128(2);
        for (id, account) in [(1, sender), (2, receiver)] {
            conn.execute(
                "INSERT INTO accounts (id, uuid) VALUES (?1, ?2)",
                rusqlite::params![id, account.as_bytes().as_slice()],
            )
            .unwrap();
        }

        let cases = [
            (Some(receiver), false, 1),
            (None, false, 1),
            (Some(sender), false, 0),
            (Some(sender), true, 0),
        ];
        for (index, (to_account, is_change, external_count)) in cases.into_iter().enumerate() {
            let id = index as i64 + 1;
            let txid = vec![id as u8; 32];
            conn.execute(
                "INSERT INTO transactions (id_tx, txid) VALUES (?1, ?2)",
                rusqlite::params![id, txid],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO v_transactions (
                   account_uuid, mined_height, txid, tx_index, block_time,
                   expiry_height, expired_unmined, fee_paid,
                   account_balance_delta, has_change, sent_note_count,
                   received_note_count, memo_count, is_shielding, total_spent,
                   total_received
                 ) VALUES (?1, 100, ?2, ?3, 1000, 200, 0, 10, -90, 1, 2, 1, 0, 0, 100, 10)",
                rusqlite::params![sender.as_bytes().as_slice(), txid, id],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO v_received_outputs (
                   id_within_pool_table, transaction_id, account_id, pool, value
                 ) VALUES (?1, ?1, 1, 3, 10)",
                [id],
            )
            .unwrap();
            conn.execute(
                "INSERT INTO v_tx_outputs (
                   txid, from_account_uuid, to_account_uuid, to_address, is_change
                 ) VALUES (?1, ?2, ?3, 'synthetic-recipient', ?4)",
                rusqlite::params![
                    txid,
                    sender.as_bytes().as_slice(),
                    to_account.map(|account| account.as_bytes().to_vec()),
                    is_change
                ],
            )
            .unwrap();

            let detail: serde_json::Value = serde_json::from_str(
                &details(&conn, &sender.to_string(), &hex_display_order(&txid)).unwrap(),
            )
            .unwrap();
            assert_eq!(detail["externalOutputCount"], external_count);
            assert_eq!(
                detail["recipient"],
                if external_count > 0 {
                    json!("synthetic-recipient")
                } else {
                    json!(null)
                }
            );
            let rows: Vec<serde_json::Value> =
                serde_json::from_str(&list(&conn, &sender.to_string(), 20, 0).unwrap()).unwrap();
            assert_eq!(rows[0], detail);
        }
    }
}
