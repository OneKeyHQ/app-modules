import {
  codegenNativeComponent,
  type CodegenTypes,
  type ViewProps,
} from "react-native";


export interface BottomTabsDrawerNativeProps extends ViewProps {
  enabled?: CodegenTypes.WithDefault<boolean, false>;
  open?: CodegenTypes.WithDefault<boolean, false>;
  swipeEnabled?: CodegenTypes.WithDefault<boolean, false>;
  hapticFeedbackEnabled?: CodegenTypes.WithDefault<boolean, false>;
  bottomBarHeight?: CodegenTypes.Double;
  onDrawerOpenChange?: CodegenTypes.DirectEventHandler<Readonly<{ open: boolean }>>;
}

export default codegenNativeComponent<BottomTabsDrawerNativeProps>(
  "RNBottomTabsDrawer"
);
