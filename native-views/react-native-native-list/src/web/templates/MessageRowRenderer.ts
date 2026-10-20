import { RowVisualStyle } from './RowVisual';
import type { RowPrimitives } from './RowVisual';
import type { MessageRow, NativeListTextStyle } from '../../models';

type Text = Readonly<{
  value: string;
  style: Required<
    Pick<NativeListTextStyle, 'fontSize' | 'lineHeight' | 'lines'>
  > &
    NativeListTextStyle;
}>;
export function resolveMessageRow(row: MessageRow) {
  const text = (
    value: string,
    style: NativeListTextStyle | undefined,
    fontSize: number,
    lineHeight: number,
    lines: 1 | 2 | 3,
    color: string,
    fontWeight: NativeListTextStyle['fontWeight'] = 'regular'
  ): Text => ({
    value,
    style: {
      fontSize,
      lineHeight,
      lines,
      color,
      fontWeight,
      alignment: 'start',
      ...style,
    },
  });
  return {
    title: text(
      row.title,
      row.style?.title,
      15,
      20,
      2,
      'var(--nl-primary)',
      'semibold'
    ),
    body: text(
      row.body,
      row.style?.body,
      13,
      18,
      row.bodyLines ?? 3,
      'var(--nl-secondary)'
    ),
    time: text(
      row.time ?? '',
      row.style?.time,
      11,
      16,
      1,
      'var(--nl-secondary)'
    ),
    horizontalPadding: row.style?.horizontalPadding ?? 12,
    verticalPadding: row.style?.verticalPadding ?? 8,
    lineGap: row.style?.lineGap ?? 0,
    leadingGap: row.style?.leadingGap ?? 12,
    imageWidth:
      row.style?.image?.width ??
      (row.leading?.kind === 'stackedImages' ? 48 : 40),
    imageHeight: row.style?.image?.height ?? 40,
    image: row.style?.image,
    automaticHeight: (row.style?.container?.height ?? row.height) === undefined,
  };
}

type Views = {
  column: HTMLElement;
  title: HTMLElement;
  body: HTMLElement;
  time: HTMLElement;
  unread: HTMLElement;
  leading?: HTMLElement;
  thumbnail?: HTMLElement;
  leadingIdentity: string;
  thumbnailIdentity: string;
  // Factory defaults are captured before any caller style is applied. They are
  // never inferred from the previous bound row's styled state.
  visualStyle?: RowVisualStyle;
  row?: MessageRow;
  primitives: RowPrimitives;
};
const viewsByBody = new WeakMap<HTMLElement, Views>();
const identity = (key: string, source: unknown) =>
  JSON.stringify([key, source]);

export function classifyMessageUpdate(
  old: MessageRow | undefined,
  row: MessageRow
) {
  if (!old || old.key !== row.key) return 'replace';
  if (JSON.stringify(old) === JSON.stringify(row)) return 'unchanged';
  if (
    identity(row.key, old.leading) !== identity(row.key, row.leading) ||
    identity(row.key, old.thumbnail) !== identity(row.key, row.thumbnail)
  )
    return 'assets';
  return 'content';
}

export function bindMessageRow(
  body: HTMLElement,
  row: MessageRow,
  primitives: RowPrimitives
): void {
  const views = viewsByBody.get(body);
  if (!views) throw new Error('Message renderer received an incompatible body');
  const resolved = resolveMessageRow(row);
  views.primitives = primitives;
  const update = classifyMessageUpdate(views.row, row);
  if (update === 'unchanged') return;
  body.style.cssText = '';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  body.style.padding = `${resolved.verticalPadding}px ${resolved.horizontalPadding}px`;
  body.style.height = resolved.automaticHeight ? 'auto' : '100%';
  views.column.style.cssText = '';
  views.column.style.rowGap = `${resolved.lineGap}px`;
  for (const [view, text] of [
    [views.title, resolved.title],
    [views.body, resolved.body],
    [views.time, resolved.time],
  ] as const) {
    view.style.cssText = '';
    view.textContent = text.value;
    primitives.textStyle(view, text.style);
  }
  views.unread.style.cssText = '';
  if (row.body) {
    if (!views.body.parentElement) views.column.appendChild(views.body);
  } else views.body.remove();
  if (row.unread) {
    if (!views.unread.parentElement)
      body.insertBefore(views.unread, views.column);
  } else views.unread.remove();
  const leadingIdentity = identity(row.key, row.leading);
  if (leadingIdentity !== views.leadingIdentity) {
    if (views.leading) {
      primitives.dispose(views.leading);
      views.leading.remove();
    }
    views.leading = primitives.visual(row.leading);
    views.leadingIdentity = leadingIdentity;
    views.visualStyle = undefined;
    if (views.leading) {
      body.insertBefore(views.leading, body.firstChild);
      views.visualStyle = new RowVisualStyle(views.leading);
    }
  }
  views.visualStyle?.apply(
    resolved.image,
    resolved.imageWidth,
    resolved.imageHeight,
    resolved.leadingGap - 12
  );
  const thumbnailIdentity = identity(row.key, row.thumbnail);
  if (thumbnailIdentity !== views.thumbnailIdentity) {
    if (views.thumbnail) {
      primitives.dispose(views.thumbnail);
      views.thumbnail.remove();
    }
    views.thumbnail = row.thumbnail
      ? primitives.thumbnail(row.thumbnail)
      : undefined;
    views.thumbnailIdentity = thumbnailIdentity;
    if (views.thumbnail) body.appendChild(views.thumbnail);
  }
  if (views.thumbnail) views.thumbnail.style.alignSelf = '';
  views.row = row;
}

