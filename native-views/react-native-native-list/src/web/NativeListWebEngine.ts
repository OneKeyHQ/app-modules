import { applyTextWeight } from './templates/RowText';
import {
  applyRowContainerStyle,
  resetRowContainerStyle,
} from './templates/RowContainerStyle';
import { hasExplicitRowHeight, rowSizeModifier } from './templates/RowElements';
import { applyTextStyleToSlot, applyValueSegments } from './templates/RowText';
import { setMarketQuoteContent } from './templates/MarketRowRenderer';
import {
  canPatchMarketTableValues,
  updateMarketTableValues,
} from './templates/MarketTableRowRenderer';
export {
  resolveWebMarketLayoutStyle,
  type WebMarketLayoutStyle,
} from './templates/MarketRowRenderer';
import { measureRailWidth } from './templates/RailRowRenderer';
import {
  rowRenderer,
  rowRendererKey,
  recycleRowBody,
  type RowRendererKey,
} from './templates/RowRendererRegistry';
import type {
  ActionAnchorInvalidatedEvent,
  CheckboxState,
  ImageSource,
  LeadingVisual,
  NativeListSnapshot,
  NativeListActionAnchor,
  NativeListActionSource,
  NativeListTheme,
  ReorderEvent,
  RowActionEvent,
  RowModel,
  RowPatch,
  SelectionDeltaEvent,
  SelectionTarget,
  TextTone,
  TrailingAccessory,
  VisibleRangeChangedEvent,
} from '../models';
import type {
  ActionAnchorState,
  ScrollToIndexFailedInfo,
  ScrollToLocationParams,
} from '../NativeList.types';
import {
  checkboxStateForKeys,
  checkboxStateForSection,
  isRowPressEnabled,
  isSelectableRow,
  reduceSelection,
  selectionStateFromSnapshot,
} from '../selection';
import {
  calculateAlignedScrollOffset,
  resolveLocationIndex,
  scrollFailure,
  validateOffset,
  type NormalizedPositionScroll,
} from '../scrolling';
import {
  applyValidatedRowPatches,
  containNativeListInput,
  emptyNativeListSnapshot,
  validateSnapshot,
} from '../validation';
import { avatarPrefetchWindow } from '../avatarPrefetch';
import {
  acquireNativeListAvatar,
  canonicalNativeListAvatarUri,
  type AvatarLease,
} from './NativeListWebAvatarCache';

const SECTION_INDEX_CONTENT_INSET = 16;
const SECTION_INDEX_RAIL_WIDTH = 32;
const SECTION_INDEX_EDGE_PADDING = 8;
const SECTION_INDEX_MIN_LABEL_SPACING = 16;
const SECTION_INDEX_CONTAINER_LABEL_SPACING = 22;
const SECTION_INDEX_MIN_HEIGHT = 120;
const SECTION_INDEX_WINDOW_Z_INDEX = 100_000;
const SECTION_INDEX_TRANSFORM_MIN_TRACKING_FRAMES = 12;
const SECTION_INDEX_TRANSFORM_MAX_TRACKING_FRAMES = 90;
const SECTION_INDEX_TRANSFORM_SETTLED_FRAMES = 4;
const SECTION_INDEX_TRANSFORM_SETTLED_EPSILON = 0.0001;
const DEFAULT_VIEWPORT_WIDTH = 320;
const DEFAULT_VIEWPORT_HEIGHT = 640;
const OVERSCAN_VIEWPORTS = 1;
export const WEB_REORDER_ANIMATION = {
  outOfWayDurationMs: 200,
  outOfWayTimingFunction: 'cubic-bezier(0.2, 0, 0, 1)',
  dropDurationMs: 80,
  dropTimingFunction: 'ease',
} as const;

const REORDER_TOUCH_LONG_PRESS_MS = 120;
const MARKET_LONG_PRESS_MS = 800;
const MARKET_MOVE_CANCEL_PX = 10;
export const WEB_MARKET_VERIFIED_PATH =
  'M9.483 11.458v3.5h-1v-3.5z M10.467 2.698a2.03 2.03 0 0 1 3.065 0l1.358 1.564a.03.03 0 0 0 .028.01l2.046-.325a2.03 2.03 0 0 1 2.347 1.971l.037 2.07q0 .016.014.026l1.776 1.066a2.03 2.03 0 0 1 .532 3.019l-1.304 1.609a.03.03 0 0 0-.005.03l.675 1.956a2.03 2.03 0 0 1-1.533 2.656l-2.033.394a.03.03 0 0 0-.023.019l-.741 1.933a2.03 2.03 0 0 1-2.88 1.05l-1.811-1.006a.03.03 0 0 0-.03 0l-1.811 1.005a2.03 2.03 0 0 1-2.88-1.049l-.742-1.933a.03.03 0 0 0-.023-.019l-2.033-.394a2.03 2.03 0 0 1-1.532-2.656l.675-1.957a.03.03 0 0 0-.005-.029l-1.304-1.61a2.03 2.03 0 0 1 .532-3.018l1.776-1.066a.03.03 0 0 0 .014-.026l.035-2.07a2.03 2.03 0 0 1 2.349-1.97l2.045.324a.03.03 0 0 0 .028-.01zm1.516 3.76a.5.5 0 0 0-.447.276l-1.861 3.724H8.483a1 1 0 0 0-1 1v3.5a1 1 0 0 0 1 1h6.692a2 2 0 0 0 1.981-1.73l.341-2.5a2 2 0 0 0-1.982-2.27h-1.939l.197-1.269a1.5 1.5 0 0 0-1.481-1.731z';
const REORDER_MOUSE_MOVE_THRESHOLD_PX = 5;
const REORDER_EDGE_RATIO = 0.25;
const REORDER_MAX_SPEED_ZONE_RATIO = 0.05;
const REORDER_MAX_SCROLL_PX = 28;
const REORDER_SCROLL_ACCELERATE_AT_MS = 360;
const REORDER_SCROLL_DAMPENING_MS = 1_200;
const REORDER_DROP_TRANSITION_MS = WEB_REORDER_ANIMATION.dropDurationMs;
const WALLET_REORDER_COMPACT_HEIGHT = 68;
let webNativeListInstanceCounter = 0;

const defaultTheme: NativeListTheme = {
  background: '#F7F7F7',
  rowBackground: '#FFFFFF',
  rowSelectedBackground: '#EAF2FF',
  rowPressedBackground: '#E8E8E8',
  subduedBackground: '#F9F9F9',
  strongBackground: '#0000000F',
  primaryText: '#111111',
  secondaryText: '#6B7280',
  disabledText: '#8D8D8D',
  icon: '#111111',
  iconSubdued: '#8D8D8D',
  separator: '#E5E7EB',
  accent: '#2F6BFF',
  positive: '#15803D',
  negative: '#DC2626',
  criticalBackground: '#FEECEC',
  inverseBackground: '#202020',
  inverseText: '#FCFCFC',
  info: '#0D74CE',
};

export type WebLayoutItem = Readonly<{
  index: number;
  key: string;
  x: number;
  y: number;
  width: number;
  height: number;
}>;

export type WebListLayout = Readonly<{
  items: readonly WebLayoutItem[];
  contentWidth: number;
  contentHeight: number;
  horizontal: boolean;
}>;

export function webSectionIndexMetrics(
  entryCount: number,
  viewportHeight: number,
  alignStart = false
) {
  const availableHeight = Math.max(
    0,
    viewportHeight - SECTION_INDEX_EDGE_PADDING * 2
  );
  const labelSpacing = alignStart
    ? SECTION_INDEX_CONTAINER_LABEL_SPACING
    : SECTION_INDEX_MIN_LABEL_SPACING;
  const trackHeight = Math.min(availableHeight, labelSpacing * entryCount);
  return {
    originY: alignStart
      ? SECTION_INDEX_EDGE_PADDING
      : (viewportHeight - trackHeight) / 2,
    trackHeight,
  };
}

export function webSectionIndexContainerLayout(
  entryCount: number,
  containerHeight: number
) {
  const height = Math.min(
    Math.max(0, containerHeight),
    SECTION_INDEX_EDGE_PADDING * 2 +
      SECTION_INDEX_CONTAINER_LABEL_SPACING * entryCount
  );
  return {
    top: Math.max(0, (containerHeight - height) / 2),
    height,
  };
}

export function webSectionIndexInverseScale(
  layoutHeight: number,
  renderedHeight: number
) {
  if (layoutHeight <= 0 || renderedHeight <= 0) return 1;
  return layoutHeight / renderedHeight;
}

export function webSectionIndexActiveKey(
  rows: readonly RowModel[],
  layout: WebListLayout,
  offset: number,
  viewportLength: number
): string | undefined {
  const indexedRows = rows.flatMap((row, index) =>
    row.type === 'sectionHeader' && row.sticky !== false && row.indexTitle
      ? [{ key: row.key, index }]
      : []
  );
  const maxOffset = Math.max(0, layout.contentHeight - viewportLength);
  if (maxOffset > 0 && offset >= maxOffset - 1) {
    return indexedRows.at(-1)?.key;
  }
  let activeKey: string | undefined;
  indexedRows.forEach(({ key, index }) => {
    if (itemStart(layout.items[index], layout.horizontal) <= offset) {
      activeKey = key;
    }
  });
  return activeKey;
}

/** Resolves every reusable Market geometry field, including legacy defaults. */

export type NativeListWebCallbacks = Readonly<{
  onRowAction?: (event: RowActionEvent) => void;
  onActionAnchorInvalidated?: (event: ActionAnchorInvalidatedEvent) => void;
  onSelectionDelta?: (event: SelectionDeltaEvent) => void;
  onReorder?: (event: ReorderEvent) => void;
  onEndReached?: (event: { generation: number; lastKey?: string }) => void;
  onVisibleRangeChanged?: (event: VisibleRangeChangedEvent) => void;
  onRefresh?: () => void;
  onScrollToIndexFailed?: (info: ScrollToIndexFailedInfo) => void;
}>;

type WebActionAnchorRecord = {
  token: string;
  actionElement: HTMLElement;
  rowElement: HTMLElement;
  bindingEpoch: string;
  open: boolean;
  invalidatedReason?: ActionAnchorInvalidatedEvent['reason'];
};

export function webActionAnchorPayload(
  token: string,
  rect: Readonly<{
    left: number;
    top: number;
    width: number;
    height: number;
  }>,
  source: NativeListActionSource,
  generation: number,
  layoutDirection: 'ltr' | 'rtl',
  slot?: number
): NativeListActionAnchor {
  return {
    token,
    windowRect: {
      x: rect.left,
      y: rect.top,
      width: rect.width,
      height: rect.height,
    },
    source,
    slot,
    generation,
    layoutDirection,
  };
}

type RenderContext = Readonly<{
  document: Document;
  snapshot: NativeListSnapshot;
  selectedKeys: ReadonlySet<string>;
  itemIndex: number;
}>;

type PendingScroll =
  | Readonly<{
      kind: 'index';
      index: number;
      scroll: NormalizedPositionScroll;
    }>
  | Readonly<{ kind: 'offset'; offset: number; animated: boolean }>
  | Readonly<{ kind: 'end'; animated: boolean }>;

type PointerReorderState = {
  pointerId: number;
  pointerType: string;
  sourceKey: string;
  currentIndex: number;
  startX: number;
  startY: number;
  clientX: number;
  clientY: number;
  originalRows: readonly RowModel[];
  workingRows: RowModel[];
  viewportRect?: Readonly<{
    left: number;
    top: number;
    right: number;
    bottom: number;
    width: number;
    height: number;
  }>;
  previewOffsetX?: number;
  previewOffsetY?: number;
  longPressTimer?: number;
  activatedAt?: number;
  active: boolean;
};

type KeyboardReorderState = {
  sourceKey: string;
  originalRows: readonly RowModel[];
};

export function isWebRowReorderable(
  snapshot: NativeListSnapshot,
  row: RowModel
): boolean {
  if (!snapshot.capabilities?.reorderable || row.disabled) return false;
  if (row.type === 'rail' && row.draggable) return true;
  if (row.type === 'identity' && row.draggable) return true;
  if (row.type === 'walletGroup' && row.draggable) return true;
  if (row.type === 'market' && row.webTable?.draggable) return true;
  return (
    (row.type === 'identity' || row.type === 'action') &&
    Boolean(row.trailing?.some((accessory) => accessory.kind === 'drag'))
  );
}

export function moveWebReorderRow(
  rows: readonly RowModel[],
  fromIndex: number,
  toIndex: number
): readonly RowModel[] {
  if (
    fromIndex === toIndex ||
    fromIndex < 0 ||
    fromIndex >= rows.length ||
    toIndex < 0 ||
    toIndex >= rows.length
  ) {
    return rows;
  }
  const next = [...rows];
  const [moved] = next.splice(fromIndex, 1);
  if (!moved) return rows;
  next.splice(toIndex, 0, moved);
  return next;
}

export function cancelWebReorderRows(
  originalRows: readonly RowModel[]
): readonly RowModel[] {
  return originalRows;
}

export function webReorderEventForRows(
  originalRows: readonly RowModel[],
  finalRows: readonly RowModel[],
  key: string
): ReorderEvent | undefined {
  const fromIndex = originalRows.findIndex((row) => row.key === key);
  const toIndex = finalRows.findIndex((row) => row.key === key);
  if (fromIndex < 0 || toIndex < 0 || fromIndex === toIndex) return undefined;
  return {
    key,
    fromIndex,
    toIndex,
    beforeKey: finalRows[toIndex - 1]?.key,
    afterKey: finalRows[toIndex + 1]?.key,
  };
}

export function webReorderAutoScrollVelocity(
  point: number,
  start: number,
  end: number,
  elapsedMs: number
): number {
  const viewportLength = Math.max(1, end - start);
  const edge = viewportLength * REORDER_EDGE_RATIO;
  const maxZone = viewportLength * REORDER_MAX_SPEED_ZONE_RATIO;
  const speedForDistance = (distance: number) => {
    if (distance > edge) return 0;
    const proposed =
      distance <= maxZone
        ? REORDER_MAX_SCROLL_PX
        : distance === edge
        ? 1
        : Math.ceil(
            REORDER_MAX_SCROLL_PX *
              Math.pow((edge - distance) / Math.max(1, edge - maxZone), 2)
          );
    if (elapsedMs < REORDER_SCROLL_ACCELERATE_AT_MS) return 1;
    if (elapsedMs >= REORDER_SCROLL_DAMPENING_MS) return proposed;
    const timeProgress =
      (elapsedMs - REORDER_SCROLL_ACCELERATE_AT_MS) /
      (REORDER_SCROLL_DAMPENING_MS - REORDER_SCROLL_ACCELERATE_AT_MS);
    return Math.ceil(proposed * timeProgress * timeProgress);
  };
  if (point <= start + edge) return -speedForDistance(point - start);
  if (point >= end - edge) return speedForDistance(end - point);
  return 0;
}

export function hasExceededWebReorderMouseThreshold(
  deltaX: number,
  deltaY: number
): boolean {
  return (
    Math.abs(deltaX) >= REORDER_MOUSE_MOVE_THRESHOLD_PX ||
    Math.abs(deltaY) >= REORDER_MOUSE_MOVE_THRESHOLD_PX
  );
}

function effectiveRows(snapshot: NativeListSnapshot): readonly RowModel[] {
  if (snapshot.rows.length > 0) return snapshot.rows;
  return snapshot.emptyState ? [snapshot.emptyState] : [];
}

function resolvedTheme(snapshot: NativeListSnapshot): NativeListTheme {
  return { ...defaultTheme, ...snapshot.theme };
}

function paddingValues(snapshot: NativeListSnapshot) {
  const fallback = snapshot.layout.contentPadding ?? 0;
  return {
    horizontal: snapshot.layout.contentPaddingHorizontal ?? fallback,
    top: snapshot.layout.contentPaddingTop ?? fallback,
    bottom: snapshot.layout.contentPaddingBottom ?? fallback,
  };
}

function isStructuralRow(row: RowModel): boolean {
  return (
    row.type === 'sectionHeader' ||
    row.type === 'system' ||
    row.type === 'action'
  );
}

function sizeModifier(row: RowModel): number {
  if (
    row.type === 'sectionHeader' &&
    (row.variant === 'summary' || row.variant === 'gallery')
  )
    return 0;
  return rowSizeModifier(row.size);
}

export function estimateWebRowHeight(
  row: RowModel,
  snapshot: NativeListSnapshot,
  availableWidth: number
): number {
  // Explicit container/model height takes precedence over template presets.
  const explicitHeight = row.style?.container?.height ?? row.height;
  if (explicitHeight !== undefined) return explicitHeight;
  const registered = rowRenderer(row);
  return Math.max(
    0,
    registered.measure(availableWidth, snapshot.layout.kind) +
      (registered.appliesSizePreset ? sizeModifier(row) : 0)
  );
}

function estimateHorizontalWidth(row: RowModel): number {
  if (row.type === 'mediaTile') return 200;
  if (row.type !== 'rail') return 280;
  return measureRailWidth(row);
}

function sectionIndexEnabled(snapshot: NativeListSnapshot): boolean {
  return Boolean(
    snapshot.capabilities?.sectionIndex?.enabled &&
      snapshot.layout.kind === 'sectioned' &&
      snapshot.layout.orientation !== 'horizontal' &&
      snapshot.rows.some(
        (row) => row.type === 'sectionHeader' && row.indexTitle
      )
  );
}

export function computeWebListLayout(
  snapshot: NativeListSnapshot,
  viewportWidth: number,
  viewportHeight: number,
  compactRowKey?: string
): WebListLayout {
  const rows = effectiveRows(snapshot);
  const horizontal = snapshot.layout.orientation === 'horizontal';
  const spacing = snapshot.layout.itemSpacing ?? 0;
  const padding = paddingValues(snapshot);
  const width = Math.max(1, viewportWidth || DEFAULT_VIEWPORT_WIDTH);
  const height = Math.max(1, viewportHeight || DEFAULT_VIEWPORT_HEIGHT);
  // OneKey patch: the index overlays the content and only keeps a small accessory-safe inset.
  const indexGutter =
    sectionIndexEnabled(snapshot) && height >= SECTION_INDEX_MIN_HEIGHT
      ? SECTION_INDEX_CONTENT_INSET
      : 0;
  const availableWidth = Math.max(
    1,
    width - padding.horizontal * 2 - indexGutter
  );
  const availableHeight = Math.max(1, height - padding.top - padding.bottom);
  const items: WebLayoutItem[] = [];

  if (horizontal) {
    let x = padding.horizontal;
    rows.forEach((row, index) => {
      const itemWidth = estimateHorizontalWidth(row);
      const rowHeight =
        row.type === 'walletGroup' && row.key === compactRowKey
          ? WALLET_REORDER_COMPACT_HEIGHT
          : estimateWebRowHeight(row, snapshot, availableWidth);
      items.push({
        index,
        key: row.key,
        x,
        y: padding.top,
        width: itemWidth,
        height:
          (row.type === 'walletGroup' && row.key === compactRowKey
            ? WALLET_REORDER_COMPACT_HEIGHT
            : undefined) ??
          row.style?.container?.height ??
          row.height ??
          (row.type === 'rail'
            ? Math.min(rowHeight, availableHeight)
            : availableHeight),
      });
      x += itemWidth + spacing;
    });
    const contentWidth =
      rows.length > 0
        ? x - spacing + padding.horizontal
        : padding.horizontal * 2;
    return {
      items,
      horizontal: true,
      contentWidth: Math.max(width, contentWidth),
      contentHeight: height,
    };
  }

  if (snapshot.layout.kind !== 'grid') {
    let y = padding.top;
    rows.forEach((row, index) => {
      const itemHeight =
        row.type === 'walletGroup' && row.key === compactRowKey
          ? WALLET_REORDER_COMPACT_HEIGHT
          : estimateWebRowHeight(row, snapshot, availableWidth);
      items.push({
        index,
        key: row.key,
        x: padding.horizontal,
        y,
        width: availableWidth,
        height: itemHeight,
      });
      y += itemHeight + spacing;
    });
    const contentHeight =
      rows.length > 0
        ? y - spacing + padding.bottom
        : padding.top + padding.bottom;
    return {
      items,
      horizontal: false,
      contentWidth: width,
      contentHeight: Math.max(height, contentHeight),
    };
  }

  const columns = snapshot.layout.gridColumns ?? 2;
  const itemWidth = (availableWidth - spacing * (columns - 1)) / columns;
  let y = padding.top;
  let column = 0;
  let bandStart = y;
  let bandHeight = 0;
  const flushBand = () => {
    if (column === 0) return;
    y = bandStart + bandHeight + spacing;
    column = 0;
    bandHeight = 0;
    bandStart = y;
  };

  rows.forEach((row, index) => {
    if (isStructuralRow(row)) {
      flushBand();
      const itemHeight = estimateWebRowHeight(row, snapshot, availableWidth);
      items.push({
        index,
        key: row.key,
        x: padding.horizontal,
        y,
        width: availableWidth,
        height: itemHeight,
      });
      y += itemHeight + spacing;
      bandStart = y;
      return;
    }
    const itemHeight =
      row.style?.container?.height ??
      row.height ??
      (row.type === 'mediaTile'
        ? itemWidth + 48
        : estimateWebRowHeight(row, snapshot, itemWidth));
    items.push({
      index,
      key: row.key,
      x: padding.horizontal + column * (itemWidth + spacing),
      y: bandStart,
      width: itemWidth,
      height: itemHeight,
    });
    bandHeight = Math.max(bandHeight, itemHeight);
    column += 1;
    if (column === columns) flushBand();
  });
  flushBand();
  const contentHeight =
    rows.length > 0
      ? Math.max(padding.top, y - spacing) + padding.bottom
      : padding.top + padding.bottom;
  return {
    items,
    horizontal: false,
    contentWidth: width,
    contentHeight: Math.max(height, contentHeight),
  };
}

export function webWalletGroupReorderBadge(row: RowModel): string | undefined {
  const draggableChildCount =
    row.type === 'walletGroup'
      ? row.children.filter((child) => child.draggable !== false).length
      : 0;
  return draggableChildCount > 0
    ? '+' + String(draggableChildCount)
    : undefined;
}

export function canStartWebWalletGroupReorder(
  row: RowModel,
  memberKey: string | undefined
): boolean {
  if (row.type !== 'walletGroup' || !memberKey) return true;
  const member = [row.parent, ...row.children].find(
    (candidate) => candidate.key === memberKey
  );
  // Legacy: only `draggable: false` excludes a member; disabled members may
  // still start the atomic group drag.
  return member?.draggable !== false;
}

function itemStart(item: WebLayoutItem, horizontal: boolean): number {
  return horizontal ? item.x : item.y;
}

function itemEnd(item: WebLayoutItem, horizontal: boolean): number {
  return itemStart(item, horizontal) + (horizontal ? item.width : item.height);
}

export function visibleWebLayoutItems(
  layout: WebListLayout,
  offset: number,
  viewportLength: number,
  overscan = 0
): readonly WebLayoutItem[] {
  if (layout.items.length === 0) return [];
  const start = Math.max(0, offset - overscan);
  const end = offset + viewportLength + overscan;
  let low = 0;
  let high = layout.items.length;
  while (low < high) {
    const middle = Math.floor((low + high) / 2);
    if (itemStart(layout.items[middle], layout.horizontal) < start)
      low = middle + 1;
    else high = middle;
  }
  // Grid items in a band share a start coordinate but can have different
  // heights. Include the preceding band when any of its items overlaps the
  // viewport; every earlier band is guaranteed to have ended before it.
  if (low > 0) {
    const previousStart = itemStart(layout.items[low - 1], layout.horizontal);
    let bandStart = low - 1;
    while (
      bandStart > 0 &&
      itemStart(layout.items[bandStart - 1], layout.horizontal) ===
        previousStart
    )
      bandStart -= 1;
    if (
      layout.items
        .slice(bandStart, low)
        .some((item) => itemEnd(item, layout.horizontal) >= start)
    )
      low = bandStart;
  }
  const visible: WebLayoutItem[] = [];
  for (let index = low; index < layout.items.length; index += 1) {
    const item = layout.items[index];
    if (itemStart(item, layout.horizontal) > end) break;
    if (itemEnd(item, layout.horizontal) >= start) visible.push(item);
  }
  return visible;
}

export type WebCollapsiblePagerScrollMetrics = Readonly<{
  offset: number;
  viewportLength: number;
}>;

export function resolveWebCollapsiblePagerScrollMetrics(
  rawOffset: number,
  rawViewportLength: number,
  headerInset: number,
  stickyInset: number
): WebCollapsiblePagerScrollMetrics {
  const header = Math.max(0, headerInset);
  const sticky = Math.max(0, stickyInset);
  return {
    offset: Math.max(0, rawOffset - header),
    viewportLength: Math.max(0, rawViewportLength - sticky),
  };
}

export function resolveWebCollapsiblePagerRawOffset(
  logicalOffset: number,
  headerInset: number
): number {
  return Math.max(0, logicalOffset) + Math.max(0, headerInset);
}

export function webLayoutItemsForMount(
  layout: WebListLayout,
  offset: number,
  viewportLength: number,
  virtualizationEnabled: boolean,
  overscan = 0
): readonly WebLayoutItem[] {
  return virtualizationEnabled
    ? visibleWebLayoutItems(layout, offset, viewportLength, overscan)
    : layout.items;
}

function rowWithoutSelectionState(row: RowModel): unknown {
  const copy = { ...row } as Record<string, unknown>;
  delete copy.selected;
  if (
    row.type === 'sectionHeader' ||
    row.type === 'action' ||
    row.type === 'dataRow'
  ) {
    if (row.checkbox) copy.checkbox = { ...row.checkbox, state: undefined };
  }
  if (row.type === 'identity' || row.type === 'action') {
    copy.trailing = row.trailing?.map((accessory) =>
      accessory.kind === 'checkbox'
        ? { ...accessory, state: undefined }
        : accessory
    );
  }
  return copy;
}

export function webRowRenderSignature(row: RowModel): string {
  return JSON.stringify(rowWithoutSelectionState(row));
}

const MARKET_QUOTE_PATCH_FIELDS = new Set([
  'revision',
  'price',
  'priceSegments',
  'change',
  'accessibilityLabel',
]);

export function isWebMarketQuotePatch(patch: RowPatch): boolean {
  return (
    patch.type === 'market' &&
    Object.keys(patch.changes).every((key) =>
      MARKET_QUOTE_PATCH_FIELDS.has(key)
    )
  );
}

/** Number of Market image-binding passes caused by one Web patch transaction. */
export function webMarketImageBindDeltaForPatches(
  patches: readonly RowPatch[]
): 0 | 1 {
  return patches.length > 0 && patches.every(isWebMarketQuotePatch) ? 0 : 1;
}

