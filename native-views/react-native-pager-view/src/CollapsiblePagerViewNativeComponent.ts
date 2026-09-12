import type * as React from "react";
import {
  codegenNativeCommands,
  codegenNativeComponent,
  type ColorValue,
  type HostComponent,
  type ViewProps,
} from "react-native";

import type {
  DirectEventHandler,
  Double,
  Int32,
  WithDefault,
} from "react-native/Libraries/Types/CodegenTypes";

export type OnPageScrollEventData = Readonly<{
  position: Double;
  offset: Double;
}>;

export type OnPageSelectedEventData = Readonly<{
  position: Double;
}>;

export type OnPageScrollStateChangedEventData = Readonly<{
  pageScrollState: "idle" | "dragging" | "settling";
}>;

export type OnCollapsibleStateChangedEventData = Readonly<{
  position: Int32;
  headerOffset: Double;
  nativePageCount: Int32;
  attachedPageCount: Int32;
  observedScrollableCount: Int32;
  retainedPages: string;
  reason: string;
}>;

export type OnNativeTabPressEventData = Readonly<{
  position: Int32;
  key: string;
}>;

/**
 * Internal native props. Consumers should use CollapsiblePagerViewProps from
 * CollapsiblePagerView instead of mounting this host component directly.
 */
export interface NativeProps extends ViewProps {
  scrollEnabled?: WithDefault<boolean, true>;
  layoutDirection?: WithDefault<"ltr" | "rtl", "ltr">;
  initialPage?: Int32;
  offscreenPageLimit?: Int32;
  // OneKey patch: Preserve nested pager gesture coordination for collapsible pages.
  nestedScrollEnabled?: WithDefault<boolean, false>;
  nativeSmoothHeaderScrollEnabled?: WithDefault<boolean, false>;
  headerHeight?: Int32;
  stickyHeaderHeight?: Int32;
  pageKeys?: string;
  retainedPages?: string;
  nativeTabBarItems?: string;
  nativeTabBarHeight?: Double;
  nativeTabBarContentPaddingHorizontal?: Double;
  nativeTabBarItemSpacing?: Double;
  nativeTabBarFontSize?: Double;
  nativeTabBarFontFamily?: string;
  nativeTabBarBackgroundColor?: ColorValue;
  nativeTabBarActiveTextColor?: ColorValue;
  nativeTabBarInactiveTextColor?: ColorValue;
  nativeTabBarIndicatorColor?: ColorValue;
  nativeTabBarIndicatorHeight?: Double;
  nativeTabBarIndicatorBottom?: Double;
  nativeSubHeaderConfig?: string;
  nativeSubHeaderSelectedBackgroundColor?: ColorValue;
  onPageScroll?: DirectEventHandler<OnPageScrollEventData>;
  onPageSelected?: DirectEventHandler<OnPageSelectedEventData>;
  onPageScrollStateChanged?: DirectEventHandler<OnPageScrollStateChangedEventData>;
  onCollapsibleStateChanged?: DirectEventHandler<OnCollapsibleStateChangedEventData>;
  onNativeTabPress?: DirectEventHandler<OnNativeTabPressEventData>;
  onNativeSubHeaderPress?: DirectEventHandler<OnNativeTabPressEventData>;
}

type CollapsiblePagerViewNativeType = HostComponent<NativeProps>;

export interface NativeCommands {
  setPage: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    selectedPage: Int32
  ) => void;
  setPageWithoutAnimation: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    selectedPage: Int32
  ) => void;
  setScrollEnabledImperatively: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    scrollEnabled: boolean
  ) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    "setPage",
    "setPageWithoutAnimation",
    "setScrollEnabledImperatively",
  ],
});

export default codegenNativeComponent<NativeProps>(
  "RNCCollapsiblePagerView"
) as HostComponent<NativeProps>;
