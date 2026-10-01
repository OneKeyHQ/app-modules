//! 扫描：把区块喂给 monero-oxide 的 `Scanner`，把产出落进 SQLite。
//!
//! ## 有界推进
//!
//! 这里**永远不会**自己循环到同步完成。调用方给一批块、拿一个结果，要不要
//! 接着扫由宿主决定 —— 这正是「调度归 TS」那条边界。理由和 Zcash 那边一样：
//! 跑到底的函数在浏览器和移动端上无法中断、无法看进度、无法让出主线程。
//!
//! ## 三道校验，每一道都有具体的失败故事
//!
//! 1. **块连续性** —— `header.previous` 必须等于我们记录的上一块哈希。对不上
//!    就是重组（或节点在骗人），向上报 `REORG_DETECTED` 带精确高度。
//!
//! 2. **RingCT 输出索引** —— 上游 `ScannableBlock` 的文档明确写着
//!    `output_index_for_first_ringct_output` **无法验证**，并要求客户端自己维护
//!    这个索引、与节点声称的比对，不一致应当断开。这里就是那个自己维护的实现：
//!    扫完一块后按「本块 v2 交易的输出总数」推进游标，下一块拿它对账。
//!    索引错了的后果是构环时取错输出，交易被网络拒绝 —— 而且很难从现象反推。
//!
//! 3. **批内高度递增** —— 节点乱序返回时直接拒绝，不要试图自己排序：
//!    能乱序就说明对端不可信，排好序继续扫等于替它掩盖问题。
//!
//! ## 花费检测的协议限制
//!
//! Monero 的观察钱包**无法独立判断自己的输出有没有被花掉** —— 判断需要
//! key image，而 key image 需要花费私钥。这不是实现取舍，是协议性质。
//!
//! 所以这里的做法是：`outputs.key_image` 由宿主通过 [`crate::spend::import_key_images`]
//! 在解锁时补齐；扫描时拿已知的 key image 去撞链上交易的输入。没补齐 key image
//! 的输出**检测不到花费**，余额会偏高 —— 这一点必须让宿主知道，见
//! [`ScanOutcome::outputs_without_key_image`]。

use std::collections::{HashMap, HashSet};

use monero_wallet::{
    address::SubaddressIndex,
    interface::ScannableBlock,
    transaction::{Input, Transaction},
    Scanner, WalletOutput,
};
use rusqlite::{params, OptionalExtension};
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::schema::REORG_WINDOW_BLOCKS;
use crate::wallet::Db;

/// 检测到重组时建议回退多少块。
///
/// Monero 的重组极少超过几块；退太多让用户重扫大段历史，退太少可能没退到
/// 分叉点之前、下一轮继续撞同一个错。宿主在重复失败时应自行加大。
pub const REORG_REWIND_BLOCKS: u64 = 10;

#[derive(Debug, Default, Clone)]
pub struct ScanOutcome {
    pub blocks_scanned: u64,
    pub outputs_found: usize,
    pub spends_detected: usize,
    /// 本批记下的「可能的花费」条数（环里含我的、还不知道 key image 的输出）。
    pub possible_spends: usize,
    pub from_height: Option<u64>,
    pub to_height: Option<u64>,
    /// 还没有 key image 的输出数。**大于 0 意味着余额可能偏高** ——
    /// 这些输出即使已经被花掉，扫描也检测不出来。
    pub outputs_without_key_image: i64,
    /// 拉块耗时（毫秒）。与 `ms_scan` 分开报，因为「网络慢」和「解密慢」
    /// 在界面上长得一模一样，但结论完全相反。
    pub ms_download: u32,
    /// 试解密与落库耗时（毫秒）。
    pub ms_scan: u32,
    /// 这一批块是怎么取来的：`blocksBin`（一次批量）或 `perBlock`（逐块并发）。
    pub fetched_via: &'static str,
    /// 批量路径这一步被放弃的原因。只在放弃发生的那一步出现。
    pub fetch_note: Option<String>,
    /// 这一步的第一批是否直接用上了上一步预取的请求。
    pub prefetch_hit: bool,
    /// 这一步之后的批量大小（下一个请求要多少块）。
    pub batch_blocks: u64,
}

impl ScanOutcome {
    pub fn to_json(&self) -> serde_json::Value {
        json!({
            "blocksScanned": self.blocks_scanned,
            "outputsFound": self.outputs_found,
            "spendsDetected": self.spends_detected,
            "possibleSpends": self.possible_spends,
            "fromHeight": self.from_height,
            "toHeight": self.to_height,
            "outputsWithoutKeyImage": self.outputs_without_key_image,
            "msDownload": self.ms_download,
            "msScan": self.ms_scan,
            "fetchedVia": self.fetched_via,
            "fetchNote": self.fetch_note,
            "prefetchHit": self.prefetch_hit,
            "batchBlocks": self.batch_blocks,
        })
    }
}

/// 本块里 RingCT 输出的数量。
///
/// 只有 v2 交易能产出 RingCT 输出（v1 不能），矿工交易也算在内且排在最前。
/// 这是我们**独立于节点**推算全局输出序号的依据 —— 上游要求客户端这么做。
fn ringct_output_count(block: &ScannableBlock) -> u64 {
    fn count(tx: &Transaction<impl monero_wallet::transaction::PotentiallyPruned>) -> u64 {
        match tx {
            Transaction::V1 { .. } => 0,
            Transaction::V2 { prefix, .. } => prefix.outputs.len() as u64,
        }
    }
    count(block.block.miner_transaction()) + block.transactions.iter().map(count).sum::<u64>()
}

