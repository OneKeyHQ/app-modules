import { TurboModuleRegistry } from 'react-native';

import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
  startBackgroundRunnerWithEntryURL(entryURL: string): void;
  installSharedBridge(): void;
  loadSegmentInBackground(
    segmentId: number,
    path: string,
  ): Promise<void>;
  /**
   * Reload one or both JS runtimes. Replaces `react-native-restart`.
   *
   * `mode`:
   * - `'ui'`: restart only the main JS runtime (the one driving the UI).
   *   The background runtime stays hot, preserving its state and avoiding
   *   the SharedRPC reload race that crashed iOS on language switch.
   * - `'all'`: restart both main and background JS runtimes. Required when
   *   the JS bundle on disk has changed (OTA install/switch) so the two
   *   runtimes cannot keep running mismatched moduleId tables.
   *
   * Any other value rejects the promise — callers must use the
   * `EAppRestartMode` enum from `@onekeyhq/shared`.
   *
   * `reason` is forwarded to RCTTriggerReloadCommandListeners and to host
   * logs / Sentry breadcrumbs so production restarts are attributable.
   *
   * ## Platform behaviour for `mode='all'`
   *
   * iOS and Android tear down the JS runtimes the same way, but the OS
   * process around them differs — code that runs natively (timers,
   * singletons, in-memory caches, scheduled jobs, foreground services)
   * cannot assume cross-platform survival semantics:
   *
   * - **iOS**: process survives. The main RCTHost is rebuilt in-place via
   *   `RCTTriggerReloadCommandListeners`; the bg RCTHost is released and
   *   re-spawned by the post-reload observer (or the host AppDelegate's
   *   `hostDidStart:`). Any process-level state (NSUserDefaults cache,
   *   live URLSession tasks, GCD timers attached to the app process)
   *   survives. iOS goes through soft reload here because abrupt
   *   termination is App Store-unfriendly and iOS lacks a clean self-
   *   relaunch path.
   * - **Android**: process is killed. `BackgroundThreadManager` invokes
   *   `Runtime.exit(0)` after queuing `makeRestartActivityTask`, so the
   *   JVM is replaced wholesale. Any process-level state is lost; Activity
   *   stack is rebuilt from the launch intent. The choice is intentional
   *   — `mode='all'` is meant for OTA install/switch where a fresh re-
   *   read of both bundles from disk is desirable, and Android has a
   *   clean self-relaunch path that iOS doesn't.
   *
   * Callers that depend on process-level state (anything held in native
   * singletons, background services, JNI handles) must not rely on it
   * surviving `mode='all'` cross-platform. For UI-only resets that should
   * preserve process state on both platforms, use `mode='ui'`.
   */
  restart(mode: string, reason: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
