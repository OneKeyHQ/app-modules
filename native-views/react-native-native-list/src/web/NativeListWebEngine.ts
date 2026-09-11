import type {
  ActionAnchorInvalidatedEvent,
  CheckboxState,
  ImageSource,
  LeadingVisual,
  MarketRow,
  MarketTextStyle,
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
import { applyRowPatches, validateSnapshot } from '../validation';
import { avatarPrefetchWindow } from '../avatarPrefetch';
import {
  acquireNativeListAvatar,
  canonicalNativeListAvatarUri,
  type AvatarLease,
} from './NativeListWebAvatarCache';

const SECTION_INDEX_CONTENT_INSET = 16;
const SECTION_INDEX_RAIL_WIDTH = 32;
const SECTION_INDEX_EDGE_PADDING = 8;
const SECTION_INDEX_LABEL_SPACING = 16;
const SECTION_INDEX_MIN_HEIGHT = 120;
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

export type WebMarketLayoutStyle = Readonly<{
  horizontalPadding: number;
  verticalPadding: number;
  leadingGap: number;
  titleBadgeGap: number;
  trailingGap: number;
  imageWidth: number;
  imageHeight: number;
  imageCornerRadius: number;
  changeWidth: number;
  changeHeight: number;
  changeCornerRadius: number;
}>;

/** Resolves every reusable Market geometry field, including legacy defaults. */
export function resolveWebMarketLayoutStyle(
  row: MarketRow
): WebMarketLayoutStyle {
  const style = row.style;
  const imageWidth = style?.image?.width ?? (row.variant === 'stock' ? 40 : 32);
  const imageHeight =
    style?.image?.height ?? (row.variant === 'stock' ? 40 : 32);
  return {
    horizontalPadding:
      style?.horizontalPadding ?? (row.variant === 'perp' ? 16 : 20),
    verticalPadding: style?.verticalPadding ?? 12,
    leadingGap: style?.leadingGap ?? (row.variant === 'perp' ? 8 : 14),
    titleBadgeGap: style?.titleBadgeGap ?? 4,
    trailingGap: style?.trailingGap ?? 8,
    imageWidth,
    imageHeight,
    imageCornerRadius:
      style?.image?.cornerRadius ??
      (style?.image?.shape === 'square'
        ? 0
        : style?.image?.shape === 'rounded'
        ? 8
        : Math.min(imageWidth, imageHeight) / 2),
    changeWidth: style?.changeWidth ?? 80,
    changeHeight: style?.changeHeight ?? 32,
    changeCornerRadius: style?.changeCornerRadius ?? 8,
  };
}

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
  if (row.size === 'small') return -8;
  if (row.size === 'large') return 12;
  return 0;
}

function approximateMessageHeight(row: RowModel, availableWidth: number) {
  if (row.type !== 'message') return 0;
  const leadingWidth = row.leading ? 52 : 0;
  const thumbnailWidth = row.thumbnail ? 88 : 0;
  const charactersPerLine = Math.max(
    18,
    Math.floor((availableWidth - leadingWidth - thumbnailWidth - 40) / 7)
  );
  const titleLines = Math.min(
    2,
    Math.max(1, Math.ceil(row.title.length / charactersPerLine))
  );
  const bodyLines = Math.min(
    row.bodyLines ?? 3,
    Math.max(1, Math.ceil(row.body.length / charactersPerLine))
  );
  return 32 + titleLines * 20 + bodyLines * 20 + 22;
}

export function estimateWebRowHeight(
  row: RowModel,
  snapshot: NativeListSnapshot,
  availableWidth: number
): number {
  // OneKey patch: explicit selector height takes precedence over presets.
  // if (row.type === 'system' && row.variant === 'spacer') return row.height;
  if (row.height !== undefined) return row.height;
  if (row.type === 'system' && row.variant === 'warning') {
    const width = Math.max(1, availableWidth - 24);
    const lines = (text: string) =>
      Math.max(
        1,
        Math.ceil(
          Array.from(text).reduce(
            (length, char) => length + (char.charCodeAt(0) > 255 ? 14 : 7),
            0
          ) / width
        )
      );
    return 32 + 20 * (lines(row.title) + lines(row.message));
  }
  if (row.type === 'walletGroup')
    // OneKey patch: wallet badges participate in the outer group height.
    // return (row.children.length + 1) * 68 + row.children.length * 12;
    return (
      [row.parent, ...row.children].reduce(
        (height, member) =>
          height + estimateWebRowHeight(member, snapshot, availableWidth),
        0
      ) +
      row.children.length * 12 +
      (row.parent.height !== undefined ? 2 : 0)
    );
  // OneKey patch: reserve the source badge line below wallet names.
  // if (row.type === 'identity' && row.presentation === 'walletSidebar')
  // return 68;
  if (row.type === 'identity' && row.presentation === 'walletSidebar')
    return 68 + (row.badges?.length ? 24 : 0);
  if (row.type === 'identity' && row.presentation === 'networkSelector')
    return 47;
  if (row.type === 'identity' && row.presentation === 'accountSelector')
    return 58;

  let base: number;
  switch (row.type) {
    case 'rail':
      base = 40;
      break;
    case 'activity':
      base = row.footerActions?.length ? 100 : 60;
      break;
    case 'message':
      base = approximateMessageHeight(row, availableWidth);
      break;
    case 'mediaTile':
      base = 244;
      break;
    case 'metricCard':
      base =
        row.variant === 'activity'
          ? 161
          : row.variant === 'performance'
          ? 178
          : 132;
      break;
    case 'sectionHeader': {
      const isHistory =
        row.variant === 'history' ||
        row.key.startsWith('history-') ||
        row.sectionKey.startsWith('history-');
      base =
        snapshot.layout.kind === 'table'
          ? 28
          : row.presentation === 'networkSelector'
          ? 47
          : isHistory
          ? 16
          : row.variant === 'summary'
          ? 68
          : row.variant === 'gallery'
          ? 32
          : row.checkbox
          ? 56
          : snapshot.layout.kind === 'linear'
          ? 30
          : 36;
      break;
    }
    case 'system':
      base =
        row.variant === 'loading' && row.loadingStyle === 'skeleton'
          ? 56
          : row.variant === 'loading' && row.loadingStyle === 'spinner'
          ? 52
          : 'presentation' in row && row.presentation === 'market'
          ? row.variant === 'loading'
            ? 68
            : 44
          : row.variant === 'noMatch' || row.variant === 'end'
          ? 36
          : row.variant === 'retry'
          ? 44
          : 56;
      break;
    case 'action':
      base = row.presentation === 'accountSelector' ? 48 : row.icon ? 60 : 44;
      break;
    case 'dataRow':
      base = row.columns.some((column) => column.secondaryText) ? 60 : 56;
      break;
    case 'market': {
      const marketStyle = resolveWebMarketLayoutStyle(row);
      base = Math.max(
        row.variant === 'stock' ? 72 : 68,
        marketStyle.imageHeight + marketStyle.verticalPadding * 2
      );
      break;
    }
    case 'identity':
      base = row.tertiary ? 72 : row.subtitle ? 60 : 56;
      break;
    default:
      base = 56;
  }
  const tableAdjustment =
    snapshot.layout.kind === 'table' &&
    row.type === 'dataRow' &&
    !row.columns.some((column) => column.secondaryText)
      ? -8
      : 0;
  return Math.max(0, base + sizeModifier(row) + tableAdjustment);
}

