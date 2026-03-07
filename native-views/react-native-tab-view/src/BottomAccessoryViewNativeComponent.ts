import {
  type HostComponent,
  type NativeSyntheticEvent,
  type ViewProps,
  requireNativeComponent,
} from 'react-native';

export interface BottomAccessoryViewNativeProps extends ViewProps {
  onNativeLayout?: (
    event: NativeSyntheticEvent<{ width: number; height: number }>
  ) => void;
  onPlacementChanged?: (
    event: NativeSyntheticEvent<{ placement: string }>
  ) => void;
}

export default requireNativeComponent<BottomAccessoryViewNativeProps>(
  'RCTBottomAccessoryView'
) as HostComponent<BottomAccessoryViewNativeProps>;
