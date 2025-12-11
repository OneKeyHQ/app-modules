import { NitroModules } from 'react-native-nitro-modules';
import type { LiteCard, LiteCardInfo } from './LiteCard.nitro';

const LiteCardHybridObject =
  NitroModules.createHybridObject<LiteCard>('LiteCard');

// Math operations (existing)
export function multiply(a: number, b: number): number {
  return LiteCardHybridObject.multiply(a, b);
}

// NFC Operations
export function checkNFCPermission(): Promise<boolean> {
  return LiteCardHybridObject.checkNFCPermission();
}

export function getLiteInfo(): Promise<LiteCardInfo> {
  return LiteCardHybridObject.getLiteInfo();
}

export function setMnemonic(mnemonic: string, pin: string, overwrite: boolean = false): Promise<boolean> {
  return LiteCardHybridObject.setMnemonic(mnemonic, pin, overwrite);
}

export function getMnemonicWithPin(pin: string): Promise<string> {
  return LiteCardHybridObject.getMnemonicWithPin(pin);
}

export function changePin(oldPin: string, newPin: string): Promise<boolean> {
  return LiteCardHybridObject.changePin(oldPin, newPin);
}

export function reset(): Promise<boolean> {
  return LiteCardHybridObject.reset();
}

// Export types
export type { LiteCardInfo } from './LiteCard.nitro';
