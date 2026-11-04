import type * as React from "react";
import {
  codegenNativeCommands,
  codegenNativeComponent,
  type ColorValue,
  type HostComponent,
  type ViewProps,
} from "react-native";
import type { CodegenTypes } from "react-native";

export type NativeScrollerScrollEvent = Readonly<{
  contentInset: Readonly<{
    top: CodegenTypes.Double;
    right: CodegenTypes.Double;
    bottom: CodegenTypes.Double;
    left: CodegenTypes.Double;
  }>;
  contentOffset: Readonly<{ x: CodegenTypes.Double; y: CodegenTypes.Double }>;
  contentSize: Readonly<{ width: CodegenTypes.Double; height: CodegenTypes.Double }>;
  layoutMeasurement: Readonly<{ width: CodegenTypes.Double; height: CodegenTypes.Double }>;
  velocity: Readonly<{ x: CodegenTypes.Double; y: CodegenTypes.Double }>;
  targetContentOffset: Readonly<{ x: CodegenTypes.Double; y: CodegenTypes.Double }>;
  zoomScale: CodegenTypes.Double;
}>;

export interface NativeProps extends ViewProps {
  contentWidth?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  contentHeight?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  scrollEnabled?: CodegenTypes.WithDefault<boolean, true>;
  nestedScrollEnabled?: CodegenTypes.WithDefault<boolean, true>;
  showsVerticalScrollIndicator?: CodegenTypes.WithDefault<boolean, true>;
  showsHorizontalScrollIndicator?: CodegenTypes.WithDefault<boolean, false>;
  bounces?: CodegenTypes.WithDefault<boolean, true>;
  alwaysBounceVertical?: CodegenTypes.WithDefault<boolean, true>;
  keyboardDismissMode?: CodegenTypes.WithDefault<"none" | "on-drag" | "interactive", "none">;
  keyboardShouldPersistTaps?: CodegenTypes.WithDefault<
    "never" | "always" | "handled",
    "never"
  >;
  scrollEventThrottle?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  decelerationRate?: CodegenTypes.WithDefault<CodegenTypes.Double, 0.998>;
  contentInsetTop?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  contentInsetRight?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  contentInsetBottom?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  contentInsetLeft?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  topScrollEdgeEffect?: CodegenTypes.WithDefault<"none" | "hard" | "soft", "none">;
  topOverlayHeight?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  refreshEnabled?: CodegenTypes.WithDefault<boolean, false>;
  refreshing?: CodegenTypes.WithDefault<boolean, false>;
  refreshTintColor?: ColorValue;
  refreshColors?: ReadonlyArray<ColorValue>;
  refreshProgressBackgroundColor?: ColorValue;
  refreshProgressViewOffset?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  refreshTitle?: string;
  refreshTitleColor?: ColorValue;
  refreshSize?: CodegenTypes.WithDefault<"default" | "large", "default">;
  onContentViewportChange?: CodegenTypes.DirectEventHandler<Readonly<{ height: CodegenTypes.Double }>>;
  onRefresh?: CodegenTypes.DirectEventHandler<null>;
  onScroll?: CodegenTypes.DirectEventHandler<NativeScrollerScrollEvent>;
  onScrollBeginDrag?: CodegenTypes.DirectEventHandler<NativeScrollerScrollEvent>;
  onScrollEndDrag?: CodegenTypes.DirectEventHandler<NativeScrollerScrollEvent>;
  onMomentumScrollBegin?: CodegenTypes.DirectEventHandler<NativeScrollerScrollEvent>;
  onMomentumScrollEnd?: CodegenTypes.DirectEventHandler<NativeScrollerScrollEvent>;
}

type NativeScrollerHost = HostComponent<NativeProps>;

export interface NativeCommands {
  scrollTo: (
    viewRef: React.ElementRef<NativeScrollerHost>,
    x: CodegenTypes.Double,
    y: CodegenTypes.Double,
    animated: boolean
  ) => void;
  scrollToEnd: (
    viewRef: React.ElementRef<NativeScrollerHost>,
    animated: boolean
  ) => void;
  flashScrollIndicators: (
    viewRef: React.ElementRef<NativeScrollerHost>
  ) => void;
  setRefreshState: (
    viewRef: React.ElementRef<NativeScrollerHost>,
    refreshing: boolean
  ) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    "scrollTo",
    "scrollToEnd",
    "flashScrollIndicators",
    "setRefreshState",
  ],
});

export default codegenNativeComponent<NativeProps>("RNCNativeScroller", {
  interfaceOnly: true,
}) as NativeScrollerHost;
