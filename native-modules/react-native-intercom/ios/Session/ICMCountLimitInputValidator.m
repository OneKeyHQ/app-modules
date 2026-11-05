// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMCountLimitInputValidator.h"
#import "ICMRootDestinationDependencies.h"

@interface ICMTranslationManager (SurveyValidatorParameters)
- (NSString *)translationForKey:(NSString *)key params:(NSDictionary<NSString *, NSString *> *)params;
@end

@implementation ICMCountLimitInputValidator
- (instancetype)initWithMinimumCount:(NSInteger)minimumCount maximumCount:(NSInteger)maximumCount {
    self = [super init];
    if (self) {
        _minimumCount = minimumCount;
        _maximumCount = maximumCount;
    }
    return self;
}
- (BOOL)validateText:(NSString *)text error:(NSError **)error {
    if (text.length > (NSUInteger)self.maximumCount) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"surveys_response_too_long"
            params:@{@"character_count": [NSString stringWithFormat:@"%ld", (long)self.maximumCount]}];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    if (text.length < (NSUInteger)self.minimumCount) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"surveys_response_too_short"
            params:@{@"character_count": [NSString stringWithFormat:@"%ld", (long)self.minimumCount]}];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    return YES;
}
- (BOOL)validateArray:(NSArray *)array error:(NSError **)error {
    if (array.count > (NSUInteger)self.maximumCount) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"surveys_multi_select_too_many_responses"
            params:@{@"response_count": [NSString stringWithFormat:@"%ld", (long)self.maximumCount]}];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    if (array.count < (NSUInteger)self.minimumCount) {
        NSString *message = [[ICMTranslationManager messenger] translationForKey:@"surveys_multi_select_too_few_responses"
            params:@{@"response_count": [NSString stringWithFormat:@"%ld", (long)self.minimumCount]}];
        *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:1000 userInfo:@{NSLocalizedDescriptionKey: message}];
        return NO;
    }
    return YES;
}
- (BOOL)validateNumber:(NSNumber *)number error:(NSError **)error { return YES; }
@end
