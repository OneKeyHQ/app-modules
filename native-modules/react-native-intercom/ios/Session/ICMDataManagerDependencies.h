// Original dependencies are declarations only; absent bodies remain UNLINKED.
#import "../Identity/IntercomNexusConfiguration.h"
#import "ICMUnreadResponseDependencies.h"
#import "IntercomNexusEvent.h"
#import "IntercomNexusClient.h"
@interface ICMUnreadManager : NSObject
@property (nonatomic, strong) NSOrderedSet *unreadConversationIds;
@property (nonatomic, strong) NSOrderedSet *unreadTicketIds;
- (void)conversationIdRead:(NSString *)identifier;
- (void)ticketIdRead:(NSString *)identifier;
@end
#import "ICMPresentationManager.h"
