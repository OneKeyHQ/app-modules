import { NitroModules } from 'react-native-nitro-modules';
import type { KeychainModule } from './KeychainModule.nitro';

const KeychainModuleHybridObject =
  NitroModules.createHybridObject<KeychainModule>('KeychainModule');

export function multiply(a: number, b: number): number {
  return KeychainModuleHybridObject.multiply(a, b);
}
