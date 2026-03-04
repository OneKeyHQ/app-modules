import { getHostComponent } from 'react-native-nitro-modules';
const AutoSizeInputConfig = require('../nitrogen/generated/shared/json/AutoSizeInputConfig.json');
import type {
  AutoSizeInputMethods,
  AutoSizeInputProps,
} from './AutoSizeInput.nitro';

export const AutoSizeInputView = getHostComponent<
  AutoSizeInputProps,
  AutoSizeInputMethods
>('AutoSizeInput', () => AutoSizeInputConfig);