/// 本块所有交易输入里的 key image，连同它所属交易的 txid。
///
/// txid 取自 `block.block.transactions`（块头里的 txid 列表），它与
/// `block.transactions`（交易体）一一对应且同序 —— 上游的
/// `expand_to_scannable_block` 就是按这个列表去拉的交易体。
///
/// **不能用 `Transaction::hash()`**：这里的交易是 `Pruned` 形态，V2 交易算
/// 哈希需要 prunable hash，而裁剪掉的正是它。
///
/// 两个列表长度对不上说明节点给的块自相矛盾，直接拒绝而不是凑合对齐 ——
/// 错位会把花费记到别人的交易上。
fn key_images_in(block: &ScannableBlock) -> Result<Vec<([u8; 32], [u8; 32])>> {
    if block.block.transactions.len() != block.transactions.len() {
        return Err(RuntimeError::with(
            ErrorCode::MalformedNodeResponse,
            json!({
                "operation": "expandBlock",
                "height": block.block.number(),
                "txidCount": block.block.transactions.len(),
                "bodyCount": block.transactions.len(),
            }),
        ));
    }

    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        for input in &tx.prefix().inputs {
            if let Input::ToKey { key_image, .. } = input {
                out.push((key_image.to_bytes(), *txid));
            }
        }
    }
    Ok(out)
}

/// 块里每个输入引用的环：`(key image, 所在交易, 金额类, 环成员的绝对全局序号)`。
///
/// `key_offsets` 是相对差分：第一个是绝对序号，之后每个是与前一个的间隔。
fn ring_references_in(block: &ScannableBlock) -> Vec<RingReference> {
    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        for input in &tx.prefix().inputs {
            if let Input::ToKey {
                amount,
                key_offsets,
                key_image,
            } = input
            {
                let mut absolute = Vec::with_capacity(key_offsets.len());
                let mut index = 0u64;
                for offset in key_offsets {
                    index = index.saturating_add(*offset);
                    absolute.push(index);
                }
                out.push(RingReference {
                    key_image: key_image.to_bytes(),
                    spend_txid: *txid,
                    amount: *amount,
                    members: absolute,
                });
            }
        }
    }
    out
}

/// 一条可能的花费：我的某笔输出出现在某个输入的环里。
struct PossibleSpend {
    out_txid: [u8; 32],
    out_index: u64,
    key_image: [u8; 32],
    spend_txid: [u8; 32],
    height: u64,
    timestamp: u64,
}

struct RingReference {
    key_image: [u8; 32],
    spend_txid: [u8; 32],
    /// `None`/`Some(0)`：RingCT 池；其余为老式按金额分池（2017 年前）。
    amount: Option<u64>,
    members: Vec<u64>,
}

/// 还没有 key image 的未花输出，按全局序号索引：`(txid, index_in_tx, 金额)`。
/// 只有它们需要「可能的花费」—— 有 key image 的由正常的花费检测负责。
type RingCandidates = HashMap<u64, Vec<([u8; 32], u64, u64)>>;

fn load_ring_candidates(db: &Db, identity_id: i64) -> Result<RingCandidates> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT txid, index_in_tx, global_index, amount_atomic FROM outputs
             WHERE identity_id = ?1 AND key_image IS NULL AND spent_at_height IS NULL",
        )
        .map_err(|e| db_err("loadRingCandidates", e))?;
    let rows = stmt
        .query_map([identity_id], |r| {
            Ok((
                r.get::<_, Vec<u8>>(0)?,
                r.get::<_, i64>(1)? as u64,
                r.get::<_, i64>(2)? as u64,
                r.get::<_, i64>(3)? as u64,
            ))
        })
        .map_err(|e| db_err("loadRingCandidates", e))?;
    let mut map: RingCandidates = HashMap::new();
    for row in rows {
        let (txid, index, global, amount) = row.map_err(|e| db_err("loadRingCandidates", e))?;
        let Ok(txid) = <[u8; 32]>::try_from(txid.as_slice()) else {
            continue;
        };
        map.entry(global).or_default().push((txid, index, amount));
    }
    Ok(map)
}

/// 一个输入的环里含我的哪些输出。老式按金额分池的输出还要金额相同才算；
/// RingCT 输入只按序号 —— 多报只会多一条「可能」，漏报才会让花费认不出。
fn ring_matches<'a>(
    candidates: &'a RingCandidates,
    reference: &'a RingReference,
) -> impl Iterator<Item = &'a ([u8; 32], u64, u64)> + 'a {
    let pool = reference.amount.filter(|a| *a != 0);
    reference
        .members
        .iter()
        .filter_map(|member| candidates.get(member))
        .flatten()
        .filter(move |(_, _, amount)| pool.is_none_or(|p| p == *amount))
}

