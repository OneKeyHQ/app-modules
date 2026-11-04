import React, { act } from 'react';
import { createRoot } from 'react-dom/client';

import {
  CollapsiblePagerView,
} from '../../../native-views/react-native-pager-view/src/CollapsiblePagerView.web';
import { useWebCollapsiblePagerPage } from '../../../native-views/react-native-pager-view/src/WebCollapsiblePagerPage';
import { NativeList as WebNativeList } from '../../../native-views/react-native-native-list/src/NativeList.web';
import type { NativeListSnapshot } from '../../../native-views/react-native-native-list/src/models';

import type { WebCollapsiblePagerPageState } from '../../../native-views/react-native-pager-view/src/WebCollapsiblePagerPage';

jest.mock('../../../native-views/react-native-native-list/src/web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(),
  canonicalNativeListAvatarUri: jest.fn(),
}));

jest.mock('react-native', () => {
  const react = require('react') as typeof React;
  return {
    Platform: { OS: 'web' },
    I18nManager: { isRTL: false },
    processColor: jest.requireActual('react-native').processColor,
    StyleSheet: { create: (styles: unknown) => styles },
    View: react.forwardRef(function MockView(
      { children, style: _style, testID, ...props }: React.PropsWithChildren<{
        style?: unknown;
        testID?: string;
      }>,
      ref: React.Ref<HTMLDivElement>,
    ) {
      const merged = Object.assign({}, ...(Array.isArray(_style) ? _style : [_style]).filter(value => value && typeof value === 'object')) as {
        transitionProperty?: string; width?: string; transform?: Array<{ translateX?: string }>;
      };
      const trackStyle = merged.transitionProperty === 'transform' ? {
        transform: `matrix(1, 0, 0, 1, ${Number.parseFloat(merged.transform?.[0]?.translateX ?? '0') / 100 * Number.parseFloat(merged.width ?? '100') / 100 * 390}, 0)`,
      } : undefined;
      return <div {...props} style={trackStyle} data-testid={testID} ref={ref}>{children}</div>;
    }),
  };
});

const { JSDOM } = require('jsdom') as {
  JSDOM: new (html: string, options: { url: string }) => { window: Window & typeof globalThis };
};

let completeArtwork = true;
let artworkRequests: Array<{ onload: (() => void) | null; onerror: (() => void) | null }> = [];

let snapshots: Array<{ key: string; state: WebCollapsiblePagerPageState }> = [];
function latestSnapshot(key = 'A') { return [...snapshots].reverse().find((item) => item.key === key)?.state; }
function SnapshotProbe({ pageKey }: { pageKey: string }) {
  const snapshot = useWebCollapsiblePagerPage();
  if (snapshot) snapshots.push({ key: pageKey, state: snapshot });
  return null;
}

const originalDocument = Object.getOwnPropertyDescriptor(globalThis, 'document');
const originalWindow = Object.getOwnPropertyDescriptor(globalThis, 'window');
const originalHTMLElement = Object.getOwnPropertyDescriptor(globalThis, 'HTMLElement');
const originalResizeObserver = globalThis.ResizeObserver;
const originalRequestAnimationFrame = globalThis.requestAnimationFrame;
const originalCancelAnimationFrame = globalThis.cancelAnimationFrame;
const originalActEnvironment = (globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }).IS_REACT_ACT_ENVIRONMENT;

let frames: Map<number, FrameRequestCallback>;
let nextFrame: number;
let resizeCallbacks: ResizeObserverCallback[];
let observedElements: Element[];
let container: HTMLDivElement;
let root: ReturnType<typeof createRoot>;

async function flushFrames() {
  await act(async () => {
    for (let turn = 0; turn < 5 && frames.size; turn += 1) {
      const callbacks = [...frames.values()];
      frames.clear();
      callbacks.forEach((callback) => callback(0));
    }
  });
}

beforeEach(() => {
  const dom = new JSDOM('<!doctype html><html><body><div id="root"></div></body></html>', { url: 'http://localhost/' });
  completeArtwork = true;
  artworkRequests = [];
  Object.defineProperty(dom.window, 'Image', { configurable: true, value: class {
    onload: (() => void) | null = null;
    onerror: (() => void) | null = null;
    set src(_value: string) {
      artworkRequests.push(this);
      if (completeArtwork) queueMicrotask(() => this.onload?.());
    }
  } });
  Object.defineProperty(dom.window.Element.prototype, 'scrollTo', {
    configurable: true,
    value(this: HTMLElement, options: ScrollToOptions) {
      if (options.top !== undefined) this.scrollTop = options.top;
    },
  });
  Object.defineProperty(globalThis, 'window', { configurable: true, value: dom.window });
  Object.defineProperty(globalThis, 'document', { configurable: true, value: dom.window.document });
  Object.defineProperty(globalThis, 'HTMLElement', { configurable: true, value: dom.window.HTMLElement });
  (globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }).IS_REACT_ACT_ENVIRONMENT = true;
  frames = new Map();
  nextFrame = 1;
  resizeCallbacks = [];
  observedElements = [];
  globalThis.requestAnimationFrame = (callback) => {
    const id = nextFrame++;
    frames.set(id, callback);
    return id;
  };
  globalThis.cancelAnimationFrame = (id) => { frames.delete(id); };
  globalThis.ResizeObserver = class {
    constructor(callback: ResizeObserverCallback) { resizeCallbacks.push(callback); }
    observe(element: Element) { observedElements.push(element); }
    unobserve() {}
    disconnect() {}
  };
  snapshots = [];
  container = dom.window.document.getElementById('root') as HTMLDivElement;
  root = createRoot(container);
});

afterEach(async () => {
  await act(async () => root.unmount());
  for (const [key, descriptor] of [
    ['window', originalWindow],
    ['document', originalDocument],
    ['HTMLElement', originalHTMLElement],
  ] as const) {
    if (descriptor) Object.defineProperty(globalThis, key, descriptor);
    else Reflect.deleteProperty(globalThis, key);
  }
  globalThis.ResizeObserver = originalResizeObserver;
  globalThis.requestAnimationFrame = originalRequestAnimationFrame;
  globalThis.cancelAnimationFrame = originalCancelAnimationFrame;
  (globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }).IS_REACT_ACT_ENVIRONMENT = originalActEnvironment;
});

function makePage(key = 'A') {
  return <div key={key}><div data-collapsible-pager-scroll="true" data-collapsible-pager-scroll-key={key}>
    <div data-testid="content"><SnapshotProbe pageKey={key} /></div>
  </div></div>;
}

function ReorderingPager({ keys, active }: { keys: string[]; active: boolean }) {
  const pager = React.useRef<CollapsiblePagerView>(null);
  const selectedIndex = keys.indexOf('A');
  React.useLayoutEffect(() => {
    pager.current?.setPageWithoutAnimation(selectedIndex);
  }, [selectedIndex]);
  return (
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active} pageRetentionDistance={2}>
      {keys.map(makePage)}
    </CollapsiblePagerView>
  );
}

function owner() {
  return container.querySelector<HTMLElement>('[data-collapsible-pager-scroll]')!;
}

