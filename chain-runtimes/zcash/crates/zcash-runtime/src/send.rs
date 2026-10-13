//! 发送：PCZT 的构造与广播。
//!
//! 拆成四段是刻意的 —— **签名那一段不在本 crate**：
//!
//! ```text
//!   runtime.pcztCreate()          构造（要钱包状态：选币、witness、anchor）
//!   runtime.pcztProve()           证明，纯计算，不碰私钥
//!   keys.pcztSign()               签名（要花费密钥，在 onekey-zcash-keys 里）
//!   runtime.pcztSubmit()          提取 + 广播，非明确拒收才写回本地钱包
//!   runtime.broadcastTransaction()  真正广播出去
//! ```
//!
//! 证明必须排在签名之前：上游 Signer 角色的文档写明，它需要足以「验证证明覆盖的是
//! 正确数据」的信息，也就是签名时证明必须已经在 PCZT 里。
//!
//! 这条缝是密钥纪律的体现：构造和广播需要钱包数据库，签名需要种子，
//! 两者永远不在同一个 crate 里同时出现。
//!
//! ## 为什么这些留在 Rust
//!
//! 选币、ZIP-317 费用、anchor 选择、witness —— 这些是**共识相关**的。
//! 选错 anchor 交易会被拒，算错费可能丢钱。所以机制留在上游实现里。
//!
//! 但**策略旋钮全部由宿主传入**（找零池、dust 策略、OVK 策略、确认数、
//! bundle padding），本 crate 不替宿主做产品决策，也不藏默认值。

use zcash_client_backend::data_api::locking::LockOwner;
use zcash_client_backend::data_api::wallet::{
    create_pczt_from_proposal, extract_and_store_transaction_from_pczt,
    input_selection::GreedyInputSelector, propose_transfer, unlock_proposal_inputs, LockRequest,
};
use zcash_client_backend::data_api::WalletRead;
use zcash_client_backend::fees::{zip317::SingleOutputChangeStrategy, DustOutputPolicy};
use zcash_client_backend::wallet::OvkPolicy;
use zcash_primitives::transaction::builder::BundlePadding;
use zcash_primitives::transaction::fees::zip317::FeeRule as Zip317FeeRule;
use zcash_protocol::{memo::MemoBytes, value::Zatoshis, ShieldedPool};

use crate::account;
use crate::error::{ErrorCode, Result, RuntimeError};
use crate::{tx_state, wallet::Db};
use serde_json::json;

/// `stage` 是稳定标识，宿主据此知道卡在构造的哪一段。
fn build_err(stage: &str, e: impl std::fmt::Display) -> RuntimeError {
    RuntimeError::with(ErrorCode::TransactionBuildError, json!({ "stage": stage })).detail(e)
}

/// 把提案阶段的失败翻译成宿主能分支处理的错误码。
///
/// 「余额不足」是发送最常见的失败，宿主要据此提示还差多少、要不要减额重试；
/// 「需要先同步」则是让用户等扫描追平后再试。这两种都**不该**被折叠成泛化的
/// `TRANSACTION_BUILD_ERROR` —— 那样宿主只能拿到一句没法判定的字符串。
///
/// **注意上游有两个不同的「余额不足」出处**，两个都要接住：
///
/// - `Error::InsufficientFunds` —— 选币阶段就凑不够（转账走这条）
/// - `Error::Change(ChangeError::InsufficientFunds)` —— 选币过了，但算上找零和
///   手续费之后不够（屏蔽零余额走这条）
///
/// 只接第一个的话，屏蔽路径会退化成泛化错误 —— 实测踩过。
fn propose_err<DE, CT, SE, FE, CHE, NR>(
    stage: &str,
    e: zcash_client_backend::data_api::error::Error<DE, CT, SE, FE, CHE, NR>,
) -> RuntimeError
where
    DE: std::fmt::Debug,
    CT: std::fmt::Debug,
    SE: std::fmt::Debug,
    FE: std::fmt::Debug,
    CHE: std::fmt::Debug,
    NR: std::fmt::Debug,
{
    use zcash_client_backend::data_api::error::Error as DataApiError;
    use zcash_client_backend::fees::ChangeError;

    let insufficient = |available: &Zatoshis, required: &Zatoshis| {
        let (have, need) = (u64::from(*available), u64::from(*required));
        RuntimeError::with(
            ErrorCode::InsufficientFunds,
            json!({
                "availableZat": have,
                "requiredZat": need,
                // 直接给差额，省得宿主自己减（还容易减错方向）
                "shortfallZat": need.saturating_sub(have),
                "stage": stage,
            }),
        )
    };

    match &e {
        DataApiError::InsufficientFunds {
            available,
            required,
        } => insufficient(available, required),
        DataApiError::Change(ChangeError::InsufficientFunds {
            available,
            required,
        }) => insufficient(available, required),
        DataApiError::ScanRequired => {
            RuntimeError::with(ErrorCode::NotSynced, json!({ "stage": stage }))
        }
        other => build_err(stage, format!("{other:?}")),
    }
}

/// 解析宿主传来的找零池名。构造与报价共用，避免两处各写一份而写歪。
pub fn parse_change_pool(name: &str) -> Result<ShieldedPool> {
    match name {
        // NU6.3 makes Orchard spend-only; new change must enter Ironwood.
        "ironwood" => Ok(ShieldedPool::Ironwood),
        // Sapling 不是合法的找零去处，理由和 `parse_spend_source` 拒它一样，
        // 而且更硬：本方案裁掉了 Sapling 的 Groth16 证明参数，建不出 Sapling
        // 输出。就算建得出，那笔找零也进了一个本钱包只读不可花的池子 ——
        // 用户的找零会当场变成取不回来的钱。
        other => Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "fallbackChangePool", "value": other }),
        )),
    }
}

/// 解析宿主传来的「只从这个池子花」。`None` 表示不限制（见
/// `SendPolicy::spend_source`）。
///
/// Orchard remains a valid spend source, but cannot receive new change.
/// Sapling spending is unsupported because this runtime does not ship its prover.
pub fn parse_spend_source(name: Option<&str>) -> Result<Option<ShieldedPool>> {
    match name {
        None => Ok(None),
        Some("ironwood") => Ok(Some(ShieldedPool::Ironwood)),
        Some("orchard") => Ok(Some(ShieldedPool::Orchard)),
        Some(other) => Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "spendSource", "value": other }),
        )),
    }
}

/// 宿主可调的发送策略。全部显式传入，本 crate 不藏默认值。
#[derive(Debug, Clone, Copy)]
pub struct SendPolicy {
    /// 可信 note 的确认数门槛（>= 1）。
    pub trusted: u32,
    /// 外来 note 的确认数门槛（>= trusted）。
    pub untrusted: u32,
    /// 交易全透明时，找零回落到哪个屏蔽池。
    pub fallback_change_pool: ShieldedPool,
    /// 是否给 Orchard bundle 填充到固定 action 数（隐藏真实 action 数量，
    /// 代价是交易更大、费用更高）。
    pub pad_orchard_bundle: bool,
    /// 零确认的自家屏蔽产出允不允许立刻再花（ZIP-315 建议允许）。
    ///
    /// 曾经硬编码 `false`，而余额读取那头却是参数 —— 宿主想跟进 ZIP-315
    /// 就会造出「余额显示可花、选币拒花」的错配。读和花必须同源。
    pub allow_zero_conf_shielding: bool,
    /// 只从这一个屏蔽池选 note。`None` = 本方案能花的全部池子。
    ///
    /// 存在的理由有两个，都不是可选的：
    ///
    /// 1. **Sapling 必须排除。** 上游 `SpendPolicy::default()` 允许 sapling，
    ///    而选币是「最老的合格 note 优先」—— 一个恢复了 2022 年前历史的账户，
    ///    每一笔转账都会先挑中 sapling note，然后在证明阶段撞上
    ///    `PROVE_SAPLING = false`。那笔钱花不了是事实，但因此**整个账户发不出
    ///    任何交易**不是。默认集合把它挡在选币之外，用户就能正常花其他池子。
    ///
    /// 2. **按池子提取需要它。** 「把旧 Orchard 余额转到透明地址」这个动作，
    ///    没有它就只是一笔普通转账，选币可能去花 Ironwood —— 把用户还想留在
    ///    私密池里的钱搬到了链上公开的地址。上游文档给的例子正是这个用法。
    ///
    /// 指定一个池子时，凑不够就是 `InsufficientFunds`，上游保证不会越池。
    pub spend_source: Option<ShieldedPool>,
}

