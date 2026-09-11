//! 发送：PCZT 的构造与广播。
//!
//! 拆成四段是刻意的 —— **签名那一段不在本 crate**：
//!
//! ```text
//!   runtime.pcztCreate()          构造（要钱包状态：选币、witness、anchor）
//!   runtime.pcztProve()           证明，纯计算，不碰私钥
//!   keys.pcztSign()               签名（要花费密钥，在 onekey-zcash-keys 里）
//!   runtime.pcztSend()            终结 + 写回本地钱包（**不发网络**）
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

/// 把「余额不足」补成「钱在透明池里，先屏蔽」。
///
/// 普通转账不花透明 UTXO（见 `SendPolicy::spend_transparent`），所以一个只有
/// 透明余额的账户拿到的原始错误是 `INSUFFICIENT_FUNDS { availableZat: 0 }` ——
/// 而用户正看着账上有钱。那个 0 说的是「屏蔽池里可花的」，但错误里没有任何
/// 东西提到池，用户只能去猜。
///
/// 这里查一次余额，把差额和透明余额一起放进错误。查询失败就原样返回原错误：
/// 补充信息拿不到不该让整个操作换一个错因。
fn explain_insufficient(
    db: &Db,
    account_id: <Db as WalletRead>::AccountId,
    err: RuntimeError,
) -> RuntimeError {
    use zcash_client_backend::data_api::wallet::ConfirmationsPolicy;

    if err.code != ErrorCode::InsufficientFunds {
        return err;
    }

    let Ok(Some(summary)) = db.get_wallet_summary(ConfirmationsPolicy::default()) else {
        return err;
    };
    let Some(balances) = summary.account_balances().get(&account_id) else {
        return err;
    };

    let regular = u64::from(balances.unshielded_regular_balance().total());
    let coinbase = u64::from(balances.unshielded_coinbase_balance().total());

    // 透明池为空时也要走完下面这段。早退会让同一个错误码出现两种形状 ——
    // 空账户拿到上游原始的 `availableZat`，有钱的账户拿到补全过的字段，
    // 宿主得写两套解析，而两者的差别恰恰是最难在测试里注意到的。
    let num = |k: &str| err.params.get(k).and_then(|v| v.as_u64());
    let shortfall = num("shortfallZat").unwrap_or(0);

    // 三种结局要分开，因为用户该做的事完全不同：
    //
    // - 普通透明余额补得上：这次没花它（宿主把 spendTransparent 关了），
    //   出路是屏蔽，或者允许花透明。
    // - 只有 coinbase 补得上：本钱包**没有任何出口** —— 转账走 NonCoinbase、
    //   屏蔽走 NonCoinbaseOnly，两条都绕开它。这时说「先屏蔽」是把人往死路指。
    // - 都补不上：就是钱不够，但仍要把各池余额报全，用户才不用自己去对
    //   为什么账上的数和错误里的数对不上。
    // shortfall 为 0 时说明缺口读不出来（上游没给这个字段），无从判断哪种情形，
    // 保持原样比猜一个更好 —— 否则 `0 >= 0` 会把它误判成「先屏蔽」。
    let code = if shortfall == 0 {
        ErrorCode::InsufficientFunds
    } else if regular >= shortfall {
        ErrorCode::FundsNeedShielding
    } else if regular + coinbase >= shortfall {
        ErrorCode::CoinbaseFundsUnspendable
    } else {
        ErrorCode::InsufficientFunds
    };

    RuntimeError::with(
        code,
        json!({
            "stage": err.params.get("stage").cloned(),
            "requiredZat": num("requiredZat"),
            "shieldedAvailableZat": num("availableZat"),
            "shortfallZat": shortfall,
            "transparentZat": regular,
            // coinbase 单列：成熟规则不同，而且只能走专门的 coinbase 屏蔽路径，
            // `pcztShield` 碰不到它。并成一个数的话，用户会照着提示去点屏蔽，
            // 然后发现钱没动。
            "transparentCoinbaseZat": coinbase,
        }),
    )
}

