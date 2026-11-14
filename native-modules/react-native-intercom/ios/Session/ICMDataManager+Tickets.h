#import "ICMDataManager.h"
@class ICMRecoveredTicket;
@interface ICMDataManager (Tickets)
- (void)fetchTicketsFromPage:(NSInteger)page success:(void (^)(NSArray *))success failure:(void (^)(NSError *))failure;
- (void)fetchTicketDetailsForTicketWithId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success failure:(void (^)(NSError *))failure;
- (void)markTicketAsRead:(NSString *)identifier;
- (void)clearTickets;
- (void)updateWithTicket:(ICMRecoveredTicket *)ticket;
- (NSNumber *)indexOfTicketWithId:(NSString *)identifier;
- (ICMRecoveredTicket *)ticketWithId:(NSString *)identifier;
- (void)sortTicketsByLatestStatusUpdatedAtDate;
- (void)updateTickets;
- (NSArray *)getRecentTickets;
@end
