#import "ICMTextField.h"
#import "ICMSubmitButton.h"
#import "ICMValidator.h"
#import "ICMEmptyInputValidator.h"
#import "ICMCountLimitInputValidator.h"

@implementation ICMTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super init];
    if (self) {
        self.accessibilityIdentifier = @"intercom textfield";
        _value = properties.initialValue;
        _inputViewDelegate = delegate;
        _cursorInset = 10;
        _allowSubmission = properties.allowSubmission;
        _fieldIsEditable = properties.valueIsEditable;
        [self configureDefaults];
        [self addTextFieldNotifications];
        if (properties.allowSubmission) [self createSubmitButton];
    }
    return self;
}
- (void)configureDefaults {
    if (self.allowSubmission) {
        self.returnKeyType = UIReturnKeySend;
        self.rightViewMode = UITextFieldViewModeAlways;
    } else {
        self.returnKeyType = UIReturnKeyDone;
        self.rightViewMode = UITextFieldViewModeUnlessEditing;
    }
    self.font = [ICMUtils fontOfSize:15 weight:0];
    self.autocorrectionType = UITextAutocorrectionTypeNo;
    self.backgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.inputAlt;
    self.layer.cornerRadius = 8;
    self.layer.borderWidth = 1;
    self.layer.borderColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder.CGColor;
    self.layer.masksToBounds = YES;
    self.clipsToBounds = YES;
    if (self.value != nil) {
        if ([self.value isKindOfClass:NSString.class]) self.text = self.value;
        else if ([self.value isKindOfClass:NSNumber.class]) self.text = [NSString stringWithFormat:@"%@", self.value];
        else self.text = @"";
    }
    self.enabled = self.fieldIsEditable;
}
- (id)inputValue { return self.text; }
- (void)setInputValue:(id)value {
    if ([value isKindOfClass:NSString.class] || value == nil) {
        self.text = value;
        [self showValidInputCheckmarkIfRelevant];
    }
}
- (NSArray *)validators { return @[]; }
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    NSMutableArray *validators = [NSMutableArray array];
    if (self.isRequired) [validators addObject:[ICMEmptyInputValidator new]];
    if (self.characterLimit != 0) [validators addObject:[[ICMCountLimitInputValidator alloc] initWithMinimumCount:0 maximumCount:self.characterLimit]];
    return [ICMValidator validateText:self.text validators:validators error:error];
}
- (BOOL)notRequiredAndEmpty {
    if (self.isRequired) return NO;
    return self.text == nil || self.text.length == 0;
}
- (void)configureSuccessState {
    [self.submitButton showSubmittedState];
    self.submitButton.isAccessibilityElement = NO;
    self.enabled = NO;
}
- (void)configureErrorState {
    self.enabled = self.fieldIsEditable;
    self.submitButton.isAccessibilityElement = YES;
    [self.submitButton resetButtonState];
    self.layer.borderColor = [ICMRecoveredDefaultThemeProvider shared].theme.textError.CGColor;
}
- (void)configureLoadingState {
    self.enabled = NO;
    self.submitButton.isAccessibilityElement = YES;
    [self.submitButton showLoadingState];
}
- (void)configureDefaultState {
    self.enabled = self.fieldIsEditable;
    self.submitButton.isAccessibilityElement = YES;
    self.layer.borderColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder.CGColor;
}
- (void)configureDisabledState {
    self.enabled = NO;
    self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
    self.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.iconMuted;
    self.submitButton.isAccessibilityElement = YES;
    self.submitButton.enabled = NO;
}
- (CGRect)editingRectForBounds:(CGRect)bounds { return [self getTextFieldRect:bounds]; }
- (CGRect)placeholderRectForBounds:(CGRect)bounds { return [self getTextFieldRect:bounds]; }
- (CGRect)textRectForBounds:(CGRect)bounds { return [self getTextFieldRect:bounds]; }
- (CGRect)getTextFieldRect:(CGRect)bounds {
    CGFloat clearWidth = [self isClearButtonVisible] ? [self clearButtonRectForBounds:bounds].size.width : 0;
    UIView *rightView = self.rightView;
    CGFloat accessoryWidth = rightView != nil ? rightView.frame.size.width : self.leftView.frame.size.width;
    CGFloat right = clearWidth + (accessoryWidth + (double)self.cursorInset);
    BOOL rtl = self.effectiveUserInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft;
    CGFloat left = (double)self.cursorInset;
    return CGRectMake(bounds.origin.x + (rtl ? right : left), bounds.origin.y + 0,
                      bounds.size.width - (right + left), bounds.size.height);
}
- (CGRect)rightViewRectForBounds:(CGRect)bounds {
    CGRect result = [super rightViewRectForBounds:bounds];
    if (self.effectiveUserInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft) result.origin.x = CGRectGetMinX(bounds);
    else result.origin.x = CGRectGetMaxX(bounds) - CGRectGetWidth(result);
    return result;
}
- (BOOL)isClearButtonVisible {
    switch (self.clearButtonMode) {
        case UITextFieldViewModeAlways: return YES;
        case UITextFieldViewModeUnlessEditing: return !self.isEditing;
        case UITextFieldViewModeWhileEditing: return self.isEditing;
        default: return NO;
    }
}
- (void)createSubmitButton {
    self.submitButton = [ICMSubmitButton new];
    if (self.fieldIsEditable) self.submitButton.enabled = self.fieldIsEditable;
    else [self.submitButton showSubmittedState];
    [self.submitButton addTarget:self action:@selector(submitButtonTapped) forControlEvents:UIControlEventTouchUpInside];
    self.rightView = self.submitButton;
    self.rightViewMode = UITextFieldViewModeAlways;
}
- (void)submitButtonTapped { [self.inputViewDelegate entryCompleteButtonTapped]; }
- (void)addTextFieldNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didBeginEditing) name:UITextFieldTextDidBeginEditingNotification object:self];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textDidChange) name:UITextFieldTextDidChangeNotification object:self];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didEndEditing) name:UITextFieldTextDidEndEditingNotification object:self];
}
- (void)didBeginEditing { [self.inputViewDelegate inputFieldDidBeginEditing]; }
- (void)textDidChange { if (self.fieldIsEditable) [self.submitButton showEnabledState]; }
- (void)didEndEditing {
    [self.inputViewDelegate inputFieldDidEndEditing];
    NSUInteger length = self.text.length;
    id<ICMInputViewDelegate> delegate = self.inputViewDelegate;
    if (length != 0) {
        [delegate valueUpdated:self.text];
        [self showValidInputCheckmarkIfRelevant];
    } else {
        [delegate valueUpdated:nil];
    }
}
- (void)showValidInputCheckmarkIfRelevant {
    if (self.allowSubmission) return;
    NSError *error = nil;
    if ([ICMValidator validateText:self.text validators:self.validators error:&error]) {
        if (self.submitButton == nil) self.submitButton = [ICMSubmitButton new];
        self.submitButton.enabled = NO;
        self.rightView = self.submitButton;
    } else {
        self.rightView = nil;
    }
}
- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    if (previousTraitCollection.userInterfaceStyle != self.traitCollection.userInterfaceStyle) {
        self.layer.borderColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder.CGColor;
    }
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
@end
