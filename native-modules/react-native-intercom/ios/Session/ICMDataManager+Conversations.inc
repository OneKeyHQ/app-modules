- (void)updateConversationsBefore:(NSDate *)before success:(void (^)(NSArray *, BOOL))success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getConversationsBefore:before pageSize:0 success:^(ICMConversationPageResponse *response) {
        self.moreConversationPagesAvailable = response.morePagesAvailable;
        BOOL added = NO;
        for (ICMConversation *conversation in response.conversations) {
            if (conversation.lastPart && ![self findExistingConversationWithId:conversation.conversationId]) {
                [self.conversations addObject:conversation];
                added = YES;
            }
        }
        if (success) success([self.conversations copy], added);
    } error:^(NSError *error) { if (failure) failure(error); }];
}
- (void)updateConversationsWithSuccess:(ICMInboxConversationsCompletion)success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getConversationsBefore:nil pageSize:0 success:^(ICMConversationPageResponse *response) {
        self.hasReceivedConversationsResponse = YES;
        if (response.conversations.count) {
            self.moreConversationPagesAvailable = response.morePagesAvailable;
            NSArray *merged = [self mergeExistingConversations:[self.conversations copy] withNewConversations:response.conversations];
            [self.conversations removeAllObjects];
            [self.conversations addObjectsFromArray:merged];
        }
        if (success) success([self.conversations copy], response.messagesEmptyState);
    } error:^(NSError *error) {
        if (failure && error.code != -999) failure(error);
    }];
}
- (NSArray *)mergeExistingConversations:(NSArray *)existing withNewConversations:(NSArray *)incoming {
    NSMutableArray *merged = [existing mutableCopy];
    [incoming enumerateObjectsUsingBlock:^(ICMConversation *conversation, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        NSUInteger existingIndex = [self getIndexOfConversation:conversation inConversations:merged];
        if (existingIndex == NSNotFound) {
            [merged addObject:conversation];
        } else {
            conversation.conversationParts = [self mergeExistingConversationParts:[merged[existingIndex] conversationParts] withNewConversationParts:conversation.conversationParts];
            conversation.composerState = [self mergeCachedComposerState:[merged[existingIndex] composerState] withNewComposerState:conversation.composerState];
            conversation.footerNotice = [merged[existingIndex] footerNotice];
            merged[existingIndex] = conversation;
        }
    }];
    return [self sortConversations:merged];
}
- (ICMComposerState *)mergeCachedComposerState:(ICMComposerState *)existing withNewComposerState:(ICMComposerState *)incoming {
    if (!incoming) return existing;
    if (!existing) return incoming;
    return incoming.version < existing.version ? existing : incoming;
}
- (NSArray *)mergeExistingConversationParts:(NSArray *)existing withNewConversationParts:(NSArray *)incoming {
    NSMutableArray *merged = [existing mutableCopy];
    [incoming enumerateObjectsUsingBlock:^(ICMConversationPart *part, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        NSUInteger existingIndex = [self getIndexOfConversationPart:part inConversationParts:merged];
        if (existingIndex == NSNotFound) [merged addObject:part];
        else merged[existingIndex] = part;
    }];
    return [self sortConversationParts:merged];
}
- (NSUInteger)getIndexOfConversation:(ICMConversation *)conversation inConversations:(NSArray *)conversations {
    return [conversations indexOfObjectPassingTest:^BOOL(ICMConversation *candidate, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        return [candidate.conversationId isEqualToString:conversation.conversationId];
    }];
}
- (NSUInteger)getIndexOfConversationPart:(ICMConversationPart *)part inConversationParts:(NSArray *)parts {
    return [parts indexOfObjectPassingTest:^BOOL(ICMConversationPart *candidate, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        return [candidate.partId isEqualToString:part.partId] || [candidate.clientAssignedUUID isEqualToString:part.clientAssignedUUID];
    }];
}
- (NSArray *)sortConversations:(NSArray *)conversations {
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"lastPart.createdAt" ascending:NO];
    return [conversations sortedArrayUsingDescriptors:@[descriptor]];
}
- (NSArray *)sortConversationParts:(NSArray *)parts {
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"createdAt" ascending:YES];
    return [parts sortedArrayUsingDescriptors:@[descriptor]];
}
- (ICMConversation *)findExistingConversationWithId:(NSString *)identifier {
    for (ICMConversation *conversation in self.conversations) {
        if ([conversation.conversationId isEqual:identifier]) return conversation;
    }
    return nil;
}
- (void)getUnreadConversationsIfNeeded {
    if (self.shouldGetUnreadConversationsOnActive) [self getUnreadConversations];
}
- (void)getUnreadConversations {
    [ICMHTTPBridge getUnreadConversationsWithSuccess:^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-inbox-updated" object:nil];
    }];
}
- (void)updateInbox {
    [self updateConversationsWithSuccess:^(NSArray *conversations, id emptyState) {
        (void)conversations; (void)emptyState;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-inbox-updated" object:nil];
    } failure:nil];
}
- (void)triggerPoll {
    if ([UIApplication sharedApplication].applicationState != UIApplicationStateActive) {
        self.shouldGetUnreadConversationsOnActive = YES;
    } else {
        if ([[ICMPresentationManager sharedInstance] isIntercomPresented]) [self updateInbox];
        [self getUnreadConversations];
    }
}
- (BOOL)isConversationCachedLocally:(NSString *)identifier {
    return [self.conversations indexOfObjectPassingTest:^BOOL(ICMConversation *conversation, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        return [conversation.conversationId isEqualToString:identifier];
    }] != NSNotFound;
}
- (NSMutableArray *)sortAllConversationsByCreatedAtDate {
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"lastPart.createdAt" ascending:NO];
    return [[self.conversations sortedArrayUsingDescriptors:@[descriptor]] mutableCopy];
}
- (void)updateWithConversation:(ICMConversation *)conversation {
    if (!conversation) return;
    if (!conversation.header.expanded) conversation.header.expanded = self.initialConversation.header.expanded;
    [self removePartsWithNoTimestampFromConversation:conversation];
    NSUInteger index = [self.conversations indexOfObjectPassingTest:^BOOL(ICMConversation *candidate, NSUInteger candidateIndex, BOOL *stop) {
        (void)candidateIndex; (void)stop;
        return [candidate.conversationId isEqualToString:conversation.conversationId];
    }];
    if (index != NSNotFound) {
        conversation.composerState = [self mergeCachedComposerState:[self.conversations[index] composerState] withNewComposerState:conversation.composerState];
        [self.conversations removeObjectAtIndex:index];
    }
    BOOL inserted = NO;
    for (NSUInteger i = 0; i < self.conversations.count; i++) {
        if ([conversation.lastPart.createdAt compare:[[self.conversations[i] lastPart] createdAt]] == NSOrderedDescending) {
            [self.conversations insertObject:conversation atIndex:i];
            inserted = YES;
            break;
        }
    }
    if (!inserted) [self.conversations addObject:conversation];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-inbox-updated" object:nil];
}
- (void)removePartsWithNoTimestampFromConversation:(ICMConversation *)conversation {
    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(ICMConversationPart *part, NSDictionary *bindings) {
        (void)bindings;
        return part.createdAt != nil;
    }];
    conversation.conversationParts = [conversation.conversationParts filteredArrayUsingPredicate:predicate];
}
- (void)clearUndeliveredConversations {
    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(ICMConversation *conversation, NSDictionary *bindings) {
        (void)bindings;
        return conversation.conversationId != nil;
    }];
    [self.conversations filterUsingPredicate:predicate];
}
- (ICMConversation *)matchedNewConversation {
    return self.initialConversation ? [self.initialConversation copy] : [ICMConversation new];
}
- (void)addParticipantsToParts:(NSArray *)parts from:(NSArray *)participants {
    NSMutableDictionary *byId = [NSMutableDictionary dictionaryWithCapacity:participants.count];
    for (ICMParticipant *participant in participants) {
        if (participant.id) byId[participant.id] = participant;
    }
    for (ICMConversationPart *part in parts) part.participant = byId[part.participantId];
}
- (void)updateConversation:(ICMConversation *)conversation fromComposerSuggestions:(ICMComposerSuggestions *)suggestions {
    if (suggestions && ![suggestions isEmpty]) {
        conversation.conversationParts = [conversation.conversationParts arrayByAddingObjectsFromArray:suggestions.conversationParts];
        conversation.composerSuggestions = suggestions;
    }
}
- (id)fallbackHeader { return self.initialConversation.headerToDisplay; }
- (NSArray *)getRecentConversations:(NSInteger)limit {
    NSMutableArray *sorted = [self sortAllConversationsByCreatedAtDate];
    NSMutableArray *recent = [[NSMutableArray alloc] initWithCapacity:(NSUInteger)limit];
    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(ICMConversation *conversation, NSDictionary *bindings) {
        (void)bindings;
        return [conversation isEligibleForRecentConversations];
    }];
    [recent addObjectsFromArray:[sorted filteredArrayUsingPredicate:predicate]];
    return recent.count < (NSUInteger)limit ? recent : [recent subarrayWithRange:NSMakeRange(0, (NSUInteger)limit)];
}