/// 解析宿主传来的找零池名。构造与报价共用，避免两处各写一份而写歪。
pub fn parse_change_pool(name: &str) -> Result<ShieldedPool> {
    match name {
        // Orchard 自 NU6.3 起是只出不进的单向闸门 —— 找零进不去。
        // 把它留作合法取值只为历史兼容，新交易不该用。
        "ironwood" => Ok(ShieldedPool::Ironwood),
        "orchard" => Ok(ShieldedPool::Orchard),
        "sapling" => Ok(ShieldedPool::Sapling),
        other => Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "fallbackChangePool", "value": other }),
        )),
    }
}

/// 解析宿主传来的「只从这个池子花」。`None` 表示不限制（见
/// `SendPolicy::spend_source`）。
///
/// 与 `parse_change_pool` 分开而不是复用：那里 sapling 是合法取值（历史找零
/// 目标），这里不是。本方案不发 Sapling 证明，接受 `"sapling"` 只会把一个
/// 必然在证明阶段失败的请求放进来，还让调用方以为可行。
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
    /// 普通转账允不允许花透明 UTXO。
    ///
    /// **这是隐私策略，由宿主决定。** 花透明输入会把被选中的透明地址在链上
    /// 关联起来；上游因此把它做成显式 opt-in，默认一个透明 UTXO 都不花。
    ///
    /// 关着的后果是具体的：一个只有透明余额的账户**发不出任何款**，
    /// 唯一出口是先 `pcztShield` 屏蔽（多一笔交易、多一份手续费、多一次等待）。
    pub spend_transparent: bool,
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

/// 一次转账提案。`create_pczt` 与 `quote` 共用 —— 两者的选币、费率、找零策略
/// 必须完全一致，否则报价和实际构造会对不上，用户看到的费用就是假的。
type TransferProposal =
    zcash_client_backend::proposal::Proposal<Zip317FeeRule, zcash_client_sqlite::ReceivedNoteId>;
type ShieldingProposal =
    zcash_client_backend::proposal::Proposal<Zip317FeeRule, std::convert::Infallible>;

