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
    /// 写入一批区块。缓存是过程性数据，重复写入同一高度即覆盖。
    pub fn insert(&self, compact_blocks: Vec<CompactBlock>) -> Result<(), CacheError> {
        let mut blocks = self.blocks.lock().map_err(|_| poisoned())?;
        for b in compact_blocks {
            let h = u32::try_from(b.height)
                .map_err(|_| CacheError(format!("区块高度超出范围: {}", b.height)))?;
            blocks.insert(h, b);
        }
        Ok(())
    }

    /// 扫完即弃：删掉一段区间，把内存还回去。
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
        // 交出去的必须是**从 from_height 起的连续前缀**，允许短读。
        //
        // 这条契约不是形式主义：调用方拿 `prior` 的元数据去接续扫描，喂给它一段
        // 跳号的块会被上游判成 `BlockHeightDiscontinuity`，而那个错误 是
        // `is_continuity_error()` 的真值 —— 于是服务端漏块会被当成链重组，触发
        // 破坏性回退、重下、再撞同一个洞。宁可短读让下一轮重来。
        //
        // 先在持锁时收集，再释放锁，最后才执行回调：回调会写钱包库，而上游有
        // 可能在那条路径上再次读取本缓存。`std::sync::Mutex` 不可重入，同一线程
        // 二次加锁在 wasm 单线程环境下就是永久死锁。
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

            // 起点缺失时一块都不交，并且**报错而不是静默返回空**：静默空会让
            // 上游把空批当成功、队列不推进，调用方原地重下同一段直到批数耗尽。
            if out.is_empty() && cap > 0 && !blocks.is_empty() {
                return Err(error::Error::BlockSource(CacheError(format!(
                    "缓存缺少起点区块 {start}，不能从后面的块接着扫"
                ))));
            }
            out
        }; // 锁在这里释放

        // 逐块打点：把「卡在第几块的扫描里」和「所有块都扫完了、卡在之后的
        // 写库/承诺树里」分开。只在排查构建里编进来。
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

    /// 收集 `with_blocks` 实际交出来的高度 —— 这是扫描唯一走的那条路。
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

    /// 缺口之后的区块不能被交出去，否则上游会把跳号判成链重组并触发破坏性回退。
    #[test]
    fn hand_out_stops_at_gap() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(10), block(11), block(13)]).unwrap();
        assert_eq!(
            handed_out(&c, 10, 4).unwrap(),
            vec![10, 11],
            "高度 12 缺，就该停"
        );
    }

    /// 起点缺失要**报错**，不能静默返回空 —— 静默空会被当成扫描成功，
    /// 队列不推进，调用方原地重下同一段。
    #[test]
    fn missing_start_is_an_error_not_an_empty_batch() {
        let c = MemoryBlockCache::new();
        c.insert(vec![block(11), block(12)]).unwrap();
        let e = handed_out(&c, 10, 2).unwrap_err();
        assert!(
            matches!(e, error::Error::BlockSource(CacheError(ref m)) if m.contains("10")),
            "起点缺失应报 BlockSource 错误，实际 {e:?}"
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
