#pragma once
#import "ICMInAppAttributes.h"
#import "ICMInAppsViewController.h"
@class ICMInAppHeaderViewController, ICMContentStackView, ICMFakeComposerView, ICMReactionsViewController, ICMConversationPart, OutboundContentBlockDelegate;
@interface ICMFullScreenInAppViewController : UIViewController <ICMInAppHeaderDelegate, ICMReactionsViewControllerDelegate>
@property (nonatomic) BOOL isInApp;
@property (nonatomic, strong) ICMInAppHeaderViewController *headerController;
@property (nonatomic, strong) UIScrollView *blockListScrollView;
@property (nonatomic, strong) ICMContentStackView *blockListStackView;
@property (nonatomic, strong) ICMFakeComposerView *fakeComposerView;
@property (nonatomic, strong) ICMReactionsViewController *reactionsController;
@property (nonatomic, strong) ICMConversation *conversation;
@property (nonatomic, weak) ICMConversationPart *conversationPart;
@property (nonatomic, weak) id<ICMInAppsDelegate> inAppsDelegate;
@property (nonatomic, weak) id<ICMFullScreenInAppDelegate> fullScreenDelegate;
@property (nonatomic, strong) OutboundContentBlockDelegate *blockDelegate;
@property (nonatomic) ICMInAppAttributes attributes;
- (instancetype)initWithConversation:(ICMConversation *)conversation inAppDelegate:(id<ICMInAppsDelegate>)delegate fullScreenDelegate:(id<ICMFullScreenInAppDelegate>)fullScreenDelegate isInApp:(BOOL)inApp attributes:(ICMInAppAttributes)attributes;
- (void)addFooterView;
- (void)didCalculateBlocksSize:(CGSize)size;
- (void)clipBlocksList;
- (void)openPressed;
@end
