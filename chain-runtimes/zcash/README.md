> Build commands below run from this directory. Use Rust 1.93.0 (pinned in
> `rust-toolchain.toml`), Python 3.11+, LLVM/Clang, and wasm-pack 0.15.0.
> Install `wasm-bindgen-cli` at the `wasm-bindgen` version in `Cargo.lock`
> and provide `wasm-opt` (Binaryen), or reuse the matching wasm-pack tool cache.
> Builds use `--mode no-install` and never update `Cargo.lock`.

# OneKey Zcash Runtime

官方 `librustzcash` 之上的最薄 wasm 绑定层。**不依赖 WebZjs**。

---

## 同一套 Rust / WASM，所有 App 端共用

`onekey-zcash-keys` 负责派生和签名，不依赖 SQLite、网络、扫描器或 prover。
`onekey-zcash-runtime` 负责钱包、扫描、SQLite 和 PCZT 证明。它们是按功能拆分的两个
WASM 产物，不是按平台维护的两套实现；Desktop、Web、扩展和 iOS/Android 都使用它们。
`zcash-storage-benchmark` 仅用于开发者合成数据测试。

所有 App 载体均在 DedicatedWorker 执行公共 TypeScript SDK 和 WASM。
移动端由 WebEmbed 创建内联 Blob Worker；其他端创建 URL Worker。
Rust native 构建保留用于测试和诊断，不作为 App 的另一套生产后端。

## 硬约束

1. **不 fork 任何 Zcash 存储实现。** 存储层来自 crates.io 上的 `zcash_client_sqlite`，
   Rust 源码零改动；唯一改动是一处 Cargo.toml feature（下节）。
2. **种子仅在授权的派生/签名调用中短暂传入 keys WASM。** Rust 清理秘密缓冲区；
   JS 字符串及跨桥复制不能宣称可可靠擦除。扫描器持有 UFVK，不保存种子。
3. **接口动词少、数据简单。** 只传 string / number / boolean / bytes / JSON 字符串，
   不把 Rust 对象句柄交给 JS 管生命周期。每次调用是一次完整往返。

## 边界：什么在 Rust，什么在 TS

| 留在 Rust（wasm） | 放在宿主 TS |
|---|---|
| 扫链的 trial decryption | 何时扫、扫哪段、扫多少（调度） |
| witness / 承诺树维护 | 账户与 birthday 管理 |
| 交易构造与选币（共识正确性） | 缓存与持久化策略 |
| 密钥派生与签名 | 进度展示、错误处理、重试 |
| 平台胶水（时钟、随机数、VFS） | 历史记录的展示模型 |

判据是三条：**密钥安全、跨边界数据量、共识正确性**。三条都不沾的，就该在 TS。

---

## Vendor 补丁

`zcash_client_sqlite` 给 `rusqlite` 开了 `bundled` feature，它会在构建期用 cc 编译 SQLite
的 C 源码。`wasm32-unknown-unknown` 没有 libc sysroot，会停在
`fatal error: 'stdio.h' file not found`。Cargo 的 feature 是并集语义，下游无法取消上游
已启用的 feature，只能改它的 Cargo.toml。

处理方式是可审计的：`scripts/vendor-deps.sh` 下载官方发布版并施加
`patches/manifest.json` 记录的补丁 —— `zcash_client_sqlite` 的 1 行 Cargo.toml 改动、
`sqlite-wasm-vfs` 的 OPFS 持久化修复（连接锁、日志删除持久化、句柄清理和元数据失败后
停止 I/O），以及 `tonic-web-wasm-client` 的宿主 fetch 注入。`scripts/prepare-vendor.py --offline`
根据校验过的官方归档与补丁重建并比较全部文件，拒绝未记录的 vendor 改动。

wasm 侧的 SQLite 由 `sqlite-wasm-rs` 提供（预编译，不在构建期编 C）。
`crates/zcash-runtime/build.rs` 自动把它的 `libwsqlite3.a` 按链接器要的名字接上，
**调用方不需要任何手工 RUSTFLAGS**。

---

## 存储：数据存在哪

所有 App 端统一使用 `sqlite-wasm-vfs` 的 OPFS SAH-pool，目录为 `.onekey-zcash-opfs`。
SQLite 按页直接读写同步访问句柄，不把整库预载成 WASM 内存镜像。
每个 origin 的池由一个 Worker 独占；第二个 Worker 不能静默改用其他数据库。
默认预留六个文件句柄，网络数据库为 `zcash-main.db` / `zcash-test.db`。

