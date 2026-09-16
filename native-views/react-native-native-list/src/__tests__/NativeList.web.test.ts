import type { IdentityRow, NativeListSnapshot, RowModel } from '../models';
import {
  WEB_LIST_CSS,
  WEB_REORDER_ANIMATION,
  canStartWebWalletGroupReorder,
  cancelWebReorderRows,
  computeWebListLayout,
  estimateWebRowHeight,
  hasExceededWebReorderMouseThreshold,
  isWebRowReorderable,
  moveWebReorderRow,
  resolveWebCollapsiblePagerRawOffset,
  resolveWebCollapsiblePagerScrollMetrics,
  visibleWebLayoutItems,
  webReorderAutoScrollVelocity,
  webReorderEventForRows,
  webLayoutItemsForMount,
  webActionAnchorPayload,
  webRowRenderSignature,
  webWalletGroupReorderBadge,
} from '../web/NativeListWebEngine';

jest.mock('../web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(),
  canonicalNativeListAvatarUri: jest.fn(),
}));

const image = {
  uri: 'data:image/png;base64,AA==',
  width: 40,
  height: 40,
} as const;

const rows: readonly RowModel[] = [
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
    indexTitle: '#',
    title: 'Assets',
  },
  {
    type: 'identity',
    key: 'identity',
    sectionKey: 'a',
    leading: { kind: 'token', image, networkImage: image },
    title: 'Identity',
    subtitle: 'Subtitle',
    tertiary: 'Tertiary',
    trailing: [
      {
        kind: 'checkbox',
        state: 'unchecked',
        target: { scope: 'row' },
      },
    ],
  },
  {
    type: 'rail',
    key: 'rail',
    sectionKey: 'a',
    visual: { kind: 'icon', name: 'star' },
    title: 'Rail',
    draggable: true,
  },
  {
    type: 'activity',
    key: 'activity',
    sectionKey: 'a',
    leading: { kind: 'icon', name: 'swap' },
    title: 'Activity',
    footerActions: [{ key: 'retry', label: 'Retry' }],
  },
  {
    type: 'message',
    key: 'message',
    sectionKey: 'a',
    title: 'Message',
    body: 'A bounded multi-line notification body.',
    time: '12:00',
  },
  {
    type: 'dataRow',
    key: 'data',
    sectionKey: 'a',
    columns: [
      { key: 'asset', text: 'BTC', secondaryText: 'Bitcoin' },
      { key: 'price', text: '$1', alignment: 'end' },
    ],
  },
  {
    type: 'mediaTile',
    key: 'media',
    sectionKey: 'a',
    variant: 'gallery',
    image,
    title: 'Media',
  },
  {
    type: 'metricCard',
    key: 'metric',
    sectionKey: 'a',
    title: 'Metric',
    value: '$1',
  },
  {
    type: 'action',
    key: 'action',
    title: 'Action',
    actionKey: 'action.run',
    icon: { kind: 'icon', name: 'plus' },
  },
  {
    type: 'system',
    key: 'system',
    variant: 'retry',
    message: 'Retry',
    actionKey: 'retry',
  },
];

function snapshot(
  layout: NativeListSnapshot['layout'],
  sourceRows: readonly RowModel[] = rows
): NativeListSnapshot {
  return {
    schemaVersion: 1,
    generation: 1,
    layout,
    rows: sourceRows,
    selection: {
      mode: 'multiple',
      selectedKeys: [],
      rowPressToggles: true,
    },
    capabilities: {
      reorderable: true,
      sectionIndex: { enabled: true },
    },
  };
}

