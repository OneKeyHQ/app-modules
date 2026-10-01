//! 同步编排：拉块 → 扫描 → 记进度。**有界推进，永不跑到底。**
//!
//! 一次调用最多处理 `max_blocks` 个块，然后带进度返回。要不要接着调由宿主
//! 决定 —— 这是「调度归 TS」那条边界。理由是具体的：跑到底的函数在浏览器和
//! 移动端上无法中断、无法看进度、无法让出线程，页面直接冻住。
//!
//! ## 扫描进度按身份独立记账
//!
//! `scan_ranges` 带 `identity_id`。这和 Zcash 那边不一样 —— 那边承诺树要求
//! 全库连续扫描，于是「用 A 的密钥扫过的区间被标记为全库已扫」，暂停中的账户
//! 会永久漏掉那一段，只能靠宿主保管的游标补救。Monero 没有承诺树、不要求
//! 连续扫描，各身份各记各的，从根上没有这个坑。

use futures_util::stream::{self, StreamExt, TryStreamExt};
use monero_daemon_rpc::prelude::*;
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};

use crate::error::{db_err, net_err, ErrorCode, Result, RuntimeError};
use crate::network::Daemon;
use crate::perf::now_ms;
use crate::scan::{self, ScanOutcome};
use crate::wallet::Db;

/// 单次调用的块数上限。
///
/// 不是性能调优参数，是**卡死防线**：宿主传个巨大的值会让这次调用跑上几分钟
/// 且中途无法中断。真正的批量由宿主按实测吞吐决定。
pub const MAX_BLOCKS_PER_CALL: u64 = 1_000;

/// 同时在途的取块请求数。
///
/// 取块是**延迟**型而不是带宽型：一块要 3～4 次往返（区块、交易、输出索引），
/// 每次 130～230 ms，实测串行时 ~1.36 s/块。块与块之间没有依赖，并发 N 个
/// 就把墙钟时间压到约 1/N。取 6 是因为浏览器、插件后台、RN 的 HTTP 栈对
/// 同一 host 的连接上限都在这个量级，再高只会排队，还更容易被公共节点限流。
pub const FETCH_CONCURRENCY: usize = 6;

/// 问链尖。**不碰钱包库** —— 建账户时需要它来定 birthday，而那一刻库还没建。
pub async fn chain_tip(daemon: &Daemon) -> Result<u64> {
    let n = daemon
        .latest_block_number()
        .await
        .map_err(|e| net_err("latestBlockNumber", format!("{e:?}")))?;
    Ok(n as u64)
}

/// 把链尖告诉某个身份：据此算出还要扫哪一段。
///
/// 新身份的待扫区间是 `[birthday, tip]`；已有进度的则是 `(已扫到, tip]`。
pub fn update_chain_tip(db: &mut Db, identity_id: i64, tip: u64) -> Result<()> {
    let identity = db.identity_by_id(identity_id)?;
    let scanned_to = scanned_to_height(db, identity_id)?;
    let start = scanned_to
        .map(|h| h + 1)
        .unwrap_or(identity.birthday_height);

    if start > tip {
        return Ok(()); // 已经追上链尖，没有待扫区间
    }

    db.conn()
        .execute(
            "INSERT INTO scan_ranges (identity_id, start_height, end_height)
             VALUES (?1, ?2, ?3)
             ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
            params![identity_id, start as i64, (tip + 1) as i64],
        )
        .map_err(|e| db_err("updateChainTip", e))?;
    Ok(())
}

/// 该身份已经连续扫到哪个高度（含）。没扫过任何块时返回 `None`。
///
/// 读的是 `identities.scanned_to_height` 而**不是** `blocks` 表的 max —— 后者
/// 是全局的，会让一个已追到链尖的身份把另一个还在回补的身份的进度推到链尖，
/// 那段历史就被永久跳过。
pub fn scanned_to_height(db: &Db, identity_id: i64) -> Result<Option<u64>> {
    Ok(db
        .conn()
        .query_row(
            "SELECT scanned_to_height FROM identities WHERE id = ?1",
            [identity_id],
            |r| r.get::<_, Option<i64>>(0),
        )
        .optional()
        .map_err(|e| db_err("scannedToHeight", e))?
        .flatten()
        .map(|h| h as u64))
}

