#import "ICMCompany.h"
#import "ICMCustomDataValidation.h"

@implementation ICMCompany
@synthesize companyId = _companyId, name = _name, createdAt = _createdAt;
@synthesize monthlySpend = _monthlySpend, plan = _plan, customAttributes = _customAttributes;

- (void)setCompanyId:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"companyId"]) _companyId = value;
}
- (void)setName:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"name"]) _name = value;
}
- (void)setCreatedAt:(NSDate *)value {
    if ([ICMCustomDataValidation validateDate:value forKey:@"createdAt"]) _createdAt = value;
}
- (void)setMonthlySpend:(NSNumber *)value {
    if ([ICMCustomDataValidation validateNumber:value forKey:@"monthlySpend"]) _monthlySpend = value;
}
- (void)setPlan:(NSString *)value {
    if ([ICMCustomDataValidation validateNonEmptyString:value forKey:@"plan"]) _plan = value;
}
- (void)setCustomAttributes:(NSDictionary<NSString *, id> *)value {
    if ([ICMCustomDataValidation validateCustomAttributes:value forKey:@"customAttributes"]) {
        _customAttributes = [ICMCustomDataValidation replaceNullAttributesAttributesWithNSNull:value];
    }
}
+ (NSString *)nullStringAttribute { return [ICMCustomDataValidation nullStringAttribute]; }
+ (NSNumber *)nullNumberAttribute { return [ICMCustomDataValidation nullNumberAttribute]; }
+ (NSDate *)nullDateAttribute { return [ICMCustomDataValidation nullDateAttribute]; }
- (NSDictionary<NSString *, id> *)attributes {
    NSMutableDictionary *attributes = [NSMutableDictionary new];
    if (self.companyId) attributes[@"id"] = self.companyId;
    if (self.name) attributes[@"name"] = self.name;
    if (self.createdAt) {
        attributes[@"created_at"] = [self.createdAt isEqual:[ICMCompany nullDateAttribute]] ? (id)[ICMCompany nullDateAttribute] : @([self.createdAt timeIntervalSince1970]);
    }
    if (self.monthlySpend) attributes[@"monthly_spend"] = self.monthlySpend;
    if (self.plan) attributes[@"plan"] = self.plan;
    if (self.customAttributes) attributes[@"custom_attributes"] = self.customAttributes;
    return [ICMCustomDataValidation replaceNullAttributesAttributesWithNSNull:attributes];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    return object && [object class] == [self class] && [self isEqualToCompany:object];
}
- (BOOL)isEqualToCompany:(ICMCompany *)other {
    return ((!self.companyId && !other.companyId) || [self.companyId isEqualToString:other.companyId])
        && ((!self.name && !other.name) || [self.name isEqualToString:other.name])
        && ((!self.createdAt && !other.createdAt) || [self.createdAt isEqual:other.createdAt])
        && ((!self.monthlySpend && !other.monthlySpend) || [self.monthlySpend isEqual:other.monthlySpend])
        && ((!self.plan && !other.plan) || [self.plan isEqualToString:other.plan])
        && ((!self.customAttributes && !other.customAttributes) || [self.customAttributes isEqual:other.customAttributes]);
}
- (NSUInteger)hash {
    return self.companyId.hash ^ self.name.hash ^ self.createdAt.hash ^ self.monthlySpend.hash ^ self.plan.hash ^ self.customAttributes.hash;
}
@end
