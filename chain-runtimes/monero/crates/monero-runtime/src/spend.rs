//! 花费路径：key image 导入、交易构造、签名、广播。
//!
//! ## 花费私钥在这里出现，且只在这里
//!
//! 它由宿主在每次调用时传进来，用完即擦，**永远不落库、不跨 wasm 边界返回**。
//! 想审计「私钥去了哪」，看这一个文件。
//!
//! ## 为什么需要导入 key image
//!
//! Monero 的观察钱包无法独立判断自己的输出有没有被花掉 —— 判断需要 key image，
//! 而 key image = `(花费私钥 + 输出偏移) × H(输出公钥)`，绕不开花费私钥。
//! 这是协议性质，不是实现取舍。
//!
//! 所以扫描只能记下输出，花费状态要等宿主在解锁时调一次
//! [`import_key_images`] 才补得齐。补齐之前余额会**偏高**（已花的看起来还在）。

use std::collections::HashMap;

use monero_wallet::{
    address::MoneroAddress,
    ed25519::{Point, Scalar},
    send::{Change, SignableTransaction},
    OutputWithDecoys, WalletOutput,
};
use rand_core::{CryptoRng, RngCore};
use rusqlite::{params, OptionalExtension};
use serde_json::{json, Value};
use zeroize::Zeroizing;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::keys;
use crate::query;
use crate::wallet::Db;

/// 环大小。当前共识值是 16（含真实成员）。
///
/// 写死而不是问节点：节点说小了会让交易在隐私上被削弱且可能被拒，
/// 这个值应当跟着硬分叉走，由我们发版控制。
pub const RING_LEN: u8 = 16;

/// 从花费私钥算出某笔输出的 key image。
///
/// 公式与上游 `SignableTransaction::sign` 内部一致：
/// `key_image = (spend + key_offset) × biased_hash(output_key)`。
///
/// 顺带校验了密钥是否真的对应这笔输出 —— 不对应就说明宿主传错了账户的种子，
/// 这时必须报错而不是算出一个无意义的 key image 存进库里（那会让花费检测
/// 永远撞不上，表现为「这笔钱花了但钱包还显示在」）。
fn key_image_for(output: &WalletOutput, spend: &Zeroizing<Scalar>) -> Result<[u8; 32]> {
    use core::ops::Deref;

    let spend_dalek: curve25519_dalek::Scalar = (**spend).into();
    let offset: curve25519_dalek::Scalar = output.key_offset().into();
    let input_key = Zeroizing::new(spend_dalek + offset);

    let derived =
        Point::from(input_key.deref() * curve25519_dalek::constants::ED25519_BASEPOINT_POINT);
    if derived.compress().to_bytes() != output.key().compress().to_bytes() {
        return Err(RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({
                "field": "spendPrivateKey",
                "reason": "doesNotMatchOutput",
            }),
        ));
    }

    let generator = Point::biased_hash(output.key().compress().to_bytes());
    let image =
        Point::from(input_key.deref() * curve25519_dalek::EdwardsPoint::from(generator.into()));
    Ok(image.compress().to_bytes())
}

/// 给还没有 key image 的输出补齐 key image。
///
/// 补齐之后花费检测才能工作。
///
/// 补齐之前已经发生的花费靠扫描时记下的「可能的花费」追认：一笔输出算出的
/// key image 若出现在它的某条可能花费里，那条就是真的花费。之后的花费由正常
/// 扫描识别，所以这笔输出的可能花费记录随即清掉。
pub fn import_key_images(
    db: &mut Db,
    identity_id: i64,
    spend_private: &[u8],
) -> Result<KeyImageImport> {
    let spend = parse_spend_key(spend_private)?;

    let pending: Vec<(i64, Vec<u8>, Vec<u8>, i64)> = {
        let mut stmt = db
            .conn()
            .prepare(
                "SELECT id, blob, txid, index_in_tx FROM outputs
                 WHERE identity_id = ?1 AND key_image IS NULL",
            )
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?;
        let rows = stmt
            .query_map([identity_id], |r| {
                Ok((r.get(0)?, r.get(1)?, r.get(2)?, r.get(3)?))
            })
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("listOutputsWithoutKeyImage", e))?
    };

    let mut computed = Vec::with_capacity(pending.len());
    for (id, blob, txid, index) in pending {
        let output = WalletOutput::read(&mut blob.as_slice())
            .map_err(|e| db_err("deserializeOutput", format!("输出 {id}: {e}")))?;
        computed.push((id, key_image_for(&output, &spend)?, txid, index));
    }
    apply_key_images(db, identity_id, &computed)
}

/// 写回算好的 key image，并用「可能的花费」追认早已发生的花费。
/// 与计算分开，好让比对逻辑脱离真实输出单独测试。
fn apply_key_images(
    db: &mut Db,
    identity_id: i64,
    computed: &[(i64, [u8; 32], Vec<u8>, i64)],
) -> Result<KeyImageImport> {
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginImportKeyImages", e))?;
    let mut spends_found = 0;
    for (id, image, txid, index) in computed {
        tx.execute(
            "UPDATE outputs SET key_image = ?1 WHERE id = ?2",
            params![image.as_slice(), id],
        )
        .map_err(|e| db_err("updateKeyImage", e))?;

        let spent: Option<(Vec<u8>, i64, Option<i64>)> = tx
            .query_row(
                "SELECT spend_txid, height, timestamp FROM possible_spends
                 WHERE identity_id = ?1 AND out_txid = ?2 AND out_index = ?3 AND key_image = ?4
                 ORDER BY height LIMIT 1",
                params![identity_id, txid, index, image.as_slice()],
                |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)),
            )
            .optional()
            .map_err(|e| db_err("matchPossibleSpend", e))?;
        if let Some((spend_txid, height, timestamp)) = spent {
            tx.execute(
                "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
                 WHERE id = ?3 AND spent_at_height IS NULL",
                params![height, spend_txid, id],
            )
            .map_err(|e| db_err("markPossibleSpend", e))?;
            tx.execute(
                "INSERT INTO transactions (identity_id, txid, height, timestamp, direction)
                 VALUES (?1, ?2, ?3, ?4, 'out')
                 ON CONFLICT (identity_id, txid) DO UPDATE
                   SET direction = 'out', height = ?3, failed = 0,
                       timestamp = COALESCE(excluded.timestamp, transactions.timestamp)",
                params![identity_id, spend_txid, height, timestamp],
            )
            .map_err(|e| db_err("recordPossibleSpend", e))?;
            spends_found += 1;
        }
        tx.execute(
            "DELETE FROM possible_spends
             WHERE identity_id = ?1 AND out_txid = ?2 AND out_index = ?3",
            params![identity_id, txid, index],
        )
        .map_err(|e| db_err("clearPossibleSpends", e))?;
    }
    tx.commit()
        .map_err(|e| db_err("commitImportKeyImages", e))?;

    Ok(KeyImageImport {
        imported: computed.len(),
        spends_found,
    })
}

