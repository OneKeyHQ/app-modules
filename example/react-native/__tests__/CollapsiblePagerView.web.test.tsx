import React from 'react';
import { Platform, View } from 'react-native';
import ReactTestRenderer from 'react-test-renderer';

import { CollapsiblePagerView as NativeCollapsiblePagerView } from '../../../native-views/react-native-pager-view/src/CollapsiblePagerView';
import { Commands as NativeCollapsiblePagerCommands } from '../../../native-views/react-native-pager-view/src/CollapsiblePagerViewNativeComponent';
import { CollapsiblePagerView } from '../../../native-views/react-native-pager-view/src/CollapsiblePagerView.web';
import { PagerView } from '../../../native-views/react-native-pager-view/src/PagerView.web';

const pages = (count: number) =>
  Array.from({ length: count }, (_, index) => (
    <View key={`page-${index}`} testID={`page-${index}`} />
  ));

const requiredProps = {
  header: <View />,
  stickyHeader: <View />,
  headerHeight: 100,
  stickyHeaderHeight: 44,
};

const originalDocument = Object.getOwnPropertyDescriptor(globalThis, 'document');
const originalResizeObserver = globalThis.ResizeObserver;
const originalPlatform = Platform.OS;

function setWebTestRuntime() {
  Object.defineProperty(Platform, 'OS', { configurable: true, value: 'web' });
  Object.defineProperty(globalThis, 'document', {
    configurable: true,
    value: {},
  });
  globalThis.ResizeObserver = class {
    observe() {}
    unobserve() {}
    disconnect() {}
  };
}

function restoreTestRuntime() {
  Object.defineProperty(Platform, 'OS', {
    configurable: true,
    value: originalPlatform,
  });
  if (originalDocument)
    Object.defineProperty(globalThis, 'document', originalDocument);
  else Reflect.deleteProperty(globalThis, 'document');
  globalThis.ResizeObserver = originalResizeObserver;
}

