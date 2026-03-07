import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export interface TabItemStruct {
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
}

export interface IconSourceStruct {
  uri: string;
  width: number;
  height: number;
  scale: number;
}

export interface TabViewProps extends HybridViewProps {
  // Tab data
  items?: TabItemStruct[];
  selectedPage?: string;
  icons?: IconSourceStruct[];

  // Display settings
  labeled?: boolean;
  sidebarAdaptable?: boolean;
  disablePageAnimations?: boolean;
  hapticFeedbackEnabled?: boolean;
  scrollEdgeAppearance?: string;
  minimizeBehavior?: string;
  tabBarHidden?: boolean;
  translucent?: boolean;

  // Colors (as processed color numbers)
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
  onPageSelected?: (key: string) => void;
  onTabLongPress?: (key: string) => void;
  onTabBarMeasured?: (height: number) => void;
  onNativeLayout?: (width: number, height: number) => void;
}

export interface TabViewMethods extends HybridViewMethods {
  // Child view management (called from custom component view)
  insertChild(tag: number, index: number): void;
  removeChild(tag: number, index: number): void;
}

export type TabView = HybridView<TabViewProps, TabViewMethods>;
