// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
#import "ICMAppIdentity.h"
#import "../Identity/ICMUserIdentity.h"
#import "../Identity/ICMDeviceTokenData.h"
NS_ASSUME_NONNULL_BEGIN
@class ICMUserAttributes;
@interface ICMIdentityStore : NSObject <NSSecureCoding>
@property (nonatomic, strong) ICMAppIdentity *appIdentity;
@property (nonatomic, strong) ICMUserIdentity *userIdentity;
@property (nonatomic, strong, nullable) ICMUserIdentity *previousUserIdentity;
@property (nonatomic, strong) ICMDeviceTokenData *deviceTokenData;
@property (nonatomic) BOOL isUserAuthenticatedWithAPI;
@property (nonatomic, strong, nullable) NSString *previousUserIdentityDeviceToken;
+ (instancetype)currentIdentity;
+ (nullable instancetype)identityFromDisk;
+ (BOOL)saveIdentityStore:(ICMIdentityStore *)store;
+ (NSString *)archivePath;
+ (NSString *)backupArchivePath;
- (BOOL)saveToDisk;
+ (NSString *)hexTokenFromDeviceToken:(nullable NSData *)token;
- (BOOL)setDeviceTokenData:(nullable NSData *)token environment:(NSUInteger)environment failure:(nullable void (^)(NSError *))failure;
- (BOOL)registerUnidentifiedUser;
- (void)usePreviousIdentity;
- (BOOL)registerIdentifiedUserWithProperties:(ICMUserAttributes *)properties;
- (void)resetUserIdentity;
- (void)updateUserIdentityWith:(ICMUserIdentity *)identity;
- (void)updateDeviceTokenSubmitted:(BOOL)submitted;
- (void)clearPreviousIdentityWithDelay:(NSTimeInterval)delay;
- (void)clearPreviousIdentity;
- (BOOL)updateSecureOptions:(nullable NSDictionary *)options;
- (nullable NSArray *)userFingerprint;
- (nullable NSString *)userJwtForFingerprint:(nullable NSArray *)fingerprint;
- (BOOL)applyRefreshedUserJwt:(nullable NSString *)jwt replacing:(NSString *)staleJwt forFingerprint:(nullable NSArray *)fingerprint;
- (void)createDeviceTokenDataIfNoneExists:(NSCoder *)coder;
@end
NS_ASSUME_NONNULL_END
