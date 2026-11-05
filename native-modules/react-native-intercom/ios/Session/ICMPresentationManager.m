// Source recovery from original 19.9.1; see IOS_PROVENANCE.md.
#import "ICMPresentationManager.h"
#import "ICMWindow.h"
#import "ICMConfig.h"
#import "ICMIdentityStore.h"
#import "ICMDataManagerRequestDependencies.h"
#import "../Identity/ICMUtils.h"
#import "ICMAppearanceReset.h"
@interface ICMRecoveredSurveyService (PresentationState)
+ (BOOL)currentlyDisplayingSurvey;
+ (void)setCurrentlyDisplayingSurvey:(BOOL)displaying;
@end
@implementation ICMPresentationManager
@synthesize launcherVisible = _launcherVisible, inAppMessagesVisible = _inAppMessagesVisible;
@synthesize manualInAppBottomPaddingSet = _manualInAppBottomPaddingSet, inAppBottomPadding = _inAppBottomPadding;
@synthesize intercomWindow = _intercomWindow, lastUnreadConversations = _lastUnreadConversations;
+ (instancetype)sharedInstance {
    static ICMPresentationManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ manager = [ICMPresentationManager new]; });
    return manager;
}
+ (void)observeSceneWillEnterForeground {
    if ([ICMUtils sceneManifestPresent]) {
        [[NSNotificationCenter defaultCenter] addObserver:[ICMPresentationManager sharedInstance] selector:@selector(sceneWillEnterForeground:) name:UISceneWillEnterForegroundNotification object:nil];
    }
}
- (instancetype)init {
    if ((self = [super init])) {
        _launcherVisible = NO;
        _inAppMessagesVisible = YES;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(conversationMarkedAsRead:) name:@"ICMConversationMarkedAsReadNotification" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateInAppBottomPaddingFromConfigIfNeeded) name:@"kICMIntercomConfigChangedNotification" object:nil];
        [ICMAppearanceReset resetAppearanceWithin:[ICMWindow class]];
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)updateInAppBottomPaddingFromConfigIfNeeded {
    if (!self.manualInAppBottomPaddingSet) self.inAppBottomPadding = [ICMConfig sharedConfig].launcherBottomPadding;
}
- (void)setInAppBottomPadding:(double)padding {
    if (_inAppBottomPadding == padding) return;
    _inAppBottomPadding = padding;
    [self.intercomWindow.rootViewController bottomPaddingUpdated];
}
- (void)setInAppMessagesVisible:(BOOL)visible {
    if (_inAppMessagesVisible == visible) return;
    _inAppMessagesVisible = visible;
    if (!visible && [ICMIdentityStore currentIdentity].userIdentity.identityExists) [self.intercomWindow.rootViewController tearDownInApps];
    [self setUnreadConversations:self.lastUnreadConversations completion:nil];
}
- (void)setLauncherVisible:(BOOL)visible {
    if (_launcherVisible == visible) return;
    _launcherVisible = visible;
    if (visible && [ICMIdentityStore currentIdentity].userIdentity.identityExists) [self presentWindowIfNeededAsFullscreen:NO];
    [self.intercomWindow.rootViewController updateLauncherVisibility];
}
- (void)hideIntercomUI { [self hideIntercom]; }
- (void)userChanged { _lastUnreadConversations = nil; [self hideIntercomUI]; }
- (BOOL)isIntercomPresented { return [self.intercomWindow.rootViewController isIntercomPresented]; }
- (void)dismissAllIntercomViews { [self.intercomWindow.rootViewController dismissAllIntercomViews]; }
- (void)setNeedsHostAppStatusBarAppearanceUpdate {
    if (self.intercomWindow) {
        [self.intercomWindow.rootViewController updateHostAppState];
        [self.intercomWindow.rootViewController setNeedsStatusBarAppearanceUpdate];
    }
}
- (void)updateWindowThemeMode { if (self.intercomWindow) [self.intercomWindow updateThemeMode]; }
- (BOOL)isContentCurrentlyPresented { return self.intercomWindow.rootViewController.contentModalPresented; }
- (void)showLauncherOrInAppsIfNeeded {
    if ([ICMConfig sharedConfig].receivedFromServer && [ICMIdentityStore currentIdentity].appIdentity.identityExists && [ICMIdentityStore currentIdentity].userIdentity.identityExists) [self showLauncherOrInAppsOnMainThread];
}
- (void)showLauncherOrInAppsOnMainThread {
    dispatch_async(dispatch_get_main_queue(), ^{ [self setUnreadConversations:self.lastUnreadConversations ?: @[] completion:nil]; });
}
- (void)setUnreadConversations:(NSArray *)conversations completion:(void (^)(void))completion {
    if (!conversations) { if (completion) completion(); return; }
    _lastUnreadConversations = conversations;
    NSArray *display = conversations;
    if (![self shouldShowInApps]) display = @[];
    if ([self shouldShowLauncher] || display.count || self.intercomWindow) {
        [self presentWindowIfNeededAsFullscreen:NO];
        [self.intercomWindow.rootViewController updateUnreadConversations:display completion:completion];
    } else {
        if (![self isIntercomPresented] && self.intercomWindow) [self hideIntercomWindow];
        if (completion) completion();
    }
}
- (void)conversationMarkedAsRead:(NSNotification *)notification {
    id identifier = notification.object;
    self.lastUnreadConversations = [self.lastUnreadConversations filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"conversationId != %@", identifier]];
}
- (void)presentCarousel:(id)carousel {
    [self presentWindowIfNeededAsFullscreen:YES];
    [self.intercomWindow.rootViewController presentCarousel:carousel];
}
- (void)presentTicket:(id)ticket fromTriggerSource:(NSUInteger)source {
    [self presentWindowIfNeededAsFullscreen:YES];
    [self.intercomWindow.rootViewController presentTicket:ticket fromTriggerSource:source];
}
- (void)pushTicket:(id)ticket fromNavigationController:(UINavigationController *)navigation didCreateTicket:(BOOL)created triggerSource:(NSUInteger)source {
    [self.intercomWindow.rootViewController pushTicket:ticket fromNavigationController:navigation didCreateTicket:created triggerSource:source];
}
- (void)presentCreateTicketFormForTicketType:(id)type conversation:(id)conversation {
    [self presentWindowIfNeededAsFullscreen:YES];
    [self.intercomWindow.rootViewController presentTicketCreateFormForTicketType:type conversation:conversation];
}
- (void)presentSurvey:(id)survey {
    if ([ICMRecoveredSurveyService currentlyDisplayingSurvey]) return;
    [self presentWindowIfNeededAsFullscreen:YES];
    [ICMRecoveredSurveyService setCurrentlyDisplayingSurvey:YES];
    [self.intercomWindow.rootViewController presentSurvey:survey];
}
- (void)presentSheet:(id)sheet { [self.intercomWindow.rootViewController presentSheet:sheet]; }
- (void)showConversation:(id)conversation { [self showConversation:conversation fromNavigationController:nil]; }
- (void)showConversation:(id)conversation fromNavigationController:(UINavigationController *)navigation {
    [self presentWindowIfNeededAsFullscreen:YES];
    if ([self.intercomWindow.rootViewController firstViewControllerIsConversation]) {
        UINavigationController *bottom = [self.intercomWindow.rootViewController getBottomNavigationController];
        [self pushConversation:conversation applyFadeTransition:YES fromNavigationController:bottom withCompletion:^{
            void (^dismiss)(void) = ^{ [self.intercomWindow.rootViewController dismissAllButBottomViewController]; };
            if ([NSThread isMainThread]) dismiss(); else dispatch_async(dispatch_get_main_queue(), dismiss);
        }];
    } else {
        id identifier = [conversation conversationId];
        if (navigation && identifier) [self pushConversation:conversation applyFadeTransition:NO fromNavigationController:navigation withCompletion:nil];
        else [self.intercomWindow.rootViewController presentConversation:conversation];
    }
}
- (void)pushConversation:(id)conversation applyFadeTransition:(BOOL)fade fromNavigationController:(UINavigationController *)navigation withCompletion:(void (^)(void))completion {
    [self.intercomWindow.rootViewController pushConversation:conversation applyFadeTransition:fade fromNavigationController:navigation];
    if (completion) completion();
}
- (void)presentHome { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHome]; }
- (void)presentHomeWithConversation:(id)conversation { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHomeWithConversation:conversation]; }
- (void)presentHelpCenter { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHelpCenter]; }
- (void)presentMessages { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentMessages]; }
- (void)presentTickets { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentTickets]; }
- (void)presentHelpCenterFromSearchBrowse { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHelpCenterFromSearchBrowse]; }
- (void)presentHelpCenterCollection:(id)collection { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHelpCenterCollection:collection]; }
- (void)presentHelpCenterCollections:(NSArray *)collections { [self presentWindowIfNeededAsFullscreen:YES]; [self.intercomWindow.rootViewController presentHelpCenterCollections:collections]; }
- (void)presentArticle:(id)article allowReactions:(BOOL)reactions anchorLink:(NSString *)anchor {
    [self presentWindowIfNeededAsFullscreen:YES];
    [self.intercomWindow.rootViewController presentArticle:article allowReactions:reactions anchorLink:anchor];
}
- (void)hideIntercomWindow {
    if (self.intercomWindow) { [self.intercomWindow tearDown]; self.intercomWindow = nil; }
}
- (BOOL)shouldShowInApps { return self.inAppMessagesVisible; }
- (BOOL)shouldShowLauncher {
    return self.launcherVisible && [ICMIdentityStore currentIdentity].userIdentity.identityExists && ![self isIntercomPresented];
}
- (void)hideIntercom { [self.intercomWindow.rootViewController tearDown]; }
- (void)setWindowIsFullScreen:(BOOL)fullscreen { self.intercomWindow.fullscreen = fullscreen; }
- (void)presentWindowIfNeededAsFullscreen:(__unused BOOL)fullscreen {
    if (self.intercomWindow) return;
    UIWindowScene *selected = nil;
    for (UIScene *scene in [UIApplication sharedApplication].connectedScenes.allObjects) {
        BOOL applicationRole = [scene.session.role isEqualToString:UIWindowSceneSessionRoleApplication];
        if ([scene isKindOfClass:[UIWindowScene class]] && applicationRole && scene.activationState == UISceneActivationStateForegroundActive) { selected = (UIWindowScene *)scene; break; }
    }
    if (selected) { self.intercomWindow = [self createIntercomWindowInScene:selected]; [self setWindowIsFullScreen:NO]; }
}
- (void)sceneWillEnterForeground:(NSNotification *)notification {
    UIScene *scene = notification.object;
    BOOL applicationRole = [scene.session.role isEqualToString:UIWindowSceneSessionRoleApplication];
    if ([scene isKindOfClass:[UIWindowScene class]] && applicationRole && !self.intercomWindow && [ICMIdentityStore currentIdentity].userIdentity.identityExists) {
        self.intercomWindow = [self createIntercomWindowInScene:(UIWindowScene *)scene];
        [self setWindowIsFullScreen:NO];
    }
}
- (ICMWindow *)createIntercomWindowInScene:(UIWindowScene *)scene {
    ICMWindow *window = [[ICMWindow alloc] initWithWindowScene:scene];
    window.rootViewController = [[ICMRootViewController alloc] initWithWindowScene:scene];
    window.rootViewController.delegate = self;
    [window makeKeyAndVisible];
    return window;
}
@end
