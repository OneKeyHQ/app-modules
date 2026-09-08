//! Isolated browser-storage benchmark for the developer Gallery.
//!
//! This is intentionally not a wallet abstraction. It owns a fixed synthetic
//! schema, exposes a small fixed action enum, and rejects every database name
//! outside the benchmark prefix. Product code must not depend on these APIs.

use std::{cell::RefCell, collections::BTreeMap, rc::Rc};

use rusqlite::{params, Connection, OpenFlags};
use serde::{Deserialize, Serialize};
use serde_json::{json, Value};
use sqlite_wasm_vfs::{
    relaxed_idb::{install as install_relaxed_idb, Preload, RelaxedIdbCfg, RelaxedIdbUtil},
    sahpool::{install as install_sahpool, OpfsSAHPoolCfg, OpfsSAHPoolUtil},
};

use crate::error::{ErrorCode, Result, RuntimeError};

const DATABASE_PREFIX: &str = "onekey-zcash-storage-bench-";
const RELAXED_VFS_NAME: &str = "onekey-zcash-benchmark-relaxed-idb";
const SAHPOOL_VFS_NAME: &str = "onekey-zcash-benchmark-opfs-sahpool";
const SAHPOOL_DIRECTORY: &str = ".onekey-zcash-benchmark-opfs-sahpool";
const SAHPOOL_CAPACITY: u32 = 6;
const MAX_ROWS: u32 = 20_000;
const MAX_PAYLOAD_BYTES: u32 = 16_384;
const MAX_BATCH_SIZE: u32 = 2_000;
const MAX_WRITER_ID: u32 = 1_000;

thread_local! {
    static RELAXED_UTIL: RefCell<Option<Rc<RelaxedIdbUtil>>> = const { RefCell::new(None) };
    static SAHPOOL_UTIL: RefCell<Option<Rc<OpfsSAHPoolUtil>>> = const { RefCell::new(None) };
}

#[derive(Clone, Copy, Debug, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub enum BenchmarkBackend {
    RelaxedIdb,
    OpfsSahpool,
}

impl BenchmarkBackend {
    fn vfs_name(self) -> &'static str {
        match self {
            Self::RelaxedIdb => RELAXED_VFS_NAME,
            Self::OpfsSahpool => SAHPOOL_VFS_NAME,
        }
    }
}

#[derive(Clone, Copy, Debug, Deserialize, Serialize, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub enum BenchmarkAction {
    Probe,
    Replace,
    Append,
    Verify,
    Mutate,
    Cleanup,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "camelCase", deny_unknown_fields)]
pub struct BenchmarkRequest {
    backend: BenchmarkBackend,
    action: BenchmarkAction,
    database_name: String,
    #[serde(default)]
    row_count: u32,
    #[serde(default = "default_payload_bytes")]
    payload_bytes: u32,
    #[serde(default = "default_batch_size")]
    batch_size: u32,
    #[serde(default)]
    writer_id: u32,
}

fn default_payload_bytes() -> u32 {
    1_024
}

fn default_batch_size() -> u32 {
    64
}

