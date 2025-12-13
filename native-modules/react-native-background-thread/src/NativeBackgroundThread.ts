import { TurboModuleRegistry } from 'react-native';

import type { CodegenTypes, TurboModule } from 'react-native';
export interface Spec extends TurboModule {
  readonly onMessage: CodegenTypes.EventEmitter<string>;
  postMessage(message: string): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BackgroundThread');
