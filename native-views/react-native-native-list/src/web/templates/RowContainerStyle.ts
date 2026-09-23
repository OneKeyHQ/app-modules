import type { RowModel } from '../../models';

export function applyRowContainerStyle(body: HTMLElement, row: RowModel): void {
  if (
    row.type === 'system' &&
    row.variant === 'warning' &&
    (row.style?.container?.height ?? row.height) !== undefined
  )
    body.style.height = '100%';
  const container = row.style?.container;
  if (!container) return;
  if (container.backgroundColor !== undefined) {
    body.style.setProperty(
      '--nl-container-background',
      container.backgroundColor
    );
    body.setAttribute('data-nl-container-background', 'true');
    // A CSS rule allows existing hover/press feedback to override the resting fill.
    body.style.removeProperty('background');
    body.style.removeProperty('background-color');
  }
  if (container.cornerRadius !== undefined)
    body.style.borderRadius = `${container.cornerRadius}px`;
  if (container.borderWidth !== undefined) {
    // Paint above member backgrounds without changing the content box or hit targets.
    body.setAttribute('data-nl-container-border', 'true');
    body.style.setProperty(
      '--nl-container-border-width',
      `${container.borderWidth}px`
    );
    body.style.setProperty(
      '--nl-container-border-color',
      container.borderColor ?? 'transparent'
    );
    body.style.borderColor = 'transparent';
  } else if (container.borderColor !== undefined)
    body.style.borderColor = container.borderColor;
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
      body.style.display = 'flex';
      body.style.flexDirection = 'column';
      if (row.type === 'mediaTile') body.style.gap = '0px';
      body.style.justifyContent = alignment;
      Array.from(body.children).forEach((child) => {
        (child as HTMLElement).style.flexShrink = '0';
      });
    } else {
      body.style.alignItems = alignment;
      // Timestamp/accessory defaults may have their own align-self.
      Array.from(body.children).forEach((child) => {
        (child as HTMLElement).style.alignSelf = alignment;
      });
    }
  }
}
