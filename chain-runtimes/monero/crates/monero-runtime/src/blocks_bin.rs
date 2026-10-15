//! Batch block fetch via `get_blocks.bin`, using wallet2's trust model.
//!
//! One request returns a whole batch of blocks with their transactions and the
//! output indices scanning needs, instead of 3-4 round trips per block.
//!
//! ## Why not monero-oxide's built-in path
//!
//! It also uses `get_blocks.bin` but recomputes transaction hashes from content and
//! checks them against the block header's hash list. Pruned transactions need the
//! node's `prunable_hash` for that, and released monerod (through v0.18.5.1) sends
//! it as all zeros (monero-project/monero#10120; fix #10652 is master-only), so it
//! always drops the batch and falls back to per-block JSON.
//!
//! ## Trust model (same as wallet2, Cake, Feather)
//!
//! The header's transaction hash list is trusted; content is not re-hashed against
//! it. Cost: a malicious node can serve fake transaction content under real hashes
//! and distort the displayed balance (inflate or hide an incoming payment). It
//! cannot steal funds: spending needs the private key, and the network rejects
//! spends of fake outputs. See `wallet2::process_new_blockchain_entry`, which uses
//! `b.tx_hashes[idx]`.
//!
//! Still checked: transaction count matches the header list, each block's output
//! index lists match its transaction outputs, and heights are contiguous from the
//! requested start. Cross-block continuity (previous hash, RingCT cursor) is
//! checked by `scan::scan_blocks`.
//!
//! Request format and parser skeleton follow monero-oxide's `monero-daemon-rpc`
//! (`src/bin_rpc/blocks_bin.rs`, `epee.rs`, MIT).

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

/// Batch-path failures, handled differently.
#[derive(Debug)]
pub enum BlocksBinError {
    /// Network failure: this step fails and the next retries. Not evidence the node lacks support.
    Network(String),
    /// Bad node response (unsupported, malformed, count mismatch): stop using the batch
    /// path for this node and fetch per block.
    Protocol(String),
}

fn protocol(detail: impl Into<String>) -> BlocksBinError {
    BlocksBinError::Protocol(detail.into())
}

fn epee_err(e: EpeeError) -> BlocksBinError {
    protocol(format!("epee: {e:?}"))
}

// ── Batch size: adaptive, capped as in wallet2 ─────────────────────────────
//
// wallet2 sends `getblocks.bin` uncapped; the node returns at most 1000 blocks /
// 20k transactions. That is fastest on a good link, but a multi-MB request may never
// finish on a tunneled or jittery one. So the size adapts: double when fast, up to
// 1000; halve when slow or failed, down to 10. Each step sends one request and scans
// and stores what it gets, so a slow later request never discards earlier blocks.

/// Per-request cap of wallet2 / monerod (`COMMAND_RPC_GET_BLOCKS_FAST_MAX_BLOCK_COUNT`).
pub const MAX_BATCH_BLOCKS: u64 = 1000;
/// Floor even on slow links; below it round-trip overhead dominates.
pub const MIN_BATCH_BLOCKS: u64 = 10;
/// Starting size on an unknown link: roughly what one request delivers within ten seconds.
pub const INITIAL_BATCH_BLOCKS: u64 = 100;
/// Double when faster than this.
const GROW_BELOW_MS: f64 = 8_000.0;
/// Halve when slower than this.
const SHRINK_ABOVE_MS: f64 = 25_000.0;

thread_local! {
    static BATCH_BLOCKS: Cell<u64> = const { Cell::new(INITIAL_BATCH_BLOCKS) };
}

/// Block count for the next request.
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

// ── Requests and pipelining ────────────────────────────────────────────────
//
// One request at a time, as in wallet2. Concurrent requests to a public node are
// served one after another (monerod holds the blockchain DB lock while building
// `get_blocks.bin`, and nodes may limit per-IP concurrency). The speedup comes from
// pipelining (`prefetch`): the next batch is in flight while this one is scanned.

/// A request's result and its duration from send to completion (from send time for a prefetch).
type RequestOutcome = (Result<Vec<ScannableBlock>, BlocksBinError>, f64);
type RequestFuture = Pin<Box<dyn Future<Output = RequestOutcome>>>;

/// The next request, already sent, whose result has not been taken.
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
        // The request is sent, and timing starts, on first poll.
        let started = now_ms();
        let transport = Transport::new(&url);
        let result = request_once(&transport, start, count).await;
        (result, now_ms() - started)
    })
}

/// Take the prefetched result only if it matches this span exactly. Otherwise (node
/// changed, rewind, range changed) drop it: refetching beats using the wrong data.
fn take_prefetched(url: &str, start: u64, count: u64) -> Option<RequestFuture> {
    PREFETCH.with(|slot| match slot.borrow_mut().take() {
        Some(p) if p.url == url && p.start == start && p.count == count => Some(p.future),
        _ => None,
    })
}

/// Fetched blocks, and whether they came from the previous step's prefetch.
pub struct Fetched {
    pub blocks: Vec<ScannableBlock>,
    pub prefetch_hit: bool,
}

/// Backoff (ms) before retrying a network failure; the length is the number of extra
/// retries. Each retry halves the request, since a smaller one often completes where a
/// large one cannot. Protocol errors are not retried.
const RETRY_BACKOFF_MS: [u32; 2] = [500, 1500];

async fn backoff(ms: u32) {
    #[cfg(target_arch = "wasm32")]
    gloo_timers::future::TimeoutFuture::new(ms).await;
    #[cfg(not(target_arch = "wasm32"))]
    tokio::time::sleep(std::time::Duration::from_millis(u64::from(ms))).await;
}

