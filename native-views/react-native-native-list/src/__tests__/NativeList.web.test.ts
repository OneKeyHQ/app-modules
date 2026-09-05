import type { NativeListSnapshot, RowModel } from '../models';
import {
  computeWebListLayout,
  estimateWebRowHeight,
  visibleWebLayoutItems,
  webRowRenderSignature,
} from '../web/NativeListWebEngine';

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
  it('matches native template heights for specialized examples', () => {
    const linear = snapshot({ kind: 'linear' });
    expect(estimateWebRowHeight(rows[0], linear, 320)).toBe(68);
    expect(estimateWebRowHeight(rows[2], linear, 320)).toBe(72);
    expect(estimateWebRowHeight(rows[4], linear, 320)).toBe(100);
    expect(estimateWebRowHeight(rows[6], linear, 320)).toBe(60);
    expect(estimateWebRowHeight(rows[8], linear, 320)).toBe(132);
    expect(estimateWebRowHeight(rows[9], linear, 320)).toBe(60);
    expect(estimateWebRowHeight(rows[10], linear, 320)).toBe(44);
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
    expect(sectioned.items[1].width).toBe(300);

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
