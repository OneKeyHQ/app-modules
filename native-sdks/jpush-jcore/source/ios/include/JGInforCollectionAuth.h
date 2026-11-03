#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface JGInforCollectionAuthItems : NSObject

@property (nonatomic, assign) BOOL isAuth;

@end

@interface JGInforCollectionAuth : NSObject

+ (void)JCollectionAuth:(void (^_Nullable)(JGInforCollectionAuthItems *authInfo))authBlock;

@end

NS_ASSUME_NONNULL_END
