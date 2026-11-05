#import "ICMCustomDataValidation.h"
#import "ICMCompany.h"
#import <limits.h>

@implementation ICMCustomDataValidation
+ (BOOL)validateNonEmptyString:(id)value forKey:(NSString *)key {
    if ([value isKindOfClass:[NSString class]] && [value length]) return YES;
    NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be an NSString and can't be nil or empty", key);
    return NO;
}
+ (BOOL)validateDate:(id)value forKey:(NSString *)key {
    if ([value isKindOfClass:[NSDate class]]) return YES;
    NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be a valid NSDate", key);
    return NO;
}
+ (BOOL)validateNumber:(id)value forKey:(NSString *)key {
    if ([value isKindOfClass:[NSNumber class]]) return YES;
    NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be a valid NSNumber", key);
    return NO;
}
+ (BOOL)validateArrayOfStrings:(id)value forKey:(NSString *)key {
    if (![value isKindOfClass:[NSArray class]] || ![value count]) {
        NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be a non empty NSArray of NSString objects", key);
        return NO;
    }
    for (id item in value) {
        if (![self validateNonEmptyString:item forKey:@"Each object in the array"]) return NO;
    }
    return YES;
}
+ (BOOL)validateCompanies:(id)value forKey:(NSString *)key {
    if (![value isKindOfClass:[NSArray class]] || ![value count]) {
        NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be a non empty NSArray of ICMCompany objects", key);
        return NO;
    }
    for (id company in value) {
        if (![company isKindOfClass:[ICMCompany class]]) {
            NSLog(@"[Intercom] INTERCOM ERROR - Each company in '%@' must be a valid ICMCompany object", key);
            return NO;
        }
        if (![company companyId]) {
            NSLog(@"[Intercom] INTERCOM ERROR - Each company in '%@' must have a companyId", key);
            return NO;
        }
    }
    return YES;
}
+ (BOOL)validateCustomAttributes:(id)value forKey:(NSString *)key {
    if (![value isKindOfClass:[NSDictionary class]] || ![value count]) {
        NSLog(@"[Intercom] INTERCOM ERROR - '%@' must be a non empty NSDictionary", key);
        return NO;
    }
    for (id attributeKey in value) {
        if (![attributeKey isKindOfClass:[NSString class]] || ![attributeKey length]) {
            NSLog(@"[Intercom] INTERCOM ERROR - Keys in '%@' must be non empty NSStrings", key);
            return NO;
        }
        id item = [value objectForKeyedSubscript:attributeKey];
        if (![item isKindOfClass:[NSString class]] && ![item isKindOfClass:[NSNumber class]] && ![item isKindOfClass:[NSNull class]]) {
            NSLog(@"[Intercom] INTERCOM ERROR - Each value in '%@' must be of type NSString, NSNumber or NSNull", key);
            return NO;
        }
    }
    return YES;
}
+ (NSDictionary *)replaceNullAttributesAttributesWithNSNull:(NSDictionary *)attributes {
    NSMutableDictionary *result = [attributes mutableCopy];
    for (id key in attributes) {
        id value = attributes[key];
        if (value == [self nullStringAttribute] || value == [self nullNumberAttribute] || value == [self nullDateAttribute]) {
            result[key] = [NSNull null];
        }
    }
    return result;
}
+ (NSString *)nullStringAttribute { return @"kICMNullStringAttribute"; }
+ (NSNumber *)nullNumberAttribute {
    static NSNumber *number;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ number = @(INT_MAX); });
    return number;
}
+ (NSDate *)nullDateAttribute {
    static NSDate *date;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ date = [NSDate distantFuture]; });
    return date;
}
@end
