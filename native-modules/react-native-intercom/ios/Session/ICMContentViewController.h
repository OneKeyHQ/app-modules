#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN
@protocol ICMContentViewControllerDelegate;
@protocol ICMContentViewController <NSObject>
- (void)reload;
- (void)closeButtonTapped;
- (void)viewDidDismissBySwipe;
@property (nonatomic, weak, nullable) id<ICMContentViewControllerDelegate> contentDelegate;
@optional
- (void)showLoading;
- (void)hideLoading;
@property (nonatomic, readonly) BOOL prefersTransparentNavigationBar;
@end

@protocol ICMContentViewControllerDelegate <NSObject>
- (void)contentDidLoadWithDismissEnabled:(BOOL)enabled;
- (void)contentDidLoadWithDismissEnabled:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(nullable void (^)(void))attempt;
- (void)showLoadingViewWithImage:(nullable UIImage *)image;
- (void)contentLoadingDidFail:(nullable NSString *)title showActionButton:(BOOL)show errorMessageSubtitle:(nullable NSString *)subtitle actionButtonTitle:(nullable NSString *)buttonTitle;
- (void)closeContentModal;
- (void)configureDismissEnabled:(BOOL)enabled;
- (void)configureDismissEnabled:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(nullable void (^)(void))attempt;
@optional
- (void)configureContentTitle:(nullable NSString *)title;
- (void)configureContentTitleView:(nullable UIView *)view;
- (void)configureCloseButtonForBackgroundColor:(nullable UIColor *)color;
@end
NS_ASSUME_NONNULL_END
