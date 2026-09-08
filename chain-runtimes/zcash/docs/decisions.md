# 设计决策与已排除的方案

记录「为什么这样」以及「哪些路走过发现不通」，避免以后重走。

---

## D1 — 存储层用 `zcash_client_sqlite`，不用 `zcash_client_memory`

**决定**：存储后端使用 crates.io 上未修改的 `zcash_client_sqlite`。

**依据**：

| | `zcash_client_memory` | `zcash_client_sqlite` |
|---|---|---|
| 发布到 crates.io | 从未 | 是，63 个正式版 |
| 所在仓库 | 2026-06-20 被切成独立仓库后停更 | 就在 librustzcash 里 |
| 近期活动 | 只有 dependabot | 与 `zcash_client_backend` 同日发版 |
| 生产使用 | 无 | 官方 Android/iOS SDK、zallet，135 个 Cargo.toml 依赖 |
| 我们的改动量 | vendor 11,122 行，27 个文件改了 22 个 | 1 行 Cargo.toml |

关键点：`zcash_client_sqlite` 与 `zcash_client_backend` **同日发版**
（2026-08-19 / 06-03 / 04-28 / 03-10 四次全对得上）。采用它不新增升级跑步机 ——
我们已经在这趟车上，只是把 storage 层的移植工作从自己这边转给 ECC。

**推论**：旧实现里为 memory 后端做的那些修复**应当丢弃，而非迁移**。原因是那些 bug
本就是我们手工打补丁打出来的；上游文档记载，它们正是拿 `zcash_client_sqlite`
当参考实现对 diff 找出来的。具体地：

- 输入锁 —— `zcash_client_sqlite` 有 `src/wallet/locking.rs`，1,319 行第一方实现，
  带 Sapling / Orchard 双池测试。我们手写的 192 行该丢。
- Ironwood 适配 —— sqlite 有十几个 ironwood migration 与完整迁池套件，第一方维护。
- 手动序列化 —— SQLite 自己按页管持久化，整块不需要。

---

## D2 — 浏览器存储用 IndexedDB VFS，不自己写 VFS（暂时）

**决定**：先用 `sqlite-wasm-vfs` 现成的 `relaxed_idb`，不自己实现 VFS。

**依据**：VFS 只有五个方法（read/write/truncate/flush/size），自己写是可行的，
但现成实现已经处理了同步/异步这个真正困难的部分。先跑通、看清真实读写模式，
再决定要不要换成自己的实现（届时只是替换那五个方法，不影响上层）。

**留给未来的口子**：需要加密、接 OneKey 自己的存储层、或移动端桥到原生存储时，
自己实现 VFS 是直接的路径。

---

## 已排除的方案

### ✗ 升 `rusqlite` 到 0.40 用 `ffi-sqlite-wasm-rs` feature

`zcash_client_sqlite 0.22` 依赖 `rusqlite ^0.37`。强升 0.40 产生 **83 个编译错误**
（`u64: FromSql` 等 API 破坏），且 `schemerz-rusqlite` 最高只有 `0.370.0`，
没有 0.40 对应版本 —— 要连带 fork 第二个 crate。

### ✗ 换用 turso（纯 Rust 的 SQLite 实现）

可行但不必要。turso 没有 rusqlite 兼容层，而 `zcash_client_sqlite` 有约 1,240 处
rusqlite 调用（`execute` 335、`query_row` 205、`prepare` 139+84、params 宏 751 等），
需要写一整层 shim；且它仍是 `0.8.0-pre`，未到 1.0。

### ✗ 自己把 SQLite 的 C 源码编到 wasm

不需要。`sqlite-wasm-rs` 已提供预编译产物。自行编译还要处理 wasm sysroot
（`stdio.h` 缺失），得不偿失。expo-sqlite 的做法也印证了这一点 —— 它给 Web 端
直接 vendor 了一个编好的 624 KB wasm，不在用户机器上编 C。

### ✗ 靠 wasm 符号差分判断某个 Rust 修复是否包含在内

实测无效：`get_legacy_transparent_address`、`get_transparent_address_metadata`、
`AddressNotRecognized`、`transparent_receivers`、`ironwood` 这些探针在不同构建里
出现次数完全一致，区分不出来。判断版本要靠版本号与字节数，不要靠符号。

