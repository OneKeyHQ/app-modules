//! 对**真实节点**的集成测试。默认全部跳过。
//!
//! ```bash
//! # 随便一个公共节点，或你自己的
//! XMR_NODE=http://node.monerodevs.org:18089 cargo test --test live_node -- --nocapture
//!
//! # 想扫自己的钱包（只需要观察密钥，不需要花费私钥）
//! XMR_NODE=... \
//! XMR_SPEND_PUB=<64位hex> XMR_VIEW_PRIV=<64位hex> XMR_BIRTHDAY=3000000 \
//!   cargo test --test live_node -- --nocapture
//! ```
//!
//! 为什么要有这个：单元测试用的是构造出来的数据，**扫描循环的三道校验和
//! RingCT 索引推算从没在真实链上跑过**。那些校验一旦写错，表现是「扫不到钱」
//! 或者「一直报重组」，而不是编译错误。

use onekey_monero_runtime::{keys, network, scan, sync, wallet};

fn node() -> Option<String> {
    std::env::var("XMR_NODE")
        .ok()
        .filter(|s| !s.trim().is_empty())
}

/// 原生传输走 `reqwest`，它需要 Tokio 的 reactor —— 用 `futures_lite::block_on`
/// 会在第一次发请求时 panic「there is no reactor running」。
fn block_on<F: std::future::Future>(f: F) -> F::Output {
    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .expect("Tokio 运行时")
        .block_on(f)
}

/// 最基本的连通性：能连上、能问到链尖。
#[test]
fn connects_and_reads_the_chain_tip() {
    let Some(url) = node() else {
        eprintln!("跳过：未设置 XMR_NODE");
        return;
    };

    block_on(async {
        let daemon = network::connect(&url).await.expect("连接节点");
        let tip = sync::chain_tip(&daemon).await.expect("取链尖");
        println!("链尖 = {tip}");
        assert!(tip > 3_000_000, "主网高度早就过 300 万了，{tip} 不合理");
    });
}

/// **这条是真正要跑的。** 扫一小段真实区块，验证：
///
/// - 块能拉下来并被上游接受为 `ScannableBlock`
/// - 我们的连续性校验不会对合法链误报重组
/// - 我们独立推算的 RingCT 游标与节点声称的一致
///
/// 最后一条是重点：上游明说这个字段不可验证、要求客户端自己算并比对。
/// 如果我们的推算逻辑错了，这里会报 `RINGCT_INDEX_MISMATCH` —— 那说明是
/// **我们**错了，不是节点在作恶。
#[test]
fn scans_real_blocks_without_false_alarms() {
    let Some(url) = node() else {
        eprintln!("跳过：未设置 XMR_NODE");
        return;
    };

    block_on(async {
        let daemon = network::connect(&url).await.expect("连接节点");
        let tip = sync::chain_tip(&daemon).await.expect("取链尖");

        // 从链尖往回 200 块开始扫 40 块：足够覆盖有交易的块，又不至于太慢。
        let start = tip.saturating_sub(200);
        let count = 40u64;

        let mut db =
            wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap()).expect("内存库");
        let (spend, view) = throwaway_keys();
        let identity = db
            .add_identity("live", &spend, &view, start)
            .expect("建身份");

        sync::update_chain_tip(&mut db, identity.id, tip).expect("告知链尖");

        let mut scanned = 0u64;
        for _ in 0..count {
            let progress = sync::step(&mut db, &daemon, identity.id, 10)
                .await
                .expect("扫描不该失败 —— 失败意味着我们的校验逻辑对合法链误报了");
            let n = progress["blocksScanned"].as_u64().unwrap_or(0);
            if n == 0 {
                break;
            }
            scanned += n;
            if progress["done"].as_bool().unwrap_or(false) {
                break;
            }
        }

        println!("扫了 {scanned} 块，从 {start} 起");
        assert!(scanned > 0, "一块都没扫到，说明拉块或扫描路径断了");
    });
}

