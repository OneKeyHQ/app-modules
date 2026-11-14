#import "ICMBridge.h"
@interface ICMBridge (IdentityLifecycle)
+ (void)logout;
+ (void)clearNonIdentityData;
+ (void)updateUserWithAttributes:(NSDictionary *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (NSError *)generateInvalidUserIdOrEmailError;
+ (NSError *)generateInvalidUpdateObjectError;
+ (NSError *)generateInvalidAuthTokenError;
@end
