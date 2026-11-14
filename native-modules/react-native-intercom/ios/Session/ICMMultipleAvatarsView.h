#import "ICMAvatarViewModel.h"
#import "ICMAvatarView.h"
@interface ICMMultipleAvatarsView : UIView
@property (nonatomic, strong) NSArray<ICMAvatarView *> *avatarViewsArray;
@property (nonatomic) CGFloat avatarHeight;
@property (nonatomic) CGFloat avatarOverlap;
@property (nonatomic) NSInteger maxAvatarCount;
- (instancetype)initWithAvatarViewModels:(NSArray<ICMAvatarViewModel *> *)models height:(CGFloat)height overlap:(CGFloat)overlap maxCount:(NSInteger)count;
- (void)configureAvatarViewsForAvatarViewModels:(NSArray<ICMAvatarViewModel *> *)models;
- (void)layoutAvatars;
- (NSUInteger)numberOfAvatarsDisplayed;
- (void)updateVisibleAvatars;
- (void)roundCutOutView:(UIView *)view withCutOutWidth:(CGFloat)width leadingEdge:(BOOL)leading;
@end
