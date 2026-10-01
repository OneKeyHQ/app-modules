//! 钱包库的表结构与迁移。
//!
//! ## 为什么要自己写
//!
//! monero-oxide 只给密码学与扫描器，**不提供任何存储**。Zcash 那边
//! `zcash_client_sqlite` 白送的这一层，在 Monero 这边没有对应物。
//!
//! ## 比 Zcash 少掉的东西
//!
//! Monero 只有一种输出类型（没有 sapling/orchard/ironwood 三池），FCMP++
//! 之前**没有承诺树**、因此也没有 witness 与检查点，也没有透明池。Zcash 库里
//! 最复杂的那几块这里根本不存在，所以这套表能做得很窄。
//!
//! ## 落盘而不是内存
//!
//! 这是选 monero-oxide 而非 monero-ts 的全部理由：后者把整个钱包放在 wasm
//! 内存里，持久化只能整块序列化重写，历史越长每次保存越贵，写一半崩了丢全部。
//! 这里是增量写 + 事务。

use rusqlite::Connection;

use crate::error::{db_err, Result};

/// 当前 schema 版本。**改表结构必须同时加迁移并把这个数加一。**
pub const SCHEMA_VERSION: i64 = 1;

/// 重组安全窗口：`blocks` 表只保留链尖往前这么多块的哈希。
///
/// Monero 的重组极少超过几块，但节点作恶不受此限。留 720 块（约一天）既够
/// 覆盖任何现实重组，又不会让这张表随链高无限增长 —— 这正是我们批评
/// wallet2 内存模型时说的那件事，自己不能犯。
pub const REORG_WINDOW_BLOCKS: u64 = 720;

/// 建表。幂等：可以在每次开库时调用。
pub fn migrate(conn: &Connection) -> Result<()> {
    let version: i64 = conn
        .query_row("PRAGMA user_version", [], |r| r.get(0))
        .map_err(|e| db_err("readUserVersion", e))?;

    if version > SCHEMA_VERSION {
        return Err(db_err(
            "migrate",
            format!(
                "库的 schema 版本 {version} 高于本 runtime 支持的 {SCHEMA_VERSION}，拒绝降级打开"
            ),
        ));
    }

    // 整个迁移一个事务：中途失败（关页、断电）不能留下改了一半、版本号却没动的库，
    // 否则下次开库会对已存在的列再 ALTER 一次而永远打不开。
    let tx = conn
        .unchecked_transaction()
        .map_err(|e| db_err("beginMigrate", e))?;
    if version < 1 {
        tx.execute_batch(V1).map_err(|e| db_err("migrateV1", e))?;
    }

    tx.execute(&format!("PRAGMA user_version = {SCHEMA_VERSION}"), [])
        .map_err(|e| db_err("setUserVersion", e))?;
    tx.commit().map_err(|e| db_err("commitMigrate", e))
}

/// 版本 1。
///
/// 命名约定：金额一律 `*_atomic` 存 piconero（u64 存成 INTEGER，Monero 的
/// 总量 1.8e19 超过 i64 上限，但单笔金额不会 —— 超了会在写入时报错而不是
/// 静默截断，见 `outputs.amount_atomic` 的 CHECK）。
const V1: &str = r#"
PRAGMA foreign_keys = ON;

-- 钱包级元数据。单行表，id 恒为 0。
CREATE TABLE IF NOT EXISTS wallet_meta (
    id              INTEGER PRIMARY KEY CHECK (id = 0),
    network         TEXT    NOT NULL,
    created_at      INTEGER NOT NULL
);

