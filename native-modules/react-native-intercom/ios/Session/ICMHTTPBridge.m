// SOURCE_RESTORED_UNLINKED: actual Presentation/Carousel/Survey/Store paths required.
#import "ICMHTTPBridge.h"
#import "ICMHTTPClient+Surveys.h"
#import "ICMHTTPClient.h"
#import "ICMBaseResponse.h"
#import "ICMIdentityStore.h"
#import "ICMDataManagerDependencies.h"
#import "ICMDataManagerRequestDependencies.h"
@interface ICMPresentationManager (UnreadResponseApplication)
- (void)setUnreadConversations:(NSArray *)conversations completion:(void (^)(void))completion;
@end
@interface ICMCarouselService (BaseResponseApplication)
+ (void)presentCarousel:(ICMCarousel *)carousel;
@end
@interface ICMRecoveredSurveyService (BaseResponseApplication)
+ (void)presentSurvey:(ICMRecoveredSurvey *)survey;
@end
@implementation ICMHTTPBridge
+ (void)handleBaseResponse:(ICMBaseResponse *)response {
    [self storeUserIdentity:response.user];
    [self presentCarousel:response.carousel];
    [self presentSurvey:response.survey];
    [self storeUnreadConversations:response.unreadConversations];
    [self storeUnreadTickets:response.unreadTickets];
    dispatch_async(dispatch_get_global_queue(2, 0), ^{ [self handleRealtimeConfig:response.realtimeConfig]; });
}
+ (void)storeUserIdentity:(ICMUserIdentity *)identity {
    if (identity) [[ICMIdentityStore currentIdentity] updateUserIdentityWith:identity];
}
+ (void)storeUnreadConversations:(ICMUnreadConversationsResponse *)response {
    if (response.conversations) {
        NSOrderedSet *identifiers = response.unreadConversationIds;
        [ICMDataManager sharedManager].unreadManager.unreadConversationIds = identifiers;
        [[ICMPresentationManager sharedInstance] setUnreadConversations:response.conversations completion:nil];
    }
}
+ (void)storeUnreadTickets:(ICMUnreadTicketsResponse *)response {
    if (response) {
        NSOrderedSet *identifiers = response.unreadTicketIds;
        [ICMDataManager sharedManager].unreadManager.unreadTicketIds = identifiers;
    }
}
+ (void)presentCarousel:(ICMCarousel *)carousel {
    if (carousel) [ICMCarouselService presentCarousel:carousel];
}
+ (void)presentSurvey:(ICMRecoveredSurvey *)survey {
    if (survey) [ICMRecoveredSurveyService presentSurvey:survey];
}
+ (void)handleRealtimeConfig:(IntercomNexusConfiguration *)configuration {
    if (!configuration || [IntercomNexusClient sharedClient].configuration.endpoints.count == 0) {
        [ICMIdentityStore currentIdentity].userIdentity.nexusConfiguration = configuration;
        [IntercomNexusClient sharedClient].configuration = configuration;
    }
}
@end

@implementation ICMHTTPBridge (Sheets)
+ (void)fetchMessengerSheetData:(NSDictionary *)parameters success:(void (^)(ICMSheet *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPClient fetchMessengerSheetData:parameters success:success failure:failure];
}
+ (void)submitMessengerSheet:(NSDictionary *)values uri:(NSString *)uri success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    [ICMHTTPClient submitMessengerSheet:values uri:uri success:success failure:failure];
}
+ (void)failSurvey:(NSString *)identifier progressId:(NSInteger)progressId success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    [ICMHTTPClient failSurvey:identifier progressId:progressId success:success error:failure];
}
@end
