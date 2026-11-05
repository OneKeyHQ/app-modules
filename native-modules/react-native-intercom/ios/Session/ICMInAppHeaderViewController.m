#import "ICMInAppHeaderViewController.h"
#import "ICMAvatarView.h"
#import "ICMAutoLayout.h"
#import "ICMConfig.h"
#import "ICMFeatureChecker.h"
#import "ICMTeamPresence.h"
#import "ICMChatNotificationContentImports.h"
@interface ICMParticipant (InAppName)
- (NSAttributedString *)adminAppNameTitle:(NSDictionary *)attributes appName:(NSString *)name;
@end
@interface ICMRecoveredParticipatingAdmin (InAppStatus)
@property (nonatomic, readonly) BOOL isActive;
@property (nonatomic, readonly) NSDate *lastActiveAt;
@end
@interface ICMTimestampCalculator (InAppStatus)
- (NSString *)textForLastActiveDate:(NSDate *)date;
@end
@implementation ICMInAppHeaderViewController
- (instancetype)initWithAvatarDiameter:(CGFloat)diameter { self = [super init]; if (self) _avatarDiameter = diameter; return self; }
- (void)loadView {
    [super loadView];
    self.view.translatesAutoresizingMaskIntoConstraints = NO;
    UIColor *clear = UIColor.clearColor;
    self.view.backgroundColor = clear;
    self.avatarView = [[ICMAvatarView alloc] initWithDefaultTeammatePresenceWithAvatarDiameter:self.avatarDiameter];
    self.avatarView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.avatarView];
    [ICMAutoLayout constrainView:self.avatarView toSize:CGSizeMake(self.avatarDiameter, self.avatarDiameter)];
    [ICMAutoLayout pinView:self.avatarView attribute:NSLayoutAttributeLeft toAttribute:NSLayoutAttributeLeft ofItem:self.view withConstant:24];
    self.closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.closeButton addTarget:self action:@selector(closePressed) forControlEvents:UIControlEventTouchUpInside];
    self.closeButton.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.closeButton];
    [ICMAutoLayout constrainView:self.closeButton toSize:CGSizeMake(44, 44)];
    [ICMAutoLayout pinView:self.closeButton attribute:NSLayoutAttributeCenterY toAttribute:NSLayoutAttributeCenterY ofItem:self.avatarView];
    self.adminTextView = [UITextView new];
    UIColor *background = UIColor.clearColor;
    self.adminTextView.backgroundColor = background;
    self.adminTextView.translatesAutoresizingMaskIntoConstraints = NO;
    self.adminTextView.editable = NO;
    self.adminTextView.selectable = NO;
    self.adminTextView.textContainerInset = UIEdgeInsetsMake(0, -5, 0, -5);
    self.adminTextView.scrollEnabled = NO;
    self.adminTextView.accessibilityIdentifier = @"intercom admin name and status";
    [self.view addSubview:self.adminTextView];
    [ICMAutoLayout pinView:self.adminTextView attribute:NSLayoutAttributeCenterY toAttribute:NSLayoutAttributeCenterY ofItem:self.avatarView];
    [ICMAutoLayout pinView:self.adminTextView attribute:NSLayoutAttributeLeft toAttribute:NSLayoutAttributeRight ofItem:self.avatarView withConstant:12];
    [ICMAutoLayout pinView:self.adminTextView attribute:NSLayoutAttributeRight toAttribute:NSLayoutAttributeLeft ofItem:self.closeButton withConstant:2];
    self.adminTextViewHeightConstraint = [ICMAutoLayout constrainView:self.adminTextView toHeight:0];
}
- (void)updateViewConstraints {
    [super updateViewConstraints];
    [self.adminTextView sizeToFit];
    CGFloat height = CGRectGetHeight(self.adminTextView.bounds);
    self.adminTextViewHeightConstraint.constant = height;
}
- (void)closePressed { [self.delegate closePressed]; }
- (void)configureForParticipant:(ICMParticipant *)participant nameAttributes:(NSDictionary *)nameAttributes statusAttributes:(NSDictionary *)statusAttributes {
    [self configureForParticipant:participant nameAttributes:nameAttributes statusAttributes:statusAttributes appName:[ICMConfig sharedConfig].appName];
}
- (void)configureForParticipant:(ICMParticipant *)participant nameAttributes:(NSDictionary *)nameAttributes statusAttributes:(NSDictionary *)statusAttributes appName:(NSString *)appName {
    NSMutableAttributedString *text = [[participant adminAppNameTitle:nameAttributes appName:appName] mutableCopy];
    if ([participant isKindOfClass:ICMRecoveredParticipatingAdmin.class]) {
        ICMRecoveredParticipatingAdmin *admin = (id)participant;
        BOOL active = admin.isActive;
        NSDate *date = admin.lastActiveAt;
        NSString *status = [[ICMTimestampCalculator sharedInstance] textForLastActiveDate:date];
        if ([ICMFeatureChecker shouldShowTeammatePresenceView]) {
            status = [[ICMTranslationManager messenger] translationForKey:active ? @"active_state" : @"away_state"];
            self.avatarView.teammatePresenceView.active = active;
        }
        if (status.length > 0) {
            NSAttributedString *line = [[NSAttributedString alloc] initWithString:[@"\n" stringByAppendingString:status] attributes:statusAttributes];
            [text appendAttributedString:line];
        }
    }
    NSMutableParagraphStyle *paragraph = [NSParagraphStyle.defaultParagraphStyle mutableCopy];
    paragraph.lineSpacing = 1;
    paragraph.lineBreakMode = NSLineBreakByTruncatingTail;
    [text addAttribute:NSParagraphStyleAttributeName value:paragraph range:NSMakeRange(0, text.length)];
    self.adminTextView.attributedText = text;
    [self.avatarView loadAvatarForParticipant:participant];
}
- (void)setCloseButtonImage:(UIImage *)image tintColor:(UIColor *)color {
    self.closeButtonRightConstraint.active = NO;
    [self.closeButton setImage:image forState:UIControlStateNormal];
    self.closeButton.tintColor = color;
    CGFloat extra = (44 - [self.closeButton imageForState:UIControlStateNormal].size.width) * -0.5;
    CGFloat total = [self.delegate headerCloseButtonRightPadding] + extra;
    self.closeButtonRightConstraint = [ICMAutoLayout pinView:self.closeButton attribute:NSLayoutAttributeRight toAttribute:NSLayoutAttributeRight ofItem:self.view withConstant:-total];
}
@end