impl SendPolicy {
    fn change_pool(&self) -> ShieldedPool {
        self.fallback_change_pool
    }

    fn padding(&self) -> BundlePadding {
        if self.pad_orchard_bundle {
            BundlePadding::DEFAULT
        } else {
            BundlePadding::UNPADDED
        }
    }
}

/// 从宿主传来的原始参数造一份发送策略。
///
/// **报价和真正构造必须走这一个入口。** 两边各拼一次 `SendPolicy`，迟早会出现
/// 某个字段只在一边接上：`pad_orchard_bundle` 就是这样 —— 报价那边曾经固定写
/// `false`，而它会改变 Orchard bundle 的大小、进而改变 ZIP-317 手续费。当时
/// 宿主的常量也是 `false`，所以对得上；等哪天真去打开这个产品开关，报出来的
/// 费用就会比实付的低，而且没有任何东西会报错。
#[allow(clippy::too_many_arguments)]
pub fn parse_send_policy(
    trusted: u32,
    untrusted: u32,
    fallback_change_pool: &str,
    pad_orchard_bundle: bool,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<&str>,
) -> Result<SendPolicy> {
    Ok(SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: parse_change_pool(fallback_change_pool)?,
        pad_orchard_bundle,
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: parse_spend_source(spend_source)?,
    })
}

/// 一次转账提案。`create_pczt` 与 `quote` 共用 —— 两者的选币、费率、找零策略
/// 必须完全一致，否则报价和实际构造会对不上，用户看到的费用就是假的。
type TransferProposal =
    zcash_client_backend::proposal::Proposal<Zip317FeeRule, zcash_client_sqlite::ReceivedNoteId>;

/// 收款只能落在 Sapling：Sapling 证明参数没随包发布（体积），这种交易必然在
/// 证明阶段失败 —— 那时用户已经点了确认。在构造前就拒绝。
fn recipient_needs_sapling_output(
    params: &zcash_protocol::consensus::Network,
    recipient: &zcash_address::ZcashAddress,
) -> bool {
    use zcash_keys::address::Address;
    match Address::try_from_zcash_address(params, recipient.clone()) {
        Ok(Address::Sapling(_)) => true,
        Ok(Address::Unified(ua)) => ua.has_sapling() && !ua.has_orchard(),
        _ => false,
    }
}

/// ZIP-320 TEX 地址只收透明输入；私密付款花的是屏蔽池，付不了它。
fn recipient_is_tex(
    params: &zcash_protocol::consensus::Network,
    recipient: &zcash_address::ZcashAddress,
) -> bool {
    matches!(
        zcash_keys::address::Address::try_from_zcash_address(params, recipient.clone()),
        Ok(zcash_keys::address::Address::Tex(_))
    )
}

// Keep the legacy text argument compatible; new hosts pass the exact ZIP 302 field.
pub fn parse_memo(text: Option<&str>, hex: Option<&str>) -> Result<Option<MemoBytes>> {
    let invalid = || RuntimeError::new(ErrorCode::InvalidMemo);
    match (text, hex) {
        (Some(_), Some(_)) => Err(invalid()),
        (_, Some(hex)) => {
            if hex.len() != 1024 {
                return Err(invalid());
            }
            let bytes = hex::decode(hex).map_err(|_| invalid())?;
            Ok(Some(MemoBytes::from_bytes(&bytes).map_err(|_| invalid())?))
        }
        (Some(text), None) => {
            let memo =
                zcash_protocol::memo::Memo::from_bytes(text.as_bytes()).map_err(|_| invalid())?;
            Ok(Some(MemoBytes::from(&memo)))
        }
        (None, None) => Ok(None),
    }
}

fn build_proposal(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
    lock: Option<zcash_client_backend::data_api::wallet::LockRequest>,
) -> Result<TransferProposal> {
    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;

    // 零值：上游只对透明收款方拒绝，屏蔽收款方会建出一笔只付手续费的交易。
    let amount = Zatoshis::from_u64(amount_zat)
        .ok()
        .filter(|a| !a.is_zero())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::AmountOutOfRange,
                json!({ "valueZat": amount_zat }),
            )
        })?;

    let recipient = zcash_address::ZcashAddress::try_from_encoded(to_address).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidAddress, json!({ "value": to_address })).detail(e)
    })?;

    if recipient_is_tex(&params, &recipient) {
        return Err(RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "reason": "texRequiresTransparentSource" }),
        ));
    }

    if recipient_needs_sapling_output(&params, &recipient) {
        return Err(RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "reason": "saplingOnlyRecipient" }),
        ));
    }

    // Payment::new 会自己校验「透明地址不能带 memo」这类约束。
    let payment = zip321::Payment::new(recipient, Some(amount), memo.cloned(), None, None, vec![])
        .map_err(|e| {
            RuntimeError::with(ErrorCode::InvalidMemo, json!({ "reason": "payment" }))
                .detail(format!("{e:?}"))
        })?;

    let request = zip321::TransactionRequest::new(vec![payment])
        .map_err(|e| build_err("transactionRequest", format!("{e:?}")))?;

    let confirmations = account::confirmations_policy(
        policy.trusted,
        policy.untrusted,
        policy.allow_zero_conf_shielding,
    )?;

    // 花费来源策略：只花所选的屏蔽池，一个透明 UTXO 都不花（与上游默认一致）。
    let spend_policy = {
        use zcash_client_backend::data_api::wallet::input_selection::SpendPolicy;
        // 不用 SpendPolicy::default()：它允许 sapling。见 SendPolicy::spend_source。
        // 屏蔽池之间永不并池（docs/08），而"当前池"只在宿主一处定义
        // （ZCASH_CURRENT_SHIELDED_POOL）：这里不藏缺省，缺参直接拒绝。
        let pool = policy.spend_source.ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "spendSource", "reason": "required" }),
            )
        })?;
        let mut base = SpendPolicy::shielded_pools([pool]);
        // A retry under the same reservation id must reselect what that id
        // already holds. The default policy excludes every lock, the caller's
        // own included, so the retry would lock a second set and leave the
        // first frozen until expiry. Only this owner's locks are admitted.
        if let Some(lock) = &lock {
            use zcash_client_backend::data_api::locking::LockedInputPolicy;
            use zcash_client_backend::data_api::wallet::input_selection::NonEmptyBTreeSet;
            base = base.with_locked_input_policy(LockedInputPolicy::PreferLocked(
                NonEmptyBTreeSet::singleton(lock.owner()),
            ));
        }
        base
    };

    let selector = GreedyInputSelector::<Db>::new();
    let change = SingleOutputChangeStrategy::<_, Db>::new(
        Zip317FeeRule::standard(),
        None,
        policy.change_pool(),
        DustOutputPolicy::default(),
    );

    // 显式标注 commitment-tree 错误类型：这一处泛型推不出来。
    let proposal =
        propose_transfer::<Db, _, _, _, zcash_client_sqlite::wallet::commitment_tree::Error>(
            db,
            &params,
            account_id,
            &selector,
            &change,
            request,
            confirmations,
            &spend_policy,
            lock,
            None,
        );

    proposal.map_err(|e| propose_err("proposeTransfer", e))
}

