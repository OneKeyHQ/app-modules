import React from 'react';
import type {
  ActivityRow,
  IdentityRow,
  NativeListSnapshot,
  RowModel,
} from '../models';
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
import { applyTextStyleToSlot } from '../web/templates/RowText';

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

  it('uses explicit header variants instead of row or section key naming', () => {
    const header = {
      type: 'sectionHeader',
      key: 'ordinary',
      sectionKey: 'ordinary',
      title: 'Date',
    } as const;
    const state = snapshot({ kind: 'sectioned' }, [header]);
    const height = estimateWebRowHeight(header, state, 320);
    for (const keys of [
      { key: 'history-today', sectionKey: 'ordinary' },
      { key: 'ordinary', sectionKey: 'history-today' },
      { key: 'ordinary', sectionKey: 'linear-tokens' },
      { key: 'ordinary', sectionKey: 'action-tokens' },
    ]) {
      expect(estimateWebRowHeight({ ...header, ...keys }, state, 320)).toBe(
        height
      );
      expect(
        estimateWebRowHeight(
          { ...header, ...keys, variant: 'history' },
          state,
          320
        )
      ).toBe(16);
      expect(
        estimateWebRowHeight(
          { ...header, ...keys, style: { container: { height: 30 } } },
          state,
          320
        )
      ).toBe(30);
    }
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

  test.each([true, false])(
    'contains rejected snapshots and patch batches (development=%s)',
    (development) => {
      const environment = globalThis as typeof globalThis & {
        __DEV__: boolean;
      };
      const previous = environment.__DEV__;
      environment.__DEV__ = development;
      const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
      const { document } = new JSDOM('<!doctype html><body></body>').window;
      const host = document.createElement('div');
      document.body.appendChild(host);
      const row: IdentityRow = {
        type: 'identity',
        key: development ? 'sensitive-key' : ` ${'k'.repeat(257)} `,
        title: development ? 'Accepted' : `Accepted${'T'.repeat(4097)}`,
        leading: {
          kind: 'token',
          fallbackText: 'A',
          ...(!development
            ? { image: { uri: '', width: 32, height: 32 } }
            : {}),
        },
        style: { title: { token: '$bodySm' } },
      };
      const valid = snapshot({ kind: 'sectioned' }, [row]);
      const invalid = { ...valid, rows: [row, row] };
      const engine = new NativeListWebEngine(host, invalid, {}, false);
      try {
        expect(engine.currentSnapshot().rows).toEqual([]);
        engine.applySnapshot(valid);
        const accepted = engine.currentSnapshot();
        expect(accepted.rows[0]).toMatchObject({
          key: row.key,
          style: { title: { fontSize: 12 } },
        });
        expect(host.textContent).toContain('Accepted');
        expect(() => engine.applySnapshot(invalid)).not.toThrow();
        expect(engine.currentSnapshot()).toBe(accepted);
        expect(() =>
          engine.applyPatches([
            {
              type: 'identity',
              key: 'unknown-sensitive-key',
              changes: { title: 'Rejected' },
            },
          ])
        ).not.toThrow();
        expect(engine.currentSnapshot()).toBe(accepted);
        engine.applyPatches([
          {
            type: 'identity',
            key: row.key,
            changes: {
              title: 'Updated',
              style: { title: { token: '$bodyLg' } },
            },
          },
        ]);
        expect(host.textContent).toContain('Updated');
        expect(engine.currentSnapshot().rows[0]).toMatchObject({
          style: { title: { fontSize: 16, lineHeight: 24 } },
        });
        expect(JSON.stringify(warning.mock.calls)).not.toContain(
          'sensitive-key'
        );
      } finally {
        engine.destroy();
        environment.__DEV__ = previous;
        warning.mockRestore();
      }
    }
  );

  test.each([undefined, Array(257).fill(1)])(
    'contains unsafe production sparkline samples before changing the rendered snapshot (%j)',
    (points) => {
      const environment = globalThis as typeof globalThis & {
        __DEV__: boolean;
      };
      const previous = environment.__DEV__;
      environment.__DEV__ = false;
      const warning = jest.spyOn(console, 'warn').mockImplementation(() => {});
      const { document } = new JSDOM('<!doctype html><body></body>').window;
      const host = document.createElement('div');
      document.body.appendChild(host);
      const valid = snapshot({ kind: 'linear' }, [rows[2]]);
      const engine = new NativeListWebEngine(host, valid, {}, false);
      const malformed: RowModel = {
        type: 'market',
        key: 'chart',
        variant: 'token',
        title: 'Chart',
        price: '$1',
        change: { text: '0%', tone: 'neutral' },
        leading: { kind: 'token', fallbackText: 'C' },
        webTable: {
          columns: [{ key: 'chart', width: 100 }],
          cells: [
            { key: 'chart', kind: 'sparkline', points, width: 100, height: 40 },
          ],
        },
      } as unknown as RowModel;
      try {
        expect(() =>
          engine.applySnapshot(snapshot({ kind: 'linear' }, [malformed]))
        ).not.toThrow();
        expect(engine.currentSnapshot()).toBe(valid);
        expect(host.textContent).toContain('Identity');
      } finally {
        engine.destroy();
        environment.__DEV__ = previous;
        warning.mockRestore();
      }
    }
  );

  test('renders and updates whitespace keys without changing action identity', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const key = '\tmarket-token \u00a0';
    const market: Extract<RowModel, { type: 'market' }> = {
      type: 'market',
      key,
      variant: 'token',
      leading: { kind: 'token', fallbackText: 'T' },
      title: 'TOKEN ',
      price: '$1',
      change: { text: '0%', tone: 'neutral' },
      pressActionKey: 'open',
    };
    const onRowAction = jest.fn();
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [market, { ...market, key: key.trim() }]),
      { onRowAction },
      false
    );
    const previousElement = global.Element;
    global.Element = document.defaultView!.Element;
    try {
      const elements = Array.from(
        host.querySelectorAll<HTMLElement>('[data-native-list-row-key]')
      );
      expect(
        elements.map((element) => element.dataset.nativeListRowKey)
      ).toEqual([key, key.trim()]);
      engine.applyPatches([
        { type: 'market', key, changes: { price: '$2', revision: 2 } },
      ]);
      expect(elements[0]!.textContent).toContain('$2');
      expect(elements[1]!.textContent).toContain('$1');
      elements[0]!.click();
      expect(onRowAction).toHaveBeenLastCalledWith(
        expect.objectContaining({ rowKey: key, actionKey: 'open' })
      );
      elements[1]!.click();
      expect(onRowAction).toHaveBeenLastCalledWith(
        expect.objectContaining({ rowKey: key.trim(), actionKey: 'open' })
      );
    } finally {
      engine.destroy();
      global.Element = previousElement;
    }
  });

  test('font configuration is instance-local, literal, and preserves patched rows', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const firstHost = document.createElement('div');
    const secondHost = document.createElement('div');
    document.body.append(firstHost, secondHost);
    const initial = snapshot({ kind: 'sectioned' }, [
      { type: 'message', key: 'm', title: 'Initial', body: 'Body', time: '' },
    ]);
    const literal = 'Host, "Quoted" Family';
    const first = new NativeListWebEngine(
      firstHost,
      initial,
      {},
      false,
      undefined,
      JSON.stringify({ regular: literal, medium: 'Medium' })
    );
    const second = new NativeListWebEngine(secondHost, initial, {}, false);
    try {
      const root = firstHost.querySelector<HTMLElement>(
        '.ok-native-list-root'
      )!;
      expect(root.style.getPropertyValue('--nl-font-regular')).toBe(
        `${JSON.stringify(
          literal
        )},-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif`
      );
      const secondRoot = secondHost.querySelector<HTMLElement>(
        '.ok-native-list-root'
      )!;
      expect(
        secondRoot.style.getPropertyValue('--nl-font-regular')
      ).not.toContain('Host');
      first.applyPatches([
        { type: 'message', key: 'm', changes: { title: 'Patched' } },
      ]);
      first.setFontFamiliesJson(JSON.stringify({ bold: 'Bold' }));
      expect(firstHost.textContent).toContain('Patched');
      expect(firstHost.textContent).not.toContain('Initial');
      expect(root.style.getPropertyValue('--nl-font-regular')).not.toContain(
        'Host'
      );
      expect(root.style.getPropertyValue('--nl-font-bold')).toContain('"Bold"');
      const preview = document.querySelector<HTMLElement>(
        '.ok-native-list-reorder-preview'
      )!;
      expect(preview.style.getPropertyValue('--nl-font-bold')).toBe(
        root.style.getPropertyValue('--nl-font-bold')
      );
      first.setFontFamiliesJson('{}');
      expect(root.style.getPropertyValue('--nl-font-bold')).not.toContain(
        'Bold'
      );
      expect(secondRoot.style.getPropertyValue('--nl-font-bold')).not.toContain(
        'Bold'
      );
    } finally {
      first.destroy();
      second.destroy();
    }
  });

  test('semibold CSS templates use the configured face through updates and reset', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.append(host);
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }),
      {},
      false,
      undefined,
      JSON.stringify({ semibold: 'Host Semibold' })
    );
    try {
      const root = host.querySelector<HTMLElement>('.ok-native-list-root')!;
      const rules = Array.from(
        document.querySelector<HTMLStyleElement>('style')!.sheet!.cssRules
      ).filter((rule): rule is CSSStyleRule => 'selectorText' in rule);
      const selectors = [
        '.ok-native-list-title',
        '.ok-native-list-action-title',
        '.ok-native-list-action-button',
        '.ok-native-list-visual-fallback',
        '.ok-native-list-composite-value',
        '.ok-native-list-index-preview',
        '.ok-native-list-reorder-count',
      ];
      const fallback = '-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif';
      for (const face of ['Host Semibold', 'Updated Semibold', undefined]) {
        engine.setFontFamiliesJson(JSON.stringify({ semibold: face }));
        for (const selector of selectors) {
          const rule = rules.find((entry) => entry.selectorText === selector)!;
          const font =
            rule.style.getPropertyValue('font-family') ||
            rule.style.getPropertyValue('font');
          const variable = font.match(/var\((--nl-font-[a-z]+)\)/)?.[1];
          expect(variable).toBeDefined();
          expect(root.style.getPropertyValue(variable!)).toBe(
            face === undefined
              ? fallback
              : `${JSON.stringify(face)},${fallback}`
          );
        }
      }
    } finally {
      engine.destroy();
    }
  });

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

  it('scales Market subscript runs with explicit typography and legacy change colors', () => {
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
      Array.from(price.querySelectorAll('span')).map((run) => [
        run.style.fontSize,
        run.style.lineHeight,
      ])
    ).toEqual([
      ['18px', ''],
      // Legacy subscript metrics: ceil(size * 0.6) on a field-size line box.
      ['11px', '18px'],
    ]);
    expect(
      Array.from(
        change.querySelectorAll<HTMLElement>(
          '.ok-native-list-text-content > span'
        )
      ).map((run) => run.style.fontSize)
    ).toEqual(['17px', '11px']);
    expect(change.style.color).toBe('rgb(18, 52, 86)');
    const styledLine = render({
      type: 'market',
      key: 'market-rich-line',
      variant: 'token',
      title: 'Market',
      leading: { kind: 'icon', name: 'coin' },
      price: '$01',
      priceSegments: [{ text: '$0' }, { text: '1', style: 'subscript' }],
      change: { text: '+1', tone: 'positive' },
      style: { price: { fontSize: 20, lineHeight: 28 } },
    }).querySelectorAll<HTMLElement>('.ok-native-list-market-price span');
    expect(
      Array.from(styledLine).map((run) => [
        run.style.fontSize,
        run.style.lineHeight,
      ])
    ).toEqual([
      ['20px', '28px'],
      ['12px', '20px'],
    ]);
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

  it.each([false, true])(
    'removes and restores the Activity leading slot on %s rich rows',
    (rich) => {
      const { document } = new JSDOM('<!doctype html><body></body>').window;
      const host = document.createElement('div');
      document.body.appendChild(host);
      const row: ActivityRow = {
        type: 'activity',
        key: 'tx',
        leading: { kind: 'image', image },
        title: 'Send',
        description: 'To Alice',
        footerActions: [{ key: 'speed-up', label: 'Speed up' }],
        ...(rich ? { amounts: [{ key: 'send', text: '-1 ETH' }] } : {
          primaryAmount: '-1 ETH',
        }),
      };
      const engine = new NativeListWebEngine(
        host,
        snapshot({ kind: 'sectioned' }, [row]),
        {},
        false
      );
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="activity"]'
      )!;
      const visual = () => rich
        ? body.querySelector<HTMLElement>('.ok-native-list-history-visual')!
        : body;
      const action = () => body.querySelector<HTMLElement>(
        '[data-native-list-action="speed-up"]'
      )!;
      try {
        expect(visual().querySelector('img')).not.toBeNull();
        engine.applySnapshot(snapshot({ kind: 'sectioned' }, [{
          ...row, hideLeading: true,
        }]));
        expect(visual().querySelector('img')).toBeNull();
        expect(body.textContent).toContain('-1 ETH');
        expect(action()).not.toBeNull();
        if (rich) {
          expect(visual().style.display).toBe('none');
          expect(body.querySelector<HTMLElement>(
            '.ok-native-list-history-identity'
          )!.style.gap).toBe('0');
          expect(body.querySelector<HTMLElement>(
            '.ok-native-list-history-actions'
          )!.style.paddingLeft).toBe('0px');
        } else {
          expect(body.firstElementChild?.className).toBe('ok-native-list-flex');
        }
        engine.applySnapshot(snapshot({ kind: 'sectioned' }, [row]));
        expect(visual().querySelector('img')).not.toBeNull();
        if (rich) {
          expect(visual().style.display).not.toBe('none');
          expect(body.querySelector<HTMLElement>(
            '.ok-native-list-history-actions'
          )!.style.paddingLeft).toBe('52px');
        }
      } finally {
        engine.destroy();
      }
    }
  );

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
    // Text styles rebuild the text nodes (see the set→clear regression test);
    // the renderer host is what stays.
    const title = () =>
      body.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
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
      expect(title().style.fontSize).toBe('18px');
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [warning]));
      expect(host.querySelector('[data-nl-renderer="system"]')).toBe(body);
      expect(title().style.fontSize).toBe('');
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
      // Legacy Web: a non-Market retry shows only its message; the row press
      // carries the retry action.
      expect(
        body.querySelector('[data-native-list-action="retry"]')
      ).toBeNull();
      expect(body.textContent).toBe('Retry connection');
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
    // Legacy Web draws no retry button, so `style.actionText` has no Web
    // target (it styles the native retry button only).
    const retry = render({
      type: 'system',
      key: 's',
      variant: 'retry',
      presentation: 'market',
      message: 'Failed',
      actionText: 'Try again',
      actionKey: 'retry',
      style: { actionText: { fontSize: 20 }, message: { fontSize: 15 } },
    });
    expect(retry.querySelector('[data-nl-slot="actionText"]')).toBeNull();
    expect(retry.querySelector('button')).toBeNull();
    expect(
      retry.querySelector<HTMLElement>('[data-nl-slot="message"]')!.style
        .fontSize
    ).toBe('15px');
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
  ): NativeListSnapshot => ({
    schemaVersion: 1,
    generation: 1,
    layout,
    rows: items,
  });
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

