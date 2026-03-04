import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeSplashScreen as ReactNativeSplashScreenType } from './ReactNativeSplashScreen.nitro';

const ReactNativeSplashScreenHybridObject =
  NitroModules.createHybridObject<ReactNativeSplashScreenType>('ReactNativeSplashScreen');

export const ReactNativeSplashScreen = ReactNativeSplashScreenHybridObject;
export type * from './ReactNativeSplashScreen.nitro';
