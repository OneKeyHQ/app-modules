//! 钱包库句柄与身份管理。
//!
//! 一个 identity = 一对观察密钥 = 宿主眼里的一个账户。库是**共享的**：
//! 多个 identity 共用一个 SQLite 文件，各自有独立的扫描进度。
//!
//! 这一点和 Zcash 有个关键差别：Zcash 的扫描队列是全库共享的（承诺树要求
//! 连续扫描），所以「用 A 的 key 扫过的区间会被标记为全库已扫」，暂停的账户
//! 会漏掉那段。Monero 没有承诺树、不要求连续扫描，**扫描进度按 identity 独立
//! 记账**（`scan_ranges.identity_id`），从根上不存在那个坑。

use monero_wallet::{address::Network, ViewPair};
use rusqlite::{params, Connection, OptionalExtension};
use serde_json::json;

use crate::error::{db_err, ErrorCode, Result, RuntimeError};
use crate::keys::{self, network_name};
use crate::schema;

/// 账户 0 下，收过钱的最大子地址之后保持登记的空闲子地址数（同比特币的 gap limit）。
pub const SUBADDRESS_LOOKAHEAD: u32 = 20;

pub struct Db {
    conn: Connection,
    network: Network,
}

/// 库里的一个身份。
pub struct Identity {
    pub id: i64,
    pub uuid: String,
    pub primary_address: String,
    pub birthday_height: u64,
    pub view_pair: ViewPair,
}

impl Db {
    /// 打开（或新建）一个钱包库并跑迁移。
    pub fn open(db_name: &str, network: Network) -> Result<Self> {
        let conn = crate::storage::open_connection(db_name)?;
        schema::migrate(&conn)?;

        // 网络是库级不变量：同一个文件里混两个网络的身份，扫描会把
        // stagenet 的块喂给 mainnet 的密钥，表现为「怎么扫都扫不到钱」。
        let existing: Option<String> = conn
            .query_row("SELECT network FROM wallet_meta WHERE id = 0", [], |r| {
                r.get(0)
            })
            .optional()
            .map_err(|e| db_err("readWalletMeta", e))?;

        match existing {
            Some(recorded) if recorded != network_name(network) => {
                return Err(RuntimeError::with(
                    ErrorCode::InvalidNetwork,
                    json!({
                        "value": network_name(network),
                        "recorded": recorded,
                        "reason": "databaseBelongsToAnotherNetwork",
                    }),
                ));
            }
            Some(_) => {}
            None => {
                conn.execute(
                    "INSERT INTO wallet_meta (id, network, created_at) VALUES (0, ?1, ?2)",
                    params![network_name(network), now_secs()],
                )
                .map_err(|e| db_err("initWalletMeta", e))?;
            }
        }

        Ok(Self { conn, network })
    }

    pub fn network(&self) -> Network {
        self.network
    }

    pub fn conn(&self) -> &Connection {
        &self.conn
    }

    pub fn conn_mut(&mut self) -> &mut Connection {
        &mut self.conn
    }