pub async fn run(request_json: &str) -> Result<String> {
    let request: BenchmarkRequest = serde_json::from_str(request_json).map_err(|e| {
        RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({ "operation": "storageBenchmarkConfig" }),
        )
        .detail(e)
    })?;
    validate_request(&request)?;

    let started = now_ms();
    let mut timings = BTreeMap::<&str, f64>::new();
    let memory_start = wasm_memory_bytes();

    let install_started = now_ms();
    let install_result = install_backend(request.backend).await;
    timings.insert("install", elapsed(install_started));

    if request.action == BenchmarkAction::Probe {
        return Ok(match install_result {
            Ok(()) => json!({
                "backend": request.backend,
                "action": request.action,
                "databaseName": request.database_name,
                "supported": true,
                "vfsName": request.backend.vfs_name(),
                "timingsMs": timings,
                "memoryBytes": {
                    "start": memory_start,
                    "afterAction": wasm_memory_bytes(),
                },
                "totalMs": elapsed(started),
            }),
            Err(error) => json!({
                "backend": request.backend,
                "action": request.action,
                "databaseName": request.database_name,
                "supported": false,
                "vfsName": request.backend.vfs_name(),
                "timingsMs": timings,
                "memoryBytes": {
                    "start": memory_start,
                    "afterAction": wasm_memory_bytes(),
                },
                "error": error.to_json(),
                "totalMs": elapsed(started),
            }),
        }
        .to_string());
    }
    install_result?;

    if request.action == BenchmarkAction::Cleanup {
        let cleanup_started = now_ms();
        let deleted = delete_database(request.backend, &request.database_name).await?;
        timings.insert("cleanup", elapsed(cleanup_started));
        return Ok(json!({
            "backend": request.backend,
            "action": request.action,
            "databaseName": request.database_name,
            "supported": true,
            "vfsName": request.backend.vfs_name(),
            "deleted": deleted,
            "timingsMs": timings,
            "memoryBytes": {
                "start": memory_start,
                "afterAction": wasm_memory_bytes(),
            },
            "totalMs": elapsed(started),
        })
        .to_string());
    }

    if request.action == BenchmarkAction::Replace {
        let cleanup_started = now_ms();
        let _ = delete_database(request.backend, &request.database_name).await?;
        timings.insert("cleanup", elapsed(cleanup_started));
    } else if matches!(request.backend, BenchmarkBackend::RelaxedIdb) {
        let preload_started = now_ms();
        relaxed_util()?
            .preload_db(vec![request.database_name.clone()])
            .await
            .map_err(|e| benchmark_error("preload", e))?;
        timings.insert("preload", elapsed(preload_started));
    }

    let open_started = now_ms();
    let mut conn = open_connection(&request)?;
    timings.insert("open", elapsed(open_started));
    let memory_after_open = wasm_memory_bytes();

    let schema_started = now_ms();
    configure_connection(&conn, request.backend)?;
    if matches!(request.action, BenchmarkAction::Replace) {
        create_schema(&conn)?;
    }
    timings.insert("schema", elapsed(schema_started));

    let mut action_metrics = json!({});
    match request.action {
        BenchmarkAction::Replace | BenchmarkAction::Append => {
            let write_started = now_ms();
            let batch_latencies = insert_rows(
                &mut conn,
                request.writer_id,
                request.row_count,
                request.payload_bytes,
                request.batch_size,
            )?;
            let write_ms = elapsed(write_started);
            timings.insert("write", write_ms);
            action_metrics = json!({
                "batchLatencyMs": latency_summary(&batch_latencies),
                "rowsPerSecond": rate(request.row_count as u64, write_ms),
                "payloadMiBPerSecond": rate(
                    request.row_count as u64 * request.payload_bytes as u64,
                    write_ms,
                ) / (1024.0 * 1024.0),
            });
        }
        BenchmarkAction::Verify => {}
        BenchmarkAction::Mutate => {
            let update_started = now_ms();
            let updated = conn.execute(
                "UPDATE benchmark_rows SET generation = generation + 1 WHERE row_index % 5 = 0",
                [],
            )?;
            timings.insert("update", elapsed(update_started));

            let delete_started = now_ms();
            let deleted = conn.execute("DELETE FROM benchmark_rows WHERE row_index % 7 = 0", [])?;
            timings.insert("delete", elapsed(delete_started));
            action_metrics = json!({ "updatedRows": updated, "deletedRows": deleted });
        }
        BenchmarkAction::Probe | BenchmarkAction::Cleanup => unreachable!(),
    }

    let read_started = now_ms();
    let (data, sample_metrics) = inspect_data(&conn)?;
    timings.insert("read", elapsed(read_started));

    let integrity_started = now_ms();
    let integrity = integrity(&conn)?;
    timings.insert("integrity", elapsed(integrity_started));
    let sqlite = sqlite_stats(&conn)?;
    drop(conn);

    Ok(json!({
        "backend": request.backend,
        "action": request.action,
        "databaseName": request.database_name,
        "supported": true,
        "vfsName": request.backend.vfs_name(),
        "workload": {
            "requestedRows": request.row_count,
            "payloadBytes": request.payload_bytes,
            "batchSize": request.batch_size,
            "writerId": request.writer_id,
        },
        "timingsMs": timings,
        "memoryBytes": {
            "start": memory_start,
            "afterOpen": memory_after_open,
            "afterAction": wasm_memory_bytes(),
        },
        "actionMetrics": action_metrics,
        "sampleMetrics": sample_metrics,
        "data": data,
        "integrity": integrity,
        "sqlite": sqlite,
        "totalMs": elapsed(started),
    })
    .to_string())
}

