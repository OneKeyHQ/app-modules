import { codegenNativeComponent, type ViewProps } from "react-native";
import type {
  DirectEventHandler,
  Double,
  WithDefault,
} from "react-native/Libraries/Types/CodegenTypes";

export interface BottomTabsDrawerNativeProps extends ViewProps {
  enabled?: WithDefault<boolean, false>;
  open?: WithDefault<boolean, false>;
  swipeEnabled?: WithDefault<boolean, false>;
  hapticFeedbackEnabled?: WithDefault<boolean, false>;
  bottomBarHeight?: Double;
  onDrawerOpenChange?: DirectEventHandler<Readonly<{ open: boolean }>>;
}

export default codegenNativeComponent<BottomTabsDrawerNativeProps>(
  "RNBottomTabsDrawer"
);