describe('CollapsiblePagerView native wrapper', () => {
  it('loads Web-named root exports without a DOM in the native runtime', () => {
    const descriptor = Object.getOwnPropertyDescriptor(globalThis, 'document');
    Object.defineProperty(globalThis, 'document', { configurable: true, value: undefined });
    try {
      jest.isolateModules(() => {
        const root = require('../../../native-views/react-native-pager-view/src/index') as typeof import('../../../native-views/react-native-pager-view/src/index');
        expect(root.WebCollapsiblePagerView).toBeDefined();
        expect(root.useWebCollapsiblePagerPage).toBeDefined();
      });
    } finally {
      if (descriptor) Object.defineProperty(globalThis, 'document', descriptor);
      else Reflect.deleteProperty(globalThis, 'document');
    }
  });

  it('passes the rendered-page-clamped initial index to native', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <NativeCollapsiblePagerView {...requiredProps} initialPage={99}>
          <View key="page-0" />
          {false}
          <View key="page-1" />
        </NativeCollapsiblePagerView>,
      );
    });
    const nativeHost = renderer.root.find(
      node => typeof node.props.retainedPages === 'string',
    );
    expect(nativeHost.props.initialPage).toBe(1);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  it('updates native page identity in the same render that replaces pages', async () => {
    const keyedPages = (keys: readonly string[]) =>
      keys.map(key => <View key={key} testID={key} />);
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <NativeCollapsiblePagerView
          {...requiredProps}
          pageRetentionDistance={10}
        >
          {keyedPages(['alpha', 'beta'])}
        </NativeCollapsiblePagerView>,
      );
    });

    const nativeHost = () =>
      renderer.root.find(node => typeof node.props.retainedPages === 'string');
    expect(JSON.parse(nativeHost().props.pageKeys)).toEqual([
      '.$alpha',
      '.$beta',
    ]);

    await ReactTestRenderer.act(() => {
      renderer.update(
        <NativeCollapsiblePagerView
          {...requiredProps}
          pageRetentionDistance={10}
        >
          {keyedPages(['beta', 'gamma'])}
        </NativeCollapsiblePagerView>,
      );
    });

    expect(JSON.parse(nativeHost().props.pageKeys)).toEqual([
      '.$beta',
      '.$gamma',
    ]);
    expect(JSON.parse(nativeHost().props.retainedPages)).toEqual([0, 1]);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  describe('native tab presses', () => {
    const nativeTabBar = {
      items: ['alpha', 'beta', 'gamma', 'delta'].map(key => ({
        key,
        title: key,
      })),
    };

    beforeEach(() => {
      jest.useFakeTimers();
      jest
        .spyOn(NativeCollapsiblePagerCommands, 'setPage')
        .mockImplementation(() => {});
      jest
        .spyOn(NativeCollapsiblePagerCommands, 'setPageWithoutAnimation')
        .mockImplementation(() => {});
    });

    afterEach(() => {
      jest.restoreAllMocks();
      jest.useRealTimers();
    });

    const pressTab = async (
      animationEnabled: boolean | undefined,
      position: number,
      initialPage = 0,
    ) => {
      let renderer!: ReactTestRenderer.ReactTestRenderer;
      await ReactTestRenderer.act(() => {
        renderer = ReactTestRenderer.create(
          <NativeCollapsiblePagerView
            {...requiredProps}
            nativeTabBar={nativeTabBar}
            initialPage={initialPage}
            nativeTabPressAnimationEnabled={animationEnabled}
          >
            {pages(nativeTabBar.items.length)}
          </NativeCollapsiblePagerView>,
          { createNodeMock: () => ({}) },
        );
      });
      const nativeHost = renderer.root.find(
        node => typeof node.props.retainedPages === 'string',
      );
      await ReactTestRenderer.act(() => {
        nativeHost.props.onNativeTabPress({
          nativeEvent: { position, key: nativeTabBar.items[position].key },
        });
      });
      // The command is dispatched on the next frame, once the page is retained.
      await ReactTestRenderer.act(() => {
        jest.runOnlyPendingTimers();
      });
      expect(JSON.parse(nativeHost.props.retainedPages)).toContain(position);
      await ReactTestRenderer.act(() => {
        renderer.unmount();
      });
    };

    it.each([
      [undefined, 0, 1, true],
      [undefined, 0, 3, false],
      [undefined, 3, 2, true],
      [undefined, 3, 0, false],
      [true, 0, 3, true],
      [false, 0, 1, false],
      [false, 0, 3, false],
    ] as const)(
      'uses animation=%s from %s to %s with animated command=%s',
      async (animationEnabled, initialPage, target, animated) => {
        await pressTab(animationEnabled, target, initialPage);
        const command = animated
          ? NativeCollapsiblePagerCommands.setPage
          : NativeCollapsiblePagerCommands.setPageWithoutAnimation;
        const otherCommand = animated
          ? NativeCollapsiblePagerCommands.setPageWithoutAnimation
          : NativeCollapsiblePagerCommands.setPage;
        expect(command).toHaveBeenCalledTimes(1);
        expect(command).toHaveBeenCalledWith(expect.anything(), target);
        expect(otherCommand).not.toHaveBeenCalled();
      },
    );

    const renderTabPager = async () => {
      let renderer!: ReactTestRenderer.ReactTestRenderer;
      await ReactTestRenderer.act(() => {
        renderer = ReactTestRenderer.create(
          <NativeCollapsiblePagerView
            {...requiredProps}
            nativeTabBar={nativeTabBar}
          >
            {pages(nativeTabBar.items.length)}
          </NativeCollapsiblePagerView>,
          { createNodeMock: () => ({}) },
        );
      });
      const nativeHost = renderer.root.find(
        node => typeof node.props.retainedPages === 'string',
      );
      const press = async (position: number, runFrame = true) => {
        await ReactTestRenderer.act(() => {
          nativeHost.props.onNativeTabPress({
            nativeEvent: { position, key: nativeTabBar.items[position].key },
          });
        });
        if (runFrame) {
          await ReactTestRenderer.act(() => {
            jest.runOnlyPendingTimers();
          });
        }
      };
      const select = async (position: number) => {
        await ReactTestRenderer.act(() => {
          nativeHost.props.onPageSelected({ nativeEvent: { position } });
        });
      };
      return { renderer, press, select };
    };

    const lastCommand = () => {
      const animated = jest.mocked(NativeCollapsiblePagerCommands.setPage).mock
        .invocationCallOrder;
      const jumped = jest.mocked(
        NativeCollapsiblePagerCommands.setPageWithoutAnimation,
      ).mock.invocationCallOrder;
      const lastAnimated = Math.max(-1, ...animated);
      const lastJump = Math.max(-1, ...jumped);
      return lastAnimated > lastJump
        ? {
            animated: true,
            target: jest.mocked(NativeCollapsiblePagerCommands.setPage).mock
              .lastCall?.[1],
          }
        : {
            animated: false,
            target: jest.mocked(
              NativeCollapsiblePagerCommands.setPageWithoutAnimation,
            ).mock.lastCall?.[1],
          };
    };

    it.each([
      [2, true],
      [0, false],
      [1, false],
    ] as const)(
      'measures adjacency from an in-flight jump target when pressing %s',
      async (target, animated) => {
        const { renderer, press } = await renderTabPager();
        await press(3);
        expect(lastCommand()).toEqual({ animated: false, target: 3 });
        // Native has not reported page 3 yet; the wrapper state still says 0.
        await press(target);
        expect(lastCommand()).toEqual({ animated, target });
        await ReactTestRenderer.act(() => {
          renderer.unmount();
        });
      },
    );

    it('ignores a superseded target that never reached native', async () => {
      const { renderer, press } = await renderTabPager();
      await press(3, false);
      await press(2);
      expect(NativeCollapsiblePagerCommands.setPage).not.toHaveBeenCalled();
      expect(lastCommand()).toEqual({ animated: false, target: 2 });
      await ReactTestRenderer.act(() => {
        renderer.unmount();
      });
    });

    it('measures adjacency from the reported page after the transition', async () => {
      const { renderer, press, select } = await renderTabPager();
      await press(3);
      await select(3);
      await press(1);
      expect(lastCommand()).toEqual({ animated: false, target: 1 });
      await select(1);
      await press(0);
      expect(lastCommand()).toEqual({ animated: true, target: 0 });
      await ReactTestRenderer.act(() => {
        renderer.unmount();
      });
    });
  });
});

