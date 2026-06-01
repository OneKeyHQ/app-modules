import { getHostComponent } from 'react-native-nitro-modules';

import type {
  PerpDepthBarsMethods,
  PerpDepthBarsProps,
} from './PerpDepthBars.nitro';
import type {
  PerpSideRatioMethods,
  PerpSideRatioProps,
} from './PerpSideRatio.nitro';

const PerpDepthBarsConfig = require('../nitrogen/generated/shared/json/PerpDepthBarsConfig.json');
const PerpSideRatioConfig = require('../nitrogen/generated/shared/json/PerpSideRatioConfig.json');

export type {
  PerpDepthBarsMethods,
  PerpDepthBarsProps,
  PerpSideRatioMethods,
  PerpSideRatioProps,
};

export const PerpDepthBarsView = getHostComponent<
  PerpDepthBarsProps,
  PerpDepthBarsMethods
>('PerpDepthBars', () => PerpDepthBarsConfig);

export const PerpSideRatioView = getHostComponent<
  PerpSideRatioProps,
  PerpSideRatioMethods
>('PerpSideRatio', () => PerpSideRatioConfig);
