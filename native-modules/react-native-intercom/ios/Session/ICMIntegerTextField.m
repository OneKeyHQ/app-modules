// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMIntegerTextField.h"
#import "ICMIntegerValidator.h"
#import "ICMValidator.h"

@implementation ICMIntegerTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super initWithProperties:properties delegate:delegate];
    if (self) [self configureDefaults];
    return self;
}
- (void)configureDefaults {
    [super configureDefaults];
    self.keyboardType = UIKeyboardTypeNumberPad;
}
- (NSArray *)validators { return @[[ICMIntegerValidator new]]; }
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    if (![super isUserInputValid:error]) return NO;
    return [ICMValidator validateText:self.text validators:self.validators error:error];
}
@end