export function createMessageRow(
  document: Document,
  row: MessageRow,
  primitives: RowPrimitives
): HTMLElement {
  const span = (className: string, role?: string) => {
    const view = document.createElement('span');
    view.className = className;
    if (role) view.dataset.nlSlot = role;
    return view;
  };
  const body = document.createElement('div');
  body.className = 'ok-native-list-row ok-native-list-standard';
  body.dataset.nlRenderer = 'message';
  const column = span('ok-native-list-flex');
  const title = span('ok-native-list-title', 'title');
  const text = span('ok-native-list-secondary', 'body');
  const time = span('ok-native-list-time', 'time');
  column.append(title, text);
  body.append(column, time);
  viewsByBody.set(body, {
    column,
    title,
    body: text,
    time,
    unread: span('ok-native-list-unread'),
    leadingIdentity: '',
    thumbnailIdentity: '',
    primitives,
  });
  bindMessageRow(body, row, primitives);
  return body;
}

export function recycleMessageRow(body: HTMLElement): void {
  const views = viewsByBody.get(body);
  if (!views) return;
  if (views.leading) {
    views.primitives.dispose(views.leading);
    views.leading.remove();
  }
  if (views.thumbnail) {
    views.primitives.dispose(views.thumbnail);
    views.thumbnail.remove();
  }
  views.leading = undefined;
  views.thumbnail = undefined;
  views.row = undefined;
  views.leadingIdentity = '';
  views.thumbnailIdentity = '';
  views.visualStyle = undefined;
}

export function measureMessageRow(
  row: MessageRow,
  availableWidth: number
): number {
  const r = resolveMessageRow(row);
  const estimate = (text: Text, width: number) => {
    if (!text.value) return 0;
    const value =
      text.style.lines === 1
        ? text.value.replace(/\r\n|[\r\n]/g, ' ')
        : text.value;
    const lines = value
      .split(/\r\n|[\r\n]/)
      .reduce(
        (total, line) =>
          total +
          Math.max(
            1,
            Math.ceil(
              Array.from(line).reduce(
                (sum, char) =>
                  sum +
                  (char.charCodeAt(0) > 255
                    ? text.style.fontSize
                    : text.style.fontSize / 2),
                0
              ) / Math.max(1, width)
            )
          ),
        0
      );
    return Math.min(text.style.lines, lines) * text.style.lineHeight;
  };
  const timeWidth = Math.min(
    availableWidth / 3,
    Math.max(0, (r.time.value.length * r.time.style.fontSize) / 2)
  );
  const textWidth =
    availableWidth -
    r.horizontalPadding * 2 -
    (row.leading ? r.imageWidth + r.leadingGap : 0) -
    (row.thumbnail ? 76 : 0) -
    (row.unread ? 19 : 0) -
    timeWidth -
    12;
  const columnHeight =
    estimate(r.title, textWidth) +
    estimate(r.body, textWidth) +
    (row.body ? r.lineGap : 0);
  return (
    r.verticalPadding * 2 +
    Math.max(
      columnHeight,
      estimate(r.time, timeWidth),
      row.leading ? r.imageHeight : 0,
      row.thumbnail ? 64 : 0
    )
  );
}

export const messageRowRenderer = {
  key: 'message' as const,
  create: createMessageRow,
  bind: bindMessageRow,
  recycle: recycleMessageRow,
  measure: measureMessageRow,
  measureRendered: (body: HTMLElement, row: MessageRow) => {
    if (!resolveMessageRow(row).automaticHeight) return undefined;
    const height = body.getBoundingClientRect().height;
    return height > 0
      ? Math.max(
          0,
          height + (row.size === 'small' ? -8 : row.size === 'large' ? 12 : 0)
        )
      : undefined;
  },
};