describe('NativeList pure DOM web layout', () => {
  it('uses list-relative offsets inside a collapsible pager viewport', () => {
    expect(resolveWebCollapsiblePagerScrollMetrics(134, 800, 134, 120)).toEqual(
      {
        offset: 0,
        viewportLength: 680,
      }
    );
    expect(resolveWebCollapsiblePagerScrollMetrics(734, 800, 134, 120)).toEqual(
      {
        offset: 600,
        viewportLength: 680,
      }
    );
    expect(resolveWebCollapsiblePagerRawOffset(600, 134)).toBe(734);
    expect(resolveWebCollapsiblePagerScrollMetrics(-20, 80, -1, 120)).toEqual({
      offset: 0,
      viewportLength: 0,
    });
  });

  it('distinguishes first-load skeleton and pagination without changing legacy loading height', () => {
    const loading = {
      type: 'system',
      key: 'loading',
      variant: 'loading',
      presentation: 'market',
    } as const;
    const list = snapshot({ kind: 'linear' }, [loading]);
    expect(estimateWebRowHeight(loading, list, 402)).toBe(68);
    expect(
      estimateWebRowHeight({ ...loading, loadingStyle: 'skeleton' }, list, 402)
    ).toBe(56);
    expect(
      estimateWebRowHeight({ ...loading, loadingStyle: 'spinner' }, list, 402)
    ).toBe(52);
  });

  it('captures logical window geometry and preserves media-close source', () => {
    expect(
      webActionAnchorPayload(
        'list:3:9:2',
        { left: 12, top: 24, width: 36, height: 36 },
        'mediaClose',
        3,
        'rtl'
      )
    ).toEqual({
      token: 'list:3:9:2',
      windowRect: { x: 12, y: 24, width: 36, height: 36 },
      source: 'mediaClose',
      slot: undefined,
      generation: 3,
      layoutDirection: 'rtl',
    });
  });

  it('matches native template heights for specialized examples', () => {
    const linear = snapshot({ kind: 'linear' });
    expect(estimateWebRowHeight(rows[0], linear, 320)).toBe(68);
    expect(estimateWebRowHeight(rows[2], linear, 320)).toBe(72);
    expect(estimateWebRowHeight(rows[4], linear, 320)).toBe(100);
    expect(estimateWebRowHeight(rows[6], linear, 320)).toBe(60);
    expect(estimateWebRowHeight(rows[8], linear, 320)).toBe(132);
    expect(estimateWebRowHeight(rows[9], linear, 320)).toBe(60);
    expect(estimateWebRowHeight(rows[10], linear, 320)).toBe(44);
    expect(
      estimateWebRowHeight(
        { ...rows[2], presentation: 'accountSelector' } as RowModel,
        linear,
        320
      )
    ).toBe(58);
  });

  it('reorders wallet-sidebar identities without a visible drag accessory', () => {
    const walletRows: readonly RowModel[] = [
      {
        type: 'identity',
        key: 'wallet-a',
        presentation: 'walletSidebar',
        leading: { kind: 'wallet', fallbackText: 'A' },
        title: 'Wallet A',
        draggable: true,
      },
      {
        type: 'identity',
        key: 'wallet-b',
        presentation: 'walletSidebar',
        leading: { kind: 'wallet', fallbackText: 'B' },
        title: 'Wallet B',
        draggable: true,
      },
    ];
    const reorderable = snapshot({ kind: 'linear' }, walletRows);

    expect(walletRows[0]?.type).toBe('identity');
    expect(
      walletRows[0] && isWebRowReorderable(reorderable, walletRows[0])
    ).toBe(true);
    expect('trailing' in (walletRows[0] ?? {})).toBe(false);
    expect(moveWebReorderRow(walletRows, 0, 1).map((row) => row.key)).toEqual([
      'wallet-b',
      'wallet-a',
    ]);
    expect(moveWebReorderRow(walletRows, 0, 0)).toBe(walletRows);
    expect(
      isWebRowReorderable(reorderable, {
        ...(walletRows[0] as IdentityRow),
        draggable: false,
      })
    ).toBe(false);
  });

  it('uses deterministic hardware wallet group height and atomic reorder', () => {
    const parent: IdentityRow = {
      type: 'identity',
      key: 'hardware',
      presentation: 'walletSidebar',
      leading: { kind: 'wallet', fallbackText: 'H' },
      title: 'Hardware',
    };
    const group: RowModel = {
      type: 'walletGroup',
      key: parent.key,
      parent,
      children: [
        {
          ...parent,
          key: 'hidden-1',
          title: 'Hidden 1',
          draggable: true,
        },
        { ...parent, key: 'hidden-2', title: 'Hidden 2' },
        {
          ...parent,
          key: 'add-hidden',
          title: 'Add hidden wallet',
          draggable: false,
        },
      ],
      draggable: true,
    };
    const peer: RowModel = { ...parent, key: 'peer', title: 'Peer' };
    const reorderable = snapshot({ kind: 'linear' }, [group, peer]);

    expect(estimateWebRowHeight(group, reorderable, 136)).toBe(308);
    expect(isWebRowReorderable(reorderable, group)).toBe(true);
    expect(moveWebReorderRow([group, peer], 0, 1)).toEqual([peer, group]);
    expect(webWalletGroupReorderBadge(group)).toBe('+2');
    expect(canStartWebWalletGroupReorder(group, 'hidden-1')).toBe(true);
    expect(canStartWebWalletGroupReorder(group, 'hidden-2')).toBe(true);
    expect(canStartWebWalletGroupReorder(group, 'add-hidden')).toBe(false);

    const compact = computeWebListLayout(reorderable, 136, 640, group.key);
    expect(compact.items.map((item) => item.height)).toEqual([68, 68]);
    expect(compact.items[1]?.y).toBe(68);

    const movedRows = moveWebReorderRow([group, peer], 0, 1);
    const movedCompact = computeWebListLayout(
      snapshot({ kind: 'linear' }, movedRows),
      136,
      640,
      group.key
    );
    expect(movedCompact.items.map((item) => item.height)).toEqual([68, 68]);
    expect(movedCompact.items[1]?.key).toBe(group.key);
  });

  it('models live wallet movement, cancellation, and stable final anchors', () => {
    const reorderRows: readonly RowModel[] = ['a', 'b', 'c', 'd'].map(
      (key) => ({
        type: 'identity',
        key,
        leading: { kind: 'wallet', fallbackText: key },
        title: key,
        draggable: true,
      })
    );
    const moved = moveWebReorderRow(reorderRows, 0, 2);

    expect(moved.map((row) => row.key)).toEqual(['b', 'c', 'a', 'd']);
    expect(reorderRows.map((row) => row.key)).toEqual(['a', 'b', 'c', 'd']);
    expect(cancelWebReorderRows(reorderRows)).toBe(reorderRows);
    expect(webReorderEventForRows(reorderRows, moved, 'a')).toEqual({
      key: 'a',
      fromIndex: 0,
      toIndex: 2,
      beforeKey: 'c',
      afterKey: 'd',
    });
  });

  it('uses a dampened quadratic edge curve for deep-list auto-scroll', () => {
    const top = 61;
    const bottom = 605;
    const edgeStart = bottom - (bottom - top) * 0.25;
    const maxZone = bottom - (bottom - top) * 0.05;

    expect(webReorderAutoScrollVelocity(300, top, bottom, 1_200)).toBe(0);
    expect(webReorderAutoScrollVelocity(edgeStart, top, bottom, 1_200)).toBe(1);
    expect(
      webReorderAutoScrollVelocity(
        (edgeStart + maxZone) / 2,
        top,
        bottom,
        1_200
      )
    ).toBeCloseTo(7);
    expect(
      webReorderAutoScrollVelocity(maxZone, top, bottom, 1_200)
    ).toBeCloseTo(28);
    expect(webReorderAutoScrollVelocity(maxZone, top, bottom, 0)).toBe(1);
    expect(webReorderAutoScrollVelocity(maxZone, top, bottom, 359)).toBe(1);
    expect(webReorderAutoScrollVelocity(maxZone, top, bottom, 360)).toBe(0);
    expect(webReorderAutoScrollVelocity(maxZone, top, bottom, 780)).toBe(7);
    expect(
      webReorderAutoScrollVelocity(maxZone, top, bottom, 1_200)
    ).toBeCloseTo(28);
  });

  it('uses react-beautiful-dnd mouse sloppiness per axis', () => {
    expect(hasExceededWebReorderMouseThreshold(3.6, 3.6)).toBe(false);
    expect(hasExceededWebReorderMouseThreshold(5, 0)).toBe(true);
    expect(hasExceededWebReorderMouseThreshold(0, -5)).toBe(true);
  });

  it('matches the wallet sidebar out-of-way and overridden drop animation', () => {
    expect(WEB_REORDER_ANIMATION).toEqual({
      outOfWayDurationMs: 200,
      outOfWayTimingFunction: 'cubic-bezier(0.2, 0, 0, 1)',
      dropDurationMs: 80,
      dropTimingFunction: 'ease',
    });
  });

  it('windows 5,000 rows instead of materializing every row', () => {
    const manyRows: RowModel[] = Array.from({ length: 5_000 }, (_, index) => ({
      type: 'identity',
      key: 'row-' + String(index),
      leading: { kind: 'icon', name: 'row' },
      title: 'Row ' + String(index),
    }));
    const layout = computeWebListLayout(
      snapshot({ kind: 'linear' }, manyRows),
      360,
      640
    );
    const visible = visibleWebLayoutItems(layout, 120_000, 640, 640);
    expect(layout.items).toHaveLength(5_000);
    expect(visible.length).toBeGreaterThan(0);
    expect(visible.length).toBeLessThan(40);
    expect(visible[0].index).toBeGreaterThan(2_000);
    expect(webLayoutItemsForMount(layout, 120_000, 640, true, 640)).toEqual(
      visible
    );
    expect(
      webLayoutItemsForMount(layout, 120_000, 640, false, 640)
    ).toHaveLength(5_000);

    const grid = computeWebListLayout(
      snapshot({ kind: 'grid', gridColumns: 2 }, manyRows),
      360,
      640
    );
    const visibleGrid = visibleWebLayoutItems(grid, 60_000, 640, 640);
    expect(visibleGrid.length).toBeGreaterThan(0);
    expect(visibleGrid.length).toBeLessThan(80);
    expect(visibleGrid[0].index).toBeGreaterThan(1_900);
  });

  it('does not invalidate image rows for controlled selection echoes', () => {
    const identity = rows[2];
    if (identity.type !== 'identity') throw new Error('Invalid fixture');
    const checked = {
      ...identity,
      selected: true,
      trailing: [
        {
          kind: 'checkbox' as const,
          state: 'checked' as const,
          target: { scope: 'row' as const },
        },
      ],
    };
    expect(webRowRenderSignature(checked)).toBe(
      webRowRenderSignature(identity)
    );
    expect(webRowRenderSignature({ ...identity, title: 'Changed' })).not.toBe(
      webRowRenderSignature(identity)
    );
  });

  it('keeps the default row cursor from disarming interactive controls', () => {
    const rule = WEB_LIST_CSS.split('\n').find(
      (line) =>
        line.includes('.ok-native-list-wallet-row') &&
        line.includes('cursor:default')
    );
    if (!rule) throw new Error('Missing default row cursor rule');
    const selectors = rule
      .slice(0, rule.indexOf('{'))
      .split(',')
      .map((selector) => selector.trim());
    expect(selectors).toEqual([
      '.ok-native-list-root .ok-native-list-item>.ok-native-list-wallet-row',
      '.ok-native-list-root .ok-native-list-wallet-member>.ok-native-list-wallet-row',
      '.ok-native-list-root .ok-native-list-item>.ok-native-list-account-row',
      '.ok-native-list-root .ok-native-list-item>.ok-native-list-account-action-row',
      '.ok-native-list-wallet-member',
    ]);
    // cursor inherits, so the row surface already covers its plain children, and a
    // descendant selector would outrank the controls' own pointer rules.
    expect(selectors.filter((selector) => selector.endsWith('*'))).toEqual([]);
    for (const control of [
      'ok-native-list-icon-button',
      'ok-native-list-checkbox',
      'ok-native-list-action-button',
    ]) {
      expect(
        new RegExp(`\\.${control}\\{[^}]*cursor:pointer`).test(WEB_LIST_CSS)
      ).toBe(true);
    }
  });
});
