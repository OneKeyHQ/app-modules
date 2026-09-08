# Zcash Runtime — 领域词汇

本文件只定义**术语**，不放实现细节、不放待办。设计决策见 `docs/decisions.md`。

---

## Wallet（钱包库）

一份 SQLite 数据库，由 `zcash_client_sqlite` 管理其 schema。

**一个 wallet 对应一个 network，而不是一个种子或一个账户。** 宿主为同一网络上的
所有 Account 注册 UFVK，数据库一次下载和扫描即可试解密全部账户。wallet 的身份只有
`network`，**不包含 lightwalletd 端点**：同一条链上不同端点提供同一份数据，换端点
不能导致全量重扫。

wallet 是**纯派生缓存**。SQLite 会保存已注册 UFVK、birthday 和扫描结果，以便重载后
继续工作；但这些不是唯一副本。宿主账户元数据仍是 UFVK 与 birthday 的权威来源，
所以删除数据库只会触发重新注册和重扫，不会丢失不可再生材料。

## Account（账户）

wallet 内的一个 UFVK 及其派生地址。对应 OneKey 的一个链上账户（一个 `hdIndex`）。

在 runtime 内以 UUID 标识。**该 UUID 不稳定**：Purge 后重建会得到新的 UUID，
所以宿主不能缓存它，每次从 UFVK 重新解析。

## Seed Fingerprint（种子指纹）

ZIP-32 定义的种子标识。**是标准值，不是任意哈希** —— 宿主持久化它作为账户身份，
自行发明的哈希会让存量账户全部失配。

## Public Test Mnemonic（公开测试助记词）

专用于可重复测试、已公开且不承载真实资产的助记词夹具。当前允许记录的是标准
`abandon` 开头、`about` 结尾的 12 词测试向量；任何真实钱包或非公开测试钱包的
助记词都不属于此概念，禁止进入文档、日志、截图或测试产物。

_Avoid_: Test seed、测试钱包助记词（两者都容易被误解为可以记录任意测试账户密钥）

## Purge（清缓存）

丢弃已扫结果、保留重建所需材料的操作。因为 wallet 是纯派生缓存，Purge 只是
"要重扫一遍"，不丢任何不可再生的数据。

两种用途：出问题时的手动重置；删账户时清除已解密的屏蔽交易历史——后者是隐私要求，
那份历史正是 Zcash 存在的意义所要隐藏的东西。

## Lane（同步车道）

扫描进度分两条逻辑车道，回答不同问题：

- **Tip lane**：余额新不新。用户当下看的是它。
- **Birthday lane**：这个钱包是否已经知道自己的全部历史。**它才决定余额可不可信**——
  回填未完成时余额可能偏低，因为更早的收款还没被发现。

两条车道由宿主调度，runtime 只按宿主指定的优先级推进一段，且候选区间只能来自
上游的建议集合。

## Reservation（占用凭证）

一次交易提案对所选 note 的占用，以 32 字节 owner token 标识。

存在的理由：屏蔽 note 形如 UTXO，同一个 note 被两笔交易花，第二笔必被拒。而
"选好币"到"真的花掉"之间隔着审核页、密码、证明、签名，这段窗口内必须防止
另一次选币挑中同一批。

token 由**调用方持有**：先生成、先持久化、再构造，这样崩溃恢复后仍知道该释放哪一批。

## Capability（能力声明）

runtime 当下**实际**能做什么。宿主据此提前隐藏做不到的操作。

每个标志定义在限制它的那段代码旁边，且与运行时的拒绝分支一一对应——不会出现
"声称支持其实不支持"。发送失败得晚代价很高：提案已锁 note、用户已输密码。

## 池（Pool）

- **transparent**：公开，与比特币同构。再分 regular 与 coinbase，后者成熟规则不同
  且只能经由专门的 shielding 路径转出。
- **sapling**：本产品不支持的历史屏蔽池。不得作为收款 receiver、余额或历史的
  产品数据源，也不得作为发送来源；“不支持”不是“显示但只读”。
- **orchard**：屏蔽。保留历史余额并支持迁出；NU6.3 后不再是新收款目标。
- **ironwood**：NU6.3 引入的当前屏蔽池；向 UA 的新付款默认落在这里。

