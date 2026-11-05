#import "ICMBridge.h"
@interface ICMBridge (OriginalContent)
+ (void)presentHome;
+ (void)presentHelpCenter;
+ (void)presentMessages;
+ (void)presentTickets;
+ (void)presentMessageComposer:(NSString *)message;
+ (void)presentHelpCenterCollections:(NSArray *)collections;
+ (void)presentArticle:(NSString *)identifier;
+ (void)presentConversation:(NSString *)identifier;
+ (void)presentCarousel:(NSString *)identifier;
+ (void)presentSurvey:(NSString *)identifier;
+ (void)presentTicket:(NSString *)identifier;
+ (void)fetchHelpCenterCollectionsWithCompletion:(void (^)(NSArray *, NSError *))completion;
+ (void)fetchHelpCenterCollection:(NSString *)identifier withCompletion:(void (^)(id, NSError *))completion;
+ (void)searchHelpCenter:(NSString *)query withCompletion:(void (^)(NSArray *, NSError *))completion;
+ (void)logEventWithName:(NSString *)name metaData:(NSDictionary *)metadata;
@end