---

## 构建环境的坑

**Apple clang 不支持 wasm32**，会报
`No available targets are compatible with triple wasm32-unknown-unknown`。
必须用 Homebrew LLVM（`/opt/homebrew/opt/llvm/bin/clang`）。
`secp256k1-sys` 自带 wasm sysroot，只是需要一个能产 wasm 的 clang。
`scripts/build-wasm.sh` 会自动探测并给出可读的报错。

**`getrandom` 0.4 在 wasm32-unknown-unknown 上必须显式选后端**，
否则编译期直接失败。已在 `.cargo/config.toml` 里配好
`--cfg getrandom_backend="wasm_js"`。

**`rand` 版本必须跟 `zcash_client_sqlite` 对齐到 0.8 / `rand_chacha` 0.3**
（即 `rand_core` 0.6 那条线）。用 0.9 会因为 `RngCore` trait 不是同一个而
无法满足 `init_wallet_db` 的泛型约束，报错信息不直观。

**headless Chrome 的 `--virtual-time-budget` 配 `--dump-dom` 会卡死**
（虚拟时间与 IndexedDB 的异步 IO 冲突），且 Chrome 拿到结果后不会自行退出。
`scripts/browser-check.py` 的做法是让页面把结果 fetch 回传给本地 server，
轮询到结果就主动结束进程。

---

## 运行期发现（2026-08-22 实测）

### lightwalletd 端点必须支持 gRPC-web + CORS

浏览器里跑的是 gRPC-web，不是原生 gRPC，所以端点必须：

1. 接受 `content-type: application/grpc-web+proto`
2. 对 OPTIONS 预检返回 CORS 头（`access-control-allow-{origin,methods,headers}`）

实测：

| 端点 | gRPC-web + CORS |
|---|---|
| `zcash-mainnet.chainsafe.dev` | ✅ |
| `zcash-testnet.chainsafe.dev` | ✅ |
| `testnet.zec.rocks` | ❌ OPTIONS 返回 404 |
| `mainnet.zec.rocks` | ❌ 连不上 |

不支持时浏览器侧的报错是 `TypeError: Failed to fetch` —— 这个信息**不会**告诉你是 CORS 问题，
排查时先用 curl 打一次 OPTIONS 预检确认。

因此本 runtime **不硬编码任何端点**，由宿主通过 `setLightwalletdUrl()` 注入；
换端点时必须先验证 gRPC-web 支持。

### 首次同步不是一次快调用

`syncAdvance` 第一次调用要先 `update_subtree_roots`（下载全部子树根），实测在 testnet 上
超过 15 分钟仍未返回。这不是错误路径，但有两个直接后果：

1. **宿主必须给进度反馈**，不能把它当成一个普通的 await。
2. `batch_size` 控制的是单批扫描量，**控制不了前置的 subtree roots 下载**。

如果这个耗时不可接受，方向是让 `sync::run` 的前置步骤可分段、可中断 —— 那需要不走
`sync::run` 而自己驱动 `scan_cached_blocks`，属于后续工作，不要在没有实测数据前就动手。

---

## D3 — 移动端原生：分叉面有多大（实测）

担心「维护多套代码」是合理的，所以先量。当前 `crates/zcash-runtime/src` 的构成：

| | 行数 | 说明 |
|---|---|---|
| **全平台共享** | **662** | `wallet` / `account` / `storage` / `blockcache` / `clock` / `error`，wasm32 与 aarch64-apple-ios 均已编译通过 |
| wasm 专属 | 361 | `bindgen` 177、`runtime` 85、`network` 61、`sync` 38 |

wasm 专属那 361 行里，真正**必须写第二份**的只有绑定层：

- `bindgen.rs`（177 行）—— wasm-bindgen 对 JS，移动端要 JNI / Swift 的等价物。
- `network.rs`（61 行）—— 只是把 gRPC-web transport 换成 tonic 原生 transport，
  逻辑与 `LightClient` 签名都不变，是一个 cfg 分支。
- `sync.rs` / `runtime.rs` —— 现在因为依赖 `network` 才一起 cfg，
  本身没有平台特异性，接上原生 transport 后可直接共享。

**结论：第二份大约 180–250 行，不是「多套代码」。**