-- 一个 identity = 一对观察密钥 = 宿主眼里的一个账户。
--
-- 存的是**观察材料**：花费公钥 + 私有观察密钥，等价于 Zcash 的 UFVK。
-- 花费私钥永远不落库、不进这个 crate —— 签名走另一条路。
CREATE TABLE IF NOT EXISTS identities (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    -- 宿主给的稳定标识，用来跨会话认人
    uuid                TEXT    NOT NULL UNIQUE,
    -- 主地址（标准地址，非子地址），同时兼作人类可读的身份
    primary_address     TEXT    NOT NULL UNIQUE,
    spend_public_key    BLOB    NOT NULL CHECK (length(spend_public_key) = 32),
    view_private_key    BLOB    NOT NULL CHECK (length(view_private_key) = 32),
    -- 从哪个高度开始扫。导入旧钱包时由宿主给。
    birthday_height     INTEGER NOT NULL CHECK (birthday_height >= 0),
    -- 本身份已经连续扫到的高度（含）。NULL = 一块都没扫过。
    --
    -- **必须按身份记**：拿别的身份的进度推算，会让一个刚追到链尖的身份把
    -- 另一个还在回补的身份的进度"推"到链尖，那段历史就被永久跳过了。
    scanned_to_height   INTEGER,
    created_at          INTEGER NOT NULL
);

-- 子地址注册表。扫描前要全部灌进 monero-oxide 的 Scanner —— 它只认注册过的。
--
-- (major, minor) 同为 0 是主地址，不进这张表（上游 SubaddressIndex::new
-- 对 (0,0) 返回 None）。
CREATE TABLE IF NOT EXISTS subaddresses (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    major           INTEGER NOT NULL CHECK (major >= 0),
    minor           INTEGER NOT NULL CHECK (minor >= 0),
    address         TEXT    NOT NULL,
    label           TEXT,
    PRIMARY KEY (identity_id, major, minor),
    CHECK (major != 0 OR minor != 0)
);
CREATE INDEX IF NOT EXISTS idx_subaddresses_address ON subaddresses(address);

-- 已扫区块的哈希与 RingCT 输出游标，每个身份各存一份。
--
-- 两个用途，都不可省：
--
-- 1. **重组检测** —— 拉到新块时比对 prev_hash，对不上就是重组。
-- 2. **RingCT 索引校验** —— 上游 `ScannableBlock` 的文档明确警告
--    `output_index_for_first_ringct_output` **无法验证**，要求客户端自己维护
--    这个索引并与节点声称的比对，不一致应当判定节点提供了虚假视图并断开。
--    `ringct_next` 存的就是"扫完本块之后下一个 RingCT 输出的全局序号"。
--
-- 不能各身份共用：一个身份回滚会删掉另一个身份的锚点，另一个身份接着在新链
-- 上扫、看不出重组；追链尖的身份裁剪时也会裁掉还在回补的身份的锚点。
--
-- 只保留链尖往前 REORG_WINDOW_BLOCKS 块，不随链高无限增长。
CREATE TABLE IF NOT EXISTS blocks (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    height          INTEGER NOT NULL,
    hash            BLOB    NOT NULL CHECK (length(hash) = 32),
    -- 扫完本块之后下一个 RingCT 输出的全局序号。
    --
    -- **允许 NULL**：从链中段开始扫、且起始块恰好没有 RingCT 输出时，我们
    -- 没有任何依据推算这个游标。此时必须存 NULL 而不是 0 —— 存 0 会让下一块
    -- 拿真实序号（几千万）来比对 0，报出一个**假的** RINGCT_INDEX_MISMATCH，
    -- 把一个诚实的节点判成作恶。
    ringct_next     INTEGER CHECK (ringct_next IS NULL OR ringct_next >= 0),
    timestamp       INTEGER NOT NULL,
    PRIMARY KEY (identity_id, height)
);

-- 扫描进度。用区间而不是单个游标：导入旧钱包会产生"回补一段历史"与
-- "追链尖"两条互不相邻的待扫区间。
--
-- Monero 没有承诺树，所以**不要求连续扫描**（Zcash 要求，因为 witness 必须
-- 沿着树增量维护）。这让这张表比 Zcash 的扫描队列简单一个量级。
CREATE TABLE IF NOT EXISTS scan_ranges (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    start_height    INTEGER NOT NULL,
    end_height      INTEGER NOT NULL,
    PRIMARY KEY (identity_id, start_height),
    CHECK (end_height > start_height)
);

