import React from "react";
import { I18nManager, Platform, StyleSheet, View } from "react-native";
import type * as ReactNative from "react-native";

import CollapsiblePagerViewNativeComponent, {
  Commands as CollapsiblePagerViewNativeCommands,
  type NativeProps,
  type OnCollapsibleStateChangedEventData,
  type OnNativeTabPressEventData,
} from "./CollapsiblePagerViewNativeComponent";
import type {
  OnPageScrollEventData,
  OnPageScrollStateChangedEventData,
  OnPageSelectedEventData,
} from "./PagerViewNativeComponent";

export type CollapsiblePagerDiagnostics =
  ReactNative.NativeSyntheticEvent<OnCollapsibleStateChangedEventData>;

export type CollapsiblePagerMountState = Readonly<{
  position: number;
  mountedPages: readonly number[];
  pageKeys: readonly string[];
}>;

export type CollapsiblePagerNativeTabBarItem = Readonly<{
  key: string;
  title: string;
  accessibilityLabel?: string;
  testID?: string;
}>;

export type CollapsiblePagerNativeTabBarStyle = Readonly<{
  height?: number;
  contentPaddingHorizontal?: number;
  itemSpacing?: number;
  fontSize?: number;
  fontFamily?: string;
  backgroundColor?: ReactNative.ColorValue;
  activeTextColor?: ReactNative.ColorValue;
  inactiveTextColor?: ReactNative.ColorValue;
  indicatorColor?: ReactNative.ColorValue;
  indicatorHeight?: number;
  indicatorBottom?: number;
}>;

export type CollapsiblePagerNativeTabBarConfig = Readonly<{
  items: readonly CollapsiblePagerNativeTabBarItem[];
  style?: CollapsiblePagerNativeTabBarStyle;
}>;

export type CollapsiblePagerNativeSubHeaderColumns = Readonly<{
  leading: string;
  middle: string;
  trailing: string;
}>;

export type CollapsiblePagerNativeSubHeaderStyle = Readonly<{
  height?: number;
  tabsHeight?: number;
  contentPaddingHorizontal?: number;
  itemSpacing?: number;
  fontSize?: number;
  columnFontSize?: number;
  trailingColumnWidth?: number;
  columnGap?: number;
  selectedBackgroundColor?: ReactNative.ColorValue;
}>;

export type CollapsiblePagerNativeSubHeaderConfig = Readonly<{
  items: readonly CollapsiblePagerNativeTabBarItem[];
  selectedKey: string;
  columns: CollapsiblePagerNativeSubHeaderColumns;
  style?: CollapsiblePagerNativeSubHeaderStyle;
}>;

export type CollapsiblePagerViewOnNativeTabPressEvent =
  ReactNative.NativeSyntheticEvent<OnNativeTabPressEventData>;

export type CollapsiblePagerViewOnNativeSubHeaderPressEvent =
  ReactNative.NativeSyntheticEvent<OnNativeTabPressEventData>;

export interface CollapsiblePagerViewProps
  extends Omit<
    NativeProps,
    | "children"
    | "headerHeight"
    | "stickyHeaderHeight"
    | "pageKeys"
    | "retainedPages"
    | "nativeTabBarItems"
    | "nativeTabBarHeight"
    | "nativeTabBarContentPaddingHorizontal"
    | "nativeTabBarItemSpacing"
    | "nativeTabBarFontSize"
    | "nativeTabBarFontFamily"
    | "nativeTabBarBackgroundColor"
    | "nativeTabBarActiveTextColor"
    | "nativeTabBarInactiveTextColor"
    | "nativeTabBarIndicatorColor"
    | "nativeTabBarIndicatorHeight"
    | "nativeTabBarIndicatorBottom"
    | "nativeSubHeaderConfig"
    | "nativeSubHeaderSelectedBackgroundColor"
    | "onNativeTabPress"
    | "onNativeSubHeaderPress"
    | "layoutDirection"
  > {
  /** Collapsible content rendered above the sticky bar. */
  header: React.ReactNode;
  /** Bar which remains pinned after the header has collapsed. */
  stickyHeader: React.ReactNode;
  /** Measured header height. It may be updated after an onLayout measurement. */
  headerHeight: number;
  /** Measured sticky bar height. */
  stickyHeaderHeight: number;
  /**
   * Lets the active native list own vertical gestures that begin on pager headers.
   * Defaults to false and has no effect on Web.
   */
  nativeSmoothHeaderScrollEnabled?: boolean;
  /**
   * Number of pages retained on either side of the selected page. Page slots
   * remain stable, while distant React/native list subtrees are really
   * unmounted. Defaults to one adjacent page.
   */
  pageRetentionDistance?: number;
  layoutDirection?: "ltr" | "rtl" | "locale";
  /** Optional native tab bar for iOS and Android. */
  nativeTabBar?: CollapsiblePagerNativeTabBarConfig;
  onNativeTabPress?: (event: CollapsiblePagerViewOnNativeTabPressEvent) => void;
  /** Optional native secondary sticky header for iOS and Android. */
  nativeSubHeader?: CollapsiblePagerNativeSubHeaderConfig;
  onNativeSubHeaderPress?: (
    event: CollapsiblePagerViewOnNativeSubHeaderPressEvent
  ) => void;
  children?: React.ReactNode;
  onMountedPagesChanged?: (state: CollapsiblePagerMountState) => void;
}

