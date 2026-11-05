/**
 * Non-sheet content geometry. JS lays the content out at the window size it
 * last saw, and the native content host clips the content to that frame. Right
 * after the window rotates, JS still holds the previous size, so a caller that
 * prepared its content for the window it is about to rotate into passes that
 * size instead.
 */
export function resolveContentSize({
  windowWidth,
  windowHeight,
  contentSize,
}: {
  windowWidth: number;
  windowHeight: number;
  contentSize?: { width: number; height: number };
}): { width: number; height: number } {
  return {
    width: contentSize?.width ?? windowWidth,
    height: contentSize?.height ?? windowHeight,
  };
}
