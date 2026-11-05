// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
#import "ICMIdentityKeychain.h"
@class IntercomNexusConfiguration;
NS_ASSUME_NONNULL_BEGIN

@interface ICMUserIdentity : NSObject <NSSecureCoding>
@property (nonatomic, strong, nullable) NSString *anonymousId;
@property (nonatomic, strong, nullable) NSString *intercomId;
@property (nonatomic, strong, nullable) NSString *userId;
@property (nonatomic, strong, nullable) NSString *email;
@property (nonatomic, strong, nullable) NSString *encryptedUserId;
@property (nonatomic, strong, nullable) NSDictionary *secureOptions;
@property (nonatomic) BOOL hasConversations;
@property (nonatomic, strong, nullable) IntercomNexusConfiguration *nexusConfiguration;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (NSDictionary *)dictionaryRepresentation;
+ (NSString *)generateAnonymousId;
- (BOOL)registerUnidentifiedUser;
- (BOOL)registerIdentifiedUserWithProperties:(NSDictionary<NSNumber *, NSString *> *)properties;
- (BOOL)matchesIdentity:(NSDictionary<NSNumber *, NSString *> *)properties;
- (void)clearIntercomId;
- (BOOL)isIdentified;
- (BOOL)isUnidentified;
- (BOOL)identityExists;
- (void)updateWithIdentity:(ICMUserIdentity *)identity;
+ (NSArray<NSString *> *)sensitiveSecureOptionKeys;
+ (nullable NSDictionary *)nonSensitiveSecureOptionsFrom:(nullable NSDictionary *)options persistingSensitiveTo:(id<ICMIdentityKeychainStoring>)keychain;
+ (nullable NSDictionary *)secureOptionsByMergingArchivedDict:(nullable NSDictionary *)options withKeychain:(id<ICMIdentityKeychainStoring>)keychain;
@end
NS_ASSUME_NONNULL_END
