//! 面向宿主的 wasm 接口。
//!
//! ## 形状约定
//!
//! - 返回值一律是 **JSON 字符串**，宿主 `JSON.parse` 即可。错误也是 JSON
//!   （`{ code, params, detail }`），所以宿主不必区分 Error 与非 Error 两条路。
//! - **金额一律用字符串**。piconero 的上限远超 JS 的 `Number.MAX_SAFE_INTEGER`，
//!   走数字会静默丢精度 —— 那是最坏的一类 bug，因为它不报错。
//! - 密钥材料**只进不出**：种子与花费私钥可以传进来，绝不作为返回值出去。
//!
//! ## 调度不在这里
//!
//! 什么时候扫、扫多少、失败怎么退避 —— 全归宿主。这里每个导出都是一次
//! 完整的往返，绝不自己循环。

use wasm_bindgen::prelude::*;

use crate::error::{ErrorCode, Result, RuntimeError};
use zeroize::Zeroizing;

use crate::{keys, network, query, runtime, spend, storage, sync, wallet};

fn json(value: serde_json::Value) -> std::result::Result<String, JsValue> {
    Ok(value.to_string())
}

/// 装上持久化存储。**必须在开任何库之前调用一次。**
#[wasm_bindgen(js_name = init)]
pub async fn init() -> std::result::Result<(), JsValue> {
    storage::install_persistent_vfs().await?;
    Ok(())
}

/// 本 runtime 锁定的上游版本，供宿主自检与日志使用。
#[wasm_bindgen(js_name = dependencyVersions)]
pub fn dependency_versions() -> String {
    crate::dependency_versions().to_string()
}

/// 打开（或新建）一个钱包库。
#[wasm_bindgen(js_name = openWallet)]
pub fn open_wallet(db_name: &str, network: &str) -> std::result::Result<String, JsValue> {
    let net = keys::parse_network(network)?;
    let db = wallet::Db::open(db_name, net)?;
    runtime::set_db(db, db_name)?;
    json(serde_json::json!({ "dbName": db_name, "network": network }))
}

#[wasm_bindgen(js_name = closeWallet)]
pub fn close_wallet() -> std::result::Result<(), JsValue> {
    runtime::close()?;
    Ok(())
}

/// 配置节点地址，空串表示停用。**没有内置默认值** —— 内置一个等于替用户决定
/// 把 IP 和查询交给谁，那是隐私决策，不该藏在 runtime 里。
///
/// 先断开旧节点再连新的：用户换掉或停用一个节点后，它不能再收到任何请求，
/// 哪怕新节点连不上 —— 那时就是没有节点。
#[wasm_bindgen(js_name = setNodeUrl)]
pub async fn set_node_url(url: &str) -> std::result::Result<(), JsValue> {
    runtime::clear_daemon();
    network::forget_node_url();
    if url.trim().is_empty() {
        return Ok(());
    }
    let daemon = network::connect(url).await?;
    runtime::set_daemon(daemon);
    Ok(())
}

/// 问链尖。**不要求钱包库已打开** —— 建账户时要用它定 birthday，
/// 而那一刻库还没建。
#[wasm_bindgen(js_name = chainTip)]
pub async fn chain_tip() -> std::result::Result<String, JsValue> {
    let tip: u64 = runtime::with_daemon(|daemon| async move {
        let r = sync::chain_tip(&daemon).await;
        (daemon, r)
    })
    .await?;
    json(serde_json::json!({ "height": tip }))
}

/// 注册一个身份（观察密钥对）。
///
/// `spendPublicKeyHex` 与 `viewPrivateKeyHex` 都是 32 字节的十六进制。
/// **花费私钥不在这里** —— 它只在签名时传入，见 `signAndBroadcast`。
#[wasm_bindgen(js_name = addAccount)]
pub fn add_account(
    uuid: &str,
    spend_public_key_hex: &str,
    view_private_key_hex: &str,
    birthday_height: u64,
) -> std::result::Result<String, JsValue> {
    let spend = decode_hex(spend_public_key_hex, "spendPublicKeyHex")?;
    let view = Zeroizing::new(decode_hex(view_private_key_hex, "viewPrivateKeyHex")?);
    let result = runtime::with_db(|db| {
        let identity = db.add_identity(uuid, &spend, &view, birthday_height)?;
        Ok(serde_json::json!({
            "accountId": identity.id,
            "uuid": identity.uuid,
            "primaryAddress": identity.primary_address,
            "birthdayHeight": identity.birthday_height,
        }))
    })?;
    json(result)
}

