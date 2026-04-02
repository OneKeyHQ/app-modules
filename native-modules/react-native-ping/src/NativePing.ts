import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  start(ipAddress: string, option: { timeout?: number; payloadSize?: number }): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNReactNativePing');