可以进一步降低：**UniFFI**（0.32.0，1100 万下载，活跃）能从一份 Rust 定义同时生成
Kotlin 与 Swift，把「JNI + Swift 两套」压成一套。注意 `equilibriumco/uniffi-zcash-lib`
（把 librustzcash 做 UniFFI 封装的现成项目）最后更新在 2024-08，**不要直接依赖**，
但它证明这条路可行。

### 但有三个真实成本，不要低估

1. **构建矩阵变大**：wasm-pack 之外多出 4 个移动架构组合（arm64/x86_64 × iOS/Android），
   CI 时长与产物管理都变复杂。
2. **调试变难**：移动端原生出问题，没有浏览器 devtools 可用。
3. **发布节奏耦合**：Rust 改一次，移动端就要重新出包、过应用商店。

第 3 条最实在，也直接决定了边界该怎么划：**放进 Rust 的逻辑越少，移动端被迫发版的频率越低。**
这正是「调度、birthday、进度、重试放 TS」这条原则的现实价值 —— 它不只是开发体验问题。

### 建议：分阶段，现在不要做

先把 Web / Desktop 这条线跑通并稳定，移动端维持 keys-only（与现状一致，不构成倒退）。
等 Rust 侧的接口经过实战、改动频率降下来，再上移动端原生 —— 那时候第二套绑定的
维护成本才是可预测的，而不是在接口还在变的时候就把它复制一份。

---

## D4 — 不拆第三个包（实测后的结论）

考虑过把 PCZT 的构造与证明拆成独立包，让日常「扫链 + 看余额」不背证明电路的体积。

**实测代价**：去掉全部 PCZT 相关代码后 wasm 从 6.73 MB 降到 **4.70 MB**，
即证明电路（`orchard/circuit` → halo2）约值 **1.6–2.0 MB**。

**但拆不了，原因是数据库**：

构造 PCZT 需要钱包数据库（选币、witness、anchor），所以「构造 + 证明」包必须
自带完整的存储栈。那就意味着两个 wasm 模块各自持有一个 SQLite 实例，
同时读写同一份 IndexedDB —— 而我们用的 `relaxed-idb` VFS 明确标注
**Multiple connections: ❌**。并发写同一个库会损坏数据。

省 1.6 MB 不值得换来数据损坏风险。

**替代的省体积方向**（未做，按性价比排序）：

1. `wasm-opt -Oz`（wasm-pack 默认只跑 `-O`）
2. 证明改为可选加载：把 `pcztProve` 单独编成一个**不含数据库**的模块 ——
   它只吃 PCZT 字节、吐 PCZT 字节，不碰存储，因此没有多连接问题。
   这条路可行，是真要省体积时的正解。
3. 检查是否有未使用的 feature 还在拉代码

注意方案 2 与被否掉的方案不同：分界线在**是否需要数据库**，
不在「是不是 PCZT 相关」。`pcztCreate` 必须和钱包同包，`pcztProve` 不必。

---

## D5 — 不用 `sync::run`，自己驱动有界扫描

**症状**：`syncAdvance(batchSize)` 在 testnet 上一次调用 900 秒未返回。

**根因**：`zcash_client_backend::sync::run` 内部是

```rust
update_subtree_roots(client, db_data).await?;
while running(client, params, db_cache, db_data, batch_size).await? {}
```

`batch_size` 只控制**单批区块数**，不控制批数 —— 这个函数的语义是「同步到底」，
不是「推进一步」。调用方无法中断、无法看进度、无法在中途让出主线程。

我们原先的接口文档写着「有界推进」，实现却是跑到尾。**是接口契约与实现不符，
不是性能问题。**

**修法**：自己驱动上游的公开 API，拆成两段：

- `syncPrepare()` —— 一次性准备：下载三个池的子树根 + 更新链尖。
  这是首次同步最慢的一步，且与 batchSize 无关。单独暴露，宿主才能给出
  「正在准备」的独立进度，而不是干等一个不返回的调用。
- `syncStep(batchSize, maxBatches, priority, activeUfvksJson)` —— 最多扫
  `maxBatches` 批就返回，且只为宿主显式传入的 UFVK 试解密；
  空集合报 `NO_ACTIVE_ACCOUNTS`，绝不回退到数据库中全部账户。
  带 `{ done, batchesDone, blocksScanned, remainingBlocks, remainingRanges }`。
  **永远不自己循环到完成。**
