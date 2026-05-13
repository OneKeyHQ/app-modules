import { TurboModuleRegistry } from 'react-native';

import type { TurboModule } from 'react-native';

/**
 * Allowed values for `restart()`'s `mode` argument.
 *
 * Defined as a string literal union (not a TypeScript `enum`) on purpose:
 *
 * 1. **Zero runtime cost.** A regular `enum` compiles to a JS object that
 *    ships with the bundle. A literal union erases to nothing.
 * 2. **Codegen-friendly.** React Native's TurboModule codegen reads this
 *    spec to generate native types; its supported set is primitives,
 *    Object/Array, Promise, and string-literal unions. Plain TS `enum`
 *    is not in that set — behaviour varies by RN version and best
 *    avoided in spec files.
 * 3. **Callsite type-safety without a runtime import.** Consumers can
 *    `import type { RestartMode } from '@onekeyfe/react-native-background-thread'`
 *    and pass a plain string; TypeScript narrows it without a runtime
 *    dependency on this module's value space.
 *
 * Migration note: the JSDoc on `restart()` historically said callers
 * should use the `EAppRestartMode` enum from `@onekeyhq/shared`. If that
 * is a regular TS string enum, its values aren't assignable to this union
 * directly — either cast at the call site (`mode as RestartMode`) or
 * migrate `EAppRestartMode` to a string-literal union / `as const` object
 * so the two stay aligned.
 */
export type RestartMode = 'ui' | 'all';

export interface Spec extends TurboModule {
  startBackgroundRunnerWithEntryURL(entryURL: string): void;
  installSharedBridge(): void;
  loadSegmentInBackground(segmentId: number, path: string): Promise<void>;
  /**
   * Reload one or both JS runtimes. Replaces `react-native-restart`.
   *
   * `mode` (see {@link RestartMode}):
   * - `'ui'`: restart only the main JS runtime (the one driving the UI).
   *   The background runtime stays hot, preserving its state and avoiding
   *   the SharedRPC reload race that crashed iOS on language switch.
   * - `'all'`: restart both main and background JS runtimes. Required when
   *   the JS bundle on disk has changed (OTA install/switch) so the two
   *   runtimes cannot keep running mismatched moduleId tables.
   *
   * Any other value rejects the promise — callers should pass a
   * `RestartMode` literal. The native validation still runs at runtime
   * (it's the source of truth for what the platforms accept), so a string
   * cast that smuggles in an unknown value will be rejected there.
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
   *   re-spawned by the host AppDelegate's `hostDidStart:`, with the
   *   module's two-stage `dispatch_after` health-check as a fallback if
   *   the host integration fails to re-arm (intentionally not driven by
   *   `RCTJavaScriptDidLoadNotification`, whose timing is unreliable in
   *   bridgeless / NewArch). Any process-level state (NSUserDefaults cache,
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
  restart(mode: RestartMode, reason: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
