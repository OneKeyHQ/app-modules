import { createNamedIcon } from '@onekeyfe/react-native-image/icon/dom';
import type {
  MarketRow,
  WebMarketTableCell,
  WebMarketTableColumn,
  WebMarketTableText,
} from '../../models';
import { createElement, markActionAnchorSource } from './RowElements';
import { applyTextWeight, applyValueSegments } from './RowText';
import type { RowPrimitives } from './RowVisual';

type CellViews = {
  element: HTMLElement;
  kind: WebMarketTableCell['kind'];
  signature: string;
  visual?: HTMLElement;
  visualKey?: string;
  visualImageKey?: string;
  title?: HTMLElement;
  resting?: HTMLElement;
  hover?: HTMLElement;
  assetShape?: string;
  hoverVisuals?: HTMLElement[];
  badgeVisuals?: HTMLElement[];
};
type Views = { cells: Map<string, CellViews>; primitives: RowPrimitives };
const views = new WeakMap<HTMLElement, Views>();
const columnSignatures = new WeakMap<readonly WebMarketTableColumn[], string>();
let sparklineId = 0;
const svgNamespace = 'http://www.w3.org/2000/svg';
function columnsSignature(columns: readonly WebMarketTableColumn[]) {
  let signature = columnSignatures.get(columns);
  if (signature === undefined) {
    signature = JSON.stringify(columns);
    columnSignatures.set(columns, signature);
  }
  return signature;
}
function semanticCellShape(cell: WebMarketTableCell): unknown {
  if (cell.kind === 'asset')
    return {
      key: cell.key,
      kind: cell.kind,
      leading: cell.leading,
      badges: cell.badges,
      hoverActionKey: cell.hoverActionKey,
      lineGap: cell.lineGap,
      secondaryBadges: cell.secondaryBadges,
      hoverImages: cell.hoverImages,
      primaryStyle: cell.title.style,
      secondaryStyle: cell.secondary?.map((value) => value.style),
      hover: cell.hoverSecondary,
    };
  if (cell.kind === 'favorite')
    return {
      key: cell.key,
      kind: cell.kind,
      actionKey: cell.actionKey,
      hoverActionKey: cell.hoverActionKey,
    };
  if (cell.kind === 'sparkline')
    return {
      key: cell.key,
      kind: cell.kind,
      width: cell.width,
      height: cell.height,
    };
  return {
    key: cell.key,
    kind: cell.kind,
    partGap: cell.partGap,
    primary: cell.primary.map((value) => value.style),
    secondary: cell.secondary?.map((value) => value.style),
  };
}
export function canPatchMarketTableValues(
  previous: MarketRow,
  next: MarketRow
): boolean {
  const a = previous.webTable,
    b = next.webTable;
  return (
    !!a &&
    !!b &&
    a.testID === b.testID &&
    a.draggable === b.draggable &&
    a.contextMenuActionKey === b.contextMenuActionKey &&
    previous.height === next.height &&
    previous.style === next.style &&
    columnsSignature(a.columns) === columnsSignature(b.columns) &&
    a.cells.length === b.cells.length &&
    a.cells.every(
      (cell, index) =>
        JSON.stringify(semanticCellShape(cell)) ===
        JSON.stringify(semanticCellShape(b.cells[index]))
    )
  );
}
function tone(value: WebMarketTableText['tone'], secondary: boolean) {
  return value === 'positive'
    ? 'var(--nl-positive)'
    : value === 'negative'
    ? 'var(--nl-negative)'
    : value === 'secondary' || (value === undefined && secondary)
    ? 'var(--nl-secondary)'
    : 'var(--nl-primary)';
}
function textPart(
  document: Document,
  part: WebMarketTableText,
  secondary: boolean
): HTMLElement {
  const element = createElement(document, 'span', undefined, part.text);
  const size = part.style?.fontSize ?? (secondary ? 12 : 16),
    height = part.style?.lineHeight ?? (secondary ? 16 : 24);
  const weight = { regular: 400, medium: 500, semibold: 600, bold: 700 }[
    part.style?.fontWeight ?? (secondary ? 'regular' : 'medium')
  ];
  Object.assign(element.style, {
    fontSize: `${size}px`,
    lineHeight: `${height}px`,
    fontWeight: String(weight),
    color: part.style?.color ?? tone(part.tone, secondary),
    minWidth: '0',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  });
  applyTextWeight(element, weight);
  if (part.segments?.length)
    applyValueSegments(element, part.segments, size, height, weight);
  return element;
}
function line(
  document: Document,
  parts: readonly WebMarketTableText[] | undefined,
  secondary: boolean,
  className?: string
): HTMLElement {
  const element = createElement(document, 'span', className);
  Object.assign(element.style, {
    display: 'flex',
    gap: '2px',
    alignItems: 'center',
    minWidth: '0',
    overflow: 'hidden',
    height: secondary ? '16px' : '24px',
    flexShrink: '0',
  });
  parts?.forEach((part) =>
    element.appendChild(textPart(document, part, secondary))
  );
  return element;
}
function action(
  element: HTMLElement,
  key: string,
  label: string,
  slot: number
) {
  element.dataset.nativeListAction = key;
  markActionAnchorSource(element, 'leadingAction', slot);
  element.setAttribute('aria-label', label);
  element.setAttribute('type', 'button');
  Object.assign(element.style, {
    border: '0',
    padding: '4px',
    background: 'transparent',
    font: 'inherit',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    flexShrink: '0',
  });
}
function icon(document: Document, name: string, size: number): SVGSVGElement {
  const svg = createNamedIcon(document, name);
  svg.setAttribute('width', String(size));
  svg.setAttribute('height', String(size));
  return svg;
}

