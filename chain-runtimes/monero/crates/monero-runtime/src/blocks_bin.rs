//! 批量取块：`get_blocks.bin`，按 wallet2 的信任模型。
//!
//! 一次请求拿回一整批块，连同块里的交易和扫描要用的输出索引 —— 逐块取要
//! 3～4 次往返一块，这里是一次往返几十上百块。
//!
//! ## 为什么不直接用 monero-oxide 自带的那条
//!
//! 它也走 `get_blocks.bin`，但坚持**用交易内容反算哈希、再与区块头的交易哈希
//! 清单比对**。剪枝交易要反算哈希得有节点给的 `prunable_hash`，而已发布的
//! monerod（截至 v0.18.5.1）把它发成全零（monero-project/monero#10120，修复
//! #10652 只在 master）。于是它每次都整批放弃，退回逐块 JSON。
//!
//! ## 这里的信任模型（与 wallet2、Cake、Feather 相同）
//!
//! 直接采用区块头里的交易哈希清单，不以交易内容反算去验证。代价：恶意节点
//! 可以在真实哈希下塞假的交易内容，让余额显示失真（虚报或藏掉一笔到账）；
//! 偷不走钱 —— 花费要私钥，拿假输出去花会被网络拒绝。wallet2 见
//! `wallet2::process_new_blockchain_entry`，它用的就是 `b.tx_hashes[idx]`。
//!
//! 仍然做的检查：交易数与区块头清单一致、每块的输出索引条数与交易输出数一致、
//! 块高从请求起点连续。块与块之间的衔接（前驱哈希、RingCT 游标）照旧由
//! `scan::scan_blocks` 校验。
//!
//! 请求格式与解析骨架取自 monero-oxide 的 `monero-daemon-rpc`
//! （`src/bin_rpc/blocks_bin.rs`、`epee.rs`，MIT）。

use std::{
    cell::{Cell, RefCell},
    future::Future,
    pin::Pin,
    task::Poll,
};

use monero_daemon_rpc::HttpTransport;
use monero_epee::{Epee, EpeeEntry, EpeeError, Type, HEADER, VERSION};
use monero_wallet::{
    block::Block,
    interface::ScannableBlock,
    transaction::{Pruned, Transaction},
};

use crate::network::{Transport, MAX_RESPONSE_BYTES};
use crate::perf::now_ms;

/// 批量路径的失败分两种，处理方式截然不同。
#[derive(Debug)]
pub enum BlocksBinError {
    /// 网络问题：这一步失败，下一轮重试。不能据此判定节点不支持。
    Network(String),
    /// 节点回的东西不对（不支持、格式不符、数量对不上）：对这个节点不再走
    /// 批量路径，退回逐块。
    Protocol(String),
}

fn protocol(detail: impl Into<String>) -> BlocksBinError {
    BlocksBinError::Protocol(detail.into())
}

fn epee_err(e: EpeeError) -> BlocksBinError {
    protocol(format!("epee: {e:?}"))
}

// ── 批量大小：自适应，上限与 wallet2 相同 ─────────────────────────────────
//
// wallet2 每次 `getblocks.bin` 不设上限，节点最多回 1000 块 / 2 万笔交易。在好
// 网络上那是最快的；在翻墙、抖动的网络上一个十几 MB 的请求可能迟迟走不完。
// 所以大小随实测调整：快就翻倍，一路涨到 1000；慢或失败就减半，最低 10 块。
// 一步只发一个请求、拿到就扫、就落库 —— 块是一个请求一个请求地"滴"进来的，
// 前面拿到的不会因为后面的慢而作废。

/// 与 wallet2 / monerod 的单次上限一致（`COMMAND_RPC_GET_BLOCKS_FAST_MAX_BLOCK_COUNT`）。
pub const MAX_BATCH_BLOCKS: u64 = 1000;
/// 再慢的网络也至少这么多一批，否则往返开销压过数据本身。
pub const MIN_BATCH_BLOCKS: u64 = 10;
/// 起步大小：不知道网络如何时，一个请求十秒内能走完的量级。
pub const INITIAL_BATCH_BLOCKS: u64 = 100;
/// 比这快就加倍。
const GROW_BELOW_MS: f64 = 8_000.0;
/// 比这慢就减半。
const SHRINK_ABOVE_MS: f64 = 25_000.0;

thread_local! {
    static BATCH_BLOCKS: Cell<u64> = const { Cell::new(INITIAL_BATCH_BLOCKS) };
}

