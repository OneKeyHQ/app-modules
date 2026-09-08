//! 内存版 compact block 缓存。
//!
//! `zcash_client_backend::sync::run` 需要一个 `BlockCache`，官方在
//! `zcash_client_sqlite` 里提供的是文件系统版（`FsBlockDb`），浏览器用不了。
//!
//! 这里用内存实现，理由是 compact block 是**过程性数据**：下载 → 扫描 → 用完即弃。
//! 真正需要持久化的是扫描结果（note、witness、承诺树），那些由 `zcash_client_sqlite`
//! 写进 SQLite，已经落在 IndexedDB 上。
//!
//! 内存占用受 `sync::run` 的 `batch_size` 约束：一次只保留正在扫的那一批。

use std::collections::BTreeMap;
use std::ops::Range;
use std::sync::Mutex;

use zcash_client_backend::data_api::chain::{error, BlockCache, BlockSource};
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

    /// 当前缓存的区块数量，供宿主观测内存占用。
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
}

/// 缓存自身的错误。内存实现里唯一可能的失败是锁中毒。
#[derive(Debug)]
pub struct CacheError(pub String);

impl std::fmt::Display for CacheError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "block cache: {}", self.0)
    }
}

impl std::error::Error for CacheError {}

fn poisoned() -> CacheError {
    CacheError("内部锁中毒（上一次持锁时发生了 panic）".into())
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
        // 先在持锁时把需要的区块**收集出来**，再释放锁，最后才执行回调。
        //
        // 不能在持锁期间调 `with_block` —— 回调会写钱包库，而上游有可能在那条
        // 路径上再次读取本缓存。`std::sync::Mutex` 不可重入，同一线程二次加锁
        // 在 wasm 单线程环境下就是永久死锁。
        let selected: Vec<CompactBlock> = {
            let blocks = self
                .blocks
                .lock()
                .map_err(|_| error::Error::BlockSource(poisoned()))?;

            let start = from_height.map(u32::from).unwrap_or(0);
            let iter = blocks.range(start..).map(|(_, b)| b.clone());
            match limit {
                Some(n) => iter.take(n).collect(),
                None => iter.collect(),
            }
        }; // 锁在这里释放

        // 逐块打点。上游 `scan_cached_blocks` 是黑盒，而它唯一会回调进来的地方
        // 就是这里 —— 于是「卡在第几块的扫描里」和「所有块都扫完了、卡在之后的
        // 写库/承诺树里」可以被分开。只在排查构建里编进来。
        #[cfg(feature = "debug-tracing")]
        let total = selected.len();
        // The index is consumed only by the debug-tracing build.
        #[allow(clippy::unused_enumerate_index)]
        for (_i, block) in selected.into_iter().enumerate() {
            #[cfg(feature = "debug-tracing")]
            crate::sync::trace(&format!(
                "cache: 交出第 {}/{} 块 h={}",
                _i + 1,
                total,
                block.height
            ));
            with_block(block)?;
        }
        #[cfg(feature = "debug-tracing")]
        crate::sync::trace("cache: 本批区块已全部交出，之后的时间都在上游写库/承诺树里");
        Ok(())
    }
}

#[async_trait::async_trait]
impl BlockCache for MemoryBlockCache {
    fn get_tip_height(
        &self,
        range: Option<&ScanRange>,
    ) -> Result<Option<BlockHeight>, Self::Error> {
        let blocks = self.blocks.lock().map_err(|_| poisoned())?;
        let tip = match range {
            None => blocks.keys().next_back().copied(),
            Some(range) => {
                let r = Self::range_of(range);
                blocks.range(r).map(|(h, _)| *h).next_back()
            }
        };
        Ok(tip.map(BlockHeight::from_u32))
    }

    async fn read(&self, range: &ScanRange) -> Result<Vec<CompactBlock>, Self::Error> {
        let blocks = self.blocks.lock().map_err(|_| poisoned())?;
        let r = Self::range_of(range);
        // 契约要求返回的区块必须连续且从 range.start 开始；允许短读。
        let mut out = Vec::new();
        let mut expect = r.start;
        while expect < r.end {
            match blocks.get(&expect) {
                Some(b) => {
                    out.push(b.clone());
                    expect += 1;
                }
                None => break,
            }
        }
        Ok(out)
    }

    async fn insert(&self, compact_blocks: Vec<CompactBlock>) -> Result<(), Self::Error> {
        let mut blocks = self.blocks.lock().map_err(|_| poisoned())?;
        for b in compact_blocks {
            let h = u32::try_from(b.height)
                .map_err(|_| CacheError(format!("区块高度超出范围: {}", b.height)))?;
            blocks.insert(h, b);
        }
        Ok(())
    }

    async fn delete(&self, range: ScanRange) -> Result<(), Self::Error> {
        let mut blocks = self.blocks.lock().map_err(|_| poisoned())?;
        let r = Self::range_of(&range);
        let doomed: Vec<u32> = blocks.range(r).map(|(h, _)| *h).collect();
        for h in doomed {
            blocks.remove(&h);
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

    #[test]
    fn empty_cache_has_no_tip() {
        let c = MemoryBlockCache::new();
        assert!(c.is_empty());
        assert_eq!(c.get_tip_height(None).unwrap(), None);
    }

    #[test]
    fn insert_then_read_contiguous() {
        let c = MemoryBlockCache::new();
        futures::executor::block_on(c.insert(vec![block(10), block(11), block(12)])).unwrap();
        assert_eq!(c.len(), 3);
        assert_eq!(
            c.get_tip_height(None).unwrap(),
            Some(BlockHeight::from_u32(12))
        );

        let got = futures::executor::block_on(c.read(&range(10, 13))).unwrap();
        assert_eq!(got.len(), 3);
        assert_eq!(got[0].height, 10);
    }

    /// 契约要求：返回的区块必须连续且从 range.start 开始，允许短读。
    /// 缺口之后的区块不能被返回，否则调用方会把不连续的数据当成连续的。
    #[test]
    fn read_stops_at_gap() {
        let c = MemoryBlockCache::new();
        futures::executor::block_on(c.insert(vec![block(10), block(11), block(13)])).unwrap();
        let got = futures::executor::block_on(c.read(&range(10, 14))).unwrap();
        assert_eq!(got.len(), 2, "遇到高度 12 的缺口就该停");
        assert_eq!(got[1].height, 11);
    }

    #[test]
    fn read_returns_empty_when_start_missing() {
        let c = MemoryBlockCache::new();
        futures::executor::block_on(c.insert(vec![block(11)])).unwrap();
        let got = futures::executor::block_on(c.read(&range(10, 12))).unwrap();
        assert!(got.is_empty(), "起点缺失时不能从中间开始返回");
    }

    #[test]
    fn delete_removes_only_the_range() {
        let c = MemoryBlockCache::new();
        futures::executor::block_on(c.insert(vec![block(10), block(11), block(12)])).unwrap();
        futures::executor::block_on(c.delete(range(11, 12))).unwrap();
        assert_eq!(c.len(), 2);
        assert_eq!(
            c.get_tip_height(None).unwrap(),
            Some(BlockHeight::from_u32(12))
        );
    }

    #[test]
    fn tip_within_range_is_bounded() {
        let c = MemoryBlockCache::new();
        futures::executor::block_on(c.insert(vec![block(10), block(20), block(30)])).unwrap();
        let tip = c.get_tip_height(Some(&range(10, 25))).unwrap();
        assert_eq!(tip, Some(BlockHeight::from_u32(20)));
    }
}
