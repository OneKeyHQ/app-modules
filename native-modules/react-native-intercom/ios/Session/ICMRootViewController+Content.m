#import "ICMRootViewController+Content.h"
#import "ICMRootViewController+Presentation.h"
#import "ICMRootViewControllerDependencies.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMDataManager.h"
#import "ICMDataManagerDependencies.h"
#import <QuartzCore/QuartzCore.h>
@implementation ICMRootViewController (Content)
- (void)presentArticle:(id)article allowReactions:(BOOL)reactions anchorLink:(NSString *)anchor {
    ICMRecoveredHelpCenterMetricService *metrics = [ICMRecoveredHelpCenterMetricService new];
    UIViewController *controller = [[ICMRecoveredArticleHostingController alloc] initWithArticleID:article metricService:metrics allowsReactions:reactions anchorLink:anchor];
    [self presentContentViewController:controller closeButtonAccessibilityText:nil];
}
- (void)presentContentViewController:(UIViewController *)controller closeButtonAccessibilityText:(NSString *)text {
    ICMContentModalViewController *modal = [[ICMContentModalViewController alloc] initWithContentViewController:controller];
    modal.delegate = self;
    modal.closeButtonAccessibilityText = text;
    [self presentIntercomModal:@[modal] useMessengerNavigationController:NO];
    if (![controller isKindOfClass:[ICMRecoveredTicketCreateFormViewController class]] && ![controller isKindOfClass:[ICMRecoveredTicketDetailsViewController class]]) self.contentModalPresented = YES;
}
- (void)presentCarousel:(id)carousel {
    UIViewController *controller = [[ICMCarouselViewController alloc] initWithCarousel:carousel];
    [self presentContentViewController:controller closeButtonAccessibilityText:nil];
}
- (void)presentTicket:(id)ticket fromTriggerSource:(NSUInteger)source {
    ICMRecoveredTicketDetailsViewController *controller = [ICMRecoveredTicketDetailsViewController new];
    ICMRecoveredTicketDetailsViewModel *model = [[ICMRecoveredTicketDetailsViewModel alloc] initWithViewController:controller ticket:ticket showSubmittedView:NO triggerSource:source];
    controller.viewModel = model;
    [self presentContentViewController:controller closeButtonAccessibilityText:nil];
}
- (void)pushTicket:(id)ticket fromNavigationController:(UINavigationController *)navigation didCreateTicket:(BOOL)created triggerSource:(NSUInteger)source {
    ICMRecoveredTicketDetailsViewController *controller = [ICMRecoveredTicketDetailsViewController new];
    ICMRecoveredTicketDetailsViewModel *model = [[ICMRecoveredTicketDetailsViewModel alloc] initWithViewController:controller ticket:ticket showSubmittedView:created triggerSource:source];
    controller.viewModel = model;
    ICMContentModalViewController *modal = [[ICMContentModalViewController alloc] initWithContentViewController:controller];
    modal.delegate = self;
    if (created) {
        CATransition *transition = [CATransition new];
        transition.duration = 0.33;
        transition.type = kCATransitionFade;
        [navigation.view.layer addAnimation:transition forKey:nil];
        navigation.viewControllers = @[];
        [navigation pushViewController:modal animated:NO];
    } else [navigation pushViewController:modal animated:YES];
}
- (void)pushConversation:(id)conversation applyFadeTransition:(BOOL)fade fromNavigationController:(UINavigationController *)navigation {
    NSString *marked = [ICMDataManager sharedManager].conversationToMark;
    UIViewController *top = navigation.topViewController;
    if (fade) {
        CATransition *transition = [CATransition new];
        transition.duration = 0.33;
        transition.type = kCATransitionFade;
        [navigation.view.layer addAnimation:transition forKey:nil];
    }
    if ([self firstViewControllerIsConversation]) {
        if ([marked isEqualToString:[conversation conversationId]] && [top isKindOfClass:[ICMRecoveredConversationViewController class]]) {
            [(ICMRecoveredConversationViewController *)top updateComposer];
            return;
        }
        navigation.viewControllers = @[];
    }
    UIViewController *controller = [[ICMRecoveredConversationViewController alloc] initWithConversation:conversation];
    [navigation pushViewController:controller animated:!fade];
}
- (void)presentTicketCreateFormForTicketType:(id)type conversation:(id)conversation {
    UIViewController *controller = [[ICMRecoveredTicketCreateFormViewController alloc] initWithTicketType:type conversation:conversation];
    [self presentContentViewController:controller closeButtonAccessibilityText:nil];
}
- (void)presentSurvey:(id)survey {
    UIViewController *controller = [[ICMRecoveredSurveyViewController alloc] initWith:survey];
    NSString *text = [[ICMTranslationManager messenger] translationForKey:@"surveys_dismiss"];
    [self presentContentViewController:controller closeButtonAccessibilityText:text];
}
- (BOOL)isPresentingHomeView:(NSArray *)controllers {
    return controllers.count == 1 && [controllers.firstObject isKindOfClass:[ICMRecoveredHomescreenViewController class]];
}
@end