跨池合计是产品口径，不是链上事实，因此由宿主计算；runtime 只逐池报事实。

## Private Receive UA Policy（私密收款地址策略）

App 对外展示的私密 Unified Address 所包含的 receiver 集合，当前仅包含 Orchard
receiver。Ironwood 没有独立的 UA receiver typecode；“地址含 Orchard receiver”不代表
新收到的资金会进入 Orchard。

_Avoid_: Ironwood 地址、最新池地址

## Current Shielded Output Pool（当前私密输出池）

当前网络升级下，新私密输出、Shield All 目标和私密找零实际落入的池，当前为
Ironwood。它与 Private Receive UA Policy、可花费来源池是三个不同维度；调用方不得
各自写死池名。

_Avoid_: Current Receiving Pool、最新池、默认隐私池

## Spendable Shielded Sources（可花费私密来源池）

普通发送允许消费的私密来源池集合，当前为 Orchard 和 Ironwood。指定来源池时必须
严格隔离，不得因透明花费测试开关而追加其他池的输入。

_Avoid_: Current Shielded Output Pool（它描述输出目标，不描述可花费来源）

## Shield All（全部屏蔽）

把账户中全部符合条件的 regular transparent UTXO 扣除手续费后，转入自己的
Current Shielded Output Pool。它不消费任何已有私密池，也不消费 Coinbase UTXO。

_Avoid_: Shield、归集（单独使用时无法表达来源、目标和“全部”语义）

## Internal Transparent Spending（内测透明花费）

wallet runtime 接受普通透明输入的底层能力。它不属于 OneKey 产品的正常发送路径；
App 始终传入关闭值，能力的开启分支只能由 runtime 自身的隔离测试覆盖，不能出现在
普通 Debug UI 或可持久化设置中。

_Avoid_: Debug 开关（Developer Mode 在正式构建中仍可能启用，不等价于构建授权）

## Transparent Mode（透明模式）

账户只使用 Zcash 透明地址和 OneKey 后端数据，不注册 UFVK、不扫描私密池，也不加载
wallet runtime。查看余额和历史不需要任何 Zcash WASM；软件发送按需加载无数据库、
无网络、无扫描器的轻量 Zcash signer。

_Avoid_: Privacy disabled（容易把产品模式误解为一次临时暂停）

## Privacy Mode（隐私模式）

账户允许注册 UFVK，并由共享 wallet runtime 扫描支持的私密池。关闭该模式必须停止
该 UFVK 的 trial decryption，而不只是隐藏 UI。当前支持对象是软件 HD 账户；导入
助记词仍属于 HD，单私钥账户和观察账户不属于 Privacy Mode。

## Privacy Pause（暂停隐私处理）

把 UFVK 标为 inactive，停止后续扫描并隐藏私密余额、历史、收款和发送入口，同时保留
runtime 缓存与 birthday。它不会移动链上资金，也不是删除本地隐私数据。

## Active UFVK Set（当前启用的隐私身份集）

宿主每次调用扫描时显式传入的 UFVK 去重集合。Runtime 只用这些身份的
Orchard key 试解密 Orchard/Ironwood，不在 Rust DB 持久化产品开关；空集合是错误，
不得解释为“扫描全部账户”。

_Avoid_: Registered UFVK Set（数据库已注册身份不等于产品已启用身份）

## Privacy Resume Cursor（隐私恢复游标）

宿主在 Privacy Pause 时保存的区块高度。同 network wallet 的 scan queue 是全局的，
其他 Active UFVK 在暂停期间会继续推进它；因此重新启用前，宿主必须加入重组安全余量，
再用 `queueRescanFrom` 把缺失区间重新排队。该游标属于宿主产品状态，不属于 runtime DB。

## Delete Local Privacy Data（删除本地隐私数据）

用户明确触发的破坏性本地操作：删除账户的 runtime 派生缓存和本地 UFVK/UA 元数据，
但保留 birthday。重新开启 Privacy Mode 时重新派生观察材料，并从保留的 birthday
重扫。该操作不得与普通模式开关合并。
