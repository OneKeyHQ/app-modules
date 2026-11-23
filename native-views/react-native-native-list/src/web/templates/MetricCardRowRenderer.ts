import type {
  MetricCardRow,
  NativeListTextStyle,
  TextTone,
} from '../../models';
import {
  captureInlineStyles,
  createElement,
  restoreInlineStyles,
  setData,
  tagSlot,
} from './RowElements';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
const toneColor = (tone: TextTone | undefined, fallback: string) =>
  tone === 'positive'
    ? 'var(--nl-positive)'
    : tone === 'negative'
    ? 'var(--nl-negative)'
    : tone === 'secondary'
    ? 'var(--nl-secondary)'
    : `var(--nl-${fallback})`;
function createBadge(
  document: Document,
  badge: { text: string; tone?: string }
) {
  const label = tagSlot(
    createElement(document, 'span', 'ok-native-list-badge', badge.text),
    'badge'
  );
  label.dataset.tone = badge.tone ?? '';
  return label;
}
function createMetricCell(
  document: Document,
  primitives: RowPrimitives,
  metric: NonNullable<MetricCardRow['metrics']>[number],
  shaded: boolean
): HTMLElement {
  const cell = createElement(document, 'div', 'ok-native-list-composite-cell');
  setData(cell, 'shaded', shaded);
  cell.appendChild(
    createElement(document, 'div', 'ok-native-list-secondary', metric.label)
  );
  const valueLine = createElement(document, 'div');
  valueLine.style.display = 'flex';
  valueLine.style.alignItems = 'center';
  valueLine.style.gap = '6px';
  if (metric.visual) {
    const visual = primitives.visual(metric.visual);
    if (visual) {
      visual.style.width = '16px';
      visual.style.height = '16px';
      visual.style.flexBasis = '16px';
      const image = visual.querySelector('img');
      if (image) {
        image.style.width = '16px';
        image.style.height = '16px';
      }
      valueLine.appendChild(visual);
    }
  }
  const value = createElement(
    document,
    'span',
    'ok-native-list-composite-value',
    metric.value
  );
  value.style.color = toneColor(metric.tone, 'primary');
  valueLine.appendChild(value);
  cell.appendChild(valueLine);
  return cell;
}

function createMetricRow(
  document: Document,
  primitives: RowPrimitives,
  row: MetricCardRow
): HTMLElement {
  if (row.variant === 'activity' || row.variant === 'performance') {
    const body = createElement(
      document,
      'div',
      'ok-native-list-row ok-native-list-composite'
    );
    body.appendChild(
      tagSlot(
        createElement(
          document,
          'div',
          'ok-native-list-composite-heading',
          row.title
        ),
        'title'
      )
    );
    const metrics = row.metrics ?? [];
    const firstLine = createElement(
      document,
      'div',
      'ok-native-list-composite-row'
    );
    metrics
      .slice(0, 2)
      .forEach((metric) =>
        firstLine.appendChild(
          createMetricCell(document, primitives, metric, false)
        )
      );
    body.appendChild(firstLine);
    if (row.variant === 'activity') {
      body.appendChild(
        createElement(document, 'div', 'ok-native-list-divider')
      );
    } else {
      const progress = createElement(
        document,
        'div',
        'ok-native-list-progress'
      );
      const fill = createElement(document, 'span');
      fill.style.width = String(Math.round((row.progress ?? 0) * 100)) + '%';
      progress.appendChild(fill);
      body.appendChild(progress);
    }
    const secondLine = createElement(
      document,
      'div',
      'ok-native-list-composite-row'
    );
    metrics
      .slice(2)
      .forEach((metric) =>
        secondLine.appendChild(
          createMetricCell(
            document,
            primitives,
            metric,
            row.variant === 'performance'
          )
        )
      );
    body.appendChild(secondLine);
    return body;
  }

  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-metric'
  );
  if (row.visual) {
    const visual = primitives.visual(row.visual);
    if (visual) body.appendChild(visual);
  }
  body.appendChild(
    tagSlot(
      createElement(document, 'div', 'ok-native-list-secondary', row.title),
      'title'
    )
  );
  body.appendChild(
    tagSlot(
      createElement(document, 'div', 'ok-native-list-metric-value', row.value),
      'value'
    )
  );
  if (row.trend) {
    const trend = tagSlot(
      createElement(document, 'div', 'ok-native-list-secondary', row.trend),
      'trend'
    );
    trend.style.color =
      row.trendTone === 'positive'
        ? 'var(--nl-positive)'
        : row.trendTone === 'negative'
        ? 'var(--nl-negative)'
        : 'var(--nl-secondary)';
    body.appendChild(trend);
  }
  if (row.subtitle)
    body.appendChild(
      tagSlot(
        createElement(
          document,
          'div',
          'ok-native-list-secondary',
          row.subtitle
        ),
        'subtitle'
      )
    );
  if (row.badge) body.appendChild(createBadge(document, row.badge));
  return body;
}