/// 每条交易的公钥：`(R, additional)`，按 txid 索引。
///
/// ## 为什么要单独留一份
///
/// monero-oxide 把「R + 输出序号 + 子地址」整条推导折叠成 `WalletOutput` 上的
/// 一个 `key_offset` 标量，**然后就把 R 丢了**。软件签名只要
/// `spend + key_offset`，所以缺了也看不出来。
///
/// 固件不接受 `key_offset`。它要 `real_out_tx_key` 自己推导一次性私钥和
/// key image —— 接受宿主算好的偏移量，等于让一个恶意宿主指挥设备拿任意密钥
/// 签名。所以硬件那条路必须有 R。
///
/// 而 R 只在扫描这一刻手里有：事后要么全量重扫，要么拿 txid 去找节点要回那条
/// 交易 —— 后者等于直接告诉节点「我对这笔感兴趣」，把扫全链换来的隐私当场
/// 还回去。所以在这里留下来，哪怕现在还没有硬件可接。
fn tx_public_keys_in(block: &ScannableBlock) -> Vec<([u8; 32], Vec<u8>, Option<Vec<u8>>)> {
    // 长度不齐的情况由 `key_images_in` 统一报错；这里取不到就跳过，
    // 不重复报同一个错。
    let mut out = Vec::new();
    for (txid, tx) in block
        .block
        .transactions
        .iter()
        .zip(block.transactions.iter())
    {
        if let Some((keys, additional)) = tx_public_keys_from_extra(&tx.prefix().extra) {
            out.push((*txid, keys, additional));
        }
    }
    out
}

/// 从一条交易的 `extra` 里取出 `(R, additional)`，各自 32 字节一个顺序拼接。
///
/// 拆成纯函数是为了能用真实链上数据钉住 —— 扫描路径本身要造一个完整的
/// `ScannableBlock` 才跑得起来，那种 fixture 只会测到我们自己的假设。
fn tx_public_keys_from_extra(extra: &[u8]) -> Option<(Vec<u8>, Option<Vec<u8>>)> {
    fn flatten(points: &[monero_wallet::ed25519::Point]) -> Vec<u8> {
        let mut out = Vec::with_capacity(points.len() * 32);
        for p in points {
            out.extend_from_slice(&p.compress().to_bytes());
        }
        out
    }

    let extra = monero_wallet::extra::Extra::read(&mut &*extra).ok()?;
    let (keys, additional) = extra.keys()?;
    Some((flatten(&keys), additional.as_deref().map(flatten)))
}

