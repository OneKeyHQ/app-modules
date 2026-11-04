#import "RCTJCoreModule.h"
#import "JGInforCollectionAuth.h"

@implementation RCTJCoreModule

RCT_EXPORT_MODULE(JCoreModule);

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

RCT_EXPORT_METHOD(setAuth:(BOOL)enable) {
    [JGInforCollectionAuth JCollectionAuth:^(JGInforCollectionAuthItems * _Nonnull authInfo) {
        authInfo.isAuth = enable;
    }];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[];
}

@end