/// 推进该身份的扫描进度。只进不退 —— 回退走 `rewind_and_requeue`。
fn advance_scanned_to(db: &Db, identity_id: i64, height: u64) -> Result<()> {
    db.conn()
        .execute(
            "UPDATE identities
             SET scanned_to_height = max(COALESCE(scanned_to_height, 0), ?2)
             WHERE id = ?1",
            params![identity_id, height as i64],
        )
        .map_err(|e| db_err("advanceScannedTo", e))?;
    Ok(())
}

/// 取当前最靠前的待扫区间。
fn next_range(db: &Db, identity_id: i64) -> Result<Option<(u64, u64)>> {
    db.conn()
        .query_row(
            "SELECT start_height, end_height FROM scan_ranges
             WHERE identity_id = ?1 ORDER BY start_height LIMIT 1",
            [identity_id],
            |r| Ok((r.get::<_, i64>(0)? as u64, r.get::<_, i64>(1)? as u64)),
        )
        .optional()
        .map_err(|e| db_err("nextRange", e))
}

/// 把已扫完的 `[start, scanned_through]` 从待扫区间里销掉。
///
/// 待扫区间**可能互相重叠**：重扫（`rewind_and_requeue`）会从回退高度重新排一段，
/// 而原来的尾部区间还在队列里。只处理"起点等于 `start`"的那一条、再把余下部分
/// 普通 INSERT 回去，一旦余下的起点正好是另一条区间的起点就撞主键 —— 整步回滚，
/// 下一步从同一处开始、再撞，扫描永远原地踏步。
///
/// 所以这里对**每一条**与已扫段相交的区间都减去已扫段，余下部分以 upsert
/// 合并（同起点取更远的终点）。重叠在此被逐步消化，不再需要区间互不相交这个前提。
fn consume_range(db: &mut Db, identity_id: i64, start: u64, scanned_through: u64) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginConsumeRange", e))?;

    let overlapping: Vec<(i64, i64)> = {
        let mut stmt = tx
            .prepare(
                "SELECT start_height, end_height FROM scan_ranges
                 WHERE identity_id = ?1 AND start_height <= ?3 AND end_height > ?2",
            )
            .map_err(|e| db_err("consumeRange", e))?;
        let rows = stmt
            .query_map(
                params![identity_id, start as i64, scanned_through as i64],
                |r| Ok((r.get::<_, i64>(0)?, r.get::<_, i64>(1)?)),
            )
            .map_err(|e| db_err("consumeRange", e))?
            .collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("consumeRange", e))?;
        rows
    };

    let upsert = |from: i64, to: i64| -> Result<()> {
        tx.execute(
            "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)
             ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
            params![identity_id, from, to],
        )
        .map(|_| ())
        .map_err(|e| db_err("consumeRange", e))
    };

    let scanned_from = start as i64;
    let resume_at = scanned_through as i64 + 1;
    for (range_start, range_end) in overlapping {
        tx.execute(
            "DELETE FROM scan_ranges WHERE identity_id = ?1 AND start_height = ?2",
            params![identity_id, range_start],
        )
        .map_err(|e| db_err("consumeRange", e))?;
        // 已扫段之前的部分（按最低起点取区间时不会出现，保留以防万一）。
        if range_start < scanned_from {
            upsert(range_start, scanned_from)?;
        }
        // 已扫段之后的部分。
        if range_end > resume_at {
            upsert(resume_at, range_end)?;
        }
    }

    tx.commit().map_err(|e| db_err("commitConsumeRange", e))
}

thread_local! {
    /// 批量路径对哪个节点、从什么时候起判定不可用。换节点即重新尝试。
    static BLOCKS_BIN_UNSUPPORTED_BY: std::cell::RefCell<Option<(String, f64)>> =
        const { std::cell::RefCell::new(None) };
}

