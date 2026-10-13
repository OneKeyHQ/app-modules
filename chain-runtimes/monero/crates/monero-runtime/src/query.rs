//! 余额、历史与可花输出的查询。
//!
//! 这一层是选 SQLite 而非内存方案的**直接兑现**：分页、按高度筛选、按子地址
//! 聚合都是索引查询，不是在内存对象上过滤。历史再长也不需要把全部数据读进来。

use monero_wallet::WalletOutput;
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::wallet::Db;

/// Monero 协议对**所有**输出的固定锁定期：10 个确认。
///
/// 这不是我们的策略，是共识规则 —— 未满 10 块的输出构不进环，节点会拒。
/// 额外时间锁（`additional_timelock`）是在这之上的附加约束。
pub const COINBASE_AND_STANDARD_LOCK: u64 = 10;

#[derive(Debug, Default, Clone)]
pub struct Balance {
    /// 全部未花输出之和。
    pub total_atomic: u128,
    /// 其中当前可花的部分（已过 10 块锁定且额外时间锁已满足，且未被预留）。
    pub unlocked_atomic: u128,
    /// 被在途交易预留的部分。已计入 total，但不可再次选中。
    pub locked_by_reservation_atomic: u128,
    /// 待核实：还没有 key image、又出现在某个环里的输出金额。**这部分可能已经
    /// 被花掉**，要等宿主补算 key image 才知道；从没进过环的一定没花，不算在内。
    pub unverifiable_atomic: u128,
    /// 自己发出、还没上链的交易的找零。已计入 total（锁定，不可花），wallet2 同款。
    pub pending_change_atomic: u128,
    /// 还没有 key image 的未花输出笔数。大于 0 时发送前要先补算 key image
    /// （选币只选有 key image 的输出）。
    pub outputs_without_key_image: u64,
}

impl Balance {
    pub fn to_json(&self) -> Value {
        // 金额一律用字符串跨边界：piconero 的上限超过 JS 的
        // Number.MAX_SAFE_INTEGER，走数字会静默丢精度。
        json!({
            "totalAtomic": self.total_atomic.to_string(),
            "unlockedAtomic": self.unlocked_atomic.to_string(),
            "lockedByReservationAtomic": self.locked_by_reservation_atomic.to_string(),
            "unverifiableAtomic": self.unverifiable_atomic.to_string(),
            "pendingChangeAtomic": self.pending_change_atomic.to_string(),
            "outputsWithoutKeyImage": self.outputs_without_key_image,
        })
    }
}

/// 算余额。`chain_tip` 用于判断锁定期；给 `None` 表示还不知道链尖，
/// 此时一律按「未解锁」处理 —— 宁可显示 0 可用，也不要让用户去花一笔构不进环的钱。
pub fn balance(db: &Db, identity_id: i64, chain_tip: Option<u64>) -> Result<Balance> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.amount_atomic, o.height, o.additional_timelock,
                    o.key_image IS NULL AS no_ki,
                    l.output_id IS NOT NULL AS reserved,
                    EXISTS (SELECT 1 FROM possible_spends p
                            WHERE p.identity_id = o.identity_id
                              AND p.out_txid = o.txid AND p.out_index = o.index_in_tx) AS maybe_spent
             FROM outputs o
             LEFT JOIN locked_outputs l ON l.output_id = o.id AND (l.locked_at > ?2 OR l.signed_at > ?3)
             WHERE o.identity_id = ?1 AND o.spent_at_height IS NULL",
        )
        .map_err(|e| db_err("balanceQuery", e))?;

    let rows = stmt
        .query_map(
            params![
                identity_id,
                crate::spend::reservation_cutoff(),
                crate::spend::signed_reservation_cutoff()
            ],
            |r| {
                Ok((
                    r.get::<_, i64>(0)? as u128,
                    r.get::<_, i64>(1)? as u64,
                    r.get::<_, i64>(2)? as u64,
                    r.get::<_, bool>(3)?,
                    r.get::<_, bool>(4)?,
                    r.get::<_, bool>(5)?,
                ))
            },
        )
        .map_err(|e| db_err("balanceQuery", e))?;

    let mut b = Balance::default();
    for row in rows {
        let (amount, height, timelock, no_key_image, reserved, maybe_spent) =
            row.map_err(|e| db_err("balanceQuery", e))?;
        b.total_atomic += amount;
        if no_key_image {
            b.outputs_without_key_image += 1;
        }
        // 没进过任何环的钱一定没花；只有进过环、又还没 key image 的才待核实。
        // 待核实的不算可用：它可能已经花掉了。
        if no_key_image && maybe_spent {
            b.unverifiable_atomic += amount;
            continue;
        }
        if reserved {
            b.locked_by_reservation_atomic += amount;
            continue;
        }
        if let Some(tip) = chain_tip {
            if is_unlocked(height, timelock, tip) {
                b.unlocked_atomic += amount;
            }
        }
    }
    // 在途交易的找零：上链扫到前不在 outputs 里，但它是用户的钱。
    let change: Vec<Option<String>> = db
        .conn()
        .prepare(
            "SELECT change_atomic FROM transactions
             WHERE identity_id = ?1 AND direction = 'out' AND height IS NULL AND failed = 0",
        )
        .and_then(|mut stmt| {
            stmt.query_map(params![identity_id], |r| r.get(0))?
                .collect::<rusqlite::Result<Vec<_>>>()
        })
        .map_err(|e| db_err("pendingChangeQuery", e))?;
    for value in change.into_iter().flatten() {
        if let Ok(amount) = value.parse::<u128>() {
            b.pending_change_atomic += amount;
        }
    }
    b.total_atomic += b.pending_change_atomic;
    Ok(b)
}

