#pragma once
#import "JPUSHInAppView.h"
@interface JPUSHInAppFullView : JPUSHInAppView <UIGestureRecognizerDelegate>
@property(nonatomic, strong) UIButton *countDownBtn;
@property(nonatomic, strong) NSTimer *countDownTimer;
@property(nonatomic) int countDownNumber;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)invalidCountDownTimer;
- (void)countDown;
- (void)initTapGesture:(UIView *)view;
- (void)inAppTapAction;
- (void)closeBtnAction;
- (void)countDownBtnAction;
- (void)configUI;
- (BOOL)initFullMessageViewLayout:(UIView *)view;
@end