describe('MetricCard renderer lifecycle', () => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => { window: { document: Document } };
  };
  it('restores standard and composite defaults without restarting unchanged images', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: Extract<RowModel, { type: 'metricCard' }> = {
      type: 'metricCard',
      key: 'metric',
      title: 'Portfolio',
      value: '$42,000',
      subtitle: 'Total balance',
      trend: '+2.4%',
      visual: { kind: 'image', image },
      metrics: [
        {
          key: 'volume',
          label: 'Volume',
          value: '$42M',
          visual: { kind: 'image', image },
        },
        { key: 'trades', label: 'Trades', value: '123' },
        { key: 'change', label: 'Change', value: '+2.4%' },
      ],
      progress: 0.5,
    };
    const metricSnapshot = (item: typeof row): NativeListSnapshot => ({
      schemaVersion: 1,
      generation: 1,
      layout: { kind: 'linear' },
      rows: [item],
    });
    const engine = new NativeListWebEngine(
      host,
      metricSnapshot(row),
      {},
      false
    );
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="metricCard"]'
      )!;
      const leading = body.querySelector('img');
      const styled: typeof row = {
        ...row,
        style: {
          container: { height: 240 },
          image: { width: 28, height: 30 },
          value: { fontSize: 22 },
          lineGap: 9,
        },
      };
      engine.applySnapshot(metricSnapshot(styled));
      expect(body.querySelector('img')).toBe(leading);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="value"]')?.style
          .fontSize
      ).toBe('22px');
      // A content update during styling must not promote styles into defaults.
      engine.applySnapshot(metricSnapshot({ ...styled, value: '$43,000' }));
      engine.applySnapshot(metricSnapshot({ ...row, value: '$43,000' }));
      expect(body.querySelector('img')).toBe(leading);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="value"]')?.style
          .fontSize
      ).toBe('');
      expect(
        body.querySelector<HTMLElement>('.ok-native-list-visual')?.style.width
      ).toBe('40px');
      for (const variant of ['activity', 'performance'] as const) {
        const composite = { ...row, variant };
        engine.applySnapshot(metricSnapshot(composite));
        expect(host.querySelector('[data-nl-renderer="metricCard"]')).toBe(
          body
        );
        const icon = body.querySelector('img');
        engine.applySnapshot(
          metricSnapshot({
            ...composite,
            style: {
              title: { fontSize: 20 },
              value: { fontSize: 30 },
              image: { width: 80, height: 80 },
            },
          })
        );
        expect(body.querySelector('img')).toBe(icon);
        expect(
          body.querySelector<HTMLElement>('[data-nl-slot="title"]')?.style
            .fontSize
        ).toBe('20px');
        expect(
          body.querySelector<HTMLElement>('.ok-native-list-composite-value')
            ?.style.fontSize
        ).toBe('');
        expect(
          body.querySelector<HTMLElement>('.ok-native-list-visual')?.style.width
        ).toBe('16px');
        engine.applySnapshot(metricSnapshot(composite));
        expect(
          body.querySelector<HTMLElement>('[data-nl-slot="title"]')?.style
            .fontSize
        ).toBe('');
        expect(body.querySelectorAll('.ok-native-list-progress')).toHaveLength(
          variant === 'performance' ? 1 : 0
        );
      }
      engine.applySnapshot(metricSnapshot(row));
      expect(
        body.querySelectorAll('.ok-native-list-composite-cell')
      ).toHaveLength(0);
      expect(
        body.querySelector<HTMLElement>('.ok-native-list-visual')?.style.width
      ).toBe('40px');
    } finally {
      engine.destroy();
    }
  });
});

