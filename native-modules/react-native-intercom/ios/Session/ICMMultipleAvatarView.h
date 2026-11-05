#import "ICMAvatarView.h"
@interface ICMMultipleAvatarView : UIView
@property (nonatomic, strong) UIColor *avatarBorderColor;
@property (nonatomic, strong) ICMAvatarView *leftAvatarView;
@property (nonatomic, strong) ICMAvatarView *topAvatarView;
@property (nonatomic, strong) ICMAvatarView *rightAvatarView;
@property (nonatomic, strong) NSArray *admins;
- (instancetype)initWithWidth:(CGFloat)width;
- (CGFloat)avatarDiameterForAdminCount:(NSUInteger)count;
- (void)bindAdmins:(NSArray *)admins;
- (void)bindAvatarView:(ICMAvatarView *)view toAdminAtIndex:(NSUInteger)index;
- (void)cancelImageLoad;
- (void)keepUserPresenceViewBackgroundColor;
@end
