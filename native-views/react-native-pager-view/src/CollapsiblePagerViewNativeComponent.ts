import type * as React from "react";
import {
  codegenNativeCommands,
  codegenNativeComponent,
  type ColorValue,
  type HostComponent,
  type ViewProps,
} from "react-native";

import type { CodegenTypes } from "react-native";

export type OnPageScrollEventData = Readonly<{
  position: CodegenTypes.Double;
  offset: CodegenTypes.Double;
}>;

export type OnPageSelectedEventData = Readonly<{
  position: CodegenTypes.Double;
}>;

export type OnPageScrollStateChangedEventData = Readonly<{
  pageScrollState: "idle" | "dragging" | "settling";
}>;

export type OnCollapsibleStateChangedEventData = Readonly<{
  position: CodegenTypes.Int32;
  headerOffset: CodegenTypes.Double;
  nativePageCount: CodegenTypes.Int32;
  attachedPageCount: CodegenTypes.Int32;
  observedScrollableCount: CodegenTypes.Int32;
  retainedPages: string;
  reason: string;
  pageKey: string;
  headerSettledAtTop: boolean;
}>;

export type OnNativeTabPressEventData = Readonly<{
  position: CodegenTypes.Int32;
  key: string;
}>;
export type OnNativeToolbarPressEventData = Readonly<{
  key: string;
  anchorRect: Readonly<{ x: CodegenTypes.Double; y: CodegenTypes.Double; width: CodegenTypes.Double; height: CodegenTypes.Double }>;
}>;
export type OnScrollEdgeCapabilityEventData = Readonly<{ available: boolean }>;

/**
 * Internal native props. Consumers should use CollapsiblePagerViewProps from
 * CollapsiblePagerView instead of mounting this host component directly.
 */
export interface NativeProps extends ViewProps {
  scrollEnabled?: CodegenTypes.WithDefault<boolean, true>;
  layoutDirection?: CodegenTypes.WithDefault<"ltr" | "rtl", "ltr">;
  initialPage?: CodegenTypes.Int32;
  offscreenPageLimit?: CodegenTypes.Int32;
  // OneKey patch: Preserve nested pager gesture coordination for collapsible pages.
  nestedScrollEnabled?: CodegenTypes.WithDefault<boolean, false>;
  nativeSmoothHeaderScrollEnabled?: CodegenTypes.WithDefault<boolean, false>;
  headerHeight?: CodegenTypes.Int32;
  stickyHeaderHeight?: CodegenTypes.Int32;
  topScrollEdgeEffect?: CodegenTypes.WithDefault<"none" | "hard" | "soft", "none">;
  topOverlayColorScheme?: string;
  topOverlayHeight?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  externalTopOverlayTag?: CodegenTypes.WithDefault<CodegenTypes.Int32, 0>;
  pageKeys?: string;
  retainedPages?: string;
  nativeTabBarConfig?: string;
  nativeTabBarItems?: string;
  nativeTabBarHeight?: CodegenTypes.Double;
  nativeTabBarVariant?: string;
  nativeTabBarTrailingReservedWidth?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  nativeTabBarContentPaddingHorizontal?: CodegenTypes.Double;
  nativeTabBarContentPaddingVertical?: CodegenTypes.Double;
  nativeTabBarItemSpacing?: CodegenTypes.Double;
  nativeTabBarFontSize?: CodegenTypes.Double;
  nativeTabBarFontFamily?: string;
  nativeTabBarBackgroundColor?: ColorValue;
  nativeTabBarActiveTextColor?: ColorValue;
  nativeTabBarInactiveTextColor?: ColorValue;
  nativeTabBarIndicatorColor?: ColorValue;
  nativeTabBarIndicatorHeight?: CodegenTypes.Double;
  nativeTabBarIndicatorBottom?: CodegenTypes.Double;
  nativeSubHeaderConfig?: string;
  nativeSubHeaderSelectedBackgroundColor?: ColorValue;
  onPageScroll?: CodegenTypes.DirectEventHandler<OnPageScrollEventData>;
  onPageSelected?: CodegenTypes.DirectEventHandler<OnPageSelectedEventData>;
  onPageScrollStateChanged?: CodegenTypes.DirectEventHandler<OnPageScrollStateChangedEventData>;
  onCollapsibleStateChanged?: CodegenTypes.DirectEventHandler<OnCollapsibleStateChangedEventData>;
  onNativeToolbarPress?: CodegenTypes.DirectEventHandler<OnNativeToolbarPressEventData>;
  onNativeTabPress?: CodegenTypes.DirectEventHandler<OnNativeTabPressEventData>;
  onNativeSubHeaderPress?: CodegenTypes.DirectEventHandler<OnNativeTabPressEventData>;
  onScrollEdgeCapability?: CodegenTypes.DirectEventHandler<OnScrollEdgeCapabilityEventData>;
}

type CollapsiblePagerViewNativeType = HostComponent<NativeProps>;

export interface NativeCommands {
  scrollHeaderToOffset: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    offset: CodegenTypes.Double
  ) => void;
  scrollHeaderBy: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    delta: CodegenTypes.Double
  ) => void;
  scrollToTop: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    target: CodegenTypes.Int32,
    animated: boolean
  ) => void;
  setPage: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    selectedPage: CodegenTypes.Int32
  ) => void;
  setPageWithoutAnimation: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    selectedPage: CodegenTypes.Int32
  ) => void;
  setScrollEnabledImperatively: (
    viewRef: React.ElementRef<CollapsiblePagerViewNativeType>,
    scrollEnabled: boolean
  ) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    "scrollHeaderToOffset",
    "scrollHeaderBy",
    "scrollToTop",
    "setPage",
    "setPageWithoutAnimation",
    "setScrollEnabledImperatively",
  ],
});

export default codegenNativeComponent<NativeProps>(
  "RNCCollapsiblePagerView"
) as HostComponent<NativeProps>;
