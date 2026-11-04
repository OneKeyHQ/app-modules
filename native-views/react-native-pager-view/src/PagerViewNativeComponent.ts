import type * as React from 'react';
import {
  codegenNativeCommands,
  codegenNativeComponent,
  type HostComponent,
  type ViewProps,
} from 'react-native';

import type { CodegenTypes } from "react-native";

export type OnPageScrollEventData = Readonly<{
  position: CodegenTypes.Double;
  offset: CodegenTypes.Double;
}>;

export type OnPageSelectedEventData = Readonly<{
  position: CodegenTypes.Double;
}>;

export type OnPageScrollStateChangedEventData = Readonly<{
  pageScrollState: 'idle' | 'dragging' | 'settling';
}>;

export interface NativeProps extends ViewProps {
  scrollEnabled?: CodegenTypes.WithDefault<boolean, true>;
  layoutDirection?: CodegenTypes.WithDefault<'ltr' | 'rtl', 'ltr'>;
  initialPage?: CodegenTypes.Int32;
  orientation?: CodegenTypes.WithDefault<'horizontal' | 'vertical', 'horizontal'>;
  offscreenPageLimit?: CodegenTypes.Int32;
  pageMargin?: CodegenTypes.Int32;
  overScrollMode?: CodegenTypes.WithDefault<'auto' | 'always' | 'never', 'auto'>;
  overdrag?: CodegenTypes.WithDefault<boolean, false>;
  keyboardDismissMode?: CodegenTypes.WithDefault<'none' | 'on-drag', 'none'>;
  /**
  * Controls the sensitivity of scroll gestures on Android.
  * Higher values make scrolling less sensitive.
  * Valid range: 2 - 8
  * @platform android
  */
  scrollSensitivity?: CodegenTypes.Int32;
  /**
  * When true on an inner PagerView, enables gesture coordination with
  * a parent (outer) PagerView. At sub-tab boundaries the inner pager's
  * gesture recognizer will not begin, allowing the outer pager to take over.
  * @platform ios
  */
  nestedScrollEnabled?: CodegenTypes.WithDefault<boolean, false>;
  onPageScroll?: CodegenTypes.DirectEventHandler<OnPageScrollEventData>;
  onPageSelected?: CodegenTypes.DirectEventHandler<OnPageSelectedEventData>;
  onPageScrollStateChanged?: CodegenTypes.DirectEventHandler<OnPageScrollStateChangedEventData>;
}

type PagerViewViewType = HostComponent<NativeProps>;

export interface NativeCommands {
  setPage: (
    viewRef: React.ElementRef<PagerViewViewType>,
    selectedPage: CodegenTypes.Int32
  ) => void;
  setPageWithoutAnimation: (
    viewRef: React.ElementRef<PagerViewViewType>,
    selectedPage: CodegenTypes.Int32
  ) => void;
  setScrollEnabledImperatively: (
    viewRef: React.ElementRef<PagerViewViewType>,
    scrollEnabled: boolean
  ) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    'setPage',
    'setPageWithoutAnimation',
    'setScrollEnabledImperatively',
  ],
});

export default codegenNativeComponent<NativeProps>(
  'RNCViewPager'
) as HostComponent<NativeProps>;
