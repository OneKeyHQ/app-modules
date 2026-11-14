#import "ICMBridge.h"
@class ICMIdentityStore, ICMUserIdentity;
@interface ICMBridge (Registration)
+ (void)registerUnidentifiedUserWithSuccess:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)registerIdentifiedUserWithProperties:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)handleExistingIdentity:(ICMUserIdentity *)identity userAttributes:(ICMUserAttributes *)attributes identityStore:(ICMIdentityStore *)store success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)handleMatchingExistingIdentity:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)handleNewIdentity:(ICMIdentityStore *)store userAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (BOOL)isPreviousIdentityMatch:(ICMUserIdentity *)previous userAttributes:(ICMUserAttributes *)attributes identityStore:(ICMIdentityStore *)store success:(void (^)(void))success;
+ (void)handleIdentifiedUserRegistration:(ICMIdentityStore *)store userAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)updateNexusConfigurationWithIdentityStore:(ICMIdentityStore *)store;
@end
@interface ICMBridge (UnrestoredUserUpdateAPI)
+ (void)updateUserWithAttributes:(NSDictionary *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end