describe('complex renderer lifecycle', () => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => { window: { document: Document } };
  };
  const snap = (row: RowModel): NativeListSnapshot => ({
    schemaVersion: 1,
    generation: 1,
    layout: { kind: 'linear' },
    rows: [row],
  });
  it('keeps Identity badges beside a one-line title', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const style = document.createElement('style');
    style.textContent = WEB_LIST_CSS;
    document.head.appendChild(style);
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: IdentityRow = {
      type: 'identity', key: 'badged', title: 'ETH',
      leading: { kind: 'token', image },
      badges: [{ key: 'multichain', text: 'Multichain', tone: 'neutral' }],
      style: { title: { lines: 1 } },
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      const title = host.querySelector<HTMLElement>('.ok-native-list-title')!;
      const text = title.querySelector<HTMLElement>('[data-nl-slot="title"]')!;
      const badges = title.querySelector<HTMLElement>('.ok-native-list-badges')!;
      expect(title.style.display).toBe('flex');
      expect(title.style.alignItems).toBe('center');
      expect(text.nextElementSibling).toBe(badges);
      expect(text.style.minWidth).toBe('0');
      expect(text.style.whiteSpace).toBe('nowrap');
      expect(document.defaultView!.getComputedStyle(badges.firstElementChild!).height)
        .toBe('20px');
      expect(document.defaultView!.getComputedStyle(badges.firstElementChild!).fontSize)
        .toBe('12px');
      engine.applySnapshot(snap({ ...row, badges: [
        { key: 'multichain', text: 'Multichain' },
      ] }));
      const defaultBadge = host.querySelector<HTMLElement>('.ok-native-list-badge')!;
      expect(defaultBadge.dataset.tone).toBeUndefined();
      expect(document.defaultView!.getComputedStyle(defaultBadge).height)
        .toBe('18px');
      expect(document.defaultView!.getComputedStyle(defaultBadge).fontSize)
        .toBe('11px');
    } finally {
      engine.destroy();
    }
  });
  it('opens icon-only help and clears it on press, rebind and touch toggle', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: IdentityRow = {
      type: 'identity', key: 'gas', title: 'ETH',
      leading: { kind: 'token', image },
      titleIcon: { name: 'GasSolid', hoverActionKey: 'native-token-info' },
    };
    const onRowAction = jest.fn();
    const onActionAnchorInvalidated = jest.fn();
    const engine = new NativeListWebEngine(host, snap(row), {
      onRowAction, onActionAnchorInvalidated,
    }, false);
    const previousElement = global.Element;
    const previousNode = global.Node;
    global.Element = document.defaultView!.Element;
    global.Node = document.defaultView!.Node;
    const icon = () => host.querySelector<HTMLElement>(
      '.ok-native-list-identity-title-icon'
    )!;
    const pointer = (type: string, pointerType: string) =>
      icon().dispatchEvent(Object.assign(
        new document.defaultView!.Event(type, { bubbles: true }),
        { pointerType }
      ));
    try {
      expect(icon().tabIndex).toBe(-1);
      pointer('pointerover', 'touch');
      expect(onRowAction).not.toHaveBeenCalled();
      pointer('pointerover', 'mouse');
      expect(onRowAction).toHaveBeenCalledTimes(1);
      const event = onRowAction.mock.calls[0]![0];
      expect(event).toMatchObject({
        rowKey: 'gas', actionKey: 'native-token-info',
        anchor: { source: 'leadingAction' },
      });
      engine.setActionAnchorState({ token: event.anchor.token, open: true });
      pointer('pointerdown', 'mouse');
      expect(onActionAnchorInvalidated).toHaveBeenCalledTimes(1);
      pointer('pointerover', 'mouse');
      expect(onRowAction).toHaveBeenCalledTimes(1);
      pointer('pointerout', 'mouse');
      pointer('pointerdown', 'touch');
      expect(onRowAction).toHaveBeenCalledTimes(2);
      const touchEvent = onRowAction.mock.calls[1]![0];
      engine.setActionAnchorState({ token: touchEvent.anchor.token, open: true });
      pointer('pointerout', 'touch');
      expect(onActionAnchorInvalidated).toHaveBeenCalledTimes(1);
      pointer('pointerdown', 'touch');
      expect(onActionAnchorInvalidated).toHaveBeenCalledTimes(2);
      pointer('pointerover', 'mouse');
      const reboundEvent = onRowAction.mock.calls[2]![0];
      engine.setActionAnchorState({ token: reboundEvent.anchor.token, open: true });
      engine.applySnapshot(snap({ ...row, titleIcon: { name: 'GasSolid' } }));
      expect(onActionAnchorInvalidated).toHaveBeenCalledTimes(3);
      expect(icon().dataset.nativeListHoverAction).toBeUndefined();
      pointer('pointerover', 'mouse');
      expect(onRowAction).toHaveBeenCalledTimes(3);
    } finally {
      engine.destroy();
      global.Element = previousElement;
      global.Node = previousNode;
    }
  });
  it('clears an explicit image skeleton on load, failure and rebind', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: IdentityRow = {
      type: 'identity', key: 'visual-loading', title: 'ETH',
      leading: { kind: 'token', image: { ...image, loadingStrategy: 'skeleton' } },
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      const visual = () => host.querySelector<HTMLElement>('.ok-native-list-visual')!;
      expect(visual().querySelector('.ok-native-list-image-skeleton')).not.toBeNull();
      visual().querySelector('img')!.dispatchEvent(
        new document.defaultView!.Event('load')
      );
      expect(visual().querySelector('.ok-native-list-image-skeleton')).toBeNull();
      engine.applySnapshot(snap({ ...row, key: 'failed-loading' }));
      expect(visual().querySelector('.ok-native-list-image-skeleton')).not.toBeNull();
      visual().querySelector('img')!.dispatchEvent(
        new document.defaultView!.Event('error')
      );
      expect(visual().querySelector('.ok-native-list-image-skeleton')).toBeNull();
      engine.applySnapshot(snap({ ...row, key: 'plain', leading: {
        kind: 'token', image: { ...image, loadingStrategy: 'none' },
      } }));
      expect(visual().querySelector('.ok-native-list-image-skeleton')).toBeNull();
    } finally {
      engine.destroy();
    }
  });
  it('isolates identity buttons and clears risk controls on rebind', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const onRowAction = jest.fn();
    const first: IdentityRow = {
      type: 'identity',
      key: 'risk',
      title: 'A long risk token symbol',
      titleIcon: { name: 'GasSolid', tintColor: '#888888' },
      leading: {
        kind: 'token',
        image,
        borderColor: '#111111',
        overlays: [
          { position: 'bottomRight', text: 'E', fontWeight: 'semibold' },
        ],
      },
      style: { leadingContentMaxWidthRatio: 0.6 },
      trailing: [
        { kind: 'button', label: 'Show', actionKey: 'show', testID: 'risk-show' },
      ],
    };
    const engine = new NativeListWebEngine(
      host,
      snap(first),
      { onRowAction },
      false
    );
    const originalElement = global.Element;
    global.Element = document.defaultView!.Element;
    try {
      const button = host.querySelector<HTMLButtonElement>(
        '.ok-native-list-trailing-button'
      )!;
      expect(button.type).toBe('button');
      expect(button.dataset.testid).toBe('risk-show');
      expect(host.querySelector('.ok-native-list-identity-title-icon')).not.toBeNull();
      expect(host.querySelector<HTMLElement>('.ok-native-list-flex')?.style.maxWidth).toContain('60%');
      expect(host.querySelector<HTMLElement>('.ok-native-list-accessories')?.style.marginInlineStart).toBe('auto');
      expect(host.querySelector<HTMLElement>('.ok-native-list-visual')?.style.borderColor).toBe('rgb(17, 17, 17)');
      expect(host.querySelector<HTMLElement>('.ok-native-list-visual-overlay')?.style.fontWeight).toBe('600');
      button.click();
      expect(onRowAction).toHaveBeenCalledTimes(1);
      expect(onRowAction.mock.calls[0]![0]).toMatchObject({
        rowKey: 'risk', actionKey: 'show',
        anchor: { source: 'trailingAccessory', slot: 0 },
      });
      engine.applySnapshot(snap({
        ...first,
        trailing: [{ kind: 'button', label: 'Hide', actionKey: 'hide', disabled: true }],
      }));
      host.querySelector<HTMLButtonElement>('.ok-native-list-trailing-button')!.click();
      expect(onRowAction).toHaveBeenCalledTimes(1);
      engine.applySnapshot(snap({
        ...first,
        key: 'other-risk',
        titleIcon: undefined,
        leading: { kind: 'token', image, overlays: [{ position: 'bottomRight', text: 'E' }] },
        style: undefined,
        trailing: [{ kind: 'icon', name: 'EditOutline', actionKey: 'edit' }],
      }));
      expect(host.querySelector('.ok-native-list-trailing-button')).toBeNull();
      expect(host.querySelector('.ok-native-list-identity-title-icon')).toBeNull();
      expect(host.querySelector<HTMLElement>('.ok-native-list-visual')?.style.borderColor).toBe('');
      expect(host.querySelector<HTMLElement>('.ok-native-list-visual-overlay')?.style.fontWeight).toBe('');
      expect(host.querySelector<HTMLElement>('.ok-native-list-flex')?.style.maxWidth).toBe('');
      engine.applySnapshot(snap({ ...first, key: 'third-risk', titleIcon: undefined, trailing: [] }));
      expect(host.querySelector('[data-native-list-action="show"]')).toBeNull();
      expect(host.querySelector('[data-native-list-action="edit"]')).toBeNull();
    } finally {
      engine.destroy();
      global.Element = originalElement;
    }
  });
  it('renders official risk glyphs and swaps overlay image failure to fallback', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: IdentityRow = {
      type: 'identity', key: 'risk-icons', title: 'Token',
      titleIcon: { name: 'GasSolid' },
      leading: { kind: 'token', fallbackIcon: {
        name: 'CryptoCoinOutline', size: 28, backgroundColor: '#eeeeee',
      }, overlays: [{ position: 'bottomRight', size: 20, padding: 2,
        image, fallbackIcon: { name: 'GlobusOutline' } }] },
      trailing: [{ kind: 'icon', name: 'CryptoCoinOutline', actionKey: 'edit', buttonStyle: 'tertiary' }],
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      expect(host.querySelector('.ok-native-list-identity-title-icon svg path')
        ?.getAttribute('d')).toContain('M15 9h4v7');
      expect(host.querySelector('.ok-native-list-visual-fallback svg path')
        ?.getAttribute('d')).toContain('M11 16.387');
      expect(host.querySelector<HTMLElement>('.ok-native-list-visual')?.style.background)
        .toBe('rgb(238, 238, 238)');
      expect(host.querySelector('.ok-native-list-tertiary-icon')).not.toBeNull();
      const style = document.createElement('style');
      style.textContent = WEB_LIST_CSS;
      document.head.appendChild(style);
      const tertiaryFocus = Array.from(style.sheet!.cssRules).find((rule) =>
        rule.cssText.startsWith(
          '.ok-native-list-icon-button.ok-native-list-tertiary-icon:focus-visible:not(:disabled)'
        )
      );
      expect(tertiaryFocus?.cssText).toContain('outline: 2px solid var(--nl-accent)');
      const networkImage = host.querySelector<HTMLImageElement>(
        '.ok-native-list-visual-overlay img'
      )!;
      expect(host.querySelector('.ok-native-list-overlay-fallback')).toBeNull();
      networkImage.dispatchEvent(new document.defaultView!.Event('error'));
      expect(host.querySelector('.ok-native-list-visual-overlay img')).toBeNull();
      expect(host.querySelector('.ok-native-list-overlay-fallback svg path')).not.toBeNull();
      engine.applySnapshot(snap({ ...row, titleIcon: undefined,
        leading: { kind: 'token', overlays: [{ position: 'bottomRight', text: 'E' }] },
        trailing: [],
      }));
      expect(host.querySelector('.ok-native-list-overlay-fallback')).toBeNull();
      expect(host.querySelector('.ok-native-list-tertiary-icon')).toBeNull();
    } finally {
      engine.destroy();
    }
  });
  it('keeps Market image slots and action context through quotes, styles and clear', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: Extract<RowModel, { type: 'market' }> = {
      type: 'market',
      key: 'quote',
      variant: 'token',
      leading: { kind: 'token', image },
      title: 'Bitcoin',
      price: '$42',
      change: { text: '+2%', tone: 'positive' },
      badges: [{ key: 'badge', text: 'Info', icon: image, actionKey: 'info' }],
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="market"]'
      )!;
      const images = Array.from(body.querySelectorAll('img'));
      images.forEach((bitmap) =>
        bitmap.dispatchEvent(new document.defaultView!.Event('load'))
      );
      const badge = body.querySelector('.ok-native-list-market-badge');
      engine.applyPatches([
        {
          type: 'market',
          key: row.key,
          changes: { price: '$43', change: { text: '-1%', tone: 'negative' } },
        },
      ]);
      expect(body.querySelector('.ok-native-list-market-badge')).toBe(badge);
      expect(
        body.querySelector('.ok-native-list-market-price')?.textContent
      ).toBe('$43');
      const styled = {
        ...row,
        title: 'Updated',
        style: {
          container: { height: 100 },
          image: { width: 38 },
          title: { fontSize: 19 },
          price: { fontSize: 20 },
        },
      };
      engine.applySnapshot(snap(styled));
      expect(host.querySelector('[data-nl-renderer="market"]')).toBe(body);
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      expect(images.every((bitmap) => bitmap.style.opacity === '1')).toBe(true);
      engine.applySnapshot(snap(row));
      expect(Array.from(body.querySelectorAll('img'))).toEqual(images);
      expect(images.every((bitmap) => bitmap.style.opacity === '1')).toBe(true);
      expect(
        body.querySelector<HTMLElement>('.ok-native-list-market-title')?.style
          .fontSize
      ).toBe('16px');
      expect(
        body.querySelector<HTMLElement>('.ok-native-list-visual')?.style.width
      ).toBe('32px');
    } finally {
      engine.destroy();
    }
  });
  it('retains Identity images while replacing accessories and switching to a header on the same key', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: IdentityRow = {
      type: 'identity',
      key: 'complex',
      title: 'Wallet',
      leading: { kind: 'account', image },
      trailing: [{ kind: 'value', text: '$42' }],
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="identity"]'
      )!;
      const imageElement = body.querySelector('img');
      engine.applySnapshot(
        snap({
          ...row,
          title: 'Renamed',
          trailing: [{ kind: 'menu', actionKey: 'menu' }],
          style: { image: { width: 30 }, title: { fontSize: 20 } },
        })
      );
      expect(body.querySelector('img')).toBe(imageElement);
      expect(body.textContent).not.toContain('$42');
      engine.applySnapshot(snap(row));
      expect(body.querySelector('img')).toBe(imageElement);
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="title"]')?.style
          .fontSize
      ).toBe('');
      engine.applySnapshot(
        snap({
          type: 'sectionHeader',
          key: row.key,
          sectionKey: 'a',
          variant: 'summary',
          title: 'Assets',
          value: '$100',
        })
      );
      expect(host.querySelector('[data-nl-renderer="identity"]')).toBeNull();
      expect(
        host.querySelector('[data-nl-renderer="sectionHeader"]')?.textContent
      ).toBe('Assets$100');
    } finally {
      engine.destroy();
    }
  });
  it('reconciles WalletGroup members by key and clears local styles without replacing images', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const parent: IdentityRow = {
      type: 'identity',
      key: 'group',
      presentation: 'walletSidebar',
      title: 'Parent',
      leading: { kind: 'wallet', image },
      height: 68,
    };
    const child = { ...parent, key: 'child', title: 'Child' };
    const removed = { ...parent, key: 'removed', title: 'Removed' };
    const row: Extract<RowModel, { type: 'walletGroup' }> = {
      type: 'walletGroup',
      key: parent.key,
      parent,
      children: [child, removed],
    };
    const engine = new NativeListWebEngine(host, snap(row), {}, false);
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="walletGroup"]'
      )!;
      const member = body.querySelector<HTMLElement>(
        '[data-native-list-group-member-key="child"]'
      )!;
      const imageElement = member.querySelector('img');
      const discarded = body.querySelector<HTMLElement>(
        '[data-native-list-group-member-key="removed"]'
      )!;
      engine.applySnapshot(
        snap({
          ...row,
          children: [
            removed,
            {
              ...child,
              style: {
                container: { height: 96, opacity: 0.6 },
                title: { fontSize: 21 },
              },
            },
          ],
          style: {
            horizontalPadding: 9,
            verticalPadding: 3,
            container: { backgroundColor: '#123456' },
          },
        })
      );
      expect(body.children[2]).toBe(member);
      expect(member.querySelector('img')).toBe(imageElement);
      expect(member.style.height).toBe('96px');
      expect(body.style.paddingInline).toBe('8px');
      expect(
        body.querySelector<HTMLElement>('[data-nl-slot="title"]')!.style
          .fontSize
      ).not.toContain('21px');
      engine.applySnapshot(snap({ ...row, children: [child] }));
      expect(body.children).toHaveLength(2);
      expect(body.children[1]).toBe(member);
      expect(member.querySelector('img')).toBe(imageElement);
      expect(member.style.height).toBe('68px');
      expect(member.style.opacity).toBe('1');
      expect(
        member.querySelector<HTMLElement>('[data-nl-slot="title"]')!.style
          .fontSize
      ).toBe('');
      expect(body.style.paddingInline).toBe('');
      expect(body.style.getPropertyValue('--nl-container-background')).toBe('');
      expect(
        discarded.querySelector('[data-nl-renderer="identity"]')!.childNodes
      ).toHaveLength(0);
      engine.applySnapshot(snap(parent));
      expect(host.querySelector('[data-nl-renderer="walletGroup"]')).toBeNull();
      expect(
        host.querySelector('[data-nl-renderer="identity"]')!.textContent
      ).toContain('Parent');
    } finally {
      engine.destroy();
    }
  });
  it('keeps legacy disabled group member actions and drag after rebinding', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const member: IdentityRow = {
      type: 'identity',
      key: 'child',
      presentation: 'walletSidebar',
      title: 'Child',
      leading: { kind: 'icon', name: 'StarOutline' },
      trailing: [
        { kind: 'menu', actionKey: 'menu' },
        { kind: 'checkbox', target: { scope: 'row' }, state: 'unchecked' },
      ],
    };
    const row: Extract<RowModel, { type: 'walletGroup' }> = {
      type: 'walletGroup',
      key: 'parent',
      parent: { ...member, key: 'parent' },
      children: [member],
    };
    const onRowAction = jest.fn();
    const onSelectionDelta = jest.fn();
    const engine = new NativeListWebEngine(
      host,
      { ...snap(row), selection: { mode: 'multiple', selectedKeys: [] } },
      { onRowAction, onSelectionDelta },
      false
    );
    const previousElement = global.Element;
    global.Element = document.defaultView!.Element;
    const buttons = () =>
      host
        .querySelector('[data-native-list-group-member-key="child"]')!
        .querySelectorAll<HTMLButtonElement>('button');
    try {
      buttons()[0]!.click();
      expect(onRowAction.mock.calls[0]![0]).toMatchObject({
        rowKey: 'child',
        actionKey: 'menu',
      });
      engine.applySnapshot(
        snap({ ...row, children: [{ ...member, disabled: true }] })
      );
      // Legacy Web: a disabled member keeps its own actions.
      buttons()[0]!.click();
      expect(onRowAction).toHaveBeenCalledTimes(2);
      expect(onRowAction.mock.calls[1]![0]).toMatchObject({
        rowKey: 'child',
        actionKey: 'menu',
      });
      // Legacy: a disabled member can still start the atomic group drag;
      // only `draggable: false` excludes it.
      expect(
        canStartWebWalletGroupReorder(
          { ...row, children: [{ ...member, disabled: true }] },
          'child'
        )
      ).toBe(true);
      expect(
        canStartWebWalletGroupReorder(
          { ...row, children: [{ ...member, draggable: false }] },
          'child'
        )
      ).toBe(false);
      // Legacy: the disabled member's own press stays blocked.
      host
        .querySelector<HTMLElement>(
          '[data-native-list-group-member-key="child"]'
        )!
        .click();
      expect(onRowAction).toHaveBeenCalledTimes(2);
    } finally {
      engine.destroy();
      global.Element = previousElement;
    }
  });
  it('keeps legacy WalletGroup member hover actions keyed on the member only', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const window = document.defaultView!;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const member: IdentityRow = {
      type: 'identity',
      key: 'child',
      presentation: 'walletSidebar',
      title: 'Child',
      leading: { kind: 'icon', name: 'StarOutline' },
      titleActionKey: 'help',
      titleActionOnHover: true,
    };
    const row: Extract<RowModel, { type: 'walletGroup' }> = {
      type: 'walletGroup',
      key: 'parent',
      disabled: true,
      parent: { ...member, key: 'parent', titleActionKey: undefined },
      children: [member],
    };
    const onRowAction = jest.fn();
    const engine = new NativeListWebEngine(
      host,
      snap(row),
      { onRowAction },
      false
    );
    const previousElement = global.Element;
    const previousNode = global.Node;
    global.Element = document.defaultView!.Element;
    global.Node = document.defaultView!.Node;
    const hover = () =>
      host
        .querySelector<HTMLElement>(
          '[data-native-list-group-member-key="child"] [data-native-list-hover-action]'
        )!
        .dispatchEvent(new window.Event('pointerover', { bubbles: true }));
    try {
      // Legacy Web checks only the member's `disabled`, not the group's.
      hover();
      expect(onRowAction).toHaveBeenCalledTimes(1);
      expect(onRowAction.mock.calls[0]![0]).toMatchObject({
        rowKey: 'child',
        actionKey: 'help',
      });
      engine.applySnapshot(
        snap({ ...row, children: [{ ...member, disabled: true }] })
      );
      hover();
      expect(onRowAction).toHaveBeenCalledTimes(1);
    } finally {
      engine.destroy();
      global.Element = previousElement;
      global.Node = previousNode;
    }
  });
});