/// `import_key_images` 的结果。
pub struct KeyImageImport {
    /// 这次补算了几笔输出的 key image。
    pub imported: usize,
    /// 其中几笔借「可能的花费」追认为早已花掉。
    pub spends_found: usize,
}

fn parse_spend_key(bytes: &[u8]) -> Result<Zeroizing<Scalar>> {
    let arr = <[u8; 32]>::try_from(bytes).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPrivateKey", "byteLen": bytes.len(), "expected": 32 }),
        )
    })?;
    let scalar = Scalar::read(&mut arr.as_slice()).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidKeyMaterial,
            json!({ "field": "spendPrivateKey", "reason": "notCanonicalScalar" }),
        )
    })?;
    Ok(Zeroizing::new(scalar))
}

/// 一笔待发交易的收款项。
#[derive(Debug, Clone)]
pub struct Payment {
    pub address: MoneroAddress,
    pub amount_atomic: u64,
}

/// 选币结果与预留。
#[derive(Debug, Clone)]
pub struct Selection {
    pub output_ids: Vec<i64>,
    pub total_atomic: u64,
}

/// 选币：大额优先，凑够 `needed` 为止。
///
/// 大额优先的理由是手续费：Monero 按交易重量计费，输入越少越便宜。代价是
/// 会更快把大额输出打碎 —— 这个取舍和 wallet2 的默认行为一致。
pub fn select_inputs(
    db: &Db,
    identity_id: i64,
    chain_tip: u64,
    needed_atomic: u64,
) -> Result<Selection> {
    let rows = query::spendable_rows(db, identity_id, chain_tip)?;

    let mut picked = Vec::new();
    let mut total: u64 = 0;
    for row in rows {
        picked.push(row.id);
        total = total.checked_add(row.amount_atomic).ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "amount", "reason": "totalOverflow" }),
            )
        })?;
        if total >= needed_atomic {
            break;
        }
    }

    if total < needed_atomic {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({
                "field": "amount",
                "reason": "insufficientUnlockedFunds",
                "availableAtomic": total.to_string(),
                "neededAtomic": needed_atomic.to_string(),
            }),
        ));
    }

    Ok(Selection {
        output_ids: picked,
        total_atomic: total,
    })
}

/// Unsigned review reservations expire; signed inputs remain held until resolved.
pub const RESERVATION_TTL_SECS: i64 = 60 * 60;

/// 仍然有效的预留的下限时间：`locked_at` 大于它才算数。
pub fn reservation_cutoff() -> i64 {
    crate::wallet::now_secs() - RESERVATION_TTL_SECS
}

/// 预留选中的输出，防止被第二笔在途交易重复选中。
///
/// 不预留的后果很具体：两笔交易花同一个输出，第二笔必然被网络拒绝，
/// 而用户看到的是「莫名其妙失败」。
pub fn reserve(db: &mut Db, reservation_id: &str, output_ids: &[i64]) -> Result<()> {
    let now = crate::wallet::now_secs();
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginReserve", e))?;
    // 用户在确认页退出不会通知这里，作废的预留只能在这时清掉。
    tx.execute(
        "DELETE FROM locked_outputs WHERE locked_at <= ?1 AND signed_at IS NULL",
        [now - RESERVATION_TTL_SECS],
    )
    .map_err(|e| db_err("expireReservations", e))?;
    for id in output_ids {
        // 已被别人预留时这里会因为主键冲突而失败 —— 那正是我们要的：
        // 宁可这笔构造不出来，也不要构造出一笔注定被拒的交易。
        tx.execute(
            "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, ?2, ?3)",
            params![id, reservation_id, now],
        )
        .map_err(|e| {
            db_err(
                "reserveOutput",
                format!("输出 {id} 可能已被另一笔在途交易预留: {e}"),
            )
        })?;
    }
    tx.commit().map_err(|e| db_err("commitReserve", e))
}

/// Atomically claim every live input before the host obtains a spend signature.
pub fn mark_signed(db: &mut Db, reservation_id: &str, expected_inputs: u32) -> Result<usize> {
    let stale = || {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({"reason": "staleReservation"}),
        )
    };
    if expected_inputs == 0 || expected_inputs > 1024 {
        return Err(stale());
    }
    let tx = db
        .conn_mut()
        .transaction()
        .map_err(|e| db_err("beginSignReservation", e))?;
    let count: u32 = tx
        .query_row(
            "SELECT count(*) FROM locked_outputs WHERE reservation_id = ?1",
            [reservation_id],
            |row| row.get(0),
        )
        .map_err(|e| db_err("readSignReservation", e))?;
    if count != expected_inputs {
        return Err(stale());
    }
    let marked = tx
        .execute(
            "UPDATE locked_outputs SET signed_at = ?2
         WHERE reservation_id = ?1 AND signed_at IS NULL AND locked_at > ?3
           AND output_id IN (SELECT id FROM outputs WHERE spent_at_height IS NULL)",
            params![
                reservation_id,
                crate::wallet::now_secs(),
                reservation_cutoff()
            ],
        )
        .map_err(|e| db_err("markReservationSigned", e))?;
    if u32::try_from(marked).map_err(|_| stale())? != expected_inputs {
        return Err(stale());
    }
    tx.commit()
        .map_err(|e| db_err("commitSignReservation", e))?;
    Ok(marked)
}

/// 放掉本身份所有没签名的预留。
///
/// 用户在确认页返回、改完再下一步时不会通知这里；新的一次准备就说明前一次
/// 没签名的已经没人要了。签了名的不动：它可能正在广播。
fn release_unsigned(db: &Db, identity_id: i64) -> Result<usize> {
    db.conn()
        .execute(
            "DELETE FROM locked_outputs
             WHERE signed_at IS NULL
               AND output_id IN (SELECT id FROM outputs WHERE identity_id = ?1)",
            [identity_id],
        )
        .map_err(|e| db_err("releaseUnsignedReservations", e))
}

/// 释放预留（用户取消，或构造失败）。
pub fn release(db: &Db, reservation_id: &str) -> Result<usize> {
    let n = db
        .conn()
        .execute(
            "DELETE FROM locked_outputs WHERE reservation_id = ?1",
            [reservation_id],
        )
        .map_err(|e| db_err("release", e))?;
    Ok(n)
}

/// 一笔构造完成、等待签名的交易。
pub struct Prepared {
    pub signable: SignableTransaction,
    pub output_ids: Vec<i64>,
    pub inputs_atomic: u64,
    /// 收款方实际收到的总额。max-send 时这是反算出来的，不等于宿主传入的值。
    pub amount_atomic: u64,
    pub fee_atomic: u64,
}

/// 给一个输出取一环诱饵。
///
/// 诱饵必须在**当前**链高上选：用旧高度会让环成员分布偏离当下的链上分布，
/// 那是可被统计分析利用的指纹。
/// monerod 的拒收原因按标志拼接，双花写作 "double spend"。
fn is_double_spend(error: &RuntimeError) -> bool {
    error
        .detail
        .as_deref()
        .is_some_and(|reason| reason.to_lowercase().contains("double spend"))
}

