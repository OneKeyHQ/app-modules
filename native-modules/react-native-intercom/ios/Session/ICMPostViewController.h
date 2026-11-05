#pragma once
#import "ICMInAppsViewController.h"
#import "ICMInAppAttributes.h"
@class ICMConversationPart, ICMFullScreenInAppViewController;
@protocol ICMConfigProtocol;
@interface ICMPostViewController : UIViewController <ICMFullScreenInAppDelegate, ICMFullScreenInAppProtocol> {
    BOOL _isInApp;
    ICMConversation *_conversation;
    __weak ICMConversationPart *_conversationPart;
    __weak id<ICMInAppsDelegate> _delegate;
    id<ICMConfigProtocol> _config;
    UIView *_backgroundView;
    ICMFullScreenInAppViewController *_inAppController;
}
@property (nonatomic) BOOL isInApp;
@property (nonatomic, strong) ICMConversation *conversation;
@property (nonatomic, weak) ICMConversationPart *conversationPart;
@property (nonatomic, weak) id<ICMInAppsDelegate> delegate;
@property (nonatomic, strong) id<ICMConfigProtocol> config;
@property (nonatomic, strong) UIView *backgroundView;
@property (nonatomic, strong) ICMFullScreenInAppViewController *inAppController;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate isInApp:(BOOL)inApp;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate isInApp:(BOOL)inApp config:(id<ICMConfigProtocol>)config;
- (ICMInAppAttributes)inAppAttributes;
- (void)animateIn;
- (void)close;
@end