/// 只出费用，不构造 PCZT、不锁定 note。
///
/// 用途是发送前给用户看手续费。**必须与 `create_pczt` 走同一条提案路径**，
/// 否则报价与实付不符 —— 那是最难被发现、也最不该发生的一类 bug。
pub fn quote(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
) -> Result<String> {
    let proposal = build_proposal(db, account_uuid, to_address, amount_zat, memo, policy, None)?;
    let fee: u64 = proposal
        .steps()
        .iter()
        .map(|s| u64::from(s.balance().fee_required()))
        .sum();

    Ok(json!({
        "feeZat": fee,
        "steps": proposal.steps().len(),
    })
    .to_string())
}

// ── note 锁定（宿主契约里的 reservation） ────────────────────────────────
//
// 屏蔽池里的钱是一个个 note，性质像 UTXO：整个花掉，花时公布 nullifier，
// 同一个 note 被两笔交易花，第二笔必被节点拒绝。
//
// 而发送流水线中间有一段「已选币、未花掉」的窗口：
//     选币 → 审核页 → 输密码 → 签名 → 广播
// 用户在审核页停着又发起第二笔（或点了 shield），选币器会把同一批 note 再选一遍。
//
// 锁定就是在选币时把这批 note 标记为已被某个操作占用，别的选币跳过；
// 用户取消就 release 还回去。上游还带按区块高度的过期时间，兜住「app 中途崩了」。
//
// 提案本身要留在内存里：`unlock_proposal_inputs` 需要提案才知道该解锁哪些 note。
thread_local! {
    static RESERVATIONS: std::cell::RefCell<
        std::collections::HashMap<String, (String, TransferProposal, Vec<u8>)>,
    > = std::cell::RefCell::new(std::collections::HashMap::new());
}

fn hex32(bytes: &[u8; 32]) -> String {
    bytes.iter().map(|b| format!("{b:02x}")).collect()
}

fn parse_owner(reservation_id: &str) -> Result<LockOwner> {
    let bad = || {
        RuntimeError::with(
            ErrorCode::InvalidReservationId,
            json!({ "value": reservation_id }),
        )
    };
    if reservation_id.len() != 64 {
        return Err(bad());
    }
    let mut out = [0u8; 32];
    for (i, chunk) in reservation_id.as_bytes().chunks(2).enumerate() {
        let hex = std::str::from_utf8(chunk).map_err(|_| bad())?;
        out[i] = u8::from_str_radix(hex, 16).map_err(|_| bad())?;
    }
    Ok(LockOwner::new(out))
}

/// 释放一次 reservation 占用的 note。
///
/// 幂等：id 不存在（已发送、已释放、或本来就没锁过）返回 `false` 而不报错 ——
/// 取消路径上重复调用是常态，不该因此弹错误给用户。
pub fn release_reservation(db: &mut Db, reservation_id: &str) -> Result<bool> {
    let owner = parse_owner(reservation_id)?;

    // Finalization is committed in the wallet database before the host can
    // observe the txid. A durability/bridge failure can therefore leave the
    // host holding a reservation id for a transaction that already spends the
    // selected notes. In that state release must only discard the in-memory
    // proposal; unlocking its inputs would make spent notes selectable again.
    let finalized_txid = db.transactionally_with_extension(|_wallet, ext| {
        tx_state::existing_txid_for_reservation(ext, reservation_id, None)
    })?;
    if finalized_txid.is_some() {
        forget_reservation(reservation_id);
        return Ok(false);
    }

    if let Some((account_uuid, proposal, approved_pczt)) =
        RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id))
    {
        if let Err(e) = unlock_proposal_inputs(db, &proposal, owner) {
            RESERVATIONS.with(|r| {
                r.borrow_mut().insert(
                    reservation_id.to_owned(),
                    (account_uuid, proposal, approved_pczt),
                );
            });
            return Err(build_err("unlockProposalInputs", format!("{e:?}")));
        }
        // Anything else this owner still holds (a set locked before a retry
        // replaced the in-memory proposal) goes too.
        release_persisted_reservation(db, reservation_id, owner)?;
        return Ok(true);
    }
    release_persisted_reservation(db, reservation_id, owner)
}

// The proposal map is process-local, but the lock owner survives in SQLite.
// Read the owner's exact output references and use the upstream lock API;
// never clear another proposal's locks or write wallet-owned tables directly.
fn release_persisted_reservation(
    db: &mut Db,
    reservation_id: &str,
    owner: LockOwner,
) -> Result<bool> {
    use rusqlite::OptionalExtension;
    use zcash_client_backend::{data_api::OutputLockStore, wallet::OutputRef};
    use zcash_primitives::transaction::TxId;
    use zcash_protocol::PoolType;

    db.transactionally_with_extension(|wallet, ext| {
        if tx_state::existing_txid_for_reservation(ext, reservation_id, None)?.is_some() {
            return Ok(false);
        }
        let mut released = false;
        for (table, index, pool) in [
            (
                "sapling_received_notes",
                "output_index",
                PoolType::Shielded(ShieldedPool::Sapling),
            ),
            (
                "orchard_received_notes",
                "action_index",
                PoolType::Shielded(ShieldedPool::Orchard),
            ),
            (
                "ironwood_received_notes",
                "action_index",
                PoolType::Shielded(ShieldedPool::Ironwood),
            ),
            (
                "transparent_received_outputs",
                "output_index",
                PoolType::Transparent,
            ),
        ] {
            loop {
                let output = ext
                    .query_row(
                        &format!(
                            "SELECT tx.txid, output.{index}
                              FROM {table} output
                              JOIN transactions tx ON tx.id_tx = output.transaction_id
                              WHERE output.lock_owner = ?1 LIMIT 1"
                        ),
                        [owner.as_bytes()],
                        |row| Ok((row.get::<_, [u8; 32]>(0)?, row.get::<_, u32>(1)?)),
                    )
                    .optional()
                    .map_err(|e| build_err("readReservationLocks", e))?;
                let Some((txid, output_index)) = output else {
                    break;
                };
                let output = OutputRef::new(TxId::from_bytes(txid), pool, output_index);
                if !wallet
                    .unlock_output(&output, owner)
                    .map_err(|e| build_err("releaseReservationLock", e))?
                {
                    return Err(build_err(
                        "releaseReservationLock",
                        "persisted lock was not released",
                    ));
                }
                released = true;
            }
        }
        Ok(released)
    })
}

/// 交易已落库后丢弃 reservation 记录。
///
/// 此时锁已由花费本身接管（note 真的被花掉了），不需要也不应该再解锁 ——
/// 解锁会把已经花出去的 note 重新放回可选池。
pub fn forget_reservation(reservation_id: &str) {
    RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id));
}

/// 账户被删掉时，它还在内存里的提案一并丢弃：那些 note 已经随账户删了。
pub fn forget_account_reservations(account_uuid: &str) {
    RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_uuid, _, _)| owner_uuid.as_str() != account_uuid)
    });
}

