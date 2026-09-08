> This project now lives in `app-modules/chain-runtimes/zcash`. See
> [IMPORT.md](./IMPORT.md) for the source revision and preserved vendor patches.
> Build commands below run from this directory. Use Rust 1.93.0 (pinned in
> `rust-toolchain.toml`), Python 3.9+, LLVM/Clang, and wasm-pack 0.15.0.
> Install `wasm-bindgen-cli` at the `wasm-bindgen` version in `Cargo.lock`
> and provide `wasm-opt` (Binaryen), or reuse the matching wasm-pack tool cache.
> Builds use `--mode no-install` and never update `Cargo.lock`.
> The original standalone browser experiments were not imported; app integration
> checks remain in app-monorepo.

# OneKey Zcash Runtime

官方 `librustzcash` 之上的最薄 wasm 绑定层。**不依赖 WebZjs**。


---

## 这个项目为什么存在

此前的实现建在 `ChainSafe/WebZjs` 的 fork 上，而那个仓库自 2026-04-16 起没有任何分支收到过推送。
更要紧的是它使用 `zcash_client_memory` 作为存储后端 —— 那个 crate 从未发布到 crates.io，
2026-06-20 被从 librustzcash 切成独立仓库后只有 dependabot 在动。我们为它 vendor 了
11,122 行代码并改动了 27 个源文件中的 22 个，且每次 `zcash_client_backend` 升版都要自己重迁一遍。

本项目改用 `zcash_client_sqlite`：它就在 librustzcash 仓库里，与 `zcash_client_backend`
**同日发版**，是官方 Android / iOS SDK 与 ECC 自家钱包 `zallet` 的存储层
（GitHub 上有 135 个 Cargo.toml 依赖它）。

换句话说：**大脑一直用的是官方的，只是存储层配了个孤儿。这个项目把它换回原厂件。**

---

## 同一套 Rust / WASM，所有 App 端共用

`onekey-zcash-keys` 负责派生和签名，不依赖 SQLite、网络、扫描器或 prover。
`onekey-zcash-runtime` 负责钱包、扫描、SQLite 和 PCZT 证明。它们是按功能拆分的两个
WASM 产物，不是按平台维护的两套实现；Desktop、Web、扩展和 iOS/Android 都使用它们。
`zcash-storage-benchmark` 仅用于开发者合成数据测试。

所有 App 载体均在 DedicatedWorker 执行公共 TypeScript SDK 和 WASM。
移动端由 WebEmbed 创建内联 Blob Worker；其他端创建 URL Worker。
Rust native 构建保留用于测试和诊断，不作为 App 的另一套生产后端。
WASM 大小以当前构建产物及 App 压缩包检查为准，不沿用早期实验数字。

## 硬约束

1. **不 vendor、不 fork 任何 Zcash 存储实现。** 存储层必须来自 crates.io 上未修改的
   `zcash_client_sqlite`。唯一例外是一处 Cargo.toml 改动（下节），Rust 源码零改动。
2. **种子仅在授权的派生/签名调用中短暂传入 keys WASM。** Rust 清理秘密缓冲区；
   JS 字符串及跨桥复制不能宣称可可靠擦除。扫描器持有 UFVK，不保存种子。
3. **接口动词少、数据简单。** 只传 string / number / boolean / JSON 字符串，不把 Rust
   对象句柄交给 JS 管生命周期。每次调用是一次完整往返。

## 边界：什么在 Rust，什么在 TS

| 留在 Rust（wasm） | 放在宿主 TS |
|---|---|
| 扫链的 trial decryption | 何时扫、扫哪段、扫多少（调度） |
| witness / 承诺树维护 | 账户与 birthday 管理 |
| 交易构造与选币（共识正确性） | 缓存与持久化策略 |
| 密钥派生与签名 | 进度展示、错误处理、重试 |
| 平台胶水（时钟、随机数、VFS） | 历史记录的展示模型 |

判据是三条：**密钥安全、跨边界数据量、共识正确性**。三条都不沾的，就该在 TS —— 那边改起来快、看得见、能打断点。

---

## 那一处 Cargo.toml 改动

`zcash_client_sqlite` 给 `rusqlite` 开了 `bundled` feature，它会在构建期用 cc 编译 SQLite
的 C 源码。`wasm32-unknown-unknown` 没有 libc sysroot，会停在
`fatal error: 'stdio.h' file not found`。Cargo 的 feature 是并集语义，下游无法取消上游
已启用的 feature，只能改它的 Cargo.toml。

