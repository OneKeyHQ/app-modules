import type { RowModel } from '../../models';

type Write = {
  element: HTMLElement;
  property: string;
  previous: string;
  previousPriority: string;
  written: string;
};

// Inline properties written by the container pass, per row body. Renderers only
// reset the nodes they own, so the container undoes its own writes on rebind.
const writesByBody = new WeakMap<HTMLElement, Write[]>();

function restoreContainerWrites(body: HTMLElement, force: boolean) {
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  const writes = writesByBody.get(body);
  if (!writes) return;
  writesByBody.delete(body);
  // Undo in reverse so repeated writes restore the oldest value.
  for (let index = writes.length - 1; index >= 0; index -= 1) {
    const write = writes[index]!;
    const { style } = write.element;
    // Fallback path only (no reset before the renderer bind): a renderer that
    // already rewrote this property owns its current value. It cannot tell a
    // renderer rewrite of the same value apart, which is why the engine and
    // WalletGroup reset before binding instead.
    if (!force && style.getPropertyValue(write.property) !== write.written)
      continue;
    style.removeProperty(write.property);
    if (write.previous)
      style.setProperty(write.property, write.previous, write.previousPriority);
  }
}

/**
 * Undo the previous container pass before a renderer rebinds `body`, so the
 * renderer always writes over its own baseline and the next
 * `applyRowContainerStyle` records that fresh value as the value to restore.
 */
export function resetRowContainerStyle(body: HTMLElement): void {
  restoreContainerWrites(body, true);
}

function writeProperty(
  body: HTMLElement,
  element: HTMLElement,
  property: string,
  value: string,
  priority = ''
) {
  const { style } = element;
  const write: Write = {
    element,
    property,
    previous: style.getPropertyValue(property),
    previousPriority: style.getPropertyPriority(property),
    written: '',
  };
  style.setProperty(property, value, priority);
  write.written = style.getPropertyValue(property);
  let writes = writesByBody.get(body);
  if (!writes) writesByBody.set(body, (writes = []));
  writes.push(write);
}

export function applyRowContainerStyle(body: HTMLElement, row: RowModel): void {
  restoreContainerWrites(body, false);
  const set = (element: HTMLElement, property: string, value: string) =>
    writeProperty(body, element, property, value);
  if (
    row.type === 'system' &&
    row.variant === 'warning' &&
    (row.style?.container?.height ?? row.height) !== undefined
  )
    set(body, 'height', '100%');
  const container = row.style?.container;
  if (!container) return;
  if (container.backgroundColor !== undefined) {
    set(body, '--nl-container-background', container.backgroundColor);
    body.setAttribute('data-nl-container-background', 'true');
    // A CSS rule allows existing hover/press feedback to override the resting fill.
    // The template/engine re-applies its own fill on every bind, so this
    // removal is not tracked for restoration.
    body.style.removeProperty('background');
    body.style.removeProperty('background-color');
  }
  if (container.cornerRadius !== undefined)
    set(body, 'border-radius', `${container.cornerRadius}px`);
  if (container.borderWidth !== undefined) {
    // Paint above member backgrounds without changing the content box or hit targets.
    body.setAttribute('data-nl-container-border', 'true');
    set(body, '--nl-container-border-width', `${container.borderWidth}px`);
    set(
      body,
      '--nl-container-border-color',
      container.borderColor ?? 'transparent'
    );
    set(body, 'border-color', 'transparent');
  } else if (container.borderColor !== undefined)
    set(body, 'border-color', container.borderColor);
  if (container.contentVerticalAlignment !== undefined) {
    const alignment = {
      top: 'flex-start',
      center: 'center',
      bottom: 'flex-end',
    }[container.contentVerticalAlignment];
    const vertical =
      row.type === 'walletGroup' ||
      row.type === 'mediaTile' ||
      row.type === 'metricCard' ||
      (row.type === 'identity' && row.presentation === 'walletSidebar') ||
      (row.type === 'system' && row.variant === 'warning');
    if (vertical) {
      set(body, 'display', 'flex');
      set(body, 'flex-direction', 'column');
      if (row.type === 'mediaTile') set(body, 'gap', '0px');
      set(body, 'justify-content', alignment);
      Array.from(body.children).forEach((child) => {
        set(child as HTMLElement, 'flex-shrink', '0');
      });
    } else {
      set(body, 'align-items', alignment);
      // Timestamp/accessory defaults may have their own align-self.
      Array.from(body.children).forEach((child) => {
        set(child as HTMLElement, 'align-self', alignment);
      });
    }
  }
}
