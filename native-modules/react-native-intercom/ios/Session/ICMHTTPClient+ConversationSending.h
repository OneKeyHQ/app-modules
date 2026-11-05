// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMComposerSuggestions.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (ConversationSending)
+ (void)createConversationWithBlocks:(NSArray * _Nullable)blocks clientAssignedUUID:(NSString * _Nullable)uuid articleMetaData:(ICMRecoveredArticleMetaData * _Nullable)article composerSuggestions:(ICMComposerSuggestions * _Nullable)suggestions success:(void (^ _Nullable)(ICMConversation *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)replyToConversation:(NSString * _Nullable)identifier clientAssignedUUID:(NSString * _Nullable)uuid withBlocks:(NSArray * _Nullable)blocks nexusMetadata:(NSDictionary * _Nullable)metadata lastAdminPartCreatedAt:(NSNumber * _Nullable)timestamp success:(void (^ _Nullable)(ICMConversationPart *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)quickReplyToConversation:(NSString * _Nullable)identifier clientAssignedUUID:(NSString * _Nullable)uuid withPartId:(NSString * _Nullable)partId withOptionUUID:(NSString * _Nullable)optionId success:(void (^ _Nullable)(ICMConversationPart *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)triggerInboundBotWithOptionUUID:(NSString * _Nullable)optionId clientAssignedUUID:(NSString * _Nullable)uuid success:(void (^ _Nullable)(ICMConversation *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
