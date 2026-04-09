import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  // Shared
  isAvailable(): Promise<boolean>;
  syncCloud(): Promise<boolean>;
  listFiles(options: {
    scope: string;
    targetPath?: string;
  }): Promise<{
    files: Array<{
      id: string;
      name: string;
      lastModified: string;
      isFile?: boolean;
    }>;
  }>;
  deleteFromCloud(item: { id: string; path?: string }): Promise<boolean>;
  fileExists(options: {
    fileId?: string;
    targetPath?: string;
    scope?: string;
  }): Promise<boolean>;
  copyToCloud(options: {
    mimetype?: string | null;
    scope: string;
    sourcePath: { path?: string; uri?: string };
    targetPath: string;
  }): Promise<string>;
  createFile(options: {
    targetPath: string;
    content: string;
    scope?: string;
  }): Promise<string>;

  // iOS only
  getIcloudDocument(filename: string): Promise<string>;

  // Android only
  loginIfNeeded(): Promise<boolean>;
  logout(): Promise<boolean>;
  getGoogleDriveDocument(fileId: string): Promise<string>;
  getCurrentlySignedInUserData(): Promise<{
    email: string;
    name: string;
    avatarUrl: string | null;
  } | null>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNCloudFs');