/// 一次构造里所有输入共用同一份 RingCT 输出分布。
///
/// 上游每选一组诱饵都向节点要一次整条链的分布（约 18MB），N 个输入就下 N 份，
/// 移动端经宿主转发很容易超时。范围相同就复用，节点只问一次。
struct CachedDecoys<'a, P> {
    inner: &'a P,
    distributions:
        std::sync::Mutex<HashMap<(std::ops::Bound<usize>, std::ops::Bound<usize>), Vec<u64>>>,
}

impl<'a, P> CachedDecoys<'a, P> {
    fn new(inner: &'a P) -> Self {
        Self {
            inner,
            distributions: std::sync::Mutex::new(HashMap::new()),
        }
    }
}

impl<P: monero_wallet::interface::ProvidesDecoys> monero_wallet::interface::ProvidesBlockchainMeta
    for CachedDecoys<'_, P>
{
    fn latest_block_number(
        &self,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<usize, monero_wallet::interface::InterfaceError>,
    > {
        self.inner.latest_block_number()
    }
}

impl<P: monero_wallet::interface::ProvidesDecoys>
    monero_wallet::interface::ProvidesUnvalidatedDecoys for CachedDecoys<'_, P>
{
    fn ringct_output_distribution(
        &self,
        range: impl Send + std::ops::RangeBounds<usize>,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<Vec<u64>, monero_wallet::interface::InterfaceError>,
    > {
        let key = (range.start_bound().cloned(), range.end_bound().cloned());
        async move {
            let cached = self
                .distributions
                .lock()
                .ok()
                .and_then(|map| map.get(&key).cloned());
            if let Some(distribution) = cached {
                return Ok(distribution);
            }
            let distribution =
                monero_wallet::interface::ProvidesDecoys::ringct_output_distribution(
                    self.inner, key,
                )
                .await?;
            if let Ok(mut map) = self.distributions.lock() {
                map.insert(key, distribution.clone());
            }
            Ok(distribution)
        }
    }

    fn unlocked_ringct_outputs(
        &self,
        indexes: &[u64],
        evaluate_unlocked: monero_wallet::interface::EvaluateUnlocked,
    ) -> impl Send
           + std::future::Future<
        Output = std::result::Result<
            Vec<Option<[Point; 2]>>,
            monero_wallet::interface::TransactionsError,
        >,
    > {
        monero_wallet::interface::ProvidesDecoys::unlocked_ringct_outputs(
            self.inner,
            indexes,
            evaluate_unlocked,
        )
    }
}

async fn ring_for<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    output_id: i64,
    chain_tip: u64,
) -> Result<OutputWithDecoys> {
    let output = query::load_output(db, output_id)?;
    OutputWithDecoys::new(rng, decoy_source, RING_LEN, chain_tip as usize, output)
        .await
        .map_err(|e| {
            RuntimeError::with(
                ErrorCode::NetworkError,
                json!({ "operation": "selectDecoys", "outputId": output_id }),
            )
            .detail(format!("{e:?}"))
        })
}

/// 取一组输出的环，已经取过的直接复用。
///
/// 复用不是优化细节：每个输入要十几个诱饵，重取一轮就是十几个网络往返。
async fn rings_for<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    output_ids: &[i64],
    chain_tip: u64,
    cache: &mut HashMap<i64, OutputWithDecoys>,
) -> Result<Vec<OutputWithDecoys>> {
    let mut inputs = Vec::with_capacity(output_ids.len());
    for id in output_ids {
        if !cache.contains_key(id) {
            let ring = ring_for(db, decoy_source, rng, *id, chain_tip).await?;
            cache.insert(*id, ring);
        }
        inputs.push(cache[id].clone());
    }
    Ok(inputs)
}

/// 组装一笔 `SignableTransaction`。找零永远回到本账户主地址。
fn assemble<R: RngCore + CryptoRng>(
    identity: &crate::wallet::Identity,
    rng: &mut R,
    rct_type: monero_wallet::ringct::RctType,
    inputs: Vec<OutputWithDecoys>,
    recipients: Vec<(MoneroAddress, u64)>,
    fee_rate: monero_wallet::interface::FeeRate,
) -> std::result::Result<SignableTransaction, monero_wallet::send::SendError> {
    let change = Change::new(identity.view_pair.clone(), None);

    // `outgoing_view_key` 给这笔交易的确定性 RNG 做种子。**绝不能跨交易复用**
    // —— 上游文档明写复用会导致临时秘密被重复使用。每次现取。
    let mut ovk = Zeroizing::new([0u8; 32]);
    rng.fill_bytes(ovk.as_mut());

    SignableTransaction::new(
        rct_type,
        ovk,
        inputs,
        recipients,
        change,
        Vec::new(),
        fee_rate,
    )
}

fn build_error(e: monero_wallet::send::SendError) -> RuntimeError {
    RuntimeError::with(ErrorCode::InvalidRequest, json!({ "field": "transaction" }))
        .detail(format!("{e:?}"))
}

/// 选币 + 构造，**不需要花费私钥**。
///
/// 返回的 `Prepared` 带着这笔交易真正要付的手续费，宿主据此在确认页上显示 ——
/// 显示的数就是会扣的数。在别处照抄一遍上游的重量公式只会让两个数慢慢分家。
///
/// ## 为什么要迭代
///
/// 手续费按交易重量算，重量取决于输入个数，而要凑几个输入又取决于手续费。
/// 先按「只够付金额」选一轮，让上游把真实手续费算出来（`NotEnoughFunds` 里
/// 带着它），不够就按新的目标再选。实测两轮收敛。
#[allow(clippy::too_many_arguments)]
pub async fn prepare<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    identity_id: i64,
    payments: &[Payment],
    fee_rate: monero_wallet::interface::FeeRate,
    chain_tip: u64,
    rct_type: monero_wallet::ringct::RctType,
) -> Result<Prepared> {
    use monero_wallet::send::SendError;

    let identity = db.identity_by_id(identity_id)?;
    let out_total = sum_payments(payments)?;
    let recipients: Vec<(MoneroAddress, u64)> = payments
        .iter()
        .map(|p| (p.address, p.amount_atomic))
        .collect();

    let mut cache: HashMap<i64, OutputWithDecoys> = HashMap::new();
    let mut needed = out_total;

    // 三轮：第一轮不含手续费，第二轮按真实手续费重选，第三轮兜住「补的输入
    // 又把手续费抬上去了」。再不收敛就是钱确实不够。
    for _ in 0..3 {
        let selection = select_inputs(db, identity_id, chain_tip, needed)?;
        let inputs = rings_for(
            db,
            decoy_source,
            rng,
            &selection.output_ids,
            chain_tip,
            &mut cache,
        )
        .await?;

        match assemble(
            &identity,
            rng,
            rct_type,
            inputs,
            recipients.clone(),
            fee_rate,
        ) {
            Ok(signable) => {
                let fee_atomic = signable.necessary_fee();
                return Ok(Prepared {
                    signable,
                    output_ids: selection.output_ids,
                    inputs_atomic: selection.total_atomic,
                    amount_atomic: out_total,
                    fee_atomic,
                });
            }
            Err(SendError::NotEnoughFunds {
                outputs,
                necessary_fee: Some(fee),
                ..
            }) => {
                // 上游算出了真实手续费，按它重新定目标。
                let next = outputs.checked_add(fee).ok_or_else(overflow)?;
                if next <= needed {
                    // 目标没往上走却仍然构不出来：再循环也是同一个结果。
                    return Err(insufficient(selection.total_atomic, next));
                }
                needed = next;
            }
            Err(e) => return Err(build_error(e)),
        }
    }

    Err(insufficient(0, needed))
}

