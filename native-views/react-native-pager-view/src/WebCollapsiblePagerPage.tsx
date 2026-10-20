import React from "react";

export type WebCollapsiblePagerPageState = Readonly<{
  isFocused: boolean;
  isRouteActive: boolean;
  owner: HTMLElement | null;
  content: HTMLElement | null;
  contentHeight?: number;
  width: number;
  height: number;
  scrollTop: number;
  pendingRouteScrollTop: number | null;
  registerContent: (element: HTMLElement, explicitHeight?: number) => () => void;
}>;

export type WebCollapsiblePagerPageBridge = Readonly<{
  setOwner: (owner: HTMLElement | null) => void;
  requestMeasure: () => void;
  setPendingRouteScrollTop: (top: number | null) => void;
}>;

const PageContext = React.createContext<WebCollapsiblePagerPageState | null>(null);

export function useWebCollapsiblePagerPage() {
  return React.useContext(PageContext);
}

export function WebCollapsiblePagerPage({
  children,
  pageKey,
  isFocused,
  isRouteActive,
  headerHeight,
  stickyHeaderHeight,
  onBridge,
  onGeometryChange,
  onScroll,
  onUserInput,
}: React.PropsWithChildren<{
  pageKey: string;
  isFocused: boolean;
  isRouteActive: boolean;
  headerHeight: number;
  stickyHeaderHeight: number;
  onBridge: (key: string, bridge: WebCollapsiblePagerPageBridge | null) => void;
  onGeometryChange: (key: string) => void;
  onScroll: (key: string, top: number) => void;
  onUserInput: (key: string) => void;
}>) {
  const [owner, setOwner] = React.useState<HTMLElement | null>(null);
  const ownerRef = React.useRef<HTMLElement | null>(null);
  const [registered, setRegistered] = React.useState<{
    element: HTMLElement;
    height?: number;
  } | null>(null);
  const registeredRef = React.useRef(registered);
  const observerRef = React.useRef<ResizeObserver | null>(null);
  const observedContentRef = React.useRef<HTMLElement | null>(null);
  const frameRef = React.useRef<number | null>(null);
  const disposedRef = React.useRef(false);
  const [pendingRouteScrollTop, setPendingRouteScrollTop] = React.useState<
    number | null
  >(null);
  const [metrics, setMetrics] = React.useState({
    width: 0,
    height: 0,
    scrollTop: 0,
  });
  const inputsRef = React.useRef({
    pageKey,
    headerHeight,
    stickyHeaderHeight,
    onGeometryChange,
    onScroll,
    onUserInput,
  });
  inputsRef.current = {
    pageKey,
    headerHeight,
    stickyHeaderHeight,
    onGeometryChange,
    onScroll,
    onUserInput,
  };

  const measure = React.useCallback(() => {
    frameRef.current = null;
    if (disposedRef.current) return;
    const current = ownerRef.current;
    if (!current?.isConnected) return;
    const ownerRect = current.getBoundingClientRect();
    const candidate = registeredRef.current?.element;
    const content = candidate?.isConnected && current.contains(candidate) ? candidate : null;
    const contentRect = (content ?? current.firstElementChild)?.getBoundingClientRect();
    const inputs = inputsRef.current;
    const pinnedBottom =
      ownerRect.top +
      Math.max(0, inputs.headerHeight - current.scrollTop) +
      inputs.stickyHeaderHeight;
    const visibleTop = Math.max(
      ownerRect.top,
      pinnedBottom,
      contentRect?.top ?? 0,
    );
    const next = {
      width: current.clientWidth,
      height: Math.max(0, ownerRect.bottom - visibleTop),
      scrollTop: Math.max(0, pinnedBottom - (contentRect?.top ?? pinnedBottom)),
    };
    setMetrics((previous) =>
      previous.width === next.width &&
      previous.height === next.height &&
      previous.scrollTop === next.scrollTop
        ? previous
        : next,
    );
    inputs.onGeometryChange(inputs.pageKey);
  }, []);

  const requestMeasure = React.useCallback(() => {
    if (!disposedRef.current && frameRef.current === null)
      frameRef.current = requestAnimationFrame(measure);
  }, [measure]);

  const syncContentObservation = React.useCallback(() => {
    const observer = observerRef.current;
    const current = ownerRef.current;
    if (!observer || !current) return;
    const candidate = registeredRef.current?.element;
    const content = candidate?.isConnected && current.contains(candidate)
      ? candidate
      : current.firstElementChild;
    const next =
      typeof HTMLElement !== "undefined" && content instanceof HTMLElement
        ? content
        : null;
    if (observedContentRef.current === next) return;
    if (observedContentRef.current) observer.unobserve(observedContentRef.current);
    observedContentRef.current = next;
    if (next) observer.observe(next);
  }, []);

  const registerContent = React.useCallback<
    WebCollapsiblePagerPageState["registerContent"]
  >(
    (element, height) => {
      const entry = {
        element,
        height: height !== undefined && Number.isFinite(height) && height >= 0
          ? height
          : undefined,
      };
      registeredRef.current = entry;
      setRegistered(entry);
      syncContentObservation();
      requestMeasure();
      return () => {
        if (registeredRef.current !== entry) return;
        registeredRef.current = null;
        setRegistered(null);
        syncContentObservation();
        requestMeasure();
      };
    },
    [requestMeasure, syncContentObservation],
  );

  const bridge = React.useMemo<WebCollapsiblePagerPageBridge>(
    () => ({
      setOwner: (next) => {
        if (ownerRef.current === next) return;
        ownerRef.current = next;
        setOwner(next);
        requestMeasure();
      },
      requestMeasure,
      setPendingRouteScrollTop,
    }),
    [requestMeasure],
  );

  React.useLayoutEffect(() => {
    disposedRef.current = false;
    return () => {
      disposedRef.current = true;
      if (frameRef.current !== null) cancelAnimationFrame(frameRef.current);
      frameRef.current = null;
    };
  }, []);

  React.useLayoutEffect(() => {
    onBridge(pageKey, bridge);
    return () => onBridge(pageKey, null);
  }, [bridge, onBridge, pageKey]);

  React.useLayoutEffect(() => {
    if (!owner) return undefined;
    const observer = new ResizeObserver(requestMeasure);
    observerRef.current = observer;
    observer.observe(owner);
    syncContentObservation();
    const handleScroll = () => {
      if (ownerRef.current !== owner) return;
      const inputs = inputsRef.current;
      inputs.onScroll(inputs.pageKey, owner.scrollTop);
      requestMeasure();
    };
    const handleKey = (event: KeyboardEvent) => {
      if (
        ["ArrowUp", "ArrowDown", "PageUp", "PageDown", "Home", "End", " "].includes(
          event.key,
        )
      )
        inputsRef.current.onUserInput(inputsRef.current.pageKey);
    };
    const handleUserInput = () =>
      inputsRef.current.onUserInput(inputsRef.current.pageKey);
    owner.addEventListener("scroll", handleScroll, { passive: true });
    owner.addEventListener("touchstart", handleUserInput, { passive: true });
    owner.addEventListener("pointerdown", handleUserInput);
    owner.addEventListener("keydown", handleKey);
    owner.addEventListener("ok-collapsible-pager-user-scroll", handleUserInput);
    owner.addEventListener("ok-collapsible-pager-insets-changed", requestMeasure);
    requestMeasure();
    return () => {
      observer.disconnect();
      observerRef.current = null;
      observedContentRef.current = null;
      owner.removeEventListener("scroll", handleScroll);
      owner.removeEventListener("touchstart", handleUserInput);
      owner.removeEventListener("pointerdown", handleUserInput);
      owner.removeEventListener("keydown", handleKey);
      owner.removeEventListener("ok-collapsible-pager-user-scroll", handleUserInput);
      owner.removeEventListener("ok-collapsible-pager-insets-changed", requestMeasure);
      if (frameRef.current !== null) cancelAnimationFrame(frameRef.current);
      frameRef.current = null;
    };
  }, [owner, requestMeasure, syncContentObservation]);

  React.useLayoutEffect(requestMeasure, [isFocused, isRouteActive, headerHeight, stickyHeaderHeight, requestMeasure]);

  const value = React.useMemo<WebCollapsiblePagerPageState>(
    () => ({
      isFocused,
      isRouteActive,
      owner,
      content: registered?.element.isConnected && owner?.contains(registered.element)
        ? registered.element
        : null,
      contentHeight: registered?.height,
      ...metrics,
      pendingRouteScrollTop,
      registerContent,
    }),
    [isFocused, isRouteActive, owner, registered, metrics, pendingRouteScrollTop, registerContent],
  );
  return <PageContext.Provider value={value}>{children}</PageContext.Provider>;
}
