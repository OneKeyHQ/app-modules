//! 扫链：**真正有界**的推进。
//!
//! ## 为什么不用 `zcash_client_backend::sync::run`
//!
//! 上游那个函数内部是 `while running(...) {}` —— 它跑到**完全同步为止**。
//! `batch_size` 只控制单批区块数，不控制批数。也就是说它不是「推进一步」，
//! 而是「同步到底」：调用方无法中断、无法看进度、无法在中途让出主线程。
//!
//! 实测在 testnet 上一次调用 900 秒未返回。对浏览器和移动端都不可接受。
//!
//! 所以这里自己驱动上游的公开 API，拆成两段：
//!
//! - [`prepare`] —— 一次性准备（下载子树根、更新链尖）。慢，但每轮同步只做一次。
//! - [`step`] —— 有界推进：最多扫 `max_batches` 批，然后带进度返回。
//!
//! 宿主拿到进度后自己决定要不要接着调 —— 这正是「调度归 TS」那条边界。

use std::collections::{HashMap, HashSet};
use std::hash::Hash;

use futures_util::TryStreamExt;
use serde_json::json;

use zcash_address::unified::{Container, Encoding, Fvk, Ufvk};
use zcash_client_backend::data_api::chain::error::Error as ChainError;
use zcash_client_backend::data_api::chain::{
    BlockCache, BlockSource, ChainState, CommitmentTreeRoot,
};
use zcash_client_backend::data_api::scanning::{ScanPriority, ScanRange};
use zcash_client_backend::data_api::{WalletCommitmentTrees, WalletRead, WalletWrite};
use zcash_client_backend::proto::service;
use zcash_client_backend::scanning::{scan_block, Nullifiers, ScanningKeys};
use zcash_keys::keys::UnifiedFullViewingKey;
use zcash_primitives::merkle_tree::HashSer;
use zcash_protocol::consensus::{BlockHeight, NetworkUpgrade, Parameters};

use crate::blockcache::MemoryBlockCache;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::LightClient;
use crate::wallet::Db;

/// 在上游建议的区间里按优先级挑一个。
///
/// `None` 表示「随便，按上游给的顺序」—— 上游本就把 ChainTip 排在 Historic 前面。
/// 指定优先级时只在同优先级里挑；挑不到就返回 None，让调用方知道这条 lane 空了，
/// 而不是悄悄去扫另一条 lane（那会让宿主的进度显示和实际发生的事对不上）。
fn pick_range(suggested: &[ScanRange], preferred: Option<ScanPriority>) -> Option<ScanRange> {
    suggested
        .iter()
        .find(|r| !r.is_empty() && preferred.is_none_or(|p| r.priority() == p))
        .cloned()
}

/// 宿主可以指定的优先级。字符串而非数字：数字在跨边界时无从校验。
pub fn parse_priority(name: &str) -> Result<Option<ScanPriority>> {
    Ok(match name {
        "" | "any" => None,
        "chainTip" => Some(ScanPriority::ChainTip),
        "historic" => Some(ScanPriority::Historic),
        "verify" => Some(ScanPriority::Verify),
        "openAdjacent" => Some(ScanPriority::OpenAdjacent),
        "foundNote" => Some(ScanPriority::FoundNote),
        other => {
            return Err(RuntimeError::with(
                ErrorCode::InvalidScanPriority,
                json!({ "value": other, "allowed": ["any", "chainTip", "historic", "verify", "openAdjacent", "foundNote"] }),
            ))
        }
    })
}

/// 检测到重组时建议回退多少块。
///
/// Zcash 的最终性很快，深重组极罕见；退太多会让用户重扫大段历史，退太少则可能
/// 没退到分叉点之前、下一轮继续撞连续性错误。宿主可以在重复失败时自行加大。
const REORG_REWIND_BLOCKS: u32 = 10;

fn net_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": operation })).detail(e)
}

fn db_err(operation: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation })).detail(e)
}

