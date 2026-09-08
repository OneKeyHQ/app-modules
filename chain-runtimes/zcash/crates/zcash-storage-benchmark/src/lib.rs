//! A small diagnostic WASM package for comparing browser SQLite VFS backends.
//!
//! It is separate from the wallet runtime so a benchmark Worker does not
//! duplicate Zcash proving and scanning code in production bundles.

#[cfg(target_arch = "wasm32")]
mod error;
#[cfg(target_arch = "wasm32")]
mod storage_benchmark;

#[cfg(target_arch = "wasm32")]
use wasm_bindgen::prelude::*;

#[cfg(target_arch = "wasm32")]
#[wasm_bindgen(js_name = runStorageBenchmark)]
pub async fn run_storage_benchmark(request_json: &str) -> Result<String, JsValue> {
    console_error_panic_hook::set_once();
    Ok(storage_benchmark::run(request_json).await?)
}
