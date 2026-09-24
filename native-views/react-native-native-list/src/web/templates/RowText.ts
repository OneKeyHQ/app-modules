import type { NativeListTextStyle } from '../../models';
import { createElement } from './RowElements';
export function applyValueSegments(
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
export function applyTextLayout(
  element: HTMLElement,
  style: NativeListTextStyle,
  defaultLines?: number
): void {
  if (
    style.lines === undefined &&
    style.truncate === undefined &&
    style.verticalAlignment === undefined &&
    style.offsetY === undefined &&
    defaultLines === undefined
  )
    return;
  let content = element.querySelector<HTMLElement>(
    ':scope > .ok-native-list-text-content'
  );
  // Line clamps are only ever written inline by templates (no stylesheet rule
  // sets them), so no computed-style read is needed; this also keeps the result
  // identical for attached and detached rows.
  const inheritedClamp = Number(
    element.style.getPropertyValue('-webkit-line-clamp')
  );
  const lines = style.lines ?? defaultLines ?? (inheritedClamp || 1);
  if (
    !content &&
    (style.verticalAlignment !== undefined ||
      style.offsetY !== undefined ||
      element.classList.contains('ok-native-list-market-change'))
  ) {
    content = element.ownerDocument.createElement('span');
    content.className = 'ok-native-list-text-content';
    content.append(...Array.from(element.childNodes));
    element.appendChild(content);
    element.style.display = 'flex';
    element.style.flexDirection = 'column';
    element.style.removeProperty('-webkit-line-clamp');
    element.style.removeProperty('-webkit-box-orient');
    content.style.minWidth = '0';
    content.style.flex = '0 1 auto';
    content.style.width = '100%';
  }
  const text = content ?? element;
  if (style.verticalAlignment !== undefined) {
    element.style.justifyContent = {
      top: 'flex-start',
      center: 'center',
      bottom: 'flex-end',
    }[style.verticalAlignment];
  }
  if (style.offsetY !== undefined)
    text.style.transform = `translateY(${style.offsetY}px)`;
  if (
    style.lines === undefined &&
    style.truncate === undefined &&
    defaultLines === undefined &&
    !inheritedClamp
  ) {
    if (content) {
      // Inherit the slot's resolved wrapping/truncation from CSS or inline style.
      content.style.whiteSpace = 'inherit';
      content.style.textOverflow = 'inherit';
      content.style.overflow = 'hidden';
    }
    return;
  }
  if (lines === 1) {
    const walker = element.ownerDocument.createTreeWalker(
      text,
      4 /* SHOW_TEXT */
    );
    let previousCarriageReturn = false;
    while (walker.nextNode()) {
      const value = walker.currentNode.nodeValue ?? '';
      if (!value) continue;
      // A CRLF pair may straddle two styled runs.
      const textValue =
        previousCarriageReturn && value.startsWith('\n')
          ? value.slice(1)
          : value;
      walker.currentNode.nodeValue = textValue.replace(/\r\n|[\r\n]/g, ' ');
      previousCarriageReturn = value.endsWith('\r');
    }
  }
  text.style.whiteSpace = lines === 1 ? 'nowrap' : 'pre-wrap';
  text.style.overflow = 'hidden';
  text.style.textOverflow = style.truncate === 'clip' ? 'clip' : 'ellipsis';
  text.style.removeProperty('-webkit-line-clamp');
  text.style.removeProperty('-webkit-box-orient');
  text.style.maxHeight = '';
  if (lines > 1 && style.truncate !== 'clip') {
    text.style.display = '-webkit-box';
    text.style.setProperty('-webkit-line-clamp', String(lines));
    text.style.setProperty('-webkit-box-orient', 'vertical');
  } else {
    text.style.display = 'block';
    if (lines > 1) {
      // Rows are styled before mounting, when computed typography is unavailable.
      // Resolve omitted line height in CSS after the template rules take effect.
      text.style.maxHeight =
        style.lineHeight !== undefined
          ? `${lines * style.lineHeight}px`
          : `${lines}lh`;
    }
  }
}
export function applyTextStyleToSlot(
  element: HTMLElement,
  style: NativeListTextStyle,
  defaultLines?: number
): void {
  if (style.fontSize !== undefined)
    element.style.fontSize = String(style.fontSize) + 'px';
  if (style.lineHeight !== undefined)
    element.style.lineHeight = String(style.lineHeight) + 'px';
  if (style.fontWeight !== undefined)
    element.style.fontWeight = String(fontWeight(style.fontWeight, 400));
  if (style.color !== undefined) element.style.color = style.color;
  // Explicit overrides also apply to rich text runs, but never to sibling badges.
  element.querySelectorAll<HTMLElement>('span').forEach((run) => {
    if (style.color !== undefined) run.style.color = style.color;
    if (style.fontSize !== undefined)
      run.style.fontSize = String(style.fontSize) + 'px';
    if (style.fontWeight !== undefined)
      run.style.fontWeight = String(fontWeight(style.fontWeight, 400));
    if (style.lineHeight !== undefined)
      run.style.lineHeight = String(style.lineHeight) + 'px';
    if (style.lines !== undefined || style.truncate !== undefined)
      run.style.whiteSpace = 'inherit';
  });
  if (style.alignment !== undefined) element.style.textAlign = style.alignment;
  applyTextLayout(element, style, defaultLines);
}
export function fontWeight(
  value: NativeListTextStyle['fontWeight'] | undefined,
  fallback: number
): number {
  if (value === 'bold') return 700;
  if (value === 'semibold') return 600;
  if (value === 'medium') return 500;
  if (value === 'regular') return 400;
  return fallback;
}
