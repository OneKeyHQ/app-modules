import type { HybridObject } from 'react-native-nitro-modules';

export interface PerfSample {
  /**
   * Process CPU usage as a percentage of one core, computed as
   * `(deltaCpuTime / deltaWallTime) * 100` against the previous sample.
   * Multi-core saturation can exceed 100. The first sample after process
   * launch returns `0` because no baseline exists yet.
   */
  cpu: number;
  /** Resident set size in bytes. iOS: phys_footprint; Android: VmRSS. */
  rss: number;
  /**
   * UI thread frame rate, frames per second over the last 1 s window.
   * Measured on the platform's main thread via Choreographer (Android)
   * or CADisplayLink (iOS). `0` until at least one window has elapsed
   * **and the sampler is running** — the underlying frame monitor only
   * runs between `start()` and `stop()`, so one-shot `sample()` calls
   * issued outside that window always report `0` here.
   */
  uiFps: number;
  /**
   * JS thread frame rate, frames per second reported by the JS-side
   * `requestAnimationFrame` ticker (see `startJsFpsTracker`). `0` if
   * the tracker has not been started or no hint has been received yet.
   * Same caveat as `uiFps`: one-shot `sample()` calls issued before
   * `start()` (or after `stop()`) report `0` here.
   */
  jsFps: number;
  /** Wall-clock timestamp (ms since unix epoch) when the sample was taken. */
  timestamp: number;
}

/**
 * Severity of a system-emitted memory pressure event, normalised across
 * platforms.
 *
 * - `low`: Android `TRIM_MEMORY_RUNNING_MODERATE` /
 *   `TRIM_MEMORY_RUNNING_LOW`. iOS does not emit this level (iOS only
 *   fires the critical warning), so JS code should treat the absence of
 *   `low` events on iOS as expected, not a missing signal.
 * - `critical`: iOS `UIApplicationDidReceiveMemoryWarningNotification`;
 *   Android `TRIM_MEMORY_RUNNING_CRITICAL` or `onLowMemory()`. Indicates
 *   the OS is about to start killing background apps (Android) or the
 *   app itself (iOS jetsam). Subscribers should drop everything that
 *   can be rebuilt on demand.
 */
export type MemoryWarningLevel = 'low' | 'critical';

export interface MemoryWarningEvent {
  level: MemoryWarningLevel;
  /**
   * Process RSS in bytes at the moment the warning fired. `0` if the
   * lookup failed.
   *
   * On iOS the primary source is `phys_footprint`; if that fails, the
   * fallback path reads `resident_size`, which is semantically smaller
   * and noisier. The two values are not directly comparable, so treat
   * unexpected magnitude differences across reports cautiously.
   */
  rss: number;
  /** Wall-clock timestamp (ms since unix epoch). */
  timestamp: number;
}

