// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMUserIdentity.h"
#import "ICMUtils.h"

@implementation ICMUserIdentity
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMUserIdentity *identity = [ICMUserIdentity new];
    [identity setAttributesFromDictionary:dictionary];
    return identity;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary *user = [@{@"type": @"user"} mutableCopy];
    if (!self.anonymousId && self.intercomId) user[@"intercom_id"] = self.intercomId;
    if (self.userId) user[@"user_id"] = self.userId;
    if (self.email) user[@"email"] = self.email;
    if (self.anonymousId) user[@"anonymous_id"] = self.anonymousId;
    if (self.encryptedUserId) user[@"encrypted_user_id"] = self.encryptedUserId;
    NSMutableDictionary *representation = [NSMutableDictionary dictionary];
    representation[@"user"] = user;
    if (self.secureOptions) [representation addEntriesFromDictionary:self.secureOptions];
    return representation;
}
+ (NSString *)generateAnonymousId { return [NSUUID UUID].UUIDString; }
- (BOOL)registerUnidentifiedUser {
    BOOL exists = [self identityExists];
    if (!exists) self.anonymousId = [ICMUserIdentity generateAnonymousId];
    return !exists;
}
- (BOOL)registerIdentifiedUserWithProperties:(NSDictionary<NSNumber *, NSString *> *)properties {
    BOOL identified = [self isIdentified];
    if (!identified) {
        self.intercomId = nil;
        if (properties[@1]) self.userId = properties[@1];
        if (properties[@0]) self.email = properties[@0];
        if (properties[@2]) self.intercomId = properties[@2];
    }
    return !identified;
}
- (BOOL)matchesIdentity:(NSDictionary<NSNumber *, NSString *> *)properties {
    BOOL matches = properties.count > 0;
    if (properties[@1] && matches) matches = [self.userId isEqualToString:properties[@1]];
    if (properties[@0] && matches) matches = [self.email isEqualToString:properties[@0]];
    return matches;
}
- (void)clearIntercomId { self.intercomId = nil; }
- (BOOL)isIdentified { return self.identityExists && !self.isUnidentified; }
- (BOOL)isUnidentified { return _anonymousId.length > 0 && !_email.length && !_userId.length; }
- (BOOL)identityExists { return _anonymousId.length || _userId.length || _intercomId.length || _email.length; }
- (void)updateWithIdentity:(ICMUserIdentity *)identity {
    if (identity.intercomId.length) self.intercomId = identity.intercomId;
    self.userId = identity.userId;
    self.email = identity.email;
    self.anonymousId = identity.anonymousId;
    if (identity.hasConversations) self.hasConversations = identity.hasConversations;
    self.encryptedUserId = identity.encryptedUserId;
}
- (void)setValue:(id)value forKey:(NSString *)key { [super setValue:value forKey:key]; }
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if (![value isKindOfClass:[NSString class]]) return;
    if ([key isEqualToString:@"anonymous_id"]) self.anonymousId = value;
    else if ([key isEqualToString:@"intercom_id"]) self.intercomId = value;
    else if ([key isEqualToString:@"user_id"]) self.userId = value;
    else if ([key isEqualToString:@"encrypted_user_id"]) self.encryptedUserId = value;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ICMUserIdentity class]]) return NO;
    ICMUserIdentity *other = object;
    return ((!other.anonymousId && !self.anonymousId) || [other.anonymousId isEqualToString:self.anonymousId])
        && ((!other.userId && !self.userId) || [other.userId isEqualToString:self.userId])
        && ((!other.intercomId && !self.intercomId) || [other.intercomId isEqualToString:self.intercomId])
        && ((!other.email && !self.email) || [other.email isEqualToString:self.email])
        && ((!other.secureOptions && !self.secureOptions) || [other.secureOptions isEqualToDictionary:self.secureOptions])
        && other.hasConversations == self.hasConversations
        && ((!other.encryptedUserId && !self.encryptedUserId) || [other.encryptedUserId isEqualToString:self.encryptedUserId]);
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.anonymousId forKey:@"anonymousId"];
    [coder encodeObject:self.intercomId forKey:@"intercomId"];
    [coder encodeObject:self.userId forKey:@"userId"];
    [coder encodeObject:self.email forKey:@"email"];
    NSDictionary *options = [[self class] nonSensitiveSecureOptionsFrom:self.secureOptions persistingSensitiveTo:[ICMRecoveredKeychain shared]];
    [coder encodeObject:options forKey:@"secureOptions"];
    [coder encodeBool:self.hasConversations forKey:@"hasConversations"];
    [coder encodeObject:self.encryptedUserId forKey:@"encryptedUserId"];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) {
        self.anonymousId = [coder decodeObjectOfClass:[NSString class] forKey:@"anonymousId"];
        self.intercomId = [coder decodeObjectOfClass:[NSString class] forKey:@"intercomId"];
        self.userId = [coder decodeObjectOfClass:[NSString class] forKey:@"userId"];
        self.email = [coder decodeObjectOfClass:[NSString class] forKey:@"email"];
        NSDictionary *options = [coder decodeObjectOfClasses:[NSSet setWithObjects:[NSDictionary class], [NSString class], nil] forKey:@"secureOptions"];
        self.secureOptions = [[self class] secureOptionsByMergingArchivedDict:options withKeychain:[ICMRecoveredKeychain shared]];
        self.hasConversations = [coder decodeBoolForKey:@"hasConversations"];
        self.encryptedUserId = [coder decodeObjectOfClass:[NSString class] forKey:@"encryptedUserId"];
    }
    return self;
}
+ (NSArray<NSString *> *)sensitiveSecureOptionKeys { return @[@"hmac", @"intercom_user_jwt"]; }
+ (NSDictionary *)nonSensitiveSecureOptionsFrom:(NSDictionary *)options persistingSensitiveTo:(id<ICMIdentityKeychainStoring>)keychain {
    NSMutableDictionary *result = options ? [options mutableCopy] : [NSMutableDictionary dictionary];
    for (NSString *key in [self sensitiveSecureOptionKeys]) {
        id value = result[key];
        if ([value isKindOfClass:[NSString class]] && [value length]) {
            BOOL written = [keychain setString:value forKey:key];
            BOOL logging = [ICMUtils debugLoggingEnabled];
            if (written) {
                if (logging) NSLog(@"[Intercom] ICMUserIdentity: '%@' written to Keychain (%@...)", key, [value substringToIndex:MIN([value length], 4)]);
                [result removeObjectForKey:key];
            } else if (logging) {
                NSLog(@"[Intercom] ICMUserIdentity: '%@' Keychain write failed — retaining in archive", key);
            }
        } else {
            [keychain deleteValueForKey:key];
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMUserIdentity: '%@' cleared from Keychain", key);
            [result removeObjectForKey:key];
        }
    }
    return result.count ? [result copy] : nil;
}
+ (NSDictionary *)secureOptionsByMergingArchivedDict:(NSDictionary *)options withKeychain:(id<ICMIdentityKeychainStoring>)keychain {
    NSMutableDictionary *result = options ? [options mutableCopy] : [NSMutableDictionary dictionary];
    for (NSString *key in [self sensitiveSecureOptionKeys]) {
        NSString *value = [keychain stringForKey:key];
        if (value.length) {
            result[key] = value;
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMUserIdentity: '%@' loaded from Keychain (%@...)", key, [value substringToIndex:MIN(value.length, 4)]);
        } else if (result[key] && [ICMUtils debugLoggingEnabled]) {
            NSLog(@"[Intercom] ICMUserIdentity: '%@' loaded from archive (pre-migration)", key);
        }
    }
    return result.count ? [result copy] : nil;
}
@end
