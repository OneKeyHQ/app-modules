import type { ScrollEdgeEffect } from "./ScrollEdgeEffect";
import React from "react";
import { createNamedIcon } from "@onekeyfe/react-native-image/icon/dom";
import type { CollapsiblePagerNativeTabBarConfig, CollapsiblePagerToolbarItem, CollapsiblePagerToolbarPress, CollapsiblePagerViewOnNativeTabPressEvent } from "./CollapsiblePagerView";
export type { CollapsiblePagerNativeTabBarConfig, CollapsiblePagerNativeTabBarItem, CollapsiblePagerNativeTabBarStyle, CollapsiblePagerToolbarItem, CollapsiblePagerToolbarPress } from "./CollapsiblePagerView";
import { I18nManager, Platform, processColor, StyleSheet, View } from "react-native";
import type * as ReactNative from "react-native";

import { WebCollapsiblePagerPage } from "./WebCollapsiblePagerPage";
import type { WebCollapsiblePagerPageBridge } from "./WebCollapsiblePagerPage";

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
  nativeTabBar?: CollapsiblePagerNativeTabBarConfig;
  nativeTabPressAnimationEnabled?: boolean;
  onNativeTabPress?: (event: CollapsiblePagerViewOnNativeTabPressEvent) => void;
  onNativeToolbarPress?: (event: ReactNative.NativeSyntheticEvent<CollapsiblePagerToolbarPress>) => void;
  header: React.ReactNode;
  stickyHeader: React.ReactNode;
  headerHeight?: number;
  stickyHeaderHeight?: number;
  isActive?: boolean;
  topOverlay?: React.ReactNode;
  topScrollEdgeEffect?: `${ScrollEdgeEffect}`;
  topOverlayColorScheme?: "light" | "dark";
  externalTopOverlayTag?: number;
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
  key: string;
  element: HTMLElement;
  paddingTarget: HTMLElement;
  rangeSpacer: HTMLElement;
  position: string;
  paddingTop: string;
  basePaddingTop: string;
  boxSizing: string;
  scrollTimelineName: string;
  scrollTimelineAxis: string;
  pagerHeaderInset: string;
  pagerStickyInset: string;
}>;

