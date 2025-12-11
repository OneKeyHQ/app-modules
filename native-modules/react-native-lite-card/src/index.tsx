import { NitroModules } from 'react-native-nitro-modules';
import type { LiteCard } from './LiteCard.nitro';

const LiteCardHybridObject =
  NitroModules.createHybridObject<LiteCard>('LiteCard');

export function multiply(a: number, b: number): number {
  return LiteCardHybridObject.multiply(a, b);
}
