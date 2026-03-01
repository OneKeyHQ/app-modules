import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeBundleUpdate as ReactNativeBundleUpdateType } from './ReactNativeBundleUpdate.nitro';

const ReactNativeBundleUpdateHybridObject =
  NitroModules.createHybridObject<ReactNativeBundleUpdateType>('ReactNativeBundleUpdate');

export const ReactNativeBundleUpdate = ReactNativeBundleUpdateHybridObject;
export type * from './ReactNativeBundleUpdate.nitro';