/// 一笔输出此刻能不能花。
///
/// 两个条件都要满足：过了协议固定的 10 块锁定；额外时间锁（若有）也已过。
/// `additional_timelock` 存的是原始值 —— 小于 5 亿按块高解释，否则按 Unix
/// 时间戳解释，这是 Monero 的约定（`CRYPTONOTE_MAX_BLOCK_NUMBER`）。
pub fn is_unlocked(height: u64, additional_timelock: u64, chain_tip: u64) -> bool {
    const MAX_BLOCK_NUMBER: u64 = 500_000_000;

    if chain_tip < height + COINBASE_AND_STANDARD_LOCK {
        return false;
    }
    if additional_timelock == 0 {
        return true;
    }
    if additional_timelock < MAX_BLOCK_NUMBER {
        chain_tip >= additional_timelock
    } else {
        // 时间戳形式的锁没法只靠链高判断。保守处理：**当作仍锁定**。
        // 让用户等，比让他构一笔必然被拒的交易好。
        false
    }
}

/// 一笔可花输出的**元信息**，不含反序列化后的密码学对象。
///
/// 筛选与展示只需要这些列。把 blob 的反序列化推迟到真正要构造交易的那一刻，
/// 免得为了挑两个输出把五百个都解一遍 —— 那是内存方案的毛病，不该在这里复现。
#[derive(Debug, Clone)]
pub struct SpendableRow {
    pub id: i64,
    pub amount_atomic: u64,
    pub global_index: u64,
    pub height: u64,
}

/// 取当前可花的输出元信息，金额降序（大额优先，减少输入数量从而降低手续费）。
///
/// 只返回**有 key image** 的输出：没有 key image 就无法确认它没被花掉，
/// 拿它构造交易有双花风险，节点会拒。
pub fn spendable_rows(db: &Db, identity_id: i64, chain_tip: u64) -> Result<Vec<SpendableRow>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT o.id, o.amount_atomic, o.global_index, o.height, o.additional_timelock
             FROM outputs o
             LEFT JOIN locked_outputs l ON l.output_id = o.id AND (l.locked_at > ?2 OR l.signed_at > ?3)
             WHERE o.identity_id = ?1
               AND o.spent_at_height IS NULL
               AND o.key_image IS NOT NULL
               AND l.output_id IS NULL
             ORDER BY o.amount_atomic DESC",
        )
        .map_err(|e| db_err("spendableRows", e))?;

    let rows = stmt
        .query_map(
            params![
                identity_id,
                crate::spend::reservation_cutoff(),
                crate::spend::signed_reservation_cutoff()
            ],
            |r| {
                Ok((
                    r.get::<_, i64>(0)?,
                    r.get::<_, i64>(1)? as u64,
                    r.get::<_, i64>(2)? as u64,
                    r.get::<_, i64>(3)? as u64,
                    r.get::<_, i64>(4)? as u64,
                ))
            },
        )
        .map_err(|e| db_err("spendableRows", e))?;

    let mut out = Vec::new();
    for row in rows {
        let (id, amount_atomic, global_index, height, timelock) =
            row.map_err(|e| db_err("spendableRows", e))?;
        if !is_unlocked(height, timelock, chain_tip) {
            continue;
        }
        out.push(SpendableRow {
            id,
            amount_atomic,
            global_index,
            height,
        });
    }
    Ok(out)
}

