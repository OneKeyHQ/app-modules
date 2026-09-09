import React from "react";
import { I18nManager, StyleSheet, View } from "react-native";
import type * as ReactNative from "react-native";

export type OnPageScrollEventData = Readonly<{
  position: number;
  offset: number;
}>;

export type OnPageSelectedEventData = Readonly<{
  position: number;
}>;

export type OnPageScrollStateChangedEventData = Readonly<{
  pageScrollState: "idle" | "dragging" | "settling";
}>;

export type OnCollapsibleStateChangedEventData = Readonly<{
  position: number;
  headerOffset: number;
  nativePageCount: number;
  attachedPageCount: number;
  observedScrollableCount: number;
  retainedPages: string;
  reason: string;
}>;

interface WebCollapsiblePagerNativeProps extends ReactNative.ViewProps {
  scrollEnabled?: boolean;
  initialPage?: number;
  offscreenPageLimit?: number;
  onPageScroll?: (
    event: ReactNative.NativeSyntheticEvent<OnPageScrollEventData>
  ) => void;
  onPageSelected?: (
    event: ReactNative.NativeSyntheticEvent<OnPageSelectedEventData>
  ) => void;
  onPageScrollStateChanged?: (
    event: ReactNative.NativeSyntheticEvent<OnPageScrollStateChangedEventData>
  ) => void;
  onCollapsibleStateChanged?: (
    event: ReactNative.NativeSyntheticEvent<OnCollapsibleStateChangedEventData>
  ) => void;
}

export type CollapsiblePagerDiagnostics =
  ReactNative.NativeSyntheticEvent<OnCollapsibleStateChangedEventData>;

export type CollapsiblePagerMountState = Readonly<{
  position: number;
  mountedPages: readonly number[];
  pageKeys: readonly string[];
}>;

export interface CollapsiblePagerViewProps
  extends Omit<
    WebCollapsiblePagerNativeProps,
    | "children"
    | "headerHeight"
    | "stickyHeaderHeight"
    | "pageKeys"
    | "retainedPages"
    | "layoutDirection"
  > {
  header: React.ReactNode;
  stickyHeader: React.ReactNode;
  headerHeight: number;
  stickyHeaderHeight: number;
  pageRetentionDistance?: number;
  layoutDirection?: "ltr" | "rtl" | "locale";
  children?: React.ReactNode;
  onMountedPagesChanged?: (state: CollapsiblePagerMountState) => void;
}

type State = {
  selectedPage: number;
  animateTransition: boolean;
};

type ConfiguredScrollElement = Readonly<{
  element: HTMLElement;
  paddingTop: string;
  boxSizing: string;
  scrollTimelineName: string;
  scrollTimelineAxis: string;
}>;

const COLLAPSE_ANIMATION_NAME = "ok-collapsible-pager-collapse";
const COLLAPSE_STYLE_ID = "ok-collapsible-pager-styles";
let collapsiblePagerWebInstance = 0;

const timelineStyle = (element: HTMLElement) =>
  element.style as CSSStyleDeclaration & {
    scrollTimelineName: string;
    scrollTimelineAxis: string;
  };

function ensureCollapseAnimation(document: Document) {
  if (document.getElementById(COLLAPSE_STYLE_ID)) return;
  const style = document.createElement("style");
  style.id = COLLAPSE_STYLE_ID;
  style.textContent = `@keyframes ${COLLAPSE_ANIMATION_NAME}{from{transform:translateY(0)}to{transform:translateY(calc(-1 * var(--ok-collapsible-header-height)))}}`;
  document.head.appendChild(style);
}

const pageKey = (child: React.ReactNode, index: number) => {
  if (React.isValidElement(child) && child.key != null)
    return String(child.key);
  return `page-${index}`;
};

/** Web counterpart of the native collapsible pager contract. */
export class CollapsiblePagerView extends React.PureComponent<
  CollapsiblePagerViewProps,
  State
