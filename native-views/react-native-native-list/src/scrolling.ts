import type {
  ScrollAlignment,
  ScrollPositionOptions,
  ScrollToIndexFailedInfo,
  ScrollToIndexParams,
  ScrollToKeyParams,
  ScrollToLocationParams,
} from './NativeList.types';
import type { RowModel } from './models';

export type NormalizedPositionScroll = Readonly<{
  animated: boolean;
  alignment: ScrollAlignment;
  viewPosition: number;
  viewOffset: number;
}>;

export type AlignedOffsetInput = Readonly<{
  itemOffset: number;
  itemLength: number;
  viewportLength: number;
  contentLength: number;
  currentOffset: number;
  alignment: ScrollAlignment;
  viewPosition: number;
  viewOffset: number;
}>;

function assertNonNegativeInteger(value: number, name: string): void {
  if (!Number.isInteger(value) || value < 0) {
    throw new Error(`NativeList ${name} must be a non-negative integer`);
  }
}

function assertFiniteNumber(value: number, name: string): void {
  if (!Number.isFinite(value)) {
    throw new Error(`NativeList ${name} must be a finite number`);
  }
}

function positionForAlignment(alignment: ScrollAlignment): number {
  switch (alignment) {
    case 'center':
      return 0.5;
    case 'end':
      return 1;
    default:
      return 0;
  }
}

export function normalizePositionScroll(
  options: ScrollPositionOptions,
  defaultAlignment: ScrollAlignment
): NormalizedPositionScroll {
  const alignment = options.alignment ?? defaultAlignment;
  if (options.alignment !== undefined && options.viewPosition !== undefined) {
    throw new Error(
      'NativeList alignment and viewPosition cannot be used together'
    );
  }
  const viewPosition = options.viewPosition ?? positionForAlignment(alignment);
  if (!Number.isFinite(viewPosition) || viewPosition < 0 || viewPosition > 1) {
    throw new Error('NativeList viewPosition must be between 0 and 1');
  }
  const viewOffset = options.viewOffset ?? 0;
  assertFiniteNumber(viewOffset, 'viewOffset');
  return {
    animated: options.animated ?? true,
    alignment:
      options.viewPosition === undefined ? alignment : ('start' as const),
    viewPosition,
    viewOffset,
  };
}

export function normalizeIndexScroll(
  paramsOrIndex: ScrollToIndexParams | number,
  animated?: boolean,
  alignment?: ScrollAlignment
): Readonly<{ index: number; scroll: NormalizedPositionScroll }> {
  if (typeof paramsOrIndex === 'number') {
    assertNonNegativeInteger(paramsOrIndex, 'scrollToIndex index');
    return {
      index: paramsOrIndex,
      scroll: normalizePositionScroll({ animated, alignment }, 'nearest'),
    };
  }
  assertNonNegativeInteger(paramsOrIndex.index, 'scrollToIndex index');
  return {
    index: paramsOrIndex.index,
    scroll: normalizePositionScroll(paramsOrIndex, 'start'),
  };
}

export function normalizeKeyScroll(
  paramsOrKey: ScrollToKeyParams | string,
  animated?: boolean,
  alignment?: ScrollAlignment
): Readonly<{ key: string; scroll: NormalizedPositionScroll }> {
  if (typeof paramsOrKey === 'string') {
    return {
      key: paramsOrKey,
      scroll: normalizePositionScroll({ animated, alignment }, 'nearest'),
    };
  }
  return {
    key: paramsOrKey.key,
    scroll: normalizePositionScroll(paramsOrKey, 'start'),
  };
}

export function validateOffset(offset: number): void {
  assertFiniteNumber(offset, 'scroll offset');
  if (offset < 0) {
    throw new Error('NativeList scroll offset must be non-negative');
  }
}

export function resolveLocationIndex(
  rows: readonly RowModel[],
  params: ScrollToLocationParams
): number | undefined {
  assertNonNegativeInteger(
    params.sectionIndex,
    'scrollToLocation sectionIndex'
  );
  assertNonNegativeInteger(params.itemIndex, 'scrollToLocation itemIndex');
  const headers = rows
    .map((row, index) => ({ row, index }))
    .filter(
      (entry) =>
        entry.row.type === 'sectionHeader' && entry.row.variant !== 'summary'
    );
  const section = headers[params.sectionIndex];
  if (!section || section.row.type !== 'sectionHeader') return undefined;
  const end = headers[params.sectionIndex + 1]?.index ?? rows.length;
  const items = rows
    .slice(section.index + 1, end)
    .map((row, offset) => ({ row, index: section.index + 1 + offset }))
    .filter(
      (entry) =>
        entry.row.type !== 'sectionHeader' &&
        entry.row.sectionKey === section.row.sectionKey
    );
  return items[params.itemIndex]?.index;
}

export function scrollFailure(
  rows: readonly RowModel[],
  index: number,
  reason: ScrollToIndexFailedInfo['reason'],
  averageItemLength = 0
): ScrollToIndexFailedInfo {
  return {
    index,
    itemCount: rows.length,
    highestMeasuredFrameIndex: rows.length - 1,
    averageItemLength,
    reason,
  };
}

export function calculateAlignedScrollOffset({
  itemOffset,
  itemLength,
  viewportLength,
  contentLength,
  currentOffset,
  alignment,
  viewPosition,
  viewOffset,
}: AlignedOffsetInput): number {
  let resolvedPosition = viewPosition;
  if (alignment === 'nearest') {
    if (itemOffset < currentOffset) resolvedPosition = 0;
    else if (itemOffset + itemLength > currentOffset + viewportLength)
      resolvedPosition = 1;
    else return currentOffset;
  }
  const target =
    itemOffset -
    resolvedPosition * Math.max(0, viewportLength - itemLength) -
    viewOffset;
  return Math.min(
    Math.max(0, target),
    Math.max(0, contentLength - viewportLength)
  );
}
