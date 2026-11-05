#import "ICMAvatarView.h"
#import <QuartzCore/QuartzCore.h>
#import <math.h>
@implementation ICMAvatarView
- (instancetype)init { return [self initWithNoTeammatePresenceWithAvatarDiameter:32]; }
- (void)commonInit { [self createAvatarImageView]; [self createMaskLayer]; [self applyBorder]; }
- (instancetype)initWithNoTeammatePresenceWithAvatarDiameter:(CGFloat)diameter {
    self = [super initWithFrame:CGRectMake(0, 0, diameter, diameter)];
    if (self) { self.userInteractionEnabled = NO; _diameter = diameter; _borderWidth = 1.5; [self commonInit]; }
    return self;
}
- (instancetype)initWithLargeTeammatePresenceWithAvatarDiameter:(CGFloat)diameter {
    self = [super initWithFrame:CGRectMake(0, 0, diameter, diameter)];
    if (self) { self.userInteractionEnabled = NO; _diameter = diameter; _borderWidth = 1.5; [self commonInit]; [self createTeammatePresenceViewWithDiameter:14 presenceBorder:2]; }
    return self;
}
- (instancetype)initWithDefaultTeammatePresenceWithAvatarDiameter:(CGFloat)diameter {
    self = [super initWithFrame:CGRectMake(0, 0, diameter, diameter)];
    if (self) { self.userInteractionEnabled = NO; _diameter = diameter; _borderWidth = 1.5; [self commonInit]; [self createTeammatePresenceViewWithDiameter:8 presenceBorder:1]; }
    return self;
}
- (void)createTeammatePresenceViewWithDiameter:(CGFloat)diameter presenceBorder:(CGFloat)border {
    self.teammatePresenceView = [[ICMTeammatePresenceView alloc] initWithDiameter:diameter border:border];
    [self addSubview:self.teammatePresenceView];
}
- (void)createAvatarImageView {
    self.avatarImageView = [ITBAnimatedImageView new];
    self.avatarImageView.contentMode = UIViewContentModeScaleAspectFill;
    [self addSubview:self.avatarImageView];
}
- (void)createMaskLayer {
    self.maskLayer = [CAShapeLayer layer];
    self.maskLayer.lineWidth = 0;
    self.maskLayer.strokeColor = UIColor.blackColor.CGColor;
    self.maskLayer.fillColor = UIColor.blackColor.CGColor;
    self.maskLayer.shouldRasterize = YES;
    self.maskLayer.rasterizationScale = self.icmDisplayScale * 2;
    self.avatarImageView.layer.mask = self.maskLayer;
}
- (void)setUserPresenceViewIsHidden:(BOOL)hidden { self.teammatePresenceView.hidden = hidden; }
- (void)setTeamPresenceBorderColor:(UIColor *)color { self.teammatePresenceView.borderColor = color; }
- (void)setBorderColor:(UIColor *)color { _borderColor = color; [self applyBorder]; }
- (void)setBorderWidth:(CGFloat)width { _borderWidth = width; [self applyBorder]; }
- (void)applyBorder {
    UIColor *color = self.borderColor;
    CAShapeLayer *layer = self.borderLayer;
    if (!color) { [layer removeFromSuperlayer]; self.borderLayer = nil; return; }
    if (!layer) {
        self.borderLayer = [CAShapeLayer layer];
        self.borderLayer.lineWidth = self.borderWidth;
        self.borderLayer.fillColor = UIColor.clearColor.CGColor;
        self.borderLayer.shouldRasterize = YES;
        self.borderLayer.rasterizationScale = self.icmDisplayScale * 2;
        [self.layer addSublayer:self.borderLayer];
    }
    self.borderLayer.strokeColor = self.borderColor.CGColor;
    [self updatePathOnLayer:self.borderLayer];
}
- (void)updateRasterizationScale {
    CGFloat scale = self.icmDisplayScale * 2;
    if (self.maskLayer.rasterizationScale != scale) self.maskLayer.rasterizationScale = scale;
    if (self.borderLayer.rasterizationScale != scale) self.borderLayer.rasterizationScale = scale;
}
- (void)updatePathOnLayer:(CAShapeLayer *)layer {
    CAAnimation *size = [self.layer animationForKey:@"bounds.size"];
    [CATransaction begin];
    if (size) {
        [CATransaction setAnimationDuration:size.duration];
        [CATransaction setAnimationTimingFunction:size.timingFunction];
        CABasicAnimation *animation;
        if ([size isKindOfClass:CASpringAnimation.class]) {
            CASpringAnimation *spring = [CASpringAnimation animationWithKeyPath:@"path"];
            spring.mass = [(CASpringAnimation *)size mass];
            spring.stiffness = [(CASpringAnimation *)size stiffness];
            spring.damping = [(CASpringAnimation *)size damping];
            if ([spring respondsToSelector:@selector(setInitialVelocity:)] && [size respondsToSelector:@selector(initialVelocity)]) spring.initialVelocity = [(CASpringAnimation *)size initialVelocity];
            animation = spring;
        } else { animation = [CABasicAnimation animationWithKeyPath:@"path"]; }
        [layer addAnimation:animation forKey:@"path"];
    } else { (void)[CATransaction disableActions]; }
    CGFloat factor = self.avatarShape == 1 ? .167 : .5;
    layer.path = [UIBezierPath bezierPathWithRoundedRect:self.bounds cornerRadius:factor * self.diameter].CGPath;
    layer.frame = self.bounds;
    [CATransaction commit];
}
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name { [self loadAvatarWithURL:url userName:name useActionColorAsBackgroundColor:YES avatarShape:1]; }
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name avatarShape:(NSInteger)shape { [self loadAvatarWithURL:url userName:name useActionColorAsBackgroundColor:YES avatarShape:shape]; }
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name useActionColorAsBackgroundColor:(BOOL)action avatarShape:(NSInteger)shape {
    [self loadAvatarWithURL:url initials:[self firstInitialOfName:name] label:name useActionColorAsBackgroundColor:action avatarShape:shape shouldAddBackground:shape == 0];
}
- (void)loadAvatarWithURL:(NSURL *)url initials:(NSString *)initials label:(NSString *)label useActionColorAsBackgroundColor:(BOOL)action avatarShape:(NSInteger)shape shouldAddBackground:(BOOL)background {
    self.avatarImageView.backgroundColor = UIColor.clearColor;
    self.avatarShape = shape;
    [self updatePathOnLayer:self.maskLayer];
    if ([(id)url isKindOfClass:NSNull.class] || url.absoluteString.length == 0) {
        ITBAnimatedImageView *imageView = self.avatarImageView;
        imageView.image = [ICMAssets defaultAvatarImageWithInitials:initials diameter:self.diameter useActionColorAsBackgroundColor:action];
        if (action && ![ICMRecoveredColorHelper isLightColor:ICMRecoveredDefaultThemeProvider.shared.theme.primaryText]) self.borderColor = [UIColor.whiteColor colorWithAlphaComponent:.2];
    } else {
        __weak ICMAvatarView *weakSelf = self;
        if (background) {
            UIColor *color;
            if (!action) color = ICMRecoveredDefaultThemeProvider.shared.theme.neutralContainer;
            else {
                BOOL light = [ICMRecoveredColorHelper isLightColor:ICMRecoveredDefaultThemeProvider.shared.theme.primaryText];
                color = light ? ICMRecoveredDefaultThemeProvider.shared.theme.primaryAvatar : ICMRecoveredDefaultThemeProvider.shared.theme.primaryText;
            }
            self.avatarImageView.backgroundColor = color;
        }
        [self.avatarImageView loadImageWithURLString:url.absoluteString actionsBeforeNetworkLoad:^{
            UIImage *image = [ICMAssets defaultAvatarImageWithInitials:initials diameter:self.diameter useActionColorAsBackgroundColor:action];
            weakSelf.avatarImageView.image = image;
        } completion:nil animationOptions:0x500000];
    }
    self.isAccessibilityElement = YES;
    self.accessibilityLabel = label;
}
- (void)loadAvatarForParticipant:(ICMParticipant *)participant { [self loadAvatar:participant.avatar useActionColorAsBackgroundColor:!participant.isGroupParticipant]; }
- (void)loadAvatarForEventParticipant:(ICMRecoveredEventParticipant *)participant {
    if (participant.avatar.initials) {
        [self loadAvatar:participant.avatar useActionColorAsBackgroundColor:!participant.isGroupParticipant];
    } else {
        self.currentAvatar = participant.avatar;
        self.currentUseActionColorAsBackgroundColor = !participant.isGroupParticipant;
        self.currentShouldAddBackground = participant.avatar.shape == 0;
        NSURL *url = [self urlForCurrentInterfaceStyleFromAvatar:participant.avatar];
        [self loadAvatarWithURL:url initials:participant.initial label:participant.label useActionColorAsBackgroundColor:!participant.isGroupParticipant avatarShape:participant.avatar.shape shouldAddBackground:participant.avatar.shape == 0];
    }
}
- (void)loadAvatar:(Avatar *)avatar useActionColorAsBackgroundColor:(BOOL)action { [self loadAvatar:avatar useActionColorAsBackgroundColor:action shouldAddBackground:avatar.shape == 0]; }
- (void)loadAvatar:(Avatar *)avatar useActionColorAsBackgroundColor:(BOOL)action shouldAddBackground:(BOOL)background {
    self.currentAvatar = avatar;
    self.currentUseActionColorAsBackgroundColor = action;
    self.currentShouldAddBackground = background;
    NSURL *url = [self urlForCurrentInterfaceStyleFromAvatar:avatar];
    [self loadAvatarWithURL:url initials:avatar.initials label:avatar.label useActionColorAsBackgroundColor:action avatarShape:avatar.shape shouldAddBackground:background];
}
- (void)cancelImageLoad { [self.avatarImageView cancelImageLoad]; self.avatarImageView.image = nil; }
- (NSString *)firstInitialOfName:(NSString *)name {
    NSString *trimmed = [name stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
    return trimmed.length > 0 ? [[trimmed substringWithRange:NSMakeRange(0, 1)] uppercaseString] : @"";
}
- (NSURL *)urlForCurrentInterfaceStyleFromAvatar:(Avatar *)avatar {
    if (self.traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark && avatar.imageDarkUrl) {
        NSURL *dark = avatar.imageDarkUrl;
        if (dark) return dark;
    }
    return avatar.imageUrl;
}
- (void)didMoveToWindow { [super didMoveToWindow]; if (self.traitCollection.displayScale > 0) [self updateRasterizationScale]; }
- (void)traitCollectionDidChange:(UITraitCollection *)previous {
    [super traitCollectionDidChange:previous];
    if (self.traitCollection.displayScale > 0 && previous.displayScale != self.traitCollection.displayScale) [self updateRasterizationScale];
    if (previous.userInterfaceStyle != self.traitCollection.userInterfaceStyle && self.currentAvatar) [self loadAvatar:self.currentAvatar useActionColorAsBackgroundColor:self.currentUseActionColorAsBackgroundColor shouldAddBackground:self.currentShouldAddBackground];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.avatarImageView.frame = self.bounds;
    [self applyBorder];
    [self updatePathOnLayer:self.maskLayer];
    [self positionActiveStateOnBottomRight];
}
- (void)positionActiveStateOnBottomRight {
    CGFloat radius = CGRectGetWidth(self.bounds) * .5;
    CGFloat x = fma(radius, .7071067811865476, CGRectGetMidX(self.bounds));
    CGFloat y = fma(radius, .7071067811865475, CGRectGetMidY(self.bounds));
    self.teammatePresenceView.center = CGPointMake(round(x), round(y));
}
@end
