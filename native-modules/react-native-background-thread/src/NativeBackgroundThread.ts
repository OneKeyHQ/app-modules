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
   */
  restart(mode: string, reason: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
