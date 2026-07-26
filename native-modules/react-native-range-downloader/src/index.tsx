import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeRangeDownloader as NativeReactNativeRangeDownloader } from './ReactNativeRangeDownloader.nitro';
import type { ReactNativeRangeDownloader as PublicReactNativeRangeDownloader } from './firmwareArtifactApi';

const ReactNativeRangeDownloaderHybridObject =
  NitroModules.createHybridObject<NativeReactNativeRangeDownloader>(
    'ReactNativeRangeDownloader'
  );

export const ReactNativeRangeDownloader =
  ReactNativeRangeDownloaderHybridObject as PublicReactNativeRangeDownloader;

// Closed set of download channels (design decision 10.1). The native side builds the
// background session identifier as `so.onekey.rangedownloader.bg.<channel>`. New channels
// must be added here (review-visible), preventing consumers from passing typo'd strings.
export const RangeDownloadChannel = {
  Bundle: 'bundle',
  Apk: 'apk',
  Chart: 'chart',
} as const;

export const FirmwareArtifactRoute = {
  Domain: 'domain',
  PinnedIp: 'pinnedIp',
} as const;

export type {
  FirmwareArtifactCapabilities,
  FirmwareArtifactDownloadParams,
  FirmwareArtifactRouteType,
  ReactNativeRangeDownloader as ReactNativeRangeDownloaderType,
} from './firmwareArtifactApi';
export type * from './ReactNativeRangeDownloader.nitro';