function drawSparkline(
  element: HTMLElement,
  cell: Extract<WebMarketTableCell, { kind: 'sparkline' }>
) {
  element.replaceChildren();
  if (cell.points.length < 2) return;
  const svg = element.ownerDocument.createElementNS(svgNamespace, 'svg');
  svg.setAttribute('width', String(cell.width));
  svg.setAttribute('height', String(cell.height));
  svg.setAttribute('viewBox', `0 0 ${cell.width} ${cell.height}`);
  svg.setAttribute('aria-hidden', 'true');
  const min = Math.min(...cell.points),
    max = Math.max(...cell.points),
    height = cell.height - 5,
    step = cell.width / (cell.points.length - 1);
  const y = (value: number) =>
    max === min ? height / 2 : height - ((value - min) * height) / (max - min);
  let d = '';
  cell.points.forEach((point, index) => {
    const x = index * step,
      py = y(point);
    if (index === 0) d = `M0 ${py}`;
    else if (index >= 2 && index < cell.points.length - 1) {
      const previousX = (index - 1) * step,
        previousY = y(cell.points[index - 1]),
        beforeX = (index - 2) * step,
        beforeY = y(cell.points[index - 2]),
        nextX = (index + 1) * step,
        nextY = y(cell.points[index + 1]);
      d += ` C${previousX + (x - beforeX) * 0.1} ${Math.max(
        0,
        previousY + (py - beforeY) * 0.1
      )} ${x - (nextX - previousX) * 0.1} ${Math.max(
        0,
        py - (nextY - previousY) * 0.1
      )} ${x} ${py}`;
    } else d += ` L${x} ${py}`;
  });
  const id = `nl-sparkline-${++sparklineId}`,
    clipId = id + '-clip';
  const defs = element.ownerDocument.createElementNS(svgNamespace, 'defs'),
    gradient = element.ownerDocument.createElementNS(
      svgNamespace,
      'linearGradient'
    );
  gradient.id = id;
  gradient.setAttribute('gradientUnits', 'userSpaceOnUse');
  gradient.setAttribute('x1', '0');
  gradient.setAttribute('y1', '0');
  gradient.setAttribute('x2', '0');
  gradient.setAttribute('y2', String(height));
  const start = element.ownerDocument.createElementNS(svgNamespace, 'stop');
  start.setAttribute('offset', '0');
  start.setAttribute('stop-color', cell.fillColor);
  const end = element.ownerDocument.createElementNS(svgNamespace, 'stop');
  end.setAttribute('offset', '1');
  end.setAttribute('stop-color', cell.fillColor);
  end.setAttribute('stop-opacity', '0');
  gradient.append(start, end);
  defs.appendChild(gradient);
  const clip = element.ownerDocument.createElementNS(svgNamespace, 'clipPath');
  clip.id = clipId;
  const rect = element.ownerDocument.createElementNS(svgNamespace, 'rect');
  rect.setAttribute('width', String(cell.width));
  rect.setAttribute('height', String(cell.height - 1));
  clip.appendChild(rect);
  defs.appendChild(clip);
  svg.appendChild(defs);
  const outline = `${d} L${cell.width + step * 2} ${y(
    cell.points[cell.points.length - 1]
  )} L${cell.width + step * 2} ${cell.height} L0 ${cell.height}`;
  const group = element.ownerDocument.createElementNS(svgNamespace, 'g');
  group.setAttribute('clip-path', `url(#${clipId})`);
  const fill = element.ownerDocument.createElementNS(svgNamespace, 'path');
  fill.setAttribute('d', outline + ' Z');
  fill.setAttribute('fill', `url(#${id})`);
  const stroke = element.ownerDocument.createElementNS(svgNamespace, 'path');
  stroke.setAttribute('d', outline);
  stroke.setAttribute('stroke', cell.strokeColor);
  stroke.setAttribute('stroke-width', '1');
  stroke.setAttribute('fill', 'none');
  group.append(fill, stroke);
  svg.appendChild(group);
  element.appendChild(svg);
}
function updateCell(
  view: CellViews,
  cell: WebMarketTableCell,
  primitives: RowPrimitives,
  index: number
) {
  const signature = JSON.stringify(cell);
  if (view.signature === signature) return;
  const element = view.element,
    document = element.ownerDocument;
  if (cell.kind === 'asset') {
    const visualKey = JSON.stringify(cell.leading);
    if (view.visualKey !== visualKey) {
      const visualImageKey = JSON.stringify(
        cell.leading?.kind === 'token'
          ? { ...cell.leading, overlays: undefined }
          : cell.leading
      );
      if (
        view.visual &&
        cell.leading?.kind === 'token' &&
        view.visualImageKey === visualImageKey
      ) {
        // Keep the main frame: its load/error callbacks own its visibility.
        for (const overlay of view.visual.querySelectorAll<HTMLElement>(
          ':scope > .ok-native-list-visual-overlay'
        )) {
          primitives.dispose(overlay);
          overlay.remove();
        }
        const overlayVisual = primitives.visual({
          kind: 'token',
          overlays: cell.leading.overlays,
        });
        if (overlayVisual) {
          for (const overlay of overlayVisual.querySelectorAll<HTMLElement>(
            ':scope > .ok-native-list-visual-overlay'
          ))
            view.visual.appendChild(overlay);
          primitives.dispose(overlayVisual);
        }
      } else {
        if (view.visual) {
          primitives.dispose(view.visual);
          view.visual.remove();
        }
        view.visual = primitives.visual(cell.leading);
      }
      view.visualImageKey = visualImageKey;
      view.visualKey = visualKey;
      if (view.visual)
        Object.assign(view.visual.style, {
          width: '40px',
          height: '40px',
          flexBasis: '40px',
          flexShrink: '0',
          marginInlineEnd: '14px',
          boxSizing: 'border-box',
          border:
            cell.leading &&
            'borderColor' in cell.leading &&
            cell.leading.borderColor
              ? `1px solid ${cell.leading.borderColor}`
              : '0',
        });
      if (view.visual) {
        if (
          cell.leading &&
          'backgroundColor' in cell.leading &&
          cell.leading.backgroundColor
        )
          view.visual.style.background = cell.leading.backgroundColor;
        for (const child of [...view.visual.children]) {
          if (child.classList.contains('ok-native-list-visual-main'))
            Object.assign((child as HTMLElement).style, {
              width: '100%',
              height: '100%',
            });
          if (
            child.classList.contains('ok-native-list-visual-overlay') &&
            cell.leading &&
            'borderColor' in cell.leading &&
            cell.leading.borderColor
          ) {
            const style = (child as HTMLElement).style;
            for (const edge of ['left', 'right', 'top', 'bottom'] as const)
              if (style[edge])
                style[edge] = `${Number.parseFloat(style[edge]) - 1}px`;
          }
        }
      }
    }
    const assetShape = JSON.stringify(semanticCellShape(cell));
    if (view.title && view.assetShape === assetShape) {
      const primary = textPart(document, cell.title, false);
      view.title.firstElementChild?.replaceWith(primary);
      view.resting?.replaceChildren(
        ...(cell.secondary ?? []).map((part) => textPart(document, part, true))
      );
      if (view.hover) {
        const button = view.hover.querySelector('button');
        view.hover.parentElement
          ?.querySelector('button')
          ?.setAttribute('aria-label', cell.hoverActionLabel ?? 'Copy address');
        for (const child of [...view.hover.children])
          if (child !== button) child.remove();
        for (const part of cell.hoverSecondary ?? [])
          view.hover.insertBefore(textPart(document, part, true), button);
      }
      view.signature = signature;
      return;
    }
    view.hoverVisuals?.forEach((visual) => primitives.dispose(visual));
    view.hoverVisuals = [];
    view.badgeVisuals?.forEach((visual) => primitives.dispose(visual));
    view.badgeVisuals = [];
    view.assetShape = assetShape;
    view.title = undefined;
    view.resting = undefined;
    view.hover = undefined;
    for (const child of [...element.children])
      if (child !== view.visual) child.remove();
    if (view.visual) element.appendChild(view.visual);
    const text = createElement(document, 'span');
    Object.assign(text.style, {
      display: 'flex',
      flexDirection: 'column',
      gap: `${cell.lineGap ?? 4}px`,
      minWidth: '0',
      flex: '1',
    });
    const title = line(document, [cell.title], false);
    view.title = title;
    title.style.gap = '4px';
    cell.badges?.forEach((badge, slot) => {
      const node = createElement(
        document,
        badge.actionKey ? 'button' : 'span',
        'ok-native-list-market-badge',
        badge.text
      );
      if (badge.actionKey)
        action(
          node,
          badge.actionKey,
          badge.accessibilityLabel ?? badge.text ?? '',
          slot
        );
      node.style.color = badge.textColor ?? '';
      node.style.backgroundColor = badge.backgroundColor ?? '';
      if (badge.style)
        Object.assign(node.style, {
          fontSize: `${badge.style.fontSize ?? 11}px`,
          fontWeight: String(
            { regular: 400, medium: 500, semibold: 600, bold: 700 }[
              badge.style.fontWeight ?? 'regular'
            ]
          ),
          lineHeight: `${badge.style.lineHeight ?? 16}px`,
          height: `${badge.style.height ?? 16}px`,
          paddingInline: `${badge.style.horizontalPadding ?? 5}px`,
        });
      applyTextWeight(
        node,
        { regular: 400, medium: 500, semibold: 600, bold: 700 }[
          badge.style?.fontWeight ?? 'regular'
        ]
      );
      if (badge.actionKey) node.dataset.nativeListHoverAction = 'true';
      if (badge.icon) {
        const visual = primitives.visual({
          kind: 'image',
          image: badge.icon,
          shape: 'circle',
        });
        if (visual) {
          Object.assign(node.style, {
            height: `${badge.icon.height}px`,
            padding: '0',
            background: 'transparent',
          });
          Object.assign(visual.style, {
            width: `${badge.icon.width}px`,
            height: `${badge.icon.height}px`,
            flexBasis: `${badge.icon.width}px`,
          });
          for (const child of [...visual.children])
            Object.assign((child as HTMLElement).style, {
              width: '100%',
              height: '100%',
            });
          node.appendChild(visual);
          view.badgeVisuals!.push(visual);
        }
      }
      if (badge.iconName === 'verified') {
        Object.assign(node.style, {
          height: '16px',
          padding: '0',
          background: 'transparent',
          borderRadius: '0',
        });
        applyTextWeight(
          node,
          { regular: 400, medium: 500, semibold: 600, bold: 700 }[
            badge.style?.fontWeight ?? 'regular'
          ]
        );
        if (badge.actionKey) node.dataset.nativeListHoverAction = 'true';
        node.textContent = '';
        const svg = icon(document, 'BadgeRecognizedSolid', 14);
        node.appendChild(svg);
      }
      title.appendChild(node);
    });
    text.appendChild(title);
    if (cell.secondary?.length || cell.hoverSecondary?.length) {
      const height =
        cell.secondary?.[0]?.style?.lineHeight ??
        cell.hoverSecondary?.[0]?.style?.lineHeight ??
        16;
      const clip = createElement(
        document,
        'span',
        'ok-native-list-table-secondary'
      );
      clip.style.height = `${height}px`;
      const track = createElement(
        document,
        'span',
        'ok-native-list-table-secondary-track'
      );
      track.style.setProperty('--nl-table-secondary-height', `${height}px`);
      if (cell.secondary?.length) {
        const resting = line(document, cell.secondary, true);
        view.resting = resting;
        resting.style.height = `${height}px`;
        const restingFrame = createElement(document, 'span');
        Object.assign(restingFrame.style, {
          display: 'flex',
          alignItems: 'center',
          gap: '4px',
          height: `${height}px`,
          minWidth: '0',
        });
        cell.secondaryBadges?.forEach((badge) => {
          const node = createElement(
            document,
            'span',
            'ok-native-list-market-badge',
            badge.text
          );
          Object.assign(node.style, {
            fontSize: `${badge.style?.fontSize ?? 11}px`,
            fontWeight: String(
              { regular: 400, medium: 500, semibold: 600, bold: 700 }[
                badge.style?.fontWeight ?? 'regular'
              ]
            ),
            lineHeight: `${badge.style?.lineHeight ?? 12}px`,
            height: `${badge.style?.height ?? 16}px`,
            paddingInline: `${badge.style?.horizontalPadding ?? 4}px`,
          });
          applyTextWeight(
            node,
            { regular: 400, medium: 500, semibold: 600, bold: 700 }[
              badge.style?.fontWeight ?? 'regular'
            ]
          );
          restingFrame.appendChild(node);
        });
        restingFrame.appendChild(resting);
        track.appendChild(restingFrame);
      }
      if (cell.hoverSecondary?.length) {
        const hover = line(document, cell.hoverSecondary, true);
        view.hover = hover;
        hover.style.height = `${height}px`;
        const hoverFrame = createElement(document, 'span');
        Object.assign(hoverFrame.style, {
          display: 'flex',
          alignItems: 'center',
          gap: cell.hoverActionKey ? '2px' : '4px',
          height: `${height}px`,
          minWidth: '0',
        });
        hoverFrame.appendChild(hover);
        if (cell.hoverImages?.length) {
          const group = createElement(document, 'span');
          Object.assign(group.style, {
            display: 'flex',
            alignItems: 'center',
            flexShrink: '0',
          });
          cell.hoverImages.forEach((image, i) => {
            const visual = image
              ? primitives.visual({ kind: 'image', image, shape: 'circle' })
              : createElement(document, 'span', 'ok-native-list-visual');
            if (visual) {
              Object.assign(visual.style, {
                width: '16px',
                height: '16px',
                flexBasis: '16px',
                border: '1px solid var(--nl-row)',
                boxSizing: 'border-box',
                marginInlineEnd:
                  i === cell.hoverImages!.length - 1 ? '0' : '-4px',
                zIndex: String(cell.hoverImages!.length - i),
                overflow: 'hidden',
              });
              for (const child of [...visual.children])
                Object.assign((child as HTMLElement).style, {
                  width: '100%',
                  height: '100%',
                });
              view.hoverVisuals!.push(visual);
              group.appendChild(visual);
            }
          });
          hoverFrame.appendChild(group);
        }
        if (cell.hoverActionKey) {
          const button = createElement(document, 'button');
          action(
            button,
            cell.hoverActionKey,
            cell.hoverActionLabel ?? 'Copy address',
            index
          );
          button.style.padding = '0';
          button.style.color = 'var(--nl-icon-subdued)';
          button.appendChild(icon(document, 'Copy3Outline', 14));
          hoverFrame.appendChild(button);
        }
        track.appendChild(hoverFrame);
        if (cell.secondary?.length) clip.dataset.hasHover = 'true';
      }
      clip.appendChild(track);
      text.appendChild(clip);
    }
    element.appendChild(text);
  } else if (cell.kind === 'favorite') {
    const button =
      element.querySelector('button') ?? createElement(document, 'button');
    action(button, cell.actionKey, cell.accessibilityLabel, index);
    button.setAttribute('aria-pressed', String(cell.active));
    (button as HTMLButtonElement).disabled = !!cell.disabled;
    button.removeAttribute('title');
    if (cell.hoverActionKey)
      button.dataset.nativeListHoverAction = cell.hoverActionKey;
    else delete button.dataset.nativeListHoverAction;
    if (cell.testID) button.dataset.testid = cell.testID;
    else delete button.dataset.testid;
    button.style.width = '24px';
    button.style.height = '24px';
    button.style.color = cell.active
      ? 'var(--nl-accent)'
      : 'var(--nl-icon-subdued)';
    let svg = button.querySelector('svg');
    if (!svg) {
      svg = icon(document, cell.active ? 'StarSolid' : 'StarOutline', 16);
      button.appendChild(svg);
    }
    const name = cell.active ? 'StarSolid' : 'StarOutline';
    if (
      !svg
        .querySelector('use')
        ?.getAttribute('href')
        ?.endsWith(`#onekey-${name}`)
    ) {
      const replacement = icon(document, name, 16);
      svg.replaceWith(replacement);
    }
    if (!button.parentElement) element.appendChild(button);
  } else if (cell.kind === 'sparkline') drawSparkline(element, cell);
  else {
    element.replaceChildren();
    const column = createElement(document, 'span');
    Object.assign(column.style, {
      display: 'flex',
      flexDirection: 'column',
      gap: '4px',
      minWidth: '0',
      width: '100%',
    });
    const primary = line(document, cell.primary, false);
    primary.style.gap = `${cell.partGap ?? 2}px`;
    column.appendChild(primary);
    if (cell.secondary?.length)
      column.appendChild(line(document, cell.secondary, true));
    element.appendChild(column);
  }
  view.signature = signature;
}
function bind(body: HTMLElement, row: MarketRow, primitives: RowPrimitives) {
  const state = views.get(body)!;
  state.primitives = primitives;
  const table = row.webTable!;
  if (table.testID) body.dataset.testid = table.testID;
  else delete body.dataset.testid;
  const desired = new Set(table.columns.map((column) => column.key));
  state.cells.forEach((view, key) => {
    if (!desired.has(key)) {
      view.hoverVisuals?.forEach((visual) => primitives.dispose(visual));
      view.badgeVisuals?.forEach((visual) => primitives.dispose(visual));
      if (view.visual) primitives.dispose(view.visual);
      view.element.remove();
      state.cells.delete(key);
    }
  });
  table.columns.forEach((column, index) => {
    const cell = table.cells[index];
    let view = state.cells.get(column.key);
    if (view?.kind !== cell.kind) {
      view?.hoverVisuals?.forEach((visual) => primitives.dispose(visual));
      view?.badgeVisuals?.forEach((visual) => primitives.dispose(visual));
      if (view?.visual) primitives.dispose(view.visual);
      view?.element.remove();
      state.cells.delete(column.key);
      view = undefined;
    }
    if (!view) {
      view = {
        element: createElement(
          body.ownerDocument,
          'span',
          'ok-native-list-table-cell'
        ),
        kind: cell.kind,
        signature: '',
      };
      view.element.dataset.columnKey = column.key;
      state.cells.set(column.key, view);
    }
    const element = view.element;
    Object.assign(element.style, {
      display: 'flex',
      boxSizing: 'border-box',
      alignItems: 'center',
      minWidth: `${column.minWidth ?? 0}px`,
      width: column.width === undefined ? 'auto' : `${column.width}px`,
      flex:
        column.flex !== undefined
          ? `${column.flex} 1 auto`
          : column.width === undefined
          ? '1 1 0px'
          : `0 0 ${column.width}px`,
      paddingInlineStart: `${column.paddingStart ?? 0}px`,
      paddingInlineEnd: `${column.paddingEnd ?? 0}px`,
      justifyContent:
        column.alignment === 'end'
          ? 'flex-end'
          : column.alignment === 'center'
          ? 'center'
          : 'flex-start',
      overflow: 'hidden',
      height: '100%',
    });
    updateCell(view, cell, primitives, index);
    body.appendChild(element);
  });
}
function create(document: Document, row: MarketRow, primitives: RowPrimitives) {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-market-table'
  );
  body.dataset.nlRenderer = 'marketTable';
  Object.assign(body.style, { gap: '0', padding: '0', borderRadius: '12px' });
  views.set(body, { cells: new Map(), primitives });
  bind(body, row, primitives);
  return body;
}
export function updateMarketTableValues(element: HTMLElement, row: MarketRow) {
  const body = element.querySelector<HTMLElement>(
    '.ok-native-list-market-table'
  );
  const state = body ? views.get(body) : undefined;
  if (!body || !state) return;
  row.webTable!.cells.forEach((cell, index) => {
    const view = state.cells.get(cell.key);
    if (view) updateCell(view, cell, state.primitives, index);
  });
  element.setAttribute(
    'aria-label',
    row.accessibilityLabel ?? [row.title, row.price, row.change.text].join(', ')
  );
}
export const marketTableRowRenderer = {
  key: 'marketTable',
  appliesSizePreset: () => false,
  create,
  bind,
  measure: () => 72,
  measureRendered: () => undefined,
  recycle: (body: HTMLElement) => {
    const state = views.get(body);
    state?.cells.forEach((view) => {
      if (view.visual) state.primitives.dispose(view.visual);
      view.hoverVisuals?.forEach((visual) => state.primitives.dispose(visual));
      view.badgeVisuals?.forEach((visual) => state.primitives.dispose(visual));
    });
    state?.cells.clear();
    body.replaceChildren();
  },
};
