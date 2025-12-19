import { getHostComponent } from 'react-native-nitro-modules';
const SkeletonConfig = require('../nitrogen/generated/shared/json/SkeletonConfig.json');
import type { SkeletonMethods, SkeletonProps } from './Skeleton.nitro';

export const SkeletonView = getHostComponent<SkeletonProps, SkeletonMethods>(
  'Skeleton',
  () => SkeletonConfig
);