function estimateHorizontalWidth(row: RowModel): number {
  if (row.type === 'mediaTile') return 200;
  if (row.type !== 'rail') return 280;
  const badgeLength = row.badge?.text.length ?? 0;
  const statusLength =
    row.status && row.status !== 'none' ? row.status.length : 0;
  return Math.min(
    288,
    Math.max(72, 50 + (row.title.length + badgeLength + statusLength) * 7)
  );
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
          row.type === 'rail'
            ? Math.min(rowHeight, availableHeight)
            : availableHeight,
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
  const itemWidth = Math.floor(
    (availableWidth - spacing * (columns - 1)) / columns
  );
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
      row.type === 'mediaTile'
        ? itemWidth + 48
        : estimateWebRowHeight(row, snapshot, itemWidth);
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
  return row.type === 'walletGroup' && row.children.length > 0
    ? '+' + String(row.children.length)
    : undefined;
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
[data-native-list-selector="walletSidebar"] .ok-native-list-title{max-width:100%;min-width:0}
.ok-native-list-root{--nl-bg:#f7f7f7;--nl-row:#fff;--nl-selected:#eaf2ff;--nl-pressed:#e8e8e8;--nl-subdued:#f9f9f9;--nl-strong:#0000000f;--nl-primary:#111;--nl-secondary:#6b7280;--nl-disabled:#8d8d8d;--nl-icon:#111;--nl-icon-subdued:#8d8d8d;--nl-separator:#e5e7eb;--nl-accent:#2f6bff;--nl-positive:#15803d;--nl-negative:#dc2626;--nl-critical:#feecec;--nl-inverse:#202020;--nl-inverse-text:#fcfcfc;--nl-info:#0d74ce;position:absolute;inset:0;display:flex;min-width:0;min-height:0;overflow:hidden;background:var(--nl-bg);color:var(--nl-primary);font-family:Roobert,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;font-synthesis:none}
.ok-native-list-viewport-frame{position:relative;flex:1;min-width:0;min-height:0;overflow:hidden}
.ok-native-list-viewport{position:absolute;inset:0;overflow:auto;overscroll-behavior:contain;-webkit-overflow-scrolling:touch;scrollbar-gutter:stable}
.ok-native-list-content{position:relative;min-width:100%;min-height:100%}
.ok-native-list-item{position:absolute;box-sizing:border-box;contain:layout paint style;outline:none}
.ok-native-list-row{width:100%;height:100%;box-sizing:border-box;display:flex;align-items:center;gap:12px;overflow:hidden;background:var(--nl-row);color:var(--nl-primary);cursor:pointer;user-select:none;-webkit-user-select:none}
.ok-native-list-item[data-table-alternate="true"]>.ok-native-list-row{background:var(--nl-bg)}
.ok-native-list-item[data-native-list-selected="true"]>.ok-native-list-row{background:var(--nl-selected)}
.ok-native-list-item[data-native-list-disabled="true"]>.ok-native-list-row{opacity:.5;cursor:default}
.ok-native-list-item:not([data-native-list-disabled="true"]):hover>.ok-native-list-row{background:var(--nl-pressed)}
.ok-native-list-item:not([data-native-list-disabled="true"]):active>.ok-native-list-row{background:var(--nl-pressed)}
.ok-native-list-item:not([data-native-list-disabled="true"]):not([data-native-list-selected="true"]):hover>.ok-native-list-wallet-row{background:var(--nl-strong)}
.ok-native-list-item:not([data-native-list-disabled="true"]):not([data-native-list-selected="true"]):active>.ok-native-list-wallet-row{background:var(--nl-pressed)}
.ok-native-list-item[data-native-list-selected="true"]:hover>.ok-native-list-wallet-row{background:var(--nl-selected)}
.ok-native-list-wallet-group{display:flex;width:100%;height:100%;box-sizing:border-box;flex-direction:column;gap:12px;overflow:hidden;border:1px solid var(--nl-separator);border-radius:12px;background:var(--nl-subdued);user-select:none;-webkit-user-select:none}
.ok-native-list-wallet-member{flex:0 0 68px;height:68px;overflow:hidden;border-radius:12px;background:transparent;cursor:pointer}
.ok-native-list-wallet-member>.ok-native-list-wallet-row{height:100%;background:transparent}
.ok-native-list-wallet-member[data-native-list-selected="true"]>.ok-native-list-wallet-row{background:var(--nl-selected)}
.ok-native-list-wallet-member:not([data-native-list-selected="true"]):hover>.ok-native-list-wallet-row{background:var(--nl-strong)}
.ok-native-list-wallet-member:not([data-native-list-selected="true"]):active>.ok-native-list-wallet-row{background:var(--nl-pressed)}
.ok-native-list-item:focus-visible>.ok-native-list-row{outline:2px solid var(--nl-accent);outline-offset:-2px}
.ok-native-list-item[data-native-list-reorderable="true"]>.ok-native-list-row{cursor:grab}
.ok-native-list-root[data-native-list-dragging="true"] .ok-native-list-row{cursor:grabbing}
.ok-native-list-item[data-native-list-animate-reorder="true"]{transition:transform ${WEB_REORDER_ANIMATION.outOfWayDurationMs}ms ${WEB_REORDER_ANIMATION.outOfWayTimingFunction},height ${WEB_REORDER_ANIMATION.outOfWayDurationMs}ms ${WEB_REORDER_ANIMATION.outOfWayTimingFunction}}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-row{overflow:hidden;border-radius:12px;background:var(--nl-row)}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-row>*{visibility:hidden}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-wallet-group{border-color:transparent;background:var(--nl-row)}
.ok-native-list-item[data-native-list-dragging="true"]>.ok-native-list-wallet-group>*{visibility:hidden}
.ok-native-list-reorder-preview{position:fixed;left:0;top:0;z-index:100001;pointer-events:none;overflow:hidden;border-radius:12px;box-shadow:0 4px 24px rgba(0,0,0,.12);transform-origin:center;will-change:transform;font-family:Roobert,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
.ok-native-list-reorder-preview>.ok-native-list-row{background:var(--nl-row);cursor:grabbing}
.ok-native-list-reorder-preview[data-native-list-selected="true"]>.ok-native-list-row{background:var(--nl-selected)}
.ok-native-list-reorder-count{position:absolute;right:4px;bottom:4px;display:flex;align-items:center;justify-content:center;box-sizing:border-box;min-width:24px;height:24px;padding:0 6px;border:1px solid var(--nl-row);border-radius:12px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;line-height:22px;font-weight:600}
.ok-native-list-item[data-separator="true"]>.ok-native-list-row{border-bottom:1px solid var(--nl-separator)}
.ok-native-list-item[data-group-position="first"]>.ok-native-list-row{border-radius:12px 12px 0 0}
.ok-native-list-item[data-group-position="last"]>.ok-native-list-row{border-radius:0 0 12px 12px}
.ok-native-list-item[data-group-position="single"]>.ok-native-list-row{border-radius:12px}
.ok-native-list-standard{padding:8px 12px}.ok-native-list-network-row{padding:0 12px}.ok-native-list-wallet-row{padding:4px 8px;flex-direction:column;justify-content:center;gap:4px}.ok-native-list-account-row{padding:4px 12px;gap:8px}
.ok-native-list-account-row .ok-native-list-visual,.ok-native-list-account-action-row .ok-native-list-visual{width:32px;height:32px;flex-basis:32px}.ok-native-list-account-row .ok-native-list-visual>img,.ok-native-list-account-row .ok-native-list-visual-main,.ok-native-list-account-action-row .ok-native-list-visual>img,.ok-native-list-account-action-row .ok-native-list-visual-main{width:32px;height:32px}.ok-native-list-account-row .ok-native-list-visual>.ok-native-list-visual-corner{width:20px;height:20px;padding:2px}.ok-native-list-account-action-row .ok-native-list-visual{border-radius:8px!important}.ok-native-list-wallet-row .ok-native-list-title{color:var(--nl-secondary);font-size:12px;line-height:16px;font-weight:400}.ok-native-list-item[data-native-list-selected="true"]>.ok-native-list-wallet-row .ok-native-list-title{color:var(--nl-primary)}.ok-native-list-account-row .ok-native-list-title{font-size:16px;line-height:20px;font-weight:400}.ok-native-list-account-row .ok-native-list-secondary{font-size:14px;line-height:20px;font-weight:400}
.ok-native-list-flex{display:flex;flex:1;min-width:0;flex-direction:column;justify-content:center}.ok-native-list-title{font-size:15px;line-height:20px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-secondary{font-size:13px;line-height:18px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-tertiary{color:var(--nl-secondary)}.ok-native-list-info{color:var(--nl-info)}
.ok-native-list-identity-row .ok-native-list-title,.ok-native-list-identity-row .ok-native-list-amounts>span:first-child{font-size:16px;line-height:20px}.ok-native-list-identity-row .ok-native-list-secondary{font-size:14px;line-height:20px}
.ok-native-list-value{font-size:14px;line-height:20px;font-weight:500;white-space:nowrap}.ok-native-list-time{font-size:11px;line-height:16px;color:var(--nl-secondary);align-self:flex-start}.ok-native-list-amounts{display:flex;flex-direction:column;align-items:flex-end;min-width:0}.ok-native-list-actions{display:flex;gap:16px;margin-top:6px}
.ok-native-list-action-button{appearance:none;border:0;background:transparent;padding:2px;color:var(--nl-accent);font:600 12px/16px inherit;cursor:pointer}.ok-native-list-action-button[data-tone="danger"]{color:var(--nl-negative)}.ok-native-list-action-button:disabled{opacity:.5;cursor:default}
.ok-native-list-visual{position:relative;flex:0 0 40px;width:40px;height:40px;border-radius:20px;overflow:visible;background:var(--nl-strong)}.ok-native-list-visual>img,.ok-native-list-visual-main{display:block;width:40px;height:40px;border-radius:inherit;object-fit:cover}.ok-native-list-visual-fallback{display:flex;width:100%;height:100%;align-items:center;justify-content:center;border-radius:inherit;color:var(--nl-primary);font-size:15px;font-weight:600;overflow:hidden}.ok-native-list-visual>.ok-native-list-visual-corner{position:absolute;right:-4px;bottom:-4px;width:18px;height:18px;padding:2px;border-radius:50%;background:var(--nl-row);object-fit:cover}.ok-native-list-stacked{display:flex;align-items:center;width:48px;flex-basis:48px}.ok-native-list-stacked img{width:28px;height:28px;border:2px solid var(--nl-row);border-radius:50%;object-fit:cover;margin-left:-10px}.ok-native-list-stacked img:first-child{margin-left:0}
.ok-native-list-accessories{display:flex;align-items:center;gap:10px;min-width:0}.ok-native-list-accessory{font-size:14px;color:var(--nl-primary);white-space:nowrap}.ok-native-list-accessory-secondary{color:var(--nl-secondary)}.ok-native-list-icon-button{display:inline-flex;align-items:center;justify-content:center;min-width:24px;height:24px;border:0;background:transparent;color:var(--nl-icon);font-size:20px;line-height:1;font-weight:500;font-family:inherit;cursor:pointer}.ok-native-list-checkbox{display:inline-flex;align-items:center;justify-content:center;box-sizing:border-box;width:20px;height:20px;border:2px solid color-mix(in srgb,var(--nl-primary) 19%,transparent);border-radius:5px;background:var(--nl-row);cursor:pointer}.ok-native-list-checkbox[data-state="checked"],.ok-native-list-checkbox[data-state="indeterminate"]{border-color:transparent;background:var(--nl-primary)}.ok-native-list-checkbox[data-state="checked"]::after{content:"";width:6px;height:10px;margin-top:-2px;border-right:2px solid var(--nl-row);border-bottom:2px solid var(--nl-row);transform:rotate(45deg)}.ok-native-list-checkbox[data-state="indeterminate"]::after{content:"";width:8px;height:2px;border-radius:1px;background:var(--nl-row)}.ok-native-list-spinner{width:16px;height:16px;border:2px solid var(--nl-separator);border-top-color:var(--nl-primary);border-radius:50%;animation:ok-native-list-spin .8s linear infinite}@keyframes ok-native-list-spin{to{transform:rotate(360deg)}}
.ok-native-list-badge{display:inline-flex;align-items:center;max-width:100%;height:18px;padding:0 5px;border-radius:5px;background:color-mix(in srgb,var(--nl-accent) 12%,transparent);color:var(--nl-accent);font-size:11px;line-height:18px;white-space:nowrap}.ok-native-list-badge[data-tone="danger"]{background:var(--nl-critical);color:var(--nl-negative)}.ok-native-list-badges{display:inline-flex;gap:4px;margin-left:6px;vertical-align:middle}
.ok-native-list-section{padding:0 8px;background:var(--nl-bg);gap:8px}.ok-native-list-section[data-variant="summary"]{padding:0 20px}.ok-native-list-section[data-variant="gallery"]{padding:0 12px}.ok-native-list-section[data-variant="history"]{padding:0 8px}.ok-native-list-section-title{font-size:13px;line-height:18px;font-weight:700;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-section[data-variant="summary"] .ok-native-list-section-title{font-size:16px;font-weight:500;color:var(--nl-primary)}.ok-native-list-section[data-variant="gallery"] .ok-native-list-section-title{font-size:16px;color:var(--nl-primary)}.ok-native-list-section[data-variant="history"] .ok-native-list-section-title{font-size:12px;line-height:16px}.ok-native-list-section-value{margin-left:auto}
.ok-native-list-action-row{justify-content:space-between;padding:0 16px}.ok-native-list-action-title{font-size:15px;font-weight:600;color:var(--nl-accent)}.ok-native-list-action-title[data-tone="danger"]{color:var(--nl-negative)}
.ok-native-list-account-action-row{justify-content:flex-start;gap:12px;padding:0 12px}.ok-native-list-account-action-row .ok-native-list-action-title{color:var(--nl-secondary);font-size:16px;line-height:24px;font-weight:400}
.ok-native-list-system{justify-content:center;padding:8px 12px;background:var(--nl-bg);color:var(--nl-secondary);cursor:default}.ok-native-list-system[data-variant="retry"],.ok-native-list-system[data-variant="noMatch"],.ok-native-list-system[data-variant="end"]{justify-content:flex-start}.ok-native-list-system[data-variant="retry"]{cursor:pointer}
.ok-native-list-rail{padding:4px;gap:6px;border-radius:8px}.ok-native-list-rail .ok-native-list-visual{width:20px;height:20px;flex-basis:20px}.ok-native-list-rail .ok-native-list-visual>img,.ok-native-list-rail .ok-native-list-visual-main{width:20px;height:20px}.ok-native-list-rail-title{font-size:12px;font-weight:500;white-space:nowrap}
.ok-native-list-media{display:block;padding:0 5px;background:transparent;border-radius:16px}.ok-native-list-media-image{display:block;width:100%;aspect-ratio:1;border-radius:10px;background:var(--nl-strong);object-fit:cover}.ok-native-list-media-image[data-state="empty"]{background:transparent}.ok-native-list-media-image[data-state="error"]{display:flex;align-items:center;justify-content:center;color:var(--nl-icon-subdued);font-size:24px}.ok-native-list-media-meta{padding-top:7px}.ok-native-list-media-subtitle-row{display:flex;align-items:center;gap:6px}.ok-native-list-media-subtitle{flex:1;min-width:0;font-size:12px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-media-network{width:14px;height:14px;border-radius:50%}.ok-native-list-media-title{font-size:16px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-media-close{position:absolute;right:9px;top:4px;border:0;background:color-mix(in srgb,var(--nl-inverse) 72%,transparent);color:var(--nl-inverse-text);width:24px;height:24px;border-radius:50%;font:18px/20px inherit;cursor:pointer}
.ok-native-list-metric{display:flex;flex-direction:column;align-items:flex-start;padding:12px;border-radius:12px;gap:5px;background:var(--nl-row)}.ok-native-list-metric-value{font-size:22px;line-height:28px;font-weight:700}.ok-native-list-composite{display:flex;flex-direction:column;align-items:stretch;padding:14px;border-radius:12px;gap:12px;background:var(--nl-subdued)}.ok-native-list-composite-heading{font-size:14px;letter-spacing:1px;color:var(--nl-secondary)}.ok-native-list-composite-row{display:flex;gap:12px}.ok-native-list-composite-cell{flex:1;min-width:0}.ok-native-list-composite-cell[data-shaded="true"]{padding:10px;border-radius:10px;background:color-mix(in srgb,var(--nl-primary) 5%,transparent)}.ok-native-list-composite-value{font-size:18px;font-weight:600}.ok-native-list-divider{height:1px;background:var(--nl-separator)}.ok-native-list-progress{height:4px;border-radius:2px;overflow:hidden;background:var(--nl-negative)}.ok-native-list-progress>span{display:block;height:100%;border-radius:2px;background:var(--nl-positive)}
.ok-native-list-data{padding:6px 12px}.ok-native-list-index{flex:0 0 28px;color:var(--nl-secondary);font-size:13px}.ok-native-list-favorite{flex:0 0 24px;color:var(--nl-icon-subdued);font-size:22px}.ok-native-list-favorite[data-active="true"]{color:var(--nl-accent)}.ok-native-list-data-cell{display:flex;flex-direction:column;min-width:0}.ok-native-list-data-cell[data-align="center"]{align-items:center}.ok-native-list-data-cell[data-align="end"]{align-items:flex-end}.ok-native-list-data-primary{display:flex;align-items:center;gap:5px;max-width:100%;font-size:16px;font-weight:500;white-space:nowrap}.ok-native-list-unread{width:7px;height:7px;flex:0 0 7px;border-radius:50%;background:var(--nl-accent)}.ok-native-list-thumbnail{width:64px;height:64px;border-radius:10px;object-fit:cover}
.ok-native-list-market{padding:12px 20px;gap:14px}.ok-native-list-market>.ok-native-list-visual{width:32px;height:32px;flex-basis:32px}.ok-native-list-market[data-variant="stock"]>.ok-native-list-visual{width:40px;height:40px;flex-basis:40px}.ok-native-list-market>.ok-native-list-visual>.ok-native-list-visual-main{width:100%;height:100%}.ok-native-list-market-main{display:flex;flex:1;min-width:0;flex-direction:column;justify-content:center}.ok-native-list-market-title-line{display:flex;align-items:center;min-width:0;gap:4px}.ok-native-list-market-title{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--nl-primary);font-size:16px;line-height:24px;font-weight:500}.ok-native-list-market-subtitle{min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:var(--nl-secondary);font-size:14px;line-height:20px;font-weight:400}.ok-native-list-market-badge{display:inline-flex;flex:0 0 auto;align-items:center;justify-content:center;box-sizing:border-box;max-width:72px;height:18px;padding:0 5px;border:0;border-radius:4px;background:var(--nl-strong);color:var(--nl-secondary);font:500 11px/18px inherit;overflow:hidden;white-space:nowrap}.ok-native-list-market-badge img{width:14px;height:14px;object-fit:contain}.ok-native-list-market-trailing{display:flex;flex:0 0 auto;align-items:center;gap:8px}.ok-native-list-market-price{max-width:112px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;text-align:right;color:var(--nl-primary);font-size:16px;line-height:24px;font-weight:500;font-variant-numeric:tabular-nums}.ok-native-list-market-change{display:flex;align-items:center;justify-content:center;box-sizing:border-box;width:80px;height:32px;border-radius:8px;color:#fff;background:#8d8d8d;font-size:14px;line-height:20px;font-weight:500;font-variant-numeric:tabular-nums;white-space:nowrap}.ok-native-list-market-change[data-tone="positive"]{background:var(--nl-positive)}.ok-native-list-market-change[data-tone="negative"]{background:var(--nl-negative)}
.ok-native-list-market-badge>svg,.ok-native-list-market-badge>.ok-native-list-market-badge-icon>svg{display:block;width:16px;height:16px;flex:0 0 16px}.ok-native-list-system[data-native-list-presentation="market"]{box-sizing:border-box;padding:12px 20px}.ok-native-list-system[data-native-list-presentation="market"]>.ok-native-list-spinner{width:32px;height:32px}
.ok-native-list-footer{flex:0 0 auto;min-height:0}.ok-native-list-sticky{position:absolute;z-index:4;left:0;right:0;top:0;pointer-events:auto;box-shadow:0 1px 0 var(--nl-separator)}.ok-native-list-index-rail{position:absolute;z-index:6;top:0;right:0;bottom:0;width:${SECTION_INDEX_RAIL_WIDTH}px;touch-action:none;cursor:pointer}.ok-native-list-index-rail[hidden]{display:none}.ok-native-list-index-button{appearance:none;position:absolute;left:6px;display:flex;width:20px;height:16px;align-items:center;justify-content:center;padding:0;transform:translateY(-50%);border:0;border-radius:8px;background:transparent;color:var(--nl-secondary);font:600 10px/1 inherit;cursor:pointer}.ok-native-list-index-button[data-active="true"]{background:var(--nl-accent);color:var(--nl-inverse-text)}.ok-native-list-index-button:focus-visible{outline:2px solid var(--nl-accent);outline-offset:1px}.ok-native-list-index-preview{position:absolute;z-index:8;right:40px;top:50%;display:flex;width:48px;height:48px;align-items:center;justify-content:center;transform:translateY(-50%) scale(.92);border-radius:14px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:22px;font-weight:600;opacity:0;pointer-events:none;transition:opacity .15s ease,transform .15s ease}.ok-native-list-index-preview[data-visible="true"]{opacity:1;transform:translateY(-50%) scale(1)}
.ok-native-list-refresh{position:absolute;z-index:7;left:50%;top:8px;display:flex;align-items:center;gap:6px;padding:6px 10px;border-radius:999px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;opacity:0;transform:translate(-50%,-16px);transition:opacity .15s ease,transform .15s ease;pointer-events:none}.ok-native-list-refresh[data-visible="true"]{opacity:1;transform:translate(-50%,0)}
.ok-native-list-warning{height:auto;display:flex;flex-direction:column;align-items:stretch;gap:4px;padding:14px 12px;border-top:1px solid;border-bottom:1px solid;box-sizing:border-box;cursor:default}.ok-native-list-warning-title,.ok-native-list-warning-message{font-size:14px;line-height:20px;white-space:normal;overflow-wrap:anywhere}.ok-native-list-warning-title{font-weight:500;color:var(--nl-primary)}.ok-native-list-warning-message{font-weight:400;color:var(--nl-secondary)}
.ok-native-list-subtitle-segments{display:flex;align-items:center;min-width:0;max-width:100%;height:20px}.ok-native-list-subtitle-segments>.ok-native-list-secondary{flex:0 1 auto;min-width:0}.ok-native-list-subtitle-dot{flex:0 0 4px;width:4px;height:4px;margin:0 6px;border-radius:50%;background:var(--nl-disabled)}.ok-native-list-wallet-row>.ok-native-list-flex{flex:0 1 auto;width:100%;align-items:center}.ok-native-list-wallet-badges{display:flex;gap:4px;justify-content:center;margin-top:4px;height:20px;max-width:100%}.ok-native-list-wallet-badges>.ok-native-list-badge{background:var(--nl-strong);color:var(--nl-secondary);font-size:12px;line-height:16px;height:20px;box-sizing:border-box;padding:2px 4px}.ok-native-list-visual-overlay{position:absolute;display:flex;align-items:center;justify-content:center;box-sizing:border-box;border-radius:50%;overflow:hidden;line-height:1;font-size:10px}.ok-native-list-visual-overlay img,.ok-native-list-visual-overlay svg{width:100%;height:100%;object-fit:contain}
.ok-native-list-row.ok-native-list-market-skeleton{padding:12px 20px;gap:0}.ok-native-list-skeleton-left{display:flex;align-items:center;gap:12px;flex:1}.ok-native-list-skeleton-text{display:flex;flex-direction:column;gap:4px}.ok-native-list-skeleton-right{display:flex;align-items:center;gap:8px}.ok-native-list-skeleton-mark{display:block;flex-shrink:0;border-radius:8px;animation:ok-native-list-skeleton 1.5s linear infinite alternate}@keyframes ok-native-list-skeleton{from{background-color:var(--nl-skeleton-base)}to{background-color:var(--nl-skeleton-highlight)}}.ok-native-list-market-spinner{display:block;width:20px;height:20px;flex-shrink:0;color:var(--nl-icon);animation:ok-native-list-spin .75s linear infinite}
@media (prefers-reduced-motion:reduce){.ok-native-list-index-preview,.ok-native-list-refresh{transition:none}.ok-native-list-spinner,.ok-native-list-market-spinner{animation:none}.ok-native-list-skeleton-mark{animation:none;background:var(--nl-skeleton-base)}}
.ok-native-list-footer{flex:0 0 auto;min-height:0}.ok-native-list-sticky{position:absolute;z-index:4;left:0;right:0;top:0;pointer-events:auto;box-shadow:0 1px 0 var(--nl-separator)}.ok-native-list-viewport-frame:has(>.ok-native-list-index-rail:not([hidden]))>.ok-native-list-viewport{scrollbar-width:none}.ok-native-list-viewport-frame:has(>.ok-native-list-index-rail:not([hidden]))>.ok-native-list-viewport::-webkit-scrollbar{display:none}.ok-native-list-index-rail{position:absolute;z-index:6;top:0;right:0;bottom:0;width:${SECTION_INDEX_RAIL_WIDTH}px;touch-action:none;cursor:pointer}.ok-native-list-index-rail[hidden]{display:none}.ok-native-list-index-button{appearance:none;position:absolute;left:15px;display:flex;width:14px;height:14px;align-items:center;justify-content:center;padding:0;transform:translateY(-50%);border:0;border-radius:7px;background:transparent;color:var(--nl-disabled);font-family:inherit;font-size:10px;font-weight:400;line-height:1;cursor:pointer}.ok-native-list-index-button[data-active="true"]{background:var(--nl-positive);color:var(--nl-inverse-text);font-weight:500}.ok-native-list-index-button:focus-visible{outline:2px solid var(--nl-positive);outline-offset:1px}
.ok-native-list-refresh{position:absolute;z-index:7;left:50%;top:8px;display:flex;align-items:center;gap:6px;padding:6px 10px;border-radius:999px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;opacity:0;transform:translate(-50%,-16px);transition:opacity .15s ease,transform .15s ease;pointer-events:none}.ok-native-list-refresh[data-visible="true"]{opacity:1;transform:translate(-50%,0)}
.ok-native-list-warning{height:auto;display:flex;flex-direction:column;align-items:stretch;gap:4px;padding:14px 12px;border-top:1px solid;border-bottom:1px solid;box-sizing:border-box;cursor:default}.ok-native-list-warning-title,.ok-native-list-warning-message{font-size:14px;line-height:20px;white-space:normal;overflow-wrap:anywhere}.ok-native-list-warning-title{font-weight:500;color:var(--nl-primary)}.ok-native-list-warning-message{font-weight:400;color:var(--nl-secondary)}
.ok-native-list-subtitle-segments{display:flex;align-items:center;min-width:0;max-width:100%;height:20px}.ok-native-list-subtitle-segments>.ok-native-list-secondary{flex:0 1 auto;min-width:0}.ok-native-list-subtitle-dot{flex:0 0 4px;width:4px;height:4px;margin:0 6px;border-radius:50%;background:var(--nl-disabled)}.ok-native-list-wallet-row>.ok-native-list-flex{flex:0 1 auto;width:100%;align-items:center}.ok-native-list-wallet-badges{display:flex;gap:4px;justify-content:center;margin-top:4px;height:20px;max-width:100%}.ok-native-list-wallet-badges>.ok-native-list-badge{background:var(--nl-strong);color:var(--nl-secondary);font-size:12px;line-height:16px;height:20px;box-sizing:border-box;padding:2px 4px}.ok-native-list-visual-overlay{position:absolute;display:flex;align-items:center;justify-content:center;box-sizing:border-box;border-radius:50%;overflow:hidden;line-height:1;font-size:10px}.ok-native-list-visual-overlay img,.ok-native-list-visual-overlay svg{width:100%;height:100%;object-fit:contain}
@media (prefers-reduced-motion:reduce){.ok-native-list-refresh{transition:none}.ok-native-list-spinner{animation:none}}
/* OneKey patch: selector controls follow their original semantic colors and geometry. */
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
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-wallet-badges>.ok-native-list-badge{font-size:11px;line-height:14px;font-weight:400;height:18px;padding:2px 6px;border-radius:4px;background:var(--nl-subdued);color:var(--nl-secondary)}
.ok-native-list-wallet-row[data-native-list-selector="walletSidebar"] .ok-native-list-wallet-badges>.ok-native-list-badge[data-tone="warning"]{background:var(--nl-caution-background);color:var(--nl-caution)}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>.ok-native-list-icon-button{box-sizing:border-box;flex:0 0 38px;width:38px;height:38px;margin:-7px;padding:7px}
/* OneKey patch: AccountSelectorAccountListItem fixes the borderless Plus slot at top18/right20. */
.ok-native-list-account-row[data-native-list-selector="accountSelector"]>.ok-native-list-accessories[data-native-list-account-control="createAddress"]{position:absolute;top:18px;right:12px}
.ok-native-list-account-row[data-native-list-selector="accountSelector"] .ok-native-list-accessories>[data-native-list-account-control="createAddress"]{flex-basis:36px;width:36px;height:36px;padding:6px;border-radius:8px}
.ok-native-list-account-action-row{padding-left:12px;padding-right:12px}.ok-native-list-account-action-row .ok-native-list-action-title{font-size:16px;line-height:24px;font-weight:400}.ok-native-list-account-action-row .ok-native-list-action-title[data-tone="primary"]{color:var(--nl-primary)}
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
  if (!fallbackUri && retryLimit === 0) return;
  const view = image.ownerDocument.defaultView;
  let currentUri = initialUri;
  let usedFallback = false;
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
    if (fallbackUri && !usedFallback && fallbackUri !== currentUri) {
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

function createImage(
  context: RenderContext,
  source: ImageSource,
  className?: string
): HTMLImageElement | undefined {
  const avatarUri = canonicalNativeListAvatarUri(source.uri);
  const uri = avatarUri ?? safeImageUri(source.uri);
  if (!uri) return undefined;
  const image = context.document.createElement('img');
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
  paint.style.backgroundSize =
    image.style.objectFit === 'fill'
      ? '100% 100%'
      : image.style.objectFit === 'center'
      ? 'auto'
      : image.style.objectFit;
  image.style.opacity = '0';
  const update = () => {
    paint.style.backgroundImage =
      'url(' + JSON.stringify(image.currentSrc || image.src) + ')';
  };
  image.addEventListener('load', update);
  image.addEventListener('error', () => {
    paint.style.backgroundImage = 'none';
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

function visualFromRow(row: RowModel): LeadingVisual | undefined {
  if (row.type === 'identity') return row.leading;
  if (row.type === 'rail') return row.visual;
  if (row.type === 'activity') return row.leading;
  if (row.type === 'message') return row.leading;
  if (row.type === 'dataRow') return row.leading;
  if (row.type === 'market') return row.leading;
  if (row.type === 'metricCard') return row.visual;
  return undefined;
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

  if ('backgroundColor' in visual && visual.backgroundColor)
    frame.style.background = visual.backgroundColor;
  const source = visual.kind === 'image' ? visual.image : visual.image;
  const image = source ? createImage(context, source) : undefined;
  if (image) {
    image.className = 'ok-native-list-visual-main';
    frame.appendChild(image);
    if (selectorPresentation) paintSelectorImageBackground(image, frame);
  } else {
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
  if ('fallbackIcon' in visual && visual.fallbackIcon) {
    const icon = visual.fallbackIcon;
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
        'ok-native-list-visual-fallback'
      );
      applySelectorIcon(fallback, icon.name);
      if (icon.tintColor) fallback.style.color = icon.tintColor;
      frame.prepend(fallback);
    };
    if (image) image.addEventListener('error', showFallback, { once: true });
    else showFallback();
  }
  if ('borderStyle' in visual && visual.borderStyle === 'dashed') {
    frame.style.border =
      '2px dashed ' + (visual.borderColor ?? 'var(--nl-disabled)');
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
        corner.style.fontWeight = '400';
      }
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
          corner.appendChild(overlayImage);
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

function createBadge(
  context: RenderContext,
  badge: Readonly<{ text: string; tone?: string }>
): HTMLElement {
  const element = createElement(
    context.document,
    'span',
    'ok-native-list-badge',
    badge.text
  );
  setData(element, 'tone', badge.tone);
  return element;
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
function applyValueSegments(
  element: HTMLElement,
  segments:
    | readonly Readonly<{ text: string; style?: 'subscript' }>[]
    | undefined,
  fontSize = 16,
  lineHeight = 24,
  weight = 500
) {
  if (!segments?.length) return;
  element.textContent = '';
  element.style.fontSize = String(fontSize) + 'px';
  element.style.lineHeight = String(lineHeight) + 'px';
  element.style.fontWeight = String(weight);
  segments.forEach((segment) => {
    const span = createElement(
      element.ownerDocument,
      'span',
      undefined,
      segment.text
    );
    if (segment.style === 'subscript') {
      span.style.fontSize = String(Math.ceil(fontSize * 0.6)) + 'px';
      span.style.lineHeight = String(fontSize) + 'px';
    }
    element.appendChild(span);
  });
}

function createAccessory(
  context: RenderContext,
  rowKey: string,
  accessory: TrailingAccessory,
  slot: number
): HTMLElement {
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
    setData(element, 'testid', accessory.testID);
    if (accessory.hoverActionKey)
      setData(element, 'nativeListHoverAction', accessory.hoverActionKey);
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
      if (row.height !== undefined && accessory.name === 'PlusSmallOutline') {
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
      const secondary = createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        accessory.secondary
      );
      secondary.style.color = toneColor(accessory.secondaryTone, 'secondary');
      element.replaceChildren(primary, secondary);
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

function appendAccessories(
  parent: HTMLElement,
  context: RenderContext,
  rowKey: string,
  accessories: readonly TrailingAccessory[] | undefined
) {
  if (!accessories?.length) return;
  const container = createElement(
    context.document,
    'span',
    'ok-native-list-accessories'
  );
  const row = context.snapshot.rows[context.itemIndex];
  if (
    row &&
    row.height !== undefined &&
    'presentation' in row &&
    row.presentation === 'networkSelector'
  ) {
    container.style.gap = accessories.some(
      (accessory) => accessory.kind === 'checkbox'
    )
      ? '12px'
      : '20px';
  }
  if (
    row &&
    row.height !== undefined &&
    'presentation' in row &&
    row.presentation === 'accountSelector' &&
    accessories.length === 1 &&
    accessories[0]?.kind === 'icon' &&
    accessories[0].name === 'PlusSmallOutline'
  ) {
    setData(container, 'nativeListAccountControl', 'createAddress');
  }
  accessories.forEach((accessory, slot) =>
    container.appendChild(createAccessory(context, rowKey, accessory, slot))
  );
  parent.appendChild(container);
}

function createTextColumn(
  context: RenderContext,
  title: string,
  subtitle?: string,
  tertiary?: string,
  tertiaryTone?: 'secondary' | 'info',
  badges?: readonly Readonly<{ text: string; tone?: string }>[]
): HTMLElement {
  const column = createElement(context.document, 'span', 'ok-native-list-flex');
  const titleLine = createElement(
    context.document,
    'span',
    'ok-native-list-title',
    title
  );
  if (badges?.length) {
    const badgeLine = createElement(
      context.document,
      'span',
      'ok-native-list-badges'
    );
    badges.forEach((badge) =>
      badgeLine.appendChild(createBadge(context, badge))
    );
    titleLine.appendChild(badgeLine);
  }
  column.appendChild(titleLine);
  if (subtitle)
    column.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        subtitle
      )
    );
  if (tertiary) {
    const element = createElement(
      context.document,
      'span',
      tertiaryTone === 'info'
        ? 'ok-native-list-secondary ok-native-list-info'
        : 'ok-native-list-secondary ok-native-list-tertiary',
      tertiary
    );
    column.appendChild(element);
  }
  return column;
}

function createSectionHeader(
  context: RenderContext,
  row: Extract<RowModel, { type: 'sectionHeader' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-section'
  );
  setData(body, 'variant', row.variant);
  if (row.titleIcon) {
    const titleIcon = createIconAction(
      context,
      row.titleIcon.name,
      row.titleIcon.actionKey,
      row.titleIcon.disabled,
      row.titleIcon.tintColor
    );
    markActionAnchorSource(titleIcon, 'leadingAction');
    body.appendChild(titleIcon);
  }
  // OneKey patch: section title help has its own measurable action target.
  // body.appendChild(createTextColumn(context, row.title, row.subtitle));
  const column = createTextColumn(context, row.title, row.subtitle);
  const title = column.firstElementChild as HTMLElement;
  title.classList.add('ok-native-list-section-title');
  if (row.titleActionKey) {
    setData(title, 'nativeListAction', row.titleActionKey);
    markActionAnchorSource(title, 'leadingAction');
    title.setAttribute('role', 'button');
    title.tabIndex = 0;
    title.style.alignSelf = 'flex-start';
    title.style.maxWidth = '100%';
    // OneKey patch: explicit network headers reserve a separate 3-point underline area.
    if (row.presentation !== 'networkSelector' || row.height === undefined) {
      title.style.textDecoration = 'underline dotted';
      title.style.textUnderlineOffset = '6px';
    }
    if (row.titleActionOnHover) setData(title, 'nativeListHoverAction', true);
  }
  if (row.presentation === 'networkSelector' && row.height !== undefined) {
    body.style.padding =
      row.variant === 'summary' ? '24px 12px 20px' : '0 12px';
    body.style.backgroundColor = 'var(--nl-bg)';
    body.style.gap = row.checkbox ? '12px' : '8px';
    title.style.fontSize = row.variant === 'summary' ? '16px' : '14px';
    title.style.lineHeight = row.variant === 'summary' ? '24px' : '20px';
    title.style.fontWeight =
      row.variant === 'summary' || (row.titleActionKey && !row.checkbox)
        ? '500'
        : '600';
    if (row.titleActionKey) {
      const text = createElement(
        context.document,
        'span',
        'ok-native-list-section-title-text',
        row.title
      );
      text.style.overflow = 'hidden';
      text.style.textOverflow = 'ellipsis';
      text.style.maxWidth = '100%';
      const dotted = context.document.createElementNS(
        'http://www.w3.org/2000/svg',
        'svg'
      );
      dotted.setAttribute('height', '2');
      dotted.style.cssText =
        'display:block;position:absolute;left:0;bottom:0;width:100%;height:2px;color:var(--nl-secondary)';
      const line = context.document.createElementNS(
        'http://www.w3.org/2000/svg',
        'line'
      );
      for (const [key, value] of Object.entries({
        x1: '1',
        y1: '1',
        x2: '100%',
        y2: '1',
        stroke: 'currentColor',
        'stroke-width': '1.5',
        'stroke-dasharray': '0,4',
        'stroke-linecap': 'round',
      }))
        line.setAttribute(key, value);
      // OneKey patch: keep both round caps inside the original full-width viewport.
      const lineViewport = context.document.createElementNS(
        'http://www.w3.org/2000/svg',
        'svg'
      );
      lineViewport.setAttribute('width', 'calc(100% - 1px)');
      lineViewport.setAttribute('height', '2');
      lineViewport.setAttribute('overflow', 'visible');
      lineViewport.appendChild(line);
      dotted.appendChild(lineViewport);
      // OneKey patch: SVG intrinsic width must not expand the title action beyond its text.
      title.style.display = 'block';
      title.style.position = 'relative';
      title.style.width = 'fit-content';
      title.style.paddingBottom = '3px';
      text.style.display = 'block';
      title.replaceChildren(text, dotted);
    }
  }
  body.appendChild(column);
  if (row.value) {
    const value = createElement(
      context.document,
      row.valueActionKey ? 'button' : 'span',
      row.valueActionKey
        ? 'ok-native-list-action-button ok-native-list-section-value'
        : 'ok-native-list-value ok-native-list-section-value',
      row.value
    );
    applyValueSegments(value, row.valueSegments);
    if (row.presentation === 'networkSelector' && row.height !== undefined) {
      value.style.fontFamily = 'inherit';
      value.style.fontSize = '16px';
      value.style.lineHeight = '24px';
      value.style.fontWeight = '500';
      if (row.valueActionKey) {
        value.style.color = 'var(--nl-secondary)';
        value.style.padding = '0';
        value.style.flexShrink = '0';
      }
    }
    if (row.valueActionTestID) setData(value, 'testid', row.valueActionTestID);
    if (row.valueActionKey) {
      value.setAttribute('type', 'button');
      setData(value, 'nativeListAction', row.valueActionKey);
      markActionAnchorSource(value, 'trailingAccessory', 0);
    }
    body.appendChild(value);
  }
  if (row.valueIcon) {
    const valueIcon = createIconAction(
      context,
      row.valueIcon.name,
      row.valueIcon.actionKey,
      row.valueIcon.disabled,
      row.valueIcon.tintColor
    );
    markActionAnchorSource(valueIcon, 'trailingAccessory', 1);
    body.appendChild(valueIcon);
  }
  if (row.checkbox)
    body.appendChild(createCheckbox(context, row.key, row.checkbox));
  return body;
}

function createActionRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'action' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    [
      'ok-native-list-row',
      'ok-native-list-action-row',
      row.presentation === 'accountSelector'
        ? 'ok-native-list-account-action-row'
        : '',
    ]
      .filter(Boolean)
      .join(' ')
  );
  if (row.icon) body.appendChild(createVisual(context, row.icon)!);
  const title = createElement(
    context.document,
    'span',
    'ok-native-list-action-title',
    row.title
  );
  setData(title, 'tone', row.tone);
  if (row.presentation === 'accountSelector' && row.icon)
    title.style.fontWeight = '500';
  body.appendChild(title);
  if (row.checkbox)
    body.appendChild(createCheckbox(context, row.key, row.checkbox));
  appendAccessories(body, context, row.key, row.trailing);
  return body;
}

function createSystemRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'system' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-system'
  );
  setData(body, 'variant', row.variant);
  if ('presentation' in row)
    setData(body, 'nativeListPresentation', row.presentation);
  if (row.variant === 'loading' && row.loadingStyle === 'skeleton') {
    body.classList.add('ok-native-list-market-skeleton');
    const background = resolvedTheme(context.snapshot).background;
    const rgb = Number.parseInt(background.slice(1, 7), 16);
    const dark =
      ((rgb >> 16) & 255) * 0.299 +
        ((rgb >> 8) & 255) * 0.587 +
        (rgb & 255) * 0.114 <
      128;
    body.style.setProperty('--nl-skeleton-base', dark ? '#111111' : '#fafafa');
    body.style.setProperty(
      '--nl-skeleton-highlight',
      dark ? '#333333' : '#cdcdcd'
    );
    const left = createElement(
      context.document,
      'div',
      'ok-native-list-skeleton-left'
    );
    const mark = (width: number, height: number, circle = false) => {
      const element = createElement(
        context.document,
        'span',
        'ok-native-list-skeleton-mark'
      );
      element.style.width = `${width}px`;
      element.style.height = `${height}px`;
      if (circle) element.style.borderRadius = '50%';
      return element;
    };
    left.appendChild(mark(32, 32, true));
    const text = createElement(
      context.document,
      'div',
      'ok-native-list-skeleton-text'
    );
    text.appendChild(mark(80, 16));
    text.appendChild(mark(60, 12));
    left.appendChild(text);
    body.appendChild(left);
    const right = createElement(
      context.document,
      'div',
      'ok-native-list-skeleton-right'
    );
    right.appendChild(mark(80, 18));
    right.appendChild(mark(80, 18));
    body.appendChild(right);
    return body;
  }
  if (row.variant === 'loading' && row.loadingStyle === 'spinner') {
    body.style.justifyContent = 'center';
    body.style.padding = '16px';
    const spinner = createElement(
      context.document,
      'span',
      'ok-native-list-market-spinner'
    );
    spinner.setAttribute('role', 'progressbar');
    // Same 20px SVG and 750ms rotation as react-native-web ActivityIndicator.
    spinner.innerHTML =
      '<svg viewBox="0 0 32 32" width="20" height="20"><circle cx="16" cy="16" r="14" fill="none" stroke="currentColor" stroke-width="4" opacity="0.2"/><circle cx="16" cy="16" r="14" fill="none" stroke="currentColor" stroke-width="4" stroke-dasharray="80" stroke-dashoffset="60"/></svg>';
    body.appendChild(spinner);
    return body;
  }
  if ('presentation' in row && row.presentation === 'market') {
    if (row.variant === 'noMatch') {
      body.style.justifyContent = 'center';
      body.style.padding = '32px';
      const message = createElement(context.document, 'span', '', row.message);
      message.style.fontSize = '16px';
      message.style.lineHeight = '24px';
      body.appendChild(message);
      return body;
    }
    if (row.variant === 'end') {
      body.style.justifyContent = 'center';
      body.style.padding = '16px';
      body.style.gap = '8px';
      for (const width of [80, 4, 80]) {
        const mark = createElement(context.document, 'span', '');
        mark.style.width = String(width) + 'px';
        mark.style.height = width === 4 ? '4px' : '1px';
        mark.style.borderRadius = width === 4 ? '2px' : '0';
        mark.style.background = 'var(--nl-separator)';
        body.appendChild(mark);
      }
      return body;
    }
  }
  if (row.variant === 'warning') {
    body.classList.add('ok-native-list-warning');
    body.style.borderColor = row.borderColor ?? 'var(--nl-separator)';
    body.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-warning-title',
        row.title
      )
    );
    body.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-warning-message',
        row.message
      )
    );
    return body;
  }
  if (row.variant === 'loading')
    body.appendChild(
      createElement(context.document, 'span', 'ok-native-list-spinner')
    );
  const message =
    row.variant === 'spacer'
      ? ''
      : row.message ?? (row.variant === 'end' ? 'End' : '');
  if (message)
    body.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        message
      )
    );
  return body;
}

function createRailRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'rail' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-rail'
  );
  const visual = createVisual(context, row.visual);
  if (visual) body.appendChild(visual);
  body.appendChild(
    createElement(
      context.document,
      'span',
      'ok-native-list-rail-title',
      row.title
    )
  );
  if (row.status && row.status !== 'none')
    body.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        row.status
      )
    );
  if (row.badge) body.appendChild(createBadge(context, row.badge));
  return body;
}

function createMediaRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'mediaTile' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-media'
  );
  if (row.image && !row.imageState) {
    const image = createImage(context, row.image, 'ok-native-list-media-image');
    if (image) body.appendChild(image);
  } else {
    const placeholder = createElement(
      context.document,
      'span',
      'ok-native-list-media-image',
      row.imageState === 'error' ? '▧' : ''
    );
    setData(placeholder, 'state', row.imageState ?? 'empty');
    body.appendChild(placeholder);
  }
  const metadata = createElement(
    context.document,
    'div',
    'ok-native-list-media-meta'
  );
  const subtitleLine = createElement(
    context.document,
    'div',
    'ok-native-list-media-subtitle-row'
  );
  subtitleLine.appendChild(
    createElement(
      context.document,
      'span',
      'ok-native-list-media-subtitle',
      row.subtitle || '-'
    )
  );
  if (row.networkImage) {
    const network = createImage(
      context,
      row.networkImage,
      'ok-native-list-media-network'
    );
    if (network) subtitleLine.appendChild(network);
  }
  metadata.appendChild(subtitleLine);
  metadata.appendChild(
    createElement(
      context.document,
      'div',
      'ok-native-list-media-title',
      row.title
    )
  );
  if (row.badge) metadata.appendChild(createBadge(context, row.badge));
  body.appendChild(metadata);
  if (row.closeActionKey) {
    const close = createElement(
      context.document,
      'button',
      'ok-native-list-media-close',
      '×'
    );
    close.setAttribute('type', 'button');
    close.setAttribute('aria-label', 'Close');
    setData(close, 'nativeListAction', row.closeActionKey);
    markActionAnchorSource(close, 'mediaClose');
    body.appendChild(close);
  }
  return body;
}

