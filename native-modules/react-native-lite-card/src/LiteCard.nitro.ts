import type { HybridObject } from 'react-native-nitro-modules';

export interface LiteCardInfo {
  [key: string]: any;
}

export interface LiteCard
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;
  
  // NFC Operations
  checkNFCPermission(): Promise<boolean>;
  getLiteInfo(): Promise<LiteCardInfo>;
  setMnemonic(mnemonic: string, pin: string, overwrite: boolean): Promise<boolean>;
  getMnemonicWithPin(pin: string): Promise<string>;
  changePin(oldPin: string, newPin: string): Promise<boolean>;
  reset(): Promise<boolean>;
}