/// 构造一笔转账的 PCZT（未签名），并**锁住选中的 note**。
///
/// 返回 `{ pcztHex, reservationId }`：`reservationId` 是这批 note 的占用凭证，
/// 调用方必须一直带着它，直到交易提交（`pcztSubmit`）或用户取消（`releaseReservation`）。
/// 不释放也不会永久卡死 —— 锁有按区块高度的过期时间，但那是兜底，不是正常路径。
///
/// `lock_for_blocks` 由宿主决定，但必须长过交易的过期高度：广播后、落库前崩溃时，
/// 只有这把锁防止同一批 note 被再花一次。
///
/// `reservation_id` 可由调用方传入（64 位 hex）。上游的设计意图就是**调用方持有
/// 这个 token**：先生成、先持久化，再来构造。这样即使中途崩溃，恢复后仍然知道
/// 该释放哪一批 note，或者重新锁住自己那批。不传则由这里随机生成。
// The public binding keeps proposal policy explicit at the call boundary.
#[allow(clippy::too_many_arguments)]
pub fn create_pczt(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&MemoBytes>,
    policy: SendPolicy,
    lock_for_blocks: u32,
    reservation_id: Option<&str>,
) -> Result<(Vec<u8>, String, u64)> {
    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;

    let owner = match reservation_id {
        Some(id) => parse_owner(id)?,
        None => LockOwner::random(&mut rand::rngs::OsRng),
    };
    let reservation_id = hex32(owner.as_bytes());
    let proposal = build_proposal(
        db,
        account_uuid,
        to_address,
        amount_zat,
        memo,
        policy,
        Some(LockRequest::new(owner, lock_for_blocks)),
    )?;
    let fee_zat = proposal
        .steps()
        .iter()
        .map(|step| u64::from(step.balance().fee_required()))
        .sum();

    // 同理：InputsErrT / ChangeErrT 只出现在 Err 分支，推不出来。
    // 提案已经锁住了 note。从这里往下任何失败都必须**先解锁再返回** ——
    // 否则调用方拿不到 reservationId（我们还没返回它），就永远没法释放，
    // 那批 note 会一直冻到锁自然过期。
    let built = (|| -> Result<Vec<u8>> {
        let pczt = create_pczt_from_proposal::<
            Db,
            _,
            std::convert::Infallible,
            _,
            std::convert::Infallible,
            _,
        >(
            db,
            &params,
            account_id,
            OvkPolicy::Sender,
            &proposal,
            None,
            policy.padding(),
        )
        .map_err(|e| build_err("createPczt", format!("{e:?}")))?;

        pczt.serialize().map_err(|e| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
                .detail(format!("{e:?}"))
        })
    })();

    let bytes = match built {
        Ok(b) => b,
        Err(e) => {
            // 尽力解锁。解锁本身再失败也只能吞掉 —— 真正的错误是上面那个，
            // 不该被一个清理动作的错误盖掉；锁还有过期时间兜底。
            let _ = unlock_proposal_inputs(db, &proposal, owner);
            return Err(e);
        }
    };

    // 提案要留着：解锁时需要它才知道该放开哪些 note。
    RESERVATIONS.with(|r| {
        r.borrow_mut().insert(
            reservation_id.clone(),
            (account_uuid.to_owned(), proposal, bytes.clone()),
        );
    });
    Ok((bytes, reservation_id, fee_zat))
}

/// 用哪个电路版本证明，由**共识分支**决定，不是产品选项。
///
/// `height` 是交易所属分支的起始高度：过了 NU6.3 激活高度就用 PostNu6_3，
/// 否则用 FixedPostNu6_2。`InsecurePreNu6_2` 永远不用于证明（上游明确标注）。
fn circuit_version_for(
    params: &zcash_protocol::consensus::Network,
    height: zcash_protocol::consensus::BlockHeight,
) -> orchard::circuit::OrchardCircuitVersion {
    use zcash_protocol::consensus::{NetworkUpgrade, Parameters};
    match params.activation_height(NetworkUpgrade::Nu6_3) {
        Some(activation) if height >= activation => {
            orchard::circuit::OrchardCircuitVersion::PostNu6_3
        }
        _ => orchard::circuit::OrchardCircuitVersion::FixedPostNu6_2,
    }
}

/// The transaction's own branch decides, not our tip: around an upgrade the two
/// disagree, and a proof under the wrong circuit is rejected. The tip is only
/// the fallback for a branch id this build does not know.
fn circuit_version_for_branch(
    params: &zcash_protocol::consensus::Network,
    consensus_branch_id: u32,
    chain_tip: zcash_protocol::consensus::BlockHeight,
) -> orchard::circuit::OrchardCircuitVersion {
    let branch_start = zcash_protocol::consensus::BranchId::try_from(consensus_branch_id)
        .ok()
        .and_then(|branch| branch.height_bounds(params))
        .map(|(start, _)| start);
    circuit_version_for(params, branch_start.unwrap_or(chain_tip))
}

/// 给 PCZT 生成 Orchard / Ironwood 零知识证明。
/// 合并两份同一笔交易的 PCZT（本地完整副本 + 外部签名器返回的脱敏副本）。
/// 合并硬件回传的签名副本。**只许补签名，不许改交易。**
///
/// 别指望后面那一步兜底：`TransactionExtractor` 是拿**合并后**的交易重算 sighash
/// 再验签名，它只回答「这笔交易自洽吗」，不知道用户批准过什么。
///
/// 裸 `Combiner` 目前挡得住换交易，但那是两条上游性质的副作用，不是本 crate 的
/// 声明：一是我们构造的 PCZT `tx_modifiable == 0`，不可增删输入/输出/action；
/// 二是效果字段全部已填，冲突会被逐字段查出来。两条都属于上游，升级就可能变。
///
/// 所以把不变量写在这里：合并前后 sighash 必须逐字节相同，且 `scriptSig` 必须仍为
/// 空 —— 它不进 sighash，又恰好是原件里为 `None`、会被 `merge_optional` 直接采纳的
/// 字段，是这层唯一真正漏着的口子。失败发生在昂贵的证明之前，错误带 `field`。
pub fn combine_pczt(original: &[u8], signed: &[u8]) -> Result<Vec<u8>> {
    use pczt::roles::signer::Signer;
    use pczt::roles::verifier::{TransparentError, Verifier};

    let parse = |bytes: &[u8], which: &str| {
        pczt::Pczt::parse(bytes).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "parse", "which": which }),
            )
            .detail(format!("{error:?}"))
        })
    };
    let signer = |pczt: pczt::Pczt, which: &str| {
        Signer::new(pczt).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "signer", "which": which }),
            )
            .detail(format!("{error:?}"))
        })
    };
    let mismatch = |field: &str| {
        RuntimeError::with(
            ErrorCode::PcztError,
            json!({
                "stage": "combine",
                "reason": "signedPcztChangedTheTransaction",
                "field": field,
            }),
        )
    };

    let original = parse(original, "original")?;
    let signed = parse(signed, "signed")?;
    let transparent_inputs = original.transparent().inputs().len();
    let approved = signer(original.clone(), "original")?;

    let combined = pczt::roles::combiner::Combiner::new(vec![original, signed])
        .combine()
        .map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "combine" }))
                .detail(format!("{error:?}"))
        })?;
    let result = signer(combined.clone(), "combined")?;

    if result.shielded_sighash() != approved.shielded_sighash() {
        return Err(mismatch("shieldedSighash"));
    }
    if combined.transparent().inputs().len() != transparent_inputs {
        return Err(mismatch("transparentInputCount"));
    }
    for index in 0..transparent_inputs {
        let approved_sighash = approved.transparent_sighash(index).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "transparentSighash", "which": "original" }),
            )
            .detail(format!("{error:?}"))
        })?;
        let combined_sighash = result.transparent_sighash(index).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "transparentSighash", "which": "combined" }),
            )
            .detail(format!("{error:?}"))
        })?;
        if approved_sighash != combined_sighash {
            return Err(mismatch("transparentSighash"));
        }
    }
    // sighash 不覆盖 scriptSig，而它正是合并里唯一能被对方无中生有填上的字段
    // （原件恒为 None，merge_optional 会直接采纳）。我们自己从不填：提取时
    // SpendFinalizer 才从 partial_signatures 拼出来。
    // 在副本上查 —— Verifier 会把 bundle 解析再序列化回去，不能动待序列化的那份。
    if transparent_inputs > 0 {
        Verifier::new(combined.clone())
            .with_transparent(|bundle| {
                if bundle
                    .inputs()
                    .iter()
                    .any(|input| input.script_sig().is_some())
                {
                    Err(TransparentError::Custom(()))
                } else {
                    Ok(())
                }
            })
            .map_err(|error| match error {
                TransparentError::Custom(()) => mismatch("transparentScriptSig"),
                other => RuntimeError::with(
                    ErrorCode::PcztError,
                    json!({ "stage": "verifyTransparent" }),
                )
                .detail(format!("{other:?}")),
            })?;
    }

    combined.serialize().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
            .detail(format!("{error:?}"))
    })
}

