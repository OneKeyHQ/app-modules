import {
  type HostComponent,
  type NativeSyntheticEvent,
  type ViewProps,
  requireNativeComponent,
} from 'react-native';

export type TabItemStruct = Readonly<{
  key: string;
  title: string;
  sfSymbol?: string;
  badge?: string;
  badgeBackgroundColor?: number;
  badgeTextColor?: number;
  activeTintColor?: number;
  hidden?: boolean;
  testID?: string;
  role?: string;
  preventsDefault?: boolean;
}>;

export type IconSourceStruct = Readonly<{
  uri: string;
  width: number;
  height: number;
  scale: number;
}>;

export interface TabViewNativeProps extends ViewProps {
  // Tab data
  items?: ReadonlyArray<TabItemStruct>;
  selectedPage?: string;
  icons?: ReadonlyArray<IconSourceStruct>;

  // Display settings
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  disablePageAnimations?: boolean;
  hapticFeedbackEnabled?: boolean;
  scrollEdgeAppearance?: string;
  minimizeBehavior?: string;
  tabBarHidden?: boolean;
  translucent?: boolean;
  /** Android only: ignore bottom system insets (navigation bar) */
  ignoreBottomInsets?: boolean;

  // Colors
  barTintColor?: string;
  activeTintColor?: string;
  inactiveTintColor?: string;
  rippleColor?: string;
  activeIndicatorColor?: string;

  // Font
  fontFamily?: string;
  fontWeight?: string;
  fontSize?: number;

  // Events
  onPageSelected?: (event: NativeSyntheticEvent<{ key: string }>) => void;
  onTabLongPress?: (event: NativeSyntheticEvent<{ key: string }>) => void;
  onTabBarMeasured?: (
    event: NativeSyntheticEvent<{ height: number }>
  ) => void;
  onNativeLayout?: (
    event: NativeSyntheticEvent<{ width: number; height: number }>
  ) => void;
}

export default requireNativeComponent<TabViewNativeProps>(
  'RCTTabView'
) as HostComponent<TabViewNativeProps>;
