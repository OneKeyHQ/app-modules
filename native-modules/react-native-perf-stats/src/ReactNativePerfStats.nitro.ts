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
   * - Android: TextView attached to the current Activity via
   *   `addContentView` — no floating-window permission required.
   * - iOS: UILabel added to the key UIWindow.
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
}
