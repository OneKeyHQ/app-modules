import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type { CallbackError, CardInfo } from './type';

export interface Spec extends TurboModule {
  getLiteInfo(
    callback: (
      error: CallbackError | null,
      data: CardInfo | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  checkNFCPermission(
    callback: (
      error: CallbackError | null,
      data: boolean | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  setMnemonic(
    mnemonic: string,
    pwd: string,
    overwrite: boolean,
    callback: (
      error: CallbackError | null,
      data: boolean | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  getMnemonicWithPin(
    pwd: string,
    callback: (
      error: CallbackError | null,
      data: string | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  changePin(
    oldPin: string,
    newPin: string,
    callback: (
      error: CallbackError | null,
      data: boolean | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  reset(
    callback: (
      error: CallbackError | null,
      data: boolean | null,
      cardInfo: CardInfo | null
    ) => void
  ): void;
  cancel(): void;
  intoSetting(): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ReactNativeLiteCard');
