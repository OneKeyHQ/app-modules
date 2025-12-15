import { NitroModules } from 'react-native-nitro-modules';
import type { KeychainModule as KeychainModuleType } from './KeychainModule.nitro';

const KeychainModuleHybridObject =
  NitroModules.createHybridObject<KeychainModuleType>('KeychainModule');

export const KeychainModule = KeychainModuleHybridObject;
export type * from './KeychainModule.nitro';
