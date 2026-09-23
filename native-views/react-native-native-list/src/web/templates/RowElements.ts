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
