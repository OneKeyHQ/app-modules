//! In-memory compact block cache.
//!
//! `zcash_client_backend::sync::run` needs a `BlockCache`; upstream's `FsBlockDb`
//! needs a filesystem. Compact blocks are transient (download, scan, discard);
//! scan results are persisted by `zcash_client_sqlite`. Memory is bounded by the
//! `sync::run` batch size.

use std::collections::BTreeMap;
use std::ops::Range;
use std::sync::Mutex;

use zcash_client_backend::data_api::chain::{error, BlockSource};
use zcash_client_backend::data_api::scanning::ScanRange;
use zcash_client_backend::proto::compact_formats::CompactBlock;
use zcash_protocol::consensus::BlockHeight;

#[derive(Debug)]
pub struct MemoryBlockCache {
    blocks: Mutex<BTreeMap<u32, CompactBlock>>,
}

impl Default for MemoryBlockCache {
    fn default() -> Self {
        Self::new()
    }
}

impl MemoryBlockCache {
    pub fn new() -> Self {
        Self {
            blocks: Mutex::new(BTreeMap::new()),
        }
    }

    /// Number of cached blocks, for observing memory use.
    pub fn len(&self) -> usize {
        self.blocks.lock().map(|b| b.len()).unwrap_or(0)
    }

    pub fn is_empty(&self) -> bool {
        self.len() == 0
    }

    fn range_of(range: &ScanRange) -> Range<u32> {
        let r = range.block_range();
        u32::from(r.start)..u32::from(r.end)
    }
    /// Inserts blocks; an existing height is overwritten.
    pub fn insert(&self, compact_blocks: Vec<CompactBlock>) -> Result<(), CacheError> {
        let mut blocks = self.blocks.lock().map_err(|_| poisoned())?;
        for b in compact_blocks {
            let h = u32::try_from(b.height)
                .map_err(|_| CacheError(format!("block height out of range: {}", b.height)))?;
            blocks.insert(h, b);
        }
        Ok(())
    }

    /// Drops a scanned range to free memory.
    pub fn delete(&self, range: &ScanRange) -> Result<(), CacheError> {
        let mut blocks = self.blocks.lock().map_err(|_| poisoned())?;
        let r = Self::range_of(range);
        let doomed: Vec<u32> = blocks.range(r).map(|(h, _)| *h).collect();
        for h in doomed {
            blocks.remove(&h);
        }
        Ok(())
    }
}

/// Cache error. In memory, the only failure is a poisoned lock.
#[derive(Debug)]
pub struct CacheError(pub String);

impl std::fmt::Display for CacheError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "block cache: {}", self.0)
    }
}

impl std::error::Error for CacheError {}

fn poisoned() -> CacheError {
    CacheError("internal lock poisoned (a previous holder panicked)".into())
}

impl BlockSource for MemoryBlockCache {
    type Error = CacheError;

    fn with_blocks<F, WalletErrT>(
        &self,
        from_height: Option<BlockHeight>,
        limit: Option<usize>,
        mut with_block: F,
    ) -> Result<(), error::Error<WalletErrT, Self::Error>>
    where
        F: FnMut(CompactBlock) -> Result<(), error::Error<WalletErrT, Self::Error>>,
    {
        // Hand out only a contiguous prefix starting at from_height; short reads
        // are allowed. A gap would surface upstream as `BlockHeightDiscontinuity`,
        // which `is_continuity_error()` treats as a reorg, so a block missing on
        // the server would trigger a destructive rewind that hits the same gap again.
        //
        // Collect under the lock and release it before calling back: the callback
        // writes the wallet and upstream may read this cache again on that path.
        // `std::sync::Mutex` is not reentrant, so that would deadlock on wasm.
        let selected: Vec<CompactBlock> = {
            let blocks = self
                .blocks
                .lock()
                .map_err(|_| error::Error::BlockSource(poisoned()))?;

            let cap = limit.unwrap_or(usize::MAX);
            let start = match from_height.map(u32::from) {
                Some(h) => h,
                None => blocks.keys().next().copied().unwrap_or(0),
            };

            let mut out = Vec::new();
            let mut expect = start;
            while out.len() < cap {
                let Some(block) = blocks.get(&expect) else {
                    break;
                };
                out.push(block.clone());
                let Some(next) = expect.checked_add(1) else {
                    break;
                };
                expect = next;
            }

            // A missing start is an error, not an empty batch: upstream treats an
            // empty batch as success without advancing the queue, so the caller
            // would re-download the same range until it runs out of batches.
            if out.is_empty() && cap > 0 && !blocks.is_empty() {
                return Err(error::Error::BlockSource(CacheError(format!(
                    "cache is missing start block {start}; cannot scan from later blocks"
                ))));
            }
            out
        }; // lock released here

        for block in selected {
            with_block(block)?;
        }
        Ok(())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use zcash_client_backend::data_api::scanning::ScanPriority;

    fn block(height: u64) -> CompactBlock {
        CompactBlock {
            height,
            ..Default::default()
        }
    }

    fn range(start: u32, end: u32) -> ScanRange {
        ScanRange::from_parts(
            BlockHeight::from_u32(start)..BlockHeight::from_u32(end),
            ScanPriority::Historic,
        )
    }

    /// Heights actually handed out by `with_blocks`, the path scanning uses.
    fn handed_out(
        c: &MemoryBlockCache,
        from: u32,
        limit: usize,
    ) -> Result<Vec<u64>, error::Error<(), CacheError>> {
        let mut got = Vec::new();
        c.with_blocks::<_, ()>(Some(BlockHeight::from_u32(from)), Some(limit), |b| {
            got.push(b.height);
            Ok(())
        })?;
        Ok(got)
    }

    #[test]
    fn empty_cache_hands_out_nothing() {
        let c = MemoryBlockCache::new();
        assert!(c.is_empty());
        assert_eq!(handed_out(&c, 10, 5).unwrap(), Vec::<u64>::new());
    }

    #[test]
    fn insert_then_hand_out_contiguous() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(10), block(11), block(12)]).unwrap();
        assert_eq!(c.len(), 3);
        assert_eq!(handed_out(&c, 10, 3).unwrap(), vec![10, 11, 12]);
    }

    #[test]
    fn limit_is_respected() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(10), block(11), block(12)]).unwrap();
        assert_eq!(handed_out(&c, 10, 2).unwrap(), vec![10, 11]);
    }

    /// Blocks past a gap must not be handed out, or upstream sees a reorg and rewinds.
    #[test]
    fn hand_out_stops_at_gap() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(10), block(11), block(13)]).unwrap();
        assert_eq!(
            handed_out(&c, 10, 4).unwrap(),
            vec![10, 11],
            "must stop at missing height 12"
        );
    }

    /// A missing start must error; an empty batch would read as a successful scan.
    #[test]
    fn missing_start_is_an_error_not_an_empty_batch() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(11), block(12)]).unwrap();
        let e = handed_out(&c, 10, 2).unwrap_err();
        assert!(
            matches!(e, error::Error::BlockSource(CacheError(ref m)) if m.contains("10")),
            "missing start should be a BlockSource error, got {e:?}"
        );
    }

    #[test]
    fn delete_removes_only_the_range() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(10), block(11), block(12)]).unwrap();
        c.delete(&range(11, 12)).unwrap();
        assert_eq!(c.len(), 2);
        assert_eq!(handed_out(&c, 12, 1).unwrap(), vec![12]);
    }
}
