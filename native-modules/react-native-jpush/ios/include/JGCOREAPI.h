#import <Foundation/Foundation.h>
#import "JGInforCollectionAuth.h"

NS_ASSUME_NONNULL_BEGIN

@interface JGCOREAPI : NSObject

+ (void)ci:(BOOL)enable;
+ (void)printLog:(BOOL)enable;
+ (void)preferIPv6:(BOOL)preferIPv6;

@end

NS_ASSUME_NONNULL_END
