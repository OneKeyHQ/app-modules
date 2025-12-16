import { TurboModuleRegistry } from 'react-native';

import type { CodegenTypes, TurboModule } from 'react-native';
export interface Spec extends TurboModule {
  readonly onBackgroundMessage: CodegenTypes.EventEmitter<string>;
  postBackgroundMessage(message: string): void;
  startBackgroundRunnerWithEntryURL(entryURL: string): void;
  initBackgroundThread(): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