fn validate_request(request: &BenchmarkRequest) -> Result<()> {
    let valid_name = request.database_name.starts_with(DATABASE_PREFIX)
        && request.database_name.ends_with(".db")
        && request.database_name.len() <= 120
        && request
            .database_name
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || matches!(b, b'-' | b'_' | b'.'));
    if !valid_name {
        return Err(RuntimeError::with(
            ErrorCode::InvalidDbName,
            json!({ "value": request.database_name }),
        ));
    }
    if request.row_count > MAX_ROWS
        || request.payload_bytes == 0
        || request.payload_bytes > MAX_PAYLOAD_BYTES
        || request.batch_size == 0
        || request.batch_size > MAX_BATCH_SIZE
        || request.writer_id > MAX_WRITER_ID
    {
        return Err(RuntimeError::with(
            ErrorCode::DatabaseError,
            json!({
                "operation": "storageBenchmarkConfig",
                "rowCount": request.row_count,
                "payloadBytes": request.payload_bytes,
                "batchSize": request.batch_size,
                "writerId": request.writer_id,
            }),
        ));
    }
    Ok(())
}

async fn install_backend(backend: BenchmarkBackend) -> Result<()> {
    use sqlite_wasm_rs as ffi;

    match backend {
        BenchmarkBackend::RelaxedIdb => {
            let cfg = RelaxedIdbCfg {
                vfs_name: RELAXED_VFS_NAME.to_owned(),
                clear_on_init: false,
                preload: Preload::None,
            };
            let util = install_relaxed_idb::<ffi::WasmOsCallback>(&cfg, false)
                .await
                .map_err(|e| benchmark_error("installRelaxedIdb", e))?;
            RELAXED_UTIL.with(|slot| *slot.borrow_mut() = Some(Rc::new(util)));
        }
        BenchmarkBackend::OpfsSahpool => {
            let cfg = OpfsSAHPoolCfg {
                vfs_name: SAHPOOL_VFS_NAME.to_owned(),
                directory: SAHPOOL_DIRECTORY.to_owned(),
                clear_on_init: false,
                initial_capacity: SAHPOOL_CAPACITY,
            };
            let util = install_sahpool::<ffi::WasmOsCallback>(&cfg, false)
                .await
                .map_err(|e| benchmark_error("installOpfsSahpool", e))?;
            util.reserve_minimum_capacity(SAHPOOL_CAPACITY)
                .await
                .map_err(|e| benchmark_error("reserveOpfsSahpool", e))?;
            SAHPOOL_UTIL.with(|slot| *slot.borrow_mut() = Some(Rc::new(util)));
        }
    }
    Ok(())
}

fn relaxed_util() -> Result<Rc<RelaxedIdbUtil>> {
    RELAXED_UTIL
        .with(|slot| slot.borrow().clone())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::NotInitialized,
                json!({ "reason": "benchmarkRelaxedVfsNotInstalled" }),
            )
        })
}

fn sahpool_util() -> Result<Rc<OpfsSAHPoolUtil>> {
    SAHPOOL_UTIL
        .with(|slot| slot.borrow().clone())
        .ok_or_else(|| {
            RuntimeError::with(
                ErrorCode::NotInitialized,
                json!({ "reason": "benchmarkSahpoolVfsNotInstalled" }),
            )
        })
}

async fn delete_database(backend: BenchmarkBackend, database_name: &str) -> Result<bool> {
    match backend {
        BenchmarkBackend::RelaxedIdb => {
            let util = relaxed_util()?;
            util.preload_db(vec![database_name.to_owned()])
                .await
                .map_err(|e| benchmark_error("preloadForDelete", e))?;
            if !util.exists(database_name) {
                return Ok(false);
            }
            util.delete_db(database_name)
                .map_err(|e| benchmark_error("deleteRelaxedIdb", e))?
                .await
                .map_err(|e| benchmark_error("commitDeleteRelaxedIdb", e))?;
            Ok(true)
        }
        BenchmarkBackend::OpfsSahpool => sahpool_util()?
            .delete_db(database_name)
            .map_err(|e| benchmark_error("deleteOpfsSahpool", e)),
    }
}

