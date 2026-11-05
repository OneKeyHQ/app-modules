// Original internal response restored from Intercom 19.9.1.
#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMUnreadTicketsResponse : ICMSafeNetworkModel
@property (nonatomic, copy) NSOrderedSet *unreadTicketIds;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)updateReadState;
- (BOOL)isEqualToUnreadTicketsResponse:(ICMUnreadTicketsResponse *)other;
@end
