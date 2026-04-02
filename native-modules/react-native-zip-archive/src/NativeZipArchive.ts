import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  isPasswordProtected(file: string): Promise<boolean>;
  unzip(from: string, to: string): Promise<string>;
  unzipWithPassword(from: string, to: string, password: string): Promise<string>;
  zipFolder(from: string, to: string): Promise<string>;
  zipFiles(files: string[], to: string): Promise<string>;
  getUncompressedSize(path: string): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNZipArchive');