/// 下一个请求要多少块。
pub fn batch_blocks() -> u64 {
    BATCH_BLOCKS.with(Cell::get)
}

fn record_success(elapsed_ms: f64) {
    BATCH_BLOCKS.with(|size| {
        let current = size.get();
        if elapsed_ms < GROW_BELOW_MS {
            size.set((current * 2).min(MAX_BATCH_BLOCKS));
        } else if elapsed_ms > SHRINK_ABOVE_MS {
            size.set((current / 2).max(MIN_BATCH_BLOCKS));
        }
    });
}

fn record_failure() {
    BATCH_BLOCKS.with(|size| size.set((size.get() / 2).max(MIN_BATCH_BLOCKS)));
}

// ── 请求与流水线 ─────────────────────────────────────────────────────────
//
// 一次只发一个请求，与 wallet2 相同。插件实测（2026-09-23，cakewallet 节点）：
// 三路并发时每一步都是阶梯状 —— 第一个请求 1～4 s 回来，另外两个要 17～38 s：
// 节点在排队（monerod 生成 `get_blocks.bin` 要占区块链数据库的锁，公共节点也
// 可能按 IP 限并发）。提速靠流水线（`prefetch`）：扫这一批的同时，下一批已经
// 在路上。

/// 一个请求的结果，以及它从发出到完成用了多久（预取的从预取那一刻算起）。
type RequestOutcome = (Result<Vec<ScannableBlock>, BlocksBinError>, f64);
type RequestFuture = Pin<Box<dyn Future<Output = RequestOutcome>>>;

/// 已经发出、结果还没被取走的下一个请求。
struct Prefetch {
    url: String,
    start: u64,
    count: u64,
    future: RequestFuture,
}

thread_local! {
    static PREFETCH: RefCell<Option<Prefetch>> = const { RefCell::new(None) };
}

fn request_future(url: &str, start: u64, count: u64) -> RequestFuture {
    let url = url.to_owned();
    Box::pin(async move {
        // 第一次被 poll 时请求才真正发出，计时也从这里开始。
        let started = now_ms();
        let transport = Transport::new(&url);
        let result = request_once(&transport, start, count).await;
        (result, now_ms() - started)
    })
}

/// 取走与这一段完全吻合的预取结果。不吻合（换了节点、回退重扫、区间变了）
/// 就丢掉 —— 宁可重发，也不能拿错一段数据。
fn take_prefetched(url: &str, start: u64, count: u64) -> Option<RequestFuture> {
    PREFETCH.with(|slot| match slot.borrow_mut().take() {
        Some(p) if p.url == url && p.start == start && p.count == count => Some(p.future),
        _ => None,
    })
}

/// 取回的块，以及它们是否来自上一步的预取。
pub struct Fetched {
    pub blocks: Vec<ScannableBlock>,
    pub prefetch_hit: bool,
}

/// 网络失败时的重试退避（毫秒），长度即额外重试次数。每次重试把请求减半 ——
/// 一个在当前网络上走不完的大请求，拆小了往往就能走完。协议错误不重试。
const RETRY_BACKOFF_MS: [u32; 2] = [500, 1500];

async fn backoff(ms: u32) {
    #[cfg(target_arch = "wasm32")]
    gloo_timers::future::TimeoutFuture::new(ms).await;
    #[cfg(not(target_arch = "wasm32"))]
    tokio::time::sleep(std::time::Duration::from_millis(u64::from(ms))).await;
}

/// 从 `start` 起取至多 `limit` 块，按高度排好。`start` 必须非零 —— monerod
/// 只在 `start_height` 非零时才认这个字段。
///
/// 只发**一个**请求（失败时以更小的请求重试），节点给多少就收多少：它可能因为
/// 2 万笔交易的上限少给，剩下的由下一步接着要。
pub async fn fetch(base_url: &str, start: u64, limit: u64) -> Result<Fetched, BlocksBinError> {
    if start == 0 {
        return Err(protocol("get_blocks.bin cannot start at the genesis block"));
    }
    let mut count = batch_blocks().min(limit).max(1);
    let mut attempt = 0;
    loop {
        let prefetched = if attempt == 0 {
            take_prefetched(base_url, start, count)
        } else {
            None
        };
        let prefetch_hit = prefetched.is_some();
        let future = prefetched.unwrap_or_else(|| request_future(base_url, start, count));
        match future.await {
            (Ok(blocks), elapsed_ms) => {
                record_success(elapsed_ms);
                return Ok(Fetched {
                    blocks,
                    prefetch_hit,
                });
            }
            (Err(BlocksBinError::Network(detail)), _) => {
                record_failure();
                let Some(&wait) = RETRY_BACKOFF_MS.get(attempt) else {
                    return Err(BlocksBinError::Network(format!(
                        "after {} attempts, last with {count} blocks: {detail}",
                        attempt + 1
                    )));
                };
                attempt += 1;
                count = batch_blocks().min(limit).max(1);
                backoff(wait).await;
            }
            (Err(protocol_error), _) => return Err(protocol_error),
        }
    }
}