/// 拉取某个池的全部子树根。
async fn subtree_roots<H: HashSer>(
    client: &mut LightClient,
    protocol: service::ShieldedProtocol,
    operation: &'static str,
) -> Result<Vec<CommitmentTreeRoot<H>>> {
    let mut request = service::GetSubtreeRootsArg::default();
    request.set_shielded_protocol(protocol);

    let mut stream = crate::network::with_timeout(
        async {
            client
                .get_subtree_roots(request)
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err(operation, e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        operation,
    )
    .await?;

    let mut roots = Vec::new();
    loop {
        // This is an inactivity timeout, not a deadline for the whole stream.
        // A healthy long subtree stream can run for as long as it needs, while
        // a stalled server still releases the carrier after one item timeout.
        let next = crate::network::with_timeout(
            async { stream.try_next().await.map_err(|e| net_err(operation, e)) },
            crate::network::DEFAULT_TIMEOUT_MS,
            operation,
        )
        .await?;
        let Some(root) = next else {
            break;
        };
        let hash = H::read(&root.root_hash[..]).map_err(|e| net_err(operation, e))?;
        roots.push(CommitmentTreeRoot::from_parts(
            BlockHeight::from_u32(root.completing_block_height as u32),
            hash,
        ));
    }
    Ok(roots)
}

/// 一次性准备：把三个池的子树根灌进钱包，并更新链尖。
///
/// **这是首次同步最慢的一步**（要下载全部子树根），且与 `batch_size` 无关 ——
/// 这正是之前 `syncAdvance` 看起来「卡住」的原因。把它单独暴露出来，
/// 宿主就能给出「正在准备」这样的独立进度，而不是干等一个不返回的调用。
///
/// 每轮同步开始时调一次即可。重复调用是幂等的，但会重新下载，没必要。
pub async fn prepare(db: &mut Db, client: &mut LightClient) -> Result<String> {
    let sapling: Vec<CommitmentTreeRoot<sapling::Node>> = subtree_roots(
        client,
        service::ShieldedProtocol::Sapling,
        "getSubtreeRoots.sapling",
    )
    .await?;
    let n_sapling = sapling.len();
    db.put_sapling_subtree_roots(0, &sapling)
        .map_err(|e| db_err("putSaplingSubtreeRoots", e))?;

    let orchard: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>> = subtree_roots(
        client,
        service::ShieldedProtocol::Orchard,
        "getSubtreeRoots.orchard",
    )
    .await?;
    let n_orchard = orchard.len();
    db.put_orchard_subtree_roots(0, &orchard)
        .map_err(|e| db_err("putOrchardSubtreeRoots", e))?;

    let ironwood: Vec<CommitmentTreeRoot<orchard::tree::MerkleHashOrchard>> = subtree_roots(
        client,
        service::ShieldedProtocol::Ironwood,
        "getSubtreeRoots.ironwood",
    )
    .await?;
    let n_ironwood = ironwood.len();
    db.put_ironwood_subtree_roots(0, &ironwood)
        .map_err(|e| db_err("putIronwoodSubtreeRoots", e))?;

    let tip = crate::network::chain_tip(client).await?;
    db.update_chain_tip(BlockHeight::from_u32(tip))
        .map_err(|e| db_err("updateChainTip", e))?;

    Ok(json!({
        "chainTip": tip,
        "saplingSubtrees": n_sapling,
        "orchardSubtrees": n_orchard,
        "ironwoodSubtrees": n_ironwood,
    })
    .to_string())
}

/// 只更新链尖，不碰子树根。
///
/// 子树根是凝固的历史（新分片几天才完成一个），`prepare` 却每次从索引 0
/// 全量重下约 1900 条 —— 主网实测 7.4s。每轮同步真正的新信息只有链尖，
/// 一个 unary 调用。`prepare` 留给开库后的第一次。
pub async fn tip(db: &mut Db, client: &mut LightClient) -> Result<String> {
    let tip = crate::network::chain_tip(client).await?;
    db.update_chain_tip(BlockHeight::from_u32(tip))
        .map_err(|e| db_err("updateChainTip", e))?;
    Ok(json!({ "chainTip": tip }).to_string())
}

/// 有界推进：最多处理 `max_batches` 批，每批 `batch_size` 个区块。
///
/// 返回进度 JSON，`done` 表示「已经没有待扫区间」。宿主据此决定是否继续调 ——
/// 本函数**永远不会**自己循环到同步完成。
pub async fn step(
    db: &mut Db,
    client: &mut LightClient,
    batch_size: u32,
    max_batches: u32,
    preferred: Option<ScanPriority>,
    active_ufvks: &[String],
) -> Result<String> {
    if batch_size == 0 || max_batches == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidBatchSize,
            json!({ "batchSize": batch_size, "maxBatches": max_batches }),
        ));
    }

    let params = *db.params();
    let wallet_ufvks = db
        .get_unified_full_viewing_keys()
        .map_err(|e| db_err("getUnifiedFullViewingKeys", e))?;
    let active_scanning_ufvks = select_active_scanning_ufvks(&params, wallet_ufvks, active_ufvks)?;
    let active_account_count = active_scanning_ufvks.len();
    let scanning_keys = ScanningKeys::from_account_ufvks(active_scanning_ufvks);
    let cache = MemoryBlockCache::new();

    let mut batches_done = 0u32;
    let mut blocks_scanned = 0u64;
    // 分阶段累计耗时。扫描慢的时候，「慢在下载还是慢在扫描」是完全不同的两件事，
    // 而在浏览器里两者的表现一模一样（页面不动），只能靠打点区分。
    let (mut ms_download, mut ms_treestate, mut ms_scan) = (0.0f64, 0.0f64, 0.0f64);
    let (mut scanned_from, mut scanned_to) = (None::<u32>, None::<u32>);
    let mut scanned_priority: Option<String> = None;

    while batches_done < max_batches {
        let suggested = db
            .suggest_scan_ranges()
            .map_err(|e| db_err("suggestScanRanges", e))?;

        // 按宿主给的优先级挑区间。**选择权在宿主，但候选只能来自上游建议** ——
        // 这样「先追链尖还是先回填历史」这类调度决策回到 App，而
        // 「哪些区间扫了才是安全的」仍由协议实现说了算，宿主没法乱指高度。
        let Some(range) = pick_range(&suggested, preferred) else {
            break; // 该优先级下没有待扫区间
        };

        // 只取这一批，剩下的留给下一次调用 —— 这就是「有界」的全部含义。
        let start = range.block_range().start;
        if scanned_from.is_none() {
            scanned_from = Some(u32::from(start));
        }
        scanned_priority = Some(format!("{:?}", range.priority()));

        let t_start = now_ms();

        // 下载：失败先原批重试一次，再对半收缩，地板 32 块。
        //
        // 2022 spam 墙区段（约 171 万~195 万高度）一批 500 块可达几十 MB，
        // 固定超时在慢链路上永远下不完 —— 不收缩的重试是原地撞墙，换端点
        // 也一样（哪台服务器这段都这么大）。成功的批量跨调用记忆（下方
        // thread_local）：不记忆时每轮都从常规值重爬梯子，在墙里等于每轮
        // 白烧两次超时；记住上次成功值、成功后再倍增回去，梯子只爬一次，
        // 出墙后几轮内自动恢复常规批量。wasm 单线程，thread_local 即会话级；
        // 载体重启后回到常规值，自愈。
        const MIN_DOWNLOAD_BATCH: u32 = 32;
        thread_local! {
            static DOWNLOAD_BATCH_HINT: std::cell::Cell<u32> =
                const { std::cell::Cell::new(0) };
            static DOWNLOAD_BATCH_STREAK: std::cell::Cell<u32> =
                const { std::cell::Cell::new(0) };
        }
        let hint = DOWNLOAD_BATCH_HINT.with(|h| h.get());
        let initial_len = if hint > 0 {
            std::cmp::min(batch_size, hint)
        } else {
            batch_size
        };
        let mut attempt_len: u32 = initial_len;
        let mut same_size_retry = 1u32;
        let (batch, batch_len) = loop {
            let attempt_end = std::cmp::min(start + attempt_len, range.block_range().end);
            let candidate = ScanRange::from_parts(start..attempt_end, range.priority());
            let candidate_len = candidate.len();
            trace(&format!(
                "step: 下载 {candidate_len} 块 @{}",
                u32::from(start)
            ));
            // 超时在 download_batch 内部按「停滞」判定；这里只兜真失败。
            let _ = candidate_len;
            match download_batch(client, &cache, &candidate).await {
                Ok(got) => break (candidate, got),
                Err(e) => {
                    if e.code != ErrorCode::NetworkError {
                        return Err(e);
                    }
                    if same_size_retry > 0 {
                        same_size_retry -= 1;
                        trace("step: 下载失败，原批重试");
                        continue;
                    }
                    if attempt_len <= MIN_DOWNLOAD_BATCH {
                        return Err(e);
                    }
                    attempt_len = std::cmp::max(MIN_DOWNLOAD_BATCH, attempt_len / 2);
                    trace(&format!("step: 下载超时，批量对半 -> {attempt_len}"));
                }
            }
        };
        // 缩过就记住缩后的值并清空连胜；**整批**（非链尖小批）在记忆值上
        // 连胜三次才向上探一档 —— 小批成功或单次侥幸都不算数，否则记忆值
        // 被顶回超时区来回震荡，每隔一轮就白烧一次探测超时。
        if attempt_len < initial_len {
            DOWNLOAD_BATCH_HINT.with(|h| h.set(attempt_len));
            DOWNLOAD_BATCH_STREAK.with(|s| s.set(0));
        } else if batch_len >= attempt_len as usize && initial_len < batch_size {
            let streak = DOWNLOAD_BATCH_STREAK.with(|s| {
                let v = s.get() + 1;
                s.set(v);
                v
            });
            if streak >= 3 {
                let grown = std::cmp::min(batch_size, attempt_len.saturating_mul(2));
                DOWNLOAD_BATCH_HINT.with(|h| h.set(grown));
                DOWNLOAD_BATCH_STREAK.with(|s| s.set(0));
                trace(&format!("step: 批量上探 -> {grown}"));
            }
        }
        // 预算截断时实际入库的可能少于请求区间，扫描终点以实收为准。
        let _ = &batch;
        scanned_to = Some(u32::from(start) + batch_len as u32);
        let t_downloaded = now_ms();
        ms_download += t_downloaded - t_start;

        // 钳位到 Sapling 激活高度之后。
        //
        // lightwalletd 在 Sapling 激活之前**没有 treestate**，而它对这种请求的
        // 错误响应会让 wasm 侧的 gRPC 调用**永久挂起** —— 不是报错，是不返回。
        // 这个坑我们自己的代码库早有记录（zcashWebSdk.ts 的 birthday 钳位）。
        let anchor = anchor_height(&params, start);

        let chain_state = crate::network::with_timeout(
            async {
                client
                    .get_tree_state(service::BlockId {
                        height: u64::from(anchor),
                        hash: Vec::new(),
                    })
                    .await
                    .map_err(|e| net_err("getTreeState", e))?
                    .into_inner()
                    .to_chain_state()
                    .map_err(|e| net_err("toChainState", e))
            },
            crate::network::DEFAULT_TIMEOUT_MS,
            "getTreeState",
        )
        .await?;

        let t_treestate = now_ms();
        ms_treestate += t_treestate - t_downloaded;
        trace(&format!("step: 开始扫描 {batch_len} 块"));

        // 重组在 scan_blocks_inline 内部按**类型**分类成 REORG_DETECTED
        // （带精确 atHeight）—— 这里不再做字符串嗅探。
        scan_blocks_inline(
            &params,
            &cache,
            db,
            &scanning_keys,
            start,
            &chain_state,
            batch_len,
        )?;

        ms_scan += now_ms() - t_treestate;
        trace(&format!("step: 扫描完成，累计 {ms_scan:.0}ms"));

        blocks_scanned += batch_len as u64;

        cache
            .delete(batch)
            .await
            .map_err(|e| db_err("cacheDelete", e))?;

        batches_done += 1;
    }

    // 扫完之后重新问一次，把剩余量报给宿主做进度条。
    let remaining = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;
    let remaining_blocks: u64 = remaining.iter().map(|r| r.len() as u64).sum();

    Ok(json!({
        "done": remaining_blocks == 0,
        "activeAccounts": active_account_count,
        "batchesDone": batches_done,
        "blocksScanned": blocks_scanned,
        "remainingBlocks": remaining_blocks,
        "remainingRanges": remaining.len(),
        // 这次实际扫的是哪一段、什么优先级 —— 宿主据此把进度归到正确的 lane
        "scannedFrom": scanned_from,
        "scannedTo": scanned_to,
        "scannedPriority": scanned_priority,
        "msDownload": ms_download as u32,
        "msTreeState": ms_treestate as u32,
        "msScan": ms_scan as u32,
    })
    .to_string())
}

