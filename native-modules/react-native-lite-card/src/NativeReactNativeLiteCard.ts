import { TurboModuleRegistry, type CodegenTypes, type TurboModule } from 'react-native';

export type CallbackError = { code: number; message: string | null } | null;

export type CardInfo = {
  hasBackup: boolean;
  isNewCard: boolean;
  serialNum: string;
  pinRetryCount: number;
} | null;

export type CallbackResult<T> = [
  error: CallbackError | null,
  data: T | null,
  cardInfo: CardInfo | null
];

export type PromiseResult<T> = {
  error: CallbackError | null;
  data: T | null;
  cardInfo: CardInfo | null;
};

export enum CardErrors {
  InitChannel = 1000,
  NotExistsNFC = 1001,
  NotEnableNFC = 1002,
  NotNFCPermission = 1003,

  ConnectionFail = 2001,
  InterruptError = 2002,
  DeviceMismatch = 2003,
  UserCancel = 2004,

  PasswordWrong = 3001,
  InputPasswordEmpty = 3002,
  NotSetPassword = 3003,
  InitPasswordError = 3004,
  CardLock = 3005,
  UpperErrorAutoReset = 3006,

  ExecFailure = 4000,
  InitializedError = 4001,
  NotInitializedError = 4002,
}

type KeyValuePair = {
  key: string;
  value: string;
};

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

  readonly onNFCActiveConnection: CodegenTypes.EventEmitter<KeyValuePair>;
  readonly onNFCUIEvent: CodegenTypes.EventEmitter<KeyValuePair>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ReactNativeLiteCard');