pub fn prove_pczt(db: &Db, pczt_bytes: &[u8]) -> Result<Vec<u8>> {
    let params = *db.params();
    let chain_tip = db
        .chain_height()
        .map_err(|error| build_err("chainHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    prove_pczt_at_height(&params, chain_tip, pczt_bytes)
}

/// Builds only public parameters, without a wallet, proposal, or spending key.
pub fn prewarm_pczt(
    params: &zcash_protocol::consensus::Network,
    target_height: zcash_protocol::consensus::BlockHeight,
) -> Result<()> {
    let version = circuit_version_for(params, target_height);
    crate::circuit_keys::proving_key(version)?;
    crate::circuit_keys::verifying_key(version);
    Ok(())
}

/// Retains upstream proof/signature validation while reusing public parameters.
pub fn extract_pczt(pczt: pczt::Pczt) -> Result<zcash_primitives::transaction::Transaction> {
    use orchard::circuit::OrchardCircuitVersion;
    use zcash_protocol::consensus::{BranchId, OrchardProtocolRevision};

    let version = BranchId::try_from(*pczt.global().consensus_branch_id())
        .ok()
        .and_then(|branch| branch.orchard_protocol_revision())
        .map(|revision| match revision {
            OrchardProtocolRevision::InsecureV1 => OrchardCircuitVersion::InsecurePreNu6_2,
            OrchardProtocolRevision::V2 => OrchardCircuitVersion::FixedPostNu6_2,
            OrchardProtocolRevision::V3 => OrchardCircuitVersion::PostNu6_3,
        });
    let has_shielded_bundle =
        !pczt.orchard().actions().is_empty() || !pczt.ironwood().actions().is_empty();
    let extractor = pczt::roles::tx_extractor::TransactionExtractor::new(pczt);
    let extractor = match version.filter(|_| has_shielded_bundle) {
        Some(version) => extractor.with_orchard(crate::circuit_keys::verifying_key(version)),
        // Let upstream report unsupported branches and malformed bundles.
        None => extractor,
    };
    extractor.extract().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "extract" }))
            .detail(format!("{error:?}"))
    })
}

/// Pure proving: no wallet, database, scan, or storage initialization.
pub fn prove_pczt_at_height(
    params: &zcash_protocol::consensus::Network,
    chain_tip: zcash_protocol::consensus::BlockHeight,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    let pczt = pczt::Pczt::parse(pczt_bytes).map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{error:?}"))
    })?;
    let version =
        circuit_version_for_branch(params, *pczt.global().consensus_branch_id(), chain_tip);
    let prover = pczt::roles::prover::Prover::new(pczt);

    let prover = if prover.requires_ironwood_proof() {
        let proving_key = crate::circuit_keys::proving_key(version)?;
        prover.create_ironwood_proof(proving_key).map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "ironwoodProof" }))
                .detail(format!("{error:?}"))
        })?
    } else {
        prover
    };

    if prover.requires_sapling_proofs() {
        return Err(RuntimeError::with(
            ErrorCode::Unimplemented,
            json!({ "what": "saplingProof" }),
        ));
    }

    let prover = if prover.requires_orchard_proof() {
        let proving_key = crate::circuit_keys::proving_key(version)?;
        prover.create_orchard_proof(proving_key).map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "orchardProof" }))
                .detail(format!("{error:?}"))
        })?
    } else {
        prover
    };

    prover.finish().serialize().map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serializeProven" }))
            .detail(format!("{error:?}"))
    })
}

/// 把签好名的 PCZT 落进钱包库：从这一刻起它花掉的 note 由这笔交易持有。
fn store_signed_pczt(
    db: &mut Db,
    account_uuid: &str,
    pczt: pczt::Pczt,
    orchard_vk: &orchard::circuit::VerifyingKey,
    reservation_id: &str,
) -> Result<String> {
    db.transactionally_with_extension(|wallet, ext| {
        let txid =
            extract_and_store_transaction_from_pczt::<_, zcash_client_sqlite::ReceivedNoteId>(
                wallet,
                pczt,
                None,
                Some(orchard_vk),
            )
            .map_err(|e| build_err("extractAndStore", format!("{e:?}")))?
            .to_string();
        tx_state::insert_finalized(ext, &txid, account_uuid, reservation_id)?;
        Ok(txid)
    })
}

/// 一次提交的结局。
pub enum SubmitOutcome {
    Accepted {
        txid: String,
    },
    /// The trusted node rejected it; keep the durable record until chain expiry.
    Rejected {
        txid: String,
        error: RuntimeError,
    },
    /// 不知道节点收没收到：已落库，交易持有 note，同步时会重播。
    Unknown {
        txid: String,
        error: RuntimeError,
    },
}

// A spent-input/duplicate-nullifier reply can refer to this transaction itself.
fn first_submit_rejection(error: RuntimeError) -> std::result::Result<RuntimeError, RuntimeError> {
    if error.code == ErrorCode::BroadcastRejected {
        Ok(error)
    } else {
        Err(error)
    }
}

fn record_submit_outcome(db: &mut Db, txid: String, result: Result<()>) -> SubmitOutcome {
    match result {
        Ok(()) => match tx_state::mark_accepted(db, &txid) {
            Ok(()) => SubmitOutcome::Accepted { txid },
            Err(error) => SubmitOutcome::Unknown { txid, error },
        },
        Err(error) => match first_submit_rejection(error) {
            Ok(error) => match tx_state::mark_rejected(db, &txid, &error) {
                Ok(()) => SubmitOutcome::Rejected { txid, error },
                Err(error) => SubmitOutcome::Unknown { txid, error },
            },
            Err(error) => SubmitOutcome::Unknown { txid, error },
        },
    }
}

