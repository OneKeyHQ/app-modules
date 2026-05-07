import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativePerfStats as ReactNativePerfStatsType } from './ReactNativePerfStats.nitro';

const ReactNativePerfStatsHybridObject =
  NitroModules.createHybridObject<ReactNativePerfStatsType>('ReactNativePerfStats');

export const ReactNativePerfStats = ReactNativePerfStatsHybridObject;
export type * from './ReactNativePerfStats.nitro';

// ---- JS FPS tracker ----------------------------------------------------
//
// Counts frames the JS event loop services per second using
// `requestAnimationFrame` and pushes the value to native via
// `setJsFpsHint`. Native then surfaces it as `PerfSample.jsFps`.
//
// Why rAF and not a setTimeout-based ticker: rAF is scheduled by RN's
// UIManager from the platform vsync (Choreographer / CADisplayLink)
// and dispatched as a JS task. When the JS thread is blocked, rAF
// callbacks are coalesced and the per-second count drops, exactly
// reflecting "how many frame opportunities did JS service" — which is
// what users mean by "JS FPS". A setTimeout(16) loop measures timer
// drift, which is correlated but coarser.
//
// Cost: one int increment + one rAF call per frame (~60–120/s) plus one
// `setJsFpsHint` call per second. Negligible vs. typical app workload.

let jsFpsRafId: number | null = null;
let jsFpsIntervalId: ReturnType<typeof setInterval> | null = null;
let jsFpsFrameCount = 0;

/**
 * Start the JS-side FPS ticker. Idempotent. Reports once per
 * `reportIntervalMs` (default 1000 ms). Stops automatically on
 * `stopJsFpsTracker()`.
 */
export function startJsFpsTracker(reportIntervalMs: number = 1000): void {
  if (jsFpsRafId != null || jsFpsIntervalId != null) return;

  const tick = () => {
    jsFpsFrameCount += 1;
    jsFpsRafId = requestAnimationFrame(tick);
  };
  jsFpsRafId = requestAnimationFrame(tick);

  jsFpsIntervalId = setInterval(() => {
    const fps = (jsFpsFrameCount * 1000) / reportIntervalMs;
    jsFpsFrameCount = 0;
    ReactNativePerfStats.setJsFpsHint(fps);
  }, reportIntervalMs);
}

/** Stop the JS-side FPS ticker. Idempotent. */
export function stopJsFpsTracker(): void {
  if (jsFpsRafId != null) {
    cancelAnimationFrame(jsFpsRafId);
    jsFpsRafId = null;
  }
  if (jsFpsIntervalId != null) {
    clearInterval(jsFpsIntervalId);
    jsFpsIntervalId = null;
  }
  jsFpsFrameCount = 0;
}
