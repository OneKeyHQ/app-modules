/**
 * Sheet content geometry. The content fills the width the platform gave the
 * sheet (never wider than the window); `maxWidth` narrows it and the
 * remaining space is split evenly as horizontal inset.
 */
export function resolveSheetWidth({
  windowWidth,
  reportedWidth,
  maxWidth,
}: {
  windowWidth: number;
  reportedWidth?: number;
  maxWidth?: number;
}): { containerWidth: number; contentWidth: number; inset: number } {
  const containerWidth = Math.min(reportedWidth ?? windowWidth, windowWidth);
  const contentWidth = Math.max(
    0,
    Math.min(maxWidth ?? containerWidth, containerWidth)
  );
  return {
    containerWidth,
    contentWidth,
    inset: (containerWidth - contentWidth) / 2,
  };
}
