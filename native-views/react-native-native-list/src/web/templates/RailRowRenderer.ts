import type { NativeListTextStyle, RailRow } from '../../models';
import type { RowPrimitives } from './RowVisual';
import { RowVisualStyle } from './RowVisual';

export function resolveRailRow(row: RailRow) {
  const style = row.style;
  return {
    horizontalPadding: style?.horizontalPadding ?? 4,
    verticalPadding: style?.verticalPadding ?? 4,
    leadingGap: style?.leadingGap ?? 6,
    badgeGap: style?.titleBadgeGap ?? 6,
    trailingGap: style?.trailingGap ?? 6,
    width: style?.image?.width ?? 20,
    height: style?.image?.height ?? 20,
    title: row.title,
    badge: row.badge?.text ?? '',
    status: row.status === 'none' ? '' : row.status ?? '',
  };
}

type Views = {
  title: HTMLElement;
  badge: HTMLElement;
  status: HTMLElement;
  visual?: HTMLElement;
  visualStyle?: RowVisualStyle;
  identity: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();

function bind(body: HTMLElement, row: RailRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  const r = resolveRailRow(row);
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  body.style.padding = `${r.verticalPadding}px ${r.horizontalPadding}px`;
  const text = (
    view: HTMLElement,
    value: string,
    style?: NativeListTextStyle
  ) => {
    view.style.cssText = '';
    view.textContent = value;
    if (style) primitives.textStyle(view, style);
    if (value) body.appendChild(view);
    else view.remove();
  };
  text(v.title, r.title, row.style?.title);
  text(v.badge, r.badge, row.style?.badge);
  if (row.badge?.tone) v.badge.dataset.tone = row.badge.tone;
  else delete v.badge.dataset.tone;
  v.badge.style.marginInlineStart = `${r.badgeGap - 6}px`;
  text(v.status, r.status, row.style?.status);
  v.status.style.marginInlineStart = `${r.trailingGap - 6}px`;
  const identity = JSON.stringify([row.key, row.visual]);
  if (identity !== v.identity) {
    if (v.visual) {
      primitives.dispose(v.visual);
      v.visual.remove();
    }
    v.visual = primitives.visual(row.visual);
    v.visualStyle = v.visual ? new RowVisualStyle(v.visual) : undefined;
    v.identity = identity;
  }
  if (v.visual) body.insertBefore(v.visual, body.firstChild);
  v.visualStyle?.apply(row.style?.image, r.width, r.height, r.leadingGap - 6);
}
function create(document: Document, row: RailRow, primitives: RowPrimitives) {
  const span = (className: string, slot: string) => {
    const view = document.createElement('span');
    view.className = className;
    view.dataset.nlSlot = slot;
    return view;
  };
  const body = document.createElement('div');
  body.className = 'ok-native-list-row ok-native-list-rail';
  body.dataset.nlRenderer = 'rail';
  views.set(body, {
    title: span('ok-native-list-rail-title', 'title'),
    badge: span('ok-native-list-badge', 'badge'),
    status: span('ok-native-list-secondary', 'status'),
    identity: '',
    primitives,
  });
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
  v.identity = '';
}
export function measureRailWidth(row: RailRow) {
  const r = resolveRailRow(row);
  const width = (
    value: string,
    style: NativeListTextStyle | undefined,
    size: number
  ) => value.length * 7 * ((style?.fontSize ?? size) / size);
  // Retain the historical 50px allowance and viewport limits at default values.
  return Math.min(
    288,
    Math.max(
      72,
      50 +
        width(r.title, row.style?.title, 12) +
        width(r.badge, row.style?.badge, 11) +
        width(r.status, row.style?.status, 13) +
        (r.horizontalPadding - 4) * 2 +
        r.width -
        20 +
        r.leadingGap -
        6 +
        (r.badge ? r.badgeGap - 6 : 0) +
        (r.status ? r.trailingGap - 6 : 0)
    )
  );
}
export const railRowRenderer = {
  key: 'rail' as const,
  create,
  bind,
  recycle,
  measure: (_row: RailRow, _width: number) => 40,
  measureRendered: (_body: HTMLElement, _row: RailRow): number | undefined =>
    undefined,
};
