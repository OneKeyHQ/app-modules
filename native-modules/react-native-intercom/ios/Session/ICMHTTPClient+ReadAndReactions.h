// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
#import "ICMBaseResponse.h"
#import "ICMConversation.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (ReadAndReactions)
+ (void)markConversationAsRead:(NSString * _Nullable)identifier success:(void (^ _Nullable)(ICMBaseResponse *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)reactToConversation:(NSString * _Nullable)identifier withReactionIndex:(NSUInteger)index;
@end
@interface ICMHTTPBridge (ReadAndReactions)
+ (void)markConversationAsRead:(ICMConversation * _Null_unspecified)conversation;
+ (void)reactToConversation:(ICMConversation * _Null_unspecified)conversation reactionIndex:(NSUInteger)index fromInApp:(BOOL)fromInApp;
@end
NS_ASSUME_NONNULL_END
