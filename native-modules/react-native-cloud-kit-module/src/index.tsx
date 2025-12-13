import { NitroModules } from 'react-native-nitro-modules';
import type { CloudKitModule } from './CloudKitModule.nitro';

const CloudKitModuleHybridObject =
  NitroModules.createHybridObject<CloudKitModule>('CloudKitModule');

export function multiply(a: number, b: number): number {
  return CloudKitModuleHybridObject.multiply(a, b);
}
