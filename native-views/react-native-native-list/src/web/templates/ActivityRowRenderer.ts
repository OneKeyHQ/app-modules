import type { ActivityRow } from '../../models';
import { createElement, tagSlot, markActionAnchorSource } from './RowElements';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
type Views = {
  column: HTMLElement;
  fields: Record<string, HTMLElement>;
  amounts: HTMLElement;
  actions: HTMLElement;
  visuals: HTMLElement[];
  visualStyles: RowVisualStyle[];
  visualKey: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
function bind(body: HTMLElement, row: ActivityRow, primitives: RowPrimitives) {
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
  v.column.style.rowGap =
    style?.lineGap !== undefined ? `${style.lineGap}px` : '';
  v.amounts.style.gap =
    style?.trailingGap !== undefined ? `${style.trailingGap}px` : '';
  for (const key of [
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
  ] as const) {
    const node = v.fields[key]!;
    node.style.cssText = '';
    node.textContent = row[key] ?? '';
    node.hidden = !row[key];
    if (style?.[key]) primitives.textStyle(node, style[key]!);
  }
  const visualKey = JSON.stringify([
    row.key,
    row.leading,
    row.secondaryLeading,
  ]);
  if (v.visualKey !== visualKey) {
    v.visuals.forEach((node) => {
      primitives.dispose(node);
      node.remove();
    });
    v.visuals = [row.leading, row.secondaryLeading]
      .map((source) => primitives.visual(source))
      .filter((node): node is HTMLElement => !!node);
    v.visuals.forEach((node) => body.insertBefore(node, v.column));
    v.visualStyles = v.visuals.map((node) => new RowVisualStyle(node));
    v.visualKey = visualKey;
  }
  if (v.visualStyles[0])
    v.visualStyles[0].apply(
      style?.image,
      style?.image?.width ?? 40,
      style?.image?.height ?? 40,
      (style?.leadingGap ?? 12) - 12
    );
  v.actions.replaceChildren();
  v.actions.hidden = !row.footerActions?.length;
  row.footerActions?.forEach((action, index) => {
    const button = createElement(
      body.ownerDocument,
      'button',
      'ok-native-list-action-button',
      action.label
    );
    button.setAttribute('type', 'button');
    button.toggleAttribute('disabled', Boolean(action.disabled));
    button.dataset.tone = action.tone ?? '';
    button.dataset.nativeListAction = action.key;
    markActionAnchorSource(button, 'footerAction', index);
    v.actions.appendChild(button);
  });
}
function create(
  document: Document,
  row: ActivityRow,
  primitives: RowPrimitives
) {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-standard'
  );
  body.dataset.nlRenderer = 'activity';
  const column = createElement(document, 'span', 'ok-native-list-flex');
  const amounts = createElement(document, 'span', 'ok-native-list-amounts');
  const actions = createElement(document, 'span', 'ok-native-list-actions');
  const fields: Record<string, HTMLElement> = {};
  for (const key of [
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
  ]) {
    const node = tagSlot(
      createElement(
        document,
        'span',
        key === 'title'
          ? 'ok-native-list-title'
          : key === 'primaryAmount'
          ? 'ok-native-list-value'
          : 'ok-native-list-secondary'
      ),
      key
    );
    fields[key] = node;
    (key.endsWith('Amount') ? amounts : column).appendChild(node);
  }
  column.appendChild(actions);
  body.append(column, amounts);
  views.set(body, {
    column,
    amounts,
    actions,
    fields,
    visuals: [],
    visualStyles: [],
    visualKey: '',
    primitives,
  });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  v.visuals.forEach((node) => {
    v.primitives.dispose(node);
    node.remove();
  });
  v.visuals = [];
  v.visualStyles = [];
  v.visualKey = '';
  v.actions.replaceChildren();
}
export const activityRowRenderer = {
  key: 'activity' as const,
  create,
  bind,
  recycle,
  measure: (row: ActivityRow, _width: number) =>
    row.footerActions?.length ? 100 : 60,
  measureRendered: (
    _body: HTMLElement,
    _row: ActivityRow
  ): number | undefined => undefined,
};
