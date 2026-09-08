//! 交易历史。
//!
//! ## 为什么直接查视图
//!
//! `zcash_client_backend` 的 `WalletRead::get_tx_history` 上游明确标注为 **test-only**
//! （「production use could return a very large number of results; either pagination or a
//! streaming design will be necessary to stabilize this feature」），返回类型还在
//! `testing` 模块下。不能拿它做生产接口。
//!
//! 所以走 `zcash_client_sqlite` 维护的 `v_transactions` 视图 —— 这也是官方
//! Android / iOS SDK 的做法。视图由上游随 schema 一起演进，我们只读不改。
//!
//! ## 耦合风险
//!
//! 直接查视图意味着依赖它的列名。升级 `zcash_client_sqlite` 时列可能变化，
//! 而那**不会**在 Rust 编译期报错，只会在运行时查询失败。因此：
//!
//! - 本模块的查询列表集中在一处，升级时先跑一次 `history_smoke` 测试；
//! - 出错时抛 `DATABASE_ERROR` 且 `params.operation = "history"`，便于定位。
//!
//! ## 边界
//!
//! 这里只做**取数与分页**，不做展示模型 —— 「这笔算收入还是支出」「显示成什么文案」
//! 属于宿主。返回的是中性事实：金额增量、是否找零、note 计数、时间戳。

use serde_json::json;

use crate::error::{ErrorCode, Result, RuntimeError};

/// 账户 UUID 在官方 schema 里是 **BLOB**（16 字节原始值），不是文本。
///
/// 用 36 字符的连字符形式去绑定，SQLite 不会报错，只会**一行都匹配不上** ——
/// 于是历史、计数、详情全部恒空，而且看起来像「还没同步出数据」。
/// 这个坑代价很高：坏查询和空钱包的返回值完全一样，测试很容易假绿。
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

/// 每页最多返回多少条，防止宿主一次拉爆内存。
pub const MAX_PAGE_SIZE: u32 = 500;

fn db_err(e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": "history" })).detail(e)
}

/// Whether deleting this account would discard a locally created outgoing
/// transaction that has not reached a terminal chain state yet.
pub fn has_unsettled_outgoing(conn: &rusqlite::Connection, account_uuid: &str) -> Result<bool> {
    conn.query_row(
        "SELECT EXISTS(
           SELECT 1
           FROM v_transactions
           WHERE account_uuid = ?1
             AND mined_height IS NULL
             AND expired_unmined = 0
             AND COALESCE(total_spent, 0) > 0
         )",
        rusqlite::params![uuid_blob(account_uuid)?],
        |row| row.get(0),
    )
    .map_err(db_err)
}

// Keep the list and detail endpoints on the exact same projection. `row_to_json`
// intentionally has one shape; adding a field to only one query would otherwise
// compile and then fail when the other endpoint reads a missing column at runtime.
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
         tx_state.broadcast_state,
         COALESCE(
           tx_state.recipient,
           (SELECT output.to_address
            FROM v_tx_outputs output
            WHERE output.txid = tx_summary.txid
              AND output.from_account_uuid = tx_summary.account_uuid
              AND output.to_account_uuid IS NULL
              AND COALESCE(output.is_change, 0) = 0
            LIMIT 1)
         ) AS recipient,
         (SELECT COUNT(*)
            FROM v_tx_outputs output
           WHERE output.txid = tx_summary.txid
             AND output.from_account_uuid = tx_summary.account_uuid
             AND output.to_account_uuid IS NULL
             AND COALESCE(output.is_change, 0) = 0
         ) AS external_output_count
     FROM v_transactions tx_summary
     JOIN transactions chain_tx ON chain_tx.txid = tx_summary.txid
     LEFT JOIN ext_onekey_tx_state tx_state
       ON tx_state.txid = tx_summary.txid
      AND tx_state.account_uuid = tx_summary.account_uuid";

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

/// 分页读取某账户的交易历史，返回 JSON 数组。
///
/// 排序：未上链的排在最前（`mined_height IS NULL`），其余按高度倒序。
/// 这样用户刚发出的交易立刻可见。
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

/// 一行 `v_transactions` → JSON。列表与详情共用，保证两处字段完全一致。
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
        // txid 以显示序（大端）给出，与区块浏览器一致。
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
        // 本账户在这笔交易里的方向性金额（视图现成列）。宿主用它们分类与
        // 显示：sent 的显示金额 = spent - received - fee（不含手续费的净额），
        // 自转/屏蔽的显示金额 = received —— 直接用 balance_delta 会把屏蔽
        // 显示成一笔「手续费大小」的交易。
        "totalSpentZat": row.get::<_, Option<i64>>(12)?,
        "totalReceivedZat": row.get::<_, Option<i64>>(13)?,
        // Preserve exact upstream pool identifiers. The host may aggregate
        // them for today's UI, but a future multi-pool UI must not need a
        // rescan to recover information discarded here.
        "poolIds": pool_ids,
        // Signed account-owned value change for each exact pool. This cannot be
        // recovered from the aggregate row after an Orchard-to-Ironwood move.
        "perPoolBalanceDeltaZat": per_pool_balance_delta_zat,
        "broadcastState": row.get::<_, Option<String>>(15)?,
        "recipient": row.get::<_, Option<String>>(16)?,
        // 本账户发出、且不属于本账户任何地址的输出数。为 0 且有花费 = 内部
        // 转移（屏蔽 / 提到自己的透明地址 / 池间搬家），与 fee 是否已知无关 ——
        // fee 未知时靠 spent-received 判方向会把自转误判成外发。
        "externalOutputCount": row.get::<_, i64>(17)?,
    }))
}

