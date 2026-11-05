#import "IntercomNexusEvent.h"
@implementation IntercomNexusEvent
@synthesize showAvatar = _showAvatar, isBot = _isBot, hasAi = _hasAi, type = _type, eventGuid = _eventGuid, fromUser = _fromUser, toUser = _toUser, conversationId = _conversationId, ticketId = _ticketId, adminName = _adminName, avatarShapeString = _avatarShapeString, adminId = _adminId, adminAvatar = _adminAvatar, assigneeId = _assigneeId, conversationPartType = _conversationPartType, topics = _topics, creationTime = _creationTime, entityId = _entityId, entityType = _entityType, thinkingStatusTransitionDelay = _thinkingStatusTransitionDelay, thinkingStatusStrings = _thinkingStatusStrings, tokenSequenceIndex = _tokenSequenceIndex, blocks = _blocks, acknowledgementPart = _acknowledgementPart, createdAt = _createdAt, clientAssignedUUID = _clientAssignedUUID, clientId = _clientId, title = _title;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    id name = dictionary[@"eventName"];
    NSUInteger type = [name isKindOfClass:[NSString class]] ? [IntercomNexusEvent eventTypeForEventName:name] : 0;
    NSSet *topics = [NSSet set];
    id values = dictionary[@"nx.Topics"];
    if ([values isKindOfClass:[NSArray class]]) topics = [NSSet setWithArray:values];
    IntercomNexusEvent *event = [[IntercomNexusEvent alloc] initWithType:type topics:topics];
    [event setAttributesFromDictionary:dictionary];
    return event;
}
- (instancetype)initWithType:(NSUInteger)type topics:(NSSet *)topics {
    self = [super init];
    if (self) {
        _type = type;
        _topics = topics;
        _eventGuid = [[[NSUUID UUID] UUIDString] lowercaseString];
        _creationTime = [NSDate date];
    }
    return self;
}
- (BOOL)isAdminEvent { return self.toUser != nil; }
- (BOOL)isTypingEvent { return (self.type == 13 || self.type == 14 || self.type == 15) && (self.adminId != nil || self.fromUser != nil); }
- (BOOL)isConversationChangedEvent { return self.type == 10 || self.type == 8 || self.type == 9 || self.type == 11 || self.type == 12; }
- (BOOL)isReadStateChangedEvent { return self.type == 18; }
- (BOOL)isConversationPartUpdatedEvent { return self.type == 21; }
- (NSDictionary *)dictionaryRepresentation {
    NSString *name = [IntercomNexusEvent eventNameForEventType:self.type];
    if (!name.length) return nil;
    NSMutableDictionary *data = [NSMutableDictionary dictionary];
    if (self.conversationId) data[@"conversationId"] = self.conversationId;
    if (self.ticketId) data[@"ticketId"] = self.ticketId;
    if (self.adminName) data[@"adminName"] = self.adminName;
    if (self.adminId) data[@"adminId"] = self.adminId;
    if (self.assigneeId) data[@"assigeeId"] = self.assigneeId;
    if (self.adminAvatar) data[@"adminAvatar"] = self.adminAvatar;
    if (self.conversationPartType) data[@"subType"] = self.conversationPartType;
    if (self.entityId) data[@"entity_id"] = self.entityId;
    if (self.entityType) data[@"entity_type"] = self.entityType;
    data[@"isBot"] = @(self.isBot);
    data[@"hasAI"] = @(self.hasAi);
    data[@"showAvatar"] = @(self.showAvatar);
    if (self.thinkingStatusTransitionDelay) data[@"statusTransitionDelay"] = self.thinkingStatusTransitionDelay;
    if (self.thinkingStatusStrings) data[@"statusStrings"] = self.thinkingStatusStrings;
    if (self.avatarShapeString) data[@"avatarShape"] = self.avatarShapeString;
    if (self.tokenSequenceIndex) data[@"tokenSequenceIndex"] = self.tokenSequenceIndex;
    if (self.blocks) data[@"blocks"] = self.blocks;
    if (self.title) data[@"title"] = self.title;
    NSMutableDictionary *result = [@{@"eventName": name, @"eventData": data, @"eventGuid": self.eventGuid} mutableCopy];
    if (self.topics) result[@"nx.Topics"] = [self.topics allObjects];
    if (self.toUser) result[@"nx.ToUser"] = self.toUser;
    if (self.fromUser) result[@"nx.FromUser"] = self.fromUser;
    if (self.acknowledgementPart) result[@"acknowledgementPart"] = self.acknowledgementPart;
    if (self.createdAt) result[@"createdAt"] = self.createdAt;
    if (self.clientAssignedUUID) result[@"clientAssignedUuid"] = self.clientAssignedUUID;
    if (self.clientId) result[@"clientId"] = self.clientId;
    return result;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if (![key isEqualToString:@"eventData"]) { [super setValue:value forKey:key]; return; }
    if ([value isKindOfClass:[NSString class]]) { self.conversationId = value; return; }
    if (![value isKindOfClass:[NSDictionary class]]) return;
    self.conversationId = [value[@"conversationId"] isKindOfClass:[NSString class]] ? value[@"conversationId"] : nil;
    self.ticketId = [value[@"ticketId"] isKindOfClass:[NSString class]] ? value[@"ticketId"] : nil;
    self.adminId = [value[@"adminId"] isKindOfClass:[NSString class]] ? value[@"adminId"] : nil;
    self.adminName = [value[@"adminName"] isKindOfClass:[NSString class]] ? value[@"adminName"] : nil;
    self.adminAvatar = [value[@"adminAvatar"] isKindOfClass:[NSString class]] ? value[@"adminAvatar"] : nil;
    self.conversationPartType = [value[@"subType"] isKindOfClass:[NSString class]] ? value[@"subType"] : nil;
    self.entityId = [value[@"entity_id"] isKindOfClass:[NSString class]] ? value[@"entity_id"] : nil;
    self.entityType = [value[@"entity_type"] isKindOfClass:[NSNumber class]] ? value[@"entity_type"] : nil;
    self.isBot = [value[@"isBot"] isKindOfClass:[NSNumber class]] ? [value[@"isBot"] boolValue] : NO;
    self.hasAi = [value[@"hasAI"] isKindOfClass:[NSNumber class]] ? [value[@"hasAI"] boolValue] : NO;
    self.thinkingStatusTransitionDelay = [value[@"statusTransitionDelay"] isKindOfClass:[NSNumber class]] ? value[@"statusTransitionDelay"] : nil;
    self.thinkingStatusStrings = [value[@"statusStrings"] isKindOfClass:[NSArray class]] ? value[@"statusStrings"] : nil;
    self.avatarShapeString = [value[@"avatarShape"] isKindOfClass:[NSString class]] ? value[@"avatarShape"] : nil;
    self.showAvatar = [value[@"showAvatar"] isKindOfClass:[NSNumber class]] ? [value[@"showAvatar"] boolValue] : YES;
    self.tokenSequenceIndex = [value[@"tokenSequenceIndex"] isKindOfClass:[NSNumber class]] ? value[@"tokenSequenceIndex"] : nil;
    self.blocks = [value[@"blocks"] isKindOfClass:[NSArray class]] ? value[@"blocks"] : nil;
    self.acknowledgementPart = [value[@"acknowledgementPart"] isKindOfClass:[NSDictionary class]] ? value[@"acknowledgementPart"] : nil;
    self.createdAt = [value[@"createdAt"] isKindOfClass:[NSNumber class]] ? value[@"createdAt"] : nil;
    self.clientAssignedUUID = [value[@"clientAssignedUuid"] isKindOfClass:[NSString class]] ? value[@"clientAssignedUuid"] : nil;
    self.clientId = [value[@"clientId"] isKindOfClass:[NSString class]] ? value[@"clientId"] : nil;
    self.title = [value[@"title"] isKindOfClass:[NSString class]] ? value[@"title"] : nil;
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"nx.FromUser"]) {
        if ([value isKindOfClass:[NSString class]]) self.fromUser = value;
    } else if ([key isEqualToString:@"nx.ToUser"]) {
        if ([value isKindOfClass:[NSString class]]) self.toUser = value;
    }
}
+ (NSDictionary *)eventStringToTypeDictionary {
    static NSDictionary *mapping;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ mapping = @{
        @"AdminIsTyping": @13,
        @"AdminIsTypingANote": @14,
        @"ConversationPartToken": @7,
        @"ConversationPartUpdated": @21,
        @"ConversationReadByAdmin": @18,
        @"ConversationSeen": @16,
        @"ConversationTitleGenerated": @24,
        @"NewComment": @6,
        @"NewContent": @22,
        @"PositiveRatingAdded": @19,
        @"ThreadAssigned": @8,
        @"ThreadClosed": @9,
        @"ThreadCreated": @12,
        @"ThreadReopened": @11,
        @"ThreadUpdated": @10,
        @"UserContentSeenByAdmin": @17,
        @"UserIsTyping": @15,
        @"VideoMessageUploading": @20,
        @"nx.Ping": @23,
        @"nx.Subscribe": @3,
        @"nx.Unsubscribe": @4,
        @"nx.UserPresence": @5
    }; });
    return mapping;
}
+ (NSDictionary *)eventTypeToStringDictionary {
    static NSDictionary *mapping;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        NSArray *names = [[self class] eventStringToTypeDictionary].allKeys;
        NSArray *types = [[[self class] eventStringToTypeDictionary] objectsForKeys:names notFoundMarker:@0];
        mapping = [NSDictionary dictionaryWithObjects:names forKeys:types];
    });
    return mapping;
}
+ (NSUInteger)eventTypeForEventName:(NSString *)name {
    id value = [IntercomNexusEvent eventStringToTypeDictionary][name];
    return value ? [value integerValue] : 0;
}
+ (NSString *)eventNameForEventType:(NSUInteger)type {
    NSString *value = [IntercomNexusEvent eventTypeToStringDictionary][@(type)];
    return value ?: @"";
}
@end
