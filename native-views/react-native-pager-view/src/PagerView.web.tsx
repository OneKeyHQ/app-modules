import React from "react";
import { I18nManager, Keyboard, StyleSheet, View } from "react-native";
import type * as ReactNative from "react-native";

export type PagerViewOnPageScrollEventData = Readonly<{
  position: number;
  offset: number;
}>;

export type PagerViewOnPageSelectedEventData = Readonly<{
  position: number;
}>;

export type PageScrollStateChangedNativeEventData = Readonly<{
  pageScrollState: "idle" | "dragging" | "settling";
}>;

export interface PagerViewProps extends ReactNative.ViewProps {
  scrollEnabled?: boolean;
  layoutDirection?: "ltr" | "rtl" | "locale";
  initialPage?: number;
  orientation?: "horizontal" | "vertical";
  offscreenPageLimit?: number;
  pageMargin?: number;
  overScrollMode?: "auto" | "always" | "never";
  overdrag?: boolean;
  keyboardDismissMode?: "none" | "on-drag";
  scrollSensitivity?: number;
  nestedScrollEnabled?: boolean;
  onPageScroll?: (
    event: ReactNative.NativeSyntheticEvent<PagerViewOnPageScrollEventData>
  ) => void;
  onPageSelected?: (
    event: ReactNative.NativeSyntheticEvent<PagerViewOnPageSelectedEventData>
  ) => void;
  onPageScrollStateChanged?: (
    event: ReactNative.NativeSyntheticEvent<PageScrollStateChangedNativeEventData>
  ) => void;
}

type State = Readonly<{
  selectedPage: number;
  animateTransition: boolean;
}>;

const pageIndex = (value: number) => {
  const index = Math.trunc(value);
  return Number.isFinite(index) ? index : null;
};

const clampedPageIndex = (value: number, pageCount: number) =>
  Math.min(
    Math.max(0, pageIndex(value) ?? 0),
    Math.max(0, pageCount - 1)
  );

/**
 * Web fallback for the ordinary PagerView API. All pages stay mounted, matching
 * the package's pre-existing PagerView lifetime contract.
 */
export class PagerView extends React.Component<PagerViewProps, State> {
  state: State = {
    selectedPage: clampedPageIndex(
      this.props.initialPage ?? 0,
      React.Children.toArray(this.props.children).length
    ),
    animateTransition: true,
  };

  private pointerStart: Readonly<{ x: number; y: number }> | null = null;
  private scrollEnabled = this.props.scrollEnabled ?? true;

  componentDidUpdate(previousProps: PagerViewProps) {
    if (previousProps.scrollEnabled !== this.props.scrollEnabled) {
      this.scrollEnabled = this.props.scrollEnabled ?? true;
    }

    const pageCount = this.pages().length;
    if (pageCount > 0 && this.state.selectedPage >= pageCount) {
      this.setState({
        selectedPage: pageCount - 1,
        animateTransition: false,
      });
    }
  }

  public setPage = (selectedPage: number) => {
    this.selectPage(selectedPage, true);
  };

  public setPageWithoutAnimation = (selectedPage: number) => {
    this.selectPage(selectedPage, false);
  };

  public setScrollEnabled = (scrollEnabled: boolean) => {
    this.scrollEnabled = scrollEnabled;
  };

  private nativeEvent<T>(nativeEvent: T) {
    return { nativeEvent } as ReactNative.NativeSyntheticEvent<T>;
  }

  private pages() {
    return React.Children.toArray(this.props.children);
  }

  private selectPage(selectedPage: number, animated: boolean) {
    const pageCount = this.pages().length;
    const position = pageIndex(selectedPage);
    if (
      position === null ||
      position < 0 ||
      position >= pageCount ||
      position === this.state.selectedPage
    ) {
      return;
    }

    this.props.onPageScrollStateChanged?.(
      this.nativeEvent<PageScrollStateChangedNativeEventData>({
        pageScrollState: animated ? "settling" : "idle",
      })
    );
    this.setState(
      { selectedPage: position, animateTransition: animated },
      () => {
        this.props.onPageScroll?.(
          this.nativeEvent<PagerViewOnPageScrollEventData>({
            position,
            offset: 0,
          })
        );
        this.props.onPageSelected?.(
          this.nativeEvent<PagerViewOnPageSelectedEventData>({ position })
        );
        if (animated) {
          this.props.onPageScrollStateChanged?.(
            this.nativeEvent<PageScrollStateChangedNativeEventData>({
              pageScrollState: "idle",
            })
          );
        }
      }
    );
  }

