import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  getIpAddresses(hostname: string): Promise<string[]>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNDnsLookup');
