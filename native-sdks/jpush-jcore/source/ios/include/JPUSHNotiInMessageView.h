#pragma once
#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import "JPUSHNotiInMessageConfig.h"
#import "JPushNotiInAppWeakScriptMessageDelegate.h"
@class JPUSHNotiInMessageView;
@protocol JPushNotiInMessageViewDelegate <NSObject>
- (void)jPushMessageViewDelegateFadeOut:(JPUSHNotiInMessageView *)view;
- (void)jPushMessageViewDidTap:(JPUSHNotiInMessageView *)view;
- (void)jPushInMessageClik:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param;
- (void)jPushInMessageCallback:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param;
- (void)jPushInMessageReportdata:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param;
@end
@interface JPUSHNotiInMessageView : UIView
    <WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler, UIGestureRecognizerDelegate>
@property(nonatomic, strong) WKWebView *webView;
@property(nonatomic, strong) JPushNotiInAppWeakScriptMessageDelegate *messageDelegate;
@property(nonatomic) BOOL isCallback;
@property(nonatomic) BOOL isNetwork;
@property(nonatomic, strong) JPUSHNotiInMessageConfig *config;
@property(nonatomic) BOOL isShow;
@property(nonatomic, weak) id<JPushNotiInMessageViewDelegate> delegate;
@property(nonatomic, strong) NSLayoutConstraint *leftConstraint;
@property(nonatomic, strong) NSLayoutConstraint *rightConstraint;
@property(nonatomic, strong) NSLayoutConstraint *topConstraint;
@property(nonatomic, strong) NSLayoutConstraint *heightConstraint;
@property(nonatomic, strong) NSLayoutConstraint *bottomConstraint;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWithConfig:(JPUSHNotiInMessageConfig *)config
                     delegate:(id<JPushNotiInMessageViewDelegate>)delegate;
- (instancetype)initWithFrame:(CGRect)frame WithConfig:(JPUSHNotiInMessageConfig *)config
                    delegate:(id<JPushNotiInMessageViewDelegate>)delegate;
- (void)setView;
- (void)initGesture;
- (void)fadeMeout;
- (void)handleTap:(UIGestureRecognizer *)gesture;
- (void)addScriptMessageHandler;
- (void)removeScriptMessageHandler;
- (void)inAppClick:(id)param;
- (void)getTplData:(id)param;
- (void)onLoadCallback:(id)param;
- (void)reportData:(id)param;
- (void)getTplExtraData:(id)param;
- (NSString *)dicToString:(id)dictionary;
- (NSData *)dicToData:(id)dictionary;
- (NSURL *)fileURLForBuggyWKWebView8WithFileURL:(NSURL *)url;
@end
