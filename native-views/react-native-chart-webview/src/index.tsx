import { getHostComponent } from 'react-native-nitro-modules';
const ChartWebviewConfig = require('../nitrogen/generated/shared/json/ChartWebviewConfig.json');
import type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';

export type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';

export const ChartWebviewView = getHostComponent<ChartWebviewProps, ChartWebviewMethods>(
  'ChartWebview',
  () => ChartWebviewConfig
);
