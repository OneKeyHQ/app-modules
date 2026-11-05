// Original HTTP response orchestration; other endpoints remain separately tracked.
#import <Foundation/Foundation.h>
@class ICMBaseResponse, ICMUserIdentity, ICMUnreadConversationsResponse, ICMUnreadTicketsResponse, ICMCarousel, ICMRecoveredSurvey, IntercomNexusConfiguration;
@interface ICMHTTPBridge : NSObject
+ (void)handleBaseResponse:(ICMBaseResponse *)response;
+ (void)storeUserIdentity:(ICMUserIdentity *)identity;
+ (void)storeUnreadConversations:(ICMUnreadConversationsResponse *)response;
+ (void)storeUnreadTickets:(ICMUnreadTicketsResponse *)response;
+ (void)presentCarousel:(ICMCarousel *)carousel;
+ (void)presentSurvey:(ICMRecoveredSurvey *)survey;
+ (void)handleRealtimeConfig:(IntercomNexusConfiguration *)configuration;
@end
@class ICMUserUpdateRequest;
@interface ICMHTTPBridge (UserUpdate)
+ (void)updateUser:(ICMUserUpdateRequest *)update success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end
@interface ICMHTTPBridge (UnrestoredMetrics)
+ (void)submitMetrics;
@end
@interface ICMHTTPBridge (UnrestoredIdentityAndSurveyRequests)
+ (void)deleteDeviceToken:(NSString *)token forUserIdentity:(ICMUserIdentity *)identity completion:(void (^)(void))completion;
+ (void)failSurvey:(NSString *)identifier progressId:(NSInteger)progressId success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end

@class ICMSheet;
@interface ICMHTTPBridge (Sheets)
+ (void)fetchMessengerSheetData:(NSDictionary *)parameters success:(void (^)(ICMSheet *))success failure:(void (^)(NSError *))failure;
+ (void)submitMessengerSheet:(NSDictionary *)values uri:(NSString *)uri success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end