/// 单笔交易详情。查不到返回 `null`（JSON），不抛错 ——
/// 「这笔不在钱包里」是正常状态，不是故障：外部 txid、已被回滚的交易都会走到这里。
pub fn details(conn: &rusqlite::Connection, account_uuid: &str, txid: &str) -> Result<String> {
    // 库里 txid 存的是内部序（小端），入参是显示序（大端），先翻回去。
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

/// 单笔交易的**逐笔明细**：本账户被花掉的、收到的，以及发给外部的输出。
///
/// 数据来自 `v_tx_outputs` —— `zcash_client_sqlite` 维护的每输出一行的视图，
/// 官方 SDK 取交易详情走的也是它。三段的判定规则：
///
/// - `received`：`to_account_uuid` 是本账户（`is_change` 区分找零与真收款）
/// - `external`：`from_account_uuid` 是本账户、而 `to_account_uuid` 为空 ——
///   即我们付给别人的输出。**只有自己发的交易才看得到收款地址**，
///   别人发给别人的输出我们既解不开也不该出现在这里
/// - `spent`：本账户在更早交易里收到、且被这笔花掉的输出，走 `v_received_output_spends`
///
/// 池号沿用上游编码：0=transparent, 2=sapling, 3=orchard, 4=ironwood。
/// 这里原样透出数字并附字符串名，宿主不必自己记这套映射。
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
                "SELECT output_pool, value, is_change, memo, to_address,
                        to_account_uuid, from_account_uuid
                 FROM v_tx_outputs
                 WHERE txid = ?1",
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
            // 同理：读出来也是 BLOB，按 String 读会直接报列类型错误
            let to_acct: Option<Vec<u8>> = row.get(5).map_err(db_err)?;
            let from_acct: Option<Vec<u8>> = row.get(6).map_err(db_err)?;

            if pool == 2 {
                continue;
            }

            if to_acct.as_deref() == Some(me.as_slice()) {
                received.push(json!({
                    "valueZat": value,
                    "pool": pool,
                    "poolName": pool_name(pool),
                    "isChange": is_change,
                    "memo": memo.as_deref().and_then(decode_memo),
                    "address": address,
                }));
            } else if from_acct.as_deref() == Some(me.as_slice()) {
                external.push(json!({
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
        // 本账户收到的输出中，被这笔交易花掉的那些。
        // 连接键是 (pool, id_within_pool_table) ↔ (pool, received_output_id)。
        // `v_received_output_spends` 里没有 output_index，别拿它当连接键。
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

/// 上游的池编码。数字是存储层的事实，名字是给宿主省一次查表。
fn pool_name(pool: i64) -> &'static str {
    match pool {
        0 => "transparent",
        2 => "sapling",
        3 => "orchard",
        4 => "ironwood",
        _ => "unknown",
    }
}

/// memo 里全零表示「没有 memo」，0xF6 开头是协议保留，都不该当文本给出去。
fn decode_memo(bytes: &[u8]) -> Option<String> {
    if bytes.is_empty() || bytes.iter().all(|b| *b == 0) || bytes[0] == 0xF6 {
        return None;
    }
    let end = bytes.iter().rposition(|b| *b != 0).map_or(0, |i| i + 1);
    String::from_utf8(bytes[..end].to_vec()).ok()
}

/// 显示序（大端 hex）→ 内部序（小端字节）。
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

fn hex_decode(s: &str) -> Option<Vec<u8>> {
    if s.len() % 2 != 0 {
        return None;
    }
    (0..s.len())
        .step_by(2)
        .map(|i| u8::from_str_radix(&s[i..i + 2], 16).ok())
        .collect()
}

/// txid 在库里以内部序（小端）存储，浏览器展示用大端。翻转后转 hex。
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

    #[test]
    fn txid_is_reversed_for_display() {
        assert_eq!(hex_display_order(&[0x01, 0x02, 0x03]), "030201");
        assert_eq!(hex_display_order(&[]), "");
    }

    #[test]
    fn rejects_bad_page_size() {
        // 用一个内存库即可，查询根本不会执行到。
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
                .with_external_migrations(vec![Box::new(crate::tx_state::Migration)])
                .init_or_migrate(&mut db)
                .unwrap();
        }

        let account_uuid = uuid::Uuid::nil().to_string();
        assert_eq!(list(&conn, &account_uuid, 20, 0).unwrap(), "[]");
        assert_eq!(
            details(&conn, &account_uuid, &"00".repeat(32)).unwrap(),
            "null"
        );
    }

    #[test]
    fn preserves_supported_pool_ids_and_hides_sapling() {
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
               txid BLOB NOT NULL,
               from_account_uuid BLOB,
               to_account_uuid BLOB,
               to_address TEXT,
               is_change INTEGER
             );
             CREATE TABLE ext_onekey_tx_state (
               txid BLOB PRIMARY KEY NOT NULL,
               account_uuid BLOB NOT NULL,
               reservation_id TEXT UNIQUE,
               recipient TEXT,
               broadcast_state TEXT NOT NULL,
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
}