describe('CollapsiblePagerView web', () => {
  beforeAll(setWebTestRuntime);
  afterAll(restoreTestRuntime);
  afterEach(() => {
    jest.useRealTimers();
  });

  it('clamps the initial page and a selection after children shrink', async () => {
    const mounted: number[] = [];
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView
          {...requiredProps}
          initialPage={99}
          onMountedPagesChanged={({ position }) => mounted.push(position)}
        >
          {pages(3)}
        </CollapsiblePagerView>,
      );
    });

    const pager = renderer.root.findByType(CollapsiblePagerView).instance;
    expect(pager.state.selectedPage).toBe(2);

    await ReactTestRenderer.act(() => {
      renderer.update(
        <CollapsiblePagerView
          {...requiredProps}
          initialPage={99}
          onMountedPagesChanged={({ position }) => mounted.push(position)}
        >
          {pages(1)}
        </CollapsiblePagerView>,
      );
    });

    expect(pager.state.selectedPage).toBe(0);
    expect(mounted.at(-1)).toBe(0);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  it('clamps against rendered pages when conditional children are empty', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView {...requiredProps} initialPage={1}>
          {null}
          <View key="only-page" testID="only-page" />
        </CollapsiblePagerView>,
      );
    });

    const pager = renderer.root.findByType(CollapsiblePagerView).instance;
    expect(pager.state.selectedPage).toBe(0);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  it('normalizes page commands and stays settling until transition completion', async () => {
    jest.useFakeTimers();
    const selected: number[] = [];
    const states: string[] = [];
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView
          {...requiredProps}
          onPageSelected={event => selected.push(event.nativeEvent.position)}
          onPageScrollStateChanged={event =>
            states.push(event.nativeEvent.pageScrollState)
          }
        >
          {pages(3)}
        </CollapsiblePagerView>,
      );
    });
    const pager = renderer.root.findByType(CollapsiblePagerView).instance;

    await ReactTestRenderer.act(() => {
      pager.setPage(1.9);
    });
    expect(selected).toEqual([1]);
    expect(states).toEqual(['settling']);

    await ReactTestRenderer.act(() => {
      jest.advanceTimersByTime(320);
    });
    expect(states).toEqual(['settling', 'idle']);

    await ReactTestRenderer.act(() => {
      pager.setPage(Number.NaN);
      pager.setPage(5);
    });
    expect(selected).toEqual([1]);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  it('returns a horizontal cancelled gesture to idle and forwards View props', async () => {
    const states: string[] = [];
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView
          {...requiredProps}
          testID="collapsible-pager"
          accessibilityLabel="Accounts"
          onPageScrollStateChanged={event =>
            states.push(event.nativeEvent.pageScrollState)
          }
        >
          {pages(2)}
        </CollapsiblePagerView>,
      );
    });

    const track = renderer.root.find(
      node =>
        typeof node.props.onTouchStart === 'function' &&
        typeof node.props.onTouchMove === 'function',
    );
    await ReactTestRenderer.act(() => {
      track.props.onTouchStart({
        nativeEvent: { touches: [{ pageX: 100, pageY: 20 }] },
      });
      track.props.onTouchMove({
        nativeEvent: { touches: [{ pageX: 70, pageY: 21 }] },
      });
      track.props.onTouchCancel();
    });

    expect(states).toEqual(['dragging', 'idle']);
    expect(
      renderer.root.findByProps({ testID: 'collapsible-pager' }).props,
    ).toMatchObject({ accessibilityLabel: 'Accounts' });
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });
});

