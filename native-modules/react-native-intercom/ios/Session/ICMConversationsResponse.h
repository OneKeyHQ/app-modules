#import "ICMConversationPageResponse.h"
#import "../Identity/ICMUserIdentity.h"
@interface ICMConversationsResponse : ICMSafeNetworkModel
@property(nonatomic, strong) ICMConversationPageResponse *conversationPage;
@property(nonatomic, strong) ICMUserIdentity *user;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToResponse:(ICMConversationsResponse *)other;
@end
