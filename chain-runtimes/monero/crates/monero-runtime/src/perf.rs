//! 计时打点。
//!
//! 扫得慢的时候，「慢在拉块还是慢在解密」是完全不同的两件事，而在浏览器里
//! 两者的表现一模一样（界面不动）。实测公共节点单次 RPC 往返可达 490ms，
//! 不把两段分开量，就会把网络延迟误判成算力不足 —— 那会让整条技术路线的
//! 结论反过来。

#[cfg(target_arch = "wasm32")]
pub fn now_ms() -> f64 {
    js_sys::Date::now()
}

#[cfg(not(target_arch = "wasm32"))]
pub fn now_ms() -> f64 {
    use std::time::{SystemTime, UNIX_EPOCH};
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_secs_f64() * 1000.0)
        .unwrap_or(0.0)
}
