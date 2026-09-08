//! 面向宿主 JS/TS 的接口。
//!
//! ## 约定
//!
//! - 入参与返回值只用 string / number / boolean / JSON 字符串。
//! - 不把 Rust 对象句柄交给 JS 管生命周期。
//! - **失败抛的 `Error` 上挂 `code` / `params` / `detail`。**
//!   `code` 是稳定错误码，用它做分支；`params` 是结构化参数，用它填 i18n 占位符；
//!   `detail` 只用于日志。**这里不产出任何面向用户的文案** —— 文案属于宿主。
//! - 每个调用都是一次完整往返，不留后台任务、不留定时器。

// ── 稳定契约 vs 诊断 ────────────────────────────────────────────────────
//
// `diag*` 前缀的方法是**排障工具，不是稳定契约**：它们随时可能改签名或消失。
// 分开命名是为了让依赖它们这件事在调用点就看得见 —— 否则宿主很容易顺手用上，
// 将来想删就变成破坏性升级。
//
// 其余方法构成钱包契约，改动需要走版本。

use wasm_bindgen::prelude::*;

use crate::perf;
use crate::{
    account,
    error::{ErrorCode, RuntimeError},
    history, network, runtime, send, storage, sync, tx_state, wallet,
};

// ── 生命周期 ────────────────────────────────────────────────────────────

/// 安装存储层。必须在其他任何调用之前 await 一次。幂等。
#[wasm_bindgen(js_name = init)]
pub async fn init() -> Result<(), JsValue> {
    install_panic_hook();
    storage::install_persistent_vfs().await?;
    Ok(())
}

/// panic 变成带堆栈的 console 错误，而不是一个永远不 settle 的 Promise。
///
/// 必须在**每个**入口都装。曾经只装在 `init()` 里，于是走 `initMemoryStorage()`
/// 的排查路径上 panic 完全静默 —— 排查工具本身反而看不见错误。
fn install_panic_hook() {
    console_error_panic_hook::set_once();
}

/// 打开 tracing 输出到浏览器 console（需 `debug-tracing` feature）。
///
/// 上游 `zcash_client_backend` 全程用 `tracing` 打点。同步卡住时，
/// console 里的**最后一条**日志就指出它停在哪一步 —— 比反复猜快得多。
///
/// 日志量很大且会拖慢扫描，只在排查时用。
#[wasm_bindgen(js_name = diagEnableTracing)]
pub fn enable_debug_tracing() {
    #[cfg(feature = "debug-tracing")]
    {
        use tracing_subscriber::prelude::*;
        let _ = tracing_subscriber::registry()
            .with(tracing_wasm::WASMLayer::new(Default::default()))
            .try_init();
    }
}

/// 改用**内存存储**：不安装持久化 VFS，数据只活在本页面生命周期内。
///
/// 用途：临时/一次性钱包；以及排查时把存储层从纯计算里摘出来 ——
/// 同样的扫描在内存 VFS 上跑一遍，就知道瓶颈在哪一半。
///
/// 必须替代 `init()` 调用，不能两个都调。
#[wasm_bindgen(js_name = diagInitMemoryStorage)]
pub fn init_memory_storage() {
    install_panic_hook();
    storage::use_memory_storage();
}

/// The capabilities of this version-locked wallet and keys package set.
///
/// 存在的理由：发送失败得太晚代价很高 —— 提案已经锁了 note，用户已经输过密码，
/// 却在证明阶段才炸。与其让流水线中途失败，不如让按钮一开始就不出现。
///
#[wasm_bindgen(js_name = capabilities)]
pub fn capabilities() -> String {
    serde_json::json!({
        "prove": {
            "orchard": send::PROVE_ORCHARD,
            "sapling": send::PROVE_SAPLING,
            "ironwood": send::PROVE_IRONWOOD,
        },
        "scan": true,
        "balance": true,
        "history": true,
        // The wallet can create the proposal, the prover package attaches proofs, and the keys
        // package signs transparent inputs. The host must combine all three capabilities.
        "proveShielding": true,
        "notes": {
            // 说明为什么不通，宿主可以据此给用户不同的提示
            "sapling": "Sapling 不在产品支持范围：不扫描、不展示、不收款、不花费；\
    证明参数（约 50MB）也不随包分发",
            "transparent": "透明输入的签名在 keys 包，本包只负责提案与证明；\
    「能否屏蔽」要两边的标志同时为真",
        },
    })
    .to_string()
}

