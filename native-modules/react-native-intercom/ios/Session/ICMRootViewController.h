// Partial original controller source; unresolved selectors remain explicitly inventoried.
#import <UIKit/UIKit.h>
#import "ICMRootViewControllerDelegate.h"
#import "ICMPresentationProtocols.h"
@class ICMHostAppState, ICMInAppsViewController, ICMWindow;
@protocol _TtP18IntercomSDKPrivate23OpenCallServiceProtocol_;
@interface ICMRootViewController : UIViewController <UIGestureRecognizerDelegate, UIAdaptivePresentationControllerDelegate, ICMContentModalViewControllerDelegate, ICMErrorViewDelegate, UISheetPresentationControllerDelegate, ICMCloseIntercomModalDelegate, ICMInAppsDelegate, ICMWindowProvider> {
    BOOL _contentModalPresented;
    BOOL _tearingDown;
    BOOL _isPresentedViewControllerDismissibleBySwipe;
    __weak id<ICMRootViewControllerDelegate> _delegate;
    ICMHostAppState *_hostAppState;
    void (^_postPresentationBlock)(void);
    ICMInAppsViewController *_inAppsVC;
    __weak UIWindow *_hostAppWindow;
    __weak UIWindowScene *_windowScene;
    UIView *_hostAppSnapshotView;
    void (^_didAttemptDismissBlock)(void);
    id<_TtP18IntercomSDKPrivate23OpenCallServiceProtocol_> _openCallService;
    NSInteger _savedAppLevelStatusBarStyle;
}
@property (nonatomic, weak) id<ICMRootViewControllerDelegate> delegate;
@property (nonatomic) BOOL contentModalPresented;
@property (nonatomic, weak) UIWindow *hostAppWindow;
@property (nonatomic) BOOL tearingDown;
@property (nonatomic) BOOL isPresentedViewControllerDismissibleBySwipe;
@property (nonatomic, strong) ICMHostAppState *hostAppState;
@property (nonatomic, copy) void (^postPresentationBlock)(void);
@property (nonatomic, strong) ICMInAppsViewController *inAppsVC;
@property (nonatomic, weak) UIWindowScene *windowScene;
@property (nonatomic, strong) UIView *hostAppSnapshotView;
@property (nonatomic, copy) void (^didAttemptDismissBlock)(void);
@property (nonatomic, strong) id<_TtP18IntercomSDKPrivate23OpenCallServiceProtocol_> openCallService;
@property (nonatomic) NSInteger savedAppLevelStatusBarStyle;
@end
// Declarations for actual original routes; absent bodies are not placeholders.
@interface ICMRootViewController (OriginalSelectors)
- (void)commonInit;
- (void)dismissHostAppKeyboard;
- (BOOL)isPresentingVC;
- (BOOL)isShowingMessenger;
- (BOOL)shouldHideIntercomWindow;
- (void)hideIntercomWindowIfNeeded;
- (void)didHideInApps;
- (ICMWindow *)intercomWindow;
- (UIWindow *)findHostAppWindow;
- (ICMHostAppState *)hostAppStateFromWindow:(UIWindow *)window;
- (void)conversationMarkedAsRead:(NSNotification *)notification;
- (BOOL)shouldShowLauncher;
- (instancetype)initWithWindowScene:(UIWindowScene *)scene;
- (void)bottomPaddingUpdated;
- (BOOL)shouldHandleTouchOnView:(UIView *)view;
- (void)tearDownInApps;
- (void)updateLauncherVisibility;
- (void)tearDown;
- (BOOL)isIntercomPresented;
- (void)dismissAllIntercomViews;
- (void)updateHostAppState;
- (void)updateUnreadConversations:(NSArray *)conversations completion:(void (^)(void))completion;
- (void)presentCarousel:(id)carousel;
- (void)presentTicket:(id)ticket fromTriggerSource:(NSUInteger)source;
- (void)pushTicket:(id)ticket fromNavigationController:(UINavigationController *)navigation didCreateTicket:(BOOL)created triggerSource:(NSUInteger)source;
- (void)presentTicketCreateFormForTicketType:(id)type conversation:(id)conversation;
- (void)presentSurvey:(id)survey;
- (void)presentSheet:(id)sheet;
- (BOOL)firstViewControllerIsConversation;
- (UINavigationController *)getBottomNavigationController;
- (void)dismissAllButBottomViewController;
- (void)presentConversation:(id)conversation;
- (void)pushConversation:(id)conversation applyFadeTransition:(BOOL)fade fromNavigationController:(UINavigationController *)navigation;
- (void)presentHome;
- (void)presentHomeWithConversation:(id)conversation;
- (void)presentHelpCenter;
- (void)presentMessages;
- (void)presentTickets;
- (void)presentHelpCenterFromSearchBrowse;
- (void)presentHelpCenterCollection:(id)collection;
- (void)presentHelpCenterCollections:(NSArray *)collections;
- (void)presentArticle:(id)article allowReactions:(BOOL)reactions anchorLink:(NSString *)anchor;
@end
