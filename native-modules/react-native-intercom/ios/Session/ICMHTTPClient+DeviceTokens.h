#import "ICMHTTPClient.h"
@class ICMUserIdentity;
@interface ICMHTTPClient (OriginalDeviceTokens)
+ (void)submitDeviceTokenWithFailure:(void (^)(NSError *))failure;
+ (void)submitDeviceTokenWithSuccess:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)deleteDeviceToken:(NSString *)token forUserIdentity:(ICMUserIdentity *)identity completion:(void (^)(void))completion;
@end
