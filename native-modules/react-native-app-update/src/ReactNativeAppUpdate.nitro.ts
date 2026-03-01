import type { HybridObject } from 'react-native-nitro-modules';

export interface AppUpdateDownloadParams {
  downloadUrl: string;
  filePath: string;
  notificationTitle: string;
  fileSize: number;
}

export interface AppUpdateFileParams {
  downloadUrl: string;
  filePath: string;
}

export interface DownloadEvent {
  type: string;
  progress: number;
  message: string;
}

export interface ReactNativeAppUpdate
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  downloadAPK(params: AppUpdateDownloadParams): Promise<void>;
  downloadASC(params: AppUpdateFileParams): Promise<void>;
  verifyASC(params: AppUpdateFileParams): Promise<void>;
  verifyAPK(params: AppUpdateFileParams): Promise<void>;
  installAPK(params: AppUpdateFileParams): Promise<void>;
  clearCache(): Promise<void>;

  addDownloadListener(callback: (event: DownloadEvent) => void): number;
  removeDownloadListener(id: number): void;
}
