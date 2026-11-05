#import <Foundation/Foundation.h>

@interface ICMCustomDataValidation : NSObject
+ (BOOL)validateNonEmptyString:(id)value forKey:(NSString *)key;
+ (BOOL)validateDate:(id)value forKey:(NSString *)key;
+ (BOOL)validateNumber:(id)value forKey:(NSString *)key;
+ (BOOL)validateArrayOfStrings:(id)value forKey:(NSString *)key;
+ (BOOL)validateCompanies:(id)value forKey:(NSString *)key;
+ (BOOL)validateCustomAttributes:(id)value forKey:(NSString *)key;
+ (NSDictionary *)replaceNullAttributesAttributesWithNSNull:(NSDictionary *)attributes;
+ (NSString *)nullStringAttribute;
+ (NSNumber *)nullNumberAttribute;
+ (NSDate *)nullDateAttribute;
@end
