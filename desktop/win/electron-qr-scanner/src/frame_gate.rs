use std::time::{Duration, Instant};

/// Mirrors `QrFrameTextGate.swift` / `QrFrameTextGate.kt`:
/// - Distinct QR texts (e.g. animated BC-UR parts) pass immediately.
/// - Identical consecutive text repeats at most once per `repeat_interval` (1 s).
/// - Capture generation changes reset the gate so a restarted session delivers
///   the first code immediately.
pub struct FrameTextGate {
    repeat_interval: Duration,
    generation: i64,
    last_text: Option<String>,
    last_emitted_at: Option<Instant>,
}

impl FrameTextGate {
    pub const DEFAULT_REPEAT_INTERVAL: Duration = Duration::from_secs(1);

    pub fn new(repeat_interval: Duration) -> Self {
        Self {
            repeat_interval,
            generation: 0,
            last_text: None,
            last_emitted_at: None,
        }
    }

    pub fn default_gate() -> Self {
        Self::new(Self::DEFAULT_REPEAT_INTERVAL)
    }

    /// Returns true if `text` should be emitted to JS for `generation` at `now`.
    pub fn accept(&mut self, generation: i64, text: &str, now: Instant) -> bool {
        if text.is_empty() {
            return false;
        }
        if generation != self.generation {
            self.generation = generation;
            self.last_text = None;
            self.last_emitted_at = None;
        }
        if let (Some(prev), Some(prev_at)) = (self.last_text.as_deref(), self.last_emitted_at) {
            if prev == text && now.saturating_duration_since(prev_at) < self.repeat_interval {
                return false;
            }
        }
        self.last_text = Some(text.to_owned());
        self.last_emitted_at = Some(now);
        true
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn distinct_ur_parts_pass_immediately() {
        let mut gate = FrameTextGate::default_gate();
        let t0 = Instant::now();
        for i in 1..=12 {
            let text = format!("ur:crypto-psbt/{i}-12/payload");
            let now = t0 + Duration::from_millis(i as u64 * 33);
            assert!(gate.accept(1, &text, now), "part {i} should pass");
        }
    }

    #[test]
    fn duplicate_text_is_throttled_for_one_second() {
        let mut gate = FrameTextGate::default_gate();
        let t0 = Instant::now();
        assert!(gate.accept(1, "bitcoin:bc1qexample", t0));
        assert!(!gate.accept(1, "bitcoin:bc1qexample", t0 + Duration::from_millis(500)));
        assert!(!gate.accept(1, "bitcoin:bc1qexample", t0 + Duration::from_millis(999)));
        assert!(gate.accept(1, "bitcoin:bc1qexample", t0 + Duration::from_millis(1000)));
    }

    #[test]
    fn new_generation_resets_throttle() {
        let mut gate = FrameTextGate::default_gate();
        let t0 = Instant::now();
        assert!(gate.accept(1, "same-qr", t0));
        assert!(!gate.accept(1, "same-qr", t0 + Duration::from_millis(100)));
        assert!(gate.accept(2, "same-qr", t0 + Duration::from_millis(100)));
    }
}
