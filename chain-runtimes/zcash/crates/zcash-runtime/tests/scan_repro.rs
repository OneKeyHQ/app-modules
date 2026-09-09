//! 原生复现：在本机跑与浏览器完全相同的扫描流程。
//!
//! 目的是把「batchSize ≥ 25 挂死」从 wasm 里搬到原生环境 —— 那边能上真调试器、
//! 能看堆栈、能 `sample` 采样，而在浏览器里只能看到页面无响应。
//!
//! 需要联网，所以默认 `#[ignore]`。跑法：
//!
//! ```bash
//! cargo test -p onekey-zcash-runtime --test scan_repro -- --ignored --nocapture
//! # 卡住时另开一个终端：
//! #   sample <pid> 5 -f /tmp/scan.txt     # macOS
//! ```

use onekey_zcash_runtime::{account, network, sync, wallet};

// 原生 gRPC 端点（与浏览器用的 gRPC-web 端点不同）。
// chainsafe 那个是 gRPC-web 代理，原生连不上；zec.rocks 反过来。
const LWD: &str = "https://testnet.zec.rocks:443";

/// Alice 的 testnet UFVK —— 公开测试向量，切勿用于真实资金。
const ALICE_UFVK: &str = "uviewtest1eacc7lytmvgp0sshwjjv4qsg9fnewq00s6zye8hqwndpdsg0tum2ft4k96t86eapddpq56exfycnxnlds75vvpydv8fgj4cecczkmt3rjat8qjfqrk2cdlm9alep2z04785sx6yekqjk6wywkttlthld4c3xmg8fvneg4p97vzxwu9xtuh0xrgfy90p6uuxf8cwl8nxfq6hlte0nnylk59xceldrkx9vge3k4utkue2txu5kpp60aw07q0f0jgp0pv2c0gr7jdm6273uxyskt72jehte5jf2dg94d84le08h2t5rhd93j2d98ja59h46est69f3a7rav7k6744p2u8dxasc7nr9p2k95x7uaknahj0kw7mu5zq9nllj7x2qswq3jswsuzwms7shv7dhxz9s4yudatwu3u3v3wqznkhu6jt7xt8whjh3dkzvsf28p6mj8tya009gwzgszz2at8alquu8y0fmqt7klayrjx7n3ulml5q00fgdr";

async fn scan_with_batch(batch_size: u32) {
    let db_name = format!("/tmp/zcash-repro-{batch_size}.sqlite");
    let _ = std::fs::remove_file(&db_name);

    let mut db = wallet::open_and_migrate("test", &db_name).expect("开库应成功");
    let mut client = network::connect_native(LWD)
        .await
        .expect("应能连上 lightwalletd");

    let tip = network::chain_tip(&mut client).await.expect("应能取链尖");
    println!("链尖 = {tip}");

    let uuid = account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 200, None)
        .await
        .expect("导入账户应成功");
    println!("账户 = {uuid}");

    let prep = sync::prepare(&mut db, &mut client)
        .await
        .expect("准备应成功");
    println!("prepare = {prep}");

    println!("peek = {}", sync::peek(&db).expect("peek 应成功"));

    let t0 = std::time::Instant::now();
    println!("开始 syncStep(batch_size={batch_size}, max_batches=1) …");
    let out = sync::step(
        &mut db,
        &mut client,
        batch_size,
        1,
        None,
        &[ALICE_UFVK.to_owned()],
    )
    .await
    .expect("扫描应成功");
    println!("耗时 {:?}  结果 {out}", t0.elapsed());
}

#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_batch_10() {
    scan_with_batch(10).await;
}

/// 浏览器里这个批量会永久挂死。原生跑一遍就能区分：
/// 若这里也挂 → 是上游扫描逻辑；若这里正常 → 是 wasm / 存储层特有的问题。
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_batch_25() {
    scan_with_batch(25).await;
}

#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_batch_100() {
    scan_with_batch(100).await;
}

