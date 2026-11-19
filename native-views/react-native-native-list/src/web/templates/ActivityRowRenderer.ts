import { createNamedIcon } from '@onekeyfe/react-native-image/icon/dom';
import type { ActivityAmount, ActivityRow, LeadingVisual } from '../../models';
import { createElement, tagSlot, markActionAnchorSource } from './RowElements';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
import { applyTextWeight, applyValueSegments } from './RowText';
type Views = {
  column: HTMLElement;
  fields: Record<string, HTMLElement>;
  amounts: HTMLElement;
  actions: HTMLElement;
  visuals: HTMLElement[];
  visualStyles: RowVisualStyle[];
  visualKey: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
function bindLegacy(
  body: HTMLElement,
  row: ActivityRow,
  primitives: RowPrimitives
) {
  const v = views.get(body)!;
  v.primitives = primitives;
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const style = row.style;
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  v.column.style.rowGap =
    style?.lineGap !== undefined ? `${style.lineGap}px` : '';
  v.amounts.style.gap =
    style?.trailingGap !== undefined ? `${style.trailingGap}px` : '';
  for (const key of [
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
  ] as const) {
    const node = v.fields[key]!;
    node.style.cssText = '';
    node.textContent = row[key] ?? '';
    node.hidden = !row[key];
    if (style?.[key]) primitives.textStyle(node, style[key]!);
  }
  const visualKey = JSON.stringify([
    row.key,
    row.hideLeading,
    row.leading,
    row.secondaryLeading,
  ]);
  if (v.visualKey !== visualKey) {
    v.visuals.forEach((node) => {
      primitives.dispose(node);
      node.remove();
    });
    v.visuals = (row.hideLeading ? [] : [row.leading, row.secondaryLeading])
      .map((source) => primitives.visual(source))
      .filter((node): node is HTMLElement => !!node);
    v.visuals.forEach((node) => body.insertBefore(node, v.column));
    v.visualStyles = v.visuals.map((node) => new RowVisualStyle(node));
    v.visualKey = visualKey;
  }
  if (v.visualStyles[0])
    v.visualStyles[0].apply(
      style?.image,
      style?.image?.width ?? 40,
      style?.image?.height ?? 40,
      (style?.leadingGap ?? 12) - 12
    );
  v.actions.replaceChildren();
  v.actions.hidden = !row.footerActions?.length;
  row.footerActions?.forEach((action, index) => {
    const button = createElement(
      body.ownerDocument,
      'button',
      'ok-native-list-action-button',
      action.label
    );
    button.setAttribute('type', 'button');
    button.toggleAttribute('disabled', Boolean(action.disabled));
    button.dataset.tone = action.tone ?? '';
    button.dataset.nativeListAction = action.key;
    markActionAnchorSource(button, 'footerAction', index);
    v.actions.appendChild(button);
  });
}
function createLegacy(
  document: Document,
  row: ActivityRow,
  primitives: RowPrimitives
) {
  const body = createElement(
    document,
    'div',
    'ok-native-list-row ok-native-list-standard'
  );
  body.dataset.nlRenderer = 'activity';
  const column = createElement(document, 'span', 'ok-native-list-flex');
  const amounts = createElement(document, 'span', 'ok-native-list-amounts');
  const actions = createElement(document, 'span', 'ok-native-list-actions');
  const fields: Record<string, HTMLElement> = {};
  for (const key of [
    'title',
    'description',
    'status',
    'primaryAmount',
    'secondaryAmount',
  ]) {
    const node = tagSlot(
      createElement(
        document,
        'span',
        key === 'title'
          ? 'ok-native-list-title'
          : key === 'primaryAmount'
          ? 'ok-native-list-value'
          : 'ok-native-list-secondary'
      ),
      key
    );
    fields[key] = node;
    (key.endsWith('Amount') ? amounts : column).appendChild(node);
  }
  column.appendChild(actions);
  body.append(column, amounts);
  views.set(body, {
    column,
    amounts,
    actions,
    fields,
    visuals: [],
    visualStyles: [],
    visualKey: '',
    primitives,
  });
  bindLegacy(body, row, primitives);
  return body;
}
function recycleLegacy(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  v.visuals.forEach((node) => {
    v.primitives.dispose(node);
    node.remove();
  });
  v.visuals = [];
  v.visualStyles = [];
  v.visualKey = '';
  v.actions.replaceChildren();
}
type AmountViews = {
  element: HTMLElement;
  primary: HTMLElement;
  secondary: HTMLElement;
  visual?: HTMLElement;
  visualKey: string;
};
type HistoryViews = {
  content: HTMLElement;
  identity: HTMLElement;
  visual: HTMLElement;
  text: HTMLElement;
  titleLine: HTMLElement;
  title: HTMLElement;
  badges: HTMLElement;
  description: HTMLElement;
  /** Description text before the action, or nothing. */
  descriptionPrefix: HTMLElement;
  /** Description text that opens the action; the whole text without one. */
  descriptionAction: HTMLButtonElement;
  amounts: HTMLElement;
  amountRows: Map<string, AmountViews>;
  fee: HTMLElement;
  feeLabel: HTMLElement;
  feeValues: HTMLElement;
  feePrimary: HTMLElement;
  feeSecondary: HTMLElement;
  actions: HTMLElement;
  actionButtons: Map<string, HTMLButtonElement>;
  visualKey: string;
  visuals: {
    node: HTMLElement;
    style: RowVisualStyle;
    background: string;
    loaded: boolean;
    fallback?: HTMLElement;
    fallbackStyle?: RowVisualStyle;
  }[];
  primitives: RowPrimitives;
};
const histories = new WeakMap<HTMLElement, HistoryViews>();
const isHistory = (row: ActivityRow) =>
  row.presentation !== undefined || row.amounts !== undefined;
const ellipsis =
  'min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;';
/** Width a description prefix always leaves for the action text after it. */
const descriptionActionReserve = '3em';
function valueText(
  node: HTMLElement,
  text: string | undefined,
  segments: ActivityAmount['textSegments'],
  size: number,
  line: number,
  weight: number
) {
  node.textContent = text ?? '';
  applyTextWeight(node, weight);
  applyValueSegments(node, segments, size, line, weight);
}
function createHistory(body: HTMLElement, primitives: RowPrimitives) {
  const document = body.ownerDocument;
  const node = (name: string) =>
    createElement(document, 'div', `ok-native-list-history-${name}`);
  const content = node('content');
  const identity = node('identity');
  const visual = node('visual');
  const text = node('text');
  const titleLine = node('title-line');
  const title = tagSlot(node('title'), 'title');
  const badges = node('badges');
  const description = node('description');
  description.dataset.nlSlot = 'description';
  const descriptionPrefix = createElement(document, 'span');
  const descriptionAction = createElement(
    document,
    'button'
  ) as HTMLButtonElement;
  descriptionAction.type = 'button';
  description.append(descriptionPrefix, descriptionAction);
  const amounts = node('amounts');
  const fee = node('fee');
  const feeLabel = node('fee-label');
  const feeValues = node('fee-values');
  const feePrimary = node('fee-primary');
  const feeSecondary = node('fee-secondary');
  const actions = node('actions');
  titleLine.append(title, badges);
  text.append(titleLine, description);
  identity.append(visual, text);
  feeValues.append(feePrimary, feeSecondary);
  fee.append(feeLabel, feeValues);
  content.append(identity, amounts, fee);
  body.replaceChildren(content, actions);
  const v: HistoryViews = {
    content,
    identity,
    visual,
    text,
    titleLine,
    title,
    badges,
    description,
    descriptionPrefix,
    descriptionAction,
    amounts,
    amountRows: new Map(),
    fee,
    feeLabel,
    feeValues,
    feePrimary,
    feeSecondary,
    actions,
    actionButtons: new Map(),
    visualKey: '',
    visuals: [],
    primitives,
  };
  histories.set(body, v);
  return v;
}
const historyIconNames = new Set([
  'ArrowTopOutline',
  'ArrowBottomOutline',
  'SwitchHorOutline',
  'CoinsOutline',
  'ShieldCheckDoneOutline',
  'UnlockedOutline',
  'Document2Outline',
  'CryptoCoinOutline',
  'ImageSquareWavesOutline',
  'DotHorOutline',
]);
function historyIcon(
  node: HTMLElement,
  source: LeadingVisual | undefined,
  size: number
) {
  const name =
    source?.kind === 'icon'
      ? source.name
      : source?.kind === 'token'
      ? source.fallbackIcon?.name
      : undefined;
  if (!name || !historyIconNames.has(name)) return;
  const placeholder = node.querySelector<HTMLElement>(
    '.ok-native-list-visual-fallback'
  );
  if (!placeholder && source && 'image' in source && source.image) return;
  const fallback = placeholder ?? node;
  Object.assign(fallback.style, {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
    height: '100%',
  });
  if (source?.kind === 'icon' && source.tintColor)
    fallback.style.color = source.tintColor;
  else if (source && 'fallbackIcon' in source && source.fallbackIcon?.tintColor)
    fallback.style.color = source.fallbackIcon.tintColor;
  const svg = createNamedIcon(node.ownerDocument, name);
  svg.setAttribute('width', String(size));
  svg.setAttribute('height', String(size));
  fallback.replaceChildren(svg);
}
function bindHistory(
  body: HTMLElement,
  row: ActivityRow,
  primitives: RowPrimitives
) {
  const v = histories.get(body) ?? createHistory(body, primitives);
  v.primitives = primitives;
  const table = row.presentation === 'table';
  const footer = Boolean(row.footerActions?.length);
  body.className = 'ok-native-list-row ok-native-list-history';
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  Object.assign(body.style, {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'stretch',
    justifyContent: 'flex-start',
    width: 'calc(100% - 16px)',
    margin: '0 8px',
    padding: `${table ? 12 : 8}px 12px`,
    height:
      !table && !footer && row.height !== 60 ? 'calc(100% - 8px)' : '100%',
    borderRadius: '12px',
    gap: '8px',
  });
  Object.assign(v.content.style, {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    minWidth: '0',
    width: '100%',
  });
  Object.assign(v.identity.style, {
    display: 'flex',
    alignItems: 'center',
    gap: row.hideLeading ? '0' : '12px',
    minWidth: '0',
    flex: '1 1 0',
  });
  Object.assign(v.visual.style, {
    display: row.hideLeading ? 'none' : '',
    position: 'relative',
    width: '40px',
    height: '40px',
    flex: '0 0 40px',
  });
  const visualKey = JSON.stringify([
    row.key,
    row.hideLeading,
    row.leading,
    row.secondaryLeading,
  ]);
  if (v.visualKey !== visualKey) {
    Array.from(v.visual.children).forEach((n) =>
      primitives.dispose(n as HTMLElement)
    );
    v.visual.replaceChildren();
    v.visuals = [];
    const sources = row.hideLeading
      ? []
      : [row.leading, row.secondaryLeading].filter(Boolean);
    sources.forEach((source) => {
      const visual = primitives.visual(source);
      if (!visual) return;
      historyIcon(visual, source, table || sources.length > 1 ? 24 : 28);
      const retained = {
        node: visual,
        style: new RowVisualStyle(visual),
        background: visual.style.background,
        loaded: false,
      };
      // Only placeholder-backed sources restore their frame background on load.
      const image = source && 'image' in source ? source.image : undefined;
      if (image?.loadingStrategy && image.loadingStrategy !== 'none') {
        visual
          .querySelector('.ok-native-list-visual-main')
          ?.addEventListener('load', () => {
            retained.background =
              source && 'backgroundColor' in source && source.backgroundColor
                ? source.backgroundColor
                : 'transparent';
            retained.loaded = true;
          });
      }
      v.visuals.push(retained);
      v.visual.append(visual);
    });
    v.visualKey = visualKey;
  }
  const sources = row.hideLeading
    ? []
    : [row.leading, row.secondaryLeading].filter(Boolean);
  v.visuals.forEach((retained, index) => {
    const { node: visual, style, background, loaded } = retained;
    const source = sources[index];
    const previousToken = visual.querySelector<HTMLElement>(
      ':scope > .ok-native-list-history-token'
    );
    if (previousToken) {
      visual.append(...Array.from(previousToken.children));
      previousToken.remove();
    }
    const currentFallback = visual.querySelector<HTMLElement>(
      ':scope > .ok-native-list-visual-fallback:not(.ok-native-list-visual-corner)'
    );
    if (currentFallback && retained.fallback !== currentFallback) {
      retained.fallback = currentFallback;
      retained.fallbackStyle = new RowVisualStyle(currentFallback);
    }
    // A terminal image error creates a new fallback node after the factory bind.
    retained.fallbackStyle?.restore();
    const size = sources.length > 1 ? (index ? 28 : 24) : 40;
    style.apply(row.style?.image, size, size, 0);
    Object.assign(visual.style, {
      background,
      position: '',
      left: '',
      top: '',
    });
    const svg = visual.querySelector<SVGElement>(
      '.ok-native-list-visual-fallback > svg'
    );
    if (svg) {
      const iconSize = String(table || sources.length > 1 ? 24 : 28);
      svg.setAttribute('width', iconSize);
      svg.setAttribute('height', iconSize);
    }
    Object.assign(visual.style, {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
    });
    if (sources.length > 1)
      Object.assign(visual.style, {
        position: 'absolute',
        left: index ? '12px' : '0',
        top: index ? '12px' : '0',
        border: index ? '2px solid var(--nl-row)' : '0',
        boxSizing: 'border-box',
      });
    if (!table && sources.length > 1) {
      const token = visual.ownerDocument.createElement('span');
      token.className = 'ok-native-list-history-token';
      Object.assign(token.style, {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        width: '24px',
        height: '24px',
        flex: '0 0 24px',
        borderRadius: 'inherit',
        boxSizing: 'border-box',
        overflow: 'hidden',
        background:
          source && 'backgroundColor' in source
            ? source.backgroundColor ?? ''
            : '',
        border:
          source && 'borderColor' in source && source.borderColor
            ? `1px solid ${source.borderColor}`
            : '0',
      });
      token.append(
        ...Array.from(visual.children).filter(
          (child) => !child.classList.contains('ok-native-list-visual-corner')
        )
      );
      const fallback = token.querySelector<HTMLElement>(
        ':scope > .ok-native-list-visual-fallback'
      );
      if (fallback) {
        fallback.style.width = '24px';
        fallback.style.height = '24px';
        fallback.style.flexShrink = '0';
      }
      const mainImage = token.querySelector<HTMLImageElement>(
        ':scope > .ok-native-list-visual-main'
      );
      if (mainImage) {
        mainImage.style.width = '100%';
        mainImage.style.height = '100%';
      }
      visual.append(token);
    }
    if (table) {
      visual.style.background = loaded
        ? background
        : source && 'backgroundColor' in source && source.backgroundColor
        ? source.backgroundColor
        : 'var(--nl-strong)';
      const borderColor =
        source && 'borderColor' in source && source.borderColor
          ? source.borderColor
          : 'var(--nl-separator)';
      visual.style.border = `1px solid ${borderColor}`;
      visual.style.boxSizing = 'border-box';
    } else if (sources.length === 1 && source?.kind === 'token') {
      visual.style.border = source.borderColor
        ? `1px solid ${source.borderColor}`
        : '0';
      visual.style.boxSizing = 'border-box';
      const mainImage = visual.querySelector<HTMLImageElement>(
        ':scope > .ok-native-list-visual-main'
      );
      if (mainImage) {
        mainImage.style.width = '100%';
        mainImage.style.height = '100%';
      }
    }
  });
  Object.assign(v.text.style, {
    minWidth: '0',
    flex: '1 1 auto',
    display: 'flex',
    flexDirection: 'column',
    gap: table ? '4px' : '0',
  });
  Object.assign(v.titleLine.style, {
    display: 'flex',
    alignItems: 'center',
    minWidth: '0',
  });
  v.title.style.cssText =
    ellipsis +
    `font-size:${table ? 14 : 16}px;line-height:${
      table ? 20 : 24
    }px;font-weight:500;`;
  v.title.textContent = row.title;
  applyTextWeight(v.title, 500);
  if (row.style?.title) primitives.textStyle(v.title, row.style.title);
  v.badges.style.cssText = 'display:flex;flex:0 0 auto;gap:8px;margin-left:8px';
  v.badges.replaceChildren();
  for (const badge of row.badges ?? []) {
    const element = createElement(
      body.ownerDocument,
      'span',
      'ok-native-list-badge',
      badge.text
    );
    element.dataset.tone = badge.tone ?? '';
    element.style.cssText =
      'height:20px;font-size:12px;line-height:16px;padding:2px 8px;border-radius:4px;box-sizing:border-box';
    applyTextWeight(element, 500);
    if (badge.tone === 'neutral')
      Object.assign(element.style, {
        background: 'var(--nl-strong)',
        color: 'var(--nl-secondary)',
      });
    if (badge.tone === 'warning')
      Object.assign(element.style, {
        background:
          'var(--nl-caution-background,color-mix(in srgb,var(--nl-caution) 12%,transparent))',
        color: 'var(--nl-caution)',
      });
    v.badges.append(element);
  }
  v.badges.style.display = row.badges?.length ? 'flex' : 'none';
  v.description.style.cssText =
    ellipsis +
    'display:flex;max-width:100%;color:var(--nl-secondary);font-family:inherit;font-size:14px;line-height:20px;font-weight:400;font-variant-numeric:tabular-nums;';
  applyTextWeight(v.description, 400);
  // The caller states where the action text starts; the string is not parsed.
  const description = row.description ?? '';
  const actionStart = row.descriptionActionKey
    ? Math.min(Math.max(row.descriptionActionStart ?? 0, 0), description.length)
    : 0;
  const actionText = description.slice(actionStart);
  const boundAction =
    row.descriptionActionKey && actionText ? row.descriptionActionKey : '';
  // `pre` keeps the space a prefix ends with; a line break stays a space.
  v.descriptionPrefix.textContent = description
    .slice(0, actionStart)
    .replace(/\r\n|[\r\n]/g, ' ');
  // The prefix keeps its width and the action truncates first, as the tail of
  // the one native label does. A prefix wider than the row is capped instead,
  // or it would squeeze the action to nothing and leave no pointer target.
  v.descriptionPrefix.style.cssText = `${ellipsis}flex-shrink:0;max-width:${
    actionText ? `calc(100% - ${descriptionActionReserve})` : '100%'
  };white-space:pre;display:${actionStart > 0 ? 'block' : 'none'}`;
  v.descriptionAction.style.cssText = `${ellipsis}appearance:none;max-width:100%;border:0;padding:0;background:transparent;text-align:left;color:inherit;font:inherit;font-variant-numeric:inherit;display:${
    actionText ? 'block' : 'none'
  };`;
  v.descriptionAction.textContent = actionText;
  v.description.style.display = row.description ? 'flex' : 'none';
  v.descriptionAction.tabIndex = boundAction ? 0 : -1;
  if (boundAction) {
    v.descriptionAction.dataset.nativeListAction = boundAction;
    markActionAnchorSource(v.descriptionAction, 'description');
    v.descriptionAction.style.cursor = 'pointer';
  } else {
    delete v.descriptionAction.dataset.nativeListAction;
    delete v.descriptionAction.dataset.nativeListAnchorSource;
    v.descriptionAction.style.cursor = 'inherit';
  }
  if (row.style?.description)
    primitives.textStyle(v.description, row.style.description);
  v.description.style.display = row.description ? 'flex' : 'none';
  Object.assign(v.amounts.style, {
    display: 'flex',
    flexDirection: 'column',
    minWidth: '0',
    gap: table ? '4px' : '0',
    flex: table ? '1 1 0' : '0 1 auto',
    maxWidth: table ? '50%' : '50%',
    alignItems: table ? 'stretch' : 'flex-end',
    alignSelf: table ? 'stretch' : 'center',
  });
  const amountKeys = new Set<string>();
  for (const amount of row.amounts ?? []) {
    amountKeys.add(amount.key);
    let view = v.amountRows.get(amount.key);
    if (!view) {
      const element = createElement(body.ownerDocument, 'div');
      const primary = tagSlot(
        createElement(body.ownerDocument, 'span'),
        'primaryAmount'
      );
      const secondary = tagSlot(
        createElement(body.ownerDocument, 'span'),
        'secondaryAmount'
      );
      element.append(primary, secondary);
      view = { element, primary, secondary, visualKey: '' };
      v.amountRows.set(amount.key, view);
    }
    view.element.style.cssText = `display:flex;align-items:center;gap:4px;min-width:0;max-width:100%;${
      table ? '' : 'justify-content:flex-end;'
    }`;
    const secondary = amount.tone === 'secondary';
    const size = table || secondary ? 14 : 16;
    const line = table || secondary ? 20 : 24;
    const weight = table || secondary ? 400 : 500;
    view.primary.style.cssText =
      ellipsis +
      `font-size:${size}px;line-height:${line}px;font-weight:${weight};color:${
        amount.tone === 'positive'
          ? 'var(--nl-positive)'
          : amount.tone === 'negative'
          ? 'var(--nl-negative)'
          : secondary
          ? 'var(--nl-secondary)'
          : 'var(--nl-primary)'
      };`;
    valueText(
      view.primary,
      amount.text,
      amount.textSegments,
      size,
      line,
      weight
    );
    const roleStyle = secondary
      ? row.style?.secondaryAmount
      : row.style?.primaryAmount;
    if (roleStyle) primitives.textStyle(view.primary, roleStyle);
    view.secondary.style.cssText =
      ellipsis +
      'font-size:14px;line-height:20px;font-weight:400;color:var(--nl-secondary);';
    valueText(
      view.secondary,
      amount.secondaryText,
      amount.secondaryTextSegments,
      14,
      20,
      400
    );
    view.secondary.hidden = !table || !amount.secondaryText;
    const amountVisualKey = JSON.stringify(table ? amount.leading : undefined);
    if (view.visualKey !== amountVisualKey) {
      if (view.visual) {
        primitives.dispose(view.visual);
        view.visual.remove();
      }
      view.visual = table ? primitives.visual(amount.leading) : undefined;
      if (view.visual) {
        new RowVisualStyle(view.visual).apply(
          { width: 20, height: 20 },
          20,
          20,
          0
        );
        historyIcon(view.visual, amount.leading, 16);
        if (amount.leading?.kind === 'token' && amount.leading.image) {
          view.visual.style.border = amount.leading.borderColor
            ? `1px solid ${amount.leading.borderColor}`
            : '0';
          view.visual.style.boxSizing = 'border-box';
        }
        view.element.prepend(view.visual);
      }
      view.visualKey = amountVisualKey;
    }
    v.amounts.append(view.element);
  }
  for (const [key, view] of v.amountRows) {
    if (!amountKeys.has(key)) {
      if (view.visual) primitives.dispose(view.visual);
      view.element.remove();
      v.amountRows.delete(key);
    }
  }
  v.fee.style.cssText =
    'display:flex;flex-direction:column;flex:.6 1 0;min-width:0;gap:4px';
  v.fee.hidden = !table || !row.fee;
  v.fee.style.display = table && row.fee ? 'flex' : 'none';
  v.fee.style.opacity = row.fee?.hidden ? '0' : '1';
  v.feeLabel.style.cssText =
    'font-size:14px;line-height:20px;color:var(--nl-secondary);';
  v.feeLabel.textContent = row.fee?.label ?? '';
  applyTextWeight(v.feeLabel, 400);
  v.feeValues.style.cssText =
    'display:flex;align-items:center;gap:4px;min-width:0';
  v.feePrimary.style.cssText =
    ellipsis + 'font-size:14px;line-height:20px;font-weight:400;';
  v.feeSecondary.style.cssText =
    ellipsis +
    'font-size:14px;line-height:20px;font-weight:400;color:var(--nl-secondary);';
  valueText(
    v.feePrimary,
    row.fee?.primary,
    row.fee?.primaryTextSegments,
    14,
    20,
    400
  );
  valueText(
    v.feeSecondary,
    row.fee?.secondary,
    row.fee?.secondaryTextSegments,
    14,
    20,
    400
  );
  v.actions.style.cssText = `display:flex;gap:12px;padding-left:${
    row.hideLeading ? 0 : 52
  }px;padding-bottom:4px;`;
  v.actions.style.display = footer ? 'flex' : 'none';
  const actionKeys = new Set<string>();
  for (const [index, action] of (row.footerActions ?? []).entries()) {
    actionKeys.add(action.key);
    let button = v.actionButtons.get(action.key);
    if (!button) {
      button = createElement(
        body.ownerDocument,
        'button',
        'ok-native-list-action-button'
      ) as HTMLButtonElement;
      button.type = 'button';
      v.actionButtons.set(action.key, button);
    }
    if (button.textContent !== action.label) button.textContent = action.label;
    button.toggleAttribute('disabled', Boolean(action.disabled));
    button.dataset.nativeListAction = action.key;
    markActionAnchorSource(button, 'footerAction', index);
    button.style.cssText =
      'font-family:inherit;font-size:14px;line-height:20px;font-weight:500;padding:4px 10px;border:1px solid transparent;border-radius:8px;background:var(--nl-strong);color:var(--nl-primary);white-space:nowrap;';
    applyTextWeight(button, 500);
    if (action.tone === 'primary')
      Object.assign(button.style, {
        background: 'var(--nl-primary)',
        color: 'var(--nl-inverse-text)',
      });
    if (v.actions.children[index] !== button)
      v.actions.insertBefore(button, v.actions.children[index] ?? null);
  }
  for (const [key, button] of v.actionButtons) {
    if (!actionKeys.has(key)) {
      button.remove();
      v.actionButtons.delete(key);
    }
  }
}
function recycle(body: HTMLElement) {
  const v = histories.get(body);
  if (v) {
    Array.from(v.visual.children).forEach((n) =>
      v.primitives.dispose(n as HTMLElement)
    );
    for (const view of v.amountRows.values())
      if (view.visual) v.primitives.dispose(view.visual);
    histories.delete(body);
  }
  recycleLegacy(body);
  views.delete(body);
}
function bind(body: HTMLElement, row: ActivityRow, primitives: RowPrimitives) {
  if (isHistory(row)) {
    if (views.has(body)) {
      recycleLegacy(body);
      views.delete(body);
    }
    bindHistory(body, row, primitives);
  } else {
    if (histories.has(body)) recycle(body);
    if (!views.has(body)) {
      const initial = createLegacy(body.ownerDocument, row, primitives);
      body.replaceChildren(...Array.from(initial.childNodes));
      body.className = initial.className;
      views.set(body, views.get(initial)!);
      views.delete(initial);
    }
    bindLegacy(body, row, primitives);
  }
}
function create(
  document: Document,
  row: ActivityRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'activity';
  bind(body, row, primitives);
  return body;
}
export const activityRowRenderer = {
  key: 'activity' as const,
  create,
  bind,
  recycle,
  measure: (row: ActivityRow, _width: number) => {
    if (!isHistory(row)) return row.footerActions?.length ? 100 : 60;
    const extra =
      row.presentation === 'table'
        ? Math.max(0, (row.amounts?.length ?? 0) - 2) * 24
        : 0;
    if (row.footerActions?.length)
      return (row.presentation === 'table' ? 68 : 60) + extra + 42;
    return 68 + extra;
  },
  measureRendered: (
    _body: HTMLElement,
    _row: ActivityRow
  ): number | undefined => undefined,
};
