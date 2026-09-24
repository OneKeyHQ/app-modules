import { applyTabularNumbers, hasExplicitRowHeight } from './RowElements';
import type {
  IdentityRow,
  NativeListTextStyle,
  RowBoxStyle,
  TrailingAccessory,
  TextTone,
} from '../../models';
import {
  createElement,
  setData,
  tagSlot,
  markActionAnchorSource,
  createTextColumn,
  createBadge,
} from './RowElements';
import {
  applyTextLayout,
  applyTextStyleToSlot,
  applyValueSegments,
} from './RowText';
import { RowVisualStyle, type RowPrimitives } from './RowVisual';
function toneColor(
  tone: TextTone | undefined,
  fallback: 'primary' | 'secondary'
): string {
  if (tone === 'positive') return 'var(--nl-positive)';
  if (tone === 'negative') return 'var(--nl-negative)';
  if (tone === 'secondary') return 'var(--nl-secondary)';
  return fallback === 'secondary' ? 'var(--nl-secondary)' : 'var(--nl-primary)';
}
function appendAccessories(
  parent: HTMLElement,
  document: Document,
  primitives: RowPrimitives,
  row: IdentityRow,
  accessories: readonly TrailingAccessory[] | undefined
) {
  if (!accessories?.length) return;
  const container = createElement(
    document,
    'span',
    'ok-native-list-accessories'
  );

  if (
    row &&
    hasExplicitRowHeight(row) &&
    'presentation' in row &&
    row.presentation === 'networkSelector'
  ) {
    container.style.gap = accessories.some(
      (accessory) => accessory.kind === 'checkbox'
    )
      ? '12px'
      : '20px';
  }
  if (
    row &&
    hasExplicitRowHeight(row) &&
    'presentation' in row &&
    row.presentation === 'accountSelector' &&
    accessories.length === 1 &&
    accessories[0]?.kind === 'icon' &&
    accessories[0].name === 'PlusSmallOutline'
  ) {
    setData(container, 'nativeListAccountControl', 'createAddress');
  }
  let valueIndex = 0;
  accessories.forEach((accessory, slot) => {
    const element = primitives.accessory(row.key, accessory, slot);
    if (accessory.kind === 'value' || accessory.kind === 'valuePair') {
      tagSlot(element, valueIndex++ === 0 ? 'value' : 'valueSecondary');
    }
    container.appendChild(element);
  });
  parent.appendChild(container);
}
function createIdentityRow(
  document: Document,
  primitives: RowPrimitives,
  row: IdentityRow
): HTMLElement {
  const presentation = row.presentation;
  const body = createElement(
    document,
    'div',
    [
      'ok-native-list-row',
      'ok-native-list-standard',
      !presentation ? 'ok-native-list-identity-row' : '',
      presentation === 'networkSelector' ? 'ok-native-list-network-row' : '',
      presentation === 'walletSidebar' ? 'ok-native-list-wallet-row' : '',
      presentation === 'accountSelector' ? 'ok-native-list-account-row' : '',
    ]
      .filter(Boolean)
      .join(' ')
  );
  setData(
    body,
    'nativeListSelector',
    hasExplicitRowHeight(row) ? presentation : undefined
  );
  if (row.titleActionKey && row.titleActionOnHover) {
    setData(body, 'nativeListHoverAction', row.titleActionKey);
    markActionAnchorSource(body, 'leadingAction');
  }
  if (row.leadingAction) {
    const action = primitives.iconAction(
      row.leadingAction.name,
      row.leadingAction.actionKey,
      row.leadingAction.disabled,
      row.leadingAction.tintColor
    );
    markActionAnchorSource(action, 'leadingAction');
    body.appendChild(action);
  }
  const visual = primitives.visual(
    row.leading,
    hasExplicitRowHeight(row) ? presentation : undefined
  );
  if (
    visual &&
    true &&
    hasExplicitRowHeight(row) &&
    row.presentation === 'walletSidebar' &&
    'fallbackIcon' in row.leading &&
    row.leading.fallbackIcon?.name === 'LockSolid'
  ) {
    // OneKey patch: hidden-wallet locks use WalletAvatar's full 40-point icon.
    const icon = visual.querySelector<SVGElement>(
      '.ok-native-list-visual-fallback svg'
    );
    if (icon) {
      icon.style.width = '40px';
      icon.style.height = '40px';
    }
    const fallback = visual.querySelector<HTMLElement>(
      '.ok-native-list-visual-fallback'
    );
    if (fallback) {
      fallback.style.borderRadius = '0';
      fallback.style.overflow = 'visible';
    }
  }
  if (
    visual &&
    true &&
    hasExplicitRowHeight(row) &&
    row.presentation === 'walletSidebar' &&
    'borderStyle' in row.leading &&
    row.leading.borderStyle === 'dashed'
  )
    visual.style.borderWidth = '1px';
  if (visual) body.appendChild(visual);
  const title = row.title;
  const subtitle = row.subtitle;
  const column = createTextColumn(
    document,
    title,
    subtitle,
    row.tertiary,
    row.tertiaryTone,
    presentation !== 'walletSidebar' ? row.badges : undefined,
    {
      title: 'title',
      subtitle: 'subtitle',
      tertiary: 'tertiary',
    }
  );
  {
    const titleElement = column.firstElementChild as HTMLElement;
    if (row.titleMatch?.length) {
      const textTarget =
        titleElement.querySelector<HTMLElement>('[data-nl-slot="title"]') ??
        titleElement;
      const firstText = textTarget.firstChild;
      if (firstText) firstText.remove();
      const fragment = document.createDocumentFragment();
      let offset = 0;
      row.titleMatch.forEach(({ start, end }) => {
        fragment.appendChild(
          document.createTextNode(row.title.slice(offset, start))
        );
        const match = createElement(
          document,
          'span',
          'ok-native-list-info',
          row.title.slice(start, end)
        );
        fragment.appendChild(match);
        offset = end;
      });
      fragment.appendChild(document.createTextNode(row.title.slice(offset)));
      textTarget.prepend(fragment);
    }
    if (row.subtitleSegments?.length) {
      column.querySelector('.ok-native-list-secondary')?.remove();
      const segments = createElement(
        document,
        'span',
        'ok-native-list-subtitle-segments'
      );
      row.subtitleSegments.forEach((segment) => {
        if (segment.separatorBefore)
          segments.appendChild(
            createElement(document, 'span', 'ok-native-list-subtitle-dot')
          );
        const text = createElement(
          document,
          'span',
          'ok-native-list-secondary',
          segment.text
        );
        tagSlot(text, 'subtitle');
        applyValueSegments(text, segment.textSegments, 14, 20, 400);
        setData(text, 'tone', segment.tone);
        text.style.color =
          segment.tone === 'disabled'
            ? 'var(--nl-disabled)'
            : segment.tone === 'caution'
            ? 'var(--nl-caution)'
            : toneColor(segment.tone, 'secondary');
        segments.appendChild(text);
      });
      column.insertBefore(segments, titleElement.nextSibling);
    }
    if (presentation === 'walletSidebar' && row.badges?.length) {
      const badges = createElement(
        document,
        'span',
        'ok-native-list-wallet-badges'
      );
      row.badges.forEach((badge) =>
        badges.appendChild(createBadge(document, badge))
      );
      column.appendChild(badges);
    }
    for (const key of ['title', 'subtitle'] as const) {
      const lines = row.style?.[key]?.lines ?? row[`${key}Lines`];
      if (lines !== undefined)
        column
          .querySelectorAll<HTMLElement>(`[data-nl-slot="${key}"]`)
          .forEach((text) => applyTextLayout(text, { lines }));
    }
  }
  body.appendChild(column);
  appendAccessories(body, document, primitives, row, row.trailing);
  return body;
}
const ROW_BOX_STYLE_KEYS: ReadonlySet<string> = new Set([
  'container',
  'horizontalPadding',
  'verticalPadding',
  'leadingGap',
  'lineGap',
  'titleBadgeGap',
  'trailingGap',
  'image',
]);
function applyIdentityStyle(body: HTMLElement, row: IdentityRow) {
  const style = (row as { style?: Record<string, unknown> }).style;
  if (!style) return;
  const box = style as RowBoxStyle;
  if (box.horizontalPadding !== undefined)
    body.style.paddingInline = String(box.horizontalPadding) + 'px';
  if (box.verticalPadding !== undefined)
    body.style.paddingBlock = String(box.verticalPadding) + 'px';
  const vertical =
    row.type === 'identity' && row.presentation === 'walletSidebar';
  const leading = body.querySelector<HTMLElement>(
    ':scope > .ok-native-list-visual, :scope > .ok-native-list-stacked'
  );
  const defaultGap =
    body.style.gap ||
    (row.type === 'identity' && row.presentation === 'walletSidebar'
      ? '4px'
      : row.type === 'identity' && row.presentation === 'accountSelector'
      ? '8px'
      : '12px');
  if (box.lineGap !== undefined) {
    const targets = body.querySelectorAll<HTMLElement>(
      ':scope > .ok-native-list-flex'
    );
    targets.forEach((column) => {
      column.style.rowGap = String(box.lineGap) + 'px';
    });
  }
  if (leading && box.leadingGap !== undefined) {
    const gap = 'calc(' + String(box.leadingGap) + 'px - ' + defaultGap + ')';
    if (vertical) leading.style.marginBottom = gap;
    else leading.style.marginInlineEnd = gap;
  }
  if (box.titleBadgeGap !== undefined) {
    const gap = String(box.titleBadgeGap) + 'px';
    body
      .querySelectorAll<HTMLElement>(
        '.ok-native-list-badges, .ok-native-list-wallet-badges'
      )
      .forEach((badges) => {
        if (badges.classList.contains('ok-native-list-wallet-badges'))
          badges.style.marginTop =
            box.lineGap === undefined
              ? gap
              : 'calc(' + gap + ' - ' + String(box.lineGap) + 'px)';
        else badges.style.marginInlineStart = gap;
      });
  }
  if (box.trailingGap !== undefined) {
    const gap = String(box.trailingGap) + 'px';
    body
      .querySelectorAll<HTMLElement>(
        '.ok-native-list-accessories, .ok-native-list-amounts'
      )
      .forEach((trailing) => {
        trailing.style.gap = gap;
      });
  }
  if (leading && box.image) {
    const image = box.image;
    if (image.width !== undefined) {
      leading.style.width = String(image.width) + 'px';
      if (!vertical) leading.style.flexBasis = String(image.width) + 'px';
    }
    if (image.height !== undefined)
      leading.style.height = String(image.height) + 'px';
    const radius =
      image.cornerRadius !== undefined
        ? String(image.cornerRadius) + 'px'
        : image.shape === 'circle'
        ? '50%'
        : image.shape === 'square'
        ? '0px'
        : image.shape === 'rounded'
        ? '10px'
        : undefined;
    if (radius !== undefined) {
      leading.style.setProperty('border-radius', radius, 'important');
      // Clip the bitmap itself; decorations may extend beyond the visual slot.
      leading.style.overflow = leading.matches('img') ? 'hidden' : 'visible';
    }
    const images = leading.matches('img')
      ? [leading]
      : leading.querySelectorAll<HTMLElement>(
          ':scope > img, :scope > .ok-native-list-visual-fallback'
        );
    images.forEach((bitmap) => {
      if (bitmap !== leading) {
        if (image.width !== undefined) bitmap.style.width = '100%';
        if (image.height !== undefined) bitmap.style.height = '100%';
      }
      if (radius !== undefined) bitmap.style.borderRadius = radius;
      if (image.contentFit !== undefined)
        bitmap.style.objectFit =
          image.contentFit === 'center' ? 'none' : image.contentFit;
    });
  }
  Object.keys(style).forEach((key) => {
    if (ROW_BOX_STYLE_KEYS.has(key)) return;
    const slotStyle = style[key] as NativeListTextStyle | undefined;
    if (!slotStyle) return;
    body
      .querySelectorAll<HTMLElement>('[data-nl-slot="' + key + '"]')
      .forEach((element) => {
        applyTextStyleToSlot(element, slotStyle);
      });
  });
}
type Views = {
  visual?: {
    key: string;
    node: HTMLElement;
    defaults: RowVisualStyle;
  };
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
function bind(body: HTMLElement, row: IdentityRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  let used = false;
  const next = createIdentityRow(
    body.ownerDocument,
    {
      ...primitives,
      visual: (source, presentation) => {
        used = true;
        const key = JSON.stringify([row.key, source, presentation]);
        if (v.visual?.key !== key) {
          if (v.visual) primitives.dispose(v.visual.node);
          const node = primitives.visual(source, presentation);
          if (!node) {
            v.visual = undefined;
            return;
          }
          v.visual = {
            key,
            node,
            defaults: new RowVisualStyle(node),
          };
        }
        v.visual.defaults.restore();
        return v.visual.node;
      },
    },
    row
  );
  if (!used && v.visual) {
    primitives.dispose(v.visual.node);
    v.visual = undefined;
  }
  body.replaceChildren(...Array.from(next.childNodes));
  body.className = next.className;
  body.style.cssText = next.style.cssText;
  if (
    row.presentation &&
    ['accountSelector', 'networkSelector', 'walletSidebar'].includes(
      row.presentation
    )
  )
    applyTabularNumbers(body);
  for (const name of body.getAttributeNames())
    if (name.startsWith('data-') && name !== 'data-nl-renderer')
      body.removeAttribute(name);
  for (const name of next.getAttributeNames())
    if (name.startsWith('data-'))
      body.setAttribute(name, next.getAttribute(name)!);
  if (row.type === 'identity' && hasExplicitRowHeight(row)) {
    const title = body.querySelector<HTMLElement>('.ok-native-list-title');
    if (row.presentation === 'accountSelector') {
      body.style.gap = '12px';
      body.style.borderRadius = '12px';
      if ('shape' in row.leading && row.leading.shape === 'rounded') {
        const visual = body.querySelector<HTMLElement>(
          '.ok-native-list-visual'
        );
        if (visual) visual.style.borderRadius = '8px';
      }
      if (title) title.style.lineHeight = '24px';
    }
    if (row.presentation === 'networkSelector') {
      body.style.borderRadius = '12px';
      const visual = body.querySelector<HTMLElement>('.ok-native-list-visual');
      if (visual) {
        visual.style.width = '32px';
        visual.style.height = '32px';
        visual.style.flexBasis = '32px';
      }
      if (
        row.leading.kind === 'network' &&
        !row.leading.image &&
        !row.leading.fallbackIcon &&
        row.leading.fallbackText
      ) {
        const fallback = visual?.querySelector<HTMLElement>(
          '.ok-native-list-visual-fallback'
        );
        if (fallback) {
          fallback.style.fontSize = '19px';
          fallback.style.lineHeight = '27px';
          fallback.style.fontWeight = '600';
          fallback.style.color = 'var(--nl-inverse-text)';
        }
      }
      visual
        ?.querySelectorAll<HTMLElement>('.ok-native-list-visual-main')
        .forEach((image) => {
          image.style.width = '32px';
          image.style.height = '32px';
        });
      if (title) {
        title.style.fontSize = '16px';
        title.style.lineHeight = '24px';
        title.style.fontWeight = '500';
      }
      body
        .querySelectorAll<HTMLElement>('.ok-native-list-accessory')
        .forEach((value) => {
          value.style.fontSize = '16px';
          value.style.lineHeight = '24px';
          value.style.fontWeight = '500';
        });
    }
  }

  applyIdentityStyle(body, row);
}
function create(
  document: Document,
  row: IdentityRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'identity';
  views.set(body, { primitives });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (v?.visual) v.primitives.dispose(v.visual.node);
  if (v) v.visual = undefined;
  body.replaceChildren();
}
export const identityRowRenderer = {
  key: 'identity' as const,
  create,
  bind,
  recycle,
  appliesSizePreset: (row: IdentityRow) => !row.presentation,
  measure: (row: IdentityRow, _width: number) =>
    row.presentation === 'walletSidebar'
      ? 68 + (row.badges?.length ? 24 : 0)
      : row.presentation === 'networkSelector'
      ? 47
      : row.presentation === 'accountSelector'
      ? 58
      : row.tertiary
      ? 72
      : row.subtitle
      ? 60
      : 56,
  measureRendered: (
    _body: HTMLElement,
    _row: IdentityRow
  ): number | undefined => undefined,
};
