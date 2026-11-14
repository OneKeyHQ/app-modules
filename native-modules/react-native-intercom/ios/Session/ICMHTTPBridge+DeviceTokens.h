#import "ICMHTTPBridge.h"
@class ICMConfig;
@interface ICMHTTPBridge (OriginalDeviceTokens)
+ (void)registerDeviceToken:(NSData *)token failure:(void (^)(NSError *))failure;
+ (void)registerDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (NSInteger)deviceTokenDecisionForConfig:(ICMConfig *)config userIdentity:(ICMUserIdentity *)identity;
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end
