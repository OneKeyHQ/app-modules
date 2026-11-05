#import <UIKit/UIKit.h>
#import "ICMRootViewControllerDelegate.h"
@class ICMWindow;
@interface ICMPresentationManager : NSObject <ICMRootViewControllerDelegate> {
    BOOL _launcherVisible;
    BOOL _inAppMessagesVisible;
    BOOL _manualInAppBottomPaddingSet;
    double _inAppBottomPadding;
    ICMWindow *_intercomWindow;
    NSArray *_lastUnreadConversations;
}
@property (nonatomic) double inAppBottomPadding;
@property (nonatomic) BOOL launcherVisible;
@property (nonatomic) BOOL inAppMessagesVisible;
@property (nonatomic) BOOL manualInAppBottomPaddingSet;
@property (nonatomic, strong) ICMWindow *intercomWindow;
@property (nonatomic, strong) NSArray *lastUnreadConversations;
+ (instancetype)sharedInstance;
+ (void)observeSceneWillEnterForeground;
- (void)updateInAppBottomPaddingFromConfigIfNeeded;
- (void)hideIntercomUI;
- (void)userChanged;
- (BOOL)isIntercomPresented;
- (void)dismissAllIntercomViews;
- (void)setNeedsHostAppStatusBarAppearanceUpdate;
- (void)updateWindowThemeMode;
- (BOOL)isContentCurrentlyPresented;
- (void)showLauncherOrInAppsIfNeeded;
- (void)showLauncherOrInAppsOnMainThread;
- (void)setUnreadConversations:(NSArray *)conversations completion:(void (^)(void))completion;
- (void)conversationMarkedAsRead:(NSNotification *)notification;
- (void)presentCarousel:(id)carousel;
- (void)presentTicket:(id)ticket fromTriggerSource:(NSUInteger)source;
- (void)pushTicket:(id)ticket fromNavigationController:(UINavigationController *)navigation didCreateTicket:(BOOL)created triggerSource:(NSUInteger)source;
- (void)presentCreateTicketFormForTicketType:(id)type conversation:(id)conversation;
- (void)presentSurvey:(id)survey;
- (void)presentSheet:(id)sheet;
- (void)showConversation:(id)conversation;
- (void)showConversation:(id)conversation fromNavigationController:(UINavigationController *)navigation;
- (void)pushConversation:(id)conversation applyFadeTransition:(BOOL)fade fromNavigationController:(UINavigationController *)navigation withCompletion:(void (^)(void))completion;
- (void)presentHome;
- (void)presentHomeWithConversation:(id)conversation;
- (void)presentHelpCenter;
- (void)presentMessages;
- (void)presentTickets;
- (void)presentHelpCenterFromSearchBrowse;
- (void)presentHelpCenterCollection:(id)collection;
- (void)presentHelpCenterCollections:(NSArray *)collections;
- (void)presentArticle:(id)article allowReactions:(BOOL)reactions anchorLink:(NSString *)anchor;
- (void)hideIntercom;
- (void)presentWindowIfNeededAsFullscreen:(BOOL)fullscreen;
- (void)sceneWillEnterForeground:(NSNotification *)notification;
- (ICMWindow *)createIntercomWindowInScene:(UIWindowScene *)scene;
@end