处理方式是可审计的：`scripts/vendor-deps.sh` 下载官方发布版并施加
`patches/zcash_client_sqlite-0.22.0-no-bundled.patch`（**1 行**）。VFS 的公共 OPFS 修复也记录在 `patches/manifest.json`：连接锁、日志删除持久化、
句柄清理和元数据失败后停止 I/O。`scripts/prepare-vendor.py --offline` 根据校验过的
官方归档与补丁重建并比较全部文件，拒绝未记录的 vendor 改动。

wasm 侧的 SQLite 由 `sqlite-wasm-rs` 提供（预编译，不在构建期编 C）。
`crates/zcash-runtime/build.rs` 自动把它的 `libwsqlite3.a` 按链接器要的名字接上，
**调用方不需要任何手工 RUSTFLAGS**。

---

## 存储：数据存在哪

所有 App 端统一使用 `sqlite-wasm-vfs` 的 OPFS SAH-pool，目录为 `.onekey-zcash-opfs`。
SQLite 按页直接读写同步访问句柄，不再把整库预载成 WASM 内存镜像。
每个 origin 的池由一个 Worker 独占；第二个 Worker 不能静默改用其他数据库。
默认预留六个文件句柄，网络数据库仍为 `zcash-main.db` / `zcash-test.db`。

连接明确设置 `journal_mode=DELETE`、`synchronous=FULL`。提交和日志删除的 flush
错误必须传回宿主；元数据 I/O 失败后该 VFS 停止服务，需要重建 Worker。
不要求 SharedArrayBuffer、COOP/COEP 或 UI 线程上的同步文件访问。
OPFS 仍受浏览器 origin、配额和清理策略约束；App 内嵌页面的来源必须稳定。
本次处于开发阶段，不提供旧 IndexedDB 库迁移。运行时的新交易持久化要求仍然保留。

## 先前版本的验证记录（不替代当前 OPFS 验收）

> 状态：**runtime 的看账与发送链路已在 testnet 上真实上链验证，App 适配层已接入并通过
> 类型、单元与浏览器 runtime 检查。** App 内的有资金发送闭环与主网真实交易仍需人工验收。

**已验证**

- [x] `zcash_client_sqlite` 0.22.0 编进 wasm32 并链接（唯一改动是 1 行 Cargo.toml）
- [x] IndexedDB VFS，**完整 schema 迁移：44 表 / 17 视图 / 84 索引**，重载后仍在
- [x] 三个 target 编译通过：`wasm32-unknown-unknown` / `aarch64-apple-ios` / macOS 原生
- [x] 单测 27 个全过（另有 9 个联网用例，默认 ignore）
- [x] 浏览器错误契约 10 项断言：每个错误都带 `code` + `params` 对象
- [x] 真实 testnet 端到端：`chainTip` → 4291075、`importAccountUfvk`
- [x] `syncStep` 有界返回（原 `syncAdvance` 会跑到同步完成才返回，已修，见 docs/D5）
- [x] 浏览器实测批量 10 / 25 / 60 / 100 / 200 / 500 全部正常（持久化 IndexedDB VFS）：
      扫描阶段 12–70ms，总耗时由两次网络往返主导；batch 500 一次追平 203 块
- [x] 清缓存→重建闭环（`examples/web/purge-rebuild.html`）：擦后是全新库（账户为空、
      uuid 变化），用 ufvk+birthday 能完整重建。这条是「出问题能救回来」的保障
- [x] 发送路径的**错误契约** 9 项（不需要资金即可跑，`examples/web/send-errors.html`）：
      txid 非法/不存在、找零池参数错、PCZT 垃圾字节、电路版本错、
      reservation 非法/不存在各有可判定错误码
- [x] **不设限制，改为提前告知**（`examples/web/insufficient-errors.html`，四情形实测）：
      转账会花透明 UTXO —— 钱是用户的，链上也接受，运行时不替他拒绝。
      `pcztQuote` 走的是与 `pcztCreate` 完全相同的提案路径，因此除手续费外
      还报出**这笔实际会从哪些池取钱**：
      `{sourceTransparentZat, sourceShieldedZat, linksTransparentToShielded}`。
      `linksTransparentToShielded` 只在同一笔里既花透明又花屏蔽时为真 ——
      那是花透明**唯一新增**的暴露（只花透明的话，对外与一笔屏蔽交易无异），
      也就是唯一值得弹提醒的情形