// OneKey patch: selector names must shrink before the sidebar clips their contents.
export const WEB_LIST_CSS = `
.ok-native-list-table-secondary{display:block;min-width:0;overflow:clip}.ok-native-list-table-secondary-track{display:flex;flex-direction:column;transition:transform 100ms ease}.ok-native-list-market-table:focus-within .ok-native-list-table-secondary[data-has-hover="true"]>.ok-native-list-table-secondary-track{transform:translateY(calc(-1 * var(--nl-table-secondary-height)))}@media(hover:hover){.ok-native-list-market-table:hover .ok-native-list-table-secondary[data-has-hover="true"]>.ok-native-list-table-secondary-track{transform:translateY(calc(-1 * var(--nl-table-secondary-height)))}}.ok-native-list-market-table button:focus-visible{outline:2px solid var(--nl-accent);outline-offset:1px}.ok-native-list-market-table button:hover{opacity:.75}.ok-native-list-market-table button:active{opacity:.5}

[data-native-list-selector="walletSidebar"] .ok-native-list-title{max-width:100%;min-width:0}
.ok-native-list-root{--nl-bg:#f7f7f7;--nl-row:#fff;--nl-selected:#eaf2ff;--nl-pressed:#e8e8e8;--nl-hover:#e8e8e8;--nl-subdued:#f9f9f9;--nl-strong:#0000000f;--nl-primary:#111;--nl-secondary:#6b7280;--nl-disabled:#8d8d8d;--nl-icon:#111;--nl-icon-subdued:#8d8d8d;--nl-separator:#e5e7eb;--nl-accent:#2f6bff;--nl-positive:#15803d;--nl-negative:#dc2626;--nl-critical:#feecec;--nl-inverse:#202020;--nl-inverse-text:#fcfcfc;--nl-info:#0d74ce;position:absolute;inset:0;display:flex;flex-direction:column;min-width:0;min-height:0;overflow:hidden;background:var(--nl-bg);color:var(--nl-primary);font-synthesis:none;font-family:var(--nl-font-regular)}
.ok-native-list-viewport-frame{position:relative;flex:1;min-width:0;min-height:0;overflow:hidden}
.ok-native-list-viewport{position:absolute;inset:0;overflow:auto;overscroll-behavior:contain;-webkit-overflow-scrolling:touch;scrollbar-gutter:stable}
.ok-native-list-content{position:relative;min-width:100%;min-height:100%}
.ok-native-list-item{position:absolute;left:0;top:0;box-sizing:border-box;contain:layout paint style;outline:none}
.ok-native-list-item[data-native-list-media-gallery="true"]{pointer-events:none}.ok-native-list-item[data-native-list-media-gallery="true"]>.ok-native-list-media{pointer-events:auto}
.ok-native-list-row{width:100%;height:100%;box-sizing:border-box;display:flex;align-items:center;gap:12px;overflow:hidden;background:var(--nl-row);color:var(--nl-primary);cursor:default;user-select:none;-webkit-user-select:none}
.ok-native-list-item[data-table-alternate="true"]>.ok-native-list-row{background:var(--nl-bg)}
.ok-native-list-item[data-native-list-selected="true"]>.ok-native-list-row{background:var(--nl-selected)}
.ok-native-list-item[data-native-list-disabled="true"]>.ok-native-list-row{cursor:default}
.ok-native-list-item[data-native-list-row-press-enabled="true"]>.ok-native-list-row{cursor:pointer}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:hover>.ok-native-list-row{background:var(--nl-hover)}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:active>.ok-native-list-row{background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:not([data-native-list-selected="true"]):hover>.ok-native-list-wallet-row{background:var(--nl-strong)}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:not([data-native-list-selected="true"]):active>.ok-native-list-wallet-row{background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-selected="true"]:hover>.ok-native-list-wallet-row{background:var(--nl-selected)}
.ok-native-list-wallet-group{display:flex;width:100%;height:100%;box-sizing:border-box;flex-direction:column;gap:12px;overflow:hidden;border:1px solid var(--nl-separator);border-radius:12px;background:var(--nl-subdued);user-select:none;-webkit-user-select:none}
.ok-native-list-wallet-member{flex:0 0 68px;height:68px;overflow:hidden;border-radius:12px;background:transparent;cursor:pointer}
.ok-native-list-wallet-member>.ok-native-list-wallet-row{height:100%;background:transparent}
.ok-native-list-wallet-member[data-native-list-selected="true"]>.ok-native-list-wallet-row{background:var(--nl-selected)}
.ok-native-list-wallet-member:not([data-native-list-selected="true"]):hover>.ok-native-list-wallet-row{background:var(--nl-strong)}
.ok-native-list-wallet-member:not([data-native-list-selected="true"]):active>.ok-native-list-wallet-row{background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:focus-visible>.ok-native-list-row{outline:2px solid var(--nl-accent);outline-offset:-2px}
.ok-native-list-item[data-native-list-reorderable="true"]>.ok-native-list-row{cursor:grab}
.ok-native-list-root[data-native-list-dragging="true"] .ok-native-list-row{cursor:grabbing}
.ok-native-list-item[data-native-list-animate-reorder="true"]{transition:transform ${WEB_REORDER_ANIMATION.outOfWayDurationMs}ms ${WEB_REORDER_ANIMATION.outOfWayTimingFunction},height ${WEB_REORDER_ANIMATION.outOfWayDurationMs}ms ${WEB_REORDER_ANIMATION.outOfWayTimingFunction}}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-row{overflow:hidden;border-radius:12px;background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-row>*{visibility:hidden}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-wallet-group{border-color:transparent;background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-wallet-group>*{visibility:hidden}
.ok-native-list-reorder-preview{position:fixed;left:0;top:0;z-index:100001;pointer-events:none;overflow:hidden;border-radius:12px;box-shadow:0 4px 24px rgba(0,0,0,.12);transform-origin:center;will-change:transform;font-family:var(--nl-font-regular);font-synthesis:none}
.ok-native-list-reorder-preview>.ok-native-list-row{background:var(--nl-pressed);cursor:grabbing}
.ok-native-list-reorder-preview[data-native-list-selected="true"]>.ok-native-list-row{background:var(--nl-selected)}
.ok-native-list-reorder-count{position:absolute;right:4px;bottom:4px;display:flex;align-items:center;justify-content:center;box-sizing:border-box;min-width:24px;height:24px;padding:0 6px;border:1px solid var(--nl-row);border-radius:12px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;line-height:22px;font-weight:600;font-family:var(--nl-font-semibold)}
.ok-native-list-item[data-separator="true"]>.ok-native-list-row{border-bottom:1px solid var(--nl-separator-color,var(--nl-separator))}
/* An inset separator keeps the transparent border so the row's height is unchanged, and paints the visible line with a logical-inset overlay so it follows RTL. */
.ok-native-list-root[data-separator-inset="true"] .ok-native-list-item[data-separator="true"]>.ok-native-list-row{position:relative;border-bottom-color:transparent}
.ok-native-list-root[data-separator-inset="true"] .ok-native-list-item[data-separator="true"]>.ok-native-list-row::after{content:"";position:absolute;inset-inline-start:var(--nl-separator-inset,0);inset-inline-end:0;bottom:-1px;height:1px;background:var(--nl-separator-color,var(--nl-separator))}
.ok-native-list-item[data-group-position="first"]>.ok-native-list-row{border-radius:var(--nl-group-radius,12px) var(--nl-group-radius,12px) 0 0}
.ok-native-list-item[data-group-position="last"]>.ok-native-list-row{border-radius:0 0 var(--nl-group-radius,12px) var(--nl-group-radius,12px)}
.ok-native-list-item[data-group-position="single"]>.ok-native-list-row{border-radius:var(--nl-group-radius,12px)}
.ok-native-list-standard{padding:8px 12px}.ok-native-list-network-row{padding:0 12px}.ok-native-list-wallet-row{padding:4px 8px;flex-direction:column;justify-content:center;gap:4px}.ok-native-list-account-row{padding:4px 12px;gap:8px}
.ok-native-list-trailing-button{appearance:none;box-sizing:border-box;flex:0 0 auto;padding:4px 10px;border:1px solid transparent;border-radius:9999px;background:var(--nl-button-bg,var(--nl-strong));color:var(--nl-button-text,var(--nl-primary));font:500 14px/20px var(--nl-font-medium);white-space:nowrap;cursor:pointer}.ok-native-list-trailing-button:hover:not(:disabled){background:var(--nl-button-hover,var(--nl-strong))}.ok-native-list-trailing-button:active:not(:disabled){background:var(--nl-button-pressed,var(--nl-pressed))}.ok-native-list-trailing-button:focus-visible{outline:2px solid var(--nl-button-focus,var(--nl-accent))}.ok-native-list-trailing-button:disabled{opacity:.4;cursor:default}
.ok-native-list-icon-button.ok-native-list-tertiary-icon{box-sizing:border-box;flex:0 0 38px;width:38px;height:38px;margin:-7px;padding:6px;border:1px solid transparent;border-radius:9999px;color:var(--nl-icon-subdued);background:transparent}.ok-native-list-icon-button.ok-native-list-tertiary-icon:hover:not(:disabled){background:var(--nl-hover)}.ok-native-list-icon-button.ok-native-list-tertiary-icon:active:not(:disabled){background:var(--nl-pressed)}.ok-native-list-icon-button.ok-native-list-tertiary-icon:disabled{opacity:.4;cursor:default}
.ok-native-list-icon-button.ok-native-list-tertiary-icon:focus-visible:not(:disabled){outline:2px solid var(--nl-accent)}
.ok-native-list-identity-title-icon{flex:0 0 20px;width:20px;height:20px;display:inline-flex;align-items:center;justify-content:center;color:var(--nl-icon);font-size:20px}.ok-native-list-identity-title-text{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.ok-native-list-account-row .ok-native-list-visual,.ok-native-list-account-action-row .ok-native-list-visual{width:32px;height:32px;flex-basis:32px}.ok-native-list-account-row .ok-native-list-visual>img,.ok-native-list-account-row .ok-native-list-visual-main,.ok-native-list-account-action-row .ok-native-list-visual>img,.ok-native-list-account-action-row .ok-native-list-visual-main{width:32px;height:32px}.ok-native-list-account-row .ok-native-list-visual>.ok-native-list-visual-corner{width:20px;height:20px;padding:2px}.ok-native-list-account-action-row .ok-native-list-visual{border-radius:8px!important}.ok-native-list-wallet-row .ok-native-list-title{color:var(--nl-secondary);font-size:12px;line-height:16px;font-weight:400;font-family:var(--nl-font-regular)}.ok-native-list-item[data-native-list-selected="true"]>.ok-native-list-wallet-row .ok-native-list-title{color:var(--nl-primary)}.ok-native-list-account-row .ok-native-list-title{font-size:16px;line-height:20px;font-weight:400;font-family:var(--nl-font-regular)}.ok-native-list-account-row .ok-native-list-secondary{font-size:14px;line-height:20px;font-weight:400;font-family:var(--nl-font-regular)}
.ok-native-list-flex{display:flex;flex:1;min-width:0;flex-direction:column;justify-content:center}.ok-native-list-title{font-size:15px;line-height:20px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;font-family:var(--nl-font-semibold)}.ok-native-list-secondary{font-size:13px;line-height:18px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-tertiary{color:var(--nl-secondary)}.ok-native-list-info{color:var(--nl-info)}
.ok-native-list-identity-row .ok-native-list-title,.ok-native-list-identity-row .ok-native-list-amounts>span:first-child{font-size:16px;line-height:20px}.ok-native-list-identity-row .ok-native-list-secondary{font-size:14px;line-height:20px}
.ok-native-list-value{font-size:14px;line-height:20px;font-weight:500;white-space:nowrap;font-family:var(--nl-font-medium)}.ok-native-list-time{font-size:11px;line-height:16px;color:var(--nl-secondary);align-self:flex-start}.ok-native-list-amounts{display:flex;flex-direction:column;align-items:flex-end;min-width:0}.ok-native-list-actions{display:flex;gap:16px;margin-top:6px}
.ok-native-list-action-button{appearance:none;border:0;background:transparent;padding:2px;color:var(--nl-accent);cursor:pointer;font:600 12px/16px var(--nl-font-semibold)}.ok-native-list-action-button[data-tone="danger"]{color:var(--nl-negative)}.ok-native-list-action-button:disabled{opacity:.5;cursor:default}
.ok-native-list-visual{position:relative;flex:0 0 40px;width:40px;height:40px;border-radius:20px;overflow:visible;background:var(--nl-strong)}.ok-native-list-visual>img,.ok-native-list-visual-main{display:block;width:40px;height:40px;border-radius:inherit;object-fit:cover}.ok-native-list-visual-fallback{display:flex;width:100%;height:100%;align-items:center;justify-content:center;border-radius:inherit;color:var(--nl-primary);font-size:15px;font-weight:600;overflow:hidden;font-family:var(--nl-font-semibold)}.ok-native-list-image-skeleton{position:absolute;inset:0;border-radius:inherit;pointer-events:none;animation:ok-native-list-skeleton-light 1.5s linear infinite alternate}.ok-native-list-image-skeleton[data-dark="true"]{animation-name:ok-native-list-skeleton-dark}@keyframes ok-native-list-skeleton-light{0%{background-color:#fafafa}100%{background-color:#cdcdcd}}@keyframes ok-native-list-skeleton-dark{0%{background-color:#111111}100%{background-color:#333333}}@media(prefers-reduced-motion:reduce){.ok-native-list-image-skeleton{animation:none;background-color:var(--nl-strong)}}.ok-native-list-visual>.ok-native-list-visual-corner{position:absolute;right:-4px;bottom:-4px;width:18px;height:18px;padding:2px;border-radius:50%;background:var(--nl-row);object-fit:cover}.ok-native-list-stacked{display:flex;align-items:center;width:48px;flex-basis:48px}.ok-native-list-stacked img{width:28px;height:28px;border:2px solid var(--nl-row);border-radius:50%;object-fit:cover;margin-left:-10px}.ok-native-list-stacked img:first-child{margin-left:0}
.ok-native-list-accessories{display:flex;align-items:center;gap:10px;min-width:0}.ok-native-list-accessory{font-size:14px;color:var(--nl-primary);white-space:nowrap}.ok-native-list-accessory-secondary{color:var(--nl-secondary)}.ok-native-list-icon-button{display:inline-flex;align-items:center;justify-content:center;min-width:24px;height:24px;border:0;background:transparent;color:var(--nl-icon);font-size:20px;line-height:1;font-weight:500;cursor:pointer;font-family:var(--nl-font-medium)}.ok-native-list-checkbox{display:inline-flex;align-items:center;justify-content:center;box-sizing:border-box;width:20px;height:20px;border:2px solid color-mix(in srgb,var(--nl-primary) 19%,transparent);border-radius:5px;background:var(--nl-row);cursor:pointer}.ok-native-list-checkbox[data-state="checked"],.ok-native-list-checkbox[data-state="indeterminate"]{border-color:transparent;background:var(--nl-primary)}.ok-native-list-checkbox[data-state="checked"]::after{content:"";width:6px;height:10px;margin-top:-2px;border-right:2px solid var(--nl-row);border-bottom:2px solid var(--nl-row);transform:rotate(45deg)}.ok-native-list-checkbox[data-state="indeterminate"]::after{content:"";width:8px;height:2px;border-radius:1px;background:var(--nl-row)}.ok-native-list-spinner{width:16px;height:16px;border:2px solid var(--nl-separator);border-top-color:var(--nl-primary);border-radius:50%;animation:ok-native-list-spin .8s linear infinite}@keyframes ok-native-list-spin{to{transform:rotate(360deg)}}
.ok-native-list-icon-button.ok-native-list-decorative-refresh{min-width:16px;width:16px;height:16px}
.ok-native-list-badge{display:inline-flex;align-items:center;max-width:100%;height:18px;padding:0 5px;border-radius:5px;background:color-mix(in srgb,var(--nl-accent) 12%,transparent);color:var(--nl-accent);font-size:11px;line-height:18px;white-space:nowrap}.ok-native-list-badge[data-tone="danger"]{background:var(--nl-critical);color:var(--nl-negative)}.ok-native-list-badge[data-tone="success"]{background:var(--nl-success-background,color-mix(in srgb,var(--nl-positive) 12%,transparent));color:var(--nl-positive)}.ok-native-list-section>.ok-native-list-badge[data-tone="success"]{box-sizing:border-box;height:20px;padding:2px 8px;border-radius:4px;font:500 12px/16px var(--nl-font-medium)}.ok-native-list-badges{display:inline-flex;gap:4px;margin-left:6px;vertical-align:middle}
.ok-native-list-identity-row .ok-native-list-badge[data-tone="neutral"]{height:20px;box-sizing:border-box;padding:2px 8px;border-radius:4px;background:var(--nl-strong);color:var(--nl-secondary);font-size:12px;line-height:16px;font-weight:500;font-family:var(--nl-font-medium)}
.ok-native-list-section{padding:0 8px;background:var(--nl-bg);gap:8px}.ok-native-list-section[data-variant="summary"]{padding:0 20px}.ok-native-list-section[data-variant="gallery"]{padding:0 12px}.ok-native-list-section[data-variant="history"]{padding:0 8px}.ok-native-list-section-title{font-size:13px;line-height:18px;font-weight:700;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;font-family:var(--nl-font-bold)}.ok-native-list-section[data-variant="summary"] .ok-native-list-section-title{font-size:16px;font-weight:500;color:var(--nl-primary);font-family:var(--nl-font-medium)}.ok-native-list-section[data-variant="gallery"] .ok-native-list-section-title{font-size:16px;color:var(--nl-primary)}.ok-native-list-section[data-variant="history"] .ok-native-list-section-title{font-size:12px;line-height:16px;letter-spacing:0.8px}.ok-native-list-section-value{margin-left:auto}
.ok-native-list-action-row{justify-content:space-between;padding:0 16px}.ok-native-list-action-title{font-size:15px;font-weight:600;color:var(--nl-accent);font-family:var(--nl-font-semibold)}.ok-native-list-action-title[data-tone="danger"]{color:var(--nl-negative)}
.ok-native-list-button-group{padding:0;gap:10px;overflow:visible}.ok-native-list-block-button{position:relative;display:flex;flex:1 1 0;min-width:0;min-height:48px;align-items:center;justify-content:center;gap:8px;padding:12px 8px;border:1px solid color-mix(in srgb,var(--nl-info) 35%,transparent);border-radius:12px;background:transparent;color:var(--nl-info);font:500 14px/20px var(--nl-font-medium);cursor:pointer}.ok-native-list-block-button>span:not(.ok-native-list-spinner){min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.ok-native-list-block-button>.ok-native-list-spinner{position:static;flex:0 0 20px;width:20px;height:20px;border-top-color:var(--nl-info)}.ok-native-list-block-button:hover:not(:disabled){background:color-mix(in srgb,var(--nl-info) 10%,transparent);border-color:var(--nl-info)}.ok-native-list-block-button:active:not(:disabled){background:color-mix(in srgb,var(--nl-info) 17%,transparent)}.ok-native-list-block-button:focus-visible{outline:2px solid var(--nl-info);outline-offset:1px}.ok-native-list-block-button:disabled{opacity:.4;cursor:default}
.ok-native-list-account-action-row{justify-content:flex-start;gap:12px;padding:0 12px}.ok-native-list-account-action-row .ok-native-list-action-title{color:var(--nl-secondary);font-size:16px;line-height:24px;font-weight:400;font-family:var(--nl-font-regular)}
.ok-native-list-system{justify-content:center;padding:8px 12px;background:var(--nl-bg);color:var(--nl-secondary);cursor:default}.ok-native-list-system[data-variant="retry"],.ok-native-list-system[data-variant="noMatch"],.ok-native-list-system[data-variant="end"]{justify-content:flex-start}.ok-native-list-system[data-variant="retry"]{cursor:pointer}
.ok-native-list-rail{padding:4px;gap:6px;border-radius:8px}.ok-native-list-rail .ok-native-list-visual{width:20px;height:20px;flex-basis:20px}.ok-native-list-rail .ok-native-list-visual>img,.ok-native-list-rail .ok-native-list-visual-main{width:20px;height:20px}.ok-native-list-rail-title{font-size:12px;font-weight:500;white-space:nowrap;font-family:var(--nl-font-medium)}
.ok-native-list-media{display:block;padding:0 5px;background:transparent;border-radius:16px}.ok-native-list-media-image{display:block;width:100%;aspect-ratio:1;border-radius:10px;background:var(--nl-strong);object-fit:cover}.ok-native-list-media-image[data-state="empty"]{background:transparent}.ok-native-list-media-image[data-state="error"]{display:flex;align-items:center;justify-content:center;color:var(--nl-icon-subdued);font-size:24px}.ok-native-list-media-meta{padding-top:7px}.ok-native-list-media-subtitle-row{display:flex;align-items:center;gap:6px}.ok-native-list-media-subtitle{flex:1;min-width:0;font-size:12px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-media-network{width:14px;height:14px;border-radius:50%}.ok-native-list-media-title{font-size:16px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;font-family:var(--nl-font-medium)}.ok-native-list-media-close{position:absolute;right:9px;top:4px;border:0;background:color-mix(in srgb,var(--nl-inverse) 72%,transparent);color:var(--nl-inverse-text);width:24px;height:24px;border-radius:50%;cursor:pointer;font:18px/20px var(--nl-font-regular)}
.ok-native-list-metric{display:flex;flex-direction:column;align-items:flex-start;padding:12px;border-radius:12px;gap:5px;background:var(--nl-row)}.ok-native-list-metric-value{font-size:22px;line-height:28px;font-weight:700;font-family:var(--nl-font-bold)}.ok-native-list-composite{display:flex;flex-direction:column;align-items:stretch;padding:14px;border-radius:12px;gap:12px;background:var(--nl-subdued)}.ok-native-list-composite-heading{font-size:14px;letter-spacing:1px;color:var(--nl-secondary)}.ok-native-list-composite-row{display:flex;gap:12px}.ok-native-list-composite-cell{flex:1;min-width:0}.ok-native-list-composite-cell[data-shaded="true"]{padding:10px;border-radius:10px;background:color-mix(in srgb,var(--nl-primary) 5%,transparent)}.ok-native-list-composite-value{font-size:18px;font-weight:600;font-family:var(--nl-font-semibold)}.ok-native-list-divider{height:1px;background:var(--nl-separator)}.ok-native-list-progress{height:4px;border-radius:2px;overflow:hidden;background:var(--nl-negative)}.ok-native-list-progress>span{display:block;height:100%;border-radius:2px;background:var(--nl-positive)}
.ok-native-list-data{padding:6px 12px}.ok-native-list-index{flex:0 0 28px;color:var(--nl-secondary);font-size:13px}.ok-native-list-favorite{flex:0 0 24px;color:var(--nl-icon-subdued);font-size:22px}.ok-native-list-favorite[data-active="true"]{color:var(--nl-accent)}.ok-native-list-data-cell{display:flex;flex-direction:column;min-width:0}.ok-native-list-data-cell[data-align="center"]{align-items:center}.ok-native-list-data-cell[data-align="end"]{align-items:flex-end}.ok-native-list-data-primary{display:flex;align-items:center;gap:5px;max-width:100%;font-size:16px;font-weight:500;white-space:nowrap;font-family:var(--nl-font-medium)}.ok-native-list-unread{width:7px;height:7px;flex:0 0 7px;border-radius:50%;background:var(--nl-accent)}.ok-native-list-thumbnail{width:64px;height:64px;border-radius:10px;object-fit:cover}
.ok-native-list-item[data-native-list-row-press-enabled="true"]:active>.ok-native-list-history{background:var(--nl-selected)}
.ok-native-list-root[data-native-list-parent-scroll="true"] .ok-native-list-item[data-native-list-row-press-enabled="true"]:active>.ok-native-list-market:not([data-variant="stock"]){opacity:.8}
.ok-native-list-root[data-native-list-parent-scroll="true"] .ok-native-list-item[data-native-list-row-press-enabled="true"]:active>.ok-native-list-market[data-variant="stock"]{background:var(--nl-selected)}
.ok-native-list-market-table .ok-native-list-visual-fallback>svg{width:28px;height:28px}
.ok-native-list-market{padding:12px 20px;gap:14px}.ok-native-list-market>.ok-native-list-visual{width:32px;height:32px;flex-basis:32px}.ok-native-list-market[data-variant="stock"]>.ok-native-list-visual{width:40px;height:40px;flex-basis:40px}.ok-native-list-market>.ok-native-list-visual>.ok-native-list-visual-main{width:100%;height:100%}.ok-native-list-market-main{display:flex;flex:1;min-width:0;flex-direction:column;justify-content:center}.ok-native-list-market-title-line{display:flex;align-items:center;min-width:0;gap:4px}.ok-native-list-market-title{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--nl-primary);font-size:16px;line-height:24px;font-weight:500;font-family:var(--nl-font-medium)}.ok-native-list-market-subtitle{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--nl-secondary);font-size:14px;line-height:20px;font-weight:400;font-family:var(--nl-font-regular)}.ok-native-list-market-badge{display:inline-flex;flex:0 0 auto;align-items:center;justify-content:center;box-sizing:border-box;max-width:72px;height:18px;padding:0 5px;border:0;border-radius:4px;background:var(--nl-strong);color:var(--nl-secondary);overflow:hidden;white-space:nowrap;font:500 11px/18px var(--nl-font-medium);font-variant-numeric:tabular-nums}.ok-native-list-market-badge img{width:14px;height:14px;object-fit:contain}.ok-native-list-market-trailing{display:flex;flex:0 0 auto;align-items:center;gap:8px}.ok-native-list-market-price{max-width:112px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;text-align:right;color:var(--nl-primary);font-size:16px;line-height:24px;font-weight:500;font-variant-numeric:tabular-nums;font-family:var(--nl-font-medium)}.ok-native-list-market-change{display:flex;align-items:center;justify-content:center;box-sizing:border-box;width:80px;height:32px;border-radius:8px;color:#fff;background:#8d8d8d;font-size:14px;line-height:20px;font-weight:500;font-variant-numeric:tabular-nums;white-space:nowrap;font-family:var(--nl-font-medium)}.ok-native-list-market-change[data-tone="positive"]{background:var(--nl-positive)}.ok-native-list-market-change[data-tone="negative"]{background:var(--nl-negative)}
.ok-native-list-market-badge>svg,.ok-native-list-market-badge>.ok-native-list-market-badge-icon>svg{display:block;width:16px;height:16px;flex:0 0 16px}.ok-native-list-system[data-native-list-presentation="market"]{box-sizing:border-box;padding:12px 20px}.ok-native-list-system[data-native-list-presentation="market"]>.ok-native-list-spinner{width:32px;height:32px}
.ok-native-list-index-preview{position:absolute;z-index:8;right:40px;top:50%;display:flex;width:48px;height:48px;align-items:center;justify-content:center;transform:translateY(-50%) scale(.92);border-radius:14px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:22px;font-weight:600;opacity:0;pointer-events:none;transition:opacity .15s ease,transform .15s ease;font-family:var(--nl-font-semibold)}.ok-native-list-index-preview[data-visible="true"]{opacity:1;transform:translateY(-50%) scale(1)}
.ok-native-list-row.ok-native-list-market-skeleton{padding:12px 20px;gap:0}.ok-native-list-skeleton-left{display:flex;align-items:center;gap:12px;flex:1}.ok-native-list-skeleton-text{display:flex;flex-direction:column;gap:4px}.ok-native-list-skeleton-right{display:flex;align-items:center;gap:8px}.ok-native-list-skeleton-mark{display:block;flex-shrink:0;border-radius:8px;animation:ok-native-list-skeleton 1.5s linear infinite alternate}@keyframes ok-native-list-skeleton{from{background-color:var(--nl-skeleton-base)}to{background-color:var(--nl-skeleton-highlight)}}.ok-native-list-market-spinner{display:block;width:20px;height:20px;flex-shrink:0;color:var(--nl-icon);animation:ok-native-list-spin .75s linear infinite}
@media (prefers-reduced-motion:reduce){.ok-native-list-index-preview,.ok-native-list-refresh{transition:none}.ok-native-list-spinner,.ok-native-list-market-spinner{animation:none}.ok-native-list-skeleton-mark{animation:none;background:var(--nl-skeleton-base)}}
.ok-native-list-footer{flex:0 0 auto;min-height:0}.ok-native-list-sticky{position:absolute;z-index:4;left:0;right:0;top:0;pointer-events:auto;box-shadow:0 1px 0 var(--nl-separator)}.ok-native-list-viewport-frame:has(>.ok-native-list-index-rail:not([hidden]))>.ok-native-list-viewport,.ok-native-list-viewport-frame[data-section-index-visible="true"]>.ok-native-list-viewport{scrollbar-width:none}.ok-native-list-viewport-frame:has(>.ok-native-list-index-rail:not([hidden]))>.ok-native-list-viewport::-webkit-scrollbar,.ok-native-list-viewport-frame[data-section-index-visible="true"]>.ok-native-list-viewport::-webkit-scrollbar{display:none}.ok-native-list-index-rail{position:absolute;z-index:6;top:0;right:0;bottom:0;width:${SECTION_INDEX_RAIL_WIDTH}px;touch-action:none;cursor:pointer;font-family:var(--nl-font-regular);font-synthesis:none}.ok-native-list-index-rail[hidden]{display:none}.ok-native-list-index-button{appearance:none;position:absolute;left:15px;display:flex;width:14px;height:14px;align-items:center;justify-content:center;padding:0;transform:translateY(-50%);border:0;border-radius:7px;background:transparent;color:var(--nl-disabled);font-size:10px;font-weight:400;line-height:1;cursor:pointer;opacity:.5;transition:opacity .15s ease;font-family:var(--nl-font-regular)}.ok-native-list-index-rail:hover .ok-native-list-index-button,.ok-native-list-index-rail:focus-within .ok-native-list-index-button,.ok-native-list-index-button[data-active="true"]{opacity:1}.ok-native-list-index-button[data-active="true"]{background:var(--nl-positive);color:var(--nl-inverse-text);font-weight:500;font-family:var(--nl-font-medium)}.ok-native-list-index-button:focus-visible{outline:2px solid var(--nl-positive);outline-offset:1px}
.ok-native-list-refresh{position:absolute;z-index:7;left:50%;top:8px;display:flex;align-items:center;gap:6px;padding:6px 10px;border-radius:999px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;opacity:0;transform:translate(-50%,-16px);transition:opacity .15s ease,transform .15s ease;pointer-events:none}.ok-native-list-refresh[data-visible="true"]{opacity:1;transform:translate(-50%,0)}
.ok-native-list-warning{height:auto;display:flex;flex-direction:column;align-items:stretch;gap:4px;padding:14px 12px;border-top:1px solid;border-bottom:1px solid;box-sizing:border-box;cursor:default}.ok-native-list-warning-title,.ok-native-list-warning-message{font-size:14px;line-height:20px;white-space:normal;overflow-wrap:anywhere}.ok-native-list-warning-title{font-weight:500;color:var(--nl-primary);font-family:var(--nl-font-medium)}.ok-native-list-warning-message{font-weight:400;color:var(--nl-secondary);font-family:var(--nl-font-regular)}
.ok-native-list-subtitle-segments{display:flex;align-items:center;min-width:0;max-width:100%;height:20px}.ok-native-list-subtitle-segments>.ok-native-list-secondary{flex:0 1 auto;min-width:0}.ok-native-list-subtitle-dot{flex:0 0 4px;width:4px;height:4px;margin:0 6px;border-radius:50%;background:var(--nl-disabled)}.ok-native-list-wallet-row>.ok-native-list-flex{flex:0 1 auto;width:100%;align-items:center}.ok-native-list-wallet-badges{display:flex;gap:4px;justify-content:center;margin-top:4px;height:20px;max-width:100%}.ok-native-list-wallet-badges>.ok-native-list-badge{background:var(--nl-strong);color:var(--nl-secondary);font-size:12px;line-height:16px;height:20px;box-sizing:border-box;padding:2px 4px}.ok-native-list-visual-overlay{position:absolute;display:flex;align-items:center;justify-content:center;box-sizing:border-box;border-radius:50%;overflow:hidden;line-height:1;font-size:10px}.ok-native-list-visual-overlay img,.ok-native-list-visual-overlay svg{width:100%;height:100%;object-fit:contain}/* OneKey patch: selector controls follow their original semantic colors and geometry. */
.ok-native-list-checkbox[data-selector="networkSelector"]{padding:0;border-radius:4px;border-color:var(--nl-checkbox-border,var(--nl-separator));background:var(--nl-checkbox-icon,var(--nl-inverse-text))}
.ok-native-list-checkbox[data-selector="networkSelector"]::after{display:none}
.ok-native-list-checkbox[data-selector="networkSelector"]>svg{display:none;width:16px;height:16px;color:var(--nl-checkbox-icon,var(--nl-inverse-text));flex-shrink:0}
.ok-native-list-checkbox[data-selector="networkSelector"][data-state="checked"],.ok-native-list-checkbox[data-selector="networkSelector"][data-state="indeterminate"]{border-color:transparent;background:var(--nl-checkbox-background,var(--nl-primary))}
.ok-native-list-checkbox[data-selector="networkSelector"][data-state="checked"]>svg[data-state="checked"],.ok-native-list-checkbox[data-selector="networkSelector"][data-state="indeterminate"]>svg[data-state="indeterminate"]{display:block}
/* OneKey patch: source WalletListItem uses four-point padding on every side. */
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"]{border-radius:12px;padding:4px}
/* OneKey patch: selected group members use the same primary title color as standalone wallets. */
.ok-native-list-wallet-member[data-native-list-selected="true"]>.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-title{color:var(--nl-primary)}
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-wallet-badges{height:18px}
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-wallet-badges>.ok-native-list-badge{font-size:11px;line-height:14px;font-weight:400;height:18px;padding:2px 6px;border-radius:4px;background:var(--nl-subdued);color:var(--nl-secondary);font-family:var(--nl-font-regular)}
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-wallet-badges>.ok-native-list-badge[data-tone="warning"]{background:var(--nl-caution-background);color:var(--nl-caution)}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>.ok-native-list-icon-button{box-sizing:border-box;flex:0 0 38px;width:38px;height:38px;margin:-7px;padding:7px}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>.ok-native-list-icon-button[data-native-list-action]:not([data-native-list-account-control="createAddress"]):hover{background:var(--nl-strong);border-radius:50%}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>.ok-native-list-icon-button[data-native-list-action]:not([data-native-list-account-control="createAddress"]):active{background:var(--nl-pressed);border-radius:50%}
/* OneKey patch: AccountSelectorAccountListItem fixes the borderless Plus slot at top18/right20. */
.ok-native-list-account-row[data-native-list-selector="accountSelector"]>.ok-native-list-accessories[data-native-list-account-control="createAddress"]{position:absolute;top:18px;right:12px}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>[data-native-list-account-control="createAddress"]{flex-basis:36px;width:36px;height:36px;padding:6px;border-radius:8px}
.ok-native-list-account-action-row{padding-left:12px;padding-right:12px}.ok-native-list-account-action-row .ok-native-list-action-title{font-size:16px;line-height:24px;font-weight:400;font-family:var(--nl-font-regular)}.ok-native-list-account-action-row .ok-native-list-action-title[data-tone="primary"]{color:var(--nl-primary)}
/* OneKey patch: account selector wallets and accounts keep the default cursor instead of pointer or grab affordances.
   Only the row surface is matched: cursor inherits, so plain children follow the row, while icon buttons,
   checkboxes, and action buttons keep advertising themselves with their own pointer cursor. */
.ok-native-list-root .ok-native-list-item>.ok-native-list-wallet-row,.ok-native-list-root .ok-native-list-wallet-member>.ok-native-list-wallet-row,.ok-native-list-root .ok-native-list-item>.ok-native-list-account-row,.ok-native-list-root .ok-native-list-item>.ok-native-list-account-action-row,.ok-native-list-wallet-member{cursor:default}
/* OneKey patch: Add account hover and pressed backgrounds keep the account row corner radius. */
.ok-native-list-account-action-row{border-radius:12px}

.ok-native-list-root .ok-native-list-item>[data-nl-container-background="true"],.ok-native-list-root .ok-native-list-wallet-member>[data-nl-container-background="true"]{background:var(--nl-container-background)}
.ok-native-list-root [data-nl-container-border="true"]{position:relative}
.ok-native-list-root [data-nl-container-border="true"]::before{content:"";position:absolute;inset:0;border-radius:inherit;box-shadow:inset 0 0 0 var(--nl-container-border-width) var(--nl-container-border-color);z-index:2;pointer-events:none}
.ok-native-list-root [data-nl-inline-warning="true"][data-nl-container-border="true"]::before{box-shadow:none;border-block:var(--nl-container-border-width) solid var(--nl-container-border-color);border-inline:0;border-radius:0}
.ok-native-list-root .ok-native-list-item[data-native-list-row-press-enabled="true"]:hover>[data-nl-container-background="true"],.ok-native-list-root .ok-native-list-item[data-native-list-row-press-enabled="true"]:active>[data-nl-container-background="true"],.ok-native-list-root .ok-native-list-wallet-member:hover>[data-nl-container-background="true"],.ok-native-list-root .ok-native-list-wallet-member:active>[data-nl-container-background="true"]{background:var(--nl-pressed)}
`;