/// 现在就发出从 `start` 起、按当前批量大小（不超过 `limit`）的请求，结果留给下一步。
///
/// wallet2 的做法（`wallet2::refresh`："pull the next set of blocks while we're
/// processing the current one"）。在 wasm 里不需要线程：请求一旦发出，响应由
/// 浏览器 / 宿主接收，与我们这边是否在扫描无关。这里只 poll 一次把请求发出去，
/// 下一步开头再 await 它 —— 扫描、回到宿主的那段空档、落库，全都和下载重叠。
pub async fn prefetch(base_url: &str, start: u64, limit: u64) {
    let count = batch_blocks().min(limit);
    if start == 0 || count == 0 {
        return;
    }
    let mut future = request_future(base_url, start, count);
    let mut ready = None;
    std::future::poll_fn(|cx| {
        if let Poll::Ready(outcome) = future.as_mut().poll(cx) {
            ready = Some(outcome);
        }
        Poll::Ready(())
    })
    .await;
    // 已完成的 future 不能再 poll，结果单独收好。
    let future: RequestFuture = match ready {
        Some(outcome) => Box::pin(futures_util::future::ready(outcome)),
        None => future,
    };
    PREFETCH.with(|slot| {
        *slot.borrow_mut() = Some(Prefetch {
            url: base_url.to_owned(),
            start,
            count,
            future,
        });
    });
}

/// 一个请求。节点多给（早于 0.18.4.3 不认 `max_block_count`）就截掉，少给就照收。
async fn request_once(
    transport: &Transport,
    start: u64,
    count: u64,
) -> Result<Vec<ScannableBlock>, BlocksBinError> {
    let response = transport
        .post(
            "get_blocks.bin",
            build_request(start, count),
            Some(MAX_RESPONSE_BYTES),
        )
        .await
        .map_err(|e| BlocksBinError::Network(format!("{e:?}")))?;
    let mut blocks = parse_response(&response, start)?;
    if blocks.is_empty() {
        return Err(protocol(format!("zero blocks for start_height {start}")));
    }
    blocks.truncate(usize::try_from(count).unwrap_or(usize::MAX));
    Ok(blocks)
}

fn push_key(request: &mut Vec<u8>, key: &str) {
    request.push(u8::try_from(key.len()).expect("epee keys are short"));
    request.extend(key.as_bytes());
}

fn build_request(start_height: u64, max_block_count: u64) -> Vec<u8> {
    let mut request = Vec::with_capacity(64);
    request.extend(HEADER);
    request.push(VERSION);
    // 三个字段。epee 的 varint 低两位是长度标记，00 表示一字节。
    request.push(3 << 2);

    push_key(&mut request, "prune");
    request.push(Type::Bool as u8);
    request.push(1);

    push_key(&mut request, "start_height");
    request.push(Type::Uint64 as u8);
    request.extend(start_height.to_le_bytes());

    // 0.18.4.3 起才有；更老的节点忽略它，由 `fetch` 截断。
    push_key(&mut request, "max_block_count");
    request.push(Type::Uint64 as u8);
    request.extend(max_block_count.to_le_bytes());

    request
}

/// 一块：区块本身、剪枝后的非矿工交易、以及每笔交易（矿工交易在前）的输出索引。
struct RawBlock {
    block: Block,
    transactions: Vec<Transaction<Pruned>>,
}

