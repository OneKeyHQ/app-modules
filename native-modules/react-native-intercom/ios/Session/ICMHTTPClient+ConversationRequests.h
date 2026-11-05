// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
#import "ICMConversation.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (ConversationRequests)
+ (void)recordInteractions:(NSString *)identifier interactions:(NSArray<NSString *> *)interactions;
+ (void)conversationDetail:(NSString * _Nullable)identifier reason:(NSString * _Nullable)reason nexusMetadata:(NSDictionary * _Nullable)metadata lastAdminPartCreatedAt:(NSNumber * _Nullable)timestamp lastAdminPartId:(NSString * _Nullable)lastId success:(void (^ _Nullable)(ICMConversation *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
@interface ICMHTTPBridge (ConversationRequests)
+ (void)recordInteractions:(NSString *)identifier interactions:(NSArray<NSString *> *)interactions;
+ (void)conversationDetail:(NSString * _Nullable)identifier reason:(NSString * _Nullable)reason nexusMetadata:(NSDictionary * _Nullable)metadata lastAdminPartCreatedAt:(NSNumber * _Nullable)timestamp lastAdminPartId:(NSString * _Nullable)lastId success:(void (^ _Nullable)(ICMConversation *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