fn open_connection(request: &BenchmarkRequest) -> Result<Connection> {
    let mut flags = OpenFlags::SQLITE_OPEN_READ_WRITE | OpenFlags::SQLITE_OPEN_URI;
    if request.action == BenchmarkAction::Replace {
        flags |= OpenFlags::SQLITE_OPEN_CREATE;
    }
    Connection::open_with_flags_and_vfs(&request.database_name, flags, request.backend.vfs_name())
        .map_err(|e| benchmark_error("open", e))
}

fn configure_connection(conn: &Connection, backend: BenchmarkBackend) -> Result<()> {
    conn.execute_batch("PRAGMA foreign_keys = ON; PRAGMA journal_mode = DELETE;")?;
    match backend {
        // relaxed-idb rejects every synchronous level except OFF. Keeping this
        // explicit makes the durability difference visible in the result.
        BenchmarkBackend::RelaxedIdb => conn.execute_batch("PRAGMA synchronous = OFF;")?,
        BenchmarkBackend::OpfsSahpool => conn.execute_batch("PRAGMA synchronous = FULL;")?,
    }
    Ok(())
}

fn create_schema(conn: &Connection) -> Result<()> {
    conn.execute_batch(
        "CREATE TABLE benchmark_rows (
           writer_id INTEGER NOT NULL,
           row_index INTEGER NOT NULL,
           generation INTEGER NOT NULL,
           payload BLOB NOT NULL,
           checksum INTEGER NOT NULL,
           PRIMARY KEY (writer_id, row_index)
         ) WITHOUT ROWID;",
    )?;
    Ok(())
}

fn insert_rows(
    conn: &mut Connection,
    writer_id: u32,
    row_count: u32,
    payload_bytes: u32,
    batch_size: u32,
) -> Result<Vec<f64>> {
    let mut latencies = Vec::new();
    let mut payload = vec![0u8; payload_bytes as usize];
    let mut start = 0;
    while start < row_count {
        let batch_started = now_ms();
        let end = (start + batch_size).min(row_count);
        let tx = conn.transaction()?;
        {
            let mut statement = tx.prepare(
                "INSERT OR REPLACE INTO benchmark_rows
                 (writer_id, row_index, generation, payload, checksum)
                 VALUES (?1, ?2, 0, ?3, ?4)",
            )?;
            for row_index in start..end {
                fill_payload(&mut payload, writer_id, row_index);
                let checksum = payload_checksum(&payload);
                statement.execute(params![writer_id, row_index, &payload, checksum])?;
            }
        }
        tx.commit()?;
        latencies.push(elapsed(batch_started));
        start = end;
    }
    Ok(latencies)
}

fn inspect_data(conn: &Connection) -> Result<(Value, Value)> {
    let (rows, payload_bytes, checksum_sum, generation_sum, writers): (i64, i64, i64, i64, i64) =
        conn.query_row(
            "SELECT COUNT(*),
                COALESCE(SUM(length(payload)), 0),
                COALESCE(SUM(checksum), 0),
                COALESCE(SUM(generation), 0),
                COUNT(DISTINCT writer_id)
         FROM benchmark_rows",
            [],
            |row| {
                Ok((
                    row.get(0)?,
                    row.get(1)?,
                    row.get(2)?,
                    row.get(3)?,
                    row.get(4)?,
                ))
            },
        )?;

    let sample_count = rows.clamp(0, 128);
    let mut mismatches = 0i64;
    let mut read_latencies = Vec::with_capacity(sample_count as usize);
    for sample in 0..sample_count {
        let offset = if sample_count <= 1 {
            0
        } else {
            sample * (rows - 1) / (sample_count - 1)
        };
        let read_started = now_ms();
        let (writer_id, row_index, payload, stored_checksum): (u32, u32, Vec<u8>, u32) = conn
            .query_row(
                "SELECT writer_id, row_index, payload, checksum
                 FROM benchmark_rows
                 ORDER BY writer_id, row_index
                 LIMIT 1 OFFSET ?1",
                [offset],
                |row| Ok((row.get(0)?, row.get(1)?, row.get(2)?, row.get(3)?)),
            )?;
        read_latencies.push(elapsed(read_started));
        let mut expected = vec![0u8; payload.len()];
        fill_payload(&mut expected, writer_id, row_index);
        if payload != expected || payload_checksum(&payload) != stored_checksum {
            mismatches += 1;
        }
    }

    Ok((
        json!({
            "rowCount": rows,
            "payloadBytes": payload_bytes,
            "checksumSum": checksum_sum,
            "generationSum": generation_sum,
            "writerCount": writers,
        }),
        json!({
            "checkedRows": sample_count,
            "mismatches": mismatches,
            "readLatencyMs": latency_summary(&read_latencies),
        }),
    ))
}

