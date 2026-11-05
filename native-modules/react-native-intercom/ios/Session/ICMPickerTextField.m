// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMPickerTextField.h"
#import "ICMSubmitButton.h"
#import "ICMEmptyInputValidator.h"
#import "ICMValidator.h"
#import "ICMAvatarDependencies.h"

@interface ICMRecoveredColorHelper (OriginalSurveyInputColor)
+ (UIColor *)suitableForegroundColorFor:(UIColor *)color theme:(ICMRecoveredTheme *)theme;
@end

@implementation ICMPickerTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties options:(NSArray *)options delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super initWithProperties:properties delegate:delegate];
    if (self) {
        self.accessibilityIdentifier = @"intercom picker question view";
        _options = [options mutableCopy];
        [self configureDefaults];
        [self addTextFieldNotificationsToUpdateColors];
    }
    return self;
}
- (void)configureDefaults {
    [super configureDefaults];
    [self.options insertObject:[[ICMTranslationManager messenger] translationForKey:@"choose_one"] atIndex:0];
    self.placeholder = [[ICMTranslationManager messenger] translationForKey:@"choose_one"];
    [self configurePicker];
    [self configureDropDownIconWithTintColor:nil];
    self.normalStatePlaceholderTextColor = [self.attributedPlaceholder attributesAtIndex:0 effectiveRange:NULL][NSForegroundColorAttributeName];
}
- (void)configurePicker {
    self.picker = [UIPickerView new];
    self.picker.delegate = self;
    self.cancelToolbarButton = [[UIBarButtonItem alloc] initWithTitle:[[ICMTranslationManager messenger] translationForKey:@"cancel"]
        style:UIBarButtonItemStyleDone target:self action:@selector(cancelTapped:)];
    self.cancelToolbarButton.accessibilityIdentifier = @"intercom toolbar cancel button";
    UIBarButtonItem *space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
    if (self.allowSubmission) {
        self.entryCompleteToolbarButton = [[UIBarButtonItem alloc] initWithTitle:[[ICMTranslationManager messenger] translationForKey:@"submit"]
            style:UIBarButtonItemStyleDone target:self action:@selector(entryCompleteButtonTapped:)];
    } else {
        self.entryCompleteToolbarButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone
            target:self action:@selector(entryCompleteButtonTapped:)];
    }
    self.entryCompleteToolbarButton.accessibilityIdentifier = @"intercom toolbar entry complete button";
    UIToolbar *toolbar = [UIToolbar new];
    toolbar.barStyle = UIBarStyleDefault;
    toolbar.items = [NSArray arrayWithObjects:self.cancelToolbarButton, space, self.entryCompleteToolbarButton, nil];
    toolbar.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
    UIToolbarAppearance *appearance = [UIToolbarAppearance new];
    toolbar.standardAppearance = appearance;
    toolbar.compactAppearance = appearance;
    [toolbar sizeToFit];
    self.inputView = self.picker;
    self.inputAccessoryView = toolbar;
}
- (void)dropdownIcon:(UIColor *)tint {
    UIImageView *view = [UIImageView new];
    UIImage *image = [ICMAssets pickerSelector];
    if (tint != nil) image = [image imageWithTintColor:tint];
    view.image = image;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    [view.widthAnchor constraintEqualToConstant:40].active = YES;
    view.contentMode = UIViewContentModeScaleAspectFit;
    self.rightView = view;
    self.rightViewMode = UITextFieldViewModeAlways;
}
- (void)configureDropDownIconWithTintColor:(UIColor *)color {
    if (self.allowSubmission) {
        [self.submitButton setBackgroundImage:[ICMAssets submitButtonBackgroundImageClear] forState:UIControlStateNormal];
        [self.submitButton setImage:[ICMAssets pickerSelector] forState:UIControlStateNormal];
        self.submitButton.userInteractionEnabled = NO;
    } else {
        [self dropdownIcon:color];
    }
}
- (void)configureErrorState {
    self.entryCompleteToolbarButton.enabled = YES;
    self.cancelToolbarButton.enabled = YES;
    [self configureDropDownIconWithTintColor:nil];
    [self.submitButton.imageView.layer removeAllAnimations];
}
- (void)configureDisabledState {
    self.enabled = NO;
    self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
    [self dropdownIcon:[ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis];
}
- (void)setInputValue:(id)value {
    [super setInputValue:value];
    [self configureDropDownIconWithTintColor:nil];
}
- (void)didEndEditing {
    NSUInteger length = self.text.length;
    id<ICMInputViewDelegate> delegate = self.inputViewDelegate;
    if (length != 0) [delegate valueUpdated:self.text];
    else [delegate valueUpdated:nil];
    [self.inputViewDelegate inputFieldDidEndEditing];
}
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    NSMutableArray *validators = [NSMutableArray array];
    if (self.isRequired) [validators addObject:[ICMEmptyInputValidator new]];
    return [ICMValidator validateText:self.text validators:validators error:error];
}
- (void)addTextFieldNotificationsToUpdateColors {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setEditingStateColors) name:UITextFieldTextDidBeginEditingNotification object:self];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setNormalStateColors) name:UITextFieldTextDidEndEditingNotification object:self];
}
- (void)configureForEditingStateBackgroundColor:(UIColor *)color {
    self.selectedStateBackgroundColor = color;
    self.selectedStateTextColor = [ICMRecoveredColorHelper suitableForegroundColorFor:color theme:[ICMRecoveredDefaultThemeProvider shared].theme];
    self.selectedStateBorderColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder;
    self.selectedStateDropDownIconColor = self.selectedStateTextColor;
}
- (void)setNormalStateColors {
    self.backgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.base;
    self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
    if (self.normalStatePlaceholderTextColor != nil) {
        self.attributedPlaceholder = [[NSAttributedString alloc] initWithString:self.placeholder
            attributes:@{NSForegroundColorAttributeName: self.normalStatePlaceholderTextColor}];
    }
    self.layer.borderColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder.CGColor;
    [self configureDropDownIconWithTintColor:nil];
}
- (void)setEditingStateColors {
    self.backgroundColor = self.selectedStateBackgroundColor;
    self.textColor = self.selectedStateTextColor;
    if (self.selectedStateTextColor != nil) {
        self.attributedPlaceholder = [[NSAttributedString alloc] initWithString:self.placeholder
            attributes:@{NSForegroundColorAttributeName: self.selectedStateTextColor}];
    }
    if (self.selectedStateBorderColor != nil) self.layer.borderColor = self.selectedStateBorderColor.CGColor;
    [self configureDropDownIconWithTintColor:self.selectedStateDropDownIconColor];
}
- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    if (previousTraitCollection.userInterfaceStyle != self.traitCollection.userInterfaceStyle) {
        if (self.isEditing) [self setEditingStateColors];
        else [self setNormalStateColors];
    }
}
- (void)entryCompleteButtonTapped:(id)sender {
    if (self.allowSubmission) {
        self.entryCompleteToolbarButton.enabled = NO;
        self.cancelToolbarButton.enabled = NO;
        [self.submitButton showLoadingState];
        UIColor *tint = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
        self.submitButton.imageView.tintColor = tint;
    }
    [self.inputViewDelegate entryCompleteButtonTapped];
}
- (void)cancelTapped:(id)sender {
    self.text = @"";
    [self endEditing:YES];
    [self.inputViewDelegate cancelButtonTapped];
    [self.picker selectRow:0 inComponent:0 animated:NO];
    [self.inputViewDelegate valueUpdated:nil];
}
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView { return 1; }
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component { return self.options.count; }
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    self.text = row >= 1 ? [self.options objectAtIndex:(NSUInteger)row] : nil;
}
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return [self.options objectAtIndex:(NSUInteger)row];
}
- (NSAttributedString *)pickerView:(UIPickerView *)pickerView attributedTitleForRow:(NSInteger)row forComponent:(NSInteger)component {
    if (row != 0) return nil;
    NSString *text = [self.options objectAtIndex:0];
    UIFont *font = [ICMUtils fontOfSize:16 weight:3];
    UIColor *color = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
    return [[NSAttributedString alloc] initWithString:text attributes:@{NSFontAttributeName: font, NSForegroundColorAttributeName: color}];
}
@end