describe('legacy default regressions', () => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => {
      window: { document: Document; Event: typeof Event };
    };
  };
  const snap = (
    items: readonly RowModel[],
    layout: NativeListSnapshot['layout'] = { kind: 'linear' }
  ): NativeListSnapshot => ({
    schemaVersion: 1,
    generation: 1,
    layout,
    rows: items,
  });
  const mount = (initial: NativeListSnapshot) => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const host = window.document.createElement('div');
    window.document.body.appendChild(host);
    const engine = new NativeListWebEngine(host, initial, {}, false);
    return { window, host, engine };
  };

  it('keeps loaded MetricCard images visible across content and style rebinding', () => {
    const row: Extract<RowModel, { type: 'metricCard' }> = {
      type: 'metricCard',
      key: 'metric',
      title: 'Portfolio',
      value: '$42,000',
      visual: { kind: 'image', image },
    };
    const composite: typeof row = {
      ...row,
      key: 'composite',
      variant: 'activity',
      metrics: [
        { key: 'a', label: 'A', value: '1', visual: { kind: 'image', image } },
        { key: 'b', label: 'B', value: '2' },
      ],
    };
    const { window, host, engine } = mount(snap([row, composite]));
    try {
      const images = Array.from(host.querySelectorAll('img'));
      expect(images).toHaveLength(2);
      images.forEach((img) => {
        expect(img.style.opacity).toBe('0');
        img.dispatchEvent(new window.Event('load'));
        expect(img.style.opacity).toBe('1');
      });
      const updates: readonly (readonly RowModel[])[] = [
        // Content-only change rebuilds the template while reusing the image.
        [
          { ...row, value: '$43,000' },
          { ...composite, title: 'Changed' },
        ],
        // Style-only change restores defaults before applying the style.
        [
          { ...row, value: '$43,000', style: { value: { fontSize: 22 } } },
          {
            ...composite,
            title: 'Changed',
            style: { title: { fontSize: 20 } },
          },
        ],
        // Clearing the style restores defaults again.
        [
          { ...row, value: '$43,000' },
          { ...composite, title: 'Changed' },
        ],
      ];
      updates.forEach((items) => {
        engine.applySnapshot(snap(items));
        const current = Array.from(host.querySelectorAll('img'));
        expect(current).toEqual(images);
        current.forEach((img) => expect(img.style.opacity).toBe('1'));
      });
    } finally {
      engine.destroy();
    }
  });

  it('includes group vertical padding in WalletGroup height and keeps legacy member presets', () => {
    const member = (
      key: string,
      extra: Partial<IdentityRow> = {}
    ): IdentityRow => ({
      type: 'identity',
      key,
      presentation: 'walletSidebar',
      leading: { kind: 'wallet', fallbackText: key },
      title: key,
      ...extra,
    });
    const group: Extract<RowModel, { type: 'walletGroup' }> = {
      type: 'walletGroup',
      key: 'parent',
      // Legacy walletSidebar presets ignore `size`; badges add 24.
      parent: member('parent', { size: 'large' }),
      children: [
        member('badged', { badges: [{ key: 'hidden', text: 'Hidden' }] }),
        member('plain', { size: 'small' }),
      ],
    };
    const unstyled = 68 + 92 + 68 + 2 * 12;
    expect(estimateWebRowHeight(group, snap([group]), 320)).toBe(unstyled);
    const padded = { ...group, style: { verticalPadding: 10 } };
    expect(estimateWebRowHeight(padded, snap([padded]), 320)).toBe(
      unstyled + 20
    );
    // An explicit parent `height` keeps the legacy 2-unit border budget
    // (native: a 1-unit inset); a container-only height does not.
    const explicit = {
      ...group,
      parent: { ...group.parent, height: 60 },
    };
    expect(estimateWebRowHeight(explicit, snap([explicit]), 320)).toBe(
      60 + 92 + 68 + 2 * 12 + 2
    );
    const containerOnly = {
      ...group,
      parent: { ...group.parent, style: { container: { height: 60 } } },
    };
    expect(
      estimateWebRowHeight(containerOnly, snap([containerOnly]), 320)
    ).toBe(60 + 92 + 68 + 2 * 12);
    // `verticalPadding` replaces that inset instead of adding to it (native).
    const explicitPadded = { ...explicit, style: { verticalPadding: 10 } };
    expect(
      estimateWebRowHeight(explicitPadded, snap([explicitPadded]), 320)
    ).toBe(60 + 92 + 68 + 2 * 12 + 20);

    const { host, engine } = mount(snap([padded]));
    try {
      const wrappers = Array.from(
        host.querySelectorAll<HTMLElement>('.ok-native-list-wallet-member')
      );
      expect(wrappers.map((wrapper) => wrapper.style.height)).toEqual([
        '68px',
        '92px',
        '68px',
      ]);
      const body = host.querySelector<HTMLElement>(
        '.ok-native-list-wallet-group'
      )!;
      // The 1px group border is the first unit of the 10-unit inset, so the
      // rendered box matches the measured height exactly.
      expect(body.style.paddingBlock).toBe('9px');
      expect(
        host.querySelector<HTMLElement>('.ok-native-list-item')?.style.height
      ).toBe(`${unstyled + 20}px`);
    } finally {
      engine.destroy();
    }
  });

  it('keeps a renderer value that equals the previous container write', () => {
    const plain: IdentityRow = {
      type: 'identity',
      key: 'selector',
      leading: { kind: 'icon', name: 'StarOutline' },
      title: 'Selector',
      style: { container: { cornerRadius: 12 } },
    };
    const { host, engine } = mount(snap([plain]));
    const body = () =>
      host.querySelector<HTMLElement>('[data-nl-renderer="identity"]')!;
    try {
      const reused = body();
      expect(reused.style.borderRadius).toBe('12px');
      // The accountSelector preset re-writes the same 12px radius itself; the
      // stale container write must not revert it on this rebind.
      engine.applySnapshot(
        snap([
          {
            ...plain,
            presentation: 'accountSelector',
            height: 76,
            style: undefined,
          },
        ])
      );
      expect(body()).toBe(reused);
      expect(reused.style.borderRadius).toBe('12px');
      engine.applySnapshot(snap([{ ...plain, style: undefined }]));
      expect(reused.style.borderRadius).toBe('');
    } finally {
      engine.destroy();
    }
  });

  it('clears container alignment from reused Activity and DataRow children', () => {
    const activity: Extract<RowModel, { type: 'activity' }> = {
      type: 'activity',
      key: 'activity',
      leading: { kind: 'icon', name: 'coin' },
      title: 'Sent',
      primaryAmount: '-1 BTC',
    };
    const data: Extract<RowModel, { type: 'dataRow' }> = {
      type: 'dataRow',
      key: 'data',
      favorite: true,
      columns: [
        { key: 'asset', text: 'BTC' },
        { key: 'price', text: '$1' },
      ],
    };
    const aligned = (alignment: 'top' | 'bottom') => ({
      container: { contentVerticalAlignment: alignment },
    });
    const { host, engine } = mount(
      snap([
        { ...activity, style: aligned('bottom') },
        { ...data, style: aligned('bottom') },
      ])
    );
    try {
      const activityBody = host.querySelector<HTMLElement>(
        '[data-nl-renderer="activity"]'
      )!;
      const dataBody = host.querySelector<HTMLElement>(
        '[data-nl-renderer="dataRow"]'
      )!;
      const favorite = dataBody.querySelector<HTMLElement>(
        '.ok-native-list-favorite'
      )!;
      const column = activityBody.querySelector<HTMLElement>(
        ':scope > .ok-native-list-flex'
      )!;
      const amounts = activityBody.querySelector<HTMLElement>(
        '.ok-native-list-amounts'
      )!;
      [column, amounts, favorite].forEach((node) =>
        expect(node.style.alignSelf).toBe('flex-end')
      );
      engine.applySnapshot(
        snap([
          { ...activity, style: aligned('top') },
          { ...data, style: aligned('top') },
        ])
      );
      [column, amounts, favorite].forEach((node) =>
        expect(node.style.alignSelf).toBe('flex-start')
      );
      engine.applySnapshot(snap([activity, data]));
      expect(host.querySelector('[data-nl-renderer="activity"]')).toBe(
        activityBody
      );
      expect(host.querySelector('[data-nl-renderer="dataRow"]')).toBe(dataBody);
      [column, amounts, favorite].forEach((node) =>
        expect(node.style.alignSelf).toBe('')
      );
      expect(activityBody.style.alignItems).toBe('');
      expect(dataBody.style.alignItems).toBe('');
    } finally {
      engine.destroy();
    }
  });

  it('keeps legacy DataRow table/linear measurement', () => {
    const data = (secondary: boolean, size?: 'small'): RowModel => ({
      type: 'dataRow',
      key: `data-${secondary}-${size}`,
      size,
      columns: [
        {
          key: 'asset',
          text: 'BTC',
          secondaryText: secondary ? 'Bitcoin' : undefined,
        },
        { key: 'price', text: '$1' },
      ],
    });
    const items = [data(false), data(true), data(false, 'small')];
    const heights = (kind: 'table' | 'linear') =>
      computeWebListLayout(snap(items, { kind }), 320, 640).items.map(
        (item) => item.height
      );
    expect(heights('table')).toEqual([48, 60, 40]);
    expect(heights('linear')).toEqual([56, 60, 48]);
  });

  it('enables selector layout for row.height and style.container.height alike', () => {
    const selectorRows = (
      height: Partial<{
        height: number;
        style: { container: { height: number } };
      }>
    ): readonly RowModel[] => [
      {
        type: 'identity',
        key: 'account',
        presentation: 'accountSelector',
        leading: { kind: 'icon', name: 'coin' },
        title: 'Account',
        trailing: [
          { kind: 'icon', name: 'PlusSmallOutline', actionKey: 'create' },
        ],
        ...height,
      } as RowModel,
      {
        type: 'action',
        key: 'action',
        presentation: 'accountSelector',
        title: 'Add',
        actionKey: 'add',
        trailing: [
          { kind: 'icon', name: 'PlusSmallOutline', actionKey: 'create' },
        ],
        ...height,
      } as RowModel,
    ];
    const selectorState = (items: readonly RowModel[]) => {
      const { host, engine } = mount(snap(items));
      try {
        const identity = host.querySelector<HTMLElement>(
          '[data-nl-renderer="identity"]'
        )!;
        const action = host.querySelector<HTMLElement>(
          '[data-nl-renderer="action"]'
        )!;
        return {
          selector: identity.dataset.nativeListSelector,
          identityControl: identity.querySelector(
            '[data-native-list-account-control="createAddress"]'
          )
            ? 'createAddress'
            : undefined,
          actionControl: action.querySelector(
            '[data-native-list-account-control="createAddress"]'
          )
            ? 'createAddress'
            : undefined,
        };
      } finally {
        engine.destroy();
      }
    };
    const legacy = selectorState(selectorRows({ height: 58 }));
    expect(legacy).toEqual({
      selector: 'accountSelector',
      identityControl: 'createAddress',
      actionControl: 'createAddress',
    });
    expect(
      selectorState(selectorRows({ style: { container: { height: 58 } } }))
    ).toEqual(legacy);
    expect(selectorState(selectorRows({}))).toEqual({
      selector: undefined,
      identityControl: undefined,
      actionControl: undefined,
    });
  });

  it('styles text identically whether or not the row is attached', () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const style = window.document.createElement('style');
    style.textContent = WEB_LIST_CSS;
    window.document.head.appendChild(style);
    const output = (
      attached: boolean,
      text: Parameters<typeof applyTextStyleToSlot>[1]
    ) => {
      const element = window.document.createElement('span');
      element.className = 'ok-native-list-title';
      element.textContent = 'Title';
      if (attached) window.document.body.appendChild(element);
      applyTextStyleToSlot(element, text);
      const html = element.outerHTML;
      element.remove();
      return html;
    };
    for (const text of [
      { offsetY: 2 },
      { verticalAlignment: 'center' as const },
      { lines: 2 as const },
      { lines: 1 as const, truncate: 'clip' as const },
    ])
      expect(output(true, text)).toBe(output(false, text));
    expect(output(false, { offsetY: 2 })).toContain(
      'white-space: inherit; text-overflow: inherit'
    );
  });

  it('keeps the legacy Web DataRow column structure', () => {
    const data: Extract<RowModel, { type: 'dataRow' }> = {
      type: 'dataRow',
      key: 'data',
      badges: [{ key: 'b', text: 'NEW' }],
      columns: [
        {
          key: 'asset',
          text: 'BTC',
          secondaryLeadingText: '1',
          secondaryText: 'Bitcoin',
          secondaryTone: 'positive',
        },
        { key: 'price', text: '$1' },
      ],
    };
    const { host, engine } = mount(snap([data]));
    try {
      const cell = host.querySelector<HTMLElement>(
        '.ok-native-list-data-cell'
      )!;
      const primaryLine = cell.querySelector<HTMLElement>(
        '.ok-native-list-data-primary'
      )!;
      // Leading text, primary text and badges share the primary line.
      expect(primaryLine.textContent).toBe('1BTCNEW');
      const [leading, secondary] = Array.from(
        cell.querySelectorAll<HTMLElement>('[data-nl-slot="columnSecondary"]')
      );
      expect(leading?.parentElement).toBe(primaryLine);
      expect(leading?.style.color).toBe('');
      expect(secondary?.textContent).toBe('Bitcoin');
      expect(secondary?.style.color).toBe('var(--nl-positive)');
      expect(secondary?.parentElement?.style.display).toBe('contents');
      // Removing the secondary text removes the second line again.
      engine.applySnapshot(
        snap([
          {
            ...data,
            columns: [{ key: 'asset', text: 'BTC' }, data.columns[1]!],
          },
        ])
      );
      expect(
        cell.querySelectorAll('[data-nl-slot="columnSecondary"]')
      ).toHaveLength(0);
      expect(
        cell.querySelector<HTMLElement>('.ok-native-list-data-secondary-line')
          ?.style.display
      ).toBe('none');
    } finally {
      engine.destroy();
    }
  });

  it('renders every retry as a legacy message-only row that fires on press', () => {
    const retry: RowModel = {
      type: 'system',
      key: 'retry',
      variant: 'retry',
      message: 'Failed to load',
      actionText: 'Try again',
      actionKey: 'reload',
    };
    const { window } = new JSDOM('<!doctype html><body></body>');
    const host = window.document.createElement('div');
    window.document.body.appendChild(host);
    const onRowAction = jest.fn();
    const engine = new NativeListWebEngine(
      host,
      snap([retry, { ...retry, key: 'market', presentation: 'market' }]),
      { onRowAction },
      false
    );
    const domWindow = window as unknown as {
      Element: typeof Element;
      HTMLElement: typeof HTMLElement;
      KeyboardEvent: typeof KeyboardEvent;
    };
    const previousElement = global.Element;
    const previousHTMLElement = global.HTMLElement;
    global.Element = domWindow.Element;
    global.HTMLElement = domWindow.HTMLElement;
    try {
      const bodies = Array.from(
        host.querySelectorAll<HTMLElement>('[data-nl-renderer="system"]')
      );
      expect(bodies).toHaveLength(2);
      for (const body of bodies) {
        expect(body.querySelector('button')).toBeNull();
        expect(body.querySelector('[data-nl-slot="actionText"]')).toBeNull();
        expect(body.textContent).toBe('Failed to load');
      }
      bodies[0]!.click();
      bodies[1]!.click();
      bodies[1]!
        .closest<HTMLElement>('[data-native-list-row-key]')!
        .dispatchEvent(
          new domWindow.KeyboardEvent('keydown', {
            key: 'Enter',
            bubbles: true,
          })
        );
      expect(onRowAction.mock.calls.map(([event]) => event)).toEqual([
        expect.objectContaining({ rowKey: 'retry', actionKey: 'reload' }),
        expect.objectContaining({ rowKey: 'market', actionKey: 'reload' }),
        expect.objectContaining({ rowKey: 'market', actionKey: 'reload' }),
      ]);
    } finally {
      engine.destroy();
      global.Element = previousElement;
      global.HTMLElement = previousHTMLElement;
    }
  });

  it('lays out an automatic-height warning at its legacy DOM border-box height', () => {
    const warning: RowModel = {
      type: 'system',
      key: 'warning',
      variant: 'warning',
      title: 'Warning',
      message: 'A long message that wraps in the rendered row',
    };
    const { window } = new JSDOM('<!doctype html><body></body>');
    const domWindow = window as unknown as { HTMLElement: typeof HTMLElement };
    // Legacy read offsetHeight (padding + both borders + wrapped text).
    Object.defineProperty(domWindow.HTMLElement.prototype, 'offsetHeight', {
      configurable: true,
      get(this: HTMLElement) {
        return this.classList.contains('ok-native-list-warning') ? 150 : 0;
      },
    });
    const host = window.document.createElement('div');
    window.document.body.appendChild(host);
    const engine = new NativeListWebEngine(
      host,
      snap([
        warning,
        { ...warning, key: 'row-height', height: 80 },
        {
          ...warning,
          key: 'container-height',
          style: { container: { height: 90 } },
        },
      ]),
      {},
      false
    );
    const itemHeight = (key: string) =>
      host.querySelector<HTMLElement>(`[data-native-list-row-key="${key}"]`)!
        .style.height;
    try {
      expect(itemHeight('warning')).toBe('150px');
      expect(itemHeight('row-height')).toBe('80px');
      expect(itemHeight('container-height')).toBe('90px');
    } finally {
      engine.destroy();
    }
  });

  it('restores a fresh unstyled render after clearing System and MetricCard text layout styles', () => {
    const warning: RowModel = {
      type: 'system',
      key: 'warning',
      variant: 'warning',
      title: 'Warning',
      message: 'One\nTwo',
    };
    const metric: RowModel = {
      type: 'metricCard',
      key: 'metric',
      title: 'Portfolio',
      value: '$42,000',
      visual: { kind: 'image', image },
    };
    const markup = (host: HTMLElement, renderer: string) => {
      const body = host.querySelector<HTMLElement>(
        `[data-nl-renderer="${renderer}"]`
      )!;
      // Image load start times differ between the two engines by design.
      return body.outerHTML.replace(
        / data-native-list-image-started-at="[^"]*"/g,
        ''
      );
    };
    const fresh = mount(snap([warning, metric]));
    const reused = mount(snap([warning, metric]));
    try {
      const image = reused.host.querySelector('img');
      reused.engine.applySnapshot(
        snap([
          {
            ...warning,
            style: { message: { lines: 1, offsetY: 3 } },
          },
          { ...metric, style: { value: { offsetY: 4, lines: 1 } } },
        ])
      );
      const message = reused.host.querySelector<HTMLElement>(
        '[data-nl-slot="message"]'
      )!;
      expect(message.textContent).toBe('One Two');
      expect(
        message.querySelector<HTMLElement>('.ok-native-list-text-content')!
          .style.transform
      ).toBe('translateY(3px)');
      expect(
        reused.host.querySelector<HTMLElement>(
          '[data-nl-slot="value"] > .ok-native-list-text-content'
        )!.style.transform
      ).toBe('translateY(4px)');
      reused.engine.applySnapshot(snap([warning, metric]));
      expect(markup(reused.host, 'system')).toBe(markup(fresh.host, 'system'));
      expect(markup(reused.host, 'metricCard')).toBe(
        markup(fresh.host, 'metricCard')
      );
      expect(
        reused.host.querySelector('[data-nl-slot="message"]')!.textContent
      ).toBe('One\nTwo');
      // Rebuilding text reuses the loaded image node.
      expect(reused.host.querySelector('img')).toBe(image);
    } finally {
      fresh.engine.destroy();
      reused.engine.destroy();
    }
  });

  it('clears walletSidebar leadingGap from reused visuals, standalone and in a WalletGroup', () => {
    const sidebar: IdentityRow = {
      type: 'identity',
      key: 'sidebar',
      presentation: 'walletSidebar',
      title: 'Wallet',
      leading: { kind: 'wallet', image },
    };
    const group: Extract<RowModel, { type: 'walletGroup' }> = {
      type: 'walletGroup',
      key: 'group',
      parent: { ...sidebar, key: 'group', title: 'Parent' },
      children: [{ ...sidebar, key: 'child', title: 'Child' }],
    };
    const visuals = (host: HTMLElement) =>
      Array.from(
        host.querySelectorAll<HTMLElement>(
          '[data-nl-renderer="identity"] > .ok-native-list-visual'
        )
      );
    const fresh = mount(snap([sidebar, group]));
    const reused = mount(snap([sidebar, group]));
    try {
      const nodes = visuals(reused.host);
      expect(nodes).toHaveLength(3);
      const styled = { leadingGap: 10 };
      reused.engine.applySnapshot(
        snap([
          { ...sidebar, style: styled },
          {
            ...group,
            parent: { ...group.parent, style: styled },
            children: [{ ...group.children[0]!, style: styled }],
          },
        ])
      );
      expect(visuals(reused.host)).toEqual(nodes);
      nodes.forEach((node) => expect(node.style.marginBottom).not.toBe(''));
      reused.engine.applySnapshot(snap([sidebar, group]));
      expect(visuals(reused.host)).toEqual(nodes);
      // Restoring re-sets properties, so compare declarations, not order.
      const declarations = (node: HTMLElement) =>
        node.style.cssText
          .split(';')
          .map((part) => part.trim())
          .filter(Boolean)
          .sort();
      expect(nodes.map(declarations)).toEqual(
        visuals(fresh.host).map(declarations)
      );
      nodes.forEach((node) => expect(node.style.marginBottom).toBe(''));
    } finally {
      fresh.engine.destroy();
      reused.engine.destroy();
    }
  });
});

