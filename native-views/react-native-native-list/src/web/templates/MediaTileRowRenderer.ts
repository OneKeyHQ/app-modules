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
function bindLegacy(
  body: HTMLElement,
  row: MediaTileRow,
  primitives: RowPrimitives
) {
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
function createLegacy(
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
  bindLegacy(body, row, primitives);
  return body;
}
function recycleLegacy(body: HTMLElement) {
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
type GalleryViews = {
  frame: HTMLElement;
  media?: HTMLImageElement | HTMLVideoElement;
  fallback: HTMLElement;
  metadata: HTMLElement;
  subtitleLine: HTMLElement;
  subtitle: HTMLElement;
  title: HTMLElement;
  badge: HTMLElement;
  network?: HTMLElement;
  networkKey: string;
  mediaKey: string;
  epoch: number;
  primitives: RowPrimitives;
  enter: () => void;
  leave: () => void;
};
const galleries = new WeakMap<HTMLElement, GalleryViews>();
const isGalleryMedia = (row: MediaTileRow) =>
  row.variant === 'gallery' && !row.image;

function clearGalleryMedia(v: GalleryViews) {
  v.epoch += 1;
  v.frame.style.borderRadius = '10px';
  if (!v.media) return;
  v.media.onerror = null;
  v.media.onload = null;
  v.media.onloadeddata = null;
  if (v.media.tagName === 'VIDEO') {
    const video = v.media as HTMLVideoElement;
    video.pause();
    video.removeAttribute('src');
    video.load();
  } else v.media.removeAttribute('src');
  v.media.remove();
  v.media = undefined;
}

function initGallery(body: HTMLElement, primitives: RowPrimitives) {
  const document = body.ownerDocument;
  const element = (className: string) => {
    const node = document.createElement('div');
    node.className = className;
    return node;
  };
  const frame = element('ok-native-list-media-image');
  const fallback = element('ok-native-list-media-fallback');
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.setAttribute('viewBox', '0 0 24 24');
  svg.setAttribute('width', '24');
  svg.setAttribute('height', '24');
  svg.setAttribute('aria-hidden', 'true');
  svg.setAttribute('fill', 'currentColor');
  for (const d of [
    'M14.25 7a2 2 0 1 1 0 4 2 2 0 0 1 0-4',
    'M21 21H3V3h18zM5 16.414V19h12.586L14 15.414l-2 2-4-4zm0-2.828 3-3 4 4 2-2 5 5V5H5z',
  ]) {
    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('d', d);
    path.setAttribute('fill-rule', 'evenodd');
    svg.append(path);
  }
  fallback.append(svg);
  Object.assign(fallback.style, {
    position: 'absolute',
    inset: '0',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'var(--nl-disabled)',
    borderRadius: '10px',
    background: 'var(--nl-strong)',
  });
  const metadata = element('ok-native-list-media-meta');
  const subtitleLine = element('ok-native-list-media-subtitle-row');
  const subtitle = element('ok-native-list-media-subtitle');
  const title = element('ok-native-list-media-title');
  const badge = element('ok-native-list-badge');
  subtitle.dataset.nlSlot = 'subtitle';
  title.dataset.nlSlot = 'title';
  badge.dataset.nlSlot = 'badge';
  subtitleLine.append(subtitle);
  metadata.append(subtitleLine, title);
  frame.append(fallback);
  body.replaceChildren(frame, metadata);
  const enter = () => {
    frame.style.opacity = '0.8';
  };
  const leave = () => {
    frame.style.opacity = '';
  };
  body.addEventListener('mouseenter', enter);
  body.addEventListener('mouseleave', leave);
  const v: GalleryViews = {
    frame,
    fallback,
    metadata,
    subtitleLine,
    subtitle,
    title,
    badge,
    networkKey: '',
    mediaKey: '',
    epoch: 0,
    primitives,
    enter,
    leave,
  };
  galleries.set(body, v);
  return v;
}

function bindGallery(
  body: HTMLElement,
  row: MediaTileRow,
  primitives: RowPrimitives
) {
  const v = galleries.get(body) ?? initGallery(body, primitives);
  v.primitives = primitives;
  const style = row.style;
  body.className = 'ok-native-list-row ok-native-list-media';
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  Object.assign(body.style, {
    display: 'block',
    position: 'relative',
    boxSizing: 'border-box',
    padding: `${style?.verticalPadding ?? 10}px ${
      style?.horizontalPadding ?? 10
    }px`,
    borderRadius: '16px',
    background: 'transparent',
    height: 'auto',
  });
  Object.assign(v.frame.style, {
    position: 'relative',
    width: '100%',
    height: 'auto',
    aspectRatio: '1',
    borderRadius: v.media?.tagName === 'VIDEO' ? '0' : '10px',
    overflow: 'hidden',
    background: 'var(--nl-bg)',
  });
  v.metadata.style.paddingTop = `${style?.leadingGap ?? 8}px`;
  v.subtitleLine.style.gap = '0px';
  v.subtitle.style.cssText =
    'font-size:12px;line-height:16px;font-weight:400;box-sizing:border-box;padding-right:8px';
  v.title.style.cssText = 'font-size:16px;line-height:24px;font-weight:500';
  v.subtitle.textContent = row.subtitle || '-';
  v.title.textContent = row.title || '-';
  if (style?.subtitle) primitives.textStyle(v.subtitle, style.subtitle);
  if (style?.title) primitives.textStyle(v.title, style.title);
  const mediaKey = JSON.stringify([row.key, row.media, row.imageState]);
  if (v.mediaKey !== mediaKey) {
    clearGalleryMedia(v);
    v.mediaKey = mediaKey;
    v.fallback.style.visibility = 'visible';
    const epoch = v.epoch;
    const source = row.media?.source;
    const probe = (attempt: number) => {
      if (v.epoch !== epoch) return;
      const kind = row.media?.probeOrder[attempt];
      if (!source?.uri || !kind) return;
      const media = body.ownerDocument.createElement(
        kind === 'video' ? 'video' : 'img'
      );
      v.frame.style.borderRadius = kind === 'video' ? '0' : '10px';
      Object.assign(media.style, {
        display: 'block',
        position: 'absolute',
        inset: '0',
        width: '100%',
        height: '100%',
        objectFit:
          source.contentFit === 'center'
            ? 'none'
            : source.contentFit ?? (kind === 'video' ? 'contain' : 'cover'),
        borderRadius: kind === 'video' ? '0' : '10px',
        pointerEvents: 'none',
      });
      media.setAttribute('aria-hidden', 'true');
      if (media.tagName === 'VIDEO') {
        const video = media as HTMLVideoElement;
        video.muted = true;
        video.autoplay = false;
        video.controls = false;
        video.playsInline = true;
        video.preload = 'metadata';
        video.tabIndex = -1;
      } else {
        (media as HTMLImageElement).alt = '';
        (media as HTMLImageElement).decoding = 'async';
      }
      media.onerror = () => {
        if (v.epoch !== epoch || v.media !== media) return;
        media.onerror = null;
        media.onload = null;
        media.onloadeddata = null;
        if (media.tagName === 'VIDEO') (media as HTMLVideoElement).pause();
        media.removeAttribute('src');
        if (media.tagName === 'VIDEO') (media as HTMLVideoElement).load();
        media.remove();
        v.media = undefined;
        v.frame.style.borderRadius = '10px';
        v.fallback.style.visibility = 'visible';
        probe(attempt + 1);
      };
      const ready = () => {
        if (v.epoch === epoch && v.media === media)
          v.fallback.style.visibility = 'hidden';
      };
      if (media.tagName === 'VIDEO') media.onloadeddata = ready;
      else media.onload = ready;
      v.media = media;
      v.frame.insertBefore(
        media,
        v.badge.parentNode === v.frame ? v.badge : null
      );
      media.src = source.uri;
    };
    probe(0);
  }
  v.badge.textContent = row.badge?.text ?? '';
  if (row.badge) {
    Object.assign(v.badge.style, {
      position: 'absolute',
      right: '0',
      bottom: '0',
      boxSizing: 'content-box',
      height: '20px',
      padding: '0 8px',
      border: '2px solid var(--nl-bg)',
      borderRadius: '10px',
      background: 'var(--nl-inverse)',
      color: 'var(--nl-inverse-text)',
      fontSize: '14px',
      fontWeight: '500',
      fontFamily: 'var(--nl-font-medium)',
      lineHeight: '20px',
    });
    if (style?.badge) primitives.textStyle(v.badge, style.badge);
    v.frame.append(v.badge);
  } else v.badge.remove();
  const networkKey = JSON.stringify(row.networkImage);
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
      v.subtitleLine.append(v.network);
    }
    v.networkKey = networkKey;
  }
}

function recycle(body: HTMLElement) {
  const gallery = galleries.get(body);
  if (gallery) {
    clearGalleryMedia(gallery);
    if (gallery.network) gallery.primitives.dispose(gallery.network);
    body.removeEventListener('mouseenter', gallery.enter);
    body.removeEventListener('mouseleave', gallery.leave);
    galleries.delete(body);
  }
  recycleLegacy(body);
  views.delete(body);
}
function bind(body: HTMLElement, row: MediaTileRow, primitives: RowPrimitives) {
  const gallery = isGalleryMedia(row);
  if (gallery) {
    if (views.has(body)) {
      recycleLegacy(body);
      views.delete(body);
    }
    bindGallery(body, row, primitives);
  } else {
    if (galleries.has(body)) recycle(body);
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
  row: MediaTileRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'mediaTile';
  bind(body, row, primitives);
  return body;
}
export const mediaTileRowRenderer = {
  key: 'mediaTile' as const,
  create,
  bind,
  recycle,
  measure: (row: MediaTileRow, width: number) =>
    isGalleryMedia(row) ? width + 48 : 244,
  measureRendered: (
    _body: HTMLElement,
    _row: MediaTileRow
  ): number | undefined => undefined,
};
