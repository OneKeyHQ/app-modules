import { NitroModules } from 'react-native-nitro-modules';
import type { ReactNativeBundleCrypto as ReactNativeBundleCryptoType } from './ReactNativeBundleCrypto.nitro';

const ReactNativeBundleCryptoHybridObject =
  NitroModules.createHybridObject<ReactNativeBundleCryptoType>('ReactNativeBundleCrypto');

export const ReactNativeBundleCrypto = ReactNativeBundleCryptoHybridObject;
export type * from './ReactNativeBundleCrypto.nitro';