-- 扫到的输出。
--
-- `blob` 是 monero-oxide `WalletOutput::serialize()` 的原样字节，花费时用
-- `WalletOutput::read()` 读回 —— 这是整个落盘方案成立的关键 API。
-- 其余列是从中抽出来供 SQL 查询的，**不是另一份真相**：任何要参与密码学的
-- 地方都必须从 blob 反序列化，抽出来的列只用于筛选和展示。
CREATE TABLE IF NOT EXISTS outputs (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    identity_id         INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    -- monero-oxide 的原样序列化，花费时读回
    blob                BLOB    NOT NULL,
    -- ── 以下均为 blob 的派生列，仅供查询 ──
    txid                BLOB    NOT NULL CHECK (length(txid) = 32),
    -- 该输出在链上的全局序号，构环时要用
    global_index        INTEGER NOT NULL,
    index_in_tx         INTEGER NOT NULL,
    amount_atomic       INTEGER NOT NULL CHECK (amount_atomic >= 0),
    -- 收进哪个子地址；主地址存 NULL
    subaddress_major    INTEGER,
    subaddress_minor    INTEGER,
    height              INTEGER NOT NULL,
    -- 额外时间锁（Monero 的 unlock_time），0 表示无
    additional_timelock INTEGER NOT NULL DEFAULT 0,
    -- 花费检测用。需要花费密钥才能算，所以可能为空（只读观察钱包）。
    key_image           BLOB    CHECK (key_image IS NULL OR length(key_image) = 32),
    -- 花在哪个高度；未花为 NULL
    spent_at_height     INTEGER,
    spent_in_txid       BLOB,
    -- 交易公钥 R，多个则 32 字节一个顺序拼接（通常只有一个）。
    --
    -- monero-oxide 扫描时把 R 折叠进 `key_offset` 后丢掉，软件签名用不到它；
    -- 硬件签名要（固件 `step_02_set_input.py` 自己推导一次性私钥，不接受宿主
    -- 算好的偏移）。扫过就没了，事后只能全量重扫或拿 txid 问节点（泄露隐私），
    -- 所以扫描当下就得留。
    tx_pub_keys             BLOB,
    -- 子地址收款用的 additional keys，每个输出一个；没有则为 NULL。
    tx_additional_pub_keys  BLOB,
    -- 输出的一次性公钥，查燃烧 bug 用（同一个公钥只有一笔能花）
    output_key          BLOB    CHECK (output_key IS NULL OR length(output_key) = 32),
    UNIQUE (identity_id, txid, index_in_tx)
);
CREATE INDEX IF NOT EXISTS idx_outputs_unspent
    ON outputs(identity_id, spent_at_height) WHERE spent_at_height IS NULL;
CREATE INDEX IF NOT EXISTS idx_outputs_height ON outputs(identity_id, height);
CREATE INDEX IF NOT EXISTS idx_outputs_key_image ON outputs(key_image) WHERE key_image IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_outputs_global ON outputs(identity_id, global_index);
CREATE INDEX IF NOT EXISTS idx_outputs_output_key ON outputs(identity_id, output_key);

-- 交易索引。收款由扫描产出；花费由 key image 命中产出。
CREATE TABLE IF NOT EXISTS transactions (
    identity_id     INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    txid            BLOB    NOT NULL CHECK (length(txid) = 32),
    height          INTEGER,           -- NULL = 还在内存池
    timestamp       INTEGER,
    -- 'in' 收款 / 'out' 花费。一笔交易可能两者皆有（找零），此时记 'out'。
    direction       TEXT    NOT NULL CHECK (direction IN ('in', 'out')),
    -- 以下是自己广播的交易的在途状态（wallet2 `process_unconfirmed_transfer`
    -- 同款）：迟迟不进内存池的判失败并放回它花的输出，否则那笔钱永远锁着。
    sent_at         INTEGER,
    pool_checked_at INTEGER,
    failed          INTEGER NOT NULL DEFAULT 0,
    -- 付出去的总额（收款 + 手续费），piconero 十进制串。待确认期间只有广播时
    -- 知道（找零要上链扫到才有）。
    outgoing_atomic TEXT,
    -- 待确认期间的找零，piconero 十进制串。wallet2 把它算进总余额（锁定），
    -- 上链扫到后它变成一笔普通输出，这里就不再计。
    change_atomic   TEXT,
    PRIMARY KEY (identity_id, txid)
);
CREATE INDEX IF NOT EXISTS idx_transactions_height ON transactions(identity_id, height);

