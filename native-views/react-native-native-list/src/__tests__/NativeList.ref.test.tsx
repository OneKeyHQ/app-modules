import React, { createRef } from 'react';
import TestRenderer, { act } from 'react-test-renderer';
import type { NativeListRef } from '../NativeList.types';
import type { NativeListSnapshot } from '../models';

const mockNativeMethods = {
  applySnapshot: jest.fn(),
  applyPatches: jest.fn(),
  reconcileSelection: jest.fn(),
  scrollToKey: jest.fn(),
  scrollToIndex: jest.fn(),
  scrollToOffset: jest.fn(),
  scrollToEnd: jest.fn(),
  setRefreshing: jest.fn(),
};

jest.mock('react-native-nitro-modules', () => ({
  callback: (value: unknown) => value,
  getHostComponent: () =>
    function MockNativeListHost(props: {
      hybridRef: (ref: typeof mockNativeMethods) => void;
    }) {
      const ReactForMock = require('react') as typeof React;
      ReactForMock.useEffect(() => props.hybridRef(mockNativeMethods), [props]);
      return null;
    },
}));

const rows: NativeListSnapshot['rows'] = [
  {
    type: 'sectionHeader',
    key: 'summary',
    sectionKey: 'summary',
    variant: 'summary',
    title: 'Summary',
  },
  {
    type: 'sectionHeader',
    key: 'header-a',
    sectionKey: 'a',
    title: 'A',
  },
  {
    type: 'system',
    key: 'a-0',
    sectionKey: 'a',
    variant: 'noMatch',
    message: 'A0',
  },
  {
    type: 'sectionHeader',
    key: 'header-b',
    sectionKey: 'b',
    title: 'B',
  },
  {
    type: 'system',
    key: 'b-0',
    sectionKey: 'b',
    variant: 'end',
    message: 'B0',
  },
];

const snapshot: NativeListSnapshot = {
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'sectioned' },
  rows,
};

describe('NativeList imperative ref', () => {
  beforeEach(() => jest.clearAllMocks());

  it('dispatches initial position and every RN-compatible scroll API', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    await act(async () => {
      TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={snapshot}
          initialScrollIndex={2}
          initialScrollViewPosition={0.5}
          initialScrollViewOffset={8}
        />
      );
    });

    expect(mockNativeMethods.scrollToIndex).toHaveBeenCalledWith(
      2,
      false,
      'start',
      0.5,
      8
    );

    act(() => {
      ref.current?.applySnapshot({ ...snapshot, generation: 2 });
      ref.current?.applyPatches([
        { key: 'a-0', type: 'system', changes: { message: 'Updated' } },
      ]);
      ref.current?.reconcileSelection(['a-0']);
      ref.current?.scrollToIndex(2, false, 'end');
      ref.current?.scrollToIndex({ index: 2, viewPosition: 0.25 });
      ref.current?.scrollToKey('b-0', false, 'center');
      ref.current?.scrollToKey({
        key: 'a-0',
        viewPosition: 0.75,
        viewOffset: 4,
      });
      ref.current?.scrollToItem({ item: rows[2], viewOffset: 12 });
      ref.current?.scrollToOffset({ offset: 320, animated: false });
      ref.current?.scrollToEnd({ animated: false });
      ref.current?.scrollToLocation({ sectionIndex: 1, itemIndex: 0 });
      ref.current?.setRefreshing(true);
    });

    expect(mockNativeMethods.applySnapshot).toHaveBeenCalledTimes(1);
    expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
    expect(mockNativeMethods.reconcileSelection).toHaveBeenCalledWith(
      '["a-0"]'
    );

    expect(mockNativeMethods.scrollToIndex).toHaveBeenNthCalledWith(
      2,
      2,
      false,
      'end',
      1,
      0
    );
    expect(mockNativeMethods.scrollToIndex).toHaveBeenNthCalledWith(
      3,
      2,
      true,
      'start',
      0.25,
      0
    );
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledWith(
      'b-0',
      false,
      'center',
      0.5,
      0
    );
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledWith(
      'a-0',
      true,
      'start',
      0.75,
      4
    );
    expect(mockNativeMethods.scrollToIndex).toHaveBeenNthCalledWith(
      4,
      2,
      true,
      'start',
      0,
      12
    );
    expect(mockNativeMethods.scrollToOffset).toHaveBeenCalledWith(320, false);
    expect(mockNativeMethods.scrollToEnd).toHaveBeenCalledWith(false);
    expect(mockNativeMethods.scrollToIndex).toHaveBeenNthCalledWith(
      5,
      4,
      true,
      'start',
      0,
      0
    );
    expect(mockNativeMethods.setRefreshing).toHaveBeenCalledWith(true);
  });

  it('reports invalid index and location requests', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const onScrollToIndexFailed = jest.fn();
    const ref = createRef<NativeListRef>();
    await act(async () => {
      TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={snapshot}
          onScrollToIndexFailed={onScrollToIndexFailed}
        />
      );
    });

    act(() => {
      ref.current?.scrollToIndex({ index: 99 });
      ref.current?.scrollToLocation({ sectionIndex: 9, itemIndex: 0 });
      ref.current?.scrollToLocation({ sectionIndex: 1, itemIndex: 1 });
      ref.current?.scrollToItem({
        item: { ...rows[2], key: 'missing' },
      });
    });

    expect(onScrollToIndexFailed).toHaveBeenNthCalledWith(
      1,
      expect.objectContaining({ index: 99, reason: 'index-out-of-range' })
    );
    expect(onScrollToIndexFailed).toHaveBeenNthCalledWith(
      2,
      expect.objectContaining({ reason: 'section-out-of-range' })
    );
    expect(onScrollToIndexFailed).toHaveBeenNthCalledWith(
      3,
      expect.objectContaining({ reason: 'item-out-of-range' })
    );
    expect(onScrollToIndexFailed).toHaveBeenNthCalledWith(
      4,
      expect.objectContaining({ reason: 'item-not-found' })
    );
  });

  it('applies an initial stable key only once', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    let renderer!: TestRenderer.ReactTestRenderer;
    await act(async () => {
      renderer = TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={snapshot}
          initialScrollKey="b-0"
          initialScrollViewOffset={16}
        />
      );
    });
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledTimes(1);
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledWith(
      'b-0',
      false,
      'start',
      0,
      16
    );

    await act(async () => {
      renderer.update(
        <NativeList
          ref={ref}
          snapshot={{ ...snapshot, generation: 2 }}
          initialScrollKey="b-0"
          initialScrollViewOffset={16}
        />
      );
    });
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledTimes(1);
  });
});
