// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMValidator.h"

@interface ICMEmptyInputValidator : NSObject <ICMValidatorProtocol>
- (nonnull NSError *)emptyInputError;
@end