/// 本 runtime 锁定的官方依赖版本，JSON 字符串。
#[wasm_bindgen(js_name = dependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// 诊断：取单个区块的结构摘要。不扫描、不写库、不改任何状态。
///
/// 用来确认浏览器（gRPC-web 代理）与原生（真 gRPC）拿到的是不是同一份数据 ——
/// 两边行为不一致时，先排除「数据本来就不同」再谈计算差异。
#[wasm_bindgen(js_name = diagBlockSummary)]
pub async fn block_summary(height: u32) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |db, mut client| async move {
            let r = network::block_summary(&mut client, height).await;
            (db, client, r)
        })
        .await?,
    )
}

/// 打开（必要时创建）钱包库并把 schema 迁移到当前版本。
///
/// 异步是因为要先把这个库从 IndexedDB 读进 VFS。VFS 装的是 `Preload::None`
/// （不预载该 origin 下的全部库），代价就是必须按名字点一次 —— 漏掉这一步，
/// 已存在的库不会出现在文件表里，SQLite 会当成新库重新建表，
/// 于是每次启动都从 birthday 全量重扫，而扫链看上去一切正常。
#[wasm_bindgen(js_name = openWallet)]
pub async fn open_wallet(network_name: String, db_name: String) -> Result<String, JsValue> {
    let _perf = perf::span("openWallet");
    let (network_name, db_name) = (network_name.as_str(), db_name.as_str());
    storage::preload_database(db_name).await?;
    let db = wallet::open_and_migrate(network_name, db_name)?;
    // 有异步操作在进行时这里会拒绝（WALLET_BUSY）—— 必须传播出去，
    // 否则调用方会以为库已经换了，而实际上还是旧的。
    runtime::set_db(db, db_name)?;
    Ok(serde_json::json!({ "dbName": db_name, "network": network_name }).to_string())
}

/// 关闭钱包，释放数据库与网络客户端。
///
/// 有异步操作在进行时抛 `WALLET_BUSY`：那次操作手上还握着数据库，
/// 此刻关闭只会让它结束后把一个本该消失的钱包放回来。
/// 临时性能打点开关：打开后重导出逐个打耗时到 console（`[PRIV-PERF] rt`）。
#[wasm_bindgen(js_name = setPerfTrace)]
pub fn set_perf_trace(enabled: bool) {
    perf::set_enabled(enabled);
}

#[wasm_bindgen(js_name = closeWallet)]
pub fn close_wallet() -> Result<(), JsValue> {
    Ok(runtime::close()?)
}

/// 配置 lightwalletd 端点，形如 `https://host:port`。
///
/// 端点由宿主决定 —— 本 runtime 不硬编码任何服务器地址。
/// 注意该端点必须支持 gRPC-web 与 CORS，否则浏览器侧会以网络错误失败。
#[wasm_bindgen(js_name = setLightwalletdUrl)]
pub fn set_lightwalletd_url(base_url: &str) -> Result<(), JsValue> {
    runtime::set_client(network::connect(base_url)?);
    Ok(())
}

// ── 链信息 ──────────────────────────────────────────────────────────────

/// 当前链尖高度。**不需要先打开钱包库** —— 只需要先 `setLightwalletdUrl`。
///
/// 建账户时宿主要靠它定 birthday，而那一刻库还不存在；若要求先开库，
/// 这一步必然失败，birthday 就会退回 Sapling 激活高度，新助记词也要全量扫。
#[wasm_bindgen(js_name = chainTip)]
pub async fn chain_tip() -> Result<u32, JsValue> {
    Ok(runtime::with_client(|mut client| async move {
        let r = network::chain_tip(&mut client).await;
        (client, r)
    })
    .await?)
}

/// Fetches the current chain tip with an isolated client.
///
/// Host-side birthday and scheduler probes must not replace the wallet's
/// global client while a sync operation has borrowed it across an await.
#[wasm_bindgen(js_name = chainTipAt)]
pub async fn chain_tip_at(base_url: String) -> Result<u32, JsValue> {
    let mut client = network::connect(&base_url)?;
    Ok(network::chain_tip(&mut client).await?)
}

// ── 账户 ────────────────────────────────────────────────────────────────

/// 导入一个只读账户（UFVK），返回账户 UUID。
///
/// `birthdayHeight` 决定扫描起点。新建账户传当前链尖高度，首次同步几乎不用扫历史；
/// 恢复钱包必须传足够早的高度，否则会漏掉历史资金 —— **这个决策属于宿主**。
#[wasm_bindgen(js_name = importAccountUfvk)]
pub async fn import_account_ufvk(
    account_name: String,
    ufvk: String,
    birthday_height: u32,
) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let r =
                account::import_ufvk(&mut db, &mut client, &account_name, &ufvk, birthday_height)
                    .await;
            (db, client, r)
        })
        .await?,
    )
}

