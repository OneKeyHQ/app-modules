import type { ScrollEdgeEffect } from "./ScrollEdgeEffect";
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
  private pageScrollState: OnPageScrollStateChangedEventData["pageScrollState"] =
    "idle";
  private configuredScrollElement: ConfiguredScrollElement | null = null;
  private readonly timelineName = `--ok-collapsible-pager-${++collapsiblePagerWebInstance}`;

  componentDidMount() {
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
    for (const key of this.pageScrollIdentities.keys()) {
      if (!currentKeys.has(key)) {
        this.pageScrollIdentities.delete(key);
        this.pageOffsets.delete(key);
      }
    }
    this.scheduleScrollConfiguration();
    this.syncFallbackHeader();
    this.notifyMountedPagesChanged();
  }

  componentWillUnmount() {
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
    this.restoreConfiguredScrollElement();
  }

  public setPage = (selectedPage: number) => {
    this.selectPage(selectedPage, true);
  };

  public setPageWithoutAnimation = (selectedPage: number) => {
    this.selectPage(selectedPage, false);
  };

  public scrollToPageTop = (options?: Readonly<{ animated?: boolean }>) => {
    this.scrollCurrentPageToTop(0, options?.animated ?? true);
  };

  public scrollToStickyTop = (options?: Readonly<{ animated?: boolean }>) => {
    this.scrollCurrentPageToTop(
      Math.max(0, this.props.headerHeight),
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
      this.sharedHeaderOffset = offset;
      this.bodyAlignment = { element, key };
      scrollElementTo(element, {
        top: offset,
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
    this.track?.removeEventListener("transitionend", this.onTrackTransitionEnd);
    this.track = track;
    this.track?.addEventListener("transitionend", this.onTrackTransitionEnd);
  };

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
    this.root = root;
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
    const headerInset = Math.max(0, this.props.headerHeight);
    const stickyInset = Math.max(0, this.props.stickyHeaderHeight);
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
  };

  private syncFallbackHeader = () => {
    if (this.cssTimelineSupported) return;
    const top = this.configuredScrollElement?.element.scrollTop ?? 0;
    const collapse = Math.min(
      Math.max(top, 0),
      Math.max(this.props.headerHeight, 0)
    );
    const transform = `translateY(${-collapse}px)`;
    if (this.headerSlot) this.headerSlot.style.transform = transform;
    if (this.stickySlot) this.stickySlot.style.transform = transform;
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

  private takeOverVerticalScroll = () => {
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
      const D = Math.max(0, this.props.headerHeight);
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
    const D = Math.max(0, this.props.headerHeight);
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
    this.cancelPendingTopFrame();
    this.savePageOffset(this.state.selectedPage);

    if (animated) this.beginPageTransition();
    else this.finishPageTransition();
    this.setState(
      { selectedPage: position, animateTransition: animated },
      () => {
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
    if (Math.abs(deltaY) >= 8 && Math.abs(deltaY) > Math.abs(deltaX))
      this.takeOverVerticalScroll();
    if (Math.abs(deltaX) >= 8 && Math.abs(deltaX) > Math.abs(deltaY)) {
      this.cancelPendingTopFrame();
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

  render() {
    const {
      children: _children,
      header,
      stickyHeader,
      topOverlay: _topOverlay,
      topScrollEdgeEffect: _topScrollEdgeEffect,
      topOverlayColorScheme: _topOverlayColorScheme,
      externalTopOverlayTag: _externalTopOverlayTag,
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
    const collapseAnimation = this.cssTimelineSupported
      ? ({
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
            { height: Math.max(0, this.props.headerHeight) },
            collapseAnimation,
          ]}
        >
          {header}
        </View>
        <View
          ref={(node) => {
            this.stickySlot = webElement(node);
          }}
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
