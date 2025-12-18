import { getHostComponent } from 'react-native-nitro-modules';
const {{viewPascalCase}}Config = require('../nitrogen/generated/shared/json/{{viewPascalCase}}Config.json');
import type { {{viewPascalCase}}Methods, {{viewPascalCase}}Props } from './{{viewPascalCase}}.nitro';

export const {{viewPascalCase}}View = getHostComponent<{{viewPascalCase}}Props, {{viewPascalCase}}Methods>(
  '{{viewPascalCase}}',
  () => {{viewPascalCase}}Config
);
