import { getHostComponent } from 'react-native-nitro-modules';
import type { TabViewProps, TabViewMethods } from './TabView.nitro';

const TabViewConfig = require('../nitrogen/generated/shared/json/TabViewConfig.json');

export type { TabViewProps, TabViewMethods };

export default getHostComponent<TabViewProps, TabViewMethods>(
  'TabView',
  () => TabViewConfig
);
