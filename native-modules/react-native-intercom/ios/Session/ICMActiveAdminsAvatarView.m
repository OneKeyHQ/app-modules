#import "ICMActiveAdminsAvatarView.h"
#import "ICMTeamPresence.h"
@interface ICMParticipant (ActiveAdminName)
@property (nonatomic, readonly) NSString *firstName;
@end
@interface ICMTranslationManager (ActiveAdminNames)
- (NSString *)translationForKey:(NSString *)key params:(NSDictionary *)parameters;
@end
@implementation ICMActiveAdminsAvatarView
- (instancetype)initWithHeight:(CGFloat)height overlap:(CGFloat)overlap maxCount:(NSInteger)maxCount {
    NSArray *admins = [ICMTeamPresence sharedInstance].activeAdmins;
    NSUInteger count = (NSUInteger)maxCount < admins.count ? (NSUInteger)maxCount : admins.count;
    NSMutableArray *models = [NSMutableArray new];
    NSString *names = nil;
    if ((NSInteger)count >= 1) {
        for (NSUInteger index = 0; index < count; index++) {
            ICMAvatarViewModel *model = [ICMAvatarViewModel alloc];
            model = [model initWithAvatar:((ICMRecoveredParticipatingAdmin *)admins[index]).avatar activeIndicatorSize:0];
            [models addObject:model];
            NSString *firstName = ((ICMRecoveredParticipatingAdmin *)admins[index]).firstName;
            names = names ? [NSString stringWithFormat:@"%@, %@", names, firstName] : firstName;
        }
    }
    [self setIsAccessibilityElement:YES];
    if (names) self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"available_teammates" params:@{@"available_teammates":names}];
    return [super initWithAvatarViewModels:models height:height overlap:overlap maxCount:maxCount];
}
- (void)setUpdateWhenTeamPresenceUpdated:(BOOL)update {
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    if (update) [center addObserver:self selector:@selector(teamPresenceUpdated) name:@"ICMTeamPresenceUpdated" object:nil];
    else [center removeObserver:self];
    _updateWhenTeamPresenceUpdated = update;
}
- (void)teamPresenceUpdated { dispatch_async(dispatch_get_main_queue(), ^{ [self updateAvatars]; }); }
- (void)updateAvatars {
    NSArray *admins = [ICMTeamPresence sharedInstance].activeAdmins;
    NSMutableArray *models = [NSMutableArray new];
    NSString *names = nil;
    for (NSUInteger index = 0; index < admins.count; index++) {
        ICMAvatarViewModel *model = [ICMAvatarViewModel alloc];
        model = [model initWithAvatar:((ICMRecoveredParticipatingAdmin *)admins[index]).avatar activeIndicatorSize:0];
        [models addObject:model];
        NSString *firstName = ((ICMRecoveredParticipatingAdmin *)admins[index]).firstName;
        names = names ? [NSString stringWithFormat:@"%@, %@", names, firstName] : firstName;
    }
    [self configureAvatarViewsForAvatarViewModels:models];
    [self invalidateIntrinsicContentSize];
    self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"available_teammates" params:@{@"available_teammates":names}];
}
@end