function prepareOwner(element: HTMLElement, extent: () => number) {
  Object.defineProperty(element, 'clientHeight', { configurable: true, value: 400 });
  Object.defineProperty(element, 'clientWidth', { configurable: true, value: 800 });
  Object.defineProperty(element, 'scrollHeight', { configurable: true, get: extent });
  element.getBoundingClientRect = () => ({
    top: 100, bottom: 500, left: 0, right: 800,
    width: 800, height: 400, x: 0, y: 100, toJSON() {},
  });
  element.scrollTo = ((options: ScrollToOptions) => {
    if (options.top !== undefined) element.scrollTop = options.top;
  }) as typeof element.scrollTo;
  const content = element.querySelector<HTMLElement>('[data-testid="content"]')!;
  content.getBoundingClientRect = () => ({
    top: 30, bottom: 900, left: 0, right: 800,
    width: 800, height: 870, x: 0, y: 30, toJSON() {},
  });
  return content;
}

it('publishes actual owner geometry and restores a pending route top after content grows', async () => {
  const pager = React.createRef<CollapsiblePagerView>();
  await act(async () => root.render(
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive>
      {makePage()}
    </CollapsiblePagerView>,
  ));
  const element = owner();
  let extent = 1200;
  const content = prepareOwner(element, () => extent);
  await act(async () => { resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)); });
  await flushFrames();
  expect(latestSnapshot()?.owner).toBe(element);
  expect(latestSnapshot()).toMatchObject({ width: 800, height: 400, scrollTop: 70 });
  let cleanup: (() => void) | undefined;
  await act(async () => { cleanup = latestSnapshot()?.registerContent(content, 600); });
  await flushFrames();
  expect(latestSnapshot()?.content).toBe(content);
  expect(latestSnapshot()?.contentHeight).toBe(600);
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={false}>
      {makePage()}
    </CollapsiblePagerView>,
  ));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive>
      {makePage()}
    </CollapsiblePagerView>,
  ));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 1200;
  await act(async () => { resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)); });
  await flushFrames();
  expect(element.scrollTop).toBe(300);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  await act(async () => cleanup?.());
});

it('keeps a route target across a real Activity disconnect and restores it after same-owner growth', async () => {
  const pager = React.createRef<CollapsiblePagerView>();
  const render = (mode: 'visible' | 'hidden', active: boolean) => (
    <React.Activity mode={mode}>
      <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
        headerHeight={0} stickyHeaderHeight={0} isActive={active}>
        {makePage()}
      </CollapsiblePagerView>
    </React.Activity>
  );
  await act(async () => root.render(render('visible', true)));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  let rawTop = 0;
  Object.defineProperty(element, 'scrollTop', {
    configurable: true,
    get: () => rawTop,
    set: (top: number) => { rawTop = Math.min(Math.max(0, top), Math.max(0, extent - 400)); },
  });
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  const samePager = pager.current;
  await act(async () => root.render(render('visible', false)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => root.render(render('hidden', false)));
  expect((samePager as unknown as { disposed: boolean }).disposed).toBe(true);
  expect(samePager?.restoreScrollPosition()).toBe(false);
  expect(frames.size).toBe(0);
  expect(element.isConnected).toBe(true);
  extent = 600;
  element.scrollTop = 200;
  await act(async () => root.render(render('visible', true)));
  await flushFrames();
  expect(pager.current).toBe(samePager);
  expect(owner()).toBe(element);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  expect(element.scrollTop).toBeLessThan(300);
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(300);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('does not transfer a route target from a removed pager instance to a new instance', async () => {
  const firstPager = React.createRef<CollapsiblePagerView>();
  const secondPager = React.createRef<CollapsiblePagerView>();
  const render = (pager: React.Ref<CollapsiblePagerView>, active: boolean) => (
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>
      {makePage()}
    </CollapsiblePagerView>
  );
  await act(async () => root.render(render(firstPager, true)));
  const firstOwner = owner();
  prepareOwner(firstOwner, () => 1200);
  await flushFrames();
  await act(async () => {
    firstOwner.scrollTop = 300;
    firstOwner.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(render(firstPager, false)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  const oldInstance = firstPager.current;
  await act(async () => root.render(null));
  expect(firstPager.current).toBeNull();
  await act(async () => root.render(render(secondPager, true)));
  const secondOwner = owner();
  prepareOwner(secondOwner, () => 1200);
  await flushFrames();
  expect(secondPager.current).not.toBe(oldInstance);
  expect(secondOwner).not.toBe(firstOwner);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('cancels a route target when Activity reconnects the same pager to a different DOM owner', async () => {
  const pager = React.createRef<CollapsiblePagerView>();
  const render = (mode: 'visible' | 'hidden', active: boolean, ownerKey: string) => (
    <React.Activity mode={mode}>
      <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
        headerHeight={0} stickyHeaderHeight={0} isActive={active}>
        <div key="A"><div key={ownerKey} data-collapsible-pager-scroll="true" data-collapsible-pager-scroll-key="A">
          <div data-testid="content"><SnapshotProbe pageKey="A" /></div>
        </div></div>
      </CollapsiblePagerView>
    </React.Activity>
  );
  await act(async () => root.render(render('visible', true, 'first')));
  const firstOwner = owner();
  prepareOwner(firstOwner, () => 1200);
  await flushFrames();
  await act(async () => {
    firstOwner.scrollTop = 300;
    firstOwner.dispatchEvent(new window.Event('scroll'));
  });
  const samePager = pager.current;
  await act(async () => root.render(render('visible', false, 'first')));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => root.render(render('hidden', false, 'first')));
  await act(async () => root.render(render('visible', true, 'second')));
  const secondOwner = owner();
  prepareOwner(secondOwner, () => 1200);
  await flushFrames();
  expect(pager.current).toBe(samePager);
  expect(secondOwner).not.toBe(firstOwner);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('publishes same-element explicit height changes and ignores stale registration cleanup', async () => {
  await act(async () => root.render(
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0}>{makePage()}</CollapsiblePagerView>,
  ));
  const element = owner();
  const content = prepareOwner(element, () => 1200);
  content.getBoundingClientRect = () => ({
    top: 100, bottom: 100, left: 0, right: 800,
    width: 800, height: 0, x: 0, y: 100, toJSON() {},
  });
  await flushFrames();
  let oldCleanup: (() => void) | undefined;
  await act(async () => { oldCleanup = latestSnapshot()?.registerContent(content, 400); });
  await flushFrames();
  expect(latestSnapshot()?.contentHeight).toBe(400);
  let newCleanup: (() => void) | undefined;
  await act(async () => { newCleanup = latestSnapshot()?.registerContent(content, 600); });
  expect(frames.size).toBeGreaterThan(0);
  await flushFrames();
  await act(async () => oldCleanup?.());
  expect(latestSnapshot()?.contentHeight).toBe(600);
  await act(async () => newCleanup?.());
  expect(latestSnapshot()?.content).toBeNull();
});

it('keeps the same logical page pending across a real React reorder and parent layout selection', async () => {
  await act(async () => root.render(<ReorderingPager keys={['A', 'B']} active />));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(<ReorderingPager keys={['A', 'B']} active={false} />));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(<ReorderingPager keys={['B', 'A']} active />));
  await flushFrames();
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  expect(element.scrollTop).toBe(0);
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(300);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('captures the previous logical owner when blur and page reorder share one commit', async () => {
  await act(async () => root.render(<ReorderingPager keys={['A', 'B']} active />));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(<ReorderingPager keys={['B', 'A']} active={false} />));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(<ReorderingPager keys={['B', 'A']} active />));
  await flushFrames();
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(300);
});

it('does not run queued tab alignment after route restoration wins during the same reorder', async () => {
  await act(async () => root.render(<ReorderingPager keys={['A', 'B']} active />));
  const element = owner();
  prepareOwner(element, () => 1200);
  await flushFrames();
  let rawTop = 0;
  const writes: number[] = [];
  Object.defineProperty(element, 'scrollTop', {
    configurable: true,
    get: () => rawTop,
    set: (value: number) => { rawTop = value; writes.push(value); },
  });
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(<ReorderingPager keys={['A', 'B']} active={false} />));
  element.scrollTop = 0;
  writes.length = 0;
  await act(async () => root.render(<ReorderingPager keys={['B', 'A']} active />));
  await flushFrames();
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  expect(writes).toEqual([300]);
});

it('cancels a pending route when the actual owner changes, while a ref rebound to the same owner preserves it', async () => {
  const renderPage = (identity: string, domKey: string, active: boolean) => (
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>
      <div key="A"><div key={domKey} data-collapsible-pager-scroll="true"
        data-collapsible-pager-scroll-key={identity}>
        <div data-testid="content"><SnapshotProbe pageKey="A" /></div>
      </div></div>
    </CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage('A:0', 'owner:0', true)));
  const first = owner();
  prepareOwner(first, () => 1200);
  await flushFrames();
  await act(async () => {
    first.scrollTop = 300;
    first.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage('A:0', 'owner:0', false)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => root.render(renderPage('A:0', 'owner:0', false)));
  expect(owner()).toBe(first);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => root.render(renderPage('A:0', 'owner:1', false)));
  expect(owner()).not.toBe(first);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('does not revive an old route target after a pagerScrollKey A-to-B-to-A reset', async () => {
  const renderPage = (identity: string, active: boolean) => (
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>
      <div key="A"><div data-collapsible-pager-scroll="true"
        data-collapsible-pager-scroll-key={identity}>
        <div data-testid="content"><SnapshotProbe pageKey="A" /></div>
      </div></div>
    </CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage('A:0', true)));
  const element = owner();
  prepareOwner(element, () => 1200);
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage('A:0', false)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => root.render(renderPage('A:1', false)));
  expect(owner()).toBe(element);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  await act(async () => root.render(renderPage('A:0', true)));
  await flushFrames();
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('cancels route pending on pointer input without changing the previous scroll position', async () => {
  const renderPage = (active: boolean) => (
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>{makePage()}</CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage(true)));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage(false)));
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(renderPage(true)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => element.dispatchEvent(new window.Event('pointerdown')));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(0);
});

it('keeps horizontal wheel input inert and cancels pending on a vertical owner wheel', async () => {
  const renderPage = (active: boolean) => (
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>{makePage()}</CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage(true)));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage(false)));
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(renderPage(true)));
  await act(async () => element.dispatchEvent(new window.WheelEvent('wheel', { deltaX: 10, deltaY: 0 })));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  await act(async () => element.dispatchEvent(new window.WheelEvent('wheel', { deltaY: 10 })));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('lets accepted same-offset top and NativeList commands take over an older route target', async () => {
  const pager = React.createRef<CollapsiblePagerView>();
  const renderPage = (active: boolean) => (
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>{makePage()}</CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage(true)));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  await flushFrames();
  const capture = async () => {
    extent = 1200;
    await act(async () => {
      element.scrollTop = 300;
      element.dispatchEvent(new window.Event('scroll'));
    });
    await act(async () => root.render(renderPage(false)));
    extent = 500;
    element.scrollTop = 0;
    await act(async () => root.render(renderPage(true)));
    expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  };
  await capture();
  await act(async () => pager.current?.scrollToPageTop({ animated: false }));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  await flushFrames();
  expect(element.scrollTop).toBe(0);
  await capture();
  await act(async () => element.dispatchEvent(new window.Event('ok-native-list-scroll-command-accepted')));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(0);
});