fn integrity(conn: &Connection) -> Result<Value> {
    let mut statement = conn.prepare("PRAGMA quick_check(10)")?;
    let messages = statement
        .query_map([], |row| row.get::<_, String>(0))?
        .collect::<std::result::Result<Vec<_>, _>>()?;
    let quick_check_ok = messages.len() == 1 && messages.first().is_some_and(|v| v == "ok");
    let foreign_key_violations: i64 =
        conn.query_row("SELECT COUNT(*) FROM pragma_foreign_key_check", [], |row| {
            row.get(0)
        })?;
    Ok(json!({
        "quickCheckOk": quick_check_ok,
        "messages": messages,
        "foreignKeyViolations": foreign_key_violations,
    }))
}

fn sqlite_stats(conn: &Connection) -> Result<Value> {
    let page_size: i64 = conn.query_row("PRAGMA page_size", [], |row| row.get(0))?;
    let page_count: i64 = conn.query_row("PRAGMA page_count", [], |row| row.get(0))?;
    let free_pages: i64 = conn.query_row("PRAGMA freelist_count", [], |row| row.get(0))?;
    let journal_mode: String = conn.query_row("PRAGMA journal_mode", [], |row| row.get(0))?;
    let synchronous: i64 = conn.query_row("PRAGMA synchronous", [], |row| row.get(0))?;
    Ok(json!({
        "pageSize": page_size,
        "pageCount": page_count,
        "freePages": free_pages,
        "logicalBytes": page_size * page_count,
        "journalMode": journal_mode,
        "synchronous": synchronous,
    }))
}

fn fill_payload(payload: &mut [u8], writer_id: u32, row_index: u32) {
    for (index, byte) in payload.iter_mut().enumerate() {
        *byte = ((index as u32 * 31 + row_index * 17 + writer_id * 13) % 251) as u8;
    }
}

fn payload_checksum(payload: &[u8]) -> u32 {
    payload.iter().fold(2_166_136_261u32, |hash, byte| {
        (hash ^ u32::from(*byte)).wrapping_mul(16_777_619)
    })
}

fn latency_summary(values: &[f64]) -> Value {
    if values.is_empty() {
        return json!({ "count": 0, "p50": 0.0, "p95": 0.0, "max": 0.0 });
    }
    let mut sorted = values.to_vec();
    sorted.sort_by(f64::total_cmp);
    let percentile = |p: f64| {
        let index = ((sorted.len() - 1) as f64 * p).round() as usize;
        sorted[index]
    };
    json!({
        "count": sorted.len(),
        "p50": percentile(0.50),
        "p95": percentile(0.95),
        "max": sorted[sorted.len() - 1],
    })
}

fn benchmark_error(operation: &'static str, error: impl std::fmt::Debug) -> RuntimeError {
    RuntimeError::with(ErrorCode::DatabaseError, json!({ "operation": operation }))
        .detail(format!("{error:?}"))
}

fn now_ms() -> f64 {
    js_sys::Date::now()
}

fn elapsed(started: f64) -> f64 {
    (now_ms() - started).max(0.0)
}

fn rate(units: u64, duration_ms: f64) -> f64 {
    if duration_ms <= 0.0 {
        return 0.0;
    }
    units as f64 * 1_000.0 / duration_ms
}

fn wasm_memory_bytes() -> u64 {
    core::arch::wasm32::memory_size(0) as u64 * 65_536
}