/// 扫描指定高度区间。浏览器里 trace 显示扫描停在某个具体高度上，
/// 而之前的原生测试都从 tip-200 / tip-2000 起步，**根本没覆盖到那一段**。
///
/// 用法（高度和批量都可覆盖）：
/// ```bash
/// ZR_FROM=4292840 ZR_BATCH=20 cargo test -p onekey-zcash-runtime \
///   --test scan_repro scan_from_height -- --ignored --nocapture
/// ```
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_from_height() {
    let from: u32 = std::env::var("ZR_FROM")
        .ok()
        .and_then(|v| v.parse().ok())
        .unwrap_or(4292840);
    let batch: u32 = std::env::var("ZR_BATCH")
        .ok()
        .and_then(|v| v.parse().ok())
        .unwrap_or(20);

    let db_name = format!("/tmp/zcash-from-{from}.sqlite");
    let _ = std::fs::remove_file(&db_name);

    let mut db = wallet::open_and_migrate("test", &db_name).expect("开库应成功");
    let mut client = network::connect_native(LWD)
        .await
        .expect("应能连上 lightwalletd");
    let tip = network::chain_tip(&mut client).await.expect("应能取链尖");
    println!("链尖 = {tip}，从 {from} 起扫 {batch} 块");

    account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, from, None)
        .await
        .expect("导入账户应成功");
    sync::prepare(&mut db, &mut client)
        .await
        .expect("准备应成功");
    println!("peek = {}", sync::peek(&db).expect("peek 应成功"));

    let t0 = std::time::Instant::now();
    let out = sync::step(
        &mut db,
        &mut client,
        batch,
        1,
        None,
        &[ALICE_UFVK.to_owned()],
    )
    .await
    .expect("扫描应成功");
    println!("耗时 {:?}  结果 {out}", t0.elapsed());
}

/// 原生侧的批量耗时曲线。浏览器上批量越大越不成比例地慢，先确认原生是不是也这样：
/// 若原生也非线性 → 是算法问题，可以在原生上用采样分析器直接定位；
/// 若原生始终线性 → 非线性是 wasm 特有的，与上游无关。
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_batch_curve() {
    for batch in [25u32, 100, 400, 1000] {
        let db_name = format!("/tmp/zcash-curve-{batch}.sqlite");
        let _ = std::fs::remove_file(&db_name);

        let mut db = wallet::open_and_migrate("test", &db_name).expect("开库应成功");
        let mut client = network::connect_native(LWD)
            .await
            .expect("应能连上 lightwalletd");
        let tip = network::chain_tip(&mut client).await.expect("应能取链尖");

        account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 2000, None)
            .await
            .expect("导入账户应成功");
        sync::prepare(&mut db, &mut client)
            .await
            .expect("准备应成功");

        let t0 = std::time::Instant::now();
        let out = sync::step(
            &mut db,
            &mut client,
            batch,
            1,
            None,
            &[ALICE_UFVK.to_owned()],
        )
        .await
        .expect("扫描应成功");
        println!("批量 {batch:>5}  总计 {:>10?}  {out}", t0.elapsed());
    }
}

/// 与浏览器 `bisect.html` 完全同构：逐块推进 40 次。
///
/// 浏览器在第 7 块停住。如果原生也停在某个高度 → 是那个区块的内容；
/// 如果原生一路跑完 → 与区块无关，是 wasm 侧特有的。
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn scan_one_by_one() {
    let db_name = "/tmp/zcash-repro-onebyone.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("开库应成功");
    let mut client = network::connect_native(LWD)
        .await
        .expect("应能连上 lightwalletd");
    let tip = network::chain_tip(&mut client).await.expect("应能取链尖");
    println!("链尖 = {tip}");

    account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 200, None)
        .await
        .expect("导入账户应成功");
    sync::prepare(&mut db, &mut client)
        .await
        .expect("准备应成功");

    for i in 0..40 {
        let peek: serde_json::Value =
            serde_json::from_str(&sync::peek(&db).expect("peek 应成功")).unwrap();
        let height = peek["ranges"][0]["start"].as_u64();
        let t0 = std::time::Instant::now();
        let out = sync::step(&mut db, &mut client, 1, 1, None, &[ALICE_UFVK.to_owned()])
            .await
            .expect("扫描应成功");
        println!("#{i} h={height:?} {:?} {out}", t0.elapsed());
    }
}