- [x] **真发不出去时错误说得清**：`INSUFFICIENT_FUNDS`（都不够）／
      `FUNDS_NEED_SHIELDING`（宿主关掉了透明花费，钱在透明池里）／
      `COINBASE_FUNDS_UNSPENDABLE`（差额只有 coinbase 能补，而本钱包
      转账走 NonCoinbase、屏蔽走 NonCoinbaseOnly，两条路都碰不到它）。
      三者都带 `{requiredZat, shieldedAvailableZat, shortfallZat,
      transparentZat, transparentCoinbaseZat}`。
      原来只有一个 `INSUFFICIENT_FUNDS { availableZat: 0 }` ——
      而用户正看着账上有钱，那个 `availableZat` 说的是屏蔽池，名字里却没有
      「屏蔽」二字，除了猜没有别的办法
- [x] **端到端发送并广播上链**（testnet）：水龙头 0.1 TAZ 落 Ironwood 池 →
      `pcztCreate` → `pcztProve`（Halo2 约 19s）→ `pcztSign` → `pcztSend` →
      `broadcastTransaction`，链上确认。逐笔手续费对账到 zatoshi
- [x] **透明 ↔ 屏蔽双向**：隐私→透明地址、透明→隐私（`pcztShield`，含**透明输入签名**）、
      透明→透明（需 `spendTransparent`），全部链上确认
- [x] **多账户共库**：acct0/1/2 同一个库，扫一遍服务全部账户

**未验证 —— 别把这些当成能用**

- [ ] **App UI 内的有资金闭环尚未执行。** runtime + `examples/web` 已真实跑过 testnet
      双向发送；App 适配层已通过类型检查及 Token/History/发送状态相关单测，但这不能替代
      Desktop、Extension MV3、iOS、Android 四端的 UI 验收
- [ ] **交易记录里的 `feeZat` 重扫后为 null。** 它来自 `transactions.fee`，
      只有钱包自己构造那笔时写入，或跑 transaction enhancement 时补上 ——
      运行时没有 enhancement。用户清一次缓存重扫，历史里的手续费会全部消失
- [ ] 主网从未测过，全部实测都在 testnet
- [ ] 失去批量试解密的代价只在很空的 testnet 区块上量过（1.4–2 倍）。
      主网稠密区块上差距可能大得多，未测。
- [x] 以 Yarn portal 包接入 monorepo；正式发布仍需固定可追溯的 runtime 版本
- [ ] 从现有 wasm blob 迁移存量用户数据
- [ ] 原生目标的 lightwalletd transport（网络层目前只有浏览器 gRPC-web）

**已知约束**

- lightwalletd 端点**必须支持 gRPC-web + CORS**。实测
  `zcash-{mainnet,testnet}.chainsafe.dev` 可用；`zec.rocks` 只开原生 gRPC，
  浏览器里挂死在第一次请求上（`grpcurl` 反而通 —— 两者说的不是同一个协议，
  用命令行探活会得出相反的结论）。
- **扫描不走上游的 `scan_cached_blocks`**，改用自己的 `scan_blocks_inline`。
  上游把试解密交给 `rayon::spawn_fifo`，而 wasm32 单线程环境下 rayon 没有工作线程，
  任务永远不会执行，随后取结果时**永久堵死主线程**。根因与取证过程见
  `docs/decisions.md` D8。代价是失去批量试解密的摊销优化（原生实测扫描阶段慢 1.4–2 倍）。
  `batchSize` 没有上限约束；用 `syncPeek()` 的 `remainingBlocks` 画进度条。
- `syncPrepare()` 要下载三个池的全部子树根 —— 实测 testnet 约 1.4 秒
  （6 sapling / 3 orchard / 0 ironwood 子树），并不慢。宿主仍应给它独立的
  「准备中」状态，因为主网子树数量会大得多。
- `relaxed-idb` 在插件 offscreen 与移动 WebView 里的表现尚未实测（浏览器页面已证）。
- 产物：runtime 6.53 MB / keys 1.99 MB（均已过 `wasm-opt -Oz`）。PCZT 证明电路约占 1.6–2.0 MB。

## API

全部通过 JSON 字符串交互。失败抛 `Error`，其上挂 **`code`（稳定错误码）+ `params`
（结构化参数）+ `detail`（仅日志）**。Rust 侧不产出任何面向用户的文案 ——
文案与 i18n 归宿主。

### `onekey-zcash-runtime`（Desktop / Web / 插件）

