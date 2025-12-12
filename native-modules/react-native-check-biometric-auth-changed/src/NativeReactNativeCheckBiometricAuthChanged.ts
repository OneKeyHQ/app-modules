import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  checkChanged(): Promise<boolean>;
}

export default TurboModuleRegistry.getEnforcing<Spec>(
  'ReactNativeCheckBiometricAuthChanged'
);
