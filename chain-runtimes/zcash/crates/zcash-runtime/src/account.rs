//! 账户导入与读取。
//!
//! 只做「看账」所需：导入 UFVK、读余额。私钥、地址派生与签名不在这条路径上。

use zcash_client_backend::data_api::wallet::ConfirmationsPolicy;
use zcash_client_backend::data_api::{
    Account as _, AccountBirthday, AccountPurpose, WalletRead, WalletWrite,
};
use zcash_keys::keys::UnifiedFullViewingKey;
use zcash_protocol::consensus::BlockHeight;

use crate::error::{ErrorCode, Result, RuntimeError};
use crate::network::{self, LightClient};
use crate::wallet::Db;
use serde_json::json;

/// 导入一个只读账户（UFVK）。
///
/// `birthday_height` 是「这个账户最早可能收到 note 的高度」。它决定扫描起点，
/// 也决定首次同步要花多久 —— 设成当天的链尖高度，新建账户就几乎不用扫历史。
///
/// 需要一次网络往返取 birthday 前一个区块的 treestate。
pub async fn import_ufvk(
    db: &mut Db,
    client: &mut LightClient,
    account_name: &str,
    ufvk_str: &str,
    birthday_height: u32,
) -> Result<String> {
    let params = *db.params();

    let ufvk = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?;

    // treestate 取的是 birthday 的前一个区块：账户从 birthday 起开始观察，
    // 所以需要「birthday 之前」的树状态作为锚点。
    let anchor_height = birthday_height.saturating_sub(1);
    let treestate = network::tree_state(client, anchor_height).await?;

    let birthday = AccountBirthday::from_treestate(treestate, None).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidBlockHeight,
            json!({ "birthdayHeight": birthday_height }),
        )
        .detail(format!("{e:?}"))
    })?;

    let account = db
        .import_account_ufvk(
            account_name,
            &ufvk,
            &birthday,
            AccountPurpose::ViewOnly,
            None,
        )
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "importAccount" }),
            )
            .detail(e)
        })?;

    Ok(account.id().expose_uuid().to_string())
}

/// 账户余额，JSON。`minConfirmations` 为 0 时把未确认也算进来。
pub fn balance_json(db: &Db, account_uuid: &str, policy: ConfirmationsPolicy) -> Result<String> {
    let account_id = parse_account_id(db, account_uuid)?;

    let summary = db.get_wallet_summary(policy).map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "walletSummary" }),
        )
        .detail(e)
    })?;

    // 还没扫到任何区块时没有摘要。这是一个明确的状态，用错误码表达，
    // 不要在返回值里塞中文原因 —— 那是宿主的文案。
    let Some(summary) = summary else {
        return Err(RuntimeError::new(ErrorCode::NotSynced));
    };

    let balances = summary.account_balances().get(&account_id).ok_or_else(|| {
        RuntimeError::with(
            ErrorCode::AccountNotFound,
            json!({ "accountId": account_uuid }),
        )
    })?;

    // 每个池各自报全五个量，**不做任何跨池合并**。
    //
    // 合并是产品决策，不是事实。比如「可花的屏蔽余额」在本钱包等于
    // orchard + ironwood（sapling 因为裁掉了证明参数只读），但那是钱包当下的
    // 策略，不是链上的性质 —— 焊进 runtime 就意味着策略一变 runtime 就在说谎。
    // 而且合并会抹掉「钱在哪个池」，那恰恰是池明细 UI 要展示的东西。
    //
    // 宿主要什么口径的合计，自己加。
    let pool = |b: &zcash_client_backend::data_api::Balance| {
        serde_json::json!({
            "spendable": u64::from(b.spendable_value()),
            "pendingChange": u64::from(b.change_pending_confirmation()),
            "pendingSpendable": u64::from(b.value_pending_spendability()),
            // 被某个 PCZT 提案占住的部分（见 send.rs 的 reservation）
            "locked": u64::from(b.locked_value()),
            "total": u64::from(b.total()),
        })
    };

    let supported_total = [
        balances.orchard_balance(),
        balances.ironwood_balance(),
        balances.unshielded_regular_balance(),
        balances.unshielded_coinbase_balance(),
    ]
    .into_iter()
    .map(|balance| u64::from(balance.total()))
    .sum::<u64>();

    Ok(serde_json::json!({
        "ready": true,
        "chainTip": u32::from(summary.chain_tip_height()),
        "fullyScannedHeight": u32::from(summary.fully_scanned_height()),

        "orchard": pool(balances.orchard_balance()),
        "ironwood": pool(balances.ironwood_balance()),

        // 透明余额也分开报：coinbase 有不同的成熟规则，而且只能走
        // `propose_shielding_coinbase` 屏蔽 —— `pcztShield` 用的是
        // NonCoinbaseOnly，屏蔽不到它。并成一个数就看不出这个差别了。
        "transparentRegular": pool(balances.unshielded_regular_balance()),
        "transparentCoinbase": pool(balances.unshielded_coinbase_balance()),

        // Sapling is intentionally excluded from both the pool list and the product total.
        "total": supported_total,
    })
    .to_string())
}