/// 就地扫描一批区块并落库。**不使用上游的 `scan_cached_blocks`。**
///
/// ## 为什么不能用 `scan_cached_blocks`
///
/// 它内部把试解密交给批量 runner：
///
/// ```text
/// Tasks::run_task()  ->  rayon::spawn_fifo(|| task.run())
/// ```
///
/// 而 `scan_cached_blocks` 用的正是 `Tasks for ()`，该实现只覆写了 `add_task`，
/// **没有覆写 `run_task`**，于是仍然走 rayon。wasm32 单线程环境下 rayon 全局线程池
/// 没有工作线程，任务被排队但永远不会执行；随后 `BatchReceiver::into_results()`
/// 在通道上等一个永远不来的结果，**把主线程彻底堵死**。
///
/// 表现极具迷惑性：空区块不会产生批次，所以扫描一路正常，直到遇到**第一个含
/// 屏蔽输出的区块**才卡住 —— 看起来像「批量大到某个阈值就挂」，实际与批量无关。
/// 页面无响应、无错误、超时定时器不触发，连 CDP 都服务不了。
///
/// 这里改用公开的 `scanning::scan_block`（它等价于 `runners = None`），
/// 试解密就地完成，不碰 rayon。代价是失去批量试解密的摊销优化（批量求逆），
/// 换来的是在单线程目标上能正确工作，且**不需要 fork 上游**。
fn scan_blocks_inline<IvkTag>(
    params: &zcash_protocol::consensus::Network,
    cache: &MemoryBlockCache,
    db: &mut Db,
    scanning_keys: &ScanningKeys<<Db as WalletRead>::AccountId, IvkTag>,
    from_height: BlockHeight,
    from_state: &ChainState,
    limit: usize,
) -> Result<()>
where
    IvkTag: Copy + Hash + Eq + Send + 'static,
{
    let mut nullifiers = Nullifiers::unspent(db).map_err(|e| db_err("unspentNullifiers", e))?;

    let mut prior = if from_height > BlockHeight::from(0) {
        db.block_metadata(from_height - 1)
            .map_err(|e| db_err("blockMetadata", e))?
    } else {
        None
    };

    let mut scanned_blocks = Vec::new();
    cache
        .with_blocks::<_, <Db as WalletRead>::Error>(Some(from_height), Some(limit), |block| {
            let scanned = scan_block(params, block, scanning_keys, &nullifiers, prior.as_ref())
                .map_err(ChainError::Scan)?;
            nullifiers.update_with(&scanned);
            prior = Some(scanned.to_block_metadata());
            scanned_blocks.push(scanned);
            Ok(())
        })
        .map_err(|e| match &e {
            // 链重组：上游的**类型化**判定（和参考循环同一个谓词）。这里是错误
            // 还带着类型的最后一刻 —— 一旦 format 成字符串再去嗅探变体名，就是
            // 上一个 bug 的形状：匹配串写错，重组识别静默失效，钱包在同一高度
            // 死循环整夜。at_height 也因此是精确值，不再用批起点估。
            ChainError::Scan(scan_err) if scan_err.is_continuity_error() => {
                let at = u32::from(scan_err.at_height());
                RuntimeError::with(
                    ErrorCode::ReorgDetected,
                    json!({
                        "atHeight": at,
                        "suggestedRewindHeight": at.saturating_sub(REORG_REWIND_BLOCKS),
                    }),
                )
                .detail(format!("{e:?}"))
            }
            _ => db_err("scanBlock", format!("{e:?}")),
        })?;

    db.put_blocks(from_state, scanned_blocks)
        .map_err(|e| db_err("putBlocks", e))?;
    Ok(())
}