fn build_proposal(
    db: &mut Db,
    account_uuid: &str,
    to_address: &str,
    amount_zat: u64,
    memo: Option<&str>,
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

    let memo_bytes = match memo {
        None => None,
        Some(m) => {
            let parsed = zcash_protocol::memo::Memo::from_bytes(m.as_bytes()).map_err(|e| {
                RuntimeError::with(ErrorCode::InvalidMemo, json!({ "byteLen": m.len() })).detail(e)
            })?;
            Some(MemoBytes::from(&parsed))
        }
    };

    // Payment::new 会自己校验「透明地址不能带 memo」这类约束。
    let payment = zip321::Payment::new(recipient, Some(amount), memo_bytes, None, None, vec![])
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

    // 花费来源策略。默认（SpendPolicy::default）**一个透明 UTXO 都不花** ——
    // 于是只有透明余额的账户会以 INSUFFICIENT_FUNDS 收场，且 availableZat 报 0。
    // 打开它就允许从本账户的透明 receiver 取，代价是被选中的地址在链上互相关联。
    let spend_policy = {
        use zcash_client_backend::data_api::wallet::input_selection::{
            SpendPolicy, TransparentSpendPolicy,
        };
        // 不用 SpendPolicy::default()：它允许 sapling。见 SendPolicy::spend_source。
        // 屏蔽池之间永不并池（docs/08），而"当前池"只在宿主一处定义
        // （ZCASH_CURRENT_SHIELDED_POOL）：这里不藏缺省，缺参直接拒绝。
        let pool = policy.spend_source.ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::TransactionBuildError,
                json!({ "stage": "spendSource", "reason": "required" }),
            )
        })?;
        let base = SpendPolicy::shielded_pools([pool]);
        if policy.spend_transparent {
            base.with_transparent(TransparentSpendPolicy::any_account_addr())
        } else {
            base
        }
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

    // db 的可变借用到这里就结束了，所以失败路径上可以再查一次余额，
    // 把「余额不足」补成「钱在透明池里」。
    let proposal = match proposal {
        Ok(p) => p,
        Err(e) => {
            return Err(explain_insufficient(
                db,
                account_id,
                propose_err("proposeTransfer", e),
            ))
        }
    };

    Ok(proposal)
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
    memo: Option<&str>,
    policy: SendPolicy,
) -> Result<String> {
    let proposal = build_proposal(db, account_uuid, to_address, amount_zat, memo, policy, None)?;
    let fee: u64 = proposal
        .steps()
        .iter()
        .map(|s| u64::from(s.balance().fee_required()))
        .sum();

    // 这笔钱实际从哪儿取。宿主要给出「会花掉你的透明余额」这类提醒，就得先知道
    // 答案 —— 而在真正构造之前，只有提案知道。光给手续费的话，宿主只能拿一个
    // 布尔开关去问用户，而用户没有任何依据来回答。
    let mut transparent_in = 0u64;
    let mut shielded_in = 0u64;
    for step in proposal.steps() {
        transparent_in += step
            .transparent_inputs()
            .iter()
            .map(|o| u64::from(o.txout().value()))
            .sum::<u64>();
        if let Some(inputs) = step.shielded_inputs() {
            shielded_in += inputs
                .notes()
                .iter()
                .map(|n| u64::from(n.note().value()))
                .sum::<u64>();
        }
    }

    // 用了透明输入时，顺手算一遍「只用屏蔽余额」要多少手续费。
    //
    // 这不是给运行时用来做决定的 —— 它只是把选择的代价摆出来。透明输入按
    // ZIP-317 各自计入 logical action，实测同一笔付款用透明要 20000、只用屏蔽
    // 要 10000。差一倍的事不告诉用户，用户就没法评估。
    //
    // 屏蔽余额本来就不够时提不出方案，此时报 null，宿主别显示这条建议。
    let shielded_only_fee = if transparent_in > 0 {
        let mut alt = policy;
        alt.spend_transparent = false;
        build_proposal(db, account_uuid, to_address, amount_zat, memo, alt, None)
            .ok()
            .map(|p| {
                p.steps()
                    .iter()
                    .map(|s| u64::from(s.balance().fee_required()))
                    .sum::<u64>()
            })
    } else {
        None
    };

    Ok(json!({
        "feeZat": fee,
        "steps": proposal.steps().len(),
        "sourceTransparentZat": transparent_in,
        "sourceShieldedZat": shielded_in,
        // 同一笔里既花透明又花屏蔽 —— 链上因此把这个 t 地址和一次屏蔽花费绑在
        // 一起。这是花透明输入唯一**新增**的暴露：只花透明的话，对外和一笔普通
        // 屏蔽交易没有区别（透明进、屏蔽出）。值得单独提醒的就是这一种。
        "linksTransparentToShielded": transparent_in > 0 && shielded_in > 0,
        // 只用屏蔽余额的手续费；null = 屏蔽余额不够，提不出这个方案。
        "shieldedOnlyFeeZat": shielded_only_fee,
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
        std::collections::HashMap<String, (String, TransferProposal)>,
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
        tx_state::existing_txid_for_reservation(ext, reservation_id)
    })?;
    if finalized_txid.is_some() {
        forget_reservation(reservation_id);
        return Ok(false);
    }

    // 转账与屏蔽的提案类型不同，分两张表存；两边都要查，否则取消屏蔽时锁放不开。
    if let Some((account_uuid, proposal)) =
        RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id))
    {
        if let Err(e) = unlock_proposal_inputs(db, &proposal, owner) {
            RESERVATIONS.with(|r| {
                r.borrow_mut()
                    .insert(reservation_id.to_owned(), (account_uuid, proposal));
            });
            return Err(build_err("unlockProposalInputs", format!("{e:?}")));
        }
        return Ok(true);
    }
    if let Some((account_uuid, proposal)) =
        SHIELD_RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id))
    {
        if let Err(e) = unlock_proposal_inputs(db, &proposal, owner) {
            SHIELD_RESERVATIONS.with(|r| {
                r.borrow_mut()
                    .insert(reservation_id.to_owned(), (account_uuid, proposal));
            });
            return Err(build_err("unlockProposalInputs", format!("{e:?}")));
        }
        return Ok(true);
    }
    Ok(false)
}

/// 交易已落库后丢弃 reservation 记录。
///
/// 此时锁已由花费本身接管（note 真的被花掉了），不需要也不应该再解锁 ——
/// 解锁会把已经花出去的 note 重新放回可选池。
pub fn forget_reservation(reservation_id: &str) {
    RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id));
    SHIELD_RESERVATIONS.with(|r| r.borrow_mut().remove(reservation_id));
}

