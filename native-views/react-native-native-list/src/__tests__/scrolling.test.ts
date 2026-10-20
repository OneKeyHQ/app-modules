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

  it.each([
    ['short/start', 100, 0, 0, 400],
    ['short/center', 100, 0.5, 0, 300],
    ['short/end', 100, 1, 0, 200],
    ['short/start/positive offset', 100, 0, 24, 376],
    ['short/center/positive offset', 100, 0.5, 24, 276],
    ['short/end/positive offset', 100, 1, 24, 176],
    ['short/start/negative offset', 100, 0, -24, 424],
    ['short/center/negative offset', 100, 0.5, -24, 324],
    ['short/end/negative offset', 100, 1, -24, 224],
    ['oversized/start', 600, 0, 0, 400],
    ['oversized/center', 600, 0.5, 0, 550],
    ['oversized/end', 600, 1, 0, 700],
    ['oversized/start/positive offset', 600, 0, 24, 376],
    ['oversized/center/positive offset', 600, 0.5, 24, 526],
    ['oversized/end/positive offset', 600, 1, 24, 676],
    ['oversized/start/negative offset', 600, 0, -24, 424],
    ['oversized/center/negative offset', 600, 0.5, -24, 574],
    ['oversized/end/negative offset', 600, 1, -24, 724],
  ] as const)(
    'aligns %s in either scrolling axis',
    (_name, itemLength, viewPosition, viewOffset, expected) => {
      expect(
        calculateAlignedScrollOffset({
          itemOffset: 400,
          itemLength,
          viewportLength: 300,
          contentLength: 2_000,
          currentOffset: 0,
          alignment: 'start',
          viewPosition,
          viewOffset,
        })
      ).toBe(expected);
    }
  );

  it.each([
    ['first short row', 0, 100, 2_000, 0.5, 0, 0],
    ['first oversized row', 0, 600, 2_000, 0.5, 0, 150],
    ['last short row', 1_900, 100, 2_000, 0, 0, 1_700],
    ['last oversized row/end', 1_400, 600, 2_000, 1, 0, 1_700],
    ['positive offset at start', 0, 600, 2_000, 0.5, 200, 0],
    ['negative offset at end', 1_400, 600, 2_000, 1, -24, 1_700],
    ['short content/start', 0, 100, 100, 0, 0, 0],
    ['short content/center', 0, 100, 100, 0.5, 0, 0],
    ['short content/end', 0, 100, 100, 1, -24, 0],
    ['group member at content end', 2_008, 870, 2_886, 0.5, -320, 2_486],
  ] as const)(
    'clamps %s to the content bounds',
    (
      _name,
      itemOffset,
      itemLength,
      contentLength,
      viewPosition,
      viewOffset,
      expected
    ) => {
      expect(
        calculateAlignedScrollOffset({
          itemOffset,
          itemLength,
          viewportLength: _name === 'group member at content end' ? 400 : 300,
          contentLength,
          currentOffset: 0,
          alignment: 'start',
          viewPosition,
          viewOffset,
        })
      ).toBe(expected);
    }
  );

  it.each([0, 400, 450, 1_100])(
    'preserves legacy nearest behavior for oversized rows at offset %s',
    (currentOffset) => {
      expect(
        calculateAlignedScrollOffset({
          itemOffset: 400,
          itemLength: 600,
          viewportLength: 300,
          contentLength: 2_000,
          currentOffset,
          alignment: 'nearest',
          viewPosition: 0,
          viewOffset: 0,
        })
      ).toBe(400);
    }
  );

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
