import { TOAST_GEOMETRY } from './toastMotion';
import { getSquirclePath } from './toastSquircle';
import { TOAST_STATUS_GLYPHS } from './toastStatusGlyph';

import type { IOverlayToastStatusIcon } from './types';

const SVG_NS = 'http://www.w3.org/2000/svg';

function createGlyph(icon: IOverlayToastStatusIcon): HTMLElement {
  const glyph = document.createElement('span');
  glyph.setAttribute('data-okt-status-glyph', '');
  const svg = document.createElementNS(SVG_NS, 'svg');
  const recipe = TOAST_STATUS_GLYPHS[icon.status];
  svg.setAttribute('viewBox', '0 0 24 24');
  svg.setAttribute('fill', 'none');
  svg.setAttribute('stroke', 'currentColor');
  svg.setAttribute('stroke-width', String(recipe.strokeWidth));
  svg.setAttribute('stroke-linecap', 'round');
  svg.setAttribute('stroke-linejoin', 'round');
  for (const points of recipe.contours) {
    const path = document.createElementNS(SVG_NS, 'path');
    path.setAttribute('data-okt-status-stroke', '');
    path.setAttribute('pathLength', '1');
    path.setAttribute(
      'd',
      `M${points.map((point) => point.join(' ')).join('L')}`
    );
    svg.append(path);
  }
  const circle = (x: number, y: number, radius: number) => {
    const node = document.createElementNS(SVG_NS, 'circle');
    node.setAttribute('cx', String(x));
    node.setAttribute('cy', String(y));
    node.setAttribute('r', String(radius));
    svg.append(node);
    return node;
  };
  if (recipe.dot) {
    const dot = circle(...recipe.dot);
    dot.setAttribute('data-okt-status-dot', '');
    dot.setAttribute('fill', 'currentColor');
    dot.setAttribute('stroke', 'none');
  }
  if (recipe.ring) {
    const { radius, trackOpacity, arcFraction } = recipe.ring;
    const track = circle(12, 12, radius);
    track.setAttribute('stroke-opacity', String(trackOpacity));
    const arc = circle(12, 12, radius);
    arc.setAttribute('transform', 'rotate(-90 12 12)');
    arc.setAttribute('pathLength', '1');
    arc.setAttribute('stroke-dasharray', `${arcFraction} 1`);
    svg.setAttribute('data-okt-status-ring', '');
  }
  glyph.append(svg);
  return glyph;
}

export function createStatusTile(
  icon: IOverlayToastStatusIcon,
  entering = false
): HTMLElement {
  const tile = document.createElement('span');
  tile.setAttribute('aria-hidden', 'true');
  tile.setAttribute('data-okt-status-tile', icon.status);
  if (entering) {
    tile.setAttribute('data-okt-status-motion', 'enter');
  }
  updateStatusTile(tile, icon, false);
  return tile;
}

export function updateStatusTile(
  tile: HTMLElement,
  icon: IOverlayToastStatusIcon,
  settle: boolean
) {
  const previous = tile.getAttribute('data-okt-status-tile');
  const glyph = tile.querySelector<HTMLElement>(
    '[data-okt-status-glyph]:not([data-okt-status-outgoing])'
  );
  if (!glyph || previous !== icon.status) {
    tile.querySelector('[data-okt-status-outgoing]')?.remove();
    if (settle && glyph && previous && previous !== icon.status) {
      const { transform: tileTransform, opacity: tileOpacity } =
        getComputedStyle(tile);
      const { transform: glyphTransform, opacity: glyphOpacity } =
        getComputedStyle(glyph);
      const visible = Number(glyphOpacity) > 0;
      tile.style.setProperty('--okt-tile-from-scale', tileTransform);
      tile.style.setProperty('--okt-tile-from-opacity', tileOpacity);
      tile.setAttribute(
        'data-okt-status-motion',
        visible ? 'settle' : 'immediate'
      );
      if (visible) {
        glyph.getAnimations?.().forEach((animation) => animation.cancel());
        glyph.style.setProperty('--okt-glyph-from-transform', glyphTransform);
        glyph.style.setProperty('--okt-glyph-from-opacity', glyphOpacity);
        glyph.setAttribute('data-okt-status-outgoing', '');
        glyph.addEventListener('animationend', (event) => {
          if (event.animationName === 'onekey-status-leave') {
            glyph.remove();
          }
        });
      } else {
        glyph.remove();
      }
    } else if (glyph) {
      tile.removeAttribute('data-okt-status-motion');
      glyph.remove();
    }
    tile.setAttribute('data-okt-status-tile', icon.status);
    tile.append(createGlyph(icon));
  }
  const radius =
    (icon.size * TOAST_GEOMETRY.tileRadius) / TOAST_GEOMETRY.tileSize;
  const squircle = getSquirclePath(icon.size, icon.size, radius, 1);
  tile.style.width = `${icon.size}px`;
  tile.style.height = `${icon.size}px`;
  tile.style.borderRadius = `${radius}px`;
  if (squircle) {
    tile.style.clipPath = `path('${squircle}')`;
  }
  tile.style.backgroundColor = icon.backgroundColor;
  tile.style.color = icon.color;
}

export function stopStatusTile(tile: HTMLElement) {
  tile.removeAttribute('data-okt-status-motion');
  tile.querySelector('[data-okt-status-outgoing]')?.remove();
}