#[wasm_bindgen(js_name = listAccounts)]
pub fn list_accounts() -> std::result::Result<String, JsValue> {
    let rows = runtime::with_db_read(|db| db.list_identities())?;
    let items: Vec<_> = rows
        .into_iter()
        .map(|(id, uuid, address)| {
            serde_json::json!({ "accountId": id, "uuid": uuid, "primaryAddress": address })
        })
        .collect();
    json(serde_json::json!({ "items": items }))
}

/// 删除一个身份连同它的全部数据。**不可逆。**
#[wasm_bindgen(js_name = removeAccount)]
pub fn remove_account(uuid: &str) -> std::result::Result<String, JsValue> {
    let removed = runtime::with_db(|db| db.remove_identity(uuid))?;
    json(serde_json::json!({ "removed": removed }))
}

/// 删除整个钱包库文件（app 重置、存储重建）。**不可逆。**
///
/// 不打开库：版本不认或已损坏的库也删得掉。删的是当前打开的库时先关掉它。
#[wasm_bindgen(js_name = deleteWallet)]
pub async fn delete_wallet(db_name: String) -> std::result::Result<String, JsValue> {
    if runtime::open_db_name().as_deref() == Some(db_name.as_str()) {
        runtime::close()?;
    }
    let deleted = storage::delete_database(&db_name).await?;
    json(serde_json::json!({ "deleted": deleted }))
}

/// 登记一个子地址并返回它的地址串。**没登记的地址收到的钱扫不到。**
#[wasm_bindgen(js_name = registerSubaddress)]
pub fn register_subaddress(
    account_id: i64,
    major: u32,
    minor: u32,
    label: Option<String>,
) -> std::result::Result<String, JsValue> {
    let address =
        runtime::with_db(|db| db.register_subaddress(account_id, major, minor, label.as_deref()))?;
    json(serde_json::json!({ "address": address, "major": major, "minor": minor }))
}

/// 把链尖告诉某个身份，据此排出待扫区间。
#[wasm_bindgen(js_name = updateChainTip)]
pub fn update_chain_tip(account_id: i64, tip: u64) -> std::result::Result<String, JsValue> {
    runtime::with_db(|db| sync::update_chain_tip(db, account_id, tip))?;
    let progress = runtime::with_db_read(|db| sync::peek(db, account_id))?;
    json(progress)
}

/// 一次有界推进：最多扫 `maxBlocks` 个块然后带进度返回。
///
/// **本函数永远不会自己循环到同步完成。** 宿主拿到进度后自己决定是否再调。
#[wasm_bindgen(js_name = syncStep)]
pub async fn sync_step(account_id: i64, max_blocks: u64) -> std::result::Result<String, JsValue> {
    let progress = runtime::with_db_and_daemon(|mut db, daemon| async move {
        let r = sync::step(&mut db, &daemon, account_id, max_blocks).await;
        (db, daemon, r)
    })
    .await?;
    json(progress)
}

/// 只读：当前扫描进度，不发网络请求。同步卡住时用它确认到底想扫哪一段。
#[wasm_bindgen(js_name = syncPeek)]
pub fn sync_peek(account_id: i64) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| sync::peek(db, account_id))?;
    json(v)
}

/// 重组恢复：回退到指定高度并把那之上的区间重新排队。**破坏性。**
#[wasm_bindgen(js_name = rewindTo)]
pub fn rewind_to(account_id: i64, height: u64, tip: u64) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db(|db| sync::rewind_and_requeue(db, account_id, height, tip))?;
    json(v)
}

