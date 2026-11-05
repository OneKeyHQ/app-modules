// Original internal response restored from Intercom 19.9.1.
#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMUnreadConversationsResponse : ICMSafeNetworkModel
@property (nonatomic, copy) NSArray *conversations;
@property (nonatomic, copy) NSOrderedSet *unreadConversationIds;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)updateReadState;
- (BOOL)isEqualToUnreadConversationsResponse:(ICMUnreadConversationsResponse *)other;
@end
