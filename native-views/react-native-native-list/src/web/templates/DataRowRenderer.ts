import type { DataRow, TextTone } from '../../models';
import { createElement, tagSlot } from './RowElements';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
type ColumnViews = {
  body: HTMLElement;
  primaryLine: HTMLElement;
  primary: HTMLElement;
  badges: HTMLElement;
  secondaryLine: HTMLElement;
  secondary: HTMLElement[];
};
type Views = {
  favorite: HTMLElement;
  index: HTMLElement;
  controls: HTMLElement;
  columns: ColumnViews[];
  visual?: HTMLElement;
  visualStyle?: RowVisualStyle;
  visualKey: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
const tone = (value: TextTone | undefined, fallback: string) =>
  value === 'positive'
    ? 'var(--nl-positive)'
    : value === 'negative'
    ? 'var(--nl-negative)'
    : value === 'secondary'
    ? 'var(--nl-secondary)'
    : `var(--nl-${fallback})`;
function bind(body: HTMLElement, row: DataRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  v.controls.replaceChildren();
  if (row.checkbox)
    v.controls.appendChild(primitives.accessory(row.key, row.checkbox, 0));
  v.controls.style.display = 'contents';
  v.index.style.cssText = '';
  v.index.hidden = row.index === undefined;
  v.index.textContent = String(row.index ?? '');
  if (style?.index) primitives.textStyle(v.index, style.index);
  v.favorite.hidden = !row.favorite;
  v.favorite.textContent = row.favoriteActive ? '★' : '☆';
  v.favorite.dataset.active = String(!!row.favoriteActive);
  const visualKey = JSON.stringify([row.key, row.leading]);
  if (visualKey !== v.visualKey) {
    if (v.visual) {
      primitives.dispose(v.visual);
      v.visual.remove();
    }
    v.visual = primitives.visual(row.leading);
    v.visualStyle = v.visual ? new RowVisualStyle(v.visual) : undefined;
    if (v.visual) body.insertBefore(v.visual, v.columns[0]!.body);
    v.visualKey = visualKey;
  }
  v.visualStyle?.apply(
    style?.image,
    style?.image?.width ?? 40,
    style?.image?.height ?? 40,
    (style?.leadingGap ?? 12) - 12
  );
  v.columns.forEach((cell, index) => {
    const column = row.columns[index];
    cell.body.hidden = !column;
    if (!column) {
      cell.body.remove();
      return;
    }
    body.appendChild(cell.body);
    cell.body.style.cssText = '';
    cell.body.hidden = false;
    cell.body.style.flex = String(column.weight ?? 1);
    cell.body.style.display = 'flex';
    cell.body.dataset.align = column.alignment ?? 'start';
    if (style?.lineGap !== undefined)
      cell.body.style.rowGap = `${style.lineGap}px`;
    cell.primaryLine.style.cssText = '';
    cell.primaryLine.style.color = tone(column.tone, 'primary');
    if (style?.titleBadgeGap !== undefined)
      cell.primaryLine.style.gap = `${style.titleBadgeGap}px`;
    cell.primary.style.cssText = '';
    cell.primary.textContent = column.text;
    if (style?.columns) primitives.textStyle(cell.primary, style.columns);
    cell.badges.replaceChildren();
    cell.badges.style.display = 'contents';
    if (column.key === 'asset')
      row.badges?.forEach((badge) => {
        const label = tagSlot(
          createElement(
            body.ownerDocument,
            'span',
            'ok-native-list-badge',
            badge.text
          ),
          'badge'
        );
        label.dataset.tone = badge.tone ?? '';
        cell.badges.appendChild(label);
      });
    cell.secondaryLine.style.display =
      !column.secondaryLeadingText && !column.secondaryText ? 'none' : 'flex';
    [column.secondaryLeadingText, column.secondaryText].forEach(
      (text, slot) => {
        const label = cell.secondary[slot]!;
        label.hidden = !text;
        if (text) cell.secondaryLine.appendChild(label);
        else label.remove();
        label.style.cssText = '';
        label.textContent = text ?? '';
        label.style.color = tone(column.secondaryTone, 'secondary');
        if (style?.columnSecondary)
          primitives.textStyle(label, style.columnSecondary);
      }
    );
  });
}
function create(document: Document, row: DataRow, primitives: RowPrimitives) {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-data'
  );
  body.dataset.nlRenderer = 'dataRow';
  const index = tagSlot(
    createElement(document, 'span', 'ok-native-list-index'),
    'index'
  );
  const favorite = createElement(document, 'span', 'ok-native-list-favorite');
  const controls = createElement(document, 'span');
  body.append(controls, index, favorite);
  const columns = Array.from({ length: 4 }, () => {
    const cell = createElement(document, 'span', 'ok-native-list-data-cell');
    const primaryLine = createElement(
      document,
      'span',
      'ok-native-list-data-primary'
    );
    const primary = tagSlot(createElement(document, 'span'), 'columns');
    const badges = createElement(document, 'span');
    primaryLine.append(primary, badges);
    const secondaryLine = createElement(
      document,
      'span',
      'ok-native-list-data-secondary-line'
    );
    secondaryLine.style.display = 'flex';
    secondaryLine.style.gap = '4px';
    const secondary = Array.from({ length: 2 }, () =>
      tagSlot(
        createElement(document, 'span', 'ok-native-list-secondary'),
        'columnSecondary'
      )
    );
    secondaryLine.append(...secondary);
    cell.append(primaryLine, secondaryLine);
    body.appendChild(cell);
    return {
      body: cell,
      primaryLine,
      primary,
      badges,
      secondaryLine,
      secondary,
    };
  });
  views.set(body, {
    favorite,
    index,
    controls,
    columns,
    visualKey: '',
    primitives,
  });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  if (v.visual) {
    v.primitives.dispose(v.visual);
    v.visual.remove();
  }
  v.visual = undefined;
  v.visualStyle = undefined;
  v.visualKey = '';
  v.controls.replaceChildren();
}
export const dataRowRenderer = {
  key: 'dataRow' as const,
  create,
  bind,
  recycle,
  measure: (row: DataRow, _width: number) =>
    row.columns.some((column) => column.secondaryText) ? 60 : 56,
  measureRendered: (_body: HTMLElement, _row: DataRow): number | undefined =>
    undefined,
};