export interface ReactNativePerfStats
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  /**
   * Start the singleton native sampler.
   *
   * - Runs on a dedicated background thread (Android: HandlerThread;
   *   iOS: dispatch source on a global queue), so it survives JS-thread
   *   blockages — the overlay keeps updating even when JS is frozen.
   * - Idempotent: calling `start` again only updates the interval; it does
   *   not spawn additional timers.
   */
  start(intervalMs: number): void;

  /** Stop the sampler. Also hides the overlay if shown. No-op if not running. */
  stop(): void;

  /**
   * Show the floating overlay (CPU + RAM) drawn natively.
   *
   * - Android: TextView attached via `WindowManager.addView` using the
   *   current Activity's window token (window type
   *   `TYPE_APPLICATION_ABOVE_SUB_PANEL`) — no floating-window permission
   *   required, and the overlay tracks Activity lifecycle (re-attach on
   *   resume, detach on pause/destroy).
   * - iOS: UILabel hosted on a dedicated passthrough UIWindow above the
   *   app's key window so it stays above modally-presented controllers.
   * - Draggable on both platforms.
   * - Idempotent. If the sampler is not running, the overlay shows "--"
   *   until `start` is called.
   */
  showOverlay(): void;

  /** Hide the floating overlay. The sampler keeps running. No-op if not shown. */
  hideOverlay(): void;

  /**
   * Take one CPU+RAM sample without affecting the overlay timer.
   * Cheap (~1ms) and runs off the JS thread via Promise.async.
   * Shares the same delta baseline as the overlay sampler.
   */
  sample(): Promise<PerfSample>;

  /**
   * Push the latest JS-thread frame count from the JS-side
   * `requestAnimationFrame` ticker. Native stores the value and
   * surfaces it as `PerfSample.jsFps` on the next sample. Cheap;
   * intended to be called once per second.
   *
   * Prefer the `startJsFpsTracker` helper exported from JS rather
   * than calling this directly.
   */
  setJsFpsHint(fps: number): void;

  /**
   * Subscribe to OS-emitted memory pressure events.
   *
   * - iOS: `UIApplicationDidReceiveMemoryWarningNotification`, mapped to
   *   `critical`. Fires on the main thread.
   * - Android: `ComponentCallbacks2.onTrimMemory` filtered to
   *   `TRIM_MEMORY_RUNNING_*` (foreground process pressure), plus
   *   `onLowMemory()` as `critical`. Fires on the main thread.
   *
   * The native listener is registered process-wide on first call and
   * survives `stop()` — memory pressure is independent of the perf
   * sampler being active. Callbacks are invoked on the JS thread.
   *
   * **Always pair this with `removeMemoryWarningListener`** (e.g. in a
   * React effect cleanup): the callback closure is retained for the life
   * of the subscription, and forgetting to remove leaks every object the
   * closure transitively captures — typically the React component instance
   * that registered it. This matters in dev too: the native listener
   * table is a process-wide singleton that **does not clear on RN
   * reload**, so a Fast-Refresh / dev reload that drops the JS realm
   * without first calling `removeMemoryWarningListener` will leave the
   * pre-reload callback in the table, pointing at a dead JS context.
   * The cleanest fix is the same effect-cleanup pattern.
   *
   * The relative order in which subscribed callbacks fire for a single
   * event is **not guaranteed** — iOS iterates a hash dictionary, Android
   * iterates a LinkedHashMap, and either may change. Don't take a
   * cross-listener dependency.
   *
   * Dispatch: when a warning arrives, the native module walks all
   * registered callbacks **serially on the main thread** before each
   * one hops to the JS thread. Cost scales with the number of
   * subscribers — keep N small (single-digit) and avoid registering one
   * listener per render-tree branch.
   *
   * Side effects of the warning itself (before your callback fires):
   * - **iOS** runs the same reclaim path as `cleanupNativeCaches`,
   *   which drops the process-wide `URLCache.shared`, the WK HTTP /
   *   AppCache / Service-Worker stores, and triggers a libmalloc
   *   pressure relief. If any code path in the app depends on
   *   URLCache for offline HTTP content, or on a Service Worker for
   *   offline WebView fallback, that state is dropped here — don't
   *   register this listener if either is load-bearing.
   * - **Android** runs `Runtime.gc()` on every warning (LOW and
   *   CRITICAL). ART treats it as a hint.
   *
   * Returns an opaque id; pass it to `removeMemoryWarningListener` to
   * unsubscribe.
   */
  addMemoryWarningListener(callback: (event: MemoryWarningEvent) => void): number;

  /** Unsubscribe a previously-registered memory warning listener. No-op for unknown ids. */
  removeMemoryWarningListener(id: number): void;

  /**
   * Trigger the same native reclaim path the OS memory-warning observer
   * runs, on demand. Use when the JS layer has reason to believe pressure
   * is building before the OS posts a warning (e.g. backgrounding, a
   * heavy route transition, jotai cache invalidation, post-import).
   *
   * - **iOS** clears `URLCache.shared` (process-wide HTTP response cache;
   *   any consumer that relies on URLCache for offline content loses
   *   it until the next request) and the WK HTTP / AppCache / Service-
   *   Worker stores (auth-related cookies / localStorage / IndexedDB
   *   are preserved). Then dispatches `malloc_zone_pressure_relief` on
   *   a background queue — the call returns to JS immediately, but the
   *   zone walk that actually frees pages can take 100-500 ms.
   * - **Android** asks ART to GC via `Runtime.gc()` (hint only; the
   *   runtime decides whether to honour it). Returns quickly.
   *
   * Cheap on Android, cheap-to-return on iOS (heavy work runs async).
   * Pair with `forceGarbageCollection()` if you also want the JS engine
   * to participate.
   */
  cleanupNativeCaches(): void;
}