- `queueRescanFrom(fromHeight)` —— 非破坏性地把
  `[fromHeight, chainTip]` 重新排为 historic 扫描。宿主在恢复曾暂停的 UFVK 时调用，
  因为上游 scan queue 是 network wallet 全局的，其他 active UFVK 可能已经把它推进到链尖。

Sapling 仍保留上游 schema、承诺树和 subtree root 兼容逻辑，但不会进入 ownership
scanning keys。Runtime 用官方 ZIP-316 API 临时构造只含 Orchard component 的 UFVK，
再交给官方 scanner；没有 fork 或修改 `zcash_client_backend`。

上游的 `download_subtree_roots` / `download_blocks` / `download_chain_state` /
`scan_blocks` 全是私有函数，所以这四段逻辑在 `sync.rs` 里重新实现了一遍 ——
但用的都是公开 API（`get_subtree_roots` / `get_block_range` / `get_tree_state` /
`scan_cached_blocks` / `put_*_subtree_roots` / `suggest_scan_ranges`），
没有复制任何协议逻辑。

**教训**：上游函数名叫 `run` 而不是 `step`，语义在文档里也写了。
接口契约要以**实测行为**为准，不能以我们希望它是什么为准 ——
这个 bug 在写文档时就该被发现，而不是等到 900 秒超时。

---

## D6 — 所有网络调用必须有超时

**为什么是硬性要求，不是「最好有」**：

wasm 里挂死的调用**宿主无法取消** —— 没有线程可以中断，Promise 也不会自己 reject。
一个不返回的 gRPC 调用会让整个钱包永久卡住，而且不产生任何错误信息，
连日志都没有。用户看到的是「转圈永远不停」。

实测踩到过：`syncStep` 只需要扫 201 个区块（`syncPeek` 0ms 就返回了区间
`{start: 4292265, end: 4292466, priority: ChainTip}`），却几分钟不返回。
区间这么小说明不是计算量问题，是某个网络调用挂住了。

**做法**：`network::with_timeout(fut, ms, operation)` 用 `gloo-timers` 的
`TimeoutFuture` 与目标 future 竞速，超时抛
`NETWORK_ERROR` 且 `params = { operation, timeoutMs }`。
`operation` 是稳定标识，宿主和排查者都能一眼看出是哪一步。

默认 30 秒（`DEFAULT_TIMEOUT_MS`）—— 单次 gRPC 调用超过这个时长基本可判定为挂死。

**顺带的价值**：超时把「挂死」变成「可定位的错误」。上面那个卡住的问题，
加上超时后一次运行就能指出是 `getBlockRange` 还是 `getTreeState`，
不用再靠猜和二分。

## 排查这类问题的手段：`syncPeek()`

`syncPeek()` 只读钱包建议的扫描区间，**不发任何网络请求**，返回
`{ ranges: [{ start, end, len, priority }], totalBlocks }`。

它同时是宿主的进度条数据源，和「同步卡住时先确认它想扫哪一段」的诊断入口 ——
先用它排除「区间异常巨大」这种可能，再去查网络层，比直接猜快得多。

---

## D7 — 「10 个区块能扫、25 个挂死」的排查记录

值得完整记下来，因为过程里我判断错了三次，每次都靠实测纠正。

### 症状

`syncStep(10, 1)` 591ms 正常返回；`syncStep(25, 1)` 永久不返回，
**页面完全无响应，没有任何错误信息，连 30 秒超时都不触发**。

超时不触发这一点本身就是关键线索：说明阻塞发生在**同步代码**里，
JS 事件循环被占住，定时器根本没机会开火。如果是 await 的网络调用挂住，超时会正常生效。

### 三次错误判断

1. **「卡在下载子树根」** —— 错。`syncPrepare` 实测 1.3–1.4 秒
   （testnet 6 sapling / 3 orchard / 0 ironwood 子树）。
2. **「treestate 高度低于 Sapling 激活导致 gRPC 挂起」**（我们自己代码库记过这个坑）
   —— 也错，因为 birthday 在链尖附近。**但这个钳位保留了**，它是真实陷阱。
3. **「批量大就是慢」** —— 错。10 通过、25 挂死这种悬崖不是量级问题。

