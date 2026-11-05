#import "ICMHTTPBridge.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ITBUpload.h"
@interface ICMHTTPBridge (OriginalReply)
+ (void)replyToConversation:(ICMConversation *)conversation clientAssignedUUID:(NSString *)uuid withBlocks:(NSArray *)blocks upload:(ITBUpload *)upload lastAdminPartCreatedAt:(NSNumber *)timestamp success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure;
@end