/// 把这个账户当前能花的**全部**输出发给一个地址。
///
/// 不能靠「不给找零输出」来实现 —— 上游明写那样会把余数全部并进手续费，
/// 等于白送矿工。正确做法是先拿到这笔交易真实的手续费，再反算金额。
///
/// 金额不影响交易重量（RingCT 的金额是定长密文），所以先用 1 piconero 试
/// 构一次拿手续费，再用 `总额 - 手续费` 正式构造，两次重量一致。
#[allow(clippy::too_many_arguments)]
pub async fn prepare_max<R: RngCore + CryptoRng + Send + Sync>(
    db: &Db,
    decoy_source: &impl monero_wallet::interface::ProvidesDecoys,
    rng: &mut R,
    identity_id: i64,
    recipient: MoneroAddress,
    fee_rate: monero_wallet::interface::FeeRate,
    chain_tip: u64,
    rct_type: monero_wallet::ringct::RctType,
) -> Result<Prepared> {
    use monero_wallet::send::SendError;

    let identity = db.identity_by_id(identity_id)?;
    let rows = query::spendable_rows(db, identity_id, chain_tip)?;
    if rows.is_empty() {
        return Err(insufficient(0, 1));
    }

    let mut output_ids = Vec::with_capacity(rows.len());
    let mut inputs_atomic: u64 = 0;
    for row in &rows {
        output_ids.push(row.id);
        inputs_atomic = inputs_atomic
            .checked_add(row.amount_atomic)
            .ok_or_else(overflow)?;
    }

    let mut cache: HashMap<i64, OutputWithDecoys> = HashMap::new();
    let inputs = rings_for(db, decoy_source, rng, &output_ids, chain_tip, &mut cache).await?;

    // 试构一次只为拿手续费。成功失败都给得出：成功读 `necessary_fee()`，
    // 失败时 `NotEnoughFunds` 里也带着它。
    let fee_atomic = match assemble(
        &identity,
        rng,
        rct_type,
        inputs.clone(),
        vec![(recipient, 1)],
        fee_rate,
    ) {
        Ok(probe) => probe.necessary_fee(),
        Err(SendError::NotEnoughFunds {
            necessary_fee: Some(fee),
            ..
        }) => fee,
        Err(e) => return Err(build_error(e)),
    };

    let amount_atomic = inputs_atomic
        .checked_sub(fee_atomic)
        .filter(|a| *a > 0)
        .ok_or_else(|| insufficient(inputs_atomic, fee_atomic.saturating_add(1)))?;

    let signable = assemble(
        &identity,
        rng,
        rct_type,
        inputs,
        vec![(recipient, amount_atomic)],
        fee_rate,
    )
    .map_err(build_error)?;

    Ok(Prepared {
        signable,
        output_ids,
        inputs_atomic,
        amount_atomic,
        fee_atomic,
    })
}

fn sum_payments(payments: &[Payment]) -> Result<u64> {
    let mut total: u64 = 0;
    for payment in payments {
        total = total
            .checked_add(payment.amount_atomic)
            .ok_or_else(overflow)?;
    }
    Ok(total)
}

fn overflow() -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({ "field": "amount", "reason": "totalOverflow" }),
    )
}

fn insufficient(available: u64, needed: u64) -> RuntimeError {
    RuntimeError::with(
        ErrorCode::InvalidRequest,
        json!({
            "field": "amount",
            "reason": "insufficientUnlockedFunds",
            "availableAtomic": available.to_string(),
            "neededAtomic": needed.to_string(),
        }),
    )
}

/// 签名一笔已构造的交易。**不碰网络，不碰数据库。**
///
/// `spend_private` 用完即擦。返回可直接广播的交易字节与 txid。
pub fn sign_prepared<R: RngCore + CryptoRng>(
    signable: SignableTransaction,
    rng: &mut R,
    spend_private: &[u8],
) -> Result<(Vec<u8>, [u8; 32])> {
    let spend = parse_spend_key(spend_private)?;
    let signed = signable.sign(rng, &spend).map_err(|e| {
        RuntimeError::with(ErrorCode::InvalidRequest, json!({ "field": "signature" }))
            .detail(format!("{e:?}"))
    })?;
    let txid = signed.hash();
    Ok((signed.serialize(), txid))
}

/// 广播。**不可逆。**
pub async fn broadcast(daemon: &crate::network::Daemon, raw: &[u8]) -> Result<()> {
    use monero_daemon_rpc::prelude::*;

    let tx = monero_wallet::transaction::Transaction::read(&mut &*raw)
        .map_err(|e| db_err("parseSignedTransaction", e))?;
    daemon.publish_transaction(&tx).await.map_err(|e| match e {
        PublishTransactionError::TransactionRejected(reason) => {
            RuntimeError::new(ErrorCode::BroadcastRejected).detail(reason)
        }
        PublishTransactionError::InterfaceError(e) => RuntimeError::with(
            ErrorCode::NetworkError,
            json!({ "operation": "publishTransaction" }),
        )
        .detail(format!("{e:?}")),
    })
}

/// 校验一个收款地址并解析金额，给宿主一个统一的入口。
pub fn parse_payment(
    network: monero_wallet::address::Network,
    address: &str,
    amount_atomic: &str,
) -> Result<Payment> {
    let address = keys::parse_address(network, address, "recipient.address")?;
    let amount_atomic: u64 = amount_atomic.parse().map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "recipient.amountAtomic", "reason": "notAnUnsignedInteger" }),
        )
    })?;
    if amount_atomic == 0 {
        return Err(RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "recipient.amountAtomic", "reason": "mustBePositive" }),
        ));
    }
    Ok(Payment {
        address,
        amount_atomic,
    })
}

