import Foundation

func nativeListAlignedScrollOffset(
  itemStart: CGFloat,
  startInset: CGFloat,
  viewportLength: CGFloat,
  itemLength: CGFloat,
  viewPosition: CGFloat,
  viewOffset: CGFloat,
  nearest: Bool
) -> CGFloat {
  let alignmentSpace = viewportLength - itemLength
  let resolvedSpace = nearest ? max(0, alignmentSpace) : alignmentSpace
  return itemStart - startInset - viewPosition * resolvedSpace - viewOffset
}

func nativeListClampedScrollOffset(
  _ requestedOffset: CGFloat,
  viewportLength: CGFloat,
  contentLength: CGFloat,
  startInset: CGFloat,
  endInset: CGFloat
) -> CGFloat {
  let minimum = -startInset
  let maximum = max(minimum, contentLength - viewportLength + endInset)
  return min(maximum, max(minimum, requestedOffset))
}