/// 把一笔输出的 blob 读回成 monero-oxide 的对象。
///
/// **参与密码学的一律走这里**。`outputs` 表上那些抽出来的列只用于筛选与展示，
/// 不是另一份真相 —— 拿它们去重建对象就是在自己实现一遍序列化，早晚出错。
///
/// blob 读不回来意味着库损坏，明确报错而不是跳过：静默跳过会表现为
/// 「这笔钱凭空消失」，比报错难查得多。
pub fn load_output(db: &Db, output_id: i64) -> Result<WalletOutput> {
    let blob: Vec<u8> = db
        .conn()
        .query_row("SELECT blob FROM outputs WHERE id = ?1", [output_id], |r| {
            r.get(0)
        })
        .optional()
        .map_err(|e| db_err("loadOutput", e))?
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "outputId", "value": output_id }),
            )
        })?;

    WalletOutput::read(&mut blob.as_slice()).map_err(|e| {
        db_err(
            "deserializeOutput",
            format!("输出 {output_id} 的 blob 读不回来: {e}"),
        )
    })
}

/// 交易历史，按高度倒序分页。
pub fn history(db: &Db, identity_id: i64, limit: u32, offset: u32) -> Result<Value> {
    // 限制上界：宿主传个巨大的 limit 会把整段历史读进内存，那正是我们选
    // SQLite 想避免的事。
    let limit = limit.clamp(1, 500);

    let mut stmt = db
        .conn()
        .prepare(
            "SELECT t.txid, t.height, t.direction,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND txid = t.txid), 0) AS received,
                    COALESCE((SELECT sum(amount_atomic) FROM outputs
                              WHERE identity_id = t.identity_id AND spent_in_txid = t.txid), 0) AS spent,
                    COALESCE(t.timestamp, b.timestamp, t.sent_at), t.failed, t.outgoing_atomic
             FROM transactions t
             LEFT JOIN blocks b ON b.identity_id = t.identity_id AND b.height = t.height
             WHERE t.identity_id = ?1
             ORDER BY t.height DESC NULLS FIRST, t.txid
             LIMIT ?2 OFFSET ?3",
        )
        .map_err(|e| db_err("history", e))?;

    let rows = stmt
        .query_map(params![identity_id, limit, offset], |r| {
            let txid: Vec<u8> = r.get(0)?;
            Ok(json!({
                "txid": hex::encode(txid),
                "height": r.get::<_, Option<i64>>(1)?,
                "direction": r.get::<_, String>(2)?,
                "receivedAtomic": r.get::<_, i64>(3)?.to_string(),
                "spentAtomic": r.get::<_, i64>(4)?.to_string(),
                "timestamp": r.get::<_, Option<i64>>(5)?,
                "failed": r.get::<_, i64>(6)? != 0,
                "outgoingAtomic": r.get::<_, Option<String>>(7)?,
            }))
        })
        .map_err(|e| db_err("history", e))?;

    let items = rows
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("history", e))?;

    let total: i64 = db
        .conn()
        .query_row(
            "SELECT count(*) FROM transactions WHERE identity_id = ?1",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("historyCount", e))?;

    Ok(json!({ "items": items, "total": total, "limit": limit, "offset": offset }))
}

