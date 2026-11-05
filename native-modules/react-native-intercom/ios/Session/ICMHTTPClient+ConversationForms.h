// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMConversation.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (ConversationForms)
+ (void)rateConversation:(NSString * _Nullable)identifier withRatingIndex:(NSUInteger)index error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)addConversationRatingRemarkForConversation:(NSString * _Nullable)identifier remark:(NSString * _Nullable)remark error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)submitFormParams:(NSDictionary * _Nullable)form forConversation:(NSString * _Nullable)identifier success:(void (^ _Nullable)(ICMConversation *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
