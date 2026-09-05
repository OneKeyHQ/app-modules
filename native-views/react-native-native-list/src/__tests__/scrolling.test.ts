import type { NativeListRef } from '../NativeList.types';
import type { RowModel } from '../models';
import {
  calculateAlignedScrollOffset,
  normalizeIndexScroll,
  normalizeKeyScroll,
  normalizePositionScroll,
  resolveLocationIndex,
  scrollFailure,
  validateOffset,
} from '../scrolling';

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
    title: 'A',
  },
  {
    type: 'identity',
    key: 'a-0',
    sectionKey: 'a',
    leading: { kind: 'icon', name: 'a' },
    title: 'A0',
  },
  {
    type: 'identity',
    key: 'a-1',
    sectionKey: 'a',
    leading: { kind: 'icon', name: 'a' },
    title: 'A1',
  },
  {
    type: 'sectionHeader',
    key: 'header-b',
    sectionKey: 'b',
    title: 'B',
  },
  {
    type: 'identity',
    key: 'b-0',
    sectionKey: 'b',
    leading: { kind: 'icon', name: 'b' },
    title: 'B0',
  },
];

describe('NativeList scrolling API', () => {
  it('keeps legacy index and key calls nearest-aligned by default', () => {
    expect(normalizeIndexScroll(3)).toEqual({
      index: 3,
      scroll: {
        animated: true,
        alignment: 'nearest',
        viewPosition: 0,
        viewOffset: 0,
      },
    });
    expect(normalizeKeyScroll('a-0', false, 'center')).toEqual({
      key: 'a-0',
      scroll: {
        animated: false,
        alignment: 'center',
        viewPosition: 0.5,
        viewOffset: 0,
      },
    });
  });

  it('normalizes React Native-style position objects', () => {
    expect(
      normalizeIndexScroll({
        index: 4,
        animated: false,
        viewPosition: 0.25,
        viewOffset: 12,
      })
    ).toEqual({
      index: 4,
      scroll: {
        animated: false,
        alignment: 'start',
        viewPosition: 0.25,
        viewOffset: 12,
      },
    });
    expect(
      normalizePositionScroll({ alignment: 'end', viewOffset: 8 }, 'start')
    ).toEqual({
      animated: true,
      alignment: 'end',
      viewPosition: 1,
      viewOffset: 8,
    });
  });

  it('rejects invalid indexes, offsets, and conflicting alignment options', () => {
    expect(() => normalizeIndexScroll(-1)).toThrow('non-negative integer');
    expect(() => validateOffset(Number.NaN)).toThrow('finite number');
    expect(() => validateOffset(-1)).toThrow('non-negative');
    expect(() =>
      normalizePositionScroll(
        { alignment: 'center', viewPosition: 0.5 },
        'start'
      )
    ).toThrow('cannot be used together');
    expect(() =>
      normalizePositionScroll({ viewPosition: 1.1 }, 'start')
    ).toThrow('between 0 and 1');
  });

  it('resolves SectionList-style locations without counting summary headers', () => {
    expect(resolveLocationIndex(rows, { sectionIndex: 0, itemIndex: 0 })).toBe(
      2
    );
    expect(resolveLocationIndex(rows, { sectionIndex: 0, itemIndex: 1 })).toBe(
      3
    );
    expect(resolveLocationIndex(rows, { sectionIndex: 1, itemIndex: 0 })).toBe(
      5
    );
    expect(
      resolveLocationIndex(rows, { sectionIndex: 2, itemIndex: 0 })
    ).toBeUndefined();
    expect(
      resolveLocationIndex(rows, { sectionIndex: 1, itemIndex: 1 })
    ).toBeUndefined();
  });

  it('calculates start, center, end, offset, nearest, and clamped positions', () => {
    const base = {
      itemOffset: 400,
      itemLength: 100,
      viewportLength: 300,
      contentLength: 1_000,
      currentOffset: 0,
      viewOffset: 0,
    } as const;
    expect(
      calculateAlignedScrollOffset({
        ...base,
        alignment: 'start',
        viewPosition: 0,
      })
    ).toBe(400);
    expect(
      calculateAlignedScrollOffset({
        ...base,
        alignment: 'center',
        viewPosition: 0.5,
      })
    ).toBe(300);
    expect(
      calculateAlignedScrollOffset({
        ...base,
        alignment: 'end',
        viewPosition: 1,
      })
    ).toBe(200);
    expect(
      calculateAlignedScrollOffset({
        ...base,
        alignment: 'start',
        viewPosition: 0,
        viewOffset: 24,
      })
    ).toBe(376);
    expect(
      calculateAlignedScrollOffset({
        ...base,
        itemOffset: 100,
        currentOffset: 50,
        alignment: 'nearest',
        viewPosition: 0,
      })
    ).toBe(50);
    expect(
      calculateAlignedScrollOffset({
        ...base,
        itemOffset: 950,
        alignment: 'start',
        viewPosition: 0,
      })
    ).toBe(700);
  });

  it('reports RN-compatible failure measurements with a concrete reason', () => {
    expect(scrollFailure(rows, 8, 'index-out-of-range', 56)).toEqual({
      index: 8,
      itemCount: 6,
      highestMeasuredFrameIndex: 5,
      averageItemLength: 56,
      reason: 'index-out-of-range',
    });
  });

  it('exposes every imperative API with legacy calls preserved', () => {
    const exercise = (ref: NativeListRef) => {
      ref.scrollToIndex(4, false, 'center');
      ref.scrollToIndex({ index: 4, viewPosition: 0.5, viewOffset: 8 });
      ref.scrollToItem({ item: rows[2] });
      ref.scrollToKey('a-0', false, 'start');
      ref.scrollToKey({ key: 'a-0', alignment: 'nearest' });
      ref.scrollToOffset({ offset: 120, animated: false });
      ref.scrollToEnd({ animated: true });
      ref.scrollToLocation({ sectionIndex: 1, itemIndex: 0 });
    };
    expect(exercise).toEqual(expect.any(Function));
  });
});