/// 余额。`chainTip` 传 `null` 表示还不知道链尖 —— 此时可用余额一律按 0 报，
/// 宁可显示 0，也不要让用户去花一笔构不进环的钱。
#[wasm_bindgen(js_name = balance)]
pub fn balance(account_id: i64, chain_tip: Option<u64>) -> std::result::Result<String, JsValue> {
    let b = runtime::with_db_read(|db| query::balance(db, account_id, chain_tip))?;
    json(b.to_json())
}

#[wasm_bindgen(js_name = history)]
pub fn history(account_id: i64, limit: u32, offset: u32) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| query::history(db, account_id, limit, offset))?;
    json(v)
}

#[wasm_bindgen(js_name = transactionDetails)]
pub fn transaction_details(
    account_id: i64,
    txid_hex: &str,
) -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| query::transaction_details(db, account_id, txid_hex))?;
    json(v)
}

/// 补齐 key image，让花费检测能工作。
///
/// **必须在解锁时尽早调用。** 补齐之前余额会偏高：已经被花掉的输出看起来还在，
/// 因为观察钱包在协议上无法独立判断花费。补齐只影响之后扫的块 —— 之前发生的
/// 花费要靠回退重扫才能识别。
#[wasm_bindgen(js_name = importKeyImages)]
pub fn import_key_images(
    account_id: i64,
    spend_private_key_hex: &str,
) -> std::result::Result<String, JsValue> {
    let spend = Zeroizing::new(decode_hex(spend_private_key_hex, "spendPrivateKeyHex")?);
    let result = runtime::with_db(|db| spend::import_key_images(db, account_id, &spend))?;
    json(serde_json::json!({
        "imported": result.imported,
        "spendsFound": result.spends_found,
    }))
}

/// 从 BIP-32 派生出来的私钥造出 Monero 的观察材料与主地址。
///
/// **不需要开库、不需要节点** —— 纯派生。这正是「地址可以先建、扫链之后再
/// 手动打开」那条产品路径的基础。
///
/// 约定与 OneKey V4 一致（`spend = sc_reduce32(key)`、`view = keccak(spend)`）。
/// 换约定会让老用户导入同一套助记词后看到空钱包。
///
/// **不返回花费私钥** —— 它就是调用方传进来的那个，没必要再跨一次边界。
#[wasm_bindgen(js_name = deriveAccount)]
pub fn derive_account(
    network: &str,
    bip32_private_key_hex: &str,
) -> std::result::Result<String, JsValue> {
    let net = keys::parse_network(network)?;
    let raw = Zeroizing::new(decode_hex(bip32_private_key_hex, "bip32PrivateKeyHex")?);
    let derived = keys::derive_from_bip32_key(net, &raw)?;
    json(serde_json::json!({
        "primaryAddress": derived.primary_address,
        "spendPublicKeyHex": hex::encode(derived.spend_public_key),
        "viewPrivateKeyHex": hex::encode(derived.view_private_key),
    }))
}

/// 从 BIP-32 私钥算出签名用的花费标量。与 `deriveAccount` 同一个约简。
#[wasm_bindgen(js_name = spendScalar)]
pub fn spend_scalar(bip32_private_key_hex: &str) -> std::result::Result<String, JsValue> {
    let raw = Zeroizing::new(decode_hex(bip32_private_key_hex, "bip32PrivateKeyHex")?);
    let scalar = Zeroizing::new(keys::spend_scalar_from_bip32_key(&raw)?);
    Ok(hex::encode(*scalar))
}

/// 派生一个子地址。纯派生，不开库。
#[wasm_bindgen(js_name = deriveSubaddress)]
pub fn derive_subaddress(
    network: &str,
    spend_public_key_hex: &str,
    view_private_key_hex: &str,
    major: u32,
    minor: u32,
) -> std::result::Result<String, JsValue> {
    let net = keys::parse_network(network)?;
    let spend = decode_hex(spend_public_key_hex, "spendPublicKeyHex")?;
    let view = Zeroizing::new(decode_hex(view_private_key_hex, "viewPrivateKeyHex")?);
    let pair = keys::view_pair_from_bytes(&spend, &view)?;
    let address = keys::subaddress(&pair, net, major, minor)?;
    json(serde_json::json!({ "address": address, "major": major, "minor": minor }))
}

