// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMRegexValidator.h"

@implementation ICMRegexValidator
- (BOOL)validateText:(NSString *)text error:(NSError **)error {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", self.regexPattern];
    BOOL valid = [predicate evaluateWithObject:text];
    if (!valid) {
        NSString *message = self.errorMessage;
        NSDictionary *userInfo = @{NSLocalizedDescriptionKey: message};
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:userInfo];
    }
    return valid;
}
- (BOOL)validateArray:(NSArray *)array error:(NSError **)error { return YES; }
- (BOOL)validateNumber:(NSNumber *)number error:(NSError **)error { return YES; }
- (NSString *)regexPattern { return @""; }
- (NSString *)errorMessage { return @""; }
@end