-- 锁定中的输出：已被某笔待广播交易选中，不可再次选中。
--
-- 与 Zcash 同样的理由：不锁会导致同一个输出被两笔在途交易同时花掉，
-- 第二笔必然被网络拒绝，而用户看到的是"莫名其妙失败"。
CREATE TABLE IF NOT EXISTS locked_outputs (
    output_id       INTEGER PRIMARY KEY REFERENCES outputs(id) ON DELETE CASCADE,
    reservation_id  TEXT    NOT NULL,
    locked_at       INTEGER NOT NULL,
    -- 宿主签好名的时间。没签名的预留在下一次准备发送时作废（用户离开了确认页）；
    -- 签了名的要等广播结果或过期。
    signed_at       INTEGER
);
CREATE INDEX IF NOT EXISTS idx_locked_reservation ON locked_outputs(reservation_id);

-- 可能的花费（monero-lws 的做法）。
--
-- 观察钱包没有花费私钥，认不出自己的钱被花掉。但一笔钱被花时它必然出现在
-- 那个输入的环里，所以扫描时把「环里含我的钱」的输入连同它的 key image 记下；
-- 等宿主凭密码（或硬件）补算出 key image，一比对就知道哪条是真的花费。
-- 从没出现在任何环里的钱一定没花。只按全局序号比对，不需要任何密钥。
CREATE TABLE IF NOT EXISTS possible_spends (
    identity_id  INTEGER NOT NULL REFERENCES identities(id) ON DELETE CASCADE,
    -- 被引用的是哪笔我的输出：(txid, index_in_tx)，与 outputs 的唯一键一致
    out_txid     BLOB    NOT NULL CHECK (length(out_txid) = 32),
    out_index    INTEGER NOT NULL,
    -- 引用它的那个输入的 key image，以及那条交易
    key_image    BLOB    NOT NULL CHECK (length(key_image) = 32),
    spend_txid   BLOB    NOT NULL CHECK (length(spend_txid) = 32),
    height       INTEGER NOT NULL,
    timestamp    INTEGER,
    PRIMARY KEY (identity_id, out_txid, out_index, key_image)
);
CREATE INDEX IF NOT EXISTS idx_possible_spends_height ON possible_spends(identity_id, height);
"#;

#[cfg(test)]
mod tests {
    use super::*;

    fn db() -> Connection {
        let conn = Connection::open_in_memory().expect("内存库");
        migrate(&conn).expect("建表");
        conn
    }

    /// 交易公钥要能原样存进去、原样拿回来。
    ///
    /// 硬件签名要的就是这串字节（固件拿它推导一次性私钥），存歪了不会报错，
    /// 只会在将来某天让设备签出一笔花不掉的交易。
    ///
    /// 复用 `db_with_identity`，不另起一份身份行的 fixture —— 那份会随表约束
    /// 变化而悄悄过期。
    #[test]
    fn transaction_public_keys_round_trip() {
        let (db, identity_id) = crate::wallet::testing::db_with_identity();
        let conn = db.conn();

        let r = vec![0xABu8; 32];
        let additional = vec![0xCDu8; 64];
        conn.execute(
            "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                  amount_atomic, height, tx_pub_keys, tx_additional_pub_keys)
             VALUES (?1, x'00', ?2, 1, 0, 1, 1, ?3, ?4)",
            rusqlite::params![identity_id, &vec![1u8; 32], &r, &additional],
        )
        .unwrap();

        let (got_r, got_additional): (Vec<u8>, Option<Vec<u8>>) = conn
            .query_row(
                "SELECT tx_pub_keys, tx_additional_pub_keys FROM outputs",
                [],
                |row| Ok((row.get(0)?, row.get(1)?)),
            )
            .unwrap();
        assert_eq!(got_r, r);
        assert_eq!(got_additional, Some(additional));
    }

