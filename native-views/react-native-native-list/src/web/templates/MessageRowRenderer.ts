import type {
  ImageSource,
  LeadingVisual,
  MessageRow,
  NativeListTextStyle,
} from '../../models';

// Asset loading and text styling remain shared; the template owns its structure.
type MessagePrimitives = Readonly<{
  visual: (source: LeadingVisual | undefined) => HTMLElement | undefined;
  thumbnail: (source: ImageSource) => HTMLElement | undefined;
  textLayout: (element: HTMLElement, style: NativeListTextStyle) => void;
}>;

export function createMessageRow(
  document: Document,
  row: MessageRow,
  primitives: MessagePrimitives
): HTMLElement {
  const span = (className: string, text?: string, role?: string) => {
    const element = document.createElement('span');
    element.className = className;
    if (text !== undefined) element.textContent = text;
    if (role) element.dataset.nlSlot = role;
    return element;
  };
  const body = document.createElement('div');
  body.className = 'ok-native-list-row ok-native-list-standard';
  const leading = primitives.visual(row.leading);
  if (leading) body.appendChild(leading);
  if (row.unread) body.appendChild(span('ok-native-list-unread'));
  const column = span('ok-native-list-flex');
  const title = span('ok-native-list-title', row.title, 'title');
  primitives.textLayout(title, { lines: row.style?.title?.lines ?? 2 });
  column.appendChild(title);
  if (row.body) {
    const text = span('ok-native-list-secondary', row.body, 'body');
    primitives.textLayout(text, {
      lines: row.style?.body?.lines ?? row.bodyLines ?? 3,
    });
    column.appendChild(text);
  }
  body.appendChild(column);
  body.appendChild(span('ok-native-list-time', row.time, 'time'));
  if (row.thumbnail) {
    const thumbnail = primitives.thumbnail(row.thumbnail);
    if (thumbnail) body.appendChild(thumbnail);
  }
  return body;
}

export function measureMessageRow(row: MessageRow, availableWidth: number) {
  const style = row.style;
  const leadingWidth = row.leading
    ? (style?.image?.width ?? 40) + (style?.leadingGap ?? 12)
    : 0;
  const thumbnailWidth = row.thumbnail ? 88 : 0;
  const textWidth = Math.max(
    1,
    availableWidth -
      leadingWidth -
      thumbnailWidth -
      (style?.horizontalPadding ?? 20) * 2
  );
  const height = (
    text: string,
    textStyle: NativeListTextStyle | undefined,
    fallbackLines: number
  ) => {
    const size = textStyle?.fontSize ?? 14;
    const charactersPerLine = Math.max(
      textStyle ? 1 : 18,
      Math.floor(textWidth / (size / 2))
    );
    const measured = text
      .split(/\r\n|[\r\n]/)
      .reduce(
        (total, line) =>
          total + Math.max(1, Math.ceil(line.length / charactersPerLine)),
        0
      );
    return (
      Math.min(textStyle?.lines ?? fallbackLines, measured) *
      (textStyle?.lineHeight ?? 20)
    );
  };
  return (
    (style?.verticalPadding ?? 16) * 2 +
    height(row.title, style?.title, 2) +
    height(row.body, style?.body, row.bodyLines ?? 3) +
    height(row.time, style?.time, 1) +
    (style?.lineGap ?? 1) * 2
  );
}
