#pragma once
#import "JPUSHInAppView.h"
@interface JPUSHInAppBannerView : JPUSHInAppView <UIGestureRecognizerDelegate>
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)configUI;
- (void)addTapGesture:(UIView *)view;
- (void)inAppTapAction;
- (void)addSwipeGesture;
- (void)fadeMeout;
- (void)configBannerImageText;
- (BOOL)initMessageViewLayout:(UIView *)view;
- (double)messageLeftMargin:(JPUSHInAppView *)view;
- (double)messageTopMargin;
- (double)messageRightMargin:(JPUSHInAppView *)view;
- (double)messageBottomMargin;
- (UIWindow *)getCurrentWindow;
@end
