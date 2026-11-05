#import "ICMStackedChatNotificationsPillView.h"
#import "ICMBlockAppearanceDependencies.h"
@interface ICMRecoveredTheme (OriginalNotificationColors)
@property (nonatomic, readonly) UIColor *neutralContainerAlt;
@end
@interface ICMTranslationManager (NotificationCount)
- (NSString *)translationForKey:(NSString *)key params:(NSDictionary<NSString *, NSString *> *)params;
@end
@implementation ICMStackedChatNotificationsPillView
@synthesize count = _count;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.accessibilityIdentifier = @"intercom snippet pill";
        self.backgroundColor = [[ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerAlt colorWithAlphaComponent:0.95];
        self.textLabel = [UILabel new];
        self.textLabel.textAlignment = NSTextAlignmentCenter;
        UIFont *font = [ICMUtils fontOfSize:14 weight:2];
        self.textLabel.font = font;
        self.textLabel.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        self.textLabel.userInteractionEnabled = YES;
        self.textLabel.translatesAutoresizingMaskIntoConstraints = NO;
        [self addSubview:self.textLabel];
        [NSLayoutConstraint activateConstraints:@[
            [self.textLabel.leadingAnchor constraintEqualToAnchor:self.leadingAnchor constant:10],
            [self.textLabel.trailingAnchor constraintEqualToAnchor:self.trailingAnchor constant:-10],
            [self.textLabel.topAnchor constraintEqualToAnchor:self.topAnchor constant:7],
            [self.textLabel.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-7]
        ]];
        [self setCount:0];
        self.layer.shadowOffset = CGSizeMake(0, 1);
        self.layer.shadowRadius = 4;
        self.layer.shadowColor = UIColor.blackColor.CGColor;
        self.layer.shadowOpacity = 0.15f;
    }
    return self;
}
- (void)layoutSubviews { [super layoutSubviews]; CGFloat radius = CGRectGetHeight(self.bounds) * 0.5; self.layer.cornerRadius = radius; }
- (void)setCount:(NSInteger)count {
    _count = count;
    ICMTranslationManager *manager = [ICMTranslationManager messenger];
    NSString *value = [NSString stringWithFormat:@"%ld", (long)count];
    NSString *text = [manager translationForKey:@"plus_x_more" params:@{@"n": value}];
    self.textLabel.text = text;
}
@end
