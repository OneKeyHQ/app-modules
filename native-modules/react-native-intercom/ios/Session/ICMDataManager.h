// Original DataManager state; true product dependencies remain UNLINKED.
#import <Foundation/Foundation.h>
#import "IntercomNexusObserverProtocol.h"
#import "ICMDataManagerProtocol.h"
@class ICMUnreadManager, ICMConversation;
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19NewConversationCard")))
__attribute__((swift_name("NewConversationCard")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredNewConversationCard : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate26NewConversationCTAResponse")))
__attribute__((swift_name("NewConversationCTAResponse")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredNewConversationCTAResponse : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9PoweredBy")))
__attribute__((swift_name("PoweredBy")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredPoweredBy : NSObject
@property (nonatomic, readonly) NSInteger icon;
@property (nonatomic, readonly) NSString *text;
@property (nonatomic, readonly) NSURL *linkURL;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19PrivacyPolicyNotice")))
__attribute__((swift_name("PrivacyPolicyNotice")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredPrivacyPolicyNotice : NSObject
@property (nonatomic, readonly) NSString *paragraph;
@end
@interface ICMDataManager : NSObject <IntercomNexusObserverProtocol, ICMDataManagerProtocol> {
    BOOL _moreConversationPagesAvailable;
    BOOL _hasReceivedConversationsResponse;
    BOOL _helpCenterArticleViewed;
    BOOL _shouldGetUnreadConversationsOnActive;
    NSMutableArray *_conversations;
    NSMutableArray *_tickets;
    NSString *_conversationToMark;
    NSString *_ticketToMark;
    ICMUnreadManager *_unreadManager;
    ICMConversation *_lastViewedConversation;
    ICMConversation *_recentOpenInboundConversation;
    NSNumber *_nextTicketsPageToFetch;
    NSNumber *_resolutionBotBehaviourId;
    ICMRecoveredNewConversationCard *_conversationCard;
    ICMRecoveredNewConversationCTAResponse *_conversationCTA;
    NSString *_openRequestId;
    ICMRecoveredPoweredBy *_poweredBy;
    ICMRecoveredPrivacyPolicyNotice *_privacyPolicyNotice;
    NSString *_botIntroId;
    NSTimer *_presenceTimer;
    ICMConversation *_initialConversation;
}
+ (instancetype)sharedManager;
@property (nonatomic, strong) NSTimer *presenceTimer;
@property (nonatomic) BOOL shouldGetUnreadConversationsOnActive;
@property (nonatomic) BOOL moreConversationPagesAvailable;
@property (nonatomic, strong) NSNumber *nextTicketsPageToFetch;
@property (nonatomic, strong) ICMConversation *initialConversation;
@property (nonatomic, readonly) NSMutableArray *conversations;
@property (nonatomic, readonly) NSMutableArray *tickets;
@property (nonatomic, strong) NSString *conversationToMark;
@property (nonatomic, strong) NSString *ticketToMark;
@property (nonatomic, strong) ICMUnreadManager *unreadManager;
@property (nonatomic, strong) ICMConversation *lastViewedConversation;
@property (nonatomic, strong) ICMConversation *recentOpenInboundConversation;
@property (nonatomic) BOOL hasReceivedConversationsResponse;
@property (nonatomic) BOOL helpCenterArticleViewed;
@property (nonatomic, strong) NSNumber *resolutionBotBehaviourId;
@property (nonatomic, strong) ICMRecoveredNewConversationCard *conversationCard;
@property (nonatomic, strong) ICMRecoveredNewConversationCTAResponse *conversationCTA;
@property (nonatomic, strong) NSString *openRequestId;
@property (nonatomic, strong) ICMRecoveredPoweredBy *poweredBy;
@property (nonatomic, strong) ICMRecoveredPrivacyPolicyNotice *privacyPolicyNotice;
@property (nonatomic, copy) NSString *botIntroId;
- (void)becomeActive;
- (void)resignActive;
- (void)resetPresenceTimer;
- (void)triggerPresence;
- (void)clearConversations;
- (BOOL)hasOpenInboundConversation;
@end
@interface ICMDataManager (MainlineEntryPoints)
- (void)updateInbox;
- (void)updateTickets;
@end
