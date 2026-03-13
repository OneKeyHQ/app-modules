import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export enum ScrollGuardDirection {
  HORIZONTAL = 'horizontal',
  VERTICAL = 'vertical',
  BOTH = 'both',
}

export interface ScrollGuardProps extends HybridViewProps {
  /**
   * The scroll direction to guard.
   * - HORIZONTAL: block parent from intercepting horizontal gestures (default)
   * - VERTICAL: block parent from intercepting vertical gestures
   * - BOTH: block both directions
   */
  direction?: ScrollGuardDirection;
}

export interface ScrollGuardMethods extends HybridViewMethods {}

export type ScrollGuard = HybridView<ScrollGuardProps, ScrollGuardMethods>;
