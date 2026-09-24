import type { NativeListActionSource } from '../../models';
export function createElement(
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

export function setData(
  element: HTMLElement,
  name: string,
  value: string | number | boolean | undefined
) {
  if (value === undefined) delete element.dataset[name];
  else element.dataset[name] = String(value);
}

export function tagSlot<T extends HTMLElement>(element: T, slot: string): T {
  element.dataset.nlSlot = slot;
  return element;
}

export function markActionAnchorSource(
  element: HTMLElement,
  source: NativeListActionSource,
  slot?: number
) {
  setData(element, 'nativeListAnchorSource', source);
  if (slot !== undefined) setData(element, 'nativeListAnchorSlot', slot);
}

export function createTextColumn(
  document: Document,
  title: string,
  subtitle?: string,
  tertiary?: string,
  tertiaryTone?: 'secondary' | 'info',
  badges?: readonly Readonly<{ text: string; tone?: string }>[],
  slots: Readonly<{ title: string; subtitle: string; tertiary: string }> = {
    title: 'title',
    subtitle: 'subtitle',
    tertiary: 'tertiary',
  }
): HTMLElement {
  const column = createElement(document, 'span', 'ok-native-list-flex');
  const titleLine = tagSlot(
    createElement(document, 'span', 'ok-native-list-title', title),
    slots.title
  );
  if (badges?.length) {
    titleLine.removeAttribute('data-nl-slot');
    titleLine.replaceChildren(
      tagSlot(createElement(document, 'span', undefined, title), slots.title)
    );
    const badgeLine = createElement(document, 'span', 'ok-native-list-badges');
    badges.forEach((badge) =>
      badgeLine.appendChild(createBadge(document, badge))
    );
    titleLine.appendChild(badgeLine);
  }
  column.appendChild(titleLine);
  if (subtitle)
    column.appendChild(
      tagSlot(
        createElement(document, 'span', 'ok-native-list-secondary', subtitle),
        slots.subtitle
      )
    );
  if (tertiary) {
    const element = tagSlot(
      createElement(
        document,
        'span',
        tertiaryTone === 'info'
          ? 'ok-native-list-secondary ok-native-list-info'
          : 'ok-native-list-secondary ok-native-list-tertiary',
        tertiary
      ),
      slots.tertiary
    );
    column.appendChild(element);
  }
  return column;
}
export function createBadge(
  document: Document,
  badge: Readonly<{ text: string; tone?: string }>
): HTMLElement {
  const element = tagSlot(
    createElement(document, 'span', 'ok-native-list-badge', badge.text),
    'badge'
  );
  setData(element, 'tone', badge.tone);
  return element;
}

// Selector digits retain their font family, including explicit button fonts.
export function applyTabularNumbers(body: HTMLElement): void {
  body.style.fontVariantNumeric = 'tabular-nums';
  body.querySelectorAll<HTMLElement>('span,button').forEach((text) => {
    text.style.fontVariantNumeric = 'tabular-nums';
  });
}

/**
 * Selector presentations use their explicit-height geometry whenever the row
 * has a resolved explicit height: the legacy `row.height` or the preferred
 * `style.container.height`.
 */
export function hasExplicitRowHeight(
  row: Readonly<{
    height?: number;
    style?: Readonly<{ container?: Readonly<{ height?: number }> }>;
  }>
): boolean {
  return (row.style?.container?.height ?? row.height) !== undefined;
}

/** Legacy `size` preset adjustment, applied by templates that opt in. */
export function rowSizeModifier(size: string | undefined): number {
  if (size === 'small') return -8;
  if (size === 'large') return 12;
  return 0;
}

// Async image reveal state is owned by the image lifecycle, not by template
// defaults: a reused image that already fired `load` never fires it again.
const isImageLoadingNode = (node: HTMLElement) =>
  node.tagName === 'IMG' ||
  node.classList.contains('ok-native-list-selector-image-background');

/** Captures factory inline styles of a template subtree. */
export function captureInlineStyles(
  root: HTMLElement
): Map<HTMLElement, string> {
  return new Map(
    [root, ...root.querySelectorAll<HTMLElement>('*')].map((node) => [
      node,
      node.style.cssText,
    ])
  );
}

/** Restores captured factory styles while keeping image loading visibility. */
export function restoreInlineStyles(defaults: Map<HTMLElement, string>) {
  defaults.forEach((css, node) => {
    if (!isImageLoadingNode(node)) {
      node.style.cssText = css;
      return;
    }
    const opacity = node.style.opacity;
    node.style.cssText = css;
    node.style.opacity = opacity;
  });
}