fn read_block<'e>(entry: EpeeEntry<'e, '_, &'e [u8]>) -> Result<RawBlock, BlocksBinError> {
    let mut fields = entry.fields().map_err(epee_err)?;
    let mut block = None;
    let mut transactions = vec![];
    while let Some(field) = fields.next() {
        let (key, value) = field.map_err(epee_err)?;
        match key.consume() {
            b"block" => {
                let mut encoding = value.to_str().map_err(epee_err)?.consume();
                let parsed = Block::read(&mut encoding)
                    .map_err(|e| protocol(format!("invalid block: {e:?}")))?;
                if !encoding.is_empty() {
                    return Err(protocol("block had extraneous bytes after it"));
                }
                block = Some(parsed);
            }
            b"txs" => {
                let mut entries = value.iterate().map_err(epee_err)?;
                while let Some(tx_entry) = entries.next() {
                    let mut tx_fields = tx_entry.map_err(epee_err)?.fields().map_err(epee_err)?;
                    let mut transaction = None;
                    while let Some(tx_field) = tx_fields.next() {
                        let (tx_key, tx_value) = tx_field.map_err(epee_err)?;
                        // `prunable_hash` 被刻意忽略：见模块说明。
                        if tx_key.consume() == b"blob" {
                            let mut encoding = tx_value.to_str().map_err(epee_err)?.consume();
                            let parsed = Transaction::<Pruned>::read(&mut encoding)
                                .map_err(|e| protocol(format!("invalid transaction: {e:?}")))?;
                            if !encoding.is_empty() {
                                return Err(protocol("transaction had extraneous bytes after it"));
                            }
                            transaction = Some(parsed);
                        }
                    }
                    transactions.push(
                        transaction.ok_or_else(|| protocol("transaction entry without a blob"))?,
                    );
                }
            }
            _ => {}
        }
    }
    Ok(RawBlock {
        block: block.ok_or_else(|| protocol("block entry without the block"))?,
        transactions,
    })
}

/// `output_indices`：块 → 交易（矿工交易在前）→ 该交易每个输出的全局索引。
fn read_output_indices<'e>(
    entry: EpeeEntry<'e, '_, &'e [u8]>,
) -> Result<Vec<Vec<Vec<u64>>>, BlocksBinError> {
    let mut blocks = vec![];
    let mut block_entries = entry.iterate().map_err(epee_err)?;
    while let Some(block_entry) = block_entries.next() {
        let mut per_tx = vec![];
        let mut fields = block_entry.map_err(epee_err)?.fields().map_err(epee_err)?;
        while let Some(field) = fields.next() {
            let (key, value) = field.map_err(epee_err)?;
            if key.consume() != b"indices" {
                continue;
            }
            let mut tx_entries = value.iterate().map_err(epee_err)?;
            while let Some(tx_entry) = tx_entries.next() {
                let mut indices = vec![];
                let mut tx_fields = tx_entry.map_err(epee_err)?.fields().map_err(epee_err)?;
                while let Some(tx_field) = tx_fields.next() {
                    let (tx_key, tx_value) = tx_field.map_err(epee_err)?;
                    if tx_key.consume() != b"indices" {
                        continue;
                    }
                    let mut values = tx_value.iterate().map_err(epee_err)?;
                    while let Some(v) = values.next() {
                        indices.push(v.map_err(epee_err)?.to_u64().map_err(epee_err)?);
                    }
                }
                per_tx.push(indices);
            }
        }
        blocks.push(per_tx);
    }
    Ok(blocks)
}