describe('PagerView web', () => {
  it('uses rendered pages for initial, update, command, and gesture bounds', async () => {
    const conditionalPages = (includeThird: boolean) => [
      <View key="page-0" testID="page-0" />,
      false,
      <View key="page-1" testID="page-1" />,
      includeThird ? <View key="page-2" testID="page-2" /> : null,
    ];
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <PagerView initialPage={2}>{conditionalPages(true)}</PagerView>,
      );
    });
    const pager = renderer.root.findByType(PagerView).instance;
    expect(pager.state.selectedPage).toBe(2);

    await ReactTestRenderer.act(() => {
      renderer.update(
        <PagerView initialPage={2}>{conditionalPages(false)}</PagerView>,
      );
    });
    expect(pager.state.selectedPage).toBe(1);

    await ReactTestRenderer.act(() => {
      pager.setPage(2);
    });
    expect(pager.state.selectedPage).toBe(1);

    const track = renderer.root.find(
      node =>
        typeof node.props.onTouchStart === 'function' &&
        typeof node.props.onTouchEnd === 'function',
    );
    await ReactTestRenderer.act(() => {
      track.props.onTouchStart({ nativeEvent: { pageX: 100, pageY: 20 } });
      track.props.onTouchEnd({ nativeEvent: { pageX: 20, pageY: 20 } });
    });
    expect(pager.state.selectedPage).toBe(1);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });

  it('clamps an initial index against conditional rendered pages', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <PagerView initialPage={2}>
          <View key="page-0" />
          {false}
          <View key="page-1" />
        </PagerView>,
      );
    });
    expect(
      renderer.root.findByType(PagerView).instance.state.selectedPage,
    ).toBe(1);
    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });
});