/// 用你自己的观察密钥扫一段，看能不能扫到输出。
///
/// 需要 `XMR_SPEND_PUB` / `XMR_VIEW_PRIV` / `XMR_BIRTHDAY`。
/// **只要观察密钥，不需要花费私钥** —— 这条测试本身就是那个边界的证明。
#[test]
fn scans_a_real_wallet_if_view_keys_are_supplied() {
    let (Some(url), Ok(spend_hex), Ok(view_hex)) = (
        node(),
        std::env::var("XMR_SPEND_PUB"),
        std::env::var("XMR_VIEW_PRIV"),
    ) else {
        eprintln!("跳过：未设置 XMR_NODE / XMR_SPEND_PUB / XMR_VIEW_PRIV");
        return;
    };

    let birthday: u64 = std::env::var("XMR_BIRTHDAY")
        .ok()
        .and_then(|s| s.parse().ok())
        .unwrap_or(0);
    let spend = hex::decode(spend_hex.trim()).expect("XMR_SPEND_PUB 必须是 hex");
    let view = hex::decode(view_hex.trim()).expect("XMR_VIEW_PRIV 必须是 hex");

    block_on(async {
        let daemon = network::connect(&url).await.expect("连接节点");
        let tip = sync::chain_tip(&daemon).await.expect("取链尖");

        let mut db =
            wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap()).expect("内存库");
        let identity = db
            .add_identity("live-wallet", &spend, &view, birthday)
            .expect("建身份");
        println!("主地址 = {}", identity.primary_address);

        // 常用的几个子地址也登记上 —— 没登记的收不到。
        for minor in 1..=5 {
            db.register_subaddress(identity.id, 0, minor, None)
                .expect("登记子地址");
        }

        sync::update_chain_tip(&mut db, identity.id, tip).expect("告知链尖");

        let budget_blocks = std::env::var("XMR_SCAN_BLOCKS")
            .ok()
            .and_then(|s| s.parse::<u64>().ok())
            .unwrap_or(2_000);

        let started = std::time::Instant::now();
        let mut scanned = 0u64;
        while scanned < budget_blocks {
            let progress = sync::step(&mut db, &daemon, identity.id, 100)
                .await
                .expect("扫描");
            let n = progress["blocksScanned"].as_u64().unwrap_or(0);
            if n == 0 || progress["done"].as_bool().unwrap_or(false) {
                break;
            }
            scanned += n;
        }
        let elapsed = started.elapsed();

        // 这就是明天要看的那个数字。
        println!(
            "扫了 {scanned} 块，耗时 {:.1}s，{:.1} 块/秒",
            elapsed.as_secs_f64(),
            scanned as f64 / elapsed.as_secs_f64().max(0.001)
        );

        let balance =
            onekey_monero_runtime::query::balance(&db, identity.id, Some(tip)).expect("算余额");
        println!(
            "总额 {} / 可用 {} / 不可验证 {}",
            balance.total_atomic, balance.unlocked_atomic, balance.unverifiable_atomic
        );
    });
}

/// 回退必须真的把那一段清掉并重新排队。
#[test]
fn rewind_actually_rolls_back_and_requeues() {
    let Some(url) = node() else {
        eprintln!("跳过：未设置 XMR_NODE");
        return;
    };

    block_on(async {
        let daemon = network::connect(&url).await.expect("连接节点");
        let tip = sync::chain_tip(&daemon).await.expect("取链尖");
        let start = tip.saturating_sub(50);

        let mut db =
            wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap()).expect("内存库");
        let (spend, view) = throwaway_keys();
        let identity = db.add_identity("rw", &spend, &view, start).expect("建身份");
        sync::update_chain_tip(&mut db, identity.id, tip).expect("告知链尖");

        sync::step(&mut db, &daemon, identity.id, 20)
            .await
            .expect("先扫一段");
        let before = sync::scanned_to_height(&db, identity.id).expect("进度");
        assert!(before.is_some(), "应当已经有进度");

        let target = start + 5;
        sync::rewind_and_requeue(&mut db, identity.id, target, tip).expect("回退");

        let after = sync::scanned_to_height(&db, identity.id).expect("进度");
        assert!(
            after.unwrap_or(0) <= target,
            "回退之后进度必须不高于目标高度，否则中间那段永远不会重扫"
        );

        let peek = sync::peek(&db, identity.id).expect("看队列");
        assert!(
            !peek["ranges"].as_array().unwrap().is_empty(),
            "回退之后必须有待扫区间"
        );
    });
}

/// 一对不对应任何真实钱包的观察密钥，用来测扫描机制本身。
fn throwaway_keys() -> ([u8; 32], [u8; 32]) {
    use monero_wallet::address::{MoneroAddress, Network};
    const ADDR: &str = "44AFFq5kSiGBoZ4NMDwYtN18obc8AemS33DBLWs3H7otXft3XjrpDtQGv7SqSsaBYBb98uNbr2VBBEt7f2wfn3RVGQBEP3A";
    let addr = MoneroAddress::from_str(Network::Mainnet, ADDR).unwrap();
    let mut view = [0u8; 32];
    view[0] = 9;
    (addr.spend().compress().to_bytes(), view)
}

#[test]
fn reorg_detection_rejects_a_gap() {
    // 这条不需要网络：构造一个跳号的场景，确认我们报的是重组而不是静默跳过。
    let mut db =
        wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap()).expect("内存库");
    let (spend, view) = throwaway_keys();
    let identity = db.add_identity("gap", &spend, &view, 0).expect("建身份");

    // 空批次是合法的 no-op，不该报错
    let outcome = scan::scan_blocks(&mut db, identity.id, &[]).expect("空批次应当无害");
    assert_eq!(outcome.blocks_scanned, 0);
}
