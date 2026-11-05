// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMUserAttributes.h"
#import "ICMCompany.h"
#import "ICMCustomDataValidation.h"

@interface ICMUserAttributes ()
@property (nonatomic, strong) NSNumber *unsubscribedFromEmailObject;
@end

@implementation ICMUserAttributes
@synthesize email = _email, userId = _userId, name = _name, phone = _phone;
@synthesize languageOverride = _languageOverride, signedUpAt = _signedUpAt;
@synthesize companies = _companies, customAttributes = _customAttributes;
@synthesize unsubscribedFromEmails = _unsubscribedFromEmails;

- (void)setEmail:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"email"]) _email = value;
}
- (void)setUserId:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"userId"]) _userId = value;
}
- (void)setName:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"name"]) _name = value;
}
- (void)setPhone:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"phone"]) _phone = value;
}
- (void)setLanguageOverride:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"languageOverride"]) _languageOverride = value;
}
- (void)setSignedUpAt:(NSDate *)value {
    if ([ICMCustomDataValidation validateDate:value forKey:@"signedUpAt"]) _signedUpAt = value;
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (void)setUnsubscribedFromEmails:(BOOL)value {
    // The original setter updates only the serialization object, not its BOOL ivar.
    _unsubscribedFromEmailObject = @(value);
}
#pragma clang diagnostic pop
- (BOOL)unsubscribedFromEmails { return _unsubscribedFromEmails; }
- (void)setCompanies:(NSArray<ICMCompany *> *)value {
    if ([ICMCustomDataValidation validateCompanies:value forKey:@"companies"]) _companies = value;
}
- (void)setCustomAttributes:(NSDictionary<NSString *, id> *)value {
    if ([ICMCustomDataValidation validateCustomAttributes:value forKey:@"customAttributes"]) {
        _customAttributes = [ICMCustomDataValidation replaceNullAttributesAttributesWithNSNull:value];
    }
}
+ (NSString *)nullStringAttribute { return [ICMCustomDataValidation nullStringAttribute]; }
+ (NSNumber *)nullNumberAttribute { return [ICMCustomDataValidation nullNumberAttribute]; }
+ (NSDate *)nullDateAttribute { return [ICMCustomDataValidation nullDateAttribute]; }
- (BOOL)hasNonIdentifyingAttributes {
    return self.name || self.phone || self.languageOverride || self.signedUpAt || self.companies || self.customAttributes || self.unsubscribedFromEmails;
}
- (NSDictionary<NSString *, id> *)attributes {
    NSMutableDictionary *attributes = [NSMutableDictionary new];
    if (self.email) attributes[@"email"] = self.email;
    if (self.userId) attributes[@"user_id"] = self.userId;
    if (self.name) attributes[@"name"] = self.name;
    if (self.phone) attributes[@"phone"] = self.phone;
    if (self.languageOverride) attributes[@"language_override"] = self.languageOverride;
    if (self.signedUpAt) {
        attributes[@"signed_up_at"] = [self.signedUpAt isEqual:[ICMUserAttributes nullDateAttribute]] ? (id)[ICMUserAttributes nullDateAttribute] : @([self.signedUpAt timeIntervalSince1970]);
    }
    if (self.unsubscribedFromEmailObject) attributes[@"unsubscribed_from_emails"] = @([self.unsubscribedFromEmailObject boolValue]);
    if (self.companies) {
        NSMutableArray *companies = [NSMutableArray new];
        for (ICMCompany *company in self.companies) [companies addObject:[company attributes]];
        attributes[@"companies"] = companies;
    }
    if (self.customAttributes) attributes[@"custom_attributes"] = self.customAttributes;
    return [ICMCustomDataValidation replaceNullAttributesAttributesWithNSNull:attributes];
}
- (NSDictionary<NSNumber *, NSString *> *)registrationAttributes {
    NSMutableDictionary *attributes = [NSMutableDictionary new];
    if (self.userId) attributes[@1] = self.userId;
    if (self.email) attributes[@0] = self.email;
    return attributes;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    return object && [object class] == [self class] && [self isEqualToUserAttributes:object];
}
- (BOOL)isEqualToUserAttributes:(ICMUserAttributes *)other {
    return ((!self.email && !other.email) || [self.email isEqualToString:other.email])
        && ((!self.userId && !other.userId) || [self.userId isEqualToString:other.userId])
        && ((!self.name && !other.name) || [self.name isEqualToString:other.name])
        && ((!self.phone && !other.phone) || [self.phone isEqualToString:other.phone])
        && ((!self.languageOverride && !other.languageOverride) || [self.languageOverride isEqualToString:other.languageOverride])
        && ((!self.signedUpAt && !other.signedUpAt) || [self.signedUpAt isEqual:other.signedUpAt])
        && ((!self.companies && !other.companies) || [self.companies isEqual:other.companies])
        && ((!self.customAttributes && !other.customAttributes) || [self.customAttributes isEqual:other.customAttributes])
        && self.unsubscribedFromEmails == other.unsubscribedFromEmails;
}
- (NSUInteger)hash {
    return self.email.hash ^ self.userId.hash ^ self.name.hash ^ self.phone.hash ^ self.languageOverride.hash ^ self.signedUpAt.hash ^ self.companies.hash ^ self.customAttributes.hash ^ @(self.unsubscribedFromEmails).hash;
}
@end
