#pragma once
#import <UIKit/UIKit.h>
#import "JPUSHInAppConfig.h"
@class JPUSHInAppView;
@protocol JPUSHInAppViewDelegate <NSObject>
@optional
- (void)jpushInAppViewShow:(JPUSHInAppView *)view;
- (void)jpushInAppViewDismiss:(JPUSHInAppView *)view;
- (void)jpushInappViewClick:(JPUSHInAppView *)view
         clickViewPart:(long long)part
             clickActionType:(long long)type
           clickAction:(NSString *)action;
@end
@interface JPUSHInAppView : UIView
@property(nonatomic) long long inappType;
@property(nonatomic, strong) JPUSHInAppConfig *config;
@property(nonatomic, weak) id<JPUSHInAppViewDelegate> delegate;
+ (instancetype)createViewWithConfig:(JPUSHInAppConfig *)config
                          delegate:(id<JPUSHInAppViewDelegate>)delegate;
- (instancetype)initWithConfig:(JPUSHInAppConfig *)config
                     delegate:(id<JPUSHInAppViewDelegate>)delegate;
- (void)showInView:(UIView *)view;
- (void)dismiss;
@end