describe('CollapsiblePagerView Web owner bookkeeping', () => {
  beforeAll(setWebTestRuntime);
  afterAll(restoreTestRuntime);
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.restoreAllMocks();
    jest.useRealTimers();
  });

  it('saves the configured business owner when a caller remaps reordered keys', async () => {
    const keyed = (keys: readonly string[]) =>
      keys.map(key => <View key={key} />);
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView {...requiredProps} initialPage={1}>
          {keyed(['A', 'B', 'C'])}
        </CollapsiblePagerView>,
      );
    });
    const pager = renderer.root.findByType(CollapsiblePagerView).instance;
    const owner = { scrollTop: 300, isConnected: true } as HTMLElement;
    const nextOwner = {
      scrollTop: 0,
      getAttribute: () => 'A',
      addEventListener: () => {},
      removeEventListener: () => {},
      firstElementChild: null,
    } as unknown as HTMLElement;
    const state = pager as unknown as {
      pageOffsets: Map<string, number>;
      sharedHeaderOffset: number;
      configuredScrollElement: { key: string; element: HTMLElement } | null;
      pageHosts: Map<number, { querySelector: () => HTMLElement }>;
    };
    state.pageOffsets.set('.$A', 300);
    state.pageOffsets.set('.$B', 200);
    state.pageOffsets.set('.$C', 400);
    state.sharedHeaderOffset = 100;
    state.configuredScrollElement = { key: '.$B', element: owner };
    await ReactTestRenderer.act(() => {
      renderer.update(
        <CollapsiblePagerView {...requiredProps} initialPage={1}>
          {keyed(['B', 'A', 'C'])}
        </CollapsiblePagerView>,
      );
    });
    state.pageHosts.set(1, { querySelector: () => nextOwner });
    await ReactTestRenderer.act(() => pager.setPageWithoutAnimation(0));
    expect(pager.state.selectedPage).toBe(0);
    expect(state.sharedHeaderOffset).toBe(100);
    expect([...state.pageOffsets.entries()]).toEqual([
      ['.$A', 300],
      ['.$B', 200],
      ['.$C', 400],
    ]);
    // The fabricated configured owner has no DOM styles/listeners to restore.
    state.configuredScrollElement = null;
    state.pageHosts.clear();
    await ReactTestRenderer.act(() => renderer.unmount());
  });

  it('removes root input listeners and cancels queued work on unmount', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CollapsiblePagerView {...requiredProps}>
          {pages(1)}
        </CollapsiblePagerView>,
      );
    });
    const pager = renderer.root.findByType(CollapsiblePagerView).instance;
    const addEventListener = jest.fn();
    const removeEventListener = jest.fn();
    const root = {
      querySelector: () => null,
      addEventListener,
      removeEventListener,
    };
    const rendered = pager.render() as React.ReactElement<{
      ref: (value: unknown) => void;
    }>;
    rendered.props.ref(root);
    expect(addEventListener.mock.calls.map(([type]) => type)).toEqual([
      'ok-collapsible-pager-scroll-key-changed',
      'wheel',
      'touchstart',
      'touchmove',
      'touchend',
      'touchcancel',
    ]);
    const queued = jest.fn();
    const frame = requestAnimationFrame(queued);
    const cancel = jest.spyOn(globalThis, 'cancelAnimationFrame');
    const state = pager as unknown as {
      pendingTopFrame: {
        frame: number;
        element: HTMLElement;
        key: string;
      } | null;
    };
    state.pendingTopFrame = {
      frame,
      element: {} as HTMLElement,
      key: '.$page-0',
    };
    await ReactTestRenderer.act(() => renderer.unmount());
    for (const [type, handler] of addEventListener.mock.calls)
      expect(removeEventListener).toHaveBeenCalledWith(type, handler);
    expect(cancel).toHaveBeenCalledWith(frame);
    expect(state.pendingTopFrame).toBeNull();
    await ReactTestRenderer.act(() => jest.runOnlyPendingTimers());
    expect(queued).not.toHaveBeenCalled();
  });
});
