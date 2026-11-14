#import "ITBBlockView.h"
@interface ITBButtonBlockView : ITBBlockView {
    UIView *_containerView;
    UIButton *_linkButton;
    NSString *_linkUrl;
}
@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UIButton *linkButton;
@property (nonatomic, copy) NSString *linkUrl;
- (void)createLinkButton;
- (void)followLink:(id)sender;
@end
