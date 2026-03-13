import { getHostComponent } from 'react-native-nitro-modules';
const ScrollGuardConfig = require('../nitrogen/generated/shared/json/ScrollGuardConfig.json');
import type { ScrollGuardMethods, ScrollGuardProps } from './ScrollGuard.nitro';
export { ScrollGuardDirection } from './ScrollGuard.nitro';

export const ScrollGuardView = getHostComponent<
  ScrollGuardProps,
  ScrollGuardMethods
>('ScrollGuard', () => ScrollGuardConfig);

export type { ScrollGuardProps, ScrollGuardMethods };