/// 从共享库里移除单个账户。
///
/// 共享库下删除**不能**是删库 —— 那会连累其他钱包的账户。库只在最后一个账户
/// 也被删掉之后才该丢弃，那由宿主判断。
pub fn remove_account(db: &mut Db, account_uuid: &str) -> Result<()> {
    let account_id = parse_account_id(db, account_uuid)?;
    db.transactionally_with_extension(|wallet, ext| {
        wallet.delete_account(account_id).map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "deleteAccount" }),
            )
            .detail(e)
        })?;
        crate::tx_state::remove_account_rows(ext, account_uuid)
    })
}

/// 按 UFVK 查这个库里的账户，找不到返回 `"null"`。
///
/// 共享库下这是**定位账户的唯一正确方式**：一份库装着全部钱包的全部账户，
/// 不能假设第一个就是要找的那个；而 runtime 的 UUID 在 purge 之后会变，
/// 也不能缓存。UFVK 是宿主手上稳定且唯一的键。
pub fn account_by_ufvk(db: &Db, ufvk_str: &str) -> Result<String> {
    let params = *db.params();
    let target = UnifiedFullViewingKey::decode(&params, ufvk_str)
        .map_err(|e| RuntimeError::new(ErrorCode::InvalidUfvk).detail(e))?
        .encode(&params);

    for id in db.get_account_ids().map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "getAccountIds" }),
        )
        .detail(e)
    })? {
        let Some(acct) = db.get_account(id).map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "getAccount" }),
            )
            .detail(e)
        })?
        else {
            continue;
        };
        if acct.ufvk().map(|k| k.encode(&params)).as_deref() == Some(target.as_str()) {
            return Ok(json!({ "uuid": acct.id().expose_uuid().to_string() }).to_string());
        }
    }
    Ok("null".to_string())
}

/// 列出本地已有账户，JSON 数组。
pub fn list_accounts_json(db: &Db) -> Result<String> {
    let ids = db.get_account_ids().map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "listAccounts" }),
        )
        .detail(e)
    })?;
    let out: Vec<String> = ids.iter().map(|id| id.expose_uuid().to_string()).collect();
    Ok(serde_json::to_string(&out).unwrap_or_else(|_| "[]".into()))
}

pub fn parse_account_id(db: &Db, account_uuid: &str) -> Result<<Db as WalletRead>::AccountId> {
    let uuid = uuid_from_str(account_uuid)?;
    let id = zcash_client_sqlite::AccountUuid::from_uuid(uuid);
    db.get_account(id)
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::DatabaseError,
                json!({ "operation": "getAccount" }),
            )
            .detail(e)
        })?
        .map(|a| a.id())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::AccountNotFound,
                json!({ "accountId": account_uuid }),
            )
        })
}

fn uuid_from_str(s: &str) -> Result<uuid::Uuid> {
    uuid::Uuid::parse_str(s).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidAccountId, json!({ "value": s })).detail(e)
    })
}

/// 高度换算成 `BlockHeight`，越界时给出可读错误。
pub fn block_height(h: u32) -> BlockHeight {
    BlockHeight::from_u32(h)
}

/// 构造确认数策略。两个门槛都必须 >= 1，且 trusted <= untrusted。
pub fn confirmations_policy(
    trusted: u32,
    untrusted: u32,
    allow_zero_conf_shielding: bool,
) -> Result<ConfirmationsPolicy> {
    let bad = || {
        RuntimeError::with(
            ErrorCode::InvalidConfirmationsPolicy,
            json!({ "trusted": trusted, "untrusted": untrusted }),
        )
    };
    let t = std::num::NonZeroU32::new(trusted).ok_or_else(bad)?;
    let u = std::num::NonZeroU32::new(untrusted).ok_or_else(bad)?;
    ConfirmationsPolicy::new(t, u, allow_zero_conf_shielding).map_err(|e| bad().detail(e))
}