it('observes the fallback content root and retries a route top when normal content grows', async () => {
  const renderPage = (active: boolean) => (
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive={active}>{makePage()}</CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage(true)));
  const element = owner();
  let extent = 1200;
  const content = prepareOwner(element, () => extent);
  await flushFrames();
  expect(observedElements).toContain(content);
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage(false)));
  extent = 500;
  element.scrollTop = 0;
  await act(async () => root.render(renderPage(true)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(300);
});

it('waits for fresh automatic chrome measurements after a hidden zero-height route', async () => {
  const renderPage = (active: boolean) => (
    <CollapsiblePagerView header={<div data-testid="natural-header" />}
      stickyHeader={<div data-testid="natural-sticky" />} isActive={active}>{makePage()}</CollapsiblePagerView>
  );
  await act(async () => root.render(renderPage(true)));
  const element = owner();
  prepareOwner(element, () => 1200);
  let headerHeight = 212;
  let stickyHeight = 133;
  const header = container.querySelector<HTMLElement>('[data-testid="natural-header"]')!.parentElement!;
  const sticky = container.querySelector<HTMLElement>('[data-testid="natural-sticky"]')!.parentElement!;
  const rect = (height: number) => ({
    top: 0, bottom: height, left: 0, right: 800,
    width: 800, height, x: 0, y: 0, toJSON() {},
  });
  header.getBoundingClientRect = () => rect(headerHeight);
  sticky.getBoundingClientRect = () => rect(stickyHeight);
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.style.getPropertyValue('--ok-collapsible-pager-header-inset')).toBe('212px');
  expect(element.style.getPropertyValue('--ok-collapsible-pager-sticky-inset')).toBe('133px');
  await act(async () => {
    element.scrollTop = 500;
    element.dispatchEvent(new window.Event('scroll'));
  });
  await act(async () => root.render(renderPage(false)));
  headerHeight = 0;
  stickyHeight = 0;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  element.scrollTop = 0;
  headerHeight = 212;
  stickyHeight = 133;
  await act(async () => root.render(renderPage(true)));
  expect(latestSnapshot()?.pendingRouteScrollTop).toBe(500);
  await flushFrames();
  expect(element.style.getPropertyValue('--ok-collapsible-pager-header-inset')).toBe('212px');
  expect(element.style.getPropertyValue('--ok-collapsible-pager-sticky-inset')).toBe('133px');
  expect(element.scrollTop).toBe(500);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});

it('does not leave scheduled chrome measurements after unmount', async () => {
  await act(async () => root.render(
    <CollapsiblePagerView header={<div />} stickyHeader={<div />}>{makePage()}</CollapsiblePagerView>,
  ));
  await flushFrames();
  await act(async () => root.unmount());
  expect(frames.size).toBe(0);
  root = createRoot(container);
});