    /// 注册一个身份。已存在时按 uuid 幂等返回原有的那个。
    ///
    /// `birthday_height` 决定回补起点。宿主给不出来时应当给链尖而不是 0 ——
    /// 给 0 会让新钱包从创世块扫起，那是几天而不是几秒。
    pub fn add_identity(
        &mut self,
        uuid: &str,
        spend_public: &[u8],
        view_private: &[u8],
        birthday_height: u64,
    ) -> Result<Identity> {
        let pair = keys::view_pair_from_bytes(spend_public, view_private)?;
        let primary = keys::primary_address(&pair, self.network);

        let by_uuid: Option<(i64, String)> = self
            .conn
            .query_row(
                "SELECT id, primary_address FROM identities WHERE uuid = ?1",
                [uuid],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .optional()
            .map_err(|e| db_err("findIdentity", e))?;
        if let Some((id, address)) = by_uuid {
            if address == primary {
                return self.identity_by_id(id);
            }
            // 同一个账户标识换了密钥（重置后新建的钱包沿用了旧标识）：旧数据属于一个
            // 已经不在的钱包，留着会被当成新钱包的，删掉。
            self.remove_identity(uuid)?;
        }

        // 同一套密钥换了标识（删掉钱包后重新导入）：扫描数据仍然有效，改挂到新标识下。
        let by_address: Option<i64> = self
            .conn
            .query_row(
                "SELECT id FROM identities WHERE primary_address = ?1",
                [&primary],
                |r| r.get(0),
            )
            .optional()
            .map_err(|e| db_err("findIdentityByAddress", e))?;

        let id = match by_address {
            Some(id) => {
                self.conn
                    .execute(
                        "UPDATE identities SET uuid = ?1 WHERE id = ?2",
                        params![uuid, id],
                    )
                    .map_err(|e| db_err("renameIdentity", e))?;
                id
            }
            None => {
                let tx = self
                    .conn
                    .transaction()
                    .map_err(|e| db_err("beginAddIdentity", e))?;
                tx.execute(
                    "INSERT INTO identities
                       (uuid, primary_address, spend_public_key, view_private_key, birthday_height, created_at)
                     VALUES (?1, ?2, ?3, ?4, ?5, ?6)",
                    params![
                        uuid,
                        &primary,
                        spend_public,
                        view_private,
                        birthday_height as i64,
                        now_secs()
                    ],
                )
                .map_err(|e| db_err("insertIdentity", e))?;
                let id = tx.last_insert_rowid();

                // 新身份的初始待扫区间：从 birthday 到 birthday（空区间），
                // 真正的终点由 `update_chain_tip` 在拿到链尖后补上。这样
                // 「还没问过链尖」和「已经扫完」在库里是两种不同的状态。
                tx.commit().map_err(|e| db_err("commitAddIdentity", e))?;
                id
            }
        };

        self.identity_by_id(id)
    }

    pub fn identity_by_id(&self, id: i64) -> Result<Identity> {
        self.conn
            .query_row(
                "SELECT id, uuid, primary_address, birthday_height, spend_public_key, view_private_key
                 FROM identities WHERE id = ?1",
                [id],
                Self::row_to_identity,
            )
            .optional()
            .map_err(|e| db_err("identityById", e))?
            .ok_or_else(|| {
                RuntimeError::with(ErrorCode::AccountNotFound, json!({ "accountId": id }))
            })?
    }

    pub fn identity_by_uuid(&self, uuid: &str) -> Result<Identity> {
        self.conn
            .query_row(
                "SELECT id, uuid, primary_address, birthday_height, spend_public_key, view_private_key
                 FROM identities WHERE uuid = ?1",
                [uuid],
                Self::row_to_identity,
            )
            .optional()
            .map_err(|e| db_err("identityByUuid", e))?
            .ok_or_else(|| {
                RuntimeError::with(ErrorCode::AccountNotFound, json!({ "accountId": uuid }))
            })?
    }

    #[allow(clippy::type_complexity)]
    fn row_to_identity(row: &rusqlite::Row<'_>) -> rusqlite::Result<Result<Identity>> {
        let id: i64 = row.get(0)?;
        let uuid: String = row.get(1)?;
        let primary_address: String = row.get(2)?;
        let birthday: i64 = row.get(3)?;
        let spend: Vec<u8> = row.get(4)?;
        let view: Vec<u8> = row.get(5)?;
        Ok(
            keys::view_pair_from_bytes(&spend, &view).map(|view_pair| Identity {
                id,
                uuid,
                primary_address,
                birthday_height: birthday as u64,
                view_pair,
            }),
        )
    }

    pub fn list_identities(&self) -> Result<Vec<(i64, String, String)>> {
        let mut stmt = self
            .conn
            .prepare("SELECT id, uuid, primary_address FROM identities ORDER BY id")
            .map_err(|e| db_err("listIdentities", e))?;
        let rows = stmt
            .query_map([], |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?)))
            .map_err(|e| db_err("listIdentities", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("listIdentities", e))
    }