function createElement(
  document: Document,
  tag: string,
  className?: string,
  text?: string
): HTMLElement {
  const element = document.createElement(tag);
  if (className) element.className = className;
  if (text !== undefined) element.textContent = text;
  return element;
}

function setData(
  element: HTMLElement,
  name: string,
  value: string | number | boolean | undefined
) {
  if (value === undefined) delete element.dataset[name];
  else element.dataset[name] = String(value);
}

function safeImageUri(uri: string): string | undefined {
  const trimmed = uri.trim();
  if (
    /^(https?:|data:image\/|blob:|file:)/i.test(trimmed) ||
    trimmed.startsWith('/')
  )
    return trimmed;
  return undefined;
}

// OneKey patch: retries belong to an image binding and must not outlive recycled rows.
const webImageRetryCleanup = new WeakMap<HTMLImageElement, () => void>();
const webAvatarCleanup = new WeakMap<HTMLImageElement, () => void>();
const webAvatarSources = new WeakMap<
  HTMLImageElement,
  { uri: string; source: ImageSource }
>();
function disposeWebImageRetries(element: HTMLElement): boolean {
  let disposed = false;
  const images = element.matches('img')
    ? [element as HTMLImageElement]
    : element.querySelectorAll('img');
  images.forEach((image) => {
    const avatarCleanup = webAvatarCleanup.get(image);
    const cleanup = webImageRetryCleanup.get(image);
    if (!cleanup && !avatarCleanup) return;
    avatarCleanup?.();
    webAvatarCleanup.delete(image);
    webAvatarSources.delete(image);
    cleanup?.();
    webImageRetryCleanup.delete(image);
    disposed = true;
  });
  return disposed;
}

function configureWebImageRetry(
  image: HTMLImageElement,
  source: ImageSource,
  initialUri: string
) {
  const fallbackUri = source.fallbackUri
    ? safeImageUri(source.fallbackUri)
    : undefined;
  const retryLimit = Number.isFinite(source.retryTimes)
    ? Math.max(0, Math.floor(source.retryTimes ?? 0))
    : 0;
  const orderedFallbacks = source.fallbackUris;
  if (!fallbackUri && retryLimit === 0 && orderedFallbacks === undefined)
    return;
  const view = image.ownerDocument.defaultView;
  let currentUri = initialUri;
  let usedFallback = false;
  let fallbackIndex = 0;
  let retryCount = 0;
  let retryTimer: number | undefined;
  let disposed = false;
  const clearRetry = () => {
    if (retryTimer !== undefined) view?.clearTimeout(retryTimer);
    retryTimer = undefined;
  };
  const handleError = (event: Event) => {
    if (disposed || !image.isConnected || retryTimer !== undefined) {
      event.stopImmediatePropagation();
      return;
    }
    if (orderedFallbacks !== undefined && retryCount >= retryLimit) {
      if (fallbackIndex >= orderedFallbacks.length) return;
      let nextUri: string | undefined;
      while (!nextUri && fallbackIndex < orderedFallbacks.length) {
        nextUri = safeImageUri(orderedFallbacks[fallbackIndex++]);
      }
      if (!nextUri) return;
      event.stopImmediatePropagation();
      retryCount = 0;
      currentUri = nextUri;
      image.src = currentUri;
      return;
    }
    if (
      orderedFallbacks === undefined &&
      fallbackUri &&
      !usedFallback &&
      fallbackUri !== currentUri
    ) {
      event.stopImmediatePropagation();
      usedFallback = true;
      currentUri = fallbackUri;
      image.src = currentUri;
      return;
    }
    if (retryCount >= retryLimit || !view) return;
    event.stopImmediatePropagation();
    retryCount += 1;
    retryTimer = view.setTimeout(() => {
      retryTimer = undefined;
      if (disposed || !image.isConnected) return;
      image.removeAttribute('src');
      image.src = currentUri;
    }, Math.floor(Math.random() * 3) * 1000);
  };
  image.addEventListener('error', handleError);
  image.addEventListener('load', clearRetry);
  webImageRetryCleanup.set(image, () => {
    disposed = true;
    clearRetry();
    image.removeEventListener('error', handleError);
    image.removeEventListener('load', clearRetry);
  });
}

function configureWebAvatar(
  image: HTMLImageElement,
  source: ImageSource,
  uri: string
) {
  const dispose = acquireNativeListAvatar(
    image.ownerDocument,
    uri,
    (resolvedUri) => {
      configureWebImageRetry(image, source, resolvedUri);
      image.src = resolvedUri;
    },
    () => {
      if (source.fallbackUris !== undefined) {
        configureWebImageRetry(image, source, uri);
        const ImageEvent = image.ownerDocument.defaultView?.Event;
        if (ImageEvent) image.dispatchEvent(new ImageEvent('error'));
        return;
      }
      const fallbackUri = source.fallbackUri
        ? safeImageUri(source.fallbackUri)
        : undefined;
      if (fallbackUri) {
        configureWebImageRetry(image, source, fallbackUri);
        image.src = fallbackUri;
        return;
      }
      const ImageEvent = image.ownerDocument.defaultView?.Event;
      if (ImageEvent) image.dispatchEvent(new ImageEvent('error'));
    }
  );
  webAvatarSources.set(image, { uri, source });
  webAvatarCleanup.set(image, dispose);
}

const WEB_IMAGE_FADE_DELAY_MS = 100;
const WEB_IMAGE_FADE_DURATION_MS = 140;

function webImageNow(image: HTMLImageElement): number {
  return image.ownerDocument.defaultView?.performance.now() ?? Date.now();
}

function revealWebImage(element: HTMLElement, startedAt: number) {
  element.getAnimations?.().forEach((animation) => animation.cancel());
  element.style.opacity = '1';
  const view = element.ownerDocument.defaultView;
  if (
    webImageNow(element as HTMLImageElement) - startedAt <
      WEB_IMAGE_FADE_DELAY_MS ||
    view?.matchMedia?.('(prefers-reduced-motion: reduce)').matches ||
    typeof element.animate !== 'function'
  )
    return;
  element.animate([{ opacity: 0 }, { opacity: 1 }], {
    duration: WEB_IMAGE_FADE_DURATION_MS,
    easing: 'ease-out',
  });
}

function createImage(
  context: RenderContext,
  source: ImageSource,
  className?: string
): HTMLImageElement | undefined {
  const avatarUri = canonicalNativeListAvatarUri(source.uri);
  const uri = avatarUri ?? safeImageUri(source.uri);
  if (!uri) return undefined;
  const image = context.document.createElement('img');
  const startedAt = webImageNow(image);
  image.style.opacity = '0';
  if (className) image.className = className;
  // OneKey patch: consume recoverable errors before the visual's final fallback listener.
  if (avatarUri) {
    configureWebAvatar(image, source, avatarUri);
  } else {
    configureWebImageRetry(image, source, uri);
    image.src = uri;
  }
  image.alt = '';
  image.draggable = false;
  image.loading = 'lazy';
  image.decoding = 'async';
  image.style.objectFit =
    source.contentFit === 'fill' ? 'fill' : source.contentFit ?? 'cover';
  image.addEventListener('load', () => {
    if (image.dataset.nativeListSelectorPaint !== 'true')
      revealWebImage(image, startedAt);
  });
  image.addEventListener('error', () => {
    image.style.opacity = '0';
  });
  image.dataset.nativeListImageStartedAt = String(startedAt);
  return image;
}

// OneKey patch: React Native Web paints selector images as centered CSS backgrounds.
// The image keeps its loading/error lifecycle; only its replaced-element pixels are hidden.
function paintSelectorImageBackground(
  image: HTMLImageElement,
  frame: HTMLElement,
  inset = 0
) {
  const paint = createElement(
    image.ownerDocument,
    'span',
    'ok-native-list-selector-image-background'
  );
  paint.style.cssText =
    'position:absolute;pointer-events:none;border-radius:inherit;background-position:center;background-repeat:no-repeat';
  paint.style.inset = String(inset) + 'px';
  paint.style.opacity = '0';
  paint.style.backgroundSize =
    image.style.objectFit === 'fill'
      ? '100% 100%'
      : image.style.objectFit === 'center'
      ? 'auto'
      : image.style.objectFit;
  image.dataset.nativeListSelectorPaint = 'true';
  image.style.opacity = '0';
  const update = () => {
    paint.style.backgroundImage =
      'url(' + JSON.stringify(image.currentSrc || image.src) + ')';
    revealWebImage(
      paint,
      Number(image.dataset.nativeListImageStartedAt) || webImageNow(image)
    );
  };
  image.addEventListener('load', update);
  image.addEventListener('error', () => {
    paint.style.backgroundImage = 'none';
    paint.style.opacity = '0';
  });
  frame.insertBefore(paint, image);
  if (image.complete && image.naturalWidth > 0) update();
}

// OneKey patch: use source SVG paths for selector actions and wallet provider marks.
const selectorIcons: Readonly<
  Record<
    string,
    Readonly<{
      viewBox: string;
      paths: readonly Readonly<{
        d: string;
        fill: string;
        fillRule: string;
        opacity: number;
      }>[];
    }>
  >
> = {
  RefreshCcwOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M5 12a7 7 0 0 0 7 7 7.15 7.15 0 0 0 4.955-2h-2.943v-2h6v6h-2v-2.257A9.148 9.148 0 0 1 12 21a9 9 0 0 1-9-9v-1h2v1Zm1-6.754A9.148 9.148 0 0 1 12 3a9 9 0 0 1 9 9v1h-2v-1a7 7 0 0 0-7-7 7.15 7.15 0 0 0-4.955 2H10v2H4V3h2v2.246Z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
    ],
  },
  DotVerSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M14 18v4h-4v-4zm0-4h-4v-4h4zm0-8h-4V2h4z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
    ],
  },
  Document2Outline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M12 16H8v-2h4zm4-4H8v-2h8zm0-4H8V6h8z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
      {
        d: 'M20 22H4V2h16zM6 20h12V4H6z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
  WalletOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M15.5 13.25a1.25 1.25 0 1 1 0 2.5 1.25 1.25 0 0 1 0-2.5',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
      {
        d: 'M17 8h4v13H7a4 4 0 0 1-4-4V6.5A3.5 3.5 0 0 1 6.5 3H17zM5 17a2 2 0 0 0 2 2h12v-9H6.5c-.537 0-1.045-.12-1.5-.337zM6.5 5a1.5 1.5 0 1 0 0 3H15V5z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
  PlusLargeOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M13 11h8v2h-8v8h-2v-8H3v-2h8V3h2z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
    ],
  },
  TicketOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M16 17.01h-2V15h2v2.01Zm0-4h-2V11h2v2.01Zm0-4h-2V7h2v2.01Z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
      {
        d: 'm22 9.927-.629.252c-1.661.665-1.661 2.977 0 3.642l.629.252V20H2v-5.927l.629-.252c1.661-.665 1.661-2.977 0-3.642L2 9.927V4h20v5.927ZM4 8.636c2.5 1.52 2.499 5.207 0 6.727V18h16v-2.637c-2.499-1.52-2.5-5.208 0-6.727V6H4v2.636Z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
  GasSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M15 9h4v7h1V8.414L17.586 6 19 4.586l3 3V18h-5v-7h-2v8h1v2H2v-2h1V3h12zM6 9v2h6V9z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
  CryptoCoinOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M11 16.387a4.5 4.5 0 0 1 0-8.775V6h2v1.613a4.5 4.5 0 0 1 2.856 2.067l-1.712 1.032a2.5 2.5 0 1 0 0 2.576l1.712 1.032A4.5 4.5 0 0 1 13 16.386V18h-2z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
      {
        d: 'M22 12c0 5.523-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2s10 4.477 10 10m-2 0a8 8 0 1 0-16 0 8 8 0 0 0 16 0',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
  GlobusOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M12 2c5.185 0 9.448 3.947 9.95 9H22v2h-.05c-.502 5.053-4.765 9-9.95 9s-9.448-3.947-9.95-9H2v-2h.05C2.552 5.947 6.815 2 12 2M9.523 13c.09 1.982.438 3.726.934 5.002.29.746.612 1.282.917 1.614.304.331.517.384.626.384s.322-.053.626-.384c.305-.332.627-.868.917-1.614.496-1.276.845-3.02.934-5.002zm-5.459 0a8 8 0 0 0 4.8 6.36 10 10 0 0 1-.271-.633C7.994 17.187 7.61 15.189 7.52 13zm12.416 0c-.09 2.189-.474 4.187-1.073 5.727a10 10 0 0 1-.271.633 8 8 0 0 0 4.8-6.36zM8.863 4.639A8 8 0 0 0 4.064 11h3.457c.09-2.189.473-4.187 1.072-5.727q.127-.327.27-.634M12 4c-.109 0-.322.053-.626.384-.305.332-.627.868-.917 1.614-.496 1.276-.844 3.02-.934 5.002h4.954c-.09-1.982-.438-3.726-.934-5.002-.29-.746-.612-1.282-.917-1.614C12.322 4.053 12.109 4 12 4m3.136.639q.144.307.271.634c.599 1.54.982 3.538 1.073 5.727h3.456a8 8 0 0 0-4.8-6.361',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  LockSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M12 2a5 5 0 0 1 5 5v2h3v13H4V9h3V7a5 5 0 0 1 5-5m-1 11v5h2v-5zm1-9a3 3 0 0 0-3 3v2h6V7a3 3 0 0 0-3-3',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  GoogleIllus: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09',
        fill: '#4285F4',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23',
        fill: '#34A853',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22z',
        fill: '#FBBC05',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53',
        fill: '#EA4335',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  AppleBrand: {
    viewBox: '0 0 16 20',
    paths: [
      {
        d: 'M11.67.834c.117 1.074-.315 2.153-.955 2.928-.64.773-1.692 1.378-2.718 1.298-.14-1.054.38-2.151.971-2.836C9.63 1.45 10.746.872 11.67.834M14.994 7.093c-.176.108-1.992 1.224-1.972 3.482.025 2.769 2.428 3.693 2.46 3.705l-.004.015a10.1 10.1 0 0 1-1.264 2.593c-.764 1.116-1.556 2.229-2.806 2.254-.598.011-1-.162-1.416-.343-.437-.19-.891-.386-1.609-.386-.751 0-1.226.203-1.683.398-.397.169-.78.333-1.32.354-1.208.047-2.124-1.207-2.895-2.32C.909 14.57-.294 10.414 1.322 7.612c.803-1.395 2.237-2.275 3.794-2.298.671-.014 1.32.244 1.89.47.434.172.821.326 1.135.326.282 0 .659-.149 1.099-.323.692-.273 1.539-.607 2.41-.518.599.026 2.276.24 3.354 1.818z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  BotIllus: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M11 2a1 1 0 1 1 2 0v1.8l1.6 1.6a1 1 0 1 1-1.4 1.4L12 5.6l-1.2 1.2a1 1 0 0 1-1.4-1.4L11 3.8z',
        fill: '#8897A5',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M8.0 6.0h8.0a5.0 5.0 0 0 1 5.0 5.0v4.0a5.0 5.0 0 0 1 -5.0 5.0h-8.0a5.0 5.0 0 0 1 -5.0 -5.0v-4.0a5.0 5.0 0 0 1 5.0 -5.0z',
        fill: '#3FA9F5',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M3.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z',
        fill: '#8897A5',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M21.0 10.0h0.0a1.5 1.5 0 0 1 1.5 1.5v3.0a1.5 1.5 0 0 1 -1.5 1.5h0.0a1.5 1.5 0 0 1 -1.5 -1.5v-3.0a1.5 1.5 0 0 1 1.5 -1.5z',
        fill: '#8897A5',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M7.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0',
        fill: '#10243E',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M13.5 12.0a1.5 1.5 0 1 0 3.0 0a1.5 1.5 0 1 0 -3.0 0',
        fill: '#10243E',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M8.5 15.4c.9.8 2.08 1.2 3.5 1.2s2.6-.4 3.5-1.2c.24-.2.6-.18.8.06.2.23.17.6-.06.8-1.14.98-2.58 1.46-4.24 1.46s-3.1-.48-4.24-1.46a.58.58 0 0 1-.06-.8c.2-.24.56-.26.8-.06',
        fill: '#10243E',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  AllNetworksSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M15.333 13.998a1.335 1.335 0 1 1 0 2.67 1.335 1.335 0 0 1 0-2.67',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
      {
        d: 'M12 0c6.627 0 12 5.373 12 12s-5.373 12-12 12S0 18.627 0 12 5.373 0 12 0M8 12.668A2 2 0 0 0 6 14.666V16c0 1.103.895 1.997 1.998 1.998h1.334A2 2 0 0 0 11.33 16v-1.334a2 2 0 0 0-1.998-1.998zm7.333 0a2.665 2.665 0 1 0 0 5.33 2.665 2.665 0 0 0 0-5.33M7.999 6.001A2 2 0 0 0 6.001 8v1.334c0 1.103.895 1.998 1.998 1.998h1.334a2 2 0 0 0 1.998-1.998V7.999a2 2 0 0 0-1.998-1.998zm6.667 0A2 2 0 0 0 12.668 8v1.334c0 1.103.895 1.998 1.998 1.998H16a2 2 0 0 0 1.998-1.998V7.999A2 2 0 0 0 16 6.001z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  CrossedSmallSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M17.87 8.25 14.12 12l3.75 3.75-2.12 2.121-3.75-3.75-3.75 3.75-2.121-2.121L9.879 12l-3.75-3.75 2.12-2.121L12 9.879l3.75-3.75 2.122 2.121Z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  AccountErrorCustom: {
    viewBox: '0 0 18 18',
    paths: [
      {
        d: 'M12.5 12.75a1.25 1.25 0 1 0 0-2.5 1.25 1.25 0 0 0 0 2.5',
        fill: '#000',
        fillRule: 'nonzero',
        opacity: 0.447,
      },
      {
        d: 'M0 3.5A3.5 3.5 0 0 1 3.5 0h8.088A2.41 2.41 0 0 1 14 2.412V5h1a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3H4a4 4 0 0 1-4-4zm2 3.163V14a2 2 0 0 0 2 2h11a1 1 0 0 0 1-1V8a1 1 0 0 0-1-1H3.5c-.537 0-1.045-.12-1.5-.337M2 3.5A1.5 1.5 0 0 0 3.5 5H12V2.412A.41.41 0 0 0 11.588 2H3.5A1.5 1.5 0 0 0 2 3.5',
        fill: '#000',
        fillRule: 'evenodd',
        opacity: 0.447,
      },
    ],
  },
  PlusSmallOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M13 11h5v2h-5v5h-2v-5H6v-2h5V6h2z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  DotHorOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M6 14H2v-4h4zm8 0h-4v-4h4zm8 0h-4v-4h4z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  ChevronRightSmallOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M15.414 12 10 17.414 8.586 16l4-4-4-4L10 6.586z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  DragOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M11 21H7v-4h4zm6 0h-4v-4h4zm-6-7H7v-4h4zm6 0h-4v-4h4zm-6-7H7V3h4zm6 0h-4V3h4z',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1.0,
      },
    ],
  },
  PencilOutline: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M22.414 7.5 7.914 22H2v-5.914l14.5-14.5zM4 16.914V20h3.086l9.5-9.5L13.5 7.414zM14.914 6 18 9.086 19.586 7.5 16.5 4.414z',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  CheckboxCheckedCustom: {
    viewBox: '0 0 16 16',
    paths: [
      {
        d: 'M12.204 5.043a1 1 0 0 1 0 1.414l-4.5 4.5a1 1 0 0 1-1.414 0l-2-2a1 1 0 1 1 1.414-1.414l1.293 1.293 3.793-3.793a1 1 0 0 1 1.414 0',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  CheckboxIndeterminateCustom: {
    viewBox: '0 0 16 16',
    paths: [
      {
        d: 'M4 8a1 1 0 0 1 1-1h6a1 1 0 0 1 0 2H5a1 1 0 0 1-1-1',
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1.0,
      },
    ],
  },
  Circle: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: 'M0 12a12 12 0 1 0 24 0a12 12 0 1 0 -24 0',
        fill: 'currentColor',
        fillRule: 'nonzero',
        opacity: 1,
      },
    ],
  },
  BadgeVerifiedSolid: {
    viewBox: '0 0 24 24',
    paths: [
      {
        d: WEB_MARKET_VERIFIED_PATH,
        fill: 'currentColor',
        fillRule: 'evenodd',
        opacity: 1,
      },
    ],
  },
};
function applySelectorIcon(element: HTMLElement, name: string) {
  const icon = selectorIcons[name];
  if (!icon) return;
  element.textContent = '';
  const svg = element.ownerDocument.createElementNS(
    'http://www.w3.org/2000/svg',
    'svg'
  );
  svg.setAttribute('viewBox', icon.viewBox);
  svg.setAttribute('width', '24');
  svg.setAttribute('height', '24');
  svg.setAttribute('aria-hidden', 'true');
  icon.paths.forEach((path) => {
    const child = element.ownerDocument.createElementNS(
      'http://www.w3.org/2000/svg',
      'path'
    );
    child.setAttribute('d', path.d);
    child.setAttribute('fill', path.fill);
    child.setAttribute('fill-rule', path.fillRule);
    child.setAttribute('fill-opacity', String(path.opacity));
    svg.appendChild(child);
  });
  element.appendChild(svg);
}

function iconGlyph(name: string): string {
  const normalized = name.toLocaleLowerCase();
  if (normalized === 'dotversolid') return '⋮';
  if (normalized.includes('chevron')) {
    if (normalized.includes('top')) return '⌃';
    if (normalized.includes('bottom')) return '⌄';
    return '›';
  }
  if (normalized.includes('plus') || normalized.includes('add')) return '+';
  if (normalized.includes('minus')) return '−';
  if (normalized.includes('star')) return '☆';
  if (normalized.includes('drag') || normalized.includes('grabber')) return '≡';
  if (normalized.includes('dot') || normalized.includes('more')) return '•••';
  if (normalized.includes('pencil') || normalized.includes('edit')) return '✎';
  if (normalized.includes('error') || normalized.includes('exclamation'))
    return '!';
  if (normalized.includes('info')) return 'i';
  if (normalized.includes('question')) return '?';
  if (normalized.includes('swap')) return '⇄';
  if (normalized.includes('trend') || normalized.includes('arrow')) return '↗';
  return name.slice(0, 1).toLocaleUpperCase();
}

