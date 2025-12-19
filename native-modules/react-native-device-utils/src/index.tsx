import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeDeviceUtils as ReactNativeDeviceUtilsType } from './ReactNativeDeviceUtils.nitro';

const ReactNativeDeviceUtilsHybridObject =
  NitroModules.createHybridObject<ReactNativeDeviceUtilsType>('ReactNativeDeviceUtils');

export const ReactNativeDeviceUtils = ReactNativeDeviceUtilsHybridObject;
export type * from './ReactNativeDeviceUtils.nitro';
