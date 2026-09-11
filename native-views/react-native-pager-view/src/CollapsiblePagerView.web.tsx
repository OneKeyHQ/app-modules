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
  basePaddingTop: string;
  boxSizing: string;
  scrollTimelineName: string;
  scrollTimelineAxis: string;
  pagerHeaderInset: string;
  pagerStickyInset: string;
}>;

const COLLAPSE_ANIMATION_NAME = "ok-collapsible-pager-collapse";
const COLLAPSE_STYLE_ID = "ok-collapsible-pager-styles";
const PAGER_HEADER_INSET = "--ok-collapsible-pager-header-inset";
const PAGER_STICKY_INSET = "--ok-collapsible-pager-sticky-inset";
let collapsiblePagerWebInstance = 0;

const pageIndex = (value: number) => {
  const index = Math.trunc(value);
  return Number.isFinite(index) ? index : null;
};

const clampedPageIndex = (value: number, pageCount: number) =>
  Math.min(
    Math.max(0, pageIndex(value) ?? 0),
    Math.max(0, pageCount - 1)
  );

const webElement = (node: unknown) => {
  const element = node as HTMLElement | null;
  return element &&
    typeof element.querySelector === "function" &&
    typeof element.addEventListener === "function"
    ? element
    : null;
};

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
    selectedPage: clampedPageIndex(
      this.props.initialPage ?? 0,
      React.Children.toArray(this.props.children).length
    ),
    animateTransition: true,
  };

  private root: HTMLElement | null = null;
  private track: HTMLElement | null = null;
  private touchStartX: number | null = null;
  private touchStartY: number | null = null;
  private horizontalTouch = false;
  private pageOffsets = new Map<string, number>();
  private pageHosts = new Map<number, HTMLElement>();
  private sharedHeaderOffset = 0;
  private scrollEnabled = this.props.scrollEnabled ?? true;
  private lastMountSignature: string | null = null;
  private configureFrame: number | null = null;
  private transitionTimer: ReturnType<typeof setTimeout> | null = null;
  private pageScrollState: OnPageScrollStateChangedEventData["pageScrollState"] =
    "idle";
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
      if (!this.scrollEnabled) {
        this.touchStartX = null;
        this.touchStartY = null;
        this.horizontalTouch = false;
        this.finishPageTransition();
      }
    }
    const pageCount = this.pages().length;
    const selectedPage = clampedPageIndex(this.state.selectedPage, pageCount);
    if (selectedPage !== this.state.selectedPage) {
      this.finishPageTransition();
      this.setState({
        selectedPage,
        animateTransition: false,
      });
      return;
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
    if (this.transitionTimer !== null) {
      clearTimeout(this.transitionTimer);
      this.transitionTimer = null;
    }
    this.setTrack(null);
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

  private emitPageScrollState(
    pageScrollState: OnPageScrollStateChangedEventData["pageScrollState"]
  ) {
    if (pageScrollState === this.pageScrollState) return;
    this.pageScrollState = pageScrollState;
    this.props.onPageScrollStateChanged?.(
      this.nativeEvent<OnPageScrollStateChangedEventData>({ pageScrollState })
    );
  }

  private finishPageTransition = () => {
    if (this.transitionTimer !== null) {
      clearTimeout(this.transitionTimer);
      this.transitionTimer = null;
    }
    this.emitPageScrollState("idle");
  };

  private beginPageTransition() {
    if (this.transitionTimer !== null) clearTimeout(this.transitionTimer);
    this.emitPageScrollState("settling");
    this.transitionTimer = setTimeout(this.finishPageTransition, 320);
  }

  private onTrackTransitionEnd = (event: TransitionEvent) => {
    if (event.target === this.track && event.propertyName === "transform") {
      this.finishPageTransition();
    }
  };

  private setTrack = (node: unknown) => {
    const track = webElement(node);
    if (track === this.track) return;
    this.track?.removeEventListener(
      "transitionend",
      this.onTrackTransitionEnd
    );
    this.track = track;
    this.track?.addEventListener("transitionend", this.onTrackTransitionEnd);
  };

  private pageScrollElement(index: number) {
    const pageHost = this.pageHosts.get(index);
    return (
      pageHost?.querySelector<HTMLElement>(
        ".ok-native-list-viewport, [data-collapsible-pager-scroll]"
      ) ?? pageHost
    );
  }

  private notifyInsetsChanged(element: HTMLElement) {
    const EventConstructor = element.ownerDocument.defaultView?.Event;
    if (EventConstructor) {
      element.dispatchEvent(
        new EventConstructor("ok-collapsible-pager-insets-changed")
      );
    }
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
    style.setProperty(PAGER_HEADER_INSET, configured.pagerHeaderInset);
    style.setProperty(PAGER_STICKY_INSET, configured.pagerStickyInset);
    configured.element.removeAttribute("data-collapsible-pager-active");
    this.notifyInsetsChanged(configured.element);
    this.configuredScrollElement = null;
  }

  private configureActiveScrollElement = () => {
    this.configureFrame = null;
    const element = this.pageScrollElement(this.state.selectedPage);
    if (!element) return;
    if (this.configuredScrollElement?.element !== element) {
      this.restoreConfiguredScrollElement();
      const style = timelineStyle(element);
      const computedPaddingTop =
        element.ownerDocument.defaultView?.getComputedStyle(element)
          .paddingTop || style.paddingTop || "0px";
      this.configuredScrollElement = {
        element,
        paddingTop: style.paddingTop,
        basePaddingTop: computedPaddingTop,
        boxSizing: style.boxSizing,
        scrollTimelineName: style.scrollTimelineName,
        scrollTimelineAxis: style.scrollTimelineAxis,
        pagerHeaderInset: style.getPropertyValue(PAGER_HEADER_INSET),
        pagerStickyInset: style.getPropertyValue(PAGER_STICKY_INSET),
      };
      element.addEventListener("scrollend", this.onVerticalScrollSettled);
    }

    const style = timelineStyle(element);
    const headerInset = Math.max(0, this.props.headerHeight);
    const stickyInset = Math.max(0, this.props.stickyHeaderHeight);
    const basePaddingTop = this.configuredScrollElement?.basePaddingTop ?? "0px";
    const paddingTop = `calc(${basePaddingTop} + ${headerInset + stickyInset}px)`;
    const headerInsetValue = `${headerInset}px`;
    const stickyInsetValue = `${stickyInset}px`;
    const insetsChanged =
      style.paddingTop !== paddingTop ||
      style.getPropertyValue(PAGER_HEADER_INSET) !== headerInsetValue ||
      style.getPropertyValue(PAGER_STICKY_INSET) !== stickyInsetValue;
    style.paddingTop = paddingTop;
    style.boxSizing = "border-box";
    style.scrollTimelineName = this.timelineName;
    style.scrollTimelineAxis = "block";
    style.setProperty(PAGER_HEADER_INSET, headerInsetValue);
    style.setProperty(PAGER_STICKY_INSET, stickyInsetValue);
    element.setAttribute("data-collapsible-pager-active", "true");
    if (insetsChanged) this.notifyInsetsChanged(element);
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
    const position = pageIndex(selectedPage);
    if (
      position === null ||
      position < 0 ||
      position >= pages.length ||
      position === this.state.selectedPage
    ) {
      return;
    }
    this.savePageOffset(this.state.selectedPage);

    if (animated) this.beginPageTransition();
    else this.finishPageTransition();
    this.setState({ selectedPage: position, animateTransition: animated }, () => {
      this.scheduleScrollConfiguration();
      const restore = () => {
        this.configureActiveScrollElement();
        this.restorePageOffset(position);
      };
      if (typeof requestAnimationFrame === "function") {
        requestAnimationFrame(restore);
      } else {
        setTimeout(restore, 0);
      }
      this.props.onPageSelected?.(
        this.nativeEvent<OnPageSelectedEventData>({ position })
      );
      this.props.onPageScroll?.(
        this.nativeEvent<OnPageScrollEventData>({
          position,
          offset: 0,
        })
      );
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
    this.horizontalTouch = false;
  };

  private onTouchMove = (event: ReactNative.GestureResponderEvent) => {
    if (this.touchStartX == null || !this.scrollEnabled || this.horizontalTouch)
      return;
    const touch = event.nativeEvent.touches[0];
    if (!touch) return;
    const deltaX = touch.pageX - this.touchStartX;
    const deltaY = touch.pageY - (this.touchStartY ?? touch.pageY);
    if (Math.abs(deltaX) >= 8 && Math.abs(deltaX) > Math.abs(deltaY)) {
      this.horizontalTouch = true;
      this.emitPageScrollState("dragging");
    }
  };

  private onTouchEnd = (event: ReactNative.GestureResponderEvent) => {
    if (this.touchStartX == null || !this.scrollEnabled) return;
    const touch = event.nativeEvent.changedTouches[0];
    if (!touch) {
      this.onTouchCancel();
      return;
    }
    const delta = touch.pageX - this.touchStartX;
    const verticalDelta = touch.pageY - (this.touchStartY ?? touch.pageY);
    this.touchStartX = null;
    this.touchStartY = null;
    const rtl =
      this.props.layoutDirection === "rtl" ||
      ((!this.props.layoutDirection ||
        this.props.layoutDirection === "locale") &&
        I18nManager.isRTL);
    const direction = Math.abs(delta) >= 40 && Math.abs(delta) > Math.abs(verticalDelta)
      ? (delta < 0 ? 1 : -1)
      : 0;
    const next = this.state.selectedPage + (rtl ? -direction : direction);
    if (direction === 0 || next < 0 || next >= this.pages().length) {
      const wasHorizontal = this.horizontalTouch;
      this.horizontalTouch = false;
      if (wasHorizontal) this.finishPageTransition();
      return;
    }
    this.horizontalTouch = false;
    this.selectPage(next, true);
  };

  private onTouchCancel = () => {
    const wasHorizontal = this.horizontalTouch;
    this.touchStartX = null;
    this.touchStartY = null;
    this.horizontalTouch = false;
    if (wasHorizontal) this.finishPageTransition();
  };

  render() {
    const {
      children: _children,
      header,
      stickyHeader,
      headerHeight: _headerHeight,
      stickyHeaderHeight: _stickyHeaderHeight,
      pageRetentionDistance: _pageRetentionDistance,
      layoutDirection: _layoutDirection,
      scrollEnabled: _scrollEnabled,
      initialPage: _initialPage,
      offscreenPageLimit: _offscreenPageLimit,
      onPageScroll: _onPageScroll,
      onPageSelected: _onPageSelected,
      onPageScrollStateChanged: _onPageScrollStateChanged,
      onCollapsibleStateChanged: _onCollapsibleStateChanged,
      onMountedPagesChanged: _onMountedPagesChanged,
      style,
      ...viewProps
    } = this.props;
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
        {...viewProps}
        ref={(node) => {
          this.root = webElement(node);
        }}
        style={[
          styles.root,
          { timelineScope: this.timelineName } as never,
          style,
        ]}
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
          ref={this.setTrack}
          style={trackStyle}
          onTouchStart={this.onTouchStart}
          onTouchMove={this.onTouchMove}
          onTouchEnd={this.onTouchEnd}
          onTouchCancel={this.onTouchCancel}
        >
          {pages.map((page, index) => (
            <View
              key={keys[index]}
              ref={(node) => {
                const host = webElement(node);
                if (host) {
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
