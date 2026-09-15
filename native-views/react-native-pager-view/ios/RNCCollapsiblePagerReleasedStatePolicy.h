#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXPORT void RNCPruneReleasedPageStates(
  NSMutableDictionary<NSString *, id> *releasedStates,
  NSArray<NSString *> *validPageKeys
);

NS_ASSUME_NONNULL_END
