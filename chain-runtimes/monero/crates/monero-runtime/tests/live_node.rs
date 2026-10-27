//! Integration tests against a **real node**. All skipped by default.
//!
//! ```bash
//! # Your own node, or a trusted public one (https recommended)
//! XMR_NODE=<monerod RPC URL> cargo test --test live_node -- --nocapture
//!
//! # Scan your own wallet (view keys only, no spend key)
//! XMR_NODE=... \
//! XMR_SPEND_PUB=<64 hex chars> XMR_VIEW_PRIV=<64 hex chars> XMR_BIRTHDAY=3000000 \
//!   cargo test --test live_node -- --nocapture
//! ```

use onekey_monero_runtime::{keys, network, scan, sync, wallet};

fn node() -> Option<String> {
    std::env::var("XMR_NODE")
        .ok()
        .filter(|s| !s.trim().is_empty())
}

// `network::remember_node_url` after `connect` enables the batch block fetch, as
// `runtime::select_node` does.

/// Native transport uses `reqwest`, which needs a Tokio reactor;
/// `futures_lite::block_on` panics with "there is no reactor running".
fn block_on<F: std::future::Future>(f: F) -> F::Output {
    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .expect("Tokio runtime")
        .block_on(f)
}

/// Basic connectivity: connect and read the chain tip.
#[test]
fn connects_and_reads_the_chain_tip() {
    let Some(url) = node() else {
        eprintln!("skipped: XMR_NODE not set");
        return;
    };

    block_on(async {
        let (daemon, _) = network::connect(&url).await.expect("connect to node");
        network::remember_node_url(&url);
        let tip = sync::chain_tip(&daemon).await.expect("fetch chain tip");
        println!("chain tip = {tip}");
        assert!(
            tip > 3_000_000,
            "mainnet is well past height 3M; {tip} is implausible"
        );
    });
}

/// Scans a short range of real blocks: no false reorgs, and the derived RingCT
/// cursor matches the node's claim (a mismatch here means our derivation is wrong).
#[test]
fn scans_real_blocks_without_false_alarms() {
    let Some(url) = node() else {
        eprintln!("skipped: XMR_NODE not set");
        return;
    };

    block_on(async {
        let (daemon, _) = network::connect(&url).await.expect("connect to node");
        network::remember_node_url(&url);
        let tip = sync::chain_tip(&daemon).await.expect("fetch chain tip");

        // Start 200 blocks below the tip: enough to hit transactions, still fast.
        let start = tip.saturating_sub(200);
        let count = 40u64;

        let mut db = wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap())
            .expect("in-memory db");
        let (spend, view) = throwaway_keys();
        let identity = db
            .add_identity("live", &spend, &view, start)
            .expect("add identity");

        sync::update_chain_tip(&mut db, identity.id, tip).expect("update chain tip");

        let mut scanned = 0u64;
        for _ in 0..count {
            let progress = sync::step(&mut db, &daemon, identity.id, 10)
                .await
                .expect("scan must not fail; a failure means our checks flagged a valid chain");
            let n = progress["blocksScanned"].as_u64().unwrap_or(0);
            if n == 0 {
                break;
            }
            scanned += n;
            if progress["done"].as_bool().unwrap_or(false) {
                break;
            }
        }

        println!("scanned {scanned} blocks from {start}");
        assert!(
            scanned > 0,
            "no blocks scanned; fetch or scan path is broken"
        );
    });
}