it('resumes geometry scheduling after StrictMode lifecycle replay', async () => {
  await act(async () => root.render(
    <React.StrictMode>
      <CollapsiblePagerView header={<div data-testid="strict-header" />}
        stickyHeader={<div data-testid="strict-sticky" />}>{makePage()}</CollapsiblePagerView>
    </React.StrictMode>,
  ));
  const element = owner();
  prepareOwner(element, () => 1200);
  for (const [testId, height] of [['strict-header', 200], ['strict-sticky', 100]] as const) {
    const chrome = container.querySelector<HTMLElement>(`[data-testid="${testId}"]`)!.parentElement!;
    chrome.getBoundingClientRect = () => ({
      top: 0, bottom: height, left: 0, right: 800,
      width: 800, height, x: 0, y: 0, toJSON() {},
    });
  }
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  await flushFrames();
  expect(latestSnapshot()?.owner).toBe(element);
  expect(element.style.getPropertyValue('--ok-collapsible-pager-header-inset')).toBe('200px');
  expect(element.style.getPropertyValue('--ok-collapsible-pager-sticky-inset')).toBe('100px');
  expect(latestSnapshot()).toMatchObject({ width: 800, height: 100 });
});

it('rejects initializing the explicitly Web pager in a native runtime', () => {
  const platform = (require('react-native') as typeof import('react-native')).Platform;
  const previous = platform.OS;
  Object.assign(platform, { OS: 'android' });
  try {
    const instance = new CollapsiblePagerView({ header: null, stickyHeader: null });
    expect(() => instance.componentDidMount()).toThrow('WebCollapsiblePagerView requires a Web DOM runtime');
  } finally {
    Object.assign(platform, { OS: previous });
  }
});

it('same commit blur and Activity hide and restores it after same-owner growth', async () => {
  const pager = React.createRef<CollapsiblePagerView>();
  const render = (mode: 'visible' | 'hidden', active: boolean) => (
    <React.Activity mode={mode}>
      <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
        headerHeight={0} stickyHeaderHeight={0} isActive={active}>
        {makePage()}
      </CollapsiblePagerView>
    </React.Activity>
  );
  await act(async () => root.render(render('visible', true)));
  const element = owner();
  let extent = 1200;
  prepareOwner(element, () => extent);
  let rawTop = 0;
  Object.defineProperty(element, 'scrollTop', {
    configurable: true,
    get: () => rawTop,
    set: (top: number) => { rawTop = Math.min(Math.max(0, top), Math.max(0, extent - 400)); },
  });
  await flushFrames();
  await act(async () => {
    element.scrollTop = 300;
    element.dispatchEvent(new window.Event('scroll'));
  });
  const samePager = pager.current;
  await act(async () => root.render(render('hidden', false)));
  expect((samePager as unknown as { disposed: boolean }).disposed).toBe(true);
  expect(samePager?.restoreScrollPosition()).toBe(false);
  expect(frames.size).toBe(0);
  expect(element.isConnected).toBe(true);
  extent = 600;
  element.scrollTop = 200;
  await act(async () => root.render(render('visible', true)));
  await flushFrames();
  expect(pager.current).toBe(samePager);
  expect(owner()).toBe(element);
  expect(element.scrollTop).toBeLessThan(300);
  extent = 1200;
  await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
  await flushFrames();
  expect(element.scrollTop).toBe(300);
  expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
});


it.each([375, 1920])('isolates retained inactive pages and reverses the attributes after selection at %ipx', async (width) => {
  Object.defineProperty(window, 'innerWidth', { configurable: true, value: width });
  const pager = React.createRef<CollapsiblePagerView>();
  await act(async () => root.render(
    <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={0} isActive pageRetentionDistance={2}>
      {makePage('A')}{makePage('B')}
    </CollapsiblePagerView>,
  ));
  const pageHost = (key: string) => container.querySelector<HTMLElement>(`[data-collapsible-pager-scroll-key="${key}"]`)!.closest<HTMLElement>('[aria-hidden]')!;
  const a = pageHost('A');
  const b = pageHost('B');
  expect(a.isConnected && b.isConnected).toBe(true);
  expect(a.inert).toBe(false);
  expect(a.getAttribute('aria-hidden')).toBe('false');
  expect(b.inert).toBe(true);
  expect(b.getAttribute('aria-hidden')).toBe('true');
  await act(async () => pager.current!.setPageWithoutAnimation(1));
  expect(pageHost('A')).toBe(a);
  expect(pageHost('B')).toBe(b);
  expect(a.inert).toBe(true);
  expect(a.getAttribute('aria-hidden')).toBe('true');
  expect(b.inert).toBe(false);
  expect(b.getAttribute('aria-hidden')).toBe('false');
  await act(async () => pager.current!.setPageWithoutAnimation(0));
  expect(a.inert).toBe(false);
  expect(a.getAttribute('aria-hidden')).toBe('false');
  expect(b.inert).toBe(true);
  expect(b.getAttribute('aria-hidden')).toBe('true');
});


