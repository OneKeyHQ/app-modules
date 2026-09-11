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
  setActionAnchorState: jest.fn(),
  setRefreshing: jest.fn(),
};
let mockOnSelectionDelta: (payloadJson: string) => void;
let mockOnReorder: (payloadJson: string) => void;
const mockWebEngine = {
  applySnapshot: jest.fn(),
  setVirtualizationEnabled: jest.fn(),
  updateCallbacks: jest.fn(),
  scrollToKey: jest.fn(),
  scrollToIndex: jest.fn(),
  destroy: jest.fn(),
};

jest.mock('react-native', () => {
  const ReactForMock = require('react') as typeof React;
  return {
    View: ReactForMock.forwardRef((props, ref) =>
      ReactForMock.createElement('div', { ...props, ref })
    ),
  };
});

jest.mock('../web/NativeListWebEngine', () => ({
  NativeListWebEngine: jest.fn(() => mockWebEngine),
}));

jest.mock('../web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(() => jest.fn()),
  canonicalNativeListAvatarUri: jest.fn((uri: string) => uri.trim()),
}));

jest.mock('@onekeyfe/react-native-image', () => ({
  OneKeyImageCache: { preload: jest.fn().mockResolvedValue(true) },
  OneKeyImageCachePolicy: {
    MEMORY: 'memory',
    DISK: 'disk',
    MEMORY_DISK: 'memory-disk',
  },
}));