describe('Web migration layout and table regressions', () => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => { window: { document: Document } };
  };

  function commandFixture(external = true) {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const view = document.defaultView!;
    const page = document.createElement('div');
    const scrollHost = document.createElement('div');
    const rowsHost = document.createElement('div');
    scrollHost.dataset.collapsiblePagerScroll = 'true';
    page.append(scrollHost);
    scrollHost.append(rowsHost);
    document.body.append(page);
    let ready = true;
    let origin = 120;
    let frameId = 0;
    const frames = new Map<number, FrameRequestCallback>();
    const requestFrame = (callback: FrameRequestCallback) => {
      frames.set(++frameId, callback);
      return frameId;
    };
    const cancelFrame = (id: number) => frames.delete(id);
    const globals = [
      'Element',
      'requestAnimationFrame',
      'cancelAnimationFrame',
    ];
    const originalGlobals = globals.map((key) =>
      Object.getOwnPropertyDescriptor(globalThis, key)
    );
    for (const [key, value] of Object.entries({
      Element: view.Element,
      requestAnimationFrame: requestFrame,
      cancelAnimationFrame: cancelFrame,
    }))
      Object.defineProperty(globalThis, key, { configurable: true, value });
    Object.assign(view, {
      requestAnimationFrame: requestFrame,
      cancelAnimationFrame: cancelFrame,
    });
    // JSDOM has no DOM scrollTo. Keep its realm method separate from RNW's
    // instance decoration so calling the wrong signature cannot pass silently.
    const domScroll = jest.fn(function (
      this: HTMLElement,
      options: ScrollToOptions
    ) {
      if (options.top !== undefined) this.scrollTop = options.top;
      if (options.left !== undefined) this.scrollLeft = options.left;
    });
    Object.defineProperty(view.Element.prototype, 'scrollTo', {
      configurable: true,
      value: domScroll,
    });
    const rnwScroll = jest.fn((options: { x?: number; y?: number }) => {
      scrollHost.scrollTop = options.y ?? 0;
      scrollHost.scrollLeft = options.x ?? 0;
    });
    Object.defineProperty(scrollHost, 'scrollTo', {
      configurable: true,
      value: rnwScroll,
    });
    Object.defineProperties(scrollHost, {
      clientWidth: { value: 360 },
      clientHeight: { get: () => (ready ? 240 : 0) },
      scrollHeight: { value: 3000 },
    });
    const rect = (top: number, height: number) => ({
      x: 20,
      y: top,
      left: 20,
      right: 380,
      top,
      bottom: top + height,
      width: 360,
      height,
      toJSON: () => ({}),
    });
    scrollHost.getBoundingClientRect = () => rect(20, 240);
    rowsHost.getBoundingClientRect = () =>
      rect(20 + origin - scrollHost.scrollTop, 1200);
    const sourceRows: RowModel[] = Array.from({ length: 30 }, (_, index) => ({
      type: 'message',
      key: `command-${index}`,
      height: 40,
      title: 'Row',
      body: '',
      time: '',
    }));
    const hostAddListener = jest.spyOn(scrollHost, 'addEventListener');
    const engine = new NativeListWebEngine(
      rowsHost,
      { ...snapshot({ kind: 'linear' }, sourceRows), capabilities: {} },
      {},
      false,
      null,
      '{}',
      external
        ? { scrollHost, contentBounds: rowsHost, stickyInset: 20 }
        : undefined
    );
    const viewport = rowsHost.querySelector<HTMLElement>(
      '.ok-native-list-viewport'
    )!;
    Object.defineProperties(viewport, {
      clientWidth: { value: 360 },
      clientHeight: { get: () => (ready ? 240 : 0) },
    });
    viewport.getBoundingClientRect = rowsHost.getBoundingClientRect;
    const resize = () => view.dispatchEvent(new view.Event('resize'));
    resize();
    const flushFrames = () => {
      for (let count = 0; frames.size; count += 1) {
        if (count === 20) throw new Error('Unexpected recurring scroll frame');
        const pending = [...frames.entries()];
        frames.clear();
        pending.forEach(([, callback]) => callback(0));
      }
    };
    return {
      document,
      page,
      scrollHost,
      viewport,
      engine,
      domScroll,
      rnwScroll,
      hostAddListener,
      resize,
      flushFrames,
      frames,
      setReady: (value: boolean) => {
        ready = value;
      },
      setOrigin: (value: number) => {
        origin = value;
      },
      cleanup: () => {
        engine.destroy();
        globals.forEach((key, index) => {
          const descriptor = originalGlobals[index];
          if (descriptor) Object.defineProperty(globalThis, key, descriptor);
          else Reflect.deleteProperty(globalThis, key);
        });
        view.close();
      },
    };
  }

  it('consumes Pager inset changes before restoring its external owner without losing later slot anchoring', () => {
    const f = commandFixture();
    try {
      f.setOrigin(0);
      f.resize();
      f.setOrigin(345);
      f.scrollHost.dispatchEvent(
        new f.document.defaultView!.Event('ok-collapsible-pager-insets-changed')
      );
      // Pager restores C after configuring the inner inset; the subsequent
      // ResizeObserver must not treat that inset as new list content.
      f.scrollHost.scrollTop = 212;
      f.resize();
      expect(f.scrollHost.scrollTop).toBe(212);
      f.scrollHost.scrollTop = 600;
      f.setOrigin(385);
      f.resize();
      expect(f.scrollHost.scrollTop).toBe(640);
    } finally {
      f.cleanup();
    }
  });

  it('uses realm DOM commands at nonzero offsets on an RNW-decorated external owner', () => {
    const f = commandFixture();
    try {
      f.scrollHost.scrollTop = 80;
      f.engine.scrollToOffset(200, false);
      expect(f.scrollHost.scrollTop).toBe(320);
      f.engine.scrollToIndex(5, {
        alignment: 'start',
        animated: false,
        viewPosition: 0,
        viewOffset: 0,
      });
      expect(f.scrollHost.scrollTop).toBe(300);
      f.engine.scrollToEnd(false);
      expect(f.scrollHost.scrollTop).toBe(1080);
      expect(f.domScroll.mock.contexts).toEqual([
        f.scrollHost,
        f.scrollHost,
        f.scrollHost,
      ]);
      expect(f.rnwScroll).not.toHaveBeenCalled();
      expect(f.viewport.scrollTop).toBe(0);
    } finally {
      f.cleanup();
    }
  });

  it('hands pending commands between the real Pager and List on their shared external owner', () => {
    const { CollapsiblePagerView } = jest.requireActual(
      '../../../react-native-pager-view/src/CollapsiblePagerView.web'
    ) as {
      CollapsiblePagerView: new (props: {
        header: null;
        stickyHeader: null;
        headerHeight: number;
        stickyHeaderHeight: number;
        children: React.ReactNode;
      }) => {
        scrollToPageTop: (options: { animated: boolean }) => void;
        scrollToStickyTop: (options: { animated: boolean }) => void;
        componentWillUnmount: () => void;
      };
    };
    const f = commandFixture();
    const pager = new CollapsiblePagerView({
      header: null,
      stickyHeader: null,
      headerHeight: 80,
      stickyHeaderHeight: 40,
      children: React.createElement('div', { key: 'page' }),
    });
    const internals = pager as unknown as {
      pageHosts: Map<number, HTMLElement>;
      configureActiveScrollElement: () => void;
    };
    internals.pageHosts.set(0, f.page);
    internals.configureActiveScrollElement();
    const accepted: EventTarget[] = [];
    f.scrollHost.addEventListener(
      'ok-native-list-scroll-command-accepted',
      (e) => accepted.push(e.target!)
    );
    try {
      f.scrollHost.scrollTop = 300;
      f.setReady(false);
      f.engine.scrollToOffset(600, false);
      f.viewport.dispatchEvent(
        new f.document.defaultView!.Event(
          'ok-collapsible-pager-cancel-scroll-command',
          { bubbles: true }
        )
      );
      f.setReady(true);
      f.resize();
      expect(f.scrollHost.scrollTop).toBe(720);
      f.setReady(false);
      f.engine.scrollToOffset(600, false);
      pager.scrollToStickyTop({ animated: false });
      f.setReady(true);
      f.resize();
      f.flushFrames();
      expect(f.scrollHost.scrollTop).toBe(80);

      pager.scrollToPageTop({ animated: false });
      // A bubbling event from the inner list viewport is not a new owner command.
      f.viewport.dispatchEvent(
        new f.document.defaultView!.Event(
          'ok-native-list-scroll-command-accepted',
          { bubbles: true }
        )
      );
      const acceptedBeforeInvalid = accepted.length;
      expect(() => f.engine.scrollToOffset(-1, false)).toThrow();
      expect(accepted).toHaveLength(acceptedBeforeInvalid);
      f.flushFrames();
      expect(f.scrollHost.scrollTop).toBe(0);

      pager.scrollToPageTop({ animated: false });
      f.engine.scrollToOffset(270, false);
      f.flushFrames();
      expect(f.scrollHost.scrollTop).toBe(390);
      expect(accepted.at(-1)).toBe(f.scrollHost);
      expect(f.rnwScroll).not.toHaveBeenCalled();
    } finally {
      pager.componentWillUnmount();
      f.cleanup();
    }
  });

  it.each([true, false])(
    'removes owner listeners and queued commands on destroy (external=%s)',
    (external) => {
      const f = commandFixture(external);
      const owner = external ? f.scrollHost : f.viewport;
      const removed = jest.spyOn(owner, 'removeEventListener');
      const cancel = () =>
        owner.dispatchEvent(
          new f.document.defaultView!.Event(
            'ok-collapsible-pager-cancel-scroll-command'
          )
        );
      try {
        owner.scrollTop = 125;
        f.engine.scrollToOffset(200, false);
        expect(owner.scrollTop).toBe(external ? 320 : 200);
        cancel();
        expect(f.domScroll).toHaveBeenLastCalledWith({
          left: 0,
          top: owner.scrollTop,
          behavior: 'instant',
        });
        f.setReady(false);
        f.engine.scrollToOffset(900, false);
        expect(f.frames.size).toBeGreaterThan(0);
        f.engine.destroy();
        expect(f.frames.size).toBe(0);
        if (external) {
          const cancelBinding = f.hostAddListener.mock.calls.find(
            ([type]) => type === 'ok-collapsible-pager-cancel-scroll-command'
          );
          const scrollBinding = f.hostAddListener.mock.calls.find(
            ([type]) => type === 'scroll'
          );
          const insetBinding = f.hostAddListener.mock.calls.find(
            ([type]) => type === 'ok-collapsible-pager-insets-changed'
          );
          expect(insetBinding).toBeDefined();
          expect(removed).toHaveBeenCalledWith(
            insetBinding![0],
            insetBinding![1]
          );
          expect(cancelBinding).toBeDefined();
          expect(scrollBinding).toBeDefined();
          expect(removed).toHaveBeenCalledWith(
            cancelBinding![0],
            cancelBinding![1]
          );
          expect(scrollBinding![2]).toEqual({ passive: true, capture: true });
          expect(removed).toHaveBeenCalledWith(
            'scroll',
            scrollBinding![1],
            true
          );
          expect(
            f.hostAddListener.mock.contexts.every((target) => target === owner)
          ).toBe(true);
          expect(
            removed.mock.contexts.every((target) => target === owner)
          ).toBe(true);
        } else
          expect(removed).toHaveBeenCalledWith(
            'ok-collapsible-pager-cancel-scroll-command',
            expect.any(Function)
          );
        const callsAfterDestroy = f.domScroll.mock.calls.length;
        f.setReady(true);
        cancel();
        owner.dispatchEvent(new f.document.defaultView!.Event('scroll'));
        f.resize();
        f.flushFrames();
        expect(f.frames.size).toBe(0);
        expect(f.domScroll).toHaveBeenCalledTimes(callsAfterDestroy);
        expect(f.rnwScroll).not.toHaveBeenCalled();
      } finally {
        f.cleanup();
      }
    }
  );

  it('captures only external host and intermediate ancestor scrolls, then removes capture on destroy', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    const pager = document.createElement('div');
    const rowsHost = document.createElement('div');
    const unrelated = document.createElement('div');
    host.append(pager, unrelated);
    pager.append(rowsHost);
    document.body.append(host);
    const originalElement = Object.getOwnPropertyDescriptor(
      globalThis,
      'Element'
    );
    Object.defineProperty(globalThis, 'Element', {
      configurable: true,
      value: document.defaultView!.Element,
    });
    const engine = new NativeListWebEngine(
      rowsHost,
      snapshot({ kind: 'sectioned' }, [
        {
          type: 'message',
          key: 'scroll-row',
          title: 'Row',
          body: '',
          time: '',
        },
      ]),
      {},
      false,
      null,
      '{}',
      { scrollHost: host }
    );
    const scroll = jest.spyOn(
      engine as unknown as { handleScroll: () => void },
      'handleScroll'
    );
    const dispatch = (target: HTMLElement) =>
      target.dispatchEvent(new document.defaultView!.Event('scroll'));
    try {
      dispatch(pager);
      expect(scroll).toHaveBeenCalledTimes(1);
      dispatch(host);
      expect(scroll).toHaveBeenCalledTimes(2);
      dispatch(unrelated);
      expect(scroll).toHaveBeenCalledTimes(2);
      dispatch(rowsHost);
      const root = rowsHost.querySelector<HTMLElement>('.ok-native-list-root')!;
      const slot = document.createElement('div');
      root.append(slot);
      dispatch(root);
      dispatch(slot);
      expect(scroll).toHaveBeenCalledTimes(3);
      engine.destroy();
      dispatch(pager);
      dispatch(host);
      expect(scroll).toHaveBeenCalledTimes(3);
      expect(host.scrollTop).toBe(0);
    } finally {
      engine.destroy();
      if (originalElement) {
        Object.defineProperty(globalThis, 'Element', originalElement);
      } else {
        Reflect.deleteProperty(globalThis, 'Element');
      }
    }
  });

  it('gates table pointer reveal on primary hover while retaining keyboard focus reveal', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const style = document.createElement('style');
    style.textContent = WEB_LIST_CSS;
    document.head.append(style);
    const rules = Array.from(style.sheet!.cssRules);
    expect(WEB_LIST_CSS).toContain(
      '.ok-native-list-table-secondary{display:block;min-width:0;overflow:clip}'
    );
    const media = rules.filter(
      (rule): rule is CSSMediaRule => 'media' in rule && 'cssRules' in rule
    );
    const hover = media.find(
      (rule) => rule.media.mediaText.replace(/\s/g, '') === '(hover:hover)'
    )!;
    expect(hover).toBeDefined();
    expect(hover.cssText).toContain('.ok-native-list-market-table:hover');
    expect(
      rules.some(
        (rule) =>
          !('media' in rule) &&
          rule.cssText.includes('.ok-native-list-market-table:hover')
      )
    ).toBe(false);
    expect(
      rules.some(
        (rule) =>
          !('media' in rule) &&
          rule.cssText.includes('.ok-native-list-market-table:focus-within')
      )
    ).toBe(true);
  });

  it('keeps compact touch holds for pointer-only micro-moves but cancels real touch movement', () => {
    jest.useFakeTimers();
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.append(host);
    const originalElement = Object.getOwnPropertyDescriptor(
      globalThis,
      'Element'
    );
    Object.defineProperty(globalThis, 'Element', {
      configurable: true,
      value: document.defaultView!.Element,
    });
    const onRowAction = jest.fn();
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [
        {
          type: 'market',
          key: 'touch-hold',
          variant: 'token',
          title: 'Token',
          leading: { kind: 'icon', name: 'CryptoCoinOutline' },
          price: '$1',
          change: { text: '+1%', tone: 'positive' },
          pressActionKey: 'press',
          longPressActionKey: 'hold',
        },
      ]),
      { onRowAction },
      false
    );
    const row = host.querySelector<HTMLElement>('[data-native-list-row-key]')!;
    const pointer = (type: string, pointerType: string, x: number) =>
      row.dispatchEvent(
        Object.assign(
          new document.defaultView!.Event(type, { bubbles: true }),
          {
            pointerId: 1,
            pointerType,
            button: 0,
            clientX: x,
            clientY: 20,
          }
        )
      );
    const touch = (points: Array<{ clientX: number; clientY: number }>) =>
      row.dispatchEvent(
        Object.assign(
          new document.defaultView!.Event('touchmove', { bubbles: true }),
          {
            touches: points,
            changedTouches: points,
          }
        )
      );
    try {
      pointer('pointerdown', 'touch', 10);
      pointer('pointermove', 'touch', 15);
      jest.advanceTimersByTime(800);
      expect(onRowAction.mock.calls.map(([event]) => event.actionKey)).toEqual([
        'hold',
      ]);
      onRowAction.mockClear();
      pointer('pointerdown', 'touch', 10);
      touch([{ clientX: 15, clientY: 20 }]);
      jest.advanceTimersByTime(800);
      expect(onRowAction).not.toHaveBeenCalled();
      pointer('pointerdown', 'touch', 10);
      touch([
        { clientX: 10, clientY: 20 },
        { clientX: 11, clientY: 20 },
      ]);
      jest.advanceTimersByTime(800);
      expect(onRowAction).not.toHaveBeenCalled();
      pointer('pointerdown', 'mouse', 10);
      pointer('pointermove', 'mouse', 15);
      jest.advanceTimersByTime(800);
      expect(onRowAction).not.toHaveBeenCalled();
      pointer('pointerdown', 'touch', 10);
      engine.destroy();
      jest.advanceTimersByTime(800);
      expect(onRowAction).not.toHaveBeenCalled();
    } finally {
      engine.destroy();
      if (originalElement)
        Object.defineProperty(globalThis, 'Element', originalElement);
      else Reflect.deleteProperty(globalThis, 'Element');
      jest.useRealTimers();
    }
  });

  it.each([2, 3, 4, 6, 7] as const)(
    'retains fractional grid widths for %i columns',
    (columns) => {
      const source: RowModel[] = Array.from(
        { length: columns },
        (_, index) => ({
          type: 'message',
          key: `fraction-${index}`,
          title: 'Grid',
          body: '',
          time: '',
          height: 50,
        })
      );
      for (const width of [1000, 1001]) {
        const layout = computeWebListLayout(
          snapshot(
            {
              kind: 'grid',
              gridColumns: columns,
              itemSpacing: 7,
              contentPaddingHorizontal: 13,
            },
            source
          ),
          width,
          400
        );
        const first = layout.items[0]!;
        const last = layout.items[columns - 1]!;
        expect(first.x).toBe(13);
        expect(last.x + last.width).toBeCloseTo(width - 13, 8);
        expect(first.width).toBeCloseTo(
          (width - 26 - 7 * (columns - 1)) / columns,
          8
        );
      }
    }
  );

  it('keeps favorite focus and row/image identity through table quote patches', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const originalElement = Object.getOwnPropertyDescriptor(
      globalThis,
      'Element'
    );
    Object.defineProperty(globalThis, 'Element', {
      configurable: true,
      value: document.defaultView!.Element,
    });
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: Extract<RowModel, { type: 'market' }> = {
      type: 'market',
      key: 'table-quote',
      variant: 'token',
      change: { text: '+1%', tone: 'positive' },
      title: 'Token',
      leading: { kind: 'token', image },
      price: '$42',
      webTable: {
        columns: [
          { key: 'favorite', width: 38 },
          { key: 'asset', width: 280 },
          { key: 'price', flex: 1 },
        ],
        cells: [
          {
            key: 'favorite',
            kind: 'favorite',
            active: false,
            actionKey: 'favorite',
            accessibilityLabel: 'Favorite',
          },
          {
            key: 'asset',
            kind: 'asset',
            leading: { kind: 'token', image },
            title: { text: 'Token' },
          },
          { key: 'price', kind: 'text', primary: [{ text: '$42' }] },
        ],
      },
    };
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row]),
      {},
      false
    );
    try {
      const body = host.querySelector<HTMLElement>(
        '[data-nl-renderer="marketTable"]'
      )!;
      const favorite = body.querySelector<HTMLButtonElement>(
        '[data-native-list-action="favorite"]'
      )!;
      const bitmap = body.querySelector('img');
      expect(bitmap).not.toBeNull();
      expect(bitmap!.getAttribute('src')).toBe(image.uri);
      favorite.focus();
      for (let tick = 0; tick < 100; tick++) {
        const price = `$${43 + tick}`;
        engine.applyPatches([
          {
            type: 'market',
            key: row.key,
            changes: {
              price,
              webTable: {
                ...row.webTable!,
                cells: [
                  {
                    key: 'favorite',
                    kind: 'favorite',
                    active: true,
                    actionKey: 'favorite',
                    accessibilityLabel: 'Remove favorite',
                  },
                  row.webTable!.cells[1]!,
                  { key: 'price', kind: 'text', primary: [{ text: price }] },
                ],
              },
            },
          },
        ]);
      }
      expect(host.querySelector('[data-nl-renderer="marketTable"]')).toBe(body);
      expect(body.querySelector('img')).toBe(bitmap);
      expect(body.querySelector('[data-native-list-action="favorite"]')).toBe(
        favorite
      );
      expect(document.activeElement).toBe(favorite);
      expect(favorite.getAttribute('aria-pressed')).toBe('true');
      expect(body.textContent).toContain('$142');
    } finally {
      engine.destroy();
      if (originalElement)
        Object.defineProperty(globalThis, 'Element', originalElement);
      else Reflect.deleteProperty(globalThis, 'Element');
    }
  });

  it('retains gallery collection padding with and without a network without changing legacy subtitles', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.append(host);
    const gallery: Extract<RowModel, { type: 'mediaTile' }> = {
      type: 'mediaTile',
      key: 'gallery',
      variant: 'gallery',
      imageState: 'empty',
      title: 'NFT',
      subtitle: 'Collection',
    };
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [
        gallery,
        { ...gallery, key: 'network', networkImage: image },
        { ...gallery, key: 'legacy', variant: 'browserPreview' },
      ]),
      {},
      false
    );
    try {
      const subtitles = host.querySelectorAll<HTMLElement>(
        '.ok-native-list-media-subtitle'
      );
      for (const subtitle of Array.from(subtitles).slice(0, 2)) {
        expect(subtitle.style.paddingRight).toBe('8px');
        expect(subtitle.style.boxSizing).toBe('border-box');
        expect(subtitle.parentElement!.style.gap).toBe('0px');
      }
      expect(
        subtitles[1]!.parentElement!.querySelector(
          '.ok-native-list-media-network'
        )
      ).not.toBeNull();
      expect(subtitles[2]!.style.paddingRight).toBe('');
    } finally {
      engine.destroy();
    }
  });

  it('preserves table token frame colors, border and external overlays through loading and reuse', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row = (dark: boolean): Extract<RowModel, { type: 'market' }> => ({
      type: 'market',
      key: 'parity',
      variant: 'token',
      title: 'Token',
      price: '$1',
      leading: { kind: 'token', image },
      change: { text: '+1%', tone: 'positive' },
      webTable: {
        columns: [{ key: 'asset', width: 300 }],
        cells: [
          {
            key: 'asset',
            kind: 'asset',
            title: { text: 'Token' },
            leading: {
              kind: 'token',
              image: { ...image, loadingStrategy: 'static', retryTimes: 0 },
              backgroundColor: dark ? '#2b2b2b' : '#ffffff',
              borderColor: dark ? '#777777' : undefined,
              fallbackIcon: { name: 'CryptoCoinOutline' },
              overlays: [
                {
                  position: 'bottomRight',
                  size: 20,
                  padding: 2,
                  offset: 4,
                  image,
                },
              ],
            },
          },
        ],
      },
    });
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row(true)]),
      {},
      false
    );
    try {
      const visual = host.querySelector<HTMLElement>(
        '[data-column-key="asset"] .ok-native-list-visual'
      )!;
      const bitmap = visual.querySelector<HTMLImageElement>(
        '.ok-native-list-visual-main'
      );
      expect(bitmap).not.toBeNull();
      expect(visual.style.background).toBe('rgb(43, 43, 43)');
      expect(visual.style.borderWidth).toBe('1px');
      expect(visual.style.boxSizing).toBe('border-box');
      expect(bitmap!.style.width).toBe('100%');
      const overlay = visual.querySelector<HTMLElement>(
        '.ok-native-list-visual-overlay'
      )!;
      expect(overlay.style.right).toBe('-5px');
      expect(overlay.style.bottom).toBe('-5px');
      const ErrorEvent = document.defaultView!.Event;
      bitmap!.dispatchEvent(new ErrorEvent('error'));
      expect(
        visual.querySelector('.ok-native-list-visual-fallback')
      ).not.toBeNull();
      expect(visual.style.background).toBe('rgb(43, 43, 43)');
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [row(false)]));
      const light = host.querySelector<HTMLElement>(
        '[data-column-key="asset"] .ok-native-list-visual'
      )!;
      expect(light.style.background).toBe('rgb(255, 255, 255)');
      expect(light.style.borderWidth).toBe('0px');
      expect(light.querySelector('.ok-native-list-visual-fallback')).toBeNull();
      expect(
        light.querySelector<HTMLElement>('.ok-native-list-visual-overlay')!
          .style.right
      ).toBe('-4px');
    } finally {
      engine.destroy();
    }
  });

  it('uses the legacy two pixel address-copy gap while keeping Stock hover logos at four', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const row: Extract<RowModel, { type: 'market' }> = {
      type: 'market',
      key: 'gaps',
      variant: 'token',
      title: 'Token',
      price: '$1',
      leading: { kind: 'token', image },
      change: { text: '+1%', tone: 'positive' },
      webTable: {
        columns: [
          { key: 'copy', width: 300 },
          { key: 'stock', width: 300 },
        ],
        cells: [
          {
            key: 'copy',
            kind: 'asset',
            title: { text: 'Token' },
            secondary: [{ text: '1D' }],
            hoverSecondary: [{ text: '0x123...456' }],
            hoverActionKey: 'copy',
            leading: { kind: 'token', image },
          },
          {
            key: 'stock',
            kind: 'asset',
            title: { text: 'Stock' },
            secondary: [{ text: 'Company' }],
            hoverSecondary: [{ text: '2 tokens' }],
            hoverImages: [image, image],
            leading: { kind: 'token', image },
          },
        ],
      },
    };
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row]),
      {},
      false
    );
    try {
      const copy = host.querySelector<HTMLElement>(
        '[data-native-list-action="copy"]'
      )!;
      expect(copy.parentElement!.style.gap).toBe('2px');
      const stockTrack = host.querySelector<HTMLElement>(
        '[data-column-key="stock"] .ok-native-list-table-secondary-track'
      )!;
      expect((stockTrack.lastElementChild as HTMLElement).style.gap).toBe(
        '4px'
      );
      expect(
        stockTrack.querySelectorAll('.ok-native-list-visual')
      ).toHaveLength(2);
    } finally {
      engine.destroy();
    }
  });
  it('recycles history loading headings and explicit compact Pending content', () => {
    const { document } = new JSDOM('<!doctype html><body></body>').window;
    const host = document.createElement('div');
    document.body.appendChild(host);
    const header = {
      type: 'sectionHeader',
      key: 'pending-title',
      sectionKey: 'pending',
      variant: 'history',
      title: 'Confirming',
      titleLoading: true,
      height: 32,
    } as const;
    const pending = {
      type: 'activity',
      key: 'pending-row',
      presentation: 'stacked',
      leading: { kind: 'icon', name: 'Document2Outline' },
      title: 'Receive',
      amounts: [{ key: 'amount', text: '+1 USDC' }],
      badges: [{ key: 'risk', text: 'Malicious', tone: 'warning' }],
      height: 60,
    } as const;
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [header, pending]),
      {},
      false
    );
    const heading = () =>
      host.querySelector<HTMLElement>('[data-nl-renderer="sectionHeader"]')!;
    const activity = () =>
      host.querySelector<HTMLElement>('[data-nl-renderer="activity"]')!;
    try {
      expect(heading().querySelector('[role="progressbar"]')).not.toBeNull();
      expect(heading().style.height).toBe('calc(100% + 4px)');
      expect(
        heading().querySelector<HTMLElement>('.ok-native-list-section-title')!
          .style.textTransform
      ).toBe('uppercase');
      expect(activity().style.height).toBe('100%');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...header, titleLoading: false, title: 'Today' },
          { ...pending, height: undefined },
        ])
      );
      expect(heading().querySelector('[role="progressbar"]')).toBeNull();
      expect(heading().style.height).not.toBe('calc(100% + 4px)');
      expect(
        heading().querySelector<HTMLElement>('.ok-native-list-section-title')!
          .style.textTransform
      ).toBe('');
      expect(activity().style.height).toBe('calc(100% - 8px)');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          header,
          {
            ...pending,
            height: undefined,
            footerActions: [
              { key: 'speed-up', label: 'Speed up' },
              { key: 'cancel', label: 'Cancel' },
            ],
          },
        ])
      );
      expect(heading().querySelector('[role="progressbar"]')).not.toBeNull();
      expect(
        activity().querySelectorAll('.ok-native-list-action-button')
      ).toHaveLength(2);
      expect(activity().style.height).toBe('100%');
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [header, pending]));
      expect(
        activity().querySelectorAll('.ok-native-list-action-button')
      ).toHaveLength(0);
      expect(activity().style.height).toBe('100%');
      const badge = activity().querySelector<HTMLElement>(
        '.ok-native-list-badge'
      )!;
      expect(badge.style.padding).toBe('2px 8px');
      expect(badge.style.borderRadius).toBe('4px');
      expect(badge.style.fontWeight).toBe('500');
      expect(badge.style.background).toContain('--nl-caution-background');
    } finally {
      engine.destroy();
    }
  });
});

