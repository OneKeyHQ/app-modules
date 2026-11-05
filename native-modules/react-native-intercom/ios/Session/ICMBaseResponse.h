#import "../Identity/ICMSafeNetworkModel.h"
#import "../Identity/ICMUserIdentity.h"
#import "../Identity/IntercomNexusConfiguration.h"
#import "ICMBaseResponseDependencies.h"
@interface ICMBaseResponse : ICMSafeNetworkModel {
@protected
    BOOL _isFirstRequest;
    ICMUnreadConversationsResponse *_unreadConversations;
    ICMUnreadTicketsResponse *_unreadTickets;
    ICMUserIdentity *_user;
    IntercomNexusConfiguration *_realtimeConfig;
    ICMCarousel *_carousel;
    ICMRecoveredSurvey *_survey;
}
@property (nonatomic, strong) ICMUnreadConversationsResponse *unreadConversations;
@property (nonatomic, strong) ICMUnreadTicketsResponse *unreadTickets;
@property (nonatomic, strong) ICMUserIdentity *user;
@property (nonatomic, strong) IntercomNexusConfiguration *realtimeConfig;
@property (nonatomic) BOOL isFirstRequest;
@property (nonatomic, strong) ICMCarousel *carousel;
@property (nonatomic, strong) ICMRecoveredSurvey *survey;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToResponse:(ICMBaseResponse *)response;
@end
