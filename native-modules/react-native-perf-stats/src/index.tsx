import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativePerfStats as ReactNativePerfStatsType } from './ReactNativePerfStats.nitro';

const ReactNativePerfStatsHybridObject =
  NitroModules.createHybridObject<ReactNativePerfStatsType>('ReactNativePerfStats');

export const ReactNativePerfStats = ReactNativePerfStatsHybridObject;
export type * from './ReactNativePerfStats.nitro';