jest.mock('react-native-nitro-modules', () => ({
  callback: (value: unknown) => value,
  getHostComponent: () =>
    function MockNativeListHost(props: {
      hybridRef: (ref: typeof mockNativeMethods) => void;
      onSelectionDelta: (payloadJson: string) => void;
      onReorder: (payloadJson: string) => void;
    }) {
      mockOnSelectionDelta = props.onSelectionDelta;
      mockOnReorder = props.onReorder;
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
      ref.current?.setActionAnchorState({
        token: 'list:1:1:1',
        open: true,
      });
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
    expect(mockNativeMethods.scrollToKey).toHaveBeenCalledWith(
      'a-0',
      true,
      'start',
      0,
      12
    );
    expect(mockNativeMethods.scrollToOffset).toHaveBeenCalledWith(320, false);
    expect(mockNativeMethods.scrollToEnd).toHaveBeenCalledWith(false);
    expect(mockNativeMethods.scrollToIndex).toHaveBeenNthCalledWith(
      4,
      4,
      true,
      'start',
      0,
      0
    );
    expect(mockNativeMethods.setRefreshing).toHaveBeenCalledWith(true);
    expect(mockNativeMethods.setActionAnchorState).toHaveBeenCalledWith(
      '{"token":"list:1:1:1","open":true}'
    );
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

  it.each(['native', 'web'])(
    '%s scrolls to the moved item by key before the reordered snapshot is echoed',
    async (platform) => {
      const { NativeList } = jest.requireActual(
        platform === 'native' ? '../NativeList' : '../NativeList.web'
      ) as typeof import('../NativeList');
      const ref = createRef<NativeListRef>();
      const reorderRows: NativeListSnapshot['rows'] = ['a', 'b', 'c'].map(
        (key) => ({
          type: 'identity',
          key,
          title: key,
          leading: { kind: 'icon', name: 'wallet' },
          draggable: true,
        })
      );
      const onReorder = jest.fn(() => {
        ref.current?.scrollToItem({
          item: reorderRows[0],
          animated: false,
          viewPosition: 0.5,
          viewOffset: 12,
        });
      });
      let renderer!: TestRenderer.ReactTestRenderer;
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList
            ref={ref}
            snapshot={{
              ...snapshot,
              layout: { kind: 'linear' },
              capabilities: { reorderable: true },
              rows: reorderRows,
            }}
            onReorder={onReorder}
          />,
          { createNodeMock: () => ({}) }
        );
      });
      const event = { key: 'a', fromIndex: 0, toIndex: 2 };
      if (platform === 'native') mockOnReorder(JSON.stringify(event));
      else {
        const { NativeListWebEngine } = jest.requireMock(
          '../web/NativeListWebEngine'
        ) as { NativeListWebEngine: jest.Mock };
        NativeListWebEngine.mock.calls[0][2].onReorder(event);
      }
      expect(onReorder).toHaveBeenCalledTimes(1);
      const methods = platform === 'native' ? mockNativeMethods : mockWebEngine;
      expect(methods.scrollToIndex).not.toHaveBeenCalled();
      if (platform === 'native')
        expect(methods.scrollToKey).toHaveBeenCalledWith(
          'a',
          false,
          'start',
          0.5,
          12
        );
      else
        expect(methods.scrollToKey).toHaveBeenCalledWith('a', {
          animated: false,
          alignment: 'start',
          viewPosition: 0.5,
          viewOffset: 12,
        });
      await act(async () => renderer.unmount());
    }
  );

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

  it('rejects invalid merged patches before dispatch and keeps the last valid snapshot', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    await act(async () => {
      TestRenderer.create(<NativeList ref={ref} snapshot={snapshot} />);
    });
    const selectedSnapshot: NativeListSnapshot = {
      ...snapshot,
      rows: [
        {
          type: 'identity',
          key: 'wallet',
          title: 'Wallet',
          leading: { kind: 'icon', name: 'wallet' },
        },
      ],
      selection: { mode: 'multiple', selectedKeys: ['wallet'] },
    };
    ref.current?.applySnapshot(selectedSnapshot);

    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { disabled: true } },
      ])
    ).toThrow('not selectable');
    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'missing', changes: { title: 'Missing' } },
      ])
    ).toThrow('unknown row key');
    expect(() =>
      ref.current?.applyPatches([
        { type: 'system', key: 'wallet', changes: { message: 'Wrong type' } },
      ])
    ).toThrow('not system');
    expect(() =>
      ref.current?.applySnapshot({
        ...selectedSnapshot,
        schemaVersion: 2,
      } as unknown as NativeListSnapshot)
    ).toThrow('schemaVersion');
    expect(mockNativeMethods.applyPatches).not.toHaveBeenCalled();
    expect(mockNativeMethods.applySnapshot).toHaveBeenCalledTimes(1);
    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { title: 'Updated' } },
      ])
    ).not.toThrow();
    expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
  });

  it('retains patches and selection across equivalent prop renders and resets on a new snapshot', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    let renderer!: TestRenderer.ReactTestRenderer;
    const selectableSnapshot: NativeListSnapshot = {
      ...snapshot,
      rows: [
        {
          type: 'identity',
          key: 'wallet',
          title: 'Wallet',
          leading: { kind: 'icon', name: 'wallet' },
          disabled: true,
        },
      ],
      selection: { mode: 'multiple', selectedKeys: [] },
    };
    await act(async () => {
      renderer = TestRenderer.create(
        <NativeList ref={ref} snapshot={selectableSnapshot} />
      );
    });
    ref.current?.applyPatches([
      { type: 'identity', key: 'wallet', changes: { disabled: false } },
    ]);
    ref.current?.reconcileSelection(['wallet']);
    await act(async () => {
      renderer.update(
        <NativeList ref={ref} snapshot={{ ...selectableSnapshot }} />
      );
    });
    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { disabled: true } },
      ])
    ).toThrow('not selectable');

    await act(async () => {
      renderer.update(
        <NativeList
          ref={ref}
          snapshot={{ ...selectableSnapshot, generation: 2 }}
        />
      );
    });
    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'wallet', changes: { title: 'Unselected' } },
      ])
    ).not.toThrow();
  });

  it('validates patches against native selection deltas before calling the consumer', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    const onSelectionDelta = jest.fn(() => {
      expect(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'wallet', changes: { disabled: true } },
        ])
      ).toThrow('not selectable');
    });
    await act(async () => {
      TestRenderer.create(
        <NativeList
          ref={ref}
          snapshot={{
            ...snapshot,
            rows: [
              {
                type: 'identity',
                key: 'wallet',
                title: 'Wallet',
                leading: { kind: 'icon', name: 'wallet' },
              },
            ],
            selection: { mode: 'multiple', selectedKeys: [] },
          }}
          onSelectionDelta={onSelectionDelta}
        />
      );
    });
    mockOnSelectionDelta(
      JSON.stringify({ addedKeys: ['wallet'], removedKeys: [], source: 'row' })
    );
    expect(onSelectionDelta).toHaveBeenCalledTimes(1);
    expect(mockNativeMethods.applyPatches).not.toHaveBeenCalled();
  });
});