  private onTouchStart = (event: ReactNative.GestureResponderEvent) => {
    if (!this.scrollEnabled) return;
    this.pointerStart = {
      x: event.nativeEvent.pageX,
      y: event.nativeEvent.pageY,
    };
    if (this.props.keyboardDismissMode === "on-drag") Keyboard.dismiss();
    this.props.onPageScrollStateChanged?.(
      this.nativeEvent<PageScrollStateChangedNativeEventData>({
        pageScrollState: "dragging",
      })
    );
  };

  private onTouchEnd = (event: ReactNative.GestureResponderEvent) => {
    const start = this.pointerStart;
    this.pointerStart = null;
    if (!start || !this.scrollEnabled) return;

    const vertical = this.props.orientation === "vertical";
    const delta = vertical
      ? event.nativeEvent.pageY - start.y
      : event.nativeEvent.pageX - start.x;
    const isRTL =
      !vertical &&
      (this.props.layoutDirection === "rtl" ||
        ((!this.props.layoutDirection ||
          this.props.layoutDirection === "locale") &&
          I18nManager.isRTL));
    const direction = Math.abs(delta) >= 40 ? (delta < 0 ? 1 : -1) : 0;
    const next = this.state.selectedPage + (isRTL ? -direction : direction);
    if (
      direction === 0 ||
      next < 0 ||
      next >= this.pages().length
    ) {
      this.props.onPageScrollStateChanged?.(
        this.nativeEvent<PageScrollStateChangedNativeEventData>({
          pageScrollState: "idle",
        })
      );
      return;
    }
    this.selectPage(next, true);
  };

  render() {
    const {
      children: _children,
      style,
      orientation = "horizontal",
      pageMargin = 0,
      scrollEnabled: _scrollEnabled,
      initialPage: _initialPage,
      layoutDirection: _layoutDirection,
      offscreenPageLimit: _offscreenPageLimit,
      overScrollMode: _overScrollMode,
      overdrag: _overdrag,
      keyboardDismissMode: _keyboardDismissMode,
      scrollSensitivity: _scrollSensitivity,
      nestedScrollEnabled: _nestedScrollEnabled,
      onPageScroll: _onPageScroll,
      onPageSelected: _onPageSelected,
      onPageScrollStateChanged: _onPageScrollStateChanged,
      ...viewProps
    } = this.props;
    const pages = this.pages();
    const vertical = orientation === "vertical";
    const pageExtent = pages.length > 0 ? `${100 / pages.length}%` : "100%";
    const transform = vertical
      ? [
          {
            translateY: `${
              (-this.state.selectedPage * 100) / Math.max(1, pages.length)
            }%`,
          },
        ]
      : [
          {
            translateX: `${
              (-this.state.selectedPage * 100) / Math.max(1, pages.length)
            }%`,
          },
        ];

    return (
      <View {...viewProps} style={[styles.root, style]}>
        <View
          style={[
            styles.track,
            vertical ? styles.verticalTrack : styles.horizontalTrack,
            vertical
              ? { height: `${Math.max(1, pages.length) * 100}%` }
              : { width: `${Math.max(1, pages.length) * 100}%` },
            {
              transform,
              transitionProperty: "transform",
              transitionDuration: this.state.animateTransition
                ? "240ms"
                : "0ms",
            } as never,
          ]}
          onTouchStart={this.onTouchStart}
          onTouchEnd={this.onTouchEnd}
        >
          {pages.map((page, index) => (
            <View
              key={React.isValidElement(page) ? page.key ?? index : index}
              style={[
                styles.page,
                vertical
                  ? { height: pageExtent, paddingVertical: pageMargin / 2 }
                  : { width: pageExtent, paddingHorizontal: pageMargin / 2 },
              ]}
            >
              {page}
            </View>
          ))}
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  root: {
    overflow: "hidden",
  },
  track: {
    flex: 1,
  },
  horizontalTrack: {
    flexDirection: "row",
    height: "100%",
  },
  verticalTrack: {
    flexDirection: "column",
    width: "100%",
  },
  page: {
    flexShrink: 0,
    minWidth: 0,
    minHeight: 0,
    overflow: "hidden",
  },
});

export type PagerViewOnPageScrollEvent =
  ReactNative.NativeSyntheticEvent<PagerViewOnPageScrollEventData>;
export type PagerViewOnPageSelectedEvent =
  ReactNative.NativeSyntheticEvent<PagerViewOnPageSelectedEventData>;
export type PageScrollStateChangedNativeEvent =
  ReactNative.NativeSyntheticEvent<PageScrollStateChangedNativeEventData>;
