import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  isAvailable(): Promise<boolean>;
  createFile(options: Object): Promise<Object>;
  fileExists(options: Object): Promise<boolean>;
  listFiles(options: Object): Promise<Object>;
  getIcloudDocument(filename: string): Promise<string>;
  deleteFromCloud(item: Object): Promise<Object>;
  copyToCloud(options: Object): Promise<Object>;
  syncCloud(): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNCloudFs');
