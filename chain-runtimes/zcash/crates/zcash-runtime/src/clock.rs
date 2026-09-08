//! 时钟。
//!
//! `zcash_client_sqlite` 需要一个 `Clock` 来给数据库记录时间戳，官方提供的
//! `SystemClock` 走 `std::time::SystemTime::now()`。在 wasm32-unknown-unknown 上
//! 那个调用会 panic（std 没有该平台的时间源），所以浏览器侧必须自己接。
//!
//! 这是本 crate 存在的典型理由之一：一小块平台胶水，不是业务逻辑。

use std::time::{Duration, SystemTime};
use zcash_client_sqlite::util::Clock;

/// 由宿主 JS 的 `Date.now()` 驱动的时钟。
#[derive(Clone, Copy, Default)]
pub struct HostClock;

impl Clock for HostClock {
    fn now(&self) -> SystemTime {
        SystemTime::UNIX_EPOCH + Duration::from_millis(now_millis())
    }
}

#[cfg(target_arch = "wasm32")]
fn now_millis() -> u64 {
    // js_sys::Date::now() 返回自 epoch 的毫秒数（f64）。
    let ms = js_sys::Date::now();
    if ms.is_finite() && ms > 0.0 {
        ms as u64
    } else {
        0
    }
}

#[cfg(not(target_arch = "wasm32"))]
fn now_millis() -> u64 {
    SystemTime::now()
        .duration_since(SystemTime::UNIX_EPOCH)
        .map(|d| d.as_millis() as u64)
        .unwrap_or(0)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn clock_is_after_2020() {
        let t = HostClock
            .now()
            .duration_since(SystemTime::UNIX_EPOCH)
            .expect("时钟不应早于 epoch");
        // 2020-01-01 的毫秒数，确保我们拿到的是真实时间而不是 0。
        assert!(t.as_millis() > 1_577_836_800_000);
    }
}
