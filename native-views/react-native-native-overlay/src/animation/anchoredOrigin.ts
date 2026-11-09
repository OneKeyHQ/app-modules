/**
 * The transform origin for an anchored panel placed on `placement` of its
 * anchor (Floating UI notation, e.g. `bottom-end`): the panel grows out of
 * the side and corner that face the anchor. Undefined for an unknown value.
 */
export function getAnchoredTransformOrigin(
  placement: string | undefined
): string | undefined {
  if (!placement) {
    return undefined;
  }
  const [side, align] = placement.split('-');
  const facing = (
    {
      top: 'bottom',
      bottom: 'top',
      left: 'right',
      right: 'left',
    } as Record<string, string>
  )[side ?? ''];
  if (!facing) {
    return undefined;
  }
  const vertical = side === 'top' || side === 'bottom';
  const cross = !align
    ? 'center'
    : vertical
    ? align === 'start'
      ? 'left'
      : 'right'
    : align === 'start'
    ? 'top'
    : 'bottom';
  return vertical ? `${cross} ${facing}` : `${facing} ${cross}`;
}
