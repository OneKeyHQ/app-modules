#pragma once
#import "JPUSHInAppView.h"
@interface JPUSHInAppModalView : JPUSHInAppView <UIGestureRecognizerDelegate>
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)configUI;
- (void)initTapGesture:(UIView *)view;
- (void)inAppTapAction;
- (void)closeBtnAction;
- (void)operateBtnAction:(UIButton *)button;
- (void)configModalImage;
- (void)configModalImageText;
- (BOOL)initModelMessageViewLayout:(UIView *)view;
@end
