#import "ICMEmptyInputValidator.h"
#import "ICMRootDestinationDependencies.h"

@implementation ICMEmptyInputValidator
- (BOOL)validateText:(NSString *)text error:(NSError **)error {
    if (text != nil && text.length != 0) return YES;
    *error = [self emptyInputError];
    return NO;
}
- (BOOL)validateArray:(NSArray *)array error:(NSError **)error {
    if (array != nil && array.count != 0) return YES;
    *error = [self emptyInputError];
    return NO;
}
- (BOOL)validateNumber:(NSNumber *)number error:(NSError **)error {
    if (number != nil) return YES;
    *error = [self emptyInputError];
    return NO;
}
- (NSError *)emptyInputError {
    NSString *message = [[ICMTranslationManager messenger] translationForKey:@"surveys_required_response"];
    return [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:@{NSLocalizedDescriptionKey: message}];
}
@end
