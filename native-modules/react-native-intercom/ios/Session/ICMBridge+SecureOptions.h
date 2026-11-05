#import "ICMBridge.h"
@interface ICMBridge (SecureOptions)
+ (void)setApiKey:(NSString *)apiKey appId:(NSString *)appId;
+ (void)setSecureOptions:(NSDictionary *)options;
+ (void)applySecureOptions:(NSDictionary *)options;
+ (void)setUserJwtProvider:(void (^)(void (^)(NSString *jwt)))provider;
+ (void)removeUserJwtProvider;
+ (void)refreshedUserJwtWasApplied;
+ (id)currentUserJwt;
+ (void)postUserJwtDidChange;
+ (void)sendSecureOptionsIfNeeded;
@end
@interface ICMBridge (UnrestoredIdentityCleanup)
+ (void)clearNonIdentityData;
@end
