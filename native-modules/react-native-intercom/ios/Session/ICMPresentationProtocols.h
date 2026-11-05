// Original required/optional protocol surface from 19.9.1 Objective-C metadata.
#import <UIKit/UIKit.h>
@class ICMWindow;
@protocol ICMContentModalViewControllerDelegate <NSObject>
@optional
- (void)dismissibleBySwipe:(BOOL)dismissible didAttemptDismiss:(void (^)(void))attempt;
@end
@protocol ICMErrorViewDelegate <NSObject>
@optional
- (void)didSelectErrorAction;
@end
@protocol ICMCloseIntercomModalDelegate <NSObject>
- (void)closeIntercomModal;
@end
@protocol ICMInAppsDelegate <NSObject>
- (void)presentConversation:(id)conversation fromFull:(BOOL)full;
- (void)dismissConversation:(id)conversation fromFull:(BOOL)full;
- (void)hideInApp:(id)controller;
- (void)showInbox;
@optional
- (void)conversationShown:(id)conversation asFull:(BOOL)full;
- (BOOL)isIntercomPresented;
- (BOOL)shouldShowLauncher;
- (void)launcherTapped;
- (void)didShowFullScreenInApp;
- (void)didHideFullScreenInApp;
- (void)didHideInApps;
@end
@protocol ICMWindowProvider <NSObject>
- (UIWindow *)hostAppWindow;
- (ICMWindow *)intercomWindow;
@end
