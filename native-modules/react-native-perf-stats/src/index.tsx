import { NitroModules } from 'react-native-nitro-modules';
import type {
  MemoryWarningEvent,
  ReactNativePerfStats as ReactNativePerfStatsType,
} from './ReactNativePerfStats.nitro';

const nativeImpl =
  NitroModules.createHybridObject<ReactNativePerfStatsType>('ReactNativePerfStats');

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
let jsFpsCurrentInterval: number | null = null;

/**
 * Start the JS-side FPS ticker. Normally invoked automatically by
 * `ReactNativePerfStats.start` and stopped by `.stop`; exported as
 * an escape hatch for advanced flows (e.g. measuring JS FPS without
 * the native sampler running).
 *
 * Calling with a new `reportIntervalMs` while already running
 * restarts the loop with the new interval; calling with the same
 * interval is a no-op.
 */
export function startJsFpsTracker(reportIntervalMs: number = 1000): void {
  if (jsFpsCurrentInterval === reportIntervalMs) return;
  stopJsFpsTracker();
  jsFpsCurrentInterval = reportIntervalMs;

  const tick = () => {
    jsFpsFrameCount += 1;
    jsFpsRafId = requestAnimationFrame(tick);
  };
  jsFpsRafId = requestAnimationFrame(tick);

  jsFpsIntervalId = setInterval(() => {
    const fps = (jsFpsFrameCount * 1000) / reportIntervalMs;
    jsFpsFrameCount = 0;
    nativeImpl.setJsFpsHint(fps);
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
  jsFpsCurrentInterval = null;
}

// `start` / `stop` wrap the native sampler so the JS-side rAF tracker
// shares its lifetime: callers never have to remember to enable it
// separately, and `PerfSample.jsFps` is populated as soon as the
// sampler is running. Other methods pass straight through to the
// HybridObject.

export const ReactNativePerfStats = {
  start(intervalMs: number): void {
    nativeImpl.start(intervalMs);
    startJsFpsTracker(intervalMs);
  },
  stop(): void {
    stopJsFpsTracker();
    nativeImpl.stop();
  },
  showOverlay: (): void => nativeImpl.showOverlay(),
  hideOverlay: (): void => nativeImpl.hideOverlay(),
  sample: () => nativeImpl.sample(),
  setJsFpsHint: (fps: number): void => nativeImpl.setJsFpsHint(fps),
  // Memory pressure is independent of the sampler — these stay active
  // even after `stop()`. iOS only emits `level: 'critical'`.
  addMemoryWarningListener: (
    callback: (event: MemoryWarningEvent) => void,
  ): number => nativeImpl.addMemoryWarningListener(callback),
  removeMemoryWarningListener: (id: number): void =>
    nativeImpl.removeMemoryWarningListener(id),
};