pub(crate) fn parse_response(
    bytes: &[u8],
    start: u64,
) -> Result<Vec<ScannableBlock>, BlocksBinError> {
    let mut epee = Epee::new(bytes).map_err(epee_err)?;
    let mut root = epee.entry().map_err(epee_err)?.fields().map_err(epee_err)?;

    let mut raw_blocks = vec![];
    let mut output_indices = vec![];
    let mut status = None;
    while let Some(field) = root.next() {
        let (key, value) = field.map_err(epee_err)?;
        match key.consume() {
            b"blocks" => {
                let mut entries = value.iterate().map_err(epee_err)?;
                while let Some(entry) = entries.next() {
                    raw_blocks.push(read_block(entry.map_err(epee_err)?)?);
                }
            }
            b"output_indices" => output_indices = read_output_indices(value)?,
            b"status" => {
                status = Some(
                    String::from_utf8_lossy(value.to_str().map_err(epee_err)?.consume())
                        .into_owned(),
                );
            }
            _ => {}
        }
    }
    if let Some(status) = status.as_deref() {
        if status != "OK" {
            return Err(protocol(format!("node status {status}")));
        }
    }
    if raw_blocks.len() != output_indices.len() {
        return Err(protocol(format!(
            "{} blocks but {} output index entries",
            raw_blocks.len(),
            output_indices.len()
        )));
    }

    let mut result = Vec::with_capacity(raw_blocks.len());
    for (offset, (raw, indices)) in raw_blocks.into_iter().zip(output_indices).enumerate() {
        let expected = start + offset as u64;
        let height = raw.block.number() as u64;
        if height != expected {
            return Err(protocol(format!(
                "expected block {expected}, node sent {height}"
            )));
        }
        // 与区块头的交易哈希清单对数 —— 我们信任的正是那份清单。
        if raw.transactions.len() != raw.block.transactions.len() {
            return Err(protocol(format!(
                "block {height}: header lists {} transactions, node sent {}",
                raw.block.transactions.len(),
                raw.transactions.len()
            )));
        }
        if indices.len() != raw.transactions.len() + 1 {
            return Err(protocol(format!(
                "block {height}: {} transactions (plus miner) but {} output index lists",
                raw.transactions.len(),
                indices.len()
            )));
        }

        // 本块第一个 RingCT 输出的全局索引：第一笔非 V1 且有输出的交易的首个索引。
        let miner = raw.block.miner_transaction();
        let mut first_ringct = None;
        let shapes = std::iter::once((
            !matches!(miner, Transaction::V1 { .. }),
            miner.prefix().outputs.len(),
        ))
        .chain(raw.transactions.iter().map(|tx| {
            (
                !matches!(tx, Transaction::V1 { .. }),
                tx.prefix().outputs.len(),
            )
        }));
        for ((is_ringct, outputs), tx_indices) in shapes.zip(&indices) {
            if tx_indices.len() != outputs {
                return Err(protocol(format!(
                    "block {height}: a transaction has {outputs} outputs but {} indices",
                    tx_indices.len()
                )));
            }
            if is_ringct && outputs != 0 && first_ringct.is_none() {
                first_ringct = Some(tx_indices[0]);
            }
        }

        result.push(ScannableBlock {
            block: raw.block,
            transactions: raw.transactions,
            output_index_for_first_ringct_output: first_ringct,
        });
    }
    Ok(result)
}

#[cfg(test)]
mod tests {
    use super::*;

    /// 请求字节必须与 monero-oxide 发出的逐字一致：它是经过节点验证的格式。
    #[test]
    fn the_request_matches_the_upstream_layout() {
        let request = build_request(3_000_000, 50);
        assert_eq!(&request[..8], &HEADER);
        assert_eq!(request[8], VERSION);
        assert_eq!(request[9], 3 << 2);
        // header(8) + version(1) + count(1)
        // + prune: 1+5+1+1 + start_height: 1+12+1+8 + max_block_count: 1+15+1+8
        assert_eq!(request.len(), 10 + 8 + 22 + 25);
        assert!(request.windows(5).any(|w| w == b"prune"));
        assert!(request.ends_with(&50u64.to_le_bytes()));
    }

