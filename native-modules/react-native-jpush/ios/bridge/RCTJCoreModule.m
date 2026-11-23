#import "RCTJCoreModule.h"
#import "JGInforCollectionAuth.h"

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#endif

@interface RCTJCoreModule () <RCTBridgeModule>
@end

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
