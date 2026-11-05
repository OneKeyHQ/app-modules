#import "ITBBlockView.h"
#import "ITBNotificationCardBlock.h"
@interface ITBNotificationCardBlockView : ITBBlockView {
    BOOL _displayChannelsView;
    BOOL _displayPlaceholder;
    UIView *_containerView;
    UIView *_userInputView;
    UILabel *_notificationTitle;
    UIView *_channelsView;
    UILabel *_userInputLabel;
    UIEdgeInsets _containerInsets;
}
@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UIView *userInputView;
@property (nonatomic, strong) UILabel *notificationTitle;
@property (nonatomic, strong) UIView *channelsView;
@property (nonatomic, strong) UILabel *userInputLabel;
@property (nonatomic) UIEdgeInsets containerInsets;
@property (nonatomic) BOOL displayChannelsView;
@property (nonatomic) BOOL displayPlaceholder;
- (void)configureChannelsViewWithChannels:(NSArray *)channels;
- (void)addUnderlineToLabel:(UILabel *)label;
- (void)configureUserInputWithBlock:(ITBNotificationCardBlock *)block;
+ (BOOL)shouldDisplayChannelsView:(ITBNotificationCardBlock *)block;
+ (CGFloat)calculateChannelViewWidthForChannels:(NSArray *)channels inBoundingSize:(CGSize)size;
@end
