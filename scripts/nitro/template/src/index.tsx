import { NitroModules } from 'react-native-nitro-modules';
import type { {{modulePascalCase}} as {{modulePascalCase}}Type } from './{{modulePascalCase}}.nitro';

const {{modulePascalCase}}HybridObject =
  NitroModules.createHybridObject<{{modulePascalCase}}Type>('{{modulePascalCase}}');

export const {{modulePascalCase}} = {{modulePascalCase}}HybridObject;
export type * from './{{modulePascalCase}}.nitro';
