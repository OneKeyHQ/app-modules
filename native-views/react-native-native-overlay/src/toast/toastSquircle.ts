/**
 * Generates an SVG path string for a continuous-curvature squircle matching
 * Figma's `cornerSmoothing` model (the same geometry used by RareUI's toast
 * cards and status tiles).
 */
export function getSquirclePath(
  width: number,
  height: number,
  cornerRadius: number,
  cornerSmoothing = 1
): string {
  if (width <= 0 || height <= 0) {
    return '';
  }
  const maxRadius = Math.min(width, height) / 2;
  const r = Math.max(0, Math.min(cornerRadius, maxRadius));
  if (r === 0) {
    return `M0 0H${width}V${height}H0Z`;
  }
  const clampedSmoothing = Math.max(0, Math.min(cornerSmoothing, 1));
  const p = Math.min((1 + clampedSmoothing) * r, maxRadius);
  const smoothing = p / r - 1;

  const arcMeasure = 90 * (1 - smoothing);
  const toRad = (deg: number) => (deg * Math.PI) / 180;
  const arcSection = Math.sin(toRad(arcMeasure / 2)) * r * Math.SQRT2;
  const angleAlpha = (90 - arcMeasure) / 2;
  const p3ToP4 = r * Math.tan(toRad(angleAlpha / 2));
  const angleBeta = 45 * smoothing;
  const c = p3ToP4 * Math.cos(toRad(angleBeta));
  const d = c * Math.tan(toRad(angleBeta));
  const b = (p - arcSection - c - d) / 3;
  const a = 2 * b;

  const fmt = (value: number) => Number(value.toFixed(3));
  const w = fmt(width);
  const h = fmt(height);
  const rFmt = fmt(r);
  const pFmt = fmt(p);
  const aEnd = fmt(p - a);
  const abEnd = fmt(p - a - b);
  const abcEnd = fmt(p - a - b - c);
  const dFmt = fmt(d);
  const hasArc = arcSection > 0.001;

  // Top-right, bottom-right, bottom-left, top-left.
  return [
    `M${fmt(width - p)} 0`,
    `C${fmt(width - aEnd)} 0 ${fmt(width - abEnd)} 0 ${fmt(
      width - abcEnd
    )} ${dFmt}`,
    hasArc ? `A${rFmt} ${rFmt} 0 0 1 ${fmt(width - d)} ${abcEnd}` : '',
    `C${w} ${abEnd} ${w} ${aEnd} ${w} ${pFmt}`,
    `L${w} ${fmt(height - p)}`,
    `C${w} ${fmt(height - aEnd)} ${w} ${fmt(height - abEnd)} ${fmt(
      width - d
    )} ${fmt(height - abcEnd)}`,
    hasArc
      ? `A${rFmt} ${rFmt} 0 0 1 ${fmt(width - abcEnd)} ${fmt(height - d)}`
      : '',
    `C${fmt(width - abEnd)} ${h} ${fmt(width - aEnd)} ${h} ${fmt(
      width - p
    )} ${h}`,
    `L${pFmt} ${h}`,
    `C${aEnd} ${h} ${abEnd} ${h} ${abcEnd} ${fmt(height - d)}`,
    hasArc ? `A${rFmt} ${rFmt} 0 0 1 ${dFmt} ${fmt(height - abcEnd)}` : '',
    `C0 ${fmt(height - abEnd)} 0 ${fmt(height - aEnd)} 0 ${fmt(height - p)}`,
    `L0 ${pFmt}`,
    `C0 ${aEnd} 0 ${abEnd} ${dFmt} ${abcEnd}`,
    hasArc ? `A${rFmt} ${rFmt} 0 0 1 ${abcEnd} ${dFmt}` : '',
    `C${abEnd} 0 ${aEnd} 0 ${pFmt} 0`,
    'Z',
  ]
    .filter(Boolean)
    .join(' ');
}
