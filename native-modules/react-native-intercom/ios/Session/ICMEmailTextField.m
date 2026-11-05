// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMEmailTextField.h"
#import "ICMEmailValidator.h"
#import "ICMValidator.h"

@implementation ICMEmailTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super initWithProperties:properties delegate:delegate];
    if (self) [self configureDefaults];
    return self;
}
- (void)configureDefaults {
    [super configureDefaults];
    self.placeholder = @"email@domain.com";
    self.keyboardType = UIKeyboardTypeEmailAddress;
    self.autocapitalizationType = UITextAutocapitalizationTypeNone;
    self.textContentType = UITextContentTypeEmailAddress;
}
- (NSArray *)validators { return @[[ICMEmailValidator new]]; }
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    if (![super isUserInputValid:error]) return NO;
    return [ICMValidator validateText:self.text validators:self.validators error:error];
}
@end