/// 从共享库里移除单个账户。**不删库** —— 库还装着其他钱包的账户。
#[wasm_bindgen(js_name = removeAccount)]
pub async fn remove_account(account_uuid: String) -> Result<(), JsValue> {
    let has_unsettled_outgoing =
        runtime::with_read_conn(|conn| history::has_unsettled_outgoing(conn, &account_uuid))?;
    let has_unresolved_broadcast = runtime::with_read_conn(|conn| {
        tx_state::unresolved_txids(conn, &account_uuid).map(|txids| !txids.is_empty())
    })?;
    let has_active_reservation = send::has_active_reservations(&account_uuid);
    if has_active_reservation || has_unsettled_outgoing || has_unresolved_broadcast {
        return Err(crate::RuntimeError::with(
            crate::ErrorCode::WalletBusy,
            serde_json::json!({
                "operation": "removeAccount",
                "activeReservation": has_active_reservation,
                "unsettledOutgoing": has_unsettled_outgoing,
                "unresolvedBroadcast": has_unresolved_broadcast,
            }),
        )
        .into());
    }
    runtime::with_db(|db| account::remove_account(db, &account_uuid))?;
    storage::durability_barrier().await?;
    Ok(())
}

/// 按 UFVK 定位这个库里的账户，返回 `{ uuid }`，找不到返回 `"null"`。
///
/// 共享库（每 network 一份，装全部钱包的全部账户）下，这是定位账户的唯一
/// 正确方式：不能假设第一个就是要找的那个，UUID 又会在 purge 后变化。
#[wasm_bindgen(js_name = accountByUfvk)]
pub fn account_by_ufvk(ufvk: String) -> Result<String, JsValue> {
    let _perf = perf::span("accountByUfvk");
    Ok(runtime::with_db(|db| account::account_by_ufvk(db, &ufvk))?)
}

/// 每个账户各自的同步完成度，从「该账户 birthday + 全库剩余待扫区间」推导。
///
/// 共享库下全局进度会误导：新加一个账户会让**所有**账户看起来都没同步完。
/// 官方 schema 没有 per-account 游标，也不该为此改 schema（见 D15），
/// 所以这里只读推导，零 schema 改动。
///
/// 返回纯块高，不给百分比 —— 进度比例的分母是移动窗口里的 note 数，
/// 永远到不了 100%，显示出来像坏了。
#[wasm_bindgen(js_name = accountSyncStatus)]
pub fn account_sync_status(account_uuid: String) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| {
        sync::account_status(db, &account_uuid)
    })?)
}

/// 列出本地账户的 UUID，JSON 数组。
#[wasm_bindgen(js_name = listAccounts)]
pub fn list_accounts() -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| account::list_accounts_json(db))?)
}

/// 账户余额，JSON。
///
/// `trusted` / `untrusted` 是确认数门槛（均 >= 1，且 trusted <= untrusted）：
/// 自己找零的 note 可以宽松，外来的应更保守。具体取值是产品决策，由宿主传。
/// 尚未同步到任何区块时抛 `NOT_SYNCED`。
#[wasm_bindgen(js_name = accountBalance)]
pub fn account_balance(
    account_uuid: String,
    trusted: u32,
    untrusted: u32,
    allow_zero_conf_shielding: bool,
) -> Result<String, JsValue> {
    let _perf = perf::span("accountBalance");
    let policy = account::confirmations_policy(trusted, untrusted, allow_zero_conf_shielding)?;
    Ok(runtime::with_db(|db| {
        account::balance_json(db, &account_uuid, policy)
    })?)
}

// ── 扫链 ────────────────────────────────────────────────────────────────

/// 同步前的一次性准备：下载三个池的子树根、更新链尖。
///
/// **这是首次同步最慢的一步**，与 batchSize 无关。单独暴露是为了让宿主
/// 能给出「正在准备」的独立进度，而不是干等一个不返回的调用。
///
/// 返回 `{ chainTip, saplingSubtrees, orchardSubtrees, ironwoodSubtrees }`。
/// 每轮同步开始时调一次即可。
#[wasm_bindgen(js_name = syncPrepare)]
pub async fn sync_prepare() -> Result<String, JsValue> {
    let _perf = perf::span("syncPrepare");
    Ok(
        runtime::with_db_and_client(|mut db, mut client| async move {
            let r = sync::prepare(&mut db, &mut client).await;
            (db, client, r)
        })
        .await?,
    )
}

/// 只更新链尖（一个 unary 调用，主网实测 ~270ms）。返回 `{ chainTip }`。
///
/// 每轮同步用它；子树根走 `syncPrepare`，开库后一次即可 —— 那 1900 条根
/// 是凝固的历史，每轮重下就是主网每轮多出 7 秒的来源。
#[wasm_bindgen(js_name = syncTip)]
pub async fn sync_tip() -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(|mut db, mut client| async move {
            let r = sync::tip(&mut db, &mut client).await;
            (db, client, r)
        })
        .await?,
    )
}

