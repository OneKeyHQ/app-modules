import type { HybridView, HybridViewProps } from 'react-native-nitro-modules';

export interface OneKeyIconNativeProps extends HybridViewProps {
  iconName?: string;
  resolvedColor?: string;
}

export type OneKeyIcon = HybridView<OneKeyIconNativeProps>;
