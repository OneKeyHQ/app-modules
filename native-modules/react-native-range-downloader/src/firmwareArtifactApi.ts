import type {
  FirmwareArtifactCapabilities as NativeFirmwareArtifactCapabilities,
  FirmwareArtifactDownloadParams as NativeFirmwareArtifactDownloadParams,
  FirmwareArtifactReceipt,
  ReactNativeRangeDownloader as NativeReactNativeRangeDownloader,
} from './ReactNativeRangeDownloader.nitro';

export type FirmwareArtifactRouteType = 'domain' | 'pinnedIp';

export interface FirmwareArtifactDownloadParams
  extends Omit<NativeFirmwareArtifactDownloadParams, 'routeType'> {
  routeType: FirmwareArtifactRouteType;
}

export interface FirmwareArtifactCapabilities
  extends Omit<NativeFirmwareArtifactCapabilities, 'supportedRouteTypes'> {
  supportedRouteTypes: FirmwareArtifactRouteType[];
}

export interface ReactNativeRangeDownloader
  extends Omit<
    NativeReactNativeRangeDownloader,
    'downloadFirmwareArtifact' | 'getFirmwareArtifactCapabilities'
  > {
  getFirmwareArtifactCapabilities(): FirmwareArtifactCapabilities;
  downloadFirmwareArtifact(
    params: FirmwareArtifactDownloadParams
  ): Promise<FirmwareArtifactReceipt>;
}
