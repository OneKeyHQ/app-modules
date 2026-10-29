import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { ColorValue, ProcessedColorValue, ViewProps } from 'react-native';
import type {
  DirectEventHandler,
  Double,
  Int32,
  WithDefault,
} from 'react-native/Libraries/Types/CodegenTypes';
//@ts-ignore
import type { ImageSource } from 'react-native/Libraries/Image/ImageSource';

export type OnPageSelectedEventData = Readonly<{
  key: string;
}>;

export type OnTabBarMeasured = Readonly<{
  height: Int32;
}>;

export type OnNativeLayout = Readonly<{
  width: Double;
  height: Double;
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
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  onTabDoublePress?: DirectEventHandler<OnPageSelectedEventData>;
  onTabBarMeasured?: DirectEventHandler<OnTabBarMeasured>;
  onNativeLayout?: DirectEventHandler<OnNativeLayout>;
  icons?: ReadonlyArray<ImageSource>;
  selectedIcons?: ReadonlyArray<ImageSource>;
  tabBarHidden?: boolean;
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  scrollEdgeAppearance?: string;
  barTintColor?: ColorValue;
  translucent?: WithDefault<boolean, true>;
  androidBottomBlurEnabled?: WithDefault<boolean, false>;
  rippleColor?: ColorValue;
  activeTintColor?: ColorValue;
  inactiveTintColor?: ColorValue;
  disablePageAnimations?: boolean;
  activeIndicatorColor?: ColorValue;
  hapticFeedbackEnabled?: boolean;
  minimizeBehavior?: string;
  fontFamily?: string;
  fontWeight?: string;
  fontSize?: Int32;
  ignoreBottomInsets?: boolean;
  androidTabBarStyle?: WithDefault<'default' | 'floating', 'default'>;
  ignoreKeyboardInsets?: WithDefault<boolean, true>;
  onTabBarPlacementChange?: DirectEventHandler<OnTabBarPlacementChange>;
  // A round button drawn above a vertical tab bar; iOS only.
  sideAccessoryIcon?: ImageSource;
  sideAccessoryLabel?: string;
  onSideAccessoryPress?: DirectEventHandler<OnSideAccessoryPress>;
}

export default codegenNativeComponent<BottomTabsNativeProps>('RNBottomTabsView');
