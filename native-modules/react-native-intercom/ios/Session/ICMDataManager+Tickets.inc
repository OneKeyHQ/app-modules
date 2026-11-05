- (void)fetchTicketsFromPage:(NSInteger)page success:(void (^)(NSArray *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getTicketsAtPage:page pageSize:0 success:^(ICMRecoveredTicketsPageResponse *response) {
        self.nextTicketsPageToFetch = response.nextPage;
        if (response.unreadTickets) self.unreadManager.unreadTicketIds = response.unreadTickets.unreadTicketIds;
        for (ICMRecoveredTicket *ticket in response.tickets) {
            NSNumber *index = [self indexOfTicketWithId:ticket.id];
            if (index) [self.tickets removeObjectAtIndex:index.integerValue];
            [self.tickets addObject:ticket];
        }
        [self sortTicketsByLatestStatusUpdatedAtDate];
        if (success) success(self.tickets);
        [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-tickets-updated" object:nil];
    } error:^(NSError *error) { if (failure) failure(error); }];
}
- (void)fetchTicketDetailsForTicketWithId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getTicketDetailsForTicketId:identifier success:^(ICMRecoveredTicket *ticket) {
        NSNumber *index = [self indexOfTicketWithId:ticket.id];
        if (index) self.tickets[index.integerValue] = ticket;
        if (success) success(ticket);
    } error:^(NSError *error) { if (failure) failure(error); }];
}
- (void)markTicketAsRead:(NSString *)identifier {
    ICMRecoveredTicket *ticket = [self ticketWithId:identifier];
    if (ticket && !ticket.read) {
        [self.unreadManager ticketIdRead:identifier];
        [ICMHTTPBridge markTicketAsReadWithID:identifier];
        [self ticketWithId:identifier].read = YES;
    }
}
- (void)clearTickets {
    [self.tickets removeAllObjects];
    self.nextTicketsPageToFetch = nil;
}
- (void)updateWithTicket:(ICMRecoveredTicket *)ticket {
    if (!ticket) return;
    NSUInteger index = [self.tickets indexOfObjectPassingTest:^BOOL(ICMRecoveredTicket *candidate, NSUInteger candidateIndex, BOOL *stop) {
        (void)candidateIndex; (void)stop;
        return [candidate.id isEqualToString:ticket.id];
    }];
    if (index != NSNotFound) [self.tickets removeObjectAtIndex:index];
    BOOL inserted = NO;
    for (NSUInteger i = 0; i < self.tickets.count; i++) {
        if ([ticket.latestStatusUpdatedAt compare:[self.tickets[i] latestStatusUpdatedAt]] == NSOrderedDescending) {
            [self.tickets insertObject:ticket atIndex:i];
            inserted = YES;
            break;
        }
    }
    if (!inserted) [self.tickets addObject:ticket];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-tickets-updated" object:nil];
}
- (NSNumber *)indexOfTicketWithId:(NSString *)identifier {
    __block NSNumber *result = nil;
    [self.tickets enumerateObjectsUsingBlock:^(ICMRecoveredTicket *ticket, NSUInteger index, BOOL *stop) {
        (void)stop;
        if ([ticket.id isEqualToString:identifier]) result = @((NSInteger)index);
    }];
    return result;
}
- (ICMRecoveredTicket *)ticketWithId:(NSString *)identifier {
    __block ICMRecoveredTicket *result = nil;
    [self.tickets enumerateObjectsUsingBlock:^(ICMRecoveredTicket *ticket, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        if ([ticket.id isEqualToString:identifier]) result = ticket;
    }];
    return result;
}
- (void)sortTicketsByLatestStatusUpdatedAtDate {
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"latestStatusUpdatedAt" ascending:NO];
    _tickets = [[self.tickets sortedArrayUsingDescriptors:@[descriptor]] mutableCopy];
}
- (void)updateTickets { [self fetchTicketsFromPage:1 success:nil failure:nil]; }
- (NSArray *)getRecentTickets {
    NSDate *cutoff = [[NSCalendar currentCalendar] dateByAddingUnit:NSCalendarUnitDay value:-7 toDate:[NSDate date] options:0];
    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(ICMRecoveredTicket *ticket, NSDictionary *bindings) {
        (void)bindings;
        return [ticket.latestStatusUpdatedAt compare:cutoff] == NSOrderedDescending;
    }];
    [self sortTicketsByLatestStatusUpdatedAtDate];
    NSArray *recent = [self.tickets filteredArrayUsingPredicate:predicate];
    return recent.count >= 4 ? [recent subarrayWithRange:NSMakeRange(0, 3)] : recent;
}