/// 扫描一批块并落库。**整批一个事务**：要么全成，要么一块都不留。
///
/// 块必须按高度升序、且彼此连续。调用方（宿主）负责按区间取块。
pub fn scan_blocks(
    db: &mut Db,
    identity_id: i64,
    blocks: &[ScannableBlock],
) -> Result<ScanOutcome> {
    if blocks.is_empty() {
        let mut outcome = ScanOutcome::default();
        outcome.outputs_without_key_image = count_outputs_without_key_image(db, identity_id)?;
        return Ok(outcome);
    }

    let identity = db.identity_by_id(identity_id)?;
    let subaddresses = db.subaddress_indices(identity_id)?;

    let mut scanner = Scanner::new(identity.view_pair.clone());
    for (major, minor) in &subaddresses {
        // (0,0) 进不了库（schema 有 CHECK），所以这里 None 不可能发生；
        // 真发生了说明有人绕过了约束直接写库，宁可跳过也不要 panic。
        if let Some(index) = SubaddressIndex::new(*major, *minor) {
            scanner.register_subaddress(index);
        }
    }

    // 我们自己维护的 key image 集合，用于花费检测。
    let known_key_images = load_key_images(db, identity_id)?;
    // 还不知道 key image 的输出，用环成员比对记下「可能的花费」。
    let mut ring_candidates = load_ring_candidates(db, identity_id)?;
    let mut possible_spends: Vec<PossibleSpend> = Vec::new();

    let mut outcome = ScanOutcome::default();
    let mut found: Vec<(u64, WalletOutput)> = Vec::new();
    // txid -> (R, additional)。只留我们真正收到输出的那些交易的。
    let mut tx_pub_keys: HashMap<[u8; 32], (Vec<u8>, Option<Vec<u8>>)> = HashMap::new();
    let mut spends: Vec<(u64, [u8; 32], [u8; 32])> = Vec::new(); // (height, key_image, txid)
    let mut block_rows: Vec<(u64, [u8; 32], Option<u64>, u64)> = Vec::new(); // (height, hash, ringct_next, ts)

    // 批起点的前一块：用于连续性与 RingCT 游标对账。
    let first_height = blocks[0].block.number() as u64;
    let mut prev = previous_block_state(db, identity_id, first_height)?;

    for block in blocks {
        let height = block.block.number() as u64;

        // ── 1. 批内必须严格递增且连续 ──────────────────────────────────
        if let Some((prev_height, _, _)) = prev {
            if height != prev_height + 1 {
                return Err(RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": height,
                        "expectedHeight": prev_height + 1,
                        "suggestedRewindHeight": height.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                ));
            }
        }

        // ── 2. 前驱哈希对账 —— 重组检测 ────────────────────────────────
        if let Some((_, prev_hash, _)) = prev {
            if block.block.header.previous != prev_hash {
                return Err(RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": height,
                        "suggestedRewindHeight": height.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                ));
            }
        }

        // ── 3. RingCT 索引对账 —— 上游要求客户端独立维护并比对 ─────────
        if let (Some((_, _, Some(expected_next))), Some(claimed)) =
            (prev, block.output_index_for_first_ringct_output)
        {
            if claimed != expected_next {
                return Err(RuntimeError::with(
                    ErrorCode::RingCtIndexMismatch,
                    json!({
                        "height": height,
                        "claimed": claimed,
                        "expected": expected_next,
                    }),
                ));
            }
        }

        let outputs = scanner
            .scan(block.clone())
            .map_err(|e| {
                RuntimeError::with(ErrorCode::ScanFailed, json!({ "height": height }))
                    .detail(format!("{e:?}"))
            })?
            // 额外时间锁由查询层按当前链高判断，这里全部收下 ——
            // 丢掉锁定中的输出会让它们永远不再出现（我们不会重扫这一段）。
            .ignore_additional_timelock();

        if !outputs.is_empty() {
            // 只在这一块确实有我们的输出时才解析 extra —— 绝大多数块一个都
            // 没有，为它们全量解析是白花 CPU。
            for (txid, keys, additional) in tx_public_keys_in(block) {
                tx_pub_keys.insert(txid, (keys, additional));
            }
        }
        // 先比对本块的输入，再把本块收到的钱加进候选：刚收到的钱至少锁 10 块，
        // 不可能在同一块被花。
        for reference in ring_references_in(block) {
            if known_key_images.contains_key(&reference.key_image) {
                continue;
            }
            for (out_txid, out_index, _) in ring_matches(&ring_candidates, &reference) {
                possible_spends.push(PossibleSpend {
                    out_txid: *out_txid,
                    out_index: *out_index,
                    key_image: reference.key_image,
                    spend_txid: reference.spend_txid,
                    height,
                    timestamp: block.block.header.timestamp,
                });
            }
        }
        for output in outputs {
            ring_candidates
                .entry(output.index_on_blockchain())
                .or_default()
                .push((
                    output.transaction(),
                    output.index_in_transaction(),
                    output.commitment().amount,
                ));
            found.push((height, output));
        }

        for (key_image, txid) in key_images_in(block)? {
            if known_key_images.contains_key(&key_image) {
                // txid 必须是**花费这笔输出的那条交易**，不是块哈希 ——
                // 记错了历史详情会指向一条不相干的交易。
                spends.push((height, key_image, txid));
            }
        }

        // 游标的依据：优先用上一块推出来的，其次用节点声称的起点。
        // 两者都没有时**保持未知**，绝不退化成 0 —— 那会让下一块的对账
        // 拿真实序号去比 0，报出一个假的 RINGCT_INDEX_MISMATCH。
        let next_ringct = prev
            .and_then(|(_, _, n)| n)
            .or(block.output_index_for_first_ringct_output)
            .map(|base| base + ringct_output_count(block));

        block_rows.push((
            height,
            block.block.hash(),
            next_ringct,
            block.block.header.timestamp,
        ));
        prev = Some((height, block.block.hash(), next_ringct));
        outcome.blocks_scanned += 1;
    }

    outcome.from_height = Some(first_height);
    outcome.to_height = Some(first_height + outcome.blocks_scanned - 1);
    outcome.outputs_found = found.len();
    outcome.spends_detected = spends.len();
    outcome.possible_spends = possible_spends.len();

    persist(
        db,
        identity_id,
        &found,
        &tx_pub_keys,
        &spends,
        &known_key_images,
        &block_rows,
        &possible_spends,
    )?;
    outcome.outputs_without_key_image = count_outputs_without_key_image(db, identity_id)?;
    Ok(outcome)
}

/// 取某高度前一块的 (高度, 哈希, 扫完后的 RingCT 游标)。
fn previous_block_state(
    db: &Db,
    identity_id: i64,
    height: u64,
) -> Result<Option<(u64, [u8; 32], Option<u64>)>> {
    if height == 0 {
        return Ok(None);
    }
    db.conn()
        .query_row(
            "SELECT height, hash, ringct_next FROM blocks WHERE identity_id = ?1 AND height = ?2",
            params![identity_id, (height - 1) as i64],
            |r| {
                let h: i64 = r.get(0)?;
                let hash: Vec<u8> = r.get(1)?;
                let next: Option<i64> = r.get(2)?;
                Ok((h as u64, hash, next.map(|n| n as u64)))
            },
        )
        .optional()
        .map_err(|e| db_err("previousBlockState", e))?
        .map(|(h, hash, next)| {
            let hash = <[u8; 32]>::try_from(hash.as_slice())
                .map_err(|_| db_err("previousBlockState", "块哈希长度不是 32"))?;
            Ok((h, hash, next))
        })
        .transpose()
}

