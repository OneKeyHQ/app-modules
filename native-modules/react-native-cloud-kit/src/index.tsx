import { NitroModules } from 'react-native-nitro-modules';
import type { CloudKit as CloudKitType } from './CloudKit.nitro';

const CloudKitHybridObject =
  NitroModules.createHybridObject<CloudKitType>('CloudKit');
export const CloudKit = CloudKitHybridObject;