/// Selects the host-authorized UFVKs and removes every non-Orchard component before scanning.
///
/// `ScanningKeys::from_account_ufvks` normally creates Sapling, Orchard, and Ironwood keys. The
/// runtime intentionally supplies an ephemeral Orchard-only UFVK instead, so the official
/// scanner still maintains all required trees while Sapling ownership discovery remains disabled.
fn select_active_scanning_ufvks<AccountId>(
    params: &zcash_protocol::consensus::Network,
    wallet_ufvks: HashMap<AccountId, UnifiedFullViewingKey>,
    active_ufvks: &[String],
) -> Result<Vec<(AccountId, UnifiedFullViewingKey)>>
where
    AccountId: Copy + Eq + Hash,
{
    if active_ufvks.is_empty() {
        return Err(RuntimeError::new(ErrorCode::NoActiveAccounts));
    }

    let requested = active_ufvks
        .iter()
        .map(|encoded| {
            UnifiedFullViewingKey::decode(params, encoded)
                .map(|ufvk| ufvk.encode(params))
                .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))
        })
        .collect::<Result<HashSet<_>>>()?;

    let mut selected = Vec::with_capacity(requested.len());
    let mut matched = HashSet::with_capacity(requested.len());
    for (account_id, ufvk) in wallet_ufvks {
        let canonical = ufvk.encode(params);
        if requested.contains(&canonical) && matched.insert(canonical) {
            selected.push((account_id, orchard_only_ufvk(params, &ufvk)?));
        }
    }

    if matched.len() != requested.len() {
        return Err(RuntimeError::with(
            ErrorCode::AccountNotFound,
            json!({
                "requestedActiveUfvks": requested.len(),
                "matchedActiveUfvks": matched.len(),
            }),
        ));
    }

    Ok(selected)
}

