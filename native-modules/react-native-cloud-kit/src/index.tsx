import { NitroModules } from 'react-native-nitro-modules';
import type { CloudKit } from './CloudKit.nitro';

const CloudKitHybridObject =
  NitroModules.createHybridObject<CloudKit>('CloudKit');

export function multiply(a: number, b: number): number {
  return CloudKitHybridObject.multiply(a, b);
}
