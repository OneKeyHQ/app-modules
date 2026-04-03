import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  getSSID(): Promise<string | null>;
  getBSSID(): Promise<string | null>;
  getBroadcast(): Promise<string | null>;
  getIPAddress(): Promise<string>;
  getIPV6Address(): Promise<string>;
  getGatewayIPAddress(): Promise<string>;
  getIPV4Address(): Promise<string>;
  getWIFIIPV4Address(): Promise<string>;
  getSubnet(): Promise<string>;
  getFrequency(): Promise<number | null>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNNetworkInfo');
