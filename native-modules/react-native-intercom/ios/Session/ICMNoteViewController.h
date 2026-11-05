#pragma once
#import "ICMContentContainerViewController.h"
#import "ICMInAppsViewController.h"
#import "ICMInAppAttributes.h"
@class ICMConversationPart, ICMFullScreenInAppViewController;
@protocol ICMConfigProtocol;
@interface ICMNoteViewController : ICMContentContainerViewController <ICMFullScreenInAppDelegate, ICMFullScreenInAppProtocol> {
    ICMConversation *_conversation;
    __weak ICMConversationPart *_conversationPart;
    __weak id<ICMInAppsDelegate> _delegate;
    id<ICMConfigProtocol> _config;
    UIView *_backgroundView;
    ICMFullScreenInAppViewController *_inAppController;
    NSArray *_inAppHeightConstraints;
}
@property (nonatomic, strong) ICMConversation *conversation;
@property (nonatomic, weak) ICMConversationPart *conversationPart;
@property (nonatomic, weak) id<ICMInAppsDelegate> delegate;
@property (nonatomic, strong) id<ICMConfigProtocol> config;
@property (nonatomic, strong) UIView *backgroundView;
@property (nonatomic, strong) ICMFullScreenInAppViewController *inAppController;
@property (nonatomic, strong) NSArray *inAppHeightConstraints;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(id<ICMConfigProtocol>)config;
+ (ICMInAppAttributes)inAppAttributes;
- (CGFloat)fakeComposerHeight;
@end
