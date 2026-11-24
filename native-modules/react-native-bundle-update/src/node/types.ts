import type { downloadNodeFile, INodeDownloadProgress } from './download.js';

export interface IDownloadPackageParams {
  downloadUrl?: string;
  latestVersion?: string;
  bundleVersion?: string;
  fileSize?: number;
  sha256?: string;
  signature?: string;
  diffDownloadUrl?: string;
  diffFileSize?: number;
  diffSha256?: string;
  diffSignature?: string;
  headers?: Record<string, string>;
  /** Desktop only: effective only when ONEKEY_ALLOW_SKIP_GPG_VERIFICATION is enabled */
  skipGPGVerification?: boolean;
}

export type IUpdateDownloadedEvent =
  | Pick<
      IDownloadPackageParams,
      | 'latestVersion'
      | 'downloadUrl'
      | 'bundleVersion'
      | 'fileSize'
      | 'sha256'
      | 'signature'
      | 'diffDownloadUrl'
      | 'diffFileSize'
      | 'diffSha256'
      | 'diffSignature'
    >
  | undefined;

export type IUpdateOperationParams =
  | (NonNullable<IUpdateDownloadedEvent> & {
      skipGPGVerification?: boolean;
    })
  | undefined;

export interface IBundleUpdateIdentity {
  appVersion: string;
  bundleVersion: string;
}

export interface IInstalledBundle extends IBundleUpdateIdentity {
  signature: string;
}

export interface IBundleUpdateNodeState {
  getUpdateBundleData(): IInstalledBundle | undefined;
  getFallbackUpdateBundleData(): IInstalledBundle[];
  setUpdateBundleData(bundle: IInstalledBundle): void;
  setFallbackUpdateBundleData(bundles: IInstalledBundle[]): void;
  setNativeVersion(version: string): void;
  setNativeBuildNumber(buildNumber: string): void;
  clearUpdateBundleData(): void;
}

export interface IBundleUpdateNodeOptions {
  userDataDir: () => string;
  appVersion: () => string;
  buildNumber: () => string;
  publicKey: string;
  store: IBundleUpdateNodeState;
  restart: () => void | Promise<void>;
  builtInBuildDir?: () => string;
  resourcesDir?: () => string;
  logger?: Pick<Console, 'info' | 'warn' | 'error'>;
  download?: typeof downloadNodeFile;
  onProgress?: (
    progress: INodeDownloadProgress & {
      latestVersion: string;
      bundleVersion: string;
    }
  ) => void;
  onDownloadStateChange?: (downloading: boolean) => void;
  isDevelopmentVerificationOverrideEnabled?: () => boolean;
}
