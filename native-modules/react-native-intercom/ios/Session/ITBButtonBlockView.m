#import "ITBButtonBlockView.h"
#import "ITBButtonBlock.h"
#import <math.h>

// Signature inferred from the original selector call; protocol metadata is absent.
@interface NSObject (ITBButtonIntentDispatch)
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
@end

@implementation ITBButtonBlockView
@synthesize containerView = _containerView, linkButton = _linkButton, linkUrl = _linkUrl;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.containerView = [UIView new];
        self.containerView.backgroundColor = UIColor.clearColor;
        [self addSubview:self.containerView];
        [self createLinkButton];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat width = self.block.estimatedSize.width;
    CGFloat height = self.block.estimatedSize.height;
    self.containerView.frame = CGRectMake(0, 0, width, height);
    CGFloat centerX = CGRectGetMidX(self.bounds);
    CGFloat centerY = CGRectGetMidY(self.bounds);
    self.containerView.center = CGPointMake(centerX, centerY);
    CGFloat buttonWidth = self.block.estimatedSize.width;
    CGFloat buttonHeight = self.block.estimatedSize.height;
    self.linkButton.bounds = CGRectMake(0, 0, buttonWidth, buttonHeight - 16);
    CGFloat buttonCenterX = CGRectGetMidX(self.bounds);
    CGFloat buttonCenterY = CGRectGetMidY(self.bounds);
    self.linkButton.center = CGPointMake(buttonCenterX, buttonCenterY);
}
- (void)createLinkButton {
    UIButtonConfiguration *configuration = [UIButtonConfiguration filledButtonConfiguration];
    configuration.titleAlignment = (UIButtonConfigurationTitleAlignment)2;
    configuration.contentInsets = NSDirectionalEdgeInsetsMake(5, 5, 5, 5);
    configuration.background.cornerRadius = 4;
    self.linkButton = [UIButton buttonWithConfiguration:configuration primaryAction:nil];
    [self.linkButton addTarget:self action:@selector(followLink:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.linkButton];
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBButtonBlock *buttonBlock = (ITBButtonBlock *)block;
    self.linkButton.configurationUpdateHandler = ^(UIButton *button) {
        UIButtonConfiguration *configuration = button.configuration;
        NSString *text = buttonBlock.text;
        UIFont *font = buttonBlock.font;
        NSMutableAttributedString *title = [[NSMutableAttributedString alloc] initWithString:text attributes:@{NSFontAttributeName:font}];
        configuration.attributedTitle = title;
        configuration.baseForegroundColor = buttonBlock.textColor;
        configuration.baseBackgroundColor = buttonBlock.backgroundColor;
        button.configuration = configuration;
    };
    self.linkUrl = buttonBlock.url;
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)constraintWidth;
    ITBButtonBlock *button = (ITBButtonBlock *)block;
    NSString *text = button.text;
    UIFont *font = button.font;
    CGRect rect = [text boundingRectWithSize:CGSizeMake(width, CGFLOAT_MAX) options:3 attributes:@{NSFontAttributeName:font} context:nil];
    CGFloat height = (CGFloat)ceilf((float)CGRectGetHeight(rect)) + 16;
    if (height < 44) height = 44;
    return CGSizeMake(width, height + 16);
}
- (void)followLink:(id)sender {
    (void)sender;
    ITBBlock *block = self.block;
    [(id)self.intentOpener handleIntent:(id<ITBIntent>)block forBlock:block fromView:self];
}
@end