/// Persist the exact authorized transaction before any external broadcast.
/// Retries and crash recovery always rebroadcast the stored bytes.
pub async fn submit_signed_pczt(
    db: &mut Db,
    client: &mut crate::network::LightClient,
    account_uuid: &str,
    pczt_bytes: &[u8],
    reservation_id: &str,
) -> Result<SubmitOutcome> {
    // 上次提交已经落库（宿主没拿到回包就重试）：重播那一笔，不再提取第二次。
    let stored = db.transactionally_with_extension(|_wallet, ext| {
        tx_state::existing_txid_for_reservation(ext, reservation_id, Some(account_uuid))
    })?;
    if let Some(txid) = stored {
        if let Err(error) = tx_state::assert_broadcastable(db, &txid) {
            return Ok(if error.code == ErrorCode::BroadcastRejected {
                SubmitOutcome::Rejected { txid, error }
            } else {
                SubmitOutcome::Unknown { txid, error }
            });
        }
        let raw = match raw_transaction(db, &txid) {
            Ok(raw) => raw,
            Err(error) => return Ok(SubmitOutcome::Unknown { txid, error }),
        };
        let result = crate::network::broadcast(client, raw).await;
        return Ok(record_submit_outcome(db, txid, result));
    }

    let approved_pczt = RESERVATIONS
        .with(|reservations| {
            reservations
                .borrow()
                .get(reservation_id)
                .filter(|(owner, _, _)| owner == account_uuid)
                .map(|(_, _, pczt)| pczt.clone())
        })
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidReservationId,
                json!({ "reason": "missingOrWrongAccount" }),
            )
        })?;
    let bound_pczt = combine_pczt(&approved_pczt, pczt_bytes)?;
    let pczt = pczt::Pczt::parse(&bound_pczt).map_err(|e| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{e:?}"))
    })?;
    let params = *db.params();
    let chain_tip = db
        .chain_height()
        .map_err(|error| build_err("chainHeight", error))?
        .ok_or_else(|| RuntimeError::new(ErrorCode::NotSynced))?;
    let version =
        circuit_version_for_branch(&params, *pczt.global().consensus_branch_id(), chain_tip);
    let orchard_vk = crate::circuit_keys::verifying_key(version);

    // This transaction atomically stores raw bytes and the reservation/txid association.
    // If it fails, nothing has been sent and the caller can safely abandon the proposal.
    let stored_txid = store_signed_pczt(db, account_uuid, pczt, orchard_vk, reservation_id)?;
    forget_reservation(reservation_id);
    // Extraction randomizes binding signatures. Broadcast only the stored bytes,
    // so the first attempt and every recovery attempt use identical authorizations.
    let raw = match raw_transaction(db, &stored_txid) {
        Ok(raw) => raw,
        Err(error) => {
            return Ok(SubmitOutcome::Unknown {
                txid: stored_txid,
                error,
            })
        }
    };
    let result = crate::network::broadcast(client, raw).await;
    Ok(record_submit_outcome(db, stored_txid, result))
}

/// 从钱包库取出已落库交易的原始字节，供重播使用。
pub fn raw_transaction(db: &Db, txid: &str) -> Result<Vec<u8>> {
    use zcash_primitives::transaction::TxId;

    let bytes = hex::decode(txid)
        .ok()
        .filter(|b| b.len() == 32)
        .ok_or_else(|| {
            RuntimeError::with(ErrorCode::TransactionBuildError, json!({ "stage": "txid" }))
        })?;
    // txid 字符串是显示序（大端），内部表示是小端。
    let mut internal = [0u8; 32];
    for (i, b) in bytes.iter().rev().enumerate() {
        internal[i] = *b;
    }

    let tx = db
        .get_transaction(TxId::from_bytes(internal))
        .map_err(|e| build_err("getTransaction", e))?
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "getTransaction", "txid": txid }),
            )
        })?;

    let mut raw = Vec::new();
    tx.write(&mut raw)
        .map_err(|e| build_err("serializeTransaction", e))?;
    Ok(raw)
}

/// Proof types supported by this wallet runtime.
pub const PROVE_ORCHARD: bool = true;
pub const PROVE_SAPLING: bool = false;
pub const PROVE_IRONWOOD: bool = true;

