import type { IdentityRow, NativeListSnapshot, RowModel } from '../models';
import {
  WEB_LIST_CSS,
  WEB_REORDER_ANIMATION,
  NativeListWebEngine,
  applyRowStyle,
  canStartWebWalletGroupReorder,
  cancelWebReorderRows,
  computeWebListLayout,
  createRowBody,
  estimateWebRowHeight,
  hasExceededWebReorderMouseThreshold,
  isWebRowReorderable,
  moveWebReorderRow,
  resolveWebCollapsiblePagerRawOffset,
  resolveWebCollapsiblePagerScrollMetrics,
  visibleWebLayoutItems,
  webSectionIndexActiveKey,
  webSectionIndexContainerLayout,
  webSectionIndexInverseScale,
  webSectionIndexMetrics,
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
  it('centers a fixed-spacing container-hosted section index', () => {
    expect(webSectionIndexContainerLayout(22, 668)).toEqual({
      top: 84,
      height: 500,
    });
    expect(webSectionIndexContainerLayout(22, 588)).toEqual({
      top: 44,
      height: 500,
    });
    expect(webSectionIndexContainerLayout(22, 472)).toEqual({
      top: 0,
      height: 472,
    });
    expect(webSectionIndexMetrics(22, 472, true)).toEqual({
      originY: 8,
      trackHeight: 456,
    });
    expect(webSectionIndexMetrics(22, 700, true)).toEqual({
      originY: 8,
      trackHeight: 484,
    });
    expect(webSectionIndexMetrics(22, 472)).toEqual({
      originY: 60,
      trackHeight: 352,
    });
  });

  it('cancels the container scale without changing section index layout', () => {
    expect(webSectionIndexInverseScale(640, 608)).toBeCloseTo(1 / 0.95);
    expect(webSectionIndexInverseScale(640, 642.77888)).toBeCloseTo(
      1 / 1.004342
    );
    expect(webSectionIndexInverseScale(640, 640)).toBe(1);
    expect(webSectionIndexInverseScale(640, 0)).toBe(1);
  });

  it('keeps the final indexed section active at the scroll limit', () => {
    const indexedRows: readonly RowModel[] = [
      {
        type: 'sectionHeader',
        key: 'header-x',
        sectionKey: 'x',
        indexTitle: 'X',
        title: 'X',
      },
      {
        type: 'identity',
        key: 'x-row',
        sectionKey: 'x',
        leading: { kind: 'icon', name: 'x' },
        title: 'X row',
      },
      {
        type: 'sectionHeader',
        key: 'header-z',
        sectionKey: 'z',
        indexTitle: 'Z',
        title: 'Z',
      },
      {
        type: 'identity',
        key: 'z-row',
        sectionKey: 'z',
        leading: { kind: 'icon', name: 'z' },
        title: 'Z row',
      },
    ];
    const layout = computeWebListLayout(
      snapshot({ kind: 'sectioned' }, indexedRows),
      320,
      120
    );
    expect(webSectionIndexActiveKey(indexedRows, layout, 0, 120)).toBe(
      'header-x'
    );
    expect(
      webSectionIndexActiveKey(
        indexedRows,
        layout,
        layout.contentHeight - 120,
        120
      )
    ).toBe('header-z');
  });

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

describe('web row style', () => {
  // The workspace ships jsdom without its type package; type the one entry used.
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => { window: { document: Document } };
  };

  const render = (styledRow: RowModel): HTMLElement => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const body = createRowBody(
      {
        document,
        snapshot: {
          schemaVersion: 1,
          generation: 1,
          layout: { kind: 'linear' },
          rows: [styledRow],
        },
        selectedKeys: new Set<string>(),
        itemIndex: 0,
      },
      styledRow
    );
    applyRowStyle(body, styledRow);
    return body;
  };

  it('honors legacy identity line limits and restores them when style is cleared', () => {
    const base: IdentityRow = {
      type: 'identity',
      key: 'legacy-lines',
      leading: { kind: 'icon', name: 'coin' },
      title: 'First\nSecond',
      subtitle: 'One\nTwo',
      titleLines: 2,
      subtitleLines: 2,
      titleMatch: [{ start: 0, end: 5 }],
    };
    for (const style of [
      { title: { lines: 1 as const }, subtitle: { lines: 3 as const } },
      {},
    ]) {
      const body = render({ ...base, style });
      const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
      const subtitle = body.querySelector<HTMLElement>(
        '[data-nl-slot="subtitle"]'
      )!;
      expect(title.style.getPropertyValue('-webkit-line-clamp')).toBe(
        'title' in style ? '' : '2'
      );
      expect(title.textContent).toBe(
        'title' in style ? 'First Second' : base.title
      );
      expect(subtitle.style.getPropertyValue('-webkit-line-clamp')).toBe(
        'subtitle' in style ? '3' : '2'
      );
    }
  });

  it('uses template line height for detached multi-line clip and retains unbounded warning defaults', () => {
    const message = render({
      type: 'message',
      key: 'clip',
      title: 'Title',
      body: 'One\nTwo\nThree\nFour',
      time: 'Now',
      style: { body: { lines: 3, truncate: 'clip' } },
    });
    expect(
      message.querySelector<HTMLElement>('[data-nl-slot="body"]')!.style
        .maxHeight
    ).toBe('54px');
    const warning = render({
      type: 'system',
      key: 'warning',
      variant: 'warning',
      title: 'Warning',
      message: 'One\nTwo\nThree\nFour',
      style: { message: { truncate: 'clip' } },
    });
    const text = warning.querySelector<HTMLElement>(
      '[data-nl-slot="message"]'
    )!;
    expect(text.style.whiteSpace).toBe('pre-wrap');
    expect(text.style.maxHeight).toBe('');
    expect(text.textContent).toContain('\n');
  });

  it('applies Market badge metrics and center image fitting without changing overlays', () => {
    const body = render({
      type: 'market',
      key: 'market-badge',
      variant: 'token',
      title: 'Market',
      leading: { kind: 'token', image, networkImage: image },
      price: '$1',
      change: { text: '+1%', tone: 'positive' },
      badges: [
        {
          key: 'tag',
          text: 'Tag',
          style: {
            fontSize: 15,
            fontWeight: 'bold',
            lineHeight: 22,
            height: 30,
            horizontalPadding: 7,
          },
        },
      ],
      style: { image: { contentFit: 'center' }, leadingGap: 9 },
    });
    const badge = body.querySelector<HTMLElement>(
      '.ok-native-list-market-badge'
    )!;
    expect(badge.style.fontSize).toBe('15px');
    expect(badge.style.fontWeight).toBe('700');
    expect(badge.style.lineHeight).toBe('22px');
    expect(badge.style.height).toBe('30px');
    expect(badge.style.paddingInline).toBe('7px');
    const visual = body.querySelector<HTMLElement>('.ok-native-list-visual')!;
    expect(visual.style.marginInlineEnd).toBe('9px');
    expect(
      visual.querySelector<HTMLElement>('.ok-native-list-visual-main')!.style
        .objectFit
    ).toBe('none');
    expect(
      visual.querySelector<HTMLElement>('.ok-native-list-visual-corner')!.style
        .objectFit
    ).not.toBe('none');
  });

  it('gives explicit Market typography priority over rich runs and legacy change colors', () => {
    const body = render({
      type: 'market',
      key: 'market-rich',
      variant: 'token',
      title: 'Market',
      leading: { kind: 'icon', name: 'coin' },
      price: '$01',
      priceSegments: [{ text: '$0' }, { text: '1', style: 'subscript' }],
      change: {
        text: '+01',
        textSegments: [{ text: '+0' }, { text: '1', style: 'subscript' }],
        tone: 'positive',
        textColor: '#ff0000',
      },
      style: {
        price: { fontSize: 18 },
        change: { fontSize: 17, color: '#123456' },
      },
    });
    const price = body.querySelector<HTMLElement>(
      '.ok-native-list-market-price'
    )!;
    const change = body.querySelector<HTMLElement>(
      '.ok-native-list-market-change'
    )!;
    expect(
      Array.from(price.querySelectorAll('span')).map(
        (run) => run.style.fontSize
      )
    ).toEqual(['18px', '18px']);
    expect(
      Array.from(
        change.querySelectorAll<HTMLElement>(
          '.ok-native-list-text-content > span'
        )
      ).map((run) => run.style.fontSize)
    ).toEqual(['17px', '17px']);
    expect(change.style.color).toBe('rgb(18, 52, 86)');
  });

  it('allows Market measurement changes while giving explicit style height precedence', () => {
    const row: RowModel = {
      type: 'market',
      key: 'market-height',
      variant: 'token',
      title: 'Market',
      leading: { kind: 'icon', name: 'coin' },
      price: '$1',
      change: { text: '+1%', tone: 'positive' },
    };
    const config = snapshot({ kind: 'linear' }, [row]);
    expect(
      estimateWebRowHeight(
        { ...row, style: { image: { height: 140 }, verticalPadding: 48 } },
        config,
        320
      )
    ).toBeGreaterThan(estimateWebRowHeight(row, config, 320));
    expect(
      estimateWebRowHeight(
        {
          ...row,
          height: 180,
          style: { image: { height: 140 }, verticalPadding: 48 },
        },
        config,
        320
      )
    ).toBe(180);
  });

  it('resolves style heights in linear, grid, horizontal, nested and footer paths without overwriting legacy heights', () => {
    const base: IdentityRow = {
      type: 'identity',
      key: 'sized',
      height: 80,
      title: 'Sized',
      leading: { kind: 'icon', name: 'coin' },
    };
    const sized: IdentityRow = {
      ...base,
      style: { container: { height: 124 } },
    };
    for (const layout of [
      { kind: 'linear' },
      { kind: 'grid', gridColumns: 2 },
      { kind: 'linear', orientation: 'horizontal' },
    ] as const) {
      expect(
        computeWebListLayout(snapshot(layout, [sized]), 360, 500).items[0]!
          .height
      ).toBe(124);
      expect(sized.height).toBe(80);
    }
    const media: RowModel = {
      type: 'mediaTile',
      variant: 'gallery',
      key: 'media-sized',
      title: 'Media',
      style: { container: { height: 150 } },
    };
    expect(
      computeWebListLayout(
        snapshot({ kind: 'grid', gridColumns: 2 }, [media]),
        360,
        500
      ).items[0]!.height
    ).toBe(150);
    const parent: IdentityRow = {
      ...base,
      key: 'parent',
      presentation: 'walletSidebar',
      style: { container: { height: 100 } },
    };
    const group: RowModel = {
      type: 'walletGroup',
      key: 'parent',
      parent,
      children: [
        { ...parent, key: 'child', style: { container: { height: 90 } } },
      ],
    };
    expect(
      estimateWebRowHeight(group, snapshot({ kind: 'linear' }, [group]), 360)
    ).toBe(204);
    const groupBody = render(group);
    expect(
      Array.from(groupBody.children).map(
        (node) => (node as HTMLElement).style.height
      )
    ).toEqual(['100px', '90px']);
    const compact = computeWebListLayout(
      snapshot({ kind: 'linear' }, [
        { ...group, style: { container: { height: 250 } } },
      ]),
      360,
      500,
      'parent'
    );
    expect(compact.items[0]!.height).toBe(68);
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const footer = {
      type: 'action',
      key: 'footer',
      actionKey: 'footer-action',
      title: 'Footer',
      height: 50,
      style: { container: { height: 92 } },
    } as const;
    const engine = new NativeListWebEngine(
      host,
      { ...snapshot({ kind: 'sectioned' }, [sized]), fixedFooter: footer },
      {},
      false
    );
    try {
      expect(
        host.querySelector<HTMLElement>('[data-native-list-row-key="sized"]')!
          .style.height
      ).toBe('124px');
      expect(
        host.querySelector<HTMLElement>(
          '.ok-native-list-footer > .ok-native-list-item'
        )!.style.height
      ).toBe('92px');
      engine.applySnapshot({
        ...snapshot({ kind: 'sectioned' }, [{ ...base, style: {} }]),
        fixedFooter: { ...footer, style: {} },
      });
      expect(
        host.querySelector<HTMLElement>('[data-native-list-row-key="sized"]')!
          .style.height
      ).toBe('80px');
      expect(
        host.querySelector<HTMLElement>(
          '.ok-native-list-footer > .ok-native-list-item'
        )!.style.height
      ).toBe('50px');
    } finally {
      engine.destroy();
    }
  });

  it('includes timestamp line limits in measured message height and respects a styled height', () => {
    const base = {
      type: 'message',
      key: 'time-height',
      title: 'Title',
      body: 'Body',
      time: 'One\nTwo\nThree',
    } as const;
    const config = snapshot({ kind: 'linear' }, [base]);
    const single = {
      ...base,
      style: { time: { lines: 1 as const, lineHeight: 22 } },
    };
    const multi = {
      ...base,
      style: { time: { lines: 3 as const, lineHeight: 22 } },
    };
    expect(
      estimateWebRowHeight(multi, config, 400) -
        estimateWebRowHeight(single, config, 400)
    ).toBe(28); // Time is beside the 38px title/body column: max(66,38)-max(22,38).
    expect(
      estimateWebRowHeight(
        { ...multi, style: { ...multi.style, container: { height: 120 } } },
        config,
        400
      )
    ).toBe(120);
  });

  it('rebinds a message through styled, cleared and different-template snapshots', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const message = {
      type: 'message',
      key: 'reused-message',
      title: 'First\nSecond',
      body: 'One\nTwo\nThree',
      time: 'Now',
      bodyLines: 1,
      height: 136,
      unread: true,
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [message]),
      {},
      false
    );
    const item = () =>
      host.querySelector<HTMLElement>(
        '[data-native-list-row-key="reused-message"]'
      )!;
    const body = () =>
      item().querySelector<HTMLElement>('[data-nl-slot="body"]')!;
    try {
      const wrapper = item();
      expect(body().textContent).toBe('One Two Three');
      engine.applyPatches([
        {
          type: 'message',
          key: message.key,
          changes: {
            style: {
              container: { height: 192, contentVerticalAlignment: 'bottom' },
              body: {
                lines: 3,
                lineHeight: 22,
                truncate: 'clip',
                color: '#FF0000',
              },
            },
          },
        },
      ]);
      expect(item()).toBe(wrapper);
      expect(item().style.height).toBe('192px');
      expect(body().textContent).toBe(message.body);
      expect(body().style.maxHeight).toBe('66px');
      engine.applyPatches([
        { type: 'message', key: message.key, changes: { style: {} } },
      ]);
      expect(item().style.height).toBe('136px');
      expect(body().textContent).toBe('One Two Three');
      expect(body().style.color).toBe('var(--nl-secondary)');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...message,
            title: 'Updated',
            body: 'Replacement',
            time: 'Later',
            unread: false,
          },
        ])
      );
      expect(item()).toBe(wrapper);
      expect(item().querySelector('.ok-native-list-unread')).toBeNull();
      expect(item().querySelector('[data-nl-slot="time"]')?.textContent).toBe(
        'Later'
      );
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'identity',
            key: message.key,
            title: 'Identity',
            leading: { kind: 'icon', name: 'StarOutline' },
          },
        ])
      );
      expect(item().querySelector('[data-nl-slot="body"]')).toBeNull();
      expect(item().querySelector('[data-nl-slot="time"]')).toBeNull();
      expect(item()).not.toBe(wrapper);
      expect(wrapper.isConnected).toBe(false);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [message]));
      expect(item()).toBe(wrapper);
      expect(item().style.height).toBe('136px');
      expect(body().textContent).toBe('One Two Three');
    } finally {
      engine.destroy();
    }
  });

  it('retains Rail views and image requests while resetting semantic styles and compatible pools', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const rail = {
      type: 'rail',
      key: 'rail-pilot',
      title: 'Bitcoin',
      visual: { kind: 'token', image, networkImage: image },
      badge: { key: 'change', text: '+12%', tone: 'success' },
      status: 'online',
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [rail]),
      {},
      false
    );
    const row = () =>
      host.querySelector<HTMLElement>(
        '[data-native-list-row-key="rail-pilot"]'
      )!;
    try {
      const wrapper = row();
      const body = wrapper.firstElementChild as HTMLElement;
      const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
      const visual = body.querySelector<HTMLElement>('.ok-native-list-visual')!;
      const badge = body.querySelector<HTMLElement>('[data-nl-slot="badge"]')!;
      const images = Array.from(body.querySelectorAll('img'));
      const corner = body.querySelector<HTMLElement>(
        '.ok-native-list-visual-corner'
      )!;
      const cornerStyle = corner.style.cssText;
      engine.applyPatches([
        {
          type: 'rail',
          key: rail.key,
          changes: {
            title: 'Updated',
            style: {
              container: { height: 96 },
              horizontalPadding: 12,
              verticalPadding: 10,
              leadingGap: 14,
              titleBadgeGap: 12,
              trailingGap: 10,
              title: { fontSize: 18, lineHeight: 24, lines: 2 },
              badge: { color: '#BC3030' },
              image: { width: 32, height: 24, shape: 'square' },
            },
          },
        },
      ]);
      expect(row()).toBe(wrapper);
      expect(row().firstElementChild).toBe(body);
      expect(body.querySelector('[data-nl-slot="title"]')).toBe(title);
      expect(title.textContent).toBe('Updated');
      expect(title.style.fontSize).toBe('18px');
      expect(row().style.height).toBe('96px');
      expect(visual.style.width).toBe('32px');
      expect(corner.style.cssText).toBe(cornerStyle);
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [rail]));
      expect(row()).toBe(wrapper);
      expect(title.style.fontSize).toBe('');
      expect(visual.style.width).toBe('20px');
      expect(row().style.height).toBe('40px');
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...rail, badge: undefined, status: 'none' },
        ])
      );
      expect(body.querySelector('[data-nl-slot="badge"]')).toBeNull();
      expect(body.querySelector('[data-nl-slot="status"]')).toBeNull();
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [rail]));
      expect(body.querySelector('[data-nl-slot="badge"]')).toBe(badge);
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'message',
            key: rail.key,
            title: 'Other',
            body: 'Body',
            time: '',
          },
        ])
      );
      expect(row()).not.toBe(wrapper);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [rail]));
      expect(row()).toBe(wrapper);
      expect(row().firstElementChild).toBe(body);
      expect(title.textContent).toBe(rail.title);
    } finally {
      engine.destroy();
    }
  });

  it('retains MediaTile assets and restores styles, empty states and close actions across family reuse', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const media = {
      type: 'mediaTile',
      variant: 'gallery',
      key: 'media-pilot',
      title: 'Collectible',
      subtitle: 'Collection',
      image: { ...image, contentFit: 'contain' },
      networkImage: image,
      badge: { key: 'amount', text: '×2' },
      closeActionKey: 'close',
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [media]),
      {},
      false
    );
    const current = () =>
      host.querySelector<HTMLElement>(
        '[data-native-list-row-key="media-pilot"]'
      )!;
    try {
      const wrapper = current();
      const body = wrapper.firstElementChild as HTMLElement;
      const picture = body.querySelector<HTMLElement>(
        '.ok-native-list-media-image'
      )!;
      const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...media,
            title: 'Updated',
            style: {
              container: { height: 260 },
              image: { width: 120, height: 100, contentFit: 'cover' },
              title: { fontSize: 13, lines: 2 },
              lineGap: 6,
              leadingGap: 14,
            },
          },
        ])
      );
      expect(current().firstElementChild).toBe(body);
      expect(body.querySelector('.ok-native-list-media-image')).toBe(picture);
      expect(title.textContent).toBe('Updated');
      expect(picture.style.width).toBe('120px');
      expect(picture.style.objectFit).toBe('cover');
      expect(current().style.height).toBe('260px');
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [media]));
      expect(title.style.fontSize).toBe('');
      expect(picture.style.width).toBe('');
      expect(picture.style.objectFit).toBe('contain');
      expect(current().style.height).toBe('244px');
      expect(
        body
          .querySelector('[data-native-list-action="close"]')
          ?.getAttribute('data-native-list-anchor-source')
      ).toBe('mediaClose');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...media, imageState: 'error', closeActionKey: undefined },
        ])
      );
      expect(body.querySelector('[data-state="error"]')).not.toBeNull();
      expect(body.querySelector('[data-native-list-action]')).toBeNull();
      expect(picture.isConnected).toBe(false);
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'rail',
            key: media.key,
            title: 'Other',
            visual: { kind: 'icon', name: 'star' },
          },
        ])
      );
      expect(current()).not.toBe(wrapper);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [media]));
      expect(current()).toBe(wrapper);
      expect(current().firstElementChild).toBe(body);
      expect(title.textContent).toBe(media.title);
      expect(body.querySelectorAll('img')).toHaveLength(2);
    } finally {
      engine.destroy();
    }
  });

  it('retains Activity images while resetting amount styles and footer actions', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row = {
      type: 'activity',
      key: 'tx',
      leading: {
        kind: 'image',
        image: { uri: 'https://example.com/a.png', width: 40, height: 40 },
      },
      title: 'Received',
      description: 'From Alice',
      status: 'Failed',
      primaryAmount: '+1 BTC',
      secondaryAmount: '$42',
      footerActions: [{ key: 'retry', label: 'Retry' }],
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row]),
      {},
      false
    );
    const body = host.querySelector<HTMLElement>(
      '[data-nl-renderer="activity"]'
    )!;
    const image = body.querySelector('img');
    try {
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...row,
            style: {
              container: { height: 120 },
              primaryAmount: { fontSize: 12 },
              status: { color: '#ff0000' },
              image: { width: 28 },
            },
          },
        ])
      );
      expect(body.querySelector('img')).toBe(image);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="primaryAmount"]')!.style
          .fontSize
      ).toBe('12px');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...row, status: 'Confirmed', footerActions: undefined },
        ])
      );
      expect(body.querySelector('img')).toBe(image);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="primaryAmount"]')!.style
          .fontSize
      ).toBe('');
      expect(
        body.querySelector('[data-native-list-action="retry"]')
      ).toBeNull();
      expect(body.textContent).toContain('Confirmed');
    } finally {
      engine.destroy();
    }
  });

  it('restores System warning styles and replaces variant content in its own host', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const warning = {
      type: 'system',
      key: 'status',
      variant: 'warning',
      title: 'Warning',
      message: 'Multiple lines of warning content',
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [warning]),
      {},
      false
    );
    const body = host.querySelector<HTMLElement>(
      '[data-nl-renderer="system"]'
    )!;
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
    try {
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...warning,
            style: {
              container: { height: 160 },
              title: { fontSize: 18, lines: 2 },
              message: { lines: 1, truncate: 'clip' },
              lineGap: 8,
            },
          },
        ])
      );
      expect(host.querySelector('[data-nl-renderer="system"]')).toBe(body);
      expect(body.querySelector('[data-nl-slot="title"]')).toBe(title);
      expect(title.style.fontSize).toBe('18px');
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [warning]));
      expect(title.style.fontSize).toBe('');
      expect(body.style.rowGap).toBe('');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'system',
            key: 'status',
            variant: 'loading',
            loadingStyle: 'spinner',
          },
        ])
      );
      expect(host.querySelector('[data-nl-renderer="system"]')).toBe(body);
      expect(body.querySelector('[data-nl-slot="title"]')).toBeNull();
      expect(body.querySelector('[role="progressbar"]')).not.toBeNull();
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'system',
            key: 'status',
            variant: 'retry',
            message: 'Retry connection',
            actionKey: 'retry',
          },
        ])
      );
      expect(body.querySelector('[role="progressbar"]')).toBeNull();
      expect(
        body.querySelector('[data-native-list-action="retry"]')
      ).not.toBeNull();
    } finally {
      engine.destroy();
    }
  });

  it('preserves Action views through style clearing and selection echoes', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const action = {
      type: 'action',
      key: 'action-pilot',
      title: 'Action',
      actionKey: 'open',
      icon: { kind: 'icon', name: 'PlusSmallOutline' },
      checkbox: {
        kind: 'checkbox',
        state: 'unchecked',
        target: { scope: 'list' },
      },
      trailing: [
        {
          kind: 'value',
          text: '$0.012',
          textSegments: [
            { text: '$0.0' },
            { text: '5', style: 'subscript' },
            { text: '12' },
          ],
        },
        { kind: 'checkbox', state: 'unchecked', target: { scope: 'row' } },
      ],
    } as const;
    const selectable = {
      type: 'identity',
      key: 'selectable',
      title: 'Other',
      leading: { kind: 'icon', name: 'star' },
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [action, selectable]),
      {},
      false
    );
    const current = () =>
      host.querySelector<HTMLElement>(
        '[data-native-list-row-key="action-pilot"]'
      )!;
    try {
      const wrapper = current();
      const body = wrapper.firstElementChild as HTMLElement;
      const title = body.querySelector('[data-nl-slot="title"]');
      const icon = body.querySelector('.ok-native-list-visual');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...action,
            style: {
              container: { height: 100 },
              title: { fontSize: 20, lines: 2 },
              value: { fontSize: 12, lines: 1 },
              leadingGap: 18,
              image: { width: 28, height: 30 },
            },
          },
        ])
      );
      expect(current().firstElementChild).toBe(body);
      expect(body.querySelector('[data-nl-slot="title"]')).toBe(title);
      expect(body.querySelector('.ok-native-list-visual')).toBe(icon);
      expect(current().style.height).toBe('100px');
      expect((icon as HTMLElement).style.width).toBe('28px');
      expect((icon as HTMLElement).style.height).toBe('30px');
      const selected = snapshot({ kind: 'sectioned' }, [action, selectable]);
      engine.applySnapshot({
        ...selected,
        selection: { ...selected.selection!, selectedKeys: [selectable.key] },
      });
      expect(current().firstElementChild).toBe(body);
      expect(current().style.height).toBe('60px');
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="title"]')!.style
          .fontSize
      ).toBe('');
      expect(
        body.querySelector('[role="checkbox"]')?.getAttribute('aria-checked')
      ).toBe('true');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            type: 'message',
            key: action.key,
            title: 'Other',
            body: 'Body',
            time: '',
          },
        ])
      );
      expect(current()).not.toBe(wrapper);
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [action, selectable])
      );
      expect(current()).toBe(wrapper);
      expect(
        body.querySelector('[role="checkbox"]')?.getAttribute('aria-checked')
      ).toBe('false');
    } finally {
      engine.destroy();
    }
  });

  it('recomputes horizontal Rail placement from styled metrics and restores default widths', () => {
    const rail = {
      type: 'rail',
      key: 'one',
      title: 'BTC',
      visual: { kind: 'icon', name: 'StarOutline' },
      badge: { key: 'change', text: '+1%' },
    } as const;
    const layout = (row: RowModel) =>
      computeWebListLayout(
        snapshot(
          { kind: 'linear', orientation: 'horizontal', itemSpacing: 8 },
          [row, { ...rail, key: 'two' }]
        ),
        390,
        200
      );
    const plain = layout(rail);
    const styled = layout({
      ...rail,
      style: {
        title: { fontSize: 24 },
        horizontalPadding: 12,
        leadingGap: 16,
        titleBadgeGap: 12,
        image: { width: 32 },
      },
    });
    expect(styled.items[0]!.width).toBeGreaterThan(plain.items[0]!.width);
    expect(styled.items[1]!.x - plain.items[1]!.x).toBe(
      styled.items[0]!.width - plain.items[0]!.width
    );
    expect(layout({ ...rail, style: {} })).toEqual(plain);
  });

  it('retains Message text and image slots through content/style updates and cancels removed retries', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const message = {
      type: 'message',
      key: 'persistent',
      title: 'First',
      body: 'Body',
      time: 'Now',
      height: 136,
      leading: {
        kind: 'token',
        image: { ...image, retryTimes: 2 },
        networkImage: image,
      },
      thumbnail: image,
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [message]),
      {},
      false
    );
    const row = () =>
      host.querySelector<HTMLElement>(
        '[data-native-list-row-key="persistent"]'
      )!;
    try {
      const body = row().firstElementChild!;
      const title = body.querySelector('[data-nl-slot="title"]')!;
      const images = Array.from(body.querySelectorAll('img'));
      expect(images).toHaveLength(3);
      const corner = body.querySelector<HTMLElement>(
        '.ok-native-list-visual-corner'
      )!;
      const cornerStyle = corner.style.cssText;
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...message,
            title: 'Updated',
            style: {
              image: { width: 56, height: 32, cornerRadius: 3 },
              title: { fontSize: 18 },
            },
          },
        ])
      );
      expect(row().firstElementChild).toBe(body);
      expect(body.querySelector('[data-nl-slot="title"]')).toBe(title);
      expect(title.textContent).toBe('Updated');
      expect(corner.style.cssText).toBe(cornerStyle);
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [message]));
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      expect((title as HTMLElement).style.fontSize).toBe('15px');
      const source = images[0]!.src;
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...message, leading: undefined, thumbnail: undefined },
        ])
      );
      images[0]!.dispatchEvent(new document.defaultView!.Event('error'));
      expect(images[0]!.isConnected).toBe(false);
      expect(images[0]!.src).toBe(source);
      expect(body.querySelector('img')).toBeNull();
      expect(body.querySelector('[data-nl-slot="title"]')).toBe(title);
    } finally {
      engine.destroy();
    }
  });

  it('reuses only compatible row hosts after removal, insertion and index changes', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const message = {
      type: 'message',
      key: 'old-message',
      title: 'Old',
      body: 'Old body',
      time: 'Now',
      style: { container: { height: 192 }, body: { color: '#FF0000' } },
    } as const;
    const identity = {
      type: 'identity',
      key: 'identity',
      title: 'Identity',
      leading: { kind: 'icon', name: 'StarOutline' },
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [message, identity]),
      {},
      false
    );
    const item = (key: string) =>
      host.querySelector<HTMLElement>(`[data-native-list-row-key="${key}"]`)!;
    try {
      const messageHost = item(message.key);
      const identityHost = item(identity.key);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, []));
      expect(messageHost.isConnected).toBe(false);
      expect(identityHost.isConnected).toBe(false);
      const replacement = {
        type: 'message',
        key: 'new-message',
        title: 'New',
        body: 'Fresh',
        time: 'Later',
        height: 136,
      } as const;
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [identity, replacement])
      );
      expect(item(identity.key)).toBe(identityHost);
      expect(item(replacement.key)).toBe(messageHost);
      expect(messageHost.style.height).toBe('136px');
      expect(
        messageHost.querySelector<HTMLElement>('[data-nl-slot="body"]')!.style
          .color
      ).toBe('var(--nl-secondary)');
      expect(messageHost.textContent).toContain('Fresh');
      expect(messageHost.textContent).not.toContain('Old body');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [replacement, identity])
      );
      expect(item(identity.key)).toBe(identityHost);
      expect(item(replacement.key)).toBe(messageHost);
    } finally {
      engine.destroy();
    }
  });

  it('preserves the value-pair break inside a shared multi-line budget', () => {
    const base: IdentityRow = {
      type: 'identity',
      key: 'pair-lines',
      title: 'Pair',
      leading: { kind: 'icon', name: 'coin' },
      trailing: [{ kind: 'valuePair', primary: 'One', secondary: 'Two' }],
    };
    const multi = render({
      ...base,
      style: { value: { lines: 3, truncate: 'clip' } },
    }).querySelector<HTMLElement>('[data-nl-slot="value"]')!;
    expect(multi.textContent).toBe('One\nTwo');
    expect(multi.lastElementChild?.getAttribute('style')).toContain(
      'white-space: inherit'
    );
    const single = render({
      ...base,
      style: { value: { lines: 1 } },
    }).querySelector<HTMLElement>('[data-nl-slot="value"]')!;
    expect(single.textContent).toBe('One Two');
  });

  it('keeps the media leading gap independent of container alignment', () => {
    const body = render({
      type: 'mediaTile',
      key: 'media-gap',
      variant: 'gallery',
      title: 'Media',
      image,
      style: {
        leadingGap: 11,
        container: { contentVerticalAlignment: 'bottom' },
      },
    });
    expect(
      body.querySelector<HTMLElement>('.ok-native-list-media-meta')!.style
        .paddingTop
    ).toBe('11px');
    expect(
      body.querySelector<HTMLElement>('.ok-native-list-media-image')!.style
        .marginBottom
    ).toBe('');
  });

  it('shares container appearance across every template without styling nested text', () => {
    const parent: IdentityRow = {
      type: 'identity',
      key: 'parent',
      presentation: 'walletSidebar',
      leading: { kind: 'icon', name: 'coin' },
      title: 'Parent',
    };
    const allRows: RowModel[] = [
      ...rows,
      {
        type: 'market',
        key: 'all-market',
        variant: 'token',
        title: 'Market',
        leading: { kind: 'icon', name: 'coin' },
        price: '$1',
        change: { text: '+1%', tone: 'positive' },
      },
      {
        type: 'walletGroup',
        key: parent.key,
        parent,
        children: [{ ...parent, key: 'child' }],
      },
    ];
    for (const row of allRows) {
      const body = render({
        ...row,
        style: {
          container: {
            height: 141,
            backgroundColor: '#123456',
            borderWidth: 2,
            borderColor: '#abcdef',
            cornerRadius: 7,
            contentVerticalAlignment: 'bottom',
          },
        },
      } as RowModel);
      expect(
        estimateWebRowHeight(
          { ...row, style: { container: { height: 141 } } } as RowModel,
          snapshot({ kind: 'linear' }, [row]),
          320
        )
      ).toBe(141);
      expect(body.style.getPropertyValue('--nl-container-background')).toBe(
        '#123456'
      );
      expect(body.style.borderRadius).toBe('7px');
      expect(body.getAttribute('data-nl-container-border')).toBe('true');
      expect(body.style.getPropertyValue('--nl-container-border-width')).toBe(
        '2px'
      );
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="title"]')?.style
          .color ?? ''
      ).toBe(row.type === 'message' ? 'var(--nl-primary)' : '');
    }
  });

  it('treats rich text as one single-line budget and keeps badges independent', () => {
    const body = render({
      type: 'identity',
      key: 'rich-lines',
      leading: { kind: 'icon', name: 'coin' },
      title: 'Bit\r\ncoin',
      titleMatch: [{ start: 0, end: 4 }],
      badges: [{ key: 'badge', text: 'Badge' }],
      style: {
        title: {
          lines: 1,
          truncate: 'clip',
          verticalAlignment: 'bottom',
          offsetY: -2,
        },
      },
    });
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
    const content = title.querySelector<HTMLElement>(
      '.ok-native-list-text-content'
    )!;
    expect(title.textContent).toBe('Bit coin');
    expect(title.style.justifyContent).toBe('flex-end');
    expect(content.style.whiteSpace).toBe('nowrap');
    expect(content.style.textOverflow).toBe('clip');
    expect(content.style.transform).toBe('translateY(-2px)');
    expect(
      body.querySelector('[data-nl-slot="badge"] .ok-native-list-text-content')
    ).toBeNull();
  });

  it('lets message style override legacy line limits and distinguishes multi-line clipping', () => {
    const message = {
      type: 'message',
      key: 'message-lines',
      title: 'Message',
      body: 'First\nSecond\nThird\nFourth',
      time: 'Now',
      bodyLines: 1,
    } as const;
    const tail = render({
      ...message,
      style: { body: { lines: 3 } },
    }).querySelector<HTMLElement>('[data-nl-slot="body"]')!;
    expect(tail.style.getPropertyValue('-webkit-line-clamp')).toBe('3');
    expect(tail.style.whiteSpace).toBe('pre-wrap');
    expect(tail.textContent).toBe(message.body);
    const clip = render({
      ...message,
      style: { body: { lines: 3, lineHeight: 18, truncate: 'clip' } },
    }).querySelector<HTMLElement>('[data-nl-slot="body"]')!;
    expect(clip.style.getPropertyValue('-webkit-line-clamp')).toBe('');
    expect(clip.style.maxHeight).toBe('54px');
    expect(clip.style.textOverflow).toBe('clip');
  });

  it('restores legacy container appearance and fixed allocation after styles are removed', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const base: IdentityRow = {
      type: 'identity',
      key: 'container-reset',
      leading: { kind: 'icon', name: 'coin' },
      title: 'Text',
      height: 80,
      opacity: 0.6,
      backgroundColor: '#ffffff',
      disabled: true,
    };
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [
        {
          ...base,
          style: {
            container: {
              backgroundColor: '#123456',
              opacity: 0.8,
              cornerRadius: 8,
              borderWidth: 2,
              borderColor: '#654321',
            },
            title: { lines: 3, truncate: 'clip' },
          },
        },
      ]),
      {},
      false
    );
    try {
      const item = host.querySelector<HTMLElement>(
        '[data-native-list-row-key="container-reset"]'
      )!;
      expect(item.style.opacity).toBe('0.4');
      const height = item.style.height;
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [{ ...base, style: {} }])
      );
      expect(item.style.opacity).toBe('0.3');
      expect(item.style.height).toBe(height);
      expect(
        item.firstElementChild?.getAttribute('data-nl-container-background')
      ).toBeNull();
      expect((item.firstElementChild as HTMLElement).style.borderRadius).toBe(
        ''
      );
      expect(
        item.querySelector<HTMLElement>('[data-nl-slot="title"]')?.style
          .maxHeight
      ).toBe('');
    } finally {
      engine.destroy();
    }
  });

  it('keeps the text layout through fast Market quote updates', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const base = {
      type: 'market',
      key: 'quote-layout',
      variant: 'token',
      height: 108,
      title: 'Market',
      leading: { kind: 'icon', name: 'coin' },
      price: '$1',
      change: { text: 'Up\n1%', tone: 'positive' },
      style: {
        changeHeight: 76,
        change: {
          lines: 2,
          truncate: 'clip',
          verticalAlignment: 'bottom',
          offsetY: 2,
        },
      },
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [base]),
      {},
      false
    );
    try {
      for (let quote = 2; quote <= 5; quote++) {
        engine.applySnapshot(
          snapshot({ kind: 'sectioned' }, [
            {
              ...base,
              price: `$${quote}`,
              change: { ...base.change, text: `Up\n${quote}%` },
            },
          ])
        );
        const change = host.querySelector<HTMLElement>(
          '.ok-native-list-market-change'
        )!;
        expect(change.textContent).toBe(`Up\n${quote}%`);
        expect(change.style.justifyContent).toBe('flex-end');
        expect(
          change.querySelectorAll('.ok-native-list-text-content')
        ).toHaveLength(1);
        const content = change.firstElementChild as HTMLElement;
        expect(content.style.maxHeight).toBe('2lh');
        expect(content.style.transform).toBe('translateY(2px)');
      }
    } finally {
      engine.destroy();
    }
  });

  it('styles primary and secondary data independently without restyling badges', () => {
    const body = render({
      type: 'dataRow',
      key: 'data',
      columns: [
        {
          key: 'asset',
          text: 'BTC',
          secondaryLeadingText: '1',
          secondaryText: 'Bitcoin',
        },
        { key: 'price', text: '$1' },
      ],
      badges: [{ key: 'tag', text: 'Tag' }],
      style: {
        columns: { fontSize: 22, color: '#112233', alignment: 'end' },
        columnSecondary: { fontSize: 11, color: '#445566' },
        lineGap: 9,
        titleBadgeGap: 12,
      },
    });
    const primary = body.querySelector<HTMLElement>(
      '[data-nl-slot="columns"]'
    )!;
    const secondary = Array.from(
      body.querySelectorAll<HTMLElement>('[data-nl-slot="columnSecondary"]')
    );
    expect(primary.style.fontSize).toBe('22px');
    expect(primary.textContent).toBe('BTC');
    expect(primary.style.textAlign).toBe('end');
    expect(secondary.map((node) => node.textContent)).toEqual(['1', 'Bitcoin']);
    expect(secondary.every((node) => node.style.fontSize === '11px')).toBe(
      true
    );
    expect(
      body.querySelector<HTMLElement>('[data-nl-slot="badge"]')!.style.fontSize
    ).toBe('');
    expect(
      body.querySelector<HTMLElement>('.ok-native-list-data-cell')!.style.rowGap
    ).toBe('9px');
  });

  it('keeps explicit media dimensions and independent caption spacing', () => {
    const body = render({
      type: 'mediaTile',
      variant: 'gallery',
      key: 'media',
      title: 'Title',
      subtitle: 'Subtitle',
      image,
      style: {
        image: {
          width: 80,
          height: 60,
          shape: 'square',
          contentFit: 'contain',
        },
        leadingGap: 13,
        lineGap: 5,
      },
    });
    const bitmap = body.querySelector<HTMLElement>(
      '.ok-native-list-media-image'
    )!;
    expect(bitmap.style.width).toBe('80px');
    expect(bitmap.style.height).toBe('60px');
    expect(bitmap.style.objectFit).toBe('contain');
    expect(bitmap.style.borderRadius).toBe('0px');
    expect(
      body.querySelector<HTMLElement>('.ok-native-list-media-meta')!.style
        .rowGap
    ).toBe('5px');
  });

  it('keeps title and badge styling independent even with search highlights', () => {
    const body = render({
      type: 'identity',
      leading: { kind: 'icon', name: 'coin' },
      key: 'badge',
      title: 'Bitcoin',
      titleMatch: [{ start: 0, end: 3 }],
      badges: [{ key: 'tag', text: 'Tag' }],
      style: {
        title: { fontSize: 24, color: '#112233' },
        badge: { fontSize: 10 },
        titleBadgeGap: 7,
      },
    });
    const title = body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
    const badge = body.querySelector<HTMLElement>('[data-nl-slot="badge"]')!;
    expect(title.textContent).toBe('Bitcoin');
    expect(title.contains(badge)).toBe(false);
    expect(
      title.querySelector<HTMLElement>('.ok-native-list-info')!.style.color
    ).toBe('rgb(17, 34, 51)');
    expect(badge.style.fontSize).toBe('10px');
  });

  it('applies missing status, rich subtitle and retry action roles', () => {
    const activity = render({
      type: 'activity',
      leading: { kind: 'icon', name: 'coin' },
      key: 'a',
      title: 'Sent',
      status: 'Pending',
      style: { status: { fontSize: 19 } },
    });
    expect(
      activity.querySelector<HTMLElement>('[data-nl-slot="status"]')!.style
        .fontSize
    ).toBe('19px');
    const identity = render({
      type: 'identity',
      leading: { kind: 'icon', name: 'coin' },
      key: 'i',
      title: 'Wallet',
      subtitleSegments: [{ text: 'Balance' }],
      style: { subtitle: { fontSize: 18 } },
    });
    expect(
      identity.querySelector<HTMLElement>('[data-nl-slot="subtitle"]')!.style
        .fontSize
    ).toBe('18px');
    const retry = render({
      type: 'system',
      key: 's',
      variant: 'retry',
      message: 'Failed',
      actionText: 'Try again',
      actionKey: 'retry',
      style: { actionText: { fontSize: 20 } },
    });
    const action = retry.querySelector<HTMLElement>(
      '[data-nl-slot="actionText"]'
    )!;
    expect(action.textContent).toBe('Try again');
    expect(action.style.fontSize).toBe('20px');
    expect(action.dataset.nativeListAction).toBe('retry');
  });

  it('applies header accessory spacing and keeps rail badge before status', () => {
    const header = render({
      type: 'sectionHeader',
      key: 'h',
      sectionKey: 'assets',
      title: 'Assets',
      value: '$1',
      checkbox: {
        kind: 'checkbox',
        state: 'unchecked',
        target: { scope: 'list' },
      },
      style: { trailingGap: 17 },
    });
    expect(
      (header.lastElementChild as HTMLElement).style.marginInlineStart
    ).toContain('17px');
    const rail = render({
      type: 'rail',
      visual: { kind: 'icon', name: 'coin' },
      key: 'r',
      title: 'Wallet',
      badge: { key: 'tag', text: 'Tag' },
      status: 'online',
    });
    expect(
      Array.from(rail.querySelectorAll<HTMLElement>('[data-nl-slot]')).map(
        (node) => node.dataset.nlSlot
      )
    ).toEqual(['title', 'badge', 'status']);
  });

  it('styles the first value accessory independently of preceding controls', () => {
    const body = render({
      type: 'identity',
      key: 'value',
      title: 'Wallet',
      leading: { kind: 'icon', name: 'coin' },
      trailing: [
        { kind: 'checkbox', state: 'unchecked' },
        { kind: 'valuePair', primary: '$10', secondary: '2 BTC' },
      ],
      style: { value: { fontSize: 19, color: '#123456' } },
    });
    const value = body.querySelector<HTMLElement>('[data-nl-slot="value"]')!;
    expect(value.textContent).toBe('$10\n2 BTC');
    expect(value.style.fontSize).toBe('19px');
    expect(
      Array.from(value.children).every(
        (run) => (run as HTMLElement).style.fontSize === '19px'
      )
    ).toBe(true);
    expect(body.querySelector('[data-nl-slot="valueSecondary"]')).toBeNull();
  });

  it('tags each slot with the model field it renders', () => {
    const body = render({
      type: 'message',
      key: 'notification',
      title: 'Title',
      body: 'Body',
      time: '1m',
    });
    expect(body.querySelector('[data-nl-slot="title"]')?.textContent).toBe(
      'Title'
    );
    expect(body.querySelector('[data-nl-slot="body"]')?.textContent).toBe(
      'Body'
    );
    expect(body.querySelector('[data-nl-slot="time"]')?.textContent).toBe('1m');
  });

  it('styles the named model field, not the view that carries it', () => {
    // metricCard renders `value` through the view identity uses for `title`.
    const body = render({
      type: 'metricCard',
      key: 'kpi',
      title: 'Volume',
      value: '42',
      style: {
        title: { fontSize: 11 },
        value: { fontSize: 22, fontWeight: 'bold' },
      },
    });
    const label = body.querySelector<HTMLElement>('[data-nl-slot="title"]');
    const value = body.querySelector<HTMLElement>('[data-nl-slot="value"]');
    expect(label?.textContent).toBe('Volume');
    expect(label?.style.fontSize).toBe('11px');
    expect(value?.textContent).toBe('42');
    expect(value?.style.fontSize).toBe('22px');
    expect(value?.style.fontWeight).toBe('700');
  });

  it.each(['activity', 'performance'] as const)(
    'targets the %s metric heading without restyling nested metric values',
    (variant) => {
      const body = render({
        type: 'metricCard',
        key: 'composite',
        variant,
        title: 'Summary',
        value: 'Unused standard value',
        metrics: [
          { key: 'a', label: 'Sent', value: '12' },
          { key: 'b', label: 'Received', value: '34' },
        ],
        style: {
          title: { color: '#ff0000' },
          value: { color: '#00ff00' },
        },
      });
      const heading = body.querySelector<HTMLElement>('[data-nl-slot="title"]');
      expect(heading?.textContent).toBe('Summary');
      expect(heading?.style.color).toBe('rgb(255, 0, 0)');
      expect(body.querySelector('[data-nl-slot="value"]')).toBeNull();
      expect(body.textContent).toContain('12');
      expect(body.textContent).toContain('34');
      expect(body.querySelector('[style*="rgb(0, 255, 0)"]')).toBeNull();
    }
  );

  it('keeps wallet member typography independent from the group and siblings', () => {
    const parent: IdentityRow = {
      type: 'identity',
      key: 'wallet',
      presentation: 'walletSidebar',
      leading: { kind: 'icon', name: 'StarOutline' },
      title: 'Parent wallet',
      style: { title: { color: '#ff0000' }, lineGap: 7 },
    };
    const body = render({
      type: 'walletGroup',
      key: 'wallet',
      parent,
      children: [
        { ...parent, key: 'child', title: 'Child wallet', style: undefined },
      ],
      style: { horizontalPadding: 13 },
    });
    const titles = body.querySelectorAll<HTMLElement>('[data-nl-slot="title"]');
    expect(titles).toHaveLength(2);
    expect(titles[0]?.style.color).toBe('rgb(255, 0, 0)');
    expect(titles[1]?.style.color).toBe('');
    expect(
      titles[0]?.closest<HTMLElement>('.ok-native-list-flex')?.style.rowGap
    ).toBe('7px');
    expect(
      titles[1]?.closest<HTMLElement>('.ok-native-list-flex')?.style.rowGap
    ).toBe('');
  });

  it("keeps today's numbers as the chrome fallbacks", () => {
    // An untouched list must render exactly as before, so every chrome variable
    // carries the current value as its CSS fallback.
    expect(WEB_LIST_CSS).toContain(
      'border-bottom:1px solid var(--nl-separator-color,var(--nl-separator))'
    );
    expect(WEB_LIST_CSS).toContain('border-radius:var(--nl-group-radius,12px)');
    // The inset variant keeps the transparent border so row height is unchanged.
    expect(WEB_LIST_CSS).toContain('border-bottom-color:transparent');
    expect(WEB_LIST_CSS).toContain(
      'inset-inline-start:var(--nl-separator-inset,0)'
    );
  });

  it('applies box padding only when the row asks for it', () => {
    const base: RowModel = {
      type: 'identity',
      key: 'btc',
      leading: { kind: 'icon', name: 'coin' },
      title: 'Bitcoin',
    };
    expect(render(base).style.paddingInline).toBe('');
    const styled = render({
      ...base,
      style: { horizontalPadding: 16, verticalPadding: 10 },
    } as RowModel);
    expect(styled.style.paddingInline).toBe('16px');
    expect(styled.style.paddingBlock).toBe('10px');
  });

  it.each(['accountSelector', 'networkSelector'] as const)(
    'applies %s styles after presentation defaults and restores them on removal',
    (presentation) => {
      const { document } = new JSDOM('<!doctype html><body></body>').window;
      const host = document.createElement('div');
      document.body.appendChild(host);
      const base: IdentityRow = {
        type: 'identity',
        key: 'selector',
        height: 76,
        presentation,
        leading: { kind: 'icon', name: 'StarOutline' },
        title: 'Selector title',
      };
      const engine = new NativeListWebEngine(
        host,
        snapshot({ kind: 'sectioned' }, [
          {
            ...base,
            style: {
              title: { fontSize: 18, lineHeight: 28, fontWeight: 'bold' },
            },
          },
        ]),
        {},
        false
      );
      try {
        const title = () =>
          host.querySelector<HTMLElement>('[data-nl-slot="title"]');
        expect(title()?.style.fontSize).toBe('18px');
        expect(title()?.style.lineHeight).toBe('28px');
        expect(title()?.style.fontWeight).toBe('700');
        const item = host.querySelector<HTMLElement>(
          '[data-native-list-row-key="selector"]'
        );
        const height = item?.style.height;
        engine.applySnapshot(snapshot({ kind: 'sectioned' }, [base]));
        expect(title()?.style.lineHeight).toBe('24px');
        expect(title()?.style.fontWeight).not.toBe('700');
        expect(item?.style.height).toBe(height);
      } finally {
        engine.destroy();
      }
    }
  );
});

