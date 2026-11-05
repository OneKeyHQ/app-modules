// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMPhoneTextField.h"
#import "ICMValidator.h"

@implementation ICMPhoneTextField
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate {
    self = [super initWithProperties:properties delegate:delegate];
    if (self) {
        [self configureDefaults];
        self.phoneValidator = [ICMPhoneValidator new];
        [self.phoneValidator loadCountryCodes];
        [self configureInputViewFromValue:properties.initialValue];
        [self addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    }
    return self;
}
- (void)configureDefaults {
    [super configureDefaults];
    self.keyboardType = UIKeyboardTypePhonePad;
    self.textContentType = UITextContentTypeTelephoneNumber;
}
- (void)configureInputViewFromValue:(id)value {
    ICMPhoneValidator *validator = self.phoneValidator;
    ICMCountryCode *country;
    if (value != nil) country = [validator countryCodeFromPhoneNumber:[self normalizedPhoneNumber:value]];
    else country = [validator countryCodeFromLocale:[[NSLocale currentLocale] objectForKey:NSLocaleCountryCode]];
    [self setInputFlagEmoji:country.emoji];
    NSString *dialCode = country.isUnkown ? @"1" : country.dialCode;
    self.placeholder = [NSString stringWithFormat:@"+%@ %@", dialCode, @"123 456 7890"];
    if (value != nil) self.text = value;
    else if (country.dialCode.length != 0) self.text = [NSString stringWithFormat:@"+%@", country.dialCode];
}
- (NSArray *)validators { return @[self.phoneValidator]; }
- (BOOL)isUserInputValid:(NSError **)error {
    if ([self notRequiredAndEmpty]) return YES;
    if (![super isUserInputValid:error]) return NO;
    return [ICMValidator validateText:self.text validators:self.validators error:error];
}
- (NSString *)normalizedPhoneNumber:(NSString *)number {
    NSString *normalized = [self.phoneValidator normalizePhoneNumber:number];
    return [self.phoneValidator stripPrefix:normalized];
}
- (void)textFieldDidChange:(UITextField *)sender {
    NSString *normalized = [self normalizedPhoneNumber:sender.text];
    if (normalized.length >= 1 && normalized.length <= 6) {
        ICMCountryCode *country = [self.phoneValidator countryCodeFromPhoneNumber:normalized];
        [self setInputFlagEmoji:country.emoji];
    }
}
- (void)setInputFlagEmoji:(NSString *)emoji {
    if ([emoji isEqualToString:self.previousEmoji]) return;
    self.cursorInset = 38;
    UIImageView *view = [UIImageView new];
    view.image = [self imageFromEmoji:emoji];
    view.contentMode = UIViewContentModeCenter;
    self.leftView = view;
    self.leftViewMode = UITextFieldViewModeAlways;
    self.flagView = view;
    self.previousEmoji = emoji;
}
- (UIImage *)imageFromEmoji:(NSString *)emoji {
    UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:CGSizeMake(20, 20)];
    return [renderer imageWithActions:^(UIGraphicsImageRendererContext *context) {
        (void)context;
        [emoji drawInRect:CGRectMake(0, 0, 20, 20) withAttributes:@{NSFontAttributeName: [ICMUtils fontOfSize:16 weight:0]}];
    }];
}
- (CGRect)leftViewRectForBounds:(CGRect)bounds {
    UIView *right = self.rightView;
    UIImageView *flag = self.flagView;
    UIView *view = right == flag ? self.rightView : self.leftView;
    return [self accessoryRectForView:view bounds:bounds];
}
- (CGRect)rightViewRectForBounds:(CGRect)bounds {
    UIView *right = self.rightView;
    UIImageView *flag = self.flagView;
    UIView *view = right == flag ? self.leftView : self.rightView;
    return [self accessoryRectForView:view bounds:bounds];
}
- (CGRect)accessoryRectForView:(UIView *)view bounds:(CGRect)bounds {
    BOOL rtl = self.effectiveUserInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft;
    UIImageView *flag = self.flagView;
    CGFloat width;
    CGFloat x;
    if (view == flag) {
        width = 20;
        x = rtl ? (CGRectGetMaxX(bounds) - 10) - 20 : 10;
    } else {
        CGSize size = [view sizeThatFits:bounds.size];
        width = size.width < bounds.size.width ? size.width : bounds.size.width;
        x = rtl ? CGRectGetMinX(bounds) : CGRectGetMaxX(bounds) - width;
    }
    return CGRectMake(x, 0, width, bounds.size.height);
}
- (CGRect)editingRectForBounds:(CGRect)bounds { return [self getPhoneFieldRect:bounds]; }
- (CGRect)textRectForBounds:(CGRect)bounds { return [self getPhoneFieldRect:bounds]; }
- (CGRect)getPhoneFieldRect:(CGRect)bounds {
    UIView *right = self.rightView;
    UIImageView *flag = self.flagView;
    UIView *accessory = right == flag ? self.leftView : self.rightView;
    CGFloat left = (double)self.cursorInset;
    CGFloat rightInset = accessory.frame.size.width + 10;
    BOOL rtl = self.effectiveUserInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft;
    return CGRectMake(bounds.origin.x + (rtl ? rightInset : left), bounds.origin.y + 0,
                      bounds.size.width - (rightInset + left), bounds.size.height);
}
@end