pub fn selection_to_json(selection: &Selection) -> Value {
    json!({
        "outputIds": selection.output_ids,
        "totalAtomic": selection.total_atomic.to_string(),
    })
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::wallet::testing::db_with_identity;
    use monero_wallet::address::Network;

    fn add_output(db: &Db, identity_id: i64, amount: i64, height: i64, with_ki: bool) -> i64 {
        let ki: Option<Vec<u8>> = with_ki.then(|| {
            let mut k = vec![0u8; 32];
            k[0] = (amount % 251) as u8;
            k[1] = (height % 251) as u8;
            k
        });
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, key_image)
                 VALUES (?1, x'00', ?2, ?3, 0, ?4, ?5, ?6)",
                params![
                    identity_id,
                    &vec![height as u8; 32],
                    height,
                    amount,
                    height,
                    ki
                ],
            )
            .unwrap();
        db.conn().last_insert_rowid()
    }

    fn add_possible_spend(db: &Db, id: i64, out_height: i64, key_image: u8, spend: u8, at: i64) {
        db.conn()
            .execute(
                "INSERT INTO possible_spends
                   (identity_id, out_txid, out_index, key_image, spend_txid, height, timestamp)
                 VALUES (?1, ?2, 0, ?3, ?4, ?5, 1700000000)",
                params![
                    id,
                    vec![out_height as u8; 32],
                    vec![key_image; 32],
                    vec![spend; 32],
                    at
                ],
            )
            .unwrap();
    }

    /// 补算出的 key image 若出现在这笔输出的某条可能花费里，那条就是真花费：
    /// 标成已花、补一条出账记录。只是被别人当诱饵的（key image 对不上）不算。
    #[test]
    fn a_key_image_found_among_possible_spends_confirms_an_earlier_spend() {
        let (mut db, id) = db_with_identity();
        let spent = add_output(&db, id, 700, 10, false);
        let decoyed = add_output(&db, id, 300, 11, false);
        add_possible_spend(&db, id, 10, 0xAA, 0x51, 30); // 真的：key image 对得上
        add_possible_spend(&db, id, 10, 0xBB, 0x52, 31); // 别人拿它当诱饵
        add_possible_spend(&db, id, 11, 0xCC, 0x53, 32); // 别人拿它当诱饵

        let result = apply_key_images(
            &mut db,
            id,
            &[
                (spent, [0xAA; 32], vec![10; 32], 0),
                (decoyed, [0xDD; 32], vec![11; 32], 0),
            ],
        )
        .unwrap();
        assert_eq!((result.imported, result.spends_found), (2, 1));

        let (height, txid): (Option<i64>, Option<Vec<u8>>) = db
            .conn()
            .query_row(
                "SELECT spent_at_height, spent_in_txid FROM outputs WHERE id = ?1",
                [spent],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((height, txid), (Some(30), Some(vec![0x51; 32])));
        let decoyed_spent: Option<i64> = db
            .conn()
            .query_row(
                "SELECT spent_at_height FROM outputs WHERE id = ?1",
                [decoyed],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(decoyed_spent, None, "只是被当诱饵，没花");

        let (direction, out_height): (String, i64) = db
            .conn()
            .query_row(
                "SELECT direction, height FROM transactions WHERE txid = ?1",
                [vec![0x51u8; 32]],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .unwrap();
        assert_eq!((direction.as_str(), out_height), ("out", 30));
        let left: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM possible_spends", [], |r| r.get(0))
            .unwrap();
        assert_eq!(left, 0, "有了 key image 的输出不再需要可能花费记录");
    }

    // Invalid transaction bytes must fail before signing, even with a review attached.
    #[test]
    fn signing_rejects_a_blob_that_is_not_a_signable_transaction() {
        let review = r#"{"network":"mainnet","fromAddress":"unused","toAddress":"unused","amountAtomic":"1","feeAtomic":"1","changeAtomic":"0","isMaxSend":false}"#;
        let e = sign_for_host("zz", &[0u8; 32], review).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "transactionDoesNotMatchReview");

        let e = sign_for_host("00010203", &[0u8; 32], review).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "transactionDoesNotMatchReview");
    }

    /// 金额相加溢出必须报错而不是回绕。回绕会让一笔「收款方总额 0」的交易
    /// 被当成合法请求构造出去。
    #[test]
    fn payment_totals_do_not_wrap_on_overflow() {
        let address = keys::parse_address(
            monero_wallet::address::Network::Mainnet,
            "4AdUndXHHZ6cfufTMvppY6JwXNouMBzSkbLYfpAV5Usx3skxNgYeYTRj5UzqtReoS44qo9mtmXCqY45DJ852K5Jv2684Rge",
            "recipient.address",
        )
        .unwrap();
        let payments = vec![
            Payment {
                address,
                amount_atomic: u64::MAX,
            },
            Payment {
                address,
                amount_atomic: 1,
            },
        ];
        let e = sum_payments(&payments).unwrap_err();
        assert_eq!(e.params["reason"], "totalOverflow");
    }

    #[test]
    fn spend_key_length_is_enforced() {
        let e = parse_spend_key(&[0u8; 31]).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidKeyMaterial);
        assert_eq!(e.params["byteLen"], 31);
    }

    /// 大额优先：Monero 按重量计费，输入越少越便宜。
    #[test]
    fn selection_prefers_large_outputs_and_stops_once_covered() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 100, 10, true);
        add_output(&db, id, 900, 11, true);
        add_output(&db, id, 500, 12, true);

        let s = select_inputs(&db, id, 100, 1_000).unwrap();
        assert_eq!(s.total_atomic, 1_400, "900 + 500 就够了，不该再拿 100");
        assert_eq!(s.output_ids.len(), 2);
    }

    #[test]
    fn selection_fails_loudly_when_funds_are_short() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 100, 10, true);
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidRequest);
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
        assert_eq!(e.params["availableAtomic"], "100");
    }

    /// 没有 key image 的输出不能参与花费 —— 无法确认它没被花掉，
    /// 拿它构造交易有双花风险。
    #[test]
    fn outputs_without_key_images_are_never_selected() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 5_000, 10, false);
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
    }

    /// 锁定期内的输出也不能选 —— 构不进环，节点会拒。
    #[test]
    fn locked_outputs_are_not_selected() {
        let (db, id) = db_with_identity();
        add_output(&db, id, 5_000, 95, true);
        // 链尖 100，输出在 95：差 5 块，未满 10 块锁定
        let e = select_inputs(&db, id, 100, 1_000).unwrap_err();
        assert_eq!(e.params["reason"], "insufficientUnlockedFunds");
        // 到 105 就够了
        assert!(select_inputs(&db, id, 105, 1_000).is_ok());
    }

    #[test]
    fn an_expired_reservation_gives_way_to_a_new_one() {
        let (mut db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        db.conn()
            .execute(
                "INSERT INTO locked_outputs (output_id, reservation_id, locked_at) VALUES (?1, 'old', ?2)",
                params![a, reservation_cutoff() - 1],
            )
            .unwrap();
        reserve(&mut db, "new", &[a]).expect("过期的预留不该挡路");
    }

    /// 用户离开确认页后，下一次准备发送放掉没签名的预留；签了名的可能正在
    /// 广播，要留着。
    #[test]
    fn a_new_send_releases_only_unsigned_reservations() {
        let (mut db, id) = db_with_identity();
        let abandoned = add_output(&db, id, 500, 10, true);
        let signed = add_output(&db, id, 400, 11, true);
        reserve(&mut db, "abandoned", &[abandoned]).unwrap();
        reserve(&mut db, "signed", &[signed]).unwrap();
        mark_signed(&mut db, "signed", 1).unwrap();

        assert_eq!(release_unsigned(&db, id).unwrap(), 1);

        let left: Vec<String> = db
            .conn()
            .prepare("SELECT reservation_id FROM locked_outputs")
            .unwrap()
            .query_map([], |r| r.get(0))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(left, vec!["signed".to_string()]);
    }

    #[test]
    fn signing_rejects_expired_partial_or_replayed_reservations_atomically() {
        let (mut db, id) = db_with_identity();
        let first = add_output(&db, id, 500, 10, true);
        let second = add_output(&db, id, 400, 11, true);
        reserve(&mut db, "review", &[first, second]).unwrap();
        assert!(mark_signed(&mut db, "review", 1).is_err());
        db.conn()
            .execute(
                "UPDATE locked_outputs SET locked_at = ?1 WHERE output_id = ?2",
                params![reservation_cutoff(), second],
            )
            .unwrap();
        assert!(mark_signed(&mut db, "review", 2).is_err());
        let marked: i64 = db
            .conn()
            .query_row(
                "SELECT count(*) FROM locked_outputs WHERE signed_at IS NOT NULL",
                [],
                |row| row.get(0),
            )
            .unwrap();
        assert_eq!(marked, 0, "partial claims must roll back");
        db.conn()
            .execute("DELETE FROM locked_outputs WHERE output_id = ?1", [second])
            .unwrap();
        assert!(mark_signed(&mut db, "review", 2).is_err());
        mark_signed(&mut db, "review", 1).unwrap();
        assert!(mark_signed(&mut db, "review", 1).is_err());
    }

    #[test]
    fn signed_reservations_stay_unspendable_after_the_unsigned_ttl() {
        let (mut db, id) = db_with_identity();
        let input = add_output(&db, id, 500, 10, true);
        reserve(&mut db, "signed", &[input]).unwrap();
        mark_signed(&mut db, "signed", 1).unwrap();
        db.conn()
            .execute(
                "UPDATE locked_outputs SET locked_at = ?1",
                [reservation_cutoff() - 1],
            )
            .unwrap();
        assert!(query::spendable_rows(&db, id, 100).unwrap().is_empty());
        assert_eq!(
            query::balance(&db, id, Some(100))
                .unwrap()
                .locked_by_reservation_atomic,
            500
        );
        assert!(reserve(&mut db, "replacement", &[input]).is_err());
    }

    /// wallet2 同款：待确认发送的找零计入总余额（锁定），不计入可用；判失败后不再计。
    #[test]
    fn pending_change_counts_in_total_until_the_send_fails() {
        let (db, id) = db_with_identity();
        let spent = add_output(&db, id, 500, 10, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row(
                "SELECT key_image FROM outputs WHERE id = ?1",
                [spent],
                |r| r.get(0),
            )
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let txid = [0x55u8; 32];
        record_broadcast(
            &db,
            id,
            &txid,
            &[key_image],
            "r",
            900,
            SentAmounts {
                outgoing_atomic: Some(200),
                change_atomic: Some(300),
            },
        )
        .unwrap();

        let b = crate::query::balance(&db, id, Some(10_000)).unwrap();
        assert_eq!(b.total_atomic, 300);
        assert_eq!(b.pending_change_atomic, 300);
        assert_eq!(b.unlocked_atomic, 0);

        crate::pool::mark_failed(&db, id, &txid).unwrap();
        let b = crate::query::balance(&db, id, Some(10_000)).unwrap();
        assert_eq!(b.pending_change_atomic, 0);
        assert_eq!(b.total_atomic, 500);
    }

    /// 广播后按交易里的 key image 标已花：预留过期或被释放了也不能漏记。
    #[test]
    fn a_broadcast_marks_its_inputs_by_key_image_without_a_reservation() {
        let (db, id) = db_with_identity();
        let a = add_output(&db, id, 500, 10, true);
        let untouched = add_output(&db, id, 400, 11, true);
        let key_image: Vec<u8> = db
            .conn()
            .query_row("SELECT key_image FROM outputs WHERE id = ?1", [a], |r| {
                r.get(0)
            })
            .unwrap();
        let key_image = <[u8; 32]>::try_from(key_image.as_slice()).unwrap();
        let txid = [0x77u8; 32];

        record_broadcast(
            &db,
            id,
            &txid,
            &[key_image],
            "gone",
            900,
            SentAmounts {
                outgoing_atomic: Some(123),
                change_atomic: None,
            },
        )
        .unwrap();

        let spent: Vec<(i64, Option<Vec<u8>>)> = db
            .conn()
            .prepare("SELECT id, spent_in_txid FROM outputs ORDER BY id")
            .unwrap()
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?)))
            .unwrap()
            .collect::<rusqlite::Result<_>>()
            .unwrap();
        assert_eq!(spent, vec![(a, Some(txid.to_vec())), (untouched, None)]);
        let outgoing: Option<String> = db
            .conn()
            .query_row(
                "SELECT outgoing_atomic FROM transactions WHERE txid = ?1",
                [txid.as_slice()],
                |r| r.get(0),
            )
            .unwrap();
        assert_eq!(outgoing.as_deref(), Some("123"));
    }

    /// 同一笔输出不能被两个预留同时持有 —— 否则两笔交易花同一个输出，
    /// 第二笔必然被网络拒绝。
    #[test]
    fn an_output_cannot_be_reserved_twice() {
        let (mut db, id) = db_with_identity();
        let a = add_output(&db, id, 5_000, 10, true);
        reserve(&mut db, "r1", &[a]).unwrap();
        assert!(reserve(&mut db, "r2", &[a]).is_err(), "第二次预留必须失败");
        assert_eq!(release(&db, "r1").unwrap(), 1);
        reserve(&mut db, "r2", &[a]).expect("释放后可以重新预留");
    }

    #[test]
    fn releasing_an_unknown_reservation_is_a_no_op() {
        let (mut db, _) = db_with_identity();
        assert_eq!(release(&db, "nope").unwrap(), 0);
    }

    #[test]
    fn payments_reject_zero_and_non_numeric_amounts() {
        let addr = crate::wallet::testing::ADDR;
        assert!(parse_payment(Network::Mainnet, addr, "0").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "-1").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "abc").is_err());
        assert!(parse_payment(Network::Mainnet, addr, "1000").is_ok());
    }

    /// 金额走字符串跨边界：piconero 上限超过 JS 的 MAX_SAFE_INTEGER。
    #[test]
    fn large_amounts_survive_the_string_boundary() {
        let addr = crate::wallet::testing::ADDR;
        let big = (u64::MAX / 2).to_string();
        let p = parse_payment(Network::Mainnet, addr, &big).unwrap();
        assert_eq!(p.amount_atomic.to_string(), big);
    }

    #[test]
    fn a_mainnet_address_is_refused_on_testnet() {
        let addr = crate::wallet::testing::ADDR;
        let e = parse_payment(Network::Testnet, addr, "1000").unwrap_err();
        assert_eq!(e.code, ErrorCode::InvalidAddress);
    }
}

