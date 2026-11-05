#import "ICMBridge+Content.h"
#import "ICMBridge+Application.h"
#import "ICMPresentationManager.h"
#import "ICMConfig.h"
#import "ICMBaseResponseDependencies.h"
#import "ICMDataManagerRequestDependencies.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMRateLimiter.h"
#import "../Identity/ICMUtils.h"
@interface ICMCarousel (OriginalCarouselAPI)
@property(nonatomic, copy) NSString *carouselId;
@end
@interface ICMRecoveredSurvey (OriginalSurveyAPI)
- (instancetype)initWithId:(NSString *)identifier;
@end
@interface ICMRecoveredHelpCenterMetricService (OriginalAPIMetrics)
+ (void)addOpenedFromAPIEvent;
+ (void)addOpenedArticleFromAPIEvent;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate21HelpCenterDataService")))
__attribute__((swift_name("HelpCenterDataService")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredHelpCenterDataService : NSObject
- (void)fetchCollectionsWithCompletion:(void (^)(NSArray *, NSError *))completion;
- (void)fetchCollectionWithCollectionId:(NSString *)identifier completion:(void (^)(id, NSError *))completion;
- (void)searchResultsFor:(NSString *)query completion:(void (^)(NSArray *, NSError *))completion;
@end
@interface ICMHTTPBridge (OriginalEventLogging)
+ (void)logBackgroundDisabledError;
+ (void)logEventWithName:(NSString *)name metadata:(NSDictionary *)metadata sentFromBackground:(BOOL)background;
@end
@implementation ICMBridge (OriginalContent)
+ (void)presentHome { [ICMBridge dispatchOnMainQueueIfAppInactive:^{ [[ICMPresentationManager sharedInstance] presentHome]; }]; }
+ (void)presentHelpCenter {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        [[ICMPresentationManager sharedInstance] presentHelpCenter];
        [ICMRecoveredHelpCenterMetricService addOpenedFromAPIEvent];
    }];
}
+ (void)presentMessages { [ICMBridge dispatchOnMainQueueIfAppInactive:^{ [[ICMPresentationManager sharedInstance] presentMessages]; }]; }
+ (void)presentTickets { [ICMBridge dispatchOnMainQueueIfAppInactive:^{ [[ICMPresentationManager sharedInstance] presentTickets]; }]; }
+ (void)presentMessageComposer:(NSString *)message {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        if ([ICMConfig sharedConfig].receivedFromServer && ![ICMConfig sharedConfig].inboundMessages) {
            NSLog(@"[Intercom] INFO - You do not appear to have inbound messaging enabled on your Intercom account. Showing Home instead.");
            [[ICMPresentationManager sharedInstance] presentHome];
        } else {
            ICMConversation *conversation = [[ICMConversation alloc] initWithInitialMessage:message];
            [[ICMPresentationManager sharedInstance] presentHomeWithConversation:conversation];
        }
    }];
}
+ (void)presentHelpCenterCollections:(NSArray *)collections {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        [[ICMPresentationManager sharedInstance] presentHelpCenterCollections:collections];
        [ICMRecoveredHelpCenterMetricService addOpenedFromAPIEvent];
    }];
}
+ (void)presentArticle:(NSString *)identifier {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        [[ICMPresentationManager sharedInstance] presentArticle:identifier allowReactions:YES anchorLink:nil];
        [ICMRecoveredHelpCenterMetricService addOpenedArticleFromAPIEvent];
    }];
}
+ (void)presentConversation:(NSString *)identifier {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        ICMConversation *conversation = [ICMConversation new];
        conversation.conversationId = identifier;
        [[ICMPresentationManager sharedInstance] showConversation:conversation];
    }];
}
+ (void)presentCarousel:(NSString *)identifier {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        ICMCarousel *carousel = [ICMCarousel new];
        carousel.carouselId = identifier;
        [[ICMPresentationManager sharedInstance] presentCarousel:carousel];
    }];
}
+ (void)presentSurvey:(NSString *)identifier {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        ICMRecoveredSurvey *survey = [[ICMRecoveredSurvey alloc] initWithId:identifier];
        [[ICMPresentationManager sharedInstance] presentSurvey:survey];
    }];
}
+ (void)presentTicket:(NSString *)identifier {
    [ICMBridge dispatchOnMainQueueIfAppInactive:^{
        [ICMHTTPBridge getTicketDetailsForTicketId:identifier success:^(ICMRecoveredTicket *ticket) {
            [[ICMPresentationManager sharedInstance] presentTicket:ticket fromTriggerSource:3];
        } error:^(NSError *error) { NSLog(@"[Intercom] Failed to fetch ticket with ID %@: %@", identifier, error.localizedDescription); }];
    }];
}
+ (void)fetchHelpCenterCollectionsWithCompletion:(void (^)(NSArray *, NSError *))completion {
    [[ICMRecoveredHelpCenterDataService new] fetchCollectionsWithCompletion:completion];
}
+ (void)fetchHelpCenterCollection:(NSString *)identifier withCompletion:(void (^)(id, NSError *))completion {
    [[ICMRecoveredHelpCenterDataService new] fetchCollectionWithCollectionId:identifier completion:completion];
}
+ (void)searchHelpCenter:(NSString *)query withCompletion:(void (^)(NSArray *, NSError *))completion {
    [[ICMRecoveredHelpCenterDataService new] searchResultsFor:query completion:completion];
}
+ (void)logEventWithName:(NSString *)name metaData:(NSDictionary *)metadata {
    BOOL background = [self appIsBackgrounded];
    if (background && ![ICMConfig sharedConfig].backgroundRequestsEnabled) [ICMHTTPBridge logBackgroundDisabledError];
    else [[ICMRateLimiter sharedInstance] limit:^{ [ICMHTTPBridge logEventWithName:name metadata:metadata sentFromBackground:background]; }];
}
@end