/// 刷新账户的透明 UTXO。**必须单独调**。
///
/// 扫区块只产出屏蔽 note；透明 UTXO 在链上是公开的，要按地址向 lightwalletd 查。
/// 不调的后果是静默的：透明余额恒为 0、屏蔽按钮永远说「没有可屏蔽的钱」，
/// 而扫链看起来一切正常。
#[wasm_bindgen(js_name = syncTransparentUtxos)]
pub async fn sync_transparent_utxos(account_uuid: String) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let r = sync::refresh_transparent_utxos(&mut db, &mut client, &account_uuid).await;
            (db, client, r)
        })
        .await?,
    )
}

/// 只读查看待扫区间，**不发网络请求**。返回
/// `{ ranges: [{ start, end, len, priority }], totalBlocks }`。
///
/// 给宿主做进度条用；同步异常时也用它确认钱包想扫哪一段。
#[wasm_bindgen(js_name = syncPeek)]
pub fn sync_peek() -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| sync::peek(db))?)
}

/// 诊断：只下载不扫描，返回各阶段耗时。**不改变钱包状态。**
///
/// 同步在浏览器里卡住时，网络挂和扫描挂的表现一样（页面无响应），
/// 但成因不同。先用这个确认网络这一半是好的，再去查扫描。
#[wasm_bindgen(js_name = diagProbeDownload)]
pub async fn sync_probe_download(batch_size: u32) -> Result<String, JsValue> {
    Ok(
        runtime::with_db_and_client(move |db, mut client| async move {
            let r = sync::probe_download(&db, &mut client, batch_size).await;
            (db, client, r)
        })
        .await?,
    )
}

/// 只报价、不构造交易、不锁定 note。返回 `{ feeZat, steps }`。
///
/// 与 `pcztCreate` 共用同一条提案路径，所以报出来的费用就是实付的费用。
/// 余额不足同样抛 `INSUFFICIENT_FUNDS`（带 shortfallZat），宿主可据此提示。
#[wasm_bindgen(js_name = pcztQuote)]
pub fn pczt_quote(
    account_uuid: String,
    to_address: String,
    amount_zat: u64,
    memo: Option<String>,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    spend_transparent: Option<bool>,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<String>,
) -> Result<String, JsValue> {
    let pool = send::parse_change_pool(&fallback_change_pool)?;
    // 报价必须和真正发送用同一套选币规则，否则报出来的费用不是实付的费用。
    // spend_source 也在其中：限定池子会换掉被选中的 note，费用随之不同。
    let policy = send::SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: pool,
        pad_orchard_bundle: false,
        spend_transparent: spend_transparent.unwrap_or(false),
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: send::parse_spend_source(spend_source.as_deref())?,
    };
    Ok(runtime::with_db(|db| {
        send::quote(
            db,
            &account_uuid,
            &to_address,
            amount_zat,
            memo.as_deref(),
            policy,
        )
    })?)
}

/// 单笔交易详情，JSON。钱包里没有这笔时返回 `"null"` 而不是报错 ——
/// 「不在钱包里」是正常状态（外部 txid、被回滚的交易），不是故障。
#[wasm_bindgen(js_name = transactionDetails)]
pub fn transaction_details(account_uuid: String, txid: String) -> Result<String, JsValue> {
    let _perf = perf::span("transactionDetails");
    Ok(runtime::with_read_conn(|conn| {
        history::details(conn, &account_uuid, &txid)
    })?)
}

/// 单笔交易的逐笔明细：`{ txid, spent[], received[], external[] }`。
///
/// 与 `transactionDetails` 的区别：那个给的是聚合事实（金额增量、笔数），
/// 这个给的是每一笔输出的明细（金额、池、是否找零、memo、地址）。
/// 交易详情页要后者，列表要前者。
#[wasm_bindgen(js_name = transactionOutputs)]
pub fn transaction_outputs(account_uuid: String, txid: String) -> Result<String, JsValue> {
    Ok(runtime::with_read_conn(|conn| {
        history::details_outputs(conn, &account_uuid, &txid)
    })?)
}

