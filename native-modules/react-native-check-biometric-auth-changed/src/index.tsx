import ReactNativeCheckBiometricAuthChanged from './NativeReactNativeCheckBiometricAuthChanged';

export function checkBiometricAuthChanged(): Promise<boolean> {
  return ReactNativeCheckBiometricAuthChanged.checkChanged();
}