```
init()                                   安装存储层，幂等，必须先调
openWallet(network, dbName)              开库 + schema 迁移
closeWallet()
setLightwalletdUrl(baseUrl)              端点由宿主注入，不硬编码

chainTip()                               当前链尖高度
importAccountUfvk(name, ufvk, birthday)  导入只读账户 → UUID
listAccounts()
accountBalance(uuid, trusted, untrusted, allowZeroConfShielding)

syncPrepare()                            一次性准备（子树根+链尖），慢，单独计时
syncStep(batchSize, maxBatches, priority, activeUfvksJson)
                                         仅为宿主显式启用的 UFVK 有界推进
queueRescanFrom(fromHeight)              非破坏性重排 [fromHeight, chainTip]
rewindTo(height)                         回退重扫（破坏性）

transactionHistory(uuid, limit, offset)  分页历史，limit 上限 500
transactionDetails(uuid, txid)           单笔详情，查不到返回 "null"
deleteWallet(dbName)                     擦掉本地缓存库（可重建），删账户/故障重置用

pcztQuote(...)                           只报价，不建交易、不锁 note
pcztCreate(...) → { pcztHex, reservationId }   构造并锁住选中的 note
pcztShield(...) → { pcztHex, reservationId }   屏蔽全部透明余额（无收款方、无金额）
releaseReservation(id)                   用户取消时放开锁，幂等
pcztProve(pczt, circuitVersion)  →  keys.pcztSign  →  pcztSend(pczt, reservationId) → txid
broadcastTransaction(txid)               真正发出去（pcztSend 只落库，不发网络）
selfCheck(network, dbName) / dependencyVersions()
```

### `onekey-zcash-keys`（iOS / Android，也可与上面同用）

```
ufvkFromSeed(network, seedBytes, accountIndex)         ← 接进 app 的实际入口
ufvkFromMnemonic(network, mnemonic, accountIndex)      测试/独立验证用
unifiedAddress(network, ufvk)
pcztSignWithSeed(network, seedBytes, accountIndex, pcztBytes)  ← 唯一接触花费密钥处
pcztSign(network, mnemonic, accountIndex, pcztBytes)
keysDependencyVersions()
```

### 发送的完整流程

```
runtime.pcztCreate()   构造（要钱包 DB：选币、witness、anchor）
runtime.pcztProve()    零知识证明（慢，要建 halo2 电路）
keys.pcztSign()        签名（要种子；在另一个包里）
runtime.pcztSend()     终结 + 写回本地钱包，返回 txid（**不发网络**）
runtime.broadcastTransaction(txid)   真正广播（不可逆）
```

**顺序不能换**：证明必须在签名之前 —— 上游 Signer 需要能验证证明覆盖的是正确数据。
广播是独立的一步，这样宿主能在落库之后、发出去之前插入确认环节，
也能在广播失败时原样重试而不用重建交易。
节点拒绝抛 `BROADCAST_REJECTED`（别重试，先查原因），网络故障抛 `NETWORK_ERROR`（可重试）。
`already known` / `already in mempool` / `already in blockchain` 视为幂等成功；所有触网调用
都有 30 秒上限，避免 gRPC-web 静默挂起。

---

## 用法

```bash
# 一次性：装 wasm target 和能编 wasm 的 clang
rustup target add wasm32-unknown-unknown
brew install llvm          # macOS 自带的 Apple clang 不支持 wasm32
cargo install wasm-pack

# 构建（自动跑 vendor-deps.sh）
bash scripts/build-wasm.sh

# 原生单测（首次运行前先准备 vendor）
bash scripts/vendor-deps.sh
cargo test --workspace --locked

# 校验缓存中的 vendor 与记录的上游／补丁完全一致
bash scripts/vendor-deps.sh --offline
```

产物 `pkg/`、`pkg-keys/` 不入库。两者一起构建，供 app-monorepo 的本地
portal 依赖或 CI artifact 使用。本次迁移不发布 npm 包。

---

## 目录

```
crates/zcash-runtime/     绑定层本体
  src/clock.rs            宿主时钟（std 在 wasm32 上没有时间源）
  src/error.rs            稳定错误码 + JS Error 映射
  src/storage.rs          VFS 安装与连接打开
  src/wallet.rs           WalletDb 组装与 schema 迁移
  src/bindgen.rs          面向 JS 的接口
  build.rs                自动接上 wasm 版 SQLite 静态库
patches/                  固定上游校验值、Cargo feature 与 VFS 持久化补丁
scripts/vendor-deps.sh    校验官方发布版并施加已记录的 patch
scripts/build-wasm.sh     构建
```

---

## 参考实现（读，不依赖）

- `ChainSafe/WebZjs` —— grpc-web 接线、wasm 线程池初始化的踩坑经验。
  注意它的 `tonic-web-wasm-client` 必须 ≥ 0.9.1（0.8.0 会丢 gRPC trailer）。
- `zcash/zcash-android-wallet-sdk` 的 `backend-lib/` —— ECC 自己写的 Rust FFI 层，
  生产在跑。核心 `lib.rs` 4,076 行是「该暴露什么、按什么粒度」的权威参照
  （其余约 18,000 行是该 SDK 自己的历史 migration、投票、slipstream，我们不需要）。