/// 有界推进扫描：最多 `maxBatches` 批，每批 `batchSize` 个区块。
///
/// `priority` 决定推进哪条 lane：`"chainTip"` 追新块、`"historic"` 回填历史、
/// 不传或 `"any"` 则按上游给的顺序。**候选区间只能来自上游的建议**，宿主无法
/// 指定任意高度 —— 调度选择归宿主，扫哪些区间安全仍归协议实现。
///
/// `activeUfvksJson` is a required JSON string array. Only those identities are used for
/// Orchard/Ironwood trial decryption. An empty array fails with `NO_ACTIVE_ACCOUNTS`; it never
/// falls back to every account stored in the runtime database.
///
/// 返回里带 `scannedFrom/scannedTo/scannedPriority`，宿主才能把进度归到正确的 lane。
///
/// **本函数永远不会自己循环到同步完成。** 返回
/// `{ done, batchesDone, blocksScanned, remainingBlocks, remainingRanges }`，
/// 宿主据此显示进度并决定是否继续调 —— 调度归宿主。
#[wasm_bindgen(js_name = syncStep)]
pub async fn sync_step(
    batch_size: u32,
    max_batches: u32,
    priority: Option<String>,
    active_ufvks_json: String,
) -> Result<String, JsValue> {
    let _perf = perf::span("syncStep");
    let preferred = sync::parse_priority(priority.as_deref().unwrap_or(""))?;
    let active_ufvks: Vec<String> = serde_json::from_str(&active_ufvks_json).map_err(|e| {
        RuntimeError::new(ErrorCode::InvalidUfvk).detail(format!("activeUfvksJson: {e}"))
    })?;
    Ok(
        runtime::with_db_and_client(move |mut db, mut client| async move {
            let r = sync::step(
                &mut db,
                &mut client,
                batch_size,
                max_batches,
                preferred,
                &active_ufvks,
            )
            .await;
            (db, client, r)
        })
        .await?,
    )
}

// ── 交易历史 ────────────────────────────────────────────────────────────

/// 分页读取交易历史，JSON 数组。未上链的排最前，其余按高度倒序。
///
/// 返回的是**中性事实**（金额增量、是否找零、note 计数、时间戳），
/// 不含「收入/支出」这类判定，也不含任何文案 —— 展示模型属于宿主。
///
/// `limit` 上限 500，超出抛 `INVALID_BATCH_SIZE`。
#[wasm_bindgen(js_name = transactionHistory)]
pub fn transaction_history(
    account_uuid: String,
    limit: u32,
    offset: u32,
) -> Result<String, JsValue> {
    let _perf = perf::span("transactionHistory");
    Ok(runtime::with_read_conn(|conn| {
        history::list(conn, &account_uuid, limit, offset)
    })?)
}

// ── 发送 ────────────────────────────────────────────────────────────────

/// 回退钱包到指定高度，让之后的同步重扫这段区间。
///
/// **破坏性**：该高度之上的扫描结果会被丢弃。返回实际回退到的高度
/// （上游可能因承诺树检查点位置退得更靠前）。
#[wasm_bindgen(js_name = rewindTo)]
pub fn rewind_to(height: u32) -> Result<u32, JsValue> {
    Ok(runtime::with_db(|db| sync::rewind_to(db, height))?)
}

/// Re-queue `[fromHeight, chainTip]` as historic work without deleting cached notes or history.
///
/// This is the resume companion to `syncStep(activeUfvksJson)`: the host persists each privacy
/// identity's pause cursor, applies its reorg margin, queues the missing range, and only then adds
/// the identity back to the active UFVK set.
#[wasm_bindgen(js_name = queueRescanFrom)]
pub fn queue_rescan_from(from_height: u32) -> Result<String, JsValue> {
    Ok(runtime::with_db(|db| {
        sync::queue_rescan_from(db, from_height)
    })?)
}

// ── 发送 ────────────────────────────────────────────────────────────────
//
// 完整流程：pcztCreate → pcztProve → （keys 包）pcztSign → pcztSend
//
// 证明在签名之前还是之后都可以（两者作用于 PCZT 的不同部分），
// 但**都必须在 pcztSend 之前完成**，否则交易无效。

/// 构造一笔转账的 PCZT（未证明、未签名），返回字节。
///
/// `fallbackChangePool`: 当前产品固定传 `"ironwood"`，交易找零进入最新隐私池。
/// `padOrchardBundle`: 是否填充 Orchard bundle 隐藏真实 action 数（更大、费更高）。
#[wasm_bindgen(js_name = pcztCreate)]
pub fn pczt_create(
    account_uuid: String,
    to_address: String,
    amount_zat: u64,
    memo: Option<String>,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    pad_orchard_bundle: bool,
    lock_for_blocks: u32,
    reservation_id: Option<String>,
    spend_transparent: Option<bool>,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<String>,
) -> Result<String, JsValue> {
    let pool = send::parse_change_pool(&fallback_change_pool)?;
    let policy = send::SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: pool,
        pad_orchard_bundle,
        spend_transparent: spend_transparent.unwrap_or(false),
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: send::parse_spend_source(spend_source.as_deref())?,
    };
    Ok(runtime::with_db(|db| {
        let (bytes, reservation_id, fee_zat) = send::create_pczt(
            db,
            &account_uuid,
            &to_address,
            amount_zat,
            memo.as_deref(),
            policy,
            lock_for_blocks,
            reservation_id.as_deref(),
        )?;
        Ok(serde_json::json!({
            "pcztHex": bytes.iter().map(|b| format!("{b:02x}")).collect::<String>(),
            "reservationId": reservation_id,
            "feeZat": fee_zat,
        })
        .to_string())
    })?)
}

