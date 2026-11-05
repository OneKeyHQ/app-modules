// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// All original method paths restored; real HTTP/UI/model dependencies remain UNLINKED.
#import "ICMDataManager.h"
#import "ICMDataManagerDependencies.h"
#import "ICMDataManagerRequestDependencies.h"
#import "ICMDataManager+Conversations.h"
#import "ICMDataManager+Tickets.h"
#import "ICMDataManager+Open.h"
#import <UIKit/UIKit.h>
#import "ICMDataManagerOpenDependencies.h"
@implementation ICMDataManager
@synthesize moreConversationPagesAvailable = _moreConversationPagesAvailable, hasReceivedConversationsResponse = _hasReceivedConversationsResponse, helpCenterArticleViewed = _helpCenterArticleViewed, shouldGetUnreadConversationsOnActive = _shouldGetUnreadConversationsOnActive, conversations = _conversations, tickets = _tickets, conversationToMark = _conversationToMark, ticketToMark = _ticketToMark, unreadManager = _unreadManager, lastViewedConversation = _lastViewedConversation, recentOpenInboundConversation = _recentOpenInboundConversation, nextTicketsPageToFetch = _nextTicketsPageToFetch, resolutionBotBehaviourId = _resolutionBotBehaviourId, conversationCard = _conversationCard, conversationCTA = _conversationCTA, openRequestId = _openRequestId, poweredBy = _poweredBy, privacyPolicyNotice = _privacyPolicyNotice, botIntroId = _botIntroId, presenceTimer = _presenceTimer, initialConversation = _initialConversation;
+ (instancetype)sharedManager {
    static ICMDataManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ manager = [[self alloc] init]; });
    return manager;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(becomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(resignActive) name:UIApplicationWillResignActiveNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(resetPresenceTimer) name:@"kIntercomNexusRealtimeConfigChangedNotification" object:nil];
        [[IntercomNexusClient sharedClient] setTopics:[NSSet setWithObject:@"*"]];
        [[IntercomNexusClient sharedClient] addObserver:self];
        [self resetPresenceTimer];
        _conversations = [NSMutableArray new];
        _tickets = [NSMutableArray new];
        self.unreadManager = [ICMUnreadManager new];
    }
    return self;
}
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[IntercomNexusClient sharedClient] removeObserver:self];
    [_presenceTimer invalidate];
    _presenceTimer = nil;
}
- (void)setConversationToMark:(NSString *)identifier {
    if (identifier == _conversationToMark || [identifier isEqualToString:_conversationToMark]) return;
    _conversationToMark = identifier;
    for (ICMConversation *conversation in self.conversations) {
        if ([identifier isEqualToString:conversation.conversationId]) {
            if (!conversation.read) conversation.read = YES;
            break;
        }
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-inbox-updated" object:nil];
}
- (void)becomeActive {
    if ([[ICMPresentationManager sharedInstance] isIntercomPresented]) {
        [self updateInbox];
        [self updateTickets];
    }
    [self triggerPresence];
    [self resetPresenceTimer];
}
- (void)resignActive {
    self.shouldGetUnreadConversationsOnActive = NO;
    self.helpCenterArticleViewed = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.presenceTimer invalidate];
        self.presenceTimer = nil;
    });
}
- (void)resetPresenceTimer {
    float interval = [IntercomNexusClient sharedClient].configuration.connectionHeartbeatInterval.floatValue;
    if (interval > 0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.presenceTimer invalidate];
            self.presenceTimer = nil;
            self.presenceTimer = [NSTimer scheduledTimerWithTimeInterval:(double)interval target:self selector:@selector(triggerPresence) userInfo:nil repeats:YES];
        });
    }
}
- (void)triggerPresence {
    IntercomNexusEvent *event = [[IntercomNexusEvent alloc] initWithType:5 topics:nil];
    [[IntercomNexusClient sharedClient] sendEvent:event];
}
- (void)clearConversations {
    [self.conversations removeAllObjects];
    self.hasReceivedConversationsResponse = NO;
    self.moreConversationPagesAvailable = NO;
}
- (BOOL)hasOpenInboundConversation {
    return [self.conversations indexOfObjectPassingTest:^BOOL(ICMConversation *conversation, NSUInteger index, BOOL *stop) {
        (void)index; (void)stop;
        return [conversation isOpenInboundConversation];
    }] != NSNotFound;
}
- (void)eventOccurred:(IntercomNexusEvent *)event {
    switch (event.type) {
        case 6:
            if (!event.conversationId.length || ![self.conversationToMark isEqualToString:event.conversationId]) [self triggerPoll];
            if (event.ticketId && ![event.ticketId isEqualToString:self.ticketToMark]) [self updateTickets];
            break;
        case 16:
            [self findExistingConversationWithId:event.conversationId].read = YES;
            [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-inbox-updated" object:nil];
            if (event.conversationId) [self.unreadManager conversationIdRead:event.conversationId];
            break;
        case 22:
            if ([event.entityType isEqualToNumber:@44]) {
                [ICMCarouselService presentCarouselFromInstanceID:event.entityId];
            } else if ([event.entityType isEqualToNumber:@85]) {
                [[ICMRecoveredSurveyService new] presentSurveyWithInstanceId:event.entityId];
            }
            break;
        case 24:
            if (event.conversationId.length) {
                [ICMHTTPBridge conversationDetail:event.conversationId reason:@"conversation_title_generated" nexusMetadata:[[IntercomNexusClient sharedClient] getConnectionStatusMetadata] lastAdminPartCreatedAt:nil lastAdminPartId:nil success:^(ICMConversation *conversation) {
                    [self updateWithConversation:conversation];
                } error:nil];
            }
            break;
        default:
            break;
    }
}

#include "ICMDataManager+Conversations.inc"
#include "ICMDataManager+Tickets.inc"
#include "ICMDataManager+Open.inc"
@end
