# OneKey Monero Runtime

monero-oxide 之上的钱包状态层。**它提供密码学、扫描器与交易构造；存储这一层
是我们自己写的，因为上游不提供。**

## 结构

```
error.rs     错误码契约（跨 wasm 边界稳定）
storage.rs   SQLite 连接、OPFS VFS、落盘屏障
schema.rs    表结构与迁移
keys.rs      观察密钥对、地址、子地址派生
wallet.rs    库句柄、身份管理、子地址注册表
scan.rs      扫描循环 + 三道校验 + 落库
sync.rs      有界推进编排（拉块 → 扫描 → 记进度）
query.rs     余额、历史、可花输出
spend.rs     key image 导入、选币、预留、构造、签名、广播
review.rs    签名前按用户确认的请求校验收款方、金额、手续费与找零
pool.rs      自己广播、尚未上链的交易的存活检查
network.rs   monerod 传输（浏览器 fetch / 原生 reqwest）
runtime.rs   跨调用会话状态与并发防线
bindgen.rs   wasm 边界
```

## 必须知道的事

**一、观察钱包检测不到花费。** Monero 的花费检测需要 key image，而 key image
需要花费私钥。这是协议性质不是实现取舍。所以宿主必须在解锁时调一次
`importKeyImages`，否则余额会**偏高**（已花的看起来还在）。余额查询里的
`unverifiableAtomic` 就是这部分的金额，宿主应当据此提示用户。

**二、节点声称的 RingCT 输出索引不可验证。** 上游文档明确要求客户端自己维护
这个索引并比对，不一致应当断开。`scan.rs` 实现了这个独立推算，不一致时报
`RINGCT_INDEX_MISMATCH`。索引错了的后果是构环时取错输出、交易被网络拒绝，
而且很难从现象反推。

**三、调度归宿主。** `syncStep` 一次最多扫 `maxBlocks` 个块就返回，**永远不会
自己循环到同步完成**。什么时候扫、扫多少、失败怎么退避全由宿主 TS 决定。

**四、节点必须和钱包在同一个网络上。** 连接时按创世块认出节点所在的链，钱包库与
节点网络不一致时报 `NODE_NETWORK_MISMATCH`。`chainTip` 在开库前调用，返回里带
`network`，宿主定 birthday 前须自行比对。

**五、广播结局 `unknown` 不是失败。** 交易在广播前已记为在途、输入已标已花；
结局不确定时由在途检查或扫链判定。宿主不得据此提示失败、引导重发。

## 构建

```bash
./scripts/build-wasm.sh          # release 到 pkg/
cargo test --workspace           # 原生测试
cargo check --workspace --target wasm32-unknown-unknown
./scripts/build-node-pkg.sh && node scripts/smoke.js   # wasm 端到端冒烟
```

**macOS 上必须有 llvm-ar**（`brew install llvm`）。系统自带的 `/usr/bin/ar`
读不懂 wasm 目标文件，会把编译好的 `.o` **静默**打成一个 96 字节的空归档，
链接期报一串 `undefined symbol: sqlite3_*`，而编译阶段不报任何错。归档器由
`.cargo/config.toml` 指定。

排查提示：本机 clang 是能编 wasm32 的，别往编译器上查。
另外 `cargo check` **不链接**，这个坑在 check 下完全看不见。

## 与 Zcash runtime 的关系

两条链各自独立的工作区，没有代码依赖。`crates/monero-runtime/build.rs`（SQLite 的
wasm 链接胶水，与链无关）与 Zcash 那边基本相同，改一边记得同步另一边。

一个刻意的差异：`runtime.rs` 在这里**不加 wasm-only 门**，所以并发防线（BUSY 守卫、
世代号）在原生 `cargo test` 里也能测。