// ── 端到端发送 ──────────────────────────────────────────────────────────

/// 手续费率的理智上限（piconero / weight）。
///
/// 上游 `fee_rate` 强制要求给一个上限，理由很实在：**手续费率来自节点，
/// 而节点可能撒谎**。报一个天价费率会让用户把余额全付成手续费。
///
/// Monero 常规优先级的费率约在 20,000 piconero/weight 量级，最高优先级约
/// 十倍。这里取 1,000,000 —— 比最高优先级还宽 5 倍，正常波动碰不到，
/// 但挡得住数量级级别的胡说。
pub const MAX_FEE_PER_WEIGHT: u64 = 1_000_000;

/// 手续费优先级。字符串而非数字：数字跨边界时无从校验。
pub fn parse_priority(name: &str) -> Result<monero_wallet::interface::FeePriority> {
    use monero_wallet::interface::FeePriority;
    Ok(match name {
        "unimportant" | "low" => FeePriority::Unimportant,
        "" | "normal" => FeePriority::Normal,
        "elevated" | "high" => FeePriority::Elevated,
        "priority" | "highest" => FeePriority::Priority,
        other => {
            return Err(RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({
                    "field": "priority",
                    "value": other,
                    "allowed": ["unimportant", "normal", "elevated", "priority"],
                }),
            ))
        }
    })
}