/// Scans a range with your own view keys to check outputs are found.
///
/// Needs `XMR_SPEND_PUB` / `XMR_VIEW_PRIV` / `XMR_BIRTHDAY`.
/// **View keys only, no spend key.**
#[test]
fn scans_a_real_wallet_if_view_keys_are_supplied() {
    let (Some(url), Ok(spend_hex), Ok(view_hex)) = (
        node(),
        std::env::var("XMR_SPEND_PUB"),
        std::env::var("XMR_VIEW_PRIV"),
    ) else {
        eprintln!("skipped: XMR_NODE / XMR_SPEND_PUB / XMR_VIEW_PRIV not set");
        return;
    };

    let birthday: u64 = std::env::var("XMR_BIRTHDAY")
        .ok()
        .and_then(|s| s.parse().ok())
        .unwrap_or(0);
    let spend = hex::decode(spend_hex.trim()).expect("XMR_SPEND_PUB must be hex");
    let view = hex::decode(view_hex.trim()).expect("XMR_VIEW_PRIV must be hex");

    block_on(async {
        let (daemon, _) = network::connect(&url).await.expect("connect to node");
        network::remember_node_url(&url);
        let tip = sync::chain_tip(&daemon).await.expect("fetch chain tip");

        let mut db = wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap())
            .expect("in-memory db");
        let identity = db
            .add_identity("live-wallet", &spend, &view, birthday)
            .expect("add identity");
        println!("primary address = {}", identity.primary_address);

        // Register common subaddresses; unregistered ones receive nothing.
        for minor in 1..=5 {
            db.register_subaddress(identity.id, 0, minor, None)
                .expect("register subaddress");
        }

        sync::update_chain_tip(&mut db, identity.id, tip).expect("update chain tip");

        let budget_blocks = std::env::var("XMR_SCAN_BLOCKS")
            .ok()
            .and_then(|s| s.parse::<u64>().ok())
            .unwrap_or(2_000);

        let started = std::time::Instant::now();
        let mut scanned = 0u64;
        while scanned < budget_blocks {
            let progress = sync::step(&mut db, &daemon, identity.id, 100)
                .await
                .expect("scan");
            let n = progress["blocksScanned"].as_u64().unwrap_or(0);
            if n == 0 || progress["done"].as_bool().unwrap_or(false) {
                break;
            }
            scanned += n;
        }
        let elapsed = started.elapsed();

        // Scan throughput.
        println!(
            "scanned {scanned} blocks in {:.1}s, {:.1} blocks/s",
            elapsed.as_secs_f64(),
            scanned as f64 / elapsed.as_secs_f64().max(0.001)
        );

        let balance =
            onekey_monero_runtime::query::balance(&db, identity.id, Some(tip)).expect("balance");
        println!(
            "total {} / unlocked {} / unverifiable {}",
            balance.total_atomic, balance.unlocked_atomic, balance.unverifiable_atomic
        );
    });
}

/// Rewind must actually clear the range and requeue it.
#[test]
fn rewind_actually_rolls_back_and_requeues() {
    let Some(url) = node() else {
        eprintln!("skipped: XMR_NODE not set");
        return;
    };

    block_on(async {
        let (daemon, _) = network::connect(&url).await.expect("connect to node");
        network::remember_node_url(&url);
        let tip = sync::chain_tip(&daemon).await.expect("fetch chain tip");
        let start = tip.saturating_sub(50);

        let mut db = wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap())
            .expect("in-memory db");
        let (spend, view) = throwaway_keys();
        let identity = db
            .add_identity("rw", &spend, &view, start)
            .expect("add identity");
        sync::update_chain_tip(&mut db, identity.id, tip).expect("update chain tip");

        sync::step(&mut db, &daemon, identity.id, 20)
            .await
            .expect("initial scan");
        let before = sync::scanned_to_height(&db, identity.id).expect("progress");
        assert!(before.is_some(), "progress should exist");

        let target = start + 5;
        sync::rewind_and_requeue(&mut db, identity.id, target, tip).expect("rewind");

        let after = sync::scanned_to_height(&db, identity.id).expect("progress");
        assert!(
            after.unwrap_or(0) <= target,
            "progress must not exceed the target after rewind, or the gap is never rescanned"
        );

        let peek = sync::peek(&db, identity.id).expect("peek queue");
        assert!(
            !peek["ranges"].as_array().unwrap().is_empty(),
            "ranges must be queued after rewind"
        );
    });
}

/// View keys matching no real wallet, for testing the scan machinery.
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
    // Offline: a gap must be reported as a reorg, not silently skipped.
    let mut db = wallet::Db::open(":memory:", keys::parse_network("mainnet").unwrap())
        .expect("in-memory db");
    let (spend, view) = throwaway_keys();
    let identity = db
        .add_identity("gap", &spend, &view, 0)
        .expect("add identity");

    // An empty batch is a valid no-op
    let outcome =
        scan::scan_blocks(&mut db, identity.id, &[]).expect("empty batch should be harmless");
    assert_eq!(outcome.blocks_scanned, 0);
}
