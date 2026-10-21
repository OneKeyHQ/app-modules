//! Runs the browser scan flow natively, where a debugger and `sample` are available.
//!
//! Needs network, so `#[ignore]` by default. Run with:
//!
//! ```bash
//! cargo test -p onekey-zcash-runtime --test scan_repro -- --ignored --nocapture
//! # If it hangs, from another terminal:
//! #   sample <pid> 5 -f /tmp/scan.txt     # macOS
//! ```

use onekey_zcash_runtime::{account, network, sync, wallet};

// Native gRPC endpoint. The chainsafe endpoint is a gRPC-web proxy that native
// clients cannot use; zec.rocks is the reverse.
const LWD: &str = "https://testnet.zec.rocks:443";

/// Alice's testnet UFVK, a public test vector. Never use for real funds.
const ALICE_UFVK: &str = "uviewtest1eacc7lytmvgp0sshwjjv4qsg9fnewq00s6zye8hqwndpdsg0tum2ft4k96t86eapddpq56exfycnxnlds75vvpydv8fgj4cecczkmt3rjat8qjfqrk2cdlm9alep2z04785sx6yekqjk6wywkttlthld4c3xmg8fvneg4p97vzxwu9xtuh0xrgfy90p6uuxf8cwl8nxfq6hlte0nnylk59xceldrkx9vge3k4utkue2txu5kpp60aw07q0f0jgp0pv2c0gr7jdm6273uxyskt72jehte5jf2dg94d84le08h2t5rhd93j2d98ja59h46est69f3a7rav7k6744p2u8dxasc7nr9p2k95x7uaknahj0kw7mu5zq9nllj7x2qswq3jswsuzwms7shv7dhxz9s4yudatwu3u3v3wqznkhu6jt7xt8whjh3dkzvsf28p6mj8tya009gwzgszz2at8alquu8y0fmqt7klayrjx7n3ulml5q00fgdr";

async fn scan_with_batch(batch_size: u32) {
    let db_name = format!("/tmp/zcash-repro-{batch_size}.sqlite");
    let _ = std::fs::remove_file(&db_name);

    let mut db = wallet::open_and_migrate("test", &db_name).expect("open wallet");
    let mut client = network::connect_native(LWD)
        .await
        .expect("connect to lightwalletd");

    let tip = network::chain_tip(&mut client).await.expect("chain tip");
    println!("tip = {tip}");

    let uuid = account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 200, None)
        .await
        .expect("import account");
    println!("account = {uuid}");

    let prep = sync::prepare(&mut db, &mut client).await.expect("prepare");
    println!("prepare = {prep}");

    println!("peek = {}", sync::peek(&db).expect("peek"));

    let t0 = std::time::Instant::now();
    println!("starting syncStep(batch_size={batch_size}, max_batches=1) …");
    let out = sync::step(
        &mut db,
        &mut client,
        batch_size,
        1,
        None,
        &[ALICE_UFVK.to_owned()],
    )
    .await
    .expect("scan");
    println!("elapsed {:?}  result {out}", t0.elapsed());
}

#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn scan_batch_10() {
    scan_with_batch(10).await;
}

/// A native hang points at upstream scanning; a pass points at wasm or the
/// storage layer.
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn scan_batch_25() {
    scan_with_batch(25).await;
}

#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn scan_batch_100() {
    scan_with_batch(100).await;
}

/// Scans from a given height (the other tests start at tip-200 / tip-2000).
///
/// Usage (height and batch size are overridable):
/// ```bash
/// ZR_FROM=4292840 ZR_BATCH=20 cargo test -p onekey-zcash-runtime \
///   --test scan_repro scan_from_height -- --ignored --nocapture
/// ```
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
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

    let mut db = wallet::open_and_migrate("test", &db_name).expect("open wallet");
    let mut client = network::connect_native(LWD)
        .await
        .expect("connect to lightwalletd");
    let tip = network::chain_tip(&mut client).await.expect("chain tip");
    println!("tip = {tip}, scanning {batch} blocks from {from}");

    account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, from, None)
        .await
        .expect("import account");
    sync::prepare(&mut db, &mut client).await.expect("prepare");
    println!("peek = {}", sync::peek(&db).expect("peek"));

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
    .expect("scan");
    println!("elapsed {:?}  result {out}", t0.elapsed());
}

