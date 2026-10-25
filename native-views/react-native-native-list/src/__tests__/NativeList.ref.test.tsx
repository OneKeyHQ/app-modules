import React, { createRef } from 'react';
import TestRenderer, { act } from 'react-test-renderer';
import type { NativeListRef } from '../NativeList.types';
import type { NativeListSnapshot, RowPatch } from '../models';
import { configureNativeListFonts } from '../fonts';
import { ScrollEdgeEffect } from '../ScrollEdgeEffect';

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
let mockOnRowAction: (payloadJson: string) => void;
const mockFontRenders = jest.fn();
const mockWebEngine = {
  setStickyInset: jest.fn(),
  currentSnapshot: jest.fn(() => snapshot),
  currentContentHeight: jest.fn(() => 0),
  setFontFamiliesJson: jest.fn(),
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
    Platform: { OS: 'ios', Version: 26 },
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
      testID?: string;
      fontFamiliesJson: string;
      snapshotJson: string;
      onSelectionDelta: (payloadJson: string) => void;
      onReorder: (payloadJson: string) => void;
      onRowAction: (payloadJson: string) => void;
    }) {
      mockFontRenders(props);
      mockOnSelectionDelta = props.onSelectionDelta;
      mockOnReorder = props.onReorder;
      mockOnRowAction = props.onRowAction;
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

  it('patches owned selector value rows with an unchanged undefined segment field', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    const input: NativeListSnapshot = {
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'linear' },
      rows: Array.from({ length: 1000 }, (_, index) => ({
        type: 'identity' as const,
        key: `token-${index}`,
        title: `T${index}`,
        leading: { kind: 'icon' as const, name: 'CryptoCoinOutline' },
        subtitle: 'Price',
        trailing: [{ kind: 'value' as const, text: '$1.00' }],
      })),
    };
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={input} />
        );
      });
      const callerRow = input.rows[500] as unknown as {
        subtitle: string | number;
      };
      callerRow.subtitle = 123;
      act(() =>
        ref.current?.applyPatches(
          Array.from({ length: 10 }, (_, index) => ({
            type: 'identity' as const,
            key: `token-${index * 10}`,
            changes: {
              subtitle: 'Price',
              subtitleSegments: undefined,
              trailing: [{ kind: 'value' as const, text: '$2.00' }],
            },
          }))
        )
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
      const patchJson = mockNativeMethods.applyPatches.mock
        .calls[0]?.[0] as string;
      expect(JSON.parse(patchJson)[0].changes).toEqual({
        subtitle: 'Price',
        trailing: [{ kind: 'value', text: '$2.00' }],
      });
    } finally {
      await act(async () => renderer?.unmount());
    }
  });

  it('isolates accepted patches from later caller mutations', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    const input: NativeListSnapshot = {
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'linear' },
      rows: [
        {
          type: 'identity',
          key: 'token',
          title: 'Token',
          leading: { kind: 'icon', name: 'CryptoCoinOutline' },
          subtitle: 'Price',
          trailing: [{ kind: 'value', text: '$1.00' }],
        },
      ],
    };
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={input} />
        );
      });
      expect(Object.isFrozen(input)).toBe(false);
      const trailing = [{ kind: 'value' as const, text: '$2.00' }];
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'token', changes: { trailing } },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
      (trailing[0] as { text: string | number }).text = 123;
      act(() =>
        ref.current?.applyPatches([
          {
            type: 'identity',
            key: 'token',
            changes: { subtitle: 'New price' },
          },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(2);
      expect(
        JSON.parse(mockNativeMethods.applyPatches.mock.calls[1][0])
      ).toMatchObject([{ key: 'token', changes: { subtitle: 'New price' } }]);
    } finally {
      await act(async () => renderer?.unmount());
    }
  });

  it('does not freeze or retain caller selection arrays on an unowned fallback', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    const input: NativeListSnapshot = {
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'linear' },
      selection: { mode: 'multiple', selectedKeys: [] },
      rows: [
        {
          type: 'identity',
          key: 'wallet',
          title: 'Wallet',
          leading: { kind: 'icon', name: 'WalletOutline' },
        },
      ],
    };
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={input} />
        );
      });
      const selectedKeys = ['wallet'];
      ref.current?.reconcileSelection(selectedKeys);
      selectedKeys.push('missing');
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'wallet', changes: { subtitle: 'One' } },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
      expect(Object.isFrozen(selectedKeys)).toBe(false);
      selectedKeys.push('still mutable');
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'wallet', changes: { title: 'Updated' } },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(2);
    } finally {
      await act(async () => renderer?.unmount());
    }
  });

  it('rejects unsafe cross-row patches without dispatch or partial acceptance', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    const input: NativeListSnapshot = {
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'sectioned' },
      selection: { mode: 'multiple', selectedKeys: ['item'] },
      rows: [
        {
          type: 'sectionHeader',
          key: 'header-a',
          sectionKey: 'a',
          title: 'A',
          indexTitle: 'A',
        },
        {
          type: 'identity',
          key: 'item',
          sectionKey: 'a',
          title: 'Item',
          leading: { kind: 'icon', name: 'CryptoCoinOutline' },
        },
        {
          type: 'sectionHeader',
          key: 'header-b',
          sectionKey: 'b',
          title: 'B',
          indexTitle: 'B',
        },
      ],
    };
    const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={input} />
        );
      });
      act(() =>
        ref.current?.applyPatches([
          {
            type: 'sectionHeader',
            key: 'header-b',
            changes: { indexTitle: 'A' },
          } as unknown as RowPatch,
        ])
      );
      act(() =>
        ref.current?.applyPatches([
          {
            type: 'identity',
            key: 'item',
            changes: { groupId: 'group' },
          } as unknown as RowPatch,
        ])
      );
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'item', changes: { disabled: true } },
        ])
      );
      expect(mockNativeMethods.applyPatches).not.toHaveBeenCalled();
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'item', changes: { subtitle: 'Valid' } },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
    } finally {
      await act(async () => renderer?.unmount());
      warning.mockRestore();
    }
  });

  it('validates the captured accessor value used by the native snapshot', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const ref = createRef<NativeListRef>();
    let reads = 0;
    const token = {
      type: 'identity' as const,
      key: 'accessor-token',
      title: 'Token',
      leading: { kind: 'icon' as const, name: 'CryptoCoinOutline' },
    };
    Object.defineProperty(token, 'subtitle', {
      enumerable: true,
      get: () => (reads++ === 0 ? 'Price' : 123),
    });
    const input: NativeListSnapshot = {
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'linear' },
      rows: [token],
    };
    let renderer!: TestRenderer.ReactTestRenderer;
    try {
      await act(async () => {
        renderer = TestRenderer.create(
          <NativeList ref={ref} snapshot={input} />
        );
      });
      const initialJson = mockFontRenders.mock.calls.at(-1)?.[0].snapshotJson;
      expect(JSON.parse(initialJson).rows[0].subtitle).toBe('Price');
      expect(reads).toBe(1);

      let patchReads = 0;
      const changes = Object.defineProperty({}, 'subtitle', {
        enumerable: true,
        get: () => (patchReads++ === 0 ? 'Updated' : 123),
      }) as { subtitle: string };
      act(() =>
        ref.current?.applyPatches([
          { type: 'identity', key: 'accessor-token', changes },
        ])
      );
      expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
      expect(
        JSON.parse(mockNativeMethods.applyPatches.mock.calls[0][0])[0].changes
          .subtitle
      ).toBe('Updated');
      expect(patchReads).toBe(1);

      mockNativeMethods.applyPatches.mockClear();
      const throwing = Object.defineProperty({}, 'subtitle', {
        enumerable: true,
        get: () => {
          throw new Error('private payload');
        },
      }) as { subtitle: string };
      const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
      try {
        act(() =>
          ref.current?.applyPatches([
            { type: 'identity', key: 'accessor-token', changes: throwing },
          ])
        );
        expect(mockNativeMethods.applyPatches).not.toHaveBeenCalled();
        expect(JSON.stringify(warning.mock.calls)).not.toContain(
          'private payload'
        );
      } finally {
        warning.mockRestore();
      }
    } finally {
      await act(async () => renderer?.unmount());
    }
  });

  it.each(['ios', 'android'])(
    'contains rejected production input on %s and keeps normalized native updates',
    async (platform) => {
      const environment = globalThis as typeof globalThis & {
        __DEV__: boolean;
      };
      const development = environment.__DEV__;
      const native = jest.requireMock('react-native') as {
        Platform: { OS: string };
      };
      const previousPlatform = native.Platform.OS;
      const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
      environment.__DEV__ = false;
      native.Platform.OS = platform;
      const { NativeList } = jest.requireActual(
        '../NativeList'
      ) as typeof import('../NativeList');
      const ref = createRef<NativeListRef>();
      const key = ` ${'k'.repeat(257)} `;
      const valid: NativeListSnapshot = {
        schemaVersion: 1,
        generation: 1,
        layout: { kind: 'linear' },
        rows: [
          {
            type: 'identity',
            key,
            title: 'T'.repeat(4097),
            leading: {
              kind: 'token',
              fallbackText: 'T',
              image: { uri: '', width: 32, height: 32 },
            },
            style: { title: { token: '$bodySm' } },
          },
        ],
      };
      const invalid = { ...valid, rows: [valid.rows[0], valid.rows[0]] };
      let renderer: TestRenderer.ReactTestRenderer | undefined;
      try {
        await act(async () => {
          renderer = TestRenderer.create(
            <NativeList ref={ref} snapshot={invalid} testID="validation-mode" />
          );
        });
        const latestSnapshot = () =>
          JSON.parse(
            mockFontRenders.mock.calls
              .filter(([props]) => props.testID === 'validation-mode')
              .slice(-1)[0][0].snapshotJson
          ) as NativeListSnapshot;
        expect(latestSnapshot().rows).toEqual([]);
        await act(async () => {
          renderer?.update(
            <NativeList ref={ref} snapshot={valid} testID="validation-mode" />
          );
        });
        expect(latestSnapshot().rows[0]).toMatchObject({
          key,
          style: { title: { fontSize: 12, lineHeight: 16 } },
          leading: { fallbackText: 'T' },
        });
        const acceptedJson = JSON.stringify(latestSnapshot());
        await act(async () => {
          renderer?.update(
            <NativeList ref={ref} snapshot={invalid} testID="validation-mode" />
          );
        });
        expect(JSON.stringify(latestSnapshot())).toBe(acceptedJson);
        expect(() =>
          ref.current?.applyPatches([
            {
              type: 'identity',
              key: 'missing-sensitive-key',
              changes: { title: 'Rejected' },
            },
          ])
        ).not.toThrow();
        expect(mockNativeMethods.applyPatches).not.toHaveBeenCalled();
        ref.current?.applyPatches([
          {
            type: 'identity',
            key,
            changes: { style: { title: { token: '$bodyLg' } } },
          },
        ]);
        expect(
          JSON.parse(mockNativeMethods.applyPatches.mock.calls[0][0])
        ).toMatchObject([
          {
            key,
            changes: { style: { title: { fontSize: 16, lineHeight: 24 } } },
          },
        ]);
        expect(JSON.stringify(warning.mock.calls)).not.toContain(
          'missing-sensitive-key'
        );
      } finally {
        await act(async () => renderer?.unmount());
        environment.__DEV__ = development;
        native.Platform.OS = previousPlatform;
        warning.mockRestore();
      }
    }
  );

  it('forwards either edge style and disables it when cleared', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    let renderer: TestRenderer.ReactTestRenderer;
    const renderList = (effect?: ScrollEdgeEffect) => (
      <NativeList
        snapshot={snapshot}
        topOverlay={<React.Fragment />}
        topScrollEdgeEffect={effect}
      />
    );
    await act(async () => {
      renderer = TestRenderer.create(renderList(ScrollEdgeEffect.Hard));
    });
    expect(mockFontRenders.mock.calls.at(-1)?.[0].topScrollEdgeEffect).toBe(
      'hard'
    );
    await act(async () => renderer.update(renderList(ScrollEdgeEffect.Soft)));
    expect(mockFontRenders.mock.calls.at(-1)?.[0].topScrollEdgeEffect).toBe(
      'soft'
    );
    await act(async () => renderer.update(renderList()));
    expect(mockFontRenders.mock.calls.at(-1)?.[0].topScrollEdgeEffect).toBe(
      'none'
    );
    await act(async () => renderer.unmount());
  });

  it('updates both mounted lists from the runtime store without replaying snapshots', async () => {
    const { NativeList } = jest.requireActual(
      '../NativeList'
    ) as typeof import('../NativeList');
    const first = createRef<NativeListRef>();
    let renderer: TestRenderer.ReactTestRenderer;
    await act(async () => {
      configureNativeListFonts({ regular: 'First' });
      renderer = TestRenderer.create(
        <>
          <NativeList ref={first} testID="font-first" snapshot={snapshot} />
          <NativeList testID="font-second" snapshot={snapshot} />
        </>
      );
    });
    const latest = (id: string) =>
      mockFontRenders.mock.calls
        .map(([props]) => props)
        .filter((props) => props.testID === id)
        .at(-1);
    const originalSnapshot = latest('font-first').snapshotJson;
    expect(latest('font-first').fontFamiliesJson).toBe('{"regular":"First"}');
    expect(latest('font-second').fontFamiliesJson).toBe('{"regular":"First"}');
    act(() =>
      first.current?.applyPatches([
        { key: 'a-0', type: 'system', changes: { message: 'Patched' } },
      ])
    );
    mockNativeMethods.applySnapshot.mockClear();
    await act(async () => configureNativeListFonts({ bold: 'Changed' }));
    expect(latest('font-first').fontFamiliesJson).toBe('{"bold":"Changed"}');
    expect(latest('font-second').fontFamiliesJson).toBe('{"bold":"Changed"}');
    expect(latest('font-first').snapshotJson).toBe(originalSnapshot);
    expect(mockNativeMethods.applySnapshot).not.toHaveBeenCalled();
    expect(mockNativeMethods.applyPatches).toHaveBeenCalledTimes(1);
    const renders = mockFontRenders.mock.calls.length;
    await act(async () => configureNativeListFonts({ bold: 'Changed' }));
    expect(mockFontRenders).toHaveBeenCalledTimes(renders);
    await act(async () => renderer!.unmount());
    const unmountedRenders = mockFontRenders.mock.calls.length;
    await act(async () => configureNativeListFonts({}));
    expect(mockFontRenders).toHaveBeenCalledTimes(unmountedRenders);
  });

  it.each([true, false])(
    'consumes native container refresh separately from row actions (refresh callback: %s)',
    async (hasRefreshCallback) => {
      const { NativeList } = jest.requireActual(
        '../NativeList'
      ) as typeof import('../NativeList');
      const onRefresh = jest.fn();
      const onRowAction = jest.fn();
      await act(async () => {
        TestRenderer.create(
          <NativeList
            snapshot={snapshot}
            onRefresh={hasRefreshCallback ? onRefresh : undefined}
            onRowAction={onRowAction}
          />
        );
      });
      act(() => mockOnRowAction('{"actionKey":"nativeList.refresh"}'));
      expect(onRefresh).toHaveBeenCalledTimes(hasRefreshCallback ? 1 : 0);
      expect(onRowAction).not.toHaveBeenCalled();

      const rowAction = { rowKey: 'a-0', actionKey: 'nativeList.refresh' };
      act(() => mockOnRowAction(JSON.stringify(rowAction)));
      expect(onRowAction).toHaveBeenCalledWith(rowAction);
      expect(onRefresh).toHaveBeenCalledTimes(hasRefreshCallback ? 1 : 0);
    }
  );

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
          { createNodeMock: () => ({ style: {} }) }
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
    ).not.toThrow();
    expect(() =>
      ref.current?.applyPatches([
        { type: 'identity', key: 'missing', changes: { title: 'Missing' } },
      ])
    ).not.toThrow();
    expect(() =>
      ref.current?.applyPatches([
        { type: 'system', key: 'wallet', changes: { message: 'Wrong type' } },
      ])
    ).not.toThrow();
    expect(() =>
      ref.current?.applySnapshot({
        ...selectedSnapshot,
        schemaVersion: 2,
      } as unknown as NativeListSnapshot)
    ).not.toThrow();
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
    ).not.toThrow();

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
      ).not.toThrow();
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
