import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeGetRandomValues as ReactNativeGetRandomValuesType } from './ReactNativeGetRandomValues.nitro';

const ReactNativeGetRandomValuesHybridObject =
  NitroModules.createHybridObject<ReactNativeGetRandomValuesType>('ReactNativeGetRandomValues');

export const ReactNativeGetRandomValues = ReactNativeGetRandomValuesHybridObject;
export type * from './ReactNativeGetRandomValues.nitro';