/// Clears all advisory locks for an account during explicit state repair.
/// Normal cancellation uses the durable owner-scoped `release_reservation`,
/// including after restart. This broader operation also clears other proposals
/// and must not be used as an automatic cancellation fallback.
pub fn clear_locked_outputs(db: &mut Db, account_uuid: &str) -> Result<u32> {
    use zcash_client_backend::data_api::OutputLockStore;

    let account_id = account::parse_account_id(db, account_uuid)?;
    let n = db
        .clear_locked_outputs(account_id)
        .map_err(|e| build_err("clearLockedOutputs", e))?;

    RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_account_uuid, _, _)| owner_account_uuid != account_uuid);
    });
    Ok(n as u32)
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod reservation_recovery_tests {
    use super::*;
    use rand::SeedableRng;
    use rand_chacha::ChaCha20Rng;
    use rusqlite::Connection;
    use zcash_client_sqlite::WalletDb;
    use zcash_protocol::consensus::Network;

    fn wallet(conn: Connection) -> Db {
        WalletDb::from_connection(
            conn,
            Network::TestNetwork,
            crate::clock::HostClock,
            ChaCha20Rng::from_seed([0; 32]),
        )
    }

    fn outcome_wallet(fail_update: bool) -> (Db, String) {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch("CREATE TABLE ext_onekey_tx_state (txid BLOB PRIMARY KEY, account_uuid BLOB, reservation_id TEXT, broadcast_state TEXT, rejection_code INTEGER, rejection_reason TEXT);").unwrap();
        conn.execute("INSERT INTO ext_onekey_tx_state (txid,account_uuid,reservation_id,broadcast_state) VALUES (?1,?2,'review','pending')", rusqlite::params![[1u8;32].as_slice(), uuid::Uuid::from_u128(1).as_bytes().as_slice()]).unwrap();
        if fail_update {
            conn.execute_batch("CREATE TRIGGER fail_state BEFORE UPDATE ON ext_onekey_tx_state BEGIN SELECT RAISE(ABORT,'synthetic write failure'); END;").unwrap();
        }
        (wallet(conn), "01".repeat(32))
    }

    #[test]
    fn both_first_and_retried_broadcasts_record_definitive_node_outcomes() {
        let (mut db, txid) = outcome_wallet(false);
        assert!(matches!(
            record_submit_outcome(&mut db, txid.clone(), Ok(())),
            SubmitOutcome::Accepted { .. }
        ));
        let rejected = RuntimeError::new(ErrorCode::BroadcastRejected).detail("bad-txns-expired");
        assert!(matches!(
            record_submit_outcome(&mut db, txid.clone(), Err(rejected)),
            SubmitOutcome::Rejected { .. }
        ));
        assert_eq!(
            tx_state::assert_broadcastable(&mut db, &txid)
                .unwrap_err()
                .code,
            ErrorCode::BroadcastRejected
        );
    }

    #[test]
    fn accepted_broadcast_with_failed_state_write_keeps_its_known_txid() {
        let (mut db, txid) = outcome_wallet(true);
        match record_submit_outcome(&mut db, txid.clone(), Ok(())) {
            SubmitOutcome::Unknown { txid: retained, .. } => assert_eq!(retained, txid),
            _ => panic!("an accepted transaction must not become a signing failure"),
        }
        tx_state::assert_broadcastable(&mut db, &txid).unwrap();
    }

    #[test]
    fn duplicate_spend_or_network_failure_keeps_the_persisted_send_pending() {
        for code in [ErrorCode::BroadcastOutcomeUnknown, ErrorCode::NetworkError] {
            let (mut db, txid) = outcome_wallet(false);
            assert!(matches!(
                record_submit_outcome(&mut db, txid.clone(), Err(RuntimeError::new(code))),
                SubmitOutcome::Unknown { .. }
            ));
            tx_state::assert_broadcastable(&mut db, &txid).unwrap();
        }
    }

    #[test]
    fn retry_cannot_use_another_accounts_reservation() {
        let (mut db, txid) = outcome_wallet(false);
        let owner = uuid::Uuid::from_u128(1).to_string();
        let other = uuid::Uuid::from_u128(2).to_string();
        db.transactionally_with_extension(|_, ext| {
            assert_eq!(
                tx_state::existing_txid_for_reservation(ext, "review", Some(&owner))?,
                Some(txid)
            );
            assert_eq!(
                tx_state::existing_txid_for_reservation(ext, "review", Some(&other))
                    .unwrap_err()
                    .code,
                ErrorCode::InvalidReservationId
            );
            Ok::<(), RuntimeError>(())
        })
        .unwrap();
    }

    #[test]
    fn restart_cancellation_releases_only_its_owner_and_preserves_finalized_locks() {
        let path =
            std::env::temp_dir().join(format!("zcash-reservation-{}.sqlite", uuid::Uuid::new_v4()));
        let owner = [1_u8; 32];
        let other_owner = [2_u8; 32];
        let conn = Connection::open(&path).unwrap();
        conn.execute_batch(
            "CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB);
                            CREATE TABLE ext_onekey_tx_state (txid BLOB, reservation_id TEXT, account_uuid BLOB);",
        )
        .unwrap();
        conn.execute("INSERT INTO transactions VALUES (1, ?1)", [[3_u8; 32]])
            .unwrap();
        for (table, index) in [
            ("sapling_received_notes", "output_index"),
            ("orchard_received_notes", "action_index"),
            ("ironwood_received_notes", "action_index"),
            ("transparent_received_outputs", "output_index"),
        ] {
            conn.execute_batch(&format!(
                "CREATE TABLE {table} (
                transaction_id INTEGER, {index} INTEGER, lock_owner BLOB, lock_expiry_height INTEGER
            );"
            ))
            .unwrap();
            conn.execute(
                &format!("INSERT INTO {table} VALUES (1, 0, ?1, 1000), (1, 1, ?2, 1000)"),
                rusqlite::params![owner, other_owner],
            )
            .unwrap();
        }
        drop(wallet(conn));

        let mut db = wallet(Connection::open(&path).unwrap());
        assert!(release_reservation(&mut db, &hex32(&owner)).unwrap());
        assert!(!release_reservation(&mut db, &hex32(&owner)).unwrap());
        let conn = Connection::open(&path).unwrap();
        for table in [
            "sapling_received_notes",
            "orchard_received_notes",
            "ironwood_received_notes",
            "transparent_received_outputs",
        ] {
            let locks: u32 = conn.query_row(&format!("SELECT COUNT(*) FROM {table} WHERE lock_owner = ?1 AND lock_expiry_height = 1000"), [other_owner], |row| row.get(0)).unwrap();
            assert_eq!(locks, 1);
            let released: u32 = conn.query_row(&format!("SELECT COUNT(*) FROM {table} WHERE lock_owner IS NULL AND lock_expiry_height IS NULL"), [], |row| row.get(0)).unwrap();
            assert_eq!(released, 1);
        }
        conn.execute(
            "INSERT INTO ext_onekey_tx_state (txid, reservation_id, account_uuid) VALUES (?1, ?2, ?3)",
            rusqlite::params![[4_u8; 32], hex32(&other_owner), uuid::Uuid::from_u128(1).as_bytes()],
        )
        .unwrap();
        assert!(!release_reservation(&mut db, &hex32(&other_owner)).unwrap());
        let locks: u32 = conn
            .query_row(
                "SELECT COUNT(*) FROM orchard_received_notes WHERE lock_owner = ?1",
                [other_owner],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(locks, 1);
        drop(conn);
        drop(db);
        std::fs::remove_file(path).unwrap();
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod policy_tests {
    use super::*;

    /// Sapling 在这两处都不是合法取值，理由不同但结论一样：证明建不出来，
    /// 而且找零会落进一个只读不可花的池子。
    #[test]
    fn sapling_is_not_a_spendable_source_nor_a_change_destination() {
        assert!(parse_change_pool("ironwood").is_ok());
        assert!(parse_change_pool("orchard").is_err());
        assert!(parse_change_pool("sapling").is_err());
        assert!(parse_spend_source(Some("sapling")).is_err());
    }

    /// 升级前后几块：本地链尖还在旧分支，交易已按新分支构造（或反过来）。
    #[test]
    fn the_proving_circuit_follows_the_transactions_branch_not_the_tip() {
        use orchard::circuit::OrchardCircuitVersion;
        use zcash_protocol::consensus::{BranchId, NetworkUpgrade, Parameters};
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let Some(activation) = params.activation_height(NetworkUpgrade::Nu6_3) else {
            return;
        };
        let before = activation - 1;
        assert_eq!(
            circuit_version_for_branch(&params, u32::from(BranchId::Nu6_3), before),
            OrchardCircuitVersion::PostNu6_3
        );
        assert_eq!(
            circuit_version_for_branch(&params, u32::from(BranchId::Nu6_1), activation),
            OrchardCircuitVersion::FixedPostNu6_2
        );
        assert_eq!(
            circuit_version_for_branch(&params, 0xdead_beef, activation),
            OrchardCircuitVersion::PostNu6_3,
            "unknown branch falls back to the tip"
        );
    }

    #[test]
    fn a_sapling_only_recipient_is_refused_before_building() {
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let ufvk = |seed: u8| {
            zcash_keys::keys::UnifiedSpendingKey::from_seed(
                &params,
                &[seed; 32],
                0u32.try_into().unwrap(),
            )
            .unwrap()
            .to_unified_full_viewing_key()
        };
        let key = ufvk(9);
        let sapling = key.sapling().unwrap().default_address().1;
        let encoded = zcash_keys::encoding::encode_payment_address_p(&params, &sapling);
        let parse = |s: &str| zcash_address::ZcashAddress::try_from_encoded(s).unwrap();
        assert!(recipient_needs_sapling_output(&params, &parse(&encoded)));
        let (ua, _) = key
            .default_address(zcash_keys::keys::UnifiedAddressRequest::AllAvailableKeys)
            .unwrap();
        assert!(
            !recipient_needs_sapling_output(&params, &parse(&ua.encode(&params))),
            "a UA with an Orchard receiver is fine"
        );
    }

    #[test]
    fn a_tex_recipient_is_recognised() {
        use zcash_address::ToAddress;
        let params = zcash_protocol::consensus::Network::MainNetwork;
        let tex = zcash_address::ZcashAddress::from_tex(
            zcash_protocol::consensus::NetworkType::Main,
            [9; 20],
        );
        let t1 = zcash_address::ZcashAddress::from_transparent_p2pkh(
            zcash_protocol::consensus::NetworkType::Main,
            [9; 20],
        );
        // Pinned: the app decodes this same pair in JS (sdkZcash/index.test.ts).
        assert_eq!(tex.encode(), "tex1pyysjzgfpyysjzgfpyysjzgfpyysjzgfl7c006");
        assert_eq!(t1.encode(), "t1JhNuqypTPZu5vdcjsvGHzbTCrTd9Xwtru");
        assert!(recipient_is_tex(&params, &tex));
        assert!(!recipient_is_tex(&params, &t1));
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod combine_tests {
    use super::*;
    use pczt::roles::{
        creator::Creator, io_finalizer::IoFinalizer, signer::Signer,
        spend_finalizer::SpendFinalizer,
    };
    use transparent::address::TransparentAddress;
    use transparent::bundle::{OutPoint, TxOut};
    use zcash_primitives::transaction::builder::{BuildConfig, Builder};
    use zcash_protocol::consensus::{BlockHeight, Network};

    fn secret_key() -> secp256k1::SecretKey {
        secp256k1::SecretKey::from_slice(&[0x11; 32]).unwrap()
    }

    /// A transparent-only PCZT, io-finalized. `extra` appends a second
    /// input/output pair; the first pair stays byte-identical so the two PCZTs
    /// still merge.
    ///
    /// Transparent-only is deliberate. `IoFinalizer` clears the three
    /// `tx_modifiable` flags ONLY when the transaction has shielded spends or
    /// outputs (io_finalizer/mod.rs:70), so a transparent-only PCZT stays
    /// modifiable and the bare Combiner will happily append the other side's
    /// extra inputs, outputs and Orchard actions.
    fn transparent_pczt(send_zat: u64, recipient: u8, extra: bool) -> Vec<u8> {
        let secp = secp256k1::Secp256k1::new();
        let pubkey = secp256k1::PublicKey::from_secret_key(&secp, &secret_key());
        let from = TransparentAddress::from_pubkey(&pubkey);
        let mut builder = Builder::new(
            Network::MainNetwork,
            BlockHeight::from_u32(3_000_000),
            BuildConfig::Standard {
                sapling_anchor: None,
                orchard_anchor: None,
                ironwood_anchor: None,
                orchard_padding: BundlePadding::DEFAULT,
                ironwood_padding: BundlePadding::DEFAULT,
            },
        );
        let input = |index: u8, value: u64, builder: &mut Builder<Network, ()>| {
            builder
                .add_transparent_p2pkh_input(
                    pubkey,
                    OutPoint::new([index; 32], 0),
                    TxOut::new(Zatoshis::const_from_u64(value), from.script().into()),
                )
                .unwrap();
        };
        input(7, 100_000, &mut builder);
        if extra {
            input(9, 50_000, &mut builder);
        }
        builder
            .add_transparent_output(
                &TransparentAddress::PublicKeyHash([recipient; 20]),
                Zatoshis::const_from_u64(send_zat),
            )
            .unwrap();
        if extra {
            builder
                .add_transparent_output(
                    &TransparentAddress::PublicKeyHash([0xcc; 20]),
                    Zatoshis::const_from_u64(50_000),
                )
                .unwrap();
        }
        let parts = builder
            .build_for_pczt(rand::rngs::OsRng, &Zip317FeeRule::standard())
            .unwrap();
        let pczt = Creator::build_from_parts(parts.pczt_parts).unwrap();
        IoFinalizer::new(pczt)
            .finalize_io()
            .unwrap()
            .serialize()
            .unwrap()
    }

    fn signed_copy(original: &[u8]) -> Vec<u8> {
        let mut signer = Signer::new(pczt::Pczt::parse(original).unwrap()).unwrap();
        for index in 0..pczt::Pczt::parse(original)
            .unwrap()
            .transparent()
            .inputs()
            .len()
        {
            signer.sign_transparent(index, &secret_key()).unwrap();
        }
        signer.finish().serialize().unwrap()
    }

    fn reason(error: &RuntimeError) -> String {
        format!("{error:?}")
    }

    #[test]
    fn combining_a_signature_copy_keeps_the_approved_transaction() {
        let original = transparent_pczt(90_000, 0xaa, false);
        let combined = combine_pczt(&original, &signed_copy(&original)).unwrap();
        let approved = Signer::new(pczt::Pczt::parse(&original).unwrap()).unwrap();
        let result = Signer::new(pczt::Pczt::parse(&combined).unwrap()).unwrap();
        assert_eq!(result.shielded_sighash(), approved.shielded_sighash());
    }

    // Conflicting values on a field both sides carry: the bare Combiner already
    // refuses this one. Pinned so a future upstream bump cannot quietly relax it.
    #[test]
    fn combine_rejects_a_conflicting_recipient() {
        let approved = transparent_pczt(90_000, 0xaa, false);
        let other = transparent_pczt(90_000, 0xbb, false);
        let error = combine_pczt(&approved, &signed_copy(&other)).unwrap_err();
        assert!(
            reason(&error).contains("DataMismatch"),
            "{}",
            reason(&error)
        );
    }

    // What actually stops a counterparty from appending an input, output or
    // Orchard action: `tx_modifiable` is already 0 on anything our pipeline
    // builds, and the Combiner refuses to grow a non-modifiable bundle. That is
    // upstream's behaviour, not ours, so pin it -- the sighash comparison in
    // `combine_pczt` is what still covers us if a future bump relaxes it.
    #[test]
    fn our_pczts_are_not_modifiable_so_the_combiner_refuses_to_grow_them() {
        let approved = transparent_pczt(90_000, 0xaa, false);
        let enlarged = transparent_pczt(90_000, 0xaa, true);
        let global = pczt::Pczt::parse(&approved).unwrap();
        let global = global.global();
        assert!(!global.inputs_modifiable());
        assert!(!global.outputs_modifiable());
        assert!(!global.shielded_modifiable());
        let error = combine_pczt(&approved, &signed_copy(&enlarged)).unwrap_err();
        assert!(
            reason(&error).contains("DataMismatch"),
            "{}",
            reason(&error)
        );
    }

    #[test]
    fn combine_rejects_a_copy_that_already_carries_a_script_sig() {
        // script_sig is absent from the sighash and absent from our original, so
        // the bare Combiner would adopt whatever the other side put there.
        let original = transparent_pczt(90_000, 0xaa, false);
        let finalized = SpendFinalizer::new(pczt::Pczt::parse(&signed_copy(&original)).unwrap())
            .finalize_spends()
            .unwrap()
            .serialize()
            .unwrap();
        let error = combine_pczt(&original, &finalized).unwrap_err();
        assert!(
            reason(&error).contains("transparentScriptSig"),
            "{}",
            reason(&error)
        );
    }
}

#[cfg(all(test, not(target_arch = "wasm32")))]
mod submit_tests {
    use super::*;

    #[test]
    fn first_submit_keeps_spent_inputs_unknown() {
        let spent = RuntimeError::with(
            ErrorCode::BroadcastOutcomeUnknown,
            json!({ "errorCode": -26 }),
        )
        .detail("bad-txns-inputs-spent".to_string());
        let rejection = first_submit_rejection(spent).unwrap_err();
        assert_eq!(rejection.code, ErrorCode::BroadcastOutcomeUnknown);
        assert_eq!(rejection.params["errorCode"], -26);
        assert_eq!(rejection.detail.as_deref(), Some("bad-txns-inputs-spent"));
    }

    #[test]
    fn first_submit_keeps_network_errors_unknown() {
        let error = RuntimeError::new(ErrorCode::NetworkError);
        assert_eq!(
            first_submit_rejection(error).unwrap_err().code,
            ErrorCode::NetworkError
        );
    }
}

#[cfg(test)]
mod memo_tests {
    use super::*;
    #[test]
    fn text_and_hex_fields_preserve_bytes_and_reject_ambiguous_input() {
        let text = parse_memo(Some(" 中文\n🙂 "), None).unwrap().unwrap();
        let hex = hex::encode(text.as_array());
        assert_eq!(parse_memo(None, Some(&hex)).unwrap().unwrap(), text);
        let binary = format!("ff000102{}", "00".repeat(508));
        assert_eq!(
            hex::encode(parse_memo(None, Some(&binary)).unwrap().unwrap().as_array()),
            binary
        );
        assert!(parse_memo(Some("text"), Some(&hex)).is_err());
        assert!(parse_memo(None, Some("ff00")).is_err());
        assert!(parse_memo(None, Some(&"zz".repeat(512))).is_err());
        assert!(parse_memo(Some(&"🙂".repeat(129)), None).is_err());
        assert!(parse_memo(None, None).unwrap().is_none());
    }
}