> {
  state: State = {
    selectedPage: Math.max(0, this.props.initialPage ?? 0),
    animateTransition: true,
  };

  private root: HTMLElement | null = null;
  private touchStartX: number | null = null;
  private touchStartY: number | null = null;
  private pageOffsets = new Map<string, number>();
  private pageHosts = new Map<number, HTMLElement>();
  private sharedHeaderOffset = 0;
  private scrollEnabled = this.props.scrollEnabled ?? true;
  private lastMountSignature: string | null = null;
  private configureFrame: number | null = null;
  private configuredScrollElement: ConfiguredScrollElement | null = null;
  private readonly timelineName = `--ok-collapsible-pager-${++collapsiblePagerWebInstance}`;

  componentDidMount() {
    if (this.root) ensureCollapseAnimation(this.root.ownerDocument);
    this.scheduleScrollConfiguration();
    this.notifyMountedPagesChanged();
    this.emitDiagnostics("mounted");
  }

  componentDidUpdate(previousProps: CollapsiblePagerViewProps) {
    if (previousProps.scrollEnabled !== this.props.scrollEnabled) {
      this.scrollEnabled = this.props.scrollEnabled ?? true;
    }
    this.scheduleScrollConfiguration();
    this.notifyMountedPagesChanged();
  }

  componentWillUnmount() {
    this.savePageOffset(this.state.selectedPage);
    if (this.configureFrame !== null) {
      cancelAnimationFrame(this.configureFrame);
      this.configureFrame = null;
    }
    this.restoreConfiguredScrollElement();
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

  private pages() {
    return React.Children.toArray(this.props.children);
  }

  private retentionDistance() {
    return Math.max(0, Math.floor(this.props.pageRetentionDistance ?? 1));
  }

  private mountedPages(
    pageCount = this.pages().length,
    selected = this.state.selectedPage
  ) {
    const mounted: number[] = [];
    const distance = this.retentionDistance();
    for (let index = 0; index < pageCount; index += 1) {
      if (Math.abs(index - selected) <= distance) mounted.push(index);
    }
    return mounted;
  }

  private nativeEvent<T>(nativeEvent: T) {
    return { nativeEvent } as ReactNative.NativeSyntheticEvent<T>;
  }

  private pageScrollElement(index: number) {
    const pageHost = this.pageHosts.get(index);
    return (
      pageHost?.querySelector<HTMLElement>(
        ".ok-native-list-viewport, [data-collapsible-pager-scroll]"
      ) ?? pageHost
    );
  }

  private restoreConfiguredScrollElement() {
    const configured = this.configuredScrollElement;
    if (!configured) return;
    configured.element.removeEventListener(
      "scrollend",
      this.onVerticalScrollSettled
    );
    const style = timelineStyle(configured.element);
    style.paddingTop = configured.paddingTop;
    style.boxSizing = configured.boxSizing;
    style.scrollTimelineName = configured.scrollTimelineName;
    style.scrollTimelineAxis = configured.scrollTimelineAxis;
    configured.element.removeAttribute("data-collapsible-pager-active");
    this.configuredScrollElement = null;
  }

  private configureActiveScrollElement = () => {
    this.configureFrame = null;
    const element = this.pageScrollElement(this.state.selectedPage);
    if (!element) return;
    if (this.configuredScrollElement?.element !== element) {
      this.restoreConfiguredScrollElement();
      const style = timelineStyle(element);
      this.configuredScrollElement = {
        element,
        paddingTop: style.paddingTop,
        boxSizing: style.boxSizing,
        scrollTimelineName: style.scrollTimelineName,
        scrollTimelineAxis: style.scrollTimelineAxis,
      };
      element.addEventListener("scrollend", this.onVerticalScrollSettled);
    }

    const style = timelineStyle(element);
    style.paddingTop = `${Math.max(
      0,
      this.props.headerHeight + this.props.stickyHeaderHeight
    )}px`;
    style.boxSizing = "border-box";
    style.scrollTimelineName = this.timelineName;
    style.scrollTimelineAxis = "block";
    element.setAttribute("data-collapsible-pager-active", "true");
  };

  private scheduleScrollConfiguration() {
    if (this.configureFrame !== null) return;
    if (typeof requestAnimationFrame === "function") {
      this.configureFrame = requestAnimationFrame(
        this.configureActiveScrollElement
      );
    } else {
      this.configureActiveScrollElement();
    }
  }

  private onVerticalScrollSettled = () => {
    this.savePageOffset(this.state.selectedPage);
    this.emitDiagnostics("scroll-settled");
  };

  private savePageOffset(index: number) {
    const key = this.pages().map(pageKey)[index];
    const scrollElement = this.pageScrollElement(index);
    if (key && scrollElement) {
      this.pageOffsets.set(key, scrollElement.scrollTop);
      this.sharedHeaderOffset = Math.min(
        Math.max(scrollElement.scrollTop, 0),
        Math.max(0, this.props.headerHeight)
      );
    }
  }

  private restorePageOffset(index: number) {
    const key = this.pages().map(pageKey)[index];
    const scrollElement = this.pageScrollElement(index);
    if (!key || !scrollElement) return;
    scrollElement.scrollTop = Math.max(
      this.pageOffsets.get(key) ?? this.sharedHeaderOffset,
      this.sharedHeaderOffset
    );
  }

  private selectPage(selectedPage: number, animated: boolean) {
    const pages = this.pages();
    if (
      selectedPage < 0 ||
      selectedPage >= pages.length ||
      selectedPage === this.state.selectedPage
    ) {
      return;
    }
    this.savePageOffset(this.state.selectedPage);

    this.props.onPageScrollStateChanged?.(
      this.nativeEvent<OnPageScrollStateChangedEventData>({
        pageScrollState: animated ? "settling" : "idle",
      })
    );
    this.setState({ selectedPage, animateTransition: animated }, () => {
      this.scheduleScrollConfiguration();
      const restore = () => {
        this.configureActiveScrollElement();
        this.restorePageOffset(selectedPage);
      };
      if (typeof requestAnimationFrame === "function") {
        requestAnimationFrame(restore);
      } else {
        setTimeout(restore, 0);
      }
      this.props.onPageSelected?.(
        this.nativeEvent<OnPageSelectedEventData>({ position: selectedPage })
      );
      this.props.onPageScroll?.(
        this.nativeEvent<OnPageScrollEventData>({
          position: selectedPage,
          offset: 0,
        })
      );
      if (animated) {
        this.props.onPageScrollStateChanged?.(
          this.nativeEvent<OnPageScrollStateChangedEventData>({
            pageScrollState: "idle",
          })
        );
      }
      this.notifyMountedPagesChanged();
      this.emitDiagnostics("page-selected");
    });
  }

  private notifyMountedPagesChanged = () => {
    if (!this.props.onMountedPagesChanged) return;
    const pages = this.pages();
    const state = {
      position: this.state.selectedPage,
      mountedPages: this.mountedPages(pages.length),
      pageKeys: pages.map(pageKey),
    };
    const signature = JSON.stringify(state);
    if (signature === this.lastMountSignature) return;
    this.lastMountSignature = signature;
    this.props.onMountedPagesChanged(state);
  };

  private emitDiagnostics(reason: string) {
    const pages = this.pages();
    const retainedPages = this.mountedPages(pages.length);
    this.props.onCollapsibleStateChanged?.(
      this.nativeEvent<OnCollapsibleStateChangedEventData>({
        position: this.state.selectedPage,
        headerOffset: Math.min(
          Math.max(
            this.pageScrollElement(this.state.selectedPage)?.scrollTop ?? 0,
            0
          ),
          Math.max(0, this.props.headerHeight)
        ),
        nativePageCount: pages.length,
        attachedPageCount: retainedPages.length,
        observedScrollableCount: retainedPages.reduce(
          (count, index) => count + (this.pageScrollElement(index) ? 1 : 0),
          0
        ),
        retainedPages: JSON.stringify(retainedPages),
        reason,
      })
    );
  }

  private onTouchStart = (event: ReactNative.GestureResponderEvent) => {
    if (!this.scrollEnabled) return;
    const touch = event.nativeEvent.touches[0];
    if (!touch) return;
    this.touchStartX = touch.pageX;
    this.touchStartY = touch.pageY;
    this.props.onPageScrollStateChanged?.(
      this.nativeEvent<OnPageScrollStateChangedEventData>({
        pageScrollState: "dragging",
      })
    );
  };

  private onTouchEnd = (event: ReactNative.GestureResponderEvent) => {
    if (this.touchStartX == null || !this.scrollEnabled) return;
    const touch = event.nativeEvent.changedTouches[0];
    if (!touch) return;
    const delta = touch.pageX - this.touchStartX;
    const verticalDelta = touch.pageY - (this.touchStartY ?? touch.pageY);
    this.touchStartX = null;
    this.touchStartY = null;
    const rtl =
      this.props.layoutDirection === "rtl" ||
      (!this.props.layoutDirection && I18nManager.isRTL);
    const direction = Math.abs(delta) >= 40 && Math.abs(delta) > Math.abs(verticalDelta)
      ? (delta < 0 ? 1 : -1)
      : 0;
    const next = this.state.selectedPage + (rtl ? -direction : direction);
    if (direction === 0 || next < 0 || next >= this.pages().length) {
      this.props.onPageScrollStateChanged?.(
        this.nativeEvent<OnPageScrollStateChangedEventData>({
          pageScrollState: "idle",
        })
      );
      return;
    }
    this.selectPage(next, true);
  };

  render() {
    const { header, stickyHeader, style, testID, accessibilityLabel } =
      this.props;
    const pages = this.pages();
    const keys = pages.map(pageKey);
    const retained = new Set(this.mountedPages(pages.length));
    const pageWidth = pages.length > 0 ? `${100 / pages.length}%` : "100%";
    const collapseAnimation = {
      "--ok-collapsible-header-height": `${Math.max(
        0,
        this.props.headerHeight
      )}px`,
      animationName: COLLAPSE_ANIMATION_NAME,
      animationDuration: "auto",
      animationFillMode: "both",
      animationTimingFunction: "linear",
      animationTimeline: this.timelineName,
      animationRange: `0px ${Math.max(1, this.props.headerHeight)}px`,
    } as never;
    const trackStyle = [
      styles.track,
      {
        width: `${Math.max(1, pages.length) * 100}%`,
        transform: [
          {
            translateX: `${
              (-this.state.selectedPage * 100) / Math.max(1, pages.length)
            }%`,
          },
        ],
        transitionProperty: "transform",
        transitionDuration: this.state.animateTransition ? "240ms" : "0ms",
      },
    ] as never;

    return (
      <View
        ref={(node) => {
          this.root = node as unknown as HTMLElement | null;
        }}
        style={[
          styles.root,
          { timelineScope: this.timelineName } as never,
          style,
        ]}
        testID={testID}
        accessibilityLabel={accessibilityLabel}
      >
        <View
          style={[
            styles.header,
            { height: Math.max(0, this.props.headerHeight) },
            collapseAnimation,
          ]}
        >
          {header}
        </View>
        <View
          style={[
            styles.sticky,
            {
              top: Math.max(0, this.props.headerHeight),
              height: Math.max(0, this.props.stickyHeaderHeight),
            },
            collapseAnimation,
          ]}
        >
          {stickyHeader}
        </View>
        <View
          style={trackStyle}
          onTouchStart={this.onTouchStart}
          onTouchEnd={this.onTouchEnd}
          onTouchCancel={() => {
            this.touchStartX = null;
            this.touchStartY = null;
          }}
        >
          {pages.map((page, index) => (
            <View
              key={keys[index]}
              ref={(node) => {
                if (node) {
                  const host = node as unknown as HTMLElement;
                  host.inert = index !== this.state.selectedPage;
                  this.pageHosts.set(index, host);
                } else {
                  this.pageHosts.delete(index);
                }
              }}
              style={[styles.page, { width: pageWidth }]}
              aria-hidden={index !== this.state.selectedPage}
            >
              {retained.has(index) ? page : null}
            </View>
          ))}
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    overflow: "hidden",
    position: "relative",
  } as never,
  header: {
    position: "absolute",
    left: 0,
    right: 0,
    top: 0,
    zIndex: 3,
    overflow: "hidden",
  } as never,
  sticky: {
    position: "absolute",
    left: 0,
    right: 0,
    zIndex: 4,
    overflow: "hidden",
  } as never,
  track: {
    position: "absolute",
    inset: 0,
    display: "flex",
    flexDirection: "row",
    alignItems: "stretch",
    touchAction: "pan-y",
  } as never,
  page: {
    flexShrink: 0,
    minWidth: 0,
    minHeight: 0,
    height: "100%",
    display: "flex",
    position: "relative",
    overflowY: "auto",
    overflowX: "hidden",
  } as never,
});

export type CollapsiblePagerViewOnPageScrollEvent =
  ReactNative.NativeSyntheticEvent<OnPageScrollEventData>;
export type CollapsiblePagerViewOnPageSelectedEvent =
  ReactNative.NativeSyntheticEvent<OnPageSelectedEventData>;
export type CollapsiblePagerViewOnPageScrollStateChangedEvent =
  ReactNative.NativeSyntheticEvent<OnPageScrollStateChangedEventData>;