describe.each([375, 1920])('review DOM regressions at %ipx', (width) => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => {
      window: {
        document: Document;
        Element: typeof Element;
        HTMLElement: typeof HTMLElement;
      };
    };
  };
  it('patches hover copy labels without replacing the button or action', () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const host = window.document.createElement('div');
    host.style.width = `${width}px`;
    Object.defineProperty(host, 'clientWidth', { value: width });
    window.document.body.append(host);
    const onRowAction = jest.fn();
    const row = (label?: string): Extract<RowModel, { type: 'market' }> => ({
      type: 'market',
      key: 'copy-label',
      variant: 'token',
      title: 'Token',
      price: '$1',
      leading: { kind: 'token', image },
      change: { text: '+1%', tone: 'positive' },
      webTable: {
        columns: [{ key: 'asset', width: 300 }],
        cells: [
          {
            key: 'asset',
            kind: 'asset',
            leading: { kind: 'token', image },
            title: { text: 'Token' },
            hoverSecondary: [{ text: '0x123' }],
            hoverActionKey: 'copy',
            hoverActionLabel: label,
          },
        ],
      },
    });
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row('Copy token')]),
      { onRowAction },
      false
    );
    const previousElement = global.Element;
    const previousHTMLElement = global.HTMLElement;
    global.Element = window.Element;
    global.HTMLElement = window.HTMLElement;
    try {
      const button = host.querySelector<HTMLButtonElement>('button')!;
      expect(button.getAttribute('aria-label')).toBe('Copy token');
      for (const label of ['Copier', undefined]) {
        engine.applyPatches([
          {
            type: 'market',
            key: 'copy-label',
            changes: { webTable: row(label).webTable },
          },
        ]);
        expect(host.querySelector('button')).toBe(button);
        expect(button.getAttribute('aria-label')).toBe(label ?? 'Copy address');
        engine.applySnapshot(
          snapshot({ kind: 'sectioned' }, [
            row(label === undefined ? 'Snapshot label' : 'Snapshot translated'),
          ])
        );
        expect(host.querySelector('button')).toBe(button);
        expect(button.getAttribute('aria-label')).toBe(
          label === undefined ? 'Snapshot label' : 'Snapshot translated'
        );
        button.click();
      }
      expect(onRowAction.mock.calls.map(([event]) => event.actionKey)).toEqual([
        'copy',
        'copy',
      ]);
    } finally {
      engine.destroy();
      global.Element = previousElement;
      global.HTMLElement = previousHTMLElement;
    }
  });

  it('honors gallery media precedence and explicit states while releasing stale probes', () => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const host = window.document.createElement('div');
    host.style.width = `${width}px`;
    Object.defineProperty(host, 'clientWidth', { value: width });
    window.document.body.append(host);
    const row: Extract<RowModel, { type: 'mediaTile' }> = {
      type: 'mediaTile',
      key: 'precedence',
      variant: 'gallery',
      title: 'NFT',
      image,
      media: {
        source: {
          uri: 'https://example.test/media.png',
          width: 40,
          height: 40,
        },
        probeOrder: ['image'],
      },
    };
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [row]),
      {},
      false
    );
    try {
      const probe = host.querySelector<HTMLImageElement>(
        '.ok-native-list-media-image img'
      )!;
      expect(probe).not.toBeNull();
      expect(probe.getAttribute('src')).toBe(row.media!.source.uri);
      const staleReady = probe.onload!;
      const staleError = probe.onerror!;
      for (const imageState of ['empty', 'error'] as const) {
        engine.applySnapshot(
          snapshot({ kind: 'sectioned' }, [
            { ...row, image: undefined, imageState },
          ])
        );
        expect(host.querySelector('img, video')).toBeNull();
        expect(
          host.querySelector('.ok-native-list-media-fallback')
        ).not.toBeNull();
        expect(probe.isConnected).toBe(false);
        expect(probe.getAttribute('src')).toBeNull();
        staleReady.call(probe, new Event('load'));
        staleError.call(probe, new Event('error'));
        expect(host.querySelector('img, video')).toBeNull();
      }
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [{ ...row, media: undefined }])
      );
      expect(host.querySelector('img')?.getAttribute('src')).toBe(image.uri);
      engine.applySnapshot(snapshot({ kind: 'sectioned' }, [row]));
      expect(
        host
          .querySelector('.ok-native-list-media-image img')
          ?.getAttribute('src')
      ).toBe(row.media!.source.uri);
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          { ...row, image: undefined, media: undefined, imageState: 'empty' },
        ])
      );
      expect(
        host.querySelector('.ok-native-list-media-fallback')
      ).not.toBeNull();
    } finally {
      engine.destroy();
    }
  });
});