/// 打印某个高度区块的结构摘要，用于和浏览器侧逐字段比对。
///
/// ```bash
/// ZR_FROM=4292845 ZR_SPAN=3 cargo test -p onekey-zcash-runtime \
///   --test scan_repro block_summary_at -- --ignored --nocapture
/// ```
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn block_summary_at() {
    let from: u32 = std::env::var("ZR_FROM")
        .ok()
        .and_then(|v| v.parse().ok())
        .unwrap_or(4292845);
    let span: u32 = std::env::var("ZR_SPAN")
        .ok()
        .and_then(|v| v.parse().ok())
        .unwrap_or(1);

    let mut client = network::connect_native(LWD)
        .await
        .expect("应能连上 lightwalletd");
    for h in from..from + span {
        match network::block_summary(&mut client, h).await {
            Ok(s) => println!("{s}"),
            Err(e) => println!("{{\"height\":{h},\"error\":\"{e}\"}}"),
        }
    }
}

/// 审查报告称 `accounts.uuid` 是 BLOB，而 history.rs 用字符串绑定，
/// 导致历史查询恒为空。这条测试实测两种绑定的匹配数，不靠读 schema 推断。
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn uuid_binding_string_vs_blob() {
    let db_name = "/tmp/zcash-uuid-binding.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("开库应成功");
    let mut client = network::connect_native(LWD).await.expect("应能连上");
    let tip = network::chain_tip(&mut client).await.expect("取链尖");
    let uuid_str = account::import_ufvk(&mut db, &mut client, "a", ALICE_UFVK, tip - 10, None)
        .await
        .expect("导入账户");
    println!("listAccounts 给出的形式: {uuid_str}");

    drop(db);
    let conn = rusqlite::Connection::open(db_name).expect("直接打开同一个库");
    let as_text: i64 = conn
        .query_row(
            "SELECT COUNT(*) FROM accounts WHERE uuid = ?1",
            [&uuid_str],
            |r| r.get(0),
        )
        .unwrap();
    let bytes = uuid::Uuid::parse_str(&uuid_str).unwrap();
    let as_blob: i64 = conn
        .query_row(
            "SELECT COUNT(*) FROM accounts WHERE uuid = ?1",
            [bytes.as_bytes().as_slice()],
            |r| r.get(0),
        )
        .unwrap();
    let total: i64 = conn
        .query_row("SELECT COUNT(*) FROM accounts", [], |r| r.get(0))
        .unwrap();

    println!("accounts 总数={total}  字符串绑定匹配={as_text}  BLOB 绑定匹配={as_blob}");
    assert_eq!(total, 1, "应当只有一个账户");
    assert_eq!(as_blob, 1, "BLOB 绑定必须匹配到");
    assert_eq!(as_text, 0, "字符串绑定匹配不到 —— 这正是历史恒空的原因");
}

/// 回归：历史查询必须真的能匹配到账户。
///
/// 之前这里报过假绿 —— 查询用文本绑定 BLOB 列，一行都匹配不上，
/// 而「空结果」和「空钱包」返回值一样，看不出坏。所以这条测试**不看返回内容**，
/// 只验证「用同一个 uuid 能在 v_transactions 上匹配到与直接查库一致的条数」。
#[tokio::test(flavor = "multi_thread")]
#[ignore = "需要联网"]
async fn history_uuid_actually_matches() {
    let db_name = "/tmp/zcash-history-uuid.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("开库");
    let mut client = network::connect_native(LWD).await.expect("连上");
    let tip = network::chain_tip(&mut client).await.expect("链尖");
    let uuid = account::import_ufvk(&mut db, &mut client, "a", ALICE_UFVK, tip - 10, None)
        .await
        .expect("导入");
    drop(db);

    let conn = rusqlite::Connection::open(db_name).expect("直接开库");
    let via_join: i64 = conn
        .query_row(
            "SELECT COUNT(*) FROM v_transactions v
             JOIN accounts a ON a.uuid = v.account_uuid
             WHERE a.uuid = ?1",
            [uuid::Uuid::parse_str(&uuid).unwrap().as_bytes().as_slice()],
            |r| r.get(0),
        )
        .unwrap();
    let history = onekey_zcash_runtime::history::list(&conn, &uuid, 500, 0).expect("history");
    let via_api = serde_json::from_str::<Vec<serde_json::Value>>(&history)
        .expect("history JSON")
        .len();
    println!("直接查={via_join}  history() 给出={via_api}");
    assert_eq!(
        via_api as i64, via_join,
        "history() 必须与直接查一致，而不是恒空"
    );
}
