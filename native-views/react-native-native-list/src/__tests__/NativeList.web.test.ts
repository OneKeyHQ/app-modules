import type {
  IdentityRow,
  MarketRow,
  NativeListSnapshot,
  RowModel,
} from '../models';
import {
  WEB_LIST_CSS,
  WEB_REORDER_ANIMATION,
  cancelWebReorderRows,
  computeWebListLayout,
  estimateWebRowHeight,
  hasExceededWebReorderMouseThreshold,
  isWebRowReorderable,
  isWebMarketQuotePatch,
  moveWebReorderRow,
  resolveWebMarketLayoutStyle,
  resolveWebCollapsiblePagerRawOffset,
  resolveWebCollapsiblePagerScrollMetrics,
  visibleWebLayoutItems,
  webReorderAutoScrollVelocity,
  webReorderEventForRows,
  webLayoutItemsForMount,
  webActionAnchorPayload,
  webMarketImageBindDeltaForPatches,
  webRowRenderSignature,
  webWalletGroupReorderBadge,
  WEB_MARKET_VERIFIED_PATH,
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

const market: MarketRow = {
  type: 'market',
  key: 'market-btc',
  variant: 'token',
  leading: {
    kind: 'token',
    image,
    networkImage: { ...image, width: 16, height: 16 },
  },
  title: 'BTC',
  subtitle: '$1.23B',
  price: '$64,230.00',
  change: { text: '+2.40%', tone: 'positive' },
  badges: [{ key: 'community', iconName: 'verified', tone: 'success' }],
};

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
    expect(resolveWebCollapsiblePagerScrollMetrics(134, 800, 134, 120)).toEqual({
      offset: 0,
      viewportLength: 680,
    });
    expect(resolveWebCollapsiblePagerScrollMetrics(734, 800, 134, 120)).toEqual({
      offset: 600,
      viewportLength: 680,
    });
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

  it('keeps Market defaults, verified glyph, patch boundary, and style reset deterministic', () => {
    const linear = snapshot({ kind: 'linear' }, [market]);
    expect(estimateWebRowHeight(market, linear, 390)).toBe(68);
    expect(
      estimateWebRowHeight(
        { ...market, key: 'stock', variant: 'stock' },
        linear,
        390
      )
    ).toBe(72);
    expect(WEB_LIST_CSS).toContain('width:80px;height:32px');
    expect(WEB_MARKET_VERIFIED_PATH).toContain('M9.483 11.458v3.5h-1v-3.5z');
    expect(
      isWebMarketQuotePatch({
        type: 'market',
        key: market.key,
        changes: {
          revision: 2,
          price: '$64,240.00',
          change: { text: '+2.41%', tone: 'positive' },
        },
      })
    ).toBe(true);
    const quotePatches = [
      {
        type: 'market',
        key: market.key,
        changes: {
          revision: 2,
          price: '$64,240.00',
          change: { text: '+2.41%', tone: 'positive' },
        },
      },
    ] as const;
    const imageBindCountBeforeQuote = 1;
    expect(
      imageBindCountBeforeQuote +
        webMarketImageBindDeltaForPatches(quotePatches)
    ).toBe(imageBindCountBeforeQuote);
    expect(
      isWebMarketQuotePatch({
        type: 'market',
        key: market.key,
        changes: { style: { changeWidth: 88 } },
      })
    ).toBe(false);
    expect(
      webMarketImageBindDeltaForPatches([
        {
          type: 'market',
          key: market.key,
          changes: { style: { changeWidth: 88 } },
        },
      ])
    ).toBe(1);
    const styled = {
      ...market,
      style: {
        horizontalPadding: 24,
        image: { width: 36, height: 38, shape: 'rounded' },
        changeWidth: 88,
        changeHeight: 36,
        changeCornerRadius: 12,
      },
    } as MarketRow;
    expect(resolveWebMarketLayoutStyle(styled)).toMatchObject({
      horizontalPadding: 24,
      imageWidth: 36,
      imageHeight: 38,
      imageCornerRadius: 8,
      changeWidth: 88,
      changeHeight: 36,
      changeCornerRadius: 12,
    });
    expect(resolveWebMarketLayoutStyle(market)).toEqual({
      horizontalPadding: 20,
      verticalPadding: 12,
      leadingGap: 14,
      titleBadgeGap: 4,
      trailingGap: 8,
      imageWidth: 32,
      imageHeight: 32,
      imageCornerRadius: 16,
      changeWidth: 80,
      changeHeight: 32,
      changeCornerRadius: 8,
    });
    expect(webRowRenderSignature(styled)).not.toBe(
      webRowRenderSignature(market)
    );
  });

  it('keeps the disabled section-index rail out of pointer hit testing', () => {
    expect(WEB_LIST_CSS).toContain(
      '.ok-native-list-index-rail[hidden]{display:none}'
    );
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
        { ...parent, key: 'hidden-1', title: 'Hidden 1' },
        { ...parent, key: 'hidden-2', title: 'Hidden 2' },
      ],
      draggable: true,
    };
    const peer: RowModel = { ...parent, key: 'peer', title: 'Peer' };
    const reorderable = snapshot({ kind: 'linear' }, [group, peer]);

    expect(estimateWebRowHeight(group, reorderable, 136)).toBe(228);
    expect(isWebRowReorderable(reorderable, group)).toBe(true);
    expect(moveWebReorderRow([group, peer], 0, 1)).toEqual([peer, group]);
    expect(webWalletGroupReorderBadge(group)).toBe('+2');

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

  it('lays out linear, sectioned, grid, table, and horizontal examples', () => {
    const linear = computeWebListLayout(
      snapshot({ kind: 'linear', contentPadding: 8 }),
      360,
      640
    );
    expect(linear.items).toHaveLength(rows.length);
    expect(linear.items[0]).toMatchObject({ x: 8, y: 8, width: 344 });

    const sectioned = computeWebListLayout(
      snapshot({
        kind: 'sectioned',
        stickyHeaders: true,
        contentPaddingHorizontal: 8,
      }),
      360,
      640
    );
    expect(sectioned.items[1].width).toBe(328);

    const grid = computeWebListLayout(
      snapshot({ kind: 'grid', gridColumns: 2, contentPadding: 10 }),
      360,
      640
    );
    expect(grid.items[0].width).toBe(340);
    expect(grid.items[2].width).toBe(170);
    expect(grid.items[3].y).toBe(grid.items[2].y);
    const partiallyVisibleBand = visibleWebLayoutItems(
      grid,
      grid.items[2].y + 50,
      1
    );
    expect(partiallyVisibleBand.map((item) => item.key)).toContain('identity');
    expect(partiallyVisibleBand.map((item) => item.key)).not.toContain('rail');

    const table = computeWebListLayout(
      snapshot({ kind: 'table' }, [rows[6]]),
      360,
      640
    );
    expect(table.items[0].height).toBe(60);

    const rail = rows[3];
    if (rail.type !== 'rail') throw new Error('Invalid fixture');
    const horizontal = computeWebListLayout(
      snapshot({ kind: 'linear', orientation: 'horizontal', itemSpacing: 4 }, [
        rail,
        { ...rail, key: 'rail-2', title: 'Long rail title' },
      ]),
      360,
      80
    );
    expect(horizontal.horizontal).toBe(true);
    expect(horizontal.items[1].x).toBeGreaterThan(horizontal.items[0].x);
    expect(horizontal.contentWidth).toBeGreaterThanOrEqual(360);
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
});
