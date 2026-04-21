import { TurboModuleRegistry } from 'react-native';

import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
  startBackgroundRunnerWithEntryURL(entryURL: string): void;
  installSharedBridge(): void;
  loadSegmentInBackground(
    segmentId: number,
    path: string,
  ): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