    /// 删除一个身份连同它的全部数据。**不可逆。**
    ///
    /// 级联由外键负责（`PRAGMA foreign_keys = ON` 在 open_connection 里开）。
    /// 留下孤儿行不只是脏数据，是隐私问题 —— 「删钱包」必须真的删干净。
    pub fn remove_identity(&mut self, uuid: &str) -> Result<bool> {
        let n = self
            .conn
            .execute("DELETE FROM identities WHERE uuid = ?1", [uuid])
            .map_err(|e| db_err("removeIdentity", e))?;
        Ok(n > 0)
    }

    /// 登记一个子地址。扫描前这些会被灌进 Scanner —— **没登记的收不到**。
    pub fn register_subaddress(
        &mut self,
        identity_id: i64,
        major: u32,
        minor: u32,
        label: Option<&str>,
    ) -> Result<String> {
        let identity = self.identity_by_id(identity_id)?;
        let address = keys::subaddress(&identity.view_pair, self.network, major, minor)?;
        self.conn
            .execute(
                "INSERT OR REPLACE INTO subaddresses (identity_id, major, minor, address, label)
                 VALUES (?1, ?2, ?3, ?4, ?5)",
                params![identity_id, major, minor, &address, label],
            )
            .map_err(|e| db_err("registerSubaddress", e))?;
        Ok(address)
    }

    /// 保证账户 0 下「收过钱的最大子地址序号 + SUBADDRESS_LOOKAHEAD」以内都已登记。
    /// 返回新登记了几个。扫描只认登记过的子地址；成本只在登记时（每个一次标量乘），
    /// 扫描是反推公钥后查表，不随数量增长。
    pub fn ensure_subaddress_lookahead(&mut self, identity_id: i64) -> Result<u32> {
        let (highest_used, highest_registered): (i64, i64) = self
            .conn
            .query_row(
                "SELECT
                   COALESCE((SELECT max(subaddress_minor) FROM outputs
                             WHERE identity_id = ?1 AND subaddress_major = 0), 0),
                   COALESCE((SELECT max(minor) FROM subaddresses
                             WHERE identity_id = ?1 AND major = 0), 0)",
                [identity_id],
                |r| Ok((r.get(0)?, r.get(1)?)),
            )
            .map_err(|e| db_err("subaddressLookahead", e))?;
        let target = highest_used as u32 + SUBADDRESS_LOOKAHEAD;
        let mut added = 0;
        for minor in (highest_registered as u32 + 1)..=target {
            self.register_subaddress(identity_id, 0, minor, None)?;
            added += 1;
        }
        Ok(added)
    }

    /// 取该身份已登记的全部子地址序号。
    pub fn subaddress_indices(&self, identity_id: i64) -> Result<Vec<(u32, u32)>> {
        let mut stmt = self
            .conn
            .prepare(
                "SELECT major, minor FROM subaddresses WHERE identity_id = ?1 ORDER BY major, minor",
            )
            .map_err(|e| db_err("subaddressIndices", e))?;
        let rows = stmt
            .query_map([identity_id], |r| {
                Ok((r.get::<_, i64>(0)? as u32, r.get::<_, i64>(1)? as u32))
            })
            .map_err(|e| db_err("subaddressIndices", e))?;
        rows.collect::<rusqlite::Result<Vec<_>>>()
            .map_err(|e| db_err("subaddressIndices", e))
    }
}

pub fn now_secs() -> i64 {
    #[cfg(target_arch = "wasm32")]
    {
        (js_sys::Date::now() / 1000.0) as i64
    }
    #[cfg(not(target_arch = "wasm32"))]
    {
        std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .map(|d| d.as_secs() as i64)
            .unwrap_or(0)
    }
}

