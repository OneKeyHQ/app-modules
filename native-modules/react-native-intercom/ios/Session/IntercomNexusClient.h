#import "IntercomNexusConnection.h"
#import "IntercomNexusEvent.h"
#import "IntercomNexusObserverProtocol.h"
@interface IntercomNexusClient : NSObject <IntercomNexusConnectionDelegate> {
    IntercomNexusConfiguration *_configuration;
    NSSet *_topics;
    NSHashTable *_observers;
    NSCache *_seenGuids;
    IntercomNexusConnection *_connection;
    NSInteger _lastNewCommentEventReceivedAt;
    IntercomNexusEvent *_lastEventReceived;
}
@property (nonatomic, strong) NSHashTable *observers;
@property (nonatomic, strong) NSCache *seenGuids;
@property (nonatomic, strong) IntercomNexusConnection *connection;
@property (nonatomic) NSInteger lastNewCommentEventReceivedAt;
@property (nonatomic, strong) IntercomNexusEvent *lastEventReceived;
@property (nonatomic, strong) IntercomNexusConfiguration *configuration;
@property (nonatomic, strong) NSSet *topics;
+ (instancetype)sharedClient;
- (void)addObserver:(id<IntercomNexusObserverProtocol>)observer;
- (void)removeObserver:(id<IntercomNexusObserverProtocol>)observer;
- (void)sendEvent:(IntercomNexusEvent *)event;
- (NSDictionary *)getConnectionStatusMetadata;
- (void)subscribeToTopics:(NSSet *)topics;
- (void)unsubscribeFromTopics:(NSSet *)topics;
@end
