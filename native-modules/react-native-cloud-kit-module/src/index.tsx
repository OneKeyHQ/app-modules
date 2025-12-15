import { NitroModules } from 'react-native-nitro-modules';
import type { CloudKitModule } from './CloudKitModule.nitro';

const CloudKitModuleHybridObject =
  NitroModules.createHybridObject<CloudKitModule>('CloudKitModule');

export const CloudKit = CloudKitModuleHybridObject;
export type * from './CloudKitModule.nitro';