/// 未花的，加上被自己广播、还没上链的交易花掉的：后者上链时要靠 key image 命中才能确认。
fn load_key_images(db: &Db, identity_id: i64) -> Result<HashMap<[u8; 32], i64>> {
    let mut stmt = db
        .conn()
        .prepare(
            "SELECT id, key_image FROM outputs o
             WHERE o.identity_id = ?1 AND o.key_image IS NOT NULL
               AND (o.spent_at_height IS NULL OR o.spent_in_txid IN (
                 SELECT txid FROM transactions WHERE identity_id = ?1 AND height IS NULL))",
        )
        .map_err(|e| db_err("loadKeyImages", e))?;
    let rows = stmt
        .query_map([identity_id], |r| {
            let id: i64 = r.get(0)?;
            let ki: Vec<u8> = r.get(1)?;
            Ok((id, ki))
        })
        .map_err(|e| db_err("loadKeyImages", e))?;

    let mut map = HashMap::new();
    for row in rows {
        let (id, ki) = row.map_err(|e| db_err("loadKeyImages", e))?;
        if let Ok(ki) = <[u8; 32]>::try_from(ki.as_slice()) {
            map.insert(ki, id);
        }
    }
    Ok(map)
}

fn count_outputs_without_key_image(db: &Db, identity_id: i64) -> Result<i64> {
    db.conn()
        .query_row(
            "SELECT count(*) FROM outputs WHERE identity_id = ?1 AND key_image IS NULL",
            [identity_id],
            |r| r.get(0),
        )
        .map_err(|e| db_err("countOutputsWithoutKeyImage", e))
}

#[allow(clippy::type_complexity, clippy::too_many_arguments)]
fn persist(
    db: &mut Db,
    identity_id: i64,
    found: &[(u64, WalletOutput)],
    tx_pub_keys: &HashMap<[u8; 32], (Vec<u8>, Option<Vec<u8>>)>,
    spends: &[(u64, [u8; 32], [u8; 32])],
    known_key_images: &HashMap<[u8; 32], i64>,
    block_rows: &[(u64, [u8; 32], Option<u64>, u64)],
    possible_spends: &[PossibleSpend],
) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginScanCommit", e))?;

    let mut seen_txids: HashSet<[u8; 32]> = HashSet::new();
    // 交易时间跟交易行一起存：块表会裁剪到链尖附近，回补扫到的交易一旦所在块被裁，联表就拿不到时间。
    let block_time: HashMap<u64, i64> = block_rows
        .iter()
        .map(|(h, _, _, ts)| (*h, *ts as i64))
        .collect();

    for (height, output) in found {
        let txid = output.transaction();
        let subaddress = output.subaddress();
        let output_key = output.key().compress().to_bytes();
        if is_burning_duplicate(
            &tx,
            identity_id,
            &output_key,
            &txid,
            output.index_in_transaction(),
        )? {
            continue;
        }
        // `INSERT OR IGNORE`：重扫同一段区间必须幂等，否则余额翻倍。
        // 唯一约束是 (identity_id, txid, index_in_tx)。
        tx.execute(
            "INSERT OR IGNORE INTO outputs
               (identity_id, blob, txid, global_index, index_in_tx, amount_atomic,
                subaddress_major, subaddress_minor, height, additional_timelock,
                tx_pub_keys, tx_additional_pub_keys, output_key)
             VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)",
            params![
                identity_id,
                output.serialize(),
                txid.as_slice(),
                output.index_on_blockchain() as i64,
                output.index_in_transaction() as i64,
                output.commitment().amount as i64,
                subaddress.map(|s| s.account() as i64),
                subaddress.map(|s| s.address() as i64),
                *height as i64,
                timelock_to_i64(output.additional_timelock()),
                tx_pub_keys.get(&txid).map(|(k, _)| k.as_slice()),
                tx_pub_keys.get(&txid).and_then(|(_, a)| a.as_deref()),
                output_key.as_slice(),
            ],
        )
        .map_err(|e| db_err("insertOutput", e))?;

        if seen_txids.insert(txid) {
            tx.execute(
                "INSERT INTO transactions (identity_id, txid, height, timestamp, direction)
                 VALUES (?1, ?2, ?3, ?4, 'in')
                 ON CONFLICT (identity_id, txid) DO UPDATE
                   SET height = COALESCE(transactions.height, excluded.height), failed = 0,
                       timestamp = COALESCE(excluded.timestamp, transactions.timestamp)",
                params![
                    identity_id,
                    txid.as_slice(),
                    *height as i64,
                    block_time.get(height)
                ],
            )
            .map_err(|e| db_err("insertIncomingTransaction", e))?;
        }
    }

    for (height, key_image, txid) in spends {
        let Some(output_id) = known_key_images.get(key_image) else {
            continue;
        };
        tx.execute(
            "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
             WHERE id = ?3 AND (spent_at_height IS NULL OR spent_in_txid IN (
               SELECT txid FROM transactions WHERE identity_id = ?4 AND height IS NULL))",
            params![*height as i64, txid.as_slice(), output_id, identity_id],
        )
        .map_err(|e| db_err("markOutputSpent", e))?;
        tx.execute(
            "INSERT INTO transactions (identity_id, txid, height, timestamp, direction)
             VALUES (?1, ?2, ?3, ?4, 'out')
             ON CONFLICT (identity_id, txid) DO UPDATE
               SET direction = 'out', height = ?3, failed = 0,
                   timestamp = COALESCE(excluded.timestamp, transactions.timestamp)",
            params![
                identity_id,
                txid.as_slice(),
                *height as i64,
                block_time.get(height)
            ],
        )
        .map_err(|e| db_err("insertOutgoingTransaction", e))?;
    }

    // 幂等：重扫同一段会遇到同样的环引用。
    for ps in possible_spends {
        tx.execute(
            "INSERT OR IGNORE INTO possible_spends
               (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
             VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)",
            params![
                identity_id,
                ps.out_txid.as_slice(),
                ps.out_index as i64,
                ps.key_image.as_slice(),
                ps.spend_txid.as_slice(),
                ps.height as i64,
                ps.timestamp as i64
            ],
        )
        .map_err(|e| db_err("insertPossibleSpend", e))?;
    }

    for (height, hash, ringct_next, timestamp) in block_rows {
        tx.execute(
            "INSERT OR REPLACE INTO blocks (identity_id, height, hash, ringct_next, timestamp)
             VALUES (?1, ?2, ?3, ?4, ?5)",
            params![
                identity_id,
                *height as i64,
                hash.as_slice(),
                ringct_next.map(|n| n as i64),
                *timestamp as i64
            ],
        )
        .map_err(|e| db_err("insertBlock", e))?;
    }

    // 裁剪块表：只留链尖往前 REORG_WINDOW_BLOCKS 块。
    // 不裁剪的话这张表会随链高无限增长 —— 那正是我们批评 wallet2 内存模型
    // 时说的那件事，自己不能犯。
    if let Some((tip, _, _, _)) = block_rows.last() {
        let floor = tip.saturating_sub(REORG_WINDOW_BLOCKS);
        tx.execute(
            "DELETE FROM blocks WHERE identity_id = ?1 AND height < ?2",
            params![identity_id, floor as i64],
        )
        .map_err(|e| db_err("pruneBlocks", e))?;
    }

    tx.commit().map_err(|e| db_err("commitScan", e))
}