function createMetricCell(
  context: RenderContext,
  metric: NonNullable<
    Extract<RowModel, { type: 'metricCard' }>['metrics']
  >[number],
  shaded: boolean
): HTMLElement {
  const cell = createElement(
    context.document,
    'div',
    'ok-native-list-composite-cell'
  );
  setData(cell, 'shaded', shaded);
  cell.appendChild(
    createElement(
      context.document,
      'div',
      'ok-native-list-secondary',
      metric.label
    )
  );
  const valueLine = createElement(context.document, 'div');
  valueLine.style.display = 'flex';
  valueLine.style.alignItems = 'center';
  valueLine.style.gap = '6px';
  if (metric.visual) {
    const visual = createVisual(context, metric.visual);
    if (visual) {
      visual.style.width = '16px';
      visual.style.height = '16px';
      visual.style.flexBasis = '16px';
      const image = visual.querySelector('img');
      if (image) {
        image.style.width = '16px';
        image.style.height = '16px';
      }
      valueLine.appendChild(visual);
    }
  }
  const value = createElement(
    context.document,
    'span',
    'ok-native-list-composite-value',
    metric.value
  );
  value.style.color = toneColor(metric.tone, 'primary');
  valueLine.appendChild(value);
  cell.appendChild(valueLine);
  return cell;
}

function createMetricRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'metricCard' }>
): HTMLElement {
  if (row.variant === 'activity' || row.variant === 'performance') {
    const body = createElement(
      context.document,
      'div',
      'ok-native-list-row ok-native-list-composite'
    );
    body.appendChild(
      createElement(
        context.document,
        'div',
        'ok-native-list-composite-heading',
        row.title
      )
    );
    const metrics = row.metrics ?? [];
    const firstLine = createElement(
      context.document,
      'div',
      'ok-native-list-composite-row'
    );
    metrics
      .slice(0, 2)
      .forEach((metric) =>
        firstLine.appendChild(createMetricCell(context, metric, false))
      );
    body.appendChild(firstLine);
    if (row.variant === 'activity') {
      body.appendChild(
        createElement(context.document, 'div', 'ok-native-list-divider')
      );
    } else {
      const progress = createElement(
        context.document,
        'div',
        'ok-native-list-progress'
      );
      const fill = createElement(context.document, 'span');
      fill.style.width = String(Math.round((row.progress ?? 0) * 100)) + '%';
      progress.appendChild(fill);
      body.appendChild(progress);
    }
    const secondLine = createElement(
      context.document,
      'div',
      'ok-native-list-composite-row'
    );
    metrics
      .slice(2)
      .forEach((metric) =>
        secondLine.appendChild(
          createMetricCell(context, metric, row.variant === 'performance')
        )
      );
    body.appendChild(secondLine);
    return body;
  }

  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-metric'
  );
  if (row.visual) {
    const visual = createVisual(context, row.visual);
    if (visual) body.appendChild(visual);
  }
  body.appendChild(
    createElement(
      context.document,
      'div',
      'ok-native-list-secondary',
      row.title
    )
  );
  body.appendChild(
    createElement(
      context.document,
      'div',
      'ok-native-list-metric-value',
      row.value
    )
  );
  if (row.trend) {
    const trend = createElement(
      context.document,
      'div',
      'ok-native-list-secondary',
      row.trend
    );
    trend.style.color =
      row.trendTone === 'positive'
        ? 'var(--nl-positive)'
        : row.trendTone === 'negative'
        ? 'var(--nl-negative)'
        : 'var(--nl-secondary)';
    body.appendChild(trend);
  }
  if (row.subtitle)
    body.appendChild(
      createElement(
        context.document,
        'div',
        'ok-native-list-secondary',
        row.subtitle
      )
    );
  if (row.badge) body.appendChild(createBadge(context, row.badge));
  return body;
}

function createDataRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'dataRow' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-data'
  );
  if (row.checkbox)
    body.appendChild(createCheckbox(context, row.key, row.checkbox));
  if (row.index !== undefined)
    body.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-index',
        String(row.index)
      )
    );
  if (row.favorite) {
    const favorite = createElement(
      context.document,
      'span',
      'ok-native-list-favorite',
      row.favoriteActive ? '★' : '☆'
    );
    setData(favorite, 'active', row.favoriteActive);
    body.appendChild(favorite);
  }
  if (row.leading) {
    const visual = createVisual(context, row.leading);
    if (visual) body.appendChild(visual);
  }
  row.columns.forEach((column) => {
    const cell = createElement(
      context.document,
      'span',
      'ok-native-list-data-cell'
    );
    cell.style.flex = String(column.weight ?? 1);
    setData(cell, 'align', column.alignment ?? 'start');
    const primary = createElement(
      context.document,
      'span',
      'ok-native-list-data-primary'
    );
    primary.style.color = toneColor(column.tone, 'primary');
    if (column.secondaryLeadingText)
      primary.appendChild(
        createElement(
          context.document,
          'span',
          'ok-native-list-secondary',
          column.secondaryLeadingText
        )
      );
    primary.appendChild(
      createElement(context.document, 'span', undefined, column.text)
    );
    if (column.key === 'asset' && row.badges?.length) {
      row.badges.forEach((badge) =>
        primary.appendChild(createBadge(context, badge))
      );
    }
    cell.appendChild(primary);
    if (column.secondaryText) {
      const secondary = createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        column.secondaryText
      );
      secondary.style.color = toneColor(column.secondaryTone, 'secondary');
      cell.appendChild(secondary);
    }
    body.appendChild(cell);
  });
  return body;
}

function createIdentityActivityOrMessageRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'identity' | 'activity' | 'message' }>
): HTMLElement {
  const presentation = row.type === 'identity' ? row.presentation : undefined;
  const body = createElement(
    context.document,
    'div',
    [
      'ok-native-list-row',
      'ok-native-list-standard',
      row.type === 'identity' && !presentation
        ? 'ok-native-list-identity-row'
        : '',
      presentation === 'networkSelector' ? 'ok-native-list-network-row' : '',
      presentation === 'walletSidebar' ? 'ok-native-list-wallet-row' : '',
      presentation === 'accountSelector' ? 'ok-native-list-account-row' : '',
    ]
      .filter(Boolean)
      .join(' ')
  );
  setData(
    body,
    'nativeListSelector',
    row.height !== undefined ? presentation : undefined
  );
  if (row.type === 'identity' && row.titleActionKey && row.titleActionOnHover) {
    setData(body, 'nativeListHoverAction', row.titleActionKey);
    markActionAnchorSource(body, 'leadingAction');
  }
  if (row.type === 'identity' && row.leadingAction) {
    const action = createIconAction(
      context,
      row.leadingAction.name,
      row.leadingAction.actionKey,
      row.leadingAction.disabled,
      row.leadingAction.tintColor
    );
    markActionAnchorSource(action, 'leadingAction');
    body.appendChild(action);
  }
  const visual = createVisual(
    context,
    visualFromRow(row),
    row.height !== undefined ? presentation : undefined
  );
  if (
    visual &&
    row.type === 'identity' &&
    row.height !== undefined &&
    row.presentation === 'walletSidebar' &&
    'fallbackIcon' in row.leading &&
    row.leading.fallbackIcon?.name === 'LockSolid'
  ) {
    // OneKey patch: hidden-wallet locks use WalletAvatar's full 40-point icon.
    const icon = visual.querySelector<SVGElement>(
      '.ok-native-list-visual-fallback svg'
    );
    if (icon) {
      icon.style.width = '40px';
      icon.style.height = '40px';
    }
    const fallback = visual.querySelector<HTMLElement>(
      '.ok-native-list-visual-fallback'
    );
    if (fallback) {
      fallback.style.borderRadius = '0';
      fallback.style.overflow = 'visible';
    }
  }
  if (
    visual &&
    row.type === 'identity' &&
    row.height !== undefined &&
    row.presentation === 'walletSidebar' &&
    'borderStyle' in row.leading &&
    row.leading.borderStyle === 'dashed'
  )
    visual.style.borderWidth = '1px';
  if (visual) body.appendChild(visual);
  if (row.type === 'activity' && row.secondaryLeading) {
    const secondVisual = createVisual(context, row.secondaryLeading);
    if (secondVisual) body.appendChild(secondVisual);
  }
  if (row.type === 'message' && row.unread)
    body.appendChild(
      createElement(context.document, 'span', 'ok-native-list-unread')
    );
  const title = row.title;
  const subtitle =
    row.type === 'identity'
      ? row.subtitle
      : row.type === 'activity'
      ? row.description
      : row.body;
  const column = createTextColumn(
    context,
    title,
    subtitle,
    row.type === 'identity' ? row.tertiary : undefined,
    row.type === 'identity' ? row.tertiaryTone : undefined,
    row.type === 'identity' && presentation !== 'walletSidebar'
      ? row.badges
      : undefined
  );
  // OneKey patch: match existing search, subtitle fragments, and sidebar badges.
  if (row.type === 'identity') {
    const titleElement = column.firstElementChild as HTMLElement;
    if (row.titleMatch?.length) {
      const firstText = titleElement.firstChild;
      if (firstText) firstText.remove();
      const fragment = context.document.createDocumentFragment();
      let offset = 0;
      row.titleMatch.forEach(({ start, end }) => {
        fragment.appendChild(
          context.document.createTextNode(row.title.slice(offset, start))
        );
        const match = createElement(
          context.document,
          'span',
          'ok-native-list-info',
          row.title.slice(start, end)
        );
        fragment.appendChild(match);
        offset = end;
      });
      fragment.appendChild(
        context.document.createTextNode(row.title.slice(offset))
      );
      titleElement.prepend(fragment);
    }
    if (row.subtitleSegments?.length) {
      column.querySelector('.ok-native-list-secondary')?.remove();
      const segments = createElement(
        context.document,
        'span',
        'ok-native-list-subtitle-segments'
      );
      row.subtitleSegments.forEach((segment) => {
        if (segment.separatorBefore)
          segments.appendChild(
            createElement(
              context.document,
              'span',
              'ok-native-list-subtitle-dot'
            )
          );
        const text = createElement(
          context.document,
          'span',
          'ok-native-list-secondary',
          segment.text
        );
        applyValueSegments(text, segment.textSegments, 14, 20, 400);
        setData(text, 'tone', segment.tone);
        text.style.color =
          segment.tone === 'disabled'
            ? 'var(--nl-disabled)'
            : segment.tone === 'caution'
            ? 'var(--nl-caution)'
            : toneColor(segment.tone, 'secondary');
        segments.appendChild(text);
      });
      column.insertBefore(segments, titleElement.nextSibling);
    }
    if (presentation === 'walletSidebar' && row.badges?.length) {
      const badges = createElement(
        context.document,
        'span',
        'ok-native-list-wallet-badges'
      );
      row.badges.forEach((badge) =>
        badges.appendChild(createBadge(context, badge))
      );
      column.appendChild(badges);
    }
  }
  if (row.type === 'activity' && row.status)
    column.appendChild(
      createElement(
        context.document,
        'span',
        'ok-native-list-secondary',
        row.status
      )
    );
  if (row.type === 'activity' && row.footerActions?.length) {
    const actions = createElement(
      context.document,
      'span',
      'ok-native-list-actions'
    );
    row.footerActions.forEach((action, slot) => {
      const button = createElement(
        context.document,
        'button',
        'ok-native-list-action-button',
        action.label
      );
      button.setAttribute('type', 'button');
      button.toggleAttribute('disabled', Boolean(action.disabled));
      setData(button, 'tone', action.tone);
      setData(button, 'nativeListAction', action.key);
      markActionAnchorSource(button, 'footerAction', slot);
      actions.appendChild(button);
    });
    column.appendChild(actions);
  }
  body.appendChild(column);
  if (row.type === 'activity') {
    const amounts = createElement(
      context.document,
      'span',
      'ok-native-list-amounts'
    );
    if (row.primaryAmount)
      amounts.appendChild(
        createElement(
          context.document,
          'span',
          'ok-native-list-value',
          row.primaryAmount
        )
      );
    if (row.secondaryAmount)
      amounts.appendChild(
        createElement(
          context.document,
          'span',
          'ok-native-list-secondary',
          row.secondaryAmount
        )
      );
    body.appendChild(amounts);
  } else if (row.type === 'message') {
    body.appendChild(
      createElement(context.document, 'span', 'ok-native-list-time', row.time)
    );
    if (row.thumbnail) {
      const thumbnail = createImage(
        context,
        row.thumbnail,
        'ok-native-list-thumbnail'
      );
      if (thumbnail) body.appendChild(thumbnail);
    }
  } else {
    appendAccessories(body, context, row.key, row.trailing);
  }
  return body;
}

// OneKey patch: SizableText enables tabular digits without replacing its font family.
function applySelectorTabularNumbers(body: HTMLElement, row: RowModel) {
  if (
    !('presentation' in row) ||
    !['accountSelector', 'networkSelector', 'walletSidebar'].includes(
      row.presentation ?? ''
    )
  )
    return;
  body.style.fontVariantNumeric = 'tabular-nums';
  body.querySelectorAll<HTMLElement>('span,button').forEach((text) => {
    text.style.fontVariantNumeric = 'tabular-nums';
  });
}

function createWalletGroupRow(
  context: RenderContext,
  row: Extract<RowModel, { type: 'walletGroup' }>
): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-wallet-group'
  );
  [row.parent, ...row.children].forEach((member, memberIndex) => {
    const memberElement = createElement(
      context.document,
      'div',
      'ok-native-list-wallet-member'
    );
    setData(memberElement, 'nativeListGroupMemberKey', member.key);
    setData(memberElement, 'testid', member.testID);
    setData(memberElement, 'nativeListGroupParent', memberIndex === 0);
    setData(memberElement, 'nativeListSelected', member.selected);
    // OneKey patch: grouped members have the same selector typography as standalone wallets.
    // memberElement.appendChild(createIdentityActivityOrMessageRow(context, member));
    const memberBody = createIdentityActivityOrMessageRow(context, member);
    applySelectorTabularNumbers(memberBody, member);
    memberElement.appendChild(memberBody);
    // OneKey patch: group children use their own measured badge height.
    memberElement.style.flexBasis =
      String(member.height ?? 68 + (member.badges?.length ? 24 : 0)) + 'px';
    memberElement.style.height = memberElement.style.flexBasis;
    memberElement.style.opacity = String(member.opacity ?? 1);
    body.appendChild(memberElement);
  });
  return body;
}

function marketFontWeight(
  value: MarketTextStyle['fontWeight'] | undefined,
  fallback: number
): number {
  if (value === 'bold') return 700;
  if (value === 'semibold') return 600;
  if (value === 'medium') return 500;
  if (value === 'regular') return 400;
  return fallback;
}

function applyMarketTextStyle(
  element: HTMLElement,
  style: MarketTextStyle | undefined,
  defaults: Readonly<{
    fontSize: number;
    lineHeight: number;
    weight: number;
    alignment: 'start' | 'center' | 'end';
  }>
) {
  element.style.fontSize = String(style?.fontSize ?? defaults.fontSize) + 'px';
  element.style.lineHeight =
    String(style?.lineHeight ?? defaults.lineHeight) + 'px';
  element.style.fontWeight = String(
    marketFontWeight(style?.fontWeight, defaults.weight)
  );
  element.style.color = style?.color ?? '';
  element.style.textAlign = style?.alignment ?? defaults.alignment;
  element.style.whiteSpace = style?.lines === 2 ? 'normal' : 'nowrap';
  element.style.display = '';
  element.style.removeProperty('-webkit-line-clamp');
  element.style.removeProperty('-webkit-box-orient');
  if (style?.lines === 2) {
    element.style.display = '-webkit-box';
    element.style.setProperty('-webkit-line-clamp', '2');
    element.style.setProperty('-webkit-box-orient', 'vertical');
  }
}

function setMarketQuoteContent(element: HTMLElement, row: MarketRow) {
  const style = row.style;
  const layoutStyle = resolveWebMarketLayoutStyle(row);
  const price = element.querySelector<HTMLElement>(
    '.ok-native-list-market-price'
  );
  if (price) {
    price.textContent = row.price;
    applyMarketTextStyle(price, style?.price, {
      fontSize: 16,
      lineHeight: 24,
      weight: 500,
      alignment: 'end',
    });
    applyValueSegments(
      price,
      row.priceSegments,
      style?.price?.fontSize ?? 16,
      style?.price?.lineHeight ?? 24,
      marketFontWeight(style?.price?.fontWeight, 500)
    );
  }
  const change = element.querySelector<HTMLElement>(
    '.ok-native-list-market-change'
  );
  if (change) {
    change.textContent = row.change.text;
    setData(change, 'tone', row.change.tone);
    applyMarketTextStyle(change, style?.change, {
      fontSize: 14,
      lineHeight: 20,
      weight: 500,
      alignment: 'center',
    });
    applyValueSegments(
      change,
      row.change.textSegments,
      style?.change?.fontSize ?? 14,
      style?.change?.lineHeight ?? 20,
      marketFontWeight(style?.change?.fontWeight, 500)
    );
    change.style.width = String(layoutStyle.changeWidth) + 'px';
    change.style.height = String(layoutStyle.changeHeight) + 'px';
    change.style.borderRadius = String(layoutStyle.changeCornerRadius) + 'px';
    change.style.color = row.change.textColor ?? style?.change?.color ?? '';
    change.style.background = row.change.backgroundColor ?? '';
  }
  element.setAttribute(
    'aria-label',
    row.accessibilityLabel ??
      [row.title, row.subtitle, row.price, row.change.text]
        .filter(Boolean)
        .join(', ')
  );
}

