//! 临时性能打点：宿主打开开关后，每个重导出的耗时打到 console。
//! 与宿主侧 `[PRIV-PERF]` 同前缀，一个过滤器看全链路。

use std::sync::atomic::{AtomicBool, Ordering};

static ENABLED: AtomicBool = AtomicBool::new(false);

pub fn set_enabled(enabled: bool) {
    ENABLED.store(enabled, Ordering::Relaxed);
}

pub fn is_enabled() -> bool {
    ENABLED.load(Ordering::Relaxed)
}

/// 作用域结束（含跨 await 的 async 函数返回）时打一条耗时。
pub struct Span {
    name: &'static str,
    started_ms: f64,
}

pub fn span(name: &'static str) -> Option<Span> {
    if !is_enabled() {
        return None;
    }
    Some(Span {
        name,
        started_ms: now_ms(),
    })
}

impl Drop for Span {
    fn drop(&mut self) {
        let ms = now_ms() - self.started_ms;
        log(&format!("[PRIV-PERF] rt {} {{\"ms\":{}}}", self.name, ms.round()));
    }
}

#[cfg(target_arch = "wasm32")]
fn now_ms() -> f64 {
    js_sys::Date::now()
}

#[cfg(not(target_arch = "wasm32"))]
fn now_ms() -> f64 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map(|d| d.as_secs_f64() * 1000.0)
        .unwrap_or(0.0)
}

#[cfg(target_arch = "wasm32")]
fn log(msg: &str) {
    #[wasm_bindgen::prelude::wasm_bindgen]
    extern "C" {
        #[wasm_bindgen(js_namespace = console, js_name = log)]
        fn console_log(s: &str);
    }
    console_log(msg);
}

#[cfg(not(target_arch = "wasm32"))]
fn log(msg: &str) {
    println!("{msg}");
}
