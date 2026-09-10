import React from 'react';
import { View } from 'react-native';
import ReactTestRenderer from 'react-test-renderer';

import { CollapsiblePagerView } from '../../../native-views/react-native-pager-view/src/CollapsiblePagerView.web';

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

describe('CollapsiblePagerView web', () => {
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
