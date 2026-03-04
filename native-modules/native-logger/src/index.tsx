import { NitroModules } from 'react-native-nitro-modules';
import type { NativeLogger as NativeLoggerType } from './NativeLogger.nitro';

const NativeLoggerHybridObject =
  NitroModules.createHybridObject<NativeLoggerType>('NativeLogger');

export const NativeLogger = NativeLoggerHybridObject;
export type * from './NativeLogger.nitro';
