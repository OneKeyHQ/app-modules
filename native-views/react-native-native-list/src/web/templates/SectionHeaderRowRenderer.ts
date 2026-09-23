import type { SectionHeaderRow } from '../../models';
import {
  createElement,
  setData,
  tagSlot,
  markActionAnchorSource,
  createTextColumn,
} from './RowElements';
import { applyValueSegments } from './RowText';
import type { RowPrimitives } from './RowVisual';
function createSectionHeader(
  document: Document,
  primitives: RowPrimitives,
  row: SectionHeaderRow
): HTMLElement {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-section'
  );
  setData(body, 'variant', row.variant);
  if (row.titleIcon) {
    const titleIcon = primitives.iconAction(
      row.titleIcon.name,
      row.titleIcon.actionKey,
      row.titleIcon.disabled,
      row.titleIcon.tintColor
    );
    markActionAnchorSource(titleIcon, 'leadingAction');
    body.appendChild(titleIcon);
  }
  // OneKey patch: section title help has its own measurable action target.
  // body.appendChild(createTextColumn(document, row.title, row.subtitle));
  const column = createTextColumn(document, row.title, row.subtitle);
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
        document,
        'span',
        'ok-native-list-section-title-text',
        row.title
      );
      text.style.overflow = 'hidden';
      text.style.textOverflow = 'ellipsis';
      text.style.maxWidth = '100%';
      const dotted = document.createElementNS(
        'http://www.w3.org/2000/svg',
        'svg'
      );
      dotted.setAttribute('height', '2');
      dotted.style.cssText =
        'display:block;position:absolute;left:0;bottom:0;width:100%;height:2px;color:var(--nl-secondary)';
      const line = document.createElementNS(
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
      const lineViewport = document.createElementNS(
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
    const value = tagSlot(
      createElement(
        document,
        row.valueActionKey ? 'button' : 'span',
        row.valueActionKey
          ? 'ok-native-list-action-button ok-native-list-section-value'
          : 'ok-native-list-value ok-native-list-section-value',
        row.value
      ),
      'value'
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
    const valueIcon = primitives.iconAction(
      row.valueIcon.name,
      row.valueIcon.actionKey,
      row.valueIcon.disabled,
      row.valueIcon.tintColor
    );
    markActionAnchorSource(valueIcon, 'trailingAccessory', 1);
    body.appendChild(valueIcon);
  }
  if (row.checkbox)
    body.appendChild(primitives.accessory(row.key, row.checkbox, 0));
  return body;
}
function bind(
  body: HTMLElement,
  row: SectionHeaderRow,
  primitives: RowPrimitives
) {
  const next = createSectionHeader(body.ownerDocument, primitives, row);
  body.replaceChildren(...Array.from(next.childNodes));
  body.className = next.className;
  body.style.cssText = next.style.cssText;
  body.dataset.variant = row.variant ?? '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  if (!style) return;
  if (style.horizontalPadding !== undefined)
    body.style.paddingInline = style.horizontalPadding + 'px';
  if (style.verticalPadding !== undefined)
    body.style.paddingBlock = style.verticalPadding + 'px';
  if (style.lineGap !== undefined)
    body
      .querySelectorAll<HTMLElement>(':scope > .ok-native-list-flex')
      .forEach((column) => {
        column.style.rowGap = style.lineGap + 'px';
      });
  if (style.trailingGap !== undefined) {
    const children = Array.from(body.children) as HTMLElement[];
    const column = children.findIndex((child) =>
      child.classList.contains('ok-native-list-flex')
    );
    children.slice(column + 2).forEach((child) => {
      child.style.marginInlineStart =
        'calc(' +
        style.trailingGap +
        'px - ' +
        (body.style.gap || '12px') +
        ')';
    });
  }
  for (const slot of ['title', 'subtitle', 'value'] as const)
    if (style[slot])
      body
        .querySelectorAll<HTMLElement>('[data-nl-slot="' + slot + '"]')
        .forEach((node) => primitives.textStyle(node, style[slot]!));
}
function create(
  document: Document,
  row: SectionHeaderRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'sectionHeader';
  bind(body, row, primitives);
  return body;
}
export const sectionHeaderRowRenderer = {
  key: 'sectionHeader' as const,
  create,
  bind,
  recycle: (body: HTMLElement) => body.replaceChildren(),
  appliesSizePreset: (row: SectionHeaderRow) =>
    !['summary', 'gallery'].includes(row.variant ?? ''),
  measure: (row: SectionHeaderRow, _width: number, layout = 'linear') =>
    layout === 'table'
      ? 28
      : row.presentation === 'networkSelector'
      ? 47
      : row.variant === 'history' ||
        row.key.startsWith('history-') ||
        row.sectionKey.startsWith('history-')
      ? 16
      : row.variant === 'summary'
      ? 68
      : row.variant === 'gallery'
      ? 32
      : row.checkbox
      ? 56
      : layout === 'linear'
      ? 30
      : 36,
  measureRendered: (
    _body: HTMLElement,
    _row: SectionHeaderRow
  ): number | undefined => undefined,
};
