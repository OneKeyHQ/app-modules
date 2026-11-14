#import "ICMAvatarDependencies.h"
#import "ICMTeammatePresenceView.h"
#import "ITBAnimatedImageView.h"
@interface ICMAvatarView : UIView {
    BOOL _currentUseActionColorAsBackgroundColor;
    BOOL _currentShouldAddBackground;
    CGFloat _diameter;
    UIColor *_borderColor;
    CGFloat _borderWidth;
    ICMTeammatePresenceView *_teammatePresenceView;
    CAShapeLayer *_borderLayer;
    CAShapeLayer *_maskLayer;
    ITBAnimatedImageView *_avatarImageView;
    NSInteger _avatarShape;
    Avatar *_currentAvatar;
}
@property (nonatomic, readonly) CGFloat diameter;
@property (nonatomic, strong) UIColor *borderColor;
@property (nonatomic) CGFloat borderWidth;
@property (nonatomic, strong) ICMTeammatePresenceView *teammatePresenceView;
@property (nonatomic, strong) CAShapeLayer *borderLayer;
@property (nonatomic, strong) CAShapeLayer *maskLayer;
@property (nonatomic, strong) ITBAnimatedImageView *avatarImageView;
@property (nonatomic) NSInteger avatarShape;
@property (nonatomic, strong) Avatar *currentAvatar;
@property (nonatomic) BOOL currentUseActionColorAsBackgroundColor;
@property (nonatomic) BOOL currentShouldAddBackground;
- (instancetype)initWithNoTeammatePresenceWithAvatarDiameter:(CGFloat)diameter;
- (instancetype)initWithLargeTeammatePresenceWithAvatarDiameter:(CGFloat)diameter;
- (instancetype)initWithDefaultTeammatePresenceWithAvatarDiameter:(CGFloat)diameter;
- (void)commonInit;
- (void)createTeammatePresenceViewWithDiameter:(CGFloat)diameter presenceBorder:(CGFloat)border;
- (void)createAvatarImageView;
- (void)createMaskLayer;
- (void)setUserPresenceViewIsHidden:(BOOL)hidden;
- (void)setTeamPresenceBorderColor:(UIColor *)color;
- (void)applyBorder;
- (void)updateRasterizationScale;
- (void)updatePathOnLayer:(CAShapeLayer *)layer;
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name;
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name avatarShape:(NSInteger)shape;
- (void)loadAvatarWithURL:(NSURL *)url userName:(NSString *)name useActionColorAsBackgroundColor:(BOOL)action avatarShape:(NSInteger)shape;
- (void)loadAvatarWithURL:(NSURL *)url initials:(NSString *)initials label:(NSString *)label useActionColorAsBackgroundColor:(BOOL)action avatarShape:(NSInteger)shape shouldAddBackground:(BOOL)background;
- (void)loadAvatarForParticipant:(ICMParticipant *)participant;
- (void)loadAvatarForEventParticipant:(ICMRecoveredEventParticipant *)participant;
- (void)loadAvatar:(Avatar *)avatar useActionColorAsBackgroundColor:(BOOL)action;
- (void)loadAvatar:(Avatar *)avatar useActionColorAsBackgroundColor:(BOOL)action shouldAddBackground:(BOOL)background;
- (void)cancelImageLoad;
- (NSString *)firstInitialOfName:(NSString *)name;
- (NSURL *)urlForCurrentInterfaceStyleFromAvatar:(Avatar *)avatar;
- (void)positionActiveStateOnBottomRight;
@end