describe.each([375, 1920])('ordered image fallback gaps at %ipx', (width) => {
  const { JSDOM } = require('jsdom') as {
    JSDOM: new (html: string) => {
      window: { document: Document; Event: typeof Event };
    };
  };
  const primary = 'https://example.test/primary.png';
  const fallback = 'https://example.test/fallback.png';
  const row = (
    fallbackUris: readonly string[],
    retryTimes = 0
  ): IdentityRow => ({
    type: 'identity',
    key: 'fallback-gaps',
    title: 'Token',
    leading: {
      kind: 'token',
      fallbackText: 'Missing',
      image: {
        ...image,
        uri: primary,
        fallbackUris,
        retryTimes,
        loadingStrategy: 'static',
      },
    },
  });
  const mount = (item: IdentityRow) => {
    const { window } = new JSDOM('<!doctype html><body></body>');
    const host = window.document.createElement('div');
    window.document.body.append(host);
    Object.defineProperty(host, 'clientWidth', { value: width });
    const engine = new NativeListWebEngine(
      host,
      snapshot({ kind: 'sectioned' }, [item]),
      {},
      false
    );
    return {
      window,
      host,
      engine,
      bitmap: host.querySelector<HTMLImageElement>('img')!,
    };
  };
  it('skips empty, whitespace and unsafe entries and preserves duplicate usable continuations', () => {
    const { window, host, engine, bitmap } = mount(
      row(['', '  ', 'data:text/html,ignored', fallback, fallback])
    );
    const terminal = jest.fn();
    bitmap.addEventListener('error', terminal);
    try {
      bitmap.dispatchEvent(new window.Event('error'));
      expect(bitmap.getAttribute('src')).toBe(fallback);
      expect(bitmap.isConnected).toBe(true);
      expect(terminal).not.toHaveBeenCalled();
      bitmap.dispatchEvent(new window.Event('load'));
      expect(bitmap.style.opacity).toBe('1');
      bitmap.dispatchEvent(new window.Event('error'));
      expect(bitmap.getAttribute('src')).toBe(fallback);
      expect(bitmap.isConnected).toBe(true);
      expect(terminal).not.toHaveBeenCalled();
      bitmap.dispatchEvent(new window.Event('error'));
      expect(terminal).toHaveBeenCalledTimes(1);
      expect(host.querySelector('img')).toBeNull();
      expect(host.textContent).toContain('Missing');
    } finally {
      engine.destroy();
    }
  });
  it('exhausts an entirely unusable chain and exposes the existing visual fallback', () => {
    const { window, host, engine, bitmap } = mount(
      row(['', '  ', 'data:text/html,ignored', 'not-a-uri'])
    );
    const terminal = jest.fn();
    bitmap.addEventListener('error', terminal);
    try {
      bitmap.dispatchEvent(new window.Event('error'));
      expect(terminal).toHaveBeenCalledTimes(1);
      expect(host.querySelector('img')).toBeNull();
      expect(host.textContent).toContain('Missing');
    } finally {
      engine.destroy();
    }
  });
  it('keeps each usable source retry budget and cancels old timers on rebind and destroy', () => {
    jest.useFakeTimers();
    const next = 'https://example.test/next.png';
    const replacement = 'https://example.test/replacement.png';
    const { window, host, engine, bitmap } = mount(
      row(['', fallback, ' ', next], 1)
    );
    const error = (element: HTMLImageElement) =>
      element.dispatchEvent(new window.Event('error'));
    try {
      error(bitmap);
      expect(bitmap.getAttribute('src')).toBe(primary);
      jest.advanceTimersByTime(2100);
      error(bitmap);
      expect(bitmap.getAttribute('src')).toBe(fallback);
      error(bitmap);
      expect(bitmap.getAttribute('src')).toBe(fallback);
      jest.advanceTimersByTime(2100);
      error(bitmap);
      expect(bitmap.getAttribute('src')).toBe(next);
      error(bitmap);
      const initial = row([fallback], 1);
      if (initial.leading.kind !== 'token' || !initial.leading.image)
        throw new Error('Expected token image');
      engine.applySnapshot(
        snapshot({ kind: 'sectioned' }, [
          {
            ...initial,
            leading: {
              ...initial.leading,
              image: { ...initial.leading.image, uri: replacement },
            },
          },
        ])
      );
      const current = host.querySelector<HTMLImageElement>('img')!;
      expect(current).not.toBe(bitmap);
      expect(current.getAttribute('src')).toBe(replacement);
      jest.advanceTimersByTime(2100);
      error(bitmap);
      expect(bitmap.getAttribute('src')).toBe(next);
      expect(current.getAttribute('src')).toBe(replacement);
      error(current);
      engine.destroy();
      jest.advanceTimersByTime(2100);
      expect(current.getAttribute('src')).toBe(replacement);
    } finally {
      engine.destroy();
      jest.useRealTimers();
    }
  });
});