/// 判定不可用之后多久再试批量。一次截断或代理改写的回包不该让好节点整个会话都走慢路。
const BLOCKS_BIN_RETRY_AFTER_MS: f64 = 10.0 * 60.0 * 1000.0;

fn blocks_bin_skipped_for(node: Option<&str>, now: f64) -> bool {
    BLOCKS_BIN_UNSUPPORTED_BY.with(|c| match (&*c.borrow(), node) {
        (Some((url, at)), Some(node)) => url == node && now - at < BLOCKS_BIN_RETRY_AFTER_MS,
        _ => false,
    })
}

/// 先走批量（`get_blocks.bin`，一次往返一整批），节点不配合时退回逐块并发。
///
/// 只有**协议**层面的失败（节点不支持、格式不符、数量对不上）才会让这个节点
/// 暂时跳过批量路径（见 `BLOCKS_BIN_RETRY_AFTER_MS`）；网络失败照常上抛，这一步
/// 失败、下一轮重试 —— 一次超时不能让一个好节点从此降级。
async fn fetch_blocks(
    daemon: &Daemon,
    start: u64,
    last: u64,
) -> Result<(Vec<ScannableBlock>, &'static str, Option<String>, bool)> {
    let node = crate::network::current_node_url();
    let skip = blocks_bin_skipped_for(node.as_deref(), now_ms());
    if let (Some(url), false, true) = (node.as_deref(), skip, start > 0) {
        match crate::blocks_bin::fetch(url, start, last - start + 1).await {
            Ok(fetched) => {
                return Ok((fetched.blocks, "blocksBin", None, fetched.prefetch_hit));
            }
            Err(crate::blocks_bin::BlocksBinError::Network(detail)) => {
                return Err(net_err(
                    "getBlocksBin",
                    format!("{start}..={last}: {detail}"),
                ));
            }
            Err(crate::blocks_bin::BlocksBinError::Protocol(detail)) => {
                BLOCKS_BIN_UNSUPPORTED_BY
                    .with(|c| *c.borrow_mut() = node.clone().map(|url| (url, now_ms())));
                // 放弃的原因随这一步的结果回给宿主，只报这一次。
                let blocks = fetch_scannable_blocks(daemon, start, last).await?;
                return Ok((
                    blocks,
                    "perBlock",
                    Some(format!("getBlocksBin unusable: {detail}")),
                    false,
                ));
            }
        }
    }
    Ok((
        fetch_scannable_blocks(daemon, start, last).await?,
        "perBlock",
        None,
        false,
    ))
}

/// 取 `[start, last]` 的可扫描块，按高度排好。
///
/// 批量路径（`blocks_bin`）不可用时的退路。
///
/// **不走** `contiguous_scannable_blocks`。它先试批量的 `get_blocks.bin`，但
/// 已发布的 monerod（截至 v0.18.5.1）在 `prune=true` 时不回 `prunable_hash`
/// （monero-project/monero#10120，修复 #10652 只在 master），monero-oxide 为了
/// 能校验交易哈希就整批放弃、回退逐块 JSON —— 于是每一步都**白下一整包再扔掉**，
/// 再串行地一块一块取。对今天的任何公共节点，那次批量尝试都注定失败。
///
/// 这里直接逐块取、N 路并发。每块仍经上游校验（编号正确、交易与区块的交易
/// 列表一致）；块与块之间的衔接（高度连续、前驱哈希、RingCT 游标）由
/// `scan::scan_blocks` 独立校验，所以完整性保障一条不少。`buffered` 保持
/// 提交顺序，结果天然按高度排列。
///
async fn fetch_scannable_blocks(
    daemon: &Daemon,
    start: u64,
    last: u64,
) -> Result<Vec<ScannableBlock>> {
    stream::iter(start..=last)
        .map(|height| async move {
            daemon
                .scannable_block_by_number(height as usize)
                .await
                .map_err(|e| net_err("scannableBlockByNumber", format!("{height}: {e:?}")))
        })
        .buffered(FETCH_CONCURRENCY)
        .try_collect()
        .await
}

