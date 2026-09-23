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