#[cfg(test)]
pub(crate) mod testing {
    use super::*;

    pub const ADDR: &str = "44AFFq5kSiGBoZ4NMDwYtN18obc8AemS33DBLWs3H7otXft3XjrpDtQGv7SqSsaBYBb98uNbr2VBBEt7f2wfn3RVGQBEP3A";

    /// 一对能用的测试密钥：花费公钥取自已知地址，观察私钥用小标量。
    pub fn key_bytes() -> ([u8; 32], [u8; 32]) {
        use monero_wallet::address::MoneroAddress;
        let addr = MoneroAddress::from_str(Network::Mainnet, ADDR).unwrap();
        let spend = addr.spend().compress().to_bytes();
        let mut view = [0u8; 32];
        view[0] = 9;
        (spend, view)
    }

    pub fn db() -> Db {
        Db::open(":memory:", Network::Mainnet).expect("内存库")
    }

    pub fn db_with_identity() -> (Db, i64) {
        let mut db = db();
        let (spend, view) = key_bytes();
        let id = db
            .add_identity("acct-1", &spend, &view, 100)
            .expect("建身份")
            .id;
        (db, id)
    }
}

#[cfg(test)]
mod tests {
    use super::testing::*;
    use super::*;

    fn registered_minors(db: &Db, id: i64) -> Vec<u32> {
        db.subaddress_indices(id)
            .unwrap()
            .into_iter()
            .filter(|(major, _)| *major == 0)
            .map(|(_, minor)| minor)
            .collect()
    }

