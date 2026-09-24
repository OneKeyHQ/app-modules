import { applyTabularNumbers, hasExplicitRowHeight } from './RowElements';
import type { ActionRow } from '../../models';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';

type Views = {
  title: HTMLElement;
  controls: HTMLElement;
  visual?: HTMLElement;
  visualStyle?: RowVisualStyle;
  visualKey: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
function bind(body: HTMLElement, row: ActionRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  body.className =
    'ok-native-list-row ok-native-list-action-row' +
    (row.presentation === 'accountSelector'
      ? ' ok-native-list-account-action-row'
      : '');
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  v.title.style.cssText = '';
  v.title.textContent = row.title;
  v.title.dataset.tone = row.tone ?? '';
  if (row.presentation === 'accountSelector' && row.icon)
    v.title.style.fontWeight = '500';
  if (style?.title) primitives.textStyle(v.title, style.title);
  const visualKey = JSON.stringify([row.key, row.icon]);
  if (v.visualKey !== visualKey) {
    if (v.visual) {
      primitives.dispose(v.visual);
      v.visual.remove();
    }
    v.visual = primitives.visual(row.icon);
    v.visualStyle = v.visual ? new RowVisualStyle(v.visual) : undefined;
    if (v.visual) body.insertBefore(v.visual, v.title);
    v.visualKey = visualKey;
  }
  if (v.visualStyle && v.visual) {
    const size = row.presentation === 'accountSelector' ? 32 : 40;
    v.visualStyle.apply(
      style?.image,
      style?.image?.width ?? size,
      style?.image?.height ?? size,
      (style?.leadingGap ?? 12) - 12
    );
  }
  v.controls.replaceChildren();
  v.controls.style.cssText = '';
  v.controls.removeAttribute('data-native-list-account-control');
  const descriptors = row.trailing ?? [];
  if (row.checkbox)
    v.controls.appendChild(primitives.accessory(row.key, row.checkbox, 0));
  if (descriptors.length) {
    const trailing = body.ownerDocument.createElement('span');
    trailing.className = 'ok-native-list-accessories';
    if (style?.trailingGap !== undefined)
      trailing.style.gap = `${style.trailingGap}px`;
    if (
      hasExplicitRowHeight(row) &&
      row.presentation === 'accountSelector' &&
      descriptors.length === 1 &&
      descriptors[0]?.kind === 'icon' &&
      descriptors[0].name === 'PlusSmallOutline'
    )
      trailing.dataset.nativeListAccountControl = 'createAddress';
    let valueIndex = 0;
    descriptors.forEach((descriptor, index) => {
      const node = primitives.accessory(row.key, descriptor, index);
      if (descriptor.kind === 'value' || descriptor.kind === 'valuePair') {
        node.dataset.nlSlot = valueIndex++ === 0 ? 'value' : 'valueSecondary';
        if (node.dataset.nlSlot === 'value' && style?.value)
          primitives.textStyle(node, style.value);
      }
      trailing.appendChild(node);
    });
    v.controls.appendChild(trailing);
  }
  v.controls.style.display = 'contents';
  if (row.presentation === 'accountSelector') applyTabularNumbers(body);
}
function create(document: Document, row: ActionRow, primitives: RowPrimitives) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'action';
  const title = document.createElement('span');
  title.className = 'ok-native-list-action-title';
  title.dataset.nlSlot = 'title';
  const controls = document.createElement('span');
  body.append(title, controls);
  views.set(body, { title, controls, visualKey: '', primitives });
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
export const actionRowRenderer = {
  key: 'action' as const,
  create,
  bind,
  recycle,
  measure: (row: ActionRow, _width: number) =>
    row.presentation === 'accountSelector' ? 48 : row.icon ? 60 : 44,
  measureRendered: (_body: HTMLElement, _row: ActionRow): number | undefined =>
    undefined,
};
