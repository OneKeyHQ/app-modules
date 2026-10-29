//! Clock for `zcash_client_sqlite`: upstream's `SystemClock` calls
//! `SystemTime::now()`, which panics on wasm32-unknown-unknown, so the browser
//! uses its own.

use std::time::{Duration, SystemTime};
use zcash_client_sqlite::util::Clock;

/// Clock backed by the host's JS `Date.now()`.
#[derive(Clone, Copy, Default)]
pub struct HostClock;

impl Clock for HostClock {
    fn now(&self) -> SystemTime {
        SystemTime::UNIX_EPOCH + Duration::from_millis(now_millis())
    }
}

#[cfg(target_arch = "wasm32")]
fn now_millis() -> u64 {
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
            .expect("clock must not be before the epoch");
        // 2020-01-01 in ms: a real time, not 0.
        assert!(t.as_millis() > 1_577_836_800_000);
    }
}