/// Whether this process still owns an in-flight proposal for the account.
/// Account repair must not delete the account while such a proposal exists:
/// the caller still holds its reservation id and may be proving or signing it.
pub fn has_active_reservations(account_uuid: &str) -> bool {
    RESERVATIONS.with(|reservations| {
        reservations
            .borrow()
            .values()
            .any(|(owner_account_uuid, _)| owner_account_uuid == account_uuid)
    }) || SHIELD_RESERVATIONS.with(|reservations| {
        reservations
            .borrow()
            .values()
            .any(|(owner_account_uuid, _)| owner_account_uuid == account_uuid)
    })
}

/// 构造一笔转账的 PCZT（未签名），并**锁住选中的 note**。
///
/// 返回 `{ pcztHex, reservationId }`：`reservationId` 是这批 note 的占用凭证，
/// 调用方必须一直带着它，直到交易发出（`pcztSend`）或用户取消（`releaseReservation`）。
/// 不释放也不会永久卡死 —— 锁有按区块高度的过期时间，但那是兜底，不是正常路径。
///
/// `lock_for_blocks` 是策略：它要覆盖「构造完成 → 交易落库」的最坏耗时。
/// 太短则用户还在审核页锁就过期了，太长则取消后 note 白白冻着。由宿主决定。
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
    memo: Option<&str>,
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
        r.borrow_mut()
            .insert(reservation_id.clone(), (account_uuid.to_owned(), proposal));
    });
    Ok((bytes, reservation_id, fee_zat))
}

/// 用哪个电路版本证明，由**共识分支**决定，不是产品选项。
///
/// 这里从网络参数与当前链尖推导：链尖过了 NU6.3 激活高度就用 PostNu6_3，
/// 否则用 FixedPostNu6_2。`InsecurePreNu6_2` 永远不用于证明（上游明确标注）。
fn circuit_version_for(
    params: &zcash_protocol::consensus::Network,
    chain_tip: zcash_protocol::consensus::BlockHeight,
) -> orchard::circuit::OrchardCircuitVersion {
    use zcash_protocol::consensus::{NetworkUpgrade, Parameters};
    match params.activation_height(NetworkUpgrade::Nu6_3) {
        Some(height) if chain_tip >= height => orchard::circuit::OrchardCircuitVersion::PostNu6_3,
        _ => orchard::circuit::OrchardCircuitVersion::FixedPostNu6_2,
    }
}

/// 给 PCZT 生成 Orchard / Ironwood 零知识证明。
/// 合并两份同一笔交易的 PCZT（本地完整副本 + 外部签名器返回的脱敏副本）。
pub fn combine_pczt(original: &[u8], signed: &[u8]) -> Result<Vec<u8>> {
    let parse = |bytes: &[u8], which: &str| {
        pczt::Pczt::parse(bytes).map_err(|error| {
            RuntimeError::with(
                ErrorCode::PcztError,
                json!({ "stage": "parse", "which": which }),
            )
            .detail(format!("{error:?}"))
        })
    };
    let original = parse(original, "original")?;
    let signed = parse(signed, "signed")?;
    let combined = pczt::roles::combiner::Combiner::new(vec![original, signed])
        .combine()
        .map_err(|error| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "combine" }))
                .detail(format!("{error:?}"))
        })?;
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