function createVisual(
  context: RenderContext,
  visual: LeadingVisual | undefined,
  selectorPresentation?: string
): HTMLElement | undefined {
  if (!visual) return undefined;
  if (visual.kind === 'stackedImages') {
    const stack = createElement(
      context.document,
      'span',
      'ok-native-list-stacked'
    );
    visual.images.forEach((source) => {
      const image = createImage(context, source);
      if (image) stack.appendChild(image);
    });
    return stack;
  }

  const frame = createElement(
    context.document,
    'span',
    'ok-native-list-visual'
  );
  const shape = visual.kind === 'icon' ? 'circle' : visual.shape ?? 'circle';
  frame.style.borderRadius =
    shape === 'square' ? '0' : shape === 'rounded' ? '10px' : '50%';
  if (visual.kind === 'icon') {
    frame.style.background = visual.backgroundColor ?? 'var(--nl-strong)';
    const fallback = createElement(
      context.document,
      'span',
      'ok-native-list-visual-fallback',
      iconGlyph(visual.name)
    );
    applySelectorIcon(fallback, visual.name);
    if (visual.tintColor) fallback.style.color = visual.tintColor;
    frame.appendChild(fallback);
    return frame;
  }

  // OneKey patch: NativeList images are usually local and should not flash a
  // placeholder. Callers can opt in with image.loadingStrategy.
  const visualBackground =
    'backgroundColor' in visual && visual.backgroundColor
      ? visual.backgroundColor
      : 'transparent';
  const source = visual.kind === 'image' ? visual.image : visual.image;
  const showsSourcePlaceholder =
    !!source &&
    source.loadingStrategy !== undefined &&
    source.loadingStrategy !== 'none';
  frame.style.background = showsSourcePlaceholder
    ? 'var(--nl-strong)'
    : visualBackground;
  const fallbackIcon =
    'fallbackIcon' in visual ? visual.fallbackIcon : undefined;
  const fallbackBackground =
    fallbackIcon?.backgroundColor ?? 'var(--nl-strong)';
  if (!source && fallbackIcon?.backgroundColor)
    frame.style.background = fallbackBackground;
  const handlesSourceFallback =
    showsSourcePlaceholder &&
    (fallbackIcon !== undefined || 'fallbackText' in visual);
  const image = source ? createImage(context, source) : undefined;
  if (image) {
    image.className = 'ok-native-list-visual-main';
    frame.appendChild(image);
    if (showsSourcePlaceholder) {
      const restoreBackground = () => {
        frame.style.background = visualBackground;
      };
      image.addEventListener('load', restoreBackground);
      if (image.complete && image.naturalWidth > 0) restoreBackground();
    }
    if (selectorPresentation) paintSelectorImageBackground(image, frame);
    if (
      source?.loadingStrategy === 'skeleton' &&
      !(image.complete && image.naturalWidth > 0)
    ) {
      const skeleton = createElement(
        context.document,
        'span',
        'ok-native-list-image-skeleton'
      );
      const background = resolvedTheme(context.snapshot).background;
      const rgb = /^#([0-9a-f]{6})/i.exec(background)?.[1];
      if (rgb) {
        const red = Number.parseInt(rgb.slice(0, 2), 16);
        const green = Number.parseInt(rgb.slice(2, 4), 16);
        const blue = Number.parseInt(rgb.slice(4, 6), 16);
        if (red * 299 + green * 587 + blue * 114 < 128_000)
          skeleton.dataset.dark = 'true';
      }
      frame.appendChild(skeleton);
      image.addEventListener('load', () => skeleton.remove(), { once: true });
      image.addEventListener('error', () => skeleton.remove(), { once: true });
    }
  } else if (!source || showsSourcePlaceholder) {
    frame.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-visual-fallback',
        'fallbackText' in visual ? visual.fallbackText ?? '' : ''
      )
    );
  }
  if (visual.kind === 'token' && visual.networkImage) {
    const corner = createImage(
      context,
      visual.networkImage,
      'ok-native-list-visual-corner'
    );
    if (corner) frame.appendChild(corner);
  } else if ('cornerIcon' in visual && visual.cornerIcon) {
    const corner = createElement(
      context.document,
      'span',
      'ok-native-list-visual-corner ok-native-list-visual-fallback',
      iconGlyph(visual.cornerIcon.name)
    );
    if (visual.cornerIcon.tintColor)
      corner.style.color = visual.cornerIcon.tintColor;
    if (visual.cornerIcon.backgroundColor)
      corner.style.background = visual.cornerIcon.backgroundColor;
    applySelectorIcon(corner, visual.cornerIcon.name);
    frame.appendChild(corner);
  }
  // OneKey patch: image failure uses the same source-derived fallback as v1.
  const hasFallbackText = 'fallbackText' in visual;
  if ((fallbackIcon || hasFallbackText) && (!source || handlesSourceFallback)) {
    const showFallback = () => {
      if (image) {
        disposeWebImageRetries(image);
        image.remove();
      }
      frame
        .querySelector(
          '.ok-native-list-visual-fallback:not(.ok-native-list-visual-corner)'
        )
        ?.remove();
      const fallback = createElement(
        context.document,
        'span',
        'ok-native-list-visual-fallback',
        fallbackIcon
          ? undefined
          : 'fallbackText' in visual
          ? visual.fallbackText ?? ''
          : ''
      );
      if (
        'fallbackTextColor' in visual &&
        visual.fallbackTextColor &&
        !fallbackIcon
      )
        fallback.style.color = visual.fallbackTextColor;
      if (fallbackIcon) {
        applySelectorIcon(fallback, fallbackIcon.name);
        if (fallbackIcon.tintColor)
          fallback.style.color = fallbackIcon.tintColor;
        if (fallbackIcon.size) {
          const svg = fallback.querySelector<SVGElement>('svg');
          if (svg) {
            svg.style.width = `${fallbackIcon.size}px`;
            svg.style.height = `${fallbackIcon.size}px`;
          }
        }
      }
      if (fallbackIcon?.backgroundColor)
        frame.style.background = fallbackBackground;
      else
        fallback.style.backgroundColor =
          'var(--nl-image-fallback-background, transparent)';
      frame.prepend(fallback);
    };
    if (image) image.addEventListener('error', showFallback, { once: true });
    else showFallback();
  }
  if ('borderStyle' in visual && visual.borderStyle === 'dashed') {
    frame.style.border =
      '2px dashed ' + (visual.borderColor ?? 'var(--nl-disabled)');
    frame.style.boxSizing = 'border-box';
  } else if ('borderColor' in visual && visual.borderColor) {
    frame.style.border = '1px solid ' + visual.borderColor;
    frame.style.boxSizing = 'border-box';
  }
  if ('overlays' in visual)
    visual.overlays?.forEach((overlay) => {
      const corner = createElement(
        context.document,
        'span',
        'ok-native-list-visual-overlay',
        overlay.text
      );
      const size = overlay.size ?? 20;
      const isWalletText =
        selectorPresentation === 'walletSidebar' &&
        !!overlay.text &&
        !overlay.image &&
        !overlay.name;
      corner.style.width =
        isWalletText && overlay.width === undefined
          ? 'auto'
          : String(overlay.width ?? size) + 'px';
      corner.style.height =
        String(overlay.height ?? (isWalletText ? 16 : size)) + 'px';
      corner.style.padding = isWalletText
        ? '0 2px'
        : String(overlay.padding ?? 0) + 'px';
      if (isWalletText) {
        corner.style.fontSize = '12px';
        corner.style.lineHeight = '16px';
        applyTextWeight(corner, '400');
      }
      if (overlay.fontWeight)
        applyTextWeight(
          corner,
          {
            regular: 400,
            medium: 500,
            semibold: 600,
            bold: 700,
          }[overlay.fontWeight]
        );
      if (
        isWalletText ||
        overlay.width !== undefined ||
        overlay.height !== undefined
      )
        corner.style.borderRadius = '9999px';
      const offsetX = String(-(overlay.offsetX ?? overlay.offset ?? 2)) + 'px';
      const offsetY = String(-(overlay.offsetY ?? overlay.offset ?? 2)) + 'px';
      corner.style.background = overlay.backgroundColor ?? 'transparent';
      corner.style.color = overlay.tintColor ?? 'var(--nl-secondary)';
      if (overlay.position === 'topLeft') {
        corner.style.left = offsetX;
        corner.style.top = offsetY;
      } else {
        corner.style.right = offsetX;
        corner.style.bottom = offsetY;
      }
      if (overlay.image) {
        const overlayImage = createImage(context, overlay.image);
        if (overlayImage) {
          if (
            (overlay.padding ?? 0) > 0 &&
            (overlay.width ?? size) === (overlay.height ?? size)
          )
            overlayImage.style.borderRadius = '50%';
          corner.appendChild(overlayImage);
          if (overlay.fallbackIcon) {
            overlayImage.addEventListener(
              'error',
              () => {
                disposeWebImageRetries(overlayImage);
                overlayImage.remove();
                const glyph = createElement(
                  context.document,
                  'span',
                  'ok-native-list-overlay-fallback'
                );
                applySelectorIcon(glyph, overlay.fallbackIcon!.name);
                if (overlay.fallbackIcon?.tintColor)
                  glyph.style.color = overlay.fallbackIcon.tintColor;
                corner.appendChild(glyph);
              },
              { once: true }
            );
          }
          if (selectorPresentation)
            paintSelectorImageBackground(
              overlayImage,
              corner,
              overlay.padding ?? 0
            );
        }
      } else if (overlay.name) applySelectorIcon(corner, overlay.name);
      frame.appendChild(corner);
    });
  return frame;
}

function toneColor(
  tone: TextTone | undefined,
  fallback: 'primary' | 'secondary'
): string {
  if (tone === 'positive') return 'var(--nl-positive)';
  if (tone === 'negative') return 'var(--nl-negative)';
  if (tone === 'secondary') return 'var(--nl-secondary)';
  return fallback === 'secondary' ? 'var(--nl-secondary)' : 'var(--nl-primary)';
}

function selectionKeysForTarget(
  target: SelectionTarget | undefined,
  rowKey: string,
  snapshot: NativeListSnapshot
): readonly string[] {
  if (!target || target.scope === 'row') {
    return snapshot.rows.some(
      (row) => row.key === rowKey && isSelectableRow(row)
    )
      ? [rowKey]
      : [];
  }
  if (target.scope === 'section') {
    return snapshot.rows
      .filter(
        (row) => row.sectionKey === target.sectionKey && isSelectableRow(row)
      )
      .map((row) => row.key);
  }
  return snapshot.rows.filter(isSelectableRow).map((row) => row.key);
}

function checkboxState(
  target: SelectionTarget | undefined,
  fallback: CheckboxState,
  rowKey: string,
  snapshot: NativeListSnapshot,
  selectedKeys: ReadonlySet<string>
): CheckboxState {
  if (target?.scope === 'section') {
    return checkboxStateForSection(
      target.sectionKey,
      snapshot.rows,
      selectedKeys
    );
  }
  return checkboxStateForKeys(
    selectionKeysForTarget(target, rowKey, snapshot),
    selectedKeys
  );
}

function createCheckbox(
  context: RenderContext,
  rowKey: string,
  accessory: Extract<TrailingAccessory, { kind: 'checkbox' }>
): HTMLElement {
  if (accessory.loading) {
    const spinner = createElement(
      context.document,
      'span',
      'ok-native-list-spinner'
    );
    spinner.setAttribute('aria-label', 'Loading');
    return spinner;
  }
  const state = checkboxState(
    accessory.target,
    accessory.state,
    rowKey,
    context.snapshot,
    context.selectedKeys
  );
  const element = createElement(
    context.document,
    'button',
    'ok-native-list-checkbox'
  );
  element.setAttribute('type', 'button');
  element.setAttribute('role', 'checkbox');
  element.setAttribute(
    'aria-checked',
    state === 'indeterminate' ? 'mixed' : String(state === 'checked')
  );
  element.setAttribute('aria-label', 'Select');
  element.toggleAttribute('disabled', Boolean(accessory.disabled));
  setData(element, 'state', state);
  setData(element, 'checkboxFallback', accessory.state);
  setData(element, 'nativeListAction', accessory.actionKey ?? 'selection');
  setData(element, 'selectionScope', accessory.target?.scope ?? 'row');
  const row = context.snapshot.rows[context.itemIndex];
  if (row && 'presentation' in row && row.presentation === 'networkSelector') {
    setData(element, 'selector', 'networkSelector');
    for (const [state, name] of [
      ['checked', 'CheckboxCheckedCustom'],
      ['indeterminate', 'CheckboxIndeterminateCustom'],
    ]) {
      const holder = createElement(context.document, 'span');
      applySelectorIcon(holder, name);
      const svg = holder.firstElementChild;
      if (svg) {
        svg.setAttribute('data-state', state);
        element.appendChild(svg);
      }
    }
  }
  if (accessory.target?.scope === 'section')
    setData(element, 'selectionKey', accessory.target.sectionKey);
  else if (accessory.target?.scope === 'row')
    setData(element, 'selectionKey', rowKey);
  return element;
}

function createIconAction(
  context: RenderContext,
  name: string,
  actionKey: string | undefined,
  disabled?: boolean,
  tintColor?: string
): HTMLElement {
  const element = createElement(
    context.document,
    actionKey ? 'button' : 'span',
    'ok-native-list-icon-button',
    iconGlyph(name)
  );
  if (element.tagName === 'BUTTON') {
    element.setAttribute('type', 'button');
    element.toggleAttribute('disabled', Boolean(disabled));
  }
  applySelectorIcon(element, name);
  if (actionKey) setData(element, 'nativeListAction', actionKey);
  if (tintColor) element.style.color = tintColor;
  return element;
}

function markActionAnchorSource(
  element: HTMLElement,
  source: NativeListActionSource,
  slot?: number
) {
  setData(element, 'nativeListAnchorSource', source);
  if (slot !== undefined) setData(element, 'nativeListAnchorSlot', slot);
}

// OneKey patch: the compact zero-count digits share the amount baseline.

function createAccessory(
  context: RenderContext,
  rowKey: string,
  accessory: TrailingAccessory,
  slot: number,
  rowDisabled: boolean
): HTMLElement {
  if (accessory.kind === 'button') {
    const element = createElement(
      context.document,
      'button',
      'ok-native-list-trailing-button',
      accessory.label
    ) as HTMLButtonElement;
    element.type = 'button';
    element.disabled = Boolean(rowDisabled || accessory.disabled);
    element.setAttribute('aria-disabled', String(element.disabled));
    element.setAttribute(
      'aria-label',
      accessory.accessibilityLabel ?? accessory.label
    );
    setData(element, 'testid', accessory.testID);
    setData(element, 'nativeListAction', accessory.actionKey);
    markActionAnchorSource(element, 'trailingAccessory', slot);
    if (accessory.textColor)
      element.style.setProperty('--nl-button-text', accessory.textColor);
    if (accessory.backgroundColor)
      element.style.setProperty('--nl-button-bg', accessory.backgroundColor);
    if (accessory.pressedBackgroundColor)
      element.style.setProperty(
        '--nl-button-pressed',
        accessory.pressedBackgroundColor
      );
    if (accessory.hoverBackgroundColor)
      element.style.setProperty(
        '--nl-button-hover',
        accessory.hoverBackgroundColor
      );
    if (accessory.focusRingColor)
      element.style.setProperty('--nl-button-focus', accessory.focusRingColor);
    return element;
  }
  if (accessory.kind === 'checkbox') {
    const element = createCheckbox(context, rowKey, accessory);
    markActionAnchorSource(element, 'trailingAccessory', slot);
    return element;
  }
  if (accessory.kind === 'icon') {
    const element = createIconAction(
      context,
      accessory.name,
      accessory.actionKey,
      accessory.disabled,
      accessory.tintColor
    );
    markActionAnchorSource(element, 'trailingAccessory', slot);
    if (accessory.name === 'RefreshCcwOutline' && !accessory.actionKey) {
      element.classList.add('ok-native-list-decorative-refresh');
      const glyph = element.querySelector('svg');
      glyph?.setAttribute('width', '16');
      glyph?.setAttribute('height', '16');
    }
    if (accessory.buttonStyle === 'tertiary') {
      element.classList.add('ok-native-list-tertiary-icon');
      setData(element, 'nativeListAnchorInset', 7);
    }
    setData(element, 'testid', accessory.testID);
    if (accessory.hoverActionKey)
      setData(element, 'nativeListHoverAction', accessory.hoverActionKey);
    if (accessory.hoverActionKey && accessory.buttonStyle === 'tertiary')
      setData(element, 'nativeListIconHelp', true);
    if (accessory.accessibilityLabel)
      element.setAttribute('aria-label', accessory.accessibilityLabel);
    const row = context.snapshot.rows[context.itemIndex];
    if (
      row &&
      'presentation' in row &&
      row.presentation === 'accountSelector'
    ) {
      setData(element, 'nativeListAnchorInset', 7);
      // OneKey patch: the create-address button omits IconButton's one-point border.
      if (hasExplicitRowHeight(row) && accessory.name === 'PlusSmallOutline') {
        setData(element, 'nativeListAccountControl', 'createAddress');
        if (!accessory.tintColor)
          element.style.color = 'var(--nl-icon-subdued)';
      }
    }
    return element;
  }
  if (accessory.kind === 'spinner') {
    return createElement(context.document, 'span', 'ok-native-list-spinner');
  }
  const actionKey = 'actionKey' in accessory ? accessory.actionKey : undefined;
  const element = createElement(
    context.document,
    actionKey ? 'button' : 'span',
    'ok-native-list-accessory'
  );
  if (element.tagName === 'BUTTON') {
    element.setAttribute('type', 'button');
    element.classList.add('ok-native-list-icon-button');
    setData(element, 'nativeListAction', actionKey);
  }
  switch (accessory.kind) {
    case 'value':
      element.textContent = accessory.text;
      applyValueSegments(element, accessory.textSegments);
      if (accessory.secondary)
        element.classList.add('ok-native-list-accessory-secondary');
      break;
    case 'valuePair': {
      const primary = createElement(
        context.document,
        'span',
        undefined,
        accessory.primary
      );
      primary.style.color = toneColor(accessory.primaryTone, 'primary');
      applyValueSegments(primary, accessory.primaryTextSegments, 16, 20, 500);
      if (accessory.primaryFontWeight === 'medium')
        applyTextWeight(primary, 500);
      if (accessory.primaryHelp) {
        const help = accessory.primaryHelp;
        primary.tabIndex = 0;
        primary.setAttribute('role', 'button');
        if (help.presentation === 'tooltip') {
          setData(primary, 'nativeListHoverAction', help.actionKey);
          setData(primary, 'nativeListIconHelp', true);
        } else {
          setData(primary, 'nativeListAction', help.actionKey);
        }
        if (help.underline === 'dashed') {
          primary.style.textDecoration = 'underline dashed';
          primary.style.textUnderlineOffset = '4px';
        }
        markActionAnchorSource(primary, 'trailingAccessory', slot);
      }
      const secondary = createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        accessory.secondary
      );
      secondary.style.color = toneColor(accessory.secondaryTone, 'secondary');
      applyValueSegments(
        secondary,
        accessory.secondaryTextSegments,
        14,
        20,
        400
      );
      element.replaceChildren(primary, '\n', secondary);
      element.classList.add('ok-native-list-amounts');
      break;
    }
    case 'radio':
      element.textContent = accessory.checked ? '●' : '○';
      break;
    case 'switch':
      element.textContent = accessory.value ? 'ON' : 'OFF';
      setData(element, 'nativeListAction', accessory.actionKey);
      break;
    case 'chevron':
      element.textContent = '›';
      break;
    case 'menu':
      element.textContent = '⋮';
      break;
    case 'drag':
      element.textContent = '≡';
      break;
    case 'progress':
      element.textContent = String(Math.round(accessory.value * 100)) + '%';
      break;
  }
  if (actionKey) markActionAnchorSource(element, 'trailingAccessory', slot);
  return element;
}

function rendererPrimitives(context: RenderContext, rowDisabled: boolean) {
  return {
    background: resolvedTheme(context.snapshot).background,
    accessory: (key: string, descriptor: TrailingAccessory, slot: number) =>
      createAccessory(context, key, descriptor, slot, rowDisabled),
    visual: (source: LeadingVisual | undefined, presentation?: string) =>
      createVisual(context, source, presentation),
    thumbnail: (source: ImageSource) =>
      createImage(context, source, 'ok-native-list-thumbnail'),
    textStyle: applyTextStyleToSlot,
    icon: applySelectorIcon,
    iconAction: (
      name: string,
      actionKey: string | undefined,
      disabled?: boolean,
      tintColor?: string
    ) => createIconAction(context, name, actionKey, disabled, tintColor),
    dispose: disposeWebImageRetries,
  };
}

export function createRowBody(
  context: RenderContext,
  row: RowModel
): HTMLElement {
  return rowRenderer(row).create(
    context.document,
    rendererPrimitives(context, Boolean(row.disabled))
  );
}

export type NativeListWebScrollOptions = Readonly<{
  scrollHost?: HTMLElement;
  contentBounds?: HTMLElement;
  stickyInset?: number;
  ownsRefresh?: boolean;
}>;

export class NativeListWebEngine {
  private readonly externalScrollHost: HTMLElement | undefined;
  private readonly ownsRefresh: boolean;
  private readonly pullTarget: HTMLElement;
  private readonly contentBounds: HTMLElement | undefined;
  private externalStickyInset: number;
  private externalOrigin: number | undefined;
  private externalGeometryCache:
    | ReturnType<NativeListWebEngine['readExternalGeometry']>
    | undefined;
  private readonly document: Document;
  private readonly root: HTMLElement;
  private readonly viewportFrame: HTMLElement;
  private readonly viewport: HTMLElement;
  private readonly content: HTMLElement;
  private readonly footer: HTMLElement;
  private readonly sticky: HTMLElement;
  private readonly indexRail: HTMLElement;
  private readonly refreshIndicator: HTMLElement;
  private readonly reorderPreview: HTMLElement;
  private readonly previousHostPosition: string;
  private readonly sectionIndexContainer: HTMLElement | undefined;
  private snapshot: NativeListSnapshot;
  private rows: readonly RowModel[] = [];
  private selectedKeys: ReadonlySet<string> = new Set();
  private callbacks: NativeListWebCallbacks;
  private layout: WebListLayout = {
    items: [],
    contentWidth: 0,
    contentHeight: 0,
    horizontal: false,
  };
  private readonly mounted = new Map<number, HTMLElement>();
  private readonly pool: Record<RowRendererKey, HTMLElement[]> = {
    walletGroup: [],
    message: [],
    rail: [],
    mediaTile: [],
    action: [],
    buttonGroup: [],
    system: [],
    activity: [],
    dataRow: [],
    metricCard: [],
    market: [],
    marketTable: [],
    identity: [],
    sectionHeader: [],
  };
  private readonly rendererKeys = new WeakMap<HTMLElement, RowRendererKey>();
  private frameHandle: number | undefined;
  private resizeObserver: ResizeObserver | undefined;
  private sectionIndexTransformObserver: MutationObserver | undefined;
  private sectionIndexTransformFrame: number | undefined;
  private sectionIndexTransformTrackingFrames = 0;
  private sectionIndexTransformSettledFrames = 0;
  private pendingScroll: PendingScroll | undefined;
  private lastVisibleSignature: string | undefined;
  // OneKey patch: URI leases outlive DOM overscan only inside this bounded window.
  private readonly avatarLeases = new Map<string, AvatarLease>();
  private avatarOffset = 0;
  private avatarDirection = 1;
  private reachedGeneration: number | undefined;
  private stickyKey: string | undefined;
  private sectionIndexEntries: readonly Readonly<{
    key: string;
    title: string;
    position: number;
  }>[] = [];
  private pointerReorder: PointerReorderState | undefined;
  private keyboardReorder: KeyboardReorderState | undefined;
  private reorderMoveFrame: number | undefined;
  private reorderAutoScrollFrame: number | undefined;
  private reorderDropTimer: number | undefined;
  private reorderMovementTimer: number | undefined;
  private reorderSettlingKey: string | undefined;
  private reorderCompactKey: string | undefined;
  private reorderExpandingKey: string | undefined;
  private suppressClickUntil = 0;
  private pullStartY: number | undefined;
  private pullDistance = 0;
  private virtualizationEnabled: boolean;
  private actionAnchor: WebActionAnchorRecord | undefined;
  private pressedHelpElement: HTMLElement | undefined;
  private readonly actionAnchorInstanceId = String(
    ++webNativeListInstanceCounter
  );
  private actionAnchorCounter = 0;
  private bindingEpochCounter = 0;
  private marketPointer:
    | Readonly<{
        pointerId: number;
        pointerType: string;
        compact: boolean;
        rowKey: string;
        startX: number;
        startY: number;
        timer: number;
      }>
    | undefined;
  private marketLongPressFired = false;
  private lastViewportWidth = -1;
  private lastViewportHeight = -1;
  private destroyed = false;
  private hasAppliedSnapshot = false;
  private lastStructuralTransitionId: string | undefined;
  private structuralFrame: number | undefined;
  private structuralFinish: number | undefined;
  private structuralRevision = 0;
  private structuralAnimations: Animation[] = [];
  private structuralScrollInterrupted = false;
  private structuralRenderedHeight: number | undefined;
  private structuralTargetHeight: number | undefined;
  private reactFooterHeight = 0;
  private structuralFooterCompensation = 0;
  private structuralFooterExpansion = 0;
  private structuralFooterFromHeight = 0;
  private structuralFooterElement: HTMLElement | undefined;
  private structuralFooterStyles:
    | { marginBottom: string; transform: string; clipPath: string }
    | undefined;
  private structuralProgress = 0;
  private structuralAnchorStyles:
    | { owner: string; content: string }
    | undefined;
  private structuralContentOverflow: string | undefined;
  private structuralPrefixTransition = false;
  private structuralTail:
    | {
        snapshot: NativeListSnapshot;
        rows: readonly RowModel[];
        layout: WebListLayout;
        firstIndex: number;
        layer: HTMLElement;
        mounted: Map<number, HTMLElement>;
      }
    | undefined;
  private sectionIndexAlignStart = false;
  // Renderers report intrinsic height after browser text wrapping.
  private measuredRendererHeights = new Map<
    string,
    { signature: string; height: number }
  >();

  private notifyScrollCommandAccepted() {
    if (this.destroyed) return;
    const owner = this.externalScrollHost ?? this.viewport;
    const EventConstructor = owner.ownerDocument.defaultView?.Event;
    if (EventConstructor)
      owner.dispatchEvent(
        new EventConstructor('ok-native-list-scroll-command-accepted')
      );
  }

  private readonly handlePagerScrollCommandCancel = (event: Event) => {
    const owner = this.externalScrollHost ?? this.viewport;
    if (this.destroyed || event.target !== owner) return;
    this.pendingScroll = undefined;
    const options: ScrollToOptions = {
      left: owner.scrollLeft,
      top: owner.scrollTop,
      behavior: 'instant',
    };
    if (this.externalScrollHost) {
      const scrollTo: (options: ScrollToOptions) => void =
        owner.ownerDocument.defaultView?.Element.prototype.scrollTo ??
        owner.scrollTo;
      scrollTo.call(owner, options);
    } else this.viewport.scrollTo(options);
  };

  constructor(
    host: HTMLElement,
    snapshot: NativeListSnapshot,
    callbacks: NativeListWebCallbacks,
    virtualizationEnabled = true,
    sectionIndexContainer?: HTMLElement | null,
    fontFamiliesJson = '{}',
    scrollOptions: NativeListWebScrollOptions = {}
  ) {
    if (
      scrollOptions.scrollHost &&
      (!(
        scrollOptions.scrollHost instanceof
        host.ownerDocument.defaultView!.HTMLElement
      ) ||
        !scrollOptions.scrollHost.contains(host))
    )
      throw new Error(
        'webScrollHost must be an ancestor HTMLElement in the same document'
      );
    this.ownsRefresh = scrollOptions.ownsRefresh === true;
    if (
      scrollOptions.scrollHost &&
      snapshot.layout.orientation === 'horizontal'
    )
      throw new Error('External scrolling supports vertical lists only');
    this.externalScrollHost = scrollOptions.scrollHost;
    this.contentBounds = scrollOptions.contentBounds;
    this.externalStickyInset = this.validateStickyInset(
      scrollOptions.stickyInset
    );
    this.document = host.ownerDocument;
    this.snapshot =
      containNativeListInput(() => validateSnapshot(snapshot)) ??
      emptyNativeListSnapshot();
    this.callbacks = callbacks;
    this.virtualizationEnabled = virtualizationEnabled;
    this.sectionIndexContainer = sectionIndexContainer ?? undefined;
    this.previousHostPosition = host.style.position;
    if (!host.style.position) host.style.position = 'relative';

    this.root = createElement(this.document, 'div', 'ok-native-list-root');
    setData(this.root, 'nativeListParentScroll', !!this.externalScrollHost);
    const style = this.document.createElement('style');
    style.textContent = WEB_LIST_CSS;
    this.viewportFrame = createElement(
      this.document,
      'div',
      'ok-native-list-viewport-frame'
    );
    this.viewport = createElement(
      this.document,
      'div',
      'ok-native-list-viewport'
    );
    this.pullTarget =
      this.externalScrollHost && this.ownsRefresh
        ? this.externalScrollHost
        : this.viewport;
    this.viewport.setAttribute('role', 'list');
    this.content = createElement(
      this.document,
      'div',
      'ok-native-list-content'
    );
    this.footer = createElement(this.document, 'div', 'ok-native-list-footer');
    this.sticky = createElement(
      this.document,
      'div',
      'ok-native-list-item ok-native-list-sticky'
    );
    this.sticky.setAttribute('aria-hidden', 'true');
    this.sticky.hidden = true;
    this.indexRail = createElement(
      this.document,
      'div',
      'ok-native-list-index-rail'
    );
    this.indexRail.setAttribute('role', 'navigation');
    this.indexRail.setAttribute('aria-label', 'Section index');
    this.indexRail.hidden = true;
    this.refreshIndicator = createElement(
      this.document,
      'div',
      'ok-native-list-refresh',
      'Refreshing'
    );
    this.refreshIndicator.setAttribute('role', 'status');
    this.reorderPreview = createElement(
      this.document,
      'div',
      'ok-native-list-reorder-preview'
    );
    this.reorderPreview.hidden = true;
    this.reorderPreview.setAttribute('aria-hidden', 'true');
    this.document.body.appendChild(this.reorderPreview);
    this.viewport.append(this.content);
    this.viewportFrame.append(
      this.viewport,
      this.sticky,
      this.indexRail,
      this.refreshIndicator
    );
    this.root.append(style, this.viewportFrame, this.footer);
    host.appendChild(this.root);
    if (this.externalScrollHost) {
      Object.assign(this.root.style, {
        position: 'relative',
        inset: 'auto',
        overflow: 'visible',
        flex: 'none',
      });
      Object.assign(this.viewportFrame.style, {
        overflow: 'visible',
        flex: 'none',
      });
      Object.assign(this.viewport.style, {
        position: 'relative',
        inset: 'auto',
        overflow: 'visible',
        scrollbarGutter: 'auto',
      });
      this.content.style.minHeight = '0';
      this.externalScrollHost.addEventListener(
        'scroll',
        this.handleExternalScroll,
        {
          passive: true,
          capture: true,
        }
      );
    }

    (this.externalScrollHost ?? this.viewport).addEventListener(
      'ok-collapsible-pager-cancel-scroll-command',
      this.handlePagerScrollCommandCancel
    );
    for (const event of ['wheel', 'pointerdown', 'touchstart'])
      (this.externalScrollHost ?? this.viewport).addEventListener(
        event,
        this.handleStructuralUserScroll,
        { passive: true }
      );
    this.viewport.addEventListener('scroll', this.handleScroll, {
      passive: true,
    });
    (this.externalScrollHost ?? this.viewport).addEventListener(
      'ok-collapsible-pager-insets-changed',
      this.handleCollapsiblePagerInsetsChanged
    );
    this.root.addEventListener('click', this.handleClick);
    this.root.addEventListener('contextmenu', this.handleMarketContextMenu);
    this.root.addEventListener('pointerdown', this.handleMarketPointerDown);
    this.root.addEventListener('pointermove', this.handleMarketPointerMove);
    this.root.addEventListener('touchmove', this.handleMarketTouchMove, {
      passive: true,
    });
    this.root.addEventListener('pointerup', this.handleMarketPointerEnd);
    this.root.addEventListener('pointercancel', this.handleMarketPointerEnd);
    // OneKey patch: preserve web tooltip hover for section titles.
    this.root.addEventListener('pointerover', this.handleTitlePointerOver);
    this.root.addEventListener('pointerdown', this.handleIconHelpPointerDown);
    this.root.addEventListener('focusin', this.handleTitlePointerOver);
    this.root.addEventListener('focusout', this.handleTitlePointerOut);
    this.root.addEventListener('pointerout', this.handleTitlePointerOut);
    this.root.addEventListener('keydown', this.handleKeyDown);
    this.viewport.addEventListener(
      'pointerdown',
      this.handleReorderPointerDown,
      { passive: false }
    );
    this.viewport.addEventListener(
      'pointermove',
      this.handleReorderPointerMove,
      { passive: false }
    );
    this.viewport.addEventListener('pointerup', this.handleReorderPointerEnd);
    this.viewport.addEventListener(
      'pointercancel',
      this.handleReorderPointerCancel
    );
    this.viewport.addEventListener('touchmove', this.handleReorderTouchMove, {
      passive: false,
    });
    this.viewport.addEventListener('touchend', this.handleReorderTouchEnd, {
      passive: false,
    });
    this.viewport.addEventListener(
      'touchcancel',
      this.handleReorderTouchCancel
    );
    this.indexRail.addEventListener('pointerdown', this.handleIndexPointer);
    this.indexRail.addEventListener('pointermove', this.handleIndexPointer);
    this.indexRail.addEventListener('pointerup', this.handleIndexPointerEnd);
    this.indexRail.addEventListener(
      'pointercancel',
      this.handleIndexPointerEnd
    );
    this.indexRail.addEventListener('click', this.handleIndexClick);
    this.pullTarget.addEventListener('pointerdown', this.handlePullStart, {
      passive: true,
    });
    this.pullTarget.addEventListener('pointermove', this.handlePullMove, {
      passive: false,
    });
    this.pullTarget.addEventListener('pointerup', this.handlePullEnd);
    this.pullTarget.addEventListener('pointercancel', this.handlePullEnd);

    const ResizeObserverConstructor = this.document.defaultView?.ResizeObserver;
    if (ResizeObserverConstructor) {
      this.resizeObserver = new ResizeObserverConstructor(() =>
        this.recomputeLayout()
      );
      this.resizeObserver.observe(this.viewport);
      if (this.externalScrollHost) {
        this.resizeObserver.observe(this.externalScrollHost);
        let ancestor: HTMLElement | null = host;
        while (ancestor && ancestor !== this.externalScrollHost) {
          this.resizeObserver.observe(ancestor);
          for (const sibling of ancestor.parentElement?.children ?? []) {
            if (sibling instanceof this.document.defaultView!.HTMLElement)
              this.resizeObserver.observe(sibling);
          }
          ancestor = ancestor.parentElement;
        }
      }
    } else {
      this.document.defaultView?.addEventListener(
        'resize',
        this.handleWindowResize
      );
    }
    this.document.fonts?.addEventListener(
      'loadingdone',
      this.handleWindowResize
    );
    this.observeSectionIndexTransforms();
    this.applyFontFamilies(fontFamiliesJson);
    this.applyValidatedSnapshot(this.snapshot);
  }

