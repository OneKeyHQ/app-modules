import type { HybridObject } from 'react-native-nitro-modules';

export interface LiteCardInfo {
  hasBackup: boolean;
  pinRetryCount: number;
  isNewCard: boolean;
  serialNum: string;
}

export interface LiteCard
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;

  // NFC Operations
  checkNFCPermission(): Promise<boolean>;
  getLiteInfo(): Promise<LiteCardInfo | null>;
  setMnemonic(
    mnemonic: string,
    pin: string,
    overwrite: boolean
  ): Promise<boolean>;
  getMnemonicWithPin(pin: string): Promise<string>;
  changePin(oldPin: string, newPin: string): Promise<boolean>;
  reset(): Promise<boolean>;
}
