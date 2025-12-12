import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeCheckBiometricAuthChanged
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  checkChanged(): Promise<boolean>;
}
