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
   * or CADisplayLink (iOS). `0` until at least one window has elapsed.
   */
  uiFps: number;
  /**
   * JS thread frame rate, frames per second reported by the JS-side
   * `requestAnimationFrame` ticker (see `startJsFpsTracker`). `0` if
   * the tracker has not been started or no hint has been received yet.
   */
  jsFps: number;
  /** Wall-clock timestamp (ms since unix epoch) when the sample was taken. */
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
}