/// 构造一笔待签名交易并预留输入。**不需要花费私钥，不广播。**
///
/// ## `signableHex` 里有私有 view key
///
/// 上游把整个 viewing pair 写进找零输出（`ChangeEnum::Standard`）——
/// Monero 的钱包协议按「发送方能看到自己的找零」来做优化，所以它要 view key。
/// 不带 view key 的那个变体 `Change::fingerprintable` 上游明确警告
/// 「You MUST assume this will harm your privacy」，所以不能为了躲这个去换。
///
/// 结论：这个产物**等价于 view key**，不是一份无害的未签名交易。任何
/// 「导出去别处签」的设计都必须把它当 view key 对待 —— 拿到它的人能看见
/// 这个账户收到过的每一笔钱。
///
/// `recipients` 是 `(地址, piconero 金额)`。`max_send` 为真时只接受一个收款方，
/// 金额由「当前可花总额 − 手续费」反算，传入的金额被忽略。
///
/// 返回的 `feeAtomic` 是这笔交易真正要付的手续费：确认页显示它，签名时不会
/// 再变。`amountAtomic` 同理 —— max-send 下它才是收款方实际收到的数。
#[allow(clippy::too_many_arguments)]
pub async fn prepare_for_host(
    db: &mut Db,
    daemon: &crate::network::Daemon,
    identity_id: i64,
    recipients: &[(&str, &str)],
    max_send: bool,
    reservation_id: &str,
    chain_tip: u64,
    priority: monero_wallet::interface::FeePriority,
) -> Result<Value> {
    use monero_daemon_rpc::prelude::*;

    let network = db.network();
    let rate = daemon
        .fee_rate(priority, MAX_FEE_PER_WEIGHT)
        .await
        .map_err(|e| {
            RuntimeError::with(ErrorCode::NetworkError, json!({ "operation": "feeRate" }))
                .detail(format!("{e:?}"))
        })?;

    // 当前共识：CLSAG + Bulletproofs+。跟着硬分叉走，由我们发版控制，
    // 不问节点 —— 节点说错了交易会被全网拒绝。
    let rct_type = monero_wallet::ringct::RctType::ClsagBulletproofPlus;
    let mut rng = rand_core::OsRng;

    // 放在选输入之前：被放弃的预留占着的输出要能被这一次选中。
    release_unsigned(db, identity_id)?;
    let decoys = CachedDecoys::new(daemon);

    let prepared = if max_send {
        if recipients.len() != 1 {
            return Err(RuntimeError::with(
                ErrorCode::InvalidRequest,
                json!({ "field": "recipientsJson", "reason": "maxSendNeedsExactlyOneRecipient" }),
            ));
        }
        let address = keys::parse_address(network, recipients[0].0, "recipient.address")?;
        prepare_max(
            db,
            &decoys,
            &mut rng,
            identity_id,
            address,
            rate,
            chain_tip,
            rct_type,
        )
        .await?
    } else {
        let payments = recipients
            .iter()
            .map(|(addr, amount)| parse_payment(network, addr, amount))
            .collect::<Result<Vec<_>>>()?;
        prepare(
            db,
            &decoys,
            &mut rng,
            identity_id,
            &payments,
            rate,
            chain_tip,
            rct_type,
        )
        .await?
    };

    let change_atomic = prepared
        .inputs_atomic
        .saturating_sub(prepared.amount_atomic)
        .saturating_sub(prepared.fee_atomic);

    // 预留放在最后：前面任何一步失败都不该留下锁住的输出。
    reserve(db, reservation_id, &prepared.output_ids)?;

    Ok(json!({
        "reservationId": reservation_id,
        "signableHex": hex::encode(prepared.signable.serialize()),
        "feeAtomic": prepared.fee_atomic.to_string(),
        "amountAtomic": prepared.amount_atomic.to_string(),
        "inputsAtomic": prepared.inputs_atomic.to_string(),
        "changeAtomic": change_atomic.to_string(),
        "inputCount": prepared.output_ids.len(),
    }))
}

/// 签名一笔已构造的交易。**不碰网络、不碰数据库。**
pub fn sign_for_host(signable_hex: &str, spend_private: &[u8], review_json: &str) -> Result<Value> {
    let request = crate::review::parse_request(review_json)?;
    let signable = crate::review::verify(signable_hex, &request, Some(spend_private))?;
    let mut rng = rand_core::OsRng;
    let (raw, txid) = sign_prepared(signable, &mut rng, spend_private)?;
    Ok(json!({
        "txid": hex::encode(txid),
        "rawHex": hex::encode(&raw),
        "sizeBytes": raw.len(),
    }))
}

/// 广播成功后的记账：输入标已花、记一条在途转出、释放预留。一个事务。
/// 自己发出的一笔交易的金额（只有广播时知道）。
#[derive(Debug, Default, Clone, Copy)]
pub struct SentAmounts {
    /// 付出去的总额（收款 + 手续费）。
    pub outgoing_atomic: Option<u64>,
    /// 找零：上链前计入总余额（锁定），wallet2 同款。
    pub change_atomic: Option<u64>,
}

