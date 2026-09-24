import type { NativeListTextStyle, SystemRow } from '../../models';
import type { RowPrimitives } from './RowVisual';
import {
  captureInlineStyles,
  createElement,
  restoreInlineStyles,
  setData,
  tagSlot,
  markActionAnchorSource,
} from './RowElements';
function createSystemRow(
  document: Document,
  primitives: RowPrimitives,
  row: SystemRow
): HTMLElement {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-system'
  );
  setData(body, 'variant', row.variant);
  if ('presentation' in row)
    setData(body, 'nativeListPresentation', row.presentation);
  if (row.variant === 'loading' && row.loadingStyle === 'skeleton') {
    body.classList.add('ok-native-list-market-skeleton');
    const background = primitives.background;
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
    const left = createElement(document, 'div', 'ok-native-list-skeleton-left');
    const mark = (width: number, height: number, circle = false) => {
      const element = createElement(
        document,
        'span',
        'ok-native-list-skeleton-mark'
      );
      element.style.width = `${width}px`;
      element.style.height = `${height}px`;
      if (circle) element.style.borderRadius = '50%';
      return element;
    };
    left.appendChild(mark(32, 32, true));
    const text = createElement(document, 'div', 'ok-native-list-skeleton-text');
    text.appendChild(mark(80, 16));
    text.appendChild(mark(60, 12));
    left.appendChild(text);
    body.appendChild(left);
    const right = createElement(
      document,
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
      document,
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
      const message = tagSlot(
        createElement(document, 'span', '', row.message),
        'message'
      );
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
        const mark = createElement(document, 'span', '');
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
      tagSlot(
        createElement(
          document,
          'span',
          'ok-native-list-warning-title',
          row.title
        ),
        'title'
      )
    );
    body.appendChild(
      tagSlot(
        createElement(
          document,
          'span',
          'ok-native-list-warning-message',
          row.message
        ),
        'message'
      )
    );
    return body;
  }
  if (row.variant === 'loading')
    body.appendChild(createElement(document, 'span', 'ok-native-list-spinner'));
  const message =
    row.variant === 'spacer'
      ? ''
      : row.message ?? (row.variant === 'end' ? 'End' : '');
  if (message)
    body.appendChild(
      tagSlot(
        createElement(document, 'span', 'ok-native-list-secondary', message),
        'message'
      )
    );
  // Legacy Web: only the Market retry draws a button. A non-Market retry shows
  // its message and the whole row triggers the retry action, so
  // `style.actionText` has no target there.
  if (
    row.variant === 'retry' &&
    'presentation' in row &&
    row.presentation === 'market'
  ) {
    const action = tagSlot(
      createElement(
        document,
        'button',
        'ok-native-list-action-button',
        row.actionText ?? 'Retry'
      ),
      'actionText'
    );
    action.setAttribute('type', 'button');
    setData(action, 'nativeListAction', row.actionKey);
    markActionAnchorSource(action, 'trailingAccessory', 0);
    body.appendChild(action);
  }
  return body;
}

type Binding = { key: string; defaults: Map<HTMLElement, string> };
const bindings = new WeakMap<HTMLElement, Binding>();
function bind(body: HTMLElement, row: SystemRow, primitives: RowPrimitives) {
  const content = { ...row, style: undefined };
  const key = JSON.stringify([content, primitives.background]);
  let binding = bindings.get(body);
  if (!binding || binding.key !== key) {
    const next = createSystemRow(body.ownerDocument, primitives, row);
    body.replaceChildren(...Array.from(next.childNodes));
    body.className = next.className;
    body.style.cssText = next.style.cssText;
    body.dataset.variant = row.variant;
    body.dataset.nativeListPresentation =
      'presentation' in row ? row.presentation ?? '' : '';
    binding = {
      key,
      defaults: captureInlineStyles(body),
    };
    bindings.set(body, binding);
  }
  restoreInlineStyles(binding.defaults);
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  if (style?.lineGap !== undefined && row.variant === 'warning')
    body.style.rowGap = `${style.lineGap}px`;
  for (const slot of ['title', 'message', 'actionText'] as const) {
    const text = style?.[slot];
    if (!text) continue;
    body
      .querySelectorAll<HTMLElement>(`[data-nl-slot="${slot}"]`)
      .forEach((node) =>
        primitives.textStyle(
          node,
          text,
          row.variant === 'warning' ? 0 : undefined
        )
      );
  }
}
function create(document: Document, row: SystemRow, primitives: RowPrimitives) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'system';
  bind(body, row, primitives);
  return body;
}
function measure(row: SystemRow, width: number) {
  if (row.variant === 'warning') {
    const textWidth = Math.max(
      1,
      width - (row.style?.horizontalPadding ?? 12) * 2
    );
    const height = (text: string, style: NativeListTextStyle | undefined) => {
      const size = style?.fontSize ?? 14;
      const lines = text
        .split(/\r\n|[\r\n]/)
        .reduce(
          (total, line) =>
            total +
            Math.max(
              1,
              Math.ceil(
                Array.from(line).reduce(
                  (length, char) =>
                    length + (char.charCodeAt(0) > 255 ? size : size / 2),
                  0
                ) / textWidth
              )
            ),
          0
        );
      return (
        Math.min(style?.lines ?? Infinity, lines) * (style?.lineHeight ?? 20)
      );
    };
    return (
      (row.style?.verticalPadding ?? 14) * 2 +
      (row.style?.lineGap ?? 4) +
      height(row.title, row.style?.title) +
      height(row.message, row.style?.message)
    );
  }
  return row.variant === 'spacer'
    ? row.height
    : row.variant === 'loading' && row.loadingStyle === 'skeleton'
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
}
export const systemRowRenderer = {
  key: 'system' as const,
  appliesSizePreset: (row: SystemRow) =>
    !['warning', 'spacer'].includes(row.variant),
  create,
  bind,
  measure,
  measureRendered: (_body: HTMLElement, _row: SystemRow): number | undefined =>
    undefined,
  recycle: (body: HTMLElement) => {
    bindings.delete(body);
    body.replaceChildren();
  },
};