/// 生成 Orchard / Ironwood 零知识证明。**必须在 pcztSend 之前做**。
#[wasm_bindgen(js_name = pcztProve)]
pub fn pczt_prove(pczt_bytes: Vec<u8>) -> Result<Vec<u8>, JsValue> {
    Ok(runtime::with_db(|db| send::prove_pczt(db, &pczt_bytes))?)
}

/// 构造「屏蔽全部透明余额」的 PCZT，返回 `{ pcztHex, reservationId }`。
///
/// 没有收款地址、没有金额：收款方是本账户自己的屏蔽地址，金额是全部透明余额。
/// 之后的 prove / sign / send / broadcast 与普通转账完全一样。
///
/// `shieldingThresholdZat` 是宿主策略：透明余额低于它就不值得屏蔽（手续费可能
/// 比金额还高）。低于阈值时抛 `INSUFFICIENT_FUNDS`。
#[wasm_bindgen(js_name = pcztShield)]
pub fn pczt_shield(
    account_uuid: String,
    shielding_threshold_zat: u64,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    pad_orchard_bundle: bool,
    lock_for_blocks: u32,
    reservation_id: Option<String>,
    allow_zero_conf_shielding: Option<bool>,
) -> Result<String, JsValue> {
    let pool = send::parse_change_pool(&fallback_change_pool)?;
    let policy = send::SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: pool,
        pad_orchard_bundle,
        // 屏蔽按定义就是花透明输入。这里不读这个字段（屏蔽路径有自己的选币器），
        // 写 true 是为了字段语义诚实，将来谁去读它也拿到对的值。
        spend_transparent: true,
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        // 同上：屏蔽不从屏蔽池取 note，源池子这个概念在这条路径上不适用。
        spend_source: None,
    };
    Ok(runtime::with_db(|db| {
        let (bytes, reservation_id, fee_zat) = send::create_shielding_pczt(
            db,
            &account_uuid,
            shielding_threshold_zat,
            policy,
            lock_for_blocks,
            reservation_id.as_deref(),
        )?;
        Ok(serde_json::json!({
            "pcztHex": bytes.iter().map(|b| format!("{b:02x}")).collect::<String>(),
            "reservationId": reservation_id,
            "feeZat": fee_zat,
        })
        .to_string())
    })?)
}

/// Exact shielding fee from the same proposal path as `pcztShield`, without
/// locking inputs or constructing a transaction.
#[wasm_bindgen(js_name = pcztShieldQuote)]
pub fn pczt_shield_quote(
    account_uuid: String,
    shielding_threshold_zat: u64,
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: String,
    pad_orchard_bundle: bool,
    allow_zero_conf_shielding: Option<bool>,
) -> Result<String, JsValue> {
    let pool = send::parse_change_pool(&fallback_change_pool)?;
    let policy = send::SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: pool,
        pad_orchard_bundle,
        spend_transparent: true,
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: None,
    };
    Ok(runtime::with_db(|db| {
        let fee_zat = send::quote_shielding(db, &account_uuid, shielding_threshold_zat, policy)?;
        Ok(serde_json::json!({ "feeZat": fee_zat }).to_string())
    })?)
}

/// 释放该账户**全部**被锁的 note，返回解锁数量。
///
/// 崩溃恢复用：`releaseReservation` 靠内存里的提案表定位，app / offscreen 重启后
/// 那张表没了，旧 token 既解不开也不知道锁了什么。
///
/// **会连带放开进行中的提案**，所以只能由用户主动触发的「修复」动作调用，
/// 不要放进正常流程。
#[wasm_bindgen(js_name = clearLockedOutputs)]
pub fn clear_locked_outputs(account_uuid: String) -> Result<u32, JsValue> {
    Ok(runtime::with_db(|db| {
        send::clear_locked_outputs(db, &account_uuid)
    })?)
}

/// 释放一次 reservation 占用的 note（用户取消发送时调）。
///
/// 幂等：id 已发送 / 已释放 / 从未存在时返回 `false` 而不报错 ——
/// 取消路径上重复调用是常态，不该因此给用户弹错误。
#[wasm_bindgen(js_name = releaseReservation)]
pub fn release_reservation(reservation_id: String) -> Result<bool, JsValue> {
    Ok(runtime::with_db(|db| {
        send::release_reservation(db, &reservation_id)
    })?)
}