describe('DataRow renderer lifecycle', () => {
  const dataSnapshot = (
    layout: NativeListSnapshot['layout'],
    items: readonly RowModel[]
  ): NativeListSnapshot => ({ schemaVersion: 1, generation: 1, layout, rows: items });
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => { window: { document: Document } };
  };
  it('retains the image while removing columns and restoring text styles', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: Extract<RowModel, { type: 'dataRow' }> = {
      type: 'dataRow',
      key: 'data',
      leading: { kind: 'image', image },
      index: 2,
      columns: [
        { key: 'asset', text: 'Bitcoin', secondaryText: 'BTC', weight: 2 },
        { key: 'price', text: '$42,000', alignment: 'end' },
        { key: 'change', text: '+2.4%' },
      ],
    };
    const engine = new NativeListWebEngine(
      host,
      dataSnapshot({ kind: 'table' }, [row]),
      {},
      false
    );
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="dataRow"]'
      )!;
      const leading = body.querySelector('img');
      engine.applySnapshot(
        dataSnapshot({ kind: 'table' }, [
          {
            ...row,
            style: {
              columns: { fontSize: 22, lines: 2 },
              columnSecondary: { color: '#ff0000' },
              image: { width: 28, height: 30 },
            },
          },
        ])
      );
      expect(body.querySelector('img')).toBe(leading);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="columns"]')?.style
          .fontSize
      ).toBe('22px');
      engine.applySnapshot(
        dataSnapshot({ kind: 'table' }, [
          {
            ...row,
            columns: [
              { key: 'asset', text: 'Ether' },
              { key: 'price', text: '$2,000' },
            ],
          },
        ])
      );
      expect(host.querySelector('[data-nl-renderer="dataRow"]')).toBe(body);
      expect(body.querySelector('img')).toBe(leading);
      expect(body.querySelectorAll('[data-nl-slot="columns"]')).toHaveLength(2);
      expect(
        body.querySelectorAll('[data-nl-slot="columnSecondary"]')
      ).toHaveLength(0);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="columns"]')?.style
          .fontSize
      ).not.toBe('22px');
      expect(body.textContent).not.toContain('$42,000');
      engine.applySnapshot(dataSnapshot({ kind: 'table' }, [row]));
      expect(body.querySelectorAll('[data-nl-slot="columns"]')).toHaveLength(3);
      expect(body.textContent).toContain('BTC');
    } finally {
      engine.destroy();
    }
  });
});