    /// 没有 additional keys 的交易（发给主地址）存 NULL，不是空串。
    /// 空串会让读取方以为「有 0 个 additional key」，和「这笔交易没有这个字段」
    /// 是两回事。
    #[test]
    fn an_output_without_additional_keys_stores_null() {
        let (db, identity_id) = crate::wallet::testing::db_with_identity();
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                                      amount_atomic, height, tx_pub_keys)
                 VALUES (?1, x'00', ?2, 1, 0, 1, 1, ?3)",
                rusqlite::params![identity_id, &vec![2u8; 32], &vec![0xABu8; 32]],
            )
            .unwrap();

        let additional: Option<Vec<u8>> = db
            .conn()
            .query_row("SELECT tx_additional_pub_keys FROM outputs", [], |r| {
                r.get(0)
            })
            .unwrap();
        assert_eq!(additional, None);
    }

    #[test]
    fn migrate_is_idempotent() {
        let conn = db();
        migrate(&conn).expect("重复迁移应当无害");
        let v: i64 = conn
            .query_row("PRAGMA user_version", [], |r| r.get(0))
            .unwrap();
        assert_eq!(v, SCHEMA_VERSION);
    }

    #[test]
    fn refuses_to_open_a_newer_database() {
        let conn = Connection::open_in_memory().unwrap();
        conn.execute_batch("PRAGMA user_version = 999").unwrap();
        // 降级打开会让新版写入的数据被旧代码误读，宁可拒绝
        assert!(migrate(&conn).is_err());
    }

    #[test]
    fn every_table_exists() {
        let conn = db();
        for t in [
            "wallet_meta",
            "identities",
            "subaddresses",
            "blocks",
            "scan_ranges",
            "outputs",
            "transactions",
            "locked_outputs",
            "possible_spends",
        ] {
            let n: i64 = conn
                .query_row(
                    "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?1",
                    [t],
                    |r| r.get(0),
                )
                .unwrap();
            assert_eq!(n, 1, "缺表 {t}");
        }
    }

    /// (0,0) 是主地址，不该进子地址表 —— 上游 `SubaddressIndex::new(0,0)`
    /// 返回 None，库里也要挡住，否则会注册出一个扫描器永远不认的条目。
    #[test]
    fn subaddress_zero_zero_is_rejected() {
        let conn = db();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        let bad = conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 0, 'a')",
            [],
        );
        assert!(bad.is_err(), "(0,0) 必须被 CHECK 拦住");
        conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 1, 'a')",
            [],
        )
        .expect("(0,1) 是合法子地址");
    }

    /// 同一个输出不能入库两次 —— 重扫同一段区间必须幂等，否则余额翻倍。
    #[test]
    fn the_same_output_cannot_be_inserted_twice() {
        let conn = db();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        let insert = "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx, amount_atomic, height)
                      VALUES (1, x'00', zeroblob(32), 7, 0, 100, 10)";
        conn.execute(insert, []).expect("首次写入");
        assert!(
            conn.execute(insert, []).is_err(),
            "同一 (identity, txid, index_in_tx) 必须被唯一约束拦住"
        );
    }

    /// 删除 identity 要连带清掉它的全部数据 —— 否则「删钱包」会留下可被后续
    /// 查询读到的孤儿行，那是隐私问题不只是脏数据。
    #[test]
    fn deleting_an_identity_cascades() {
        let conn = db();
        conn.execute("PRAGMA foreign_keys = ON", []).unwrap();
        conn.execute(
            "INSERT INTO identities (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
             VALUES ('u', 'addr', zeroblob(32), zeroblob(32), 0, 0)",
            [],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx, amount_atomic, height)
             VALUES (1, x'00', zeroblob(32), 7, 0, 100, 10)",
            [],
        )
        .unwrap();
        conn.execute(
            "INSERT INTO subaddresses (identity_id, major, minor, address) VALUES (1, 0, 1, 'a')",
            [],
        )
        .unwrap();

        conn.execute("DELETE FROM identities WHERE id = 1", [])
            .unwrap();
        for t in ["outputs", "subaddresses"] {
            let n: i64 = conn
                .query_row(&format!("SELECT count(*) FROM {t}"), [], |r| r.get(0))
                .unwrap();
            assert_eq!(n, 0, "{t} 应当被级联清空");
        }
    }
}