fn orchard_only_ufvk(
    params: &zcash_protocol::consensus::Network,
    ufvk: &UnifiedFullViewingKey,
) -> Result<UnifiedFullViewingKey> {
    let encoded = ufvk.encode(params);
    let (network, container) =
        Ufvk::decode(&encoded).map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;
    let orchard = container
        .items()
        .into_iter()
        .filter(|item| matches!(item, Fvk::Orchard(_)))
        .collect::<Vec<_>>();
    let container = Ufvk::try_from_items(orchard)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;
    UnifiedFullViewingKey::decode(params, &container.encode(&network))
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))
}

/// 单个账户的同步完成度。**不发网络请求。**
///
/// 共享库里所有账户共用一条扫描队列，所以「全库剩余多少」对单个账户是误导的：
/// 刚加进来的账户会把全库进度拉回去，让早就扫完的账户看起来也没好。
///
/// 这里的判据是：**高于该账户 birthday 的待扫区间**才与它有关。低于 birthday
/// 的区间是别的账户的历史，与它无关。
pub fn account_status(db: &Db, account_uuid: &str) -> Result<String> {
    let account_id = crate::account::parse_account_id(db, account_uuid)?;
    let birthday = u32::from(
        db.get_account_birthday(account_id)
            .map_err(|e| db_err("getAccountBirthday", e))?,
    );

    let ranges = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;

    // 只算与这个账户有关的那部分
    let mut remaining: u64 = 0;
    let mut lowest_pending: Option<u32> = None;
    for r in &ranges {
        let end = u32::from(r.block_range().end);
        if end <= birthday {
            continue;
        }
        let start = u32::from(r.block_range().start).max(birthday);
        remaining += u64::from(end.saturating_sub(start));
        lowest_pending = Some(lowest_pending.map_or(start, |cur: u32| cur.min(start)));
    }

    let summary = db
        .get_wallet_summary(zcash_client_backend::data_api::wallet::ConfirmationsPolicy::default())
        .map_err(|e| db_err("walletSummary", e))?;
    let chain_tip = summary.as_ref().map(|s| u32::from(s.chain_tip_height()));

    // 扫到哪了 = 与本账户相关的最低待扫高度；没有待扫就是已追到链尖。
    let scanned_to = lowest_pending.or(chain_tip);

    Ok(json!({
        "birthdayHeight": birthday,
        "chainTip": chain_tip,
        "scannedToHeight": scanned_to,
        "remainingBlocks": remaining,
        "isComplete": remaining == 0,
    })
    .to_string())
}

/// 只读：钱包当前建议扫描哪些区间。**不发任何网络请求。**
///
/// 用途有二：给宿主做进度条；以及在同步卡住时确认它到底想扫哪一段 ——
/// 不用猜，直接看。
pub fn peek(db: &Db) -> Result<String> {
    let ranges = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;

    let items: Vec<_> = ranges
        .iter()
        .map(|r| {
            json!({
                "start": u32::from(r.block_range().start),
                "end": u32::from(r.block_range().end),
                "len": r.len(),
                "priority": format!("{:?}", r.priority()),
            })
        })
        .collect();

    let total: u64 = ranges.iter().map(|r| r.len() as u64).sum();
    Ok(json!({ "ranges": items, "totalBlocks": total }).to_string())
}