fn record_broadcast(
    db: &Db,
    identity_id: i64,
    txid: &[u8; 32],
    key_images: &[[u8; 32]],
    reservation_id: &str,
    chain_tip: u64,
    amounts: SentAmounts,
) -> Result<()> {
    let tx_db = db
        .conn()
        .unchecked_transaction()
        .map_err(|e| db_err("beginRecordSend", e))?;
    for key_image in key_images {
        tx_db
            .execute(
                "UPDATE outputs SET spent_at_height = ?1, spent_in_txid = ?2
                 WHERE identity_id = ?3 AND key_image = ?4 AND spent_at_height IS NULL",
                params![
                    chain_tip as i64,
                    txid.as_slice(),
                    identity_id,
                    key_image.as_slice()
                ],
            )
            .map_err(|e| db_err("markSpentAfterBroadcast", e))?;
    }
    tx_db
        .execute(
            "INSERT INTO transactions
               (identity_id, txid, height, direction, sent_at, outgoing_atomic, change_atomic)
             VALUES (?1, ?2, NULL, 'out', ?3, ?4, ?5)
             ON CONFLICT (identity_id, txid) DO UPDATE
               SET direction = 'out', sent_at = ?3, failed = 0,
                   outgoing_atomic = COALESCE(?4, outgoing_atomic),
                   change_atomic = COALESCE(?5, change_atomic)",
            params![
                identity_id,
                txid.as_slice(),
                crate::wallet::now_secs(),
                amounts.outgoing_atomic.map(|a| a.to_string()),
                amounts.change_atomic.map(|a| a.to_string())
            ],
        )
        .map_err(|e| db_err("recordOutgoing", e))?;
    tx_db
        .execute(
            "DELETE FROM locked_outputs WHERE reservation_id = ?1",
            [reservation_id],
        )
        .map_err(|e| db_err("releaseAfterBroadcast", e))?;
    tx_db.commit().map_err(|e| db_err("commitRecordSend", e))
}

/// 广播一笔已签名的交易，并记账。**不可逆。**
///
/// 广播成功后立刻把被花的输出标记掉：不标的话下一次选币会再选中它们，
/// 构出一笔必然被网络拒绝的交易。
pub async fn broadcast_signed(
    db: &Db,
    daemon: &crate::network::Daemon,
    identity_id: i64,
    raw_hex: &str,
    reservation_id: &str,
    chain_tip: u64,
    amounts: SentAmounts,
) -> Result<Value> {
    let raw = hex::decode(raw_hex).map_err(|_| {
        RuntimeError::with(
            ErrorCode::InvalidRequest,
            json!({ "field": "rawHex", "reason": "notHex" }),
        )
    })?;

    let tx = monero_wallet::transaction::Transaction::read(&mut raw.as_slice())
        .map_err(|e| db_err("parseSignedTransaction", e))?;
    let txid = tx.hash();

    // 明确拒收说明这笔不会上链，输入放回可用。其余错误分不清节点收没收到：
    // 照样记成在途交易，输入不会被重选（重发换一批输入就可能付两次）；
    // 节点真没收到的话，在途检查过了扩散期会判失败并放回输入。
    // 「双花」也可能是这笔自己早已上链（回包丢过、重试了同一份签名），同样当
    // 不确定处理：真是别人花的，扩散期后在途检查会判失败。
    let mut unknown = match broadcast(daemon, &raw).await {
        Ok(()) => None,
        Err(e) if e.code == ErrorCode::BroadcastRejected && !is_double_spend(&e) => {
            release(db, reservation_id)?;
            return Err(e);
        }
        Err(e) => Some(e),
    };

    // 广播已经发生，不可逆。立刻记账。按交易自己的 key image 标，不靠预留：
    // 预留可能已经过期，或者签名失败释放后又拿同一份签名重发。
    let key_images: Vec<[u8; 32]> = tx
        .prefix()
        .inputs
        .iter()
        .filter_map(|input| match input {
            monero_wallet::transaction::Input::ToKey { key_image, .. } => {
                Some(key_image.to_bytes())
            }
            _ => None,
        })
        .collect();
    // 记账失败也不能报「发送失败」：交易可能已经在网上，报失败会引人重发。
    // 按不确定返回；扫链按 key image 会认出这笔。
    if let Err(e) = record_broadcast(
        db,
        identity_id,
        &txid,
        &key_images,
        reservation_id,
        chain_tip,
        amounts,
    ) {
        unknown = Some(e);
    }

    Ok(json!({
        "txid": hex::encode(txid),
        "sizeBytes": raw.len(),
        "broadcast": if unknown.is_some() { "unknown" } else { "accepted" },
        "error": unknown.map(|e| e.to_json()),
    }))
}

#[cfg(test)]
mod cached_decoys_tests {
    use super::*;
    use monero_wallet::interface::{
        EvaluateUnlocked, InterfaceError, ProvidesBlockchainMeta, ProvidesDecoys,
        ProvidesUnvalidatedDecoys, TransactionsError,
    };
    use std::sync::atomic::{AtomicUsize, Ordering};

    struct CountingSource(AtomicUsize);

    impl ProvidesBlockchainMeta for CountingSource {
        async fn latest_block_number(&self) -> std::result::Result<usize, InterfaceError> {
            Ok(100)
        }
    }

    impl ProvidesUnvalidatedDecoys for CountingSource {
        async fn ringct_output_distribution(
            &self,
            _range: impl Send + std::ops::RangeBounds<usize>,
        ) -> std::result::Result<Vec<u64>, InterfaceError> {
            self.0.fetch_add(1, Ordering::SeqCst);
            Ok(vec![1, 2, 3])
        }

        async fn unlocked_ringct_outputs(
            &self,
            _indexes: &[u64],
            _evaluate_unlocked: EvaluateUnlocked,
        ) -> std::result::Result<Vec<Option<[Point; 2]>>, TransactionsError> {
            Ok(Vec::new())
        }
    }

    // Every input's decoy selection asks for the same range; the node is asked once.
    #[test]
    fn one_distribution_download_per_prepare() {
        let source = CountingSource(AtomicUsize::new(0));
        let cached = CachedDecoys::new(&source);
        for _ in 0..3 {
            let d = futures_lite::future::block_on(ProvidesDecoys::ringct_output_distribution(
                &cached,
                ..=100usize,
            ))
            .unwrap();
            assert_eq!(d, vec![1, 2, 3]);
        }
        assert_eq!(source.0.load(Ordering::SeqCst), 1);
    }
}

#[cfg(test)]
mod double_spend_tests {
    use super::*;

    #[test]
    fn a_double_spend_rejection_is_not_treated_as_final() {
        let spent = RuntimeError::new(ErrorCode::BroadcastRejected).detail("double spend");
        let other = RuntimeError::new(ErrorCode::BroadcastRejected).detail("fee too low");
        assert!(is_double_spend(&spent));
        assert!(!is_double_spend(&other));
    }
}
