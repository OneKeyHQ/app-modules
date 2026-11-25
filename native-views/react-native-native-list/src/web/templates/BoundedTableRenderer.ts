import type {
  DataRow,
  MarketRow,
  NativeListTableColumn,
  NativeListTextStyle,
} from '../../models';
import { markActionAnchorSource } from './RowElements';
import { applyValueSegments, fontWeight } from './RowText';
import { type RowPrimitives, RowVisualStyle } from './RowVisual';

type Row = DataRow | MarketRow;
type Cell = {
  element: HTMLElement;
  labels: HTMLElement;
  visual?: HTMLElement;
  visualKey?: string;
};
const states = new WeakMap<
  HTMLElement,
  { cells: Cell[]; primitives: RowPrimitives }
>();

function label(
  document: Document,
  column: NativeListTableColumn,
  secondary: boolean,
  style: NativeListTextStyle | undefined,
  primitives: RowPrimitives
) {
  const element = document.createElement('span');
  element.textContent = secondary ? column.secondaryText ?? '' : column.text;
  const tone =
    (secondary ? column.secondaryTone : column.tone) ??
    (secondary ? 'secondary' : 'primary');
  const size = style?.fontSize ?? (secondary ? 12 : 14);
  const height = style?.lineHeight ?? (secondary ? 16 : 20);
  const weight = fontWeight(style?.fontWeight, secondary ? 400 : 500);
  Object.assign(element.style, {
    display: 'block',
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    fontSize: size + 'px',
    lineHeight: height + 'px',
    fontWeight: String(weight),
    color: 'var(--nl-' + tone + ')',
  });
  applyValueSegments(
    element,
    secondary ? column.secondaryTextSegments : column.textSegments,
    size,
    height,
    weight
  );
  if (style) primitives.textStyle(element, style);
  return element;
}

function bind(body: HTMLElement, row: Row, primitives: RowPrimitives) {
  const state = states.get(body)!;
  state.primitives = primitives;
  body.style.columnGap = (row.style?.columnGap ?? 8) + 'px';
  body.style.paddingInline = (row.style?.horizontalPadding ?? 0) + 'px';
  body.style.paddingBlock = (row.style?.verticalPadding ?? 0) + 'px';
  const columns = row.tableColumns!;
  while (state.cells.length > columns.length) {
    const old = state.cells.pop()!;
    if (old.visual) primitives.dispose(old.visual);
    old.element.remove();
  }
  columns.forEach((column, index) => {
    const tag = column.actionKey ? 'button' : 'div';
    let cell = state.cells[index];
    if (!cell || cell.element.localName !== tag) {
      const element = body.ownerDocument.createElement(tag);
      if (tag === 'button') element.setAttribute('type', 'button');
      if (cell?.visual) primitives.dispose(cell.visual);
      if (cell) cell.element.replaceWith(element);
      else body.appendChild(element);
      const labels = body.ownerDocument.createElement('span');
      labels.style.cssText =
        'display:flex;flex-direction:column;min-width:0;overflow:hidden';
      element.appendChild(labels);
      cell = { element, labels };
      state.cells[index] = cell;
    }
    const { element, labels } = cell;
    element.style.cssText =
      'display:flex;align-self:stretch;align-items:center;min-width:0;overflow:hidden;box-sizing:border-box;background:transparent;border:0;padding:0;font:inherit';
    element.style.flex =
      column.width === undefined ? '1 1 0px' : '0 0 ' + column.width + 'px';
    element.style.justifyContent =
      column.alignment === 'end'
        ? 'flex-end'
        : column.alignment === 'center'
        ? 'center'
        : 'flex-start';
    element.style.textAlign = column.alignment ?? 'start';
    if (column.actionKey) {
      element.dataset.nativeListAction = column.actionKey;
      element.setAttribute('aria-label', column.accessibilityLabel);
      element.toggleAttribute('disabled', !!row.disabled);
      markActionAnchorSource(element, 'leadingAction', index);
    }
    const visualKey = JSON.stringify([
      row.key,
      column.key,
      column.leading,
      row.style?.image,
      row.style?.leadingGap,
    ]);
    if (visualKey !== cell.visualKey) {
      if (cell.visual) {
        primitives.dispose(cell.visual);
        cell.visual.remove();
      }
      cell.visual = primitives.visual(column.leading);
      if (cell.visual) {
        element.prepend(cell.visual);
        const { width = 24, height = 24 } = row.style?.image ?? {};
        new RowVisualStyle(cell.visual).apply(
          { ...row.style?.image, width, height },
          width,
          height,
          0
        );
        cell.visual.style.marginInlineEnd = (row.style?.leadingGap ?? 4) + 'px';
      }
      cell.visualKey = visualKey;
    }
    labels.style.gap = (row.style?.lineGap ?? 0) + 'px';
    labels.replaceChildren(
      label(
        body.ownerDocument,
        column,
        false,
        row.type === 'market' ? row.style?.price : row.style?.columns,
        primitives
      )
    );
    if (column.secondaryText || column.secondaryTextSegments?.length)
      labels.appendChild(
        label(
          body.ownerDocument,
          column,
          true,
          row.type === 'market'
            ? row.style?.subtitle
            : row.style?.columnSecondary,
          primitives
        )
      );
  });
}

export const boundedTableRenderer = {
  key: 'boundedTable',
  create(document: Document, row: Row, primitives: RowPrimitives) {
    const body = document.createElement('div');
    body.className = 'ok-native-list-row';
    body.dataset.nlRenderer = 'boundedTable';
    body.style.cssText =
      'display:flex;align-items:center;gap:0;overflow:hidden';
    states.set(body, { cells: [], primitives });
    bind(body, row, primitives);
    return body;
  },
  bind,
  measure: () => 56,
  measureRendered: () => undefined,
  recycle(body: HTMLElement) {
    const state = states.get(body);
    state?.cells.forEach((cell) => {
      if (cell.visual) state.primitives.dispose(cell.visual);
      cell.element.remove();
    });
    if (state) state.cells.length = 0;
  },
};