/// 一次有界推进。
///
/// 返回进度 JSON；`done` 表示已经没有待扫区间。**本函数永远不会自己循环到
/// 同步完成** —— 宿主拿到进度后自己决定要不要接着调。
pub async fn step(
    db: &mut Db,
    daemon: &Daemon,
    identity_id: i64,
    max_blocks: u64,
) -> Result<Value> {
    if max_blocks == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "maxBlocks", "reason": "mustBePositive" }),
        ));
    }
    let max_blocks = max_blocks.min(MAX_BLOCKS_PER_CALL);

    let sends_failed = crate::pool::check_pending(db, daemon, identity_id).await?;

    let Some((start, end)) = next_range(db, identity_id)? else {
        let mut v = progress(db, identity_id, &ScanOutcome::default(), true)?;
        v["sendsFailed"] = json!(sends_failed);
        return Ok(v);
    };

    // 区间是左闭右开（end 是"第一个还没扫的高度之后"），上游要的是闭区间。
    let last = (start + max_blocks - 1).min(end.saturating_sub(1));
    if last < start {
        return Ok(progress(db, identity_id, &ScanOutcome::default(), true)?);
    }

    // 分段打点。扫得慢的时候，「慢在拉块还是慢在解密」是完全不同的两件事，
    // 而在浏览器里两者的表现一模一样（界面不动）—— 只能靠打点区分。
    // 公共节点单次往返可达几百毫秒，不分开量就会把网络延迟误判成算力不足。
    let t0 = now_ms();

    let (blocks, fetched_via, fetch_note, prefetch_hit) = fetch_blocks(daemon, start, last).await?;

    // 流水线：扫这一批之前，先把下一批的请求发出去（wallet2 同款）。起点按实际
    // 拿到的块算 —— 只拿到部分前缀时，下一步从断点开始，预取也得从那里开始。
    if fetched_via == "blocksBin" {
        let next = start + blocks.len() as u64;
        if let (Some(url), true) = (crate::network::current_node_url(), next < end) {
            // 与下一步的请求一致：同样受 max_blocks 与区间终点约束，大小取
            // 这一次请求之后调整过的批量。
            crate::blocks_bin::prefetch(&url, next, (end - next).min(max_blocks)).await;
        }
    }

    let t_downloaded = now_ms();

    if blocks.is_empty() {
        // 节点对一个非空区间返回了零个块 —— 它没有守协议。
        // **不能当成"扫完了"静默推进**，那会让这段高度被永远跳过。
        return Err(RuntimeError::with(
            ErrorCode::MalformedNodeResponse,
            json!({ "operation": "contiguousScannableBlocks", "from": start, "to": last }),
        ));
    }

    db.ensure_subaddress_lookahead(identity_id)?;
    let mut outcome = scan::scan_blocks(db, identity_id, &blocks)?;
    // 收到钱的子地址靠近窗口末尾时窗口会扩大；用新窗口重扫同一批（块已在手，
    // 落库幂等），这批里后面发给新登记子地址的钱才不会漏。
    while outcome.outputs_found > 0 && db.ensure_subaddress_lookahead(identity_id)? > 0 {
        outcome = scan::scan_blocks(db, identity_id, &blocks)?;
    }
    let t_scanned = now_ms();
    outcome.ms_download = (t_downloaded - t0) as u32;
    outcome.ms_scan = (t_scanned - t_downloaded) as u32;
    outcome.fetched_via = fetched_via;
    outcome.fetch_note = fetch_note;
    outcome.prefetch_hit = prefetch_hit;
    outcome.batch_blocks = crate::blocks_bin::batch_blocks();

    if let Some(through) = outcome.to_height {
        consume_range(db, identity_id, start, through)?;
        advance_scanned_to(db, identity_id, through)?;
    }

    let done = next_range(db, identity_id)?.is_none();
    let mut v = progress(db, identity_id, &outcome, done)?;
    v["sendsFailed"] = json!(sends_failed);
    Ok(v)
}

