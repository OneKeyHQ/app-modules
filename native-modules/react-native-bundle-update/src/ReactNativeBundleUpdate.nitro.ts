import type { HybridObject } from 'react-native-nitro-modules';

export interface BundleDownloadParams {
  downloadUrl: string;
  latestVersion: string;
  bundleVersion: string;
  fileSize: number;
  sha256: string;
}

export interface BundleDownloadResult {
  downloadedFile: string;
  downloadUrl: string;
  latestVersion: string;
  bundleVersion: string;
  sha256: string;
}

export interface BundleVerifyParams {
  downloadedFile: string;
  sha256: string;
  latestVersion: string;
  bundleVersion: string;
}

export interface BundleVerifyASCParams {
  downloadedFile: string;
  sha256: string;
  latestVersion: string;
  bundleVersion: string;
  signature: string;
}

export interface BundleDownloadASCParams {
  downloadUrl: string;
  downloadedFile: string;
  signature: string;
  latestVersion: string;
  bundleVersion: string;
  sha256: string;
}

export interface BundleInstallParams {
  downloadedFile: string;
  latestVersion: string;
  bundleVersion: string;
  signature: string;
}

export interface BundleSwitchParams {
  appVersion: string;
  bundleVersion: string;
  signature: string;
}

export interface BundleDownloadEvent {
  type: string;
  progress: number;
  message: string;
}

export interface TestResult {
  success: boolean;
  message: string;
}

export interface LocalBundleInfo {
  appVersion: string;
  bundleVersion: string;
}

export interface FallbackBundleInfo {
  appVersion: string;
  bundleVersion: string;
  signature: string;
}

export interface AscFileInfo {
  fileName: string;
  filePath: string;
  fileSize: number;
}

export interface ReactNativeBundleUpdate
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Download
  downloadBundle(params: BundleDownloadParams): Promise<BundleDownloadResult>;

  // Verify
  verifyBundle(params: BundleVerifyParams): Promise<void>;
  verifyBundleASC(params: BundleVerifyASCParams): Promise<void>;
  downloadBundleASC(params: BundleDownloadASCParams): Promise<void>;

  // Install
  installBundle(params: BundleInstallParams): Promise<void>;

  // Clear
  clearBundle(): Promise<void>;
  clearAllJSBundleData(): Promise<TestResult>;

  // Bundle data
  getFallbackUpdateBundleData(): Promise<FallbackBundleInfo[]>;
  setCurrentUpdateBundleData(params: BundleSwitchParams): Promise<void>;

  // Paths
  getWebEmbedPath(): string;
  getWebEmbedPathAsync(): Promise<string>;
  getJsBundlePath(): string;
  getJsBundlePathAsync(): Promise<string>;
  getNativeAppVersion(): Promise<string>;

  // Verification & testing
  testVerification(): Promise<boolean>;
  testSkipVerification(): Promise<boolean>;
  isBundleExists(appVersion: string, bundleVersion: string): Promise<boolean>;
  verifyExtractedBundle(
    appVersion: string,
    bundleVersion: string,
  ): Promise<void>;
  listLocalBundles(): Promise<LocalBundleInfo[]>;
  listAscFiles(): Promise<AscFileInfo[]>;
  getSha256FromFilePath(filePath: string): Promise<string>;

  // Test/debug methods
  testDeleteJsBundle(
    appVersion: string,
    bundleVersion: string,
  ): Promise<TestResult>;
  testDeleteJsRuntimeDir(
    appVersion: string,
    bundleVersion: string,
  ): Promise<TestResult>;
  testDeleteMetadataJson(
    appVersion: string,
    bundleVersion: string,
  ): Promise<TestResult>;
  testWriteEmptyMetadataJson(
    appVersion: string,
    bundleVersion: string,
  ): Promise<TestResult>;

  // Events
  addDownloadListener(callback: (event: BundleDownloadEvent) => void): number;
  removeDownloadListener(id: number): void;
}