/// Fetch up to `limit` blocks from `start`, ordered by height. `start` must be
/// nonzero: monerod ignores `start_height` when it is zero.
///
/// Sends one request (retried smaller on failure) and accepts however many blocks
/// the node returns; it may return fewer due to the 20k-transaction cap, and the
/// next step requests the rest.
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

/// Send the request from `start` now, sized by the current batch (at most `limit`),
/// and keep the result for the next step.
///
/// As in `wallet2::refresh` ("pull the next set of blocks while we're processing
/// the current one"). wasm needs no thread: once sent, the browser / host receives
/// the response whether or not we are scanning. This polls once to send it and the
/// next step awaits it, so scanning, the return to the host and DB writes all
/// overlap the download.
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
    // A completed future must not be polled again; keep its result separately.
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

/// One request. Extra blocks (nodes before 0.18.4.3 ignore `max_block_count`) are
/// truncated; fewer are accepted.
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
    // Three fields. The low two bits of an epee varint mark its length; 00 means one byte.
    request.push(3 << 2);

    push_key(&mut request, "prune");
    request.push(Type::Bool as u8);
    request.push(1);

    push_key(&mut request, "start_height");
    request.push(Type::Uint64 as u8);
    request.extend(start_height.to_le_bytes());

    // Added in 0.18.4.3; older nodes ignore it and `fetch` truncates.
    push_key(&mut request, "max_block_count");
    request.push(Type::Uint64 as u8);
    request.extend(max_block_count.to_le_bytes());

    request
}

/// One block: the block and its pruned non-miner transactions.
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
                        // `prunable_hash` is deliberately ignored; see the module docs.
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

/// `output_indices`: block -> transaction (miner first) -> global index of each output.
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
        // Check against the header's transaction hash list, which is what we trust.
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

        // Global index of this block's first RingCT output: the first index of the first
        // non-V1 transaction with outputs.
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

    /// Request bytes must match monero-oxide's byte for byte; nodes are known to accept that layout.
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

    /// Doubles up to 1000 when fast; halves down to 10 when slow or failed.
    #[test]
    fn the_batch_grows_on_fast_requests_and_shrinks_on_slow_or_failed_ones() {
        BATCH_BLOCKS.with(|b| b.set(INITIAL_BATCH_BLOCKS));
        for _ in 0..10 {
            record_success(1_000.0);
        }
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS);
        record_success(15_000.0);
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS, "steady at medium speed");
        record_success(40_000.0);
        assert_eq!(batch_blocks(), MAX_BATCH_BLOCKS / 2);
        for _ in 0..20 {
            record_failure();
        }
        assert_eq!(batch_blocks(), MIN_BATCH_BLOCKS);
        BATCH_BLOCKS.with(|b| b.set(INITIAL_BATCH_BLOCKS));
    }

    /// A prefetch is taken only by an exact match; a mismatch discards it and clears the slot.
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
            "taking clears the slot"
        );

        park("https://a", 100, 20);
        assert!(take_prefetched("https://b", 100, 20).is_none(), "new node");
        park("https://a", 100, 20);
        assert!(
            take_prefetched("https://a", 101, 20).is_none(),
            "different start (rewind, partial prefix)"
        );
        park("https://a", 100, 20);
        assert!(
            take_prefetched("https://a", 100, 7).is_none(),
            "different count"
        );
    }

    #[test]
    fn genesis_is_refused_rather_than_silently_misread() {
        let Err(e) = futures_lite::future::block_on(fetch("https://x.invalid", 0, 10)) else {
            panic!("genesis must be refused");
        };
        assert!(matches!(e, BlocksBinError::Protocol(_)));
    }

    /// A real node response (xmr-node.cakewallet.com, 2026-09-23, start_height=3768621,
    /// max_block_count=2), captured via the desktop main process. Covers the full parse:
    /// blocks, pruned transactions, output indices, first RingCT index.
    #[test]
    fn parses_a_real_node_response() {
        let bytes = include_bytes!("../tests/fixtures/get_blocks_bin_3768621.bin");
        let blocks = parse_response(bytes, 3_768_621).expect("parse real response");
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
            // Every block has miner outputs, so a first RingCT index always exists.
            assert!(b.output_index_for_first_ringct_output.is_some());
        }
        // Previous hashes chain: the blocks really are contiguous.
        for pair in blocks.windows(2) {
            assert_eq!(pair[1].block.header.previous, pair[0].block.hash());
        }
        // RingCT cursor chains: next first index = this first index + this block's output count.
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

    /// Scan cost benchmark (native; release is more accurate). The fixture is too large
    /// to commit; capture it on demand:
    /// `XMR_BLOCKS_BIN=/path/to/get_blocks.bin XMR_BLOCKS_BIN_START=3767000 \
    ///  cargo test --release --lib scan_cost -- --nocapture`
    #[test]
    fn scan_cost_on_real_blocks() {
        let (Ok(path), Ok(start)) = (
            std::env::var("XMR_BLOCKS_BIN"),
            std::env::var("XMR_BLOCKS_BIN_START"),
        ) else {
            eprintln!("skipped: XMR_BLOCKS_BIN / XMR_BLOCKS_BIN_START not set");
            return;
        };
        let start: u64 = start.parse().unwrap();
        let bytes = std::fs::read(path).unwrap();
        let t0 = std::time::Instant::now();
        let blocks = parse_response(&bytes, start).expect("parse");
        let parse_ms = t0.elapsed().as_secs_f64() * 1000.0;

        let mut db = crate::wallet::testing::db();
        let (spend, view) = crate::wallet::testing::key_bytes();
        let id = db.add_identity("bench", &spend, &view, start).unwrap().id;
        let t1 = std::time::Instant::now();
        crate::scan::scan_blocks(&mut db, id, &blocks).expect("scan");
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