### 定位手段

- `syncPeek()` —— 只读待扫区间、不发网络。**0ms** 返回
  `{start, end, len: 201, priority: "ChainTip"}`，一下排除「区间异常巨大」。
- `syncProbeDownload(n)` —— 只下载不扫描。**100 个区块下载仅 303ms**，
  网络这一半彻底洗清，问题锁定在 `scan_cached_blocks`。
- 干净库跑 25 同样挂 —— 排除「累积状态」。

### 真正的原因：我自己写的缓存里有可重入死锁

`MemoryBlockCache::with_blocks` 原本在**整个遍历期间持有 `Mutex`**，
并在持锁状态下调用 `with_block` 回调 —— 而那个回调会写钱包库，
上游有可能在那条路径上再次读取本缓存。

`std::sync::Mutex` 不可重入。wasm 是单线程，同一线程二次加锁 = **永久死锁**，
表现正是「页面卡死、无错误、定时器不响」。批量小时不触发那条二次读取路径，
所以 10 能过、25 挂死。

**修法**：持锁时只把区块 `clone` 收集出来，**释放锁之后**才执行回调。

### 教训

- 在持锁状态下调用外部回调，等于把锁的作用域交给了不受控的代码。
  单线程环境下这不是「可能有竞争」，是「一定死锁」。
- 「小批量能跑、大批量挂死」不要当成性能问题处理。真的性能问题是渐变的；
  陡峭悬崖几乎总是状态机或锁的问题。
- 超时不触发是强信号，说明阻塞在同步代码里 —— 这个信息比超时本身更有价值。

---

## D7 补充 — 原生复现证明问题在 wasm 侧，不在扫描逻辑

浏览器里 `batchSize ≥ 25` 挂死。为了排除「上游扫描逻辑本身有问题」，
给网络层补了**原生 transport**（tonic + TLS），在本机跑完全相同的流程：

```
batch=25   耗时 513ms   blocksScanned=25   remainingBlocks=176
batch=100  耗时 517ms   blocksScanned=100  remainingBlocks=101
```

**原生环境完全正常，而且 25 与 100 耗时几乎相同**（513 vs 517ms）——
说明扫描本身根本不是瓶颈，耗时由固定开销主导。

结论：**症结在 wasm 侧**。

> ⚠️ 当时据此推断「嫌疑在 `relaxed-idb` 存储层」，**这个推断是错的** ——
> 换成内存 VFS 照样卡。而且这一节说「上游 `scan_cached_blocks` 没有问题」
> 也不准确：它在 wasm 上确实有问题，只是原生跑不出来。
> 真正的根因见 D8。这一节保留，是因为「原生对照」这个方法本身是对的，
> 只是当时的对照实验没控住变量（两端连的服务器不同、覆盖的高度区间也不同）。

### 顺带的收获

这次为复现而加的原生 transport **本身就是移动端要用的东西**
（见 D3 关于移动端原生 runtime 的讨论）。现在 `network` 与 `sync` 模块
已是全平台的，只有 `runtime`（thread_local 状态）仍限 wasm。

`crates/zcash-runtime/tests/scan_repro.rs` 保留为可复现的回归用例，
默认 `#[ignore]`（需要联网）。跑法：

```bash
cargo test -p onekey-zcash-runtime --test scan_repro -- --ignored --nocapture
```

注意端点不通用：浏览器要 gRPC-web + CORS（`zcash-*.chainsafe.dev`），
原生要真 gRPC（`testnet.zec.rocks:443`）。两边端点互不相通，
配错的表现都是「连不上」但原因完全不同。

---

## D8 — 扫描不走上游 `scan_cached_blocks`：wasm 上它会永久堵死主线程

### 症状

浏览器里扫描随机卡死：页面无响应、无报错、超时定时器不触发，连 Chrome
DevTools 协议都服务不了（`Profiler.enable` 超时）。原生跑同一段完全正常。

### 根因

`zcash_client_backend` 把试解密交给批量 runner，而 `Tasks` trait 的**默认**
`run_task` 是：

```rust
fn run_task(&self, item: Item) {
    let task = self.add_task(item);
    rayon::spawn_fifo(|| task.run());
}
```