  private validateStickyInset(value = 0): number {
    if (!Number.isFinite(value) || value < 0)
      throw new Error('webStickyInset must be finite and non-negative');
    return value;
  }

  setStickyInset(value = 0) {
    const next = this.validateStickyInset(value);
    if (next === this.externalStickyInset) return;
    this.externalStickyInset = next;
    this.recomputeLayout();
  }

  private externalGeometry() {
    return (this.externalGeometryCache ??= this.readExternalGeometry());
  }

  private readExternalGeometry() {
    const host = this.externalScrollHost!;
    const hostRect = host.getBoundingClientRect();
    const bodyRect = this.viewport.getBoundingClientRect();
    const boundsRect = (
      this.contentBounds ?? this.root
    ).getBoundingClientRect();
    const top = hostRect.top + host.clientTop + this.externalStickyInset;
    const bottom = hostRect.top + host.clientTop + host.clientHeight;
    return {
      origin: bodyRect.top - hostRect.top - host.clientTop + host.scrollTop,
      containerOrigin:
        boundsRect.top - hostRect.top - host.clientTop + host.scrollTop,
      end: boundsRect.bottom - hostRect.top - host.clientTop + host.scrollTop,
      top,
      bottom,
      offset: top - bodyRect.top,
      viewportLength: Math.max(0, bottom - top),
      visible:
        bodyRect.width > 0 &&
        bodyRect.right > hostRect.left &&
        bodyRect.left < hostRect.right &&
        host.clientHeight > 0 &&
        boundsRect.bottom > top &&
        boundsRect.top < bottom,
    };
  }

  private fontFamiliesJson = '{}';

  private applyFontFamilies(json: string) {
    const families = JSON.parse(json) as Record<string, string | undefined>;
    const fallback = '-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif';
    for (const face of ['regular', 'medium', 'semibold', 'bold']) {
      const name = families[face];
      const family =
        name === undefined ? fallback : `${JSON.stringify(name)},${fallback}`;
      for (const element of [this.root, this.indexRail, this.reorderPreview]) {
        element.style.setProperty(`--nl-font-${face}`, family);
      }
    }
    this.fontFamiliesJson = json;
  }

  setFontFamiliesJson(json: string) {
    if (json === this.fontFamiliesJson || this.destroyed) return;
    this.invalidateActionAnchor('layout');
    this.cancelPointerReorder(true);
    this.cancelKeyboardReorder();
    this.applyFontFamilies(json);
    this.measuredRendererHeights.clear();
    this.recomputeLayout();
  }

  updateCallbacks(callbacks: NativeListWebCallbacks) {
    this.callbacks = callbacks;
  }

  setVirtualizationEnabled(enabled: boolean) {
    if (this.virtualizationEnabled === enabled) return;
    this.virtualizationEnabled = enabled;
    this.renderWindow();
  }

  applySnapshot(snapshot: NativeListSnapshot) {
    const validated = containNativeListInput(() => {
      const next = validateSnapshot(snapshot);
      if (this.externalScrollHost && next.layout.orientation === 'horizontal')
        throw new Error('External scrolling supports vertical lists only');
      return next;
    });
    if (!validated) return;
    this.applyValidatedSnapshot(validated);
  }

  private applyValidatedSnapshot(validated: NativeListSnapshot) {
    this.pressedHelpElement = undefined;
    this.invalidateActionAnchor('snapshot');
    const transition = validated.structuralTransition;
    const isNewTransition =
      transition !== undefined &&
      transition.id !== this.lastStructuralTransitionId;
    if (transition) this.lastStructuralTransitionId = transition.id;
    const nextRows = effectiveRows(validated);
    const changedKeys =
      this.rows.length !== nextRows.length ||
      this.rows.some((row, index) => row.key !== nextRows[index]?.key);
    const incompatibleActivePrefix =
      this.structuralPrefixTransition &&
      (validated.layout.kind !== this.snapshot.layout.kind ||
        validated.layout.orientation !== this.snapshot.layout.orientation ||
        nextRows.some(
          (row, index) =>
            row.height !== this.rows[index]?.height ||
            row.style?.container?.height !==
              this.rows[index]?.style?.container?.height
        ));
    const shouldAnimate =
      this.hasAppliedSnapshot &&
      isNewTransition &&
      changedKeys &&
      (this.snapshot.layout.kind === 'linear' ||
        this.snapshot.layout.kind === 'table') &&
      this.snapshot.layout.orientation !== 'horizontal' &&
      (validated.layout.kind === 'linear' ||
        validated.layout.kind === 'table') &&
      validated.layout.orientation !== 'horizontal' &&
      (transition?.durationMs ?? 220) > 0 &&
      !this.document.defaultView?.matchMedia?.(
        '(prefers-reduced-motion: reduce)'
      ).matches;
    if (shouldAnimate && transition)
      this.applyStructuralSnapshot(validated, transition);
    else {
      if (isNewTransition || changedKeys || incompatibleActivePrefix)
        this.cancelStructuralAnimation();
      this.setSnapshot(validated);
    }
    this.hasAppliedSnapshot = true;
  }

  private cancelStructuralAnimation() {
    this.structuralRevision += 1;
    if (this.structuralFrame !== undefined)
      this.document.defaultView?.cancelAnimationFrame(this.structuralFrame);
    if (this.structuralFinish !== undefined)
      this.document.defaultView?.clearTimeout(this.structuralFinish);
    this.structuralFrame = undefined;
    this.structuralFinish = undefined;
    this.structuralAnimations.forEach((animation) => animation.cancel());
    this.structuralAnimations = [];
    if (this.structuralTail) {
      disposeWebImageRetries(this.structuralTail.layer);
      this.structuralTail.layer.remove();
      this.structuralTail = undefined;
    }
    if (this.structuralContentOverflow !== undefined) {
      this.content.style.overflow = this.structuralContentOverflow;
      this.structuralContentOverflow = undefined;
    }
    this.structuralPrefixTransition = false;
    if (this.structuralFooterElement && this.structuralFooterStyles) {
      this.structuralFooterElement.style.marginBottom =
        this.structuralFooterStyles.marginBottom;
      this.structuralFooterElement.style.transform =
        this.structuralFooterStyles.transform;
      this.structuralFooterElement.style.clipPath =
        this.structuralFooterStyles.clipPath;
    }
    this.structuralFooterElement = undefined;
    this.structuralFooterStyles = undefined;
    this.structuralScrollInterrupted = false;
    this.structuralRenderedHeight = undefined;
    this.structuralTargetHeight = undefined;
    this.structuralFooterCompensation = 0;
    this.structuralFooterExpansion = 0;
    this.structuralProgress = 0;
    const owner = this.externalScrollHost ?? this.viewport;
    if (this.structuralAnchorStyles) {
      owner.style.overflowAnchor = this.structuralAnchorStyles.owner;
      this.content.style.overflowAnchor = this.structuralAnchorStyles.content;
      this.structuralAnchorStyles = undefined;
    }
  }

  private handleStructuralUserScroll = (event: Event) => {
    if (
      event.type === 'pointerdown' &&
      event.target !== (this.externalScrollHost ?? this.viewport) &&
      (event as PointerEvent).pointerType !== 'touch'
    )
      return;
    this.structuralScrollInterrupted = true;
    if (this.structuralPrefixTransition) {
      this.cancelStructuralAnimation();
      this.recomputeLayout();
    }
  };

  private updateStructuralTailWindow() {
    const tail = this.structuralTail;
    if (!tail) return;
    const visible = visibleWebLayoutItems(
      tail.layout,
      this.currentOffset(),
      this.viewportLength(),
      this.viewportLength() * OVERSCAN_VIEWPORTS
    ).filter((item) => item.index >= tail.firstIndex);
    const desired = new Set(visible.map((item) => item.index));
    tail.mounted.forEach((element, index) => {
      if (desired.has(index)) return;
      disposeWebImageRetries(element);
      element.remove();
      tail.mounted.delete(index);
    });
    visible.forEach((item) => {
      if (tail.mounted.has(item.index)) return;
      const row = tail.rows[item.index];
      if (!row) return;
      const element = createElement(
        this.document,
        'div',
        'ok-native-list-item'
      );
      element.setAttribute('aria-hidden', 'true');
      element.style.pointerEvents = 'none';
      element.style.opacity = String(
        (row.style?.container?.opacity ?? row.opacity ?? 1) *
          (row.disabled ? 0.5 : 1)
      );
      const body = createRowBody(
        {
          document: this.document,
          snapshot: tail.snapshot,
          selectedKeys: selectionStateFromSnapshot(tail.snapshot).selectedKeys,
          itemIndex: item.index,
        },
        row
      );
      applyRowStyle(body, row);
      element.append(body);
      this.positionElement(element, item);
      tail.layer.append(element);
      tail.mounted.set(item.index, element);
    });
  }

  private writeStructuralHeight(height: number) {
    if (this.externalScrollHost) this.writeExternalHeight(height);
    else this.content.style.height = `${height}px`;
  }

  private reactFooterElement() {
    return this.contentBounds?.querySelector<HTMLElement>(
      '[data-testid="native-list-footer-slot"]'
    );
  }

  private readReactFooterHeight() {
    return this.reactFooterElement()?.getBoundingClientRect().height ?? 0;
  }

  private applyStructuralFooterVisual(progress: number) {
    const footer = this.structuralFooterElement;
    if (!footer || this.structuralFooterExpansion <= 0) return;
    const hidden = this.structuralFooterExpansion * (1 - progress);
    footer.style.marginBottom = `${-hidden}px`;
    footer.style.transform = `translateY(${-hidden}px)`;
    footer.style.clipPath = `inset(${hidden}px 0 0 0)`;
  }

  private applyStructuralSnapshot(
    next: NativeListSnapshot,
    transition: NonNullable<NativeListSnapshot['structuralTransition']>
  ) {
    const owner = this.externalScrollHost ?? this.viewport;
    const oldOffset = owner.scrollTop;
    const oldScrollHeight = owner.scrollHeight;
    const oldHeight =
      this.structuralRenderedHeight ??
      (this.content.getBoundingClientRect().height ||
        this.layout.contentHeight);
    const nextFooterHeight = this.readReactFooterHeight();
    const oldFooterHeight =
      this.structuralRenderedHeight === undefined
        ? this.reactFooterHeight
        : this.structuralFooterFromHeight +
          (this.reactFooterHeight - this.structuralFooterFromHeight) *
            this.structuralProgress;
    const footerDifference = oldFooterHeight - nextFooterHeight;
    const startHeight = oldHeight + Math.max(0, footerDifference);
    const oldSnapshot = this.snapshot;
    const oldRows = this.rows;
    const oldLayout = this.layout;
    const oldItems = oldLayout.items;
    const nextRows = effectiveRows(next);
    const prefixCount = Math.min(oldRows.length, nextRows.length);
    const prefixTailChange =
      oldSnapshot.layout.kind === next.layout.kind &&
      oldSnapshot.layout.orientation === next.layout.orientation &&
      oldRows.length !== nextRows.length &&
      oldRows
        .slice(0, prefixCount)
        .every((row, index) => row.key === nextRows[index]?.key);
    const oldPositions = new Map<string, number>();
    this.mounted.forEach((element, index) => {
      const row = this.rows[index];
      if (row) oldPositions.set(row.key, element.getBoundingClientRect().top);
    });
    const nextKeys = new Set(effectiveRows(next).map((row) => row.key));
    const visibleOffset = this.currentOffset();
    const visibleSurvivor = oldItems.find(
      (item) => item.y + item.height > visibleOffset && nextKeys.has(item.key)
    );
    const anchor = visibleSurvivor;
    this.cancelStructuralAnimation();
    this.structuralScrollInterrupted = false;
    this.structuralAnchorStyles = {
      owner: owner.style.overflowAnchor,
      content: this.content.style.overflowAnchor,
    };
    owner.style.overflowAnchor = 'none';
    this.content.style.overflowAnchor = 'none';
    this.structuralContentOverflow = this.content.style.overflow;
    if (prefixTailChange) this.content.style.overflow = 'clip';
    this.structuralRenderedHeight = startHeight;
    this.structuralFooterFromHeight = oldFooterHeight;
    this.structuralFooterCompensation = Math.max(0, footerDifference);
    this.structuralFooterExpansion = Math.max(0, -footerDifference);
    this.structuralProgress = 0;
    this.reactFooterHeight = nextFooterHeight;
    this.structuralFooterElement = this.reactFooterElement() ?? undefined;
    if (this.structuralFooterElement) {
      const style = this.structuralFooterElement.style;
      this.structuralFooterStyles = {
        marginBottom: style.marginBottom,
        transform: style.transform,
        clipPath: style.clipPath,
      };
      this.applyStructuralFooterVisual(0);
    }
    this.setSnapshot(next);
    const newHeight = this.layout.contentHeight;
    this.structuralTargetHeight = newHeight;
    const stablePrefixGeometry =
      prefixTailChange &&
      oldItems.length === oldRows.length &&
      this.layout.items.length === nextRows.length &&
      oldItems.slice(0, prefixCount).every((item, index) => {
        const nextItem = this.layout.items[index];
        return (
          nextItem?.y === item.y &&
          nextItem?.height === item.height &&
          nextItem?.width === item.width
        );
      });
    if (!stablePrefixGeometry) {
      this.content.style.overflow = this.structuralContentOverflow;
      this.structuralContentOverflow = undefined;
    } else {
      this.structuralPrefixTransition = true;
    }
    if (stablePrefixGeometry && nextRows.length < oldRows.length) {
      const layer = createElement(
        this.document,
        'div',
        'ok-native-list-structural-tail'
      );
      layer.setAttribute('aria-hidden', 'true');
      layer.setAttribute('inert', '');
      Object.assign(layer.style, {
        position: 'absolute',
        inset: '0',
        pointerEvents: 'none',
      });
      this.content.append(layer);
      this.structuralTail = {
        snapshot: oldSnapshot,
        rows: oldRows,
        layout: oldLayout,
        firstIndex: prefixCount,
        layer,
        mounted: new Map(),
      };
      this.updateStructuralTailWindow();
    }
    const nextAnchor =
      anchor && this.layout.items.find((item) => item.key === anchor.key);
    const delta = nextAnchor && anchor ? nextAnchor.y - anchor.y : 0;
    let finalScrollHeight = oldScrollHeight + newHeight - oldHeight;
    if (stablePrefixGeometry && this.externalScrollHost) {
      const beforeMeasure = owner.scrollTop;
      const tailLayer = this.structuralTail?.layer;
      if (tailLayer) tailLayer.style.display = 'none';
      this.writeStructuralHeight(newHeight);
      this.applyStructuralFooterVisual(1);
      finalScrollHeight = owner.scrollHeight;
      this.writeStructuralHeight(startHeight);
      this.applyStructuralFooterVisual(0);
      if (tailLayer) tailLayer.style.display = '';
      owner.scrollTop = beforeMeasure;
    }
    const finalMaxOffset = Math.max(0, finalScrollHeight - owner.clientHeight);
    let targetOffset = Math.max(0, Math.min(oldOffset + delta, finalMaxOffset));
    if (
      !visibleSurvivor &&
      transition.anchorKey &&
      !(stablePrefixGeometry && nextRows.length > oldRows.length)
    ) {
      const fallback = this.layout.items.find(
        (item) => item.key === transition.anchorKey
      );
      if (fallback) {
        const pagerStickyInset = Number.parseFloat(
          this.externalScrollHost?.style.getPropertyValue(
            '--ok-collapsible-pager-sticky-inset'
          ) ?? ''
        );
        const effectiveStickyInset = Math.max(
          this.externalStickyInset,
          Number.isFinite(pagerStickyInset) ? pagerStickyInset : 0
        );
        const fallbackTop = this.externalScrollHost
          ? this.externalGeometry().origin + fallback.y - effectiveStickyInset
          : resolveWebCollapsiblePagerRawOffset(
              Math.max(0, fallback.y - this.collapsiblePagerInsets().sticky),
              this.collapsiblePagerInsets().header
            );
        if (
          stablePrefixGeometry &&
          this.externalScrollHost &&
          this.reactFooterHeight > 0
        ) {
          const footerTop = this.externalGeometry().origin + newHeight;
          const footerStart = Math.max(
            0,
            footerTop + this.reactFooterHeight - owner.clientHeight
          );
          const footerEnd = Math.max(0, footerTop - effectiveStickyInset);
          const nearestVisibleOffset =
            footerStart <= footerEnd
              ? Math.max(footerStart, Math.min(oldOffset, footerEnd))
              : footerEnd;
          targetOffset = Math.max(
            0,
            Math.min(nearestVisibleOffset, finalMaxOffset)
          );
        } else {
          targetOffset = Math.min(targetOffset, Math.max(0, fallbackTop));
        }
      }
    }
    const duration = transition.durationMs ?? 220;
    const easing = 'cubic-bezier(0.42, 0, 0.58, 1)';
    this.mounted.forEach((element, index) => {
      const row = this.rows[index];
      const previousTop = row && oldPositions.get(row.key);
      if (previousTop === undefined || typeof element.animate !== 'function')
        return;
      const offset = previousTop - element.getBoundingClientRect().top;
      if (Math.abs(offset) < 0.5) return;
      this.structuralAnimations.push(
        element.animate(
          [
            { transform: `translateY(${offset}px)` },
            { transform: 'translateY(0)' },
          ],
          { duration, easing }
        )
      );
    });
    const startedAt =
      this.document.defaultView?.performance.now() ?? Date.now();
    const revision = this.structuralRevision;
    const finish = () => {
      if (this.destroyed || revision !== this.structuralRevision) return;
      const ownsScroll = !this.structuralScrollInterrupted;
      this.cancelStructuralAnimation();
      this.recomputeLayout();
      if (stablePrefixGeometry && ownsScroll) {
        owner.scrollTop = Math.max(
          0,
          Math.min(targetOffset, owner.scrollHeight - owner.clientHeight)
        );
      }
    };
    const step = (time: number) => {
      if (this.destroyed || revision !== this.structuralRevision) return;
      if (
        this.structuralPrefixTransition &&
        this.document.defaultView?.matchMedia?.(
          '(prefers-reduced-motion: reduce)'
        ).matches
      ) {
        finish();
        return;
      }
      const progress = Math.min(1, (time - startedAt) / duration);
      const eased = progress * progress * (3 - 2 * progress);
      this.structuralProgress = eased;
      const renderedHeight =
        oldHeight +
        ((this.structuralTargetHeight ?? newHeight) - oldHeight) * eased +
        this.structuralFooterCompensation * (1 - eased);
      this.structuralRenderedHeight = renderedHeight;
      this.writeStructuralHeight(renderedHeight);
      this.applyStructuralFooterVisual(eased);
      if (!this.structuralScrollInterrupted)
        owner.scrollTop = oldOffset + (targetOffset - oldOffset) * eased;
      this.externalGeometryCache = undefined;
      this.updateStructuralTailWindow();
      if (progress < 1)
        this.structuralFrame =
          this.document.defaultView?.requestAnimationFrame(step);
    };
    this.structuralFrame =
      this.document.defaultView?.requestAnimationFrame(step);
    this.structuralFinish = this.document.defaultView?.setTimeout(
      finish,
      duration + 40
    );
  }

  currentContentHeight(): number {
    return this.layout.contentHeight;
  }

  currentSnapshot(): NativeListSnapshot {
    return this.snapshot;
  }

  applyPatches(patches: readonly RowPatch[]) {
    const accepted = containNativeListInput(() =>
      applyValidatedRowPatches(this.snapshot, patches)
    );
    if (!accepted || accepted.patches.length === 0) return;
    patches = accepted.patches;
    const next = accepted.snapshot;
    const selection = new Set(this.selectedKeys);
    patches.forEach((patch) => {
      const selected =
        'selected' in patch.changes ? patch.changes.selected : undefined;
      if (selected === true) selection.add(patch.key);
      else if (selected === false || selected === null)
        selection.delete(patch.key);
    });
    const tableValuesOnly = patches.every((patch) => {
      const index = this.rows.findIndex((row) => row.key === patch.key);
      const before = this.rows[index],
        after = next.rows[index];
      return (
        patch.type === 'market' &&
        before?.type === 'market' &&
        after?.type === 'market' &&
        Object.keys(patch.changes).every(
          (key) => MARKET_QUOTE_PATCH_FIELDS.has(key) || key === 'webTable'
        ) &&
        canPatchMarketTableValues(before, after)
      );
    });
    if (webMarketImageBindDeltaForPatches(patches) === 0 || tableValuesOnly) {
      this.snapshot = next;
      this.rows = effectiveRows(next);
      this.selectedKeys = selection;
      patches.forEach((patch) => {
        const index = this.rows.findIndex((row) => row.key === patch.key);
        const row = this.rows[index];
        const element = this.mounted.get(index);
        if (row?.type === 'market' && element) {
          if (row.webTable) updateMarketTableValues(element, row);
          else setMarketQuoteContent(element, row);
          element.dataset.renderSignature = webRowRenderSignature(row);
        }
        if (
          row?.type === 'market' &&
          this.sticky.dataset.nativeListRowKey === row.key
        ) {
          if (row.webTable) updateMarketTableValues(this.sticky, row);
          else setMarketQuoteContent(this.sticky, row);
          this.sticky.dataset.renderSignature = webRowRenderSignature(row);
        }
      });
      return;
    }
    this.invalidateActionAnchor('snapshot');
    this.setSnapshot(next, selection);
  }

  reconcileSelection(keys: readonly string[]) {
    const selectable = new Set(
      this.snapshot.rows.filter(isSelectableRow).map((row) => row.key)
    );
    if (keys.some((key) => !selectable.has(key))) return;
    if (this.snapshot.selection?.mode === 'single' && keys.length > 1) return;
    this.selectedKeys = new Set(keys);
    this.updateVisibleSelection();
    this.renderFooter();
  }

  scrollToIndex(index: number, scroll: NormalizedPositionScroll) {
    if (index < 0 || index >= this.rows.length) {
      this.emitScrollFailure(index, 'index-out-of-range');
      return;
    }
    this.notifyScrollCommandAccepted();
    if (!this.canScroll()) {
      this.pendingScroll = { kind: 'index', index, scroll };
      return;
    }
    const item = this.layout.items[index];
    if (!item) {
      this.pendingScroll = { kind: 'index', index, scroll };
      return;
    }
    const viewportLength = this.viewportLength();
    const contentLength = this.contentLength();
    const currentOffset = this.currentOffset();
    const offset = calculateAlignedScrollOffset({
      itemOffset: itemStart(item, this.layout.horizontal),
      itemLength: this.layout.horizontal ? item.width : item.height,
      viewportLength,
      contentLength,
      currentOffset,
      alignment: scroll.alignment,
      viewPosition: scroll.viewPosition,
      viewOffset: scroll.viewOffset,
    });
    this.scrollToAbsoluteOffset(offset, scroll.animated);
  }

  scrollToKey(key: string, scroll: NormalizedPositionScroll) {
    const index = this.rows.findIndex((row) => row.key === key);
    if (index >= 0) this.scrollToIndex(index, scroll);
  }

