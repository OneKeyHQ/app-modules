import type {
  CheckboxState,
  ImageSource,
  LeadingVisual,
  NativeListSnapshot,
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

const SECTION_INDEX_GUTTER = 44;
const DEFAULT_VIEWPORT_WIDTH = 320;
const DEFAULT_VIEWPORT_HEIGHT = 640;
const OVERSCAN_VIEWPORTS = 1;

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

export type NativeListWebCallbacks = Readonly<{
  onRowAction?: (event: RowActionEvent) => void;
  onSelectionDelta?: (event: SelectionDeltaEvent) => void;
  onReorder?: (event: ReorderEvent) => void;
  onEndReached?: (event: { generation: number; lastKey?: string }) => void;
  onVisibleRangeChanged?: (event: VisibleRangeChangedEvent) => void;
  onRefresh?: () => void;
  onScrollToIndexFailed?: (info: ScrollToIndexFailedInfo) => void;
}>;

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
  if (row.type === 'system' && row.variant === 'spacer') return row.height;
  if (row.type === 'identity' && row.presentation === 'walletSidebar')
    return 68;
  if (row.type === 'identity' && row.presentation === 'networkSelector')
    return 47;

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
        row.variant === 'noMatch' || row.variant === 'end'
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
  viewportHeight: number
): WebListLayout {
  const rows = effectiveRows(snapshot);
  const horizontal = snapshot.layout.orientation === 'horizontal';
  const spacing = snapshot.layout.itemSpacing ?? 0;
  const padding = paddingValues(snapshot);
  const indexGutter = sectionIndexEnabled(snapshot) ? SECTION_INDEX_GUTTER : 0;
  const width = Math.max(1, viewportWidth || DEFAULT_VIEWPORT_WIDTH);
  const height = Math.max(1, viewportHeight || DEFAULT_VIEWPORT_HEIGHT);
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
      const rowHeight = estimateWebRowHeight(row, snapshot, availableWidth);
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

const WEB_LIST_CSS = `
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
.ok-native-list-item:focus-visible>.ok-native-list-row{outline:2px solid var(--nl-accent);outline-offset:-2px}
.ok-native-list-item[data-separator="true"]>.ok-native-list-row{border-bottom:1px solid var(--nl-separator)}
.ok-native-list-item[data-group-position="first"]>.ok-native-list-row{border-radius:12px 12px 0 0}
.ok-native-list-item[data-group-position="last"]>.ok-native-list-row{border-radius:0 0 12px 12px}
.ok-native-list-item[data-group-position="single"]>.ok-native-list-row{border-radius:12px}
.ok-native-list-standard{padding:8px 12px}.ok-native-list-network-row{padding:0 12px}.ok-native-list-wallet-row{padding:6px 8px;flex-direction:column;justify-content:center;gap:4px}.ok-native-list-account-row{padding:4px 12px}
.ok-native-list-flex{display:flex;flex:1;min-width:0;flex-direction:column;justify-content:center}.ok-native-list-title{font-size:15px;line-height:20px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-secondary{font-size:13px;line-height:18px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-tertiary{color:var(--nl-secondary)}.ok-native-list-info{color:var(--nl-info)}
.ok-native-list-value{font-size:14px;line-height:20px;font-weight:500;white-space:nowrap}.ok-native-list-time{font-size:11px;line-height:16px;color:var(--nl-secondary);align-self:flex-start}.ok-native-list-amounts{display:flex;flex-direction:column;align-items:flex-end;min-width:0}.ok-native-list-actions{display:flex;gap:16px;margin-top:6px}
.ok-native-list-action-button{appearance:none;border:0;background:transparent;padding:2px;color:var(--nl-accent);font:600 12px/16px inherit;cursor:pointer}.ok-native-list-action-button[data-tone="danger"]{color:var(--nl-negative)}.ok-native-list-action-button:disabled{opacity:.5;cursor:default}
.ok-native-list-visual{position:relative;flex:0 0 40px;width:40px;height:40px;border-radius:20px;overflow:visible;background:var(--nl-strong)}.ok-native-list-visual>img,.ok-native-list-visual-main{display:block;width:40px;height:40px;border-radius:inherit;object-fit:cover}.ok-native-list-visual-fallback{display:flex;width:100%;height:100%;align-items:center;justify-content:center;border-radius:inherit;color:var(--nl-primary);font-size:15px;font-weight:600;overflow:hidden}.ok-native-list-visual-corner{position:absolute;right:-4px;bottom:-4px;width:18px;height:18px;padding:2px;border-radius:50%;background:var(--nl-row);object-fit:cover}.ok-native-list-stacked{display:flex;align-items:center;width:48px;flex-basis:48px}.ok-native-list-stacked img{width:28px;height:28px;border:2px solid var(--nl-row);border-radius:50%;object-fit:cover;margin-left:-10px}.ok-native-list-stacked img:first-child{margin-left:0}
.ok-native-list-accessories{display:flex;align-items:center;gap:10px;min-width:0}.ok-native-list-accessory{font-size:14px;color:var(--nl-primary);white-space:nowrap}.ok-native-list-accessory-secondary{color:var(--nl-secondary)}.ok-native-list-icon-button{display:inline-flex;align-items:center;justify-content:center;min-width:24px;height:24px;border:0;background:transparent;color:var(--nl-icon);font:500 20px/1 inherit;cursor:pointer}.ok-native-list-checkbox{display:inline-flex;align-items:center;justify-content:center;box-sizing:border-box;width:20px;height:20px;border:2px solid color-mix(in srgb,var(--nl-primary) 19%,transparent);border-radius:5px;background:var(--nl-row);cursor:pointer}.ok-native-list-checkbox[data-state="checked"],.ok-native-list-checkbox[data-state="indeterminate"]{border-color:transparent;background:var(--nl-primary)}.ok-native-list-checkbox[data-state="checked"]::after{content:"";width:6px;height:10px;margin-top:-2px;border-right:2px solid var(--nl-row);border-bottom:2px solid var(--nl-row);transform:rotate(45deg)}.ok-native-list-checkbox[data-state="indeterminate"]::after{content:"";width:8px;height:2px;border-radius:1px;background:var(--nl-row)}.ok-native-list-spinner{width:16px;height:16px;border:2px solid var(--nl-separator);border-top-color:var(--nl-primary);border-radius:50%;animation:ok-native-list-spin .8s linear infinite}@keyframes ok-native-list-spin{to{transform:rotate(360deg)}}
.ok-native-list-badge{display:inline-flex;align-items:center;max-width:100%;height:18px;padding:0 5px;border-radius:5px;background:color-mix(in srgb,var(--nl-accent) 12%,transparent);color:var(--nl-accent);font-size:11px;line-height:18px;white-space:nowrap}.ok-native-list-badge[data-tone="danger"]{background:var(--nl-critical);color:var(--nl-negative)}.ok-native-list-badges{display:inline-flex;gap:4px;margin-left:6px;vertical-align:middle}
.ok-native-list-section{padding:0 8px;background:var(--nl-bg);gap:8px}.ok-native-list-section[data-variant="summary"]{padding:0 20px}.ok-native-list-section[data-variant="gallery"]{padding:0 12px}.ok-native-list-section[data-variant="history"]{padding:0 8px}.ok-native-list-section-title{font-size:13px;line-height:18px;font-weight:700;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-section[data-variant="summary"] .ok-native-list-section-title{font-size:16px;font-weight:500;color:var(--nl-primary)}.ok-native-list-section[data-variant="gallery"] .ok-native-list-section-title{font-size:16px;color:var(--nl-primary)}.ok-native-list-section[data-variant="history"] .ok-native-list-section-title{font-size:12px;line-height:16px}.ok-native-list-section-value{margin-left:auto}
.ok-native-list-action-row{justify-content:space-between;padding:0 16px}.ok-native-list-action-title{font-size:15px;font-weight:600;color:var(--nl-accent)}.ok-native-list-action-title[data-tone="danger"]{color:var(--nl-negative)}
.ok-native-list-system{justify-content:center;padding:8px 12px;background:var(--nl-bg);color:var(--nl-secondary);cursor:default}.ok-native-list-system[data-variant="retry"],.ok-native-list-system[data-variant="noMatch"],.ok-native-list-system[data-variant="end"]{justify-content:flex-start}.ok-native-list-system[data-variant="retry"]{cursor:pointer}
.ok-native-list-rail{padding:4px;gap:6px;border-radius:8px}.ok-native-list-rail .ok-native-list-visual{width:20px;height:20px;flex-basis:20px}.ok-native-list-rail .ok-native-list-visual>img,.ok-native-list-rail .ok-native-list-visual-main{width:20px;height:20px}.ok-native-list-rail-title{font-size:12px;font-weight:500;white-space:nowrap}
.ok-native-list-media{display:block;padding:0 5px;background:transparent;border-radius:16px}.ok-native-list-media-image{display:block;width:100%;aspect-ratio:1;border-radius:10px;background:var(--nl-strong);object-fit:cover}.ok-native-list-media-image[data-state="empty"]{background:transparent}.ok-native-list-media-image[data-state="error"]{display:flex;align-items:center;justify-content:center;color:var(--nl-icon-subdued);font-size:24px}.ok-native-list-media-meta{padding-top:7px}.ok-native-list-media-subtitle-row{display:flex;align-items:center;gap:6px}.ok-native-list-media-subtitle{flex:1;min-width:0;font-size:12px;color:var(--nl-secondary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-media-network{width:14px;height:14px;border-radius:50%}.ok-native-list-media-title{font-size:16px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.ok-native-list-media-close{position:absolute;right:9px;top:4px;border:0;background:color-mix(in srgb,var(--nl-inverse) 72%,transparent);color:var(--nl-inverse-text);width:24px;height:24px;border-radius:50%;font:18px/20px inherit;cursor:pointer}
.ok-native-list-metric{display:flex;flex-direction:column;align-items:flex-start;padding:12px;border-radius:12px;gap:5px;background:var(--nl-row)}.ok-native-list-metric-value{font-size:22px;line-height:28px;font-weight:700}.ok-native-list-composite{display:flex;flex-direction:column;align-items:stretch;padding:14px;border-radius:12px;gap:12px;background:var(--nl-subdued)}.ok-native-list-composite-heading{font-size:14px;letter-spacing:1px;color:var(--nl-secondary)}.ok-native-list-composite-row{display:flex;gap:12px}.ok-native-list-composite-cell{flex:1;min-width:0}.ok-native-list-composite-cell[data-shaded="true"]{padding:10px;border-radius:10px;background:color-mix(in srgb,var(--nl-primary) 5%,transparent)}.ok-native-list-composite-value{font-size:18px;font-weight:600}.ok-native-list-divider{height:1px;background:var(--nl-separator)}.ok-native-list-progress{height:4px;border-radius:2px;overflow:hidden;background:var(--nl-negative)}.ok-native-list-progress>span{display:block;height:100%;border-radius:2px;background:var(--nl-positive)}
.ok-native-list-data{padding:6px 12px}.ok-native-list-index{flex:0 0 28px;color:var(--nl-secondary);font-size:13px}.ok-native-list-favorite{flex:0 0 24px;color:var(--nl-icon-subdued);font-size:22px}.ok-native-list-favorite[data-active="true"]{color:var(--nl-accent)}.ok-native-list-data-cell{display:flex;flex-direction:column;min-width:0}.ok-native-list-data-cell[data-align="center"]{align-items:center}.ok-native-list-data-cell[data-align="end"]{align-items:flex-end}.ok-native-list-data-primary{display:flex;align-items:center;gap:5px;max-width:100%;font-size:16px;font-weight:500;white-space:nowrap}.ok-native-list-unread{width:7px;height:7px;flex:0 0 7px;border-radius:50%;background:var(--nl-accent)}.ok-native-list-thumbnail{width:64px;height:64px;border-radius:10px;object-fit:cover}
.ok-native-list-footer{flex:0 0 auto;min-height:0}.ok-native-list-sticky{position:absolute;z-index:4;left:0;right:0;top:0;pointer-events:auto;box-shadow:0 1px 0 var(--nl-separator)}.ok-native-list-index-rail{position:absolute;z-index:6;top:0;right:0;bottom:0;width:44px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:0;touch-action:none}.ok-native-list-index-button{appearance:none;border:0;background:transparent;display:flex;flex:1;max-height:22px;min-height:12px;width:100%;align-items:center;justify-content:center;padding:0;color:var(--nl-secondary);font:600 11px/1 inherit;cursor:pointer}.ok-native-list-index-button[data-active="true"]{color:var(--nl-accent)}.ok-native-list-index-preview{position:absolute;z-index:8;left:50%;top:50%;display:flex;width:72px;height:72px;align-items:center;justify-content:center;transform:translate(-50%,-50%) scale(.92);border-radius:16px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:28px;font-weight:600;opacity:0;pointer-events:none;transition:opacity .15s ease,transform .15s ease}.ok-native-list-index-preview[data-visible="true"]{opacity:1;transform:translate(-50%,-50%) scale(1)}
.ok-native-list-refresh{position:absolute;z-index:7;left:50%;top:8px;display:flex;align-items:center;gap:6px;padding:6px 10px;border-radius:999px;background:var(--nl-inverse);color:var(--nl-inverse-text);font-size:12px;opacity:0;transform:translate(-50%,-16px);transition:opacity .15s ease,transform .15s ease;pointer-events:none}.ok-native-list-refresh[data-visible="true"]{opacity:1;transform:translate(-50%,0)}
@media (prefers-reduced-motion:reduce){.ok-native-list-index-preview,.ok-native-list-refresh{transition:none}.ok-native-list-spinner{animation:none}}
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

function createImage(
  context: RenderContext,
  source: ImageSource,
  className?: string
): HTMLImageElement | undefined {
  const uri = safeImageUri(source.uri);
  if (!uri) return undefined;
  const image = context.document.createElement('img');
  if (className) image.className = className;
  image.src = uri;
  image.alt = '';
  image.draggable = false;
  image.loading = 'lazy';
  image.decoding = 'async';
  image.style.objectFit =
    source.contentFit === 'fill' ? 'fill' : source.contentFit ?? 'cover';
  return image;
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
  if (row.type === 'metricCard') return row.visual;
  return undefined;
}

function createVisual(
  context: RenderContext,
  visual: LeadingVisual | undefined
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
    frame.appendChild(corner);
  }
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
  if (actionKey) setData(element, 'nativeListAction', actionKey);
  if (tintColor) element.style.color = tintColor;
  return element;
}

function createAccessory(
  context: RenderContext,
  rowKey: string,
  accessory: TrailingAccessory
): HTMLElement {
  if (accessory.kind === 'checkbox')
    return createCheckbox(context, rowKey, accessory);
  if (accessory.kind === 'icon') {
    return createIconAction(
      context,
      accessory.name,
      accessory.actionKey,
      accessory.disabled,
      accessory.tintColor
    );
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
      element.textContent = '•••';
      break;
    case 'drag':
      element.textContent = '≡';
      break;
    case 'progress':
      element.textContent = String(Math.round(accessory.value * 100)) + '%';
      break;
  }
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
  accessories.forEach((accessory) =>
    container.appendChild(createAccessory(context, rowKey, accessory))
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
  if (row.titleIcon)
    body.appendChild(
      createIconAction(
        context,
        row.titleIcon.name,
        row.titleIcon.actionKey,
        row.titleIcon.disabled,
        row.titleIcon.tintColor
      )
    );
  body.appendChild(createTextColumn(context, row.title, row.subtitle));
  if (row.value) {
    const value = createElement(
      context.document,
      row.valueActionKey ? 'button' : 'span',
      row.valueActionKey
        ? 'ok-native-list-action-button ok-native-list-section-value'
        : 'ok-native-list-value ok-native-list-section-value',
      row.value
    );
    if (row.valueActionKey) {
      value.setAttribute('type', 'button');
      setData(value, 'nativeListAction', row.valueActionKey);
    }
    body.appendChild(value);
  }
  if (row.valueIcon)
    body.appendChild(
      createIconAction(
        context,
        row.valueIcon.name,
        row.valueIcon.actionKey,
        row.valueIcon.disabled,
        row.valueIcon.tintColor
      )
    );
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
    'ok-native-list-row ok-native-list-action-row'
  );
  if (row.icon) body.appendChild(createVisual(context, row.icon)!);
  const title = createElement(
    context.document,
    'span',
    'ok-native-list-action-title',
    row.title
  );
  setData(title, 'tone', row.tone);
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
      presentation === 'networkSelector' ? 'ok-native-list-network-row' : '',
      presentation === 'walletSidebar' ? 'ok-native-list-wallet-row' : '',
      presentation === 'accountSelector' ? 'ok-native-list-account-row' : '',
    ]
      .filter(Boolean)
      .join(' ')
  );
  if (row.type === 'identity' && row.leadingAction) {
    body.appendChild(
      createIconAction(
        context,
        row.leadingAction.name,
        row.leadingAction.actionKey,
        row.leadingAction.disabled,
        row.leadingAction.tintColor
      )
    );
  }
  const visual = createVisual(context, visualFromRow(row));
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
    row.type === 'identity' ? row.badges : undefined
  );
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
    row.footerActions.forEach((action) => {
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

function createRowBody(context: RenderContext, row: RowModel): HTMLElement {
  switch (row.type) {
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
  private readonly indexPreview: HTMLElement;
  private readonly refreshIndicator: HTMLElement;
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
  private reachedGeneration: number | undefined;
  private stickyKey: string | undefined;
  private previewTimer: number | undefined;
  private dragFromIndex: number | undefined;
  private pullStartY: number | undefined;
  private pullDistance = 0;
  private destroyed = false;

  constructor(
    host: HTMLElement,
    snapshot: NativeListSnapshot,
    callbacks: NativeListWebCallbacks
  ) {
    this.document = host.ownerDocument;
    this.snapshot = validateSnapshot(snapshot);
    this.callbacks = callbacks;
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
    this.indexPreview = createElement(
      this.document,
      'div',
      'ok-native-list-index-preview'
    );
    this.indexPreview.setAttribute('aria-live', 'polite');
    this.refreshIndicator = createElement(
      this.document,
      'div',
      'ok-native-list-refresh',
      'Refreshing'
    );
    this.refreshIndicator.setAttribute('role', 'status');
    this.viewport.append(this.content);
    this.viewportFrame.append(
      this.viewport,
      this.sticky,
      this.indexRail,
      this.indexPreview,
      this.refreshIndicator
    );
    this.root.append(style, this.viewportFrame, this.footer);
    host.appendChild(this.root);

    this.viewport.addEventListener('scroll', this.handleScroll, {
      passive: true,
    });
    this.root.addEventListener('click', this.handleClick);
    this.root.addEventListener('keydown', this.handleKeyDown);
    this.content.addEventListener('dragstart', this.handleDragStart);
    this.content.addEventListener('dragover', this.handleDragOver);
    this.content.addEventListener('drop', this.handleDrop);
    this.content.addEventListener('dragend', this.handleDragEnd);
    this.indexRail.addEventListener('pointerdown', this.handleIndexPointer);
    this.indexRail.addEventListener('pointermove', this.handleIndexPointer);
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

  applySnapshot(snapshot: NativeListSnapshot) {
    this.setSnapshot(validateSnapshot(snapshot));
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
        (row) => row.type === 'sectionHeader' && row.variant !== 'summary'
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

  destroy() {
    if (this.destroyed) return;
    this.destroyed = true;
    if (this.frameHandle !== undefined) this.cancelFrame(this.frameHandle);
    if (this.previewTimer !== undefined)
      this.document.defaultView?.clearTimeout(this.previewTimer);
    this.resizeObserver?.disconnect();
    this.document.defaultView?.removeEventListener(
      'resize',
      this.handleWindowResize
    );
    this.viewport.removeEventListener('scroll', this.handleScroll);
    this.root.removeEventListener('click', this.handleClick);
    this.root.removeEventListener('keydown', this.handleKeyDown);
    this.content.removeEventListener('dragstart', this.handleDragStart);
    this.content.removeEventListener('dragover', this.handleDragOver);
    this.content.removeEventListener('drop', this.handleDrop);
    this.content.removeEventListener('dragend', this.handleDragEnd);
    this.indexRail.removeEventListener('pointerdown', this.handleIndexPointer);
    this.indexRail.removeEventListener('pointermove', this.handleIndexPointer);
    this.indexRail.removeEventListener('click', this.handleIndexClick);
    this.viewport.removeEventListener('pointerdown', this.handlePullStart);
    this.viewport.removeEventListener('pointermove', this.handlePullMove);
    this.viewport.removeEventListener('pointerup', this.handlePullEnd);
    this.viewport.removeEventListener('pointercancel', this.handlePullEnd);
    const host = this.root.parentElement;
    this.root.remove();
    if (host) host.style.position = this.previousHostPosition;
    this.mounted.clear();
    this.pool.length = 0;
  }

  private setSnapshot(
    snapshot: NativeListSnapshot,
    selectedKeys?: ReadonlySet<string>
  ) {
    this.snapshot = snapshot;
    this.rows = effectiveRows(snapshot);
    this.selectedKeys =
      selectedKeys ?? selectionStateFromSnapshot(snapshot).selectedKeys;
    if (this.reachedGeneration !== snapshot.generation)
      this.reachedGeneration = undefined;
    this.renderSectionIndex();
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
      if (value) this.root.style.setProperty(name, value);
    });
  }

  private recomputeLayout = () => {
    if (this.destroyed) return;
    const previousHorizontal = this.layout.horizontal;
    this.layout = computeWebListLayout(
      this.snapshot,
      this.viewport.clientWidth,
      this.viewport.clientHeight
    );
    this.content.style.width = String(this.layout.contentWidth) + 'px';
    this.content.style.height = String(this.layout.contentHeight) + 'px';
    if (previousHorizontal !== this.layout.horizontal) {
      this.viewport.scrollLeft = 0;
      this.viewport.scrollTop = 0;
    }
    this.renderWindow();
    this.performPendingScroll();
  };

  private renderWindow() {
    const viewportLength = this.viewportLength();
    const visible = visibleWebLayoutItems(
      this.layout,
      this.currentOffset(),
      viewportLength,
      viewportLength * OVERSCAN_VIEWPORTS
    );
    const desired = new Set(visible.map((item) => item.index));
    this.mounted.forEach((element, index) => {
      if (!desired.has(index)) {
        this.mounted.delete(index);
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
    element.className = overlay
      ? 'ok-native-list-item ok-native-list-sticky'
      : 'ok-native-list-item';
    setData(element, 'nativeListRowKey', row.key);
    setData(element, 'nativeListRowIndex', index);
    setData(element, 'nativeListDisabled', Boolean(row.disabled));
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
    element.draggable = this.isReorderable(row);
    const context = {
      document: this.document,
      snapshot: this.snapshot,
      selectedKeys: this.selectedKeys,
      itemIndex: index,
    };
    element.replaceChildren(createRowBody(context, row));
  }

  private renderFooter() {
    const row = this.snapshot.fixedFooter;
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

  private renderSectionIndex() {
    this.indexRail.replaceChildren();
    if (!sectionIndexEnabled(this.snapshot)) {
      this.indexRail.hidden = true;
      return;
    }
    const fragment = this.document.createDocumentFragment();
    this.snapshot.rows.forEach((row, index) => {
      if (row.type !== 'sectionHeader' || !row.indexTitle) return;
      const button = createElement(
        this.document,
        'button',
        'ok-native-list-index-button',
        row.indexTitle
      );
      button.setAttribute('type', 'button');
      button.setAttribute('aria-label', 'Jump to ' + row.indexTitle);
      setData(button, 'sectionPosition', index);
      setData(button, 'sectionKey', row.key);
      fragment.appendChild(button);
    });
    this.indexRail.appendChild(fragment);
    this.indexRail.hidden = this.indexRail.childElementCount === 0;
  }

  private updateVisibleSelection() {
    const update = (element: HTMLElement, row: RowModel | undefined) => {
      if (!row) return;
      const selected = this.selectedKeys.has(row.key);
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
    this.updateSectionIndex(first?.index ?? -1);
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
      if (row?.type === 'sectionHeader' && row.variant !== 'summary') {
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
        candidate.variant !== 'summary'
      ) {
        nextIndex = cursor;
        break;
      }
    }
    const next = this.layout.items[nextIndex];
    const translate = next
      ? Math.min(0, next.y - this.viewport.scrollTop - item.height)
      : 0;
    this.sticky.style.transform =
      'translate3d(0,' + String(translate) + 'px,0)';
    this.updateVisibleSelection();
  }

  private updateSectionIndex(firstVisibleIndex: number) {
    let activeKey: string | undefined;
    this.snapshot.rows.forEach((row, index) => {
      if (
        index <= firstVisibleIndex &&
        row.type === 'sectionHeader' &&
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
    return this.layout.horizontal
      ? this.viewport.clientWidth || DEFAULT_VIEWPORT_WIDTH
      : this.viewport.clientHeight || DEFAULT_VIEWPORT_HEIGHT;
  }

  private contentLength(): number {
    return this.layout.horizontal
      ? this.layout.contentWidth
      : this.layout.contentHeight;
  }

  private currentOffset(): number {
    return this.layout.horizontal
      ? this.viewport.scrollLeft
      : this.viewport.scrollTop;
  }

  private scrollToAbsoluteOffset(offset: number, animated: boolean) {
    const resolved = Math.min(
      Math.max(0, offset),
      Math.max(0, this.contentLength() - this.viewportLength())
    );
    this.viewport.scrollTo(
      this.layout.horizontal
        ? { left: resolved, top: 0, behavior: animated ? 'smooth' : 'auto' }
        : { left: 0, top: resolved, behavior: animated ? 'smooth' : 'auto' }
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
    if ('title' in row) return row.title;
    if ('message' in row && row.message) return row.message;
    return row.type;
  }

  private isReorderable(row: RowModel): boolean {
    if (!this.snapshot.capabilities?.reorderable || row.disabled) return false;
    if (row.type === 'rail' && row.draggable) return true;
    return (
      (row.type === 'identity' || row.type === 'action') &&
      Boolean(row.trailing?.some((accessory) => accessory.kind === 'drag'))
    );
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

  private emitRowAction(row: RowModel, actionKey: string) {
    this.callbacks.onRowAction?.({
      rowKey: row.key,
      actionKey,
      sectionKey: row.sectionKey,
    });
  }

  private handleRowPress(row: RowModel) {
    if (row.disabled) return;
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
        : 'press';
    this.emitRowAction(row, actionKey);
  }

  private handleClick = (event: Event) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-key]'
    );
    const row = this.rowAtElement(rowElement);
    if (!row || row.disabled) return;
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
        this.emitRowAction(row, action.dataset.nativeListAction);
      return;
    }
    this.handleRowPress(row);
  };

  private handleKeyDown = (event: KeyboardEvent) => {
    if (event.key !== 'Enter' && event.key !== ' ') return;
    const target = event.target;
    if (!(target instanceof HTMLElement)) return;
    if (target.matches('button')) return;
    const row = this.rowAtElement(target.closest('[data-native-list-row-key]'));
    if (!row) return;
    event.preventDefault();
    this.handleRowPress(row);
  };

  private handleScroll = () => {
    this.scheduleFrame();
  };

  private handleWindowResize = () => {
    this.recomputeLayout();
  };

  private scheduleFrame() {
    if (this.frameHandle !== undefined || this.destroyed) return;
    this.frameHandle = this.requestFrame(() => {
      this.frameHandle = undefined;
      this.renderWindow();
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

  private selectIndexPosition(position: number, title: string) {
    this.scrollToIndex(position, {
      animated: false,
      alignment: 'start',
      viewPosition: 0,
      viewOffset: 0,
    });
    this.indexPreview.textContent = title;
    setData(this.indexPreview, 'visible', true);
    if (this.previewTimer !== undefined)
      this.document.defaultView?.clearTimeout(this.previewTimer);
    this.previewTimer = this.document.defaultView?.setTimeout(() => {
      setData(this.indexPreview, 'visible', false);
    }, 180);
  }

  private indexButtonAtEvent(event: PointerEvent): HTMLElement | undefined {
    const direct = (event.target as Element | null)?.closest<HTMLElement>(
      '[data-section-position]'
    );
    if (direct) return direct;
    return (
      this.document
        .elementFromPoint(event.clientX, event.clientY)
        ?.closest<HTMLElement>('[data-section-position]') ?? undefined
    );
  }

  private handleIndexPointer = (event: PointerEvent) => {
    if (event.type === 'pointermove' && event.buttons === 0) return;
    const button = this.indexButtonAtEvent(event);
    if (!button) return;
    event.preventDefault();
    this.selectIndexPosition(
      Number(button.dataset.sectionPosition),
      button.textContent ?? ''
    );
  };

  private handleIndexClick = (event: Event) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const button = target.closest<HTMLElement>('[data-section-position]');
    if (!button) return;
    this.selectIndexPosition(
      Number(button.dataset.sectionPosition),
      button.textContent ?? ''
    );
  };

  private handlePullStart = (event: PointerEvent) => {
    if (
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

  private handleDragStart = (event: DragEvent) => {
    const target = event.target;
    if (!(target instanceof Element)) return;
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-index]'
    );
    const index = Number(rowElement?.dataset.nativeListRowIndex);
    const row = this.rows[index];
    if (!row || !this.isReorderable(row)) {
      event.preventDefault();
      return;
    }
    this.dragFromIndex = index;
    event.dataTransfer?.setData('text/plain', row.key);
    if (event.dataTransfer) event.dataTransfer.effectAllowed = 'move';
  };

  private handleDragOver = (event: DragEvent) => {
    if (this.dragFromIndex === undefined) return;
    const target = event.target;
    if (!(target instanceof Element)) return;
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-index]'
    );
    const toIndex = Number(rowElement?.dataset.nativeListRowIndex);
    const from = this.rows[this.dragFromIndex];
    const to = this.rows[toIndex];
    if (
      !from ||
      !to ||
      !this.isReorderable(to) ||
      from.sectionKey !== to.sectionKey
    )
      return;
    event.preventDefault();
    if (event.dataTransfer) event.dataTransfer.dropEffect = 'move';
  };

  private handleDrop = (event: DragEvent) => {
    const fromIndex = this.dragFromIndex;
    this.dragFromIndex = undefined;
    if (fromIndex === undefined) return;
    const target = event.target;
    if (!(target instanceof Element)) return;
    const rowElement = target.closest<HTMLElement>(
      '[data-native-list-row-index]'
    );
    const toIndex = Number(rowElement?.dataset.nativeListRowIndex);
    const from = this.rows[fromIndex];
    const to = this.rows[toIndex];
    if (
      !from ||
      !to ||
      !this.isReorderable(from) ||
      !this.isReorderable(to) ||
      from.sectionKey !== to.sectionKey
    )
      return;
    event.preventDefault();
    if (fromIndex === toIndex) return;
    const rows = [...this.snapshot.rows];
    const moved = rows.splice(fromIndex, 1)[0];
    if (!moved) return;
    rows.splice(toIndex, 0, moved);
    this.setSnapshot({ ...this.snapshot, rows }, this.selectedKeys);
    this.callbacks.onReorder?.({
      key: moved.key,
      fromIndex,
      toIndex,
      beforeKey: rows[toIndex - 1]?.key,
      afterKey: rows[toIndex + 1]?.key,
    });
  };

  private handleDragEnd = () => {
    this.dragFromIndex = undefined;
  };
}