describe.each([375, 1920])('nested NativeList scroll ownership at %ipx', (width) => {
  const snapshot: NativeListSnapshot = { schemaVersion: 1, generation: 1, layout: { kind: 'sectioned' }, rows: [{ type: 'identity', key: 'nested-row', title: 'Token' }] };
  function geometry(element: HTMLElement) {
    Object.defineProperties(element, {
      clientWidth: { configurable: true, value: width },
      clientHeight: { configurable: true, value: 400 },
      scrollHeight: { configurable: true, value: 1600 },
    });
    element.getBoundingClientRect = () => ({ top: 100, bottom: 500, left: 0, right: width, width, height: 400, x: 0, y: 100, toJSON() {} });
  }
  it('uses the owned slot host for insets, collapse, top commands and route restoration', async () => {
    const pager = React.createRef<CollapsiblePagerView>();
    const render = (active: boolean) => <React.Activity mode={active ? 'visible' : 'hidden'}><CollapsiblePagerView ref={pager} header={<div data-testid="slot-header" />} stickyHeader={<div />} headerHeight={100} stickyHeaderHeight={44} isActive={active}>
      <div key="A"><SnapshotProbe pageKey="A" /><WebNativeList testID="slot-list" snapshot={snapshot} listHeader={<div>Summary</div>} listFooter={<div>Footer</div>} /></div>
    </CollapsiblePagerView></React.Activity>;
    await act(async () => root.render(render(true)));
    const list = container.querySelector<HTMLElement>('[data-testid="slot-list"]')!;
    const owned = list.firstElementChild as HTMLElement;
    const bounds = owned.firstElementChild as HTMLElement;
    const viewport = list.querySelector<HTMLElement>('.ok-native-list-viewport')!;
    expect(viewport).not.toBeNull();
    geometry(owned);
    bounds.getBoundingClientRect = () => ({ top: 100 - owned.scrollTop, bottom: 1500 - owned.scrollTop, left: 0, right: width, width, height: 1400, x: 0, y: 100 - owned.scrollTop, toJSON() {} });
    await act(async () => resizeCallbacks.forEach((callback) => callback([], {} as ResizeObserver)));
    await flushFrames();
    expect(latestSnapshot()?.owner).toBe(owned);
    expect(latestSnapshot()?.owner).not.toBe(viewport);
    expect(owned.getAttribute('data-collapsible-pager-scroll')).toBe('true');
    expect(bounds.style.paddingTop).toContain('144px');
    expect(viewport.style.paddingTop).toBe('');
    await act(async () => { owned.scrollTop = 80; owned.dispatchEvent(new window.Event('scroll')); });
    await flushFrames();
    expect(container.querySelector('[data-testid="slot-header"]')!.parentElement!.style.transform).toBe('translateY(-80px)');
    expect(viewport.scrollTop).toBe(0);
    await act(async () => pager.current!.scrollToPageTop({ animated: false }));
    await flushFrames();
    expect(owned.scrollTop).toBe(0);
    expect(viewport.scrollTop).toBe(0);
    await act(async () => { owned.scrollTop = 300; owned.dispatchEvent(new window.Event('scroll')); });
    await act(async () => root.render(render(false)));
    expect(latestSnapshot()?.pendingRouteScrollTop).toBe(300);
    owned.scrollTop = 0;
    await act(async () => root.render(render(true)));
    await flushFrames();
    expect(owned.scrollTop).toBe(300);
    expect(viewport.scrollTop).toBe(0);
    expect(latestSnapshot()?.pendingRouteScrollTop).toBeNull();
    await act(async () => root.render(null));
    expect(owned.isConnected).toBe(false);
    expect(owned.hasAttribute('data-collapsible-pager-scroll')).toBe(false);
    expect(container.querySelectorAll('[data-collapsible-pager-scroll]')).toHaveLength(0);
  });
  it('keeps the ordinary no-slot viewport as the owner', async () => {
    await act(async () => root.render(<CollapsiblePagerView header={<div />} stickyHeader={<div />} headerHeight={0} stickyHeaderHeight={0}>
      <div key="A"><SnapshotProbe pageKey="A" /><WebNativeList snapshot={snapshot} /></div>
    </CollapsiblePagerView>));
    await flushFrames();
    const viewport = container.querySelector<HTMLElement>('.ok-native-list-viewport')!;
    expect(latestSnapshot()?.owner).toBe(viewport);
    expect(container.querySelectorAll('[data-collapsible-pager-scroll]')).toHaveLength(0);
  });
  it('preserves an explicit external host instead of claiming a second owner', async () => {
    function ExternalPage() {
      const [external, setExternal] = React.useState<HTMLDivElement | null>(null);
      return <div data-testid="external-owner" data-collapsible-pager-scroll="true" ref={setExternal}>
        <SnapshotProbe pageKey="A" /><WebNativeList snapshot={snapshot} listHeader={<div>Summary</div>} webScrollHost={external} />
      </div>;
    }
    await act(async () => root.render(<CollapsiblePagerView header={<div />} stickyHeader={<div />} headerHeight={0} stickyHeaderHeight={0}>
      <ExternalPage key="A" />
    </CollapsiblePagerView>));
    const external = container.querySelector<HTMLElement>('[data-testid="external-owner"]')!;
    geometry(external);
    await flushFrames();
    expect(latestSnapshot()?.owner).toBe(external);
    expect(container.querySelectorAll('[data-collapsible-pager-scroll]')).toHaveLength(1);
    expect(external.querySelector('.ok-native-list-viewport')).not.toBe(external);
  });
});


