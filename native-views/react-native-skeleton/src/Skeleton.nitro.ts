import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export interface SkeletonProps extends HybridViewProps {
  color?: string;
  shimmerSpeed?: number;
  shimmerGradientColors?: string[];
}
export interface SkeletonMethods extends HybridViewMethods {}

export type Skeleton = HybridView<SkeletonProps, SkeletonMethods>;
