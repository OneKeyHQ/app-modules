// True original UI classes, declarations only until their own source is restored.
#import <UIKit/UIKit.h>
#import "ICMHostAppState.h"
#import "ICMInAppsViewController.h"
#import "ICMNavigationController.h"
#import "ICMMessengerNavigationController.h"
#import "ICMContentModalViewController.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate15OpenCallService")))
@interface ICMRecoveredOpenCallService : NSObject
+ (instancetype)shared;
- (void)performOpenCallIfNeeded;
@end
