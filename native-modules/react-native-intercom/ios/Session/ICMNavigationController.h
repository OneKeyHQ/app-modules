#import <UIKit/UIKit.h>
#import "ICMPresentationProtocols.h"
#import "ICMHostAppState.h"
@interface ICMNavigationController : UINavigationController {
    UIView *_invisibleView;
    ICMHostAppState *_hostAppState;
    __weak id<ICMCloseIntercomModalDelegate> _closeIntercomModalDelegate;
}
@property (nonatomic, strong) UIView *invisibleView;
@property (nonatomic, strong) ICMHostAppState *hostAppState;
@property (nonatomic, weak) id<ICMCloseIntercomModalDelegate> closeIntercomModalDelegate;
- (instancetype)initWithHostAppState:(ICMHostAppState *)state closeIntercomModalDelegate:(id<ICMCloseIntercomModalDelegate>)delegate;
- (void)setupNavigationBar;
- (void)configureTransparentNavigationBar;
- (void)closeIntercomModal;
- (void)displayCloseButton;
- (void)removeCloseButton;
- (UIBarButtonItem *)closeButton;
- (void)fadeToViewController:(UIViewController *)controller;
@end
