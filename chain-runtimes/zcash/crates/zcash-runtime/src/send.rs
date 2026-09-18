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
            // 宿主的屏蔽碰不到它。并成一个数的话，用户会照着提示去点屏蔽，
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
    /// 唯一出口是先屏蔽（多一笔交易、多一份手续费、多一次等待）。
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
    spend_transparent: Option<bool>,
    allow_zero_conf_shielding: Option<bool>,
    spend_source: Option<&str>,
) -> Result<SendPolicy> {
    Ok(SendPolicy {
        trusted,
        untrusted,
        fallback_change_pool: parse_change_pool(fallback_change_pool)?,
        pad_orchard_bundle,
        spend_transparent: spend_transparent.unwrap_or(false),
        allow_zero_conf_shielding: allow_zero_conf_shielding.unwrap_or(false),
        spend_source: parse_spend_source(spend_source)?,
    })
}

/// 一次转账提案。`create_pczt` 与 `quote` 共用 —— 两者的选币、费率、找零策略
/// 必须完全一致，否则报价和实际构造会对不上，用户看到的费用就是假的。
type TransferProposal =
    zcash_client_backend::proposal::Proposal<Zip317FeeRule, zcash_client_sqlite::ReceivedNoteId>;

/// 这个收款地址收不收得进屏蔽池。
///
/// 不认识的 receiver 类型算「收得进」：UA 里可能有本 crate 的 `zcash_address`
/// 还不认识的池子，把它判成纯透明会拒掉一笔合法的屏蔽转账 —— 比放过一笔
/// 公开转账更糟。
fn recipient_accepts_shielded(
    params: &zcash_protocol::consensus::Network,
    recipient: &zcash_address::ZcashAddress,
) -> Result<bool> {
    use zcash_keys::address::Address;
    let parsed = Address::try_from_zcash_address(params, recipient.clone()).map_err(|e| {
        RuntimeError::with(
            ErrorCode::InvalidAddress,
            json!({ "stage": "recipientKind" }),
        )
        .detail(format!("{e:?}"))
    })?;
    Ok(match parsed {
        Address::Transparent(_) | Address::Tex(_) => false,
        Address::Sapling(_) => true,
        Address::Unified(ua) => ua.has_orchard() || ua.has_sapling() || !ua.unknown().is_empty(),
    })
}

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

    // 花透明输入换来的隐私代价，只有在收款方是屏蔽地址时才买到了东西：
    // 那笔钱进了池子。收款方只能收透明时，这笔交易从头到尾公开，被选中的
    // 透明地址还白白关联到了收款方 —— 用户为一个不存在的收益付了隐私。
    //
    // 宿主本来就只在收款方是屏蔽地址时才打开这个开关（见
    // `shouldPreferTransparentForShieldedSend`）。这里是拦宿主的 bug：
    // 这个开关的代价是用户看不见的，不能靠调用方自觉。
    if policy.spend_transparent && !recipient_accepts_shielded(&params, &recipient)? {
        return Err(RuntimeError::with(
            ErrorCode::TransactionBuildError,
            json!({ "stage": "spendTransparent", "reason": "transparentOnlyRecipient" }),
        ));
    }

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
        if tx_state::existing_txid_for_reservation(ext, reservation_id)?.is_some() {
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
            .retain(|_, (owner_account_uuid, _)| owner_account_uuid != account_uuid);
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

    #[test]
    fn restart_cancellation_releases_only_its_owner_and_preserves_finalized_locks() {
        let path =
            std::env::temp_dir().join(format!("zcash-reservation-{}.sqlite", uuid::Uuid::new_v4()));
        let owner = [1_u8; 32];
        let other_owner = [2_u8; 32];
        let conn = Connection::open(&path).unwrap();
        conn.execute_batch(
            "CREATE TABLE transactions (id_tx INTEGER PRIMARY KEY, txid BLOB);
                            CREATE TABLE ext_onekey_tx_state (txid BLOB, reservation_id TEXT);",
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
            "INSERT INTO ext_onekey_tx_state VALUES (?1, ?2)",
            rusqlite::params![[4_u8; 32], hex32(&other_owner)],
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
    use zcash_address::ZcashAddress;
    use zcash_keys::keys::{UnifiedAddressRequest, UnifiedSpendingKey};
    use zcash_protocol::consensus::Network;

    fn parse(encoded: &str) -> ZcashAddress {
        ZcashAddress::try_from_encoded(encoded).expect("a valid address")
    }

    /// Sapling 在这两处都不是合法取值，理由不同但结论一样：证明建不出来，
    /// 而且找零会落进一个只读不可花的池子。
    #[test]
    fn sapling_is_not_a_spendable_source_nor_a_change_destination() {
        assert!(parse_change_pool("ironwood").is_ok());
        assert!(parse_change_pool("orchard").is_ok());
        assert!(parse_change_pool("sapling").is_err());
        assert!(parse_spend_source(Some("sapling")).is_err());
    }

    #[test]
    fn only_a_shielded_recipient_justifies_spending_transparent_inputs() {
        let params = Network::MainNetwork;
        let taddr = ::transparent::address::TransparentAddress::PublicKeyHash([3; 20]);

        // 裸 t 地址、TEX 地址：整笔交易公开，花透明输入换不到任何隐私。
        let t_encoded = zcash_keys::encoding::encode_transparent_address_p(&params, &taddr);
        assert!(!recipient_accepts_shielded(&params, &parse(&t_encoded)).unwrap());

        let tex = zcash_keys::address::Address::Tex([3; 20])
            .to_zcash_address(&params)
            .encode();
        assert!(!recipient_accepts_shielded(&params, &parse(&tex)).unwrap());

        // UA：钱进了屏蔽池，这才是这个开关买到的东西。
        let ua = UnifiedSpendingKey::from_seed(
            &params,
            &[7u8; 32],
            0u32.try_into().expect("valid ZIP-32 account index"),
        )
        .expect("test seed derives a USK")
        .to_unified_full_viewing_key()
        .default_address(UnifiedAddressRequest::ALLOW_ALL)
        .expect("a default unified address")
        .0
        .encode(&params);
        assert!(recipient_accepts_shielded(&params, &parse(&ua)).unwrap());
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