/// 单笔交易详情。
pub fn transaction_details(db: &Db, identity_id: i64, txid_hex: &str) -> Result<Value> {
    let txid = hex::decode(txid_hex).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "txid", "reason": "notHex" }),
        )
    })?;
    if txid.len() != 32 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "txid", "byteLen": txid.len(), "expected": 32 }),
        ));
    }

    let row = db
        .conn()
        .query_row(
            "SELECT t.height, t.direction, COALESCE(t.timestamp, b.timestamp, t.sent_at)
             FROM transactions t LEFT JOIN blocks b ON b.identity_id = t.identity_id AND b.height = t.height
             WHERE t.identity_id = ?1 AND t.txid = ?2",
            params![identity_id, &txid],
            |r| {
                Ok((
                    r.get::<_, Option<i64>>(0)?,
                    r.get::<_, String>(1)?,
                    r.get::<_, Option<i64>>(2)?,
                ))
            },
        )
        .optional()
        .map_err(|e| db_err("transactionDetails", e))?;

    let Some((height, direction, timestamp)) = row else {
        return Ok(Value::Null);
    };

    let mut stmt = db
        .conn()
        .prepare(
            "SELECT amount_atomic, subaddress_major, subaddress_minor, index_in_tx
             FROM outputs WHERE identity_id = ?1 AND txid = ?2 ORDER BY index_in_tx",
        )
        .map_err(|e| db_err("transactionOutputs", e))?;
    let received = stmt
        .query_map(params![identity_id, &txid], |r| {
            Ok(json!({
                "amountAtomic": r.get::<_, i64>(0)?.to_string(),
                "subaddressMajor": r.get::<_, Option<i64>>(1)?,
                "subaddressMinor": r.get::<_, Option<i64>>(2)?,
                "indexInTransaction": r.get::<_, i64>(3)?,
            }))
        })
        .map_err(|e| db_err("transactionOutputs", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("transactionOutputs", e))?;

    Ok(json!({
        "txid": txid_hex,
        "height": height,
        "direction": direction,
        "timestamp": timestamp,
        "received": received,
    }))
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    fn add_output(
        db: &Db,
        identity_id: i64,
        amount: i64,
        height: i64,
        with_key_image: bool,
    ) -> i64 {
        let ki: Option<Vec<u8>> = with_key_image.then(|| {
            let mut k = vec![0u8; 32];
            // 每个输出一个不同的 key image，免得唯一性问题掩盖 bug
            k[0] = (amount % 251) as u8;
            k[1] = (height % 251) as u8;
            k
        });
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image)
                 VALUES (?1, x'00', ?2, ?3, 0, ?4, ?5, ?6)",
                params![
                    identity_id,
                    &vec![height as u8; 32],
                    height,
                    amount,
                    height,
                    ki
                ],
            )
            .unwrap();
        db.conn().last_insert_rowid()
    }

    /// 10 块锁定是共识规则，不是我们的策略 —— 未满 10 块的输出构不进环。
    #[test]
    fn the_ten_block_lock_is_enforced() {
        assert!(!is_unlocked(100, 0, 100), "同高度必然锁定");
        assert!(!is_unlocked(100, 0, 109), "差一块也是锁定");
        assert!(is_unlocked(100, 0, 110), "满 10 块解锁");
    }

    #[test]
    fn additional_block_timelock_is_honoured() {
        // 额外锁到 200 块：即使过了 10 块锁也还不能花
        assert!(!is_unlocked(100, 200, 150));
        assert!(is_unlocked(100, 200, 200));
    }

    /// 时间戳形式的时间锁光靠链高判断不了。保守当作仍锁定 —— 让用户等，
    /// 好过让他构一笔必然被节点拒绝的交易。
    #[test]
    fn timestamp_timelocks_are_treated_as_locked() {
        assert!(!is_unlocked(100, 1_700_000_000, 999_999));
    }

    /// 不知道链尖时可用余额必须是 0，不能乐观放行。
    #[test]
    fn unknown_chain_tip_means_nothing_is_spendable() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 1_000, 10, true);
        let b = balance(&db, id, None).unwrap();
        assert_eq!(b.total_atomic, 1_000);
        assert_eq!(b.unlocked_atomic, 0);
    }

    /// 没 key image、又进过某个环的输出**可能已经被花掉**，必须单独报出来；
    /// 从没进过环的一定没花（花它必然要把它放进环），不能算成待核实。
    #[test]
    fn only_outputs_seen_in_a_ring_are_pending_verification() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 700, 10, true);
        add_output(&db, id, 300, 11, false);
        add_output(&db, id, 200, 12, false);
        db.conn()
            .execute(
                "INSERT INTO possible_spends
                   (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                 VALUES (?1, ?2, 0, ?3, ?4, 20, 0)",
                params![id, vec![12u8; 32], vec![9u8; 32], vec![8u8; 32]],
            )
            .unwrap();
        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 1_200);
        assert_eq!(b.unverifiable_atomic, 200);
        assert_eq!(b.unlocked_atomic, 1_000, "可能已花的不算可用");
    }

    /// 被在途交易预留的输出仍计入总额，但不可再次选中 —— 不锁会让同一笔
    /// 输出被两笔交易同时花掉，第二笔必然被网络拒绝。
    #[test]
    fn reserved_outputs_are_excluded_from_unlocked_and_from_selection() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        add_output(&db, id, 400, 11, true);
        db.conn()
            .execute(
                "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, 'r', ?2)",
                params![a, crate::wallet::now_secs()],
            )
            .unwrap();

        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 900);
        assert_eq!(b.locked_by_reservation_atomic, 500);
        assert_eq!(b.unlocked_atomic, 400);

        let spendable = spendable_rows(&db, id, 100).unwrap();
        assert_eq!(spendable.len(), 1, "被预留的不能再被选中");
        assert_eq!(spendable[0].amount_atomic, 400);
    }

    /// 用户在确认页退出不会通知 runtime；过期的预留不能永远锁着钱。
    #[test]
    fn an_expired_reservation_no_longer_holds_its_outputs() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        db.conn()
            .execute(
                "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, 'r', ?2)",
                params![a, crate::spend::reservation_cutoff() - 1],
            )
            .unwrap();

        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.locked_by_reservation_atomic, 0);
        assert_eq!(b.unlocked_atomic, 500);
        assert_eq!(spendable_rows(&db, id, 100).unwrap().len(), 1);
    }

    /// 已花的输出不再计入任何口径。
    #[test]
    fn spent_outputs_leave_the_balance() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        db.conn()
            .execute("UPDATE outputs SET spent_at_height = 20 WHERE id = ?1", [a])
            .unwrap();
        let b = balance(&db, id, Some(100)).unwrap();
        assert_eq!(b.total_atomic, 0);
    }

    /// 自己广播还没上链的：时间取广播时间（不然每次读都是「现在」），
    /// 金额取广播时记下的，失败要标出来。
    #[test]
    fn a_pending_send_reports_its_broadcast_time_amount_and_failure() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO transactions
                   (identity_id, txid, height, direction, sent_at, failed, outgoing_atomic)
                 VALUES (?1, ?2, NULL, 'out', 1700000000, 1, '1234')",
                params![id, vec![5u8; 32]],
            )
            .unwrap();
        let item = &history(&db, id, 10, 0).unwrap()["items"][0];
        assert_eq!(item["timestamp"], 1_700_000_000);
        assert_eq!(item["failed"], true);
        assert_eq!(item["outgoingAtomic"], "1234");
    }

    /// 历史分页的上界必须有硬限制 —— 宿主传个巨大的 limit 会把整段历史读进
    /// 内存，那正是选 SQLite 要避免的。
    #[test]
    fn history_limit_is_clamped() {
        let (db, id) = db_with_identity();
        let v = history(&db, id, 99_999, 0).unwrap();
        assert_eq!(v["limit"], 500);
        let v = history(&db, id, 0, 0).unwrap();
        assert_eq!(v["limit"], 1);
    }

    #[test]
    fn transaction_details_rejects_malformed_txids() {
        let (db, id) = db_with_identity();
        let e = transaction_details(&db, id, "zzzz").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        let e = transaction_details(&db, id, "00ff").unwrap_err();
        assert_eq!(e.params["byteLen"], 2);
    }

    #[test]
    fn unknown_transaction_returns_null_not_an_error() {
        let (db, id) = db_with_identity();
        let v = transaction_details(&db, id, &"ab".repeat(32)).unwrap();
        assert!(v.is_null());
    }
}