type WebRouteTarget = Readonly<{
  key: string;
  identity: string;
  owner: HTMLElement;
  generation: number;
  top: number;
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
  Math.min(Math.max(0, pageIndex(value) ?? 0), Math.max(0, pageCount - 1));

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

function scrollElementTo(element: HTMLElement, options: ScrollToOptions) {
  // RN Web decorates ScrollView nodes with a React Native scrollTo signature.
  const scrollTo: (options: ScrollToOptions) => void =
    element.ownerDocument.defaultView?.Element.prototype.scrollTo ??
    element.scrollTo;
  scrollTo.call(element, options);
}

function ensureCollapseAnimation(document: Document) {
  if (document.getElementById(COLLAPSE_STYLE_ID)) return;
  const style = document.createElement("style");
  style.id = COLLAPSE_STYLE_ID;
  style.textContent = `@keyframes ${COLLAPSE_ANIMATION_NAME}{from{transform:translateY(0)}to{transform:translateY(calc(-1 * var(--ok-collapsible-header-height)))}}`;
  document.head.appendChild(style);
}

function supportsScrollTimeline(document: Document) {
  const css = document.defaultView?.CSS;
  return !!(
    typeof css?.supports === "function" &&
    css.supports("animation-timeline", "--ok-test") &&
    css.supports("timeline-scope", "--ok-test") &&
    css.supports("scroll-timeline-name", "--ok-test") &&
    css.supports("scroll-timeline-axis", "block") &&
    css.supports("animation-range", "0px 1px") &&
    css.supports("animation-duration", "auto")
  );
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
  private tabProgress: number | null = null;
  private tabProgressFrame: number | null = null;
  private tabProgressGeneration = 0;
  private tabButtons = new Map<string, HTMLButtonElement>();
  private headerSlot: HTMLElement | null = null;
  private stickySlot: HTMLElement | null = null;
  private cssTimelineSupported = false;
  private overlayTouch: {
    owner: HTMLElement;
    target: HTMLElement;
    x: number;
    y: number;
    lastY: number;
  } | null = null;
  private touchStartX: number | null = null;
  private touchStartY: number | null = null;
  private horizontalTouch = false;
  private pageOffsets = new Map<string, number>();
  private pageHosts = new Map<number, HTMLElement>();
  private pageScrollIdentities = new Map<string, string>();
  private pageBridges = new Map<string, WebCollapsiblePagerPageBridge>();
  private ownerRecords = new Map<
    string,
    { element: HTMLElement; identity: string; generation: number }
  >();
  private ownerGeneration = 0;
  private lastRawTops = new Map<string, number>();
  private routeTarget: WebRouteTarget | null = null;
  private measuredHeaderHeight = 0;
  private measuredStickyHeight = 0;
  private headerMeasured = false;
  private stickyMeasured = false;
  private headerContent: HTMLElement | null = null;
  private stickyContent: HTMLElement | null = null;
  private chromeObserver: ResizeObserver | null = null;
  private chromeMeasureFrame: number | null = null;
  private disposed = false;
  private sharedHeaderOffset = 0;
  private selectionRestoreToken = 0;
  private bodyAlignment: Readonly<{
    element: HTMLElement;
    key: string;
  }> | null = null;
  private scrollEnabled = this.props.scrollEnabled ?? true;
  private lastMountSignature: string | null = null;
  private configureFrame: number | null = null;
  private pendingTopFrame: Readonly<{
    frame: number;
    element: HTMLElement;
    key: string;
  }> | null = null;
  private transitionTimer: ReturnType<typeof setTimeout> | null = null;
  private transitionGeneration = 0;
  private pageScrollState: OnPageScrollStateChangedEventData["pageScrollState"] =
    "idle";
  private configuredScrollElement: ConfiguredScrollElement | null = null;
  private readonly timelineName = `--ok-collapsible-pager-${++collapsiblePagerWebInstance}`;

  private get headerHeight() {
    return this.hasExplicitHeaderHeight
      ? (this.props.headerHeight ?? this.measuredHeaderHeight)
      : this.measuredHeaderHeight;
  }

  private get hasExplicitHeaderHeight() {
    return this.props.headerHeight !== undefined &&
      Number.isFinite(this.props.headerHeight) &&
      this.props.headerHeight >= 0;
  }

  private get stickyHeaderHeight() {
    return this.hasExplicitStickyHeaderHeight
      ? (this.props.stickyHeaderHeight ?? this.measuredStickyHeight)
      : this.measuredStickyHeight;
  }

  private get hasExplicitStickyHeaderHeight() {
    return this.props.stickyHeaderHeight !== undefined &&
      Number.isFinite(this.props.stickyHeaderHeight) &&
      this.props.stickyHeaderHeight >= 0;
  }

  getSnapshotBeforeUpdate(previousProps: CollapsiblePagerViewProps) {
    if (previousProps.isActive !== false && this.props.isActive === false)
      return this.captureRouteTarget(previousProps);
    return null;
  }

  componentDidMount() {
    this.disposed = false;
    if (Platform.OS !== "web" || typeof document === "undefined")
      throw new Error("WebCollapsiblePagerView requires a Web DOM runtime");
    if (!this.hasExplicitHeaderHeight) this.headerMeasured = false;
    if (!this.hasExplicitStickyHeaderHeight) this.stickyMeasured = false;
    this.observeChrome();
    if (this.root) {
      this.cssTimelineSupported = supportsScrollTimeline(
        this.root.ownerDocument
      );
      if (this.cssTimelineSupported) {
        ensureCollapseAnimation(this.root.ownerDocument);
        this.forceUpdate();
      }
    }
    this.scheduleScrollConfiguration();
    this.syncFallbackHeader();
    this.notifyMountedPagesChanged();
    this.syncToolbar();
    this.tabProgress = this.state.selectedPage;
    this.syncTabColors();
    this.emitDiagnostics("mounted");
  }

  componentDidUpdate(
    previousProps: CollapsiblePagerViewProps,
    _previousState: State,
    routeSnapshot: WebRouteTarget | null,
  ) {
    if (routeSnapshot) {
      const record = this.ownerRecords.get(routeSnapshot.key);
      if (
        this.pages().map(pageKey).includes(routeSnapshot.key) &&
        record?.element === routeSnapshot.owner &&
        record.identity === routeSnapshot.identity &&
        record.generation === routeSnapshot.generation
      )
        this.setRouteTarget(routeSnapshot);
    }
    if (previousProps.isActive === false && this.props.isActive !== false) {
      if (!this.hasExplicitHeaderHeight) this.headerMeasured = false;
      if (!this.hasExplicitStickyHeaderHeight) this.stickyMeasured = false;
      this.scheduleChromeMeasurement();
      this.restoreScrollPosition();
    }
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
      this.selectionRestoreToken += 1;
      this.cancelPendingTopFrame();
      this.finishPageTransition();
      this.setState({
        selectedPage,
        animateTransition: false,
      });
      return;
    }
    // Child layout effects can run before page-host refs are reattached.
    // Recheck identities in the same commit, before a new user command runs.
    this.mountedPages().forEach((index) =>
      this.synchronizeScrollOwnerIdentity(index)
    );
    const currentKeys = new Set(this.pages().map(pageKey));
    const ownedKeys = new Set([
      ...this.pageScrollIdentities.keys(),
      ...this.pageOffsets.keys(),
      ...this.lastRawTops.keys(),
      ...this.ownerRecords.keys(),
    ]);
    for (const key of ownedKeys) {
      if (!currentKeys.has(key)) {
        this.pageScrollIdentities.delete(key);
        this.pageOffsets.delete(key);
        this.lastRawTops.delete(key);
        this.ownerRecords.delete(key);
        if (this.routeTarget?.key === key) this.setRouteTarget(null);
      }
    }
    this.scheduleScrollConfiguration();
    this.syncFallbackHeader();
    this.notifyMountedPagesChanged();
    this.syncToolbar();
    if (previousProps.isActive === false && this.props.isActive !== false) this.readTabProgress();
    if (this.props.isActive === false || !this.tabColorWeights()) this.cancelTabProgressFrame();
    this.syncTabColors();
  }

  componentWillUnmount() {
    // Activity can disconnect effects before the inactive update commits.
    const routeTarget = this.captureRouteTarget(this.props);
    if (routeTarget) this.setRouteTarget(routeTarget);
    this.disposed = true;
    this.setToolbarSlot(null);
    this.selectionRestoreToken += 1;
    this.cancelPendingTopFrame();
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
    this.setRoot(null);
    this.chromeObserver?.disconnect();
    this.chromeObserver = null;
    if (this.chromeMeasureFrame !== null) {
      cancelAnimationFrame(this.chromeMeasureFrame);
      this.chromeMeasureFrame = null;
    }
    this.pageBridges.clear();
    this.restoreConfiguredScrollElement();
  }

  private setHeaderContent = (node: unknown) => {
    const element = webElement(node);
    if (element === this.headerContent) return;
    if (this.headerContent) this.chromeObserver?.unobserve(this.headerContent);
    this.headerContent = element;
    this.headerMeasured = false;
    if (element) this.chromeObserver?.observe(element);
    this.scheduleChromeMeasurement();
  };

  private setStickyContent = (node: unknown) => {
    const element = webElement(node);
    if (element === this.stickyContent) return;
    if (this.stickyContent) this.chromeObserver?.unobserve(this.stickyContent);
    this.stickyContent = element;
    this.stickyMeasured = false;
    if (element) this.chromeObserver?.observe(element);
    this.scheduleChromeMeasurement();
  };

  private observeChrome() {
    this.chromeObserver = new ResizeObserver(this.scheduleChromeMeasurement);
    if (this.root) this.chromeObserver.observe(this.root);
    if (this.headerContent) this.chromeObserver.observe(this.headerContent);
    if (this.stickyContent) this.chromeObserver.observe(this.stickyContent);
    this.scheduleChromeMeasurement();
  }

  private scheduleChromeMeasurement = () => {
    if (this.disposed || this.chromeMeasureFrame !== null || typeof requestAnimationFrame !== "function")
      return;
    this.chromeMeasureFrame = requestAnimationFrame(() => {
      this.chromeMeasureFrame = null;
      const header = this.headerContent;
      const sticky = this.stickyContent;
      const nextHeader = header?.isConnected
        ? Math.max(0, header.getBoundingClientRect().height)
        : 0;
      const nextSticky = sticky?.isConnected
        ? Math.max(0, sticky.getBoundingClientRect().height)
        : 0;
      const headerChanged =
        this.measuredHeaderHeight !== nextHeader ||
        this.headerMeasured !== Boolean(header?.isConnected);
      const stickyChanged =
        this.measuredStickyHeight !== nextSticky ||
        this.stickyMeasured !== Boolean(sticky?.isConnected);
      this.measuredHeaderHeight = nextHeader;
      this.measuredStickyHeight = nextSticky;
      this.headerMeasured = Boolean(header?.isConnected);
      this.stickyMeasured = Boolean(sticky?.isConnected);
      this.syncToolbar();
      this.syncTabColors();
      if (headerChanged || stickyChanged) {
        this.forceUpdate();
        this.scheduleScrollConfiguration();
      }
    });
  };

  private onPageBridge = (key: string, bridge: WebCollapsiblePagerPageBridge | null) => {
    if (bridge) {
      this.pageBridges.set(key, bridge);
      bridge.setPendingRouteScrollTop(
        this.routeTarget?.key === key ? this.routeTarget.top : null,
      );
      const index = this.pages().map(pageKey).indexOf(key);
      if (index >= 0) this.syncPageOwner(index);
    } else {
      this.pageBridges.delete(key);
    }
  };

  private setRouteTarget(target: WebRouteTarget | null) {
    const oldKey = this.routeTarget?.key;
    this.routeTarget = target;
    if (oldKey) this.pageBridges.get(oldKey)?.setPendingRouteScrollTop(null);
    if (target)
      this.pageBridges.get(target.key)?.setPendingRouteScrollTop(target.top);
  }

  private syncPageOwner(index: number) {
    const key = this.pages().map(pageKey)[index];
    const element = this.pageScrollElement(index);
    if (!key || !element) return;
    const identity = element.getAttribute("data-collapsible-pager-scroll-key") ?? key;
    const previous = this.ownerRecords.get(key);
    if (previous?.element !== element || previous.identity !== identity) {
      if (this.routeTarget?.key === key) this.setRouteTarget(null);
      this.ownerRecords.set(key, {
        element,
        identity,
        generation: ++this.ownerGeneration,
      });
      this.lastRawTops.delete(key);
    }
    this.pageBridges.get(key)?.setOwner(element);
  }

  private captureRouteTarget(previousProps: CollapsiblePagerViewProps) {
    const index = this.state.selectedPage;
    const oldKey = React.Children.toArray(previousProps.children).map(pageKey)[index];
    const key = this.configuredScrollElement?.key ?? oldKey;
    const record = key ? this.ownerRecords.get(key) : null;
    if (!record?.element.isConnected) return null;
    if (
      this.routeTarget?.key === key &&
      this.routeTarget.identity === record.identity &&
      this.routeTarget.owner === record.element &&
      this.routeTarget.generation === record.generation
    )
      return this.routeTarget;
    const top =
      record.element.clientHeight > 0
        ? record.element.scrollTop
        : this.lastRawTops.get(key) ?? record.element.scrollTop;
    return {
      key,
      identity: record.identity,
      owner: record.element,
      generation: record.generation,
      top: Math.max(0, top),
    };
  }

  public restoreScrollPosition = () => {
    if (this.disposed || this.props.isActive === false) return false;
    const pending = this.routeTarget;
    if (!pending) return true;
    const key = this.pages().map(pageKey)[this.state.selectedPage];
    if (key !== pending.key) return false;
    this.syncPageOwner(this.state.selectedPage);
    const record = this.ownerRecords.get(key);
    if (
      !record ||
      record.element !== pending.owner ||
      record.identity !== pending.identity ||
      record.generation !== pending.generation
    )
      return false;
    const element = record.element;
    const configured = this.configuredScrollElement;
    if (
      !element.isConnected ||
      element.clientHeight <= 0 ||
      configured?.element !== element ||
      configured.key !== key ||
      (!this.hasExplicitHeaderHeight && !this.headerMeasured) ||
      (!this.hasExplicitStickyHeaderHeight && !this.stickyMeasured) ||
      element.getAttribute("data-collapsible-pager-active") !== "true" ||
      element.style.getPropertyValue(PAGER_HEADER_INSET).trim() !==
        `${this.headerHeight}px` ||
      element.style.getPropertyValue(PAGER_STICKY_INSET).trim() !==
        `${this.stickyHeaderHeight}px` ||
      element.scrollHeight - element.clientHeight < pending.top
    )
      return false;
    this.selectionRestoreToken += 1;
    this.cancelPendingTopFrame();
    this.setRouteTarget(null);
    this.lastRawTops.set(key, pending.top);
    const D = this.headerHeight;
    this.sharedHeaderOffset = Math.min(pending.top, D);
    this.pageOffsets.set(key, Math.max(0, pending.top - D));
    this.bodyAlignment = { element, key };
    scrollElementTo(element, { top: pending.top, behavior: "instant" });
    this.syncFallbackHeader();
    return true;
  };

  private onPageScroll = (key: string, top: number) => {
    if (this.props.isActive !== false) this.lastRawTops.set(key, top);
  };

  private onPageGeometryChange = (key: string) => {
    if (this.routeTarget?.key === key) this.restoreScrollPosition();
  };

  private onPageUserInput = (key: string) => {
    if (this.routeTarget?.key === key) this.setRouteTarget(null);
  };

  public setPage = (selectedPage: number) => {
    this.selectPage(selectedPage, true);
  };

  public setPageWithoutAnimation = (selectedPage: number) => {
    this.selectPage(selectedPage, false);
  };

  public scrollToPageTop = (options?: Readonly<{ animated?: boolean }>) => {
    this.scrollCurrentPageToTop(0, options?.animated ?? true);
  };

  public scrollHeaderBy = (delta: number) => {
    if (!Number.isFinite(delta)) return;
    const offset = Math.min(
      this.headerHeight,
      Math.max(0, this.sharedHeaderOffset + delta)
    );
    this.scrollCurrentPageToTop(offset, false);
  };

  public scrollHeaderToOffset = (offset: number) => {
    if (!Number.isFinite(offset)) return;
    this.scrollCurrentPageToTop(
      Math.min(this.headerHeight, Math.max(0, offset)),
      false
    );
  };

  public scrollToStickyTop = (options?: Readonly<{ animated?: boolean }>) => {
    this.scrollCurrentPageToTop(
      this.headerHeight,
      options?.animated ?? true
    );
  };

  private cancelPendingTopFrame = () => {
    if (!this.pendingTopFrame) return;
    const pending = this.pendingTopFrame;
    cancelAnimationFrame(pending.frame);
    this.pendingTopFrame = null;
    if (
      this.pageScrollElement(this.state.selectedPage) === pending.element &&
      this.pages().map(pageKey)[this.state.selectedPage] === pending.key
    )
      this.savePageOffset(this.state.selectedPage);
  };

  private onQueuedTopWheel = (event: WheelEvent) => {
    if (event.deltaY !== 0) this.takeOverVerticalScroll();
  };

  private onScrollbarPointerDown = (event: PointerEvent) => {
    const element = this.pageScrollElement(this.state.selectedPage);
    if (!element || event.target !== element || event.button !== 0) return;
    const rect = element.getBoundingClientRect();
    const computed =
      element.ownerDocument.defaultView?.getComputedStyle(element);
    const borderLeft = Number.parseFloat(computed?.borderLeftWidth ?? "0") || 0;
    const borderRight =
      Number.parseFloat(computed?.borderRightWidth ?? "0") || 0;
    const clientStart = rect.left + element.clientLeft;
    const clientEnd = clientStart + element.clientWidth;
    const leftGutter = clientStart - (rect.left + borderLeft);
    const rightGutter = rect.right - borderRight - clientEnd;
    const inVerticalTrack =
      event.clientY >= rect.top + element.clientTop &&
      event.clientY < rect.top + element.clientTop + element.clientHeight;
    const inGutter =
      (leftGutter > 0 &&
        event.clientX >= rect.left + borderLeft &&
        event.clientX < clientStart) ||
      (rightGutter > 0 &&
        event.clientX >= clientEnd &&
        event.clientX < rect.right - borderRight);
    if (inVerticalTrack && inGutter) this.takeOverVerticalScroll();
  };

  private onQueuedTopKey = (event: KeyboardEvent) => {
    if (
      [
        "ArrowUp",
        "ArrowDown",
        "PageUp",
        "PageDown",
        "Home",
        "End",
        " ",
      ].includes(event.key)
    )
      this.takeOverVerticalScroll();
  };

  private scrollCurrentPageToTop(offset: number, animated: boolean) {
    if (this.horizontalTouch || this.transitionTimer !== null) return;
    const element = this.pageScrollElement(this.state.selectedPage);
    const key = this.pages().map(pageKey)[this.state.selectedPage];
    if (!element?.isConnected || !key) return;
    this.setRouteTarget(null);
    this.selectionRestoreToken += 1;
    this.cancelPendingTopFrame();
    const EventConstructor = element.ownerDocument.defaultView?.Event;
    if (EventConstructor)
      element.dispatchEvent(
        new EventConstructor("ok-collapsible-pager-cancel-scroll-command")
      );
    scrollElementTo(element, {
      left: element.scrollLeft,
      top: element.scrollTop,
      behavior: "instant",
    });
    const frame = requestAnimationFrame(() => {
      if (this.pendingTopFrame?.frame !== frame) return;
      this.pendingTopFrame = null;
      if (
        !element.isConnected ||
        this.horizontalTouch ||
        this.transitionTimer !== null ||
        this.pageScrollElement(this.state.selectedPage) !== element ||
        this.pages().map(pageKey)[this.state.selectedPage] !== key
      )
        return;
      this.pageOffsets.set(key, 0);
      this.sharedHeaderOffset = Math.min(this.headerHeight, Math.max(0, offset));
      this.bodyAlignment = { element, key };
      scrollElementTo(element, {
        top: this.sharedHeaderOffset,
        behavior: animated ? "smooth" : "instant",
      });
      this.syncFallbackHeader();
    });
    this.pendingTopFrame = { frame, element, key };
  }

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
    this.transitionGeneration += 1;
    if (this.transitionTimer !== null) {
      clearTimeout(this.transitionTimer);
      this.transitionTimer = null;
    }
    this.readTabProgress();
    this.cancelTabProgressFrame();
    this.syncTabColors();
    this.emitPageScrollState("idle");
    this.syncToolbar();
  };

  private beginPageTransition() {
    this.suspendToolbar();
    if (this.transitionTimer !== null) clearTimeout(this.transitionTimer);
    const generation = ++this.transitionGeneration;
    this.emitPageScrollState("settling");
    this.transitionTimer = setTimeout(() => {
      if (generation === this.transitionGeneration) this.finishPageTransition();
    }, 320);
  }

  private onTrackTransitionEnd = (event: TransitionEvent) => {
    if (event.target === this.track && event.propertyName === "transform") {
      this.readTabProgress();
      if (this.tabColorWeights() && this.tabProgress !== null && Math.abs(this.tabProgress - this.state.selectedPage) > 0.001) return;
      this.finishPageTransition();
    }
  };

  private setTrack = (node: unknown) => {
    const track = webElement(node);
    if (track === this.track) return;
    this.cancelTabProgressFrame();
    this.track?.removeEventListener("transitionend", this.onTrackTransitionEnd);
    this.track = track;
    this.track?.addEventListener("transitionend", this.onTrackTransitionEnd);
  };

  private cancelTabProgressFrame() {
    this.tabProgressGeneration += 1;
    if (this.tabProgressFrame !== null) cancelAnimationFrame(this.tabProgressFrame);
    this.tabProgressFrame = null;
  }

  private readTabProgress() {
    const track = this.track;
    const width = this.root?.getBoundingClientRect().width ?? 0;
    if (!track?.isConnected || width <= 0) return null;
    const transform = track.ownerDocument.defaultView?.getComputedStyle(track).transform;
    const matrix = transform?.match(/^matrix(3d)?\(([^)]+)\)$/);
    const offset = matrix ? Number(matrix[2].split(",")[matrix[1] ? 12 : 4]) : transform === "none" ? 0 : NaN;
    if (!Number.isFinite(offset)) return null;
    this.tabProgress = Math.min(Math.max(0, -offset / width), Math.max(0, this.pages().length - 1));
    return this.tabProgress;
  }

  private startTabProgressFrames() {
    this.cancelTabProgressFrame();
    if (!this.tabColorWeights() || typeof requestAnimationFrame !== "function") return;
    const track = this.track;
    const root = this.root;
    const width = root?.getBoundingClientRect().width ?? 0;
    if (!track?.isConnected || !root?.isConnected || width <= 0 || this.props.isActive === false) return;
    const generation = this.tabProgressGeneration;
    const selection = this.selectionRestoreToken;
    const keys = JSON.stringify(this.pages().map(pageKey));
    const owner = this.pageScrollElement(this.state.selectedPage);
    const sample = () => {
      if (generation !== this.tabProgressGeneration || this.disposed) return;
      this.tabProgressFrame = null;
      if (this.track !== track || this.root !== root || !track.isConnected || !root.isConnected ||
          this.props.isActive === false || selection !== this.selectionRestoreToken ||
          keys !== JSON.stringify(this.pages().map(pageKey)) || owner !== this.pageScrollElement(this.state.selectedPage) ||
          width !== root.getBoundingClientRect().width || !this.tabColorWeights()) {
        this.cancelTabProgressFrame();
        this.readTabProgress();
        this.syncTabColors();
        if (this.transitionTimer !== null) this.startTabProgressFrames();
        return;
      }
      this.readTabProgress();
      this.syncTabColors();
      if (this.transitionTimer !== null) this.tabProgressFrame = requestAnimationFrame(sample);
    };
    sample();
  }

  private tabColorWeights() {
    const config = this.props.nativeTabBar;
    if (!config?.controlled || config.style?.variant !== "text" || this.tabProgress === null) return null;
    const keys: string[] = [];
    React.Children.forEach(this.props.children, child => {
      if (child !== null && child !== undefined && typeof child !== "boolean") keys.push(pageKey(child, keys.length));
    });
    const itemKeys = config.items.map(item => item.key);
    if (!keys.length || !keys.includes(config.selectedKey ?? "") || new Set(keys).size !== keys.length ||
        new Set(itemKeys).size !== itemKeys.length || keys.some(key => !itemKeys.includes(key))) return null;
    const progress = Math.min(Math.max(0, this.tabProgress), keys.length - 1);
    const source = Math.floor(progress);
    const destination = Math.min(source + 1, keys.length - 1);
    const fraction = progress - source;
    return new Map(keys.map((key, index) => [key, index === source ? 1 - fraction : index === destination ? fraction : 0]));
  }

  private tabFontOwnerKey(weights: Map<string, number> | null) {
    const config = this.props.nativeTabBar;
    const semantic = config?.controlled ? config.selectedKey : config?.items[this.state.selectedPage]?.key;
    if (!weights || this.tabProgress === null) return semantic;
    const keys = Array.from(weights.keys());
    const progress = Math.min(Math.max(0, this.tabProgress), keys.length - 1);
    return keys[Math.floor(progress + 0.5)];
  }

  private tabColor(selected: boolean, weight: number | undefined) {
    const style = this.props.nativeTabBar?.style;
    const active = style?.activeTextColor ?? "#000000";
    const inactive = style?.inactiveTextColor ?? "#666666";
    if (weight === undefined) return String(selected ? active : inactive);
    if (weight <= 0) return String(inactive);
    if (weight >= 1) return String(active);
    const first = processColor(inactive);
    const last = processColor(active);
    if (typeof first !== "number" || typeof last !== "number") return String(selected ? active : inactive);
    const channel = (shift: number) => ((first >>> shift) & 255) * (1 - weight) + ((last >>> shift) & 255) * weight;
    return `rgba(${Math.round(channel(16))}, ${Math.round(channel(8))}, ${Math.round(channel(0))}, ${(channel(24) / 255).toFixed(4)})`;
  }

  private syncTabColors() {
    const weights = this.tabColorWeights();
    const config = this.props.nativeTabBar;
    if (!config) return;
    const selectedKey = config.controlled ? config.selectedKey : config.items[this.state.selectedPage]?.key;
    const fontOwnerKey = this.tabFontOwnerKey(weights);
    for (const [key, button] of this.tabButtons) {
      const fontSelected = key === fontOwnerKey;
      button.style.color = this.tabColor(key === selectedKey, weights ? weights.get(key) ?? 0 : undefined);
      button.style.fontWeight = config.style?.variant === "text" && fontSelected ? "600" : "500";
      const fontFamily = (fontSelected ? config.style?.activeFontFamily ?? config.style?.fontFamily : config.style?.fontFamily) ?? "";
      if (config.controlled && config.style?.variant === "text") {
        button.style.fontFamily = config.style?.fontFamily ?? "";
        const label = button.querySelector<HTMLElement>(".ok-pager-tab-label");
        if (label) label.style.fontFamily = fontFamily;
      } else button.style.fontFamily = fontFamily;
    }
  }

  private pageScrollElement(index: number) {
    const pageHost = this.pageHosts.get(index);
    return (
      pageHost?.querySelector<HTMLElement>("[data-collapsible-pager-scroll]") ??
      pageHost?.querySelector<HTMLElement>(".ok-native-list-viewport") ??
      pageHost
    );
  }

  private setRoot = (node: unknown) => {
    const root = webElement(node);
    if (root === this.root) return;
    this.root?.removeEventListener(
      "ok-collapsible-pager-scroll-key-changed",
      this.onScrollOwnerIdentityChanged
    );
    this.root?.removeEventListener("wheel", this.onOverlayWheel);
    this.root?.removeEventListener("touchstart", this.onOverlayTouchStart);
    this.root?.removeEventListener("touchmove", this.onOverlayTouchMove);
    this.root?.removeEventListener("touchend", this.onOverlayTouchEnd);
    this.root?.removeEventListener("touchcancel", this.onOverlayTouchEnd);
    this.overlayTouch = null;
    if (this.root) this.chromeObserver?.unobserve(this.root);
    this.root = root;
    if (root) this.chromeObserver?.observe(root);
    else this.suspendToolbar();
    root?.addEventListener(
      "ok-collapsible-pager-scroll-key-changed",
      this.onScrollOwnerIdentityChanged
    );
    root?.addEventListener("wheel", this.onOverlayWheel, { passive: false });
    root?.addEventListener("touchstart", this.onOverlayTouchStart, {
      passive: true,
    });
    root?.addEventListener("touchmove", this.onOverlayTouchMove, {
      passive: false,
    });
    root?.addEventListener("touchend", this.onOverlayTouchEnd);
    root?.addEventListener("touchcancel", this.onOverlayTouchEnd);
  };

  private overlayTarget(event: Event) {
    const target = event.target as HTMLElement | null;
    if (
      !target ||
      (!this.headerSlot?.contains(target) &&
        !this.stickySlot?.contains(target)) ||
      target.closest("input,textarea,select,[contenteditable=true]")
    )
      return null;
    return target;
  }

  private nestedScrollConsumes(target: HTMLElement, delta: number) {
    for (
      let node: HTMLElement | null = target;
      node && node !== this.root;
      node = node.parentElement
    ) {
      const style = node.ownerDocument.defaultView?.getComputedStyle(node);
      if (
        /auto|scroll/.test(style?.overflowY ?? "") &&
        node.scrollHeight > node.clientHeight &&
        (delta < 0
          ? node.scrollTop > 0
          : node.scrollTop < node.scrollHeight - node.clientHeight)
      )
        return true;
    }
    return false;
  }

  private scrollOverlayOwner(owner: HTMLElement, delta: number, event: Event) {
    if (!event.cancelable) return;
    const EventConstructor = owner.ownerDocument.defaultView?.Event;
    if (EventConstructor)
      owner.dispatchEvent(
        new EventConstructor("ok-collapsible-pager-user-scroll")
      );
    const top = Math.min(
      Math.max(0, owner.scrollTop + delta),
      owner.scrollHeight - owner.clientHeight
    );
    if (top === owner.scrollTop) return;
    this.takeOverVerticalScroll();
    scrollElementTo(owner, { top, behavior: "instant" });
    event.preventDefault();
  }

  private onOverlayWheel = (event: WheelEvent) => {
    const target = this.overlayTarget(event);
    const owner = this.pageScrollElement(this.state.selectedPage);
    if (
      !target ||
      !owner ||
      event.defaultPrevented ||
      event.ctrlKey ||
      Math.abs(event.deltaX) > Math.abs(event.deltaY)
    )
      return;
    const lineHeight =
      Number.parseFloat(
        target.ownerDocument.defaultView?.getComputedStyle(target).lineHeight ??
          ""
      ) || 16;
    const delta =
      event.deltaY *
      (event.deltaMode === 1
        ? lineHeight
        : event.deltaMode === 2
        ? owner.clientHeight
        : 1);
    if (!this.nestedScrollConsumes(target, delta))
      this.scrollOverlayOwner(owner, delta, event);
  };

  private onOverlayTouchStart = (event: TouchEvent) => {
    const target = this.overlayTarget(event);
    const owner = this.pageScrollElement(this.state.selectedPage);
    const touch = event.touches.length === 1 ? event.touches[0] : null;
    this.overlayTouch =
      target && owner && touch
        ? {
            owner,
            target,
            x: touch.clientX,
            y: touch.clientY,
            lastY: touch.clientY,
          }
        : null;
  };

  private onOverlayTouchMove = (event: TouchEvent) => {
    if (event.defaultPrevented) return;
    const pending = this.overlayTouch;
    const touch = event.touches.length === 1 ? event.touches[0] : null;
    if (
      !pending ||
      !touch ||
      pending.owner !== this.pageScrollElement(this.state.selectedPage)
    ) {
      this.overlayTouch = null;
      return;
    }
    const delta = pending.lastY - touch.clientY;
    pending.lastY = touch.clientY;
    if (
      Math.abs(touch.clientY - pending.y) < 8 ||
      Math.abs(touch.clientX - pending.x) >= Math.abs(touch.clientY - pending.y)
    )
      return;
    if (!this.nestedScrollConsumes(pending.target, delta))
      this.scrollOverlayOwner(pending.owner, delta, event);
  };

  private onOverlayTouchEnd = () => {
    this.overlayTouch = null;
  };

  private synchronizeScrollOwnerIdentity(index: number) {
    const key = this.pages().map(pageKey)[index];
    const element = this.pageScrollElement(index);
    if (!key || !element) return;
    const identity =
      element.getAttribute("data-collapsible-pager-scroll-key") ?? key;
    const previous = this.pageScrollIdentities.get(key);
    this.pageScrollIdentities.set(key, identity);
    this.syncPageOwner(index);
    if (previous === undefined || previous === identity) return;
    if (index === this.state.selectedPage) {
      this.selectionRestoreToken += 1;
      this.cancelPendingTopFrame();
      this.sharedHeaderOffset = 0;
      this.bodyAlignment = null;
      scrollElementTo(element, { top: 0, behavior: "instant" });
    }
    this.pageOffsets.delete(key);
  }

  private onScrollOwnerIdentityChanged = (event: Event) => {
    this.pages().forEach((_, index) => {
      if (event.target === this.pageScrollElement(index))
        this.synchronizeScrollOwnerIdentity(index);
    });
  };

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
    this.cancelPendingTopFrame();
    configured.element.removeEventListener(
      "ok-native-list-scroll-command-accepted",
      this.onListScrollCommandAccepted
    );
    configured.element.removeEventListener("wheel", this.onQueuedTopWheel);
    configured.element.removeEventListener(
      "pointerdown",
      this.onScrollbarPointerDown
    );
    configured.element.removeEventListener("keydown", this.onQueuedTopKey);
    configured.element.removeEventListener(
      "scrollend",
      this.onVerticalScrollSettled
    );
    configured.element.removeEventListener("scroll", this.syncFallbackHeader);
    const style = timelineStyle(configured.element);
    configured.rangeSpacer.remove();
    style.position = configured.position;
    configured.paddingTarget.style.paddingTop = configured.paddingTop;
    style.boxSizing = configured.boxSizing;
    style.scrollTimelineName = configured.scrollTimelineName;
    style.scrollTimelineAxis = configured.scrollTimelineAxis;
    style.setProperty(PAGER_HEADER_INSET, configured.pagerHeaderInset);
    style.setProperty(PAGER_STICKY_INSET, configured.pagerStickyInset);
    configured.element.removeAttribute("data-collapsible-pager-active");
    this.notifyInsetsChanged(configured.element);
    this.configuredScrollElement = null;
    this.syncFallbackHeader();
    if (this.bodyAlignment?.element === configured.element)
      this.bodyAlignment = null;
  }

  private configureActiveScrollElement = () => {
    this.configureFrame = null;
    const element = this.pageScrollElement(this.state.selectedPage);
    if (!element) return;
    this.synchronizeScrollOwnerIdentity(this.state.selectedPage);
    if (this.configuredScrollElement?.element !== element) {
      this.restoreConfiguredScrollElement();
      const style = timelineStyle(element);
      const paddingTarget = element.hasAttribute(
        "data-collapsible-pager-scroll"
      )
        ? webElement(element.firstElementChild) ?? element
        : element;
      const computedPaddingTop =
        element.ownerDocument.defaultView?.getComputedStyle(paddingTarget)
          .paddingTop ||
        paddingTarget.style.paddingTop ||
        "0px";
      const rangeSpacer = element.ownerDocument.createElement("div");
      Object.assign(rangeSpacer.style, {
        position: "absolute",
        top: "0",
        left: "0",
        width: "1px",
        pointerEvents: "none",
        opacity: "0",
      });
      rangeSpacer.setAttribute("aria-hidden", "true");
      const position = style.position;
      if (
        element.ownerDocument.defaultView?.getComputedStyle(element)
          .position === "static"
      )
        style.position = "relative";
      element.appendChild(rangeSpacer);
      this.configuredScrollElement = {
        key: this.pages().map(pageKey)[this.state.selectedPage],
        element,
        paddingTarget,
        rangeSpacer,
        position,
        paddingTop: paddingTarget.style.paddingTop,
        basePaddingTop: computedPaddingTop,
        boxSizing: style.boxSizing,
        scrollTimelineName: style.scrollTimelineName,
        scrollTimelineAxis: style.scrollTimelineAxis,
        pagerHeaderInset: style.getPropertyValue(PAGER_HEADER_INSET),
        pagerStickyInset: style.getPropertyValue(PAGER_STICKY_INSET),
      };
      element.addEventListener("scrollend", this.onVerticalScrollSettled);
      element.addEventListener(
        "ok-native-list-scroll-command-accepted",
        this.onListScrollCommandAccepted
      );
      element.addEventListener("wheel", this.onQueuedTopWheel);
      element.addEventListener("pointerdown", this.onScrollbarPointerDown);
      element.addEventListener("keydown", this.onQueuedTopKey);
      if (!this.cssTimelineSupported)
        element.addEventListener("scroll", this.syncFallbackHeader, {
          passive: true,
        });
    }

    const style = timelineStyle(element);
    const paddingStyle =
      this.configuredScrollElement?.paddingTarget.style ?? style;
    const headerInset = this.headerHeight;
    const stickyInset = this.stickyHeaderHeight;
    if (this.configuredScrollElement)
      this.configuredScrollElement.rangeSpacer.style.height = `calc(100% + ${headerInset}px)`;
    const basePaddingTop =
      this.configuredScrollElement?.basePaddingTop ?? "0px";
    const paddingTop = `calc(${basePaddingTop} + ${
      headerInset + stickyInset
    }px)`;
    const headerInsetValue = `${headerInset}px`;
    const stickyInsetValue = `${stickyInset}px`;
    const insetsChanged =
      paddingStyle.paddingTop !== paddingTop ||
      style.getPropertyValue(PAGER_HEADER_INSET) !== headerInsetValue ||
      style.getPropertyValue(PAGER_STICKY_INSET) !== stickyInsetValue;
    paddingStyle.paddingTop = paddingTop;
    style.boxSizing = "border-box";
    style.scrollTimelineName = this.timelineName;
    style.scrollTimelineAxis = "block";
    style.setProperty(PAGER_HEADER_INSET, headerInsetValue);
    style.setProperty(PAGER_STICKY_INSET, stickyInsetValue);
    element.setAttribute("data-collapsible-pager-active", "true");
    if (insetsChanged) this.notifyInsetsChanged(element);
    this.syncFallbackHeader();
    this.pageBridges.get(this.pages().map(pageKey)[this.state.selectedPage])?.requestMeasure();
    this.restoreScrollPosition();
  };

  private syncFallbackHeader = () => {
    if (this.cssTimelineSupported) return;
    const top = this.configuredScrollElement?.element.scrollTop ?? 0;
    const collapse = Math.min(
      Math.max(top, 0),
      this.headerHeight
    );
    const transform = `translateY(${-collapse}px)`;
    if (this.headerSlot) this.headerSlot.style.transform = transform;
    if (this.stickySlot) this.stickySlot.style.transform = transform;
  };

  private scheduleScrollConfiguration() {
    if (this.disposed || this.configureFrame !== null) return;
    if (typeof requestAnimationFrame === "function") {
      this.configureFrame = requestAnimationFrame(
        this.configureActiveScrollElement
      );
    } else {
      this.configureActiveScrollElement();
    }
  }

  private takeOverVerticalScroll = () => {
    this.setRouteTarget(null);
    this.selectionRestoreToken += 1;
    this.bodyAlignment = null;
    this.cancelPendingTopFrame();
    this.savePageOffset(this.state.selectedPage);
  };

  private onListScrollCommandAccepted = (event: Event) => {
    if (event.target !== this.pageScrollElement(this.state.selectedPage))
      return;
    this.takeOverVerticalScroll();
  };

  private onVerticalScrollSettled = (event?: Event) => {
    if (
      this.pendingTopFrame ||
      (event &&
        event.target !== this.pageScrollElement(this.state.selectedPage))
    )
      return;
    this.savePageOffset(this.state.selectedPage);
    this.emitDiagnostics("scroll-settled");
  };

  private savePageOffset(index: number) {
    const configured =
      index === this.state.selectedPage ? this.configuredScrollElement : null;
    const key = configured?.key ?? this.pages().map(pageKey)[index];
    const scrollElement = configured?.element ?? this.pageScrollElement(index);
    if (configured && !scrollElement?.isConnected) return;
    if (key && scrollElement) {
      if (
        this.bodyAlignment?.key === key &&
        this.bodyAlignment.element === scrollElement
      )
        return;
      const D = this.headerHeight;
      this.pageOffsets.set(key, Math.max(0, scrollElement.scrollTop - D));
      this.sharedHeaderOffset = Math.min(
        Math.max(scrollElement.scrollTop, 0),
        D
      );
    }
  }

  private restorePageOffset(index: number) {
    const key = this.pages().map(pageKey)[index];
    const scrollElement = this.pageScrollElement(index);
    if (!key || !scrollElement) return;
    const D = this.headerHeight;
    const C = Math.min(Math.max(0, this.sharedHeaderOffset), D);
    const expectedQ = C + (C === D ? this.pageOffsets.get(key) ?? 0 : 0);
    this.bodyAlignment = { element: scrollElement, key };
    scrollElement.scrollTop = expectedQ;
    this.syncFallbackHeader();
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
    this.synchronizeScrollOwnerIdentity(this.state.selectedPage);
    const selectionToken = ++this.selectionRestoreToken;
    const expectedKey = this.pages().map(pageKey)[position];
    if (this.routeTarget && this.routeTarget.key !== expectedKey)
      this.setRouteTarget(null);
    this.cancelPendingTopFrame();
    this.savePageOffset(this.state.selectedPage);

    if (animated) this.beginPageTransition();
    else {
      this.finishPageTransition();
      this.tabProgress = position;
    }
    this.setState(
      { selectedPage: position, animateTransition: animated },
      () => {
        if (animated) this.startTabProgressFrames();
        this.scheduleScrollConfiguration();
        const restore = () => {
          if (
            this.selectionRestoreToken !== selectionToken ||
            this.state.selectedPage !== position ||
            this.pages().map(pageKey)[position] !== expectedKey ||
            !this.pageScrollElement(position)?.isConnected
          )
            return;
          this.configureActiveScrollElement();
          if (
            this.selectionRestoreToken !== selectionToken ||
            this.routeTarget?.key === expectedKey
          ) {
            return;
          }
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
      }
    );
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
          this.headerHeight
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
    if (Math.abs(deltaY) >= 8 && Math.abs(deltaY) > Math.abs(deltaX))
      this.takeOverVerticalScroll();
    if (Math.abs(deltaX) >= 8 && Math.abs(deltaX) > Math.abs(deltaY)) {
      this.cancelPendingTopFrame();
      this.horizontalTouch = true;
      this.emitPageScrollState("dragging");
      this.suspendToolbar();
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
    const direction =
      Math.abs(delta) >= 40 && Math.abs(delta) > Math.abs(verticalDelta)
        ? delta < 0
          ? 1
          : -1
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

  private toolbarSlot: HTMLDivElement | null = null;
  private toolbarButton: HTMLButtonElement | null = null;
  private toolbarOwnerKey: string | undefined;
  private toolbarOwnerEntry: CollapsiblePagerToolbarItem | undefined;
  private toolbarIconIdentity = "";
  private toolbarLoadedIcon: SVGSVGElement | null = null;
  private toolbarPendingIdentity = "";
  private toolbarReadyIcon: SVGSVGElement | null = null;
  private toolbarFailedIdentity = "";
  private toolbarAnimation: Animation | null = null;
  private toolbarPresence = false;
  private toolbarPresenceInterrupted = false;
  private toolbarImage: HTMLImageElement | null = null;
  private toolbarGeneration = 0;
  private toolbarTouchKey: string | null | undefined;

  private ownsSettledToolbar() {
    return this.props.nativeTabBar?.controlled === true && this.props.nativeTabBar.toolbar !== undefined;
  }

  private toolbarCanCommit() {
    if (!this.ownsSettledToolbar()) return true;
    if (this.disposed || this.props.isActive === false || this.horizontalTouch ||
        this.transitionTimer !== null || this.pageScrollState !== "idle" || !this.root?.isConnected) return false;
    const actual = this.readTabProgress();
    if (actual === null || Math.abs(actual - this.state.selectedPage) > 0.001) return false;
    const config = this.props.nativeTabBar!;
    const keys: string[] = [];
    React.Children.forEach(this.props.children, child => {
      if (child !== null && child !== undefined && typeof child !== "boolean") keys.push(pageKey(child, keys.length));
    });
    const itemKeys = config.items.map(item => item.key);
    const valid = keys.length > 0 && new Set(keys).size === keys.length &&
      new Set(itemKeys).size === itemKeys.length && keys.every(key => itemKeys.includes(key));
    return !valid || !keys.includes(config.selectedKey ?? "") || keys[this.state.selectedPage] === config.selectedKey;
  }

  private cancelToolbarPending() {
    if (this.toolbarPendingIdentity || this.toolbarImage || this.toolbarReadyIcon) this.toolbarGeneration += 1;
    if (this.toolbarImage) this.toolbarImage.onload = this.toolbarImage.onerror = null;
    this.toolbarImage = null;
    this.toolbarPendingIdentity = "";
    this.toolbarReadyIcon = null;
  }

  private setToolbarInteractive(enabled: boolean) {
    const button = this.toolbarButton;
    if (!button) return;
    if (!enabled) {
      if (button.ownerDocument.activeElement === button) button.blur();
      if (this.toolbarTouchKey !== undefined) this.toolbarTouchKey = null;
    }
    button.disabled = !enabled;
    button.style.pointerEvents = enabled ? "auto" : "none";
    button.tabIndex = enabled ? 0 : -1;
    button.setAttribute("aria-hidden", String(!enabled));
  }

  private suspendToolbar() {
    if (!this.ownsSettledToolbar()) return;
    this.setToolbarInteractive(false);
    this.cancelToolbarPending();
    if (this.toolbarAnimation && this.toolbarButton) {
      const opacity = this.toolbarButton.ownerDocument.defaultView?.getComputedStyle(this.toolbarButton).opacity;
      this.toolbarAnimation.cancel();
      this.toolbarAnimation = null;
      this.toolbarButton.style.opacity = opacity || this.toolbarButton.style.opacity;
      this.toolbarPresenceInterrupted = true;
    }
  }

  private setToolbarSlot = (slot: HTMLDivElement | null) => {
    if (this.toolbarSlot === slot) return;
    this.toolbarAnimation?.cancel();
    this.toolbarAnimation = null;
    this.cancelToolbarPending();
    this.toolbarGeneration += 1;
    this.toolbarButton?.remove();
    this.toolbarButton = null;
    this.toolbarOwnerKey = undefined;
    this.toolbarOwnerEntry = undefined;
    this.toolbarIconIdentity = "";
    this.toolbarLoadedIcon = null;
    this.toolbarFailedIdentity = "";
    this.toolbarPresence = false;
    this.toolbarPresenceInterrupted = false;
    this.toolbarTouchKey = null;
    this.toolbarSlot = slot;
    if (slot) this.syncToolbar();
  };

  private animateToolbar(visible: boolean) {
    const button = this.toolbarButton;
    if (!button || (this.toolbarPresence === visible && !this.toolbarPresenceInterrupted)) return;
    this.toolbarPresenceInterrupted = false;
    this.toolbarPresence = visible;
    const current = button.ownerDocument.defaultView?.getComputedStyle(button).opacity ?? button.style.opacity;
    this.toolbarAnimation?.cancel();
    this.toolbarAnimation = null;
    button.style.opacity = visible ? "1" : "0";
    const finish = () => {
      button.hidden = !visible;
      if (!visible && !this.toolbarOwnerKey && this.toolbarButton === button) {
        button.remove();
        this.toolbarButton = null;
      }
    };
    if (typeof button.animate !== "function") { finish(); return; }
    button.hidden = false;
    const animation = button.animate([{ opacity: current || "0" }, { opacity: visible ? "1" : "0" }], { duration: 100, fill: "forwards" });
    this.toolbarAnimation = animation;
    animation.onfinish = () => {
      if (this.toolbarAnimation !== animation || this.toolbarButton !== button || this.toolbarPresence !== visible) return;
      animation.cancel();
      this.toolbarAnimation = null;
      finish();
    };
  }

  private hideToolbar(failed: boolean) {
    this.setToolbarInteractive(false);
    this.toolbarOwnerKey = undefined;
    this.toolbarOwnerEntry = undefined;
    this.toolbarButton?.removeAttribute("data-testid");
    this.toolbarButton?.removeAttribute("aria-label");
    if (failed) this.toolbarButton?.replaceChildren();
    this.animateToolbar(false);
  }

  private commitToolbar(key: string, entry: CollapsiblePagerToolbarItem, icon: SVGSVGElement, identity: string) {
    const button = this.toolbarButton;
    if (!button || !this.toolbarCanCommit()) return;
    if (this.toolbarOwnerKey !== key && this.toolbarTouchKey !== undefined) this.toolbarTouchKey = null;
    const size = Math.min(128, Math.max(1, entry.size ?? 38));
    const iconSize = Math.min(size, Math.max(1, entry.iconSize ?? 24));
    const contentWidth = Math.min(size, Math.max(0, entry.contentWidth ?? 24));
    const trailingInset = Math.min(128, Math.max(0, entry.trailingInset ?? 20));
    if (button.firstChild !== icon) button.replaceChildren(icon);
    Object.assign(button.style, { position: "absolute", width: `${size}px`, height: `${size}px`, borderRadius: "50%", top: "50%", transform: "translateY(-50%)",
      insetInlineEnd: `${trailingInset - (size - contentWidth) / 2}px`, padding: `${(size - iconSize) / 2}px`, color: entry.iconColor ?? "#000000" });
    const base = entry.selected ? entry.selectedBackgroundColor ?? "transparent" : "transparent";
    button.style.setProperty("--pager-toolbar-base", base);
    button.style.setProperty("--pager-toolbar-hover", entry.hoverBackgroundColor ?? base);
    button.style.setProperty("--pager-toolbar-pressed", entry.pressedBackgroundColor ?? base);
    button.setAttribute("aria-label", entry.accessibilityLabel ?? "");
    if (entry.testID) button.setAttribute("data-testid", entry.testID); else button.removeAttribute("data-testid");
    this.toolbarOwnerKey = key;
    this.toolbarOwnerEntry = { ...entry };
    this.toolbarIconIdentity = identity;
    this.toolbarLoadedIcon = icon;
    this.toolbarPendingIdentity = "";
    this.toolbarReadyIcon = null;
    this.toolbarImage = null;
    this.animateToolbar(true);
    this.setToolbarInteractive(true);
  }

  private syncToolbar() {
    const slot = this.toolbarSlot;
    if (!slot) return;
    if (!this.toolbarCanCommit()) { this.suspendToolbar(); return; }
    const config = this.props.nativeTabBar;
    const key = config?.controlled ? config.selectedKey : config?.items[this.state.selectedPage]?.key;
    const entry = key ? config?.toolbar?.[key] : undefined;
    if (!entry?.iconName || !key) {
      this.cancelToolbarPending();
      this.hideToolbar(false);
      return;
    }
    let button = this.toolbarButton;
    if (!button) {
      button = slot.ownerDocument.createElement("button");
      button.type = "button";
      button.className = "ok-pager-toolbar";
      button.style.opacity = "0";
      button.hidden = true;
      button.disabled = true;
      button.addEventListener("pointerdown", () => { this.toolbarTouchKey = button!.disabled ? null : this.toolbarOwnerKey ?? null; });
      button.addEventListener("pointercancel", () => { this.toolbarTouchKey = null; });
      button.onclick = event => {
        if (this.toolbarButton !== event.currentTarget) return;
        const pointer = event.detail !== 0;
        const touched = this.toolbarTouchKey;
        if (pointer) this.toolbarTouchKey = undefined;
        const current = this.props.nativeTabBar;
        const semantic = current?.controlled ? current.selectedKey : current?.items[this.state.selectedPage]?.key;
        const owner = this.toolbarOwnerKey;
        if (button!.disabled || !this.toolbarCanCommit() || !owner || owner !== semantic ||
            !this.toolbarOwnerEntry || (pointer && touched !== owner)) return;
        const rect = button!.getBoundingClientRect();
        this.props.onNativeToolbarPress?.({ nativeEvent: { key: owner,
          anchorRect: { x: rect.x, y: rect.y, width: rect.width, height: rect.height } } } as ReactNative.NativeSyntheticEvent<CollapsiblePagerToolbarPress>);
      };
      slot.appendChild(button);
      this.toolbarButton = button;
      this.toolbarPresence = false;
    }
    const size = Math.min(128, Math.max(1, entry.size ?? 38));
    const iconSize = Math.min(size, Math.max(1, entry.iconSize ?? 24));
    const identity = `${entry.iconName}|${entry.iconColor ?? "#000000"}|${iconSize}`;
    if (identity === this.toolbarIconIdentity && this.toolbarLoadedIcon) {
      this.cancelToolbarPending();
      this.commitToolbar(key, entry, this.toolbarLoadedIcon, identity);
      return;
    }
    if (identity === this.toolbarPendingIdentity) {
      if (this.toolbarReadyIcon) this.commitToolbar(key, entry, this.toolbarReadyIcon, identity);
      return;
    }
    this.cancelToolbarPending();
    this.setToolbarInteractive(false);
    this.toolbarPendingIdentity = identity;
    if (identity === this.toolbarFailedIdentity) { this.hideToolbar(true); return; }
    const generation = ++this.toolbarGeneration;
    const icon = createNamedIcon(button.ownerDocument, entry.iconName);
    icon.setAttribute("width", String(iconSize)); icon.setAttribute("height", String(iconSize));
    const use = icon.querySelector("use");
    const fail = () => {
      if (this.toolbarGeneration !== generation || this.toolbarPendingIdentity !== identity) return;
      this.toolbarImage = null;
      this.toolbarFailedIdentity = identity;
      this.hideToolbar(true);
    };
    if (!use) { fail(); return; }
    const ready = () => {
      if (this.disposed || this.toolbarGeneration !== generation || this.toolbarPendingIdentity !== identity) return;
      this.toolbarImage = null;
      this.toolbarReadyIcon = icon;
      this.syncToolbar();
    };
    const href = use.getAttribute("href") ?? use.getAttribute("xlink:href") ?? "";
    const view = button.ownerDocument.defaultView;
    if (view?.Image && href.split("#")[0]) {
      const image = new view.Image();
      this.toolbarImage = image;
      image.onload = ready;
      image.onerror = fail;
      image.src = href.split("#")[0];
    } else ready();
  }

  private renderNativeTabBar() {
    const config = this.props.nativeTabBar;
    if (!config?.items.length || (config.style?.variant !== "text" && config.controlled !== true && config.toolbar === undefined)) return null;
    const style = config.style;
    const variant = style?.variant ?? "underline";
    const selectedKey = config.controlled ? config.selectedKey : config.items[this.state.selectedPage]?.key;
    const toolbarEntries = config.toolbar === undefined ? undefined : Object.values(config.toolbar);
    const reservedWidth = toolbarEntries === undefined ? 0 : toolbarEntries.length === 0 ? 44 : Math.max(...toolbarEntries.map(entry => {
      const size = Math.min(128, Math.max(1, entry.size ?? 38));
      return Math.min(size, Math.max(0, entry.contentWidth ?? 24)) + Math.min(128, Math.max(0, entry.trailingInset ?? 20));
    }));
    const segmented = variant === "segmented";
    const reserveTextFaces = config.controlled && variant === "text";
    const colorWeights = this.tabColorWeights();
    const fontOwnerKey = this.tabFontOwnerKey(colorWeights);
    return React.createElement("div", {
      className: "ok-pager-tab-bar",
      dir: this.props.layoutDirection === "rtl" || ((this.props.layoutDirection ?? "locale") === "locale" && I18nManager.isRTL) ? "rtl" : "ltr",
      style: { display: "flex", alignItems: "center", position: "relative", height: style?.height ?? 44,
        backgroundColor: String(style?.backgroundColor ?? "transparent"), minWidth: 0 },
    },
      React.createElement("style", null, `.ok-pager-tab{box-sizing:border-box;appearance:none;border:0;background:transparent;cursor:default;white-space:nowrap;flex-shrink:0;line-height:normal}.ok-pager-toolbar{box-sizing:border-box;appearance:none;border:1px solid transparent;cursor:default;display:flex;align-items:center;justify-content:center;background:var(--pager-toolbar-base)}.ok-pager-toolbar:hover:enabled{background:var(--pager-toolbar-hover)}.ok-pager-toolbar:active:enabled{background:var(--pager-toolbar-pressed)}`),
      React.createElement("div", { role: "tablist", style: { display: "flex", overflowX: segmented ? "hidden" : "auto", flex: 1,
        padding: `${style?.contentPaddingVertical ?? 0}px ${style?.contentPaddingHorizontal ?? 20}px`, gap: style?.itemSpacing ?? 8 } },
        ...config.items.map((item, position) => {
          const selected = item.key === selectedKey;
          const fontSelected = item.key === fontOwnerKey;
          return React.createElement("button", {
            key: item.key, role: "tab", type: "button", className: "ok-pager-tab", "aria-selected": selected,
            ref: (button: HTMLButtonElement | null) => { if (button) this.tabButtons.set(item.key, button); else this.tabButtons.delete(item.key); },
            "aria-label": item.accessibilityLabel ?? item.title, "data-testid": item.testID,
            style: { padding: variant === "text" ? "6px 10px" : "6px 8px", minWidth: 44, height: (style?.height ?? 44) - 2 * (style?.contentPaddingVertical ?? 0),
              fontSize: style?.fontSize ?? (variant === "text" ? 18 : 16),
              fontWeight: variant === "text" && fontSelected ? 600 : 500,
              fontFamily: reserveTextFaces ? style?.fontFamily : fontSelected ? style?.activeFontFamily ?? style?.fontFamily : style?.fontFamily,
              display: reserveTextFaces ? "inline-grid" : undefined,
              gridTemplateAreas: reserveTextFaces ? '"label"' : undefined,
              alignItems: reserveTextFaces ? "center" : undefined,
              color: this.tabColor(selected, colorWeights ? colorWeights.get(item.key) ?? 0 : undefined),
              flex: segmented ? 1 : undefined, borderRadius: segmented ? 999 : 0,
              backgroundColor: segmented && selected ? String(style?.indicatorColor ?? "transparent") : "transparent",
              borderBottom: variant === "underline" && selected ? `${style?.indicatorHeight ?? 2}px solid ${String(style?.indicatorColor ?? "#000000")}` : undefined },
            onClick: () => {
              if (!config.controlled) {
                const animated = this.props.nativeTabPressAnimationEnabled ?? Math.abs(position - this.state.selectedPage) <= 1;
                if (animated) this.setPage(position); else this.setPageWithoutAnimation(position);
              }
              this.props.onNativeTabPress?.({ nativeEvent: { position, key: item.key } } as CollapsiblePagerViewOnNativeTabPressEvent);
            },
          }, reserveTextFaces ? [
            React.createElement("span", { key: "label", className: "ok-pager-tab-label", style: {
              gridArea: "label", fontFamily: fontSelected ? style?.activeFontFamily ?? style?.fontFamily : style?.fontFamily,
            } }, item.title),
            ...[false, true].map(active => React.createElement("span", {
              key: active ? "active-width" : "normal-width", "aria-hidden": true,
              style: { gridArea: "label", visibility: "hidden", pointerEvents: "none", userSelect: "none",
                fontWeight: active ? 600 : 500,
                fontFamily: active ? style?.activeFontFamily ?? style?.fontFamily : style?.fontFamily },
            }, item.title)),
          ] : item.title);
        })),
      config.toolbar !== undefined ? React.createElement("div", { ref: this.setToolbarSlot, className: "ok-pager-toolbar-slot", style: { flexShrink: 0, width: reservedWidth, height: "100%", position: "relative" } }) : null);
  }

  render() {
    const {
      children: _children,
      nativeTabBar: _nativeTabBar,
      nativeTabPressAnimationEnabled: _nativeTabPressAnimationEnabled,
      onNativeTabPress: _onNativeTabPress,
      onNativeToolbarPress: _onNativeToolbarPress,
      header,
      stickyHeader,
      topOverlay: _topOverlay,
      topScrollEdgeEffect: _topScrollEdgeEffect,
      topOverlayColorScheme: _topOverlayColorScheme,
      externalTopOverlayTag: _externalTopOverlayTag,
      headerHeight: _headerHeight,
      stickyHeaderHeight: _stickyHeaderHeight,
      isActive: _isActive,
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
    const collapseAnimation = this.cssTimelineSupported
      ? ({
          "--ok-collapsible-header-height": `${Math.max(
            0,
            this.headerHeight
          )}px`,
          animationName: COLLAPSE_ANIMATION_NAME,
          animationDuration: "auto",
          animationFillMode: "both",
          animationTimingFunction: "linear",
          animationTimeline: this.timelineName,
          animationRange: `0px ${Math.max(1, this.headerHeight)}px`,
        } as never)
      : undefined;
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
        ref={this.setRoot}
        style={[
          styles.root,
          this.cssTimelineSupported
            ? ({ timelineScope: this.timelineName } as never)
            : undefined,
          style,
        ]}
      >
        <View
          ref={(node) => {
            this.headerSlot = webElement(node);
          }}
          style={[
            styles.header,
            { height: this.headerHeight },
            collapseAnimation,
          ]}
        >
          {this.hasExplicitHeaderHeight ? header : (
            <View ref={this.setHeaderContent}>{header}</View>
          )}
        </View>
        <View
          ref={(node) => {
            this.stickySlot = webElement(node);
          }}
          style={[
            styles.sticky,
            {
              top: this.headerHeight,
              height: this.stickyHeaderHeight,
            },
            collapseAnimation,
          ]}
        >
          {this.hasExplicitStickyHeaderHeight ? <>{this.renderNativeTabBar()}{stickyHeader}</> : (
            <View ref={this.setStickyContent}>{this.renderNativeTabBar()}{stickyHeader}</View>
          )}
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
              {retained.has(index) ? (
                <WebCollapsiblePagerPage
                  pageKey={keys[index]}
                  isFocused={index === this.state.selectedPage}
                  isRouteActive={this.props.isActive !== false}
                  headerHeight={this.headerHeight}
                  stickyHeaderHeight={this.stickyHeaderHeight}
                  onBridge={this.onPageBridge}
                  onGeometryChange={this.onPageGeometryChange}
                  onScroll={this.onPageScroll}
                  onUserInput={this.onPageUserInput}
                >
                  {page}
                </WebCollapsiblePagerPage>
              ) : null}
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
    overflow: "clip",
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
    overflow: "visible",
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