type VisualSlot = { key: string; node: HTMLElement };
type Views = {
  contentKey: string;
  visuals: VisualSlot[];
  defaults: Map<HTMLElement, string>;
  primitives: RowPrimitives;
  visualStyle?: RowVisualStyle;
};
const views = new WeakMap<HTMLElement, Views>();
function bind(
  body: HTMLElement,
  row: MetricCardRow,
  primitives: RowPrimitives
) {
  const v = views.get(body)!;
  v.primitives = primitives;
  // Text styles are part of the key: applyTextLayout inserts wrappers and
  // rewrites text (lines: 1), which restoring inline styles cannot undo.
  // Rebuilding reuses equal visual slots, so images never restart.
  const contentKey = JSON.stringify({
    ...row,
    style: {
      title: row.style?.title,
      value: row.style?.value,
      trend: row.style?.trend,
      subtitle: row.style?.subtitle,
    },
  });
  if (contentKey !== v.contentKey) {
    let index = 0;
    restoreInlineStyles(v.defaults);
    const next = createMetricRow(
      body.ownerDocument,
      {
        ...primitives,
        visual: (source) => {
          const slot = index++;
          const key = JSON.stringify([
            row.key,
            row.variant ?? 'standard',
            source,
          ]);
          const previous = v.visuals[slot];
          if (previous?.key === key) return previous.node;
          if (previous) primitives.dispose(previous.node);
          const node = primitives.visual(source);
          if (node) v.visuals[slot] = { key, node };
          return node;
        },
      },
      row
    );
    v.visuals.slice(index).forEach((slot) => primitives.dispose(slot.node));
    v.visuals.length = index;
    body.replaceChildren(...Array.from(next.childNodes));
    body.className = next.className;
    body.style.cssText = next.style.cssText;
    // Image opacity is loading state; restoring it would hide loaded images.
    v.defaults = captureInlineStyles(body);
    const leading = body.querySelector<HTMLElement>(
      ':scope > .ok-native-list-visual, :scope > .ok-native-list-stacked'
    );
    v.visualStyle = leading ? new RowVisualStyle(leading) : undefined;
    v.contentKey = contentKey;
  }
  restoreInlineStyles(v.defaults);
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  const composite = row.variant === 'activity' || row.variant === 'performance';
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  if (style?.lineGap !== undefined) body.style.rowGap = `${style.lineGap}px`;
  if (!composite && v.visualStyle) {
    v.visualStyle.apply(
      style?.image,
      style?.image?.width ?? 40,
      style?.image?.height ?? 40,
      0
    );
    const leading = body.querySelector<HTMLElement>(
      ':scope > .ok-native-list-visual, :scope > .ok-native-list-stacked'
    )!;
    leading.style.marginBottom =
      style?.leadingGap === undefined
        ? ''
        : `${style.leadingGap - (style.lineGap ?? 5)}px`;
  }
  const slots: ReadonlyArray<'title' | 'value' | 'trend' | 'subtitle'> =
    composite ? ['title'] : ['title', 'value', 'trend', 'subtitle'];
  slots.forEach((slot) => {
    const text: NativeListTextStyle | undefined = style?.[slot];
    if (text)
      body
        .querySelectorAll<HTMLElement>(`[data-nl-slot="${slot}"]`)
        .forEach((node) => primitives.textStyle(node, text));
  });
}
function create(
  document: Document,
  row: MetricCardRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'metricCard';
  views.set(body, {
    contentKey: '',
    visuals: [],
    defaults: new Map(),
    primitives,
  });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  v.visuals.forEach((slot) => v.primitives.dispose(slot.node));
  v.visuals = [];
  v.defaults.clear();
  v.contentKey = '';
  v.visualStyle = undefined;
  body.replaceChildren();
}
export const metricCardRowRenderer = {
  key: 'metricCard' as const,
  create,
  bind,
  recycle,
  measure: (row: MetricCardRow, _width: number) =>
    row.variant === 'activity'
      ? 161
      : row.variant === 'performance'
      ? 178
      : 132,
  measureRendered: (
    _body: HTMLElement,
    _row: MetricCardRow
  ): number | undefined => undefined,
};
