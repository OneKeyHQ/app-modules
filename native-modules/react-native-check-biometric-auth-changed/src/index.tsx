import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeCheckBiometricAuthChanged } from './ReactNativeCheckBiometricAuthChanged.nitro';

const ReactNativeCheckBiometricAuthChangedHybridObject =
  NitroModules.createHybridObject<ReactNativeCheckBiometricAuthChanged>(
    'ReactNativeCheckBiometricAuthChanged'
  );

export function checkBiometricAuthChanged(): Promise<boolean> {
  return ReactNativeCheckBiometricAuthChangedHybridObject.checkChanged();
}
