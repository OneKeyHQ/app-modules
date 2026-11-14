#import "ICMErrorView.h"
#import "ICMActionButton.h"
#import "ICMAutoLayout.h"
#import "ICMBlockAppearanceDependencies.h"
#import "UIFont+ICMWeight.h"
@implementation ICMErrorView
- (instancetype)initWithTitle:(NSString *)title message:(NSString *)message showActionButton:(BOOL)show actionButtonTitle:(NSString *)buttonTitle {
    if ((self = [super init])) {
        [self createTitleLabel:title];
        [self createMessageLabel:message];
        if (show) { _actionButtonTitle = buttonTitle; [self createRetryButton]; }
        [self createConstraints];
        self.accessibilityIdentifier = @"error view";
    }
    return self;
}
- (void)createTitleLabel:(NSString *)title {
    self.titleLabel = [UILabel new];
    self.titleLabel.text = title;
    self.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.titleLabel.numberOfLines = 0;
    self.titleLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody weight:UIFontWeightMedium];
    self.titleLabel.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
    self.titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    self.titleLabel.accessibilityIdentifier = @"error view title label";
    [self addSubview:self.titleLabel];
}
- (void)createMessageLabel:(NSString *)message {
    self.messageLabel = [UILabel new];
    self.messageLabel.text = message;
    self.messageLabel.textAlignment = NSTextAlignmentCenter;
    self.messageLabel.numberOfLines = 2;
    self.messageLabel.lineBreakMode = NSLineBreakByWordWrapping;
    self.messageLabel.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
    self.messageLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
    self.messageLabel.translatesAutoresizingMaskIntoConstraints = NO;
    self.messageLabel.accessibilityIdentifier = @"error view message label";
    [self addSubview:self.messageLabel];
}
- (void)createRetryButton {
    self.actionButton = [[ICMActionButton alloc] initWithTitle:self.actionButtonTitle style:0 backgroundColor:[ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer foregroundColor:[ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer];
    self.actionButton.accessibilityIdentifier = @"error view retry button";
    self.actionButton.titleLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline weight:UIFontWeightBold];
    self.actionButton.clipsToBounds = YES;
    self.actionButton.translatesAutoresizingMaskIntoConstraints = NO;
    [self.actionButton addTarget:self action:@selector(didSelectActionButton) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.actionButton];
}
- (void)didSelectActionButton { [self.delegate didSelectErrorAction]; }
- (void)createConstraints {
    [ICMAutoLayout pinView:self.titleLabel edges:0xa toSameEdgesOfView:self];
    [ICMAutoLayout pinView:self.titleLabel attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:self];
    [ICMAutoLayout pinView:self.messageLabel attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeBottom ofItem:self.titleLabel withConstant:0];
    [ICMAutoLayout pinView:self.messageLabel edges:0xa toSameEdgesOfView:self];
    if (self.actionButton) {
        [ICMAutoLayout constrainView:self.actionButton toSize:CGSizeMake(self.actionButton.intrinsicContentSize.width + 52, 44)];
        [ICMAutoLayout centerView:self.actionButton inContainerOnAxis:NSLayoutAttributeCenterX];
        [ICMAutoLayout pinView:self.actionButton attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeBottom ofItem:self];
        [ICMAutoLayout pinView:self.actionButton attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeBottom ofItem:self.messageLabel withConstant:16];
    } else [ICMAutoLayout pinView:self.messageLabel attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeBottom ofItem:self];
}
- (void)setAlphaAnimated:(CGFloat)alpha {
    [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionBeginFromCurrentState animations:^{ self.alpha = alpha; } completion:nil];
}
@end