/// Pure proving: no wallet, database, scan, or storage initialization.
pub fn prove_pczt_at_height(
    params: &zcash_protocol::consensus::Network,
    chain_tip: zcash_protocol::consensus::BlockHeight,
    pczt_bytes: &[u8],
) -> Result<Vec<u8>> {
    let version = circuit_version_for(params, chain_tip);
    let pczt = pczt::Pczt::parse(pczt_bytes).map_err(|error| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{error:?}"))
    })?;
    let prover = pczt::roles::prover::Prover::new(pczt);

    let prover = if prover.requires_ironwood_proof() {
        let proving_key = orchard::circuit::ProvingKey::build(version);
        prover
            .create_ironwood_proof(&proving_key)
            .map_err(|error| {
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
        let proving_key = orchard::circuit::ProvingKey::build(version);
        prover.create_orchard_proof(&proving_key).map_err(|error| {
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

/// 终结已签名的 PCZT，提取交易并写回钱包，返回 txid。
///
/// 写回是必要的：钱包要立刻知道这笔花费占用了哪些 note，
/// 否则下一笔可能重复选中同一批 —— `zcash_client_sqlite` 的 note locking 负责这件事。
///
/// **不校验零知识证明。** 上游允许在此处传入 verifying key 做一次证明校验，
/// 但那需要现场构建 Orchard 电路（很贵），而这个 PCZT 是本机刚构造并签名的 ——
/// 校验它等于校验自己。若将来接受外部传入的 PCZT（多签、离线签名机），
/// 必须在这里补上校验，否则会把无效证明写进钱包。
pub fn extract_and_store(
    db: &mut Db,
    account_uuid: &str,
    pczt_bytes: &[u8],
    reservation_id: &str,
) -> Result<String> {
    let pczt = pczt::Pczt::parse(pczt_bytes).map_err(|e| {
        RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "parse" }))
            .detail(format!("{e:?}"))
    })?;

    db.transactionally_with_extension(|wallet, ext| {
        if let Some(txid) = tx_state::existing_txid_for_reservation(ext, reservation_id)? {
            return Ok(txid);
        }
        let txid =
            extract_and_store_transaction_from_pczt::<_, zcash_client_sqlite::ReceivedNoteId>(
                wallet, pczt, None, None,
            )
            .map_err(|e| build_err("extractAndStore", format!("{e:?}")))?
            .to_string();
        tx_state::insert_finalized(ext, &txid, account_uuid, reservation_id)?;
        Ok(txid)
    })
}

/// 从钱包库取出已提取交易的原始字节，供广播使用。
///
/// `extract_and_store` 只落库、不发网络，广播是**独立的一步**：这样宿主可以在
/// 落库之后、真正发出去之前插入确认环节，也能在广播失败后原样重试而不必重建交易。
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

/// 构造一笔「屏蔽全部透明余额」的 PCZT，并锁住选中的透明 UTXO。
///
/// ## 为什么它不是普通转账
///
/// 钱落在 t 地址那一刻就和用户公开绑定了。屏蔽是把它搬进隐私池，
/// 让之后的花费别人看不见。所以它：
///
/// - **没有收款地址** —— 收款方就是本账户自己的屏蔽地址
/// - **没有金额** —— 扫走全部透明余额。留零头等于白搭：它照样公开，
///   将来还要再付一次手续费才能搬走
///
/// `shielding_threshold` 是宿主的策略：透明余额低于它就不值得屏蔽（手续费可能
/// 超过金额本身）。低于阈值时上游报「余额不足」，我们照常映射成
/// `INSUFFICIENT_FUNDS`，宿主可据此提示「金额太小，暂不值得屏蔽」。
///
/// 只屏蔽非 coinbase 输出：coinbase 有单独的成熟度规则，上游要求走
/// `propose_shielding_coinbase`。矿工场景不在本钱包的目标内。
fn build_shielding_proposal(
    db: &mut Db,
    account_uuid: &str,
    shielding_threshold_zat: u64,
    policy: SendPolicy,
    lock: Option<LockRequest>,
) -> Result<ShieldingProposal> {
    use zcash_client_backend::data_api::wallet::propose_shielding;
    use zcash_client_backend::data_api::CoinbaseFilter;

    let params = *db.params();
    let account_id = account::parse_account_id(db, account_uuid)?;

    let threshold = Zatoshis::from_u64(shielding_threshold_zat).map_err(|_| {
        RuntimeError::with(
            ErrorCode::AmountOutOfRange,
            json!({ "valueZat": shielding_threshold_zat }),
        )
    })?;

    // 本账户名下全部透明收款地址（含找零与独立地址）—— 屏蔽是「扫干净」，
    // 漏掉任何一个地址都会留下公开残额。
    let receivers = db
        .get_transparent_receivers(account_id, true, true)
        .map_err(|e| build_err("getTransparentReceivers", e))?;
    if receivers.is_empty() {
        return Err(RuntimeError::with(
            ErrorCode::InsufficientFunds,
            json!({ "availableZat": 0, "requiredZat": shielding_threshold_zat, "reason": "noTransparentAddress" }),
        ));
    }
    let from_addrs: Vec<_> = receivers.keys().copied().collect();

    let confirmations = account::confirmations_policy(
        policy.trusted,
        policy.untrusted,
        policy.allow_zero_conf_shielding,
    )?;
    let selector = GreedyInputSelector::<Db>::new();
    let change = SingleOutputChangeStrategy::<_, Db>::new(
        Zip317FeeRule::standard(),
        None,
        policy.change_pool(),
        DustOutputPolicy::default(),
    );

    propose_shielding::<Db, _, _, _, zcash_client_sqlite::wallet::commitment_tree::Error>(
        db,
        &params,
        &selector,
        &change,
        threshold,
        &from_addrs,
        account_id,
        confirmations,
        CoinbaseFilter::NonCoinbaseOnly,
        lock,
    )
    .map_err(|e| propose_err("proposeShielding", e))
}

/// Exact ZIP-317 fee for the same sweep proposal used by `create_shielding_pczt`.
/// It does not lock transparent UTXOs or construct a PCZT.
pub fn quote_shielding(
    db: &mut Db,
    account_uuid: &str,
    shielding_threshold_zat: u64,
    policy: SendPolicy,
) -> Result<u64> {
    let proposal =
        build_shielding_proposal(db, account_uuid, shielding_threshold_zat, policy, None)?;
    Ok(proposal
        .steps()
        .iter()
        .map(|step| u64::from(step.balance().fee_required()))
        .sum())
}

pub fn create_shielding_pczt(
    db: &mut Db,
    account_uuid: &str,
    shielding_threshold_zat: u64,
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
    let proposal = build_shielding_proposal(
        db,
        account_uuid,
        shielding_threshold_zat,
        policy,
        Some(LockRequest::new(owner, lock_for_blocks)),
    )?;
    let fee_zat = proposal
        .steps()
        .iter()
        .map(|step| u64::from(step.balance().fee_required()))
        .sum();

    // 同 create_pczt：提案已锁 note，之后失败必须先解锁再返回。
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
        .map_err(|e| build_err("createShieldingPczt", format!("{e:?}")))?;

        pczt.serialize().map_err(|e| {
            RuntimeError::with(ErrorCode::PcztError, json!({ "stage": "serialize" }))
                .detail(format!("{e:?}"))
        })
    })();

    let bytes = match built {
        Ok(b) => b,
        Err(e) => {
            let _ = unlock_proposal_inputs(db, &proposal, owner);
            return Err(e);
        }
    };

    SHIELD_RESERVATIONS.with(|r| {
        r.borrow_mut()
            .insert(reservation_id.clone(), (account_uuid.to_owned(), proposal));
    });
    Ok((bytes, reservation_id, fee_zat))
}

