import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeRangeDownloader as ReactNativeRangeDownloaderType } from './ReactNativeRangeDownloader.nitro';

const ReactNativeRangeDownloaderHybridObject =
  NitroModules.createHybridObject<ReactNativeRangeDownloaderType>(
    'ReactNativeRangeDownloader'
  );

export const ReactNativeRangeDownloader =
  ReactNativeRangeDownloaderHybridObject;

// Closed set of download channels (design decision 10.1). The native side builds the
// background session identifier as `so.onekey.rangedownloader.bg.<channel>`. New channels
// must be added here (review-visible), preventing consumers from passing typo'd strings.
export const RangeDownloadChannel = {
  Bundle: 'bundle',
  Apk: 'apk',
  Chart: 'chart',
  Firmware: 'firmware',
} as const;

export type * from './ReactNativeRangeDownloader.nitro';
