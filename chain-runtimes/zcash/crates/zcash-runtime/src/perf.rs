//! Perf tracing: when enabled by the host, logs each export's duration
//! to the console. Shares the host's `[PRIV-PERF]` prefix so one filter covers both.

use std::sync::atomic::{AtomicBool, Ordering};

static ENABLED: AtomicBool = AtomicBool::new(false);

pub fn set_enabled(enabled: bool) {
    ENABLED.store(enabled, Ordering::Relaxed);
}

pub fn is_enabled() -> bool {
    ENABLED.load(Ordering::Relaxed)
}

/// Logs the elapsed time when dropped, including across awaits in async fns.
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
        log(&format!(
            "[PRIV-PERF] rt {} {{\"ms\":{}}}",
            self.name,
            ms.round()
        ));
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
