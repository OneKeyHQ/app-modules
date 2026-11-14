#pragma once
#import <UIKit/UIKit.h>
#import "ICMContentViewController.h"
#import "ICMPresentationProtocols.h"
@class ICMNavigationController;
@protocol _TtP18IntercomSDKPrivate23OpenCallServiceProtocol_;
@interface ICMContentModalViewController : UIViewController <ICMContentViewControllerDelegate, ICMErrorViewDelegate> {
    __weak id<ICMContentModalViewControllerDelegate> _delegate;
    NSString *_closeButtonAccessibilityText;
    UIBarButtonItem *_closeButton;
    UIColor *_closeButtonTintColor;
    UIViewController *_currentChildViewController;
    id<ICMContentViewController> _contentViewController;
    id<_TtP18IntercomSDKPrivate23OpenCallServiceProtocol_> _openCallService;
}
@property (nonatomic, weak) id<ICMContentModalViewControllerDelegate> delegate;
@property (nonatomic, strong) NSString *closeButtonAccessibilityText;
@property (nonatomic, strong) UIBarButtonItem *closeButton;
@property (nonatomic, strong) UIColor *closeButtonTintColor;
@property (nonatomic, strong) UIViewController *currentChildViewController;
@property (nonatomic, strong) id<ICMContentViewController> contentViewController;
@property (nonatomic, strong) id<_TtP18IntercomSDKPrivate23OpenCallServiceProtocol_> openCallService;
- (instancetype)initWithContentViewController:(id)controller;
- (void)setupContentViewController;
- (void)configureAsDismissible:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(void (^)(void))attempt;
- (void)addViewControllerAsChildViewController:(UIViewController *)controller;
- (void)removeCurrentChildViewController;
- (ICMNavigationController *)intercomNavigationController;
- (void)openCallStateDidChange:(NSNotification *)notification;
- (void)handleOpenCallState;
- (void)showLoadingIndicator;
- (void)showOpenCallError;
@end