/// Native scan time by batch size. Nonlinear here means an algorithmic issue to
/// profile natively; linear means the browser slowdown is wasm-specific.
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn scan_batch_curve() {
    for batch in [25u32, 100, 400, 1000] {
        let db_name = format!("/tmp/zcash-curve-{batch}.sqlite");
        let _ = std::fs::remove_file(&db_name);

        let mut db = wallet::open_and_migrate("test", &db_name).expect("open wallet");
        let mut client = network::connect_native(LWD)
            .await
            .expect("connect to lightwalletd");
        let tip = network::chain_tip(&mut client).await.expect("chain tip");

        account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 2000, None)
            .await
            .expect("import account");
        sync::prepare(&mut db, &mut client).await.expect("prepare");

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
        .expect("scan");
        println!("batch {batch:>5}  total {:>10?}  {out}", t0.elapsed());
    }
}

/// Advances one block at a time, 40 times.
/// A native stall points at block content; a full run points at wasm.
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn scan_one_by_one() {
    let db_name = "/tmp/zcash-repro-onebyone.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("open wallet");
    let mut client = network::connect_native(LWD)
        .await
        .expect("connect to lightwalletd");
    let tip = network::chain_tip(&mut client).await.expect("chain tip");
    println!("tip = {tip}");

    account::import_ufvk(&mut db, &mut client, "alice", ALICE_UFVK, tip - 200, None)
        .await
        .expect("import account");
    sync::prepare(&mut db, &mut client).await.expect("prepare");

    for i in 0..40 {
        let peek: serde_json::Value =
            serde_json::from_str(&sync::peek(&db).expect("peek")).unwrap();
        let height = peek["ranges"][0]["start"].as_u64();
        let t0 = std::time::Instant::now();
        let out = sync::step(&mut db, &mut client, 1, 1, None, &[ALICE_UFVK.to_owned()])
            .await
            .expect("scan");
        println!("#{i} h={height:?} {:?} {out}", t0.elapsed());
    }
}

/// Prints block structure summaries for field-by-field comparison with the browser.
///
/// ```bash
/// ZR_FROM=4292845 ZR_SPAN=3 cargo test -p onekey-zcash-runtime \
///   --test scan_repro block_summary_at -- --ignored --nocapture
/// ```
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
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
        .expect("connect to lightwalletd");
    for h in from..from + span {
        match network::block_summary(&mut client, h).await {
            Ok(s) => println!("{s}"),
            Err(e) => println!("{{\"height\":{h},\"error\":\"{e}\"}}"),
        }
    }
}

/// `accounts.uuid` is a BLOB: measures how many rows a string and a BLOB
/// binding match.
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn uuid_binding_string_vs_blob() {
    let db_name = "/tmp/zcash-uuid-binding.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("open wallet");
    let mut client = network::connect_native(LWD).await.expect("connect");
    let tip = network::chain_tip(&mut client).await.expect("chain tip");
    let uuid_str = account::import_ufvk(&mut db, &mut client, "a", ALICE_UFVK, tip - 10, None)
        .await
        .expect("import account");
    println!("listAccounts form: {uuid_str}");

    drop(db);
    let conn = rusqlite::Connection::open(db_name).expect("open the same database directly");
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

    println!(
        "accounts total={total}  string binding matched={as_text}  BLOB binding matched={as_blob}"
    );
    assert_eq!(total, 1, "exactly one account");
    assert_eq!(as_blob, 1, "BLOB binding must match");
    assert_eq!(
        as_text, 0,
        "string binding matches nothing, which leaves history empty"
    );
}

/// Regression: history must actually match the account.
///
/// An empty result looks like an empty wallet, so this compares the row count
/// from history() with a direct v_transactions query instead of the content.
#[tokio::test(flavor = "multi_thread")]
#[ignore = "requires network"]
async fn history_uuid_actually_matches() {
    let db_name = "/tmp/zcash-history-uuid.sqlite";
    let _ = std::fs::remove_file(db_name);

    let mut db = wallet::open_and_migrate("test", db_name).expect("open wallet");
    let mut client = network::connect_native(LWD).await.expect("connect");
    let tip = network::chain_tip(&mut client).await.expect("chain tip");
    let uuid = account::import_ufvk(&mut db, &mut client, "a", ALICE_UFVK, tip - 10, None)
        .await
        .expect("import account");
    drop(db);

    let conn = rusqlite::Connection::open(db_name).expect("open database directly");
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
    println!("direct={via_join}  history()={via_api}");
    assert_eq!(
        via_api as i64, via_join,
        "history() must match the direct query"
    );
}
