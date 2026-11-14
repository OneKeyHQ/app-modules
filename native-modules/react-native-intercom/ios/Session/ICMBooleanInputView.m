#import "ICMBooleanInputView.h"
#import "ICMAutoLayout.h"
#import "ICMAssets.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMValidator.h"
#import "ICMEmptyInputValidator.h"
@implementation ICMBooleanInputView
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate options:(NSUInteger)options {
    self = [super init];
    if (self) {
        _value = properties.initialValue;
        _fieldIsEditable = properties.valueIsEditable;
        _inputViewDelegate = delegate;
        _showLoading = (options & 1) != 0;
        _verticalLayout = (options & 2) != 0;
        _disableAfterSelect = (options & 4) != 0;
        [self buildButtons];
        [self configureDefaults];
    }
    return self;
}
- (void)buildButtons {
    [self buildTrueButton];
    [self buildFalseButton];
    if (!self.verticalLayout) [self buildDivider];
    [self layoutButtons];
}
- (UIButton *)buildBooleanButton:(NSString *)title backgroundImage:(UIImage *)image backgroundImageSelected:(UIImage *)selected tapSelector:(SEL)selector {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    NSAttributedString *normal = [[NSAttributedString alloc] initWithString:title attributes:@{NSFontAttributeName:[ICMUtils fontOfSize:16 weight:0], NSForegroundColorAttributeName:[UIColor blackColor]}];
    [button setAttributedTitle:normal forState:UIControlStateNormal];
    NSAttributedString *selectedTitle = [[NSAttributedString alloc] initWithString:title attributes:@{NSFontAttributeName:[ICMUtils fontOfSize:16 weight:5], NSForegroundColorAttributeName:[UIColor blackColor]}];
    [button setAttributedTitle:selectedTitle forState:UIControlStateSelected];
    NSAttributedString *disabled = [[NSAttributedString alloc] initWithString:title attributes:@{NSFontAttributeName:[ICMUtils fontOfSize:16 weight:0], NSForegroundColorAttributeName:[UIColor colorWithRed:0.53 green:0.53 blue:0.53 alpha:1]}];
    [button setAttributedTitle:disabled forState:UIControlStateDisabled];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button setBackgroundImage:selected forState:UIControlStateSelected];
    [button addTarget:self action:selector forControlEvents:UIControlEventTouchUpInside];
    button.userInteractionEnabled = self.fieldIsEditable;
    return button;
}
- (void)buildTrueButton {
    NSString *title = [[ICMTranslationManager messenger] translationForKey:@"attribute_collector_positive"];
    UIImage *image = self.verticalLayout ? [ICMAssets booleanButtonImage] : [ICMAssets booleanButtonLeftImage];
    UIImage *selected = self.verticalLayout ? [ICMAssets booleanButtonImageSelected] : [ICMAssets booleanButtonLeftImageSelected];
    self.trueButton = [self buildBooleanButton:title backgroundImage:image backgroundImageSelected:selected tapSelector:@selector(trueButtonTapped)];
    self.trueButton.accessibilityIdentifier = @"intercom boolean button Yes";
    [self addSubview:self.trueButton];
}
- (void)buildFalseButton {
    NSString *title = [[ICMTranslationManager messenger] translationForKey:@"attribute_collector_negative"];
    UIImage *image = self.verticalLayout ? [ICMAssets booleanButtonImage] : [ICMAssets booleanButtonRightImage];
    UIImage *selected = self.verticalLayout ? [ICMAssets booleanButtonImageSelected] : [ICMAssets booleanButtonRightImageSelected];
    self.falseButton = [self buildBooleanButton:title backgroundImage:image backgroundImageSelected:selected tapSelector:@selector(falseButtonTapped)];
    self.falseButton.accessibilityIdentifier = @"intercom boolean button No";
    [self addSubview:self.falseButton];
}
- (void)buildDivider {
    self.dividerView = [UIView new];
    self.dividerView.backgroundColor = [UIColor colorWithRed:0.88 green:0.88 blue:0.88 alpha:1];
    [self addSubview:self.dividerView];
    [ICMAutoLayout constrainView:self.dividerView toSize:CGSizeMake(1, 40)];
    [ICMAutoLayout centerView:self.dividerView inContainerOnAxis:NSLayoutAttributeCenterX];
    [ICMAutoLayout pinView:self.dividerView edges:1 toSameEdgesOfView:self];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    if (self.verticalLayout) return;
    UIButton *first;
    UIButton *second;
    if (self.effectiveUserInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft) { first = self.falseButton; second = self.trueButton; }
    else { first = self.trueButton; second = self.falseButton; }
    [first setBackgroundImage:[ICMAssets booleanButtonLeftImage] forState:UIControlStateNormal];
    [first setBackgroundImage:[ICMAssets booleanButtonLeftImageSelected] forState:UIControlStateSelected];
    [second setBackgroundImage:[ICMAssets booleanButtonRightImage] forState:UIControlStateNormal];
    [second setBackgroundImage:[ICMAssets booleanButtonRightImageSelected] forState:UIControlStateSelected];
}
- (void)layoutButtons {
    [ICMAutoLayout constrainView:self.trueButton toHeight:40];
    [ICMAutoLayout constrainView:self.falseButton toHeight:40];
    if (self.verticalLayout) {
        [ICMAutoLayout pinView:self.trueButton edges:0x31 toSameEdgesOfView:self];
        [ICMAutoLayout pinView:self.falseButton edges:0x34 toSameEdgesOfView:self];
        [ICMAutoLayout pinView:self.trueButton attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeTop ofItem:self.falseButton withConstant:-8];
    } else {
        [ICMAutoLayout pinView:self.trueButton edges:0x11 toSameEdgesOfView:self];
        [ICMAutoLayout pinView:self.trueButton attribute:NSLayoutAttributeTrailing toAttribute:NSLayoutAttributeLeading ofItem:self.dividerView];
        [ICMAutoLayout pinView:self.falseButton edges:0x21 toSameEdgesOfView:self];
        [ICMAutoLayout pinView:self.falseButton attribute:NSLayoutAttributeLeading toAttribute:NSLayoutAttributeTrailing ofItem:self.dividerView];
    }
    [NSLayoutConstraint constraintWithItem:self.trueButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.falseButton attribute:NSLayoutAttributeWidth multiplier:1 constant:0].active = YES;
}
- (void)trueButtonTapped {
    if (self.showLoading) [self showLoadingState:self.trueButton];
    [ICMFeedbackGenerator feedbackWithType:0];
    [self setTrueButtonSelected:!self.disableAfterSelect];
    self.value = @YES;
    [self.inputViewDelegate entryCompleteButtonTapped];
}
- (void)falseButtonTapped {
    if (self.showLoading) [self showLoadingState:self.falseButton];
    [ICMFeedbackGenerator feedbackWithType:0];
    [self setFalseButtonSelected:!self.disableAfterSelect];
    self.value = @NO;
    [self.inputViewDelegate entryCompleteButtonTapped];
}
- (void)setTrueButtonSelected:(BOOL)enabled {
    self.trueButton.selected = YES;
    self.falseButton.selected = NO;
    self.falseButton.enabled = enabled;
}
- (void)setFalseButtonSelected:(BOOL)enabled {
    self.falseButton.selected = YES;
    self.trueButton.selected = NO;
    self.trueButton.enabled = enabled;
}
- (void)showLoadingState:(UIButton *)button {
    [button setAttributedTitle:nil forState:UIControlStateSelected];
    [button setImage:[ICMAssets buttonLoadingImage] forState:UIControlStateNormal];
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    animation.cumulative = YES;
    animation.duration = 0.4000000059604645;
    animation.repeatCount = INFINITY;
    animation.fromValue = @0;
    animation.toValue = @6.283185307179586;
    [button.imageView.layer addAnimation:animation forKey:@"spin"];
}
- (void)showTrueButtonLoadingState { [self showLoadingState:self.trueButton]; }
- (void)showFalseButtonLoadingState { [self showLoadingState:self.falseButton]; }
- (void)resetLoadingState {
    UIButton *button = self.value.boolValue ? self.trueButton : self.falseButton;
    [button.imageView.layer removeAnimationForKey:@"spin"];
    [button setImage:nil forState:UIControlStateNormal];
}
- (void)configureDefaults {
    if (!self.value) return;
    if (self.value.boolValue) [self setTrueButtonSelected:self.fieldIsEditable];
    else if (!self.value.boolValue) [self setFalseButtonSelected:self.fieldIsEditable];
}
- (void)configureSuccessState {
    [self resetLoadingState];
    self.trueButton.userInteractionEnabled = NO;
    self.falseButton.userInteractionEnabled = NO;
}
- (void)configureErrorState {
    [self resetLoadingState];
    self.trueButton.selected = NO;
    self.falseButton.selected = NO;
    self.trueButton.enabled = YES;
    self.falseButton.enabled = YES;
    self.trueButton.userInteractionEnabled = YES;
    self.falseButton.userInteractionEnabled = YES;
}
- (id)inputValue { return self.value; }
- (void)setInputValue:(id)value {
    BOOL number = [value isKindOfClass:[NSNumber class]];
    if (number || !value) { self.value = value; [self configureDefaults]; }
}
- (BOOL)isUserInputValid:(NSError **)error {
    NSMutableArray *validators = [[NSArray array] mutableCopy];
    if (self.isRequired) [validators addObject:[ICMEmptyInputValidator new]];
    return [ICMValidator validateNumber:self.value validators:validators error:error];
}
- (void)configureLoadingState {
    self.trueButton.userInteractionEnabled = NO;
    self.falseButton.userInteractionEnabled = NO;
}
- (void)configureDisabledState {
    self.trueButton.userInteractionEnabled = NO;
    self.falseButton.userInteractionEnabled = NO;
    UIColor *trueColor = [UIColor redColor];
    self.trueButton.titleLabel.textColor = trueColor;
    UIColor *falseColor = [UIColor redColor];
    self.falseButton.titleLabel.textColor = falseColor;
}
- (void)configureDefaultState {}
@end
