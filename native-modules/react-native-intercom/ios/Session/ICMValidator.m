// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMValidator.h"

@implementation ICMValidator
+ (BOOL)validateText:(NSString *)text validators:(NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError **)error {
    if (validators.count == 0) return YES;
    NSString *trimmedText = [text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    BOOL enumerated = NO;
    for (id<ICMValidatorProtocol> validator in validators) {
        enumerated = YES;
        if (![validator validateText:trimmedText error:error]) return NO;
    }
    return enumerated;
}
+ (BOOL)validateArray:(NSArray *)array validators:(NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError **)error {
    if (validators.count == 0) return YES;
    BOOL enumerated = NO;
    for (id<ICMValidatorProtocol> validator in validators) {
        enumerated = YES;
        if (![validator validateArray:array error:error]) return NO;
    }
    return enumerated;
}
+ (BOOL)validateNumber:(NSNumber *)number validators:(NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError **)error {
    if (validators.count == 0) return YES;
    BOOL enumerated = NO;
    for (id<ICMValidatorProtocol> validator in validators) {
        enumerated = YES;
        if (![validator validateNumber:number error:error]) return NO;
    }
    return enumerated;
}
@end