function createMarketRow(context: RenderContext, row: MarketRow): HTMLElement {
  const body = createElement(
    context.document,
    'div',
    'ok-native-list-row ok-native-list-market'
  );
  setData(body, 'variant', row.variant);
  const style = row.style;
  const layoutStyle = resolveWebMarketLayoutStyle(row);
  body.style.padding =
    String(layoutStyle.verticalPadding) +
    'px ' +
    String(layoutStyle.horizontalPadding) +
    'px';
  body.style.gap = '0px';
  if (row.leadingAction) {
    const action = createIconAction(
      context,
      row.leadingAction.name,
      row.leadingAction.actionKey,
      row.leadingAction.disabled,
      row.leadingAction.tintColor
    );
    action.style.flex = '0 0 36px';
    action.style.width = '36px';
    action.style.height = '36px';
    action.style.marginRight = '5px';
    setData(action, 'testid', row.leadingAction.testID);
    if (row.leadingAction.accessibilityLabel)
      action.setAttribute('aria-label', row.leadingAction.accessibilityLabel);
    markActionAnchorSource(action, 'leadingAction');
    body.appendChild(action);
  }
  const visual = createVisual(context, row.leading);
  if (visual) {
    const width = layoutStyle.imageWidth;
    const height = layoutStyle.imageHeight;
    visual.style.width = String(width) + 'px';
    visual.style.height = String(height) + 'px';
    visual.style.flexBasis = String(width) + 'px';
    visual.style.borderRadius = String(layoutStyle.imageCornerRadius) + 'px';
    const borderColor =
      'borderColor' in row.leading ? row.leading.borderColor : undefined;
    if (borderColor) {
      visual.style.border = '1px solid ' + borderColor;
      visual.style.boxSizing = 'border-box';
    }
    visual.querySelectorAll<HTMLElement>('img').forEach((image) => {
      if (!image.classList.contains('ok-native-list-visual-corner')) {
        image.style.width = '100%';
        image.style.height = '100%';
        if (borderColor) {
          image.style.borderRadius = '0';
          image.style.clipPath =
            'inset(-1px round ' + String(layoutStyle.imageCornerRadius) + 'px)';
        }
        image.style.objectFit =
          style?.image?.contentFit ?? image.style.objectFit;
      } else {
        image.style.width = '20px';
        image.style.height = '20px';
        if (borderColor) {
          image.style.right = '-5px';
          image.style.bottom = '-5px';
        }
      }
    });
    visual.style.marginRight = String(layoutStyle.leadingGap) + 'px';
    body.appendChild(visual);
  }
  const main = createElement(
    context.document,
    'span',
    'ok-native-list-market-main'
  );
  main.style.gap = String(style?.lineGap ?? 0) + 'px';
  const titleLine = createElement(
    context.document,
    'span',
    'ok-native-list-market-title-line'
  );
  titleLine.style.gap = String(layoutStyle.titleBadgeGap) + 'px';
  const title = createElement(
    context.document,
    'span',
    'ok-native-list-market-title',
    row.title
  );
  applyMarketTextStyle(title, style?.title, {
    fontSize: 16,
    lineHeight: 24,
    weight: 500,
    alignment: 'start',
  });
  titleLine.appendChild(title);
  row.badges?.forEach((badge, slot) => {
    const element = createElement(
      context.document,
      badge.actionKey ? 'button' : 'span',
      'ok-native-list-market-badge',
      badge.text
    );
    if (badge.actionKey) {
      element.setAttribute('type', 'button');
      setData(element, 'nativeListAction', badge.actionKey);
      markActionAnchorSource(element, 'marketBadge', slot);
    }
    setData(element, 'tone', badge.tone);
    element.style.color =
      badge.textColor ??
      (badge.tone === 'success'
        ? 'var(--nl-positive)'
        : badge.tone === 'danger'
        ? 'var(--nl-negative)'
        : badge.tone === 'info'
        ? 'var(--nl-info)'
        : badge.tone === 'warning'
        ? 'var(--nl-primary)'
        : '');
    element.style.background =
      badge.backgroundColor ??
      ((badge.icon || badge.iconName) && !badge.text ? 'transparent' : '');
    if ((badge.icon || badge.iconName) && !badge.text) {
      element.style.padding = '0';
    }
    if (badge.accessibilityLabel)
      element.setAttribute('aria-label', badge.accessibilityLabel);
    if (badge.icon) {
      const icon = createImage(context, badge.icon);
      if (icon) {
        icon.style.borderRadius = '50%';
        element.prepend(icon);
      }
    }
    if (badge.iconName === 'verified') {
      const icon = createElement(
        context.document,
        'span',
        'ok-native-list-market-badge-icon'
      );
      applySelectorIcon(icon, 'BadgeVerifiedSolid');
      element.prepend(icon);
    }
    titleLine.appendChild(element);
  });
  main.appendChild(titleLine);
  if (row.subtitlePrefix || row.subtitle || row.subtitleSegments?.length) {
    const subtitleLine = createElement(
      context.document,
      'span',
      'ok-native-list-market-subtitle-line'
    );
    subtitleLine.style.display = 'flex';
    subtitleLine.style.alignItems = 'center';
    subtitleLine.style.minWidth = '0';
    subtitleLine.style.overflow = 'hidden';
    subtitleLine.style.gap =
      String(row.subtitlePrefix ? row.subtitlePrefix.gap ?? 4 : 0) + 'px';
    if (row.subtitlePrefix) {
      const prefix = createElement(
        context.document,
        'span',
        'ok-native-list-market-subtitle-prefix',
        row.subtitlePrefix.text
      );
      applyMarketTextStyle(prefix, row.subtitlePrefix.style, {
        fontSize: 12,
        lineHeight: 16,
        weight: 400,
        alignment: 'start',
      });
      prefix.style.display = 'block';
      prefix.style.flex = '1 1 auto';
      prefix.style.minWidth = '0';
      prefix.style.overflow = 'hidden';
      prefix.style.textOverflow = 'ellipsis';
      prefix.style.color =
        row.subtitlePrefix.style?.color ?? 'var(--nl-secondary)';
      if (row.subtitlePrefix.maxWidth !== undefined) {
        prefix.style.maxWidth = String(row.subtitlePrefix.maxWidth) + 'px';
      }
      subtitleLine.appendChild(prefix);
    }
    if (row.subtitle || row.subtitleSegments?.length) {
      const subtitle = createElement(
        context.document,
        'span',
        'ok-native-list-market-subtitle',
        row.subtitle
      );
      applyMarketTextStyle(subtitle, style?.subtitle, {
        fontSize: 14,
        lineHeight: 20,
        weight: 400,
        alignment: 'start',
      });
      applyValueSegments(
        subtitle,
        row.subtitleSegments,
        style?.subtitle?.fontSize ?? 14,
        style?.subtitle?.lineHeight ?? 20,
        marketFontWeight(style?.subtitle?.fontWeight, 400)
      );
      subtitle.style.flex = row.subtitlePrefix ? '0 0 auto' : '1 1 auto';
      subtitleLine.appendChild(subtitle);
    }
    main.appendChild(subtitleLine);
  }
  body.appendChild(main);
  const trailing = createElement(
    context.document,
    'span',
    'ok-native-list-market-trailing'
  );
  trailing.style.gap = String(layoutStyle.trailingGap) + 'px';
  trailing.append(
    createElement(context.document, 'span', 'ok-native-list-market-price'),
    createElement(context.document, 'span', 'ok-native-list-market-change')
  );
  body.appendChild(trailing);
  setMarketQuoteContent(body, row);
  return body;
}

function createRowBody(context: RenderContext, row: RowModel): HTMLElement {
  switch (row.type) {
    case 'walletGroup':
      return createWalletGroupRow(context, row);
    case 'sectionHeader':
      return createSectionHeader(context, row);
    case 'action':
      return createActionRow(context, row);
    case 'system':
      return createSystemRow(context, row);
    case 'rail':
      return createRailRow(context, row);
    case 'mediaTile':
      return createMediaRow(context, row);
    case 'metricCard':
      return createMetricRow(context, row);
    case 'dataRow':
      return createDataRow(context, row);
    case 'market':
      return createMarketRow(context, row);
    case 'identity':
    case 'activity':
    case 'message':
      return createIdentityActivityOrMessageRow(context, row);
  }
}

