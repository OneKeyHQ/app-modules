import type { MarketRow, MarketTextStyle } from '../../models';
import { createElement, setData, markActionAnchorSource } from './RowElements';
import {
  applyTextStyleToSlot,
  applyValueSegments,
  fontWeight,
} from './RowText';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
export type WebMarketLayoutStyle = Readonly<{
  horizontalPadding: number;
  verticalPadding: number;
  leadingGap: number;
  titleBadgeGap: number;
  trailingGap: number;
  imageWidth: number;
  imageHeight: number;
  imageCornerRadius: number;
  changeWidth: number;
  changeHeight: number;
  changeCornerRadius: number;
}>;
export function resolveWebMarketLayoutStyle(
  row: MarketRow
): WebMarketLayoutStyle {
  const style = row.style;
  const imageWidth = style?.image?.width ?? (row.variant === 'stock' ? 40 : 32);
  const imageHeight =
    style?.image?.height ?? (row.variant === 'stock' ? 40 : 32);
  return {
    horizontalPadding:
      style?.horizontalPadding ?? (row.variant === 'perp' ? 16 : 20),
    verticalPadding: style?.verticalPadding ?? 12,
    leadingGap: style?.leadingGap ?? (row.variant === 'perp' ? 8 : 14),
    titleBadgeGap: style?.titleBadgeGap ?? 4,
    trailingGap: style?.trailingGap ?? 8,
    imageWidth,
    imageHeight,
    imageCornerRadius:
      style?.image?.cornerRadius ??
      (style?.image?.shape === 'square'
        ? 0
        : style?.image?.shape === 'rounded'
        ? 8
        : Math.min(imageWidth, imageHeight) / 2),
    changeWidth: style?.changeWidth ?? 80,
    changeHeight: style?.changeHeight ?? 32,
    changeCornerRadius: style?.changeCornerRadius ?? 8,
  };
}
function applyMarketTextStyle(
  element: HTMLElement,
  style: MarketTextStyle | undefined,
  defaults: Readonly<{
    fontSize: number;
    lineHeight: number;
    weight: number;
    alignment: 'start' | 'center' | 'end';
  }>
) {
  element.style.fontSize = String(style?.fontSize ?? defaults.fontSize) + 'px';
  element.style.lineHeight =
    String(style?.lineHeight ?? defaults.lineHeight) + 'px';
  element.style.fontWeight = String(
    fontWeight(style?.fontWeight, defaults.weight)
  );
  element.style.color = style?.color ?? '';
  element.style.textAlign = style?.alignment ?? defaults.alignment;
  element.style.whiteSpace = (style?.lines ?? 1) > 1 ? 'normal' : 'nowrap';
  element.style.display = '';
  element.style.removeProperty('-webkit-line-clamp');
  element.style.removeProperty('-webkit-box-orient');
  if ((style?.lines ?? 1) > 1) {
    element.style.display = '-webkit-box';
    element.style.setProperty('-webkit-line-clamp', String(style?.lines ?? 2));
    element.style.setProperty('-webkit-box-orient', 'vertical');
  }
}
export function setMarketQuoteContent(element: HTMLElement, row: MarketRow) {
  const style = row.style;
  const layoutStyle = resolveWebMarketLayoutStyle(row);
  const price = element.querySelector<HTMLElement>(
    '.ok-native-list-market-price'
  );
  if (price) {
    price.textContent = row.price;
    applyMarketTextStyle(price, style?.price, {
      fontSize: 16,
      lineHeight: 24,
      weight: 500,
      alignment: 'end',
    });
    applyValueSegments(
      price,
      row.priceSegments,
      style?.price?.fontSize ?? 16,
      style?.price?.lineHeight ?? 24,
      fontWeight(style?.price?.fontWeight, 500)
    );
  }
  const change = element.querySelector<HTMLElement>(
    '.ok-native-list-market-change'
  );
  if (change) {
    change.textContent = row.change.text;
    setData(change, 'tone', row.change.tone);
    applyMarketTextStyle(change, style?.change, {
      fontSize: 14,
      lineHeight: 20,
      weight: 500,
      alignment: 'center',
    });
    applyValueSegments(
      change,
      row.change.textSegments,
      style?.change?.fontSize ?? 14,
      style?.change?.lineHeight ?? 20,
      fontWeight(style?.change?.fontWeight, 500)
    );
    change.style.width = String(layoutStyle.changeWidth) + 'px';
    change.style.height = String(layoutStyle.changeHeight) + 'px';
    change.style.borderRadius = String(layoutStyle.changeCornerRadius) + 'px';
    change.style.color = style?.change?.color ?? row.change.textColor ?? '';
    change.style.background = row.change.backgroundColor ?? '';
  }
  if (price) applyTextStyleToSlot(price, style?.price ?? {}, 1);
  if (change) applyTextStyleToSlot(change, style?.change ?? {}, 1);
  element.setAttribute(
    'aria-label',
    row.accessibilityLabel ??
      [row.title, row.subtitle, row.price, row.change.text]
        .filter(Boolean)
        .join(', ')
  );
}
function createMarketRow(
  document: Document,
  primitives: RowPrimitives,
  row: MarketRow
): HTMLElement {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-market'
  );
  setData(body, 'variant', row.variant);
  const style = row.style;
  const layoutStyle = resolveWebMarketLayoutStyle(row);
  body.style.padding =
    String(layoutStyle.verticalPadding) +
    'px ' +
    String(layoutStyle.horizontalPadding) +
    'px';
  body.style.gap = '0px';
  if (row.leadingAction) {
    const action = primitives.iconAction(
      row.leadingAction.name,
      row.leadingAction.actionKey,
      row.leadingAction.disabled,
      row.leadingAction.tintColor
    );
    action.style.flex = '0 0 36px';
    action.style.width = '36px';
    action.style.height = '36px';
    action.style.marginRight = '5px';
    setData(action, 'testid', row.leadingAction.testID);
    if (row.leadingAction.accessibilityLabel)
      action.setAttribute('aria-label', row.leadingAction.accessibilityLabel);
    markActionAnchorSource(action, 'leadingAction');
    body.appendChild(action);
  }
  const visual = primitives.visual(row.leading);
  if (visual) {
    const width = layoutStyle.imageWidth;
    const height = layoutStyle.imageHeight;
    visual.style.width = String(width) + 'px';
    visual.style.height = String(height) + 'px';
    visual.style.flexBasis = String(width) + 'px';
    visual.style.borderRadius = String(layoutStyle.imageCornerRadius) + 'px';
    const borderColor =
      'borderColor' in row.leading ? row.leading.borderColor : undefined;
    if (borderColor) {
      visual.style.border = '1px solid ' + borderColor;
      visual.style.boxSizing = 'border-box';
    }
    visual.querySelectorAll<HTMLElement>('img').forEach((image) => {
      if (!image.classList.contains('ok-native-list-visual-corner')) {
        image.style.width = '100%';
        image.style.height = '100%';
        if (borderColor) {
          image.style.borderRadius = '0';
          image.style.clipPath =
            'inset(-1px round ' + String(layoutStyle.imageCornerRadius) + 'px)';
        }
        image.style.objectFit =
          style?.image?.contentFit === 'center'
            ? 'none'
            : style?.image?.contentFit ?? image.style.objectFit;
      } else {
        image.style.width = '20px';
        image.style.height = '20px';
        if (borderColor) {
          image.style.right = '-5px';
          image.style.bottom = '-5px';
        }
      }
    });
    visual.style.marginInlineEnd = String(layoutStyle.leadingGap) + 'px';
    body.appendChild(visual);
  }
  const main = createElement(document, 'span', 'ok-native-list-market-main');
  main.style.gap = String(style?.lineGap ?? 0) + 'px';
  const titleLine = createElement(
    document,
    'span',
    'ok-native-list-market-title-line'
  );
  titleLine.style.gap = String(layoutStyle.titleBadgeGap) + 'px';
  const title = createElement(
    document,
    'span',
    'ok-native-list-market-title',
    row.title
  );
  applyMarketTextStyle(title, style?.title, {
    fontSize: 16,
    lineHeight: 24,
    weight: 500,
    alignment: 'start',
  });
  titleLine.appendChild(title);
  row.badges?.forEach((badge, slot) => {
    const element = createElement(
      document,
      badge.actionKey ? 'button' : 'span',
      'ok-native-list-market-badge',
      badge.text
    );
    if (badge.actionKey) {
      element.setAttribute('type', 'button');
      setData(element, 'nativeListAction', badge.actionKey);
      markActionAnchorSource(element, 'marketBadge', slot);
    }
    setData(element, 'tone', badge.tone);
    element.style.color =
      badge.textColor ??
      (badge.tone === 'success'
        ? 'var(--nl-positive)'
        : badge.tone === 'danger'
        ? 'var(--nl-negative)'
        : badge.tone === 'info'
        ? 'var(--nl-info)'
        : badge.tone === 'warning'
        ? 'var(--nl-primary)'
        : '');
    element.style.background =
      badge.backgroundColor ??
      ((badge.icon || badge.iconName) && !badge.text ? 'transparent' : '');
    if ((badge.icon || badge.iconName) && !badge.text) {
      element.style.padding = '0';
    }
    if (badge.style) {
      applyTextStyleToSlot(element, badge.style);
      if (badge.style.height !== undefined)
        element.style.height = `${badge.style.height}px`;
      if (badge.style.horizontalPadding !== undefined && badge.text)
        element.style.paddingInline = `${badge.style.horizontalPadding}px`;
    }
    if (badge.accessibilityLabel)
      element.setAttribute('aria-label', badge.accessibilityLabel);
    if (badge.icon) {
      const icon = primitives.thumbnail(badge.icon);
      if (icon) {
        icon.className = '';
        icon.style.borderRadius = '50%';
        element.prepend(icon);
      }
    }
    if (badge.iconName === 'verified') {
      const icon = createElement(
        document,
        'span',
        'ok-native-list-market-badge-icon'
      );
      primitives.icon(icon, 'BadgeVerifiedSolid');
      element.prepend(icon);
    }
    titleLine.appendChild(element);
  });
  main.appendChild(titleLine);
  if (row.subtitlePrefix || row.subtitle || row.subtitleSegments?.length) {
    const subtitleLine = createElement(
      document,
      'span',
      'ok-native-list-market-subtitle-line'
    );
    subtitleLine.style.display = 'flex';
    subtitleLine.style.alignItems = 'center';
    subtitleLine.style.minWidth = '0';
    subtitleLine.style.overflow = 'hidden';
    subtitleLine.style.gap =
      String(row.subtitlePrefix ? row.subtitlePrefix.gap ?? 4 : 0) + 'px';
    if (row.subtitlePrefix) {
      const prefix = createElement(
        document,
        'span',
        'ok-native-list-market-subtitle-prefix',
        row.subtitlePrefix.text
      );
      applyMarketTextStyle(prefix, row.subtitlePrefix.style, {
        fontSize: 12,
        lineHeight: 16,
        weight: 400,
        alignment: 'start',
      });
      prefix.style.display = 'block';
      prefix.style.flex = '1 1 auto';
      prefix.style.minWidth = '0';
      prefix.style.overflow = 'hidden';
      prefix.style.textOverflow = 'ellipsis';
      prefix.style.color =
        row.subtitlePrefix.style?.color ?? 'var(--nl-secondary)';
      if (row.subtitlePrefix.maxWidth !== undefined) {
        prefix.style.maxWidth = String(row.subtitlePrefix.maxWidth) + 'px';
      }
      subtitleLine.appendChild(prefix);
    }
    if (row.subtitle || row.subtitleSegments?.length) {
      const subtitle = createElement(
        document,
        'span',
        'ok-native-list-market-subtitle',
        row.subtitle
      );
      applyMarketTextStyle(subtitle, style?.subtitle, {
        fontSize: 14,
        lineHeight: 20,
        weight: 400,
        alignment: 'start',
      });
      applyValueSegments(
        subtitle,
        row.subtitleSegments,
        style?.subtitle?.fontSize ?? 14,
        style?.subtitle?.lineHeight ?? 20,
        fontWeight(style?.subtitle?.fontWeight, 400)
      );
      subtitle.style.flex = row.subtitlePrefix ? '0 0 auto' : '1 1 auto';
      subtitleLine.appendChild(subtitle);
    }
    main.appendChild(subtitleLine);
  }
  body.appendChild(main);
  const trailing = createElement(
    document,
    'span',
    'ok-native-list-market-trailing'
  );
  trailing.style.gap = String(layoutStyle.trailingGap) + 'px';
  trailing.append(
    createElement(document, 'span', 'ok-native-list-market-price'),
    createElement(document, 'span', 'ok-native-list-market-change')
  );
  body.appendChild(trailing);
  setMarketQuoteContent(body, row);
  for (const key of ['title', 'subtitle'] as const) {
    const text = body.querySelector<HTMLElement>(
      `.ok-native-list-market-${key}`
    );
    if (text) applyTextStyleToSlot(text, style?.[key] ?? {}, 1);
  }
  const prefix = body.querySelector<HTMLElement>(
    '.ok-native-list-market-subtitle-prefix'
  );
  if (prefix) applyTextStyleToSlot(prefix, row.subtitlePrefix?.style ?? {}, 1);
  return body;
}
// A quote payload changes only the quote nodes; full binds retain equal image slots.
type Asset = {
  key: string;
  node: HTMLElement;
  defaults: RowVisualStyle;
};
type Views = { assets: Asset[]; content: string; primitives: RowPrimitives };
const views = new WeakMap<HTMLElement, Views>();
function bind(body: HTMLElement, row: MarketRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  const content = JSON.stringify({
    ...row,
    price: undefined,
    priceSegments: undefined,
    change: undefined,
    accessibilityLabel: undefined,
  });
  if (v.content === content) {
    setMarketQuoteContent(body, row);
    return;
  }
  let index = 0;
  function asset(source: unknown, createAsset: () => HTMLElement | undefined) {
    const slot = index++,
      key = JSON.stringify([row.key, source]);
    let entry = v.assets[slot];
    if (entry?.key !== key) {
      if (entry) primitives.dispose(entry.node);
      const node = createAsset();
      if (!node) return;
      entry = {
        key,
        node,
        defaults: new RowVisualStyle(node),
      };
      v.assets[slot] = entry;
    }
    entry.defaults.restore();
    return entry.node;
  }
  const next = createMarketRow(
    body.ownerDocument,
    {
      ...primitives,
      visual: (source) => asset(source, () => primitives.visual(source)),
      thumbnail: (source) => asset(source, () => primitives.thumbnail(source)),
    },
    row
  );
  v.assets.slice(index).forEach((slot) => primitives.dispose(slot.node));
  v.assets.length = index;
  body.replaceChildren(...Array.from(next.childNodes));
  body.className = next.className;
  body.style.cssText = next.style.cssText;
  body.dataset.variant = row.variant;
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  body.setAttribute('aria-label', next.getAttribute('aria-label') ?? '');
  v.content = content;
}
function create(document: Document, row: MarketRow, primitives: RowPrimitives) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'market';
  views.set(body, { assets: [], content: '', primitives });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  v.assets.forEach((slot) => v.primitives.dispose(slot.node));
  v.assets = [];
  v.content = '';
  body.replaceChildren();
}
export const marketRowRenderer = {
  key: 'market' as const,
  create,
  bind,
  recycle,
  measure: (row: MarketRow, _width: number) => {
    const style = resolveWebMarketLayoutStyle(row);
    return Math.max(
      row.variant === 'stock' ? 72 : 68,
      style.imageHeight + 2 * style.verticalPadding
    );
  },
  measureRendered: (_body: HTMLElement, _row: MarketRow): number | undefined =>
    undefined,
};
