# Monero 硬件签名与离线签名：接缝在哪，什么已经做了

记录于 2026-09-22。结论都注明了来源（上游源码行为 / 固件源码 / 实测）。

## 一句话

软件签名已经通了。硬件签名接不上的地方**只有一个是不可逆的**，已经修掉；
其余都能在真正接硬件时再补，不会丢数据。

---

## 1. 三方各自给什么

### monero-oxide（我们用的密码学库）

签名只有两条路：

| 路径 | 入参 | 适用 |
|---|---|---|
| `SignableTransaction::sign(rng, spend_key)` | 花费私钥 | 软件钱包（我们现在走这条） |
| `SignableTransaction::multisig(ThresholdKeys<Ed25519>)` | FROST 门限密钥 | 门限多签，**不是**硬件 |

没有「外部签名者」回调。`multisig` 是 FROST，和固件那套分步协议是两回事。

`SignableTransaction` 的公开访问器只有：
`fee_rate()` / `necessary_fee()` / `serialize()` / `read()` /
`unsigned_transaction(key_images)` / `sign()`。

**它不公开 `inputs()` 也不公开 `payments()`** —— 序列化出来的 blob 从外面
拆不开。要拿里面的东西，只能在 `prepare` 那一刻、东西还在手里时留下来。

有用的是 `unsigned_transaction(key_images: Vec<CompressedPoint>)`：
这是上游留的两段式接缝 —— key image 从外面给（设备算的），它给出未签名交易。

### 固件（`firmware/core/src/apps/monero/`）

实现的是 Trezor 那套分步协议，step 01–10：
init → set_input → inputs_permutation → input_vini → all_inputs_set →
set_output → all_outputs_set → sign_input → sign_final。

**设备自己构造并签名整笔交易**，宿主只是喂数据。

每个输入要一个 `MoneroTransactionSourceEntry`（见
`signing/step_02_set_input.py`）：

- `outputs[]` —— 整个环，每项 `{ idx（全局序号）, key.dest, key.mask }`
- `real_output` —— 环里哪一个是我们的
- `real_out_tx_key` —— **创建这笔输出的那条交易的公钥 R**
- `real_out_additional_tx_keys` —— 子地址收款时的 additional keys
- `real_output_in_tx_index` —— 我们的输出在那条交易里的序号
- `amount` / `subaddr_minor` / `rct`

关键一点，别想绕：设备用 `real_out_tx_key` + `real_output_in_tx_index` +
子地址**自己推导**一次性私钥和 key image。它不接受宿主算好的偏移量 ——
接受了就等于让一个恶意宿主指挥设备拿任意密钥签名。

### hd-core（`@onekeyfe/hd-core@1.2.4`，实测）

48 个 `Monero*` protobuf **类型**，**0 个可调用方法**。
`CoreApi` 上没有任何 `moneroXxx()`。SDK 现在驱动不了签名会话。

---

## 2. 差在哪：一个不可逆，其余可逆

### 不可逆（已修）：交易公钥 R

monero-oxide 扫描时把「R + 输出序号 + 子地址」整条推导**折叠成一个
`key_offset` 标量**（`WalletOutput` 只存 `key_offset: Scalar`），
然后把 R 丢掉。软件签名只要 `spend + key_offset`，所以一直没暴露出来。

固件要 R。而 R 只在扫描那一刻手里有，事后只有两条路：

1. 全量重扫（每个已有用户一次）
2. 拿 txid 去问节点要回那条交易 —— 等于直接告诉节点「我对这笔感兴趣」，
   把扫全链换来的隐私当场还回去

所以**必须在发版前就开始存**。已做：

- schema V2：`outputs.tx_pub_keys` + `outputs.tx_additional_pub_keys`
  （32 字节一个顺序拼接；没有 additional 时存 NULL 而不是空串 ——
  「有 0 个」和「没有这个字段」是两回事）
- `scan.rs::tx_public_keys_from_extra()` 在扫描当下从 `Extra` 取
- 只在这一块确实有我们的输出时才解析（绝大多数块一个都没有）
- 测试：真实主网向量（高度 3767756，交易
  `097edcb4…51bb`）+ 迁移测试（v1 库就地升 v2）+ 空 extra 不 panic

### 可逆（不急）：环成员与诱饵

`Decoys` 公开了 `offsets()` / `positions()`（绝对全局序号）/
`signer_index()`（= `real_output`）/ `ring()`（= `[dest, commitment]`），
全都够用。

但它们只活在 `SignableTransaction` 里，而那个类型不公开 `inputs()`。

**不要紧**：诱饵每次 `prepare` 都重新选（必须如此 —— 用旧链高选的环，
成员分布会偏离当下的链上分布，那是可被统计分析利用的指纹）。
所以什么都没丢，接硬件那天让 `prepare` 顺手多返回一份就行。

真接的时候改这里：`spend.rs::prepare()` 里 `inputs` 那个 Vec 还在手上，
在它被 move 进 `SignableTransaction::new` 之前把每项的
`decoys().positions()` / `ring()` / `signer_index()` 取出来。

---

## 3. 一条硬约束：`signableHex` 等价于 view key

上游把整个 viewing pair 写进找零输出：
`ChangeEnum::Standard { view_pair, .. }`，
`write()` 里是 `view_pair.spend().compress().write(w)` 后面紧跟
`view_pair.view.write(w)` —— **私有 view key 明明白白在里面**。

原因是 Monero 的钱包协议按「发送方能看到自己的找零」来优化。
不带 view key 的变体是 `Change::fingerprintable`，上游自带警告
「You MUST assume this will harm your privacy」，所以不能为了躲这个去换。

**对离线签名设计的直接后果**：那份「未签名交易」不是无害的。
谁拿到它，谁就能看见这个账户收到过的每一笔钱。它只能在 view key
本来就可以去的地方流动。做气隙签名（二维码 / U 盘）之前必须先想清这一条。

---

## 4. 接硬件时的动作清单

1. 等 hd-core 暴露 Monero 方法（现在是 0 个）。没有这个前面都白搭。
2. `prepare` 增补返回每个输入的环数据（见 §2「可逆」那段，改一处）。
3. 写 `KeyringHardware`，按固件 step 01–10 驱动。
   接缝已经在位：vault 的发送路径按 keyring 分流，
   `IEncodedTxXmr` 带着未签名产物，软件签名走 `CoreChainSoftware`，
   硬件那条只要在同一个位置插进去。
4. key image 从设备来，用
   `SignableTransaction::unsigned_transaction(key_images)` 合回去。
