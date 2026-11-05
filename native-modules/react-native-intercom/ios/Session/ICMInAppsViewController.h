#pragma once
#import <UIKit/UIKit.h>
#import "ICMPresentationProtocols.h"
#import "ICMHostAppState.h"
@class ICMConversation;
@protocol ICMInAppUpdateProtocol <NSObject>
- (void)conversationMarkedAsRead:(NSString *)identifier;
@end
@protocol ICMInAppsAnimationProtocol <NSObject>
- (void)animateOutWithCompletion:(void (^)(void))completion;
@end
@protocol ICMFullScreenInAppProtocol <NSObject>
@property (nonatomic, readonly) ICMConversation *conversation;
@end
@interface ICMInAppsViewController : UIViewController <ICMInAppsDelegate, ICMInAppUpdateProtocol> {
    NSArray *_conversations;
    ICMHostAppState *_hostAppState;
    __weak id<ICMInAppsDelegate> _delegate;
    UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *_snippetsInAppViewController;
    UIViewController<ICMFullScreenInAppProtocol> *_fullScreenInAppViewController;
}
@property (nonatomic, strong) NSArray *conversations;
@property (nonatomic, strong) ICMHostAppState *hostAppState;
@property (nonatomic, weak) id<ICMInAppsDelegate> delegate;
@property (nonatomic, strong) UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *snippetsInAppViewController;
@property (nonatomic, strong) UIViewController<ICMFullScreenInAppProtocol> *fullScreenInAppViewController;
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMInAppsDelegate>)delegate hostAppState:(ICMHostAppState *)state;
- (UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *)snippetsViewControllerForConversations:(NSArray *)conversations;
- (UIViewController<ICMFullScreenInAppProtocol> *)fullScreenViewControllerForConversations:(NSArray *)conversations;
- (BOOL)shouldHandleTouchOnView:(UIView *)view;
- (void)tearDownInApps;
- (void)tearDown;
- (void)updateLauncherVisibility;
- (BOOL)isShowingLauncherOrInApp;
- (void)setConversations:(NSArray *)conversations completion:(void (^)(void))completion;
+ (void)prepareConversationsForLayout:(NSArray *)conversations completion:(void (^)(BOOL))completion;
- (void)updateSnippetsInApp:(UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *)controller;
- (void)updateFullScreenInApp:(UIViewController<ICMFullScreenInAppProtocol> *)controller fromSnippet:(BOOL)snippet;
- (void)bottomPaddingUpdated;
+ (NSDictionary *)inAppMetricInfoForConversation:(ICMConversation *)conversation;
- (void)addReceivedEventForConversation:(ICMConversation *)conversation asFull:(BOOL)full;
- (void)addViewedEventForConversation:(ICMConversation *)conversation asFull:(BOOL)full;
- (NSUInteger)state;
- (void)hideLauncherWithAnimation;
@end
