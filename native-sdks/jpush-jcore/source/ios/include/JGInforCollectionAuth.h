#pragma once
#import "JGInforCollectionAuthItems.h"

NS_ASSUME_NONNULL_BEGIN
@interface JGInforCollectionAuth : NSObject
+ (void)JCollectionAuth:(void (^ _Nullable)(JGInforCollectionAuthItems *authInfo))authBlock;
@end
NS_ASSUME_NONNULL_END
