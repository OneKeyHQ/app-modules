#pragma once
#import "ICMInAppsViewController.h"
#import "ICMChatNotificationDependencies.h"
@interface ICMStackedChatNotificationsViewController : UIViewController <ICMChatNotificationItemActionDelegate, ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol>
@property (nonatomic, unsafe_unretained) id<ICMInAppsDelegate> delegate;
@property (nonatomic, strong) ICMStackedChatNotificationsView *stackView;
@property (nonatomic, strong) ICMStackedChatNotificationsPillView *pillView;
@property (nonatomic, strong) NSArray *conversations;
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMInAppsDelegate>)delegate;
- (void)addSubviewsForConversations:(NSArray *)conversations;
- (void)pillTapped;
- (void)updatePillCount;
- (void)setPillCount:(NSInteger)count;
@end