`scan_cached_blocks` 用的 `Tasks for ()` 只覆写了 `add_task`，**没有覆写
`run_task`**，所以仍然走 rayon。wasm32-unknown-unknown 是单线程、无 atomics，
rayon 全局线程池没有任何工作线程 —— 任务被排进队列后永远不会执行。

随后第二遍扫描调 `BatchReceiver::into_results()`，它的文档写得很直白：
「Blocks until the results of the batch are ready」。结果永远不来，
主线程就永久阻塞在通道上。

### 为什么表现得像「批量大到某个阈值就挂」

空区块不产生批次。测试用的 testnet 区间里绝大多数是空块（93 字节 / 0 交易），
只有 4292845 一个区块有内容（6 sapling spend / 2 output / 2 ironwood action）。
批量越大越容易把它圈进去，于是看起来像「批量超过某个值就挂」——
实际与批量毫无关系，只与**这一批里有没有含屏蔽输出的区块**有关。

### 解法

用公开 API `scanning::scan_block` 自己写扫描循环（`sync::scan_blocks_inline`）。
`scan_block` 内部等价于 `scan_block_with_runners(..., runners = None)`，
试解密就地完成，不碰 rayon。

- **不需要 fork 上游**：`scan_block` / `ScanningKeys::from_account_ufvks` /
  `Nullifiers::unspent` / `ScannedBlock::to_block_metadata` 全是公开项。
- 代价：失去批量试解密的摊销优化（批量求逆）。原生实测扫描阶段耗时：
  1000 块 45ms → 88ms，约 1.4–2 倍。这段 testnet 区块很空，
  主网稠密区块上的差距会更大，届时可重新评估。
- 备选方案（未采用）：vendor `zcash_client_backend` 并把 `run_task` 在 wasm 上
  改成就地执行 —— 能保住批量优化，但要多 fork 一个大 crate。
  rayon 在整个依赖图里只有 `zcash_client_backend` 一个使用者，
  所以「patch rayon 成单线程 shim」也可行，同样因为增加 fork 面而未选。

这是上游在单线程目标上的真实缺陷，值得反馈给 librustzcash。

### 实测（修复后，持久化 IndexedDB VFS，testnet）

| batchSize | 总耗时 | 下载 | treestate | 扫描 |
|---|---|---|---|---|
| 10 | 853ms | 523 | 255 | 70 |
| 25 | 533ms | 258 | 257 | 17 |
| 60 | 549ms | 279 | 257 | 12 |
| 100 | 567ms | 287 | 255 | 24 |
| 200 | 633ms | 351 | 254 | 26 |
| 500 | 614ms | 333 | 255 | 25（203 块，追平） |

扫描本身很便宜，耗时基本全在两次网络往返上。宿主要提速应该减少往返次数，
而不是调批量。

### 排查过程里更值得记住的教训

**测量工具本身骗了我好几轮。** 页面用 `fetch` 往测试服务器回传日志，
于是「被测代码卡住」和「回传通道自己死了」在日志上完全一样 —— 都是
「日志停在某一行」。据此得出过两个错误结论：

1. 「batchSize ≥ 25 永久挂死」—— 写进了 README 当作阻塞发版的问题，实际是假的；
2. 「区块 4292846 有毒」—— 停止位置其实是被 tracing 自身拖慢造成的假象。

修法是把证据通道和被测对象彻底解耦：`scripts/cdp_console.py` 直接从
Chrome DevTools 协议读 console，不经过页面网络。换上它之后，
第一轮就拿到了真实的停止位置。

顺带修掉的两个工具缺陷：

- `browser-check.py` 原来用串行 `TCPServer`，页面同时发上报和拉资源时会互相排队；
  已换成 `ThreadingHTTPServer`。
- 判定「通过」只看「行数够且没有 FAIL」，页面卡在最后一步时照样报通过 ——
  真发生过。现在必须收到页面自己发的终止标记。

**另一条：对照实验要控住所有变量。** 早期「原生正常」的结论其实无效 ——
原生连的是 `testnet.zec.rocks`、浏览器连的是 `zcash-testnet.chainsafe.dev`，
服务器都不是同一个；而且原生跑的高度区间根本没覆盖到出问题的区块。
后来加了 `network::block_summary`（返回编码长度 + 校验和），
确认两端拿到的字节完全一致，对照才真正成立。
