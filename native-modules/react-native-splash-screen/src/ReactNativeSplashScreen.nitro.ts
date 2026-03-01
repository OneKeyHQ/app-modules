import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeSplashScreen
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  preventAutoHideAsync(): Promise<boolean>;
  hideAsync(): Promise<boolean>;
}
