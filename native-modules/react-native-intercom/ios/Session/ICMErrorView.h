#import <UIKit/UIKit.h>
#import "ICMPresentationProtocols.h"
@class ICMActionButton;
@interface ICMErrorView : UIView {
    __weak id<ICMErrorViewDelegate> _delegate;
    UILabel *_titleLabel;
    UILabel *_messageLabel;
    ICMActionButton *_actionButton;
    NSString *_actionButtonTitle;
}
@property (nonatomic, weak) id<ICMErrorViewDelegate> delegate;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *messageLabel;
@property (nonatomic, strong) ICMActionButton *actionButton;
@property (nonatomic, strong) NSString *actionButtonTitle;
- (instancetype)initWithTitle:(NSString *)title message:(NSString *)message showActionButton:(BOOL)show actionButtonTitle:(NSString *)buttonTitle;
- (void)createTitleLabel:(NSString *)title;
- (void)createMessageLabel:(NSString *)message;
- (void)createRetryButton;
- (void)didSelectActionButton;
- (void)createConstraints;
- (void)setAlphaAnimated:(CGFloat)alpha;
@end
