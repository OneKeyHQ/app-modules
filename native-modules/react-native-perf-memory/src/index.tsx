import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativePerfMemory as ReactNativePerfMemoryType } from './ReactNativePerfMemory.nitro';

const ReactNativePerfMemoryHybridObject =
  NitroModules.createHybridObject<ReactNativePerfMemoryType>('ReactNativePerfMemory');

export const ReactNativePerfMemory = ReactNativePerfMemoryHybridObject;
export type * from './ReactNativePerfMemory.nitro';
