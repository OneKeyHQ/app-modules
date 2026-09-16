#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

// Keep the dictionary unspecialized: NSMutableDictionary generics are invariant,
// so Objective-C++ callers with a concrete value type would fail to compile.
FOUNDATION_EXPORT void RNCPruneReleasedPageStates(
  NSMutableDictionary *releasedStates,
  NSArray<NSString *> *validPageKeys
);

NS_ASSUME_NONNULL_END
