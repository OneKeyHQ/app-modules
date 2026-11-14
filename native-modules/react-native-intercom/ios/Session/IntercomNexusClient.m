#import "IntercomNexusClient.h"
@implementation IntercomNexusClient
@synthesize configuration = _configuration, topics = _topics, observers = _observers, seenGuids = _seenGuids, connection = _connection, lastNewCommentEventReceivedAt = _lastNewCommentEventReceivedAt, lastEventReceived = _lastEventReceived;
+ (instancetype)sharedClient {
    static IntercomNexusClient *client;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ client = [IntercomNexusClient new]; });
    return client;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _observers = [NSHashTable hashTableWithOptions:0x205];
        _seenGuids = [NSCache new];
        [[NSNotificationCenter defaultCenter] addObserver:self.seenGuids selector:@selector(removeAllObjects) name:UIApplicationDidReceiveMemoryWarningNotification object:nil];
        _seenGuids.countLimit = 200;
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self.seenGuids]; }
- (void)setConfiguration:(IntercomNexusConfiguration *)configuration {
    @synchronized (self) {
        _configuration = configuration;
        if (self.connection) self.connection.configuration = configuration;
        else self.connection = [[IntercomNexusConnection alloc] initWithConfiguration:configuration delegate:self];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"kIntercomNexusRealtimeConfigChangedNotification" object:nil];
        if (!configuration) self.connection = nil;
    }
}
- (void)setTopics:(NSSet *)topics {
    if ([topics isEqual:_topics]) return;
    NSMutableSet *added = [topics mutableCopy];
    [added minusSet:_topics];
    NSMutableSet *removed = [_topics mutableCopy];
    [removed minusSet:topics];
    _topics = topics;
    [self subscribeToTopics:added];
    [self unsubscribeFromTopics:removed];
}
- (void)addObserver:(id<IntercomNexusObserverProtocol>)observer {
    @synchronized (self) { [self.observers addObject:observer]; }
}
- (void)removeObserver:(id<IntercomNexusObserverProtocol>)observer {
    @synchronized (self) { [self.observers removeObject:observer]; }
}
- (void)sendEvent:(IntercomNexusEvent *)event {
    if (!event.type) return;
    NSDictionary *dictionary = [event dictionaryRepresentation];
    if (!dictionary) return;
    [self.seenGuids setObject:@YES forKey:event.eventGuid];
    @synchronized (self) { [self.connection sendMessage:dictionary]; }
}
- (NSDictionary *)getConnectionStatusMetadata {
    NSMutableDictionary *result = [NSMutableDictionary new];
    NSUInteger state = self.connection.connectionState;
    if (state <= 2) result[@"connectionState"] = (@[@"initialising", @"connected", @"disconnected"])[state];
    if (self.lastEventReceived) {
        result[@"lastMessageReceivedAt"] = @(@(self.lastEventReceived.creationTime.timeIntervalSince1970 * 1000).integerValue);
        if ([self.lastEventReceived dictionaryRepresentation][@"eventName"]) {
            result[@"lastMessageType"] = [self.lastEventReceived dictionaryRepresentation][@"eventName"];
        }
    }
    if (self.lastNewCommentEventReceivedAt) result[@"lastNewCommentReceivedAt"] = @(self.lastNewCommentEventReceivedAt);
    return result;
}
- (void)subscribeToTopics:(NSSet *)topics {
    if (topics.count) [self sendEvent:[[IntercomNexusEvent alloc] initWithType:3 topics:topics]];
}
- (void)unsubscribeFromTopics:(NSSet *)topics {
    if (topics.count) [self sendEvent:[[IntercomNexusEvent alloc] initWithType:4 topics:topics]];
}
- (void)connection:(IntercomNexusConnection *)connection didReceiveMessage:(NSDictionary *)message {
    (void)connection;
    if (!message[@"eventName"]) return;
    IntercomNexusEvent *event = [IntercomNexusEvent instanceFromDictionary:message];
    self.lastEventReceived = event;
    if (event.type == 6) self.lastNewCommentEventReceivedAt = @(event.creationTime.timeIntervalSince1970 * 1000).integerValue;
    if ([self.seenGuids objectForKey:event.eventGuid]) return;
    [self.seenGuids setObject:@YES forKey:event.eventGuid];
    @synchronized (self) {
        for (id<IntercomNexusObserverProtocol> observer in self.observers.allObjects) {
            if ([observer respondsToSelector:@selector(observedConversationId)]) {
                if (!event.conversationId) continue;
                if (![[observer observedConversationId] isKindOfClass:[NSString class]]) continue;
                if (![[observer observedConversationId] isEqualToString:event.conversationId]) continue;
            }
            dispatch_async(dispatch_get_main_queue(), ^{ [observer eventOccurred:event]; });
        }
    }
}
- (void)connectionDidConnect:(IntercomNexusConnection *)connection {
    (void)connection;
    @synchronized (self) {
        for (id<IntercomNexusObserverProtocol> observer in self.observers.allObjects) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [observer eventOccurred:[[IntercomNexusEvent alloc] initWithType:1 topics:nil]];
            });
        }
    }
    [self subscribeToTopics:self.topics];
}
- (void)connectionDidDisconnect:(IntercomNexusConnection *)connection {
    (void)connection;
    @synchronized (self) {
        for (id<IntercomNexusObserverProtocol> observer in self.observers.allObjects) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [observer eventOccurred:[[IntercomNexusEvent alloc] initWithType:2 topics:nil]];
            });
        }
    }
}
@end
