#import "ICMMultipleAvatarView.h"
#import "ICMFeatureChecker.h"
#import "ICMTeamPresence.h"
@interface ICMRecoveredParticipatingAdmin (MultipleAvatarPresence)
@property (nonatomic, readonly) BOOL isActive;
@end
@implementation ICMMultipleAvatarView
- (instancetype)initWithWidth:(CGFloat)width {
    self = [super initWithFrame:CGRectMake(0, 0, width, width)];
    if (self) {
        self.userInteractionEnabled = NO;
        _avatarBorderColor = UIColor.whiteColor;
        CGFloat diameter = [self avatarDiameterForAdminCount:1];
        self.rightAvatarView = [[ICMAvatarView alloc] initWithNoTeammatePresenceWithAvatarDiameter:diameter];
        self.rightAvatarView.hidden = YES;
        self.rightAvatarView.accessibilityIdentifier = @"multiple avatar right";
        self.rightAvatarView.borderColor = self.avatarBorderColor;
        [self addSubview:self.rightAvatarView];
        self.topAvatarView = [[ICMAvatarView alloc] initWithNoTeammatePresenceWithAvatarDiameter:diameter];
        self.topAvatarView.hidden = YES;
        self.topAvatarView.borderColor = self.avatarBorderColor;
        self.topAvatarView.accessibilityIdentifier = @"multiple avatar top";
        [self addSubview:self.topAvatarView];
        self.leftAvatarView = [[ICMAvatarView alloc] initWithNoTeammatePresenceWithAvatarDiameter:diameter];
        self.leftAvatarView.accessibilityIdentifier = @"multiple avatar left";
        [self addSubview:self.leftAvatarView];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat diameter = [self avatarDiameterForAdminCount:self.admins.count];
    CGFloat border = self.leftAvatarView.borderWidth;
    NSUInteger count = self.admins.count;
    if (count == 1) {
        self.leftAvatarView.frame = self.bounds;
        [self.leftAvatarView setUserPresenceViewIsHidden:YES];
    } else if (count == 2) {
        self.leftAvatarView.frame = CGRectMake(border, CGRectGetMidY(self.bounds) - diameter / 2, diameter, diameter);
        [self.leftAvatarView setUserPresenceViewIsHidden:YES];
        self.rightAvatarView.frame = CGRectMake(CGRectGetMaxX(self.bounds) - diameter - border, CGRectGetMidY(self.bounds) - diameter / 2, diameter, diameter);
    } else {
        self.leftAvatarView.frame = CGRectMake(border, .75 * diameter, diameter, diameter);
        [self.leftAvatarView setUserPresenceViewIsHidden:YES];
        self.topAvatarView.frame = CGRectMake(CGRectGetMidX(self.bounds) - diameter / 2, border, diameter, diameter);
        self.rightAvatarView.frame = CGRectMake(CGRectGetMaxX(self.bounds) - diameter - border, .75 * diameter, diameter, diameter);
    }
}
- (CGFloat)avatarDiameterForAdminCount:(NSUInteger)count { CGFloat width = CGRectGetWidth(self.frame); return count > 1 ? width * .5199999809265137 : width; }
- (void)bindAdmins:(NSArray *)admins {
    if (self.admins.count != admins.count) [self setNeedsLayout];
    self.admins = admins;
    [self bindAvatarView:self.leftAvatarView toAdminAtIndex:0];
    [self bindAvatarView:self.rightAvatarView toAdminAtIndex:1];
    [self bindAvatarView:self.topAvatarView toAdminAtIndex:2];
    self.leftAvatarView.borderColor = self.admins.count >= 2 ? self.avatarBorderColor : nil;
}
- (void)setAvatarBorderColor:(UIColor *)color {
    _avatarBorderColor = color;
    self.topAvatarView.borderColor = color;
    self.rightAvatarView.borderColor = color;
    self.leftAvatarView.borderColor = self.admins.count > 1 ? color : nil;
}
- (void)bindAvatarView:(ICMAvatarView *)view toAdminAtIndex:(NSUInteger)index {
    NSUInteger count = self.admins.count;
    if (count > index) {
        ICMParticipant *admin = self.admins[index];
        [view loadAvatarForParticipant:admin];
        if ([admin isKindOfClass:ICMRecoveredParticipatingAdmin.class] && [ICMFeatureChecker shouldShowTeammatePresenceView]) view.teammatePresenceView.active = [(ICMRecoveredParticipatingAdmin *)admin isActive];
    }
    view.hidden = count <= index;
}
- (void)cancelImageLoad { [self.leftAvatarView cancelImageLoad]; [self.rightAvatarView cancelImageLoad]; [self.topAvatarView cancelImageLoad]; }
- (void)keepUserPresenceViewBackgroundColor {
    ICMTeammatePresenceView *presence = self.leftAvatarView.teammatePresenceView;
    presence.active = self.leftAvatarView.teammatePresenceView.isActive;
}
@end