/// 终结已证明且已签名的 PCZT，提取交易并**写回本地钱包**，返回 txid。
///
/// **这一步不发网络。** 交易还没有广播出去，必须再调 `broadcastTransaction(txid)`。
/// 两步分开，是为了让宿主能在落库之后、真正发出去之前插入确认环节，
/// 也能在广播失败时原样重试而不必重建交易。
#[wasm_bindgen(js_name = pcztSend)]
pub async fn pczt_send(
    account_uuid: String,
    pczt_bytes: Vec<u8>,
    reservation_id: String,
) -> Result<String, JsValue> {
    let txid = runtime::with_db(|db| {
        send::extract_and_store(db, &account_uuid, &pczt_bytes, &reservation_id)
    })?;
    // Once extract_and_store commits, the transaction itself owns the spent
    // notes. Drop the proposal before the durability barrier: a barrier error
    // means the host does not know whether persistence reached durable storage,
    // but it must never be allowed to unlock inputs that may already be spent.
    send::forget_reservation(&reservation_id);
    storage::durability_barrier().await.map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::FinalizeOutcomeUnknown,
            serde_json::json!({ "operation": "finalizeTransaction" }),
        )
        .detail(error)
    })?;
    Ok(txid)
}

/// Runtime-owned unresolved broadcast intents for this account. The host uses
/// this only to fail closed before constructing another payment; it does not
/// persist or reinterpret the returned transaction IDs.
#[wasm_bindgen(js_name = pendingBroadcastTxids)]
pub fn pending_broadcast_txids(account_uuid: String) -> Result<String, JsValue> {
    let txids = runtime::with_read_conn(|conn| tx_state::unresolved_txids(conn, &account_uuid))?;
    Ok(serde_json::to_string(&txids).map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::DatabaseError,
            serde_json::json!({ "operation": "serializePendingBroadcasts" }),
        )
        .detail(error)
    })?)
}

#[wasm_bindgen(js_name = broadcastRetryTxids)]
pub fn rebroadcastable_txids(account_uuid: String) -> Result<String, JsValue> {
    let txids =
        runtime::with_read_conn(|conn| tx_state::rebroadcastable_txids(conn, &account_uuid))?;
    Ok(serde_json::to_string(&txids).map_err(|error| {
        crate::RuntimeError::with(
            crate::ErrorCode::DatabaseError,
            serde_json::json!({ "operation": "serializeRebroadcastableTransactions" }),
        )
        .detail(error)
    })?)
}

/// 把 `pcztSend` 落库的交易广播到 lightwalletd。**不可逆。**
///
/// 节点拒绝抛 `BROADCAST_REJECTED`（不要重试，先查原因）；
/// 网络本身出错抛 `NETWORK_ERROR`（可以重试，交易已在本地库里）。
#[wasm_bindgen(js_name = broadcastTransaction)]
pub async fn broadcast_transaction(txid: String) -> Result<(), JsValue> {
    let result = runtime::with_db_and_client(move |mut db, mut client| async move {
        let r = match tx_state::assert_broadcastable(&mut db, &txid)
            .and_then(|_| send::raw_transaction(&db, &txid))
        {
            Ok(raw) => network::broadcast(&mut client, raw).await,
            Err(e) => Err(e),
        };
        let r = match r {
            Ok(()) => tx_state::mark_accepted(&mut db, &txid),
            Err(e) if e.code == crate::ErrorCode::BroadcastRejected => {
                match tx_state::mark_rejected(&mut db, &txid, &e) {
                    Ok(()) => Err(e),
                    Err(persist_error) => Err(persist_error),
                }
            }
            Err(e) => Err(e),
        };
        (db, client, r)
    })
    .await;
    let changed_lifecycle = result.is_ok()
        || result
            .as_ref()
            .is_err_and(|error| error.code == crate::ErrorCode::BroadcastRejected);
    if changed_lifecycle {
        storage::durability_barrier().await?;
    }
    Ok(result?)
}

// ── 自检 ────────────────────────────────────────────────────────────────

/// 删除一个钱包库（连同 IndexedDB 里的字节）。**不可逆。**
///
/// 库是纯派生缓存：UFVK 与 birthday 由宿主保管，不在库里，所以删掉只是要重扫。
/// 用途一是出问题时手动重置，用途二是删账户时清掉已解密的屏蔽历史。
///
/// 若删的正是当前打开的库，会先自动关闭 —— 底层要求删除前库必须已关闭。
/// 返回 `true` 表示确实删掉了，`false` 表示本来就不存在（不算错误）。
#[wasm_bindgen(js_name = deleteWallet)]
pub async fn delete_wallet(db_name: String) -> Result<bool, JsValue> {
    if runtime::open_db_name().as_deref() == Some(db_name.as_str()) {
        runtime::close()?;
    }
    Ok(storage::delete_database(&db_name).await?)
}

