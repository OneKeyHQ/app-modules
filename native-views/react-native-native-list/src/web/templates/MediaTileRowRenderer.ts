import type { MediaTileRow, NativeListTextStyle } from '../../models';
import type { RowPrimitives } from './RowVisual';

type Views = {
  title: HTMLElement;
  subtitle: HTMLElement;
  badge: HTMLElement;
  metadata: HTMLElement;
  subtitleLine: HTMLElement;
  close: HTMLButtonElement;
  image?: HTMLElement;
  network?: HTMLElement;
  imageKey: string;
  networkKey: string;
  primitives: RowPrimitives;
};
const views = new WeakMap<HTMLElement, Views>();
function bind(body: HTMLElement, row: MediaTileRow, primitives: RowPrimitives) {
  const v = views.get(body)!;
  v.primitives = primitives;
  const style = row.style;
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  if (style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${style.horizontalPadding}px`;
  if (style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${style.verticalPadding}px`;
  const text = (
    node: HTMLElement,
    value: string,
    roleStyle?: NativeListTextStyle
  ) => {
    node.style.cssText = '';
    node.textContent = value;
    if (roleStyle) primitives.textStyle(node, roleStyle);
  };
  text(v.title, row.title, style?.title);
  text(v.subtitle, row.subtitle || '-', style?.subtitle);
  text(v.badge, row.badge?.text ?? '', style?.badge);
  if (row.badge) {
    v.metadata.appendChild(v.badge);
    v.badge.dataset.tone = row.badge.tone ?? '';
  } else v.badge.remove();
  v.metadata.style.cssText = '';
  if (style?.leadingGap !== undefined)
    v.metadata.style.paddingTop = `${style.leadingGap}px`;
  if (style?.lineGap !== undefined)
    Object.assign(v.metadata.style, {
      display: 'flex',
      flexDirection: 'column',
      rowGap: `${style.lineGap}px`,
    });
  const imageKey = JSON.stringify([row.key, row.image, row.imageState]);
  if (v.imageKey !== imageKey) {
    if (v.image) {
      primitives.dispose(v.image);
      v.image.remove();
    }
    v.image =
      row.image && !row.imageState
        ? primitives.thumbnail(row.image)
        : undefined;
    if (!v.image) {
      v.image = body.ownerDocument.createElement('span');
      v.image.dataset.state = row.imageState ?? 'empty';
      v.image.textContent = row.imageState === 'error' ? '▧' : '';
    }
    v.image.className = 'ok-native-list-media-image';
    body.insertBefore(v.image, v.metadata);
    v.imageKey = imageKey;
  }
  const image = v.image!;
  // Only restore geometry; loading opacity is owned by the image primitive.
  ['width', 'height', 'border-radius', 'overflow', 'object-fit'].forEach(
    (key) => image.style.removeProperty(key)
  );
  if (style?.image?.width !== undefined)
    image.style.width = `${style.image.width}px`;
  if (style?.image?.height !== undefined)
    image.style.height = `${style.image.height}px`;
  const shape = style?.image?.shape;
  const radius =
    style?.image?.cornerRadius !== undefined
      ? `${style.image.cornerRadius}px`
      : shape === 'circle'
      ? '50%'
      : shape === 'square'
      ? '0px'
      : shape === 'rounded'
      ? '10px'
      : undefined;
  if (radius !== undefined)
    image.style.setProperty('border-radius', radius, 'important');
  const fit = style?.image?.contentFit ?? row.image?.contentFit;
  if (fit) image.style.objectFit = fit === 'center' ? 'none' : fit;
  const networkKey = JSON.stringify([row.key, row.networkImage]);
  if (v.networkKey !== networkKey) {
    if (v.network) {
      primitives.dispose(v.network);
      v.network.remove();
    }
    v.network = row.networkImage
      ? primitives.thumbnail(row.networkImage)
      : undefined;
    if (v.network) {
      v.network.className = 'ok-native-list-media-network';
      v.subtitleLine.appendChild(v.network);
    }
    v.networkKey = networkKey;
  }
  if (row.closeActionKey) {
    v.close.dataset.nativeListAction = row.closeActionKey;
    body.appendChild(v.close);
  } else v.close.remove();
}
function create(
  document: Document,
  row: MediaTileRow,
  primitives: RowPrimitives
) {
  const node = (tag: string, className: string, slot?: string) => {
    const n = document.createElement(tag);
    n.className = className;
    if (slot) n.dataset.nlSlot = slot;
    return n;
  };
  const body = node('div', 'ok-native-list-row ok-native-list-media');
  body.dataset.nlRenderer = 'mediaTile';
  const metadata = node('div', 'ok-native-list-media-meta');
  const subtitleLine = node('div', 'ok-native-list-media-subtitle-row');
  const title = node('div', 'ok-native-list-media-title', 'title');
  const subtitle = node('span', 'ok-native-list-media-subtitle', 'subtitle');
  const badge = node('span', 'ok-native-list-badge', 'badge');
  const close = node(
    'button',
    'ok-native-list-media-close'
  ) as HTMLButtonElement;
  close.type = 'button';
  close.textContent = '×';
  close.setAttribute('aria-label', 'Close');
  close.dataset.nativeListAnchorSource = 'mediaClose';
  subtitleLine.appendChild(subtitle);
  metadata.append(subtitleLine, title);
  body.appendChild(metadata);
  views.set(body, {
    title,
    subtitle,
    badge,
    metadata,
    subtitleLine,
    close,
    primitives,
    imageKey: '',
    networkKey: '',
  });
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const v = views.get(body);
  if (!v) return;
  [v.image, v.network].forEach((n) => {
    if (n) {
      v.primitives.dispose(n);
      n.remove();
    }
  });
  v.image = v.network = undefined;
  v.imageKey = v.networkKey = '';
}
export const mediaTileRowRenderer = {
  key: 'mediaTile' as const,
  create,
  bind,
  recycle,
  measure: (_row: MediaTileRow, _width: number) => 244,
  measureRendered: (
    _body: HTMLElement,
    _row: MediaTileRow
  ): number | undefined => undefined,
};