/// 校验一个收款地址是否合法且属于该网络。
#[wasm_bindgen(js_name = validateAddress)]
pub fn validate_address(network: &str, address: &str) -> std::result::Result<bool, JsValue> {
    let net = keys::parse_network(network)?;
    Ok(keys::validate_address(net, address))
}

/// 宿主签好名后调用：之后的准备发送不会把这笔预留当成被放弃的。
#[wasm_bindgen(js_name = markReservationSigned)]
pub fn mark_reservation_signed(reservation_id: &str) -> std::result::Result<String, JsValue> {
    let n = runtime::with_db(|db| spend::mark_signed(db, reservation_id))?;
    json(serde_json::json!({ "marked": n }))
}

/// 释放一次预留（用户取消，或构造失败）。
#[wasm_bindgen(js_name = releaseReservation)]
pub fn release_reservation(reservation_id: &str) -> std::result::Result<String, JsValue> {
    let n = runtime::with_db(|db| spend::release(db, reservation_id))?;
    json(serde_json::json!({ "released": n }))
}

/// 把网络请求交给宿主去发，而不是在 wasm 里 `fetch`。
///
/// `handler` 形如 `(url, contentType, body: Uint8Array) => Promise<Uint8Array>`。
/// 传 `undefined` 卸载。
///
/// wasm 里的 fetch 受浏览器的混合内容与 CORS 管，而 monerod 默认不发 CORS 头
/// —— 2026-09-22 实测 12 家公共节点只有 1 家可用。把请求交回宿主，插件的
/// background、桌面主进程、移动端原生都不受那两条规则管，于是全网节点可用。
///
/// 和性能无关：纯扫描只占总耗时 0.4%，受限的是可达性不是速度。
#[wasm_bindgen(js_name = setRequestHandler)]
pub fn set_request_handler(handler: Option<js_sys::Function>) {
    crate::network::set_host_handler(handler);
}

/// 诊断：库是否健在、各表行数、逻辑大小。**不发网络请求。**
#[wasm_bindgen(js_name = diagDatabase)]
pub fn diag_database() -> std::result::Result<String, JsValue> {
    let v = runtime::with_db_read(|db| {
        let mut counts = serde_json::Map::new();
        for table in [
            "identities",
            "subaddresses",
            "outputs",
            "transactions",
            "blocks",
            "scan_ranges",
            "locked_outputs",
            "possible_spends",
        ] {
            let n: i64 = db
                .conn()
                .query_row(&format!("SELECT count(*) FROM {table}"), [], |r| r.get(0))
                .map_err(|e| crate::error::db_err("diagCount", e))?;
            counts.insert(table.to_owned(), serde_json::json!(n));
        }
        // Logical size (pages x page size), as the Zcash runtime reports it.
        // Works through any VFS; the backing store's physical footprint can
        // differ (free pages, storage-layer overhead).
        let pragma = |name: &str| -> Result<i64> {
            db.conn()
                .query_row(&format!("PRAGMA {name}"), [], |r| r.get(0))
                .map_err(|e| crate::error::db_err("diagPragma", e))
        };
        let page_size = pragma("page_size")?;
        let page_count = pragma("page_count")?;
        Ok(serde_json::json!({
            "dbName": runtime::open_db_name(),
            "counts": counts,
            "pageSize": page_size,
            "pageCount": page_count,
            "logicalBytes": page_size * page_count,
        }))
    })?;
    json(v)
}

fn decode_hex(value: &str, field: &str) -> Result<Vec<u8>> {
    hex::decode(value).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            serde_json::json!({ "field": field, "reason": "notHex" }),
        )
    })
}

// ── 花费路径 ────────────────────────────────────────────────────────────

