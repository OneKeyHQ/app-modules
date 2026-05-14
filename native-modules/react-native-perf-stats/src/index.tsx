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

// Module-level latch so forceGarbageCollection's "no GC binding" branch
// warns exactly once per JS realm. Avoids spamming the console when a
// memory-warning handler calls forceGarbageCollection on every event.
let forceGcMissingWarned = false;

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
  // Clamp before use: `setInterval(_, 0)` fires every event-loop tick and
  // the per-second divide below would race away to Infinity; NaN/negative
  // values are equally meaningless here. 100 ms is below one rAF frame
  // on a 60 Hz display, so it's already finer than the data warrants;
  // 60 s is an arbitrary upper sanity bound.
  const safeInterval = Number.isFinite(reportIntervalMs)
    ? Math.max(100, Math.min(60_000, Math.trunc(reportIntervalMs)))
    : 1000;
  if (jsFpsCurrentInterval === safeInterval) return;
  stopJsFpsTracker();
  jsFpsCurrentInterval = safeInterval;

  const tick = () => {
    jsFpsFrameCount += 1;
    jsFpsRafId = requestAnimationFrame(tick);
  };
  jsFpsRafId = requestAnimationFrame(tick);

  jsFpsIntervalId = setInterval(() => {
    const fps = (jsFpsFrameCount * 1000) / safeInterval;
    jsFpsFrameCount = 0;
    nativeImpl.setJsFpsHint(fps);
  }, safeInterval);
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
  /**
   * Run the same native reclaim path the OS memory-warning observer
   * triggers, on demand. Returns immediately (heavy work runs async
   * on iOS). See the spec doc for what gets dropped on each platform.
   */
  cleanupNativeCaches: (): void => nativeImpl.cleanupNativeCaches(),

  /**
   * Best-effort hint to the JS engine that now is a good time to GC.
   *
   * Hermes does not expose a public `collectGarbage` binding in production
   * builds; the only stable JS-level entry point is the (undocumented)
   * `HermesInternal.gc` property, which is present in some builds and
   * absent in others. We feature-detect it and fall back to a no-op so
   * callers never have to branch.
   *
   * Returns `true` only if a GC binding was both found AND invoked
   * without throwing. A `false` return therefore covers three cases —
   * binding missing (production Hermes is the common case), binding
   * present but threw, and any unexpected failure. The first miss is
   * logged once via `console.warn` so the caller knows it landed in the
   * "no binding" branch; throws are logged on every occurrence because
   * those are real errors.
   *
   * Cost: when honoured, Hermes does a stop-the-world collection that
   * can take 100–500 ms — never call this on the hot path. Memory-warning
   * handlers are the intended use case.
   */
  forceGarbageCollection(): boolean {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const g = globalThis as any;
    const hi = g?.HermesInternal;
    if (hi && typeof hi.gc === 'function') {
      try {
        hi.gc();
        return true;
      } catch (e) {
        console.warn('[PerfStats] HermesInternal.gc threw:', e);
        return false;
      }
    }
    if (typeof g?.gc === 'function') {
      // V8-style binding (only present with --expose-gc; never in
      // production Hermes, but harmless to try).
      try {
        g.gc();
        return true;
      } catch (e) {
        console.warn('[PerfStats] globalThis.gc threw:', e);
        return false;
      }
    }
    if (!forceGcMissingWarned) {
      forceGcMissingWarned = true;
      console.warn(
        '[PerfStats] forceGarbageCollection: no GC binding found ' +
          '(HermesInternal.gc / globalThis.gc absent). Production Hermes ' +
          'strips these; this warning fires once per JS realm.',
      );
    }
    return false;
  },
};
