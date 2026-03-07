import { getHostComponent } from 'react-native-nitro-modules';
import type {
  BottomAccessoryViewProps,
  BottomAccessoryViewMethods,
} from './BottomAccessoryView.nitro';

const BottomAccessoryViewConfig = require('../nitrogen/generated/shared/json/BottomAccessoryViewConfig.json');

export type { BottomAccessoryViewProps, BottomAccessoryViewMethods };

export default getHostComponent<
  BottomAccessoryViewProps,
  BottomAccessoryViewMethods
>('BottomAccessoryView', () => BottomAccessoryViewConfig);
