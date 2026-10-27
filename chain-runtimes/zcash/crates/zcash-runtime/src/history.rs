//! Transaction history.
//!
//! Reads the `v_transactions` view maintained by `zcash_client_sqlite`, as the official SDKs do.
//!
//! Column names are not checked at compile time; run `history_smoke` after upgrading
//! `zcash_client_sqlite`. Failures are `DATABASE_ERROR` with `params.operation = "history"`.

use serde_json::json;
use zcash_address::unified::{self, Container, Encoding};
use zcash_address::{ConversionError, ToAddress, TryFromAddress, ZcashAddress};
use zcash_protocol::consensus::NetworkType;

use crate::error::{ErrorCode, Result, RuntimeError};

/// Account UUIDs are 16-byte BLOBs in the upstream schema, not text.
///
/// Binding the hyphenated string silently matches no rows.
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

// List and detail share one projection because `row_to_json` has one shape.
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
          ORDER BY output.output_pool, output.output_index
          LIMIT 1) AS recipient,
         (SELECT COUNT(*)
            FROM v_tx_outputs output
           WHERE output.txid = tx_summary.txid
             AND output.from_account_uuid = tx_summary.account_uuid
             AND (output.to_account_uuid IS NULL
                  OR output.to_account_uuid <> tx_summary.account_uuid)
             AND COALESCE(output.is_change, 0) = 0
         ) AS external_output_count,
         (SELECT output.output_pool
          FROM v_tx_outputs output
          WHERE output.txid = tx_summary.txid
            AND output.from_account_uuid = tx_summary.account_uuid
            AND (output.to_account_uuid IS NULL
                 OR output.to_account_uuid <> tx_summary.account_uuid)
            AND COALESCE(output.is_change, 0) = 0
          ORDER BY output.output_pool, output.output_index
          LIMIT 1) AS recipient_pool,
         chain_tx.created IS NOT NULL AS created_locally
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
    let recipient = match (row.get::<_, Option<String>>(16)?, row.get(18)?) {
        (Some(address), Some(pool)) => Some(paid_address(&address, pool, row.get(19)?)),
        (address, _) => address,
    };
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
        // Account-side amounts; balance_delta would show a shielding transaction as just its fee.
        "totalSpentZat": row.get::<_, Option<i64>>(12)?,
        "totalReceivedZat": row.get::<_, Option<i64>>(13)?,
        "poolIds": pool_ids,
        // Signed value change per pool; not recoverable from the aggregate after an Orchard-to-Ironwood move.
        "perPoolBalanceDeltaZat": per_pool_balance_delta_zat,
        // The trusted node definitively rejected it; it can never be mined.
        "rejected": row.get::<_, bool>(15)?,
        "recipient": recipient,
        // Outputs sent outside this account. Zero with spends means an internal transfer.
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
/// Reads `v_tx_outputs`:
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
                        payment.to_address, chain_tx.created IS NOT NULL
                 FROM v_tx_outputs output
                 LEFT JOIN sent_notes payment
                   ON payment.transaction_id = output.transaction_id
                  AND payment.output_pool = output.output_pool
                  AND payment.output_index = output.output_index
                 LEFT JOIN transactions chain_tx
                   ON chain_tx.id_tx = output.transaction_id
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
            let created_locally: bool = row.get(8).map_err(db_err)?;
            // Compact scanning also marks explicit self-payments as change; real change has no recipient address.
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
                    "address": address
                        .map(|address| paid_address(&address, pool, created_locally)),
                }));
            }
        }
    }

    let mut spent = Vec::new();
    {
        // Join on (pool, id_within_pool_table) = (pool, received_output_id); `v_received_output_spends` has no output_index.
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

/// A stored Unified Address with the network it was encoded for.
struct StoredUnified(NetworkType, unified::Address);

impl TryFromAddress for StoredUnified {
    type Error = std::convert::Infallible;

    fn try_from_unified(
        net: NetworkType,
        data: unified::Address,
    ) -> std::result::Result<Self, ConversionError<Self::Error>> {
        Ok(Self(net, data))
    }
}

/// The receiver an output paid, where upstream stored a wallet UA containing it.
/// Shielded: Orchard-only UA, unless a send made here names an address unlike
/// the wallet's address-table UAs (exactly Orchard + P2PKH for OneKey UFVKs).
fn paid_address(stored: &str, pool: i64, created_locally: bool) -> String {
    let Some(StoredUnified(net, ua)) = ZcashAddress::try_from_encoded(stored)
        .ok()
        .and_then(|address| address.convert().ok())
    else {
        return stored.to_owned();
    };
    let items = ua.items();
    let is_address_table_shape = items.len() == 2
        && items
            .iter()
            .any(|receiver| matches!(receiver, unified::Receiver::Orchard(_)))
        && items
            .iter()
            .any(|receiver| matches!(receiver, unified::Receiver::P2pkh(_)));
    let paid = items.into_iter().find_map(|receiver| match receiver {
        unified::Receiver::P2pkh(data) if pool == 0 => {
            Some(ZcashAddress::from_transparent_p2pkh(net, data))
        }
        unified::Receiver::P2sh(data) if pool == 0 => {
            Some(ZcashAddress::from_transparent_p2sh(net, data))
        }
        unified::Receiver::Orchard(data)
            if (pool == 3 || pool == 4) && (!created_locally || is_address_table_shape) =>
        {
            unified::Address::try_from_items(vec![unified::Receiver::Orchard(data)])
                .ok()
                .map(|orchard_only| ZcashAddress::from_unified(net, orchard_only))
        }
        _ => None,
    });
    paid.map_or_else(|| stored.to_owned(), |address| address.encode())
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
             CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB NOT NULL, created TEXT);
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
               output_pool INTEGER DEFAULT 4,
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
            "ALTER TABLE v_tx_outputs ADD COLUMN value INTEGER DEFAULT 10000;
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
            "ALTER TABLE v_tx_outputs ADD COLUMN value INTEGER;
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

/// Recipients and fees of transactions stored the way the wallet really stores
/// them: decrypted by enhancement, or recorded by a local send.
#[cfg(test)]
mod recipient_tests {
    use std::collections::HashMap;

    use orchard::builder::{Builder as OrchardBuilder, BundleType};
    use orchard::bundle::{Authorized as OrchardAuthorized, BundleVersion};
    use orchard::primitives::redpallas::Signature;
    use orchard::value::NoteValue;
    use orchard::{Anchor, Proof};
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use transparent::address::{Script, TransparentAddress};
    use transparent::bundle::{Authorized, Bundle, OutPoint, TxIn, TxOut};
    use transparent::keys::{IncomingViewingKey, NonHardenedChildIndex};
    use zcash_client_backend::data_api::chain::ChainState;
    use zcash_client_backend::data_api::wallet::TargetHeight;
    use zcash_client_backend::data_api::{
        Account as _, AccountBirthday, AccountPurpose, SentTransaction, SentTransactionOutput,
        WalletWrite,
    };
    use zcash_client_backend::decrypt_transaction;
    use zcash_client_backend::encoding::AddressCodec;
    use zcash_client_backend::wallet::{Recipient, WalletTransparentOutput};
    use zcash_client_sqlite::wallet::init::WalletMigrator;
    use zcash_client_sqlite::{AccountUuid, WalletDb};
    use zcash_keys::keys::{
        ReceiverRequirement, UnifiedAddressRequest, UnifiedFullViewingKey, UnifiedSpendingKey,
    };
    use zcash_primitives::block::BlockHash;
    use zcash_primitives::transaction::{Transaction, TransactionData};
    use zcash_protocol::consensus::{BlockHeight, BranchId, Network};
    use zcash_protocol::memo::MemoBytes;
    use zcash_protocol::value::{ZatBalance, Zatoshis};
    use zcash_protocol::PoolType;

    use super::*;
    use crate::clock::HostClock;

    const NET: Network = Network::MainNetwork;
    const TIP: u32 = 3_525_600;
    const MINED: u32 = 3_525_531;

    struct Wallet {
        conn: rusqlite::Connection,
        accounts: Vec<(AccountUuid, UnifiedFullViewingKey)>,
    }

    impl Wallet {
        /// Accounts 0..count of one seed, as the host registers them.
        fn new(count: u32) -> Self {
            let mut conn = rusqlite::Connection::open_in_memory().unwrap();
            rusqlite::vtab::array::load_module(&conn).unwrap();
            let mut accounts = Vec::new();
            {
                let mut db = db(&mut conn);
                WalletMigrator::new()
                    .with_external_migrations(crate::tx_state::migrations())
                    .init_or_migrate(&mut db)
                    .unwrap();
                let birthday = AccountBirthday::from_parts(
                    ChainState::empty(BlockHeight::from_u32(3_525_000), BlockHash([0; 32])),
                    None,
                );
                for index in 0..count {
                    let ufvk = ufvk(&[9; 32], index);
                    let account = db
                        .import_account_ufvk(
                            &format!("hd-{index}"),
                            &ufvk,
                            &birthday,
                            AccountPurpose::ViewOnly,
                            None,
                        )
                        .unwrap();
                    accounts.push((account.id(), ufvk));
                }
                db.update_chain_tip(BlockHeight::from_u32(TIP)).unwrap();
            }
            Self { conn, accounts }
        }

        fn account(&self, index: usize) -> AccountUuid {
            self.accounts[index].0
        }

        /// A transparent UTXO of `index`, so stored transactions spending it are
        /// attributed to that account.
        fn fund(&mut self, index: usize) -> OutPoint {
            let outpoint = OutPoint::new([5; 32], 0);
            let utxo = WalletTransparentOutput::from_parts(
                outpoint.clone(),
                TxOut::new(
                    Zatoshis::const_from_u64(1_000_000),
                    taddr(&self.accounts[index].1, 0).script().into(),
                ),
                Some(BlockHeight::from_u32(3_525_100)),
                Some(self.account(index)),
                None,
                None,
            )
            .unwrap();
            db(&mut self.conn)
                .put_received_transparent_utxo(&utxo)
                .unwrap();
            outpoint
        }

        /// Stores `tx` as enhancement does: decrypted with every account's keys.
        fn enhance(&mut self, tx: &Transaction) {
            let keys = self.accounts.iter().cloned().collect::<HashMap<_, _>>();
            db(&mut self.conn)
                .store_decrypted_tx(decrypt_transaction(
                    &NET,
                    Some(BlockHeight::from_u32(MINED)),
                    Some(BlockHeight::from_u32(TIP)),
                    tx,
                    &keys,
                ))
                .unwrap();
        }

        fn remove(&mut self, index: usize) {
            let account = self.account(index);
            db(&mut self.conn).delete_account(account).unwrap();
        }

        fn row(&self, index: usize, tx: &Transaction) -> serde_json::Value {
            let txid = tx.txid().to_string();
            let rows: Vec<serde_json::Value> = serde_json::from_str(
                &list(
                    &self.conn,
                    &self.account(index).expose_uuid().to_string(),
                    20,
                    0,
                )
                .unwrap(),
            )
            .unwrap();
            let row = rows.into_iter().find(|row| row["txid"] == txid).unwrap();
            let detail: serde_json::Value = serde_json::from_str(
                &details(
                    &self.conn,
                    &self.account(index).expose_uuid().to_string(),
                    &txid,
                )
                .unwrap(),
            )
            .unwrap();
            assert_eq!(row, detail);
            row
        }

        fn external(&self, index: usize, tx: &Transaction) -> Vec<serde_json::Value> {
            let outputs: serde_json::Value = serde_json::from_str(
                &details_outputs(
                    &self.conn,
                    &self.account(index).expose_uuid().to_string(),
                    &tx.txid().to_string(),
                )
                .unwrap(),
            )
            .unwrap();
            outputs["external"].as_array().unwrap().clone()
        }

        /// What upstream stored for the payment, before the read projection:
        /// its address and whether it names a receiving account.
        fn stored_payment(&self) -> (String, bool) {
            self.conn
                .query_row(
                    "SELECT to_address, to_account_id IS NOT NULL FROM sent_notes
                     WHERE transaction_id = (SELECT MAX(id_tx) FROM transactions)
                     ORDER BY output_pool, output_index LIMIT 1",
                    [],
                    |row| Ok((row.get(0)?, row.get(1)?)),
                )
                .unwrap()
        }

        /// Records `tx` as a send created here, paying `to` in Ironwood.
        fn record_local_send(&mut self, tx: &Transaction, input: OutPoint, index: usize, to: &str) {
            let outputs = [SentTransactionOutput::from_parts(
                index,
                Recipient::External {
                    recipient_address: to.parse().unwrap(),
                    output_pool: PoolType::IRONWOOD,
                },
                Zatoshis::const_from_u64(20_000),
                Some(MemoBytes::empty()),
            )];
            let account = self.account(0);
            db(&mut self.conn)
                .store_transactions_to_be_sent(&[SentTransaction::new(
                    tx,
                    // `time` is a wasm-only dependency of this crate.
                    std::time::SystemTime::now().into(),
                    TargetHeight::from(BlockHeight::from_u32(MINED)),
                    account,
                    &outputs,
                    Zatoshis::const_from_u64(10_000),
                    &[input],
                )])
                .unwrap();
        }
    }

    fn db(
        conn: &mut rusqlite::Connection,
    ) -> WalletDb<&mut rusqlite::Connection, Network, HostClock, ChaCha20Rng> {
        WalletDb::from_connection(conn, NET, HostClock, ChaCha20Rng::from_seed([7; 32]))
    }

    /// A UFVK as a third-party wallet derives it, with a Sapling key.
    fn full_ufvk(seed: &[u8; 32], index: u32) -> UnifiedFullViewingKey {
        UnifiedSpendingKey::from_seed(&NET, seed, index.try_into().unwrap())
            .unwrap()
            .to_unified_full_viewing_key()
    }

    /// A UFVK as OneKey builds it (keys runtime `ufvkFromAccountKeys`):
    /// transparent and Orchard only.
    fn ufvk(seed: &[u8; 32], index: u32) -> UnifiedFullViewingKey {
        use zcash_address::unified::{Fvk, Ufvk};
        let (net, full) = Ufvk::decode(&full_ufvk(seed, index).encode(&NET)).unwrap();
        let items = full
            .items()
            .into_iter()
            .filter(|item| matches!(item, Fvk::P2pkh(_) | Fvk::Orchard(_)))
            .collect();
        let onekey = Ufvk::try_from_items(items).unwrap().encode(&net);
        UnifiedFullViewingKey::decode(&NET, &onekey).unwrap()
    }

    fn taddr(ufvk: &UnifiedFullViewingKey, index: u32) -> TransparentAddress {
        ufvk.transparent()
            .unwrap()
            .derive_external_ivk()
            .unwrap()
            .derive_address(NonHardenedChildIndex::from_index(index).unwrap())
            .unwrap()
    }

    fn receive_orchard(encoded: &str) -> orchard::Address {
        match zcash_keys::address::Address::decode(&NET, encoded) {
            Some(zcash_keys::address::Address::Unified(ua)) => *ua.orchard().unwrap(),
            _ => panic!("not a unified address"),
        }
    }

    /// The address the Receive page shows (keys runtime policy "orchard").
    fn receive_address(ufvk: &UnifiedFullViewingKey) -> zcash_keys::address::UnifiedAddress {
        ufvk.default_address(UnifiedAddressRequest::ORCHARD)
            .unwrap()
            .0
    }

    /// An Ironwood bundle paying `recipient`, and the payment's action index.
    /// The proof and signatures are placeholders: storing a transaction never
    /// verifies them.
    fn ironwood_payment(
        recipient: orchard::Address,
    ) -> (orchard::Bundle<OrchardAuthorized, ZatBalance>, usize) {
        let version = BundleVersion::ironwood_v3();
        let mut builder = OrchardBuilder::new(
            BundleType::DEFAULT,
            version,
            version.default_flags(),
            Anchor::empty_tree(),
        )
        .unwrap();
        builder
            .add_output(None, recipient, NoteValue::from_raw(20_000), [0; 512])
            .unwrap();
        let (bundle, meta) = authorize_placeholder(builder);
        (bundle, meta.output_action_index(0).unwrap())
    }

    /// An Ironwood bundle in which a stranger spends a 30_000 note to pay
    /// `recipient` 20_000, leaving 10_000 as the fee.
    fn stranger_ironwood_payment(
        recipient: orchard::Address,
    ) -> orchard::Bundle<OrchardAuthorized, ZatBalance> {
        use incrementalmerkletree::{Hashable, Level};
        use orchard::keys::{FullViewingKey, Scope, SpendingKey};
        use orchard::note::{RandomSeed, Rho};
        use orchard::tree::{MerkleHashOrchard, MerklePath};

        let version = BundleVersion::ironwood_v3();
        let fvk = FullViewingKey::from(&SpendingKey::from_bytes([8; 32]).unwrap());
        let rho = Rho::from_bytes(&[0; 32]).unwrap();
        let note = orchard::Note::from_parts(
            fvk.address_at(0u32, Scope::External),
            NoteValue::from_raw(30_000),
            rho,
            RandomSeed::from_bytes([1; 32], &rho).unwrap(),
            version.note_version(),
        )
        .unwrap();
        let path = MerklePath::from_parts(
            0,
            std::array::from_fn(|level| MerkleHashOrchard::empty_root(Level::from(level as u8))),
        );
        let mut builder = OrchardBuilder::new(
            BundleType::DEFAULT,
            version,
            version.default_flags(),
            path.root(note.commitment().into()),
        )
        .unwrap();
        builder.add_spend(fvk, note, path).unwrap();
        builder
            .add_output(None, recipient, NoteValue::from_raw(20_000), [0; 512])
            .unwrap();
        authorize_placeholder(builder).0
    }

    fn authorize_placeholder(
        builder: OrchardBuilder,
    ) -> (
        orchard::Bundle<OrchardAuthorized, ZatBalance>,
        orchard::builder::BundleMetadata,
    ) {
        let mut rng = ChaCha20Rng::from_seed([3; 32]);
        let (bundle, meta) = builder.build::<ZatBalance>(&mut rng).unwrap().unwrap();
        let proof_len = Proof::expected_proof_size(bundle.actions().len());
        let bundle = bundle.map_authorization(
            &mut rng,
            |_, _, _| Signature::from([0; 64]),
            |_, _| {
                OrchardAuthorized::from_parts(
                    Proof::new(vec![0; proof_len]),
                    Signature::from([0; 64]),
                )
            },
        );
        (bundle, meta)
    }

    /// A transaction with no transparent part.
    fn shielded_transaction(
        ironwood: orchard::Bundle<OrchardAuthorized, ZatBalance>,
    ) -> Transaction {
        TransactionData::<zcash_primitives::transaction::Authorized>::from_parts_v6(
            BranchId::Nu6_3,
            0,
            BlockHeight::from_u32(MINED + 40),
            None,
            None,
            None,
            Some(ironwood),
        )
        .freeze()
        .unwrap()
    }

    fn transaction(
        input: OutPoint,
        outputs: Vec<TxOut>,
        ironwood: Option<orchard::Bundle<OrchardAuthorized, ZatBalance>>,
    ) -> Transaction {
        let transparent = Bundle {
            vin: vec![TxIn::from_parts(input, Script::default(), u32::MAX)],
            vout: outputs,
            authorization: Authorized,
        };
        TransactionData::<zcash_primitives::transaction::Authorized>::from_parts_v6(
            BranchId::Nu6_3,
            0,
            BlockHeight::from_u32(MINED + 40),
            Some(transparent),
            None,
            None,
            ironwood,
        )
        .freeze()
        .unwrap()
    }

    fn pay(address: &TransparentAddress) -> TxOut {
        TxOut::new(Zatoshis::const_from_u64(20_000), address.script().into())
    }

    #[test]
    fn enhanced_transparent_payment_to_a_sibling_names_its_t_address() {
        let mut wallet = Wallet::new(2);
        let input = wallet.fund(0);
        let sibling = taddr(&wallet.accounts[1].1, 0).encode(&NET);
        let tx = transaction(input, vec![pay(&taddr(&wallet.accounts[1].1, 0))], None);
        wallet.enhance(&tx);

        assert_eq!(wallet.row(0, &tx)["recipient"], sibling);
        assert_eq!(wallet.external(0, &tx)[0]["address"], sibling);
    }

    #[test]
    fn removing_the_paid_sibling_keeps_its_t_address() {
        let mut wallet = Wallet::new(2);
        let input = wallet.fund(0);
        let sibling = taddr(&wallet.accounts[1].1, 0).encode(&NET);
        let tx = transaction(input, vec![pay(&taddr(&wallet.accounts[1].1, 0))], None);
        wallet.enhance(&tx);
        wallet.remove(1);

        // Upstream rewrote the payment to the sibling's address-table UA.
        assert!(wallet.stored_payment().0.starts_with("u1"));
        assert_eq!(wallet.row(0, &tx)["recipient"], sibling);
        assert_eq!(wallet.external(0, &tx)[0]["address"], sibling);
    }

    #[test]
    fn enhanced_shielded_payment_to_a_sibling_names_its_receive_address() {
        let mut wallet = Wallet::new(2);
        let input = wallet.fund(0);
        let receive = receive_address(&wallet.accounts[1].1);
        let (bundle, _) = ironwood_payment(*receive.orchard().unwrap());
        let tx = transaction(input, vec![], Some(bundle));
        wallet.enhance(&tx);
        let expected = receive.encode(&NET);

        // Upstream stores the sibling's address-table UA, which adds receivers
        // the payer never saw.
        assert_ne!(wallet.stored_payment().0, expected);
        assert_eq!(wallet.row(0, &tx)["recipient"], expected);
        assert_eq!(wallet.external(0, &tx)[0]["address"], expected);

        wallet.remove(1);
        assert_eq!(wallet.row(0, &tx)["recipient"], expected);
        assert_eq!(wallet.external(0, &tx)[0]["address"], expected);
    }

    #[test]
    fn a_local_send_keeps_an_entered_address_unless_it_has_the_address_table_shape() {
        use ReceiverRequirement::{Omit, Require};
        let payee = full_ufvk(&[4; 32], 0);
        let address = |request| payee.default_address(request).unwrap().0.encode(&NET);
        let all = address(UnifiedAddressRequest::unsafe_custom(
            Require, Require, Require,
        ));
        let orchard_only = address(UnifiedAddressRequest::ORCHARD);
        let orchard_p2pkh = address(UnifiedAddressRequest::unsafe_custom(Require, Omit, Require));
        for (entered, shown) in [
            (all.clone(), all),
            (orchard_only.clone(), orchard_only.clone()),
            // The shape upstream substitutes for a sibling account's address.
            (orchard_p2pkh, orchard_only),
        ] {
            let mut wallet = Wallet::new(1);
            let input = wallet.fund(0);
            let (bundle, index) = ironwood_payment(receive_orchard(&entered));
            let tx = transaction(input.clone(), vec![], Some(bundle));
            wallet.record_local_send(&tx, input, index, &entered);

            assert_eq!(wallet.row(0, &tx)["recipient"], shown);
            assert_eq!(wallet.external(0, &tx)[0]["address"], shown);
        }
    }

    #[test]
    fn a_local_send_to_a_removed_sibling_names_its_receive_address() {
        let mut wallet = Wallet::new(2);
        let input = wallet.fund(0);
        let receive = receive_address(&wallet.accounts[1].1).encode(&NET);
        let (bundle, index) = ironwood_payment(receive_orchard(&receive));
        let tx = transaction(input.clone(), vec![], Some(bundle));
        wallet.record_local_send(&tx, input, index, &receive);
        assert_eq!(wallet.stored_payment(), (receive.clone(), false));
        // Status observation fetches the sent transaction and stores it again,
        // which links the payment to the sibling.
        wallet.enhance(&tx);
        assert_eq!(wallet.stored_payment(), (receive.clone(), true));
        // Removing the sibling rewrites it to an address-table UA.
        wallet.remove(1);
        let (stored, _) = wallet.stored_payment();
        assert_ne!(stored, receive);
        let (_, rewritten) = unified::Address::decode(&stored).unwrap();
        let mut kinds = rewritten
            .items()
            .iter()
            .map(|receiver| match receiver {
                unified::Receiver::Orchard(_) => "orchard",
                unified::Receiver::P2pkh(_) => "p2pkh",
                _ => "other",
            })
            .collect::<Vec<_>>();
        kinds.sort_unstable();
        assert_eq!(kinds, ["orchard", "p2pkh"]);

        assert_eq!(wallet.row(0, &tx)["recipient"], receive);
        assert_eq!(wallet.external(0, &tx)[0]["address"], receive);
    }

    #[test]
    fn the_first_external_output_is_the_recipient() {
        let mut wallet = Wallet::new(1);
        let input = wallet.fund(0);
        let payee = ufvk(&[4; 32], 0);
        let first = taddr(&payee, 7);
        let second = taddr(&payee, 3);
        let tx = transaction(input, vec![pay(&first), pay(&second)], None);
        wallet.enhance(&tx);

        let row = wallet.row(0, &tx);
        assert_eq!(row["externalOutputCount"], 2);
        assert_eq!(row["recipient"], first.encode(&NET));
    }

    #[test]
    fn an_enhanced_shielded_receive_reports_the_fee_its_sender_paid() {
        let mut wallet = Wallet::new(1);
        let receive = receive_address(&wallet.accounts[0].1);
        let tx = shielded_transaction(stranger_ironwood_payment(*receive.orchard().unwrap()));
        wallet.enhance(&tx);

        let row = wallet.row(0, &tx);
        assert_eq!(row["totalReceivedZat"], 20_000);
        assert_eq!(row["feeZat"], 10_000);
    }

    #[test]
    fn a_receive_spending_an_unknown_transparent_input_has_no_fee() {
        let mut wallet = Wallet::new(1);
        let receive = receive_address(&wallet.accounts[0].1);
        let (bundle, _) = ironwood_payment(*receive.orchard().unwrap());
        // Not the wallet's output: its value, and so the fee, is unknown.
        let tx = transaction(OutPoint::new([6; 32], 0), vec![], Some(bundle));
        wallet.enhance(&tx);

        let row = wallet.row(0, &tx);
        assert_eq!(row["totalReceivedZat"], 20_000);
        assert_eq!(row["feeZat"], json!(null));
    }

    #[test]
    fn unparseable_or_plain_addresses_are_returned_unchanged() {
        for pool in [0, 3, 4] {
            assert_eq!(paid_address("synthetic", pool, false), "synthetic");
        }
        let t = taddr(&ufvk(&[4; 32], 0), 0).encode(&NET);
        assert_eq!(paid_address(&t, 0, false), t);
        let receive = receive_address(&ufvk(&[4; 32], 0)).encode(&NET);
        assert_eq!(paid_address(&receive, 4, false), receive);
    }
}
