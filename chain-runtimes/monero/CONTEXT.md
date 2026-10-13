# 维护上下文

**这里只记不能从代码本身读出来的东西。**

## 代码里看不出来的几件事

**一、扫描进度为什么在 `identities` 而不是从 `blocks` 推。**

`blocks` 表是全局的（块哈希是客观事实，各身份共享）。拿它的 `max(height)` 当进度，
会让一个追到链尖的身份把另一个还在回补的身份的进度一起推到链尖，那段历史被永久跳过。
回归测试 `two_identities_keep_independent_progress` 钉住了这一点。**别改回去。**

**二、为什么 `runtime.rs` 没有 wasm-only 门。**

加了门，`runtime.rs` 与绑定层在原生 `cargo test` 里根本不编译，并发防线（BUSY 守卫、
世代号）就测不到。这里刻意不加，`runtime.rs` 里的测试覆盖它们。

**三、`cargo check` 不链接。**

给 wasm 目标做验证时只跑 check 会漏掉链接期的问题。macOS 的 `/usr/bin/ar` 读不懂
wasm 目标文件，会把编译好的 `.o` **静默**打成空归档，只有 `cargo build` 才暴露。
归档器已由 `.cargo/config.toml` 固定成 llvm-ar。

## 发送的记账顺序

- 交易在**广播之前**就记为在途：输入按 key image 标为已花、写一条待确认记录。
  广播结局未知（超时、Worker 被终止）时，这笔交易仍然可见、输入不会被重新选中，
  由在途检查（`pool.rs`）或扫链按 key image 认出来解决。只有节点明确拒收时才撤销。
- 重组回退时，自己发出的交易改回待确认，而不是删除记录、放回输入。
- 已签名但始终没有走到广播的预留 24 小时后过期（未签名的 1 小时）。过期后这批输入
  重新排在选币前面，旧签名万一再被广播，两笔争同一批 key image，链上只成一笔。

## 协议性的硬限制，不是实现问题

**观察钱包检测不到花费。** 需要 key image，而 key image 需要花费私钥。宿主必须
在解锁时调 `importKeyImages`，否则余额偏高。`balance` 返回的
`unverifiableAtomic` 就是这部分，宿主应当据此提示用户。

**节点声称的 RingCT 输出索引不可验证。** 上游文档明确要求客户端自己维护并比对。
`scan.rs` 实现了独立推算（按本块 v2 交易的输出总数推进游标），不一致报
`RINGCT_INDEX_MISMATCH`。索引错了的后果是构环取错输出、交易被全网拒绝。

**时间戳形式的时间锁判不了。** 只有链高判得了块高形式的锁。时间戳形式的一律
按「仍锁定」处理 —— 让用户等，好过让他构一笔必然被拒的交易。

## 不信节点的几个值

「节点说错了」的后果比「我们发版晚了」严重，所以这些由 runtime 写死或校验：

- **环大小 16**（`spend::RING_LEN`）
- **RctType = ClsagBulletproofPlus** —— 跟着硬分叉由我们发版控制
- **手续费**：每档优先级各有费率上限（`spend::max_fee_per_weight`，按主网现值留
  5 倍余量）；节点给的取整单位（quantization mask）必须等于协议常量
  `spend::FEE_QUANTIZATION_MASK`（10,000）。不符就拒绝构造交易，宁可发不出去也不多付。
- **网络**：连接节点时按创世块认出它在哪条链上，认不出就不连；钱包库与节点一起使用前
  比对两边的网络，不一致报 `NODE_NETWORK_MISMATCH`。`chainTip` 在钱包库打开前调用，
  返回里带 `network`，宿主定 birthday 前须自行比对。

## 对真实节点的测试

`tests/live_node.rs` 里联网的用例需要显式指定节点：

```bash
XMR_NODE=<monerod RPC URL> cargo test --test live_node -- --nocapture
```

它会打印链尖、扫描速度（块/秒）和余额。
