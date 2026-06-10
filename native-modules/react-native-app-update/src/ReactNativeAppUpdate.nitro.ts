import type { HybridObject } from 'react-native-nitro-modules';

export interface AppUpdateDownloadParams {
  downloadUrl: string;
  notificationTitle: string;
  fileSize: number;
}

export interface AppUpdateFileParams {
  downloadUrl: string;
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
  // Wipe downloaded APK artifacts from cacheDir/apks (.apk / .partial /
  // .SHA256SUMS.asc). Android-only; the JS layer gates on update status before
  // calling so this does NOT cancel an in-flight download. iOS is a no-op stub.
  clearApkCache(): Promise<void>;

  // Verification & testing
  testVerification(): Promise<boolean>;
  testSkipVerification(): Promise<boolean>;
  isSkipGpgVerificationAllowed(): boolean;

  addDownloadListener(callback: (event: DownloadEvent) => void): number;
  removeDownloadListener(id: number): void;
}