type State = {
  selectedPage: number;
  transientRetainedPages: readonly number[];
};

const pageIndex = (value: number) => {
  const index = Math.trunc(value);
  return Number.isFinite(index) ? index : null;
};

const clampedPageIndex = (value: number, pageCount: number) =>
  Math.min(Math.max(0, pageIndex(value) ?? 0), Math.max(0, pageCount - 1));

const pageKey = (child: React.ReactNode, index: number) => {
  if (React.isValidElement(child) && child.key != null) {
    return String(child.key);
  }
  return `page-${index}`;
};

/**
 * Pager variant whose vertical header coordination is performed by the native
 * scrolling containers. React participates only at settled page boundaries to
 * retain the selected/adjacent heavy page subtrees.
 */
export class CollapsiblePagerView extends React.PureComponent<
  CollapsiblePagerViewProps,
  State
> {
  state: State = {
    selectedPage: clampedPageIndex(
      this.props.initialPage ?? 0,
      React.Children.toArray(this.props.children).length
    ),
    transientRetainedPages: [],
  };

  private nativeRef: React.ElementRef<
    typeof CollapsiblePagerViewNativeComponent
  > | null = null;

  private lastMountSignature: string | null = null;

  private nativeTabCommandId = 0;

  private latestNativeTabTarget: number | null = null;

  componentDidMount() {
    this.notifyMountedPagesChanged();
  }

  componentDidUpdate() {
    const pageCount = this.pages().length;
    const selectedPage = clampedPageIndex(this.state.selectedPage, pageCount);
    if (selectedPage !== this.state.selectedPage) {
      this.setState({ selectedPage });
      return;
    }
    this.notifyMountedPagesChanged();
  }

  public setPage = (selectedPage: number) => {
    const position = pageIndex(selectedPage);
    if (
      this.nativeRef &&
      position !== null &&
      position >= 0 &&
      position < this.pages().length
    ) {
      if (this.nativeTabBarEnabled()) {
        this.dispatchNativeTabPageCommand(position, true);
      } else {
        CollapsiblePagerViewNativeCommands.setPage(this.nativeRef, position);
      }
    }
  };

  public setPageWithoutAnimation = (selectedPage: number) => {
    const position = pageIndex(selectedPage);
    if (
      this.nativeRef &&
      position !== null &&
      position >= 0 &&
      position < this.pages().length
    ) {
      if (this.nativeTabBarEnabled()) {
        this.dispatchNativeTabPageCommand(position, false);
      } else {
        CollapsiblePagerViewNativeCommands.setPageWithoutAnimation(
          this.nativeRef,
          position
        );
      }
    }
  };

  public setScrollEnabled = (scrollEnabled: boolean) => {
    if (this.nativeRef) {
      CollapsiblePagerViewNativeCommands.setScrollEnabledImperatively(
        this.nativeRef,
        scrollEnabled
      );
    }
  };

  private pages() {
    return React.Children.toArray(this.props.children);
  }

  private nativeTabBarEnabled() {
    return (
      (Platform.OS === "ios" || Platform.OS === "android") &&
      !!this.props.nativeTabBar?.items.length
    );
  }

  private dispatchNativeTabPageCommand(position: number, animated: boolean) {
    const commandId = ++this.nativeTabCommandId;
    this.latestNativeTabTarget = position;
    this.setState(
      (state) => ({
        transientRetainedPages: state.transientRetainedPages.includes(position)
          ? state.transientRetainedPages
          : [...state.transientRetainedPages, position],
      }),
      () => {
        requestAnimationFrame(() => {
          if (commandId !== this.nativeTabCommandId || !this.nativeRef) {
            return;
          }
          if (animated) {
            CollapsiblePagerViewNativeCommands.setPage(
              this.nativeRef,
              position
            );
          } else {
            CollapsiblePagerViewNativeCommands.setPageWithoutAnimation(
              this.nativeRef,
              position
            );
          }
        });
      }
    );
  }

  private retentionDistance() {
    return Math.max(0, Math.floor(this.props.pageRetentionDistance ?? 1));
  }

  private mountedPages(pageCount = this.pages().length) {
    const distance = this.retentionDistance();
    const selected = Math.min(
      Math.max(0, this.state.selectedPage),
      Math.max(0, pageCount - 1)
    );
    const mounted: number[] = [];
    for (let index = 0; index < pageCount; index += 1) {
      if (
        Math.abs(index - selected) <= distance ||
        this.state.transientRetainedPages.includes(index)
      ) {
        mounted.push(index);
      }
    }
    return mounted;
  }

  private notifyMountedPagesChanged = () => {
    if (!this.props.onMountedPagesChanged) {
      return;
    }
    const pages = this.pages();
    const state = {
      position: this.state.selectedPage,
      mountedPages: this.mountedPages(pages.length),
      pageKeys: pages.map(pageKey),
    };
    const signature = JSON.stringify(state);
    if (signature === this.lastMountSignature) {
      return;
    }
    this.lastMountSignature = signature;
    this.props.onMountedPagesChanged(state);
  };

  private onPageSelected = (
    event: ReactNative.NativeSyntheticEvent<OnPageSelectedEventData>
  ) => {
    const position = clampedPageIndex(
      event.nativeEvent.position,
      this.pages().length
    );
    if (
      position !== this.state.selectedPage ||
      this.state.transientRetainedPages.length > 0
    ) {
      const pendingTarget = this.latestNativeTabTarget;
      const transitionComplete = pendingTarget === position;
      if (transitionComplete) {
        this.latestNativeTabTarget = null;
      }
      this.setState(
        {
          selectedPage: position,
          transientRetainedPages:
            !transitionComplete && pendingTarget !== null
              ? [pendingTarget]
              : [],
        },
        this.notifyMountedPagesChanged
      );
    }
    this.props.onPageSelected?.(event);
  };

  private onNativeTabPress = (
    event: ReactNative.NativeSyntheticEvent<OnNativeTabPressEventData>
  ) => {
    this.props.onNativeTabPress?.(event);
  };

  private onNativeSubHeaderPress = (
    event: ReactNative.NativeSyntheticEvent<OnNativeTabPressEventData>
  ) => {
    this.props.onNativeSubHeaderPress?.(event);
  };

  render() {
    const {
      children: _children,
      header,
      stickyHeader,
      headerHeight,
      stickyHeaderHeight,
      nativeSmoothHeaderScrollEnabled,
      pageRetentionDistance: _pageRetentionDistance,
      onMountedPagesChanged: _onMountedPagesChanged,
      onPageSelected: _onPageSelected,
      nativeTabBar,
      onNativeTabPress: _onNativeTabPress,
      nativeSubHeader,
      onNativeSubHeaderPress: _onNativeSubHeaderPress,
      layoutDirection,
      initialPage: _initialPage,
      offscreenPageLimit,
      ...nativeProps
    } = this.props;
    const pages = this.pages();
    const pageKeys = pages.map(pageKey);
    const retained = new Set(this.mountedPages(pages.length));
    const deducedLayoutDirection =
      !layoutDirection || layoutDirection === "locale"
        ? I18nManager.isRTL
          ? "rtl"
          : "ltr"
        : layoutDirection;
    const nativeTabBarEnabled =
      (Platform.OS === "ios" || Platform.OS === "android") &&
      !!nativeTabBar?.items.length;
    const nativeTabBarStyle = nativeTabBar?.style;
    const nativeSubHeaderEnabled =
      (Platform.OS === "ios" || Platform.OS === "android") &&
      !!nativeSubHeader?.items.length;
    const nativeSubHeaderStyle = nativeSubHeader?.style;
    const nativeSubHeaderConfig = nativeSubHeaderEnabled
      ? JSON.stringify({
          ...nativeSubHeader,
          style: {
            ...nativeSubHeaderStyle,
            selectedBackgroundColor: undefined,
          },
        })
      : undefined;

    return (
      <CollapsiblePagerViewNativeComponent
        {...nativeProps}
        ref={(ref) => {
          this.nativeRef = ref;
        }}
        layoutDirection={deducedLayoutDirection}
        initialPage={clampedPageIndex(
          this.props.initialPage ?? 0,
          pages.length
        )}
        offscreenPageLimit={Math.max(1, offscreenPageLimit ?? 1)}
        headerHeight={Math.max(0, Math.round(headerHeight))}
        stickyHeaderHeight={Math.max(0, Math.round(stickyHeaderHeight))}
        nativeSmoothHeaderScrollEnabled={nativeSmoothHeaderScrollEnabled}
        pageKeys={JSON.stringify(pageKeys)}
        retainedPages={JSON.stringify([...retained])}
        onPageSelected={this.onPageSelected}
        nativeTabBarItems={
          nativeTabBarEnabled ? JSON.stringify(nativeTabBar.items) : undefined
        }
        nativeTabBarHeight={
          nativeTabBarEnabled ? nativeTabBarStyle?.height ?? 44 : undefined
        }
        nativeTabBarContentPaddingHorizontal={
          nativeTabBarEnabled
            ? nativeTabBarStyle?.contentPaddingHorizontal ?? 20
            : undefined
        }
        nativeTabBarItemSpacing={
          nativeTabBarEnabled ? nativeTabBarStyle?.itemSpacing ?? 8 : undefined
        }
        nativeTabBarFontSize={
          nativeTabBarEnabled ? nativeTabBarStyle?.fontSize ?? 16 : undefined
        }
        nativeTabBarFontFamily={
          nativeTabBarEnabled ? nativeTabBarStyle?.fontFamily : undefined
        }
        nativeTabBarBackgroundColor={
          nativeTabBarEnabled ? nativeTabBarStyle?.backgroundColor : undefined
        }
        nativeTabBarActiveTextColor={
          nativeTabBarEnabled ? nativeTabBarStyle?.activeTextColor : undefined
        }
        nativeTabBarInactiveTextColor={
          nativeTabBarEnabled ? nativeTabBarStyle?.inactiveTextColor : undefined
        }
        nativeTabBarIndicatorColor={
          nativeTabBarEnabled ? nativeTabBarStyle?.indicatorColor : undefined
        }
        nativeTabBarIndicatorHeight={
          nativeTabBarEnabled
            ? nativeTabBarStyle?.indicatorHeight ?? 2
            : undefined
        }
        nativeTabBarIndicatorBottom={
          nativeTabBarEnabled
            ? nativeTabBarStyle?.indicatorBottom ?? 0
            : undefined
        }
        nativeSubHeaderConfig={nativeSubHeaderConfig}
        nativeSubHeaderSelectedBackgroundColor={
          nativeSubHeaderEnabled
            ? nativeSubHeaderStyle?.selectedBackgroundColor
            : undefined
        }
        onNativeTabPress={
          nativeTabBarEnabled ? this.onNativeTabPress : undefined
        }
        onNativeSubHeaderPress={
          nativeSubHeaderEnabled ? this.onNativeSubHeaderPress : undefined
        }
      >
        <View
          key="collapsible-header"
          collapsable={false}
          pointerEvents="box-none"
          style={[
            styles.slot,
            {
              bottom: undefined,
              height: Math.max(0, Math.round(headerHeight)),
            },
          ]}
        >
          {header}
        </View>
        <View
          key="sticky-header"
          collapsable={false}
          pointerEvents="box-none"
          style={[
            styles.slot,
            {
              top: Math.max(0, Math.round(headerHeight)),
              bottom: undefined,
              height: Math.max(0, Math.round(stickyHeaderHeight)),
            },
          ]}
        >
          {stickyHeader}
        </View>
        {pages.map((page, index) => (
          <View
            key={pageKeys[index]}
            collapsable={false}
            style={[
              styles.slot,
              // Android translates the expanded pager while consuming header scroll.
              // Keep Yoga's page bounds aligned with that native viewport.
              Platform.OS === "android"
                ? { bottom: -Math.max(0, Math.round(headerHeight)) }
                : null,
            ]}
          >
            {retained.has(index) ? page : null}
          </View>
        ))}
      </CollapsiblePagerViewNativeComponent>
    );
  }
}

const styles = {
  slot: StyleSheet.absoluteFill,
};

export type CollapsiblePagerViewOnPageScrollEvent =
  ReactNative.NativeSyntheticEvent<OnPageScrollEventData>;
export type CollapsiblePagerViewOnPageSelectedEvent =
  ReactNative.NativeSyntheticEvent<OnPageSelectedEventData>;
export type CollapsiblePagerViewOnPageScrollStateChangedEvent =
  ReactNative.NativeSyntheticEvent<OnPageScrollStateChangedEventData>;