/// 选币、构造、预留。**不需要花费私钥，不广播。**
///
/// `recipientsJson` 形如 `[{ "address": "...", "amountAtomic": "1000000000000" }]`。
///
/// `maxSend` 为真时只接受一个收款方，金额由「当前可花总额 − 手续费」反算，
/// 传入的金额被忽略 —— 不能靠「不给找零」实现，那样余数会全部并进手续费。
///
/// 返回 `{ reservationId, signableHex, feeAtomic, amountAtomic, changeAtomic,
/// inputsAtomic, inputCount }`。`feeAtomic` 和 `amountAtomic` 是这笔交易真正
/// 会用的数：确认页显示它们，签名时不会再变。
///
/// 失败不会留下预留。成功后必须走 `signTransaction` + `broadcastTransaction`，
/// 或者显式 `releaseReservation` —— 否则那些输出会一直锁着。
#[wasm_bindgen(js_name = prepareTransaction)]
pub async fn prepare_transaction(
    account_id: i64,
    recipients_json: &str,
    max_send: bool,
    reservation_id: &str,
    chain_tip: u64,
    priority: &str,
) -> std::result::Result<String, JsValue> {
    let recipients: Vec<RecipientJson> = serde_json::from_str(recipients_json).map_err(|e| {
        JsValue::from(
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                serde_json::json!({ "field": "recipientsJson" }),
            )
            .detail(e),
        )
    })?;
    if recipients.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            serde_json::json!({ "field": "recipientsJson", "reason": "empty" }),
        )
        .into());
    }

    let fee_priority = spend::parse_priority(priority)?;
    let reservation = reservation_id.to_owned();

    let v = runtime::with_db_and_daemon(move |mut db, daemon| async move {
        let r = spend::prepare_for_host(
            &mut db,
            &daemon,
            account_id,
            &recipients
                .iter()
                .map(|r| (r.address.as_str(), r.amount_atomic.as_str()))
                .collect::<Vec<_>>(),
            max_send,
            &reservation,
            chain_tip,
            fee_priority,
        )
        .await;
        (db, daemon, r)
    })
    .await?;

    json(v)
}

/// 签名一笔已构造的交易。**不碰网络、不碰数据库、不广播。**
///
/// `spendPrivateKeyHex` 用完即擦，绝不落库、绝不返回。
/// 返回 `{ txid, rawHex, sizeBytes }` —— txid 在签名这一刻就定下来了，
/// 宿主据此在广播之前就能建立待确认记录。
#[wasm_bindgen(js_name = signTransaction)]
pub fn sign_transaction(
    signable_hex: &str,
    spend_private_key_hex: &str,
) -> std::result::Result<String, JsValue> {
    let spend = Zeroizing::new(decode_hex(spend_private_key_hex, "spendPrivateKeyHex")?);
    json(spend::sign_for_host(signable_hex, &spend)?)
}

/// 广播一笔已签名的交易。**不可逆。**
///
/// 节点接受或结果不确定（网络错误）都记账：被花的输出标记掉，交易进在途，
/// 返回 `broadcast: accepted | unknown`。只有节点明确拒收才报错并放回输入。
#[wasm_bindgen(js_name = broadcastTransaction)]
pub async fn broadcast_transaction(
    account_id: i64,
    raw_hex: &str,
    reservation_id: &str,
    chain_tip: u64,
    outgoing_atomic: Option<String>,
    change_atomic: Option<String>,
) -> std::result::Result<String, JsValue> {
    let raw = raw_hex.to_owned();
    let reservation = reservation_id.to_owned();
    // 十进制串：u64 过不了宿主桥。只用于显示与余额，格式不对就当没给。
    let amounts = spend::SentAmounts {
        outgoing_atomic: outgoing_atomic.and_then(|a| a.parse::<u64>().ok()),
        change_atomic: change_atomic.and_then(|a| a.parse::<u64>().ok()),
    };
    let v = runtime::with_db_and_daemon(move |db, daemon| async move {
        let r = spend::broadcast_signed(
            &db,
            &daemon,
            account_id,
            &raw,
            &reservation,
            chain_tip,
            amounts,
        )
        .await;
        (db, daemon, r)
    })
    .await?;
    json(v)
}

#[derive(serde::Deserialize)]
struct RecipientJson {
    address: String,
    #[serde(rename = "amountAtomic")]
    amount_atomic: String,
}