describe.each([375, 1920])(
  'retained History visual binding at %ipx',
  (width) => {
    const { JSDOM } = require('jsdom') as {
      JSDOM: new (html: string) => {
        window: { document: Document; Event: typeof Event };
      };
    };
    it.each([
      [false, 'load', 'static'],
      [false, 'load', 'none'],
      [true, 'load', 'none'],
      [false, 'load', undefined],
      [true, 'load', undefined],
      [true, 'load', 'static'],
      [false, 'error', 'static'],
      [true, 'error', 'static'],
    ] as const)(
      'matches fresh styling through presentation changes (pair=%s, state=%s, strategy=%s)',
      (pair, state, loadingStrategy) => {
        const { window } = new JSDOM('<!doctype html><body></body>');
        const mount = (row: ActivityRow) => {
          const host = window.document.createElement('div');
          window.document.body.append(host);
          Object.defineProperty(host, 'clientWidth', { value: width });
          const engine = new NativeListWebEngine(
            host,
            snapshot({ kind: 'sectioned' }, [row]),
            {},
            false
          );
          return { host, engine };
        };
        const base: ActivityRow = {
          type: 'activity',
          key: 'retained-history',
          presentation: 'table',
          title: 'Receive',
          height: 60,
          amounts: [{ key: 'amount', text: '+1 USDC' }],
          leading: {
            kind: 'token',
            image: { ...image, loadingStrategy },
            fallbackText: 'Token',
            fallbackIcon: { name: 'CoinsOutline' },
            networkImage: { ...image, uri: 'https://example.test/network.png' },
          },
          ...(pair
            ? {
                secondaryLeading: {
                  kind: 'token' as const,
                  image: {
                    ...image,
                    uri: 'https://example.test/secondary.png',
                    loadingStrategy,
                  },
                  fallbackText: 'Other',
                },
              }
            : {}),
        };
        const { host, engine } = mount(base);
        const corners = Array.from(
          host.querySelectorAll('.ok-native-list-visual-corner')
        );
        const images = Array.from(
          host.querySelectorAll<HTMLImageElement>('.ok-native-list-visual-main')
        );
        images.forEach((bitmap) =>
          bitmap.dispatchEvent(new window.Event(state))
        );
        const fallbacks = Array.from(
          host.querySelectorAll<HTMLElement>('.ok-native-list-visual-fallback')
        );
        const geometry = (root: HTMLElement) =>
          Array.from(
            root.querySelector<HTMLElement>('.ok-native-list-history-visual')!
              .children
          ).map((visual) => {
            const node = visual as HTMLElement;
            const bitmap = node.querySelector<HTMLElement>(
              '.ok-native-list-visual-main, .ok-native-list-visual-fallback'
            )!;
            return [
              [
                'width',
                'height',
                'border-radius',
                'background',
                'border',
                'position',
                'left',
                'top',
                'overflow',
                'box-sizing',
              ].map((key) => node.style.getPropertyValue(key)),
              bitmap.style.borderRadius,
              bitmap.style.objectFit,
              bitmap.style.width,
              bitmap.style.height,
              bitmap.parentElement === node,
            ];
          });
        try {
          for (const row of [
            {
              ...base,
              style: {
                image: {
                  shape: 'square' as const,
                  contentFit: 'contain' as const,
                },
              },
            },
            {
              ...base,
              presentation: 'stacked' as const,
              style: {
                image: { cornerRadius: 7, contentFit: 'center' as const },
              },
            },
            { ...base, presentation: 'stacked' as const },
            base,
          ]) {
            engine.applySnapshot(snapshot({ kind: 'sectioned' }, [row]));
            const fresh = mount(row);
            fresh.host
              .querySelectorAll('.ok-native-list-visual-main')
              .forEach((bitmap) =>
                bitmap.dispatchEvent(new window.Event(state))
              );
            try {
              if (state === 'load')
                expect(geometry(host)).toEqual(geometry(fresh.host));
              else {
                expect(geometry(host).map(([frame]) => frame)).toEqual(
                  geometry(fresh.host).map(([frame]) => frame)
                );
                fallbacks.forEach((fallback) => {
                  expect(fallback.style.borderRadius).toBe(
                    row.style?.image?.cornerRadius !== undefined
                      ? `${row.style.image.cornerRadius}px`
                      : row.style?.image?.shape === 'square'
                      ? '0px'
                      : ''
                  );
                  expect(fallback.style.objectFit).toBe(
                    row.style?.image?.contentFit === 'center'
                      ? 'none'
                      : row.style?.image?.contentFit ?? ''
                  );
                });
              }
            } finally {
              fresh.engine.destroy();
              fresh.host.remove();
            }
            expect(
              Array.from(host.querySelectorAll('.ok-native-list-visual-main'))
            ).toEqual(state === 'load' ? images : []);
            expect(
              Array.from(
                host.querySelectorAll('.ok-native-list-visual-fallback')
              )
            ).toEqual(fallbacks);
            expect(
              Array.from(host.querySelectorAll('.ok-native-list-visual-corner'))
            ).toEqual(corners);
            images.forEach((bitmap) =>
              expect(bitmap.style.opacity).toBe(state === 'load' ? '1' : '0')
            );
            fallbacks.forEach((fallback) =>
              expect(fallback.isConnected).toBe(true)
            );
          }
        } finally {
          engine.destroy();
        }
      }
    );
  }
);
