import { iconResourceHref } from './assetUrls';

/** Native SDK DOM consumers own name coverage, styling and accessible labels. */
export function createNamedIcon(
  document: Document,
  name: string
): SVGSVGElement {
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.setAttribute('width', '24');
  svg.setAttribute('height', '24');
  svg.setAttribute('aria-hidden', 'true');
  svg.setAttribute('focusable', 'false');
  const href = iconResourceHref(name);
  if (href) {
    const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
    use.setAttribute('href', href);
    svg.append(use);
  }
  return svg;
}
/// <reference lib="dom" />
