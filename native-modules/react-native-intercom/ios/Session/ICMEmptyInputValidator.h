#import "ICMValidator.h"

@interface ICMEmptyInputValidator : NSObject <ICMValidatorProtocol>
- (nonnull NSError *)emptyInputError;
@end