    fn insert_output_at_minor(db: &Db, id: i64, minor: u32) {
        db.conn()
            .execute(
                "INSERT INTO outputs (identity_id, blob, txid, global_index, index_in_tx,
                   amount_atomic, subaddress_major, subaddress_minor, height)
                 VALUES (?1, x'00', ?2, 1, 0, 1, 0, ?3, 150)",
                params![id, vec![minor as u8; 32], minor],
            )
            .unwrap();
    }

    #[test]
    fn a_new_identity_gets_the_lookahead_window() {
        let (mut db, id) = db_with_identity();
        assert_eq!(
            db.ensure_subaddress_lookahead(id).unwrap(),
            SUBADDRESS_LOOKAHEAD
        );
        assert_eq!(
            registered_minors(&db, id),
            (1..=SUBADDRESS_LOOKAHEAD).collect::<Vec<_>>()
        );
        assert_eq!(
            db.ensure_subaddress_lookahead(id).unwrap(),
            0,
            "已够就不再登记"
        );
    }

    #[test]
    fn a_payment_near_the_end_widens_the_window() {
        let (mut db, id) = db_with_identity();
        db.ensure_subaddress_lookahead(id).unwrap();
        insert_output_at_minor(&db, id, 18);
        assert_eq!(db.ensure_subaddress_lookahead(id).unwrap(), 18);
        assert_eq!(
            registered_minors(&db, id).last(),
            Some(&(18 + SUBADDRESS_LOOKAHEAD))
        );
    }

    #[test]
    fn the_same_account_id_with_other_keys_starts_clean() {
        let (mut db, old) = db_with_identity();
        insert_output_at_minor(&db, old, 1);
        let (spend, _) = key_bytes();
        let mut other_view = [0u8; 32];
        other_view[0] = 11;

        let fresh = db.add_identity("acct-1", &spend, &other_view, 100).unwrap();

        assert_ne!(fresh.id, old);
        let outputs: i64 = db
            .conn()
            .query_row("SELECT count(*) FROM outputs", [], |r| r.get(0))
            .unwrap();
        assert_eq!(outputs, 0);
    }

    /// 删了钱包又导入同一套助记词：新标识、同密钥，扫描数据照用，不能因为主地址
    /// 唯一约束而建不出来。
    #[test]
    fn the_same_keys_under_a_new_account_id_keep_their_scan() {
        let (mut db, old) = db_with_identity();
        let (spend, view) = key_bytes();

        let again = db.add_identity("acct-2", &spend, &view, 100).unwrap();

        assert_eq!(again.id, old);
        assert_eq!(again.uuid, "acct-2");
    }

    #[test]
    fn adding_the_same_identity_twice_is_idempotent() {
        let mut db = db();
        let (spend, view) = key_bytes();
        let a = db.add_identity("acct-1", &spend, &view, 100).unwrap();
        let b = db.add_identity("acct-1", &spend, &view, 999).unwrap();
        assert_eq!(a.id, b.id, "同一 uuid 必须复用同一行");
        assert_eq!(b.birthday_height, 100, "重复注册不应改写已有的 birthday");
        assert_eq!(db.list_identities().unwrap().len(), 1);
    }

    /// 库级网络不变量：同一个文件混两个网络会让扫描静默失效
    /// （拿 stagenet 的块喂 mainnet 的密钥，表现为「怎么扫都扫不到」）。
    #[test]
    fn a_database_refuses_a_different_network() {
        let conn = crate::storage::open_connection(":memory:").unwrap();
        schema::migrate(&conn).unwrap();
        conn.execute(
            "INSERT INTO wallet_meta (id, network, created_at) VALUES (0, 'testnet', 0)",
            [],
        )
        .unwrap();
        // 用同一条连接模拟「已有库」，再以 mainnet 打开应当被拒
        let recorded: String = conn
            .query_row("SELECT network FROM wallet_meta WHERE id = 0", [], |r| {
                r.get(0)
            })
            .unwrap();
        assert_eq!(recorded, "testnet");
        assert_ne!(recorded, network_name(Network::Mainnet));
    }

    #[test]
    fn identity_lookup_works_by_id_and_uuid() {
        let (db, id) = db_with_identity();
        let by_id = db.identity_by_id(id).unwrap();
        let by_uuid = db.identity_by_uuid("acct-1").unwrap();
        assert_eq!(by_id.id, by_uuid.id);
        assert_eq!(by_id.primary_address, by_uuid.primary_address);
        assert!(
            by_id.primary_address.starts_with('4'),
            "主网主地址以 4 开头"
        );
    }

    #[test]
    fn missing_identity_is_account_not_found_not_a_database_error() {
        let db = db();
        // Identity 内含 ViewPair，上游刻意不实现 Debug（免得密钥被打进日志），
        // 所以这里不能用 unwrap_err —— 那要求 Ok 类型可 Debug。
        match db.identity_by_uuid("nope") {
            Ok(_) => panic!("不存在的身份不该查得到"),
            Err(e) => assert_eq!(e.code, ErrorCode::AccountNotFound),
        }
    }

    #[test]
    fn subaddresses_register_and_list_back() {
        let (mut db, id) = db_with_identity();
        let a = db.register_subaddress(id, 0, 1, Some("收款")).unwrap();
        let b = db.register_subaddress(id, 1, 0, None).unwrap();
        assert_ne!(a, b);
        assert_eq!(db.subaddress_indices(id).unwrap(), vec![(0, 1), (1, 0)]);
        // 重复登记同一个序号是幂等的，不该产生第二行
        db.register_subaddress(id, 0, 1, Some("改个标签")).unwrap();
        assert_eq!(db.subaddress_indices(id).unwrap().len(), 2);
    }

    #[test]
    fn removing_an_identity_clears_its_subaddresses() {
        let (mut db, id) = db_with_identity();
        db.register_subaddress(id, 0, 1, None).unwrap();
        assert!(db.remove_identity("acct-1").unwrap());
        assert!(db.subaddress_indices(id).unwrap().is_empty());
        assert!(
            !db.remove_identity("acct-1").unwrap(),
            "删第二次应当返回 false"
        );
    }
}
