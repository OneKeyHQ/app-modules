import {
  codegenNativeComponent,
  type CodegenTypes,
  type ColorValue,
  type ProcessedColorValue,
  type ViewProps,
} from 'react-native';
// @ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';

export type OnTabDoublePressEventData = Readonly<{
  key: string;
  didScrollToTop: boolean;
}>;

export type OnPageSelectedEventData = Readonly<{
  key: string;
}>;

export type OnTabBarMeasured = Readonly<{
  height: CodegenTypes.Int32;
}>;

export type OnNativeLayout = Readonly<{
  width: CodegenTypes.Double;
  height: CodegenTypes.Double;
}>;

// Where the system draws the tab bar: 'bottom', or 'leading' / 'trailing' for
// a vertical bar (the iPhone Duo side strip, iOS 27.1+).
export type OnTabBarPlacementChange = Readonly<{
  placement: string;
}>;

export type OnSideAccessoryPress = Readonly<{}>;

export type BottomTabsItems = ReadonlyArray<{
  key: string;
  title: string;
  sfSymbol?: string;
  badge?: string;
  badgeBackgroundColor?: ProcessedColorValue | null;
  badgeTextColor?: ProcessedColorValue | null;
  activeTintColor?: ProcessedColorValue | null;
  hidden?: boolean;
  testID?: string;
  role?: string;
  preventsDefault?: boolean;
}>;

export interface BottomTabsNativeProps extends ViewProps {
  items: BottomTabsItems;
  selectedPage: string;
  onPageSelected?: CodegenTypes.DirectEventHandler<OnPageSelectedEventData>;
  onTabDoublePress?: CodegenTypes.DirectEventHandler<OnTabDoublePressEventData>;
  onTabBarMeasured?: CodegenTypes.DirectEventHandler<OnTabBarMeasured>;
  onNativeLayout?: CodegenTypes.DirectEventHandler<OnNativeLayout>;
  icons?: ReadonlyArray<ImageSource>;
  selectedIcons?: ReadonlyArray<ImageSource>;
  tabBarHidden?: boolean;
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  scrollEdgeAppearance?: string;
  barTintColor?: ColorValue;
  translucent?: CodegenTypes.WithDefault<boolean, true>;
  androidBottomBlurEnabled?: CodegenTypes.WithDefault<boolean, false>;
  rippleColor?: ColorValue;
  activeTintColor?: ColorValue;
  inactiveTintColor?: ColorValue;
  disablePageAnimations?: boolean;
  activeIndicatorColor?: ColorValue;
  hapticFeedbackEnabled?: boolean;
  minimizeBehavior?: string;
  fontFamily?: string;
  fontWeight?: string;
  fontSize?: CodegenTypes.Int32;
  ignoreBottomInsets?: boolean;
  androidTabBarStyle?: CodegenTypes.WithDefault<'default' | 'floating', 'default'>;
  ignoreKeyboardInsets?: CodegenTypes.WithDefault<boolean, true>;
  onTabBarPlacementChange?: CodegenTypes.DirectEventHandler<OnTabBarPlacementChange>;
  // A round button drawn above a vertical tab bar; iOS only.
  sideAccessoryIcon?: ImageSource;
  sideAccessoryLabel?: string;
  onSideAccessoryPress?: CodegenTypes.DirectEventHandler<OnSideAccessoryPress>;
}

export default codegenNativeComponent<BottomTabsNativeProps>('RNBottomTabsView');