/// 燃烧 bug：同一个一次性公钥出现在另一笔输出里。它们只有一笔能花（花一笔就烧掉
/// 其余），所以只认最先看到的那笔，后来的不入账 —— 宁可少算，不能把花不掉的钱
/// 算进余额。同一笔输出重扫不算重复。
fn is_burning_duplicate(
    conn: &rusqlite::Connection,
    identity_id: i64,
    output_key: &[u8; 32],
    txid: &[u8; 32],
    index_in_tx: u64,
) -> Result<bool> {
    conn.query_row(
        "SELECT EXISTS (SELECT 1 FROM outputs
           WHERE identity_id = ?1 AND output_key = ?2
             AND NOT (txid = ?3 AND index_in_tx = ?4))",
        params![
            identity_id,
            output_key.as_slice(),
            txid.as_slice(),
            index_in_tx as i64
        ],
        |r| r.get(0),
    )
    .map_err(|e| db_err("burningBugCheck", e))
}

fn timelock_to_i64(timelock: monero_wallet::transaction::Timelock) -> i64 {
    use monero_wallet::transaction::Timelock;
    match timelock {
        Timelock::None => 0,
        Timelock::Block(b) => b as i64,
        Timelock::Time(t) => t as i64,
    }
}

/// 回退到指定高度：丢掉该高度**之上**的全部扫描结果。
///
/// **破坏性。** 用于重组恢复。已广播的交易不受影响（它们在链上），但本地
/// 未确认记录会丢。
pub fn rewind_to(db: &mut Db, identity_id: i64, height: u64) -> Result<()> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginRewind", e))?;

    tx.execute(
        "DELETE FROM outputs WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindOutputs", e))?;

    // 花费标记也要撤销：那笔花费所在的块可能已经不在链上了。
    // 自己广播、还没上链的不撤：它的高度只是广播时的链尖，撤了会被再次选中。
    tx.execute(
        "UPDATE outputs SET spent_at_height = NULL, spent_in_txid = NULL
         WHERE identity_id = ?1 AND spent_at_height > ?2
           AND spent_in_txid NOT IN (
             SELECT txid FROM transactions WHERE identity_id = ?1 AND height IS NULL)",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindSpends", e))?;

    tx.execute(
        "DELETE FROM transactions WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindTransactions", e))?;

    tx.execute(
        "DELETE FROM possible_spends WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindPossibleSpends", e))?;

    tx.execute(
        "DELETE FROM blocks WHERE identity_id = ?1 AND height > ?2",
        params![identity_id, height as i64],
    )
    .map_err(|e| db_err("rewindBlocks", e))?;

    tx.commit().map_err(|e| db_err("commitRewind", e))
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;

    /// 用真实主网块验证环引用的换算：相对差分要累加成严格递增的绝对序号，
    /// 当前共识环大小 16，全部是 RingCT 输入。
    #[test]
    fn ring_references_resolve_to_increasing_absolute_positions_on_real_blocks() {
        let blocks = crate::blocks_bin::parse_response(
            include_bytes!("../tests/fixtures/get_blocks_bin_3768621.bin"),
            3_768_621,
        )
        .expect("解析真实响应");
        let references: Vec<RingReference> = blocks.iter().flat_map(ring_references_in).collect();
        assert!(!references.is_empty());
        let mut key_images = HashSet::new();
        for reference in &references {
            assert_eq!(reference.members.len(), 16);
            assert!(reference.members.windows(2).all(|w| w[0] < w[1]));
            assert_eq!(reference.amount.unwrap_or(0), 0, "RingCT 输入");
            assert!(key_images.insert(reference.key_image), "key image 不应重复");
        }

        // 我的一笔输出若恰好是某个环的成员，就被记为可能的花费。
        let target = &references[0];
        let mut candidates = RingCandidates::new();
        candidates.insert(target.members[5], vec![([7; 32], 1, 12_345)]);
        assert_eq!(ring_matches(&candidates, target).count(), 1);
    }

    /// 老式按金额分池的输入，序号只在同一金额池内有意义。
    #[test]
    fn a_pre_ringct_input_matches_only_outputs_of_its_amount() {
        let reference = RingReference {
            key_image: [1; 32],
            spend_txid: [2; 32],
            amount: Some(5_000),
            members: vec![40, 41],
        };
        let mut candidates = RingCandidates::new();
        candidates.insert(41, vec![([7; 32], 0, 5_000), ([8; 32], 0, 6_000)]);
        let matched: Vec<_> = ring_matches(&candidates, &reference).collect();
        assert_eq!(matched.len(), 1);
        assert_eq!(matched[0].0, [7; 32]);
    }
    use rusqlite::params;

    /// 真实主网交易向量。
    ///
    /// 取自 mainnet 高度 3767756 的交易
    /// `097edcb40a4345c0e83c41e8631b9e71173389270694e7a7499d3c2fa17951bb`。
    /// 结构是 `01`(TxPublicKey) + 32 字节 R + `02 09 01`(Nonce/加密 payment id)
    /// + 8 字节。
    ///
    /// 用真数据而不是自己拼一个 extra：自己拼只会测到我们对格式的理解，
    /// 而这里要证明的恰恰是对真实链上数据的理解没错。
    #[test]
    fn the_transaction_public_key_is_read_from_a_real_mainnet_extra() {
        let extra = hex::decode(
            "01d3c8b566d9fd0bff3f93448591ec257cc8bf813e1aaa808e97254a5c65ea166\
             00209012c9f54c03a7c72d0",
        )
        .unwrap();

        let (keys, additional) = tx_public_keys_from_extra(&extra).expect("应当解析出交易公钥");
        assert_eq!(
            hex::encode(&keys),
            "d3c8b566d9fd0bff3f93448591ec257cc8bf813e1aaa808e97254a5c65ea1660"
        );
        assert_eq!(keys.len(), 32, "单个 R 就是 32 字节，不要多也不要少");
        // 这笔交易发给主地址，没有 additional keys。空 Vec 和 None 是两回事：
        // 前者表示「有 0 个」，后者表示「没有这个字段」。
        assert_eq!(additional, None);
    }

    /// 空 extra 不是错误，只是没有公钥 —— 不能 panic，也不能当成解析失败去
    /// 中断整批扫描。
    #[test]
    fn an_extra_without_keys_yields_nothing_rather_than_failing() {
        assert!(tx_public_keys_from_extra(&[]).is_none());
    }

    /// **回归测试。** RingCT 游标曾经在「没有依据」时退化成 0。
    ///
    /// 触发路径：从链中段开始扫，而起始块恰好没有 RingCT 输出（节点因此给的
    /// `output_index_for_first_ringct_output` 是 None）。游标存成 0 之后，
    /// 下一块拿真实序号（几千万）来比对 0，报出一个**假的**
    /// `RINGCT_INDEX_MISMATCH` —— 把一个诚实的节点判成作恶，扫描直接卡死。
    ///
    /// 正确行为：没有依据就存 NULL，对账跳过，等拿到真实锚点再开始比。
    #[test]
    fn an_unknown_ringct_cursor_is_stored_as_null_not_zero() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                 VALUES (?1, ?2, ?3, NULL, 0)",
                params![id, 3_000_000i64, &vec![7u8; 32]],
            )
            .expect("游标应当允许 NULL");

        let prev = previous_block_state(&db, id, 3_000_001).expect("读前一块");
        let (height, _, cursor) = prev.expect("前一块应当在");
        assert_eq!(height, 3_000_000);
        assert_eq!(cursor, None, "未知必须是 None，不能是 Some(0)");
    }

    /// 一个身份回滚不能动另一个身份的锚点：否则另一个身份在新链上接着扫，
    /// 看不出重组，孤块里的钱留在余额里。
    #[test]
    fn one_identity_rewinding_leaves_the_others_anchors_alone() {
        let (mut db, a) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO identities (uuid, primary_address, spend_public_key,
                   view_private_key, birthday_height, created_at)
                 VALUES ('b', 'b', ?1, ?1, 0, 0)",
                [vec![9u8; 32]],
            )
            .unwrap();
        let b = db.conn().last_insert_rowid();
        for id in [a, b] {
            db.conn()
                .execute(
                    "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                     VALUES (?1, 100, ?2, NULL, 0)",
                    params![id, vec![7u8; 32]],
                )
                .unwrap();
        }

        rewind_to(&mut db, a, 50).unwrap();

        assert!(previous_block_state(&db, a, 101).unwrap().is_none());
        assert!(previous_block_state(&db, b, 101).unwrap().is_some());
    }

    #[test]
    fn a_reused_output_key_from_another_transaction_is_a_burning_duplicate() {
        let (db, id) = db_with_identity();
        let key = [4u8; 32];
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, output_key)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3)",
                params![id, [1u8; 32].as_slice(), key.as_slice()],
            )
            .unwrap();

        assert!(is_burning_duplicate(db.conn(), id, &key, &[2u8; 32], 0).unwrap());
        assert!(
            !is_burning_duplicate(db.conn(), id, &key, &[1u8; 32], 0).unwrap(),
            "同一笔重扫不算"
        );
        assert!(!is_burning_duplicate(db.conn(), id, &[5u8; 32], &[2u8; 32], 0).unwrap());
    }

    /// 有依据时游标正常推进。
    #[test]
    fn a_known_ringct_cursor_round_trips() {
        let (db, id) = db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO blocks (identity_id, height, hash, ringct_next, timestamp)
                 VALUES (?1, ?2, ?3, ?4, 0)",
                params![id, 3_000_000i64, &vec![7u8; 32], 82_000_000i64],
            )
            .unwrap();
        let (_, _, cursor) = previous_block_state(&db, id, 3_000_001).unwrap().unwrap();
        assert_eq!(cursor, Some(82_000_000));
    }

    /// 回补扫到的交易，所在块随后被裁出块表，时间也不能丢（否则界面显示 1970）。
    #[test]
    fn a_transaction_keeps_its_time_after_its_block_is_pruned() {
        let (mut db, id) = db_with_identity();
        let tx_height = 100u64;
        let tip = tx_height + REORG_WINDOW_BLOCKS + 5;
        let key_image = [1u8; 32];
        let txid = [2u8; 32];
        let known: HashMap<[u8; 32], i64> = [(key_image, 1)].into_iter().collect();
        persist(
            &mut db,
            id,
            &[],
            &HashMap::new(),
            &[(tx_height, key_image, txid)],
            &known,
            &[
                (tx_height, [7u8; 32], None, 1_700_000_000),
                (tip, [8u8; 32], None, 1_700_100_000),
            ],
            &[],
        )
        .unwrap();

        let pruned: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM blocks WHERE height = ?1",
                [tx_height as i64],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(pruned, 0, "块本身应当已被裁掉");

        let history = crate::query::history(&db, id, 10, 0).unwrap();
        assert_eq!(history["items"][0]["timestamp"], 1_700_000_000);
    }

    /// 自己广播的交易上链：输出早在广播时标了已花，扫到时仍要按 key image 认出来，
    /// 把交易从待确认转成已确认，高度改成真实上链高度。
    #[test]
    fn our_broadcast_spend_is_confirmed_when_its_block_is_scanned() {
        let (mut db, id) = db_with_identity();
        let key_image = [1u8; 32];
        let txid = [2u8; 32];
        let (broadcast_tip, mined_at) = (500i64, 503u64);
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3, ?4, ?5)",
                params![id, [9u8; 32].as_slice(), key_image.as_slice(), broadcast_tip, txid.as_slice()],
            )
            .unwrap();
        let output_id = db.conn().last_insert_rowid();
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction)
                 VALUES (?1, ?2, NULL, 'out')",
                params![id, txid.as_slice()],
            )
            .unwrap();

        let known = load_key_images(&db, id).unwrap();
        assert_eq!(
            known.get(&key_image),
            Some(&output_id),
            "待确认的花费也要参与比对"
        );

        persist(
            &mut db,
            id,
            &[],
            &HashMap::new(),
            &[(mined_at, key_image, txid)],
            &known,
            &[(mined_at, [7u8; 32], None, 1_700_000_000)],
            &[],
        )
        .unwrap();

        let tx_height: Option<i64> = db
            .conn()
            .query_row(
                "SELECT height FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(tx_height, Some(mined_at as i64));
        let spent_at: i64 = db
            .conn()
            .query_row(
                "SELECT spent_at_height FROM outputs WHERE id = ?1",
                [output_id],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(spent_at, mined_at as i64);
        assert!(
            load_key_images(&db, id).unwrap().is_empty(),
            "确认后不再参与比对"
        );
    }

    #[test]
    fn a_rewind_keeps_the_mark_on_outputs_our_pending_send_spent() {
        let (mut db, id) = db_with_identity();
        let txid = [2u8; 32];
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image, spent_at_height, spent_in_txid)
                 VALUES (?1, x'00', ?2, 1, 0, 700, 10, ?3, 500, ?4)",
                params![id, [9u8; 32].as_slice(), [1u8; 32].as_slice(), txid.as_slice()],
            )
            .unwrap();
        db.conn()
            .execute(
                "INSERT INTO transactions (identity_id, txid, height, direction)
                 VALUES (?1, ?2, NULL, 'out')",
                params![id, txid.as_slice()],
            )
            .unwrap();

        rewind_to(&mut db, id, 490).unwrap();

        let spent_in: Option<Vec<u8>> = db
            .conn()
            .query_row("SELECT spent_in_txid FROM outputs", [], |r| r.get(0))
            .unwrap();
        assert_eq!(spent_in, Some(txid.to_vec()));
    }

    /// 空批次是合法的 no-op，不该报错也不该动任何状态。
    #[test]
    fn an_empty_batch_is_a_harmless_no_op() {
        let (mut db, id) = db_with_identity();
        let outcome = scan_blocks(&mut db, id, &[]).expect("空批次");
        assert_eq!(outcome.blocks_scanned, 0);
        assert_eq!(outcome.outputs_found, 0);
        assert!(outcome.from_height.is_none());
    }
}
