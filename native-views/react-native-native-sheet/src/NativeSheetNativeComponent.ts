import {
  codegenNativeComponent,
  type ColorValue,
  type ViewProps,
} from 'react-native';
import type {
  DirectEventHandler,
  Double,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';

export type NativeSheetDismissEvent = Readonly<{
  reason: string;
}>;

export type NativeSheetPresentedEvent = Readonly<{
  height: Double;
}>;

export interface NativeSheetNativeProps extends ViewProps {
  open: boolean;
  sheetHeight: Double;
  securityBlocked?: WithDefault<boolean, false>;
  dismissOnPanDown?: WithDefault<boolean, true>;
  dismissOnBackdropPress?: WithDefault<boolean, false>;
  dismissOnBackPress?: WithDefault<boolean, true>;
  showHandle?: WithDefault<boolean, true>;
  cornerRadius?: WithDefault<Double, 32>;
  dimAmount?: WithDefault<Double, 0.4>;
  sheetBackgroundColor?: ColorValue;
  onDismiss?: DirectEventHandler<NativeSheetDismissEvent>;
  onPresented?: DirectEventHandler<NativeSheetPresentedEvent>;
}

export default codegenNativeComponent<NativeSheetNativeProps>('RNCNativeSheet');