/// 诊断探针：只做**下载**，不扫描、不写库。返回各阶段耗时。
///
/// 用来把「网络慢/挂」与「扫描慢/挂」分开 —— 这两者在浏览器里的表现完全一样
/// （都是页面无响应），但成因和修法完全不同。
///
/// 也可以当网络健康检查用：它不改变任何钱包状态。
pub async fn probe_download(db: &Db, client: &mut LightClient, batch_size: u32) -> Result<String> {
    let t0 = now_ms();

    let ranges = db
        .suggest_scan_ranges()
        .map_err(|e| db_err("suggestScanRanges", e))?;
    let Some(range) = ranges.into_iter().find(|r| !r.is_empty()) else {
        return Ok(json!({ "skipped": "noRange" }).to_string());
    };

    let start = range.block_range().start;
    let end = std::cmp::min(start + batch_size, range.block_range().end);
    let batch = ScanRange::from_parts(start..end, range.priority());
    let t_range = now_ms();

    let cache = MemoryBlockCache::new();
    download_batch(client, &cache, &batch).await?;
    let t_download = now_ms();

    let params = *db.params();
    let anchor = anchor_height(&params, start);
    crate::network::with_timeout(
        async {
            client
                .get_tree_state(service::BlockId {
                    height: u64::from(anchor),
                    hash: Vec::new(),
                })
                .await
                .map_err(|e| net_err("getTreeState", e))?
                .into_inner()
                .to_chain_state()
                .map_err(|e| net_err("toChainState", e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        "getTreeState",
    )
    .await?;
    let t_tree = now_ms();

    Ok(json!({
        "rangeStart": u32::from(start),
        "rangeEnd": u32::from(end),
        "blocksCached": cache.len(),
        "anchorHeight": anchor,
        "msSuggestRanges": (t_range - t0) as u32,
        "msDownload": (t_download - t_range) as u32,
        "msTreeState": (t_tree - t_download) as u32,
    })
    .to_string())
}

#[cfg(target_arch = "wasm32")]
fn now_ms() -> f64 {
    js_sys::Date::now()
}

#[cfg(not(target_arch = "wasm32"))]
fn now_ms() -> f64 {
    use std::time::{SystemTime, UNIX_EPOCH};
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_secs_f64() * 1000.0)
        .unwrap_or(0.0)
}

/// 阶段打点。一次调用要跑很久时，最后一条打点就指出它停在哪个阶段 ——
/// 浏览器里除此之外看不到任何东西。
#[cfg(target_arch = "wasm32")]
pub(crate) fn trace(msg: &str) {
    #[wasm_bindgen::prelude::wasm_bindgen]
    extern "C" {
        #[wasm_bindgen(js_namespace = console)]
        fn log(s: &str);
    }
    log(msg);
}

#[cfg(not(target_arch = "wasm32"))]
pub(crate) fn trace(msg: &str) {
    println!("{msg}");
}

/// 取 `start - 1` 作为锚点，但不低于 Sapling 激活高度。
///
/// 低于该高度时 lightwalletd 没有 treestate，其错误响应会让 wasm 的 gRPC
/// 调用永久挂起（不是返回错误），所以必须在发请求前钳住。
fn anchor_height(params: &zcash_protocol::consensus::Network, start: BlockHeight) -> u32 {
    let sapling = params
        .activation_height(NetworkUpgrade::Sapling)
        .map(u32::from)
        .unwrap_or(1);
    u32::from(start).saturating_sub(1).max(sapling)
}

/// 软预算：块还在到达时不判死，但总时长过线后**把已到手的连续前缀拿去扫**，
/// 不再等剩余的块。超时语义因此从「总时长一刀切」变成「停滞才是死」——
/// 一个健康推进的大批（spam 墙里 500 块几十 MB）不再被硬杀重下；
/// 慢链路的每一秒都变成已入库的块，批量大小由实际吞吐逐秒决定。
const DOWNLOAD_SOFT_BUDGET_MS: f64 = 30_000.0;

/// 流式下载一段区块并写入缓存，返回**实际入库的块数**（连续前缀）。
///
/// 每条流消息各自套 DEFAULT_TIMEOUT_MS 的**停滞**超时：只有一颗块都没
/// 收到过的停滞才向上抛错（交给调用方重试/对半）；已有收获的停滞和
/// 预算超线一样，截断收工。
async fn download_batch(
    client: &mut LightClient,
    cache: &MemoryBlockCache,
    range: &ScanRange,
) -> Result<usize> {
    let start = service::BlockId {
        height: range.block_range().start.into(),
        hash: Vec::new(),
    };
    let end = service::BlockId {
        height: (range.block_range().end - 1).into(),
        hash: Vec::new(),
    };

    let mut stream = crate::network::with_timeout(
        async {
            client
                .get_block_range(service::BlockRange {
                    start: Some(start),
                    end: Some(end),
                    pool_types: vec![],
                })
                .await
                .map(|response| response.into_inner())
                .map_err(|e| net_err("getBlockRange", e))
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        "getBlockRange",
    )
    .await?;

    let started = now_ms();
    let mut blocks = Vec::new();
    loop {
        if !blocks.is_empty() && now_ms() - started > DOWNLOAD_SOFT_BUDGET_MS {
            trace(&format!("step: 下载达预算，截断扫已获 {} 块", blocks.len()));
            break;
        }
        let next = crate::network::with_timeout(
            async {
                stream
                    .try_next()
                    .await
                    .map_err(|e| net_err("getBlockRange", e))
            },
            crate::network::DEFAULT_TIMEOUT_MS,
            "getBlockRange",
        )
        .await;
        match next {
            Ok(Some(block)) => blocks.push(block),
            Ok(None) => break, // 流正常结束（整批到齐）
            Err(e) => {
                if blocks.is_empty() {
                    return Err(e); // 真停滞且颗粒无收
                }
                trace(&format!("step: 下载停滞，截断扫已获 {} 块", blocks.len()));
                break;
            }
        }
    }

    let got = blocks.len();
    if got == 0 {
        return Err(net_err("getBlockRange", "empty stream for non-empty range"));
    }
    cache
        .insert(blocks)
        .await
        .map_err(|e| db_err("cacheInsert", e))?;
    Ok(got)
}

/// 回退钱包到指定高度，让之后的同步重扫这段区间。
///
/// **破坏性**：该高度之上的扫描结果（note、witness、承诺树检查点）会被丢弃。
/// 已广播的交易不受影响（它们在链上），但本地未确认记录可能丢失。
///
/// 返回实际回退到的高度：上游可能因承诺树检查点位置退得比请求的更靠前。
pub fn rewind_to(db: &mut Db, height: u32) -> Result<u32> {
    let target = BlockHeight::from_u32(height);
    let actual = db
        .truncate_to_height(target)
        .map_err(|e| db_err("truncateToHeight", e))?;
    Ok(u32::from(actual))
}

/// Re-queues an inclusive block range without truncating cached wallet state.
///
/// The host uses this when a previously inactive UFVK becomes active again. Because the upstream
/// scan queue is shared by the whole network database, other active identities may have advanced
/// it while this identity was paused. The host owns the pause cursor and passes the first height
/// that must be trial-decrypted again. A small reorg safety margin should be applied by the host.
pub fn queue_rescan_from(db: &mut Db, from_height: u32) -> Result<String> {
    let Some(tip) = db
        .chain_height()
        .map_err(|e| db_err("chainHeight", e))?
        .map(u32::from)
    else {
        return Err(RuntimeError::new(ErrorCode::NotSynced));
    };

    if from_height > tip {
        return Ok(json!({
            "queued": false,
            "fromHeight": from_height,
            "toHeight": tip,
        })
        .to_string());
    }

    let range = BlockHeight::from_u32(from_height)..BlockHeight::from_u32(tip.saturating_add(1));
    let ranges = (range, Vec::new()).into();
    db.queue_rescans(ranges, ScanPriority::Historic)
        .map_err(|e| db_err("queueRescanFrom", e))?;

    Ok(json!({
        "queued": true,
        "fromHeight": from_height,
        "toHeight": tip,
    })
    .to_string())
}

/// 刷新账户的透明 UTXO。
///
/// ## 为什么必须单独做
///
/// 屏蔽 note 是扫区块试解密出来的，透明 UTXO 不是 —— 它们在链上是公开的，
/// 由 lightwalletd 按地址直接返回。`scan_cached_blocks` 不会产出它们。
///
/// 不做这一步的后果是**静默的**：透明余额恒为 0，屏蔽（把透明余额转进隐私池）
/// 永远报「没有可屏蔽的钱」，而扫链看起来一切正常。
///
/// 只查该账户名下的透明收款地址（含找零与独立地址）—— 漏掉任何一个都会
/// 让那部分钱在钱包里不存在。
pub async fn refresh_transparent_utxos(
    db: &mut Db,
    client: &mut LightClient,
    account_uuid: &str,
) -> Result<String> {
    use ::transparent::bundle::{OutPoint, TxOut};
    use zcash_client_backend::proto::service::GetAddressUtxosArg;
    use zcash_client_backend::wallet::WalletTransparentOutput;
    use zcash_protocol::value::Zatoshis;

    let params = *db.params();

    let account_id = crate::account::parse_account_id(db, account_uuid)?;

    // 地址 → 账户。返回的每条 UTXO 靠它归属，否则批量查回来就分不清是谁的。
    let mut owner_of: std::collections::HashMap<String, <Db as WalletRead>::AccountId> =
        std::collections::HashMap::new();
    let mut addresses: Vec<String> = Vec::new();
    let receivers = db
        .get_transparent_receivers(account_id, true, true)
        .map_err(|e| db_err("getTransparentReceivers", e))?;
    for addr in receivers.keys() {
        let encoded = zcash_keys::encoding::encode_transparent_address_p(&params, addr);
        owner_of.insert(encoded.clone(), account_id);
        addresses.push(encoded);
    }

    if addresses.is_empty() {
        return Ok(json!({ "addresses": 0, "utxos": 0, "storedZat": 0 }).to_string());
    }
    let address_count = addresses.len();
    let start_height = u64::from(
        db.utxo_query_height(account_id)
            .map_err(|e| db_err("utxoQueryHeight", e))?,
    );

    let replies = crate::network::with_timeout(
        async {
            client
                .get_address_utxos(GetAddressUtxosArg {
                    addresses,
                    start_height,
                    max_entries: 0,
                })
                .await
                .map_err(|e| net_err("getAddressUtxos", e))
                .map(|r| r.into_inner().address_utxos)
        },
        crate::network::DEFAULT_TIMEOUT_MS,
        "getAddressUtxos",
    )
    .await?;

    let mut stored = 0u32;
    let mut total: u64 = 0;
    for utxo in replies {
        let Ok(txid) = <[u8; 32]>::try_from(&utxo.txid[..]) else {
            continue;
        };
        let Ok(index) = u32::try_from(utxo.index) else {
            continue;
        };
        let Ok(value) = u64::try_from(utxo.value_zat)
            .map_err(|_| ())
            .and_then(|v| Zatoshis::from_u64(v).map_err(|_| ()))
        else {
            continue;
        };

        // `Script` 内部包着 zcash_script 的类型，构造函数不公开；`read` 走的是
        // 带长度前缀的编码，所以这里自己拼前缀，避免为了一个构造再引一个 crate。
        let Some(script_pubkey) = read_script(&utxo.script) else {
            continue;
        };
        let txout = TxOut::new(value, script_pubkey);
        let height = u32::try_from(utxo.height).ok().map(BlockHeight::from_u32);

        // 归属靠地址查回来。批量查询里混着多个账户的输出，用「当前账户」会把
        // 别人的钱记到自己头上。
        let Some(&owner) = owner_of.get(&utxo.address) else {
            continue;
        };

        let Some(output) = WalletTransparentOutput::from_parts(
            OutPoint::new(txid, index),
            txout,
            height,
            Some(owner),
            None,
            None,
        ) else {
            // recipient_address 解不出来的输出我们无法归属，跳过而不是猜。
            continue;
        };

        db.put_received_transparent_utxo(&output)
            .map_err(|e| db_err("putReceivedTransparentUtxo", e))?;
        stored += 1;
        total += u64::from(value);
    }

    Ok(json!({
        "addresses": address_count,
        "utxos": stored,
        "storedZat": total,
    })
    .to_string())
}

/// 把裸 script 字节读成 `Script`。
///
/// 上游的 `Script::read` 期待 CompactSize 长度前缀（链上编码），而
/// lightwalletd 的 `GetAddressUtxos` 给的是裸字节，所以这里补上前缀再读。
/// 这样不必为了一个构造函数把 `zcash_script` 也拉进依赖树。
fn read_script(raw: &[u8]) -> Option<::transparent::address::Script> {
    let mut buf = Vec::with_capacity(raw.len() + 9);
    // CompactSize：<253 一个字节；再大用 0xfd + u16 / 0xfe + u32
    match raw.len() {
        n if n < 253 => buf.push(n as u8),
        n if n <= u16::MAX as usize => {
            buf.push(0xfd);
            buf.extend_from_slice(&(n as u16).to_le_bytes());
        }
        n => {
            buf.push(0xfe);
            buf.extend_from_slice(&(n as u32).to_le_bytes());
        }
    }
    buf.extend_from_slice(raw);
    ::transparent::address::Script::read(&buf[..]).ok()
}

#[cfg(test)]
mod tests {
    use super::*;
    use zcash_keys::keys::UnifiedSpendingKey;
    use zcash_protocol::consensus::Network;

    fn test_ufvk(network: &Network, seed_byte: u8) -> UnifiedFullViewingKey {
        UnifiedSpendingKey::from_seed(
            network,
            &[seed_byte; 32],
            0u32.try_into().expect("valid ZIP-32 account index"),
        )
        .expect("test seed derives a USK")
        .to_unified_full_viewing_key()
    }

    fn selected_ufvks_for(
        network: &Network,
        wallet_ufvks: HashMap<u32, UnifiedFullViewingKey>,
        active_ufvks: &[String],
    ) -> Result<Vec<(u32, UnifiedFullViewingKey)>> {
        select_active_scanning_ufvks(network, wallet_ufvks, active_ufvks)
    }

    #[test]
    fn anchor_never_precedes_sapling_activation() {
        for net in [Network::MainNetwork, Network::TestNetwork] {
            let sapling = u32::from(net.activation_height(NetworkUpgrade::Sapling).unwrap());
            // 请求高度远早于 Sapling —— 必须被抬到激活高度，否则 gRPC 会挂死
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(1)), sapling);
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(sapling)), sapling);
            // 正常情况取 start - 1
            let h = sapling + 1000;
            assert_eq!(anchor_height(&net, BlockHeight::from_u32(h)), h - 1);
        }
    }

    #[test]
    fn only_explicitly_active_ufvk_gets_scanning_keys() {
        let network = Network::TestNetwork;
        let active = test_ufvk(&network, 1);
        let inactive = test_ufvk(&network, 2);
        let keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active.clone()), (20, inactive)]),
                &[active.encode(&network)],
            )
            .expect("active UFVK is present in the wallet database"),
        );

        assert!(keys.sapling().is_empty());
        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
        assert!(keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 10));
        assert!(keys
            .ironwood()
            .keys()
            .all(|(account_id, _)| *account_id == 10));
    }

    #[test]
    fn duplicate_active_ufvk_is_deduplicated() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 3);
        let encoded = ufvk.encode(&network);
        let keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, ufvk)]),
                &[encoded.clone(), encoded],
            )
            .expect("duplicate host aliases select one runtime identity"),
        );

        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
    }

    #[test]
    fn empty_active_set_fails_closed() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 4);
        let error = selected_ufvks_for(&network, HashMap::from([(10, ufvk)]), &[])
            .expect_err("an empty set must not fall back to every database account");

        assert_eq!(error.code, ErrorCode::NoActiveAccounts);
    }

    #[test]
    fn sapling_component_never_reaches_scanning_keys() {
        let network = Network::TestNetwork;
        let ufvk = test_ufvk(&network, 5);
        assert!(ufvk.sapling().is_some());
        assert!(ufvk.orchard().is_some());

        let encoded = ufvk.encode(&network);
        let selected =
            select_active_scanning_ufvks(&network, HashMap::from([(10, ufvk)]), &[encoded])
                .expect("active UFVK is present in the wallet database");
        assert!(selected[0].1.sapling().is_none());
        assert!(selected[0].1.orchard().is_some());

        let keys = ScanningKeys::from_account_ufvks(selected);
        assert!(keys.sapling().is_empty());
        assert_eq!(keys.orchard().len(), 2);
        assert_eq!(keys.ironwood().len(), 2);
    }

    #[test]
    fn paused_identity_can_requeue_range_advanced_by_another_identity() {
        let network = Network::TestNetwork;
        let paused = test_ufvk(&network, 6);
        let active = test_ufvk(&network, 7);
        let active_keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active.clone()), (20, paused.clone())]),
                &[active.encode(&network)],
            )
            .expect("the active identity can advance the shared queue alone"),
        );
        assert!(active_keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 10));

        let mut db = crate::wallet::open_and_migrate("test", ":memory:")
            .expect("in-memory wallet database opens");
        let activation = u32::from(
            network
                .activation_height(NetworkUpgrade::Sapling)
                .expect("testnet has Sapling activation"),
        );
        let resume_from = activation + 50;
        let tip = activation + 100;
        db.update_chain_tip(BlockHeight::from_u32(tip))
            .expect("chain tip update succeeds");

        let result: serde_json::Value = serde_json::from_str(
            &queue_rescan_from(&mut db, resume_from).expect("resume range is queued"),
        )
        .expect("queue result is JSON");
        assert_eq!(result["queued"], true);
        assert_eq!(result["fromHeight"], resume_from);
        assert_eq!(result["toHeight"], tip);

        let ranges = db.suggest_scan_ranges().expect("scan queue is readable");
        assert!(ranges.iter().any(|range| {
            range.priority() == ScanPriority::Historic
                && u32::from(range.block_range().start) <= resume_from
                && u32::from(range.block_range().end) >= tip + 1
        }));

        let resumed_keys = ScanningKeys::from_account_ufvks(
            selected_ufvks_for(
                &network,
                HashMap::from([(10, active), (20, paused.clone())]),
                &[paused.encode(&network)],
            )
            .expect("the resumed identity is explicitly selected"),
        );
        assert!(resumed_keys
            .orchard()
            .keys()
            .all(|(account_id, _)| *account_id == 20));
    }
}