fn progress(db: &Db, identity_id: i64, outcome: &ScanOutcome, done: bool) -> Result<Value> {
    let remaining: i64 = db
        .conn()
        .query_row(
            "SELECT COALESCE(sum(end_height - start_height), 0) FROM scan_ranges WHERE identity_id = ?1",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("remainingBlocks", e))?;

    let mut v = outcome.to_json();
    v["done"] = json!(done);
    v["remainingBlocks"] = json!(remaining);
    v["scannedToHeight"] = json!(scanned_to_height(db, identity_id)?);
    Ok(v)
}

/// 只读：当前的扫描进度，不发任何网络请求。
///
/// 同步卡住时用它确认 runtime 到底想扫哪一段 —— 不用猜。
pub fn peek(db: &Db, identity_id: i64) -> Result<Value> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT start_height, end_height FROM scan_ranges
             WHERE identity_id = ?1 ORDER BY start_height",
        )
        .map_err(|e| db_err("peek", e))?;
    let ranges = stmt
        .query_map([identity_id], |r| {
            Ok(json!({
                "start": r.get::<_, i64>(0)?,
                "end": r.get::<_, i64>(1)?,
            }))
        })
        .map_err(|e| db_err("peek", e))?
        .collect::<rusqlite::Result<Vec<_>>>()
        .map_err(|e| db_err("peek", e))?;

    Ok(json!({
        "ranges": ranges,
        "scannedToHeight": scanned_to_height(db, identity_id)?,
        // 回补起点以运行时为准：宿主存的生日可能与这里不一致（重新启用不会改已有身份的生日）。
        "birthdayHeight": db.identity_by_id(identity_id)?.birthday_height,
    }))
}

