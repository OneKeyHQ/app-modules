import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeAppUpdate as ReactNativeAppUpdateType } from './ReactNativeAppUpdate.nitro';

const ReactNativeAppUpdateHybridObject =
  NitroModules.createHybridObject<ReactNativeAppUpdateType>('ReactNativeAppUpdate');

export const ReactNativeAppUpdate = ReactNativeAppUpdateHybridObject;
export type * from './ReactNativeAppUpdate.nitro';