// 屏蔽提案的 NoteRef 类型与转账不同（Infallible），所以单独存一张表。
thread_local! {
    static SHIELD_RESERVATIONS: std::cell::RefCell<
        std::collections::HashMap<String, (String, ShieldingProposal)>,
    > = std::cell::RefCell::new(std::collections::HashMap::new());
}

/// 释放某账户当前被锁住的**全部** note。
///
/// ## 为什么需要它
///
/// `releaseReservation` 靠内存里的提案表定位要解锁哪些 note。app 或 offscreen
/// 重启后那张表就没了 —— 此时旧的 reservationId 既解不开锁，也无从知道锁了什么，
/// 那批 note 会一直冻到过期。
///
/// 这是宿主的「状态修复」入口：不需要 token，直接把该账户名下所有锁清掉。
/// 代价是会连带放开**正在进行中**的提案，所以只该由用户主动触发的修复动作调用，
/// 不能在正常流程里用。
pub fn clear_locked_outputs(db: &mut Db, account_uuid: &str) -> Result<u32> {
    use zcash_client_backend::data_api::OutputLockStore;

    let account_id = account::parse_account_id(db, account_uuid)?;
    let n = db
        .clear_locked_outputs(account_id)
        .map_err(|e| build_err("clearLockedOutputs", e))?;

    RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_account_uuid, _)| owner_account_uuid != account_uuid);
    });
    SHIELD_RESERVATIONS.with(|r| {
        r.borrow_mut()
            .retain(|_, (owner_account_uuid, _)| owner_account_uuid != account_uuid);
    });
    Ok(n as u32)
}