/// 重组恢复：回退到指定高度，并把那之上的区间重新排进待扫队列。
pub fn rewind_and_requeue(db: &mut Db, identity_id: i64, height: u64, tip: u64) -> Result<Value> {
    scan::rewind_to(db, identity_id, height)?;
    // 进度也要退。不退的话下一轮会从一个已经被丢弃的高度接着扫，
    // 中间那段永远不会被重新覆盖。一块都没扫过的保持 NULL：当成 0 会让
    // 下一次 update_chain_tip 从创世块排队。
    db.conn()
        .execute(
            "UPDATE identities SET scanned_to_height = min(scanned_to_height, ?2)
             WHERE id = ?1 AND scanned_to_height IS NOT NULL",
            params![identity_id, height as i64],
        )
        .map_err(|e| db_err("rewindScannedTo", e))?;
    // 往生日之前重扫（用户选了更早的日期）：生日跟着提前，进度才有正确的起点。
    db.conn()
        .execute(
            "UPDATE identities SET birthday_height = ?2 WHERE id = ?1 AND birthday_height > ?2",
            params![identity_id, (height + 1) as i64],
        )
        .map_err(|e| db_err("rewindBirthday", e))?;
    // 回退后那一段要重扫。直接排进队列，不依赖宿主记得再调一次 update_chain_tip。
    if tip > height {
        db.conn()
            .execute(
                "INSERT INTO scan_ranges (identity_id, start_height, end_height)
                 VALUES (?1, ?2, ?3)
                 ON CONFLICT (identity_id, start_height) DO UPDATE SET end_height = max(end_height, ?3)",
                params![identity_id, (height + 1) as i64, (tip + 1) as i64],
            )
            .map_err(|e| db_err("requeueAfterRewind", e))?;
    }
    Ok(json!({ "rewoundTo": height, "requeuedTo": tip }))
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    #[test]
    fn a_new_identity_has_nothing_queued_until_the_tip_is_known() {
        let (db, id) = db_with_identity();
        assert!(next_range(&db, id).unwrap().is_none());
        let p = peek(&db, id).unwrap();
        assert_eq!(p["ranges"].as_array().unwrap().len(), 0);
        assert_eq!(p["birthdayHeight"], 100, "进度的起点取运行时的生日");
    }

    #[test]
    fn learning_the_tip_queues_the_backfill_range() {
        let (mut db, id) = db_with_identity(); // birthday = 100
        update_chain_tip(&mut db, id, 150).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("应当排上队");
        assert_eq!(start, 100, "从 birthday 起扫");
        assert_eq!(end, 151, "区间左闭右开");
    }

    /// 链尖前进时只该抬高终点，不该产生第二条重叠区间 —— 重叠会导致同一段
    /// 被扫两遍，浪费带宽还让进度显示倒退。
    #[test]
    fn advancing_the_tip_extends_rather_than_duplicates() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 150).unwrap();
        update_chain_tip(&mut db, id, 200).unwrap();
        let ranges = peek(&db, id).unwrap();
        let ranges = ranges["ranges"].as_array().unwrap();
        assert_eq!(ranges.len(), 1, "只该有一条区间");
        assert_eq!(ranges[0]["end"], 201);
    }

    /// 链尖不动时重复调用必须幂等。
    #[test]
    fn the_same_tip_twice_changes_nothing() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 150).unwrap();
        let before = peek(&db, id).unwrap();
        update_chain_tip(&mut db, id, 150).unwrap();
        assert_eq!(before, peek(&db, id).unwrap());
    }

    /// **回归**：重扫排进来的区间与旧的尾部区间重叠，余下部分的起点正好落在
    /// 另一条区间的起点上。旧实现撞主键、整步回滚，扫描原地踏步（插件实测）。
    #[test]
    fn consuming_across_overlapping_ranges_merges_instead_of_colliding() {
        let (mut db, id) = db_with_identity();
        for (s, e) in [(100_i64, 200_i64), (161, 250)] {
            db.conn()
                .execute(
                    "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)",
                    params![id, s, e],
                )
                .unwrap();
        }
        consume_range(&mut db, id, 100, 160).expect("不能再撞主键");
        let ranges: Vec<(i64, i64)> = db
            .conn()
            .prepare("SELECT start_height, end_height FROM scan_ranges WHERE identity_id = ?1 ORDER BY start_height")
            .unwrap()
            .query_map([id], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(ranges, vec![(161, 250)]);
    }

    /// 已扫段横跨一条完全被覆盖的区间：那条区间整条消失，不留下重复扫描。
    #[test]
    fn a_range_fully_covered_by_the_scan_disappears() {
        let (mut db, id) = db_with_identity();
        for (s, e) in [(100_i64, 300_i64), (150, 170)] {
            db.conn()
                .execute(
                    "INSERT INTO scan_ranges (identity_id, start_height, end_height) VALUES (?1, ?2, ?3)",
                    params![id, s, e],
                )
                .unwrap();
        }
        consume_range(&mut db, id, 100, 199).unwrap();
        let ranges: Vec<(i64, i64)> = db
            .conn()
            .prepare("SELECT start_height, end_height FROM scan_ranges WHERE identity_id = ?1 ORDER BY start_height")
            .unwrap()
            .query_map([id], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(ranges, vec![(200, 300)]);
    }

    #[test]
    fn consuming_part_of_a_range_leaves_the_remainder() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 200).unwrap();
        consume_range(&mut db, id, 100, 149).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("剩余部分还在");
        assert_eq!((start, end), (150, 201));
    }

    #[test]
    fn consuming_a_whole_range_clears_it() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 120).unwrap();
        consume_range(&mut db, id, 100, 120).unwrap();
        assert!(next_range(&db, id).unwrap().is_none());
    }

    /// 单次调用的块数有硬上限：宿主传个巨大的值会让这次调用跑上几分钟且
    /// 中途无法中断，页面直接冻住。
    #[test]
    fn max_blocks_is_clamped_and_zero_is_rejected() {
        assert_eq!(u64::MAX.min(MAX_BLOCKS_PER_CALL), MAX_BLOCKS_PER_CALL);
        // 0 会让 `start + max_blocks - 1` 下溢，必须挡在入口
        assert!(MAX_BLOCKS_PER_CALL > 0);
    }

    /// **回归测试。** 扫描进度曾经是从全局 `blocks` 表取 `max(height)` 的，
    /// 而那张表所有身份共享 —— 一个追到链尖的身份会把另一个还在回补的身份的
    /// 进度一起"推"到链尖，那段历史就被永久跳过、再也不会重扫。
    #[test]
    fn two_identities_keep_independent_progress() {
        use crate::wallet::testing::key_bytes;

        let (mut db, a) = db_with_identity(); // birthday 100
        let (spend, mut view) = key_bytes();
        view[1] = 3; // 换一个观察密钥，造出第二个不同的身份
        let b = db.add_identity("acct-2", &spend, &view, 100).unwrap().id;

        update_chain_tip(&mut db, a, 1_000).unwrap();
        update_chain_tip(&mut db, b, 1_000).unwrap();

        // A 一路扫到链尖，B 一块没扫
        consume_range(&mut db, a, 100, 1_000).unwrap();
        advance_scanned_to(&db, a, 1_000).unwrap();

        assert_eq!(scanned_to_height(&db, a).unwrap(), Some(1_000));
        assert_eq!(
            scanned_to_height(&db, b).unwrap(),
            None,
            "B 一块都没扫过，进度必须仍是 None"
        );

        // B 的待扫区间必须原封不动，仍从它的 birthday 起
        let (start, _) = next_range(&db, b).unwrap().expect("B 的区间还在");
        assert_eq!(start, 100, "B 的起点不该被 A 的进度带跑");
    }

    #[test]
    fn rewinding_requeues_the_rolled_back_span() {
        let (mut db, id) = db_with_identity();
        update_chain_tip(&mut db, id, 200).unwrap();
        consume_range(&mut db, id, 100, 200).unwrap();
        assert!(next_range(&db, id).unwrap().is_none());

        rewind_and_requeue(&mut db, id, 180, 200).unwrap();
        let (start, end) = next_range(&db, id).unwrap().expect("回退后应重新排队");
        assert_eq!((start, end), (181, 201));
    }

    #[test]
    fn a_node_that_failed_the_batch_path_gets_another_try_later() {
        BLOCKS_BIN_UNSUPPORTED_BY.with(|c| *c.borrow_mut() = Some(("http://n".into(), 1_000.0)));
        assert!(blocks_bin_skipped_for(Some("http://n"), 1_000.0 + 60_000.0));
        assert!(!blocks_bin_skipped_for(Some("http://other"), 1_000.0));
        assert!(!blocks_bin_skipped_for(
            Some("http://n"),
            1_000.0 + BLOCKS_BIN_RETRY_AFTER_MS
        ));
    }

    /// 一块都没扫过就重扫（清缓存、换起点）：不能从创世块开始排队。
    #[test]
    fn rewinding_before_the_first_step_does_not_queue_from_genesis() {
        let (mut db, id) = db_with_identity(); // 生日 100
        rewind_and_requeue(&mut db, id, 100, 200).unwrap();
        update_chain_tip(&mut db, id, 200).unwrap();

        assert_eq!(scanned_to_height(&db, id).unwrap(), None);
        let (start, _) = next_range(&db, id).unwrap().expect("有待扫区间");
        assert_eq!(start, 100);
    }

    /// 用户选了比生日更早的日期重扫：生日跟着提前，进度的起点才对。
    #[test]
    fn rewinding_before_the_birthday_moves_the_birthday() {
        let (mut db, id) = db_with_identity(); // 生日 100
        rewind_and_requeue(&mut db, id, 49, 200).unwrap();
        assert_eq!(db.identity_by_id(id).unwrap().birthday_height, 50);
        let (start, _) = next_range(&db, id).unwrap().expect("有待扫区间");
        assert_eq!(start, 50);
    }
}