export class NativeListWebEngine {
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
  private readonly pool: HTMLElement[] = [];
  private frameHandle: number | undefined;
  private resizeObserver: ResizeObserver | undefined;
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
  private readonly actionAnchorInstanceId = String(
    ++webNativeListInstanceCounter
  );
  private actionAnchorCounter = 0;
  private bindingEpochCounter = 0;
  private marketPointer:
    | Readonly<{
        pointerId: number;
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
  // OneKey patch: warning banners are measured after normal browser text wrapping.
  private measuredWarningHeights = new Map<string, number>();

  constructor(
    host: HTMLElement,
    snapshot: NativeListSnapshot,
    callbacks: NativeListWebCallbacks,
    virtualizationEnabled = true
  ) {
    this.document = host.ownerDocument;
    this.snapshot = validateSnapshot(snapshot);
    this.callbacks = callbacks;
    this.virtualizationEnabled = virtualizationEnabled;
    this.previousHostPosition = host.style.position;
    if (!host.style.position) host.style.position = 'relative';

    this.root = createElement(this.document, 'div', 'ok-native-list-root');
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

    this.viewport.addEventListener('scroll', this.handleScroll, {
      passive: true,
    });
    this.viewport.addEventListener(
      'ok-collapsible-pager-insets-changed',
      this.handleCollapsiblePagerInsetsChanged
    );
    this.root.addEventListener('click', this.handleClick);
    this.root.addEventListener('pointerdown', this.handleMarketPointerDown);
    this.root.addEventListener('pointermove', this.handleMarketPointerMove);
    this.root.addEventListener('pointerup', this.handleMarketPointerEnd);
    this.root.addEventListener('pointercancel', this.handleMarketPointerEnd);
    // OneKey patch: preserve web tooltip hover for section titles.
    this.root.addEventListener('pointerover', this.handleTitlePointerOver);
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
    this.viewport.addEventListener('pointerdown', this.handlePullStart, {
      passive: true,
    });
    this.viewport.addEventListener('pointermove', this.handlePullMove, {
      passive: false,
    });
    this.viewport.addEventListener('pointerup', this.handlePullEnd);
    this.viewport.addEventListener('pointercancel', this.handlePullEnd);

    const ResizeObserverConstructor = this.document.defaultView?.ResizeObserver;
    if (ResizeObserverConstructor) {
      this.resizeObserver = new ResizeObserverConstructor(() =>
        this.recomputeLayout()
      );
      this.resizeObserver.observe(this.viewport);
    } else {
      this.document.defaultView?.addEventListener(
        'resize',
        this.handleWindowResize
      );
    }
    this.applySnapshot(this.snapshot);
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
    const validated = validateSnapshot(snapshot);
    this.invalidateActionAnchor('snapshot');
    this.setSnapshot(validated);
  }

  applyPatches(patches: readonly RowPatch[]) {
    if (patches.length === 0) return;
    const next = applyRowPatches(this.snapshot, patches);
    const selection = new Set(this.selectedKeys);
    patches.forEach((patch) => {
      const selected =
        'selected' in patch.changes ? patch.changes.selected : undefined;
      if (selected === true) selection.add(patch.key);
      else if (selected === false) selection.delete(patch.key);
    });
    if (webMarketImageBindDeltaForPatches(patches) === 0) {
      this.snapshot = next;
      this.rows = effectiveRows(next);
      this.selectedKeys = selection;
      patches.forEach((patch) => {
        const index = this.rows.findIndex((row) => row.key === patch.key);
        const row = this.rows[index];
        const element = this.mounted.get(index);
        if (row?.type === 'market' && element) {
          setMarketQuoteContent(element, row);
          element.dataset.renderSignature = webRowRenderSignature(row);
        }
        if (
          row?.type === 'market' &&
          this.sticky.dataset.nativeListRowKey === row.key
        ) {
          setMarketQuoteContent(this.sticky, row);
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
    if (!this.canScroll()) {
      this.pendingScroll = { kind: 'offset', offset, animated };
      return;
    }
    this.scrollToAbsoluteOffset(offset, animated);
  }

  scrollToEnd(animated: boolean) {
    if (!this.canScroll()) {
      this.pendingScroll = { kind: 'end', animated };
      return;
    }
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
    this.invalidateActionAnchor('destroy');
    this.destroyed = true;
    if (this.frameHandle !== undefined) this.cancelFrame(this.frameHandle);
    if (this.reorderMovementTimer !== undefined)
      this.document.defaultView?.clearTimeout(this.reorderMovementTimer);
    this.resizeObserver?.disconnect();
    this.document.defaultView?.removeEventListener(
      'resize',
      this.handleWindowResize
    );
    this.viewport.removeEventListener('scroll', this.handleScroll);
    this.viewport.removeEventListener(
      'ok-collapsible-pager-insets-changed',
      this.handleCollapsiblePagerInsetsChanged
    );
    this.root.removeEventListener('click', this.handleClick);
    this.cancelMarketPointer();
    this.root.removeEventListener('pointerdown', this.handleMarketPointerDown);
    this.root.removeEventListener('pointermove', this.handleMarketPointerMove);
    this.root.removeEventListener('pointerup', this.handleMarketPointerEnd);
    this.root.removeEventListener('pointercancel', this.handleMarketPointerEnd);
    this.root.removeEventListener('pointerover', this.handleTitlePointerOver);
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
    this.viewport.removeEventListener('pointerdown', this.handlePullStart);
    this.viewport.removeEventListener('pointermove', this.handlePullMove);
    this.viewport.removeEventListener('pointerup', this.handlePullEnd);
    this.viewport.removeEventListener('pointercancel', this.handlePullEnd);
    const host = this.root.parentElement;
    disposeWebImageRetries(this.root);
    this.pool.forEach(disposeWebImageRetries);
    this.root.remove();
    this.hideReorderPreview();
    this.reorderPreview.remove();
    if (host) host.style.position = this.previousHostPosition;
    this.mounted.clear();
    this.pool.length = 0;
    this.avatarLeases.forEach((release) => release());
    this.avatarLeases.clear();
  }

  private setSnapshot(
    snapshot: NativeListSnapshot,
    selectedKeys?: ReadonlySet<string>
  ) {
    this.cancelMarketPointer();
    this.measuredWarningHeights.clear();
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
      '--nl-subdued': theme.subduedBackground,
      '--nl-strong': theme.strongBackground,
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
      '--nl-negative': theme.negative,
      '--nl-critical': theme.criticalBackground,
      '--nl-inverse': theme.inverseBackground,
      '--nl-inverse-text': theme.inverseText,
      '--nl-info': theme.info,
    };
    Object.entries(values).forEach(([name, value]) => {
      if (!value) return;
      this.root.style.setProperty(name, value);
      this.reorderPreview.style.setProperty(name, value);
    });
  }

  private recomputeLayout = () => {
    if (this.destroyed) return;
    const viewportWidth = this.viewport.clientWidth;
    const viewportHeight =
      this.snapshot.layout.orientation === 'horizontal'
        ? this.viewport.clientHeight
        : this.verticalScrollMetrics().viewportLength;
    if (this.snapshot.layout.orientation !== 'horizontal') {
      const stickyInset = this.collapsiblePagerInsets().sticky;
      this.sticky.style.top = String(stickyInset) + 'px';
      this.indexRail.style.top = String(stickyInset) + 'px';
      this.refreshIndicator.style.top = String(stickyInset + 8) + 'px';
    } else {
      this.sticky.style.top = '0px';
      this.indexRail.style.top = '0px';
      this.refreshIndicator.style.top = '8px';
    }
    if (
      this.lastViewportWidth >= 0 &&
      (viewportWidth !== this.lastViewportWidth ||
        viewportHeight !== this.lastViewportHeight)
    ) {
      this.invalidateActionAnchor('layout');
      this.measuredWarningHeights.clear();
    }
    this.lastViewportWidth = viewportWidth;
    this.lastViewportHeight = viewportHeight;
    const previousHorizontal = this.layout.horizontal;
    const measuredSnapshot: NativeListSnapshot = {
      ...this.snapshot,
      rows: this.snapshot.rows.map((row) =>
        row.type === 'system' &&
        row.variant === 'warning' &&
        row.height === undefined &&
        this.measuredWarningHeights.has(row.key)
          ? { ...row, height: this.measuredWarningHeights.get(row.key) }
          : row
      ),
    };
    this.layout = computeWebListLayout(
      measuredSnapshot,
      viewportWidth,
      viewportHeight,
      this.reorderCompactKey
    );
    this.content.style.width = String(this.layout.contentWidth) + 'px';
    this.content.style.height = String(this.layout.contentHeight) + 'px';
    this.renderSectionIndex(
      this.viewport.clientHeight <= 0
        ? DEFAULT_VIEWPORT_HEIGHT
        : Math.max(1, viewportHeight)
    );
    if (previousHorizontal !== this.layout.horizontal) {
      this.viewport.scrollLeft = 0;
      this.viewport.scrollTop = 0;
    }
    this.renderWindow();
    this.performPendingScroll();
  };

  private updateAvatarWindow() {
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
    const visible = webLayoutItemsForMount(
      this.layout,
      this.currentOffset(),
      viewportLength,
      this.virtualizationEnabled,
      viewportLength * OVERSCAN_VIEWPORTS
    );
    const desired = new Set(visible.map((item) => item.index));
    this.mounted.forEach((element, index) => {
      if (!desired.has(index)) {
        this.invalidateActionAnchorForElement(element);
        this.mounted.delete(index);
        if (disposeWebImageRetries(element))
          element.removeAttribute('data-render-signature');
        element.remove();
        this.pool.push(element);
      }
    });

    visible.forEach((layoutItem) => {
      const row = this.rows[layoutItem.index];
      if (!row) return;
      let element = this.mounted.get(layoutItem.index);
      if (!element) {
        element =
          this.pool.pop() ??
          createElement(this.document, 'div', 'ok-native-list-item');
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
    let measuredWarningChanged = false;
    this.mounted.forEach((element, index) => {
      const row = this.rows[index];
      if (
        row?.type !== 'system' ||
        row.variant !== 'warning' ||
        row.height !== undefined
      )
        return;
      const height =
        element.querySelector<HTMLElement>('.ok-native-list-warning')
          ?.offsetHeight ?? 0;
      if (height > 0 && height !== this.measuredWarningHeights.get(row.key)) {
        this.measuredWarningHeights.set(row.key, height);
        measuredWarningChanged = true;
      }
    });
    if (measuredWarningChanged) {
      this.recomputeLayout();
      return;
    }
    this.updateVisibleSelection();
    this.updateVisibleState();
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
    disposeWebImageRetries(element);
    const bindingEpoch = String(++this.bindingEpochCounter);
    element.className = overlay
      ? 'ok-native-list-item ok-native-list-sticky'
      : 'ok-native-list-item';
    setData(element, 'nativeListRowKey', row.key);
    setData(element, 'nativeListBindingEpoch', bindingEpoch);
    setData(element, 'nativeListRowIndex', index);
    setData(element, 'nativeListDisabled', Boolean(row.disabled));
    setData(element, 'testid', row.testID);
    // OneKey patch: deprecation dims a row without disabling its actions.
    element.style.opacity = String(row.opacity ?? 1);
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
    if (!row.disabled && !(row.type === 'system' && row.variant === 'spacer')) {
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
    const body = createRowBody(context, row);
    applySelectorTabularNumbers(body, row);
    // OneKey patch: explicit selector fields preserve original page geometry.
    element.style.contain = row.backgroundFullWidth ? 'layout style' : '';
    if (row.backgroundColor) body.style.backgroundColor = row.backgroundColor;
    if (row.backgroundFullWidth && row.backgroundColor) {
      const bleed = paddingValues(this.snapshot).horizontal;
      body.style.position = 'relative';
      body.style.overflow = 'visible';
      body.style.boxShadow =
        String(-bleed) +
        'px 0 ' +
        row.backgroundColor +
        ',' +
        String(bleed) +
        'px 0 ' +
        row.backgroundColor;
    }
    if (row.type === 'identity' && row.height !== undefined) {
      const title = body.querySelector<HTMLElement>('.ok-native-list-title');
      if (row.presentation === 'accountSelector') {
        body.style.gap = '12px';
        body.style.borderRadius = '12px';
        if ('shape' in row.leading && row.leading.shape === 'rounded') {
          const visual = body.querySelector<HTMLElement>(
            '.ok-native-list-visual'
          );
          if (visual) visual.style.borderRadius = '8px';
        }
        if (title) title.style.lineHeight = '24px';
      }
      if (row.presentation === 'networkSelector') {
        body.style.borderRadius = '12px';
        const visual = body.querySelector<HTMLElement>(
          '.ok-native-list-visual'
        );
        if (visual) {
          visual.style.width = '32px';
          visual.style.height = '32px';
          visual.style.flexBasis = '32px';
        }
        if (
          row.leading.kind === 'network' &&
          !row.leading.image &&
          !row.leading.fallbackIcon &&
          row.leading.fallbackText
        ) {
          const fallback = visual?.querySelector<HTMLElement>(
            '.ok-native-list-visual-fallback'
          );
          if (fallback) {
            fallback.style.fontSize = '19px';
            fallback.style.lineHeight = '27px';
            fallback.style.fontWeight = '600';
            fallback.style.color = 'var(--nl-inverse-text)';
          }
        }
        visual
          ?.querySelectorAll<HTMLElement>('.ok-native-list-visual-main')
          .forEach((image) => {
            image.style.width = '32px';
            image.style.height = '32px';
          });
        if (title) {
          title.style.fontSize = '16px';
          title.style.lineHeight = '24px';
          title.style.fontWeight = '500';
        }
        body
          .querySelectorAll<HTMLElement>('.ok-native-list-accessory')
          .forEach((value) => {
            value.style.fontSize = '16px';
            value.style.lineHeight = '24px';
            value.style.fontWeight = '500';
          });
      }
    }
    element.replaceChildren(body);
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
      Math.floor(trackHeight / SECTION_INDEX_LABEL_SPACING)
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
    const availableHeight = Math.max(
      0,
      viewportHeight - SECTION_INDEX_EDGE_PADDING * 2
    );
    const trackHeight = Math.min(
      availableHeight,
      SECTION_INDEX_LABEL_SPACING * this.sectionIndexEntries.length
    );
    return {
      originY: (viewportHeight - trackHeight) / 2,
      trackHeight,
    };
  }

  private renderSectionIndex(viewportHeight: number) {
    this.indexRail.replaceChildren();
    if (!sectionIndexEnabled(this.snapshot)) {
      this.sectionIndexEntries = [];
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
      this.indexRail.hidden = true;
      return;
    }
    const visibleEntryIndices =
      this.sectionIndexVisibleEntryIndices(viewportHeight);
    setData(
      this.indexRail,
      'compact',
      visibleEntryIndices.length < this.sectionIndexEntries.length
    );
    const metrics = this.sectionIndexMetrics(viewportHeight);
    const visibleTrackHeight = Math.min(
      metrics.trackHeight,
      SECTION_INDEX_LABEL_SPACING * visibleEntryIndices.length
    );
    const visibleOriginY =
      metrics.originY + (metrics.trackHeight - visibleTrackHeight) / 2;
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
            (visibleTrackHeight * (visibleIndex + 0.5)) /
              visibleEntryIndices.length
        ) + 'px';
      fragment.appendChild(button);
    });
    this.indexRail.appendChild(fragment);
    this.indexRail.hidden = this.indexRail.childElementCount === 0;
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
    const visible = visibleWebLayoutItems(
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
    let activeKey: string | undefined;
    this.snapshot.rows.forEach((row, index) => {
      if (
        // OneKey patch: a spacer ending exactly at the viewport is not the active section.
        // index <= firstVisibleIndex &&
        itemStart(this.layout.items[index], this.layout.horizontal) <=
          this.currentOffset() &&
        row.type === 'sectionHeader' &&
        row.sticky !== false &&
        row.indexTitle
      )
        activeKey = row.key;
    });
    this.indexRail
      .querySelectorAll<HTMLElement>('[data-section-key]')
      .forEach((button) =>
        setData(button, 'active', button.dataset.sectionKey === activeKey)
      );
  }

  private checkEndReached(lastVisibleIndex: number) {
    if (
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
    const viewportLength = this.layout.horizontal
      ? this.viewport.clientWidth
      : this.viewport.clientHeight;
    return viewportLength > 0 && this.layout.items.length > 0;
  }

  private viewportLength(): number {
    if (this.layout.horizontal) {
      return this.viewport.clientWidth || DEFAULT_VIEWPORT_WIDTH;
    }
    if (this.viewport.clientHeight <= 0) return DEFAULT_VIEWPORT_HEIGHT;
    return Math.max(1, this.verticalScrollMetrics().viewportLength);
  }

  private contentLength(): number {
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

  private scrollToAbsoluteOffset(offset: number, animated: boolean) {
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
    rowElement?: HTMLElement
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
    if (row.disabled || row.pressDisabled) return;
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

  private handleMarketPointerDown = (event: PointerEvent) => {
    if (event.button !== 0 || !(event.target instanceof Element)) return;
    this.marketLongPressFired = false;
    if (event.target.closest('[data-native-list-action]')) return;
    const rowElement = event.target.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(rowElement);
    if (row?.type !== 'market' || row.disabled) return;
    this.cancelMarketPointer();
    if (row.pressInActionKey)
      this.emitRowAction(
        row,
        row.pressInActionKey,
        rowElement ?? undefined,
        rowElement ?? undefined
      );
    if (!row.longPressActionKey || !rowElement) return;
    markActionAnchorSource(rowElement, 'row');
    const timer = this.document.defaultView?.setTimeout(() => {
      const current = this.marketPointer;
      if (!current || current.pointerId !== event.pointerId) return;
      this.marketPointer = undefined;
      this.marketLongPressFired = true;
      this.emitRowAction(row, row.longPressActionKey!, rowElement, rowElement);
    }, MARKET_LONG_PRESS_MS);
    if (timer === undefined) return;
    this.marketPointer = {
      pointerId: event.pointerId,
      rowKey: row.key,
      startX: event.clientX,
      startY: event.clientY,
      timer,
    };
  };

  private handleMarketPointerMove = (event: PointerEvent) => {
    const state = this.marketPointer;
    if (!state || state.pointerId !== event.pointerId) return;
    if (
      Math.abs(event.clientX - state.startX) > MARKET_MOVE_CANCEL_PX ||
      Math.abs(event.clientY - state.startY) > MARKET_MOVE_CANCEL_PX
    )
      this.cancelMarketPointer();
  };

  private handleMarketPointerEnd = (event: PointerEvent) => {
    if (this.marketPointer?.pointerId === event.pointerId)
      this.cancelMarketPointer();
  };

  // OneKey patch: hover opens the same anchored help action as native taps.
  private handleTitlePointerOver = (event: PointerEvent) => {
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
    if (sourceRow && !sourceRow.disabled && actionKey)
      this.emitRowAction(sourceRow, actionKey, action, rowElement ?? undefined);
  };

  private handleTitlePointerOut = (event: PointerEvent) => {
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
    const action = target.closest<HTMLElement>('[data-native-list-action]');
    if (action) {
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

  private handleScroll = () => {
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
      this.viewport.scrollTop > 0 ||
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
    const groupParent = row.querySelector<HTMLElement>(
      '[data-native-list-group-parent="true"]>.ok-native-list-wallet-row'
    );
    const previewRow = groupParent ?? row;
    const previewHeight = groupParent ? 68 : rect.height;
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
    const sourceRow = state.workingRows[state.currentIndex];
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
    const top =
      viewportRect.top +
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
