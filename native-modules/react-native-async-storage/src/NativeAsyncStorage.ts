import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  multiGet(keys: string[]): Promise<[string, string | null][]>;
  multiSet(keyValuePairs: [string, string][]): Promise<void>;
  multiRemove(keys: string[]): Promise<void>;
  multiMerge(keyValuePairs: [string, string][]): Promise<void>;
  getAllKeys(): Promise<string[]>;
  clear(): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNCAsyncStorage');