describe('home native text toolbar DOM contract', () => {
  beforeEach(() => {
    jest.spyOn(window.HTMLElement.prototype, 'getBoundingClientRect').mockImplementation(() => ({ width: 390, height: 844, x: 0, y: 0, top: 0, left: 0, bottom: 844, right: 390, toJSON() {} }));
  });
  const items = [{ key: 'tokens', title: 'Tokens' }, { key: 'activity', title: 'Activity' },
    { key: 'extra', title: 'Extra' }];
  const toolbar = { tokens: { iconName: 'Copy3Outline', testID: 'token-toolbar',
    accessibilityLabel: 'Token settings', selected: true, selectedBackgroundColor: '#cccccc',
    hoverBackgroundColor: '#eeeeee', pressedBackgroundColor: '#dddddd' },
    extra: { iconName: 'StarOutline', testID: 'extra-toolbar', accessibilityLabel: 'Extra filter' } };
  function renderBar(selectedKey: string, onTab = jest.fn(), onToolbar = jest.fn(), customToolbar = toolbar,
    pager = React.createRef<CollapsiblePagerView>()) {
    return <CollapsiblePagerView ref={pager} header={<div />} headerHeight={0} stickyHeaderHeight={44}
      nativeTabBar={{ controlled: true, selectedKey, items, toolbar: customToolbar,
        style: { variant: 'text', activeFontFamily: 'Semibold', fontFamily: 'Medium' } }}
      onNativeTabPress={onTab} onNativeToolbarPress={onToolbar}>
      {makePage('tokens')}{makePage('activity')}
    </CollapsiblePagerView>;
  }

  it('emits controlled extra-tab intent while keeping selection and page commands owned by the parent', async () => {
    const pager = React.createRef<CollapsiblePagerView>();
    const onTab = jest.fn();
    await act(async () => root.render(renderBar('tokens', onTab, jest.fn(), toolbar, pager)));
    const animated = jest.spyOn(pager.current!, 'setPage');
    const immediate = jest.spyOn(pager.current!, 'setPageWithoutAnimation');
    const tabs = Array.from(container.querySelectorAll<HTMLButtonElement>('[role="tab"]'));
    expect(tabs).toHaveLength(3);
    await act(async () => tabs[2].click());
    await flushFrames();
    expect(onTab).toHaveBeenCalledWith(expect.objectContaining({ nativeEvent: { position: 2, key: 'extra' } }));
    expect(animated).not.toHaveBeenCalled(); expect(immediate).not.toHaveBeenCalled();
    expect(tabs.map(tab => tab.getAttribute('aria-selected'))).toEqual(['true', 'false', 'false']);
    await act(async () => root.render(renderBar('extra', onTab, jest.fn(), toolbar, pager)));
    expect(tabs.map(tab => tab.getAttribute('aria-selected'))).toEqual(['false', 'false', 'true']);
    expect(tabs[2].style.fontFamily).toBe('Semibold');
    expect(tabs[2].style.fontWeight).toBe('600');
  });

  it('renders no toolbar for a selected key with no toolbar entry', async () => {
    await act(async () => root.render(renderBar('activity')));
    expect(container.querySelector('.ok-pager-toolbar')).toBeNull();
  });

  it('resolves an icon name through the SDK fixture resource table and emits its measured anchor', async () => {
    const onToolbar = jest.fn();
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar)));
    const button = container.querySelector<HTMLButtonElement>('[data-testid="token-toolbar"]')!;
    expect(button.disabled).toBe(false);
    expect(button.getAttribute('aria-label')).toBe('Token settings');
    const href = button.querySelector('svg use')!.getAttribute('href')!;
    expect(href).toMatch(/icon-fixtures\/web\/assets\/Copy3Outline.svg#onekey-Copy3Outline$/);
    const fs = require('node:fs') as typeof import('node:fs');
    const artwork = fs.readFileSync(new URL(href.split('#')[0]), 'utf8');
    expect(artwork).toContain('id="onekey-Copy3Outline"');
    expect(artwork).toContain('<path');
    button.getBoundingClientRect = () => ({ x: 310, y: 175, top: 175, left: 310,
      right: 348, bottom: 213, width: 38, height: 38, toJSON() {} });
    await act(async () => button.click());
    expect(onToolbar).toHaveBeenCalledTimes(1);
    expect(onToolbar.mock.calls[0][0].nativeEvent).toEqual({ key: 'tokens',
      anchorRect: { x: 310, y: 175, width: 38, height: 38 } });
  });

  it('disables an unknown artwork name and emits no toolbar press', async () => {
    const onToolbar = jest.fn();
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar,
      { tokens: { ...toolbar.tokens, iconName: 'NoSuchArtworkForContractTest' } } as typeof toolbar)));
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    expect(button.disabled).toBe(true);
    expect(button.querySelector('svg use')).toBeNull();
    await act(async () => button.click());
    expect(onToolbar).not.toHaveBeenCalled();
  });

  it('reuses the toolbar button and updates artwork and background when the controlled key changes', async () => {
    const pager = React.createRef<CollapsiblePagerView>();
    await act(async () => root.render(renderBar('tokens', jest.fn(), jest.fn(), toolbar, pager)));
    const previous = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    expect(previous.style.getPropertyValue('--pager-toolbar-base')).toBe('#cccccc');
    expect(previous.style.getPropertyValue('--pager-toolbar-hover')).toBe('#eeeeee');
    expect(previous.style.getPropertyValue('--pager-toolbar-pressed')).toBe('#dddddd');
    const styles = container.querySelector('style')!.textContent!;
    expect(styles).toContain('.ok-pager-toolbar:hover');
    expect(styles).toContain('.ok-pager-toolbar:active');
    await act(async () => root.render(renderBar('extra', jest.fn(), jest.fn(), toolbar, pager)));
    const current = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    expect(current).toBe(previous);
    expect(previous.isConnected).toBe(true);
    expect(current.style.getPropertyValue('--pager-toolbar-base')).toBe('transparent');
    expect(current.querySelector('use')!.getAttribute('href')).toContain('StarOutline.svg#onekey-StarOutline');
    await act(async () => root.render(renderBar('activity', jest.fn(), jest.fn(), toolbar, pager)));
    await act(async () => pager.current!.setPageWithoutAnimation(1));
    expect(current.isConnected).toBe(false);
    expect(container.querySelector('.ok-pager-toolbar')).toBeNull();
  });

  it('keeps a caller-provided legacy sticky tab bar single for underline and segmented configs', async () => {
    for (const variant of ['underline', 'segmented'] as const) {
      await act(async () => root.render(
        <CollapsiblePagerView header={<div />} headerHeight={0} stickyHeaderHeight={44}
          stickyHeader={<div data-testid="legacy-sticky-tabs">Legacy tabs</div>}
          nativeTabBar={{ items, style: { variant } }}>
          {makePage('tokens')}{makePage('activity')}
        </CollapsiblePagerView>,
      ));
      expect(container.querySelectorAll('[data-testid="legacy-sticky-tabs"]')).toHaveLength(1);
      expect(container.querySelector('.ok-pager-tab-bar')).toBeNull();
      expect(container.querySelector('.ok-pager-toolbar')).toBeNull();
    }
  });

  it('keeps a fixed slot for empty/absent entries and uses the largest entry dimensions', async () => {
    const configured = { ...toolbar, tokens: { ...toolbar.tokens, contentWidth: 30, trailingInset: 25 } };
    await act(async () => root.render(renderBar('tokens', jest.fn(), jest.fn(), configured)));
    const slot = container.querySelector<HTMLElement>('.ok-pager-toolbar-slot')!;
    expect(slot.style.width).toBe('55px');
    await act(async () => root.render(renderBar('extra', jest.fn(), jest.fn(), configured)));
    expect(container.querySelector('.ok-pager-toolbar-slot')).toBe(slot);
    expect(slot.style.width).toBe('55px');
    await act(async () => root.render(renderBar('none', jest.fn(), jest.fn(), configured)));
    expect(slot.style.width).toBe('55px');
    expect(slot.querySelector('button')).toBeNull();
    await act(async () => root.render(renderBar('none', jest.fn(), jest.fn(), {} as typeof toolbar)));
    expect(slot.style.width).toBe('44px');
    await act(async () => root.render(<CollapsiblePagerView nativeTabBar={{ controlled: true, selectedKey: 'tokens', items, style: { variant: 'text' } }} header={<div />} stickyHeader={<div />}>{makePage('tokens')}</CollapsiblePagerView>));
    expect(container.querySelector('.ok-pager-toolbar-slot')).toBeNull();
  });

  it('reuses identical artwork across keys and rejects a stale pointer click', async () => {
    const configured = { ...toolbar, extra: { ...toolbar.extra, iconName: toolbar.tokens.iconName } };
    const onToolbar = jest.fn();
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar, configured)));
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    const icon = button.querySelector('svg');
    const requests = artworkRequests.length;
    button.dispatchEvent(new window.Event('pointerdown', { bubbles: true }));
    await act(async () => root.render(renderBar('extra', jest.fn(), onToolbar, configured)));
    expect(container.querySelector('.ok-pager-toolbar')).toBe(button);
    expect(button.querySelector('svg')).toBe(icon);
    expect(artworkRequests).toHaveLength(requests);
    button.dispatchEvent(new window.MouseEvent('click', { bubbles: true, detail: 1 }));
    expect(onToolbar).not.toHaveBeenCalled();
    button.dispatchEvent(new window.MouseEvent('click', { bubbles: true, detail: 0 }));
    expect(onToolbar.mock.calls[0][0].nativeEvent.key).toBe('extra');
  });

  it('does not restart pending artwork and invalidates B when cached A returns', async () => {
    await act(async () => root.render(renderBar('tokens')));
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    const initial = button.querySelector('svg');
    completeArtwork = false;
    await act(async () => root.render(renderBar('extra')));
    const pending = artworkRequests[artworkRequests.length - 1];
    const stale = pending.onload;
    const requests = artworkRequests.length;
    await act(async () => root.render(renderBar('extra')));
    expect(artworkRequests).toHaveLength(requests);
    await act(async () => root.render(renderBar('tokens')));
    expect(button.disabled).toBe(false);
    await act(async () => stale?.());
    expect(button.querySelector('svg')).toBe(initial);
    expect(button.disabled).toBe(false);
  });

  it('hides loaded artwork when returning to a failed identity without retrying it', async () => {
    completeArtwork = false;
    const onToolbar = jest.fn();
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar)));
    await act(async () => artworkRequests[0].onerror?.());
    completeArtwork = true;
    await act(async () => root.render(renderBar('extra', jest.fn(), onToolbar)));
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    expect(button.disabled).toBe(false);
    expect(button.querySelector('use')!.getAttribute('href')).toContain('StarOutline.svg');
    const requests = artworkRequests.length;
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar)));
    expect(button.disabled).toBe(true);
    expect(button.hidden).toBe(true);
    expect(button.getAttribute('aria-hidden')).toBe('true');
    button.click();
    expect(onToolbar).not.toHaveBeenCalled();
    await act(async () => root.render(renderBar('tokens', jest.fn(), onToolbar)));
    expect(artworkRequests).toHaveLength(requests);
  });

  it('cancels a pending replacement when returning to a failed identity', async () => {
    completeArtwork = false;
    await act(async () => root.render(renderBar('tokens')));
    await act(async () => artworkRequests[0].onerror?.());
    await act(async () => root.render(renderBar('extra')));
    const pending = artworkRequests[artworkRequests.length - 1];
    const stale = pending.onload;
    const requests = artworkRequests.length;
    await act(async () => root.render(renderBar('tokens')));
    expect(pending.onload).toBeNull();
    expect(pending.onerror).toBeNull();
    await act(async () => stale?.());
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    expect(button.disabled).toBe(true);
    expect(button.hidden).toBe(true);
    expect(button.querySelector('use')).toBeNull();
    await act(async () => root.render(renderBar('tokens')));
    expect(artworkRequests).toHaveLength(requests);
  });

  function presenceAnimations() {
    const calls: Array<{ frames: Keyframe[]; duration: number; animation: { cancel: jest.Mock; onfinish: (() => void) | null } }> = [];
    Object.defineProperty(window.HTMLElement.prototype, 'animate', { configurable: true, value(frames: Keyframe[], options: KeyframeAnimationOptions) {
      const animation = { cancel: jest.fn(), onfinish: null as (() => void) | null };
      calls.push({ frames, duration: Number(options.duration), animation }); return animation;
    } }); return calls;
  }
  const adjacentToolbar = { ...toolbar, activity: { ...toolbar.extra, accessibilityLabel: 'Activity tools', testID: 'activity-toolbar', selected: false } };
  function finishTrack(track: HTMLElement) {
    const event = new window.Event('transitionend');
    Object.defineProperty(event, 'propertyName', { value: 'transform' }); track.dispatchEvent(event);
  }
  it('retains old metadata through motion and atomically replaces different artwork after settle and readiness', async () => {
    const animations = presenceAnimations(); const pager = React.createRef<CollapsiblePagerView>(); const onToolbar = jest.fn();
    const render = (key: string) => renderBar(key, jest.fn(), onToolbar, adjacentToolbar, pager);
    await act(async () => root.render(render('tokens'))); animations[0].animation.onfinish?.();
    const presentation = colorPresentation(); const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    const artwork = button.firstChild; const requests = artworkRequests.length; completeArtwork = false;
    button.dispatchEvent(new window.Event('pointerdown', { bubbles: true }));
    await act(async () => root.render(render('activity')));
    expect(button.disabled).toBe(true); expect(button.getAttribute('aria-hidden')).toBe('true');
    expect(button.getAttribute('aria-label')).toBe('Token settings');
    expect(button.style.getPropertyValue('--pager-toolbar-base')).toBe('#cccccc');
    expect(button.firstChild).toBe(artwork); expect(artworkRequests).toHaveLength(requests);
    await act(async () => pager.current!.setPage(1)); presentation.setOffset(-195); await flushFrames();
    expect(button.firstChild).toBe(artwork); expect(button.getAttribute('aria-label')).toBe('Token settings');
    expect(artworkRequests).toHaveLength(requests); presentation.setOffset(-390);
    await act(async () => finishTrack(presentation.track));
    expect(button.firstChild).toBe(artwork); expect(button.disabled).toBe(true);
    expect(button.getAttribute('aria-label')).toBe('Token settings'); expect(artworkRequests).toHaveLength(requests + 1);
    await act(async () => artworkRequests[requests].onload?.());
    expect(container.querySelector('.ok-pager-toolbar')).toBe(button); expect(button.firstChild).not.toBe(artwork);
    expect(button.getAttribute('aria-label')).toBe('Activity tools');
    expect(button.style.getPropertyValue('--pager-toolbar-base')).toBe('transparent');
    expect(button.disabled).toBe(false); expect(animations).toHaveLength(1);
    button.dispatchEvent(new window.MouseEvent('click', { bubbles: true, detail: 1 })); expect(onToolbar).not.toHaveBeenCalled();
    button.click(); expect(onToolbar.mock.calls[0][0].nativeEvent.key).toBe('activity');
  });
  it('transfers a shared SVG owner and LP state only at settle without replaying old pointer input', async () => {
    const animations = presenceAnimations(); const pager = React.createRef<CollapsiblePagerView>(); const onToolbar = jest.fn();
    const map = { ...adjacentToolbar, activity: { ...adjacentToolbar.activity, iconName: toolbar.tokens.iconName } };
    const render = (key: string) => renderBar(key, jest.fn(), onToolbar, map, pager);
    await act(async () => root.render(render('tokens'))); animations[0].animation.onfinish?.();
    const presentation = colorPresentation(); const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    const icon = button.firstChild; const requests = artworkRequests.length;
    button.focus(); button.dispatchEvent(new window.Event('pointerdown', { bubbles: true }));
    await act(async () => root.render(render('activity')));
    expect(document.activeElement).not.toBe(button); expect(button.getAttribute('aria-label')).toBe('Token settings');
    expect(button.style.getPropertyValue('--pager-toolbar-base')).toBe('#cccccc');
    await act(async () => pager.current!.setPage(1)); presentation.setOffset(-390);
    await act(async () => finishTrack(presentation.track));
    expect(button.firstChild).toBe(icon); expect(artworkRequests).toHaveLength(requests); expect(animations).toHaveLength(1);
    expect(button.getAttribute('aria-label')).toBe('Activity tools'); expect(button.style.getPropertyValue('--pager-toolbar-base')).toBe('transparent');
    button.click(); expect(onToolbar.mock.calls[0][0].nativeEvent.key).toBe('activity');
    button.dispatchEvent(new window.MouseEvent('click', { bubbles: true, detail: 1 })); expect(onToolbar).toHaveBeenCalledTimes(1);
    button.dispatchEvent(new window.Event('pointerdown', { bubbles: true }));
    button.dispatchEvent(new window.MouseEvent('click', { bubbles: true, detail: 1 })); expect(onToolbar).toHaveBeenCalledTimes(2);
  });
  it('uses cancellable 100ms presence fades while keeping the full map slot', async () => {
    const animations = presenceAnimations(); const pager = React.createRef<CollapsiblePagerView>();
    const render = (key: string) => renderBar(key, jest.fn(), jest.fn(), toolbar, pager);
    await act(async () => root.render(render('tokens'))); animations[0].animation.onfinish?.();
    const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
    await act(async () => root.render(render('activity'))); await act(async () => pager.current!.setPageWithoutAnimation(1));
    expect(button.disabled).toBe(true); expect(button.getAttribute('aria-hidden')).toBe('true');
    expect(animations[1].duration).toBe(100); expect(animations[1].frames[1].opacity).toBe('0');
    expect(container.querySelector<HTMLElement>('.ok-pager-toolbar-slot')!.style.width).toBe('44px');
    const staleHide = animations[1].animation.onfinish;
    const originalStyle = window.getComputedStyle.bind(window);
    jest.spyOn(window, 'getComputedStyle').mockImplementation(element => element === button ? { opacity: '0.4' } as CSSStyleDeclaration : originalStyle(element));
    await act(async () => root.render(render('tokens'))); await act(async () => pager.current!.setPageWithoutAnimation(0));
    expect(animations[2].frames[1].opacity).toBe('1'); expect(animations.every(item => item.duration === 100)).toBe(true);
    expect(animations[2].frames[0].opacity).toBe('0.4');
    staleHide?.(); expect(container.querySelector('.ok-pager-toolbar')).toBe(button); expect(button.disabled).toBe(false);
  });

  it('keeps a timeout-before-physical-end disabled and restores no-animation ownership after an interrupted transition', async () => {
    jest.useFakeTimers({ doNotFake: ['queueMicrotask', 'requestAnimationFrame', 'cancelAnimationFrame'] });
    try {
      const pager = React.createRef<CollapsiblePagerView>(); const onToolbar = jest.fn();
      const render = (key: string) => renderBar(key, jest.fn(), onToolbar, adjacentToolbar, pager);
      await act(async () => root.render(render('tokens')));
      const presentation = colorPresentation(); const button = container.querySelector<HTMLButtonElement>('.ok-pager-toolbar')!;
      await act(async () => root.render(render('activity'))); await act(async () => pager.current!.setPage(1));
      presentation.setOffset(-195); await act(async () => jest.advanceTimersByTime(400));
      expect(button.disabled).toBe(true); expect(button.getAttribute('aria-label')).toBe('Token settings');
      presentation.setOffset(-390); await act(async () => finishTrack(presentation.track));
      expect(button.disabled).toBe(false); expect(button.getAttribute('aria-label')).toBe('Activity tools');
      await act(async () => root.render(render('tokens'))); presentation.setOffset(-195);
      await act(async () => pager.current!.setPage(0)); expect(button.disabled).toBe(true);
      presentation.setOffset(0); await act(async () => pager.current!.setPageWithoutAnimation(1));
      await act(async () => pager.current!.setPageWithoutAnimation(0)); await act(async () => jest.advanceTimersByTime(400));
      expect(button.disabled).toBe(false); expect(button.getAttribute('aria-label')).toBe('Token settings');
      button.click(); expect(onToolbar.mock.calls[0][0].nativeEvent.key).toBe('tokens');
    } finally { jest.useRealTimers(); }
  });

  function colorPresentation() {
    const viewport = container.firstElementChild as HTMLElement;
    const owner = container.querySelector<HTMLElement>('[data-collapsible-pager-scroll-key="tokens"]')!;
    const track = owner.parentElement!.parentElement!.parentElement!;
    let width = 390;
    let offset = 0;
    viewport.getBoundingClientRect = () => ({ width } as DOMRect);
    const original = window.getComputedStyle.bind(window);
    jest.spyOn(window, 'getComputedStyle').mockImplementation((element) => element === track
      ? { transform: `matrix(1, 0, 0, 1, ${offset}, 0)` } as CSSStyleDeclaration : original(element));
    return { track, setOffset(value: number) { offset = value; }, setWidth(value: number) { width = value; } };
  }

  function renderColors(pager: React.RefObject<CollapsiblePagerView | null>, selectedKey: string, isActive = true) {
    return <CollapsiblePagerView ref={pager} header={<div />} stickyHeader={<div />}
      headerHeight={0} stickyHeaderHeight={44} isActive={isActive}
      nativeTabBar={{ controlled: true, selectedKey, items, style: { variant: 'text' } }}>
      {makePage('tokens')}{makePage('activity')}
    </CollapsiblePagerView>;
  }

  it('keeps actual page emphasis through config echoes and samples CSS presentation during reversal', async () => {
    const pager = React.createRef<CollapsiblePagerView>();
    await act(async () => root.render(renderColors(pager, 'tokens')));
    const presentation = colorPresentation();
    const tabs = Array.from(container.querySelectorAll<HTMLButtonElement>('[role="tab"]'));
    await act(async () => root.render(renderColors(pager, 'activity')));
    expect(tabs.map(tab => tab.style.color)).toEqual(['rgb(0, 0, 0)', 'rgb(102, 102, 102)', 'rgb(102, 102, 102)']);
    expect(tabs.map(tab => tab.getAttribute('aria-selected'))).toEqual(['false', 'true', 'false']);
    await act(async () => pager.current!.setPage(1));
    presentation.setOffset(-195);
    await flushFrames();
    expect(tabs.slice(0, 2).map(tab => tab.style.color)).toEqual(['rgb(51, 51, 51)', 'rgb(51, 51, 51)']);
    await act(async () => root.render(renderColors(pager, 'activity')));
    expect(tabs[0].style.color).toBe('rgb(51, 51, 51)');
    await act(async () => root.render(renderColors(pager, 'tokens')));
    await act(async () => pager.current!.setPage(0));
    presentation.setOffset(-97.5);
    await flushFrames();
    expect(tabs.slice(0, 2).map(tab => tab.style.color)).toEqual(['rgb(26, 26, 26)', 'rgb(77, 77, 77)']);
    const staleEnd = new window.Event('transitionend');
    Object.defineProperty(staleEnd, 'propertyName', { value: 'transform' });
    presentation.track.dispatchEvent(staleEnd);
    presentation.setOffset(0);
    await flushFrames();
    expect(tabs[0].style.color).toBe('rgb(0, 0, 0)');
    expect(tabs[1].style.color).toBe('rgb(102, 102, 102)');
  });

  it('resamples resize and route restoration and preserves the non-page fallback', async () => {
    const pager = React.createRef<CollapsiblePagerView>();
    await act(async () => root.render(renderColors(pager, 'tokens')));
    const presentation = colorPresentation();
    const tabs = Array.from(container.querySelectorAll<HTMLButtonElement>('[role="tab"]'));
    await act(async () => root.render(renderColors(pager, 'activity')));
    await act(async () => pager.current!.setPage(1));
    presentation.setOffset(-390);
    presentation.setWidth(780);
    await flushFrames();
    expect(tabs[0].style.color).toBe('rgb(51, 51, 51)');
    presentation.setWidth(0);
    await act(async () => root.render(renderColors(pager, 'activity', false)));
    await flushFrames();
    presentation.setWidth(390);
    await act(async () => root.render(renderColors(pager, 'activity')));
    expect(tabs[1].style.color).toBe('rgb(0, 0, 0)');
    await act(async () => root.render(renderColors(pager, 'extra')));
    await flushFrames();
    expect(tabs.map(tab => tab.style.color)).toEqual(['rgb(102, 102, 102)', 'rgb(102, 102, 102)', 'rgb(0, 0, 0)']);
  });

});
