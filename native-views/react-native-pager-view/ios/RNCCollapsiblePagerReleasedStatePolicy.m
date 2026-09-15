#import "RNCCollapsiblePagerReleasedStatePolicy.h"

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
