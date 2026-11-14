#pragma once
// Genuine downstream declarations; missing class bodies remain explicit link gaps.
#import "ICMInAppsViewController.h"
#import "ICMContentContainerViewController.h"
@class ICMConfig;
#import "ICMStackedChatNotificationsViewController.h"
#import "ICMChatNotificationViewController.h"
@interface ICMLauncherViewController : UIViewController <ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol>
- (instancetype)initWithDelegate:(id<ICMInAppsDelegate>)delegate;
@end
#import "ICMPostViewController.h"
#import "ICMNoteViewController.h"
