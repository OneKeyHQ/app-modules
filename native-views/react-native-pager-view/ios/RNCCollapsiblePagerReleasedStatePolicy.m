#import "RNCCollapsiblePagerReleasedStatePolicy.h"

CGFloat RNCLimitRestoredScrollOffset(
  CGFloat offset,
  CGFloat contentHeight,
  CGFloat viewportHeight,
  CGFloat topInset,
  CGFloat bottomInset
)
{
  if (viewportHeight <= 0) return offset;
  CGFloat maximum = MAX(-topInset, contentHeight - viewportHeight + bottomInset);
  return MIN(offset, maximum);
}

void RNCPruneReleasedPageStates(
  NSMutableDictionary *releasedStates,
  NSArray<NSString *> *validPageKeys
)
{
  NSSet<NSString *> *validKeys = [NSSet setWithArray:validPageKeys];
  for (NSString *pageKey in releasedStates.allKeys) {
    if (![validKeys containsObject:pageKey]) {
      [releasedStates removeObjectForKey:pageKey];
    }
  }
}