连接明确设置 `journal_mode=DELETE`、`synchronous=FULL`。提交和日志删除的 flush
错误必须传回宿主；元数据 I/O 失败后该 VFS 停止服务，需要重建 Worker。
不要求 SharedArrayBuffer、COOP/COEP 或 UI 线程上的同步文件访问。
OPFS 仍受浏览器 origin、配额和清理策略约束；App 内嵌页面的来源必须稳定。

## 已知约束

- lightwalletd 端点**必须支持 gRPC-web + CORS**。`zcash-{mainnet,testnet}.chainsafe.dev`
  可用；只开原生 gRPC 的端点在浏览器里会挂在第一次请求上（`grpcurl` 能通不代表
  gRPC-web 能通）。端点由宿主注入，runtime 不硬编码。
- **扫描不走上游的 `scan_cached_blocks`**，改用自己的 `scan_blocks_inline`。
  上游把试解密交给 `rayon::spawn_fifo`，而 wasm32 单线程环境下 rayon 没有工作线程，
  任务永远不会执行，随后取结果时永久堵死主线程。
  代价是失去批量试解密的摊销优化。用 `syncPeek()` 的 `remainingBlocks` 画进度条。
- `syncPrepare()` 要下载各池的全部子树根。宿主应给它独立的「准备中」状态，
  主网子树数量比 testnet 多得多。
- Sapling 不在产品范围内：不作为收款 receiver、扫描身份或发送来源。

## API

全部通过 JSON 字符串或字节交互。失败抛 `Error`，其上挂 **`code`（稳定错误码）+ `params`
（结构化参数）+ `detail`（仅日志）**。Rust 侧不产出任何面向用户的文案 ——
文案与 i18n 归宿主。`capabilities()` / `keysCapabilities()` 报告当前构建实际支持的能力。

### `onekey-zcash-runtime`

```
init()                                   安装存储层，幂等，必须先调
setRequestHandler(handler)               宿主提供 lightwalletd 请求的 fetch
openWallet(network, dbName) / closeWallet()
setLightwalletdUrl(baseUrl)              端点由宿主注入，不硬编码
chainTip() / chainTipAt(baseUrl)

importAccountUfvk(name, ufvk, birthday, seedFingerprintHex?, accountIndex?)
                                         导入账户 → UUID；服务器返回的树状态必须
                                         正好是 birthday 前一块
removeAccount(uuid) / removeDeletedAccount(uuid)
accountByUfvk(ufvk) / listAccounts() / accountSyncStatus(uuid)
accountBalance(uuid, trusted, untrusted, allowZeroConfShielding)

syncPrepare() / syncTip() / syncPeek()   准备（子树根 + 链尖）与只读进度
syncStep(batchSize, maxBatches, priority, activeUfvksJson)
                                         仅为宿主显式启用的 UFVK 有界推进
queueRescanFrom(fromHeight)              非破坏性重排 [fromHeight, chainTip]
rewindTo(height)                         回退重扫（破坏性）

transactionHistory(uuid, limit, offset)  分页历史，limit 上限 500
transactionDetails(uuid, txid) / transactionOutputs(uuid, txid)
deleteWallet(dbName)                     擦掉本地缓存库（可重建）
storageStats()

pcztQuote(...)                           只报价，不建交易、不锁 note
pcztCreate(...) → { pcztHex, reservationId }   构造并锁住选中的 note
releaseReservation(id)                   用户取消时放开锁，幂等
clearLockedOutputs(uuid)                 显式修复：清掉该账户全部锁（有未决广播时拒绝）
pcztProve(pczt)                          生成证明
pcztCombine(original, signed)            合并外部签名器回传的副本（只许补签名）
pcztSubmit(uuid, pczt, reservationId)    终结、落库、广播，返回 {txid, broadcast}
pendingBroadcastTxids(uuid) / broadcastRetryTxids(uuid)
broadcastTransaction(txid)               重播已落库的交易（同步循环用）

prewarmPczt(network, height)             预建证明参数，不开钱包
pcztProveAtHeight(height, pczt)          无钱包证明（透明资金付给 UA 的路径）
pcztExtractStateless(pczt)               无钱包校验并提取最终交易
dependencyVersions() / diag*             诊断
```

### `onekey-zcash-keys`

