#import "ICMRootViewController+Destinations.h"
#import "ICMRootViewController+Presentation.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMConfig.h"
#import "ICMConfigUIModels.h"
#import "ICMIdentityStore.h"
#import "ICMDataManager.h"
@implementation ICMRootViewController (Destinations)
- (void)presentHome {
    if ([self errorViewIsDisplayed]) return;
    switch ([ICMConfig sharedConfig].modules.home.openConfig.type) {
        case 0: {
            UIViewController *home = [[ICMRecoveredHomescreenViewController alloc] initWithDelegate:self];
            [self presentIntercomWithViewControllers:@[home]];
            break;
        }
        case 1: [self presentMessages]; break;
        case 2: [self presentHelpCenter]; break;
        case 3: [self presentTickets]; break;
        case 4: [self presentConversationSpace]; break;
        default: break;
    }
}
- (void)presentHomeWithConversation:(id)conversation {
    if ([self errorViewIsDisplayed]) return;
    UIViewController *home = [[ICMRecoveredHomescreenViewController alloc] initWithDelegate:self];
    UIViewController *detail = [[ICMRecoveredConversationViewController alloc] initWithConversation:conversation];
    [self presentIntercomWithViewControllers:@[home, detail]];
}
- (void)presentConversationSpace {
    UIViewController *home = [[ICMRecoveredHomescreenViewController alloc] initWithDelegate:self];
    UIViewController *space = [ICMRecoveredConversationSpaceViewController new];
    [self presentIntercomWithViewControllers:@[home, space]];
}
- (void)presentMessages {
    if ([self errorViewIsDisplayed]) return;
    UIViewController *inbox = [ICMRecoveredInboxViewController new];
    [self presentIntercomWithViewControllers:@[inbox]];
}
- (void)presentTickets {
    if ([self errorViewIsDisplayed]) return;
    UIViewController *tickets = [ICMRecoveredTicketsListViewController new];
    [self presentIntercomWithViewControllers:@[tickets]];
}
- (void)presentConversation:(id)conversation {
    if ([self errorViewIsDisplayed]) return;
    UIViewController *detail = [[ICMRecoveredConversationViewController alloc] initWithConversation:conversation];
    [self presentIntercomWithViewControllers:@[detail]];
}
- (void)presentHelpCenter {
    UIViewController *help = [[ICMRecoveredHelpCenterContainerViewController alloc] initWithDelegate:self];
    [self presentHelpCenterViewController:help];
}
- (void)presentHelpCenterFromSearchBrowse {
    UIViewController *help = [[ICMRecoveredHelpCenterContainerViewController alloc] initFromSearchBrowse:YES delegate:self];
    [self presentHelpCenterViewController:help];
}
- (void)presentHelpCenterCollections:(NSArray *)collections {
    UIViewController *help = [[ICMRecoveredHelpCenterContainerViewController alloc] initWithCollections:collections delegate:self];
    [self presentHelpCenterViewController:help];
}
- (void)presentHelpCenterCollection:(id)collection {
    UIViewController *help = [[ICMRecoveredHelpCenterContainerViewController alloc] initWithCollection:collection delegate:self];
    [self presentHelpCenterViewController:help];
}
- (void)presentHelpCenterViewController:(UIViewController *)controller {
    [ICMDataManager sharedManager].helpCenterArticleViewed = NO;
    [self presentIntercomModal:@[controller] useMessengerNavigationController:NO];
}
- (BOOL)errorViewIsDisplayed {
    if ([ICMIdentityStore currentIdentity].userIdentity.identityExists && !self.tearingDown) return NO;
    NSString *title = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_title"];
    NSString *description = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
    NSString *close = [[ICMTranslationManager messenger] translationForKey:@"close"];
    [self logIdentifyError];
    ICMRecoveredErrorViewController *error = [[ICMRecoveredErrorViewController alloc] initWithTitle:title subtitle:description showActionButton:YES actionButtonTitle:close];
    error.errorDelegate = self;
    [self presentErrorViewController:error];
    return YES;
}
- (void)presentErrorViewController:(UIViewController *)controller {
    if (!self.presentedViewController && !self.tearingDown) [self presentIntercomModal:@[controller] useMessengerNavigationController:YES];
}
- (void)logIdentifyError {
    BOOL hasIdentity = [ICMIdentityStore currentIdentity] != nil;
    BOOL hasUser = [ICMIdentityStore currentIdentity].userIdentity != nil;
    BOOL exists = [ICMIdentityStore currentIdentity].userIdentity.identityExists;
    NSLog(@"[Intercom] ERROR - Cannot present Messenger because no user is registered - you need register a user first -> Showing fallback error screen. Debug info: { has_identity: %d, has_user: %d, has_user_identification: %d }", hasIdentity, hasUser, exists);
}
@end