    /// 快就加倍到 1000 封顶，慢或失败就减半到 10 保底。
    #[test]
    fn the_batch_grows_on_fast_requests_and_shrinks_on_slow_or_failed_ones() {
        BATCH_BLOCKS.with(|b| b.set(INITIAL_BATCH_BLOCKS));
        for _ in 0..10 {
            record_success(1_000.0);
        }
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS);
        record_success(15_000.0);
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS, "中速不动");
        record_success(40_000.0);
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS / 2);
        for _ in 0..20 {
            record_failure();
        }
        assert_eq!(batch_blocks(), MIN_BATCH_BLOCKS);
        BATCH_BLOCKS.with(|b| b.set(INITIAL_BATCH_BLOCKS));
    }

    /// 预取只在完全吻合时被取走；不吻合的一律丢弃，槽位同时被清空。
    #[test]
    fn a_prefetch_is_taken_only_by_the_exact_same_request() {
        let park = |url: &str, start: u64, count: u64| {
            PREFETCH.with(|slot| {
                *slot.borrow_mut() = Some(Prefetch {
                    url: url.to_owned(),
                    start,
                    count,
                    future: Box::pin(futures_util::future::ready((Ok(vec![]), 0.0))),
                });
            });
        };
        park("https://a", 100, 20);
        assert!(take_prefetched("https://a", 100, 20).is_some());
        assert!(
            take_prefetched("https://a", 100, 20).is_none(),
            "取走即清空"
        );

        park("https://a", 100, 20);
        assert!(take_prefetched("https://b", 100, 20).is_none(), "换了节点");
        park("https://a", 100, 20);
        assert!(
            take_prefetched("https://a", 101, 20).is_none(),
            "起点变了（回退、部分前缀）"
        );
        park("https://a", 100, 20);
        assert!(
            take_prefetched("https://a", 100, 7).is_none(),
            "区间长度变了"
        );
    }

    #[test]
    fn genesis_is_refused_rather_than_silently_misread() {
        let Err(e) = futures_lite::future::block_on(fetch("https://x.invalid", 0, 10)) else {
            panic!("genesis must be refused");
        };
        assert!(matches!(e, BlocksBinError::Protocol(_)));
    }

    /// 真实节点的真实响应（xmr-node.cakewallet.com，2026-09-23，
    /// start_height=3768621、max_block_count=2），经桌面主进程抓取。
    /// 验证的是整条解析：区块、剪枝交易、输出索引、首个 RingCT 索引。
    #[test]
    fn parses_a_real_node_response() {
        let bytes = include_bytes!("../tests/fixtures/get_blocks_bin_3768621.bin");
        let blocks = parse_response(bytes, 3_768_621).expect("解析真实响应");
        eprintln!(
            "node returned {} blocks: {:?}",
            blocks.len(),
            blocks
                .iter()
                .map(|b| (
                    b.block.number(),
                    b.transactions.len(),
                    b.output_index_for_first_ringct_output
                ))
                .collect::<Vec<_>>()
        );
        assert!(!blocks.is_empty());
        for (i, b) in blocks.iter().enumerate() {
            assert_eq!(b.block.number() as u64, 3_768_621 + i as u64);
            assert_eq!(b.transactions.len(), b.block.transactions.len());
            // 每块至少有矿工交易的输出，所以一定有首个 RingCT 索引。
            assert!(b.output_index_for_first_ringct_output.is_some());
        }
        // 前驱哈希首尾相接：确认块真的是连续的一段。
        for pair in blocks.windows(2) {
            assert_eq!(pair[1].block.header.previous, pair[0].block.hash());
        }
        // RingCT 游标首尾相接：下一块的首个索引 = 这一块首个索引 + 这一块的输出总数。
        for pair in blocks.windows(2) {
            let outputs: u64 =
                std::iter::once(pair[0].block.miner_transaction().prefix().outputs.len())
                    .chain(
                        pair[0]
                            .transactions
                            .iter()
                            .map(|t| t.prefix().outputs.len()),
                    )
                    .map(|n| n as u64)
                    .sum();
            assert_eq!(
                pair[1].output_index_for_first_ringct_output,
                pair[0]
                    .output_index_for_first_ringct_output
                    .map(|i| i + outputs)
            );
        }
    }

    /// 扫描耗时基准（原生，release 下更准）。夹具太大不入库，按需抓：
    /// `XMR_BLOCKS_BIN=/path/to/get_blocks.bin XMR_BLOCKS_BIN_START=3767000 \
    ///  cargo test --release --lib scan_cost -- --nocapture`
    #[test]
    fn scan_cost_on_real_blocks() {
        let (Ok(path), Ok(start)) = (
            std::env::var("XMR_BLOCKS_BIN"),
            std::env::var("XMR_BLOCKS_BIN_START"),
        ) else {
            eprintln!("跳过：未设置 XMR_BLOCKS_BIN / XMR_BLOCKS_BIN_START");
            return;
        };
        let start: u64 = start.parse().unwrap();
        let bytes = std::fs::read(path).unwrap();
        let t0 = std::time::Instant::now();
        let blocks = parse_response(&bytes, start).expect("解析");
        let parse_ms = t0.elapsed().as_secs_f64() * 1000.0;

        let mut db = crate::wallet::testing::db();
        let (spend, view) = crate::wallet::testing::key_bytes();
        let id = db.add_identity("bench", &spend, &view, start).unwrap().id;
        let t1 = std::time::Instant::now();
        crate::scan::scan_blocks(&mut db, id, &blocks).expect("扫描");
        let scan_ms = t1.elapsed().as_secs_f64() * 1000.0;
        let txs: usize = blocks.iter().map(|b| b.transactions.len()).sum();
        eprintln!(
            "{} blocks / {txs} txs / {} bytes: parse {parse_ms:.0} ms, scan {scan_ms:.0} ms ({:.1} ms/block)",
            blocks.len(),
            bytes.len(),
            scan_ms / blocks.len() as f64
        );
    }

    #[test]
    fn garbage_is_a_protocol_error_not_a_panic() {
        let e = parse_response(b"not epee at all", 1).unwrap_err();
        assert!(matches!(e, BlocksBinError::Protocol(_)));
    }
}