/// 当前钱包库的用量，按数据语义分组。**只读，不改任何数据。**
///
/// 这是**稳定契约**，不是 `diag*` —— 宿主要靠它做容量守卫（比如超过软阈值就
/// 停掉 birthday 回填），那属于产品行为，不能建立在随时会消失的排障接口上。
///
/// `logicalBytes` 是页数 × 页大小，**不等于 IndexedDB 的物理占用**：那边有写
/// 放大、旧版本页和异步刷盘。两个数不该互相外推。
///
/// 刻意不提供「执行任意 SQL」：一条写错的语句就能删掉仍待证明的 anchor。
#[wasm_bindgen(js_name = storageStats)]
pub fn storage_stats() -> Result<String, JsValue> {
    let db_name = runtime::open_db_name()
        .ok_or_else(|| JsValue::from(crate::RuntimeError::new(crate::ErrorCode::WalletNotOpen)))?;
    let conn = storage::open_connection(&db_name)?;
    Ok(crate::stats::storage_stats(&conn)?)
}

/// Diagnostic: checks for persisted bytes without opening or creating a database.
#[wasm_bindgen(js_name = diagDatabaseExists)]
pub async fn database_exists(db_name: &str) -> Result<bool, JsValue> {
    Ok(storage::database_exists(db_name).await?)
}

/// Diagnostic: runs bounded, read-only consistency checks on the open database.
#[wasm_bindgen(js_name = diagDatabaseHealth)]
pub fn database_health() -> Result<String, JsValue> {
    Ok(runtime::with_read_conn(crate::stats::database_health)?)
}

/// 诊断：数一份 PCZT 里各 bundle 有多少输入。
///
/// 用来回答「这笔钱是从哪个池出去的」——费用差只能间接推断，输入个数是直接证据。
/// 判断 `spendTransparent` 会不会让一笔本可全屏蔽的交易混进公开输入，靠的就是它。
#[wasm_bindgen(js_name = diagPcztInputCounts)]
pub fn pczt_input_counts(pczt_bytes: Vec<u8>) -> Result<String, JsValue> {
    let pczt = pczt::Pczt::parse(&pczt_bytes).map_err(|e| {
        JsValue::from(
            crate::RuntimeError::new(crate::ErrorCode::PcztError).detail(format!("{e:?}")),
        )
    })?;
    // action 个数单独看会骗人：Orchard/Ironwood 的 action 是「花费+输出」成对的，
    // 而且 bundle 会补 dummy 凑到最少 2 个。所以「有 2 个 action」既可能是真花了
    // 屏蔽 note，也可能只是找零输出加一个 dummy。
    //
    // `valueSum` = 该池的花费减输出（负号单独给出）。为负说明净流入这个池
    // （只收了找零），为正才说明真的花了这个池里的 note。
    let sum = |b: &pczt::orchard::Bundle| {
        let (magnitude, negative) = *b.value_sum();
        serde_json::json!({ "magnitude": magnitude, "negative": negative })
    };
    // 透明输入的**金额**才是判断有没有混合的可靠依据：
    // 屏蔽花费额 = 付款额 + 手续费 − 透明输入额。`valueSum` 只给净流向，
    // 净流入为负并不等于零花费 —— 一笔交易完全可以既有真实屏蔽花费又净流入。
    let transparent_value: u64 = pczt.transparent().inputs().iter().map(|i| *i.value()).sum();

    Ok(serde_json::json!({
        "transparent": pczt.transparent().inputs().len(),
        "transparentValue": transparent_value,
        "sapling": pczt.sapling().spends().len(),
        "orchard": pczt.orchard().actions().len(),
        "ironwood": pczt.ironwood().actions().len(),
        "orchardValueSum": sum(pczt.orchard()),
        "ironwoodValueSum": sum(pczt.ironwood()),
    })
    .to_string())
}

/// 自检：打开库、跑完迁移，回报建出来的表 / 视图 / 索引数量。
#[wasm_bindgen(js_name = diagSelfCheck)]
pub fn self_check(network_name: &str, db_name: &str) -> Result<String, JsValue> {
    drop(wallet::open_and_migrate(network_name, db_name)?);
    let conn = storage::open_connection(db_name)?;

    let count_of = |kind: &str| -> Result<i64, JsValue> {
        conn.query_row(
            "SELECT COUNT(*) FROM sqlite_master WHERE type = ?1",
            [kind],
            |r| r.get(0),
        )
        .map_err(|e| JsValue::from(crate::RuntimeError::from(e)))
    };

    Ok(serde_json::json!({
        "tables": count_of("table")?,
        "views": count_of("view")?,
        "indexes": count_of("index")?,
        "versions": crate::dependency_versions(),
    })
    .to_string())
}