```
ufvkFromSeed(network, seedBytes, accountIndex)
unifiedAddress(network, ufvk, receiverPolicy)
                                         "all" = Orchard + 透明；"shielded" / "orchard"
                                         = 仅 Orchard。不含 Sapling receiver
transparentAddressFromUfvk(network, ufvk) / transparentAccountPubKeyFromUfvk(network, ufvk)
validateAddress(network, address) / seedFingerprint(seed)
pcztVerifyReview(pczt, reviewJson)       只校验，不签名
pcztSignWithSeed(network, seed, accountIndex, pczt, reviewJson)
                                         唯一接触花费密钥处；先按 review 校验每个
                                         spend 与 output，不符即拒绝
transparentTx* / transparentShield*      无状态透明交易，见 TRANSPARENT_TX_API.md
parseTransparentTransactions(requestJson)
keysCapabilities() / keysDependencyVersions()
```

### 发送的完整流程

```
runtime.pcztCreate()        构造（要钱包 DB：选币、witness、anchor），锁住 note
runtime.pcztProve()         零知识证明（慢，要建 halo2 电路）
keys.pcztSignWithSeed()     按 review 校验后签名（要种子；在另一个包里）
runtime.pcztSubmit()        与原件合并校验 → 提取 → 落库 → 广播
```

**顺序不能换**：证明必须在签名之前 —— 上游 Signer 需要能验证证明覆盖的是正确数据。

**先落库后广播**：签好的交易字节与 reservation→txid 关联在广播前原子落库，之后的每次
重试和崩溃恢复都广播同一份字节。广播结局：

- `accepted`：节点接受。`already known` / `already in mempool` / `already in blockchain`
  视为幂等成功。
- `rejected`：只有明确表示交易在共识上无效或已过期的回复才算拒收。交易仍保留在库中，
  它选中的 note 要到交易过期高度之后才会重新可用。
- `unknown`：其余任何错误（超时、临时错误、无法判定的回复）。交易保持待确认，
  由同步循环重播原字节；宿主不得把它当作失败、引导用户重发。

同一个 `reservationId` 已有落库交易时，`pcztCreate` 拒绝再用它构造新交易；
`pcztSubmit` 对它只会重播已落库的那一笔。所有触网调用都有 30 秒上限。

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

产物 `pkg/`、`pkg-keys/` 不入库。两者一起构建，供 App 的本地 portal 依赖或 CI
artifact 使用。独立 npm 发布流程、版本规则与 App 切换步骤见
[RELEASING.md](./RELEASING.md)。

---

## 目录

```
crates/zcash-runtime/       钱包、扫描、PCZT 构造/证明/提交
  src/bindgen.rs            面向 JS 的接口
  src/send.rs               提案、reservation、PCZT 生命周期
  src/tx_state.rs           reservation→txid 关联与广播结局
  src/guard.rs              破坏性操作的持久化闸门
  src/sync.rs               有界扫描
  src/storage.rs            VFS 安装、连接打开与落盘屏障
  build.rs                  自动接上 wasm 版 SQLite 静态库
crates/zcash-keys-runtime/  派生、review 校验与签名、无状态透明交易
crates/zcash-storage-benchmark/  开发者合成数据测试
patches/                    固定上游校验值与补丁
scripts/vendor-deps.sh      校验官方发布版并施加已记录的 patch
scripts/build-wasm.sh       构建
```

## Stateless transparent funding of Unified recipients

`transparentTxQuote` accepts mainnet t1/t3/tex recipients and Unified recipients with an
Orchard receiver (an Ironwood output after NU6.3). It uses only the host-supplied
confirmed, regular transparent UTXOs, explicit outpoints, target height, and
transparent change address. Sapling/Sprout destinations and coinbase inputs remain
unsupported. No wallet registration, database, scan, or network call is required.

Software keyrings use this sequence for Unified recipients:

1. `keys.transparentShieldCreateWithSeed` (or `WithAccountXprv`) constructs a PCZT
   from the same validated request used for the quote.
2. `runtime.pcztProveAtHeight` proves it without calling `init` or opening a wallet.
3. `keys.transparentShieldSignWithSeed` (or `WithAccountXprv`) checks the original
   request, all effects of the original PCZT, and the proved PCZT before signing.
4. `runtime.pcztExtractStateless` verifies and extracts the final transaction.

`parseTransparentTransactions` verifies raw previous transactions against their
requested transaction IDs and returns transparent output values, scripts, and
coinbase status. It only needs transaction IDs and raw bytes; the host must check
each UTXO's output index and value against the result.
This API uses only the keys module; no wallet database or scan is required.

The host owns transparent outpoint reservations, signature-time UTXO revalidation,
pending broadcast records, and submission to its node. The recipient's wallet
scans to discover and later spend the received shielded notes; the sender does not
need to scan. Private inputs still use the existing stateful wallet/PCZT APIs.

The App's hardware Shield flow is separate; these signing exports are for software
seed/account-xprv keyrings.
