// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMFloatTextField.h"
#import "ICMFloatValidator.h"
#import "ICMValidator.h"

@implementation ICMFloatTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super initWithProperties:properties delegate:delegate];
    if (self) [self configureDefaults];
    return self;
}
- (void)configureDefaults {
    [super configureDefaults];
    self.keyboardType = UIKeyboardTypeDecimalPad;
}
- (NSArray *)validators { return @[[ICMFloatValidator new]]; }
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    if (![super isUserInputValid:error]) return NO;
    return [ICMValidator validateText:self.text validators:self.validators error:error];
}
@end
