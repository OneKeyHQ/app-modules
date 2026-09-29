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
  WithDefault,
} from "react-native/Libraries/Types/CodegenTypes";

export type NativeScrollerScrollEvent = Readonly<{
  contentInset: Readonly<{
    top: Double;
    right: Double;
    bottom: Double;
    left: Double;
  }>;
  contentOffset: Readonly<{ x: Double; y: Double }>;
  contentSize: Readonly<{ width: Double; height: Double }>;
  layoutMeasurement: Readonly<{ width: Double; height: Double }>;
  velocity: Readonly<{ x: Double; y: Double }>;
  targetContentOffset: Readonly<{ x: Double; y: Double }>;
  zoomScale: Double;
}>;

export interface NativeProps extends ViewProps {
  contentWidth?: WithDefault<Double, 0>;
  contentHeight?: WithDefault<Double, 0>;
  scrollEnabled?: WithDefault<boolean, true>;
  nestedScrollEnabled?: WithDefault<boolean, true>;
  showsVerticalScrollIndicator?: WithDefault<boolean, true>;
  showsHorizontalScrollIndicator?: WithDefault<boolean, false>;
  bounces?: WithDefault<boolean, true>;
  alwaysBounceVertical?: WithDefault<boolean, true>;
  keyboardDismissMode?: WithDefault<"none" | "on-drag" | "interactive", "none">;
  keyboardShouldPersistTaps?: WithDefault<
    "never" | "always" | "handled",
    "never"
  >;
  scrollEventThrottle?: WithDefault<Double, 0>;
  decelerationRate?: WithDefault<Double, 0.998>;
  contentInsetTop?: WithDefault<Double, 0>;
  contentInsetRight?: WithDefault<Double, 0>;
  contentInsetBottom?: WithDefault<Double, 0>;
  contentInsetLeft?: WithDefault<Double, 0>;
  refreshEnabled?: WithDefault<boolean, false>;
  refreshing?: WithDefault<boolean, false>;
  refreshTintColor?: ColorValue;
  refreshColors?: ReadonlyArray<ColorValue>;
  refreshProgressBackgroundColor?: ColorValue;
  refreshProgressViewOffset?: WithDefault<Double, 0>;
  refreshTitle?: string;
  refreshTitleColor?: ColorValue;
  refreshSize?: WithDefault<"default" | "large", "default">;
  onContentViewportChange?: DirectEventHandler<Readonly<{ height: Double }>>;
  onRefresh?: DirectEventHandler<null>;
  onScroll?: DirectEventHandler<NativeScrollerScrollEvent>;
  onScrollBeginDrag?: DirectEventHandler<NativeScrollerScrollEvent>;
  onScrollEndDrag?: DirectEventHandler<NativeScrollerScrollEvent>;
  onMomentumScrollBegin?: DirectEventHandler<NativeScrollerScrollEvent>;
  onMomentumScrollEnd?: DirectEventHandler<NativeScrollerScrollEvent>;
}

type NativeScrollerHost = HostComponent<NativeProps>;

export interface NativeCommands {
  scrollTo: (
    viewRef: React.ElementRef<NativeScrollerHost>,
    x: Double,
    y: Double,
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