  scrollToOffset(offset: number, animated: boolean) {
    validateOffset(offset);
    this.notifyScrollCommandAccepted();
    if (!this.canScroll()) {
      this.pendingScroll = { kind: 'offset', offset, animated };
      return;
    }
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      this.writeExternalOffset(geometry.containerOrigin + offset, animated);
    } else this.scrollToAbsoluteOffset(offset, animated);
  }

  scrollToEnd(animated: boolean) {
    this.notifyScrollCommandAccepted();
    if (!this.canScroll()) {
      this.pendingScroll = { kind: 'end', animated };
      return;
    }
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      this.writeExternalOffset(
        geometry.end - this.externalScrollHost.clientHeight,
        animated
      );
    } else
      this.scrollToAbsoluteOffset(
        Math.max(0, this.contentLength() - this.viewportLength()),
        animated
      );
  }

  scrollToLocation(
    params: ScrollToLocationParams,
    scroll: NormalizedPositionScroll
  ) {
    const index = resolveLocationIndex(this.snapshot.rows, params);
    if (index === undefined) {
      const sectionCount = this.snapshot.rows.filter(
        (row) =>
          row.type === 'sectionHeader' &&
          row.sticky !== false &&
          row.variant !== 'summary'
      ).length;
      this.emitScrollFailure(
        params.itemIndex,
        params.sectionIndex >= sectionCount
          ? 'section-out-of-range'
          : 'item-out-of-range'
      );
      return;
    }
    this.scrollToIndex(index, scroll);
  }

  setRefreshing(refreshing: boolean) {
    this.snapshot = {
      ...this.snapshot,
      capabilities: { ...this.snapshot.capabilities, refreshing },
    };
    this.updateRefreshIndicator();
  }

  setActionAnchorState(state: ActionAnchorState) {
    const anchor = this.actionAnchor;
    if (!anchor || anchor.token !== state.token) return;
    if (!state.open) {
      if (state.restoreFocus && this.isActionAnchorValid(anchor)) {
        anchor.actionElement.focus({ preventScroll: true });
      }
      anchor.actionElement.removeAttribute('aria-expanded');
      this.actionAnchor = undefined;
      return;
    }
    if (anchor.invalidatedReason || !this.isActionAnchorValid(anchor)) {
      this.emitActionAnchorInvalidated(
        anchor,
        anchor.invalidatedReason ?? 'rebind'
      );
      return;
    }
    anchor.open = true;
    anchor.actionElement.setAttribute('aria-expanded', 'true');
  }

  destroy() {
    if (this.destroyed) return;
    this.cancelStructuralAnimation();
    this.pressedHelpElement = undefined;
    this.invalidateActionAnchor('destroy');
    this.destroyed = true;
    if (this.frameHandle !== undefined) this.cancelFrame(this.frameHandle);
    if (this.sectionIndexTransformFrame !== undefined)
      this.cancelFrame(this.sectionIndexTransformFrame);
    if (this.reorderMovementTimer !== undefined)
      this.document.defaultView?.clearTimeout(this.reorderMovementTimer);
    this.resizeObserver?.disconnect();
    this.externalScrollHost?.removeEventListener(
      'scroll',
      this.handleExternalScroll,
      true
    );
    this.document.fonts?.removeEventListener(
      'loadingdone',
      this.handleWindowResize
    );
    this.sectionIndexTransformObserver?.disconnect();
    this.document.defaultView?.removeEventListener(
      'resize',
      this.handleWindowResize
    );
    this.pendingScroll = undefined;
    (this.externalScrollHost ?? this.viewport).removeEventListener(
      'ok-collapsible-pager-cancel-scroll-command',
      this.handlePagerScrollCommandCancel
    );
    for (const event of ['wheel', 'pointerdown', 'touchstart'])
      (this.externalScrollHost ?? this.viewport).removeEventListener(
        event,
        this.handleStructuralUserScroll
      );
    this.viewport.removeEventListener('scroll', this.handleScroll);
    (this.externalScrollHost ?? this.viewport).removeEventListener(
      'ok-collapsible-pager-insets-changed',
      this.handleCollapsiblePagerInsetsChanged
    );
    this.root.removeEventListener('click', this.handleClick);
    this.root.removeEventListener('contextmenu', this.handleMarketContextMenu);
    this.cancelMarketPointer();
    this.root.removeEventListener('pointerdown', this.handleMarketPointerDown);
    this.root.removeEventListener('pointermove', this.handleMarketPointerMove);
    this.root.removeEventListener('touchmove', this.handleMarketTouchMove);
    this.root.removeEventListener('pointerup', this.handleMarketPointerEnd);
    this.root.removeEventListener('pointercancel', this.handleMarketPointerEnd);
    this.root.removeEventListener('pointerover', this.handleTitlePointerOver);
    this.root.removeEventListener(
      'pointerdown',
      this.handleIconHelpPointerDown
    );
    this.root.removeEventListener('focusin', this.handleTitlePointerOver);
    this.root.removeEventListener('focusout', this.handleTitlePointerOut);
    this.root.removeEventListener('pointerout', this.handleTitlePointerOut);
    this.root.removeEventListener('keydown', this.handleKeyDown);
    this.cancelPointerReorder(true);
    this.viewport.removeEventListener(
      'pointerdown',
      this.handleReorderPointerDown
    );
    this.viewport.removeEventListener(
      'pointermove',
      this.handleReorderPointerMove
    );
    this.viewport.removeEventListener(
      'pointerup',
      this.handleReorderPointerEnd
    );
    this.viewport.removeEventListener(
      'pointercancel',
      this.handleReorderPointerCancel
    );
    this.viewport.removeEventListener('touchmove', this.handleReorderTouchMove);
    this.viewport.removeEventListener('touchend', this.handleReorderTouchEnd);
    this.viewport.removeEventListener(
      'touchcancel',
      this.handleReorderTouchCancel
    );
    this.indexRail.removeEventListener('pointerdown', this.handleIndexPointer);
    this.indexRail.removeEventListener('pointermove', this.handleIndexPointer);
    this.indexRail.removeEventListener('pointerup', this.handleIndexPointerEnd);
    this.indexRail.removeEventListener(
      'pointercancel',
      this.handleIndexPointerEnd
    );
    this.indexRail.removeEventListener('click', this.handleIndexClick);
    this.pullTarget.removeEventListener('pointerdown', this.handlePullStart);
    this.pullTarget.removeEventListener('pointermove', this.handlePullMove);
    this.pullTarget.removeEventListener('pointerup', this.handlePullEnd);
    this.pullTarget.removeEventListener('pointercancel', this.handlePullEnd);
    const host = this.root.parentElement;
    disposeWebImageRetries(this.root);
    Object.values(this.pool).forEach((elements) =>
      elements.forEach(disposeWebImageRetries)
    );
    this.root.remove();
    this.indexRail.remove();
    this.hideReorderPreview();
    this.reorderPreview.remove();
    if (host) host.style.position = this.previousHostPosition;
    this.mounted.clear();
    Object.values(this.pool).forEach((elements) => {
      elements.length = 0;
    });
    this.avatarLeases.forEach((release) => release());
    this.avatarLeases.clear();
  }

  private setSnapshot(
    snapshot: NativeListSnapshot,
    selectedKeys?: ReadonlySet<string>
  ) {
    this.cancelMarketPointer();
    this.pressedHelpElement = undefined;
    this.measuredRendererHeights.clear();
    this.snapshot = snapshot;
    this.rows = effectiveRows(snapshot);
    this.selectedKeys =
      selectedKeys ?? selectionStateFromSnapshot(snapshot).selectedKeys;
    if (this.reachedGeneration !== snapshot.generation)
      this.reachedGeneration = undefined;
    this.applyTheme();
    this.recomputeLayout();
    this.renderFooter();
    this.updateRefreshIndicator();
  }

  private applyTheme() {
    const theme = resolvedTheme(this.snapshot);
    const values: Readonly<Record<string, string | undefined>> = {
      '--nl-bg': theme.background,
      '--nl-row': theme.rowBackground,
      '--nl-selected': theme.rowSelectedBackground,
      '--nl-pressed': theme.rowPressedBackground,
      '--nl-hover': theme.rowHoverBackground ?? theme.rowPressedBackground,
      '--nl-subdued': theme.subduedBackground,
      '--nl-strong': theme.strongBackground,
      '--nl-image-fallback-background':
        theme.imageFallbackBackground ?? 'transparent',
      '--nl-primary': theme.primaryText,
      '--nl-secondary': theme.secondaryText,
      '--nl-disabled': theme.disabledText,
      '--nl-caution': theme.caution ?? '#AB6400',
      '--nl-caution-background': theme.cautionBackground,
      '--nl-checkbox-background': theme.checkboxBackground,
      '--nl-checkbox-border': theme.checkboxBorder,
      '--nl-checkbox-icon': theme.checkboxIcon,
      '--nl-icon': theme.icon,
      '--nl-icon-subdued': theme.iconSubdued,
      '--nl-separator': theme.separator,
      '--nl-accent': theme.accent,
      '--nl-positive': theme.positive,
      '--nl-success-background': theme.successBackground,
      '--nl-negative': theme.negative,
      '--nl-critical': theme.criticalBackground,
      '--nl-inverse': theme.inverseBackground,
      '--nl-inverse-text': theme.inverseText,
      '--nl-info': theme.info,
    };
    Object.entries(values).forEach(([name, value]) => {
      if (!value) return;
      this.root.style.setProperty(name, value);
      this.indexRail.style.setProperty(name, value);
      this.reorderPreview.style.setProperty(name, value);
    });
    this.applyListStyle();
  }

  /**
   * List chrome from `snapshot.listStyle`. Every value is absent by default and
   * the stylesheet carries today's number as the fallback, so an untouched list
   * renders exactly as before. See docs/STYLE_SPEC.md §5.
   */
  private applyListStyle() {
    const listStyle = this.snapshot.listStyle;
    const inset = listStyle?.separator?.inset;
    setData(this.root, 'separatorInset', inset !== undefined && inset > 0);
    const chrome: Readonly<Record<string, string | undefined>> = {
      '--nl-separator-inset':
        inset === undefined ? undefined : String(inset) + 'px',
      '--nl-separator-color': listStyle?.separator?.color,
      '--nl-group-radius':
        listStyle?.groupCornerRadius === undefined
          ? undefined
          : String(listStyle.groupCornerRadius) + 'px',
    };
    Object.entries(chrome).forEach(([name, value]) => {
      if (value === undefined) this.root.style.removeProperty(name);
      else this.root.style.setProperty(name, value);
    });
  }

  private computeLayout(
    viewportWidth: number,
    viewportHeight: number
  ): WebListLayout {
    const measuredSnapshot: NativeListSnapshot = {
      ...this.snapshot,
      rows: this.snapshot.rows.map((row) =>
        row.style?.container?.height === undefined &&
        row.height === undefined &&
        this.measuredRendererHeights.get(row.key)?.signature ===
          webRowRenderSignature(row)
          ? {
              ...row,
              height: this.measuredRendererHeights.get(row.key)!.height,
            }
          : row
      ),
    };
    let layout = computeWebListLayout(
      measuredSnapshot,
      viewportWidth,
      viewportHeight,
      this.reorderCompactKey
    );
    if (this.externalScrollHost) {
      const padding = paddingValues(this.snapshot);
      layout = {
        ...layout,
        contentHeight:
          Math.max(
            padding.top,
            ...layout.items.map((item) => item.y + item.height)
          ) + padding.bottom,
      };
    }
    return layout;
  }

  private writeExternalHeight(height: number) {
    const value = String(height) + 'px';
    this.content.style.height = value;
    this.viewport.style.height = value;
    this.viewportFrame.style.height = value;
    this.root.style.height = value;
  }

  private recomputeLayout = () => {
    if (this.destroyed) return;
    if (
      this.structuralPrefixTransition &&
      this.lastViewportWidth > 0 &&
      this.viewport.clientWidth > 0 &&
      this.viewport.clientWidth !== this.lastViewportWidth
    )
      this.cancelStructuralAnimation();
    if (this.structuralRenderedHeight === undefined)
      this.reactFooterHeight = this.readReactFooterHeight();
    else {
      const nextFooterHeight = this.readReactFooterHeight();
      const footerDelta = this.reactFooterHeight - nextFooterHeight;
      if (footerDelta !== 0) {
        this.reactFooterHeight = nextFooterHeight;
        const priorCompensation = this.structuralFooterCompensation;
        const difference = this.structuralFooterFromHeight - nextFooterHeight;
        this.structuralFooterCompensation = Math.max(0, difference);
        this.structuralFooterExpansion = Math.max(0, -difference);
        this.structuralRenderedHeight +=
          (this.structuralFooterCompensation - priorCompensation) *
          (1 - this.structuralProgress);
        this.applyStructuralFooterVisual(this.structuralProgress);
      }
    }
    const cachedWidth = this.lastViewportWidth;
    const cachedHeight = this.lastViewportHeight;
    const precomputedLayout =
      this.externalScrollHost && cachedWidth > 0 && cachedHeight > 0
        ? this.computeLayout(cachedWidth, cachedHeight)
        : undefined;
    if (precomputedLayout && this.structuralRenderedHeight === undefined) {
      // Commit appended row extent before measuring React-owned slot changes.
      this.writeExternalHeight(
        Math.max(this.layout.contentHeight, precomputedLayout.contentHeight)
      );
    }
    this.externalGeometryCache = undefined;
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      const previousOrigin = this.externalOrigin;
      this.externalOrigin = geometry.origin;
      if (
        previousOrigin !== undefined &&
        geometry.origin !== previousOrigin &&
        this.externalScrollHost.scrollTop > previousOrigin &&
        geometry.visible
      )
        this.externalScrollHost.scrollTop += geometry.origin - previousOrigin;
    }
    const viewportWidth = this.viewport.clientWidth;
    const viewportHeight =
      this.snapshot.layout.orientation === 'horizontal'
        ? this.viewport.clientHeight
        : this.verticalScrollMetrics().viewportLength;
    const stickyInset =
      this.snapshot.layout.orientation === 'horizontal'
        ? 0
        : this.collapsiblePagerInsets().sticky;
    if (this.snapshot.layout.orientation !== 'horizontal') {
      this.sticky.style.top = String(stickyInset) + 'px';
      this.refreshIndicator.style.top = String(stickyInset + 8) + 'px';
    } else {
      this.sticky.style.top = '0px';
      this.refreshIndicator.style.top = '8px';
    }
    if (
      this.lastViewportWidth >= 0 &&
      (viewportWidth !== this.lastViewportWidth ||
        viewportHeight !== this.lastViewportHeight)
    ) {
      this.invalidateActionAnchor('layout');
      this.measuredRendererHeights.clear();
    }
    this.lastViewportWidth = viewportWidth;
    this.lastViewportHeight = viewportHeight;
    const previousHorizontal = this.layout.horizontal;
    this.layout =
      precomputedLayout &&
      viewportWidth === cachedWidth &&
      viewportHeight === cachedHeight
        ? precomputedLayout
        : this.computeLayout(viewportWidth, viewportHeight);
    this.content.style.width = String(this.layout.contentWidth) + 'px';
    const renderedHeight =
      this.structuralRenderedHeight ?? this.layout.contentHeight;
    this.content.style.height = String(renderedHeight) + 'px';
    if (this.externalScrollHost) {
      this.writeExternalHeight(renderedHeight);
      this.externalGeometryCache = undefined;
    }
    if (this.structuralRenderedHeight !== undefined)
      this.structuralTargetHeight = this.layout.contentHeight;
    this.renderSectionIndex(
      this.viewport.clientHeight <= 0
        ? DEFAULT_VIEWPORT_HEIGHT
        : Math.max(1, viewportHeight),
      stickyInset
    );
    if (previousHorizontal !== this.layout.horizontal) {
      this.viewport.scrollLeft = 0;
      this.viewport.scrollTop = 0;
    }
    this.renderWindow();
    this.performPendingScroll();
  };

  private updateAvatarWindow() {
    if (this.externalScrollHost && !this.externalGeometry().visible) {
      this.avatarLeases.forEach((lease) => lease());
      this.avatarLeases.clear();
      return;
    }
    const offset = this.currentOffset();
    if (offset !== this.avatarOffset)
      this.avatarDirection = Math.sign(offset - this.avatarOffset);
    this.avatarOffset = offset;
    const visible = visibleWebLayoutItems(
      this.layout,
      offset,
      this.viewportLength(),
      0
    );
    const first = visible[0]?.index ?? -1;
    const last = visible[visible.length - 1]?.index ?? -1;
    const candidates = avatarPrefetchWindow(
      this.rows,
      first,
      last,
      this.avatarDirection
    );
    const desired = new Set(
      candidates
        .map(({ source }) => canonicalNativeListAvatarUri(source.uri))
        .filter((uri) => uri !== undefined)
    );
    this.avatarLeases.forEach((release, uri) => {
      if (!desired.has(uri)) {
        release();
        this.avatarLeases.delete(uri);
      }
    });
    candidates.forEach(({ source, priority }) => {
      const uri = canonicalNativeListAvatarUri(source.uri);
      if (!uri) return;
      const existing = this.avatarLeases.get(uri);
      if (existing) existing.setPriority(priority);
      else {
        let lease: AvatarLease | undefined;
        lease = acquireNativeListAvatar(
          this.document,
          uri,
          () => {},
          () => {
            if (this.avatarLeases.get(uri) === lease) {
              lease?.();
              this.avatarLeases.delete(uri);
            }
          },
          priority
        );
        this.avatarLeases.set(uri, lease);
      }
    });
  }

  private renderWindow() {
    this.updateAvatarWindow();
    const viewportLength = this.viewportLength();
    const visible =
      this.externalScrollHost && !this.externalGeometry().visible
        ? []
        : webLayoutItemsForMount(
            this.layout,
            this.currentOffset(),
            viewportLength,
            this.virtualizationEnabled,
            viewportLength * OVERSCAN_VIEWPORTS
          );
    const desired = new Set(visible.map((item) => item.index));
    this.mounted.forEach((element, index) => {
      const nextRow = this.rows[index];
      const rendererKey = this.rendererKeys.get(element);
      if (
        !desired.has(index) ||
        !nextRow ||
        rendererKey !== rowRendererKey(nextRow)
      ) {
        this.invalidateActionAnchorForElement(element);
        this.mounted.delete(index);
        const recycled = element.firstElementChild
          ? recycleRowBody(element.firstElementChild as HTMLElement)
          : false;
        if (disposeWebImageRetries(element) || recycled)
          element.removeAttribute('data-render-signature');
        element.remove();
        if (rendererKey) this.pool[rendererKey].push(element);
      }
    });

    visible.forEach((layoutItem) => {
      const row = this.rows[layoutItem.index];
      if (!row) return;
      let element = this.mounted.get(layoutItem.index);
      if (!element) {
        const rendererKey = rowRendererKey(row);
        element =
          this.pool[rendererKey].pop() ??
          createElement(this.document, 'div', 'ok-native-list-item');
        this.rendererKeys.set(element, rendererKey);
        setData(element, 'nativeListAnimateReorder', false);
        this.mounted.set(layoutItem.index, element);
        this.content.appendChild(element);
      }
      this.positionElement(element, layoutItem);
      const signature = webRowRenderSignature(row);
      if (
        element.dataset.nativeListRowKey !== row.key ||
        element.dataset.renderSignature !== signature
      ) {
        this.renderElement(element, layoutItem.index, row);
        element.dataset.renderSignature = signature;
      }
    });
    let measuredHeightChanged = false;
    this.mounted.forEach((element, index) => {
      const row = this.rows[index];
      const registered = row && rowRenderer(row);
      if (registered && element.firstElementChild) {
        const height = registered.measureRendered(
          element.firstElementChild as HTMLElement
        );
        if (
          height !== undefined &&
          (height !== this.measuredRendererHeights.get(row.key)?.height ||
            webRowRenderSignature(row) !==
              this.measuredRendererHeights.get(row.key)?.signature)
        ) {
          this.measuredRendererHeights.set(row.key, {
            signature: webRowRenderSignature(row),
            height,
          });
          measuredHeightChanged = true;
        }
        return;
      }
    });
    if (measuredHeightChanged) {
      this.recomputeLayout();
      return;
    }
    this.updateVisibleSelection();
    this.updateVisibleState();
    this.updateStructuralTailWindow();
  }

  private positionElement(element: HTMLElement, item: WebLayoutItem) {
    element.style.transform =
      'translate3d(' + String(item.x) + 'px,' + String(item.y) + 'px,0)';
    element.style.width = String(item.width) + 'px';
    element.style.height = String(item.height) + 'px';
  }

  private renderElement(
    element: HTMLElement,
    index: number,
    row: RowModel,
    overlay = false
  ) {
    this.invalidateActionAnchorForElement(element);
    const registered = rowRenderer(row);
    const existingBody = element.firstElementChild as HTMLElement | null;
    const reusableBody =
      existingBody?.dataset.nlRenderer === registered.key
        ? existingBody
        : undefined;
    if (!reusableBody) disposeWebImageRetries(element);
    const bindingEpoch = String(++this.bindingEpochCounter);
    element.className = overlay
      ? 'ok-native-list-item ok-native-list-sticky'
      : 'ok-native-list-item';
    setData(element, 'nativeListRowKey', row.key);
    setData(
      element,
      'nativeListMediaGallery',
      row.type === 'mediaTile' && row.variant === 'gallery' ? 'true' : undefined
    );
    setData(element, 'nativeListBindingEpoch', bindingEpoch);
    setData(element, 'nativeListRowIndex', index);
    setData(element, 'nativeListDisabled', Boolean(row.disabled));
    setData(element, 'nativeListRowPressEnabled', isRowPressEnabled(row));
    setData(element, 'testid', row.testID);
    // OneKey patch: deprecation dims a row without disabling its actions.
    element.style.opacity = String(
      (row.style?.container?.opacity ?? row.opacity ?? 1) *
        (row.disabled ? 0.5 : 1)
    );
    setData(element, 'nativeListReorderable', this.isReorderable(row));
    setData(
      element,
      'nativeListDragging',
      this.pointerReorder?.active && this.pointerReorder.sourceKey === row.key
    );
    setData(element, 'separator', row.separator);
    setData(element, 'groupPosition', row.groupPosition);
    setData(
      element,
      'tableAlternate',
      this.snapshot.layout.kind === 'table' &&
        row.type === 'dataRow' &&
        (row.index ?? index) % 2 === 0
    );
    element.setAttribute('role', 'listitem');
    element.setAttribute(
      'aria-label',
      row.accessibilityLabel ?? this.rowLabel(row)
    );
    if (isRowPressEnabled(row)) {
      element.tabIndex = 0;
    } else {
      element.removeAttribute('tabindex');
    }
    element.draggable = false;
    const context = {
      document: this.document,
      snapshot: this.snapshot,
      selectedKeys: this.selectedKeys,
      itemIndex: index,
    };
    const body = reusableBody ?? createRowBody(context, row);
    if (reusableBody) {
      resetRowContainerStyle(body);
      registered.bind(body, rendererPrimitives(context, Boolean(row.disabled)));
    }
    // OneKey patch: explicit selector fields preserve original page geometry.
    element.style.contain = row.backgroundFullWidth ? 'layout style' : '';
    if (row.backgroundColor) body.style.backgroundColor = row.backgroundColor;
    const restingBackground =
      row.style?.container?.backgroundColor ?? row.backgroundColor;
    if (row.backgroundFullWidth && restingBackground) {
      const bleed = paddingValues(this.snapshot).horizontal;
      body.style.position = 'relative';
      body.style.overflow = 'visible';
      body.style.boxShadow =
        String(-bleed) +
        'px 0 ' +
        restingBackground +
        ',' +
        String(bleed) +
        'px 0 ' +
        restingBackground;
    }
    // Template and presentation defaults must precede caller overrides.
    applyRowStyle(body, row);
    if (body.parentElement !== element) element.replaceChildren(body);
    if (
      row.type === 'market' &&
      row.diagnostics?.imageBindActionKey &&
      body.querySelector('img')
    ) {
      this.callbacks.onRowAction?.({
        rowKey: row.key,
        actionKey: row.diagnostics.imageBindActionKey,
        sectionKey: row.sectionKey,
      });
    }
  }

  private renderFooter() {
    const row = this.snapshot.fixedFooter;
    this.invalidateActionAnchorForElement(this.footer);
    disposeWebImageRetries(this.footer);
    this.footer.replaceChildren();
    if (!row) return;
    const element = createElement(this.document, 'div', 'ok-native-list-item');
    this.renderElement(element, -1, row);
    element.style.position = 'relative';
    element.style.transform = 'none';
    element.style.width = '100%';
    element.style.height =
      String(
        estimateWebRowHeight(
          row,
          this.snapshot,
          this.viewport.clientWidth || DEFAULT_VIEWPORT_WIDTH
        )
      ) + 'px';
    this.footer.appendChild(element);
  }

  private sectionIndexVisibleEntryIndices(
    viewportHeight: number
  ): readonly number[] {
    const entryCount = this.sectionIndexEntries.length;
    if (entryCount <= 1) return entryCount ? [0] : [];
    const { trackHeight } = this.sectionIndexMetrics(viewportHeight);
    const maxVisible = Math.max(
      1,
      Math.floor(trackHeight / SECTION_INDEX_MIN_LABEL_SPACING)
    );
    if (entryCount <= maxVisible) {
      return Array.from({ length: entryCount }, (_, index) => index);
    }
    if (maxVisible === 1) return [0];
    const result = new Set<number>();
    for (let slot = 0; slot < maxVisible; slot += 1) {
      result.add(Math.round((slot * (entryCount - 1)) / (maxVisible - 1)));
    }
    return [...result].sort((left, right) => left - right);
  }

  private sectionIndexMetrics(viewportHeight: number) {
    return webSectionIndexMetrics(
      this.sectionIndexEntries.length,
      viewportHeight,
      this.sectionIndexAlignStart
    );
  }

  private configureSectionIndexRail(
    viewportHeight: number,
    stickyInset: number,
    windowCentered: boolean
  ): number {
    const view = this.document.defaultView;
    const direction =
      view?.getComputedStyle(this.viewportFrame).direction === 'rtl'
        ? 'rtl'
        : 'ltr';
    this.indexRail.style.direction = direction;
    if (!windowCentered) {
      this.sectionIndexAlignStart = false;
      this.resetSectionIndexTransformCompensation();
      if (this.indexRail.parentElement !== this.viewportFrame) {
        this.viewportFrame.appendChild(this.indexRail);
      }
      this.indexRail.style.removeProperty('position');
      this.indexRail.style.removeProperty('left');
      this.indexRail.style.removeProperty('right');
      this.indexRail.style.removeProperty('height');
      this.indexRail.style.removeProperty('z-index');
      this.indexRail.style.insetInlineEnd = '0px';
      this.indexRail.style.top = String(stickyInset) + 'px';
      this.indexRail.style.bottom = '0px';
      return viewportHeight;
    }

    if (!view) return viewportHeight;
    const sectionIndexContainer = this.sectionIndexContainer?.isConnected
      ? this.sectionIndexContainer
      : undefined;
    const frame = this.viewportFrame.getBoundingClientRect();
    if (sectionIndexContainer) {
      const container = sectionIndexContainer.getBoundingClientRect();
      const railLayout = webSectionIndexContainerLayout(
        this.sectionIndexEntries.length,
        sectionIndexContainer.clientHeight || container.height
      );
      if (this.indexRail.parentElement !== sectionIndexContainer) {
        sectionIndexContainer.appendChild(this.indexRail);
      }
      this.sectionIndexAlignStart = true;
      this.indexRail.style.position = 'absolute';
      this.indexRail.style.removeProperty('left');
      this.indexRail.style.removeProperty('right');
      this.indexRail.style.insetInlineEnd =
        String(
          Math.max(
            0,
            direction === 'rtl'
              ? frame.left - container.left
              : container.right - frame.right
          )
        ) + 'px';
      this.indexRail.style.top = String(railLayout.top) + 'px';
      this.indexRail.style.bottom = 'auto';
      this.indexRail.style.height = String(railLayout.height) + 'px';
      this.indexRail.style.zIndex = String(SECTION_INDEX_WINDOW_Z_INDEX);
      this.startSectionIndexTransformCompensation();
      return railLayout.height;
    }

    this.sectionIndexAlignStart = false;
    this.resetSectionIndexTransformCompensation();
    const visualViewport = view.visualViewport;
    const windowTop = visualViewport?.offsetTop ?? 0;
    const windowHeight = visualViewport?.height ?? view.innerHeight;
    const railHeight = Math.min(
      windowHeight,
      SECTION_INDEX_EDGE_PADDING * 2 +
        SECTION_INDEX_MIN_LABEL_SPACING * this.sectionIndexEntries.length
    );
    if (this.indexRail.parentElement !== this.document.body) {
      this.document.body.appendChild(this.indexRail);
    }
    this.indexRail.style.position = 'fixed';
    this.indexRail.style.removeProperty('left');
    this.indexRail.style.removeProperty('right');
    this.indexRail.style.insetInlineEnd =
      String(
        Math.max(
          0,
          direction === 'rtl' ? frame.left : view.innerWidth - frame.right
        )
      ) + 'px';
    this.indexRail.style.top =
      String(windowTop + (windowHeight - railHeight) / 2) + 'px';
    this.indexRail.style.bottom = 'auto';
    this.indexRail.style.height = String(railHeight) + 'px';
    this.indexRail.style.zIndex = String(SECTION_INDEX_WINDOW_Z_INDEX);
    return railHeight;
  }

  private observeSectionIndexTransforms() {
    const container = this.sectionIndexContainer;
    const MutationObserverConstructor =
      this.document.defaultView?.MutationObserver;
    if (!container || !MutationObserverConstructor) return;
    this.sectionIndexTransformObserver = new MutationObserverConstructor(() =>
      this.startSectionIndexTransformCompensation()
    );
    let element: HTMLElement | null = container;
    while (element) {
      this.sectionIndexTransformObserver.observe(element, {
        attributes: true,
        attributeFilter: ['style'],
      });
      if (element === this.document.body) break;
      element = element.parentElement;
    }
  }

  private startSectionIndexTransformCompensation() {
    this.sectionIndexTransformTrackingFrames = 0;
    this.sectionIndexTransformSettledFrames = 0;
    this.updateSectionIndexTransformCompensation();
    this.scheduleSectionIndexTransformCompensation();
  }

  private scheduleSectionIndexTransformCompensation() {
    if (
      this.destroyed ||
      this.sectionIndexTransformFrame !== undefined ||
      this.indexRail.parentElement !== this.sectionIndexContainer
    ) {
      return;
    }
    this.sectionIndexTransformFrame = this.requestFrame(() => {
      this.sectionIndexTransformFrame = undefined;
      const settled = this.updateSectionIndexTransformCompensation();
      this.sectionIndexTransformTrackingFrames += 1;
      this.sectionIndexTransformSettledFrames = settled
        ? this.sectionIndexTransformSettledFrames + 1
        : 0;
      if (
        this.sectionIndexTransformTrackingFrames <
          SECTION_INDEX_TRANSFORM_MAX_TRACKING_FRAMES &&
        (this.sectionIndexTransformTrackingFrames <
          SECTION_INDEX_TRANSFORM_MIN_TRACKING_FRAMES ||
          this.sectionIndexTransformSettledFrames <
            SECTION_INDEX_TRANSFORM_SETTLED_FRAMES)
      ) {
        this.scheduleSectionIndexTransformCompensation();
      }
    });
  }

  private updateSectionIndexTransformCompensation() {
    const container = this.sectionIndexContainer;
    if (!container || this.indexRail.parentElement !== container) return true;
    const inverseScale = webSectionIndexInverseScale(
      container.clientHeight,
      container.getBoundingClientRect().height
    );
    this.indexRail.style.transformOrigin = 'center center';
    this.indexRail.style.transform =
      Math.abs(inverseScale - 1) < SECTION_INDEX_TRANSFORM_SETTLED_EPSILON
        ? 'none'
        : `scale(${String(inverseScale)})`;
    return Math.abs(inverseScale - 1) < SECTION_INDEX_TRANSFORM_SETTLED_EPSILON;
  }

  private resetSectionIndexTransformCompensation() {
    if (this.sectionIndexTransformFrame !== undefined) {
      this.cancelFrame(this.sectionIndexTransformFrame);
      this.sectionIndexTransformFrame = undefined;
    }
    this.sectionIndexTransformTrackingFrames = 0;
    this.sectionIndexTransformSettledFrames = 0;
    this.indexRail.style.removeProperty('transform');
    this.indexRail.style.removeProperty('transform-origin');
  }

  private renderSectionIndex(viewportHeight: number, stickyInset: number) {
    this.indexRail.replaceChildren();
    if (!sectionIndexEnabled(this.snapshot)) {
      this.sectionIndexEntries = [];
      this.configureSectionIndexRail(viewportHeight, stickyInset, false);
      setData(this.viewportFrame, 'sectionIndexVisible', false);
      this.indexRail.hidden = true;
      return;
    }
    this.sectionIndexEntries = this.snapshot.rows.flatMap((row, position) =>
      row.type === 'sectionHeader' && row.indexTitle
        ? [{ key: row.key, title: row.indexTitle, position }]
        : []
    );
    if (
      this.sectionIndexEntries.length === 0 ||
      viewportHeight < SECTION_INDEX_MIN_HEIGHT
    ) {
      this.configureSectionIndexRail(viewportHeight, stickyInset, false);
      setData(this.viewportFrame, 'sectionIndexVisible', false);
      this.indexRail.hidden = true;
      return;
    }
    const windowCentered =
      this.snapshot.capabilities?.sectionIndex?.centeredInWindow === true &&
      this.viewport.clientHeight > 0;
    const indexLayoutHeight = this.configureSectionIndexRail(
      viewportHeight,
      stickyInset,
      windowCentered
    );
    const visibleEntryIndices =
      this.sectionIndexVisibleEntryIndices(indexLayoutHeight);
    setData(
      this.indexRail,
      'compact',
      visibleEntryIndices.length < this.sectionIndexEntries.length
    );
    const metrics = this.sectionIndexMetrics(indexLayoutHeight);
    const visibleTrackHeight = Math.min(
      metrics.trackHeight,
      SECTION_INDEX_MIN_LABEL_SPACING * visibleEntryIndices.length
    );
    const allEntriesVisible =
      visibleEntryIndices.length === this.sectionIndexEntries.length;
    const resolvedVisibleTrackHeight = allEntriesVisible
      ? metrics.trackHeight
      : visibleTrackHeight;
    const visibleOriginY =
      metrics.originY + (metrics.trackHeight - resolvedVisibleTrackHeight) / 2;
    const fragment = this.document.createDocumentFragment();
    visibleEntryIndices.forEach((entryIndex, visibleIndex) => {
      const entry = this.sectionIndexEntries[entryIndex];
      if (!entry) return;
      const button = createElement(
        this.document,
        'button',
        'ok-native-list-index-button',
        entry.title
      );
      button.setAttribute('type', 'button');
      button.setAttribute('aria-label', 'Jump to ' + entry.title);
      setData(button, 'sectionEntryIndex', entryIndex);
      setData(button, 'sectionPosition', entry.position);
      setData(button, 'sectionKey', entry.key);
      button.style.top =
        String(
          visibleOriginY +
            (resolvedVisibleTrackHeight * (visibleIndex + 0.5)) /
              visibleEntryIndices.length
        ) + 'px';
      fragment.appendChild(button);
    });
    this.indexRail.appendChild(fragment);
    this.indexRail.hidden = this.indexRail.childElementCount === 0;
    setData(this.viewportFrame, 'sectionIndexVisible', !this.indexRail.hidden);
  }

  private updateVisibleSelection() {
    const update = (element: HTMLElement, row: RowModel | undefined) => {
      if (!row) return;
      // OneKey patch: selector adapters mark active rows independently of checkbox selection.
      // const selected = this.selectedKeys.has(row.key);
      const selected = row.selected === true || this.selectedKeys.has(row.key);
      setData(element, 'nativeListSelected', selected);
      element.setAttribute('aria-selected', String(selected));
      element
        .querySelectorAll<HTMLElement>('.ok-native-list-checkbox')
        .forEach((checkbox) => {
          const scope = checkbox.dataset.selectionScope;
          const key = checkbox.dataset.selectionKey;
          const target: SelectionTarget | undefined =
            scope === 'section' && key
              ? { scope: 'section', sectionKey: key }
              : scope === 'list'
              ? { scope: 'list' }
              : { scope: 'row' };
          const state = checkboxState(
            target,
            (checkbox.dataset.checkboxFallback as CheckboxState | undefined) ??
              'unchecked',
            row.key,
            this.snapshot,
            this.selectedKeys
          );
          setData(checkbox, 'state', state);
          checkbox.setAttribute(
            'aria-checked',
            state === 'indeterminate' ? 'mixed' : String(state === 'checked')
          );
        });
    };
    this.mounted.forEach((element, index) => update(element, this.rows[index]));
    const stickyIndex = Number(this.sticky.dataset.nativeListRowIndex);
    if (!Number.isNaN(stickyIndex)) update(this.sticky, this.rows[stickyIndex]);
  }

  private updateVisibleState() {
    const visible =
      this.externalScrollHost && !this.externalGeometry().visible
        ? []
        : visibleWebLayoutItems(
            this.layout,
            this.currentOffset(),
            this.viewportLength()
          );
    const first = visible[0];
    const last = visible.at(-1);
    const signature =
      String(first?.index ?? -1) +
      ':' +
      String(last?.index ?? -1) +
      ':' +
      (first?.key ?? '') +
      ':' +
      (last?.key ?? '');
    if (signature !== this.lastVisibleSignature) {
      this.lastVisibleSignature = signature;
      this.callbacks.onVisibleRangeChanged?.({
        firstKey: first?.key,
        lastKey: last?.key,
        firstIndex: first?.index ?? -1,
        lastIndex: last?.index ?? -1,
      });
    }
    this.checkEndReached(last?.index ?? -1);
    this.updateStickyHeader(first?.index ?? -1);
    // OneKey patch: index highlighting follows header positions at scroll boundaries.
    // this.updateSectionIndex(first?.index ?? -1);
    this.updateSectionIndex();
  }

  private updateStickyHeader(firstVisibleIndex: number) {
    if (
      !this.snapshot.layout.stickyHeaders ||
      this.layout.horizontal ||
      firstVisibleIndex < 0
    ) {
      this.sticky.hidden = true;
      this.stickyKey = undefined;
      return;
    }
    let index = -1;
    for (let cursor = firstVisibleIndex; cursor >= 0; cursor -= 1) {
      const row = this.rows[cursor];
      if (
        row?.type === 'sectionHeader' &&
        row.sticky !== false &&
        row.variant !== 'summary'
      ) {
        index = cursor;
        break;
      }
    }
    const row = this.rows[index];
    const item = this.layout.items[index];
    if (!row || row.type !== 'sectionHeader' || !item) {
      this.sticky.hidden = true;
      this.stickyKey = undefined;
      return;
    }
    const signature = webRowRenderSignature(row);
    if (
      this.stickyKey !== row.key ||
      this.sticky.dataset.renderSignature !== signature
    ) {
      this.renderElement(this.sticky, index, row, true);
      this.sticky.dataset.renderSignature = signature;
      this.stickyKey = row.key;
    }
    this.sticky.hidden = false;
    this.sticky.style.left = String(item.x) + 'px';
    this.sticky.style.right = 'auto';
    this.sticky.style.width = String(item.width) + 'px';
    this.sticky.style.height = String(item.height) + 'px';
    if (this.externalScrollHost)
      this.sticky.style.top = String(Math.max(0, this.currentOffset())) + 'px';
    let nextIndex = -1;
    for (let cursor = index + 1; cursor < this.rows.length; cursor += 1) {
      const candidate = this.rows[cursor];
      if (
        candidate?.type === 'sectionHeader' &&
        candidate.sticky !== false &&
        candidate.variant !== 'summary'
      ) {
        nextIndex = cursor;
        break;
      }
    }
    const next = this.layout.items[nextIndex];
    const translate = next
      ? Math.min(0, next.y - this.currentOffset() - item.height)
      : 0;
    this.sticky.style.transform =
      'translate3d(0,' + String(translate) + 'px,0)';
    this.updateVisibleSelection();
  }

  // private updateSectionIndex(firstVisibleIndex: number) {
  private updateSectionIndex() {
    const activeKey = webSectionIndexActiveKey(
      this.snapshot.rows,
      this.layout,
      this.currentOffset(),
      this.viewportLength()
    );
    this.indexRail
      .querySelectorAll<HTMLElement>('[data-section-key]')
      .forEach((button) =>
        setData(button, 'active', button.dataset.sectionKey === activeKey)
      );
  }

  private checkEndReached(lastVisibleIndex: number) {
    if (
      (this.externalScrollHost && !this.externalGeometry().visible) ||
      !this.snapshot.capabilities?.loadMore ||
      this.reachedGeneration === this.snapshot.generation ||
      this.rows.length === 0
    )
      return;
    const threshold = Math.max(
      1,
      Math.ceil(
        this.rows.length *
          (this.snapshot.capabilities.endReachedThreshold ?? 0.2)
      )
    );
    if (lastVisibleIndex < this.rows.length - threshold) return;
    this.reachedGeneration = this.snapshot.generation;
    this.callbacks.onEndReached?.({
      generation: this.snapshot.generation,
      lastKey: this.rows.at(-1)?.key,
    });
  }

  private emitScrollFailure(
    index: number,
    reason: ScrollToIndexFailedInfo['reason']
  ) {
    const average =
      this.layout.items.length === 0
        ? 0
        : this.layout.items.reduce(
            (sum, item) =>
              sum + (this.layout.horizontal ? item.width : item.height),
            0
          ) / this.layout.items.length;
    this.callbacks.onScrollToIndexFailed?.(
      scrollFailure(this.rows, index, reason, average)
    );
  }

  private canScroll(): boolean {
    if (this.externalScrollHost)
      return (
        this.viewport.clientWidth > 0 &&
        this.externalScrollHost.clientHeight > 0
      );
    const viewportLength = this.layout.horizontal
      ? this.viewport.clientWidth
      : this.viewport.clientHeight;
    return viewportLength > 0 && this.layout.items.length > 0;
  }

  private viewportLength(): number {
    if (this.layout.horizontal) {
      return this.viewport.clientWidth || DEFAULT_VIEWPORT_WIDTH;
    }
    if (this.externalScrollHost) return this.externalGeometry().viewportLength;
    if (this.viewport.clientHeight <= 0) return DEFAULT_VIEWPORT_HEIGHT;
    return Math.max(1, this.verticalScrollMetrics().viewportLength);
  }

  private contentLength(): number {
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      return Math.max(
        this.layout.contentHeight,
        geometry.end - geometry.origin
      );
    }
    return this.layout.horizontal
      ? this.layout.contentWidth
      : this.layout.contentHeight;
  }

  private currentOffset(): number {
    return this.layout.horizontal
      ? this.viewport.scrollLeft
      : this.verticalScrollMetrics().offset;
  }

  private collapsiblePagerInsets(): Readonly<{
    header: number;
    sticky: number;
  }> {
    if (this.externalScrollHost)
      return { header: 0, sticky: this.externalStickyInset };
    const readInset = (name: string) => {
      const parsed = Number.parseFloat(
        this.viewport.style.getPropertyValue(name)
      );
      return Number.isFinite(parsed) ? Math.max(0, parsed) : 0;
    };
    return {
      header: readInset('--ok-collapsible-pager-header-inset'),
      sticky: readInset('--ok-collapsible-pager-sticky-inset'),
    };
  }

  private verticalScrollMetrics(): WebCollapsiblePagerScrollMetrics {
    if (this.externalScrollHost) return this.externalGeometry();
    const insets = this.collapsiblePagerInsets();
    return resolveWebCollapsiblePagerScrollMetrics(
      this.viewport.scrollTop,
      this.viewport.clientHeight,
      insets.header,
      insets.sticky
    );
  }

  private handleCollapsiblePagerInsetsChanged = () => {
    this.recomputeLayout();
  };

  private writeExternalOffset(rawOffset: number, animated: boolean) {
    this.externalGeometryCache = undefined;
    const host = this.externalScrollHost!;
    const scrollTo: (options: ScrollToOptions) => void =
      host.ownerDocument.defaultView?.Element.prototype.scrollTo ??
      host.scrollTo;
    scrollTo.call(host, {
      top: Math.max(
        0,
        Math.min(host.scrollHeight - host.clientHeight, rawOffset)
      ),
      behavior: animated ? 'smooth' : 'auto',
    });
    this.scheduleFrame();
  }

  private scrollToAbsoluteOffset(offset: number, animated: boolean) {
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      this.writeExternalOffset(
        geometry.origin + offset - this.externalStickyInset,
        animated
      );
      return;
    }
    const resolved = Math.min(
      Math.max(0, offset),
      Math.max(0, this.contentLength() - this.viewportLength())
    );
    const rawOffset = this.layout.horizontal
      ? resolved
      : resolveWebCollapsiblePagerRawOffset(
          resolved,
          this.collapsiblePagerInsets().header
        );
    this.viewport.scrollTo(
      this.layout.horizontal
        ? { left: rawOffset, top: 0, behavior: animated ? 'smooth' : 'auto' }
        : { left: 0, top: rawOffset, behavior: animated ? 'smooth' : 'auto' }
    );
    this.scheduleFrame();
  }

  private performPendingScroll() {
    const pending = this.pendingScroll;
    if (!pending || !this.canScroll()) return;
    this.pendingScroll = undefined;
    if (pending.kind === 'index')
      this.scrollToIndex(pending.index, pending.scroll);
    else if (pending.kind === 'offset')
      this.scrollToOffset(pending.offset, pending.animated);
    else this.scrollToEnd(pending.animated);
  }

  private rowAtElement(element: Element | null): RowModel | undefined {
    if (!element) return undefined;
    const key = (element as HTMLElement).dataset.nativeListRowKey;
    return (
      this.rows.find((row) => row.key === key) ??
      (this.snapshot.fixedFooter?.key === key
        ? this.snapshot.fixedFooter
        : undefined)
    );
  }

  private rowLabel(row: RowModel): string {
    if (row.type === 'walletGroup') return row.parent.title;
    if ('title' in row) return row.title;
    if ('message' in row && row.message) return row.message;
    return row.type;
  }

  private isReorderable(row: RowModel): boolean {
    return isWebRowReorderable(this.snapshot, row);
  }

  private activateSelection(target: SelectionTarget, sourceRow: RowModel) {
    const action =
      target.scope === 'row'
        ? { scope: 'row' as const, key: sourceRow.key }
        : target;
    const result = reduceSelection(
      {
        mode: this.snapshot.selection?.mode ?? 'none',
        selectedKeys: this.selectedKeys,
      },
      action,
      this.snapshot.rows
    );
    if (
      result.delta.addedKeys.length === 0 &&
      result.delta.removedKeys.length === 0
    )
      return;
    this.selectedKeys = result.state.selectedKeys;
    this.updateVisibleSelection();
    this.renderFooter();
    this.callbacks.onSelectionDelta?.(result.delta);
  }

  private emitRowAction(
    row: RowModel,
    actionKey: string,
    actionElement?: HTMLElement,
    rowElement?: HTMLElement,
    windowPoint?: RowActionEvent['windowPoint']
  ) {
    const anchor =
      actionElement && rowElement
        ? this.createActionAnchor(actionElement, rowElement)
        : undefined;
    this.callbacks.onRowAction?.({
      rowKey: row.key,
      actionKey,
      sectionKey: row.sectionKey,
      anchor,
      ...(windowPoint &&
      Number.isFinite(windowPoint.x) &&
      Number.isFinite(windowPoint.y)
        ? { windowPoint }
        : {}),
    });
  }

  private createActionAnchor(
    actionElement: HTMLElement,
    rowElement: HTMLElement,
    sourceOverride?: NativeListActionSource
  ): NativeListActionAnchor | undefined {
    const source =
      sourceOverride ??
      (actionElement.dataset.nativeListAnchorSource as
        | NativeListActionSource
        | undefined);
    const bindingEpoch = rowElement.dataset.nativeListBindingEpoch;
    if (!source || !bindingEpoch || !rowElement.contains(actionElement))
      return undefined;
    this.invalidateActionAnchor('rebind');
    const actualRect = actionElement.getBoundingClientRect();
    const inset = Number(actionElement.dataset.nativeListAnchorInset ?? 0);
    const rect = {
      left: actualRect.left + inset,
      top: actualRect.top + inset,
      width: actualRect.width - inset * 2,
      height: actualRect.height - inset * 2,
    };
    const token = [
      this.actionAnchorInstanceId,
      this.snapshot.generation,
      ++this.actionAnchorCounter,
      bindingEpoch,
    ].join(':');
    const slotValue = actionElement.dataset.nativeListAnchorSlot;
    const direction =
      this.document.defaultView?.getComputedStyle(actionElement).direction ===
        'rtl' || actionElement.closest('[dir="rtl"]')
        ? 'rtl'
        : 'ltr';
    this.actionAnchor = {
      token,
      actionElement,
      rowElement,
      bindingEpoch,
      open: false,
    };
    return webActionAnchorPayload(
      token,
      rect,
      source,
      this.snapshot.generation,
      direction,
      slotValue === undefined ? undefined : Number(slotValue)
    );
  }

  private isActionAnchorValid(anchor: WebActionAnchorRecord): boolean {
    return (
      anchor.actionElement.isConnected &&
      anchor.rowElement.isConnected &&
      anchor.rowElement.contains(anchor.actionElement) &&
      anchor.rowElement.dataset.nativeListBindingEpoch === anchor.bindingEpoch
    );
  }

  private invalidateActionAnchorForElement(element: HTMLElement) {
    if (this.pressedHelpElement && element.contains(this.pressedHelpElement))
      this.pressedHelpElement = undefined;
    const anchor = this.actionAnchor;
    if (
      anchor &&
      (element === anchor.rowElement || element.contains(anchor.actionElement))
    ) {
      this.invalidateActionAnchor('rebind');
    }
  }

  private invalidateActionAnchor(
    reason: ActionAnchorInvalidatedEvent['reason']
  ) {
    const anchor = this.actionAnchor;
    if (!anchor || anchor.invalidatedReason) return;
    anchor.invalidatedReason = reason;
    anchor.actionElement.removeAttribute('aria-expanded');
    if (anchor.open) this.emitActionAnchorInvalidated(anchor, reason);
  }

  private emitActionAnchorInvalidated(
    anchor: WebActionAnchorRecord,
    reason: ActionAnchorInvalidatedEvent['reason']
  ) {
    if (this.actionAnchor !== anchor) return;
    this.actionAnchor = undefined;
    this.callbacks.onActionAnchorInvalidated?.({ token: anchor.token, reason });
  }

  private handleRowPress(
    row: RowModel,
    rowElement?: HTMLElement,
    sourceElement = rowElement
  ) {
    // OneKey patch: missing-address rows keep their create-address accessory interactive.
    // if (row.disabled) return;
    if (!isRowPressEnabled(row)) return;
    if (
      this.snapshot.selection?.rowPressToggles &&
      this.snapshot.selection.mode !== 'none' &&
      isSelectableRow(row)
    ) {
      this.activateSelection({ scope: 'row' }, row);
      return;
    }
    const actionKey =
      row.type === 'action'
        ? row.actionKey
        : row.type === 'system' && row.variant === 'retry'
        ? row.actionKey
        : row.type === 'market' && row.pressActionKey
        ? row.pressActionKey
        : 'press';
    if (rowElement && sourceElement) {
      const anchor = this.createActionAnchor(sourceElement, rowElement, 'row');
      this.callbacks.onRowAction?.({
        rowKey: row.key,
        actionKey,
        sectionKey: row.sectionKey,
        anchor,
      });
    } else {
      this.emitRowAction(row, actionKey);
    }
  }

  private cancelMarketPointer() {
    const state = this.marketPointer;
    if (!state) return;
    this.document.defaultView?.clearTimeout(state.timer);
    this.marketPointer = undefined;
  }

  private handleMarketContextMenu = (event: MouseEvent) => {
    if (
      !(event.target instanceof Element) ||
      event.target.closest('[data-native-list-action]')
    )
      return;
    const element = event.target.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(element);
    if (
      row?.type !== 'market' ||
      !row.webTable?.contextMenuActionKey ||
      !element ||
      !isRowPressEnabled(row)
    )
      return;
    event.preventDefault();
    this.cancelMarketPointer();
    markActionAnchorSource(element, 'row');
    const rect = element.getBoundingClientRect();
    this.emitRowAction(
      row,
      row.webTable.contextMenuActionKey,
      element,
      element,
      event.clientX || event.clientY
        ? { x: event.clientX, y: event.clientY }
        : { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 }
    );
  };

  private handleMarketPointerDown = (event: PointerEvent) => {
    if (event.button !== 0 || !(event.target instanceof Element)) return;
    this.marketLongPressFired = false;
    if (event.target.closest('[data-native-list-action]')) return;
    const rowElement = event.target.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(rowElement);
    if (
      (row?.type !== 'market' && row?.type !== 'identity') ||
      !isRowPressEnabled(row)
    )
      return;
    this.cancelMarketPointer();
    if (row.type === 'market' && row.pressInActionKey)
      this.emitRowAction(
        row,
        row.pressInActionKey,
        rowElement ?? undefined,
        rowElement ?? undefined
      );
    if (!row.longPressActionKey || !rowElement) return;
    markActionAnchorSource(rowElement, 'row');
    const bindingEpoch = rowElement.dataset.nativeListBindingEpoch;
    const timer = this.document.defaultView?.setTimeout(() => {
      const current = this.marketPointer;
      if (!current || current.pointerId !== event.pointerId) return;
      const currentRow = this.rowAtElement(rowElement);
      this.marketPointer = undefined;
      if (
        !rowElement.isConnected ||
        rowElement.dataset.nativeListBindingEpoch !== bindingEpoch ||
        currentRow?.key !== row.key ||
        (currentRow.type !== 'market' && currentRow.type !== 'identity') ||
        !isRowPressEnabled(currentRow) ||
        !currentRow.longPressActionKey
      )
        return;
      this.marketLongPressFired = true;
      this.emitRowAction(
        currentRow,
        currentRow.longPressActionKey,
        rowElement,
        rowElement,
        { x: event.clientX, y: event.clientY }
      );
    }, MARKET_LONG_PRESS_MS);
    if (timer === undefined) return;
    this.marketPointer = {
      pointerId: event.pointerId,
      pointerType: event.pointerType,
      compact: row.type === 'identity' || !row.webTable,
      rowKey: row.key,
      startX: event.clientX,
      startY: event.clientY,
      timer,
    };
  };

  private handleMarketPointerMove = (event: PointerEvent) => {
    const state = this.marketPointer;
    if (!state || state.pointerId !== event.pointerId) return;
    if (state.compact && state.pointerType === 'touch') return;
    const row = this.rows.find((item) => item.key === state.rowKey);
    const threshold =
      row?.type === 'market' && !row.webTable ? 3 : MARKET_MOVE_CANCEL_PX;
    if (
      Math.abs(event.clientX - state.startX) > threshold ||
      Math.abs(event.clientY - state.startY) > threshold
    )
      this.cancelMarketPointer();
  };

  private handleMarketTouchMove = (event: TouchEvent) => {
    const state = this.marketPointer;
    if (
      !state?.compact ||
      state.pointerType !== 'touch' ||
      !(event.target instanceof Element) ||
      event.target.closest<HTMLElement>('[data-native-list-row-key]')?.dataset
        .nativeListRowKey !== state.rowKey
    )
      return;
    if (event.touches.length > 1) {
      this.cancelMarketPointer();
      return;
    }
    const touch = event.touches[0] ?? event.changedTouches[0];
    if (
      !touch ||
      !Number.isFinite(touch.clientX) ||
      !Number.isFinite(touch.clientY)
    )
      return;
    if (
      Math.abs(touch.clientX - state.startX) > 3 ||
      Math.abs(touch.clientY - state.startY) > 3
    )
      this.cancelMarketPointer();
  };

  private handleMarketPointerEnd = (event: PointerEvent) => {
    if (this.marketPointer?.pointerId === event.pointerId)
      this.cancelMarketPointer();
  };

  // OneKey patch: hover opens the same anchored help action as native taps.
  private handleTitlePointerOver = (event: PointerEvent | FocusEvent) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const action = target.closest<HTMLElement>(
      '[data-native-list-hover-action]'
    );
    if (
      !action ||
      (event.relatedTarget instanceof Node &&
        action.contains(event.relatedTarget))
    )
      return;
    if (action.dataset.nativeListIconHelp === 'true') {
      if (
        event.type === 'pointerover' &&
        (event as PointerEvent).pointerType === 'touch'
      )
        return;
      if (this.pressedHelpElement === action) return;
    }
    if (
      action.classList.contains('ok-native-list-market-subtitle-prefix') &&
      action.scrollWidth <= action.clientWidth + 1
    )
      return;
    this.emitHoverAction(action);
  };

  private emitHoverAction(action: HTMLElement) {
    const rowElement = action.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(rowElement);
    const memberKey = action.closest<HTMLElement>(
      '[data-native-list-group-member-key]'
    )?.dataset.nativeListGroupMemberKey;
    const sourceRow =
      row?.type === 'walletGroup'
        ? [row.parent, ...row.children].find(
            (member) => member.key === memberKey
          ) ?? row
        : row;
    const actionKey =
      action.dataset.nativeListHoverAction === 'true'
        ? action.dataset.nativeListAction
        : action.dataset.nativeListHoverAction;
    // Legacy Web: hover checks only the resolved source (the member for a
    // WalletGroup), not the enclosing row's `disabled`.
    if (sourceRow && !sourceRow.disabled && actionKey)
      this.emitRowAction(sourceRow, actionKey, action, rowElement ?? undefined);
  }

  private handleIconHelpPointerDown = (event: PointerEvent) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const action = target.closest<HTMLElement>(
      '[data-native-list-icon-help="true"]'
    );
    if (!action) return;
    if (event.pointerType === 'touch') {
      if (this.actionAnchor?.actionElement === action && this.actionAnchor.open)
        this.invalidateActionAnchor('pointerLeave');
      else this.emitHoverAction(action);
    } else {
      this.pressedHelpElement = action;
      if (this.actionAnchor?.actionElement === action)
        this.invalidateActionAnchor('pointerLeave');
    }
  };

  private handleTitlePointerOut = (event: PointerEvent | FocusEvent) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const action = target.closest<HTMLElement>(
      '[data-native-list-hover-action]'
    );
    if (
      !action ||
      (event.relatedTarget instanceof Node &&
        action.contains(event.relatedTarget))
    )
      return;
    if (
      action.dataset.nativeListIconHelp === 'true' &&
      event.type === 'pointerout' &&
      (event as PointerEvent).pointerType === 'touch'
    )
      return;
    if (event.type === 'pointerout' && this.pressedHelpElement === action)
      this.pressedHelpElement = undefined;
    if (this.actionAnchor?.actionElement === action)
      this.invalidateActionAnchor('pointerLeave');
  };

  private handleClick = (event: Event) => {
    if (this.marketLongPressFired || Date.now() < this.suppressClickUntil) {
      this.marketLongPressFired = false;
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    const target = event.target;
    if (!(target instanceof Element)) return;
    if (target.closest('[data-native-list-icon-help="true"]')) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(rowElement);
    if (!row || row.disabled) return;
    const memberElement = target.closest<HTMLElement>(
      '[data-native-list-group-member-key]'
    );
    const memberKey = memberElement?.dataset.nativeListGroupMemberKey;
    const sourceRow =
      row.type === 'walletGroup' && memberKey
        ? [row.parent, ...row.children].find(
            (member) => member.key === memberKey
          ) ?? row
        : row;
    // Legacy Web: a disabled row (or whole WalletGroup) blocks everything
    // above. A disabled WalletGroup member still emits its own actions, but its
    // member press is blocked by `isRowPressEnabled` in `handleRowPress`.
    const action = target.closest<HTMLElement>('[data-native-list-action]');
    if (action) {
      if (action.tagName === 'BUTTON' && action.hasAttribute('disabled'))
        return;
      if (action.classList.contains('ok-native-list-trailing-button'))
        event.stopPropagation();
      const scope = action.dataset.selectionScope;
      if (scope === 'row') this.activateSelection({ scope: 'row' }, row);
      else if (scope === 'section' && action.dataset.selectionKey)
        this.activateSelection(
          { scope: 'section', sectionKey: action.dataset.selectionKey },
          row
        );
      else if (scope === 'list') this.activateSelection({ scope: 'list' }, row);
      else if (action.dataset.nativeListAction)
        this.emitRowAction(
          sourceRow,
          action.dataset.nativeListAction,
          action,
          rowElement ?? undefined
        );
      return;
    }
    this.handleRowPress(
      sourceRow,
      rowElement ?? undefined,
      memberElement ?? rowElement ?? undefined
    );
  };

  private handleKeyDown = (event: KeyboardEvent) => {
    if (
      (event.key === 'Enter' || event.key === ' ') &&
      event.target instanceof HTMLElement &&
      event.target.matches('[role="button"][data-native-list-hover-action]')
    ) {
      event.preventDefault();
      if (
        this.actionAnchor?.actionElement !== event.target ||
        !this.actionAnchor.open
      )
        this.emitHoverAction(event.target);
      return;
    }
    // OneKey patch: non-button title help supports keyboard activation.
    if (
      (event.key === 'Enter' || event.key === ' ') &&
      event.target instanceof HTMLElement &&
      event.target.matches('[role="button"][data-native-list-action]')
    ) {
      event.preventDefault();
      event.target.click();
      return;
    }
    if (event.key === 'Escape') {
      if (this.pointerReorder?.active) {
        event.preventDefault();
        this.cancelPointerReorder(true);
        return;
      }
      if (this.keyboardReorder) {
        event.preventDefault();
        this.cancelKeyboardReorder();
        return;
      }
    }
    if (
      this.keyboardReorder &&
      (event.key === 'ArrowUp' || event.key === 'ArrowLeft')
    ) {
      event.preventDefault();
      this.moveKeyboardReorder(-1);
      return;
    }
    if (
      this.keyboardReorder &&
      (event.key === 'ArrowDown' || event.key === 'ArrowRight')
    ) {
      event.preventDefault();
      this.moveKeyboardReorder(1);
      return;
    }
    if (event.key !== 'Enter' && event.key !== ' ') return;
    const target = event.target;
    if (!(target instanceof HTMLElement)) return;
    if (target.matches('button')) return;
    const row = this.rowAtElement(target.closest('[data-native-list-row-key]'));
    if (!row) return;
    event.preventDefault();
    if (event.key === ' ' && this.isReorderable(row)) {
      if (this.keyboardReorder) this.finishKeyboardReorder();
      else this.startKeyboardReorder(row);
      return;
    }
    this.handleRowPress(
      row,
      target.closest<HTMLElement>('[data-native-list-row-key]') ?? undefined
    );
  };

  private startKeyboardReorder(row: RowModel) {
    this.keyboardReorder = {
      sourceKey: row.key,
      originalRows: this.snapshot.rows,
    };
    this.updateReorderVisualState();
  }

  private moveKeyboardReorder(delta: -1 | 1) {
    const state = this.keyboardReorder;
    if (!state) return;
    const fromIndex = this.snapshot.rows.findIndex(
      (row) => row.key === state.sourceKey
    );
    const toIndex = fromIndex + delta;
    const from = this.snapshot.rows[fromIndex];
    const to = this.snapshot.rows[toIndex];
    if (
      !from ||
      !to ||
      !this.isReorderable(to) ||
      from.sectionKey !== to.sectionKey
    )
      return;
    const rows = moveWebReorderRow(this.snapshot.rows, fromIndex, toIndex);
    this.remapMountedRows(rows);
    this.setSnapshot({ ...this.snapshot, rows }, this.selectedKeys);
    this.updateReorderVisualState();
    this.scrollToIndex(toIndex, {
      animated: false,
      alignment: 'nearest',
      viewPosition: 0,
      viewOffset: 0,
    });
    [...this.mounted.values()]
      .find((element) => element.dataset.nativeListRowKey === state.sourceKey)
      ?.focus({ preventScroll: true });
  }

  private finishKeyboardReorder() {
    const state = this.keyboardReorder;
    if (!state) return;
    const reorderEvent = webReorderEventForRows(
      state.originalRows,
      this.snapshot.rows,
      state.sourceKey
    );
    this.keyboardReorder = undefined;
    this.updateReorderVisualState();
    if (reorderEvent) this.callbacks.onReorder?.(reorderEvent);
  }

  private cancelKeyboardReorder() {
    const state = this.keyboardReorder;
    if (!state) return;
    this.keyboardReorder = undefined;
    this.remapMountedRows(state.originalRows);
    this.setSnapshot(
      { ...this.snapshot, rows: cancelWebReorderRows(state.originalRows) },
      this.selectedKeys
    );
    this.updateReorderVisualState();
    [...this.mounted.values()]
      .find((element) => element.dataset.nativeListRowKey === state.sourceKey)
      ?.focus({ preventScroll: true });
  }

  private handleExternalScroll = (event: Event) => {
    const target = event.target;
    if (
      target === this.externalScrollHost ||
      (target instanceof Element &&
        target !== this.root &&
        this.externalScrollHost?.contains(target) &&
        target.contains(this.root))
    )
      this.handleScroll();
  };

  private handleScroll = () => {
    this.externalGeometryCache = undefined;
    this.pressedHelpElement = undefined;
    this.cancelMarketPointer();
    this.invalidateActionAnchor('scroll');
    this.scheduleFrame();
  };

  private handleWindowResize = () => {
    const state = this.pointerReorder;
    if (state?.active) state.viewportRect = this.reorderViewportRect();
    this.recomputeLayout();
  };

  private scheduleFrame() {
    if (this.frameHandle !== undefined || this.destroyed) return;
    this.frameHandle = this.requestFrame(() => {
      this.frameHandle = undefined;
      this.externalGeometryCache = undefined;
      if (this.externalScrollHost)
        this.externalOrigin = this.externalGeometry().origin;
      if (this.virtualizationEnabled) this.renderWindow();
      else {
        this.updateAvatarWindow();
        this.updateVisibleState();
      }
    });
  }

  private requestFrame(callback: FrameRequestCallback): number {
    const view = this.document.defaultView;
    return view?.requestAnimationFrame
      ? view.requestAnimationFrame(callback)
      : view?.setTimeout(() => callback(Date.now()), 16) ?? 0;
  }

  private cancelFrame(handle: number) {
    const view = this.document.defaultView;
    if (view?.cancelAnimationFrame) view.cancelAnimationFrame(handle);
    else view?.clearTimeout(handle);
  }

  private selectIndexPosition(position: number) {
    const activeKey = this.sectionIndexEntries.find(
      (entry) => entry.position === position
    )?.key;
    this.indexRail
      .querySelectorAll<HTMLElement>('[data-section-key]')
      .forEach((button) =>
        setData(button, 'active', button.dataset.sectionKey === activeKey)
      );
    this.scrollToIndex(position, {
      animated: false,
      alignment: 'start',
      viewPosition: 0,
      viewOffset: 0,
    });
  }

  private sectionIndexEntryAtEvent(
    event: PointerEvent
  ): NativeListWebEngine['sectionIndexEntries'][number] | undefined {
    const rail = this.indexRail.getBoundingClientRect();
    if (this.sectionIndexEntries.length === 0 || rail.height <= 0) {
      return undefined;
    }
    const metrics = this.sectionIndexMetrics(rail.height);
    if (metrics.trackHeight <= 0) return undefined;
    const progress = Math.min(
      1,
      Math.max(
        0,
        (event.clientY - rail.top - metrics.originY) / metrics.trackHeight
      )
    );
    const entryIndex = Math.min(
      this.sectionIndexEntries.length - 1,
      Math.floor(progress * this.sectionIndexEntries.length)
    );
    return this.sectionIndexEntries[entryIndex];
  }

  private handleIndexPointer = (event: PointerEvent) => {
    if (event.type === 'pointermove' && event.buttons === 0) return;
    const entry = this.sectionIndexEntryAtEvent(event);
    if (!entry) return;
    event.preventDefault();
    if (event.type === 'pointerdown') {
      this.indexRail.setPointerCapture?.(event.pointerId);
    }
    this.selectIndexPosition(entry.position);
  };

  private handleIndexPointerEnd = (event: PointerEvent) => {
    if (event.type === 'pointerup') {
      const entry = this.sectionIndexEntryAtEvent(event);
      if (entry) this.selectIndexPosition(entry.position);
    }
  };

  private handleIndexClick = (event: Event) => {
    if (
      'detail' in event &&
      typeof event.detail === 'number' &&
      event.detail > 0
    )
      return;
    const target = event.target;
    if (!(target instanceof Element)) return;
    const button = target.closest<HTMLElement>('[data-section-entry-index]');
    if (!button) return;
    const entry =
      this.sectionIndexEntries[Number(button.dataset.sectionEntryIndex)];
    if (!entry) return;
    this.selectIndexPosition(entry.position);
  };

  private handlePullStart = (event: PointerEvent) => {
    if (
      this.pointerReorder?.pointerId === event.pointerId ||
      !this.snapshot.capabilities?.pullToRefresh ||
      (!!this.externalScrollHost && !this.ownsRefresh) ||
      this.pullTarget.scrollTop > 0 ||
      (event.pointerType !== 'touch' && event.pointerType !== 'pen')
    )
      return;
    this.pullStartY = event.clientY;
    this.pullDistance = 0;
  };

  private handlePullMove = (event: PointerEvent) => {
    if (this.pullStartY === undefined) return;
    this.pullDistance = Math.max(
      0,
      Math.min(96, event.clientY - this.pullStartY)
    );
    if (this.pullDistance <= 0) return;
    event.preventDefault();
    this.refreshIndicator.textContent =
      this.pullDistance >= 64 ? 'Release to refresh' : 'Pull to refresh';
    setData(this.refreshIndicator, 'visible', true);
    this.refreshIndicator.style.transform =
      'translate(-50%,' + String(Math.min(32, this.pullDistance / 2)) + 'px)';
  };

  private handlePullEnd = () => {
    if (this.pullStartY === undefined) return;
    const shouldRefresh = this.pullDistance >= 64;
    this.pullStartY = undefined;
    this.pullDistance = 0;
    this.refreshIndicator.style.removeProperty('transform');
    this.updateRefreshIndicator();
    if (!shouldRefresh) return;
    this.callbacks.onRefresh?.();
    this.callbacks.onRowAction?.({ actionKey: 'nativeList.refresh' });
  };

  private updateRefreshIndicator() {
    const refreshing = Boolean(this.snapshot.capabilities?.refreshing);
    this.refreshIndicator.textContent = refreshing ? 'Refreshing' : '';
    setData(this.refreshIndicator, 'visible', refreshing);
  }

  private handleReorderPointerDown = (event: PointerEvent) => {
    if (
      this.pointerReorder ||
      this.keyboardReorder ||
      event.isPrimary === false ||
      (event.pointerType === 'mouse' && event.button !== 0)
    ) {
      return;
    }
    const target = event.target;
    if (!(target instanceof Element)) return;
    if (target.closest('button,input,textarea,select,a')) return;
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-index]'
    );
    const index = Number(rowElement?.dataset.nativeListRowIndex);
    const row = this.rows[index];
    if (!row || !this.isReorderable(row)) return;
    const memberKey = target.closest<HTMLElement>(
      '[data-native-list-group-member-key]'
    )?.dataset.nativeListGroupMemberKey;
    if (!canStartWebWalletGroupReorder(row, memberKey)) return;
    // OneKey patch: a child drag reorders its parent wallet group as one item.
    // if (
    // row.type === 'walletGroup' &&
    // target.closest<HTMLElement>('[data-native-list-group-parent]')?.dataset
    // .nativeListGroupParent !== 'true'
    // )
    // return;

    const view = this.document.defaultView;
    const state: PointerReorderState = {
      pointerId: event.pointerId,
      pointerType: event.pointerType || 'mouse',
      sourceKey: row.key,
      currentIndex: index,
      startX: event.clientX,
      startY: event.clientY,
      clientX: event.clientX,
      clientY: event.clientY,
      originalRows: this.snapshot.rows,
      workingRows: [...this.snapshot.rows],
      active: false,
    };
    this.pointerReorder = state;
    // OneKey patch: normal wallet taps retain their target until a drag is activated.
    if (state.pointerType !== 'mouse') {
      state.longPressTimer = view?.setTimeout(
        () => this.activatePointerReorder(state),
        REORDER_TOUCH_LONG_PRESS_MS
      );
    }
  };

  private activatePointerReorder(state: PointerReorderState) {
    if (this.pointerReorder !== state) return;
    this.clearReorderLongPress(state);
    state.active = true;
    this.cancelMarketPointer();
    state.activatedAt = Date.now();
    state.viewportRect = this.reorderViewportRect();
    this.captureReorderPointer(state);
    this.showReorderPreview(state);
    if (
      state.workingRows[state.currentIndex]?.type === 'walletGroup' &&
      this.reorderCompactKey !== state.sourceKey
    ) {
      this.reorderCompactKey = state.sourceKey;
      this.mounted.forEach((element) =>
        setData(element, 'nativeListAnimateReorder', true)
      );
      this.recomputeLayout();
    }
    this.updateReorderVisualState();
    this.scheduleReorderAutoScroll();
  }

  private captureReorderPointer(state: PointerReorderState) {
    try {
      this.viewport.setPointerCapture?.(state.pointerId);
    } catch {
      // A canceled pointer can disappear before the long-press timer fires.
    }
  }

  private releaseReorderPointer(state: PointerReorderState) {
    try {
      if (this.viewport.hasPointerCapture?.(state.pointerId))
        this.viewport.releasePointerCapture?.(state.pointerId);
    } catch {
      // The browser may have released capture while dispatching pointercancel.
    }
  }

  private clearReorderLongPress(state: PointerReorderState) {
    if (state.longPressTimer === undefined) return;
    this.document.defaultView?.clearTimeout(state.longPressTimer);
    state.longPressTimer = undefined;
  }

  private handleReorderPointerMove = (event: PointerEvent) => {
    const state = this.pointerReorder;
    if (!state || state.pointerId !== event.pointerId) return;
    state.clientX = event.clientX;
    state.clientY = event.clientY;

    if (!state.active) {
      if (state.pointerType !== 'mouse') {
        this.cancelPointerReorder(false);
        return;
      }
      if (
        !hasExceededWebReorderMouseThreshold(
          event.clientX - state.startX,
          event.clientY - state.startY
        )
      )
        return;
      this.activatePointerReorder(state);
      if (!state.active) return;
      event.preventDefault();
      this.scheduleReorderMove();
      this.scheduleReorderAutoScroll();
      return;
    }

    event.preventDefault();
    this.scheduleReorderMove();
    this.scheduleReorderAutoScroll();
  };

  private handleReorderTouchMove = (event: TouchEvent) => {
    const state = this.pointerReorder;
    if (!state?.active || state.pointerType === 'mouse') return;
    const touch = event.touches[0];
    if (!touch) return;
    event.preventDefault();
    state.clientX = touch.clientX;
    state.clientY = touch.clientY;
    this.scheduleReorderMove();
    this.scheduleReorderAutoScroll();
  };

  private handleReorderTouchEnd = (event: TouchEvent) => {
    const state = this.pointerReorder;
    if (!state?.active || state.pointerType === 'mouse') return;
    event.preventDefault();
    this.completePointerReorder(state);
  };

  private handleReorderTouchCancel = () => {
    const state = this.pointerReorder;
    if (!state?.active || state.pointerType === 'mouse') return;
    this.cancelPointerReorder(true);
  };

  private setCurrentOffset(offset: number) {
    if (this.externalScrollHost) {
      this.scrollToAbsoluteOffset(offset, false);
      return;
    }
    const maximum = Math.max(0, this.contentLength() - this.viewportLength());
    const next = Math.max(0, Math.min(maximum, offset));
    if (this.layout.horizontal) this.viewport.scrollLeft = next;
    else {
      this.viewport.scrollTop = resolveWebCollapsiblePagerRawOffset(
        next,
        this.collapsiblePagerInsets().header
      );
    }
    this.scheduleFrame();
  }

  private showReorderPreview(state: PointerReorderState) {
    this.hideReorderPreview();
    const source = [...this.mounted.values()].find(
      (element) => element.dataset.nativeListRowKey === state.sourceKey
    );
    const row = source?.firstElementChild;
    if (!source || !row) return;
    const rect = source.getBoundingClientRect();
    const sourceRow = state.workingRows[state.currentIndex];
    const preview =
      sourceRow && rowRenderer(sourceRow).reorderPreview(row as HTMLElement);
    const previewRow = preview?.element ?? row;
    const previewHeight = preview?.height ?? rect.height;
    state.previewOffsetX = state.startX - rect.left;
    state.previewOffsetY = Math.min(
      previewHeight,
      Math.max(0, state.startY - rect.top)
    );
    this.reorderPreview.replaceChildren(previewRow.cloneNode(true));
    // Cloned previews need their own lease when a source row is recycled during dragging.
    const originals = previewRow.querySelectorAll('img');
    this.reorderPreview.querySelectorAll('img').forEach((image, index) => {
      const original = originals.item(index);
      const avatar = original ? webAvatarSources.get(original) : undefined;
      if (!avatar) return;
      const paint = image.previousElementSibling;
      if (
        paint?.classList.contains('ok-native-list-selector-image-background')
      ) {
        image.addEventListener('load', () => {
          (paint as HTMLElement).style.backgroundImage =
            'url(' + JSON.stringify(image.currentSrc || image.src) + ')';
        });
      }
      configureWebAvatar(image, avatar.source, avatar.uri);
    });
    const badgeText = sourceRow
      ? webWalletGroupReorderBadge(sourceRow)
      : undefined;
    if (badgeText) {
      this.reorderPreview.appendChild(
        createElement(
          this.document,
          'span',
          'ok-native-list-reorder-count',
          badgeText
        )
      );
    }
    setData(
      this.reorderPreview,
      'nativeListSelected',
      source.dataset.nativeListSelected === 'true'
    );
    this.reorderPreview.style.width = String(rect.width) + 'px';
    this.reorderPreview.style.height = String(previewHeight) + 'px';
    this.reorderPreview.style.transition = 'none';
    this.reorderPreview.hidden = false;
    this.updateReorderPreview(state);
  }

  private animateReorderPreviewToCurrent(state: PointerReorderState) {
    const destination = [...this.mounted.values()].find(
      (element) => element.dataset.nativeListRowKey === state.sourceKey
    );
    const destinationIndex = this.rows.findIndex(
      (row) => row.key === state.sourceKey
    );
    const destinationLayout = this.layout.items.find(
      (item) => item.index === destinationIndex
    );
    if (this.reorderPreview.hidden || !destination || !destinationLayout) {
      this.hideReorderPreview();
      return;
    }
    this.reorderSettlingKey = state.sourceKey;
    const viewportRect = this.viewport.getBoundingClientRect();
    const insets = this.collapsiblePagerInsets();
    const left =
      viewportRect.left + destinationLayout.x - this.viewport.scrollLeft;
    const top = this.externalScrollHost
      ? viewportRect.top + destinationLayout.y
      : viewportRect.top +
        destinationLayout.y +
        insets.header +
        insets.sticky -
        this.viewport.scrollTop;
    this.reorderPreview.getBoundingClientRect();
    this.reorderPreview.style.transition =
      'transform ' +
      String(REORDER_DROP_TRANSITION_MS) +
      'ms ' +
      WEB_REORDER_ANIMATION.dropTimingFunction +
      ', box-shadow ' +
      String(REORDER_DROP_TRANSITION_MS) +
      'ms ' +
      WEB_REORDER_ANIMATION.dropTimingFunction;
    this.reorderPreview.style.boxShadow = 'none';
    this.reorderPreview.style.transform =
      'translate3d(' + String(left) + 'px,' + String(top) + 'px,0) scale(1)';
    this.reorderDropTimer = this.document.defaultView?.setTimeout(() => {
      this.reorderDropTimer = undefined;
      this.finishReorderDrop();
    }, REORDER_DROP_TRANSITION_MS);
  }

  private clearReorderPreviewVisual() {
    this.reorderPreview.hidden = true;
    disposeWebImageRetries(this.reorderPreview);
    this.reorderPreview.replaceChildren();
    this.reorderPreview.style.removeProperty('transform');
    this.reorderPreview.style.removeProperty('transition');
    this.reorderPreview.style.removeProperty('box-shadow');
    this.reorderPreview.removeAttribute('data-native-list-selected');
  }

  private finishReorderDrop() {
    this.clearReorderPreviewVisual();
    this.reorderSettlingKey = undefined;
    if (this.reorderCompactKey) {
      this.reorderExpandingKey = this.reorderCompactKey;
      this.reorderCompactKey = undefined;
      this.mounted.forEach((element) =>
        setData(element, 'nativeListAnimateReorder', true)
      );
      this.recomputeLayout();
    }
    this.updateReorderVisualState();
  }

  private hideReorderPreview() {
    if (this.reorderDropTimer !== undefined) {
      this.document.defaultView?.clearTimeout(this.reorderDropTimer);
      this.reorderDropTimer = undefined;
    }
    this.clearReorderPreviewVisual();
    this.reorderSettlingKey = undefined;
    this.reorderExpandingKey = undefined;
    if (this.reorderCompactKey) {
      this.reorderCompactKey = undefined;
      this.recomputeLayout();
    }
  }

  private updateReorderPreview(state: PointerReorderState) {
    if (
      this.reorderPreview.hidden ||
      state.previewOffsetX === undefined ||
      state.previewOffsetY === undefined
    )
      return;
    const x = state.clientX - state.previewOffsetX;
    const y = state.clientY - state.previewOffsetY;
    this.reorderPreview.style.transform =
      'translate3d(' + String(x) + 'px,' + String(y) + 'px,0)';
  }

  private reorderViewportRect() {
    if (this.externalScrollHost) {
      const geometry = this.externalGeometry();
      const body = this.viewport.getBoundingClientRect();
      return {
        left: body.left,
        right: body.right,
        width: body.width,
        top: geometry.top,
        bottom: geometry.bottom,
        height: geometry.viewportLength,
      };
    }
    const { left, top, right, bottom, width, height } =
      this.viewport.getBoundingClientRect();
    return { left, top, right, bottom, width, height };
  }

  private scheduleReorderMove() {
    if (this.reorderMoveFrame !== undefined || !this.pointerReorder?.active)
      return;
    this.reorderMoveFrame = this.requestFrame(this.runReorderMove);
  }

  private runReorderMove = () => {
    this.reorderMoveFrame = undefined;
    const state = this.pointerReorder;
    if (!state?.active) return;
    this.updateReorderPreview(state);
    this.movePointerReorderTo(state.clientX, state.clientY);
  };

  private flushReorderMove(state: PointerReorderState) {
    if (this.reorderMoveFrame !== undefined) {
      this.cancelFrame(this.reorderMoveFrame);
      this.reorderMoveFrame = undefined;
    }
    if (this.pointerReorder !== state || !state.active) return;
    this.updateReorderPreview(state);
    this.movePointerReorderTo(state.clientX, state.clientY);
  }

  private stopReorderMove() {
    if (this.reorderMoveFrame === undefined) return;
    this.cancelFrame(this.reorderMoveFrame);
    this.reorderMoveFrame = undefined;
  }

  private reorderIndexAtPointer(
    state: PointerReorderState,
    clientX: number,
    clientY: number
  ): number {
    const rect = state.viewportRect ?? this.reorderViewportRect();
    const local = this.layout.horizontal
      ? Math.max(0, Math.min(Math.max(0, rect.width - 1), clientX - rect.left))
      : Math.max(0, Math.min(Math.max(0, rect.height - 1), clientY - rect.top));
    const coordinate = this.currentOffset() + local;
    const centerAt = (index: number) => {
      const item = this.layout.items[index];
      return item
        ? (itemStart(item, this.layout.horizontal) +
            itemEnd(item, this.layout.horizontal)) /
            2
        : Number.POSITIVE_INFINITY;
    };
    let low = 0;
    let high = this.layout.items.length;
    while (low < high) {
      const middle = Math.floor((low + high) / 2);
      if (centerAt(middle) < coordinate) low = middle + 1;
      else high = middle;
    }
    const previous = Math.max(0, low - 1);
    const next = Math.min(this.layout.items.length - 1, low);
    const nearest =
      Math.abs(centerAt(previous) - coordinate) <=
      Math.abs(centerAt(next) - coordinate)
        ? previous
        : next;
    return this.layout.items[nearest]?.index ?? -1;
  }

  private movePointerReorderTo(clientX: number, clientY: number) {
    const state = this.pointerReorder;
    if (!state?.active) return;
    const fromIndex = state.currentIndex;
    const toIndex = this.reorderIndexAtPointer(state, clientX, clientY);
    const from = state.workingRows[fromIndex];
    const to = state.workingRows[toIndex];
    if (
      !from ||
      !to ||
      fromIndex === toIndex ||
      !this.isReorderable(from) ||
      !this.isReorderable(to) ||
      from.sectionKey !== to.sectionKey
    )
      return;
    const [moved] = state.workingRows.splice(fromIndex, 1);
    if (!moved) return;
    state.workingRows.splice(toIndex, 0, moved);
    state.currentIndex = toIndex;
    const canReuseLayout = this.canReuseReorderLayout(fromIndex, toIndex);
    this.snapshot = { ...this.snapshot, rows: state.workingRows };
    this.rows = state.workingRows;
    this.remapMountedRowsForMove(fromIndex, toIndex);
    if (canReuseLayout) this.renderWindow();
    else this.recomputeLayout();
    this.updateReorderVisualState();
  }

  private canReuseReorderLayout(fromIndex: number, toIndex: number): boolean {
    const start = Math.min(fromIndex, toIndex);
    const end = Math.max(fromIndex, toIndex);
    const first = this.layout.items[start];
    if (!first || this.layout.items.length !== this.rows.length) return false;
    for (let index = start + 1; index <= end; index += 1) {
      const item = this.layout.items[index];
      if (!item || item.width !== first.width || item.height !== first.height)
        return false;
    }
    return true;
  }

  private remapMountedRowsForMove(fromIndex: number, toIndex: number) {
    const remapped: Array<readonly [number, HTMLElement]> = [];
    this.mounted.forEach((element, index) => {
      let nextIndex = index;
      if (index === fromIndex) nextIndex = toIndex;
      else if (fromIndex < toIndex && index > fromIndex && index <= toIndex)
        nextIndex = index - 1;
      else if (fromIndex > toIndex && index >= toIndex && index < fromIndex)
        nextIndex = index + 1;
      setData(element, 'nativeListAnimateReorder', true);
      setData(element, 'nativeListRowIndex', nextIndex);
      remapped.push([nextIndex, element]);
    });
    this.mounted.clear();
    remapped.forEach(([index, element]) => this.mounted.set(index, element));
  }

  private reorderAutoScrollVelocity(): number {
    const state = this.pointerReorder;
    if (!state?.active) return 0;
    const rect = state.viewportRect ?? this.reorderViewportRect();
    const point = this.layout.horizontal ? state.clientX : state.clientY;
    const start = this.layout.horizontal ? rect.left : rect.top;
    const end = this.layout.horizontal ? rect.right : rect.bottom;
    return webReorderAutoScrollVelocity(
      point,
      start,
      end,
      Date.now() - (state.activatedAt ?? 0)
    );
  }

  private scheduleReorderAutoScroll() {
    if (
      this.reorderAutoScrollFrame !== undefined ||
      !this.pointerReorder?.active
    )
      return;
    this.reorderAutoScrollFrame = this.requestFrame(this.runReorderAutoScroll);
  }

  private runReorderAutoScroll = () => {
    this.reorderAutoScrollFrame = undefined;
    const state = this.pointerReorder;
    if (!state?.active) return;
    const velocity = this.reorderAutoScrollVelocity();
    if (velocity === 0) return;
    const before = this.currentOffset();
    this.setCurrentOffset(before + velocity);
    if (this.currentOffset() !== before) {
      this.movePointerReorderTo(state.clientX, state.clientY);
      this.scheduleReorderAutoScroll();
    }
  };

  private handleReorderPointerEnd = (event: PointerEvent) => {
    const state = this.pointerReorder;
    if (!state || state.pointerId !== event.pointerId) return;
    this.clearReorderLongPress(state);
    this.releaseReorderPointer(state);
    if (!state.active) {
      this.pointerReorder = undefined;
      return;
    }

    event.preventDefault();
    state.clientX = event.clientX;
    state.clientY = event.clientY;
    this.completePointerReorder(state);
  };

  private completePointerReorder(state: PointerReorderState) {
    this.flushReorderMove(state);
    const finalRows = this.snapshot.rows;
    const reorderEvent = webReorderEventForRows(
      state.originalRows,
      finalRows,
      state.sourceKey
    );
    this.animateReorderPreviewToCurrent(state);
    this.pointerReorder = undefined;
    this.stopReorderMove();
    this.stopReorderAutoScroll();
    this.suppressClickUntil = Date.now() + 300;
    this.updateReorderVisualState();
    if (reorderEvent) this.callbacks.onReorder?.(reorderEvent);
  }

  private handleReorderPointerCancel = (event: PointerEvent) => {
    if (this.pointerReorder?.pointerId !== event.pointerId) return;
    if (
      this.pointerReorder.active &&
      this.pointerReorder.pointerType !== 'mouse'
    )
      return;
    this.cancelPointerReorder(true);
  };

  private stopReorderAutoScroll() {
    if (this.reorderAutoScrollFrame === undefined) return;
    this.cancelFrame(this.reorderAutoScrollFrame);
    this.reorderAutoScrollFrame = undefined;
  }

  private cancelPointerReorder(restore: boolean) {
    const state = this.pointerReorder;
    if (!state) return;
    this.clearReorderLongPress(state);
    this.releaseReorderPointer(state);
    this.pointerReorder = undefined;
    this.stopReorderMove();
    this.stopReorderAutoScroll();
    if (state.active && restore) {
      this.remapMountedRows(state.originalRows);
      this.setSnapshot(
        { ...this.snapshot, rows: cancelWebReorderRows(state.originalRows) },
        this.selectedKeys
      );
      this.suppressClickUntil = Date.now() + 300;
    }
    if (state.active && restore) this.animateReorderPreviewToCurrent(state);
    else this.hideReorderPreview();
    this.updateReorderVisualState();
  }

  private remapMountedRows(rows: readonly RowModel[]) {
    const elementByKey = new Map<string, HTMLElement>();
    this.mounted.forEach((element) => {
      const key = element.dataset.nativeListRowKey;
      if (key) elementByKey.set(key, element);
      setData(element, 'nativeListAnimateReorder', true);
    });
    this.mounted.clear();
    rows.forEach((row, index) => {
      const element = elementByKey.get(row.key);
      if (element) {
        setData(element, 'nativeListRowIndex', index);
        this.mounted.set(index, element);
      }
    });
  }

  private updateReorderVisualState() {
    const sourceKey = this.pointerReorder?.active
      ? this.pointerReorder.sourceKey
      : this.keyboardReorder?.sourceKey ?? this.reorderSettlingKey;
    setData(this.root, 'nativeListDragging', Boolean(sourceKey));
    if (this.reorderMovementTimer !== undefined) {
      this.document.defaultView?.clearTimeout(this.reorderMovementTimer);
      this.reorderMovementTimer = undefined;
    }
    if (!sourceKey) {
      this.reorderMovementTimer = this.document.defaultView?.setTimeout(() => {
        this.reorderMovementTimer = undefined;
        this.reorderExpandingKey = undefined;
        this.mounted.forEach((element) => {
          setData(element, 'nativeListAnimateReorder', false);
          setData(element, 'nativeListReorderExpanding', false);
        });
      }, WEB_REORDER_ANIMATION.outOfWayDurationMs);
    }
    this.mounted.forEach((element) => {
      const dragging = element.dataset.nativeListRowKey === sourceKey;
      setData(
        element,
        'nativeListReorderExpanding',
        element.dataset.nativeListRowKey === this.reorderExpandingKey
      );
      setData(element, 'nativeListDragging', dragging);
      if (dragging) element.setAttribute('aria-grabbed', 'true');
      else element.removeAttribute('aria-grabbed');
    });
  }
}

export function applyRowStyle(body: HTMLElement, row: RowModel): void {
  applyRowContainerStyle(body, row);
}
