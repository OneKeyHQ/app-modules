#import "ICMDateTextField.h"
#import "ICMSubmitButton.h"
#import "ICMAssets.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMWindowDependencies.h"
#import "ICMValidator.h"
#import "ICMEmptyInputValidator.h"
@implementation ICMDateTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties allowTimeSelection:(BOOL)allowTimeSelection delegate:(id<ICMInputViewDelegate>)delegate {
    return [[ICMDateTextField alloc] initWithProperties:properties allowTimeSelection:allowTimeSelection delegate:delegate dateFormatter:[ICMDateTextField defaultDateFormatter]];
}
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties allowTimeSelection:(BOOL)allowTimeSelection delegate:(id<ICMInputViewDelegate>)delegate dateFormatter:(NSDateFormatter *)formatter {
    id initial = properties.initialValue;
    if (initial) {
        NSDate *date = [NSDate dateWithTimeIntervalSince1970:[initial doubleValue]];
        NSString *text = [formatter stringFromDate:date];
        properties = [[ICMRecoveredInputProperties alloc] initWithInitialValue:text allowSubmission:properties.allowSubmission valueIsEditable:properties.valueIsEditable];
    }
    self = [super initWithProperties:properties delegate:delegate];
    if (self) {
        self.dateFormatter = formatter;
        self.allowTimeSelection = allowTimeSelection;
        [self configureDefaults];
        [self addTarget:self action:@selector(textFieldDidBeginEditing:) forControlEvents:UIControlEventEditingDidBegin];
    }
    return self;
}
+ (NSDateFormatter *)defaultDateFormatter {
    NSDateFormatter *formatter = [NSDateFormatter new];
    formatter.locale = [NSLocale currentLocale];
    formatter.dateStyle = NSDateFormatterMediumStyle;
    formatter.timeStyle = NSDateFormatterNoStyle;
    return formatter;
}
- (void)configureDefaults {
    [super configureDefaults];
    self.placeholder = [[ICMTranslationManager messenger] translationForKey:@"choose_the_date"];
    [self configurePicker];
    [self configureDateIcon];
}
- (void)configurePicker {
    self.datePicker = [UIDatePicker new];
    self.datePicker.datePickerMode = self.allowTimeSelection ? UIDatePickerModeDateAndTime : UIDatePickerModeDate;
    self.datePicker.preferredDatePickerStyle = UIDatePickerStyleWheels;
    [self.datePicker addTarget:self action:@selector(dateChanged:) forControlEvents:UIControlEventValueChanged];
    self.cancelToolbarButton = [[UIBarButtonItem alloc] initWithTitle:[[ICMTranslationManager messenger] translationForKey:@"cancel"] style:UIBarButtonItemStyleDone target:self action:@selector(cancelTapped:)];
    self.cancelToolbarButton.accessibilityIdentifier = @"intercom toolbar cancel button";
    UIBarButtonItem *space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:NULL];
    UIBarButtonItem *submit;
    if (self.allowSubmission) submit = [[UIBarButtonItem alloc] initWithTitle:[[ICMTranslationManager messenger] translationForKey:@"submit"] style:UIBarButtonItemStyleDone target:self action:@selector(entryCompleteButtonTapped:)];
    else submit = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(entryCompleteButtonTapped:)];
    self.entryCompleteToolbarButton = submit;
    self.entryCompleteToolbarButton.accessibilityIdentifier = @"intercom toolbar submit button";
    UIToolbar *toolbar = [UIToolbar new];
    toolbar.barStyle = UIBarStyleDefault;
    toolbar.items = [NSArray arrayWithObjects:self.cancelToolbarButton, space, self.entryCompleteToolbarButton, nil];
    toolbar.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
    UIToolbarAppearance *appearance = [UIToolbarAppearance new];
    toolbar.standardAppearance = appearance;
    toolbar.compactAppearance = appearance;
    [toolbar sizeToFit];
    self.inputView = self.datePicker;
    self.inputAccessoryView = toolbar;
}
- (void)configureDateIcon {
    if (self.allowSubmission) {
        [self.submitButton setBackgroundImage:[ICMAssets submitButtonBackgroundImageClear] forState:UIControlStateNormal];
        [self.submitButton setImage:[ICMAssets dateSelector] forState:UIControlStateNormal];
        BOOL editable = self.fieldIsEditable;
        self.submitButton.userInteractionEnabled = editable;
    } else {
        UIImageView *view = [UIImageView new];
        view.image = [ICMAssets dateSelector];
        view.translatesAutoresizingMaskIntoConstraints = NO;
        [view.widthAnchor constraintEqualToConstant:40].active = YES;
        view.contentMode = UIViewContentModeScaleAspectFit;
        self.rightView = view;
        self.rightViewMode = UITextFieldViewModeAlways;
    }
}
- (void)configureErrorState {
    self.entryCompleteToolbarButton.enabled = YES;
    self.cancelToolbarButton.enabled = YES;
    [self configureDateIcon];
    [self.submitButton.imageView.layer removeAllAnimations];
}
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    NSMutableArray *validators = [[NSArray array] mutableCopy];
    if (self.isRequired) [validators addObject:[ICMEmptyInputValidator new]];
    return [ICMValidator validateText:self.text validators:validators error:error];
}
- (void)textFieldDidBeginEditing:(id)sender {
    (void)sender;
    if (self.text.length != 0) return;
    self.text = [self formattedStringFromDate:self.datePicker.date];
}
- (void)dateChanged:(UIDatePicker *)picker {
    self.text = [self formattedStringFromDate:picker.date];
    NSTimeInterval timestamp = picker.date.timeIntervalSince1970;
    id<ICMInputViewDelegate> delegate = self.inputViewDelegate;
    [delegate valueUpdated:@(timestamp)];
}
- (NSString *)formattedStringFromDate:(NSDate *)date { return [self.dateFormatter stringFromDate:date]; }
- (id)inputValue { return @(self.datePicker.date.timeIntervalSince1970); }
- (void)setInputValue:(id)value {
    [super setInputValue:value];
    if ([value isKindOfClass:[NSNumber class]]) {
        NSDate *date = [NSDate dateWithTimeIntervalSince1970:[value doubleValue]];
        self.text = [self formattedStringFromDate:date];
        self.datePicker.date = date;
    }
    [self configureDateIcon];
}
- (void)entryCompleteButtonTapped:(id)sender {
    (void)sender;
    if (self.allowSubmission) {
        self.entryCompleteToolbarButton.enabled = NO;
        self.cancelToolbarButton.enabled = NO;
        [self.submitButton showLoadingState];
        UIColor *color = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
        self.submitButton.imageView.tintColor = color;
    }
    [self.inputViewDelegate entryCompleteButtonTapped];
}
- (void)cancelTapped:(id)sender {
    (void)sender;
    self.text = @"";
    [self endEditing:YES];
    [self.inputViewDelegate cancelButtonTapped];
    UIDatePicker *picker = self.datePicker;
    picker.date = [NSDate date];
    [self.inputViewDelegate valueUpdated:nil];
}
@end
