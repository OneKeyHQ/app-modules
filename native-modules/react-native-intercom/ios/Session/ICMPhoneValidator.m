#import "ICMPhoneValidator.h"
#import "ICMRootDestinationDependencies.h"
#import "../Identity/ICMUtils.h"

@implementation ICMPhoneValidator
- (void)loadCountryCodes {
    if (self.countryCodes.count != 0) return;
    NSError *error = nil;
    NSArray *items = [ICMUtils jsonArrayFromLocalFile:@"data/intercom_area_codes" error:&error];
    if (error != nil) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Error parsing intercom_area_codes.json");
        return;
    }
    self.countryCodes = [NSMutableArray arrayWithCapacity:items.count];
    for (id item in items) {
        ICMCountryCode *country = [ICMCountryCode instanceFromDictionary:item];
        [self.countryCodes addObject:country];
    }
}
- (NSString *)normalizePhoneNumber:(NSString *)number {
    NSRegularExpression *whitespace = [NSRegularExpression regularExpressionWithPattern:@"\\s+" options:NSRegularExpressionCaseInsensitive error:NULL];
    NSString *withoutWhitespace = [whitespace stringByReplacingMatchesInString:number options:0 range:NSMakeRange(0, number.length) withTemplate:@""];
    NSRegularExpression *punctuation = [NSRegularExpression regularExpressionWithPattern:@"^(\\()|(\\))|(-)" options:NSRegularExpressionCaseInsensitive error:NULL];
    return [punctuation stringByReplacingMatchesInString:withoutWhitespace options:0 range:NSMakeRange(0, withoutWhitespace.length) withTemplate:@""];
}
- (BOOL)containsPrefix:(NSString *)number {
    NSRegularExpression *expression = [NSRegularExpression regularExpressionWithPattern:@"^(\\+)|(00)" options:NSRegularExpressionCaseInsensitive error:NULL];
    NSRange match = [expression rangeOfFirstMatchInString:number options:0 range:NSMakeRange(0, number.length)];
    return match.location == 0;
}
- (NSString *)stripPrefix:(NSString *)number {
    NSRegularExpression *expression = [NSRegularExpression regularExpressionWithPattern:@"^(\\+)|(00)" options:NSRegularExpressionCaseInsensitive error:NULL];
    NSRange match = [expression rangeOfFirstMatchInString:number options:0 range:NSMakeRange(0, number.length)];
    if (match.location == NSNotFound) return number;
    return [number stringByReplacingCharactersInRange:match withString:@""];
}
- (BOOL)containsNonNumericCharacters:(NSString *)number {
    NSRegularExpression *expression = [NSRegularExpression regularExpressionWithPattern:@"\\D" options:NSRegularExpressionCaseInsensitive error:NULL];
    return [expression firstMatchInString:number options:0 range:NSMakeRange(0, number.length)] != nil;
}
- (ICMCountryCode *)countryCodeFromPhoneNumber:(NSString *)number {
    ICMCountryCode *fallback = [ICMCountryCode new];
    for (ICMCountryCode *country in self.countryCodes) {
        if ([number hasPrefix:country.dialCode]) {
            int32_t priority = (int32_t)country.priority;
            if (priority == 0) return country;
            if (priority == 1) fallback = country;
            else if (priority == 2 && [self matchesAreaCodeInPhoneNumber:number countryCode:country]) return country;
        }
    }
    return fallback;
}
- (BOOL)matchesAreaCodeInPhoneNumber:(NSString *)number countryCode:(ICMCountryCode *)countryCode {
    NSString *suffix = [number substringWithRange:NSMakeRange(countryCode.dialCode.length, number.length - countryCode.dialCode.length)];
    for (NSString *areaCode in countryCode.areaCodes) {
        if ([suffix hasPrefix:areaCode]) return YES;
    }
    return NO;
}
- (ICMCountryCode *)countryCodeFromLocale:(NSString *)locale {
    ICMCountryCode *fallback = [ICMCountryCode new];
    for (ICMCountryCode *country in self.countryCodes) {
        if ([locale compare:country.code options:NSCaseInsensitiveSearch] == NSOrderedSame) return country;
    }
    return fallback;
}
- (BOOL)validateText:(NSString *)text error:(NSError **)error {
    NSString *normalized = [self normalizePhoneNumber:text];
    if (![self containsPrefix:normalized]) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"that_number_needs_a_prefix"];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1001 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    NSString *stripped = [self stripPrefix:normalized];
    if ([self containsNonNumericCharacters:stripped]) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"string_is_incorrect"];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1002 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    if (stripped.length >= 16) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"that_number_has_too_many_digits"];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1003 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    ICMCountryCode *country = [self countryCodeFromPhoneNumber:stripped];
    if (country.isUnkown) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"that_country_code_doesnt_look_quite_right"];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1003 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    NSUInteger firstLength = country.dialCode.length;
    NSUInteger totalLength = stripped.length;
    NSUInteger secondLength = country.dialCode.length;
    NSString *suffix = [stripped substringWithRange:NSMakeRange(firstLength, totalLength - secondLength)];
    if (suffix.length <= 5) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"that_number_is_missing_a_few_digits"];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1003 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    return YES;
}
- (BOOL)validateArray:(NSArray *)array error:(NSError **)error { return YES; }
- (BOOL)validateNumber:(NSNumber *)number error:(NSError **)error { return YES; }
@end
