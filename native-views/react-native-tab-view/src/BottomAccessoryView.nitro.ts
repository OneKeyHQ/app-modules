import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export interface BottomAccessoryViewProps extends HybridViewProps {
  onNativeLayout?: (width: number, height: number) => void;
  onPlacementChanged?: (placement: string) => void;
}

export interface BottomAccessoryViewMethods extends HybridViewMethods {}

export type BottomAccessoryView = HybridView<
  BottomAccessoryViewProps,
  BottomAccessoryViewMethods
>;
