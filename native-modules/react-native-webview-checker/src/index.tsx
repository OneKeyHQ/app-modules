import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeWebviewChecker as ReactNativeWebviewCheckerType } from './ReactNativeWebviewChecker.nitro';

const ReactNativeWebviewCheckerHybridObject =
  NitroModules.createHybridObject<ReactNativeWebviewCheckerType>('ReactNativeWebviewChecker');

export const ReactNativeWebviewChecker = ReactNativeWebviewCheckerHybridObject;
export type * from './ReactNativeWebviewChecker.nitro';
